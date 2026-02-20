<template>
  <div class="sales-receipt-container">
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
              <div class="stat-label">收款单数</div>
            </div>
          </div>
        </t-col>
        <t-col :span="6">
          <div class="stat-item">
            <div class="stat-icon stat-icon-warning">
              <t-icon name="time" size="24px" />
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stats.pendingAmount }}</div>
              <div class="stat-label">待收金额</div>
            </div>
          </div>
        </t-col>
        <t-col :span="6">
          <div class="stat-item">
            <div class="stat-icon stat-icon-success">
              <t-icon name="check-circle" size="24px" />
            </div>
            <div class="stat-content">
              <div class="stat-value">¥{{ formatMoney(stats.receivedAmount) }}</div>
              <div class="stat-label">已收金额</div>
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
              <div class="stat-label">应收总额</div>
            </div>
          </div>
        </t-col>
      </t-row>
    </t-card>

    <!-- 搜索卡片 -->
    <t-card class="search-card">
      <t-form :data="searchForm" layout="inline" @submit="handleSearch" @reset="handleReset">
        <t-form-item label="收款单号" name="receiptNo">
          <t-input v-model="searchForm.receiptNo" placeholder="请输入收款单号" clearable />
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
        <t-form-item label="收款状态" name="status">
          <t-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <t-option value="0" label="待收款" />
            <t-option value="1" label="部分收款" />
            <t-option value="2" label="已收款" />
            <t-option value="3" label="已退款" />
          </t-select>
        </t-form-item>
        <t-form-item label="收款日期" name="receiptDateRange">
          <t-date-range-picker v-model="searchForm.receiptDateRange" clearable />
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
          新增收款
        </t-button>
        <t-button theme="default" @click="handleBatchConfirm">
          <template #icon><t-icon name="check" /></template>
          批量确认
        </t-button>
        <t-button theme="default" @click="handleExport">
          <template #icon><t-icon name="download" /></template>
          导出报表
        </t-button>
      </t-space>
    </div>

    <!-- 收款列表表格 -->
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
          <t-tag v-if="row.status === 0" theme="warning">待收款</t-tag>
          <t-tag v-else-if="row.status === 1" theme="primary">部分收款</t-tag>
          <t-tag v-else-if="row.status === 2" theme="success">已收款</t-tag>
          <t-tag v-else-if="row.status === 3" theme="danger">已退款</t-tag>
        </template>

        <template #orderAmount="{ row }">
          <span class="amount-text">¥{{ formatMoney(row.orderAmount) }}</span>
        </template>

        <template #receivedAmount="{ row }">
          <span class="amount-text success">¥{{ formatMoney(row.receivedAmount) }}</span>
        </template>

        <template #unreceivedAmount="{ row }">
          <span class="amount-text danger">¥{{ formatMoney(row.unreceivedAmount) }}</span>
        </template>

        <template #action="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleView(row)">查看</t-link>
            <t-link v-if="row.status !== 3" theme="primary" @click="handleReceipt(row)">收款</t-link>
            <t-link v-if="row.status !== 3" theme="primary" @click="handleRefund(row)">退款</t-link>
            <t-link v-if="row.status !== 3" theme="primary" @click="handleEdit(row)">编辑</t-link>
            <t-link v-if="row.status === 0" theme="danger" @click="handleDelete(row)">删除</t-link>
          </t-space>
        </template>
      </t-table>
    </t-card>

    <!-- 收款单编辑弹窗 -->
    <t-dialog
      v-model:visible="editDialogVisible"
      :header="isEdit ? '编辑收款单' : '新增收款单'"
      :footer="false"
      width="800px"
    >
      <t-tabs v-model="activeTab">
        <t-tab-panel value="basic" label="基本信息">
          <t-form :data="editForm" :label-width="100">
            <t-form-item label="收款单号" name="receiptNo">
              <t-input v-model="editForm.receiptNo" placeholder="自动生成" disabled />
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
            <t-form-item label="收款金额" name="receiptAmount" required>
              <t-input-number v-model="editForm.receiptAmount" :min="0" :decimal-places="2" style="width: 100%" />
            </t-form-item>
            <t-form-item label="收款方式" name="receiptMethod" required>
              <t-select v-model="editForm.receiptMethod" placeholder="请选择收款方式">
                <t-option value="1" label="银行转账" />
                <t-option value="2" label="现金" />
                <t-option value="3" label="支票" />
                <t-option value="4" label="其他" />
              </t-select>
            </t-form-item>
            <t-form-item label="收款日期" name="receiptDate" required>
              <t-date-picker v-model="editForm.receiptDate" placeholder="请选择收款日期" />
            </t-form-item>
            <t-form-item label="收款账户" name="accountId" required>
              <t-select v-model="editForm.accountId" placeholder="请选择收款账户">
                <t-option value="1" label="工商银行 1234" />
                <t-option value="2" label="建设银行 5678" />
              </t-select>
            </t-form-item>
            <t-form-item label="收款说明" name="remark" required>
              <t-textarea v-model="editForm.remark" placeholder="请输入收款说明" :maxlength="200" />
            </t-form-item>
          </t-form>
        </t-tab-panel>

        <t-tab-panel value="summary" label="汇总信息">
          <t-descriptions :column="2" bordered>
            <t-descriptions-item label="收款单号">{{ editForm.receiptNo || '-' }}</t-descriptions-item>
            <t-descriptions-item label="客户">{{ getCustomerName(editForm.customerId) }}</t-descriptions-item>
            <t-descriptions-item label="关联订单">{{ editForm.orderNo || '-' }}</t-descriptions-item>
            <t-descriptions-item label="订单金额">¥{{ formatMoney(editForm.orderAmount) }}</t-descriptions-item>
            <t-descriptions-item label="已收金额">¥{{ formatMoney(editForm.receivedAmount) }}</t-descriptions-item>
            <t-descriptions-item label="本次收款">¥{{ formatMoney(editForm.receiptAmount) }}</t-descriptions-item>
            <t-descriptions-item label="收款方式">{{ getReceiptMethodName(editForm.receiptMethod) }}</t-descriptions-item>
            <t-descriptions-item label="收款日期">{{ editForm.receiptDate }}</t-descriptions-item>
            <t-descriptions-item label="收款账户">{{ getAccountName(editForm.accountId) }}</t-descriptions-item>
            <t-descriptions-item label="收款说明" :span="2">{{ editForm.remark }}</t-descriptions-item>
          </t-descriptions>
        </t-tab-panel>
      </t-tabs>

      <div class="dialog-footer">
        <t-space>
          <t-button theme="default" @click="editDialogVisible = false">取消</t-button>
          <t-button theme="primary" @click="handleSave">保存</t-button>
          <t-button v-if="!isEdit" theme="success" @click="handleSaveAndConfirm">保存并确认</t-button>
        </t-space>
      </div>
    </t-dialog>

    <!-- 收款单详情弹窗 -->
    <t-dialog v-model:visible="detailDialogVisible" header="收款单详情" :footer="false" width="900px">
      <t-tabs v-model="detailTab">
        <t-tab-panel value="info" label="基本信息">
          <t-descriptions :column="2" bordered>
            <t-descriptions-item label="收款单号">{{ currentDetail.receiptNo }}</t-descriptions-item>
            <t-descriptions-item label="状态">
              <t-tag v-if="currentDetail.status === 0" theme="warning">待收款</t-tag>
              <t-tag v-else-if="currentDetail.status === 1" theme="primary">部分收款</t-tag>
              <t-tag v-else-if="currentDetail.status === 2" theme="success">已收款</t-tag>
              <t-tag v-else-if="currentDetail.status === 3" theme="danger">已退款</t-tag>
            </t-descriptions-item>
            <t-descriptions-item label="客户">{{ currentDetail.customerName }}</t-descriptions-item>
            <t-descriptions-item label="关联订单">{{ currentDetail.orderNo }}</t-descriptions-item>
            <t-descriptions-item label="订单金额">¥{{ formatMoney(currentDetail.orderAmount) }}</t-descriptions-item>
            <t-descriptions-item label="已收金额">¥{{ formatMoney(currentDetail.receivedAmount) }}</t-descriptions-item>
            <t-descriptions-item label="未收金额">¥{{ formatMoney(currentDetail.unreceivedAmount) }}</t-descriptions-item>
            <t-descriptions-item label="收款比例">{{ currentDetail.receiptPercent }}%</t-descriptions-item>
            <t-descriptions-item label="创建时间">{{ currentDetail.createTime }}</t-descriptions-item>
            <t-descriptions-item label="创建人">{{ currentDetail.creator }}</t-descriptions-item>
          </t-descriptions>
        </t-tab-panel>

        <t-tab-panel value="receipts" label="收款记录">
          <t-table :data="currentDetail.receipts" :columns="receiptColumns" :bordered="true" size="small">
            <template #receiptAmount="{ row }">
              <span class="amount-text success">¥{{ formatMoney(row.receiptAmount) }}</span>
            </template>
          </t-table>
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

    <!-- 收款弹窗 -->
    <t-dialog v-model:visible="receiptDialogVisible" header="收款确认" :footer="false" width="600px">
      <t-form :data="receiptForm" :label-width="100">
        <t-form-item label="收款单号">
          <t-input :value="currentDetail.receiptNo" disabled />
        </t-form-item>
        <t-form-item label="客户">
          <t-input :value="currentDetail.customerName" disabled />
        </t-form-item>
        <t-form-item label="订单金额">
          <span class="amount-text">¥{{ formatMoney(currentDetail.orderAmount) }}</span>
        </t-form-item>
        <t-form-item label="已收金额">
          <span class="amount-text success">¥{{ formatMoney(currentDetail.receivedAmount) }}</span>
        </t-form-item>
        <t-form-item label="未收金额">
          <span class="amount-text danger">¥{{ formatMoney(currentDetail.unreceivedAmount) }}</span>
        </t-form-item>
        <t-form-item label="收款金额" name="receiptAmount" required>
          <t-input-number v-model="receiptForm.receiptAmount" :min="0" :max="currentDetail.unreceivedAmount" :decimal-places="2" style="width: 100%" />
        </t-form-item>
        <t-form-item label="收款方式" name="receiptMethod" required>
          <t-select v-model="receiptForm.receiptMethod" placeholder="请选择收款方式">
            <t-option value="1" label="银行转账" />
            <t-option value="2" label="现金" />
            <t-option value="3" label="支票" />
            <t-option value="4" label="其他" />
          </t-select>
        </t-form-item>
        <t-form-item label="收款说明" name="remark">
          <t-textarea v-model="receiptForm.remark" placeholder="请输入收款说明" :maxlength="200" />
        </t-form-item>
      </t-form>
      
      <div class="dialog-footer">
        <t-space>
          <t-button theme="default" @click="receiptDialogVisible = false">取消</t-button>
          <t-button theme="primary" @click="handleConfirmReceipt">确认收款</t-button>
        </t-space>
      </div>
    </t-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { MessagePlugin } from 'tdesign-vue-next'

// 统计数据
const stats = reactive({
  totalCount: 0,
  pendingAmount: 0,
  receivedAmount: 0,
  totalAmount: 0
})

// 搜索表单
const searchForm = reactive({
  receiptNo: '',
  customerId: '',
  orderNo: '',
  status: '',
  receiptDateRange: []
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
  { colKey: 'receiptNo', title: '收款单号', width: 150 },
  { colKey: 'customerName', title: '客户', width: 120 },
  { colKey: 'orderNo', title: '关联订单', width: 150 },
  { colKey: 'orderAmount', title: '订单金额', width: 120 },
  { colKey: 'receivedAmount', title: '已收金额', width: 120 },
  { colKey: 'unreceivedAmount', title: '未收金额', width: 120 },
  { colKey: 'receiptPercent', title: '收款比例', width: 100 },
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
  receiptNo: '',
  customerId: '',
  orderId: '',
  orderNo: '',
  orderAmount: 0,
  receivedAmount: 0,
  receiptAmount: 0,
  receiptMethod: '1',
  receiptDate: '',
  accountId: '',
  remark: ''
})

// 详情弹窗
const detailDialogVisible = ref(false)
const detailTab = ref('info')
const currentDetail = ref({})

const receiptColumns = [
  { colKey: 'receiptNo', title: '收款单号', width: 150 },
  { colKey: 'receiptDate', title: '收款日期', width: 120 },
  { colKey: 'receiptAmount', title: '收款金额', width: 120 },
  { colKey: 'receiptMethod', title: '收款方式', width: 100 },
  { colKey: 'creator', title: '经办人', width: 100 }
]

// 收款弹窗
const receiptDialogVisible = ref(false)
const receiptForm = reactive({
  receiptAmount: 0,
  receiptMethod: '1',
  remark: ''
})

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

// 获取收款方式名称
const getReceiptMethodName = (receiptMethod: string) => {
  const map: Record<string, string> = {
    '1': '银行转账',
    '2': '现金',
    '3': '支票',
    '4': '其他'
  }
  return map[receiptMethod] || '-'
}

// 获取账户名称
const getAccountName = (accountId: string) => {
  const map: Record<string, string> = {
    '1': '工商银行 1234',
    '2': '建设银行 5678'
  }
  return map[accountId] || '-'
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    await new Promise(resolve => setTimeout(resolve, 500))
    
    tableData.value = [
      {
        id: '1',
        receiptNo: 'RCP-20260119001',
        customerId: '1',
        customerName: '客户A',
        orderId: '1',
        orderNo: 'SO-20260119001',
        orderAmount: 150000,
        receivedAmount: 50000,
        unreceivedAmount: 100000,
        receiptPercent: 33.33,
        status: 1,
        createTime: '2026-01-19 10:00:00'
      },
      {
        id: '2',
        receiptNo: 'RCP-20260119002',
        customerId: '2',
        customerName: '客户B',
        orderId: '2',
        orderNo: 'SO-20260119002',
        orderAmount: 80000,
        receivedAmount: 80000,
        unreceivedAmount: 0,
        receiptPercent: 100,
        status: 2,
        createTime: '2026-01-18 14:30:00'
      }
    ]
    
    pagination.total = 2
    
    stats.totalCount = 2
    stats.pendingAmount = 100000
    stats.receivedAmount = 130000
    stats.totalAmount = 230000
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
    receiptNo: '',
    customerId: '',
    orderNo: '',
    status: '',
    receiptDateRange: []
  })
  handleSearch()
}

// 新增
const handleAdd = () => {
  isEdit.value = false
  activeTab.value = 'basic'
  Object.assign(editForm, {
    id: '',
    receiptNo: `RCP-${Date.now()}`,
    customerId: '',
    orderId: '',
    orderNo: '',
    orderAmount: 0,
    receivedAmount: 0,
    receiptAmount: 0,
    receiptMethod: '1',
    receiptDate: new Date().toISOString().split('T')[0],
    accountId: '',
    remark: ''
  })
  editDialogVisible.value = true
}

// 编辑
const handleEdit = (row: any) => {
  isEdit.value = true
  activeTab.value = 'basic'
  Object.assign(editForm, {
    ...row,
    receiptAmount: row.unreceivedAmount
  })
  editDialogVisible.value = true
}

// 查看
const handleView = (row: any) => {
  detailTab.value = 'info'
  currentDetail.value = {
    ...row,
    receipts: [
      {
        receiptNo: 'RCP-20260119001',
        receiptDate: '2026-01-19',
        receiptAmount: 50000,
        receiptMethod: '银行转账',
        creator: '张三'
      }
    ],
    logs: [
      { id: '1', time: '2026-01-19 10:00:00', action: '创建收款单', operator: '张三', remark: '' },
      { id: '2', time: '2026-01-19 14:00:00', action: '收款', operator: '张三', remark: '部分收款' }
    ]
  }
  detailDialogVisible.value = true
}

// 收款
const handleReceipt = (row: any) => {
  currentDetail.value = { ...row }
  Object.assign(receiptForm, {
    receiptAmount: row.unreceivedAmount,
    receiptMethod: '1',
    remark: ''
  })
  receiptDialogVisible.value = true
}

// 确认收款
const handleConfirmReceipt = () => {
  if (!receiptForm.receiptAmount || receiptForm.receiptAmount <= 0) {
    MessagePlugin.warning('请输入收款金额')
    return
  }
  
  MessagePlugin.success('收款成功')
  receiptDialogVisible.value = false
  loadData()
}

// 退款
const handleRefund = async (row: any) => {
  try {
    await MessagePlugin.confirm('确认对该收款单退款?')
    MessagePlugin.success('退款成功')
    loadData()
  } catch {
    // 用户取消
  }
}

// 删除
const handleDelete = async (row: any) => {
  try {
    await MessagePlugin.confirm('确认删除该收款单?')
    MessagePlugin.success('删除成功')
    loadData()
  } catch {
    // 用户取消
  }
}

// 批量确认
const handleBatchConfirm = async () => {
  if (selectedRowKeys.value.length === 0) {
    MessagePlugin.warning('请选择要确认的收款单')
    return
  }
  try {
    await MessagePlugin.confirm(`确认批量确认 ${selectedRowKeys.value.length} 个收款单?`)
    MessagePlugin.success('批量确认成功')
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
  if (!editForm.receiptAmount || editForm.receiptAmount <= 0) {
    MessagePlugin.warning('请输入收款金额')
    return
  }
  if (!editForm.remark) {
    MessagePlugin.warning('请输入收款说明')
    return
  }
  
  MessagePlugin.success('保存成功')
  editDialogVisible.value = false
  loadData()
}

// 保存并确认
const handleSaveAndConfirm = () => {
  handleSave()
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
.sales-receipt-container {
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

      &.success {
        color: var(--td-success-color);
      }

      &.danger {
        color: var(--td-error-color);
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
