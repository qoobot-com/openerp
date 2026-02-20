<template>
  <div class="process-version">
    <t-card title="流程版本管理" :bordered="false">
      <template #actions>
        <t-button theme="primary" @click="handleNewVersion">
          <template #icon><add-icon /></template>
          新建版本
        </t-button>
      </template>

      <!-- 流程信息 -->
      <div class="process-info">
        <t-descriptions :column="3" bordered>
          <t-descriptions-item label="流程名称">请假审批流程</t-descriptions-item>
          <t-descriptions-item label="流程分类">审批流程</t-descriptions-item>
          <t-descriptions-item label="当前版本">v2.1</t-descriptions-item>
          <t-descriptions-item label="创建人">张三</t-descriptions-item>
          <t-descriptions-item label="创建时间">2026-02-01</t-descriptions-item>
          <t-descriptions-item label="总实例数">256</t-descriptions-item>
        </t-descriptions>
      </div>

      <!-- 版本列表 -->
      <t-table
        :data="versionData"
        :columns="columns"
        row-key="version"
        class="version-table"
      >
        <template #status="{ row }">
          <t-tag v-if="row.isCurrent" theme="success">当前版本</t-tag>
          <t-tag v-else-if="row.isPublished" theme="primary">已发布</t-tag>
          <t-tag v-else theme="default">草稿</t-tag>
        </template>
        <template #action="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleView(row)">查看</t-link>
            <t-link theme="primary" @click="handleEdit(row)">编辑</t-link>
            <t-link v-if="!row.isCurrent && row.isPublished" theme="primary" @click="handleActivate(row)">启用</t-link>
            <t-link v-if="!row.isCurrent" theme="danger" @click="handleDelete(row)">删除</t-link>
          </t-space>
        </template>
      </t-table>
    </t-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { MessagePlugin } from 'tdesign-vue-next'
import { AddIcon } from 'tdesign-icons-vue-next'

const versionData = ref([
  {
    version: 'v2.1',
    description: '优化审批节点配置',
    isCurrent: true,
    isPublished: true,
    instances: 45,
    publishTime: '2026-02-15 10:30:00',
    publishBy: '李四',
  },
  {
    version: 'v2.0',
    description: '新增条件判断逻辑',
    isCurrent: false,
    isPublished: true,
    instances: 156,
    publishTime: '2026-02-10 14:20:00',
    publishBy: '张三',
  },
  {
    version: 'v1.2',
    description: '修复审批人员配置问题',
    isCurrent: false,
    isPublished: true,
    instances: 55,
    publishTime: '2026-02-05 09:15:00',
    publishBy: '王五',
  },
  {
    version: 'v1.1',
    description: '初始版本',
    isCurrent: false,
    isPublished: false,
    instances: 0,
    publishTime: '',
    publishBy: '',
  },
])

const columns = [
  { colKey: 'version', title: '版本号', width: 100 },
  { colKey: 'description', title: '版本说明' },
  { colKey: 'instances', title: '实例数', width: 100 },
  { colKey: 'status', title: '状态', width: 120 },
  { colKey: 'publishTime', title: '发布时间', width: 180 },
  { colKey: 'publishBy', title: '发布人', width: 100 },
  { colKey: 'action', title: '操作', width: 200 },
]

const handleNewVersion = () => {
  MessagePlugin.info('新建版本')
}

const handleView = (row: any) => {
  MessagePlugin.info(`查看版本: ${row.version}`)
}

const handleEdit = (row: any) => {
  MessagePlugin.info(`编辑版本: ${row.version}`)
}

const handleActivate = (row: any) => {
  MessagePlugin.success(`启用版本: ${row.version}`)
}

const handleDelete = (row: any) => {
  MessagePlugin.success(`删除版本: ${row.version}`)
}
</script>

<style scoped lang="scss">
.process-version {
  .process-info {
    margin-bottom: 16px;
  }

  .version-table {
    margin-top: 16px;
  }
}
</style>
