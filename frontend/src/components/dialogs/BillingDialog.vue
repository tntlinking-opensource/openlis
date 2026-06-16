<template>
  <el-dialog :model-value="modelValue" @update:model-value="$emit('update:modelValue', $event)" title="费用确认与取消" width="700px" :close-on-click-modal="false" append-to-body>
    <div style="margin-bottom:12px;display:flex;gap:8px;align-items:center;">
      <el-input v-model="searchForm.syh" placeholder="样本号" clearable style="width:120px" />
      <el-input v-model="searchForm.brxm" placeholder="姓名" clearable style="width:100px" />
      <el-input v-model="searchForm.brxxTmh" placeholder="条码号" clearable style="width:140px" />
      <el-date-picker v-model="searchForm.jyrq" type="date" value-format="YYYY-MM-DD" placeholder="检验日期" style="width:130px" />
      <el-button type="primary" @click="searchSamples">查询</el-button>
      <el-button @click="resetSearch">重置</el-button>
    </div>

    <div style="margin-bottom:8px;display:flex;gap:8px;align-items:center;">
      <el-checkbox v-model="selectAll" @change="handleSelectAll">全选</el-checkbox>
      <span style="color:#909399;font-size:12px;">已选择 {{ selectedSamples.length }} 条</span>
    </div>

    <el-table ref="sampleTableRef" :data="samples" border stripe size="small" max-height="200" @selection-change="handleSelectionChange" @row-click="handleRowClick" highlight-current-row>
      <el-table-column type="selection" width="45" />
      <el-table-column prop="brxx_tmh" label="条码号" width="130" />
      <el-table-column prop="syh" label="样本号" width="80" />
      <el-table-column prop="brxm" label="姓名" width="80" />
      <el-table-column prop="ksmc" label="科室" width="100" />
      <el-table-column prop="sfbz" label="收费状态" width="80">
        <template #default="{row}">
          <el-tag :type="row.sfbz ? 'success' : 'info'" size="small">{{ row.sfbz ? '已收费' : '未收费' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="zfbz" label="作废状态" width="80">
        <template #default="{row}">
          <el-tag :type="row.zfbz ? 'danger' : 'success'" size="small">{{ row.zfbz ? '已作废' : '正常' }}</el-tag>
        </template>
      </el-table-column>
    </el-table>

    <div style="margin-top:16px;">
      <h4 style="margin:0 0 12px 0;font-size:14px;">费用明细</h4>
      <el-table :data="feeDetails" border stripe size="small" max-height="180">
        <el-table-column prop="zhxmdm" label="项目代码" width="100" />
        <el-table-column prop="zhxmmc" label="项目名称" />
        <el-table-column prop="sf" label="收费" width="60">
          <template #default="{row}">
            <el-tag :type="row.sf ? 'success' : 'info'" size="small">{{ row.sf ? '是' : '否' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="zfbz" label="作废" width="60">
          <template #default="{row}">
            <el-tag :type="row.zfbz ? 'danger' : 'success'" size="small">{{ row.zfbz ? '是' : '否' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="dj" label="单价" width="70" />
        <el-table-column prop="sl" label="数量" width="60" />
      </el-table>
    </div>

    <div style="margin-top:16px;display:flex;gap:8px;">
      <el-button type="primary" @click="doBatchConfirm" :disabled="selectedSamples.length === 0">批量确认收费</el-button>
      <el-button type="warning" @click="doBatchCancel" :disabled="selectedSamples.length === 0">批量取消收费</el-button>
      <el-button type="danger" @click="doBatchInvalidate" :disabled="selectedSamples.length === 0">批量作废</el-button>
    </div>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { fetchBillingSamples, fetchBillingDetails, batchConfirmBilling, batchCancelBilling, batchInvalidateBilling } from '../../api/billing'

const props = defineProps({ modelValue: Boolean })
const emit = defineEmits(['update:modelValue'])

const sampleTableRef = ref(null)
const samples = ref([])
const selectedSamples = ref([])
const selectAll = ref(false)
const feeDetails = ref([])

const searchForm = reactive({
  syh: '',
  brxm: '',
  brxxTmh: '',
  jyrq: ''
})

const searchSamples = async () => {
  try {
    const params = {}
    if (searchForm.syh) params.syh = searchForm.syh
    if (searchForm.brxm) params.brxm = searchForm.brxm
    if (searchForm.brxxTmh) params.brxxTmh = searchForm.brxxTmh
    if (searchForm.jyrq) params.jyrq = searchForm.jyrq
    const { data } = await fetchBillingSamples(params)
    samples.value = Array.isArray(data) ? data : []
    selectedSamples.value = []
    selectAll.value = false
    feeDetails.value = []
  } catch (e) { ElMessage.error('查询失败') }
}

const resetSearch = () => {
  searchForm.syh = ''
  searchForm.brxm = ''
  searchForm.brxxTmh = ''
  searchForm.jyrq = ''
  searchSamples()
}

const handleSelectionChange = (selection) => {
  selectedSamples.value = selection
}

const handleRowClick = (row) => {
  loadFeeDetails(row.brxx_id)
}

const handleSelectAll = (val) => {
  if (val) {
    sampleTableRef.value?.toggleAllSelection()
  } else {
    sampleTableRef.value?.clearSelection()
  }
}

const loadFeeDetails = async (brxxId) => {
  try {
    const { data } = await fetchBillingDetails(brxxId)
    feeDetails.value = Array.isArray(data) ? data : []
  } catch (e) {}
}

const doBatchConfirm = async () => {
  if (!selectedSamples.value.length) { ElMessage.warning('请选择样本'); return }
  try {
    await ElMessageBox.confirm(`确认对 ${selectedSamples.value.length} 条样本收费?`, '提示', { type: 'info' })
    const brxxIds = selectedSamples.value.map(s => s.brxx_id)
    const user = JSON.parse(localStorage.getItem('user') || '{}')
    const { data } = await batchConfirmBilling(brxxIds, { czydm: user.czydm || 'admin' })
    if (data.success) {
      ElMessage.success(`收费成功 (${data.count || selectedSamples.value.length})`)
      searchSamples()
    } else {
      ElMessage.error(data.message)
    }
  } catch (e) {}
}

const doBatchCancel = async () => {
  if (!selectedSamples.value.length) { ElMessage.warning('请选择样本'); return }
  try {
    await ElMessageBox.confirm(`确定取消 ${selectedSamples.value.length} 条样本的收费?`, '提示', { type: 'warning' })
    const brxxIds = selectedSamples.value.map(s => s.brxx_id)
    const user = JSON.parse(localStorage.getItem('user') || '{}')
    const { data } = await batchCancelBilling(brxxIds, { czydm: user.czydm || 'admin' })
    if (data.success) {
      ElMessage.success(`取消收费成功 (${data.count || selectedSamples.value.length})`)
      searchSamples()
    } else {
      ElMessage.error(data.message)
    }
  } catch (e) {}
}

const doBatchInvalidate = async () => {
  if (!selectedSamples.value.length) { ElMessage.warning('请选择样本'); return }
  try {
    await ElMessageBox.confirm(`确定作废 ${selectedSamples.value.length} 条样本的费用?`, '提示', { type: 'danger' })
    const brxxIds = selectedSamples.value.map(s => s.brxx_id)
    const user = JSON.parse(localStorage.getItem('user') || '{}')
    const { data } = await batchInvalidateBilling(brxxIds, { czydm: user.czydm || 'admin' })
    if (data.success) {
      ElMessage.success(`作废成功 (${data.count || selectedSamples.value.length})`)
      searchSamples()
    } else {
      ElMessage.error(data.message)
    }
  } catch (e) {}
}

onMounted(() => { searchSamples() })
</script>