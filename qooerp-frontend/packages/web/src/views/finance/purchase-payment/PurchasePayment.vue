<template>
  <div class="purchase-payment-container">
    <!-- 统计卡片 -->
    <t-row :gutter="[16, 16]" class="statistics-cards">
      <t-col :span="6">
        <t-card>
          <statistic-item
            title="本月付款单数"
            :value="statistics.totalCount"
            :loading="loading"
            icon="money-circle"
            color="#0052D9"
          />
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card>
          <statistic-item
            title="本月付款金额"
            :value="statistics.totalAmount"
            :precision="2"
            prefix="¥"
            :loading="loading"
            icon="chart-column"
            color="#00A870"
          />
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card>
          <statistic-item
            title="待付款金额"
            :value="statistics.pendingAmount"
            :precision="2"
            prefix="¥"
            :loading="loading"
            icon="time"
            color="#ED7B2F"
          />
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card>
          <statistic-item
            title="未付款订单"
            :value="statistics.unpaidOrderCount"
            :loading="loading"
            icon="shopping-cart"
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
            <t-form-item label="付款单号" name="paymentNo">
              <t-input v-model="searchForm.paymentNo" placeholder="请输入付款单号" clearable />
            </t-form-item>
          </t-col>
          <t-col :span="4">
            <t-form-item label="采购单号" name="purchaseOrderNo">
              <t-input v-model="searchForm.purchaseOrderNo" placeholder="请输入采购单号" clearable />
            </t-form-item>
          </t-col>
          <t-col :span="4">
            <t-form-item label="供应商" name="supplier">
              <t-input v-model="searchForm.supplier" placeholder="请输入供应商" clearable />
            </t-form-item>
          </t-col>
          <t-col :span="4">
            <t-form-item label="付款日期" name="dateRange">
              <t-date-range-picker
                v-model="searchForm.dateRange"
                placeholder="请选择日期范围"
                clearable
              />
            </t-form-item>
          </t-col>
          <t-col :span="4">
            <t-form-item label="付款状态" name="paymentStatus">
              <t-select v-model="searchForm.paymentStatus" placeholder="请选择付款状态" clearable>
                <t-option value="draft" label="草稿" />
                <t-option value="partial" label="部分付款" />
                <t-option value="paid" label="已付款" />
              </t-select>
            </t-form-item>
          </t-col>
          <t-col :span="4">
            <t-form-item label="付款方式" name="paymentMethod">
              <t-select v-model="searchForm.paymentMethod" placeholder="请选择付款方式" clearable>
                <t-option value="现金" label="现金" />
                <t-option value="转账" label="转账" />
                <t-option value="支票" label="支票" />
                <t-option value="其他" label="其他" />
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
          新增付款
        </t-button>
        <t-button theme="default" @click="handleFromOrder">
          <template #icon><t-icon name="shopping-cart" /></template>
          从订单付款
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
        <template #paymentStatus="{ row }">
          <t-tag :theme="getPaymentStatusTheme(row.paymentStatus)">
            {{ getPaymentStatusText(row.paymentStatus) }}
          </t-tag>
        </template>
        <template #paymentMethod="{ row }">
          <t-tag variant="light" theme="primary">{{ row.paymentMethod }}</t-tag>
        </template>
        <template #action="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleView(row)">详情</t-link>
            <t-link theme="primary" @click="handleEdit(row)">编辑</t-link>
            <t-link v-if="row.paymentStatus !== 'paid'" theme="success" @click="handleConfirmPay(row)">
              确认付款
            </t-link>
            <t-link theme="danger" @click="handleDelete(row)">删除</t-link>
          </t-space>
        </template>
      </t-table>
    </t-card>

    <!-- 编辑弹窗 -->
    <t-dialog
      v-model:visible="editDialogVisible"
      header="采购付款单"
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
                <t-form-item label="付款单号" name="paymentNo">
                  <t-input v-model="editForm.paymentNo" placeholder="自动生成" disabled />
                </t-form-item>
              </t-col>
              <t-col :span="12">
                <t-form-item label="付款日期" name="paymentDate">
                  <t-date-picker v-model="editForm.paymentDate" placeholder="请选择付款日期" />
                </t-form-item>
              </t-col>
            </t-row>
            <t-row :gutter="16">
              <t-col :span="12">
                <t-form-item label="采购订单" name="purchaseOrderNo">
                  <t-input v-model="editForm.purchaseOrderNo" placeholder="请选择采购订单" @click="handleSelectOrder">
                    <template #suffix-icon>
                      <t-icon name="search" />
                    </template>
                  </t-input>
                </t-form-item>
              </t-col>
              <t-col :span="12">
                <t-form-item label="供应商" name="supplier">
                  <t-input v-model="editForm.supplier" placeholder="供应商" disabled />
                </t-form-item>
              </t-col>
            </t-row>
            <t-row :gutter="16">
              <t-col :span="12">
                <t-form-item label="联系人" name="contact">
                  <t-input v-model="editForm.contact" placeholder="联系人" disabled />
                </t-form-item>
              </t-col>
              <t-col :span="12">
                <t-form-item label="联系电话" name="phone">
                  <t-input v-model="editForm.phone" placeholder="联系电话" disabled />
                </t-form-item>
              </t-col>
            </t-row>
            <t-row :gutter="16">
              <t-col :span="12">
                <t-form-item label="收款账户" name="receiverAccount">
                  <t-input v-model="editForm.receiverAccount" placeholder="请输入收款账户" />
                </t-form-item>
              </t-col>
              <t-col :span="12">
                <t-form-item label="收款人" name="receiver">
                  <t-input v-model="editForm.receiver" placeholder="请输入收款人" />
                </t-form-item>
              </t-col>
            </t-row>
            <t-row :gutter="16">
              <t-col :span="12">
                <t-form-item label="付款方式" name="paymentMethod">
                  <t-select v-model="editForm.paymentMethod" placeholder="请选择付款方式">
                    <t-option value="现金" label="现金" />
                    <t-option value="转账" label="转账" />
                    <t-option value="支票" label="支票" />
                    <t-option value="其他" label="其他" />
                  </t-select>
                </t-form-item>
              </t-col>
              <t-col :span="12">
                <t-form-item label="币种" name="currency">
                  <t-select v-model="editForm.currency" placeholder="请选择币种">
                    <t-option value="CNY" label="人民币" />
                    <t-option value="USD" label="美元" />
                    <t-option value="EUR" label="欧元" />
                  </t-select>
                </t-form-item>
              </t-col>
            </t-row>
            <t-row :gutter="16">
              <t-col :span="12">
                <t-form-item label="汇率" name="exchangeRate">
                  <t-input-number v-model="editForm.exchangeRate" placeholder="汇率" :min="0" :precision="4" />
                </t-form-item>
              </t-col>
              <t-col :span="12">
                <t-form-item label="付款账户" name="payerAccount">
                  <t-select v-model="editForm.payerAccount" placeholder="请选择付款账户">
                    <t-option value="招商银行" label="招商银行" />
                    <t-option value="工商银行" label="工商银行" />
                    <t-option value="建设银行" label="建设银行" />
                  </t-select>
                </t-form-item>
              </t-col>
            </t-row>
            <t-form-item label="付款说明" name="description">
              <t-textarea
                v-model="editForm.description"
                placeholder="请输入付款说明"
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
        <t-tab-panel value="details" label="付款明细">
          <div class="detail-actions">
            <t-button theme="primary" size="small" @click="handleAddPaymentDetail">
              <template #icon><t-icon name="add" /></template>
              添加付款明细
            </t-button>
          </div>
          <t-table
            :data="editForm.payments"
            :columns="paymentColumns"
            :pagination="false"
            row-key="id"
          >
            <template #paymentDate="{ row }">
              <t-date-picker v-model="row.paymentDate" placeholder="请选择日期" size="small" />
            </template>
            <template #amount="{ row }">
              <t-input-number v-model="row.amount" placeholder="金额" size="small" :min="0" :precision="2" @change="calculateTotal" />
            </template>
            <template #action="{ row }">
              <t-button theme="danger" size="small" @click="handleRemovePaymentDetail(row)">
                <template #icon><t-icon name="delete" /></template>
              </t-button>
            </template>
          </t-table>
          <div class="summary-info">
            <t-descriptions :column="4" bordered>
              <t-descriptions-item label="付款次数">{{ editForm.payments.length }}</t-descriptions-item>
              <t-descriptions-item label="订单总金额">¥{{ formatMoney(editForm.orderAmount) }}</t-descriptions-item>
              <t-descriptions-item label="已付金额">¥{{ formatMoney(totalPaidAmount) }}</t-descriptions-item>
              <t-descriptions-item label="未付金额">¥{{ formatMoney(editForm.orderAmount - totalPaidAmount) }}</t-descriptions-item>
            </t-descriptions>
          </div>
        </t-tab-panel>
        <t-tab-panel value="summary" label="汇总信息">
          <t-descriptions :column="2" bordered>
            <t-descriptions-item label="采购单号">{{ editForm.purchaseOrderNo }}</t-descriptions-item>
            <t-descriptions-item label="供应商">{{ editForm.supplier }}</t-descriptions-item>
            <t-descriptions-item label="订单总金额">¥{{ formatMoney(editForm.orderAmount) }}</t-descriptions-item>
            <t-descriptions-item label="已付金额">¥{{ formatMoney(totalPaidAmount) }}</t-descriptions-item>
            <t-descriptions-item label="未付金额">¥{{ formatMoney(editForm.orderAmount - totalPaidAmount) }}</t-descriptions-item>
            <t-descriptions-item label="付款状态">
              <t-tag :theme="getPaymentStatusTheme(editForm.paymentStatus)">
                {{ getPaymentStatusText(editForm.paymentStatus) }}
              </t-tag>
            </t-descriptions-item>
            <t-descriptions-item label="付款方式">{{ editForm.paymentMethod }}</t-descriptions-item>
            <t-descriptions-item label="创建时间">{{ editForm.createTime }}</t-descriptions-item>
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
    <t-dialog v-model:visible="detailDialogVisible" header="付款单详情" width="1000px" :footer="false">
      <t-tabs theme="card">
        <t-tab-panel value="basic" label="基本信息">
          <t-descriptions :column="2" bordered>
            <t-descriptions-item label="付款单号">{{ currentRow.paymentNo }}</t-descriptions-item>
            <t-descriptions-item label="付款日期">{{ currentRow.paymentDate }}</t-descriptions-item>
            <t-descriptions-item label="采购订单">{{ currentRow.purchaseOrderNo }}</t-descriptions-item>
            <t-descriptions-item label="供应商">{{ currentRow.supplier }}</t-descriptions-item>
            <t-descriptions-item label="联系人">{{ currentRow.contact }}</t-descriptions-item>
            <t-descriptions-item label="联系电话">{{ currentRow.phone }}</t-descriptions-item>
            <t-descriptions-item label="收款账户">{{ currentRow.receiverAccount }}</t-descriptions-item>
            <t-descriptions-item label="收款人">{{ currentRow.receiver }}</t-descriptions-item>
            <t-descriptions-item label="付款方式">{{ currentRow.paymentMethod }}</t-descriptions-item>
            <t-descriptions-item label="币种">{{ currentRow.currency }}</t-descriptions-item>
            <t-descriptions-item label="汇率">{{ currentRow.exchangeRate }}</t-descriptions-item>
            <t-descriptions-item label="付款账户">{{ currentRow.payerAccount }}</t-descriptions-item>
            <t-descriptions-item label="付款状态" :span="2">
              <t-tag :theme="getPaymentStatusTheme(currentRow.paymentStatus)">
                {{ getPaymentStatusText(currentRow.paymentStatus) }}
              </t-tag>
            </t-descriptions-item>
            <t-descriptions-item label="付款说明" :span="2">{{ currentRow.description }}</t-descriptions-item>
            <t-descriptions-item label="内部备注" :span="2">{{ currentRow.internalRemark }}</t-descriptions-item>
            <t-descriptions-item label="创建时间">{{ currentRow.createTime }}</t-descriptions-item>
            <t-descriptions-item label="更新时间">{{ currentRow.updateTime }}</t-descriptions-item>
          </t-descriptions>
        </t-tab-panel>
        <t-tab-panel value="details" label="付款明细">
          <t-table :data="currentRow.payments" :columns="paymentColumns" :pagination="false">
            <template #paymentDate="{ row }">{{ row.paymentDate }}</template>
            <template #amount="{ row }">¥{{ formatMoney(row.amount) }}</template>
          </t-table>
        </t-tab-panel>
        <t-tab-panel value="summary" label="汇总信息">
          <t-descriptions :column="2" bordered>
            <t-descriptions-item label="采购单号">{{ currentRow.purchaseOrderNo }}</t-descriptions-item>
            <t-descriptions-item label="供应商">{{ currentRow.supplier }}</t-descriptions-item>
            <t-descriptions-item label="订单总金额">¥{{ formatMoney(currentRow.orderAmount) }}</t-descriptions-item>
            <t-descriptions-item label="已付金额">¥{{ formatMoney(currentRow.paidAmount) }}</t-descriptions-item>
            <t-descriptions-item label="未付金额">¥{{ formatMoney(currentRow.orderAmount - currentRow.paidAmount) }}</t-descriptions-item>
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

    <!-- 确认付款弹窗 -->
    <t-dialog v-model:visible="confirmPayDialogVisible" header="确认付款" width="600px" @confirm="handleConfirmPaySubmit">
      <t-form :data="confirmPayForm" label-width="100px">
        <t-form-item label="付款账户">
          <t-select v-model="confirmPayForm.account" placeholder="请选择付款账户">
            <t-option value="招商银行" label="招商银行" />
            <t-option value="工商银行" label="工商银行" />
            <t-option value="建设银行" label="建设银行" />
          </t-select>
        </t-form-item>
        <t-form-item label="付款金额">
          <t-input-number v-model="confirmPayForm.amount" placeholder="付款金额" :min="0" :precision="2" />
        </t-form-item>
        <t-form-item label="付款方式">
          <t-select v-model="confirmPayForm.paymentMethod" placeholder="请选择付款方式">
            <t-option value="现金" label="现金" />
            <t-option value="转账" label="转账" />
            <t-option value="支票" label="支票" />
          </t-select>
        </t-form-item>
        <t-form-item label="付款说明">
          <t-textarea v-model="confirmPayForm.remark" placeholder="请输入付款说明" :autosize="{ minRows: 2 }" />
        </t-form-item>
      </t-form>
    </t-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { MessagePlugin } from 'tdesign-vue-next'
import type { FormInstanceFunctions, FormRule } from 'tdesign-vue-next'

defineOptions({ name: 'PurchasePayment' })

// 统计数据
const statistics = reactive({
  totalCount: 86,
  totalAmount: 32560.00,
  pendingAmount: 18420.50,
  unpaidOrderCount: 23
})

// 搜索表单
const searchForm = reactive({
  paymentNo: '',
  purchaseOrderNo: '',
  supplier: '',
  dateRange: [],
  paymentStatus: '',
  paymentMethod: ''
})

// 表格数据
const loading = ref(false)
const tableData = ref<any[]>([
  {
    id: '1',
    paymentNo: 'FK202602190001',
    paymentDate: '2026-02-19',
    purchaseOrderNo: 'CG202602190001',
    supplier: '供应商A',
    contact: '张经理',
    phone: '13800138001',
    receiverAccount: '6222 0000 0000 0000',
    receiver: '供应商A',
    paymentMethod: '转账',
    currency: 'CNY',
    exchangeRate: 1.0000,
    payerAccount: '招商银行',
    paymentStatus: 'partial',
    orderAmount: 15000.00,
    paidAmount: 8000.00,
    description: '第一批货款',
    internalRemark: '',
    createTime: '2026-02-19 10:00:00',
    updateTime: '2026-02-19 14:00:00',
    payments: [
      { id: '1', paymentDate: '2026-02-19', amount: 8000.00, description: '首款' }
    ]
  },
  {
    id: '2',
    paymentNo: 'FK202602180001',
    paymentDate: '2026-02-18',
    purchaseOrderNo: 'CG202602180001',
    supplier: '供应商B',
    contact: '李经理',
    phone: '13900139002',
    receiverAccount: '6222 0000 0000 0001',
    receiver: '供应商B',
    paymentMethod: '支票',
    currency: 'CNY',
    exchangeRate: 1.0000,
    payerAccount: '工商银行',
    paymentStatus: 'paid',
    orderAmount: 12000.00,
    paidAmount: 12000.00,
    description: '货款全额支付',
    internalRemark: '',
    createTime: '2026-02-18 15:30:00',
    updateTime: '2026-02-18 16:00:00',
    payments: [
      { id: '1', paymentDate: '2026-02-18', amount: 12000.00, description: '全额' }
    ]
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
  { colKey: 'paymentNo', title: '付款单号', width: 150 },
  { colKey: 'paymentDate', title: '付款日期', width: 120 },
  { colKey: 'purchaseOrderNo', title: '采购订单', width: 150 },
  { colKey: 'supplier', title: '供应商', width: 150 },
  { colKey: 'orderAmount', title: '订单金额', width: 120, cell: (h: any, { row }: any) => `¥${formatMoney(row.orderAmount)}` },
  { colKey: 'paidAmount', title: '已付金额', width: 120, cell: (h: any, { row }: any) => `¥${formatMoney(row.paidAmount)}` },
  { colKey: 'paymentStatus', title: '付款状态', width: 100 },
  { colKey: 'paymentMethod', title: '付款方式', width: 100 },
  { colKey: 'description', title: '付款说明', ellipsis: true, width: 150 },
  { colKey: 'action', title: '操作', width: 220, fixed: 'right' }
]

// 付款明细列
const paymentColumns = [
  { colKey: 'paymentDate', title: '付款日期', width: 150 },
  { colKey: 'amount', title: '付款金额', width: 150 },
  { colKey: 'description', title: '付款说明', width: 250 },
  { colKey: 'action', title: '操作', width: 80 }
]

// 订单列表
const orderDialogVisible = ref(false)
const orderList = ref<any[]>([
  {
    id: '1',
    orderNo: 'CG202602190002',
    supplier: '供应商C',
    orderAmount: 20000.00,
    paidAmount: 0
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
  { colKey: 'orderAmount', title: '订单金额', width: 120 },
  { colKey: 'paidAmount', title: '已付金额', width: 120 },
  { colKey: 'action', title: '操作', width: 80, cell: () => '选择' }
]

// 编辑弹窗
const editDialogVisible = ref(false)
const activeTab = ref('basic')
const editFormRef = ref<FormInstanceFunctions>()
const editForm = reactive({
  id: '',
  paymentNo: '',
  paymentDate: '',
  purchaseOrderNo: '',
  supplier: '',
  contact: '',
  phone: '',
  receiverAccount: '',
  receiver: '',
  paymentMethod: '',
  currency: 'CNY',
  exchangeRate: 1.0000,
  payerAccount: '',
  description: '',
  internalRemark: '',
  orderAmount: 0,
  payments: [],
  paymentStatus: 'draft'
})

const editRules: Record<string, FormRule[]> = {
  paymentDate: [{ required: true, message: '请选择付款日期', type: 'error' }],
  purchaseOrderNo: [{ required: true, message: '请选择采购订单', type: 'error' }],
  paymentMethod: [{ required: true, message: '请选择付款方式', type: 'error' }]
}

// 详情弹窗
const detailDialogVisible = ref(false)
const currentRow = ref<any>({})

// 确认付款弹窗
const confirmPayDialogVisible = ref(false)
const confirmPayForm = reactive({
  account: '',
  amount: 0,
  paymentMethod: '',
  remark: ''
})
const currentPayRow = ref<any>(null)

// 计算已付金额
const totalPaidAmount = computed(() => {
  return editForm.payments.reduce((sum: number, item: any) => sum + (item.amount || 0), 0)
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
  searchForm.paymentNo = ''
  searchForm.purchaseOrderNo = ''
  searchForm.supplier = ''
  searchForm.dateRange = []
  searchForm.paymentStatus = ''
  searchForm.paymentMethod = ''
}

const handlePageChange = (pageInfo: any) => {
  pagination.current = pageInfo.current
  pagination.pageSize = pageInfo.pageSize
}

const handleAdd = () => {
  activeTab.value = 'basic'
  Object.assign(editForm, {
    id: '',
    paymentNo: `FK${Date.now()}`,
    paymentDate: new Date().toISOString().split('T')[0],
    purchaseOrderNo: '',
    supplier: '',
    contact: '',
    phone: '',
    receiverAccount: '',
    receiver: '',
    paymentMethod: '',
    currency: 'CNY',
    exchangeRate: 1.0000,
    payerAccount: '',
    description: '',
    internalRemark: '',
    orderAmount: 0,
    payments: [],
    paymentStatus: 'draft'
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
    MessagePlugin.warning('请选择要删除的付款单')
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
  editForm.orderAmount = row.orderAmount
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

const handleAddPaymentDetail = () => {
  editForm.payments.push({
    id: String(Date.now()),
    paymentDate: new Date().toISOString().split('T')[0],
    amount: 0,
    description: ''
  })
}

const handleRemovePaymentDetail = (row: any) => {
  const index = editForm.payments.findIndex((item: any) => item.id === row.id)
  if (index > -1) {
    editForm.payments.splice(index, 1)
  }
  calculateTotal()
}

const calculateTotal = () => {
  // 自动计算总金额
}

const handleConfirmPay = (row: any) => {
  currentPayRow.value = row
  confirmPayForm.account = row.payerAccount
  confirmPayForm.amount = row.orderAmount - row.paidAmount
  confirmPayForm.paymentMethod = row.paymentMethod
  confirmPayForm.remark = ''
  confirmPayDialogVisible.value = true
}

const handleConfirmPaySubmit = () => {
  if (currentPayRow.value) {
    currentPayRow.value.paidAmount += confirmPayForm.amount
    currentPayRow.value.paymentStatus = currentPayRow.value.paidAmount >= currentPayRow.value.orderAmount ? 'paid' : 'partial'
    MessagePlugin.success('付款成功')
    confirmPayDialogVisible.value = false
  }
}

// 辅助函数
const getPaymentStatusText = (status: string) => {
  const map: Record<string, string> = {
    draft: '草稿',
    partial: '部分付款',
    paid: '已付款'
  }
  return map[status] || status
}

const getPaymentStatusTheme = (status: string) => {
  const map: Record<string, string> = {
    draft: 'default',
    partial: 'warning',
    paid: 'success'
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
.purchase-payment-container {
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
