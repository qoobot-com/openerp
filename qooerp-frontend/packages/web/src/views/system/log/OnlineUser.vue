<template>
  <div class="online-user-page">
    <!-- 搜索卡片 -->
    <t-card class="search-card">
      <t-form ref="searchForm" :data="searchForm" layout="inline" @submit="handleSearch" @reset="handleReset">
        <t-form-item label="用户名" name="username">
          <t-input v-model="searchForm.username" placeholder="请输入用户名" clearable />
        </t-form-item>
        <t-form-item label="部门" name="department">
          <t-input v-model="searchForm.department" placeholder="请输入部门" clearable />
        </t-form-item>
        <t-form-item label="登录IP" name="ip">
          <t-input v-model="searchForm.ip" placeholder="请输入登录IP" clearable />
        </t-form-item>
        <t-form-item label="登录时间" name="timeRange">
          <t-date-range-picker
            v-model="searchForm.timeRange"
            placeholder="请选择登录时间"
            clearable
          />
        </t-form-item>
        <t-form-item>
          <t-space>
            <t-button theme="primary" type="submit">查询</t-button>
            <t-button theme="default" type="reset">重置</t-button>
          </t-space>
        </t-form-item>
      </t-form>
    </t-card>

    <!-- 操作栏 -->
    <t-card class="action-card">
      <t-space>
        <t-button theme="danger" :disabled="!selectedRowKeys.length" @click="handleBatchForceLogout">
          <template #icon><t-icon name="poweroff" /></template>
          批量强制下线
        </t-button>
        <t-button theme="default" @click="handleRefresh">
          <template #icon><t-icon name="refresh" /></template>
          刷新
        </t-button>
      </t-space>
      <div style="float: right; color: var(--td-text-color-secondary); padding: 8px 0;">
        当前在线用户数: <span style="font-weight: bold; color: var(--td-brand-color);">{{ totalCount }}</span> 人
      </div>
    </t-card>

    <!-- 用户表格 -->
    <t-card class="table-card">
      <t-table
        :data="tableData"
        :columns="columns"
        :selected-row-keys="selectedRowKeys"
        :loading="loading"
        :pagination="pagination"
        row-key="sessionId"
        @select-change="handleSelectChange"
        @page-change="handlePageChange"
      >
        <template #avatar="{ row }">
          <t-avatar :image="row.avatar" size="small" />
        </template>
        <template #status="{ row }">
          <t-tag v-if="row.status === 'online'" theme="success" variant="light">在线</t-tag>
          <t-tag v-else theme="default" variant="light">空闲</t-tag>
        </template>
        <template #operation="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleViewDetail(row)">详情</t-link>
            <t-popconfirm content="确认强制该用户下线吗？" @confirm="handleForceLogout(row.sessionId)">
              <t-link theme="danger">强制下线</t-link>
            </t-popconfirm>
          </t-space>
        </template>
      </t-table>
    </t-card>

    <!-- 用户详情弹窗 -->
    <t-dialog
      v-model:visible="detailDialogVisible"
      header="在线用户详情"
      width="700px"
      :footer="false"
    >
      <t-space direction="vertical" style="width: 100%;" :size="16">
        <t-card>
          <template #header>
            <t-space>
              <t-avatar :image="currentUser?.avatar" size="large" />
              <div>
                <div style="font-weight: bold;">{{ currentUser?.username }} ({{ currentUser?.realName }})</div>
                <div style="color: var(--td-text-color-secondary); font-size: 12px;">{{ currentUser?.department }}</div>
              </div>
              <t-tag v-if="currentUser?.status === 'online'" theme="success">在线</t-tag>
              <t-tag v-else theme="default">空闲</t-tag>
            </t-space>
          </template>
          <t-descriptions :column="2" bordered>
            <t-descriptions-item label="用户ID">{{ currentUser?.userId }}</t-descriptions-item>
            <t-descriptions-item label="Session ID">{{ currentUser?.sessionId }}</t-descriptions-item>
            <t-descriptions-item label="手机号">{{ currentUser?.phone }}</t-descriptions-item>
            <t-descriptions-item label="邮箱">{{ currentUser?.email }}</t-descriptions-item>
            <t-descriptions-item label="登录IP">{{ currentUser?.loginIp }}</t-descriptions-item>
            <t-descriptions-item label="登录地点">{{ currentUser?.loginLocation }}</t-descriptions-item>
            <t-descriptions-item label="浏览器">{{ currentUser?.browser }}</t-descriptions-item>
            <t-descriptions-item label="操作系统">{{ currentUser?.os }}</t-descriptions-item>
            <t-descriptions-item label="登录时间">{{ currentUser?.loginTime }}</t-descriptions-item>
            <t-descriptions-item label="最后活动时间">{{ currentUser?.lastActiveTime }}</t-descriptions-item>
            <t-descriptions-item label="在线时长">{{ currentUser?.onlineDuration }}</t-descriptions-item>
            <t-descriptions-item label="设备类型">{{ currentUser?.deviceType }}</t-descriptions-item>
          </t-descriptions>
        </t-card>
        <t-space>
          <t-button theme="danger" @click="handleForceLogout(currentUser?.sessionId)">
            <template #icon><t-icon name="poweroff" /></template>
            强制下线
          </t-button>
          <t-button theme="default" @click="detailDialogVisible = false">关闭</t-button>
        </t-space>
      </t-space>
    </t-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue';
import { MessagePlugin } from 'tdesign-vue-next';

// 搜索表单
const searchForm = reactive({
  username: '',
  department: '',
  ip: '',
  timeRange: [],
});

// 表格数据
const tableData = ref([]);
const loading = ref(false);
const selectedRowKeys = ref([]);
const totalCount = ref(0);

// 分页
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
});

// 详情弹窗
const detailDialogVisible = ref(false);
const currentUser = ref<any>(null);

// 表格列
const columns = [
  { colKey: 'row-select', type: 'multiple', width: 50 },
  { colKey: 'avatar', title: '头像', width: 80 },
  { colKey: 'username', title: '用户名', width: 120 },
  { colKey: 'realName', title: '姓名', width: 100 },
  { colKey: 'department', title: '部门', width: 150 },
  { colKey: 'loginIp', title: '登录IP', width: 140 },
  { colKey: 'loginLocation', title: '登录地点', width: 150 },
  { colKey: 'browser', title: '浏览器', width: 150 },
  { colKey: 'loginTime', title: '登录时间', width: 180 },
  { colKey: 'lastActiveTime', title: '最后活动', width: 180 },
  { colKey: 'status', title: '状态', width: 80 },
  { colKey: 'operation', title: '操作', width: 150, fixed: 'right' },
];

// 加载数据
const loadData = async () => {
  loading.value = true;
  try {
    await new Promise(resolve => setTimeout(resolve, 500));
    const mockData = [
      {
        sessionId: 'sess_001',
        userId: 1,
        username: 'admin',
        realName: '系统管理员',
        avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=1',
        department: '系统管理部',
        phone: '13800138000',
        email: 'admin@example.com',
        loginIp: '192.168.1.100',
        loginLocation: '北京市朝阳区',
        browser: 'Chrome 120.0',
        os: 'Windows 11',
        deviceType: 'PC',
        loginTime: '2026-02-19 09:30:00',
        lastActiveTime: '2026-02-19 15:25:30',
        onlineDuration: '5小时55分钟',
        status: 'online',
      },
      {
        sessionId: 'sess_002',
        userId: 2,
        username: 'zhangsan',
        realName: '张三',
        avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=2',
        department: '技术研发部',
        phone: '13800138001',
        email: 'zhangsan@example.com',
        loginIp: '192.168.1.101',
        loginLocation: '上海市浦东新区',
        browser: 'Firefox 121.0',
        os: 'MacOS 14',
        deviceType: 'PC',
        loginTime: '2026-02-19 10:15:20',
        lastActiveTime: '2026-02-19 14:50:15',
        onlineDuration: '4小时35分钟',
        status: 'idle',
      },
      {
        sessionId: 'sess_003',
        userId: 3,
        username: 'lisi',
        realName: '李四',
        avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=3',
        department: '销售部',
        phone: '13800138002',
        email: 'lisi@example.com',
        loginIp: '192.168.1.102',
        loginLocation: '广东省深圳市南山区',
        browser: 'Chrome 119.0',
        os: 'Windows 10',
        deviceType: 'PC',
        loginTime: '2026-02-19 11:20:10',
        lastActiveTime: '2026-02-19 15:20:45',
        onlineDuration: '4小时00分钟',
        status: 'online',
      },
    ];
    tableData.value = mockData;
    pagination.total = 35;
    totalCount.value = 35;
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
    username: '',
    department: '',
    ip: '',
    timeRange: [],
  });
  handleSearch();
};

// 刷新
const handleRefresh = () => {
  loadData();
  MessagePlugin.success('刷新成功');
};

// 批量强制下线
const handleBatchForceLogout = async () => {
  try {
    await new Promise(resolve => setTimeout(resolve, 300));
    MessagePlugin.success(`成功强制下线 ${selectedRowKeys.value.length} 个用户`);
    selectedRowKeys.value = [];
    loadData();
  } catch (error) {
    MessagePlugin.error('操作失败');
  }
};

// 强制下线
const handleForceLogout = async (sessionId: string) => {
  try {
    await new Promise(resolve => setTimeout(resolve, 300));
    MessagePlugin.success('强制下线成功');
    detailDialogVisible.value = false;
    loadData();
  } catch (error) {
    MessagePlugin.error('操作失败');
  }
};

// 查看详情
const handleViewDetail = (row: any) => {
  currentUser.value = row;
  detailDialogVisible.value = true;
};

// 选择变化
const handleSelectChange = (value: string[]) => {
  selectedRowKeys.value = value;
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
.online-user-page {
  .search-card,
  .action-card,
  .table-card {
    margin-bottom: 16px;
  }
}
</style>
