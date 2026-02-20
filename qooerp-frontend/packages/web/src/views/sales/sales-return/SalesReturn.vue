<template>
  <div class="sales-return-container">
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
              <div class="stat-label">退货单数</div>
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
              <div class="stat-label">待处理</div>
            </div>
          </div>
        </t-col>
        <t-col :span="6">
          <div class="stat-item">
            <div class="stat-icon stat-icon-success">
              <t-icon name="check-circle" size="24px" />
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stats.completedCount }}</div>
              <div class="stat-label">已完成</div>
            </div>
          </div>
        </t-col>
        <t-col :span="6">
          <div class="stat-item">
            <div class="stat-icon stat-icon-danger">
              <t-icon name="money-circle" size="24px" />
            </div>
            <div class="stat-content">
              <div class="stat-value">¥{{ formatMoney(stats.totalAmount) }}</div>
              <div class="stat-label">退款金额</div>
            </div>
          </div>
        </t-col>
      </t-row>
    </t-card>

    <!-- 搜索卡片 -->
    <t-card class="search-card">
      <t-form :data="searchForm" layout="inline" @submit="handleSearch" @reset="handleReset">
        <t-form-item label="退货单号" name="returnNo">
          <t-input v-model="searchForm.returnNo" placeholder="请输入退货单号" clearable />
        </t-form-item>
        <t-form-item label="客户" name="customerId">
          <t-select v-model="searchForm.customerId" placeholder="请选择客户" clearable>
            <t-option value="1" label="客户A" />
            <t-option value="2" label="客户B" />
          </t-select>
        </t-form-item>
        <t-form-item label="关联订单" name="orderNo">
          <t-input v-model="searchForm.orderNo" placeholder="请输入订单号" clearable />
        </t-form-item>
        <t-form-item label="退货状态" name="status">
          <t-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <t-option value="0" label="待审核" />
            <t-option value="1" label="已审核" />
            <t-option value="2" label="已退款" />
            <t-option value="3" label="已完成" />
            <t-option value="4" label="已驳回" />
          </t-select>
        </t-form-item>
        <t-form-item label="退货日期" name="returnDateRange">
          <t-date-range-picker v-model="searchForm.returnDateRange" clearable />
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
          新增退货
        </t-button>
        <t-button theme="default" @click="handleBatchApprove">
          <template #icon><t-icon name="check" /></template>
          批量审核
        </t-button>
        <t-button theme="default" @click="handleExport">
          <template #icon><t-icon name="download" /></template>
          导出报表
        </t-button>
      </t-space>
    </div>

    <!-- 退货列表表格 -->
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
          <t-tag v-if="row.status === 0" theme="warning">待审核</t-tag>
          <t-tag v-else-if="row.status === 1" theme="primary">已审核</t-tag>
          <t-tag v-else-if="row.status === 2" theme="success">已退款</t-tag>
          <t-tag v-else-if="row.status === 3" theme="success">已完成</t-tag>
          <t-tag v-else-if="row.status === 4" theme="danger">已驳回</t-tag>
        </template>

        <template #returnAmount="{ row }">
          <span class="amount-text">¥{{ formatMoney(row.returnAmount) }}</span>
        </template>

        <template #action="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleView(row)">查看</t-link>
            <t-link v-if="row.status === 0" theme="primary" @click="handleApprove(row)">审核</t-link>
            <t-link v-if="row.status === 0" theme="danger" @click="handleReject(row)">驳回</t-link>
            <t-link v-if="row.status === 1" theme="primary" @click="handleRefund(row)">退款</t-link>
            <t-link v-if="row.status === 0" theme="primary" @click="handleEdit(row)">编辑</t-link>
            <t-link v-if="row.status === 0" theme="danger" @click="handleDelete(row)">删除</t-link>
          </t-space>
        </template>
      </t-table>
    </t-card>

    <!-- 退货单编辑弹窗 -->
    <t-dialog
      v-model:visible="editDialogVisible"
      :header="isEdit ? '编辑退货单' : '新增退货单'"
      :footer="false"
      width="800px"
    >
      <t-tabs v-model="activeTab">
        <t-tab-panel value="basic" label="基本信息">
          <t-form :data="editForm" :label-width="100">
            <t-form-item label="退货单号" name="returnNo">
              <t-input v-model="editForm.returnNo" placeholder="自动生成" disabled />
            </t-form-item>
            <t-form-item label="客户" name="customerId" required>
              <t-select v-model="editForm.customerId" placeholder="请选择客户">
                <t-option value="1" label="客户A" />
                <t-option value="2" label="客户B" />
              </t-select>
            </t-form-item>
            <t-form-item label="关联订单" name="orderId" required>
              <t-select v-model="editForm.orderId" placeholder="请选择销售订单">
                <t-option value="1" label="SO-20260119001" />
                <t-option value="2" label="SO-20260119002" />
              </t-select>
            </t-form-item>
            <t-form-item label="退货类型" name="returnType" required>
              <t-radio-group v-model="editForm.returnType">
                <t-radio value="1">质量退货</t-radio>
                <t-radio value="2">数量不符</t-radio>
                <t-radio value="3">发错货</t-radio>
                <t-radio value="4">其他</t-radio>
              </t-radio-group>
            </t-form-item>
            <t-form-item label="退货日期" name="returnDate" required>
              <t-date-picker v-model="editForm.returnDate" placeholder="请选择退货日期" />
            </t-form-item>
            <t-form-item label="退货原因" name="returnReason" required>
              <t-textarea v-model="editForm.returnReason" placeholder="请输入退货原因" :maxlength="200" />
            </t-form-item>
            <t-form-item label="退款方式" name="refundMethod" required>
              <t-select v-model="editForm.refundMethod" placeholder="请选择退款方式">
                <t-option value="1" label="原路退回" />
                <t-option value="2" label="银行转账" />
                <t-option value="3" label="现金退款" />
              </t-select>
            </t-form-item>
            <t-form-item label="备注" name="remark">
              <t-textarea v-model="editForm.remark" placeholder="请输入备注" :maxlength="200" />
            </t-form-item>
          </t-form>
        </t-tab-panel>

        <t-tab-panel value="detail" label="退货明细">
          <div class="detail-actions">
            <t-button size="small" theme="primary" @click="handleAddDetail">
              <template #icon><t-icon name="add" /></template>
              添加商品
            </t-button>
          </div>
          <t-table :data="editForm.details" :columns="detailColumns" :bordered="true" size="small">
            <template #quantity="{ row, rowIndex }">
              <t-input-number v-model="row.quantity" :min="1" :max="row.maxQuantity" size="small" />
            </template>
            <template #action="{ row, rowIndex }">
              <t-link theme="danger" @click="handleRemoveDetail(rowIndex)">删除</t-link>
            </template>
          </t-table>
        </t-tab-panel>

        <t-tab-panel value="summary" label="汇总信息">
          <t-descriptions :column="2" bordered>
            <t-descriptions-item label="退货单号">{{ editForm.returnNo || '-' }}</t-descriptions-item>
            <t-descriptions-item label="退货日期">{{ editForm.returnDate || '-' }}</t-descriptions-item>
            <t-descriptions-item label="客户">{{ getCustomerName(editForm.customerId) }}</t-descriptions-item>
            <t-descriptions-item label="关联订单">{{ editForm.orderNo || '-' }}</t-descriptions-item>
            <t-descriptions-item label="退货类型">{{ getReturnTypeName(editForm.returnType) }}</t-descriptions-item>
            <t-descriptions-item label="退货数量">{{ getTotalQuantity() }}</t-descriptions-item>
            <t-descriptions-item label="退货金额">¥{{ formatMoney(getTotalAmount()) }}</t-descriptions-item>
            <t-descriptions-item label="退款方式">{{ getRefundMethodName(editForm.refundMethod) }}</t-descriptions-item>
            <t-descriptions-item label="退货原因" :span="2">{{ editForm.returnReason || '-' }}</t-descriptions-item>
          </t-descriptions>
        </t-tab-panel>
      </t-tabs>

      <div class="dialog-footer">
        <t-space>
          <t-button theme="default" @click="editDialogVisible = false">取消</t-button>
          <t-button theme="primary" @click="handleSave">保存</t-button>
          <t-button v-if="!isEdit" theme="success" @click="handleSaveAndSubmit">保存并提交</t-button>
        </t-space>
      </div>
    </t-dialog>

    <!-- 退货单详情弹窗 -->
    <t-dialog v-model:visible="detailDialogVisible" header="退货单详情" :footer="false" width="900px">
      <t-tabs v-model="detailTab">
        <t-tab-panel value="info" label="基本信息">
          <t-descriptions :column="2" bordered>
            <t-descriptions-item label="退货单号">{{ currentDetail.returnNo }}</t-descriptions-item>
            <t-descriptions-item label="状态">
              <t-tag v-if="currentDetail.status === 0" theme="warning">待审核</t-tag>
              <t-tag v-else-if="currentDetail.status === 1" theme="primary">已审核</t-tag>
              <t-tag v-else-if="currentDetail.status === 2" theme="success">已退款</t-tag>
              <t-tag v-else-if="currentDetail.status === 3" theme="success">已完成</t-tag>
              <t-tag v-else-if="currentDetail.status === 4" theme="danger">已驳回</t-tag>
            </t-descriptions-item>
            <t-descriptions-item label="客户">{{ currentDetail.customerName }}</t-descriptions-item>
            <t-descriptions-item label="关联订单">{{ currentDetail.orderNo }}</t-descriptions-item>
            <t-descriptions-item label="退货类型">{{ currentDetail.returnTypeName }}</t-descriptions-item>
            <t-descriptions-item label="退货日期">{{ currentDetail.returnDate }}</t-descriptions-item>
            <t-descriptions-item label="退货数量">{{ currentDetail.totalQuantity }}</t-descriptions-item>
            <t-descriptions-item label="退货金额">¥{{ formatMoney(currentDetail.returnAmount) }}</t-descriptions-item>
            <t-descriptions-item label="退款方式">{{ currentDetail.refundMethodName }}</t-descriptions-item>
            <t-descriptions-item label="退货原因" :span="2">{{ currentDetail.returnReason }}</t-descriptions-item>
            <t-descriptions-item label="创建时间">{{ currentDetail.createTime }}</t-descriptions-item>
            <t-descriptions-item label="创建人">{{ currentDetail.creator }}</t-descriptions-item>
          </t-descriptions>
        </t-tab-panel>

        <t-tab-panel value="details" label="退货明细">
          <t-table :data="currentDetail.details" :columns="viewDetailColumns" :bordered="true" size="small" />
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
  completedCount: 0,
  totalAmount: 0
})

// 搜索表单
const searchForm = reactive({
  returnNo: '',
  customerId: '',
  orderNo: '',
  status: '',
  returnDateRange: []
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
  { colKey: 'returnNo', title: '退货单号', width: 150 },
  { colKey: 'customerName', title: '客户', width: 120 },
  { colKey: 'orderNo', title: '关联订单', width: 150 },
  { colKey: 'returnTypeName', title: '退货类型', width: 100 },
  { colKey: 'returnDate', title: '退货日期', width: 120 },
  { colKey: 'totalQuantity', title: '退货数量', width: 100 },
  { colKey: 'returnAmount', title: '退货金额', width: 120 },
  { colKey: 'status', title: '状态', width: 100 },
  { colKey: 'createTime', title: '创建时间', width: 150 },
  { colKey: 'action', title: '操作', width: 260, fixed: 'right' }
]

// 编辑弹窗
const editDialogVisible = ref(false)
const isEdit = ref(false)
const activeTab = ref('basic')
const editForm = reactive({
  id: '',
  returnNo: '',
  customerId: '',
  orderId: '',
  orderNo: '',
  returnType: '1',
  returnDate: '',
  returnReason: '',
  refundMethod: '1',
  remark: '',
  details: []
})

// 退货明细列
const detailColumns = [
  { colKey: 'productNo', title: '商品编号', width: 120 },
  { colKey: 'productName', title: '商品名称', width: 150 },
  { colKey: 'spec', title: '规格', width: 100 },
  { colKey: 'unit', title: '单位', width: 80 },
  { colKey: 'maxQuantity', title: '可退数量', width: 100 },
  { colKey: 'quantity', title: '退货数量', width: 100 },
  { colKey: 'unitPrice', title: '单价', width: 100 },
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
  { colKey: 'quantity', title: '退货数量', width: 100 },
  { colKey: 'unitPrice', title: '单价', width: 100 },
  { colKey: 'amount', title: '金额', width: 100 }
]

// 格式化金额
const formatMoney = (value: number) => {
  return (value || 0).toFixed(2)
}

// 获取客户名称
const getCustomerName = (customerId: string) => {
  const map: Record<string, string> = {
    '1': '客户A',
    '2': '客户B'
  }
  return map[customerId] || '-'
}

// 获取退货类型名称
const getReturnTypeName = (returnType: string) => {
  const map: Record<string, string> = {
    '1': '质量退货',
    '2': '数量不符',
    '3': '发错货',
    '4': '其他'
  }
  return map[returnType] || '-'
}

// 获取退款方式名称
const getRefundMethodName = (refundMethod: string) => {
  const map: Record<string, string> = {
    '1': '原路退回',
    '2': '银行转账',
    '3': '现金退款'
  }
  return map[refundMethod] || '-'
}

// 获取总数量
const getTotalQuantity = () => {
  return editForm.details.reduce((sum: number, item: any) => sum + (item.quantity || 0), 0)
}

// 获取总金额
const getTotalAmount = () => {
  return editForm.details.reduce((sum: number, item: any) => sum + ((item.quantity || 0) * (item.unitPrice || 0)), 0)
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    await new Promise(resolve => setTimeout(resolve, 500))
    
    tableData.value = [
      {
        id: '1',
        returnNo: 'SRT-20260119001',
        customerId: '1',
        customerName: '客户A',
        orderId: '1',
        orderNo: 'SO-20260119001',
        returnType: '1',
        returnTypeName: '质量退货',
        returnDate: '2026-01-19',
        totalQuantity: 5,
        returnAmount: 7500,
        status: 0,
        createTime: '2026-01-19 10:00:00'
      },
      {
        id: '2',
        returnNo: 'SRT-20260119002',
        customerId: '2',
        customerName: '客户B',
        orderId: '2',
        orderNo: 'SO-20260119002',
        returnType: '2',
        returnTypeName: '数量不符',
        returnDate: '2026-01-18',
        totalQuantity: 3,
        returnAmount: 4800,
        status: 3,
        createTime: '2026-01-18 14:30:00'
      }
    ]
    
    pagination.total = 2
    
    stats.totalCount = 2
    stats.pendingCount = 1
    stats.completedCount = 1
    stats.totalAmount = 12300
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
    returnNo: '',
    customerId: '',
    orderNo: '',
    status: '',
    returnDateRange: []
  })
  handleSearch()
}

// 新增
const handleAdd = () => {
  isEdit.value = false
  activeTab.value = 'basic'
  Object.assign(editForm, {
    id: '',
    returnNo: `SRT-${Date.now()}`,
    customerId: '',
    orderId: '',
    orderNo: '',
    returnType: '1',
    returnDate: new Date().toISOString().split('T')[0],
    returnReason: '',
    refundMethod: '1',
    remark: '',
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
        quantity: 3,
        unitPrice: 1500,
        amount: 4500
      },
      {
        productNo: 'P002',
        productName: '商品B',
        spec: '默认规格',
        unit: '件',
        quantity: 2,
        unitPrice: 1500,
        amount: 3000
      }
    ],
    logs: [
      { id: '1', time: '2026-01-19 10:00:00', action: '创建退货单', operator: '张三', remark: '' },
      { id: '2', time: '2026-01-19 14:00:00', action: '提交审核', operator: '张三', remark: '' }
    ]
  }
  detailDialogVisible.value = true
}

// 审核
const handleApprove = async (row: any) => {
  try {
    await MessagePlugin.confirm('确认审核通过该退货单?')
    MessagePlugin.success('审核成功')
    loadData()
  } catch {
    // 用户取消
  }
}

// 驳回
const handleReject = async (row: any) => {
  try {
    await MessagePlugin.confirm('确认驳回该退货单?')
    MessagePlugin.success('驳回成功')
    loadData()
  } catch {
    // 用户取消
  }
}

// 退款
const handleRefund = async (row: any) => {
  try {
    await MessagePlugin.confirm('确认退款该退货单?')
    MessagePlugin.success('退款成功')
    loadData()
  } catch {
    // 用户取消
  }
}

// 删除
const handleDelete = async (row: any) => {
  try {
    await MessagePlugin.confirm('确认删除该退货单?')
    MessagePlugin.success('删除成功')
    loadData()
  } catch {
    // 用户取消
  }
}

// 批量审核
const handleBatchApprove = async () => {
  if (selectedRowKeys.value.length === 0) {
    MessagePlugin.warning('请选择要审核的退货单')
    return
  }
  try {
    await MessagePlugin.confirm(`确认批量审核 ${selectedRowKeys.value.length} 个退货单?`)
    MessagePlugin.success('批量审核成功')
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
  if (!editForm.orderId) {
    MessagePlugin.warning('请选择关联订单')
    return
  }
  if (!editForm.returnReason) {
    MessagePlugin.warning('请输入退货原因')
    return
  }
  
  MessagePlugin.success('保存成功')
  editDialogVisible.value = false
  loadData()
}

// 保存并提交
const handleSaveAndSubmit = () => {
  handleSave()
}

// 添加明细
const handleAddDetail = () => {
  editForm.details.push({
    productNo: '',
    productName: '',
    spec: '',
    unit: '',
    maxQuantity: 0,
    quantity: 1,
    unitPrice: 0,
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
.sales-return-container {
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

        &.stat-icon-danger {
          background: var(--td-error-color-1);
          color: var(--td-error-color);
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
      color: var(--td-error-color);
      font-weight: 600;
    }
  }

  .detail-actions {
    margin-bottom: 16px;
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
