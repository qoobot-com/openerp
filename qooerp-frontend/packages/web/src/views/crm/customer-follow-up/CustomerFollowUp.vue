<template>
  <div class="customer-follow-up-container">
    <!-- 统计卡片 -->
    <t-row :gutter="16" class="mb-4">
      <t-col :span="6">
        <t-card theme="primary">
          <t-statistic title="跟进记录总数" :value="statistics.total" :loading="loading">
            <template #prefix><icon-file-list /></template>
          </t-statistic>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="success">
          <t-statistic title="今日跟进" :value="statistics.today" :loading="loading">
            <template #prefix><icon-check-circle /></template>
          </t-statistic>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="warning">
          <t-statistic title="待跟进" :value="statistics.pending" :loading="loading">
            <template #prefix><icon-time /></template>
          </t-statistic>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="default">
          <t-statistic title="客户数" :value="statistics.customers" :loading="loading">
            <template #prefix><icon-user /></template>
          </t-statistic>
        </t-card>
      </t-col>
    </t-row>

    <!-- 搜索卡片 -->
    <t-card class="mb-4">
      <t-form :data="searchForm" layout="inline" @submit="handleSearch">
        <t-form-item label="跟进单号" name="followUpCode">
          <t-input v-model="searchForm.followUpCode" placeholder="请输入跟进单号" clearable />
        </t-form-item>
        <t-form-item label="客户名称" name="customerName">
          <t-input v-model="searchForm.customerName" placeholder="请输入客户名称" clearable />
        </t-form-item>
        <t-form-item label="跟进方式" name="followType">
          <t-select v-model="searchForm.followType" placeholder="请选择跟进方式" clearable>
            <t-option value="电话" label="电话" />
            <t-option value="拜访" label="拜访" />
            <t-option value="邮件" label="邮件" />
            <t-option value="微信" label="微信" />
            <t-option value="其他" label="其他" />
          </t-select>
        </t-form-item>
        <t-form-item label="跟进人" name="follower">
          <t-input v-model="searchForm.follower" placeholder="请输入跟进人" clearable />
        </t-form-item>
        <t-form-item label="跟进日期" name="followDate">
          <t-date-picker v-model="searchForm.followDate" placeholder="请选择跟进日期" clearable />
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
          新增跟进
        </t-button>
        <t-button theme="default" @click="handleBatchComplete">
          <template #icon><icon-check /></template>
          批量完成
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

    <!-- 跟进记录列表 -->
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
        <template #followType="{ row }">
          <t-tag v-if="row.followType === '电话'" theme="primary" variant="light">电话</t-tag>
          <t-tag v-else-if="row.followType === '拜访'" theme="success" variant="light">拜访</t-tag>
          <t-tag v-else-if="row.followType === '邮件'" theme="cyan" variant="light">邮件</t-tag>
          <t-tag v-else-if="row.followType === '微信'" theme="warning" variant="light">微信</t-tag>
          <t-tag v-else theme="default" variant="light">其他</t-tag>
        </template>
        <template #status="{ row }">
          <t-tag v-if="row.status === 'completed'" theme="success" variant="light">已完成</t-tag>
          <t-tag v-else-if="row.status === 'pending'" theme="warning" variant="light">待跟进</t-tag>
          <t-tag v-else theme="danger" variant="light">已过期</t-tag>
        </template>
        <template #operation="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleView(row)">查看</t-link>
            <t-link theme="primary" @click="handleEdit(row)">编辑</t-link>
            <t-link theme="success" @click="handleComplete(row)">完成</t-link>
            <t-popconfirm content="确定要删除此跟进记录吗？" @confirm="handleDelete(row)">
              <t-link theme="danger">删除</t-link>
            </t-popconfirm>
          </t-space>
        </template>
      </t-table>
    </t-card>

    <!-- 新增/编辑跟进弹窗 -->
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
            <t-form-item label="跟进单号" name="followUpCode">
              <t-input v-model="formData.followUpCode" placeholder="自动生成" disabled />
            </t-form-item>
          </t-col>
          <t-col :span="12">
            <t-form-item label="关联客户" name="customerId">
              <t-select v-model="formData.customerId" placeholder="请选择客户" filterable>
                <t-option v-for="item in customers" :key="item.id" :value="item.id" :label="item.name" />
              </t-select>
            </t-form-item>
          </t-col>
        </t-row>
        <t-row :gutter="16">
          <t-col :span="12">
            <t-form-item label="跟进方式" name="followType">
              <t-select v-model="formData.followType" placeholder="请选择跟进方式">
                <t-option value="电话" label="电话" />
                <t-option value="拜访" label="拜访" />
                <t-option value="邮件" label="邮件" />
                <t-option value="微信" label="微信" />
                <t-option value="其他" label="其他" />
              </t-select>
            </t-form-item>
          </t-col>
          <t-col :span="12">
            <t-form-item label="跟进人" name="follower">
              <t-input v-model="formData.follower" placeholder="请输入跟进人" />
            </t-form-item>
          </t-col>
        </t-row>
        <t-form-item label="跟进内容" name="content">
          <t-textarea v-model="formData.content" placeholder="请输入跟进内容" :maxlength="500" />
        </t-form-item>
        <t-row :gutter="16">
          <t-col :span="12">
            <t-form-item label="跟进日期" name="followDate">
              <t-date-picker v-model="formData.followDate" placeholder="请选择跟进日期" />
            </t-form-item>
          </t-col>
          <t-col :span="12">
            <t-form-item label="跟进时间" name="followTime">
              <t-time-picker v-model="formData.followTime" placeholder="请选择跟进时间" />
            </t-form-item>
          </t-col>
        </t-row>
        <t-form-item label="下次跟进日期" name="nextFollowDate">
          <t-date-picker v-model="formData.nextFollowDate" placeholder="请选择下次跟进日期" />
        </t-form-item>
        <t-form-item label="跟进结果" name="result">
          <t-textarea v-model="formData.result" placeholder="请输入跟进结果" :maxlength="500" />
        </t-form-item>
        <t-form-item label="备注" name="remark">
          <t-textarea v-model="formData.remark" placeholder="请输入备注" :maxlength="500" />
        </t-form-item>
      </t-form>
      <div class="dialog-footer">
        <t-button theme="default" @click="dialogVisible = false">取消</t-button>
        <t-button theme="primary" @click="handleSave">保存</t-button>
      </div>
    </t-dialog>

    <!-- 跟进详情弹窗 -->
    <t-dialog
      v-model:visible="detailVisible"
      header="跟进详情"
      width="700px"
      :footer="false"
    >
      <t-descriptions :column="2" bordered>
        <t-descriptions-item label="跟进单号">{{ detailData.followUpCode }}</t-descriptions-item>
        <t-descriptions-item label="关联客户">{{ detailData.customerName }}</t-descriptions-item>
        <t-descriptions-item label="跟进方式">{{ detailData.followType }}</t-descriptions-item>
        <t-descriptions-item label="跟进人">{{ detailData.follower }}</t-descriptions-item>
        <t-descriptions-item label="跟进日期">{{ detailData.followDate }}</t-descriptions-item>
        <t-descriptions-item label="跟进时间">{{ detailData.followTime }}</t-descriptions-item>
        <t-descriptions-item label="下次跟进日期">{{ detailData.nextFollowDate }}</t-descriptions-item>
        <t-descriptions-item label="状态">
          <t-tag v-if="detailData.status === 'completed'" theme="success">已完成</t-tag>
          <t-tag v-else-if="detailData.status === 'pending'" theme="warning">待跟进</t-tag>
          <t-tag v-else theme="danger">已过期</t-tag>
        </t-descriptions-item>
        <t-descriptions-item label="跟进内容" :span="2">{{ detailData.content }}</t-descriptions-item>
        <t-descriptions-item label="跟进结果" :span="2">{{ detailData.result }}</t-descriptions-item>
        <t-descriptions-item label="备注" :span="2">{{ detailData.remark }}</t-descriptions-item>
        <t-descriptions-item label="创建时间" :span="2">{{ detailData.createTime }}</t-descriptions-item>
      </t-descriptions>
    </t-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { MessagePlugin } from 'tdesign-vue-next';
import {
  IconFileList, IconCheckCircle, IconTime, IconUser,
  IconAdd, IconCheck, IconDownload, IconRefresh
} from 'tdesign-icons-vue-next';

const loading = ref(false);
const dialogVisible = ref(false);
const detailVisible = ref(false);
const dialogTitle = ref('新增跟进');
const formRef = ref();
const selectedRowKeys = ref([]);

const statistics = reactive({
  total: 456,
  today: 23,
  pending: 45,
  customers: 89
});

const searchForm = reactive({
  followUpCode: '',
  customerName: '',
  followType: '',
  follower: '',
  followDate: ''
});

const formData = reactive({
  id: '',
  followUpCode: '',
  customerId: '',
  followType: '',
  follower: '',
  content: '',
  followDate: '',
  followTime: '',
  nextFollowDate: '',
  result: '',
  remark: ''
});

const detailData = reactive<any>({});

const pagination = reactive({
  current: 1,
  pageSize: 20,
  total: 456
});

const customers = ref([
  { id: 1, name: '上海科技有限公司' },
  { id: 2, name: '北京贸易公司' }
]);

const columns = [
  { colKey: 'followUpCode', title: '跟进单号', width: 130 },
  { colKey: 'customerName', title: '客户名称', width: 150 },
  { colKey: 'followType', title: '跟进方式', width: 80 },
  { colKey: 'follower', title: '跟进人', width: 100 },
  { colKey: 'followDate', title: '跟进日期', width: 120 },
  { colKey: 'nextFollowDate', title: '下次跟进日期', width: 120 },
  { colKey: 'status', title: '状态', width: 90 },
  { colKey: 'createTime', title: '创建时间', width: 120 },
  { colKey: 'operation', title: '操作', width: 180, fixed: 'right' }
];

const rules = {
  customerId: [{ required: true, message: '请选择客户', type: 'error' }],
  followType: [{ required: true, message: '请选择跟进方式', type: 'error' }],
  follower: [{ required: true, message: '请输入跟进人', type: 'error' }],
  content: [{ required: true, message: '请输入跟进内容', type: 'error' }]
};

const tableData = ref([
  {
    id: 1,
    followUpCode: 'FU202600001',
    customerId: 1,
    customerName: '上海科技有限公司',
    followType: '拜访',
    follower: '张三',
    content: '上门拜访，了解ERP系统需求',
    followDate: '2026-02-18',
    followTime: '10:30:00',
    nextFollowDate: '2026-02-25',
    result: '客户对系统功能很感兴趣，需要进一步沟通',
    remark: '',
    status: 'completed',
    createTime: '2026-02-18 10:00:00'
  },
  {
    id: 2,
    followUpCode: 'FU202600002',
    customerId: 2,
    customerName: '北京贸易公司',
    followType: '电话',
    follower: '李四',
    content: '电话沟通CRM系统升级需求',
    followDate: '2026-02-19',
    followTime: '14:20:00',
    nextFollowDate: '2026-02-26',
    result: '',
    remark: '',
    status: 'pending',
    createTime: '2026-02-19 14:00:00'
  }
]);

const handleSearch = () => {
  MessagePlugin.success('查询成功');
};

const handleReset = () => {
  Object.assign(searchForm, {
    followUpCode: '',
    customerName: '',
    followType: '',
    follower: '',
    followDate: ''
  });
};

const handleAdd = () => {
  dialogTitle.value = '新增跟进';
  dialogVisible.value = true;
};

const handleEdit = (row: any) => {
  dialogTitle.value = '编辑跟进';
  dialogVisible.value = true;
  Object.assign(formData, row);
};

const handleView = (row: any) => {
  detailVisible.value = true;
  Object.assign(detailData, row);
};

const handleComplete = (row: any) => {
  MessagePlugin.success('跟进记录已完成');
};

const handleBatchComplete = () => {
  MessagePlugin.success('批量完成成功');
};

const handleDelete = (row: any) => {
  MessagePlugin.success('跟进记录已删除');
};

const handleSave = () => {
  MessagePlugin.success(dialogTitle.value === '新增跟进' ? '跟进记录已创建' : '跟进记录已更新');
  dialogVisible.value = false;
};

const handleDialogClose = () => {
  Object.assign(formData, {
    id: '',
    followUpCode: '',
    customerId: '',
    followType: '',
    follower: '',
    content: '',
    followDate: '',
    followTime: '',
    nextFollowDate: '',
    result: '',
    remark: ''
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
  console.log('CustomerFollowUp mounted');
});
</script>

<style scoped lang="less">
.customer-follow-up-container {
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
</style>
