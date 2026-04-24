<template>
  <FloatingPanel
    v-model="visible"
    title="人员信息设置"
    :width="980"
    :height="550"
    class="staff-setting-panel"
  >
    <div class="dialog-body">
      <div class="top">
        <div class="grid-title">人员信息列表：</div>
        <el-table
          :data="list"
          height="260"
          border
          size="small"
          highlight-current-row
          @current-change="onRowSelect"
        >
          <el-table-column prop="czydm" label="人员代码" width="90" />
          <el-table-column prop="czyxm" label="姓名" width="120" />
          <el-table-column prop="pym" label="拼音码" width="80" />
          <el-table-column prop="ksdm" label="科室代码" width="80" />
          <el-table-column prop="ksmc" label="所属科室" width="140" />
          <el-table-column prop="gzzmc" label="工作组名称" width="140" />
          <el-table-column prop="zcmc" label="职称" width="90" />
          <el-table-column prop="hisCzydm" label="HIS代码" width="90" />
          <el-table-column prop="ysbz" label="是否医生" width="80">
            <template #default="{ row }">
              <el-checkbox v-model="row.ysbz" disabled />
            </template>
          </el-table-column>
          <el-table-column prop="czybz" label="是否操作员" width="90">
            <template #default="{ row }">
              <el-checkbox v-model="row.czybz" disabled />
            </template>
          </el-table-column>
          <el-table-column prop="glybz" label="是否管理员" width="90">
            <template #default="{ row }">
              <el-checkbox v-model="row.glybz" disabled />
            </template>
          </el-table-column>
          <el-table-column prop="sybz" label="是否使用" width="80">
            <template #default="{ row }">
              <el-checkbox v-model="row.sybz" disabled />
            </template>
          </el-table-column>
        </el-table>
      </div>

      <div class="bottom">
        <div class="form">
          <div class="row">
            <label>人员代码</label>
            <el-input
              v-model="form.czydm"
              size="small"
              class="inp"
              :disabled="editingMode === 'edit'"
            />
            <label>姓 名</label>
            <el-input v-model="form.czyxm" size="small" class="inp" />
            <label>拼音码</label>
            <el-input v-model="form.pym" size="small" class="inp-short" />
          </div>
          <div class="row">
            <label>科室代码</label>
            <el-input v-model="form.ksdm" size="small" class="inp" />
            <label>职称代码</label>
            <el-input v-model="form.zcdm" size="small" class="inp" />
            <label>HIS代码</label>
            <el-input v-model="form.hisCzydm" size="small" class="inp-short" />
          </div>
          <div class="row">
            <label>工作组代码</label>
            <el-input v-model="form.gzzdm" size="small" class="inp" />
            <label>身份证号码</label>
            <el-input v-model="form.czysfzhm" size="small" class="inp-long" />
          </div>
          <div class="row check-row">
            <el-checkbox v-model="form.ysbz">是否医生</el-checkbox>
            <el-checkbox v-model="form.czybz">是否操作员</el-checkbox>
            <el-checkbox v-model="form.glybz">是否管理员</el-checkbox>
            <el-checkbox v-model="form.sybz">是否使用</el-checkbox>
          </div>
          <div class="row check-row">
            <el-checkbox v-model="form.qkmm">清空密码</el-checkbox>
            <el-checkbox v-model="form.qkdzqm">清空电子签名</el-checkbox>
          </div>
        </div>

        <div class="btns">
          <el-button size="small" @click="onAdd" :disabled="editingMode === 'add'">增加(A)</el-button>
          <el-button size="small" @click="onEdit" :disabled="!current || editingMode === 'edit'">
            修改(E)
          </el-button>
          <el-button size="small" @click="onCancel" :disabled="!editingMode">放弃(C)</el-button>
          <el-button size="small" type="primary" @click="onSave" :disabled="!editingMode">
            保存(S)
          </el-button>
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
  czydm: '',
  czyxm: '',
  pym: '',
  ksdm: '',
  zcdm: '',
  hisCzydm: '',
  gzzdm: '',
  czysfzhm: '',
  ysbz: false,
  czybz: true,
  glybz: false,
  sybz: true,
  qkmm: false,
  qkdzqm: false
})

const loadList = async () => {
  const res = await axios.get('/api/basic/staff/list')
  list.value = res.data || []
}

const fillFormFromRow = (row) => {
  if (!row) return
  form.czydm = row.czydm || ''
  form.czyxm = row.czyxm || ''
  form.pym = row.pym || ''
  form.ksdm = row.ksdm || ''
  form.zcdm = row.zcdm || ''
  form.hisCzydm = row.hisCzydm || ''
  form.gzzdm = row.gzzdm || ''
  form.czysfzhm = row.czysfzhm || ''
  form.ysbz = row.ysbz ?? false
  form.czybz = row.czybz ?? true
  form.glybz = row.glybz ?? false
  form.sybz = row.sybz ?? true
  form.qkmm = false
  form.qkdzqm = false
}

const resetForm = () => {
  fillFormFromRow({
    czydm: '',
    czyxm: '',
    pym: '',
    ksdm: '',
    zcdm: '',
    hisCzydm: '',
    gzzdm: '',
    czysfzhm: '',
    ysbz: false,
    czybz: true,
    glybz: false,
    sybz: true
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
    if (!form.czydm.trim()) {
      ElMessage.warning('人员代码不能为空')
      return
    }
    if (!form.czyxm.trim()) {
      ElMessage.warning('姓名不能为空')
      return
    }
    if (!form.ksdm.trim()) {
      ElMessage.warning('科室代码不能为空')
      return
    }

    const payload = {
      ...form,
      xgbz: editingMode.value === 'edit'
    }

    const res = await axios.post('/api/basic/staff/save', payload)
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
.staff-setting-panel :deep(.panel-content) {
  padding: 8px 10px 10px;
  background: #9cc6ea;
}

.dialog-body {
  display: flex;
  flex-direction: column;
  gap: 8px;
  background: #9cc6ea;
}

.top {
  flex: 1;
}

.grid-title {
  font-size: 12px;
  margin-bottom: 4px;
}

.bottom {
  flex: 0 0 auto;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.form {
  background: #dbe9f6;
  padding: 6px 8px;
  border: 1px solid #7ba5d8;
}

.row {
  display: flex;
  align-items: center;
  margin-bottom: 4px;
  gap: 4px;
}

label {
  width: 72px;
  font-size: 12px;
}

.inp {
  width: 150px;
}

.inp-short {
  width: 110px;
}

.inp-long {
  width: 240px;
}

.check-row {
  gap: 16px;
}

.btns {
  display: flex;
  justify-content: flex-end;
  gap: 6px;
}
</style>


