<template>
  <div class="operation-log-page">
    <!-- 搜索卡片 -->
    <t-card class="search-card">
      <t-form ref="searchForm" :data="searchForm" layout="inline" @submit="handleSearch" @reset="handleReset">
        <t-form-item label="操作人" name="operator">
          <t-input v-model="searchForm.operator" placeholder="请输入操作人" clearable />
        </t-form-item>
        <t-form-item label="操作模块" name="module">
          <t-select v-model="searchForm.module" placeholder="请选择操作模块" clearable>
            <t-option value="system" label="系统管理" />
            <t-option value="user" label="用户管理" />
            <t-option value="role" label="角色管理" />
            <t-option value="menu" label="菜单管理" />
            <t-option value="dept" label="部门管理" />
          </t-select>
        </t-form-item>
        <t-form-item label="操作类型" name="operationType">
          <t-select v-model="searchForm.operationType" placeholder="请选择操作类型" clearable>
            <t-option value="login" label="登录" />
            <t-option value="create" label="新增" />
            <t-option value="update" label="修改" />
            <t-option value="delete" label="删除" />
            <t-option value="export" label="导出" />
            <t-option value="import" label="导入" />
          </t-select>
        </t-form-item>
        <t-form-item label="状态" name="status">
          <t-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <t-option value="success" label="成功" />
            <t-option value="fail" label="失败" />
          </t-select>
        </t-form-item>
        <t-form-item label="操作时间" name="timeRange">
          <t-date-range-picker
            v-model="searchForm.timeRange"
            placeholder="请选择操作时间"
            clearable
          />
        </t-form-item>
        <t-form-item>
          <t-space>
            <t-button theme="primary" type="submit">查询</t-button>
            <t-button theme="default" type="reset">重置</t-button>
            <t-button theme="default" @click="handleExport">
              <template #icon><t-icon name="download" /></template>
              导出
            </t-button>
          </t-space>
        </t-form-item>
      </t-form>
    </t-card>

    <!-- 操作栏 -->
    <t-card class="action-card">
      <t-space>
        <t-button theme="danger" :disabled="!selectedRowKeys.length" @click="handleBatchDelete">
          <template #icon><t-icon name="delete" /></template>
          批量删除
        </t-button>
        <t-button theme="default" @click="handleClearAll">
          <template #icon><t-icon name="delete" /></template>
          清空日志
        </t-button>
      </t-space>
    </t-card>

    <!-- 日志表格 -->
    <t-card class="table-card">
      <t-table
        :data="tableData"
        :columns="columns"
        :selected-row-keys="selectedRowKeys"
        :loading="loading"
        :pagination="pagination"
        row-key="id"
        @select-change="handleSelectChange"
        @page-change="handlePageChange"
      >
        <template #status="{ row }">
          <t-tag v-if="row.status === 'success'" theme="success" variant="light">成功</t-tag>
          <t-tag v-else theme="danger" variant="light">失败</t-tag>
        </template>
        <template #operation="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleViewDetail(row)">详情</t-link>
            <t-popconfirm content="确认删除该日志吗？" @confirm="handleDelete(row.id)">
              <t-link theme="danger">删除</t-link>
            </t-popconfirm>
          </t-space>
        </template>
      </t-table>
    </t-card>

    <!-- 日志详情弹窗 -->
    <t-dialog
      v-model:visible="detailDialogVisible"
      header="操作日志详情"
      width="800px"
      :footer="false"
    >
      <t-descriptions :column="2" bordered>
        <t-descriptions-item label="日志ID">{{ currentLog?.id }}</t-descriptions-item>
        <t-descriptions-item label="操作人">{{ currentLog?.operator }}</t-descriptions-item>
        <t-descriptions-item label="操作人ID">{{ currentLog?.operatorId }}</t-descriptions-item>
        <t-descriptions-item label="所属部门">{{ currentLog?.department }}</t-descriptions-item>
        <t-descriptions-item label="操作模块">{{ currentLog?.module }}</t-descriptions-item>
        <t-descriptions-item label="操作类型">
          <t-tag>{{ currentLog?.operationType }}</t-tag>
        </t-descriptions-item>
        <t-descriptions-item label="操作名称">{{ currentLog?.operationName }}</t-descriptions-item>
        <t-descriptions-item label="状态">
          <t-tag v-if="currentLog?.status === 'success'" theme="success">成功</t-tag>
          <t-tag v-else theme="danger">失败</t-tag>
        </t-descriptions-item>
        <t-descriptions-item label="请求方式">{{ currentLog?.requestMethod }}</t-descriptions-item>
        <t-descriptions-item label="请求URL">{{ currentLog?.requestUrl }}</t-descriptions-item>
        <t-descriptions-item label="客户端IP">{{ currentLog?.clientIp }}</t-descriptions-item>
        <t-descriptions-item label="浏览器">{{ currentLog?.browser }}</t-descriptions-item>
        <t-descriptions-item label="操作系统">{{ currentLog?.os }}</t-descriptions-item>
        <t-descriptions-item label="操作时间" :span="2">{{ currentLog?.operationTime }}</t-descriptions-item>
        <t-descriptions-item label="请求参数" :span="2">
          <pre style="max-height: 200px; overflow: auto; margin: 0;">{{ currentLog?.requestParams }}</pre>
        </t-descriptions-item>
        <t-descriptions-item label="响应结果" :span="2">
          <pre style="max-height: 200px; overflow: auto; margin: 0;">{{ currentLog?.response }}</pre>
        </t-descriptions-item>
        <t-descriptions-item v-if="currentLog?.errorMsg" label="错误信息" :span="2">
          <span style="color: var(--td-error-color);">{{ currentLog?.errorMsg }}</span>
        </t-descriptions-item>
      </t-descriptions>
    </t-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue';
import { MessagePlugin } from 'tdesign-vue-next';

// 搜索表单
const searchForm = reactive({
  operator: '',
  module: '',
  operationType: '',
  status: '',
  timeRange: [],
});

// 表格数据
const tableData = ref([]);
const loading = ref(false);
const selectedRowKeys = ref([]);

// 分页
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
});

// 详情弹窗
const detailDialogVisible = ref(false);
const currentLog = ref<any>(null);

// 表格列
const columns = [
  { colKey: 'row-select', type: 'multiple', width: 50 },
  { colKey: 'operator', title: '操作人', width: 120 },
  { colKey: 'department', title: '所属部门', width: 150 },
  { colKey: 'module', title: '操作模块', width: 120 },
  { colKey: 'operationType', title: '操作类型', width: 100 },
  { colKey: 'operationName', title: '操作名称', width: 150 },
  { colKey: 'clientIp', title: '客户端IP', width: 140 },
  { colKey: 'browser', title: '浏览器', width: 150 },
  { colKey: 'status', title: '状态', width: 80 },
  { colKey: 'operationTime', title: '操作时间', width: 180 },
  { colKey: 'operation', title: '操作', width: 120, fixed: 'right' },
];

// 加载数据
const loadData = async () => {
  loading.value = true;
  try {
    await new Promise(resolve => setTimeout(resolve, 500));
    const mockData = [
      {
        id: 1,
        operator: 'admin',
        operatorId: 1,
        department: '系统管理部',
        module: 'system',
        operationType: 'login',
        operationName: '用户登录',
        requestMethod: 'POST',
        requestUrl: '/api/auth/login',
        clientIp: '192.168.1.100',
        browser: 'Chrome 120.0',
        os: 'Windows 11',
        status: 'success',
        operationTime: '2026-02-19 14:30:25',
        requestParams: '{"username":"admin","password":"***"}',
        response: '{"code":200,"message":"登录成功"}',
      },
      {
        id: 2,
        operator: 'admin',
        operatorId: 1,
        department: '系统管理部',
        module: 'user',
        operationType: 'create',
        operationName: '新增用户',
        requestMethod: 'POST',
        requestUrl: '/api/system/user',
        clientIp: '192.168.1.100',
        browser: 'Chrome 120.0',
        os: 'Windows 11',
        status: 'success',
        operationTime: '2026-02-19 14:32:10',
        requestParams: '{"username":"test","name":"测试用户"}',
        response: '{"code":200,"message":"创建成功"}',
      },
      {
        id: 3,
        operator: 'test',
        operatorId: 2,
        department: '技术研发部',
        module: 'user',
        operationType: 'update',
        operationName: '修改用户',
        requestMethod: 'PUT',
        requestUrl: '/api/system/user/2',
        clientIp: '192.168.1.101',
        browser: 'Firefox 121.0',
        os: 'MacOS 14',
        status: 'fail',
        operationTime: '2026-02-19 14:35:20',
        requestParams: '{"phone":"13800138000"}',
        response: '{"code":400,"message":"手机号格式错误"}',
        errorMsg: '手机号格式错误',
      },
    ];
    tableData.value = mockData;
    pagination.total = 50;
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
    operator: '',
    module: '',
    operationType: '',
    status: '',
    timeRange: [],
  });
  handleSearch();
};

// 导出
const handleExport = async () => {
  try {
    await new Promise(resolve => setTimeout(resolve, 500));
    MessagePlugin.success('导出成功');
  } catch (error) {
    MessagePlugin.error('导出失败');
  }
};

// 批量删除
const handleBatchDelete = async () => {
  try {
    await new Promise(resolve => setTimeout(resolve, 300));
    MessagePlugin.success(`成功删除 ${selectedRowKeys.value.length} 条日志`);
    selectedRowKeys.value = [];
    loadData();
  } catch (error) {
    MessagePlugin.error('删除失败');
  }
};

// 清空日志
const handleClearAll = async () => {
  try {
    await new Promise(resolve => setTimeout(resolve, 300));
    MessagePlugin.success('清空日志成功');
    loadData();
  } catch (error) {
    MessagePlugin.error('清空失败');
  }
};

// 删除
const handleDelete = async (id: number) => {
  try {
    await new Promise(resolve => setTimeout(resolve, 300));
    MessagePlugin.success('删除成功');
    loadData();
  } catch (error) {
    MessagePlugin.error('删除失败');
  }
};

// 查看详情
const handleViewDetail = (row: any) => {
  currentLog.value = row;
  detailDialogVisible.value = true;
};

// 选择变化
const handleSelectChange = (value: number[]) => {
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
.operation-log-page {
  .search-card,
  .action-card,
  .table-card {
    margin-bottom: 16px;
  }

  pre {
    background: var(--td-bg-color-container-hover);
    padding: 12px;
    border-radius: 4px;
    font-size: 12px;
  }
}
</style>
