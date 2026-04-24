<template>
  <el-dialog
    v-model="visible"
    title="流程控制设置"
    width="520px"
    :close-on-click-modal="false"
    class="process-control-dialog windows-dialog"
  >
    <div class="dialog-body">
      <div class="box">
        <el-checkbox v-model="form.sqkg" class="chk">
          双签控制开关（勾中开关，那么必须点击检验，才能进行审核操作）
        </el-checkbox>

        <el-checkbox v-model="form.mzsjkg" class="chk" disabled>
          门诊收据使用控制（勾选则门诊收据可重复使用，反之则只能使用一次）
        </el-checkbox>

        <el-checkbox v-model="form.jmjkk" class="chk" disabled>
          居民健康卡使用控制（勾选则在进行提取时无需手工录入直接提取卡号信息）
        </el-checkbox>

        <div class="warn">系统设置，请勿随意修改</div>
      </div>
    </div>

    <template #footer>
      <el-button @click="handleClose">关闭(C)</el-button>
      <el-button type="primary" @click="handleSave">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { computed, reactive, watch } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const props = defineProps({
  modelValue: { type: Boolean, default: false }
})
const emit = defineEmits(['update:modelValue'])

const visible = computed({
  get: () => props.modelValue,
  set: (v) => emit('update:modelValue', v)
})

const form = reactive({
  sqkg: false, // id=1
  mzsjkg: false, // id=2
  jmjkk: false // id=3
})

const load = async () => {
  const res = await axios.get('/api/system/process-control/list')
  if (!res.data?.success) throw new Error(res.data?.message || '读取失败')

  const list = res.data.data || []
  const byId = new Map(list.map((r) => [Number(r.id), r]))
  form.sqkg = Number(byId.get(1)?.kgz || 0) === 1
  form.mzsjkg = Number(byId.get(2)?.kgz || 0) === 1
  form.jmjkk = Number(byId.get(3)?.kgz || 0) === 1
}

watch(visible, async (v) => {
  if (v) {
    try {
      await load()
    } catch (e) {
      ElMessage.error('读取失败：' + (e.response?.data?.message || e.message))
    }
  }
})

const handleSave = async () => {
  try {
    const res = await axios.post('/api/system/process-control/save', { ...form })
    if (res.data?.success) {
      ElMessage.success(res.data?.message || '设置成功！（需重启程序后生效）')
      visible.value = false
    } else {
      ElMessage.warning(res.data?.message || '设置失败')
    }
  } catch (e) {
    ElMessage.error('保存失败：' + (e.response?.data?.message || e.message))
  }
}

const handleClose = () => {
  visible.value = false
}
</script>

<style scoped>
.process-control-dialog :deep(.el-dialog) {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  background: #fff;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.15);
}

.process-control-dialog :deep(.el-dialog__header) {
  background: #fff;
  color: #303133;
  padding: 14px 16px;
  border-bottom: 1px solid #dcdfe6;
  font-size: 16px;
  font-weight: 500;
}

.process-control-dialog :deep(.el-dialog__body) {
  padding: 20px 16px;
  background: #fff;
}

.process-control-dialog :deep(.el-dialog__footer) {
  padding: 12px 16px;
  background: #fff;
  border-top: 1px solid #dcdfe6;
}

.dialog-body {
  background: #fff;
}

.box {
  background: #fff;
  padding: 6px 6px 12px;
}

.chk {
  display: block;
  margin: 16px 0;
  font-size: 14px;
  color: #303133;
}

.warn {
  margin-top: 20px;
  color: #f56c6c;
  font-size: 13px;
}
</style>


