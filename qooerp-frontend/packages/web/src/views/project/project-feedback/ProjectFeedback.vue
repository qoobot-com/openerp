<template>
  <div class="project-feedback-container">
    <!-- 统计卡片 -->
    <t-row :gutter="16" class="mb-4">
      <t-col :span="6">
        <t-card theme="primary">
          <t-statistic title="反馈总数" :value="statistics.total" :loading="loading">
            <template #prefix><icon-chat-bubble /></template>
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
        <t-form-item label="反馈编号" name="feedbackCode">
          <t-input v-model="searchForm.feedbackCode" placeholder="请输入反馈编号" clearable />
        </t-form-item>
        <t-form-item label="项目名称" name="projectName">
          <t-input v-model="searchForm.projectName" placeholder="请输入项目名称" clearable />
        </t-form-item>
        <t-form-item label="反馈类型" name="feedbackType">
          <t-select v-model="searchForm.feedbackType" placeholder="请选择反馈类型" clearable>
            <t-option value="功能建议" label="功能建议" />
            <t-option value="问题反馈" label="问题反馈" />
            <t-option value="性能问题" label="性能问题" />
            <t-option value="其他" label="其他" />
          </t-select>
        </t-form-item>
        <t-form-item label="优先级" name="priority">
          <t-select v-model="searchForm.priority" placeholder="请选择优先级" clearable>
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
          新增反馈
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

    <!-- 反馈列表 -->
    <t-card>
      <t-table
        :data="tableData"
        :columns="columns"
        :loading="loading"
        :pagination="pagination"
        row-key="id"
        @page-change="handlePageChange"
      >
        <template #priority="{ row }">
          <t-tag v-if="row.priority === 'high'" theme="danger" variant="light">高</t-tag>
          <t-tag v-else-if="row.priority === 'medium'" theme="warning" variant="light">中</t-tag>
          <t-tag v-else theme="default" variant="light">低</t-tag>
        </template>
        <template #status="{ row }">
          <t-tag v-if="row.status === 'pending'" theme="warning" variant="light">待处理</t-tag>
          <t-tag v-else-if="row.status === 'handled'" theme="success" variant="light">已处理</t-tag>
          <t-tag v-else theme="default" variant="light">已关闭</t-tag>
        </template>
        <template #operation="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleView(row)">查看</t-link>
            <t-link theme="primary" @click="handleEdit(row)">编辑</t-link>
            <t-link theme="success" @click="handleReply(row)">回复</t-link>
            <t-link v-if="row.status !== 'closed'" theme="danger" @click="handleClose(row)">关闭</t-link>
          </t-space>
        </template>
      </t-table>
    </t-card>

    <!-- 新增/编辑反馈弹窗 -->
    <t-dialog
      v-model:visible="dialogVisible"
      :header="dialogTitle"
      width="700px"
      :footer="false"
      @close="handleDialogClose"
    >
      <t-form :data="formData" ref="formRef" :rules="rules" label-width="120px">
        <t-row :gutter="16">
          <t-col :span="12">
            <t-form-item label="反馈编号" name="feedbackCode">
              <t-input v-model="formData.feedbackCode" placeholder="自动生成" disabled />
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
            <t-form-item label="反馈类型" name="feedbackType">
              <t-select v-model="formData.feedbackType" placeholder="请选择反馈类型">
                <t-option value="功能建议" label="功能建议" />
                <t-option value="问题反馈" label="问题反馈" />
                <t-option value="性能问题" label="性能问题" />
                <t-option value="其他" label="其他" />
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
        <t-form-item label="反馈标题" name="title">
          <t-input v-model="formData.title" placeholder="请输入反馈标题" />
        </t-form-item>
        <t-form-item label="反馈内容" name="content">
          <t-textarea v-model="formData.content" placeholder="请输入反馈内容" :maxlength="1000" />
        </t-form-item>
        <t-row :gutter="16">
          <t-col :span="12">
            <t-form-item label="反馈人" name="feedbacker">
              <t-input v-model="formData.feedbacker" placeholder="请输入反馈人" />
            </t-form-item>
          </t-col>
          <t-col :span="12">
            <t-form-item label="联系方式" name="contact">
              <t-input v-model="formData.contact" placeholder="请输入联系方式" />
            </t-form-item>
          </t-col>
        </t-row>
        <t-form-item label="附件" name="attachments">
          <t-upload v-model="formData.attachments" theme="file-input" multiple />
        </t-form-item>
      </t-form>
      <div class="dialog-footer">
        <t-button theme="default" @click="dialogVisible = false">取消</t-button>
        <t-button theme="primary" @click="handleSave">保存</t-button>
      </div>
    </t-dialog>

    <!-- 反馈详情弹窗 -->
    <t-dialog
      v-model:visible="detailVisible"
      header="反馈详情"
      width="900px"
      :footer="false"
    >
      <t-tabs v-model="detailActiveTab" theme="card">
        <t-tab-panel value="basic" label="基本信息">
          <t-descriptions :column="2" bordered>
            <t-descriptions-item label="反馈编号">{{ detailData.feedbackCode }}</t-descriptions-item>
            <t-descriptions-item label="关联项目">{{ detailData.projectName }}</t-descriptions-item>
            <t-descriptions-item label="反馈类型">{{ detailData.feedbackType }}</t-descriptions-item>
            <t-descriptions-item label="优先级">
              <t-tag v-if="detailData.priority === 'high'" theme="danger">高</t-tag>
              <t-tag v-else-if="detailData.priority === 'medium'" theme="warning">中</t-tag>
              <t-tag v-else>低</t-tag>
            </t-descriptions-item>
            <t-descriptions-item label="反馈标题" :span="2">{{ detailData.title }}</t-descriptions-item>
            <t-descriptions-item label="反馈人">{{ detailData.feedbacker }}</t-descriptions-item>
            <t-descriptions-item label="联系方式">{{ detailData.contact }}</t-descriptions-item>
            <t-descriptions-item label="提交时间">{{ detailData.submitTime }}</t-descriptions-item>
            <t-descriptions-item label="状态">
              <t-tag v-if="detailData.status === 'pending'" theme="warning">待处理</t-tag>
              <t-tag v-else-if="detailData.status === 'handled'" theme="success">已处理</t-tag>
              <t-tag v-else>已关闭</t-tag>
            </t-descriptions-item>
          </t-descriptions>
        </t-tab-panel>

        <t-tab-panel value="content" label="反馈内容">
          <div class="feedback-content">{{ detailData.content }}</div>
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
      v-model:visible="replyDialogVisible"
      header="回复反馈"
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
        <t-button theme="default" @click="replyDialogVisible = false">取消</t-button>
        <t-button theme="primary" @click="handleSaveReply">提交</t-button>
      </div>
    </t-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { MessagePlugin } from 'tdesign-vue-next';
import {
  IconChatBubble, IconCheckCircle, IconTime, IconCloseCircle,
  IconAdd, IconDownload, IconRefresh
} from 'tdesign-icons-vue-next';

const loading = ref(false);
const dialogVisible = ref(false);
const detailVisible = ref(false);
const replyDialogVisible = ref(false);
const dialogTitle = ref('新增反馈');
const detailActiveTab = ref('basic');
const formRef = ref();

const statistics = reactive({
  total: 67,
  handled: 45,
  pending: 18,
  closed: 4
});

const searchForm = reactive({
  feedbackCode: '',
  projectName: '',
  feedbackType: '',
  priority: ''
});

const formData = reactive({
  id: '',
  feedbackCode: '',
  projectId: '',
  feedbackType: '',
  priority: 'medium',
  title: '',
  content: '',
  feedbacker: '',
  contact: '',
  attachments: []
});

const replyForm = reactive({
  feedbackId: '',
  content: '',
  attachments: []
});

const detailData = reactive<any>({});

const pagination = reactive({
  current: 1,
  pageSize: 20,
  total: 67
});

const columns = [
  { colKey: 'feedbackCode', title: '反馈编号', width: 130 },
  { colKey: 'projectName', title: '关联项目', width: 150 },
  { colKey: 'feedbackType', title: '反馈类型', width: 100 },
  { colKey: 'title', title: '反馈标题', width: 200, ellipsis: true },
  { colKey: 'feedbacker', title: '反馈人', width: 100 },
  { colKey: 'priority', title: '优先级', width: 80 },
  { colKey: 'status', title: '状态', width: 90 },
  { colKey: 'submitTime', title: '提交时间', width: 150 },
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
  feedbackType: [{ required: true, message: '请选择反馈类型', type: 'error' }],
  priority: [{ required: true, message: '请选择优先级', type: 'error' }],
  title: [{ required: true, message: '请输入反馈标题', type: 'error' }],
  content: [{ required: true, message: '请输入反馈内容', type: 'error' }],
  feedbacker: [{ required: true, message: '请输入反馈人', type: 'error' }]
};

const tableData = ref([
  {
    id: 1,
    feedbackCode: 'FB202600001',
    projectId: '1',
    projectName: 'ERP系统开发',
    feedbackType: '功能建议',
    priority: 'medium',
    title: '建议增加批量导入功能',
    content: '希望能在库存管理模块中增加批量导入功能，方便批量录入库存数据',
    feedbacker: '张三',
    contact: '13800138000',
    status: 'pending',
    submitTime: '2026-02-19 10:00:00',
    createTime: '2026-02-19 10:00:00'
  }
]);

const handleSearch = () => {
  MessagePlugin.success('查询成功');
};

const handleReset = () => {
  Object.assign(searchForm, {
    feedbackCode: '',
    projectName: '',
    feedbackType: '',
    priority: ''
  });
};

const handleAdd = () => {
  dialogTitle.value = '新增反馈';
  dialogVisible.value = true;
};

const handleEdit = (row: any) => {
  dialogTitle.value = '编辑反馈';
  dialogVisible.value = true;
  Object.assign(formData, row);
};

const handleView = (row: any) => {
  detailVisible.value = true;
  detailActiveTab.value = 'basic';
  Object.assign(detailData, row);
  detailData.replies = [
    { replier: '管理员', content: '已收到您的建议，我们会评估后考虑是否添加此功能', replyTime: '2026-02-19 11:00:00' }
  ];
  detailData.logs = [
    { operator: '张三', action: '提交反馈', createTime: '2026-02-19 10:00:00' }
  ];
};

const handleReply = (row: any) => {
  replyForm.feedbackId = row.id;
  replyDialogVisible.value = true;
};

const handleClose = (row: any) => {
  MessagePlugin.success('反馈已关闭');
};

const handleSaveReply = () => {
  MessagePlugin.success('回复已提交');
  replyDialogVisible.value = false;
};

const handleSave = () => {
  MessagePlugin.success(dialogTitle.value === '新增反馈' ? '反馈已创建' : '反馈已更新');
  dialogVisible.value = false;
};

const handleDialogClose = () => {
  Object.assign(formData, {
    id: '',
    feedbackCode: '',
    projectId: '',
    feedbackType: '',
    priority: 'medium',
    title: '',
    content: '',
    feedbacker: '',
    contact: '',
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
  console.log('ProjectFeedback mounted');
});
</script>

<style scoped lang="less">
.project-feedback-container {
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

.feedback-content {
  padding: 20px;
  background-color: #f5f5f5;
  border-radius: 4px;
  white-space: pre-wrap;
}
</style>
