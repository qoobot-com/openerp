<template>
  <div class="approval-delegation">
    <t-card title="审批委托" :bordered="false">
      <template #actions>
        <t-button theme="primary" @click="handleCreate">
          <template #icon><add-icon /></template>
          新建委托
        </t-button>
      </template>

      <!-- 搜索栏 -->
      <div class="search-bar">
        <t-form :data="searchForm" layout="inline" @submit="handleSearch">
          <t-form-item label="委托人" name="delegator">
            <t-input v-model="searchForm.delegator" placeholder="请输入委托人" clearable />
          </t-form-item>
          <t-form-item label="状态" name="status">
            <t-select v-model="searchForm.status" placeholder="请选择状态" clearable>
              <t-option value="active" label="生效中" />
              <t-option value="expired" label="已过期" />
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
        <template #status="{ row }">
          <t-tag :theme="row.status === 'active' ? 'success' : 'default'">
            {{ row.status === 'active' ? '生效中' : '已过期' }}
          </t-tag>
        </template>
        <template #action="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleView(row)">查看</t-link>
            <t-link v-if="row.status === 'active'" theme="danger" @click="handleCancel(row)">取消</t-link>
            <t-link theme="danger" @click="handleDelete(row)">删除</t-link>
          </t-space>
        </template>
      </t-table>
    </t-card>

    <!-- 新建/编辑对话框 -->
    <t-dialog
      v-model:visible="dialogVisible"
      :header="dialogTitle"
      width="600px"
      @confirm="handleSubmit"
    >
      <t-form :data="formData" label-width="120px">
        <t-form-item label="被委托人" name="delegatee">
          <t-input v-model="formData.delegatee" placeholder="请选择被委托人" />
        </t-form-item>
        <t-form-item label="开始时间" name="startTime">
          <t-date-picker v-model="formData.startTime" placeholder="请选择开始时间" />
        </t-form-item>
        <t-form-item label="结束时间" name="endTime">
          <t-date-picker v-model="formData.endTime" placeholder="请选择结束时间" />
        </t-form-item>
        <t-form-item label="委托范围" name="scope">
          <t-checkbox-group v-model="formData.scope">
            <t-checkbox value="all">全部流程</t-checkbox>
            <t-checkbox value="specific">指定流程</t-checkbox>
          </t-checkbox-group>
        </t-form-item>
        <t-form-item label="委托原因" name="reason">
          <t-textarea v-model="formData.reason" placeholder="请输入委托原因" :autosize="{ minRows: 3 }" />
        </t-form-item>
      </t-form>
    </t-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { MessagePlugin } from 'tdesign-vue-next'
import { AddIcon } from 'tdesign-icons-vue-next'

const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('新建委托')
const searchForm = reactive({
  delegator: '',
  status: '',
})
const formData = reactive({
  delegatee: '',
  startTime: '',
  endTime: '',
  scope: ['all'],
  reason: '',
})

const tableData = ref([
  {
    id: 1,
    delegator: '张三',
    delegatee: '李四',
    startTime: '2026-02-19',
    endTime: '2026-02-25',
    scope: '全部流程',
    reason: '外出培训期间委托审批',
    status: 'active',
    createTime: '2026-02-18',
  },
  {
    id: 2,
    delegator: '王五',
    delegatee: '赵六',
    startTime: '2026-02-15',
    endTime: '2026-02-17',
    scope: '请假审批流程',
    reason: '休假期间委托审批',
    status: 'expired',
    createTime: '2026-02-14',
  },
])

const columns = [
  { colKey: 'id', title: 'ID', width: 80 },
  { colKey: 'delegator', title: '委托人', width: 100 },
  { colKey: 'delegatee', title: '被委托人', width: 100 },
  { colKey: 'startTime', title: '开始时间', width: 120 },
  { colKey: 'endTime', title: '结束时间', width: 120 },
  { colKey: 'scope', title: '委托范围', width: 120 },
  { colKey: 'status', title: '状态', width: 100 },
  { colKey: 'createTime', title: '创建时间', width: 120 },
  { colKey: 'action', title: '操作', width: 180 },
]

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 2,
})

const handleCreate = () => {
  dialogTitle.value = '新建委托'
  dialogVisible.value = true
}

const handleView = (row: any) => {
  MessagePlugin.info(`查看委托: ${row.delegator}`)
}

const handleCancel = (row: any) => {
  MessagePlugin.success(`取消委托: ${row.delegator}`)
  row.status = 'expired'
}

const handleDelete = (row: any) => {
  MessagePlugin.success(`删除委托: ${row.delegator}`)
}

const handleSubmit = () => {
  MessagePlugin.success('保存成功')
  dialogVisible.value = false
}

const handleSearch = () => {
  loading.value = true
  setTimeout(() => {
    loading.value = false
  }, 500)
}

const handleReset = () => {
  Object.assign(searchForm, { delegator: '', status: '' })
}

const handlePageChange = (page: number) => {
  pagination.current = page
}
</script>

<style scoped lang="scss">
.approval-delegation {
  .search-bar {
    margin-bottom: 16px;
  }
}
</style>
