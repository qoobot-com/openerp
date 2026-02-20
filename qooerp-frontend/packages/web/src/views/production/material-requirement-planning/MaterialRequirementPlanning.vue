<template>
  <div class="material-requirement-planning">
    <!-- 统计卡片 -->
    <t-row :gutter="[16, 16]" class="statistics-cards">
      <t-col :span="3">
        <t-card theme="primary1" :header-bordered="false">
          <div class="statistic-content">
            <div class="statistic-value">{{ statistics.total }}</div>
            <div class="statistic-label">MRP总数</div>
          </div>
        </t-card>
      </t-col>
      <t-col :span="3">
        <t-card theme="success" :header-bordered="false">
          <div class="statistic-content">
            <div class="statistic-value">{{ statistics.approved }}</div>
            <div class="statistic-label">已批准</div>
          </div>
        </t-card>
      </t-col>
      <t-col :span="3">
        <t-card theme="warning" :header-bordered="false">
          <div class="statistic-content">
            <div class="statistic-value">{{ statistics.pending }}</div>
            <div class="statistic-label">待批准</div>
          </div>
        </t-card>
      </t-col>
      <t-col :span="3">
        <t-card theme="info" :header-bordered="false">
          <div class="statistic-content">
            <div class="statistic-value">{{ statistics.totalAmount }}</div>
            <div class="statistic-label">总金额(万)</div>
          </div>
        </t-card>
      </t-col>
    </t-row>

    <!-- 搜索卡片 -->
    <t-card title="MRP查询" class="search-card">
      <t-form :data="searchForm" layout="inline" @submit="handleSearch">
        <t-form-item label="MRP编号" name="mrpNo">
          <t-input v-model="searchForm.mrpNo" placeholder="请输入MRP编号" clearable />
        </t-form-item>
        <t-form-item label="产品名称" name="productName">
          <t-input v-model="searchForm.productName" placeholder="请输入产品名称" clearable />
        </t-form-item>
        <t-form-item label="计划状态" name="status">
          <t-select v-model="searchForm.status" placeholder="请选择计划状态" clearable>
            <t-option value="draft" label="草稿" />
            <t-option value="pending" label="待批准" />
            <t-option value="approved" label="已批准" />
            <t-option value="rejected" label="已驳回" />
          </t-select>
        </t-form-item>
        <t-form-item label="计划日期" name="planDate">
          <t-date-range-picker v-model="searchForm.planDate" placeholder="请选择计划日期" />
        </t-form-item>
        <t-form-item>
          <t-space>
            <t-button theme="primary" type="submit">查询</t-button>
            <t-button theme="default" @click="handleReset">重置</t-button>
          </t-space>
        </t-form-item>
      </t-form>
    </t-card>

    <!-- MRP列表 -->
    <t-card title="MRP列表" class="table-card">
      <template #actions>
        <t-space>
          <t-button theme="primary" @click="handleAdd">
            <template #icon><add-icon /></template>
            新增MRP
          </t-button>
          <t-button theme="success" @click="handleCalculate">
            <template #icon><calculate-icon /></template>
            运行MRP计算
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
          <t-tag v-else-if="row.status === 'pending'" theme="warning" variant="light">待批准</t-tag>
          <t-tag v-else-if="row.status === 'approved'" theme="success" variant="light">已批准</t-tag>
          <t-tag v-else theme="error" variant="light">已驳回</t-tag>
        </template>
        <template #action="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleView(row)">查看</t-link>
            <t-link theme="primary" @click="handleEdit(row)">编辑</t-link>
            <t-link v-if="row.status === 'pending'" theme="success" @click="handleApprove(row)">批准</t-link>
            <t-link theme="danger" @click="handleDelete(row)">删除</t-link>
          </t-space>
        </template>
      </t-table>
    </t-card>

    <!-- 新增/编辑MRP弹窗 -->
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
            <t-form-item label="MRP编号" name="mrpNo">
              <t-input v-model="formData.mrpNo" placeholder="请输入MRP编号" />
            </t-form-item>
            <t-form-item label="计划名称" name="planName">
              <t-input v-model="formData.planName" placeholder="请输入计划名称" />
            </t-form-item>
            <t-form-item label="计划类型" name="planType">
              <t-select v-model="formData.planType" placeholder="请选择计划类型">
                <t-option value="regular" label="常规MRP" />
                <t-option value="emergency" label="紧急MRP" />
                <t-option value="adjustment" label="调整MRP" />
              </t-select>
            </t-form-item>
            <t-form-item label="计划期间" name="planPeriod">
              <t-date-range-picker v-model="formData.planPeriod" placeholder="请选择计划期间" />
            </t-form-item>
            <t-form-item label="计划说明" name="description">
              <t-textarea v-model="formData.description" placeholder="请输入计划说明" :maxlength="1000" />
            </t-form-item>
          </t-form>
        </t-tab-panel>
        <t-tab-panel value="requirements" label="物料需求">
          <div class="requirements-section">
            <t-button theme="primary" size="small" @click="handleAddRequirement">
              <template #icon><add-icon /></template>
              添加需求
            </t-button>
            <t-table
              :data="formData.requirements"
              :columns="requirementColumns"
              :pagination="false"
              row-key="id"
            >
              <template #action="{ row, rowIndex }">
                <t-link theme="danger" @click="handleDeleteRequirement(rowIndex)">删除</t-link>
              </template>
            </t-table>
          </div>
        </t-tab-panel>
        <t-tab-panel value="summary" label="汇总信息">
          <t-descriptions :column="2" bordered>
            <t-descriptions-item label="MRP编号">{{ formData.mrpNo }}</t-descriptions-item>
            <t-descriptions-item label="计划名称">{{ formData.planName }}</t-descriptions-item>
            <t-descriptions-item label="计划类型">
              <t-tag v-if="formData.planType === 'regular'" theme="primary" variant="light">常规MRP</t-tag>
              <t-tag v-else-if="formData.planType === 'emergency'" theme="warning" variant="light">紧急MRP</t-tag>
              <t-tag v-else theme="info" variant="light">调整MRP</t-tag>
            </t-descriptions-item>
            <t-descriptions-item label="计划期间">{{ formData.planPeriod?.join(' ~ ') }}</t-descriptions-item>
            <t-descriptions-item label="物料种类">{{ formData.requirements.length }}种</t-descriptions-item>
            <t-descriptions-item label="总需求数量">{{ totalQuantity }}</t-descriptions-item>
            <t-descriptions-item label="总金额">¥{{ totalAmount }}</t-descriptions-item>
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

    <!-- MRP详情弹窗 -->
    <t-dialog
      v-model:visible="detailVisible"
      header="MRP详情"
      :width="1200"
      :footer="false"
    >
      <t-tabs v-model="detailTab" theme="card">
        <t-tab-panel value="basic" label="基本信息">
          <t-descriptions :column="2" bordered>
            <t-descriptions-item label="MRP编号">{{ detailData.mrpNo }}</t-descriptions-item>
            <t-descriptions-item label="计划名称">{{ detailData.planName }}</t-descriptions-item>
            <t-descriptions-item label="计划类型">
              <t-tag v-if="detailData.planType === 'regular'" theme="primary" variant="light">常规MRP</t-tag>
              <t-tag v-else-if="detailData.planType === 'emergency'" theme="warning" variant="light">紧急MRP</t-tag>
              <t-tag v-else theme="info" variant="light">调整MRP</t-tag>
            </t-descriptions-item>
            <t-descriptions-item label="计划状态">
              <t-tag v-if="detailData.status === 'draft'" theme="default" variant="light">草稿</t-tag>
              <t-tag v-else-if="detailData.status === 'pending'" theme="warning" variant="light">待批准</t-tag>
              <t-tag v-else-if="detailData.status === 'approved'" theme="success" variant="light">已批准</t-tag>
              <t-tag v-else theme="error" variant="light">已驳回</t-tag>
            </t-descriptions-item>
            <t-descriptions-item label="计划期间">{{ detailData.planPeriod }}</t-descriptions-item>
            <t-descriptions-item label="物料种类">{{ detailData.requirements?.length || 0 }}种</t-descriptions-item>
            <t-descriptions-item label="总金额">¥{{ detailData.totalAmount }}</t-descriptions-item>
            <t-descriptions-item label="计划说明" :span="2">{{ detailData.description }}</t-descriptions-item>
          </t-descriptions>
        </t-tab-panel>
        <t-tab-panel value="requirements" label="物料需求">
          <t-table
            :data="detailData.requirements || []"
            :columns="requirementColumns"
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
import { AddIcon, CalculateIcon, DownloadIcon } from 'tdesign-icons-vue-next'

// 统计数据
const statistics = ref({
  total: 150,
  approved: 120,
  pending: 30,
  totalAmount: 500
})

// 搜索表单
const searchForm = reactive({
  mrpNo: '',
  productName: '',
  status: '',
  planDate: []
})

// 表格数据
const loading = ref(false)
const tableData = ref([
  {
    id: 1,
    mrpNo: 'MRP-20260219-001',
    planName: '2月物料需求计划',
    planType: 'regular',
    status: 'approved',
    planPeriod: '2026-02-01 ~ 2026-02-28',
    totalAmount: 500000,
    requirements: [
      { id: 1, materialCode: 'M-001', materialName: '原材料A', specification: '规格A', quantity: 100, unit: '件', price: 500, amount: 50000 },
      { id: 2, materialCode: 'M-002', materialName: '原材料B', specification: '规格B', quantity: 200, unit: '件', price: 300, amount: 60000 }
    ],
    description: '2月份生产物料需求'
  },
  {
    id: 2,
    mrpNo: 'MRP-20260219-002',
    planName: '紧急物料需求',
    planType: 'emergency',
    status: 'pending',
    planPeriod: '2026-02-19 ~ 2026-02-25',
    totalAmount: 200000,
    requirements: [
      { id: 1, materialCode: 'M-003', materialName: '原材料C', specification: '规格C', quantity: 50, unit: '件', price: 1000, amount: 50000 }
    ],
    description: '紧急补货需求'
  },
  {
    id: 3,
    mrpNo: 'MRP-20260219-003',
    planName: '3月物料需求计划',
    planType: 'regular',
    status: 'draft',
    planPeriod: '2026-03-01 ~ 2026-03-31',
    totalAmount: 0,
    requirements: [],
    description: '3月份生产物料需求'
  }
])

// 表格列定义
const columns = [
  { colKey: 'mrpNo', title: 'MRP编号', width: 150 },
  { colKey: 'planName', title: '计划名称', width: 150 },
  { colKey: 'planType', title: '计划类型', width: 120 },
  { colKey: 'planPeriod', title: '计划期间', width: 180 },
  { colKey: 'totalAmount', title: '总金额', width: 120 },
  { colKey: 'status', title: '计划状态', width: 100, cell: 'status' },
  { colKey: 'action', title: '操作', width: 200, fixed: 'right', cell: 'action' }
]

const requirementColumns = [
  { colKey: 'materialCode', title: '物料编号', width: 120 },
  { colKey: 'materialName', title: '物料名称', width: 150 },
  { colKey: 'specification', title: '规格', width: 120 },
  { colKey: 'quantity', title: '需求数量', width: 100 },
  { colKey: 'unit', title: '单位', width: 80 },
  { colKey: 'price', title: '单价', width: 100 },
  { colKey: 'amount', title: '金额', width: 100 },
  { colKey: 'action', title: '操作', width: 80, cell: 'action' }
]

// 分页
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 150
})

const rowKey = 'id'

// 新增/编辑弹窗
const dialogVisible = ref(false)
const dialogTitle = computed(() => (formData.id ? '编辑MRP' : '新增MRP'))
const activeTab = ref('basic')
const formRef = ref()
const formData = reactive({
  id: undefined,
  mrpNo: '',
  planName: '',
  planType: 'regular',
  planPeriod: [],
  description: '',
  requirements: []
})

const rules = {
  mrpNo: [{ required: true, message: '请输入MRP编号' }],
  planName: [{ required: true, message: '请输入计划名称' }],
  planType: [{ required: true, message: '请选择计划类型' }],
  planPeriod: [{ required: true, message: '请选择计划期间' }]
}

// 计算总数量和总金额
const totalQuantity = computed(() => {
  return formData.requirements.reduce((sum, item: any) => sum + (item.quantity || 0), 0)
})

const totalAmount = computed(() => {
  return formData.requirements.reduce((sum, item: any) => sum + (item.amount || 0), 0)
})

// MRP详情弹窗
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
    mrpNo: '',
    productName: '',
    status: '',
    planDate: []
  })
  MessagePlugin.success('已重置')
}

// 新增MRP
const handleAdd = () => {
  Object.assign(formData, {
    id: undefined,
    mrpNo: '',
    planName: '',
    planType: 'regular',
    planPeriod: [],
    description: '',
    requirements: []
  })
  activeTab.value = 'basic'
  dialogVisible.value = true
}

// 编辑MRP
const handleEdit = (row: any) => {
  Object.assign(formData, { ...row })
  activeTab.value = 'basic'
  dialogVisible.value = true
}

// 批准MRP
const handleApprove = (row: any) => {
  const dialog = DialogPlugin({
    header: '批准MRP',
    body: `确认要批准MRP"${row.planName}"吗？`,
    confirmBtn: '确定',
    cancelBtn: '取消',
    onConfirm: () => {
      dialog.hide()
      MessagePlugin.success('MRP已批准')
    }
  })
}

// 删除MRP
const handleDelete = (row: any) => {
  const dialog = DialogPlugin({
    header: '确认删除',
    body: `确认要删除MRP"${row.planName}"吗？`,
    confirmBtn: '确定',
    cancelBtn: '取消',
    onConfirm: () => {
      dialog.hide()
      MessagePlugin.success('删除成功')
    }
  })
}

// 运行MRP计算
const handleCalculate = () => {
  MessagePlugin.loading('MRP计算中...', 2000)
  setTimeout(() => {
    MessagePlugin.success('MRP计算完成')
  }, 2000)
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
      { id: 1, time: '2026-02-19 10:00', content: '创建MRP计划', operator: '管理员' },
      { id: 2, time: '2026-02-19 14:00', content: '批准MRP计划', operator: '经理' }
    ]
  }
  detailTab.value = 'basic'
  detailVisible.value = true
}

// 添加需求
const handleAddRequirement = () => {
  formData.requirements.push({
    id: Date.now(),
    materialCode: '',
    materialName: '',
    specification: '',
    quantity: 0,
    unit: '件',
    price: 0,
    amount: 0
  })
}

// 删除需求
const handleDeleteRequirement = (index: number) => {
  formData.requirements.splice(index, 1)
}
</script>

<style scoped lang="less">
.material-requirement-planning {
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

  .requirements-section {
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
