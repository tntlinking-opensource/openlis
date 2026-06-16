<template>
  <el-dialog :model-value="modelValue" @update:model-value="$emit('update:modelValue', $event)" title="综合统计" width="1200px" :close-on-click-modal="false" append-to-body>
    <div style="margin-bottom:12px;display:flex;gap:8px;align-items:center;">
      <el-date-picker v-model="beginDate" type="date" value-format="YYYY-MM-DD" placeholder="开始日期" style="width:150px" />
      <el-date-picker v-model="endDate" type="date" value-format="YYYY-MM-DD" placeholder="结束日期" style="width:150px" />
      <el-button type="primary" @click="loadAll">查询</el-button>
    </div>

    <el-tabs v-model="activeTab">
      <el-tab-pane label="综合概览" name="overview">
        <el-row :gutter="12" style="margin-bottom:16px;">
          <el-col :span="6"><el-statistic title="总样本数" :value="overview.total?.total || 0" /></el-col>
          <el-col :span="6"><el-statistic title="已审核" :value="overview.total?.audited || 0" /></el-col>
          <el-col :span="6"><el-statistic title="已打印" :value="overview.total?.printed || 0" /></el-col>
        </el-row>
        <el-row :gutter="12">
          <el-col :span="12">
            <strong>按状态</strong>
            <el-table :data="overview.byStatus || []" border stripe size="small" style="margin-top:8px;">
              <el-table-column prop="statusName" label="状态" />
              <el-table-column prop="cnt" label="数量" width="100" />
            </el-table>
          </el-col>
          <el-col :span="12">
            <strong>按病人类型</strong>
            <el-table :data="overview.byPatientType || []" border stripe size="small" style="margin-top:8px;">
              <el-table-column prop="name" label="类型" />
              <el-table-column prop="cnt" label="数量" width="100" />
            </el-table>
          </el-col>
        </el-row>
      </el-tab-pane>

      <el-tab-pane label="科室统计" name="dept">
        <el-table :data="byDept" border stripe size="small" max-height="350">
          <el-table-column prop="ksmc" label="科室" />
          <el-table-column prop="cnt" label="样本数" width="100" />
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="医生统计" name="doctor">
        <el-table :data="byDoctor" border stripe size="small" max-height="350">
          <el-table-column prop="czyxm" label="医生" />
          <el-table-column prop="cnt" label="审核数" width="100" />
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="项目统计" name="item">
        <el-table :data="byItem" border stripe size="small" max-height="350">
          <el-table-column prop="xmzwmc" label="项目名称" />
          <el-table-column prop="cnt" label="样本数" width="100" />
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="工作量统计" name="workload">
        <el-tabs v-model="workloadTab" type="card" @tab-change="loadWorkloadTab">
          <el-tab-pane label="按检验项目" name="item">
            <div style="margin-bottom:8px;">
              <el-button type="success" size="small" @click="exportExcel('item')">导出Excel</el-button>
            </div>
            <el-table :data="workloadItem" border stripe size="small" max-height="350" @row-dblclick="onWorkloadRowDblClick('item', $event)">
              <el-table-column prop="zhid" label="项目代码" width="100" />
              <el-table-column prop="zhmc" label="项目名称" width="150" />
              <el-table-column prop="mzrs" label="门诊人数" width="90" />
              <el-table-column prop="mzfy" label="门诊费用" width="100" />
              <el-table-column prop="zyrs" label="住院人数" width="90" />
              <el-table-column prop="zyfy" label="住院费用" width="100" />
              <el-table-column prop="tjrs" label="体检人数" width="90" />
              <el-table-column prop="tjfy" label="体检费用" width="100" />
              <el-table-column prop="qtrs" label="其他人数" width="90" />
              <el-table-column prop="qtfy" label="其他费用" width="100" />
              <el-table-column prop="zrs" label="总人数" width="90" />
              <el-table-column prop="zfy" label="总费用" width="100" />
            </el-table>
          </el-tab-pane>

          <el-tab-pane label="按开单科室" name="dept">
            <div style="margin-bottom:8px;">
              <el-button type="success" size="small" @click="exportExcel('dept')">导出Excel</el-button>
            </div>
            <el-table :data="workloadDept" border stripe size="small" max-height="350" @row-dblclick="onWorkloadRowDblClick('dept', $event)">
              <el-table-column prop="ksmc" label="科室" width="150" />
              <el-table-column prop="mzrs" label="门诊人数" width="90" />
              <el-table-column prop="mzfy" label="门诊费用" width="100" />
              <el-table-column prop="zyrs" label="住院人数" width="90" />
              <el-table-column prop="zyfy" label="住院费用" width="100" />
              <el-table-column prop="tjrs" label="体检人数" width="90" />
              <el-table-column prop="tjfy" label="体检费用" width="100" />
              <el-table-column prop="qtrs" label="其他人数" width="90" />
              <el-table-column prop="qtfy" label="其他费用" width="100" />
              <el-table-column prop="zrs" label="总人数" width="90" />
              <el-table-column prop="zfy" label="总费用" width="100" />
            </el-table>
          </el-tab-pane>

          <el-tab-pane label="按开单医生" name="doctor">
            <div style="margin-bottom:8px;">
              <el-button type="success" size="small" @click="exportExcel('doctor')">导出Excel</el-button>
            </div>
            <el-table :data="workloadDoctor" border stripe size="small" max-height="350" @row-dblclick="onWorkloadRowDblClick('doctor', $event)">
              <el-table-column prop="sjys" label="医生" width="150" />
              <el-table-column prop="mzrs" label="门诊人数" width="90" />
              <el-table-column prop="mzfy" label="门诊费用" width="100" />
              <el-table-column prop="zyrs" label="住院人数" width="90" />
              <el-table-column prop="zyfy" label="住院费用" width="100" />
              <el-table-column prop="tjrs" label="体检人数" width="90" />
              <el-table-column prop="tjfy" label="体检费用" width="100" />
              <el-table-column prop="qtrs" label="其他人数" width="90" />
              <el-table-column prop="qtfy" label="其他费用" width="100" />
              <el-table-column prop="zrs" label="总人数" width="90" />
              <el-table-column prop="zfy" label="总费用" width="100" />
            </el-table>
          </el-tab-pane>

          <el-tab-pane label="按检验医生" name="examiner">
            <div style="margin-bottom:8px;">
              <el-button type="success" size="small" @click="exportExcel('examiner')">导出Excel</el-button>
            </div>
            <el-table :data="workloadExaminer" border stripe size="small" max-height="350" @row-dblclick="onWorkloadRowDblClick('examiner', $event)">
              <el-table-column prop="jyys" label="检验医生" width="150" />
              <el-table-column prop="mzrs" label="门诊人数" width="90" />
              <el-table-column prop="mzfy" label="门诊费用" width="100" />
              <el-table-column prop="zyrs" label="住院人数" width="90" />
              <el-table-column prop="zyfy" label="住院费用" width="100" />
              <el-table-column prop="tjrs" label="体检人数" width="90" />
              <el-table-column prop="tjfy" label="体检费用" width="100" />
              <el-table-column prop="qtrs" label="其他人数" width="90" />
              <el-table-column prop="qtfy" label="其他费用" width="100" />
              <el-table-column prop="zrs" label="总人数" width="90" />
              <el-table-column prop="zfy" label="总费用" width="100" />
            </el-table>
          </el-tab-pane>
        </el-tabs>
      </el-tab-pane>
    </el-tabs>

    <el-dialog v-model="detailVisible" title="明细数据" width="900px" append-to-body :close-on-click-modal="false">
      <el-table :data="detailData" border stripe size="small" max-height="400">
        <el-table-column prop="sjh" label="样本号" width="100" />
        <el-table-column prop="xm" label="姓名" width="80" />
        <el-table-column prop="xb" label="性别" width="60" />
        <el-table-column prop="nl" label="年龄" width="60" />
        <el-table-column prop="ksmc" label="科室" width="120" />
        <el-table-column prop="sjys" label="开单医生" width="100" />
        <el-table-column prop="jyys" label="检验医生" width="100" />
        <el-table-column prop="brlb" label="病人类型" width="80" />
        <el-table-column prop="jysj" label="检验时间" width="160" />
      </el-table>
    </el-dialog>
  </el-dialog>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  fetchComprehensiveStat,
  fetchStatByDepartment,
  fetchStatByDoctor,
  fetchStatByItem,
  fetchWorkloadByItemV2,
  fetchWorkloadItemDetailV2,
  fetchWorkloadByDept,
  fetchWorkloadDeptDetail,
  fetchWorkloadByDoctor,
  fetchWorkloadDoctorDetail,
  fetchWorkloadByExaminer,
  fetchWorkloadExaminerDetail
} from '../../api/statistics'

const props = defineProps({ modelValue: Boolean })
const emit = defineEmits(['update:modelValue'])

const beginDate = ref('')
const endDate = ref('')
const activeTab = ref('overview')
const overview = ref({})
const byDept = ref([])
const byDoctor = ref([])
const byItem = ref([])

const workloadTab = ref('item')
const workloadItem = ref([])
const workloadDept = ref([])
const workloadDoctor = ref([])
const workloadExaminer = ref([])

const detailVisible = ref(false)
const detailData = ref([])

const workloadLoaded = ref({ item: false, dept: false, doctor: false, examiner: false })

const loadAll = async () => {
  const params = { beginDate: beginDate.value, endDate: endDate.value }
  try {
    const [comp, dept, doctor, item] = await Promise.all([
      fetchComprehensiveStat(params),
      fetchStatByDepartment(params),
      fetchStatByDoctor(params),
      fetchStatByItem(params)
    ])
    overview.value = comp.data?.data || comp.data || {}
    byDept.value = Array.isArray(dept.data) ? dept.data : []
    byDoctor.value = Array.isArray(doctor.data) ? doctor.data : []
    byItem.value = Array.isArray(item.data) ? item.data : []
  } catch (e) { ElMessage.error('统计查询失败') }

  workloadLoaded.value = { item: false, dept: false, doctor: false, examiner: false }
  loadWorkloadTab()
}

const loadWorkloadTab = async () => {
  const tab = workloadTab.value
  if (workloadLoaded.value[tab]) return
  const params = { beginDate: beginDate.value, endDate: endDate.value }
  try {
    let res
    switch (tab) {
      case 'item':
        res = await fetchWorkloadByItemV2(params)
        workloadItem.value = Array.isArray(res.data) ? res.data : []
        break
      case 'dept':
        res = await fetchWorkloadByDept(params)
        workloadDept.value = Array.isArray(res.data) ? res.data : []
        break
      case 'doctor':
        res = await fetchWorkloadByDoctor(params)
        workloadDoctor.value = Array.isArray(res.data) ? res.data : []
        break
      case 'examiner':
        res = await fetchWorkloadByExaminer(params)
        workloadExaminer.value = Array.isArray(res.data) ? res.data : []
        break
    }
    workloadLoaded.value[tab] = true
  } catch (e) { ElMessage.error('工作量查询失败') }
}

const onWorkloadRowDblClick = (tab, row) => (async () => {
  const params = { beginDate: beginDate.value, endDate: endDate.value }
  try {
    let res
    switch (tab) {
      case 'item':
        res = await fetchWorkloadItemDetailV2({ ...params, zhid: row.zhid })
        break
      case 'dept':
        res = await fetchWorkloadDeptDetail({ ...params, ksmc: row.ksmc })
        break
      case 'doctor':
        res = await fetchWorkloadDoctorDetail({ ...params, sjys: row.sjys })
        break
      case 'examiner':
        res = await fetchWorkloadExaminerDetail({ ...params, jyys: row.jyys })
        break
    }
    detailData.value = Array.isArray(res.data) ? res.data : []
    detailVisible.value = true
  } catch (e) { ElMessage.error('明细查询失败') }
})()

const exportExcel = (tab) => {
  const bd = beginDate.value || '2020-01-01'
  const ed = endDate.value || '2099-12-31'
  window.open(`/api/statistics/export-workload?tab=${tab}&beginDate=${bd}&endDate=${ed}`, '_blank')
}

onMounted(() => { loadAll() })
</script>
