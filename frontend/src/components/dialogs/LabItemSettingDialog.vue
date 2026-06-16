<template>
  <el-dialog
    :model-value="modelValue"
    @update:model-value="$emit('update:modelValue', $event)"
    title="检验项目设置"
    width="1400px"
    :close-on-click-modal="false"
    append-to-body
    class="lab-item-setting-dialog"
  >
    <el-tabs v-model="activeTab" type="border-card" @tab-change="onTabChange">
      <!-- Tab1: 检验项目设置 -->
      <el-tab-pane label="检验项目设置" name="tab1">
        <div class="tab1-container">
          <!-- 左侧：项目列表 -->
          <div class="left-panel">
            <div class="search-bar">
              <span class="search-label">查询项目名称</span>
              <el-input
                v-model="searchKeyword"
                placeholder="输入拼音码或项目名称"
                size="small"
                style="width: 150px"
                @keyup.enter="handleSearch"
                clearable
              />
              <el-button size="small" type="primary" @click="handleSearch">查询</el-button>
            </div>

            <!-- 项目列表网格 -->
            <el-table
              :data="itemList"
              border
              stripe
              size="small"
              height="400"
              highlight-current-row
              @current-change="onItemRowSelect"
              @row-dblclick="onItemDblClick"
              class="item-grid"
            >
              <el-table-column prop="xmid" label="项目ID" width="60" />
              <el-table-column prop="xmdm" label="项目代码" width="80" />
              <el-table-column prop="xmzwmc" label="中文名称" min-width="120" />
              <el-table-column prop="xmywmc" label="英文名称" width="100" />
              <el-table-column prop="xmdw" label="单位" width="60" />
              <el-table-column prop="xmjd" label="精度" width="60" />
              <el-table-column prop="pym" label="拼音码" width="80" />
              <el-table-column prop="qtdm" label="其他代码" width="80" />
              <el-table-column prop="tybz" label="停用" width="50">
                <template #default="{row}">
                  <el-tag :type="row.tybz ? 'danger' : 'success'" size="small">{{ row.tybz ? '是' : '否' }}</el-tag>
                </template>
              </el-table-column>
            </el-table>

            <!-- 搜索结果网格（浮动显示） -->
            <el-table
              v-if="searchResults.length > 0"
              :data="searchResults"
              border
              size="small"
              height="150"
              highlight-current-row
              class="search-results-grid"
              @row-dblclick="onSearchResultDblClick"
            >
              <el-table-column prop="xmid" label="ID" width="50" />
              <el-table-column prop="xmdm" label="代码" width="70" />
              <el-table-column prop="xmzwmc" label="中文名称" />
              <el-table-column prop="pym" label="拼音码" width="80" />
            </el-table>
          </div>

          <!-- 右侧：编辑表单 -->
          <div class="right-panel">
            <div class="form-title">项目基本信息</div>
            <el-form :model="itemForm" label-width="100px" size="small" class="item-form" :disabled="!editing">
              <el-row :gutter="10">
                <el-col :span="12">
                  <el-form-item label="中文名称" required>
                    <el-input v-model="itemForm.xmzwmc" placeholder="输入中文名称" @keyup.enter="focusNextField('xmywmc')" />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="英文名称">
                    <el-input v-model="itemForm.xmywmc" ref="xmywmc" placeholder="输入英文名称" @keyup.enter="focusNextField('pym')" />
                  </el-form-item>
                </el-col>
              </el-row>

              <el-row :gutter="10">
                <el-col :span="12">
                  <el-form-item label="拼音码" required>
                    <el-input v-model="itemForm.pym" ref="pym" placeholder="拼音码" @keyup.enter="focusNextField('qtdm')" />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="其他代码">
                    <el-input v-model="itemForm.qtdm" ref="qtdm" placeholder="其他代码" @keyup.enter="focusNextField('xmdw')" />
                  </el-form-item>
                </el-col>
              </el-row>

              <el-row :gutter="10">
                <el-col :span="12">
                  <el-form-item label="项目单位">
                    <el-input v-model="itemForm.xmdw" ref="xmdw" placeholder="项目单位" @keyup.enter="focusNextField('xmjd')" />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="精度">
                    <el-select v-model="itemForm.xmjd" style="width: 100%">
                      <el-option v-for="p in precisionOptions" :key="p.value" :label="p.label" :value="p.value" />
                    </el-select>
                  </el-form-item>
                </el-col>
              </el-row>

              <el-row :gutter="10">
                <el-col :span="12">
                  <el-form-item label="项目类型">
                    <el-select v-model="itemForm.itemType" style="width: 100%">
                      <el-option v-for="t in itemTypeOptions" :key="t.value" :label="t.label" :value="t.value" />
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="HIS费用代码">
                    <el-input v-model="itemForm.hisFydm" placeholder="HIS费用代码" @keyup.enter="handleHisFeeSearch" />
                  </el-form-item>
                </el-col>
              </el-row>

              <el-row :gutter="10">
                <el-col :span="12">
                  <el-form-item label="知识库代码">
                    <el-input v-model="itemForm.zskXmdm" readonly placeholder="知识库代码" />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="知识库名称">
                    <el-input v-model="itemForm.zskXmmc" placeholder="知识库名称" @keyup.enter="focusNextField('lcyy')" />
                  </el-form-item>
                </el-col>
              </el-row>

              <el-form-item label="临床意义">
                <el-input v-model="itemForm.lcyy" type="textarea" :rows="2" placeholder="输入临床意义" />
              </el-form-item>

              <el-row :gutter="10">
                <el-col :span="6">
                  <el-checkbox v-model="itemForm.jsbz" :true-value="1" :false-value="0">计算标志</el-checkbox>
                </el-col>
                <el-col :span="6">
                  <el-checkbox v-model="itemForm.dybz" :true-value="1" :false-value="0">打印标志</el-checkbox>
                </el-col>
                <el-col :span="6">
                  <el-checkbox v-model="itemForm.tybz" :true-value="1" :false-value="0">停用标志</el-checkbox>
                </el-col>
                <el-col :span="6">
                  <el-checkbox v-model="itemForm.zsbz" :true-value="1" :false-value="0">注释标志</el-checkbox>
                </el-col>
              </el-row>
            </el-form>

            <!-- 操作按钮 -->
            <div class="button-bar">
              <el-button size="small" type="primary" @click="handleItemAdd">新增</el-button>
              <el-button size="small" type="primary" @click="handleItemUpdate" :disabled="!selectedItem">修改</el-button>
              <el-button size="small" type="danger" @click="handleItemDelete" :disabled="!selectedItem">删除</el-button>
              <el-button size="small" @click="handleItemCancel" :disabled="!editing">取消</el-button>
              <el-button size="small" type="success" @click="handleItemSave" :disabled="!editing">保存</el-button>
              <el-button size="small" @click="handleItemClose">关闭</el-button>
            </div>
          </div>
        </div>

        <!-- HIS费用代码搜索结果（浮动） -->
        <el-table
          v-if="hisFeeResults.length > 0"
          :data="hisFeeResults"
          border
          size="small"
          height="120"
          highlight-current-row
          class="his-fee-grid"
          @row-dblclick="onHisFeeSelect"
        >
          <el-table-column prop="fydm" label="费用代码" width="100" />
          <el-table-column prop="fymc" label="费用名称" />
          <el-table-column prop="sfbz" label="收费标准" width="100" />
        </el-table>
      </el-tab-pane>

      <!-- Tab2: 项目组合设置 -->
      <el-tab-pane label="项目组合设置" name="tab2">
        <div class="tab2-wrapper">
          <!-- 左侧：组合列表 -->
          <div class="combo-list-panel">
            <div class="panel-header">
              <span>组合列表</span>
              <el-button size="small" type="success" @click="handleComboAdd">新增</el-button>
            </div>
            <div class="search-bar">
              <el-input v-model="comboSearchKeyword" placeholder="搜索组合名称/拼音码" size="small" clearable @input="searchComboList">
                <template #prefix>
                  <el-icon><Search /></el-icon>
                </template>
              </el-input>
            </div>
            <el-table
              :data="comboList"
              border
              stripe
              size="small"
              height="420"
              highlight-current-row
              @current-change="onComboSelect"
            >
              <el-table-column prop="zhid" label="ID" width="60" />
              <el-table-column prop="zhmc" label="组合名称" min-width="120" />
              <el-table-column prop="pym" label="拼音码" width="80" />
              <el-table-column prop="qybz" label="启用" width="60">
                <template #default="{row}">
                  <el-tag :type="row.qybz ? 'success' : 'info'" size="small">{{ row.qybz ? '是' : '否' }}</el-tag>
                </template>
              </el-table-column>
            </el-table>
          </div>

          <!-- 右侧：组合详情 -->
          <div class="combo-detail-panel">
            <!-- 组合基本信息卡片 -->
            <div class="detail-card">
              <div class="card-header">
                <span class="card-title">组合基本信息</span>
                <div class="card-actions">
                  <el-button size="small" type="primary" @click="editingCombo = true" :disabled="!selectedCombo || editingCombo">修改</el-button>
                  <el-button size="small" type="danger" @click="handleComboDelete" :disabled="!selectedCombo">删除</el-button>
                </div>
              </div>
              <div class="card-body">
                <el-form :model="comboForm" label-width="90px" size="small" :disabled="!editingCombo">
                  <el-row :gutter="16">
                    <el-col :span="12">
                      <el-form-item label="组合名称" required>
                        <el-input v-model="comboForm.zhmc" placeholder="组合名称" />
                      </el-form-item>
                    </el-col>
                    <el-col :span="12">
                      <el-form-item label="拼音码">
                        <el-input v-model="comboForm.pym" placeholder="拼音码" />
                      </el-form-item>
                    </el-col>
                  </el-row>
                  <el-row :gutter="16">
                    <el-col :span="12">
                      <el-form-item label="标本类型">
                        <el-select v-model="comboForm.bbzl" placeholder="选择标本类型" filterable clearable style="width: 100%">
                          <el-option v-for="st in specimenTypes" :key="st.bm" :label="st.bmsm" :value="st.bm" />
                        </el-select>
                      </el-form-item>
                    </el-col>
                    <el-col :span="12">
                      <el-form-item label="启用状态">
                        <el-switch v-model="comboForm.qybz" :active-value="1" :inactive-value="0" />
                      </el-form-item>
                    </el-col>
                  </el-row>
                  <el-row :gutter="16">
                    <el-col :span="8">
                      <el-form-item label="收费标准">
                        <el-input-number v-model="comboForm.sfbz" :min="0" :precision="2" style="width: 100%" />
                      </el-form-item>
                    </el-col>
                    <el-col :span="8">
                      <el-form-item label="工作量">
                        <el-input-number v-model="comboForm.gzl" :min="0" :precision="2" style="width: 100%" />
                      </el-form-item>
                    </el-col>
                    <el-col :span="8">
                      <el-form-item label="显示颜色">
                        <div class="color-picker-row">
                          <el-input
                            v-model="comboForm.bqys"
                            type="color"
                            style="width: 40px; height: 32px; padding: 0; border: 1px solid #dcdfe6; cursor: pointer;"
                            :disabled="!editingCombo"
                          />
                          <span v-if="!editingCombo" class="color-preview" :style="{ backgroundColor: comboForm.bqys || '#FF0000' }">
                            {{ comboForm.yssm || '无' }}
                          </span>
                          <el-input v-else v-model="comboForm.yssm" placeholder="颜色说明" style="flex: 1" />
                        </div>
                      </el-form-item>
                    </el-col>
                  </el-row>
                </el-form>
                <div v-if="editingCombo" class="form-actions">
                  <el-button size="small" type="primary" @click="handleComboSave">保存</el-button>
                  <el-button size="small" @click="handleComboCancel">取消</el-button>
                </div>
              </div>
            </div>

            <!-- 组合明细卡片 -->
            <div class="detail-card">
              <div class="card-header">
                <span class="card-title">组合所含项目 [{{ comboItems.length }}项]</span>
                <div class="card-actions">
                  <el-button size="small" type="success" @click="showAddItemToComboDialog = true" :disabled="!selectedCombo">添加项目</el-button>
                  <el-button size="small" @click="handleRemoveComboItem" :disabled="!selectedComboItem">移除</el-button>
                  <el-button size="small" @click="handleComboItemUp" :disabled="!selectedComboItem || selectedComboItemIndex === 0">上移</el-button>
                  <el-button size="small" @click="handleComboItemDown" :disabled="!selectedComboItem || selectedComboItemIndex === comboItems.length - 1">下移</el-button>
                </div>
              </div>
              <div class="card-body">
                <el-table
                  :data="comboItems"
                  border
                  stripe
                  size="small"
                  height="180"
                  highlight-current-row
                  @row-click="onComboItemSelect"
                >
                  <el-table-column type="index" label="序" width="50" />
                  <el-table-column prop="xmid" label="项目ID" width="70" />
                  <el-table-column prop="xmdm" label="项目代码" width="80" />
                  <el-table-column prop="xmzwmc" label="项目名称" min-width="120" />
                  <el-table-column prop="xmywmc" label="英文名称" width="100" />
                  <el-table-column prop="pym" label="拼音码" width="80" />
                  <el-table-column prop="xmdw" label="单位" width="60" />
                </el-table>
              </div>
            </div>
          </div>
        </div>

        <!-- 添加项目到组合对话框 -->
        <el-dialog v-model="showAddItemToComboDialog" title="添加项目到组合" width="500px" append-to-body>
          <el-input v-model="itemSearchKeyword" placeholder="输入拼音码搜索项目" size="small" clearable />
          <el-table
            :data="itemSearchResults"
            border
            stripe
            size="small"
            height="300"
            highlight-current-row
            @row-click="onItemSearchResultSelect"
            style="margin-top: 10px"
          >
            <el-table-column prop="xmid" label="ID" width="60" />
            <el-table-column prop="xmdm" label="代码" width="80" />
            <el-table-column prop="xmzwmc" label="名称" />
            <el-table-column prop="pym" label="拼音码" width="80" />
          </el-table>
          <template #footer>
            <el-button @click="showAddItemToComboDialog = false">取消</el-button>
            <el-button type="primary" @click="confirmAddItemToCombo" :disabled="!selectedItemForCombo">添加</el-button>
          </template>
        </el-dialog>
      </el-tab-pane>

      <!-- Tab3: 仪器项目组合 -->
      <el-tab-pane label="仪器项目组合" name="tab3">
        <div class="tab3-tree-wrapper">
          <!-- 左侧树形结构 -->
          <div class="tree-panel">
            <div class="panel-header">
              <span>仪器结构</span>
              <el-input v-model="instSearchKeyword" placeholder="搜索仪器" size="small" clearable style="width: 140px;" />
            </div>
            <el-tree
              ref="comboTreeRef"
              :data="filteredInstComboTree"
              :props="{ label: 'label', children: 'children' }"
              node-key="id"
              default-expand-all
              highlight-current
              :expand-on-click-node="false"
              @node-click="onComboTreeNodeClick"
            >
              <template #default="{ node, data }">
                <span v-if="data.type === 'item'" style="display:none;"></span>
                <span v-else class="tree-node">
                  <span class="node-icon">
                    <el-tag v-if="data.type === 'instrument'" type="primary" size="small">仪</el-tag>
                    <el-tag v-else-if="data.type === 'combo'" type="success" size="small">组</el-tag>
                  </span>
                  <span class="node-label">{{ node.label }}</span>
                  <span v-if="data.type === 'combo'" class="node-count">({{ data.itemCount || 0 }})</span>
                </span>
              </template>
            </el-tree>
          </div>

          <!-- 右侧详情面板 -->
          <div class="detail-panel">
            <!-- 已选仪器信息 -->
            <div class="detail-card" v-if="selectedInstForCombo">
              <div class="card-header">
                <span class="card-title">当前仪器</span>
              </div>
              <div class="card-body">
                <div class="info-row">
                  <span class="info-label">仪器：</span>
                  <span class="info-value">{{ instrumentList.find(i => i.sbDjid === selectedInstForCombo)?.label }}</span>
                </div>
                <div class="info-row" v-if="selectedComboInTree">
                  <span class="info-label">已选组合：</span>
                  <span class="info-value">{{ selectedComboInTree.label }}</span>
                </div>
                <div class="info-row">
                  <span class="info-label">类型：</span>
                  <span class="info-value">{{ selectedComboInTree?.type || '-' }}</span>
                </div>
              </div>
            </div>

            <!-- 组合操作 -->
            <div class="detail-card" v-if="selectedComboInTree && selectedComboInTree.type === 'combo'">
              <div class="card-header">
                <span class="card-title">组合操作</span>
              </div>
              <div class="card-body">
                <div class="action-buttons">
                  <el-button size="small" type="danger" @click="handleRemoveInstCombo">移除组合</el-button>
                </div>
              </div>
            </div>

            <!-- 组合项目列表 -->
            <div class="detail-card combo-items-card" v-if="selectedComboInTree && selectedComboInTree.type === 'combo' && selectedComboInTree.items">
              <div class="card-header">
                <span class="card-title">包含项目 ({{ selectedComboInTree.itemCount || 0 }})</span>
              </div>
              <div class="card-body items-list-body">
                <el-table
                  :data="selectedComboInTree.items"
                  border
                  size="small"
                  max-height="350"
                  style="width: 100%;"
                >
                  <el-table-column prop="xmdm" label="代码" width="90" />
                  <el-table-column prop="label" label="项目名称" min-width="150" />
                  <el-table-column prop="xmdw" label="单位" width="70" />
                </el-table>
              </div>
            </div>

            <!-- 仪器操作 -->
            <div class="detail-card" v-if="selectedInstForCombo && (!selectedComboInTree || selectedComboInTree.type === 'instrument')">
              <div class="card-header">
                <span class="card-title">仪器操作</span>
              </div>
              <div class="card-body">
                <div class="action-buttons">
                  <el-button size="small" type="success" @click="openAddInstComboDialog">添加组合</el-button>
                </div>
              </div>
            </div>

            <!-- 项目操作 -->
            <div class="detail-card" v-if="selectedItemInTree && selectedItemInTree.type === 'item'">
              <div class="card-header">
                <span class="card-title">项目信息</span>
              </div>
              <div class="card-body">
                <div class="info-row">
                  <span class="info-label">项目ID：</span>
                  <span class="info-value">{{ selectedItemInTree.xmid }}</span>
                </div>
                <div class="info-row">
                  <span class="info-label">项目代码：</span>
                  <span class="info-value">{{ selectedItemInTree.xmdm || '-' }}</span>
                </div>
                <div class="info-row">
                  <span class="info-label">项目名称：</span>
                  <span class="info-value">{{ selectedItemInTree.xmzwmc }}</span>
                </div>
                <div class="info-row">
                  <span class="info-label">拼音码：</span>
                  <span class="info-value">{{ selectedItemInTree.pym || '-' }}</span>
                </div>
                <div class="action-buttons" style="margin-top: 12px;">
                  <el-button size="small" type="danger" @click="handleRemoveInstItem">移除项目</el-button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 添加项目到仪器对话框 -->
        <el-dialog v-model="showAddInstItemDialog" title="添加项目到仪器" width="500px" append-to-body>
          <el-input v-model="instItemSearchKeyword" placeholder="输入拼音码搜索项目" size="small" clearable />
          <el-table
            :data="instItemSearchResults"
            border
            stripe
            size="small"
            height="300"
            highlight-current-row
            @row-click="onInstItemSearchSelect"
            style="margin-top: 10px"
          >
            <el-table-column prop="xmid" label="ID" width="60" />
            <el-table-column prop="xmdm" label="代码" width="80" />
            <el-table-column prop="xmzwmc" label="名称" />
            <el-table-column prop="pym" label="拼音码" width="80" />
          </el-table>
          <template #footer>
            <el-button @click="showAddInstItemDialog = false">取消</el-button>
            <el-button type="primary" @click="confirmAddInstItem" :disabled="!selectedItemForInst">添加</el-button>
          </template>
        </el-dialog>

        <!-- 组合详情对话框 -->
        <el-dialog v-model="showComboDetailDialog" title="组合详情" width="600px" append-to-body>
          <el-descriptions :column="2" border v-if="selectedComboDetail">
            <el-descriptions-item label="组合ID">{{ selectedComboDetail.zhid }}</el-descriptions-item>
            <el-descriptions-item label="拼音码">{{ selectedComboDetail.pym || '-' }}</el-descriptions-item>
            <el-descriptions-item label="组合名称" :span="2">{{ selectedComboDetail.label || selectedComboDetail.zhmc }}</el-descriptions-item>
          </el-descriptions>
          <div v-if="selectedComboDetail && selectedComboDetail.children && selectedComboDetail.children.length > 0" style="margin-top: 20px;">
            <div class="panel-header">包含项目</div>
            <el-table :data="selectedComboDetail.children" border size="small" max-height="300">
              <el-table-column prop="xmdm" label="项目代码" width="100" />
              <el-table-column prop="xmzwmc" label="项目名称" min-width="150" />
              <el-table-column prop="pym" label="拼音码" width="100" />
            </el-table>
          </div>
          <template #footer>
            <el-button @click="showComboDetailDialog = false">关闭</el-button>
          </template>
        </el-dialog>

        <!-- 添加组合到仪器对话框 -->
        <el-dialog v-model="showAddInstComboDialog" title="添加组合到仪器" width="500px" append-to-body>
          <el-select v-model="selectedComboToAdd" placeholder="搜索并选择组合" style="width: 100%" filterable clearable>
            <el-option v-for="c in unassignedCombos" :key="c.zhid" :label="c.zhmc" :value="c.zhid" />
          </el-select>
          <template #footer>
            <el-button @click="showAddInstComboDialog = false">取消</el-button>
            <el-button type="primary" @click="confirmAddInstCombo" :disabled="!selectedComboToAdd">添加</el-button>
          </template>
        </el-dialog>
      </el-tab-pane>

      <!-- Tab4: 仪器所属项目 -->
      <el-tab-pane label="仪器所属项目" name="tab4">
        <div class="tab4-container">
          <div class="left-tree-panel">
            <div class="panel-header">仪器 → 项目</div>
            <el-tree
              :data="instrumentItemTree"
              :props="{ label: 'label', children: 'children' }"
              node-key="id"
              default-expand-all
              highlight-current
              @node-click="onInstItemNodeClick"
            />
          </div>

          <div class="right-ref-panel" v-if="selectedInstItem">
            <div class="panel-header">{{ selectedInstItem.xmzwmc }} ({{ selectedInstItem.xmdm }}) - 仪器项目设置</div>
            <el-form :model="instItemDetail" label-width="80px" size="small" class="inst-item-form">
              <el-row :gutter="10">
                <el-col :span="6">
                  <el-form-item label="通道值">
                    <el-input v-model="instItemDetail.xmbm" placeholder="通道值" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item label="系数">
                    <el-input-number v-model="instItemDetail.xs" :min="0" :precision="4" style="width:100%" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item label="单位">
                    <el-input v-model="instItemDetail.yqxmdw" placeholder="单位" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item label="检测方法">
                    <el-input v-model="instItemDetail.xmjc" placeholder="检测方法" />
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row :gutter="10">
                <el-col :span="6">
                  <el-form-item label="计算标志">
                    <el-switch v-model="instItemDetail.jsbz" :active-value="1" :inactive-value="0" />
                  </el-form-item>
                </el-col>
                <el-col :span="18">
                  <el-form-item label="操作">
                    <el-button size="small" type="primary" @click="handleSaveInstItem">保存</el-button>
                    <el-button size="small" @click="handleOpenFormula">公式设置</el-button>
                    <el-button size="small" @click="handleOpenDataReplace">数据替换</el-button>
                  </el-form-item>
                </el-col>
              </el-row>
            </el-form>

            <el-divider content-position="left">参考值设置</el-divider>
            <el-table :data="refRanges" border stripe size="small" max-height="200">
              <el-table-column prop="bbsgbz" label="标本质控" width="80">
                <template #default="{row}">{{ row.bbsgbz ? '是' : '否' }}</template>
              </el-table-column>
              <el-table-column prop="bbzl" label="标本质种" width="80" />
              <el-table-column prop="xbsgbz" label="性别相关" width="80">
                <template #default="{row}">{{ row.xbsgbz ? '是' : '否' }}</template>
              </el-table-column>
              <el-table-column prop="brxb" label="性别" width="60">
                <template #default="{row}">{{ {0:'全部',1:'男',2:'女'}[row.brxb] || '全部' }}</template>
              </el-table-column>
              <el-table-column prop="nlsgbz" label="年龄相关" width="80">
                <template #default="{row}">{{ row.nlsgbz ? '是' : '否' }}</template>
              </el-table-column>
              <el-table-column prop="nllx" label="年龄类型" width="80" />
              <el-table-column prop="nlsx" label="年龄起始" width="70" />
              <el-table-column prop="nlxx" label="年龄结束" width="70" />
              <el-table-column prop="ckz" label="参考范围" min-width="100" />
              <el-table-column prop="ckzdx" label="参考低" width="70" />
              <el-table-column prop="ckzgx" label="参考高" width="70" />
              <el-table-column prop="bjzdx" label="报警低" width="70" />
              <el-table-column prop="bjzgx" label="报警高" width="70" />
              <el-table-column label="操作" width="100">
                <template #default="{row}">
                  <el-button link type="primary" size="small" @click="handleEditRefRange(row)">编辑</el-button>
                  <el-button link type="danger" size="small" @click="handleDeleteRefRange(row)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
            <div style="margin-top: 10px">
              <el-button size="small" type="success" @click="handleAddRefRange">新增参考范围</el-button>
            </div>

            <el-divider content-position="left">默认值设置</el-divider>
            <el-table :data="defaultValues" border stripe size="small" max-height="150">
              <el-table-column prop="mrz" label="默认值" min-width="200" />
              <el-table-column prop="mr" label="默认标志" width="80">
                <template #default="{row}">{{ row.mr ? '是' : '否' }}</template>
              </el-table-column>
              <el-table-column prop="tsbz" label="提示标志" width="80">
                <template #default="{row}">{{ row.tsbz ? '是' : '否' }}</template>
              </el-table-column>
              <el-table-column label="操作" width="120">
                <template #default="{row}">
                  <el-button link type="primary" size="small" @click="handleEditDefault">编辑</el-button>
                  <el-button link type="danger" size="small" @click="handleSaveDefault">保存</el-button>
                </template>
              </el-table-column>
            </el-table>

            <!-- 默认值编辑对话框 -->
            <el-dialog v-model="defaultFormVisible" title="编辑默认值" width="400px" append-to-body>
              <el-form :model="defaultForm" label-width="80px" size="small">
                <el-form-item label="默认值" required>
                  <el-input v-model="defaultForm.mrz" type="textarea" :rows="3" placeholder="输入默认值" />
                </el-form-item>
                <el-form-item label="默认标志">
                  <el-switch v-model="defaultForm.mr" :active-value="true" :inactive-value="false" />
                </el-form-item>
                <el-form-item label="提示标志">
                  <el-switch v-model="defaultForm.tsbz" :active-value="true" :inactive-value="false" />
                </el-form-item>
              </el-form>
              <template #footer>
                <el-button @click="defaultFormVisible = false">取消</el-button>
                <el-button type="primary" @click="handleSaveDefault">保存</el-button>
              </template>
            </el-dialog>

            <!-- 数据替换设置对话框 -->
            <el-dialog v-model="dataReplaceVisible" title="数据替换设置" width="600px" append-to-body>
              <div style="margin-bottom: 10px;">
                <el-button size="small" type="primary" @click="handleAddDataReplace">新增</el-button>
              </div>
              <el-table :data="dataReplaceList" border stripe size="small" max-height="300">
                <el-table-column prop="originalValue" label="原始值" width="200" />
                <el-table-column prop="replaceValue" label="替换值" width="200" />
                <el-table-column label="操作" width="120">
                  <template #default="{row}">
                    <el-button link type="primary" size="small" @click="handleEditDataReplace(row)">编辑</el-button>
                    <el-button link type="danger" size="small" @click="handleDeleteDataReplace(row)">删除</el-button>
                  </template>
                </el-table-column>
              </el-table>
              <el-divider v-if="dataReplaceEditMode" content-position="left">编辑</el-divider>
              <el-form v-if="dataReplaceEditMode" :model="dataReplaceForm" label-width="80px" size="small" style="margin-top: 10px;">
                <el-row :gutter="10">
                  <el-col :span="12">
                    <el-form-item label="原始值" required>
                      <el-input v-model="dataReplaceForm.originalValue" placeholder="输入原始值" />
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="替换值" required>
                      <el-input v-model="dataReplaceForm.replaceValue" placeholder="输入替换值" />
                    </el-form-item>
                  </el-col>
                </el-row>
                <el-row>
                  <el-col :span="24" style="text-align: right;">
                    <el-button size="small" @click="handleCancelDataReplace">取消</el-button>
                    <el-button size="small" type="primary" @click="handleSaveDataReplace">保存</el-button>
                  </el-col>
                </el-row>
              </el-form>
            </el-dialog>

            <!-- 公式设置对话框 -->
            <FormulaSettingDialog
              v-model="formulaDialogVisible"
              :sb-djid="selectedInstItem?.sbDjid"
              :xmid="selectedInstItem?.xmid"
              :item-name="selectedInstItem?.xmzwmc"
              :instrument-name="selectedInstItem?.sbmc"
              @saved="handleFormulaSaved"
            />

            <!-- 参考范围编辑对话框 -->
            <el-dialog v-model="refFormVisible" :title="refForm.id ? '编辑参考范围' : '新增参考范围'" width="700px" append-to-body>
              <el-form :model="refForm" label-width="90px" size="small">
                <el-row :gutter="8">
                  <el-col :span="8">
                    <el-form-item label="标本质控">
                      <el-switch v-model="refForm.bbsgbz" :active-value="1" :inactive-value="0" />
                    </el-form-item>
                  </el-col>
                  <el-col :span="8">
                    <el-form-item label="标本质种">
                      <el-input v-model="refForm.bbzl" />
                    </el-form-item>
                  </el-col>
                  <el-col :span="8">
                    <el-form-item label="性别相关">
                      <el-switch v-model="refForm.xbsgbz" :active-value="1" :inactive-value="0" />
                    </el-form-item>
                  </el-col>
                </el-row>
                <el-row :gutter="8">
                  <el-col :span="8">
                    <el-form-item label="性别">
                      <el-select v-model="refForm.brxb" style="width:100%">
                        <el-option :value="0" label="全部" />
                        <el-option :value="1" label="男" />
                        <el-option :value="2" label="女" />
                      </el-select>
                    </el-form-item>
                  </el-col>
                  <el-col :span="8">
                    <el-form-item label="年龄分层">
                      <el-switch v-model="refForm.nlsgbz" :active-value="1" :inactive-value="0" />
                    </el-form-item>
                  </el-col>
                  <el-col :span="8" v-if="refForm.nlsgbz">
                    <el-form-item label="年龄类型">
                      <el-input v-model="refForm.nllx" />
                    </el-form-item>
                  </el-col>
                </el-row>
                <el-row :gutter="8" v-if="refForm.nlsgbz">
                  <el-col :span="12">
                    <el-form-item label="年龄起始"><el-input-number v-model="refForm.nlsx" :min="0" style="width:100%" /></el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="年龄结束"><el-input-number v-model="refForm.nlxx" :min="0" style="width:100%" /></el-form-item>
                  </el-col>
                </el-row>
                <el-row :gutter="8">
                  <el-col :span="12">
                    <el-form-item label="参考低值"><el-input v-model="refForm.ckzdx" /></el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="参考高值"><el-input v-model="refForm.ckzgx" /></el-form-item>
                  </el-col>
                </el-row>
                <el-form-item label="参考文本"><el-input v-model="refForm.ckz" /></el-form-item>
                <el-row :gutter="8">
                  <el-col :span="12">
                    <el-form-item label="报警低值"><el-input v-model="refForm.bjzdx" /></el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="报警高值"><el-input v-model="refForm.bjzgx" /></el-form-item>
                  </el-col>
                </el-row>
                <el-divider content-position="left">复查/审核设置</el-divider>
                <el-row :gutter="8">
                  <el-col :span="8">
                    <el-form-item label="复查提示">
                      <el-switch v-model="refForm.jgfctsbz" :active-value="1" :inactive-value="0" />
                    </el-form-item>
                  </el-col>
                  <el-col :span="8">
                    <el-form-item label="自动审核">
                      <el-switch v-model="refForm.zdshbz" :active-value="1" :inactive-value="0" />
                    </el-form-item>
                  </el-col>
                </el-row>
                <el-row :gutter="8">
                  <el-col :span="12">
                    <el-form-item label="即时结果低"><el-input v-model="refForm.jszdx" /></el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="即时结果高"><el-input v-model="refForm.jszgx" /></el-form-item>
                  </el-col>
                </el-row>
                <el-row :gutter="8">
                  <el-col :span="12">
                    <el-form-item label="复查结果低"><el-input v-model="refForm.fczdx" /></el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="复查结果高"><el-input v-model="refForm.fczgx" /></el-form-item>
                  </el-col>
                </el-row>
                <el-row :gutter="8">
                  <el-col :span="12">
                    <el-form-item label="审核低限"><el-input v-model="refForm.zdshdx" /></el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="审核高限"><el-input v-model="refForm.zdshgx" /></el-form-item>
                  </el-col>
                </el-row>
                <el-form-item label="审核常用区间"><el-input v-model="refForm.zdshcyqj" placeholder="如: 18-60" /></el-form-item>
              </el-form>
              <template #footer>
                <el-button @click="refFormVisible = false">取消</el-button>
                <el-button type="primary" @click="handleSaveRefRange">保存</el-button>
              </template>
            </el-dialog>
          </div>

          <div class="right-ref-panel" v-else style="display: flex; align-items: center; justify-content: center; color: #909399;">
            选择仪器下的项目
          </div>
        </div>
      </el-tab-pane>

      <!-- Tab5: 项目时间设置 -->
      <el-tab-pane label="项目时间设置" name="tab5">
        <div class="tab5-container">
          <el-tabs v-model="tab5SubTab" type="border-card">
            <el-tab-pane label="TAT设置" name="tat">
              <div class="toolbar">
                <el-select v-model="filterInstForTat" placeholder="按仪器筛选" clearable style="width: 150px" @change="loadTatSettings">
                  <el-option v-for="i in instrumentList" :key="i.sbDjid" :label="i.label" :value="i.sbDjid" />
                </el-select>
                <el-button size="small" type="success" @click="handleTatAdd">新增</el-button>
                <el-button size="small" @click="handleAutoCalculateTat">自动计算</el-button>
              </div>

              <el-table :data="tatSettings" border stripe size="small" max-height="350">
                <el-table-column prop="sbDjid" label="设备ID" width="70" />
                <el-table-column prop="sbmc" label="仪器名称" width="120" />
                <el-table-column prop="brlb" label="病人类型" width="70">
                  <template #default="{row}">{{ {1:'门诊',2:'住院',3:'体检',4:'其他'}[row.brlb] || row.brlb }}</template>
                </el-table-column>
                <el-table-column prop="syqk" label="紧急类型" width="80">
                  <template #default="{row}">{{ {0:'普通',1:'常规',2:'急诊',3:'特急',4:'即时'}[row.syqk] || row.syqk }}</template>
                </el-table-column>
                <el-table-column prop="zhmc" label="组合名称" min-width="150" />
                <el-table-column prop="TAT" label="TAT(分)" width="80" />
                <el-table-column label="操作" width="100">
                  <template #default="{row}">
                    <el-button link type="primary" size="small" @click="handleEditTatSetting(row)">编辑</el-button>
                    <el-button link type="danger" size="small" @click="handleDeleteTatSetting(row)">删除</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-tab-pane>

            <el-tab-pane label="完成时间设置" name="completion">
              <div class="toolbar">
                <el-button size="small" type="primary" @click="completionTimeDialogVisible = true">项目完成时间设置</el-button>
              </div>
            </el-tab-pane>
          </el-tabs>
        </div>

        <!-- TAT编辑对话框 -->
        <el-dialog v-model="tatFormVisible" :title="tatForm._edit ? '编辑TAT' : '新增TAT'" width="500px" append-to-body>
          <el-form :model="tatForm" label-width="90px" size="small">
            <el-form-item label="仪器" required>
              <el-select v-model="tatForm.sbDjid" style="width:100%">
                <el-option v-for="i in instrumentList" :key="i.sbDjid" :label="i.label" :value="i.sbDjid" />
              </el-select>
            </el-form-item>
            <el-form-item label="病人类型" required>
              <el-select v-model="tatForm.brlb" style="width:100%">
                <el-option :value="1" label="门诊" />
                <el-option :value="2" label="住院" />
                <el-option :value="3" label="体检" />
                <el-option :value="4" label="其他" />
              </el-select>
            </el-form-item>
            <el-form-item label="紧急类型" required>
              <el-select v-model="tatForm.syqk" style="width:100%">
                <el-option :value="0" label="普通" />
                <el-option :value="1" label="常规" />
                <el-option :value="2" label="急诊" />
                <el-option :value="3" label="特急" />
                <el-option :value="4" label="即时" />
              </el-select>
            </el-form-item>
            <el-form-item label="组合" required>
              <el-select v-model="tatForm.zhid" filterable style="width:100%" @change="onTatComboChange">
                <el-option v-for="c in comboOptions" :key="c.zhid" :label="c.zhmc" :value="c.zhid" />
              </el-select>
            </el-form-item>
            <el-form-item label="TAT(分钟)" required>
              <el-input-number v-model="tatForm.TAT" :min="1" style="width:100%" />
            </el-form-item>
          </el-form>
          <template #footer>
            <el-button @click="tatFormVisible = false">取消</el-button>
            <el-button type="primary" @click="handleSaveTatSetting">保存</el-button>
          </template>
        </el-dialog>

        <!-- 项目完成时间设置对话框 -->
        <CompletionTimeSettingDialog v-model="completionTimeDialogVisible" />
      </el-tab-pane>

      <!-- Tab6: 批量系数设置 -->
      <el-tab-pane label="批量系数设置" name="tab6">
        <div class="tab6-container">
          <div class="left-table-panel">
            <div class="panel-header">仪器列表</div>
            <el-table
              :data="instrumentList"
              border
              stripe
              size="small"
              highlight-current-row
              @current-change="onInstForCoeffSelect"
              max-height="450"
            >
              <el-table-column prop="sbDjid" label="ID" width="60" />
              <el-table-column prop="label" label="仪器名称" />
            </el-table>
          </div>

          <div class="right-coeff-panel">
            <div class="panel-header">
              <span>项目系数 - {{ selectedInstForCoeff?.label || '请选择仪器' }}</span>
              <el-button type="primary" size="small" @click="showBatchSetCoeffDialog" :disabled="!selectedInstForCoeff">批量设置系数</el-button>
            </div>
            <el-table :data="coeffItems" border stripe size="small" max-height="350">
              <el-table-column prop="xmdm" label="项目代码" width="80" />
              <el-table-column prop="xmzwmc" label="项目名称" width="150" />
              <el-table-column prop="xmdw" label="单位" width="60" />
              <el-table-column prop="xs" label="系数" width="100">
                <template #default="{row}">
                  <el-input-number v-model="row.xs" :min="0" :max="100" :precision="4" size="small" style="width: 90px" />
                </template>
              </el-table-column>
              <el-table-column label="操作" width="80">
                <template #default="{row}">
                  <el-button link type="primary" size="small" @click="handleResetCoeff(row)">重置</el-button>
                </template>
              </el-table-column>
            </el-table>
            <div v-if="selectedInstForCoeff" style="margin-top: 12px; text-align: right;">
              <el-button type="primary" @click="handleSaveAllCoeff">保存全部</el-button>
            </div>
          </div>
        </div>

        <!-- 批量设置系数对话框 -->
        <el-dialog v-model="showBatchSetCoeff" title="批量设置系数" width="350px" append-to-body>
          <el-form :model="batchCoeffForm" label-width="80px" size="small">
            <el-form-item label="系数值" required>
              <el-input-number v-model="batchCoeffForm.xs" :min="0" :max="100" :precision="4" style="width: 100%" />
            </el-form-item>
          </el-form>
          <template #footer>
            <el-button @click="showBatchSetCoeff = false">取消</el-button>
            <el-button type="primary" @click="handleApplyBatchCoeff">应用</el-button>
          </template>
        </el-dialog>
      </el-tab-pane>
    </el-tabs>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick, watch, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import FormulaSettingDialog from './FormulaSettingDialog.vue'
import CompletionTimeSettingDialog from './CompletionTimeSettingDialog.vue'
import {
  fetchTestItems, searchTestItems, saveTestItem, deleteTestItem,
  fetchTestItemTypes, fetchPrecisions,
  fetchCombos, saveCombo as saveComboApi, deleteCombo as deleteComboApi,
  fetchComboItems, addComboItem, removeComboItem, reorderComboItems,
  fetchInstrumentComboTree, assignCombo, removeInstrumentCombo, fetchUnassignedCombos,
  fetchInstrumentItems, addInstrumentItem, removeInstrumentItem, saveInstrumentItem,
  fetchInstrumentItemTree, fetchRefRanges as fetchRefRangesApi, saveRefRange as saveRefRangeApi,
  deleteRefRange as deleteRefRangeApi, batchSaveCoeff,
  fetchDefault as fetchDefaultApi, saveDefault as saveDefaultApi,
  fetchDataReplaceSettings, saveDataReplaceSetting, deleteDataReplaceSetting,
  fetchTatSettings, saveTatSetting as saveTatSettingApi, deleteTatSetting as deleteTatSettingApi,
  autoCalculateTat as autoCalculateTatApi
} from '../../api/labItem'
import { fetchInstrumentCoefficients } from '../../api/instrumentItem'
import { fetchSpecimenTypes } from '../../api/specimenType'

const props = defineProps({ modelValue: Boolean })
const emit = defineEmits(['update:modelValue'])

const activeTab = ref('tab1')

// ============== Tab1: 检验项目设置 ==============
const searchKeyword = ref('')
const itemList = ref([])
const searchResults = ref([])
const selectedItem = ref(null)
const editing = ref(false)
const isNewItem = ref(false)
const hisFeeResults = ref([])

const precisionOptions = [
  { value: 1, label: '整数(0位小数)' },
  { value: 2, label: '1位小数' },
  { value: 3, label: '2位小数' },
  { value: 4, label: '3位小数' },
  { value: 5, label: '4位小数' }
]

const itemTypeOptions = [
  { value: 0, label: '常规' },
  { value: 1, label: '生化' },
  { value: 2, label: '免疫' },
  { value: 3, label: '血液' },
  { value: 4, label: '微生物' }
]

const itemForm = reactive({
  xmid: 0,
  xmzwmc: '',
  xmywmc: '',
  pym: '',
  qtdm: '',
  xmdw: '',
  xmjd: 3,
  itemType: 0,
  jsbz: 0,
  dybz: 1,
  tybz: 0,
  zsbz: 0,
  hisFydm: '',
  hisJyxmmc: '',
  zskXmdm: '',
  zskXmmc: '',
  lcyy: ''
})

const formRefs = reactive({})

// ============== Tab2: 项目组合设置 ==============
const comboSearchKeyword = ref('')
const comboList = ref([])
const selectedCombo = ref(null)
const selectedComboItem = ref(null)
const selectedComboItemIndex = ref(-1)
const comboItems = ref([])
const editingCombo = ref(false)
const showAddItemToComboDialog = ref(false)
const itemSearchKeyword = ref('')
const itemSearchResults = ref([])
const selectedItemForCombo = ref(null)
const specimenTypes = ref([])

watch(showAddItemToComboDialog, (val) => {
  if (val) {
    loadItemsForComboDialog()
  }
})

const allItemsForCombo = ref([])

const loadItemsForComboDialog = async () => {
  try {
    const { data } = await fetchTestItems({ keyword: '' })
    const items = Array.isArray(data) ? data : (Array.isArray(data?.data) ? data.data : [])
    allItemsForCombo.value = items
    itemSearchResults.value = items
  } catch (e) {}
}

watch(itemSearchKeyword, (val) => {
  if (!val) {
    itemSearchResults.value = allItemsForCombo.value
  } else {
    const q = val.toLowerCase()
    itemSearchResults.value = allItemsForCombo.value.filter(item =>
      item.pym?.toLowerCase().includes(q) ||
      item.xmzwmc?.toLowerCase().includes(q) ||
      item.xmdm?.toLowerCase().includes(q)
    )
  }
})

const comboForm = reactive({
  zhid: 0,
  zhmc: '',
  pym: '',
  bbzl: '',
  gzl: 1,
  sfbz: 0,
  qybz: 1,
  bqys: '#FF0000',
  yssm: ''
})

// ============== Tab3: 仪器项目组合 ==============
const instrumentComboTree = ref([])
const instrumentList = ref([])
const selectedInstForCombo = ref(null)
const selectedComboForAssign = ref(null)
const unassignedCombos = ref([])
const instItems = ref([])
const instCombos = ref([])
const selectedInstItemForRemove = ref(null)
const selectedComboForInst = ref(null)
const showAddInstItemDialog = ref(false)
const showAddInstComboDialog = ref(false)
const selectedItemForInst = ref(null)
const selectedComboToAdd = ref(null)

const filteredUnassignedCombos = computed(() => {
  if (!comboSearchKeyword.value) return unassignedCombos.value
  const keyword = comboSearchKeyword.value.toLowerCase()
  return unassignedCombos.value.filter(c => c.zhmc?.toLowerCase().includes(keyword))
})
const instItemSearchKeyword = ref('')
const instItemSearchResults = ref([])
const showComboDetailDialog = ref(false)
const selectedComboDetail = ref(null)
const selectedComboItems = ref([])
const selectedComboInTree = ref(null)
const selectedItemInTree = ref(null)
const comboTreeRef = ref(null)
const instSearchKeyword = ref('')

const filteredInstComboTree = computed(() => {
  if (!instSearchKeyword.value) return instrumentComboTree.value
  if (!instrumentComboTree.value?.length) return []
  const keyword = instSearchKeyword.value.toLowerCase()
  const result = []
  for (const inst of instrumentComboTree.value) {
    if (inst.label?.toLowerCase().includes(keyword)) {
      result.push({ ...inst, children: inst.children ? [...inst.children] : [] })
    }
  }
  console.log('filteredInstComboTree:', result.length, 'instruments')
  return result
})

const displayInstItems = computed(() => {
  if (selectedComboForInst.value && selectedComboItems.value.length > 0) {
    return selectedComboItems.value
  }
  return instItems.value
})

// ============== Tab4: 仪器所属项目 ==============
const instrumentItemTree = ref([])
const selectedInstItem = ref(null)
const instItemDetail = reactive({
  xmbm: '',
  xs: 1,
  yqxmdw: '',
  xmjc: '',
  jsbz: 0
})
const refRanges = ref([])
const refFormVisible = ref(false)
const refForm = reactive({
  id: null,
  xmid: 0,
  sbDjid: 0,
  bbsgbz: 0,
  bbzl: '',
  xbsgbz: 0,
  brxb: 0,
  nlsgbz: 0,
  nllx: '',
  nlsx: 0,
  nlxx: 0,
  ckz: '',
  ckzdx: '',
  ckzgx: '',
  bjzdx: '',
  bjzgx: '',
  jszgx: '',
  jszdx: '',
  fczgx: '',
  fczdx: '',
  zdshbz: 0,
  zdshgx: '',
  zdshdx: '',
  zdshcyqj: '',
  jgfctsbz: 0
})
const defaultValues = ref([])
const defaultFormVisible = ref(false)
const defaultForm = reactive({
  mrz: '',
  mr: false,
  tsbz: false
})

// ============== 数据替换设置 ==============
const dataReplaceVisible = ref(false)
const dataReplaceList = ref([])
const dataReplaceForm = reactive({
  id: null,
  sbDjid: 0,
  xmid: 0,
  originalValue: '',
  replaceValue: ''
})
const dataReplaceEditMode = ref(false)

// ============== 公式设置 ==============
const formulaDialogVisible = ref(false)

// ============== Tab5: 项目时间设置 ==============
const tab5SubTab = ref('tat')
const filterInstForTat = ref('')
const tatSettings = ref([])
const tatFormVisible = ref(false)
const tatForm = reactive({
  sbDjid: '',
  brlb: 1,
  syqk: 1,
  zhid: '',
  zhmc: '',
  TAT: 60,
  _edit: false
})
const comboOptions = ref([])

// ============== Tab6: 批量系数设置 ==============
const selectedInstForCoeff = ref(null)
const coeffItems = ref([])
const showBatchSetCoeff = ref(false)
const batchCoeffForm = ref({ xs: 1 })

// ============== 项目完成时间设置 ==============
const completionTimeDialogVisible = ref(false)

// ============== 通用方法 ==============
const focusNextField = (fieldName) => {
  nextTick(() => {
    if (formRefs[fieldName]) {
      formRefs[fieldName].focus()
    }
  })
}

const loadInstruments = async () => {
  try {
    const { data } = await fetchInstrumentItemTree()
    instrumentList.value = Array.isArray(data) ? data.map(i => ({ sbDjid: i.sbDjid || i.sb_djid, label: i.label || i.sbmc })) : []
    instrumentComboTree.value = Array.isArray(data) ? data : []
    instrumentItemTree.value = Array.isArray(data) ? data : []
  } catch (e) { ElMessage.error('加载仪器列表失败') }
}

const loadItems = async (keyword = '') => {
  try {
    const { data } = await fetchTestItems({ keyword })
    itemList.value = Array.isArray(data) ? data : []
  } catch (e) {}
}

const handleSearch = async () => {
  if (!searchKeyword.value) {
    loadItems()
    return
  }
  try {
    const { data } = await searchTestItems(searchKeyword.value)
    const results = Array.isArray(data) ? data : []
    searchResults.value = results
    if (results.length === 1) {
      selectSearchResult(results[0])
      searchResults.value = []
    }
  } catch (e) {}
}

const selectSearchResult = (item) => {
  const found = itemList.value.find(i => i.xmid === item.xmid)
  if (found) {
    onItemRowSelect(found)
  } else {
    itemList.value = [item]
    onItemRowSelect(item)
  }
  searchResults.value = []
}

const onSearchResultDblClick = (row) => {
  selectSearchResult(row)
}

const onItemRowSelect = (row) => {
  if (!row) return
  selectedItem.value = row
  Object.assign(itemForm, {
    xmid: row.xmid || 0,
    xmzwmc: row.xmzwmc || '',
    xmywmc: row.xmywmc || '',
    pym: row.pym || '',
    qtdm: row.qtdm || '',
    xmdw: row.xmdw || '',
    xmjd: row.xmjd || 3,
    itemType: row.itemType || 0,
    jsbz: row.jsbz ? 1 : 0,
    dybz: row.dybz ? 1 : 0,
    tybz: row.tybz ? 1 : 0,
    zsbz: row.zsbz ? 1 : 0,
    hisFydm: row.hisFydm || row.his_fydm || '',
    hisJyxmmc: row.hisJyxmmc || row.his_jyxmmc || '',
    zskXmdm: row.zskXmdm || row.zsk_xmdm || '',
    zskXmmc: row.zskXmmc || row.zsk_xmmc || '',
    lcyy: row.lcyy || ''
  })
}

const onItemDblClick = () => {
  handleItemUpdate()
}

const handleItemAdd = () => {
  isNewItem.value = true
  editing.value = true
  Object.assign(itemForm, {
    xmid: 0,
    xmzwmc: '',
    xmywmc: '',
    pym: '',
    qtdm: '',
    xmdw: '',
    xmjd: 3,
    itemType: 0,
    jsbz: 0,
    dybz: 1,
    tybz: 0,
    zsbz: 0,
    hisFydm: '',
    hisJyxmmc: '',
    zskXmdm: '',
    zskXmmc: '',
    lcyy: ''
  })
}

const handleItemUpdate = () => {
  if (!selectedItem.value) return
  isNewItem.value = false
  editing.value = true
}

const handleItemCancel = () => {
  editing.value = false
  if (selectedItem.value) {
    onItemRowSelect(selectedItem.value)
  }
}

const handleItemSave = async () => {
  if (!itemForm.xmzwmc) { ElMessage.warning('中文名称不能为空'); return }
  if (!itemForm.pym) { ElMessage.warning('拼音码不能为空'); return }
  try {
    const { data } = await saveTestItem(itemForm)
    if (data.success) {
      ElMessage.success('保存成功')
      editing.value = false
      await loadItems()
    } else {
      ElMessage.error(data.message || '保存失败')
    }
  } catch (e) { ElMessage.error('保存失败') }
}

const handleItemDelete = async () => {
  if (!selectedItem.value) return
  try {
    await ElMessageBox.confirm(`确定删除项目"${selectedItem.value.xmzwmc}"？`, '提示', { type: 'warning' })
    const { data } = await deleteTestItem(selectedItem.value.xmid)
    if (data.success) {
      ElMessage.success('删除成功')
      selectedItem.value = null
      await loadItems()
    } else {
      ElMessage.error(data.message || '删除失败')
    }
  } catch (e) {}
}

const handleItemClose = () => {
  emit('update:modelValue', false)
}

const handleHisFeeSearch = () => {
  hisFeeResults.value = []
}

// ============== Tab2 方法 ==============
const loadCombos = async (keyword = '') => {
  try {
    const { data } = await fetchCombos({ keyword })
    comboList.value = Array.isArray(data) ? data : []
    comboOptions.value = Array.isArray(data) ? data : []
  } catch (e) {}
}

const loadSpecimenTypes = async () => {
  try {
    const { data } = await fetchSpecimenTypes({ keyword: '' })
    specimenTypes.value = Array.isArray(data) ? data : []
  } catch (e) {}
}

const searchComboList = () => {
  loadCombos(comboSearchKeyword.value)
}

const onComboSelect = async (row) => {
  if (!row) return
  selectedCombo.value = row
  Object.assign(comboForm, {
    zhid: row.zhid || 0,
    zhmc: row.zhmc || '',
    pym: row.pym || '',
    bbzl: row.bbzl || '',
    gzl: row.gzl || 1,
    sfbz: row.sfbz || 0,
    qybz: row.qybz ? 1 : 0,
    bqys: row.bqys || '#FF0000',
    yssm: row.yssm || ''
  })
  editingCombo.value = false
  await loadComboItems(row.zhid)
}

const loadComboItems = async (zhid) => {
  try {
    const { data } = await fetchComboItems(zhid)
    comboItems.value = Array.isArray(data) ? data : []
  } catch (e) {}
}

const handleComboAdd = () => {
  selectedCombo.value = null
  Object.assign(comboForm, {
    zhid: 0,
    zhmc: '',
    pym: '',
    bbzl: '',
    gzl: 1,
    sfbz: 0,
    qybz: 1,
    bqys: '#FF0000',
    yssm: ''
  })
  comboItems.value = []
  editingCombo.value = true
}

const handleComboSave = async () => {
  if (!comboForm.zhmc) { ElMessage.warning('组合名称不能为空'); return }
  try {
    const { data } = await saveComboApi(comboForm)
    if (data.success) {
      ElMessage.success('保存成功')
      editingCombo.value = false
      await loadCombos()
      const savedCombo = comboList.value.find(c => c.zhid === comboForm.zhid)
      if (savedCombo) {
        selectedCombo.value = savedCombo
        Object.assign(comboForm, savedCombo)
      }
    } else {
      ElMessage.error(data.message || '保存失败')
    }
  } catch (e) { ElMessage.error('保存失败') }
}

const handleComboDelete = async () => {
  if (!selectedCombo.value) return
  try {
    await ElMessageBox.confirm(`确定删除组合"${selectedCombo.value.zhmc}"？`, '提示', { type: 'warning' })
    const { data } = await deleteComboApi(selectedCombo.value.zhid)
    if (data.success) {
      ElMessage.success('删除成功')
      selectedCombo.value = null
      editingCombo.value = false
      await loadCombos()
    } else {
      ElMessage.error(data.message)
    }
  } catch (e) {}
}

const handleComboCancel = () => {
  editingCombo.value = false
  if (selectedCombo.value) {
    onComboSelect(selectedCombo.value)
  }
}

const onComboItemSelect = (row) => {
  selectedComboItem.value = row
  selectedComboItemIndex.value = comboItems.value.findIndex(item => item.xmid === row.xmid)
}

const onComboItemSelectionChange = (selection) => {
  if (selection.length > 0) {
    selectedComboItem.value = selection[0]
  }
}

const onItemSearchResultSelect = (row) => {
  selectedItemForCombo.value = row
}

const confirmAddItemToCombo = async () => {
  if (!selectedItemForCombo.value || !selectedCombo.value) return
  try {
    const { data } = await addComboItem(selectedCombo.value.zhid, { xmid: selectedItemForCombo.value.xmid })
    if (data.success) {
      ElMessage.success('添加成功')
      showAddItemToComboDialog.value = false
      selectedItemForCombo.value = null
      await loadComboItems(selectedCombo.value.zhid)
    } else {
      ElMessage.error(data.message)
    }
  } catch (e) {}
}

const handleRemoveComboItem = async () => {
  if (!selectedComboItem.value || !selectedCombo.value) return
  try {
    await ElMessageBox.confirm('确定移除此项目？', '提示', { type: 'warning' })
    const { data } = await removeComboItem(selectedCombo.value.zhid, selectedComboItem.value.xmid)
    if (data.success) {
      ElMessage.success('移除成功')
      selectedComboItem.value = null
      await loadComboItems(selectedCombo.value.zhid)
    } else {
      ElMessage.error(data.message)
    }
  } catch (e) {}
}

const handleComboItemUp = async () => {
  if (selectedComboItemIndex.value <= 0) return
  const items = [...comboItems.value]
  const idx = selectedComboItemIndex.value
  ;[items[idx - 1], items[idx]] = [items[idx], items[idx - 1]]
  comboItems.value = items
  selectedComboItem.value = items[idx - 1]
  selectedComboItemIndex.value = idx - 1
  await reorderComboItems(selectedCombo.value.zhid, items.map((item, i) => ({ xmid: item.xmid, id: i + 1 })))
}

const handleComboItemDown = async () => {
  if (selectedComboItemIndex.value >= comboItems.value.length - 1) return
  const items = [...comboItems.value]
  const idx = selectedComboItemIndex.value
  ;[items[idx], items[idx + 1]] = [items[idx + 1], items[idx]]
  comboItems.value = items
  selectedComboItem.value = items[idx + 1]
  selectedComboItemIndex.value = idx + 1
  await reorderComboItems(selectedCombo.value.zhid, items.map((item, i) => ({ xmid: item.xmid, id: i + 1 })))
}

// ============== Tab3 方法 ==============
const loadInstComboTree = async () => {
  await loadInstrumentsForTab3()
}

const loadInstrumentsForTab3 = async () => {
  try {
    const { data } = await fetchInstrumentComboTree()
    const transformTree = (nodes) => {
      nodes.forEach(n => {
        if (n.type === 'combo' && n.children) {
          n.itemCount = n.children.length
          n.items = n.children
          delete n.children
        }
        if (n.children) transformTree(n.children)
      })
    }
    if (Array.isArray(data)) {
      transformTree(data)
    }
    instrumentComboTree.value = Array.isArray(data) ? data : []
    const instMap = new Map()
    const flatten = (nodes) => {
      nodes.forEach(n => {
        if (n.type === 'instrument') {
          instMap.set(n.sbDjid, n)
        }
        if (n.children) flatten(n.children)
      })
    }
    flatten(instrumentComboTree.value)
    instrumentList.value = Array.from(instMap.values())
  } catch (e) {}
}

const onInstSelect = async (val) => {
  selectedInstForCombo.value = val
  await loadInstItemsAndCombos(val)
}

const loadInstItemsAndCombos = async (sbDjid) => {
  if (!sbDjid) return
  try {
    const [itemsRes] = await Promise.all([fetchInstrumentItems(sbDjid)])
    instItems.value = Array.isArray(itemsRes.data) ? itemsRes.data : []
    const findInstNode = (nodes) => {
      for (const n of nodes) {
        if (n.type === 'instrument' && n.sbDjid === sbDjid) return n
        if (n.children) {
          const found = findInstNode(n.children)
          if (found) return found
        }
      }
      return null
    }
    const instNode = findInstNode(instrumentComboTree.value)
    const assignedCombos = instNode?.children || []
    instCombos.value = assignedCombos
    unassignedCombos.value = []
  } catch (e) {
    instItems.value = []
    instCombos.value = []
    unassignedCombos.value = []
  }
}

const onInstItemSelect = (row) => {
  selectedInstItemForRemove.value = row
}

const onInstComboSelect = async (row) => {
  selectedComboForInst.value = row
  selectedComboItems.value = []
  if (row && row.zhid) {
    try {
      const { data } = await fetchComboItems(row.zhid)
      selectedComboItems.value = Array.isArray(data) ? data : []
    } catch (e) {
      selectedComboItems.value = []
    }
  }
}

const onComboTreeNodeClick = (data) => {
  selectedComboInTree.value = null
  selectedItemInTree.value = null
  if (data.type === 'instrument') {
    selectedInstForCombo.value = data.sbDjid
    selectedComboInTree.value = data
  } else if (data.type === 'combo') {
    selectedComboInTree.value = data
    selectedInstForCombo.value = data.sbDjid
  } else if (data.type === 'item') {
    selectedItemInTree.value = data
    selectedComboInTree.value = null
  }
}

const openAddInstItemDialog = async () => {
  if (!selectedInstForCombo.value) return
  selectedItemForInst.value = null
  instItemSearchKeyword.value = ''
  try {
    const { data } = await fetchTestItems({ keyword: '' })
    const items = Array.isArray(data) ? data : (Array.isArray(data?.data) ? data.data : [])
    instItemSearchResults.value = items
  } catch (e) {
    instItemSearchResults.value = []
  }
  showAddInstItemDialog.value = true
}

watch(instItemSearchKeyword, (val) => {
  if (!val) {
    fetchTestItems({ keyword: '' }).then(res => {
      instItemSearchResults.value = Array.isArray(res.data) ? res.data : []
    })
  } else {
    const q = val.toLowerCase()
    instItemSearchResults.value = instItemSearchResults.value.filter(item =>
      item.pym?.toLowerCase().includes(q) ||
      item.xmzwmc?.toLowerCase().includes(q) ||
      item.xmdm?.toLowerCase().includes(q)
    )
  }
})

const onInstItemSearchSelect = (row) => {
  selectedItemForInst.value = row
}

const confirmAddInstItem = async () => {
  if (!selectedInstForCombo.value || !selectedItemForInst.value) return
  try {
    const { data } = await addInstrumentItem({ sbDjid: selectedInstForCombo.value, xmid: selectedItemForInst.value.xmid })
    if (data.success) {
      ElMessage.success('添加成功')
      showAddInstItemDialog.value = false
      await loadInstItemsAndCombos(selectedInstForCombo.value)
      if (selectedComboForInst.value && selectedComboForInst.value.zhid) {
        const { data: comboData } = await fetchComboItems(selectedComboForInst.value.zhid)
        selectedComboItems.value = Array.isArray(comboData) ? comboData : []
      }
    } else {
      ElMessage.error(data.message)
    }
  } catch (e) {}
}

const handleRemoveInstItem = async () => {
  if (!selectedInstForCombo.value || !selectedInstItemForRemove.value) return
  try {
    await ElMessageBox.confirm('确定移除此项目？', '提示', { type: 'warning' })
    const { data } = await removeInstrumentItem(selectedInstForCombo.value, selectedInstItemForRemove.value.xmid)
    if (data.success) {
      ElMessage.success('移除成功')
      await loadInstItemsAndCombos(selectedInstForCombo.value)
    } else {
      ElMessage.error(data.message)
    }
  } catch (e) {}
}

const openAddInstComboDialog = async () => {
  if (!selectedInstForCombo.value) return
  selectedComboToAdd.value = null
  comboSearchKeyword.value = ''
  try {
    const { data } = await fetchUnassignedCombos(selectedInstForCombo.value)
    unassignedCombos.value = Array.isArray(data) ? data : []
  } catch (e) {
    unassignedCombos.value = []
  }
  showAddInstComboDialog.value = true
}

const confirmAddInstCombo = async () => {
  if (!selectedInstForCombo.value || !selectedComboToAdd.value) return
  try {
    const { data } = await assignCombo({ sbDjid: selectedInstForCombo.value, zhid: selectedComboToAdd.value })
    if (data.success) {
      ElMessage.success('添加成功')
      showAddInstComboDialog.value = false
      await loadInstrumentsForTab3()
      await loadInstItemsAndCombos(selectedInstForCombo.value)
    } else {
      ElMessage.error(data.message)
    }
  } catch (e) {}
}

const handleRemoveInstCombo = async () => {
  console.log('handleRemoveInstCombo called', {
    inst: selectedInstForCombo.value,
    combo: selectedComboInTree.value,
    zhid: selectedComboInTree.value?.zhid
  })
  if (!selectedInstForCombo.value) {
    console.log('Early return: inst is null, selectedComboInTree:', selectedComboInTree.value)
    ElMessage.warning('请先在左侧选择仪器')
    return
  }
  if (!selectedComboInTree.value || selectedComboInTree.value.type !== 'combo') {
    console.log('Early return: combo is null or not a combo')
    ElMessage.warning('请先在左侧选择一个组合')
    return
  }
  try {
    await ElMessageBox.confirm('确定移除此组合？', '提示', { type: 'warning' })
    const { data } = await removeInstrumentCombo(selectedInstForCombo.value, selectedComboInTree.value.zhid)
    if (data.success) {
      ElMessage.success('移除成功')
      selectedComboInTree.value = null
      await loadInstrumentsForTab3()
    } else {
      ElMessage.error(data.message)
    }
  } catch (e) {
    console.error('Remove combo error:', e)
  }
}

const viewComboDetail = async () => {
  if (!selectedComboForInst.value) return
  selectedComboDetail.value = { ...selectedComboForInst.value, children: [] }
  showComboDetailDialog.value = true
  try {
    const { data } = await fetchComboItems(selectedComboForInst.value.zhid)
    if (Array.isArray(data)) {
      selectedComboDetail.value.children = data
    }
  } catch (e) {}
}

// ============== Tab4 方法 ==============
const loadInstItemTree = async () => {
  await loadInstruments()
}

const onInstItemNodeClick = async (node) => {
  if (node.type === 'item') {
    selectedInstItem.value = node
    Object.assign(instItemDetail, {
      xmbm: node.xmdm || '',
      xs: node.xs != null ? Number(node.xs) : 1,
      yqxmdw: node.yqxmdw || '',
      xmjc: node.xmjc || '',
      jsbz: node.jsbz || 0
    })
    await loadRefRanges(node.sbDjid, node.xmid)
    await loadDefaultValues(node.sbDjid, node.xmid)
  }
}

const loadRefRanges = async (instId, itemId) => {
  try {
    const { data } = await fetchRefRangesApi(instId, itemId)
    refRanges.value = Array.isArray(data) ? data : []
  } catch (e) {}
}

const loadDefaultValues = async (instId, itemId) => {
  try {
    const { data } = await fetchDefaultApi(instId, itemId)
    if (data) {
      defaultValues.value = [data]
      Object.assign(defaultForm, {
        mrz: data.mrz || '',
        mr: data.mr || false,
        tsbz: data.tsbz || false
      })
    } else {
      defaultValues.value = []
    }
  } catch (e) {}
}

const handleSaveInstItem = async () => {
  if (!selectedInstItem.value) {
    ElMessage.warning('请先选择仪器下的项目')
    return
  }
  try {
    const { data } = await saveInstrumentItem({
      sbDjid: selectedInstItem.value.sbDjid,
      xmid: selectedInstItem.value.xmid,
      xmbm: instItemDetail.xmbm,
      xs: instItemDetail.xs,
      yqxmdw: instItemDetail.yqxmdw,
      xmjc: instItemDetail.xmjc
    })
    if (data.success) {
      ElMessage.success('保存成功')
      await loadInstItemTree()
    } else {
      ElMessage.error(data.message || '保存失败')
    }
  } catch (e) {
    ElMessage.error('保存失败')
  }
}

const handleOpenFormula = () => {
  if (!selectedInstItem.value) {
    ElMessage.warning('请先选择仪器下的项目')
    return
  }
  formulaDialogVisible.value = true
}

const handleFormulaSaved = () => {
  console.log('Formula saved')
}

const handleOpenDataReplace = async () => {
  if (!selectedInstItem.value) {
    ElMessage.warning('请先选择仪器下的项目')
    return
  }
  dataReplaceEditMode.value = false
  Object.assign(dataReplaceForm, {
    id: null,
    sbDjid: selectedInstItem.value.sbDjid,
    xmid: selectedInstItem.value.xmid,
    originalValue: '',
    replaceValue: ''
  })
  await loadDataReplaceList()
  dataReplaceVisible.value = true
}

const loadDataReplaceList = async () => {
  if (!selectedInstItem.value) return
  try {
    const { data } = await fetchDataReplaceSettings(selectedInstItem.value.sbDjid, selectedInstItem.value.xmid)
    dataReplaceList.value = Array.isArray(data) ? data : []
  } catch (e) {
    dataReplaceList.value = []
  }
}

const handleAddDataReplace = () => {
  dataReplaceEditMode.value = true
  Object.assign(dataReplaceForm, {
    id: null,
    originalValue: '',
    replaceValue: ''
  })
}

const handleEditDataReplace = (row) => {
  dataReplaceEditMode.value = true
  Object.assign(dataReplaceForm, { ...row })
}

const handleCancelDataReplace = () => {
  dataReplaceEditMode.value = false
  Object.assign(dataReplaceForm, {
    id: null,
    originalValue: '',
    replaceValue: ''
  })
}

const handleSaveDataReplace = async () => {
  if (!dataReplaceForm.originalValue || !dataReplaceForm.replaceValue) {
    ElMessage.warning('请填写完整信息')
    return
  }
  try {
    const { data } = await saveDataReplaceSetting({
      id: dataReplaceForm.id,
      sbDjid: selectedInstItem.value.sbDjid,
      xmid: selectedInstItem.value.xmid,
      originalValue: dataReplaceForm.originalValue,
      replaceValue: dataReplaceForm.replaceValue
    })
    if (data.success) {
      ElMessage.success('保存成功')
      await loadDataReplaceList()
      handleCancelDataReplace()
    } else {
      ElMessage.error(data.message)
    }
  } catch (e) {
    ElMessage.error('保存失败')
  }
}

const handleDeleteDataReplace = async (row) => {
  try {
    await ElMessageBox.confirm('确定删除此数据替换规则？', '提示', { type: 'warning' })
    const { data } = await deleteDataReplaceSetting(row.id)
    if (data.success) {
      ElMessage.success('删除成功')
      await loadDataReplaceList()
    } else {
      ElMessage.error(data.message)
    }
  } catch (e) {}
}

const handleEditDefault = () => {
  defaultFormVisible.value = true
}

const handleSaveDefault = async () => {
  if (!selectedInstItem.value) return
  try {
    const { data } = await saveDefaultApi({
      xmid: selectedInstItem.value.xmid,
      sbDjid: selectedInstItem.value.sbDjid,
      mrz: defaultForm.mrz,
      mr: defaultForm.mr ? 1 : 0,
      tsbz: defaultForm.tsbz ? 1 : 0
    })
    if (data.success) {
      ElMessage.success('保存成功')
      defaultFormVisible.value = false
      await loadDefaultValues(selectedInstItem.value.sbDjid, selectedInstItem.value.xmid)
    } else {
      ElMessage.error(data.message)
    }
  } catch (e) {}
}

const handleAddRefRange = () => {
  Object.assign(refForm, {
    id: null,
    xmid: selectedInstItem.value.xmid,
    sbDjid: selectedInstItem.value.sbDjid,
    bbsgbz: 0,
    bbzl: '',
    xbsgbz: 0,
    brxb: 0,
    nlsgbz: 0,
    nllx: '',
    nlsx: 0,
    nlxx: 0,
    ckz: '',
    ckzdx: '',
    ckzgx: '',
    bjzdx: '',
    bjzgx: '',
    jszgx: '',
    jszdx: '',
    fczgx: '',
    fczdx: '',
    zdshbz: 0,
    zdshgx: '',
    zdshdx: '',
    zdshcyqj: '',
    jgfctsbz: 0
  })
  refFormVisible.value = true
}

const handleEditRefRange = (row) => {
  Object.assign(refForm, { ...row })
  refFormVisible.value = true
}

const handleSaveRefRange = async () => {
  try {
    const { data } = await saveRefRangeApi(refForm)
    if (data.success) {
      ElMessage.success('保存成功')
      refFormVisible.value = false
      await loadRefRanges(selectedInstItem.value.sbDjid, selectedInstItem.value.xmid)
    } else {
      ElMessage.error(data.message)
    }
  } catch (e) {}
}

const handleDeleteRefRange = async (row) => {
  try {
    await ElMessageBox.confirm('确定删除此参考范围？', '提示', { type: 'warning' })
    const { data } = await deleteRefRangeApi(row.id)
    if (data.success) {
      ElMessage.success('删除成功')
      await loadRefRanges(selectedInstItem.value.sbDjid, selectedInstItem.value.xmid)
    } else {
      ElMessage.error(data.message)
    }
  } catch (e) {}
}

// ============== Tab5 方法 ==============
const loadTatSettings = async () => {
  try {
    const { data } = await fetchTatSettings()
    let result = Array.isArray(data) ? data : []
    if (filterInstForTat.value) {
      result = result.filter(item => item.sbDjid === filterInstForTat.value)
    }
    tatSettings.value = result
  } catch (e) {}
}

const handleTatAdd = () => {
  Object.assign(tatForm, {
    sbDjid: '',
    brlb: 1,
    syqk: 1,
    zhid: '',
    zhmc: '',
    TAT: 60,
    _edit: false
  })
  tatFormVisible.value = true
}

const handleEditTatSetting = (row) => {
  Object.assign(tatForm, { ...row, _edit: true })
  tatFormVisible.value = true
}

const onTatComboChange = (zhid) => {
  const combo = comboOptions.value.find(c => c.zhid === zhid)
  if (combo) tatForm.zhmc = combo.zhmc
}

const handleSaveTatSetting = async () => {
  if (!tatForm.sbDjid) { ElMessage.warning('请选择仪器'); return }
  if (!tatForm.zhid) { ElMessage.warning('请选择组合'); return }
  try {
    const { data } = await saveTatSettingApi(tatForm)
    if (data.success) {
      ElMessage.success('保存成功')
      tatFormVisible.value = false
      await loadTatSettings()
    } else {
      ElMessage.error(data.message)
    }
  } catch (e) { ElMessage.error('保存失败') }
}

const handleDeleteTatSetting = async (row) => {
  try {
    await ElMessageBox.confirm('确定删除此TAT设置？', '提示', { type: 'warning' })
    const { data } = await deleteTatSettingApi(row.sbDjid, row.brlb, row.syqk, row.zhid)
    if (data.success) {
      ElMessage.success('删除成功')
      await loadTatSettings()
    } else {
      ElMessage.error(data.message)
    }
  } catch (e) {}
}

const handleAutoCalculateTat = async () => {
  try {
    const { data } = await autoCalculateTatApi()
    if (data.success) {
      ElMessage.success('自动计算完成')
      await loadTatSettings()
    } else {
      ElMessage.error(data.message || '计算失败')
    }
  } catch (e) { ElMessage.error('计算失败') }
}

// ============== Tab6 方法 ==============
const onInstForCoeffSelect = (row) => {
  selectedInstForCoeff.value = row
  if (row) loadCoeffItems(row.sbDjid)
}

const loadCoeffItems = async (sbDjid) => {
  try {
    const { data } = await fetchInstrumentCoefficients(sbDjid)
    coeffItems.value = Array.isArray(data) ? data.map(item => ({
      xmid: item.xmid,
      xmdm: item.xmdm,
      xmzwmc: item.xmzwmc,
      xmdw: item.xmdw || '',
      xs: item.xs != null ? Number(item.xs) : 1
    })) : []
  } catch (e) {
    coeffItems.value = []
  }
}

const showBatchSetCoeffDialog = () => {
  batchCoeffForm.value.xs = 1
  showBatchSetCoeff.value = true
}

const handleResetCoeff = (row) => {
  row.xs = 1
}

const handleApplyBatchCoeff = () => {
  coeffItems.value.forEach(item => { item.xs = batchCoeffForm.value.xs })
  showBatchSetCoeff.value = false
}

const handleSaveAllCoeff = async () => {
  if (!selectedInstForCoeff.value) return
  try {
    const items = coeffItems.value.map(item => ({ xmid: item.xmid, xmdm: item.xmdm, xs: item.xs }))
    const { data } = await batchSaveCoeff({ sbDjid: selectedInstForCoeff.value.sbDjid, items })
    if (data.success) {
      ElMessage.success('保存成功')
      await loadCoeffItems(selectedInstForCoeff.value.sbDjid)
    } else {
      ElMessage.error(data.message || '保存失败')
    }
  } catch (e) { ElMessage.error('保存失败') }
}

// ============== Tab切换 ==============
const onTabChange = (tab) => {
  if (tab === 'tab1') loadItems()
  else if (tab === 'tab2') { loadCombos(); loadInstruments() }
  else if (tab === 'tab3') loadInstComboTree()
  else if (tab === 'tab4') loadInstItemTree()
  else if (tab === 'tab5') { loadTatSettings(); loadInstruments(); loadCombos() }
  else if (tab === 'tab6') loadInstruments()
}

// ============== 初始化 ==============
onMounted(() => {
  loadItems()
  loadCombos()
  loadSpecimenTypes()
})
</script>

<style scoped>
.lab-item-setting-dialog :deep(.el-dialog__body) {
  padding: 10px;
}

.combo-items-table-wrapper {
  flex: 1;
  overflow-y: auto;
}

.tab1-container {
  display: flex;
  gap: 10px;
  height: 520px;
}

.left-panel {
  width: 55%;
  display: flex;
  flex-direction: column;
}

.search-bar {
  display: flex;
  gap: 8px;
  align-items: center;
  margin-bottom: 8px;
}

.search-label {
  font-size: 13px;
  color: #1a73e8;
  white-space: nowrap;
}

.right-panel {
  width: 45%;
  display: flex;
  flex-direction: column;
}

.form-title {
  font-size: 13px;
  font-weight: 500;
  color: #1a73e8;
  margin-bottom: 10px;
  padding-bottom: 5px;
  border-bottom: 1px solid #e4e7ed;
}

.item-form :deep(.el-form-item) {
  margin-bottom: 12px;
}

.item-form :deep(.el-row) {
  margin-bottom: 0;
}

.button-bar {
  display: flex;
  gap: 8px;
  margin-top: 10px;
  padding-top: 10px;
  border-top: 1px solid #e4e7ed;
}

.his-fee-grid {
  position: absolute;
  right: 20px;
  bottom: 60px;
  width: 300px;
  z-index: 100;
  background: white;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.15);
}

.search-results-grid {
  margin-top: 5px;
}

/* Tab2 - 新布局 */
.tab2-wrapper {
  display: flex;
  gap: 16px;
  height: 520px;
  overflow: hidden;
}

.combo-list-panel {
  width: 380px;
  display: flex;
  flex-direction: column;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  background: #fff;
}

.combo-detail-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 12px;
  overflow-y: auto;
}

.detail-card {
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  background: #fff;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 12px;
  background: #f5f7fa;
  border-bottom: 1px solid #e4e7ed;
  border-radius: 4px 4px 0 0;
}

.card-title {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
}

.card-actions {
  display: flex;
  gap: 8px;
}

.card-body {
  padding: 12px;
}

.form-actions {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #e4e7ed;
}

.color-picker-row {
  display: flex;
  align-items: center;
  gap: 8px;
}

.color-preview {
  display: inline-block;
  padding: 4px 8px;
  font-size: 12px;
  color: #fff;
  border-radius: 4px;
  max-width: 120px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  background: #f5f7fa;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  margin-bottom: 8px;
  font-size: 13px;
  font-weight: 500;
  color: #303133;
}

.panel-header span {
  color: #303133;
}

/* Tab3 */
.tab3-tree-wrapper {
  display: flex;
  gap: 15px;
  height: 520px;
}

.tree-panel {
  width: 350px;
  height: 100%;
  display: flex;
  flex-direction: column;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  overflow: hidden;
}

.tree-panel .panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 12px;
  background: #f5f7fa;
  border-bottom: 1px solid #dcdfe6;
}

.tree-panel :deep(.el-tree) {
  flex: 1;
  overflow-y: auto;
  padding: 8px;
}

.tree-node {
  display: flex;
  align-items: center;
  gap: 6px;
}

.node-icon {
  flex-shrink: 0;
}

.node-label {
  flex: 1;
}

.node-count {
  color: #909399;
  font-size: 12px;
}

.detail-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 12px;
  overflow-y: auto;
}

.detail-card {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  overflow: hidden;
}

.detail-card .card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 12px;
  background: #f5f7fa;
  border-bottom: 1px solid #dcdfe6;
}

.detail-card .card-title {
  font-weight: 600;
  font-size: 14px;
}

.detail-card .card-body {
  padding: 12px;
}

.combo-items-card {
  flex: 1;
  min-height: 0;
}

.combo-items-card .items-list-body {
  padding: 0;
  overflow: hidden;
}

.combo-items-card .el-table {
  max-height: none;
  height: 100%;
}

.detail-card .info-row {
  display: flex;
  margin-bottom: 8px;
}

.detail-card .info-row:last-child {
  margin-bottom: 0;
}

.detail-card .info-label {
  width: 80px;
  color: #606266;
  flex-shrink: 0;
}

.detail-card .info-value {
  color: #303133;
}

.detail-card .action-buttons {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.button-row {
  display: flex;
  gap: 10px;
  margin-top: 10px;
  justify-content: center;
}

/* Tab4 */
.tab4-container {
  display: flex;
  gap: 10px;
  height: 520px;
}

.left-tree-panel {
  width: 280px;
  overflow: auto;
}

.left-tree-panel :deep(.el-tree) {
  max-height: 480px;
  overflow-y: auto;
}

.right-ref-panel {
  flex: 1;
  overflow: auto;
}

/* Tab5 */
.tab5-container {
  height: 520px;
}

.toolbar {
  display: flex;
  gap: 8px;
  align-items: center;
  margin-bottom: 12px;
}

/* Tab6 */
.tab6-container {
  display: flex;
  gap: 10px;
  height: 520px;
}

.left-table-panel {
  width: 280px;
}

.right-coeff-panel {
  flex: 1;
}

.color-input-disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>
