<template>
  <div class="project-risk-container">
    <!-- 统计卡片 -->
    <t-row :gutter="16" class="mb-4">
      <t-col :span="6">
        <t-card theme="primary">
          <t-statistic title="风险总数" :value="statistics.total" :loading="loading">
            <template #prefix><icon-error-circle /></template>
          </t-statistic>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="danger">
          <t-statistic title="高风险" :value="statistics.high" :loading="loading">
            <template #prefix><icon-error /></template>
          </t-statistic>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="warning">
          <t-statistic title="中风险" :value="statistics.medium" :loading="loading">
            <template #prefix><icon-warning /></template>
          </t-statistic>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="success">
          <t-statistic title="已解决" :value="statistics.resolved" :loading="loading">
            <template #prefix><icon-check-circle /></template>
          </t-statistic>
        </t-card>
      </t-col>
    </t-row>

    <!-- 搜索卡片 -->
    <t-card class="mb-4">
      <t-form :data="searchForm" layout="inline" @submit="handleSearch">
        <t-form-item label="风险编号" name="riskCode">
          <t-input v-model="searchForm.riskCode" placeholder="请输入风险编号" clearable />
        </t-form-item>
        <t-form-item label="项目名称" name="projectName">
          <t-input v-model="searchForm.projectName" placeholder="请输入项目名称" clearable />
        </t-form-item>
        <t-form-item label="风险类型" name="riskType">
          <t-select v-model="searchForm.riskType" placeholder="请选择风险类型" clearable>
            <t-option value="技术风险" label="技术风险" />
            <t-option value="进度风险" label="进度风险" />
            <t-option value="资源风险" label="资源风险" />
            <t-option value="成本风险" label="成本风险" />
            <t-option value="质量风险" label="质量风险" />
          </t-select>
        </t-form-item>
        <t-form-item label="风险级别" name="riskLevel">
          <t-select v-model="searchForm.riskLevel" placeholder="请选择风险级别" clearable>
            <t-option value="high" label="高" />
            <t-option value="medium" label="中" />
            <t-option value="low" label="低" />
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
          新增风险
        </t-button>
        <t-button theme="default" @click="handleBatchClose">
          <template #icon><icon-check /></template>
          批量关闭
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

    <!-- 风险列表 -->
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
        <template #riskLevel="{ row }">
          <t-tag v-if="row.riskLevel === 'high'" theme="danger" variant="light">高</t-tag>
          <t-tag v-else-if="row.riskLevel === 'medium'" theme="warning" variant="light">中</t-tag>
          <t-tag v-else theme="success" variant="light">低</t-tag>
        </template>
        <template #probability="{ row }">
          <t-tag v-if="row.probability === 'high'" theme="danger">高</t-tag>
          <t-tag v-else-if="row.probability === 'medium'" theme="warning">中</t-tag>
          <t-tag v-else>低</t-tag>
        </template>
        <template #impact="{ row }">
          <t-tag v-if="row.impact === 'high'" theme="danger">高</t-tag>
          <t-tag v-else-if="row.impact === 'medium'" theme="warning">中</t-tag>
          <t-tag v-else>低</t-tag>
        </template>
        <template #status="{ row }">
          <t-tag v-if="row.status === 'open'" theme="danger" variant="light">未处理</t-tag>
          <t-tag v-else-if="row.status === 'mitigating'" theme="warning" variant="light">处理中</t-tag>
          <t-tag v-else theme="success" variant="light">已解决</t-tag>
        </template>
        <template #operation="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleView(row)">查看</t-link>
            <t-link theme="primary" @click="handleEdit(row)">编辑</t-link>
            <t-link theme="success" @click="handleMitigate(row)">应对</t-link>
            <t-link theme="danger" @click="handleClose(row)">关闭</t-link>
          </t-space>
        </template>
      </t-table>
    </t-card>

    <!-- 新增/编辑风险弹窗 -->
    <t-dialog
      v-model:visible="dialogVisible"
      :header="dialogTitle"
      width="800px"
      :footer="false"
      @close="handleDialogClose"
    >
      <t-form :data="formData" ref="formRef" :rules="rules" label-width="120px">
        <t-row :gutter="16">
          <t-col :span="12">
            <t-form-item label="风险编号" name="riskCode">
              <t-input v-model="formData.riskCode" placeholder="自动生成" disabled />
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
            <t-form-item label="风险类型" name="riskType">
              <t-select v-model="formData.riskType" placeholder="请选择风险类型">
                <t-option value="技术风险" label="技术风险" />
                <t-option value="进度风险" label="进度风险" />
                <t-option value="资源风险" label="资源风险" />
                <t-option value="成本风险" label="成本风险" />
                <t-option value="质量风险" label="质量风险" />
              </t-select>
            </t-form-item>
          </t-col>
          <t-col :span="12">
            <t-form-item label="风险级别" name="riskLevel">
              <t-select v-model="formData.riskLevel" placeholder="请选择风险级别">
                <t-option value="high" label="高" />
                <t-option value="medium" label="中" />
                <t-option value="low" label="低" />
              </t-select>
            </t-form-item>
          </t-col>
        </t-row>
        <t-row :gutter="16">
          <t-col :span="12">
            <t-form-item label="发生概率" name="probability">
              <t-select v-model="formData.probability" placeholder="请选择发生概率">
                <t-option value="high" label="高" />
                <t-option value="medium" label="中" />
                <t-option value="low" label="低" />
              </t-select>
            </t-form-item>
          </t-col>
          <t-col :span="12">
            <t-form-item label="影响程度" name="impact">
              <t-select v-model="formData.impact" placeholder="请选择影响程度">
                <t-option value="high" label="高" />
                <t-option value="medium" label="中" />
                <t-option value="low" label="低" />
              </t-select>
            </t-form-item>
          </t-col>
        </t-row>
        <t-form-item label="风险描述" name="description">
          <t-textarea v-model="formData.description" placeholder="请输入风险描述" :maxlength="1000" />
        </t-form-item>
        <t-form-item label="责任人" name="owner">
          <t-input v-model="formData.owner" placeholder="请输入责任人" />
        </t-form-item>
        <t-form-item label="应对措施" name="mitigation">
          <t-textarea v-model="formData.mitigation" placeholder="请输入应对措施" :maxlength="1000" />
        </t-form-item>
        <t-form-item label="截止日期" name="dueDate">
          <t-date-picker v-model="formData.dueDate" placeholder="请选择截止日期" />
        </t-form-item>
      </t-form>
      <div class="dialog-footer">
        <t-button theme="default" @click="dialogVisible = false">取消</t-button>
        <t-button theme="primary" @click="handleSave">保存</t-button>
      </div>
    </t-dialog>

    <!-- 风险详情弹窗 -->
    <t-dialog
      v-model:visible="detailVisible"
      header="风险详情"
      width="900px"
      :footer="false"
    >
      <t-tabs v-model="detailActiveTab" theme="card">
        <t-tab-panel value="basic" label="基本信息">
          <t-descriptions :column="2" bordered>
            <t-descriptions-item label="风险编号">{{ detailData.riskCode }}</t-descriptions-item>
            <t-descriptions-item label="关联项目">{{ detailData.projectName }}</t-descriptions-item>
            <t-descriptions-item label="风险类型">{{ detailData.riskType }}</t-descriptions-item>
            <t-descriptions-item label="风险级别">
              <t-tag v-if="detailData.riskLevel === 'high'" theme="danger">高</t-tag>
              <t-tag v-else-if="detailData.riskLevel === 'medium'" theme="warning">中</t-tag>
              <t-tag v-else theme="success">低</t-tag>
            </t-descriptions-item>
            <t-descriptions-item label="发生概率">
              <t-tag v-if="detailData.probability === 'high'" theme="danger">高</t-tag>
              <t-tag v-else-if="detailData.probability === 'medium'" theme="warning">中</t-tag>
              <t-tag v-else>低</t-tag>
            </t-descriptions-item>
            <t-descriptions-item label="影响程度">
              <t-tag v-if="detailData.impact === 'high'" theme="danger">高</t-tag>
              <t-tag v-else-if="detailData.impact === 'medium'" theme="warning">中</t-tag>
              <t-tag v-else>低</t-tag>
            </t-descriptions-item>
            <t-descriptions-item label="责任人">{{ detailData.owner }}</t-descriptions-item>
            <t-descriptions-item label="截止日期">{{ detailData.dueDate }}</t-descriptions-item>
          </t-descriptions>
        </t-tab-panel>

        <t-tab-panel value="description" label="风险描述">
          <div class="risk-content">{{ detailData.description }}</div>
        </t-tab-panel>

        <t-tab-panel value="mitigation" label="应对措施">
          <div class="risk-content">{{ detailData.mitigation }}</div>
        </t-tab-panel>

        <t-tab-panel value="history" label="处理历史">
          <t-table :data="detailData.history || []" :columns="historyColumns" :pagination="false" />
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
  IconErrorCircle, IconError, IconWarning, IconCheckCircle,
  IconAdd, IconCheck, IconDownload, IconRefresh
} from 'tdesign-icons-vue-next';

const loading = ref(false);
const dialogVisible = ref(false);
const detailVisible = ref(false);
const dialogTitle = ref('新增风险');
const detailActiveTab = ref('basic');
const formRef = ref();
const selectedRowKeys = ref([]);

const statistics = reactive({
  total: 45,
  high: 8,
  medium: 15,
  resolved: 22
});

const searchForm = reactive({
  riskCode: '',
  projectName: '',
  riskType: '',
  riskLevel: ''
});

const formData = reactive({
  id: '',
  riskCode: '',
  projectId: '',
  riskType: '',
  riskLevel: '',
  probability: '',
  impact: '',
  description: '',
  owner: '',
  mitigation: '',
  dueDate: ''
});

const detailData = reactive<any>({});

const pagination = reactive({
  current: 1,
  pageSize: 20,
  total: 45
});

const columns = [
  { colKey: 'riskCode', title: '风险编号', width: 130 },
  { colKey: 'projectName', title: '关联项目', width: 150 },
  { colKey: 'riskType', title: '风险类型', width: 100 },
  { colKey: 'description', title: '风险描述', width: 200, ellipsis: true },
  { colKey: 'riskLevel', title: '风险级别', width: 80 },
  { colKey: 'probability', title: '发生概率', width: 90 },
  { colKey: 'impact', title: '影响程度', width: 90 },
  { colKey: 'owner', title: '责任人', width: 100 },
  { colKey: 'status', title: '状态', width: 90 },
  { colKey: 'dueDate', title: '截止日期', width: 120 },
  { colKey: 'operation', title: '操作', width: 180, fixed: 'right' }
];

const historyColumns = [
  { colKey: 'action', title: '处理动作', width: 150 },
  { colKey: 'handler', title: '处理人', width: 100 },
  { colKey: 'content', title: '处理内容', width: 200, ellipsis: true },
  { colKey: 'handleTime', title: '处理时间', width: 150 }
];

const logColumns = [
  { colKey: 'operator', title: '操作人', width: 100 },
  { colKey: 'action', title: '操作内容', width: 200 },
  { colKey: 'createTime', title: '操作时间', width: 150 }
];

const rules = {
  projectId: [{ required: true, message: '请选择项目', type: 'error' }],
  riskType: [{ required: true, message: '请选择风险类型', type: 'error' }],
  riskLevel: [{ required: true, message: '请选择风险级别', type: 'error' }],
  probability: [{ required: true, message: '请选择发生概率', type: 'error' }],
  impact: [{ required: true, message: '请选择影响程度', type: 'error' }],
  description: [{ required: true, message: '请输入风险描述', type: 'error' }],
  owner: [{ required: true, message: '请输入责任人', type: 'error' }]
};

const tableData = ref([
  {
    id: 1,
    riskCode: 'PR202600001',
    projectName: 'ERP系统开发',
    projectId: '1',
    riskType: '技术风险',
    riskLevel: 'high',
    probability: 'high',
    impact: 'high',
    description: '核心技术框架存在未知漏洞，可能影响系统稳定性',
    owner: '张三',
    mitigation: '进行深度安全测试，邀请第三方安全评估',
    status: 'open',
    dueDate: '2026-03-01',
    createTime: '2026-02-19 09:00:00'
  },
  {
    id: 2,
    riskCode: 'PR202600002',
    projectName: 'CRM系统开发',
    projectId: '2',
    riskType: '进度风险',
    riskLevel: 'medium',
    probability: 'medium',
    impact: 'medium',
    description: '部分模块开发进度滞后，可能影响整体交付时间',
    owner: '李四',
    mitigation: '增加开发人员投入，调整开发计划',
    status: 'mitigating',
    dueDate: '2026-02-28',
    createTime: '2026-02-18 14:00:00'
  }
]);

const handleSearch = () => {
  MessagePlugin.success('查询成功');
};

const handleReset = () => {
  Object.assign(searchForm, {
    riskCode: '',
    projectName: '',
    riskType: '',
    riskLevel: ''
  });
};

const handleAdd = () => {
  dialogTitle.value = '新增风险';
  dialogVisible.value = true;
};

const handleEdit = (row: any) => {
  dialogTitle.value = '编辑风险';
  dialogVisible.value = true;
  Object.assign(formData, row);
};

const handleView = (row: any) => {
  detailVisible.value = true;
  detailActiveTab.value = 'basic';
  Object.assign(detailData, row);
  detailData.history = [
    { action: '创建风险', handler: '张三', content: '创建风险记录', handleTime: '2026-02-19 09:00:00' }
  ];
  detailData.logs = [
    { operator: '张三', action: '创建风险', createTime: '2026-02-19 09:00:00' }
  ];
};

const handleMitigate = (row: any) => {
  MessagePlugin.success('风险应对计划已启动');
};

const handleClose = (row: any) => {
  MessagePlugin.success('风险已关闭');
};

const handleBatchClose = () => {
  MessagePlugin.success('已批量关闭选中的风险');
};

const handleSave = () => {
  MessagePlugin.success(dialogTitle.value === '新增风险' ? '风险已创建' : '风险已更新');
  dialogVisible.value = false;
};

const handleDialogClose = () => {
  Object.assign(formData, {
    id: '',
    riskCode: '',
    projectId: '',
    riskType: '',
    riskLevel: '',
    probability: '',
    impact: '',
    description: '',
    owner: '',
    mitigation: '',
    dueDate: ''
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
  console.log('ProjectRisk mounted');
});
</script>

<style scoped lang="less">
.project-risk-container {
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

.risk-content {
  padding: 20px;
  background-color: #f5f5f5;
  border-radius: 4px;
  white-space: pre-wrap;
}
</style>
