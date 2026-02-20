<template>
  <div class="fund-report-page">
    <!-- 筛选卡片 -->
    <t-card title="报表筛选" class="filter-card">
      <t-form :data="filterForm" layout="inline" @submit="handleSearch" @reset="handleReset">
        <t-form-item label="报表类型" name="reportType">
          <t-select v-model="filterForm.reportType" placeholder="请选择报表类型">
            <t-option value="cashflow" label="现金流量表" />
            <t-option value="income" label="收支明细表" />
            <t-option value="balance" label="资金余额表" />
            <t-option value="revenue" label="收入汇总表" />
            <t-option value="expense" label="支出汇总表" />
          </t-select>
        </t-form-item>
        <t-form-item label="日期范围" name="dateRange">
          <t-date-range-picker v-model="filterForm.dateRange" placeholder="请选择日期范围" />
        </t-form-item>
        <t-form-item label="账户" name="account">
          <t-select v-model="filterForm.account" placeholder="请选择账户" clearable>
            <t-option v-for="acc in accounts" :key="acc.value" :value="acc.value" :label="acc.label" />
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
    <t-card class="action-card">
      <t-space>
        <t-button theme="primary" @click="handleExport">
          <template #icon><t-icon name="download" /></template>
          导出报表
        </t-button>
        <t-button theme="default" @click="handlePrint">
          <template #icon><t-icon name="print" /></template>
          打印报表
        </t-button>
        <t-button theme="success" @click="handleRefresh">
          <template #icon><t-icon name="refresh" /></template>
          刷新数据
        </t-button>
      </t-space>
    </t-card>

    <!-- 资金概览 -->
    <t-card title="资金概览" class="overview-card">
      <t-row :gutter="16">
        <t-col :span="6">
          <div class="overview-item income">
            <div class="overview-icon">
              <t-icon name="add-circle" size="28px" />
            </div>
            <div class="overview-content">
              <div class="overview-value">{{ formatMoney(overview.income) }}</div>
              <div class="overview-label">本期收入</div>
            </div>
          </div>
        </t-col>
        <t-col :span="6">
          <div class="overview-item expense">
            <div class="overview-icon">
              <t-icon name="remove-circle" size="28px" />
            </div>
            <div class="overview-content">
              <div class="overview-value">{{ formatMoney(overview.expense) }}</div>
              <div class="overview-label">本期支出</div>
            </div>
          </div>
        </t-col>
        <t-col :span="6">
          <div class="overview-item net">
            <div class="overview-icon">
              <t-icon name="wallet" size="28px" />
            </div>
            <div class="overview-content">
              <div class="overview-value">{{ formatMoney(overview.netIncome) }}</div>
              <div class="overview-label">净收支</div>
            </div>
          </div>
        </t-col>
        <t-col :span="6">
          <div class="overview-item balance">
            <div class="overview-icon">
              <t-icon name="money-circle" size="28px" />
            </div>
            <div class="overview-content">
              <div class="overview-value">{{ formatMoney(overview.balance) }}</div>
              <div class="overview-label">账户余额</div>
            </div>
          </div>
        </t-col>
      </t-row>
    </t-card>

    <!-- 现金流量表 -->
    <t-card v-if="filterForm.reportType === 'cashflow'" title="现金流量表" class="report-card">
      <t-table :data="cashflowData" :columns="cashflowColumns" stripe hover>
        <template #change="{ row }">
          <span :class="row.change >= 0 ? 'positive' : 'negative'">
            {{ row.change >= 0 ? '+' : '' }}{{ row.change }}%
          </span>
        </template>
      </t-table>
    </t-card>

    <!-- 收支明细表 -->
    <t-card v-if="filterForm.reportType === 'income'" title="收支明细表" class="report-card">
      <t-table :data="incomeData" :columns="incomeColumns" stripe hover>
        <template #type="{ row }">
          <t-tag v-if="row.type === 'income'" theme="success">收入</t-tag>
          <t-tag v-else theme="danger">支出</t-tag>
        </template>
        <template #amount="{ row }">
          <span :class="row.type === 'income' ? 'positive' : 'negative'">
            {{ row.type === 'income' ? '+' : '-' }}{{ formatMoney(row.amount) }}
          </span>
        </template>
      </t-table>
    </t-card>

    <!-- 资金余额表 -->
    <t-card v-if="filterForm.reportType === 'balance'" title="资金余额表" class="report-card">
      <t-table :data="balanceData" :columns="balanceColumns" stripe hover />
    </t-card>

    <!-- 收入汇总表 -->
    <t-card v-if="filterForm.reportType === 'revenue'" title="收入汇总表" class="report-card">
      <t-table :data="revenueData" :columns="revenueColumns" stripe hover>
        <template #trend="{ row }">
          <span :class="row.trend >= 0 ? 'positive' : 'negative'">
            <t-icon :name="row.trend >= 0 ? 'arrow-up' : 'arrow-down'" />
            {{ Math.abs(row.trend) }}%
          </span>
        </template>
      </t-table>
    </t-card>

    <!-- 支出汇总表 -->
    <t-card v-if="filterForm.reportType === 'expense'" title="支出汇总表" class="report-card">
      <t-table :data="expenseData" :columns="expenseColumns" stripe hover>
        <template #trend="{ row }">
          <span :class="row.trend <= 0 ? 'positive' : 'negative'">
            <t-icon :name="row.trend <= 0 ? 'arrow-down' : 'arrow-up'" />
            {{ Math.abs(row.trend) }}%
          </span>
        </template>
      </t-table>
    </t-card>

    <!-- 趋势图表 -->
    <t-card title="资金趋势图" class="chart-card">
      <div class="chart-container" ref="chartContainer"></div>
    </t-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, nextTick } from 'vue'
import { MessagePlugin } from 'tdesign-vue-next'

const filterForm = reactive({
  reportType: 'cashflow',
  dateRange: [],
  account: ''
})

const accounts = ref([
  { value: 'acc1', label: '中国工商银行' },
  { value: 'acc2', label: '中国建设银行' },
  { value: 'acc3', label: '支付宝' },
  { value: 'acc4', label: '微信支付' }
])

const overview = reactive({
  income: 1258000,
  expense: 865000,
  netIncome: 393000,
  balance: 3250000
})

const cashflowData = ref<any[]>([])
const incomeData = ref<any[]>([])
const balanceData = ref<any[]>([])
const revenueData = ref<any[]>([])
const expenseData = ref<any[]>([])

const cashflowColumns = [
  { colKey: 'category', title: '项目', width: 200 },
  { colKey: 'currentAmount', title: '本期金额', width: 150, cell: (h, { row }) => formatMoney(row.currentAmount) },
  { colKey: 'lastAmount', title: '上期金额', width: 150, cell: (h, { row }) => formatMoney(row.lastAmount) },
  { colKey: 'change', title: '变动幅度', width: 100 },
  { colKey: 'percentage', title: '占比', width: 100, cell: (h, { row }) => row.percentage + '%' }
]

const incomeColumns = [
  { colKey: 'date', title: '日期', width: 110 },
  { colKey: 'account', title: '账户', width: 120 },
  { colKey: 'type', title: '类型', width: 80 },
  { colKey: 'category', title: '分类', width: 120 },
  { colKey: 'amount', title: '金额', width: 120 },
  { colKey: 'description', title: '说明', width: 200 },
  { colKey: 'operator', title: '经办人', width: 100 }
]

const balanceColumns = [
  { colKey: 'account', title: '账户名称', width: 180 },
  { colKey: 'accountNo', title: '账号', width: 200 },
  { colKey: 'currency', title: '币种', width: 80 },
  { colKey: 'balance', title: '账户余额', width: 150, cell: (h, { row }) => formatMoney(row.balance) },
  { colKey: 'frozen', title: '冻结金额', width: 120, cell: (h, { row }) => formatMoney(row.frozen) },
  { colKey: 'available', title: '可用余额', width: 120, cell: (h, { row }) => formatMoney(row.available) },
  { colKey: 'updateTime', title: '更新时间', width: 160 }
]

const revenueColumns = [
  { colKey: 'category', title: '收入分类', width: 150 },
  { colKey: 'currentAmount', title: '本期金额', width: 150, cell: (h, { row }) => formatMoney(row.currentAmount) },
  { colKey: 'lastAmount', title: '上期金额', width: 150, cell: (h, { row }) => formatMoney(row.lastAmount) },
  { colKey: 'trend', title: '环比', width: 100 },
  { colKey: 'percentage', title: '占比', width: 100, cell: (h, { row }) => row.percentage + '%' }
]

const expenseColumns = [
  { colKey: 'category', title: '支出分类', width: 150 },
  { colKey: 'currentAmount', title: '本期金额', width: 150, cell: (h, { row }) => formatMoney(row.currentAmount) },
  { colKey: 'lastAmount', title: '上期金额', width: 150, cell: (h, { row }) => formatMoney(row.lastAmount) },
  { colKey: 'trend', title: '环比', width: 100 },
  { colKey: 'percentage', title: '占比', width: 100, cell: (h, { row }) => row.percentage + '%' }
]

onMounted(() => {
  loadCashflowData()
  loadIncomeData()
  loadBalanceData()
  loadRevenueData()
  loadExpenseData()
})

const loadCashflowData = async () => {
  cashflowData.value = [
    { category: '经营活动现金流入', currentAmount: 850000, lastAmount: 720000, change: 18.06, percentage: 67.6 },
    { category: '经营活动现金流出', currentAmount: 520000, lastAmount: 480000, change: 8.33, percentage: 60.1 },
    { category: '经营活动现金净额', currentAmount: 330000, lastAmount: 240000, change: 37.5, percentage: 0 },
    { category: '投资活动现金流入', currentAmount: 200000, lastAmount: 180000, change: 11.11, percentage: 15.9 },
    { category: '投资活动现金流出', currentAmount: 150000, lastAmount: 120000, change: 25, percentage: 17.3 },
    { category: '投资活动现金净额', currentAmount: 50000, lastAmount: 60000, change: -16.67, percentage: 0 },
    { category: '筹资活动现金流入', currentAmount: 208000, lastAmount: 150000, change: 38.67, percentage: 16.5 },
    { category: '筹资活动现金流出', currentAmount: 195000, lastAmount: 160000, change: 21.88, percentage: 22.5 },
    { category: '筹资活动现金净额', currentAmount: 13000, lastAmount: -10000, change: 230, percentage: 0 },
    { category: '现金净增加额', currentAmount: 393000, lastAmount: 290000, change: 35.52, percentage: 0 }
  ]
}

const loadIncomeData = async () => {
  incomeData.value = [
    { date: '2026-02-19', account: '中国工商银行', type: 'income', category: '销售收入', amount: 50000, description: '货款到账', operator: '张三' },
    { date: '2026-02-18', account: '支付宝', type: 'expense', category: '办公费用', amount: 1200, description: '办公用品采购', operator: '李四' },
    { date: '2026-02-18', account: '中国建设银行', type: 'income', category: '其他收入', amount: 8000, description: '退款收入', operator: '王五' },
    { date: '20xx-xx-xx', account: '微信支付', type: 'expense', category: '差旅费', amount: 3500, description: '出差报销', operator: '赵六' },
    { date: '20xx-xx-xx', account: '中国工商银行', type: 'income', category: '销售收入', amount: 120000, description: '货款到账', operator: '张三' },
    { date: '2026-02-16', account: '支付宝', type: 'expense', category: '采购付款', amount: 45000, description: '供应商付款', operator: '李四' },
    { date: '2026-02-16', account: '中国建设银行', type: 'expense', category: '工资福利', amount: 180000, description: '员工工资', operator: '王五' },
    { date: '2026-02-15', account: '中国工商银行', type: 'income', category: '销售收入', amount: 80000, description: '货款到账', operator: '张三' }
  ]
}

const loadBalanceData = async () => {
  balanceData.value = [
    { account: '中国工商银行', accountNo: '6222 0000 0000 0000', currency: 'CNY', balance: 1250000, frozen: 50000, available: 1200000, updateTime: '2026-02-19 10:30' },
    { account: '中国建设银行', accountNo: '6227 0000 0000 0000', currency: 'CNY', balance: 980000, frozen: 30000, available: 950000, updateTime: '2026-02-19 10:25' },
    { account: '支付宝', accountNo: 'alipay001', currency: 'CNY', balance: 520000, frozen: 0, available: 520000, updateTime: '2026-02-19 10:20' },
    { account: '微信支付', accountNo: 'wxpay001', currency: 'CNY', balance: 500000, frozen: 20000, available: 480000, updateTime: '2026-02-19 10:15' }
  ]
}

const loadRevenueData = async () => {
  revenueData.value = [
    { category: '销售收入', currentAmount: 980000, lastAmount: 850000, trend: 15.29, percentage: 77.9 },
    { category: '服务收入', currentAmount: 150000, lastAmount: 120000, trend: 25, percentage: 11.9 },
    { category: '投资收益', currentAmount: 80000, lastAmount: 60000, trend: 33.33, percentage: 6.4 },
    { category: '其他收入', currentAmount: 48000, lastAmount: 40000, trend: 20, percentage: 3.8 },
    { category: '合计', currentAmount: 1258000, lastAmount: 1070000, trend: 17.57, percentage: 100 }
  ]
}

const loadExpenseData = async () => {
  expenseData.value = [
    { category: '采购付款', currentAmount: 450000, lastAmount: 380000, trend: -18.42, percentage: 52.0 },
    { category: '工资福利', currentAmount: 250000, lastAmount: 220000, trend: -13.64, percentage: 28.9 },
    { category: '办公费用', currentAmount: 65000, lastAmount: 60000, trend: -8.33, percentage: 7.5 },
    { category: '差旅费', currentAmount: 45000, lastAmount: 50000, trend: 10, percentage: 5.2 },
    { category: '其他费用', currentAmount: 55000, lastAmount: 48000, trend: -14.58, percentage: 6.4 },
    { category: '合计', currentAmount: 865000, lastAmount: 758000, trend: -14.12, percentage: 100 }
  ]
}

const handleSearch = () => {
  loadCashflowData()
  loadIncomeData()
  loadBalanceData()
  loadRevenueData()
  loadExpenseData()
  MessagePlugin.success('报表数据已刷新')
}

const handleReset = () => {
  Object.assign(filterForm, {
    reportType: 'cashflow',
    dateRange: [],
    account: ''
  })
  handleSearch()
}

const handleExport = () => {
  MessagePlugin.success('报表导出成功')
}

const handlePrint = () => {
  window.print()
}

const handleRefresh = () => {
  handleSearch()
}

const formatMoney = (amount: number) => {
  return '¥' + amount.toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}
</script>

<style scoped lang="scss">
.fund-report-page {
  padding: 20px;

  .filter-card,
  .action-card,
  .overview-card,
  .report-card,
  .chart-card {
    margin-bottom: 16px;
  }

  .overview-item {
    display: flex;
    align-items: center;
    gap: 16px;
    padding: 20px;
    border-radius: 8px;

    &.income {
      background: linear-gradient(135deg, #00c853 0%, #00e676 100%);
      color: white;
    }

    &.expense {
      background: linear-gradient(135deg, #ff3d00 0%, #ff5722 100%);
      color: white;
    }

    &.net {
      background: linear-gradient(135deg, #2196f3 0%, #42a5f5 100%);
      color: white;
    }

    &.balance {
      background: linear-gradient(135deg, #9c27b0 0%, #ab47bc 100%);
      color: white;
    }

    .overview-icon {
      width: 56px;
      height: 56px;
      border-radius: 50%;
      background: rgba(255, 255, 255, 0.2);
      display: flex;
      align-items: center;
      justify-content: center;
    }

    .overview-content {
      flex: 1;

      .overview-value {
        font-size: 24px;
        font-weight: bold;
        line-height: 1.2;
      }

      .overview-label {
        font-size: 14px;
        opacity: 0.9;
        margin-top: 4px;
      }
    }
  }

  .positive {
    color: #00c853;
    font-weight: 500;
  }

  .negative {
    color: #ff3d00;
    font-weight: 500;
  }

  .chart-container {
    width: 100%;
    height: 400px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: #f7f8fa;
    border-radius: 8px;
    color: #999;
    font-size: 14px;
  }
}

@media print {
  .filter-card,
  .action-card {
    display: none !important;
  }
}
</style>
