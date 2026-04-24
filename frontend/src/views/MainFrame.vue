<template>
  <div class="main-frame">
    <!-- 标题栏 -->
    <div class="title-bar">
      <div class="title-text">实验室报告系统</div>
      <div class="title-buttons">
        <button class="title-btn minimize" @click="handleMinimize" title="最小化">-</button>
        <button class="title-btn maximize" @click="handleMaximize" title="最大化">□</button>
        <button class="title-btn close" @click="handleCloseWindow" title="关闭">×</button>
      </div>
    </div>

    <!-- 菜单栏 -->
    <div class="menu-bar">
      <el-menu mode="horizontal" class="main-menu" :default-active="activeMenu">
        <!-- 样本管理[Y]：下拉菜单结构对齐旧系统（2.1~2.9），当前阶段仅实现“样本录入” -->
        <el-sub-menu index="sample">
          <template #title>样本管理[Y]</template>
          <el-menu-item index="sample-entry" @click="openSampleManagement">样本录入</el-menu-item>
          <el-menu-item index="sample-date" @click="handleSelectDateMenu">日期选择</el-menu-item>
          <el-menu-item index="sample-batch-print" disabled>批量打印中心</el-menu-item>
          <el-menu-item index="sample-sep-1" class="menu-sep" disabled />
          <el-menu-item index="sample-reload" disabled>重新提取病人信息</el-menu-item>
          <el-menu-item index="sample-new" @click="handleNew">新增样本</el-menu-item>
          <el-menu-item index="sample-save" @click="handleSave">保存样本</el-menu-item>
          <el-menu-item index="sample-audit" @click="handleAudit">审核样本</el-menu-item>
          <el-menu-item index="sample-cancel-audit" disabled>取消审核</el-menu-item>
          <el-menu-item index="sample-print" @click="handlePrint">打印报告</el-menu-item>
          <el-menu-item index="sample-modify-audit" disabled>修改审核</el-menu-item>
        </el-sub-menu>
        
        <!-- 质控[Z] -->
        <el-sub-menu index="qc">
          <template #title>质控[Z]</template>
          <el-menu-item index="qc-setup" @click="openQcManagement">质控设置</el-menu-item>
          <el-menu-item index="qc-entry" @click="openQcDaily">质控录入</el-menu-item>
          <el-menu-item index="qc-analysis" @click="openQcAnalysis">质控分析</el-menu-item>
          <el-menu-item index="qc-evaluation" disabled>质控评价</el-menu-item>
        </el-sub-menu>
        
        <!-- 仪器[M] - 选择检验仪器 -->
        <el-menu-item index="instrument" @click="openInstrumentSelection">
          <span>仪器[M]</span>
        </el-menu-item>
        
        <!-- 查询统计[T] - 启用 -->
        <el-menu-item index="query" @click="openQueryStatistics">
          <span>查询统计[T]</span>
        </el-menu-item>
        
        <!-- 系统设置[S] - 完整功能（结构对齐旧系统） -->
        <el-sub-menu index="system">
          <template #title>系统设置[S]</template>
          <!-- 顺序/分组严格按旧系统截图：1 / 4 / 2 -->
          <el-menu-item index="system-engineer" @click="openDialog('engineer')">工程师系统设置</el-menu-item>
          <el-menu-item index="system-sep-1" class="menu-sep" disabled />

          <el-menu-item index="system-lock" @click="openDialog('lock')">系统锁定  Ctrl+K</el-menu-item>
          <el-menu-item index="system-password" @click="openDialog('passwordChange')">密码修改</el-menu-item>
          <el-menu-item index="system-common-code" @click="openDialog('commonCode')">通用编码设置</el-menu-item>
          <el-menu-item index="system-special-report" @click="openDialog('specialReport')">特殊报告设置</el-menu-item>
          <el-menu-item index="system-sep-2" class="menu-sep" disabled />

          <el-menu-item index="system-process" @click="openDialog('process')">流程控制设置</el-menu-item>
          <el-menu-item index="system-permission" @click="openDialog('permission')">刷新权限</el-menu-item>
        </el-sub-menu>
        
        <!-- 基本设置[J] - 下拉长列表结构对齐旧系统 -->
        <el-sub-menu index="basic">
          <template #title>基本设置[J]</template>
          <el-menu-item index="basic-dept" @click="openDialog('dept')">科室信息设置</el-menu-item>
          <el-menu-item index="basic-personnel" @click="openDialog('staff')">人员信息设置</el-menu-item>
          <el-menu-item index="basic-workgroup" @click="openDialog('workgroup')">工作组别设置</el-menu-item>
          <el-menu-item index="basic-personnel-workgroup" @click="openDialog('personnelWorkgroup')">人员工作组设置</el-menu-item>
          <el-menu-item index="basic-patient-category" @click="openDialog('patientCategory')">病人类别设置</el-menu-item>
          <el-menu-item index="basic-instrument" @click="openDialog('instrument')">仪器设备设置</el-menu-item>

          <!-- 本阶段开发目标之外的基础设置：全部置灰不可点（明确“保留不做”） -->
          <el-menu-item index="basic-instrument-range" disabled>仪器号段设置</el-menu-item>
          <el-menu-item index="basic-test-items" disabled>检验项目设置</el-menu-item>
          <el-menu-item index="basic-specimen-type" disabled>标本类型设置</el-menu-item>
          <el-menu-item index="basic-sampling-site" disabled>采样部位设置</el-menu-item>
          <el-menu-item index="basic-test-purpose" disabled>检验目的设置</el-menu-item>
          <el-menu-item index="basic-common-remark" disabled>常用备注设置</el-menu-item>
          <el-menu-item index="basic-high-low-flag" disabled>高低标志设置</el-menu-item>
          <el-menu-item index="basic-tube-color" disabled>试管颜色设置</el-menu-item>
          <el-menu-item index="basic-personal-params" disabled>个性化参数设置</el-menu-item>
          <el-menu-item index="basic-report-setting" disabled>检验报告设置</el-menu-item>
          <el-menu-item index="basic-bone-marrow" disabled>骨髓结论设置</el-menu-item>
          <el-menu-item index="basic-bacteria-eval" disabled>细菌评价设置</el-menu-item>
          <el-menu-item index="basic-bacteria" disabled>细菌设置</el-menu-item>
          <el-menu-item index="basic-antibiotic" disabled>抗生素设置</el-menu-item>
          <el-menu-item index="basic-hospital-site" disabled>医院站点设置</el-menu-item>
          <el-menu-item index="basic-dynamic-query" disabled>动态查询设置</el-menu-item>
          <el-menu-item index="basic-instrument-site" disabled>仪器站点设置</el-menu-item>
          <el-menu-item index="basic-critical" disabled>特殊项目危急值设置</el-menu-item>
          <el-menu-item index="basic-tat" disabled>项目TAT提示设置</el-menu-item>
          <el-menu-item index="basic-result-select" disabled>常用选择结果设置</el-menu-item>
          <el-menu-item index="basic-operator-permission" @click="openDialog('operatorPermission')">操作员权限设置</el-menu-item>
        </el-sub-menu>
        
        <!-- 空菜单：其它工具 -->
        <el-menu-item index="tools" disabled>
          <span>其它工具</span>
        </el-menu-item>
      </el-menu>
    </div>

    <!-- 工具栏：移除快捷键显示 -->
    <div class="toolbar toolbar-legacy" role="toolbar" aria-label="主功能栏">
      <button class="tb-btn" type="button" title="选择日期" @click.prevent="handleSelectDate">
        <span class="tb-icon i-calendar"></span>
        <span class="tb-text">选择日期</span>
      </button>
      <div class="tb-sep" aria-hidden="true"></div>

      <button class="tb-btn" type="button" title="查找" @click.prevent="handleFind">
        <span class="tb-icon i-find"></span>
        <span class="tb-text">查找</span>
      </button>
      <button 
        class="tb-btn" 
        :class="{ 'tb-disabled': !toolbarHandlers.refresh }"
        type="button" 
        title="刷新" 
        @click.prevent="handleRefresh"
        :disabled="!toolbarHandlers.refresh"
      >
        <span class="tb-icon i-refresh"></span>
        <span class="tb-text">刷新</span>
      </button>
      <div class="tb-sep" aria-hidden="true"></div>

      <button class="tb-btn tb-primary" type="button" title="新增" @click.prevent="handleNew">
        <span class="tb-icon i-add"></span>
        <span class="tb-text">新增</span>
      </button>
      <button class="tb-btn tb-success" type="button" title="保存" @click.prevent="handleSave">
        <span class="tb-icon i-save"></span>
        <span class="tb-text">保存</span>
      </button>
      <button 
        class="tb-btn" 
        :class="{ 'tb-disabled': !toolbarHandlers.cancel }"
        type="button" 
        title="撤销" 
        @click.prevent="handleCancel"
        :disabled="!toolbarHandlers.cancel"
      >
        <span class="tb-icon i-undo"></span>
        <span class="tb-text">撤销</span>
      </button>
      <div class="tb-sep" aria-hidden="true"></div>

      <button class="tb-btn" type="button" title="检验" @click.prevent="handleTest">
        <span class="tb-icon i-test"></span>
        <span class="tb-text">检验</span>
      </button>
      <button class="tb-btn" type="button" title="审核" @click.prevent="handleAudit">
        <span class="tb-icon i-check"></span>
        <span class="tb-text">审核</span>
      </button>
      <button 
        class="tb-btn" 
        :class="{ 'tb-disabled': !toolbarHandlers.review }"
        type="button" 
        title="回顾" 
        @click.prevent="handleReview"
        :disabled="!toolbarHandlers.review"
      >
        <span class="tb-icon i-review"></span>
        <span class="tb-text">回顾</span>
      </button>
      <button 
        class="tb-btn tb-disabled" 
        type="button" 
        title="提取" 
        disabled
      >
        <span class="tb-icon i-extract"></span>
        <span class="tb-text">提取</span>
      </button>
      <div class="tb-sep" aria-hidden="true"></div>

      <button class="tb-btn" type="button" title="锁定" @click.prevent="handleLock">
        <span class="tb-icon i-lock"></span>
        <span class="tb-text">锁定</span>
      </button>
      <button 
        class="tb-btn tb-disabled" 
        type="button" 
        title="报告查询" 
        disabled
      >
        <span class="tb-icon i-report-search"></span>
        <span class="tb-text">报告查询</span>
      </button>
      <button class="tb-btn" type="button" title="打印" @click.prevent="handlePrint">
        <span class="tb-icon i-print"></span>
        <span class="tb-text">打印</span>
      </button>
      <button class="tb-btn" type="button" title="关闭" @click.prevent="handleClose">
        <span class="tb-icon i-close"></span>
        <span class="tb-text">关闭</span>
      </button>
      <button 
        class="tb-btn tb-disabled" 
        type="button" 
        title="患者360 - 功能待实现" 
        disabled
      >
        <span class="tb-icon i-360"></span>
        <span class="tb-text">患者360</span>
      </button>
    </div>

    <!-- 主工作区 - 主页图或工作面板 -->
    <div class="main-workspace">
      <!-- 使用 router-view 渲染子路由 -->
      <router-view v-if="$route.name === 'SampleManagement' || $route.name === 'QcManagement' || $route.name === 'QueryStatistics'" />
      
      <!-- 主页图：未选择设备时显示 -->
      <div v-else-if="!selectedDevice" class="home-page">
        <div class="home-page-content">
          <div class="home-page-graphics">
            <!-- 显微镜 -->
            <div class="graphic-item microscope">
              <svg viewBox="0 0 200 200" class="graphic-svg">
                <circle cx="100" cy="100" r="80" fill="none" stroke="#4a90e2" stroke-width="3"/>
                <circle cx="100" cy="100" r="50" fill="none" stroke="#4a90e2" stroke-width="2"/>
                <line x1="100" y1="20" x2="100" y2="180" stroke="#4a90e2" stroke-width="2"/>
                <line x1="20" y1="100" x2="180" y2="100" stroke="#4a90e2" stroke-width="2"/>
                <rect x="60" y="60" width="80" height="80" fill="none" stroke="#4a90e2" stroke-width="2"/>
              </svg>
            </div>
            <!-- 洗手台 -->
            <div class="graphic-item sink">
              <svg viewBox="0 0 200 200" class="graphic-svg">
                <rect x="40" y="80" width="120" height="80" fill="none" stroke="#52c41a" stroke-width="3" rx="5"/>
                <circle cx="100" cy="120" r="20" fill="none" stroke="#52c41a" stroke-width="2"/>
                <line x1="100" y1="100" x2="100" y2="140" stroke="#52c41a" stroke-width="2"/>
                <line x1="80" y1="120" x2="120" y2="120" stroke="#52c41a" stroke-width="2"/>
              </svg>
            </div>
            <!-- 笔记本 -->
            <div class="graphic-item notebook">
              <svg viewBox="0 0 200 200" class="graphic-svg">
                <rect x="50" y="60" width="100" height="80" fill="#fff3cd" stroke="#ffc107" stroke-width="3" rx="3"/>
                <line x1="50" y1="100" x2="150" y2="100" stroke="#ffc107" stroke-width="1"/>
                <line x1="50" y1="120" x2="150" y2="120" stroke="#ffc107" stroke-width="1"/>
                <line x1="50" y1="80" x2="150" y2="80" stroke="#ffc107" stroke-width="1"/>
              </svg>
            </div>
          </div>
          <div class="home-page-title">
            <h1>实验室信息管理系统</h1>
            <p class="home-page-subtitle">Laboratory Information System</p>
            <p class="home-page-hint">
              <span v-if="!lastDevice">
                当前未绑定检验仪器，请点击上方菜单栏【仪器[M]】选择检验仪器。
              </span>
              <span v-else>
                当前未绑定检验仪器，上次使用：
                <strong>{{ lastDevice.sbmc || lastDevice.sbbm || '未知设备' }}</strong>。
                您可以
                <el-button
                  type="primary"
                  size="small"
                  class="home-primary-btn"
                  @click="continueWithLastDevice"
                >
                  继续使用上次仪器
                </el-button>
                或点击上方菜单栏【仪器[M]】选择其他检验仪器。
              </span>
            </p>
          </div>
        </div>
      </div>
      
      <!-- 工作面板：选择仪器后显示（但不在样本录入或质控页面时） -->
      <template v-else-if="$route.name !== 'SampleManagement' && $route.name !== 'QcManagement'">
        <!-- 左面板：病人信息 -->
        <div class="workspace-panel workspace-panel-left">
          <div class="panel-header">病人信息</div>
          <div class="panel-content">
            <!-- TODO: 病人信息输入表单 -->
            <div class="empty-panel-hint">病人信息区域</div>
          </div>
        </div>
        
        <!-- 分割器 -->
        <div class="workspace-splitter"></div>
        
        <!-- 中面板：结果信息 -->
        <div class="workspace-panel workspace-panel-center">
          <div class="panel-header">结果信息</div>
          <div class="panel-content">
            <!-- TODO: 检验结果表格 -->
            <div class="empty-panel-hint">结果信息区域</div>
          </div>
        </div>
        
        <!-- 分割器 -->
        <div class="workspace-splitter"></div>
        
        <!-- 右面板：病人列表 -->
        <div class="workspace-panel workspace-panel-right">
          <div class="panel-header">病人列表</div>
          <div class="panel-content">
            <!-- TODO: 病人列表表格 -->
            <div class="empty-panel-hint">病人列表区域</div>
          </div>
        </div>
      </template>
    </div>

    <!-- 状态栏 -->
    <div class="status-bar">
      <div class="status-item">
        <span class="status-label">操作员：</span>
        <span class="status-value">{{ currentUser?.czyxm || currentUser?.czydm || '未知' }}</span>
      </div>
      <div class="status-item">
        <span class="status-label">仪器：</span>
        <span class="status-value">
          {{ selectedInstrument || '未选择' }}
          <el-button
            v-if="selectedDevice"
            type="text"
            size="small"
            class="unbind-btn"
            @click="unbindInstrument"
          >
            解除绑定
          </el-button>
        </span>
      </div>
      <div class="status-item">
        <span class="status-label">时间：</span>
        <span class="status-value">{{ currentTime }}</span>
      </div>
      <div class="status-item">
        <span class="status-label">数据库：</span>
        <span class="status-value">lisdata</span>
      </div>
      <div class="status-item">
        <span class="status-label">检验人：</span>
        <span class="status-value">-</span>
      </div>
      <div class="status-item">
        <span class="status-label">审核人：</span>
        <span class="status-value">-</span>
      </div>
    </div>

    <!-- 对话框容器 -->
    <EngineerSettingDialog v-model="dialogs.engineer" />
    <SystemLockDialog v-model="dialogs.lock" :current-user="currentUser" />
    <PasswordChangeDialog v-model="dialogs.passwordChange" />
    <PermissionDialog v-model="dialogs.permission" />
    <CommonCodeDialog v-model="dialogs.commonCode" />
    <SpecialReportDialog v-model="dialogs.specialReport" />
    <ProcessControlDialog v-model="dialogs.process" />
    <DeptSettingDialog v-model="dialogs.dept" />
    <StaffSettingDialog v-model="dialogs.staff" />
    <StaffGroupDialog v-model="dialogs.personnelWorkgroup" />
    <WorkgroupSettingDialog v-model="dialogs.workgroup" />
    <PatientCategorySettingDialog v-model="dialogs.patientCategory" />
    <InstrumentSettingDialog v-model="dialogs.instrument" />
    <OperatorPermissionDialog v-model="dialogs.operatorPermission" />
    <InstrumentSelectionDialog 
      v-model="dialogs.instrumentSelection"
      :allow-cancel="false"
      @select="handleInstrumentSelected"
    />
    
    <!-- 日期选择对话框 -->
    <el-dialog v-model="datePickerDialogVisible" title="选择日期" width="320px" :close-on-click-modal="false">
      <el-date-picker
        v-model="selectedDate"
        type="date"
        placeholder="选择日期"
        format="YYYY-MM-DD"
        value-format="YYYY-MM-DD"
        style="width: 100%"
      />
      <template #footer>
        <el-button @click="datePickerDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmDateSelect">确定</el-button>
      </template>
    </el-dialog>
    <!-- 其他对话框... -->
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, provide } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'
import EngineerSettingDialog from '../components/dialogs/EngineerSettingDialog.vue'
import SystemLockDialog from '../components/dialogs/SystemLockDialog.vue'
import PasswordChangeDialog from '../components/dialogs/PasswordChangeDialog.vue'
import PermissionDialog from '../components/dialogs/PermissionDialog.vue'
import CommonCodeDialog from '../components/dialogs/CommonCodeDialog.vue'
import SpecialReportDialog from '../components/dialogs/SpecialReportDialog.vue'
import ProcessControlDialog from '../components/dialogs/ProcessControlDialog.vue'
import DeptSettingDialog from '../components/dialogs/DeptSettingDialog.vue'
import StaffSettingDialog from '../components/dialogs/StaffSettingDialog.vue'
import StaffGroupDialog from '../components/dialogs/StaffGroupDialog.vue'
import WorkgroupSettingDialog from '../components/dialogs/WorkgroupSettingDialog.vue'
import PatientCategorySettingDialog from '../components/dialogs/PatientCategorySettingDialog.vue'
import InstrumentSettingDialog from './main/settings/InstrumentSettingDialog.vue'
import OperatorPermissionDialog from '../components/dialogs/OperatorPermissionDialog.vue'
import InstrumentSelectionDialog from '../components/dialogs/InstrumentSelectionDialog.vue'
const router = useRouter()
const currentUser = ref(null)
const selectedInstrument = ref('')
const selectedInstrumentInfo = ref(null) // 存储选中的检验科室完整信息
const selectedDevice = ref(null) // 存储选中的仪器设备完整信息
const lastDevice = ref(null) // 上次使用的仪器设备（用于快捷继续使用）
const currentView = ref('')
const activeMenu = ref('')

const dialogs = ref({
  engineer: false,
  lock: false,
  passwordChange: false,
  permission: false,
  commonCode: false,
  specialReport: false,
  process: false,
  dept: false,
  staff: false,
  workgroup: false,
  personnelWorkgroup: false,
  patientCategory: false,
  instrument: false,
  operatorPermission: false,
  instrumentSelection: false // 选择检验仪器对话框
})

const datePickerDialogVisible = ref(false)
const selectedDate = ref(new Date().toISOString().slice(0, 10))

const currentTime = ref('')

// 更新时间
const updateTime = () => {
  const now = new Date()
  currentTime.value = now.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

// 打开对话框
const openDialog = (dialogName) => {
  if (dialogs.value[dialogName] !== undefined) {
    dialogs.value[dialogName] = true
  }
}

const handleMenuTodo = (name) => {
  ElMessage.info(`${name}：当前仅还原菜单结构；本周先把“系统设置”6.1-6.5 完整实现并复刻界面`)
}

// 工具栏按钮处理（通过 provide/inject 机制，让子组件注册自己的处理函数）
const toolbarHandlers = ref({
  find: null,
  refresh: null,
  new: null,
  save: null,
  cancel: null,
  test: null,
  audit: null,
  review: null,
  extract: null,
  print: null,
  selectDate: null // 选择日期处理器
})

const handleFind = () => {
  if (toolbarHandlers.value.find) {
    toolbarHandlers.value.find()
  } else {
    ElMessage.info('请先进入样本录入界面')
  }
}
// 选择日期：弹出日历选择器
const handleSelectDate = () => {
  selectedDate.value = new Date().toISOString().slice(0, 10)
  datePickerDialogVisible.value = true
}

const confirmDateSelect = () => {
  datePickerDialogVisible.value = false
  if (toolbarHandlers.value.selectDate) {
    toolbarHandlers.value.selectDate(selectedDate.value)
  } else {
    ElMessage.info(`已选择日期：${selectedDate.value}（请在样本录入界面使用此功能）`)
  }
}

// 菜单日期选择
const handleSelectDateMenu = () => {
  selectedDate.value = new Date().toISOString().slice(0, 10)
  datePickerDialogVisible.value = true
}

// 刷新：刷新右侧样本列表
const handleRefresh = () => {
  if (toolbarHandlers.value.refresh) {
    toolbarHandlers.value.refresh()
  } else {
    ElMessage.info('请先进入样本录入界面')
  }
}
const handleNew = () => {
  if (toolbarHandlers.value.new) {
    toolbarHandlers.value.new()
  } else {
    /* TODO: 默认处理 */
  }
}
const handleSave = () => {
  if (toolbarHandlers.value.save) {
    toolbarHandlers.value.save()
  } else {
    /* TODO: 默认处理 */
  }
}
// 撤销：撤销当前编辑，清空表单
const handleCancel = () => {
  if (toolbarHandlers.value.cancel) {
    toolbarHandlers.value.cancel()
  } else {
    ElMessage.info('请先进入样本录入界面')
  }
}
const handleTest = () => {
  if (toolbarHandlers.value.test) {
    toolbarHandlers.value.test()
  } else {
    /* TODO: 默认处理 */
  }
}
const handleAudit = () => {
  if (toolbarHandlers.value.audit) {
    toolbarHandlers.value.audit()
  } else {
    /* TODO: 默认处理 */
  }
}
// 回顾：查看今日样本记录
const handleReview = () => {
  if (toolbarHandlers.value.review) {
    toolbarHandlers.value.review()
  } else {
    ElMessage.info('请先进入样本录入界面')
  }
}
const handleLock = () => {
  openDialog('lock')
}
const handlePrint = () => {
  if (toolbarHandlers.value.print) {
    toolbarHandlers.value.print()
  } else {
    ElMessage.info('请先进入样本录入界面')
  }
}
// 关闭：关闭当前工作区，返回图片主页
const handleClose = () => {
  selectedDevice.value = null
  router.push('/main')
}

// 打开样本管理主界面
const openSampleManagement = () => {
  // 必须先选择仪器
  if (!selectedDevice.value) {
    ElMessage.warning('请先选择检验仪器')
    dialogs.value.instrumentSelection = true
    return
  }
  router.push('/main/sample')
}

// 打开质控设置
const openQcManagement = () => {
  router.push('/main/qc?tab=setup')
}

// 打开质控录入
const openQcDaily = () => {
  router.push('/main/qc?tab=entry')
}

// 质控评价（已禁用）
const openQcEvaluation = () => {
  // 已禁用，不跳转
}

// 打开质控分析
const openQcAnalysis = () => {
  router.push('/main/qc?tab=analysis')
}

// 打开查询统计
const openQueryStatistics = () => {
  router.push('/main/query')
}

// 窗口控制
const handleMinimize = () => {
  // 浏览器环境无法真正最小化，可以隐藏窗口或提示
  console.log('最小化窗口')
}

const handleMaximize = () => {
  // 浏览器环境无法真正最大化，可以全屏或提示
  if (!document.fullscreenElement) {
    document.documentElement.requestFullscreen()
  } else {
    document.exitFullscreen()
  }
}

const handleCloseWindow = () => {
  if (confirm('确定要退出系统吗？')) {
    localStorage.removeItem('user')
    window.location.href = '/login'
  }
}

const openNotepad = () => {
  // 浏览器环境无法直接执行系统命令，提示用户
  ElMessage.info('请手动打开记事本')
  // 或者可以打开一个文本编辑器对话框
}

const openCalculator = () => {
  // 浏览器环境无法直接执行系统命令，提示用户
  ElMessage.info('请手动打开计算器')
  // 或者可以实现一个网页版计算器
}

let timeInterval = null

// 打开选择仪器对话框（点击"仪器[M]"菜单时调用）
const openInstrumentSelection = async () => {
  try {
    // 直接弹出选择仪器对话框
    dialogs.value.instrumentSelection = true
  } catch (e) {
    console.error('打开选择仪器对话框失败:', e)
    ElMessage.error('打开选择仪器对话框失败：' + (e.response?.data?.message || e.message))
  }
}

// 处理仪器设备选择（从选择仪器对话框返回的是设备，不是检验科室）
const handleInstrumentSelected = (device) => {
  console.log('选择仪器设备:', device)
  
  // 设备信息包含：sb_djid, sbmc, sbbm, yq_xslb等
  // 保存设备信息
  selectedDevice.value = device
  selectedInstrument.value = device.sbmc || device.sbbm || '未知设备'
  lastDevice.value = device
  
  // 保存到localStorage
  localStorage.setItem('selectedDevice', JSON.stringify(device))
  
  // 同时保存检验科室信息（如果设备中有ksdm）
  if (device.ksdm) {
    const instrumentInfo = {
      ksdm: device.ksdm,
      ksmc: device.ksmc || device.ksdm
    }
    localStorage.setItem('selectedInstrument', JSON.stringify(instrumentInfo))
    selectedInstrumentInfo.value = instrumentInfo
  }
  
  ElMessage.success(`已选择仪器设备：${device.sbmc || device.sbbm}`)

  // 选中仪器后，直接进入样本管理主界面（对齐旧系统：连上仪器即可开始样本录入）
  router.push('/main/sample')
}

// 继续使用上次仪器（从主页按钮触发）
const continueWithLastDevice = () => {
  if (!lastDevice.value) {
    ElMessage.warning('没有可用的历史仪器，请先选择检验仪器')
    return
  }
  selectedDevice.value = lastDevice.value
  selectedInstrument.value = lastDevice.value.sbmc || lastDevice.value.sbbm || '未知设备'
  
  // 保存到localStorage，确保页面刷新后仍然有效
  localStorage.setItem('selectedDevice', JSON.stringify(lastDevice.value))
  
  ElMessage.success(`已继续使用上次仪器：${selectedInstrument.value}`)

  // 继续使用历史仪器时，同样直接进入样本管理界面
  router.push('/main/sample')
}

// 解除当前仪器绑定并返回主页
const unbindInstrument = () => {
  if (!selectedDevice.value) {
    ElMessage.info('当前未绑定仪器')
    return
  }
  selectedDevice.value = null
  if (selectedInstrumentInfo.value) {
    selectedInstrument.value =
      selectedInstrumentInfo.value.ksmc || selectedInstrumentInfo.value.ksdm
  } else {
    selectedInstrument.value = ''
  }
  ElMessage.success('已解除当前仪器绑定，返回主页')
}

onMounted(async () => {
  // 从localStorage获取用户信息
  const userStr = localStorage.getItem('user')
  if (userStr) {
    currentUser.value = JSON.parse(userStr)
  } else {
    // 如果没有用户信息，跳转到登录页
    window.location.href = '/login'
    return
  }

  // 初始化时间
  updateTime()
  timeInterval = setInterval(updateTime, 1000)

  // 绑定快捷键
  document.addEventListener('keydown', handleKeyDown)

  // 检查是否已选择检验科室和设备
  const savedInstrument = localStorage.getItem('selectedInstrument')
  const savedDevice = localStorage.getItem('selectedDevice')
  
  if (savedInstrument) {
    selectedInstrumentInfo.value = JSON.parse(savedInstrument)
  }
  
  if (savedDevice) {
    // 保存为“上次使用的仪器”，但不在登录时自动绑定
    lastDevice.value = JSON.parse(savedDevice)
  } else if (savedInstrument) {
    // 如果只选择了检验科室，显示检验科室名称
    selectedInstrument.value = selectedInstrumentInfo.value.ksmc || selectedInstrumentInfo.value.ksdm
  }
  
  // 登录后不立即弹出选择仪器对话框，而是显示主页图
  // 用户需要点击"仪器[M]"菜单才会弹出选择对话框
})

// 提供工具栏处理器注册接口给子组件（在 setup 顶层）
provide('registerToolbarHandler', (name, handler) => {
  if (toolbarHandlers.value[name] !== undefined) {
    toolbarHandlers.value[name] = handler
  }
})

// 提供清理接口
provide('unregisterToolbarHandler', (name) => {
  if (toolbarHandlers.value[name] !== undefined) {
    toolbarHandlers.value[name] = null
  }
})

onUnmounted(() => {
  if (timeInterval) {
    clearInterval(timeInterval)
  }
  document.removeEventListener('keydown', handleKeyDown)
})

// 快捷键处理
const handleKeyDown = (event) => {
  // F2 - 打印
  if (event.key === 'F2') {
    event.preventDefault()
    handlePrint()
  }
  // F3 - 查找
  else if (event.key === 'F3') {
    event.preventDefault()
    handleFind()
  }
  // F4 - 回顾
  else if (event.key === 'F4') {
    event.preventDefault()
    handleReview()
  }
  // F5 - 刷新
  else if (event.key === 'F5') {
    event.preventDefault()
    handleRefresh()
  }
  // F7 - 检验
  else if (event.key === 'F7') {
    event.preventDefault()
    handleTest()
  }
  // F8 - 审核
  else if (event.key === 'F8') {
    event.preventDefault()
    handleAudit()
  }
  // F9 - 新增
  else if (event.key === 'F9') {
    event.preventDefault()
    handleNew()
  }
  // F10 - 保存
  else if (event.key === 'F10') {
    event.preventDefault()
    handleSave()
  }
  // F11 - 提取
  else if (event.key === 'F11') {
    event.preventDefault()
    handleExtract()
  }
  // ESC - 撤销
  else if (event.key === 'Escape') {
    event.preventDefault()
    handleCancel()
  }
}
</script>

<style scoped>
.main-frame {
  display: flex;
  flex-direction: column;
  height: 100vh;
  width: 100vw;
  overflow: hidden;
  background: #f0f2f5;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Microsoft YaHei', sans-serif;
  font-size: 13px;
}

/* 标题栏 - 现代简洁风格 */
.title-bar {
  height: 40px;
  background: linear-gradient(135deg, #1a73e8 0%, #0d47a1 100%);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px;
  user-select: none;
  box-shadow: 0 1px 3px rgba(0,0,0,0.12);
}

.title-text {
  font-size: 13px;
  font-weight: 500;
  color: #fff;
  letter-spacing: 0.5px;
}

.title-buttons {
  display: flex;
  gap: 4px;
}

.title-btn {
  width: 28px;
  height: 24px;
  border: none;
  background: rgba(255,255,255,0.15);
  cursor: pointer;
  font-size: 14px;
  font-weight: bold;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0;
  margin: 0;
  line-height: 1;
  border-radius: 4px;
  transition: all 0.2s;
}

.title-btn:hover {
  background: rgba(255,255,255,0.25);
}

.title-btn.close:hover {
  background: #ea4335;
}

.title-btn:active {
  transform: scale(0.95);
}

/* 菜单栏 - 现代简洁风格 */
.menu-bar {
  background: #ffffff;
  border-bottom: 1px solid #e4e7ed;
  min-height: 40px;
  box-shadow: 0 1px 2px rgba(0,0,0,0.05);
}

.main-menu {
  border-bottom: none;
  background: transparent;
  height: 40px;
}

.main-menu >>> .el-sub-menu__title,
.main-menu >>> .el-menu-item {
  height: 40px;
  line-height: 40px;
  padding: 0 16px;
  font-size: 13px;
  color: #303133;
  border-bottom: none;
}

.main-menu >>> .el-sub-menu__title:hover,
.main-menu >>> .el-menu-item:hover:not(.is-disabled) {
  background: #f5f7fa;
  color: #1a73e8;
}

.main-menu >>> .el-menu-item.is-disabled {
  color: #c0c4cc;
  cursor: default;
}

.main-menu >>> .el-menu-item.is-disabled:hover {
  background: transparent;
  color: #c0c4cc;
}

.main-menu >>> .el-sub-menu.is-active .el-sub-menu__title {
  background: #f5f7fa;
  color: #1a73e8;
}

.main-menu >>> .el-menu--horizontal .el-sub-menu .el-menu {
  background: #ffffff;
  border: 1px solid #e4e7ed;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border-radius: 4px;
}

/* 下拉菜单：长列表滚动 */
.main-menu >>> .el-menu--popup {
  max-height: calc(100vh - 140px);
  overflow-y: auto;
  overflow-x: hidden;
  border-radius: 4px;
  padding: 4px 0;
}

/* 系统设置：分割线 */
.main-menu >>> .menu-sep {
  height: 16px !important;
  line-height: 16px !important;
  padding: 0 !important;
  margin: 0 6px;
  background: transparent !important;
  cursor: default !important;
}

/* 工具栏 - 现代简洁风格 */
.toolbar {
  border-bottom: 1px solid #e4e7ed;
  background: #fafafa;
}

.toolbar-legacy {
  height: 48px;
  background: #ffffff;
  display: flex;
  align-items: stretch;
  padding: 4px 8px;
  gap: 4px;
  overflow: hidden;
  border-bottom: 1px solid #e4e7ed;
}

.tb-sep {
  width: 1px;
  margin: 4px 8px;
  background: #e4e7ed;
}

.tb-btn {
  padding: 4px 12px;
  height: 36px;
  border: 1px solid #e4e7ed;
  background: #ffffff;
  border-radius: 4px;
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: center;
  gap: 6px;
  cursor: pointer;
  font-size: 12px;
  color: #606266;
  transition: all 0.2s;
}

.tb-btn:hover {
  background: #f5f7fa;
  border-color: #1a73e8;
  color: #1a73e8;
}

.tb-btn:active {
  background: #ecf5ff;
}

.tb-btn.tb-disabled,
.tb-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  background: #f5f7fa;
}

.tb-btn.tb-disabled:hover,
.tb-btn:disabled:hover {
  border-color: #e4e7ed;
  color: #606266;
  background: #f5f7fa;
}

.tb-btn.tb-disabled:active,
.tb-btn:disabled:active {
  transform: none;
}

.tb-btn.tb-primary {
  background: linear-gradient(#d8ecff 0%, #c9e4ff 55%, #b7d7f0 100%);
}

.tb-btn.tb-success {
  background: linear-gradient(#dff6e5 0%, #cfeedd 55%, #bfe4cf 100%);
}

.tb-icon {
  width: 26px;
  height: 26px;
  display: inline-block;
  border-radius: 4px;
  background-color: #ffffff;
  border: 1px solid rgba(0, 0, 0, 0.10);
  box-shadow:
    0 1px 0 rgba(255, 255, 255, 0.9) inset,
    0 1px 2px rgba(0, 0, 0, 0.10);
  position: relative;
}

.tb-text {
  font-size: 11px;
  line-height: 12px;
  color: #0b1b2b;
  white-space: nowrap;
}

/* ====== 纯 CSS 图标（接近旧系统：蓝/灰/橙点缀） ====== */
.tb-icon::before,
.tb-icon::after {
  content: "";
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
}

.i-find::before {
  width: 12px;
  height: 12px;
  border: 2px solid #2d6fb8;
  border-radius: 50%;
  transform: translate(-60%, -60%);
}
.i-find::after {
  width: 8px;
  height: 2px;
  background: #2d6fb8;
  transform: translate(4px, 6px) rotate(45deg);
  border-radius: 1px;
}

.i-refresh::before {
  width: 14px;
  height: 14px;
  border: 2px solid transparent;
  border-top-color: #2d6fb8;
  border-right-color: #2d6fb8;
  border-radius: 50%;
  transform: translate(-50%, -50%) rotate(20deg);
}
.i-refresh::after {
  width: 0;
  height: 0;
  border-left: 5px solid #2d6fb8;
  border-top: 4px solid transparent;
  border-bottom: 4px solid transparent;
  transform: translate(6px, -4px) rotate(20deg);
}

.i-add::before {
  width: 14px;
  height: 14px;
  background: linear-gradient(#3aa0ff, #1f6fd1);
  border-radius: 3px;
  box-shadow: 0 0 0 1px rgba(0,0,0,0.08) inset;
}
.i-add::after {
  width: 10px;
  height: 10px;
  background:
    linear-gradient(#fff,#fff) center/2px 10px no-repeat,
    linear-gradient(#fff,#fff) center/10px 2px no-repeat;
}

.i-save::before {
  width: 16px;
  height: 14px;
  background: #2d6fb8;
  border-radius: 2px;
  transform: translate(-50%, -55%);
}
.i-save::after {
  width: 12px;
  height: 6px;
  background: #ffffff;
  border-radius: 1px;
  transform: translate(-50%, 2px);
}

.i-undo::before {
  width: 0;
  height: 0;
  border-right: 10px solid #2d6fb8;
  border-top: 7px solid transparent;
  border-bottom: 7px solid transparent;
  transform: translate(-2px, -2px);
}
.i-undo::after {
  width: 10px;
  height: 2px;
  background: #2d6fb8;
  transform: translate(4px, 6px);
  border-radius: 1px;
}

.i-test::before {
  width: 10px;
  height: 16px;
  border: 2px solid #2d6fb8;
  border-top: none;
  border-radius: 0 0 4px 4px;
  transform: translate(-50%, -45%);
}
.i-test::after {
  width: 10px;
  height: 6px;
  background: rgba(255, 87, 34, 0.75);
  transform: translate(-50%, 3px);
  border-radius: 0 0 3px 3px;
}

.i-check::before {
  width: 14px;
  height: 14px;
  border-radius: 50%;
  background: #e9f3ff;
  border: 2px solid #2d6fb8;
}
.i-check::after {
  width: 8px;
  height: 4px;
  border-left: 2px solid #2d6fb8;
  border-bottom: 2px solid #2d6fb8;
  transform: translate(-40%, -10%) rotate(-45deg);
}

.i-review::before {
  width: 14px;
  height: 14px;
  border-radius: 50%;
  border: 2px solid #2d6fb8;
}
.i-review::after {
  width: 6px;
  height: 6px;
  border-left: 2px solid #2d6fb8;
  border-bottom: 2px solid #2d6fb8;
  transform: translate(-10%, -10%) rotate(45deg);
}

.i-extract::before {
  width: 14px;
  height: 2px;
  background: #2d6fb8;
  transform: translate(-50%, 6px);
  border-radius: 1px;
}
.i-extract::after {
  width: 0;
  height: 0;
  border-top: 10px solid #2d6fb8;
  border-left: 6px solid transparent;
  border-right: 6px solid transparent;
  transform: translate(-50%, -2px);
}

.i-lock::before {
  width: 14px;
  height: 10px;
  border: 2px solid #2d6fb8;
  border-radius: 2px;
  transform: translate(-50%, -20%);
}
.i-lock::after {
  width: 10px;
  height: 8px;
  border: 2px solid #2d6fb8;
  border-bottom: none;
  border-radius: 10px 10px 0 0;
  transform: translate(-50%, -10px);
}

.i-report-search::before {
  width: 12px;
  height: 16px;
  background: #ffffff;
  border: 1px solid rgba(45,111,184,0.7);
  border-radius: 2px;
  box-shadow: 0 0 0 2px rgba(45,111,184,0.12) inset;
  transform: translate(-55%, -45%);
}
.i-report-search::after {
  width: 8px;
  height: 8px;
  border: 2px solid #2d6fb8;
  border-radius: 50%;
  transform: translate(6px, 4px);
  box-shadow: 6px 6px 0 -5px #2d6fb8;
}

.i-print::before {
  width: 16px;
  height: 8px;
  background: #2d6fb8;
  border-radius: 2px;
  transform: translate(-50%, 0px);
}
.i-print::after {
  width: 14px;
  height: 10px;
  background: #ffffff;
  border: 1px solid rgba(0,0,0,0.12);
  border-radius: 2px;
  transform: translate(-50%, -10px);
}

.i-close::before {
  width: 14px;
  height: 14px;
  background:
    linear-gradient(#d12b2b,#d12b2b) center/2px 14px no-repeat,
    linear-gradient(#d12b2b,#d12b2b) center/14px 2px no-repeat;
  transform: translate(-50%, -50%) rotate(45deg);
}
.i-close::after {
  width: 16px;
  height: 16px;
  border-radius: 3px;
  border: 1px solid rgba(209,43,43,0.35);
  background: rgba(255, 235, 238, 0.6);
  z-index: -1;
}

.i-360::before {
  width: 16px;
  height: 16px;
  border-radius: 50%;
  border: 3px solid #2d6fb8;
  border-right-color: transparent;
}
.i-360::after {
  width: 0;
  height: 0;
  border-left: 6px solid #2d6fb8;
  border-top: 4px solid transparent;
  border-bottom: 4px solid transparent;
  transform: translate(8px, -6px) rotate(20deg);
}

.i-calendar::before {
  width: 16px;
  height: 14px;
  background: #ffffff;
  border: 2px solid #2d6fb8;
  border-radius: 2px;
  transform: translate(-50%, -45%);
  box-shadow: 0 -6px 0 0 rgba(45,111,184,0.25) inset;
}
.i-calendar::after {
  width: 14px;
  height: 2px;
  background: #2d6fb8;
  transform: translate(-50%, -12px);
  border-radius: 1px;
}

/* 主工作区 - 左中右三面板布局 */
.main-workspace {
  flex: 1;
  overflow: hidden;
  background: #ffffff;
  display: flex;
  flex-direction: row;
  position: relative;
  border: none;
  margin: 0;
  min-width: 0;
}

/* 确保router-view占满宽度 */
.main-workspace > * {
  flex: 1;
  min-width: 0;
  width: 100%;
}

/* 左面板：病人信息 */
.workspace-panel-left {
  width: 281px;
  min-width: 200px;
  max-width: 400px;
  background: #fafafa;
  border-right: 1px solid #e4e7ed;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 中面板：结果信息 */
.workspace-panel-center {
  flex: 1;
  min-width: 300px;
  background: #ffffff;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 右面板：病人列表 */
.workspace-panel-right {
  width: 400px;
  min-width: 300px;
  max-width: 600px;
  background: #fafafa;
  border-left: 1px solid #e4e7ed;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 面板通用样式 */
.workspace-panel {
  background: #ffffff;
}

.panel-header {
  height: 36px;
  line-height: 36px;
  padding: 0 12px;
  background: #f5f7fa;
  border-bottom: 1px solid #e4e7ed;
  font-size: 13px;
  font-weight: 500;
  color: #303133;
}

.panel-content {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
  padding: 12px;
  background: #ffffff;
}

.empty-panel-hint {
  padding: 20px;
  text-align: center;
  color: #909399;
  font-size: 13px;
}

/* 分割器 */
.workspace-splitter {
  width: 6px;
  background: #f0f0f0;
  border-left: 1px solid #e4e7ed;
  border-right: 1px solid #e4e7ed;
  cursor: col-resize;
  min-width: 6px;
  flex-shrink: 0;
}

.workspace-splitter:hover {
  background: #e4e7ed;
}

/* 状态栏 - 现代简洁风格 */
.status-bar {
  height: 28px;
  background: #f5f7fa;
  border-top: 1px solid #e4e7ed;
  display: flex;
  align-items: center;
  padding: 0 12px;
  font-size: 12px;
  gap: 15px;
}

.status-item {
  display: flex;
  align-items: center;
  white-space: nowrap;
}

.status-label {
  color: #909399;
  margin-right: 4px;
}

.status-value {
  color: #606266;
}

/* 全局样式覆盖 - 现代简洁风格 */
>>> .el-dialog {
  border: 1px solid #e4e7ed;
}

>>> .el-dialog__header {
  background: #ffffff;
  color: #303133;
  padding: 16px 20px;
  border-bottom: 1px solid #e4e7ed;
}

>>> .el-dialog__title {
  font-size: 15px;
  font-weight: 500;
}

>>> .el-dialog__body {
  padding: 20px;
  background: #ffffff;
}

>>> .el-dialog__footer {
  padding: 12px 20px;
  background: #ffffff;
  border-top: 1px solid #e4e7ed;
}

>>> .el-button {
  border: 1px solid #dcdfe6;
  background: #ffffff;
  color: #606266;
  font-size: 13px;
  padding: 8px 16px;
  border-radius: 4px;
}

>>> .el-button:hover {
  border-color: #1a73e8;
  color: #1a73e8;
}

>>> .el-button--primary {
  background: #1a73e8;
  border-color: #1a73e8;
  color: #ffffff;
}

>>> .el-button--primary:hover {
  background: #1557b0;
  border-color: #1557b0;
}

>>> .el-input__wrapper {
  background: #ffffff;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  box-shadow: none;
}

>>> .el-input__wrapper:hover {
  border-color: #c0c4cc;
}

>>> .el-input__wrapper.is-focus {
  border-color: #1a73e8;
}

>>> .el-input__inner {
  color: #303133;
  font-size: 13px;
}

/* 表格现代化 */
>>> .el-table {
  font-size: 13px;
}

>>> .el-table th {
  background: #f5f7fa;
  color: #606266;
  font-weight: 500;
}

>>> .el-table tr:hover {
  background: #f5f7fa;
}

>>> .el-table--border .el-table__cell {
  border-color: #ebeef5;
}

/* 下拉框现代化 */
>>> .el-select {
  width: 100%;
}

>>> .el-select .el-input__wrapper {
  border-radius: 4px;
}

/* 消息提示现代化 */
>>> .el-message {
  border-radius: 4px;
}

:deep(.el-dialog__header) {
  background: #ffffff;
  color: #303133;
  padding: 16px 20px;
  border-bottom: 1px solid #e4e7ed;
}

:deep(.el-dialog__title) {
  font-size: 15px;
  font-weight: 500;
}

:deep(.el-dialog__body) {
  padding: 20px;
  background: #ffffff;
}

:deep(.el-dialog__footer) {
  padding: 12px 20px;
  background: #ffffff;
  border-top: 1px solid #e4e7ed;
}

:deep(.el-button) {
  border: 1px solid #dcdfe6;
  background: #ffffff;
  color: #606266;
  font-size: 13px;
  padding: 8px 16px;
  border-radius: 4px;
}

:deep(.el-button:hover) {
  border-color: #1a73e8;
  color: #1a73e8;
}

:deep(.el-button--primary) {
  background: #1a73e8;
  border-color: #1a73e8;
  color: #ffffff;
}

:deep(.el-button--primary:hover) {
  background: #1557b0;
  border-color: #1557b0;
}

:deep(.el-input__wrapper) {
  background: #ffffff;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  box-shadow: none;
}

:deep(.el-input__wrapper:hover) {
  border-color: #c0c4cc;
}

:deep(.el-input__wrapper.is-focus) {
  border-color: #1a73e8;
}

:deep(.el-input__inner) {
  color: #303133;
  font-size: 13px;
}

/* 表格现代化 */
:deep(.el-table) {
  font-size: 13px;
}

:deep(.el-table th) {
  background: #f5f7fa;
  color: #606266;
  font-weight: 500;
}

:deep(.el-table tr:hover) {
  background: #f5f7fa;
}

:deep(.el-table--border .el-table__cell) {
  border-color: #ebeef5;
}

/* 下拉框现代化 */
:deep(.el-select) {
  width: 100%;
}

:deep(.el-select .el-input__wrapper) {
  border-radius: 4px;
}

/* 消息提示现代化 */
:deep(.el-message) {
  border-radius: 4px;
}

/* 主页图样式 */
.home-page {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #e3f2fd 0%, #bbdefb 50%, #90caf9 100%);
  position: relative;
  overflow: hidden;
}

.home-page::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.3) 0%, transparent 70%);
  animation: pulse 4s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); opacity: 0.3; }
  50% { transform: scale(1.1); opacity: 0.5; }
}

.home-page-content {
  position: relative;
  z-index: 1;
  text-align: center;
  padding: 40px;
}

.home-page-graphics {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 80px;
  margin-bottom: 60px;
  flex-wrap: wrap;
}

.graphic-item {
  width: 200px;
  height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
  animation: float 3s ease-in-out infinite;
}

.graphic-item.microscope {
  animation-delay: 0s;
}

.graphic-item.sink {
  animation-delay: 1s;
}

.graphic-item.notebook {
  animation-delay: 2s;
}

@keyframes float {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-20px); }
}

.graphic-svg {
  width: 100%;
  height: 100%;
  filter: drop-shadow(0 4px 8px rgba(0, 0, 0, 0.1));
}

.home-page-title h1 {
  font-size: 48px;
  font-weight: bold;
  color: #1976d2;
  margin: 0 0 10px 0;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
}

.home-page-subtitle {
  font-size: 18px;
  color: #64b5f6;
  margin: 0 0 30px 0;
  font-style: italic;
}

.home-page-hint {
  font-size: 16px;
  color: #424242;
  margin: 0;
  padding: 15px 30px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 8px;
  display: inline-block;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}
</style>

