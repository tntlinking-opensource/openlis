<template>
  <FloatingPanel
    v-model="visible"
    title="密码修改"
    :width="450"
    :height="320"
    :resizable="false"
    class="password-change-panel"
  >
    <div class="dialog-content">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="操作员代码:">
          <el-input v-model="form.czydm" disabled size="small" />
        </el-form-item>
        <el-form-item label="操作员姓名:">
          <el-input v-model="form.czyxm" disabled size="small" />
        </el-form-item>
        <el-form-item label="原密码:" prop="oldPassword">
          <el-input
            v-model="form.oldPassword"
            type="password"
            size="small"
            placeholder="请输入原密码"
            autofocus
          />
        </el-form-item>
        <el-form-item label="新密码:" prop="newPassword">
          <el-input
            v-model="form.newPassword"
            type="password"
            size="small"
            placeholder="请输入新密码"
          />
        </el-form-item>
        <el-form-item label="确认新密码:" prop="confirmPassword">
          <el-input
            v-model="form.confirmPassword"
            type="password"
            size="small"
            placeholder="请再次输入新密码"
            @keyup.enter="handleSave"
          />
        </el-form-item>
      </el-form>
    </div>
    
    <div class="panel-footer">
      <el-button size="small" @click="handleCancel">取消</el-button>
      <el-button size="small" type="primary" @click="handleSave">确定</el-button>
    </div>
  </FloatingPanel>
</template>

<script setup>
import { computed, reactive, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import FloatingPanel from '@/components/FloatingPanel.vue'

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

const formRef = ref(null)

const form = reactive({
  czydm: '',
  czyxm: '',
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 验证规则
const validateConfirmPassword = (rule, value, callback) => {
  if (value !== form.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

// 监听对话框打开，填充当前用户信息
watch(visible, (newVal) => {
  if (newVal) {
    const userStr = localStorage.getItem('user')
    if (userStr) {
      const user = JSON.parse(userStr)
      form.czydm = user.czydm || ''
      form.czyxm = user.czyxm || user.czydm || ''
    }
    form.oldPassword = ''
    form.newPassword = ''
    form.confirmPassword = ''
    // 清除表单验证状态
    if (formRef.value) {
      formRef.value.clearValidate()
    }
  }
})

const handleSave = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    const payload = {
      czydm: form.czydm,
      oldPassword: form.oldPassword,
      newPassword: form.newPassword
    }
    
    const res = await axios.post('/api/system/password/change', payload)
    if (res.data?.success) {
      ElMessage.success(res.data.message || '密码修改成功')
      visible.value = false
      // 清空表单
      form.oldPassword = ''
      form.newPassword = ''
      form.confirmPassword = ''
    } else {
      ElMessage.error(res.data?.message || '密码修改失败')
    }
  } catch (e) {
    if (e.response?.data?.message) {
      ElMessage.error(e.response.data.message)
    } else if (e.message && !e.message.includes('validation')) {
      ElMessage.error('密码修改失败：' + e.message)
    }
    // 验证失败时不显示错误（Element Plus会自动显示）
  }
}

const handleCancel = () => {
  visible.value = false
}
</script>

<style scoped>
.password-change-panel :deep(.panel-content) {
  padding: 16px;
  background: #fff;
}

.panel-footer {
  padding: 8px 16px;
  background: #f5f7fa;
  border-top: 1px solid #dcdfe6;
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}
</style>

