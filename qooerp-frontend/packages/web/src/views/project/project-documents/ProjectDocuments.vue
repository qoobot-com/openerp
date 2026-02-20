<template>
  <div class="project-documents">
    <t-row :gutter="[16, 16]" class="statistics-cards">
      <t-col :span="3"><t-card theme="primary1" :header-bordered="false"><div class="statistic-content"><div class="statistic-value">{{ statistics.total }}</div><div class="statistic-label">文档总数</div></div></t-card></t-col>
      <t-col :span="3"><t-card theme="success" :header-bordered="false"><div class="statistic-content"><div class="statistic-value">{{ statistics.versions }}</div><div class="statistic-label">版本数</div></div></t-card></t-col>
      <t-col :span="3"><t-card theme="warning" :header-bordered="false"><div class="statistic-content"><div class="statistic-value">{{ statistics.downloads }}</div><div class="statistic-label">下载次数</div></div></t-card></t-col>
      <t-col :span="3"><t-card theme="info" :header-bordered="false"><div class="statistic-content"><div class="statistic-value">{{ statistics.size }}</div><div class="statistic-label">总容量(MB)</div></div></t-card></t-col>
    </t-row>
    <t-card title="项目文档" class="table-card">
      <template #actions><t-space><t-button theme="primary" @click="handleUpload">上传文档</t-button><t-button theme="default" @click="handleExport">导出列表</t-button></t-space></template>
      <t-table :data="tableData" :columns="columns" :loading="loading" :pagination="pagination" @page-change="handlePageChange">
        <template #fileType="{ row }"><t-icon v-if="row.fileType === 'pdf'" name="file-pdf" size="24px" /><t-icon v-else-if="row.fileType === 'word'" name="file-word" size="24px" /><t-icon v-else-if="row.fileType === 'excel'" name="file-excel" size="24px" /><t-icon v-else name="file-image" size="24px" /></template>
        <template #action="{ row }"><t-space><t-link theme="primary" @click="handleView(row)">查看</t-link><t-link theme="primary" @click="handleDownload(row)">下载</t-link><t-link theme="danger" @click="handleDelete(row)">删除</t-link></t-space></template>
      </t-table>
    </t-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { MessagePlugin } from 'tdesign-vue-next'

const statistics = ref({ total: 200, versions: 500, downloads: 1000, size: 1024 })
const loading = ref(false)
const tableData = ref([
  { id: 1, docNo: 'DOC-001', docName: '项目需求规格说明书', project: '智能制造系统研发', fileType: 'word', version: 'v2.0', size: 2.5, uploadBy: '张三', uploadTime: '2026-02-19' },
  { id: 2, docNo: 'DOC-002', docName: '系统设计文档', project: '智能制造系统研发', fileType: 'pdf', version: 'v1.5', size: 5.0, uploadBy: '李四', uploadTime: '2026-02-18' },
  { id: 3, docNo: 'DOC-003', docName: '技术方案', project: '自动化生产线改造', fileType: 'excel', version: 'v1.0', size: 1.2, uploadBy: '王五', uploadTime: '20xx-xx-xx' }
])

const columns = [
  { colKey: 'docNo', title: '文档编号', width: 120 },
  { colKey: 'docName', title: '文档名称', width: 200 },
  { colKey: 'project', title: '所属项目', width: 200 },
  { colKey: 'fileType', title: '文件类型', width: 100, cell: 'fileType' },
  { colKey: 'version', title: '版本', width: 80 },
  { colKey: 'size', title: '大小(MB)', width: 100 },
  { colKey: 'uploadBy', title: '上传人', width: 100 },
  { colKey: 'uploadTime', title: '上传时间', width: 120 },
  { colKey: 'action', title: '操作', width: 200, cell: 'action' }
]

const pagination = reactive({ current: 1, pageSize: 10, total: 200 })
const handleUpload = () => { MessagePlugin.success('上传文档') }
const handleView = (row: any) => { MessagePlugin.success('查看文档') }
const handleDownload = (row: any) => { MessagePlugin.success('下载成功') }
const handleDelete = (row: any) => { MessagePlugin.success('删除成功') }
const handleExport = () => { MessagePlugin.success('导出成功') }
const handlePageChange = (pageInfo: any) => { pagination.current = pageInfo.current; pagination.pageSize = pageInfo.pageSize }
</script>

<style scoped lang="less">
.project-documents { padding: 24px; .statistics-cards { margin-bottom: 24px; .statistic-content { text-align: center; padding: 16px 0; .statistic-value { font-size: 32px; font-weight: 600; margin-bottom: 8px; } .statistic-label { font-size: 14px; color: rgba(0, 0, 0, 0.6); } } } .table-card { margin-bottom: 24px; } }
</style>
