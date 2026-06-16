<template>
  <el-dialog :model-value="modelValue" @update:model-value="$emit('update:modelValue', $event)" title="检验项目设置" width="1100px" :close-on-click-modal="false" append-to-body>
    <div style="margin-bottom:12px;display:flex;gap:8px;align-items:center;">
      <el-input v-model="keyword" placeholder="搜索项目(名称/拼音/代码/其他代码)" clearable style="width:260px" @keyup.enter="loadList" />
      <el-select v-model="filterType" placeholder="项目类型" clearable style="width:150px">
        <el-option v-for="t in itemTypes" :key="t.value" :label="t.label" :value="t.value" />
      </el-select>
      <el-button type="primary" @click="loadList">查询</el-button>
      <el-button type="success" @click="openForm(null)">新增</el-button>
    </div>
    <el-table :data="list" border stripe max-height="400" size="small" highlight-current-row @row-dblclick="openForm">
      <el-table-column prop="xmid" label="ID" width="60" />
      <el-table-column prop="xmdm" label="项目代码" width="80" />
      <el-table-column prop="xmzwmc" label="中文名称" min-width="120" />
      <el-table-column prop="xmywmc" label="英文名称" width="100" />
      <el-table-column prop="pym" label="拼音码" width="80" />
      <el-table-column prop="qtdm" label="其他代码" width="80" />
      <el-table-column prop="xmdw" label="单位" width="60" />
      <el-table-column prop="xmjd" label="精度" width="70">
        <template #default="{row}">{{ precisionLabel(row.xmjd) }}</template>
      </el-table-column>
      <el-table-column prop="item_type" label="类型" width="60">
        <template #default="{row}">{{ typeLabel(row.item_type) }}</template>
      </el-table-column>
      <el-table-column prop="jsbz" label="计算" width="50">
        <template #default="{row}"><el-tag :type="row.jsbz?'success':'info'" size="small">{{ row.jsbz?'是':'否' }}</el-tag></template>
      </el-table-column>
      <el-table-column prop="xs" label="系数" width="60" />
      <el-table-column prop="sfbz" label="收费" width="70" />
      <el-table-column prop="his_fydm" label="HIS费用代码" width="100" />
      <el-table-column prop="tybz" label="停用" width="50">
        <template #default="{row}"><el-tag :type="row.tybz?'danger':'success'" size="small">{{ row.tybz?'是':'否' }}</el-tag></template>
      </el-table-column>
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{row}">
          <el-button link type="primary" size="small" @click="openForm(row)">编辑</el-button>
          <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="formVisible" title="编辑检验项目" width="750px" append-to-body>
      <el-form :model="form" label-width="100px" size="small">
        <el-divider content-position="left">基本信息</el-divider>
        <el-row :gutter="12">
          <el-col :span="8"><el-form-item label="项目代码"><el-input v-model="form.xmdm" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="中文名称" required><el-input v-model="form.xmzwmc" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="英文名称"><el-input v-model="form.xmywmc" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="12">
          <el-col :span="8"><el-form-item label="拼音码" required><el-input v-model="form.pym" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="其他代码"><el-input v-model="form.qtdm" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="项目类型">
            <el-select v-model="form.itemType" style="width:100%">
              <el-option v-for="t in itemTypes" :key="t.value" :label="t.label" :value="t.value" />
            </el-select>
          </el-form-item></el-col>
        </el-row>
        <el-divider content-position="left">结果设置</el-divider>
        <el-row :gutter="12">
          <el-col :span="8"><el-form-item label="单位"><el-input v-model="form.xmdw" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="精度">
            <el-select v-model="form.xmjd" style="width:100%">
              <el-option v-for="p in precisions" :key="p.value" :label="p.label" :value="p.value" />
            </el-select>
          </el-form-item></el-col>
          <el-col :span="8"><el-form-item label="系数"><el-input-number v-model="form.xs" :min="0" :precision="3" style="width:100%" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="12">
          <el-col :span="8"><el-form-item label="计算标志"><el-switch v-model="form.jsbz" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="质控标志"><el-switch v-model="form.zsbz" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="打印标志"><el-switch v-model="form.dybz" /></el-form-item></el-col>
        </el-row>
        <el-divider content-position="left">收费设置</el-divider>
        <el-row :gutter="12">
          <el-col :span="8"><el-form-item label="收费标准"><el-input-number v-model="form.sfbz" :min="0" :precision="2" style="width:100%" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="工作量"><el-input-number v-model="form.gzl" :min="0" :precision="2" style="width:100%" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="样本稀释率"><el-input-number v-model="form.sjxhl" :min="0" :precision="3" style="width:100%" /></el-form-item></el-col>
        </el-row>
        <el-divider content-position="left">HIS对接</el-divider>
        <el-row :gutter="12">
          <el-col :span="12"><el-form-item label="HIS费用代码"><el-input v-model="form.hisFydm" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="HIS项目名称"><el-input v-model="form.hisJyxmmc" /></el-form-item></el-col>
        </el-row>
        <el-divider content-position="left">其他</el-divider>
        <el-row :gutter="12">
          <el-col :span="12"><el-form-item label="主索引代码"><el-input v-model="form.zskXmdm" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="主索引名称"><el-input v-model="form.zskXmmc" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="12">
          <el-col :span="24"><el-form-item label="临床意义"><el-input v-model="form.lcyy" type="textarea" :rows="2" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="12">
          <el-col :span="8"><el-form-item label="停用"><el-switch v-model="form.tybz" /></el-form-item></el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="formVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </el-dialog>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { fetchTestItems, saveTestItem, deleteTestItem, fetchPrecisions, fetchTestItemTypes } from '../../api/testItem'

const props = defineProps({ modelValue: Boolean })
const emit = defineEmits(['update:modelValue'])

const keyword = ref('')
const filterType = ref('')
const list = ref([])
const precisions = ref([])
const itemTypes = ref([])
const formVisible = ref(false)
const form = ref({})

const precisionLabel = (v) => {
  const p = precisions.value.find(i => i.value === v)
  return p ? p.label : String(v || '')
}

const typeLabel = (v) => {
  const t = itemTypes.value.find(i => i.value === v)
  return t ? t.label : String(v || '')
}

const loadList = async () => {
  try {
    const { data } = await fetchTestItems({ keyword: keyword.value })
    let result = Array.isArray(data) ? data : []
    if (filterType.value) {
      result = result.filter(i => String(i.item_type) === String(filterType.value))
    }
    list.value = result
  } catch (e) { ElMessage.error('查询失败') }
}

const loadPrecisions = async () => {
  try {
    const { data } = await fetchPrecisions()
    precisions.value = Array.isArray(data) ? data : []
  } catch (e) {}
}

const loadItemTypes = async () => {
  try {
    const { data } = await fetchTestItemTypes()
    itemTypes.value = Array.isArray(data) ? data : []
  } catch (e) {
    itemTypes.value = [
      { value: 0, label: '常规' },
      { value: 1, label: '生化' },
      { value: 2, label: '免疫' },
      { value: 3, label: '血液' },
      { value: 4, label: '微生物' }
    ]
  }
}

const openForm = (row) => {
  form.value = row ? { ...row, jsbz: !!row.jsbz, tybz: !!row.tybz, dybz: !!row.dybz, zsbz: !!row.zsbz } : { xmid: 0, xmjd: 3, xmlx: 0, jsbz: false, tybz: false, dybz: true, zsbz: false, xs: 1, sfbz: 0, gzl: 0, itemType: 0, sjxhl: 0 }
  formVisible.value = true
}

const handleSave = async () => {
  if (!form.value.xmzwmc) { ElMessage.warning('中文名称不能为空'); return }
  if (!form.value.pym) { ElMessage.warning('拼音码不能为空'); return }
  try {
    const { data } = await saveTestItem(form.value)
    if (data.success) { ElMessage.success('保存成功'); formVisible.value = false; loadList() }
    else ElMessage.error(data.message)
  } catch (e) { ElMessage.error('保存失败') }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定删除项目"${row.xmzwmc}"？`, '提示', { type: 'warning' })
    const { data } = await deleteTestItem(row.xmid)
    if (data.success) { ElMessage.success('删除成功'); loadList() }
    else ElMessage.error(data.message)
  } catch (e) {}
}

onMounted(() => { loadList(); loadPrecisions(); loadItemTypes() })
</script>
