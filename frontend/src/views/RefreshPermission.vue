<template>
  <div class="refresh-permission-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>6.3 刷新权限</span>
        </div>
      </template>
      
      <el-alert
        title="当前操作员被赋予新的权限后，可点击刷新权限，无需退出程序重新登录"
        type="info"
        :closable="false"
        style="margin-bottom: 20px;"
      />
      
      <el-form :model="refreshForm" label-width="120px" style="max-width: 500px;">
        <el-form-item label="用户名">
          <el-input v-model="refreshForm.username" placeholder="请输入操作员代码（留空则使用当前登录用户）" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleRefresh" :loading="refreshing">刷新权限</el-button>
        </el-form-item>
      </el-form>
      
      <el-divider content-position="left">当前权限信息</el-divider>
      <el-descriptions :column="2" border v-if="permissions">
        <el-descriptions-item label="操作员代码">{{ permissions.czydm }}</el-descriptions-item>
        <el-descriptions-item label="操作员姓名">{{ permissions.czyxm }}</el-descriptions-item>
        <el-descriptions-item label="科室代码">{{ permissions.ksdm || '-' }}</el-descriptions-item>
        <el-descriptions-item label="工作组代码">{{ permissions.gzzdm || '-' }}</el-descriptions-item>
        <el-descriptions-item label="医生标志">
          <el-tag :type="permissions.ysbz ? 'success' : 'info'">
            {{ permissions.ysbz ? '是' : '否' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="操作员标志">
          <el-tag :type="permissions.czybz ? 'success' : 'info'">
            {{ permissions.czybz ? '是' : '否' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="管理员标志">
          <el-tag :type="permissions.glybz ? 'success' : 'warning'">
            {{ permissions.glybz ? '是' : '否' }}
          </el-tag>
        </el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const refreshing = ref(false)
const permissions = ref(null)

const refreshForm = reactive({
  username: ''
})

const handleRefresh = async () => {
  // 如果未输入用户名，尝试使用当前登录用户（需要从store或localStorage获取）
  const username = refreshForm.username || getCurrentUser()
  if (!username) {
    ElMessage.warning('请输入用户名或先登录系统')
    return
  }
  
  refreshing.value = true
  try {
    const response = await axios.post('/api/system/permission/refresh', { username })
    if (response.data.success) {
      ElMessage.success('权限刷新成功')
      permissions.value = response.data.permissions
      // TODO: 实际应用中，这里应该更新前端的用户权限状态
    } else {
      ElMessage.error(response.data.message)
    }
  } catch (error) {
    ElMessage.error('刷新权限失败：' + error.message)
  } finally {
    refreshing.value = false
  }
}

const getCurrentUser = () => {
  // TODO: 从store或localStorage获取当前登录用户
  // 临时返回null，需要登录模块完善后实现
  return null
}
</script>

<style scoped>
.refresh-permission-container {
  padding: 20px;
}

.card-header {
  font-size: 16px;
  font-weight: bold;
}
</style>

