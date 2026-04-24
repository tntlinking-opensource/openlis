<template>
  <div class="system-setting-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>6.1 工程师系统设置</span>
        </div>
      </template>
      
      <el-form :model="formData" label-width="120px" v-loading="loading">
        <!-- bgxt_xtsz 设置项 -->
        <el-divider content-position="left">系统设置项</el-divider>
        <el-table :data="bgxtSettings" border style="width: 100%; margin-bottom: 20px;">
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="mc" label="名称">
            <template #default="scope">
              <el-input v-model="scope.row.mc" size="small" />
            </template>
          </el-table-column>
          <el-table-column prop="bz" label="标志" width="100">
            <template #default="scope">
              <el-input-number v-model="scope.row.bz" size="small" :min="0" />
            </template>
          </el-table-column>
          <el-table-column prop="sm" label="说明">
            <template #default="scope">
              <el-input v-model="scope.row.sm" size="small" />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="100">
            <template #default="scope">
              <el-button type="danger" size="small" @click="deleteBgxtSetting(scope.row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-button type="primary" @click="addBgxtSetting">新增设置项</el-button>
        
        <!-- sys_xtsz 系统设置 -->
        <el-divider content-position="left">系统参数设置</el-divider>
        <el-form-item label="联网标志">
          <el-switch v-model="formData.sysSetting.lwbz" />
        </el-form-item>
        <el-form-item label="补写条">
          <el-switch v-model="formData.sysSetting.bxt" />
        </el-form-item>
        <el-form-item label="住院费用控制">
          <el-input-number v-model="formData.sysSetting.zyfykz" :min="0" />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleSave" :loading="saving">保存</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const loading = ref(false)
const saving = ref(false)
const bgxtSettings = ref([])
const formData = reactive({
  sysSetting: {
    lwbz: false,
    bxt: false,
    zyfykz: 0
  }
})

const loadSettings = async () => {
  loading.value = true
  try {
    const response = await axios.get('/api/system/setting/list')
    if (response.data.bgxtSettings) {
      bgxtSettings.value = response.data.bgxtSettings
    }
    if (response.data.sysSetting) {
      Object.assign(formData.sysSetting, response.data.sysSetting)
    }
  } catch (error) {
    ElMessage.error('加载设置失败：' + error.message)
  } finally {
    loading.value = false
  }
}

const handleSave = async () => {
  saving.value = true
  try {
    const response = await axios.post('/api/system/setting/save', {
      bgxtSettings: bgxtSettings.value,
      sysSetting: formData.sysSetting
    })
    if (response.data.success) {
      ElMessage.success('保存成功')
      await loadSettings()
    } else {
      ElMessage.error(response.data.message)
    }
  } catch (error) {
    ElMessage.error('保存失败：' + error.message)
  } finally {
    saving.value = false
  }
}

const handleReset = () => {
  loadSettings()
}

const addBgxtSetting = () => {
  bgxtSettings.value.push({
    id: null,
    mc: '',
    bz: 0,
    sm: ''
  })
}

const deleteBgxtSetting = (row) => {
  const index = bgxtSettings.value.indexOf(row)
  if (index > -1) {
    bgxtSettings.value.splice(index, 1)
  }
}

onMounted(() => {
  loadSettings()
})
</script>

<style scoped>
.system-setting-container {
  padding: 20px;
}

.card-header {
  font-size: 16px;
  font-weight: bold;
}
</style>

