<template>
  <div class="dashboard">
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
                <span>较上周</span>
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
          <div ref="salesChartRef" class="chart"></div>
        </t-card>
      </t-col>
      <t-col :span="8">
        <t-card title="数据分布" :bordered="false" hover-shadow>
          <div ref="pieChartRef" class="chart"></div>
        </t-card>
      </t-col>
    </t-row>

    <!-- 待办事项 -->
    <t-row :gutter="[16, 16]" class="todo-row">
      <t-col :span="12">
        <t-card title="待办事项" :bordered="false" hover-shadow>
          <t-list :split="true">
            <t-list-item v-for="item in todoList" :key="item.id">
              <template #action>
                <t-tag :theme="item.priority === 'high' ? 'danger' : item.priority === 'medium' ? 'warning' : 'default'">
                  {{ item.priority === 'high' ? '高' : item.priority === 'medium' ? '中' : '低' }}
                </t-tag>
              </template>
              <div class="todo-content">
                <div class="todo-title">{{ item.title }}</div>
                <div class="todo-time">{{ item.time }}</div>
              </div>
            </t-list-item>
          </t-list>
        </t-card>
      </t-col>
      <t-col :span="12">
        <t-card title="最近操作" :bordered="false" hover-shadow>
          <t-timeline>
            <t-timeline-item v-for="item in activityList" :key="item.id">
              <template #dot>
                <component :is="item.icon" />
              </template>
              <div class="activity-content">
                <div class="activity-title">{{ item.title }}</div>
                <div class="activity-time">{{ item.time }}</div>
              </div>
            </t-timeline-item>
          </t-timeline>
        </t-card>
      </t-col>
    </t-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'

// 引入 TDesign 图标
import * as icons from 'tdesign-icons-vue-next'

const {
  UsergroupIcon,
  UserIcon,
  ShoppingCartIcon,
  MoneyCircleIcon,
  ChevronUpIcon,
  ChevronDownIcon,
  CheckCircleFilledIcon,
  TimeFilledIcon,
  Edit1Icon,
} = icons

// 统计数据
const stats = [
  { title: '总用户数', value: '12,345', icon: UsergroupIcon, color: '#e7f4ff', trend: 12.5 },
  { title: '今日订单', value: '1,234', icon: ShoppingCartIcon, color: '#fff0e7', trend: 8.2 },
  { title: '在线用户', value: '456', icon: UserIcon, color: '#e8f7ed', trend: -3.5 },
  { title: '总收入', value: '¥234,567', icon: MoneyCircleIcon, color: '#fef0e7', trend: 15.3 },
]

// 待办事项
const todoList = [
  { id: 1, title: '审核新用户注册申请', time: '10分钟前', priority: 'high' },
  { id: 2, title: '处理客户投诉反馈', time: '30分钟前', priority: 'high' },
  { id: 3, title: '更新产品价格信息', time: '1小时前', priority: 'medium' },
  { id: 4, title: '准备周报数据', time: '2小时前', priority: 'low' },
  { id: 5, title: '安排团队会议', time: '3小时前', priority: 'medium' },
]

// 最近操作
const activityList = [
  { id: 1, title: '管理员完成了系统配置更新', time: '5分钟前', icon: CheckCircleFilledIcon },
  { id: 2, title: '张三提交了新的订单申请', time: '15分钟前', icon: TimeFilledIcon },
  { id: 3, title: '李四编辑了用户信息', time: '30分钟前', icon: Edit1Icon },
  { id: 4, title: '王五审核通过了角色权限', time: '1小时前', icon: CheckCircleFilledIcon },
]

// 图表引用
const salesChartRef = ref<HTMLElement>()
const pieChartRef = ref<HTMLElement>()
let salesChart: echarts.ECharts | null = null
let pieChart: echarts.ECharts | null = null

// 初始化销售趋势图
const initSalesChart = () => {
  if (!salesChartRef.value) return

  salesChart = echarts.init(salesChartRef.value)

  const option = {
    tooltip: {
      trigger: 'axis',
    },
    legend: {
      data: ['订单量', '销售额'],
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true,
    },
    xAxis: {
      type: 'category',
      data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
    },
    yAxis: [
      {
        type: 'value',
        name: '订单量',
      },
      {
        type: 'value',
        name: '销售额',
      },
    ],
    series: [
      {
        name: '订单量',
        type: 'line',
        smooth: true,
        data: [120, 132, 101, 134, 90, 230, 210],
      },
      {
        name: '销售额',
        type: 'bar',
        yAxisIndex: 1,
        data: [2200, 1820, 1910, 2340, 2900, 3300, 3100],
      },
    ],
  }

  salesChart.setOption(option)
}

// 初始化饼图
const initPieChart = () => {
  if (!pieChartRef.value) return

  pieChart = echarts.init(pieChartRef.value)

  const option = {
    tooltip: {
      trigger: 'item',
    },
    legend: {
      orient: 'vertical',
      left: 'left',
    },
    series: [
      {
        name: '数据分布',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2,
        },
        label: {
          show: false,
          position: 'center',
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 20,
            fontWeight: 'bold',
          },
        },
        labelLine: {
          show: false,
        },
        data: [
          { value: 1048, name: '已完成' },
          { value: 735, name: '进行中' },
          { value: 580, name: '待处理' },
          { value: 484, name: '已取消' },
        ],
      },
    ],
  }

  pieChart.setOption(option)
}

// 响应式调整
const handleResize = () => {
  salesChart?.resize()
  pieChart?.resize()
}

onMounted(() => {
  initSalesChart()
  initPieChart()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  salesChart?.dispose()
  pieChart?.dispose()
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped lang="scss">
.dashboard {
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

          &.up {
            color: var(--td-success-color);
          }

          &.down {
            color: var(--td-error-color);
          }
        }
      }
    }
  }

  .charts-row {
    margin-bottom: 16px;

    .chart {
      height: 350px;
    }
  }

  .todo-row {
    .todo-content {
      flex: 1;

      .todo-title {
        font-size: 14px;
        color: var(--td-text-color-primary);
        margin-bottom: 4px;
      }

      .todo-time {
        font-size: 12px;
        color: var(--td-text-color-placeholder);
      }
    }

    .activity-content {
      .activity-title {
        font-size: 14px;
        color: var(--td-text-color-primary);
        margin-bottom: 4px;
      }

      .activity-time {
        font-size: 12px;
        color: var(--td-text-color-placeholder);
      }
    }
  }
}
</style>
