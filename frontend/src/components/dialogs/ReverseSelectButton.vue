<template>
  <el-button size="small" @click="handleReverseSelect" :disabled="disabled">
    反选
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

const handleReverseSelect = () => {
  emit('select', 'reverse')
}
</script>
