<template>
  <div class="sales-quote-container">
    <!-- 统计卡片 -->
    <t-card class="stats-card">
      <t-row :gutter="16">
        <t-col :span="6">
          <div class="stat-item">
            <div class="stat-icon">
              <t-icon name="file-paste" size="24px" />
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stats.totalCount }}</div>
              <div class="stat-label">报价单数</div>
            </div>
          </div>
        </t-col>
        <t-col :span="6">
          <div class="stat-item">
            <div class="stat-icon stat-icon-warning">
              <t-icon name="time" size="24px" />
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stats.pendingCount }}</div>
              <div class="stat-label">待确认</div>
            </div>
          </div>
        </t-col>
        <t-col :span="6">
          <div class="stat-item">
            <div class="stat-icon stat-icon-success">
              <t-icon name="check-circle" size="24px" />
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stats.convertedCount }}</div>
              <div class="stat-label">已转订单</div>
            </div>
          </div>
        </t-col>
        <t-col :span="6">
          <div class="stat-item">
            <div class="stat-icon stat-icon-info">
              <t-icon name="money-circle" size="24px" />
            </div>
            <div class="stat-content">
              <div class="stat-value">¥{{ formatMoney(stats.totalAmount) }}</div>
              <div class="stat-label">报价金额</div>
            </div>
          </div>
        </t-col>
      </t-row>
    </t-card>

    <!-- 搜索卡片 -->
    <t-card class="search-card">
      <t-form :data="searchForm" layout="inline" @submit="handleSearch" @reset="handleReset">
        <t-form-item label="报价单号" name="quoteNo">
          <t-input v-model="searchForm.quoteNo" placeholder="请输入报价单号" clearable />
        </t-form-item>
        <t-form-item label="客户" name="customerId">
          <t-select v-model="searchForm.customerId" placeholder="请选择客户" clearable>
            <t-option value="1" label="客户A" />
            <t-option value="2" label="客户B" />
          </t-select>
        </t-form-item>
        <t-form-item label="报价状态" name="status">
          <t-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <t-option value="0" label="草稿" />
            <t-option value="1" label="已发送" />
            <t-option value="2" label="已确认" />
            <t-option value="3" label="已拒绝" />
            <t-option value="4" label="已过期" />
            <t-option value="5" label="已转订单" />
          </t-select>
        </t-form-item>
        <t-form-item label="有效期" name="validityDate">
          <t-date-range-picker v-model="searchForm.validityDate" clearable />
        </t-form-item>
        <t-form-item>
          <t-space>
            <t-button theme="primary" type="submit">查询</t-button>
            <t-button theme="default" type="reset">重置</t-button>
          </t-space>
        </t-form-item>
      </t-form>
    </t-card>

    <!-- 操作栏 -->
    <div class="action-bar">
      <t-space>
        <t-button theme="primary" @click="handleAdd">
          <template #icon><t-icon name="add" /></template>
          新增报价
        </t-button>
        <t-button theme="default" @click="handleBatchSend">
          <template #icon><t-icon name="mail" /></template>
          批量发送
        </t-button>
        <t-button theme="default" @click="handleExport">
          <template #icon><t-icon name="download" /></template>
          导出报表
        </t-button>
      </t-space>
    </div>

    <!-- 报价列表表格 -->
    <t-card class="table-card">
      <t-table
        :data="tableData"
        :columns="columns"
        :loading="loading"
        :pagination="pagination"
        :selected-row-keys="selectedRowKeys"
        row-key="id"
        @page-change="handlePageChange"
        @select-change="handleSelectChange"
      >
        <template #status="{ row }">
          <t-tag v-if="row.status === 0" theme="default">草稿</t-tag>
          <t-tag v-else-if="row.status === 1" theme="warning">已发送</t-tag>
          <t-tag v-else-if="row.status === 2" theme="success">已确认</t-tag>
          <t-tag v-else-if="row.status === 3" theme="danger">已拒绝</t-tag>
          <t-tag v-else-if="row.status === 4" theme="warning">已过期</t-tag>
          <t-tag v-else-if="row.status === 5" theme="success">已转订单</t-tag>
        </template>

        <template #totalAmount="{ row }">
          <span class="amount-text">¥{{ formatMoney(row.totalAmount) }}</span>
        </template>

        <template #validUntil="{ row }">
          <span :class="{ 'text-danger': isExpired(row.validUntil) }">
            {{ row.validUntil }}
          </span>
        </template>

        <template #action="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleView(row)">查看</t-link>
            <t-link v-if="row.status === 0" theme="primary" @click="handleSend(row)">发送</t-link>
            <t-link v-if="row.status === 2" theme="primary" @click="handleToOrder(row)">转订单</t-link>
            <t-link v-if="row.status === 0" theme="primary" @click="handleEdit(row)">编辑</t-link>
            <t-link v-if="row.status === 0" theme="primary" @click="handleCopy(row)">复制</t-link>
            <t-link v-if="row.status === 0 || row.status === 4" theme="danger" @click="handleDelete(row)">删除</t-link>
          </t-space>
        </template>
      </t-table>
    </t-card>

    <!-- 报价单编辑弹窗 -->
    <t-dialog
      v-model:visible="editDialogVisible"
      :header="isEdit ? '编辑报价单' : '新增报价单'"
      :footer="false"
      width="900px"
    >
      <t-tabs v-model="activeTab">
        <t-tab-panel value="basic" label="基本信息">
          <t-form :data="editForm" :label-width="100">
            <t-form-item label="报价单号" name="quoteNo">
              <t-input v-model="editForm.quoteNo" placeholder="自动生成" disabled />
            </t-form-item>
            <t-form-item label="客户" name="customerId" required>
              <t-select v-model="editForm.customerId" placeholder="请选择客户">
                <t-option value="1" label="客户A" />
                <t-option value="2" label="客户B" />
              </t-select>
            </t-form-item>
            <t-form-item label="联系人" name="contactId" required>
              <t-select v-model="editForm.contactId" placeholder="请选择联系人">
                <t-option value="1" label="张三" />
                <t-option value="2" label="李四" />
              </t-select>
            </t-form-item>
            <t-form-item label="报价日期" name="quoteDate" required>
              <t-date-picker v-model="editForm.quoteDate" placeholder="请选择报价日期" />
            </t-form-item>
            <t-form-item label="有效期至" name="validUntil" required>
              <t-date-picker v-model="editForm.validUntil" placeholder="请选择有效期" />
            </t-form-item>
            <t-form-item label="币种" name="currency" required>
              <t-select v-model="editForm.currency" placeholder="请选择币种">
                <t-option value="CNY" label="人民币(CNY)" />
                <t-option value="USD" label="美元(USD)" />
                <t-option value="EUR" label="欧元(EUR)" />
              </t-select>
            </t-form-item>
            <t-form-item label="税率" name="taxRate" required>
              <t-input-number v-model="editForm.taxRate" :min="0" :max="100" :decimal-places="2" />
              <span style="margin-left: 8px">%</span>
            </t-form-item>
            <t-form-item label="付款方式" name="paymentMethod" required>
              <t-select v-model="editForm.paymentMethod" placeholder="请选择付款方式">
                <t-option value="1" label="预付全款" />
                <t-option value="2" label="货到付款" />
                <t-option value="3" label="分期付款" />
                <t-option value="4" label="月结" />
              </t-select>
            </t-form-item>
            <t-form-item label="交货周期" name="deliveryPeriod" required>
              <t-input-number v-model="editForm.deliveryPeriod" :min="0" />
              <span style="margin-left: 8px">天</span>
            </t-form-item>
            <t-form-item label="备注" name="remark">
              <t-textarea v-model="editForm.remark" placeholder="请输入备注" :maxlength="500" />
            </t-form-item>
          </t-form>
        </t-tab-panel>

        <t-tab-panel value="detail" label="报价明细">
          <div class="detail-actions">
            <t-button size="small" theme="primary" @click="handleAddDetail">
              <template #icon><t-icon name="add" /></template>
              添加商品
            </t-button>
          </div>
          <t-table :data="editForm.details" :columns="detailColumns" :bordered="true" size="small">
            <template #quantity="{ row, rowIndex }">
              <t-input-number v-model="row.quantity" :min="1" size="small" @change="recalcDetail(row, rowIndex)" />
            </template>
            <template #unitPrice="{ row, rowIndex }">
              <t-input-number v-model="row.unitPrice" :min="0" :decimal-places="2" size="small" @change="recalcDetail(row, rowIndex)" />
            </template>
            <template #discountRate="{ row, rowIndex }">
              <t-input-number v-model="row.discountRate" :min="0" :max="100" :decimal-places="2" size="small" @change="recalcDetail(row, rowIndex)" />
              <span style="margin-left: 4px">%</span>
            </template>
            <template #amount="{ row }">
              <span class="amount-text">¥{{ formatMoney(row.amount) }}</span>
            </template>
            <template #action="{ row, rowIndex }">
              <t-link theme="danger" @click="handleRemoveDetail(rowIndex)">删除</t-link>
            </template>
          </t-table>
          
          <!-- 汇总信息 -->
          <div class="detail-summary">
            <t-row :gutter="16">
              <t-col :span="18"></t-col>
              <t-col :span="6">
                <div class="summary-item">
                  <span class="summary-label">商品小计:</span>
                  <span class="summary-value">¥{{ formatMoney(getSubtotal()) }}</span>
                </div>
                <div class="summary-item">
                  <span class="summary-label">折扣金额:</span>
                  <span class="summary-value text-danger">-¥{{ formatMoney(getDiscountAmount()) }}</span>
                </div>
                <div class="summary-item">
                  <span class="summary-label">税额:</span>
                  <span class="summary-value">¥{{ formatMoney(getTaxAmount()) }}</span>
                </div>
                <div class="summary-item total">
                  <span class="summary-label">报价总额:</span>
                  <span class="summary-value">¥{{ formatMoney(getTotalAmount()) }}</span>
                </div>
              </t-col>
            </t-row>
          </div>
        </t-tab-panel>

        <t-tab-panel value="terms" label="条款说明">
          <t-form :data="editForm" :label-width="120">
            <t-form-item label="报价说明" name="quoteRemark">
              <t-textarea v-model="editForm.quoteRemark" placeholder="请输入报价说明" :maxlength="500" />
            </t-form-item>
            <t-form-item label="交货条款" name="deliveryTerms">
              <t-textarea v-model="editForm.deliveryTerms" placeholder="请输入交货条款" :maxlength="500" />
            </t-form-item>
            <t-form-item label="质量保证" name="qualityGuarantee">
              <t-textarea v-model="editForm.qualityGuarantee" placeholder="请输入质量保证条款" :maxlength="500" />
            </t-form-item>
            <t-form-item label="其他条款" name="otherTerms">
              <t-textarea v-model="editForm.otherTerms" placeholder="请输入其他条款" :maxlength="500" />
            </t-form-item>
          </t-form>
        </t-tab-panel>

        <t-tab-panel value="summary" label="汇总信息">
          <t-descriptions :column="2" bordered>
            <t-descriptions-item label="报价单号">{{ editForm.quoteNo || '-' }}</t-descriptions-item>
            <t-descriptions-item label="客户">{{ getCustomerName(editForm.customerId) }}</t-descriptions-item>
            <t-descriptions-item label="联系人">{{ getContactName(editForm.contactId) }}</t-descriptions-item>
            <t-descriptions-item label="报价日期">{{ editForm.quoteDate || '-' }}</t-descriptions-item>
            <t-descriptions-item label="有效期至">{{ editForm.validUntil || '-' }}</t-descriptions-item>
            <t-descriptions-item label="币种">{{ editForm.currency || '-' }}</t-descriptions-item>
            <t-descriptions-item label="税率">{{ editForm.taxRate }}%</t-descriptions-item>
            <t-descriptions-item label="付款方式">{{ getPaymentMethodName(editForm.paymentMethod) }}</t-descriptions-item>
            <t-descriptions-item label="商品种类">{{ editForm.details.length }}</t-descriptions-item>
            <t-descriptions-item label="报价数量">{{ getTotalQuantity() }}</t-descriptions-item>
            <t-descriptions-item label="报价总额">¥{{ formatMoney(getTotalAmount()) }}</t-descriptions-item>
            <t-descriptions-item label="交货周期">{{ editForm.deliveryPeriod }}天</t-descriptions-item>
          </t-descriptions>
        </t-tab-panel>
      </t-tabs>

      <div class="dialog-footer">
        <t-space>
          <t-button theme="default" @click="editDialogVisible = false">取消</t-button>
          <t-button theme="primary" @click="handleSave">保存</t-button>
          <t-button v-if="!isEdit" theme="success" @click="handleSaveAndSend">保存并发送</t-button>
        </t-space>
      </div>
    </t-dialog>

    <!-- 报价单详情弹窗 -->
    <t-dialog v-model:visible="detailDialogVisible" header="报价单详情" :footer="false" width="900px">
      <t-tabs v-model="detailTab">
        <t-tab-panel value="info" label="基本信息">
          <t-descriptions :column="2" bordered>
            <t-descriptions-item label="报价单号">{{ currentDetail.quoteNo }}</t-descriptions-item>
            <t-descriptions-item label="状态">
              <t-tag v-if="currentDetail.status === 0" theme="default">草稿</t-tag>
              <t-tag v-else-if="currentDetail.status === 1" theme="warning">已发送</t-tag>
              <t-tag v-else-if="currentDetail.status === 2" theme="success">已确认</t-tag>
              <t-tag v-else-if="currentDetail.status === 3" theme="danger">已拒绝</t-tag>
              <t-tag v-else-if="currentDetail.status === 4" theme="warning">已过期</t-tag>
              <t-tag v-else-if="currentDetail.status === 5" theme="success">已转订单</t-tag>
            </t-descriptions-item>
            <t-descriptions-item label="客户">{{ currentDetail.customerName }}</t-descriptions-item>
            <t-descriptions-item label="联系人">{{ currentDetail.contactName }}</t-descriptions-item>
            <t-descriptions-item label="报价日期">{{ currentDetail.quoteDate }}</t-descriptions-item>
            <t-descriptions-item label="有效期至">{{ currentDetail.validUntil }}</t-descriptions-item>
            <t-descriptions-item label="币种">{{ currentDetail.currency }}</t-descriptions-item>
            <t-descriptions-item label="税率">{{ currentDetail.taxRate }}%</t-descriptions-item>
            <t-descriptions-item label="付款方式">{{ currentDetail.paymentMethodName }}</t-descriptions-item>
            <t-descriptions-item label="交货周期">{{ currentDetail.deliveryPeriod }}天</t-descriptions-item>
            <t-descriptions-item label="商品种类">{{ currentDetail.totalVarieties }}</t-descriptions-item>
            <t-descriptions-item label="报价数量">{{ currentDetail.totalQuantity }}</t-descriptions-item>
            <t-descriptions-item label="报价总额">¥{{ formatMoney(currentDetail.totalAmount) }}</t-descriptions-item>
            <t-descriptions-item label="备注" :span="2">{{ currentDetail.remark || '-' }}</t-descriptions-item>
            <t-descriptions-item label="创建时间">{{ currentDetail.createTime }}</t-descriptions-item>
            <t-descriptions-item label="创建人">{{ currentDetail.creator }}</t-descriptions-item>
          </t-descriptions>
        </t-tab-panel>

        <t-tab-panel value="details" label="报价明细">
          <t-table :data="currentDetail.details" :columns="viewDetailColumns" :bordered="true" size="small" />
          
          <!-- 汇总信息 -->
          <div class="detail-summary">
            <t-row :gutter="16">
              <t-col :span="18"></t-col>
              <t-col :span="6">
                <div class="summary-item">
                  <span class="summary-label">商品小计:</span>
                  <span class="summary-value">¥{{ formatMoney(currentDetail.subtotal) }}</span>
                </div>
                <div class="summary-item">
                  <span class="summary-label">折扣金额:</span>
                  <span class="summary-value text-danger">-¥{{ formatMoney(currentDetail.discountAmount) }}</span>
                </div>
                <div class="summary-item">
                  <span class="summary-label">税额:</span>
                  <span class="summary-value">¥{{ formatMoney(currentDetail.taxAmount) }}</span>
                </div>
                <div class="summary-item total">
                  <span class="summary-label">报价总额:</span>
                  <span class="summary-value">¥{{ formatMoney(currentDetail.totalAmount) }}</span>
                </div>
              </t-col>
            </t-row>
          </div>
        </t-tab-panel>

        <t-tab-panel value="terms" label="条款说明">
          <t-descriptions :column="1" bordered>
            <t-descriptions-item label="报价说明">{{ currentDetail.quoteRemark || '-' }}</t-descriptions-item>
            <t-descriptions-item label="交货条款">{{ currentDetail.deliveryTerms || '-' }}</t-descriptions-item>
            <t-descriptions-item label="质量保证">{{ currentDetail.qualityGuarantee || '-' }}</t-descriptions-item>
            <t-descriptions-item label="其他条款">{{ currentDetail.otherTerms || '-' }}</t-descriptions-item>
          </t-descriptions>
        </t-tab-panel>

        <t-tab-panel value="log" label="操作日志">
          <t-timeline>
            <t-timeline-item v-for="log in currentDetail.logs" :key="log.id" :label="log.time">
              {{ log.action }} - {{ log.operator }}
              <div class="log-remark">{{ log.remark }}</div>
            </t-timeline-item>
          </t-timeline>
        </t-tab-panel>
      </t-tabs>
    </t-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { MessagePlugin } from 'tdesign-vue-next'

// 统计数据
const stats = reactive({
  totalCount: 0,
  pendingCount: 0,
  convertedCount: 0,
  totalAmount: 0
})

// 搜索表单
const searchForm = reactive({
  quoteNo: '',
  customerId: '',
  status: '',
  validityDate: []
})

// 表格数据
const tableData = ref([])
const loading = ref(false)
const selectedRowKeys = ref([])
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0
})

// 表格列
const columns = [
  { colKey: 'quoteNo', title: '报价单号', width: 150 },
  { colKey: 'customerName', title: '客户', width: 120 },
  { colKey: 'contactName', title: '联系人', width: 100 },
  { colKey: 'quoteDate', title: '报价日期', width: 120 },
  { colKey: 'validUntil', title: '有效期至', width: 120 },
  { colKey: 'totalQuantity', title: '报价数量', width: 100 },
  { colKey: 'totalAmount', title: '报价金额', width: 120 },
  { colKey: 'status', title: '状态', width: 100 },
  { colKey: 'createTime', title: '创建时间', width: 150 },
  { colKey: 'action', title: '操作', width: 280, fixed: 'right' }
]

// 编辑弹窗
const editDialogVisible = ref(false)
const isEdit = ref(false)
const activeTab = ref('basic')
const editForm = reactive({
  id: '',
  quoteNo: '',
  customerId: '',
  contactId: '',
  quoteDate: '',
  validUntil: '',
  currency: 'CNY',
  taxRate: 13,
  paymentMethod: '1',
  deliveryPeriod: 7,
  remark: '',
  quoteRemark: '',
  deliveryTerms: '',
  qualityGuarantee: '',
  otherTerms: '',
  details: []
})

// 报价明细列
const detailColumns = [
  { colKey: 'productNo', title: '商品编号', width: 120 },
  { colKey: 'productName', title: '商品名称', width: 150 },
  { colKey: 'spec', title: '规格', width: 100 },
  { colKey: 'unit', title: '单位', width: 80 },
  { colKey: 'quantity', title: '数量', width: 100 },
  { colKey: 'unitPrice', title: '单价', width: 100 },
  { colKey: 'discountRate', title: '折扣率', width: 100 },
  { colKey: 'amount', title: '金额', width: 100 },
  { colKey: 'action', title: '操作', width: 80 }
]

// 详情弹窗
const detailDialogVisible = ref(false)
const detailTab = ref('info')
const currentDetail = ref({})

const viewDetailColumns = [
  { colKey: 'productNo', title: '商品编号', width: 120 },
  { colKey: 'productName', title: '商品名称', width: 150 },
  { colKey: 'spec', title: '规格', width: 100 },
  { colKey: 'unit', title: '单位', width: 80 },
  { colKey: 'quantity', title: '数量', width: 100 },
  { colKey: 'unitPrice', title: '单价', width: 100 },
  { colKey: 'discountRate', title: '折扣率', width: 100 },
  { colKey: 'amount', title: '金额', width: 100 }
]

// 格式化金额
const formatMoney = (value: number) => {
  return (value || 0).toFixed(2)
}

// 判断是否过期
const isExpired = (validUntil: string) => {
  return new Date(validUntil) < new Date()
}

// 获取客户名称
const getCustomerName = (customerId: string) => {
  const map: Record<string, string> = {
    '1': '客户A',
    '2': '客户B'
  }
  return map[customerId] || '-'
}

// 获取联系人名称
const getContactName = (contactId: string) => {
  const map: Record<string, string> = {
    '1': '张三',
    '2': '李四'
  }
  return map[contactId] || '-'
}

// 获取付款方式名称
const getPaymentMethodName = (paymentMethod: string) => {
  const map: Record<string, string> = {
    '1': '预付全款',
    '2': '货到付款',
    '3': '分期付款',
    '4': '月结'
  }
  return map[paymentMethod] || '-'
}

// 获取总数量
const getTotalQuantity = () => {
  return editForm.details.reduce((sum: number, item: any) => sum + (item.quantity || 0), 0)
}

// 获取小计
const getSubtotal = () => {
  return editForm.details.reduce((sum: number, item: any) => sum + ((item.quantity || 0) * (item.unitPrice || 0)), 0)
}

// 获取折扣金额
const getDiscountAmount = () => {
  return editForm.details.reduce((sum: number, item: any) => {
    const amount = (item.quantity || 0) * (item.unitPrice || 0)
    return sum + (amount * (item.discountRate || 0) / 100)
  }, 0)
}

// 获取税额
const getTaxAmount = () => {
  const subtotal = getSubtotal()
  const discount = getDiscountAmount()
  const taxable = subtotal - discount
  return taxable * (editForm.taxRate || 0) / 100
}

// 获取总金额
const getTotalAmount = () => {
  const subtotal = getSubtotal()
  const discount = getDiscountAmount()
  const tax = getTaxAmount()
  return subtotal - discount + tax
}

// 重新计算明细金额
const recalcDetail = (row: any, index: number) => {
  const amount = (row.quantity || 0) * (row.unitPrice || 0)
  const discount = amount * (row.discountRate || 0) / 100
  row.amount = amount - discount
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    await new Promise(resolve => setTimeout(resolve, 500))
    
    tableData.value = [
      {
        id: '1',
        quoteNo: 'QT-20260119001',
        customerId: '1',
        customerName: '客户A',
        contactId: '1',
        contactName: '张三',
        quoteDate: '2026-01-19',
        validUntil: '2026-02-19',
        totalQuantity: 10,
        totalAmount: 11300,
        status: 1,
        createTime: '2026-01-19 10:00:00'
      },
      {
        id: '2',
        quoteNo: 'QT-20260119002',
        customerId: '2',
        customerName: '客户B',
        contactId: '2',
        contactName: '李四',
        quoteDate: '2026-01-18',
        validUntil: '2026-01-15',
        totalQuantity: 5,
        totalAmount: 5650,
        status: 4,
        createTime: '2026-01-18 14:30:00'
      }
    ]
    
    pagination.total = 2
    
    stats.totalCount = 2
    stats.pendingCount = 1
    stats.convertedCount = 0
    stats.totalAmount = 16950
  } catch (error) {
    MessagePlugin.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  loadData()
}

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    quoteNo: '',
    customerId: '',
    status: '',
    validityDate: []
  })
  handleSearch()
}

// 新增
const handleAdd = () => {
  isEdit.value = false
  activeTab.value = 'basic'
  Object.assign(editForm, {
    id: '',
    quoteNo: `QT-${Date.now()}`,
    customerId: '',
    contactId: '',
    quoteDate: new Date().toISOString().split('T')[0],
    validUntil: '',
    currency: 'CNY',
    taxRate: 13,
    paymentMethod: '1',
    deliveryPeriod: 7,
    remark: '',
    quoteRemark: '',
    deliveryTerms: '',
    qualityGuarantee: '',
    otherTerms: '',
    details: []
  })
  editDialogVisible.value = true
}

// 编辑
const handleEdit = (row: any) => {
  isEdit.value = true
  activeTab.value = 'basic'
  Object.assign(editForm, {
    ...row,
    details: row.details || []
  })
  editDialogVisible.value = true
}

// 查看
const handleView = (row: any) => {
  detailTab.value = 'info'
  currentDetail.value = {
    ...row,
    details: [
      {
        productNo: 'P001',
        productName: '商品A',
        spec: '默认规格',
        unit: '件',
        quantity: 5,
        unitPrice: 1500,
        discountRate: 0,
        amount: 7500
      },
      {
        productNo: 'P002',
        productName: '商品B',
        spec: '默认规格',
        unit: '件',
        quantity: 5,
        unitPrice: 1500,
        discountRate: 0,
        amount: 7500
      }
    ],
    subtotal: 15000,
    discountAmount: 0,
    taxAmount: 1950,
    logs: [
      { id: '1', time: '2026-01-19 10:00:00', action: '创建报价单', operator: '张三', remark: '' },
      { id: '2', time: '2026-01-19 14:00:00', action: '发送报价单', operator: '张三', remark: '' }
    ]
  }
  detailDialogVisible.value = true
}

// 发送
const handleSend = async (row: any) => {
  try {
    await MessagePlugin.confirm('确认发送该报价单给客户?')
    MessagePlugin.success('发送成功')
    loadData()
  } catch {
    // 用户取消
  }
}

// 转订单
const handleToOrder = async (row: any) => {
  try {
    await MessagePlugin.confirm('确认将该报价单转为销售订单?')
    MessagePlugin.success('转订单成功')
    loadData()
  } catch {
    // 用户取消
  }
}

// 复制
const handleCopy = (row: any) => {
  isEdit.value = false
  activeTab.value = 'basic'
  Object.assign(editForm, {
    ...row,
    id: '',
    quoteNo: `QT-${Date.now()}`,
    details: row.details || []
  })
  editDialogVisible.value = true
  MessagePlugin.success('已复制报价单')
}

// 删除
const handleDelete = async (row: any) => {
  try {
    await MessagePlugin.confirm('确认删除该报价单?')
    MessagePlugin.success('删除成功')
    loadData()
  } catch {
    // 用户取消
  }
}

// 批量发送
const handleBatchSend = async () => {
  if (selectedRowKeys.value.length === 0) {
    MessagePlugin.warning('请选择要发送的报价单')
    return
  }
  try {
    await MessagePlugin.confirm(`确认批量发送 ${selectedRowKeys.value.length} 个报价单?`)
    MessagePlugin.success('批量发送成功')
    loadData()
  } catch {
    // 用户取消
  }
}

// 导出
const handleExport = () => {
  MessagePlugin.success('导出成功')
}

// 保存
const handleSave = () => {
  if (!editForm.customerId) {
    MessagePlugin.warning('请选择客户')
    return
  }
  if (!editForm.contactId) {
    MessagePlugin.warning('请选择联系人')
    return
  }
  if (!editForm.validUntil) {
    MessagePlugin.warning('请选择有效期')
    return
  }
  
  MessagePlugin.success('保存成功')
  editDialogVisible.value = false
  loadData()
}

// 保存并发送
const handleSaveAndSend = () => {
  handleSave()
}

// 添加明细
const handleAddDetail = () => {
  editForm.details.push({
    productNo: '',
    productName: '',
    spec: '',
    unit: '',
    quantity: 1,
    unitPrice: 0,
    discountRate: 0,
    amount: 0
  })
}

// 移除明细
const handleRemoveDetail = (index: number) => {
  editForm.details.splice(index, 1)
}

// 分页变化
const handlePageChange = (pageInfo: any) => {
  pagination.current = pageInfo.current
  pagination.pageSize = pageInfo.pageSize
  loadData()
}

// 选择变化
const handleSelectChange = (value: any) => {
  selectedRowKeys.value = value
}

onMounted(() => {
  loadData()
})
</script>

<style scoped lang="less">
.sales-quote-container {
  padding: 16px;

  .stats-card {
    margin-bottom: 16px;

    .stat-item {
      display: flex;
      align-items: center;
      gap: 16px;

      .stat-icon {
        width: 56px;
        height: 56px;
        border-radius: 8px;
        background: var(--td-brand-color-1);
        display: flex;
        align-items: center;
        justify-content: center;
        color: var(--td-brand-color);

        &.stat-icon-warning {
          background: var(--td-warning-color-1);
          color: var(--td-warning-color);
        }

        &.stat-icon-success {
          background: var(--td-success-color-1);
          color: var(--td-success-color);
        }

        &.stat-icon-info {
          background: var(--td-brand-color-1);
          color: var(--td-brand-color);
        }
      }

      .stat-content {
        flex: 1;

        .stat-value {
          font-size: 24px;
          font-weight: 600;
          color: var(--td-text-color-primary);
          line-height: 1.2;
        }

        .stat-label {
          font-size: 14px;
          color: var(--td-text-color-secondary);
          margin-top: 4px;
        }
      }
    }
  }

  .search-card {
    margin-bottom: 16px;
  }

  .action-bar {
    margin-bottom: 16px;
  }

  .table-card {
    .amount-text {
      font-weight: 600;
    }

    .text-danger {
      color: var(--td-error-color);
    }
  }

  .detail-actions {
    margin-bottom: 16px;
  }

  .detail-summary {
    margin-top: 16px;
    padding: 16px;
    background: var(--td-bg-color-container-hover);
    border-radius: 4px;

    .summary-item {
      display: flex;
      justify-content: flex-end;
      align-items: center;
      margin-bottom: 8px;

      .summary-label {
        font-size: 14px;
        color: var(--td-text-color-secondary);
        margin-right: 16px;
      }

      .summary-value {
        font-size: 16px;
        font-weight: 600;
        color: var(--td-text-color-primary);
        min-width: 120px;
        text-align: right;

        &.text-danger {
          color: var(--td-error-color);
        }
      }

      &.total {
        margin-top: 16px;
        padding-top: 16px;
        border-top: 1px solid var(--td-component-border);

        .summary-value {
          font-size: 18px;
          color: var(--td-brand-color);
        }
      }
    }
  }

  .dialog-footer {
    margin-top: 24px;
    text-align: right;
  }

  .log-remark {
    font-size: 12px;
    color: var(--td-text-color-secondary);
    margin-top: 4px;
  }
}
</style>
