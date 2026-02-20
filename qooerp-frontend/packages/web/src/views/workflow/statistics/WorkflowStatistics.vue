<template>
  <div class="workflow-statistics">
    <t-card title="工作流统计" :bordered="false">
      <!-- 日期范围 -->
      <div class="date-picker">
        <t-date-range-picker v-model="dateRange" placeholder="请选择日期范围" @change="handleDateChange" />
        <t-button theme="primary" @click="handleExport">导出统计</t-button>
      </div>

      <!-- 总体统计 -->
      <t-row :gutter="[16, 16]" class="overview-row">
        <t-col :span="6">
          <t-card :bordered="false" hover-shadow>
            <div class="overview-item">
              <div class="overview-icon" style="background: #e7f4ff">
                <usergroup-icon />
              </div>
              <div class="overview-content">
                <div class="overview-value">{{ overview.totalProcesses }}</div>
                <div class="overview-label">总流程数</div>
              </div>
            </div>
          </t-card>
        </t-col>
        <t-col :span="6">
          <t-card :bordered="false" hover-shadow>
            <div class="overview-item">
              <div class="overview-icon" style="background: #fff0e7">
                <check-circle-icon />
              </div>
              <div class="overview-content">
                <div class="overview-value">{{ overview.completed }}</div>
                <div class="overview-label">已完成</div>
              </div>
            </div>
          </t-card>
        </t-col>
        <t-col :span="6">
          <t-card :bordered="false" hover-shadow>
            <div class="overview-item">
              <div class="overview-icon" style="background: #e8f7ed">
                <time-icon />
              </div>
              <div class="overview-content">
                <div class="overview-value">{{ overview.running }}</div>
                <div class="overview-label">运行中</div>
              </div>
            </div>
          </t-card>
        </t-col>
        <t-col :span="6">
          <t-card :bordered="false" hover-shadow>
            <div class="overview-item">
              <div class="overview-icon" style="background: #fef0e7">
                <error-circle-icon />
              </div>
              <div class="overview-content">
                <div class="overview-value">{{ overview.terminated }}</div>
                <div class="overview-label">已终止</div>
              </div>
            </div>
          </t-card>
        </t-col>
      </t-row>

      <!-- 图表统计 -->
      <t-row :gutter="[16, 16]" class="charts-row">
        <t-col :span="12">
          <t-card title="流程趋势" :bordered="false" hover-shadow>
            <div ref="trendChartRef" class="chart"></div>
          </t-card>
        </t-col>
        <t-col :span="12">
          <t-card title="流程分布" :bordered="false" hover-shadow>
            <div ref="pieChartRef" class="chart"></div>
          </t-card>
        </t-col>
      </t-row>

      <t-row :gutter="[16, 16]" class="charts-row">
        <t-col :span="12">
          <t-card title="部门统计" :bordered="false" hover-shadow>
            <div ref="deptChartRef" class="chart"></div>
          </t-card>
        </t-col>
        <t-col :span="12">
          <t-card title="人员统计" :bordered="false" hover-shadow>
            <div ref="userChartRef" class="chart"></div>
          </t-card>
        </t-col>
      </t-row>

      <!-- 详细统计表格 -->
      <t-row :gutter="[16, 16]" class="table-row">
        <t-col :span="24">
          <t-card title="流程统计明细" :bordered="false" hover-shadow>
            <t-table
              :data="detailData"
              :columns="detailColumns"
              row-key="id"
              :pagination="{ pageSize: 10 }"
            >
              <template #action="{ row }">
                <t-link theme="primary" @click="handleView(row)">查看详情</t-link>
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
import {
  UsergroupIcon, CheckCircleIcon, TimeIcon, ErrorCircleIcon,
} from 'tdesign-icons-vue-next'
import * as echarts from 'echarts'

const dateRange = ref([])

const overview = reactive({
  totalProcesses: 1256,
  completed: 1089,
  running: 156,
  terminated: 11,
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
    completionRate: '89.8%',
  },
  {
    id: 2,
    processName: '报销审批流程',
    instances: 189,
    completed: 178,
    running: 11,
    terminated: 0,
    avgDuration: '5.2小时',
    completionRate: '94.2%',
  },
  {
    id: 3,
    processName: '采购审批流程',
    instances: 145,
    completed: 135,
    running: 10,
    terminated: 0,
    avgDuration: '4.8小时',
    completionRate: '93.1%',
  },
])

const detailColumns = [
  { colKey: 'processName', title: '流程名称' },
  { colKey: 'instances', title: '总实例数', width: 100 },
  { colKey: 'completed', title: '已完成', width: 100 },
  { colKey: 'running', title: '运行中', width: 100 },
  { colKey: 'terminated', title: '已终止', width: 100 },
  { colKey: 'avgDuration', title: '平均耗时', width: 120 },
  { colKey: 'completionRate', title: '完成率', width: 120 },
  { colKey: 'action', title: '操作', width: 100 },
]

const trendChartRef = ref<HTMLElement>()
const pieChartRef = ref<HTMLElement>()
const deptChartRef = ref<HTMLElement>()
const userChartRef = ref<HTMLElement>()

let trendChart: echarts.ECharts | null = null
let pieChart: echarts.ECharts | null = null
let deptChart: echarts.ECharts | null = null
let userChart: echarts.ECharts | null = null

const initTrendChart = () => {
  if (!trendChartRef.value) return
  trendChart = echarts.init(trendChartRef.value)

  const option = {
    tooltip: { trigger: 'axis' },
    legend: { data: ['发起数', '完成数'] },
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
      {
        name: '完成数',
        type: 'line',
        smooth: true,
        data: [42, 48, 35, 60, 45, 23, 28],
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

const initDeptChart = () => {
  if (!deptChartRef.value) return
  deptChart = echarts.init(deptChartRef.value)

  const option = {
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: ['技术部', '市场部', '销售部', '财务部', '行政部'],
    },
    yAxis: { type: 'value' },
    series: [
      {
        name: '流程数量',
        type: 'bar',
        data: [256, 189, 145, 98, 67],
      },
    ],
  }

  deptChart.setOption(option)
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
    yAxis: { type: 'value' },
    series: [
      {
        name: '处理任务数',
        type: 'bar',
        data: [89, 76, 65, 54, 45],
      },
    ],
  }

  userChart.setOption(option)
}

const handleResize = () => {
  trendChart?.resize()
  pieChart?.resize()
  deptChart?.resize()
  userChart?.resize()
}

const handleDateChange = () => {
  console.log('Date changed')
}

const handleExport = () => {
  MessagePlugin.success('导出统计成功')
}

const handleView = (row: any) => {
  MessagePlugin.info(`查看详情: ${row.processName}`)
}

onMounted(() => {
  initTrendChart()
  initPieChart()
  initDeptChart()
  initUserChart()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  trendChart?.dispose()
  pieChart?.dispose()
  deptChart?.dispose()
  userChart?.dispose()
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped lang="scss">
.workflow-statistics {
  .date-picker {
    display: flex;
    gap: 12px;
    margin-bottom: 16px;
  }

  .overview-row {
    margin-bottom: 16px;

    .overview-item {
      display: flex;
      align-items: center;
      gap: 16px;

      .overview-icon {
        width: 56px;
        height: 56px;
        border-radius: 12px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 28px;
        color: var(--td-brand-color);
      }

      .overview-content {
        .overview-value {
          font-size: 24px;
          font-weight: 600;
          color: var(--td-text-color-primary);
          margin-bottom: 4px;
        }

        .overview-label {
          font-size: 14px;
          color: var(--td-text-color-secondary);
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
