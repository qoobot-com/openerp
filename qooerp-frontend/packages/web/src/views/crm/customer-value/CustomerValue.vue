<template>
  <div class="customer-value-container">
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
          <t-statistic title="平均CLV" :value="statistics.avgCLV" :decimal-places="0" :loading="loading">
            <template #prefix><icon-money-circle /></template>
          </t-statistic>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="warning">
          <t-card theme="warning">
            <t-statistic title="高价值客户" :value="statistics.highValue" :loading="loading">
              <template #prefix><icon-star /></template>
            </t-statistic>
          </t-card>
        </t-col>
      </t-col>
      <t-col :span="6">
        <t-card theme="default">
          <t-statistic title="总CLV" :value="statistics.totalCLV" :decimal-places="0" :loading="loading">
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
        <t-form-item label="价值区间" name="valueRange">
          <t-select v-model="searchForm.valueRange" placeholder="请选择价值区间" clearable>
            <t-option value="high" label="高价值 (>10万)" />
            <t-option value="medium" label="中价值 (5-10万)" />
            <t-option value="low" label="低价值 (<5万)" />
          </t-select>
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
        <t-card title="客户价值分布">
          <div class="chart-container" ref="distributionChartRef">
            <div class="chart-placeholder">客户价值分布图表</div>
          </div>
        </t-card>
      </t-col>
      <t-col :span="12">
        <t-card title="客户价值趋势">
          <div class="chart-container" ref="trendChartRef">
            <div class="chart-placeholder">客户价值趋势图表</div>
          </div>
        </t-card>
      </t-col>
    </t-row>

    <!-- TOP 10 客户价值排行榜 -->
    <t-card title="客户价值 TOP 10" class="mb-4">
      <div class="top-list">
        <div v-for="(item, index) in top10Customers" :key="item.id" class="top-item">
          <div class="top-rank" :class="`rank-${index + 1}`">{{ index + 1 }}</div>
          <div class="top-info">
            <div class="top-name">{{ item.customerName }}</div>
            <div class="top-type">{{ item.customerType }}</div>
          </div>
          <div class="top-value">¥{{ item.clv.toLocaleString() }}</div>
          <div class="top-trend" :class="item.trend >= 0 ? 'up' : 'down'">
            <icon-arrow-up v-if="item.trend >= 0" />
            <icon-arrow-down v-else />
            <span>{{ Math.abs(item.trend) }}%</span>
          </div>
        </div>
      </div>
    </t-card>

    <!-- 客户价值明细表格 -->
    <t-card title="客户价值明细">
      <t-table
        :data="tableData"
        :columns="columns"
        :loading="loading"
        :pagination="pagination"
        row-key="id"
        @page-change="handlePageChange"
      >
        <template #valueLevel="{ row }">
          <t-tag v-if="row.valueLevel === 'high'" theme="danger" variant="light">高价值</t-tag>
          <t-tag v-else-if="row.valueLevel === 'medium'" theme="warning" variant="light">中价值</t-tag>
          <t-tag v-else theme="default" variant="light">低价值</t-tag>
        </template>
        <template #operation="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleView(row)">查看详情</t-link>
            <t-link theme="success" @click="handleOptimize(row)">优化建议</t-link>
          </t-space>
        </template>
      </t-table>
    </t-card>

    <!-- 客户价值详情弹窗 -->
    <t-dialog
      v-model:visible="detailVisible"
      header="客户价值详情"
      width="900px"
      :footer="false"
    >
      <t-tabs v-model="detailActiveTab" theme="card">
        <t-tab-panel value="basic" label="基本信息">
          <t-descriptions :column="2" bordered>
            <t-descriptions-item label="客户编号">{{ detailData.customerCode }}</t-descriptions-item>
            <t-descriptions-item label="客户名称">{{ detailData.customerName }}</t-descriptions-item>
            <t-descriptions-item label="客户类型">{{ detailData.customerType }}</t-descriptions-item>
            <t-descriptions-item label="客户级别">{{ detailData.customerLevel }}</t-descriptions-item>
            <t-descriptions-item label="销售负责人">{{ detailData.salesPerson }}</t-descriptions-item>
            <t-descriptions-item label="客户价值等级">
              <t-tag v-if="detailData.valueLevel === 'high'" theme="danger">高价值</t-tag>
              <t-tag v-else-if="detailData.valueLevel === 'medium'" theme="warning">中价值</t-tag>
              <t-tag v-else>低价值</t-tag>
            </t-descriptions-item>
            <t-descriptions-item label="生命周期价值">¥{{ detailData.clv?.toLocaleString() }}</t-descriptions-item>
            <t-descriptions-item label="累计消费">¥{{ detailData.totalAmount?.toLocaleString() }}</t-descriptions-item>
            <t-descriptions-item label="平均客单价">¥{{ detailData.avgOrderValue?.toLocaleString() }}</t-descriptions-item>
            <t-descriptions-item label="订单数量">{{ detailData.orderCount }}</t-descriptions-item>
            <t-descriptions-item label="平均消费周期">{{ detailData.avgPurchasePeriod }}天</t-descriptions-item>
          </t-descriptions>
        </t-tab-panel>

        <t-tab-panel value="history" label="消费历史">
          <t-table :data="detailData.orderHistory || []" :columns="historyColumns" :pagination="false" />
        </t-tab-panel>

        <t-tab-panel value="analysis" label="价值分析">
          <div class="analysis-container">
            <t-alert theme="info" message="分析结果" close>
              <template #default>
                <div>该客户为{{ detailData.valueLevel === 'high' ? '高价值' : detailData.valueLevel === 'medium' ? '中等价值' : '低价值' }}客户，建议{{ detailData.suggestion }}</div>
              </template>
            </t-alert>
            <div class="analysis-item">
              <div class="analysis-label">价值贡献排名</div>
              <div class="analysis-value">第 {{ detailData.rank }} 名</div>
            </div>
            <div class="analysis-item">
              <div class="analysis-label">客户价值指数</div>
              <div class="analysis-value">{{ detailData.valueIndex }}</div>
            </div>
            <div class="analysis-item">
              <div class="analysis-label">增长潜力</div>
              <div class="analysis-value" :class="detailData.growthPotential >= 0 ? 'positive' : 'negative'">
                {{ detailData.growthPotential >= 0 ? '+' : '' }}{{ detailData.growthPotential }}%
              </div>
            </div>
          </div>
        </t-tab-panel>
      </t-tabs>
    </t-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, h } from 'vue';
import { MessagePlugin } from 'tdesign-vue-next';
import {
  IconUser, IconMoneyCircle, IconStar, IconChartPie,
  IconArrowUp, IconArrowDown
} from 'tdesign-icons-vue-next';

const loading = ref(false);
const detailVisible = ref(false);
const detailActiveTab = ref('basic');
const distributionChartRef = ref();
const trendChartRef = ref();

const statistics = reactive({
  total: 156,
  avgCLV: 82500,
  highValue: 28,
  totalCLV: 12870000
});

const searchForm = reactive({
  period: [],
  customerType: '',
  valueRange: ''
});

const detailData = reactive<any>({});

const pagination = reactive({
  current: 1,
  pageSize: 20,
  total: 156
});

const columns = [
  { colKey: 'customerCode', title: '客户编号', width: 130 },
  { colKey: 'customerName', title: '客户名称', width: 150 },
  { colKey: 'customerType', title: '客户类型', width: 100 },
  { colKey: 'customerLevel', title: '客户级别', width: 80 },
  { colKey: 'salesPerson', title: '销售负责人', width: 100 },
  { colKey: 'clv', title: '生命周期价值', width: 120 },
  { colKey: 'totalAmount', title: '累计消费', width: 120 },
  { colKey: 'orderCount', title: '订单数量', width: 100 },
  { colKey: 'avgOrderValue', title: '平均客单价', width: 120 },
  { colKey: 'valueLevel', title: '价值等级', width: 100 },
  { colKey: 'operation', title: '操作', width: 150 }
];

const historyColumns = [
  { colKey: 'orderCode', title: '订单编号', width: 130 },
  { colKey: 'orderDate', title: '下单日期', width: 120 },
  { colKey: 'amount', title: '订单金额', width: 120 },
  { colKey: 'status', title: '订单状态', width: 100 }
];

const top10Customers = ref([
  { id: 1, customerCode: 'C202600001', customerName: '上海科技有限公司', customerType: '企业客户', clv: 456800, trend: 12.5 },
  { id: 2, customerCode: 'C202600002', customerName: '北京贸易公司', customerType: '企业客户', clv: 389200, trend: 8.3 },
  { id: 3, customerCode: 'C202600003', customerName: '深圳电子公司', customerType: '企业客户', clv: 356700, trend: -2.1 },
  { id: 4, customerCode: 'C202600004', customerName: '广州制造企业', customerType: '企业客户', clv: 312500, trend: 15.6 },
  { id: 5, customerCode: 'C202600005', customerName: '杭州网络公司', customerType: '企业客户', clv: 289300, trend: 5.8 }
]);

const tableData = ref([
  {
    id: 1,
    customerCode: 'C202600001',
    customerName: '上海科技有限公司',
    customerType: '企业客户',
    customerLevel: 'A',
    salesPerson: '张三',
    clv: 456800,
    totalAmount: 256800,
    orderCount: 23,
    avgOrderValue: 11165,
    avgPurchasePeriod: 45,
    valueLevel: 'high',
    rank: 1,
    valueIndex: 95,
    growthPotential: 12.5,
    suggestion: '重点维护，提供VIP服务'
  },
  {
    id: 2,
    customerCode: 'C202600002',
    customerName: '北京贸易公司',
    customerType: '企业客户',
    customerLevel: 'B',
    salesPerson: '李四',
    clv: 389200,
    totalAmount: 125600,
    orderCount: 18,
    avgOrderValue: 6978,
    avgPurchasePeriod: 38,
    valueLevel: 'high',
    rank: 2,
    valueIndex: 88,
    growthPotential: 8.3,
    suggestion: '提升客单价，增加交叉销售'
  }
]);

const handleSearch = () => {
  MessagePlugin.success('查询成功');
};

const handleReset = () => {
  Object.assign(searchForm, {
    period: [],
    customerType: '',
    valueRange: ''
  });
};

const handleView = (row: any) => {
  detailVisible.value = true;
  detailActiveTab.value = 'basic';
  Object.assign(detailData, row);
  detailData.orderHistory = [
    { orderCode: 'ORD20260201001', orderDate: '2026-02-01', amount: 15600, status: '已完成' }
  ];
};

const handleOptimize = (row: any) => {
  MessagePlugin.info('优化建议功能开发中');
};

const handlePageChange = (pageInfo: any) => {
  pagination.current = pageInfo.current;
  pagination.pageSize = pageInfo.pageSize;
};

onMounted(() => {
  console.log('CustomerValue mounted');
});
</script>

<style scoped lang="less">
.customer-value-container {
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

.chart-placeholder {
  color: #999;
  font-size: 14px;
}

.top-list {
  .top-item {
    display: flex;
    align-items: center;
    gap: 16px;
    padding: 16px;
    border-bottom: 1px solid #f0f0f0;

    &:last-child {
      border-bottom: none;
    }

    .top-rank {
      width: 36px;
      height: 36px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      font-weight: bold;
      font-size: 16px;
      background-color: #f0f0f0;
      color: #999;

      &.rank-1 {
        background-color: #ffd700;
        color: #fff;
      }

      &.rank-2 {
        background-color: #c0c0c0;
        color: #fff;
      }

      &.rank-3 {
        background-color: #cd7f32;
        color: #fff;
      }
    }

    .top-info {
      flex: 1;

      .top-name {
        font-size: 15px;
        font-weight: 500;
        color: #333;
        margin-bottom: 4px;
      }

      .top-type {
        font-size: 13px;
        color: #999;
      }
    }

    .top-value {
      font-size: 18px;
      font-weight: bold;
      color: #d54941;
      margin-right: 16px;
    }

    .top-trend {
      display: flex;
      align-items: center;
      gap: 4px;
      font-size: 13px;

      &.up {
        color: #00a870;
      }

      &.down {
        color: #d54941;
      }
    }
  }
}

.analysis-container {
  .analysis-item {
    display: flex;
    justify-content: space-between;
    padding: 12px 0;
    border-bottom: 1px solid #f0f0f0;

    &:last-child {
      border-bottom: none;
    }

    .analysis-label {
      color: #666;
      font-size: 14px;
    }

    .analysis-value {
      font-size: 15px;
      font-weight: 500;
      color: #333;

      &.positive {
        color: #00a870;
      }

      &.negative {
        color: #d54941;
      }
    }
  }
}
</style>
