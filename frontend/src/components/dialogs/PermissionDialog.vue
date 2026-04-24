<template>
  <el-dialog
    v-model="visible"
    title="刷新权限"
    width="400px"
    :close-on-click-modal="false"
  >
    <div class="dialog-content">
      <p>当前操作员被赋予的新的权限后可点击刷新权限，则不用退出程序重新登录</p>
      <el-button type="primary" @click="handleRefresh">刷新权限</el-button>
    </div>
    <template #footer>
      <el-button @click="handleClose">关闭</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:modelValue'])

const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const handleRefresh = () => {
  // TODO: 刷新权限逻辑
  visible.value = false
}

const handleClose = () => {
  visible.value = false
}
</script>

