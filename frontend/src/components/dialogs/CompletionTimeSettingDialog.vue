<template>
  <el-dialog
    :model-value="modelValue"
    @update:model-value="$emit('update:modelValue', $event)"
    title="项目完成时间设置"
    width="900px"
    :close-on-click-modal="false"
    append-to-body
  >
    <div class="completion-settings">
      <div class="toolbar">
        <el-select v-model="selectedZhid" placeholder="选择组合" clearable style="width:200px" @change="loadSettings">
          <el-option v-for="c in comboList" :key="c.zhid" :label="c.zhmc" :value="c.zhid" />
        </el-select>
        <el-radio-group v-model="form.szlb" size="small" style="margin-left:10px">
          <el-radio-button :value="1">完成所需时间</el-radio-button>
          <el-radio-button :value="2">定点完成时间</el-radio-button>
        </el-radio-group>
        <el-checkbox v-model="showDisabled" size="small" style="margin-left:10px">显示停用</el-checkbox>
        <el-button type="success" size="small" @click="handleAdd" :disabled="!selectedZhid">新增</el-button>
      </div>

      <el-table :data="tableData" border stripe size="small" max-height="300" style="margin-top:10px">
        <el-table-column prop="qssj" label="起始时间" width="100" />
        <el-table-column prop="jssj" label="结束时间" width="100" />
        <el-table-column prop="ygrqmc" label="完成日期" width="100" />
        <el-table-column prop="ygsj" label="完成时间" width="100" />
        <el-table-column prop="ddsj" label="完成所需时间(分)" width="130" />
        <el-table-column prop="tybz" label="停用标志" width="80">
          <template #default="{row}">
            <el-tag :type="row.tybz === 1 ? 'danger' : 'success'" size="small">
              {{ row.tybz === 1 ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{row}">
            <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-divider content-position="left" v-if="formVisible">编辑</el-divider>
      <el-form v-if="formVisible" :model="form" label-width="120px" size="small" style="margin-top:10px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="起始时间" required>
              <el-input v-model="form.qssj" placeholder="如: 08:00:00" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束时间" required>
              <el-input v-model="form.jssj" placeholder="如: 12:00:00" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="完成日期" required>
              <el-select v-model="form.ygrq" style="width:100%">
                <el-option :value="0" label="当日" />
                <el-option :value="1" label="次日" />
                <el-option :value="2" label="第三日" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="完成时间" required>
              <el-input v-model="form.ygsj" placeholder="如: 15:00:00" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="完成所需时间" required>
              <el-input-number v-model="form.ddsj" :min="0" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="停用标志">
              <el-switch v-model="tybzBool" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24" style="text-align:right">
            <el-button @click="formVisible = false">取消</el-button>
            <el-button type="primary" @click="handleSave">保存</el-button>
          </el-col>
        </el-row>
      </el-form>
    </div>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, watch, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { fetchCombos, fetchCompletionSettings, saveCompletionSetting, deleteCompletionSetting } from '../../api/combo'

const props = defineProps({ modelValue: Boolean })
const emit = defineEmits(['update:modelValue'])

const comboList = ref([])
const selectedZhid = ref(null)
const tableData = ref([])
const formVisible = ref(false)
const showDisabled = ref(false)

const form = reactive({
  id: null,
  zhid: null,
  szlb: 1,
  qssj: '',
  jssj: '',
  ygrq: 0,
  ygsj: '',
  ddsj: 60,
  tybz: 0
})

const tybzBool = computed({
  get: () => form.tybz === 1,
  set: (val) => { form.tybz = val ? 1 : 0 }
})

const loadCombos = async () => {
  try {
    const { data } = await fetchCombos()
    comboList.value = Array.isArray(data) ? data : []
  } catch (e) {}
}

const loadSettings = async () => {
  if (!selectedZhid.value) {
    tableData.value = []
    return
  }
  try {
    const params = {
      zhid: selectedZhid.value,
      szlb: form.szlb,
      tybz: showDisabled.value ? null : 0
    }
    const { data } = await fetchCompletionSettings(params)
    tableData.value = Array.isArray(data) ? data : []
  } catch (e) {}
}

const handleAdd = () => {
  form.id = null
  form.zhid = selectedZhid.value
  form.szlb = 1
  form.qssj = ''
  form.jssj = ''
  form.ygrq = 0
  form.ygsj = ''
  form.ddsj = 60
  form.tybz = 0
  formVisible.value = true
}

const handleEdit = (row) => {
  form.id = row.id
  form.zhid = row.zhid
  form.szlb = row.szlb
  form.qssj = row.qssj
  form.jssj = row.jssj
  form.ygrq = row.ygrq
  form.ygsj = row.ygsj
  form.ddsj = row.ddsj
  form.tybz = row.tybz
  formVisible.value = true
}

const handleSave = async () => {
  if (!form.qssj || !form.jssj) {
    ElMessage.warning('请填写起始时间和结束时间')
    return
  }
  try {
    const { data } = await saveCompletionSetting({ ...form })
    if (data.success) {
      ElMessage.success(data.message || '保存成功')
      formVisible.value = false
      await loadSettings()
    } else {
      ElMessage.error(data.message || '保存失败')
    }
  } catch (e) {
    ElMessage.error('保存失败')
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定删除此设置？', '提示', { type: 'warning' })
    const { data } = await deleteCompletionSetting(row.id)
    if (data.success) {
      ElMessage.success(data.message || '删除成功')
      await loadSettings()
    } else {
      ElMessage.error(data.message || '删除失败')
    }
  } catch (e) {}
}

watch(() => props.modelValue, (val) => {
  if (val) {
    loadCombos()
    selectedZhid.value = null
    tableData.value = []
    formVisible.value = false
  }
})

watch([selectedZhid, () => form.szlb, showDisabled], () => {
  loadSettings()
})
</script>

<style scoped>
.completion-settings {
  padding: 10px;
}
.toolbar {
  display: flex;
  align-items: center;
  gap: 10px;
}
</style>
