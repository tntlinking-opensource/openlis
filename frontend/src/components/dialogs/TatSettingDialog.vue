<template>
  <el-dialog :model-value="modelValue" @update:model-value="$emit('update:modelValue', $event)" title="项目时间设置(TAT)" width="950px" :close-on-click-modal="false" append-to-body>
    <el-tabs v-model="activeTab">
      <el-tab-pane label="TAT设置" name="settings">
        <div style="margin-bottom:12px;display:flex;gap:8px;align-items:center;">
          <el-select v-model="filterInst" placeholder="按仪器筛选" clearable style="width:150px" @change="loadSettings">
            <el-option v-for="i in instruments" :key="i.sbDjid" :label="i.sbmc" :value="i.sbDjid" />
          </el-select>
          <el-button type="success" size="small" @click="openForm(null)">新增</el-button>
          <el-button size="small" @click="doAutoCalc">自动计算</el-button>
        </div>

        <el-table :data="settings" border stripe size="small" max-height="350">
          <el-table-column prop="sbDjid" label="设备ID" width="70" />
          <el-table-column prop="sbmc" label="仪器名称" width="120" />
          <el-table-column prop="brlb" label="病人类型" width="70">
            <template #default="{row}">{{ {1:'门诊',2:'住院',3:'体检',4:'其他',5:'科研'}[row.brlb] || row.brlb }}</template>
          </el-table-column>
          <el-table-column prop="syqk" label="紧急类型" width="80">
            <template #default="{row}">{{ {0:'普通',1:'常规',2:'急诊',3:'特急',4:'即时'}[row.syqk] || row.syqk }}</template>
          </el-table-column>
          <el-table-column prop="zhmc" label="组合名称" min-width="150" />
          <el-table-column prop="TAT" label="TAT(分)" width="80" />
          <el-table-column label="操作" width="100" fixed="right">
            <template #default="{row}">
              <el-button link type="primary" size="small" @click="openForm(row)">编辑</el-button>
              <el-button link type="danger" size="small" @click="doDelete(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="TAT统计" name="stats">
        <div style="margin-bottom:12px;display:flex;gap:8px;align-items:center;">
          <el-date-picker v-model="statBegin" type="date" value-format="YYYY-MM-DD" style="width:150px" placeholder="开始日期" />
          <el-date-picker v-model="statEnd" type="date" value-format="YYYY-MM-DD" style="width:150px" placeholder="结束日期" />
          <el-button type="primary" @click="loadStats">查询</el-button>
          <el-dropdown style="margin-left:auto;" @command="doTatExport">
            <el-button type="success" size="small">导出<el-icon style="margin-left:4px;"><arrow-down /></el-icon></el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="statistics">统计报表</el-dropdown-item>
                <el-dropdown-item command="overtime">超时样本</el-dropdown-item>
                <el-dropdown-item command="trend">趋势数据</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>

        <div v-if="tatStats.percentiles" style="display:flex;gap:12px;margin-bottom:16px;">
          <el-card v-for="label in ['p50','p90','p95','p99']" :key="label" shadow="hover" style="flex:1;text-align:center;">
            <div style="font-size:12px;color:#909399;">{{ {p50:'P50(中位数)',p90:'P90',p95:'P95',p99:'P99'}[label] }}</div>
            <div style="font-size:22px;font-weight:bold;color:#409eff;">{{ tatStats.percentiles[label] || '-' }}</div>
            <div style="font-size:11px;color:#c0c4cc;">分钟</div>
          </el-card>
        </div>

        <div style="display:flex;gap:16px;margin-bottom:16px;">
          <div style="flex:1;">
            <strong>分阶段TAT</strong>
            <div id="tatPhaseChart" style="height:220px;margin-top:8px;"></div>
          </div>
          <div style="flex:1;">
            <strong>TAT趋势(实际 vs 目标)</strong>
            <div id="tatTrendChart" style="height:220px;margin-top:8px;"></div>
          </div>
        </div>

        <div style="margin-bottom:16px;">
          <strong>平均TAT(按组合)</strong>
          <el-table :data="tatStats.avgTat || []" border stripe size="small" style="margin-top:8px;">
            <el-table-column prop="zhmc" label="组合" min-width="150" />
            <el-table-column prop="avgMin" label="平均(分)" width="100" />
            <el-table-column prop="cnt" label="样本数" width="80" />
          </el-table>
        </div>

        <div style="margin-bottom:16px;">
          <strong>超时样本(Top 100)</strong>
          <el-table :data="tatStats.overtime || []" border stripe size="small" max-height="200" style="margin-top:8px;">
            <el-table-column prop="brxm" label="姓名" width="80" />
            <el-table-column prop="syh" label="样本号" width="100" />
            <el-table-column prop="zhmc" label="组合" width="120" />
            <el-table-column prop="actualMin" label="实际(分)" width="90" />
            <el-table-column prop="TAT" label="标准(分)" width="90" />
            <el-table-column prop="isOver" label="超时" width="60">
              <template #default="{row}"><el-tag :type="row.isOver ? 'danger' : 'success'" size="small">{{ row.isOver ? '是' : '否' }}</el-tag></template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>
    </el-tabs>

    <el-dialog v-model="formVisible" :title="form._edit ? '编辑TAT' : '新增TAT'" width="500px" append-to-body>
      <el-form :model="form" label-width="90px" size="small">
        <el-form-item label="仪器" required>
          <el-select v-model="form.sbDjid" style="width:100%" placeholder="选择仪器">
            <el-option v-for="i in instruments" :key="i.sbDjid" :label="i.sbmc" :value="i.sbDjid" />
          </el-select>
        </el-form-item>
        <el-form-item label="病人类型" required>
          <el-select v-model="form.brlb" style="width:100%">
            <el-option :value="1" label="门诊" /><el-option :value="2" label="住院" />
            <el-option :value="3" label="体检" /><el-option :value="4" label="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="紧急类型" required>
          <el-select v-model="form.syqk" style="width:100%">
            <el-option :value="0" label="普通" /><el-option :value="1" label="常规" />
            <el-option :value="2" label="急诊" /><el-option :value="3" label="特急" />
            <el-option :value="4" label="即时" />
          </el-select>
        </el-form-item>
        <el-form-item label="组合" required>
          <el-select v-model="form.zhid" filterable style="width:100%" placeholder="搜索组合" @change="onComboChange">
            <el-option v-for="c in comboOptions" :key="c.zhid" :label="c.zhmc" :value="c.zhid" />
          </el-select>
        </el-form-item>
        <el-form-item label="TAT(分钟)" required><el-input-number v-model="form.TAT" :min="1" style="width:100%" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </el-dialog>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowDown } from '@element-plus/icons-vue'
import { fetchTatSettings, saveTatSetting, deleteTatSetting, autoCalculateTat, fetchTatStatistics, fetchTatOvertime, fetchTatTrend } from '../../api/tat'
import { fetchCombos } from '../../api/combo'
import { fetchInstrumentItemTree } from '../../api/instrumentItem'
import * as echarts from 'echarts'
import axios from 'axios'

const props = defineProps({ modelValue: Boolean })
const emit = defineEmits(['update:modelValue'])

const activeTab = ref('settings')
const settings = ref([])
const instruments = ref([])
const comboOptions = ref([])
const filterInst = ref('')
const formVisible = ref(false)
const form = ref({})
const statBegin = ref(new Date(Date.now() - 365 * 86400000).toISOString().slice(0, 10))
const statEnd = ref(new Date().toISOString().slice(0, 10))
const tatStats = ref({})
let phaseChart = null, trendChart = null

const loadSettings = async () => {
  try {
    const { data } = await fetchTatSettings()
    let result = Array.isArray(data) ? data : []
    if (filterInst.value) {
      result = result.filter(item => item.sbDjid === filterInst.value)
    }
    settings.value = result
  } catch (e) {}
}

const loadInstruments = async () => {
  try {
    const { data } = await fetchInstrumentItemTree()
    instruments.value = Array.isArray(data) ? data.map(i => ({ sbDjid: i.sbDjid, sbmc: i.label })) : []
  } catch (e) {}
}

const loadCombos = async (keyword = '') => {
  try {
    const { data } = await fetchCombos({ keyword })
    comboOptions.value = Array.isArray(data) ? data : []
  } catch (e) {}
}

const openForm = (row) => {
  if (row) {
    form.value = { ...row, _edit: true }
  } else {
    form.value = { sbDjid: '', brlb: 1, syqk: 1, zhid: '', zhmc: '', TAT: 60, _edit: false }
  }
  formVisible.value = true
}

const onComboChange = (zhid) => {
  const combo = comboOptions.value.find(c => c.zhid === zhid)
  if (combo) {
    form.value.zhmc = combo.zhmc
  }
}

const handleSave = async () => {
  if (!form.value.sbDjid) { ElMessage.warning('请选择仪器'); return }
  if (!form.value.zhid) { ElMessage.warning('请选择组合'); return }
  if (!form.value.TAT) { ElMessage.warning('请输入TAT时间'); return }
  try {
    const { data } = await saveTatSetting(form.value)
    if (data.success) { ElMessage.success('保存成功'); formVisible.value = false; loadSettings() }
    else ElMessage.error(data.message)
  } catch (e) { ElMessage.error('保存失败') }
}

const doDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定删除此TAT设置？', '提示', { type: 'warning' })
    const { data } = await deleteTatSetting(row.sbDjid, row.brlb, row.syqk, row.zhid)
    if (data.success) { ElMessage.success('删除成功'); loadSettings() }
    else ElMessage.error(data.message)
  } catch (e) {}
}

const doAutoCalc = async () => {
  try {
    const { data } = await autoCalculateTat()
    if (data.success) { ElMessage.success('自动计算完成'); loadSettings() }
    else ElMessage.error(data.message || '计算失败')
  } catch (e) { ElMessage.error('计算失败') }
}

const doTatExport = (type) => {
  if (!statBegin.value || !statEnd.value) return ElMessage.warning('请选择日期范围')
  window.open(`/api/tat/export?type=${type}&beginDate=${statBegin.value}&endDate=${statEnd.value}`, '_blank')
}

const loadStats = async () => {
  if (!statBegin.value || !statEnd.value) { ElMessage.warning('请选择日期范围'); return }
  const params = { beginDate: statBegin.value, endDate: statEnd.value }
  try {
    const [stat, over, trend, phase] = await Promise.all([
      fetchTatStatistics(params),
      fetchTatOvertime(params),
      fetchTatTrend(params),
      axios.get('/api/tat/phase-stats', { params })
    ])
    const statData = stat.data?.data || stat.data || {}
    tatStats.value = {
      avgTat: Array.isArray(statData.avgTat) ? statData.avgTat : [],
      overtime: Array.isArray(over.data?.data) ? over.data.data : Array.isArray(over.data) ? over.data : [],
      trend: Array.isArray(trend.data) ? trend.data : [],
      percentiles: statData.percentiles || null,
      phases: Array.isArray(phase.data) ? phase.data : []
    }
    await nextTick()
    renderCharts()
  } catch (e) { console.error(e) }
}

const renderCharts = () => {
  const phases = tatStats.value.phases || []
  const phaseDom = document.getElementById('tatPhaseChart')
  if (phaseDom && phases.length > 0) {
    if (!phaseChart) phaseChart = echarts.init(phaseDom)
    phaseChart.setOption({
      tooltip: {},
      xAxis: { type: 'category', data: phases.map(r => r.phase) },
      yAxis: { type: 'value', name: '分钟' },
      series: [{ type: 'bar', data: phases.map(r => ({ value: Number(r.avgMin) || 0, cnt: r.cnt })), itemStyle: { color: '#409eff' }, label: { show: true, position: 'top', formatter: p => p.value + '分' } }]
    })
  }

  const trend = tatStats.value.trend || []
  const trendDom = document.getElementById('tatTrendChart')
  if (trendDom && trend.length > 0) {
    if (!trendChart) trendChart = echarts.init(trendDom)
    const hasTarget = trend.some(r => r.targetMin != null)
    const series = [
      { name: '实际TAT', type: 'line', data: trend.map(r => Number(r.avgMin || 0).toFixed(1)), smooth: true, itemStyle: { color: '#409eff' } }
    ]
    if (hasTarget) {
      series.push({ name: '目标TAT', type: 'line', data: trend.map(r => Number(r.targetMin || 0).toFixed(1)), lineStyle: { type: 'dashed', color: '#f56c6c' }, itemStyle: { color: '#f56c6c' } })
    }
    trendChart.setOption({
      tooltip: { trigger: 'axis' },
      legend: hasTarget ? { data: ['实际TAT', '目标TAT'] } : undefined,
      xAxis: { type: 'category', data: trend.map(r => r.date), axisLabel: { fontSize: 10, rotate: 30 } },
      yAxis: { type: 'value', name: '分钟' },
      series
    })
  }
}

onMounted(() => { loadSettings(); loadInstruments(); loadCombos() })
</script>
