<template>
  <div class="budget-page">
    <!-- 统计卡片 -->
    <t-row :gutter="16" class="stats-row">
      <t-col :span="6">
        <t-card theme="primary1" header-bordered>
          <div class="stat-item">
            <div class="stat-icon">
              <t-icon name="wallet" size="24px" />
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ formatMoney(statistics.totalBudget) }}</div>
              <div class="stat-label">总预算</div>
            </div>
          </div>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="success1" header-bordered>
          <div class="stat-item">
            <div class="stat-icon">
              <t-icon name="check-circle" size="24px" />
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ formatMoney(statistics.usedBudget) }}</div>
              <div class="stat-label">已使用</div>
            </div>
          </div>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="warning1" header-bordered>
          <div class="stat-item">
            <div class="stat-icon">
              <t-icon name="time" size="24px" />
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ formatMoney(statistics.remainingBudget) }}</div>
              <div class="stat-label">剩余预算</div>
            </div>
          </div>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="danger1" header-bordered>
          <div class="stat-item">
            <div class="stat-icon">
              <t-icon name="error-circle" size="24px" />
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ statistics.usageRate }}%</div>
              <div class="stat-label">使用率</div>
            </div>
          </div>
        </t-card>
      </t-col>
    </t-row>

    <!-- 搜索卡片 -->
    <t-card title="搜索筛选" class="search-card">
      <t-form :data="searchForm" layout="inline" @submit="handleSearch" @reset="handleReset">
        <t-form-item label="预算年度" name="year">
          <t-select v-model="searchForm.year" placeholder="请选择预算年度">
            <t-option value="2026" label="2026年" />
            <t-option value="2025" label="2025年" />
            <t-option value="2024" label="2024年" />
          </t-select>
        </t-form-item>
        <t-form-item label="预算类型" name="type">
          <t-select v-model="searchForm.type" placeholder="请选择预算类型" clearable>
            <t-option value="department" label="部门预算" />
            <t-option value="project" label="项目预算" />
            <t-option value="expense" label="费用预算" />
            <t-option value="revenue" label="收入预算" />
          </t-select>
        </t-form-item>
        <t-form-item label="部门" name="department">
          <t-select v-model="searchForm.department" placeholder="请选择部门" clearable>
            <t-option v-for="dept in departments" :key="dept.value" :value="dept.value" :label="dept.label" />
          </t-select>
        </t-form-item>
        <t-form-item label="状态" name="status">
          <t-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <t-option value="draft" label="草稿" />
            <t-option value="submitted" label="已提交" />
            <t-option value="approved" label="已批准" />
            <t-option value="rejected" label="已驳回" />
          </t-select>
        </t-form-item>
        <t-form-item>
          <t-space>
            <t-button theme="primary" type="submit">查询</t-button>
            <t-button theme="default" type="reset">重置</t-button>
          </t-space>
        </t-form-item>
      </t-form>
    </t-card>

    <!-- 操作栏 -->
    <t-card class="action-card">
      <t-space>
        <t-button theme="primary" @click="handleAdd">
          <template #icon><t-icon name="add" /></template>
          新增预算
        </t-button>
        <t-button theme="success" @click="handleBatchApprove">
          <template #icon><t-icon name="check" /></template>
          批量批准
        </t-button>
        <t-button theme="default" @click="handleExport">
          <template #icon><t-icon name="download" /></template>
          导出报表
        </t-button>
      </t-space>
    </t-card>

    <!-- 预算列表 -->
    <t-card>
      <t-table
        :data="budgetList"
        :columns="columns"
        :row-key="rowKey"
        :loading="loading"
        :pagination="pagination"
        @page-change="handlePageChange"
        :selected-row-keys="selectedRowKeys"
        @select-change="handleSelectChange"
        stripe
        hover
      >
        <template #type="{ row }">
          <t-tag v-if="row.type === 'department'" theme="primary">部门预算</t-tag>
          <t-tag v-else-if="row.type === 'project'" theme="success">项目预算</t-tag>
          <t-tag v-else-if="row.type === 'expense'" theme="warning">费用预算</t-tag>
          <t-tag v-else-if="row.type === 'revenue'" theme="info">收入预算</t-tag>
        </template>
        <template #usageRate="{ row }">
          <t-progress :percentage="row.usageRate" :show-info="true" size="small" :theme="getProgressTheme(row.usageRate)" />
        </template>
        <template #status="{ row }">
          <t-tag v-if="row.status === 'draft'" theme="default">草稿</t-tag>
          <t-tag v-else-if="row.status === 'submitted'" theme="warning">已提交</t-tag>
          <t-tag v-else-if="row.status === 'approved'" theme="success">已批准</t-tag>
          <t-tag v-else-if="row.status === 'rejected'" theme="danger">已驳回</t-tag>
        </template>
        <template #action="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleView(row)">查看</t-link>
            <t-link v-if="row.status === 'draft'" theme="primary" @click="handleEdit(row)">编辑</t-link>
            <t-link v-if="row.status === 'submitted'" theme="success" @click="handleApprove(row)">批准</t-link>
            <t-link v-if="row.status === 'submitted'" theme="danger" @click="handleReject(row)">驳回</t-link>
            <t-link theme="danger" @click="handleDelete(row)">删除</t-link>
          </t-space>
        </template>
      </t-table>
    </t-card>

    <!-- 预算编辑弹窗 -->
    <t-dialog
      v-model:visible="editDialogVisible"
      :header="editDialogTitle"
      width="900px"
      :footer="false"
      @close="handleEditClose"
    >
      <t-form ref="editFormRef" :data="editForm" :rules="editRules" label-width="100px" @submit="handleEditSubmit">
        <t-tabs v-model="activeTab">
          <t-tab-panel value="basic" label="基本信息">
            <t-form-item label="预算名称" name="budgetName">
              <t-input v-model="editForm.budgetName" placeholder="请输入预算名称" />
            </t-form-item>
            <t-form-item label="预算编码" name="budgetCode">
              <t-input v-model="editForm.budgetCode" placeholder="请输入预算编码" disabled />
            </t-form-item>
            <t-form-item label="预算年度" name="year">
              <t-select v-model="editForm.year" placeholder="请选择预算年度">
                <t-option value="2026" label="2026年" />
                <t-option value="2025" label="2025年" />
                <t-option value="2024" label="2024年" />
              </t-select>
            </t-form-item>
            <t-form-item label="预算类型" name="type">
              <t-radio-group v-model="editForm.type">
                <t-radio value="department">部门预算</t-radio>
                <t-radio value="project">项目预算</t-radio>
                <t-radio value="expense">费用预算</t-radio>
                <t-radio value="revenue">收入预算</t-radio>
              </t-radio-group>
            </t-form-item>
            <t-form-item label="所属部门" name="department">
              <t-select v-model="editForm.department" placeholder="请选择所属部门">
                <t-option v-for="dept in departments" :key="dept.value" :value="dept.value" :label="dept.label" />
              </t-select>
            </t-form-item>
            <t-form-item label="项目名称" name="project" v-if="editForm.type === 'project'">
              <t-select v-model="editForm.project" placeholder="请选择项目">
                <t-option v-for="proj in projects" :key="proj.value" :value="proj.value" :label="proj.label" />
              </t-select>
            </t-form-item>
            <t-form-item label="预算期间" name="period">
              <t-select v-model="editForm.period" placeholder="请选择预算期间">
                <t-option value="annual" label="年度预算" />
                <t-option value="quarterly" label="季度预算" />
                <t-option value="monthly" label="月度预算" />
              </t-select>
            </t-form-item>
            <t-form-item label="预算金额" name="amount">
              <t-input-number v-model="editForm.amount" :min="0" placeholder="请输入预算金额" />
            </t-form-item>
            <t-form-item label="预算说明" name="description">
              <t-textarea v-model="editForm.description" placeholder="请输入预算说明" :autosize="{ minRows: 4, maxRows: 8 }" />
            </t-form-item>
          </t-tab-panel>

          <t-tab-panel value="details" label="预算明细">
            <t-button theme="primary" @click="handleAddDetail" style="margin-bottom: 16px">
              <template #icon><t-icon name="add" /></template>
              新增明细
            </t-button>
            <t-table :data="editForm.details" :columns="detailColumns" stripe hover>
              <template #amount="{ row }">
                <t-input-number v-model="row.amount" :min="0" size="small" />
              </template>
              <template #action="{ row, rowIndex }">
                <t-link theme="danger" @click="handleRemoveDetail(rowIndex)">删除</t-link>
              </template>
            </t-table>
          </t-tab-panel>

          <t-tab-panel value="summary" label="汇总信息">
            <t-descriptions :column="2" bordered>
              <t-descriptions-item label="预算名称">{{ editForm.budgetName }}</t-descriptions-item>
              <t-descriptions-item label="预算编码">{{ editForm.budgetCode }}</t-descriptions-item>
              <t-descriptions-item label="预算年度">{{ editForm.year }}年</t-descriptions-item>
              <t-descriptions-item label="预算类型">{{ getTypeName(editForm.type) }}</t-descriptions-item>
              <t-descriptions-item label="所属部门">{{ getDepartmentName(editForm.department) }}</t-descriptions-item>
              <t-descriptions-item label="预算期间">{{ getPeriodName(editForm.period) }}</t-descriptions-item>
              <t-descriptions-item label="预算总金额">{{ formatMoney(editForm.amount) }}</t-descriptions-item>
              <t-descriptions-item label="明细总数">{{ editForm.details.length }}</t-descriptions-item>
              <t-descriptions-item label="明细总金额">{{ formatMoney(calculateDetailTotal()) }}</t-descriptions-item>
              <t-descriptions-item label="预算说明">{{ editForm.description }}</t-descriptions-item>
            </t-descriptions>
          </t-tab-panel>
        </t-tabs>
        <t-form-item style="margin-top: 20px">
          <t-space>
            <t-button theme="primary" type="submit">保存</t-button>
            <t-button theme="default" @click="handleEditClose">取消</t-button>
          </t-space>
        </t-form-item>
      </t-form>
    </t-dialog>

    <!-- 预算详情弹窗 -->
    <t-dialog
      v-model:visible="detailDialogVisible"
      header="预算详情"
      width="900px"
      :footer="false"
    >
      <t-tabs>
        <t-tab-panel value="basic" label="基本信息">
          <t-descriptions :column="2" bordered>
            <t-descriptions-item label="预算名称">{{ detailForm.budgetName }}</t-descriptions-item>
            <t-descriptions-item label="预算编码">{{ detailForm.budgetCode }}</t-descriptions-item>
            <t-descriptions-item label="预算年度">{{ detailForm.year }}年</t-descriptions-item>
            <t-descriptions-item label="预算类型">{{ getTypeName(detailForm.type) }}</t-descriptions-item>
            <t-descriptions-item label="所属部门">{{ getDepartmentName(detailForm.department) }}</t-descriptions-item>
            <t-descriptions-item label="预算期间">{{ getPeriodName(detailForm.period) }}</t-descriptions-item>
            <t-descriptions-item label="预算总金额">{{ formatMoney(detailForm.amount) }}</t-descriptions-item>
            <t-descriptions-item label="已使用金额">{{ formatMoney(detailForm.usedAmount) }}</t-descriptions-item>
            <t-descriptions-item label="剩余金额">{{ formatMoney(detailForm.amount - detailForm.usedAmount) }}</t-descriptions-item>
            <t-descriptions-item label="使用率">{{ detailForm.usageRate }}%</t-descriptions-item>
            <t-descriptions-item label="预算说明" :span="2">{{ detailForm.description }}</t-descriptions-item>
          </t-descriptions>
        </t-tab-panel>
        <t-tab-panel value="details" label="预算明细">
          <t-table :data="detailForm.details" :columns="detailDetailColumns" stripe hover>
            <template #usageRate="{ row }">
              <t-progress :percentage="row.usageRate" :show-info="true" size="small" :theme="getProgressTheme(row.usageRate)" />
            </template>
          </t-table>
        </t-tab-panel>
      </t-tabs>
    </t-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { MessagePlugin, DialogPlugin } from 'tdesign-vue-next'

const rowKey = 'id'

const loading = ref(false)
const editDialogVisible = ref(false)
const detailDialogVisible = ref(false)
const editDialogTitle = computed(() => isEdit.value ? '编辑预算' : '新增预算')
const isEdit = ref(false)
const activeTab = ref('basic')

const statistics = reactive({
  totalBudget: 5000000,
  usedBudget: 3200000,
  remainingBudget: 1800000,
  usageRate: 64
})

const searchForm = reactive({
  year: '2026',
  type: '',
  department: '',
  status: ''
})

const editForm = reactive({
  id: '',
  budgetName: '',
  budgetCode: '',
  year: '2026',
  type: 'department',
  department: '',
  project: '',
  period: 'annual',
  amount: 0,
  description: '',
  details: []
})

const editRules = {
  budgetName: [{ required: true, message: '请输入预算名称' }],
  year: [{ required: true, message: '请选择预算年度' }],
  type: [{ required: true, message: '请选择预算类型' }],
  department: [{ required: true, message: '请选择所属部门' }],
  period: [{ required: true, message: '请选择预算期间' }],
  amount: [{ required: true, message: '请输入预算金额' }]
}

const detailForm = reactive({
  ...editForm,
  usedAmount: 0,
  usageRate: 0
})

const departments = ref([
  { value: 'tech', label: '技术部' },
  { value: 'product', label: '产品部' },
  { value: 'marketing', label: '市场部' },
  { value: 'sales', label: '销售部' },
  { value: 'hr', label: '人力资源部' },
  { value: 'finance', label: '财务部' }
])

const projects = ref([
  { value: 'proj1', label: 'ERP系统开发' },
  { value: 'proj2', label: '移动端开发' },
  { value: 'proj3', label: '数据平台建设' }
])

const budgetList = ref<any[]>([])
const selectedRowKeys = ref<string[]>([])

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0
})

const columns = [
  { colKey: 'budgetName', title: '预算名称', width: 180 },
  { colKey: 'budgetCode', title: '预算编码', width: 140 },
  { colKey: 'year', title: '预算年度', width: 90 },
  { colKey: 'type', title: '预算类型', width: 100 },
  { colKey: 'department', title: '所属部门', width: 100, cell: (h, { row }) => getDepartmentName(row.department) },
  { colKey: 'amount', title: '预算金额', width: 120, cell: (h, { row }) => formatMoney(row.amount) },
  { colKey: 'usedAmount', title: '已使用', width: 120, cell: (h, { row }) => formatMoney(row.usedAmount) },
  { colKey: 'usageRate', title: '使用率', width: 150 },
  { colKey: 'status', title: '状态', width: 90 },
  { colKey: 'createTime', title: '创建时间', width: 110 },
  { colKey: 'action', title: '操作', width: 200, fixed: 'right' }
]

const detailColumns = [
  { colKey: 'category', title: '明细分类', width: 150 },
  { colKey: 'itemName', title: '明细项目', width: 150 },
  { colKey: 'amount', title: '预算金额', width: 120 },
  { colKey: 'description', title: '说明', width: 200 },
  { colKey: 'action', title: '操作', width: 80 }
]

const detailDetailColumns = [
  { colKey: 'category', title: '明细分类', width: 150 },
  { colKey: 'itemName', title: '明细项目', width: 150 },
  { colKey: 'budgetAmount', title: '预算金额', width: 120, cell: (h, { row }) => formatMoney(row.budgetAmount) },
  { colKey: 'usedAmount', title: '已使用', width: 120, cell: (h, { row }) => formatMoney(row.usedAmount) },
  { colKey: 'usageRate', title: '使用率', width: 150 },
  { colKey: 'description', title: '说明', width: 200 }
]

onMounted(() => {
  loadData()
})

const loadData = async () => {
  loading.value = true
  setTimeout(() => {
    budgetList.value = [
      {
        id: '1',
        budgetName: '技术部2026年度预算',
        budgetCode: 'YS20260219001',
        year: '2026',
        type: 'department',
        department: 'tech',
        amount: 1500000,
        usedAmount: 950000,
        usageRate: 63.33,
        status: 'approved',
        createTime: '2026-01-15',
        description: '技术部2026年度总预算',
        details: [
          { category: '人力成本', itemName: '薪资福利', budgetAmount: 1000000, usedAmount: 650000, usageRate: 65, description: '员工薪资和福利' },
          { category: '设备采购', itemName: '开发设备', budgetAmount: 300000, usedAmount: 180000, usageRate: 60, description: '开发用电脑和设备' },
          { category: '培训费用', itemName: '技术培训', budgetAmount: 200000, usedAmount: 120000, usageRate: 60, description: '员工技术培训' }
        ]
      },
      {
        id: '2',
        budgetName: '市场部2026年度预算',
        budgetCode: 'YS20260219002',
        year: '2026',
        type: 'department',
        department: 'marketing',
        amount: 800000,
        usedAmount: 520000,
        usageRate: 65,
        status: 'approved',
        createTime: '2026-01-16',
        description: '市场部2026年度总预算',
        details: [
          { category: '推广费用', itemName: '线上推广', budgetAmount: 400000, usedAmount: 280000, usageRate: 70, description: '线上广告投放' },
          { category: '活动费用', itemName: '线下活动', budgetAmount: 250000, usedAmount: 150000, usageRate: 60, description: '线下活动组织' },
          { category: '差旅费用', itemName: '出差报销', budgetAmount: 150000, usedAmount: 90000, usageRate: 60, description: '员工出差费用' }
        ]
      },
      {
        id: '3',
        budgetName: 'ERP系统开发项目预算',
        budgetCode: 'YS20260219003',
        year: '2026',
        type: 'project',
        department: 'tech',
        project: 'proj1',
        amount: 2000000,
        usedAmount: 1200000,
        usageRate: 60,
        status: 'approved',
        createTime: '2026-02-01',
        description: 'ERP系统开发项目总预算',
        details: [
          { category: '人力成本', itemName: '开发人员', budgetAmount: 1200000, usedAmount: 720000, usageRate: 60, description: '开发团队人力成本' },
          { category: '设备费用', itemName: '服务器设备', budgetAmount: 500000, usedAmount: 300000, usageRate: 60, description: '服务器采购费用' },
          { category: '其他费用', itemName: '其他支出', budgetAmount: 300000, usedAmount: 180000, usageRate: 60, description: '项目其他费用' }
        ]
      }
    ]
    pagination.total = 15
    loading.value = false
  }, 500)
}

const handleSearch = () => {
  pagination.current = 1
  loadData()
}

const handleReset = () => {
  Object.assign(searchForm, {
    year: '2026',
    type: '',
    department: '',
    status: ''
  })
  handleSearch()
}

const handleAdd = () => {
  isEdit.value = false
  Object.assign(editForm, {
    id: '',
    budgetName: '',
    budgetCode: `YS${Date.now()}`,
    year: '2026',
    type: 'department',
    department: '',
    project: '',
    period: 'annual',
    amount: 0,
    description: '',
    details: []
  })
  activeTab.value = 'basic'
  editDialogVisible.value = true
}

const handleEdit = (row: any) => {
  isEdit.value = true
  Object.assign(editForm, {
    ...row,
    details: [...row.details]
  })
  activeTab.value = 'basic'
  editDialogVisible.value = true
}

const handleEditClose = () => {
  editDialogVisible.value = false
}

const handleEditSubmit = () => {
  MessagePlugin.success(isEdit.value ? '预算更新成功' : '预算创建成功')
  editDialogVisible.value = false
  loadData()
}

const handleView = (row: any) => {
  Object.assign(detailForm, {
    ...row,
    details: [...row.details]
  })
  detailDialogVisible.value = true
}

const handleApprove = (row: any) => {
  const dialog = DialogPlugin({
    header: '确认批准',
    body: `确定要批准预算"${row.budgetName}"吗？`,
    confirmBtn: {
      theme: 'primary',
      content: '确认批准'
    },
    onConfirm: () => {
      MessagePlugin.success('预算已批准')
      dialog.destroy()
      loadData()
    }
  })
}

const handleReject = (row: any) => {
  const dialog = DialogPlugin({
    header: '确认驳回',
    body: `确定要驳回预算"${row.budgetName}"吗？`,
    confirmBtn: {
      theme: 'danger',
      content: '确认驳回'
    },
    onConfirm: () => {
      MessagePlugin.success('预算已驳回')
      dialog.destroy()
      loadData()
    }
  })
}

const handleDelete = (row: any) => {
  const dialog = DialogPlugin({
    header: '确认删除',
    body: `确定要删除预算"${row.budgetName}"吗？`,
    confirmBtn: {
      theme: 'danger',
      content: '确认删除'
    },
    onConfirm: () => {
      MessagePlugin.success('删除成功')
      dialog.destroy()
      loadData()
    }
  })
}

const handleBatchApprove = () => {
  if (selectedRowKeys.value.length === 0) {
    MessagePlugin.warning('请先选择要批准的预算')
    return
  }
  const dialog = DialogPlugin({
    header: '批量批准',
    body: `确定要批准选中的 ${selectedRowKeys.value.length} 个预算吗？`,
    confirmBtn: {
      theme: 'primary',
      content: '确认批准'
    },
    onConfirm: () => {
      MessagePlugin.success('批量批准成功')
      dialog.destroy()
      loadData()
    }
  })
}

const handleExport = () => {
  MessagePlugin.success('报表导出成功')
}

const handlePageChange = (pageInfo: any) => {
  pagination.current = pageInfo.current
  pagination.pageSize = pageInfo.pageSize
  loadData()
}

const handleSelectChange = (value: string[]) => {
  selectedRowKeys.value = value
}

const handleAddDetail = () => {
  editForm.details.push({
    category: '',
    itemName: '',
    amount: 0,
    description: ''
  })
}

const handleRemoveDetail = (index: number) => {
  editForm.details.splice(index, 1)
}

const calculateDetailTotal = () => {
  return editForm.details.reduce((sum, item) => sum + (item.amount || 0), 0)
}

const getProgressTheme = (percentage: number) => {
  if (percentage >= 90) return 'danger'
  if (percentage >= 70) return 'warning'
  return 'success'
}

const getTypeName = (value: string) => {
  const map = {
    department: '部门预算',
    project: '项目预算',
    expense: '费用预算',
    revenue: '收入预算'
  }
  return map[value] || value
}

const getDepartmentName = (value: string) => {
  const dept = departments.value.find(d => d.value === value)
  return dept ? dept.label : value
}

const getPeriodName = (value: string) => {
  const map = {
    annual: '年度预算',
    quarterly: '季度预算',
    monthly: '月度预算'
  }
  return map[value] || value
}

const formatMoney = (amount: number) => {
  return '¥' + amount.toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}
</script>

<style scoped lang="scss">
.budget-page {
  padding: 20px;

  .stats-row {
    margin-bottom: 16px;
  }

  .stat-item {
    display: flex;
    align-items: center;
    gap: 16px;

    .stat-icon {
      width: 48px;
      height: 48px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      background: rgba(255, 255, 255, 0.2);
      color: white;
    }

    .stat-content {
      flex: 1;

      .stat-value {
        font-size: 24px;
        font-weight: bold;
        color: #333;
        line-height: 1.2;
      }

      .stat-label {
        font-size: 14px;
        color: #666;
        margin-top: 4px;
      }
    }
  }

  .search-card,
  .action-card {
    margin-bottom: 16px;
  }
}
</style>
