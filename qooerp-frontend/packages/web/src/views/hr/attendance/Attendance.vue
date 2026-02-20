<template>
  <div class="attendance-page">
    <!-- 统计卡片 -->
    <t-row :gutter="16" style="margin-bottom: 16px;">
      <t-col :span="6">
        <t-card>
          <t-statistic title="今日出勤" :value="stats.todayAttendance" suffix="人" />
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card>
          <t-statistic title="今日请假" :value="stats.todayLeave" suffix="人" />
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card>
          <t-statistic title="今日加班" :value="stats.todayOvertime" suffix="人" />
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card>
          <t-statistic title="今日迟到" :value="stats.todayLate" suffix="人" />
        </t-card>
      </t-col>
    </t-row>

    <!-- 搜索卡片 -->
    <t-card class="search-card">
      <t-form ref="searchForm" :data="searchForm" layout="inline" @submit="handleSearch" @reset="handleReset">
        <t-form-item label="姓名" name="name">
          <t-input v-model="searchForm.name" placeholder="请输入姓名" clearable />
        </t-form-item>
        <t-form-item label="部门" name="departmentId">
          <t-select v-model="searchForm.departmentId" placeholder="请选择部门" clearable>
            <t-option value="1" label="技术研发部" />
            <t-option value="2" label="市场部" />
            <t-option value="3" label="运营部" />
            <t-option value="4" label="财务部" />
          </t-select>
        </t-form-item>
        <t-form-item label="考勤日期" name="attendanceDate">
          <t-date-picker v-model="searchForm.attendanceDate" placeholder="请选择考勤日期" clearable />
        </t-form-item>
        <t-form-item label="考勤状态" name="status">
          <t-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <t-option value="normal" label="正常" />
            <t-option value="late" label="迟到" />
            <t-option value="early" label="早退" />
            <t-option value="absent" label="缺勤" />
            <t-option value="leave" label="请假" />
          </t-select>
        </t-form-item>
        <t-form-item>
          <t-space>
            <t-button theme="primary" type="submit">查询</t-button>
            <t-button theme="default" type="reset">重置</t-button>
            <t-button theme="success" @click="handleBatchCheckIn">
              <template #icon><t-icon name="check" /></template>
              批量签到
            </t-button>
          </t-space>
        </t-form-item>
      </t-form>
    </t-card>

    <!-- 考勤表格 -->
    <t-card class="table-card">
      <t-table
        :data="tableData"
        :columns="columns"
        :loading="loading"
        :pagination="pagination"
        row-key="id"
        @page-change="handlePageChange"
      >
        <template #avatar="{ row }">
          <t-avatar :image="row.avatar" size="small" />
        </template>
        <template #status="{ row }">
          <t-tag v-if="row.status === 'normal'" theme="success" variant="light">正常</t-tag>
          <t-tag v-else-if="row.status === 'late'" theme="warning" variant="light">迟到</t-tag>
          <t-tag v-else-if="row.status === 'early'" theme="warning" variant="light">早退</t-tag>
          <t-tag v-else-if="row.status === 'absent'" theme="danger" variant="light">缺勤</t-tag>
          <t-tag v-else-if="row.status === 'leave'" theme="primary" variant="light">请假</t-tag>
        </template>
        <template #operation="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleDetail(row)">详情</t-link>
            <t-link theme="primary" @click="handleEdit(row)">编辑</t-link>
          </t-space>
        </template>
      </t-table>
    </t-card>

    <!-- 考勤详情弹窗 -->
    <t-dialog v-model:visible="detailVisible" header="考勤详情" width="600px" :footer="false">
      <t-descriptions v-if="currentAttendance" :column="2" bordered>
        <t-descriptions-item label="姓名">{{ currentAttendance.name }}</t-descriptions-item>
        <t-descriptions-item label="工号">{{ currentAttendance.employeeNo }}</t-descriptions-item>
        <t-descriptions-item label="部门">{{ currentAttendance.department }}</t-descriptions-item>
        <t-descriptions-item label="考勤日期">{{ currentAttendance.attendanceDate }}</t-descriptions-item>
        <t-descriptions-item label="上班时间">{{ currentAttendance.checkInTime }}</t-descriptions-item>
        <t-descriptions-item label="下班时间">{{ currentAttendance.checkOutTime }}</t-descriptions-item>
        <t-descriptions-item label="工作时长">{{ currentAttendance.workHours }}小时</t-descriptions-item>
        <t-descriptions-item label="状态">
          <t-tag v-if="currentAttendance.status === 'normal'" theme="success">正常</t-tag>
          <t-tag v-else-if="currentAttendance.status === 'late'" theme="warning">迟到</t-tag>
          <t-tag v-else-if="currentAttendance.status === 'early'" theme="warning">早退</t-tag>
          <t-tag v-else-if="currentAttendance.status === 'absent'" theme="danger">缺勤</t-tag>
          <t-tag v-else theme="primary">请假</t-tag>
        </t-descriptions-item>
        <t-descriptions-item v-if="currentAttendance.remark" label="备注" :span="2">{{ currentAttendance.remark }}</t-descriptions-item>
      </t-descriptions>
    </t-dialog>

    <!-- 编辑弹窗 -->
    <t-dialog
      v-model:visible="editVisible"
      header="编辑考勤"
      width="500px"
      :confirm-btn="{
        content: '保存',
        theme: 'primary',
        loading: saving,
      }"
      @confirm="handleSubmit"
    >
      <t-form v-if="currentAttendance" ref="formRef" :data="formData" label-align="right" label-width="100px">
        <t-form-item label="上班时间">
          <t-time-picker v-model="formData.checkInTime" format="HH:mm" />
        </t-form-item>
        <t-form-item label="下班时间">
          <t-time-picker v-model="formData.checkOutTime" format="HH:mm" />
        </t-form-item>
        <t-form-item label="考勤状态">
          <t-select v-model="formData.status">
            <t-option value="normal" label="正常" />
            <t-option value="late" label="迟到" />
            <t-option value="early" label="早退" />
            <t-option value="absent" label="缺勤" />
            <t-option value="leave" label="请假" />
          </t-select>
        </t-form-item>
        <t-form-item label="备注">
          <t-textarea v-model="formData.remark" placeholder="请输入备注" :maxlength="200" />
        </t-form-item>
      </t-form>
    </t-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue';
import { MessagePlugin } from 'tdesign-vue-next';

// 统计数据
const stats = reactive({
  todayAttendance: 128,
  todayLeave: 8,
  todayOvertime: 12,
  todayLate: 5,
});

// 搜索表单
const searchForm = reactive({
  name: '',
  departmentId: '',
  attendanceDate: '',
  status: '',
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
const detailVisible = ref(false);
const editVisible = ref(false);
const currentAttendance = ref<any>(null);
const formRef = ref();
const saving = ref(false);

const formData = reactive({
  id: undefined,
  checkInTime: '',
  checkOutTime: '',
  status: 'normal',
  remark: '',
});

// 表格列
const columns = [
  { colKey: 'avatar', title: '头像', width: 80 },
  { colKey: 'employeeNo', title: '工号', width: 120 },
  { colKey: 'name', title: '姓名', width: 100 },
  { colKey: 'department', title: '部门', width: 150 },
  { colKey: 'attendanceDate', title: '考勤日期', width: 120 },
  { colKey: 'checkInTime', title: '上班时间', width: 100 },
  { colKey: 'checkOutTime', title: '下班时间', width: 100 },
  { colKey: 'workHours', title: '工作时长', width: 100 },
  { colKey: 'status', title: '状态', width: 100 },
  { colKey: 'operation', title: '操作', width: 150, fixed: 'right' },
];

// 加载数据
const loadData = async () => {
  loading.value = true;
  try {
    await new Promise(resolve => setTimeout(resolve, 500));
    const mockData = [
      {
        id: 1,
        avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=1',
        employeeNo: 'E001',
        name: '张三',
        department: '技术研发部',
        attendanceDate: '2026-02-19',
        checkInTime: '08:55',
        checkOutTime: '18:05',
        workHours: 9.2,
        status: 'normal',
        remark: '',
      },
      {
        id: 2,
        avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=2',
        employeeNo: 'E002',
        name: '李四',
        department: '市场部',
        attendanceDate: '2026-02-19',
        checkInTime: '09:15',
        checkOutTime: '18:00',
        workHours: 8.8,
        status: 'late',
        remark: '迟到15分钟',
      },
      {
        id: 3,
        avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=3',
        employeeNo: 'E003',
        name: '王五',
        department: '运营部',
        attendanceDate: '2026-02-19',
        checkInTime: '',
        checkOutTime: '',
        workHours: 0,
        status: 'leave',
        remark: '病假',
      },
    ];
    tableData.value = mockData;
    pagination.total = 150;
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
    name: '',
    departmentId: '',
    attendanceDate: '',
    status: '',
  });
  handleSearch();
};

// 批量签到
const handleBatchCheckIn = () => {
  MessagePlugin.info('批量签到功能开发中');
};

// 详情
const handleDetail = (row: any) => {
  currentAttendance.value = row;
  detailVisible.value = true;
};

// 编辑
const handleEdit = (row: any) => {
  currentAttendance.value = row;
  Object.assign(formData, {
    id: row.id,
    checkInTime: row.checkInTime,
    checkOutTime: row.checkOutTime,
    status: row.status,
    remark: row.remark,
  });
  editVisible.value = true;
};

// 提交
const handleSubmit = async () => {
  saving.value = true;
  try {
    await new Promise(resolve => setTimeout(resolve, 500));
    MessagePlugin.success('保存成功');
    editVisible.value = false;
    loadData();
  } catch (error) {
    MessagePlugin.error('保存失败');
  } finally {
    saving.value = false;
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
.attendance-page {
  .search-card,
  .table-card {
    margin-bottom: 16px;
  }
}
</style>
