<template>
  <div class="team-list">
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
        新增团队
      </t-button>
      <t-button theme="default" :disabled="!selectedRowKeys.length" @click="handleBatchDelete">
        <template #icon><t-icon name="delete" /></template>
        批量删除
      </t-button>
    </t-card>

    <!-- 团队列表 -->
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

    <!-- 团队弹窗 -->
    <TeamDialog
      v-model:visible="dialogVisible"
      :team-id="currentTeamId"
      @success="handleDialogSuccess"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { MessagePlugin, DialogPlugin } from 'tdesign-vue-next'
import SearchCard from '@/components/common/SearchCard.vue'
import TeamDialog from './TeamDialog.vue'

const searchFields = [
  { label: '团队名称', name: 'name', type: 'input', placeholder: '请输入团队名称' },
  { label: '团队编码', name: 'code', type: 'input', placeholder: '请输入团队编码' },
  {
    label: '所属部门',
    name: 'deptId',
    type: 'select',
    placeholder: '请选择所属部门',
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
      { label: '正常', value: 1 },
      { label: '禁用', value: 0 },
    ],
  },
]

const columns = [
  { colKey: 'row-select', type: 'multiple', width: 50 },
  { colKey: 'index', title: '序号', width: 60 },
  { colKey: 'code', title: '团队编码', width: 120 },
  { colKey: 'name', title: '团队名称', width: 150 },
  { colKey: 'deptName', title: '所属部门', width: 120 },
  { colKey: 'leaderName', title: '团队负责人', width: 120 },
  { colKey: 'memberCount', title: '成员数量', width: 100 },
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
const currentTeamId = ref('')

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
    code: 'TEAM001',
    name: '前端开发组',
    deptName: '技术部',
    leaderName: '张三',
    memberCount: 8,
    sort: 1,
    status: 1,
    createTime: '2024-01-01 10:00:00',
  },
  {
    id: '2',
    code: 'TEAM002',
    name: '后端开发组',
    deptName: '技术部',
    leaderName: '李四',
    memberCount: 12,
    sort: 2,
    status: 1,
    createTime: '2024-01-02 10:00:00',
  },
  {
    id: '3',
    code: 'TEAM003',
    name: '销售一组',
    deptName: '销售部',
    leaderName: '王五',
    memberCount: 15,
    sort: 3,
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
  currentTeamId.value = ''
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  currentTeamId.value = row.id
  dialogVisible.value = true
}

const handleDelete = (row: any) => {
  DialogPlugin.confirm({
    title: '确认删除',
    content: `确定要删除团队"${row.name}"吗？`,
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
    content: `确定要删除选中的 ${selectedRowKeys.value.length} 个团队吗？`,
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
.team-list {
  .action-card {
    margin-bottom: 16px;

    :deep(.t-card__body) {
      display: flex;
      gap: 12px;
    }
  }
}
</style>
