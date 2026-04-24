<template>
  <Teleport to="body">
    <!-- 透明遮罩层：阻止背景点击但不遮挡视觉 -->
    <div
      v-if="modelValue"
      class="floating-panel-overlay"
      :style="overlayStyle"
      @mousedown.stop="bringToFront"
    ></div>
    
    <div
      v-if="modelValue"
      ref="panelRef"
      class="floating-panel"
      :class="{ minimized: isMinimized }"
      :style="panelStyle"
      @mousedown.stop="bringToFront"
    >
      <!-- 标题栏（可拖动区域） -->
      <div
        class="panel-header"
        :class="{ dragging: isDragging }"
        @mousedown="startDrag"
      >
        <span class="panel-title">{{ title }}</span>
        <div class="panel-actions">
          <el-button
            link
            size="small"
            @click="toggleMinimize"
            title="最小化"
          >
            <el-icon><Minus /></el-icon>
          </el-button>
          <el-button
            v-if="closable"
            link
            size="small"
            @click="handleClose"
            title="关闭"
          >
            <el-icon><Close /></el-icon>
          </el-button>
        </div>
      </div>

      <!-- 内容区域 -->
      <div v-if="!isMinimized" class="panel-content" :style="contentStyle">
        <slot />
      </div>

      <!-- 调整大小手柄 -->
      <div
        v-if="!isMinimized && resizable"
        class="resize-handle"
        @mousedown.stop="startResize"
      ></div>
    </div>
  </Teleport>
</template>

<script setup>
import { ref, computed, watch, onMounted, onUnmounted } from 'vue'
import { Minus, Close } from '@element-plus/icons-vue'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  title: {
    type: String,
    default: '面板'
  },
  width: {
    type: [String, Number],
    default: 1000
  },
  height: {
    type: [String, Number],
    default: 600
  },
  resizable: {
    type: Boolean,
    default: true
  },
  draggable: {
    type: Boolean,
    default: true
  },
  closable: {
    type: Boolean,
    default: true
  },
  minWidth: {
    type: Number,
    default: 400
  },
  minHeight: {
    type: Number,
    default: 300
  },
  initialX: {
    type: Number,
    default: null
  },
  initialY: {
    type: Number,
    default: null
  }
})

const emit = defineEmits(['update:modelValue', 'close'])

const panelRef = ref(null)
const isMinimized = ref(false)
const isDragging = ref(false)
const isResizing = ref(false)

// 面板位置和尺寸
const position = ref({
  x: props.initialX ?? (window.innerWidth - parseInt(props.width)) / 2,
  y: props.initialY ?? (window.innerHeight - parseInt(props.height)) / 2
})

const size = ref({
  width: parseInt(props.width),
  height: parseInt(props.height)
})

// 拖拽相关
const dragStart = ref({ x: 0, y: 0 })

// 调整大小相关
const resizeStart = ref({ x: 0, y: 0, width: 0, height: 0 })

// 计算样式
const overlayStyle = computed(() => {
  return {
    zIndex: zIndex.value - 1
  }
})

const panelStyle = computed(() => {
  if (isMinimized.value) {
    return {
      left: `${position.value.x}px`,
      top: `${position.value.y}px`,
      width: '300px',
      height: '40px', // 仅标题栏高度
      zIndex: zIndex.value
    }
  }
  return {
    left: `${position.value.x}px`,
    top: `${position.value.y}px`,
    width: `${size.value.width}px`,
    height: `${size.value.height}px`,
    zIndex: zIndex.value
  }
})

const contentStyle = computed(() => {
  return {
    height: `${size.value.height - 40}px` // 减去标题栏高度
  }
})

// Z-index 管理（确保当前面板在最前）
const zIndex = ref(1000)
let maxZIndex = 1000

const bringToFront = () => {
  maxZIndex += 1
  zIndex.value = maxZIndex
}

// 拖拽功能
const startDrag = (e) => {
  if (!props.draggable || isMinimized.value) return
  isDragging.value = true
  dragStart.value = {
    x: e.clientX - position.value.x,
    y: e.clientY - position.value.y
  }
  bringToFront()
  document.addEventListener('mousemove', onDrag)
  document.addEventListener('mouseup', stopDrag)
  e.preventDefault()
}

const onDrag = (e) => {
  if (!isDragging.value) return
  const newX = e.clientX - dragStart.value.x
  const newY = e.clientY - dragStart.value.y
  
  // 限制在视窗内
  const maxX = window.innerWidth - size.value.width
  const maxY = window.innerHeight - (isMinimized.value ? 40 : size.value.height)
  
  position.value.x = Math.max(0, Math.min(newX, maxX))
  position.value.y = Math.max(0, Math.min(newY, maxY))
}

const stopDrag = () => {
  isDragging.value = false
  document.removeEventListener('mousemove', onDrag)
  document.removeEventListener('mouseup', stopDrag)
}

// 调整大小功能
const startResize = (e) => {
  if (!props.resizable) return
  isResizing.value = true
  resizeStart.value = {
    x: e.clientX,
    y: e.clientY,
    width: size.value.width,
    height: size.value.height
  }
  bringToFront()
  document.addEventListener('mousemove', onResize)
  document.addEventListener('mouseup', stopResize)
  e.preventDefault()
  e.stopPropagation()
}

const onResize = (e) => {
  if (!isResizing.value) return
  const deltaX = e.clientX - resizeStart.value.x
  const deltaY = e.clientY - resizeStart.value.y
  
  const newWidth = Math.max(props.minWidth, resizeStart.value.width + deltaX)
  const newHeight = Math.max(props.minHeight, resizeStart.value.height + deltaY)
  
  // 限制在视窗内
  const maxWidth = window.innerWidth - position.value.x
  const maxHeight = window.innerHeight - position.value.y
  
  size.value.width = Math.min(newWidth, maxWidth)
  size.value.height = Math.min(newHeight, maxHeight)
}

const stopResize = () => {
  isResizing.value = false
  document.removeEventListener('mousemove', onResize)
  document.removeEventListener('mouseup', stopResize)
}

// 最小化功能
const toggleMinimize = () => {
  isMinimized.value = !isMinimized.value
  if (!isMinimized.value) {
    bringToFront()
  }
}

// 关闭功能
const handleClose = () => {
  // 如果不可关闭，只触发 close 事件让父组件处理，不关闭面板
  if (!props.closable) {
    emit('close')
    return
  }
  // 可关闭时，正常关闭
  emit('update:modelValue', false)
  emit('close')
}

// 监听 props 变化
watch(() => props.modelValue, (newVal) => {
  if (newVal) {
    bringToFront()
    isMinimized.value = false
  }
})

watch(() => props.width, (newVal) => {
  if (!isMinimized.value) {
    size.value.width = parseInt(newVal)
  }
})

watch(() => props.height, (newVal) => {
  if (!isMinimized.value) {
    size.value.height = parseInt(newVal)
  }
})

// 清理
onUnmounted(() => {
  stopDrag()
  stopResize()
})
</script>

<style scoped>
.floating-panel-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: transparent;
  pointer-events: auto;
  z-index: 999;
  /* 阻止背景点击，但不遮挡视觉 */
  cursor: default;
}

.floating-panel {
  position: fixed;
  background: #fff;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  user-select: none;
}

.floating-panel.minimized {
  height: auto;
}

.panel-header {
  height: 36px;
  background: linear-gradient(to bottom, #f8f9fa, #e9ecef);
  border-bottom: 1px solid #dee2e6;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 10px;
  cursor: move;
  flex-shrink: 0;
  user-select: none;
}

.panel-header.dragging {
  cursor: grabbing;
}

.panel-title {
  font-weight: 600;
  color: #212529;
  font-size: 13px;
  letter-spacing: 0.3px;
}

.panel-actions {
  display: flex;
  gap: 4px;
}

.panel-actions .el-button {
  padding: 2px 6px;
  color: #6c757d;
  margin-left: 4px;
}

.panel-actions .el-button:hover {
  color: #495057;
  background: rgba(0, 0, 0, 0.05);
}

.panel-content {
  flex: 1;
  overflow: auto;
  padding: 16px;
}

.resize-handle {
  position: absolute;
  right: 0;
  bottom: 0;
  width: 16px;
  height: 16px;
  cursor: nwse-resize;
  background: linear-gradient(
    -45deg,
    transparent 0%,
    transparent 40%,
    #c0c4cc 40%,
    #c0c4cc 45%,
    transparent 45%,
    transparent 55%,
    #c0c4cc 55%,
    #c0c4cc 60%,
    transparent 60%
  );
  z-index: 10;
}

.resize-handle:hover {
  background: linear-gradient(
    -45deg,
    transparent 0%,
    transparent 40%,
    #409eff 40%,
    #409eff 45%,
    transparent 45%,
    transparent 55%,
    #409eff 55%,
    #409eff 60%,
    transparent 60%
  );
}
</style>

