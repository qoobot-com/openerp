<template>
  <div class="monitor-dashboard">
    <t-card title="流程监控看板" :bordered="false">
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
                  <span>较昨日</span>
                </div>
              </div>
            </div>
          </t-card>
        </t-col>
      </t-row>

      <!-- 图表区域 -->
      <t-row :gutter="[16, 16]" class="charts-row">
        <t-col :span="16">
          <t-card title="流程趋势" :bordered="false" hover-shadow>
            <div ref="trendChartRef" class="chart"></div>
          </t-card>
        </t-col>
        <t-col :span="8">
          <t-card title="流程分布" :bordered="false" hover-shadow>
            <div ref="pieChartRef" class="chart"></div>
          </t-card>
        </t-col>
      </t-row>

      <!-- 实时流程列表 -->
      <t-row :gutter="[16, 16]" class="list-row">
        <t-col :span="24">
          <t-card title="实时流程" :bordered="false" hover-shadow>
            <t-table
              :data="processList"
              :columns="processColumns"
              row-key="id"
              :pagination="{ pageSize: 5 }"
            >
              <template #status="{ row }">
                <t-tag
                  :theme="{
                    running: 'primary',
                    waiting: 'warning',
                    error: 'danger',
                    success: 'success',
                  }[row.status]"
                >
                  {{ { running: '运行中', waiting: '等待中', error: '异常', success: '完成' }[row.status] }}
                </t-tag>
              </template>
            </t-table>
          </t-card>
        </t-col>
      </t-row>

      <!-- 超时预警 -->
      <t-row :gutter="[16, 16]" class="alert-row">
        <t-col :span="24">
          <t-card title="超时预警" :bordered="false" hover-shadow>
            <t-alert theme="warning" title="以下流程即将超时，请及时处理">
              <t-table
                :data="alertList"
                :columns="alertColumns"
                row-key="id"
                :pagination="{ pageSize: 5 }"
              >
                <template #urgency="{ row }">
                  <t-tag
                    :theme="{
                      high: 'danger',
                      medium: 'warning',
                    }[row.urgency]"
                  >
                    {{ { high: '紧急', medium: '一般' }[row.urgency] }}
                  </t-tag>
                </template>
              </t-table>
            </t-alert>
          </t-card>
        </t-col>
      </t-row>
    </t-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import {
  UsergroupIcon, TimeIcon, CheckCircleIcon, ErrorCircleIcon, ChevronUpIcon, ChevronDownIcon,
} from 'tdesign-icons-vue-next'

const stats = [
  { title: '运行中流程', value: '156', icon: UsergroupIcon, color: '#e7f4ff', trend: 12.5 },
  { title: '待办任务', value: '324', icon: TimeIcon, color: '#fff0e7', trend: 8.2 },
  { title: '今日完成', value: '89', icon: CheckCircleIcon, color: '#e8f7ed', trend: 15.3 },
  { title: '异常流程', value: '5', icon: ErrorCircleIcon, color: '#fef0e7', trend: -2.5 },
]

const processList = ref([
  {
    id: 1,
    processName: '请假审批流程',
    instanceNo: 'INS202602190001',
    currentNode: '部门审批',
    initiator: '张三',
    status: 'running',
    startTime: '2026-02-19 09:30:00',
  },
  {
    id: 2,
    processName: '报销审批流程',
    instanceNo: 'INS202602190002',
    currentNode: '财务审批',
    initiator: '李四',
    status: 'waiting',
    startTime: '2026-02-19 10:20:00',
  },
  {
    id: 3,
    processName: '采购审批流程',
    instanceNo: 'INS202602190003',
    currentNode: '部门审批',
    initiator: '王五',
    status: 'running',
    startTime: '2026-02-19 11:00:00',
  },
])

const processColumns = [
  { colKey: 'instanceNo', title: '实例编号' },
  { colKey: 'processName', title: '流程名称' },
  { colKey: 'initiator', title: '发起人' },
  { colKey: 'currentNode', title: '当前节点' },
  { colKey: 'status', title: '状态' },
  { colKey: 'startTime', title: '开始时间' },
]

const alertList = ref([
  {
    id: 1,
    processName: '请假审批流程',
    instanceNo: 'INS202602180001',
    currentNode: '财务审批',
    initiator: '张三',
    deadline: '2026-02-19 17:00:00',
    remaining: '30分钟',
    urgency: 'high',
  },
  {
    id: 2,
    processName: '报销审批流程',
    instanceNo: 'INS202602180002',
    currentNode: '部门审批',
    initiator: '李四',
    deadline: '2026-02-20 10:00:00',
    remaining: '17小时',
    urgency: 'medium',
  },
])

const alertColumns = [
  { colKey: 'instanceNo', title: '实例编号' },
  { colKey: 'processName', title: '流程名称' },
  { colKey: 'initiator', title: '发起人' },
  { colKey: 'currentNode', title: '当前节点' },
  { colKey: 'deadline', title: '截止时间' },
  { colKey: 'remaining', title: '剩余时间' },
  { colKey: 'urgency', title: '紧急程度' },
]

const trendChartRef = ref<HTMLElement>()
const pieChartRef = ref<HTMLElement>()
let trendChart: echarts.ECharts | null = null
let pieChart: echarts.ECharts | null = null

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
          { value: 156, name: '审批流程' },
          { value: 89, name: '业务流程' },
          { value: 67, name: '系统流程' },
          { value: 45, name: '其他流程' },
        ],
      },
    ],
  }

  pieChart.setOption(option)
}

const handleResize = () => {
  trendChart?.resize()
  pieChart?.resize()
}

onMounted(() => {
  initTrendChart()
  initPieChart()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  trendChart?.dispose()
  pieChart?.dispose()
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped lang="scss">
.monitor-dashboard {
  .stats-row {
    margin-bottom: 16px;
  }

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
          font-size: 24px;
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

  .charts-row,
  .list-row,
  .alert-row {
    margin-bottom: 16px;

    .chart {
      height: 300px;
    }
  }
}
</style>
