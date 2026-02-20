<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { MessagePlugin } from 'tdesign-vue-next'

// 统计数据
const statistics = reactive({
  totalEnergy: 128500,
  electricCost: 56800,
  waterCost: 12500,
  gasCost: 59200,
})

// 搜索条件
const searchForm = reactive({
  analysisPeriod: ['2026-02-01', '2026-02-28'],
  energyType: '',
  productionLine: '',
})

// 能源类型选项
const energyTypeOptions = [
  { label: '全部', value: '' },
  { label: '电力', value: 'electric' },
  { label: '水', value: 'water' },
  { label: '天然气', value: 'gas' },
  { label: '蒸汽', value: 'steam' },
]

// 生产线选项
const productionLineOptions = [
  { label: '全部', value: '' },
  { label: '生产线1', value: 'line1' },
  { label: '生产线2', value: 'line2' },
  { label: '生产线3', value: 'line3' },
  { label: '生产线4', value: 'line4' },
]

// 表格数据
const tableData = ref([])
const loading = ref(false)

// 分页
const pagination = reactive({
  current: 1,
  pageSize: 20,
  total: 156,
})

// 获取数据
const fetchData = async () => {
  loading.value = true
  try {
    // 模拟 API 调用
    await new Promise(resolve => setTimeout(resolve, 500))
    tableData.value = Array.from({ length: 20 }, (_, i) => ({
      key: i + 1,
      productionLine: ['生产线1', '生产线2', '生产线3', '生产线4'][i % 4],
      energyType: ['electric', 'water', 'gas', 'steam'][i % 4],
      planConsumption: Math.floor(Math.random() * 5000) + 3000,
      actualConsumption: Math.floor(Math.random() * 5000) + 3000,
      unit: ['kWh', 'm³', 'm³', 'kg'][i % 4],
      unitPrice: Math.floor(Math.random() * 5) + 1,
      totalCost: 0,
      energyEfficiency: 0,
      trend: ['up', 'down', 'stable'][i % 3],
    })).map(item => {
      item.totalCost = item.actualConsumption * item.unitPrice
      item.energyEfficiency = ((item.planConsumption / item.actualConsumption) * 100).toFixed(2)
      return item
    })
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  fetchData()
}

// 重置搜索
const handleReset = () => {
  Object.assign(searchForm, {
    analysisPeriod: ['2026-02-01', '2026-02-28'],
    energyType: '',
    productionLine: '',
  })
  handleSearch()
}

// 导出报表
const handleExport = () => {
  MessagePlugin.success('导出成功')
}

// 详情
const handleDetail = (record) => {
  MessagePlugin.info('查看能耗详情')
}

// 分页变化
const onPageChange = (pageInfo) => {
  pagination.current = pageInfo.current
  pagination.pageSize = pageInfo.pageSize
  fetchData()
}

// 能源类型标签
const getEnergyTypeTag = (type) => {
  const map = {
    electric: { label: '电力', theme: 'warning' },
    water: { label: '水', theme: 'primary' },
    gas: { label: '天然气', theme: 'danger' },
    steam: { label: '蒸汽', theme: 'success' },
  }
  return map[type] || { label: '未知', theme: 'default' }
}

// 趋势图标
const getTrendIcon = (trend) => {
  if (trend === 'up') return '↑'
  if (trend === 'down') return '↓'
  return '→'
}

onMounted(() => {
  fetchData()
})
</script>

<template>
  <div class="production-energy-management">
    <!-- 统计卡片 -->
    <t-row :gutter="16" class="statistics-cards">
      <t-col :span="6">
        <t-card>
          <t-statistic title="总能耗(元)" :value="statistics.totalEnergy" />
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="warning">
          <t-statistic title="电费(元)" :value="statistics.electricCost" />
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="primary">
          <t-statistic title="水费(元)" :value="statistics.waterCost" />
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="danger">
          <t-statistic title="燃气费(元)" :value="statistics.gasCost" />
        </t-card>
      </t-col>
    </t-row>

    <!-- 搜索条件 -->
    <t-card title="能耗分析条件" class="search-card">
      <t-form :data="searchForm" layout="inline">
        <t-form-item label="分析周期">
          <t-date-range-picker v-model="searchForm.analysisPeriod" clearable />
        </t-form-item>
        <t-form-item label="能源类型">
          <t-select v-model="searchForm.energyType" :options="energyTypeOptions" placeholder="请选择能源类型" clearable />
        </t-form-item>
        <t-form-item label="生产线">
          <t-select v-model="searchForm.productionLine" :options="productionLineOptions" placeholder="请选择生产线" clearable />
        </t-form-item>
        <t-form-item>
          <t-button theme="primary" @click="handleSearch">分析</t-button>
          <t-button theme="default" @click="handleReset">重置</t-button>
          <t-button theme="default" @click="handleExport">
            <template #icon><t-icon name="download" /></template>
            导出报表
          </t-button>
        </t-form-item>
      </t-form>
    </t-card>

    <!-- 能耗分析图表 -->
    <t-row :gutter="16" class="charts-row">
      <t-col :span="12">
        <t-card title="能耗趋势分析">
          <div class="chart-container" style="height: 300px;">
            <t-chart :data="{
              categories: ['1月', '2月', '3月', '4月', '5月', '6月'],
              series: [
                { name: '电力', data: [54000, 56800, 55000, 57000, 56000, 59000] },
                { name: '水', data: [12000, 12500, 11800, 13000, 12200, 13500] },
                { name: '天然气', data: [58000, 59200, 57000, 60000, 58000, 62000] }
              ]
            }" type="line" />
          </div>
        </t-card>
      </t-col>
      <t-col :span="12">
        <t-card title="能源类型分布">
          <div class="chart-container" style="height: 300px;">
            <t-chart :data="{
              categories: ['电力', '水', '天然气', '蒸汽'],
              series: [
                { name: '能耗占比', data: [44, 10, 46, 0] }
              ]
            }" type="pie" />
          </div>
        </t-card>
      </t-col>
    </t-row>

    <!-- 能耗明细表格 -->
    <t-card title="能耗明细" class="table-card">
      <t-table
        :data="tableData"
        :columns="[
          { colKey: 'productionLine', title: '生产线', width: 120 },
          { colKey: 'energyType', title: '能源类型', width: 120 },
          { colKey: 'planConsumption', title: '计划消耗', width: 120 },
          { colKey: 'actualConsumption', title: '实际消耗', width: 120 },
          { colKey: 'unit', title: '单位', width: 80 },
          { colKey: 'unitPrice', title: '单价(元)', width: 100 },
          { colKey: 'totalCost', title: '总成本(元)', width: 120 },
          { colKey: 'energyEfficiency', title: '能效(%)', width: 100 },
          { colKey: 'trend', title: '趋势', width: 80 },
          { colKey: 'action', title: '操作', width: 100 },
        ]"
        :loading="loading"
        :pagination="pagination"
        @page-change="onPageChange"
        row-key="key"
        bordered
        stripe
      >
        <template #energyType="{ row }">
          <t-tag :theme="getEnergyTypeTag(row.energyType).theme">
            {{ getEnergyTypeTag(row.energyType).label }}
          </t-tag>
        </template>
        <template #energyEfficiency="{ row }">
          <t-progress :percentage="parseFloat(row.energyEfficiency)" :theme="parseFloat(row.energyEfficiency) >= 90 ? 'success' : parseFloat(row.energyEfficiency) >= 70 ? 'warning' : 'danger'" />
        </template>
        <template #trend="{ row }">
          <span :style="{ color: row.trend === 'up' ? '#f5222d' : row.trend === 'down' ? '#52c41a' : '#8c8c8c' }">
            {{ getTrendIcon(row.trend) }}
          </span>
        </template>
        <template #action="{ row }">
          <t-link theme="primary" @click="handleDetail(row)">详情</t-link>
        </template>
      </t-table>
    </t-card>
  </div>
</template>

<style scoped lang="less">
.production-energy-management {
  padding: 16px;

  .statistics-cards {
    margin-bottom: 16px;
  }

  .search-card {
    margin-bottom: 16px;
  }

  .charts-row {
    margin-bottom: 16px;
  }

  .table-card {
    margin-bottom: 16px;
  }
}
</style>
