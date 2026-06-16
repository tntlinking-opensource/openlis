<template>
  <el-dialog :model-value="modelValue" @update:model-value="$emit('update:modelValue', $event)" title="仪器所属项目设置" width="1100px" :close-on-click-modal="false" append-to-body>
    <div style="display:flex;gap:16px;height:550px;">
      <div style="width:280px;border:1px solid #e4e7ed;border-radius:4px;overflow:auto;">
        <div style="padding:8px 12px;background:#f5f7fa;font-weight:500;border-bottom:1px solid #e4e7ed;">仪器 → 项目</div>
        <el-tree :data="tree" :props="{ label: 'label', children: 'children' }" node-key="id" default-expand-all highlight-current @node-click="onNodeClick" />
      </div>
      <div style="flex:1;overflow:auto;">
        <div v-if="selectedItem" style="padding:8px;">
          <h4 style="margin:0 0 12px">{{ selectedItem.xmzwmc }} ({{ selectedItem.xmdm }}) - 参考范围设置</h4>
          <el-tabs>
            <el-tab-pane label="参考范围">
              <el-table :data="refRanges" border stripe size="small" max-height="250">
                <el-table-column prop="brxb" label="性别" width="60">
                  <template #default="{row}">{{ {0:'全部',1:'男',2:'女'}[row.brxb] || '全部' }}</template>
                </el-table-column>
                <el-table-column prop="nlsgbz" label="年龄" width="100">
                  <template #default="{row}">{{ row.nlsgbz ? `${row.nllx||''}${row.nlsx || 0}-${row.nlxx || 0}岁` : '全部' }}</template>
                </el-table-column>
                <el-table-column prop="ckz" label="参考范围" min-width="120" />
                <el-table-column prop="ckzdx" label="参考低" width="70" />
                <el-table-column prop="ckzgx" label="参考高" width="70" />
                <el-table-column prop="bjzdx" label="报警低" width="70" />
                <el-table-column prop="bjzgx" label="报警高" width="70" />
                <el-table-column label="操作" width="100" fixed="right">
                  <template #default="{row}">
                    <el-button link type="primary" size="small" @click="editRefRange(row)">编辑</el-button>
                    <el-button link type="danger" size="small" @click="deleteRef(row)">删除</el-button>
                  </template>
                </el-table-column>
              </el-table>
              <div style="margin-top:12px;">
                <el-button size="small" type="success" @click="addRefRange">新增参考范围</el-button>
              </div>
            </el-tab-pane>
            <el-tab-pane label="默认值设置">
              <el-form :model="defaultForm" label-width="100px" size="small" style="max-width:500px;">
                <el-form-item label="默认值">
                  <el-input v-model="defaultForm.mrz" style="width:200px" />
                </el-form-item>
                <el-form-item label="自动填充">
                  <el-switch v-model="defaultForm.mr" :active-value="1" :inactive-value="0" />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" size="small" @click="doSaveDefault">保存默认值</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>
          </el-tabs>

          <el-dialog v-model="refFormVisible" :title="refForm.id ? '编辑参考范围' : '新增参考范围'" width="650px" append-to-body>
            <el-form :model="refForm" label-width="90px" size="small">
              <el-divider content-position="left">基本信息</el-divider>
              <el-row :gutter="12">
                <el-col :span="12"><el-form-item label="性别">
                  <el-select v-model="refForm.brxb" style="width:100%">
                    <el-option :value="0" label="全部" />
                    <el-option :value="1" label="男" />
                    <el-option :value="2" label="女" />
                  </el-select>
                </el-form-item></el-col>
                <el-col :span="12"><el-form-item label="年龄分层">
                  <el-switch v-model="refForm.nlsgbz" :active-value="1" :inactive-value="0" />
                </el-form-item></el-col>
              </el-row>
              <el-row :gutter="12" v-if="refForm.nlsgbz">
                <el-col :span="8"><el-form-item label="年龄类型"><el-input v-model="refForm.nllx" placeholder="如:岁/月" /></el-form-item></el-col>
                <el-col :span="8"><el-form-item label="年龄起始"><el-input-number v-model="refForm.nlsx" :min="0" style="width:100%" /></el-form-item></el-col>
                <el-col :span="8"><el-form-item label="年龄结束"><el-input-number v-model="refForm.nlxx" :min="0" style="width:100%" /></el-form-item></el-col>
              </el-row>
              <el-divider content-position="left">参考范围</el-divider>
              <el-row :gutter="12">
                <el-col :span="12"><el-form-item label="参考低值"><el-input v-model="refForm.ckzdx" /></el-form-item></el-col>
                <el-col :span="12"><el-form-item label="参考高值"><el-input v-model="refForm.ckzgx" /></el-form-item></el-col>
              </el-row>
              <el-row :gutter="12">
                <el-col :span="24"><el-form-item label="参考文本"><el-input v-model="refForm.ckz" placeholder="如:3.9-6.1" /></el-form-item></el-col>
              </el-row>
              <el-divider content-position="left">报警范围</el-divider>
              <el-row :gutter="12">
                <el-col :span="12"><el-form-item label="报警低值"><el-input v-model="refForm.bjzdx" /></el-form-item></el-col>
                <el-col :span="12"><el-form-item label="报警高值"><el-input v-model="refForm.bjzgx" /></el-form-item></el-col>
              </el-row>
              <el-divider content-position="left">警告范围</el-divider>
              <el-row :gutter="12">
                <el-col :span="12"><el-form-item label="警告低值"><el-input v-model="refForm.jszdx" /></el-form-item></el-col>
                <el-col :span="12"><el-form-item label="警告高值"><el-input v-model="refForm.jszgx" /></el-form-item></el-col>
              </el-row>
              <el-divider content-position="left">复审范围</el-divider>
              <el-row :gutter="12">
                <el-col :span="12"><el-form-item label="复审低值"><el-input v-model="refForm.fczdx" /></el-form-item></el-col>
                <el-col :span="12"><el-form-item label="复审高值"><el-input v-model="refForm.fczgx" /></el-form-item></el-col>
              </el-row>
              <el-divider content-position="left">自动审核</el-divider>
              <el-row :gutter="12">
                <el-col :span="8"><el-form-item label="自动审核"><el-switch v-model="refForm.zdshbz" :active-value="1" :inactive-value="0" /></el-form-item></el-col>
                <el-col :span="8"><el-form-item label="审核低值"><el-input v-model="refForm.zdshdx" /></el-form-item></el-col>
                <el-col :span="8"><el-form-item label="审核高值"><el-input v-model="refForm.zdshgx" /></el-form-item></el-col>
              </el-row>
              <el-row :gutter="12">
                <el-col :span="12"><el-form-item label="采纳区间"><el-input-number v-model="refForm.zdshcyqj" :min="0" style="width:100%" /></el-form-item></el-col>
              </el-row>
            </el-form>
            <template #footer>
              <el-button @click="refFormVisible = false">取消</el-button>
              <el-button type="primary" @click="doSaveRefRange">保存</el-button>
            </template>
          </el-dialog>
        </div>
        <div v-else style="text-align:center;color:#909399;padding-top:100px;">请从左侧选择一个项目</div>
      </div>
    </div>
  </el-dialog>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { fetchInstrumentItemTree, fetchRefRanges, saveRefRange, deleteRefRange, fetchDefault, saveDefault } from '../../api/instrumentItem'

const props = defineProps({ modelValue: Boolean })
const emit = defineEmits(['update:modelValue'])

const tree = ref([])
const selectedItem = ref(null)
const selectedInst = ref(null)
const refRanges = ref([])
const refFormVisible = ref(false)
const refForm = ref({})
const defaultForm = ref({ mrz: '', mr: 0 })

const loadTree = async () => {
  try {
    const { data } = await fetchInstrumentItemTree()
    tree.value = Array.isArray(data) ? data : []
  } catch (e) {}
}

const onNodeClick = async (node) => {
  if (node.type === 'item') {
    selectedItem.value = node
    selectedInst.value = node.sb_djid
    loadRefRanges(node.sb_djid, node.xmid)
    loadDefaultVal(node.sb_djid, node.xmid)
  }
}

const loadRefRanges = async (instId, itemId) => {
  try {
    const { data } = await fetchRefRanges(instId, itemId)
    refRanges.value = Array.isArray(data) ? data : []
  } catch (e) {}
}

const loadDefaultVal = async (instId, itemId) => {
  try {
    const { data } = await fetchDefault(instId, itemId)
    defaultForm.value = { mrz: data.mrz || '', mr: data.mr || 0 }
  } catch (e) {}
}

const addRefRange = () => {
  refForm.value = { xmid: selectedItem.value.xmid, sbDjid: selectedInst.value, brxb: 0, nlsgbz: 0, ckzdx: '', ckzgx: '', bjzdx: '', bjzgx: '', ckz: '', jszdx: '', jszgx: '', fczdx: '', fczgx: '', zdshbz: 0, zdshdx: '', zdshgx: '', zdshcyqj: 0 }
  refFormVisible.value = true
}

const editRefRange = (row) => {
  refForm.value = { ...row }
  refFormVisible.value = true
}

const doSaveRefRange = async () => {
  try {
    const { data } = await saveRefRange(refForm.value)
    if (data.success) { ElMessage.success('保存成功'); refFormVisible.value = false; loadRefRanges(selectedInst.value, selectedItem.value.xmid) }
    else ElMessage.error(data.message)
  } catch (e) {}
}

const deleteRef = async (row) => {
  try {
    await ElMessageBox.confirm('确定删除此参考范围？', '提示', { type: 'warning' })
    const { data } = await deleteRefRange(row.id)
    if (data.success) { ElMessage.success('删除成功'); loadRefRanges(selectedInst.value, selectedItem.value.xmid) }
  } catch (e) {}
}

const doSaveDefault = async () => {
  try {
    const { data } = await saveDefault({ xmid: selectedItem.value.xmid, sbDjid: selectedInst.value, mrz: defaultForm.value.mrz, mr: defaultForm.value.mr })
    if (data.success) ElMessage.success('默认值已保存')
    else ElMessage.error(data.message || '保存失败')
  } catch (e) { ElMessage.error('保存失败') }
}

onMounted(() => { loadTree() })
</script>
