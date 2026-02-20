<template>
  <div class="project-closure">
    <t-row :gutter="[16, 16]" class="statistics-cards">
      <t-col :span="3"><t-card theme="success" :header-bordered="false"><div class="statistic-content"><div class="statistic-value">{{ statistics.completed }}</div><div class="statistic-label">已收尾</div></div></t-card></t-col>
      <t-col :span="3"><t-card theme="info" :header-bordered="false"><div class="statistic-content"><div class="statistic-value">{{ statistics.pending }}</div><div class="statistic-label">待收尾</div></div></t-card></t-col>
      <t-col :span="3"><t-card theme="warning" :header-bordered="false"><div class="statistic-content"><div class="statistic-value">{{ statistics.avgDuration }}</div><div class="statistic-label">平均周期(天)</div></div></t-card></t-col>
      <t-col :span="3"><t-card theme="primary1" :header-bordered="false"><div class="statistic-content"><div class="statistic-value">{{ statistics.successRate }}%</div><div class="statistic-label">成功率</div></div></t-card></t-col>
    </t-row>
    <t-card title="项目收尾" class="table-card">
      <t-table :data="tableData" :columns="columns" :loading="loading" :pagination="pagination" @page-change="handlePageChange">
        <template #status="{ row }"><t-tag v-if="row.status === 'completed'" theme="success" variant="light">已完成</t-tag><t-tag v-else-if="row.status === 'closing'" theme="warning" variant="light">收尾中</t-tag><t-tag v-else theme="default" variant="light">待收尾</t-tag></template>
        <template #action="{ row }"><t-space><t-link theme="primary" @click="handleView(row)">查看</t-link><t-link v-if="row.status === 'pending'" theme="success" @click="handleClose(row)">收尾</t-link></t-space></template>
      </t-table>
    </t-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { MessagePlugin } from 'tdesign-vue-next'

const statistics = ref({ completed: 50, pending: 10, avgDuration: 180, successRate: 95 })
const loading = ref(false)
const tableData = ref([
  { id: 1, projectNo: 'PRJ-2025-001', projectName: '智能制造系统研发', status: 'completed', actualStartDate: '2025-01-01', actualEndDate: '2025-12-31', budget: 1000000, actualCost: 950000, duration: 365 },
  { id: 2, projectNo: 'PRJ-2025-002', projectName: '自动化生产线改造', status: 'closing', actualStartDate: '2025-06-01', actualEndDate: '2026-01-31', budget: 800000, actualCost: 820000, duration: 245 },
  { id: 3, projectNo: 'PRJ-2025-003', projectName: '产品研发项目A', status: 'pending', actualStartDate: '2025-09-01', actualEndDate: '2026-02-28', budget: 500000, actualCost: 480000, duration: 180 }
])

const columns = [
  { colKey: 'projectNo', title: '项目编号', width: 150 },
  { colKey: 'projectName', title: '项目名称', width: 200 },
  { colKey: 'status', title: '状态', width: 100, cell: 'status' },
  { colKey: 'actualStartDate', title: '实际开始', width: 120 },
  { colKey: 'actualEndDate', title: '实际结束', width: 120 },
  { colKey: 'duration', title: '周期(天)', width: 100 },
  { colKey: 'budget', title: '预算', width: 120 },
  { colKey: 'actualCost', title: '实际成本', width: 120 },
  { colKey: 'action', title: '操作', width: 150, cell: 'action' }
]

const pagination = reactive({ current: 1, pageSize: 10, total: 60 })
const handlePageChange = (pageInfo: any) => { pagination.current = pageInfo.current; pagination.pageSize = pageInfo.pageSize }
const handleClose = (row: any) => { MessagePlugin.success('项目已进入收尾流程') }
const handleView = (row: any) => { console.log('查看项目', row) }
</script>

<style scoped lang="less">
.project-closure { padding: 24px; .statistics-cards { margin-bottom: 24px; .statistic-content { text-align: center; padding: 16px 0; .statistic-value { font-size: 32px; font-weight: 600; margin-bottom: 8px; } .statistic-label { font-size: 14px; color: rgba(0, 0, 0, 0.6); } } } .table-card { margin-bottom: 24px; } }
</style>
