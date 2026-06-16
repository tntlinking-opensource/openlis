<template>
  <el-dialog
    :model-value="modelValue"
    @update:model-value="$emit('update:modelValue', $event)"
    title="公式设置"
    width="1100px"
    :close-on-click-modal="false"
    append-to-body
    class="formula-setting-dialog"
  >
    <div class="formula-container">
      <!-- 左侧：仪器列表 -->
      <div class="left-panel">
        <div class="panel-header">仪器列表</div>
        <el-table
          :data="instrumentList"
          border
          size="small"
          height="350"
          highlight-current-row
          @row-click="onInstrumentSelect"
        >
          <el-table-column prop="sbmc" label="仪器名称" />
        </el-table>
      </div>

      <!-- 中间：公式列表 -->
      <div class="middle-panel">
        <div class="panel-header">公式列表</div>
        <el-table
          :data="formulaList"
          border
          size="small"
          height="350"
          highlight-current-row
          @row-click="onFormulaSelect"
        >
          <el-table-column prop="jsgs" label="公式" min-width="150">
            <template #default="{row}">
              <span :title="row.bds">{{ row.jsgs }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="xmid" label="项目ID" width="70" />
        </el-table>
      </div>

      <!-- 右侧：公式编辑器 -->
      <div class="right-panel">
        <div class="panel-header">
          <span>公式编辑器 - {{ selectedInstrument ? selectedInstrument.sbmc : '请选择仪器' }}</span>
        </div>

        <!-- 项目选择区域 -->
        <div class="editor-section">
          <div class="form-row">
            <label>计算项目：</label>
            <el-select
              v-model="selectedXmid"
              placeholder="选择计算项目"
              size="small"
              style="width: 200px"
              @change="onProjectChange"
            >
              <el-option
                v-for="item in projectList"
                :key="item.xmid"
                :label="item.xmzwmc || item.xmdm || `项目${item.xmid}`"
                :value="item.xmid"
              />
            </el-select>
          </div>

          <div class="form-row">
            <label>模糊搜索：</label>
            <el-input
              v-model="searchKeyword"
              placeholder="输入拼音码搜索"
              size="small"
              style="width: 150px"
              @keyup.enter="searchProjects"
              clearable
            />
            <el-button size="small" type="primary" @click="searchProjects">查询</el-button>
            <el-button size="small" type="success" @click="handleNew" :disabled="!selectedInstrument">新增</el-button>
            <el-button size="small" type="warning" @click="handleUpdate" :disabled="!selectedFormula">修改</el-button>
          </div>
        </div>

        <!-- 项目搜索结果 -->
        <el-table
          v-if="searchResults.length > 0"
          :data="searchResults"
          border
          size="small"
          height="120"
          highlight-current-row
          @row-dblclick="onSearchResultDblClick"
          class="search-results"
        >
          <el-table-column prop="xmid" label="ID" width="50" />
          <el-table-column prop="xmzwmc" label="项目名称" min-width="120" />
          <el-table-column prop="pym" label="拼音码" width="80" />
        </el-table>

        <!-- 公式显示区域 -->
        <div class="formula-display">
          <label>公式预览：</label>
          <div class="formula-preview" v-if="currentFormulaPreview">
            {{ currentFormulaPreview }}
          </div>
          <div class="formula-preview empty" v-else>
            请通过下方按钮添加项目和运算符
          </div>
        </div>

        <!-- 运算按钮 -->
        <div class="operator-buttons">
          <el-button size="small" @click="addOperator('+')">+</el-button>
          <el-button size="small" @click="addOperator('-')">-</el-button>
          <el-button size="small" @click="addOperator('*')">*</el-button>
          <el-button size="small" @click="addOperator('/')">/</el-button>
          <el-button size="small" @click="addOperator('(')">(</el-button>
          <el-button size="small" @click="addOperator(')')">)</el-button>
          <el-button size="small" type="primary" @click="addPower">^</el-button>
        </div>

        <!-- 系数输入 -->
        <div class="coefficient-input">
          <label>系数：</label>
          <el-input
            v-model="coefficient"
            placeholder="输入系数后回车"
            size="small"
            style="width: 120px"
            @keyup.enter="addCoefficient"
          />
          <span style="font-size: 12px; color: #909399;">输入系数后按回车键添加到公式</span>
        </div>

        <!-- 操作按钮 -->
        <div class="action-buttons">
          <el-button size="small" type="primary" @click="handleSave" :disabled="!canSave">保存</el-button>
          <el-button size="small" @click="handleCancel" :disabled="editMode === 0">取消</el-button>
          <el-button size="small" @click="handleClose">关闭</el-button>
        </div>
      </div>
    </div>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { fetchInstrumentItemTree, fetchFormulaList, fetchProjectListByInstrument, searchProjectsApi, saveFormula } from '../../api/labItem'

const props = defineProps({
  modelValue: Boolean,
  sbDjid: Number,
  xmid: Number,
  itemName: String,
  instrumentName: String
})

const emit = defineEmits(['update:modelValue', 'saved'])

const instrumentList = ref([])
const formulaList = ref([])
const projectList = ref([])
const searchResults = ref([])
const selectedInstrument = ref(null)
const selectedFormula = ref(null)
const selectedXmid = ref(null)
const searchKeyword = ref('')
const coefficient = ref('')

const editMode = ref(0)
const formulaElements = ref([])
const viewingFormulaPreview = ref('')

const currentFormulaPreview = computed(() => {
  if (editMode.value !== 0) {
    if (formulaElements.value.length === 0) return ''
    return formulaElements.value.map(e => typeof e === 'string' ? e : e.xmzwmc).join('')
  }
  return viewingFormulaPreview.value
})

const canSave = computed(() => {
  if (!selectedInstrument.value || !selectedXmid.value || formulaElements.value.length === 0) {
    return false
  }
  const hasProject = formulaElements.value.some(e => typeof e !== 'string')
  return hasProject
})

watch(() => props.modelValue, async (val) => {
  if (val) {
    await loadInstruments()
  }
})

const loadInstruments = async () => {
  try {
    const { data } = await fetchInstrumentItemTree()
    instrumentList.value = Array.isArray(data) ? data.map(i => ({ sbDjid: i.sbDjid, sbmc: i.label || i.sbmc })) : []
  } catch (e) {
    ElMessage.error('加载仪器列表失败')
  }
}

const onInstrumentSelect = async (row) => {
  selectedInstrument.value = row
  await loadFormulas(row.sbDjid)
  await loadProjects(row.sbDjid)
}

const loadFormulas = async (sbDjid) => {
  try {
    const { data } = await fetchFormulaList(sbDjid)
    formulaList.value = Array.isArray(data) ? data : []
  } catch (e) {
    formulaList.value = []
  }
}

const loadProjects = async (sbDjid) => {
  try {
    const { data } = await fetchProjectListByInstrument(sbDjid)
    projectList.value = Array.isArray(data) ? data : []
  } catch (e) {
    projectList.value = []
  }
}

const onFormulaSelect = (row) => {
  selectedFormula.value = row
  selectedXmid.value = row.xmid
  formulaElements.value = []
  viewingFormulaPreview.value = row.bdssm || row.jsgs || ''
  editMode.value = 0
}

const onProjectChange = (xmid) => {
  const project = projectList.value.find(p => p.xmid === xmid)
  if (project) {
    addProjectToFormula(project)
  }
}

const searchProjects = async () => {
  if (!searchKeyword.value || !selectedInstrument.value) return
  try {
    const { data } = await searchProjectsApi(searchKeyword.value, selectedInstrument.value.sbDjid)
    searchResults.value = Array.isArray(data) ? data : []
  } catch (e) {
    searchResults.value = []
  }
}

const onSearchResultDblClick = (row) => {
  selectedXmid.value = row.xmid
  addProjectToFormula(row)
  searchResults.value = []
  searchKeyword.value = ''
}

const addProjectToFormula = (project) => {
  formulaElements.value.push(project)
}

const addOperator = (op) => {
  formulaElements.value.push(op)
}

const addPower = () => {
  formulaElements.value.push('^')
}

const addCoefficient = () => {
  if (coefficient.value) {
    formulaElements.value.push(coefficient.value)
    coefficient.value = ''
  }
}

const handleBack = () => {
  if (formulaElements.value.length > 0) {
    formulaElements.value.pop()
  }
}

const handleClear = () => {
  formulaElements.value = []
}

const handleNew = () => {
  editMode.value = 1
  formulaElements.value = []
  selectedXmid.value = null
  selectedFormula.value = null
}

const handleUpdate = async () => {
  if (!selectedFormula.value) return
  editMode.value = 2
  formulaElements.value = []
  if (!projectList.value.length && selectedInstrument.value) {
    await loadProjects(selectedInstrument.value.sbDjid)
  }
  if (selectedFormula.value.bds) {
    parseBdsToElements(selectedFormula.value.bds)
  }
}

const parseBdsToElements = (bds) => {
  if (!bds || !projectList.value.length) return
  const regex = /@xm(\d+)|([+\-*/^()])/g
  let match
  while ((match = regex.exec(bds)) !== null) {
    if (match[1]) {
      const xmid = parseInt(match[1])
      const project = projectList.value.find(p => p.xmid === xmid)
      if (project) {
        formulaElements.value.push(project)
      }
    } else if (match[2]) {
      formulaElements.value.push(match[2])
    }
  }
}

const handleCancel = () => {
  editMode.value = 0
  formulaElements.value = []
  if (selectedFormula.value) {
    selectedXmid.value = selectedFormula.value.xmid
  }
}

const handleSave = async () => {
  if (!selectedInstrument.value || !selectedXmid.value) {
    ElMessage.warning('请选择计算项目和仪器')
    return
  }
  if (formulaElements.value.length === 0) {
    ElMessage.warning('请添加公式内容')
    return
  }
  try {
    const bdssm = currentFormulaPreview.value
    const bds = buildFormulaSql()
    await saveFormula({
      sbDjid: selectedInstrument.value.sbDjid,
      xmid: selectedXmid.value,
      bds: bds,
      bdssm: bdssm
    })
    ElMessage.success('保存成功')
    viewingFormulaPreview.value = bdssm
    editMode.value = 0
    await loadFormulas(selectedInstrument.value.sbDjid)
  } catch (e) {
    ElMessage.error('保存失败')
  }
}

const buildFormulaSql = () => {
  let sql = ''
  for (const elem of formulaElements.value) {
    if (typeof elem === 'string') {
      sql += elem
    } else {
      sql += `@xm${elem.xmid}`
    }
  }
  return sql
}

const handleClose = () => {
  emit('update:modelValue', false)
}
</script>

<style scoped>
.formula-container {
  display: flex;
  gap: 10px;
  height: 500px;
}

.left-panel, .middle-panel {
  width: 200px;
}

.right-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.panel-header {
  font-size: 13px;
  font-weight: 500;
  padding: 8px;
  background: #f5f7fa;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  margin-bottom: 5px;
}

.editor-section {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-row {
  display: flex;
  align-items: center;
  gap: 8px;
}

.form-row label {
  width: 70px;
  font-size: 12px;
}

.search-results {
  margin-top: 5px;
}

.formula-display {
  margin-top: 10px;
}

.formula-display label {
  font-size: 12px;
  display: block;
  margin-bottom: 5px;
}

.formula-preview {
  padding: 10px;
  background: #f5f7fa;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  min-height: 40px;
  font-family: monospace;
  font-size: 13px;
}

.formula-preview.empty {
  color: #909399;
  font-style: italic;
}

.operator-buttons {
  display: flex;
  gap: 5px;
  margin-top: 10px;
}

.coefficient-input {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 10px;
}

.coefficient-input label {
  font-size: 12px;
  width: 40px;
}

.action-buttons {
  display: flex;
  gap: 10px;
  margin-top: 15px;
  padding-top: 10px;
  border-top: 1px solid #e4e7ed;
}
</style>
