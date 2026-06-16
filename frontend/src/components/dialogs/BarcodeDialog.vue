<template>
  <el-dialog :model-value="modelValue" @update:model-value="$emit('update:modelValue', $event)" title="条码管理" width="800px" :close-on-click-modal="false" append-to-body>
    <el-tabs v-model="activeTab">
      <el-tab-pane label="生成条码" name="generate">
        <div style="margin-bottom:12px;">
          <el-button type="primary" @click="doGenerate">生成新条码</el-button>
          <span v-if="generatedBarcode" style="margin-left:16px;font-size:18px;font-weight:bold;color:#409eff;">
            {{ generatedBarcode }}
          </span>
        </div>
        <el-descriptions title="条码配置" :column="3" border size="small">
          <el-descriptions-item label="前缀">{{ config.prefix || '-' }}</el-descriptions-item>
          <el-descriptions-item label="日期格式">{{ config.dateFormat || '-' }}</el-descriptions-item>
          <el-descriptions-item label="序号长度">{{ config.seqLength || '-' }}</el-descriptions-item>
        </el-descriptions>
      </el-tab-pane>

      <el-tab-pane label="打印标签" name="print">
        <div style="margin-bottom:12px;display:flex;gap:8px;flex-wrap:wrap;">
          <el-input v-model="searchForm.brxm" placeholder="姓名" clearable style="width:120px" />
          <el-input v-model="searchForm.syh" placeholder="样本号" clearable style="width:120px" />
          <el-input v-model="searchForm.brxxTmh" placeholder="条码号" clearable style="width:120px" />
          <el-date-picker v-model="searchForm.jyrq" type="date" value-format="YYYY-MM-DD" placeholder="检验日期" style="width:140px" />
          <el-button type="primary" @click="loadSamples">查询</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </div>
        <div style="margin-bottom:8px;display:flex;gap:8px;">
          <el-checkbox v-model="selectAll" @change="handleSelectAll">全选</el-checkbox>
          <span style="color:#909399;font-size:12px;">已选择 {{ selectedSamples.length }} 条</span>
        </div>
        <el-table ref="sampleTableRef" :data="samples" border stripe size="small" max-height="280" @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="45" />
          <el-table-column prop="brxx_tmh" label="条码号" width="130" />
          <el-table-column prop="syh" label="样本号" width="80" />
          <el-table-column prop="brxm" label="姓名" width="80" />
          <el-table-column prop="brxb" label="性别" width="50">
            <template #default="{row}">{{ row.brxb === 1 ? '男' : row.brxb === 2 ? '女' : '' }}</template>
          </el-table-column>
          <el-table-column prop="brnl" label="年龄" width="60" />
          <el-table-column prop="ksmc" label="科室" width="100" />
          <el-table-column prop="jyrq" label="检验日期" width="100" />
          <el-table-column prop="ybzt" label="状态" width="70">
            <template #default="{row}">
              <el-tag :type="getStatusType(row.ybzt)" size="small">{{ getStatusText(row.ybzt) }}</el-tag>
            </template>
          </el-table-column>
        </el-table>
        <div style="margin-top:12px;display:flex;gap:8px;">
          <el-button type="primary" @click="doPrintLabels" :disabled="selectedSamples.length === 0">打印选中标签</el-button>
          <el-button @click="doReprint" :disabled="selectedSamples.length === 0">重新打印</el-button>
        </div>
      </el-tab-pane>

      <el-tab-pane label="标签预览" name="preview">
        <div style="margin-bottom:12px;">
          <el-button type="primary" @click="refreshPreview" :disabled="!previewSample">刷新预览</el-button>
          <el-button @click="printDirect" :disabled="!previewSample">直接打印</el-button>
        </div>
        <div v-if="previewSample" class="label-preview">
          <div class="barcode-label">
            <div class="label-header">
              <span class="hospital-name">{{ hospitalName }}</span>
              <span class="label-title">检验标本标签</span>
            </div>
            <div class="label-content">
              <div class="label-row">
                <span class="label-key">条码号:</span>
                <span class="label-value barcode-value">{{ previewSample.brxx_tmh }}</span>
              </div>
              <div class="label-row">
                <span class="label-key">姓名:</span>
                <span class="label-value">{{ previewSample.brxm }}</span>
                <span class="label-key">性别:</span>
                <span class="label-value">{{ previewSample.brxb === 1 ? '男' : previewSample.brxb === 2 ? '女' : '-' }}</span>
                <span class="label-key">年龄:</span>
                <span class="label-value">{{ previewSample.brnl || '-' }}</span>
              </div>
              <div class="label-row">
                <span class="label-key">科室:</span>
                <span class="label-value">{{ previewSample.ksmc || '-' }}</span>
              </div>
              <div class="label-row">
                <span class="label-key">床号:</span>
                <span class="label-value">{{ previewSample.brch || '-' }}</span>
                <span class="label-key">标本:</span>
                <span class="label-value">{{ previewSample.bbzl || '-' }}</span>
              </div>
              <div class="label-row">
                <span class="label-key">样本号:</span>
                <span class="label-value">{{ previewSample.syh || '-' }}</span>
                <span class="label-key">日期:</span>
                <span class="label-value">{{ previewSample.jyrq || '-' }}</span>
              </div>
            </div>
            <div class="barcode-area">
              <svg :id="'barcode-' + previewSample.brxx_id" class="barcode-svg"></svg>
            </div>
          </div>
        </div>
        <div v-else style="text-align:center;padding:60px;color:#909399;">
          请在"打印标签"页选择要预览的样本
        </div>
      </el-tab-pane>
    </el-tabs>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { generateBarcode, printBarcodeLabels, reprintBarcodeLabels, fetchBarcodeConfig, fetchUnprintedSamples } from '../../api/barcode'
import { getStatusText as _getStatusText } from '../../utils/sampleStatus'

const props = defineProps({ modelValue: Boolean })
const emit = defineEmits(['update:modelValue'])

const activeTab = ref('generate')
const generatedBarcode = ref('')
const config = ref({})
const sampleTableRef = ref(null)
const samples = ref([])
const selectedSamples = ref([])
const selectAll = ref(false)
const previewSample = ref(null)
const hospitalName = ref('医院名称')

const searchForm = reactive({
  brxm: '',
  syh: '',
  brxxTmh: '',
  jyrq: ''
})

const loadConfig = async () => {
  try {
    const { data } = await fetchBarcodeConfig()
    config.value = data || {}
  } catch (e) {}
}

const doGenerate = async () => {
  try {
    const { data } = await generateBarcode()
    generatedBarcode.value = data?.barcode || data || ''
    ElMessage.success('条码已生成: ' + generatedBarcode.value)
  } catch (e) { ElMessage.error('生成失败') }
}

const loadSamples = async () => {
  try {
    const params = {}
    if (searchForm.brxm) params.brxm = searchForm.brxm
    if (searchForm.syh) params.syh = searchForm.syh
    if (searchForm.brxxTmh) params.brxxTmh = searchForm.brxxTmh
    if (searchForm.jyrq) params.jyrq = searchForm.jyrq
    const { data } = await fetchUnprintedSamples(params)
    samples.value = Array.isArray(data) ? data : []
    selectedSamples.value = []
    selectAll.value = false
  } catch (e) { ElMessage.error('查询失败') }
}

const resetSearch = () => {
  searchForm.brxm = ''
  searchForm.syh = ''
  searchForm.brxxTmh = ''
  searchForm.jyrq = ''
  loadSamples()
}

const handleSelectionChange = (selection) => {
  selectedSamples.value = selection
  previewSample.value = selection.length === 1 ? selection[0] : null
}

const handleSelectAll = (val) => {
  if (val) {
    sampleTableRef.value?.toggleAllSelection()
  } else {
    sampleTableRef.value?.clearSelection()
  }
}

const doPrintLabels = async () => {
  if (!selectedSamples.value.length) { ElMessage.warning('请选择样本'); return }
  try {
    const ids = selectedSamples.value.map(s => s.brxx_id)
    const { data } = await printBarcodeLabels(ids)
    if (data.success) {
      ElMessage.success('标签已发送打印' + (data.count ? ` (${data.count}张)` : ''))
    } else {
      ElMessage.error(data.message || '打印失败')
    }
  } catch (e) { ElMessage.error('打印失败') }
}

const doReprint = async () => {
  if (!selectedSamples.value.length) { ElMessage.warning('请选择样本'); return }
  try {
    const ids = selectedSamples.value.map(s => s.brxx_id)
    const { data } = await reprintBarcodeLabels(ids)
    if (data.success) {
      ElMessage.success('重印完成' + (data.count ? ` (${data.count}张)` : ''))
    } else {
      ElMessage.error(data.message || '重印失败')
    }
  } catch (e) { ElMessage.error('重印失败') }
}

const refreshPreview = () => {
  if (previewSample.value) {
    previewSample.value = { ...previewSample.value }
  }
}

const printDirect = async () => {
  if (!previewSample.value) return
  ElMessage.info('正在生成标签...')
  try {
    const response = await fetch(`/api/barcode/print-pdf/${previewSample.value.brxx_id}`)
    const blob = await response.blob()
    const filename = `label_${previewSample.value.brxx_tmh || previewSample.value.brxx_id}.pdf`
    const url = window.URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = filename
    a.click()
    window.URL.revokeObjectURL(url)
    ElMessage.success('标签已下载')
  } catch (e) {
    ElMessage.error('下载失败')
  }
}

const getStatusType = (ybzt) => {
  if (ybzt === -1) return 'danger'
  if (ybzt >= 3) return 'success'
  if (ybzt >= 2) return 'warning'
  if (ybzt >= 1) return 'info'
  return 'info'
}

const getStatusText = (ybzt) => _getStatusText(ybzt)

watch(activeTab, (val) => {
  if (val === 'print' && samples.value.length === 0) {
    loadSamples()
  }
})

onMounted(() => { loadConfig() })
</script>

<style scoped>
.label-preview {
  display: flex;
  justify-content: center;
  padding: 20px;
  background: #f5f7fa;
}

.barcode-label {
  width: 280px;
  background: white;
  border: 1px solid #dcdfe6;
  padding: 16px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.label-header {
  text-align: center;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px dashed #dcdfe6;
}

.hospital-name {
  display: block;
  font-size: 14px;
  font-weight: bold;
  color: #303133;
}

.label-title {
  display: block;
  font-size: 12px;
  color: #606266;
  margin-top: 4px;
}

.label-content {
  font-size: 12px;
}

.label-row {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  margin-bottom: 6px;
}

.label-key {
  color: #909399;
  min-width: 50px;
}

.label-value {
  color: #303133;
}

.barcode-value {
  font-weight: bold;
  font-size: 14px;
  color: #409eff;
}

.barcode-area {
  margin-top: 12px;
  padding-top: 8px;
  border-top: 1px dashed #dcdfe6;
  text-align: center;
  min-height: 60px;
}

.barcode-svg {
  max-width: 100%;
}
</style>