<template>
  <div class="sales-report-container">
    <!-- 统计卡片 -->
    <t-row :gutter="[16, 16]" class="mb-4">
      <t-col :span="3">
        <t-card theme="primary1" :header="false" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon">
              <t-icon name="file-list" size="40px" />
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.orderCount }}</div>
              <div class="stat-label">销售订单数</div>
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
              <div class="stat-value">¥{{ stats.totalAmount.toLocaleString() }}</div>
              <div class="stat-label">销售金额</div>
            </div>
          </div>
        </t-card>
      </t-col>
      <t-col :span="3">
        <t-card theme="warning" :header="false" class="stat-card">
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
        <t-card theme="info" :header="false" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon">
              <t-icon name="shop" size="40px" />
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.productCount }}</div>
              <div class="stat-label">销售商品数</div>
            </div>
          </div>
        </t-card>
      </t-col>
    </t-row>

    <!-- 搜索卡片 -->
    <t-card class="mb-4">
      <t-form :data="searchForm" layout="inline" @submit="handleSearch" @reset="handleReset">
        <t-form-item label="报表类型" name="reportType">
          <t-select
            v-model="searchForm.reportType"
            placeholder="请选择报表类型"
            style="width: 200px"
            clearable
          >
            <t-option value="order" label="销售订单报表" />
            <t-option value="customer" label="客户统计报表" />
            <t-option value="product" label="商品销售报表" />
            <t-option value="profit" label="销售利润分析" />
            <t-option value="receipt" label="销售收款报表" />
          </t-select>
        </t-form-item>
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
        <t-form-item label="日期范围" name="dateRange">
          <t-date-range-picker
            v-model="searchForm.dateRange"
            placeholder="请选择日期范围"
            style="width: 280px"
            clearable
          />
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
        <t-button theme="primary" @click="handleExport">
          <template #icon>
            <t-icon name="download" />
          </template>
          导出报表
        </t-button>
        <t-button theme="success" @click="handlePrint">
          <template #icon>
            <t-icon name="print" />
          </template>
          打印报表
        </t-button>
        <t-button theme="default" @click="handleRefresh">
          <template #icon>
            <t-icon name="refresh" />
          </template>
          刷新数据
        </t-button>
      </t-space>
    </t-card>

    <!-- 报表格卡片 -->
    <t-card>
      <!-- 销售订单报表 -->
      <template v-if="searchForm.reportType === 'order' || !searchForm.reportType">
        <h3 class="report-title">销售订单报表</h3>
        <t-table
          :data="orderReportData"
          :columns="orderReportColumns"
          :pagination="pagination"
          :loading="loading"
          stripe
          hover
          row-key="id"
        />
      </template>

      <!-- 客户统计报表 -->
      <template v-if="searchForm.reportType === 'customer'">
        <h3 class="report-title">客户统计报表</h3>
        <t-table
          :data="customerReportData"
          :columns="customerReportColumns"
          :pagination="pagination"
          :loading="loading"
          stripe
          hover
          row-key="id"
        />
      </template>

      <!-- 商品销售报表 -->
      <template v-if="searchForm.reportType === 'product'">
        <h3 class="report-title">商品销售报表</h3>
        <t-table
          :data="productReportData"
          :columns="productReportColumns"
          :pagination="pagination"
          :loading="loading"
          stripe
          hover
          row-key="id"
        />
      </template>

      <!-- 销售利润分析 -->
      <template v-if="searchForm.reportType === 'profit'">
        <h3 class="report-title">销售利润分析</h3>
        <t-table
          :data="profitReportData"
          :columns="profitReportColumns"
          :pagination="pagination"
          :loading="loading"
          stripe
          hover
          row-key="id"
        />
      </template>

      <!-- 销售收款报表 -->
      <template v-if="searchForm.reportType === 'receipt'">
        <h3 class="report-title">销售收款报表</h3>
        <t-table
          :data="receiptReportData"
          :columns="receiptReportColumns"
          :pagination="pagination"
          :loading="loading"
          stripe
          hover
          row-key="id"
        />
      </template>
    </t-card>

    <!-- 图表区域 -->
    <t-card class="mt-4">
      <h3 class="chart-title">销售趋势图</h3>
      <div class="chart-placeholder">
        <t-result title="图表开发中" description="销售趋势图表将在后续版本中提供" theme="default">
          <template #icon>
            <t-icon name="chart-bar" size="80px" />
          </template>
        </t-result>
      </div>
    </t-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { MessagePlugin, DialogPlugin } from 'tdesign-vue-next'

// 统计数据
const stats = ref({
  orderCount: 1250,
  totalAmount: 4580000,
  customerCount: 86,
  productCount: 325,
})

// 搜索表单
const searchForm = reactive({
  reportType: 'order',
  customerId: undefined,
  dateRange: [],
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

// 销售订单报表列
const orderReportColumns = [
  { colKey: 'orderNo', title: '订单编号', width: 150 },
  { colKey: 'customerName', title: '客户', width: 200 },
  { colKey: 'orderDate', title: '订单日期', width: 120 },
  { colKey: 'quantity', title: '销售数量', width: 100 },
  { colKey: 'amount', title: '订单金额', width: 120 },
  { colKey: 'receivedAmount', title: '已收金额', width: 120 },
  { colKey: 'status', title: '状态', width: 100 },
]

// 销售订单报表数据
const orderReportData = ref([
  {
    id: '1',
    orderNo: 'SO202602190001',
    customerName: '北京科技有限公司',
    orderDate: '2026-02-19',
    quantity: 50,
    amount: 12500,
    receivedAmount: 12500,
    status: '已完成',
  },
  {
    id: '2',
    orderNo: 'SO202602190002',
    customerName: '上海贸易公司',
    orderDate: '2026-02-19',
    quantity: 30,
    amount: 8900,
    receivedAmount: 5000,
    status: '部分收款',
  },
  {
    id: '3',
    orderNo: 'SO202602180001',
    customerName: '深圳电子有限公司',
    orderDate: '2026-02-18',
    quantity: 80,
    amount: 22000,
    receivedAmount: 22000,
    status: '已完成',
  },
  {
    id: '4',
    orderNo: 'SO202602180002',
    customerName: '广州制造企业',
    orderDate: '2026-02-18',
    quantity: 45,
    amount: 15000,
    receivedAmount: 0,
    status: '待收款',
  },
  {
    id: '5',
    orderNo: 'SO202602170001',
    customerName: '杭州网络公司',
    orderDate: '2026-02-17',
    quantity: 25,
    amount: 7500,
    receivedAmount: 7500,
    status: '已完成',
  },
])

// 客户统计报表列
const customerReportColumns = [
  { colKey: 'customerName', title: '客户', width: 200 },
  { colKey: 'orderCount', title: '订单数', width: 100 },
  { colKey: 'quantity', title: '销售数量', width: 120 },
  { colKey: 'totalAmount', title: '销售总额', width: 120 },
  { colKey: 'receivedAmount', title: '已收金额', width: 120 },
  { colKey: 'unpaidAmount', title: '未收金额', width: 120 },
  { colKey: 'avgAmount', title: '平均订单金额', width: 150 },
  { colKey: 'lastOrderDate', title: '最近订单', width: 120 },
]

// 客户统计报表数据
const customerReportData = ref([
  {
    id: '1',
    customerName: '北京科技有限公司',
    orderCount: 25,
    quantity: 650,
    totalAmount: 180000,
    receivedAmount: 165000,
    unpaidAmount: 15000,
    avgAmount: 7200,
    lastOrderDate: '2026-02-19',
  },
  {
    id: '2',
    customerName: '上海贸易公司',
    orderCount: 20,
    quantity: 480,
    totalAmount: 135000,
    receivedAmount: 125000,
    unpaidAmount: 10000,
    avgAmount: 6750,
    lastOrderDate: '2026-02-19',
  },
  {
    id: '3',
    customerName: '深圳电子有限公司',
    orderCount: 18,
    quantity: 420,
    totalAmount: 118000,
    receivedAmount: 118000,
    unpaidAmount: 0,
    avgAmount: 6556,
    lastOrderDate: '2026-02-18',
  },
  {
    id: '4',
    customerName: '广州制造企业',
    orderCount: 15,
    quantity: 380,
    totalAmount: 105000,
    receivedAmount: 95000,
    unpaidAmount: 10000,
    avgAmount: 7000,
    lastOrderDate: '2026-02-18',
  },
  {
    id: '5',
    customerName: '杭州网络公司',
    orderCount: 12,
    quantity: 290,
    totalAmount: 85000,
    receivedAmount: 85000,
    unpaidAmount: 0,
    avgAmount: 7083,
    lastOrderDate: '2026-02-17',
  },
])

// 商品销售报表列
const productReportColumns = [
  { colKey: 'productCode', title: '商品编号', width: 120 },
  { colKey: 'productName', title: '商品名称', width: 200 },
  { colKey: 'spec', title: '规格', width: 100 },
  { colKey: 'unit', title: '单位', width: 80 },
  { colKey: 'saleCount', title: '销售次数', width: 100 },
  { colKey: 'saleQuantity', title: '销售总量', width: 120 },
  { colKey: 'saleAmount', title: '销售总额', width: 120 },
  { colKey: 'avgPrice', title: '平均单价', width: 120 },
  { colKey: 'maxPrice', title: '最高单价', width: 120 },
  { colKey: 'minPrice', title: '最低单价', width: 120 },
]

// 商品销售报表数据
const productReportData = ref([
  {
    id: '1',
    productCode: 'P001',
    productName: '笔记本电脑',
    spec: 'i7/16G/512G',
    unit: '台',
    saleCount: 45,
    saleQuantity: 180,
    saleAmount: 720000,
    avgPrice: 4000,
    maxPrice: 4500,
    minPrice: 3800,
  },
  {
    id: '2',
    productCode: 'P002',
    productName: '无线鼠标',
    spec: '黑色',
    unit: '个',
    saleCount: 80,
    saleQuantity: 350,
    saleAmount: 17500,
    avgPrice: 50,
    maxPrice: 65,
    minPrice: 40,
  },
  {
    id: '3',
    productCode: 'P003',
    productName: '机械键盘',
    spec: '青轴',
    unit: '个',
    saleCount: 60,
    saleQuantity: 280,
    saleAmount: 42000,
    avgPrice: 150,
    maxPrice: 180,
    minPrice: 130,
  },
  {
    id: '4',
    productCode: 'P004',
    productName: '显示器',
    spec: '27寸/IPS',
    unit: '台',
    saleCount: 35,
    saleQuantity: 120,
    saleAmount: 108000,
    avgPrice: 900,
    maxPrice: 1100,
    minPrice: 850,
  },
  {
    id: '5',
    productCode: 'P005',
    productName: '耳机',
    spec: '降噪',
    unit: '个',
    saleCount: 90,
    saleQuantity: 400,
    saleAmount: 56000,
    avgPrice: 140,
    maxPrice: 180,
    minPrice: 120,
  },
])

// 销售利润分析列
const profitReportColumns = [
  { colKey: 'profitType', title: '利润类别', width: 150 },
  { colKey: 'currentAmount', title: '本期金额', width: 120 },
  { colKey: 'lastAmount', title: '上期金额', width: 120 },
  { colKey: 'diffAmount', title: '差异金额', width: 120 },
  { colKey: 'diffRate', title: '差异率', width: 100 },
  { colKey: 'trend', title: '趋势', width: 100 },
]

// 销售利润分析数据
const profitReportData = ref([
  {
    id: '1',
    profitType: '销售收入',
    currentAmount: 1200000,
    lastAmount: 1000000,
    diffAmount: 200000,
    diffRate: '+20%',
    trend: '上升',
  },
  {
    id: '2',
    profitType: '销售成本',
    currentAmount: 800000,
    lastAmount: 650000,
    diffAmount: 150000,
    diffRate: '+23.1%',
    trend: '上升',
  },
  {
    id: '3',
    profitType: '毛利',
    currentAmount: 400000,
    lastAmount: 350000,
    diffAmount: 50000,
    diffRate: '+14.3%',
    trend: '上升',
  },
  {
    id: '4',
    profitType: '销售费用',
    currentAmount: 50000,
    lastAmount: 45000,
    diffAmount: 5000,
    diffRate: '+11.1%',
    trend: '上升',
  },
  {
    id: '5',
    profitType: '净利润',
    currentAmount: 350000,
    lastAmount: 305000,
    diffAmount: 45000,
    diffRate: '+14.8%',
    trend: '上升',
  },
])

// 销售收款报表列
const receiptReportColumns = [
  { colKey: 'receiptNo', title: '收款单号', width: 150 },
  { colKey: 'orderNo', title: '订单编号', width: 150 },
  { colKey: 'customerName', title: '客户', width: 200 },
  { colKey: 'receiptDate', title: '收款日期', width: 120 },
  { colKey: 'receiptAmount', title: '收款金额', width: 120 },
  { colKey: 'paymentMethod', title: '收款方式', width: 120 },
  { colKey: 'status', title: '状态', width: 100 },
]

// 销售收款报表数据
const receiptReportData = ref([
  {
    id: '1',
    receiptNo: 'SR202602190001',
    orderNo: 'SO202602190001',
    customerName: '北京科技有限公司',
    receiptDate: '2026-02-19',
    receiptAmount: 12500,
    paymentMethod: '银行转账',
    status: '已确认',
  },
  {
    id: '2',
    receiptNo: 'SR202602190002',
    orderNo: 'SO202602190002',
    customerName: '上海贸易公司',
    receiptDate: '2026-02-19',
    receiptAmount: 5000,
    paymentMethod: '银行转账',
    status: '已确认',
  },
  {
    id: '3',
    receiptNo: 'SR202602180001',
    orderNo: 'SO202602180001',
    customerName: '深圳电子有限公司',
    receiptDate: '2026-02-18',
    receiptAmount: 22000,
    paymentMethod: '银行转账',
    status: '已确认',
  },
  {
    id: '4',
    receiptNo: 'SR202602170001',
    orderNo: 'SO202602170001',
    customerName: '杭州网络公司',
    receiptDate: '2026-02-17',
    receiptAmount: 7500,
    paymentMethod: '支付宝',
    status: '已确认',
  },
  {
    id: '5',
    receiptNo: 'SR202602160001',
    orderNo: 'SO202602160001',
    customerName: '广州制造企业',
    receiptDate: '2026-02-16',
    receiptAmount: 10000,
    paymentMethod: '银行转账',
    status: '已确认',
  },
])

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
  searchForm.reportType = 'order'
  searchForm.customerId = undefined
  searchForm.dateRange = []
  pagination.current = 1
  loadData()
}

// 导出报表
const handleExport = () => {
  const dialog = DialogPlugin({
    header: '确认导出',
    body: '是否确认导出当前报表数据？',
    confirmBtn: {
      content: '确认导出',
      theme: 'primary',
    },
    onConfirm: () => {
      dialog.hide()
      MessagePlugin.success('报表导出成功')
    },
  })
}

// 打印报表
const handlePrint = () => {
  window.print()
  MessagePlugin.success('正在打印报表...')
}

// 刷新数据
const handleRefresh = () => {
  loadData()
  MessagePlugin.success('数据刷新成功')
}
</script>

<style scoped lang="less">
.sales-report-container {
  padding: 16px;
  background-color: #f3f3f3;
  min-height: 100vh;

  .mb-4 {
    margin-bottom: 16px;
  }

  .mt-4 {
    margin-top: 16px;
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

  .report-title {
    font-size: 18px;
    font-weight: bold;
    margin-bottom: 16px;
    color: #303133;
  }

  .chart-title {
    font-size: 18px;
    font-weight: bold;
    margin-bottom: 16px;
    color: #303133;
  }

  .chart-placeholder {
    padding: 40px 0;
    text-align: center;
  }
}
</style>
