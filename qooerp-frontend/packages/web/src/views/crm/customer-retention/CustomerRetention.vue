<template>
  <div class="customer-retention-container">
    <!-- 统计卡片 -->
    <t-row :gutter="16" class="mb-4">
      <t-col :span="6">
        <t-card theme="primary">
          <t-statistic title="客户总数" :value="statistics.totalCustomers" :loading="loading">
            <template #prefix><icon-user /></template>
          </t-statistic>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="success">
          <t-statistic title="留存率" :value="statistics.retentionRate" suffix="%" :loading="loading">
            <template #prefix><icon-heart /></template>
          </t-statistic>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="warning">
          <t-statistic title="流失客户" :value="statistics.churnedCustomers" :loading="loading">
            <template #prefix><icon-usergroup-delete /></template>
          </t-statistic>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="danger">
          <t-statistic title="流失率" :value="statistics.churnRate" suffix="%" :loading="loading">
            <template #prefix><icon-chart-line-data /></template>
          </t-statistic>
        </t-card>
      </t-col>
    </t-row>

    <!-- 搜索卡片 -->
    <t-card class="mb-4">
      <t-form :data="searchForm" layout="inline" @submit="handleSearch">
        <t-form-item label="分析周期" name="period">
          <t-date-range-picker v-model="searchForm.period" placeholder="请选择分析周期" />
        </t-form-item>
        <t-form-item label="客户类型" name="customerType">
          <t-select v-model="searchForm.customerType" placeholder="请选择客户类型" clearable>
            <t-option value="all" label="全部" />
            <t-option value="vip" label="VIP客户" />
            <t-option value="normal" label="普通客户" />
            <t-option value="potential" label="潜在客户" />
          </t-select>
        </t-form-item>
        <t-form-item label="风险等级" name="riskLevel">
          <t-select v-model="searchForm.riskLevel" placeholder="请选择风险等级" clearable>
            <t-option value="high" label="高风险" />
            <t-option value="medium" label="中风险" />
            <t-option value="low" label="低风险" />
            <t-option value="none" label="无风险" />
          </t-select>
        </t-form-item>
        <t-form-item>
          <t-button theme="primary" type="submit">查询</t-button>
          <t-button theme="default" @click="handleReset">重置</t-button>
        </t-form-item>
      </t-form>
    </t-card>

    <!-- 标签页 -->
    <t-card>
      <t-tabs v-model="activeTab" theme="card">
        <!-- 留存分析 -->
        <t-tab-panel value="retention" label="留存分析">
          <t-row :gutter="16" class="mb-4">
            <t-col :span="12">
              <t-card title="留存趋势">
                <div class="chart-placeholder">
                  <t-icon name="chart-line" size="60px" />
                  <p>留存趋势折线图</p>
                </div>
              </t-card>
            </t-col>
            <t-col :span="12">
              <t-card title="留存分布">
                <div class="chart-placeholder">
                  <t-icon name="chart-bar" size="60px" />
                  <p>留存分布柱状图</p>
                </div>
              </t-card>
            </t-col>
          </t-row>
          <t-card title="留存率统计">
            <t-table :data="retentionData" :columns="retentionColumns" :pagination="false" />
          </t-card>
        </t-tab-panel>

        <!-- 流失预警 -->
        <t-tab-panel value="churn" label="流失预警">
          <t-row :gutter="16" class="mb-4">
            <t-col :span="6">
              <t-card title="高风险客户">
                <t-statistic :value="statistics.highRiskCustomers" />
              </t-card>
            </t-col>
            <t-col :span="6">
              <t-card title="中风险客户">
                <t-statistic :value="statistics.mediumRiskCustomers" />
              </t-card>
            </t-col>
            <t-col :span="6">
              <t-card title="低风险客户">
                <t-statistic :value="statistics.lowRiskCustomers" />
              </t-card>
            </t-col>
            <t-col :span="6">
              <t-card title="无风险客户">
                <t-statistic :value="statistics.noRiskCustomers" />
              </t-card>
            </t-col>
          </t-row>
          <div class="mb-4 flex justify-between items-center">
            <t-button theme="primary" @click="handleRetentionCampaign">
              <template #icon><icon-send /></template>
              发送挽留活动
            </t-button>
          </div>
          <t-table
            :data="churnRiskData"
            :columns="churnRiskColumns"
            :loading="loading"
            :pagination="pagination"
            @page-change="handlePageChange"
          >
            <template #riskLevel="{ row }">
              <t-tag v-if="row.riskLevel === 'high'" theme="danger">高风险</t-tag>
              <t-tag v-else-if="row.riskLevel === 'medium'" theme="warning">中风险</t-tag>
              <t-tag v-else-if="row.riskLevel === 'low'" theme="primary">低风险</t-tag>
              <t-tag v-else theme="success">无风险</t-tag>
            </template>
            <template #lastActivity="{ row }">
              <t-tag v-if="row.lastActivityDays <= 7" theme="success" variant="light">活跃</t-tag>
              <t-tag v-else-if="row.lastActivityDays <= 30" theme="warning" variant="light">一般</t-tag>
              <t-tag v-else theme="danger" variant="light">不活跃</t-tag>
            </template>
            <template #operation="{ row }">
              <t-space>
                <t-link theme="primary" @click="handleViewCustomer(row)">查看</t-link>
                <t-link theme="primary" @click="handleAddActivity(row)">添加跟进</t-link>
                <t-link theme="success" @click="handleSendOffer(row)">发送优惠</t-link>
              </t-space>
            </template>
          </t-table>
        </t-tab-panel>

        <!-- 流失分析 -->
        <t-tab-panel value="churnAnalysis" label="流失分析">
          <t-row :gutter="16" class="mb-4">
            <t-col :span="12">
              <t-card title="流失趋势">
                <div class="chart-placeholder">
                  <t-icon name="chart-line" size="60px" />
                  <p>流失趋势折线图</p>
                </div>
              </t-card>
            </t-col>
            <t-col :span="12">
              <t-card title="流失原因分布">
                <div class="chart-placeholder">
                  <t-icon name="chart-pie" size="60px" />
                  <p>流失原因饼图</p>
                </div>
              </t-card>
            </t-col>
          </t-row>
          <t-card title="流失客户统计">
            <t-table :data="churnedData" :columns="churnedColumns" :pagination="churnedPagination" @page-change="handleChurnedPageChange" />
          </t-card>
        </t-tab-panel>

        <!-- 挽留策略 -->
        <t-tab-panel value="strategy" label="挽留策略">
          <div class="mb-4 flex justify-between items-center">
            <t-button theme="primary" @click="handleAddStrategy">
              <template #icon><icon-add /></template>
              新建策略
            </t-button>
          </div>
          <t-table
            :data="strategies"
            :columns="strategyColumns"
            :loading="loading"
            :pagination="strategyPagination"
            @page-change="handleStrategyPageChange"
          >
            <template #status="{ row }">
              <t-tag v-if="row.status === 'active'" theme="success">启用</t-tag>
              <t-tag v-else theme="default">禁用</t-tag>
            </template>
            <template #effectiveness="{ row }">
              <t-progress :percentage="row.effectiveness" :show-info="true" />
            </template>
            <template #operation="{ row }">
              <t-space>
                <t-link theme="primary" @click="handleViewStrategy(row)">查看</t-link>
                <t-link theme="primary" @click="handleEditStrategy(row)">编辑</t-link>
                <t-link v-if="row.status === 'active'" theme="warning" @click="handleDisableStrategy(row)">禁用</t-link>
                <t-link v-else theme="success" @click="handleEnableStrategy(row)">启用</t-link>
              </t-space>
            </template>
          </t-table>
        </t-tab-panel>
      </t-tabs>
    </t-card>

    <!-- 新建策略弹窗 -->
    <t-dialog
      v-model:visible="strategyDialogVisible"
      :header="strategyDialogTitle"
      width="700px"
      :footer="false"
    >
      <t-form :data="strategyForm" label-width="120px">
        <t-form-item label="策略名称" name="name">
          <t-input v-model="strategyForm.name" placeholder="请输入策略名称" />
        </t-form-item>
        <t-form-item label="适用对象" name="target">
          <t-select v-model="strategyForm.target" placeholder="请选择适用对象">
            <t-option value="high_risk" label="高风险客户" />
            <t-option value="medium_risk" label="中风险客户" />
            <t-option value="low_risk" label="低风险客户" />
            <t-option value="inactive" label="不活跃客户" />
          </t-select>
        </t-form-item>
        <t-form-item label="挽留方式" name="method">
          <t-select v-model="strategyForm.method" placeholder="请选择挽留方式">
            <t-option value="coupon" label="优惠券" />
            <t-option value="discount" label="折扣优惠" />
            <t-option value="gift" label="赠送服务" />
            <t-option value="call" label="电话回访" />
          </t-select>
        </t-form-item>
        <t-form-item label="优惠力度" name="offer">
          <t-input-number v-model="strategyForm.offer" :min="0" :max="100" :suffix="'%'" />
        </t-form-item>
        <t-form-item label="策略描述" name="description">
          <t-textarea v-model="strategyForm.description" placeholder="请输入策略描述" :maxlength="500" />
        </t-form-item>
      </t-form>
      <div class="dialog-footer">
        <t-button theme="default" @click="strategyDialogVisible = false">取消</t-button>
        <t-button theme="primary" @click="handleSaveStrategy">保存</t-button>
      </div>
    </t-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { MessagePlugin } from 'tdesign-vue-next';
import {
  IconUser, IconHeart, IconUsergroupDelete, IconChartLineData,
  IconSend, IconAdd
} from 'tdesign-icons-vue-next';

const activeTab = ref('retention');
const loading = ref(false);
const strategyDialogVisible = ref(false);
const strategyDialogTitle = ref('新建策略');

const statistics = reactive({
  totalCustomers: 5600,
  retentionRate: 85.6,
  churnedCustomers: 234,
  churnRate: 4.2,
  highRiskCustomers: 45,
  mediumRiskCustomers: 123,
  lowRiskCustomers: 312,
  noRiskCustomers: 5120
});

const searchForm = reactive({
  period: '',
  customerType: '',
  riskLevel: ''
});

const strategyForm = reactive({
  name: '',
  target: '',
  method: '',
  offer: 10,
  description: ''
});

const pagination = reactive({ current: 1, pageSize: 20, total: 45 });
const churnedPagination = reactive({ current: 1, pageSize: 20, total: 234 });
const strategyPagination = reactive({ current: 1, pageSize: 20, total: 8 });

const retentionData = ref([
  { period: '2026-01', newCustomers: 450, retainedCustomers: 385, retentionRate: 85.6, churnedCustomers: 65 },
  { period: '2025-12', newCustomers: 520, retainedCustomers: 445, retentionRate: 85.6, churnedCustomers: 75 }
]);

const churnRiskData = ref([
  { id: 1, customerName: '上海贸易公司', customerType: 'VIP', riskLevel: 'high', riskScore: 85, lastActivityDays: 45, totalAmount: 256000, lastOrderDate: '2026-01-05' },
  { id: 2, customerName: '北京科技发展', customerType: '普通', riskLevel: 'medium', riskScore: 65, lastActivityDays: 30, totalAmount: 128000, lastOrderDate: '2026-01-20' }
]);

const churnedData = ref([
  { id: 1, customerName: '广州电子公司', customerType: '普通', churnDate: '2026-02-10', churnReason: '价格过高', totalAmount: 56000, lifecycle: 180 },
  { id: 2, customerName: '深圳制造企业', customerType: 'VIP', churnDate: '2026-02-08', churnReason: '服务不满意', totalAmount: 189000, lifecycle: 360 }
]);

const strategies = ref([
  { id: 1, name: '高风险客户优惠策略', target: '高风险客户', method: '优惠券', offer: 20, status: 'active', effectiveness: 78 },
  { id: 2, name: '不活跃客户回访策略', target: '不活跃客户', method: '电话回访', offer: 0, status: 'active', effectiveness: 65 }
]);

const retentionColumns = [
  { colKey: 'period', title: '周期', width: 120 },
  { colKey: 'newCustomers', title: '新增客户', width: 100 },
  { colKey: 'retainedCustomers', title: '留存客户', width: 100 },
  { colKey: 'retentionRate', title: '留存率', width: 100 },
  { colKey: 'churnedCustomers', title: '流失客户', width: 100 }
];

const churnRiskColumns = [
  { colKey: 'customerName', title: '客户名称', width: 150 },
  { colKey: 'customerType', title: '客户类型', width: 100 },
  { colKey: 'riskLevel', title: '风险等级', width: 100 },
  { colKey: 'riskScore', title: '风险评分', width: 100 },
  { colKey: 'lastActivity', title: '活跃状态', width: 100 },
  { colKey: 'lastActivityDays', title: '未活跃天数', width: 120 },
  { colKey: 'totalAmount', title: '累计消费', width: 100 },
  { colKey: 'lastOrderDate', title: '最后订单', width: 120 },
  { colKey: 'operation', title: '操作', width: 200 }
];

const churnedColumns = [
  { colKey: 'customerName', title: '客户名称', width: 150 },
  { colKey: 'customerType', title: '客户类型', width: 100 },
  { colKey: 'churnDate', title: '流失日期', width: 120 },
  { colKey: 'churnReason', title: '流失原因', width: 150 },
  { colKey: 'totalAmount', title: '累计消费', width: 100 },
  { colKey: 'lifecycle', title: '生命周期(天)', width: 150 }
];

const strategyColumns = [
  { colKey: 'name', title: '策略名称', width: 200 },
  { colKey: 'target', title: '适用对象', width: 120 },
  { colKey: 'method', title: '挽留方式', width: 100 },
  { colKey: 'offer', title: '优惠力度', width: 100 },
  { colKey: 'status', title: '状态', width: 80 },
  { colKey: 'effectiveness', title: '有效性', width: 150 },
  { colKey: 'operation', title: '操作', width: 200 }
];

const handleSearch = () => { MessagePlugin.success('查询成功'); };
const handleReset = () => { Object.assign(searchForm, { period: '', customerType: '', riskLevel: '' }); };

const handlePageChange = (pageInfo: any) => { pagination.current = pageInfo.current; };
const handleChurnedPageChange = (pageInfo: any) => { churnedPagination.current = pageInfo.current; };
const handleStrategyPageChange = (pageInfo: any) => { strategyPagination.current = pageInfo.current; };

const handleViewCustomer = (row: any) => console.log('View customer', row);
const handleAddActivity = (row: any) => MessagePlugin.success('跟进已添加');
const handleSendOffer = (row: any) => MessagePlugin.success('优惠已发送');
const handleRetentionCampaign = () => MessagePlugin.success('挽留活动已发送');

const handleAddStrategy = () => {
  strategyDialogTitle.value = '新建策略';
  strategyDialogVisible.value = true;
};

const handleViewStrategy = (row: any) => console.log('View strategy', row);
const handleEditStrategy = (row: any) => {
  strategyDialogTitle.value = '编辑策略';
  strategyDialogVisible.value = true;
  Object.assign(strategyForm, row);
};
const handleDisableStrategy = (row: any) => MessagePlugin.success('策略已禁用');
const handleEnableStrategy = (row: any) => MessagePlugin.success('策略已启用');
const handleSaveStrategy = () => {
  MessagePlugin.success('策略已保存');
  strategyDialogVisible.value = false;
};

onMounted(() => { console.log('CustomerRetention mounted'); });
</script>

<style scoped lang="less">
.customer-retention-container { padding: 20px; }
.mb-4 { margin-bottom: 16px; }
.flex { display: flex; }
.justify-between { justify-content: space-between; }
.items-center { align-items: center; }
.dialog-footer { display: flex; justify-content: flex-end; gap: 10px; margin-top: 20px; }
.chart-placeholder { text-align: center; padding: 40px; color: #999; }
</style>
