<template>
  <el-dialog 
    :model-value="visible" 
    @update:model-value="$emit('update:visible', $event)" 
    title="原始值查看" 
    width="800px" 
    :close-on-click-modal="false" 
    append-to-body
  >
    <div class="raw-data-dialog">
      <div class="info-bar">
        <span>样本号: {{ brxxId }}</span>
        <el-button size="small" @click="loadData" :loading="loading">刷新</el-button>
      </div>
      
      <el-table 
        :data="rawData" 
        border 
        stripe 
        size="small" 
        max-height="400"
        v-loading="loading"
      >
        <el-table-column prop="syh" label="样本号" width="100" />
        <el-table-column prop="xmmc" label="项目名称" width="150" />
        <el-table-column prop="jyjg" label="原始结果" width="120">
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
        <el-table-column prop="cjsj" label="采集时间" width="160" />
      </el-table>
      
      <div v-if="!loading && rawData.length === 0" class="empty-tip">
        暂无原始数据
      </div>
    </div>
  </el-dialog>
</template>

<script setup>
import { ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  brxxId: {
    type: Number,
    default: null
  }
})

const emit = defineEmits(['update:visible'])

const rawData = ref([])
const loading = ref(false)

const loadData = async () => {
  if (!props.brxxId) return
  
  loading.value = true
  try {
    const { data } = await axios.get(`/api/sample/rawData/${props.brxxId}`)
    rawData.value = Array.isArray(data) ? data : (data?.data || [])
  } catch (e) {
    ElMessage.error('获取原始数据失败：' + (e.response?.data?.message || e.message))
    rawData.value = []
  } finally {
    loading.value = false
  }
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

watch(() => props.visible, (val) => {
  if (val) {
    loadData()
  }
})

watch(() => props.brxxId, () => {
  if (props.visible) {
    loadData()
  }
})
</script>

<style scoped>
.info-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding: 8px 12px;
  background: #f5f7fa;
  border-radius: 4px;
}

.empty-tip {
  text-align: center;
  padding: 40px;
  color: #909399;
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
</style>
