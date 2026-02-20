<template>
  <div class="project-planning">
    <!-- 统计卡片 -->
    <t-row :gutter="[16, 16]" class="statistics-cards">
      <t-col :span="3">
        <t-card theme="primary1" :header-bordered="false">
          <div class="statistic-content">
            <div class="statistic-value">{{ statistics.total }}</div>
            <div class="statistic-label">计划总数</div>
          </div>
        </t-card>
      </t-col>
      <t-col :span="3">
        <t-card theme="success" :header-bordered="false">
          <div class="statistic-content">
            <div class="statistic-value">{{ statistics.executing }}</div>
            <div class="statistic-label">执行中</div>
          </div>
        </t-card>
      </t-col>
      <t-col :span="3">
        <t-card theme="warning" :header-bordered="false">
          <div class="statistic-content">
            <div class="statistic-value">{{ statistics.onSchedule }}</div>
            <div class="statistic-label">按计划</div>
          </div>
        </t-card>
      </t-col>
      <t-col :span="3">
        <t-card theme="info" :header-bordered="false">
          <div class="statistic-content">
            <div class="statistic-value">{{ statistics.tasks }}</div>
            <div class="statistic-label">任务总数</div>
          </div>
        </t-card>
      </t-col>
    </t-row>

    <!-- 搜索卡片 -->
    <t-card title="计划查询" class="search-card">
      <t-form :data="searchForm" layout="inline" @submit="handleSearch">
        <t-form-item label="计划编号" name="planNo">
          <t-input v-model="searchForm.planNo" placeholder="请输入计划编号" clearable />
        </t-form-item>
        <t-form-item label="项目名称" name="projectName">
          <t-input v-model="searchForm.projectName" placeholder="请输入项目名称" clearable />
        </t-form-item>
        <t-form-item label="计划状态" name="status">
          <t-select v-model="searchForm.status" placeholder="请选择计划状态" clearable>
            <t-option value="draft" label="草稿" />
            <t-option value="approved" label="已批准" />
            <t-option value="executing" label="执行中" />
            <t-option value="completed" label="已完成" />
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

    <!-- 计划列表 -->
    <t-card title="计划列表" class="table-card">
      <template #actions>
        <t-space>
          <t-button theme="primary" @click="handleAdd">
            <template #icon><add-icon /></template>
            新增计划
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
          <t-tag v-if="row.status === 'draft'" theme="default" variant="light">草稿</t-tag>
          <t-tag v-else-if="row.status === 'approved'" theme="success" variant="light">已批准</t-tag>
          <t-tag v-else-if="row.status === 'executing'" theme="primary" variant="light">执行中</t-tag>
          <t-tag v-else theme="info" variant="light">已完成</t-tag>
        </template>
        <template #progress="{ row }">
          <t-progress :percentage="row.progress" size="small" style="width: 100px" />
        </template>
        <template #action="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleView(row)">查看</t-link>
            <t-link theme="primary" @click="handleEdit(row)">编辑</t-link>
            <t-link theme="danger" @click="handleDelete(row)">删除</t-link>
          </t-space>
        </template>
      </t-table>
    </t-card>

    <!-- 新增/编辑计划弹窗 -->
    <t-dialog
      v-model:visible="dialogVisible"
      :header="dialogTitle"
      :width="900"
      :confirm-btn="null"
      :cancel-btn="null"
      @close="handleDialogClose"
    >
      <t-tabs v-model="activeTab" theme="card">
        <t-tab-panel value="basic" label="基本信息">
          <t-form :data="formData" ref="formRef" :rules="rules" label-width="120px">
            <t-form-item label="计划编号" name="planNo">
              <t-input v-model="formData.planNo" placeholder="请输入计划编号" />
            </t-form-item>
            <t-form-item label="计划名称" name="planName">
              <t-input v-model="formData.planName" placeholder="请输入计划名称" />
            </t-form-item>
            <t-form-item label="关联项目" name="projectName">
              <t-input v-model="formData.projectName" placeholder="请输入关联项目" />
            </t-form-item>
            <t-form-item label="计划周期" name="planPeriod">
              <t-date-range-picker v-model="formData.planPeriod" placeholder="请选择计划周期" />
            </t-form-item>
            <t-form-item label="计划说明" name="description">
              <t-textarea v-model="formData.description" placeholder="请输入计划说明" :maxlength="1000" />
            </t-form-item>
          </t-form>
        </t-tab-panel>
        <t-tab-panel value="tasks" label="任务列表">
          <div class="tasks-section">
            <t-button theme="primary" size="small" @click="handleAddTask">
              <template #icon><add-icon /></template>
              添加任务
            </t-button>
            <t-table
              :data="formData.tasks"
              :columns="taskColumns"
              :pagination="false"
              row-key="id"
            >
              <template #action="{ row, rowIndex }">
                <t-link theme="danger" @click="handleDeleteTask(rowIndex)">删除</t-link>
              </template>
            </t-table>
          </div>
        </t-tab-panel>
        <t-tab-panel value="summary" label="汇总信息">
          <t-descriptions :column="2" bordered>
            <t-descriptions-item label="计划编号">{{ formData.planNo }}</t-descriptions-item>
            <t-descriptions-item label="计划名称">{{ formData.planName }}</t-descriptions-item>
            <t-descriptions-item label="关联项目">{{ formData.projectName }}</t-descriptions-item>
            <t-descriptions-item label="计划周期">{{ formData.planPeriod?.join(' ~ ') }}</t-descriptions-item>
            <t-descriptions-item label="任务数量">{{ formData.tasks.length }}个</t-descriptions-item>
            <t-descriptions-item label="总工时">{{ totalWorkHours }}小时</t-descriptions-item>
          </t-descriptions>
        </t-tab-panel>
      </t-tabs>
      <template #footer>
        <t-space>
          <t-button theme="default" @click="handleDialogClose">取消</t-button>
          <t-button theme="primary" @click="handleSubmit">确定</t-button>
        </t-space>
      </template>
    </t-dialog>

    <!-- 计划详情弹窗 -->
    <t-dialog
      v-model:visible="detailVisible"
      header="计划详情"
      :width="1000"
      :footer="false"
    >
      <t-tabs v-model="detailTab" theme="card">
        <t-tab-panel value="basic" label="基本信息">
          <t-descriptions :column="2" bordered>
            <t-descriptions-item label="计划编号">{{ detailData.planNo }}</t-descriptions-item>
            <t-descriptions-item label="计划名称">{{ detailData.planName }}</t-descriptions-item>
            <t-descriptions-item label="关联项目">{{ detailData.projectName }}</t-descriptions-item>
            <t-descriptions-item label="计划状态">
              <t-tag v-if="detailData.status === 'draft'" theme="default" variant="light">草稿</t-tag>
              <t-tag v-else-if="detailData.status === 'approved'" theme="success" variant="light">已批准</t-tag>
              <t-tag v-else-if="detailData.status === 'executing'" theme="primary" variant="light">执行中</t-tag>
              <t-tag v-else theme="info" variant="light">已完成</t-tag>
            </t-descriptions-item>
            <t-descriptions-item label="计划周期">{{ detailData.planPeriod }}</t-descriptions-item>
            <t-descriptions-item label="完成进度">{{ detailData.progress }}%</t-descriptions-item>
            <t-descriptions-item label="任务数量">{{ detailData.tasks?.length || 0 }}个</t-descriptions-item>
            <t-descriptions-item label="总工时">{{ detailData.totalWorkHours }}小时</t-descriptions-item>
            <t-descriptions-item label="计划说明" :span="2">{{ detailData.description }}</t-descriptions-item>
          </t-descriptions>
        </t-tab-panel>
        <t-tab-panel value="tasks" label="任务列表">
          <t-table
            :data="detailData.tasks || []"
            :columns="taskColumns"
            :pagination="false"
            row-key="id"
          />
        </t-tab-panel>
        <t-tab-panel value="log" label="操作日志">
          <t-timeline>
            <t-timeline-item v-for="log in detailData.logs || []" :key="log.id" :label="log.time">
              <div>{{ log.content }}</div>
              <div class="log-operator">操作人: {{ log.operator }}</div>
            </t-timeline-item>
          </t-timeline>
        </t-tab-panel>
      </t-tabs>
    </t-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { MessagePlugin, DialogPlugin } from 'tdesign-vue-next'
import { AddIcon, DownloadIcon } from 'tdesign-icons-vue-next'

// 统计数据
const statistics = ref({
  total: 50,
  executing: 20,
  onSchedule: 18,
  tasks: 300
})

// 搜索表单
const searchForm = reactive({
  planNo: '',
  projectName: '',
  status: ''
})

// 表格数据
const loading = ref(false)
const tableData = ref([
  {
    id: 1,
    planNo: 'PLAN-20260219-001',
    planName: '项目A开发计划',
    projectName: '智能制造系统研发',
    planPeriod: ['2026-02-20', '2026-06-30'],
    status: 'executing',
    progress: 45,
    totalWorkHours: 1200,
    description: '项目A的开发计划',
    tasks: [
      { id: 1, taskName: '需求分析', startDate: '2026-02-20', endDate: '2026-03-31', workHours: 160 },
      { id: 2, taskName: '系统设计', startDate: '2026-04-01', endDate: '2026-05-31', workHours: 320 }
    ]
  },
  {
    id: 2,
    planNo: 'PLAN-20260219-002',
    planName: '项目B实施计划',
    projectName: '自动化生产线改造',
    planPeriod: ['2026-03-01', '2026-08-31'],
    status: 'approved',
    progress: 0,
    totalWorkHours: 800,
    description: '项目B的实施计划',
    tasks: []
  },
  {
    id: 3,
    planNo: 'PLAN-20260219-003',
    planName: '项目C测试计划',
    projectName: '产品研发项目A',
    planPeriod: ['2026-04-01', '2026-05-31'],
    status: 'draft',
    progress: 0,
    totalWorkHours: 400,
    description: '项目C的测试计划',
    tasks: []
  }
])

// 表格列定义
const columns = [
  { colKey: 'planNo', title: '计划编号', width: 150 },
  { colKey: 'planName', title: '计划名称', width: 150 },
  { colKey: 'projectName', title: '关联项目', width: 200 },
  { colKey: 'planPeriod', title: '计划周期', width: 200 },
  { colKey: 'totalWorkHours', title: '总工时(h)', width: 120 },
  { colKey: 'progress', title: '完成进度', width: 120, cell: 'progress' },
  { colKey: 'status', title: '计划状态', width: 100, cell: 'status' },
  { colKey: 'action', title: '操作', width: 180, fixed: 'right', cell: 'action' }
]

const taskColumns = [
  { colKey: 'taskName', title: '任务名称', width: 200 },
  { colKey: 'startDate', title: '开始日期', width: 120 },
  { colKey: 'endDate', title: '结束日期', width: 120 },
  { colKey: 'workHours', title: '工时(小时)', width: 120 },
  { colKey: 'action', title: '操作', width: 80, cell: 'action' }
]

// 分页
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 50
})

const rowKey = 'id'

// 新增/编辑弹窗
const dialogVisible = ref(false)
const dialogTitle = computed(() => (formData.id ? '编辑计划' : '新增计划'))
const activeTab = ref('basic')
const formRef = ref()
const formData = reactive({
  id: undefined,
  planNo: '',
  planName: '',
  projectName: '',
  planPeriod: [],
  description: '',
  tasks: []
})

const rules = {
  planNo: [{ required: true, message: '请输入计划编号' }],
  planName: [{ required: true, message: '请输入计划名称' }],
  projectName: [{ required: true, message: '请输入关联项目' }],
  planPeriod: [{ required: true, message: '请选择计划周期' }]
}

// 计算总工时
const totalWorkHours = computed(() => {
  return formData.tasks.reduce((sum, item: any) => sum + (item.workHours || 0), 0)
})

// 计划详情弹窗
const detailVisible = ref(false)
const detailTab = ref('basic')
const detailData = ref<any>({})

// 搜索
const handleSearch = () => {
  loading.value = true
  setTimeout(() => {
    loading.value = false
    MessagePlugin.success('查询成功')
  }, 500)
}

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    planNo: '',
    projectName: '',
    status: ''
  })
  MessagePlugin.success('已重置')
}

// 新增计划
const handleAdd = () => {
  Object.assign(formData, {
    id: undefined,
    planNo: '',
    planName: '',
    projectName: '',
    planPeriod: [],
    description: '',
    tasks: []
  })
  activeTab.value = 'basic'
  dialogVisible.value = true
}

// 编辑计划
const handleEdit = (row: any) => {
  Object.assign(formData, { ...row })
  activeTab.value = 'basic'
  dialogVisible.value = true
}

// 删除计划
const handleDelete = (row: any) => {
  const dialog = DialogPlugin({
    header: '确认删除',
    body: `确认要删除计划"${row.planName}"吗？`,
    confirmBtn: '确定',
    cancelBtn: '取消',
    onConfirm: () => {
      dialog.hide()
      MessagePlugin.success('删除成功')
    }
  })
}

// 导出报表
const handleExport = () => {
  MessagePlugin.success('导出成功')
}

// 分页变化
const handlePageChange = (pageInfo: any) => {
  pagination.current = pageInfo.current
  pagination.pageSize = pageInfo.pageSize
}

// 提交表单
const handleSubmit = async () => {
  const valid = await formRef.value?.validate()
  if (valid === true) {
    dialogVisible.value = false
    MessagePlugin.success(formData.id ? '更新成功' : '新增成功')
  }
}

// 关闭弹窗
const handleDialogClose = () => {
  dialogVisible.value = false
}

// 查看详情
const handleView = (row: any) => {
  detailData.value = {
    ...row,
    logs: [
      { id: 1, time: '2026-02-19 10:00', content: '创建计划', operator: '管理员' },
      { id: 2, time: '2026-02-19 14:00', content: '批准计划', operator: '经理' }
    ]
  }
  detailTab.value = 'basic'
  detailVisible.value = true
}

// 添加任务
const handleAddTask = () => {
  formData.tasks.push({
    id: Date.now(),
    taskName: '',
    startDate: '',
    endDate: '',
    workHours: 0
  })
}

// 删除任务
const handleDeleteTask = (index: number) => {
  formData.tasks.splice(index, 1)
}
</script>

<style scoped lang="less">
.project-planning {
  padding: 24px;

  .statistics-cards {
    margin-bottom: 24px;

    .statistic-content {
      text-align: center;
      padding: 16px 0;

      .statistic-value {
        font-size: 32px;
        font-weight: 600;
        margin-bottom: 8px;
      }

      .statistic-label {
        font-size: 14px;
        color: rgba(0, 0, 0, 0.6);
      }
    }
  }

  .search-card,
  .table-card {
    margin-bottom: 24px;
  }

  .tasks-section {
    .t-button {
      margin-bottom: 16px;
    }
  }

  .log-operator {
    font-size: 12px;
    color: rgba(0, 0, 0, 0.6);
    margin-top: 4px;
  }
}
</style>
