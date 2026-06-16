<template>
  <el-dialog :model-value="modelValue" @update:model-value="$emit('update:modelValue', $event)" title="分管规则设置" width="1200px" :close-on-click-modal="false" append-to-body>
    <div style="display:flex;gap:0;height:560px;border:1px solid #dcdfe6;">
      <!-- 左侧：大类 -->
      <div style="width:240px;display:flex;flex-direction:column;border-right:1px solid #dcdfe6;">
        <div style="padding:8px;background:#f5f7fa;border-bottom:1px solid #dcdfe6;font-weight:bold;">新增规则大类</div>
        <el-table :data="categories" border stripe size="small" highlight-current-row @current-change="onCatSelect" style="flex:1;">
          <el-table-column prop="dlid" label="大类编号" width="70" />
          <el-table-column prop="dlmc" label="规则名称" />
          <el-table-column prop="isuse" label="启用" width="45">
            <template #default="{row}">{{ row.isuse ? '是' : '否' }}</template>
          </el-table-column>
        </el-table>
        <div style="padding:8px;border-top:1px solid #dcdfe6;">
          <div style="display:flex;align-items:center;margin-bottom:6px;">
            <label style="width:60px;font-size:12px;">规则名称</label>
            <el-input v-model="catForm.dlmc" size="small" />
          </div>
          <div style="display:flex;align-items:center;margin-bottom:6px;">
            <label style="width:60px;font-size:12px;">是否使用</label>
            <el-switch v-model="catForm.isuse" :active-value="1" :inactive-value="0" size="small" />
          </div>
          <div style="display:flex;gap:8px;">
            <el-button size="small" type="success" @click="insertCat" style="flex:1;">新增</el-button>
            <el-button size="small" type="primary" @click="updateCat" style="flex:1;">修改</el-button>
          </div>
        </div>
      </div>

      <!-- 中间：小类 -->
      <div style="width:280px;display:flex;flex-direction:column;border-right:1px solid #dcdfe6;">
        <div style="padding:8px;background:#f5f7fa;border-bottom:1px solid #dcdfe6;font-weight:bold;">新增规则小类</div>
        <el-table :data="subcategories" border stripe size="small" highlight-current-row @current-change="onSubSelect" style="flex:1;">
          <el-table-column prop="xlbh" label="小类编号" width="65" />
          <el-table-column prop="xlmc" label="规则名称" />
          <el-table-column prop="yxxh" label="序号" width="45" />
          <el-table-column prop="isuse" label="启用" width="40">
            <template #default="{row}">{{ row.isuse ? '是' : '否' }}</template>
          </el-table-column>
        </el-table>
        <div style="padding:8px;border-top:1px solid #dcdfe6;">
          <div style="display:flex;align-items:center;margin-bottom:6px;">
            <label style="width:60px;font-size:12px;">规则名称</label>
            <el-input v-model="subNameForm.xlmc" size="small" />
          </div>
          <div style="display:flex;align-items:center;margin-bottom:6px;">
            <label style="width:60px;font-size:12px;">是否使用</label>
            <el-switch v-model="subNameForm.isuse" :active-value="1" :inactive-value="0" size="small" />
          </div>
          <div style="display:flex;gap:8px;">
            <el-button size="small" type="success" @click="insertSub" :disabled="!selectedCat" style="flex:1;">新增</el-button>
            <el-button size="small" type="primary" @click="updateSubName" :disabled="!selectedSub" style="flex:1;">修改</el-button>
          </div>
        </div>
      </div>

      <!-- 右侧 -->
      <div style="flex:1;display:flex;flex-direction:column;min-width:500px;">
        <!-- 右上：规则内容设置 -->
        <div style="padding:8px;border-bottom:1px solid #dcdfe6;">
          <div style="font-weight:bold;margin-bottom:8px;">规则内容设置</div>
          <div style="display:flex;flex-wrap:wrap;gap:8px 16px;align-items:center;">
            <div style="display:flex;align-items:center;">
              <label style="width:65px;font-size:12px;">优先序号</label>
              <el-input v-model="detailForm.yxxh" size="small" style="width:75px;" />
            </div>
            <div style="display:flex;align-items:center;">
              <label style="width:75px;font-size:12px;">条码号个数</label>
              <el-input v-model="detailForm.tmhgs" size="small" style="width:75px;" />
            </div>
            <div style="display:flex;align-items:center;">
              <label style="width:65px;font-size:12px;">条码份数</label>
              <el-select v-model="detailForm.tmfs" size="small" style="width:75px;" clearable>
                <el-option label="连续" value="连续" />
                <el-option label="住院" value="住院" />
                <el-option label="其他" value="其他" />
              </el-select>
            </div>
            <div style="display:flex;align-items:center;">
              <label style="width:80px;font-size:12px;">释放类标志</label>
              <el-switch v-model="detailForm.sflbz" :active-value="1" :inactive-value="0" size="small" />
            </div>
          </div>
          <div style="display:flex;flex-wrap:wrap;gap:8px 16px;align-items:center;margin-top:6px;">
            <div style="display:flex;align-items:center;">
              <label style="width:65px;font-size:12px;">试管颜色</label>
              <el-input v-model="detailForm.sgys" size="small" style="width:75px;" />
            </div>
            <div style="display:flex;align-items:center;position:relative;">
              <label style="width:65px;font-size:12px;">材料费代码</label>
              <el-input v-model="detailForm.clfdm" size="small" style="width:75px;" readonly />
            </div>
            <div style="display:flex;align-items:center;position:relative;">
              <label style="width:65px;font-size:12px;">材料费名称</label>
              <el-input v-model="detailForm.clfmc" size="small" style="width:180px;" placeholder="回车搜索" @keydown.enter.prevent="searchFeeItems" />
              <div v-if="feeSearchResults.length > 0" style="position:absolute;top:28px;left:65px;right:0;z-index:9999;background:#fff;border:1px solid #dcdfe6;border-radius:4px;max-height:200px;overflow-y:auto;box-shadow:0 2px 12px rgba(0,0,0,.12);min-width:320px;">
                <div v-for="item in feeSearchResults" :key="item.code" style="padding:6px 12px;cursor:pointer;display:flex;justify-content:space-between;font-size:12px;" @click="selectFeeItem(item)">
                  <span>{{ item.name }}</span>
                  <span style="color:#999;">{{ item.code }} | {{ item.pym }}</span>
                </div>
              </div>
            </div>
            <el-button size="small" @click="syncFeeByColor" :disabled="!detailForm.sgys || !detailForm.clfdm">同步</el-button>
          </div>
          <div style="display:flex;flex-wrap:wrap;gap:8px 16px;align-items:center;margin-top:6px;">
            <div style="display:flex;align-items:center;">
              <label style="width:65px;font-size:12px;">采集要求</label>
              <el-input v-model="detailForm.cjyq" size="small" style="width:200px;" />
            </div>
            <div style="display:flex;align-items:center;">
              <label style="width:65px;font-size:12px;">注意事项</label>
              <el-input v-model="detailForm.zysx" size="small" style="width:200px;" />
            </div>
            <el-button type="primary" size="small" @click="saveDetail" :disabled="!selectedSub">保存</el-button>
          </div>
        </div>

        <!-- 右下：组合项目 -->
        <div style="flex:1;display:flex;flex-direction:column;padding:8px;">
          <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:6px;">
            <span style="font-weight:bold;">规则对应组合项目</span>
            <div style="display:flex;gap:6px;">
              <el-checkbox v-model="showUnassigned" size="small">未分配组合</el-checkbox>
              <el-input v-model="comboSearch" placeholder="搜索" clearable size="small" style="width:120px;" @input="filterComboItems" />
              <el-button size="small" type="success" @click="openComboAssign" :disabled="!selectedSub">添加</el-button>
              <el-button size="small" type="danger" @click="removeSelectedCombos" :disabled="selectedCombos.length === 0">删除组合</el-button>
            </div>
          </div>
          <el-table ref="comboTableRef" :data="filteredComboItems" border stripe size="small" style="flex:1;" @selection-change="handleComboSelection" @row-dblclick="removeComboItem">
            <el-table-column type="selection" width="35" />
            <el-table-column prop="zhid" label="项目ID" width="80" />
            <el-table-column prop="zhxmmc" label="组合项目名称" />
            <el-table-column prop="his_zhmc" label="HIS组合名称" width="150" />
            <el-table-column prop="bbzlName" label="标本类型" width="80" />
          </el-table>
        </div>
      </div>
    </div>

    <!-- 添加组合项目弹窗 -->
    <el-dialog v-model="comboFormVisible" title="添加组合项目" width="600px" append-to-body>
      <div style="margin-bottom:12px;">
        <el-input v-model="comboSearch" placeholder="搜索项目代码或名称" clearable size="small" @input="filterAvailableCombos">
        </el-input>
      </div>
      <el-table ref="availableComboTableRef" :data="filteredAvailableComboItems" border stripe size="small" max-height="350" @selection-change="handleAvailableComboSelection">
        <el-table-column type="selection" width="35" />
        <el-table-column prop="zhid" label="项目ID" width="80" />
        <el-table-column prop="zhxmmc" label="项目名称" />
        <el-table-column prop="pym" label="拼音码" width="100" />
      </el-table>
      <template #footer>
        <el-button @click="comboFormVisible = false">取消</el-button>
        <el-button type="primary" @click="assignComboItems">添加选中 ({{ selectedAvailableCombos.length }})</el-button>
      </template>
    </el-dialog>
  </el-dialog>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'
import { fetchTubeCategories, saveTubeCategory, fetchTubeSubcategories, saveTubeSubcategory, fetchComboItemsByCat, fetchAvailableComboItems, saveComboMapping, removeComboMapping } from '../../api/tubeSplit'
import { fetchFeeItems, syncMaterialFee } from '../../api/materialFee'

const props = defineProps({ modelValue: Boolean })
const emit = defineEmits(['update:modelValue'])

const categories = ref([])
const selectedCat = ref(null)
const catForm = ref({ dlmc: '', isuse: 1 })

const subcategories = ref([])
const selectedSub = ref(null)
const subNameForm = ref({ xlmc: '', isuse: 1 })

const detailForm = ref({ yxxh: '', tmhgs: '', tmfs: '', sflbz: 0, sgys: '', clfdm: '', clfmc: '', cjyq: '', zysx: '' })
const feeSearchResults = ref([])

const comboTableRef = ref(null)
const comboItems = ref([])
const selectedCombos = ref([])
const filteredComboItems = ref([])
const comboSearch = ref('')
const showUnassigned = ref(false)

const comboFormVisible = ref(false)
const availableComboItems = ref([])
const selectedAvailableCombos = ref([])
const filteredAvailableComboItems = ref([])

const loadCategories = async () => {
  try {
    const { data } = await fetchTubeCategories()
    categories.value = Array.isArray(data) ? data : []
  } catch (e) {}
}

const onCatSelect = (row) => {
  selectedCat.value = row
  if (row) {
    catForm.value = { dlmc: row.dlmc || '', isuse: row.isuse ?? 1 }
    loadSubcategories(row.dlid)
  } else {
    subcategories.value = []
  }
  selectedSub.value = null
  resetDetail()
  comboItems.value = []
  filteredComboItems.value = []
}

const onSubSelect = (row) => {
  selectedSub.value = row
  if (row) {
    subNameForm.value = { xlmc: row.xlmc || '', isuse: row.isuse ?? 1 }
    detailForm.value = {
      yxxh: row.yxxh ?? '',
      tmhgs: row.tmhgs ?? '',
      tmfs: row.tmfs ?? '',
      sflbz: row.sflbz ?? 0,
      sgys: row.sgys ?? '',
      clfdm: row.clfdm ?? '',
      clfmc: row.clfmc ?? '',
      cjyq: row.cjyq ?? '',
      zysx: row.zysx ?? ''
    }
    loadComboItems(row.xlbh)
  } else {
    resetDetail()
    comboItems.value = []
    filteredComboItems.value = []
  }
}

const resetDetail = () => {
  detailForm.value = { yxxh: '', tmhgs: '', tmfs: '', sflbz: 0, sgys: '', clfdm: '', clfmc: '', cjyq: '', zysx: '' }
}

const loadSubcategories = async (dlid) => {
  try {
    const { data } = await fetchTubeSubcategories({ dlid })
    subcategories.value = Array.isArray(data) ? data : []
  } catch (e) {}
}

const loadComboItems = async (xlbh) => {
  try {
    const { data } = await fetchComboItemsByCat(xlbh)
    comboItems.value = Array.isArray(data) ? data : []
    filterComboItems()
  } catch (e) {}
}

const filterComboItems = () => {
  const search = comboSearch.value.toLowerCase()
  filteredComboItems.value = comboItems.value.filter(item =>
    !search || String(item.zhid).toLowerCase().includes(search) || item.zhxmmc?.toLowerCase().includes(search)
  )
}

const insertCat = async () => {
  if (!catForm.value.dlmc) { ElMessage.warning('请输入规则名称'); return }
  try {
    const { data } = await saveTubeCategory({ dlmc: catForm.value.dlmc, isuse: catForm.value.isuse })
    if (data.success) { ElMessage.success('新增成功'); loadCategories() }
    else ElMessage.error(data.message)
  } catch (e) { ElMessage.error('新增失败') }
}

const updateCat = async () => {
  if (!selectedCat.value) { ElMessage.warning('请先选择大类'); return }
  try {
    const { data } = await saveTubeCategory({ dlid: selectedCat.value.dlid, dlmc: catForm.value.dlmc, isuse: catForm.value.isuse })
    if (data.success) { ElMessage.success('修改成功'); loadCategories() }
    else ElMessage.error(data.message)
  } catch (e) { ElMessage.error('修改失败') }
}

const insertSub = async () => {
  if (!selectedCat.value) { ElMessage.warning('请先选择大类'); return }
  if (!subNameForm.value.xlmc) { ElMessage.warning('请输入规则名称'); return }
  try {
    const { data } = await saveTubeSubcategory({
      dlid: selectedCat.value.dlid, xlmc: subNameForm.value.xlmc, isuse: subNameForm.value.isuse,
      yxxh: 0, tmhgs: '', tmfs: '', sflbz: 0, cjyq: '', zysx: '', sgys: '', clfdm: '', clfmc: ''
    })
    if (data.success) { ElMessage.success('新增成功'); loadSubcategories(selectedCat.value.dlid) }
    else ElMessage.error(data.message)
  } catch (e) { ElMessage.error('新增失败') }
}

const updateSubName = async () => {
  if (!selectedSub.value) { ElMessage.warning('请先选择小类'); return }
  try {
    const { data } = await saveTubeSubcategory({
      xlbh: selectedSub.value.xlbh, dlid: selectedCat.value.dlid,
      xlmc: subNameForm.value.xlmc, isuse: subNameForm.value.isuse,
      yxxh: selectedSub.value.yxxh, tmhgs: selectedSub.value.tmhgs,
      tmfs: selectedSub.value.tmfs, sflbz: selectedSub.value.sflbz,
      cjyq: selectedSub.value.cjyq, zysx: selectedSub.value.zysx,
      sgys: selectedSub.value.sgys, clfdm: selectedSub.value.clfdm, clfmc: selectedSub.value.clfmc
    })
    if (data.success) { ElMessage.success('修改成功'); loadSubcategories(selectedCat.value.dlid) }
    else ElMessage.error(data.message)
  } catch (e) { ElMessage.error('修改失败') }
}

const saveDetail = async () => {
  if (!selectedSub.value) { ElMessage.warning('请先选择小类'); return }
  try {
    const { data } = await saveTubeSubcategory({
      xlbh: selectedSub.value.xlbh, dlid: selectedCat.value.dlid,
      xlmc: selectedSub.value.xlmc, isuse: selectedSub.value.isuse,
      yxxh: detailForm.value.yxxh, tmhgs: detailForm.value.tmhgs,
      tmfs: detailForm.value.tmfs, sflbz: detailForm.value.sflbz,
      cjyq: detailForm.value.cjyq, zysx: detailForm.value.zysx,
      sgys: detailForm.value.sgys, clfdm: detailForm.value.clfdm, clfmc: detailForm.value.clfmc
    })
    if (data.success) { ElMessage.success('保存成功'); loadSubcategories(selectedCat.value.dlid) }
    else ElMessage.error(data.message)
  } catch (e) { ElMessage.error('保存失败') }
}

const searchFeeItems = async () => {
  const keyword = detailForm.value.clfmc?.trim()
  if (!keyword) return
  try {
    const { data } = await fetchFeeItems({ pym: keyword })
    const items = Array.isArray(data) ? data : []
    if (items.length === 0) { ElMessage.warning('未找到匹配的费用项目'); feeSearchResults.value = []; return }
    feeSearchResults.value = items
  } catch (e) { feeSearchResults.value = [] }
}

const selectFeeItem = (item) => {
  detailForm.value.clfdm = item.code
  detailForm.value.clfmc = item.name
  feeSearchResults.value = []
}

const syncFeeByColor = async () => {
  if (!detailForm.value.sgys || !detailForm.value.clfdm) { ElMessage.warning('请先填写试管颜色和材料费'); return }
  try {
    await ElMessageBox.confirm(
      `确认将材料费 [${detailForm.value.clfdm} ${detailForm.value.clfmc}] 同步到所有试管颜色为"${detailForm.value.sgys}"的小类？`,
      '同步确认', { confirmButtonText: '确认', cancelButtonText: '取消', type: 'warning' }
    )
    const { data } = await syncMaterialFee({ sgys: detailForm.value.sgys, clfdm: detailForm.value.clfdm, clfmc: detailForm.value.clfmc })
    if (data.success) { ElMessage.success('同步成功'); if (selectedCat.value) loadSubcategories(selectedCat.value.dlid) }
    else ElMessage.error(data.message || '同步失败')
  } catch (e) { if (e !== 'cancel') ElMessage.error('同步失败') }
}

const openComboAssign = async () => {
  if (!selectedSub.value) return
  try {
    const { data } = await fetchAvailableComboItems(selectedSub.value.xlbh)
    availableComboItems.value = Array.isArray(data) ? data : []
    filteredAvailableComboItems.value = [...availableComboItems.value]
    selectedAvailableCombos.value = []
    comboFormVisible.value = true
  } catch (e) { ElMessage.error('加载可选项目失败') }
}

const filterAvailableCombos = () => {
  const search = comboSearch.value.toLowerCase()
  filteredAvailableComboItems.value = availableComboItems.value.filter(item =>
    !search || String(item.zhid).toLowerCase().includes(search) || item.zhxmmc?.toLowerCase().includes(search)
  )
}

const handleComboSelection = (selection) => { selectedCombos.value = selection }
const handleAvailableComboSelection = (selection) => { selectedAvailableCombos.value = selection }

const assignComboItems = async () => {
  if (!selectedAvailableCombos.value.length) { ElMessage.warning('请选择要添加的项目'); return }
  try {
    const items = selectedAvailableCombos.value.map(item => ({ xlbh: selectedSub.value.xlbh, zhid: item.zhid, zhxmmc: item.zhxmmc, yxxh: 0 }))
    const { data } = await saveComboMapping(selectedSub.value.xlbh, items)
    if (data.success) { ElMessage.success('添加成功'); comboFormVisible.value = false; loadComboItems(selectedSub.value.xlbh) }
    else ElMessage.error(data.message)
  } catch (e) { ElMessage.error('添加失败') }
}

const removeComboItem = async (row) => {
  try {
    const { data } = await removeComboMapping(selectedSub.value.xlbh, row.zhid)
    if (data.success) { loadComboItems(selectedSub.value.xlbh) }
    else ElMessage.error(data.message)
  } catch (e) {}
}

const removeSelectedCombos = async () => {
  if (!selectedCombos.value.length) return
  try {
    for (const item of selectedCombos.value) await removeComboMapping(selectedSub.value.xlbh, item.zhid)
    ElMessage.success('删除成功')
    loadComboItems(selectedSub.value.xlbh)
  } catch (e) { ElMessage.error('删除失败') }
}

watch(comboSearch, () => { filterComboItems() })
onMounted(() => { loadCategories() })
</script>

<style scoped>
</style>
