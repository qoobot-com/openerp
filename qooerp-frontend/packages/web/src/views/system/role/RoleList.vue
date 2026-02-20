<template>
  <div class="role-list">
    <!-- 搜索卡片 -->
    <SearchCard
      :fields="searchFields"
      @search="handleSearch"
      @reset="handleReset"
    />

    <!-- 操作栏 -->
    <t-card :bordered="false" class="action-card">
      <t-button theme="primary" @click="handleAdd">
        <template #icon><t-icon name="add" /></template>
        新增角色
      </t-button>
      <t-button theme="default" :disabled="!selectedRowKeys.length" @click="handleBatchDelete">
        <template #icon><t-icon name="delete" /></template>
        批量删除
      </t-button>
    </t-card>

    <!-- 角色列表 -->
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
          <t-link theme="default" @click="handlePermission(row)">权限配置</t-link>
          <t-link theme="danger" @click="handleDelete(row)">删除</t-link>
        </template>
      </t-table>
    </t-card>

    <!-- 角色弹窗 -->
    <RoleDialog
      v-model:visible="dialogVisible"
      :role-id="currentRoleId"
      @success="handleDialogSuccess"
    />

    <!-- 权限配置弹窗 -->
    <PermissionDialog
      v-model:visible="permissionVisible"
      :role-id="currentRoleId"
      @success="handlePermissionSuccess"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { MessagePlugin, DialogPlugin } from 'tdesign-vue-next'
import SearchCard from '@/components/common/SearchCard.vue'
import RoleDialog from './RoleDialog.vue'
import PermissionDialog from './PermissionDialog.vue'

const searchFields = [
  { label: '角色名称', name: 'name', type: 'input', placeholder: '请输入角色名称' },
  { label: '角色编码', name: 'code', type: 'input', placeholder: '请输入角色编码' },
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

const columns = [
  { colKey: 'row-select', type: 'multiple', width: 50 },
  { colKey: 'index', title: '序号', width: 60 },
  { colKey: 'name', title: '角色名称', width: 150 },
  { colKey: 'code', title: '角色编码', width: 150 },
  { colKey: 'description', title: '描述', width: 200 },
  { colKey: 'userCount', title: '用户数', width: 100 },
  { colKey: 'status', title: '状态', width: 80 },
  { colKey: 'createTime', title: '创建时间', width: 180 },
  { colKey: 'action', title: '操作', width: 200, fixed: 'right' },
]

const rowKey = 'id'
const loading = ref(false)
const tableData = ref<any[]>([])
const selectedRowKeys = ref<any[]>([])
const dialogVisible = ref(false)
const permissionVisible = ref(false)
const currentRoleId = ref('')

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  pageSizeOptions: [10, 20, 50, 100],
})

const mockData = [
  {
    id: '1',
    name: '超级管理员',
    code: 'super_admin',
    description: '拥有系统所有权限',
    userCount: 1,
    status: 1,
    createTime: '2024-01-01 10:00:00',
  },
  {
    id: '2',
    name: '普通管理员',
    code: 'admin',
    description: '拥有部分管理权限',
    userCount: 5,
    status: 1,
    createTime: '2024-01-02 10:00:00',
  },
  {
    id: '3',
    name: '普通用户',
    code: 'user',
    description: '普通用户权限',
    userCount: 50,
    status: 1,
    createTime: '2024-01-03 10:00:00',
  },
]

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

const handleSearch = (values: any) => {
  pagination.current = 1
  fetchData(values)
}

const handleReset = () => {
  pagination.current = 1
  fetchData()
}

const handleAdd = () => {
  currentRoleId.value = ''
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  currentRoleId.value = row.id
  dialogVisible.value = true
}

const handlePermission = (row: any) => {
  currentRoleId.value = row.id
  permissionVisible.value = true
}

const handleDelete = (row: any) => {
  DialogPlugin.confirm({
    title: '确认删除',
    content: `确定要删除角色"${row.name}"吗？`,
    onConfirm: async () => {
      await new Promise((resolve) => setTimeout(resolve, 300))
      MessagePlugin.success('删除成功')
      fetchData()
    },
  })
}

const handleBatchDelete = () => {
  DialogPlugin.confirm({
    title: '确认删除',
    content: `确定要删除选中的 ${selectedRowKeys.value.length} 个角色吗？`,
    onConfirm: async () => {
      await new Promise((resolve) => setTimeout(resolve, 300))
      MessagePlugin.success('删除成功')
      selectedRowKeys.value = []
      fetchData()
    },
  })
}

const handleSelectChange = (value: any) => {
  selectedRowKeys.value = value
}

const handlePageChange = (pageInfo: any) => {
  pagination.current = pageInfo.current
  pagination.pageSize = pageInfo.pageSize
  fetchData()
}

const handleDialogSuccess = () => {
  dialogVisible.value = false
  fetchData()
}

const handlePermissionSuccess = () => {
  permissionVisible.value = false
}

fetchData()
</script>

<style scoped lang="scss">
.role-list {
  .action-card {
    margin-bottom: 16px;

    :deep(.t-card__body) {
      display: flex;
      gap: 12px;
    }
  }
}
</style>
