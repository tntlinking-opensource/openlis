<template>
  <el-dialog 
    :model-value="visible" 
    @update:model-value="$emit('update:visible', $event)" 
    title="样本作废" 
    width="500px" 
    :close-on-click-modal="false" 
    append-to-body
  >
    <div class="invalid-dialog">
      <div class="sample-info" v-if="sample">
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="姓名">{{ sample.brxm || sample.name }}</el-descriptions-item>
          <el-descriptions-item label="条码号">{{ sample.brxx_tmh || sample.barcode }}</el-descriptions-item>
          <el-descriptions-item label="样本号">{{ sample.syh || sample.sampleNo }}</el-descriptions-item>
          <el-descriptions-item label="科室">{{ sample.ksmc || sample.dept }}</el-descriptions-item>
        </el-descriptions>
      </div>
      
      <el-form :model="form" label-width="80px" style="margin-top: 16px;">
        <el-form-item label="作废原因" required>
          <el-input 
            v-model="form.reason" 
            type="textarea" 
            :rows="4" 
            placeholder="请输入作废原因" 
            maxlength="200"
            show-word-limit
          />
        </el-form-item>
      </el-form>
    </div>
    
    <template #footer>
      <el-button @click="handleCancel">取消</el-button>
      <el-button type="danger" @click="handleConfirm" :loading="loading">确认作废</el-button>
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
  reason: ''
})

const loading = ref(false)

watch(() => props.visible, (val) => {
  if (val) {
    form.value.reason = ''
  }
})

const handleConfirm = async () => {
  if (!form.value.reason || !form.value.reason.trim()) {
    ElMessage.warning('请输入作废原因')
    return
  }
  
  if (!props.sample || !props.sample.brxx_id) {
    ElMessage.error('样本信息不完整')
    return
  }
  
  loading.value = true
  try {
    const { data } = await axios.post(`/api/sample/invalid/${props.sample.brxx_id}`, {
      reason: form.value.reason.trim()
    })
    if (data.success) {
      ElMessage.success(data.message || '样本已作废')
      emit('confirm', form.value.reason)
      emit('update:visible', false)
    } else {
      ElMessage.error(data.message || '作废失败')
    }
  } catch (e) {
    ElMessage.error('作废失败：' + (e.response?.data?.message || e.message))
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
.sample-info {
  margin-bottom: 8px;
}
</style>
