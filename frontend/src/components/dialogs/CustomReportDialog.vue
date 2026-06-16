<template>
  <el-dialog :model-value="modelValue" @update:model-value="$emit('update:modelValue', $event)" title="自定义报表" width="1100px" :close-on-click-modal="false" append-to-body>
    <div style="margin-bottom:12px;display:flex;gap:8px;align-items:center;flex-wrap:wrap;">
      <el-date-picker v-model="beginDate" type="date" value-format="YYYY-MM-DD" placeholder="开始日期" style="width:150px" />
      <el-date-picker v-model="endDate" type="date" value-format="YYYY-MM-DD" placeholder="结束日期" style="width:150px" />
      <el-button type="primary" @click="loadReport">查询</el-button>
      <el-button type="success" @click="doExport">导出</el-button>
      <el-radio-group v-model="chartType" size="small" style="margin-left:auto;">
        <el-radio-button label="bar">柱状图</el-radio-button>
        <el-radio-button label="pie">饼图</el-radio-button>
        <el-radio-button label="line">折线图</el-radio-button>
      </el-radio-group>
    </div>

    <el-tabs v-model="dimension" @tab-change="onTabChange">
      <el-tab-pane label="按组合" name="combo" />
      <el-tab-pane label="按科室" name="department" />
      <el-tab-pane label="按医生" name="doctor" />
      <el-tab-pane label="按检验员" name="examiner" />
    </el-tabs>

    <div style="display:flex;gap:16px;">
      <div style="flex:2;">
        <el-table :data="reportData" border stripe size="small" max-height="360" @row-dblclick="handleDrillDown">
          <el-table-column prop="name" label="名称" />
          <el-table-column prop="cnt" label="数量" width="100" />
          <el-table-column label="操作" width="80">
            <template #default="{ row }">
              <el-button type="text" size="small" @click.stop="handleDrillDown(row)">明细</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div style="flex:3;min-height:360px;" id="customReportChart"></div>
    </div>

    <div style="margin-top:12px;">
      <el-descriptions title="患者类别统计" :column="4" border size="small" v-if="patientTypeData.length > 0">
        <el-descriptions-item v-for="pt in patientTypeData" :key="pt.brlb" :label="pt.brlb">
          {{ pt.cnt }}人
        </el-descriptions-item>
      </el-descriptions>
    </div>

    <el-dialog v-model="detailVisible" title="明细记录" width="900px" append-to-body>
      <el-table :data="detailData" border stripe size="small" max-height="450">
        <el-table-column prop="brxm" label="姓名" width="80" />
        <el-table-column prop="tmh" label="条码号" width="120" />
        <el-table-column prop="brxb" label="性别" width="50" />
        <el-table-column prop="brlb" label="类别" width="60" />
        <el-table-column prop="ksmc" label="科室" width="100" />
        <el-table-column prop="bgmc" label="报告名称" />
        <el-table-column prop="jyrq" label="检验日期" width="160">
          <template #default="{ row }">{{ row.jyrq?.replace('T', ' ').substring(0, 19) }}</template>
        </el-table-column>
        <el-table-column prop="shrq" label="审核日期" width="160">
          <template #default="{ row }">{{ row.shrq?.replace('T', ' ').substring(0, 19) }}</template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </el-dialog>
</template>

<script setup>
import { ref, watch, nextTick, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { queryCustomReport, fetchCustomReportDetail, fetchPatientTypeWithFee } from '../../api/customReport'
import * as echarts from 'echarts'

const props = defineProps({ modelValue: Boolean })
const emit = defineEmits(['update:modelValue'])

const dimension = ref('combo')
const beginDate = ref('')
const endDate = ref('')
const reportData = ref([])
const chartType = ref('bar')
const detailVisible = ref(false)
const detailData = ref([])
const patientTypeData = ref([])
let chartInstance = null

const doExport = () => {
  if (!beginDate.value || !endDate.value) return ElMessage.warning('请选择日期范围')
  window.open(`/api/custom-report/export?dimension=${dimension.value}&beginDate=${beginDate.value}&endDate=${endDate.value}`, '_blank')
}

const renderChart = () => {
  const dom = document.getElementById('customReportChart')
  if (!dom || reportData.value.length === 0) return
  if (!chartInstance) chartInstance = echarts.init(dom)
  const names = reportData.value.map(r => r.name || '')
  const values = reportData.value.map(r => Number(r.cnt) || 0)
  let option = { title: { text: '统计结果', left: 'center', textStyle: { fontSize: 13 } }, tooltip: {}, grid: { left: 80, right: 20, bottom: 60, top: 40 } }
  if (chartType.value === 'pie') {
    const pieData = reportData.value.slice(0, 10).map(r => ({ name: r.name || '未知', value: Number(r.cnt) || 0 }))
    const otherCnt = reportData.value.slice(10).reduce((s, r) => s + (Number(r.cnt) || 0), 0)
    if (otherCnt > 0) pieData.push({ name: '其他', value: otherCnt })
    option.series = [{ type: 'pie', radius: ['30%', '65%'], label: { fontSize: 10 }, data: pieData }]
  } else if (chartType.value === 'line') {
    option.xAxis = { type: 'category', data: names, axisLabel: { rotate: 30, fontSize: 10 } }
    option.yAxis = { type: 'value' }
    option.series = [{ type: 'line', data: values, smooth: true }]
  } else {
    option.xAxis = { type: 'category', data: names, axisLabel: { rotate: 30, fontSize: 10 } }
    option.yAxis = { type: 'value' }
    option.series = [{ type: 'bar', data: values, itemStyle: { color: '#409eff' } }]
  }
  chartInstance.setOption(option, true)
}

const handleDrillDown = async (row) => {
  if (!beginDate.value || !endDate.value) return ElMessage.warning('请选择日期范围')
  const dim = dimension.value
  try {
    const filter = row.code || row.name
    const { data } = await fetchCustomReportDetail({
      beginDate: beginDate.value,
      endDate: endDate.value,
      dimension: dim,
      filter: filter
    })
    detailData.value = data || []
    detailVisible.value = true
  } catch (e) { ElMessage.error('查询明细失败') }
}

const loadPatientType = async () => {
  if (!beginDate.value || !endDate.value) return
  try {
    const { data } = await fetchPatientTypeWithFee({ beginDate: beginDate.value, endDate: endDate.value })
    patientTypeData.value = data || []
  } catch (e) {}
}

watch(chartType, () => { nextTick(renderChart) })

const onTabChange = () => {
  loadReport()
}

const loadReport = async () => {
  if (!beginDate.value || !endDate.value) {
    reportData.value = []
    return
  }
  try {
    const { data } = await queryCustomReport({
      dimension: dimension.value,
      beginDate: beginDate.value,
      endDate: endDate.value
    })
    reportData.value = data?.data || []
    await nextTick()
    renderChart()
    loadPatientType()
  } catch (e) { ElMessage.error('查询失败') }
}
</script>
