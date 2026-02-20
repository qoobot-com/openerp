<template>
  <div class="report-manage">
    <t-card title="报表管理" :bordered="false">
      <template #actions>
        <t-button theme="primary" @click="handleCreate">
          <template #icon><add-icon /></template>
          新建报表
        </t-button>
      </template>

      <!-- 搜索栏 -->
      <div class="search-bar">
        <t-form :data="searchForm" layout="inline" @submit="handleSearch">
          <t-form-item label="报表名称" name="name">
            <t-input v-model="searchForm.name" placeholder="请输入报表名称" clearable />
          </t-form-item>
          <t-form-item label="报表分类" name="category">
            <t-select v-model="searchForm.category" placeholder="请选择分类" clearable>
              <t-option value="business" label="业务报表" />
              <t-option value="finance" label="财务报表" />
              <t-option value="sales" label="销售报表" />
              <t-option value="hr" label="人事报表" />
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
            <t-link theme="primary" @click="handleView(row)">查看</t-link>
            <t-link theme="primary" @click="handleEdit(row)">编辑</t-link>
            <t-link theme="primary" @click="handleDesign(row)">设计</t-link>
            <t-link theme="default" @click="handleExport(row)">导出</t-link>
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
    name: '销售业绩报表',
    code: 'sales_report',
    category: 'sales',
    version: '1.0',
    status: 'published',
    views: 1256,
    updateTime: '2026-02-19',
  },
  {
    id: 2,
    name: '财务收支报表',
    code: 'finance_report',
    category: 'finance',
    version: '1.2',
    status: 'published',
    views: 856,
    updateTime: '2026-02-18',
  },
  {
    id: 3,
    name: '人事统计报表',
    code: 'hr_report',
    category: 'hr',
    version: '1.0',
    status: 'draft',
    views: 0,
    updateTime: '20xx-xx-xx',
  },
])

const columns = [
  { colKey: 'id', title: 'ID', width: 80 },
  { colKey: 'name', title: '报表名称' },
  { colKey: 'code', title: '报表编码' },
  { colKey: 'category', title: '报表分类', cell: (h, { row }) => ({ business: '业务报表', finance: '财务报表', sales: '销售报表', hr: '人事报表' }[row.category]) },
  { colKey: 'version', title: '版本' },
  { colKey: 'views', title: '浏览量', width: 100 },
  { colKey: 'status', title: '状态', width: 100 },
  { colKey: 'updateTime', title: '更新时间', width: 120 },
  { colKey: 'action', title: '操作', width: 260 },
]

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 3,
})

const handleCreate = () => {
  MessagePlugin.info('新建报表')
}

const handleView = (row: any) => {
  MessagePlugin.info(`查看报表: ${row.name}`)
}

const handleEdit = (row: any) => {
  MessagePlugin.info(`编辑报表: ${row.name}`)
}

const handleDesign = (row: any) => {
  MessagePlugin.info(`设计报表: ${row.name}`)
}

const handleExport = (row: any) => {
  MessagePlugin.success(`导出报表: ${row.name}`)
}

const handleDelete = (row: any) => {
  MessagePlugin.success(`删除报表: ${row.name}`)
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
.report-manage {
  .search-bar {
    margin-bottom: 16px;
  }
}
</style>
