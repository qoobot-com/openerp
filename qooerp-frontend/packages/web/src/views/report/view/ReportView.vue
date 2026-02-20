<template>
  <div class="report-view">
    <t-card :title="reportInfo.name" :bordered="false">
      <template #actions>
        <t-space>
          <t-button theme="default" @click="handleRefresh">
            <template #icon><refresh-icon /></template>
            刷新
          </t-button>
          <t-button theme="default" @click="handleExport">导出</t-button>
          <t-button theme="default" @click="handleShare">分享</t-button>
        </t-space>
      </template>

      <!-- 筛选条件 -->
      <div class="filter-bar">
        <t-form :data="filterForm" layout="inline">
          <t-form-item label="日期范围" name="dateRange">
            <t-date-range-picker v-model="filterForm.dateRange" placeholder="请选择日期范围" />
          </t-form-item>
          <t-form-item label="部门" name="department">
            <t-select v-model="filterForm.department" placeholder="请选择部门" clearable>
              <t-option value="all" label="全部" />
              <t-option value="tech" label="技术部" />
              <t-option value="sales" label="销售部" />
              <t-option value="market" label="市场部" />
            </t-select>
          </t-form-item>
          <t-form-item>
            <t-button theme="primary" @click="handleQuery">查询</t-button>
            <t-button theme="default" @click="handleReset">重置</t-button>
          </t-form-item>
        </t-form>
      </div>

      <!-- 统计卡片 -->
      <t-row :gutter="[16, 16]" class="stats-row">
        <t-col :span="6" v-for="stat in stats" :key="stat.title">
          <t-card :bordered="false" hover-shadow class="stat-card">
            <div class="stat-content">
              <div class="stat-icon" :style="{ background: stat.color }">
                <component :is="stat.icon" />
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ stat.value }}</div>
                <div class="stat-title">{{ stat.title }}</div>
                <div class="stat-trend" :class="stat.trend > 0 ? 'up' : 'down'">
                  <component :is="stat.trend > 0 ? 'chevron-up' : 'chevron-down'" />
                  <span>{{ Math.abs(stat.trend) }}%</span>
                  <span>较上期</span>
                </div>
              </div>
            </div>
          </t-card>
        </t-col>
      </t-row>

      <!-- 图表区域 -->
      <t-row :gutter="[16, 16]" class="charts-row">
        <t-col :span="16">
          <t-card title="销售趋势" :bordered="false" hover-shadow>
            <div ref="trendChartRef" class="chart"></div>
          </t-card>
        </t-col>
        <t-col :span="8">
          <t-card title="产品分布" :bordered="false" hover-shadow>
            <div ref="pieChartRef" class="chart"></div>
          </t-card>
        </t-col>
      </t-row>

      <t-row :gutter="[16, 16]" class="charts-row">
        <t-col :span="12">
          <t-card title="部门对比" :bordered="false" hover-shadow>
            <div ref="barChartRef" class="chart"></div>
          </t-card>
        </t-col>
        <t-col :span="12">
          <t-card title="区域分析" :bordered="false" hover-shadow>
            <div ref="mapChartRef" class="chart"></div>
          </t-card>
        </t-col>
      </t-row>

      <!-- 数据表格 -->
      <t-row :gutter="[16, 16]" class="table-row">
        <t-col :span="24">
          <t-card title="详细数据" :bordered="false" hover-shadow>
            <t-table
              :data="detailData"
              :columns="detailColumns"
              row-key="id"
              :pagination="{ pageSize: 10 }"
            />
          </t-card>
        </t-col>
      </t-row>
    </t-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { MessagePlugin } from 'tdesign-vue-next'
import {
  RefreshIcon, MoneyCircleIcon, ShoppingCartIcon, UsergroupIcon, ChevronUpIcon, ChevronDownIcon,
} from 'tdesign-icons-vue-next'
import * as echarts from 'echarts'

const reportInfo = ref({
  name: '销售业绩报表',
  code: 'sales_report',
})

const filterForm = reactive({
  dateRange: [],
  department: 'all',
})

const stats = [
  { title: '总销售额', value: '¥2,345,678', icon: MoneyCircleIcon, color: '#e7f4ff', trend: 15.3 },
  { title: '订单数量', value: '12,345', icon: ShoppingCartIcon, color: '#fff0e7', trend: 8.2 },
  { title: '客户数量', value: '1,234', icon: UsergroupIcon, color: '#e8f7ed', trend: 12.5 },
  { title: '平均客单价', value: '¥189.9', icon: MoneyCircleIcon, color: '#fef0e7', trend: -3.5 },
]

const detailData = ref([
  {
    id: 1,
    product: '产品A',
    sales: '¥456,789',
    orders: 2345,
    avgPrice: '¥194.7',
    growth: '15.3%',
  },
  {
    id: 2,
    product: '产品B',
    sales: '¥345,678',
    orders: 1890,
    avgPrice: '¥182.9',
    growth: '8.2%',
  },
  {
    id: 3,
    product: '产品C',
    sales: '¥234,567',
    orders: 1234,
    avgPrice: '¥190.1',
    growth: '-3.5%',
  },
])

const detailColumns = [
  { colKey: 'product', title: '产品名称' },
  { colKey: 'sales', title: '销售额', width: 150 },
  { colKey: 'orders', title: '订单数', width: 100 },
  { colKey: 'avgPrice', title: '平均客单价', width: 150 },
  { colKey: 'growth', title: '增长率', width: 100 },
]

const trendChartRef = ref<HTMLElement>()
const pieChartRef = ref<HTMLElement>()
const barChartRef = ref<HTMLElement>()
const mapChartRef = ref<HTMLElement>()

let trendChart: echarts.ECharts | null = null
let pieChart: echarts.ECharts | null = null
let barChart: echarts.ECharts | null = null
let mapChart: echarts.ECharts | null = null

const initTrendChart = () => {
  if (!trendChartRef.value) return
  trendChart = echarts.init(trendChartRef.value)

  const option = {
    tooltip: { trigger: 'axis' },
    legend: { data: ['销售额', '订单量'] },
    xAxis: {
      type: 'category',
      data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
    },
    yAxis: [
      { type: 'value', name: '销售额' },
      { type: 'value', name: '订单量' },
    ],
    series: [
      {
        name: '销售额',
        type: 'line',
        smooth: true,
        data: [320000, 350000, 280000, 420000, 380000, 450000, 520000],
      },
      {
        name: '订单量',
        type: 'bar',
        yAxisIndex: 1,
        data: [1500, 1800, 1200, 2100, 1900, 2300, 2800],
      },
    ],
  }

  trendChart.setOption(option)
}

const initPieChart = () => {
  if (!pieChartRef.value) return
  pieChart = echarts.init(pieChartRef.value)

  const option = {
    tooltip: { trigger: 'item' },
    legend: { orient: 'vertical', left: 'left' },
    series: [
      {
        name: '产品分布',
        type: 'pie',
        radius: ['40%', '70%'],
        data: [
          { value: 456789, name: '产品A' },
          { value: 345678, name: '产品B' },
          { value: 234567, name: '产品C' },
          { value: 123456, name: '产品D' },
        ],
      },
    ],
  }

  pieChart.setOption(option)
}

const initBarChart = () => {
  if (!barChartRef.value) return
  barChart = echarts.init(barChartRef.value)

  const option = {
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: ['技术部', '销售部', '市场部', '财务部', '行政部'],
    },
    yAxis: { type: 'value', name: '销售额' },
    series: [
      {
        name: '销售额',
        type: 'bar',
        data: [450000, 680000, 520000, 320000, 280000],
      },
    ],
  }

  barChart.setOption(option)
}

const initMapChart = () => {
  if (!mapChartRef.value) return
  mapChart = echarts.init(mapChartRef.value)

  const option = {
    tooltip: { trigger: 'item' },
    series: [
      {
        name: '区域销售',
        type: 'pie',
        radius: '50%',
        data: [
          { value: 450000, name: '华东' },
          { value: 320000, name: '华南' },
          { value: 280000, name: '华北' },
          { value: 180000, name: '西南' },
          { value: 150000, name: '西北' },
        ],
      },
    ],
  }

  mapChart.setOption(option)
}

const handleResize = () => {
  trendChart?.resize()
  pieChart?.resize()
  barChart?.resize()
  mapChart?.resize()
}

const handleRefresh = () => {
  MessagePlugin.success('刷新成功')
}

const handleQuery = () => {
  MessagePlugin.success('查询成功')
}

const handleReset = () => {
  Object.assign(filterForm, { dateRange: [], department: 'all' })
  MessagePlugin.success('重置成功')
}

const handleExport = () => {
  MessagePlugin.success('导出成功')
}

const handleShare = () => {
  MessagePlugin.success('分享成功')
}

onMounted(() => {
  initTrendChart()
  initPieChart()
  initBarChart()
  initMapChart()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  trendChart?.dispose()
  pieChart?.dispose()
  barChart?.dispose()
  mapChart?.dispose()
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped lang="scss">
.report-view {
  .filter-bar {
    margin-bottom: 16px;
  }

  .stats-row {
    margin-bottom: 16px;

    .stat-card {
      .stat-content {
        display: flex;
        align-items: center;
        gap: 16px;

        .stat-icon {
          width: 56px;
          height: 56px;
          border-radius: 12px;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 28px;
          color: var(--td-brand-color);
        }

        .stat-info {
          flex: 1;

          .stat-value {
            font-size: 20px;
            font-weight: 600;
            color: var(--td-text-color-primary);
            margin-bottom: 4px;
          }

          .stat-title {
            font-size: 14px;
            color: var(--td-text-color-secondary);
            margin-bottom: 8px;
          }

          .stat-trend {
            display: flex;
            align-items: center;
            gap: 4px;
            font-size: 12px;

            &.up { color: var(--td-success-color); }
            &.down { color: var(--td-error-color); }
          }
        }
      }
    }
  }

  .charts-row {
    margin-bottom: 16px;

    .chart {
      height: 300px;
    }
  }

  .table-row {
    margin-bottom: 16px;
  }
}
</style>
