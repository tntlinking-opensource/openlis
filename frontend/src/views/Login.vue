<template>
  <div class="login-container">
    <div class="login-box">
      <h2>实验室报告系统</h2>
      <el-form :model="loginForm" :rules="rules" ref="loginFormRef" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入操作员代码" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" @keyup.enter="handleLogin" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleLogin" :loading="loading" style="width: 100%">登录</el-button>
        </el-form-item>
      </el-form>
      <div class="version-info">版本号：{{ version }}</div>
    </div>
    
    <div class="footer">
      Copyright © 2025 湖北天天数链技术有限公司
      <div style="margin-top: 2px;">
        <a
          href="https://open.tntlinking.com/communityTreaty"
          target="_blank"
          rel="noopener noreferrer"
          style="
            color: #667eea;
            text-decoration: none;
            font-size: 12px;
          ">
          本系统软件源代码许可来源于《天天开源软件（社区版）许可协议》
        </a>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const router = useRouter()
const loginFormRef = ref(null)
const loading = ref(false)
const version = ref('1.0.0') // 默认版本号，可以从package.json或后端获取

const loginForm = reactive({
  username: '',
  password: ''
})

// 获取版本号（可以从后端API获取，或从package.json读取）
onMounted(() => {
  // 可以调用后端API获取版本号
  // axios.get('/api/system/version').then(res => { version.value = res.data.version })
  // 或者从package.json读取（需要配置）
  version.value = '1.0.0'
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  await loginFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    loading.value = true
    try {
      const response = await axios.post('/api/auth/login', loginForm)
      if (response.data.success) {
        ElMessage.success('登录成功')
        // 保存用户信息到 localStorage
        if (response.data.user) {
          localStorage.setItem('user', JSON.stringify(response.data.user))
        }
        router.push('/main')
      } else {
        ElMessage.error(response.data.message || '登录失败')
      }
    } catch (error) {
      ElMessage.error('登录失败：' + (error.response?.data?.message || error.message))
    } finally {
      loading.value = false
    }
  })
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: #f5f5f5;
  position: relative;
}

.login-box {
  width: 400px;
  padding: 40px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.login-box h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
}

.version-info {
  text-align: center;
  margin-top: 20px;
  font-size: 12px;
  color: #999;
}

.footer {
  position: absolute;
  bottom: 20px;
  right: 20px;
  text-align: right;
  font-size: 12px;
  color: #666;
}

.footer a:hover {
  text-decoration: underline;
}
</style>

