<template>
  <FloatingPanel
    v-model="visible"
    title="工程师系统设置"
    :width="650"
    :height="500"
    class="engineer-setting-panel"
  >
    <div class="dialog-content">
      <div class="gcsxtsz">
        <div class="groupbox">
          <div class="group-title">系统参数设置</div>

          <div class="row">
            <div class="field">
              <label>医院代码：</label>
              <el-input v-model="form.wYydm" size="small" class="inp short" />
            </div>
            <div class="field grow">
              <label>医院名称：</label>
              <el-input v-model="form.yymc" size="small" class="inp" />
            </div>
          </div>

          <div class="row">
            <el-checkbox v-model="form.hisConnectbz" label="是否与HIS系统连接" disabled />
            <el-checkbox v-model="form.tjConnectbz" label="是否与体检系统连接" disabled />
            <el-checkbox v-model="form.yszConnectbz" label="是否与医生工作站连接" disabled />
          </div>

          <div class="row">
            <div class="radio-groupbox">
              <div class="radio-title">HIS连接级别：</div>
              <el-radio-group v-model="form.hisConnectLevel" disabled>
                <el-radio :label="1">初级：仅读取病人信息</el-radio>
                <el-radio :label="2">高级：读取病人信息并记帐</el-radio>
              </el-radio-group>
            </div>
          </div>

          <div class="row">
            <div class="sub-groupbox">
              <div class="sub-title">检验结果回写设置</div>
              <div class="sub-content">
                <el-checkbox v-model="form.tjJghcbz" label="向体检系统回写检验结果" disabled />
                <el-checkbox v-model="form.yszJghcbz" label="向医生站回写检验结果" disabled />
                <el-checkbox v-model="form.qtxtJghcbz" label="向其它第三方系统回写检验结果" disabled />
              </div>
            </div>
          </div>

          <div class="warn">
            提示：请不要随意修改设置参数，设置错误将造成系统报错而无法正常使用！
          </div>
        </div>

        <div class="groupbox bottom">
          <div class="row bottom-row">
            <el-checkbox v-model="form.websc" label="WEB上传下载开关" disabled />
            <div class="field">
              <label>报告归档时间：</label>
              <el-input v-model="form.gdsj" size="small" class="inp tiny" disabled />
              <span class="unit">天</span>
            </div>
            <el-checkbox v-model="form.hisConnectYbzx" label="样本中心收费开关" disabled />
          </div>
        </div>
      </div>
    </div>
    
    <div class="panel-footer">
      <el-button size="small" @click="handleCancel">关闭(C)</el-button>
      <el-button size="small" type="primary" @click="handleSave">确定(O)</el-button>
    </div>
  </FloatingPanel>
</template>

<script setup>
import { computed, reactive, watch } from 'vue'
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

const form = reactive({
  wYydm: '',
  yymc: '',
  jykksdm: '',
  hisConnectbz: false,
  hisConnectLevel: 0,
  tjConnectbz: false,
  yszConnectbz: false,
  tjJghcbz: false,
  yszJghcbz: false,
  qtxtJghcbz: false,
  websc: false,
  gdsj: 10,
  hisConnectYbzx: false
})

const loadConfig = async () => {
  const res = await axios.get('/api/engineer/setting/config')
  if (res.data?.success) {
    const d = res.data.data || {}
    form.wYydm = d.wYydm || ''
    form.yymc = d.yymc || ''
    form.jykksdm = d.jykksdm || ''
    form.hisConnectbz = !!d.hisConnectbz
    form.hisConnectLevel = Number(d.hisConnectLevel || 0)
    form.tjConnectbz = !!d.tjConnectbz
    form.yszConnectbz = !!d.yszConnectbz
    form.tjJghcbz = !!d.tjJghcbz
    form.yszJghcbz = !!d.yszJghcbz
    form.qtxtJghcbz = !!d.qtxtJghcbz
    form.websc = !!d.websc
    form.gdsj = Number(d.gdsj ?? 10)
    form.hisConnectYbzx = !!d.hisConnectYbzx
  } else {
    ElMessage.error(res.data?.message || '读取系统配置失败')
  }
}

watch(visible, async (v) => {
  if (v) {
    try {
      await loadConfig()
    } catch (e) {
      ElMessage.error('读取系统配置失败：' + (e.response?.data?.message || e.message))
    }
  }
})

const handleSave = async () => {
  try {
    // 前端同款校验（后端也会做）
    if (!String(form.yymc || '').trim()) {
      ElMessage.warning('医院名称不能为空!')
      return
    }
    if (form.gdsj === null || form.gdsj === undefined || String(form.gdsj).trim() === '') {
      ElMessage.warning('归档时间不能为空!')
      return
    }
    if (form.hisConnectbz && (!form.hisConnectLevel || Number(form.hisConnectLevel) <= 0)) {
      ElMessage.warning('请选择HIS连接级别!')
      return
    }

    const userStr = localStorage.getItem('user')
    const user = userStr ? JSON.parse(userStr) : null
    const payload = {
      ...form,
      // 旧系统用 pub_ksdm，这里取当前登录人科室代码（若有）
      jykksdm: form.jykksdm || user?.ksdm || ''
    }

    const res = await axios.post('/api/engineer/setting/config', payload)
    if (res.data?.success) {
      ElMessage.success(res.data?.message || '设置成功!')
      visible.value = false
    } else {
      ElMessage.warning(res.data?.message || '设置失败!')
    }
  } catch (e) {
    ElMessage.error('保存失败：' + (e.response?.data?.message || e.message))
  }
}

const handleCancel = () => {
  visible.value = false
}
</script>

<style scoped>
.gcsxtsz {
  background: #fff;
}

.groupbox {
  border: 1px solid rgba(0, 0, 0, 0.25);
  background: #fff;
  padding: 10px 10px 12px;
  position: relative;
}

.group-title {
  position: absolute;
  top: -9px;
  left: 10px;
  padding: 0 6px;
  background: #fff;
  font-size: 12px;
  color: #000;
}

.row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 10px;
  flex-wrap: wrap;
}

.field {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #000;
}

.field.grow {
  flex: 1;
  min-width: 260px;
}

.inp :deep(.el-input__wrapper) {
  background: #fff;
}

.short {
  width: 90px;
}

.tiny {
  width: 80px;
}

.radio-groupbox {
  border: 1px solid rgba(0, 0, 0, 0.25);
  background: rgba(255, 255, 255, 0.15);
  padding: 8px 10px;
  width: 100%;
}

.radio-title {
  font-size: 12px;
  margin-bottom: 6px;
}

.sub-groupbox {
  border: 1px solid rgba(0, 0, 0, 0.25);
  background: rgba(255, 255, 255, 0.15);
  padding: 8px 10px;
  width: 100%;
}

.sub-title {
  font-size: 12px;
  margin-bottom: 6px;
}

.sub-content {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.warn {
  margin-top: 14px;
  color: #c00;
  font-weight: bold;
  font-size: 14px;
  text-align: left;
}

.bottom {
  margin-top: 0;
  border-top: none;
  padding-top: 12px;
}

.bottom-row {
  justify-content: space-between;
}

.unit {
  margin-left: 4px;
}

.engineer-setting-panel :deep(.panel-content) {
  padding: 12px;
  background: #fff;
}

.panel-footer {
  padding: 8px 12px;
  background: #fff;
  border-top: 1px solid #dcdfe6;
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}
</style>

