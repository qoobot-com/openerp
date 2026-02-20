<template>
  <div class="sales-opportunity-container">
    <!-- 统计卡片 -->
    <t-row :gutter="16" class="mb-4">
      <t-col :span="6">
        <t-card theme="primary">
          <t-statistic title="销售机会总数" :value="statistics.total" :loading="loading">
            <template #prefix><icon-shop /></template>
          </t-statistic>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="success">
          <t-statistic title="预计金额" :value="statistics.amount" :loading="loading">
            <template #prefix><icon-money-circle /></template>
          </t-statistic>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="warning">
          <t-statistic title="跟进中" :value="statistics.following" :loading="loading">
            <template #prefix><icon-time /></template>
          </t-statistic>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="danger">
          <t-statistic title="本月赢单" :value="statistics.won" :loading="loading">
            <template #prefix><icon-check-circle /></template>
          </t-statistic>
        </t-card>
      </t-col>
    </t-row>

    <!-- 搜索卡片 -->
    <t-card class="mb-4">
      <t-form :data="searchForm" layout="inline" @submit="handleSearch">
        <t-form-item label="机会编号" name="opportunityCode">
          <t-input v-model="searchForm.opportunityCode" placeholder="请输入机会编号" clearable />
        </t-form-item>
        <t-form-item label="机会名称" name="opportunityName">
          <t-input v-model="searchForm.opportunityName" placeholder="请输入机会名称" clearable />
        </t-form-item>
        <t-form-item label="客户名称" name="customerName">
          <t-input v-model="searchForm.customerName" placeholder="请输入客户名称" clearable />
        </t-form-item>
        <t-form-item label="销售阶段" name="stage">
          <t-select v-model="searchForm.stage" placeholder="请选择销售阶段" clearable>
            <t-option value="初步接触" label="初步接触" />
            <t-option value="需求分析" label="需求分析" />
            <t-option value="方案报价" label="方案报价" />
            <t-option value="商务谈判" label="商务谈判" />
            <t-option value="合同签署" label="合同签署" />
          </t-select>
        </t-form-item>
        <t-form-item label="负责人" name="owner">
          <t-input v-model="searchForm.owner" placeholder="请输入负责人" clearable />
        </t-form-item>
        <t-form-item>
          <t-button theme="primary" type="submit">查询</t-button>
          <t-button theme="default" @click="handleReset">重置</t-button>
        </t-form-item>
      </t-form>
    </t-card>

    <!-- 操作栏 -->
    <div class="mb-4 flex justify-between items-center">
      <div>
        <t-button theme="primary" @click="handleAdd">
          <template #icon><icon-add /></template>
          新增机会
        </t-button>
        <t-button theme="default" @click="handleBatchConvert">
          <template #icon><icon-check /></template>
          批量转化
        </t-button>
        <t-button theme="default" @click="handleExport">
          <template #icon><icon-download /></template>
          导出报表
        </t-button>
      </div>
      <t-button theme="default" variant="outline" @click="handleRefresh">
        <template #icon><icon-refresh /></template>
        刷新
      </t-button>
    </div>

    <!-- 销售机会列表 -->
    <t-card>
      <t-table
        :data="tableData"
        :columns="columns"
        :loading="loading"
        :pagination="pagination"
        row-key="id"
        :selected-row-keys="selectedRowKeys"
        @select-change="handleSelectChange"
        @page-change="handlePageChange"
      >
        <template #stage="{ row }">
          <t-tag v-if="row.stage === '初步接触'" theme="primary" variant="light">初步接触</t-tag>
          <t-tag v-else-if="row.stage === '需求分析'" theme="cyan" variant="light">需求分析</t-tag>
          <t-tag v-else-if="row.stage === '方案报价'" theme="warning" variant="light">方案报价</t-tag>
          <t-tag v-else-if="row.stage === '商务谈判'" theme="orange" variant="light">商务谈判</t-tag>
          <t-tag v-else-if="row.stage === '合同签署'" theme="success" variant="light">合同签署</t-tag>
        </template>
        <template #probability="{ row }">
          <t-progress :percentage="row.probability" size="small" />
        </template>
        <template #priority="{ row }">
          <t-tag v-if="row.priority === 'high'" theme="danger" variant="light">高</t-tag>
          <t-tag v-else-if="row.priority === 'medium'" theme="warning" variant="light">中</t-tag>
          <t-tag v-else theme="default" variant="light">低</t-tag>
        </template>
        <template #operation="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleView(row)">查看</t-link>
            <t-link theme="primary" @click="handleEdit(row)">编辑</t-link>
            <t-link theme="success" @click="handleConvert(row)">转化</t-link>
            <t-popconfirm content="确定要删除此机会吗？" @confirm="handleDelete(row)">
              <t-link theme="danger">删除</t-link>
            </t-popconfirm>
          </t-space>
        </template>
      </t-table>
    </t-card>

    <!-- 新增/编辑机会弹窗 -->
    <t-dialog
      v-model:visible="dialogVisible"
      :header="dialogTitle"
      width="900px"
      :footer="false"
      @close="handleDialogClose"
    >
      <t-tabs v-model="activeTab" theme="card">
        <!-- 基本信息 -->
        <t-tab-panel value="basic" label="基本信息">
          <t-form :data="formData" ref="formRef" :rules="rules" label-width="120px">
            <t-row :gutter="16">
              <t-col :span="12">
                <t-form-item label="机会编号" name="opportunityCode">
                  <t-input v-model="formData.opportunityCode" placeholder="自动生成" disabled />
                </t-form-item>
              </t-col>
              <t-col :span="12">
                <t-form-item label="机会名称" name="opportunityName">
                  <t-input v-model="formData.opportunityName" placeholder="请输入机会名称" />
                </t-form-item>
              </t-col>
            </t-row>
            <t-row :gutter="16">
              <t-col :span="12">
                <t-form-item label="关联客户" name="customerId">
                  <t-select v-model="formData.customerId" placeholder="请选择客户" filterable>
                    <t-option v-for="item in customers" :key="item.id" :value="item.id" :label="item.name" />
                  </t-select>
                </t-form-item>
              </t-col>
              <t-col :span="12">
                <t-form-item label="负责人" name="owner">
                  <t-input v-model="formData.owner" placeholder="请输入负责人" />
                </t-form-item>
              </t-col>
            </t-row>
            <t-row :gutter="16">
              <t-col :span="12">
                <t-form-item label="预计金额" name="expectedAmount">
                  <t-input-number v-model="formData.expectedAmount" placeholder="请输入预计金额" :min="0" />
                </t-form-item>
              </t-col>
              <t-col :span="12">
                <t-form-item label="预计成交日期" name="expectedCloseDate">
                  <t-date-picker v-model="formData.expectedCloseDate" placeholder="请选择预计成交日期" />
                </t-form-item>
              </t-col>
            </t-row>
            <t-row :gutter="16">
              <t-col :span="12">
                <t-form-item label="销售阶段" name="stage">
                  <t-select v-model="formData.stage" placeholder="请选择销售阶段">
                    <t-option value="初步接触" label="初步接触" />
                    <t-option value="需求分析" label="需求分析" />
                    <t-option value="方案报价" label="方案报价" />
                    <t-option value="商务谈判" label="商务谈判" />
                    <t-option value="合同签署" label="合同签署" />
                  </t-select>
                </t-form-item>
              </t-col>
              <t-col :span="12">
                <t-form-item label="成功概率" name="probability">
                  <t-slider v-model="formData.probability" :min="0" :max="100" :marks="{ 0: '0%', 25: '25%', 50: '50%', 75: '75%', 100: '100%' }" />
                </t-form-item>
              </t-col>
            </t-row>
            <t-row :gutter="16">
              <t-col :span="12">
                <t-form-item label="优先级" name="priority">
                  <t-select v-model="formData.priority" placeholder="请选择优先级">
                    <t-option value="high" label="高" />
                    <t-option value="medium" label="中" />
                    <t-option value="low" label="低" />
                  </t-select>
                </t-form-item>
              </t-col>
              <t-col :span="12">
                <t-form-item label="来源" name="source">
                  <t-select v-model="formData.source" placeholder="请选择来源">
                    <t-option value="客户咨询" label="客户咨询" />
                    <t-option value="市场活动" label="市场活动" />
                    <t-option value="客户介绍" label="客户介绍" />
                    <t-option value="电话营销" label="电话营销" />
                    <t-option value="其他" label="其他" />
                  </t-select>
                </t-form-item>
              </t-col>
            </t-row>
            <t-form-item label="备注" name="remark">
              <t-textarea v-model="formData.remark" placeholder="请输入备注" :maxlength="500" />
            </t-form-item>
          </t-form>
        </t-tab-panel>

        <!-- 竞品信息 -->
        <t-tab-panel value="competitor" label="竞品信息">
          <t-form :data="formData" label-width="120px">
            <t-form-item label="竞品名称" name="competitor">
              <t-input v-model="formData.competitor" placeholder="请输入竞品名称" />
            </t-form-item>
            <t-form-item label="竞品优势" name="competitorStrength">
              <t-textarea v-model="formData.competitorStrength" placeholder="请输入竞品优势" />
            </t-form-item>
            <t-form-item label="竞品劣势" name="competitorWeakness">
              <t-textarea v-model="formData.competitorWeakness" placeholder="请输入竞品劣势" />
            </t-form-item>
          </t-form>
        </t-tab-panel>
      </t-tabs>

      <div class="dialog-footer">
        <t-button theme="default" @click="dialogVisible = false">取消</t-button>
        <t-button theme="primary" @click="handleSave">保存</t-button>
      </div>
    </t-dialog>

    <!-- 机会详情弹窗 -->
    <t-dialog
      v-model:visible="detailVisible"
      header="机会详情"
      width="900px"
      :footer="false"
    >
      <t-tabs v-model="detailActiveTab" theme="card">
        <t-tab-panel value="basic" label="基本信息">
          <t-descriptions :column="2" bordered>
            <t-descriptions-item label="机会编号">{{ detailData.opportunityCode }}</t-descriptions-item>
            <t-descriptions-item label="机会名称">{{ detailData.opportunityName }}</t-descriptions-item>
            <t-descriptions-item label="关联客户">{{ detailData.customerName }}</t-descriptions-item>
            <t-descriptions-item label="负责人">{{ detailData.owner }}</t-descriptions-item>
            <t-descriptions-item label="预计金额">¥{{ detailData.expectedAmount }}</t-descriptions-item>
            <t-descriptions-item label="预计成交日期">{{ detailData.expectedCloseDate }}</t-descriptions-item>
            <t-descriptions-item label="销售阶段">{{ detailData.stage }}</t-descriptions-item>
            <t-descriptions-item label="成功概率">{{ detailData.probability }}%</t-descriptions-item>
            <t-descriptions-item label="优先级">{{ detailData.priority }}</t-descriptions-item>
            <t-descriptions-item label="来源">{{ detailData.source }}</t-descriptions-item>
            <t-descriptions-item label="创建时间" :span="2">{{ detailData.createTime }}</t-descriptions-item>
            <t-descriptions-item label="备注" :span="2">{{ detailData.remark }}</t-descriptions-item>
          </t-descriptions>
        </t-tab-panel>

        <t-tab-panel value="competitor" label="竞品信息">
          <t-descriptions :column="1" bordered>
            <t-descriptions-item label="竞品名称">{{ detailData.competitor }}</t-descriptions-item>
            <t-descriptions-item label="竞品优势">{{ detailData.competitorStrength }}</t-descriptions-item>
            <t-descriptions-item label="竞品劣势">{{ detailData.competitorWeakness }}</t-descriptions-item>
          </t-descriptions>
        </t-tab-panel>

        <t-tab-panel value="logs" label="操作日志">
          <t-table :data="detailData.logs || []" :columns="logColumns" :pagination="false" />
        </t-tab-panel>
      </t-tabs>
    </t-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { MessagePlugin } from 'tdesign-vue-next';
import {
  IconShop, IconMoneyCircle, IconTime, IconCheckCircle,
  IconAdd, IconCheck, IconDownload, IconRefresh
} from 'tdesign-icons-vue-next';

const loading = ref(false);
const dialogVisible = ref(false);
const detailVisible = ref(false);
const dialogTitle = ref('新增机会');
const activeTab = ref('basic');
const detailActiveTab = ref('basic');
const formRef = ref();
const selectedRowKeys = ref([]);

const statistics = reactive({
  total: 68,
  amount: 5800000,
  following: 45,
  won: 12
});

const searchForm = reactive({
  opportunityCode: '',
  opportunityName: '',
  customerName: '',
  stage: '',
  owner: ''
});

const formData = reactive({
  id: '',
  opportunityCode: '',
  opportunityName: '',
  customerId: '',
  owner: '',
  expectedAmount: 0,
  expectedCloseDate: '',
  stage: '初步接触',
  probability: 20,
  priority: 'medium',
  source: '',
  remark: '',
  competitor: '',
  competitorStrength: '',
  competitorWeakness: ''
});

const detailData = reactive<any>({});

const pagination = reactive({
  current: 1,
  pageSize: 20,
  total: 68
});

const customers = ref([
  { id: 1, name: '上海科技有限公司' },
  { id: 2, name: '北京贸易公司' }
]);

const columns = [
  { colKey: 'opportunityCode', title: '机会编号', width: 130 },
  { colKey: 'opportunityName', title: '机会名称', width: 150 },
  { colKey: 'customerName', title: '关联客户', width: 150 },
  { colKey: 'owner', title: '负责人', width: 100 },
  { colKey: 'expectedAmount', title: '预计金额', width: 120 },
  { colKey: 'expectedCloseDate', title: '预计成交日期', width: 120 },
  { colKey: 'stage', title: '销售阶段', width: 100 },
  { colKey: 'probability', title: '成功概率', width: 120 },
  { colKey: 'priority', title: '优先级', width: 80 },
  { colKey: 'createTime', title: '创建时间', width: 120 },
  { colKey: 'operation', title: '操作', width: 180, fixed: 'right' }
];

const logColumns = [
  { colKey: 'operator', title: '操作人', width: 100 },
  { colKey: 'action', title: '操作内容', width: 200 },
  { colKey: 'createTime', title: '操作时间', width: 150 }
];

const rules = {
  opportunityName: [{ required: true, message: '请输入机会名称', type: 'error' }],
  customerId: [{ required: true, message: '请选择客户', type: 'error' }],
  owner: [{ required: true, message: '请输入负责人', type: 'error' }],
  expectedAmount: [{ required: true, message: '请输入预计金额', type: 'error' }]
};

const tableData = ref([
  {
    id: 1,
    opportunityCode: 'OP202600001',
    opportunityName: 'ERP系统采购项目',
    customerId: 1,
    customerName: '上海科技有限公司',
    owner: '张三',
    expectedAmount: 500000,
    expectedCloseDate: '2026-03-15',
    stage: '方案报价',
    probability: 60,
    priority: 'high',
    source: '客户咨询',
    remark: '',
    competitor: '某友',
    competitorStrength: '品牌知名度高',
    competitorWeakness: '价格较高',
    createTime: '2026-01-15 10:30:00'
  },
  {
    id: 2,
    opportunityCode: 'OP202600002',
    opportunityName: 'CRM系统升级项目',
    customerId: 2,
    customerName: '北京贸易公司',
    owner: '李四',
    expectedAmount: 300000,
    expectedCloseDate: '2026-02-28',
    stage: '需求分析',
    probability: 40,
    priority: 'medium',
    source: '市场活动',
    remark: '',
    competitor: '某金',
    competitorStrength: '功能完善',
    competitorWeakness: '操作复杂',
    createTime: '2026-01-20 14:20:00'
  }
]);

const handleSearch = () => {
  MessagePlugin.success('查询成功');
};

const handleReset = () => {
  Object.assign(searchForm, {
    opportunityCode: '',
    opportunityName: '',
    customerName: '',
    stage: '',
    owner: ''
  });
};

const handleAdd = () => {
  dialogTitle.value = '新增机会';
  dialogVisible.value = true;
  activeTab.value = 'basic';
};

const handleEdit = (row: any) => {
  dialogTitle.value = '编辑机会';
  dialogVisible.value = true;
  activeTab.value = 'basic';
  Object.assign(formData, row);
};

const handleView = (row: any) => {
  detailVisible.value = true;
  detailActiveTab.value = 'basic';
  Object.assign(detailData, row);
  detailData.logs = [
    { operator: '张三', action: '创建机会', createTime: '2026-01-15 10:30:00' },
    { operator: '张三', action: '更新机会信息', createTime: '2026-01-18 09:15:00' }
  ];
};

const handleConvert = (row: any) => {
  MessagePlugin.success(`机会 ${row.opportunityName} 已转化为订单`);
};

const handleBatchConvert = () => {
  MessagePlugin.success('批量转化成功');
};

const handleDelete = (row: any) => {
  MessagePlugin.success('机会已删除');
};

const handleSave = () => {
  MessagePlugin.success(dialogTitle.value === '新增机会' ? '机会已创建' : '机会已更新');
  dialogVisible.value = false;
};

const handleDialogClose = () => {
  Object.assign(formData, {
    id: '',
    opportunityCode: '',
    opportunityName: '',
    customerId: '',
    owner: '',
    expectedAmount: 0,
    expectedCloseDate: '',
    stage: '初步接触',
    probability: 20,
    priority: 'medium',
    source: '',
    remark: '',
    competitor: '',
    competitorStrength: '',
    competitorWeakness: ''
  });
};

const handleExport = () => {
  MessagePlugin.success('报表导出成功');
};

const handleRefresh = () => {
  MessagePlugin.success('数据已刷新');
};

const handleSelectChange = (value: any) => {
  selectedRowKeys.value = value;
};

const handlePageChange = (pageInfo: any) => {
  pagination.current = pageInfo.current;
  pagination.pageSize = pageInfo.pageSize;
};

onMounted(() => {
  console.log('SalesOpportunity mounted');
});
</script>

<style scoped lang="less">
.sales-opportunity-container {
  padding: 20px;
}

.mb-4 {
  margin-bottom: 16px;
}

.flex {
  display: flex;
}

.justify-between {
  justify-content: space-between;
}

.items-center {
  align-items: center;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}
</style>
