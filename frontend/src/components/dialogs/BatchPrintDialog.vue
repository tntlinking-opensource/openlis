<template>
  <el-dialog :model-value="modelValue" @update:model-value="$emit('update:modelValue', $event)" title="批量打印中心" width="1200px" :close-on-click-modal="false" append-to-body>
    <div class="batch-print-container">
      <div class="left-panel">
        <div class="filter-bar">
          <div class="filter-row">
            <el-date-picker v-model="dateRange" type="daterange" start-placeholder="开始日期" end-placeholder="结束日期" size="small" style="width:100%;" value-format="YYYY-MM-DD" />
          </div>
          <div class="filter-row">
            <el-select v-model="filterBrlb" placeholder="患者类别" clearable size="small" style="width:32%;">
              <el-option label="门诊" value="1" />
              <el-option label="住院" value="2" />
              <el-option label="体检" value="3" />
              <el-option label="其他" value="4" />
            </el-select>
            <el-select v-model="filterKsdm" placeholder="科室" clearable size="small" style="width:32%;">
              <el-option v-for="d in departments" :key="d.ksdm" :label="d.ksmc" :value="d.ksdm" />
            </el-select>
            <el-select v-if="filterBrlb === '3'" v-model="filterTjdw" placeholder="体检单位" clearable size="small" style="width:32%;">
              <el-option v-for="d in departments" :key="d.ksdm" :label="d.ksmc" :value="d.ksdm" />
            </el-select>
          </div>
          <div class="filter-row">
            <el-checkbox v-model="skipPrinted">跳过已打印</el-checkbox>
            <el-checkbox v-model="directPrint" style="margin-left:8px;">直接打印</el-checkbox>
            <el-button type="primary" size="small" @click="loadList" style="margin-left:auto;">查询</el-button>
          </div>
        </div>
        <div class="panel-header">
          <span>样本列表 ({{ selectedIds.length }}/{{ list.length }})</span>
          <div style="display:flex;gap:4px;">
            <el-button size="small" @click="selectAudited">全选已审核</el-button>
            <el-checkbox v-model="allSelected" @change="toggleAll">全选</el-checkbox>
          </div>
        </div>
        <div class="sample-list">
          <div
            v-for="item in list"
            :key="item.brxx_id"
            class="sample-item"
            :class="{ selected: isSelected(item.brxx_id), previewing: previewId === item.brxx_id }"
            @click="previewItem(item)"
          >
            <el-checkbox :model-value="isSelected(item.brxx_id)" @click.stop @change="toggleSelect(item)" />
            <div class="sample-info">
              <span class="sample-name">{{ item.brxm || '未知' }}</span>
              <span class="sample-no">{{ item.syh || item.brxx_tmh }}</span>
            </div>
            <div class="sample-status">
              <el-tag :type="item.ybzt >= 3 ? 'success' : item.ybzt >= 2 ? 'warning' : 'info'" size="small">
                {{ getStatusText(item.ybzt) }}
              </el-tag>
            </div>
          </div>
          <div v-if="list.length === 0" class="empty-tip">暂无样本数据，请调整查询条件</div>
        </div>
        <div v-if="printProgress.show" class="progress-bar">
          <el-progress :percentage="printProgress.pct" :format="() => `${printProgress.current}/${printProgress.total}`" />
          <span class="progress-text">{{ printProgress.text }}</span>
        </div>
      </div>

      <div class="right-panel">
        <div class="panel-header">
          <span>报告预览</span>
          <div style="display:flex;align-items:center;gap:8px;">
            <el-select v-if="templateList.length > 0" v-model="selectedTemplateId" placeholder="自动匹配" clearable size="small" style="width:180px;">
              <el-option label="自动匹配" :value="null" />
              <el-option v-for="t in templateList" :key="t.template_id" :label="t.template_name" :value="t.template_id" />
            </el-select>
            <el-button type="primary" size="small" @click="printSelected" :disabled="!selectedIds.length || printProgress.show">
              打印选中 ({{ selectedIds.length }})
            </el-button>
          </div>
        </div>
        <div class="preview-area">
          <template v-if="previewId && isSelected(previewId)">
            <iframe :srcdoc="previewHtml" class="preview-frame"></iframe>
          </template>
          <template v-else>
            <div class="preview-empty">
              <span>点击左侧样本查看预览</span>
            </div>
          </template>
        </div>
      </div>
    </div>
  </el-dialog>
</template>

<script setup>
import { ref, computed, watch, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { batchPrint } from '../../api/sample'
import { batchPrintQuery, listTemplates, fetchPrintDepartments } from '../../api/report'
import { useInstrumentStore } from '../../utils/instrumentStore'
import { getStatusText } from '../../utils/sampleStatus'
const instrumentStore = useInstrumentStore()

const props = defineProps({
  modelValue: Boolean,
  preSelectedIds: {
    type: Array,
    default: () => []
  },
  initialDate: {
    type: String,
    default: ''
  }
})
const emit = defineEmits(['update:modelValue', 'printed'])

const list = ref([])
const selectedRows = ref([])
const previewId = ref(null)
const previewHtml = ref('')
const skipPrinted = ref(true)
const directPrint = ref(true)
const templateList = ref([])
const selectedTemplateId = ref(null)
const departments = ref([])
const filterBrlb = ref(null)
const filterKsdm = ref('')
const filterTjdw = ref('')
const dateRange = ref([])
const printProgress = reactive({ show: false, current: 0, total: 0, pct: 0, text: '' })

const selectedIds = computed(() => selectedRows.value.map(r => r.brxx_id))
const isSelected = (id) => selectedIds.value.includes(id)

const allSelected = computed({
  get: () => list.value.length > 0 && selectedIds.value.length === list.value.length,
  set: (val) => {
    if (val) {
      selectedRows.value = [...list.value]
    } else {
      selectedRows.value = []
    }
  }
})

const selectAudited = () => {
  selectedRows.value = list.value.filter(item => item.ybzt === 2 || item.ybzt === 3)
}

const loadTemplates = async () => {
  try {
    const bgbh = instrumentStore.state.bgbh
    const bgmc = instrumentStore.state.bgmc
    const params = {}
    if (bgbh && bgmc) {
      params.bgbh = bgbh
      params.bgmc = bgmc
    }
    const { data } = await listTemplates(params)
    templateList.value = Array.isArray(data) ? data : []
  } catch (e) {
    templateList.value = []
  }
}

const loadDepartments = async () => {
  try {
    const { data } = await fetchPrintDepartments()
    departments.value = Array.isArray(data) ? data : []
  } catch (e) {
    departments.value = []
  }
}

const toggleSelect = async (item) => {
  const idx = selectedRows.value.findIndex(r => r.brxx_id === item.brxx_id)
  if (idx >= 0) {
    selectedRows.value.splice(idx, 1)
  } else {
    selectedRows.value.push(item)
  }
  if (!isSelected(item.brxx_id)) {
    if (previewId.value === item.brxx_id) {
      previewId.value = null
      previewHtml.value = ''
    }
  } else {
    await loadPreview(item)
  }
}

const previewItem = async (item) => {
  if (previewId.value === item.brxx_id) return
  await loadPreview(item)
}

const loadPreview = async (item) => {
  try {
    previewId.value = item.brxx_id
    previewHtml.value = ''
    const payload = {
      brxxIds: [Number(item.brxx_id)],
      czydm: JSON.parse(localStorage.getItem('user') || '{}').czydm || 'admin',
      skipPrinted: false,
      templateId: selectedTemplateId.value || null
    }
    const { data } = await batchPrint(payload)
    if (data.success && data.results && data.results.length > 0) {
      const result = data.results[0]
      if (result.success && result.reportHtml) {
        previewHtml.value = result.reportHtml
      } else {
        previewHtml.value = '<div style="padding:20px;text-align:center;color:#e74c3c;">' + (result.message || '无法生成预览') + '</div>'
      }
    } else {
      const msg = (data.results && data.results[0] && data.results[0].message) || data.message || '暂无预览数据'
      previewHtml.value = '<div style="padding:20px;text-align:center;color:#e74c3c;">' + msg + '</div>'
    }
  } catch (e) {
    previewHtml.value = '<div style="padding:20px;text-align:center;color:#999;">预览加载失败</div>'
  }
}

const toggleAll = (val) => {
  if (val) {
    selectedRows.value = [...list.value]
  } else {
    selectedRows.value = []
  }
  previewId.value = null
  previewHtml.value = ''
}

const printSelected = async () => {
  if (!selectedIds.value.length) {
    ElMessage.warning('请先选择要打印的样本')
    return
  }
  printProgress.show = true
  printProgress.current = 0
  printProgress.total = selectedRows.value.length
  printProgress.pct = 0
  printProgress.text = '准备打印...'
  try {
    const payload = {
      brxxIds: selectedRows.value.map(row => Number(row.brxx_id)),
      czydm: JSON.parse(localStorage.getItem('user') || '{}').czydm || 'admin',
      skipPrinted: Boolean(skipPrinted.value),
      templateId: selectedTemplateId.value || null
    }
    const { data } = await batchPrint(payload)
    if (data.success) {
      const results = data.results || []
      let delay = 0
      for (let i = 0; i < results.length; i++) {
        const item = results[i]
        printProgress.current = i + 1
        printProgress.pct = Math.round(((i + 1) / printProgress.total) * 100)
        printProgress.text = `正在打印 ${item.brxm || ''}...`
        if (item.success && item.reportHtml) {
          if (directPrint.value) {
            setTimeout(((html) => () => {
              const win = window.open('', `_blank_${Date.now()}`, 'width=800,height=600')
              if (win) {
                win.document.write(html)
                win.document.close()
                win.focus()
                setTimeout(() => { win.print() }, 300)
              }
            })(item.reportHtml), delay)
            delay += 500
          }
        }
      }
      const successCount = results.filter(r => r.success).length
      const skipCount = results.filter(r => !r.success).length
      let msg = `打印完成：${successCount} 份`
      if (skipCount > 0) msg += `，跳过 ${skipCount} 份`
      ElMessage.success(msg)
      emit('printed')
      setTimeout(() => {
        printProgress.show = false
        emit('update:modelValue', false)
      }, 1000)
    } else {
      ElMessage.error(data.message)
      printProgress.show = false
    }
  } catch (e) {
    ElMessage.error('打印失败')
    printProgress.show = false
  }
}

watch(() => props.modelValue, async (val) => {
  if (val) {
    selectedRows.value = []
    previewId.value = null
    previewHtml.value = ''
    selectedTemplateId.value = null
    printProgress.show = false
    const date = props.initialDate || new Date().toISOString().split('T')[0]
    dateRange.value = [date, date]
    await Promise.all([loadList(), loadTemplates(), loadDepartments()])
    if (props.preSelectedIds.length > 0) {
      const ordered = props.preSelectedIds.map(id => list.value.find(item => item.brxx_id === id)).filter(Boolean)
      selectedRows.value = ordered
      if (ordered.length > 0) {
        await loadPreview(ordered[0])
      }
    }
  } else {
    selectedRows.value = []
    previewId.value = null
    previewHtml.value = ''
    printProgress.show = false
  }
})

watch(selectedRows, (newRows) => {
  if (newRows.length === 0) {
    previewId.value = null
    previewHtml.value = ''
  }
}, { deep: true })

const loadList = async () => {
  try {
    let beginDate = ''
    let endDate = ''
    if (dateRange.value && dateRange.value.length === 2) {
      beginDate = dateRange.value[0]
      endDate = dateRange.value[1]
    } else {
      const today = new Date().toISOString().split('T')[0]
      beginDate = today
      endDate = today
    }
    const params = {
      beginDate,
      endDate,
      brlb: filterBrlb.value || null,
      ksdm: filterKsdm.value || '',
      tjdw: filterTjdw.value || ''
    }
    const sbDjid = instrumentStore.state.sbDjid
    if (sbDjid) {
      params.sbDjid = sbDjid
    }
    const { data } = await batchPrintQuery(params)
    const allItems = Array.isArray(data) ? data.filter(item => item.ybzt >= 0) : []
    if (props.preSelectedIds.length > 0) {
      const filtered = allItems.filter(item => props.preSelectedIds.includes(item.brxx_id))
      const idSet = new Set(filtered.map(i => i.brxx_id))
      const ordered = props.preSelectedIds.filter(id => idSet.has(id)).map(id => filtered.find(i => i.brxx_id === id))
      list.value = ordered
    } else {
      list.value = allItems
    }
  } catch (e) {
    list.value = []
  }
}
</script>

<style scoped>
.batch-print-container {
  display: flex;
  gap: 16px;
  height: 560px;
}

.left-panel {
  width: 380px;
  display: flex;
  flex-direction: column;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
}

.filter-bar {
  padding: 8px;
  border-bottom: 1px solid #dcdfe6;
  background: #f5f7fa;
}

.filter-row {
  display: flex;
  gap: 4px;
  margin-bottom: 6px;
  align-items: center;
}

.filter-row:last-child {
  margin-bottom: 0;
}

.right-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
}

.panel-header {
  padding: 10px 12px;
  background: #f5f7fa;
  border-bottom: 1px solid #dcdfe6;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 500;
}

.sample-list {
  flex: 1;
  overflow-y: auto;
  padding: 8px;
}

.sample-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px;
  border-radius: 4px;
  cursor: pointer;
  transition: background 0.2s;
}

.sample-item:hover {
  background: #f5f7fa;
}

.sample-item.selected {
  background: #ecf5ff;
}

.sample-item.previewing {
  background: #ecf5ff;
  border: 1px solid #409eff;
}

.sample-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.sample-name {
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.sample-no {
  font-size: 12px;
  color: #909399;
}

.empty-tip {
  text-align: center;
  color: #909399;
  padding: 20px;
}

.preview-area {
  flex: 1;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.preview-frame {
  flex: 1;
  border: none;
  width: 100%;
}

.preview-empty {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #909399;
  font-size: 14px;
}

.progress-bar {
  padding: 8px 12px;
  border-top: 1px solid #dcdfe6;
  background: #f5f7fa;
}

.progress-text {
  font-size: 12px;
  color: #606266;
  margin-top: 4px;
  display: block;
}
</style>
