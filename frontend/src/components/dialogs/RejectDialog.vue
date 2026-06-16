<template>
  <el-dialog 
    :model-value="visible" 
    @update:model-value="$emit('update:visible', $event)" 
    title="样本拒收" 
    width="500px" 
    :close-on-click-modal="false" 
    append-to-body
  >
    <div class="reject-dialog">
      <div class="sample-info" v-if="sample">
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="姓名">{{ sample.brxm || sample.name }}</el-descriptions-item>
          <el-descriptions-item label="条码号">{{ sample.brxx_tmh || sample.barcode }}</el-descriptions-item>
          <el-descriptions-item label="样本号">{{ sample.syh || sample.sampleNo }}</el-descriptions-item>
          <el-descriptions-item label="科室">{{ sample.ksmc || sample.dept }}</el-descriptions-item>
        </el-descriptions>
      </div>
      
      <el-form :model="form" label-width="80px" style="margin-top: 16px;">
        <el-form-item label="拒收原因" required>
          <el-select v-model="form.reason" placeholder="请选择拒收原因" style="width: 100%;">
            <el-option label="样本量不足" value="样本量不足" />
            <el-option label="样本类型错误" value="样本类型错误" />
            <el-option label="样本溶血" value="样本溶血" />
            <el-option label="样本脂血" value="样本脂血" />
            <el-option label="样本凝块" value="样本凝块" />
            <el-option label="容器错误" value="容器错误" />
            <el-option label="信息不符" value="信息不符" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="详细说明" v-if="form.reason === '其他'">
          <el-input 
            v-model="form.detail" 
            type="textarea" 
            :rows="3" 
            placeholder="请输入详细说明" 
          />
        </el-form-item>
        
        <el-form-item label="处理方式">
          <el-select v-model="form.action" placeholder="请选择处理方式" style="width: 100%;">
            <el-option label="重新采样" value="重新采样" />
            <el-option label="补做检测" value="补做检测" />
            <el-option label="无需处理" value="无需处理" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="备注">
          <el-input 
            v-model="form.notes" 
            type="textarea" 
            :rows="2" 
            placeholder="备注信息（可选）" 
          />
        </el-form-item>
      </el-form>
    </div>
    
    <template #footer>
      <el-button @click="handleCancel">取消</el-button>
      <el-button type="danger" @click="handleConfirm" :loading="loading">确认拒收</el-button>
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
  reason: '',
  detail: '',
  action: '重新采样',
  notes: ''
})

const loading = ref(false)

watch(() => props.visible, (val) => {
  if (val) {
    form.value = {
      reason: '',
      detail: '',
      action: '重新采样',
      notes: ''
    }
  }
})

const handleConfirm = async () => {
  if (!form.value.reason) {
    ElMessage.warning('请选择拒收原因')
    return
  }
  
  if (form.value.reason === '其他' && !form.value.detail) {
    ElMessage.warning('请输入详细说明')
    return
  }
  
  if (!props.sample || !props.sample.brxx_id) {
    ElMessage.error('样本信息不完整')
    return
  }
  
  loading.value = true
  try {
    const { data } = await axios.post(`/api/sample/reject/${props.sample.brxx_id}`, {
      reason: form.value.reason === '其他' ? form.value.detail : form.value.reason,
      action: form.value.action,
      notes: form.value.notes
    })
    if (data.success) {
      ElMessage.success(data.message || '样本已拒收')
      emit('confirm', { ...form.value })
      emit('update:visible', false)
    } else {
      ElMessage.error(data.message || '拒收失败')
    }
  } catch (e) {
    ElMessage.error('拒收失败：' + (e.response?.data?.message || e.message))
  } finally {
    loading.value = false
  }
}

const handleCancel = () => {
  emit('cancel')
  emit('update:visible', false)
}
</script>
