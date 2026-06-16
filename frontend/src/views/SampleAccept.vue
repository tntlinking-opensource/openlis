<template>
  <div class="sample-accept">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>样本核收</span>
        </div>
      </template>
      
      <div class="accept-form">
        <div class="barcode-input-section">
          <BarcodeInput 
            ref="barcodeInputRef"
            @scan="handleBarcodeScan" 
          />
        </div>
        
        <div class="sample-info-section" v-if="currentSample">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="姓名">{{ currentSample.brxm }}</el-descriptions-item>
            <el-descriptions-item label="性别">{{ currentSample.brxb === 1 ? '男' : currentSample.brxb === 2 ? '女' : '' }}</el-descriptions-item>
            <el-descriptions-item label="年龄">{{ currentSample.brnl }} {{ ageUnitText(currentSample.nllx) }}</el-descriptions-item>
            <el-descriptions-item label="科室">{{ currentSample.ksmc }}</el-descriptions-item>
            <el-descriptions-item label="条码号">{{ currentSample.brxx_tmh }}</el-descriptions-item>
            <el-descriptions-item label="样本类型">{{ currentSample.bbzl }}</el-descriptions-item>
            <el-descriptions-item label="检验项目" :span="2">{{ currentSample.xmmc }}</el-descriptions-item>
          </el-descriptions>
          
          <div class="action-buttons">
            <el-button type="success" size="large" @click="handleAccept" :loading="accepting">
              核收
            </el-button>
            <el-button type="danger" size="large" @click="handleReject">
              拒收
            </el-button>
            <el-button size="large" @click="handleClear">
              清空
            </el-button>
          </div>
        </div>
        
        <div class="empty-section" v-else>
          <el-empty description="请扫描或输入条码进行核收" />
        </div>
      </div>
      
      <RejectDialog 
        v-model="rejectDialogVisible" 
        :sample="currentSample" 
        @confirm="handleRejectConfirm"
      />
      
      <SignatureDialog
        v-model="signatureDialogVisible"
        :sample="currentSample"
        @confirm="handleSignatureConfirm"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import BarcodeInput from '../components/dialogs/BarcodeInput.vue'
import RejectDialog from '../components/dialogs/RejectDialog.vue'
import SignatureDialog from '../components/dialogs/SignatureDialog.vue'

const barcodeInputRef = ref(null)
const currentSample = ref(null)
const accepting = ref(false)
const rejectDialogVisible = ref(false)
const signatureDialogVisible = ref(false)

const ageUnitText = (unit) => {
  const n = Number(unit)
  if (n === 2) return '月'
  if (n === 3) return '天'
  return '岁'
}

const handleBarcodeScan = async (barcode) => {
  if (!barcode) return
  
  try {
    const { data } = await axios.get('/api/sample/patients', { params: { barcode } })
    const list = Array.isArray(data) ? data : []
    if (list.length > 0) {
      currentSample.value = list[0]
      ElMessage.success('找到样本信息')
    } else {
      currentSample.value = null
      ElMessage.warning('未找到该条码对应的样本')
    }
  } catch (e) {
    ElMessage.error('查询失败：' + (e.response?.data?.message || e.message))
  }
}

const handleAccept = async () => {
  if (!currentSample.value) {
    ElMessage.warning('请先扫描样本条码')
    return
  }
  
  try {
    const { data } = await axios.post(`/api/sample/accept/${currentSample.value.brxx_id}`)
    if (data.success) {
      ElMessage.success(data.message || '核收成功')
      currentSample.value = null
      barcodeInputRef.value?.focus()
    } else {
      ElMessage.error(data.message || '核收失败')
    }
  } catch (e) {
    ElMessage.error('核收失败：' + (e.response?.data?.message || e.message))
  }
}

const handleReject = () => {
  if (!currentSample.value) {
    ElMessage.warning('请先扫描样本条码')
    return
  }
  rejectDialogVisible.value = true
}

const handleRejectConfirm = (reason) => {
  ElMessage.success('样本已拒收')
  currentSample.value = null
  barcodeInputRef.value?.focus()
}

const handleClear = () => {
  currentSample.value = null
  barcodeInputRef.value?.focus()
}

const handleSignatureConfirm = (signature) => {
  ElMessage.success('电子签名核收成功')
  currentSample.value = null
  barcodeInputRef.value?.focus()
}
</script>

<style scoped>
.sample-accept {
  padding: 20px;
}

.card-header {
  font-size: 16px;
  font-weight: bold;
}

.accept-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.barcode-input-section {
  padding: 20px;
  background: #f5f7fa;
  border-radius: 4px;
}

.sample-info-section {
  padding: 20px 0;
}

.action-buttons {
  display: flex;
  gap: 16px;
  justify-content: center;
  margin-top: 24px;
}

.empty-section {
  padding: 60px 0;
}
</style>
