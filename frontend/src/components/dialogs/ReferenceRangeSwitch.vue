<template>
  <el-switch
    :model-value="modelValue"
    @update:model-value="$emit('update:modelValue', $event)"
    @change="$emit('change', $event)"
    active-text="显示参考值"
    inactive-text="隐藏参考值"
  />
</template>

<script setup>
defineProps({
  modelValue: {
    type: Boolean,
    default: true
  }
})

defineEmits(['update:modelValue', 'change'])
</script>
