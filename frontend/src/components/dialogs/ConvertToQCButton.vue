<template>
  <el-button 
    type="info" 
    size="small" 
    @click="handleConvertToQC" 
    :loading="loading"
    :disabled="disabled"
    title="转换为质控"
  >
    <slot>
      <span>转QC</span>
    </slot>
  </el-button>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'

const props = defineProps({
  disabled: {
    type: Boolean,
    default: false
  },
  sample: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['click', 'success', 'error'])

const loading = ref(false)

const handleConvertToQC = async () => {
  if (!props.sample || !props.sample.brxx_id) {
    ElMessage.warning('请先选择要转换的样本')
    return
  }
  
  try {
    await ElMessageBox.confirm(
      `确定要将样本 ${props.sample.syh || props.sample.barcode} 转换为质控样本吗？`, 
      '转换为质控', 
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    loading.value = true
    const { data } = await axios.post('/api/sample/convertToQC', {
      brxxId: props.sample.brxx_id,
      czydm: JSON.parse(localStorage.getItem('user') || '{}').czydm || 'admin'
    })
    
    if (data.success) {
      ElMessage.success(data.message || '已转换为质控样本')
      emit('success', data)
    } else {
      ElMessage.error(data.message || '转换失败')
      emit('error', data)
    }
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('转换失败：' + (e.response?.data?.message || e.message))
      emit('error', e)
    }
  } finally {
    loading.value = false
  }
  emit('click')
}
</script>
