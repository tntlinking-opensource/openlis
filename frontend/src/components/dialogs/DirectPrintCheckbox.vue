<template>
  <el-checkbox 
    :model-value="modelValue" 
    @update:model-value="$emit('update:modelValue', $event)"
    @change="$emit('change', $event)"
  >
    直接打印
  </el-checkbox>
</template>

<script setup>
defineProps({
  modelValue: {
    type: Boolean,
    default: false
  }
})

defineEmits(['update:modelValue', 'change'])
</script>
