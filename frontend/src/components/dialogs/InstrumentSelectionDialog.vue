<template>
  <FloatingPanel
    v-model="visible"
    title="选择检验仪器"
    :width="900"
    :height="600"
    :resizable="true"
    :closable="true"
    class="instrument-selection-panel"
  >
    <div class="instrument-selection-content">
      <!-- Tab分页：通用、免疫、涂片、细菌（对应数据库yq_xslb字段值） -->
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="通用" name="通用">
          <div class="instrument-list">
            <el-table
              :data="currentInstrumentList"
              highlight-current-row
              @row-dblclick="handleSelect"
              @row-click="handleRowClick"
              style="width: 100%"
              height="400"
            >
              <el-table-column prop="sbmc" label="设备名称" />
              <el-table-column prop="sbbm" label="设备别名" width="120" />
              <el-table-column prop="pym" label="拼音码" width="120" />
              <el-table-column prop="hbid" label="合并号" width="100" />
              <el-table-column prop="zj" label="主机标识" width="100">
                <template #default="{ row }">
                  <span>{{ row.zj ? '主机' : '' }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="skbz" label="双控模式" width="100">
                <template #default="{ row }">
                  <span>{{ row.skbz ? '开' : '关' }}</span>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-tab-pane>
        
        <el-tab-pane label="免疫" name="免疫">
          <div class="instrument-list">
            <el-table
              :data="currentInstrumentList"
              highlight-current-row
              @row-dblclick="handleSelect"
              @row-click="handleRowClick"
              style="width: 100%"
              height="400"
            >
              <el-table-column prop="sbmc" label="设备名称" />
              <el-table-column prop="sbbm" label="设备别名" width="120" />
              <el-table-column prop="pym" label="拼音码" width="120" />
              <el-table-column prop="hbid" label="合并号" width="100" />
              <el-table-column prop="zj" label="主机标识" width="100">
                <template #default="{ row }">
                  <span>{{ row.zj ? '主机' : '' }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="skbz" label="双控模式" width="100">
                <template #default="{ row }">
                  <span>{{ row.skbz ? '开' : '关' }}</span>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-tab-pane>
        
        <el-tab-pane label="涂片" name="涂片">
          <div class="instrument-list">
            <el-table
              :data="currentInstrumentList"
              highlight-current-row
              @row-dblclick="handleSelect"
              @row-click="handleRowClick"
              style="width: 100%"
              height="400"
            >
              <el-table-column prop="sbmc" label="设备名称" />
              <el-table-column prop="sbbm" label="设备别名" width="120" />
              <el-table-column prop="pym" label="拼音码" width="120" />
              <el-table-column prop="hbid" label="合并号" width="100" />
              <el-table-column prop="zj" label="主机标识" width="100">
                <template #default="{ row }">
                  <span>{{ row.zj ? '主机' : '' }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="skbz" label="双控模式" width="100">
                <template #default="{ row }">
                  <span>{{ row.skbz ? '开' : '关' }}</span>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-tab-pane>
        
        <el-tab-pane label="细菌" name="细菌">
          <div class="instrument-list">
            <el-table
              :data="currentInstrumentList"
              highlight-current-row
              @row-dblclick="handleSelect"
              @row-click="handleRowClick"
              style="width: 100%"
              height="400"
            >
              <el-table-column prop="sbmc" label="设备名称" />
              <el-table-column prop="sbbm" label="设备别名" width="120" />
              <el-table-column prop="pym" label="拼音码" width="120" />
              <el-table-column prop="hbid" label="合并号" width="100" />
              <el-table-column prop="zj" label="主机标识" width="100">
                <template #default="{ row }">
                  <span>{{ row.zj ? '主机' : '' }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="skbz" label="双控模式" width="100">
                <template #default="{ row }">
                  <span>{{ row.skbz ? '开' : '关' }}</span>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-tab-pane>
      </el-tabs>

      <div class="selection-actions">
        <el-button type="primary" @click="handleSelect" :disabled="!selectedRow">
          确定(Q)
        </el-button>
        <el-button @click="handleCancel" v-if="allowCancel">
          取消
        </el-button>
      </div>
    </div>
  </FloatingPanel>
</template>

<script setup>
import { ref, watch, computed, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import FloatingPanel from '@/components/FloatingPanel.vue'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  allowCancel: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:modelValue', 'select', 'cancel'])

const visible = ref(props.modelValue)
const activeTab = ref('通用') // 当前激活的Tab，默认"通用"
const instrumentList = ref({
  '通用': [],  // 通用
  '免疫': [],  // 免疫
  '涂片': [],  // 涂片
  '细菌': []   // 细菌
})
const selectedRow = ref(null)

// 当前Tab的仪器列表
const currentInstrumentList = computed(() => {
  return instrumentList.value[activeTab.value] || []
})

// 监听modelValue变化
watch(() => props.modelValue, (newVal) => {
  visible.value = newVal
  if (newVal) {
    // 重置选择
    selectedRow.value = null
    // 只加载当前激活Tab的仪器列表（延迟加载其他Tab）
    loadInstrumentList(activeTab.value)
  }
})

// 监听visible变化，同步到父组件
watch(visible, (newVal) => {
  emit('update:modelValue', newVal)
})

// 加载仪器列表（按Tab类别）
const loadInstrumentList = async (tabName = null) => {
  const targetTab = tabName || activeTab.value
  try {
    console.log(`加载${targetTab}类别仪器列表...`)
    
    // 不再要求先选择检验科室，直接查询所有仪器
    // 如果需要按科室过滤，可以在后端根据用户信息自动处理
    
    // 调用后端API，按类别查询仪器设备（不传ksdm参数，查询所有）
    const res = await axios.get('/api/instrument/selection/devices', {
      params: { 
        category: targetTab,
        gzzdm: '' // 空字符串表示查询所有工作组
      }
    })
    console.log(`${targetTab}类别仪器列表:`, res.data)
    instrumentList.value[targetTab] = res.data || []

    if (instrumentList.value[targetTab].length === 0) {
      console.log(`${targetTab}类别暂无仪器设备`)
    }
  } catch (e) {
    console.error(`加载${targetTab}类别仪器列表失败:`, e)
    // 不显示错误提示，因为可能是该类别确实没有仪器
    instrumentList.value[targetTab] = []
  }
}

// Tab切换处理
const handleTabChange = (tabName) => {
  console.log('切换到Tab:', tabName)
  selectedRow.value = null // 切换Tab时清空选择
  // 如果该Tab的数据未加载，则加载
  if (instrumentList.value[tabName].length === 0) {
    loadInstrumentList(tabName)
  }
}

// 行点击
const handleRowClick = (row) => {
  selectedRow.value = row
}

// 选择检验科室
const handleSelect = () => {
  if (!selectedRow.value && instrumentList.value.length > 0) {
    // 如果没有选中行，但列表不为空，提示选择
    ElMessage.warning('请先选择检验科室')
    return
  }
  
  if (!selectedRow.value) {
    ElMessage.error('没有可选择的检验科室')
    return
  }

  console.log('选择检验科室:', selectedRow.value)
  emit('select', selectedRow.value)
  visible.value = false
}

// 取消选择
const handleCancel = () => {
  emit('cancel')
  visible.value = false
}

// 键盘事件：回车选择
const handleKeyPress = (e) => {
  if (e.key === 'Enter' && selectedRow.value) {
    handleSelect()
  }
}

onMounted(() => {
  // 添加键盘事件监听
  window.addEventListener('keypress', handleKeyPress)
})

// 组件卸载时移除事件监听
onUnmounted(() => {
  window.removeEventListener('keypress', handleKeyPress)
})
</script>

<style scoped>
.instrument-selection-content {
  padding: 20px;
  display: flex;
  flex-direction: column;
  height: 100%;
}

.selection-tip {
  margin-bottom: 20px;
}

.instrument-list {
  flex: 1;
  overflow: hidden;
}

.selection-actions {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>

