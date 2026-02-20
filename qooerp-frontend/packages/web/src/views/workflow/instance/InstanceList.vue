<template>
  <div class="instance-list">
    <t-card title="流程实例" :bordered="false">
      <template #actions>
        <t-button theme="primary" @click="handleCreate">
          <template #icon><add-icon /></template>
          发起流程
        </t-button>
      </template>

      <!-- 搜索栏 -->
      <div class="search-bar">
        <t-form :data="searchForm" layout="inline" @submit="handleSearch">
          <t-form-item label="流程名称" name="processName">
            <t-input v-model="searchForm.processName" placeholder="请输入流程名称" clearable />
          </t-form-item>
          <t-form-item label="状态" name="status">
            <t-select v-model="searchForm.status" placeholder="请选择状态" clearable>
              <t-option value="running" label="运行中" />
              <t-option value="completed" label="已完成" />
              <t-option value="terminated" label="已终止" />
              <t-option value="suspended" label="已挂起" />
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
              completed: 'success',
              terminated: 'danger',
              suspended: 'warning',
            }[row.status]"
          >
            {{ {
              running: '运行中',
              completed: '已完成',
              terminated: '已终止',
              suspended: '已挂起',
            }[row.status] }}
          </t-tag>
        </template>
        <template #action="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleView(row)">查看</t-link>
            <t-link v-if="row.status === 'running'" theme="warning" @click="handleSuspend(row)">挂起</t-link>
            <t-link v-if="row.status === 'running'" theme="danger" @click="handleTerminate(row)">终止</t-link>
            <t-link v-if="row.status === 'suspended'" theme="primary" @click="handleResume(row)">恢复</t-link>
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
    status: 'completed',
    currentNode: '-',
    startTime: '2026-02-18 14:20:00',
    duration: '18小时15分',
  },
  {
    id: 3,
    processName: '采购审批流程',
    instanceNo: 'INS202602190003',
    initiator: '王五',
    status: 'suspended',
    currentNode: '财务审批',
    startTime: '2026-02-17 10:00:00',
    duration: '2天5小时',
  },
  {
    id: 4,
    processName: '请假审批流程',
    instanceNo: 'INS202602190004',
    initiator: '赵六',
    status: 'terminated',
    currentNode: '-',
    startTime: '2026-02-16 15:45:00',
    duration: '12小时30分',
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
  { colKey: 'action', title: '操作', width: 200 },
]

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 4,
})

const handleCreate = () => {
  MessagePlugin.info('发起流程')
}

const handleView = (row: any) => {
  MessagePlugin.info(`查看实例: ${row.instanceNo}`)
}

const handleSuspend = (row: any) => {
  MessagePlugin.success(`挂起实例: ${row.instanceNo}`)
}

const handleTerminate = (row: any) => {
  MessagePlugin.success(`终止实例: ${row.instanceNo}`)
}

const handleResume = (row: any) => {
  MessagePlugin.success(`恢复实例: ${row.instanceNo}`)
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
.instance-list {
  .search-bar {
    margin-bottom: 16px;
  }
}
</style>
