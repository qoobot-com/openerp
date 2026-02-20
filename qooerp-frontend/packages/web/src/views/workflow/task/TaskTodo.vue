<template>
  <div class="task-todo">
    <t-card title="待办任务" :bordered="false">
      <template #actions>
        <t-button theme="primary" @click="handleRefresh">
          <template #icon><refresh-icon /></template>
          刷新
        </t-button>
      </template>

      <!-- 搜索栏 -->
      <div class="search-bar">
        <t-form :data="searchForm" layout="inline" @submit="handleSearch">
          <t-form-item label="流程名称" name="processName">
            <t-input v-model="searchForm.processName" placeholder="请输入流程名称" clearable />
          </t-form-item>
          <t-form-item label="优先级" name="priority">
            <t-select v-model="searchForm.priority" placeholder="请选择优先级" clearable>
              <t-option value="high" label="高" />
              <t-option value="medium" label="中" />
              <t-option value="low" label="低" />
            </t-select>
          </t-form-item>
          <t-form-item>
            <t-button type="submit" theme="primary">搜索</t-button>
            <t-button theme="default" @click="handleReset">重置</t-button>
          </t-form-item>
        </t-form>
      </div>

      <!-- 任务统计 -->
      <div class="task-stats">
        <t-row :gutter="[16, 16]">
          <t-col :span="6">
            <t-statistic title="待办总数" :value="total" />
          </t-col>
          <t-col :span="6">
            <t-statistic title="高优先级" :value="highPriority" trend="increase" />
          </t-col>
          <t-col :span="6">
            <t-statistic title="即将超时" :value="overdue" trend="decrease" />
          </t-col>
          <t-col :span="6">
            <t-statistic title="今日新增" :value="todayNew" />
          </t-col>
        </t-row>
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
        <template #priority="{ row }">
          <t-tag
            :theme="{
              high: 'danger',
              medium: 'warning',
              low: 'default',
            }[row.priority]"
          >
            {{ { high: '高', medium: '中', low: '低' }[row.priority] }}
          </t-tag>
        </template>
        <template #status="{ row }">
          <t-tag v-if="row.isUrgent" theme="danger">即将超时</t-tag>
          <t-tag v-else theme="default">正常</t-tag>
        </template>
        <template #action="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleApprove(row)">办理</t-link>
            <t-link theme="warning" @click="handleDelegate(row)">委派</t-link>
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
import { RefreshIcon } from 'tdesign-icons-vue-next'

const loading = ref(false)
const total = ref(24)
const highPriority = ref(8)
const overdue = ref(3)
const todayNew = ref(12)

const searchForm = reactive({
  processName: '',
  priority: '',
})

const tableData = ref([
  {
    id: 1,
    taskName: '部门审批',
    processName: '请假审批流程',
    instanceNo: 'INS202602190001',
    initiator: '张三',
    priority: 'high',
    isUrgent: true,
    submitTime: '2026-02-19 09:30:00',
    deadline: '2026-02-19 17:00:00',
  },
  {
    id: 2,
    taskName: '财务审批',
    processName: '报销审批流程',
    instanceNo: 'INS202602190002',
    initiator: '李四',
    priority: 'medium',
    isUrgent: false,
    submitTime: '2026-02-19 10:20:00',
    deadline: '20xx-xx-xx 17:00:00',
  },
  {
    id: 3,
    taskName: '采购审批',
    processName: '采购审批流程',
    instanceNo: 'INS202602190003',
    initiator: '王五',
    priority: 'high',
    isUrgent: true,
    submitTime: '2026-02-19 11:00:00',
    deadline: '2026-02-19 16:00:00',
  },
  {
    id: 4,
    taskName: '合同审批',
    processName: '合同审批流程',
    instanceNo: 'INS202602190004',
    initiator: '赵六',
    priority: 'low',
    isUrgent: false,
    submitTime: '2026-02-19 14:00:00',
    deadline: '2026-02-22 17:00:00',
  },
])

const columns = [
  { colKey: 'id', title: 'ID', width: 80 },
  { colKey: 'taskName', title: '任务名称', width: 120 },
  { colKey: 'processName', title: '流程名称' },
  { colKey: 'instanceNo', title: '实例编号', width: 150 },
  { colKey: 'initiator', title: '发起人', width: 100 },
  { colKey: 'priority', title: '优先级', width: 80 },
  { colKey: 'status', title: '状态', width: 100 },
  { colKey: 'submitTime', title: '提交时间', width: 180 },
  { colKey: 'deadline', title: '截止时间', width: 180 },
  { colKey: 'action', title: '操作', width: 180 },
]

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 24,
})

const handleRefresh = () => {
  loading.value = true
  setTimeout(() => {
    loading.value = false
    MessagePlugin.success('刷新成功')
  }, 500)
}

const handleApprove = (row: any) => {
  MessagePlugin.info(`办理任务: ${row.taskName}`)
}

const handleDelegate = (row: any) => {
  MessagePlugin.info(`委派任务: ${row.taskName}`)
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
  Object.assign(searchForm, { processName: '', priority: '' })
}

const handlePageChange = (page: number) => {
  pagination.current = page
}
</script>

<style scoped lang="scss">
.task-todo {
  .search-bar {
    margin-bottom: 16px;
  }

  .task-stats {
    margin-bottom: 16px;
  }
}
</style>
