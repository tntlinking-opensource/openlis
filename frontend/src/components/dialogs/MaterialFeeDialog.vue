<template>
  <el-dialog :model-value="modelValue" @update:modelValue="$emit('update:modelValue', $event)" title="材料费设置" width="800px" :close-on-click-modal="false" append-to-body>
    <el-tabs v-model="activeTab">
      <el-tab-pane label="材料费查询" name="search">
        <div style="margin-bottom:12px;display:flex;gap:8px;align-items:center;">
          <el-input v-model="searchForm.pym" placeholder="搜索(代码/名称/拼音)" clearable style="width:200px" />
          <el-button type="primary" @click="searchItems">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </div>
        <el-table :data="feeItems" border stripe size="small" max-height="250">
          <el-table-column prop="clfdm" label="代码" width="100" />
          <el-table-column prop="clfmc" label="名称" />
          <el-table-column prop="pym" label="拼音码" width="100" />
          <el-table-column prop="dj" label="单价" width="80" />
          <el-table-column label="绑定状态" width="90">
            <template #default="{row}">
              <el-tag :type="row.bound ? 'success' : 'info'" size="small">{{ row.bound ? '已绑定' : '未绑定' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="100">
            <template #default="{row}">
              <el-button link type="primary" size="small" @click="showBindDialog(row)">绑定</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="批量同步" name="sync">
        <div style="margin-bottom:16px;padding:12px;background:#f5f7fa;border-radius:4px;">
          <el-form :model="syncForm" label-width="90px" size="small">
            <el-form-item label="材料费代码">
              <el-input v-model="syncForm.clfdm" style="width:200px" />
            </el-form-item>
            <el-form-item label="材料费名称">
              <el-input v-model="syncForm.clfmc" style="width:300px" />
            </el-form-item>
          </el-form>
          <el-button type="primary" @click="doSync">执行同步</el-button>
        </div>
        <el-table :data="syncHistory" border stripe size="small" max-height="200">
          <el-table-column prop="clfdm" label="代码" width="100" />
          <el-table-column prop="clfmc" label="名称" />
          <el-table-column prop="result" label="结果" />
          <el-table-column prop="time" label="时间" width="150" />
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="绑定管理" name="bind">
        <div style="margin-bottom:12px;display:flex;gap:8px;align-items:center;">
          <el-select v-model="bindSearch.dlid" placeholder="选择大类" clearable style="width:150px" @change="onCatChange">
            <el-option v-for="cat in categories" :key="cat.dlid" :label="cat.dlmc" :value="cat.dlid" />
          </el-select>
          <el-input v-model="bindSearch.xlmc" placeholder="小类名称" clearable style="width:150px" />
          <el-button type="primary" @click="searchBindings">搜索</el-button>
        </div>
        <el-table :data="bindings" border stripe size="small" max-height="280">
          <el-table-column prop="dlmc" label="大类" width="100" />
          <el-table-column prop="xlbh" label="小类编号" width="80" />
          <el-table-column prop="xlmc" label="小类名称" width="120" />
          <el-table-column prop="clfdm" label="材料费代码" width="100" />
          <el-table-column prop="clfmc" label="材料费名称" />
          <el-table-column prop="sgys" label="试管颜色" width="80" />
          <el-table-column label="操作" width="80">
            <template #default="{row}">
              <el-button link type="danger" size="small" @click="unbindMaterial(row)">解除绑定</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <el-dialog v-model="bindDialogVisible" title="绑定材料费到试管类型" width="500px" append-to-body>
      <div style="margin-bottom:16px;">
        <el-alert :title="selectedFee?.clfmc" type="info" :closable="false" />
      </div>
      <el-form :model="bindForm" label-width="100px" size="small">
        <el-form-item label="选择大类" required>
          <el-select v-model="bindForm.dlid" placeholder="请选择大类" style="width:100%" @change="onBindCatChange">
            <el-option v-for="cat in categories" :key="cat.dlid" :label="cat.dlmc" :value="cat.dlid" />
          </el-select>
        </el-form-item>
        <el-form-item label="选择小类" required>
          <el-select v-model="bindForm.xlbh" placeholder="请选择小类" style="width:100%">
            <el-option v-for="sub in subcategories" :key="sub.xlbh" :label="sub.xlmc" :value="sub.xlbh" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="bindDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="doBind">确认绑定</el-button>
      </template>
    </el-dialog>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { fetchFeeItems, syncMaterialFee, fetchMaterialBindings, bindMaterialFee, unbindMaterialFee, fetchTubeCategories, fetchTubeSubcategories } from '../../api/materialFee'

const props = defineProps({ modelValue: Boolean })
const emit = defineEmits(['update:modelValue'])

const activeTab = ref('search')
const searchForm = reactive({ pym: '' })
const feeItems = ref([])
const syncForm = reactive({ clfdm: '', clfmc: '' })
const syncHistory = ref([])
const bindSearch = reactive({ dlid: null, xlmc: '' })
const categories = ref([])
const subcategories = ref([])
const bindings = ref([])
const bindDialogVisible = ref(false)
const selectedFee = ref(null)
const bindForm = reactive({ dlid: null, xlbh: null })

const loadCategories = async () => {
  try {
    const { data } = await fetchTubeCategories()
    categories.value = Array.isArray(data) ? data : []
  } catch (e) {}
}

const onCatChange = () => {
  bindSearch.xlmc = ''
  searchBindings()
}

const searchItems = async () => {
  try {
    const { data } = await fetchFeeItems({ pym: searchForm.pym })
    feeItems.value = Array.isArray(data) ? data : []
  } catch (e) {}
}

const resetSearch = () => {
  searchForm.pym = ''
  searchItems()
}

const searchBindings = async () => {
  try {
    const params = {}
    if (bindSearch.dlid) params.dlid = bindSearch.dlid
    if (bindSearch.xlmc) params.xlmc = bindSearch.xlmc
    const { data } = await fetchMaterialBindings(params)
    bindings.value = Array.isArray(data) ? data : []
  } catch (e) {}
}

const doSync = async () => {
  if (!syncForm.clfdm) { ElMessage.warning('请输入材料费代码'); return }
  try {
    const { data } = await syncMaterialFee(syncForm)
    if (data.success) {
      ElMessage.success('同步成功')
      syncHistory.value.unshift({ clfdm: syncForm.clfdm, clfmc: syncForm.clfmc, result: '成功', time: new Date().toLocaleString() })
    } else {
      ElMessage.error(data.message)
      syncHistory.value.unshift({ clfdm: syncForm.clfdm, clfmc: syncForm.clfmc, result: '失败: ' + data.message, time: new Date().toLocaleString() })
    }
  } catch (e) {
    ElMessage.error('同步失败')
    syncHistory.value.unshift({ clfdm: syncForm.clfdm, clfmc: syncForm.clfmc, result: '失败', time: new Date().toLocaleString() })
  }
}

const showBindDialog = async (row) => {
  selectedFee.value = row
  bindForm.dlid = null
  bindForm.xlbh = null
  subcategories.value = []
  bindDialogVisible.value = true
}

const onBindCatChange = async () => {
  if (bindForm.dlid) {
    try {
      const { data } = await fetchTubeSubcategories({ dlid: bindForm.dlid })
      subcategories.value = Array.isArray(data) ? data : []
    } catch (e) {}
  } else {
    subcategories.value = []
  }
}

const doBind = async () => {
  if (!bindForm.dlid || !bindForm.xlbh) { ElMessage.warning('请选择大类和子类'); return }
  try {
    const { data } = await bindMaterialFee({ xlbh: bindForm.xlbh, clfdm: selectedFee.value.clfdm, clfmc: selectedFee.value.clfmc })
    if (data.success) {
      ElMessage.success('绑定成功')
      bindDialogVisible.value = false
      searchBindings()
      searchItems()
    } else {
      ElMessage.error(data.message)
    }
  } catch (e) { ElMessage.error('绑定失败') }
}

const unbindMaterial = async (row) => {
  try {
    const { data } = await unbindMaterialFee({ xlbh: row.xlbh })
    if (data.success) {
      ElMessage.success('解除绑定成功')
      searchBindings()
      searchItems()
    } else {
      ElMessage.error(data.message)
    }
  } catch (e) { ElMessage.error('解除绑定失败') }
}

onMounted(() => {
  loadCategories()
  searchItems()
  searchBindings()
})
</script>