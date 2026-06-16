<template>
  <el-dialog :model-value="modelValue" @update:model-value="$emit('update:modelValue', $event)" title="高低标志设置" width="600px" :close-on-click-modal="false" append-to-body>
    <el-alert type="info" :closable="false" style="margin-bottom:12px">
      系统同一时间仅允许一组活跃的高低值标志（启用状态）。停用的标志组可以删除。
    </el-alert>

    <div style="margin-bottom:12px;display:flex;gap:8px;">
      <el-button type="success" @click="openForm(null)">新增标志组</el-button>
    </div>

    <el-table :data="groups" border stripe size="small">
      <el-table-column prop="bhid" label="组ID" width="60" />
      <el-table-column label="高标志" width="80">
        <template #default="{row}">{{ row.high }}</template>
      </el-table-column>
      <el-table-column label="低标志" width="80">
        <template #default="{row}">{{ row.low }}</template>
      </el-table-column>
      <el-table-column label="报警高" width="80">
        <template #default="{row}">{{ row.alarmHigh }}</template>
      </el-table-column>
      <el-table-column label="报警低" width="80">
        <template #default="{row}">{{ row.alarmLow }}</template>
      </el-table-column>
      <el-table-column label="状态" width="80">
        <template #default="{row}">
          <el-tag :type="row.active ? 'success' : 'info'" size="small">{{ row.active ? '启用' : '停用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120">
        <template #default="{row}">
          <el-button link type="primary" size="small" @click="openForm(row)">编辑</el-button>
          <el-button link type="danger" size="small" @click="handleDelete(row)" :disabled="row.active">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="formVisible" :title="form.bhid ? '编辑标志组' : '新增标志组'" width="400px" append-to-body>
      <el-form :model="form" label-width="80px" size="small">
        <el-form-item label="高标志" required><el-input v-model="form.high" /></el-form-item>
        <el-form-item label="低标志" required><el-input v-model="form.low" /></el-form-item>
        <el-form-item label="报警高"><el-input v-model="form.alarmHigh" /></el-form-item>
        <el-form-item label="报警低"><el-input v-model="form.alarmLow" /></el-form-item>
        <el-form-item label="启用"><el-switch v-model="form.activate" /></el-form-item>
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
import { fetchHighLowFlags, saveHighLowFlags, deleteHighLowFlags } from '../../api/highlowFlag'

const props = defineProps({ modelValue: Boolean })
const emit = defineEmits(['update:modelValue'])

const groups = ref([])
const formVisible = ref(false)
const form = ref({})

const loadGroups = async () => {
  try {
    const { data } = await fetchHighLowFlags()
    const rows = Array.isArray(data) ? data : []
    const map = {}
    rows.forEach(r => {
      const baseId = Math.floor(((r.bhid || 0) - 1) / 4) * 4 + 1
      if (!map[baseId]) map[baseId] = { bhid: baseId, high: '', low: '', alarmHigh: '', alarmLow: '', active: false }
      const bs = r.bs || 0
      if (bs === 1) {
        map[baseId].high = r.bh
        map[baseId].active = !!r.sybz
      }
      else if (bs === 0) {
        map[baseId].low = r.bh
        if (!map[baseId].active) map[baseId].active = !!r.sybz
      }
      else if (bs === 3) { map[baseId].alarmHigh = r.bh }
      else if (bs === 2) { map[baseId].alarmLow = r.bh }
    })
    groups.value = Object.values(map)
  } catch (e) {}
}

const openForm = (row) => {
  if (row) {
    form.value = { ...row, activate: row.active }
  } else {
    form.value = { bhid: 0, high: '', low: '', alarmHigh: '', alarmLow: '', activate: groups.value.length === 0 }
  }
  formVisible.value = true
}

const handleSave = async () => {
  try {
    const { data } = await saveHighLowFlags(form.value)
    if (data.success) { ElMessage.success('保存成功'); formVisible.value = false; loadGroups() }
    else ElMessage.error(data.message)
  } catch (e) { ElMessage.error('保存失败') }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定删除此标志组？', '提示', { type: 'warning' })
    const { data } = await deleteHighLowFlags(row.bhid)
    if (data.success) { ElMessage.success('删除成功'); loadGroups() }
    else ElMessage.error(data.message)
  } catch (e) {}
}

onMounted(() => { loadGroups() })
</script>
