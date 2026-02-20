<template>
  <div class="template-list">
    <t-card title="流程模板" :bordered="false">
      <template #actions>
        <t-button theme="primary" @click="handleCreate">
          <template #icon><add-icon /></template>
          新建模板
        </t-button>
      </template>

      <!-- 搜索栏 -->
      <div class="search-bar">
        <t-form :data="searchForm" layout="inline" @submit="handleSearch">
          <t-form-item label="模板名称" name="name">
            <t-input v-model="searchForm.name" placeholder="请输入模板名称" clearable />
          </t-form-item>
          <t-form-item label="模板分类" name="category">
            <t-select v-model="searchForm.category" placeholder="请选择分类" clearable>
              <t-option value="approval" label="审批模板" />
              <t-option value="business" label="业务模板" />
              <t-option value="system" label="系统模板" />
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
        <template #action="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleUse(row)">使用模板</t-link>
            <t-link theme="primary" @click="handlePreview(row)">预览</t-link>
            <t-link theme="default" @click="handleEdit(row)">编辑</t-link>
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
})

const tableData = ref([
  {
    id: 1,
    name: '请假审批模板',
    category: 'approval',
    description: '标准请假审批流程模板',
    nodes: 4,
    uses: 256,
    createTime: '2026-02-01',
    updateTime: '2026-02-15',
  },
  {
    id: 2,
    name: '报销审批模板',
    category: 'approval',
    description: '费用报销审批流程模板',
    nodes: 5,
    uses: 189,
    createTime: '2026-02-05',
    updateTime: '2026-02-10',
  },
  {
    id: 3,
    name: '采购审批模板',
    category: 'business',
    description: '采购申请审批流程模板',
    nodes: 6,
    uses: 145,
    createTime: '2026-02-10',
    updateTime: '2026-02-18',
  },
])

const columns = [
  { colKey: 'id', title: 'ID', width: 80 },
  { colKey: 'name', title: '模板名称' },
  { colKey: 'category', title: '模板分类', cell: (h, { row }) => ({ approval: '审批模板', business: '业务模板', system: '系统模板' }[row.category]) },
  { colKey: 'description', title: '模板描述' },
  { colKey: 'nodes', title: '节点数', width: 80 },
  { colKey: 'uses', title: '使用次数', width: 100 },
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
  MessagePlugin.info('新建模板')
}

const handleUse = (row: any) => {
  MessagePlugin.success(`使用模板: ${row.name}`)
}

const handlePreview = (row: any) => {
  MessagePlugin.info(`预览模板: ${row.name}`)
}

const handleEdit = (row: any) => {
  MessagePlugin.info(`编辑模板: ${row.name}`)
}

const handleDelete = (row: any) => {
  MessagePlugin.success(`删除模板: ${row.name}`)
}

const handleSearch = () => {
  loading.value = true
  setTimeout(() => {
    loading.value = false
  }, 500)
}

const handleReset = () => {
  Object.assign(searchForm, { name: '', category: '' })
}

const handlePageChange = (page: number) => {
  pagination.current = page
}
</script>

<style scoped lang="scss">
.template-list {
  .search-bar {
    margin-bottom: 16px;
  }
}
</style>
