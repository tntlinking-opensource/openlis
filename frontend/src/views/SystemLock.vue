<template>
  <div class="system-lock-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>6.2 系统锁定</span>
        </div>
      </template>
      
      <el-alert
        title="操作员离开电脑时，可进行系统锁定，避免他人修改信息"
        type="info"
        :closable="false"
        style="margin-bottom: 20px;"
      />
      
      <div style="text-align: center; padding: 40px 0;">
        <el-button type="primary" size="large" @click="showLockDialog = true" :disabled="isLocked">
          锁定系统
        </el-button>
        <el-button type="warning" size="large" @click="showUnlockDialog = true" :disabled="!isLocked" style="margin-left: 20px;">
          解锁系统
        </el-button>
      </div>
      
      <!-- 锁定对话框 -->
      <el-dialog v-model="showLockDialog" title="系统锁定" width="400px" :close-on-click-modal="false">
        <el-form :model="lockForm" label-width="100px">
          <el-form-item label="用户名">
            <el-input v-model="lockForm.username" placeholder="请输入操作员代码" />
          </el-form-item>
          <el-form-item label="密码">
            <el-input v-model="lockForm.password" type="password" placeholder="请输入密码" @keyup.enter="handleLock" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="showLockDialog = false">取消</el-button>
          <el-button type="primary" @click="handleLock" :loading="locking">确认锁定</el-button>
        </template>
      </el-dialog>
      
      <!-- 解锁对话框 -->
      <el-dialog v-model="showUnlockDialog" title="系统解锁" width="400px" :close-on-click-modal="false">
        <el-form :model="unlockForm" label-width="100px">
          <el-form-item label="用户名">
            <el-input v-model="unlockForm.username" placeholder="请输入操作员代码" />
          </el-form-item>
          <el-form-item label="密码">
            <el-input v-model="unlockForm.password" type="password" placeholder="请输入密码" @keyup.enter="handleUnlock" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="showUnlockDialog = false">取消</el-button>
          <el-button type="primary" @click="handleUnlock" :loading="unlocking">确认解锁</el-button>
        </template>
      </el-dialog>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const locking = ref(false)
const unlocking = ref(false)
const isLocked = ref(false) // 系统锁定状态
const showLockDialog = ref(false)
const showUnlockDialog = ref(false)

const lockForm = reactive({
  username: '',
  password: ''
})

const unlockForm = reactive({
  username: '',
  password: ''
})

const handleLock = async () => {
  if (!lockForm.username || !lockForm.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  
  locking.value = true
  try {
    const response = await axios.post('/api/system/lock/lock', lockForm)
    if (response.data.success) {
      ElMessage.success('系统已锁定')
      isLocked.value = true
      showLockDialog.value = false
      // 清空表单
      lockForm.username = ''
      lockForm.password = ''
    } else {
      ElMessage.error(response.data.message)
    }
  } catch (error) {
    ElMessage.error('锁定失败：' + error.message)
  } finally {
    locking.value = false
  }
}

const handleUnlock = async () => {
  if (!unlockForm.username || !unlockForm.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  
  unlocking.value = true
  try {
    const response = await axios.post('/api/system/lock/unlock', unlockForm)
    if (response.data.success) {
      ElMessage.success('系统已解锁')
      isLocked.value = false
      showUnlockDialog.value = false
      // 清空表单
      unlockForm.username = ''
      unlockForm.password = ''
    } else {
      ElMessage.error(response.data.message)
    }
  } catch (error) {
    ElMessage.error('解锁失败：' + error.message)
  } finally {
    unlocking.value = false
  }
}
</script>

<style scoped>
.system-lock-container {
  padding: 20px;
}

.card-header {
  font-size: 16px;
  font-weight: bold;
}
</style>

