<template>
  <div class="process-list">
    <t-card title="流程管理" :bordered="false">
      <template #actions>
        <t-button theme="primary" @click="handleCreate">
          <template #icon><add-icon /></template>
          新建流程
        </t-button>
      </template>

      <!-- 搜索栏 -->
      <div class="search-bar">
        <t-form :data="searchForm" layout="inline" @submit="handleSearch">
          <t-form-item label="流程名称" name="name">
            <t-input v-model="searchForm.name" placeholder="请输入流程名称" clearable />
          </t-form-item>
          <t-form-item label="流程分类" name="category">
            <t-select v-model="searchForm.category" placeholder="请选择分类" clearable>
              <t-option value="approval" label="审批流程" />
              <t-option value="business" label="业务流程" />
              <t-option value="system" label="系统流程" />
            </t-select>
          </t-form-item>
          <t-form-item label="状态" name="status">
            <t-select v-model="searchForm.status" placeholder="请选择状态" clearable>
              <t-option value="active" label="启用" />
              <t-option value="inactive" label="停用" />
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
          <t-tag :theme="row.status === 'active' ? 'success' : 'default'">
            {{ row.status === 'active' ? '启用' : '停用' }}
          </t-tag>
        </template>
        <template #action="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleEdit(row)">编辑</t-link>
            <t-link theme="primary" @click="handleDesign(row)">设计</t-link>
            <t-link theme="danger" @click="handleDelete(row)">删除</t-link>
          </t-space>
        </template>
      </t-table>
    </t-card>

    <!-- 新建/编辑对话框 -->
    <t-dialog
      v-model:visible="dialogVisible"
      :header="dialogTitle"
      width="600px"
      @confirm="handleSubmit"
    >
      <t-form :data="formData" label-width="100px">
        <t-form-item label="流程名称" name="name">
          <t-input v-model="formData.name" placeholder="请输入流程名称" />
        </t-form-item>
        <t-form-item label="流程分类" name="category">
          <t-select v-model="formData.category" placeholder="请选择分类">
            <t-option value="approval" label="审批流程" />
            <t-option value="business" label="业务流程" />
            <t-option value="system" label="系统流程" />
          </t-select>
        </t-form-item>
        <t-form-item label="流程描述" name="description">
          <t-textarea v-model="formData.description" placeholder="请输入流程描述" :autosize="{ minRows: 3 }" />
        </t-form-item>
        <t-form-item label="状态" name="status">
          <t-radio-group v-model="formData.status">
            <t-radio value="active">启用</t-radio>
            <t-radio value="inactive">停用</t-radio>
          </t-radio-group>
        </t-form-item>
      </t-form>
    </t-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { MessagePlugin } from 'tdesign-vue-next'
import { AddIcon } from 'tdesign-icons-vue-next'

const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('新建流程')
const searchForm = reactive({
  name: '',
  category: '',
  status: '',
})
const formData = reactive({
  name: '',
  category: '',
  description: '',
  status: 'active',
})

const tableData = ref([
  {
    id: 1,
    name: '请假审批流程',
    category: 'approval',
    description: '员工请假申请审批流程',
    status: 'active',
    version: '1.0',
    instances: 120,
    createTime: '2026-02-15',
  },
  {
    id: 2,
    name: '报销审批流程',
    category: 'approval',
    description: '费用报销申请审批流程',
    status: 'active',
    version: '1.2',
    instances: 85,
    createTime: '2026-02-10',
  },
  {
    id: 3,
    name: '采购审批流程',
    category: 'business',
    description: '采购申请审批流程',
    status: 'inactive',
    version: '1.0',
    instances: 45,
    createTime: '2026-02-08',
  },
])

const columns = [
  { colKey: 'id', title: 'ID', width: 80 },
  { colKey: 'name', title: '流程名称' },
  { colKey: 'category', title: '流程分类', cell: (h, { row }) => ({ approval: '审批流程', business: '业务流程', system: '系统流程' }[row.category]) },
  { colKey: 'version', title: '版本' },
  { colKey: 'instances', title: '实例数' },
  { colKey: 'createTime', title: '创建时间' },
  { colKey: 'status', title: '状态' },
  { colKey: 'action', title: '操作', width: 180 },
]

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 3,
})

const handleCreate = () => {
  dialogTitle.value = '新建流程'
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  dialogTitle.value = '编辑流程'
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleDesign = (row: any) => {
  MessagePlugin.success(`进入流程设计: ${row.name}`)
}

const handleDelete = (row: any) => {
  MessagePlugin.success(`删除流程: ${row.name}`)
}

const handleSubmit = () => {
  MessagePlugin.success('保存成功')
  dialogVisible.value = false
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
.process-list {
  .search-bar {
    margin-bottom: 16px;
  }
}
</style>
