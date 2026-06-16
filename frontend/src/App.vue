<template>
  <router-view />
</template>

<script setup>
import { onMounted, onUnmounted } from 'vue'

const handleKeyDown = (event) => {
  if (event.key === 'Escape') {
    const activeDialogs = document.querySelectorAll('.el-dialog__wrapper')
    if (activeDialogs.length > 0) {
      return
    }
  }
}

onMounted(() => {
  document.addEventListener('keydown', handleKeyDown)
})

onUnmounted(() => {
  document.removeEventListener('keydown', handleKeyDown)
})
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Microsoft YaHei', Arial, sans-serif;
}
</style>

