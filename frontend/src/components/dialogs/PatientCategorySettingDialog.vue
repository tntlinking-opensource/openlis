<template>
  <FloatingPanel
    v-model="visible"
    title="病人类别设置"
    :width="1000"
    :height="600"
    class="patient-category-setting-panel"
  >
    <div class="dialog-body">
      <div class="left">
        <div class="grid-title">病人类别信息列表：</div>
        <el-table
          :data="list"
          height="300"
          border
          size="small"
          highlight-current-row
          @current-change="onRowSelect"
        >
          <el-table-column prop="bm" label="类别代码" width="70" />
          <el-table-column prop="bmsm" label="类别名称" width="90" />
          <el-table-column prop="pym" label="拼音码" width="70" />
          <el-table-column prop="qtdm" label="其他代码" width="70" />
          <el-table-column prop="mrksbz" label="默认科室标志" width="90">
            <template #default="{ row }">
              <el-checkbox v-model="row.mrksbz" disabled />
            </template>
          </el-table-column>
          <el-table-column prop="mrksmc" label="科室名称" width="90" />
          <el-table-column prop="mrysbz" label="默认医生标志" width="90">
            <template #default="{ row }">
              <el-checkbox v-model="row.mrysbz" disabled />
            </template>
          </el-table-column>
          <el-table-column prop="mrysmc" label="医生名称" width="80" />
          <el-table-column prop="sjlyfsms" label="来源方式" width="110" />
          <el-table-column prop="xh" label="序号" width="50" />
          <el-table-column prop="tybz" label="停用标志" width="70">
            <template #default="{ row }">
              <el-checkbox v-model="row.tybz" disabled />
            </template>
          </el-table-column>
        </el-table>
      </div>

      <div class="right">
        <div class="form">
          <div class="row">
            <label>类别代码</label>
            <el-input v-model="form.bm" size="small" class="inp" :disabled="editingMode==='edit'" />
          </div>
          <div class="row">
            <label>类别名称</label>
            <el-input v-model="form.bmsm" size="small" class="inp" @blur="onBmsmBlur" />
          </div>
          <div class="row">
            <label>拼 音 码</label>
            <el-input v-model="form.pym" size="small" class="inp" />
          </div>
          <div class="row">
            <label>其 他 码</label>
            <el-input v-model="form.qtdm" size="small" class="inp" />
          </div>
          <div class="row">
            <label>来源方式</label>
            <el-select v-model="form.sjlyfs" size="small" class="inp">
              <el-option label="手工录入" :value="0" />
              <el-option label="接口导入" :value="1" />
            </el-select>
          </div>
          <div class="row">
            <label>序 号</label>
            <el-input-number v-model="form.xh" size="small" class="inp" :min="1" />
          </div>
          <div class="row check-row">
            <el-checkbox v-model="form.mrksbz" @change="onMrksbzChange">默认科室标志</el-checkbox>
            <el-input v-model="form.mrksmc" size="small" class="inp-small" :disabled="!form.mrksbz" placeholder="科室名称" />
          </div>
          <div class="row check-row">
            <el-checkbox v-model="form.mrysbz" @change="onMrysbzChange">默认医生标志</el-checkbox>
            <el-input v-model="form.mrysmc" size="small" class="inp-small" :disabled="!form.mrysbz" placeholder="医生名称" />
          </div>
          <div class="row check-row">
            <el-checkbox v-model="form.jkbz">接口标志</el-checkbox>
            <el-checkbox v-model="form.jgxxBz" @change="onJgxxBzChange">机构信息标志</el-checkbox>
          </div>
          <div class="row" v-if="form.jgxxBz">
            <label>机构信息</label>
            <el-input v-model="form.jgxx" size="small" class="inp" />
          </div>
          <div class="row check-row">
            <el-checkbox v-model="form.qxkz">权限控制</el-checkbox>
            <el-checkbox v-model="form.tybz">停用标志</el-checkbox>
          </div>
          <div class="row">
            <label>类别颜色</label>
            <div class="color-picker-wrapper">
              <div class="color-display" :style="{ backgroundColor: colorHex }" @click="showColorPicker = true"></div>
              <el-input v-model="form.brlbys" size="small" class="inp-small" type="number" />
            </div>
          </div>
        </div>

        <div class="btns">
          <el-button size="small" @click="onAdd" :disabled="editingMode==='add'">增加(A)</el-button>
          <el-button size="small" @click="onEdit" :disabled="!current || editingMode==='edit'">修改(E)</el-button>
          <el-button size="small" @click="onCancel" :disabled="!editingMode">放弃(C)</el-button>
          <el-button size="small" type="primary" @click="onSave" :disabled="!editingMode">保存(S)</el-button>
          <el-button size="small" @click="handleClose">关闭(X)</el-button>
        </div>
      </div>
    </div>

    <!-- 颜色选择器（简化版，使用原生input） -->
    <input
      v-if="showColorPicker"
      ref="colorInputRef"
      type="color"
      :value="colorHex"
      @change="onColorChange"
      style="position: absolute; opacity: 0; pointer-events: none;"
    />
  </FloatingPanel>
</template>

<script setup>
import { computed, reactive, ref, watch, nextTick } from 'vue'
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

const list = ref([])
const current = ref(null)
const editingMode = ref('')
const showColorPicker = ref(false)
const colorInputRef = ref(null)

const form = reactive({
  bm: null,
  bmsm: '',
  pym: '',
  qtdm: '',
  sjlyfs: 0,
  sjlyfsms: '',
  mrksbz: false,
  mrksdm: '',
  mrksmc: '',
  mrysbz: false,
  mrysdm: '',
  mrysmc: '',
  xh: 1,
  tybz: false,
  jkbz: false,
  jgxxBz: false,
  jgxx: '',
  qxkz: false,
  brlbys: 16777201
})

// 颜色值转换为十六进制（简化处理）
const colorHex = computed(() => {
  const val = form.brlbys || 16777201
  return '#' + (val & 0xFFFFFF).toString(16).padStart(6, '0')
})

const onColorChange = (e) => {
  const hex = e.target.value
  form.brlbys = parseInt(hex.substring(1), 16)
  showColorPicker.value = false
}

// 自动生成拼音码
const onBmsmBlur = () => {
  if (form.bmsm && !form.pym) {
    form.pym = form.bmsm.toUpperCase().substring(0, 1)
  }
  if (form.sjlyfs === 0) {
    form.sjlyfsms = '手工录入'
  } else if (form.sjlyfs === 1) {
    form.sjlyfsms = '接口导入'
  }
}

const onMrksbzChange = () => {
  if (!form.mrksbz) {
    form.mrksdm = ''
    form.mrksmc = ''
  }
}

const onMrysbzChange = () => {
  if (!form.mrysbz) {
    form.mrysdm = ''
    form.mrysmc = ''
  }
}

const onJgxxBzChange = () => {
  if (!form.jgxxBz) {
    form.jgxx = ''
  }
}

const loadList = async () => {
  try {
    const res = await axios.get('/api/basic/patient-category/list')
    list.value = res.data || []
  } catch (e) {
    ElMessage.error('读取失败：' + (e.response?.data?.message || e.message))
  }
}

const fillFormFromRow = (row) => {
  if (!row) return
  form.bm = row.bm || null
  form.bmsm = row.bmsm || ''
  form.pym = row.pym || ''
  form.qtdm = row.qtdm || ''
  form.sjlyfs = row.sjlyfs ?? 0
  form.sjlyfsms = row.sjlyfsms || ''
  form.mrksbz = row.mrksbz ?? false
  form.mrksdm = row.mrksdm || ''
  form.mrksmc = row.mrksmc || ''
  form.mrysbz = row.mrysbz ?? false
  form.mrysdm = row.mrysdm || ''
  form.mrysmc = row.mrysmc || ''
  form.xh = row.xh ?? 1
  form.tybz = row.tybz ?? false
  form.jkbz = row.jkbz ?? false
  form.jgxxBz = row.jgxx_bz ?? false
  form.jgxx = row.jgxx || ''
  form.qxkz = row.qxkz ?? false
  form.brlbys = row.brlbys ?? 16777201
}

const resetForm = () => {
  fillFormFromRow({
    bm: null,
    bmsm: '',
    pym: '',
    qtdm: '',
    sjlyfs: 0,
    sjlyfsms: '',
    mrksbz: false,
    mrksdm: '',
    mrksmc: '',
    mrysbz: false,
    mrysdm: '',
    mrysmc: '',
    xh: 1,
    tybz: false,
    jkbz: false,
    jgxxBz: false,
    jgxx: '',
    qxkz: false,
    brlbys: 16777201
  })
}

const onRowSelect = (row) => {
  if (editingMode.value) return
  current.value = row
  if (row) fillFormFromRow(row)
}

const onAdd = async () => {
  try {
    const res = await axios.get('/api/basic/patient-category/next-code')
    form.bm = res.data?.bm || 1
    editingMode.value = 'add'
    current.value = null
    resetForm()
    form.bm = res.data?.bm || 1
  } catch (e) {
    ElMessage.error('获取类别代码失败：' + (e.response?.data?.message || e.message))
  }
}

const onEdit = () => {
  if (!current.value) return
  editingMode.value = 'edit'
}

const onCancel = () => {
  editingMode.value = ''
  if (current.value) fillFormFromRow(current.value)
  else resetForm()
}

const onSave = async () => {
  try {
    if (!form.bmsm.trim()) {
      ElMessage.warning('类别名称不能为空')
      return
    }
    if (form.mrksbz && !form.mrksmc.trim()) {
      ElMessage.warning('默认科室标志选中时，科室名称不能为空')
      return
    }
    if (form.mrysbz && !form.mrysmc.trim()) {
      ElMessage.warning('默认医生标志选中时，医生名称不能为空')
      return
    }
    const payload = { ...form }
    const res = await axios.post('/api/basic/patient-category/save', payload)
    if (res.data?.success) {
      ElMessage.success(res.data.message || '保存成功')
      editingMode.value = ''
      await loadList()
    } else {
      ElMessage.warning(res.data?.message || '保存失败')
    }
  } catch (e) {
    ElMessage.error('保存失败：' + (e.response?.data?.message || e.message))
  }
}

watch(visible, async (v) => {
  if (v) {
    editingMode.value = ''
    current.value = null
    resetForm()
    await loadList()
  }
})

const handleClose = () => {
  visible.value = false
}
</script>

<style scoped>
.patient-category-setting-panel :deep(.panel-content) {
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

.grid-title {
  font-size: 12px;
  margin-bottom: 4px;
}

.right {
  flex: 2;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.form {
  background: #fff;
  padding: 8px;
  border: 1px solid #ccc;
  max-height: 500px;
  overflow-y: auto;
}

.row {
  display: flex;
  align-items: center;
  margin-bottom: 6px;
}

.row label {
  width: 90px;
  font-size: 12px;
  text-align: right;
  margin-right: 6px;
}

.row .inp {
  flex: 1;
}

.inp-small {
  width: 120px;
  margin-left: 8px;
}

.check-row {
  margin-top: 8px;
  flex-wrap: wrap;
}

.color-picker-wrapper {
  display: flex;
  align-items: center;
  gap: 6px;
  flex: 1;
}

.color-display {
  width: 40px;
  height: 24px;
  border: 1px solid #ccc;
  cursor: pointer;
}

.btns {
  display: flex;
  gap: 6px;
  justify-content: center;
  padding-top: 8px;
}
</style>

