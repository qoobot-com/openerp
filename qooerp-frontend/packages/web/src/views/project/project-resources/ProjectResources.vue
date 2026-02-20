<template>
  <div class="project-resources">
    <t-row :gutter="[16, 16]" class="statistics-cards">
      <t-col :span="3"><t-card theme="primary1" :header-bordered="false"><div class="statistic-content"><div class="statistic-value">{{ statistics.members }}</div><div class="statistic-label">团队成员</div></div></t-card></t-col>
      <t-col :span="3"><t-card theme="success" :header-bordered="false"><div class="statistic-content"><div class="statistic-value">{{ statistics.available }}</div><div class="statistic-label">可用资源</div></div></t-card></t-col>
      <t-col :span="3"><t-card theme="warning" :header-bordered="false"><div class="statistic-content"><div class="statistic-value">{{ statistics.inUse }}</div><div class="statistic-label">使用中</div></div></t-card></t-col>
      <t-col :span="3"><t-card theme="info" :header-bordered="false"><div class="statistic-content"><div class="statistic-value">{{ statistics.utilization }}%</div><div class="statistic-label">利用率</div></div></t-card></t-col>
    </t-row>
    <t-card title="资源管理" class="table-card">
      <template #actions><t-space><t-button theme="primary" @click="handleAdd">新增资源</t-button><t-button theme="default" @click="handleExport">导出报表</t-button></t-space></template>
      <t-table :data="tableData" :columns="columns" :loading="loading" :pagination="pagination" @page-change="handlePageChange">
        <template #status="{ row }"><t-tag v-if="row.status === 'available'" theme="success" variant="light">可用</t-tag><t-tag v-else-if="row.status === 'in-use'" theme="warning" variant="light">使用中</t-tag><t-tag v-else theme="error" variant="light">不可用</t-tag></template>
        <template #action="{ row }"><t-space><t-link theme="primary" @click="handleEdit(row)">编辑</t-link><t-link theme="danger" @click="handleDelete(row)">删除</t-link></t-space></template>
      </t-table>
    </t-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { MessagePlugin } from 'tdesign-vue-next'

const statistics = ref({ members: 100, available: 60, inUse: 40, utilization: 67 })
const loading = ref(false)
const tableData = ref([
  { id: 1, resourceNo: 'RES-001', resourceName: '张三', resourceType: '人员', status: 'in-use', currentProject: '智能制造系统研发', skill: '开发工程师' },
  { id: 2, resourceNo: 'RES-002', resourceName: '李四', resourceType: '人员', status: 'available', currentProject: '', skill: '测试工程师' },
  { id: 3, resourceNo: 'RES-003', resourceName: '服务器A', resourceType: '设备', status: 'in-use', currentProject: '智能制造系统研发', skill: '' }
])

const columns = [
  { colKey: 'resourceNo', title: '资源编号', width: 120 },
  { colKey: 'resourceName', title: '资源名称', width: 120 },
  { colKey: 'resourceType', title: '资源类型', width: 100 },
  { colKey: 'skill', title: '技能/规格', width: 150 },
  { colKey: 'status', title: '状态', width: 100, cell: 'status' },
  { colKey: 'currentProject', title: '当前项目', width: 200 },
  { colKey: 'action', title: '操作', width: 150, cell: 'action' }
]

const pagination = reactive({ current: 1, pageSize: 10, total: 100 })
const handleAdd = () => { MessagePlugin.success('新增资源') }
const handleEdit = (row: any) => { MessagePlugin.success('编辑资源') }
const handleDelete = (row: any) => { MessagePlugin.success('删除成功') }
const handleExport = () => { MessagePlugin.success('导出成功') }
const handlePageChange = (pageInfo: any) => { pagination.current = pageInfo.current; pagination.pageSize = pageInfo.pageSize }
</script>

<style scoped lang="less">
.project-resources { padding: 24px; .statistics-cards { margin-bottom: 24px; .statistic-content { text-align: center; padding: 16px 0; .statistic-value { font-size: 32px; font-weight: 600; margin-bottom: 8px; } .statistic-label { font-size: 14px; color: rgba(0, 0, 0, 0.6); } } } .table-card { margin-bottom: 24px; } }
</style>
