<template>
  <FloatingPanel
    v-model="visible"
    title="系统锁定"
    :width="450"
    :height="280"
    :resizable="false"
    :closable="false"
    class="system-lock-panel"
    @close="handleCloseAttempt"
  >
    <div class="dialog-content">
      <p>当前屏幕已经锁定，软件系统并未关闭，请输入密码后回车即返回操作界面</p>
      <el-form :model="lockForm" label-width="80px">
        <el-form-item label="操作员:">
          <el-input v-model="lockForm.operator" disabled size="small" />
        </el-form-item>
        <el-form-item label="密码:">
          <el-input
            v-model="lockForm.password"
            type="password"
            size="small"
            @keyup.enter="handleUnlock"
            autofocus
          />
        </el-form-item>
        <el-form-item label="当前操作员:">
          <el-input v-model="lockForm.currentOperator" disabled size="small" />
        </el-form-item>
      </el-form>
    </div>
    
    <div class="panel-footer">
      <el-button size="small" @click="handleExit">退出程序</el-button>
      <el-button size="small" type="primary" @click="handleUnlock">解锁</el-button>
    </div>
  </FloatingPanel>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import FloatingPanel from '@/components/FloatingPanel.vue'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  currentUser: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['update:modelValue'])
const router = useRouter()

// 系统锁定：使用ref直接控制，不允许通过v-model关闭
const isUnlocked = ref(false)

const visible = computed({
  get: () => props.modelValue,
  set: (val) => {
    // 只有在解锁成功后才允许关闭
    if (!val && !isUnlocked.value) {
      // 尝试关闭但未解锁，阻止关闭
      ElMessage.warning('系统已锁定，请输入密码解锁')
      return
    }
    emit('update:modelValue', val)
  }
})

const lockForm = ref({
  operator: '',
  password: '',
  currentOperator: ''
})

// 监听对话框打开，填充当前操作员信息
watch(visible, (newVal) => {
  if (newVal) {
    // 重置解锁状态
    isUnlocked.value = false
    // 填充当前操作员信息
    if (props.currentUser) {
      lockForm.value.operator = props.currentUser.czydm || ''
      lockForm.value.currentOperator = props.currentUser.czyxm || props.currentUser.czydm || ''
    } else {
      // 从localStorage获取
      const userStr = localStorage.getItem('user')
      if (userStr) {
        const user = JSON.parse(userStr)
        lockForm.value.operator = user.czydm || ''
        lockForm.value.currentOperator = user.czyxm || user.czydm || ''
      }
    }
    lockForm.value.password = ''
  }
})

const handleUnlock = async () => {
  if (!lockForm.value.password) {
    ElMessage.warning('请输入密码')
    return
  }
  
  try {
    const payload = {
      username: lockForm.value.operator,
      password: lockForm.value.password
    }
    console.log('解锁请求:', payload) // 调试信息
    const res = await axios.post('/api/system/lock/unlock', payload)
    console.log('解锁响应:', res.data) // 调试信息
    if (res.data?.success) {
      ElMessage.success('解锁成功')
      isUnlocked.value = true // 标记已解锁
      visible.value = false
      lockForm.value.password = ''
    } else {
      ElMessage.error(res.data?.message || '密码错误，解锁失败')
      lockForm.value.password = ''
    }
  } catch (e) {
    console.error('解锁异常:', e) // 调试信息
    ElMessage.error('解锁失败：' + (e.response?.data?.message || e.message))
    lockForm.value.password = ''
  }
}

const handleExit = () => {
  router.push('/login')
}

const handleCloseAttempt = (e) => {
  // 系统锁定不允许直接关闭，必须通过密码解锁
  // 阻止任何关闭操作
  e?.preventDefault?.()
  ElMessage.warning('系统已锁定，请输入密码解锁')
  // 确保面板保持打开状态
  if (!visible.value) {
    visible.value = true
  }
}
</script>

<style scoped>
.system-lock-panel :deep(.panel-content) {
  padding: 16px;
  background: #ece9d8;
  color: #000;
}

.dialog-content p {
  margin-bottom: 16px;
  font-size: 12px;
  color: #000;
}

.dialog-content :deep(.el-form-item__label) {
  color: #000;
  font-size: 12px;
}

.panel-footer {
  padding: 8px 16px;
  background: #ece9d8;
  border-top: 1px solid #8b8b8b;
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}
</style>

