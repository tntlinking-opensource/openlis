<template>
  <el-dialog 
    :model-value="visible" 
    @update:model-value="$emit('update:visible', $event)" 
    title="修改审核时间" 
    width="600px" 
    :close-on-click-modal="false" 
    append-to-body
  >
    <div class="edit-time-dialog">
      <div class="sample-info" v-if="sample">
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="姓名">{{ sample.brxm || sample.name }}</el-descriptions-item>
          <el-descriptions-item label="条码号">{{ sample.brxx_tmh || sample.barcode }}</el-descriptions-item>
          <el-descriptions-item label="样本号">{{ sample.syh || sample.sampleNo }}</el-descriptions-item>
        </el-descriptions>
      </div>
      
      <el-form :model="form" label-width="100px" style="margin-top: 16px;">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="采样时间">
              <el-date-picker 
                v-model="form.cjrq" 
                type="datetime" 
                placeholder="选择时间"
                value-format="YYYY-MM-DD HH:mm:ss"
                style="width: 100%;"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="接收时间">
              <el-date-picker 
                v-model="form.jsrq" 
                type="datetime" 
                placeholder="选择时间"
                value-format="YYYY-MM-DD HH:mm:ss"
                style="width: 100%;"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="申请时间">
              <el-date-picker 
                v-model="form.sqrq" 
                type="datetime" 
                placeholder="选择时间"
                value-format="YYYY-MM-DD HH:mm:ss"
                style="width: 100%;"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="检验时间">
              <el-date-picker 
                v-model="form.jyrq" 
                type="datetime" 
                placeholder="选择时间"
                value-format="YYYY-MM-DD HH:mm:ss"
                style="width: 100%;"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="审核时间">
              <el-date-picker 
                v-model="form.shrq" 
                type="datetime" 
                placeholder="选择时间"
                value-format="YYYY-MM-DD HH:mm:ss"
                style="width: 100%;"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </div>
    
    <template #footer>
      <el-button @click="handleCancel">取消</el-button>
      <el-button type="primary" @click="handleConfirm" :loading="loading">确认修改</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  sample: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['update:visible', 'confirm', 'cancel'])

const form = ref({
  cjrq: '',
  jsrq: '',
  sqrq: '',
  jyrq: '',
  shrq: ''
})

const loading = ref(false)

watch(() => props.visible, (val) => {
  if (val && props.sample) {
    form.value = {
      cjrq: props.sample.cjrq || '',
      jsrq: props.sample.jsrq || '',
      sqrq: props.sample.sqrq || '',
      jyrq: props.sample.jyrq || '',
      shrq: props.sample.shrq || ''
    }
  }
})

const handleConfirm = async () => {
  if (!props.sample || !props.sample.brxx_id) {
    ElMessage.error('样本信息不完整')
    return
  }
  
  loading.value = true
  try {
    const { data } = await axios.put(`/api/sample/updateTime/${props.sample.brxx_id}`, form.value)
    if (data.success) {
      ElMessage.success(data.message || '时间修改成功')
      emit('confirm', { ...form.value })
      emit('update:visible', false)
    } else {
      ElMessage.error(data.message || '修改失败')
    }
  } catch (e) {
    ElMessage.error('修改失败：' + (e.response?.data?.message || e.message))
  } finally {
    loading.value = false
  }
}

const handleCancel = () => {
  emit('cancel')
  emit('update:visible', false)
}
</script>
