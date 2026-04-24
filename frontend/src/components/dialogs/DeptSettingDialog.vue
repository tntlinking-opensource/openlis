<template>
  <FloatingPanel
    v-model="visible"
    title="科室信息设置"
    :width="900"
    :height="500"
    class="dept-setting-panel"
  >
    <div class="dialog-body">
      <div class="left">
        <div class="grid-title">科室信息列表：</div>
        <el-table
          :data="list"
          height="320"
          border
          size="small"
          highlight-current-row
          @current-change="onRowSelect"
        >
          <el-table-column prop="ksdm" label="科室代码" width="90" />
          <el-table-column prop="ksmc" label="科室名称" width="180" />
          <el-table-column prop="pym" label="拼音码" width="120" />
          <el-table-column prop="szdm" label="数字码" width="100" />
          <el-table-column prop="hisKsdm" label="HIS代码" width="110" />
          <el-table-column prop="jyksbz" label="检验科室标志" width="110">
            <template #default="{ row }">
              <el-checkbox v-model="row.jyksbz" disabled />
            </template>
          </el-table-column>
          <el-table-column prop="sybz" label="使用标志" width="90">
            <template #default="{ row }">
              <el-checkbox v-model="row.sybz" disabled />
            </template>
          </el-table-column>
          <el-table-column prop="kslb" label="科室类别" width="90" />
          <el-table-column prop="zylb" label="专业类别" width="90" />
        </el-table>
      </div>

      <div class="right">
        <div class="form">
          <div class="row">
            <label>科室代码</label>
            <el-input v-model="form.ksdm" size="small" class="inp" :disabled="editingMode==='edit'" />
          </div>
          <div class="row">
            <label>科室名称</label>
            <el-input v-model="form.ksmc" size="small" class="inp" />
          </div>
          <div class="row">
            <label>拼 音 码</label>
            <el-input v-model="form.pym" size="small" class="inp" />
          </div>
          <div class="row">
            <label>数 字 码</label>
            <el-input v-model="form.szdm" size="small" class="inp" />
          </div>
          <div class="row">
            <label>HIS代码</label>
            <el-input v-model="form.hisKsdm" size="small" class="inp" />
          </div>
          <div class="row">
            <label>科室类别</label>
            <el-input v-model="form.kslb" size="small" class="inp" />
          </div>
          <div class="row">
            <label>专业类别</label>
            <el-input v-model="form.zylb" size="small" class="inp" />
          </div>
          <div class="row check-row">
            <el-checkbox v-model="form.sybz">使用标志</el-checkbox>
            <el-checkbox v-model="form.jyksbz">检验科室标志</el-checkbox>
          </div>
        </div>

        <div class="btns">
          <el-button size="small" @click="onAdd" :disabled="editingMode==='add'">增加(A)</el-button>
          <el-button size="small" @click="onEdit" :disabled="!current || editingMode==='edit'">修改(E)</el-button>
          <el-button size="small" @click="onCancel" :disabled="!editingMode">放弃(C)</el-button>
          <el-button size="small" type="primary" @click="onSave" :disabled="!editingMode">保存(S)</el-button>
          <el-button size="small" @click="handleClose">关闭(X)</el-button>
        </div>
      </div>
    </div>
  </FloatingPanel>
</template>

<script setup>
import { computed, reactive, ref, watch } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import FloatingPanel from '@/components/FloatingPanel.vue'

const props = defineProps({
  modelValue: { type: Boolean, default: false }
})
const emit = defineEmits(['update:modelValue'])

const visible = computed({
  get: () => props.modelValue,
  set: (v) => emit('update:modelValue', v)
})

const list = ref([])
const current = ref(null)
const editingMode = ref('') // '', 'add', 'edit'

const form = reactive({
  ksdm: '',
  ksmc: '',
  pym: '',
  szdm: '',
  hisKsdm: '',
  bmdm: '',
  sybz: true,
  jyksbz: false,
  zylb: '',
  kslb: ''
})

const loadList = async () => {
  const res = await axios.get('/api/basic/dept/list')
  list.value = res.data || []
}

const fillFormFromRow = (row) => {
  if (!row) return
  form.ksdm = row.ksdm || ''
  form.ksmc = row.ksmc || ''
  form.pym = row.pym || ''
  form.szdm = row.szdm || ''
  form.hisKsdm = row.hisKsdm || row.his_ksdm || ''
  form.bmdm = row.bmdm || ''
  form.sybz = row.sybz ?? true
  form.jyksbz = row.jyksbz ?? false
  form.zylb = row.zylb || ''
  form.kslb = row.kslb || ''
}

const resetForm = () => {
  fillFormFromRow({
    ksdm: '',
    ksmc: '',
    pym: '',
    szdm: '',
    hisKsdm: '',
    bmdm: '',
    sybz: true,
    jyksbz: false,
    zylb: '',
    kslb: ''
  })
}

const onRowSelect = (row) => {
  if (editingMode.value) return
  current.value = row
  if (row) fillFormFromRow(row)
}

const onAdd = () => {
  editingMode.value = 'add'
  current.value = null
  resetForm()
}

const onEdit = () => {
  if (!current.value) return
  editingMode.value = 'edit'
}

const onCancel = () => {
  editingMode.value = ''
  if (current.value) fillFormFromRow(current.value)
}

const onSave = async () => {
  try {
    if (!form.ksdm.trim()) {
      ElMessage.warning('科室代码不能为空')
      return
    }
    if (!form.ksmc.trim()) {
      ElMessage.warning('科室名称不能为空')
      return
    }
    const payload = { ...form }
    const res = await axios.post('/api/basic/dept/save', payload)
    if (res.data?.success) {
      ElMessage.success(res.data.message || '保存成功')
      editingMode.value = ''
      await loadList()
    } else {
      ElMessage.warning(res.data?.message || '保存失败')
    }
  } catch (e) {
    ElMessage.error('保存失败：' + (e.response?.data?.message || e.message))
  }
}

watch(visible, async (v) => {
  if (v) {
    editingMode.value = ''
    current.value = null
    resetForm()
    try {
      await loadList()
    } catch (e) {
      ElMessage.error('读取失败：' + (e.response?.data?.message || e.message))
    }
  }
})

const handleClose = () => {
  visible.value = false
}
</script>

<style scoped>
.dept-setting-panel :deep(.panel-content) {
  padding: 8px 10px 10px;
  background: #9cc6ea;
}

.dialog-body {
  display: flex;
  gap: 8px;
  background: #9cc6ea;
}

.left {
  flex: 3;
}

.grid-title {
  font-size: 12px;
  margin-bottom: 4px;
}

.right {
  flex: 2;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.form {
  border: 1px solid rgba(0, 0, 0, 0.25);
  background: rgba(255, 255, 255, 0.12);
  padding: 8px 10px;
}

.row {
  display: flex;
  align-items: center;
  margin-bottom: 6px;
  font-size: 12px;
}

.row label {
  width: 70px;
}

.inp {
  flex: 1;
}

.check-row {
  gap: 16px;
}

.btns {
  display: flex;
  gap: 8px;
  padding-top: 4px;
}
</style>


