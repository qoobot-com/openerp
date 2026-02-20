<template>
  <div class="reimbursement-container">
    <!-- 统计卡片 -->
    <t-row :gutter="[16, 16]" class="statistics-cards">
      <t-col :span="6">
        <t-card>
          <statistic-item
            title="本月报销单数"
            :value="statistics.totalCount"
            :loading="loading"
            icon="money-circle"
            color="#0052D9"
          />
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card>
          <statistic-item
            title="本月报销金额"
            :value="statistics.totalAmount"
            :precision="2"
            prefix="¥"
            :loading="loading"
            icon="chart-column"
            color="#00A870"
          />
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card>
          <statistic-item
            title="待审核报销"
            :value="statistics.pendingCount"
            :loading="loading"
            icon="time"
            color="#ED7B2F"
          />
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card>
          <statistic-item
            title="待付款报销"
            :value="statistics.pendingPaymentCount"
            :loading="loading"
            icon="wallet"
            color="#E34D59"
          />
        </t-card>
      </t-col>
    </t-row>

    <!-- 搜索卡片 -->
    <t-card class="search-card">
      <t-form
        ref="searchFormRef"
        :data="searchForm"
        label-width="100px"
        @submit="handleSearch"
        @reset="handleReset"
      >
        <t-row :gutter="16">
          <t-col :span="4">
            <t-form-item label="报销单号" name="reimbursementNo">
              <t-input v-model="searchForm.reimbursementNo" placeholder="请输入报销单号" clearable />
            </t-form-item>
          </t-col>
          <t-col :span="4">
            <t-form-item label="报销人" name="applicant">
              <t-input v-model="searchForm.applicant" placeholder="请输入报销人" clearable />
            </t-form-item>
          </t-col>
          <t-col :span="4">
            <t-form-item label="报销类型" name="reimbursementType">
              <t-select v-model="searchForm.reimbursementType" placeholder="请选择报销类型" clearable>
                <t-option value="差旅费" label="差旅费" />
                <t-option value="办公费" label="办公费" />
                <t-option value="招待费" label="招待费" />
                <t-option value="交通费" label="交通费" />
                <t-option value="其他" label="其他" />
              </t-select>
            </t-form-item>
          </t-col>
          <t-col :span="4">
            <t-form-item label="报销日期" name="dateRange">
              <t-date-range-picker
                v-model="searchForm.dateRange"
                placeholder="请选择日期范围"
                clearable
              />
            </t-form-item>
          </t-col>
          <t-col :span="4">
            <t-form-item label="审核状态" name="approvalStatus">
              <t-select v-model="searchForm.approvalStatus" placeholder="请选择审核状态" clearable>
                <t-option value="draft" label="草稿" />
                <t-option value="pending" label="待审核" />
                <t-option value="approved" label="已审核" />
                <t-option value="rejected" label="已驳回" />
              </t-select>
            </t-form-item>
          </t-col>
          <t-col :span="4">
            <t-form-item label="付款状态" name="paymentStatus">
              <t-select v-model="searchForm.paymentStatus" placeholder="请选择付款状态" clearable>
                <t-option value="unpaid" label="未付款" />
                <t-option value="partial" label="部分付款" />
                <t-option value="paid" label="已付款" />
              </t-select>
            </t-form-item>
          </t-col>
        </t-row>
        <t-row>
          <t-col :span="24" class="search-actions">
            <t-space>
              <t-button theme="primary" type="submit">查询</t-button>
              <t-button theme="default" type="reset">重置</t-button>
            </t-space>
          </t-col>
        </t-row>
      </t-form>
    </t-card>

    <!-- 操作栏 -->
    <t-card class="action-card">
      <t-space>
        <t-button theme="primary" @click="handleAdd">
          <template #icon><t-icon name="add" /></template>
          新增报销
        </t-button>
        <t-button theme="default" @click="handleBatchDelete">
          <template #icon><t-icon name="delete" /></template>
          批量删除
        </t-button>
        <t-button theme="default" @click="handleExport">
          <template #icon><t-icon name="download" /></template>
          导出报表
        </t-button>
        <t-button theme="default" @click="handlePrint">
          <template #icon><t-icon name="print" /></template>
          打印单据
        </t-button>
      </t-space>
    </t-card>

    <!-- 列表 -->
    <t-card>
      <t-table
        v-model:row-selection="selectedRowKeys"
        :data="tableData"
        :columns="columns"
        :loading="loading"
        :pagination="pagination"
        @page-change="handlePageChange"
        row-key="id"
      >
        <template #approvalStatus="{ row }">
          <t-tag :theme="getApprovalStatusTheme(row.approvalStatus)">
            {{ getApprovalStatusText(row.approvalStatus) }}
          </t-tag>
        </template>
        <template #paymentStatus="{ row }">
          <t-tag :theme="getPaymentStatusTheme(row.paymentStatus)">
            {{ getPaymentStatusText(row.paymentStatus) }}
          </t-tag>
        </template>
        <template #reimbursementType="{ row }">
          <t-tag variant="light" theme="primary">{{ row.reimbursementType }}</t-tag>
        </template>
        <template #action="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleView(row)">详情</t-link>
            <t-link theme="primary" @click="handleEdit(row)">编辑</t-link>
            <t-link v-if="row.approvalStatus === 'pending'" theme="success" @click="handleApprove(row)">
              审核
            </t-link>
            <t-link v-if="row.approvalStatus === 'approved' && row.paymentStatus !== 'paid'" theme="warning" @click="handlePay(row)">
              付款
            </t-link>
            <t-link theme="danger" @click="handleDelete(row)">删除</t-link>
          </t-space>
        </template>
      </t-table>
    </t-card>

    <!-- 编辑弹窗 -->
    <t-dialog
      v-model:visible="editDialogVisible"
      header="报销单"
      width="1000px"
      :footer="false"
      @close="handleEditDialogClose"
    >
      <t-tabs v-model="activeTab" theme="card">
        <t-tab-panel value="basic" label="基本信息">
          <t-form
            ref="editFormRef"
            :data="editForm"
            :rules="editRules"
            label-width="120px"
            @submit="handleSubmit"
          >
            <t-row :gutter="16">
              <t-col :span="12">
                <t-form-item label="报销单号" name="reimbursementNo">
                  <t-input v-model="editForm.reimbursementNo" placeholder="自动生成" disabled />
                </t-form-item>
              </t-col>
              <t-col :span="12">
                <t-form-item label="报销日期" name="reimbursementDate">
                  <t-date-picker v-model="editForm.reimbursementDate" placeholder="请选择报销日期" />
                </t-form-item>
              </t-col>
            </t-row>
            <t-row :gutter="16">
              <t-col :span="12">
                <t-form-item label="报销人" name="applicant">
                  <t-select v-model="editForm.applicant" placeholder="请选择报销人" filterable>
                    <t-option value="张三" label="张三" />
                    <t-option value="李四" label="李四" />
                    <t-option value="王五" label="王五" />
                  </t-select>
                </t-form-item>
              </t-col>
              <t-col :span="12">
                <t-form-item label="部门" name="department">
                  <t-input v-model="editForm.department" placeholder="请输入部门" disabled />
                </t-form-item>
              </t-col>
            </t-row>
            <t-row :gutter="16">
              <t-col :span="12">
                <t-form-item label="报销类型" name="reimbursementType">
                  <t-select v-model="editForm.reimbursementType" placeholder="请选择报销类型">
                    <t-option value="差旅费" label="差旅费" />
                    <t-option value="办公费" label="办公费" />
                    <t-option value="招待费" label="招待费" />
                    <t-option value="交通费" label="交通费" />
                    <t-option value="其他" label="其他" />
                  </t-select>
                </t-form-item>
              </t-col>
              <t-col :span="12">
                <t-form-item label="报销账户" name="account">
                  <t-select v-model="editForm.account" placeholder="请选择报销账户">
                    <t-option value="支付宝" label="支付宝" />
                    <t-option value="微信" label="微信" />
                    <t-option value="银行卡" label="银行卡" />
                  </t-select>
                </t-form-item>
              </t-col>
            </t-row>
            <t-row :gutter="16">
              <t-col :span="12">
                <t-form-item label="收款账号" name="accountNo">
                  <t-input v-model="editForm.accountNo" placeholder="请输入收款账号" />
                </t-form-item>
              </t-col>
              <t-col :span="12">
                <t-form-item label="收款人" name="payee">
                  <t-input v-model="editForm.payee" placeholder="请输入收款人" />
                </t-form-item>
              </t-col>
            </t-row>
            <t-form-item label="报销说明" name="description">
              <t-textarea
                v-model="editForm.description"
                placeholder="请输入报销说明"
                :maxlength="500"
                :autosize="{ minRows: 3 }"
              />
            </t-form-item>
            <t-form-item label="内部备注" name="internalRemark">
              <t-textarea
                v-model="editForm.internalRemark"
                placeholder="请输入内部备注"
                :maxlength="200"
                :autosize="{ minRows: 2 }"
              />
            </t-form-item>
          </t-form>
        </t-tab-panel>
        <t-tab-panel value="details" label="报销明细">
          <div class="detail-actions">
            <t-button theme="primary" size="small" @click="handleAddDetail">
              <template #icon><t-icon name="add" /></template>
              添加明细
            </t-button>
          </div>
          <t-table
            :data="editForm.details"
            :columns="detailColumns"
            :pagination="false"
            row-key="id"
          >
            <template #detailType="{ row }">
              <t-input v-model="row.detailType" placeholder="请输入明细类型" size="small" />
            </template>
            <template #expenseDate="{ row }">
              <t-date-picker v-model="row.expenseDate" placeholder="请选择日期" size="small" />
            </template>
            <template #description="{ row }">
              <t-input v-model="row.description" placeholder="请输入说明" size="small" />
            </template>
            <template #amount="{ row }">
              <t-input-number v-model="row.amount" placeholder="金额" size="small" :min="0" :precision="2" @change="calculateTotal" />
            </template>
            <template #action="{ row }">
              <t-button theme="danger" size="small" @click="handleRemoveDetail(row)">
                <template #icon><t-icon name="delete" /></template>
              </t-button>
            </template>
          </t-table>
          <div class="summary-info">
            <t-descriptions :column="4" bordered>
              <t-descriptions-item label="报销明细数">{{ editForm.details.length }}</t-descriptions-item>
              <t-descriptions-item label="报销总金额">¥{{ formatMoney(totalAmount) }}</t-descriptions-item>
              <t-descriptions-item label="大写金额">{{ convertToChineseMoney(totalAmount) }}</t-descriptions-item>
              <t-descriptions-item label="已付金额">¥{{ formatMoney(editForm.paidAmount) }}</t-descriptions-item>
            </t-descriptions>
          </div>
        </t-tab-panel>
        <t-tab-panel value="summary" label="汇总信息">
          <t-descriptions :column="2" bordered>
            <t-descriptions-item label="报销明细数">{{ editForm.details.length }}</t-descriptions-item>
            <t-descriptions-item label="报销总金额">¥{{ formatMoney(totalAmount) }}</t-descriptions-item>
            <t-descriptions-item label="大写金额">{{ convertToChineseMoney(totalAmount) }}</t-descriptions-item>
            <t-descriptions-item label="已付金额">¥{{ formatMoney(editForm.paidAmount) }}</t-descriptions-item>
            <t-descriptions-item label="未付金额">¥{{ formatMoney(totalAmount - editForm.paidAmount) }}</t-descriptions-item>
            <t-descriptions-item label="审核状态">
              <t-tag :theme="getApprovalStatusTheme(editForm.approvalStatus)">
                {{ getApprovalStatusText(editForm.approvalStatus) }}
              </t-tag>
            </t-descriptions-item>
            <t-descriptions-item label="付款状态">
              <t-tag :theme="getPaymentStatusTheme(editForm.paymentStatus)">
                {{ getPaymentStatusText(editForm.paymentStatus) }}
              </t-tag>
            </t-descriptions-item>
            <t-descriptions-item label="创建时间">{{ editForm.createTime }}</t-descriptions-item>
          </t-descriptions>
        </t-tab-panel>
      </t-tabs>
      <div class="dialog-footer">
        <t-space>
          <t-button theme="default" @click="editDialogVisible = false">取消</t-button>
          <t-button theme="primary" @click="handleSubmit">保存</t-button>
        </t-space>
      </div>
    </t-dialog>

    <!-- 详情弹窗 -->
    <t-dialog v-model:visible="detailDialogVisible" header="报销单详情" width="1000px" :footer="false">
      <t-tabs theme="card">
        <t-tab-panel value="basic" label="基本信息">
          <t-descriptions :column="2" bordered>
            <t-descriptions-item label="报销单号">{{ currentRow.reimbursementNo }}</t-descriptions-item>
            <t-descriptions-item label="报销日期">{{ currentRow.reimbursementDate }}</t-descriptions-item>
            <t-descriptions-item label="报销人">{{ currentRow.applicant }}</t-descriptions-item>
            <t-descriptions-item label="部门">{{ currentRow.department }}</t-descriptions-item>
            <t-descriptions-item label="报销类型">{{ currentRow.reimbursementType }}</t-descriptions-item>
            <t-descriptions-item label="报销账户">{{ currentRow.account }}</t-descriptions-item>
            <t-descriptions-item label="收款账号">{{ currentRow.accountNo }}</t-descriptions-item>
            <t-descriptions-item label="收款人">{{ currentRow.payee }}</t-descriptions-item>
            <t-descriptions-item label="审核状态" :span="2">
              <t-tag :theme="getApprovalStatusTheme(currentRow.approvalStatus)">
                {{ getApprovalStatusText(currentRow.approvalStatus) }}
              </t-tag>
            </t-descriptions-item>
            <t-descriptions-item label="付款状态" :span="2">
              <t-tag :theme="getPaymentStatusTheme(currentRow.paymentStatus)">
                {{ getPaymentStatusText(currentRow.paymentStatus) }}
              </t-tag>
            </t-descriptions-item>
            <t-descriptions-item label="报销说明" :span="2">{{ currentRow.description }}</t-descriptions-item>
            <t-descriptions-item label="内部备注" :span="2">{{ currentRow.internalRemark }}</t-descriptions-item>
            <t-descriptions-item label="创建时间">{{ currentRow.createTime }}</t-descriptions-item>
            <t-descriptions-item label="更新时间">{{ currentRow.updateTime }}</t-descriptions-item>
          </t-descriptions>
        </t-tab-panel>
        <t-tab-panel value="details" label="报销明细">
          <t-table :data="currentRow.details" :columns="detailColumns" :pagination="false">
            <template #detailType="{ row }">{{ row.detailType }}</template>
            <template #expenseDate="{ row }">{{ row.expenseDate }}</template>
            <template #description="{ row }">{{ row.description }}</template>
            <template #amount="{ row }">¥{{ formatMoney(row.amount) }}</template>
          </t-table>
        </t-tab-panel>
        <t-tab-panel value="summary" label="汇总信息">
          <t-descriptions :column="2" bordered>
            <t-descriptions-item label="报销明细数">{{ currentRow.detailCount }}</t-descriptions-item>
            <t-descriptions-item label="报销总金额">¥{{ formatMoney(currentRow.totalAmount) }}</t-descriptions-item>
            <t-descriptions-item label="大写金额">{{ convertToChineseMoney(currentRow.totalAmount) }}</t-descriptions-item>
            <t-descriptions-item label="已付金额">¥{{ formatMoney(currentRow.paidAmount) }}</t-descriptions-item>
            <t-descriptions-item label="未付金额">¥{{ formatMoney(currentRow.totalAmount - currentRow.paidAmount) }}</t-descriptions-item>
            <t-descriptions-item label="创建时间">{{ currentRow.createTime }}</t-descriptions-item>
          </t-descriptions>
        </t-tab-panel>
      </t-tabs>
    </t-dialog>

    <!-- 审核弹窗 -->
    <t-dialog v-model:visible="approveDialogVisible" header="报销审核" width="600px" @confirm="handleApproveConfirm">
      <t-form :data="approveForm" label-width="100px">
        <t-form-item label="审核结果">
          <t-radio-group v-model="approveForm.result">
            <t-radio value="approved">通过</t-radio>
            <t-radio value="rejected">驳回</t-radio>
          </t-radio-group>
        </t-form-item>
        <t-form-item label="审核意见">
          <t-textarea v-model="approveForm.remark" placeholder="请输入审核意见" :autosize="{ minRows: 3 }" />
        </t-form-item>
      </t-form>
    </t-dialog>

    <!-- 付款弹窗 -->
    <t-dialog v-model:visible="payDialogVisible" header="报销付款" width="600px" @confirm="handlePayConfirm">
      <t-form :data="payForm" label-width="100px">
        <t-form-item label="付款账户">
          <t-select v-model="payForm.account" placeholder="请选择付款账户">
            <t-option value="支付宝" label="支付宝" />
            <t-option value="微信" label="微信" />
            <t-option value="银行卡" label="银行卡" />
          </t-select>
        </t-form-item>
        <t-form-item label="付款金额">
          <t-input-number v-model="payForm.amount" placeholder="付款金额" :min="0" :precision="2" />
        </t-form-item>
        <t-form-item label="付款方式">
          <t-select v-model="payForm.paymentMethod" placeholder="请选择付款方式">
            <t-option value="现金" label="现金" />
            <t-option value="转账" label="转账" />
            <t-option value="支票" label="支票" />
          </t-select>
        </t-form-item>
        <t-form-item label="付款说明">
          <t-textarea v-model="payForm.remark" placeholder="请输入付款说明" :autosize="{ minRows: 2 }" />
        </t-form-item>
      </t-form>
    </t-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { MessagePlugin } from 'tdesign-vue-next'
import type { FormInstanceFunctions, FormRule } from 'tdesign-vue-next'

defineOptions({ name: 'Reimbursement' })

// 统计数据
const statistics = reactive({
  totalCount: 128,
  totalAmount: 45680.50,
  pendingCount: 15,
  pendingPaymentCount: 23
})

// 搜索表单
const searchForm = reactive({
  reimbursementNo: '',
  applicant: '',
  reimbursementType: '',
  dateRange: [],
  approvalStatus: '',
  paymentStatus: ''
})

// 表格数据
const loading = ref(false)
const tableData = ref<any[]>([
  {
    id: '1',
    reimbursementNo: 'BX202602190001',
    reimbursementDate: '2026-02-19',
    applicant: '张三',
    department: '销售部',
    reimbursementType: '差旅费',
    account: '支付宝',
    accountNo: 'zhangsan@alipay.com',
    payee: '张三',
    approvalStatus: 'pending',
    paymentStatus: 'unpaid',
    description: '出差上海报销',
    internalRemark: '紧急报销',
    detailCount: 5,
    totalAmount: 2500.00,
    paidAmount: 0,
    createTime: '2026-02-19 10:00:00',
    updateTime: '2026-02-19 10:00:00',
    details: [
      { id: '1', detailType: '交通费', expenseDate: '2026-02-18', description: '高铁票', amount: 560 },
      { id: '2', detailType: '住宿费', expenseDate: '2026-02-18', description: '酒店住宿', amount: 800 },
      { id: '3', detailType: '餐费', expenseDate: '2026-02-18', description: '客户招待', amount: 500 },
      { id: '4', detailType: '交通费', expenseDate: '2026-02-19', description: '出租车', amount: 240 },
      { id: '5', detailType: '其他', expenseDate: '2026-02-19', description: '其他费用', amount: 400 }
    ]
  },
  {
    id: '2',
    reimbursementNo: 'BX202602190002',
    reimbursementDate: '2026-02-18',
    applicant: '李四',
    department: '技术部',
    reimbursementType: '办公费',
    account: '微信',
    accountNo: 'lisi@wechat.com',
    payee: '李四',
    approvalStatus: 'approved',
    paymentStatus: 'partial',
    description: '办公用品采购',
    internalRemark: '',
    detailCount: 3,
    totalAmount: 1200.00,
    paidAmount: 800.00,
    createTime: '2026-02-18 14:30:00',
    updateTime: '2026-02-19 09:00:00',
    details: []
  }
])

const selectedRowKeys = ref([])
const pagination = reactive({
  current: 1,
  pageSize: 20,
  total: tableData.value.length
})

// 表格列
const columns = [
  { colKey: 'reimbursementNo', title: '报销单号', width: 150 },
  { colKey: 'reimbursementDate', title: '报销日期', width: 120 },
  { colKey: 'applicant', title: '报销人', width: 100 },
  { colKey: 'department', title: '部门', width: 100 },
  { colKey: 'reimbursementType', title: '报销类型', width: 120 },
  { colKey: 'detailCount', title: '明细数', width: 80 },
  { colKey: 'totalAmount', title: '报销金额', width: 120, cell: (h: any, { row }: any) => `¥${formatMoney(row.totalAmount)}` },
  { colKey: 'approvalStatus', title: '审核状态', width: 100 },
  { colKey: 'paymentStatus', title: '付款状态', width: 100 },
  { colKey: 'description', title: '报销说明', ellipsis: true, width: 150 },
  { colKey: 'action', title: '操作', width: 260, fixed: 'right' }
]

// 明细列
const detailColumns = [
  { colKey: 'detailType', title: '明细类型', width: 120 },
  { colKey: 'expenseDate', title: '费用日期', width: 120 },
  { colKey: 'description', title: '费用说明', width: 200 },
  { colKey: 'amount', title: '金额', width: 120 },
  { colKey: 'action', title: '操作', width: 80 }
]

// 编辑弹窗
const editDialogVisible = ref(false)
const activeTab = ref('basic')
const editFormRef = ref<FormInstanceFunctions>()
const editForm = reactive({
  id: '',
  reimbursementNo: '',
  reimbursementDate: '',
  applicant: '',
  department: '',
  reimbursementType: '',
  account: '',
  accountNo: '',
  payee: '',
  description: '',
  internalRemark: '',
  approvalStatus: 'draft',
  paymentStatus: 'unpaid',
  paidAmount: 0,
  details: []
})

const editRules: Record<string, FormRule[]> = {
  reimbursementDate: [{ required: true, message: '请选择报销日期', type: 'error' }],
  applicant: [{ required: true, message: '请选择报销人', type: 'error' }],
  reimbursementType: [{ required: true, message: '请选择报销类型', type: 'error' }],
  account: [{ required: true, message: '请选择报销账户', type: 'error' }]
}

// 详情弹窗
const detailDialogVisible = ref(false)
const currentRow = ref<any>({})

// 审核弹窗
const approveDialogVisible = ref(false)
const approveForm = reactive({
  result: 'approved',
  remark: ''
})
const currentApproveRow = ref<any>(null)

// 付款弹窗
const payDialogVisible = ref(false)
const payForm = reactive({
  account: '',
  amount: 0,
  paymentMethod: '',
  remark: ''
})
const currentPayRow = ref<any>(null)

// 计算总金额
const totalAmount = computed(() => {
  return editForm.details.reduce((sum: number, item: any) => sum + (item.amount || 0), 0)
})

// 方法
const handleSearch = () => {
  loading.value = true
  setTimeout(() => {
    loading.value = false
    MessagePlugin.success('查询成功')
  }, 500)
}

const handleReset = () => {
  searchForm.reimbursementNo = ''
  searchForm.applicant = ''
  searchForm.reimbursementType = ''
  searchForm.dateRange = []
  searchForm.approvalStatus = ''
  searchForm.paymentStatus = ''
}

const handlePageChange = (pageInfo: any) => {
  pagination.current = pageInfo.current
  pagination.pageSize = pageInfo.pageSize
}

const handleAdd = () => {
  activeTab.value = 'basic'
  Object.assign(editForm, {
    id: '',
    reimbursementNo: `BX${Date.now()}`,
    reimbursementDate: new Date().toISOString().split('T')[0],
    applicant: '',
    department: '',
    reimbursementType: '',
    account: '',
    accountNo: '',
    payee: '',
    description: '',
    internalRemark: '',
    approvalStatus: 'draft',
    paymentStatus: 'unpaid',
    paidAmount: 0,
    details: []
  })
  editDialogVisible.value = true
}

const handleEdit = (row: any) => {
  activeTab.value = 'basic'
  Object.assign(editForm, row)
  editDialogVisible.value = true
}

const handleView = (row: any) => {
  currentRow.value = row
  detailDialogVisible.value = true
}

const handleDelete = (row: any) => {
  MessagePlugin.warning('删除功能待后端接口支持')
}

const handleBatchDelete = () => {
  if (selectedRowKeys.value.length === 0) {
    MessagePlugin.warning('请选择要删除的报销单')
    return
  }
  MessagePlugin.warning('批量删除功能待后端接口支持')
}

const handleExport = () => {
  MessagePlugin.info('导出功能开发中')
}

const handlePrint = () => {
  MessagePlugin.info('打印功能开发中')
}

const handleEditDialogClose = () => {
  editFormRef.value?.reset()
}

const handleSubmit = async () => {
  const valid = await editFormRef.value?.validate()
  if (valid) {
    MessagePlugin.success('保存成功')
    editDialogVisible.value = false
  }
}

const handleAddDetail = () => {
  editForm.details.push({
    id: String(Date.now()),
    detailType: '',
    expenseDate: '',
    description: '',
    amount: 0
  })
}

const handleRemoveDetail = (row: any) => {
  const index = editForm.details.findIndex((item: any) => item.id === row.id)
  if (index > -1) {
    editForm.details.splice(index, 1)
  }
  calculateTotal()
}

const calculateTotal = () => {
  // 自动计算总金额
}

const handleApprove = (row: any) => {
  currentApproveRow.value = row
  approveForm.result = 'approved'
  approveForm.remark = ''
  approveDialogVisible.value = true
}

const handleApproveConfirm = () => {
  if (currentApproveRow.value) {
    currentApproveRow.value.approvalStatus = approveForm.result
    MessagePlugin.success(approveForm.result === 'approved' ? '审核通过' : '已驳回')
    approveDialogVisible.value = false
  }
}

const handlePay = (row: any) => {
  currentPayRow.value = row
  payForm.account = row.account
  payForm.amount = row.totalAmount - row.paidAmount
  payForm.paymentMethod = '转账'
  payForm.remark = ''
  payDialogVisible.value = true
}

const handlePayConfirm = () => {
  if (currentPayRow.value) {
    currentPayRow.value.paidAmount += payForm.amount
    currentPayRow.value.paymentStatus = currentPayRow.value.paidAmount >= currentPayRow.value.totalAmount ? 'paid' : 'partial'
    MessagePlugin.success('付款成功')
    payDialogVisible.value = false
  }
}

// 辅助函数
const getApprovalStatusText = (status: string) => {
  const map: Record<string, string> = {
    draft: '草稿',
    pending: '待审核',
    approved: '已审核',
    rejected: '已驳回'
  }
  return map[status] || status
}

const getApprovalStatusTheme = (status: string) => {
  const map: Record<string, string> = {
    draft: 'default',
    pending: 'warning',
    approved: 'success',
    rejected: 'danger'
  }
  return map[status] || 'default'
}

const getPaymentStatusText = (status: string) => {
  const map: Record<string, string> = {
    unpaid: '未付款',
    partial: '部分付款',
    paid: '已付款'
  }
  return map[status] || status
}

const getPaymentStatusTheme = (status: string) => {
  const map: Record<string, string> = {
    unpaid: 'danger',
    partial: 'warning',
    paid: 'success'
  }
  return map[status] || 'default'
}

const formatMoney = (amount: number) => {
  return (amount || 0).toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ',')
}

const convertToChineseMoney = (amount: number) => {
  if (!amount || amount === 0) return '零元整'
  
  const digits = ['零', '壹', '贰', '叁', '肆', '伍', '陆', '柒', '捌', '玖']
  const units = ['', '拾', '佰', '仟']
  const bigUnits = ['', '万', '亿']
  
  let num = Math.floor(amount)
  let decimal = Math.round((amount - num) * 100)
  
  let result = ''
  
  // 整数部分
  if (num > 0) {
    let numStr = num.toString()
    let zeroFlag = false
    
    for (let i = 0; i < numStr.length; i++) {
      let digit = parseInt(numStr[i])
      let pos = numStr.length - 1 - i
      
      if (digit === 0) {
        zeroFlag = true
        if (pos % 4 === 0) {
          result += bigUnits[Math.floor(pos / 4)]
        }
      } else {
        if (zeroFlag) {
          result += '零'
          zeroFlag = false
        }
        result += digits[digit] + units[pos % 4]
        if (pos % 4 === 0) {
          result += bigUnits[Math.floor(pos / 4)]
        }
      }
    }
    result += '元'
  } else {
    result = '零元'
  }
  
  // 小数部分
  if (decimal > 0) {
    const jiao = Math.floor(decimal / 10)
    const fen = decimal % 10
    if (jiao > 0) {
      result += digits[jiao] + '角'
    }
    if (fen > 0) {
      result += digits[fen] + '分'
    }
  } else {
    result += '整'
  }
  
  return result
}
</script>

<script lang="ts">
// 统计卡片组件
import { defineComponent, h } from 'vue'

const StatisticItem = defineComponent({
  name: 'StatisticItem',
  props: {
    title: String,
    value: [Number, String],
    precision: { type: Number, default: 0 },
    prefix: String,
    loading: Boolean,
    icon: String,
    color: { type: String, default: '#0052D9' }
  },
  setup(props) {
    const displayValue = computed(() => {
      if (props.loading) return '-'
      if (typeof props.value === 'number') {
        return props.value.toFixed(props.precision)
      }
      return props.value
    })
    
    return () => h('div', { class: 'statistic-item' }, [
      h('div', { class: 'statistic-icon', style: { color: props.color } }, [
        h('t-icon', { name: props.icon, size: '32px' })
      ]),
      h('div', { class: 'statistic-content' }, [
        h('div', { class: 'statistic-title' }, props.title),
        h('div', { class: 'statistic-value', style: { color: props.color } }, [
          props.prefix,
          displayValue.value
        ])
      ])
    ])
  }
})

// 注册组件
const components = { StatisticItem }
</script>

<style scoped lang="less">
.reimbursement-container {
  padding: 16px;
  
  .statistics-cards {
    margin-bottom: 16px;
    
    .statistic-item {
      display: flex;
      align-items: center;
      
      .statistic-icon {
        margin-right: 16px;
      }
      
      .statistic-content {
        flex: 1;
        
        .statistic-title {
          font-size: 14px;
          color: var(--td-text-color-secondary);
          margin-bottom: 8px;
        }
        
        .statistic-value {
          font-size: 28px;
          font-weight: bold;
        }
      }
    }
  }
  
  .search-card {
    margin-bottom: 16px;
    
    .search-actions {
      display: flex;
      justify-content: flex-end;
    }
  }
  
  .action-card {
    margin-bottom: 16px;
  }
  
  .detail-actions {
    margin-bottom: 16px;
    display: flex;
    justify-content: flex-end;
  }
  
  .summary-info {
    margin-top: 16px;
  }
  
  .dialog-footer {
    margin-top: 24px;
    display: flex;
    justify-content: flex-end;
  }
}
</style>
