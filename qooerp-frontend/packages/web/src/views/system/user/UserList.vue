<template>
  <div class="user-list">
    <!-- 搜索卡片 -->
    <SearchCard :fields="searchFields" @search="handleSearch" @reset="handleReset" />

    <!-- 操作栏 -->
    <t-card :bordered="false" class="action-card">
      <t-button theme="primary" @click="handleAdd">
        <template #icon><t-icon name="add" /></template>
        新增用户
      </t-button>
      <t-button theme="default" :disabled="!selectedRowKeys.length" @click="handleBatchDelete">
        <template #icon><t-icon name="delete" /></template>
        批量删除
      </t-button>
      <t-button theme="default" @click="handleExport">
        <template #icon><t-icon name="download" /></template>
        导出
      </t-button>
    </t-card>

    <!-- 用户列表 -->
    <t-card :bordered="false">
      <t-table
        :data="tableData"
        :columns="columns"
        :row-key="rowKey"
        :selected-row-keys="selectedRowKeys"
        :loading="loading"
        :pagination="pagination"
        @select-change="handleSelectChange"
        @page-change="handlePageChange"
      >
        <template #status="{ row }">
          <t-tag v-if="row.status === 1" theme="success">正常</t-tag>
          <t-tag v-else theme="danger">禁用</t-tag>
        </template>

        <template #action="{ row }">
          <t-link theme="primary" @click="handleEdit(row)">编辑</t-link>
          <t-link theme="danger" @click="handleDelete(row)">删除</t-link>
          <t-link theme="default" @click="handleResetPassword(row)">重置密码</t-link>
        </template>
      </t-table>
    </t-card>

    <!-- 用户弹窗 -->
    <UserDialog
      v-model:visible="dialogVisible"
      :user-id="currentUserId"
      @success="handleDialogSuccess"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { MessagePlugin, DialogPlugin } from 'tdesign-vue-next'
import SearchCard from '@/components/common/SearchCard.vue'
import UserDialog from './UserDialog.vue'

// 搜索字段配置
const searchFields = [
  { label: '用户名', name: 'username', type: 'input', placeholder: '请输入用户名' },
  { label: '姓名', name: 'name', type: 'input', placeholder: '请输入姓名' },
  { label: '手机号', name: 'phone', type: 'input', placeholder: '请输入手机号' },
  {
    label: '状态',
    name: 'status',
    type: 'select',
    placeholder: '请选择状态',
    options: [
      { label: '正常', value: 1 },
      { label: '禁用', value: 0 },
    ],
  },
]

// 表格列配置
const columns = [
  { colKey: 'row-select', type: 'multiple', width: 50 },
  { colKey: 'index', title: '序号', width: 60 },
  { colKey: 'username', title: '用户名', width: 120 },
  { colKey: 'name', title: '姓名', width: 100 },
  { colKey: 'phone', title: '手机号', width: 130 },
  { colKey: 'email', title: '邮箱', width: 180 },
  { colKey: 'deptName', title: '部门', width: 120 },
  { colKey: 'roleName', title: '角色', width: 120 },
  { colKey: 'status', title: '状态', width: 80 },
  { colKey: 'createTime', title: '创建时间', width: 180 },
  { colKey: 'action', title: '操作', width: 180, fixed: 'right' },
]

const rowKey = 'id'
const loading = ref(false)
const tableData = ref<any[]>([])
const selectedRowKeys = ref<any[]>([])
const dialogVisible = ref(false)
const currentUserId = ref('')

// 分页配置
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  pageSizeOptions: [10, 20, 50, 100],
})

// 模拟数据
const mockData = [
  {
    id: '1',
    username: 'admin',
    name: '管理员',
    phone: '13800138000',
    email: 'admin@qooerp.com',
    deptName: '技术部',
    roleName: '超级管理员',
    status: 1,
    createTime: '2024-01-01 10:00:00',
  },
  {
    id: '2',
    username: 'zhangsan',
    name: '张三',
    phone: '13800138001',
    email: 'zhangsan@qooerp.com',
    deptName: '销售部',
    roleName: '销售经理',
    status: 1,
    createTime: '2024-01-02 10:00:00',
  },
  {
    id: '3',
    username: 'lisi',
    name: '李四',
    phone: '13800138002',
    email: 'lisi@qooerp.com',
    deptName: '财务部',
    roleName: '财务专员',
    status: 0,
    createTime: '2024-01-03 10:00:00',
  },
]

// 获取列表数据
const fetchData = async (params: any = {}) => {
  loading.value = true
  try {
    await new Promise((resolve) => setTimeout(resolve, 500))
    tableData.value = mockData
    pagination.total = 3
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = (values: any) => {
  pagination.current = 1
  fetchData(values)
}

// 重置
const handleReset = () => {
  pagination.current = 1
  fetchData()
}

// 新增
const handleAdd = () => {
  currentUserId.value = ''
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row: any) => {
  currentUserId.value = row.id
  dialogVisible.value = true
}

// 删除
const handleDelete = (row: any) => {
  DialogPlugin.confirm({
    title: '确认删除',
    content: `确定要删除用户"${row.name}"吗？`,
    onConfirm: async () => {
      await new Promise((resolve) => setTimeout(resolve, 300))
      MessagePlugin.success('删除成功')
      fetchData()
    },
  })
}

// 批量删除
const handleBatchDelete = () => {
  DialogPlugin.confirm({
    title: '确认删除',
    content: `确定要删除选中的 ${selectedRowKeys.value.length} 个用户吗？`,
    onConfirm: async () => {
      await new Promise((resolve) => setTimeout(resolve, 300))
      MessagePlugin.success('删除成功')
      selectedRowKeys.value = []
      fetchData()
    },
  })
}

// 导出
const handleExport = () => {
  MessagePlugin.info('导出功能开发中...')
}

// 重置密码
const handleResetPassword = (row: any) => {
  DialogPlugin.confirm({
    title: '确认重置密码',
    content: `确定要重置用户"${row.name}"的密码吗？`,
    onConfirm: async () => {
      await new Promise((resolve) => setTimeout(resolve, 300))
      MessagePlugin.success('密码重置成功，新密码为: 123456')
    },
  })
}

// 选择行
const handleSelectChange = (value: any) => {
  selectedRowKeys.value = value
}

// 分页变化
const handlePageChange = (pageInfo: any) => {
  pagination.current = pageInfo.current
  pagination.pageSize = pageInfo.pageSize
  fetchData()
}

// 弹窗成功回调
const handleDialogSuccess = () => {
  dialogVisible.value = false
  fetchData()
}

// 初始化
fetchData()
</script>

<style scoped lang="scss">
.user-list {
  .action-card {
    margin-bottom: 16px;

    :deep(.t-card__body) {
      display: flex;
      gap: 12px;
    }
  }
}
</style>
