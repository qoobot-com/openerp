<template>
  <div class="customer-lifecycle-container">
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
          <t-statistic title="流失客户" :value="statistics.churned" :loading="loading">
            <template #prefix><icon-close-circle /></template>
          </t-statistic>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="default">
          <t-statistic title="流失率" :value="statistics.churnRate" suffix="%" :loading="loading">
            <template #prefix><icon-chart-pie /></template>
          </t-statistic>
        </t-card>
      </t-col>
    </t-row>

    <!-- 搜索条件 -->
    <t-card class="mb-4">
      <t-form :data="searchForm" layout="inline" @submit="handleSearch">
        <t-form-item label="分析周期" name="period">
          <t-date-range-picker v-model="searchForm.period" placeholder="选择分析周期" />
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
        </t-form-item>
      </t-form>
    </t-card>

    <!-- 分析图表 -->
    <t-row :gutter="16" class="mb-4">
      <t-col :span="12">
        <t-card title="客户生命周期分布">
          <div class="chart-container" ref="funnelChartRef">
            <div class="funnel-container">
              <div v-for="(item, index) in funnelData" :key="index" class="funnel-item" :style="{ width: item.width + '%' }">
                <span class="funnel-label">{{ item.label }}</span>
                <span class="funnel-value">{{ item.value }}</span>
              </div>
            </div>
          </div>
        </t-card>
      </t-col>
      <t-col :span="12">
        <t-card title="客户生命周期转化趋势">
          <div class="chart-container" ref="trendChartRef">
            <div class="trend-placeholder">趋势图表区域</div>
          </div>
        </t-card>
      </t-col>
    </t-row>

    <!-- 关键指标 -->
    <t-card title="关键指标分析" class="mb-4">
      <t-row :gutter="16">
        <t-col :span="6">
          <div class="indicator-item">
            <div class="indicator-title">平均获客周期</div>
            <div class="indicator-value">45 天</div>
            <div class="indicator-trend up">
              <icon-arrow-down />
              <span>较上月减少 3 天</span>
            </div>
          </div>
        </t-col>
        <t-col :span="6">
          <div class="indicator-item">
            <div class="indicator-title">客户生命周期价值</div>
            <div class="indicator-value">¥128,500</div>
            <div class="indicator-trend up">
              <icon-arrow-up />
              <span>较上月增长 8%</span>
            </div>
          </div>
        </t-col>
        <t-col :span="6">
          <div class="indicator-item">
            <div class="indicator-title">平均存续时间</div>
            <div class="indicator-value">18 个月</div>
            <div class="indicator-trend up">
              <icon-arrow-up />
              <span>较上月增加 2 个月</span>
            </div>
          </div>
        </t-col>
        <t-col :span="6">
          <div class="indicator-item">
            <div class="indicator-title">留存率</div>
            <div class="indicator-value">85.6%</div>
            <div class="indicator-trend down">
              <icon-arrow-down />
              <span>较上月下降 1.2%</span>
            </div>
          </div>
        </t-col>
      </t-row>
    </t-card>

    <!-- 客户生命周期列表 -->
    <t-card title="客户生命周期明细">
      <t-table
        :data="tableData"
        :columns="columns"
        :loading="loading"
        :pagination="pagination"
        row-key="id"
        @page-change="handlePageChange"
      >
        <template #stage="{ row }">
          <t-tag v-if="row.stage === 'potential'" theme="default" variant="light">潜在</t-tag>
          <t-tag v-else-if="row.stage === 'new'" theme="primary" variant="light">新客户</t-tag>
          <t-tag v-else-if="row.stage === 'active'" theme="success" variant="light">活跃</t-tag>
          <t-tag v-else-if="row.stage === 'at_risk'" theme="warning" variant="light">风险</t-tag>
          <t-tag v-else theme="danger" variant="light">流失</t-tag>
        </template>
        <template #operation="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleView(row)">查看</t-link>
            <t-link theme="success" @click="handleNurture(row)">培育</t-link>
          </t-space>
        </template>
      </t-table>
    </t-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, h } from 'vue';
import { MessagePlugin } from 'tdesign-vue-next';
import {
  IconUser, IconCheckCircle, IconCloseCircle, IconChartPie,
  IconArrowUp, IconArrowDown
} from 'tdesign-icons-vue-next';

const loading = ref(false);
const funnelChartRef = ref();
const trendChartRef = ref();

const statistics = reactive({
  total: 156,
  active: 89,
  churned: 12,
  churnRate: 7.69
});

const searchForm = reactive({
  period: [],
  customerType: '',
  salesPerson: ''
});

const pagination = reactive({
  current: 1,
  pageSize: 20,
  total: 156
});

const columns = [
  { colKey: 'customerCode', title: '客户编号', width: 130 },
  { colKey: 'customerName', title: '客户名称', width: 150 },
  { colKey: 'customerType', title: '客户类型', width: 100 },
  { colKey: 'salesPerson', title: '销售负责人', width: 100 },
  { colKey: 'stage', title: '生命周期阶段', width: 100 },
  { colKey: 'acquireDate', title: '获客日期', width: 120 },
  { colKey: 'lifecycleDays', title: '生命周期(天)', width: 120 },
  { colKey: 'totalAmount', title: '累计金额', width: 120 },
  { colKey: 'operation', title: '操作', width: 120 }
];

const funnelData = [
  { label: '潜在客户', value: 45, width: 100 },
  { label: '新客户', value: 32, width: 85 },
  { label: '活跃客户', value: 89, width: 70 },
  { label: '风险客户', value: 15, width: 40 },
  { label: '流失客户', value: 12, width: 25 }
];

const tableData = ref([
  {
    id: 1,
    customerCode: 'C202600001',
    customerName: '上海科技有限公司',
    customerType: '企业客户',
    salesPerson: '张三',
    stage: 'active',
    acquireDate: '2025-08-15',
    lifecycleDays: 189,
    totalAmount: 256800,
    lastOrderDate: '2026-02-15'
  },
  {
    id: 2,
    customerCode: 'C202600002',
    customerName: '北京贸易公司',
    customerType: '企业客户',
    salesPerson: '李四',
    stage: 'active',
    acquireDate: '2025-10-20',
    lifecycleDays: 123,
    totalAmount: 125600,
    lastOrderDate: '2026-02-10'
  },
  {
    id: 3,
    customerCode: 'C202600003',
    customerName: '深圳电子公司',
    customerType: '企业客户',
    salesPerson: '王五',
    stage: 'at_risk',
    acquireDate: '2025-06-01',
    lifecycleDays: 264,
    totalAmount: 98000,
    lastOrderDate: '2025-12-20'
  }
]);

const handleSearch = () => {
  MessagePlugin.success('查询成功');
};

const handleReset = () => {
  Object.assign(searchForm, {
    period: [],
    customerType: '',
    salesPerson: ''
  });
};

const handleView = (row: any) => {
  MessagePlugin.info(`查看客户: ${row.customerName}`);
};

const handleNurture = (row: any) => {
  MessagePlugin.info(`客户培育: ${row.customerName}`);
};

const handlePageChange = (pageInfo: any) => {
  pagination.current = pageInfo.current;
  pagination.pageSize = pageInfo.pageSize;
};

onMounted(() => {
  console.log('CustomerLifecycle mounted');
});
</script>

<style scoped lang="less">
.customer-lifecycle-container {
  padding: 20px;
}

.mb-4 {
  margin-bottom: 16px;
}

.chart-container {
  height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.funnel-container {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  gap: 16px;
  padding: 20px;
}

.funnel-item {
  height: 40px;
  background: linear-gradient(90deg, #0052d9 0%, #266fe8 100%);
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  color: white;
  font-size: 14px;
  transition: all 0.3s;

  &:hover {
    opacity: 0.8;
    transform: scale(1.02);
  }
}

.funnel-label {
  font-weight: 500;
}

.funnel-value {
  font-weight: bold;
}

.trend-placeholder {
  color: #999;
  font-size: 14px;
}

.indicator-item {
  padding: 16px;
  background-color: #f5f5f5;
  border-radius: 8px;

  .indicator-title {
    font-size: 14px;
    color: #666;
    margin-bottom: 8px;
  }

  .indicator-value {
    font-size: 24px;
    font-weight: bold;
    color: #333;
    margin-bottom: 8px;
  }

  .indicator-trend {
    display: flex;
    align-items: center;
    gap: 4px;
    font-size: 12px;

    &.up {
      color: #00a870;
    }

    &.down {
      color: #d54941;
    }
  }
}
</style>
