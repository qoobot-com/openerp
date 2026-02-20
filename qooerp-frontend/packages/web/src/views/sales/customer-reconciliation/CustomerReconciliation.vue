<template>
  <div class="customer-reconciliation-container">
    <!-- 统计卡片 -->
    <t-row :gutter="[16, 16]" class="mb-4">
      <t-col :span="3">
        <t-card theme="primary1" :header="false" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon">
              <t-icon name="usergroup" size="40px" />
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.customerCount }}</div>
              <div class="stat-label">客户数</div>
            </div>
          </div>
        </t-card>
      </t-col>
      <t-col :span="3">
        <t-card theme="warning" :header="false" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon">
              <t-icon name="time" size="40px" />
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.pendingCount }}</div>
              <div class="stat-label">待对账</div>
            </div>
          </div>
        </t-card>
      </t-col>
      <t-col :span="3">
        <t-card theme="success" :header="false" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon">
              <t-icon name="money-circle" size="40px" />
            </div>
            <div class="stat-info">
              <div class="stat-value">¥{{ stats.receivableAmount.toLocaleString() }}</div>
              <div class="stat-label">应收金额</div>
            </div>
          </div>
        </t-card>
      </t-col>
      <t-col :span="3">
        <t-card theme="info" :header="false" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon">
              <t-icon name="check-circle" size="40px" />
            </div>
            <div class="stat-info">
              <div class="stat-value">¥{{ stats.receivedAmount.toLocaleString() }}</div>
              <div class="stat-label">已收金额</div>
            </div>
          </div>
        </t-card>
      </t-col>
    </t-row>

    <!-- 搜索卡片 -->
    <t-card class="mb-4">
      <t-form :data="searchForm" layout="inline" @submit="handleSearch" @reset="handleReset">
        <t-form-item label="客户" name="customerId">
          <t-select
            v-model="searchForm.customerId"
            placeholder="请选择客户"
            style="width: 200px"
            clearable
            filterable
          >
            <t-option v-for="item in customerList" :key="item.value" :value="item.value" :label="item.label" />
          </t-select>
        </t-form-item>
        <t-form-item label="对账期间" name="reconciliationPeriod">
          <t-date-range-picker
            v-model="searchForm.reconciliationPeriod"
            placeholder="请选择对账期间"
            style="width: 280px"
            clearable
          />
        </t-form-item>
        <t-form-item label="对账状态" name="status">
          <t-select
            v-model="searchForm.status"
            placeholder="请选择对账状态"
            style="width: 150px"
            clearable
          >
            <t-option value="pending" label="待对账" />
            <t-option value="confirmed" label="已确认" />
            <t-option value="completed" label="已完成" />
            <t-option value="cancelled" label="已取消" />
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
    <t-card class="mb-4">
      <t-space>
        <t-button theme="primary" @click="handleAdd">
          <template #icon>
            <t-icon name="add" />
          </template>
          新增对账
        </t-button>
        <t-button theme="success" @click="handleBatchConfirm">
          <template #icon>
            <t-icon name="check" />
          </template>
          批量确认
        </t-button>
        <t-button theme="default" @click="handleExport">
          <template #icon>
            <t-icon name="download" />
          </template>
          导出报表
        </t-button>
      </t-space>
    </t-card>

    <!-- 对账列表 -->
    <t-card>
      <t-table
        :data="reconciliationList"
        :columns="columns"
        :pagination="pagination"
        :loading="loading"
        stripe
        hover
        row-key="id"
        :selected-row-keys="selectedRowKeys"
        @select-change="handleSelectChange"
      >
        <template #status="{ row }">
          <t-tag v-if="row.status === 'pending'" theme="warning">待对账</t-tag>
          <t-tag v-else-if="row.status === 'confirmed'" theme="primary">已确认</t-tag>
          <t-tag v-else-if="row.status === 'completed'" theme="success">已完成</t-tag>
          <t-tag v-else theme="default">已取消</t-tag>
        </template>
        <template #action="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleView(row)">查看</t-link>
            <t-link v-if="row.status === 'pending'" theme="success" @click="handleConfirm(row)">确认</t-link>
            <t-link v-if="row.status === 'pending'" theme="warning" @click="handleProcessDiff(row)">处理差异</t-link>
            <t-link theme="default" @click="handleExportBill(row)">导出对账单</t-link>
          </t-space>
        </template>
      </t-table>
    </t-card>

    <!-- 新增对账单弹窗 -->
    <t-dialog
      v-model:visible="addDialogVisible"
      header="新增对账单"
      width="600px"
      :confirm-btn="null"
      :cancel-btn="null"
    >
      <t-form :data="addForm" label-width="120px" @submit="handleAddSubmit">
        <t-form-item label="客户" name="customerId">
          <t-select
            v-model="addForm.customerId"
            placeholder="请选择客户"
            filterable
          >
            <t-option v-for="item in customerList" :key="item.value" :value="item.value" :label="item.label" />
          </t-select>
        </t-form-item>
        <t-form-item label="对账期间" name="reconciliationPeriod">
          <t-date-range-picker v-model="addForm.reconciliationPeriod" placeholder="请选择对账期间" />
        </t-form-item>
        <t-form-item label="对账说明" name="remark">
          <t-textarea
            v-model="addForm.remark"
            placeholder="请输入对账说明"
            :autosize="{ minRows: 3, maxRows: 5 }"
          />
        </t-form-item>
        <t-form-item>
          <t-space>
            <t-button theme="primary" type="submit">确认</t-button>
            <t-button theme="default" @click="addDialogVisible = false">取消</t-button>
          </t-space>
        </t-form-item>
      </t-form>
    </t-dialog>

    <!-- 对账单详情弹窗 -->
    <t-dialog
      v-model:visible="detailDialogVisible"
      header="对账单详情"
      width="900px"
      :confirm-btn="null"
      :cancel-btn="null"
    >
      <t-tabs v-model="activeTab">
        <t-tab-panel value="basic" label="基本信息">
          <t-descriptions :column="2" bordered>
            <t-descriptions-item label="对账单号">{{ currentReconciliation.reconciliationNo }}</t-descriptions-item>
            <t-descriptions-item label="状态">
              <t-tag v-if="currentReconciliation.status === 'pending'" theme="warning">待对账</t-tag>
              <t-tag v-else-if="currentReconciliation.status === 'confirmed'" theme="primary">已确认</t-tag>
              <t-tag v-else-if="currentReconciliation.status === 'completed'" theme="success">已完成</t-tag>
              <t-tag v-else theme="default">已取消</t-tag>
            </t-descriptions-item>
            <t-descriptions-item label="客户">{{ currentReconciliation.customerName }}</t-descriptions-item>
            <t-descriptions-item label="对账期间">{{ currentReconciliation.period }}</t-descriptions-item>
            <t-descriptions-item label="订单数">{{ currentReconciliation.orderCount }}</t-descriptions-item>
            <t-descriptions-item label="订单金额">¥{{ currentReconciliation.orderAmount.toLocaleString() }}</t-descriptions-item>
            <t-descriptions-item label="已收金额">¥{{ currentReconciliation.receivedAmount.toLocaleString() }}</t-descriptions-item>
            <t-descriptions-item label="应收金额">¥{{ currentReconciliation.receivableAmount.toLocaleString() }}</t-descriptions-item>
            <t-descriptions-item label="差异金额">¥{{ currentReconciliation.diffAmount.toLocaleString() }}</t-descriptions-item>
            <t-descriptions-item label="创建时间">{{ currentReconciliation.createTime }}</t-descriptions-item>
            <t-descriptions-item label="对账时间">{{ currentReconciliation.reconciliationTime || '-' }}</t-descriptions-item>
            <t-descriptions-item label="确认时间">{{ currentReconciliation.confirmTime || '-' }}</t-descriptions-item>
            <t-descriptions-item label="对账说明">{{ currentReconciliation.remark || '-' }}</t-descriptions-item>
          </t-descriptions>
        </t-tab-panel>
        <t-tab-panel value="order" label="订单明细">
          <t-table :data="currentReconciliation.orderDetails" :columns="orderDetailColumns" stripe />
        </t-tab-panel>
        <t-tab-panel value="receipt" label="收款明细">
          <t-table :data="currentReconciliation.receiptDetails" :columns="receiptDetailColumns" stripe />
        </t-tab-panel>
        <t-tab-panel value="log" label="操作日志">
          <t-timeline>
            <t-timeline-item
              v-for="log in currentReconciliation.logs"
              :key="log.id"
              :label="log.time"
            >
              {{ log.operator }} {{ log.action }} - {{ log.remark }}
            </t-timeline-item>
          </t-timeline>
        </t-tab-panel>
      </t-tabs>
      <div class="dialog-footer">
        <t-button theme="default" @click="detailDialogVisible = false">关闭</t-button>
      </div>
    </t-dialog>

    <!-- 处理差异弹窗 -->
    <t-dialog
      v-model:visible="processDiffDialogVisible"
      header="处理差异"
      width="500px"
      :confirm-btn="null"
      :cancel-btn="null"
    >
      <t-form :data="processDiffForm" label-width="120px" @submit="handleProcessDiffSubmit">
        <t-form-item label="对账单号">
          <span>{{ currentReconciliation.reconciliationNo }}</span>
        </t-form-item>
        <t-form-item label="差异金额">
          <span style="color: #e34d59">¥{{ currentReconciliation.diffAmount.toLocaleString() }}</span>
        </t-form-item>
        <t-form-item label="差异原因" name="diffReason">
          <t-input v-model="processDiffForm.diffReason" placeholder="请输入差异原因" />
        </t-form-item>
        <t-form-item label="处理方式" name="processMethod">
          <t-radio-group v-model="processDiffForm.processMethod">
            <t-radio value="adjust">调整应收金额</t-radio>
            <t-radio value="supplement">补充收款</t-radio>
            <t-radio value="waive">豁免差异</t-radio>
          </t-radio-group>
        </t-form-item>
        <t-form-item label="处理说明" name="processRemark">
          <t-textarea
            v-model="processDiffForm.processRemark"
            placeholder="请输入处理说明"
            :autosize="{ minRows: 3, maxRows: 5 }"
          />
        </t-form-item>
        <t-form-item>
          <t-space>
            <t-button theme="primary" type="submit">确认处理</t-button>
            <t-button theme="default" @click="processDiffDialogVisible = false">取消</t-button>
          </t-space>
        </t-form-item>
      </t-form>
    </t-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { MessagePlugin, DialogPlugin } from 'tdesign-vue-next'

// 统计数据
const stats = ref({
  customerCount: 86,
  pendingCount: 12,
  receivableAmount: 520000,
  receivedAmount: 480000,
})

// 搜索表单
const searchForm = reactive({
  customerId: undefined,
  reconciliationPeriod: [],
  status: undefined,
})

// 客户列表
const customerList = ref([
  { value: '1', label: '北京科技有限公司' },
  { value: '2', label: '上海贸易公司' },
  { value: '3', label: '深圳电子有限公司' },
  { value: '4', label: '广州制造企业' },
  { value: '5', label: '杭州网络公司' },
])

// 分页
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 100,
  onChange: (pageInfo: any) => {
    pagination.current = pageInfo.current
    pagination.pageSize = pageInfo.pageSize
    loadData()
  },
})

// 加载状态
const loading = ref(false)

// 选中的行
const selectedRowKeys = ref([])

// 新增对账单弹窗
const addDialogVisible = ref(false)
const addForm = reactive({
  customerId: undefined,
  reconciliationPeriod: [],
  remark: '',
})

// 对账单详情弹窗
const detailDialogVisible = ref(false)
const activeTab = ref('basic')
const currentReconciliation = ref<any>({})

// 处理差异弹窗
const processDiffDialogVisible = ref(false)
const processDiffForm = reactive({
  diffReason: '',
  processMethod: 'adjust',
  processRemark: '',
})

// 对账列表
const reconciliationList = ref([
  {
    id: '1',
    reconciliationNo: 'CR202602190001',
    customerName: '北京科技有限公司',
    period: '2026-02',
    orderCount: 25,
    orderAmount: 180000,
    receivedAmount: 165000,
    receivableAmount: 15000,
    status: 'pending',
    createTime: '2026-02-19 10:00:00',
    reconciliationTime: '',
    confirmTime: '',
    diffAmount: 0,
    remark: '',
  },
  {
    id: '2',
    reconciliationNo: 'CR202602190002',
    customerName: '上海贸易公司',
    period: '2026-02',
    orderCount: 20,
    orderAmount: 135000,
    receivedAmount: 125000,
    receivableAmount: 10000,
    status: 'confirmed',
    createTime: '2026-02-19 11:00:00',
    reconciliationTime: '2026-02-19 14:00:00',
    confirmTime: '2026-02-19 16:00:00',
    diffAmount: 0,
    remark: '',
  },
  {
    id: '3',
    reconciliationNo: 'CR202602180001',
    customerName: '深圳电子有限公司',
    period: '2026-01',
    orderCount: 18,
    orderAmount: 118000,
    receivedAmount: 110000,
    receivableAmount: 8000,
    status: 'completed',
    createTime: '2026-02-18 09:00:00',
    reconciliationTime: '2026-02-18 10:00:00',
    confirmTime: '2026-02-18 15:00:00',
    diffAmount: 0,
    remark: '',
  },
  {
    id: '4',
    reconciliationNo: 'CR202602170001',
    customerName: '广州制造企业',
    period: '2026-01',
    orderCount: 15,
    orderAmount: 105000,
    receivedAmount: 95000,
    receivableAmount: 10000,
    status: 'pending',
    createTime: '2026-02-17 10:00:00',
    reconciliationTime: '',
    confirmTime: '',
    diffAmount: 500,
    remark: '',
  },
  {
    id: '5',
    reconciliationNo: 'CR202602160001',
    customerName: '杭州网络公司',
    period: '2026-01',
    orderCount: 12,
    orderAmount: 85000,
    receivedAmount: 85000,
    receivableAmount: 0,
    status: 'completed',
    createTime: '2026-02-16 11:00:00',
    reconciliationTime: '2026-02-16 12:00:00',
    confirmTime: '2026-02-16 17:00:00',
    diffAmount: 0,
    remark: '',
  },
])

// 列定义
const columns = [
  { colKey: 'reconciliationNo', title: '对账单号', width: 150 },
  { colKey: 'customerName', title: '客户', width: 200 },
  { colKey: 'period', title: '对账期间', width: 100 },
  { colKey: 'orderCount', title: '订单数', width: 100 },
  { colKey: 'orderAmount', title: '订单金额', width: 120 },
  { colKey: 'receivedAmount', title: '已收金额', width: 120 },
  { colKey: 'receivableAmount', title: '应收金额', width: 120 },
  { colKey: 'status', title: '状态', width: 100 },
  { colKey: 'createTime', title: '创建时间', width: 180 },
  { colKey: 'action', title: '操作', width: 250, fixed: 'right' },
]

// 订单明细列
const orderDetailColumns = [
  { colKey: 'orderNo', title: '订单编号', width: 150 },
  { colKey: 'orderDate', title: '订单日期', width: 120 },
  { colKey: 'quantity', title: '销售数量', width: 100 },
  { colKey: 'amount', title: '订单金额', width: 120 },
  { colKey: 'receivedAmount', title: '已收金额', width: 120 },
  { colKey: 'status', title: '状态', width: 100 },
]

// 收款明细列
const receiptDetailColumns = [
  { colKey: 'receiptNo', title: '收款单号', width: 150 },
  { colKey: 'receiptDate', title: '收款日期', width: 120 },
  { colKey: 'receiptAmount', title: '收款金额', width: 120 },
  { colKey: 'paymentMethod', title: '收款方式', width: 120 },
  { colKey: 'operator', title: '经办人', width: 100 },
]

// 加载数据
const loadData = () => {
  loading.value = true
  setTimeout(() => {
    loading.value = false
  }, 500)
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  loadData()
  MessagePlugin.success('查询成功')
}

// 重置
const handleReset = () => {
  searchForm.customerId = undefined
  searchForm.reconciliationPeriod = []
  searchForm.status = undefined
  pagination.current = 1
  loadData()
}

// 选择变化
const handleSelectChange = (value: any) => {
  selectedRowKeys.value = value
}

// 新增对账单
const handleAdd = () => {
  addForm.customerId = undefined
  addForm.reconciliationPeriod = []
  addForm.remark = ''
  addDialogVisible.value = true
}

// 新增对账单提交
const handleAddSubmit = () => {
  addDialogVisible.value = false
  MessagePlugin.success('对账单创建成功')
  loadData()
}

// 查看详情
const handleView = (row: any) => {
  currentReconciliation.value = {
    ...row,
    orderDetails: [
      { id: '1', orderNo: 'SO202602190001', orderDate: '2026-02-19', quantity: 50, amount: 12500, receivedAmount: 12500, status: '已完成' },
      { id: '2', orderNo: 'SO202602180001', orderDate: '2026-02-18', quantity: 30, amount: 8900, receivedAmount: 8900, status: '已完成' },
    ],
    receiptDetails: [
      { id: '1', receiptNo: 'SR202602190001', receiptDate: '2026-02-19', receiptAmount: 12500, paymentMethod: '银行转账', operator: '张三' },
      { id: '2', receiptNo: 'SR202602180001', receiptDate: '2026-02-18', receiptAmount: 8900, paymentMethod: '银行转账', operator: '李四' },
    ],
    logs: [
      { id: '1', time: '2026-02-19 10:00:00', operator: '张三', action: '创建对账单', remark: '' },
      { id: '2', time: '2026-02-19 10:30:00', operator: '张三', action: '导出对账单', remark: '' },
    ],
  }
  activeTab.value = 'basic'
  detailDialogVisible.value = true
}

// 确认对账
const handleConfirm = (row: any) => {
  const dialog = DialogPlugin({
    header: '确认对账',
    body: `是否确认对账单 ${row.reconciliationNo}？`,
    confirmBtn: {
      content: '确认',
      theme: 'primary',
    },
    onConfirm: () => {
      dialog.hide()
      MessagePlugin.success('对账确认成功')
      loadData()
    },
  })
}

// 批量确认
const handleBatchConfirm = () => {
  if (selectedRowKeys.value.length === 0) {
    MessagePlugin.warning('请先选择要对账的单据')
    return
  }
  const dialog = DialogPlugin({
    header: '批量确认',
    body: `是否确认对账选中的 ${selectedRowKeys.value.length} 个对账单？`,
    confirmBtn: {
      content: '确认',
      theme: 'primary',
    },
    onConfirm: () => {
      dialog.hide()
      MessagePlugin.success('批量确认成功')
      selectedRowKeys.value = []
      loadData()
    },
  })
}

// 处理差异
const handleProcessDiff = (row: any) => {
  currentReconciliation.value = row
  processDiffForm.diffReason = ''
  processDiffForm.processMethod = 'adjust'
  processDiffForm.processRemark = ''
  processDiffDialogVisible.value = true
}

// 处理差异提交
const handleProcessDiffSubmit = () => {
  processDiffDialogVisible.value = false
  MessagePlugin.success('差异处理成功')
  loadData()
}

// 导出对账单
const handleExportBill = (row: any) => {
  MessagePlugin.success(`对账单 ${row.reconciliationNo} 导出成功`)
}

// 导出报表
const handleExport = () => {
  MessagePlugin.success('报表导出成功')
}
</script>

<style scoped lang="less">
.customer-reconciliation-container {
  padding: 16px;
  background-color: #f3f3f3;
  min-height: 100vh;

  .mb-4 {
    margin-bottom: 16px;
  }

  .stat-card {
    .stat-content {
      display: flex;
      align-items: center;
      justify-content: space-between;

      .stat-icon {
        display: flex;
        align-items: center;
        justify-content: center;
        width: 70px;
        height: 70px;
        background-color: rgba(255, 255, 255, 0.3);
        border-radius: 12px;
      }

      .stat-info {
        flex: 1;
        text-align: center;
        padding-right: 70px;

        .stat-value {
          font-size: 28px;
          font-weight: bold;
          color: #fff;
          margin-bottom: 8px;
        }

        .stat-label {
          font-size: 14px;
          color: rgba(255, 255, 255, 0.85);
        }
      }
    }
  }

  .dialog-footer {
    margin-top: 20px;
    text-align: center;
  }
}
</style>
