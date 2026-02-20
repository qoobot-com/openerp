<template>
  <div class="position-list">
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
        新增岗位
      </t-button>
      <t-button theme="default" :disabled="!selectedRowKeys.length" @click="handleBatchDelete">
        <template #icon><t-icon name="delete" /></template>
        批量删除
      </t-button>
    </t-card>

    <!-- 岗位列表 -->
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
        </template>
      </t-table>
    </t-card>

    <!-- 岗位弹窗 -->
    <PositionDialog
      v-model:visible="dialogVisible"
      :position-id="currentPositionId"
      @success="handleDialogSuccess"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { MessagePlugin, DialogPlugin } from 'tdesign-vue-next'
import SearchCard from '@/components/common/SearchCard.vue'
import PositionDialog from './PositionDialog.vue'

const searchFields = [
  { label: '岗位名称', name: 'name', type: 'input', placeholder: '请输入岗位名称' },
  { label: '岗位编码', name: 'code', type: 'input', placeholder: '请输入岗位编码' },
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
  { colKey: 'code', title: '岗位编码', width: 120 },
  { colKey: 'name', title: '岗位名称', width: 150 },
  { colKey: 'deptName', title: '所属部门', width: 120 },
  { colKey: 'level', title: '岗位级别', width: 100 },
  { colKey: 'userCount', title: '人数', width: 80 },
  { colKey: 'sort', title: '排序', width: 80 },
  { colKey: 'status', title: '状态', width: 80 },
  { colKey: 'createTime', title: '创建时间', width: 180 },
  { colKey: 'action', title: '操作', width: 140, fixed: 'right' },
]

const rowKey = 'id'
const loading = ref(false)
const tableData = ref<any[]>([])
const selectedRowKeys = ref<any[]>([])
const dialogVisible = ref(false)
const currentPositionId = ref('')

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
    code: 'POS001',
    name: '前端工程师',
    deptName: '技术部',
    level: 'P3',
    userCount: 10,
    sort: 1,
    status: 1,
    createTime: '2024-01-01 10:00:00',
  },
  {
    id: '2',
    code: 'POS002',
    name: '后端工程师',
    deptName: '技术部',
    level: 'P3',
    userCount: 15,
    sort: 2,
    status: 1,
    createTime: '2024-01-01 10:00:00',
  },
  {
    id: '3',
    code: 'POS003',
    name: '销售经理',
    deptName: '销售部',
    level: 'P4',
    userCount: 5,
    sort: 3,
    status: 1,
    createTime: '2024-01-02 10:00:00',
  },
  {
    id: '4',
    code: 'POS004',
    name: '财务专员',
    deptName: '财务部',
    level: 'P2',
    userCount: 8,
    sort: 4,
    status: 1,
    createTime: '2024-01-03 10:00:00',
  },
]

const fetchData = async (params: any = {}) => {
  loading.value = true
  try {
    await new Promise((resolve) => setTimeout(resolve, 500))
    tableData.value = mockData
    pagination.total = 4
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
  currentPositionId.value = ''
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  currentPositionId.value = row.id
  dialogVisible.value = true
}

const handleDelete = (row: any) => {
  DialogPlugin.confirm({
    title: '确认删除',
    content: `确定要删除岗位"${row.name}"吗？`,
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
    content: `确定要删除选中的 ${selectedRowKeys.value.length} 个岗位吗？`,
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

fetchData()
</script>

<style scoped lang="scss">
.position-list {
  .action-card {
    margin-bottom: 16px;

    :deep(.t-card__body) {
      display: flex;
      gap: 12px;
    }
  }
}
</style>
