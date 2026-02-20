<template>
  <div class="project-change-container">
    <!-- 统计卡片 -->
    <t-row :gutter="16" class="mb-4">
      <t-col :span="6">
        <t-card theme="primary">
          <t-statistic title="变更总数" :value="statistics.total" :loading="loading">
            <template #prefix><icon-edit-1 /></template>
          </t-statistic>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="warning">
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
      </t-row>

    <!-- 搜索卡片 -->
    <t-card class="mb-4">
      <t-form :data="searchForm" layout="inline" @submit="handleSearch">
        <t-form-item label="变更编号" name="changeCode">
          <t-input v-model="searchForm.changeCode" placeholder="请输入变更编号" clearable />
        </t-form-item>
        <t-form-item label="项目名称" name="projectName">
          <t-input v-model="searchForm.projectName" placeholder="请输入项目名称" clearable />
        </t-form-item>
        <t-form-item label="变更类型" name="changeType">
          <t-select v-model="searchForm.changeType" placeholder="请选择变更类型" clearable>
            <t-option value="需求变更" label="需求变更" />
            <t-option value="技术变更" label="技术变更" />
            <t-option value="进度变更" label="进度变更" />
            <t-option value="资源变更" label="资源变更" />
          </t-select>
        </t-form-item>
        <t-form-item label="审批状态" name="approvalStatus">
          <t-select v-model="searchForm.approvalStatus" placeholder="请选择审批状态" clearable>
            <t-option value="pending" label="待审批" />
            <t-option value="approved" label="已批准" />
            <t-option value="rejected" label="已拒绝" />
          </t-select>
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
          新增变更
        </t-button>
        <t-button theme="default" @click="handleBatchApprove">
          <template #icon><icon-check /></template>
          批量批准
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

    <!-- 变更列表 -->
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
        <template #priority="{ row }">
          <t-tag v-if="row.priority === 'high'" theme="danger" variant="light">高</t-tag>
          <t-tag v-else-if="row.priority === 'medium'" theme="warning" variant="light">中</t-tag>
          <t-tag v-else theme="default" variant="light">低</t-tag>
        </template>
        <template #approvalStatus="{ row }">
          <t-tag v-if="row.approvalStatus === 'pending'" theme="warning" variant="light">待审批</t-tag>
          <t-tag v-else-if="row.approvalStatus === 'approved'" theme="success" variant="light">已批准</t-tag>
          <t-tag v-else theme="danger" variant="light">已拒绝</t-tag>
        </template>
        <template #operation="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleView(row)">查看</t-link>
            <t-link theme="primary" @click="handleEdit(row)">编辑</t-link>
            <t-link v-if="row.approvalStatus === 'pending'" theme="success" @click="handleApprove(row)">批准</t-link>
            <t-link v-if="row.approvalStatus === 'pending'" theme="danger" @click="handleReject(row)">拒绝</t-link>
          </t-space>
        </template>
      </t-table>
    </t-card>

    <!-- 新增/编辑变更弹窗 -->
    <t-dialog
      v-model:visible="dialogVisible"
      :header="dialogTitle"
      width="900px"
      :footer="false"
      @close="handleDialogClose"
    >
      <t-tabs v-model="activeTab" theme="card">
        <t-tab-panel value="basic" label="基本信息">
          <t-form :data="formData" ref="formRef" :rules="rules" label-width="120px">
            <t-row :gutter="16">
              <t-col :span="12">
                <t-form-item label="变更编号" name="changeCode">
                  <t-input v-model="formData.changeCode" placeholder="自动生成" disabled />
                </t-form-item>
              </t-col>
              <t-col :span="12">
                <t-form-item label="关联项目" name="projectId">
                  <t-select v-model="formData.projectId" placeholder="请选择项目">
                    <t-option value="1" label="ERP系统开发" />
                    <t-option value="2" label="CRM系统开发" />
                    <t-option value="3" label="移动端开发" />
                  </t-select>
                </t-form-item>
              </t-col>
            </t-row>
            <t-row :gutter="16">
              <t-col :span="12">
                <t-form-item label="变更类型" name="changeType">
                  <t-select v-model="formData.changeType" placeholder="请选择变更类型">
                    <t-option value="需求变更" label="需求变更" />
                    <t-option value="技术变更" label="技术变更" />
                    <t-option value="进度变更" label="进度变更" />
                    <t-option value="资源变更" label="资源变更" />
                  </t-select>
                </t-form-item>
              </t-col>
              <t-col :span="12">
                <t-form-item label="优先级" name="priority">
                  <t-select v-model="formData.priority" placeholder="请选择优先级">
                    <t-option value="high" label="高" />
                    <t-option value="medium" label="中" />
                    <t-option value="low" label="低" />
                  </t-select>
                </t-form-item>
              </t-col>
            </t-row>
            <t-form-item label="变更标题" name="title">
              <t-input v-model="formData.title" placeholder="请输入变更标题" />
            </t-form-item>
            <t-form-item label="变更原因" name="reason">
              <t-textarea v-model="formData.reason" placeholder="请输入变更原因" :maxlength="1000" />
            </t-form-item>
            <t-form-item label="变更描述" name="description">
              <t-textarea v-model="formData.description" placeholder="请输入变更描述" :maxlength="1000" />
            </t-form-item>
            <t-form-item label="发起人" name="initiator">
              <t-input v-model="formData.initiator" placeholder="请输入发起人" />
            </t-form-item>
          </t-form>
        </t-tab-panel>

        <t-tab-panel value="impact" label="影响分析">
          <t-form :data="formData" label-width="120px">
            <t-form-item label="进度影响" name="scheduleImpact">
              <t-textarea v-model="formData.scheduleImpact" placeholder="请输入进度影响说明" :maxlength="500" />
            </t-form-item>
            <t-form-item label="成本影响" name="costImpact">
              <t-input-number v-model="formData.costImpact" placeholder="请输入成本影响金额" :min="0" />
            </t-form-item>
            <t-form-item label="质量影响" name="qualityImpact">
              <t-textarea v-model="formData.qualityImpact" placeholder="请输入质量影响说明" :maxlength="500" />
            </t-form-item>
            <t-form-item label="资源影响" name="resourceImpact">
              <t-textarea v-model="formData.resourceImpact" placeholder="请输入资源影响说明" :maxlength="500" />
            </t-form-item>
          </t-form>
        </t-tab-panel>

        <t-tab-panel value="solution" label="解决方案">
          <t-form :data="formData" label-width="120px">
            <t-form-item label="解决方案" name="solution">
              <t-textarea v-model="formData.solution" placeholder="请输入解决方案" :maxlength="1000" />
            </t-form-item>
            <t-form-item label="实施计划" name="implementationPlan">
              <t-textarea v-model="formData.implementationPlan" placeholder="请输入实施计划" :maxlength="1000" />
            </t-form-item>
            <t-form-item label="预计完成日期" name="estimatedCompleteDate">
              <t-date-picker v-model="formData.estimatedCompleteDate" placeholder="请选择预计完成日期" />
            </t-form-item>
          </t-form>
        </t-tab-panel>
      </t-tabs>

      <div class="dialog-footer">
        <t-button theme="default" @click="dialogVisible = false">取消</t-button>
        <t-button theme="primary" @click="handleSubmit">提交审批</t-button>
      </div>
    </t-dialog>

    <!-- 变更详情弹窗 -->
    <t-dialog
      v-model:visible="detailVisible"
      header="变更详情"
      width="1000px"
      :footer="false"
    >
      <t-tabs v-model="detailActiveTab" theme="card">
        <t-tab-panel value="basic" label="基本信息">
          <t-descriptions :column="2" bordered>
            <t-descriptions-item label="变更编号">{{ detailData.changeCode }}</t-descriptions-item>
            <t-descriptions-item label="关联项目">{{ detailData.projectName }}</t-descriptions-item>
            <t-descriptions-item label="变更类型">{{ detailData.changeType }}</t-descriptions-item>
            <t-descriptions-item label="优先级">
              <t-tag v-if="detailData.priority === 'high'" theme="danger">高</t-tag>
              <t-tag v-else-if="detailData.priority === 'medium'" theme="warning">中</t-tag>
              <t-tag v-else>低</t-tag>
            </t-descriptions-item>
            <t-descriptions-item label="变更标题" :span="2">{{ detailData.title }}</t-descriptions-item>
            <t-descriptions-item label="发起人">{{ detailData.initiator }}</t-descriptions-item>
            <t-descriptions-item label="发起时间">{{ detailData.createTime }}</t-descriptions-item>
            <t-descriptions-item label="审批状态" :span="2">
              <t-tag v-if="detailData.approvalStatus === 'pending'" theme="warning">待审批</t-tag>
              <t-tag v-else-if="detailData.approvalStatus === 'approved'" theme="success">已批准</t-tag>
              <t-tag v-else theme="danger">已拒绝</t-tag>
            </t-descriptions-item>
          </t-descriptions>
        </t-tab-panel>

        <t-tab-panel value="reason" label="变更原因">
          <div class="change-content">{{ detailData.reason }}</div>
        </t-tab-panel>

        <t-tab-panel value="description" label="变更描述">
          <div class="change-content">{{ detailData.description }}</div>
        </t-tab-panel>

        <t-tab-panel value="impact" label="影响分析">
          <t-descriptions :column="1" bordered>
            <t-descriptions-item label="进度影响">{{ detailData.scheduleImpact }}</t-descriptions-item>
            <t-descriptions-item label="成本影响">{{ detailData.costImpact }} 元</t-descriptions-item>
            <t-descriptions-item label="质量影响">{{ detailData.qualityImpact }}</t-descriptions-item>
            <t-descriptions-item label="资源影响">{{ detailData.resourceImpact }}</t-descriptions-item>
          </t-descriptions>
        </t-tab-panel>

        <t-tab-panel value="solution" label="解决方案">
          <t-descriptions :column="1" bordered>
            <t-descriptions-item label="解决方案">{{ detailData.solution }}</t-descriptions-item>
            <t-descriptions-item label="实施计划">{{ detailData.implementationPlan }}</t-descriptions-item>
            <t-descriptions-item label="预计完成日期">{{ detailData.estimatedCompleteDate }}</t-descriptions-item>
          </t-descriptions>
        </t-tab-panel>

        <t-tab-panel value="approval" label="审批记录">
          <t-table :data="detailData.approvals || []" :columns="approvalColumns" :pagination="false" />
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
  IconEdit1, IconTime, IconCheckCircle, IconCloseCircle,
  IconAdd, IconCheck, IconDownload, IconRefresh
} from 'tdesign-icons-vue-next';

const loading = ref(false);
const dialogVisible = ref(false);
const detailVisible = ref(false);
const dialogTitle = ref('新增变更');
const activeTab = ref('basic');
const detailActiveTab = ref('basic');
const formRef = ref();
const selectedRowKeys = ref([]);

const statistics = reactive({
  total: 56,
  pending: 18,
  approved: 32,
  rejected: 6
});

const searchForm = reactive({
  changeCode: '',
  projectName: '',
  changeType: '',
  approvalStatus: ''
});

const formData = reactive({
  id: '',
  changeCode: '',
  projectId: '',
  changeType: '',
  priority: 'medium',
  title: '',
  reason: '',
  description: '',
  initiator: '',
  scheduleImpact: '',
  costImpact: 0,
  qualityImpact: '',
  resourceImpact: '',
  solution: '',
  implementationPlan: '',
  estimatedCompleteDate: ''
});

const detailData = reactive<any>({});

const pagination = reactive({
  current: 1,
  pageSize: 20,
  total: 56
});

const columns = [
  { colKey: 'changeCode', title: '变更编号', width: 130 },
  { colKey: 'projectName', title: '关联项目', width: 150 },
  { colKey: 'changeType', title: '变更类型', width: 100 },
  { colKey: 'title', title: '变更标题', width: 200, ellipsis: true },
  { colKey: 'priority', title: '优先级', width: 80 },
  { colKey: 'initiator', title: '发起人', width: 100 },
  { colKey: 'approvalStatus', title: '审批状态', width: 90 },
  { colKey: 'createTime', title: '发起时间', width: 150 },
  { colKey: 'operation', title: '操作', width: 200, fixed: 'right' }
];

const approvalColumns = [
  { colKey: 'approver', title: '审批人', width: 100 },
  { colKey: 'status', title: '审批结果', width: 100 },
  { colKey: 'comment', title: '审批意见', width: 200 },
  { colKey: 'approveTime', title: '审批时间', width: 150 }
];

const logColumns = [
  { colKey: 'operator', title: '操作人', width: 100 },
  { colKey: 'action', title: '操作内容', width: 200 },
  { colKey: 'createTime', title: '操作时间', width: 150 }
];

const rules = {
  projectId: [{ required: true, message: '请选择项目', type: 'error' }],
  changeType: [{ required: true, message: '请选择变更类型', type: 'error' }],
  priority: [{ required: true, message: '请选择优先级', type: 'error' }],
  title: [{ required: true, message: '请输入变更标题', type: 'error' }],
  reason: [{ required: true, message: '请输入变更原因', type: 'error' }],
  description: [{ required: true, message: '请输入变更描述', type: 'error' }],
  initiator: [{ required: true, message: '请输入发起人', type: 'error' }]
};

const tableData = ref([
  {
    id: 1,
    changeCode: 'PC202600001',
    projectName: 'ERP系统开发',
    projectId: '1',
    changeType: '需求变更',
    priority: 'high',
    title: '增加库存预警功能',
    reason: '客户要求增加库存预警功能，以提高库存管理效率',
    description: '在现有库存管理模块基础上，增加库存预警功能',
    initiator: '张三',
    approvalStatus: 'pending',
    scheduleImpact: '预计增加5天开发时间',
    costImpact: 50000,
    qualityImpact: '需进行额外测试',
    resourceImpact: '需增加1名开发人员',
    solution: '新增库存预警配置页面和预警规则引擎',
    implementationPlan: '分三个阶段实施：需求确认、开发实现、测试验收',
    estimatedCompleteDate: '2026-03-15',
    createTime: '2026-02-19 10:00:00'
  }
]);

const handleSearch = () => {
  MessagePlugin.success('查询成功');
};

const handleReset = () => {
  Object.assign(searchForm, {
    changeCode: '',
    projectName: '',
    changeType: '',
    approvalStatus: ''
  });
};

const handleAdd = () => {
  dialogTitle.value = '新增变更';
  dialogVisible.value = true;
  activeTab.value = 'basic';
};

const handleEdit = (row: any) => {
  dialogTitle.value = '编辑变更';
  dialogVisible.value = true;
  activeTab.value = 'basic';
  Object.assign(formData, row);
};

const handleView = (row: any) => {
  detailVisible.value = true;
  detailActiveTab.value = 'basic';
  Object.assign(detailData, row);
  detailData.approvals = [
    { approver: '项目经理', status: 'pending', comment: '', approveTime: '' }
  ];
  detailData.logs = [
    { operator: '张三', action: '创建变更申请', createTime: '2026-02-19 10:00:00' }
  ];
};

const handleApprove = (row: any) => {
  MessagePlugin.success('变更已批准');
};

const handleReject = (row: any) => {
  MessagePlugin.success('变更已拒绝');
};

const handleBatchApprove = () => {
  MessagePlugin.success('已批量批准选中的变更');
};

const handleSubmit = () => {
  MessagePlugin.success('变更申请已提交审批');
  dialogVisible.value = false;
};

const handleDialogClose = () => {
  Object.assign(formData, {
    id: '',
    changeCode: '',
    projectId: '',
    changeType: '',
    priority: 'medium',
    title: '',
    reason: '',
    description: '',
    initiator: '',
    scheduleImpact: '',
    costImpact: 0,
    qualityImpact: '',
    resourceImpact: '',
    solution: '',
    implementationPlan: '',
    estimatedCompleteDate: ''
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
  console.log('ProjectChange mounted');
});
</script>

<style scoped lang="less">
.project-change-container {
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

.change-content {
  padding: 20px;
  background-color: #f5f5f5;
  border-radius: 4px;
  white-space: pre-wrap;
}
</style>
