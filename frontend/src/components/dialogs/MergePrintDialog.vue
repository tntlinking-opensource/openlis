<template>
  <el-dialog 
    :model-value="visible" 
    @update:model-value="$emit('update:visible', $event)" 
    title="报告合并打印" 
    width="700px" 
    :close-on-click-modal="false" 
    append-to-body
  >
    <div class="merge-print-dialog">
      <div class="selected-samples">
        <div class="header">
          <span>已选择 {{ samples.length }} 份报告</span>
          <el-button size="small" @click="clearSelection">清空选择</el-button>
        </div>
        
        <el-table :data="samples" border stripe size="small" max-height="300" style="margin-top: 8px;">
          <el-table-column prop="brxm" label="姓名" width="80" />
          <el-table-column prop="brxx_tmh" label="条码号" width="130" />
          <el-table-column prop="syh" label="样本号" width="100" />
          <el-table-column prop="ksmc" label="科室" width="100" />
          <el-table-column prop="jyrq" label="检验日期" width="140" />
          <el-table-column label="状态" width="80">
            <template #default="{row}">
              <el-tag :type="row.ybzt >= 3 ? 'success' : row.ybzt >= 2 ? 'warning' : 'info'" size="small">
                {{ getStatusText(row.ybzt) }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
      </div>
      
      <div class="print-options" style="margin-top: 16px;">
        <el-form :model="options" label-width="80px">
          <el-form-item label="打印选项">
            <el-checkbox v-model="options.skipPrinted">跳过已打印</el-checkbox>
            <el-checkbox v-model="options.autoAudit" style="margin-left: 16px;">自动审核</el-checkbox>
          </el-form-item>
          <el-form-item label="报告模板" v-if="templateList.length > 0">
            <el-select v-model="options.templateId" placeholder="自动匹配" clearable style="width: 200px;">
              <el-option label="自动匹配" :value="null" />
              <el-option v-for="t in templateList" :key="t.template_id" :label="t.template_name" :value="t.template_id" />
            </el-select>
          </el-form-item>
        </el-form>
      </div>
    </div>
    
    <template #footer>
      <el-button @click="handleCancel">取消</el-button>
      <el-button type="primary" @click="handleConfirm" :loading="loading" :disabled="samples.length === 0">
        开始打印 ({{ samples.length }})
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import { getStatusText } from '../../utils/sampleStatus'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  samples: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['update:visible', 'confirm', 'cancel'])

const loading = ref(false)
const templateList = ref([])
const options = ref({
  skipPrinted: true,
  autoAudit: false,
  templateId: null
})

const clearSelection = () => {
  emit('update:visible', false)
  emit('confirm', { samples: [], action: 'clear' })
}

const loadTemplates = async () => {
  try {
    const { data } = await axios.get('/api/report-template/list')
    templateList.value = Array.isArray(data) ? data : []
  } catch (e) {
    templateList.value = []
  }
}

watch(() => props.visible, (val) => {
  if (val) {
    loadTemplates()
  }
})

const handleConfirm = async () => {
  if (props.samples.length === 0) {
    ElMessage.warning('请选择要打印的报告')
    return
  }
  
  loading.value = true
  try {
    const { data } = await axios.post('/api/report/mergePrint', {
      brxxIds: props.samples.map(s => s.brxx_id),
      skipPrinted: options.value.skipPrinted,
      autoAudit: options.value.autoAudit,
      templateId: options.value.templateId,
      czydm: JSON.parse(localStorage.getItem('user') || '{}').czydm || 'admin'
    })
    
    if (data.success) {
      ElMessage.success(data.message || '合并打印任务已提交')
      emit('confirm', { samples: props.samples, results: data.results })
      emit('update:visible', false)
    } else {
      ElMessage.error(data.message || '打印失败')
    }
  } catch (e) {
    ElMessage.error('打印失败：' + (e.response?.data?.message || e.message))
  } finally {
    loading.value = false
  }
}

const handleCancel = () => {
  emit('cancel')
  emit('update:visible', false)
}
</script>

<style scoped>
.selected-samples {
  border: 1px solid #ebeef5;
  padding: 12px;
  border-radius: 4px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
