<template>
  <el-dialog 
    :model-value="visible" 
    @update:model-value="$emit('update:visible', $event)" 
    title="电子签名" 
    width="450px" 
    :close-on-click-modal="false" 
    append-to-body
  >
    <div class="signature-dialog">
      <div class="sample-info" v-if="sample">
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="姓名">{{ sample.brxm || sample.name }}</el-descriptions-item>
          <el-descriptions-item label="条码号">{{ sample.brxx_tmh || sample.barcode }}</el-descriptions-item>
        </el-descriptions>
      </div>
      
      <div class="signature-section" style="margin-top: 20px;">
        <el-form :model="form" label-width="80px">
          <el-form-item label="签名密码" required>
            <el-input 
              v-model="form.password" 
              type="password" 
              placeholder="请输入签名密码" 
              show-password
              @keyup.enter="handleConfirm"
            />
          </el-form-item>
          <el-form-item label="签名类型">
            <el-radio-group v-model="form.type">
              <el-radio label="审核">审核签名</el-radio>
              <el-radio label="检验">检验签名</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-form>
        
        <div class="signature-notice">
          <el-alert type="info" :closable="false">
            电子签名将具有与手写签名同等的法律效力，请妥善保管您的签名密码。
          </el-alert>
        </div>
      </div>
    </div>
    
    <template #footer>
      <el-button @click="handleCancel">取消</el-button>
      <el-button type="primary" @click="handleConfirm" :loading="loading">确认签名</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  sample: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['update:visible', 'confirm', 'cancel'])

const form = ref({
  password: '',
  type: '审核'
})

const loading = ref(false)

watch(() => props.visible, (val) => {
  if (val) {
    form.value.password = ''
    form.value.type = '审核'
  }
})

const handleConfirm = async () => {
  if (!form.value.password) {
    ElMessage.warning('请输入签名密码')
    return
  }
  
  if (!props.sample || !props.sample.brxx_id) {
    ElMessage.error('样本信息不完整')
    return
  }
  
  loading.value = true
  try {
    const { data } = await axios.post(`/api/sample/acceptWithSign/${props.sample.brxx_id}`, {
      password: form.value.password,
      signType: form.value.type,
      czydm: JSON.parse(localStorage.getItem('user') || '{}').czydm || 'admin'
    })
    if (data.success) {
      ElMessage.success(data.message || '电子签名核收成功')
      emit('confirm', { ...form.value })
      emit('update:visible', false)
    } else {
      ElMessage.error(data.message || '签名失败')
    }
  } catch (e) {
    ElMessage.error('签名失败：' + (e.response?.data?.message || e.message))
  } finally {
    loading.value = false
  }
}

const handleCancel = () => {
  emit('cancel')
  emit('update:visible', false)
}
</script>

<style scoped>
.signature-section {
  padding: 10px 0;
}

.signature-notice {
  margin-top: 16px;
}
</style>
