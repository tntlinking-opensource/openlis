<template>
  <el-dialog 
    :model-value="modelValue" 
    @update:model-value="$emit('update:modelValue', $event)" 
    title="提取向导" 
    width="800px" 
    :close-on-click-modal="false" 
    append-to-body
  >
    <div class="extract-wizard">
      <!-- 顶部：仪器和日期选择 -->
      <div class="wizard-header">
        <div class="form-item">
          <label>仪器：</label>
          <span class="instrument-name">{{ currentDevice?.sbmc || '未选择仪器' }}</span>
        </div>
        <div class="form-item">
          <label>日期：</label>
          <el-date-picker 
            v-model="extractDate" 
            type="date" 
            value-format="YYYY-MM-DD" 
            placeholder="选择日期"
            style="width: 150px;"
          />
        </div>
        <div class="form-item">
          <label>待提取：</label>
          <span class="pending-count">{{ pendingCount }} 条</span>
          <el-button type="primary" size="small" @click="loadExtractStatus" :loading="loading">
            刷新
          </el-button>
        </div>
      </div>

      <!-- 选项 -->
      <div class="wizard-options">
        <el-checkbox v-model="selectAll" @change="handleSelectAll">全选</el-checkbox>
        <el-checkbox v-model="abnormalOnly">仅提取异常结果</el-checkbox>
      </div>

      <!-- 预览列表 -->
      <div class="preview-list">
        <el-table 
          :data="extractItems" 
          border 
          stripe 
          size="small"
          max-height="300"
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="50" />
          <el-table-column prop="syh" label="样本号" width="100" />
          <el-table-column prop="xmmc" label="项目名称" width="120" />
          <el-table-column prop="jyjg" label="结果" width="80">
            <template #default="{row}">
              <span :class="getResultClass(row)">{{ row.jyjg }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="xmdw" label="单位" width="80" />
          <el-table-column prop="gdbj" label="标志" width="60">
            <template #default="{row}">
              <span :class="getFlagClass(row.gdbj)">{{ row.gdbj || '-' }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="ckz" label="参考值" width="120" />
        </el-table>
        <div v-if="extractItems.length === 0" class="empty-tip">
          暂无待提取数据
        </div>
      </div>

      <!-- 底部按钮 -->
      <div class="wizard-footer">
        <span class="selected-info">已选择 {{ selectedItems.length }} 项</span>
        <div class="button-group">
          <el-button @click="handleClose">取消</el-button>
          <el-button type="primary" @click="showConfirmDialog" :disabled="selectedItems.length === 0" :loading="extracting">
            开始提取
          </el-button>
        </div>
      </div>

      <!-- 提取确认对话框 -->
      <el-dialog
        v-model="confirmDialogVisible"
        title="确认提取"
        width="450px"
        append-to-body
        :close-on-click-modal="false"
      >
        <div class="confirm-content">
          <div class="confirm-item">
            <span class="label">仪器：</span>
            <span class="value">{{ currentDevice?.sbmc || '-' }}</span>
          </div>
          <div class="confirm-item">
            <span class="label">日期：</span>
            <span class="value">{{ extractDate }}</span>
          </div>
          <div class="confirm-item">
            <span class="label">提取数量：</span>
            <span class="value highlight">{{ selectedItems.length }} 条</span>
          </div>
          <div class="confirm-item" v-if="abnormalCount > 0">
            <span class="label">异常结果：</span>
            <span class="value warning">{{ abnormalCount }} 条</span>
          </div>
          <div class="confirm-warning">
            <el-icon><Warning /></el-icon>
            <span>确认后将立即提取数据，是否继续？</span>
          </div>
        </div>
        <template #footer>
          <el-button @click="confirmDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleExtract" :loading="extracting">确认提取</el-button>
        </template>
      </el-dialog>

      <!-- 提取进度 -->
      <div v-if="extracting" class="extract-progress">
        <el-progress :percentage="progressPercent" :status="progressStatus" />
        <span class="progress-text">{{ progressText }}</span>
      </div>
    </div>
  </el-dialog>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { extractFromInstrument, getExtractStatus, getExtractPreview } from '../../api/sample'
import { Warning } from '@element-plus/icons-vue'

const props = defineProps({
  modelValue: Boolean,
  currentDevice: Object,
  patientName: String
})
const emit = defineEmits(['update:modelValue', 'extracted'])

const extractDate = ref(new Date().toISOString().slice(0, 10))
const pendingCount = ref(0)
const extractItems = ref([])
const selectedItems = ref([])
const loading = ref(false)
const extracting = ref(false)
const selectAll = ref(true)
const abnormalOnly = ref(false)
const progressPercent = ref(0)
const progressStatus = ref('')
const progressText = ref('')
const confirmDialogVisible = ref(false)

const abnormalCount = computed(() => {
  return selectedItems.value.filter(item => item.gdbj).length
})

let statusTimer = null

// 加载待提取状态
const loadExtractStatus = async () => {
  if (!props.currentDevice?.sb_djid) {
    ElMessage.warning('请先选择仪器')
    return
  }
  loading.value = true
  try {
    const [statusRes, previewRes] = await Promise.all([
      getExtractStatus(props.currentDevice.sb_djid, extractDate.value, props.patientName || localStorage.getItem('currentPatientName') || ''),
      getExtractPreview(props.currentDevice.sb_djid, extractDate.value, props.patientName || localStorage.getItem('currentPatientName') || '')
    ])
    pendingCount.value = statusRes.data.pending || 0
    if (previewRes.data.success && previewRes.data.data) {
      extractItems.value = previewRes.data.data.map((item, index) => ({
        id: index,
        syh: item.syh,
        xmmc: item.xmzwmc || item.xmmc || '',
        jyjg: item.jyjg,
        xmdw: item.yqxmdw || '',
        gdbj: item.gdbj || '',
        ckz: item.ckz || '',
        xmid: item.xmid
      }))
    } else {
      extractItems.value = []
    }
  } catch (e) {
    ElMessage.error('获取待提取状态失败')
  } finally {
    loading.value = false
  }
}

const handleSelectionChange = (selection) => {
  selectedItems.value = selection
}

const handleSelectAll = (val) => {
  if (val) {
    selectedItems.value = abnormalOnly.value 
      ? extractItems.value.filter(item => item.gdbj)
      : [...extractItems.value]
  } else {
    selectedItems.value = []
  }
}

watch(abnormalOnly, (val) => {
  if (val) {
    selectedItems.value = extractItems.value.filter(item => item.gdbj)
    if (selectedItems.value.length === 0 && extractItems.value.length > 0) {
      ElMessage.info('没有异常结果')
    }
  } else if (selectAll.value) {
    selectedItems.value = [...extractItems.value]
  }
})

const showConfirmDialog = () => {
  confirmDialogVisible.value = true
}

const getResultClass = (row) => {
  if (row.gdbj === 'H' || row.gdbj === '↑') return 'result-high'
  if (row.gdbj === 'L' || row.gdbj === '↓') return 'result-low'
  return ''
}

const getFlagClass = (flag) => {
  if (flag === 'H' || flag === '↑') return 'flag-high'
  if (flag === 'L' || flag === '↓') return 'flag-low'
  return ''
}

const handleExtract = async () => {
  if (!props.currentDevice?.sb_djid) {
    ElMessage.warning('请先选择仪器')
    return
  }
  extracting.value = true
  progressPercent.value = 0
  progressStatus.value = ''
  progressText.value = '正在提取...'
  
  try {
    const { data } = await extractFromInstrument({
      sbDjid: props.currentDevice.sb_djid,
      beginDate: extractDate.value,
      czydm: JSON.parse(localStorage.getItem('user') || '{}').czydm || 'admin',
      bz: 1,
      patientName: props.patientName || localStorage.getItem('currentPatientName') || ''
    })
    
    if (data.success) {
      ElMessage.success(data.message || '提取成功')
      emit('extracted')
      handleClose()
    } else {
      ElMessage.error(data.message || '提取失败')
    }
  } catch (e) {
    ElMessage.error('提取失败：' + (e.message || '未知错误'))
  } finally {
    extracting.value = false
  }
}

const handleClose = () => {
  if (statusTimer) {
    clearInterval(statusTimer)
    statusTimer = null
  }
  emit('update:modelValue', false)
}

watch(() => props.modelValue, (val) => {
  if (val) {
    loadExtractStatus()
  }
})
</script>

<style scoped>
.extract-wizard {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.wizard-header {
  display: flex;
  gap: 24px;
  align-items: center;
  padding: 12px;
  background: #f5f7fa;
  border-radius: 4px;
}

.form-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.form-item label {
  font-weight: 500;
}

.instrument-name {
  color: #409eff;
}

.pending-count {
  color: #67c23a;
  font-weight: bold;
}

.wizard-options {
  display: flex;
  gap: 24px;
}

.preview-list {
  border: 1px solid #ebeef5;
  border-radius: 4px;
}

.empty-tip {
  text-align: center;
  padding: 40px;
  color: #909399;
}

.wizard-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.selected-info {
  color: #606266;
}

.button-group {
  display: flex;
  gap: 8px;
}

.extract-progress {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 4px;
}

.progress-text {
  color: #606266;
  font-size: 14px;
}

.result-high,
.flag-high {
  color: #f56c6c;
  font-weight: bold;
}

.result-low,
.flag-low {
  color: #409eff;
  font-weight: bold;
}

.confirm-content {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.confirm-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.confirm-item .label {
  color: #606266;
  width: 80px;
}

.confirm-item .value {
  color: #303133;
}

.confirm-item .value.highlight {
  color: #409eff;
  font-weight: bold;
  font-size: 16px;
}

.confirm-item .value.warning {
  color: #e6a23c;
  font-weight: bold;
}

.confirm-warning {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px;
  background: #fdf6ec;
  border-radius: 4px;
  color: #e6a23c;
  margin-top: 8px;
}
</style>
