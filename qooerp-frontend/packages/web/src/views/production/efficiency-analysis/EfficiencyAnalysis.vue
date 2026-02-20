<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { MessagePlugin } from 'tdesign-vue-next'

// 统计数据
const statistics = reactive({
  efficiency: 87.5,
  utilization: 82.3,
  yield: 95.6,
  downtime: 4.2,
})

// 搜索条件
const searchForm = reactive({
  analysisPeriod: ['2026-02-01', '2026-02-28'],
  productionLine: '',
  workstation: '',
})

// 生产线选项
const productionLineOptions = [
  { label: '全部', value: '' },
  { label: '生产线1', value: 'line1' },
  { label: '生产线2', value: 'line2' },
  { label: '生产线3', value: 'line3' },
  { label: '生产线4', value: 'line4' },
]

// 工位选项
const workstationOptions = [
  { label: '全部', value: '' },
  { label: '工位1', value: 'ws1' },
  { label: '工位2', value: 'ws2' },
  { label: '工位3', value: 'ws3' },
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
      workstation: `工位${(i % 3) + 1}`,
      standardTime: Math.floor(Math.random() * 60) + 30,
      actualTime: Math.floor(Math.random() * 70) + 25,
      efficiency: 0,
      planOutput: Math.floor(Math.random() * 500) + 300,
      actualOutput: Math.floor(Math.random() * 500) + 300,
      utilization: 0,
      downtime: Math.floor(Math.random() * 60),
      trend: ['up', 'down', 'stable'][i % 3],
    })).map(item => {
      item.efficiency = ((item.standardTime / item.actualTime) * 100).toFixed(2)
      item.utilization = ((item.actualOutput / item.planOutput) * 100).toFixed(2)
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
    productionLine: '',
    workstation: '',
  })
  handleSearch()
}

// 导出报表
const handleExport = () => {
  MessagePlugin.success('导出成功')
}

// 详情
const handleDetail = (record) => {
  MessagePlugin.info('查看效率详情')
}

// 分页变化
const onPageChange = (pageInfo) => {
  pagination.current = pageInfo.current
  pagination.pageSize = pageInfo.pageSize
  fetchData()
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
  <div class="production-efficiency-analysis">
    <!-- 统计卡片 -->
    <t-row :gutter="16" class="statistics-cards">
      <t-col :span="6">
        <t-card>
          <t-statistic title="生产效率(%)" :value="statistics.efficiency" :precision="1" />
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="success">
          <t-statistic title="设备利用率(%)" :value="statistics.utilization" :precision="1" />
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="primary">
          <t-statistic title="成品率(%)" :value="statistics.yield" :precision="1" />
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="warning">
          <t-statistic title="停机时间(小时)" :value="statistics.downtime" :precision="1" />
        </t-card>
      </t-col>
    </t-row>

    <!-- 搜索条件 -->
    <t-card title="效率分析条件" class="search-card">
      <t-form :data="searchForm" layout="inline">
        <t-form-item label="分析周期">
          <t-date-range-picker v-model="searchForm.analysisPeriod" clearable />
        </t-form-item>
        <t-form-item label="生产线">
          <t-select v-model="searchForm.productionLine" :options="productionLineOptions" placeholder="请选择生产线" clearable />
        </t-form-item>
        <t-form-item label="工位">
          <t-select v-model="searchForm.workstation" :options="workstationOptions" placeholder="请选择工位" clearable />
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

    <!-- 效率分析图表 -->
    <t-row :gutter="16" class="charts-row">
      <t-col :span="12">
        <t-card title="效率趋势分析">
          <div class="chart-container" style="height: 300px;">
            <t-chart :data="{
              categories: ['1月', '2月', '3月', '4月', '5月', '6月'],
              series: [
                { name: '生产效率', data: [85, 87, 86, 88, 87.5, 89] },
                { name: '设备利用率', data: [80, 82, 81, 83, 82.3, 84] }
              ]
            }" type="line" />
          </div>
        </t-card>
      </t-col>
      <t-col :span="12">
        <t-card title="工位效率对比">
          <div class="chart-container" style="height: 300px;">
            <t-chart :data="{
              categories: ['工位1', '工位2', '工位3', '工位4', '工位5'],
              series: [
                { name: '生产效率', data: [88, 85, 87, 86, 89] },
                { name: '设备利用率', data: [83, 80, 82, 81, 84] }
              ]
            }" type="bar" />
          </div>
        </t-card>
      </t-col>
    </t-row>

    <!-- 效率TOP 10 -->
    <t-card title="效率TOP 10" class="top-card">
      <t-table
        :data="tableData.slice(0, 10)"
        :columns="[
          { colKey: 'rank', title: '排名', width: 80 },
          { colKey: 'productionLine', title: '生产线', width: 120 },
          { colKey: 'workstation', title: '工位', width: 100 },
          { colKey: 'standardTime', title: '标准时间(分钟)', width: 150 },
          { colKey: 'actualTime', title: '实际时间(分钟)', width: 150 },
          { colKey: 'efficiency', title: '效率(%)', width: 100 },
          { colKey: 'planOutput', title: '计划产量', width: 100 },
          { colKey: 'actualOutput', title: '实际产量', width: 100 },
          { colKey: 'utilization', title: '利用率(%)', width: 100 },
          { colKey: 'downtime', title: '停机时间(分钟)', width: 140 },
          { colKey: 'trend', title: '趋势', width: 80 },
          { colKey: 'action', title: '操作', width: 100 },
        ]"
        :loading="loading"
        row-key="key"
        bordered
        stripe
      >
        <template #rank="{ rowIndex }">
          <span :style="{ fontWeight: rowIndex < 3 ? 'bold' : 'normal', color: rowIndex < 3 ? '#52c41a' : '' }">
            {{ rowIndex + 1 }}
          </span>
        </template>
        <template #efficiency="{ row }">
          <t-progress :percentage="parseFloat(row.efficiency)" :theme="parseFloat(row.efficiency) >= 80 ? 'success' : parseFloat(row.efficiency) >= 60 ? 'warning' : 'danger'" />
        </template>
        <template #utilization="{ row }">
          <t-progress :percentage="parseFloat(row.utilization)" :theme="parseFloat(row.utilization) >= 80 ? 'success' : parseFloat(row.utilization) >= 60 ? 'warning' : 'danger'" />
        </template>
        <template #trend="{ row }">
          <span :style="{ color: row.trend === 'up' ? '#52c41a' : row.trend === 'down' ? '#f5222d' : '#8c8c8c' }">
            {{ getTrendIcon(row.trend) }}
          </span>
        </template>
        <template #action="{ row }">
          <t-link theme="primary" @click="handleDetail(row)">详情</t-link>
        </template>
      </t-table>
    </t-card>

    <!-- 效率明细表格 -->
    <t-card title="效率明细" class="table-card">
      <t-table
        :data="tableData"
        :columns="[
          { colKey: 'productionLine', title: '生产线', width: 120 },
          { colKey: 'workstation', title: '工位', width: 100 },
          { colKey: 'standardTime', title: '标准时间(分钟)', width: 150 },
          { colKey: 'actualTime', title: '实际时间(分钟)', width: 150 },
          { colKey: 'efficiency', title: '效率(%)', width: 100 },
          { colKey: 'planOutput', title: '计划产量', width: 100 },
          { colKey: 'actualOutput', title: '实际产量', width: 100 },
          { colKey: 'utilization', title: '利用率(%)', width: 100 },
          { colKey: 'downtime', title: '停机时间(分钟)', width: 140 },
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
        <template #efficiency="{ row }">
          <t-progress :percentage="parseFloat(row.efficiency)" :theme="parseFloat(row.efficiency) >= 80 ? 'success' : parseFloat(row.efficiency) >= 60 ? 'warning' : 'danger'" />
        </template>
        <template #utilization="{ row }">
          <t-progress :percentage="parseFloat(row.utilization)" :theme="parseFloat(row.utilization) >= 80 ? 'success' : parseFloat(row.utilization) >= 60 ? 'warning' : 'danger'" />
        </template>
        <template #trend="{ row }">
          <span :style="{ color: row.trend === 'up' ? '#52c41a' : row.trend === 'down' ? '#f5222d' : '#8c8c8c' }">
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
.production-efficiency-analysis {
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

  .top-card {
    margin-bottom: 16px;
  }

  .table-card {
    margin-bottom: 16px;
  }
}
</style>
