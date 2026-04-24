<template>
  <div class="qc-management">
    <header class="page-header">
      <h2>质控管理</h2>
    </header>

    <div class="qc-tabs">
      <button 
        class="qc-tab" 
        :class="{ active: activeTab === 'setup' }" 
        @click="switchTab('setup')"
      >
        质控设置
      </button>
      <button 
        class="qc-tab" 
        :class="{ active: activeTab === 'entry' }" 
        @click="switchTab('entry')"
      >
        质控录入
      </button>
      <button 
        class="qc-tab" 
        :class="{ active: activeTab === 'analysis' }" 
        @click="switchTab('analysis')"
      >
        质控分析
      </button>
      <button 
        class="qc-tab disabled" 
        :class="{ active: activeTab === 'evaluation' }" 
        @click="switchTab('evaluation')"
        disabled
      >
        质控评价
      </button>
    </div>

    <!-- 质控设置 -->
    <div v-show="activeTab === 'setup'" class="tab-content">
      <div class="qc-setup-main">
        <!-- 左侧：质控品列表 -->
        <section class="sm-panel sm-left">
          <header class="sm-panel-header">
            <span>质控品</span>
            <el-button type="primary" size="small" @click="showProductDialog()">新增</el-button>
          </header>
          <div class="sm-panel-body">
            <div class="search-bar">
              <input 
                type="text" 
                v-model="productKeyword" 
                placeholder="搜索质控品..." 
                @keyup.enter="loadProducts"
              />
              <el-button size="small" @click="loadProducts">搜索</el-button>
            </div>
            <div class="list-container">
              <div 
                v-for="item in productList" 
                :key="item.zkpid"
                class="list-item"
                :class="{ selected: selectedProduct?.zkpid === item.zkpid }"
                @click="selectProduct(item)"
              >
                <div class="item-main">
                  <span class="item-title">{{ item.zwmc }}</span>
                  <span class="item-sub">{{ item.ph }}</span>
                </div>
                <div class="item-tags">
                  <span v-if="item.sybz" class="tag-active">使用中</span>
                  <span v-else class="tag-inactive">停用</span>
                </div>
              </div>
              <div v-if="productList.length === 0" class="empty-tip">
                暂无质控品，请新增
              </div>
            </div>
          </div>
        </section>

        <!-- 中间：已选质控品 + 质控项目管理 -->
        <section class="sm-panel sm-center">
          <div class="center-tabs">
            <button 
              class="center-tab" 
              :class="{ active: setupSubTab === 'projects' }"
              @click="setupSubTab = 'projects'"
            >
              质控项目
            </button>
            <button 
              class="center-tab" 
              :class="{ active: setupSubTab === 'rules' }"
              @click="setupSubTab = 'rules'"
            >
              规则设置
            </button>
          </div>
          
          <div class="sm-panel-body">
            <!-- 质控项目管理 -->
            <div v-show="setupSubTab === 'projects'" v-if="selectedProduct" class="project-section">
              <div class="section-header">
                <span>已选：{{ selectedProduct.zwmc }} ({{ selectedProduct.ph }})</span>
                <div>
                  <el-button type="primary" size="small" @click="showAddProjectDialog()">添加项目</el-button>
                  <el-button size="small" @click="loadProductProjects">刷新</el-button>
                </div>
              </div>
              <table class="data-table">
                <thead>
                  <tr>
                    <th>项目编码</th>
                    <th>项目名称</th>
                    <th>单位</th>
                    <th>靶值</th>
                    <th>标准差</th>
                    <th>在控范围</th>
                    <th>类型</th>
                    <th>操作</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="item in productProjectList" :key="item.zkxmid">
                    <td>{{ item.xmid }}</td>
                    <td>{{ item.xmmc }}</td>
                    <td>{{ item.xmdw }}</td>
                    <td>
                      <input 
                        v-model="item.bz" 
                        size="small" 
                        class="inline-input"
                        @change="handleUpdateProject(item)"
                      />
                    </td>
                    <td>
                      <input 
                        v-model="item.bzc" 
                        size="small" 
                        class="inline-input"
                        @change="handleUpdateProject(item)"
                      />
                    </td>
                    <td>{{ item.zkdz }} - {{ item.zkgz }}</td>
                    <td>
                      <span :class="item.dx_lx === 1 ? 'text-warning' : 'text-info'">
                        {{ item.dx_lx === 1 ? '定性' : '定量' }}
                      </span>
                    </td>
                    <td>
                      <el-button type="danger" size="small" @click="handleDeleteProject(item.zkxmid)">删除</el-button>
                    </td>
                  </tr>
                  <tr v-if="productProjectList.length === 0">
                    <td colspan="8" class="empty-cell">暂无质控项目，请添加</td>
                  </tr>
                </tbody>
              </table>
            </div>
            
            <!-- 规则设置 -->
            <div v-show="setupSubTab === 'rules'" v-if="selectedProduct" class="rules-section">
              <div class="section-header">
                <span>质控规则 - {{ selectedProduct.zwmc }}</span>
              </div>
              <div class="rules-list">
                <div class="rule-item">
                  <span class="rule-label">Westgard规则:</span>
                  <div class="rule-checkboxes">
                    <label><input type="checkbox" /> 1 2s</label>
                    <label><input type="checkbox" /> 1 3s</label>
                    <label><input type="checkbox" /> 2 2s</label>
                    <label><input type="checkbox" /> R 4s</label>
                    <label><input type="checkbox" /> 4 2s</label>
                    <label><input type="checkbox" /> 10x</label>
                  </div>
                </div>
                <div class="rule-item">
                  <span class="rule-label">失控规则:</span>
                  <div class="rule-inputs">
                    <span>偏低值: <input v-model="ruleForm.zkdz" placeholder="偏低值" class="inline-input" /></span>
                    <span>偏高值: <input v-model="ruleForm.zkgz" placeholder="偏高值" class="inline-input" /></span>
                  </div>
                </div>
              </div>
              <div class="form-actions">
                <el-button type="primary" @click="saveRules">保存规则</el-button>
              </div>
            </div>
            
            <div v-if="!selectedProduct" class="empty-tip">
              请先在左侧选择质控品
            </div>
          </div>
        </section>

        <!-- 右侧：操作按钮 -->
        <section class="sm-panel sm-right">
          <header class="sm-panel-header">操作</header>
          <div class="sm-panel-body">
            <div class="action-buttons">
              <el-button type="primary" @click="showProductDialog(selectedProduct)">修改</el-button>
              <el-button type="danger" @click="handleDeleteProduct(selectedProduct?.zkpid)">删除</el-button>
            </div>
            <div class="product-info" v-if="selectedProduct">
              <h4>质控品信息</h4>
              <div class="info-row">
                <label>名称:</label>
                <span>{{ selectedProduct.zwmc }}</span>
              </div>
              <div class="info-row">
                <label>批号:</label>
                <span>{{ selectedProduct.ph }}</span>
              </div>
              <div class="info-row">
                <label>厂家:</label>
                <span>{{ selectedProduct.sccj }}</span>
              </div>
              <div class="info-row">
                <label>有效期:</label>
                <span>{{ formatDate(selectedProduct.syrq) }}</span>
              </div>
              <div class="info-row">
                <label>说明:</label>
                <span>{{ selectedProduct.zkpsm }}</span>
              </div>
            </div>
          </div>
        </section>
      </div>
    </div>

    <!-- 质控录入 -->
    <div v-show="activeTab === 'entry'" class="tab-content">
      <div class="qc-entry-main">
        <!-- 顶部筛选 -->
        <div class="filter-bar">
          <div class="filter-group">
            <label>质控品:</label>
            <select v-model="entryProductFilter" @change="onEntryProductChange">
              <option :value="null">全部</option>
              <option v-for="p in productList" :key="p.zkpid" :value="p.zkpid">
                {{ p.zwmc }} ({{ p.ph }})
              </option>
            </select>
          </div>
          <div class="filter-group">
            <label>检验项目:</label>
            <select v-model="entryProjectFilter" @change="loadEntryData">
              <option :value="null">全部</option>
              <option v-for="p in entryProjectOptions" :key="p.zkxmid" :value="p.zkxmid">
                {{ p.xmmc }}
              </option>
            </select>
          </div>
          <div class="filter-group">
            <label>日期:</label>
            <input type="date" v-model="entryDate" @change="loadEntryData" />
          </div>
          <el-button type="primary" @click="showEntryDialog()">录入结果</el-button>
          <el-button @click="loadEntryData">刷新</el-button>
        </div>

        <!-- 录入列表 -->
        <div class="entry-content">
          <table class="data-table">
            <thead>
              <tr>
                <th>序号</th>
                <th>质控品</th>
                <th>检验项目</th>
                <th>原始结果</th>
                <th>用户结果</th>
                <th>金标准</th>
                <th>状态</th>
                <th>失控</th>
                <th>录入时间</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(item, index) in entryList" :key="item.id">
                <td>{{ index + 1 }}</td>
                <td>{{ item.zkpmc }}</td>
                <td>{{ item.xmmc }}</td>
                <td>{{ item.yssj }}</td>
                <td class="result-cell">{{ item.yhsj }}</td>
                <td>{{ item.jssj }}</td>
                <td>
                  <span :class="item.sybz ? 'text-success' : 'text-danger'">
                    {{ item.sybz ? '有效' : '无效' }}
                  </span>
                </td>
                <td>
                  <span :class="item.skbz ? 'text-danger bold' : 'text-success'">
                    {{ item.skbz ? '失控' : '在控' }}
                  </span>
                </td>
                <td>{{ formatDate(item.syrq) }}</td>
                <td>
                  <el-button type="danger" size="small" @click="handleDeleteEntry(item.id)">删除</el-button>
                </td>
              </tr>
              <tr v-if="entryList.length === 0">
                <td colspan="10" class="empty-cell">暂无质控数据</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <!-- 质控分析 -->
    <div v-show="activeTab === 'analysis'" class="tab-content">
      <div class="qc-analysis-main">
        <!-- 顶部筛选 -->
        <div class="filter-bar">
          <div class="filter-group">
            <label>质控品:</label>
            <select v-model="analysisProductFilter" @change="onAnalysisProductChange">
              <option :value="null">全部</option>
              <option v-for="p in productList" :key="p.zkpid" :value="p.zkpid">
                {{ p.zwmc }} ({{ p.ph }})
              </option>
            </select>
          </div>
          <div class="filter-group">
            <label>项目:</label>
            <select v-model="analysisProjectFilter" @change="loadAnalysisData">
              <option :value="null">全部</option>
              <option v-for="p in analysisProjectOptions" :key="p.zkxmid" :value="p.zkxmid">
                {{ p.xmmc }}
              </option>
            </select>
          </div>
          <div class="filter-group">
            <label>日期:</label>
            <input type="date" v-model="analysisBegDate" @change="loadAnalysisData" />
            <span>至</span>
            <input type="date" v-model="analysisEndDate" @change="loadAnalysisData" />
          </div>
          <el-button type="primary" @click="showAnalysisEntryDialog()">录入</el-button>
          <el-button @click="loadAnalysisData">刷新</el-button>
        </div>

        <!-- 统计卡片 -->
        <div class="stats-cards">
          <div class="stat-card">
            <div class="stat-label">总次数</div>
            <div class="stat-value">{{ analysisStats.total }}</div>
          </div>
          <div class="stat-card success">
            <div class="stat-label">有效</div>
            <div class="stat-value">{{ analysisStats.valid }}</div>
          </div>
          <div class="stat-card danger">
            <div class="stat-label">失控</div>
            <div class="stat-value">{{ analysisStats.invalid }}</div>
          </div>
          <div class="stat-card">
            <div class="stat-label">失控率</div>
            <div class="stat-value" :class="analysisStats.invalid > 0 ? 'text-danger' : ''">
              {{ analysisStats.invalidRate }}%
            </div>
          </div>
        </div>

        <!-- 分析内容 -->
        <div class="analysis-content">
          <div class="analysis-tabs">
            <button 
              :class="{ active: analysisSubTab === 'data' }"
              @click="analysisSubTab = 'data'"
            >
              质控数据
            </button>
            <button 
              :class="{ active: analysisSubTab === 'chart' }"
              @click="analysisSubTab = 'chart'"
            >
              趋势图
            </button>
          </div>

          <!-- 数据表格 -->
          <div v-show="analysisSubTab === 'data'" class="analysis-data">
            <table class="data-table">
              <thead>
                <tr>
                  <th>日期</th>
                  <th>项目</th>
                  <th>靶值</th>
                  <th>检测结果</th>
                  <th>偏倚</th>
                  <th>偏倚%</th>
                  <th>状态</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="item in analysisData" :key="item.id" 
                    :class="item.skbz ? 'row-danger' : ''">
                  <td>{{ formatDate(item.syrq) }}</td>
                  <td>{{ item.xmmc }}</td>
                  <td>{{ item.target_bz }}</td>
                  <td class="result-cell">{{ item.yhsj }}</td>
                  <td>{{ calcBias(item) }}</td>
                  <td>{{ calcBiasPercent(item) }}%</td>
                  <td>
                    <span :class="item.skbz ? 'text-danger bold' : 'text-success'">
                      {{ item.skbz ? '失控' : '在控' }}
                    </span>
                  </td>
                </tr>
                <tr v-if="analysisData.length === 0">
                  <td colspan="7" class="empty-cell">暂无数据</td>
                </tr>
              </tbody>
            </table>
          </div>

          <!-- 趋势图 (ECharts) -->
          <div v-show="analysisSubTab === 'chart'" class="analysis-chart">
            <div v-if="chartData.length === 0" class="empty-tip">
              暂无数据可显示趋势图
            </div>
            <div v-else class="chart-container">
              <div class="chart-header">
                <span>{{ currentAnalysisProject?.xmmc }} - Levey-Jennings质控图</span>
              </div>
              <div id="qcChart" style="width: 100%; height: 400px;"></div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 质控评价（禁用提示） -->
    <div v-show="activeTab === 'evaluation'" class="tab-content disabled-content">
      <div class="disabled-tip">
        <el-icon class="tip-icon"><warning /></el-icon>
        <p>质控评价功能暂未开放，敬请期待</p>
      </div>
    </div>

    <!-- 质控品编辑对话框 -->
    <el-dialog v-model="productDialogVisible" :title="editingProduct ? '编辑质控品' : '新增质控品'" width="500px">
      <el-form :model="productForm" label-width="100px">
        <el-form-item label="质控品名称" required>
          <el-input v-model="productForm.zwmc" placeholder="中文名" />
        </el-form-item>
        <el-form-item label="英文名">
          <el-input v-model="productForm.ywmc" placeholder="英文名" />
        </el-form-item>
        <el-form-item label="批号" required>
          <el-input v-model="productForm.ph" placeholder="批号" />
        </el-form-item>
        <el-form-item label="生产厂家">
          <el-input v-model="productForm.sccj" placeholder="生产厂家" />
        </el-form-item>
        <el-form-item label="说明">
          <el-input v-model="productForm.zkpsm" type="textarea" placeholder="说明" />
        </el-form-item>
        <el-form-item label="是否使用">
          <el-switch v-model="productForm.sybz" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="productDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveProduct">保存</el-button>
      </template>
    </el-dialog>

    <!-- 添加质控项目对话框 -->
    <el-dialog v-model="addProjectDialogVisible" title="添加质控项目" width="600px">
      <el-form :model="projectForm" label-width="100px">
        <el-form-item label="选择项目" required>
          <el-select v-model="projectForm.xmid" placeholder="请选择检验项目" style="width: 100%" filterable>
            <el-option v-for="x in availableProjects" :key="x.xmid" :label="x.xmzwmc + (x.xmdw ? ' (' + x.xmdw + ')' : '')" :value="x.xmid" />
          </el-select>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="靶值">
              <el-input v-model="projectForm.bz" placeholder="靶值" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="标准差">
              <el-input v-model="projectForm.bzc" placeholder="标准差" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="在控低值">
              <el-input v-model="projectForm.zkdz" placeholder="在控低值" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="在控高值">
              <el-input v-model="projectForm.zkgz" placeholder="在控高值" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="定性类型">
          <el-radio-group v-model="projectForm.dx_lx">
            <el-radio :label="0">定量</el-radio>
            <el-radio :label="1">定性</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addProjectDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveProject">保存</el-button>
      </template>
    </el-dialog>

    <!-- 质控录入对话框 -->
    <el-dialog v-model="entryDialogVisible" title="录入质控结果" width="550px">
      <el-form :model="entryForm" label-width="100px">
        <el-form-item label="选择质控品" required>
          <el-select v-model="entryForm.zkpid" placeholder="请选择质控品" style="width: 100%" @change="onEntryFormProductChange">
            <el-option v-for="p in productList" :key="p.zkpid" :label="p.zwmc + ' (' + p.ph + ')'" :value="p.zkpid" />
          </el-select>
        </el-form-item>
        <el-form-item label="选择项目" required>
          <el-select v-model="entryForm.zkxmid" placeholder="请先选择质控品" style="width: 100%" :disabled="!entryForm.zkpid">
            <el-option v-for="p in entryFormProjectOptions" :key="p.zkxmid" :label="p.xmmc + (p.xmdw ? ' (' + p.xmdw + ')' : '')" :value="p.zkxmid" />
          </el-select>
        </el-form-item>
        <el-form-item label="结果日期">
          <el-date-picker v-model="entryForm.resultDate" type="date" placeholder="选择日期" style="width: 100%" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="原始结果">
          <el-input v-model="entryForm.yssj" placeholder="原始结果" />
        </el-form-item>
        <el-form-item label="用户结果">
          <el-input v-model="entryForm.yhsj" placeholder="用户结果(系统自动判断失控)" />
        </el-form-item>
        <el-form-item label="金标准结果">
          <el-input v-model="entryForm.jssj" placeholder="金标准结果" />
        </el-form-item>
        <el-form-item label="是否有效">
          <el-switch v-model="entryForm.sybz" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="entryDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveEntry">保存</el-button>
      </template>
    </el-dialog>

    <!-- 质控分析录入对话框 -->
    <el-dialog v-model="analysisEntryDialogVisible" title="录入质控结果" width="500px">
      <el-form :model="analysisEntryForm" label-width="100px">
        <el-form-item label="选择质控品" required>
          <el-select v-model="analysisEntryForm.zkpid" placeholder="请选择质控品" style="width: 100%" @change="onAnalysisEntryProductChange">
            <el-option v-for="p in productList" :key="p.zkpid" :label="p.zwmc + ' (' + p.ph + ')'" :value="p.zkpid" />
          </el-select>
        </el-form-item>
        <el-form-item label="选择项目" required>
          <el-select v-model="analysisEntryForm.zkxmid" placeholder="请先选择质控品" style="width: 100%" :disabled="!analysisEntryForm.zkpid">
            <el-option v-for="p in analysisEntryProjectOptions" :key="p.zkxmid" :label="p.xmmc + (p.xmdw ? ' (' + p.xmdw + ')' : '')" :value="p.zkxmid" />
          </el-select>
        </el-form-item>
        <el-form-item label="原始结果">
          <el-input v-model="analysisEntryForm.yssj" placeholder="原始结果" />
        </el-form-item>
        <el-form-item label="用户结果">
          <el-input v-model="analysisEntryForm.yhsj" placeholder="用户结果" />
        </el-form-item>
        <el-form-item label="金标准结果">
          <el-input v-model="analysisEntryForm.jssj" placeholder="金标准结果" />
        </el-form-item>
        <el-form-item label="是否有效">
          <el-switch v-model="analysisEntryForm.sybz" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="analysisEntryDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveAnalysisEntry">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import * as echarts from 'echarts'
import { fetchQcProducts, addQcProduct, updateQcProduct, deleteQcProduct, fetchDailyResults, addDailyResult, deleteDailyResult, fetchQcProjects, addQcProject, deleteQcProject, fetchAllProjects, updateQcProject, fetchAvailableProjects, fetchQcAnalysis } from '@/api/qc'

const route = useRoute()
const router = useRouter()

const activeTab = ref('setup')
const setupSubTab = ref('projects')

const switchTab = (tab) => {
  if (tab === 'evaluation') return // 禁用
  activeTab.value = tab
  router.push('/main/qc?tab=' + tab)
}

watch(() => route.query.tab, (newTab) => {
  if (newTab && ['setup', 'entry', 'analysis', 'evaluation'].includes(newTab)) {
    activeTab.value = newTab
  }
}, { immediate: true })

// ============ 质控品管理 ============
const productList = ref([])
const productKeyword = ref('')
const productDialogVisible = ref(false)
const editingProduct = ref(null)
const selectedProduct = ref(null)
const productForm = reactive({
  zwmc: '',
  ywmc: '',
  ph: '',
  sccj: '',
  zkpsm: '',
  sybz: true
})

const loadProducts = async () => {
  try {
    const res = await fetchQcProducts({ keyword: productKeyword.value })
    productList.value = res.data || []
  } catch (e) {
    ElMessage.error('加载质控品失败')
  }
}

const selectProduct = (item) => {
  selectedProduct.value = item
  loadProductProjects()
}

const showProductDialog = (item = null) => {
  editingProduct.value = item
  if (item) {
    Object.assign(productForm, {
      zwmc: item.zwmc || '',
      ywmc: item.ywmc || '',
      ph: item.ph || '',
      sccj: item.sccj || '',
      zkpsm: item.zkpsm || '',
      sybz: item.sybz ?? true
    })
  } else {
    Object.assign(productForm, {
      zwmc: '', ywmc: '', ph: '', sccj: '', zkpsm: '', sybz: true
    })
  }
  productDialogVisible.value = true
}

const handleSaveProduct = async () => {
  if (!productForm.zwmc || !productForm.ph) {
    return ElMessage.warning('请填写必填项')
  }
  try {
    if (editingProduct.value) {
      await updateQcProduct(editingProduct.value.zkpid, productForm)
      ElMessage.success('修改成功')
    } else {
      const res = await addQcProduct(productForm)
      if (res.data.success) {
        ElMessage.success(res.data.message || '添加成功')
      } else {
        ElMessage.error(res.data.message || '添加失败')
      }
    }
    productDialogVisible.value = false
    loadProducts()
  } catch (e) {
    console.error('保存质控品失败:', e)
    ElMessage.error(e.response?.data?.message || e.message || '操作失败')
  }
}

const handleDeleteProduct = async (zkpid) => {
  if (!zkpid) return
  try {
    await ElMessageBox.confirm('确定要删除该质控品吗?', '提示', { type: 'warning' })
    await deleteQcProduct(zkpid)
    ElMessage.success('删除成功')
    if (selectedProduct.value?.zkpid === zkpid) {
      selectedProduct.value = null
    }
    loadProducts()
  } catch (e) {
    if (e !== 'cancel') ElMessage.error(e.message || '删除失败')
  }
}

// ============ 质控项目管理 ============
const productProjectList = ref([])
const addProjectDialogVisible = ref(false)
const availableProjects = ref([])
const projectForm = reactive({
  xmid: null, bz: '', bzc: '', zkdz: '', zkgz: '', dx_lx: 0
})

const loadProductProjects = async () => {
  if (!selectedProduct.value) return
  try {
    const res = await fetchQcProjects(selectedProduct.value.zkpid)
    productProjectList.value = res.data || []
  } catch (e) {
    console.error(e)
  }
}

const showAddProjectDialog = async () => {
  if (!selectedProduct.value) return ElMessage.warning('请先选择质控品')
  try {
    const res = await fetchAvailableProjects(selectedProduct.value.zkpid)
    availableProjects.value = res.data || []
    projectForm.xmid = null
    projectForm.bz = ''
    projectForm.bzc = ''
    projectForm.zkdz = ''
    projectForm.zkgz = ''
    projectForm.dx_lx = 0
    addProjectDialogVisible.value = true
  } catch (e) {
    ElMessage.error('加载可选项目失败')
  }
}

const handleSaveProject = async () => {
  if (!projectForm.xmid) return ElMessage.warning('请选择项目')
  try {
    await addQcProject({
      zkpid: selectedProduct.value.zkpid,
      ...projectForm
    })
    ElMessage.success('添加成功')
    addProjectDialogVisible.value = false
    loadProductProjects()
  } catch (e) {
    ElMessage.error(e.message || '添加失败')
  }
}

const handleUpdateProject = async (item) => {
  try {
    await updateQcProject(item.zkxmid, {
      bz: item.bz, bzc: item.bzc, zkdz: item.zkdz, zkgz: item.zkgz, dx_lx: item.dx_lx
    })
  } catch (e) {
    ElMessage.error('保存失败')
  }
}

const handleDeleteProject = async (zkxmid) => {
  try {
    await ElMessageBox.confirm('确定要删除该项目吗?', '提示', { type: 'warning' })
    await deleteQcProject(zkxmid)
    ElMessage.success('删除成功')
    loadProductProjects()
  } catch (e) {
    if (e !== 'cancel') ElMessage.error(e.message || '删除失败')
  }
}

// ============ 规则设置 ============
const ruleForm = reactive({ zkdz: '', zkgz: '' })
const saveRules = () => {
  ElMessage.success('规则保存成功')
}

// ============ 质控录入 ============
const entryList = ref([])
const entryProductFilter = ref(null)
const entryProjectFilter = ref(null)
const entryDate = ref(new Date().toISOString().slice(0, 10))
const entryDialogVisible = ref(false)
const entryProjectOptions = ref([])
const entryFormProjectOptions = ref([])
const entryForm = reactive({
  zkpid: null, zkxmid: null, yssj: '', yhsj: '', jssj: '', sybz: true, resultDate: ''
})

const onEntryProductChange = async () => {
  entryProjectFilter.value = null
  await loadEntryData()
  if (entryProductFilter.value) {
    try {
      const res = await fetchQcProjects(entryProductFilter.value)
      entryProjectOptions.value = res.data || []
    } catch (e) {}
  } else {
    entryProjectOptions.value = []
  }
}

const loadEntryData = async () => {
  try {
    const params = { date: entryDate.value }
    if (entryProductFilter.value) params.zkpid = entryProductFilter.value
    const res = await fetchDailyResults(params)
    let data = res.data || []
    if (entryProjectFilter.value) {
      data = data.filter(d => d.zkxmid === entryProjectFilter.value)
    }
    entryList.value = data
  } catch (e) {
    ElMessage.error('加载质控数据失败')
  }
}

const showEntryDialog = () => {
  entryForm.zkpid = null
  entryForm.zkxmid = null
  entryForm.resultDate = new Date().toISOString().slice(0, 10)
  entryForm.yssj = ''
  entryForm.yhsj = ''
  entryForm.jssj = ''
  entryForm.sybz = true
  entryFormProjectOptions.value = []
  entryDialogVisible.value = true
}

const onEntryFormProductChange = async () => {
  if (entryForm.zkpid) {
    try {
      const res = await fetchQcProjects(entryForm.zkpid)
      entryFormProjectOptions.value = res.data || []
    } catch (e) {}
  } else {
    entryFormProjectOptions.value = []
  }
}

const handleSaveEntry = async () => {
  if (!entryForm.zkpid || !entryForm.zkxmid) {
    return ElMessage.warning('请选择质控品和项目')
  }
  try {
    const res = await addDailyResult(entryForm)
    if (res.data.success) {
      ElMessage.success(res.data.message || '保存成功')
      entryDialogVisible.value = false
      loadEntryData()
    } else {
      ElMessage.warning(res.data.message || '保存失败')
    }
  } catch (e) {
    ElMessage.error(e.message || '保存失败')
  }
}

const handleDeleteEntry = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该记录吗?', '提示', { type: 'warning' })
    await deleteDailyResult(id)
    ElMessage.success('删除成功')
    loadEntryData()
  } catch (e) {
    if (e !== 'cancel') ElMessage.error(e.message || '删除失败')
  }
}

// ============ 质控分析 ============
const analysisProductFilter = ref(null)
const analysisProjectFilter = ref(null)
const analysisProjectOptions = ref([])
const analysisBegDate = ref('')
const analysisEndDate = ref('')
const analysisSubTab = ref('data')
const analysisEntryDialogVisible = ref(false)
const analysisEntryProjectOptions = ref([])
const analysisEntryForm = reactive({
  zkpid: null, zkxmid: null, yssj: '', yhsj: '', jssj: '', sybz: true
})
const analysisData = ref([])
const analysisStats = reactive({ total: 0, valid: 0, invalid: 0, invalidRate: '0.0' })

// 趋势图数据 - 根据选中的质控品和项目过滤
const chartData = computed(() => {
  let data = analysisData.value
  
  // 调试：打印原始数据
  console.log('chartData原始数据条数:', data.length)
  console.log('选中的质控品:', analysisProductFilter.value, '项目:', analysisProjectFilter.value)
  if (data.length > 0) {
    console.log('chartData第一项zkpid类型:', typeof data[0].zkpid, 'zkxmid类型:', typeof data[0].zkxmid)
  }
  
  // 转换为数字进行比较（防止类型不匹配）
  const productId = analysisProductFilter.value ? Number(analysisProductFilter.value) : null
  const projectId = analysisProjectFilter.value ? Number(analysisProjectFilter.value) : null
  
  // 如果选择了质控品，只显示该质控品的数据
  if (productId) {
    data = data.filter(item => Number(item.zkpid) === productId)
  }
  // 如果选择了项目，只显示该项目的数据
  if (projectId) {
    data = data.filter(item => Number(item.zkxmid) === projectId)
  }
  
  console.log('chartData过滤后数据条数:', data.length)
  if (data.length > 0) {
    console.log('chartData第一项:', JSON.stringify(data[0]))
  }
  
  return data.slice(0, 30) // 最多显示30个点
})

const onAnalysisProductChange = async () => {
  analysisProjectFilter.value = null
  await loadAnalysisData()
  if (analysisProductFilter.value) {
    try {
      const res = await fetchQcProjects(analysisProductFilter.value)
      analysisProjectOptions.value = res.data || []
    } catch (e) {}
  } else {
    analysisProjectOptions.value = []
  }
}

const loadAnalysisData = async () => {
  try {
    const params = {}
    if (analysisProductFilter.value) params.zkpid = analysisProductFilter.value
    if (analysisProjectFilter.value) params.zkxmid = analysisProjectFilter.value
    if (analysisBegDate.value) params.begDate = analysisBegDate.value
    if (analysisEndDate.value) params.endDate = analysisEndDate.value
    if (!analysisBegDate.value && !analysisEndDate.value) {
      params.days = 30
    }
    
    const res = await fetchQcAnalysis(params)
    const result = res.data || {}
    analysisStats.total = result.total || 0
    analysisStats.valid = result.valid || 0
    analysisStats.invalid = result.invalid || 0
    analysisStats.invalidRate = result.invalidRate || '0.0'
    analysisData.value = result.data || []
  } catch (e) {
    console.error(e)
  }
}

const showAnalysisEntryDialog = () => {
  analysisEntryForm.zkpid = null
  analysisEntryForm.zkxmid = null
  analysisEntryForm.yssj = ''
  analysisEntryForm.yhsj = ''
  analysisEntryForm.jssj = ''
  analysisEntryForm.sybz = true
  analysisEntryProjectOptions.value = []
  analysisEntryDialogVisible.value = true
}

const onAnalysisEntryProductChange = async () => {
  if (analysisEntryForm.zkpid) {
    try {
      const res = await fetchQcProjects(analysisEntryForm.zkpid)
      analysisEntryProjectOptions.value = res.data || []
    } catch (e) {}
  } else {
    analysisEntryProjectOptions.value = []
  }
}

const handleSaveAnalysisEntry = async () => {
  if (!analysisEntryForm.zkpid || !analysisEntryForm.zkxmid) {
    return ElMessage.warning('请选择质控品和项目')
  }
  try {
    const res = await addDailyResult(analysisEntryForm)
    if (res.data.success) {
      ElMessage.success(res.data.message || '保存成功')
      analysisEntryDialogVisible.value = false
      loadAnalysisData()
    } else {
      ElMessage.warning(res.data.message || '保存失败')
    }
  } catch (e) {
    ElMessage.error(e.message || '保存失败')
  }
}

const currentAnalysisProject = computed(() => {
  if (!analysisProjectFilter.value) return null
  return analysisProjectOptions.value.find(p => p.zkxmid === analysisProjectFilter.value)
})

const calcBias = (item) => {
  if (!item.yhsj || !item.target_bz) return '-'
  const yhsj = parseFloat(item.yhsj)
  const bz = parseFloat(item.target_bz)
  if (isNaN(yhsj) || isNaN(bz) || bz === 0) return '-'
  return (yhsj - bz).toFixed(2)
}

const calcBiasPercent = (item) => {
  if (!item.yhsj || !item.target_bz) return '-'
  const yhsj = parseFloat(item.yhsj)
  const bz = parseFloat(item.target_bz)
  if (isNaN(yhsj) || isNaN(bz) || bz === 0) return '-'
  return ((yhsj - bz) / bz * 100).toFixed(1)
}

const getChartY = (item) => {
  const yhsjVal = item.yhsj
  const bzVal = item.target_bz
  const bzcVal = item.target_bzc
  
  if (!yhsjVal || !bzVal) {
    console.log('Chart DEBUG: yhsj=', yhsjVal, 'target_bz=', bzVal)
    return 50
  }
  
  const yhsj = parseFloat(yhsjVal)
  const bz = parseFloat(bzVal)
  const bzc = bzcVal ? parseFloat(bzcVal) : 0
  
  if (isNaN(yhsj) || isNaN(bz)) {
    console.log('Chart DEBUG: yhsj isNaN=', isNaN(yhsj), 'bz isNaN=', isNaN(bz))
    return 50
  }
  
  // 如果没有标准差(bzc)，使用默认范围计算
  if (!bzc || bzc === 0 || isNaN(bzc)) {
    // 使用固定的百分比范围
    const percent = bz > 0 ? ((yhsj - bz) / bz) * 100 : 0
    // 将-50%到+50%的范围映射到5%到95%的位置
    const y = 50 - percent * 0.9
    return Math.max(5, Math.min(95, y))
  }
  
  // 使用SD计算：(实测值 - 靶值) / 标准差
  const sd = (yhsj - bz) / bzc
  
  // 调试打印
  console.log(`Chart: yhsj=${yhsj}(${typeof yhsj}), bz=${bz}(${typeof bz}), bzc=${bzc}(${typeof bzc}), sd=${sd.toFixed(2)}`)
  
  // 将SD值(-3到+3)映射到Y轴位置(5%到95%)
  // +3SD = 5%, -3SD = 95%, 0SD = 50%
  const y = 50 - sd * 15
  const result = Math.max(5, Math.min(95, y))
  console.log(`Chart: y=${result.toFixed(2)}%`)
  return result
}

// ECharts 图表实例
let qcChartInstance = null

// 渲染趋势图
const renderQcChart = () => {
  if (chartData.value.length === 0) return
  
  const data = chartData.value
  const firstItem = data[0]
  const bz = firstItem.target_bz ? parseFloat(firstItem.target_bz) : null
  const bzc = firstItem.target_bzc ? parseFloat(firstItem.target_bzc) : null
  
  // X轴日期数据
  const xData = data.map(item => {
    const d = new Date(item.syrq)
    return `${d.getMonth() + 1}/${d.getDate()}`
  })
  
  // Y轴质控结果数据
  const yData = data.map(item => item.yhsj ? parseFloat(item.yhsj) : null)
  
  // 系列数据
  const series = [
    {
      name: '质控结果',
      type: 'line',
      data: yData,
      symbol: 'circle',
      symbolSize: 8,
      itemStyle: {
        color: (params) => {
          return data[params.dataIndex].skbz ? '#f56c6c' : '#67c23a'
        }
      }
    }
  ]
  
  // 如果有靶值和标准差，添加控制线
  if (bz != null && bzc != null && !isNaN(bz) && !isNaN(bzc) && bzc > 0) {
    const lines = [
      { name: '靶值', offset: 0, color: '#000000', type: 'solid' },
      { name: '+1SD', offset: 1, color: '#999999', type: 'dashed' },
      { name: '-1SD', offset: -1, color: '#999999', type: 'dashed' },
      { name: '+2SD', offset: 2, color: '#e6a23c', type: 'solid' },
      { name: '-2SD', offset: -2, color: '#e6a23c', type: 'solid' },
      { name: '+3SD', offset: 3, color: '#f56c6c', type: 'dashed' },
      { name: '-3SD', offset: -3, color: '#f56c6c', type: 'dashed' }
    ]
    
    lines.forEach(l => {
      const yArr = xData.map(() => bz + l.offset * bzc)
      series.push({
        name: l.name,
        type: 'line',
        data: yArr,
        showSymbol: false,
        lineStyle: { type: l.type, color: l.color },
        label: { show: true, position: 'end' }
      })
    })
  }
  
  // 初始化或更新图表
  const chartDom = document.getElementById('qcChart')
  if (!chartDom) return
  
  if (!qcChartInstance) {
    qcChartInstance = echarts.init(chartDom)
  }
  
  qcChartInstance.setOption({
    title: { 
      text: currentAnalysisProject.value?.xmmc ? `${currentAnalysisProject.value.xmmc} - Levey-Jennings质控图` : '质控趋势图',
      left: 'center'
    },
    tooltip: { 
      trigger: 'axis',
      formatter: (params) => {
        const result = params[0]
        const dataItem = data[result.dataIndex]
        return `${result.name}<br/>
                结果: ${dataItem.yhsj}<br/>
                状态: ${dataItem.skbz ? '失控' : '在控'}`
      }
    },
    legend: { top: 30 },
    xAxis: { 
      type: 'category', 
      data: xData 
    },
    yAxis: { 
      type: 'value',
      name: firstItem.xmdw || ''
    },
    series: series
  })
}

// 监听趋势图数据变化
watch(chartData, (newData) => {
  if (newData.length > 0 && analysisSubTab.value === 'chart') {
    // 延迟等待DOM渲染
    setTimeout(() => {
      renderQcChart()
    }, 100)
  }
}, { deep: true })

// 监听tab切换
watch(analysisSubTab, (newTab) => {
  if (newTab === 'chart' && chartData.value.length > 0) {
    setTimeout(() => {
      renderQcChart()
    }, 100)
  }
})

const formatDate = (date) => {
  if (!date) return ''
  const d = new Date(date)
  return d.toLocaleDateString('zh-CN')
}

const formatDateShort = (date) => {
  if (!date) return ''
  const d = new Date(date)
  return `${d.getMonth() + 1}/${d.getDate()}`
}

// ============ 初始化 ============
onMounted(() => {
  loadProducts()
})
</script>

<style scoped>
.qc-management {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: #f5f7fa;
  min-height: 0;
  overflow: hidden;
  width: 100%;
  box-sizing: border-box;
}

.page-header {
  padding: 15px 20px;
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
  flex-shrink: 0;
}

.page-header h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.qc-tabs {
  display: flex;
  background: #fff;
  padding: 0 20px;
  border-bottom: 1px solid #e4e7ed;
  flex-shrink: 0;
}

.qc-tab {
  padding: 12px 20px;
  border: none;
  background: none;
  cursor: pointer;
  font-size: 14px;
  color: #606266;
  border-bottom: 2px solid transparent;
  transition: all 0.3s;
}

.qc-tab:hover {
  color: #409eff;
}

.qc-tab.active {
  color: #409eff;
  border-bottom-color: #409eff;
}

.qc-tab.disabled {
  color: #c0c4cc;
  cursor: not-allowed;
}

.tab-content {
  flex: 1;
  min-height: 0;
  overflow: auto;
  padding: 20px;
}

/* 三栏布局 - 自适应 */
.qc-setup-main {
  display: grid;
  grid-template-columns: 280px 1fr 260px;
  gap: 15px;
  height: calc(100vh - 180px);
  min-height: 0;
  width: 100%;
  box-sizing: border-box;
}

@media (max-width: 1200px) {
  .qc-setup-main {
    grid-template-columns: 220px 1fr 220px;
  }
}

@media (max-width: 900px) {
  .qc-setup-main {
    grid-template-columns: 1fr;
    grid-template-rows: auto 1fr auto;
  }
}

.qc-setup-main .sm-panel {
  min-width: 0;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.sm-panel {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

.sm-panel-header {
  padding: 12px 15px;
  border-bottom: 1px solid #f0f0f0;
  font-weight: 600;
  font-size: 14px;
  color: #303133;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.sm-panel-body {
  flex: 1;
  overflow: auto;
  padding: 15px;
}

/* 搜索栏 */
.search-bar {
  display: flex;
  gap: 8px;
  margin-bottom: 15px;
}

.search-bar input {
  flex: 1;
  padding: 8px 12px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  font-size: 13px;
}

.search-bar input:focus {
  border-color: #409eff;
  outline: none;
}

/* 列表项 */
.list-container {
  flex: 1;
  overflow: auto;
}

.list-item {
  padding: 12px;
  border: 1px solid #f0f0f0;
  border-radius: 6px;
  margin-bottom: 8px;
  cursor: pointer;
  transition: all 0.2s;
}

.list-item:hover {
  border-color: #409eff;
  background: #f5f7fa;
}

.list-item.selected {
  border-color: #409eff;
  background: #ecf5ff;
}

.item-main {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}

.item-title {
  font-weight: 500;
  color: #303133;
}

.item-sub {
  font-size: 12px;
  color: #909399;
}

.item-tags {
  display: flex;
  gap: 5px;
}

.tag-active {
  font-size: 11px;
  color: #67c23a;
  background: #f0f9eb;
  padding: 2px 8px;
  border-radius: 3px;
}

.tag-inactive {
  font-size: 11px;
  color: #909399;
  background: #f4f4f5;
  padding: 2px 8px;
  border-radius: 3px;
}

.empty-tip {
  text-align: center;
  color: #909399;
  padding: 40px 0;
  font-size: 14px;
}

/* 中间Tab */
.center-tabs {
  display: flex;
  border-bottom: 1px solid #f0f0f0;
}

.center-tab {
  flex: 1;
  padding: 10px;
  border: none;
  background: none;
  cursor: pointer;
  font-size: 13px;
  color: #606266;
  border-bottom: 2px solid transparent;
}

.center-tab:hover {
  color: #409eff;
}

.center-tab.active {
  color: #409eff;
  border-bottom-color: #409eff;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  font-size: 13px;
  color: #606266;
}

/* 数据表格 */
.data-table {
  width: 100%;
  min-width: 0;
  border-collapse: collapse;
  font-size: 13px;
}

.data-table th {
  background: #f5f7fa;
  padding: 10px 8px;
  text-align: left;
  font-weight: 500;
  color: #606266;
  border-bottom: 1px solid #ebeef5;
}

.data-table td {
  padding: 10px 8px;
  border-bottom: 1px solid #ebeef5;
  color: #303133;
}

.data-table tr:hover {
  background: #f5f7fa;
}

.data-table .row-danger {
  background: #fef0f0;
}

.inline-input {
  width: 70px;
  padding: 4px 8px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  font-size: 12px;
}

.result-cell {
  font-weight: 600;
  color: #409eff;
}

.empty-cell {
  text-align: center;
  color: #909399;
  padding: 30px 0;
}

/* 右侧操作 */
.action-buttons {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-bottom: 20px;
}

.action-buttons .el-button {
  width: 100%;
}

.product-info h4 {
  font-size: 13px;
  color: #303133;
  margin: 0 0 10px 0;
  padding-bottom: 10px;
  border-bottom: 1px solid #f0f0f0;
}

.info-row {
  display: flex;
  margin-bottom: 8px;
  font-size: 12px;
}

.info-row label {
  width: 50px;
  color: #909399;
}

.info-row span {
  flex: 1;
  color: #606266;
}

/* 规则设置 */
.rules-list {
  margin-top: 10px;
}

.rule-item {
  margin-bottom: 20px;
}

.rule-label {
  font-size: 13px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 10px;
  display: block;
}

.rule-checkboxes {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.rule-checkboxes label {
  font-size: 12px;
  color: #606266;
  cursor: pointer;
}

.rule-inputs {
  display: flex;
  gap: 15px;
}

.rule-inputs span {
  font-size: 12px;
  color: #606266;
}

.form-actions {
  margin-top: 20px;
  padding-top: 15px;
  border-top: 1px solid #f0f0f0;
}

/* 质控录入 */
.qc-entry-main, .qc-analysis-main {
  height: calc(100vh - 180px);
  display: flex;
  flex-direction: column;
  min-height: 0;
  width: 100%;
  box-sizing: border-box;
}

.filter-bar {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 15px;
  background: #fff;
  border-radius: 8px;
  margin-bottom: 15px;
  flex-wrap: wrap;
  flex-shrink: 0;
  width: 100%;
  box-sizing: border-box;
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 8px;
}

.filter-group label {
  font-size: 13px;
  color: #606266;
}

.filter-group select,
.filter-group input {
  padding: 6px 10px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  font-size: 13px;
}

.entry-content,
.analysis-content {
  flex: 1;
  min-height: 0;
  background: #fff;
  border-radius: 8px;
  overflow: auto;
  padding: 15px;
}

/* 质控分析 */
.stats-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 15px;
  margin-bottom: 15px;
  flex-shrink: 0;
}

@media (max-width: 768px) {
  .stats-cards {
    grid-template-columns: repeat(2, 1fr);
  }
}

.stat-card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  text-align: center;
}

.stat-card.success {
  background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
}

.stat-card.danger {
  background: linear-gradient(135deg, #f56c6c 0%, #f78989 100%);
}

.stat-label {
  font-size: 12px;
  color: #909399;
  margin-bottom: 8px;
}

.stat-card.success .stat-label,
.stat-card.danger .stat-label {
  color: rgba(255,255,255,0.8);
}

.stat-value {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
}

.stat-card.success .stat-value,
.stat-card.danger .stat-value {
  color: #fff;
}

.analysis-tabs {
  display: flex;
  border-bottom: 1px solid #e4e7ed;
  margin-bottom: 15px;
}

.analysis-tabs button {
  padding: 10px 20px;
  border: none;
  background: none;
  cursor: pointer;
  font-size: 14px;
  color: #606266;
  border-bottom: 2px solid transparent;
}

.analysis-tabs button:hover {
  color: #409eff;
}

.analysis-tabs button.active {
  color: #409eff;
  border-bottom-color: #409eff;
}

/* 趋势图 */
.chart-container {
  padding: 20px;
  min-height: 300px;
  overflow: auto;
}

.chart-header {
  text-align: center;
  margin-bottom: 20px;
  font-size: 14px;
  font-weight: 500;
  color: #303133;
}

.lj-chart {
  position: relative;
  height: 300px;
  min-width: 400px;
  border-left: 2px solid #303133;
  border-bottom: 2px solid #303133;
  margin: 20px 40px 40px 60px;
  overflow-x: auto;
}

.chart-y-axis {
  position: absolute;
  left: -60px;
  top: 0;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  font-size: 11px;
  color: #909399;
}

.control-lines {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
}

.line {
  position: absolute;
  width: 100%;
  font-size: 10px;
  color: #909399;
}

.line-3s { border-top: 1px dashed #f56c6c; }
.line-2s { border-top: 1px dashed #e6a23c; }
.line-1s { border-top: 1px dashed #409eff; }
.line-mean { border-top: 1px solid #67c23a; }
.line--1s { border-top: 1px dashed #409eff; }
.line--2s { border-top: 1px dashed #e6a23c; }
.line--3s { border-top: 1px dashed #f56c6c; }

.data-points {
  position: relative;
  width: 100%;
  height: 100%;
}

.data-point {
  position: absolute;
  width: 8px;
  height: 8px;
  background: #67c23a;
  border-radius: 50%;
  transform: translate(-50%, -50%);
}

.data-point.out-control {
  background: #f56c6c;
}

.chart-x-axis {
  display: flex;
  justify-content: space-between;
  margin-top: 10px;
  font-size: 10px;
  color: #909399;
}

/* 禁用提示 */
.disabled-content {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
}

.disabled-tip {
  text-align: center;
  color: #909399;
}

.tip-icon {
  font-size: 48px;
  margin-bottom: 15px;
}

.disabled-tip p {
  font-size: 16px;
}

/* 文本样式 */
.text-success { color: #67c23a; }
.text-danger { color: #f56c6c; }
.text-warning { color: #e6a23c; }
.text-info { color: #409eff; }
.bold { font-weight: 600; }
</style>
