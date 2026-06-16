<template>
  <div class="sample-management" @click="handleClickOutside">
    <!-- 注意：工具栏已移至 MainFrame，这里不再显示工具栏 -->
    <!-- 主体三栏布局：左=病人信息+组合项目列表，中=结果，右=病人/样本列表 -->
    <div class="sm-main">
      <!-- 左：样本录入 -->
      <section class="sm-panel sm-left">
        <header class="sm-panel-header">样本录入</header>
        <div class="sm-panel-body">
          <div class="patient-form">
            <!-- 按照旧系统截图顺序：样本号、实验情况、样本条码、病人类型、检索方式、门诊号/住院号、姓名、性别、年龄... -->
            <div class="form-row">
              <label>样本号</label>
              <input type="text" v-model="patientForm.sampleNo" />
              <label>实验情况</label>
              <select v-model="patientForm.experimentStatus">
                <option value=""> </option>
                <option value="普通">普通</option>
                <option value="紧急">紧急</option>
                <option value="危急">危急</option>
              </select>
            </div>
            <div class="form-row">
              <label>样本条码</label>
              <input type="text" v-model="patientForm.barcode" />
              <label>病人类型</label>
              <select v-model="patientForm.type" @change="handlePatientTypeChange">
                <option value=""> </option>
                <option v-for="pt in patientTypeOptions" :key="pt.bm" :value="pt.bmsm">{{ pt.bmsm }}</option>
              </select>
            </div>
            <div class="form-row">
              <label>检索方式</label>
              <select v-model="patientForm.searchMethod">
                <option value="收据编号">收据编号</option>
                <option value="门诊号">门诊号</option>
                <option value="住院号">住院号</option>
                <option value="体检号">体检号</option>
              </select>
              <label>{{ patientForm.searchMethod === '住院号' ? '住院号' : patientForm.searchMethod === '门诊号' ? '门诊号' : patientForm.searchMethod === '体检号' ? '体检号' : '收据编号' }}</label>
              <input type="text" v-model="patientForm.patientId" />
            </div>
            <div class="form-row">
              <label>姓名</label>
              <input type="text" v-model="patientForm.name" />
              <label>性别</label>
              <select v-model="patientForm.sex">
                <option value=""> </option>
                <option value="M">男</option>
                <option value="F">女</option>
              </select>
            </div>
            <div class="form-row">
              <label>年龄</label>
              <input type="text" v-model="patientForm.age" />
              <label>年龄单位</label>
              <select v-model="patientForm.ageUnit">
                <option value="Y">岁</option>
                <option value="M">月</option>
                <option value="D">天</option>
              </select>
            </div>
            <div class="form-row">
              <label>科室</label>
              <input type="text" v-model="patientForm.dept" />
              <label>床号</label>
              <input type="text" v-model="patientForm.bedNo" />
            </div>
            <div class="form-row">
              <label>样本类型</label>
              <el-select v-model="patientForm.sampleType" filterable clearable placeholder=" " size="small" style="flex:1">
                <el-option v-for="st in sampleTypeOptions" :key="st.bm" :label="st.bmsm" :value="st.bmsm" />
              </el-select>
              <label>样本形态</label>
              <input type="text" v-model="patientForm.sampleMorphology" />
            </div>
            <div class="form-row">
              <label>申请时间</label>
              <input type="datetime-local" v-model="patientForm.applicationTime" />
              <label>申请医生</label>
              <input type="text" v-model="patientForm.doctor" />
            </div>
            <div class="form-row">
              <label>采样时间</label>
              <input type="datetime-local" v-model="patientForm.samplingTime" />
              <label>核收时间</label>
              <input type="datetime-local" v-model="patientForm.verificationTime" />
            </div>
            <div class="form-row">
              <label>录入人</label>
              <input type="text" v-model="patientForm.entryPerson" />
              <label>检验时间</label>
              <input type="datetime-local" v-model="patientForm.inspectionTime" />
            </div>
            <div class="form-row">
              <label>检验医师</label>
              <input type="text" v-model="patientForm.inspectingPhysician" />
              <label>审核时间</label>
              <input type="datetime-local" v-model="patientForm.reviewTime" />
            </div>
            <div class="form-row">
              <label>审核医师</label>
              <input type="text" v-model="patientForm.reviewingPhysician" />
              <label>临床诊断</label>
              <input type="text" v-model="patientForm.diagnosis" />
            </div>
            <div class="form-row">
              <label>备注</label>
              <textarea v-model="patientForm.remarks" class="full-textarea"></textarea>
            </div>
            <div class="form-row">
              <label>补充备注</label>
              <input type="text" v-model="patientForm.additionalRemarks" />
              <label>打印时间</label>
              <input type="datetime-local" v-model="patientForm.printTime" />
            </div>
            <div class="form-row">
              <label>打印次数</label>
              <input type="text" v-model="patientForm.printCount" />
              <label>病区</label>
              <input type="text" v-model="patientForm.ward" />
            </div>
            <div class="form-row">
              <label>体检单位</label>
              <input type="text" v-model="patientForm.physicalExamUnit" />
              <label>证件号码</label>
              <input type="text" v-model="patientForm.idNumber" />
            </div>
            <div class="form-row">
              <label>联系方式</label>
              <input type="text" class="full" v-model="patientForm.contactInfo" />
            </div>
            <!-- 辅助功能勾选区：封存禁用 -->
            <div class="auxiliary-panel">
              <div class="auxiliary-title">辅助功能</div>
              <div class="auxiliary-options">
                <label><input type="checkbox" v-model="auxSettings.autoSave" /> 自动保存</label>
                <label><input type="checkbox" v-model="auxSettings.itemInherit" /> 项目继承</label>
                <label><input type="checkbox" v-model="auxSettings.autoIncrement" /> 自动增加</label>
                <label><input type="checkbox" v-model="auxSettings.autoShift" /> 自动后移</label>
                <label><input type="checkbox" v-model="auxSettings.printThenAudit" /> 打印即审核</label>
                <label><input type="checkbox" v-model="auxSettings.autoFetchResult" /> 自动提取结果</label>
                <label><input type="checkbox" v-model="auxSettings.directPrint" /> 直接打印</label>
              </div>
            </div>
          </div>
          <div class="sub-panel-header">组合项目列表</div>
          <div class="sub-panel-body">
            <div class="combo-search">
              <input
                type="text"
                v-model="comboKeyword"
                placeholder="按名称/拼音检索组合项目"
                @keyup.enter="loadCombos"
              />
              <button type="button" @click="loadCombos">查找</button>
            </div>
            <table class="simple-table">
              <thead>
                <tr>
                  <th>组合项目名</th>
                  <th>拼音码</th>
                </tr>
              </thead>
              <tbody>
                <tr
                  v-for="combo in comboItems"
                  :key="combo.zhid"
                  @click="selectCombo(combo)"
                  :class="{ 'row-active': combo.zhid === selectedComboId }"
                >
                  <td>{{ combo.zhmc }}</td>
                  <td>{{ combo.pym }}</td>
                </tr>
                <tr v-if="comboItems.length === 0">
                  <td colspan="2" class="placeholder-cell">当前无组合项目，请调整检索条件或稍后重试</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </section>

      <!-- 中：结果信息 -->
       <section class="sm-panel sm-center">
         <header class="sm-panel-header">结果信息</header>
         <div class="sm-panel-body result-scroll-body">
           <table class="simple-table">
            <thead>
              <tr>
                <th>NO.</th>
                <th>项目名称</th>
                <th>结果</th>
                <th>标记</th>
                <th>参考值</th>
                <th>单位</th>
              </tr>
            </thead>
            <tbody>
              <tr
                v-for="(item, index) in resultItems"
                :key="item.id || index"
                :class="{ 'row-active': index === activeResultIndex }"
                @click="activeResultIndex = index"
              >
                <td>{{ index + 1 }}</td>
                <td>{{ item.name }}</td>
                <td>
                  <input
                    type="text"
                    v-model="item.result"
                    class="cell-input"
                    :disabled="!isResultEditable"
                    :class="{ 'cell-input-disabled': !isResultEditable }"
                  />
                </td>
                <td><span :style="{ color: item.flag === '↑' ? '#e74c3c' : item.flag === '↓' ? '#3498db' : '' }">{{ item.flag }}</span></td>
                <td>{{ item.refRange }}</td>
                <td>{{ item.unit }}</td>
              </tr>
              <tr v-if="resultItems.length === 0">
                <td colspan="6" class="placeholder-cell">
                  后续将在此处加载检验项目及结果明细（支持键盘录入与仪器导入）
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </section>

      <!-- 右：样本信息浏览 + 六个预留Tab -->
      <section class="sm-panel sm-right">
        <header class="sm-panel-header">样本信息浏览</header>
        <div class="sm-panel-body">
          <!-- 顶部七个Tab：仅“浏览”可用，其余按照旧系统名称置灰占位 -->
          <div class="right-tabs">
            <button class="right-tab" :class="{ active: activeRightTab === 'browse' }" @click="activeRightTab = 'browse'">浏览</button>
            <button class="right-tab" :class="{ active: activeRightTab === 'batch' }" @click="activeRightTab = 'batch'">批量处理</button>
            <button class="right-tab" :class="{ active: activeRightTab === 'history' }" @click="activeRightTab = 'history'">历史回顾</button>
            <button class="right-tab" :class="{ active: activeRightTab === 'progress' }" @click="activeRightTab = 'progress'">数据处理进度</button>
            <button class="right-tab" :class="{ active: activeRightTab === 'sample' }" @click="activeRightTab = 'sample'">标本处理</button>
            <button class="right-tab" :class="{ active: activeRightTab === 'application' }" @click="activeRightTab = 'application'">申请信息</button>
            <button class="right-tab" :class="{ active: activeRightTab === 'warning' }" @click="activeRightTab = 'warning'">警示信息</button>
          </div>
          <div class="right-tab-content">
            <!-- 浏览Tab内容 -->
            <div v-show="activeRightTab === 'browse'">
              <div class="right-filter">
              <div class="filter-row">
                <label>样本分类</label>
                <select v-model="patientFilter.category" @change="loadPatientsFromApi" style="flex:1;">
                  <option value="所有">所有</option>
                  <option value="未审核">未审核</option>
                  <option value="已出结果">已出结果</option>
                  <option value="已出报告">已出报告</option>
                  <option value="已检验">已检验</option>
                  <option value="已打印">已打印</option>
                  <option value="紧急标本">紧急标本</option>
                  <option value="危急报告">危急报告</option>
                  <option value="自审未过">自审未过</option>
                  <option value="自审未出">自审未出</option>
                  <option value="自审用章">自审用章</option>
                  <option value="自审需审">自审需审</option>
                  <option value="未收款">未收款</option>
                  <option value="已缴费">已缴费</option>
                  <option value="自费">自费</option>
                  <option v-for="pt in patientTypeOptions" :key="'f'+pt.bm" :value="pt.bmsm">{{ pt.bmsm }}</option>
                  <option value="外院病人">外院病人</option>
                  <option value="出院病人">出院病人</option>
                </select>
              </div>
              <div class="filter-row">
                <label>日期</label>
                 <input type="date" v-model="patientFilter.date" @change="loadPatientsFromApi(); $event.target.blur()" style="flex:1;" />
                <label>样本号</label>
                <input type="text" v-model="patientFilter.sampleNo" placeholder="样本号" @keyup.enter="loadPatientsFromApi" style="flex:1;" />
                <el-button type="primary" size="small" @click="loadPatientsFromApi">搜索</el-button>
              </div>
            </div>
            <div class="right-table">
              <table class="simple-table">
                <thead>
                  <tr>
                    <th width="40">序号</th>
                    <th width="40">签急</th>
                    <th width="50">状态</th>
                    <th width="60">姓名</th>
                    <th width="100">条码号</th>
                    <th width="80">病历号</th>
                    <th width="70">样本类型</th>
                    <th width="80">送检日期</th>
                    <th width="70">检验医生</th>
                    <th width="70">审核医生</th>
                  </tr>
                </thead>
                <tbody>
                  <tr
                    v-for="(row, index) in patientList"
                    :key="row.id || index"
                    @click="selectPatient(row)"
                    @contextmenu.prevent="handleRightClick($event)"
                    :class="row.id === activePatientId ? 'row-active' : ''"
                  >
                    <td>{{ index + 1 }}</td>
                    <td class="cell-urgent">{{ urgencyText(row.urgency) }}</td>
                    <td class="cell-status">
                      <span
                        class="status-badge"
                        :class="{
                          'status-entry': statusText(row.status) === '录入',
                          'status-inspect': statusText(row.status) === '已检验',
                          'status-audit': statusText(row.status) === '已审核',
                          'status-print': statusText(row.status) === '已打印'
                        }"
                      >
                        {{ statusText(row.status) }}
                      </span>
                    </td>
                    <td>{{ row.name }}</td>
                    <td>{{ row.barcode }}</td>
                    <td>{{ row.patientId }}</td>
                    <td>{{ row.sampleType }}</td>
                    <td>{{ row.submitDate || '' }}</td>
                    <td>{{ row.inspectDoctor }}</td>
                    <td>{{ row.auditDoctor }}</td>
                  </tr>
                  <tr v-if="patientList.length === 0">
                    <td colspan="10" class="placeholder-cell">
                      当前条件下暂无样本记录
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
            </div>
            
            <!-- 批量处理Tab内容 -->
            <div v-show="activeRightTab === 'batch'" class="batch-tab-content">
              <div class="batch-toolbar">
                <el-button type="primary" size="small" @click="selectAllAudited" :loading="batchLoading">全选已审核</el-button>
                <el-button size="small" @click="clearSelection" :loading="batchLoading">清空选择</el-button>
                <span class="batch-info">已选择 {{ selectedPatientIds.length }} 项</span>
              </div>
              <div class="batch-actions">
                <el-button type="warning" size="small" @click="handleBatchAudit" :loading="batchLoading" :disabled="selectedPatientIds.length === 0">批量审核</el-button>
                <el-button type="success" size="small" @click="handleBatchPrint" :loading="batchLoading" :disabled="selectedPatientIds.length === 0">批量打印</el-button>
                <el-button type="info" size="small" @click="handleBatchUnaudit" :loading="batchLoading" :disabled="selectedPatientIds.length === 0">取消审核</el-button>
                <el-button type="danger" size="small" @click="handleBatchInvalidate" :loading="batchLoading" :disabled="selectedPatientIds.length === 0">批量作废</el-button>
              </div>
              <div class="batch-table">
                <table class="simple-table">
                  <thead>
                    <tr>
                      <th width="40">选</th>
                      <th>样本号</th>
                      <th>姓名</th>
                      <th>状态</th>
                      <th>科室</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="row in patientList" :key="row.id" @click="previewPatient(row)" :class="selectedPatientIds.includes(row.id) ? 'row-selected' : ''">
                      <td><input type="checkbox" :checked="selectedPatientIds.includes(row.id)" @click.stop="togglePatientSelection(row)" /></td>
                      <td>{{ row.sampleNo }}</td>
                      <td>{{ row.name }}</td>
                      <td>{{ statusText(row.status) }}</td>
                      <td>{{ row.ward }}</td>
                    </tr>
                    <tr v-if="patientList.length === 0">
                      <td colspan="5" class="placeholder-cell">当前条件下暂无样本记录</td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>

            <!-- 数据处理进度Tab内容 -->
            <div v-show="activeRightTab === 'progress'" class="progress-tab-content">
              <div class="progress-toolbar">
                <label>日期：</label>
                <input type="date" v-model="progressStatsDate" @change="loadProgressStats" />
                <el-button type="primary" size="small" @click="loadProgressStats">刷新</el-button>
              </div>
              <div class="progress-chart">
                <div class="progress-summary">
                  <div class="progress-item">
                    <span class="progress-label">录入：</span>
                    <span class="progress-value">{{ progressStats.statusEntry || 0 }}</span>
                  </div>
                  <div class="progress-item">
                    <span class="progress-label">已检验：</span>
                    <span class="progress-value">{{ progressStats.statusInspected || 0 }}</span>
                  </div>
                  <div class="progress-item">
                    <span class="progress-label">已审核：</span>
                    <span class="progress-value">{{ progressStats.statusAudited || 0 }}</span>
                  </div>
                  <div class="progress-item">
                    <span class="progress-label">已打印：</span>
                    <span class="progress-value">{{ progressStats.statusPrinted || 0 }}</span>
                  </div>
                  <div class="progress-item">
                    <span class="progress-label">已作废：</span>
                    <span class="progress-value">{{ progressStats.statusInvalid || 0 }}</span>
                  </div>
                  <div class="progress-item total">
                    <span class="progress-label">合计：</span>
                    <span class="progress-value">{{ progressStats.total || 0 }}</span>
                  </div>
                </div>
              </div>
            </div>

            <!-- 标本处理Tab内容 -->
            <div v-show="activeRightTab === 'sample'" class="sample-tab-content">
              <!-- 拒收录入区 -->
              <div class="rejection-form">
                <div class="rejection-form-row">
                  <label>条码号：</label>
                  <input v-model="rejectForm.testBarcode" @keydown.enter.prevent="lookupBarcode" placeholder="扫码/输入条码" style="width:150px" ref="barcodeInput" />
                  <el-button type="primary" size="small" @click="lookupBarcode">查询</el-button>
                </div>
                <div v-if="rejectForm.patientName" class="rejection-form-info">
                  <div class="rejection-form-row">
                    <label>姓名：</label><span>{{ rejectForm.patientName }}</span>
                    <label style="margin-left:12px">性别：</label><span>{{ rejectForm.sex }}</span>
                    <label style="margin-left:12px">年龄：</label><span>{{ rejectForm.age }}</span>
                  </div>
                  <div class="rejection-form-row">
                    <label>科室：</label><span>{{ rejectForm.department }}</span>
                    <label style="margin-left:12px">床号：</label><span>{{ rejectForm.bedNumber }}</span>
                    <label style="margin-left:12px">标本：</label><span>{{ rejectForm.sampleType }}</span>
                  </div>
                </div>
                <div class="rejection-form-row">
                  <label>不合格原因：</label>
                  <select v-model="rejectForm.errorReason" style="width:150px">
                    <option value="">请选择</option>
                    <option v-for="t in errorTypes" :key="t.code" :value="t.name">{{ t.name }}</option>
                  </select>
                </div>
                <div class="rejection-form-row">
                  <label>处理措施：</label>
                  <select v-model="rejectForm.handlingMeasures" style="width:120px">
                    <option value="">请选择</option>
                    <option v-for="m in handlingMeasuresList" :key="m.code" :value="m.name">{{ m.name }}</option>
                  </select>
                  <input v-if="rejectForm.handlingMeasures === '其他'" v-model="rejectForm.handlingMeasuresOther" placeholder="其他措施说明" style="width:150px;margin-left:8px" />
                </div>
                <div class="rejection-form-row">
                  <label>备注：</label>
                  <input v-model="rejectForm.notes" style="width:250px" />
                </div>
                <div class="rejection-form-row">
                  <label>临床接收人：</label>
                  <input v-model="rejectForm.recipient" style="width:120px" />
                  <el-button type="danger" size="small" @click="submitRejection" :disabled="!rejectForm.testBarcode || !rejectForm.errorReason" style="margin-left:12px">确认拒收</el-button>
                  <el-button size="small" @click="resetRejectForm">清空</el-button>
                </div>
              </div>
              <!-- 拒收记录查询区 -->
              <div class="rejection-records">
                <div class="rejection-records-header">
                  <label>查询日期：</label>
                  <input type="date" v-model="rejectQueryBeginDate" style="width:130px" />
                  <span> 至 </span>
                  <input type="date" v-model="rejectQueryEndDate" style="width:130px" />
                  <el-button type="primary" size="small" @click="loadRejectRecords">查询</el-button>
                  <el-button size="small" @click="exportRejectRecords">导出</el-button>
                </div>
                <table class="simple-table" style="font-size:12px">
                  <thead>
                    <tr>
                      <th>时间</th>
                      <th>条码</th>
                      <th>姓名</th>
                      <th>不合格原因</th>
                      <th>处理措施</th>
                      <th>操作员</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="row in rejectRecords" :key="row.id">
                      <td>{{ (row.create_time || '').substring(0, 16) }}</td>
                      <td>{{ row.test_barcode }}</td>
                      <td>{{ row.patient_name }}</td>
                      <td>{{ row.error_reason }}</td>
                      <td>{{ row.handling_measures }}</td>
                      <td>{{ row.operator_name }}</td>
                    </tr>
                    <tr v-if="rejectRecords.length === 0">
                      <td colspan="6" class="placeholder-cell">暂无拒收记录</td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>

            <!-- 历史回顾Tab内容 -->
            <div v-show="activeRightTab === 'history'" class="history-tab-content">
              <div class="history-toolbar">
                <label>日期：</label>
                <input type="date" v-model="historyDate" @change="loadHistoryRecords" />
                <el-button type="primary" size="small" @click="loadHistoryRecords">刷新</el-button>
              </div>
              <div class="history-table">
                <table class="simple-table">
                  <thead>
                    <tr>
                      <th>样本号</th>
                      <th>姓名</th>
                      <th>原状态</th>
                      <th>新状态</th>
                      <th>操作人</th>
                      <th>操作时间</th>
                      <th>备注</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="row in historyRecords" :key="row.id">
                      <td>{{ row.sampleNo }}</td>
                      <td>{{ row.name }}</td>
                      <td>{{ statusText(row.oldStatus) }}</td>
                      <td>{{ statusText(row.newStatus) }}</td>
                      <td>{{ row.operator }}</td>
                      <td>{{ row.operateTime }}</td>
                      <td>{{ row.remarks }}</td>
                    </tr>
                    <tr v-if="historyRecords.length === 0">
                      <td colspan="7" class="placeholder-cell">当前无历史记录</td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>

            <!-- 申请信息Tab内容 -->
            <div v-show="activeRightTab === 'application'" class="application-tab-content">
              <div class="application-toolbar">
                <label>日期：</label>
                <input type="date" v-model="applicationDate" @change="loadApplicationInfo" />
                <el-button type="primary" size="small" @click="loadApplicationInfo">刷新</el-button>
              </div>
              <div class="application-table">
                <table class="simple-table">
                  <thead>
                    <tr>
                      <th>申请单号</th>
                      <th>患者姓名</th>
                      <th>科室</th>
                      <th>医生</th>
                      <th>申请日期</th>
                      <th>状态</th>
                      <th>操作</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="row in applicationList" :key="row.id">
                      <td>{{ row.applyNo }}</td>
                      <td>{{ row.patientName }}</td>
                      <td>{{ row.dept }}</td>
                      <td>{{ row.doctor }}</td>
                      <td>{{ row.applyDate }}</td>
                      <td>{{ row.status }}</td>
                      <td>
                        <el-button type="primary" size="small" @click="viewApplication(row)">查看</el-button>
                      </td>
                    </tr>
                    <tr v-if="applicationList.length === 0">
                      <td colspan="7" class="placeholder-cell">当前无申请信息</td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>

            <!-- 警示信息Tab内容 -->
            <div v-show="activeRightTab === 'warning'" class="warning-tab-content">
              <div class="warning-toolbar">
                <label>日期：</label>
                <input type="date" v-model="warningDate" @change="loadWarningInfo" />
                <el-button type="primary" size="small" @click="loadWarningInfo">刷新</el-button>
                <el-button type="danger" size="small" @click="clearAllWarnings">清除全部</el-button>
              </div>
              <div class="warning-table">
                <table class="simple-table">
                  <thead>
                    <tr>
                      <th>样本号</th>
                      <th>患者姓名</th>
                      <th>警示类型</th>
                      <th>警示内容</th>
                      <th>时间</th>
                      <th>操作</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="row in warningList" :key="row.id" :class="getWarningClass(row.type)">
                      <td>{{ row.sampleNo }}</td>
                      <td>{{ row.patientName }}</td>
                      <td>{{ row.type }}</td>
                      <td>{{ row.message }}</td>
                      <td>{{ row.createTime }}</td>
                      <td>
                        <el-button type="primary" size="small" @click="handleWarning(row)">处理</el-button>
                      </td>
                    </tr>
                    <tr v-if="warningList.length === 0">
                      <td colspan="6" class="placeholder-cell">当前无警示信息</td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        </div>
      </section>
    </div>

    <!-- 查找对话框 -->
    <el-dialog
      v-model="showSearchDialog"
      title="查找样本"
      width="800px"
      :close-on-click-modal="false"
    >
      <div class="search-dialog">
        <div class="search-form">
          <div class="search-row">
            <label>实验号/样本号：</label>
            <el-input v-model="searchForm.sampleNo" placeholder="支持模糊查询" clearable />
            <label>姓名：</label>
            <el-input v-model="searchForm.name" placeholder="支持模糊查询" clearable />
          </div>
          <div class="search-row">
            <label>条码号：</label>
            <el-input v-model="searchForm.barcode" placeholder="支持模糊查询" clearable />
            <label>病人号：</label>
            <el-input v-model="searchForm.patientId" placeholder="支持模糊查询" clearable />
          </div>
          <div class="search-row">
            <label>日期：</label>
            <el-date-picker
              v-model="searchForm.date"
              type="date"
              placeholder="选择日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
            />
            <div style="flex: 1"></div>
            <el-button type="primary" @click="executeSearch">查找</el-button>
            <el-button @click="showSearchDialog = false">关闭</el-button>
          </div>
        </div>
        <div class="search-results">
          <table class="simple-table">
            <thead>
              <tr>
                <th>序号</th>
                <th>样本号</th>
                <th>姓名</th>
                <th>性别</th>
                <th>年龄</th>
                <th>状态</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(row, index) in searchResults" :key="row.id || index">
                <td>{{ index + 1 }}</td>
                <td>{{ row.sampleNo }}</td>
                <td>{{ row.name }}</td>
                <td>{{ row.sex }}</td>
                <td>{{ row.age }}</td>
                <td>
                  <span v-if="row.status === 0" class="status-badge status-entry">登记</span>
                  <span v-else-if="row.status === 1" class="status-badge status-inspect">未审核</span>
                  <span v-else-if="row.status === 2" class="status-badge status-audit">已审核</span>
                  <span v-else-if="row.status === 3" class="status-badge status-print">已打印</span>
                </td>
                <td>
                  <el-button type="primary" size="small" @click="selectSearchResult(row)">选择</el-button>
                </td>
              </tr>
              <tr v-if="searchResults.length === 0">
                <td colspan="7" class="placeholder-cell">暂无数据，请点击"查找"按钮查询</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </el-dialog>

    <!-- 右键菜单 -->
    <div v-if="showContextMenu" class="context-menu" :style="{ top: contextMenuY + 'px', left: contextMenuX + 'px' }">
      <div class="context-menu-item" @click="handleContextNew">新增样本</div>
      <div class="context-menu-item" @click="handleContextSave">保存样本</div>
      <div class="context-menu-divider"></div>
      <div class="context-menu-item" @click="handleContextRefresh">刷新列表</div>
      <div class="context-menu-item" @click="handleContextFind">查找样本</div>
      <div class="context-menu-divider"></div>
      <div class="context-menu-item" @click="handleContextInspect">检验样本</div>
      <div class="context-menu-item" @click="handleContextAudit">审核样本</div>
      <div class="context-menu-item" @click="handleContextPrint">打印报告</div>
      <div class="context-menu-item" @click="handleContextPreview">报告预览</div>
      <div class="context-menu-divider"></div>
      <div class="context-menu-item" @click="handleContextInvalidate">报告作废</div>
      <div class="context-menu-item" @click="handleContextUnaudit">取消审核</div>
      <div class="context-menu-item" @click="handleContextEditTime">修改审核时间</div>
      <div class="context-menu-divider"></div>
      <div class="context-menu-item" @click="showContextMenu = false">取消</div>
    </div>

    <EditTimeDialog v-model:visible="editTimeVisible" :sample="patientForm" @confirm="handleEditTimeConfirm" />
  </div>
</template>

<script setup>
import { reactive, ref, onMounted, onUnmounted, inject, watch, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { fetchPatients, saveSample, inspectSample, auditSample, unauditSample, printSample, searchSamples, getNextSampleNo, fetchCombos, fetchComboItems, fetchResults, fetchReportHtml, batchAudit, batchPrint, batchInvalidate, batchUnaudit, getProgressStats, getSampleIssues, handleSampleIssue, getHistoryRecords, getApplicationInfo, getWarningInfo, clearWarnings, getErrorTypes, getHandlingMeasures, handleSpecimenError, reportIncorrect, getRejectRecords, extractFromInstrument, getExtractStatus, fetchDropdownOptions } from '@/api/sample'
import { openBatchPrintDialog } from '../utils/eventBus'
import { useInstrumentStore } from '../utils/instrumentStore'
import { fetchActiveFlags } from '../api/highlowFlag'
import { getStatusText } from '../utils/sampleStatus'
import EditTimeDialog from '../components/dialogs/EditTimeDialog.vue'

const router = useRouter()

// 注入工具栏处理器注册接口
const registerToolbarHandler = inject('registerToolbarHandler', null)
const unregisterToolbarHandler = inject('unregisterToolbarHandler', null)

// 样本录入表单（按照旧系统字段顺序）
const patientForm = reactive({
  brxx_id: null, // 当前编辑 brxx_id（新增时必须为空，避免误走“更新保存”）
  sampleNo: '', // 样本号 (syh) - 必填
  experimentStatus: '', // 实验情况 (syqk)
  barcode: '', // 样本条码 (brxx_tmh) - 住院病人时必填
  type: '', // 病人类型 (brlb)：门诊病人/住院病人/体检人员/其他病人/科研人员
  searchMethod: '收据编号', // 检索方式
  patientId: '', // 门诊号/住院号/体检号等 (brbh)
  name: '', // 姓名 (brxm) - 必填
  sex: '', // 性别 (brxb)
  age: '', // 年龄 (brnl)
  ageUnit: 'Y', // 年龄单位 (nllx)
  dept: '', // 科室 (ksdm)
  bedNo: '', // 床号 (brch)
  sampleType: '', // 样本类型 (bbzl)
  sampleMorphology: '', // 样本形态
  applicationTime: '', // 申请时间
  doctor: '', // 申请医生/送检医生 (jyys)
  samplingTime: '', // 采样时间
  verificationTime: '', // 核收时间
  entryPerson: '', // 录入人
  inspectionTime: '', // 检验时间
  inspectingPhysician: '', // 检验医师
  reviewTime: '', // 审核时间
  reviewingPhysician: '', // 审核医师 (shys)
  diagnosis: '', // 临床诊断 (lczd)
  remarks: '', // 备注 (bz)
  additionalRemarks: '', // 补充备注
  printTime: '', // 打印时间
  printCount: '', // 打印次数 (dycs)
  ward: '', // 病区
  physicalExamUnit: '', // 体检单位 (tjdw)
  idNumber: '', // 证件号码 (zjhm)
  contactInfo: '' // 联系方式
})

// 组合项目列表（从后端加载）
const comboItems = ref([])
const comboKeyword = ref('')
const selectedComboId = ref(null)

// 检验结果明细
const resultItems = ref([])
const activeResultIndex = ref(-1)

const flagConfig = ref({ high: '↑', low: '↓', alarmHigh: '↑↑', alarmLow: '↓↓' })

const patientTypeOptions = ref([])
const sampleTypeOptions = ref([])

const loadDropdownOptions = async () => {
  try {
    const { data } = await fetchDropdownOptions()
    if (data.patientTypes) patientTypeOptions.value = data.patientTypes
    if (data.sampleTypes) sampleTypeOptions.value = data.sampleTypes
  } catch (e) {
    console.warn('加载下拉选项失败', e)
  }
}

const loadFlagConfig = async () => {
  try {
    const { data } = await fetchActiveFlags()
    if (data) flagConfig.value = data
  } catch (e) {}
}

const calcFlag = (result, refRange, criticalLow, criticalHigh) => {
  if (!result || isNaN(parseFloat(result))) return ''
  const val = parseFloat(result)
  const cfg = flagConfig.value
  if (criticalHigh && !isNaN(parseFloat(criticalHigh)) && val > parseFloat(criticalHigh)) return cfg.alarmHigh || '↑↑'
  if (criticalLow && !isNaN(parseFloat(criticalLow)) && val < parseFloat(criticalLow)) return cfg.alarmLow || '↓↓'
  if (refRange) {
    const match = String(refRange).match(/([\d.]+)\s*[-–—]\s*([\d.]+)/)
    if (match) {
      const low = parseFloat(match[1])
      const high = parseFloat(match[2])
      if (!isNaN(low) && !isNaN(high)) {
        if (val < low) return cfg.low || '↓'
        if (val > high) return cfg.high || '↑'
      }
    }
  }
  return ''
}

// 当前绑定的仪器（从 MainFrame 选择后写入 localStorage）
const currentDevice = ref(null)
const instrumentStore = useInstrumentStore()

// 右侧"患者信息浏览"过滤条件
const patientFilter = reactive({
  category: '所有',
  date: new Date().toISOString().slice(0, 10),
  sampleNo: ''
})

const patientList = ref([])
const activePatientId = ref(null)
const activeBrxxId = ref(null) // 当前编辑样本对应的 brxx_id（保存后由后端返回）
const currentSampleStatus = ref(null)
const isNewMode = ref(false) // 新增模式，阻止自动选中
const hasEverSelected = ref(false) // 用户是否手动选过样本，刷新时不自动选中

const isResultEditable = computed(() => {
  const s = currentSampleStatus.value
  if (s === null || s === undefined) return true
  return s === 0 || s === 1 || s === 4
})

const canAudit = computed(() => {
  const s = currentSampleStatus.value
  return s === 1 || s === 4
})

// 右侧Tab状态管理
const activeRightTab = ref('browse') // browse: 浏览, batch: 批量处理, history: 历史回顾, progress: 数据处理进度, sample: 标本处理

// 批量处理相关状态
const selectedPatientIds = ref([]) // 批量处理选中的样本ID列表
const batchLoading = ref(false)

// 辅助功能设置
const auxSettings = reactive({
  autoSave: false,          // 自动保存
  itemInherit: false,       // 项目继承
  autoIncrement: false,     // 自动增加
  autoShift: false,         // 自动后移
  printThenAudit: false,    // 打印即审核
  autoFetchResult: false,   // 自动提取结果
  directPrint: false        // 直接打印
})

// ==================== 撤销/重做 历史栈管理 ====================
const MAX_HISTORY = 50

// 状态快照结构
const createSnapshot = () => ({
  patientForm: JSON.parse(JSON.stringify(patientForm)),
  resultItems: JSON.parse(JSON.stringify(resultItems.value)),
  selectedComboId: selectedComboId.value,
  timestamp: Date.now()
})

// 恢复快照
const restoreSnapshot = (snapshot) => {
  if (!snapshot) return
  Object.assign(patientForm, snapshot.patientForm)
  resultItems.value = snapshot.resultItems
  selectedComboId.value = snapshot.selectedComboId
}

const historyStack = ref([])
const redoStack = ref([])
const lastSavedState = ref(null) // 用于脏检测

// 保存快照（带防抖）
let saveSnapshotTimer = null
const saveSnapshot = (immediate = false) => {
  if (saveSnapshotTimer) {
    clearTimeout(saveSnapshotTimer)
    saveSnapshotTimer = null
  }
  const doSave = () => {
    // 新变化清除重做栈
    redoStack.value = []
    const snapshot = createSnapshot()
    historyStack.value.push(snapshot)
    // 限制栈大小
    if (historyStack.value.length > MAX_HISTORY) {
      historyStack.value.shift()
    }
  }
  if (immediate) {
    doSave()
  } else {
    saveSnapshotTimer = setTimeout(doSave, 500)
  }
}

// 撤销
const undo = () => {
  if (historyStack.value.length === 0) {
    ElMessage.info('没有可撤销的操作')
    return
  }
  // 保存当前状态到重做栈
  const currentSnapshot = createSnapshot()
  redoStack.value.push(currentSnapshot)
  // 恢复上一个状态
  const prevSnapshot = historyStack.value.pop()
  restoreSnapshot(prevSnapshot)
  ElMessage.info('已撤销')
}

// 重做
const redo = () => {
  if (redoStack.value.length === 0) {
    ElMessage.info('没有可重做的操作')
    return
  }
  // 保存当前状态到历史栈
  const currentSnapshot = createSnapshot()
  historyStack.value.push(currentSnapshot)
  // 恢复重做状态
  const nextSnapshot = redoStack.value.pop()
  restoreSnapshot(nextSnapshot)
  ElMessage.info('已重做')
}

// 脏状态检测
const isDirty = computed(() => {
  if (!lastSavedState.value) return false
  const current = createSnapshot()
  return JSON.stringify(current.patientForm) !== JSON.stringify(lastSavedState.value.patientForm) ||
         JSON.stringify(current.resultItems) !== JSON.stringify(lastSavedState.value.resultItems)
})

// 刷新相关状态
const lastRefreshTime = ref(null)
const isRefreshing = ref(false)

// 数据处理进度相关
const progressStatsDate = ref(new Date().toISOString().slice(0, 10))
const progressStats = reactive({
  statusEntry: 0,
  statusInspected: 0,
  statusAudited: 0,
  statusPrinted: 0,
  statusInvalid: 0,
  total: 0
})

const loadProgressStats = async () => {
  try {
    const res = await getProgressStats(progressStatsDate.value)
    if (res.data.success) {
      Object.assign(progressStats, res.data.stats)
    }
  } catch (err) {
    console.error('加载进度统计失败:', err)
  }
}

// 标本处理相关
const sampleIssuesDate = ref(new Date().toISOString().slice(0, 10))
const sampleIssuesList = ref([])

const errorTypes = ref([])
const handlingMeasuresList = ref([])
const rejectForm = ref({
  testBarcode: '', patientName: '', sex: '', age: '', patientType: null,
  department: '', bedNumber: '', sampleType: '', itemName: '',
  errorReason: '', handlingMeasures: '', handlingMeasuresOther: '',
  notes: '', recipient: '', brxxId: null
})
const rejectRecords = ref([])
const rejectQueryBeginDate = ref(new Date().toISOString().slice(0, 10))
const rejectQueryEndDate = ref(new Date().toISOString().slice(0, 10))

const loadErrorTypes = async () => {
  try {
    const res = await getErrorTypes()
    errorTypes.value = res.data || []
  } catch (e) { console.error('加载错误类型失败:', e) }
}
const loadHandlingMeasures = async () => {
  try {
    const res = await getHandlingMeasures()
    handlingMeasuresList.value = res.data || []
  } catch (e) { console.error('加载处理措施失败:', e) }
}
const lookupBarcode = async () => {
  const barcode = rejectForm.value.testBarcode.trim()
  if (!barcode) return
  try {
    const res = await fetchPatients({ barcode })
    const list = res.data || []
    if (list.length > 0) {
      const p = list[0]
      rejectForm.value.brxxId = p.id
      rejectForm.value.patientName = p.name
      rejectForm.value.sex = p.sex === 1 ? '男' : p.sex === 2 ? '女' : ''
      rejectForm.value.age = p.age
      rejectForm.value.department = p.ward
      rejectForm.value.bedNumber = p.bed || ''
      rejectForm.value.sampleType = p.specimenType || ''
      rejectForm.value.patientType = p.type
    } else {
      ElMessage.warning('未找到该条码对应的样本')
    }
  } catch (err) {
    ElMessage.error('查询失败: ' + (err.response?.data?.message || err.message))
  }
}
const submitRejection = async () => {
  const form = rejectForm.value
  if (!form.testBarcode || !form.patientName) { ElMessage.warning('请先查询样本信息'); return }
  if (!form.errorReason) { ElMessage.warning('请选择不合格原因'); return }
  try {
    const user = JSON.parse(localStorage.getItem('user') || '{}')
    const res = await handleSpecimenError(form.brxxId, {
      errorType: (errorTypes.value.find(t => t.name === form.errorReason) || {}).code,
      errorReason: form.errorReason,
      handlingMeasures: form.handlingMeasures,
      handlingMeasuresOther: form.handlingMeasuresOther,
      notes: form.notes,
      recipient: form.recipient,
      operatorCode: user.czydm || '',
      operatorName: user.czymc || user.czyxm || '',
      testBarcode: form.testBarcode,
      patientName: form.patientName,
      sex: form.sex,
      age: form.age,
      patientType: form.patientType,
      department: form.department,
      bedNumber: form.bedNumber,
      sampleType: form.sampleType
    })
    if (res.data.success) {
      ElMessage.success('标本拒收处理成功')
      resetRejectForm()
      loadRejectRecords()
      loadPatientsFromApi()
    } else {
      ElMessage.error(res.data.message)
    }
  } catch (err) {
    ElMessage.error('处理失败: ' + (err.response?.data?.message || err.message))
  }
}
const resetRejectForm = () => {
  rejectForm.value = {
    testBarcode: '', patientName: '', sex: '', age: '', patientType: null,
    department: '', bedNumber: '', sampleType: '', itemName: '',
    errorReason: '', handlingMeasures: '', handlingMeasuresOther: '',
    notes: '', recipient: '', brxxId: null
  }
}
const loadRejectRecords = async () => {
  try {
    const res = await getRejectRecords({
      beginDate: rejectQueryBeginDate.value,
      endDate: rejectQueryEndDate.value
    })
    rejectRecords.value = res.data || []
  } catch (e) { console.error('加载拒收记录失败:', e) }
}
const exportRejectRecords = () => {
  if (rejectRecords.value.length === 0) { ElMessage.warning('无数据可导出'); return }
  const header = '时间,条码,姓名,性别,年龄,科室,不合格原因,处理措施,备注,操作员\n'
  const rows = rejectRecords.value.map(r =>
    `"${(r.create_time || '').substring(0, 16)}","${r.test_barcode || ''}","${r.patient_name || ''}","${r.sex || ''}","${r.age || ''}","${r.department || ''}","${r.error_reason || ''}","${r.handling_measures || ''}","${r.notes || ''}","${r.operator_name || ''}"`
  ).join('\n')
  const BOM = '\uFEFF'
  const blob = new Blob([BOM + header + rows], { type: 'text/csv;charset=utf-8' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = `拒收记录_${rejectQueryBeginDate.value}_${rejectQueryEndDate.value}.csv`
  a.click()
  URL.revokeObjectURL(url)
}

const loadSampleIssues = async () => {
  try {
    const res = await getSampleIssues(sampleIssuesDate.value)
    sampleIssuesList.value = res.data || []
  } catch (err) {
    console.error('加载标本问题列表失败:', err)
  }
}

const handleIssue = async (row, action) => {
  try {
    const remarks = row.remarks || ''
    const res = await handleSampleIssue({ brxxId: row.id, action, remarks })
    if (res.data.success) {
      ElMessage.success(res.data.message)
      loadSampleIssues()
    } else {
      ElMessage.error(res.data.message)
    }
  } catch (err) {
    ElMessage.error('操作失败: ' + (err.response?.data?.message || err.message))
  }
}

// 历史回顾相关
const historyDate = ref(new Date().toISOString().slice(0, 10))
const historyRecords = ref([])

const loadHistoryRecords = async () => {
  try {
    const res = await getHistoryRecords(historyDate.value)
    historyRecords.value = res.data || []
  } catch (err) {
    console.error('加载历史记录失败:', err)
  }
}

// 申请信息相关
const applicationDate = ref(new Date().toISOString().slice(0, 10))
const applicationList = ref([])

const loadApplicationInfo = async () => {
  try {
    const res = await getApplicationInfo(applicationDate.value)
    applicationList.value = res.data || []
  } catch (err) {
    console.error('加载申请信息失败:', err)
  }
}

const viewApplication = (row) => {
  ElMessage.info('查看申请详情: ' + row.applyNo)
}

// 警示信息相关
const warningDate = ref(new Date().toISOString().slice(0, 10))
const warningList = ref([])

const loadWarningInfo = async () => {
  try {
    const res = await getWarningInfo(warningDate.value)
    warningList.value = res.data || []
  } catch (err) {
    console.error('加载警示信息失败:', err)
  }
}

const getWarningClass = (type) => {
  if (type === '危急值') return 'warning-critical'
  if (type === '异常值') return 'warning-abnormal'
  return ''
}

const handleWarning = (row) => {
  ElMessage.info('处理警示: ' + row.message)
}

const clearAllWarnings = async () => {
  try {
    await ElMessageBox.confirm('确定要清除所有警示信息吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await clearWarnings()
    if (res.data.success) {
      ElMessage.success('已清除')
      loadWarningInfo()
    }
  } catch (err) {
    if (err !== 'cancel') console.error('清除失败:', err)
  }
}

// 批量处理：选中/取消选中样本
const togglePatientSelection = (row) => {
  const index = selectedPatientIds.value.indexOf(row.id)
  if (index === -1) {
    selectedPatientIds.value.push(row.id)
  } else {
    selectedPatientIds.value.splice(index, 1)
  }
}

// 批量处理：仅预览样本（不改变选中状态）
const previewPatient = (row) => {
  activePatientId.value = row.id
  activeBrxxId.value = row.id
  currentSampleStatus.value = row.status != null ? Number(row.status) : null
}

// 批量处理：全选当天已审核样本
const selectAllAudited = () => {
  const audited = patientList.value.filter(p => p.status === 2)
  selectedPatientIds.value = audited.map(p => p.id)
}

// 批量处理：清空选择
const clearSelection = () => {
  selectedPatientIds.value = []
}

// 批量审核
const handleBatchAudit = async () => {
  if (selectedPatientIds.value.length === 0) {
    ElMessage.warning('请先选择要审核的样本')
    return
  }
  try {
    await ElMessageBox.confirm(`确定要批量审核选中的 ${selectedPatientIds.value.length} 条样本吗？`, '批量审核')
    batchLoading.value = true
    const res = await batchAudit(selectedPatientIds.value)
    if (res.data.success) {
      ElMessage.success(res.data.message)
      clearSelection()
      loadPatientsFromApi()
    } else {
      ElMessage.warning(res.data.message)
    }
  } catch (err) {
    if (err !== 'cancel') ElMessage.error('批量审核失败: ' + (err.response?.data?.message || err.message))
  } finally {
    batchLoading.value = false
  }
}

// 批量打印
const handleBatchPrint = async () => {
  if (selectedPatientIds.value.length === 0) {
    ElMessage.warning('请先选择要打印的样本')
    return
  }
  openBatchPrintDialog(selectedPatientIds.value, patientFilter.date)
}

// 批量取消审核
const handleBatchUnaudit = async () => {
  if (selectedPatientIds.value.length === 0) {
    ElMessage.warning('请先选择要取消审核的样本')
    return
  }
  try {
    await ElMessageBox.confirm(`确定要取消审核选中的 ${selectedPatientIds.value.length} 条样本吗？`, '批量取消审核')
    batchLoading.value = true
    const res = await batchUnaudit(selectedPatientIds.value)
    if (res.data.success) {
      ElMessage.success(res.data.message)
      clearSelection()
      loadPatientsFromApi()
    } else {
      ElMessage.warning(res.data.message)
    }
  } catch (err) {
    if (err !== 'cancel') ElMessage.error('批量取消审核失败: ' + (err.response?.data?.message || err.message))
  } finally {
    batchLoading.value = false
  }
}

// 批量作废
const handleBatchInvalidate = async () => {
  if (selectedPatientIds.value.length === 0) {
    ElMessage.warning('请先选择要作废的样本')
    return
  }
  try {
    const { value: reason } = await ElMessageBox.prompt('请输入作废原因', '批量作废', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputValidator: (val) => val ? true : '作废原因不能为空'
    })
    batchLoading.value = true
    const res = await batchInvalidate(selectedPatientIds.value, reason)
    if (res.data.success) {
      ElMessage.success(res.data.message)
      clearSelection()
      loadPatientsFromApi()
    } else {
      ElMessage.warning(res.data.message)
    }
  } catch (err) {
    if (err !== 'cancel') ElMessage.error('批量作废失败: ' + (err.response?.data?.message || err.message))
  } finally {
    batchLoading.value = false
  }
}

// 处理病人类型变化：住院病人时，检索方式自动设为"住院号"
const handlePatientTypeChange = () => {
  if (patientForm.type === '住院病人') {
    patientForm.searchMethod = '住院号'
  } else if (patientForm.type === '门诊病人') {
    patientForm.searchMethod = '门诊号'
  } else if (patientForm.type === '体检人员') {
    patientForm.searchMethod = '体检号'
  }
}

// 新增样本：清空表单和结果，准备录入
const handleNew = () => {
  Object.assign(patientForm, {
    brxx_id: null,
    sampleNo: '',
    experimentStatus: '普通',
    barcode: '',
    type: '',
    searchMethod: '收据编号',
    patientId: '',
    name: '',
    sex: '',
    age: '',
    ageUnit: 'Y',
    dept: '',
    bedNo: '',
    sampleType: '',
    sampleMorphology: '',
    applicationTime: '',
    doctor: '',
    samplingTime: '',
    verificationTime: '',
    entryPerson: '',
    inspectionTime: '',
    inspectingPhysician: '',
    reviewTime: '',
    reviewingPhysician: '',
    diagnosis: '',
    remarks: '',
    additionalRemarks: '',
    printTime: '',
    printCount: '',
    ward: '',
    physicalExamUnit: '',
    idNumber: '',
    contactInfo: ''
  })
  resultItems.value = []
  activeResultIndex.value = -1
  selectedComboId.value = null
  activePatientId.value = null
  activeBrxxId.value = null
  currentSampleStatus.value = null
  isNewMode.value = true
  
  // 重置脏检测状态
  lastSavedState.value = createSnapshot()
  historyStack.value = []
  redoStack.value = []

  // 滚动到表单顶部
  const formEl = document.querySelector('.sample-management')
  if (formEl) {
    formEl.scrollIntoView({ behavior: 'smooth', block: 'start' })
  }

  // 聚焦到样本号输入框
  setTimeout(() => {
    const sampleNoInput = document.querySelector('input[v-model="patientForm.sampleNo"]')
    if (sampleNoInput) {
      sampleNoInput.focus()
    }
  }, 100)

  // 新增(F9)：自动生成当天递增样本号（yyyyMMdd + 4位流水）
  const targetDate = patientFilter.date || new Date().toISOString().slice(0, 10)
  console.log('handleNew - targetDate:', targetDate)
  getNextSampleNo({ date: targetDate })
    .then((res) => {
      console.log('handleNew - getNextSampleNo响应:', res.data)
      if (res.data?.success && res.data.sampleNo) {
        patientForm.sampleNo = res.data.sampleNo
        console.log('handleNew - 样本号已设置:', patientForm.sampleNo)
      }
    })
    .catch((e) => {
      console.error('handleNew - 获取样本号失败:', e)
    })
  ElMessage.info('已进入新增样本状态，请录入样本信息和检验项目')
}

const statusText = (status) => {
  if (status === null || status === undefined || status === '') return '登记'
  return getStatusText(Number(status))
}

const urgencyText = (urgency) => {
  const u = Number(urgency)
  if (u === 2) return '急'
  if (u === 3) return '危'
  return ''
}

const categoryText = (category) => {
  const c = Number(category)
  if (c === 1) return '门诊'
  if (c === 2) return '住院'
  if (c === 3) return '体检'
  if (c === 4) return '其他'
  if (c === 5) return '科研'
  return ''
}

const ageUnitText = (ageUnit) => {
  // nllx: 1 岁 2 月 3 天（旧系统还有小时等，这里先最常用）
  const n = Number(ageUnit)
  if (n === 2) return '月'
  if (n === 3) return '天'
  return '岁'
}

// 加载组合项目列表
const loadCombos = () => {
  const devStr = localStorage.getItem('selectedDevice')
  let sbDjid = null
  if (devStr) {
    try {
      const dev = JSON.parse(devStr)
      const devId = dev.sb_djid || dev.sbDjid
      if (devId !== undefined && devId !== null) {
        sbDjid = devId
      }
      console.log('loadCombos - device:', dev, 'sbDjid:', sbDjid)
    } catch (e) {
      console.error('解析设备失败:', e)
    }
  }
  console.log('loadCombos - 调用API, sbDjid:', sbDjid)
  
  // 只添加有值的参数
  const params = {}
  if (sbDjid !== null) {
    params.sbDjid = sbDjid
  }
  if (comboKeyword.value) {
    params.keyword = comboKeyword.value
  }
  
  fetchCombos(params)
    .then((res) => {
      console.log('loadCombos - 响应数据:', res.data)
      comboItems.value = res.data || []
      console.log('loadCombos - comboItems:', comboItems.value)
    })
    .catch((e) => {
      console.error('loadCombos - 错误:', e)
      ElMessage.error(e.response?.data?.message || e.message || '加载组合项目失败')
      comboItems.value = []
    })
}

// 保存样本（按照旧系统逻辑：姓名+样本号为必填，住院病人时条码号必填）
const handleSave = () => {
  // 必填项验证：姓名 + 样本号
  if (!patientForm.name || !patientForm.name.trim()) {
    ElMessage.warning('病人姓名不能为空！')
    return
  }
  if (!patientForm.sampleNo || !patientForm.sampleNo.trim()) {
    ElMessage.warning('样本号不能为空！')
    return
  }
  // 住院病人时，住院号必填（住院号存储在patientId字段）
  if (patientForm.type === '住院病人' && (!patientForm.patientId || !patientForm.patientId.trim())) {
    ElMessage.warning('住院病人必须输入住院号，请输入正确的住院号！')
    return
  }
  // 保存前先保存快照
  saveSnapshot(true)
  
  // 将当前病人信息 + 结果 + 当前仪器ID 打包发送到后端
  const sbDjid =
    currentDevice.value?.sb_djid ||
    currentDevice.value?.sbDjid ||
    null

  const payload = {
    brxx_id: activeBrxxId.value,
    patient: { ...patientForm },
    results: resultItems.value,
    sb_djid: sbDjid,
    bgbh: instrumentStore.state.bgbh || null,
    bgmc: instrumentStore.state.bgmc || null,
    bgbt: instrumentStore.state.bgbt || null,
    bgyj: instrumentStore.state.bgyj || null,
    bgjglx: instrumentStore.state.bgjglx || null
  }
  saveSample(payload)
    .then((res) => {
      if (res.data?.success) {
        ElMessage.success(res.data.message || '样本信息保存成功')
        if (res.data.brxx_id) {
          activeBrxxId.value = res.data.brxx_id
          activePatientId.value = res.data.brxx_id
          patientForm.brxx_id = res.data.brxx_id
        }
        lastSavedState.value = createSnapshot()
        loadPatientsFromApi()
        autoExtract()
      } else {
        ElMessage.error(res.data?.message || '样本保存失败')
      }
    })
    .catch((e) => {
      ElMessage.error(e.response?.data?.message || e.message || '样本保存失败')
    })
}

// 自动提取结果
const autoExtract = async () => {
  if (!auxSettings.autoFetchResult) return
  if (!currentDevice.value?.sb_djid) return
  
  try {
    const { data } = await extractFromInstrument({
      sbDjid: currentDevice.value.sb_djid,
      beginDate: new Date().toISOString().slice(0, 10),
      czydm: JSON.parse(localStorage.getItem('user') || '{}').czydm || 'admin',
      bz: 1,
      patientName: patientForm.name || ''
    })
    if (data.success) {
      ElMessage.success(data.message || '自动提取成功')
      if (toolbarHandlers.value.refresh) {
        toolbarHandlers.value.refresh()
      }
      if (activeBrxxId.value) {
        loadResults(activeBrxxId.value)
      }
    }
  } catch (e) {
    console.error('自动提取失败:', e)
  }
}

const handleToolbarExtract = async () => {
  if (!currentDevice.value?.sb_djid) {
    ElMessage.warning('请先选择仪器')
    return
  }
  if (!activeBrxxId.value) {
    ElMessage.warning('请先选择样本信息')
    return
  }
  if (!patientForm.name || !patientForm.name.trim()) {
    ElMessage.warning('请先录入患者姓名')
    return
  }
  try {
    const { data } = await extractFromInstrument({
      sbDjid: currentDevice.value.sb_djid,
      beginDate: null,
      czydm: JSON.parse(localStorage.getItem('user') || '{}').czydm || 'admin',
      bz: 1,
      patientName: patientForm.name
    })
    if (data.success) {
      ElMessage.success(data.message || '提取成功')
      await loadPatientsFromApi()
      if (activeBrxxId.value) {
        loadResults(activeBrxxId.value)
      }
    } else {
      ElMessage.error(data.message || '提取失败')
    }
  } catch (e) {
    ElMessage.error('提取失败：' + (e.message || '未知错误'))
  }
}

const handleCancel = async () => {
  if (isDirty.value) {
    try {
      await ElMessageBox.confirm('确定要放弃当前编辑吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      handleNew()
      ElMessage.info('已放弃当前编辑')
    } catch {
      // 用户取消
    }
  } else {
    undo()
  }
}

// 检验：将当前样本 ybzt 置为“接收/已检验”
const handleInspect = () => {
  if (!activeBrxxId.value) {
    ElMessage.warning('请先在右侧选择要检验的样本或保存当前样本')
    return
  }
  const czydm = JSON.parse(localStorage.getItem('user') || '{}').czydm || 'admin'
  inspectSample(activeBrxxId.value, czydm)
    .then((res) => {
      if (res.data?.success) {
        ElMessage.success(res.data.message || '检验完成')
        loadPatientsFromApi()
      } else {
        ElMessage.error(res.data?.message || '检验失败')
      }
    })
    .catch((e) => {
      ElMessage.error(e.response?.data?.message || e.message || '检验失败')
    })
}

// 审核：将当前样本 ybzt 置为“审核”
const handleAudit = () => {
  if (!activeBrxxId.value) {
    ElMessage.warning('请先在右侧选择要审核的样本或保存当前样本')
    return
  }
  const s = currentSampleStatus.value
  if (s === 2) {
    ElMessage.warning('该样本已审核，不能重复审核')
    return
  }
  if (s === 3) {
    ElMessage.warning('该样本已打印，不能审核')
    return
  }
  if (s !== 1 && s !== 4) {
    ElMessage.warning('样本状态不是未审核/已检验，无法审核')
    return
  }
  auditSample(activeBrxxId.value)
    .then((res) => {
      if (res.data?.success) {
        ElMessage.success(res.data.message || '审核完成')
        loadPatientsFromApi()
      } else {
        ElMessage.error(res.data?.message || '审核失败')
      }
    })
    .catch((e) => {
      ElMessage.error(e.response?.data?.message || e.message || '审核失败')
    })
}

const handleCancelAudit = () => {
  if (!activeBrxxId.value) {
    ElMessage.warning('请先在右侧选择要取消审核的样本')
    return
  }
  ElMessageBox.confirm('确定要取消审核此样本吗？', '取消审核', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    unauditSample(activeBrxxId.value)
      .then((res) => {
        if (res.data?.success) {
          ElMessage.success('取消审核成功')
          loadPatientsFromApi()
        } else {
          ElMessage.error(res.data?.message || '取消审核失败')
        }
      })
      .catch((e) => {
        ElMessage.error(e.response?.data?.message || e.message || '取消审核失败')
      })
  }).catch(() => {})
}

const handleModifyAudit = () => {
  if (!activeBrxxId.value) {
    ElMessage.warning('请先在右侧选择要修改审核的样本')
    return
  }
  ElMessageBox.confirm('修改审核将取消当前审核状态，确定继续吗？', '修改审核', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    unauditSample(activeBrxxId.value)
      .then((res) => {
        if (res.data?.success) {
          ElMessage.success('已取消审核，可修改后重新审核')
          loadPatientsFromApi()
        } else {
          ElMessage.error(res.data?.message || '操作失败')
        }
      })
      .catch((e) => {
        ElMessage.error(e.response?.data?.message || e.message || '操作失败')
      })
  }).catch(() => {})
}

const printPreviewVisible = ref(false)
const printPreviewPatient = reactive({})
const printPreviewResults = ref([])

// 打印：将当前样本 ybzt 置为“已打印”，并弹出打印预览
const handlePrint = () => {
  if (!activeBrxxId.value) {
    ElMessage.warning('请先在右侧选择要打印的样本')
    return
  }
  printSample(activeBrxxId.value)
    .then((res) => {
      if (res.data?.success) {
        ElMessage.success(res.data.message || '打印完成')
        loadPatientsFromApi()
        // 打开“报告HTML”新窗口（可直接浏览器打印），对齐旧系统“报表预览→打印”
        fetchReportHtml(activeBrxxId.value)
          .then((r) => {
            const w = window.open('', '_blank')
            if (w) {
              w.document.open()
              w.document.write(r.data || '')
              w.document.close()
              w.focus()
            } else {
              ElMessage.warning('浏览器拦截了弹窗，请允许弹窗后再打印')
            }
          })
          .catch((e) => {
            ElMessage.error(e.response?.data?.message || e.message || '获取报告内容失败')
          })
      } else {
        ElMessage.error(res.data?.message || '打印失败')
      }
    })
    .catch((e) => {
      ElMessage.error(e.response?.data?.message || e.message || '打印失败')
    })
}

// 查找对话框相关
const showSearchDialog = ref(false)

// 右键菜单相关
const showContextMenu = ref(false)
const contextMenuX = ref(0)
const contextMenuY = ref(0)

const handleRightClick = (event) => {
  event.preventDefault()
  contextMenuX.value = event.clientX
  contextMenuY.value = event.clientY
  showContextMenu.value = true
}

const handleContextRefresh = () => {
  loadPatientsFromApi()
  showContextMenu.value = false
}

const handleContextFind = () => {
  showSearchDialog.value = true
  showContextMenu.value = false
}

const handleContextEdit = () => {
  if (activePatientId.value) {
    ElMessage.info('修改样本功能')
  }
  showContextMenu.value = false
}

const handleContextAudit = async () => {
  if (activePatientId.value) {
    try {
      await handleAudit()
    } catch (e) {
      ElMessage.error('审核失败')
    }
  }
  showContextMenu.value = false
}

const handleContextPrint = () => {
  if (activePatientId.value) {
    handlePrint()
  }
  showContextMenu.value = false
}

const handleContextNew = () => {
  handleNew()
  showContextMenu.value = false
}

const handleContextSave = () => {
  handleSave()
  showContextMenu.value = false
}

const handleContextInspect = async () => {
  if (activePatientId.value) {
    try {
      await handleInspect()
    } catch (e) {
      ElMessage.error('检验失败')
    }
  }
  showContextMenu.value = false
}

const handleContextPreview = () => {
  if (activePatientId.value) {
    printPreviewVisible.value = true
    fetchReportHtml(activePatientId.value).then(res => {
      printPreviewResults.value = []
      printPreviewPatient.value = patientForm
    })
  }
  showContextMenu.value = false
}

const handleContextInvalidate = async () => {
  if (activePatientId.value) {
    try {
      await ElMessageBox.confirm('确定要作废该报告吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      const res = await batchInvalidate([activePatientId.value], '报告作废')
      if (res.data.success) {
        ElMessage.success('报告已作废')
        loadPatientsFromApi()
      } else {
        ElMessage.error(res.data.message)
      }
    } catch (e) {
      if (e !== 'cancel') {
        ElMessage.error('作废失败')
      }
    }
  }
  showContextMenu.value = false
}

const handleContextUnaudit = async () => {
  if (activePatientId.value) {
    try {
      const res = await batchUnaudit([activePatientId.value])
      if (res.data.success) {
        ElMessage.success('已取消审核')
        loadPatientsFromApi()
      } else {
        ElMessage.error(res.data.message)
      }
    } catch (e) {
      ElMessage.error('取消审核失败')
    }
  }
  showContextMenu.value = false
}

// 点击其他区域关闭右键菜单
const handleClickOutside = () => {
  showContextMenu.value = false
}

const editTimeVisible = ref(false)

const handleContextEditTime = () => {
  if (!activePatientId.value) {
    ElMessage.warning('请先选择一个样本')
  } else {
    editTimeVisible.value = true
  }
  showContextMenu.value = false
}

const handleEditTimeConfirm = () => {
  loadPatientsFromApi()
}

const searchForm = reactive({
  sampleNo: '',
  name: '',
  barcode: '',
  patientId: '',
  date: ''
})
const searchResults = ref([])

// 查找功能
const handleFind = () => {
  showSearchDialog.value = true
  // 初始化日期为今天
  if (!searchForm.date) {
    searchForm.date = new Date().toISOString().slice(0, 10)
  }
}

// 执行查找
const executeSearch = () => {
  searchSamples({
    sampleNo: searchForm.sampleNo,
    name: searchForm.name,
    barcode: searchForm.barcode,
    patientId: searchForm.patientId,
    date: searchForm.date
  })
    .then((res) => {
      searchResults.value = res.data || []
      if (searchResults.value.length === 0) {
        ElMessage.info('未找到匹配的样本')
      } else {
        ElMessage.success(`找到 ${searchResults.value.length} 条记录`)
      }
    })
    .catch((e) => {
      ElMessage.error(e.response?.data?.message || e.message || '查找失败')
      searchResults.value = []
    })
}

// 选择查找结果
const selectSearchResult = (row) => {
  selectPatient(row)
  showSearchDialog.value = false
  // 刷新右侧列表并定位到该记录
  loadPatientsFromApi()
  setTimeout(() => {
    activePatientId.value = row.id
    activeBrxxId.value = row.id
    patientForm.brxx_id = row.id
  }, 300)
}

// 选择日期：改变当前录入日期
const handleSelectDate = (dateStr) => {
  if (dateStr) {
    patientFilter.date = dateStr
    ElMessage.success(`已切换到日期：${dateStr}`)
    // 刷新右侧样本列表
    loadPatientsFromApi()
  } else {
    // 如果没有传入日期，弹出日期选择对话框
    ElMessageBox.prompt('请输入日期（格式：YYYY-MM-DD）', '选择日期', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputPattern: /^\d{4}-\d{2}-\d{2}$/,
      inputErrorMessage: '日期格式不正确，请输入 YYYY-MM-DD 格式',
      inputValue: patientFilter.date || new Date().toISOString().slice(0, 10)
    }).then(({ value }) => {
      patientFilter.date = value
      ElMessage.success(`已切换到日期：${value}`)
      loadPatientsFromApi()
    }).catch(() => {
      // 用户取消
    })
  }
}

// 刷新：刷新右侧样本列表
const handleRefresh = async () => {
  if (isDirty.value) {
    try {
      await ElMessageBox.confirm('当前有未保存的修改，确定要刷新吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
    } catch {
      return
    }
  }
  isRefreshing.value = true
  try {
    await loadPatientsFromApi()
    lastRefreshTime.value = new Date().toLocaleTimeString()
    ElMessage.success('已刷新样本列表')
  } catch (e) {
    ElMessage.error('刷新失败')
  } finally {
    isRefreshing.value = false
  }
}

// 回顾功能：查看历史记录（暂时复用查找功能，显示最近的数据）
const handleReview = () => {
  // 回顾功能：显示最近处理的样本
  const today = new Date().toISOString().slice(0, 10)
  searchSamples({ date: today })
    .then((res) => {
      searchResults.value = res.data || []
      if (searchResults.value.length === 0) {
        ElMessage.info('今日暂无样本记录')
      } else {
        showSearchDialog.value = true
        ElMessage.success(`今日共 ${searchResults.value.length} 条记录`)
      }
    })
    .catch((e) => {
      ElMessage.error(e.response?.data?.message || e.message || '加载历史记录失败')
    })
}

// 加载某个样本的检验结果（从 bgxt_jyjg）
const loadResults = (brxxId) => {
  if (!brxxId) {
    resultItems.value = []
    activeResultIndex.value = -1
    return
  }
  fetchResults(brxxId)
    .then((res) => {
      const list = res.data || []
      resultItems.value = list.map((r) => ({
        xmid: r.xmid,
        code: r.code,
        name: r.name,
        unit: r.unit,
        refRange: r.refRange,
        result: r.result,
        criticalLow: r.criticalLow,
        criticalHigh: r.criticalHigh,
        flag: r.highLowFlag || calcFlag(r.result, r.refRange, r.criticalLow, r.criticalHigh)
      }))
      activeResultIndex.value = resultItems.value.length > 0 ? 0 : -1
    })
    .catch((e) => {
      ElMessage.error(e.response?.data?.message || e.message || '加载检验结果失败')
      resultItems.value = []
      activeResultIndex.value = -1
    })
}

// 选择组合项目：从后端按组合ID加载真实项目列表
const selectCombo = (combo) => {
  if (!combo || !combo.zhid) {
    return
  }
  selectedComboId.value = combo.zhid
  
  console.log('selectCombo - 开始加载组合项目:', combo)
  
  const devStr = localStorage.getItem('selectedDevice')
  let sbDjid = null
  if (devStr) {
    try {
      const dev = JSON.parse(devStr)
      const devId = dev.sb_djid || dev.sbDjid
      if (devId !== undefined && devId !== null) {
        sbDjid = devId
      }
      console.log('selectCombo - 设备信息:', dev, 'sbDjid:', sbDjid)
    } catch (e) {
      console.error('selectCombo - 解析设备失败:', e)
    }
  } else {
    console.log('selectCombo - 未找到设备信息')
  }
  
  // 准备参考范围匹配参数（只添加有值的参数）
  const params = {}
  if (sbDjid !== null) {
    params.sbDjid = sbDjid
  }
  if (patientForm.sampleType) {
    params.sampleType = patientForm.sampleType
  }
  if (patientForm.sex) {
    params.sex = patientForm.sex === 'M' ? 1 : (patientForm.sex === 'F' ? 2 : null)
  }
  if (patientForm.age) {
    params.age = parseInt(patientForm.age)
  }
  if (patientForm.ageUnit) {
    params.ageUnit = patientForm.ageUnit === 'Y' ? 1 : (patientForm.ageUnit === 'M' ? 2 : (patientForm.ageUnit === 'D' ? 3 : 1))
  }
  
  console.log('selectCombo - 请求参数:', params)
  
  fetchComboItems(combo.zhid, params)
    .then((res) => {
      console.log('selectCombo - 响应成功:', res.data)
      const list = res.data || []
      resultItems.value = list.map((item) => ({
        xmid: item.xmid,
        code: item.code,
        name: item.name,
        unit: item.unit || '',
        refRange: item.refRange || '',
        result: ''
      }))
      activeResultIndex.value = resultItems.value.length > 0 ? 0 : -1
      if (resultItems.value.length === 0) {
        ElMessage.info('该组合下暂无明细项目')
      }
    })
    .catch((e) => {
      console.error('selectCombo - 请求失败:', e)
      console.error('selectCombo - 错误详情:', e.response?.data)
      ElMessage.error(e.response?.data?.message || e.message || '加载组合项目明细失败')
    })
}

// 选择右侧病人列表中的一条记录，回填到左边和中间区域
  const selectPatient = (row, refillForm = true) => {
    hasEverSelected.value = true
    activePatientId.value = row.id
    activeBrxxId.value = row.id
    currentSampleStatus.value = row.status != null ? Number(row.status) : null
    patientForm.brxx_id = row.id
    if (refillForm) {
      patientForm.sampleNo = row.sampleNo || ''
      patientForm.barcode = row.barcode || ''
      patientForm.patientId = row.patientId || ''
      patientForm.name = row.name || ''
      const sexVal = row.sexCode || row.sex
      if (sexVal === '男' || sexVal === 1 || sexVal === '1' || sexVal === 'M') {
        patientForm.sex = 'M'
      } else if (sexVal === '女' || sexVal === 2 || sexVal === '2' || sexVal === 'F') {
        patientForm.sex = 'F'
      } else {
        patientForm.sex = ''
      }
      patientForm.age = row.age || ''
      const nllx = String(row.ageUnit || row.nllx || 'Y')
      if (nllx === 'Y' || nllx === '1' || nllx === '岁') {
        patientForm.ageUnit = 'Y'
      } else if (nllx === 'M' || nllx === '2' || nllx === '月') {
        patientForm.ageUnit = 'M'
      } else if (nllx === 'D' || nllx === '3' || nllx === '天') {
        patientForm.ageUnit = 'D'
      } else {
        patientForm.ageUnit = 'Y'
      }
      patientForm.type = row.category || ''
      const categoryMap = { 1: '门诊病人', 2: '住院病人', 3: '体检人员', 4: '其他病人', 5: '科研人员' }
      const ptMatch = patientTypeOptions.value.find(pt => pt.bm == row.category || pt.bm === row.category)
      if (ptMatch) {
        patientForm.type = ptMatch.bmsm
      } else if (categoryMap[row.category]) {
        patientForm.type = categoryMap[row.category]
      }
      patientForm.urgency = row.urgency || ''
      if (row.urgency == 0 || row.urgency === '0') patientForm.experimentStatus = '普通'
      else if (row.urgency == 1 || row.urgency === '1') patientForm.experimentStatus = '普通'
      else if (row.urgency == 2 || row.urgency === '2') patientForm.experimentStatus = '紧急'
      else if (row.urgency == 3 || row.urgency === '3') patientForm.experimentStatus = '危急'
      patientForm.dept = row.dept || ''
      patientForm.ward = row.ward || ''
      patientForm.bedNo = row.bedNo || ''
      patientForm.sampleType = row.sampleType || ''
      patientForm.sampleTypeCode = row.sampleTypeCode || ''
      patientForm.doctor = row.doctor || ''
      patientForm.diagnosis = row.diagnosis || ''
      patientForm.remarks = row.remarks || ''
      patientForm.additionalRemarks = row.additionalRemarks || ''
      patientForm.physicalExamUnit = row.physicalExamUnit || ''
      patientForm.idNumber = row.idNumber || ''
      patientForm.contactInfo = row.contactInfo || ''
      patientForm.sampleMorphology = row.sampleMorphology || ''
      patientForm.inspectingPhysician = row.inspectingPhysician || row.inspectDoctor || ''
      patientForm.reviewingPhysician = row.reviewingPhysician || row.auditDoctor || ''
      patientForm.applicationTime = row.applicationTime || ''
      patientForm.samplingTime = row.samplingTime || ''
      patientForm.verificationTime = row.verificationTime || ''
      patientForm.entryPerson = row.entryPerson || ''
      patientForm.inspectionTime = row.inspectionTime || ''
      patientForm.reviewTime = row.reviewTime || ''
      patientForm.printTime = row.printTime || ''
      patientForm.printCount = row.printCount != null ? String(row.printCount) : ''
      patientForm.submitDate = row.submitDate || ''
    }
    loadResults(row.id)
  }

// 为方便演示，挂载时构造一点示例病人数据
const loadPatientsFromApi = () => {
  const today = new Date().toISOString().slice(0, 10)
  if (!patientFilter.date) {
    patientFilter.date = today
  }
  console.log('loadPatientsFromApi - 请求参数:', { date: patientFilter.date, patientType: patientFilter.category, sampleNo: patientFilter.sampleNo })
  fetchPatients({
    date: patientFilter.date,
    patientType: patientFilter.category,
    sampleNo: patientFilter.sampleNo
  })
    .then((res) => {
      console.log('loadPatientsFromApi - 响应:', res.data)
      patientList.value = res.data || []
      console.log('loadPatientsFromApi - patientList:', patientList.value)
      if (activePatientId.value) {
        const match = patientList.value.find(p => p.id === activePatientId.value)
        if (match) {
          selectPatient(match, false)
        } else {
          activePatientId.value = null
          activeBrxxId.value = null
        }
      } else if (patientList.value.length > 0 && !isNewMode.value && hasEverSelected.value) {
        selectPatient(patientList.value[0])
      }
      isNewMode.value = false
    })
    .catch((e) => {
      console.error('loadPatientsFromApi - 错误:', e)
      ElMessage.error(e.response?.data?.message || e.message || '加载病人列表失败')
      patientList.value = []
    })
}

// 监听筛选条件变化，自动搜索
watch(() => patientFilter.sampleNo, () => { loadPatientsFromApi() })
watch(() => patientFilter.category, () => { loadPatientsFromApi() })

// 监听表单和结果变化，自动保存快照
watch(() => ({ ...patientForm }), () => {
  saveSnapshot()
  if (patientForm.name) {
    localStorage.setItem('currentPatientName', patientForm.name)
  }
  if (activePatientId.value) {
    const row = patientList.value.find(p => p.id === activePatientId.value)
    if (row) {
      row.name = patientForm.name
      row.barcode = patientForm.barcode
      row.sampleNo = patientForm.sampleNo
      row.sex = patientForm.sex
      row.age = patientForm.age
      row.urgency = patientForm.urgency
    }
  }
}, { deep: true })
watch(resultItems, (items) => {
  items.forEach(item => {
    if (item.result !== undefined) {
      item.flag = calcFlag(item.result, item.refRange, item.criticalLow, item.criticalHigh)
    }
  })
  saveSnapshot()
}, { deep: true })

let keydownHandler = null

onMounted(() => {
  loadFlagConfig()
  loadDropdownOptions()
  // 从 localStorage 读取当前绑定仪器；如果没有，则引导用户回到主界面选择仪器
  const devStr = localStorage.getItem('selectedDevice')
  if (!devStr) {
    ElMessage.warning('当前未绑定仪器，请先在主界面选择仪器')
    router.push('/main')
    return
  }
  try {
    currentDevice.value = JSON.parse(devStr)
  } catch (e) {
    console.error('解析 selectedDevice 失败', e)
  }

  // 注册工具栏按钮处理器到 MainFrame
  if (registerToolbarHandler) {
    registerToolbarHandler('new', handleNew)
    registerToolbarHandler('save', handleSave)
    registerToolbarHandler('cancel', handleCancel)
    registerToolbarHandler('test', handleInspect)
    registerToolbarHandler('audit', handleAudit)
    registerToolbarHandler('cancelAudit', handleCancelAudit)
    registerToolbarHandler('modifyAudit', handleModifyAudit)
    registerToolbarHandler('print', handlePrint)
    registerToolbarHandler('find', handleFind)
    registerToolbarHandler('review', handleReview)
    registerToolbarHandler('refresh', handleRefresh)
    registerToolbarHandler('selectDate', handleSelectDate)
  }

  loadPatientsFromApi()
  loadCombos()
  loadErrorTypes()
  loadHandlingMeasures()
  loadRejectRecords()

  // 简单绑定本页面常用快捷键（F9/F10/ESC/F7/F8），不和主框架冲突
  keydownHandler = (e) => {
    // Ctrl+Z 撤销, Ctrl+Y / Ctrl+Shift+Z 重做
    if (e.ctrlKey && e.key === 'z' && !e.shiftKey) {
      e.preventDefault()
      undo()
    } else if (e.ctrlKey && (e.key === 'y' || (e.shiftKey && e.key === 'Z'))) {
      e.preventDefault()
      redo()
    } else if (e.key === 'F9') {
      e.preventDefault()
      handleNew()
    } else if (e.key === 'F10') {
      e.preventDefault()
      handleSave()
    } else if (e.key === 'Escape') {
      const activeDialogs = document.querySelectorAll('.el-dialog__wrapper')
      if (activeDialogs.length === 0) {
        e.preventDefault()
        handleCancel()
      }
    } else if (e.key === 'F7') {
      e.preventDefault()
      handleInspect()
    } else if (e.key === 'F8') {
      e.preventDefault()
      handleAudit()
    }
  }
  window.addEventListener('keydown', keydownHandler)
})

onUnmounted(() => {
  if (keydownHandler) {
    window.removeEventListener('keydown', keydownHandler)
  }
  
  // 清理工具栏按钮处理器
  if (unregisterToolbarHandler) {
    unregisterToolbarHandler('new')
    unregisterToolbarHandler('save')
    unregisterToolbarHandler('cancel')
    unregisterToolbarHandler('test')
    unregisterToolbarHandler('audit')
    unregisterToolbarHandler('cancelAudit')
    unregisterToolbarHandler('modifyAudit')
    unregisterToolbarHandler('print')
    unregisterToolbarHandler('find')
    unregisterToolbarHandler('review')
    unregisterToolbarHandler('refresh')
    unregisterToolbarHandler('selectDate')
    unregisterToolbarHandler('extract')
  }
})
</script>

<style scoped>
.sample-management {
  display: flex;
  flex-direction: column;
  height: 100%;
  width: 100%;
  background: #c0d8f0; /* 更接近旧系统整体淡蓝底色 */
  overflow: hidden;
}

.tb-btn {
  width: 64px;
  height: 54px;
  padding: 4px 4px 2px;
  border: 1px solid rgba(0, 0, 0, 0.18);
  border-top-color: rgba(255, 255, 255, 0.75);
  border-left-color: rgba(255, 255, 255, 0.55);
  border-right-color: rgba(0, 0, 0, 0.2);
  border-bottom-color: rgba(0, 0, 0, 0.25);
  background: linear-gradient(#f7fbff 0%, #e8f3ff 55%, #d7ecfb 100%);
  border-radius: 2px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-start;
  gap: 3px;
  cursor: default;
  user-select: none;
}

.tb-btn.tb-primary {
  background: linear-gradient(#d8ecff 0%, #c9e4ff 55%, #b7d7f0 100%);
}

.tb-btn.tb-success {
  background: linear-gradient(#dff6e5 0%, #cfeedd 55%, #bfe4cf 100%);
}

.tb-icon {
  width: 26px;
  height: 26px;
  display: inline-block;
  border-radius: 4px;
  background-color: #ffffff;
  border: 1px solid rgba(0, 0, 0, 0.1);
}

.tb-text {
  font-size: 11px;
  line-height: 12px;
  color: #0b1b2b;
  white-space: nowrap;
}

.tb-sep {
  width: 2px;
  margin: 2px 4px;
  background: linear-gradient(
    to bottom,
    rgba(255, 255, 255, 0.85),
    rgba(0, 0, 0, 0.2),
    rgba(255, 255, 255, 0.85)
  );
  border-left: 1px solid rgba(255, 255, 255, 0.55);
  border-right: 1px solid rgba(0, 0, 0, 0.1);
}

.sm-main {
  flex: 1;
  display: flex;
  flex-direction: row;
  padding: 2px 4px 4px;
  gap: 3px;
  min-height: 0 !important;
  overflow: hidden !important;
}

.sm-panel {
  background: #ffffff;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.sm-left {
  width: 480px;
  min-width: 400px;
  max-width: 600px;
}

.sm-center {
  flex: 1;
  min-width: 320px;
  min-height: 0 !important;
  overflow: hidden !important;
}

.sm-right {
  width: 420px;
  min-width: 320px;
}

.sm-panel-header {
  height: 36px;
  line-height: 36px;
  padding: 0 12px;
  background: #f5f7fa;
  border-bottom: 1px solid #e4e7ed;
  font-size: 13px;
  font-weight: 500;
  color: #303133;
}

.sm-panel-body {
  flex: 1;
  padding: 12px;
  overflow: auto;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.result-scroll-body {
  padding: 0;
  overflow-y: auto;
  flex: 1;
  min-height: 0;
}

.result-scroll-body .simple-table thead {
  position: sticky;
  top: 0;
  z-index: 1;
  background: #f5f7fa;
}

.patient-form {
  border: 1px solid #e4e7ed;
  padding: 8px 12px;
  margin-bottom: 8px;
  background: #fafafa;
  border-radius: 4px;
  flex: 0 1 auto;
  overflow-y: auto; /* 表单内容可滚动 */
  min-height: 0;
  max-height: calc(100vh - 400px); /* 限制最大高度，防止占据过多空间 */
}

.form-row {
  display: grid;
  grid-template-columns: auto 1fr auto 1fr;
  gap: 4px 6px;
  margin-bottom: 4px;
  font-size: 12px;
  align-items: center;
}

.form-row label {
  text-align: right;
  line-height: 28px;
  white-space: nowrap;
  padding-right: 2px;
}

.form-row input,
.form-row select {
  height: 28px;
  border: 1px solid #dcdfe6;
  padding: 0 8px;
  font-size: 13px;
  border-radius: 4px;
  box-sizing: border-box;
  width: 100%;
}

.form-row .full {
  grid-column: 2 / -1;
}

.form-row :deep(.el-select) {
  width: 100% !important;
}

.form-row :deep(.el-select .el-input__wrapper) {
  height: 28px;
  box-shadow: 0 0 0 1px #dcdfe6 inset;
}

.form-row :deep(.el-select .el-input__inner) {
  font-size: 13px;
  height: 26px;
}

.form-row .full-textarea {
  grid-column: 2 / -1;
  height: 60px;
  resize: vertical;
  border: 1px solid #dcdfe6;
  padding: 4px 8px;
  font-size: 13px;
  border-radius: 4px;
  box-sizing: border-box;
  font-family: inherit;
}

.sub-panel-header {
  height: 32px;
  line-height: 32px;
  padding: 0 12px;
  background: #f5f7fa;
  border-top: 1px solid #e4e7ed;
  border-bottom: 1px solid #e4e7ed;
  font-size: 13px;
  font-weight: 500;
  margin-top: 8px;
  flex-shrink: 0;
}

.sub-panel-body {
  height: 200px;
  min-height: 200px;
  max-height: 200px;
  border: 1px solid #e4e7ed;
  border-top: none;
  border-radius: 0 0 4px 4px;
  overflow: auto;
  flex-shrink: 0;
  position: relative;
}

.combo-search {
  display: flex;
  gap: 8px;
  padding: 8px;
  border-bottom: 1px solid #e4e7ed;
  background: #fafafa;
}

.combo-search input {
  flex: 1;
  height: 28px;
  border: 1px solid #dcdfe6;
  padding: 0 8px;
  font-size: 13px;
  border-radius: 4px;
}

.combo-search button {
  padding: 0 12px;
  height: 28px;
  font-size: 13px;
  border-radius: 4px;
}

.simple-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 13px;
  border-radius: 4px;
  overflow: hidden;
}

.simple-table th,
.simple-table td {
  border: 1px solid #ebeef5;
  padding: 8px;
  white-space: nowrap;
}

.simple-table thead {
  background: #f5f7fa;
}

.simple-table th {
  font-weight: 500;
  color: #606266;
}

.placeholder-cell {
  text-align: center;
  color: #909399;
  padding: 20px 8px;
}

.right-filter {
  margin-bottom: 8px;
  border: 1px solid #e4e7ed;
  padding: 8px;
  background: #fafafa;
  border-radius: 4px;
  font-size: 13px;
}

.filter-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
  flex-wrap: wrap;
}

.filter-row label {
  text-align: right;
  line-height: 28px;
  white-space: nowrap;
  min-width: auto;
}

.filter-row input,
.filter-row select {
  height: 28px;
  border: 1px solid #dcdfe6;
  padding: 0 8px;
  font-size: 13px;
  border-radius: 4px;
  box-sizing: border-box;
  min-width: 0;
}

.right-table {
  overflow-y: auto;
  flex: 1;
  min-height: 0;
}
.right-table .simple-table thead {
  position: sticky;
  top: 0;
  z-index: 1;
  background: #f5f7fa;
}

.right-tab-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
  overflow: hidden;
}

.right-tabs {
  display: flex;
  gap: 2px;
  margin-bottom: 10px;
  flex-wrap: wrap;
}

.right-tab {
  padding: 8px 16px;
  border: none;
  background: #e0e0e0;
  cursor: pointer;
  font-size: 13px;
  border-radius: 4px;
}

.right-tab:hover {
  background: #d0d0d0;
}

.right-tab.active {
  background: #1976d2;
  color: white;
}

.row-active {
  background-color: #ecf5ff;
}

.cell-input {
  width: 100%;
  border: none;
  outline: none;
  height: 24px;
  box-sizing: border-box;
}

.cell-input-disabled {
  background-color: #f5f5f5;
  color: #999;
  cursor: not-allowed;
}

.cell-urgent {
  color: #f56c6c;
  font-weight: 600;
  text-align: center;
}

.cell-status {
  font-weight: bold;
}

/* 查找对话框样式 */
.search-dialog {
  padding: 10px;
}

.search-form {
  margin-bottom: 20px;
  padding: 16px;
  background: #f5f7fa;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
}

.search-row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.search-row label {
  width: 100px;
  text-align: right;
  font-size: 13px;
  color: #606266;
}

.search-row .el-input {
  flex: 1;
}

.search-results {
  max-height: 400px;
  overflow-y: auto;
}

.status-badge {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 3px;
  font-size: 11px;
  font-weight: normal;
}

.status-badge.status-entry {
  background: #e3f2fd;
  color: #1976d2;
}

.status-badge.status-inspect {
  background: #fff3e0;
  color: #f57c00;
}

.status-badge.status-audit {
  background: #fff8e1;
  color: #f9a825;
}

.status-badge.status-print {
  background: #e8f5e9;
  color: #388e3c;
}

.print-preview-header {
  display: flex;
  flex-wrap: wrap;
  gap: 10px 20px;
  margin-bottom: 10px;
  font-size: 12px;
}

.print-preview-body {
  max-height: 400px;
  overflow-y: auto;
}

.batch-tab-content {
  padding: 10px;
}

.batch-toolbar {
  display: flex;
  gap: 10px;
  margin-bottom: 10px;
  align-items: center;
}

.batch-info {
  font-size: 12px;
  color: #666;
}

.batch-actions {
  display: flex;
  gap: 10px;
  margin-bottom: 10px;
}

.batch-table {
  max-height: 300px;
  overflow-y: auto;
}

.row-selected {
  background: #e3f2fd !important;
}

.right-tab.active {
  background: #1976d2;
  color: white;
}

.progress-tab-content,
.sample-tab-content {
  padding: 10px;
}

.progress-toolbar,
.sample-toolbar {
  display: flex;
  gap: 10px;
  margin-bottom: 10px;
  align-items: center;
}

.progress-summary {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  padding: 15px;
  background: #f5f5f5;
  border-radius: 4px;
}

.progress-item {
  display: flex;
  align-items: center;
  gap: 5px;
}

.progress-label {
  font-size: 13px;
  color: #666;
}

.progress-value {
  font-size: 18px;
  font-weight: bold;
  color: #1976d2;
}

.progress-item.total .progress-value {
  color: #388e3c;
}

.sample-table {
  max-height: 350px;
  overflow-y: auto;
}

.rejection-form {
  padding: 8px;
  border-bottom: 1px solid #e0e0e0;
  margin-bottom: 10px;
}

.rejection-form-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 6px;
  font-size: 13px;
}

.rejection-form-row label {
  min-width: 80px;
  color: #555;
  text-align: right;
}

.rejection-form-row input,
.rejection-form-row select {
  padding: 3px 6px;
  border: 1px solid #ccc;
  border-radius: 3px;
  font-size: 13px;
}

.rejection-form-info {
  background: #f5f5f5;
  padding: 6px 8px;
  border-radius: 4px;
  margin-bottom: 6px;
}

.rejection-records {
  padding: 0;
}

.rejection-records-header {
  display: flex;
  gap: 8px;
  align-items: center;
  margin-bottom: 8px;
  font-size: 13px;
}

.rejection-records-header input[type="date"] {
  padding: 3px 6px;
  border: 1px solid #ccc;
  border-radius: 3px;
  font-size: 13px;
}

.auxiliary-panel {
  margin-top: 10px;
  padding: 10px;
  background: #f5f5f5;
  border-radius: 4px;
}

.auxiliary-title {
  font-size: 12px;
  font-weight: bold;
  margin-bottom: 8px;
  color: #333;
}

.auxiliary-options {
  display: flex;
  flex-wrap: wrap;
  gap: 10px 20px;
}

.auxiliary-options label {
  font-size: 12px;
  display: flex;
  align-items: center;
  gap: 4px;
  cursor: pointer;
}

.auxiliary-options input[type="checkbox"] {
  cursor: pointer;
}

.auxiliary-options input[type="checkbox"]:disabled {
  cursor: not-allowed;
  opacity: 0.5;
}

.auxiliary-options label:has(input:disabled) {
  cursor: not-allowed;
  opacity: 0.5;
  color: #999;
}

.context-menu {
  position: fixed;
  background: white;
  border: 1px solid #ddd;
  box-shadow: 2px 2px 8px rgba(0, 0, 0, 0.15);
  z-index: 9999;
  min-width: 120px;
  border-radius: 4px;
}

.context-menu-item {
  padding: 8px 16px;
  cursor: pointer;
  font-size: 13px;
}

.context-menu-item:hover {
  background: #e3f2fd;
}

.context-menu-divider {
  height: 1px;
  background: #eee;
  margin: 4px 0;
}
</style>


