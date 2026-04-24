<template>
  <div class="query-statistics-container">
    <!-- 查询条件区 -->
    <el-card class="query-condition-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span>查询条件</span>
          <el-button size="small" type="primary" @click="handleClearFilters">清空筛选条件</el-button>
        </div>
      </template>
      <el-form :inline="true" :model="queryForm" class="query-form">
        <el-form-item label="日期范围">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="病人类型">
          <el-select v-model="queryForm.patientType" placeholder="请选择" clearable>
            <el-option v-for="pt in patientTypeOptions" :key="pt" :label="pt" :value="pt" />
          </el-select>
        </el-form-item>
        <el-form-item label="科室">
          <el-input v-model="queryForm.department" placeholder="请输入科室" clearable style="width: 150px" />
        </el-form-item>
        <el-form-item label="检验项目">
          <el-input v-model="queryForm.testItem" placeholder="请输入项目名称" clearable style="width: 150px" />
        </el-form-item>
        <el-form-item label="样本状态">
          <el-select v-model="queryForm.status" placeholder="请选择" clearable>
            <el-option v-for="s in statusOptions" :key="s" :label="s" :value="s" />
          </el-select>
        </el-form-item>
        <el-form-item label="仪器">
          <el-input v-model="queryForm.instrument" placeholder="请输入仪器名称" clearable style="width: 150px" />
        </el-form-item>
        <el-form-item label="检验医生">
          <el-input v-model="queryForm.examiner" placeholder="请输入医生姓名" clearable style="width: 150px" />
        </el-form-item>
        <el-form-item label="审核医生">
          <el-input v-model="queryForm.auditor" placeholder="请输入审核医生" clearable style="width: 150px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="success" @click="handlePrint">打印</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 病人样本明细列表区 -->
    <el-card class="detail-list-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span>病人样本明细</span>
          <span class="result-count">共 {{ detailTotal }} 条</span>
        </div>
      </template>
      <el-table
        :data="detailList"
        border
        stripe
        height="350"
        v-loading="detailLoading"
      >
        <el-table-column prop="barcode" label="条码号" width="140" />
        <el-table-column prop="receiveTime" label="核收时间" width="160">
          <template #default="{ row }">
            {{ formatDate(row.receiveTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="instrument" label="设备" width="120" />
        <el-table-column prop="comboName" label="组合名称" width="120" />
        <el-table-column prop="sampleTypeName" label="标本种类" width="100" />
        <el-table-column prop="patientName" label="病人姓名" width="100" />
        <el-table-column prop="sex" label="性别" width="60" />
        <el-table-column prop="age" label="年龄" width="60" />
        <el-table-column prop="department" label="科室" width="100" />
        <el-table-column prop="bedNo" label="床号" width="80" />
        <el-table-column prop="patientCategory" label="病人类别" width="100" />
        <el-table-column prop="workGroup" label="工作组" width="100" />
        <el-table-column prop="receiver" label="核收人" width="100" />
      </el-table>
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="detailPage"
          v-model:page-size="detailPageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="detailTotal"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadDetailList"
          @current-change="loadDetailList"
        />
      </div>
    </el-card>

    <!-- 统计图表区 -->
    <el-card class="statistics-chart-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span>统计图表</span>
        </div>
      </template>
      <div class="charts-container">
        <div class="chart-item">
          <h4>病人类别统计</h4>
          <div ref="patientTypeChartRef" class="chart"></div>
        </div>
        <div class="chart-item">
          <h4>样本状态统计</h4>
          <div ref="statusChartRef" class="chart"></div>
        </div>
        <div class="chart-item chart-wide">
          <h4>日期趋势统计</h4>
          <div ref="dateTrendChartRef" class="chart"></div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

// 动态选项数据
const patientTypeOptions = ref([])
const statusOptions = ref([])

// 查询表单
const queryForm = reactive({
  patientType: '',
  department: '',
  testItem: '',
  status: '',
  instrument: '',
  examiner: '',
  auditor: ''
})

// 日期范围
const dateRange = ref([])

// 明细列表数据
const detailList = ref([])
const detailLoading = ref(false)
const detailTotal = ref(0)
const detailPage = ref(1)
const detailPageSize = ref(20)

// 图表引用
const patientTypeChartRef = ref(null)
const statusChartRef = ref(null)
const dateTrendChartRef = ref(null)

// 图表实例
let patientTypeChart = null
let statusChart = null
let dateTrendChart = null

// 加载动态选项
const loadOptions = async () => {
  try {
    const res = await axios.get('/api/query/options')
    const options = res.data || {}
    
    patientTypeOptions.value = options.patientTypes || []
    statusOptions.value = options.statuses || []
  } catch (e) {
    console.error('加载选项失败:', e)
    // 使用默认值
    patientTypeOptions.value = ['门诊病人', '住院病人', '体检人员', '其他病人', '科研人员']
    statusOptions.value = ['新建', '已保存', '已检验', '已审核', '已打印', '作废']
  }
}

// 加载明细列表
const loadDetailList = async () => {
  detailLoading.value = true
  try {
    const params = {
      ...queryForm,
      startDate: dateRange.value?.[0] || null,
      endDate: dateRange.value?.[1] || null,
      page: detailPage.value,
      pageSize: detailPageSize.value
    }
    
    // 移除空值
    Object.keys(params).forEach(key => {
      if (params[key] === '' || params[key] === null) {
        delete params[key]
      }
    })
    
    const res = await axios.get('/api/query/sample/list', { params })
    detailList.value = res.data?.list || []
    detailTotal.value = res.data?.total || 0
  } catch (e) {
    console.error('加载明细列表失败:', e)
    detailList.value = []
    detailTotal.value = 0
  } finally {
    detailLoading.value = false
  }
}

// 查询
const handleQuery = async () => {
  detailPage.value = 1
  await loadDetailList()
  await loadStatistics()
}

// 重置
const handleReset = () => {
  Object.keys(queryForm).forEach(key => {
    queryForm[key] = ''
  })
  dateRange.value = []
  detailPage.value = 1
  handleQuery()
}

// 清空筛选条件
const handleClearFilters = () => {
  Object.keys(queryForm).forEach(key => {
    queryForm[key] = ''
  })
  dateRange.value = []
  detailPage.value = 1
  handleQuery()
}

// 打印
const handlePrint = () => {
  window.print()
}

// 加载统计数据
const loadStatistics = async () => {
  try {
    const params = {
      ...queryForm,
      startDate: dateRange.value?.[0] || null,
      endDate: dateRange.value?.[1] || null
    }
    
    // 移除空值
    Object.keys(params).forEach(key => {
      if (params[key] === '' || params[key] === null) {
        delete params[key]
      }
    })
    
    const res = await axios.get('/api/query/sample/statistics', { params })
    const statistics = res.data
    
    // 更新图表
    updateCharts(statistics)
  } catch (e) {
    console.error('加载统计数据失败:', e)
  }
}

// 更新图表
const updateCharts = (statistics) => {
  // 病人类别饼图
  if (patientTypeChart && statistics.byPatientType) {
    patientTypeChart.setOption({
      series: [{
        data: statistics.byPatientType
      }]
    })
  }
  
  // 样本状态饼图
  if (statusChart && statistics.byStatus) {
    statusChart.setOption({
      series: [{
        data: statistics.byStatus
      }]
    })
  }
  
  // 日期趋势柱状图
  if (dateTrendChart && statistics.byDate) {
    dateTrendChart.setOption({
      xAxis: {
        data: statistics.byDate.map(item => item.name || item.date)
      },
      series: [{
        data: statistics.byDate.map(item => item.value || item.count)
      }]
    })
  }
}

// 格式化日期
const formatDate = (date) => {
  if (!date) return ''
  const d = new Date(date)
  return d.toLocaleString('zh-CN')
}

// 初始化图表
const initCharts = async () => {
  // 动态导入echarts
  const echarts = await import('echarts')
  
  // 病人类别饼图
  if (patientTypeChartRef.value) {
    patientTypeChart = echarts.init(patientTypeChartRef.value)
    patientTypeChart.setOption({
      tooltip: { trigger: 'item' },
      legend: { bottom: '0%' },
      series: [{
        name: '病人类别',
        type: 'pie',
        radius: '50%',
        data: []
      }]
    })
  }
  
  // 样本状态饼图
  if (statusChartRef.value) {
    statusChart = echarts.init(statusChartRef.value)
    statusChart.setOption({
      tooltip: { trigger: 'item' },
      legend: { bottom: '0%' },
      series: [{
        name: '样本状态',
        type: 'pie',
        radius: '50%',
        data: []
      }]
    })
  }
  
  // 日期趋势柱状图
  if (dateTrendChartRef.value) {
    dateTrendChart = echarts.init(dateTrendChartRef.value)
    dateTrendChart.setOption({
      tooltip: { trigger: 'axis' },
      xAxis: {
        type: 'category',
        data: []
      },
      yAxis: {
        type: 'value'
      },
      series: [{
        name: '样本数',
        type: 'bar',
        data: [],
        itemStyle: { color: '#1890FF' }
      }]
    })
  }
}

onMounted(async () => {
  // 默认查询当天数据
  const today = new Date()
  const yesterday = new Date(today)
  yesterday.setDate(yesterday.getDate() - 7)
  dateRange.value = [
    yesterday.toISOString().split('T')[0],
    today.toISOString().split('T')[0]
  ]
  
  // 先加载选项
  await loadOptions()
  
  nextTick(() => {
    initCharts()
    handleQuery()
  })
})
</script>

<style scoped>
.query-statistics-container {
  padding: 20px;
  height: 100%;
  overflow-y: auto;
  box-sizing: border-box;
}

.query-condition-card {
  margin-bottom: 20px;
  flex-shrink: 0;
}

.query-form {
  display: flex;
  flex-wrap: wrap;
}

.detail-list-card {
  margin-bottom: 20px;
  flex-shrink: 0;
}

.statistics-chart-card {
  margin-bottom: 20px;
  flex-shrink: 0;
}

.charts-container {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

.chart-item {
  width: 400px;
  min-height: 300px;
}

.chart-item h4 {
  text-align: center;
  margin-bottom: 10px;
  color: #303133;
}

.chart-item.chart-wide {
  width: 100%;
}

.chart {
  width: 100%;
  height: 280px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 10px;
}

.card-header span {
  font-weight: bold;
}

.result-count {
  color: #409EFF;
  font-weight: normal;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

/* 打印样式 */
@media print {
  .query-condition-card,
  .statistics-chart-card {
    display: none;
  }
  
  .detail-list-card {
    display: block;
  }
}
</style>
