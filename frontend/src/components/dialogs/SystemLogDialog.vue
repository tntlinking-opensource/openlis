<template>
  <el-dialog :model-value="modelValue" @update:model-value="$emit('update:modelValue', $event)" title="系统日志" width="1100px" :close-on-click-modal="false" append-to-body>
    <div style="margin-bottom:12px;display:flex;gap:8px;align-items:center;flex-wrap:wrap;">
      <el-select v-model="query.zxtid" placeholder="子系统" clearable style="width:140px">
        <el-option v-for="s in systems" :key="s.zxtid" :label="s.zxtmc" :value="s.zxtid" />
      </el-select>
      <el-select v-model="query.ztid" placeholder="操作类型" clearable style="width:140px">
        <el-option v-for="t in opTypes" :key="t.ztid" :label="t.ztsm" :value="t.ztid" />
      </el-select>
      <el-select v-model="query.czydm" placeholder="操作员" clearable filterable style="width:140px">
        <el-option v-for="o in operators" :key="o.czydm" :label="o.czyxm" :value="o.czydm" />
      </el-select>
      <el-date-picker v-model="query.beginDate" type="date" value-format="YYYY-MM-DD" placeholder="开始日期" style="width:150px" />
      <el-date-picker v-model="query.endDate" type="date" value-format="YYYY-MM-DD" placeholder="结束日期" style="width:150px" />
      <el-button type="primary" @click="loadLogs">查询</el-button>
      <el-button @click="exportExcel">导出Excel</el-button>
    </div>

    <el-table :data="logs" border stripe size="small" max-height="400">
      <el-table-column prop="czrq" label="操作时间" width="160" />
      <el-table-column prop="czyxm" label="操作员" width="100" />
      <el-table-column prop="czip" label="IP地址" width="130" />
      <el-table-column prop="czmk" label="操作模块" width="140" />
      <el-table-column prop="sm" label="操作说明" />
    </el-table>

    <div style="margin-top:12px;display:flex;justify-content:flex-end;">
      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[50, 100, 200]"
        layout="total, sizes, prev, pager, next"
        @size-change="loadLogs"
        @current-change="loadLogs"
      />
    </div>
  </el-dialog>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { saveAs } from 'file-saver'
import { fetchLogSystems, fetchAllOperationTypes, querySystemLogs, searchOperators } from '../../api/systemLog'

const props = defineProps({ modelValue: Boolean })
const emit = defineEmits(['update:modelValue'])

const query = ref({ zxtid: null, ztid: null, czydm: '', beginDate: '', endDate: '' })
const systems = ref([])
const opTypes = ref([])
const operators = ref([])
const logs = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(50)

const loadSystems = async () => {
  try {
    const { data } = await fetchLogSystems()
    systems.value = Array.isArray(data) ? data : []
  } catch (e) {}
}

const loadOpTypes = async () => {
  try {
    const { data } = await fetchAllOperationTypes()
    opTypes.value = Array.isArray(data) ? data : []
  } catch (e) {}
}

const loadOperators = async () => {
  try {
    const { data } = await searchOperators({ name: '' })
    operators.value = Array.isArray(data) ? data : []
  } catch (e) {}
}

const loadLogs = async () => {
  try {
    const params = { ...query.value, pageNum: pageNum.value, pageSize: pageSize.value }
    const { data } = await querySystemLogs(params)
    logs.value = data?.data || []
    total.value = data?.total || 0
  } catch (e) {}
}

const exportExcel = async () => {
  try {
    const XLSX = await import('xlsx')
    const params = { ...query.value, pageNum: 1, pageSize: 10000 }
    const { data } = await querySystemLogs(params)
    const rows = data?.data || []
    const wsData = rows.map(r => ({
      '操作时间': r.czrq,
      '操作员': r.czyxm,
      'IP地址': r.czip || '',
      '操作模块': r.czmk || '',
      '操作说明': r.sm
    }))
    const ws = XLSX.utils.json_to_sheet(wsData)
    const wb = XLSX.utils.book_new()
    XLSX.utils.book_append_sheet(wb, ws, '操作日志')
    const wbout = XLSX.write(wb, { bookType: 'xlsx', type: 'array' })
    saveAs(new Blob([wbout], { type: 'application/octet-stream' }), `操作日志_${new Date().toISOString().slice(0, 10)}.xlsx`)
  } catch (e) {
    ElMessage.error('导出失败')
  }
}

watch(() => query.value.zxtid, () => {
  loadLogs()
})

onMounted(() => { loadSystems(); loadOpTypes(); loadOperators(); loadLogs() })
</script>
