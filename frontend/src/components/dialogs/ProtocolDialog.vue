<template>
  <el-dialog :model-value="modelValue" @update:model-value="$emit('update:modelValue', $event)" title="通信协议" width="900px" :close-on-click-modal="false" append-to-body>
    <el-tabs v-model="activeTab">
      <el-tab-pane label="ASTM协议" name="astm">
        <div style="margin-bottom:12px;display:flex;gap:8px;align-items:center;">
          <el-input v-model="astmSbDjid" placeholder="设备ID" style="width:120px" />
          <el-button type="success" @click="doAstmStart">启动</el-button>
          <el-button type="danger" @click="doAstmStop">停止</el-button>
          <el-button @click="loadAstmStatus">刷新状态</el-button>
        </div>

        <el-descriptions :column="2" border size="small" style="margin-bottom:12px;">
          <el-descriptions-item label="连接状态">
            <el-tag :type="astmStatus.connected ? 'success' : 'info'" size="small">
              {{ astmStatus.connected ? '已连接' : '未连接' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="最后通信">{{ astmStatus.lastCommunication || '-' }}</el-descriptions-item>
        </el-descriptions>

        <h4 style="margin:0 0 8px;">ASTM数据解析</h4>
        <el-input v-model="astmRawData" type="textarea" :rows="6" placeholder="粘贴ASTM原始数据（每行一条记录）" />
        <el-button type="primary" @click="doAstmParse" style="margin-top:8px;">解析</el-button>

        <el-table v-if="astmRecords.length" :data="astmRecords" border stripe size="small" max-height="200" style="margin-top:12px;">
          <el-table-column prop="sampleId" label="样本ID" width="120" />
          <el-table-column prop="testCode" label="项目代码" width="100" />
          <el-table-column prop="result" label="结果" width="100" />
          <el-table-column prop="unit" label="单位" width="80" />
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="HL7协议" name="hl7">
        <div style="margin-bottom:12px;">
          <el-button type="success" @click="doHl7Send">发送HL7消息</el-button>
        </div>

        <h4 style="margin:0 0 8px;">HL7消息解析</h4>
        <el-input v-model="hl7Message" type="textarea" :rows="8" placeholder="粘贴HL7消息文本" />
        <el-button type="primary" @click="doHl7Parse" style="margin-top:8px;">解析</el-button>

        <template v-if="hl7Parsed">
          <el-descriptions title="患者信息" :column="2" border size="small" style="margin-top:12px;" v-if="hl7Parsed.patientInfo">
            <el-descriptions-item label="患者ID">{{ hl7Parsed.patientInfo.patientId || '-' }}</el-descriptions-item>
            <el-descriptions-item label="患者姓名">{{ hl7Parsed.patientInfo.patientName || '-' }}</el-descriptions-item>
          </el-descriptions>

          <div v-if="hl7Parsed.orders?.length" style="margin-top:12px;">
            <strong>医嘱</strong>
            <el-table :data="hl7Parsed.orders" border stripe size="small" style="margin-top:8px;">
              <el-table-column prop="orderCode" label="医嘱代码" />
              <el-table-column prop="requestDate" label="请求日期" />
            </el-table>
          </div>

          <div v-if="hl7Parsed.results?.length" style="margin-top:12px;">
            <strong>结果</strong>
            <el-table :data="hl7Parsed.results" border stripe size="small" style="margin-top:8px;">
              <el-table-column prop="testCode" label="项目代码" width="100" />
              <el-table-column prop="resultValue" label="结果值" width="100" />
              <el-table-column prop="unit" label="单位" width="80" />
              <el-table-column prop="refRange" label="参考范围" width="100" />
              <el-table-column prop="valueType" label="类型" width="60" />
            </el-table>
          </div>

          <el-descriptions :column="1" border size="small" style="margin-top:8px;">
            <el-descriptions-item label="段数量">{{ hl7Parsed.segmentCount || 0 }}</el-descriptions-item>
          </el-descriptions>
        </template>
      </el-tab-pane>
    </el-tabs>
  </el-dialog>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { fetchAstmStatus, startAstm, stopAstm, parseAstm, sendHl7, parseHl7 } from '../../api/protocol'

const props = defineProps({ modelValue: Boolean })
const emit = defineEmits(['update:modelValue'])

const activeTab = ref('astm')

const astmSbDjid = ref('')
const astmStatus = ref({})
const astmRawData = ref('')
const astmRecords = ref([])

const hl7Message = ref('')
const hl7Parsed = ref(null)

const loadAstmStatus = async () => {
  try {
    const { data } = await fetchAstmStatus({ sbDjid: astmSbDjid.value })
    astmStatus.value = data || {}
  } catch (e) {}
}

const doAstmStart = async () => {
  try {
    const { data } = await startAstm({ sbDjid: astmSbDjid.value })
    if (data.success) { ElMessage.success('ASTM已启动'); loadAstmStatus() }
    else ElMessage.error(data.message)
  } catch (e) { ElMessage.error('启动失败') }
}

const doAstmStop = async () => {
  try {
    const { data } = await stopAstm({ sbDjid: astmSbDjid.value })
    if (data.success) { ElMessage.success('ASTM已停止'); loadAstmStatus() }
    else ElMessage.error(data.message)
  } catch (e) { ElMessage.error('停止失败') }
}

const doAstmParse = async () => {
  if (!astmRawData.value.trim()) return
  try {
    const { data } = await parseAstm({ rawData: astmRawData.value })
    astmRecords.value = data?.records || []
    if (data?.success) ElMessage.success(`解析完成: ${data.totalLines} 行`)
    else ElMessage.warning('解析结果为空')
  } catch (e) { ElMessage.error('解析失败') }
}

const doHl7Send = async () => {
  if (!hl7Message.value.trim()) { ElMessage.warning('请输入HL7消息'); return }
  try {
    const { data } = await sendHl7({ message: hl7Message.value })
    if (data.success) ElMessage.success('发送成功: ' + (data.messageId || ''))
    else ElMessage.error(data.message)
  } catch (e) { ElMessage.error('发送失败') }
}

const doHl7Parse = async () => {
  if (!hl7Message.value.trim()) return
  try {
    const { data } = await parseHl7({ message: hl7Message.value })
    hl7Parsed.value = data?.parsed || null
  } catch (e) { ElMessage.error('解析失败') }
}
</script>
