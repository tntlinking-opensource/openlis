<template>
  <el-dialog 
    :model-value="visible" 
    @update:model-value="$emit('update:visible', $event)" 
    title="打印进度" 
    width="450px" 
    :close-on-click-modal="false" 
    :show-close="false"
    append-to-body
  >
    <div class="print-progress-dialog">
      <div class="progress-info">
        <span>正在打印 {{ current }} / {{ total }}</span>
        <span class="percentage">{{ percentage }}%</span>
      </div>
      
      <el-progress 
        :percentage="percentage" 
        :status="progressStatus" 
        :stroke-width="20"
      />
      
      <div class="current-item" v-if="currentItem">
        <span>当前: {{ currentItem.brxm || '未知' }} ({{ currentItem.syh || currentItem.barcode }})</span>
      </div>
      
      <div class="progress-text" v-if="statusText">
        {{ statusText }}
      </div>
    </div>
    
    <template #footer v-if="showCancel">
      <el-button @click="handleCancel" :disabled="completing">取消</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, watch } from 'vue'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  total: {
    type: Number,
    default: 0
  },
  current: {
    type: Number,
    default: 0
  },
  currentItem: {
    type: Object,
    default: null
  },
  showCancel: {
    type: Boolean,
    default: true
  }
})

const emit = defineEmits(['update:visible', 'complete', 'cancel'])

const completing = ref(false)

const percentage = computed(() => {
  if (props.total === 0) return 0
  return Math.round((props.current / props.total) * 100)
})

const progressStatus = computed(() => {
  if (percentage.value >= 100) return 'success'
  return ''
})

const statusText = computed(() => {
  if (props.current >= props.total) {
    completing.value = true
    return '打印完成'
  }
  return '正在处理...'
})

watch(() => props.current, (val) => {
  if (val >= props.total && props.total > 0) {
    setTimeout(() => {
      emit('complete')
      emit('update:visible', false)
    }, 1000)
  }
})

const handleCancel = () => {
  emit('cancel')
  emit('update:visible', false)
}
</script>

<style scoped>
.print-progress-dialog {
  padding: 10px 0;
}

.progress-info {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
  font-size: 14px;
}

.percentage {
  font-weight: bold;
  color: #409eff;
}

.current-item {
  margin-top: 12px;
  font-size: 13px;
  color: #606266;
}

.progress-text {
  margin-top: 8px;
  text-align: center;
  font-size: 13px;
  color: #909399;
}
</style>
