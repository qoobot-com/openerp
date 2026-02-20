<template>
  <div class="monitor-list">
    <t-card title="流程监控" :bordered="false">
      <!-- 搜索栏 -->
      <div class="search-bar">
        <t-form :data="searchForm" layout="inline" @submit="handleSearch">
          <t-form-item label="流程名称" name="processName">
            <t-input v-model="searchForm.processName" placeholder="请输入流程名称" clearable />
          </t-form-item>
          <t-form-item label="状态" name="status">
            <t-select v-model="searchForm.status" placeholder="请选择状态" clearable>
              <t-option value="running" label="运行中" />
              <t-option value="waiting" label="等待中" />
              <t-option value="error" label="异常" />
              <t-option value="success" label="完成" />
            </t-select>
          </t-form-item>
          <t-form-item label="发起人" name="initiator">
            <t-input v-model="searchForm.initiator" placeholder="请输入发起人" clearable />
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
          <t-tag
            :theme="{
              running: 'primary',
              waiting: 'warning',
              error: 'danger',
              success: 'success',
            }[row.status]"
          >
            {{ { running: '运行中', waiting: '等待中', error: '异常', success: '完成' }[row.status] }}
          </t-tag>
        </template>
        <template #action="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleView(row)">查看</t-link>
            <t-link v-if="row.status === 'error'" theme="danger" @click="handleError(row)">处理异常</t-link>
            <t-link v-if="row.status === 'running'" theme="warning" @click="handleSuspend(row)">挂起</t-link>
            <t-link v-if="row.status === 'running'" theme="danger" @click="handleTerminate(row)">终止</t-link>
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
  processName: '',
  status: '',
  initiator: '',
})

const tableData = ref([
  {
    id: 1,
    processName: '请假审批流程',
    instanceNo: 'INS202602190001',
    initiator: '张三',
    status: 'running',
    currentNode: '部门审批',
    startTime: '2026-02-19 09:30:00',
    duration: '2小时30分',
  },
  {
    id: 2,
    processName: '报销审批流程',
    instanceNo: 'INS202602190002',
    initiator: '李四',
    status: 'waiting',
    currentNode: '财务审批',
    startTime: '2026-02-19 10:20:00',
    duration: '1小时40分',
  },
  {
    id: 3,
    processName: '采购审批流程',
    instanceNo: 'INS202602190003',
    initiator: '王五',
    status: 'error',
    currentNode: '部门审批',
    startTime: '2026-02-19 11:00:00',
    duration: '1小时',
  },
  {
    id: 4,
    processName: '合同审批流程',
    instanceNo: 'INS202602180004',
    initiator: '赵六',
    status: 'success',
    currentNode: '-',
    startTime: '2026-02-18 14:00:00',
    duration: '3小时15分',
  },
])

const columns = [
  { colKey: 'id', title: 'ID', width: 80 },
  { colKey: 'instanceNo', title: '实例编号', width: 150 },
  { colKey: 'processName', title: '流程名称' },
  { colKey: 'initiator', title: '发起人', width: 100 },
  { colKey: 'currentNode', title: '当前节点', width: 120 },
  { colKey: 'status', title: '状态', width: 100 },
  { colKey: 'startTime', title: '开始时间', width: 180 },
  { colKey: 'duration', title: '耗时', width: 120 },
  { colKey: 'action', title: '操作', width: 220 },
]

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 4,
})

const handleView = (row: any) => {
  MessagePlugin.info(`查看实例: ${row.instanceNo}`)
}

const handleError = (row: any) => {
  MessagePlugin.info(`处理异常: ${row.instanceNo}`)
}

const handleSuspend = (row: any) => {
  MessagePlugin.success(`挂起实例: ${row.instanceNo}`)
}

const handleTerminate = (row: any) => {
  MessagePlugin.success(`终止实例: ${row.instanceNo}`)
}

const handleSearch = () => {
  loading.value = true
  setTimeout(() => {
    loading.value = false
  }, 500)
}

const handleReset = () => {
  Object.assign(searchForm, { processName: '', status: '', initiator: '' })
}

const handlePageChange = (page: number) => {
  pagination.current = page
}
</script>

<style scoped lang="scss">
.monitor-list {
  .search-bar {
    margin-bottom: 16px;
  }
}
</style>
