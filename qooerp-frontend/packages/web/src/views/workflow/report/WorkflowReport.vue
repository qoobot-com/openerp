<template>
  <div class="workflow-report">
    <t-card title="工作流报表" :bordered="false">
      <!-- 搜索条件 -->
      <div class="search-bar">
        <t-form :data="searchForm" layout="inline" @submit="handleSearch">
          <t-form-item label="流程类型" name="processType">
            <t-select v-model="searchForm.processType" placeholder="请选择流程类型" clearable>
              <t-option value="all" label="全部" />
              <t-option value="approval" label="审批流程" />
              <t-option value="business" label="业务流程" />
              <t-option value="system" label="系统流程" />
            </t-select>
          </t-form-item>
          <t-form-item label="日期范围" name="dateRange">
            <t-date-range-picker v-model="searchForm.dateRange" placeholder="请选择日期范围" />
          </t-form-item>
          <t-form-item>
            <t-button type="submit" theme="primary">查询</t-button>
            <t-button theme="default" @click="handleExport">导出</t-button>
          </t-form-item>
        </t-form>
      </div>

      <!-- 报表统计 -->
      <t-row :gutter="[16, 16]" class="stats-row">
        <t-col :span="6">
          <t-statistic title="总流程数" :value="statistics.total" />
        </t-col>
        <t-col :span="6">
          <t-statistic title="运行中" :value="statistics.running" />
        </t-col>
        <t-col :span="6">
          <t-statistic title="已完成" :value="statistics.completed" />
        </t-col>
        <t-col :span="6">
          <t-statistic title="平均耗时" :value="statistics.avgDuration" suffix="小时" />
        </t-col>
      </t-row>

      <!-- 图表分析 -->
      <t-row :gutter="[16, 16]" class="charts-row">
        <t-col :span="12">
          <t-card title="流程趋势分析" :bordered="false" hover-shadow>
            <div ref="trendChartRef" class="chart"></div>
          </t-card>
        </t-col>
        <t-col :span="12">
          <t-card title="流程类型分布" :bordered="false" hover-shadow>
            <div ref="pieChartRef" class="chart"></div>
          </t-card>
        </t-col>
      </t-row>

      <t-row :gutter="[16, 16]" class="charts-row">
        <t-col :span="12">
          <t-card title="流程效率分析" :bordered="false" hover-shadow>
            <div ref="efficiencyChartRef" class="chart"></div>
          </t-card>
        </t-col>
        <t-col :span="12">
          <t-card title="节点处理统计" :bordered="false" hover-shadow>
            <div ref="nodeChartRef" class="chart"></div>
          </t-card>
        </t-col>
      </t-row>

      <!-- 详细数据 -->
      <t-row :gutter="[16, 16]" class="table-row">
        <t-col :span="24">
          <t-card title="流程明细" :bordered="false" hover-shadow>
            <t-table
              :data="detailData"
              :columns="detailColumns"
              row-key="id"
              :pagination="{ pageSize: 10 }"
            >
              <template #status="{ row }">
                <t-tag
                  :theme="{
                    running: 'primary',
                    completed: 'success',
                    terminated: 'danger',
                  }[row.status]"
                >
                  {{ { running: '运行中', completed: '已完成', terminated: '已终止' }[row.status] }}
                </t-tag>
              </template>
            </t-table>
          </t-card>
        </t-col>
      </t-row>
    </t-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { MessagePlugin } from 'tdesign-vue-next'
import * as echarts from 'echarts'

const searchForm = reactive({
  processType: 'all',
  dateRange: [],
})

const statistics = reactive({
  total: 1256,
  running: 156,
  completed: 1089,
  avgDuration: 4.5,
})

const detailData = ref([
  {
    id: 1,
    processName: '请假审批流程',
    instances: 256,
    completed: 230,
    running: 25,
    terminated: 1,
    avgDuration: '3.5小时',
    status: 'running',
  },
  {
    id: 2,
    processName: '报销审批流程',
    instances: 189,
    completed: 178,
    running: 11,
    terminated: 0,
    avgDuration: '5.2小时',
    status: 'running',
  },
  {
    id: 3,
    processName: '采购审批流程',
    instances: 145,
    completed: 135,
    running: 10,
    terminated: 0,
    avgDuration: '4.8小时',
    status: 'running',
  },
])

const detailColumns = [
  { colKey: 'processName', title: '流程名称' },
  { colKey: 'instances', title: '总实例数', width: 100 },
  { colKey: 'completed', title: '已完成', width: 100 },
  { colKey: 'running', title: '运行中', width: 100 },
  { colKey: 'terminated', title: '已终止', width: 100 },
  { colKey: 'avgDuration', title: '平均耗时', width: 120 },
  { colKey: 'status', title: '状态', width: 100 },
]

const trendChartRef = ref<HTMLElement>()
const pieChartRef = ref<HTMLElement>()
const efficiencyChartRef = ref<HTMLElement>()
const nodeChartRef = ref<HTMLElement>()

let trendChart: echarts.ECharts | null = null
let pieChart: echarts.ECharts | null = null
let efficiencyChart: echarts.ECharts | null = null
let nodeChart: echarts.ECharts | null = null

const initTrendChart = () => {
  if (!trendChartRef.value) return
  trendChart = echarts.init(trendChartRef.value)

  const option = {
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
    },
    yAxis: { type: 'value' },
    series: [
      {
        name: '发起数',
        type: 'line',
        smooth: true,
        data: [45, 52, 38, 65, 48, 25, 30],
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
        name: '流程类型',
        type: 'pie',
        radius: ['40%', '70%'],
        data: [
          { value: 600, name: '审批流程' },
          { value: 400, name: '业务流程' },
          { value: 256, name: '系统流程' },
        ],
      },
    ],
  }

  pieChart.setOption(option)
}

const initEfficiencyChart = () => {
  if (!efficiencyChartRef.value) return
  efficiencyChart = echarts.init(efficiencyChartRef.value)

  const option = {
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: ['请假审批', '报销审批', '采购审批', '合同审批'],
    },
    yAxis: { type: 'value', name: '小时' },
    series: [
      {
        name: '平均耗时',
        type: 'bar',
        data: [3.5, 5.2, 4.8, 6.5],
      },
    ],
  }

  efficiencyChart.setOption(option)
}

const initNodeChart = () => {
  if (!nodeChartRef.value) return
  nodeChart = echarts.init(nodeChartRef.value)

  const option = {
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: ['开始', '部门审批', '财务审批', '总经理审批', '结束'],
    },
    yAxis: { type: 'value' },
    series: [
      {
        name: '处理次数',
        type: 'bar',
        data: [1256, 1120, 856, 234, 1089],
      },
    ],
  }

  nodeChart.setOption(option)
}

const handleResize = () => {
  trendChart?.resize()
  pieChart?.resize()
  efficiencyChart?.resize()
  nodeChart?.resize()
}

const handleSearch = () => {
  MessagePlugin.success('查询成功')
}

const handleExport = () => {
  MessagePlugin.success('导出成功')
}

onMounted(() => {
  initTrendChart()
  initPieChart()
  initEfficiencyChart()
  initNodeChart()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  trendChart?.dispose()
  pieChart?.dispose()
  efficiencyChart?.dispose()
  nodeChart?.dispose()
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped lang="scss">
.workflow-report {
  .search-bar {
    margin-bottom: 16px;
  }

  .stats-row {
    margin-bottom: 16px;
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
