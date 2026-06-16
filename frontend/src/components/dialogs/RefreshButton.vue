<template>
  <el-button 
    :loading="loading" 
    @click="$emit('click')" 
    size="default"
    :disabled="loading"
    title="刷新"
  >
    <slot>
      <span>刷新</span>
    </slot>
  </el-button>
</template>

<script setup>
defineProps({
  loading: {
    type: Boolean,
    default: false
  }
})

defineEmits(['click'])
</script>
