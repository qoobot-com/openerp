<template>
  <div class="project-approval-container">
    <!-- 统计卡片 -->
    <t-row :gutter="16" class="mb-4">
      <t-col :span="6">
        <t-card theme="primary">
          <t-statistic title="待审批" :value="statistics.pending" :loading="loading">
            <template #prefix><icon-time /></template>
          </t-statistic>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="success">
          <t-statistic title="已批准" :value="statistics.approved" :loading="loading">
            <template #prefix><icon-check-circle /></template>
          </t-statistic>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="danger">
          <t-statistic title="已拒绝" :value="statistics.rejected" :loading="loading">
            <template #prefix><icon-close-circle /></template>
          </t-statistic>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="warning">
          <t-statistic title="已撤销" :value="statistics.withdrawn" :loading="loading">
            <template #prefix><icon-rollback /></template>
          </t-statistic>
        </t-card>
      </t-col>
    </t-row>

    <!-- 搜索卡片 -->
    <t-card class="mb-4">
      <t-form :data="searchForm" layout="inline" @submit="handleSearch">
        <t-form-item label="审批编号" name="approvalCode">
          <t-input v-model="searchForm.approvalCode" placeholder="请输入审批编号" clearable />
        </t-form-item>
        <t-form-item label="审批类型" name="approvalType">
          <t-select v-model="searchForm.approvalType" placeholder="请选择审批类型" clearable>
            <t-option value="项目立项" label="项目立项" />
            <t-option value="需求变更" label="需求变更" />
            <t-option value="里程碑" label="里程碑" />
            <t-option value="项目收尾" label="项目收尾" />
          </t-select>
        </t-form-item>
        <t-form-item label="审批状态" name="status">
          <t-select v-model="searchForm.status" placeholder="请选择审批状态" clearable>
            <t-option value="pending" label="待审批" />
            <t-option value="approved" label="已批准" />
            <t-option value="rejected" label="已拒绝" />
            <t-option value="withdrawn" label="已撤销" />
          </t-select>
        </t-form-item>
        <t-form-item label="发起人" name="initiator">
          <t-input v-model="searchForm.initiator" placeholder="请输入发起人" clearable />
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
        <t-button theme="success" @click="handleBatchApprove">
          <template #icon><icon-check /></template>
          批量批准
        </t-button>
        <t-button theme="danger" @click="handleBatchReject">
          <template #icon><icon-close /></template>
          批量拒绝
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

    <!-- 审批列表 -->
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
        <template #status="{ row }">
          <t-tag v-if="row.status === 'pending'" theme="warning" variant="light">待审批</t-tag>
          <t-tag v-else-if="row.status === 'approved'" theme="success" variant="light">已批准</t-tag>
          <t-tag v-else-if="row.status === 'rejected'" theme="danger" variant="light">已拒绝</t-tag>
          <t-tag v-else theme="default" variant="light">已撤销</t-tag>
        </template>
        <template #priority="{ row }">
          <t-tag v-if="row.priority === 'high'" theme="danger" variant="light">高</t-tag>
          <t-tag v-else-if="row.priority === 'medium'" theme="warning" variant="light">中</t-tag>
          <t-tag v-else theme="default" variant="light">低</t-tag>
        </template>
        <template #operation="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleView(row)">查看</t-link>
            <t-link v-if="row.status === 'pending'" theme="success" @click="handleApprove(row)">批准</t-link>
            <t-link v-if="row.status === 'pending'" theme="danger" @click="handleReject(row)">拒绝</t-link>
            <t-link v-if="row.status === 'pending'" theme="warning" @click="handleWithdraw(row)">撤销</t-link>
          </t-space>
        </template>
      </t-table>
    </t-card>

    <!-- 审批详情弹窗 -->
    <t-dialog
      v-model:visible="detailVisible"
      header="审批详情"
      width="900px"
      :footer="false"
    >
      <t-tabs v-model="detailActiveTab" theme="card">
        <t-tab-panel value="basic" label="基本信息">
          <t-descriptions :column="2" bordered>
            <t-descriptions-item label="审批编号">{{ detailData.approvalCode }}</t-descriptions-item>
            <t-descriptions-item label="审批类型">{{ detailData.approvalType }}</t-descriptions-item>
            <t-descriptions-item label="关联项目">{{ detailData.projectName }}</t-descriptions-item>
            <t-descriptions-item label="优先级">
              <t-tag v-if="detailData.priority === 'high'" theme="danger">高</t-tag>
              <t-tag v-else-if="detailData.priority === 'medium'" theme="warning">中</t-tag>
              <t-tag v-else>低</t-tag>
            </t-descriptions-item>
            <t-descriptions-item label="发起人">{{ detailData.initiator }}</t-descriptions-item>
            <t-descriptions-item label="发起时间">{{ detailData.createTime }}</t-descriptions-item>
            <t-descriptions-item label="审批状态" :span="2">
              <t-tag v-if="detailData.status === 'pending'" theme="warning">待审批</t-tag>
              <t-tag v-else-if="detailData.status === 'approved'" theme="success">已批准</t-tag>
              <t-tag v-else-if="detailData.status === 'rejected'" theme="danger">已拒绝</t-tag>
              <t-tag v-else>已撤销</t-tag>
            </t-descriptions-item>
          </t-descriptions>
        </t-tab-panel>

        <t-tab-panel value="content" label="审批内容">
          <div class="approval-content">{{ detailData.content }}</div>
        </t-tab-panel>

        <t-tab-panel value="flow" label="审批流程">
          <t-steps :current="detailData.currentStep" direction="vertical">
            <t-step-item v-for="(step, index) in detailData.flowSteps" :key="index" :title="step.title" :content="step.content">
              <template #icon>
                <icon-check-circle v-if="step.status === 'completed'" />
                <icon-time v-else-if="step.status === 'pending'" />
                <icon-close-circle v-else />
              </template>
            </t-step-item>
          </t-steps>
        </t-tab-panel>

        <t-tab-panel value="history" label="审批记录">
          <t-table :data="detailData.approvalHistory || []" :columns="historyColumns" :pagination="false" />
        </t-tab-panel>

        <t-tab-panel value="logs" label="操作日志">
          <t-table :data="detailData.logs || []" :columns="logColumns" :pagination="false" />
        </t-tab-panel>
      </t-tabs>
    </t-dialog>

    <!-- 审批操作弹窗 -->
    <t-dialog
      v-model:visible="actionDialogVisible"
      :header="actionDialogTitle"
      width="600px"
      :footer="false"
    >
      <t-form :data="actionForm" label-width="100px">
        <t-form-item label="审批意见">
          <t-textarea v-model="actionForm.comment" placeholder="请输入审批意见" :maxlength="500" />
        </t-form-item>
      </t-form>
      <div class="dialog-footer">
        <t-button theme="default" @click="actionDialogVisible = false">取消</t-button>
        <t-button theme="primary" @click="handleConfirmAction">确定</t-button>
      </div>
    </t-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { MessagePlugin } from 'tdesign-vue-next';
import {
  IconTime, IconCheckCircle, IconCloseCircle, IconRollback,
  IconCheck, IconClose, IconDownload, IconRefresh
} from 'tdesign-icons-vue-next';

const loading = ref(false);
const detailVisible = ref(false);
const actionDialogVisible = ref(false);
const actionDialogTitle = ref('');
const detailActiveTab = ref('basic');
const selectedRowKeys = ref([]);
let currentAction = '';
let currentRow: any = null;

const statistics = reactive({
  pending: 15,
  approved: 68,
  rejected: 8,
  withdrawn: 3
});

const searchForm = reactive({
  approvalCode: '',
  approvalType: '',
  status: '',
  initiator: ''
});

const actionForm = reactive({
  comment: ''
});

const detailData = reactive<any>({});

const pagination = reactive({
  current: 1,
  pageSize: 20,
  total: 94
});

const columns = [
  { colKey: 'approvalCode', title: '审批编号', width: 130 },
  { colKey: 'approvalType', title: '审批类型', width: 100 },
  { colKey: 'projectName', title: '关联项目', width: 150 },
  { colKey: 'title', title: '审批标题', width: 200, ellipsis: true },
  { colKey: 'initiator', title: '发起人', width: 100 },
  { colKey: 'priority', title: '优先级', width: 80 },
  { colKey: 'status', title: '审批状态', width: 90 },
  { colKey: 'createTime', title: '发起时间', width: 150 },
  { colKey: 'operation', title: '操作', width: 200, fixed: 'right' }
];

const historyColumns = [
  { colKey: 'approver', title: '审批人', width: 100 },
  { colKey: 'result', title: '审批结果', width: 100 },
  { colKey: 'comment', title: '审批意见', width: 200 },
  { colKey: 'approveTime', title: '审批时间', width: 150 }
];

const logColumns = [
  { colKey: 'operator', title: '操作人', width: 100 },
  { colKey: 'action', title: '操作内容', width: 200 },
  { colKey: 'createTime', title: '操作时间', width: 150 }
];

const tableData = ref([
  {
    id: 1,
    approvalCode: 'PA202600001',
    approvalType: '项目立项',
    projectName: 'ERP系统开发',
    projectId: '1',
    title: 'ERP系统开发项目立项申请',
    initiator: '张三',
    priority: 'high',
    status: 'pending',
    content: '申请立项ERP系统开发项目，项目周期6个月，预算80万元',
    currentStep: 1,
    flowSteps: [
      { title: '部门主管', content: '王五', status: 'completed' },
      { title: '项目经理', content: '李四', status: 'pending' },
      { title: '总经理', content: '赵六', status: 'pending' }
    ],
    approvalHistory: [
      { approver: '王五', result: '已批准', comment: '同意立项', approveTime: '2026-02-19 10:00:00' }
    ],
    logs: [
      { operator: '张三', action: '发起审批', createTime: '2026-02-18 09:00:00' },
      { operator: '王五', action: '批准申请', createTime: '2026-02-19 10:00:00' }
    ],
    createTime: '2026-02-18 09:00:00'
  }
]);

const handleSearch = () => {
  MessagePlugin.success('查询成功');
};

const handleReset = () => {
  Object.assign(searchForm, {
    approvalCode: '',
    approvalType: '',
    status: '',
    initiator: ''
  });
};

const handleView = (row: any) => {
  detailVisible.value = true;
  detailActiveTab.value = 'basic';
  Object.assign(detailData, row);
};

const handleApprove = (row: any) => {
  currentAction = 'approve';
  currentRow = row;
  actionDialogTitle.value = '批准审批';
  actionForm.comment = '';
  actionDialogVisible.value = true;
};

const handleReject = (row: any) => {
  currentAction = 'reject';
  currentRow = row;
  actionDialogTitle.value = '拒绝审批';
  actionForm.comment = '';
  actionDialogVisible.value = true;
};

const handleWithdraw = (row: any) => {
  MessagePlugin.success('审批已撤销');
};

const handleBatchApprove = () => {
  if (selectedRowKeys.value.length === 0) {
    MessagePlugin.warning('请选择要批准的审批');
    return;
  }
  MessagePlugin.success('已批量批准选中的审批');
};

const handleBatchReject = () => {
  if (selectedRowKeys.value.length === 0) {
    MessagePlugin.warning('请选择要拒绝的审批');
    return;
  }
  MessagePlugin.success('已批量拒绝选中的审批');
};

const handleConfirmAction = () => {
  if (currentAction === 'approve') {
    MessagePlugin.success('审批已批准');
  } else if (currentAction === 'reject') {
    MessagePlugin.success('审批已拒绝');
  }
  actionDialogVisible.value = false;
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
  console.log('ProjectApproval mounted');
});
</script>

<style scoped lang="less">
.project-approval-container {
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

.approval-content {
  padding: 20px;
  background-color: #f5f5f5;
  border-radius: 4px;
  white-space: pre-wrap;
}
</style>
