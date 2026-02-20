<template>
  <div class="campaign-management-container">
    <!-- 统计卡片 -->
    <t-row :gutter="16" class="mb-4">
      <t-col :span="6">
        <t-card theme="primary">
          <t-statistic title="活动总数" :value="statistics.totalCampaigns" :loading="loading">
            <template #prefix><icon-bill /></template>
          </t-statistic>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="success">
          <t-statistic title="进行中" :value="statistics.activeCampaigns" :loading="loading">
            <template #prefix><icon-play-circle /></template>
          </t-statistic>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="warning">
          <t-statistic title="预算使用" :value="statistics.budgetUsage" suffix="%" :loading="loading">
            <template #prefix><icon-chart-line-data /></template>
          </t-statistic>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="danger">
          <t-statistic title="ROI" :value="statistics.roi" :loading="loading">
            <template #prefix><icon-money-circle /></template>
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
            <t-option value="brand" label="品牌推广" />
            <t-option value="product" label="产品推广" />
            <t-option value="promotion" label="促销活动" />
            <t-option value="event" label="活动营销" />
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
          <t-tag v-if="row.type === 'brand'" theme="primary" variant="light">品牌推广</t-tag>
          <t-tag v-else-if="row.type === 'product'" theme="success" variant="light">产品推广</t-tag>
          <t-tag v-else-if="row.type === 'promotion'" theme="warning" variant="light">促销活动</t-tag>
          <t-tag v-else theme="default" variant="light">活动营销</t-tag>
        </template>
        <template #status="{ row }">
          <t-tag v-if="row.status === 'draft'" theme="default" variant="light">草稿</t-tag>
          <t-tag v-else-if="row.status === 'active'" theme="success" variant="light">进行中</t-tag>
          <t-tag v-else-if="row.status === 'paused'" theme="warning" variant="light">已暂停</t-tag>
          <t-tag v-else theme="default" variant="light">已完成</t-tag>
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
                    <t-option value="brand" label="品牌推广" />
                    <t-option value="product" label="产品推广" />
                    <t-option value="promotion" label="促销活动" />
                    <t-option value="event" label="活动营销" />
                  </t-select>
                </t-form-item>
              </t-col>
            </t-row>
            <t-row :gutter="16">
              <t-col :span="12">
                <t-form-item label="开始日期" name="startDate">
                  <t-date-picker v-model="formData.startDate" placeholder="请选择开始日期" enable-time-picker />
                </t-form-item>
              </t-col>
              <t-col :span="12">
                <t-form-item label="结束日期" name="endDate">
                  <t-date-picker v-model="formData.endDate" placeholder="请选择结束日期" enable-time-picker />
                </t-form-item>
              </t-col>
            </t-row>
            <t-row :gutter="16">
              <t-col :span="12">
                <t-form-item label="活动预算" name="budget">
                  <t-input-number v-model="formData.budget" :min="0" :suffix="'元'" />
                </t-form-item>
              </t-col>
              <t-col :span="12">
                <t-form-item label="负责人" name="owner">
                  <t-select v-model="formData.owner" placeholder="请选择负责人">
                    <t-option value="1" label="张三" />
                    <t-option value="2" label="李四" />
                  </t-select>
                </t-form-item>
              </t-col>
            </t-row>
            <t-form-item label="活动描述" name="description">
              <t-textarea v-model="formData.description" placeholder="请输入活动描述" :maxlength="1000" :autosize="{ minRows: 4 }" />
            </t-form-item>
          </t-form>
        </t-tab-panel>

        <t-tab-panel value="channel" label="渠道设置">
          <t-form :data="formData" label-width="120px">
            <t-form-item label="投放渠道" name="channels">
              <t-checkbox-group v-model="formData.channels">
                <t-checkbox value="wechat">微信公众号</t-checkbox>
                <t-checkbox value="weibo">微博</t-checkbox>
                <t-checkbox value="douyin">抖音</t-checkbox>
                <t-checkbox value="xiaohongshu">小红书</t-checkbox>
                <t-checkbox value="email">邮件</t-checkbox>
                <t-checkbox value="sms">短信</t-checkbox>
              </t-checkbox-group>
            </t-form-item>
            <t-form-item label="目标受众" name="audience">
              <t-select v-model="formData.audience" placeholder="请选择目标受众" multiple>
                <t-option value="all" label="全部用户" />
                <t-option value="vip" label="VIP用户" />
                <t-option value="new" label="新用户" />
                <t-option value="active" label="活跃用户" />
              </t-select>
            </t-form-item>
            <t-form-item label="预期效果" name="expectedResults">
              <t-input-number v-model="formData.expectedImpressions" :min="0" label="曝光量" :suffix="'次'" />
              <t-input-number v-model="formData.expectedClicks" :min="0" label="点击量" :suffix="'次'" />
              <t-input-number v-model="formData.expectedConversions" :min="0" label="转化量" :suffix="'次'" />
            </t-form-item>
          </t-form>
        </t-tab-panel>

        <t-tab-panel value="content" label="内容设置">
          <t-form :data="formData" label-width="120px">
            <t-form-item label="活动主题" name="theme">
              <t-input v-model="formData.theme" placeholder="请输入活动主题" />
            </t-form-item>
            <t-form-item label="活动口号" name="slogan">
              <t-input v-model="formData.slogan" placeholder="请输入活动口号" />
            </t-form-item>
            <t-form-item label="推广内容" name="content">
              <t-textarea v-model="formData.content" placeholder="请输入推广内容" :maxlength="2000" :autosize="{ minRows: 6 }" />
            </t-form-item>
            <t-form-item label="宣传图片" name="images">
              <t-upload v-model="formData.images" theme="image" accept="image/*" multiple />
            </t-form-item>
            <t-form-item label="跳转链接" name="landingUrl">
              <t-input v-model="formData.landingUrl" placeholder="请输入跳转链接" />
            </t-form-item>
          </t-form>
        </t-tab-panel>
      </t-tabs>
      <div class="dialog-footer">
        <t-button theme="default" @click="dialogVisible = false">取消</t-button>
        <t-button theme="default" @click="handleSaveDraft">保存草稿</t-button>
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
            <t-descriptions-item label="活动编号">{{ detailData.campaignCode }}</t-descriptions-item>
            <t-descriptions-item label="活动类型">{{ getTypeName(detailData.type) }}</t-descriptions-item>
            <t-descriptions-item label="活动状态">
              <t-tag v-if="detailData.status === 'active'" theme="success">进行中</t-tag>
              <t-tag v-else-if="detailData.status === 'paused'" theme="warning">已暂停</t-tag>
              <t-tag v-else-if="detailData.status === 'completed'" theme="default">已完成</t-tag>
              <t-tag v-else theme="default">草稿</t-tag>
            </t-descriptions-item>
            <t-descriptions-item label="负责人">{{ detailData.owner }}</t-descriptions-item>
            <t-descriptions-item label="开始日期">{{ detailData.startDate }}</t-descriptions-item>
            <t-descriptions-item label="结束日期">{{ detailData.endDate }}</t-descriptions-item>
            <t-descriptions-item label="活动预算" :span="2">{{ detailData.budget }} 元</t-descriptions-item>
            <t-descriptions-item label="实际支出" :span="2">{{ detailData.actualSpend || 0 }} 元</t-descriptions-item>
            <t-descriptions-item label="活动描述" :span="2">{{ detailData.description }}</t-descriptions-item>
          </t-descriptions>
        </t-tab-panel>

        <t-tab-panel value="performance" label="效果数据">
          <t-row :gutter="16" class="mb-4">
            <t-col :span="6">
              <t-statistic title="曝光量" :value="detailData.impressions || 0" />
            </t-col>
            <t-col :span="6">
              <t-statistic title="点击量" :value="detailData.clicks || 0" />
            </t-col>
            <t-col :span="6">
              <t-statistic title="转化量" :value="detailData.conversions || 0" />
            </t-col>
            <t-col :span="6">
              <t-statistic title="ROI" :value="detailData.roi || 0" />
            </t-col>
          </t-row>
          <div class="chart-placeholder">
            <t-icon name="chart-line" size="60px" />
            <p>效果趋势图表</p>
          </div>
        </t-tab-panel>

        <t-tab-panel value="tasks" label="任务列表">
          <div class="mb-4">
            <t-button theme="primary" @click="handleAddTask">
              <template #icon><icon-add /></template>
              添加任务
            </t-button>
          </div>
          <t-table :data="detailData.tasks || []" :columns="taskColumns" :pagination="false" />
        </t-tab-panel>
      </t-tabs>
    </t-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { MessagePlugin } from 'tdesign-vue-next';
import {
  IconBill, IconPlayCircle, IconChartLineData, IconMoneyCircle,
  IconAdd, IconPauseCircle, IconRefresh
} from 'tdesign-icons-vue-next';

const loading = ref(false);
const dialogVisible = ref(false);
const detailVisible = ref(false);
const dialogTitle = ref('新建活动');
const dialogActiveTab = ref('basic');
const detailActiveTab = ref('basic');
const formRef = ref();

const statistics = reactive({
  totalCampaigns: 56,
  activeCampaigns: 12,
  budgetUsage: 68.5,
  roi: 3.2
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
  startDate: '',
  endDate: '',
  budget: 0,
  owner: '',
  description: '',
  channels: [],
  audience: [],
  expectedImpressions: 0,
  expectedClicks: 0,
  expectedConversions: 0,
  theme: '',
  slogan: '',
  content: '',
  images: [],
  landingUrl: ''
});

const detailData = reactive<any>({});

const pagination = reactive({
  current: 1,
  pageSize: 20,
  total: 56
});

const columns = [
  { colKey: 'campaignCode', title: '活动编号', width: 140 },
  { colKey: 'name', title: '活动名称', width: 200 },
  { colKey: 'type', title: '活动类型', width: 100 },
  { colKey: 'status', title: '状态', width: 100 },
  { colKey: 'startDate', title: '开始日期', width: 120 },
  { colKey: 'endDate', title: '结束日期', width: 120 },
  { colKey: 'budget', title: '预算(元)', width: 100 },
  { colKey: 'impressions', title: '曝光量', width: 100 },
  { colKey: 'clicks', title: '点击量', width: 100 },
  { colKey: 'conversions', title: '转化量', width: 100 },
  { colKey: 'roi', title: 'ROI', width: 80 },
  { colKey: 'operation', title: '操作', width: 200 }
];

const taskColumns = [
  { colKey: 'taskName', title: '任务名称', width: 200 },
  { colKey: 'assignee', title: '负责人', width: 100 },
  { colKey: 'startDate', title: '开始日期', width: 120 },
  { colKey: 'endDate', title: '结束日期', width: 120 },
  { colKey: 'status', title: '状态', width: 100 }
];

const rules = {
  name: [{ required: true, message: '请输入活动名称', type: 'error' }],
  type: [{ required: true, message: '请选择活动类型', type: 'error' }],
  startDate: [{ required: true, message: '请选择开始日期', type: 'error' }],
  endDate: [{ required: true, message: '请选择结束日期', type: 'error' }],
  budget: [{ required: true, message: '请输入活动预算', type: 'error' }]
};

const tableData = ref([
  {
    id: 1,
    campaignCode: 'CMP202600001',
    name: '2026春季品牌推广活动',
    type: 'brand',
    status: 'active',
    startDate: '2026-02-01',
    endDate: '2026-03-31',
    budget: 100000,
    impressions: 567800,
    clicks: 23400,
    conversions: 1234,
    roi: 3.2,
    actualSpend: 68500,
    owner: '张三',
    description: '2026春季品牌推广活动，提升品牌知名度和用户认知'
  }
]);

const handleSearch = () => { MessagePlugin.success('查询成功'); };
const handleReset = () => { Object.assign(searchForm, { name: '', type: '', status: '' }); };
const handleRefresh = () => { MessagePlugin.success('数据已刷新'); };

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
  detailData.tasks = [
    { taskName: '微信公众号推广', assignee: '李四', startDate: '2026-02-01', endDate: '2026-03-31', status: '进行中' }
  ];
};

const handlePause = (row: any) => { MessagePlugin.success('活动已暂停'); };
const handleResume = (row: any) => { MessagePlugin.success('活动已恢复'); };
const handleDelete = (row: any) => { MessagePlugin.success('活动已删除'); };
const handleBatchPause = () => { MessagePlugin.success('批量暂停成功'); };
const handleAddTask = () => { MessagePlugin.success('添加任务'); };
const handleSaveDraft = () => { MessagePlugin.success('草稿已保存'); };

const handleSave = () => {
  MessagePlugin.success(dialogTitle.value === '新建活动' ? '活动已创建' : '活动已更新');
  dialogVisible.value = false;
};

const handleDialogClose = () => {
  Object.assign(formData, {
    id: '', name: '', type: '', startDate: '', endDate: '',
    budget: 0, owner: '', description: '', channels: [], audience: [],
    expectedImpressions: 0, expectedClicks: 0, expectedConversions: 0,
    theme: '', slogan: '', content: '', images: [], landingUrl: ''
  });
};

const handlePageChange = (pageInfo: any) => {
  pagination.current = pageInfo.current;
  pagination.pageSize = pageInfo.pageSize;
};

const getTypeName = (type: string) => {
  const names: Record<string, string> = {
    brand: '品牌推广', product: '产品推广', promotion: '促销活动', event: '活动营销'
  };
  return names[type] || type;
};

onMounted(() => { console.log('CampaignManagement mounted'); });
</script>

<style scoped lang="less">
.campaign-management-container { padding: 20px; }
.mb-4 { margin-bottom: 16px; }
.flex { display: flex; }
.justify-between { justify-content: space-between; }
.items-center { align-items: center; }
.dialog-footer { display: flex; justify-content: flex-end; gap: 10px; margin-top: 20px; }
.chart-placeholder { text-align: center; padding: 40px; color: #999; }
</style>
