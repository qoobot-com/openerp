<template>
  <div class="performance-analysis">
    <t-card title="流程性能分析" :bordered="false">
      <!-- 搜索条件 -->
      <div class="search-bar">
        <t-form :data="searchForm" layout="inline" @submit="handleSearch">
          <t-form-item label="流程类型" name="processType">
            <t-select v-model="searchForm.processType" placeholder="请选择流程类型" clearable>
              <t-option value="all" label="全部" />
              <t-option value="approval" label="审批流程" />
              <t-option value="business" label="业务流程" />
            </t-select>
          </t-form-item>
          <t-form-item label="日期范围" name="dateRange">
            <t-date-range-picker v-model="searchForm.dateRange" placeholder="请选择日期范围" />
          </t-form-item>
          <t-form-item>
            <t-button type="submit" theme="primary">分析</t-button>
            <t-button theme="default" @click="handleExport">导出报告</t-button>
          </t-form-item>
        </t-form>
      </div>

      <!-- 性能指标 -->
      <t-row :gutter="[16, 16]" class="stats-row">
        <t-col :span="6">
          <t-card :bordered="false" hover-shadow>
            <div class="stat-item">
              <div class="stat-label">平均完成时间</div>
              <div class="stat-value">4.5 <span class="unit">小时</span></div>
              <div class="stat-trend down">↓ 12% 较上月</div>
            </div>
          </t-card>
        </t-col>
        <t-col :span="6">
          <t-card :bordered="false" hover-shadow>
            <div class="stat-item">
              <div class="stat-label">超时率</div>
              <div class="stat-value">2.3 <span class="unit">%</span></div>
              <div class="stat-trend down">↓ 5% 较上月</div>
            </div>
          </t-card>
        </t-col>
        <t-col :span="6">
          <t-card :bordered="false" hover-shadow>
            <div class="stat-item">
              <div class="stat-label">节点平均处理时间</div>
              <div class="stat-value">1.2 <span class="unit">小时</span></div>
              <div class="stat-trend down">↓ 8% 较上月</div>
            </div>
          </t-card>
        </t-col>
        <t-col :span="6">
          <t-card :bordered="false" hover-shadow>
            <div class="stat-item">
              <div class="stat-label">流程完成率</div>
              <div class="stat-value">98.5 <span class="unit">%</span></div>
              <div class="stat-trend up">↑ 2% 较上月</div>
            </div>
          </t-card>
        </t-col>
      </t-row>

      <!-- 图表分析 -->
      <t-row :gutter="[16, 16]" class="charts-row">
        <t-col :span="12">
          <t-card title="流程耗时趋势" :bordered="false" hover-shadow>
            <div ref="trendChartRef" class="chart"></div>
          </t-card>
        </t-col>
        <t-col :span="12">
          <t-card title="节点耗时分布" :bordered="false" hover-shadow>
            <div ref="barChartRef" class="chart"></div>
          </t-card>
        </t-col>
      </t-row>

      <t-row :gutter="[16, 16]" class="charts-row">
        <t-col :span="12">
          <t-card title="瓶颈节点分析" :bordered="false" hover-shadow>
            <div ref="bottleneckChartRef" class="chart"></div>
          </t-card>
        </t-col>
        <t-col :span="12">
          <t-card title="人员效率统计" :bordered="false" hover-shadow>
            <div ref="userChartRef" class="chart"></div>
          </t-card>
        </t-col>
      </t-row>

      <!-- 详细数据 -->
      <t-row :gutter="[16, 16]" class="table-row">
        <t-col :span="24">
          <t-card title="流程性能明细" :bordered="false" hover-shadow>
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
import * as echarts from 'echarts'

const searchForm = reactive({
  processType: 'all',
  dateRange: [],
})

const detailData = ref([
  {
    id: 1,
    processName: '请假审批流程',
    avgDuration: '3.5小时',
    maxDuration: '8.2小时',
    minDuration: '1.5小时',
    timeoutRate: '1.2%',
    completeRate: '99.5%',
    nodeCount: 4,
  },
  {
    id: 2,
    processName: '报销审批流程',
    avgDuration: '5.2小时',
    maxDuration: '12.5小时',
    minDuration: '2.1小时',
    timeoutRate: '2.8%',
    completeRate: '98.2%',
    nodeCount: 5,
  },
  {
    id: 3,
    processName: '采购审批流程',
    avgDuration: '4.8小时',
    maxDuration: '15.3小时',
    minDuration: '2.5小时',
    timeoutRate: '3.5%',
    completeRate: '97.8%',
    nodeCount: 6,
  },
])

const detailColumns = [
  { colKey: 'processName', title: '流程名称' },
  { colKey: 'avgDuration', title: '平均耗时', width: 120 },
  { colKey: 'maxDuration', title: '最大耗时', width: 120 },
  { colKey: 'minDuration', title: '最小耗时', width: 120 },
  { colKey: 'timeoutRate', title: '超时率', width: 100 },
  { colKey: 'completeRate', title: '完成率', width: 100 },
  { colKey: 'nodeCount', title: '节点数', width: 80 },
]

const trendChartRef = ref<HTMLElement>()
const barChartRef = ref<HTMLElement>()
const bottleneckChartRef = ref<HTMLElement>()
const userChartRef = ref<HTMLElement>()

let trendChart: echarts.ECharts | null = null
let barChart: echarts.ECharts | null = null
let bottleneckChart: echarts.ECharts | null = null
let userChart: echarts.ECharts | null = null

const initTrendChart = () => {
  if (!trendChartRef.value) return
  trendChart = echarts.init(trendChartRef.value)

  const option = {
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
    },
    yAxis: { type: 'value', name: '小时' },
    series: [
      {
        name: '平均耗时',
        type: 'line',
        smooth: true,
        data: [4.5, 4.2, 4.8, 5.1, 4.3, 3.8, 4.0],
      },
    ],
  }

  trendChart.setOption(option)
}

const initBarChart = () => {
  if (!barChartRef.value) return
  barChart = echarts.init(barChartRef.value)

  const option = {
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: ['开始', '部门审批', '财务审批', '总经理审批', '结束'],
    },
    yAxis: { type: 'value', name: '小时' },
    series: [
      {
        name: '平均耗时',
        type: 'bar',
        data: [0.1, 1.5, 2.3, 1.8, 0.1],
      },
    ],
  }

  barChart.setOption(option)
}

const initBottleneckChart = () => {
  if (!bottleneckChartRef.value) return
  bottleneckChart = echarts.init(bottleneckChartRef.value)

  const option = {
    tooltip: { trigger: 'item' },
    series: [
      {
        name: '节点耗时占比',
        type: 'pie',
        radius: ['40%', '70%'],
        data: [
          { value: 2.3, name: '财务审批' },
          { value: 1.8, name: '总经理审批' },
          { value: 1.5, name: '部门审批' },
          { value: 0.2, name: '其他' },
        ],
      },
    ],
  }

  bottleneckChart.setOption(option)
}

const initUserChart = () => {
  if (!userChartRef.value) return
  userChart = echarts.init(userChartRef.value)

  const option = {
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: ['张三', '李四', '王五', '赵六', '钱七'],
    },
    yAxis: { type: 'value', name: '小时' },
    series: [
      {
        name: '平均处理时间',
        type: 'bar',
        data: [0.8, 1.2, 1.5, 0.9, 1.0],
      },
    ],
  }

  userChart.setOption(option)
}

const handleResize = () => {
  trendChart?.resize()
  barChart?.resize()
  bottleneckChart?.resize()
  userChart?.resize()
}

const handleSearch = () => {
  MessagePlugin.success('分析成功')
}

const handleExport = () => {
  MessagePlugin.success('导出成功')
}

onMounted(() => {
  initTrendChart()
  initBarChart()
  initBottleneckChart()
  initUserChart()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  trendChart?.dispose()
  barChart?.dispose()
  bottleneckChart?.dispose()
  userChart?.dispose()
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped lang="scss">
.performance-analysis {
  .search-bar {
    margin-bottom: 16px;
  }

  .stats-row {
    margin-bottom: 16px;

    .stat-item {
      .stat-label {
        font-size: 14px;
        color: var(--td-text-color-secondary);
        margin-bottom: 8px;
      }

      .stat-value {
        font-size: 24px;
        font-weight: 600;
        color: var(--td-text-color-primary);
        margin-bottom: 4px;

        .unit {
          font-size: 14px;
          font-weight: normal;
          color: var(--td-text-color-secondary);
        }
      }

      .stat-trend {
        font-size: 12px;

        &.up { color: var(--td-success-color); }
        &.down { color: var(--td-error-color); }
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
