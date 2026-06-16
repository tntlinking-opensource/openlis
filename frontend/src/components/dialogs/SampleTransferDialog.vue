<template>
  <el-dialog 
    :model-value="visible" 
    @update:model-value="$emit('update:visible', $event)" 
    title="样本转移" 
    width="500px" 
    :close-on-click-modal="false" 
    append-to-body
  >
    <div class="sample-transfer-dialog">
      <div class="sample-info" v-if="sample">
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="姓名">{{ sample.brxm || sample.name }}</el-descriptions-item>
          <el-descriptions-item label="条码号">{{ sample.brxx_tmh || sample.barcode }}</el-descriptions-item>
          <el-descriptions-item label="样本号">{{ sample.syh || sample.sampleNo }}</el-descriptions-item>
          <el-descriptions-item label="当前仪器">{{ sample.sbmc || sample.instrument }}</el-descriptions-item>
        </el-descriptions>
      </div>
      
      <el-form :model="form" label-width="100px" style="margin-top: 16px;">
        <el-form-item label="目标日期" required>
          <el-date-picker 
            v-model="form.targetDate" 
            type="date" 
            placeholder="选择目标日期"
            value-format="YYYY-MM-DD"
            style="width: 100%;"
          />
        </el-form-item>
        
        <el-form-item label="目标仪器" required>
          <el-select v-model="form.targetInstrument" placeholder="请选择目标仪器" style="width: 100%;">
            <el-option 
              v-for="i in instrumentList" 
              :key="i.sb_djid" 
              :label="i.sbmc" 
              :value="i.sb_djid" 
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="目标样本号">
          <el-input v-model="form.targetSyh" placeholder="留空则自动生成" />
        </el-form-item>
        
        <el-form-item label="起始编号">
          <el-input-number v-model="form.startNo" :min="1" placeholder="起始编号" style="width: 100%;" />
        </el-form-item>
        
        <el-form-item label="备注">
          <el-input v-model="form.notes" type="textarea" :rows="2" placeholder="备注信息" />
        </el-form-item>
      </el-form>
    </div>
    
    <template #footer>
      <el-button @click="handleCancel">取消</el-button>
      <el-button type="primary" @click="handleConfirm" :loading="loading">确认转移</el-button>
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
  targetDate: new Date().toISOString().slice(0, 10),
  targetInstrument: null,
  targetSyh: '',
  startNo: 1,
  notes: ''
})

const loading = ref(false)
const instrumentList = ref([])

const loadInstruments = async () => {
  try {
    const { data } = await axios.get('/api/instrument/list')
    instrumentList.value = Array.isArray(data) ? data : (data?.data || [])
  } catch (e) {
    instrumentList.value = []
  }
}

watch(() => props.visible, (val) => {
  if (val) {
    loadInstruments()
    form.value = {
      targetDate: new Date().toISOString().slice(0, 10),
      targetInstrument: null,
      targetSyh: '',
      startNo: 1,
      notes: ''
    }
  }
})

const handleConfirm = async () => {
  if (!form.value.targetDate) {
    ElMessage.warning('请选择目标日期')
    return
  }
  
  if (!form.value.targetInstrument) {
    ElMessage.warning('请选择目标仪器')
    return
  }
  
  if (!props.sample || !props.sample.brxx_id) {
    ElMessage.error('样本信息不完整')
    return
  }
  
  loading.value = true
  try {
    const { data } = await axios.post('/api/sample/transfer', {
      brxxId: props.sample.brxx_id,
      targetDate: form.value.targetDate,
      targetInstrument: form.value.targetInstrument,
      targetSyh: form.value.targetSyh,
      startNo: form.value.startNo,
      notes: form.value.notes
    })
    if (data.success) {
      ElMessage.success(data.message || '样本转移成功')
      emit('confirm', { ...form.value })
      emit('update:visible', false)
    } else {
      ElMessage.error(data.message || '转移失败')
    }
  } catch (e) {
    ElMessage.error('转移失败：' + (e.response?.data?.message || e.message))
  } finally {
    loading.value = false
  }
}

const handleCancel = () => {
  emit('cancel')
  emit('update:visible', false)
}
</script>
