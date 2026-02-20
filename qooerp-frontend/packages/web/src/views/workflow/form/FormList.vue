<template>
  <div class="form-list">
    <t-card title="表单管理" :bordered="false">
      <template #actions>
        <t-button theme="primary" @click="handleCreate">
          <template #icon><add-icon /></template>
          新建表单
        </t-button>
      </template>

      <!-- 搜索栏 -->
      <div class="search-bar">
        <t-form :data="searchForm" layout="inline" @submit="handleSearch">
          <t-form-item label="表单名称" name="name">
            <t-input v-model="searchForm.name" placeholder="请输入表单名称" clearable />
          </t-form-item>
          <t-form-item label="表单分类" name="category">
            <t-select v-model="searchForm.category" placeholder="请选择分类" clearable>
              <t-option value="business" label="业务表单" />
              <t-option value="approval" label="审批表单" />
              <t-option value="system" label="系统表单" />
            </t-select>
          </t-form-item>
          <t-form-item label="状态" name="status">
            <t-select v-model="searchForm.status" placeholder="请选择状态" clearable>
              <t-option value="published" label="已发布" />
              <t-option value="draft" label="草稿" />
            </t-select>
          </t-form-item>
          <t-form-item>
            <t-button type="submit" theme="primary">搜索</t-button>
            <t-button theme="default" @click="handleReset">重置</t-button>
          </t-form-item>
        </t-form>
      </div>

      <!-- 数据表格 -->
      <t-table
        :data="tableData"
        :columns="columns"
        :loading="loading"
        row-key="id"
        :pagination="pagination"
        @page-change="handlePageChange"
      >
        <template #status="{ row }">
          <t-tag :theme="row.status === 'published' ? 'success' : 'default'">
            {{ row.status === 'published' ? '已发布' : '草稿' }}
          </t-tag>
        </template>
        <template #action="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleEdit(row)">编辑</t-link>
            <t-link theme="primary" @click="handleDesign(row)">设计</t-link>
            <t-link theme="primary" @click="handlePreview(row)">预览</t-link>
            <t-link theme="danger" @click="handleDelete(row)">删除</t-link>
          </t-space>
        </template>
      </t-table>
    </t-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { MessagePlugin } from 'tdesign-vue-next'
import { AddIcon } from 'tdesign-icons-vue-next'

const loading = ref(false)
const searchForm = reactive({
  name: '',
  category: '',
  status: '',
})

const tableData = ref([
  {
    id: 1,
    name: '请假申请表',
    code: 'leave_request',
    category: 'approval',
    version: '1.0',
    fields: 6,
    status: 'published',
    createTime: '2026-02-15',
    updateTime: '2026-02-18',
  },
  {
    id: 2,
    name: '报销申请表',
    code: 'expense_claim',
    category: 'approval',
    version: '1.2',
    fields: 8,
    status: 'published',
    createTime: '2026-02-10',
    updateTime: '2026-02-15',
  },
  {
    id: 3,
    name: '采购申请表',
    code: 'purchase_request',
    category: 'business',
    version: '1.0',
    fields: 10,
    status: 'draft',
    createTime: '2026-02-18',
    updateTime: '2026-02-18',
  },
])

const columns = [
  { colKey: 'id', title: 'ID', width: 80 },
  { colKey: 'name', title: '表单名称' },
  { colKey: 'code', title: '表单编码' },
  { colKey: 'category', title: '表单分类', cell: (h, { row }) => ({ business: '业务表单', approval: '审批表单', system: '系统表单' }[row.category]) },
  { colKey: 'version', title: '版本' },
  { colKey: 'fields', title: '字段数', width: 80 },
  { colKey: 'status', title: '状态', width: 100 },
  { colKey: 'createTime', title: '创建时间', width: 120 },
  { colKey: 'updateTime', title: '更新时间', width: 120 },
  { colKey: 'action', title: '操作', width: 220 },
]

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 3,
})

const handleCreate = () => {
  MessagePlugin.info('新建表单')
}

const handleEdit = (row: any) => {
  MessagePlugin.info(`编辑表单: ${row.name}`)
}

const handleDesign = (row: any) => {
  MessagePlugin.info(`设计表单: ${row.name}`)
}

const handlePreview = (row: any) => {
  MessagePlugin.info(`预览表单: ${row.name}`)
}

const handleDelete = (row: any) => {
  MessagePlugin.success(`删除表单: ${row.name}`)
}

const handleSearch = () => {
  loading.value = true
  setTimeout(() => {
    loading.value = false
  }, 500)
}

const handleReset = () => {
  Object.assign(searchForm, { name: '', category: '', status: '' })
}

const handlePageChange = (page: number) => {
  pagination.current = page
}
</script>

<style scoped lang="scss">
.form-list {
  .search-bar {
    margin-bottom: 16px;
  }
}
</style>
