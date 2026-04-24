<template>
  <FloatingPanel
    v-model="visible"
    title="工作组别设置"
    :width="900"
    :height="500"
    class="workgroup-setting-panel"
  >
    <div class="dialog-body">
      <div class="left">
        <div class="grid-title">工作组信息列表：</div>
        <el-table
          :data="list"
          height="320"
          border
          size="small"
          highlight-current-row
          @current-change="onRowSelect"
        >
          <el-table-column prop="gzzdm" label="工作组代码" width="110" />
          <el-table-column prop="gzzmc" label="工作组名称" width="160" />
          <el-table-column prop="pym" label="拼音码" width="90" />
          <el-table-column prop="gzzlxmc" label="工作组类型" width="130" />
          <el-table-column prop="his_ksdm" label="HIS代码" width="90" />
          <el-table-column prop="xh" label="序号" width="90" />
          <el-table-column prop="sybz" label="是否使用" width="90">
            <template #default="{ row }">
              <el-checkbox v-model="row.sybz" disabled />
            </template>
          </el-table-column>
        </el-table>
      </div>

      <div class="right">
        <div class="form">
          <div class="row">
            <label>工作组代码</label>
            <el-input v-model="form.gzzdm" size="small" class="inp" :disabled="editingMode==='edit'" />
          </div>
          <div class="row">
            <label>工作组名称</label>
            <el-input v-model="form.gzzmc" size="small" class="inp" @blur="onGzzmcBlur" />
          </div>
          <div class="row">
            <label>拼 音 码</label>
            <el-input v-model="form.pym" size="small" class="inp" />
          </div>
          <div class="row">
            <label>工作组类型</label>
            <el-select v-model="form.gzzlx" size="small" class="inp">
              <el-option label="检验类" :value="1" />
              <el-option label="库房类" :value="2" />
              <el-option label="后勤类" :value="3" />
            </el-select>
          </div>
          <div class="row">
            <label>HIS代码</label>
            <el-input v-model="form.hisKsdm" size="small" class="inp" />
          </div>
          <div class="row">
            <label>序 号</label>
            <el-input-number v-model="form.xh" size="small" class="inp" :min="1" />
          </div>
          <div class="row check-row">
            <el-checkbox v-model="form.sybz">使用标志</el-checkbox>
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
  gzzdm: '',
  gzzmc: '',
  pym: '',
  gzzlx: null,
  hisKsdm: '',
  xh: 1,
  sybz: true,
  xgbz: false
})

// 自动生成拼音码（简化版，实际应调用后端或前端拼音转换）
const onGzzmcBlur = () => {
  if (form.gzzmc && !form.pym) {
    // 简单处理：取首字母（实际应调用拼音转换函数）
    form.pym = form.gzzmc.toUpperCase().substring(0, 1)
  }
}

const loadList = async () => {
  try {
    const res = await axios.get('/api/basic/workgroup/list')
    list.value = res.data || []
  } catch (e) {
    ElMessage.error('读取失败：' + (e.response?.data?.message || e.message))
  }
}

const fillFormFromRow = (row) => {
  if (!row) return
  form.gzzdm = row.gzzdm || ''
  form.gzzmc = row.gzzmc || ''
  form.pym = row.pym || ''
  form.gzzlx = row.gzzlx || null
  form.hisKsdm = row.his_ksdm || ''
  form.xh = row.xh || 1
  form.sybz = row.sybz ?? true
  form.ksdm = row.ssksdm || ''
}

const resetForm = () => {
  fillFormFromRow({
    gzzdm: '',
    gzzmc: '',
    pym: '',
    gzzlx: null,
    hisKsdm: '',
    xh: 1,
    sybz: true,
    ksdm: ''
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
  form.xgbz = false
}

const onEdit = () => {
  if (!current.value) return
  editingMode.value = 'edit'
  form.xgbz = true
}

const onCancel = () => {
  editingMode.value = ''
  if (current.value) fillFormFromRow(current.value)
  else resetForm()
}

const onSave = async () => {
  try {
    if (!form.gzzdm.trim()) {
      ElMessage.warning('工作组代码不能为空')
      return
    }
    if (!form.gzzmc.trim()) {
      ElMessage.warning('工作组名称不能为空')
      return
    }
    if (form.gzzlx == null) {
      ElMessage.warning('工作组类型不能为空')
      return
    }
    if (!form.xh || form.xh < 1) {
      ElMessage.warning('序号不能为空且必须大于0')
      return
    }
    const payload = { ...form }
    const res = await axios.post('/api/basic/workgroup/save', payload)
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
    await loadList()
  }
})

const handleClose = () => {
  visible.value = false
}
</script>

<style scoped>
.workgroup-setting-panel :deep(.panel-content) {
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
  background: #fff;
  padding: 8px;
  border: 1px solid #ccc;
}

.row {
  display: flex;
  align-items: center;
  margin-bottom: 6px;
}

.row label {
  width: 80px;
  font-size: 12px;
  text-align: right;
  margin-right: 6px;
}

.row .inp {
  flex: 1;
}

.check-row {
  margin-top: 8px;
}

.btns {
  display: flex;
  gap: 6px;
  justify-content: center;
  padding-top: 8px;
}
</style>

