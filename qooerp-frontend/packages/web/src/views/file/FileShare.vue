<template>
  <div class="file-share">
    <t-card title="文件共享" :bordered="false">
      <!-- 搜索栏 -->
      <div class="search-bar">
        <t-form :data="searchForm" layout="inline">
          <t-form-item label="文件名称" name="fileName">
            <t-input v-model="searchForm.fileName" placeholder="请输入文件名称" clearable />
          </t-form-item>
          <t-form-item label="共享类型" name="shareType">
            <t-select v-model="searchForm.shareType" placeholder="请选择类型" clearable>
              <t-option value="link" label="链接共享" />
              <t-option value="user" label="用户共享" />
              <t-option value="department" label="部门共享" />
            </t-select>
          </t-form-item>
          <t-form-item>
            <t-button theme="primary">搜索</t-button>
          </t-form-item>
        </t-form>
      </div>

      <!-- 共享文件列表 -->
      <t-table
        :data="shareList"
        :columns="columns"
        row-key="id"
        :pagination="{ pageSize: 10 }"
      >
        <template #shareType="{ row }">
          <t-tag :theme="{ link: 'primary', user: 'success', department: 'warning' }[row.shareType]">
            {{ { link: '链接共享', user: '用户共享', department: '部门共享' }[row.shareType] }}
          </t-tag>
        </template>
        <template #action="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleView(row)">查看</t-link>
            <t-link theme="primary" @click="handleCopyLink(row)">复制链接</t-link>
            <t-link theme="danger" @click="handleCancel(row)">取消共享</t-link>
          </t-space>
        </template>
      </t-table>
    </t-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { MessagePlugin } from 'tdesign-vue-next'

const searchForm = reactive({
  fileName: '',
  shareType: '',
})

const shareList = ref([
  {
    id: 1,
    fileName: '项目需求文档.docx',
    shareType: 'link',
    shareTarget: '公开链接',
    expireTime: '2026-03-19',
    createTime: '2026-02-19',
  },
  {
    id: 2,
    fileName: '设计稿.psd',
    shareType: 'user',
    shareTarget: '张三、李四',
    expireTime: '永久',
    createTime: '2026-02-18',
  },
  {
    id: 3,
    fileName: '数据报表.xlsx',
    shareType: 'department',
    shareTarget: '技术部、销售部',
    expireTime: '2026-02-26',
    createTime: '2026-02-17',
  },
])

const columns = [
  { colKey: 'fileName', title: '文件名称' },
  { colKey: 'shareType', title: '共享类型', width: 120 },
  { colKey: 'shareTarget', title: '共享对象' },
  { colKey: 'expireTime', title: '过期时间', width: 120 },
  { colKey: 'createTime', title: '创建时间', width: 120 },
  { colKey: 'action', title: '操作', width: 260 },
]

const handleView = (row: any) => {
  MessagePlugin.info(`查看: ${row.fileName}`)
}

const handleCopyLink = (row: any) => {
  MessagePlugin.success('链接已复制')
}

const handleCancel = (row: any) => {
  MessagePlugin.success(`取消共享: ${row.fileName}`)
}
</script>

<style scoped lang="scss">
.file-share {
  .search-bar {
    margin-bottom: 16px;
  }
}
</style>
