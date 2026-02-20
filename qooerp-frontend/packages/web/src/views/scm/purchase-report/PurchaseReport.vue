<template>
  <div class="purchase-report-container">
    <!-- 报表筛选 -->
    <t-card class="filter-card">
      <t-form :data="filterForm" layout="inline" @submit="handleFilter" @reset="handleReset">
        <t-form-item label="报表类型" name="reportType">
          <t-select v-model="filterForm.reportType" placeholder="请选择报表类型">
            <t-option value="order" label="采购订单报表" />
            <t-option value="supplier" label="供应商统计报表" />
            <t-option value="product" label="商品采购报表" />
            <t-option value="cost" label="采购成本分析" />
            <t-option value="payment" label="采购付款报表" />
          </t-select>
        </t-form-item>
        <t-form-item label="供应商" name="supplierId">
          <t-select v-model="filterForm.supplierId" placeholder="请选择供应商" clearable>
            <t-option value="1" label="供应商A" />
            <t-option value="2" label="供应商B" />
          </t-select>
        </t-form-item>
        <t-form-item label="日期范围" name="dateRange">
          <t-date-range-picker v-model="filterForm.dateRange" clearable />
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
        <t-button theme="primary" @click="handleExport">
          <template #icon><t-icon name="download" /></template>
          导出报表
        </t-button>
        <t-button theme="default" @click="handlePrint">
          <template #icon><t-icon name="print" /></template>
          打印报表
        </t-button>
        <t-button theme="default" @click="handleRefresh">
          <template #icon><t-icon name="refresh" /></template>
          刷新数据
        </t-button>
      </t-space>
    </div>

    <!-- 采购概览卡片 -->
    <t-card class="overview-card">
      <t-row :gutter="16">
        <t-col :span="6">
          <div class="overview-item">
            <div class="overview-label">采购订单数</div>
            <div class="overview-value">{{ overview.orderCount }}</div>
          </div>
        </t-col>
        <t-col :span="6">
          <div class="overview-item">
            <div class="overview-label">采购金额</div>
            <div class="overview-value amount">¥{{ formatMoney(overview.orderAmount) }}</div>
          </div>
        </t-col>
        <t-col :span="6">
          <div class="overview-item">
            <div class="overview-label">供应商数</div>
            <div class="overview-value">{{ overview.supplierCount }}</div>
          </div>
        </t-col>
        <t-col :span="6">
          <div class="overview-item">
            <div class="overview-label">采购商品数</div>
            <div class="overview-value">{{ overview.productCount }}</div>
          </div>
        </t-col>
      </t-row>
    </t-card>

    <!-- 报表内容 -->
    <t-card class="report-card">
      <!-- 采购订单报表 -->
      <div v-if="filterForm.reportType === 'order'" class="report-section">
        <div class="section-title">采购订单报表</div>
        <t-table :data="orderReportData" :columns="orderColumns" :loading="loading" :pagination="pagination" />
      </div>

      <!-- 供应商统计报表 -->
      <div v-else-if="filterForm.reportType === 'supplier'" class="report-section">
        <div class="section-title">供应商统计报表</div>
        <t-table :data="supplierReportData" :columns="supplierColumns" :loading="loading" :pagination="pagination" />
      </div>

      <!-- 商品采购报表 -->
      <div v-else-if="filterForm.reportType === 'product'" class="report-section">
        <div class="section-title">商品采购报表</div>
        <t-table :data="productReportData" :columns="productColumns" :loading="loading" :pagination="pagination" />
      </div>

      <!-- 采购成本分析 -->
      <div v-else-if="filterForm.reportType === 'cost'" class="report-section">
        <div class="section-title">采购成本分析</div>
        <t-table :data="costReportData" :columns="costColumns" :loading="loading" :pagination="pagination" />
      </div>

      <!-- 采购付款报表 -->
      <div v-else-if="filterForm.reportType === 'payment'" class="report-section">
        <div class="section-title">采购付款报表</div>
        <t-table :data="paymentReportData" :columns="paymentColumns" :loading="loading" :pagination="pagination" />
      </div>
    </t-card>

    <!-- 趋势图表（预留） -->
    <t-card class="chart-card">
      <div class="chart-title">采购趋势分析</div>
      <div class="chart-placeholder">
        <t-icon name="chart-line" size="48px" />
        <div>图表区域 - 预留</div>
      </div>
    </t-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { MessagePlugin } from 'tdesign-vue-next'

// 筛选表单
const filterForm = reactive({
  reportType: 'order',
  supplierId: '',
  dateRange: []
})

// 加载状态
const loading = ref(false)

// 分页
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0
})

// 采购概览
const overview = reactive({
  orderCount: 0,
  orderAmount: 0,
  supplierCount: 0,
  productCount: 0
})

// 采购订单报表数据
const orderReportData = ref([])
const orderColumns = [
  { colKey: 'orderNo', title: '订单编号', width: 150 },
  { colKey: 'supplierName', title: '供应商', width: 120 },
  { colKey: 'orderDate', title: '订单日期', width: 120 },
  { colKey: 'totalQuantity', title: '采购数量', width: 100 },
  { colKey: 'orderAmount', title: '订单金额', width: 120 },
  { colKey: 'paidAmount', title: '已付金额', width: 120 },
  { colKey: 'status', title: '状态', width: 100 },
  { colKey: 'creator', title: '创建人', width: 100 }
]

// 供应商统计报表数据
const supplierReportData = ref([])
const supplierColumns = [
  { colKey: 'supplierName', title: '供应商', width: 150 },
  { colKey: 'orderCount', title: '订单数', width: 100 },
  { colKey: 'totalQuantity', title: '采购数量', width: 100 },
  { colKey: 'totalAmount', title: '采购总额', width: 120 },
  { colKey: 'paidAmount', title: '已付金额', width: 120 },
  { colKey: 'unpaidAmount', title: '未付金额', width: 120 },
  { colKey: 'averageAmount', title: '平均订单金额', width: 120 },
  { colKey: 'lastOrderDate', title: '最近采购', width: 120 }
]

// 商品采购报表数据
const productReportData = ref([])
const productColumns = [
  { colKey: 'productNo', title: '商品编号', width: 120 },
  { colKey: 'productName', title: '商品名称', width: 150 },
  { colKey: 'spec', title: '规格', width: 100 },
  { colKey: 'unit', title: '单位', width: 80 },
  { colKey: 'orderCount', title: '采购次数', width: 100 },
  { colKey: 'totalQuantity', title: '采购总量', width: 100 },
  { colKey: 'totalAmount', title: '采购总额', width: 120 },
  { colKey: 'averagePrice', title: '平均单价', width: 100 },
  { colKey: 'maxPrice', title: '最高单价', width: 100 },
  { colKey: 'minPrice', title: '最低单价', width: 100 }
]

// 采购成本分析数据
const costReportData = ref([])
const costColumns = [
  { colKey: 'category', title: '成本类别', width: 150 },
  { colKey: 'currentAmount', title: '本期金额', width: 120 },
  { colKey: 'lastAmount', title: '上期金额', width: 120 },
  { colKey: 'diffAmount', title: '差异金额', width: 120 },
  { colKey: 'diffPercent', title: '差异率', width: 100 },
  { colKey: 'trend', title: '趋势', width: 100 }
]

// 采购付款报表数据
const paymentReportData = ref([])
const paymentColumns = [
  { colKey: 'paymentNo', title: '付款单号', width: 150 },
  { colKey: 'orderNo', title: '订单编号', width: 150 },
  { colKey: 'supplierName', title: '供应商', width: 120 },
  { colKey: 'paymentDate', title: '付款日期', width: 120 },
  { colKey: 'paymentAmount', title: '付款金额', width: 120 },
  { colKey: 'paymentMethod', title: '付款方式', width: 100 },
  { colKey: 'status', title: '状态', width: 100 },
  { colKey: 'creator', title: '经办人', width: 100 }
]

// 格式化金额
const formatMoney = (value: number) => {
  return (value || 0).toFixed(2)
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    await new Promise(resolve => setTimeout(resolve, 500))
    
    // 采购订单报表
    orderReportData.value = [
      {
        orderNo: 'PO-20260119001',
        supplierName: '供应商A',
        orderDate: '2026-01-19',
        totalQuantity: 100,
        orderAmount: 150000,
        paidAmount: 50000,
        status: '部分付款',
        creator: '张三'
      },
      {
        orderNo: 'PO-20260119002',
        supplierName: '供应商B',
        orderDate: '2026-01-18',
        totalQuantity: 50,
        orderAmount: 80000,
        paidAmount: 80000,
        status: '已付款',
        creator: '李四'
      }
    ]
    
    // 供应商统计报表
    supplierReportData.value = [
      {
        supplierName: '供应商A',
        orderCount: 15,
        totalQuantity: 500,
        totalAmount: 750000,
        paidAmount: 400000,
        unpaidAmount: 350000,
        averageAmount: 50000,
        lastOrderDate: '2026-01-19'
      },
      {
        supplierName: '供应商B',
        orderCount: 10,
        totalQuantity: 300,
        totalAmount: 450000,
        paidAmount: 450000,
        unpaidAmount: 0,
        averageAmount: 45000,
        lastOrderDate: '2026-01-18'
      }
    ]
    
    // 商品采购报表
    productReportData.value = [
      {
        productNo: 'P001',
        productName: '商品A',
        spec: '默认规格',
        unit: '件',
        orderCount: 20,
        totalQuantity: 400,
        totalAmount: 600000,
        averagePrice: 1500,
        maxPrice: 1600,
        minPrice: 1400
      },
      {
        productNo: 'P002',
        productName: '商品B',
        spec: '默认规格',
        unit: '件',
        orderCount: 15,
        totalQuantity: 300,
        totalAmount: 480000,
        averagePrice: 1600,
        maxPrice: 1700,
        minPrice: 1500
      }
    ]
    
    // 采购成本分析
    costReportData.value = [
      {
        category: '商品成本',
        currentAmount: 1000000,
        lastAmount: 900000,
        diffAmount: 100000,
        diffPercent: '11.11%',
        trend: '上升'
      },
      {
        category: '运输成本',
        currentAmount: 50000,
        lastAmount: 45000,
        diffAmount: 5000,
        diffPercent: '11.11%',
        trend: '上升'
      },
      {
        category: '其他成本',
        currentAmount: 30000,
        lastAmount: 35000,
        diffAmount: -5000,
        diffPercent: '-14.29%',
        trend: '下降'
      }
    ]
    
    // 采购付款报表
    paymentReportData.value = [
      {
        paymentNo: 'PAY-20260119001',
        orderNo: 'PO-20260119001',
        supplierName: '供应商A',
        paymentDate: '2026-01-19',
        paymentAmount: 50000,
        paymentMethod: '银行转账',
        status: '已付款',
        creator: '张三'
      },
      {
        paymentNo: 'PAY-20260119002',
        orderNo: 'PO-20260119002',
        supplierName: '供应商B',
        paymentDate: '2026-01-18',
        paymentAmount: 80000,
        paymentMethod: '银行转账',
        status: '已付款',
        creator: '李四'
      }
    ]
    
    pagination.total = 2
    
    overview.orderCount = 2
    overview.orderAmount = 230000
    overview.supplierCount = 2
    overview.productCount = 2
  } catch (error) {
    MessagePlugin.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

// 筛选
const handleFilter = () => {
  pagination.current = 1
  loadData()
}

// 重置
const handleReset = () => {
  Object.assign(filterForm, {
    reportType: 'order',
    supplierId: '',
    dateRange: []
  })
  handleFilter()
}

// 导出
const handleExport = () => {
  MessagePlugin.success('导出报表成功')
}

// 打印
const handlePrint = () => {
  MessagePlugin.success('打印报表成功')
}

// 刷新
const handleRefresh = () => {
  loadData()
  MessagePlugin.success('刷新成功')
}

onMounted(() => {
  loadData()
})
</script>

<style scoped lang="less">
.purchase-report-container {
  padding: 16px;

  .filter-card {
    margin-bottom: 16px;
  }

  .action-bar {
    margin-bottom: 16px;
  }

  .overview-card {
    margin-bottom: 16px;

    .overview-item {
      text-align: center;
      padding: 16px;
      border-radius: 8px;
      background: var(--td-bg-color-container-hover);

      .overview-label {
        font-size: 14px;
        color: var(--td-text-color-secondary);
        margin-bottom: 8px;
      }

      .overview-value {
        font-size: 28px;
        font-weight: 600;
        color: var(--td-text-color-primary);

        &.amount {
          color: var(--td-brand-color);
        }
      }
    }
  }

  .report-card {
    margin-bottom: 16px;

    .report-section {
      .section-title {
        font-size: 16px;
        font-weight: 600;
        color: var(--td-text-color-primary);
        margin-bottom: 16px;
      }
    }
  }

  .chart-card {
    .chart-title {
      font-size: 16px;
      font-weight: 600;
      color: var(--td-text-color-primary);
      margin-bottom: 16px;
    }

    .chart-placeholder {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      height: 300px;
      color: var(--td-text-color-placeholder);
      font-size: 14px;

      .t-icon {
        margin-bottom: 16px;
      }
    }
  }
}
</style>
