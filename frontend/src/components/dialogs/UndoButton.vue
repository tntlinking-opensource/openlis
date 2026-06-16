<template>
  <el-button 
    :disabled="disabled || !canUndo" 
    @click="handleUndo" 
    size="default"
    title="撤销 (Ctrl+Z)"
  >
    <slot>
      <span>撤销</span>
    </slot>
  </el-button>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'

const props = defineProps({
  disabled: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['click', 'change'])

const historyStack = ref([])
const MAX_HISTORY = 50

const canUndo = computed(() => historyStack.value.length > 0)

const pushState = (state) => {
  if (historyStack.value.length >= MAX_HISTORY) {
    historyStack.value.shift()
  }
  historyStack.value.push(state)
  emit('change', { canUndo: canUndo.value, canRedo: false })
}

const undo = () => {
  if (historyStack.value.length === 0) {
    ElMessage.info('没有可撤销的操作')
    return null
  }
  const state = historyStack.value.pop()
  emit('change', { canUndo: canUndo.value, canRedo: true })
  return state
}

const clearHistory = () => {
  historyStack.value = []
  emit('change', { canUndo: false, canRedo: false })
}

const handleUndo = () => {
  const state = undo()
  if (state !== null) {
    emit('click', state)
  }
}

defineExpose({
  pushState,
  undo,
  clearHistory,
  canUndo
})
</script>
