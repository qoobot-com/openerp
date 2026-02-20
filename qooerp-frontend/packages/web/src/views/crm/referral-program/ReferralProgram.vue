<template>
  <div class="referral-program-container">
    <!-- 统计卡片 -->
    <t-row :gutter="16" class="mb-4">
      <t-col :span="6">
        <t-card theme="primary">
          <t-statistic title="推荐人数" :value="statistics.totalReferrals" :loading="loading">
            <template #prefix><icon-usergroup-add /></template>
          </t-statistic>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="success">
          <t-statistic title="成功转化" :value="statistics.convertedReferrals" :loading="loading">
            <template #prefix><icon-check-circle /></template>
          </t-statistic>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="warning">
          <t-statistic title="转化率" :value="statistics.conversionRate" suffix="%" :loading="loading">
            <template #prefix><icon-chart-line-data /></template>
          </t-statistic>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="danger">
          <t-statistic title="奖励支出" :value="statistics.totalRewards" suffix="元" :loading="loading">
            <template #prefix><icon-gift /></template>
          </t-statistic>
        </t-card>
      </t-col>
    </t-row>

    <!-- 搜索卡片 -->
    <t-card class="mb-4">
      <t-form :data="searchForm" layout="inline" @submit="handleSearch">
        <t-form-item label="推荐人" name="referrer">
          <t-input v-model="searchForm.referrer" placeholder="请输入推荐人姓名" clearable />
        </t-form-item>
        <t-form-item label="被推荐人" name="referee">
          <t-input v-model="searchForm.referee" placeholder="请输入被推荐人姓名" clearable />
        </t-form-item>
        <t-form-item label="推荐状态" name="status">
          <t-select v-model="searchForm.status" placeholder="请选择推荐状态" clearable>
            <t-option value="pending" label="待确认" />
            <t-option value="confirmed" label="已确认" />
            <t-option value="converted" label="已转化" />
            <t-option value="expired" label="已失效" />
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
        <t-button theme="primary" @click="handleAddReferral">
          <template #icon><icon-add /></template>
          手动添加
        </t-button>
      </div>
      <t-button theme="default" variant="outline" @click="handleRefresh">
        <template #icon><icon-refresh /></template>
        刷新
      </t-button>
    </div>

    <!-- 推荐记录列表 -->
    <t-card>
      <t-table
        :data="referralData"
        :columns="columns"
        :loading="loading"
        :pagination="pagination"
        row-key="id"
        @page-change="handlePageChange"
      >
        <template #status="{ row }">
          <t-tag v-if="row.status === 'pending'" theme="warning">待确认</t-tag>
          <t-tag v-else-if="row.status === 'confirmed'" theme="primary">已确认</t-tag>
          <t-tag v-else-if="row.status === 'converted'" theme="success">已转化</t-tag>
          <t-tag v-else theme="default">已失效</t-tag>
        </template>
        <template #rewardStatus="{ row }">
          <t-tag v-if="row.rewardStatus === 'pending'" theme="warning">待发放</t-tag>
          <t-tag v-else-if="row.rewardStatus === 'sent'" theme="success">已发放</t-tag>
          <t-tag v-else theme="default">无需奖励</t-tag>
        </template>
        <template #operation="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleView(row)">查看</t-link>
            <t-link v-if="row.status === 'pending'" theme="success" @click="handleConfirm(row)">确认</t-link>
            <t-link v-if="row.status === 'confirmed'" theme="primary" @click="handleConvert(row)">标记转化</t-link>
            <t-link v-if="row.rewardStatus === 'pending'" theme="warning" @click="handleSendReward(row)">发放奖励</t-link>
          </t-space>
        </template>
      </t-table>
    </t-card>

    <!-- 手动添加弹窗 -->
    <t-dialog
      v-model:visible="addDialogVisible"
      header="手动添加推荐"
      width="600px"
      :footer="false"
    >
      <t-form :data="addForm" label-width="120px">
        <t-form-item label="推荐人" name="referrer">
          <t-select v-model="addForm.referrer" placeholder="请选择推荐人" filterable>
            <t-option value="1" label="张三" />
            <t-option value="2" label="李四" />
          </t-select>
        </t-form-item>
        <t-form-item label="被推荐人" name="refereeName">
          <t-input v-model="addForm.refereeName" placeholder="请输入被推荐人姓名" />
        </t-form-item>
        <t-form-item label="联系电话" name="refereePhone">
          <t-input v-model="addForm.refereePhone" placeholder="请输入联系电话" />
        </t-form-item>
        <t-form-item label="电子邮箱" name="refereeEmail">
          <t-input v-model="addForm.refereeEmail" placeholder="请输入电子邮箱" />
        </t-form-item>
        <t-form-item label="备注" name="remark">
          <t-textarea v-model="addForm.remark" placeholder="请输入备注信息" :maxlength="200" />
        </t-form-item>
      </t-form>
      <div class="dialog-footer">
        <t-button theme="default" @click="addDialogVisible = false">取消</t-button>
        <t-button theme="primary" @click="handleSaveAdd">保存</t-button>
      </div>
    </t-dialog>

    <!-- 推荐详情弹窗 -->
    <t-dialog
      v-model:visible="detailVisible"
      header="推荐详情"
      width="800px"
      :footer="false"
    >
      <t-descriptions :column="2" bordered>
        <t-descriptions-item label="推荐编号">{{ detailData.referralCode }}</t-descriptions-item>
        <t-descriptions-item label="推荐状态">
          <t-tag v-if="detailData.status === 'converted'" theme="success">已转化</t-tag>
          <t-tag v-else-if="detailData.status === 'confirmed'" theme="primary">已确认</t-tag>
          <t-tag v-else-if="detailData.status === 'pending'" theme="warning">待确认</t-tag>
          <t-tag v-else theme="default">已失效</t-tag>
        </t-descriptions-item>
        <t-descriptions-item label="推荐人">{{ detailData.referrerName }}</t-descriptions-item>
        <t-descriptions-item label="被推荐人">{{ detailData.refereeName }}</t-descriptions-item>
        <t-descriptions-item label="联系电话">{{ detailData.refereePhone }}</t-descriptions-item>
        <t-descriptions-item label="电子邮箱">{{ detailData.refereeEmail }}</t-descriptions-item>
        <t-descriptions-item label="推荐时间" :span="2">{{ detailData.referralTime }}</t-descriptions-item>
        <t-descriptions-item label="确认时间">{{ detailData.confirmTime || '-' }}</t-descriptions-item>
        <t-descriptions-item label="转化时间">{{ detailData.convertTime || '-' }}</t-descriptions-item>
        <t-descriptions-item label="推荐奖励">
          {{ detailData.rewardAmount ? detailData.rewardAmount + '元' : '-' }}
        </t-descriptions-item>
        <t-descriptions-item label="奖励状态">
          <t-tag v-if="detailData.rewardStatus === 'sent'" theme="success">已发放</t-tag>
          <t-tag v-else-if="detailData.rewardStatus === 'pending'" theme="warning">待发放</t-tag>
          <t-tag v-else>无需奖励</t-tag>
        </t-descriptions-item>
      </t-descriptions>
    </t-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { MessagePlugin } from 'tdesign-vue-next';
import {
  IconUsergroupAdd, IconCheckCircle, IconChartLineData, IconGift,
  IconAdd, IconRefresh
} from 'tdesign-icons-vue-next';

const loading = ref(false);
const addDialogVisible = ref(false);
const detailVisible = ref(false);

const statistics = reactive({
  totalReferrals: 1234,
  convertedReferrals: 456,
  conversionRate: 36.9,
  totalRewards: 45600
});

const searchForm = reactive({
  referrer: '',
  referee: '',
  status: ''
});

const addForm = reactive({
  referrer: '',
  refereeName: '',
  refereePhone: '',
  refereeEmail: '',
  remark: ''
});

const detailData = reactive<any>({});

const pagination = reactive({
  current: 1,
  pageSize: 20,
  total: 1234
});

const columns = [
  { colKey: 'referralCode', title: '推荐编号', width: 140 },
  { colKey: 'referrerName', title: '推荐人', width: 100 },
  { colKey: 'refereeName', title: '被推荐人', width: 100 },
  { colKey: 'refereePhone', title: '联系电话', width: 120 },
  { colKey: 'status', title: '状态', width: 100 },
  { colKey: 'rewardAmount', title: '奖励金额', width: 100 },
  { colKey: 'rewardStatus', title: '奖励状态', width: 100 },
  { colKey: 'referralTime', title: '推荐时间', width: 150 },
  { colKey: 'operation', title: '操作', width: 180 }
];

const referralData = ref([
  {
    id: 1,
    referralCode: 'REF202600001',
    referrerId: '1',
    referrerName: '张三',
    refereeName: '王五',
    refereePhone: '13900139001',
    refereeEmail: 'wangwu@example.com',
    status: 'converted',
    rewardAmount: 100,
    rewardStatus: 'sent',
    referralTime: '2026-01-15 10:00:00',
    confirmTime: '2026-01-16 14:00:00',
    convertTime: '2026-01-20 10:00:00'
  },
  {
    id: 2,
    referralCode: 'REF202600002',
    referrerId: '2',
    referrerName: '李四',
    refereeName: '赵六',
    refereePhone: '13900139002',
    refereeEmail: 'zhaoliu@example.com',
    status: 'confirmed',
    rewardAmount: 100,
    rewardStatus: 'pending',
    referralTime: '2026-02-10 11:00:00',
    confirmTime: '2026-02-12 09:00:00',
    convertTime: ''
  }
]);

const handleSearch = () => { MessagePlugin.success('查询成功'); };
const handleReset = () => { Object.assign(searchForm, { referrer: '', referee: '', status: '' }); };
const handleRefresh = () => { MessagePlugin.success('数据已刷新'); };

const handleAddReferral = () => { addDialogVisible.value = true; };
const handleSaveAdd = () => {
  MessagePlugin.success('推荐已添加');
  addDialogVisible.value = false;
};

const handleView = (row: any) => {
  Object.assign(detailData, row);
  detailVisible.value = true;
};
const handleConfirm = (row: any) => { MessagePlugin.success('已确认推荐'); };
const handleConvert = (row: any) => { MessagePlugin.success('已标记转化'); };
const handleSendReward = (row: any) => { MessagePlugin.success('奖励已发放'); };

const handlePageChange = (pageInfo: any) => {
  pagination.current = pageInfo.current;
  pagination.pageSize = pageInfo.pageSize;
};

onMounted(() => { console.log('ReferralProgram mounted'); });
</script>

<style scoped lang="less">
.referral-program-container { padding: 20px; }
.mb-4 { margin-bottom: 16px; }
.flex { display: flex; }
.justify-between { justify-content: space-between; }
.items-center { align-items: center; }
.dialog-footer { display: flex; justify-content: flex-end; gap: 10px; margin-top: 20px; }
</style>
