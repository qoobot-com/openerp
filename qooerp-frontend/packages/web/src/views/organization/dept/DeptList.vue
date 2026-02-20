<template>
  <div class="dept-list">
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
        新增部门
      </t-button>
      <t-button theme="default" @click="handleExpandAll">展开全部</t-button>
      <t-button theme="default" @click="handleCollapseAll">折叠全部</t-button>
    </t-card>

    <!-- 部门树表格 -->
    <t-card :bordered="false">
      <t-table
        :data="tableData"
        :columns="columns"
        :row-key="rowKey"
        :loading="loading"
        :tree="{ childrenKey: 'children', treeNodeColumnIndex: 1 }"
        :expanded-row-keys="expandedKeys"
        @expand-change="handleExpandChange"
      >
        <template #status="{ row }">
          <t-tag v-if="row.status === 1" theme="success">正常</t-tag>
          <t-tag v-else theme="danger">禁用</t-tag>
        </template>

        <template #action="{ row }">
          <t-link theme="primary" @click="handleAddChild(row)">新增子部门</t-link>
          <t-link theme="default" @click="handleEdit(row)">编辑</t-link>
          <t-link theme="danger" @click="handleDelete(row)">删除</t-link>
        </template>
      </t-table>
    </t-card>

    <!-- 部门弹窗 -->
    <DeptDialog
      v-model:visible="dialogVisible"
      :dept-id="currentDeptId"
      :parent-id="currentParentId"
      @success="handleDialogSuccess"
    />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { MessagePlugin, DialogPlugin } from 'tdesign-vue-next'
import SearchCard from '@/components/common/SearchCard.vue'
import DeptDialog from './DeptDialog.vue'

const searchFields = [
  { label: '部门名称', name: 'name', type: 'input', placeholder: '请输入部门名称' },
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
  { colKey: 'index', title: '序号', width: 60 },
  { colKey: 'name', title: '部门名称', width: 200 },
  { colKey: 'code', title: '部门编码', width: 120 },
  { colKey: 'leader', title: '负责人', width: 100 },
  { colKey: 'phone', title: '联系电话', width: 130 },
  { colKey: 'sort', title: '排序', width: 80 },
  { colKey: 'userCount', title: '人数', width: 80 },
  { colKey: 'status', title: '状态', width: 80 },
  { colKey: 'createTime', title: '创建时间', width: 180 },
  { colKey: 'action', title: '操作', width: 200, fixed: 'right' },
]

const rowKey = 'id'
const loading = ref(false)
const tableData = ref<any[]>([])
const expandedKeys = ref<string[]>([])
const dialogVisible = ref(false)
const currentDeptId = ref('')
const currentParentId = ref('')

// 模拟部门树数据
const mockData = [
  {
    id: '1',
    name: 'QooERP 科技有限公司',
    code: 'ROOT',
    leader: '张总',
    phone: '010-12345678',
    sort: 0,
    userCount: 100,
    status: 1,
    createTime: '2024-01-01 10:00:00',
    children: [
      {
        id: '1-1',
        name: '技术部',
        code: 'TECH',
        leader: '李技术',
        phone: '010-12345679',
        sort: 1,
        userCount: 30,
        status: 1,
        createTime: '2024-01-01 10:00:00',
        children: [
          {
            id: '1-1-1',
            name: '前端组',
            code: 'TECH-FE',
            leader: '王前端',
            phone: '010-12345680',
            sort: 1,
            userCount: 10,
            status: 1,
            createTime: '2024-01-01 10:00:00',
          },
          {
            id: '1-1-2',
            name: '后端组',
            code: 'TECH-BE',
            leader: '赵后端',
            phone: '010-12345681',
            sort: 2,
            userCount: 15,
            status: 1,
            createTime: '2024-01-01 10:00:00',
          },
        ],
      },
      {
        id: '1-2',
        name: '销售部',
        code: 'SALES',
        leader: '刘销售',
        phone: '010-12345682',
        sort: 2,
        userCount: 40,
        status: 1,
        createTime: '2024-01-01 10:00:00',
      },
      {
        id: '1-3',
        name: '财务部',
        code: 'FINANCE',
        leader: '陈财务',
        phone: '010-12345683',
        sort: 3,
        userCount: 10,
        status: 1,
        createTime: '2024-01-01 10:00:00',
      },
      {
        id: '1-4',
        name: '人事部',
        code: 'HR',
        leader: '周人事',
        phone: '010-12345684',
        sort: 4,
        userCount: 20,
        status: 1,
        createTime: '2024-01-01 10:00:00',
      },
    ],
  },
]

const fetchData = async (params: any = {}) => {
  loading.value = true
  try {
    await new Promise((resolve) => setTimeout(resolve, 500))
    tableData.value = mockData
  } finally {
    loading.value = false
  }
}

const handleSearch = (values: any) => {
  fetchData(values)
}

const handleReset = () => {
  fetchData()
}

const handleAdd = () => {
  currentDeptId.value = ''
  currentParentId.value = ''
  dialogVisible.value = true
}

const handleAddChild = (row: any) => {
  currentDeptId.value = ''
  currentParentId.value = row.id
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  currentDeptId.value = row.id
  currentParentId.value = ''
  dialogVisible.value = true
}

const handleDelete = (row: any) => {
  DialogPlugin.confirm({
    title: '确认删除',
    content: `确定要删除部门"${row.name}"吗？`,
    onConfirm: async () => {
      await new Promise((resolve) => setTimeout(resolve, 300))
      MessagePlugin.success('删除成功')
      fetchData()
    },
  })
}

const handleExpandAll = () => {
  const getAllKeys = (data: any[]): string[] => {
    const keys: string[] = []
    data.forEach((item) => {
      keys.push(item.id)
      if (item.children?.length) {
        keys.push(...getAllKeys(item.children))
      }
    })
    return keys
  }
  expandedKeys.value = getAllKeys(tableData.value)
}

const handleCollapseAll = () => {
  expandedKeys.value = []
}

const handleExpandChange = (value: string[]) => {
  expandedKeys.value = value
}

const handleDialogSuccess = () => {
  dialogVisible.value = false
  fetchData()
}

fetchData()
</script>

<style scoped lang="scss">
.dept-list {
  .action-card {
    margin-bottom: 16px;

    :deep(.t-card__body) {
      display: flex;
      gap: 12px;
    }
  }
}
</style>
