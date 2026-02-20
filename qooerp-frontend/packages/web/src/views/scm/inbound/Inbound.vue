<template>
  <div class="inbound-container">
    <!-- 统计卡片 -->
    <t-row :gutter="[16, 16]" class="statistics-cards">
      <t-col :span="6">
        <t-card>
          <statistic-item
            title="本月入库单数"
            :value="statistics.totalCount"
            :loading="loading"
            icon="file-list"
            color="#0052D9"
          />
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card>
          <statistic-item
            title="本月入库数量"
            :value="statistics.totalQuantity"
            :loading="loading"
            icon="check-circle"
            color="#00A870"
          />
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card>
          <statistic-item
            title="本月入库金额"
            :value="statistics.totalAmount"
            :precision="2"
            prefix="¥"
            :loading="loading"
            icon="chart-column"
            color="#ED7B2F"
          />
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card>
          <statistic-item
            title="待审核入库单"
            :value="statistics.pendingCount"
            :loading="loading"
            icon="time"
            color="#E34D59"
          />
        </t-card>
      </t-col>
    </t-row>

    <!-- 搜索卡片 -->
    <t-card class="search-card">
      <t-form
        ref="searchFormRef"
        :data="searchForm"
        label-width="100px"
        @submit="handleSearch"
        @reset="handleReset"
      >
        <t-row :gutter="16">
          <t-col :span="4">
            <t-form-item label="入库单号" name="inboundNo">
              <t-input v-model="searchForm.inboundNo" placeholder="请输入入库单号" clearable />
            </t-form-item>
          </t-col>
          <t-col :span="4">
            <t-form-item label="采购订单" name="purchaseOrderNo">
              <t-input v-model="searchForm.purchaseOrderNo" placeholder="请输入采购订单号" clearable />
            </t-form-item>
          </t-col>
          <t-col :span="4">
            <t-form-item label="入库类型" name="inboundType">
              <t-select v-model="searchForm.inboundType" placeholder="请选择入库类型" clearable>
                <t-option value="purchase" label="采购入库" />
                <t-option value="return" label="退货入库" />
                <t-option value="transfer" label="调拨入库" />
                <t-option value="production" label="生产入库" />
                <t-option value="other" label="其他入库" />
              </t-select>
            </t-form-item>
          </t-col>
          <t-col :span="4">
            <t-form-item label="供应商" name="supplier">
              <t-input v-model="searchForm.supplier" placeholder="请输入供应商" clearable />
            </t-form-item>
          </t-col>
          <t-col :span="4">
            <t-form-item label="入库日期" name="dateRange">
              <t-date-range-picker
                v-model="searchForm.dateRange"
                placeholder="请选择日期范围"
                clearable
              />
            </t-form-item>
          </t-col>
          <t-col :span="4">
            <t-form-item label="审核状态" name="approvalStatus">
              <t-select v-model="searchForm.approvalStatus" placeholder="请选择审核状态" clearable>
                <t-option value="draft" label="草稿" />
                <t-option value="pending" label="待审核" />
                <t-option value="approved" label="已审核" />
                <t-option value="rejected" label="已驳回" />
              </t-select>
            </t-form-item>
          </t-col>
        </t-row>
        <t-row>
          <t-col :span="24" class="search-actions">
            <t-space>
              <t-button theme="primary" type="submit">查询</t-button>
              <t-button theme="default" type="reset">重置</t-button>
            </t-space>
          </t-col>
        </t-row>
      </t-form>
    </t-card>

    <!-- 操作栏 -->
    <t-card class="action-card">
      <t-space>
        <t-button theme="primary" @click="handleAdd">
          <template #icon><t-icon name="add" /></template>
          新增入库
        </t-button>
        <t-button theme="default" @click="handleFromOrder">
          <template #icon><t-icon name="shopping-cart" /></template>
          从订单入库
        </t-button>
        <t-button theme="default" @click="handleBatchDelete">
          <template #icon><t-icon name="delete" /></template>
          批量删除
        </t-button>
        <t-button theme="default" @click="handleExport">
          <template #icon><t-icon name="download" /></template>
          导出报表
        </t-button>
        <t-button theme="default" @click="handlePrint">
          <template #icon><t-icon name="print" /></template>
          打印单据
        </t-button>
      </t-space>
    </t-card>

    <!-- 列表 -->
    <t-card>
      <t-table
        v-model:row-selection="selectedRowKeys"
        :data="tableData"
        :columns="columns"
        :loading="loading"
        :pagination="pagination"
        @page-change="handlePageChange"
        row-key="id"
      >
        <template #inboundType="{ row }">
          <t-tag :theme="getInboundTypeTheme(row.inboundType)">
            {{ getInboundTypeText(row.inboundType) }}
          </t-tag>
        </template>
        <template #approvalStatus="{ row }">
          <t-tag :theme="getApprovalStatusTheme(row.approvalStatus)">
            {{ getApprovalStatusText(row.approvalStatus) }}
          </t-tag>
        </template>
        <template #action="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleView(row)">详情</t-link>
            <t-link theme="primary" @click="handleEdit(row)">编辑</t-link>
            <t-link v-if="row.approvalStatus === 'pending'" theme="success" @click="handleApprove(row)">
              审核
            </t-link>
            <t-link theme="danger" @click="handleDelete(row)">删除</t-link>
          </t-space>
        </template>
      </t-table>
    </t-card>

    <!-- 编辑弹窗 -->
    <t-dialog
      v-model:visible="editDialogVisible"
      header="采购入库单"
      width="1000px"
      :footer="false"
      @close="handleEditDialogClose"
    >
      <t-tabs v-model="activeTab" theme="card">
        <t-tab-panel value="basic" label="基本信息">
          <t-form
            ref="editFormRef"
            :data="editForm"
            :rules="editRules"
            label-width="120px"
            @submit="handleSubmit"
          >
            <t-row :gutter="16">
              <t-col :span="12">
                <t-form-item label="入库单号" name="inboundNo">
                  <t-input v-model="editForm.inboundNo" placeholder="自动生成" disabled />
                </t-form-item>
              </t-col>
              <t-col :span="12">
                <t-form-item label="入库日期" name="inboundDate">
                  <t-date-picker v-model="editForm.inboundDate" placeholder="请选择入库日期" />
                </t-form-item>
              </t-col>
            </t-row>
            <t-row :gutter="16">
              <t-col :span="12">
                <t-form-item label="入库类型" name="inboundType">
                  <t-select v-model="editForm.inboundType" placeholder="请选择入库类型">
                    <t-option value="purchase" label="采购入库" />
                    <t-option value="return" label="退货入库" />
                    <t-option value="transfer" label="调拨入库" />
                    <t-option value="production" label="生产入库" />
                    <t-option value="other" label="其他入库" />
                  </t-select>
                </t-form-item>
              </t-col>
              <t-col :span="12">
                <t-form-item label="采购订单" name="purchaseOrderNo">
                  <t-input v-model="editForm.purchaseOrderNo" placeholder="请选择采购订单" @click="handleSelectOrder">
                    <template #suffix-icon>
                      <t-icon name="search" />
                    </template>
                  </t-input>
                </t-form-item>
              </t-col>
            </t-row>
            <t-row :gutter="16">
              <t-col :span="12">
                <t-form-item label="供应商" name="supplier">
                  <t-input v-model="editForm.supplier" placeholder="供应商" disabled />
                </t-form-item>
              </t-col>
              <t-col :span="12">
                <t-form-item label="入库仓库" name="warehouse">
                  <t-select v-model="editForm.warehouse" placeholder="请选择入库仓库">
                    <t-option value="主仓库" label="主仓库" />
                    <t-option value="分仓A" label="分仓A" />
                    <t-option value="分仓B" label="分仓B" />
                  </t-select>
                </t-form-item>
              </t-col>
            </t-row>
            <t-row :gutter="16">
              <t-col :span="12">
                <t-form-item label="经手人" name="operator">
                  <t-select v-model="editForm.operator" placeholder="请选择经手人">
                    <t-option value="张三" label="张三" />
                    <t-option value="李四" label="李四" />
                    <t-option value="王五" label="王五" />
                  </t-select>
                </t-form-item>
              </t-col>
              <t-col :span="12">
                <t-form-item label="入库状态" name="inboundStatus">
                  <t-select v-model="editForm.inboundStatus" placeholder="请选择入库状态">
                    <t-option value="partial" label="部分入库" />
                    <t-option value="completed" label="全部入库" />
                  </t-select>
                </t-form-item>
              </t-col>
            </t-row>
            <t-form-item label="入库说明" name="description">
              <t-textarea
                v-model="editForm.description"
                placeholder="请输入入库说明"
                :maxlength="500"
                :autosize="{ minRows: 3 }"
              />
            </t-form-item>
            <t-form-item label="内部备注" name="internalRemark">
              <t-textarea
                v-model="editForm.internalRemark"
                placeholder="请输入内部备注"
                :maxlength="200"
                :autosize="{ minRows: 2 }"
              />
            </t-form-item>
          </t-form>
        </t-tab-panel>
        <t-tab-panel value="details" label="入库商品">
          <div class="detail-actions">
            <t-button theme="primary" size="small" @click="handleAddProduct">
              <template #icon><t-icon name="add" /></template>
              添加商品
            </t-button>
          </div>
          <t-table
            :data="editForm.products"
            :columns="productColumns"
            :pagination="false"
            row-key="id"
          >
            <template #productCode="{ row }">
              <t-input v-model="row.productCode" placeholder="商品编码" size="small" />
            </template>
            <template #productName="{ row }">
              <t-input v-model="row.productName" placeholder="商品名称" size="small" />
            </template>
            <template #specification="{ row }">
              <t-input v-model="row.specification" placeholder="规格" size="small" />
            </template>
            <template #unit="{ row }">
              <t-input v-model="row.unit" placeholder="单位" size="small" />
            </template>
            <template #quantity="{ row }">
              <t-input-number v-model="row.quantity" placeholder="数量" size="small" :min="0" @change="calculateTotal" />
            </template>
            <template #price="{ row }">
              <t-input-number v-model="row.price" placeholder="单价" size="small" :min="0" :precision="2" @change="calculateTotal" />
            </template>
            <template #amount="{ row }">
              <t-input v-model="row.amount" placeholder="金额" size="small" disabled />
            </template>
            <template #action="{ row }">
              <t-button theme="danger" size="small" @click="handleRemoveProduct(row)">
                <template #icon><t-icon name="delete" /></template>
              </t-button>
            </template>
          </t-table>
          <div class="summary-info">
            <t-descriptions :column="4" bordered>
              <t-descriptions-item label="入库商品数">{{ editForm.products.length }}</t-descriptions-item>
              <t-descriptions-item label="入库总数量">{{ totalQuantity }}</t-descriptions-item>
              <t-descriptions-item label="入库总金额">¥{{ formatMoney(totalAmount) }}</t-descriptions-item>
              <t-descriptions-item label="审核状态">
                <t-tag :theme="getApprovalStatusTheme(editForm.approvalStatus)">
                  {{ getApprovalStatusText(editForm.approvalStatus) }}
                </t-tag>
              </t-descriptions-item>
            </t-descriptions>
          </div>
        </t-tab-panel>
        <t-tab-panel value="summary" label="汇总信息">
          <t-descriptions :column="2" bordered>
            <t-descriptions-item label="入库单号">{{ editForm.inboundNo }}</t-descriptions-item>
            <t-descriptions-item label="入库日期">{{ editForm.inboundDate }}</t-descriptions-item>
            <t-descriptions-item label="入库类型">
              <t-tag :theme="getInboundTypeTheme(editForm.inboundType)">
                {{ getInboundTypeText(editForm.inboundType) }}
              </t-tag>
            </t-descriptions-item>
            <t-descriptions-item label="供应商">{{ editForm.supplier }}</t-descriptions-item>
            <t-descriptions-item label="入库仓库">{{ editForm.warehouse }}</t-descriptions-item>
            <t-descriptions-item label="经手人">{{ editForm.operator }}</t-descriptions-item>
            <t-descriptions-item label="入库商品数">{{ editForm.products.length }}</t-descriptions-item>
            <t-descriptions-item label="入库总数量">{{ totalQuantity }}</t-descriptions-item>
            <t-descriptions-item label="入库总金额">¥{{ formatMoney(totalAmount) }}</t-descriptions-item>
            <t-descriptions-item label="审核状态">
              <t-tag :theme="getApprovalStatusTheme(editForm.approvalStatus)">
                {{ getApprovalStatusText(editForm.approvalStatus) }}
              </t-tag>
            </t-descriptions-item>
            <t-descriptions-item label="创建时间">{{ editForm.createTime }}</t-descriptions-item>
            <t-descriptions-item label="更新时间">{{ editForm.updateTime }}</t-descriptions-item>
          </t-descriptions>
        </t-tab-panel>
      </t-tabs>
      <div class="dialog-footer">
        <t-space>
          <t-button theme="default" @click="editDialogVisible = false">取消</t-button>
          <t-button theme="primary" @click="handleSubmit">保存</t-button>
        </t-space>
      </div>
    </t-dialog>

    <!-- 详情弹窗 -->
    <t-dialog v-model:visible="detailDialogVisible" header="入库单详情" width="1000px" :footer="false">
      <t-tabs theme="card">
        <t-tab-panel value="basic" label="基本信息">
          <t-descriptions :column="2" bordered>
            <t-descriptions-item label="入库单号">{{ currentRow.inboundNo }}</t-descriptions-item>
            <t-descriptions-item label="入库日期">{{ currentRow.inboundDate }}</t-descriptions-item>
            <t-descriptions-item label="入库类型">
              <t-tag :theme="getInboundTypeTheme(currentRow.inboundType)">
                {{ getInboundTypeText(currentRow.inboundType) }}
              </t-tag>
            </t-descriptions-item>
            <t-descriptions-item label="采购订单">{{ currentRow.purchaseOrderNo }}</t-descriptions-item>
            <t-descriptions-item label="供应商">{{ currentRow.supplier }}</t-descriptions-item>
            <t-descriptions-item label="入库仓库">{{ currentRow.warehouse }}</t-descriptions-item>
            <t-descriptions-item label="经手人">{{ currentRow.operator }}</t-descriptions-item>
            <t-descriptions-item label="入库状态">{{ currentRow.inboundStatus }}</t-descriptions-item>
            <t-descriptions-item label="审核状态" :span="2">
              <t-tag :theme="getApprovalStatusTheme(currentRow.approvalStatus)">
                {{ getApprovalStatusText(currentRow.approvalStatus) }}
              </t-tag>
            </t-descriptions-item>
            <t-descriptions-item label="入库说明" :span="2">{{ currentRow.description }}</t-descriptions-item>
            <t-descriptions-item label="内部备注" :span="2">{{ currentRow.internalRemark }}</t-descriptions-item>
            <t-descriptions-item label="创建时间">{{ currentRow.createTime }}</t-descriptions-item>
            <t-descriptions-item label="更新时间">{{ currentRow.updateTime }}</t-descriptions-item>
          </t-descriptions>
        </t-tab-panel>
        <t-tab-panel value="details" label="入库商品">
          <t-table :data="currentRow.products" :columns="productColumns" :pagination="false">
            <template #productCode="{ row }">{{ row.productCode }}</template>
            <template #productName="{ row }">{{ row.productName }}</template>
            <template #specification="{ row }">{{ row.specification }}</template>
            <template #unit="{ row }">{{ row.unit }}</template>
            <template #quantity="{ row }">{{ row.quantity }}</template>
            <template #price="{ row }">¥{{ formatMoney(row.price) }}</template>
            <template #amount="{ row }">¥{{ formatMoney(row.amount) }}</template>
          </t-table>
        </t-tab-panel>
        <t-tab-panel value="summary" label="汇总信息">
          <t-descriptions :column="2" bordered>
            <t-descriptions-item label="入库商品数">{{ currentRow.productCount }}</t-descriptions-item>
            <t-descriptions-item label="入库总数量">{{ currentRow.totalQuantity }}</t-descriptions-item>
            <t-descriptions-item label="入库总金额">¥{{ formatMoney(currentRow.totalAmount) }}</t-descriptions-item>
            <t-descriptions-item label="创建时间">{{ currentRow.createTime }}</t-descriptions-item>
          </t-descriptions>
        </t-tab-panel>
      </t-tabs>
    </t-dialog>

    <!-- 选择订单弹窗 -->
    <t-dialog v-model:visible="orderDialogVisible" header="选择采购订单" width="800px" :footer="false">
      <t-table
        :data="orderList"
        :columns="orderColumns"
        :pagination="orderPagination"
        row-key="id"
        @row-click="handleSelectOrderRow"
      />
    </t-dialog>

    <!-- 审核弹窗 -->
    <t-dialog v-model:visible="approveDialogVisible" header="入库审核" width="600px" @confirm="handleApproveConfirm">
      <t-form :data="approveForm" label-width="100px">
        <t-form-item label="审核结果">
          <t-radio-group v-model="approveForm.result">
            <t-radio value="approved">通过</t-radio>
            <t-radio value="rejected">驳回</t-radio>
          </t-radio-group>
        </t-form-item>
        <t-form-item label="审核意见">
          <t-textarea v-model="approveForm.remark" placeholder="请输入审核意见" :autosize="{ minRows: 3 }" />
        </t-form-item>
      </t-form>
    </t-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { MessagePlugin } from 'tdesign-vue-next'
import type { FormInstanceFunctions, FormRule } from 'tdesign-vue-next'

defineOptions({ name: 'Inbound' })

// 统计数据
const statistics = reactive({
  totalCount: 156,
  totalQuantity: 5680,
  totalAmount: 125680.00,
  pendingCount: 18
})

// 搜索表单
const searchForm = reactive({
  inboundNo: '',
  purchaseOrderNo: '',
  inboundType: '',
  supplier: '',
  dateRange: [],
  approvalStatus: ''
})

// 表格数据
const loading = ref(false)
const tableData = ref<any[]>([
  {
    id: '1',
    inboundNo: 'RK202602190001',
    inboundDate: '2026-02-19',
    inboundType: 'purchase',
    purchaseOrderNo: 'CG202602190001',
    supplier: '供应商A',
    warehouse: '主仓库',
    operator: '张三',
    inboundStatus: 'completed',
    approvalStatus: 'approved',
    description: '第一批采购入库',
    internalRemark: '',
    productCount: 5,
    totalQuantity: 500,
    totalAmount: 25000.00,
    createTime: '2026-02-19 10:00:00',
    updateTime: '2026-02-19 14:00:00',
    products: [
      { id: '1', productCode: 'SP001', productName: '商品A', specification: '500g', unit: '件', quantity: 200, price: 50.00, amount: 10000 },
      { id: '2', productCode: 'SP002', productName: '商品B', specification: '1kg', unit: '件', quantity: 300, price: 50.00, amount: 15000 }
    ]
  },
  {
    id: '2',
    inboundNo: 'RK202602180001',
    inboundDate: '2026-02-18',
    inboundType: 'return',
    purchaseOrderNo: '',
    supplier: '供应商B',
    warehouse: '分仓A',
    operator: '李四',
    inboundStatus: 'completed',
    approvalStatus: 'pending',
    description: '退货入库',
    internalRemark: '',
    productCount: 3,
    totalQuantity: 150,
    totalAmount: 8000.00,
    createTime: '2026-02-18 15:30:00',
    updateTime: '2026-02-18 16:00:00',
    products: []
  }
])

const selectedRowKeys = ref([])
const pagination = reactive({
  current: 1,
  pageSize: 20,
  total: tableData.value.length
})

// 表格列
const columns = [
  { colKey: 'inboundNo', title: '入库单号', width: 150 },
  { colKey: 'inboundDate', title: '入库日期', width: 120 },
  { colKey: 'inboundType', title: '入库类型', width: 120 },
  { colKey: 'purchaseOrderNo', title: '采购订单', width: 150 },
  { colKey: 'supplier', title: '供应商', width: 150 },
  { colKey: 'warehouse', title: '入库仓库', width: 100 },
  { colKey: 'totalQuantity', title: '入库数量', width: 100 },
  { colKey: 'totalAmount', title: '入库金额', width: 120, cell: (h: any, { row }: any) => `¥${formatMoney(row.totalAmount)}` },
  { colKey: 'operator', title: '经手人', width: 100 },
  { colKey: 'approvalStatus', title: '审核状态', width: 100 },
  { colKey: 'description', title: '入库说明', ellipsis: true, width: 150 },
  { colKey: 'action', title: '操作', width: 220, fixed: 'right' }
]

// 商品明细列
const productColumns = [
  { colKey: 'productCode', title: '商品编码', width: 120 },
  { colKey: 'productName', title: '商品名称', width: 150 },
  { colKey: 'specification', title: '规格', width: 100 },
  { colKey: 'unit', title: '单位', width: 80 },
  { colKey: 'quantity', title: '数量', width: 100 },
  { colKey: 'price', title: '单价', width: 100 },
  { colKey: 'amount', title: '金额', width: 120 },
  { colKey: 'action', title: '操作', width: 80 }
]

// 订单列表
const orderDialogVisible = ref(false)
const orderList = ref<any[]>([
  {
    id: '1',
    orderNo: 'CG202602190002',
    supplier: '供应商C',
    productCount: 4,
    totalQuantity: 300,
    totalAmount: 15000.00
  }
])
const orderPagination = reactive({
  current: 1,
  pageSize: 10,
  total: 1
})
const orderColumns = [
  { colKey: 'orderNo', title: '采购单号', width: 180 },
  { colKey: 'supplier', title: '供应商', width: 150 },
  { colKey: 'productCount', title: '商品数', width: 80 },
  { colKey: 'totalQuantity', title: '总数量', width: 100 },
  { colKey: 'totalAmount', title: '总金额', width: 120 },
  { colKey: 'action', title: '操作', width: 80, cell: () => '选择' }
]

// 编辑弹窗
const editDialogVisible = ref(false)
const activeTab = ref('basic')
const editFormRef = ref<FormInstanceFunctions>()
const editForm = reactive({
  id: '',
  inboundNo: '',
  inboundDate: '',
  inboundType: '',
  purchaseOrderNo: '',
  supplier: '',
  warehouse: '',
  operator: '',
  inboundStatus: '',
  description: '',
  internalRemark: '',
  approvalStatus: 'draft',
  products: []
})

const editRules: Record<string, FormRule[]> = {
  inboundDate: [{ required: true, message: '请选择入库日期', type: 'error' }],
  inboundType: [{ required: true, message: '请选择入库类型', type: 'error' }],
  warehouse: [{ required: true, message: '请选择入库仓库', type: 'error' }],
  operator: [{ required: true, message: '请选择经手人', type: 'error' }]
}

// 详情弹窗
const detailDialogVisible = ref(false)
const currentRow = ref<any>({})

// 审核弹窗
const approveDialogVisible = ref(false)
const approveForm = reactive({
  result: 'approved',
  remark: ''
})
const currentApproveRow = ref<any>(null)

// 计算总数量和总金额
const totalQuantity = computed(() => {
  return editForm.products.reduce((sum: number, item: any) => sum + (item.quantity || 0), 0)
})

const totalAmount = computed(() => {
  return editForm.products.reduce((sum: number, item: any) => sum + (item.amount || 0), 0)
})

// 方法
const handleSearch = () => {
  loading.value = true
  setTimeout(() => {
    loading.value = false
    MessagePlugin.success('查询成功')
  }, 500)
}

const handleReset = () => {
  searchForm.inboundNo = ''
  searchForm.purchaseOrderNo = ''
  searchForm.inboundType = ''
  searchForm.supplier = ''
  searchForm.dateRange = []
  searchForm.approvalStatus = ''
}

const handlePageChange = (pageInfo: any) => {
  pagination.current = pageInfo.current
  pagination.pageSize = pageInfo.pageSize
}

const handleAdd = () => {
  activeTab.value = 'basic'
  Object.assign(editForm, {
    id: '',
    inboundNo: `RK${Date.now()}`,
    inboundDate: new Date().toISOString().split('T')[0],
    inboundType: '',
    purchaseOrderNo: '',
    supplier: '',
    warehouse: '',
    operator: '',
    inboundStatus: '',
    description: '',
    internalRemark: '',
    approvalStatus: 'draft',
    products: []
  })
  editDialogVisible.value = true
}

const handleEdit = (row: any) => {
  activeTab.value = 'basic'
  Object.assign(editForm, row)
  editDialogVisible.value = true
}

const handleView = (row: any) => {
  currentRow.value = row
  detailDialogVisible.value = true
}

const handleDelete = (row: any) => {
  MessagePlugin.warning('删除功能待后端接口支持')
}

const handleBatchDelete = () => {
  if (selectedRowKeys.value.length === 0) {
    MessagePlugin.warning('请选择要删除的入库单')
    return
  }
  MessagePlugin.warning('批量删除功能待后端接口支持')
}

const handleExport = () => {
  MessagePlugin.info('导出功能开发中')
}

const handlePrint = () => {
  MessagePlugin.info('打印功能开发中')
}

const handleFromOrder = () => {
  orderDialogVisible.value = true
}

const handleSelectOrder = () => {
  orderDialogVisible.value = true
}

const handleSelectOrderRow = (row: any) => {
  editForm.purchaseOrderNo = row.orderNo
  editForm.supplier = row.supplier
  editForm.products = row.products || []
  orderDialogVisible.value = false
}

const handleEditDialogClose = () => {
  editFormRef.value?.reset()
}

const handleSubmit = async () => {
  const valid = await editFormRef.value?.validate()
  if (valid) {
    MessagePlugin.success('保存成功')
    editDialogVisible.value = false
  }
}

const handleAddProduct = () => {
  editForm.products.push({
    id: String(Date.now()),
    productCode: '',
    productName: '',
    specification: '',
    unit: '',
    quantity: 0,
    price: 0,
    amount: 0
  })
}

const handleRemoveProduct = (row: any) => {
  const index = editForm.products.findIndex((item: any) => item.id === row.id)
  if (index > -1) {
    editForm.products.splice(index, 1)
  }
  calculateTotal()
}

const calculateTotal = () => {
  // 计算每个商品的金额
  editForm.products.forEach((item: any) => {
    item.amount = (item.quantity || 0) * (item.price || 0)
  })
}

const handleApprove = (row: any) => {
  currentApproveRow.value = row
  approveForm.result = 'approved'
  approveForm.remark = ''
  approveDialogVisible.value = true
}

const handleApproveConfirm = () => {
  if (currentApproveRow.value) {
    currentApproveRow.value.approvalStatus = approveForm.result
    MessagePlugin.success(approveForm.result === 'approved' ? '审核通过' : '已驳回')
    approveDialogVisible.value = false
  }
}

// 辅助函数
const getInboundTypeText = (type: string) => {
  const map: Record<string, string> = {
    purchase: '采购入库',
    return: '退货入库',
    transfer: '调拨入库',
    production: '生产入库',
    other: '其他入库'
  }
  return map[type] || type
}

const getInboundTypeTheme = (type: string) => {
  const map: Record<string, string> = {
    purchase: 'primary',
    return: 'success',
    transfer: 'warning',
    production: 'default',
    other: 'default'
  }
  return map[type] || 'default'
}

const getApprovalStatusText = (status: string) => {
  const map: Record<string, string> = {
    draft: '草稿',
    pending: '待审核',
    approved: '已审核',
    rejected: '已驳回'
  }
  return map[status] || status
}

const getApprovalStatusTheme = (status: string) => {
  const map: Record<string, string> = {
    draft: 'default',
    pending: 'warning',
    approved: 'success',
    rejected: 'danger'
  }
  return map[status] || 'default'
}

const formatMoney = (amount: number) => {
  return (amount || 0).toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ',')
}
</script>

<script lang="ts">
import { defineComponent, h, computed } from 'vue'

const StatisticItem = defineComponent({
  name: 'StatisticItem',
  props: {
    title: String,
    value: [Number, String],
    precision: { type: Number, default: 0 },
    prefix: String,
    loading: Boolean,
    icon: String,
    color: { type: String, default: '#0052D9' }
  },
  setup(props) {
    const displayValue = computed(() => {
      if (props.loading) return '-'
      if (typeof props.value === 'number') {
        return props.value.toFixed(props.precision)
      }
      return props.value
    })
    
    return () => h('div', { class: 'statistic-item' }, [
      h('div', { class: 'statistic-icon', style: { color: props.color } }, [
        h('t-icon', { name: props.icon, size: '32px' })
      ]),
      h('div', { class: 'statistic-content' }, [
        h('div', { class: 'statistic-title' }, props.title),
        h('div', { class: 'statistic-value', style: { color: props.color } }, [
          props.prefix,
          displayValue.value
        ])
      ])
    ])
  }
})

const components = { StatisticItem }
</script>

<style scoped lang="less">
.inbound-container {
  padding: 16px;
  
  .statistics-cards {
    margin-bottom: 16px;
    
    .statistic-item {
      display: flex;
      align-items: center;
      
      .statistic-icon {
        margin-right: 16px;
      }
      
      .statistic-content {
        flex: 1;
        
        .statistic-title {
          font-size: 14px;
          color: var(--td-text-color-secondary);
          margin-bottom: 8px;
        }
        
        .statistic-value {
          font-size: 28px;
          font-weight: bold;
        }
      }
    }
  }
  
  .search-card {
    margin-bottom: 16px;
    
    .search-actions {
      display: flex;
      justify-content: flex-end;
    }
  }
  
  .action-card {
    margin-bottom: 16px;
  }
  
  .detail-actions {
    margin-bottom: 16px;
    display: flex;
    justify-content: flex-end;
  }
  
  .summary-info {
    margin-top: 16px;
  }
  
  .dialog-footer {
    margin-top: 24px;
    display: flex;
    justify-content: flex-end;
  }
}
</style>
