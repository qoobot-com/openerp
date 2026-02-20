<template>
  <div class="customer-360-container">
    <!-- 客户信息卡片 -->
    <t-card class="mb-4">
      <t-row :gutter="16" class="customer-header">
        <t-col :span="3">
          <div class="customer-avatar">
            <t-avatar size="large">{{ customerInfo.customerName?.charAt(0) }}</t-avatar>
          </div>
        </t-col>
        <t-col :span="9">
          <div class="customer-basic">
            <h2>{{ customerInfo.customerName }}</h2>
            <div class="customer-tags">
              <t-tag v-for="tag in customerInfo.tags" :key="tag" :theme="tag.theme" variant="light">{{ tag.label }}</t-tag>
            </div>
            <div class="customer-meta">
              <span>客户编号: {{ customerInfo.customerCode }}</span>
              <span>级别: {{ customerInfo.level }}</span>
              <span>来源: {{ customerInfo.source }}</span>
            </div>
          </div>
        </t-col>
        <t-col :span="12">
          <div class="customer-stats">
            <t-row :gutter="16">
              <t-col :span="6">
                <t-statistic title="累计消费" :value="customerInfo.totalAmount" suffix="元" />
              </t-col>
              <t-col :span="6">
                <t-statistic title="订单数" :value="customerInfo.orderCount" />
              </t-col>
              <t-col :span="6">
                <t-statistic title="跟进次数" :value="customerInfo.followCount" />
              </t-col>
              <t-col :span="6">
                <t-statistic title="最后跟进" :value="customerInfo.lastFollowDays" suffix="天前" />
              </t-col>
            </t-row>
          </div>
        </t-col>
      </t-row>
    </t-card>

    <!-- 标签页 -->
    <t-card>
      <t-tabs v-model="activeTab" theme="card">
        <!-- 基本信息 -->
        <t-tab-panel value="basic" label="基本信息">
          <t-descriptions :column="2" bordered title="客户详情">
            <t-descriptions-item label="客户名称" :span="2">{{ customerInfo.customerName }}</t-descriptions-item>
            <t-descriptions-item label="客户编号">{{ customerInfo.customerCode }}</t-descriptions-item>
            <t-descriptions-item label="客户类型">{{ customerInfo.customerType }}</t-descriptions-item>
            <t-descriptions-item label="客户级别">{{ customerInfo.level }}</t-descriptions-item>
            <t-descriptions-item label="客户来源">{{ customerInfo.source }}</t-descriptions-item>
            <t-descriptions-item label="所属行业">{{ customerInfo.industry }}</t-descriptions-item>
            <t-descriptions-item label="销售负责人">{{ customerInfo.salesOwner }}</t-descriptions-item>
            <t-descriptions-item label="联系电话">{{ customerInfo.phone }}</t-descriptions-item>
            <t-descriptions-item label="电子邮箱" :span="2">{{ customerInfo.email }}</t-descriptions-item>
            <t-descriptions-item label="所在地区" :span="2">{{ customerInfo.address }}</t-descriptions-item>
            <t-descriptions-item label="客户状态">
              <t-tag :theme="customerInfo.status === 'active' ? 'success' : 'default'">{{ customerInfo.status === 'active' ? '活跃' : '非活跃' }}</t-tag>
            </t-descriptions-item>
            <t-descriptions-item label="创建时间">{{ customerInfo.createTime }}</t-descriptions-item>
          </t-descriptions>

          <div class="mt-4">
            <h3>联系信息</h3>
            <t-table :data="contacts" :columns="contactColumns" :pagination="false" />
          </div>
        </t-tab-panel>

        <!-- 销售机会 -->
        <t-tab-panel value="opportunity" label="销售机会">
          <t-table
            :data="opportunities"
            :columns="opportunityColumns"
            :loading="loading"
            :pagination="opportunityPagination"
            @page-change="handleOpportunityPageChange"
          >
            <template #stage="{ row }">
              <t-tag :theme="getStageTheme(row.stage)">{{ row.stage }}</t-tag>
            </template>
            <template #probability="{ row }">
              <t-progress :percentage="row.probability" :show-info="false" />
              <span>{{ row.probability }}%</span>
            </template>
            <template #operation="{ row }">
              <t-link theme="primary" @click="handleViewOpportunity(row)">查看</t-link>
            </template>
          </t-table>
        </t-tab-panel>

        <!-- 订单记录 -->
        <t-tab-panel value="orders" label="订单记录">
          <t-table
            :data="orders"
            :columns="orderColumns"
            :loading="loading"
            :pagination="orderPagination"
            @page-change="handleOrderPageChange"
          >
            <template #status="{ row }">
              <t-tag :theme="getOrderStatusTheme(row.status)">{{ row.status }}</t-tag>
            </template>
            <template #operation="{ row }">
              <t-link theme="primary" @click="handleViewOrder(row)">查看</t-link>
            </template>
          </t-table>
        </t-tab-panel>

        <!-- 跟进记录 -->
        <t-tab-panel value="follow" label="跟进记录">
          <div class="mb-4">
            <t-button theme="primary" @click="handleAddFollow">
              <template #icon><icon-add /></template>
              新增跟进
            </t-button>
          </div>
          <t-table
            :data="followRecords"
            :columns="followColumns"
            :loading="loading"
            :pagination="followPagination"
            @page-change="handleFollowPageChange"
          >
            <template #operation="{ row }">
              <t-link theme="primary" @click="handleViewFollow(row)">查看</t-link>
            </template>
          </t-table>
        </t-tab-panel>

        <!-- 客户反馈 -->
        <t-tab-panel value="feedback" label="客户反馈">
          <t-table
            :data="feedbacks"
            :columns="feedbackColumns"
            :loading="loading"
            :pagination="feedbackPagination"
            @page-change="handleFeedbackPageChange"
          >
            <template #sentiment="{ row }">
              <t-tag v-if="row.sentiment === 'positive'" theme="success">好评</t-tag>
              <t-tag v-else-if="row.sentiment === 'neutral'" theme="warning">中评</t-tag>
              <t-tag v-else theme="danger">差评</t-tag>
            </template>
            <template #operation="{ row }">
              <t-link theme="primary" @click="handleViewFeedback(row)">查看</t-link>
            </template>
          </t-table>
        </t-tab-panel>

        <!-- 合同信息 -->
        <t-tab-panel value="contracts" label="合同信息">
          <t-table
            :data="contracts"
            :columns="contractColumns"
            :loading="loading"
            :pagination="contractPagination"
            @page-change="handleContractPageChange"
          >
            <template #status="{ row }">
              <t-tag :theme="getContractStatusTheme(row.status)">{{ row.status }}</t-tag>
            </template>
            <template #operation="{ row }">
              <t-link theme="primary" @click="handleViewContract(row)">查看</t-link>
            </template>
          </t-table>
        </t-tab-panel>

        <!-- 服务工单 -->
        <t-tab-panel value="service" label="服务工单">
          <t-table
            :data="serviceTickets"
            :columns="serviceColumns"
            :loading="loading"
            :pagination="servicePagination"
            @page-change="handleServicePageChange"
          >
            <template #priority="{ row }">
              <t-tag v-if="row.priority === 'high'" theme="danger">高</t-tag>
              <t-tag v-else-if="row.priority === 'medium'" theme="warning">中</t-tag>
              <t-tag v-else theme="default">低</t-tag>
            </template>
            <template #status="{ row }">
              <t-tag :theme="getServiceStatusTheme(row.status)">{{ row.status }}</t-tag>
            </template>
            <template #operation="{ row }">
              <t-link theme="primary" @click="handleViewService(row)">查看</t-link>
            </template>
          </t-table>
        </t-tab-panel>

        <!-- 数据分析 -->
        <t-tab-panel value="analysis" label="数据分析">
          <t-row :gutter="16">
            <t-col :span="12">
              <t-card title="消费趋势">
                <div class="chart-placeholder">
                  <t-icon name="chart-line" size="60px" />
                  <p>消费趋势图表</p>
                </div>
              </t-card>
            </t-col>
            <t-col :span="12">
              <t-card title="订单分布">
                <div class="chart-placeholder">
                  <t-icon name="chart-pie" size="60px" />
                  <p>订单分布饼图</p>
                </div>
              </t-card>
            </t-col>
          </t-row>
          <t-row :gutter="16" class="mt-4">
            <t-col :span="12">
              <t-card title="产品偏好">
                <div class="chart-placeholder">
                  <t-icon name="chart-bar" size="60px" />
                  <p>产品偏好柱状图</p>
                </div>
              </t-card>
            </t-col>
            <t-col :span="12">
              <t-card title="互动频率">
                <div class="chart-placeholder">
                  <t-icon name="chart-area" size="60px" />
                  <p>互动频率折线图</p>
                </div>
              </t-card>
            </t-col>
          </t-row>
        </t-tab-panel>
      </t-tabs>
    </t-card>

    <!-- 新增跟进弹窗 -->
    <t-dialog
      v-model:visible="followDialogVisible"
      header="新增跟进记录"
      width="700px"
      :footer="false"
    >
      <t-form :data="followForm" label-width="100px">
        <t-form-item label="跟进类型" name="type">
          <t-select v-model="followForm.type" placeholder="请选择跟进类型">
            <t-option value="电话" label="电话" />
            <t-option value="拜访" label="拜访" />
            <t-option value="邮件" label="邮件" />
            <t-option value="微信" label="微信" />
            <t-option value="其他" label="其他" />
          </t-select>
        </t-form-item>
        <t-form-item label="跟进内容" name="content">
          <t-textarea v-model="followForm.content" placeholder="请输入跟进内容" :maxlength="1000" />
        </t-form-item>
        <t-form-item label="下次跟进" name="nextFollowDate">
          <t-date-picker v-model="followForm.nextFollowDate" placeholder="请选择下次跟进时间" />
        </t-form-item>
        <t-form-item label="附件" name="attachments">
          <t-upload v-model="followForm.attachments" theme="file-input" multiple />
        </t-form-item>
      </t-form>
      <div class="dialog-footer">
        <t-button theme="default" @click="followDialogVisible = false">取消</t-button>
        <t-button theme="primary" @click="handleSaveFollow">保存</t-button>
      </div>
    </t-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { MessagePlugin } from 'tdesign-vue-next';
import { IconAdd } from 'tdesign-icons-vue-next';

const activeTab = ref('basic');
const loading = ref(false);
const followDialogVisible = ref(false);

const customerInfo = reactive({
  customerName: '上海科技有限公司',
  customerCode: 'CUS20260001',
  customerType: '企业客户',
  level: 'VIP',
  source: '展会',
  industry: '制造业',
  salesOwner: '张三',
  phone: '021-12345678',
  email: 'contact@shanghai-tech.com',
  address: '上海市浦东新区张江高科技园区',
  status: 'active',
  totalAmount: 568000,
  orderCount: 23,
  followCount: 45,
  lastFollowDays: 3,
  createTime: '2025-06-15',
  tags: [
    { label: 'VIP', theme: 'primary' },
    { label: '高价值', theme: 'success' },
    { label: '老客户', theme: 'warning' }
  ]
});

const contacts = [
  { name: '李明', position: '总经理', phone: '13800138001', email: 'liming@shanghai-tech.com' },
  { name: '王芳', position: '采购经理', phone: '13800138002', email: 'wangfang@shanghai-tech.com' }
];

const opportunities = ref([
  { id: 1, name: 'ERP系统升级项目', amount: 200000, stage: '谈判中', probability: 60, expectedDate: '2026-03-31' }
]);

const orders = ref([
  { id: 1, orderCode: 'ORD202600001', productName: '财务管理系统', amount: 58000, status: '已完成', createDate: '2026-02-10' }
]);

const followRecords = ref([
  { id: 1, type: '拜访', content: '拜访了解使用情况，客户反馈良好', followDate: '2026-02-16', followBy: '张三' }
]);

const feedbacks = ref([
  { id: 1, type: '产品建议', rating: 5, sentiment: 'positive', title: '界面美观易用', submitTime: '2026-02-15' }
]);

const contracts = ref([
  { id: 1, contractCode: 'CON202600001', contractName: '年度服务合同', amount: 100000, status: '执行中', signDate: '2026-01-01' }
]);

const serviceTickets = ref([
  { id: 1, ticketCode: 'SR202600001', title: '系统登录问题', priority: 'high', status: '待处理', createTime: '2026-02-19' }
]);

const followForm = reactive({
  type: '',
  content: '',
  nextFollowDate: '',
  attachments: []
});

const contactColumns = [
  { colKey: 'name', title: '姓名', width: 100 },
  { colKey: 'position', title: '职位', width: 120 },
  { colKey: 'phone', title: '联系电话', width: 150 },
  { colKey: 'email', title: '邮箱', width: 200 }
];

const opportunityColumns = [
  { colKey: 'name', title: '机会名称', width: 200 },
  { colKey: 'amount', title: '预计金额(元)', width: 120 },
  { colKey: 'stage', title: '阶段', width: 100 },
  { colKey: 'probability', title: '成功概率', width: 120 },
  { colKey: 'expectedDate', title: '预计成交日期', width: 120 },
  { colKey: 'operation', title: '操作', width: 80 }
];

const orderColumns = [
  { colKey: 'orderCode', title: '订单编号', width: 140 },
  { colKey: 'productName', title: '产品名称', width: 150 },
  { colKey: 'amount', title: '订单金额(元)', width: 120 },
  { colKey: 'status', title: '状态', width: 100 },
  { colKey: 'createDate', title: '下单日期', width: 120 },
  { colKey: 'operation', title: '操作', width: 80 }
];

const followColumns = [
  { colKey: 'type', title: '跟进类型', width: 100 },
  { colKey: 'content', title: '跟进内容', width: 300, ellipsis: true },
  { colKey: 'followDate', title: '跟进日期', width: 120 },
  { colKey: 'followBy', title: '跟进人', width: 100 },
  { colKey: 'operation', title: '操作', width: 80 }
];

const feedbackColumns = [
  { colKey: 'type', title: '反馈类型', width: 100 },
  { colKey: 'title', title: '反馈标题', width: 200, ellipsis: true },
  { colKey: 'rating', title: '评分', width: 80 },
  { colKey: 'sentiment', title: '情感倾向', width: 100 },
  { colKey: 'submitTime', title: '提交时间', width: 150 },
  { colKey: 'operation', title: '操作', width: 80 }
];

const contractColumns = [
  { colKey: 'contractCode', title: '合同编号', width: 140 },
  { colKey: 'contractName', title: '合同名称', width: 150 },
  { colKey: 'amount', title: '合同金额(元)', width: 120 },
  { colKey: 'status', title: '状态', width: 100 },
  { colKey: 'signDate', title: '签订日期', width: 120 },
  { colKey: 'operation', title: '操作', width: 80 }
];

const serviceColumns = [
  { colKey: 'ticketCode', title: '工单编号', width: 140 },
  { colKey: 'title', title: '工单标题', width: 200, ellipsis: true },
  { colKey: 'priority', title: '优先级', width: 80 },
  { colKey: 'status', title: '状态', width: 100 },
  { colKey: 'createTime', title: '创建时间', width: 150 },
  { colKey: 'operation', title: '操作', width: 80 }
];

const opportunityPagination = reactive({ current: 1, pageSize: 10, total: 1 });
const orderPagination = reactive({ current: 1, pageSize: 10, total: 1 });
const followPagination = reactive({ current: 1, pageSize: 10, total: 1 });
const feedbackPagination = reactive({ current: 1, pageSize: 10, total: 1 });
const contractPagination = reactive({ current: 1, pageSize: 10, total: 1 });
const servicePagination = reactive({ current: 1, pageSize: 10, total: 1 });

const getStageTheme = (stage: string) => {
  const themes: Record<string, any> = { '初步接触': 'default', '需求分析': 'primary', '方案提交': 'warning', '谈判中': 'warning', '已成交': 'success', '已流失': 'danger' };
  return themes[stage] || 'default';
};

const getOrderStatusTheme = (status: string) => {
  const themes: Record<string, any> = { '待付款': 'warning', '已付款': 'primary', '生产中': 'primary', '已发货': 'primary', '已完成': 'success', '已取消': 'default' };
  return themes[status] || 'default';
};

const getContractStatusTheme = (status: string) => {
  const themes: Record<string, any> = { '草稿': 'default', '审批中': 'warning', '执行中': 'success', '已完成': 'default', '已终止': 'danger' };
  return themes[status] || 'default';
};

const getServiceStatusTheme = (status: string) => {
  const themes: Record<string, any> = { '待处理': 'warning', '处理中': 'primary', '已解决': 'success', '已关闭': 'default' };
  return themes[status] || 'default';
};

const handleOpportunityPageChange = (pageInfo: any) => { opportunityPagination.current = pageInfo.current; };
const handleOrderPageChange = (pageInfo: any) => { orderPagination.current = pageInfo.current; };
const handleFollowPageChange = (pageInfo: any) => { followPagination.current = pageInfo.current; };
const handleFeedbackPageChange = (pageInfo: any) => { feedbackPagination.current = pageInfo.current; };
const handleContractPageChange = (pageInfo: any) => { contractPagination.current = pageInfo.current; };
const handleServicePageChange = (pageInfo: any) => { servicePagination.current = pageInfo.current; };

const handleAddFollow = () => { followDialogVisible.value = true; };
const handleSaveFollow = () => { MessagePlugin.success('跟进记录已保存'); followDialogVisible.value = false; };
const handleViewOpportunity = (row: any) => console.log('View opportunity', row);
const handleViewOrder = (row: any) => console.log('View order', row);
const handleViewFollow = (row: any) => console.log('View follow', row);
const handleViewFeedback = (row: any) => console.log('View feedback', row);
const handleViewContract = (row: any) => console.log('View contract', row);
const handleViewService = (row: any) => console.log('View service', row);

onMounted(() => { console.log('Customer360 mounted'); });
</script>

<style scoped lang="less">
.customer-360-container { padding: 20px; }
.mb-4 { margin-bottom: 16px; }
.mt-4 { margin-top: 16px; }
.customer-header { display: flex; align-items: center; }
.customer-avatar { display: flex; align-items: center; }
.customer-basic h2 { margin: 0 0 10px 0; }
.customer-tags { display: flex; gap: 8px; margin-bottom: 10px; }
.customer-meta { display: flex; gap: 20px; color: #666; }
.chart-placeholder { text-align: center; padding: 40px; color: #999; }
.dialog-footer { display: flex; justify-content: flex-end; gap: 10px; margin-top: 20px; }
</style>
