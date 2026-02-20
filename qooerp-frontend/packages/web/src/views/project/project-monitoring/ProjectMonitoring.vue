<template>
  <div class="project-monitoring">
    <t-row :gutter="[16, 16]" class="statistics-cards">
      <t-col :span="3"><t-card theme="primary1" :header-bordered="false"><div class="statistic-content"><div class="statistic-value">{{ statistics.total }}</div><div class="statistic-label">监控项目</div></div></t-card></t-col>
      <t-col :span="3"><t-card theme="success" :header-bordered="false"><div class="statistic-content"><div class="statistic-value">{{ statistics.onTime }}</div><div class="statistic-label">按期完成</div></div></t-card></t-col>
      <t-col :span="3"><t-card theme="warning" :header-bordered="false"><div class="statistic-content"><div class="statistic-value">{{ statistics.delayed }}</div><div class="statistic-label">延期风险</div></div></t-card></t-col>
      <t-col :span="3"><t-card theme="error" :header-bordered="false"><div class="statistic-content"><div class="statistic-value">{{ statistics.overBudget }}</div><div class="statistic-label">超支</div></div></t-card></t-col>
    </t-row>
    <t-card title="项目监控" class="table-card">
      <template #actions><t-space><t-button theme="primary" @click="handleRefresh">刷新</t-button></t-space></template>
      <t-table :data="tableData" :columns="columns" :loading="loading" :pagination="pagination" @page-change="handlePageChange">
        <template #scheduleStatus="{ row }"><t-tag v-if="row.scheduleStatus === 'on-track'" theme="success" variant="light">按期</t-tag><t-tag v-else-if="row.scheduleStatus === 'delayed'" theme="warning" variant="light">延期</t-tag><t-tag v-else theme="error" variant="light">严重延期</t-tag></template>
        <template #budgetStatus="{ row }"><t-tag v-if="row.budgetStatus === 'normal'" theme="success" variant="light">正常</t-tag><t-tag v-else theme="error" variant="light">超支</t-tag></template>
        <template #progress="{ row }"><t-progress :percentage="row.progress" size="small" style="width: 100px" /></template>
        <template #action="{ row }"><t-space><t-link theme="primary" @click="handleView(row)">查看</t-link></t-space></template>
      </t-table>
    </t-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { MessagePlugin } from 'tdesign-vue-next'

const statistics = ref({ total: 30, onTime: 20, delayed: 8, overBudget: 2 })
const loading = ref(false)
const tableData = ref([
  { id: 1, projectNo: 'PRJ-001', projectName: '智能制造系统研发', progress: 65, scheduleStatus: 'on-track', budgetStatus: 'normal', startDate: '20xx-xx-xx', endDate: '2026-12-31', budget: 1000000, actualCost: 650000 },
  { id: 2, projectNo: 'PRJ-002', projectName: '自动化生产线改造', progress: 45, scheduleStatus: 'delayed', budgetStatus: 'normal', startDate: '2026-03-01', endDate: '2026-08-31', budget: 800000, actualCost: 400000 },
  { id: 3, projectNo: 'PRJ-003', projectName: '产品研发项目A', progress: 30, scheduleStatus: 'delayed', budgetStatus: 'over-budget', startDate: '2026-04-01', endDate: '2026-09-30', budget: 500000, actualCost: 550000 }
])

const columns = [
  { colKey: 'projectNo', title: '项目编号', width: 120 },
  { colKey: 'projectName', title: '项目名称', width: 200 },
  { colKey: 'progress', title: '完成进度', width: 120, cell: 'progress' },
  { colKey: 'scheduleStatus', title: '进度状态', width: 100, cell: 'scheduleStatus' },
  { colKey: 'budgetStatus', title: '预算状态', width: 100, cell: 'budgetStatus' },
  { colKey: 'budget', title: '预算', width: 120 },
  { colKey: 'actualCost', title: '实际成本', width: 120 },
  { colKey: 'endDate', title: '计划结束', width: 120 },
  { colKey: 'action', title: '操作', width: 100, cell: 'action' }
]

const pagination = reactive({ current: 1, pageSize: 10, total: 30 })
const handleRefresh = () => { MessagePlugin.success('刷新成功') }
const handlePageChange = (pageInfo: any) => { pagination.current = pageInfo.current; pagination.pageSize = pageInfo.pageSize }
const handleView = (row: any) => { console.log('查看项目', row) }
</script>

<style scoped lang="less">
.project-monitoring { padding: 24px; .statistics-cards { margin-bottom: 24px; .statistic-content { text-align: center; padding: 16px 0; .statistic-value { font-size: 32px; font-weight: 600; margin-bottom: 8px; } .statistic-label { font-size: 14px; color: rgba(0, 0, 0, 0.6); } } } .table-card { margin-bottom: 24px; } }
</style>
