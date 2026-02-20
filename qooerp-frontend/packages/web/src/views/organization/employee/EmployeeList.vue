<template>
  <div class="employee-list">
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
        新增员工
      </t-button>
      <t-button theme="default" :disabled="!selectedRowKeys.length" @click="handleBatchDelete">
        <template #icon><t-icon name="delete" /></template>
        批量删除
      </t-button>
      <t-button theme="default" @click="handleImport">
        <template #icon><t-icon name="upload" /></template>
        导入
      </t-button>
      <t-button theme="default" @click="handleExport">
        <template #icon><t-icon name="download" /></template>
        导出
      </t-button>
    </t-card>

    <!-- 员工列表 -->
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
        <template #avatar="{ row }">
          <t-avatar :image="row.avatar || ''">{{ row.name?.charAt(0) }}</t-avatar>
        </template>

        <template #gender="{ row }">
          <t-tag v-if="row.gender === 'male'" theme="primary">男</t-tag>
          <t-tag v-else-if="row.gender === 'female'" theme="danger">女</t-tag>
          <t-tag v-else theme="default">未知</t-tag>
        </template>

        <template #status="{ row }">
          <t-tag v-if="row.status === 1" theme="success">在职</t-tag>
          <t-tag v-else-if="row.status === 2" theme="warning">离职</t-tag>
          <t-tag v-else theme="default">试用期</t-tag>
        </template>

        <template #action="{ row }">
          <t-link theme="primary" @click="handleView(row)">详情</t-link>
          <t-link theme="default" @click="handleEdit(row)">编辑</t-link>
          <t-link theme="danger" @click="handleDelete(row)">删除</t-link>
        </template>
      </t-table>
    </t-card>

    <!-- 员工弹窗 -->
    <EmployeeDialog
      v-model:visible="dialogVisible"
      :employee-id="currentEmployeeId"
      @success="handleDialogSuccess"
    />

    <!-- 详情弹窗 -->
    <EmployeeDetailDialog
      v-model:visible="detailVisible"
      :employee-id="currentEmployeeId"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { MessagePlugin, DialogPlugin } from 'tdesign-vue-next'
import SearchCard from '@/components/common/SearchCard.vue'
import EmployeeDialog from './EmployeeDialog.vue'
import EmployeeDetailDialog from './EmployeeDetailDialog.vue'

const searchFields = [
  { label: '姓名', name: 'name', type: 'input', placeholder: '请输入姓名' },
  { label: '工号', name: 'code', type: 'input', placeholder: '请输入工号' },
  { label: '手机号', name: 'phone', type: 'input', placeholder: '请输入手机号' },
  {
    label: '部门',
    name: 'deptId',
    type: 'select',
    placeholder: '请选择部门',
    options: [
      { label: '技术部', value: '1' },
      { label: '销售部', value: '2' },
      { label: '财务部', value: '3' },
      { label: '人事部', value: '4' },
    ],
  },
  {
    label: '状态',
    name: 'status',
    type: 'select',
    placeholder: '请选择状态',
    options: [
      { label: '在职', value: 1 },
      { label: '试用期', value: 0 },
      { label: '离职', value: 2 },
    ],
  },
]

const columns = [
  { colKey: 'row-select', type: 'multiple', width: 50 },
  { colKey: 'index', title: '序号', width: 60 },
  { colKey: 'avatar', title: '头像', width: 80 },
  { colKey: 'code', title: '工号', width: 100 },
  { colKey: 'name', title: '姓名', width: 100 },
  { colKey: 'gender', title: '性别', width: 60 },
  { colKey: 'phone', title: '手机号', width: 130 },
  { colKey: 'email', title: '邮箱', width: 180 },
  { colKey: 'deptName', title: '部门', width: 120 },
  { colKey: 'positionName', title: '岗位', width: 120 },
  { colKey: 'status', title: '状态', width: 80 },
  { colKey: 'entryDate', title: '入职日期', width: 120 },
  { colKey: 'action', title: '操作', width: 160, fixed: 'right' },
]

const rowKey = 'id'
const loading = ref(false)
const tableData = ref<any[]>([])
const selectedRowKeys = ref<any[]>([])
const dialogVisible = ref(false)
const detailVisible = ref(false)
const currentEmployeeId = ref('')

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
    avatar: '',
    code: 'EMP001',
    name: '张三',
    gender: 'male',
    phone: '13800138001',
    email: 'zhangsan@qooerp.com',
    deptName: '技术部',
    positionName: '前端工程师',
    status: 1,
    entryDate: '2024-01-01',
  },
  {
    id: '2',
    avatar: '',
    code: 'EMP002',
    name: '李四',
    gender: 'female',
    phone: '13800138002',
    email: 'lisi@qooerp.com',
    deptName: '销售部',
    positionName: '销售经理',
    status: 1,
    entryDate: '2024-02-01',
  },
  {
    id: '3',
    avatar: '',
    code: 'EMP003',
    name: '王五',
    gender: 'male',
    phone: '13800138003',
    email: 'wangwu@qooerp.com',
    deptName: '财务部',
    positionName: '财务专员',
    status: 0,
    entryDate: '2024-03-01',
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
  currentEmployeeId.value = ''
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  currentEmployeeId.value = row.id
  dialogVisible.value = true
}

const handleView = (row: any) => {
  currentEmployeeId.value = row.id
  detailVisible.value = true
}

const handleDelete = (row: any) => {
  DialogPlugin.confirm({
    title: '确认删除',
    content: `确定要删除员工"${row.name}"吗？`,
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
    content: `确定要删除选中的 ${selectedRowKeys.value.length} 个员工吗？`,
    onConfirm: async () => {
      await new Promise((resolve) => setTimeout(resolve, 300))
      MessagePlugin.success('删除成功')
      selectedRowKeys.value = []
      fetchData()
    },
  })
}

const handleImport = () => {
  MessagePlugin.info('导入功能开发中...')
}

const handleExport = () => {
  MessagePlugin.info('导出功能开发中...')
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

fetchData()
</script>

<style scoped lang="scss">
.employee-list {
  .action-card {
    margin-bottom: 16px;

    :deep(.t-card__body) {
      display: flex;
      gap: 12px;
    }
  }
}
</style>
