<template>
  <div class="project-communication-container">
    <!-- 统计卡片 -->
    <t-row :gutter="16" class="mb-4">
      <t-col :span="6">
        <t-card theme="primary">
          <t-statistic title="沟通总数" :value="statistics.total" :loading="loading">
            <template #prefix><icon-chat /></template>
          </t-statistic>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="success">
          <t-statistic title="已处理" :value="statistics.handled" :loading="loading">
            <template #prefix><icon-check-circle /></template>
          </t-statistic>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="warning">
          <t-statistic title="待处理" :value="statistics.pending" :loading="loading">
            <template #prefix><icon-time /></template>
          </t-statistic>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="danger">
          <t-statistic title="已关闭" :value="statistics.closed" :loading="loading">
            <template #prefix><icon-close-circle /></template>
          </t-statistic>
        </t-card>
      </t-col>
    </t-row>

    <!-- 搜索卡片 -->
    <t-card class="mb-4">
      <t-form :data="searchForm" layout="inline" @submit="handleSearch">
        <t-form-item label="沟通编号" name="communicationCode">
          <t-input v-model="searchForm.communicationCode" placeholder="请输入沟通编号" clearable />
        </t-form-item>
        <t-form-item label="项目名称" name="projectName">
          <t-input v-model="searchForm.projectName" placeholder="请输入项目名称" clearable />
        </t-form-item>
        <t-form-item label="沟通类型" name="communicationType">
          <t-select v-model="searchForm.communicationType" placeholder="请选择沟通类型" clearable>
            <t-option value="会议" label="会议" />
            <t-option value="邮件" label="邮件" />
            <t-option value="即时通讯" label="即时通讯" />
            <t-option value="电话" label="电话" />
          </t-select>
        </t-form-item>
        <t-form-item label="状态" name="status">
          <t-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <t-option value="pending" label="待处理" />
            <t-option value="handled" label="已处理" />
            <t-option value="closed" label="已关闭" />
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
          新增沟通
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

    <!-- 沟通列表 -->
    <t-card>
      <t-table
        :data="tableData"
        :columns="columns"
        :loading="loading"
        :pagination="pagination"
        row-key="id"
        @page-change="handlePageChange"
      >
        <template #status="{ row }">
          <t-tag v-if="row.status === 'pending'" theme="warning" variant="light">待处理</t-tag>
          <t-tag v-else-if="row.status === 'handled'" theme="success" variant="light">已处理</t-tag>
          <t-tag v-else theme="default" variant="light">已关闭</t-tag>
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
            <t-link theme="primary" @click="handleReply(row)">回复</t-link>
            <t-link theme="danger" @click="handleClose(row)">关闭</t-link>
          </t-space>
        </template>
      </t-table>
    </t-card>

    <!-- 新增/编辑沟通弹窗 -->
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
            <t-form-item label="沟通编号" name="communicationCode">
              <t-input v-model="formData.communicationCode" placeholder="自动生成" disabled />
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
            <t-form-item label="沟通类型" name="communicationType">
              <t-select v-model="formData.communicationType" placeholder="请选择沟通类型">
                <t-option value="会议" label="会议" />
                <t-option value="邮件" label="邮件" />
                <t-option value="即时通讯" label="即时通讯" />
                <t-option value="电话" label="电话" />
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
        <t-form-item label="沟通主题" name="subject">
          <t-input v-model="formData.subject" placeholder="请输入沟通主题" />
        </t-form-item>
        <t-form-item label="沟通内容" name="content">
          <t-textarea v-model="formData.content" placeholder="请输入沟通内容" :maxlength="1000" />
        </t-form-item>
        <t-form-item label="参与人员" name="participants">
          <t-select v-model="formData.participants" multiple placeholder="请选择参与人员">
            <t-option value="张三" label="张三" />
            <t-option value="李四" label="李四" />
            <t-option value="王五" label="王五" />
          </t-select>
        </t-form-item>
        <t-form-item label="附件" name="attachments">
          <t-upload v-model="formData.attachments" theme="file-input" multiple />
        </t-form-item>
      </t-form>
      <div class="dialog-footer">
        <t-button theme="default" @click="dialogVisible = false">取消</t-button>
        <t-button theme="primary" @click="handleSave">保存</t-button>
      </div>
    </t-dialog>

    <!-- 沟通详情弹窗 -->
    <t-dialog
      v-model:visible="detailVisible"
      header="沟通详情"
      width="900px"
      :footer="false"
    >
      <t-tabs v-model="detailActiveTab" theme="card">
        <t-tab-panel value="basic" label="基本信息">
          <t-descriptions :column="2" bordered>
            <t-descriptions-item label="沟通编号">{{ detailData.communicationCode }}</t-descriptions-item>
            <t-descriptions-item label="关联项目">{{ detailData.projectName }}</t-descriptions-item>
            <t-descriptions-item label="沟通类型">{{ detailData.communicationType }}</t-descriptions-item>
            <t-descriptions-item label="优先级">
              <t-tag v-if="detailData.priority === 'high'" theme="danger">高</t-tag>
              <t-tag v-else-if="detailData.priority === 'medium'" theme="warning">中</t-tag>
              <t-tag v-else>低</t-tag>
            </t-descriptions-item>
            <t-descriptions-item label="沟通主题" :span="2">{{ detailData.subject }}</t-descriptions-item>
            <t-descriptions-item label="发起人">{{ detailData.initiator }}</t-descriptions-item>
            <t-descriptions-item label="发起时间">{{ detailData.initiateTime }}</t-descriptions-item>
            <t-descriptions-item label="参与人员" :span="2">{{ detailData.participants?.join(', ') }}</t-descriptions-item>
          </t-descriptions>
        </t-tab-panel>

        <t-tab-panel value="content" label="沟通内容">
          <div class="communication-content">{{ detailData.content }}</div>
        </t-tab-panel>

        <t-tab-panel value="replies" label="回复记录">
          <t-table :data="detailData.replies || []" :columns="replyColumns" :pagination="false" />
        </t-tab-panel>

        <t-tab-panel value="logs" label="操作日志">
          <t-table :data="detailData.logs || []" :columns="logColumns" :pagination="false" />
        </t-tab-panel>
      </t-tabs>
    </t-dialog>

    <!-- 回复弹窗 -->
    <t-dialog
      v-model:visible="replyVisible"
      header="回复沟通"
      width="600px"
      :footer="false"
    >
      <t-form :data="replyForm" label-width="100px">
        <t-form-item label="回复内容" name="content">
          <t-textarea v-model="replyForm.content" placeholder="请输入回复内容" :maxlength="1000" />
        </t-form-item>
        <t-form-item label="附件" name="attachments">
          <t-upload v-model="replyForm.attachments" theme="file-input" multiple />
        </t-form-item>
      </t-form>
      <div class="dialog-footer">
        <t-button theme="default" @click="replyVisible = false">取消</t-button>
        <t-button theme="primary" @click="handleSaveReply">提交</t-button>
      </div>
    </t-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { MessagePlugin } from 'tdesign-vue-next';
import {
  IconChat, IconCheckCircle, IconTime, IconCloseCircle,
  IconAdd, IconDownload, IconRefresh
} from 'tdesign-icons-vue-next';

const loading = ref(false);
const dialogVisible = ref(false);
const detailVisible = ref(false);
const replyVisible = ref(false);
const dialogTitle = ref('新增沟通');
const detailActiveTab = ref('basic');
const formRef = ref();

const statistics = reactive({
  total: 89,
  handled: 56,
  pending: 23,
  closed: 10
});

const searchForm = reactive({
  communicationCode: '',
  projectName: '',
  communicationType: '',
  status: ''
});

const formData = reactive({
  id: '',
  communicationCode: '',
  projectId: '',
  communicationType: '',
  priority: 'medium',
  subject: '',
  content: '',
  participants: [],
  attachments: []
});

const replyForm = reactive({
  communicationId: '',
  content: '',
  attachments: []
});

const detailData = reactive<any>({});

const pagination = reactive({
  current: 1,
  pageSize: 20,
  total: 89
});

const columns = [
  { colKey: 'communicationCode', title: '沟通编号', width: 130 },
  { colKey: 'projectName', title: '关联项目', width: 150 },
  { colKey: 'communicationType', title: '沟通类型', width: 100 },
  { colKey: 'subject', title: '沟通主题', width: 200, ellipsis: true },
  { colKey: 'initiator', title: '发起人', width: 100 },
  { colKey: 'priority', title: '优先级', width: 80 },
  { colKey: 'status', title: '状态', width: 90 },
  { colKey: 'initiateTime', title: '发起时间', width: 150 },
  { colKey: 'operation', title: '操作', width: 180, fixed: 'right' }
];

const replyColumns = [
  { colKey: 'replier', title: '回复人', width: 100 },
  { colKey: 'content', title: '回复内容', width: 300, ellipsis: true },
  { colKey: 'replyTime', title: '回复时间', width: 150 }
];

const logColumns = [
  { colKey: 'operator', title: '操作人', width: 100 },
  { colKey: 'action', title: '操作内容', width: 200 },
  { colKey: 'createTime', title: '操作时间', width: 150 }
];

const rules = {
  projectId: [{ required: true, message: '请选择项目', type: 'error' }],
  communicationType: [{ required: true, message: '请选择沟通类型', type: 'error' }],
  subject: [{ required: true, message: '请输入沟通主题', type: 'error' }],
  content: [{ required: true, message: '请输入沟通内容', type: 'error' }]
};

const tableData = ref([
  {
    id: 1,
    communicationCode: 'PC202600001',
    projectName: 'ERP系统开发',
    projectId: '1',
    communicationType: '会议',
    priority: 'high',
    subject: '需求变更讨论会',
    content: '讨论ERP系统需求变更事项，涉及模块：供应链、财务',
    initiator: '张三',
    participants: ['张三', '李四', '王五'],
    status: 'pending',
    initiateTime: '2026-02-19 10:00:00'
  },
  {
    id: 2,
    communicationCode: 'PC202600002',
    projectName: 'CRM系统开发',
    projectId: '2',
    communicationType: '邮件',
    priority: 'medium',
    subject: '功能确认邮件',
    content: '确认CRM系统客户管理模块功能',
    initiator: '李四',
    participants: ['李四', '王五'],
    status: 'handled',
    initiateTime: '2026-02-18 15:30:00'
  }
]);

const handleSearch = () => {
  MessagePlugin.success('查询成功');
};

const handleReset = () => {
  Object.assign(searchForm, {
    communicationCode: '',
    projectName: '',
    communicationType: '',
    status: ''
  });
};

const handleAdd = () => {
  dialogTitle.value = '新增沟通';
  dialogVisible.value = true;
};

const handleEdit = (row: any) => {
  dialogTitle.value = '编辑沟通';
  dialogVisible.value = true;
  Object.assign(formData, row);
};

const handleView = (row: any) => {
  detailVisible.value = true;
  detailActiveTab.value = 'basic';
  Object.assign(detailData, row);
  detailData.replies = [
    { replier: '李四', content: '同意变更', replyTime: '2026-02-19 11:00:00' }
  ];
  detailData.logs = [
    { operator: '张三', action: '创建沟通', createTime: '2026-02-19 10:00:00' },
    { operator: '李四', action: '回复沟通', createTime: '2026-02-19 11:00:00' }
  ];
};

const handleReply = (row: any) => {
  replyForm.communicationId = row.id;
  replyVisible.value = true;
};

const handleClose = (row: any) => {
  MessagePlugin.success('沟通已关闭');
};

const handleSave = () => {
  MessagePlugin.success(dialogTitle.value === '新增沟通' ? '沟通已创建' : '沟通已更新');
  dialogVisible.value = false;
};

const handleSaveReply = () => {
  MessagePlugin.success('回复已提交');
  replyVisible.value = false;
};

const handleDialogClose = () => {
  Object.assign(formData, {
    id: '',
    communicationCode: '',
    projectId: '',
    communicationType: '',
    priority: 'medium',
    subject: '',
    content: '',
    participants: [],
    attachments: []
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
  console.log('ProjectCommunication mounted');
});
</script>

<style scoped lang="less">
.project-communication-container {
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

.communication-content {
  padding: 20px;
  background-color: #f5f5f5;
  border-radius: 4px;
  white-space: pre-wrap;
}
</style>
