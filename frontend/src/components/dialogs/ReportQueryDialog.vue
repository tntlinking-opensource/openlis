<template>
  <el-dialog :model-value="modelValue" @update:model-value="$emit('update:modelValue', $event)" title="报告查询" width="1100px" :close-on-click-modal="false" append-to-body>
    <!-- 基础筛选 -->
    <div style="margin-bottom:12px;display:flex;gap:8px;align-items:center;flex-wrap:wrap;">
      <el-input v-model="query.brxm" placeholder="姓名" clearable style="width:100px" />
      <el-input v-model="query.brxxTmh" placeholder="条码号" clearable style="width:120px" />
      <el-input v-model="query.syh" placeholder="样本号" clearable style="width:100px" />
      <el-select v-model="query.sbDjid" placeholder="仪器" clearable style="width:140px">
        <el-option v-for="i in filterOpts.instruments" :key="i.sb_djid" :label="i.sbmc" :value="i.sb_djid" />
      </el-select>
      <el-select v-model="query.ksdm" placeholder="科室" clearable style="width:120px">
        <el-option v-for="d in filterOpts.departments" :key="d.ksdm" :label="d.ksmc" :value="d.ksdm" />
      </el-select>
      <el-date-picker v-model="query.beginDate" type="date" value-format="YYYY-MM-DD" placeholder="开始" style="width:140px" />
      <el-date-picker v-model="query.endDate" type="date" value-format="YYYY-MM-DD" placeholder="结束" style="width:140px" />
      <el-button type="primary" @click="loadReports">查询</el-button>
      <el-button @click="showAdvanced = !showAdvanced">{{ showAdvanced ? '收起' : '高级' }}</el-button>
    </div>

    <!-- 高级筛选面板 -->
    <div v-if="showAdvanced" class="advanced-filter">
      <div class="filter-row">
        <el-select v-model="query.ybzt" placeholder="报告状态" clearable style="width:120px">
          <el-option label="登记" :value="0" />
          <el-option label="未审核" :value="1" />
          <el-option label="已审核" :value="2" />
          <el-option label="已打印" :value="3" />
          <el-option label="已作废" :value="-1" />
        </el-select>
        <el-select v-model="query.brlx" placeholder="患者类型" clearable style="width:120px">
          <el-option label="门诊" value="MZ" />
          <el-option label="住院" value="ZY" />
          <el-option label="体检" value="TJ" />
        </el-select>
        <el-input v-model="query.zd" placeholder="诊断关键词" clearable style="width:140px" />
        <el-checkbox v-model="query.abnormalOnly">仅显示异常结果</el-checkbox>
      </div>
    </div>

    <!-- 批量操作栏 -->
    <div class="batch-actions">
      <el-checkbox v-model="allSelected" @change="toggleSelectAll">全选</el-checkbox>
      <span class="selected-count">已选择 {{ selectedRows.length }} 项</span>
      <el-button type="primary" size="small" @click="batchPrint" :disabled="selectedRows.length === 0">批量打印</el-button>
      <el-button size="small" @click="batchExport" :disabled="selectedRows.length === 0">批量导出</el-button>
    </div>

    <!-- 报告列表 -->
    <el-table 
      ref="reportTableRef"
      :data="paginatedReports" 
      border 
      stripe 
      size="small" 
      max-height="300" 
      highlight-current-row 
      @current-change="onSelectReport"
      @selection-change="onSelectionChange"
    >
      <el-table-column type="selection" width="40" />
      <el-table-column prop="brxm" label="姓名" width="80" />
      <el-table-column prop="brxb" label="性别" width="50">
        <template #default="{row}">{{ row.brxb === 1 ? '男' : row.brxb === 2 ? '女' : '' }}</template>
      </el-table-column>
      <el-table-column prop="brnl" label="年龄" width="60" />
      <el-table-column prop="brxx_tmh" label="条码号" width="120" />
      <el-table-column prop="syh" label="样本号" width="100" />
      <el-table-column prop="ksmc" label="科室" width="100" />
      <el-table-column prop="ybzt" label="状态" width="70">
        <template #default="{row}">
          <el-tag :type="getStatusType(row.ybzt)" size="small">
            {{ getStatusText(row.ybzt) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="jyrq" label="检验日期" width="140" />
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{row}">
          <el-button link type="primary" size="small" @click="doPrint(row)">打印</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination">
      <span class="page-info">共 {{ reports.length }} 条</span>
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="reports.length"
        layout="sizes, prev, pager, next"
        small
      />
    </div>

    <!-- 结果详情 -->
    <div v-if="selectedReport" style="margin-top:12px;">
      <strong>检验结果 - {{ selectedReport.brxm }}</strong>
      <el-table :data="results" border stripe size="small" max-height="200" style="margin-top:8px;">
        <el-table-column prop="name" label="项目名称" width="150" />
        <el-table-column prop="code" label="代码" width="80" />
        <el-table-column prop="result" label="结果" width="100">
          <template #default="{row}">
            <span :class="getResultClass(row)">{{ row.result }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="unit" label="单位" width="70" />
        <el-table-column prop="highLowFlag" label="标志" width="60">
          <template #default="{row}">
            <span :class="getFlagClass(row.highLowFlag)">{{ row.highLowFlag || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="refRange" label="参考值" width="120" />
      </el-table>
    </div>
  </el-dialog>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { queryReports, fetchReportResults, printReport, fetchReportFilterOptions } from '../../api/report'
import { fetchReportHtml, batchPrint as batchPrintSamples } from '../../api/sample'
import { getStatusText, getStatusType } from '../../utils/sampleStatus'

const props = defineProps({ modelValue: Boolean })
const emit = defineEmits(['update:modelValue'])

const query = ref({ 
  beginDate: '', 
  endDate: '', 
  brxm: '', 
  brxxTmh: '', 
  syh: '', 
  ksdm: '', 
  sbDjid: null,
  ybzt: null,
  brlx: '',
  zd: '',
  abnormalOnly: false
})
const reports = ref([])
const selectedReport = ref(null)
const results = ref([])
const filterOpts = ref({ instruments: [], departments: [], doctors: [] })
const showAdvanced = ref(false)
const selectedRows = ref([])
const allSelected = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const reportTableRef = ref(null)

const paginatedReports = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  return reports.value.slice(start, start + pageSize.value)
})

const loadFilterOpts = async () => {
  try {
    const { data } = await fetchReportFilterOptions()
    filterOpts.value = data || { instruments: [], departments: [], doctors: [] }
  } catch (e) {}
}

const loadReports = async () => {
  try {
    const { data } = await queryReports(query.value)
    reports.value = data?.data || []
    selectedReport.value = null
    results.value = []
    currentPage.value = 1
  } catch (e) { ElMessage.error('查询失败') }
}

const onSelectReport = async (row) => {
  selectedReport.value = row
  if (row) {
    try {
      const { data } = await fetchReportResults(row.brxx_id)
      results.value = Array.isArray(data) ? data : []
    } catch (e) { results.value = [] }
  } else { results.value = [] }
}

const onSelectionChange = (selection) => {
  selectedRows.value = selection
  allSelected.value = selection.length === reports.value.length && reports.value.length > 0
}

const toggleSelectAll = (val) => {
  if (val) {
    reportTableRef.value?.toggleAllSelection()
  } else {
    reportTableRef.value?.clearSelection()
  }
}

const getResultClass = (row) => {
  if (row.highLowFlag === 'H' || row.highLowFlag === '↑') return 'result-high'
  if (row.highLowFlag === 'L' || row.highLowFlag === '↓') return 'result-low'
  return ''
}

const getFlagClass = (flag) => {
  if (flag === 'H' || flag === '↑') return 'flag-high'
  if (flag === 'L' || flag === '↓') return 'flag-low'
  return ''
}

const doPrint = async (row) => {
  try {
    const { data } = await printReport(row.brxx_id, { czydm: JSON.parse(localStorage.getItem('user') || '{}').czydm || 'admin' })
    if (data.success) {
      ElMessage.success(data.message || '打印成功')
      fetchReportHtml(row.brxx_id)
        .then((r) => {
          const w = window.open('', '_blank')
          if (w) {
            w.document.open()
            w.document.write(r.data || '')
            w.document.close()
            w.focus()
          } else {
            ElMessage.warning('浏览器拦截了弹窗，请允许弹窗后再打印')
          }
        })
        .catch((e) => ElMessage.error(e.response?.data?.message || e.message || '获取报告内容失败'))
    } else {
      ElMessage.error(data.message)
    }
  } catch (e) { ElMessage.error('打印失败') }
}

const batchPrint = async () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请选择要打印的报告')
    return
  }
  try {
    const { data } = await batchPrintSamples({ 
      brxxIds: selectedRows.value.map(r => r.brxx_id),
      czydm: JSON.parse(localStorage.getItem('user') || '{}').czydm || 'admin'
    })
    if (data.success) {
      ElMessage.success(data.message || `批量打印完成`)
      const results = data.results || []
      for (const r of results) {
        if (r.success && r.reportHtml) {
          const w = window.open('', '_blank')
          if (w) {
            w.document.open()
            w.document.write(r.reportHtml)
            w.document.close()
            w.focus()
          }
        }
      }
    } else {
      ElMessage.error(data.message)
    }
  } catch (e) { ElMessage.error('批量打印失败') }
}

const batchExport = () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请选择要导出的报告')
    return
  }
  ElMessage.info(`已选择 ${selectedRows.value.length} 份报告开始导出...`)
}

onMounted(() => { loadFilterOpts() })
</script>

<style scoped>
.advanced-filter {
  margin-bottom: 12px;
  padding: 12px;
  background: #f5f7fa;
  border-radius: 4px;
}

.filter-row {
  display: flex;
  gap: 12px;
  align-items: center;
  flex-wrap: wrap;
}

.batch-actions {
  display: flex;
  gap: 12px;
  align-items: center;
  margin-bottom: 12px;
  padding: 8px 12px;
  background: #f0f9eb;
  border-radius: 4px;
}

.selected-count {
  color: #606266;
  font-size: 14px;
}

.pagination {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 12px;
}

.page-info {
  color: #606266;
  font-size: 14px;
}

.result-high {
  color: #f56c6c;
  font-weight: bold;
}

.result-low {
  color: #409eff;
  font-weight: bold;
}

.flag-high {
  color: #f56c6c;
  font-weight: bold;
}

.flag-low {
  color: #409eff;
  font-weight: bold;
}
</style>
