<template>
  <div class="inquiry-management-container">
    <!-- 统计卡片 -->
    <t-row :gutter="[16, 16]" class="mb-4">
      <t-col :span="3">
        <t-card theme="primary1" :header="false" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon">
              <t-icon name="file-list" size="40px" />
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.inquiryCount }}</div>
              <div class="stat-label">询价单数</div>
            </div>
          </div>
        </t-card>
      </t-col>
      <t-col :span="3">
        <t-card theme="warning" :header="false" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon">
              <t-icon name="time" size="40px" />
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.pendingCount }}</div>
              <div class="stat-label">待处理</div>
            </div>
          </div>
        </t-card>
      </t-col>
      <t-col :span="3">
        <t-card theme="success" :header="false" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon">
              <t-icon name="check-circle" size="40px" />
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.completedCount }}</div>
              <div class="stat-label">已完成</div>
            </div>
          </div>
        </t-card>
      </t-col>
      <t-col :span="3">
        <t-card theme="info" :header="false" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon">
              <t-icon name="shop" size="40px" />
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.supplierCount }}</div>
              <div class="stat-label">供应商数</div>
            </div>
          </div>
        </t-card>
      </t-col>
    </t-row>

    <!-- 搜索卡片 -->
    <t-card class="mb-4">
      <t-form :data="searchForm" layout="inline" @submit="handleSearch" @reset="handleReset">
        <t-form-item label="询价单号" name="inquiryNo">
          <t-input v-model="searchForm.inquiryNo" placeholder="请输入询价单号" clearable style="width: 180px" />
        </t-form-item>
        <t-form-item label="供应商" name="supplierId">
          <t-select
            v-model="searchForm.supplierId"
            placeholder="请选择供应商"
            style="width: 180px"
            clearable
            filterable
          >
            <t-option v-for="item in supplierList" :key="item.value" :value="item.value" :label="item.label" />
          </t-select>
        </t-form-item>
        <t-form-item label="询价状态" name="status">
          <t-select
            v-model="searchForm.status"
            placeholder="请选择询价状态"
            style="width: 140px"
            clearable
          >
            <t-option value="pending" label="待处理" />
            <t-option value="quoted" label="已报价" />
            <t-option value="completed" label="已完成" />
            <t-option value="cancelled" label="已取消" />
          </t-select>
        </t-form-item>
        <t-form-item label="询价日期" name="inquiryDate">
          <t-date-picker
            v-model="searchForm.inquiryDate"
            placeholder="请选择询价日期"
            style="width: 180px"
            clearable
          />
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
    <t-card class="mb-4">
      <t-space>
        <t-button theme="primary" @click="handleAdd">
          <template #icon>
            <t-icon name="add" />
          </template>
          新增询价
        </t-button>
        <t-button theme="success" @click="handleBatchConvert">
          <template #icon>
            <t-icon name="check" />
          </template>
          批量转订单
        </t-button>
        <t-button theme="default" @click="handleExport">
          <template #icon>
            <t-icon name="download" />
          </template>
          导出报表
        </t-button>
      </t-space>
    </t-card>

    <!-- 询价列表 -->
    <t-card>
      <t-table
        :data="inquiryList"
        :columns="columns"
        :pagination="pagination"
        :loading="loading"
        stripe
        hover
        row-key="id"
        :selected-row-keys="selectedRowKeys"
        @select-change="handleSelectChange"
      >
        <template #priority="{ row }">
          <t-tag v-if="row.priority === 'high'" theme="danger">高</t-tag>
          <t-tag v-else-if="row.priority === 'medium'" theme="warning">中</t-tag>
          <t-tag v-else theme="default">低</t-tag>
        </template>
        <template #status="{ row }">
          <t-tag v-if="row.status === 'pending'" theme="warning">待处理</t-tag>
          <t-tag v-else-if="row.status === 'quoted'" theme="primary">已报价</t-tag>
          <t-tag v-else-if="row.status === 'completed'" theme="success">已完成</t-tag>
          <t-tag v-else theme="default">已取消</t-tag>
        </template>
        <template #action="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleView(row)">查看</t-link>
            <t-link v-if="row.status === 'pending'" theme="success" @click="handleQuote(row)">报价</t-link>
            <t-link v-if="row.status === 'quoted'" theme="warning" @click="handleConvert(row)">转订单</t-link>
            <t-link v-if="row.status === 'pending'" theme="default" @click="handleEdit(row)">编辑</t-link>
            <t-link v-if="row.status === 'pending'" theme="danger" @click="handleCancel(row)">取消</t-link>
          </t-space>
        </template>
      </t-table>
    </t-card>

    <!-- 新增/编辑询价单弹窗 -->
    <t-dialog
      v-model:visible="editDialogVisible"
      :header="isEdit ? '编辑询价单' : '新增询价单'"
      width="900px"
      :confirm-btn="null"
      :cancel-btn="null"
    >
      <t-tabs v-model="activeEditTab">
        <t-tab-panel value="basic" label="基本信息">
          <t-form :data="editForm" label-width="120px">
            <t-row :gutter="16">
              <t-col :span="12">
                <t-form-item label="询价单号">
                  <span>{{ editForm.inquiryNo || '自动生成' }}</span>
                </t-form-item>
              </t-col>
              <t-col :span="12">
                <t-form-item label="供应商" name="supplierId">
                  <t-select v-model="editForm.supplierId" placeholder="请选择供应商" filterable>
                    <t-option v-for="item in supplierList" :key="item.value" :value="item.value" :label="item.label" />
                  </t-select>
                </t-form-item>
              </t-col>
            </t-row>
            <t-row :gutter="16">
              <t-col :span="12">
                <t-form-item label="询价日期" name="inquiryDate">
                  <t-date-picker v-model="editForm.inquiryDate" placeholder="请选择询价日期" />
                </t-form-item>
              </t-col>
              <t-col :span="12">
                <t-form-item label="期望报价日期" name="expectedQuoteDate">
                  <t-date-picker v-model="editForm.expectedQuoteDate" placeholder="请选择期望报价日期" />
                </t-form-item>
              </t-col>
            </t-row>
            <t-row :gutter="16">
              <t-col :span="12">
                <t-form-item label="优先级" name="priority">
                  <t-select v-model="editForm.priority" placeholder="请选择优先级">
                    <t-option value="high" label="高" />
                    <t-option value="medium" label="中" />
                    <t-option value="low" label="低" />
                  </t-select>
                </t-form-item>
              </t-col>
              <t-col :span="12">
                <t-form-item label="币种" name="currency">
                  <t-select v-model="editForm.currency" placeholder="请选择币种">
                    <t-option value="CNY" label="人民币" />
                    <t-option value="USD" label="美元" />
                    <t-option value="EUR" label="欧元" />
                  </t-select>
                </t-form-item>
              </t-col>
            </t-row>
            <t-form-item label="询价说明" name="remark">
              <t-textarea
                v-model="editForm.remark"
                placeholder="请输入询价说明"
                :autosize="{ minRows: 3, maxRows: 5 }"
              />
            </t-form-item>
          </t-form>
        </t-tab-panel>
        <t-tab-panel value="detail" label="询价明细">
          <div class="detail-header">
            <t-button theme="primary" size="small" @click="handleAddDetail">
              <template #icon>
                <t-icon name="add" />
              </template>
              添加明细
            </t-button>
          </div>
          <t-table :data="editForm.details" :columns="detailColumns" stripe size="small">
            <template #action="{ rowIndex }">
              <t-link theme="danger" @click="handleRemoveDetail(rowIndex)">删除</t-link>
            </template>
          </t-table>
        </t-tab-panel>
        <t-tab-panel value="summary" label="汇总信息">
          <t-descriptions :column="2" bordered>
            <t-descriptions-item label="询价明细数">{{ editForm.details?.length || 0 }}</t-descriptions-item>
            <t-descriptions-item label="询价总数">{{ getTotalQuantity() }}</t-descriptions-item>
            <t-descriptions-item label="预估总金额">¥{{ getTotalAmount() }}</t-descriptions-item>
            <t-descriptions-item label="供应商">{{ getSupplierName(editForm.supplierId) }}</t-descriptions-item>
          </t-descriptions>
        </t-tab-panel>
      </t-tabs>
      <div class="dialog-footer">
        <t-space>
          <t-button theme="primary" @click="handleEditSubmit">确认</t-button>
          <t-button theme="default" @click="editDialogVisible = false">取消</t-button>
        </t-space>
      </div>
    </t-dialog>

    <!-- 询价单详情弹窗 -->
    <t-dialog
      v-model:visible="detailDialogVisible"
      header="询价单详情"
      width="900px"
      :confirm-btn="null"
      :cancel-btn="null"
    >
      <t-tabs v-model="activeDetailTab">
        <t-tab-panel value="basic" label="基本信息">
          <t-descriptions :column="2" bordered>
            <t-descriptions-item label="询价单号">{{ currentInquiry.inquiryNo }}</t-descriptions-item>
            <t-descriptions-item label="供应商">{{ currentInquiry.supplierName }}</t-descriptions-item>
            <t-descriptions-item label="询价日期">{{ currentInquiry.inquiryDate }}</t-descriptions-item>
            <t-descriptions-item label="期望报价日期">{{ currentInquiry.expectedQuoteDate }}</t-descriptions-item>
            <t-descriptions-item label="优先级">
              <t-tag v-if="currentInquiry.priority === 'high'" theme="danger">高</t-tag>
              <t-tag v-else-if="currentInquiry.priority === 'medium'" theme="warning">中</t-tag>
              <t-tag v-else theme="default">低</t-tag>
            </t-descriptions-item>
            <t-descriptions-item label="状态">
              <t-tag v-if="currentInquiry.status === 'pending'" theme="warning">待处理</t-tag>
              <t-tag v-else-if="currentInquiry.status === 'quoted'" theme="primary">已报价</t-tag>
              <t-tag v-else-if="currentInquiry.status === 'completed'" theme="success">已完成</t-tag>
              <t-tag v-else theme="default">已取消</t-tag>
            </t-descriptions-item>
            <t-descriptions-item label="询价数量">{{ currentInquiry.quantity }}</t-descriptions-item>
            <t-descriptions-item label="预估金额">¥{{ currentInquiry.estimatedAmount?.toLocaleString() }}</t-descriptions-item>
            <t-descriptions-item label="创建时间">{{ currentInquiry.createTime }}</t-descriptions-item>
            <t-descriptions-item label="询价说明">{{ currentInquiry.remark || '-' }}</t-descriptions-item>
          </t-descriptions>
        </t-tab-panel>
        <t-tab-panel value="detail" label="询价明细">
          <t-table :data="currentInquiry.details" :columns="viewDetailColumns" stripe />
        </t-tab-panel>
        <t-tab-panel value="quote" label="报价信息">
          <t-table :data="currentInquiry.quotes" :columns="quoteColumns" stripe>
            <template #action="{ row }">
              <t-space>
                <t-link theme="primary" @click="handleSelectQuote(row)">选择</t-link>
              </t-space>
            </template>
          </t-table>
        </t-tab-panel>
      </t-tabs>
      <div class="dialog-footer">
        <t-button theme="default" @click="detailDialogVisible = false">关闭</t-button>
      </div>
    </t-dialog>

    <!-- 报价弹窗 -->
    <t-dialog
      v-model:visible="quoteDialogVisible"
      header="报价"
      width="800px"
      :confirm-btn="null"
      :cancel-btn="null"
    >
      <t-form :data="quoteForm" label-width="120px" @submit="handleQuoteSubmit">
        <t-form-item label="询价单号">
          <span>{{ currentInquiry.inquiryNo }}</span>
        </t-form-item>
        <t-form-item label="报价金额" name="quoteAmount">
          <t-input-number v-model="quoteForm.quoteAmount" placeholder="请输入报价金额" :min="0" :decimal-places="2" />
        </t-form-item>
        <t-form-item label="报价说明" name="quoteRemark">
          <t-textarea
            v-model="quoteForm.quoteRemark"
            placeholder="请输入报价说明"
            :autosize="{ minRows: 3, maxRows: 5 }"
          />
        </t-form-item>
        <t-form-item>
          <t-space>
            <t-button theme="primary" type="submit">确认报价</t-button>
            <t-button theme="default" @click="quoteDialogVisible = false">取消</t-button>
          </t-space>
        </t-form-item>
      </t-form>
    </t-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { MessagePlugin, DialogPlugin } from 'tdesign-vue-next'

// 统计数据
const stats = ref({
  inquiryCount: 156,
  pendingCount: 32,
  completedCount: 108,
  supplierCount: 28,
})

// 搜索表单
const searchForm = reactive({
  inquiryNo: '',
  supplierId: undefined,
  status: undefined,
  inquiryDate: undefined,
})

// 供应商列表
const supplierList = ref([
  { value: '1', label: '北京科技有限公司' },
  { value: '2', label: '上海贸易公司' },
  { value: '3', label: '深圳电子有限公司' },
  { value: '4', label: '广州制造企业' },
  { value: '5', label: '杭州网络公司' },
])

// 分页
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 100,
  onChange: (pageInfo: any) => {
    pagination.current = pageInfo.current
    pagination.pageSize = pageInfo.pageSize
    loadData()
  },
})

// 加载状态
const loading = ref(false)

// 选中的行
const selectedRowKeys = ref([])

// 新增/编辑询价单
const editDialogVisible = ref(false)
const isEdit = ref(false)
const activeEditTab = ref('basic')
const editForm = reactive({
  id: '',
  inquiryNo: '',
  supplierId: undefined,
  inquiryDate: '',
  expectedQuoteDate: '',
  priority: 'medium',
  currency: 'CNY',
  remark: '',
  details: [] as any[],
})

// 询价单详情
const detailDialogVisible = ref(false)
const activeDetailTab = ref('basic')
const currentInquiry = ref<any>({})

// 报价弹窗
const quoteDialogVisible = ref(false)
const quoteForm = reactive({
  quoteAmount: 0,
  quoteRemark: '',
})

// 明细列表列
const detailColumns = [
  { colKey: 'productCode', title: '商品编号', width: 120 },
  { colKey: 'productName', title: '商品名称', width: 200 },
  { colKey: 'spec', title: '规格', width: 100 },
  { colKey: 'unit', title: '单位', width: 80 },
  { colKey: 'quantity', title: '询价数量', width: 100, edit: true },
  { colKey: 'estimatedPrice', title: '预估单价', width: 120, edit: true },
  { colKey: 'estimatedAmount', title: '预估金额', width: 120 },
  { colKey: 'action', title: '操作', width: 80 },
]

// 查看明细列
const viewDetailColumns = [
  { colKey: 'productCode', title: '商品编号', width: 120 },
  { colKey: 'productName', title: '商品名称', width: 200 },
  { colKey: 'spec', title: '规格', width: 100 },
  { colKey: 'unit', title: '单位', width: 80 },
  { colKey: 'quantity', title: '询价数量', width: 100 },
  { colKey: 'estimatedPrice', title: '预估单价', width: 120 },
  { colKey: 'estimatedAmount', title: '预估金额', width: 120 },
]

// 报价列
const quoteColumns = [
  { colKey: 'quoteNo', title: '报价单号', width: 150 },
  { colKey: 'quoteAmount', title: '报价金额', width: 120 },
  { colKey: 'quoteDate', title: '报价日期', width: 120 },
  { colKey: 'quoteRemark', title: '报价说明', width: 200 },
  { colKey: 'action', title: '操作', width: 100 },
]

// 询价列表
const inquiryList = ref([
  {
    id: '1',
    inquiryNo: 'IQ202602190001',
    supplierName: '北京科技有限公司',
    inquiryDate: '2026-02-19',
    expectedQuoteDate: '2026-02-22',
    priority: 'high',
    status: 'pending',
    quantity: 50,
    estimatedAmount: 12500,
    createTime: '2026-02-19 10:00:00',
    remark: '急需采购',
  },
  {
    id: '2',
    inquiryNo: 'IQ202602190002',
    supplierName: '上海贸易公司',
    inquiryDate: '2026-02-19',
    expectedQuoteDate: '2026-02-23',
    priority: 'medium',
    status: 'quoted',
    quantity: 30,
    estimatedAmount: 8900,
    createTime: '2026-02-19 11:00:00',
    remark: '',
  },
  {
    id: '3',
    inquiryNo: 'IQ202602180001',
    supplierName: '深圳电子有限公司',
    inquiryDate: '2026-02-18',
    expectedQuoteDate: '2026-02-21',
    priority: 'low',
    status: 'completed',
    quantity: 80,
    estimatedAmount: 22000,
    createTime: '2026-02-18 09:00:00',
    remark: '',
  },
  {
    id: '4',
    inquiryNo: 'IQ202602170001',
    supplierName: '广州制造企业',
    inquiryDate: '2026-02-17',
    expectedQuoteDate: '2026-02-20',
    priority: 'medium',
    status: 'pending',
    quantity: 45,
    estimatedAmount: 15000,
    createTime: '2026-02-17 10:00:00',
    remark: '',
  },
  {
    id: '5',
    inquiryNo: 'IQ202602160001',
    supplierName: '杭州网络公司',
    inquiryDate: '2026-02-16',
    expectedQuoteDate: '2026-02-19',
    priority: 'low',
    status: 'completed',
    quantity: 25,
    estimatedAmount: 7500,
    createTime: '2026-02-16 11:00:00',
    remark: '',
  },
])

// 列定义
const columns = [
  { colKey: 'inquiryNo', title: '询价单号', width: 150 },
  { colKey: 'supplierName', title: '供应商', width: 200 },
  { colKey: 'inquiryDate', title: '询价日期', width: 120 },
  { colKey: 'expectedQuoteDate', title: '期望报价日期', width: 140 },
  { colKey: 'priority', title: '优先级', width: 80 },
  { colKey: 'quantity', title: '询价数量', width: 100 },
  { colKey: 'estimatedAmount', title: '预估金额', width: 120 },
  { colKey: 'status', title: '状态', width: 100 },
  { colKey: 'createTime', title: '创建时间', width: 180 },
  { colKey: 'action', title: '操作', width: 250, fixed: 'right' },
]

// 加载数据
const loadData = () => {
  loading.value = true
  setTimeout(() => {
    loading.value = false
  }, 500)
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  loadData()
  MessagePlugin.success('查询成功')
}

// 重置
const handleReset = () => {
  searchForm.inquiryNo = ''
  searchForm.supplierId = undefined
  searchForm.status = undefined
  searchForm.inquiryDate = undefined
  pagination.current = 1
  loadData()
}

// 选择变化
const handleSelectChange = (value: any) => {
  selectedRowKeys.value = value
}

// 新增询价
const handleAdd = () => {
  isEdit.value = false
  editForm.id = ''
  editForm.inquiryNo = ''
  editForm.supplierId = undefined
  editForm.inquiryDate = ''
  editForm.expectedQuoteDate = ''
  editForm.priority = 'medium'
  editForm.currency = 'CNY'
  editForm.remark = ''
  editForm.details = []
  activeEditTab.value = 'basic'
  editDialogVisible.value = true
}

// 编辑询价
const handleEdit = (row: any) => {
  isEdit.value = true
  Object.assign(editForm, row)
  editForm.details = [
    { id: '1', productCode: 'P001', productName: '笔记本电脑', spec: 'i7/16G/512G', unit: '台', quantity: 10, estimatedPrice: 4000, estimatedAmount: 40000 },
  ]
  activeEditTab.value = 'basic'
  editDialogVisible.value = true
}

// 添加明细
const handleAddDetail = () => {
  editForm.details.push({
    id: Date.now().toString(),
    productCode: '',
    productName: '',
    spec: '',
    unit: '',
    quantity: 0,
    estimatedPrice: 0,
    estimatedAmount: 0,
  })
}

// 删除明细
const handleRemoveDetail = (rowIndex: number) => {
  editForm.details.splice(rowIndex, 1)
}

// 获取总数
const getTotalQuantity = () => {
  return editForm.details.reduce((sum, item) => sum + item.quantity, 0)
}

// 获取总金额
const getTotalAmount = () => {
  return editForm.details.reduce((sum, item) => sum + item.estimatedAmount, 0)
}

// 获取供应商名称
const getSupplierName = (supplierId: string) => {
  const supplier = supplierList.value.find((s) => s.value === supplierId)
  return supplier?.label || '-'
}

// 提交编辑
const handleEditSubmit = () => {
  editDialogVisible.value = false
  MessagePlugin.success(isEdit.value ? '修改成功' : '新增成功')
  loadData()
}

// 查看详情
const handleView = (row: any) => {
  currentInquiry.value = {
    ...row,
    details: [
      { id: '1', productCode: 'P001', productName: '笔记本电脑', spec: 'i7/16G/512G', unit: '台', quantity: 10, estimatedPrice: 4000, estimatedAmount: 40000 },
    ],
    quotes: [
      { id: '1', quoteNo: 'QT202602190001', quoteAmount: 12000, quoteDate: '2026-02-19', quoteRemark: '优惠报价' },
    ],
  }
  activeDetailTab.value = 'basic'
  detailDialogVisible.value = true
}

// 报价
const handleQuote = (row: any) => {
  currentInquiry.value = row
  quoteForm.quoteAmount = 0
  quoteForm.quoteRemark = ''
  quoteDialogVisible.value = true
}

// 提交报价
const handleQuoteSubmit = () => {
  quoteDialogVisible.value = false
  MessagePlugin.success('报价成功')
  loadData()
}

// 选择报价
const handleSelectQuote = (row: any) => {
  MessagePlugin.success('已选择报价')
}

// 转订单
const handleConvert = (row: any) => {
  const dialog = DialogPlugin({
    header: '转采购订单',
    body: `是否将询价单 ${row.inquiryNo} 转为采购订单？`,
    confirmBtn: {
      content: '确认',
      theme: 'primary',
    },
    onConfirm: () => {
      dialog.hide()
      MessagePlugin.success('已转为采购订单')
      loadData()
    },
  })
}

// 批量转订单
const handleBatchConvert = () => {
  if (selectedRowKeys.value.length === 0) {
    MessagePlugin.warning('请先选择要转订单的单据')
    return
  }
  const dialog = DialogPlugin({
    header: '批量转订单',
    body: `是否将选中的 ${selectedRowKeys.value.length} 个询价单转为采购订单？`,
    confirmBtn: {
      content: '确认',
      theme: 'primary',
    },
    onConfirm: () => {
      dialog.hide()
      MessagePlugin.success('批量转订单成功')
      selectedRowKeys.value = []
      loadData()
    },
  })
}

// 取消询价
const handleCancel = (row: any) => {
  const dialog = DialogPlugin({
    header: '取消询价',
    body: `是否取消询价单 ${row.inquiryNo}？`,
    confirmBtn: {
      content: '确认取消',
      theme: 'danger',
    },
    onConfirm: () => {
      dialog.hide()
      MessagePlugin.success('已取消询价')
      loadData()
    },
  })
}

// 导出报表
const handleExport = () => {
  MessagePlugin.success('报表导出成功')
}
</script>

<style scoped lang="less">
.inquiry-management-container {
  padding: 16px;
  background-color: #f3f3f3;
  min-height: 100vh;

  .mb-4 {
    margin-bottom: 16px;
  }

  .stat-card {
    .stat-content {
      display: flex;
      align-items: center;
      justify-content: space-between;

      .stat-icon {
        display: flex;
        align-items: center;
        justify-content: center;
        width: 70px;
        height: 70px;
        background-color: rgba(255, 255, 255, 0.3);
        border-radius: 12px;
      }

      .stat-info {
        flex: 1;
        text-align: center;
        padding-right: 70px;

        .stat-value {
          font-size: 28px;
          font-weight: bold;
          color: #fff;
          margin-bottom: 8px;
        }

        .stat-label {
          font-size: 14px;
          color: rgba(255, 255, 255, 0.85);
        }
      }
    }
  }

  .detail-header {
    margin-bottom: 12px;
  }

  .dialog-footer {
    margin-top: 20px;
    text-align: center;
  }
}
</style>
