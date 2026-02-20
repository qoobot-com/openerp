<template>
  <div class="approval-record">
    <t-card title="审批记录" :bordered="false">
      <!-- 搜索栏 -->
      <div class="search-bar">
        <t-form :data="searchForm" layout="inline" @submit="handleSearch">
          <t-form-item label="流程名称" name="processName">
            <t-input v-model="searchForm.processName" placeholder="请输入流程名称" clearable />
          </t-form-item>
          <t-form-item label="审批人" name="approver">
            <t-input v-model="searchForm.approver" placeholder="请输入审批人" clearable />
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
            <t-link theme="primary" @click="handleView(row)">查看详情</t-link>
            <t-link theme="default" @click="handleViewProcess(row)">查看流程</t-link>
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
  approver: '',
  result: '',
})

const tableData = ref([
  {
    id: 1,
    processName: '请假审批流程',
    instanceNo: 'INS202602190001',
    taskName: '部门审批',
    approver: '李四',
    result: 'approved',
    approvalTime: '2026-02-19 10:30:00',
    duration: '1小时',
    remark: '同意申请，已核实相关情况',
  },
  {
    id: 2,
    processName: '报销审批流程',
    instanceNo: 'INS202602190002',
    taskName: '财务审批',
    approver: '王五',
    result: 'rejected',
    approvalTime: '2026-02-19 14:20:00',
    duration: '30分钟',
    remark: '报销金额超出标准，请修改后重新提交',
  },
  {
    id: 3,
    processName: '采购审批流程',
    instanceNo: 'INS202602190003',
    taskName: '部门审批',
    approver: '张三',
    result: 'delegated',
    approvalTime: '2026-02-19 16:00:00',
    duration: '15分钟',
    remark: '委派给赵六处理',
  },
])

const columns = [
  { colKey: 'id', title: 'ID', width: 80 },
  { colKey: 'processName', title: '流程名称' },
  { colKey: 'instanceNo', title: '实例编号', width: 150 },
  { colKey: 'taskName', title: '任务名称', width: 120 },
  { colKey: 'approver', title: '审批人', width: 100 },
  { colKey: 'result', title: '审批结果', width: 100 },
  { colKey: 'approvalTime', title: '审批时间', width: 180 },
  { colKey: 'duration', title: '耗时', width: 100 },
  { colKey: 'action', title: '操作', width: 180 },
]

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 3,
})

const handleView = (row: any) => {
  MessagePlugin.info(`查看详情: ${row.instanceNo}`)
}

const handleViewProcess = (row: any) => {
  MessagePlugin.info(`查看流程: ${row.instanceNo}`)
}

const handleSearch = () => {
  loading.value = true
  setTimeout(() => {
    loading.value = false
  }, 500)
}

const handleReset = () => {
  Object.assign(searchForm, { processName: '', approver: '', result: '' })
}

const handlePageChange = (page: number) => {
  pagination.current = page
}
</script>

<style scoped lang="scss">
.approval-record {
  .search-bar {
    margin-bottom: 16px;
  }
}
</style>
