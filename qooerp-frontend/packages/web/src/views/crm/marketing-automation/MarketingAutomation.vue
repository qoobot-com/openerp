<template>
  <div class="marketing-automation-container">
    <!-- 统计卡片 -->
    <t-row :gutter="16" class="mb-4">
      <t-col :span="6">
        <t-card theme="primary">
          <t-statistic title="活动总数" :value="statistics.total" :loading="loading">
            <template #prefix><icon-bill /></template>
          </t-statistic>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="success">
          <t-statistic title="进行中" :value="statistics.active" :loading="loading">
            <template #prefix><icon-play-circle /></template>
          </t-statistic>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="warning">
          <t-statistic title="已发送" :value="statistics.sent" :loading="loading">
            <template #prefix><icon-mail /></template>
          </t-statistic>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="danger">
          <t-statistic title="已暂停" :value="statistics.paused" :loading="loading">
            <template #prefix><icon-pause-circle /></template>
          </t-statistic>
        </t-card>
      </t-col>
    </t-row>

    <!-- 搜索卡片 -->
    <t-card class="mb-4">
      <t-form :data="searchForm" layout="inline" @submit="handleSearch">
        <t-form-item label="活动名称" name="name">
          <t-input v-model="searchForm.name" placeholder="请输入活动名称" clearable />
        </t-form-item>
        <t-form-item label="活动类型" name="type">
          <t-select v-model="searchForm.type" placeholder="请选择活动类型" clearable>
            <t-option value="邮件营销" label="邮件营销" />
            <t-option value="短信营销" label="短信营销" />
            <t-option value="微信营销" label="微信营销" />
            <t-option value="推送通知" label="推送通知" />
          </t-select>
        </t-form-item>
        <t-form-item label="活动状态" name="status">
          <t-select v-model="searchForm.status" placeholder="请选择活动状态" clearable>
            <t-option value="draft" label="草稿" />
            <t-option value="active" label="进行中" />
            <t-option value="paused" label="已暂停" />
            <t-option value="completed" label="已完成" />
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
          新建活动
        </t-button>
        <t-button theme="default" @click="handleBatchPause">
          <template #icon><icon-pause-circle /></template>
          批量暂停
        </t-button>
      </div>
      <t-button theme="default" variant="outline" @click="handleRefresh">
        <template #icon><icon-refresh /></template>
        刷新
      </t-button>
    </div>

    <!-- 活动列表 -->
    <t-card>
      <t-table
        :data="tableData"
        :columns="columns"
        :loading="loading"
        :pagination="pagination"
        row-key="id"
        @page-change="handlePageChange"
      >
        <template #type="{ row }">
          <t-tag v-if="row.type === '邮件营销'" theme="primary" variant="light">邮件营销</t-tag>
          <t-tag v-else-if="row.type === '短信营销'" theme="success" variant="light">短信营销</t-tag>
          <t-tag v-else-if="row.type === '微信营销'" theme="warning" variant="light">微信营销</t-tag>
          <t-tag v-else theme="default" variant="light">推送通知</t-tag>
        </template>
        <template #status="{ row }">
          <t-tag v-if="row.status === 'draft'" theme="default" variant="light">草稿</t-tag>
          <t-tag v-else-if="row.status === 'active'" theme="success" variant="light">进行中</t-tag>
          <t-tag v-else-if="row.status === 'paused'" theme="warning" variant="light">已暂停</t-tag>
          <t-tag v-else theme="default" variant="light">已完成</t-tag>
        </template>
        <template #triggerType="{ row }">
          <t-tag v-if="row.triggerType === 'immediate'" variant="light">立即发送</t-tag>
          <t-tag v-else-if="row.triggerType === 'scheduled'" theme="primary" variant="light">定时发送</t-tag>
          <t-tag v-else theme="success" variant="light">触发式</t-tag>
        </template>
        <template #operation="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleView(row)">查看</t-link>
            <t-link theme="primary" @click="handleEdit(row)">编辑</t-link>
            <t-link v-if="row.status === 'active'" theme="warning" @click="handlePause(row)">暂停</t-link>
            <t-link v-if="row.status === 'paused'" theme="success" @click="handleResume(row)">恢复</t-link>
            <t-link theme="danger" @click="handleDelete(row)">删除</t-link>
          </t-space>
        </template>
      </t-table>
    </t-card>

    <!-- 新增/编辑活动弹窗 -->
    <t-dialog
      v-model:visible="dialogVisible"
      :header="dialogTitle"
      width="900px"
      :footer="false"
      @close="handleDialogClose"
    >
      <t-tabs v-model="dialogActiveTab" theme="card">
        <t-tab-panel value="basic" label="基本信息">
          <t-form :data="formData" ref="formRef" :rules="rules" label-width="120px">
            <t-row :gutter="16">
              <t-col :span="12">
                <t-form-item label="活动名称" name="name">
                  <t-input v-model="formData.name" placeholder="请输入活动名称" />
                </t-form-item>
              </t-col>
              <t-col :span="12">
                <t-form-item label="活动类型" name="type">
                  <t-select v-model="formData.type" placeholder="请选择活动类型">
                    <t-option value="邮件营销" label="邮件营销" />
                    <t-option value="短信营销" label="短信营销" />
                    <t-option value="微信营销" label="微信营销" />
                    <t-option value="推送通知" label="推送通知" />
                  </t-select>
                </t-form-item>
              </t-col>
            </t-row>
            <t-row :gutter="16">
              <t-col :span="12">
                <t-form-item label="触发类型" name="triggerType">
                  <t-select v-model="formData.triggerType" placeholder="请选择触发类型" @change="handleTriggerTypeChange">
                    <t-option value="immediate" label="立即发送" />
                    <t-option value="scheduled" label="定时发送" />
                    <t-option value="event" label="触发式" />
                  </t-select>
                </t-form-item>
              </t-col>
              <t-col :span="12" v-if="formData.triggerType === 'scheduled'">
                <t-form-item label="发送时间" name="scheduleTime">
                  <t-date-picker v-model="formData.scheduleTime" placeholder="请选择发送时间" enable-time-picker />
                </t-form-item>
              </t-col>
              <t-col :span="12" v-if="formData.triggerType === 'event'">
                <t-form-item label="触发事件" name="triggerEvent">
                  <t-select v-model="formData.triggerEvent" placeholder="请选择触发事件">
                    <t-option value="customer_register" label="客户注册" />
                    <t-option value="order_complete" label="订单完成" />
                    <t-option value="product_view" label="产品浏览" />
                    <t-option value="cart_abandon" label="购物车遗弃" />
                  </t-select>
                </t-form-item>
              </t-col>
            </t-row>
            <t-form-item label="目标客户" name="targetCustomers">
              <t-select v-model="formData.targetCustomers" placeholder="请选择目标客户分段" multiple>
                <t-option value="1" label="VIP客户" />
                <t-option value="2" label="高价值客户" />
                <t-option value="3" label="新注册客户" />
                <t-option value="4" label="活跃客户" />
              </t-select>
            </t-form-item>
            <t-form-item label="活动描述" name="description">
              <t-textarea v-model="formData.description" placeholder="请输入活动描述" :maxlength="500" />
            </t-form-item>
          </t-form>
        </t-tab-panel>

        <t-tab-panel value="content" label="内容设置">
          <t-form :data="formData" label-width="120px">
            <t-form-item label="消息标题" name="title">
              <t-input v-model="formData.title" placeholder="请输入消息标题" />
            </t-form-item>
            <t-form-item label="消息内容" name="content">
              <t-textarea v-model="formData.content" placeholder="请输入消息内容" :maxlength="2000" :autosize="{ minRows: 6 }" />
            </t-form-item>
            <t-form-item label="跳转链接" name="link">
              <t-input v-model="formData.link" placeholder="请输入跳转链接（可选）" />
            </t-form-item>
            <t-form-item label="附件" name="attachments">
              <t-upload v-model="formData.attachments" theme="file-input" multiple />
            </t-form-item>
          </t-form>
        </t-tab-panel>

        <t-tab-panel value="rule" label="触发规则">
          <t-form :data="formData" label-width="120px">
            <t-form-item label="重复频率" name="frequency">
              <t-radio-group v-model="formData.frequency">
                <t-radio value="once">仅一次</t-radio>
                <t-radio value="daily">每天</t-radio>
                <t-radio value="weekly">每周</t-radio>
                <t-radio value="monthly">每月</t-radio>
              </t-radio-group>
            </t-form-item>
            <t-form-item label="冷却时间" name="cooldown">
              <t-input-number v-model="formData.cooldown" :min="0" :suffix="'天'" />
              <span class="ml-2 text-sm text-gray-500">同一客户在此期间内不会重复收到</span>
            </t-form-item>
            <t-form-item label="发送时间段" name="timeRange">
              <t-time-range-picker v-model="formData.timeRange" placeholder="请选择发送时间段" />
            </t-form-item>
          </t-form>
        </t-tab-panel>
      </t-tabs>
      <div class="dialog-footer">
        <t-button theme="default" @click="dialogVisible = false">取消</t-button>
        <t-button theme="primary" @click="handleSave">保存</t-button>
      </div>
    </t-dialog>

    <!-- 活动详情弹窗 -->
    <t-dialog
      v-model:visible="detailVisible"
      header="活动详情"
      width="900px"
      :footer="false"
    >
      <t-tabs v-model="detailActiveTab" theme="card">
        <t-tab-panel value="basic" label="基本信息">
          <t-descriptions :column="2" bordered>
            <t-descriptions-item label="活动名称" :span="2">{{ detailData.name }}</t-descriptions-item>
            <t-descriptions-item label="活动类型">{{ detailData.type }}</t-descriptions-item>
            <t-descriptions-item label="活动状态">
              <t-tag v-if="detailData.status === 'active'" theme="success">进行中</t-tag>
              <t-tag v-else-if="detailData.status === 'paused'" theme="warning">已暂停</t-tag>
              <t-tag v-else-if="detailData.status === 'completed'" theme="default">已完成</t-tag>
              <t-tag v-else theme="default">草稿</t-tag>
            </t-descriptions-item>
            <t-descriptions-item label="触发类型">{{ getTriggerTypeName(detailData.triggerType) }}</t-descriptions-item>
            <t-descriptions-item label="创建时间">{{ detailData.createTime }}</t-descriptions-item>
            <t-descriptions-item label="活动描述" :span="2">{{ detailData.description || '-' }}</t-descriptions-item>
          </t-descriptions>
        </t-tab-panel>

        <t-tab-panel value="content" label="内容设置">
          <t-descriptions :column="1" bordered>
            <t-descriptions-item label="消息标题">{{ detailData.title }}</t-descriptions-item>
            <t-descriptions-item label="消息内容">{{ detailData.content }}</t-descriptions-item>
            <t-descriptions-item label="跳转链接">{{ detailData.link || '-' }}</t-descriptions-item>
          </t-descriptions>
        </t-tab-panel>

        <t-tab-panel value="statistics" label="数据统计">
          <t-row :gutter="16">
            <t-col :span="6">
              <t-statistic title="目标客户数" :value="detailData.targetCount || 0" />
            </t-col>
            <t-col :span="6">
              <t-statistic title="已发送" :value="detailData.sentCount || 0" />
            </t-col>
            <t-col :span="6">
              <t-statistic title="已打开" :value="detailData.openedCount || 0" />
            </t-col>
            <t-col :span="6">
              <t-statistic title="已点击" :value="detailData.clickedCount || 0" />
            </t-col>
          </t-row>
          <div class="mt-4">
            <h4>转化率统计</h4>
            <div class="chart-placeholder">
              <t-icon name="chart-line" size="60px" />
              <p>转化率图表</p>
            </div>
          </div>
        </t-tab-panel>

        <t-tab-panel value="logs" label="发送记录">
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
  IconBill, IconPlayCircle, IconMail, IconPauseCircle,
  IconAdd, IconRefresh
} from 'tdesign-icons-vue-next';

const loading = ref(false);
const dialogVisible = ref(false);
const detailVisible = ref(false);
const dialogTitle = ref('新建活动');
const dialogActiveTab = ref('basic');
const detailActiveTab = ref('basic');
const formRef = ref();

const statistics = reactive({
  total: 56,
  active: 23,
  sent: 1234,
  paused: 8
});

const searchForm = reactive({
  name: '',
  type: '',
  status: ''
});

const formData = reactive({
  id: '',
  name: '',
  type: '',
  triggerType: 'immediate',
  triggerEvent: '',
  scheduleTime: '',
  targetCustomers: [],
  description: '',
  title: '',
  content: '',
  link: '',
  attachments: [],
  frequency: 'once',
  cooldown: 0,
  timeRange: ''
});

const detailData = reactive<any>({});

const pagination = reactive({
  current: 1,
  pageSize: 20,
  total: 56
});

const columns = [
  { colKey: 'name', title: '活动名称', width: 200 },
  { colKey: 'type', title: '活动类型', width: 100 },
  { colKey: 'triggerType', title: '触发类型', width: 100 },
  { colKey: 'status', title: '状态', width: 100 },
  { colKey: 'targetCount', title: '目标客户', width: 100 },
  { colKey: 'sentCount', title: '已发送', width: 100 },
  { colKey: 'openedCount', title: '已打开', width: 100 },
  { colKey: 'clickedCount', title: '已点击', width: 100 },
  { colKey: 'createTime', title: '创建时间', width: 150 },
  { colKey: 'operation', title: '操作', width: 200 }
];

const logColumns = [
  { colKey: 'customerName', title: '客户名称', width: 150 },
  { colKey: 'contact', title: '联系方式', width: 150 },
  { colKey: 'sendTime', title: '发送时间', width: 150 },
  { colKey: 'status', title: '状态', width: 100 },
  { colKey: 'openTime', title: '打开时间', width: 150 }
];

const rules = {
  name: [{ required: true, message: '请输入活动名称', type: 'error' }],
  type: [{ required: true, message: '请选择活动类型', type: 'error' }],
  triggerType: [{ required: true, message: '请选择触发类型', type: 'error' }],
  title: [{ required: true, message: '请输入消息标题', type: 'error' }],
  content: [{ required: true, message: '请输入消息内容', type: 'error' }]
};

const tableData = ref([
  {
    id: 1,
    name: '新用户欢迎邮件',
    type: '邮件营销',
    triggerType: 'event',
    status: 'active',
    targetCount: 500,
    sentCount: 423,
    openedCount: 356,
    clickedCount: 189,
    createTime: '2026-02-15 10:00:00',
    title: '欢迎加入QooERP大家庭',
    content: '感谢您注册QooERP，让我们一起开启高效办公之旅。',
    link: '',
    description: '新用户注册后自动发送欢迎邮件',
    frequency: 'once',
    cooldown: 0
  },
  {
    id: 2,
    name: '订单完成短信通知',
    type: '短信营销',
    triggerType: 'event',
    status: 'active',
    targetCount: 0,
    sentCount: 156,
    openedCount: 145,
    clickedCount: 78,
    createTime: '2026-02-10 14:30:00',
    title: '您的订单已完成',
    content: '您的订单已全部完成，感谢您的购买！',
    link: '',
    description: '订单完成时自动发送短信通知',
    frequency: 'once',
    cooldown: 1
  }
]);

const handleSearch = () => { MessagePlugin.success('查询成功'); };

const handleReset = () => {
  Object.assign(searchForm, { name: '', type: '', status: '' });
};

const handleAdd = () => {
  dialogTitle.value = '新建活动';
  dialogVisible.value = true;
};

const handleEdit = (row: any) => {
  dialogTitle.value = '编辑活动';
  dialogVisible.value = true;
  Object.assign(formData, row);
};

const handleView = (row: any) => {
  detailVisible.value = true;
  detailActiveTab.value = 'basic';
  Object.assign(detailData, row);
  detailData.logs = [
    { customerName: '上海科技有限公司', contact: '13800138000', sendTime: '2026-02-19 10:00:00', status: '已发送', openTime: '2026-02-19 10:05:00' }
  ];
};

const handlePause = (row: any) => {
  MessagePlugin.success('活动已暂停');
};

const handleResume = (row: any) => {
  MessagePlugin.success('活动已恢复');
};

const handleDelete = (row: any) => {
  MessagePlugin.success('活动已删除');
};

const handleBatchPause = () => { MessagePlugin.success('已批量暂停'); };

const handleSave = () => {
  MessagePlugin.success(dialogTitle.value === '新建活动' ? '活动已创建' : '活动已更新');
  dialogVisible.value = false;
};

const handleDialogClose = () => {
  Object.assign(formData, {
    id: '', name: '', type: '', triggerType: 'immediate',
    triggerEvent: '', scheduleTime: '', targetCustomers: [],
    description: '', title: '', content: '', link: '',
    attachments: [], frequency: 'once', cooldown: 0, timeRange: ''
  });
};

const handleRefresh = () => { MessagePlugin.success('数据已刷新'); };

const handlePageChange = (pageInfo: any) => {
  pagination.current = pageInfo.current;
  pagination.pageSize = pageInfo.pageSize;
};

const handleTriggerTypeChange = () => {};

const getTriggerTypeName = (type: string) => {
  const names: Record<string, string> = {
    immediate: '立即发送',
    scheduled: '定时发送',
    event: '触发式'
  };
  return names[type] || type;
};

onMounted(() => { console.log('MarketingAutomation mounted'); });
</script>

<style scoped lang="less">
.marketing-automation-container { padding: 20px; }
.mb-4 { margin-bottom: 16px; }
.mt-4 { margin-top: 16px; }
.flex { display: flex; }
.justify-between { justify-content: space-between; }
.items-center { align-items: center; }
.ml-2 { margin-left: 8px; }
.text-sm { font-size: 14px; }
.text-gray-500 { color: #999; }
.dialog-footer { display: flex; justify-content: flex-end; gap: 10px; margin-top: 20px; }
.chart-placeholder { text-align: center; padding: 40px; color: #999; }
</style>
