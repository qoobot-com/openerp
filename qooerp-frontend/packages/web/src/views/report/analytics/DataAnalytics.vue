<template>
  <div class="data-analytics">
    <t-card title="数据分析" :bordered="false">
      <t-tabs v-model="activeTab">
        <t-tab-panel value="trend" label="趋势分析">
          <div class="analytics-section">
            <div class="date-selector">
              <t-radio-group v-model="timeRange" variant="default-filled">
                <t-radio-button value="week">近7天</t-radio-button>
                <t-radio-button value="month">近30天</t-radio-button>
                <t-radio-button value="quarter">近90天</t-radio-button>
                <t-radio-button value="year">近一年</t-radio-button>
              </t-radio-group>
              <t-date-range-picker v-model="customDate" placeholder="自定义范围" style="margin-left: 12px" />
            </div>

            <t-row :gutter="[16, 16]" class="chart-row">
              <t-col :span="24">
                <t-card title="销售趋势" :bordered="false" hover-shadow>
                  <div ref="trendChartRef" class="chart"></div>
                </t-card>
              </t-col>
            </t-row>

            <t-row :gutter="[16, 16]" class="chart-row">
              <t-col :span="12">
                <t-card title="增长趋势" :bordered="false" hover-shadow>
                  <div ref="growthChartRef" class="chart"></div>
                </t-card>
              </t-col>
              <t-col :span="12">
                <t-card title="占比趋势" :bordered="false" hover-shadow>
                  <div ref="stackChartRef" class="chart"></div>
                </t-card>
              </t-col>
            </t-row>
          </div>
        </t-tab-panel>

        <t-tab-panel value="compare" label="对比分析">
          <div class="analytics-section">
            <div class="compare-controls">
              <t-space>
                <t-select v-model="comparePeriod" placeholder="对比周期" style="width: 150px">
                  <t-option value="yoy" label="同比" />
                  <t-option value="mom" label="环比" />
                  <t-option value="wow" label="周同比" />
                </t-select>
                <t-date-range-picker v-model="compareDate1" placeholder="周期1" />
                <span>vs</span>
                <t-date-range-picker v-model="compareDate2" placeholder="周期2" />
                <t-button theme="primary" @click="handleCompare">对比</t-button>
              </t-space>
            </div>

            <t-row :gutter="[16, 16]" class="chart-row">
              <t-col :span="24">
                <t-card title="数据对比" :bordered="false" hover-shadow>
                  <div ref="compareChartRef" class="chart"></div>
                </t-card>
              </t-col>
            </t-row>
          </div>
        </t-tab-panel>

        <t-tab-panel value="funnel" label="漏斗分析">
          <div class="analytics-section">
            <t-row :gutter="[16, 16]" class="chart-row">
              <t-col :span="24">
                <t-card title="转化漏斗" :bordered="false" hover-shadow>
                  <div ref="funnelChartRef" class="chart"></div>
                </t-card>
              </t-col>
            </t-row>

            <t-row :gutter="[16, 16]" class="table-row">
              <t-col :span="24">
                <t-card title="漏斗数据" :bordered="false" hover-shadow>
                  <t-table
                    :data="funnelData"
                    :columns="funnelColumns"
                    row-key="id"
                  />
                </t-card>
              </t-col>
            </t-row>
          </div>
        </t-tab-panel>

        <t-tab-panel value="correlation" label="关联分析">
          <div class="analytics-section">
            <t-row :gutter="[16, 16]" class="chart-row">
              <t-col :span="12">
                <t-card title="相关性矩阵" :bordered="false" hover-shadow>
                  <div ref="correlationChartRef" class="chart"></div>
                </t-card>
              </t-col>
              <t-col :span="12">
                <t-card title="散点分布" :bordered="false" hover-shadow>
                  <div ref="scatterChartRef" class="chart"></div>
                </t-card>
              </t-col>
            </t-row>
          </div>
        </t-tab-panel>
      </t-tabs>
    </t-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { MessagePlugin } from 'tdesign-vue-next'
import * as echarts from 'echarts'

const activeTab = ref('trend')
const timeRange = ref('month')
const customDate = ref([])
const comparePeriod = ref('yoy')
const compareDate1 = ref([])
const compareDate2 = ref([])

const funnelData = ref([
  { id: 1, stage: '浏览商品', users: 10000, rate: '100%', conversion: '-' },
  { id: 2, stage: '加入购物车', users: 6500, rate: '65%', conversion: '-35%' },
  { id: 3, stage: '提交订单', users: 3200, rate: '32%', conversion: '-50.8%' },
  { id: 4, stage: '支付成功', users: 2800, rate: '28%', conversion: '-12.5%' },
])

const funnelColumns = [
  { colKey: 'stage', title: '漏斗阶段' },
  { colKey: 'users', title: '用户数', width: 120 },
  { colKey: 'rate', title: '转化率', width: 120 },
  { colKey: 'conversion', title: '转化流失', width: 120 },
]

const trendChartRef = ref<HTMLElement>()
const growthChartRef = ref<HTMLElement>()
const stackChartRef = ref<HTMLElement>()
const compareChartRef = ref<HTMLElement>()
const funnelChartRef = ref<HTMLElement>()
const correlationChartRef = ref<HTMLElement>()
const scatterChartRef = ref<HTMLElement>()

let trendChart: echarts.ECharts | null = null
let growthChart: echarts.ECharts | null = null
let stackChart: echarts.ECharts | null = null
let compareChart: echarts.ECharts | null = null
let funnelChart: echarts.ECharts | null = null
let correlationChart: echarts.ECharts | null = null
let scatterChart: echarts.ECharts | null = null

const initTrendChart = () => {
  if (!trendChartRef.value) return
  trendChart = echarts.init(trendChartRef.value)

  const option = {
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月'],
    },
    yAxis: { type: 'value', name: '销售额' },
    series: [
      {
        name: '销售额',
        type: 'line',
        smooth: true,
        data: [320000, 350000, 280000, 420000, 380000, 450000, 520000, 480000, 560000, 620000, 680000, 750000],
      },
    ],
  }

  trendChart.setOption(option)
}

const initGrowthChart = () => {
  if (!growthChartRef.value) return
  growthChart = echarts.init(growthChartRef.value)

  const option = {
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: ['1月', '2月', '3月', '4月', '5月', '6月'],
    },
    yAxis: { type: 'value', name: '增长率(%)' },
    series: [
      {
        name: '同比增长率',
        type: 'line',
        smooth: true,
        data: [12.5, 15.3, -5.2, 18.5, 8.2, 22.3],
      },
    ],
  }

  growthChart.setOption(option)
}

const initStackChart = () => {
  if (!stackChartRef.value) return
  stackChart = echarts.init(stackChartRef.value)

  const option = {
    tooltip: { trigger: 'axis' },
    legend: { data: ['产品A', '产品B', '产品C'] },
    xAxis: {
      type: 'category',
      data: ['1月', '2月', '3月', '4月', '5月', '6月'],
    },
    yAxis: { type: 'value', name: '销售额' },
    series: [
      {
        name: '产品A',
        type: 'bar',
        stack: 'total',
        data: [150000, 180000, 120000, 210000, 190000, 230000],
      },
      {
        name: '产品B',
        type: 'bar',
        stack: 'total',
        data: [100000, 110000, 90000, 130000, 120000, 150000],
      },
      {
        name: '产品C',
        type: 'bar',
        stack: 'total',
        data: [70000, 60000, 70000, 80000, 70000, 90000],
      },
    ],
  }

  stackChart.setOption(option)
}

const initCompareChart = () => {
  if (!compareChartRef.value) return
  compareChart = echarts.init(compareChartRef.value)

  const option = {
    tooltip: { trigger: 'axis' },
    legend: { data: ['本期', '同期'] },
    xAxis: {
      type: 'category',
      data: ['1月', '2月', '3月', '4月', '5月', '6月'],
    },
    yAxis: { type: 'value', name: '销售额' },
    series: [
      {
        name: '本期',
        type: 'line',
        smooth: true,
        data: [320000, 350000, 280000, 420000, 380000, 450000],
      },
      {
        name: '同期',
        type: 'line',
        smooth: true,
        data: [280000, 300000, 250000, 360000, 320000, 380000],
      },
    ],
  }

  compareChart.setOption(option)
}

const initFunnelChart = () => {
  if (!funnelChartRef.value) return
  funnelChart = echarts.init(funnelChartRef.value)

  const option = {
    tooltip: { trigger: 'item' },
    series: [
      {
        name: '漏斗分析',
        type: 'funnel',
        left: '10%',
        top: 60,
        bottom: 60,
        width: '80%',
        min: 0,
        max: 100,
        minSize: '0%',
        maxSize: '100%',
        sort: 'descending',
        gap: 2,
        label: { show: true, position: 'inside' },
        labelLine: { length: 10, lineStyle: { width: 1, type: 'solid' } },
        itemStyle: { borderColor: '#fff', borderWidth: 1 },
        emphasis: { label: { fontSize: 20 } },
        data: [
          { value: 100, name: '浏览商品' },
          { value: 65, name: '加入购物车' },
          { value: 32, name: '提交订单' },
          { value: 28, name: '支付成功' },
        ],
      },
    ],
  }

  funnelChart.setOption(option)
}

const initCorrelationChart = () => {
  if (!correlationChartRef.value) return
  correlationChart = echarts.init(correlationChartRef.value)

  const option = {
    tooltip: { position: 'top' },
    grid: { top: '10%', bottom: '10%', left: '10%', right: '10%' },
    xAxis: { type: 'category', data: ['销售额', '订单量', '客单价', '复购率'] },
    yAxis: { type: 'category', data: ['销售额', '订单量', '客单价', '复购 rate'] },
    visualMap: { min: -1, max: 1, calculable: true, orient: 'horizontal', left: 'center', bottom: '0%' },
    series: [
      {
        name: '相关性',
        type: 'heatmap',
        data: [
          [0, 0, 1], [0, 1, 0.8], [0, 2, 0.6], [0, 3, 0.5],
          [1, 0, 0.8], [1, 1, 1], [1, 2, 0.4], [1, 3, 0.3],
          [2, 0, 0.6], [2, 1, 0.4], [2, 2, 1], [2, 3, 0.2],
          [3, 0, 0.5], [3, 1, 0.3], [3, 2, 0.2], [3, 3, 1],
        ],
        label: { show: true },
        emphasis: { itemStyle: { shadowBlur: 10, shadowColor: 'rgba(0, 0, 0, 0.5)' } },
      },
    ],
  }

  correlationChart.setOption(option)
}

const initScatterChart = () => {
  if (!scatterChartRef.value) return
  scatterChart = echarts.init(scatterChartRef.value)

  const option = {
    tooltip: { trigger: 'item' },
    xAxis: { type: 'value', name: '订单量' },
    yAxis: { type: 'value', name: '销售额' },
    series: [
      {
        name: '客户分布',
        type: 'scatter',
        symbolSize: 10,
        data: Array.from({ length: 50 }, () => [
          Math.floor(Math.random() * 100) + 10,
          Math.floor(Math.random() * 1000) + 100,
        ]),
      },
    ],
  }

  scatterChart.setOption(option)
}

const handleResize = () => {
  trendChart?.resize()
  growthChart?.resize()
  stackChart?.resize()
  compareChart?.resize()
  funnelChart?.resize()
  correlationChart?.resize()
  scatterChart?.resize()
}

const handleCompare = () => {
  MessagePlugin.success('对比分析')
}

onMounted(() => {
  initTrendChart()
  initGrowthChart()
  initStackChart()
  initCompareChart()
  initFunnelChart()
  initCorrelationChart()
  initScatterChart()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  trendChart?.dispose()
  growthChart?.dispose()
  stackChart?.dispose()
  compareChart?.dispose()
  funnelChart?.dispose()
  correlationChart?.dispose()
  scatterChart?.dispose()
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped lang="scss">
.data-analytics {
  .analytics-section {
    padding: 20px 0;

    .date-selector,
    .compare-controls {
      margin-bottom: 16px;
      display: flex;
      align-items: center;
    }

    .chart-row,
    .table-row {
      margin-bottom: 16px;

      .chart {
        height: 400px;
      }
    }
  }
}
</style>
