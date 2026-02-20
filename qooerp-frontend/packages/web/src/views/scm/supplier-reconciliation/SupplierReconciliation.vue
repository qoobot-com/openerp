<template>
  <div class="supplier-reconciliation-container">
    <!-- 统计卡片 -->
    <t-card class="stats-card">
      <t-row :gutter="16">
        <t-col :span="6">
          <div class="stat-item">
            <div class="stat-icon">
              <t-icon name="user" size="24px" />
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stats.supplierCount }}</div>
              <div class="stat-label">供应商数</div>
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
              <div class="stat-label">待对账</div>
            </div>
          </div>
        </t-col>
        <t-col :span="6">
          <div class="stat-item">
            <div class="stat-icon stat-icon-danger">
              <t-icon name="error-circle" size="24px" />
            </div>
            <div class="stat-content">
              <div class="stat-value">¥{{ formatMoney(stats.unpaidAmount) }}</div>
              <div class="stat-label">应付金额</div>
            </div>
          </div>
        </t-col>
        <t-col :span="6">
          <div class="stat-item">
            <div class="stat-icon stat-icon-success">
              <t-icon name="money-circle" size="24px" />
            </div>
            <div class="stat-content">
              <div class="stat-value">¥{{ formatMoney(stats.paidAmount) }}</div>
              <div class="stat-label">已付金额</div>
            </div>
          </div>
        </t-col>
      </t-row>
    </t-card>

    <!-- 搜索卡片 -->
    <t-card class="search-card">
      <t-form :data="searchForm" layout="inline" @submit="handleSearch" @reset="handleReset">
        <t-form-item label="供应商" name="supplierId">
          <t-select v-model="searchForm.supplierId" placeholder="请选择供应商" clearable>
            <t-option value="1" label="供应商A" />
            <t-option value="2" label="供应商B" />
          </t-select>
        </t-form-item>
        <t-form-item label="对账期间" name="period">
          <t-date-picker v-model="searchForm.period" mode="month" clearable />
        </t-form-item>
        <t-form-item label="对账状态" name="status">
          <t-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <t-option value="0" label="待对账" />
            <t-option value="1" label="对账中" />
            <t-option value="2" label="已确认" />
            <t-option value="3" label="有差异" />
          </t-select>
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
        <t-button theme="primary" @click="handleCreate">
          <template #icon><t-icon name="add" /></template>
          新增对账
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

    <!-- 对账列表表格 -->
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
          <t-tag v-if="row.status === 0" theme="warning">待对账</t-tag>
          <t-tag v-else-if="row.status === 1" theme="primary">对账中</t-tag>
          <t-tag v-else-if="row.status === 2" theme="success">已确认</t-tag>
          <t-tag v-else-if="row.status === 3" theme="danger">有差异</t-tag>
        </template>

        <template #orderAmount="{ row }">
          <span class="amount-text">¥{{ formatMoney(row.orderAmount) }}</span>
        </template>

        <template #paidAmount="{ row }">
          <span class="amount-text success">¥{{ formatMoney(row.paidAmount) }}</span>
        </template>

        <template #unpaidAmount="{ row }">
          <span class="amount-text danger">¥{{ formatMoney(row.unpaidAmount) }}</span>
        </template>

        <template #action="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleView(row)">查看</t-link>
            <t-link v-if="row.status === 0" theme="primary" @click="handleReconcile(row)">对账</t-link>
            <t-link v-if="row.status === 1" theme="primary" @click="handleConfirm(row)">确认</t-link>
            <t-link v-if="row.status === 3" theme="warning" @click="handleResolve(row)">处理差异</t-link>
            <t-link theme="primary" @click="handleExportBill(row)">导出对账单</t-link>
          </t-space>
        </template>
      </t-table>
    </t-card>

    <!-- 新增对账弹窗 -->
    <t-dialog
      v-model:visible="createDialogVisible"
      header="新增对账单"
      :footer="false"
      width="800px"
    >
      <t-form :data="createForm" :label-width="100">
        <t-form-item label="供应商" name="supplierId" required>
          <t-select v-model="createForm.supplierId" placeholder="请选择供应商">
            <t-option value="1" label="供应商A" />
            <t-option value="2" label="供应商B" />
          </t-select>
        </t-form-item>
        <t-form-item label="对账期间" name="period" required>
          <t-date-picker v-model="createForm.period" mode="month" />
        </t-form-item>
        <t-form-item label="对账说明" name="remark">
          <t-textarea v-model="createForm.remark" placeholder="请输入对账说明" :maxlength="200" />
        </t-form-item>
      </t-form>
      
      <div class="dialog-footer">
        <t-space>
          <t-button theme="default" @click="createDialogVisible = false">取消</t-button>
          <t-button theme="primary" @click="handleSaveCreate">生成对账单</t-button>
        </t-space>
      </div>
    </t-dialog>

    <!-- 对账详情弹窗 -->
    <t-dialog v-model:visible="detailDialogVisible" header="对账单详情" :footer="false" width="900px">
      <t-tabs v-model="detailTab">
        <t-tab-panel value="info" label="基本信息">
          <t-descriptions :column="2" bordered>
            <t-descriptions-item label="对账单号">{{ currentDetail.reconcileNo }}</t-descriptions-item>
            <t-descriptions-item label="状态">
              <t-tag v-if="currentDetail.status === 0" theme="warning">待对账</t-tag>
              <t-tag v-else-if="currentDetail.status === 1" theme="primary">对账中</t-tag>
              <t-tag v-else-if="currentDetail.status === 2" theme="success">已确认</t-tag>
              <t-tag v-else-if="currentDetail.status === 3" theme="danger">有差异</t-tag>
            </t-descriptions-item>
            <t-descriptions-item label="供应商">{{ currentDetail.supplierName }}</t-descriptions-item>
            <t-descriptions-item label="对账期间">{{ currentDetail.period }}</t-descriptions-item>
            <t-descriptions-item label="订单金额">¥{{ formatMoney(currentDetail.orderAmount) }}</t-descriptions-item>
            <t-descriptions-item label="已付金额">¥{{ formatMoney(currentDetail.paidAmount) }}</t-descriptions-item>
            <t-descriptions-item label="应付金额">¥{{ formatMoney(currentDetail.unpaidAmount) }}</t-descriptions-item>
            <t-descriptions-item label="差异金额">
              <span v-if="currentDetail.diffAmount" :class="currentDetail.diffAmount > 0 ? 'diff-positive' : 'diff-negative'">
                ¥{{ formatMoney(currentDetail.diffAmount) }}
              </span>
              <span v-else>-</span>
            </t-descriptions-item>
            <t-descriptions-item label="创建时间">{{ currentDetail.createTime }}</t-descriptions-item>
            <t-descriptions-item label="创建人">{{ currentDetail.creator }}</t-descriptions-item>
            <t-descriptions-item label="对账时间">{{ currentDetail.reconcileTime || '-' }}</t-descriptions-item>
            <t-descriptions-item label="确认时间">{{ currentDetail.confirmTime || '-' }}</t-descriptions-item>
          </t-descriptions>
        </t-tab-panel>

        <t-tab-panel value="orders" label="订单明细">
          <t-table :data="currentDetail.orders" :columns="orderColumns" :bordered="true" size="small">
            <template #orderAmount="{ row }">
              <span class="amount-text">¥{{ formatMoney(row.orderAmount) }}</span>
            </template>
            <template #paidAmount="{ row }">
              <span class="amount-text success">¥{{ formatMoney(row.paidAmount) }}</span>
            </template>
          </t-table>
        </t-tab-panel>

        <t-tab-panel value="payments" label="付款明细">
          <t-table :data="currentDetail.payments" :columns="paymentColumns" :bordered="true" size="small">
            <template #paymentAmount="{ row }">
              <span class="amount-text success">¥{{ formatMoney(row.paymentAmount) }}</span>
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

    <!-- 处理差异弹窗 -->
    <t-dialog v-model:visible="resolveDialogVisible" header="处理差异" :footer="false" width="600px">
      <t-alert message="请确认差异原因并处理" type="warning" style="margin-bottom: 16px" />
      
      <t-form :data="resolveForm" :label-width="100">
        <t-form-item label="差异金额">
          <span class="diff-amount">¥{{ formatMoney(currentDetail.diffAmount || 0) }}</span>
        </t-form-item>
        <t-form-item label="差异原因" name="reason" required>
          <t-select v-model="resolveForm.reason" placeholder="请选择差异原因">
            <t-option value="1" label="订单未入账" />
            <t-option value="2" label="退货未冲减" />
            <t-option value="3" label="运费差异" />
            <t-option value="4" label="其他原因" />
          </t-select>
        </t-form-item>
        <t-form-item label="处理方式" name="method" required>
          <t-radio-group v-model="resolveForm.method">
            <t-radio value="1">调整账单</t-radio>
            <t-radio value="2">补充付款</t-radio>
            <t-radio value="3">等待供应商确认</t-radio>
          </t-radio-group>
        </t-form-item>
        <t-form-item label="处理说明" name="remark" required>
          <t-textarea v-model="resolveForm.remark" placeholder="请输入处理说明" :maxlength="200" />
        </t-form-item>
      </t-form>
      
      <div class="dialog-footer">
        <t-space>
          <t-button theme="default" @click="resolveDialogVisible = false">取消</t-button>
          <t-button theme="primary" @click="handleSaveResolve">提交处理</t-button>
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
  supplierCount: 0,
  pendingCount: 0,
  unpaidAmount: 0,
  paidAmount: 0
})

// 搜索表单
const searchForm = reactive({
  supplierId: '',
  period: '',
  status: ''
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
  { colKey: 'reconcileNo', title: '对账单号', width: 150 },
  { colKey: 'supplierName', title: '供应商', width: 120 },
  { colKey: 'period', title: '对账期间', width: 120 },
  { colKey: 'orderCount', title: '订单数', width: 80 },
  { colKey: 'orderAmount', title: '订单金额', width: 120 },
  { colKey: 'paidAmount', title: '已付金额', width: 120 },
  { colKey: 'unpaidAmount', title: '应付金额', width: 120 },
  { colKey: 'status', title: '状态', width: 100 },
  { colKey: 'createTime', title: '创建时间', width: 150 },
  { colKey: 'action', title: '操作', width: 260, fixed: 'right' }
]

// 新增对账弹窗
const createDialogVisible = ref(false)
const createForm = reactive({
  supplierId: '',
  period: '',
  remark: ''
})

// 详情弹窗
const detailDialogVisible = ref(false)
const detailTab = ref('info')
const currentDetail = ref({})

const orderColumns = [
  { colKey: 'orderNo', title: '订单编号', width: 150 },
  { colKey: 'orderDate', title: '订单日期', width: 120 },
  { colKey: 'totalQuantity', title: '采购数量', width: 100 },
  { colKey: 'orderAmount', title: '订单金额', width: 120 },
  { colKey: 'paidAmount', title: '已付金额', width: 120 },
  { colKey: 'status', title: '状态', width: 100 }
]

const paymentColumns = [
  { colKey: 'paymentNo', title: '付款单号', width: 150 },
  { colKey: 'paymentDate', title: '付款日期', width: 120 },
  { colKey: 'paymentAmount', title: '付款金额', width: 120 },
  { colKey: 'paymentMethod', title: '付款方式', width: 100 },
  { colKey: 'creator', title: '经办人', width: 100 }
]

// 处理差异弹窗
const resolveDialogVisible = ref(false)
const resolveForm = reactive({
  reason: '',
  method: '1',
  remark: ''
})

// 格式化金额
const formatMoney = (value: number) => {
  return (value || 0).toFixed(2)
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    await new Promise(resolve => setTimeout(resolve, 500))
    
    tableData.value = [
      {
        id: '1',
        reconcileNo: 'RCP-20260119001',
        supplierId: '1',
        supplierName: '供应商A',
        period: '2026-01',
        orderCount: 15,
        orderAmount: 750000,
        paidAmount: 400000,
        unpaidAmount: 350000,
        status: 1,
        diffAmount: null,
        createTime: '2026-01-19 10:00:00',
        reconcileTime: '2026-01-19 14:00:00',
        confirmTime: null
      },
      {
        id: '2',
        reconcileNo: 'RCP-20260119002',
        supplierId: '2',
        supplierName: '供应商B',
        period: '2026-01',
        orderCount: 10,
        orderAmount: 450000,
        paidAmount: 450000,
        unpaidAmount: 0,
        status: 2,
        diffAmount: null,
        createTime: '2026-01-18 10:00:00',
        reconcileTime: '2026-01-18 14:00:00',
        confirmTime: '2026-01-18 16:00:00'
      }
    ]
    
    pagination.total = 2
    
    stats.supplierCount = 2
    stats.pendingCount = 1
    stats.unpaidAmount = 350000
    stats.paidAmount = 850000
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
    supplierId: '',
    period: '',
    status: ''
  })
  handleSearch()
}

// 新增对账
const handleCreate = () => {
  Object.assign(createForm, {
    supplierId: '',
    period: '',
    remark: ''
  })
  createDialogVisible.value = true
}

// 保存新增
const handleSaveCreate = () => {
  if (!createForm.supplierId) {
    MessagePlugin.warning('请选择供应商')
    return
  }
  if (!createForm.period) {
    MessagePlugin.warning('请选择对账期间')
    return
  }
  
  MessagePlugin.success('对账单生成成功')
  createDialogVisible.value = false
  loadData()
}

// 查看
const handleView = (row: any) => {
  detailTab.value = 'info'
  currentDetail.value = {
    ...row,
    orders: [
      {
        orderNo: 'PO-20260119001',
        orderDate: '2026-01-19',
        totalQuantity: 100,
        orderAmount: 150000,
        paidAmount: 50000,
        status: '部分付款'
      },
      {
        orderNo: 'PO-20260119002',
        orderDate: '2026-01-18',
        totalQuantity: 50,
        orderAmount: 80000,
        paidAmount: 80000,
        status: '已付款'
      }
    ],
    payments: [
      {
        paymentNo: 'PAY-20260119001',
        paymentDate: '2026-01-19',
        paymentAmount: 50000,
        paymentMethod: '银行转账',
        creator: '张三'
      },
      {
        paymentNo: 'PAY-20260119002',
        paymentDate: '2026-01-18',
        paymentAmount: 80000,
        paymentMethod: '银行转账',
        creator: '李四'
      }
    ],
    logs: [
      { id: '1', time: '2026-01-19 10:00:00', action: '创建对账单', operator: '张三', remark: '' },
      { id: '2', time: '2026-01-19 14:00:00', action: '开始对账', operator: '张三', remark: '' }
    ]
  }
  detailDialogVisible.value = true
}

// 对账
const handleReconcile = (row: any) => {
  MessagePlugin.success('开始对账')
  loadData()
}

// 确认
const handleConfirm = async (row: any) => {
  try {
    await MessagePlugin.confirm('确认该对账单无误?')
    MessagePlugin.success('确认成功')
    loadData()
  } catch {
    // 用户取消
  }
}

// 处理差异
const handleResolve = (row: any) => {
  detailTab.value = 'info'
  currentDetail.value = { ...row }
  Object.assign(resolveForm, {
    reason: '',
    method: '1',
    remark: ''
  })
  resolveDialogVisible.value = true
}

// 保存处理差异
const handleSaveResolve = () => {
  if (!resolveForm.reason) {
    MessagePlugin.warning('请选择差异原因')
    return
  }
  if (!resolveForm.remark) {
    MessagePlugin.warning('请输入处理说明')
    return
  }
  
  MessagePlugin.success('差异处理成功')
  resolveDialogVisible.value = false
  loadData()
}

// 批量确认
const handleBatchConfirm = async () => {
  if (selectedRowKeys.value.length === 0) {
    MessagePlugin.warning('请选择要确认的对账单')
    return
  }
  try {
    await MessagePlugin.confirm(`确认批量确认 ${selectedRowKeys.value.length} 个对账单?`)
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

// 导出对账单
const handleExportBill = (row: any) => {
  MessagePlugin.success(`导出对账单 ${row.reconcileNo} 成功`)
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
.supplier-reconciliation-container {
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

        &.stat-icon-danger {
          background: var(--td-error-color-1);
          color: var(--td-error-color);
        }

        &.stat-icon-success {
          background: var(--td-success-color-1);
          color: var(--td-success-color);
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

  .diff-amount {
    font-size: 18px;
    font-weight: 600;
    color: var(--td-warning-color);
  }

  .diff-positive {
    color: var(--td-success-color);
  }

  .diff-negative {
    color: var(--td-error-color);
  }
}
</style>
