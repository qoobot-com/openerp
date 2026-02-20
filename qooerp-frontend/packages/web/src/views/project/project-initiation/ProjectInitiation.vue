<template>
  <div class="project-initiation">
    <!-- 统计卡片 -->
    <t-row :gutter="[16, 16]" class="statistics-cards">
      <t-col :span="3">
        <t-card theme="primary1" :header-bordered="false">
          <div class="statistic-content">
            <div class="statistic-value">{{ statistics.total }}</div>
            <div class="statistic-label">项目总数</div>
          </div>
        </t-card>
      </t-col>
      <t-col :span="3">
        <t-card theme="success" :header-bordered="false">
          <div class="statistic-content">
            <div class="statistic-value">{{ statistics.approved }}</div>
            <div class="statistic-label">已立项</div>
          </div>
        </t-card>
      </t-col>
      <t-col :span="3">
        <t-card theme="warning" :header-bordered="false">
          <div class="statistic-content">
            <div class="statistic-value">{{ statistics.pending }}</div>
            <div class="statistic-label">待审批</div>
          </div>
        </t-card>
      </t-col>
      <t-col :span="3">
        <t-card theme="info" :header-bordered="false">
          <div class="statistic-content">
            <div class="statistic-value">{{ statistics.totalBudget }}</div>
            <div class="statistic-label">总预算(万)</div>
          </div>
        </t-card>
      </t-col>
    </t-row>

    <!-- 搜索卡片 -->
    <t-card title="项目查询" class="search-card">
      <t-form :data="searchForm" layout="inline" @submit="handleSearch">
        <t-form-item label="项目编号" name="projectNo">
          <t-input v-model="searchForm.projectNo" placeholder="请输入项目编号" clearable />
        </t-form-item>
        <t-form-item label="项目名称" name="projectName">
          <t-input v-model="searchForm.projectName" placeholder="请输入项目名称" clearable />
        </t-form-item>
        <t-form-item label="项目类型" name="projectType">
          <t-select v-model="searchForm.projectType" placeholder="请选择项目类型" clearable>
            <t-option value="product" label="产品研发" />
            <t-option value="software" label="软件开发" />
            <t-option value="engineering" label="工程项目" />
            <t-option value="service" label="服务项目" />
          </t-select>
        </t-form-item>
        <t-form-item label="立项状态" name="status">
          <t-select v-model="searchForm.status" placeholder="请选择立项状态" clearable>
            <t-option value="draft" label="草稿" />
            <t-option value="pending" label="待审批" />
            <t-option value="approved" label="已立项" />
            <t-option value="rejected" label="已驳回" />
          </t-select>
        </t-form-item>
        <t-form-item label="立项日期" name="initiationDate">
          <t-date-range-picker v-model="searchForm.initiationDate" placeholder="请选择立项日期" />
        </t-form-item>
        <t-form-item>
          <t-space>
            <t-button theme="primary" type="submit">查询</t-button>
            <t-button theme="default" @click="handleReset">重置</t-button>
          </t-space>
        </t-form-item>
      </t-form>
    </t-card>

    <!-- 项目列表 -->
    <t-card title="项目列表" class="table-card">
      <template #actions>
        <t-space>
          <t-button theme="primary" @click="handleAdd">
            <template #icon><add-icon /></template>
            新增项目
          </t-button>
          <t-button theme="success" @click="handleBatchApprove">
            <template #icon><check-icon /></template>
            批量审批
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
        :selected-row-keys="selectedRowKeys"
        :loading="loading"
        :pagination="pagination"
        @select-change="handleSelectChange"
        @page-change="handlePageChange"
      >
        <template #status="{ row }">
          <t-tag v-if="row.status === 'draft'" theme="default" variant="light">草稿</t-tag>
          <t-tag v-else-if="row.status === 'pending'" theme="warning" variant="light">待审批</t-tag>
          <t-tag v-else-if="row.status === 'approved'" theme="success" variant="light">已立项</t-tag>
          <t-tag v-else theme="error" variant="light">已驳回</t-tag>
        </template>
        <template #projectType="{ row }">
          <t-tag v-if="row.projectType === 'product'" theme="primary" variant="light">产品研发</t-tag>
          <t-tag v-else-if="row.projectType === 'software'" theme="success" variant="light">软件开发</t-tag>
          <t-tag v-else-if="row.projectType === 'engineering'" theme="warning" variant="light">工程项目</t-tag>
          <t-tag v-else theme="info" variant="light">服务项目</t-tag>
        </template>
        <template #action="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleView(row)">查看</t-link>
            <t-link theme="primary" @click="handleEdit(row)">编辑</t-link>
            <t-link v-if="row.status === 'pending'" theme="success" @click="handleApprove(row)">审批</t-link>
            <t-link theme="danger" @click="handleDelete(row)">删除</t-link>
          </t-space>
        </template>
      </t-table>
    </t-card>

    <!-- 新增/编辑项目弹窗 -->
    <t-dialog
      v-model:visible="dialogVisible"
      :header="dialogTitle"
      :width="1000"
      :confirm-btn="null"
      :cancel-btn="null"
      @close="handleDialogClose"
    >
      <t-tabs v-model="activeTab" theme="card">
        <t-tab-panel value="basic" label="基本信息">
          <t-form :data="formData" ref="formRef" :rules="rules" label-width="120px">
            <t-form-item label="项目编号" name="projectNo">
              <t-input v-model="formData.projectNo" placeholder="请输入项目编号" />
            </t-form-item>
            <t-form-item label="项目名称" name="projectName">
              <t-input v-model="formData.projectName" placeholder="请输入项目名称" />
            </t-form-item>
            <t-form-item label="项目类型" name="projectType">
              <t-select v-model="formData.projectType" placeholder="请选择项目类型">
                <t-option value="product" label="产品研发" />
                <t-option value="software" label="软件开发" />
                <t-option value="engineering" label="工程项目" />
                <t-option value="service" label="服务项目" />
              </t-select>
            </t-form-item>
            <t-form-item label="客户名称" name="customerName">
              <t-input v-model="formData.customerName" placeholder="请输入客户名称" />
            </t-form-item>
            <t-form-item label="项目负责人" name="projectManager">
              <t-select v-model="formData.projectManager" placeholder="请选择项目负责人" filterable>
                <t-option value="1" label="张三" />
                <t-option value="2" label="李四" />
                <t-option value="3" label="王五" />
              </t-select>
            </t-form-item>
            <t-form-item label="项目预算" name="budget">
              <t-input-number v-model="formData.budget" :min="0" placeholder="请输入项目预算" />
            </t-form-item>
            <t-form-item label="计划开始日期" name="startDate">
              <t-date-picker v-model="formData.startDate" placeholder="请选择计划开始日期" />
            </t-form-item>
            <t-form-item label="计划结束日期" name="endDate">
              <t-date-picker v-model="formData.endDate" placeholder="请选择计划结束日期" />
            </t-form-item>
            <t-form-item label="项目描述" name="description">
              <t-textarea v-model="formData.description" placeholder="请输入项目描述" :maxlength="1000" />
            </t-form-item>
          </t-form>
        </t-tab-panel>
        <t-tab-panel value="team" label="项目团队">
          <div class="team-section">
            <t-button theme="primary" size="small" @click="handleAddMember">
              <template #icon><add-icon /></template>
              添加成员
            </t-button>
            <t-table
              :data="formData.members"
              :columns="memberColumns"
              :pagination="false"
              row-key="id"
            >
              <template #action="{ row, rowIndex }">
                <t-link theme="danger" @click="handleDeleteMember(rowIndex)">删除</t-link>
              </template>
            </t-table>
          </div>
        </t-tab-panel>
        <t-tab-panel value="milestone" label="项目里程碑">
          <div class="milestone-section">
            <t-button theme="primary" size="small" @click="handleAddMilestone">
              <template #icon><add-icon /></template>
              添加里程碑
            </t-button>
            <t-table
              :data="formData.milestones"
              :columns="milestoneColumns"
              :pagination="false"
              row-key="id"
            >
              <template #action="{ row, rowIndex }">
                <t-link theme="danger" @click="handleDeleteMilestone(rowIndex)">删除</t-link>
              </template>
            </t-table>
          </div>
        </t-tab-panel>
        <t-tab-panel value="summary" label="汇总信息">
          <t-descriptions :column="2" bordered>
            <t-descriptions-item label="项目编号">{{ formData.projectNo }}</t-descriptions-item>
            <t-descriptions-item label="项目名称">{{ formData.projectName }}</t-descriptions-item>
            <t-descriptions-item label="项目类型">
              <t-tag v-if="formData.projectType === 'product'" theme="primary" variant="light">产品研发</t-tag>
              <t-tag v-else-if="formData.projectType === 'software'" theme="success" variant="light">软件开发</t-tag>
              <t-tag v-else-if="formData.projectType === 'engineering'" theme="warning" variant="light">工程项目</t-tag>
              <t-tag v-else theme="info" variant="light">服务项目</t-tag>
            </t-descriptions-item>
            <t-descriptions-item label="客户名称">{{ formData.customerName }}</t-descriptions-item>
            <t-descriptions-item label="项目负责人">{{ formData.projectManager }}</t-descriptions-item>
            <t-descriptions-item label="项目预算">¥{{ formData.budget }}</t-descriptions-item>
            <t-descriptions-item label="计划开始日期">{{ formData.startDate }}</t-descriptions-item>
            <t-descriptions-item label="计划结束日期">{{ formData.endDate }}</t-descriptions-item>
            <t-descriptions-item label="团队成员" :span="2">{{ formData.members.length }}人</t-descriptions-item>
            <t-descriptions-item label="里程碑" :span="2">{{ formData.milestones.length }}个</t-descriptions-item>
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

    <!-- 项目详情弹窗 -->
    <t-dialog
      v-model:visible="detailVisible"
      header="项目详情"
      :width="1200"
      :footer="false"
    >
      <t-tabs v-model="detailTab" theme="card">
        <t-tab-panel value="basic" label="基本信息">
          <t-descriptions :column="2" bordered>
            <t-descriptions-item label="项目编号">{{ detailData.projectNo }}</t-descriptions-item>
            <t-descriptions-item label="项目名称">{{ detailData.projectName }}</t-descriptions-item>
            <t-descriptions-item label="项目类型">
              <t-tag v-if="detailData.projectType === 'product'" theme="primary" variant="light">产品研发</t-tag>
              <t-tag v-else-if="detailData.projectType === 'software'" theme="success" variant="light">软件开发</t-tag>
              <t-tag v-else-if="detailData.projectType === 'engineering'" theme="warning" variant="light">工程项目</t-tag>
              <t-tag v-else theme="info" variant="light">服务项目</t-tag>
            </t-descriptions-item>
            <t-descriptions-item label="立项状态">
              <t-tag v-if="detailData.status === 'draft'" theme="default" variant="light">草稿</t-tag>
              <t-tag v-else-if="detailData.status === 'pending'" theme="warning" variant="light">待审批</t-tag>
              <t-tag v-else-if="detailData.status === 'approved'" theme="success" variant="light">已立项</t-tag>
              <t-tag v-else theme="error" variant="light">已驳回</t-tag>
            </t-descriptions-item>
            <t-descriptions-item label="客户名称">{{ detailData.customerName }}</t-descriptions-item>
            <t-descriptions-item label="项目负责人">{{ detailData.projectManager }}</t-descriptions-item>
            <t-descriptions-item label="项目预算">¥{{ detailData.budget }}</t-descriptions-item>
            <t-descriptions-item label="审批人">{{ detailData.approver }}</t-descriptions-item>
            <t-descriptions-item label="审批时间">{{ detailData.approveTime }}</t-descriptions-item>
            <t-descriptions-item label="项目描述" :span="2">{{ detailData.description }}</t-descriptions-item>
          </t-descriptions>
        </t-tab-panel>
        <t-tab-panel value="team" label="项目团队">
          <t-table
            :data="detailData.members || []"
            :columns="memberColumns"
            :pagination="false"
            row-key="id"
          />
        </t-tab-panel>
        <t-tab-panel value="milestone" label="项目里程碑">
          <t-table
            :data="detailData.milestones || []"
            :columns="milestoneColumns"
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
import { AddIcon, CheckIcon, DownloadIcon } from 'tdesign-icons-vue-next'

// 统计数据
const statistics = ref({
  total: 30,
  approved: 20,
  pending: 10,
  totalBudget: 500
})

// 搜索表单
const searchForm = reactive({
  projectNo: '',
  projectName: '',
  projectType: '',
  status: '',
  initiationDate: []
})

// 表格数据
const loading = ref(false)
const tableData = ref([
  {
    id: 1,
    projectNo: 'PRJ-20260219-001',
    projectName: '智能制造系统研发',
    projectType: 'software',
    customerName: 'ABC公司',
    projectManager: '1',
    budget: 1000000,
    startDate: '2026-02-20',
    endDate: '2026-12-31',
    status: 'approved',
    approver: '经理',
    approveTime: '2026-02-19 15:00',
    description: '智能制造系统开发项目',
    members: [
      { id: 1, memberName: '张三', role: '项目经理', phone: '13800138001' },
      { id: 2, memberName: '李四', role: '开发工程师', phone: '13800138002' }
    ],
    milestones: [
      { id: 1, milestoneName: '需求分析', targetDate: '2026-03-31' },
      { id: 2, milestoneName: '系统设计', targetDate: '2026-05-31' }
    ]
  },
  {
    id: 2,
    projectNo: 'PRJ-20260219-002',
    projectName: '自动化生产线改造',
    projectType: 'engineering',
    customerName: 'XYZ公司',
    projectManager: '2',
    budget: 800000,
    startDate: '2026-03-01',
    endDate: '2026-08-31',
    status: 'pending',
    approver: '',
    approveTime: '',
    description: '生产线自动化改造项目',
    members: [
      { id: 1, memberName: '王五', role: '项目经理', phone: '13800138003' }
    ],
    milestones: []
  },
  {
    id: 3,
    projectNo: 'PRJ-20260219-003',
    projectName: '产品研发项目A',
    projectType: 'product',
    customerName: '内部项目',
    projectManager: '3',
    budget: 500000,
    startDate: '2026-04-01',
    endDate: '2026-09-30',
    status: 'draft',
    approver: '',
    approveTime: '',
    description: '新产品研发项目',
    members: [],
    milestones: []
  }
])

// 表格列定义
const columns = [
  { colKey: 'projectNo', title: '项目编号', width: 150 },
  { colKey: 'projectName', title: '项目名称', width: 200 },
  { colKey: 'projectType', title: '项目类型', width: 100, cell: 'projectType' },
  { colKey: 'customerName', title: '客户名称', width: 150 },
  { colKey: 'projectManager', title: '项目负责人', width: 100 },
  { colKey: 'budget', title: '项目预算', width: 120 },
  { colKey: 'startDate', title: '计划开始', width: 120 },
  { colKey: 'endDate', title: '计划结束', width: 120 },
  { colKey: 'status', title: '立项状态', width: 100, cell: 'status' },
  { colKey: 'action', title: '操作', width: 200, fixed: 'right', cell: 'action' }
]

const memberColumns = [
  { colKey: 'memberName', title: '成员姓名', width: 120 },
  { colKey: 'role', title: '角色', width: 120 },
  { colKey: 'phone', title: '联系电话', width: 150 },
  { colKey: 'action', title: '操作', width: 80, cell: 'action' }
]

const milestoneColumns = [
  { colKey: 'milestoneName', title: '里程碑名称', width: 200 },
  { colKey: 'targetDate', title: '目标日期', width: 120 },
  { colKey: 'action', title: '操作', width: 80, cell: 'action' }
]

// 分页
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 30
})

const rowKey = 'id'
const selectedRowKeys = ref<number[]>([])

// 新增/编辑弹窗
const dialogVisible = ref(false)
const dialogTitle = computed(() => (formData.id ? '编辑项目' : '新增项目'))
const activeTab = ref('basic')
const formRef = ref()
const formData = reactive({
  id: undefined,
  projectNo: '',
  projectName: '',
  projectType: '',
  customerName: '',
  projectManager: '',
  budget: 0,
  startDate: '',
  endDate: '',
  description: '',
  members: [],
  milestones: []
})

const rules = {
  projectNo: [{ required: true, message: '请输入项目编号' }],
  projectName: [{ required: true, message: '请输入项目名称' }],
  projectType: [{ required: true, message: '请选择项目类型' }],
  customerName: [{ required: true, message: '请输入客户名称' }],
  projectManager: [{ required: true, message: '请选择项目负责人' }],
  budget: [{ required: true, message: '请输入项目预算' }],
  startDate: [{ required: true, message: '请选择计划开始日期' }],
  endDate: [{ required: true, message: '请选择计划结束日期' }]
}

// 项目详情弹窗
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
    projectNo: '',
    projectName: '',
    projectType: '',
    status: '',
    initiationDate: []
  })
  MessagePlugin.success('已重置')
}

// 新增项目
const handleAdd = () => {
  Object.assign(formData, {
    id: undefined,
    projectNo: '',
    projectName: '',
    projectType: '',
    customerName: '',
    projectManager: '',
    budget: 0,
    startDate: '',
    endDate: '',
    description: '',
    members: [],
    milestones: []
  })
  activeTab.value = 'basic'
  dialogVisible.value = true
}

// 编辑项目
const handleEdit = (row: any) => {
  Object.assign(formData, { ...row })
  activeTab.value = 'basic'
  dialogVisible.value = true
}

// 审批项目
const handleApprove = (row: any) => {
  const dialog = DialogPlugin({
    header: '审批项目',
    body: `确认要立项"${row.projectName}"吗？`,
    confirmBtn: '确定',
    cancelBtn: '取消',
    onConfirm: () => {
      dialog.hide()
      MessagePlugin.success('项目已立项')
    }
  })
}

// 删除项目
const handleDelete = (row: any) => {
  const dialog = DialogPlugin({
    header: '确认删除',
    body: `确认要删除项目"${row.projectName}"吗？`,
    confirmBtn: '确定',
    cancelBtn: '取消',
    onConfirm: () => {
      dialog.hide()
      MessagePlugin.success('删除成功')
    }
  })
}

// 批量审批
const handleBatchApprove = () => {
  if (selectedRowKeys.value.length === 0) {
    MessagePlugin.warning('请先选择项目')
    return
  }
  MessagePlugin.success(`已批量审批${selectedRowKeys.value.length}个项目`)
}

// 导出报表
const handleExport = () => {
  MessagePlugin.success('导出成功')
}

// 选择行
const handleSelectChange = (value: number[]) => {
  selectedRowKeys.value = value
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
      { id: 1, time: '2026-02-19 10:00', content: '创建项目', operator: '管理员' },
      { id: 2, time: '2026-02-19 15:00', content: '项目立项', operator: '经理' }
    ]
  }
  detailTab.value = 'basic'
  detailVisible.value = true
}

// 添加成员
const handleAddMember = () => {
  formData.members.push({
    id: Date.now(),
    memberName: '',
    role: '',
    phone: ''
  })
}

// 删除成员
const handleDeleteMember = (index: number) => {
  formData.members.splice(index, 1)
}

// 添加里程碑
const handleAddMilestone = () => {
  formData.milestones.push({
    id: Date.now(),
    milestoneName: '',
    targetDate: ''
  })
}

// 删除里程碑
const handleDeleteMilestone = (index: number) => {
  formData.milestones.splice(index, 1)
}
</script>

<style scoped lang="less">
.project-initiation {
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

  .team-section,
  .milestone-section {
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
