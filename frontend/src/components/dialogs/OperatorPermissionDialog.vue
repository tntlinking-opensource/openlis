<template>
  <FloatingPanel
    v-model="visible"
    title="操作员权限设置"
    :width="1000"
    :height="600"
    class="operator-permission-panel"
  >
    <div class="dialog-body">
      <!-- 左侧：操作员列表和子系统选择 -->
      <div class="left-panel">
        <div class="search-box">
          <div class="search-row">
            <label>科室名称：</label>
            <el-input
              v-model="searchKsmc"
              size="small"
              placeholder="输入科室名称"
              @change="loadOperators"
              @keyup.enter="loadOperators"
            />
          </div>
          <div class="search-row">
            <label>操作员：</label>
            <el-input
              v-model="searchCzyxm"
              size="small"
              placeholder="输入操作员"
              @change="loadOperators"
              @keyup.enter="loadOperators"
            />
          </div>
        </div>

        <div class="operator-list">
          <div class="list-title">操作员列表：</div>
          <el-table
            :data="operators"
            height="200"
            border
            size="small"
            highlight-current-row
            @current-change="onOperatorSelect"
          >
            <el-table-column prop="czydm" label="代码" width="80" />
            <el-table-column prop="czyxm" label="姓名" width="100" />
            <el-table-column prop="ksmc" label="科室" width="120" />
          </el-table>
        </div>

        <div class="subsystem-list">
          <div class="list-title">子系统分类：</div>
          <el-table
            :data="subsystems"
            height="200"
            border
            size="small"
            highlight-current-row
            @current-change="onSubsystemSelect"
          >
            <el-table-column prop="zxtmc" label="子系统名称" />
          </el-table>
        </div>
      </div>

      <!-- 右侧：权限设置标签页 -->
      <div class="right-panel">
        <el-tabs v-model="activeTab" @tab-change="onTabChange">
          <!-- 标签1：菜单权限设置 -->
          <el-tab-pane label="菜单权限设置" name="menu">
            <div class="permission-content">
              <div class="category-list">
                <div class="list-title">菜单分类：</div>
                <el-table
                  :data="permissionCategories"
                  height="400"
                  border
                  size="small"
                  highlight-current-row
                  @current-change="onPermissionCategorySelect"
                >
                  <el-table-column prop="dlmc" label="菜单模块" />
                </el-table>
              </div>

              <div class="item-list">
                <div class="list-title">菜单名称：</div>
                <el-table
                  :data="permissionItems"
                  height="400"
                  border
                  size="small"
                >
                  <el-table-column prop="xlmc" label="菜单名称" width="200" />
                  <el-table-column label="授权" width="80" align="center">
                    <template #default="{ row }">
                      <el-checkbox
                        v-model="row.bz"
                        @change="onPermissionItemChange(row)"
                      />
                    </template>
                  </el-table-column>
                </el-table>
                <div class="hint-text">
                  在左侧选中操作员，在右侧的菜单名称中打勾即可
                </div>
              </div>
            </div>
          </el-tab-pane>

          <!-- 标签2：模块权限设置 -->
          <el-tab-pane label="模块权限设置" name="module">
            <div class="permission-content">
              <div class="category-list">
                <div class="list-title">模块分类：</div>
                <el-table
                  :data="moduleCategories"
                  height="400"
                  border
                  size="small"
                  highlight-current-row
                  @current-change="onModuleCategorySelect"
                >
                  <el-table-column prop="frm_caption" label="模块分类" />
                </el-table>
              </div>

              <div class="item-list">
                <div class="list-title">模块名称：</div>
                <el-table
                  :data="modules"
                  height="400"
                  border
                  size="small"
                >
                  <el-table-column prop="caption" label="模块名称" width="200" />
                  <el-table-column label="授权" width="80" align="center">
                    <template #default="{ row }">
                      <el-checkbox
                        v-model="row.bz"
                        @change="onModuleChange(row)"
                      />
                    </template>
                  </el-table-column>
                </el-table>
                <div class="hint-text">
                  在左侧选中操作员，在右侧的模块名称中打勾即可
                </div>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>

        <div class="footer-buttons">
          <el-button size="small" @click="onSave">保存(S)</el-button>
          <el-button size="small" @click="onCancel">取消(C)</el-button>
        </div>
      </div>
    </div>
  </FloatingPanel>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import FloatingPanel from '@/components/FloatingPanel.vue'

const props = defineProps({
  modelValue: { type: Boolean, default: false }
})

const emit = defineEmits(['update:modelValue'])

const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

// 搜索条件
const searchKsmc = ref('')
const searchCzyxm = ref('')

// 数据列表
const operators = ref([])
const subsystems = ref([])
const permissionCategories = ref([])
const permissionItems = ref([])
const moduleCategories = ref([])
const modules = ref([])

// 当前选中
const currentOperator = ref(null)
const currentSubsystem = ref(null)
const currentPermissionCategory = ref(null)
const currentModuleCategory = ref(null)

// 标签页
const activeTab = ref('menu')

// 权限变更记录（用于批量保存）
const permissionChanges = ref([])

// 加载操作员列表
const loadOperators = async () => {
  try {
    const res = await axios.get('/api/system/operator-permission/operators', {
      params: {
        czyxm: searchCzyxm.value,
        ksmc: searchKsmc.value
      }
    })
    operators.value = res.data || []
  } catch (e) {
    ElMessage.error('加载操作员列表失败：' + (e.response?.data?.message || e.message))
  }
}

// 加载子系统列表
const loadSubsystems = async () => {
  try {
    const res = await axios.get('/api/system/operator-permission/subsystems')
    subsystems.value = res.data || []
    if (subsystems.value.length > 0) {
      currentSubsystem.value = subsystems.value[0]
      onSubsystemSelect(currentSubsystem.value)
    }
  } catch (e) {
    ElMessage.error('加载子系统列表失败：' + (e.response?.data?.message || e.message))
  }
}

// 选中操作员
const onOperatorSelect = (row) => {
  currentOperator.value = row
  if (row && currentSubsystem.value) {
    loadPermissionCategories()
    loadModuleCategories()
  }
}

// 选中子系统
const onSubsystemSelect = (row) => {
  currentSubsystem.value = row
  if (row && currentOperator.value) {
    loadPermissionCategories()
    loadModuleCategories()
  }
}

// 加载权限大类
const loadPermissionCategories = async () => {
  if (!currentSubsystem.value) return
  try {
    const res = await axios.get('/api/system/operator-permission/permission-categories', {
      params: {
        zxtid: currentSubsystem.value.zxtid
      }
    })
    permissionCategories.value = res.data || []
    if (permissionCategories.value.length > 0) {
      currentPermissionCategory.value = permissionCategories.value[0]
      loadPermissionItems()
    }
  } catch (e) {
    ElMessage.error('加载权限分类失败：' + (e.response?.data?.message || e.message))
  }
}

// 选中权限大类
const onPermissionCategorySelect = (row) => {
  currentPermissionCategory.value = row
  loadPermissionItems()
}

// 加载权限小类
const loadPermissionItems = async () => {
  if (!currentPermissionCategory.value || !currentOperator.value) return
  try {
    const res = await axios.get('/api/system/operator-permission/permission-items', {
      params: {
        dldm: currentPermissionCategory.value.dldm,
        czydm: currentOperator.value.czydm
      }
    })
    permissionItems.value = res.data || []
  } catch (e) {
    ElMessage.error('加载权限项失败：' + (e.response?.data?.message || e.message))
  }
}

// 权限项变更
const onPermissionItemChange = (row) => {
  // 记录变更，用于批量保存
  const change = permissionChanges.value.find(c => c.xldm === row.xldm)
  if (change) {
    change.bz = row.bz
  } else {
    permissionChanges.value.push({
      xldm: row.xldm,
      bz: row.bz
    })
  }
}

// 加载模块分类
const loadModuleCategories = async () => {
  if (!currentSubsystem.value) return
  try {
    const res = await axios.get('/api/system/operator-permission/module-categories', {
      params: {
        zxtid: currentSubsystem.value.zxtid
      }
    })
    moduleCategories.value = res.data || []
    if (moduleCategories.value.length > 0) {
      currentModuleCategory.value = moduleCategories.value[0]
      loadModules()
    }
  } catch (e) {
    ElMessage.error('加载模块分类失败：' + (e.response?.data?.message || e.message))
  }
}

// 选中模块分类
const onModuleCategorySelect = (row) => {
  currentModuleCategory.value = row
  loadModules()
}

// 加载模块列表
const loadModules = async () => {
  if (!currentModuleCategory.value || !currentOperator.value) return
  try {
    const res = await axios.get('/api/system/operator-permission/modules', {
      params: {
        frmName: currentModuleCategory.value.frm_name,
        czydm: currentOperator.value.czydm,
        showAll: false
      }
    })
    modules.value = res.data || []
  } catch (e) {
    ElMessage.error('加载模块列表失败：' + (e.response?.data?.message || e.message))
  }
}

// 模块变更
const onModuleChange = (row) => {
  // 模块权限的保存逻辑待实现（需要调用 sys_in_czymkqx / sys_de_czymkqx）
  ElMessage.info('模块权限保存功能待实现')
}

// 标签页切换
const onTabChange = (tabName) => {
  if (tabName === 'menu' && currentPermissionCategory.value) {
    loadPermissionItems()
  } else if (tabName === 'module' && currentModuleCategory.value) {
    loadModules()
  }
}

// 保存
const onSave = async () => {
  if (!currentOperator.value) {
    ElMessage.warning('请先选择操作员')
    return
  }

  if (activeTab.value === 'menu') {
    // 保存菜单权限
    if (permissionChanges.value.length === 0) {
      ElMessage.info('没有需要保存的权限变更')
      return
    }

    try {
      const res = await axios.post('/api/system/operator-permission/save-menu-permissions', {
        czydm: currentOperator.value.czydm,
        items: permissionChanges.value
      })

      if (res.data?.success) {
        ElMessage.success('保存成功')
        permissionChanges.value = []
        // 重新加载权限项以刷新状态
        loadPermissionItems()
      } else {
        ElMessage.error(res.data?.message || '保存失败')
      }
    } catch (e) {
      ElMessage.error('保存失败：' + (e.response?.data?.message || e.message))
    }
  } else {
    ElMessage.info('模块权限保存功能待实现')
  }
}

// 取消
const onCancel = () => {
  permissionChanges.value = []
  ElMessage.info('已取消')
}

// 窗口打开时加载数据
watch(visible, (v) => {
  if (v) {
    loadOperators()
    loadSubsystems()
    permissionChanges.value = []
  }
})
</script>

<style scoped>
.dialog-body {
  display: flex;
  height: 100%;
  gap: 10px;
}

.left-panel {
  width: 280px;
  display: flex;
  flex-direction: column;
  gap: 10px;
  border-right: 1px solid #e4e7ed;
  padding-right: 10px;
}

.search-box {
  padding: 10px;
  background: #f5f7fa;
  border-radius: 4px;
}

.search-row {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
  gap: 8px;
}

.search-row label {
  width: 70px;
  font-size: 12px;
}

.search-row:last-child {
  margin-bottom: 0;
}

.operator-list,
.subsystem-list {
  flex: 1;
  min-height: 0;
}

.list-title {
  font-size: 12px;
  font-weight: bold;
  margin-bottom: 5px;
  color: #606266;
}

.right-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.permission-content {
  display: flex;
  gap: 10px;
  height: 100%;
}

.category-list {
  width: 200px;
}

.item-list {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.hint-text {
  margin-top: 10px;
  font-size: 12px;
  color: #f56c6c;
}

.footer-buttons {
  margin-top: 10px;
  text-align: right;
  padding-top: 10px;
  border-top: 1px solid #e4e7ed;
}
</style>

