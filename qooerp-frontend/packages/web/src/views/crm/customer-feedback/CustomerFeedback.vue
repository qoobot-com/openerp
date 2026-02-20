<template>
  <div class="customer-feedback-container">
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
          <t-statistic title="好评" :value="statistics.positive" :loading="loading">
            <template #prefix><icon-thumbup /></template>
          </t-statistic>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="warning">
          <t-statistic title="中评" :value="statistics.neutral" :loading="loading">
            <template #prefix><icon-minus-circle /></template>
          </t-statistic>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="danger">
          <t-statistic title="差评" :value="statistics.negative" :loading="loading">
            <template #prefix><icon-thumbdown /></template>
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
        <t-form-item label="客户名称" name="customerName">
          <t-input v-model="searchForm.customerName" placeholder="请输入客户名称" clearable />
        </t-form-item>
        <t-form-item label="反馈类型" name="feedbackType">
          <t-select v-model="searchForm.feedbackType" placeholder="请选择反馈类型" clearable>
            <t-option value="产品建议" label="产品建议" />
            <t-option value="服务反馈" label="服务反馈" />
            <t-option value="使用问题" label="使用问题" />
            <t-option value="其他" label="其他" />
          </t-select>
        </t-form-item>
        <t-form-item label="评价等级" name="rating">
          <t-select v-model="searchForm.rating" placeholder="请选择评价等级" clearable>
            <t-option value="5" label="5星" />
            <t-option value="4" label="4星" />
            <t-option value="3" label="3星" />
            <t-option value="2" label="2星" />
            <t-option value="1" label="1星" />
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
        <template #rating="{ row }">
          <t-rate v-model="row.rating" :count="5" disabled allow-half />
        </template>
        <template #sentiment="{ row }">
          <t-tag v-if="row.sentiment === 'positive'" theme="success" variant="light">好评</t-tag>
          <t-tag v-else-if="row.sentiment === 'neutral'" theme="warning" variant="light">中评</t-tag>
          <t-tag v-else theme="danger" variant="light">差评</t-tag>
        </template>
        <template #status="{ row }">
          <t-tag v-if="row.status === 'pending'" theme="warning" variant="light">待处理</t-tag>
          <t-tag v-else-if="row.status === 'processed'" theme="success" variant="light">已处理</t-tag>
          <t-tag v-else theme="default" variant="light">已关闭</t-tag>
        </template>
        <template #operation="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleView(row)">查看</t-link>
            <t-link theme="primary" @click="handleReply(row)">回复</t-link>
            <t-link theme="danger" @click="handleDelete(row)">删除</t-link>
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
            <t-form-item label="客户名称" name="customerId">
              <t-select v-model="formData.customerId" placeholder="请选择客户" filterable>
                <t-option value="1" label="上海科技有限公司" />
                <t-option value="2" label="北京贸易公司" />
                <t-option value="3" label="深圳电子公司" />
              </t-select>
            </t-form-item>
          </t-col>
        </t-row>
        <t-row :gutter="16">
          <t-col :span="12">
            <t-form-item label="反馈类型" name="feedbackType">
              <t-select v-model="formData.feedbackType" placeholder="请选择反馈类型">
                <t-option value="产品建议" label="产品建议" />
                <t-option value="服务反馈" label="服务反馈" />
                <t-option value="使用问题" label="使用问题" />
                <t-option value="其他" label="其他" />
              </t-select>
            </t-form-item>
          </t-col>
          <t-col :span="12">
            <t-form-item label="评价等级" name="rating">
              <t-rate v-model="formData.rating" :count="5" allow-half />
            </t-form-item>
          </t-col>
        </t-row>
        <t-form-item label="反馈标题" name="title">
          <t-input v-model="formData.title" placeholder="请输入反馈标题" />
        </t-form-item>
        <t-form-item label="反馈内容" name="content">
          <t-textarea v-model="formData.content" placeholder="请输入反馈内容" :maxlength="1000" />
        </t-form-item>
        <t-form-item label="联系方式" name="contact">
          <t-input v-model="formData.contact" placeholder="请输入联系方式" />
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
            <t-descriptions-item label="客户名称">{{ detailData.customerName }}</t-descriptions-item>
            <t-descriptions-item label="反馈类型">{{ detailData.feedbackType }}</t-descriptions-item>
            <t-descriptions-item label="评价等级">
              <t-rate v-model="detailData.rating" :count="5" disabled allow-half />
            </t-descriptions-item>
            <t-descriptions-item label="反馈标题" :span="2">{{ detailData.title }}</t-descriptions-item>
            <t-descriptions-item label="联系方式">{{ detailData.contact }}</t-descriptions-item>
            <t-descriptions-item label="提交时间">{{ detailData.submitTime }}</t-descriptions-item>
            <t-descriptions-item label="情感倾向" :span="2">
              <t-tag v-if="detailData.sentiment === 'positive'" theme="success">好评</t-tag>
              <t-tag v-else-if="detailData.sentiment === 'neutral'" theme="warning">中评</t-tag>
              <t-tag v-else theme="danger">差评</t-tag>
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
  IconChatBubble, IconThumbup, IconMinusCircle, IconThumbdown,
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
  total: 89,
  positive: 56,
  neutral: 22,
  negative: 11
});

const searchForm = reactive({
  feedbackCode: '',
  customerName: '',
  feedbackType: '',
  rating: ''
});

const formData = reactive({
  id: '',
  feedbackCode: '',
  customerId: '',
  feedbackType: '',
  rating: 5,
  title: '',
  content: '',
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
  total: 89
});

const columns = [
  { colKey: 'feedbackCode', title: '反馈编号', width: 130 },
  { colKey: 'customerName', title: '客户名称', width: 150 },
  { colKey: 'feedbackType', title: '反馈类型', width: 100 },
  { colKey: 'title', title: '反馈标题', width: 200, ellipsis: true },
  { colKey: 'rating', title: '评价等级', width: 120 },
  { colKey: 'sentiment', title: '情感倾向', width: 80 },
  { colKey: 'status', title: '状态', width: 90 },
  { colKey: 'submitTime', title: '提交时间', width: 150 },
  { colKey: 'operation', title: '操作', width: 150 }
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
  customerId: [{ required: true, message: '请选择客户', type: 'error' }],
  feedbackType: [{ required: true, message: '请选择反馈类型', type: 'error' }],
  title: [{ required: true, message: '请输入反馈标题', type: 'error' }],
  content: [{ required: true, message: '请输入反馈内容', type: 'error' }]
};

const tableData = ref([
  {
    id: 1,
    feedbackCode: 'CF202600001',
    customerId: '1',
    customerName: '上海科技有限公司',
    feedbackType: '产品建议',
    rating: 5,
    title: '希望增加批量导入功能',
    content: '目前单个录入太慢，希望增加批量导入功能',
    contact: '13800138000',
    sentiment: 'positive',
    status: 'processed',
    submitTime: '2026-02-19 10:00:00',
    createTime: '2026-02-19 10:00:00'
  },
  {
    id: 2,
    feedbackCode: 'CF202600002',
    customerId: '2',
    customerName: '北京贸易公司',
    feedbackType: '服务反馈',
    rating: 3,
    title: '客服响应速度有待提升',
    content: '有时候咨询问题响应较慢，希望能够改进',
    contact: '13900139000',
    sentiment: 'neutral',
    status: 'pending',
    submitTime: '2026-02-18 15:30:00',
    createTime: '2026-02-18 15:30:00'
  }
]);

const handleSearch = () => {
  MessagePlugin.success('查询成功');
};

const handleReset = () => {
  Object.assign(searchForm, {
    feedbackCode: '',
    customerName: '',
    feedbackType: '',
    rating: ''
  });
};

const handleAdd = () => {
  dialogTitle.value = '新增反馈';
  dialogVisible.value = true;
};

const handleView = (row: any) => {
  detailVisible.value = true;
  detailActiveTab.value = 'basic';
  Object.assign(detailData, row);
  detailData.replies = [
    { replier: '客服', content: '感谢您的建议，我们会评估后考虑添加此功能', replyTime: '2026-02-19 11:00:00' }
  ];
  detailData.logs = [
    { operator: '客户', action: '提交反馈', createTime: '2026-02-19 10:00:00' }
  ];
};

const handleReply = (row: any) => {
  replyForm.feedbackId = row.id;
  replyDialogVisible.value = true;
};

const handleDelete = (row: any) => {
  MessagePlugin.success('反馈已删除');
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
    customerId: '',
    feedbackType: '',
    rating: 5,
    title: '',
    content: '',
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
  console.log('CustomerFeedback mounted');
});
</script>

<style scoped lang="less">
.customer-feedback-container {
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
