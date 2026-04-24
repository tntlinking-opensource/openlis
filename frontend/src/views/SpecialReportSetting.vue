<template>
  <div class="special-report-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>6.5 特殊报告设置</span>
        </div>
      </template>
      
      <el-row :gutter="20">
        <!-- 左侧：报告类型列表 -->
        <el-col :span="8">
          <el-card>
            <template #header>
              <span>报告类型</span>
            </template>
            <el-table :data="reportTypeList" border highlight-current-row @current-change="handleReportTypeSelect">
              <el-table-column prop="hbmc" label="报告名称" />
            </el-table>
          </el-card>
        </el-col>
        
        <!-- 右侧：关联的项目列表 -->
        <el-col :span="16">
          <el-card>
            <template #header>
              <div style="display: flex; justify-content: space-between; align-items: center;">
                <span>关联项目（{{ selectedReportType?.hbmc || '请选择报告类型' }}）</span>
                <el-button type="primary" size="small" @click="showAssociateDialog = true" :disabled="!selectedReportType">
                  关联项目
                </el-button>
              </div>
            </template>
            <el-table :data="itemList" border v-loading="loading">
              <el-table-column prop="xmid" label="项目ID" width="100" />
              <el-table-column prop="xmzwmc" label="中文名称" />
              <el-table-column prop="xmywmc" label="英文名称" />
              <el-table-column prop="xmdw" label="单位" width="80" />
              <el-table-column prop="sybz" label="使用标志" width="100">
                <template #default="scope">
                  <el-tag :type="scope.row.sybz ? 'success' : 'info'">
                    {{ scope.row.sybz ? '是' : '否' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="150">
                <template #default="scope">
                  <el-button type="danger" size="small" @click="deleteItem(scope.row.mkid, scope.row.xmid)">
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-col>
      </el-row>
      
      <!-- 关联项目对话框 -->
      <el-dialog v-model="showAssociateDialog" title="关联项目" width="600px">
        <el-form>
          <el-form-item label="搜索项目">
            <el-input v-model="searchKeyword" placeholder="输入项目名称、英文名或拼音码" @input="searchItems" />
          </el-form-item>
        </el-form>
        <el-table :data="availableItems" border max-height="400" v-loading="searching">
          <el-table-column prop="xmid" label="项目ID" width="100" />
          <el-table-column prop="xmzwmc" label="中文名称" />
          <el-table-column prop="xmywmc" label="英文名称" />
          <el-table-column prop="xmdw" label="单位" width="80" />
          <el-table-column label="操作" width="100">
            <template #default="scope">
              <el-button type="primary" size="small" @click="associateItem(scope.row.xmid)">
                关联
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <template #footer>
          <el-button @click="showAssociateDialog = false">关闭</el-button>
        </template>
      </el-dialog>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'

const loading = ref(false)
const searching = ref(false)
const reportTypeList = ref([])
const itemList = ref([])
const availableItems = ref([])
const selectedReportType = ref(null)
const showAssociateDialog = ref(false)
const searchKeyword = ref('')

const loadReportTypeList = async () => {
  try {
    const response = await axios.get('/api/system/special-report/module/list')
    reportTypeList.value = response.data.data || []
  } catch (error) {
    ElMessage.error('加载报告类型失败：' + error.message)
  }
}

const handleReportTypeSelect = async (row) => {
  if (!row) return
  selectedReportType.value = row
  loading.value = true
  try {
    const response = await axios.get('/api/system/special-report/linked/list', {
      params: { mkid: row.mkid }
    })
    itemList.value = response.data.data || []
  } catch (error) {
    ElMessage.error('加载关联项目失败：' + error.message)
  } finally {
    loading.value = false
  }
}

const searchItems = async () => {
  if (!searchKeyword.value) {
    availableItems.value = []
    return
  }
  searching.value = true
  try {
    const response = await axios.get('/api/system/special-report/item/search', {
      params: { mc: searchKeyword.value }
    })
    availableItems.value = response.data.data || []
  } catch (error) {
    ElMessage.error('搜索项目失败：' + error.message)
  } finally {
    searching.value = false
  }
}

const associateItem = async (xmid) => {
  if (!selectedReportType.value) {
    ElMessage.warning('请先选择报告类型')
    return
  }
  
  try {
    const response = await axios.post('/api/system/special-report/link', {
      mkid: selectedReportType.value.mkid,
      xmid: xmid,
      mksm: ''
    })
    if (response.data.success) {
      ElMessage.success('关联成功')
      showAssociateDialog.value = false
      searchKeyword.value = ''
      availableItems.value = []
      await handleReportTypeSelect(selectedReportType.value)
    } else {
      ElMessage.error(response.data.message)
    }
  } catch (error) {
    ElMessage.error('关联失败：' + error.message)
  }
}

const deleteItem = async (hbid, xmid) => {
  try {
    await ElMessageBox.confirm('确定要删除这个关联项目吗？', '提示', {
      type: 'warning'
    })
    const response = await axios.delete('/api/system/special-report/link', {
      params: { mkid: hbid, xmid: xmid }
    })
    if (response.data.success) {
      ElMessage.success('删除成功')
      await handleReportTypeSelect(selectedReportType.value)
    } else {
      ElMessage.error(response.data.message)
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败：' + error.message)
    }
  }
}

onMounted(() => {
  loadReportTypeList()
})
</script>

<style scoped>
.special-report-container {
  padding: 20px;
}

.card-header {
  font-size: 16px;
  font-weight: bold;
}
</style>

