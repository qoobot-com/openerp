<template>
  <div class="project-reports">
    <t-row :gutter="[16, 16]" class="statistics-cards">
      <t-col :span="3"><t-card theme="primary1" :header-bordered="false"><div class="statistic-content"><div class="statistic-value">{{ statistics.total }}</div><div class="statistic-label">总项目数</div></div></t-card></t-col>
      <t-col :span="3"><t-card theme="success" :header-bordered="false"><div class="statistic-content"><div class="statistic-value">{{ statistics.onSchedule }}</div><div class="statistic-label">按期完成</div></div></t-card></t-col>
      <t-col :span="3"><t-card theme="warning" :header-bordered="false"><div class="statistic-content"><div class="statistic-value">{{ statistics.delayed }}</div><div class="statistic-label">延期项目</div></div></t-card></t-col>
      <t-col :span="3"><t-card theme="info" :header-bordered="false"><div class="statistic-content"><div class="statistic-value">{{ statistics.totalBudget }}</div><div class="statistic-label">总预算(万)</div></div></t-card></t-col>
    </t-row>
    <t-card title="项目报表" class="table-card">
      <template #actions><t-space><t-button theme="primary" @click="handleExport">导出报表</t-button></t-space></template>
      <t-table :data="tableData" :columns="columns" :loading="loading" :pagination="pagination" @page-change="handlePageChange">
        <template #scheduleStatus="{ row }"><t-tag v-if="row.scheduleStatus === 'on-time'" theme="success" variant="light">按期</t-tag><t-tag v-else theme="error" variant="light">延期</t-tag></template>
        <template #budgetStatus="{ row }"><t-tag v-if="row.budgetStatus === 'normal'" theme="success" variant="light">正常</t-tag><t-tag v-else theme="error" variant="light">超支</t-tag></template>
        <template #action="{ row }"><t-space><t-link theme="primary" @click="handleView(row)">查看</t-link></t-space></template>
      </t-table>
    </t-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { MessagePlugin } from 'tdesign-vue-next'

const statistics = ref({ total: 50, onSchedule: 40, delayed: 10, totalBudget: 5000 })
const loading = ref(false)
const tableData = ref([
  { id: 1, projectNo: 'PRJ-001', projectName: '智能制造系统研发', projectManager: '张三', startDate: '2026-02-20', endDate: '2026-12-31', progress: 65, budget: 1000, actualCost: 650, scheduleStatus: 'on-time', budgetStatus: 'normal' },
  { id: 2, projectNo: 'PRJ-002', projectName: '自动化生产线改造', projectManager: '李四', startDate: '2026-03-01', endDate: '2026-08-31', progress: 45, budget: 800, actualCost: 400, scheduleStatus: 'delayed', budgetStatus: 'normal' },
  { id: 3, projectNo: 'PRJ-003', projectName: '产品研发项目A', projectManager: '王五', startDate: '2026-04-01', endDate: '2026-09-30', progress: 30, budget: 500, actualCost: 550, scheduleStatus: 'on-time', budgetStatus: 'over-budget' }
])

const columns = [
  { colKey: 'projectNo', title: '项目编号', width: 120 },
  { colKey: 'projectName', title: '项目名称', width: 200 },
  { colKey: 'projectManager', title: '项目经理', width: 100 },
  { colKey: 'startDate', title: '开始日期', width: 120 },
  { colKey: 'endDate', title: '结束日期', width: 120 },
  { colKey: 'progress', title: '进度(%)', width: 100 },
  { colKey: 'budget', title: '预算(万)', width: 100 },
  { colKey: 'actualCost', title: '实际(万)', width: 100 },
  { colKey: 'scheduleStatus', title: '进度状态', width: 100, cell: 'scheduleStatus' },
  { colKey: 'budgetStatus', title: '预算状态', width: 100, cell: 'budgetStatus' },
  { colKey: 'action', title: '操作', width: 100, cell: 'action' }
]

const pagination = reactive({ current: 1, pageSize: 10, total: 50 })
const handleExport = () => { MessagePlugin.success('导出成功') }
const handlePageChange = (pageInfo: any) => { pagination.current = pageInfo.current; pagination.pageSize = pageInfo.pageSize }
const handleView = (row: any) => { console.log('查看项目', row) }
</script>

<style scoped lang="less">
.project-reports { padding: 24px; .statistics-cards { margin-bottom: 24px; .statistic-content { text-align: center; padding: 16px 0; .statistic-value { font-size: 32px; font-weight: 600; margin-bottom: 8px; } .statistic-label { font-size: 14px; color: rgba(0, 0, 0, 0.6); } } } .table-card { margin-bottom: 24px; } }
</style>
