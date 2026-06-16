<template>
  <el-dialog :model-value="modelValue" @update:model-value="$emit('update:modelValue', $event)" title="仪器项目组合设置" width="800px" :close-on-click-modal="false" append-to-body>
    <div style="display:flex;gap:16px;height:450px;">
      <div style="flex:1;border:1px solid #e4e7ed;border-radius:4px;overflow:auto;">
        <div style="padding:8px 12px;background:#f5f7fa;font-weight:500;border-bottom:1px solid #e4e7ed;">仪器 → 组合</div>
        <el-tree :data="tree" :props="{ label: 'label', children: 'children' }" node-key="id" default-expand-all highlight-current @node-click="onNodeClick">
          <template #default="{ node, data }">
            <span :style="{ color: data.type === 'combo' ? '#67c23a' : '#303133' }">
              {{ data.label }}
              <el-button v-if="data.type === 'combo'" link type="danger" size="small" @click.stop="removeCombo(data)">移除</el-button>
            </span>
          </template>
        </el-tree>
      </div>
      <div style="width:280px;">
        <div style="margin-bottom:8px;font-weight:500;">分配组合到仪器</div>
        <el-select v-model="selectedInst" placeholder="选择仪器" style="width:100%;margin-bottom:8px" @change="loadUnassigned">
          <el-option v-for="i in instruments" :key="i.sbDjid || i.sb_djid" :label="i.sbmc" :value="i.sbDjid || i.sb_djid" />
        </el-select>
        <el-select v-model="selectedCombo" placeholder="选择组合" style="width:100%;margin-bottom:8px">
          <el-option v-for="c in unassignedCombos" :key="c.zhid" :label="c.zhmc" :value="c.zhid" />
        </el-select>
        <el-button type="primary" style="width:100%" @click="doAssign" :disabled="!selectedInst || !selectedCombo">分配</el-button>
      </div>
    </div>
  </el-dialog>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { fetchInstrumentComboTree, assignCombo, removeInstrumentCombo, fetchUnassignedCombos } from '../../api/instrumentCombo'

const props = defineProps({ modelValue: Boolean })
const emit = defineEmits(['update:modelValue'])

const tree = ref([])
const instruments = ref([])
const selectedInst = ref(null)
const selectedCombo = ref(null)
const unassignedCombos = ref([])

const loadTree = async () => {
  try {
    const { data } = await fetchInstrumentComboTree()
    tree.value = Array.isArray(data) ? data : []
    instruments.value = tree.value.map(n => ({ sbDjid: n.sbDjid, sbmc: n.label }))
  } catch (e) {}
}

const loadUnassigned = async () => {
  if (!selectedInst.value) return
  try {
    const { data } = await fetchUnassignedCombos(selectedInst.value)
    unassignedCombos.value = Array.isArray(data) ? data : []
    selectedCombo.value = null
  } catch (e) {}
}

const doAssign = async () => {
  try {
    const { data } = await assignCombo({ sbDjid: selectedInst.value, zhid: selectedCombo.value })
    if (data.success) { ElMessage.success('分配成功'); loadTree(); loadUnassigned() }
    else ElMessage.error(data.message)
  } catch (e) {}
}

const removeCombo = async (node) => {
  try {
    await ElMessageBox.confirm('确定移除此组合？', '提示', { type: 'warning' })
    const instId = node.id.replace('combo_', '').split('_')[0]
    const { data } = await removeInstrumentCombo(instId, node.zhid)
    if (data.success) { ElMessage.success('移除成功'); loadTree() }
    else ElMessage.error(data.message)
  } catch (e) {}
}

const onNodeClick = () => {}

onMounted(() => { loadTree() })
</script>
