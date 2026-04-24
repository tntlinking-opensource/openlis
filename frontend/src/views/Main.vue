<template>
  <div class="main-container">
    <!-- 顶部导航栏 -->
    <el-header class="header">
      <div class="logo">
        <span>LIS系统 - 实验室报告系统</span>
      </div>
      <div class="user-info">
        <span>当前用户：{{ currentUser?.czyxm || currentUser?.czydm || '未知' }}</span>
        <el-button type="danger" size="small" @click="handleLogout">退出登录</el-button>
      </div>
    </el-header>

    <el-container class="main-content">
      <!-- 侧边栏菜单 -->
      <el-aside width="200px" class="sidebar">
        <el-menu
          :default-active="activeMenu"
          router
          class="menu"
        >
          <!-- 样本管理 -->
          <el-menu-item index="/main/sample">
            <el-icon><Document /></el-icon>
            <span>二、样本管理</span>
          </el-menu-item>
          <!-- 质控管理 -->
          <el-menu-item index="/main/qc">
            <el-icon><DataAnalysis /></el-icon>
            <span>三、质控管理</span>
          </el-menu-item>
          <!-- 查询统计 -->
          <el-menu-item index="/main/query">
            <el-icon><Search /></el-icon>
            <span>五、查询统计</span>
          </el-menu-item>
          <!-- 系统设置 -->
          <el-menu-item index="/system/setting">
            <el-icon><Setting /></el-icon>
            <span>6.1 工程师系统设置</span>
          </el-menu-item>
          <el-menu-item index="/system/lock">
            <el-icon><Lock /></el-icon>
            <span>6.2 系统锁定</span>
          </el-menu-item>
          <el-menu-item index="/system/permission">
            <el-icon><Refresh /></el-icon>
            <span>6.3 刷新权限</span>
          </el-menu-item>
          <el-menu-item index="/system/common-code">
            <el-icon><Document /></el-icon>
            <span>6.4 通用编码设置</span>
          </el-menu-item>
          <el-menu-item index="/system/special-report">
            <el-icon><Files /></el-icon>
            <span>6.5 特殊报告设置</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <!-- 主内容区 -->
      <el-main class="content">
        <router-view v-if="$route.path !== '/main'" />
        <div v-else class="welcome">
          <h1>欢迎使用 LIS 系统</h1>
          <p>请从左侧菜单选择功能模块</p>
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Setting, Lock, Refresh, Document, Files, DataAnalysis, Search } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const currentUser = ref(null)

const activeMenu = computed(() => {
  return route.path
})

onMounted(() => {
  // 从 localStorage 获取用户信息
  const userStr = localStorage.getItem('user')
  if (userStr) {
    currentUser.value = JSON.parse(userStr)
  } else {
    // 如果没有用户信息，跳转到登录页
    router.push('/login')
  }
})

const handleLogout = () => {
  localStorage.removeItem('user')
  ElMessage.success('已退出登录')
  router.push('/login')
}
</script>

<style scoped>
.main-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.logo {
  font-size: 20px;
  font-weight: bold;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.main-content {
  flex: 1;
  overflow: hidden;
}

.sidebar {
  background: #f5f5f5;
  border-right: 1px solid #e0e0e0;
}

.menu {
  border-right: none;
  height: 100%;
}

.content {
  padding: 20px;
  background: #fff;
  overflow-y: auto;
}

.welcome {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #666;
}

.welcome h1 {
  margin-bottom: 20px;
  color: #333;
}

.welcome p {
  font-size: 16px;
}
</style>

