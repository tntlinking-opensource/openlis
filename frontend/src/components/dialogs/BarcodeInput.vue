<template>
  <el-input
    ref="inputRef"
    v-model="value"
    :placeholder="placeholder"
    :disabled="disabled"
    :clearable="clearable"
    @keyup.enter="handleEnter"
    @change="handleChange"
    class="barcode-input"
  >
    <template #prefix>
      <el-icon><Scanner /></el-icon>
    </template>
  </el-input>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  },
  placeholder: {
    type: String,
    default: '扫描或输入条码号'
  },
  disabled: {
    type: Boolean,
    default: false
  },
  clearable: {
    type: Boolean,
    default: true
  },
  autoFocus: {
    type: Boolean,
    default: true
  }
})

const emit = defineEmits(['update:modelValue', 'scan', 'enter', 'change'])

const inputRef = ref(null)
const value = ref(props.modelValue)

let barcodeBuffer = ''
let barcodeTimer = null

const handleBarcodeScanner = (event) => {
  if (event.key === 'Enter') {
    if (barcodeBuffer.length > 0) {
      emit('scan', barcodeBuffer)
      emit('update:modelValue', barcodeBuffer)
      barcodeBuffer = ''
    }
    return
  }
  
  if (event.key.length === 1 && /[a-zA-Z0-9]/.test(event.key)) {
    barcodeBuffer += event.key
    
    if (barcodeTimer) {
      clearTimeout(barcodeTimer)
    }
    
    barcodeTimer = setTimeout(() => {
      if (barcodeBuffer.length >= 6) {
        emit('scan', barcodeBuffer)
        emit('update:modelValue', barcodeBuffer)
      }
      barcodeBuffer = ''
    }, 100)
  }
}

const handleEnter = () => {
  if (value.value) {
    emit('scan', value.value)
    emit('enter', value.value)
  }
}

const handleChange = (val) => {
  emit('change', val)
}

const focus = () => {
  inputRef.value?.focus()
}

onMounted(() => {
  if (props.autoFocus) {
    setTimeout(() => {
      focus()
    }, 100)
  }
  document.addEventListener('keydown', handleBarcodeScanner)
})

onUnmounted(() => {
  document.removeEventListener('keydown', handleBarcodeScanner)
  if (barcodeTimer) {
    clearTimeout(barcodeTimer)
  }
})

defineExpose({
  focus,
  inputRef
})
</script>

<style scoped>
.barcode-input {
  max-width: 400px;
}
</style>
