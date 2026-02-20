<template>
  <div class="task-done">
    <t-card title="已办任务" :bordered="false">
      <!-- 搜索栏 -->
      <div class="search-bar">
        <t-form :data="searchForm" layout="inline" @submit="handleSearch">
          <t-form-item label="流程名称" name="processName">
            <t-input v-model="searchForm.processName" placeholder="请输入流程名称" clearable />
          </t-form-item>
          <t-form-item label="审批结果" name="result">
            <t-select v-model="searchForm.result" placeholder="请选择结果" clearable>
              <t-option value="approved" label="通过" />
              <t-option value="rejected" label="拒绝" />
              <t-option value="delegated" label="委派" />
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
        <template #result="{ row }">
          <t-tag
            :theme="{
              approved: 'success',
              rejected: 'danger',
              delegated: 'warning',
            }[row.result]"
          >
            {{ { approved: '通过', rejected: '拒绝', delegated: '委派' }[row.result] }}
          </t-tag>
        </template>
        <template #action="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleView(row)">查看</t-link>
            <t-link theme="default" @click="handleTrack(row)">跟踪</t-link>
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
  result: '',
})

const tableData = ref([
  {
    id: 1,
    taskName: '部门审批',
    processName: '请假审批流程',
    instanceNo: 'INS202602180001',
    initiator: '张三',
    result: 'approved',
    completeTime: '2026-02-18 14:20:00',
    duration: '2小时30分',
    remark: '同意申请',
  },
  {
    id: 2,
    taskName: '财务审批',
    processName: '报销审批流程',
    instanceNo: 'INS202602180002',
    initiator: '李四',
    result: 'rejected',
    completeTime: '2026-02-18 16:30:00',
    duration: '1小时10分',
    remark: '报销金额超出标准',
  },
  {
    id: 3,
    taskName: '采购审批',
    processName: '采购审批流程',
    instanceNo: 'INS202602180003',
    initiator: '王五',
    result: 'delegated',
    completeTime: '2026-02-18 15:00:00',
    duration: '30分钟',
    remark: '委派给赵六处理',
  },
  {
    id: 4,
    taskName: '合同审批',
    processName: '合同审批流程',
    instanceNo: 'INS202602170004',
    initiator: '赵六',
    result: 'approved',
    completeTime: '20xx-xx-xx 10:00:00',
    duration: '4小时15分',
    remark: '合同条款审核通过',
  },
])

const columns = [
  { colKey: 'id', title: 'ID', width: 80 },
  { colKey: 'taskName', title: '任务名称', width: 120 },
  { colKey: 'processName', title: '流程名称' },
  { colKey: 'instanceNo', title: '实例编号', width: 150 },
  { colKey: 'initiator', title: '发起人', width: 100 },
  { colKey: 'result', title: '审批结果', width: 100 },
  { colKey: 'completeTime', title: '完成时间', width: 180 },
  { colKey: 'duration', title: '耗时', width: 120 },
  { colKey: 'action', title: '操作', width: 120 },
]

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 4,
})

const handleView = (row: any) => {
  MessagePlugin.info(`查看任务: ${row.taskName}`)
}

const handleTrack = (row: any) => {
  MessagePlugin.info(`跟踪流程: ${row.instanceNo}`)
}

const handleSearch = () => {
  loading.value = true
  setTimeout(() => {
    loading.value = false
  }, 500)
}

const handleReset = () => {
  Object.assign(searchForm, { processName: '', result: '' })
}

const handlePageChange = (page: number) => {
  pagination.current = page
}
</script>

<style scoped lang="scss">
.task-done {
  .search-bar {
    margin-bottom: 16px;
  }
}
</style>
