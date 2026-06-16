<template>
  <el-dialog :model-value="modelValue" @update:model-value="$emit('update:modelValue', $event)" title="危急值管理" width="1100px" :close-on-click-modal="false" append-to-body>
    <el-tabs v-model="activeTab">
      <el-tab-pane label="危急值列表" name="list">
        <div style="margin-bottom:12px;display:flex;gap:8px;align-items:center;">
          <el-date-picker v-model="beginDate" type="date" value-format="YYYY-MM-DD" placeholder="开始日期" style="width:150px" />
          <el-date-picker v-model="endDate" type="date" value-format="YYYY-MM-DD" placeholder="结束日期" style="width:150px" />
          <el-button type="primary" @click="loadList">查询</el-button>
          <el-button type="success" @click="showAddForm = true">新增危急值</el-button>
          <el-button type="warning" @click="doBatchProcess" :disabled="selectedIds.length === 0">
            批量处理({{ selectedIds.length }})
          </el-button>
          <el-button type="info" @click="toggleVoice">
            {{ voiceEnabled ? '关闭语音' : '开启语音' }}
          </el-button>
        </div>

        <el-table :data="list" border stripe size="small" max-height="300"
          @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="40" />
          <el-table-column prop="brxm" label="姓名" width="70" />
          <el-table-column prop="brxb" label="性别" width="45">
            <template #default="{row}">{{ row.brxb === 1 ? '男' : row.brxb === 2 ? '女' : '' }}</template>
          </el-table-column>
          <el-table-column prop="xmzwmc" label="项目" width="110" />
          <el-table-column prop="critical_value" label="危急值" width="120" />
          <el-table-column prop="ksmc" label="科室" width="90" />
          <el-table-column prop="add_date" label="上报时间" width="140" />
          <el-table-column prop="add_oper_name" label="上报人" width="70" />
          <el-table-column label="状态" width="70">
            <template #default="{row}">
              <el-tag :type="row.process_date ? 'success' : 'danger'" size="small">
                {{ row.process_date ? '已处理' : '未处理' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120">
            <template #default="{row}">
              <el-button v-if="!row.process_date" link type="primary" size="small" @click="doProcessOne(row)">处理</el-button>
              <el-button link type="danger" size="small" @click="doCancel(row)">取消</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="危急值语音提醒" name="voice">
        <div style="margin-bottom:12px;display:flex;gap:8px;align-items:center;">
          <el-button type="primary" @click="speakAll">播报全部未处理</el-button>
          <el-button @click="stopSpeaking">停止播报</el-button>
          <el-switch v-model="autoVoice" active-text="自动播报(30s)" style="margin-left:16px;" />
          <span style="margin-left:auto;color:#909399;font-size:12px;">
            未处理危急值: {{ unprocessedList.length }} 条
          </span>
        </div>
        <el-table :data="unprocessedList" border stripe size="small" max-height="350">
          <el-table-column prop="brxm" label="姓名" width="70" />
          <el-table-column prop="xmzwmc" label="项目" width="120" />
          <el-table-column prop="critical_value" label="危急值" />
          <el-table-column prop="ksmc" label="科室" width="100" />
          <el-table-column prop="add_date" label="上报时间" width="150" />
          <el-table-column label="操作" width="100">
            <template #default="{row}">
              <el-button link type="primary" size="small" @click="speakOne(row)">播报</el-button>
              <el-button link type="success" size="small" @click="doProcessOne(row)">处理</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="危急值统计" name="stat">
        <div style="margin-bottom:12px;display:flex;gap:8px;align-items:center;">
          <el-date-picker v-model="statBegin" type="date" value-format="YYYY-MM-DD" style="width:150px" />
          <el-date-picker v-model="statEnd" type="date" value-format="YYYY-MM-DD" style="width:150px" />
          <el-button type="primary" @click="loadStat">查询</el-button>
          <el-button type="success" @click="doExport">导出</el-button>
        </div>

        <div style="display:flex;gap:16px;margin-bottom:16px;">
          <el-card shadow="hover" style="flex:1;text-align:center;">
            <el-statistic title="危急值总数" :value="statData.total || 0" />
          </el-card>
          <el-card shadow="hover" style="flex:1;text-align:center;">
            <div style="height:220px;" id="cvDeptPie"></div>
          </el-card>
          <el-card shadow="hover" style="flex:1;text-align:center;">
            <div style="height:220px;" id="cvItemPie"></div>
          </el-card>
        </div>

        <div style="display:grid;grid-template-columns:1fr 1fr;gap:12px;margin-bottom:12px;">
          <el-table :data="statData.byDepartment || []" border stripe size="small" max-height="200">
            <el-table-column prop="ksmc" label="科室" />
            <el-table-column prop="cnt" label="数量" width="80" />
          </el-table>
          <el-table :data="statData.byOperator || []" border stripe size="small" max-height="200">
            <el-table-column prop="czyxm" label="操作员" />
            <el-table-column prop="cnt" label="数量" width="80" />
          </el-table>
        </div>
        <div style="display:grid;grid-template-columns:1fr 1fr;gap:12px;margin-bottom:12px;">
          <el-table :data="statData.byItem || []" border stripe size="small" max-height="200">
            <el-table-column prop="xmmc" label="检验项目" />
            <el-table-column prop="cnt" label="数量" width="80" />
          </el-table>
          <el-table :data="statData.byUrgency || []" border stripe size="small" max-height="200">
            <el-table-column prop="syqk" label="紧急程度" />
            <el-table-column prop="cnt" label="数量" width="80" />
          </el-table>
        </div>

        <el-card shadow="hover" v-if="(statData.byMonth || []).length > 0">
          <div style="height:250px;" id="cvMonthTrend"></div>
        </el-card>
      </el-tab-pane>
    </el-tabs>

    <el-dialog v-model="showAddForm" title="新增危急值" width="550px" append-to-body>
      <el-form :model="addForm" label-width="80px" size="small">
        <el-form-item label="报告ID" required>
          <div style="display:flex;gap:8px;">
            <el-input v-model="addForm.reportId" type="number" placeholder="输入报告ID(brxx_id)" style="flex:1" />
            <el-button size="small" @click="loadPatientPreview">查询患者</el-button>
          </div>
        </el-form-item>
        <el-descriptions v-if="patientPreview" :column="2" border size="small" style="margin-bottom:12px;">
          <el-descriptions-item label="姓名">{{ patientPreview.brxm }}</el-descriptions-item>
          <el-descriptions-item label="性别">{{ patientPreview.brxb == 1 ? '男' : patientPreview.brxb == 2 ? '女' : '' }}</el-descriptions-item>
          <el-descriptions-item label="科室">{{ patientPreview.ksmc || patientPreview.ksdm }}</el-descriptions-item>
          <el-descriptions-item label="条码">{{ patientPreview.brtmh }}</el-descriptions-item>
        </el-descriptions>
        <el-alert v-if="patientNotFound" type="error" :closable="false" style="margin-bottom:12px;" description="未找到该患者记录，请确认报告ID" />
        <el-form-item label="项目" required>
          <el-select v-model="addForm.xmid" filterable placeholder="选择检验项目" style="width:100%">
            <el-option v-for="p in allProjects" :key="p.xmid" :label="p.xmzwmc" :value="p.xmid" />
          </el-select>
        </el-form-item>
        <el-form-item label="危急值" required>
          <el-input v-model="addForm.criticalValue" type="textarea" :rows="2" placeholder="输入危急值内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddForm = false">取消</el-button>
        <el-button type="primary" @click="doAdd">保存</el-button>
      </template>
    </el-dialog>
  </el-dialog>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { fetchCriticalValues, addCriticalValue, deleteCriticalValue, fetchCriticalValueStat, processCriticalValues, fetchPatientPreview } from '../../api/criticalValue'
import { fetchAllProjects } from '../../api/qc'
import * as echarts from 'echarts'

const props = defineProps({ modelValue: Boolean })
const emit = defineEmits(['update:modelValue'])

const activeTab = ref('list')
const beginDate = ref('')
const endDate = ref('')
const list = ref([])
const selectedIds = ref([])
const showAddForm = ref(false)
const addForm = ref({ reportId: '', xmid: '', criticalValue: '' })
const patientPreview = ref(null)
const patientNotFound = ref(false)
const allProjects = ref([])
const statBegin = ref(new Date(new Date().getFullYear(), 0, 1).toISOString().slice(0, 10))
const statEnd = ref(new Date().toISOString().slice(0, 10))
const statData = ref({})
const voiceEnabled = ref(false)
const autoVoice = ref(false)
let autoVoiceTimer = null

const unprocessedList = computed(() => list.value.filter(r => !r.process_date))

const loadList = async () => {
  try {
    const { data } = await fetchCriticalValues({ beginDate: beginDate.value, endDate: endDate.value })
    list.value = Array.isArray(data?.data) ? data.data : Array.isArray(data) ? data : []
  } catch (e) {}
}

const loadStat = async () => {
  try {
    const { data } = await fetchCriticalValueStat({ beginDate: statBegin.value, endDate: statEnd.value })
    statData.value = data?.data || data || {}
    await nextTick()
    renderCharts()
  } catch (e) {}
}

const doExport = () => {
  if (!statBegin.value || !statEnd.value) return ElMessage.warning('请选择日期范围')
  window.open(`/api/critical-value/export?beginDate=${statBegin.value}&endDate=${statEnd.value}`, '_blank')
}

const loadPatientPreview = async () => {
  patientPreview.value = null
  patientNotFound.value = false
  if (!addForm.value.reportId) return
  try {
    const { data } = await fetchPatientPreview({ reportId: addForm.value.reportId })
    if (data && data.brxm) {
      patientPreview.value = data
      patientNotFound.value = false
    } else {
      patientNotFound.value = true
    }
  } catch (e) { patientNotFound.value = true }
}

const doAdd = async () => {
  if (!addForm.value.reportId || !addForm.value.criticalValue) return ElMessage.warning('请填写必填项')
  if (!patientPreview.value) {
    await loadPatientPreview()
    if (!patientPreview.value) return ElMessage.warning('未找到该报告ID对应的患者，请先点击查询患者')
  }
  if (!addForm.value.xmid) return ElMessage.warning('请选择检验项目')
  const user = JSON.parse(localStorage.getItem('user') || '{}')
  try {
    const { data } = await addCriticalValue({
      reportId: addForm.value.reportId,
      xmid: addForm.value.xmid,
      criticalValue: addForm.value.criticalValue,
      addOperCode: user.czydm || 'admin',
      addOperName: user.czyxm || '管理员'
    })
    if (data.success) { ElMessage.success('添加成功'); showAddForm.value = false; addForm.value = { reportId: '', xmid: '', criticalValue: '' }; patientPreview.value = null; patientNotFound.value = false; loadList() }
    else ElMessage.error(data.message)
  } catch (e) { ElMessage.error('添加失败') }
}

const doCancel = async (row) => {
  try {
    await ElMessageBox.confirm('确定取消此危急值？', '提示', { type: 'warning' })
    const user = JSON.parse(localStorage.getItem('user') || '{}')
    const { data } = await deleteCriticalValue(row.id, { cancelOperCode: user.czydm || 'admin' })
    if (data.success) { ElMessage.success('已取消'); loadList() }
    else ElMessage.error(data.message)
  } catch (e) {}
}

const handleSelectionChange = (rows) => {
  selectedIds.value = rows.map(r => r.id)
}

const doBatchProcess = async () => {
  if (selectedIds.value.length === 0) return
  try {
    await ElMessageBox.confirm(`确定批量处理 ${selectedIds.value.length} 条危急值？`, '提示', { type: 'info' })
    const user = JSON.parse(localStorage.getItem('user') || '{}')
    const { data } = await processCriticalValues({ ids: selectedIds.value, processOperName: user.czyxm || '管理员' })
    if (data.success) { ElMessage.success(data.message); loadList() }
    else ElMessage.error(data.message)
  } catch (e) {}
}

const doProcessOne = async (row) => {
  const user = JSON.parse(localStorage.getItem('user') || '{}')
  try {
    const { data } = await processCriticalValues({ ids: [row.id], processOperName: user.czyxm || '管理员' })
    if (data.success) { ElMessage.success('处理成功'); loadList() }
    else ElMessage.error(data.message)
  } catch (e) { ElMessage.error('处理失败') }
}

const buildVoiceText = (row) => {
  return `危急值提醒：患者${row.brxm || ''}，${row.xmzwmc || ''}，结果${row.critical_value || ''}，科室${row.ksmc || ''}`
}

const getChineseVoice = () => {
  const voices = window.speechSynthesis.getVoices()
  return voices.find(v => v.lang === 'zh-CN') ||
         voices.find(v => v.lang.startsWith('zh')) ||
         null
}

const speakOne = (row) => {
  if (!('speechSynthesis' in window)) return ElMessage.warning('浏览器不支持语音播报')
  window.speechSynthesis.cancel()
  const utterance = new SpeechSynthesisUtterance(buildVoiceText(row))
  utterance.lang = 'zh-CN'
  utterance.rate = 0.9
  const zhVoice = getChineseVoice()
  if (zhVoice) utterance.voice = zhVoice
  window.speechSynthesis.speak(utterance)
}

const speakAll = () => {
  if (unprocessedList.value.length === 0) return ElMessage.info('无未处理危急值')
  if (!('speechSynthesis' in window)) return ElMessage.warning('浏览器不支持语音播报')
  window.speechSynthesis.cancel()
  const texts = unprocessedList.value.map(r => buildVoiceText(r))
  let idx = 0
  const zhVoice = getChineseVoice()
  const speakNext = () => {
    if (idx >= texts.length) return
    const u = new SpeechSynthesisUtterance(texts[idx])
    u.lang = 'zh-CN'
    u.rate = 0.9
    if (zhVoice) u.voice = zhVoice
    u.onend = () => { idx++; speakNext() }
    window.speechSynthesis.speak(u)
  }
  speakNext()
}

const stopSpeaking = () => {
  if ('speechSynthesis' in window) window.speechSynthesis.cancel()
}

const toggleVoice = () => { voiceEnabled.value = !voiceEnabled.value }

watch(autoVoice, (v) => {
  if (v) {
    autoVoiceTimer = setInterval(() => {
      if (unprocessedList.value.length > 0) speakAll()
    }, 30000)
  } else {
    if (autoVoiceTimer) { clearInterval(autoVoiceTimer); autoVoiceTimer = null }
  }
})

watch(() => props.modelValue, (v) => {
  if (!v) {
    autoVoice.value = false
    stopSpeaking()
  }
})

const loadProjects = async () => {
  try {
    const { data } = await fetchAllProjects()
    allProjects.value = Array.isArray(data?.data) ? data.data : Array.isArray(data) ? data : []
  } catch (e) {}
}

function buildPieData(arr, nameKey) {
  if (!arr || arr.length === 0) return []
  const top10 = arr.slice(0, 10)
  const otherCnt = arr.slice(10).reduce((s, r) => s + (Number(r.cnt) || 0), 0)
  const pieData = top10.map(r => ({ name: r[nameKey] || '未知', value: Number(r.cnt) || 0 }))
  if (otherCnt > 0) pieData.push({ name: '其他', value: otherCnt })
  return pieData
}

let deptChart = null, itemChart = null, monthChart = null

const renderCharts = () => {
  const byDept = statData.value.byDepartment || []
  const byItem = statData.value.byItem || []
  const byMonth = statData.value.byMonth || []

  const deptDom = document.getElementById('cvDeptPie')
  if (deptDom && byDept.length > 0) {
    if (!deptChart) deptChart = echarts.init(deptDom)
    deptChart.setOption({
      title: { text: '科室分布', left: 'center', textStyle: { fontSize: 13 } },
      tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
      series: [{ type: 'pie', radius: ['30%', '65%'], label: { fontSize: 10 }, data: buildPieData(byDept, 'ksmc') }]
    })
  }

  const itemDom = document.getElementById('cvItemPie')
  if (itemDom && byItem.length > 0) {
    if (!itemChart) itemChart = echarts.init(itemDom)
    itemChart.setOption({
      title: { text: '项目分布', left: 'center', textStyle: { fontSize: 13 } },
      tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
      series: [{ type: 'pie', radius: ['30%', '65%'], label: { fontSize: 10 }, data: buildPieData(byItem, 'xmmc') }]
    })
  }

  const monthDom = document.getElementById('cvMonthTrend')
  if (monthDom && byMonth.length > 0) {
    if (!monthChart) monthChart = echarts.init(monthDom)
    monthChart.setOption({
      title: { text: '月度趋势', left: 'center', textStyle: { fontSize: 13 } },
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: byMonth.map(r => r.month) },
      yAxis: { type: 'value', name: '数量' },
      series: [{ type: 'line', data: byMonth.map(r => Number(r.cnt) || 0), smooth: true, areaStyle: { opacity: 0.2 } }]
    })
  }
}

onMounted(() => { loadList(); loadStat(); loadProjects() })
onUnmounted(() => { stopSpeaking(); if (autoVoiceTimer) clearInterval(autoVoiceTimer) })
</script>
