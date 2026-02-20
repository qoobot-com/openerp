<template>
  <div class="project-execution">
    <!-- 统计卡片 -->
    <t-row :gutter="[16, 16]" class="statistics-cards">
      <t-col :span="3">
        <t-card theme="primary1" :header-bordered="false">
          <div class="statistic-content">
            <div class="statistic-value">{{ statistics.total }}</div>
            <div class="statistic-label">任务总数</div>
          </div>
        </t-card>
      </t-col>
      <t-col :span="3">
        <t-card theme="success" :header-bordered="false">
          <div class="statistic-content">
            <div class="statistic-value">{{ statistics.completed }}</div>
            <div class="statistic-label">已完成</div>
          </div>
        </t-card>
      </t-col>
      <t-col :span="3">
        <t-card theme="warning" :header-bordered="false">
          <div class="statistic-content">
            <div class="statistic-value">{{ statistics.pending }}</div>
            <div class="statistic-label">进行中</div>
          </div>
        </t-card>
      </t-col>
      <t-col :span="3">
        <t-card theme="info" :header-bordered="false">
          <div class="statistic-content">
            <div class="statistic-value">{{ statistics.overdue }}</div>
            <div class="statistic-label">已逾期</div>
          </div>
        </t-card>
      </t-col>
    </t-row>

    <!-- 搜索卡片 -->
    <t-card title="任务查询" class="search-card">
      <t-form :data="searchForm" layout="inline" @submit="handleSearch">
        <t-form-item label="任务编号" name="taskNo">
          <t-input v-model="searchForm.taskNo" placeholder="请输入任务编号" clearable />
        </t-form-item>
        <t-form-item label="任务名称" name="taskName">
          <t-input v-model="searchForm.taskName" placeholder="请输入任务名称" clearable />
        </t-form-item>
        <t-form-item label="任务状态" name="status">
          <t-select v-model="searchForm.status" placeholder="请选择任务状态" clearable>
            <t-option value="pending" label="待开始" />
            <t-option value="in-progress" label="进行中" />
            <t-option value="completed" label="已完成" />
            <t-option value="overdue" label="已逾期" />
          </t-select>
        </t-form-item>
        <t-form-item>
          <t-space>
            <t-button theme="primary" type="submit">查询</t-button>
            <t-button theme="default" @click="handleReset">重置</t-button>
          </t-space>
        </t-form-item>
      </t-form>
    </t-card>

    <!-- 任务列表 -->
    <t-card title="任务列表" class="table-card">
      <template #actions>
        <t-space>
          <t-button theme="primary" @click="handleAdd">
            <template #icon><add-icon /></template>
            新增任务
          </t-button>
          <t-button theme="default" @click="handleExport">
            <template #icon><download-icon /></template>
            导出报表
          </t-button>
        </t-space>
      </template>
      <t-table
        :data="tableData"
        :columns="columns"
        :row-key="rowKey"
        :loading="loading"
        :pagination="pagination"
        @page-change="handlePageChange"
      >
        <template #status="{ row }">
          <t-tag v-if="row.status === 'pending'" theme="default" variant="light">待开始</t-tag>
          <t-tag v-else-if="row.status === 'in-progress'" theme="primary" variant="light">进行中</t-tag>
          <t-tag v-else-if="row.status === 'completed'" theme="success" variant="light">已完成</t-tag>
          <t-tag v-else theme="error" variant="light">已逾期</t-tag>
        </template>
        <template #progress="{ row }">
          <t-progress :percentage="row.progress" size="small" style="width: 100px" />
        </template>
        <template #action="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleView(row)">查看</t-link>
            <t-link theme="primary" @click="handleEdit(row)">编辑</t-link>
            <t-link v-if="row.status === 'pending'" theme="success" @click="handleStart(row)">开始</t-link>
            <t-link v-if="row.status === 'in-progress'" theme="success" @click="handleComplete(row)">完成</t-link>
          </t-space>
        </template>
      </t-table>
    </t-card>

    <!-- 新增/编辑任务弹窗 -->
    <t-dialog
      v-model:visible="dialogVisible"
      :header="dialogTitle"
      :width="700"
      :confirm-btn="null"
      :cancel-btn="null"
      @close="handleDialogClose"
    >
      <t-form :data="formData" ref="formRef" :rules="rules" label-width="120px">
        <t-form-item label="任务编号" name="taskNo">
          <t-input v-model="formData.taskNo" placeholder="请输入任务编号" />
        </t-form-item>
        <t-form-item label="任务名称" name="taskName">
          <t-input v-model="formData.taskName" placeholder="请输入任务名称" />
        </t-form-item>
        <t-form-item label="关联项目" name="projectName">
          <t-input v-model="formData.projectName" placeholder="请输入关联项目" />
        </t-form-item>
        <t-form-item label="负责人" name="assignee">
          <t-select v-model="formData.assignee" placeholder="请选择负责人" filterable>
            <t-option value="1" label="张三" />
            <t-option value="2" label="李四" />
            <t-option value="3" label="王五" />
          </t-select>
        </t-form-item>
        <t-form-item label="计划开始日期" name="startDate">
          <t-date-picker v-model="formData.startDate" placeholder="请选择计划开始日期" />
        </t-form-item>
        <t-form-item label="计划结束日期" name="endDate">
          <t-date-picker v-model="formData.endDate" placeholder="请选择计划结束日期" />
        </t-form-item>
        <t-form-item label="预计工时" name="estimatedHours">
          <t-input-number v-model="formData.estimatedHours" :min="0" placeholder="请输入预计工时" />
        </t-form-item>
        <t-form-item label="任务描述" name="description">
          <t-textarea v-model="formData.description" placeholder="请输入任务描述" :maxlength="500" />
        </t-form-item>
      </t-form>
      <template #footer>
        <t-space>
          <t-button theme="default" @click="handleDialogClose">取消</t-button>
          <t-button theme="primary" @click="handleSubmit">确定</t-button>
        </t-space>
      </template>
    </t-dialog>

    <!-- 任务详情弹窗 -->
    <t-dialog
      v-model:visible="detailVisible"
      header="任务详情"
      :width="800"
      :footer="false"
    >
      <t-descriptions :column="2" bordered>
        <t-descriptions-item label="任务编号">{{ detailData.taskNo }}</t-descriptions-item>
        <t-descriptions-item label="任务名称">{{ detailData.taskName }}</t-descriptions-item>
        <t-descriptions-item label="关联项目">{{ detailData.projectName }}</t-descriptions-item>
        <t-descriptions-item label="负责人">{{ detailData.assignee }}</t-descriptions-item>
        <t-descriptions-item label="任务状态">
          <t-tag v-if="detailData.status === 'pending'" theme="default" variant="light">待开始</t-tag>
          <t-tag v-else-if="detailData.status === 'in-progress'" theme="primary" variant="light">进行中</t-tag>
          <t-tag v-else-if="detailData.status === 'completed'" theme="success" variant="light">已完成</t-tag>
          <t-tag v-else theme="error" variant="light">已逾期</t-tag>
        </t-descriptions-item>
        <t-descriptions-item label="完成进度">{{ detailData.progress }}%</t-descriptions-item>
        <t-descriptions-item label="计划开始日期">{{ detailData.startDate }}</t-descriptions-item>
        <t-descriptions-item label="计划结束日期">{{ detailData.endDate }}</t-descriptions-item>
        <t-descriptions-item label="预计工时">{{ detailData.estimatedHours }}小时</t-descriptions-item>
        <t-descriptions-item label="任务描述" :span="2">{{ detailData.description }}</t-descriptions-item>
      </t-descriptions>
    </t-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { MessagePlugin, DialogPlugin } from 'tdesign-vue-next'
import { AddIcon, DownloadIcon } from 'tdesign-icons-vue-next'

// 统计数据
const statistics = ref({
  total: 300,
  completed: 200,
  pending: 90,
  overdue: 10
})

// 搜索表单
const searchForm = reactive({
  taskNo: '',
  taskName: '',
  status: ''
})

// 表格数据
const loading = ref(false)
const tableData = ref([
  { id: 1, taskNo: 'TASK-001', taskName: '需求分析', projectName: '智能制造系统研发', assignee: '1', startDate: '20xx-xx-xx', endDate: '2026-03-31', estimatedHours: 160, status: 'completed', progress: 100, description: '完成需求分析' },
  { id: 2, taskNo: 'TASK-002', taskName: '系统设计', projectName: '智能制造系统研发', assignee: '2', startDate: '2026-04-01', endDate: '2026-05-31', estimatedHours: 320, status: 'in-progress', progress: 50, description: '进行系统设计' },
  { id: 3, taskNo: 'TASK-003', taskName: '设备安装', projectName: '自动化生产线改造', assignee: '3', startDate: '2026-03-01', endDate: '2026-03-15', estimatedHours: 80, status: 'pending', progress: 0, description: '安装设备' }
])

const columns = [
  { colKey: 'taskNo', title: '任务编号', width: 120 },
  { colKey: 'taskName', title: '任务名称', width: 150 },
  { colKey: 'projectName', title: '关联项目', width: 150 },
  { colKey: 'assignee', title: '负责人', width: 100 },
  { colKey: 'startDate', title: '计划开始', width: 120 },
  { colKey: 'endDate', title: '计划结束', width: 120 },
  { colKey: 'estimatedHours', title: '预计工时', width: 100 },
  { colKey: 'progress', title: '完成进度', width: 120, cell: 'progress' },
  { colKey: 'status', title: '任务状态', width: 100, cell: 'status' },
  { colKey: 'action', title: '操作', width: 200, fixed: 'right', cell: 'action' }
]

const pagination = reactive({ current: 1, pageSize: 10, total: 300 })
const rowKey = 'id'
const dialogVisible = ref(false)
const dialogTitle = computed(() => (formData.id ? '编辑任务' : '新增任务'))
const formRef = ref()
const formData = reactive({
  id: undefined,
  taskNo: '',
  taskName: '',
  projectName: '',
  assignee: '',
  startDate: '',
  endDate: '',
  estimatedHours: 0,
  description: ''
})

const rules = {
  taskNo: [{ required: true, message: '请输入任务编号' }],
  taskName: [{ required: true, message: '请输入任务名称' }],
  projectName: [{ required: true, message: '请输入关联项目' }],
  assignee: [{ required: true, message: '请选择负责人' }],
  startDate: [{ required: true, message: '请选择计划开始日期' }],
  endDate: [{ required: true, message: '请选择计划结束日期' }],
  estimatedHours: [{ required: true, message: '请输入预计工时' }]
}

const detailVisible = ref(false)
const detailData = ref<any>({})

const handleSearch = () => { loading.value = true; setTimeout(() => { loading.value = false; MessagePlugin.success('查询成功') }, 500) }
const handleReset = () => { Object.assign(searchForm, { taskNo: '', taskName: '', status: '' }); MessagePlugin.success('已重置') }
const handleAdd = () => { Object.assign(formData, { id: undefined, taskNo: '', taskName: '', projectName: '', assignee: '', startDate: '', endDate: '', estimatedHours: 0, description: '' }); dialogVisible.value = true }
const handleEdit = (row: any) => { Object.assign(formData, { ...row }); dialogVisible.value = true }
const handleStart = (row: any) => { MessagePlugin.success('任务已开始') }
const handleComplete = (row: any) => { MessagePlugin.success('任务已完成') }
const handleDelete = (row: any) => { MessagePlugin.success('删除成功') }
const handleExport = () => { MessagePlugin.success('导出成功') }
const handlePageChange = (pageInfo: any) => { pagination.current = pageInfo.current; pagination.pageSize = pageInfo.pageSize }
const handleSubmit = async () => { const valid = await formRef.value?.validate(); if (valid === true) { dialogVisible.value = false; MessagePlugin.success(formData.id ? '更新成功' : '新增成功') } }
const handleDialogClose = () => { dialogVisible.value = false }
const handleView = (row: any) => { detailData.value = { ...row }; detailVisible.value = true }
</script>

<style scoped lang="less">
.project-execution { padding: 24px; .statistics-cards { margin-bottom: 24px; .statistic-content { text-align: center; padding: 16px 0; .statistic-value { font-size: 32px; font-weight: 600; margin-bottom: 8px; } .statistic-label { font-size: 14px; color: rgba(0, 0, 0, 0.6); } } } .search-card, .table-card { margin-bottom: 24px; } }
</style>
