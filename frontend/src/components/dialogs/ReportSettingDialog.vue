<template>
  <el-dialog
    :model-value="modelValue"
    @update:modelValue="$emit('update:modelValue', $event)"
    title="检验报告模板设计器"
    width="1100px"
    top="1vh"
    :close-on-click-modal="false"
    append-to-body
    destroy-on-close
  >
    <div style="display:flex;gap:12px;height:82vh;">
      <!-- 左侧：模板列表 -->
      <div style="width:200px;border:1px solid #dcdfe6;border-radius:4px;padding:8px;display:flex;flex-direction:column;">
        <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:6px;">
          <span style="font-size:12px;color:#909399;font-weight:bold;">模板列表</span>
          <el-button
            v-if="selectedTemplateIds.length > 0"
            size="mini"
            type="danger"
            @click="batchDeleteTemplates"
          >删除({{ selectedTemplateIds.length }})</el-button>
        </div>
        <div style="flex:1;overflow-y:auto;border:1px solid #ebeef5;border-radius:4px;">
          <div
            v-for="t in templateList"
            :key="t.template_id"
            style="padding:8px 12px;cursor:pointer;border-bottom:1px solid #f5f7fa;font-size:12px;white-space:nowrap;overflow:hidden;text-overflow:ellipsis;display:flex;align-items:center;"
            :style="currentTemplateId === t.template_id ? 'background:#ecf5ff;color:#409eff;font-weight:bold;' : 'background:white;'"
          >
            <el-checkbox
              :model-value="selectedTemplateIds.includes(t.template_id)"
              @change="toggleTemplateSelect(t.template_id)"
              @click.stop
              style="margin-right:8px;"
            />
            <span @click="onTemplateChange(t.template_id)" style="flex:1;overflow:hidden;text-overflow:ellipsis;">
              {{ t.template_name }}
            </span>
          </div>
          <div v-if="templateList.length === 0" style="padding:20px;text-align:center;color:#909399;font-size:12px;">
            暂无模板
          </div>
        </div>
        <div v-if="currentTemplateId" style="margin-top:8px;border-top:1px solid #ebeef5;padding-top:8px;">
          <div style="font-size:12px;color:#909399;margin-bottom:4px;">关联仪器</div>
          <el-select v-model="templateSbDjid" placeholder="通用模板" clearable size="small" style="width:100%;" @change="handleTemplateInstrumentChange">
            <el-option :value="null" label="通用模板（不限仪器）" />
            <el-option v-for="d in deviceList" :key="d.sb_djid" :value="d.sb_djid" :label="d.sbmc" />
          </el-select>
        </div>
        <div v-if="currentTemplateId" style="margin-top:8px;border-top:1px solid #ebeef5;padding-top:8px;">
          <div style="font-size:12px;color:#909399;margin-bottom:4px;">报告编号</div>
          <el-input v-model="templateBgbh" placeholder="报告编号" size="small" style="width:100%;" />
        </div>
        <div v-if="currentTemplateId" style="margin-top:8px;border-top:1px solid #ebeef5;padding-top:8px;">
          <div style="font-size:12px;color:#909399;margin-bottom:4px;">报告名称</div>
          <el-input v-model="templateBgmc" placeholder="报告名称" size="small" style="width:100%;" />
        </div>
      </div>

      <!-- 中间：组件面板 -->
      <div style="width:220px;border:1px solid #dcdfe6;border-radius:4px;padding:8px;overflow-y:auto;display:flex;flex-direction:column;">
        <el-button size="small" type="warning" icon="MagicStick" @click="autoFill" style="width:100%;margin-bottom:12px;">智能填充</el-button>
        <h4 style="margin:0 0 12px 0;font-size:14px;color:#303133;text-align:left;">组件库</h4>

        <!-- 基础组件 -->
        <div style="margin-bottom:12px;text-align:left;">
          <div style="font-size:12px;color:#909399;margin-bottom:6px;">基础组件</div>
          <div style="display:grid;grid-template-columns:1fr 1fr;gap:4px;">
            <el-button size="small" type="primary" plain @click="addComponent('title')">标题</el-button>
            <el-button size="small" type="primary" plain @click="addComponent('subtitle')">副标题</el-button>
            <el-button size="small" type="primary" plain @click="addComponent('divider')">分隔线</el-button>
            <el-button size="small" type="primary" plain @click="addComponent('space')">间距</el-button>
          </div>
        </div>

        <!-- 信息组件 -->
        <div style="margin-bottom:12px;text-align:left;">
          <div style="font-size:12px;color:#909399;margin-bottom:6px;">信息字段</div>
          <div style="display:grid;grid-template-columns:1fr 1fr;gap:4px;">
            <el-button size="small" @click="addComponent('field', {label:'患者姓名', field:'patientName'})">患者姓名</el-button>
            <el-button size="small" @click="addComponent('field', {label:'性别', field:'gender'})">性别</el-button>
            <el-button size="small" @click="addComponent('field', {label:'年龄', field:'age'})">年龄</el-button>
            <el-button size="small" @click="addComponent('field', {label:'科室', field:'department'})">科室</el-button>
            <el-button size="small" @click="addComponent('field', {label:'床号', field:'bedNo'})">床号</el-button>
            <el-button size="small" @click="addComponent('field', {label:'住院号', field:'inpatientNo'})">住院号</el-button>
            <el-button size="small" @click="addComponent('field', {label:'标本类型', field:'specimenType'})">标本类型</el-button>
            <el-button size="small" @click="addComponent('field', {label:'标本', field:'specimen'})">标本</el-button>
            <el-button size="small" @click="addComponent('field', {label:'病历号', field:'medicalRecordNo'})">病历号</el-button>
            <el-button size="small" @click="addComponent('field', {label:'病区', field:'ward'})">病区</el-button>
            <el-button size="small" @click="addComponent('field', {label:'样本条码', field:'sampleBarcode'})">样本条码</el-button>
            <el-button size="small" @click="addComponent('field', {label:'检测号', field:'testNo'})">检测号</el-button>
            <el-button size="small" @click="addComponent('field', {label:'采集时间', field:'collectTime'})">采集时间</el-button>
            <el-button size="small" @click="addComponent('field', {label:'接收时间', field:'receiveTime'})">接收时间</el-button>
            <el-button size="small" @click="addComponent('field', {label:'检验项目', field:'testItems'})">检验项目</el-button>
            <el-button size="small" @click="addComponent('field', {label:'临床诊断', field:'diagnosis'})">临床诊断</el-button>
            <el-button size="small" @click="addComponent('field', {label:'申请医生', field:'requestDoctor'})">申请医生</el-button>
            <el-button size="small" @click="addComponent('field', {label:'检验者', field:'inspector'})">检验者</el-button>
            <el-button size="small" @click="addComponent('field', {label:'复核者', field:'reviewer'})">复核者</el-button>
            <el-button size="small" @click="addComponent('field', {label:'报告时间', field:'reportTime'})">报告时间</el-button>
          </div>
          <el-divider style="margin:8px 0;" />
          <div style="font-size:11px;color:#909399;margin-bottom:4px;">自定义字段</div>
          <div style="display:grid;grid-template-columns:1fr 1fr;gap:4px;margin-bottom:4px;">
            <el-input v-model="customField.label" size="small" placeholder="显示名称" />
            <el-input v-model="customField.field" size="small" placeholder="字段名" />
          </div>
          <el-button size="small" type="primary" @click="addCustomField" style="width:100%;">添加自定义字段</el-button>
        </div>

        <!-- 表格组件 -->
        <div style="margin-bottom:12px;text-align:left;">
          <div style="font-size:12px;color:#909399;margin-bottom:6px;">检验结果表格</div>
          <el-button size="small" type="success" @click="addComponent('resultTable')" style="width:100%;margin-bottom:4px;">添加结果表格</el-button>
          <el-button size="small" type="info" @click="resultEditorVisible = true" style="width:100%;">编辑结果数据 ({{resultRows.length}}条)</el-button>
        </div>

        <!-- 页脚组件 -->
        <div style="margin-bottom:12px;text-align:left;">
          <div style="font-size:12px;color:#909399;margin-bottom:6px;">页脚组件</div>
          <div style="display:grid;grid-template-columns:1fr 1fr;gap:4px;">
            <el-button size="small" type="warning" plain @click="addComponent('footerNote')">备注</el-button>
            <el-button size="small" type="warning" plain @click="addComponent('footerInfo')">医院信息</el-button>
            <el-button size="small" type="warning" plain @click="addComponent('signLinePair')">签名对</el-button>
          </div>
        </div>

        <!-- 图片组件 -->
        <div style="margin-bottom:12px;text-align:left;">
          <div style="font-size:12px;color:#909399;margin-bottom:6px;">图片组件</div>
          <el-button size="small" @click="addComponent('image', {src:'hospitalLogo', label:'医院图标'})" style="width:100%;margin-bottom:4px;">医院图标</el-button>
          <el-button size="small" @click="addComponent('image', {src:'doctorSignature', label:'医生签名'})" style="width:100%;margin-bottom:4px;">医生签名</el-button>
          <el-button size="small" @click="addComponent('image', {src:'hospitalSeal', label:'医院印章'})" style="width:100%;margin-bottom:4px;">医院印章</el-button>
          <input type="file" ref="imageUpload" accept="image/*" style="display:none" @change="handleImageUpload" />
          <el-button size="small" type="primary" @click="$refs.imageUpload.click()" style="width:100%;">上传本地图片</el-button>
        </div>

        <!-- 操作 -->
        <div style="margin-top:auto;padding-top:12px;border-top:1px solid #ebeef5;text-align:left;">
          <el-button size="small" type="primary" @click="handleSaveAsNew" style="width:100%;margin-bottom:6px;">保存为新模板</el-button>
          <el-button size="small" type="success" @click="handleSave" :disabled="!currentTemplateId" style="width:100%;margin-bottom:6px;">保存修改当前模板</el-button>
          <el-button size="small" type="warning" @click="clearAll" style="width:100%;">清空全部</el-button>
        </div>
      </div>

      <!-- 中间：设计画布 -->
      <div style="flex:1;border:1px solid #dcdfe6;border-radius:4px;display:flex;flex-direction:column;overflow:hidden;">
        <div style="padding:8px 12px;background:#f5f7fa;border-bottom:1px solid #dcdfe6;display:flex;justify-content:space-between;align-items:center;flex-wrap:wrap;gap:8px;">
          <span style="font-weight:bold;">设计画布</span>
          <div style="display:flex;align-items:center;gap:8px;">
            <el-button size="small" @click="zoomOut" :disabled="canvasZoom <= 0.5">缩小</el-button>
            <span style="font-size:12px;min-width:50px;text-align:center;">{{ Math.round(canvasZoom * 100) }}%</span>
            <el-button size="small" @click="zoomIn" :disabled="canvasZoom >= 2">放大</el-button>
            <el-button size="small" @click="canvasZoom = 1">100%</el-button>
            <el-button size="small" type="primary" @click="handlePreview">预览PDF</el-button>
          </div>
          <div v-if="selectedComponent !== null && components[selectedComponent]?.type === 'image'" style="display:flex;align-items:center;gap:8px;border-left:1px solid #dcdfe6;padding-left:8px;">
            <span style="font-size:12px;color:#909399;">图片缩放：</span>
            <el-button size="small" @click="zoomImageIn" icon="Plus">放大</el-button>
            <el-button size="small" @click="zoomImageOut" icon="Minus">缩小</el-button>
            <span style="font-size:12px;min-width:50px;text-align:center;">{{ components[selectedComponent]?.width || 100 }}px</span>
          </div>
        </div>
        <div style="flex:1;padding:10px;overflow:auto;background:#e8e8e8;">
          <div
            class="design-canvas"
            style="width:793px;min-height:1123px;background:white;font-family:SimSun,serif;font-size:12px;"
            @click="selectedComponent = null"
          >
            <div v-if="components.length === 0" style="text-align:center;color:#c0c4cc;padding:100px 0;">
              <p style="font-size:16px;">点击左侧按钮添加组件</p>
              <p style="font-size:12px;margin-top:8px;">或使用"智能填充"自动生成模板</p>
            </div>

            <div v-for="(comp, index) in components" :key="index"
              :class="['canvas-item', {selected: selectedComponent === index}]"
              :style="getCompStyle(comp)"
              @click.stop="selectedComponent = index"
              @mousedown="startCompDrag($event, index)"
              @mouseenter="hoveredComponent = index"
              @mouseleave="hoveredComponent = -1"
              >
              <!-- 标题 -->
              <template v-if="comp.type === 'title'">
                <div style="text-align:center;font-size:20px;font-weight:bold;">
                  {{ comp.text || '报告标题' }}
                </div>
              </template>

              <!-- 副标题 -->
              <template v-else-if="comp.type === 'subtitle'">
                <div style="text-align:center;font-size:14px;color:#666;">
                  {{ comp.text || '副标题' }}
                </div>
              </template>

              <!-- 分隔线 -->
              <template v-else-if="comp.type === 'divider'">
                <hr style="border:none;border-top:1px solid #333;"/>
              </template>

              <!-- 间距 -->
              <template v-else-if="comp.type === 'space'">
                <div :style="{height: (comp.height || 20) + 'px'}"></div>
              </template>

              <!-- 字段 -->
              <template v-else-if="comp.type === 'field'">
                <span style="font-size:12px;white-space:nowrap;">
                  <span v-if="comp.showLabel" style="color:#666;">{{ comp.label }}：</span>
                  <span>【{{ comp.field }}】</span>
                </span>
              </template>

              <!-- 结果表格 -->
              <template v-else-if="comp.type === 'resultTable'">
                <table style="width:100%;border-collapse:collapse;margin:6px 0;border:none;">
                  <thead>
                    <tr>
                      <th style="border:none;padding:3px 4px;text-align:center;font-size:10px;border-bottom:1px solid #333;">序号/项目名称</th>
                      <th style="border:none;padding:3px 4px;text-align:center;font-size:10px;border-bottom:1px solid #333;">测定结果</th>
                      <th style="border:none;padding:3px 4px;text-align:center;font-size:10px;border-bottom:1px solid #333;">单位</th>
                      <th style="border:none;padding:3px 4px;text-align:center;font-size:10px;border-bottom:1px solid #333;">参考区间</th>
                      <th style="border:none;padding:3px 4px;text-align:center;font-size:10px;border-bottom:1px solid #333;">检测方法</th>
                    </tr>
                  </thead>
                  <tbody>
                    <template v-for="(row, ri) in resultRows" :key="ri">
                      <tr>
                        <td :rowspan="2" style="border:none;border-bottom:1px solid #eee;padding:2px 4px;text-align:left;font-size:9px;vertical-align:top;">{{ri+1}} {{row.name}}</td>
                        <td style="border:none;padding:1px 4px;text-align:center;font-size:9px;">{{row.result}} <span v-if="row.flag" :style="{color: row.flag==='↑'?'red':'blue'}">{{row.flag}}</span></td>
                        <td style="border:none;padding:0 4px;font-size:8px;color:#999;"></td>
                        <td :rowspan="2" style="border:none;border-bottom:1px solid #eee;padding:2px 4px;text-align:center;font-size:9px;vertical-align:top;">{{row.reference}}</td>
                        <td :rowspan="2" style="border:none;border-bottom:1px solid #eee;padding:2px 4px;text-align:center;font-size:8px;vertical-align:top;">{{row.method}}</td>
                      </tr>
                      <tr>
                        <td style="border:none;border-bottom:1px solid #eee;padding:0 4px;font-size:8px;color:#666;"></td>
                        <td style="border:none;border-bottom:1px solid #eee;padding:0 4px;font-size:8px;">{{row.unit}}</td>
                      </tr>
                    </template>
                  </tbody>
                </table>
              </template>

              <!-- 医院信息标题 -->
              <template v-else-if="comp.type === 'hospitalHeader'">
                <div style="text-align:center;font-size:20px;font-weight:bold;margin-bottom:4px;">
                  {{ comp.text || 'XX医院检验报告' }}
                </div>
              </template>

              <!-- 医院信息 -->
              <template v-else-if="comp.type === 'hospitalInfo'">
                <div style="text-align:center;font-size:10px;color:#888;margin-bottom:12px;">
                  {{ comp.text || '地址、电话等信息' }}
                </div>
              </template>

              <!-- 患者信息区块标题 -->
              <template v-else-if="comp.type === 'patientInfo'">
                <div style="font-weight:bold;font-size:13px;margin-bottom:8px;color:#333;border-bottom:1px solid #ddd;padding-bottom:4px;">
                  {{ comp.label || '患者信息' }}
                </div>
              </template>

              <!-- 签名区 -->
              <template v-else-if="comp.type === 'signatureBlock'">
                <div style="display:flex;justify-content:space-between;margin-top:20px;padding:0 20px;">
                  <div style="text-align:center;">
                    <div style="margin-bottom:16px;">采样人：________</div>
                    <div>时间：________</div>
                  </div>
                  <div style="text-align:center;">
                    <div style="margin-bottom:16px;">检验人：________</div>
                    <div>时间：________</div>
                  </div>
                  <div style="text-align:center;">
                    <div style="margin-bottom:16px;">审核人：________</div>
                    <div>时间：________</div>
                  </div>
                </div>
              </template>

              <!-- 签名横线组件 -->
              <template v-else-if="comp.type === 'signLine'">
                <div style="display:flex;flex-direction:column;align-items:center;">
                  <div :style="{width:(comp.width||100)+'px',borderBottom:'1px solid #333',marginBottom:'4px'}"></div>
                  <div style="font-size:11px;color:#666;">{{ comp.label || '签名' }}</div>
                </div>
              </template>

              <!-- 签名对（一左一右） -->
              <template v-else-if="comp.type === 'signLinePair'">
                <div style="display:flex;justify-content:space-between;padding:0 20px;">
                  <div style="display:flex;flex-direction:column;align-items:center;">
                    <div style="width:120px;border-bottom:1px solid #333;margin-bottom:2px;"></div>
                    <span style="font-size:10px;color:#666;">{{ comp.leftLabel || '检验者签名' }}</span>
                  </div>
                  <div style="display:flex;flex-direction:column;align-items:center;">
                    <div style="width:120px;border-bottom:1px solid #333;margin-bottom:2px;"></div>
                    <span style="font-size:10px;color:#666;">{{ comp.rightLabel || '审核者签名' }}</span>
                  </div>
                </div>
              </template>

              <!-- 备注组件 -->
              <template v-else-if="comp.type === 'footerNote'">
                <div style="font-size:11px;color:#333;text-align:left;padding:4px 0;">
                  {{ comp.text || '备注：本检测结果仅对所送检标本负责，如有疑问请在收到报告后3日内提出。' }}
                </div>
              </template>

              <!-- 医院信息页脚 -->
              <template v-else-if="comp.type === 'footerInfo'">
                <div style="font-size:10px;color:#666;text-align:center;padding:2px 0;">
                  {{ comp.text || '地址：XX省XX市XX路XX号  电话：XXXX-XXXXXXXX  邮编：XXXXXX' }}
                </div>
              </template>

              <!-- 图片组件 -->
              <template v-else-if="comp.type === 'image'">
                <img v-if="comp.src === 'hospitalLogo'" :src="imageStore.hospitalLogo" :style="{width:(comp.width||80)+'px', height:(comp.height||'auto')}" draggable="false" />
                <img v-else-if="comp.src === 'hospitalSeal'" :src="imageStore.hospitalSeal" :style="{width:(comp.width||80)+'px', height:(comp.height||'auto')}" draggable="false" />
                <div v-else style="display:flex;flex-direction:column;align-items:center;">
                  <img :src="uploadedImages[comp.src] || ''" :style="{width:(comp.width||100)+'px', height:(comp.height||'auto')}" draggable="false" />
                  <div v-if="comp.showLabel" style="font-size:11px;color:#999;margin-top:4px;">{{ comp.label }}</div>
                </div>
              </template>

              <!-- 删除按钮 -->
              <div v-if="hoveredComponent === index" class="delete-btn" @click.stop="deleteComponent(index)">×</div>
            </div>
          </div>
        </div>
      </div>
      </div>

    <!-- 结果数据编辑对话框 -->
    <el-dialog v-model="resultEditorVisible" title="检验结果数据" width="900px" append-to-body>
      <div style="max-height:500px;overflow:auto;">
        <table style="width:100%;border-collapse:collapse;font-size:12px;">
          <thead>
            <tr style="background:#f5f7fa;">
              <th style="border:1px solid #ddd;padding:6px;text-align:center;width:30px;">序号</th>
              <th style="border:1px solid #ddd;padding:6px;text-align:left;">项目名称</th>
              <th style="border:1px solid #ddd;padding:6px;text-align:center;width:80px;">测定结果</th>
              <th style="border:1px solid #ddd;padding:6px;text-align:center;width:50px;">标志</th>
              <th style="border:1px solid #ddd;padding:6px;text-align:center;width:80px;">单位</th>
              <th style="border:1px solid #ddd;padding:6px;text-align:center;width:100px;">参考区间</th>
              <th style="border:1px solid #ddd;padding:6px;text-align:center;width:100px;">检测方法</th>
              <th style="border:1px solid #ddd;padding:6px;text-align:center;width:50px;">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(row, ri) in resultRows" :key="ri">
              <td style="border:1px solid #ddd;padding:4px;text-align:center;">{{ri+1}}</td>
              <td style="border:1px solid #ddd;padding:2px;"><el-input v-model="row.name" size="small" /></td>
              <td style="border:1px solid #ddd;padding:2px;"><el-input v-model="row.result" size="small" /></td>
              <td style="border:1px solid #ddd;padding:2px;"><el-input v-model="row.flag" size="small" placeholder="" /></td>
              <td style="border:1px solid #ddd;padding:2px;"><el-input v-model="row.unit" size="small" /></td>
              <td style="border:1px solid #ddd;padding:2px;"><el-input v-model="row.reference" size="small" /></td>
              <td style="border:1px solid #ddd;padding:2px;"><el-input v-model="row.method" size="small" /></td>
              <td style="border:1px solid #ddd;padding:4px;text-align:center;"><el-button size="small" type="danger" @click="resultRows.splice(ri,1)" link>删</el-button></td>
            </tr>
          </tbody>
        </table>
      </div>
      <div style="margin-top:12px;text-align:center;">
        <el-button size="small" type="primary" @click="resultRows.push({name:'',result:'',unit:'',reference:'',method:'',flag:''})">添加行</el-button>
      </div>
      <template #footer>
        <el-button @click="resultEditorVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 预览对话框 -->
    <el-dialog v-model="previewVisible" title="模板预览" width="800px" append-to-body>
      <div v-if="previewLoading" style="text-align:center;padding:40px;">
        <el-icon class="is-loading" size="32"><Loading /></el-icon>
        <p>正在生成PDF...</p>
      </div>
      <iframe v-else-if="previewUrl" :src="previewUrl" style="width:100%;height:600px;border:none;" />
      <div v-else style="text-align:center;padding:40px;color:#f56c6c;">预览生成失败</div>
    </el-dialog>
  </el-dialog>
</template>

<script setup>
import { ref, watch, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Loading, MagicStick } from '@element-plus/icons-vue'
import axios from 'axios'
import { onMounted } from 'vue'

const props = defineProps({
  modelValue: Boolean,
  templateId: { type: Number, default: null }
})
const emit = defineEmits(['update:modelValue', 'saved'])

const templateList = ref([])
const selectedComponent = ref(null)
const hoveredComponent = ref(-1)
const components = ref([])
const previewVisible = ref(false)
const previewUrl = ref('')
const previewLoading = ref(false)
const currentTemplateId = ref(null)
const selectedTemplateIds = ref([])
const imageUpload = ref(null)
const uploadedImages = ref({})
const draggingIndex = ref(-1)
const dragOffset = ref({ x: 0, y: 0 })
const customField = ref({ label: '', field: '' })
const canvasZoom = ref(1)
const resultEditorVisible = ref(false)
const templateSbDjid = ref(null)
const templateBgbh = ref('')
const templateBgmc = ref('')
const deviceList = ref([])
const resultRows = ref([
  { name: '*白细胞数目(WBC)', result: '7.76', unit: 'x10^9/L', reference: '3.5---9.5', method: '荧光染色法', flag: '' },
  { name: '中性粒细胞百分比(Neu%)', result: '82.80', unit: '%', reference: '40---75', method: '荧光染色法', flag: '↑' },
  { name: '淋巴细胞百分比(Lym%)', result: '9.60', unit: '%', reference: '20---50', method: '荧光染色法', flag: '↓' },
  { name: '*红细胞数目(RBC)', result: '2.58', unit: 'x10^12/L', reference: '3.80---5.80', method: '鞘流阻抗法', flag: '↓' },
  { name: '*血红蛋白浓度(HGB)', result: '90', unit: 'g/L', reference: '115---175', method: '比色法', flag: '↓' },
  { name: '*血小板数目(PLT)', result: '115', unit: 'x10^9/L', reference: '125---350', method: '鞘流阻抗法', flag: '↓' },
])

const imageStore = {
  hospitalLogo: '/images/hospital_red_cross.png',
  hospitalSeal: '/images/seal.png',
  doctorSignature: '/images/doctor.png'
}

onMounted(async () => {
  try {
    const { data } = await axios.get('/api/report-template/list')
    templateList.value = data || []
    if (templateList.value.length > 0 && !currentTemplateId.value) {
      const firstTemplateId = templateList.value[0].template_id
      currentTemplateId.value = firstTemplateId
      const { data: template } = await axios.get(`/api/report-template/${firstTemplateId}`)
      templateSbDjid.value = template.sb_djid || null
      if (template.html_content) {
        try {
          const parsed = JSON.parse(template.html_content)
          if (Array.isArray(parsed.components)) {
            components.value = parsed.components
          }
        } catch (e) {}
      }
    }
  } catch (e) {
    templateList.value = []
  }
  try {
    const { data } = await axios.get('/api/basic/instrument/list')
    deviceList.value = Array.isArray(data) ? data : (data.data || [])
  } catch (e) {
    deviceList.value = []
  }
})

const loadTemplateList = async () => {
  try {
    const { data } = await axios.get('/api/report-template/list')
    templateList.value = data || []
  } catch (e) {
    templateList.value = []
  }
}

const onTemplateChange = (templateId) => {
  loadTemplate(templateId)
}

const zoomIn = () => {
  if (canvasZoom.value < 2) {
    canvasZoom.value = Math.min(2, canvasZoom.value + 0.1)
  }
}

const zoomOut = () => {
  if (canvasZoom.value > 0.5) {
    canvasZoom.value = Math.max(0.5, canvasZoom.value - 0.1)
  }
}

const zoomImageIn = () => {
  if (selectedComponent.value !== null) {
    const comp = components.value[selectedComponent.value]
    if (comp && comp.type === 'image') {
      comp.width = (comp.width || 100) + 20
    }
  }
}

const zoomImageOut = () => {
  if (selectedComponent.value !== null) {
    const comp = components.value[selectedComponent.value]
    if (comp && comp.type === 'image' && comp.width > 30) {
      comp.width = (comp.width || 100) - 20
    }
  }
}

const addCustomField = () => {
  if (!customField.value.label || !customField.value.field) {
    ElMessage.warning('请输入显示名称和字段名')
    return
  }
  addComponent('field', {
    label: customField.value.label,
    field: customField.value.field
  })
  customField.value = { label: '', field: '' }
}

const handleImageUpload = (event) => {
  const file = event.target.files[0]
  if (!file) return

  if (!file.type.startsWith('image/')) {
    ElMessage.error('请选择图片文件')
    return
  }

  const reader = new FileReader()
  reader.onload = (e) => {
    const base64 = e.target.result
    const imageId = 'img_' + Date.now()
    uploadedImages.value[imageId] = base64

    const comp = {
      type: 'image',
      src: imageId,
      label: file.name,
      width: 100,
      align: 'center',
      showLabel: true,
      marginTop: 8,
      marginBottom: 0
    }
    components.value.push(comp)
    selectedComponent.value = components.value.length - 1
  }
  reader.readAsDataURL(file)

  event.target.value = ''
}

const startDrag = (event, index) => {
  event.preventDefault()
  event.stopPropagation()

  selectedComponent.value = index

  const comp = components.value[index]
  if (!comp.dragOffset) {
    comp.dragOffset = { x: 0, y: 0 }
  }

  const startMouseX = event.clientX
  const startMouseY = event.clientY
  const startOffsetX = comp.dragOffset.x
  const startOffsetY = comp.dragOffset.y

  const onMouseMove = (moveEvent) => {
    draggingIndex.value = index
    const dx = moveEvent.clientX - startMouseX
    const dy = moveEvent.clientY - startMouseY
    comp.dragOffset = {
      x: startOffsetX + dx,
      y: startOffsetY + dy
    }
  }

  const onMouseUp = () => {
    draggingIndex.value = -1
    document.removeEventListener('mousemove', onMouseMove)
    document.removeEventListener('mouseup', onMouseUp)
  }

  document.addEventListener('mousemove', onMouseMove)
  document.addEventListener('mouseup', onMouseUp)
}

const onCompMouseDown = (event, index) => {
  event.preventDefault()
  event.stopPropagation()

  selectedComponent.value = index

  const comp = components.value[index]
  if (!comp.dragOffset) {
    comp.dragOffset = { x: 0, y: 0 }
  }

  const startX = event.clientX
  const startY = event.clientY
  const startOffsetX = comp.dragOffset.x
  const startOffsetY = comp.dragOffset.y

  const onMouseMove = (e) => {
    const dx = e.clientX - startX
    const dy = e.clientY - startY
    comp.dragOffset = { x: startOffsetX + dx, y: startOffsetY + dy }
  }

  const onMouseUp = () => {
    document.removeEventListener('mousemove', onMouseMove)
    document.removeEventListener('mouseup', onMouseUp)
  }

  document.addEventListener('mousemove', onMouseMove)
  document.addEventListener('mouseup', onMouseUp)
}

const onFieldMouseDown = (event, compIndex, fieldIndex) => {
  event.preventDefault()
  event.stopPropagation()

  const comp = components.value[compIndex]
  const field = comp.fields[fieldIndex]

  if (!field.dragOffset) {
    field.dragOffset = { x: 0, y: 0 }
  }

  const startX = event.clientX
  const startY = event.clientY
  const startOffsetX = field.dragOffset.x
  const startOffsetY = field.dragOffset.y

  const onMouseMove = (e) => {
    const dx = e.clientX - startX
    const dy = e.clientY - startY
    field.dragOffset = { x: startOffsetX + dx, y: startOffsetY + dy }
  }

  const onMouseUp = () => {
    document.removeEventListener('mousemove', onMouseMove)
    document.removeEventListener('mouseup', onMouseUp)
  }

  document.addEventListener('mousemove', onMouseMove)
  document.addEventListener('mouseup', onMouseUp)
}

const canvasRows = computed(() => {
  const rows = []
  const sorted = [...components.value].sort((a, b) => (a.top || 0) - (b.top || 0))
  let i = 0
  while (i < sorted.length) {
    const comp = sorted[i]
    const origIndex = components.value.indexOf(comp)
    if (comp.type === 'field') {
      const currentTop = comp.top
      const fields = []
      while (i < sorted.length && sorted[i].type === 'field' && sorted[i].top === currentTop) {
        fields.push({ comp: sorted[i], index: components.value.indexOf(sorted[i]) })
        i++
      }
      if (fields.length > 1) {
        rows.push({ type: 'fieldRow', fields })
      } else {
        rows.push({ type: 'single', comp, index: origIndex })
      }
    } else {
      rows.push({ type: 'single', comp, index: origIndex })
      i++
    }
  }
  return rows
})

const getCompStyle = (comp) => {
  const style = {}
  if (comp.top !== undefined && comp.top !== null) {
    style.position = 'absolute'
    style.top = comp.top + 'px'
    style.left = (comp.left || 0) + 'px'
    if (comp.width) {
      style.width = comp.width + 'px'
    }
  }
  if (comp.height && comp.type === 'space') {
    style.height = comp.height + 'px'
  }
  if (comp.dragOffset && (comp.dragOffset.x !== 0 || comp.dragOffset.y !== 0)) {
    style.transform = `translate(${comp.dragOffset.x}px, ${comp.dragOffset.y}px)`
  }
  return style
}

const startCompDrag = (event, index) => {
  event.preventDefault()
  event.stopPropagation()

  selectedComponent.value = index

  const comp = components.value[index]
  if (!comp.dragOffset) {
    comp.dragOffset = { x: 0, y: 0 }
  }

  const startX = event.clientX
  const startY = event.clientY
  const startOffsetX = comp.dragOffset.x
  const startOffsetY = comp.dragOffset.y

  draggingIndex.value = index

  const onMouseMove = (e) => {
    const dx = e.clientX - startX
    const dy = e.clientY - startY
    comp.dragOffset = { x: startOffsetX + dx, y: startOffsetY + dy }
  }

  const onMouseUp = () => {
    if (comp.dragOffset) {
      comp.top = Math.round(((comp.top || 0) + comp.dragOffset.y) / 5) * 5
      comp.left = Math.round(((comp.left || 0) + comp.dragOffset.x) / 5) * 5
      comp.dragOffset = { x: 0, y: 0 }
    }
    draggingIndex.value = -1
    document.removeEventListener('mousemove', onMouseMove)
    document.removeEventListener('mouseup', onMouseUp)
  }

  document.addEventListener('mousemove', onMouseMove)
  document.addEventListener('mouseup', onMouseUp)
}

const startImageResize = (event, index, direction) => {
  event.preventDefault()
  event.stopPropagation()

  if (direction === 'move') {
    startCompDrag(event, index)
    return
  }

  selectedComponent.value = index
  const comp = components.value[index]

  const startX = event.clientX
  const startY = event.clientY
  const startWidth = comp.width || 80
  const startHeight = comp.height || 'auto'

  const onMouseMove = (e) => {
    const dx = e.clientX - startX
    const dy = e.clientY - startY

    if (direction.includes('e')) {
      comp.width = Math.max(20, startWidth + dx)
    }
    if (direction.includes('w')) {
      comp.width = Math.max(20, startWidth - dx)
    }
    if (direction.includes('s') && comp.height !== 'auto') {
      comp.height = Math.max(20, parseInt(startHeight) + dy)
    }
    if (direction.includes('n') && comp.height !== 'auto') {
      comp.height = Math.max(20, parseInt(startHeight) - dy)
    }
  }

  const onMouseUp = () => {
    document.removeEventListener('mousemove', onMouseMove)
    document.removeEventListener('mouseup', onMouseUp)
  }

  document.addEventListener('mousemove', onMouseMove)
  document.addEventListener('mouseup', onMouseUp)
}

const getTransformBoxStyle = (comp) => {
  const style = {
    position: 'absolute',
    top: '0',
    left: '0',
    right: '0',
    bottom: '0',
    pointerEvents: 'none'
  }
  return style
}

const startTransformMove = (event, index) => {
  startCompDrag(event, index)
}

const startTransform = (event, index, direction) => {
  event.preventDefault()
  event.stopPropagation()

  selectedComponent.value = index
  const comp = components.value[index]

  const el = event.currentTarget.closest('.canvas-item')
  const rect = el.getBoundingClientRect()

  const startX = event.clientX
  const startY = event.clientY

  const startWidth = rect.width
  const startHeight = rect.height
  const startLeft = rect.left
  const startTop = rect.top

  const onMouseMove = (e) => {
    const dx = e.clientX - startX
    const dy = e.clientY - startY

    if (direction === 'e') {
      comp.transformWidth = Math.max(30, startWidth + dx)
    } else if (direction === 'w') {
      comp.transformWidth = Math.max(30, startWidth - dx)
      comp.transformOffsetX = dx
    } else if (direction === 's') {
      comp.transformHeight = Math.max(20, startHeight + dy)
    } else if (direction === 'n') {
      comp.transformHeight = Math.max(20, startHeight - dy)
      comp.transformOffsetY = dy
    } else if (direction === 'se') {
      comp.transformWidth = Math.max(30, startWidth + dx)
      comp.transformHeight = Math.max(20, startHeight + dy)
    } else if (direction === 'sw') {
      comp.transformWidth = Math.max(30, startWidth - dx)
      comp.transformHeight = Math.max(20, startHeight + dy)
      comp.transformOffsetX = dx
    } else if (direction === 'ne') {
      comp.transformWidth = Math.max(30, startWidth + dx)
      comp.transformHeight = Math.max(20, startHeight - dy)
      comp.transformOffsetY = dy
    } else if (direction === 'nw') {
      comp.transformWidth = Math.max(30, startWidth - dx)
      comp.transformHeight = Math.max(20, startHeight - dy)
      comp.transformOffsetX = dx
      comp.transformOffsetY = dy
    }
  }

  const onMouseUp = () => {
    document.removeEventListener('mousemove', onMouseMove)
    document.removeEventListener('mouseup', onMouseUp)
  }

  document.addEventListener('mousemove', onMouseMove)
  document.addEventListener('mouseup', onMouseUp)
}

const autoFill = () => {
  components.value = [
    { type: 'hospitalHeader', text: '苍溪县中医医院检验报告', fontSize: 20, align: 'center', top: 10, left: 0, width: 793 },
    { type: 'hospitalInfo', text: '地址：苍溪县陵江镇解放路西段49号  电话：0839-5220000', fontSize: 9, align: 'center', top: 45, left: 0, width: 793 },
    { type: 'divider', top: 62, left: 0, width: 793 },
    { type: 'field', label: '患者姓名', field: 'patientName', showLabel: true, top: 72, left: 0, width: 120 },
    { type: 'field', label: '性别', field: 'gender', showLabel: true, top: 72, left: 160, width: 60 },
    { type: 'field', label: '年龄', field: 'age', showLabel: true, top: 72, left: 250, width: 60 },
    { type: 'field', label: '病历号', field: 'medicalRecordNo', showLabel: true, top: 72, left: 340, width: 140 },
    { type: 'field', label: '病区', field: 'ward', showLabel: true, top: 72, left: 520, width: 120 },
    { type: 'field', label: '床号', field: 'bedNo', showLabel: true, top: 72, left: 670, width: 70 },
    { type: 'field', label: '科室', field: 'department', showLabel: true, top: 100, left: 0, width: 120 },
    { type: 'field', label: '样本条码', field: 'sampleBarcode', showLabel: true, top: 100, left: 160, width: 160 },
    { type: 'field', label: '检测号', field: 'testNo', showLabel: true, top: 100, left: 360, width: 130 },
    { type: 'field', label: '标本', field: 'specimen', showLabel: true, top: 100, left: 520, width: 80 },
    { type: 'field', label: '采集时间', field: 'collectTime', showLabel: true, top: 100, left: 630, width: 140 },
    { type: 'field', label: '临床诊断', field: 'diagnosis', showLabel: true, top: 128, left: 0, width: 300 },
    { type: 'field', label: '检验项目', field: 'testItems', showLabel: true, top: 128, left: 340, width: 200 },
    { type: 'field', label: '申请医生', field: 'requestDoctor', showLabel: true, top: 128, left: 580, width: 100 },
    { type: 'divider', top: 155, left: 0, width: 793 },
    { type: 'resultTable', title: '', rows: 10, top: 165, left: 0, width: 793 },
    { type: 'divider', top: 870, left: 0, width: 793 },
    { type: 'signLinePair', leftLabel: '检验者签名', rightLabel: '审核者签名', top: 875, left: 0, width: 793 },
    { type: 'footerNote', text: '备注：本检测结果仅对所送检标本负责，如有疑问请在收到报告后3日内提出。', top: 905, left: 0, width: 793 },
    { type: 'field', label: '申请医生', field: 'requestDoctor', showLabel: true, top: 925, left: 0, width: 100 },
    { type: 'field', label: '采样时间', field: 'sampleTime', showLabel: true, top: 925, left: 130, width: 160 },
    { type: 'field', label: '接收时间', field: 'receiveTime', showLabel: true, top: 925, left: 330, width: 160 },
    { type: 'field', label: '报告时间', field: 'reportTime', showLabel: true, top: 925, left: 530, width: 160 },
    { type: 'field', label: '检验者', field: 'inspector', showLabel: true, top: 945, left: 0, width: 100 },
    { type: 'field', label: '审核者', field: 'reviewer', showLabel: true, top: 945, left: 200, width: 100 },
    { type: 'footerInfo', text: '地址：苍溪县陵江镇解放路西段49号  电话：0839-5220000  邮编：628400', top: 965, left: 0, width: 793 },
  ]
  selectedComponent.value = null
}

const addComponent = (type, options = {}) => {
  const lastTop = components.value.length > 0
    ? Math.max(...components.value.map(c => c.top || 0)) + 40
    : 20
  let comp = { type, top: lastTop, left: 0, marginTop: 0, marginBottom: 0 }

  switch (type) {
    case 'title':
    case 'hospitalHeader':
      comp = { ...comp, text: '检验报告单', fontSize: 20, align: 'center', left: 0, width: 793 }
      break
    case 'subtitle':
      comp = { ...comp, text: 'Laboratory Report', fontSize: 14, align: 'center', left: 0, width: 793 }
      break
    case 'hospitalInfo':
      comp = { ...comp, text: '地址：XX市XX路XX号  电话：XXXX-XXXXXXXX', fontSize: 10, align: 'center', left: 0, width: 793 }
      break
    case 'patientInfo':
      comp = { ...comp, label: '患者信息', left: 0, width: 793 }
      break
    case 'divider':
      comp = { ...comp, style: 'solid', margin: '12px 0', left: 0, width: 793 }
      break
    case 'space':
      comp = { ...comp, height: 20, left: 0, width: 793 }
      break
    case 'field':
      comp = {
        ...comp,
        label: options.label || '字段',
        field: options.field || 'fieldName',
        showLabel: true,
        labelWidth: 80,
        fontSize: 12,
        left: 0,
        width: 200
      }
      break
    case 'fieldRow':
      comp = { ...comp, fields: options.fields || [], left: 0, width: 793 }
      break
    case 'resultTable':
      comp = { ...comp, title: '检验结果', rows: 5, left: 0, width: 793 }
      break
    case 'signatureBlock':
      comp = { ...comp, left: 0, width: 793 }
      break
    case 'doctorSignLine':
      comp = { ...comp, width: 120, left: 0 }
      break
    case 'signLine':
      comp = { ...comp, width: 100, label: '签名', left: 0 }
      break
    case 'signLinePair':
      comp = { ...comp, leftLabel: '检验者签名', rightLabel: '审核者签名', left: 0, width: 793 }
      break
    case 'footerNote':
      comp = { ...comp, text: '备注：本检测结果仅对所送检标本负责，如有疑问请在收到报告后3日内提出。', left: 0, width: 793 }
      break
    case 'footerInfo':
      comp = { ...comp, text: '地址：XX省XX市XX路XX号  电话：XXXX-XXXXXXXX  邮编：XXXXXX', left: 0, width: 793 }
      break
    case 'image':
      comp = {
        ...comp,
        src: options.src || 'hospitalLogo',
        label: options.label || '图片',
        width: options.src === 'doctorSignature' ? 120 : 80,
        align: 'center',
        showLabel: false,
        left: 0
      }
      break
  }

  components.value.push(comp)
  selectedComponent.value = components.value.length - 1
}

const deleteComponent = (index) => {
  components.value.splice(index, 1)
  selectedComponent.value = null
}

const clearAll = () => {
  components.value = []
  selectedComponent.value = null
}

const deleteTemplate = async () => {
  if (!currentTemplateId.value) return
  try {
    await axios.delete(`/api/report-template/${currentTemplateId.value}`)
    ElMessage.success('删除成功')
    currentTemplateId.value = null
    components.value = []
    await loadTemplateList()
  } catch (e) {
    ElMessage.error('删除失败')
  }
}

const toggleTemplateSelect = (templateId) => {
  const index = selectedTemplateIds.value.indexOf(templateId)
  if (index > -1) {
    selectedTemplateIds.value.splice(index, 1)
  } else {
    selectedTemplateIds.value.push(templateId)
  }
}

const batchDeleteTemplates = async () => {
  if (selectedTemplateIds.value.length === 0) return
  try {
    await Promise.all(selectedTemplateIds.value.map(id =>
      axios.delete(`/api/report-template/${id}`)
    ))
    ElMessage.success('批量删除成功')
    selectedTemplateIds.value = []
    currentTemplateId.value = null
    components.value = []
    await loadTemplateList()
  } catch (e) {
    ElMessage.error('批量删除失败')
  }
}

const loadTemplate = async (templateId) => {
  if (!templateId) return
  try {
    const { data: template } = await axios.get(`/api/report-template/${templateId}`)
    currentTemplateId.value = templateId
    templateSbDjid.value = template.sb_djid || null
    templateBgbh.value = template.bgbh || ''
    templateBgmc.value = template.bgmc || ''

    if (template.html_content) {
      try {
        const parsed = JSON.parse(template.html_content)
        if (Array.isArray(parsed.components)) {
          components.value = parsed.components
          return
        }
      } catch (e) {}
    }

    components.value = []
  } catch (e) {
    components.value = []
  }
}

const generateHtml = (data = null) => {
  let html = `<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8"/>
</head>
<body>`

  const comps = [...components.value].sort((a, b) => (a.top || 0) - (b.top || 0))

  let i = 0
  let resultTablePassed = false
  while (i < comps.length) {
    const comp = comps[i]

    if (resultTablePassed && comp.type !== 'resultTable') {
      if (!html.includes('class="footer-push"')) {
        html += `<div class="footer-push">`
      }
    }

    switch (comp.type) {
      case 'title':
      case 'hospitalHeader':
        html += `<h1 style="text-align:center;font-size:${comp.fontSize || 16}pt;margin:4pt 0 2pt 0;">${comp.text || '报告标题'}</h1>`
        i++
        break
      case 'subtitle':
        html += `<p style="text-align:center;font-size:${comp.fontSize || 8}pt;margin:2pt 0;color:#666;">${comp.text || ''}</p>`
        i++
        break
      case 'hospitalInfo':
        html += `<p style="text-align:center;font-size:${comp.fontSize || 8}pt;margin:2pt 0;color:#888;">${comp.text || ''}</p>`
        i++
        break
      case 'divider':
        html += `<hr/>`
        i++
        break
      case 'space': {
        const h = Math.round((comp.height || 20) * 0.75)
        html += `<div style="height:${h}pt;"></div>`
        i++
        break
      }
      case 'patientInfo':
        html += `<p style="font-weight:bold;font-size:10pt;margin:4pt 0 2pt 0;">${comp.label || '患者信息'}</p>`
        i++
        break
      case 'field': {
        const currentTop = comp.top
        const rowFields = []
        while (i < comps.length && comps[i].type === 'field' && Math.abs((comps[i].top || 0) - (currentTop || 0)) <= 5) {
          rowFields.push(comps[i])
          i++
        }
        html += `<p style="font-size:9pt;margin:2pt 0;">`
        rowFields.forEach(f => {
          const lbl = f.showLabel ? `${f.label}：` : ''
          html += `<span style="font-size:9pt;">${lbl}{{${f.field}}}\u3000\u3000</span>`
        })
        html += `</p>`
        break
      }
      case 'resultTable': {
        const title = comp.title || '检验结果'
        if (title) {
          html += `<p style="font-weight:bold;font-size:10pt;margin:4pt 0 2pt 0;">${title}</p>`
        }
        html += `<table class="result-table" style="width:100%;border:none;" cellspacing="0"><thead><tr><th style="border:none;border-bottom:0.5pt solid #333;padding:3pt 4pt;font-size:9pt;">序号/项目名称</th><th style="border:none;border-bottom:0.5pt solid #333;padding:3pt 4pt;font-size:9pt;">测定结果</th><th style="border:none;border-bottom:0.5pt solid #333;padding:3pt 4pt;font-size:9pt;">单位</th><th style="border:none;border-bottom:0.5pt solid #333;padding:3pt 4pt;font-size:9pt;">参考区间</th><th style="border:none;border-bottom:0.5pt solid #333;padding:3pt 4pt;font-size:9pt;">检测方法</th></tr></thead><tbody>`
        const rows = (data && data.resultRows && data.resultRows.length > 0) ? data.resultRows : []
        const rowCount = rows.length > 0 ? rows.length : (comp.rows || 5)
        for (let r = 0; r < rowCount; r++) {
          const row = rows[r] || {}
          const idx = r + 1
          const name = row.name || '&nbsp;'
          const result = row.result || '&nbsp;'
          const flag = row.flag || ''
          const unit = row.unit || '&nbsp;'
          const reference = row.reference || '&nbsp;'
          const method = row.method || '&nbsp;'
          const resultDisplay = flag ? `${result} ${flag}` : result
          html += `<tr><td rowspan="2" style="border:none;padding:3pt 4pt;font-size:9pt;vertical-align:top;">${idx} ${name}</td><td style="border:none;padding:2pt 4pt;font-size:9pt;">${resultDisplay}</td><td style="border:none;padding:0 4pt;font-size:8pt;">&nbsp;</td><td rowspan="2" style="border:none;padding:3pt 4pt;font-size:9pt;vertical-align:top;">${reference}</td><td rowspan="2" style="border:none;padding:3pt 4pt;font-size:9pt;vertical-align:top;">${method}</td></tr><tr><td style="border:none;padding:0 4pt;font-size:8pt;color:#666;">&nbsp;</td><td style="border:none;padding:0 4pt;">${unit}</td></tr>`
        }
        html += '</tbody></table>'
        resultTablePassed = true
        i++
        break
      }
      case 'signLine':
        html += `<span style="font-size:9pt;">${comp.label || '医生签名'}_________________________</span>`
        i++
        break
      case 'signLinePair':
        html += `<table style="width:100%;margin:0;" cellspacing="0"><tr><td style="border:none;padding:0;text-align:left;">_________________________</td><td style="border:none;padding:0;text-align:right;">_________________________</td></tr><tr><td style="border:none;padding:0;text-align:left;font-size:7pt;">${comp.leftLabel || '检验者签名'}</td><td style="border:none;padding:0;text-align:right;font-size:7pt;">${comp.rightLabel || '审核者签名'}</td></tr></table>`
        i++
        break
      case 'signatureBlock':
        html += `<table style="width:100%;" cellspacing="0"><tr>`
        html += `<td style="font-size:9pt;padding:3pt 4pt;border:none;">采样人：{{sampler}}</td>`
        html += `<td style="font-size:9pt;padding:3pt 4pt;border:none;">检验人：{{inspector}}</td>`
        html += `<td style="font-size:9pt;padding:3pt 4pt;border:none;">审核人：{{reviewer}}</td>`
        html += `</tr><tr>`
        html += `<td style="font-size:9pt;padding:3pt 4pt;border:none;">时间：{{sampleTime}}</td>`
        html += `<td style="font-size:9pt;padding:3pt 4pt;border:none;">时间：{{inspectTime}}</td>`
        html += `<td style="font-size:9pt;padding:3pt 4pt;border:none;">时间：{{reviewTime}}</td>`
        html += `</tr></table>`
        i++
        break
      case 'image': {
        let imgSrc = ''
        if (comp.src === 'hospitalLogo') {
          imgSrc = imageStore.hospitalLogo
        } else if (comp.src === 'doctorSignature') {
          imgSrc = imageStore.doctorSignature
        } else if (comp.src === 'hospitalSeal') {
          imgSrc = imageStore.hospitalSeal
        } else {
          imgSrc = uploadedImages.value[comp.src] || ''
        }
        if (imgSrc) {
          html += `<div style="text-align:${comp.align || 'center'};"><img src="${imgSrc}" style="width:${comp.width || 80}px;height:${comp.height || 'auto'};"/></div>`
        }
        i++
        break
      }
      case 'footerNote':
        html += `<p style="font-size:8pt;margin:2pt 0;color:#333;">${comp.text || '备注：本检测结果仅对所送检标本负责，如有疑问请在收到报告后3日内提出。'}</p>`
        i++
        break
      case 'footerInfo':
        html += `<p style="font-size:8pt;margin:2pt 0;color:#666;text-align:center;">${comp.text || ''}</p>`
        html += `<p style="font-size:1pt;margin:0;color:#fff;">&nbsp;</p>`
        i++
        break
      default:
        i++
        break
    }
  }

  if (html.includes('class="footer-push"')) {
    html += `</div>`
  }

  html += `</body></html>`
  return html
}

const handlePreview = async () => {
  previewLoading.value = true
  previewVisible.value = true
  previewUrl.value = ''

  try {
    const previewData = {
      patientName: '张三', gender: '男', age: '45岁', bedNo: '003',
      department: '内科', specimenType: '血液', inpatientNo: '2024001234',
      specimen: '血清', medicalRecordNo: 'BL20240001', ward: '内科一病区',
      sampleBarcode: 'BC2024011500123', testNo: 'JC20240115001',
      diagnosis: '常规体检', requestDoctor: '王医生',
      collectTime: '2024-01-15 08:30', receiveTime: '2024-01-15 09:00',
      testTime: '2024-01-15 10:00', testItems: '血常规',
      sampler: '刘护士', sampleTime: '2024-01-15 08:30',
      inspector: '李医生', reviewer: '赵医生',
      reportTime: '2024-01-15 10:30', reviewTime: '2024-01-15 10:30'
    }
    const html = generateHtml({ resultRows: resultRows.value, ...previewData })
    console.log('signLinePair HTML check:', html.includes('____________\u3000') ? 'NEW CODE OK' : 'OLD CODE - needs refresh')
    console.log('发送的data:', previewData)
    const { data: res } = await axios.post('/api/report/render', {
      html: html,
      data: previewData
    })
    console.log('后端返回:', res)
    if (res.success && res.pdf) {
      previewUrl.value = `data:application/pdf;base64,${res.pdf}`
    } else {
      ElMessage.error(res.message || '预览失败')
    }
  } catch (e) {
    console.error('预览错误:', e)
    ElMessage.error('预览失败: ' + (e.message || e))
  } finally {
    previewLoading.value = false
  }
}

const handleTemplateInstrumentChange = async (val) => {
  if (!currentTemplateId.value) return
  try {
    await axios.post('/api/report-template/save', {
      templateId: currentTemplateId.value,
      sbDjid: val
    })
    ElMessage.success(val ? '已关联仪器' : '已设为通用模板')
  } catch (e) {
    ElMessage.error('保存失败')
  }
}

const handleSave = async () => {
  if (!currentTemplateId.value) return
  try {
    const htmlContent = JSON.stringify({ components: components.value })
    const currentTemplate = templateList.value.find(t => t.template_id === currentTemplateId.value)
    const defaultName = currentTemplate ? currentTemplate.template_name : ''

    ElMessageBox.prompt('修改模板名称', '保存修改', {
      confirmButtonText: '保存',
      cancelButtonText: '取消',
      inputValue: defaultName
    }).then(async ({ value }) => {
      const { data: res } = await axios.post('/api/report-template/save', {
        templateId: currentTemplateId.value,
        templateName: value,
        htmlContent: htmlContent,
        sbDjid: templateSbDjid.value,
        bgbh: templateBgbh.value,
        bgmc: templateBgmc.value
      })
      if (res.success) {
        ElMessage.success('保存成功')
        await loadTemplateList()
        emit('saved')
      } else {
        ElMessage.error(res.message || '保存失败')
      }
    }).catch(() => {})
  } catch (e) {
    ElMessage.error('保存失败')
  }
}

const handleSaveAsNew = async () => {
  try {
    const htmlContent = JSON.stringify({ components: components.value })

    ElMessageBox.prompt('请输入新模板名称', '保存为新模板', {
      confirmButtonText: '保存',
      cancelButtonText: '取消',
      inputValue: '新模板_' + new Date().toLocaleDateString()
    }).then(async ({ value }) => {
      const { data: res } = await axios.post('/api/report-template/save', {
        templateId: null,
        templateName: value,
        htmlContent: htmlContent,
        sbDjid: templateSbDjid.value,
        bgbh: templateBgbh.value,
        bgmc: templateBgmc.value
      })
      if (res.success) {
        ElMessage.success('保存成功')
        currentTemplateId.value = res.templateId
        await loadTemplateList()
        emit('saved')
      } else {
        ElMessage.error(res.message || '保存失败')
      }
    }).catch(() => {})
  } catch (e) {
    ElMessage.error('保存失败')
  }
}

watch(() => props.modelValue, (val) => {
  if (val && props.templateId) {
    loadTemplate(props.templateId)
  }
})

watch(() => props.templateId, (val) => {
  if (val && props.modelValue) {
    loadTemplate(val)
  }
})
</script>

<style scoped>
.canvas-item {
  position: absolute;
  cursor: move;
  transition: background 0.2s;
}

.canvas-item:hover {
  background: #f0f9ff;
}

.canvas-item.selected {
  background: #e6f7ff;
  outline: 2px solid #1890ff;
}

.template-item:hover {
  background: #f5f7fa;
}

.canvas-item.dragging {
  cursor: move;
  opacity: 0.8;
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
}

.design-canvas {
  position: relative;
  width: 210mm;
  min-height: 297mm;
}

.delete-btn {
  position: absolute;
  top: -8px;
  right: -8px;
  width: 20px;
  height: 20px;
  background: #ff4d4f;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font-size: 14px;
  line-height: 1;
  z-index: 10;
}

.delete-btn:hover {
  background: #ff7875;
}

.resize-handle {
  position: absolute;
  background: #1890ff;
  border: 1px solid #fff;
  z-index: 10;
}

.resize-handle-n,
.resize-handle-s {
  height: 6px;
  width: 30px;
  left: 50%;
  transform: translateX(-50%);
  cursor: ns-resize;
}

.resize-handle-n { top: -3px; }
.resize-handle-s { bottom: -3px; }

.resize-handle-e,
.resize-handle-w {
  width: 6px;
  height: 30px;
  top: 50%;
  transform: translateY(-50%);
  cursor: ew-resize;
}

.resize-handle-e { right: -3px; }
.resize-handle-w { left: -3px; }

.resize-handle-ne,
.resize-handle-nw,
.resize-handle-se,
.resize-handle-sw {
  width: 10px;
  height: 10px;
  border-radius: 50%;
}

.resize-handle-ne { top: -3px; right: -3px; cursor: nesw-resize; }
.resize-handle-nw { top: -3px; left: -3px; cursor: nesw-resize; }
.resize-handle-se { bottom: -3px; right: -3px; cursor: nesw-resize; }
.resize-handle-sw { bottom: -3px; left: -3px; cursor: nesw-resize; }

.transform-box {
  position: absolute;
  top: -4px;
  left: -4px;
  right: -4px;
  bottom: -4px;
  border: 2px solid #1890ff;
  pointer-events: all;
  z-index: 100;
}

.transform-handle {
  position: absolute;
  background: #fff;
  border: 2px solid #1890ff;
  z-index: 101;
}

.transform-handle-n,
.transform-handle-s {
  height: 8px;
  width: 20px;
  left: 50%;
  transform: translateX(-50%);
  cursor: ns-resize;
}

.transform-handle-n { top: -5px; }
.transform-handle-s { bottom: -5px; }

.transform-handle-e,
.transform-handle-w {
  width: 8px;
  height: 20px;
  top: 50%;
  transform: translateY(-50%);
  cursor: ew-resize;
}

.transform-handle-e { right: -5px; }
.transform-handle-w { left: -5px; }

.transform-handle-ne,
.transform-handle-nw,
.transform-handle-se,
.transform-handle-sw {
  width: 12px;
  height: 12px;
  border-radius: 2px;
}

.transform-handle-ne { top: -6px; right: -6px; cursor: nesw-resize; }
.transform-handle-nw { top: -6px; left: -6px; cursor: nesw-resize; }
.transform-handle-se { bottom: -6px; right: -6px; cursor: nesw-resize; }
.transform-handle-sw { bottom: -6px; left: -6px; cursor: nesw-resize; }
</style>
