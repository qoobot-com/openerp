<template>
  <div class="project-cost-container">
    <!-- 统计卡片 -->
    <t-row :gutter="16" class="mb-4">
      <t-col :span="6">
        <t-card theme="primary">
          <t-statistic title="总预算" :value="statistics.totalBudget" :decimal-places="2" :loading="loading">
            <template #prefix><icon-money-circle /></template>
          </t-statistic>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="success">
          <t-statistic title="已用预算" :value="statistics.usedBudget" :decimal-places="2" :loading="loading">
            <template #prefix><icon-bubble-chart /></template>
          </t-statistic>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="warning">
          <t-statistic title="剩余预算" :value="statistics.remainingBudget" :decimal-places="2" :loading="loading">
            <template #prefix><icon-wallet /></template>
          </t-statistic>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="danger">
          <t-statistic title="预算使用率" :value="statistics.usageRate" suffix="%" :loading="loading">
            <template #prefix><icon-chart-pie /></template>
          </t-statistic>
        </t-card>
      </t-col>
    </t-row>

    <!-- 搜索卡片 -->
    <t-card class="mb-4">
      <t-form :data="searchForm" layout="inline" @submit="handleSearch">
        <t-form-item label="项目名称" name="projectName">
          <t-input v-model="searchForm.projectName" placeholder="请输入项目名称" clearable />
        </t-form-item>
        <t-form-item label="费用类型" name="costType">
          <t-select v-model="searchForm.costType" placeholder="请选择费用类型" clearable>
            <t-option value="人力成本" label="人力成本" />
            <t-option value="设备成本" label="设备成本" />
            <t-option value="材料成本" label="材料成本" />
            <t-option value="其他费用" label="其他费用" />
          </t-select>
        </t-form-item>
        <t-form-item label="日期范围" name="dateRange">
          <t-date-range-picker v-model="searchForm.dateRange" placeholder="选择日期范围" />
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
        <t-button theme="primary" @click="handleAddExpense">
          <template #icon><icon-add /></template>
          新增费用
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

    <!-- 项目成本列表 -->
    <t-card>
      <t-table
        :data="projectCostData"
        :columns="projectColumns"
        :loading="loading"
        :pagination="false"
        row-key="id"
        @row-click="handleRowClick"
      >
        <template #budgetStatus="{ row }">
          <t-tag v-if="row.budgetStatus === 'normal'" theme="success" variant="light">正常</t-tag>
          <t-tag v-else-if="row.budgetStatus === 'warning'" theme="warning" variant="light">预警</t-tag>
          <t-tag v-else theme="danger" variant="light">超支</t-tag>
        </template>
      </t-table>
    </t-card>

    <!-- 费用明细表格 -->
    <t-card class="mt-4">
      <div class="card-header">
        <span class="card-title">费用明细</span>
        <span v-if="selectedProject" class="project-name">- {{ selectedProject.projectName }}</span>
      </div>
      <t-table
        :data="expenseData"
        :columns="expenseColumns"
        :loading="loading"
        :pagination="pagination"
        row-key="id"
        @page-change="handlePageChange"
      >
        <template #costType="{ row }">
          <t-tag v-if="row.costType === '人力成本'" theme="primary" variant="light">人力成本</t-tag>
          <t-tag v-else-if="row.costType === '设备成本'" theme="success" variant="light">设备成本</t-tag>
          <t-tag v-else-if="row.costType === '材料成本'" theme="warning" variant="light">材料成本</t-tag>
          <t-tag v-else theme="default" variant="light">其他费用</t-tag>
        </template>
        <template #operation="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleViewExpense(row)">查看</t-link>
            <t-link theme="primary" @click="handleEditExpense(row)">编辑</t-link>
            <t-popconfirm content="确定要删除此费用吗？" @confirm="handleDeleteExpense(row)">
              <t-link theme="danger">删除</t-link>
            </t-popconfirm>
          </t-space>
        </template>
      </t-table>
    </t-card>

    <!-- 新增/编辑费用弹窗 -->
    <t-dialog
      v-model:visible="expenseDialogVisible"
      :header="expenseDialogTitle"
      width="700px"
      :footer="false"
      @close="handleExpenseDialogClose"
    >
      <t-form :data="expenseForm" ref="expenseFormRef" :rules="expenseRules" label-width="120px">
        <t-form-item label="关联项目" name="projectId">
          <t-select v-model="expenseForm.projectId" placeholder="请选择项目" disabled>
            <t-option value="1" label="ERP系统开发" />
            <t-option value="2" label="CRM系统开发" />
          </t-select>
        </t-form-item>
        <t-row :gutter="16">
          <t-col :span="12">
            <t-form-item label="费用编号" name="expenseCode">
              <t-input v-model="expenseForm.expenseCode" placeholder="自动生成" disabled />
            </t-form-item>
          </t-col>
          <t-col :span="12">
            <t-form-item label="费用类型" name="costType">
              <t-select v-model="expenseForm.costType" placeholder="请选择费用类型">
                <t-option value="人力成本" label="人力成本" />
                <t-option value="设备成本" label="设备成本" />
                <t-option value="材料成本" label="材料成本" />
                <t-option value="其他费用" label="其他费用" />
              </t-select>
            </t-form-item>
          </t-col>
        </t-row>
        <t-form-item label="费用名称" name="expenseName">
          <t-input v-model="expenseForm.expenseName" placeholder="请输入费用名称" />
        </t-form-item>
        <t-row :gutter="16">
          <t-col :span="12">
            <t-form-item label="金额" name="amount">
              <t-input-number v-model="expenseForm.amount" placeholder="请输入金额" :min="0" :decimal-places="2" />
            </t-form-item>
          </t-col>
          <t-col :span="12">
            <t-form-item label="发生日期" name="expenseDate">
              <t-date-picker v-model="expenseForm.expenseDate" placeholder="请选择发生日期" />
            </t-form-item>
          </t-col>
        </t-row>
        <t-form-item label="费用描述" name="description">
          <t-textarea v-model="expenseForm.description" placeholder="请输入费用描述" :maxlength="500" />
        </t-form-item>
        <t-form-item label="经办人" name="handler">
          <t-input v-model="expenseForm.handler" placeholder="请输入经办人" />
        </t-form-item>
      </t-form>
      <div class="dialog-footer">
        <t-button theme="default" @click="expenseDialogVisible = false">取消</t-button>
        <t-button theme="primary" @click="handleSaveExpense">保存</t-button>
      </div>
    </t-dialog>

    <!-- 费用详情弹窗 -->
    <t-dialog
      v-model:visible="expenseDetailVisible"
      header="费用详情"
      width="600px"
      :footer="false"
    >
      <t-descriptions :column="2" bordered>
        <t-descriptions-item label="费用编号">{{ expenseDetailData.expenseCode }}</t-descriptions-item>
        <t-descriptions-item label="费用类型">{{ expenseDetailData.costType }}</t-descriptions-item>
        <t-descriptions-item label="费用名称" :span="2">{{ expenseDetailData.expenseName }}</t-descriptions-item>
        <t-descriptions-item label="金额">{{ expenseDetailData.amount }} 元</t-descriptions-item>
        <t-descriptions-item label="发生日期">{{ expenseDetailData.expenseDate }}</t-descriptions-item>
        <t-descriptions-item label="经办人">{{ expenseDetailData.handler }}</t-descriptions-item>
        <t-descriptions-item label="创建时间">{{ expenseDetailData.createTime }}</t-descriptions-item>
        <t-descriptions-item label="费用描述" :span="2">{{ expenseDetailData.description }}</t-descriptions-item>
      </t-descriptions>
    </t-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { MessagePlugin } from 'tdesign-vue-next';
import {
  IconMoneyCircle, IconBubbleChart, IconWallet, IconChartPie,
  IconAdd, IconDownload, IconRefresh
} from 'tdesign-icons-vue-next';

const loading = ref(false);
const expenseDialogVisible = ref(false);
const expenseDetailVisible = ref(false);
const expenseDialogTitle = ref('新增费用');
const expenseFormRef = ref();
const selectedProject = ref<any>(null);

const statistics = reactive({
  totalBudget: 1500000,
  usedBudget: 890000,
  remainingBudget: 610000,
  usageRate: 59.33
});

const searchForm = reactive({
  projectName: '',
  costType: '',
  dateRange: []
});

const expenseForm = reactive({
  id: '',
  expenseCode: '',
  projectId: '',
  costType: '',
  expenseName: '',
  amount: 0,
  expenseDate: '',
  description: '',
  handler: ''
});

const expenseDetailData = reactive<any>({});

const pagination = reactive({
  current: 1,
  pageSize: 20,
  total: 45
});

const projectColumns = [
  { colKey: 'projectName', title: '项目名称', width: 200 },
  { colKey: 'totalBudget', title: '总预算', width: 120 },
  { colKey: 'usedBudget', title: '已用预算', width: 120 },
  { colKey: 'remainingBudget', title: '剩余预算', width: 120 },
  { colKey: 'usageRate', title: '使用率', width: 100 },
  { colKey: 'budgetStatus', title: '预算状态', width: 100 },
  { colKey: 'startDate', title: '开始日期', width: 120 },
  { colKey: 'endDate', title: '结束日期', width: 120 }
];

const expenseColumns = [
  { colKey: 'expenseCode', title: '费用编号', width: 130 },
  { colKey: 'costType', title: '费用类型', width: 100 },
  { colKey: 'expenseName', title: '费用名称', width: 150 },
  { colKey: 'amount', title: '金额', width: 120 },
  { colKey: 'expenseDate', title: '发生日期', width: 120 },
  { colKey: 'handler', title: '经办人', width: 100 },
  { colKey: 'operation', title: '操作', width: 150, fixed: 'right' }
];

const expenseRules = {
  projectId: [{ required: true, message: '请选择项目', type: 'error' }],
  costType: [{ required: true, message: '请选择费用类型', type: 'error' }],
  expenseName: [{ required: true, message: '请输入费用名称', type: 'error' }],
  amount: [{ required: true, message: '请输入金额', type: 'error' }],
  expenseDate: [{ required: true, message: '请选择发生日期', type: 'error' }],
  handler: [{ required: true, message: '请输入经办人', type: 'error' }]
};

const projectCostData = ref([
  {
    id: 1,
    projectId: '1',
    projectName: 'ERP系统开发',
    totalBudget: 800000,
    usedBudget: 480000,
    remainingBudget: 320000,
    usageRate: 60,
    budgetStatus: 'normal',
    startDate: '2026-01-01',
    endDate: '2026-06-30'
  },
  {
    id: 2,
    projectId: '2',
    projectName: 'CRM系统开发',
    totalBudget: 700000,
    usedBudget: 410000,
    remainingBudget: 290000,
    usageRate: 58.57,
    budgetStatus: 'normal',
    startDate: '2026-02-01',
    endDate: '2026-07-31'
  }
]);

const expenseData = ref([
  {
    id: 1,
    expenseCode: 'PE202600001',
    projectId: '1',
    projectName: 'ERP系统开发',
    costType: '人力成本',
    expenseName: '开发人员工资',
    amount: 50000,
    expenseDate: '2026-02-15',
    description: '2月份开发人员工资',
    handler: '张三',
    createTime: '2026-02-15 10:00:00'
  },
  {
    id: 2,
    expenseCode: 'PE202600002',
    projectId: '1',
    projectName: 'ERP系统开发',
    costType: '设备成本',
    expenseName: '服务器租赁费',
    amount: 3000,
    expenseDate: '2026-02-10',
    description: '测试服务器租赁费用',
    handler: '李四',
    createTime: '2026-02-10 14:00:00'
  }
]);

const handleSearch = () => {
  MessagePlugin.success('查询成功');
};

const handleReset = () => {
  Object.assign(searchForm, {
    projectName: '',
    costType: '',
    dateRange: []
  });
};

const handleRowClick = (row: any) => {
  selectedProject.value = row;
  MessagePlugin.info(`已选择项目: ${row.projectName}`);
};

const handleAddExpense = () => {
  if (!selectedProject.value) {
    MessagePlugin.warning('请先选择项目');
    return;
  }
  expenseDialogTitle.value = '新增费用';
  expenseForm.projectId = selectedProject.value.projectId;
  expenseDialogVisible.value = true;
};

const handleEditExpense = (row: any) => {
  expenseDialogTitle.value = '编辑费用';
  Object.assign(expenseForm, row);
  expenseDialogVisible.value = true;
};

const handleViewExpense = (row: any) => {
  Object.assign(expenseDetailData, row);
  expenseDetailVisible.value = true;
};

const handleDeleteExpense = (row: any) => {
  MessagePlugin.success('费用已删除');
};

const handleSaveExpense = () => {
  MessagePlugin.success(expenseDialogTitle.value === '新增费用' ? '费用已创建' : '费用已更新');
  expenseDialogVisible.value = false;
};

const handleExpenseDialogClose = () => {
  Object.assign(expenseForm, {
    id: '',
    expenseCode: '',
    projectId: '',
    costType: '',
    expenseName: '',
    amount: 0,
    expenseDate: '',
    description: '',
    handler: ''
  });
};

const handleExport = () => {
  MessagePlugin.success('报表导出成功');
};

const handleRefresh = () => {
  MessagePlugin.success('数据已刷新');
};

const handlePageChange = (pageInfo: any) => {
  pagination.current = pageInfo.current;
  pagination.pageSize = pageInfo.pageSize;
};

onMounted(() => {
  console.log('ProjectCost mounted');
});
</script>

<style scoped lang="less">
.project-cost-container {
  padding: 20px;
}

.mb-4 {
  margin-bottom: 16px;
}

.mt-4 {
  margin-top: 16px;
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

.card-header {
  margin-bottom: 16px;
  font-size: 16px;
  font-weight: 500;
}

.card-title {
  color: #333;
}

.project-name {
  color: #999;
  margin-left: 8px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}
</style>
