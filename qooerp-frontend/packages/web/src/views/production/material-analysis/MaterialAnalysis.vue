<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { MessagePlugin } from 'tdesign-vue-next'

// 统计数据
const statistics = reactive({
  totalMaterial: 328,
  warningMaterial: 15,
  shortageMaterial: 5,
  utilizationRate: 92.5,
})

// 搜索条件
const searchForm = reactive({
  analysisPeriod: ['2026-02-01', '2026-02-28'],
  materialCode: '',
  materialType: '',
  productionLine: '',
})

// 物料类型选项
const materialTypeOptions = [
  { label: '全部', value: '' },
  { label: '原材料', value: 'raw' },
  { label: '辅助材料', value: 'auxiliary' },
  { label: '包装材料', value: 'packaging' },
  { label: '半成品', value: 'semi' },
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
  total: 328,
})

// 获取数据
const fetchData = async () => {
  loading.value = true
  try {
    // 模拟 API 调用
    await new Promise(resolve => setTimeout(resolve, 500))
    tableData.value = Array.from({ length: 20 }, (_, i) => ({
      key: i + 1,
      materialCode: `M00${i + 1}`,
      materialName: ['钢材', '铝材', '塑料', '木材', '橡胶'][i % 5],
      materialType: ['raw', 'auxiliary', 'packaging', 'semi'][i % 4],
      productionLine: ['生产线1', '生产线2', '生产线3', '生产线4'][i % 4],
      planQuantity: Math.floor(Math.random() * 1000) + 500,
      actualQuantity: Math.floor(Math.random() * 1000) + 500,
      usageRate: 0,
      unit: ['kg', 'm', '件'][i % 3],
      unitPrice: Math.floor(Math.random() * 50) + 10,
      totalCost: 0,
      status: ['normal', 'warning', 'shortage'][i % 3],
    })).map(item => {
      item.usageRate = ((item.actualQuantity / item.planQuantity) * 100).toFixed(2)
      item.totalCost = item.actualQuantity * item.unitPrice
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
    materialCode: '',
    materialType: '',
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
  MessagePlugin.info('查看物料详情')
}

// 分页变化
const onPageChange = (pageInfo) => {
  pagination.current = pageInfo.current
  pagination.pageSize = pageInfo.pageSize
  fetchData()
}

// 状态标签
const getStatusTag = (status) => {
  const map = {
    normal: { label: '正常', theme: 'success' },
    warning: { label: '预警', theme: 'warning' },
    shortage: { label: '短缺', theme: 'danger' },
  }
  return map[status] || { label: '未知', theme: 'default' }
}

onMounted(() => {
  fetchData()
})
</script>

<template>
  <div class="production-material-analysis">
    <!-- 统计卡片 -->
    <t-row :gutter="16" class="statistics-cards">
      <t-col :span="6">
        <t-card>
          <t-statistic title="物料总数" :value="statistics.totalMaterial" />
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="warning">
          <t-statistic title="预警物料" :value="statistics.warningMaterial" />
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="danger">
          <t-statistic title="短缺物料" :value="statistics.shortageMaterial" />
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="success">
          <t-statistic title="利用率(%)" :value="statistics.utilizationRate" :precision="1" />
        </t-card>
      </t-col>
    </t-row>

    <!-- 搜索条件 -->
    <t-card title="物料分析条件" class="search-card">
      <t-form :data="searchForm" layout="inline">
        <t-form-item label="分析周期">
          <t-date-range-picker v-model="searchForm.analysisPeriod" clearable />
        </t-form-item>
        <t-form-item label="物料编码">
          <t-input v-model="searchForm.materialCode" placeholder="请输入物料编码" clearable />
        </t-form-item>
        <t-form-item label="物料类型">
          <t-select v-model="searchForm.materialType" :options="materialTypeOptions" placeholder="请选择物料类型" clearable />
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

    <!-- 物料分析图表 -->
    <t-row :gutter="16" class="charts-row">
      <t-col :span="12">
        <t-card title="物料消耗趋势">
          <div class="chart-container" style="height: 300px;">
            <t-chart :data="{
              categories: ['1月', '2月', '3月', '4月', '5月', '6月'],
              series: [
                { name: '计划消耗', data: [5000, 5200, 4800, 5100, 4900, 5300] },
                { name: '实际消耗', data: [4800, 5000, 4700, 4900, 4800, 5100] }
              ]
            }" type="line" />
          </div>
        </t-card>
      </t-col>
      <t-col :span="12">
        <t-card title="物料类型分布">
          <div class="chart-container" style="height: 300px;">
            <t-chart :data="{
              categories: ['原材料', '辅助材料', '包装材料', '半成品'],
              series: [
                { name: '消耗占比', data: [40, 30, 20, 10] }
              ]
            }" type="pie" />
          </div>
        </t-card>
      </t-col>
    </t-row>

    <!-- 物料明细表格 -->
    <t-card title="物料明细" class="table-card">
      <t-table
        :data="tableData"
        :columns="[
          { colKey: 'materialCode', title: '物料编码', width: 120 },
          { colKey: 'materialName', title: '物料名称', width: 120 },
          { colKey: 'materialType', title: '物料类型', width: 120 },
          { colKey: 'productionLine', title: '生产线', width: 120 },
          { colKey: 'planQuantity', title: '计划数量', width: 100 },
          { colKey: 'actualQuantity', title: '实际数量', width: 100 },
          { colKey: 'usageRate', title: '利用率(%)', width: 100 },
          { colKey: 'unit', title: '单位', width: 80 },
          { colKey: 'unitPrice', title: '单价(元)', width: 100 },
          { colKey: 'totalCost', title: '总成本(元)', width: 120 },
          { colKey: 'status', title: '状态', width: 100 },
          { colKey: 'action', title: '操作', width: 100 },
        ]"
        :loading="loading"
        :pagination="pagination"
        @page-change="onPageChange"
        row-key="key"
        bordered
        stripe
      >
        <template #materialType="{ row }">
          <t-tag :theme="['primary', 'success', 'warning', 'danger'][['raw', 'auxiliary', 'packaging', 'semi'].indexOf(row.materialType)]">
            {{ { raw: '原材料', auxiliary: '辅助材料', packaging: '包装材料', semi: '半成品' }[row.materialType] }}
          </t-tag>
        </template>
        <template #usageRate="{ row }">
          <t-progress :percentage="parseFloat(row.usageRate)" :theme="parseFloat(row.usageRate) >= 90 ? 'success' : parseFloat(row.usageRate) >= 70 ? 'warning' : 'danger'" />
        </template>
        <template #status="{ row }">
          <t-tag :theme="getStatusTag(row.status).theme">
            {{ getStatusTag(row.status).label }}
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
.production-material-analysis {
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
