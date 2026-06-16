<template>
  <el-button 
    type="warning" 
    size="small" 
    @click="handleQuickExtract" 
    :loading="loading"
    :disabled="disabled"
    title="快速提取"
  >
    <slot>
      <span>快速提取</span>
    </slot>
  </el-button>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const props = defineProps({
  disabled: {
    type: Boolean,
    default: false
  },
  instrumentId: {
    type: [Number, String],
    default: null
  },
  date: {
    type: String,
    default: null
  }
})

const emit = defineEmits(['click', 'success', 'error'])

const loading = ref(false)

const handleQuickExtract = async () => {
  loading.value = true
  try {
    const { data } = await axios.post('/api/sample/quickExtract', {
      sbDjid: props.instrumentId,
      date: props.date || new Date().toISOString().slice(0, 10),
      czydm: JSON.parse(localStorage.getItem('user') || '{}').czydm || 'admin'
    })
    if (data.success) {
      ElMessage.success(data.message || '快速提取完成')
      emit('success', data)
    } else {
      ElMessage.warning(data.message || '提取失败')
      emit('error', data)
    }
  } catch (e) {
    ElMessage.error('快速提取失败：' + (e.response?.data?.message || e.message))
    emit('error', e)
  } finally {
    loading.value = false
  }
  emit('click')
}
</script>
