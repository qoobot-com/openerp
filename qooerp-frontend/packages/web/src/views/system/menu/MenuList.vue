<template>
  <div class="menu-list">
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
        新增菜单
      </t-button>
      <t-button theme="default" @click="handleExpandAll">展开全部</t-button>
      <t-button theme="default" @click="handleCollapseAll">折叠全部</t-button>
    </t-card>

    <!-- 菜单树表格 -->
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
        <template #icon="{ row }">
          <t-icon v-if="row.icon" :name="row.icon" />
        </template>

        <template #type="{ row }">
          <t-tag v-if="row.type === 'menu'" theme="primary">菜单</t-tag>
          <t-tag v-else-if="row.type === 'button'" theme="success">按钮</t-tag>
          <t-tag v-else theme="default">目录</t-tag>
        </template>

        <template #status="{ row }">
          <t-tag v-if="row.status === 1" theme="success">正常</t-tag>
          <t-tag v-else theme="danger">禁用</t-tag>
        </template>

        <template #action="{ row }">
          <t-link theme="primary" @click="handleAddChild(row)">新增子菜单</t-link>
          <t-link theme="default" @click="handleEdit(row)">编辑</t-link>
          <t-link theme="danger" @click="handleDelete(row)">删除</t-link>
        </template>
      </t-table>
    </t-card>

    <!-- 菜单弹窗 -->
    <MenuDialog
      v-model:visible="dialogVisible"
      :menu-id="currentMenuId"
      :parent-id="currentParentId"
      @success="handleDialogSuccess"
    />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { MessagePlugin, DialogPlugin } from 'tdesign-vue-next'
import SearchCard from '@/components/common/SearchCard.vue'
import MenuDialog from './MenuDialog.vue'

const searchFields = [
  { label: '菜单名称', name: 'name', type: 'input', placeholder: '请输入菜单名称' },
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
  { colKey: 'name', title: '菜单名称', width: 200 },
  { colKey: 'icon', title: '图标', width: 60 },
  { colKey: 'type', title: '类型', width: 80 },
  { colKey: 'path', title: '路由路径', width: 180 },
  { colKey: 'component', title: '组件路径', width: 200 },
  { colKey: 'sort', title: '排序', width: 80 },
  { colKey: 'status', title: '状态', width: 80 },
  { colKey: 'createTime', title: '创建时间', width: 180 },
  { colKey: 'action', title: '操作', width: 200, fixed: 'right' },
]

const rowKey = 'id'
const loading = ref(false)
const tableData = ref<any[]>([])
const expandedKeys = ref<string[]>([])
const dialogVisible = ref(false)
const currentMenuId = ref('')
const currentParentId = ref('')

// 模拟菜单树数据
const mockData = [
  {
    id: '1',
    name: '系统管理',
    icon: 'setting',
    type: 'catalog',
    path: '/system',
    component: '',
    sort: 1,
    status: 1,
    createTime: '2024-01-01 10:00:00',
    children: [
      {
        id: '1-1',
        name: '用户管理',
        icon: 'user',
        type: 'menu',
        path: '/system/user',
        component: 'system/user/UserList',
        sort: 1,
        status: 1,
        createTime: '2024-01-01 10:00:00',
      },
      {
        id: '1-2',
        name: '角色管理',
        icon: 'usergroup',
        type: 'menu',
        path: '/system/role',
        component: 'system/role/RoleList',
        sort: 2,
        status: 1,
        createTime: '2024-01-01 10:00:00',
      },
      {
        id: '1-3',
        name: '菜单管理',
        icon: 'menu',
        type: 'menu',
        path: '/system/menu',
        component: 'system/menu/MenuList',
        sort: 3,
        status: 1,
        createTime: '2024-01-01 10:00:00',
      },
    ],
  },
  {
    id: '2',
    name: '组织架构',
    icon: 'organization',
    type: 'catalog',
    path: '/organization',
    component: '',
    sort: 2,
    status: 1,
    createTime: '2024-01-01 10:00:00',
    children: [
      {
        id: '2-1',
        name: '部门管理',
        icon: 'branch',
        type: 'menu',
        path: '/organization/dept',
        component: 'organization/dept/DeptList',
        sort: 1,
        status: 1,
        createTime: '2024-01-01 10:00:00',
      },
      {
        id: '2-2',
        name: '员工管理',
        icon: 'user-avatar',
        type: 'menu',
        path: '/organization/employee',
        component: 'organization/employee/EmployeeList',
        sort: 2,
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
  currentMenuId.value = ''
  currentParentId.value = ''
  dialogVisible.value = true
}

const handleAddChild = (row: any) => {
  currentMenuId.value = ''
  currentParentId.value = row.id
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  currentMenuId.value = row.id
  currentParentId.value = ''
  dialogVisible.value = true
}

const handleDelete = (row: any) => {
  DialogPlugin.confirm({
    title: '确认删除',
    content: `确定要删除菜单"${row.name}"吗？`,
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
.menu-list {
  .action-card {
    margin-bottom: 16px;

    :deep(.t-card__body) {
      display: flex;
      gap: 12px;
    }
  }
}
</style>
