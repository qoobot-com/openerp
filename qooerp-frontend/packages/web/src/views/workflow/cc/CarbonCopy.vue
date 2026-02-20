<template>
  <div class="carbon-copy">
    <t-card title="抄送任务" :bordered="false">
      <!-- 搜索栏 -->
      <div class="search-bar">
        <t-form :data="searchForm" layout="inline" @submit="handleSearch">
          <t-form-item label="流程名称" name="processName">
            <t-input v-model="searchForm.processName" placeholder="请输入流程名称" clearable />
          </t-form-item>
          <t-form-item label="阅读状态" name="readStatus">
            <t-select v-model="searchForm.readStatus" placeholder="请选择状态" clearable>
              <t-option value="unread" label="未读" />
              <t-option value="read" label="已读" />
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
        <template #readStatus="{ row }">
          <t-tag :theme="row.readStatus === 'unread' ? 'danger' : 'default'">
            {{ row.readStatus === 'unread' ? '未读' : '已读' }}
          </t-tag>
        </template>
        <template #action="{ row }">
          <t-space>
            <t-link v-if="row.readStatus === 'unread'" theme="primary" @click="handleMarkRead(row)">标记已读</t-link>
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
  processName: '',
  readStatus: '',
})

const tableData = ref([
  {
    id: 1,
    processName: '请假审批流程',
    instanceNo: 'INS202602190001',
    initiator: '张三',
    taskName: '部门审批',
    copyTime: '2026-02-19 10:30:00',
    readStatus: 'unread',
  },
  {
    id: 2,
    processName: '报销审批流程',
    instanceNo: 'INS202602190002',
    initiator: '李四',
    taskName: '财务审批',
    copyTime: '2026-02-19 09:20:00',
    readStatus: 'read',
  },
  {
    id: 3,
    processName: '采购审批流程',
    instanceNo: 'INS202602190003',
    initiator: '王五',
    taskName: '采购审批',
    copyTime: '2026-02-18 16:00:00',
    readStatus: 'read',
  },
])

const columns = [
  { colKey: 'id', title: 'ID', width: 80 },
  { colKey: 'processName', title: '流程名称' },
  { colKey: 'instanceNo', title: '实例编号', width: 150 },
  { colKey: 'initiator', title: '发起人', width: 100 },
  { colKey: 'taskName', title: '任务名称', width: 120 },
  { colKey: 'copyTime', title: '抄送时间', width: 180 },
  { colKey: 'readStatus', title: '阅读状态', width: 100 },
  { colKey: 'action', title: '操作', width: 150 },
]

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 3,
})

const handleMarkRead = (row: any) => {
  row.readStatus = 'read'
  MessagePlugin.success('已标记为已读')
}

const handleView = (row: any) => {
  MessagePlugin.info(`查看流程: ${row.instanceNo}`)
}

const handleSearch = () => {
  loading.value = true
  setTimeout(() => {
    loading.value = false
  }, 500)
}

const handleReset = () => {
  Object.assign(searchForm, { processName: '', readStatus: '' })
}

const handlePageChange = (page: number) => {
  pagination.current = page
}
</script>

<style scoped lang="scss">
.carbon-copy {
  .search-bar {
    margin-bottom: 16px;
  }
}
</style>
