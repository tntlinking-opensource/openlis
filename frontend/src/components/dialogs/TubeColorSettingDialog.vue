<template>
  <el-dialog :model-value="modelValue" @update:model-value="$emit('update:modelValue', $event)" title="试管颜色设置" width="500px" :close-on-click-modal="false" append-to-body>
    <div style="margin-bottom:12px;display:flex;gap:8px;align-items:center;">
      <el-input v-model="keyword" placeholder="搜索(名称/拼音)" clearable style="width:200px" @keyup.enter="loadList" />
      <el-button type="primary" @click="loadList">查询</el-button>
      <el-button type="success" @click="openForm(null)">新增</el-button>
    </div>

    <el-table :data="list" border stripe max-height="400" size="small">
      <el-table-column prop="sgys" label="试管颜色" width="150" />
      <el-table-column prop="pym" label="拼音码" width="120" />
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{row}">
          <el-button link type="primary" size="small" @click="openForm(row)">编辑</el-button>
          <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="formVisible" :title="form.pym ? '编辑试管颜色' : '新增试管颜色'" width="400px" append-to-body>
      <el-form :model="form" label-width="80px" size="small">
        <el-form-item label="试管颜色" required><el-input v-model="form.sgys" /></el-form-item>
        <el-form-item label="拼音码" required><el-input v-model="form.pym" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </el-dialog>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { fetchTubeColors, saveTubeColor, deleteTubeColor } from '../../api/tubeColor'

const props = defineProps({ modelValue: Boolean })
const emit = defineEmits(['update:modelValue'])

const keyword = ref('')
const list = ref([])
const formVisible = ref(false)
const form = ref({})

const loadList = async () => {
  try {
    const { data } = await fetchTubeColors(keyword.value)
    list.value = Array.isArray(data) ? data : []
  } catch (e) { ElMessage.error('查询失败') }
}

const openForm = (row) => {
  if (row) {
    form.value = { ...row, oldPym: row.pym }
  } else {
    form.value = { sgys: '', pym: '', oldPym: '' }
  }
  formVisible.value = true
}

const handleSave = async () => {
  try {
    const { data } = await saveTubeColor(form.value)
    if (data.success) { ElMessage.success('保存成功'); formVisible.value = false; loadList() }
    else ElMessage.error(data.message)
  } catch (e) { ElMessage.error('保存失败') }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定删除"${row.sgys}"？`, '提示', { type: 'warning' })
    const { data } = await deleteTubeColor(row.pym)
    if (data.success) { ElMessage.success('删除成功'); loadList() }
    else ElMessage.error(data.message)
  } catch (e) {}
}

onMounted(() => { loadList() })
</script>