<template>
  <el-dialog :model-value="modelValue" @update:model-value="$emit('update:modelValue', $event)" title="批量系数设置" width="800px" :close-on-click-modal="false" append-to-body>
    <div style="display:flex;gap:16px;height:450px;">
      <div style="width:280px;border:1px solid #e4e7ed;border-radius:4px;overflow:auto;">
        <div style="padding:8px 12px;background:#f5f7fa;font-weight:500;border-bottom:1px solid #e4e7ed;">仪器列表</div>
        <el-table :data="instruments" border stripe size="small" highlight-current-row @current-change="onInstSelect" max-height="400">
          <el-table-column prop="sbDjid" label="ID" width="60" />
          <el-table-column prop="sbmc" label="仪器名称" />
        </el-table>
      </div>
      <div style="flex:1;overflow:auto;">
        <div style="margin-bottom:12px;display:flex;justify-content:space-between;align-items:center;">
          <strong>项目系数 - {{ selectedInst?.sbmc || '请选择仪器' }}</strong>
          <el-button type="primary" size="small" @click="handleBatchSet" :disabled="!selectedInst">批量设置系数</el-button>
        </div>
        <el-table :data="items" border stripe size="small" max-height="350">
          <el-table-column prop="xmdm" label="项目代码" width="80" />
          <el-table-column prop="xmzwmc" label="项目名称" width="150" />
          <el-table-column prop="xmdw" label="单位" width="60" />
          <el-table-column prop="xs" label="系数" width="100">
            <template #default="{row}">
              <el-input-number v-model="row.xs" :min="0" :max="100" :precision="4" size="small" style="width:90px" @change="onXsChange(row)" />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80">
            <template #default="{row}">
              <el-button link type="primary" size="small" @click="resetCoeff(row)">重置</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div v-if="selectedInst" style="margin-top:12px;text-align:right;">
          <el-button type="primary" @click="handleSave">保存全部</el-button>
        </div>
      </div>
    </div>

    <el-dialog v-model="batchFormVisible" title="批量设置系数" width="350px" append-to-body>
      <el-form :model="batchForm" label-width="80px" size="small">
        <el-form-item label="系数值" required>
          <el-input-number v-model="batchForm.xs" :min="0" :max="100" :precision="4" style="width:100%" />
        </el-form-item>
        <el-form-item label="说明">
          <span style="color:#909399;font-size:12px;">将以此系数值更新该仪器下所有项目</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="batchFormVisible = false">取消</el-button>
        <el-button type="primary" @click="applyBatchSet">应用</el-button>
      </template>
    </el-dialog>
  </el-dialog>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { fetchInstrumentItemTree, batchSaveCoeff } from '../../api/instrumentItem'

const props = defineProps({ modelValue: Boolean })
const emit = defineEmits(['update:modelValue'])

const instruments = ref([])
const items = ref([])
const selectedInst = ref(null)
const batchFormVisible = ref(false)
const batchForm = ref({ xs: 1 })
const modifiedItems = ref(new Set())

const loadInstruments = async () => {
  try {
    const { data } = await fetchInstrumentItemTree()
    instruments.value = Array.isArray(data) ? data.map(i => ({ sbDjid: i.sbDjid, sbmc: i.label })) : []
  } catch (e) { ElMessage.error('加载仪器列表失败') }
}

const onInstSelect = (row) => {
  selectedInst.value = row
  if (row) {
    loadInstItems(row.sbDjid)
  } else {
    items.value = []
  }
}

const loadInstItems = async (sbDjid) => {
  try {
    const { data } = await fetchInstrumentItemTree()
    const instNode = Array.isArray(data) ? data.find(i => i.sbDjid === sbDjid) : null
    if (instNode && instNode.children) {
      items.value = instNode.children.map(item => ({
        xmid: item.xmid,
        xmdm: item.xmdm,
        xmzwmc: item.xmzwmc,
        xmdw: item.xmdw || '',
        xs: item.xs != null ? Number(item.xs) : 1
      }))
    } else {
      items.value = []
    }
    modifiedItems.value.clear()
  } catch (e) { ElMessage.error('加载项目列表失败') }
}

const onXsChange = (row) => {
  modifiedItems.value.add(row.xmid)
}

const resetCoeff = (row) => {
  row.xs = 1
  modifiedItems.value.add(row.xmid)
}

const handleBatchSet = () => {
  batchForm.value = { xs: 1 }
  batchFormVisible.value = true
}

const applyBatchSet = () => {
  if (items.value.length === 0) {
    ElMessage.warning('该项目下没有可设置的项目')
    return
  }
  items.value.forEach(item => {
    item.xs = batchForm.value.xs
  })
  modifiedItems.value = new Set(items.value.map(i => i.xmid))
  batchFormVisible.value = false
}

const handleSave = async () => {
  if (!selectedInst.value || items.value.length === 0) {
    ElMessage.warning('没有可保存的数据')
    return
  }
  try {
    const itemsToSave = items.value
      .filter(item => modifiedItems.value.has(item.xmid))
      .map(item => ({
        xmid: item.xmid,
        xmdm: item.xmdm,
        xs: item.xs
      }))
    if (itemsToSave.length === 0) {
      ElMessage.info('没有修改的数据')
      return
    }
    const { data } = await batchSaveCoeff({
      sbDjid: selectedInst.value.sbDjid,
      items: itemsToSave
    })
    if (data.success) {
      ElMessage.success('保存成功')
      modifiedItems.value.clear()
      loadInstItems(selectedInst.value.sbDjid)
    } else {
      ElMessage.error(data.message || '保存失败')
    }
  } catch (e) { ElMessage.error('保存失败') }
}

onMounted(() => { loadInstruments() })
</script>