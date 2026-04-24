<template>
  <FloatingPanel
    v-model="visible"
    title="仪器设备设置"
    :width="1200"
    :height="700"
    class="instrument-setting-panel"
  >
    <el-tabs v-model="activeTab" class="tab-wrapper">
      <!-- Tab1：仪器设备设置（旧系统第一个子界面） -->
      <el-tab-pane label="仪器设备设置" name="basic">
        <div class="dialog-body">
          <!-- 左侧：仪器设备列表 -->
          <div class="left">
            <div class="toolbar">
              <el-input
                v-model="keyword"
                size="small"
                placeholder="按设备代码/名称/拼音查询"
                class="keyword-input"
              />
              <el-button size="small" @click="loadList">查询</el-button>
            </div>
            <el-table
              :data="list"
              height="360"
              border
              size="small"
              highlight-current-row
              @current-change="handleRowChange"
            >
              <el-table-column prop="sbdm" label="设备代码" width="120" />
              <el-table-column prop="sbmc" label="设备名称" width="180" />
              <el-table-column prop="sbbm" label="设备别名" width="140" />
              <el-table-column prop="ksdm" label="科室代码" width="90" />
              <el-table-column prop="gzzdm" label="工作组代码" width="90" />
              <el-table-column prop="pym" label="拼音码" width="90" />
              <el-table-column prop="zxbz" label="执行标志" width="90">
                <template #default="{ row }">
                  <el-checkbox v-model="row.zxbz" disabled />
                </template>
              </el-table-column>
              <el-table-column prop="tybz" label="停用标志" width="90">
                <template #default="{ row }">
                  <el-checkbox v-model="row.tybz" disabled />
                </template>
              </el-table-column>
            </el-table>
          </div>

          <!-- 右侧：基础编辑区 -->
          <div class="right">
            <div class="form">
              <div class="row">
                <label>设备代码</label>
                <el-input v-model="form.sbdm" size="small" class="inp" />
              </div>
              <div class="row">
                <label>设备名称</label>
                <el-input v-model="form.sbmc" size="small" class="inp" />
              </div>
              <div class="row">
                <label>设备别名</label>
                <el-input v-model="form.sbbm" size="small" class="inp" />
              </div>
              <div class="row">
                <label>科室代码</label>
                <el-input v-model="form.ksdm" size="small" class="inp" />
              </div>
              <div class="row">
                <label>工作组代码</label>
                <el-input v-model="form.gzzdm" size="small" class="inp" />
              </div>
              <div class="row">
                <label>拼音码</label>
                <el-input v-model="form.pym" size="small" class="inp" />
              </div>
              <div class="row check-row">
                <el-checkbox v-model="form.zxbz">执行标志</el-checkbox>
                <el-checkbox v-model="form.tybz">停用标志</el-checkbox>
              </div>
            </div>

            <div class="btns">
              <el-button size="small" @click="onAdd">增加(A)</el-button>
              <el-button size="small" @click="onEdit" :disabled="!current">修改(E)</el-button>
              <el-button size="small" type="danger" @click="onDelete" :disabled="!current">删除(D)</el-button>
              <el-button size="small" @click="onCancel" :disabled="!editing">放弃(C)</el-button>
              <el-button size="small" type="primary" @click="onSave" :disabled="!editing">保存(S)</el-button>
              <el-button size="small" @click="handleClose">关闭(X)</el-button>
            </div>
          </div>
        </div>
      </el-tab-pane>

      <!-- Tab2：报告合并设置 -->
      <el-tab-pane label="报告合并设置" name="merge">
        <div class="dialog-body">
          <div class="left">
            <div class="grid-title">合并组列表</div>
            <el-table
              :data="mergeGroupList"
              height="360"
              border
              size="small"
              highlight-current-row
              @current-change="handleMergeGroupChange"
            >
              <el-table-column prop="hbid" label="合并组ID" width="80" />
              <el-table-column prop="hbmc" label="合并组名称">
                <template #default="{ row }">
                  <el-input
                    v-if="editingMergeGroup && currentMergeGroup && currentMergeGroup.hbid === row.hbid"
                    v-model="row.hbmc"
                    size="small"
                    @blur="saveMergeGroup"
                    @keyup.enter="saveMergeGroup"
                  />
                  <span v-else>{{ row.hbmc }}</span>
                </template>
              </el-table-column>
            </el-table>
            <div class="btns" style="margin-top: 8px">
              <el-button size="small" @click="onAddMergeGroup">增加合并组</el-button>
              <el-button size="small" @click="onEditMergeGroup" :disabled="!currentMergeGroup || editingMergeGroup">修改</el-button>
              <el-button size="small" @click="onDeleteMergeGroup" :disabled="!currentMergeGroup || editingMergeGroup">删除</el-button>
              <el-button v-if="editingMergeGroup" size="small" type="primary" @click="saveMergeGroup">保存</el-button>
            </div>
          </div>
          <div class="middle">
            <div class="grid-title">已分配设备</div>
            <el-table
              :data="mergeGroupDetailList"
              height="360"
              border
              size="small"
              highlight-current-row
              @current-change="handleMergeDetailChange"
            >
              <el-table-column prop="sbmc" label="设备名称" />
              <el-table-column prop="zjsm" label="主机标志" width="80" />
            </el-table>
            <div class="btns" style="margin-top: 8px">
              <el-button size="small" @click="onRemoveFromMergeGroup" :disabled="!currentMergeDetail">移除</el-button>
            </div>
          </div>
          <div class="right">
            <div class="grid-title">未分配设备</div>
            <el-table
              :data="list"
              height="360"
              border
              size="small"
              highlight-current-row
              @current-change="handleUnassignedDeviceChange"
            >
              <el-table-column prop="sbmc" label="设备名称" />
            </el-table>
            <div class="btns" style="margin-top: 8px">
              <el-button size="small" @click="onAddToMergeGroup" :disabled="!currentMergeGroup || !currentUnassignedDevice">添加</el-button>
            </div>
          </div>
        </div>
      </el-tab-pane>

      <!-- Tab3：工作组设备设置 -->
      <el-tab-pane label="工作组设备设置" name="workgroup">
        <div class="dialog-body">
          <div class="left">
            <div class="grid-title">工作组树形结构</div>
            <el-tree
              :data="workgroupTreeData"
              :props="{ children: 'children', label: 'dsp' }"
              node-key="id"
              default-expand-all
              highlight-current
              @node-click="handleWorkgroupNodeClick"
              style="height: 360px; overflow-y: auto; background: #fff; padding: 8px; border: 1px solid #ccc;"
            />
            <div class="btns" style="margin-top: 8px">
              <el-button size="small" @click="onAssignToWorkgroup" :disabled="!selectedWorkgroup || !currentUnassignedWorkgroupDevice">分配</el-button>
            </div>
          </div>
          <div class="right">
            <div class="grid-title">未分配设备</div>
            <el-table
              :data="unassignedWorkgroupDevices"
              height="360"
              border
              size="small"
              highlight-current-row
              @current-change="handleUnassignedWorkgroupDeviceChange"
            >
              <el-table-column prop="sb_djid" label="设备ID" width="80" />
              <el-table-column prop="sbmc" label="设备名称" />
            </el-table>
            <div class="btns" style="margin-top: 8px">
              <el-button size="small" @click="onUnassignFromWorkgroup" :disabled="!selectedWorkgroupDevice">取消分配</el-button>
            </div>
          </div>
        </div>
      </el-tab-pane>

      <!-- Tab4：站点设备设置 -->
      <el-tab-pane label="站点设备设置" name="site">
        <div class="dialog-body">
          <div class="left">
            <div class="grid-title">站点树形结构</div>
            <el-tree
              :data="siteTreeData"
              :props="{ children: 'children', label: 'dsp' }"
              node-key="id"
              default-expand-all
              highlight-current
              @node-click="handleSiteNodeClick"
              style="height: 360px; overflow-y: auto; background: #fff; padding: 8px; border: 1px solid #ccc;"
            />
            <div class="btns" style="margin-top: 8px">
              <el-button size="small" @click="onAssignToSite" :disabled="!selectedSite || !currentUnassignedSiteDevice">分配</el-button>
            </div>
          </div>
          <div class="right">
            <div class="grid-title">未分配设备</div>
            <el-table
              :data="unassignedSiteDevices"
              height="360"
              border
              size="small"
              highlight-current-row
              @current-change="handleUnassignedSiteDeviceChange"
            >
              <el-table-column prop="sb_djid" label="设备ID" width="80" />
              <el-table-column prop="sbmc" label="设备名称" />
            </el-table>
            <div class="btns" style="margin-top: 8px">
              <el-button size="small" @click="onUnassignFromSite" :disabled="!selectedSiteDevice">取消分配</el-button>
            </div>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>
  </FloatingPanel>
</template>

<script setup>
import { computed, reactive, ref, watch } from 'vue'
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

const activeTab = ref('basic')
const keyword = ref('')
const list = ref([])
const current = ref(null)
const editing = ref(false)

const form = reactive({
  sbDjid: null,
  sbdm: '',
  sbmc: '',
  sbbm: '',
  ksdm: '',
  gzzdm: '',
  pym: '',
  zxbz: true,
  tybz: false
})

const fillForm = (row) => {
  form.sbDjid = row?.sb_djid || null
  form.sbdm = row?.sbdm || ''
  form.sbmc = row?.sbmc || ''
  form.sbbm = row?.sbbm || ''
  form.ksdm = row?.ksdm || ''
  form.gzzdm = row?.gzzdm || ''
  form.pym = row?.pym || ''
  form.zxbz = row?.zxbz ?? true
  form.tybz = row?.tybz ?? false
}

const loadList = async () => {
  try {
    // 从localStorage获取当前用户科室代码（对应旧系统pub_ksdm）
    const userStr = localStorage.getItem('user')
    const user = userStr ? JSON.parse(userStr) : null
    
    // 确定使用的科室代码：
    // 1. 优先使用form中的值（如果用户手动输入了科室代码）
    // 2. 否则，如果form.ksdm为空，不传ksdm参数，查询所有仪器
    // 3. 这样用户可以查看所有仪器，也可以手动输入科室代码进行筛选
    let ksdm = form.ksdm
    if (!ksdm || ksdm.trim() === '') {
      // form中没有科室代码时，不传ksdm参数，查询所有仪器
      ksdm = ''
    }
    
    // 构建查询参数
    const params = {}
    if (ksdm && ksdm.trim() !== '') {
      params.ksdm = ksdm.trim()
    }
    // 注意：不传ksdm参数时，后端会查询所有仪器
    
    console.log('加载仪器列表，参数:', params, '用户信息:', user)
    const res = await axios.get('/api/basic/instrument/list', { params })
    console.log('仪器列表返回数据数量:', res.data?.length || 0)
    
    let data = res.data || []
    
    // 如果有关键词，在前端进行过滤
    if (keyword.value && keyword.value.trim() !== '') {
      const kw = keyword.value.trim().toLowerCase()
      data = data.filter(item => {
        return (item.sbdm && item.sbdm.toLowerCase().includes(kw)) ||
               (item.sbmc && item.sbmc.toLowerCase().includes(kw)) ||
               (item.pym && item.pym.toLowerCase().includes(kw))
      })
      console.log('关键词过滤后数据数量:', data.length)
    }
    
    list.value = data
    
    if (list.value.length === 0) {
      if (ksdm) {
        ElMessage.warning(`科室代码 ${ksdm} 下未查询到仪器设备数据${keyword.value ? '（已按关键词过滤）' : ''}`)
      } else if (keyword.value) {
        ElMessage.warning(`未找到匹配关键词"${keyword.value}"的仪器设备`)
      } else {
        ElMessage.warning('未查询到仪器设备数据，请检查数据库')
      }
    } else {
      console.log('成功加载仪器列表，共', list.value.length, '条')
    }
  } catch (e) {
    console.error('读取仪器列表失败:', e)
    ElMessage.error('读取仪器列表失败：' + (e.response?.data?.message || e.message))
    list.value = []
  }
}

const onAdd = () => {
  current.value = null
  fillForm({})
  editing.value = true
}

const onEdit = () => {
  if (!current.value) return
  fillForm(current.value)
  editing.value = true
}

const onCancel = () => {
  editing.value = false
  if (current.value) fillForm(current.value)
}

const onSave = async () => {
  try {
    if (!form.sbmc.trim()) {
      ElMessage.warning('设备名称不能为空')
      return
    }
    const payload = { ...form }
    const res = await axios.post('/api/basic/instrument/save', payload)
    if (res.data?.success) {
      ElMessage.success(res.data.message || '保存成功')
      editing.value = false
      await loadList()
    } else {
      ElMessage.warning(res.data?.message || '保存失败')
    }
  } catch (e) {
    ElMessage.error('保存失败：' + (e.response?.data?.message || e.message))
  }
}

const onDelete = async () => {
  if (!current.value || !current.value.sbDjid) {
    ElMessage.warning('请先选择要删除的仪器')
    return
  }
  
  try {
    await ElMessageBox.confirm(
      '确定要删除选中的仪器吗？删除后将无法恢复。',
      '删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )
    
    const res = await axios.post('/api/basic/instrument/delete', null, {
      params: { sbDjid: current.value.sbDjid }
    })
    
    if (res.data?.success) {
      ElMessage.success(res.data.message || '删除成功')
      current.value = null
      fillForm({})
      await loadList()
    } else {
      ElMessage.warning(res.data?.message || '删除失败')
    }
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('删除失败：' + (e.response?.data?.message || e.message))
    }
  }
}

const handleRowChange = (row) => {
  current.value = row
  if (!editing.value && row) fillForm(row)
}

// ==================== Tab2: 报告合并设置 ====================
const mergeGroupList = ref([])
const currentMergeGroup = ref(null)
const mergeGroupDetailList = ref([])
const currentMergeDetail = ref(null)
const currentUnassignedDevice = ref(null)

const loadMergeGroups = async () => {
  try {
    const res = await axios.get('/api/basic/instrument/merge-group/list')
    mergeGroupList.value = res.data || []
  } catch (e) {
    ElMessage.error('读取合并组列表失败：' + (e.response?.data?.message || e.message))
  }
}

const loadMergeGroupDetail = async (hbid) => {
  if (!hbid) {
    mergeGroupDetailList.value = []
    return
  }
  try {
    const res = await axios.get('/api/basic/instrument/merge-group/detail', { params: { hbid } })
    mergeGroupDetailList.value = res.data || []
  } catch (e) {
    ElMessage.error('读取合并组明细失败：' + (e.response?.data?.message || e.message))
  }
}

const handleMergeGroupChange = (row) => {
  // 如果正在编辑，先保存
  if (editingMergeGroup.value && currentMergeGroup.value) {
    saveMergeGroup()
  }
  currentMergeGroup.value = row
  editingMergeGroup.value = false
  if (row) {
    loadMergeGroupDetail(row.hbid)
  } else {
    mergeGroupDetailList.value = []
  }
}

const handleUnassignedDeviceChange = (row) => {
  currentUnassignedDevice.value = row
}

const handleMergeDetailChange = (row) => {
  currentMergeDetail.value = row
}

const onAddMergeGroup = async () => {
  // 参考旧系统BitBtn4Click：新增合并组，自动生成hbid
  const newGroup = { hbid: null, hbmc: '新合并组' }
  currentMergeGroup.value = newGroup
  mergeGroupDetailList.value = []
  editingMergeGroup.value = true
  // 立即保存以获取hbid
  await saveMergeGroup()
}

const onEditMergeGroup = async () => {
  if (!currentMergeGroup.value) return
  // 参考旧系统BitBtn5Click：进入编辑模式
  editingMergeGroup.value = true
  // 保存当前编辑的名称
  await saveMergeGroup()
}

const editingMergeGroup = ref(false)

const saveMergeGroup = async () => {
  if (!currentMergeGroup.value) return
  if (!currentMergeGroup.value.hbmc || !currentMergeGroup.value.hbmc.trim()) {
    ElMessage.warning('合并组名称不能为空')
    return
  }
  try {
    // 参考旧系统BitBtn7Click：Post保存
    const payload = {
      hbid: currentMergeGroup.value.hbid || 0,
      hbmc: currentMergeGroup.value.hbmc.trim()
    }
    const res = await axios.post('/api/basic/instrument/merge-group/save', payload)
    if (res.data?.success) {
      ElMessage.success('保存成功')
      editingMergeGroup.value = false
      const oldHbid = currentMergeGroup.value.hbid
      await loadMergeGroups()
      // 重新选中当前合并组（新增时通过名称匹配，修改时通过hbid匹配）
      if (oldHbid) {
        const found = mergeGroupList.value.find(g => g.hbid === oldHbid)
        if (found) {
          currentMergeGroup.value = found
          await loadMergeGroupDetail(found.hbid)
        }
      } else {
        // 新增时，找到名称匹配的最新记录
        const found = mergeGroupList.value.find(g => g.hbmc === payload.hbmc)
        if (found) {
          currentMergeGroup.value = found
          await loadMergeGroupDetail(found.hbid)
        }
      }
    } else {
      ElMessage.warning(res.data?.message || '保存失败')
    }
  } catch (e) {
    ElMessage.error('保存失败：' + (e.response?.data?.message || e.message))
  }
}

const onDeleteMergeGroup = async () => {
  if (!currentMergeGroup.value) return
  try {
    const res = await axios.post('/api/basic/instrument/merge-group/delete', null, { params: { hbid: currentMergeGroup.value.hbid } })
    if (res.data?.success) {
      ElMessage.success('删除成功')
      await loadMergeGroups()
      currentMergeGroup.value = null
      mergeGroupDetailList.value = []
    } else {
      ElMessage.warning(res.data?.message || '删除失败')
    }
  } catch (e) {
    ElMessage.error('删除失败：' + (e.response?.data?.message || e.message))
  }
}

const onAddToMergeGroup = async () => {
  if (!currentMergeGroup.value || !currentUnassignedDevice.value) return
  try {
    const payload = {
      hbid: currentMergeGroup.value.hbid,
      sbDjid: currentUnassignedDevice.value.sb_djid,
      sybz: false,
      bz: 1
    }
    const res = await axios.post('/api/basic/instrument/merge-group/device', payload)
    if (res.data?.success) {
      ElMessage.success('添加成功')
      await loadMergeGroupDetail(currentMergeGroup.value.hbid)
      await loadList()
    } else {
      ElMessage.warning(res.data?.message || '添加失败')
    }
  } catch (e) {
    ElMessage.error('添加失败：' + (e.response?.data?.message || e.message))
  }
}

const onRemoveFromMergeGroup = async () => {
  if (!currentMergeGroup.value || !currentMergeDetail.value) return
  try {
    const payload = {
      hbid: currentMergeGroup.value.hbid,
      sbDjid: currentMergeDetail.value.sb_djid,
      sybz: false,
      bz: 2
    }
    const res = await axios.post('/api/basic/instrument/merge-group/device', payload)
    if (res.data?.success) {
      ElMessage.success('移除成功')
      await loadMergeGroupDetail(currentMergeGroup.value.hbid)
      await loadList()
    } else {
      ElMessage.warning(res.data?.message || '移除失败')
    }
  } catch (e) {
    ElMessage.error('移除失败：' + (e.response?.data?.message || e.message))
  }
}

// ==================== Tab3: 工作组设备设置 ====================
const workgroupTreeData = ref([])
const selectedWorkgroup = ref(null)
const unassignedWorkgroupDevices = ref([])
const currentUnassignedWorkgroupDevice = ref(null)
const selectedWorkgroupDevice = ref(null)

const loadWorkgroupTree = async () => {
  try {
    const res = await axios.get('/api/basic/instrument/workgroup/tree')
    workgroupTreeData.value = buildTree(res.data || [])
  } catch (e) {
    ElMessage.error('读取工作组树失败：' + (e.response?.data?.message || e.message))
  }
}

const loadUnassignedWorkgroupDevices = async () => {
  try {
    const res = await axios.get('/api/basic/instrument/workgroup/unassigned')
    unassignedWorkgroupDevices.value = res.data || []
  } catch (e) {
    ElMessage.error('读取未分配设备失败：' + (e.response?.data?.message || e.message))
  }
}

const handleWorkgroupNodeClick = (data) => {
  if (data.pid === 0) {
    selectedWorkgroup.value = data
  } else {
    selectedWorkgroupDevice.value = data
  }
}

const handleUnassignedWorkgroupDeviceChange = (row) => {
  currentUnassignedWorkgroupDevice.value = row
}

const onAssignToWorkgroup = async () => {
  if (!selectedWorkgroup.value || !currentUnassignedWorkgroupDevice.value) return
  try {
    const payload = {
      sbDjid: currentUnassignedWorkgroupDevice.value.sb_djid,
      gzzdm: selectedWorkgroup.value.ksdm
    }
    const res = await axios.post('/api/basic/instrument/workgroup/assign', payload)
    if (res.data?.success) {
      ElMessage.success('分配成功')
      await loadWorkgroupTree()
      await loadUnassignedWorkgroupDevices()
    } else {
      ElMessage.warning(res.data?.message || '分配失败')
    }
  } catch (e) {
    ElMessage.error('分配失败：' + (e.response?.data?.message || e.message))
  }
}

const onUnassignFromWorkgroup = async () => {
  if (!selectedWorkgroupDevice.value) return
  try {
    const res = await axios.post('/api/basic/instrument/workgroup/unassign', null, { params: { sbDjid: selectedWorkgroupDevice.value.sb_djid } })
    if (res.data?.success) {
      ElMessage.success('取消分配成功')
      await loadWorkgroupTree()
      await loadUnassignedWorkgroupDevices()
    } else {
      ElMessage.warning(res.data?.message || '取消分配失败')
    }
  } catch (e) {
    ElMessage.error('取消分配失败：' + (e.response?.data?.message || e.message))
  }
}

// ==================== Tab4: 站点设备设置 ====================
const siteTreeData = ref([])
const selectedSite = ref(null)
const unassignedSiteDevices = ref([])
const currentUnassignedSiteDevice = ref(null)
const selectedSiteDevice = ref(null)

const loadSiteTree = async () => {
  try {
    const res = await axios.get('/api/basic/instrument/site/tree')
    siteTreeData.value = buildTree(res.data || [])
  } catch (e) {
    ElMessage.error('读取站点树失败：' + (e.response?.data?.message || e.message))
  }
}

const loadUnassignedSiteDevices = async () => {
  try {
    const res = await axios.get('/api/basic/instrument/site/unassigned')
    unassignedSiteDevices.value = res.data || []
  } catch (e) {
    ElMessage.error('读取未分配设备失败：' + (e.response?.data?.message || e.message))
  }
}

const handleSiteNodeClick = (data) => {
  if (data.pid === 0) {
    selectedSite.value = data
  } else {
    selectedSiteDevice.value = data
  }
}

const handleUnassignedSiteDeviceChange = (row) => {
  currentUnassignedSiteDevice.value = row
}

const onAssignToSite = async () => {
  if (!selectedSite.value || !currentUnassignedSiteDevice.value) return
  try {
    const payload = {
      sbDjid: currentUnassignedSiteDevice.value.sb_djid,
      zddm: selectedSite.value.id
    }
    const res = await axios.post('/api/basic/instrument/site/assign', payload)
    if (res.data?.success) {
      ElMessage.success('分配成功')
      await loadSiteTree()
      await loadUnassignedSiteDevices()
    } else {
      ElMessage.warning(res.data?.message || '分配失败')
    }
  } catch (e) {
    ElMessage.error('分配失败：' + (e.response?.data?.message || e.message))
  }
}

const onUnassignFromSite = async () => {
  if (!selectedSiteDevice.value) return
  try {
    const res = await axios.post('/api/basic/instrument/site/unassign', null, { params: { sbDjid: selectedSiteDevice.value.sb_djid } })
    if (res.data?.success) {
      ElMessage.success('取消分配成功')
      await loadSiteTree()
      await loadUnassignedSiteDevices()
    } else {
      ElMessage.warning(res.data?.message || '取消分配失败')
    }
  } catch (e) {
    ElMessage.error('取消分配失败：' + (e.response?.data?.message || e.message))
  }
}

// ==================== 工具函数 ====================
const buildTree = (data) => {
  const map = {}
  const roots = []
  data.forEach(item => {
    map[item.id] = { ...item, children: [] }
  })
  data.forEach(item => {
    if (item.pid === 0) {
      roots.push(map[item.id])
    } else {
      const parent = map[item.pid]
      if (parent) {
        parent.children.push(map[item.id])
      }
    }
  })
  return roots
}

watch(
  () => visible.value,
  async (v) => {
    if (v) {
      editing.value = false
      current.value = null
      // 初始化时，从用户信息中获取科室代码并设置到form中
      const userStr = localStorage.getItem('user')
      const user = userStr ? JSON.parse(userStr) : null
      if (user?.ksdm) {
        form.ksdm = user.ksdm
      }
      await loadList()
      await loadMergeGroups()
      await loadWorkgroupTree()
      await loadUnassignedWorkgroupDevices()
      await loadSiteTree()
      await loadUnassignedSiteDevices()
    }
  }
)

watch(
  () => activeTab.value,
  async (v) => {
    if (v === 'merge') {
      await loadMergeGroups()
      await loadList()
    } else if (v === 'workgroup') {
      await loadWorkgroupTree()
      await loadUnassignedWorkgroupDevices()
    } else if (v === 'site') {
      await loadSiteTree()
      await loadUnassignedSiteDevices()
    }
  }
)
</script>

<style scoped>
.instrument-setting-panel :deep(.panel-content) {
  padding: 0;
  background: #9cc6ea;
  overflow: hidden;
}

.tab-wrapper {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.tab-wrapper :deep(.el-tabs__content) {
  flex: 1;
  overflow: auto;
  padding: 8px 10px 10px;
}

.dialog-body {
  display: flex;
  gap: 8px;
  background: #9cc6ea;
}

.left {
  flex: 2;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.middle {
  flex: 2;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.right {
  flex: 2;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.grid-title {
  font-size: 12px;
  font-weight: bold;
  padding: 4px 0;
  background: #fff;
  border: 1px solid #ccc;
  text-align: center;
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

.row .inp {
  flex: 1;
}

.check-row {
  margin-top: 8px;
  gap: 16px;
}

.btns {
  display: flex;
  gap: 6px;
  justify-content: center;
  padding-top: 8px;
}
</style>


