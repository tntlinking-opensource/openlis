<template>
  <el-dialog
    :model-value="modelValue"
    @update:modelValue="$emit('update:modelValue', $event)"
    title="报表模板设计"
    width="95%"
    top="2vh"
    :close-on-click-modal="false"
    append-to-body
    destroy-on-close
  >
    <div style="display:flex;gap:16px;height:80vh;">
      <!-- 左侧模板列表 -->
      <div style="width:250px;border:1px solid #dcdfe6;border-radius:4px;display:flex;flex-direction:column;">
        <div style="padding:12px;background:#f5f7fa;border-bottom:1px solid #dcdfe6;">
          <el-input v-model="keyword" placeholder="搜索模板" clearable size="small" @keyup.enter="loadList" />
          <div style="margin-top:8px;">
            <el-button type="primary" size="small" @click="loadList" style="width:100%;">刷新</el-button>
          </div>
        </div>
        <el-table
          :data="templateList"
          border
          size="small"
          highlight-current-row
          @row-click="handleSelectTemplate"
          :current-row-key="selectedTemplateId"
          style="flex:1;"
          max-height="none"
        >
          <el-table-column prop="template_id" label="ID" width="50" />
          <el-table-column prop="template_name" label="模板名称" min-width="120" show-overflow-tooltip />
        </el-table>
      </div>

      <!-- 中间模板编辑区 -->
      <div style="flex:1;display:flex;flex-direction:column;border:1px solid #dcdfe6;border-radius:4px;">
        <div style="padding:8px 12px;background:#f5f7fa;border-bottom:1px solid #dcdfe6;display:flex;justify-content:space-between;align-items:center;">
          <span style="font-weight:bold;">模板编辑</span>
          <div style="display:flex;gap:8px;">
            <el-button size="small" type="primary" @click="handlePreview">预览</el-button>
            <el-button size="small" type="success" @click="handleSave">保存</el-button>
          </div>
        </div>
        <div style="flex:1;display:flex;">
          <!-- 模板信息 -->
          <div style="width:200px;padding:12px;border-right:1px solid #dcdfe6;">
            <el-form label-width="80px" size="small">
              <el-form-item label="模板名称">
                <el-input v-model="currentTemplate.template_name" />
              </el-form-item>
              <el-form-item label="模板类型">
                <el-select v-model="currentTemplate.template_type" style="width:100%;">
                  <el-option label="单列" value="single_col" />
                  <el-option label="双列" value="double_col" />
                  <el-option label="图表" value="chart" />
                  <el-option label="自定义" value="custom" />
                </el-select>
              </el-form-item>
              <el-form-item label="适用标本">
                <el-select v-model="selectedBbzl" multiple placeholder="选择标本类型" style="width:100%;" filterable>
                  <el-option v-for="item in specimenTypes" :key="item.bm" :label="item.bmsm" :value="String(item.bm)" />
                </el-select>
              </el-form-item>
              <el-form-item label="描述">
                <el-input v-model="currentTemplate.description" type="textarea" :rows="3" />
              </el-form-item>
              <el-form-item label="报告编号">
                <el-input v-model="currentTemplate.bgbh" placeholder="如：BC" />
              </el-form-item>
              <el-form-item label="报告名称">
                <el-input v-model="currentTemplate.bgmc" placeholder="如：血常规" />
              </el-form-item>
            </el-form>
          </div>
          <!-- HTML编辑器 -->
          <div style="flex:1;display:flex;flex-direction:column;">
            <div style="padding:8px 12px;background:#fafafa;border-bottom:1px solid #ebeef5;font-size:12px;color:#909399;">
              HTML模板内容 (使用 {{variable}} 作为变量占位符)
            </div>
            <textarea
              v-model="htmlContent"
              style="flex:1;border:none;resize:none;padding:12px;font-family:'Consolas','Monaco',monospace;font-size:13px;line-height:1.4;outline:none;"
              spellcheck="false"
              placeholder="输入HTML模板内容..."
            ></textarea>
          </div>
        </div>
      </div>

      <!-- 右侧预览区 -->
      <div style="width:500px;border:1px solid #dcdfe6;border-radius:4px;display:flex;flex-direction:column;">
        <div style="padding:8px 12px;background:#f5f7fa;border-bottom:1px solid #dcdfe6;display:flex;justify-content:space-between;align-items:center;">
          <span style="font-weight:bold;">预览</span>
          <el-button size="small" @click="handleRefreshPreview">刷新</el-button>
        </div>
        <div style="flex:1;padding:0;overflow:hidden;background:#f5f5f5;">
          <div v-if="loadingPreview" style="display:flex;align-items:center;justify-content:center;height:100%;">
            <el-icon class="is-loading" size="32"><Loading /></el-icon>
          </div>
          <iframe v-else-if="previewUrl" :src="previewUrl" style="width:100%;height:100%;border:none;background:white;" />
          <div v-else style="display:flex;align-items:center;justify-content:center;height:100%;color:#909399;">
            点击"预览"查看效果
          </div>
        </div>
      </div>
    </div>
  </el-dialog>
</template>

<script setup>
import { ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Loading } from '@element-plus/icons-vue'
import axios from 'axios'

const props = defineProps({ modelValue: Boolean })
const emit = defineEmits(['update:modelValue'])

const keyword = ref('')
const templateList = ref([])
const selectedTemplateId = ref(null)
const currentTemplate = ref({})
const htmlContent = ref('')
const previewUrl = ref('')
const loadingPreview = ref(false)
const specimenTypes = ref([])
const selectedBbzl = ref([])

const loadList = async () => {
  try {
    const { data } = await axios.get('/api/report-template/list', { params: { keyword: keyword.value } })
    templateList.value = Array.isArray(data) ? data : []
  } catch (e) {
    ElMessage.error('加载模板列表失败')
  }
}

const loadSpecimenTypes = async () => {
  try {
    const { data } = await axios.get('/api/specimen-type/list', { params: { keyword: '' } })
    specimenTypes.value = Array.isArray(data) ? data : []
  } catch (e) {
    console.error('加载标本类型失败', e)
  }
}

const handleSelectTemplate = async (row) => {
  selectedTemplateId.value = row.template_id
  try {
    const { data } = await axios.get(`/api/report-template/${row.template_id}`)
    currentTemplate.value = { ...data }
    const htmlResp = await axios.get(`/api/report-template/${row.template_id}/html`)
    htmlContent.value = htmlResp.data.html || getDefaultHtml()
    try {
      const bbzl = data.bbzl
      selectedBbzl.value = bbzl ? JSON.parse(bbzl) : []
    } catch (e) {
      selectedBbzl.value = []
    }
  } catch (e) {
    currentTemplate.value = { ...row }
    htmlContent.value = getDefaultHtml()
    selectedBbzl.value = []
  }
  previewUrl.value = ''
}

const handlePreview = async () => {
  if (!htmlContent.value) {
    ElMessage.warning('请先输入HTML模板内容')
    return
  }
  loadingPreview.value = true
  previewUrl.value = ''
  try {
    const { data } = await axios.post('/api/report/render', {
      html: htmlContent.value,
      data: getSampleData()
    })
    if (data.success && data.pdf) {
      previewUrl.value = `data:application/pdf;base64,${data.pdf}`
    } else {
      ElMessage.error(data.message || '预览失败')
    }
  } catch (e) {
    ElMessage.error('预览失败')
  } finally {
    loadingPreview.value = false
  }
}

const handleRefreshPreview = () => {
  handlePreview()
}

const handleSave = async () => {
  if (!currentTemplate.value.template_id) {
    ElMessage.warning('请先选择一个模板')
    return
  }
  try {
    await axios.post('/api/report-template/save', {
      templateId: currentTemplate.value.template_id,
      templateName: currentTemplate.value.template_name,
      templateType: currentTemplate.value.template_type,
      description: currentTemplate.value.description,
      htmlContent: htmlContent.value,
      bbzl: JSON.stringify(selectedBbzl.value),
      bgbh: currentTemplate.value.bgbh || '',
      bgmc: currentTemplate.value.bgmc || ''
    })
    ElMessage.success('保存成功')
    loadList()
  } catch (e) {
    ElMessage.error('保存失败')
  }
}

const getDefaultHtml = () => {
  return `<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta charset="UTF-8"/>
<style>
* { margin: 0; padding: 0; box-sizing: border-box; }
body { font-family: "SimSun", "宋体", serif; font-size: 12px; }
.report { width: 100%; max-width: 700px; margin: 0 auto; padding: 20px; }
.header { text-align: center; margin-bottom: 20px; border-bottom: 1px solid #333; padding-bottom: 10px; }
.header h1 { font-size: 20px; margin-bottom: 5px; }
.info-table { width: 100%; border-collapse: collapse; margin-bottom: 20px; }
.info-table td { border: 1px solid #333; padding: 5px 8px; }
.info-table .label { background: #f0f0f0; font-weight: bold; width: 20%; }
.result-table { width: 100%; border-collapse: collapse; margin-bottom: 20px; }
.result-table th, .result-table td { border: 1px solid #333; padding: 5px 8px; }
.result-table th { background: #f0f0f0; }
.signature { display: flex; justify-content: space-between; margin-top: 30px; }
.signature div { text-align: center; }
</style>
</head>
<body>
<div class="report">
  <div class="header">
    <h1>{{reportTitle}}</h1>
  </div>

  <table class="info-table">
    <tr>
      <td class="label">患者姓名</td>
      <td>{{patientName}}</td>
      <td class="label">性别</td>
      <td>{{gender}}</td>
      <td class="label">年龄</td>
      <td>{{age}}</td>
    </tr>
    <tr>
      <td class="label">科室</td>
      <td>{{department}}</td>
      <td class="label">标本类型</td>
      <td>{{specimenType}}</td>
      <td class="label">采集时间</td>
      <td>{{collectTime}}</td>
    </tr>
  </table>

  <div class="signature">
    <div>
      <p>检验者：{{inspector}}</p>
      <p>复核者：{{reviewer}}</p>
    </div>
    <div>
      <p>报告时间：{{reportTime}}</p>
    </div>
  </div>
</div>
</body>
</html>`
}

const getSampleData = () => {
  return {
    reportTitle: '检验报告单',
    patientName: '张三',
    gender: '男',
    age: '45岁',
    department: '内科',
    specimenType: '血液',
    collectTime: '2024-01-15 08:30',
    inspector: '李医生',
    reviewer: '王主任',
    reportTime: '2024-01-15 10:30'
  }
}

watch(() => props.modelValue, (val) => {
  if (val) {
    loadList()
    loadSpecimenTypes()
    selectedTemplateId.value = null
    currentTemplate.value = {}
    htmlContent.value = ''
    previewUrl.value = ''
    selectedBbzl.value = []
  }
})
</script>
