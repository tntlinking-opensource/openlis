<template>
  <el-dialog
    v-model="visible"
    title="特殊报告设置"
    width="520px"
    :close-on-click-modal="false"
    class="special-report-dialog windows-dialog"
  >
    <div class="dialog-content sr">
      <div class="row top">
        <div class="field">
          <label>请选择报告类型：</label>
          <el-select v-model="mkid" size="small" class="sel" @change="reloadLinked">
            <el-option v-for="m in modules" :key="m.mkid" :label="m.mksm" :value="m.mkid" />
          </el-select>
        </div>
        <el-button size="small" @click="handleLink">添加关联</el-button>
      </div>

      <div class="row">
        <div class="field">
          <label>项目查询：</label>
          <el-input
            v-model="keyword"
            size="small"
            class="inp"
            @keydown.enter.prevent="doSearch"
          />
        </div>
        <el-button size="small" @click="handleUnlink">删除关联</el-button>
      </div>

      <div class="group">
        <div class="group-title">已有关联项目</div>
        <el-table
          :data="linked"
          size="small"
          height="140"
          border
          highlight-current-row
          @current-change="onLinkedSelect"
        >
          <el-table-column prop="mkid" label="模块ID" width="70" align="center" />
          <el-table-column prop="mksm" label="模块说明" width="110" />
          <el-table-column prop="xmid" label="项目ID" width="70" align="center" />
          <el-table-column prop="xmzwmc" label="项目中文名称" />
          <el-table-column prop="xmywmc" label="项目英文名称" />
        </el-table>
      </div>

      <div class="desc">
        <div class="desc-title">说明：</div>
        <div class="desc-text">
          需要处理这些特殊的报告需要在程序中设置项目和报告录入模板的关联关系，
          当设置好关联后，在样本信息模块下双击设置的关联项目就会打开它关联的模板进行录入。
        </div>
      </div>

      <!-- 查询结果弹出表（模拟旧系统 DBGrid1） -->
      <div v-if="showSearch" class="search-pop">
        <el-table
          :data="searchResults"
          size="small"
          height="120"
          border
          highlight-current-row
          @row-dblclick="pickItem"
          @current-change="onSearchSelect"
        >
          <el-table-column prop="xmid" label="项目ID" width="70" />
          <el-table-column prop="xmzwmc" label="项目中文名称" />
          <el-table-column prop="xmywmc" label="项目英文名称" />
          <el-table-column prop="xmdw" label="项目单位" width="80" />
          <el-table-column prop="qtdm" label="其它代码" width="80" />
        </el-table>
      </div>
    </div>
    <template #footer>
      <el-button @click="handleClose">关闭</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:modelValue'])

const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const modules = ref([])
const mkid = ref(0)
const keyword = ref('')
const linked = ref([])
const currentLinked = ref(null)

const showSearch = ref(false)
const searchResults = ref([])
const currentSearch = ref(null)

const loadModules = async () => {
  const res = await axios.get('/api/system/special-report/module/list')
  if (!res.data?.success) throw new Error(res.data?.message || '读取模块失败')
  modules.value = res.data.data || []
  if (modules.value.length > 0) mkid.value = modules.value[0].mkid
}

const reloadLinked = async () => {
  const res = await axios.get('/api/system/special-report/linked/list', { params: { mkid: mkid.value } })
  if (!res.data?.success) throw new Error(res.data?.message || '读取关联失败')
  linked.value = res.data.data || []
  currentLinked.value = null
}

const doSearch = async () => {
  const res = await axios.get('/api/system/special-report/item/search', { params: { mc: keyword.value } })
  if (!res.data?.success) throw new Error(res.data?.message || '查询失败')
  searchResults.value = res.data.data || []
  if (searchResults.value.length === 0) {
    ElMessage.warning('没有找到要的检验项目！')
    showSearch.value = false
    return
  }
  if (searchResults.value.length === 1) {
    keyword.value = searchResults.value[0].xmzwmc
    currentSearch.value = searchResults.value[0]
    showSearch.value = false
  } else {
    showSearch.value = true
  }
}

const pickItem = (row) => {
  if (!row) return
  keyword.value = row.xmzwmc
  currentSearch.value = row
  showSearch.value = false
}

const onSearchSelect = (row) => {
  currentSearch.value = row || null
}

const onLinkedSelect = (row) => {
  currentLinked.value = row || null
}

const handleLink = async () => {
  try {
    if (!currentSearch.value?.xmid) {
      await doSearch()
      if (!currentSearch.value?.xmid) return
    }
    const m = modules.value.find((x) => x.mkid === mkid.value)
    const res = await axios.post('/api/system/special-report/link', {
      mkid: mkid.value,
      mksm: m?.mksm || '',
      xmid: currentSearch.value.xmid
    })
    if (res.data?.success) {
      ElMessage.success(res.data?.message || '添加关联成功！')
      await reloadLinked()
    } else {
      ElMessage.warning(res.data?.message || '添加失败')
    }
  } catch (e) {
    ElMessage.error('添加失败：' + (e.response?.data?.message || e.message))
  }
}

const handleUnlink = async () => {
  try {
    if (!currentLinked.value?.xmid) return
    const res = await axios.delete('/api/system/special-report/link', {
      params: { mkid: mkid.value, xmid: currentLinked.value.xmid }
    })
    if (res.data?.success) {
      ElMessage.success(res.data?.message || '删除关联成功！')
      await reloadLinked()
    } else {
      ElMessage.warning(res.data?.message || '删除失败')
    }
  } catch (e) {
    ElMessage.error('删除失败：' + (e.response?.data?.message || e.message))
  }
}

watch(visible, async (v) => {
  if (v) {
    try {
      if (modules.value.length === 0) await loadModules()
      await reloadLinked()
      showSearch.value = false
      currentSearch.value = null
    } catch (e) {
      ElMessage.error('初始化失败：' + (e.response?.data?.message || e.message))
    }
  }
})

onMounted(async () => {
  // 预加载一次，避免第一次打开卡顿
  try {
    await loadModules()
  } catch {
    // ignore
  }
})

const handleClose = () => {
  visible.value = false
}
</script>

<style scoped>
.special-report-dialog :deep(.el-dialog) {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  background: #fff;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.15);
}

.special-report-dialog :deep(.el-dialog__header) {
  background: #fff;
  color: #303133;
  padding: 14px 16px;
  border-bottom: 1px solid #dcdfe6;
  font-size: 16px;
  font-weight: 500;
}

.special-report-dialog :deep(.el-dialog__body) {
  padding: 16px;
  background: #fff;
}

.sr {
  position: relative;
  background: #fff;
}

.row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  margin-bottom: 12px;
}

.field {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #303133;
  flex: 1;
}

.sel {
  width: 200px;
}

.inp {
  width: 200px;
}

.group {
  position: relative;
  margin-top: 10px;
  border: 1px solid #dcdfe6;
  padding: 12px 8px 8px;
  background: #f5f7fa;
  border-radius: 4px;
}

.group-title {
  position: absolute;
  top: -10px;
  left: 10px;
  padding: 0 8px;
  background: #fff;
  font-size: 13px;
  color: #606266;
}

.desc {
  margin-top: 12px;
  display: flex;
  gap: 8px;
  font-size: 13px;
}

.desc-title {
  width: 50px;
  color: #909399;
}

.desc-text {
  flex: 1;
  line-height: 1.5;
  color: #606266;
}

.search-pop {
  position: absolute;
  left: 86px;
  top: 120px;
  width: 410px;
  border: 1px solid #dcdfe6;
  background: #fff;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.15);
  border-radius: 4px;
  z-index: 10;
}
</style>

