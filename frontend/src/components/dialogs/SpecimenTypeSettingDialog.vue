<template>
  <el-dialog :model-value="modelValue" @update:model-value="$emit('update:modelValue', $event)" title="标本类型设置" width="1000px" :close-on-click-modal="false" append-to-body>
    <div style="margin-bottom:12px;display:flex;gap:8px;align-items:center;">
      <el-input v-model="keyword" placeholder="搜索(名称/拼音)" clearable style="width:200px" @keyup.enter="loadList" />
      <el-button type="primary" @click="loadList">查询</el-button>
      <el-button type="success" @click="openForm(null)">新增</el-button>
    </div>
    <el-table :data="list" border stripe max-height="400" size="small">
      <el-table-column prop="bm" label="编码" width="60" />
      <el-table-column prop="bmsm" label="名称" min-width="100" />
      <el-table-column prop="pym" label="拼音码" width="80" />
      <el-table-column prop="qtdm" label="其他代码" width="80" />
      <el-table-column prop="xssx" label="显示顺序" width="80" />
      <el-table-column prop="whonet" label="WHONET" width="80" />
      <el-table-column prop="his_bmdm" label="HIS编码" width="100" />
      <el-table-column prop="rqdm" label="容器代码" width="80" />
      <el-table-column prop="rqlx" label="容器类型" width="80" />
      <el-table-column prop="cjyq" label="采集要求" min-width="120" show-overflow-tooltip />
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{row}">
          <el-button link type="primary" size="small" @click="openForm(row)">编辑</el-button>
          <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="formVisible" title="编辑标本类型" width="500px" append-to-body>
      <el-form :model="form" label-width="80px" size="small">
        <el-form-item label="名称" required><el-input v-model="form.bmsm" /></el-form-item>
        <el-form-item label="拼音码" required><el-input v-model="form.pym" /></el-form-item>
        <el-form-item label="WHONET"><el-input v-model="form.whonet" /></el-form-item>
        <el-form-item label="其他代码"><el-input v-model="form.qtdm" /></el-form-item>
        <el-form-item label="显示顺序"><el-input-number v-model="form.xssx" :min="0" /></el-form-item>
        <el-form-item label="HIS编码"><el-input v-model="form.his_bmdm" /></el-form-item>
        <el-form-item label="容器代码"><el-input v-model="form.rqdm" /></el-form-item>
        <el-form-item label="容器类型"><el-input v-model="form.rqlx" /></el-form-item>
        <el-form-item label="采集要求"><el-input v-model="form.cjyq" type="textarea" :rows="2" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </el-dialog>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { fetchSpecimenTypes, saveSpecimenType, deleteSpecimenType } from '../../api/specimenType'

const props = defineProps({ modelValue: Boolean })
const emit = defineEmits(['update:modelValue'])

const keyword = ref('')
const list = ref([])
const formVisible = ref(false)
const form = ref({})

const loadList = async () => {
  try {
    const res = await fetchSpecimenTypes({ keyword: keyword.value })
    const data = res.data
    list.value = Array.isArray(data) ? data : []
  } catch (e) { ElMessage.error('查询失败') }
}

const openForm = (row) => {
  form.value = row ? { ...row } : { bm: 0, bmsm: '', pym: '', xssx: 0 }
  formVisible.value = true
}

const handleSave = async () => {
  try {
    const { data } = await saveSpecimenType(form.value)
    if (data.success) { ElMessage.success('保存成功'); formVisible.value = false; loadList() }
    else ElMessage.error(data.message)
  } catch (e) { ElMessage.error('保存失败') }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定删除"${row.bmsm}"？`, '提示', { type: 'warning' })
    const { data } = await deleteSpecimenType(row.bm)
    if (data.success) { ElMessage.success('删除成功'); loadList() }
    else ElMessage.error(data.message)
  } catch (e) {}
}

onMounted(() => { loadList() })
</script>
