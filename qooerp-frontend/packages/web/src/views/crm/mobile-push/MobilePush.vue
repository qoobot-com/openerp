<template>
  <div class="mobile-push-container">
    <!-- 统计卡片 -->
    <t-row :gutter="16" class="mb-4">
      <t-col :span="6">
        <t-card theme="primary">
          <t-statistic title="总用户数" :value="statistics.totalUsers" :loading="loading">
            <template #prefix><icon-user /></template>
          </t-statistic>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="success">
          <t-statistic title="今日推送" :value="statistics.todayPush" :loading="loading">
            <template #prefix><icon-send /></template>
          </t-statistic>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="warning">
          <t-statistic title="送达率" :value="statistics.deliveryRate" suffix="%" :loading="loading">
            <template #prefix><icon-check-circle /></template>
          </t-statistic>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="danger">
          <t-statistic title="点击率" :value="statistics.clickRate" suffix="%" :loading="loading">
            <template #prefix><icon-bolt /></template>
          </t-statistic>
        </t-card>
      </t-col>
    </t-row>

    <!-- 搜索卡片 -->
    <t-card class="mb-4">
      <t-form :data="searchForm" layout="inline" @submit="handleSearch">
        <t-form-item label="推送标题" name="title">
          <t-input v-model="searchForm.title" placeholder="请输入推送标题" clearable />
        </t-form-item>
        <t-form-item label="推送类型" name="type">
          <t-select v-model="searchForm.type" placeholder="请选择推送类型" clearable>
            <t-option value="notification" label="通知消息" />
            <t-option value="marketing" label="营销推广" />
            <t-option value="system" label="系统通知" />
            <t-option value="activity" label="活动提醒" />
          </t-select>
        </t-form-item>
        <t-form-item label="推送状态" name="status">
          <t-select v-model="searchForm.status" placeholder="请选择推送状态" clearable>
            <t-option value="draft" label="草稿" />
            <t-option value="scheduled" label="待发送" />
            <t-option value="sent" label="已发送" />
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
          新建推送
        </t-button>
        <t-button theme="default" @click="handleBatchSend">
          <template #icon><icon-send /></template>
          批量发送
        </t-button>
      </div>
      <t-button theme="default" variant="outline" @click="handleRefresh">
        <template #icon><icon-refresh /></template>
        刷新
      </t-button>
    </div>

    <!-- 推送列表 -->
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
          <t-tag v-if="row.type === 'notification'" theme="primary" variant="light">通知消息</t-tag>
          <t-tag v-else-if="row.type === 'marketing'" theme="success" variant="light">营销推广</t-tag>
          <t-tag v-else-if="row.type === 'system'" theme="warning" variant="light">系统通知</t-tag>
          <t-tag v-else theme="default" variant="light">活动提醒</t-tag>
        </template>
        <template #status="{ row }">
          <t-tag v-if="row.status === 'draft'" theme="default" variant="light">草稿</t-tag>
          <t-tag v-else-if="row.status === 'scheduled'" theme="warning" variant="light">待发送</t-tag>
          <t-tag v-else theme="success" variant="light">已发送</t-tag>
        </template>
        <template #platform="{ row }">
          <t-space>
            <t-tag v-if="row.platform.includes('ios')" theme="default" variant="light">iOS</t-tag>
            <t-tag v-if="row.platform.includes('android')" theme="success" variant="light">Android</t-tag>
          </t-space>
        </template>
        <template #operation="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleView(row)">查看</t-link>
            <t-link theme="primary" @click="handleEdit(row)">编辑</t-link>
            <t-link v-if="row.status === 'scheduled'" theme="success" @click="handleSendNow(row)">立即发送</t-link>
            <t-link theme="danger" @click="handleDelete(row)">删除</t-link>
          </t-space>
        </template>
      </t-table>
    </t-card>

    <!-- 新增/编辑推送弹窗 -->
    <t-dialog
      v-model:visible="dialogVisible"
      :header="dialogTitle"
      width="800px"
      :footer="false"
      @close="handleDialogClose"
    >
      <t-tabs v-model="dialogActiveTab" theme="card">
        <t-tab-panel value="basic" label="基本信息">
          <t-form :data="formData" ref="formRef" :rules="rules" label-width="120px">
            <t-row :gutter="16">
              <t-col :span="12">
                <t-form-item label="推送标题" name="title">
                  <t-input v-model="formData.title" placeholder="请输入推送标题" :maxlength="30" />
                </t-form-item>
              </t-col>
              <t-col :span="12">
                <t-form-item label="推送类型" name="type">
                  <t-select v-model="formData.type" placeholder="请选择推送类型">
                    <t-option value="notification" label="通知消息" />
                    <t-option value="marketing" label="营销推广" />
                    <t-option value="system" label="系统通知" />
                    <t-option value="activity" label="活动提醒" />
                  </t-select>
                </t-form-item>
              </t-col>
            </t-row>
            <t-row :gutter="16">
              <t-col :span="12">
                <t-form-item label="目标平台" name="platform">
                  <t-checkbox-group v-model="formData.platform">
                    <t-checkbox value="ios">iOS</t-checkbox>
                    <t-checkbox value="android">Android</t-checkbox>
                  </t-checkbox-group>
                </t-form-item>
              </t-col>
              <t-col :span="12">
                <t-form-item label="推送优先级" name="priority">
                  <t-radio-group v-model="formData.priority">
                    <t-radio value="high">高</t-radio>
                    <t-radio value="normal">正常</t-radio>
                    <t-radio value="low">低</t-radio>
                  </t-radio-group>
                </t-form-item>
              </t-col>
            </t-row>
            <t-form-item label="目标用户" name="targetUsers">
              <t-select v-model="formData.targetUsers" placeholder="请选择目标用户分段" multiple>
                <t-option value="all" label="全部用户" />
                <t-option value="vip" label="VIP用户" />
                <t-option value="new" label="新用户" />
                <t-option value="active" label="活跃用户" />
              </t-select>
            </t-form-item>
          </t-form>
        </t-tab-panel>

        <t-tab-panel value="content" label="内容设置">
          <t-form :data="formData" label-width="120px">
            <t-form-item label="推送内容" name="content">
              <t-textarea v-model="formData.content" placeholder="请输入推送内容" :maxlength="200" :autosize="{ minRows: 4 }" />
              <div class="text-sm text-gray-500">{{ formData.content.length }}/200</div>
            </t-form-item>
            <t-form-item label="跳转链接" name="url">
              <t-input v-model="formData.url" placeholder="请输入跳转链接（可选）" />
            </t-form-item>
            <t-form-item label="附加数据" name="data">
              <t-textarea v-model="formData.data" placeholder="请输入JSON格式的附加数据（可选）" :autosize="{ minRows: 3 }" />
            </t-form-item>
          </t-form>
        </t-tab-panel>

        <t-tab-panel value="schedule" label="发送设置">
          <t-form :data="formData" label-width="120px">
            <t-form-item label="发送方式" name="sendType">
              <t-radio-group v-model="formData.sendType">
                <t-radio value="immediate">立即发送</t-radio>
                <t-radio value="scheduled">定时发送</t-radio>
              </t-radio-group>
            </t-form-item>
            <t-form-item v-if="formData.sendType === 'scheduled'" label="发送时间" name="scheduleTime">
              <t-date-picker v-model="formData.scheduleTime" placeholder="请选择发送时间" enable-time-picker />
            </t-form-item>
            <t-form-item label="过期时间" name="expireTime">
              <t-input-number v-model="formData.expireTime" :min="1" :max="30" :suffix="'天'" />
              <span class="ml-2 text-sm text-gray-500">推送消息的有效期</span>
            </t-form-item>
            <t-form-item label="静默时段" name="silentPeriod">
              <t-time-range-picker v-model="formData.silentPeriod" placeholder="请选择静默时段（不推送）" />
            </t-form-item>
          </t-form>
        </t-tab-panel>
      </t-tabs>
      <div class="dialog-footer">
        <t-button theme="default" @click="dialogVisible = false">取消</t-button>
        <t-button theme="default" @click="handleSaveDraft">保存草稿</t-button>
        <t-button theme="primary" @click="handleSave">{{ formData.sendType === 'immediate' ? '立即发送' : '保存' }}</t-button>
      </div>
    </t-dialog>

    <!-- 推送详情弹窗 -->
    <t-dialog
      v-model:visible="detailVisible"
      header="推送详情"
      width="900px"
      :footer="false"
    >
      <t-tabs v-model="detailActiveTab" theme="card">
        <t-tab-panel value="basic" label="基本信息">
          <t-descriptions :column="2" bordered>
            <t-descriptions-item label="推送标题" :span="2">{{ detailData.title }}</t-descriptions-item>
            <t-descriptions-item label="推送类型">{{ getTypeName(detailData.type) }}</t-descriptions-item>
            <t-descriptions-item label="推送状态">
              <t-tag v-if="detailData.status === 'sent'" theme="success">已发送</t-tag>
              <t-tag v-else-if="detailData.status === 'scheduled'" theme="warning">待发送</t-tag>
              <t-tag v-else theme="default">草稿</t-tag>
            </t-descriptions-item>
            <t-descriptions-item label="目标平台">{{ detailData.platform?.join(', ') || '-' }}</t-descriptions-item>
            <t-descriptions-item label="优先级">{{ detailData.priority || '正常' }}</t-descriptions-item>
            <t-descriptions-item label="创建时间">{{ detailData.createTime }}</t-descriptions-item>
            <t-descriptions-item label="发送时间">{{ detailData.sendTime || '-' }}</t-descriptions-item>
          </t-descriptions>
        </t-tab-panel>

        <t-tab-panel value="content" label="推送内容">
          <t-descriptions :column="1" bordered>
            <t-descriptions-item label="推送内容">{{ detailData.content }}</t-descriptions-item>
            <t-descriptions-item label="跳转链接">{{ detailData.url || '-' }}</t-descriptions-item>
          </t-descriptions>
        </t-tab-panel>

        <t-tab-panel value="statistics" label="数据统计">
          <t-row :gutter="16">
            <t-col :span="6">
              <t-statistic title="目标用户数" :value="detailData.targetCount || 0" />
            </t-col>
            <t-col :span="6">
              <t-statistic title="已送达" :value="detailData.deliveredCount || 0" />
            </t-col>
            <t-col :span="6">
              <t-statistic title="已点击" :value="detailData.clickedCount || 0" />
            </t-col>
            <t-col :span="6">
              <t-statistic title="转化率" :value="detailData.conversionRate || 0" suffix="%" />
            </t-col>
          </t-row>
          <div class="mt-4">
            <h4>数据趋势</h4>
            <div class="chart-placeholder">
              <t-icon name="chart-line" size="60px" />
              <p>数据趋势图表</p>
            </div>
          </div>
        </t-tab-panel>

        <t-tab-panel value="logs" label="发送日志">
          <t-table :data="detailData.logs || []" :columns="logColumns" :pagination="false">
            <template #status="{ row }">
              <t-tag v-if="row.status === 'delivered'" theme="success">已送达</t-tag>
              <t-tag v-else-if="row.status === 'failed'" theme="danger">失败</t-tag>
              <t-tag v-else theme="warning">待发送</t-tag>
            </template>
          </t-table>
        </t-tab-panel>
      </t-tabs>
    </t-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { MessagePlugin } from 'tdesign-vue-next';
import {
  IconUser, IconSend, IconCheckCircle, IconBolt,
  IconAdd, IconRefresh
} from 'tdesign-icons-vue-next';

const loading = ref(false);
const dialogVisible = ref(false);
const detailVisible = ref(false);
const dialogTitle = ref('新建推送');
const dialogActiveTab = ref('basic');
const detailActiveTab = ref('basic');
const formRef = ref();

const statistics = reactive({
  totalUsers: 15600,
  todayPush: 123,
  deliveryRate: 95.6,
  clickRate: 12.3
});

const searchForm = reactive({
  title: '',
  type: '',
  status: ''
});

const formData = reactive({
  id: '',
  title: '',
  type: '',
  platform: ['ios', 'android'],
  priority: 'normal',
  targetUsers: [],
  content: '',
  url: '',
  data: '',
  sendType: 'immediate',
  scheduleTime: '',
  expireTime: 7,
  silentPeriod: ''
});

const detailData = reactive<any>({});

const pagination = reactive({
  current: 1,
  pageSize: 20,
  total: 123
});

const columns = [
  { colKey: 'title', title: '推送标题', width: 200, ellipsis: true },
  { colKey: 'type', title: '推送类型', width: 100 },
  { colKey: 'platform', title: '平台', width: 120 },
  { colKey: 'status', title: '状态', width: 100 },
  { colKey: 'targetCount', title: '目标用户', width: 100 },
  { colKey: 'deliveredCount', title: '已送达', width: 100 },
  { colKey: 'clickedCount', title: '已点击', width: 100 },
  { colKey: 'sendTime', title: '发送时间', width: 150 },
  { colKey: 'createTime', title: '创建时间', width: 150 },
  { colKey: 'operation', title: '操作', width: 180 }
];

const logColumns = [
  { colKey: 'userId', title: '用户ID', width: 150 },
  { colKey: 'device', title: '设备', width: 100 },
  { colKey: 'sendTime', title: '发送时间', width: 150 },
  { colKey: 'status', title: '状态', width: 100 },
  { colKey: 'failReason', title: '失败原因', width: 200 }
];

const rules = {
  title: [{ required: true, message: '请输入推送标题', type: 'error' }],
  type: [{ required: true, message: '请选择推送类型', type: 'error' }],
  platform: [{ required: true, message: '请选择目标平台', type: 'error' }],
  content: [{ required: true, message: '请输入推送内容', type: 'error' }]
};

const tableData = ref([
  {
    id: 1,
    title: '新版本更新提醒',
    type: 'system',
    platform: ['ios', 'android'],
    status: 'sent',
    targetCount: 15600,
    deliveredCount: 14850,
    clickedCount: 1823,
    sendTime: '2026-02-19 09:00:00',
    createTime: '2026-02-18 16:00:00',
    content: 'QooERP 2.0版本已发布，快来体验新功能吧！',
    url: 'https://qooerp.com/update',
    priority: 'high'
  },
  {
    id: 2,
    title: '限时优惠活动',
    type: 'marketing',
    platform: ['android'],
    status: 'scheduled',
    targetCount: 5000,
    deliveredCount: 0,
    clickedCount: 0,
    sendTime: '2026-02-20 10:00:00',
    createTime: '2026-02-19 14:00:00',
    content: '限时特惠，立享8折优惠',
    url: 'https://qooerp.com/promotion',
    priority: 'normal'
  }
]);

const handleSearch = () => { MessagePlugin.success('查询成功'); };

const handleReset = () => {
  Object.assign(searchForm, { title: '', type: '', status: '' });
};

const handleAdd = () => {
  dialogTitle.value = '新建推送';
  dialogVisible.value = true;
};

const handleEdit = (row: any) => {
  dialogTitle.value = '编辑推送';
  dialogVisible.value = true;
  Object.assign(formData, row);
};

const handleView = (row: any) => {
  detailVisible.value = true;
  detailActiveTab.value = 'basic';
  Object.assign(detailData, row);
  detailData.logs = [
    { userId: 'user001', device: 'iPhone 15', sendTime: '2026-02-19 09:00:01', status: 'delivered', failReason: '' }
  ];
};

const handleBatchSend = () => { MessagePlugin.success('批量发送成功'); };
const handleSendNow = (row: any) => { MessagePlugin.success('立即发送成功'); };
const handleDelete = (row: any) => { MessagePlugin.success('推送已删除'); };
const handleSaveDraft = () => { MessagePlugin.success('草稿已保存'); };

const handleSave = () => {
  MessagePlugin.success(formData.sendType === 'immediate' ? '推送已发送' : '推送已保存');
  dialogVisible.value = false;
};

const handleDialogClose = () => {
  Object.assign(formData, {
    id: '', title: '', type: '', platform: ['ios', 'android'],
    priority: 'normal', targetUsers: [], content: '', url: '', data: '',
    sendType: 'immediate', scheduleTime: '', expireTime: 7, silentPeriod: ''
  });
};

const handleRefresh = () => { MessagePlugin.success('数据已刷新'); };

const handlePageChange = (pageInfo: any) => {
  pagination.current = pageInfo.current;
  pagination.pageSize = pageInfo.pageSize;
};

const getTypeName = (type: string) => {
  const names: Record<string, string> = {
    notification: '通知消息', marketing: '营销推广', system: '系统通知', activity: '活动提醒'
  };
  return names[type] || type;
};

onMounted(() => { console.log('MobilePush mounted'); });
</script>

<style scoped lang="less">
.mobile-push-container { padding: 20px; }
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
