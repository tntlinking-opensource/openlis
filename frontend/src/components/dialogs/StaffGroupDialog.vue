<template>
  <FloatingPanel
    v-model="visible"
    title="人员工作组设置"
    :width="900"
    :height="500"
    class="staff-group-panel"
  >
    <div class="dialog-body">
      <!-- 左侧：人员列表 -->
      <div class="left">
        <div class="toolbar">
          <el-input
            v-model="keyword"
            size="small"
            placeholder="按人员代码/姓名/拼音查询"
            class="keyword-input"
            @keyup.enter.native="loadStaff"
          />
          <el-button size="small" @click="loadStaff">查询</el-button>
        </div>
        <el-table
          :data="staffList"
          height="360"
          border
          size="small"
          highlight-current-row
          @current-change="onRowSelect"
        >
          <el-table-column prop="czydm" label="人员代码" width="90" />
          <el-table-column prop="czyxm" label="姓名" width="110" />
          <el-table-column prop="pym" label="拼音码" width="80" />
          <el-table-column prop="ksmc" label="所属科室" width="140" />
          <el-table-column prop="gzzmc" label="当前工作组" min-width="140" />
        </el-table>
      </div>

      <!-- 右侧：工作组分配 -->
      <div class="right">
        <div class="form">
          <div class="row">
            <label>人员代码</label>
            <span class="readonly">{{ currentStaff?.czydm || '' }}</span>
          </div>
          <div class="row">
            <label>姓 名</label>
            <span class="readonly">{{ currentStaff?.czyxm || '' }}</span>
          </div>
          <div class="row">
            <label>所属科室</label>
            <span class="readonly">{{ currentStaff?.ksmc || '' }}</span>
          </div>
          <div class="row">
            <label>工作组</label>
            <el-select
              v-model="selectedGroup"
              size="small"
              class="select"
              placeholder="请选择工作组"
              :disabled="!currentStaff"
            >
              <el-option
                v-for="g in groupList"
                :key="g.gzzdm"
                :label="`${g.gzzdm} - ${g.gzzmc}`"
                :value="g.gzzdm"
              />
            </el-select>
          </div>
        </div>

        <div class="btns">
          <el-button
            size="small"
            type="primary"
            :disabled="!currentStaff"
            @click="onSave"
          >
            保存(S)
          </el-button>
          <el-button size="small" @click="handleClose">
            关闭(X)
          </el-button>
        </div>
      </div>
    </div>
  </FloatingPanel>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import FloatingPanel from '@/components/FloatingPanel.vue'

const props = defineProps({
  modelValue: { type: Boolean, default: false }
})
const emit = defineEmits(['update:modelValue'])

const visible = computed({
  get: () => props.modelValue,
  set: (v) => emit('update:modelValue', v)
})

const keyword = ref('')
const staffList = ref([])
const groupList = ref([])
const currentStaff = ref(null)
const selectedGroup = ref('')

const loadStaff = async () => {
  try {
    const params = {}
    if (keyword.value && keyword.value.trim()) {
      params.keyword = keyword.value.trim()
    }
    const res = await axios.get('/api/basic/staff/group-list', { params })
    staffList.value = res.data || []
  } catch (e) {
    ElMessage.error('读取人员失败：' + (e.response?.data?.message || e.message))
  }
}

const loadGroups = async () => {
  try {
    const res = await axios.get('/api/basic/workgroup/list')
    groupList.value = res.data || []
  } catch (e) {
    ElMessage.error('读取工作组失败：' + (e.response?.data?.message || e.message))
  }
}

const onRowSelect = (row) => {
  currentStaff.value = row
  if (row) {
    selectedGroup.value = row.gzzdm || ''
  } else {
    selectedGroup.value = ''
  }
}

const onSave = async () => {
  if (!currentStaff.value) return
  try {
    const payload = {
      czydm: currentStaff.value.czydm,
      czyxm: currentStaff.value.czyxm,
      pym: currentStaff.value.pym,
      ksdm: currentStaff.value.ksdm,
      zcdm: currentStaff.value.zcdm,
      hisCzydm: currentStaff.value.hisCzydm,
      ysbz: currentStaff.value.ysbz,
      czybz: currentStaff.value.czybz,
      glybz: currentStaff.value.glybz,
      sybz: currentStaff.value.sybz,
      gzzdm: selectedGroup.value || '',
      xgbz: true,
      qkmm: false,
      czysfzhm: currentStaff.value.czysfzhm,
      qkdzqm: false
    }
    const res = await axios.post('/api/basic/staff/save', payload)
    if (res.data?.success) {
      ElMessage.success(res.data.message || '保存成功')
      await loadStaff()
    } else {
      ElMessage.warning(res.data?.message || '保存失败')
    }
  } catch (e) {
    ElMessage.error('保存失败：' + (e.response?.data?.message || e.message))
  }
}

watch(visible, async (v) => {
  if (v) {
    keyword.value = ''
    currentStaff.value = null
    selectedGroup.value = ''
    await Promise.all([loadStaff(), loadGroups()])
  }
})

const handleClose = () => {
  visible.value = false
}
</script>

<style scoped>
.staff-group-panel :deep(.panel-content) {
  padding: 8px 10px 10px;
  background: #9cc6ea;
}

.dialog-body {
  display: flex;
  gap: 8px;
  background: #9cc6ea;
}

.left {
  flex: 3;
}

.right {
  flex: 2;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.toolbar {
  display: flex;
  gap: 6px;
  margin-bottom: 6px;
}

.keyword-input {
  flex: 1;
}

.form {
  background: #fff;
  padding: 8px;
  border: 1px solid #ccc;
}

.row {
  display: flex;
  align-items: center;
  margin-bottom: 6px;
}

.row label {
  width: 80px;
  font-size: 12px;
  text-align: right;
  margin-right: 6px;
}

.row .readonly {
  font-size: 12px;
}

.select {
  flex: 1;
}

.btns {
  display: flex;
  gap: 6px;
  justify-content: center;
  padding-top: 8px;
}
</style>


