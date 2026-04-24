<template>
  <div class="common-code-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>6.4 通用编码设置</span>
        </div>
      </template>
      
      <el-row :gutter="20">
        <!-- 左侧：编码主表列表 -->
        <el-col :span="8">
          <el-card>
            <template #header>
              <div style="display: flex; justify-content: space-between; align-items: center;">
                <span>编码主表</span>
                <el-button type="primary" size="small" @click="showAddMainDialog = true">新增</el-button>
              </div>
            </template>
            <el-table :data="mainList" border highlight-current-row @current-change="handleMainSelect">
              <el-table-column prop="bmmc" label="编码名称" />
              <el-table-column prop="bmdm" label="编码代码" width="100" />
            </el-table>
          </el-card>
        </el-col>
        
        <!-- 右侧：编码明细列表 -->
        <el-col :span="16">
          <el-card>
            <template #header>
              <div style="display: flex; justify-content: space-between; align-items: center;">
                <span>编码明细（{{ selectedMain?.bmmc || '请选择编码主表' }}）</span>
                <div>
                  <el-button type="primary" size="small" @click="showAddDetailDialog('sibling')" :disabled="!selectedMain">
                    增加同级
                  </el-button>
                  <el-button type="success" size="small" @click="showAddDetailDialog('child')" :disabled="!selectedMain">
                    增加下级
                  </el-button>
                </div>
              </div>
            </template>
            <el-table :data="detailList" border v-loading="loading">
              <el-table-column prop="bm" label="编码" width="100" />
              <el-table-column prop="bmsm" label="编码说明" />
              <el-table-column prop="szdm" label="数字代码" width="120" />
              <el-table-column prop="pym" label="拼音码" width="120" />
              <el-table-column prop="mrzbz" label="默认值" width="80">
                <template #default="scope">
                  <el-tag :type="scope.row.mrzbz ? 'success' : 'info'">
                    {{ scope.row.mrzbz ? '是' : '否' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="150">
                <template #default="scope">
                  <el-button type="primary" size="small" @click="editDetail(scope.row)">修改</el-button>
                  <el-button type="danger" size="small" @click="deleteDetail(scope.row.id)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-col>
      </el-row>
      
      <!-- 新增/编辑编码主表对话框 -->
      <el-dialog v-model="showAddMainDialog" :title="editingMain ? '编辑编码主表' : '新增编码主表'" width="500px">
        <el-form :model="mainForm" label-width="100px">
          <el-form-item label="编码名称">
            <el-input v-model="mainForm.bmmc" />
          </el-form-item>
          <el-form-item label="编码代码">
            <el-input-number v-model="mainForm.bmdm" :min="0" />
          </el-form-item>
          <el-form-item label="编码编号">
            <el-input v-model="mainForm.bmbh" maxlength="12" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="showAddMainDialog = false">取消</el-button>
          <el-button type="primary" @click="saveMain">保存</el-button>
        </template>
      </el-dialog>
      
      <!-- 新增/编辑编码明细对话框 -->
      <el-dialog v-model="showAddDetailDialog" :title="editingDetail ? '编辑编码明细' : (addType === 'sibling' ? '增加同级' : '增加下级')" width="500px">
        <el-form :model="detailForm" label-width="100px">
          <el-form-item label="编码">
            <el-input-number v-model="detailForm.bm" :min="0" />
          </el-form-item>
          <el-form-item label="编码说明">
            <el-input v-model="detailForm.bmsm" />
          </el-form-item>
          <el-form-item label="数字代码">
            <el-input v-model="detailForm.szdm" />
          </el-form-item>
          <el-form-item label="拼音码">
            <el-input v-model="detailForm.pym" />
          </el-form-item>
          <el-form-item label="默认值标志">
            <el-switch v-model="detailForm.mrzbz" />
          </el-form-item>
          <el-form-item label="备注">
            <el-input v-model="detailForm.bz" type="textarea" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="showAddDetailDialog = false">取消</el-button>
          <el-button type="primary" @click="saveDetail">保存</el-button>
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
const mainList = ref([])
const detailList = ref([])
const selectedMain = ref(null)
const showAddMainDialog = ref(false)
const showAddDetailDialog = ref(false)
const editingMain = ref(false)
const editingDetail = ref(false)
const addType = ref('sibling') // 'sibling' 同级, 'child' 下级

const mainForm = reactive({
  id: null,
  bmmc: '',
  bmdm: null,
  bmbh: '',
  syccbm: null,
  syccmc: '',
  tybz: false
})

const detailForm = reactive({
  id: null,
  bmdm: null,
  bm: null,
  bmsm: '',
  szdm: '',
  pym: '',
  mrzbz: false,
  tybz: false,
  bz: ''
})

const loadMainList = async () => {
  try {
    const response = await axios.get('/api/system/common-code/main/list')
    mainList.value = response.data
  } catch (error) {
    ElMessage.error('加载编码主表失败：' + error.message)
  }
}

const handleMainSelect = async (row) => {
  if (!row) return
  selectedMain.value = row
  loading.value = true
  try {
    const response = await axios.get('/api/system/common-code/detail/list', {
      params: { bmdm: row.bmdm }
    })
    detailList.value = response.data
  } catch (error) {
    ElMessage.error('加载编码明细失败：' + error.message)
  } finally {
    loading.value = false
  }
}

const saveMain = async () => {
  try {
    const response = await axios.post('/api/system/common-code/main/save', mainForm)
    if (response.data.success) {
      ElMessage.success('保存成功')
      showAddMainDialog.value = false
      resetMainForm()
      await loadMainList()
    } else {
      ElMessage.error(response.data.message)
    }
  } catch (error) {
    ElMessage.error('保存失败：' + error.message)
  }
}

const showAddDetailDialogFunc = (type) => {
  addType.value = type
  editingDetail.value = false
  resetDetailForm()
  if (selectedMain.value) {
    detailForm.bmdm = selectedMain.value.bmdm
  }
  showAddDetailDialog.value = true
}

const editDetail = (row) => {
  editingDetail.value = true
  Object.assign(detailForm, row)
  showAddDetailDialog.value = true
}

const saveDetail = async () => {
  try {
    const response = await axios.post('/api/system/common-code/detail/save', {
      detail: detailForm,
      type: addType.value
    })
    if (response.data.success) {
      ElMessage.success('保存成功')
      showAddDetailDialog.value = false
      resetDetailForm()
      if (selectedMain.value) {
        await handleMainSelect(selectedMain.value)
      }
    } else {
      ElMessage.error(response.data.message)
    }
  } catch (error) {
    ElMessage.error('保存失败：' + error.message)
  }
}

const deleteDetail = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这条编码明细吗？', '提示', {
      type: 'warning'
    })
    const response = await axios.delete(`/api/system/common-code/detail/${id}`)
    if (response.data.success) {
      ElMessage.success('删除成功')
      if (selectedMain.value) {
        await handleMainSelect(selectedMain.value)
      }
    } else {
      ElMessage.error(response.data.message)
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败：' + error.message)
    }
  }
}

const resetMainForm = () => {
  Object.assign(mainForm, {
    id: null,
    bmmc: '',
    bmdm: null,
    bmbh: '',
    syccbm: null,
    syccmc: '',
    tybz: false
  })
  editingMain.value = false
}

const resetDetailForm = () => {
  Object.assign(detailForm, {
    id: null,
    bmdm: null,
    bm: null,
    bmsm: '',
    szdm: '',
    pym: '',
    mrzbz: false,
    tybz: false,
    bz: ''
  })
  editingDetail.value = false
}

onMounted(() => {
  loadMainList()
})
</script>

<style scoped>
.common-code-container {
  padding: 20px;
}

.card-header {
  font-size: 16px;
  font-weight: bold;
}
</style>

