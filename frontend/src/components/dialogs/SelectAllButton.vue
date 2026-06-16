<template>
  <el-button size="small" @click="handleSelectAll" :disabled="disabled">
    全选
  </el-button>
</template>

<script setup>
const props = defineProps({
  disabled: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['select'])

const handleSelectAll = () => {
  emit('select', 'all')
}
</script>
