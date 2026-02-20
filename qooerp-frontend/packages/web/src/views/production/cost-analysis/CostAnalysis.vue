<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { MessagePlugin } from 'tdesign-vue-next'

// 统计数据
const statistics = reactive({
  totalCost: 2568000,
  averageCost: 4200,
  varianceRate: 5.2,
  savingsRate: 12.3,
})

// 搜索条件
const searchForm = reactive({
  analysisPeriod: ['2026-02-01', '2026-02-28'],
  productCode: '',
  productionLine: '',
  costType: '',
})

// 成本类型选项
const costTypeOptions = [
  { label: '全部', value: '' },
  { label: '直接材料', value: 'material' },
  { label: '直接人工', value: 'labor' },
  { label: '制造费用', value: 'overhead' },
  { label: '其他费用', value: 'other' },
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
      productCode: `P00${i + 1}`,
      productName: ['产品A', '产品B', '产品C', '产品D', '产品E'][i % 5],
      productionLine: ['生产线1', '生产线2', '生产线3', '生产线4'][i % 4],
      materialCost: Math.floor(Math.random() * 2000) + 1000,
      laborCost: Math.floor(Math.random() * 1000) + 500,
      overheadCost: Math.floor(Math.random() * 800) + 200,
      otherCost: Math.floor(Math.random() * 400) + 100,
      totalCost: 0,
      standardCost: Math.floor(Math.random() * 4500) + 4000,
      variance: 0,
      varianceRate: 0,
    })).map(item => {
      item.totalCost = item.materialCost + item.laborCost + item.overheadCost + item.otherCost
      item.variance = item.totalCost - item.standardCost
      item.varianceRate = ((item.variance / item.standardCost) * 100).toFixed(2)
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
    productCode: '',
    productionLine: '',
    costType: '',
  })
  handleSearch()
}

// 导出报表
const handleExport = () => {
  MessagePlugin.success('导出成功')
}

// 详情
const handleDetail = (record) => {
  MessagePlugin.info('查看成本详情')
}

// 分页变化
const onPageChange = (pageInfo) => {
  pagination.current = pageInfo.current
  pagination.pageSize = pageInfo.pageSize
  fetchData()
}

onMounted(() => {
  fetchData()
})
</script>

<template>
  <div class="production-cost-analysis">
    <!-- 统计卡片 -->
    <t-row :gutter="16" class="statistics-cards">
      <t-col :span="6">
        <t-card>
          <t-statistic title="总成本(元)" :value="statistics.totalCost" :precision="2" />
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="success">
          <t-statistic title="平均成本(元)" :value="statistics.averageCost" :precision="2" />
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="primary">
          <t-statistic title="成本差异率(%)" :value="statistics.varianceRate" :precision="1" />
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="warning">
          <t-statistic title="成本节约率(%)" :value="statistics.savingsRate" :precision="1" />
        </t-card>
      </t-col>
    </t-row>

    <!-- 搜索条件 -->
    <t-card title="成本分析条件" class="search-card">
      <t-form :data="searchForm" layout="inline">
        <t-form-item label="分析周期">
          <t-date-range-picker v-model="searchForm.analysisPeriod" clearable />
        </t-form-item>
        <t-form-item label="产品编码">
          <t-input v-model="searchForm.productCode" placeholder="请输入产品编码" clearable />
        </t-form-item>
        <t-form-item label="生产线">
          <t-select v-model="searchForm.productionLine" :options="productionLineOptions" placeholder="请选择生产线" clearable />
        </t-form-item>
        <t-form-item label="成本类型">
          <t-select v-model="searchForm.costType" :options="costTypeOptions" placeholder="请选择成本类型" clearable />
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

    <!-- 成本分析图表 -->
    <t-row :gutter="16" class="charts-row">
      <t-col :span="12">
        <t-card title="成本构成分析">
          <div class="chart-container" style="height: 300px;">
            <t-chart :data="{
              categories: ['直接材料', '直接人工', '制造费用', '其他费用'],
              series: [
                { name: '成本占比', data: [45, 30, 20, 5] }
              ]
            }" type="pie" />
          </div>
        </t-card>
      </t-col>
      <t-col :span="12">
        <t-card title="成本趋势分析">
          <div class="chart-container" style="height: 300px;">
            <t-chart :data="{
              categories: ['1月', '2月', '3月', '4月', '5月', '6月'],
              series: [
                { name: '实际成本', data: [4200, 4300, 4100, 4400, 4200, 4300] },
                { name: '标准成本', data: [4500, 4500, 4500, 4500, 4500, 4500] }
              ]
            }" type="line" />
          </div>
        </t-card>
      </t-col>
    </t-row>

    <!-- 成本TOP 10 -->
    <t-card title="成本TOP 10" class="top-card">
      <t-table
        :data="tableData.slice(0, 10)"
        :columns="[
          { colKey: 'rank', title: '排名', width: 80 },
          { colKey: 'productCode', title: '产品编码', width: 120 },
          { colKey: 'productName', title: '产品名称', width: 120 },
          { colKey: 'productionLine', title: '生产线', width: 120 },
          { colKey: 'materialCost', title: '直接材料(元)', width: 130 },
          { colKey: 'laborCost', title: '直接人工(元)', width: 130 },
          { colKey: 'overheadCost', title: '制造费用(元)', width: 130 },
          { colKey: 'otherCost', title: '其他费用(元)', width: 120 },
          { colKey: 'totalCost', title: '总成本(元)', width: 120 },
          { colKey: 'standardCost', title: '标准成本(元)', width: 130 },
          { colKey: 'variance', title: '成本差异(元)', width: 130 },
          { colKey: 'varianceRate', title: '差异率(%)', width: 100 },
          { colKey: 'action', title: '操作', width: 100 },
        ]"
        :loading="loading"
        row-key="key"
        bordered
        stripe
      >
        <template #rank="{ rowIndex }">
          <span :style="{ fontWeight: rowIndex < 3 ? 'bold' : 'normal', color: rowIndex < 3 ? '#f5222d' : '' }">
            {{ rowIndex + 1 }}
          </span>
        </template>
        <template #variance="{ row }">
          <span :style="{ color: row.variance > 0 ? '#f5222d' : '#52c41a' }">
            {{ row.variance > 0 ? '+' : '' }}{{ row.variance }}
          </span>
        </template>
        <template #varianceRate="{ row }">
          <t-tag :theme="parseFloat(row.varianceRate) > 0 ? 'danger' : 'success'">
            {{ parseFloat(row.varianceRate) > 0 ? '+' : '' }}{{ row.varianceRate }}%
          </t-tag>
        </template>
        <template #action="{ row }">
          <t-link theme="primary" @click="handleDetail(row)">详情</t-link>
        </template>
      </t-table>
    </t-card>

    <!-- 成本明细表格 -->
    <t-card title="成本明细" class="table-card">
      <t-table
        :data="tableData"
        :columns="[
          { colKey: 'productCode', title: '产品编码', width: 120 },
          { colKey: 'productName', title: '产品名称', width: 120 },
          { colKey: 'productionLine', title: '生产线', width: 120 },
          { colKey: 'materialCost', title: '直接材料(元)', width: 130 },
          { colKey: 'laborCost', title: '直接人工(元)', width: 130 },
          { colKey: 'overheadCost', title: '制造费用(元)', width: 130 },
          { colKey: 'otherCost', title: '其他费用(元)', width: 120 },
          { colKey: 'totalCost', title: '总成本(元)', width: 120 },
          { colKey: 'standardCost', title: '标准成本(元)', width: 130 },
          { colKey: 'variance', title: '成本差异(元)', width: 130 },
          { colKey: 'varianceRate', title: '差异率(%)', width: 100 },
          { colKey: 'action', title: '操作', width: 100 },
        ]"
        :loading="loading"
        :pagination="pagination"
        @page-change="onPageChange"
        row-key="key"
        bordered
        stripe
      >
        <template #variance="{ row }">
          <span :style="{ color: row.variance > 0 ? '#f5222d' : '#52c41a' }">
            {{ row.variance > 0 ? '+' : '' }}{{ row.variance }}
          </span>
        </template>
        <template #varianceRate="{ row }">
          <t-tag :theme="parseFloat(row.varianceRate) > 0 ? 'danger' : 'success'">
            {{ parseFloat(row.varianceRate) > 0 ? '+' : '' }}{{ row.varianceRate }}%
          </t-tag>
        </template>
        <template #action="{ row }">
          <t-link theme="primary" @click="handleDetail(row)">详情</t-link>
        </template>
      </t-table>
    </t-card>
  </div>
</template>

<style scoped lang="less">
.production-cost-analysis {
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
