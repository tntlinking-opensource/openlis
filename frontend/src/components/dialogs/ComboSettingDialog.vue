<template>
  <el-dialog :model-value="modelValue" @update:model-value="$emit('update:modelValue', $event)" title="项目组合设置" width="1100px" :close-on-click-modal="false" append-to-body>
    <div style="display:flex;gap:16px;height:550px;">
      <div style="width:380px;display:flex;flex-direction:column;">
        <div style="margin-bottom:8px;display:flex;gap:8px;align-items:center;">
          <el-input v-model="keyword" placeholder="搜索组合" clearable style="width:160px" @keyup.enter="loadList" />
          <el-button type="primary" size="small" @click="loadList">查询</el-button>
          <el-button type="success" size="small" @click="openForm(null)">新增</el-button>
        </div>
        <el-table :data="list" border stripe size="small" max-height="200" highlight-current-row @current-change="onComboSelect" @row-dblclick="openForm">
          <el-table-column prop="zhid" label="ID" width="60" />
          <el-table-column prop="zhmc" label="组合名称" min-width="120" />
          <el-table-column prop="pym" label="拼音码" width="80" />
          <el-table-column prop="sfbz" label="收费" width="60" />
          <el-table-column prop="qybz" label="启用" width="50">
            <template #default="{row}"><el-tag :type="row.qybz==1?'success':'info'" size="small">{{ row.qybz==1?'是':'否' }}</el-tag></template>
          </el-table-column>
          <el-table-column label="操作" width="80" fixed="right">
            <template #default="{row}">
              <el-button link type="danger" size="small" @click.stop="handleDelete(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <div v-if="selectedCombo" style="margin-top:12px;flex:1;display:flex;flex-direction:column;">
          <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:8px;">
            <strong>组合明细 [{{ comboItems.length }}项]</strong>
            <div style="display:flex;gap:4px;">
              <el-button size="small" type="success" @click="showAddItem = true">添加</el-button>
              <el-button size="small" @click="showCopyFrom = true">复制</el-button>
            </div>
          </div>
          <el-table :data="comboItems" border stripe size="small" max-height="300" show-summary>
            <el-table-column type="index" label="序" width="40" />
            <el-table-column prop="xmdm" label="代码" width="70" />
            <el-table-column prop="xmzwmc" label="项目名称" min-width="120" show-overflow-tooltip />
            <el-table-column prop="xmdw" label="单位" width="60" />
            <el-table-column prop="mrjg" label="默认结果" width="80">
              <template #default="{row}">
                <el-input v-model="row.mrjg" size="small" style="width:70px" @change="updateItemDefault(row)" />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="70" fixed="right">
              <template #default="{row, $index}">
                <el-button link type="primary" size="small" @click.stop="moveItemUp($index)" :disabled="$index===0">↑</el-button>
                <el-button link type="primary" size="small" @click.stop="moveItemDown($index)" :disabled="$index===comboItems.length-1">↓</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <div v-else style="flex:1;display:flex;align-items:center;justify-content:center;color:#909399;">
          选择上方组合查看明细
        </div>
      </div>

      <div style="flex:1;border:1px solid #e4e7ed;border-radius:4px;padding:12px;" v-if="selectedCombo">
        <h4 style="margin:0 0 12px">组合详情 - {{ selectedCombo.zhmc }}</h4>
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="组合名称">{{ selectedCombo.zhmc }}</el-descriptions-item>
          <el-descriptions-item label="拼音码">{{ selectedCombo.pym }}</el-descriptions-item>
          <el-descriptions-item label="标本类型">{{ selectedCombo.bbzl || '-' }}</el-descriptions-item>
          <el-descriptions-item label="启用状态">{{ selectedCombo.qybz==1?'是':'否' }}</el-descriptions-item>
          <el-descriptions-item label="收费标准">{{ selectedCombo.sfbz }}</el-descriptions-item>
          <el-descriptions-item label="工作量">{{ selectedCombo.gzl }}</el-descriptions-item>
          <el-descriptions-item label="HIS代码" :span="2">{{ selectedCombo.hisXmdm || '-' }}</el-descriptions-item>
          <el-descriptions-item label="HIS名称" :span="2">{{ selectedCombo.hisZhmc || '-' }}</el-descriptions-item>
        </el-descriptions>
        <div style="margin-top:12px;">
          <el-button type="primary" size="small" @click="openForm(selectedCombo)">编辑组合信息</el-button>
        </div>
      </div>
    </div>

    <el-dialog v-model="comboFormVisible" :title="comboForm.zhid?'编辑组合':'新增组合'" width="600px" append-to-body>
      <el-form :model="comboForm" label-width="100px" size="small">
        <el-form-item label="组合名称" required><el-input v-model="comboForm.zhmc" /></el-form-item>
        <el-form-item label="拼音码" required><el-input v-model="comboForm.pym" /></el-form-item>
        <el-row :gutter="12">
          <el-col :span="12"><el-form-item label="HIS代码"><el-input v-model="comboForm.hisXmdm" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="HIS名称"><el-input v-model="comboForm.hisZhmc" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="12">
          <el-col :span="8"><el-form-item label="标本类型"><el-input-number v-model="comboForm.bbzl" :min="0" style="width:100%" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="收费标准"><el-input-number v-model="comboForm.sfbz" :min="0" :precision="2" style="width:100%" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="工作量"><el-input-number v-model="comboForm.gzl" :min="0" :precision="2" style="width:100%" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="12">
          <el-col :span="8"><el-form-item label="启用"><el-switch v-model="comboForm.qybz" :active-value="1" :inactive-value="0" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="标签颜色"><el-input v-model="comboForm.bqys" type="color" style="width:60px;padding:0;" /></el-form-item></el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="comboFormVisible = false">取消</el-button>
        <el-button type="primary" @click="saveComboForm">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showAddItem" title="添加项目到组合" width="500px" append-to-body>
      <el-select v-model="addItemXmid" filterable remote :remote-method="searchItems" placeholder="搜索项目(名称/拼音/代码)" style="width:100%">
        <el-option v-for="i in itemOptions" :key="i.xmid" :label="`${i.xmzwmc} (${i.xmdm})`" :value="i.xmid" />
      </el-select>
      <template #footer>
        <el-button @click="showAddItem = false">取消</el-button>
        <el-button type="primary" @click="addItemToCombo">添加</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showCopyFrom" title="从其他组合复制项目" width="400px" append-to-body>
      <el-select v-model="copyFromZhid" placeholder="选择源组合" style="width:100%">
        <el-option v-for="c in list.filter(i => i.zhid !== selectedCombo?.zhid)" :key="c.zhid" :label="c.zhmc" :value="c.zhid" />
      </el-select>
      <template #footer>
        <el-button @click="showCopyFrom = false">取消</el-button>
        <el-button type="primary" @click="doCopyFrom">复制</el-button>
      </template>
    </el-dialog>
  </el-dialog>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { fetchCombos, saveCombo, deleteCombo, fetchComboItems, addComboItem, removeComboItem, copyComboFrom, reorderComboItems } from '../../api/combo'
import { searchTestItems } from '../../api/testItem'

const props = defineProps({ modelValue: Boolean })
const emit = defineEmits(['update:modelValue'])

const keyword = ref('')
const list = ref([])
const selectedCombo = ref(null)
const comboItems = ref([])
const comboFormVisible = ref(false)
const comboForm = ref({})
const showAddItem = ref(false)
const addItemXmid = ref(null)
const itemOptions = ref([])
const showCopyFrom = ref(false)
const copyFromZhid = ref(null)

const loadList = async () => {
  try {
    const { data } = await fetchCombos({ keyword: keyword.value })
    list.value = Array.isArray(data) ? data : []
  } catch (e) { ElMessage.error('查询失败') }
}

const onComboSelect = (row) => {
  selectedCombo.value = row
  if (row) loadComboItems(row.zhid)
  else comboItems.value = []
}

const loadComboItems = async (zhid) => {
  try {
    const { data } = await fetchComboItems(zhid)
    comboItems.value = Array.isArray(data) ? data : (Array.isArray(data?.data) ? data.data : [])
  } catch (e) {}
}

const openForm = (row) => {
  comboForm.value = row ? { ...row } : { zhid: 0, zhmc: '', pym: '', qybz: 1, sfbz: 0, gzl: 0, bbzl: 0, bqys: '#409EFF' }
  comboFormVisible.value = true
}

const saveComboForm = async () => {
  if (!comboForm.value.zhmc) { ElMessage.warning('组合名称不能为空'); return }
  if (!comboForm.value.pym) { ElMessage.warning('拼音码不能为空'); return }
  try {
    const { data } = await saveCombo(comboForm.value)
    if (data.success) { ElMessage.success('保存成功'); comboFormVisible.value = false; loadList() }
    else ElMessage.error(data.message)
  } catch (e) { ElMessage.error('保存失败') }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定删除组合"${row.zhmc}"？将同时删除其下所有明细！`, '提示', { type: 'warning' })
    const { data } = await deleteCombo(row.zhid)
    if (data.success) { ElMessage.success('删除成功'); if (selectedCombo.value?.zhid === row.zhid) selectedCombo.value = null; loadList() }
    else ElMessage.error(data.message)
  } catch (e) {}
}

const searchItems = async (q) => {
  if (!q) return
  const { data } = await searchTestItems(q)
  itemOptions.value = Array.isArray(data) ? data : []
}

const addItemToCombo = async () => {
  if (!addItemXmid.value || !selectedCombo.value) return
  try {
    const { data } = await addComboItem(selectedCombo.value.zhid, { xmid: addItemXmid.value })
    if (data.success) { ElMessage.success('添加成功'); showAddItem.value = false; addItemXmid.value = null; loadComboItems(selectedCombo.value.zhid) }
    else ElMessage.error(data.message)
  } catch (e) {}
}

const removeItem = async (row) => {
  if (!selectedCombo.value) return
  try {
    const { data } = await removeComboItem(selectedCombo.value.zhid, row.xmid)
    if (data.success) { ElMessage.success('移除成功'); loadComboItems(selectedCombo.value.zhid) }
    else ElMessage.error(data.message)
  } catch (e) {}
}

const updateItemDefault = async (row) => {
}

const moveItemUp = async (index) => {
  if (index === 0) return
  const items = [...comboItems.value]
  const temp = items[index]
  items[index] = items[index - 1]
  items[index - 1] = temp
  comboItems.value = items
  await saveOrder()
}

const moveItemDown = async (index) => {
  if (index === comboItems.value.length - 1) return
  const items = [...comboItems.value]
  const temp = items[index]
  items[index] = items[index + 1]
  items[index + 1] = temp
  comboItems.value = items
  await saveOrder()
}

const saveOrder = async () => {
  if (!selectedCombo.value) return
  const order = comboItems.value.map((item, index) => ({ xmid: item.xmid, id: index + 1 }))
  try {
    await reorderComboItems(selectedCombo.value.zhid, order)
  } catch (e) {}
}

const doCopyFrom = async () => {
  if (!copyFromZhid.value || !selectedCombo.value) return
  try {
    const { data } = await copyComboFrom(selectedCombo.value.zhid, copyFromZhid.value)
    if (data.success) { ElMessage.success('复制成功'); showCopyFrom.value = false; copyFromZhid.value = null; loadComboItems(selectedCombo.value.zhid) }
    else ElMessage.error(data.message)
  } catch (e) {}
}

onMounted(() => { loadList() })
</script>
