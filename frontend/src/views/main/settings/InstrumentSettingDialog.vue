<template>
  <FloatingPanel
    v-model="visible"
    title="仪器设备"
    :width="1200"
    :height="700"
    class="instrument-setting-panel"
  >
    <el-tabs v-model="activeTab" class="tab-wrapper">
      <!-- Tab1：仪器管理 -->
      <el-tab-pane label="仪器管理" name="basic">
        <div class="tab1-layout">
          <div class="tab1-left">
            <el-table :data="list" height="100%" border size="small" highlight-current-row @current-change="handleRowChange">
              <el-table-column prop="sb_djid" label="仪器ID" width="70" />
              <el-table-column prop="sbmc" label="仪器名称" width="160" />
              <el-table-column prop="sbbm" label="简称" width="70" />
              <el-table-column prop="comsm" label="通讯端口" width="70" />
              <el-table-column prop="btl" label="波特率" width="60" />
              <el-table-column prop="jyw" label="校验位" width="55" />
              <el-table-column prop="sjw" label="数据位" width="55" />
              <el-table-column prop="tzw" label="停止位" width="55" />
              <el-table-column prop="xmxsfs" label="项目显示方式" width="100" />
              <el-table-column prop="bgbt" label="报告标题" width="120" />
              <el-table-column prop="tx" label="图形" width="50" />
              <el-table-column prop="dyfs" label="打印者方式" width="80" />
              <el-table-column prop="shzfs" label="审核者方式" width="80" />
              <el-table-column prop="sxpl" label="刷新频率" width="70" />
              <el-table-column prop="tybz" label="停用标志" width="70">
                <template #default="{ row }"><el-checkbox v-model="row.tybz" disabled /></template>
              </el-table-column>
            </el-table>
          </div>
          <div class="tab1-right">
            <div class="tab1-right-scroll">
              <el-form :model="form" label-width="90px" size="small" :disabled="!editing">
                <el-row :gutter="8">
                  <el-col :span="12"><el-form-item label="分类"><el-input v-model="form.szdm" /></el-form-item></el-col>
                  <el-col :span="12"><el-form-item label="简称"><el-input v-model="form.sbbm" /></el-form-item></el-col>
                </el-row>
                <el-row :gutter="8">
                  <el-col :span="12"><el-form-item label="仪器名称"><el-input v-model="form.sbmc" /></el-form-item></el-col>
                  <el-col :span="12"><el-form-item label="显示方式">
                    <el-select v-model="form.xsfs"><el-option v-for="o in displayModes" :key="o" :label="o" :value="o" /></el-select>
                  </el-form-item></el-col>
                </el-row>
                <el-row :gutter="8">
                  <el-col :span="12"><el-form-item label="报告标题"><el-input v-model="form.bgbt" /></el-form-item></el-col>
                  <el-col :span="12"><el-form-item label="图形">
                    <el-select v-model="form.tx"><el-option v-for="o in graphOptions" :key="o" :label="o" :value="o" /></el-select>
                  </el-form-item></el-col>
                </el-row>
                <el-row :gutter="8">
                  <el-col :span="12"><el-form-item label="打印者">
                    <el-select v-model="form.dyfs"><el-option v-for="o in printModes" :key="o" :label="o" :value="o" /></el-select>
                  </el-form-item></el-col>
                  <el-col :span="12"><el-form-item label="审核者">
                    <el-select v-model="form.shzfs"><el-option v-for="o in reviewModes" :key="o" :label="o" :value="o" /></el-select>
                  </el-form-item></el-col>
                </el-row>
                <el-row :gutter="8">
                  <el-col :span="12"><el-form-item label="最近结果"><el-input-number v-model="form.zjjgts" :min="0" /></el-form-item></el-col>
                  <el-col :span="12"><el-form-item label="拼音码"><el-input v-model="form.pym" /></el-form-item></el-col>
                </el-row>
                <el-row :gutter="8">
                  <el-col :span="12"><el-form-item label="报告页脚"><el-input v-model="form.bgyj" /></el-form-item></el-col>
                  <el-col :span="12"><el-form-item label="报告格式"><el-input v-model="form.bblb" /></el-form-item></el-col>
                </el-row>
                <el-row :gutter="8">
                  <el-col :span="12"><el-form-item label="绑定报告模板">
                    <el-select v-model="selectedTemplateId" placeholder="选择模板（自动填充编号/名称）" clearable style="width:100%;">
                      <el-option v-for="t in templateOptions" :key="t.template_id" :label="t.template_name" :value="t.template_id">
                        <span>{{ t.template_name }}</span>
                        <span v-if="t.bgbh || t.bgmc" style="color:#999;font-size:11px;margin-left:8px;">{{ t.bgbh }}/{{ t.bgmc }}</span>
                      </el-option>
                    </el-select>
                  </el-form-item></el-col>
                  <el-col :span="6"><el-form-item label="报告编号"><el-input v-model="form.bgbh" placeholder="自动" disabled /></el-form-item></el-col>
                  <el-col :span="6"><el-form-item label="报告名称"><el-input v-model="form.bgmc" placeholder="自动" disabled /></el-form-item></el-col>
                </el-row>
                <el-row :gutter="8">
                  <el-col :span="12"><el-form-item label="工作组"><el-input v-model="form.gzzdm" /></el-form-item></el-col>
                  <el-col :span="12"><el-form-item label="显示类别">
                    <el-select v-model="form.xslb"><el-option v-for="o in displayCategories" :key="o" :label="o" :value="o" /></el-select>
                  </el-form-item></el-col>
                </el-row>
                <el-row :gutter="8">
                  <el-col :span="12"><el-form-item label="质控类别">
                    <el-select v-model="form.zklb"><el-option v-for="o in qcCategories" :key="o" :label="o" :value="o" /></el-select>
                  </el-form-item></el-col>
                  <el-col :span="12"><el-form-item label="仪器站点"><el-input v-model="form.yqzd" /></el-form-item></el-col>
                </el-row>
                <el-row :gutter="8">
                  <el-col :span="12"><el-form-item label="采集程序"><el-input v-model="form.cjcx" /></el-form-item></el-col>
                  <el-col :span="12"><el-form-item label="质控基号"><el-input v-model="form.zkjh" /></el-form-item></el-col>
                </el-row>
                <el-row :gutter="8">
                  <el-col :span="12"><el-form-item label="急诊基号"><el-input v-model="form.jzjh" /></el-form-item></el-col>
                  <el-col :span="12"><el-form-item label="刷新频率"><el-input-number v-model="form.sxpl" :min="0" /></el-form-item></el-col>
                </el-row>
                <el-row :gutter="8">
                  <el-col :span="24">
                    <el-checkbox v-model="form.tybz">停用标志</el-checkbox>
                    <el-checkbox v-model="form.kztsbz">空值提示</el-checkbox>
                    <el-checkbox v-model="form.jkxmxz">项目选择</el-checkbox>
                    <el-checkbox v-model="form.fsztsbz">负值提示</el-checkbox>
                    <el-checkbox v-model="form.zerotsbz">0结果提示</el-checkbox>
                    <el-checkbox v-model="form.ycxwc">数据发送标志</el-checkbox>
                  </el-col>
                </el-row>
              </el-form>

              <fieldset class="color-section" :disabled="!editing">
                <legend>颜色设置</legend>
                <div class="color-grid">
                  <div v-for="c in colorFields" :key="c.key" class="color-row">
                    <label>{{ c.label }}</label>
                    <el-color-picker v-model="form[c.key]" size="small" />
                  </div>
                </div>
                <el-button size="small" @click="resetColors" style="margin-top:4px">恢复默认值</el-button>
              </fieldset>

              <fieldset class="comm-section" :disabled="!editing">
                <legend>通讯参数设置</legend>
                <el-tabs v-model="commTab" size="small">
                  <el-tab-pane label="串口" name="serial">
                    <el-row :gutter="8">
                      <el-col :span="8"><el-form-item label="通讯端口" label-width="70px"><el-select v-model="form.comsm"><el-option v-for="o in commPorts" :key="o" :label="o" :value="o" /></el-select></el-form-item></el-col>
                      <el-col :span="8"><el-form-item label="波特率" label-width="60px"><el-select v-model="form.btl"><el-option v-for="o in baudRates" :key="o" :label="o" :value="o" /></el-select></el-form-item></el-col>
                      <el-col :span="8"><el-form-item label="校验位" label-width="60px"><el-select v-model="form.jyw"><el-option v-for="o in parities" :key="o" :label="o" :value="o" /></el-select></el-form-item></el-col>
                    </el-row>
                    <el-row :gutter="8">
                      <el-col :span="8"><el-form-item label="数据位" label-width="70px"><el-select v-model="form.sjw"><el-option v-for="o in dataBits" :key="o" :label="o" :value="o" /></el-select></el-form-item></el-col>
                      <el-col :span="8"><el-form-item label="停止位" label-width="60px"><el-select v-model="form.tzw"><el-option v-for="o in stopBits" :key="o" :label="o" :value="o" /></el-select></el-form-item></el-col>
                    </el-row>
                  </el-tab-pane>
                  <el-tab-pane label="网口" name="network">
                    <el-row :gutter="8">
                      <el-col :span="12"><el-form-item label="IP地址" label-width="60px"><el-input v-model="form.ip" /></el-form-item></el-col>
                      <el-col :span="12"><el-form-item label="端口号" label-width="60px"><el-input v-model="form.dk" /></el-form-item></el-col>
                    </el-row>
                  </el-tab-pane>
                  <el-tab-pane label="数据库" name="database">
                    <el-form-item label="数据库连接" label-width="80px"><el-input v-model="form.sjklj" /></el-form-item>
                  </el-tab-pane>
                  <el-tab-pane label="读取文件" name="file">
                    <el-row :gutter="8">
                      <el-col :span="12"><el-form-item label="文件位置" label-width="70px"><el-input v-model="form.wjdz" /></el-form-item></el-col>
                      <el-col :span="12"><el-form-item label="备份位置" label-width="70px"><el-input v-model="form.bfdz" /></el-form-item></el-col>
                    </el-row>
                    <el-row :gutter="8">
                      <el-col :span="12"><el-form-item label="用户名" label-width="70px"><el-input v-model="form.wjyhm" /></el-form-item></el-col>
                      <el-col :span="12"><el-form-item label="密码" label-width="70px"><el-input v-model="form.wjmm" type="password" show-password /></el-form-item></el-col>
                    </el-row>
                  </el-tab-pane>
                </el-tabs>
              </fieldset>
            </div>

            <div class="btns">
              <el-button size="small" @click="onAdd">新增(A)</el-button>
              <el-button size="small" @click="onEdit" :disabled="!current">修改(E)</el-button>
              <el-button size="small" @click="onCancel" :disabled="!editing">取消(C)</el-button>
              <el-button size="small" type="primary" @click="onSave" :disabled="!editing">保存(S)</el-button>
              <el-button size="small" @click="handleClose">关闭(Q)</el-button>
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
import { listTemplates } from '../../../api/report'

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  defaultTab: { type: String, default: 'basic' }
})
const emit = defineEmits(['update:modelValue'])

const visible = computed({
  get: () => props.modelValue,
  set: (v) => emit('update:modelValue', v)
})

const activeTab = ref('basic')
const commTab = ref('serial')
const keyword = ref('')
const list = ref([])
const current = ref(null)
const editing = ref(false)
const templateOptions = ref([])
const selectedTemplateId = ref(null)

const loadTemplateOptions = async () => {
  try {
    const { data } = await listTemplates({})
    templateOptions.value = Array.isArray(data) ? data : []
    if (current.value) {
      const row = current.value
      const matched = templateOptions.value.find(t => (t.bgbh === row.bgbh && t.bgmc === row.bgmc) || t.sb_djid === row.sb_djid)
      if (matched && !selectedTemplateId.value) {
        selectedTemplateId.value = matched.template_id
      }
    }
  } catch (e) {
    templateOptions.value = []
  }
}

watch(visible, (val) => {
  if (val) {
    loadTemplateOptions()
    selectedTemplateId.value = null
  }
})

watch(selectedTemplateId, (tid) => {
  if (tid) {
    const tpl = templateOptions.value.find(t => t.template_id === tid)
    if (tpl) {
      form.bgbh = tpl.bgbh || ''
      form.bgmc = tpl.bgmc || ''
    }
  } else {
    form.bgbh = ''
    form.bgmc = ''
  }
})

watch(templateOptions, (opts) => {
  if (opts.length > 0 && current.value && !selectedTemplateId.value) {
    const row = current.value
    const matched = opts.find(t => (t.bgbh === row.bgbh && t.bgmc === row.bgmc) || t.sb_djid === row.sb_djid)
    if (matched) {
      selectedTemplateId.value = matched.template_id
    }
  }
}, { immediate: false })

const displayModes = ['按仪器', '按组合', '按项目']
const graphOptions = ['无', '折线图', '柱状图']
const printModes = ['不打印', '打印', '询问']
const reviewModes = ['不审核', '审核', '询问']
const displayCategories = ['通用', '细菌', '酶标', '骨髓', '涂片', 'PCR']
const qcCategories = ['结果', '比值']
const commPorts = ['COM1', 'COM2', 'COM3', 'COM4', 'COM5', 'COM6', 'COM7', 'COM8']
const baudRates = [1200, 2400, 4800, 9600, 19200, 38400, 57600, 115200]
const parities = ['无', '奇校验', '偶校验']
const dataBits = [7, 8]
const stopBits = [1, 2]

const colorFields = [
  { key: 'yszcze', label: '正常值：' },
  { key: 'yspgz', label: '偏高值：' },
  { key: 'yspdz', label: '偏低值：' },
  { key: 'ysbjgz', label: '危急高：' },
  { key: 'ysbjdz', label: '危急低：' },
  { key: 'yswsh', label: '未审核：' },
  { key: 'ysysh', label: '已审核：' },
  { key: 'ysydy', label: '已打印：' },
  { key: 'ysyjy', label: '已检验：' },
  { key: 'ysycz', label: '异常值：' },
  { key: 'yswjz', label: '危急值：' },
  { key: 'ysycy', label: '已查阅：' },
  { key: 'ysjgwc', label: '已完成：' },
]

const defaultColors = {
  yszcze: '#000000', yspgz: '#FF0000', yspdz: '#0000FF',
  ysbjgz: '#FF00FF', ysbjdz: '#008000',
  yswsh: '#000000', ysysh: '#000000', ysydy: '#000000',
  ysyjy: '#000000', ysycz: '#FF0000', yswjz: '#FF0000',
  ysycy: '#000000', ysjgwc: '#000000',
}

const resetColors = () => {
  Object.assign(form, defaultColors)
}

const form = reactive({
  sbDjid: null, sbdm: '', sbmc: '', sbbm: '', ksdm: '', gzzdm: '', pym: '',
  zxbz: true, tybz: false,
  comsm: '', btl: 9600, jyw: '无', sjw: 8, tzw: 1,
  xmxsfs: '', bgbt: '', bgyj: '', mrzhid: null, tx: '无',
  dyfs: '不打印', shzfs: '不审核', sxpl: 0, ycxwc: false,
  xsfs: '按仪器', bblb: '', bgbh: '', bgmc: '', xslb: '通用', zklb: '结果',
  yqzd: '', zjjgts: 7, zkjh: '', jzjh: '', cjcx: '', szdm: '',
  kztsbz: false, jkxmxz: false, fsztsbz: false, zerotsbz: false,
  ip: '', dk: '', sjklj: '', wjdz: '', bfdz: '', wjyhm: '', wjmm: '',
  ...defaultColors,
})

const fillForm = (row) => {
  if (!row) {
    Object.assign(form, {
      sbDjid: null, sbdm: '', sbmc: '', sbbm: '', ksdm: '', gzzdm: '', pym: '',
      zxbz: true, tybz: false,
      comsm: '', btl: 9600, jyw: '无', sjw: 8, tzw: 1,
      xmxsfs: '', bgbt: '', bgyj: '', mrzhid: null, tx: '无',
      dyfs: '不打印', shzfs: '不审核', sxpl: 0, ycxwc: false,
      xsfs: '按仪器', bblb: '', bgbh: '', bgmc: '', xslb: '通用', zklb: '结果',
      yqzd: '', zjjgts: 7, zkjh: '', jzjh: '', cjcx: '', szdm: '',
      kztsbz: false, jkxmxz: false, fsztsbz: false, zerotsbz: false,
      ip: '', dk: '', sjklj: '', wjdz: '', bfdz: '', wjyhm: '', wjmm: '',
      ...defaultColors,
    })
    selectedTemplateId.value = null
    return
  }
  form.sbDjid = row.sb_djid ?? null
  form.sbdm = row.sbdm ?? ''
  form.sbmc = row.sbmc ?? ''
  form.sbbm = row.sbbm ?? ''
  form.ksdm = row.ksdm ?? ''
  form.gzzdm = row.gzzdm ?? ''
  form.pym = row.pym ?? ''
  form.zxbz = row.zxbz ?? true
  form.tybz = row.tybz ?? false
  form.comsm = row.comsm ?? ''
  form.btl = row.btl ?? 9600
  form.jyw = row.jyw ?? '无'
  form.sjw = row.sjw ?? 8
  form.tzw = row.tzw ?? 1
  form.xmxsfs = row.xmxsfs ?? ''
  form.bgbt = row.bgbt ?? ''
  form.bgyj = row.bgyj ?? ''
  form.mrzhid = row.mrzhid ?? null
  form.tx = row.tx ?? '无'
  form.dyfs = row.dyfs ?? '不打印'
  form.shzfs = row.shzfs ?? '不审核'
  form.sxpl = row.sxpl ?? 0
  form.ycxwc = row.ycxwc ?? false
  form.xsfs = row.xsfs ?? '按仪器'
  form.bblb = row.bblb ?? ''
  form.bgbh = row.bgbh ?? ''
  form.bgmc = row.bgmc ?? ''
  const matched = templateOptions.value.find(t => (t.bgbh === row.bgbh && t.bgmc === row.bgmc) || t.sb_djid === row.sb_djid)
  selectedTemplateId.value = matched ? matched.template_id : null
  form.xslb = row.xslb ?? '通用'
  form.zklb = row.zklb ?? '结果'
  form.yqzd = row.yqzd ?? ''
  form.zjjgts = row.zjjgts ?? 7
  form.zkjh = row.zkjh ?? ''
  form.jzjh = row.jzjh ?? ''
  form.cjcx = row.cjcx ?? ''
  form.szdm = row.szdm ?? ''
  form.kztsbz = row.kztsbz ?? false
  form.jkxmxz = row.jkxmxz ?? false
  form.fsztsbz = row.fsztsbz ?? false
  form.zerotsbz = row.zerotsbz ?? false
  form.ip = row.ip ?? ''
  form.dk = row.dk ?? ''
  form.sjklj = row.sjklj ?? ''
  form.wjdz = row.wjdz ?? ''
  form.bfdz = row.bfdz ?? ''
  form.wjyhm = row.wjyhm ?? ''
  form.wjmm = row.wjmm ?? ''
  form.yszcz = row.yszcz ?? '#000000'
  form.yspgz = row.yspgz ?? '#FF0000'
  form.yspdz = row.yspdz ?? '#0000FF'
  form.ysbjgz = row.ysbjgz ?? '#FF00FF'
  form.ysbjdz = row.ysbjdz ?? '#008000'
  form.yswsh = row.yswsh ?? '#000000'
  form.ysysh = row.ysysh ?? '#000000'
  form.ysycy = row.ysycy ?? '#000000'
  form.ysydy = row.ysydy ?? '#000000'
  form.ysyjy = row.ysyjy ?? '#000000'
  form.ysycz = row.ysycz ?? '#FF0000'
  form.yswjz = row.yswjz ?? '#FF0000'
  form.ysjgwc = row.ysjgwc ?? '#000000'
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

const handleRowChange = async (row) => {
  current.value = row
  if (!editing.value && row) {
    try {
      const res = await axios.get(`/api/basic/instrument/${row.sb_djid}`)
      fillForm(res.data)
    } catch (e) {
      fillForm(row)
    }
  }
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
      activeTab.value = props.defaultTab
      editing.value = false
      current.value = null
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

.tab-wrapper :deep(.el-tabs) {
  flex: 1;
  display: flex;
  flex-direction: column;
  height: 100%;
}

.tab-wrapper :deep(.el-tabs__content) {
  flex: 1;
  overflow: hidden;
  padding: 4px;
}

.tab-wrapper :deep(.el-tab-pane) {
  height: 100%;
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

.grid-title {
  font-weight: bold;
  margin-bottom: 8px;
  padding: 4px 0;
}

.checkbox-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 10px;
  height: 320px;
  overflow-y: auto;
  border: 1px solid #ddd;
  background: #fafafa;
}

.add-panel {
  padding: 10px;
  border: 1px solid #ddd;
  background: #f5f5f5;
}

.placeholder-panel {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 100%;
  color: #999;
  font-size: 14px;
  gap: 10px;
}

.dialog-body {
  display: flex;
  gap: 10px;
  padding: 10px;
  min-height: 400px;
}

.dialog-body .left {
  flex: 0 0 35%;
}

.dialog-body .right {
  flex: 1;
}

.tab1-layout {
  display: flex;
  gap: 8px;
  height: 100%;
  background: #9cc6ea;
}

.tab1-left {
  width: 380px;
  flex-shrink: 0;
  overflow: auto;
}

.tab1-right {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
  overflow: hidden;
  min-width: 0;
}

.tab1-right-scroll {
  flex: 1;
  overflow-y: auto;
  background: #fff;
  border: 1px solid #ccc;
  padding: 8px;
}

.tab1-right .el-form-item {
  margin-bottom: 4px;
}

.tab1-right .el-form-item__label {
  font-size: 12px;
  padding: 0 4px;
}

.color-section {
  border: 1px solid #ccc;
  padding: 8px;
  margin-top: 8px;
  font-size: 12px;
}

.color-section legend {
  font-size: 12px;
  font-weight: bold;
  padding: 0 4px;
}

.color-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(130px, 1fr));
  gap: 4px;
}

.color-row {
  display: flex;
  align-items: center;
  gap: 4px;
}

.color-row label {
  font-size: 12px;
  white-space: nowrap;
}

.comm-section {
  border: 1px solid #ccc;
  padding: 8px;
  margin-top: 8px;
  font-size: 12px;
}

.comm-section legend {
  font-size: 12px;
  font-weight: bold;
  padding: 0 4px;
}
</style>


