<template>
  <div class="purchase-request-container">
    <!-- 统计卡片 -->
    <t-card class="stats-card">
      <t-row :gutter="16">
        <t-col :span="6">
          <div class="stat-item">
            <div class="stat-icon">
              <t-icon name="file-paste" size="24px" />
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stats.totalCount }}</div>
              <div class="stat-label">申请单数</div>
            </div>
          </div>
        </t-col>
        <t-col :span="6">
          <div class="stat-item">
            <div class="stat-icon stat-icon-warning">
              <t-icon name="time" size="24px" />
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stats.pendingCount }}</div>
              <div class="stat-label">待审核</div>
            </div>
          </div>
        </t-col>
        <t-col :span="6">
          <div class="stat-item">
            <div class="stat-icon stat-icon-success">
              <t-icon name="check-circle" size="24px" />
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stats.approvedCount }}</div>
              <div class="stat-label">已批准</div>
            </div>
          </div>
        </t-col>
        <t-col :span="6">
          <div class="stat-item">
            <div class="stat-icon stat-icon-info">
              <t-icon name="money-circle" size="24px" />
            </div>
            <div class="stat-content">
              <div class="stat-value">¥{{ formatMoney(stats.totalAmount) }}</div>
              <div class="stat-label">申请金额</div>
            </div>
          </div>
        </t-col>
      </t-row>
    </t-card>

    <!-- 搜索卡片 -->
    <t-card class="search-card">
      <t-form :data="searchForm" layout="inline" @submit="handleSearch" @reset="handleReset">
        <t-form-item label="申请单号" name="requestNo">
          <t-input v-model="searchForm.requestNo" placeholder="请输入申请单号" clearable />
        </t-form-item>
        <t-form-item label="申请部门" name="departmentId">
          <t-select v-model="searchForm.departmentId" placeholder="请选择部门" clearable>
            <t-option value="1" label="部门A" />
            <t-option value="2" label="部门B" />
          </t-select>
        </t-form-item>
        <t-form-item label="申请类型" name="requestType">
          <t-select v-model="searchForm.requestType" placeholder="请选择类型" clearable>
            <t-option value="1" label="商品采购" />
            <t-option value="2" label="设备采购" />
            <t-option value="3" label="物料采购" />
          </t-select>
        </t-form-item>
        <t-form-item label="审批状态" name="status">
          <t-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <t-option value="0" label="待审批" />
            <t-option value="1" label="审批中" />
            <t-option value="2" label="已批准" />
            <t-option value="3" label="已驳回" />
            <t-option value="4" label="已转订单" />
          </t-select>
        </t-form-item>
        <t-form-item label="申请日期" name="requestDateRange">
          <t-date-range-picker v-model="searchForm.requestDateRange" clearable />
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
    <div class="action-bar">
      <t-space>
        <t-button theme="primary" @click="handleAdd">
          <template #icon><t-icon name="add" /></template>
          新增申请
        </t-button>
        <t-button theme="default" @click="handleBatchApprove">
          <template #icon><t-icon name="check" /></template>
          批量审批
        </t-button>
        <t-button theme="default" @click="handleBatchToOrder">
          <template #icon><t-icon name="file-list" /></template>
          批量转订单
        </t-button>
        <t-button theme="default" @click="handleExport">
          <template #icon><t-icon name="download" /></template>
          导出报表
        </t-button>
      </t-space>
    </div>

    <!-- 申请列表表格 -->
    <t-card class="table-card">
      <t-table
        :data="tableData"
        :columns="columns"
        :loading="loading"
        :pagination="pagination"
        :selected-row-keys="selectedRowKeys"
        row-key="id"
        @page-change="handlePageChange"
        @select-change="handleSelectChange"
      >
        <template #status="{ row }">
          <t-tag v-if="row.status === 0" theme="warning">待审批</t-tag>
          <t-tag v-else-if="row.status === 1" theme="primary">审批中</t-tag>
          <t-tag v-else-if="row.status === 2" theme="success">已批准</t-tag>
          <t-tag v-else-if="row.status === 3" theme="danger">已驳回</t-tag>
          <t-tag v-else-if="row.status === 4" theme="success">已转订单</t-tag>
        </template>

        <template #priority="{ row }">
          <t-tag v-if="row.priority === 1" theme="danger">紧急</t-tag>
          <t-tag v-else-if="row.priority === 2" theme="warning">重要</t-tag>
          <t-tag v-else theme="default">普通</t-tag>
        </template>

        <template #totalAmount="{ row }">
          <span class="amount-text">¥{{ formatMoney(row.totalAmount) }}</span>
        </template>

        <template #action="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleView(row)">查看</t-link>
            <t-link v-if="row.status === 0" theme="primary" @click="handleApprove(row)">审批</t-link>
            <t-link v-if="row.status === 0" theme="danger" @click="handleReject(row)">驳回</t-link>
            <t-link v-if="row.status === 2" theme="primary" @click="handleToOrder(row)">转订单</t-link>
            <t-link v-if="row.status === 0" theme="primary" @click="handleEdit(row)">编辑</t-link>
            <t-link v-if="row.status === 0 || row.status === 3" theme="danger" @click="handleDelete(row)">删除</t-link>
          </t-space>
        </template>
      </t-table>
    </t-card>

    <!-- 申请单编辑弹窗 -->
    <t-dialog
      v-model:visible="editDialogVisible"
      :header="isEdit ? '编辑申请单' : '新增申请单'"
      :footer="false"
      width="800px"
    >
      <t-tabs v-model="activeTab">
        <t-tab-panel value="basic" label="基本信息">
          <t-form :data="editForm" :label-width="100">
            <t-form-item label="申请单号" name="requestNo">
              <t-input v-model="editForm.requestNo" placeholder="自动生成" disabled />
            </t-form-item>
            <t-form-item label="申请部门" name="departmentId" required>
              <t-select v-model="editForm.departmentId" placeholder="请选择部门">
                <t-option value="1" label="部门A" />
                <t-option value="2" label="部门B" />
              </t-select>
            </t-form-item>
            <t-form-item label="申请人" name="applicant" required>
              <t-input v-model="editForm.applicant" placeholder="请输入申请人" />
            </t-form-item>
            <t-form-item label="申请类型" name="requestType" required>
              <t-select v-model="editForm.requestType" placeholder="请选择类型">
                <t-option value="1" label="商品采购" />
                <t-option value="2" label="设备采购" />
                <t-option value="3" label="物料采购" />
              </t-select>
            </t-form-item>
            <t-form-item label="优先级" name="priority" required>
              <t-radio-group v-model="editForm.priority">
                <t-radio :value="1">紧急</t-radio>
                <t-radio :value="2">重要</t-radio>
                <t-radio :value="3">普通</t-radio>
              </t-radio-group>
            </t-form-item>
            <t-form-item label="期望到货日期" name="expectedDate" required>
              <t-date-picker v-model="editForm.expectedDate" placeholder="请选择期望到货日期" />
            </t-form-item>
            <t-form-item label="申请说明" name="remark" required>
              <t-textarea v-model="editForm.remark" placeholder="请输入申请说明" :maxlength="500" />
            </t-form-item>
          </t-form>
        </t-tab-panel>

        <t-tab-panel value="detail" label="申请明细">
          <div class="detail-actions">
            <t-button size="small" theme="primary" @click="handleAddDetail">
              <template #icon><t-icon name="add" /></template>
              添加商品
            </t-button>
          </div>
          <t-table :data="editForm.details" :columns="detailColumns" :bordered="true" size="small">
            <template #quantity="{ row, rowIndex }">
              <t-input-number v-model="row.quantity" :min="1" size="small" />
            </template>
            <template #estimatedPrice="{ row, rowIndex }">
              <t-input-number v-model="row.estimatedPrice" :min="0" :decimal-places="2" size="small" />
            </template>
            <template #action="{ row, rowIndex }">
              <t-link theme="danger" @click="handleRemoveDetail(rowIndex)">删除</t-link>
            </template>
          </t-table>
        </t-tab-panel>

        <t-tab-panel value="summary" label="汇总信息">
          <t-descriptions :column="2" bordered>
            <t-descriptions-item label="申请单号">{{ editForm.requestNo || '-' }}</t-descriptions-item>
            <t-descriptions-item label="申请部门">{{ getDepartmentName(editForm.departmentId) }}</t-descriptions-item>
            <t-descriptions-item label="申请人">{{ editForm.applicant }}</t-descriptions-item>
            <t-descriptions-item label="申请类型">{{ getRequestTypeName(editForm.requestType) }}</t-descriptions-item>
            <t-descriptions-item label="优先级">{{ getPriorityName(editForm.priority) }}</t-descriptions-item>
            <t-descriptions-item label="期望到货日期">{{ editForm.expectedDate || '-' }}</t-descriptions-item>
            <t-descriptions-item label="商品种类">{{ editForm.details.length }}</t-descriptions-item>
            <t-descriptions-item label="申请数量">{{ getTotalQuantity() }}</t-descriptions-item>
            <t-descriptions-item label="预估金额">¥{{ formatMoney(getTotalAmount()) }}</t-descriptions-item>
            <t-descriptions-item label="申请说明" :span="2">{{ editForm.remark }}</t-descriptions-item>
          </t-descriptions>
        </t-tab-panel>
      </t-tabs>

      <div class="dialog-footer">
        <t-space>
          <t-button theme="default" @click="editDialogVisible = false">取消</t-button>
          <t-button theme="primary" @click="handleSave">保存</t-button>
          <t-button v-if="!isEdit" theme="success" @click="handleSaveAndSubmit">保存并提交</t-button>
        </t-space>
      </div>
    </t-dialog>

    <!-- 申请单详情弹窗 -->
    <t-dialog v-model:visible="detailDialogVisible" header="申请单详情" :footer="false" width="900px">
      <t-tabs v-model="detailTab">
        <t-tab-panel value="info" label="基本信息">
          <t-descriptions :column="2" bordered>
            <t-descriptions-item label="申请单号">{{ currentDetail.requestNo }}</t-descriptions-item>
            <t-descriptions-item label="状态">
              <t-tag v-if="currentDetail.status === 0" theme="warning">待审批</t-tag>
              <t-tag v-else-if="currentDetail.status === 1" theme="primary">审批中</t-tag>
              <t-tag v-else-if="currentDetail.status === 2" theme="success">已批准</t-tag>
              <t-tag v-else-if="currentDetail.status === 3" theme="danger">已驳回</t-tag>
              <t-tag v-else-if="currentDetail.status === 4" theme="success">已转订单</t-tag>
            </t-descriptions-item>
            <t-descriptions-item label="申请部门">{{ currentDetail.departmentName }}</t-descriptions-item>
            <t-descriptions-item label="申请人">{{ currentDetail.applicant }}</t-descriptions-item>
            <t-descriptions-item label="申请类型">{{ currentDetail.requestTypeName }}</t-descriptions-item>
            <t-descriptions-item label="优先级">
              <t-tag v-if="currentDetail.priority === 1" theme="danger">紧急</t-tag>
              <t-tag v-else-if="currentDetail.priority === 2" theme="warning">重要</t-tag>
              <t-tag v-else theme="default">普通</t-tag>
            </t-descriptions-item>
            <t-descriptions-item label="期望到货日期">{{ currentDetail.expectedDate }}</t-descriptions-item>
            <t-descriptions-item label="商品种类">{{ currentDetail.totalVarieties }}</t-descriptions-item>
            <t-descriptions-item label="申请数量">{{ currentDetail.totalQuantity }}</t-descriptions-item>
            <t-descriptions-item label="预估金额">¥{{ formatMoney(currentDetail.totalAmount) }}</t-descriptions-item>
            <t-descriptions-item label="申请说明" :span="2">{{ currentDetail.remark }}</t-descriptions-item>
            <t-descriptions-item label="创建时间">{{ currentDetail.createTime }}</t-descriptions-item>
            <t-descriptions-item label="审批人">{{ currentDetail.approver || '-' }}</t-descriptions-item>
          </t-descriptions>
        </t-tab-panel>

        <t-tab-panel value="details" label="申请明细">
          <t-table :data="currentDetail.details" :columns="viewDetailColumns" :bordered="true" size="small">
            <template #estimatedPrice="{ row }">
              <span class="amount-text">¥{{ formatMoney(row.estimatedPrice) }}</span>
            </template>
          </t-table>
        </t-tab-panel>

        <t-tab-panel value="approval" label="审批流程">
          <t-steps :current="currentDetail.approvalStep || 0">
            <t-step-item title="提交申请" content="申请人提交采购申请" />
            <t-step-item title="部门审批" content="部门负责人审批" />
            <t-step-item title="采购审批" content="采购部审批" />
            <t-step-item title="财务审批" content="财务部审批" />
            <t-step-item title="完成" content="审批完成" />
          </t-steps>
        </t-tab-panel>
      </t-tabs>
    </t-dialog>

    <!-- 审批弹窗 -->
    <t-dialog v-model:visible="approveDialogVisible" header="审批申请" :footer="false" width="600px">
      <t-form :data="approveForm" :label-width="100">
        <t-form-item label="申请单号">
          <t-input :value="currentDetail.requestNo" disabled />
        </t-form-item>
        <t-form-item label="申请部门">
          <t-input :value="currentDetail.departmentName" disabled />
        </t-form-item>
        <t-form-item label="申请金额">
          <span class="amount-text">¥{{ formatMoney(currentDetail.totalAmount) }}</span>
        </t-form-item>
        <t-form-item label="审批意见" name="opinion" required>
          <t-textarea v-model="approveForm.opinion" placeholder="请输入审批意见" :maxlength="200" />
        </t-form-item>
      </t-form>

      <div class="dialog-footer">
        <t-space>
          <t-button theme="default" @click="approveDialogVisible = false">取消</t-button>
          <t-button theme="danger" @click="handleRejectSubmit">驳回</t-button>
          <t-button theme="success" @click="handleApproveSubmit">批准</t-button>
        </t-space>
      </div>
    </t-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { MessagePlugin } from 'tdesign-vue-next'

// 统计数据
const stats = reactive({
  totalCount: 0,
  pendingCount: 0,
  approvedCount: 0,
  totalAmount: 0
})

// 搜索表单
const searchForm = reactive({
  requestNo: '',
  departmentId: '',
  requestType: '',
  status: '',
  requestDateRange: []
})

// 表格数据
const tableData = ref([])
const loading = ref(false)
const selectedRowKeys = ref([])
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0
})

// 表格列
const columns = [
  { colKey: 'requestNo', title: '申请单号', width: 150 },
  { colKey: 'departmentName', title: '申请部门', width: 120 },
  { colKey: 'applicant', title: '申请人', width: 100 },
  { colKey: 'requestTypeName', title: '申请类型', width: 100 },
  { colKey: 'priority', title: '优先级', width: 80 },
  { colKey: 'totalQuantity', title: '申请数量', width: 100 },
  { colKey: 'totalAmount', title: '预估金额', width: 120 },
  { colKey: 'expectedDate', title: '期望到货', width: 120 },
  { colKey: 'status', title: '状态', width: 100 },
  { colKey: 'createTime', title: '创建时间', width: 150 },
  { colKey: 'action', title: '操作', width: 280, fixed: 'right' }
]

// 编辑弹窗
const editDialogVisible = ref(false)
const isEdit = ref(false)
const activeTab = ref('basic')
const editForm = reactive({
  id: '',
  requestNo: '',
  departmentId: '',
  applicant: '',
  requestType: '1',
  priority: '3',
  expectedDate: '',
  remark: '',
  details: []
})

// 申请明细列
const detailColumns = [
  { colKey: 'productNo', title: '商品编号', width: 120 },
  { colKey: 'productName', title: '商品名称', width: 150 },
  { colKey: 'spec', title: '规格', width: 100 },
  { colKey: 'unit', title: '单位', width: 80 },
  { colKey: 'quantity', title: '申请数量', width: 100 },
  { colKey: 'estimatedPrice', title: '预估单价', width: 100 },
  { colKey: 'amount', title: '预估金额', width: 100 },
  { colKey: 'action', title: '操作', width: 80 }
]

// 详情弹窗
const detailDialogVisible = ref(false)
const detailTab = ref('info')
const currentDetail = ref({})

const viewDetailColumns = [
  { colKey: 'productNo', title: '商品编号', width: 120 },
  { colKey: 'productName', title: '商品名称', width: 150 },
  { colKey: 'spec', title: '规格', width: 100 },
  { colKey: 'unit', title: '单位', width: 80 },
  { colKey: 'quantity', title: '申请数量', width: 100 },
  { colKey: 'estimatedPrice', title: '预估单价', width: 100 },
  { colKey: 'amount', title: '预估金额', width: 100 }
]

// 审批弹窗
const approveDialogVisible = ref(false)
const approveForm = reactive({
  opinion: ''
})

// 格式化金额
const formatMoney = (value: number) => {
  return (value || 0).toFixed(2)
}

// 获取部门名称
const getDepartmentName = (departmentId: string) => {
  const map: Record<string, string> = {
    '1': '部门A',
    '2': '部门B'
  }
  return map[departmentId] || '-'
}

// 获取申请类型名称
const getRequestTypeName = (requestType: string) => {
  const map: Record<string, string> = {
    '1': '商品采购',
    '2': '设备采购',
    '3': '物料采购'
  }
  return map[requestType] || '-'
}

// 获取优先级名称
const getPriorityName = (priority: string) => {
  const map: Record<string, string> = {
    '1': '紧急',
    '2': '重要',
    '3': '普通'
  }
  return map[priority] || '-'
}

// 获取总数量
const getTotalQuantity = () => {
  return editForm.details.reduce((sum: number, item: any) => sum + (item.quantity || 0), 0)
}

// 获取总金额
const getTotalAmount = () => {
  return editForm.details.reduce((sum: number, item: any) => sum + ((item.quantity || 0) * (item.estimatedPrice || 0)), 0)
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    await new Promise(resolve => setTimeout(resolve, 500))

    tableData.value = [
      {
        id: '1',
        requestNo: 'PRQ-20260119001',
        departmentId: '1',
        departmentName: '部门A',
        applicant: '张三',
        requestType: '1',
        requestTypeName: '商品采购',
        priority: 2,
        totalQuantity: 50,
        totalAmount: 75000,
        expectedDate: '20xx-xx-xx',
        status: 1,
        createTime: '2026-01-19 10:00:00'
      },
      {
        id: '2',
        requestNo: 'PRQ-20260119002',
        departmentId: '2',
        departmentName: '部门B',
        applicant: '李四',
        requestType: '2',
        requestTypeName: '设备采购',
        priority: 1,
        totalQuantity: 10,
        totalAmount: 150000,
        expectedDate: '2026-02-15',
        status: 2,
        createTime: '2026-01-18 14:30:00'
      }
    ]

    pagination.total = 2

    stats.totalCount = 2
    stats.pendingCount = 1
    stats.approvedCount = 1
    stats.totalAmount = 225000
  } catch (error) {
    MessagePlugin.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  loadData()
}

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    requestNo: '',
    departmentId: '',
    requestType: '',
    status: '',
    requestDateRange: []
  })
  handleSearch()
}

// 新增
const handleAdd = () => {
  isEdit.value = false
  activeTab.value = 'basic'
  Object.assign(editForm, {
    id: '',
    requestNo: `PRQ-${Date.now()}`,
    departmentId: '',
    applicant: '',
    requestType: '1',
    priority: '3',
    expectedDate: '',
    remark: '',
    details: []
  })
  editDialogVisible.value = true
}

// 编辑
const handleEdit = (row: any) => {
  isEdit.value = true
  activeTab.value = 'basic'
  Object.assign(editForm, {
    ...row,
    details: row.details || []
  })
  editDialogVisible.value = true
}

// 查看
const handleView = (row: any) => {
  detailTab.value = 'info'
  currentDetail.value = {
    ...row,
    details: [
      {
        productNo: 'P001',
        productName: '商品A',
        spec: '默认规格',
        unit: '件',
        quantity: 25,
        estimatedPrice: 1500,
        amount: 37500
      },
      {
        productNo: 'P002',
        productName: '商品B',
        spec: '默认规格',
        unit: '件',
        quantity: 25,
        estimatedPrice: 1500,
        amount: 37500
      }
    ],
    approvalStep: 1
  }
  detailDialogVisible.value = true
}

// 审批
const handleApprove = (row: any) => {
  currentDetail.value = { ...row }
  approveForm.opinion = ''
  approveDialogVisible.value = true
}

// 提交审批
const handleApproveSubmit = () => {
  if (!approveForm.opinion) {
    MessagePlugin.warning('请输入审批意见')
    return
  }

  MessagePlugin.success('审批通过')
  approveDialogVisible.value = false
  loadData()
}

// 提交驳回
const handleRejectSubmit = () => {
  if (!approveForm.opinion) {
    MessagePlugin.warning('请输入审批意见')
    return
  }

  MessagePlugin.success('已驳回')
  approveDialogVisible.value = false
  loadData()
}

// 驳回
const handleReject = async (row: any) => {
  try {
    await MessagePlugin.confirm('确认驳回该申请?')
    MessagePlugin.success('驳回成功')
    loadData()
  } catch {
    // 用户取消
  }
}

// 转订单
const handleToOrder = async (row: any) => {
  try {
    await MessagePlugin.confirm('确认将该申请转为采购订单?')
    MessagePlugin.success('转订单成功')
    loadData()
  } catch {
    // 用户取消
  }
}

// 删除
const handleDelete = async (row: any) => {
  try {
    await MessagePlugin.confirm('确认删除该申请?')
    MessagePlugin.success('删除成功')
    loadData()
  } catch {
    // 用户取消
  }
}

// 批量审批
const handleBatchApprove = async () => {
  if (selectedRowKeys.value.length === 0) {
    MessagePlugin.warning('请选择要审批的申请')
    return
  }
  try {
    await MessagePlugin.confirm(`确认批量审批 ${selectedRowKeys.value.length} 个申请?`)
    MessagePlugin.success('批量审批成功')
    loadData()
  } catch {
    // 用户取消
  }
}

// 批量转订单
const handleBatchToOrder = async () => {
  if (selectedRowKeys.value.length === 0) {
    MessagePlugin.warning('请选择要转订单的申请')
    return
  }
  try {
    await MessagePlugin.confirm(`确认批量转订单 ${selectedRowKeys.value.length} 个申请?`)
    MessagePlugin.success('批量转订单成功')
    loadData()
  } catch {
    // 用户取消
  }
}

// 导出
const handleExport = () => {
  MessagePlugin.success('导出成功')
}

// 保存
const handleSave = () => {
  if (!editForm.departmentId) {
    MessagePlugin.warning('请选择申请部门')
    return
  }
  if (!editForm.applicant) {
    MessagePlugin.warning('请输入申请人')
    return
  }
  if (!editForm.expectedDate) {
    MessagePlugin.warning('请选择期望到货日期')
    return
  }
  if (!editForm.remark) {
    MessagePlugin.warning('请输入申请说明')
    return
  }

  MessagePlugin.success('保存成功')
  editDialogVisible.value = false
  loadData()
}

// 保存并提交
const handleSaveAndSubmit = () => {
  handleSave()
}

// 添加明细
const handleAddDetail = () => {
  editForm.details.push({
    productNo: '',
    productName: '',
    spec: '',
    unit: '',
    quantity: 1,
    estimatedPrice: 0,
    amount: 0
  })
}

// 移除明细
const handleRemoveDetail = (index: number) => {
  editForm.details.splice(index, 1)
}

// 分页变化
const handlePageChange = (pageInfo: any) => {
  pagination.current = pageInfo.current
  pagination.pageSize = pageInfo.pageSize
  loadData()
}

// 选择变化
const handleSelectChange = (value: any) => {
  selectedRowKeys.value = value
}

onMounted(() => {
  loadData()
})
</script>

<style scoped lang="less">
.purchase-request-container {
  padding: 16px;

  .stats-card {
    margin-bottom: 16px;

    .stat-item {
      display: flex;
      align-items: center;
      gap: 16px;

      .stat-icon {
        width: 56px;
        height: 56px;
        border-radius: 8px;
        background: var(--td-brand-color-1);
        display: flex;
        align-items: center;
        justify-content: center;
        color: var(--td-brand-color);

        &.stat-icon-warning {
          background: var(--td-warning-color-1);
          color: var(--td-warning-color);
        }

        &.stat-icon-success {
          background: var(--td-success-color-1);
          color: var(--td-success-color);
        }

        &.stat-icon-info {
          background: var(--td-brand-color-1);
          color: var(--td-brand-color);
        }
      }

      .stat-content {
        flex: 1;

        .stat-value {
          font-size: 24px;
          font-weight: 600;
          color: var(--td-text-color-primary);
          line-height: 1.2;
        }

        .stat-label {
          font-size: 14px;
          color: var(--td-text-color-secondary);
          margin-top: 4px;
        }
      }
    }
  }

  .search-card {
    margin-bottom: 16px;
  }

  .action-bar {
    margin-bottom: 16px;
  }

  .table-card {
    .amount-text {
      font-weight: 600;
    }
  }

  .detail-actions {
    margin-bottom: 16px;
  }

  .dialog-footer {
    margin-top: 24px;
    text-align: right;
  }
}
</style>
