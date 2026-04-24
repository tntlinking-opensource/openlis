<template>
  <FloatingPanel
    v-model="visible"
    title="通用编码设置"
    :width="740"
    :height="600"
    class="common-code-panel"
  >
    <div class="dialog-content cc">
      <div class="cc-left">
        <div class="tree-box">
          <el-tree
            :data="treeData"
            node-key="key"
            :default-expand-all="false"
            :expand-on-click-node="true"
            highlight-current
            @node-click="onNodeClick"
          />
        </div>
      </div>

      <div class="cc-right">
        <!-- 主表面板（level=0） -->
        <div class="panel" v-show="selected?.level === 0">
          <div class="form-row"><label>编 码 代 码</label><el-input v-model="formMain.bmdm" size="small" class="inp" :disabled="!editing" /></div>
          <div class="form-row"><label>编 码 名 称</label><el-input v-model="formMain.bmmc" size="small" class="inp" :disabled="!editing" /></div>
          <div class="form-row"><label>编 码 编 号</label><el-input v-model="formMain.bmbh" size="small" class="inp" :disabled="!editing" /></div>
          <div class="form-row"><label>使用层次编码</label><el-input v-model="formMain.syccbm" size="small" class="inp" :disabled="!editing" /></div>
          <div class="form-row"><label>使用层次名称</label><el-input v-model="formMain.syccmc" size="small" class="inp" :disabled="!editing" /></div>
          <div class="form-row">
            <el-checkbox v-model="formMain.tybz" :disabled="!editing">停用标志</el-checkbox>
          </div>
        </div>

        <!-- 明细面板（level=1） -->
        <div class="panel" v-show="selected?.level === 1">
          <div class="form-row"><label>编 码</label><el-input v-model="formDetail.bm" size="small" class="inp" :disabled="!editing" /></div>
          <div class="form-row"><label>编 码 说 明发</label><el-input v-model="formDetail.bmsm" size="small" class="inp" :disabled="!editing" /></div>
          <div class="form-row"><label>拼 音 码</label><el-input v-model="formDetail.pym" size="small" class="inp" :disabled="!editing" /></div>
          <div class="form-row"><label>数 字 代 码</label><el-input v-model="formDetail.szdm" size="small" class="inp" :disabled="!editing" /></div>
          <div class="form-row">
            <el-checkbox v-model="formDetail.mrzbz" :disabled="!editing">默认值</el-checkbox>
            <el-checkbox v-model="formDetail.tybz" :disabled="!editing">停用标志</el-checkbox>
          </div>
          <div class="form-row"><label>备 注</label><el-input v-model="formDetail.bz" size="small" class="inp" :disabled="!editing" /></div>
        </div>

        <div class="btns">
          <el-button size="small" @click="addSibling" :disabled="editing">增加同级(A)</el-button>
          <el-button size="small" @click="cancelEdit" :disabled="!editing">放弃(C)</el-button>
          <el-button size="small" @click="addChild" :disabled="editing">增加下级(N)</el-button>
          <el-button size="small" @click="editCurrent" :disabled="editing || !selected">修改(E)</el-button>
          <el-button size="small" type="primary" @click="save" :disabled="!editing">保存</el-button>
          <el-button size="small" @click="handleClose">关闭(X)</el-button>
        </div>
      </div>
    </div>
  </FloatingPanel>
</template>

<script setup>
import { computed, reactive, ref, watch } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import FloatingPanel from '@/components/FloatingPanel.vue'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:modelValue'])

const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const treeData = ref([])
const selected = ref(null) // { level, main?, detail? }
const originalSelected = ref(null) // 保存原始选中节点，用于恢复
const editing = ref(false)
const editMode = ref('') // addMain/addDetail/editMain/editDetail

const formMain = reactive({ id: null, bmdm: '', bmmc: '', bmbh: '', syccbm: '', syccmc: '', tybz: false })
const formDetail = reactive({ id: null, bmdm: null, bm: '', bmsm: '', pym: '', szdm: '', mrzbz: false, tybz: false, bz: '' })

const loadTree = async () => {
  const mains = (await axios.get('/api/system/common-code/main/list')).data || []
  const mainNodes = []
  for (const m of mains) {
    const details = (await axios.get('/api/system/common-code/detail/list', { params: { bmdm: m.bmdm } })).data || []
    mainNodes.push({
      key: `m-${m.id}`,
      label: `[${m.bmdm}]${m.bmmc || ''}`,
      level: 0,
      main: m,
      children: details.map((d) => ({
        key: `d-${d.id}`,
        label: `[${d.bm}]${d.bmsm || ''}`,
        level: 1,
        main: m,
        detail: d
      }))
    })
  }
  treeData.value = mainNodes
}

const fillFromSelection = () => {
  if (!selected.value) return
  if (selected.value.level === 0) {
    const m = selected.value.main
    formMain.id = m.id
    formMain.bmdm = m.bmdm ?? ''
    formMain.bmmc = m.bmmc ?? ''
    formMain.bmbh = m.bmbh ?? ''
    formMain.syccbm = m.syccbm ?? ''
    formMain.syccmc = m.syccmc ?? ''
    formMain.tybz = !!m.tybz
  } else {
    const d = selected.value.detail
    formDetail.id = d.id
    formDetail.bmdm = d.bmdm
    formDetail.bm = d.bm ?? ''
    formDetail.bmsm = d.bmsm ?? ''
    formDetail.pym = d.pym ?? ''
    formDetail.szdm = d.szdm ?? ''
    formDetail.mrzbz = !!d.mrzbz
    formDetail.tybz = !!d.tybz
    formDetail.bz = d.bz ?? ''
  }
}

const onNodeClick = (node) => {
  if (editing.value) {
    // 编辑状态下，提示用户先保存或放弃
    ElMessage.warning('请先保存或放弃当前编辑')
    return
  }
  selected.value = node
  originalSelected.value = node
  fillFromSelection()
}

const addSibling = () => {
  // 保存原始选中节点
  originalSelected.value = selected.value ? { ...selected.value } : null
  
  if (!selected.value) {
    // 没选中：默认新增主表
    editMode.value = 'addMain'
    editing.value = true
    Object.assign(formMain, { id: null, bmdm: '', bmmc: '', bmbh: '', syccbm: '', syccmc: '', tybz: false })
    // 临时设置selected为level 0，用于显示主表面板
    selected.value = { level: 0, main: null }
    return
  }
  editing.value = true
  if (selected.value.level === 0) {
    editMode.value = 'addMain'
    Object.assign(formMain, { id: null, bmdm: '', bmmc: '', bmbh: '', syccbm: '', syccmc: '', tybz: false })
    // 保持selected为level 0，显示主表面板
  } else {
    editMode.value = 'addDetail'
    // 保存当前选中的节点信息
    const currentMain = selected.value.main
    Object.assign(formDetail, { id: null, bmdm: currentMain?.bmdm, bm: '', bmsm: '', pym: '', szdm: '', mrzbz: false, tybz: false, bz: '' })
    // 保持selected为level 1，显示明细面板
  }
}

const addChild = () => {
  if (!selected.value) {
    ElMessage.warning('请先选择一个主表项')
    return
  }
  // 保存原始选中节点
  originalSelected.value = { ...selected.value }
  
  editing.value = true
  editMode.value = 'addDetail'
  const main = selected.value.level === 0 ? selected.value.main : selected.value.main
  if (!main || !main.bmdm) {
    ElMessage.warning('无法添加下级：主表信息不完整')
    return
  }
  // 清空明细表单，准备新增
  Object.assign(formDetail, { id: null, bmdm: main.bmdm, bm: '', bmsm: '', pym: '', szdm: '', mrzbz: false, tybz: false, bz: '' })
  // 切换到明细视图（level 1），这样会显示明细面板
  selected.value = { level: 1, main, detail: null }
}

const editCurrent = () => {
  if (!selected.value) return
  editing.value = true
  editMode.value = selected.value.level === 0 ? 'editMain' : 'editDetail'
}

const cancelEdit = () => {
  editing.value = false
  editMode.value = ''
  // 恢复原始选中节点
  if (originalSelected.value) {
    selected.value = originalSelected.value
    fillFromSelection()
  } else if (selected.value) {
    // 如果没有原始选中，尝试恢复当前选中
    fillFromSelection()
  } else {
    // 如果selected被清空，清空表单
    Object.assign(formMain, { id: null, bmdm: '', bmmc: '', bmbh: '', syccbm: '', syccmc: '', tybz: false })
    Object.assign(formDetail, { id: null, bmdm: null, bm: '', bmsm: '', pym: '', szdm: '', mrzbz: false, tybz: false, bz: '' })
  }
}

const save = async () => {
  try {
    const currentMode = editMode.value
    const currentLevel = selected.value?.level
    
    if (currentMode === 'addMain' || currentMode === 'editMain' || (currentLevel === 0 && currentMode !== 'addDetail')) {
      // 保存主表
      if (!formMain.bmdm || !formMain.bmmc) {
        ElMessage.warning('编码代码和编码名称不能为空')
        return
      }
      const payload = {
        id: formMain.id,
        bmdm: Number(formMain.bmdm),
        bmmc: formMain.bmmc,
        bmbh: formMain.bmbh,
        syccbm: formMain.syccbm === '' ? null : Number(formMain.syccbm),
        syccmc: formMain.syccmc,
        tybz: !!formMain.tybz
      }
      const res = await axios.post('/api/system/common-code/main/save', payload)
      if (!res.data?.success) throw new Error(res.data?.message || '保存失败')
      ElMessage.success(res.data.message || '保存成功')
      editing.value = false
      editMode.value = ''
      await loadTree()
      // 如果是新增，选中新创建的节点
      if (currentMode === 'addMain') {
        const newNode = treeData.value.find(n => n.main?.bmdm === payload.bmdm)
        if (newNode) {
          selected.value = newNode
          originalSelected.value = newNode
          fillFromSelection()
        }
      } else if (originalSelected.value) {
        // 恢复原始选中节点
        const mainNode = treeData.value.find(n => n.main?.id === originalSelected.value.main?.id)
        if (mainNode) {
          selected.value = mainNode
          originalSelected.value = mainNode
          fillFromSelection()
        }
      } else if (selected.value) {
        // 编辑模式，恢复选中
        fillFromSelection()
      }
    } else {
      // 保存明细
      if (!formDetail.bmdm) {
        ElMessage.warning('无法保存：缺少主表编码')
        return
      }
      const payload = {
        detail: {
          id: formDetail.id,
          bmdm: Number(formDetail.bmdm),
          bm: formDetail.bm === '' ? null : Number(formDetail.bm),
          bmsm: formDetail.bmsm,
          pym: formDetail.pym,
          szdm: formDetail.szdm,
          mrzbz: !!formDetail.mrzbz,
          tybz: !!formDetail.tybz,
          bz: formDetail.bz
        },
        type: currentMode === 'addDetail' && currentLevel === 0 ? 'child' : 'sibling'
      }
      const res = await axios.post('/api/system/common-code/detail/save', payload)
      if (!res.data?.success) throw new Error(res.data?.message || '保存失败')
      ElMessage.success(res.data.message || '保存成功')
      editing.value = false
      editMode.value = ''
      await loadTree()
      // 如果是新增，选中新创建的节点
      if (currentMode === 'addDetail') {
        const mainNode = treeData.value.find(n => n.main?.bmdm === payload.detail.bmdm)
        if (mainNode) {
          if (res.data?.data?.id) {
            // 找到新创建的明细节点
            const newDetailNode = mainNode.children?.find(c => c.detail?.id === res.data.data.id)
            if (newDetailNode) {
              selected.value = newDetailNode
              originalSelected.value = newDetailNode
              fillFromSelection()
            }
          } else {
            // 如果没有返回id，选中主节点
            selected.value = mainNode
            originalSelected.value = mainNode
            fillFromSelection()
          }
        }
      } else if (originalSelected.value) {
        // 恢复原始选中节点
        const mainNode = treeData.value.find(n => n.main?.bmdm === originalSelected.value.main?.bmdm)
        if (mainNode) {
          if (originalSelected.value.level === 1 && originalSelected.value.detail?.id) {
            // 恢复明细节点
            const detailNode = mainNode.children?.find(c => c.detail?.id === originalSelected.value.detail.id)
            if (detailNode) {
              selected.value = detailNode
              originalSelected.value = detailNode
              fillFromSelection()
            } else {
              // 如果找不到，选中主节点
              selected.value = mainNode
              originalSelected.value = mainNode
              fillFromSelection()
            }
          } else {
            // 恢复主节点
            selected.value = mainNode
            originalSelected.value = mainNode
            fillFromSelection()
          }
        }
      } else if (selected.value) {
        // 恢复选中
        fillFromSelection()
      }
    }
  } catch (e) {
    ElMessage.error('保存失败：' + (e.response?.data?.message || e.message))
  }
}

watch(visible, async (v) => {
  if (v) {
    try {
      editing.value = false
      editMode.value = ''
      selected.value = null
      originalSelected.value = null
      await loadTree()
    } catch (e) {
      ElMessage.error('读取失败：' + (e.response?.data?.message || e.message))
    }
  }
})

const handleClose = () => {
  visible.value = false
}
</script>

<style scoped>
.common-code-panel :deep(.panel-content) {
  padding: 10px 12px 12px;
  background: #fff;
}

.cc {
  display: flex;
  gap: 8px;
  background: #fff;
}

.cc-left {
  width: 380px;
}

.tree-box {
  border: 1px solid #dcdfe6;
  background: #fff;
  height: 520px;
  overflow: auto;
  padding: 6px;
}

.cc-right {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.panel {
  border: 1px solid #dcdfe6;
  background: #fff;
  padding: 10px;
  min-height: 280px;
}

.form-row {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
  font-size: 12px;
}

.form-row label {
  width: 92px;
}

.inp {
  width: 220px;
}

.btns {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  justify-content: flex-start;
  padding: 6px 0;
}
</style>

