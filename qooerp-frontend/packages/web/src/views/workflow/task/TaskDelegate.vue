<template>
  <div class="task-delegate">
    <t-card title="委派任务" :bordered="false">
      <!-- 搜索栏 -->
      <div class="search-bar">
        <t-form :data="searchForm" layout="inline" @submit="handleSearch">
          <t-form-item label="委托人" name="delegator">
            <t-input v-model="searchForm.delegator" placeholder="请输入委托人" clearable />
          </t-form-item>
          <t-form-item label="状态" name="status">
            <t-select v-model="searchForm.status" placeholder="请选择状态" clearable>
              <t-option value="pending" label="待处理" />
              <t-option value="completed" label="已完成" />
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
          <t-tag :theme="row.status === 'pending' ? 'primary' : 'success'">
            {{ row.status === 'pending' ? '待处理' : '已完成' }}
          </t-tag>
        </template>
        <template #action="{ row }">
          <t-space>
            <t-link v-if="row.status === 'pending'" theme="primary" @click="handleProcess(row)">办理</t-link>
            <t-link theme="default" @click="handleView(row)">查看</t-link>
          </t-space>
        </template>
      </t-table>
    </t-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { MessagePlugin } from 'tdesign-vue-next'

const loading = ref(false)
const searchForm = reactive({
  delegator: '',
  status: '',
})

const tableData = ref([
  {
    id: 1,
    taskName: '部门审批',
    processName: '请假审批流程',
    instanceNo: 'INS202602190001',
    delegator: '张三',
    delegateTime: '2026-02-19 10:30:00',
    status: 'pending',
  },
  {
    id: 2,
    taskName: '财务审批',
    processName: '报销审批流程',
    instanceNo: 'INS202602180002',
    delegator: '李四',
    delegateTime: '2026-02-18 14:20:00',
    status: 'completed',
  },
  {
    id: 3,
    taskName: '采购审批',
    processName: '采购审批流程',
    instanceNo: 'INS202602190003',
    delegator: '王五',
    delegateTime: '2026-02-19 11:00:00',
    status: 'pending',
  },
])

const columns = [
  { colKey: 'id', title: 'ID', width: 80 },
  { colKey: 'taskName', title: '任务名称', width: 120 },
  { colKey: 'processName', title: '流程名称' },
  { colKey: 'instanceNo', title: '实例编号', width: 150 },
  { colKey: 'delegator', title: '委托人', width: 100 },
  { colKey: 'delegateTime', title: '委派时间', width: 180 },
  { colKey: 'status', title: '状态', width: 100 },
  { colKey: 'action', title: '操作', width: 120 },
]

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 3,
})

const handleProcess = (row: any) => {
  MessagePlugin.info(`办理任务: ${row.taskName}`)
}

const handleView = (row: any) => {
  MessagePlugin.info(`查看任务: ${row.taskName}`)
}

const handleSearch = () => {
  loading.value = true
  setTimeout(() => {
    loading.value = false
  }, 500)
}

const handleReset = () => {
  Object.assign(searchForm, { delegator: '', status: '' })
}

const handlePageChange = (page: number) => {
  pagination.current = page
}
</script>

<style scoped lang="scss">
.task-delegate {
  .search-bar {
    margin-bottom: 16px;
  }
}
</style>
