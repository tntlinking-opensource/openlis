<template>
  <el-dialog 
    :model-value="visible" 
    @update:model-value="$emit('update:visible', $event)" 
    title="患者360视图" 
    width="1000px" 
    :close-on-click-modal="false" 
    append-to-body
  >
    <div class="patient-360-dialog">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="患者信息" name="info">
          <div class="patient-info-section">
            <el-descriptions :column="3" border v-if="patientData.patient">
              <el-descriptions-item label="姓名">{{ patientData.patient.brxm }}</el-descriptions-item>
              <el-descriptions-item label="性别">{{ patientData.patient.brxb === 1 ? '男' : '女' }}</el-descriptions-item>
              <el-descriptions-item label="年龄">{{ patientData.patient.brnl }} {{ ageUnitText(patientData.patient.nllx) }}</el-descriptions-item>
              <el-descriptions-item label="证件号">{{ patientData.patient.zjhm }}</el-descriptions-item>
              <el-descriptions-item label="联系电话">{{ patientData.patient.lxdh }}</el-descriptions-item>
              <el-descriptions-item label="地址" :span="2">{{ patientData.patient.jtdz }}</el-descriptions-item>
            </el-descriptions>
          </div>
        </el-tab-pane>
        
        <el-tab-pane label="样本列表" name="samples">
          <el-table :data="patientData.samples" border stripe size="small" max-height="300">
            <el-table-column prop="syh" label="样本号" width="100" />
            <el-table-column prop="brxx_tmh" label="条码号" width="130" />
            <el-table-column prop="ksmc" label="科室" width="100" />
            <el-table-column prop="bbzl" label="样本类型" width="100" />
            <el-table-column prop="jyrq" label="检验日期" width="160" />
            <el-table-column label="状态" width="80">
              <template #default="{row}">
                <el-tag :type="getStatusType(row.ybzt)" size="small">
                  {{ getStatusText(row.ybzt) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100">
              <template #default="{row}">
                <el-button link type="primary" size="small" @click="viewSampleDetail(row)">查看</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        
        <el-tab-pane label="报告列表" name="reports">
          <el-table :data="patientData.reports" border stripe size="small" max-height="300">
            <el-table-column prop="syh" label="样本号" width="100" />
            <el-table-column prop="brxx_tmh" label="条码号" width="130" />
            <el-table-column prop="ksmc" label="科室" width="100" />
            <el-table-column prop="jyrq" label="检验日期" width="160" />
            <el-table-column prop="shys" label="审核医生" width="100" />
            <el-table-column label="状态" width="80">
              <template #default="{row}">
                <el-tag :type="row.ybzt >= 3 ? 'success' : row.ybzt >= 2 ? 'warning' : 'info'" size="small">
                  {{ getStatusText(row.ybzt) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100">
              <template #default="{row}">
                <el-button link type="primary" size="small" @click="viewReport(row)">查看</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
      
      <div v-if="loading" class="loading-mask">
        <el-icon class="is-loading"><Loading /></el-icon>
      </div>
    </div>
  </el-dialog>
</template>

<script setup>
import { ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import { Loading } from '@element-plus/icons-vue'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  patientId: {
    type: Number,
    default: null
  }
})

const emit = defineEmits(['update:visible'])

const activeTab = ref('info')
const patientData = ref({
  patient: null,
  samples: [],
  reports: []
})
const loading = ref(false)

const loadPatient360 = async () => {
  if (!props.patientId) return
  
  loading.value = true
  try {
    const { data } = await axios.get(`/api/patient/360/${props.patientId}`)
    if (data.success === false) {
      ElMessage.error(data.message || '获取患者360信息失败')
      patientData.value = { patient: null, samples: [], reports: [] }
      return
    }
    patientData.value = {
      patient: data?.patientInfo || null,
      samples: Array.isArray(data?.results) ? data.results : [],
      reports: Array.isArray(data?.results) ? data.results : []
    }
  } catch (e) {
    ElMessage.error('获取患者360信息失败：' + (e.response?.data?.message || e.message))
    patientData.value = { patient: null, samples: [], reports: [] }
  } finally {
    loading.value = false
  }
}

const ageUnitText = (unit) => {
  const n = Number(unit)
  if (n === 2) return '月'
  if (n === 3) return '天'
  return '岁'
}

const getStatusType = (status) => {
  if (status >= 3) return 'success'
  if (status >= 2) return 'warning'
  if (status === -1) return 'danger'
  return 'info'
}

import { getStatusText as _getStatusText } from '../../utils/sampleStatus'
const getStatusText = (status) => _getStatusText(status)

const viewSampleDetail = (sample) => {
  ElMessage.info('查看样本详情: ' + sample.syh)
}

const viewReport = (report) => {
  ElMessage.info('查看报告: ' + report.syh)
}

watch(() => props.visible, (val) => {
  if (val) {
    activeTab.value = 'info'
    loadPatient360()
  }
})

watch(() => props.patientId, () => {
  if (props.visible) {
    loadPatient360()
  }
})
</script>

<style scoped>
.patient-360-dialog {
  position: relative;
  min-height: 400px;
}

.loading-mask {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.patient-info-section {
  padding: 10px 0;
}
</style>
