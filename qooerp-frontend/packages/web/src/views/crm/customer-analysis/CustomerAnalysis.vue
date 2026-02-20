<template>
  <div class="customer-analysis-container">
    <!-- 统计卡片 -->
    <t-row :gutter="16" class="mb-4">
      <t-col :span="6">
        <t-card theme="primary">
          <t-statistic title="客户总数" :value="statistics.total" :loading="loading">
            <template #prefix><icon-user /></template>
          </t-statistic>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="success">
          <t-statistic title="活跃客户" :value="statistics.active" :loading="loading">
            <template #prefix><icon-check-circle /></template>
          </t-statistic>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="warning">
          <t-statistic title="潜在客户" :value="statistics.potential" :loading="loading">
            <template #prefix><icon-time /></template>
          </t-statistic>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="danger">
          <t-statistic title="流失客户" :value="statistics.lost" :loading="loading">
            <template #prefix><icon-close-circle /></template>
          </t-statistic>
        </t-card>
      </t-col>
    </t-row>

    <!-- 搜索条件 -->
    <t-card class="mb-4">
      <t-form :data="searchForm" layout="inline" @submit="handleSearch">
        <t-form-item label="分析周期" name="period">
          <t-select v-model="searchForm.period" placeholder="请选择分析周期">
            <t-option value="week" label="本周" />
            <t-option value="month" label="本月" />
            <t-option value="quarter" label="本季度" />
            <t-option value="year" label="本年" />
          </t-select>
        </t-form-item>
        <t-form-item label="客户类型" name="customerType">
          <t-select v-model="searchForm.customerType" placeholder="请选择客户类型" clearable>
            <t-option value="企业客户" label="企业客户" />
            <t-option value="个人客户" label="个人客户" />
            <t-option value="政府客户" label="政府客户" />
          </t-select>
        </t-form-item>
        <t-form-item label="销售负责人" name="salesPerson">
          <t-input v-model="searchForm.salesPerson" placeholder="请输入销售负责人" clearable />
        </t-form-item>
        <t-form-item>
          <t-button theme="primary" type="submit">查询</t-button>
          <t-button theme="default" @click="handleReset">重置</t-button>
          <t-button theme="default" @click="handleExport">
            <template #icon><icon-download /></template>
            导出报表
          </t-button>
        </t-form-item>
      </t-form>
    </t-card>

    <!-- 分析图表 -->
    <t-row :gutter="16" class="mb-4">
      <!-- 客户趋势分析 -->
      <t-col :span="12">
        <t-card title="客户增长趋势" header-bordered>
          <div class="chart-placeholder" style="height: 300px; display: flex; align-items: center; justify-content: center; background: #f5f5f5;">
            <span>客户增长趋势图表 (折线图)</span>
          </div>
        </t-card>
      </t-col>
      <!-- 客户类型分布 -->
      <t-col :span="12">
        <t-card title="客户类型分布" header-bordered>
          <div class="chart-placeholder" style="height: 300px; display: flex; align-items: center; justify-content: center; background: #f5f5f5;">
            <span>客户类型分布图表 (饼图)</span>
          </div>
        </t-card>
      </t-col>
    </t-row>

    <t-row :gutter="16" class="mb-4">
      <!-- 客户价值分析 -->
      <t-col :span="12">
        <t-card title="客户价值分析" header-bordered>
          <div class="chart-placeholder" style="height: 300px; display: flex; align-items: center; justify-content: center; background: #f5f5f5;">
            <span>客户价值分析图表 (柱状图)</span>
          </div>
        </t-card>
      </t-col>
      <!-- 客户地区分布 -->
      <t-col :span="12">
        <t-card title="客户地区分布" header-bordered>
          <div class="chart-placeholder" style="height: 300px; display: flex; align-items: center; justify-content: center; background: #f5f5f5;">
            <span>客户地区分布图表 (地图)</span>
          </div>
        </t-card>
      </t-col>
    </t-row>

    <!-- 分析指标 -->
    <t-card title="关键指标分析" class="mb-4">
      <t-row :gutter="16">
        <t-col :span="6">
          <div class="metric-item">
            <div class="metric-label">客户增长率</div>
            <div class="metric-value">+15.2%</div>
            <t-progress :percentage="65" size="small" />
          </div>
        </t-col>
        <t-col :span="6">
          <div class="metric-item">
            <div class="metric-label">客户转化率</div>
            <div class="metric-value">32.5%</div>
            <t-progress :percentage="32" size="small" />
          </div>
        </t-col>
        <t-col :span="6">
          <div class="metric-item">
            <div class="metric-label">客户留存率</div>
            <div class="metric-value">89.3%</div>
            <t-progress :percentage="89" size="small" />
          </div>
        </t-col>
        <t-col :span="6">
          <div class="metric-item">
            <div class="metric-label">平均客单价</div>
            <div class="metric-value">¥45,280</div>
            <t-progress :percentage="78" size="small" />
          </div>
        </t-col>
      </t-row>
    </t-card>

    <!-- 客户排名列表 -->
    <t-card title="客户TOP 10排名">
      <t-table
        :data="topCustomers"
        :columns="topColumns"
        :loading="loading"
        :pagination="false"
      >
        <template #trend="{ row }">
          <span v-if="row.trend > 0" style="color: #00a870;">▲ {{ row.trend }}%</span>
          <span v-else-if="row.trend < 0" style="color: #e34d59;">▼ {{ Math.abs(row.trend) }}%</span>
          <span v-else style="color: #999;">- 0%</span>
        </template>
      </t-table>
    </t-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { MessagePlugin } from 'tdesign-vue-next';
import {
  IconUser, IconCheckCircle, IconTime, IconCloseCircle, IconDownload
} from 'tdesign-icons-vue-next';

const loading = ref(false);

const statistics = reactive({
  total: 156,
  active: 89,
  potential: 45,
  lost: 22
});

const searchForm = reactive({
  period: 'month',
  customerType: '',
  salesPerson: ''
});

const topCustomers = ref([
  {
    id: 1,
    customerCode: 'C202600001',
    customerName: '上海科技有限公司',
    salesPerson: '张三',
    totalAmount: 1250000,
    orderCount: 45,
    trend: 15.2,
    lastOrderDate: '2026-02-18'
  },
  {
    id: 2,
    customerCode: 'C202600002',
    customerName: '北京贸易公司',
    salesPerson: '李四',
    totalAmount: 980000,
    orderCount: 38,
    trend: 8.5,
    lastOrderDate: '20xx-xx-xx'
  },
  {
    id: 3,
    customerCode: 'C202600003',
    customerName: '深圳电子科技',
    salesPerson: '王五',
    totalAmount: 850000,
    orderCount: 32,
    trend: -2.3,
    lastOrderDate: '2026-02-15'
  },
  {
    id: 4,
    customerCode: 'C202600004',
    customerName: '广州制造集团',
    salesPerson: '赵六',
    totalAmount: 720000,
    orderCount: 28,
    trend: 12.8,
    lastOrderDate: '2026-02-16'
  },
  {
    id: 5,
    customerCode: 'C202600005',
    customerName: '杭州软件公司',
    salesPerson: '张三',
    totalAmount: 650000,
    orderCount: 25,
    trend: 5.6,
    lastOrderDate: '2026-02-14'
  },
  {
    id: 6,
    customerCode: 'C202600006',
    customerName: '成都物流企业',
    salesPerson: '李四',
    totalAmount: 580000,
    orderCount: 22,
    trend: 0,
    lastOrderDate: '2026-02-13'
  },
  {
    id: 7,
    customerCode: 'C202600007',
    customerName: '武汉零售连锁',
    salesPerson: '王五',
    totalAmount: 520000,
    orderCount: 20,
    trend: -5.8,
    lastOrderDate: '2026-02-12'
  },
  {
    id: 8,
    customerCode: 'C202600008',
    customerName: '南京化工集团',
    salesPerson: '赵六',
    totalAmount: 480000,
    orderCount: 18,
    trend: 3.2,
    lastOrderDate: '2026-02-11'
  },
  {
    id: 9,
    customerCode: 'C202600009',
    customerName: '西安建筑公司',
    salesPerson: '张三',
    totalAmount: 420000,
    orderCount: 16,
    trend: 8.9,
    lastOrderDate: '2026-02-10'
  },
  {
    id: 10,
    customerCode: 'C202600010',
    customerName: '重庆汽车制造',
    salesPerson: '李四',
    totalAmount: 380000,
    orderCount: 15,
    trend: 1.5,
    lastOrderDate: '2026-02-09'
  }
]);

const topColumns = [
  { colKey: 'customerCode', title: '客户编号', width: 130 },
  { colKey: 'customerName', title: '客户名称', width: 150 },
  { colKey: 'salesPerson', title: '销售负责人', width: 100 },
  { colKey: 'totalAmount', title: '累计金额', width: 120 },
  { colKey: 'orderCount', title: '订单数量', width: 100 },
  { colKey: 'trend', title: '增长趋势', width: 100 },
  { colKey: 'lastOrderDate', title: '最后下单日期', width: 120 }
];

const handleSearch = () => {
  MessagePlugin.success('查询成功');
};

const handleReset = () => {
  Object.assign(searchForm, {
    period: 'month',
    customerType: '',
    salesPerson: ''
  });
};

const handleExport = () => {
  MessagePlugin.success('报表导出成功');
};

onMounted(() => {
  console.log('CustomerAnalysis mounted');
});
</script>

<style scoped lang="less">
.customer-analysis-container {
  padding: 20px;
}

.mb-4 {
  margin-bottom: 16px;
}

.metric-item {
  padding: 16px;
  background: #f5f5f5;
  border-radius: 4px;
  text-align: center;

  .metric-label {
    font-size: 14px;
    color: #666;
    margin-bottom: 8px;
  }

  .metric-value {
    font-size: 24px;
    font-weight: bold;
    color: #0052d9;
    margin-bottom: 12px;
  }
}
</style>
