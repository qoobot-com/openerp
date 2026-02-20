<template>
  <div class="leave-page">
    <!-- 操作栏 -->
    <t-card class="action-card">
      <t-space>
        <t-button theme="primary" @click="handleApply">
          <template #icon><t-icon name="add" /></template>
          申请请假
        </t-button>
      </t-space>
    </t-card>

    <!-- 搜索卡片 -->
    <t-card class="search-card">
      <t-form ref="searchForm" :data="searchForm" layout="inline" @submit="handleSearch" @reset="handleReset">
        <t-form-item label="申请人" name="applicantName">
          <t-input v-model="searchForm.applicantName" placeholder="请输入申请人" clearable />
        </t-form-item>
        <t-form-item label="部门" name="departmentId">
          <t-select v-model="searchForm.departmentId" placeholder="请选择部门" clearable>
            <t-option value="1" label="技术研发部" />
            <t-option value="2" label="市场部" />
            <t-option value="3" label="运营部" />
            <t-option value="4" label="财务部" />
          </t-select>
        </t-form-item>
        <t-form-item label="请假类型" name="leaveType">
          <t-select v-model="searchForm.leaveType" placeholder="请选择请假类型" clearable>
            <t-option value="1" label="事假" />
            <t-option value="2" label="病假" />
            <t-option value="3" label="年假" />
            <t-option value="4" label="调休" />
            <t-option value="5" label="婚假" />
            <t-option value="6" label="产假" />
          </t-select>
        </t-form-item>
        <t-form-item label="状态" name="status">
          <t-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <t-option value="pending" label="待审批" />
            <t-option value="approved" label="已通过" />
            <t-option value="rejected" label="已拒绝" />
            <t-option value="cancelled" label="已撤销" />
          </t-select>
        </t-form-item>
        <t-form-item label="申请日期" name="applyDate">
          <t-date-range-picker v-model="searchForm.applyDate" placeholder="请选择申请日期" clearable />
        </t-form-item>
        <t-form-item>
          <t-space>
            <t-button theme="primary" type="submit">查询</t-button>
            <t-button theme="default" type="reset">重置</t-button>
          </t-space>
        </t-form-item>
      </t-form>
    </t-card>

    <!-- 请假列表表格 -->
    <t-card class="table-card">
      <t-table
        :data="tableData"
        :columns="columns"
        :loading="loading"
        :pagination="pagination"
        row-key="id"
        @page-change="handlePageChange"
      >
        <template #leaveType="{ row }">
          <t-tag>{{ getLeaveTypeText(row.leaveType) }}</t-tag>
        </template>
        <template #status="{ row }">
          <t-tag v-if="row.status === 'pending'" theme="warning" variant="light">待审批</t-tag>
          <t-tag v-else-if="row.status === 'approved'" theme="success" variant="light">已通过</t-tag>
          <t-tag v-else-if="row.status === 'rejected'" theme="danger" variant="light">已拒绝</t-tag>
          <t-tag v-else theme="default" variant="light">已撤销</t-tag>
        </template>
        <template #operation="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleView(row)">详情</t-link>
            <t-link v-if="row.status === 'pending'" theme="primary" @click="handleApprove(row)">审批</t-link>
            <t-link v-if="row.status === 'pending'" theme="danger" @click="handleReject(row)">拒绝</t-link>
            <t-link v-if="row.status === 'pending'" theme="default" @click="handleCancel(row)">撤销</t-link>
          </t-space>
        </template>
      </t-table>
    </t-card>

    <!-- 请假申请弹窗 -->
    <t-dialog
      v-model:visible="dialogVisible"
      :header="isEdit ? '编辑请假申请' : '申请请假'"
      width="600px"
      :confirm-btn="{
        content: '提交',
        theme: 'primary',
        loading: saving,
      }"
      @confirm="handleSubmit"
    >
      <t-form ref="formRef" :data="formData" :rules="rules" label-align="right" label-width="120px">
        <t-form-item label="申请人" name="applicantId">
          <t-select v-model="formData.applicantId" placeholder="请选择申请人">
            <t-option value="1" label="张三 (E001)" />
            <t-option value="2" label="李四 (E002)" />
            <t-option value="3" label="王五 (E003)" />
          </t-select>
        </t-form-item>
        <t-form-item label="请假类型" name="leaveType">
          <t-select v-model="formData.leaveType" placeholder="请选择请假类型">
            <t-option value="1" label="事假" />
            <t-option value="2" label="病假" />
            <t-option value="3" label="年假" />
            <t-option value="4" label="调休" />
            <t-option value="5" label="婚假" />
            <t-option value="6" label="产假" />
          </t-select>
        </t-form-item>
        <t-form-item label="请假开始时间" name="startTime">
          <t-date-picker v-model="formData.startTime" enable-time-picker format="YYYY-MM-DD HH:mm" />
        </t-form-item>
        <t-form-item label="请假结束时间" name="endTime">
          <t-date-picker v-model="formData.endTime" enable-time-picker format="YYYY-MM-DD HH:mm" />
        </t-form-item>
        <t-form-item label="请假天数" name="days">
          <t-input-number v-model="formData.days" :min="0.5" :step="0.5" :decimal-places="1" placeholder="请输入请假天数" />
          <span style="margin-left: 8px;">天</span>
        </t-form-item>
        <t-form-item label="请假事由" name="reason">
          <t-textarea v-model="formData.reason" placeholder="请输入请假事由" :maxlength="500" />
        </t-form-item>
        <t-form-item label="审批人" name="approverId">
          <t-select v-model="formData.approverId" placeholder="请选择审批人">
            <t-option value="1" label="部门经理" />
            <t-option value="2" label="人事主管" />
            <t-option value="3" label="副总经理" />
          </t-select>
        </t-form-item>
        <t-form-item label="附件">
          <t-upload
            v-model="formData.attachments"
            action="https://service-bv448zsw-1257786608.gz.apigw.tencentcs.com/api/upload"
            theme="file"
            tips="支持上传请假相关证明文件"
          />
        </t-form-item>
      </t-form>
    </t-dialog>

    <!-- 请假详情弹窗 -->
    <t-dialog v-model:visible="detailVisible" header="请假详情" width="700px" :footer="false">
      <t-descriptions v-if="currentLeave" :column="2" bordered>
        <t-descriptions-item label="申请单号">{{ currentLeave.leaveNo }}</t-descriptions-item>
        <t-descriptions-item label="状态">
          <t-tag v-if="currentLeave.status === 'pending'" theme="warning">待审批</t-tag>
          <t-tag v-else-if="currentLeave.status === 'approved'" theme="success">已通过</t-tag>
          <t-tag v-else-if="currentLeave.status === 'rejected'" theme="danger">已拒绝</t-tag>
          <t-tag v-else theme="default">已撤销</t-tag>
        </t-descriptions-item>
        <t-descriptions-item label="申请人">{{ currentLeave.applicantName }}</t-descriptions-item>
        <t-descriptions-item label="部门">{{ currentLeave.department }}</t-descriptions-item>
        <t-descriptions-item label="请假类型">
          <t-tag>{{ getLeaveTypeText(currentLeave.leaveType) }}</t-tag>
        </t-descriptions-item>
        <t-descriptions-item label="请假天数">{{ currentLeave.days }}天</t-descriptions-item>
        <t-descriptions-item label="开始时间">{{ currentLeave.startTime }}</t-descriptions-item>
        <t-descriptions-item label="结束时间">{{ currentLeave.endTime }}</t-descriptions-item>
        <t-descriptions-item label="申请时间">{{ currentLeave.applyTime }}</t-descriptions-item>
        <t-descriptions-item label="审批人">{{ currentLeave.approverName }}</t-descriptions-item>
        <t-descriptions-item label="请假事由" :span="2">{{ currentLeave.reason }}</t-descriptions-item>
        <t-descriptions-item v-if="currentLeave.approvalTime" label="审批时间">{{ currentLeave.approvalTime }}</t-descriptions-item>
        <t-descriptions-item v-if="currentLeave.approvalRemark" label="审批意见" :span="2">{{ currentLeave.approvalRemark }}</t-descriptions-item>
      </t-descriptions>
      <t-space style="margin-top: 16px;">
        <t-button v-if="currentLeave?.status === 'pending'" theme="primary" @click="handleApprove(currentLeave)">审批</t-button>
        <t-button v-if="currentLeave?.status === 'pending'" theme="danger" @click="handleReject(currentLeave)">拒绝</t-button>
        <t-button v-if="currentLeave?.status === 'pending'" theme="default" @click="handleCancel(currentLeave)">撤销</t-button>
        <t-button @click="detailVisible = false">关闭</t-button>
      </t-space>
    </t-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue';
import { MessagePlugin } from 'tdesign-vue-next';

// 搜索表单
const searchForm = reactive({
  applicantName: '',
  departmentId: '',
  leaveType: '',
  status: '',
  applyDate: [],
});

// 表格数据
const tableData = ref([]);
const loading = ref(false);

// 分页
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
});

// 弹窗
const dialogVisible = ref(false);
const detailVisible = ref(false);
const currentLeave = ref<any>(null);
const formRef = ref();
const saving = ref(false);

const formData = reactive({
  id: undefined,
  applicantId: '',
  leaveType: '',
  startTime: '',
  endTime: '',
  days: 1,
  reason: '',
  approverId: '',
  attachments: [],
});

const rules = {
  applicantId: [{ required: true, message: '请选择申请人', type: 'error' }],
  leaveType: [{ required: true, message: '请选择请假类型', type: 'error' }],
  startTime: [{ required: true, message: '请选择开始时间', type: 'error' }],
  endTime: [{ required: true, message: '请选择结束时间', type: 'error' }],
  days: [{ required: true, message: '请输入请假天数', type: 'error' }],
  reason: [{ required: true, message: '请输入请假事由', type: 'error' }],
  approverId: [{ required: true, message: '请选择审批人', type: 'error' }],
};

const isEdit = computed(() => !!formData.id);

// 表格列
const columns = [
  { colKey: 'leaveNo', title: '申请单号', width: 150 },
  { colKey: 'applicantName', title: '申请人', width: 100 },
  { colKey: 'department', title: '部门', width: 150 },
  { colKey: 'leaveType', title: '请假类型', width: 100 },
  { colKey: 'startTime', title: '开始时间', width: 180 },
  { colKey: 'endTime', title: '结束时间', width: 180 },
  { colKey: 'days', title: '天数', width: 80 },
  { colKey: 'status', title: '状态', width: 100 },
  { colKey: 'applyTime', title: '申请时间', width: 180 },
  { colKey: 'approverName', title: '审批人', width: 100 },
  { colKey: 'operation', title: '操作', width: 250, fixed: 'right' },
];

// 获取请假类型文本
const getLeaveTypeText = (type: string) => {
  const map: Record<string, string> = {
    '1': '事假',
    '2': '病假',
    '3': '年假',
    '4': '调休',
    '5': '婚假',
    '6': '产假',
  };
  return map[type] || type;
};

// 加载数据
const loadData = async () => {
  loading.value = true;
  try {
    await new Promise(resolve => setTimeout(resolve, 500));
    const mockData = [
      {
        id: 1,
        leaveNo: 'LEA20260219001',
        applicantId: '1',
        applicantName: '张三',
        department: '技术研发部',
        leaveType: '2',
        startTime: '20xx-xx-xx 09:00',
        endTime: '2026-02-21 18:00',
        days: 2,
        reason: '感冒发烧，去医院看病',
        status: 'pending',
        applyTime: '2026-02-19 10:30:00',
        approverId: '1',
        approverName: '部门经理',
      },
      {
        id: 2,
        leaveNo: 'LEA20260219002',
        applicantId: '2',
        applicantName: '李四',
        department: '市场部',
        leaveType: '3',
        startTime: '2026-02-22 09:00',
        endTime: '2026-02-26 18:00',
        days: 5,
        reason: '年假休假，回家探亲',
        status: 'approved',
        applyTime: '2026-02-18 15:20:00',
        approverId: '2',
        approverName: '人事主管',
        approvalTime: '2026-02-18 16:00:00',
        approvalRemark: '同意',
      },
    ];
    tableData.value = mockData;
    pagination.total = 35;
  } catch (error) {
    MessagePlugin.error('加载数据失败');
  } finally {
    loading.value = false;
  }
};

// 搜索
const handleSearch = () => {
  pagination.current = 1;
  loadData();
};

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    applicantName: '',
    departmentId: '',
    leaveType: '',
    status: '',
    applyDate: [],
  });
  handleSearch();
};

// 申请请假
const handleApply = () => {
  Object.assign(formData, {
    id: undefined,
    applicantId: '',
    leaveType: '',
    startTime: '',
    endTime: '',
    days: 1,
    reason: '',
    approverId: '',
    attachments: [],
  });
  dialogVisible.value = true;
};

// 提交
const handleSubmit = async () => {
  const valid = await formRef.value?.validate();
  if (!valid) return;

  saving.value = true;
  try {
    await new Promise(resolve => setTimeout(resolve, 500));
    MessagePlugin.success(isEdit.value ? '更新成功' : '申请成功');
    dialogVisible.value = false;
    loadData();
  } catch (error) {
    MessagePlugin.error('操作失败');
  } finally {
    saving.value = false;
  }
};

// 详情
const handleView = (row: any) => {
  currentLeave.value = row;
  detailVisible.value = true;
};

// 审批
const handleApprove = async (row: any) => {
  try {
    await new Promise(resolve => setTimeout(resolve, 300));
    MessagePlugin.success('审批通过');
    loadData();
  } catch (error) {
    MessagePlugin.error('审批失败');
  }
};

// 拒绝
const handleReject = async (row: any) => {
  try {
    await new Promise(resolve => setTimeout(resolve, 300));
    MessagePlugin.success('已拒绝');
    loadData();
  } catch (error) {
    MessagePlugin.error('操作失败');
  }
};

// 撤销
const handleCancel = async (row: any) => {
  try {
    await new Promise(resolve => setTimeout(resolve, 300));
    MessagePlugin.success('已撤销');
    loadData();
  } catch (error) {
    MessagePlugin.error('撤销失败');
  }
};

// 分页变化
const handlePageChange = (pageInfo: any) => {
  pagination.current = pageInfo.current;
  pagination.pageSize = pageInfo.pageSize;
  loadData();
};

loadData();
</script>

<style lang="scss" scoped>
.leave-page {
  .action-card,
  .search-card,
  .table-card {
    margin-bottom: 16px;
  }
}
</style>
