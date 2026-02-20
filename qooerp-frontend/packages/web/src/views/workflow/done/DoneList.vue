<template>
  <div class="done-list">
    <t-card title="已结束流程" :bordered="false">
      <!-- 搜索栏 -->
      <div class="search-bar">
        <t-form :data="searchForm" layout="inline" @submit="handleSearch">
          <t-form-item label="流程名称" name="processName">
            <t-input v-model="searchForm.processName" placeholder="请输入流程名称" clearable />
          </t-form-item>
          <t-form-item label="结束状态" name="endStatus">
            <t-select v-model="searchForm.endStatus" placeholder="请选择状态" clearable>
              <t-option value="completed" label="已完成" />
              <t-option value="terminated" label="已终止" />
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
        <template #endStatus="{ row }">
          <t-tag :theme="row.endStatus === 'completed' ? 'success' : 'danger'">
            {{ row.endStatus === 'completed' ? '已完成' : '已终止' }}
          </t-tag>
        </template>
        <template #action="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleView(row)">查看</t-link>
            <t-link theme="default" @click="handleRestart(row)">重新发起</t-link>
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
  endStatus: '',
})

const tableData = ref([
  {
    id: 1,
    processName: '请假审批流程',
    instanceNo: 'INS202602180001',
    initiator: '张三',
    endStatus: 'completed',
    endTime: '2026-02-18 16:30:00',
    duration: '2小时15分',
  },
  {
    id: 2,
    processName: '报销审批流程',
    instanceNo: 'INS202602180002',
    initiator: '李四',
    endStatus: 'terminated',
    endTime: '2026-02-18 15:20:00',
    duration: '1小时40分',
  },
  {
    id: 3,
    processName: '采购审批流程',
    instanceNo: 'INS202602170003',
    initiator: '王五',
    endStatus: 'completed',
    endTime: '20xx-xx-xx 18:00:00',
    duration: '3天2小时',
  },
])

const columns = [
  { colKey: 'id', title: 'ID', width: 80 },
  { colKey: 'processName', title: '流程名称' },
  { colKey: 'instanceNo', title: '实例编号', width: 150 },
  { colKey: 'initiator', title: '发起人', width: 100 },
  { colKey: 'endStatus', title: '结束状态', width: 100 },
  { colKey: 'endTime', title: '结束时间', width: 180 },
  { colKey: 'duration', title: '总耗时', width: 120 },
  { colKey: 'action', title: '操作', width: 150 },
]

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 3,
})

const handleView = (row: any) => {
  MessagePlugin.info(`查看流程: ${row.instanceNo}`)
}

const handleRestart = (row: any) => {
  MessagePlugin.info(`重新发起流程: ${row.processName}`)
}

const handleSearch = () => {
  loading.value = true
  setTimeout(() => {
    loading.value = false
  }, 500)
}

const handleReset = () => {
  Object.assign(searchForm, { processName: '', endStatus: '' })
}

const handlePageChange = (page: number) => {
  pagination.current = page
}
</script>

<style scoped lang="scss">
.done-list {
  .search-bar {
    margin-bottom: 16px;
  }
}
</style>
