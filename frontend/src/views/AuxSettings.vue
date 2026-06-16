<template>
  <div class="aux-settings">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>辅助功能设置</span>
        </div>
      </template>
      
      <el-form :model="settings" label-width="140px">
        <el-form-item label="自动保存">
          <el-switch v-model="settings.autoSave" @change="handleChange('autoSave')" />
        </el-form-item>
        
        <el-form-item label="自动提取结果">
          <el-switch v-model="settings.autoExtract" @change="handleChange('autoExtract')" />
        </el-form-item>
        
        <el-form-item label="打印即审核">
          <el-switch v-model="settings.printThenAudit" @change="handleChange('printThenAudit')" />
        </el-form-item>
        
        <el-form-item label="显示参考值">
          <el-switch v-model="settings.showReferenceRange" @change="handleChange('showReferenceRange')" />
        </el-form-item>
        
        <el-form-item label="直接打印">
          <el-switch v-model="settings.directPrint" @change="handleChange('directPrint')" />
        </el-form-item>
        
        <el-form-item label="自动增加">
          <el-switch v-model="settings.autoIncrement" @change="handleChange('autoIncrement')" />
        </el-form-item>
        
        <el-form-item label="项目继承">
          <el-switch v-model="settings.itemInherit" @change="handleChange('itemInherit')" />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleSave" :loading="saving">保存设置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const settings = ref({
  autoSave: false,
  autoExtract: false,
  printThenAudit: false,
  showReferenceRange: true,
  directPrint: false,
  autoIncrement: false,
  itemInherit: false
})

const saving = ref(false)

const loadSettings = async () => {
  try {
    const { data } = await axios.get('/api/system/setting/auxSettings')
    if (data) {
      settings.value = { ...settings.value, ...data }
    }
  } catch (e) {
    console.error('加载辅助设置失败:', e)
  }
}

const handleChange = (key) => {
  // Real-time feedback without saving
}

const handleSave = async () => {
  saving.value = true
  try {
    const { data } = await axios.put('/api/system/setting/auxSettings', settings.value)
    if (data.success) {
      ElMessage.success(data.message || '设置已保存')
    } else {
      ElMessage.error(data.message || '保存失败')
    }
  } catch (e) {
    ElMessage.error('保存失败：' + (e.response?.data?.message || e.message))
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  loadSettings()
})
</script>

<style scoped>
.aux-settings {
  padding: 20px;
}

.card-header {
  font-size: 16px;
  font-weight: bold;
}
</style>
