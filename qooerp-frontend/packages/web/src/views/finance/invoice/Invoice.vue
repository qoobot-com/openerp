<template>
  <div class="invoice-container">
    <!-- 统计卡片 -->
    <t-row :gutter="16" class="mb-4">
      <t-col :span="6">
        <t-card>
          <t-statistic
            title="本月发票数"
            :value="statistics.monthlyCount"
            suffix="张"
          />
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card>
          <t-statistic
            title="本月发票金额"
            :value="statistics.monthlyAmount"
            :precision="2"
            prefix="¥"
          />
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card>
          <t-statistic
            title="待开票金额"
            :value="statistics.pendingAmount"
            :precision="2"
            prefix="¥"
          />
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card>
          <t-statistic
            title="已开票金额"
            :value="statistics.issuedAmount"
            :precision="2"
            prefix="¥"
          />
        </t-card>
      </t-col>
    </t-row>

    <!-- 搜索卡片 -->
    <t-card class="mb-4">
      <t-form :data="searchForm" layout="inline" @submit="handleSearch">
        <t-form-item label="发票号码" name="invoiceNo">
          <t-input
            v-model="searchForm.invoiceNo"
            placeholder="请输入发票号码"
            clearable
          />
        </t-form-item>
        <t-form-item label="发票类型" name="invoiceType">
          <t-select
            v-model="searchForm.invoiceType"
            placeholder="请选择发票类型"
            clearable
            style="width: 200px"
          >
            <t-option value="vat_general" label="增值税普通发票" />
            <t-option value="vat_special" label="增值税专用发票" />
            <t-option value="electronic" label="电子发票" />
          </t-select>
        </t-form-item>
        <t-form-item label="客户" name="customerId">
          <t-select
            v-model="searchForm.customerId"
            placeholder="请选择客户"
            clearable
            style="width: 200px"
          >
            <t-option value="1" label="客户A" />
            <t-option value="2" label="客户B" />
          </t-select>
        </t-form-item>
        <t-form-item label="开票日期" name="invoiceDate">
          <t-date-range-picker
            v-model="searchForm.invoiceDate"
            clearable
            placeholder="请选择日期范围"
          />
        </t-form-item>
        <t-form-item label="发票状态" name="status">
          <t-select
            v-model="searchForm.status"
            placeholder="请选择状态"
            clearable
            style="width: 200px"
          >
            <t-option value="draft" label="草稿" />
            <t-option value="pending" label="待审核" />
            <t-option value="issued" label="已开票" />
            <t-option value="cancelled" label="已作废" />
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

    <!-- 操作栏 -->
    <t-card class="mb-4">
      <t-space>
        <t-button theme="primary" @click="handleAdd">
          <template #icon><add-icon /></template>
          新增发票
        </t-button>
        <t-button theme="default" @click="handleCreateFromOrder">
          <template #icon><file-copy-icon /></template>
          从订单开票
        </t-button>
        <t-button theme="danger" @click="handleBatchDelete">
          <template #icon><delete-icon /></template>
          批量删除
        </t-button>
        <t-button theme="default" @click="handleExport">
          <template #icon><download-icon /></template>
          导出发票
        </t-button>
        <t-button theme="default" @click="handlePrint">
          <template #icon><print-icon /></template>
          打印发票
        </t-button>
      </t-space>
    </t-card>

    <!-- 发票列表表格 -->
    <t-card>
      <t-table
        :data="tableData"
        :columns="columns"
        :selected-row-keys="selectedRowKeys"
        :loading="loading"
        :pagination="pagination"
        row-key="id"
        @select-change="handleSelectChange"
        @page-change="handlePageChange"
      >
        <template #invoiceType="{ row }">
          <t-tag v-if="row.invoiceType === 'vat_general'" theme="default">
            增值税普通
          </t-tag>
          <t-tag v-else-if="row.invoiceType === 'vat_special'" theme="primary">
            增值税专用
          </t-tag>
          <t-tag v-else theme="success">电子发票</t-tag>
        </template>

        <template #status="{ row }">
          <t-tag v-if="row.status === 'draft'" theme="default">草稿</t-tag>
          <t-tag v-else-if="row.status === 'pending'" theme="warning">
            待审核
          </t-tag>
          <t-tag v-else-if="row.status === 'issued'" theme="success">
            已开票
          </t-tag>
          <t-tag v-else theme="danger">已作废</t-tag>
        </template>

        <template #operation="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleView(row)">查看</t-link>
            <t-link
              v-if="row.status === 'draft'"
              theme="primary"
              @click="handleEdit(row)"
            >
              编辑
            </t-link>
            <t-link
              v-if="row.status === 'draft' || row.status === 'pending'"
              theme="success"
              @click="handleIssue(row)"
            >
              开票
            </t-link>
            <t-link theme="primary" @click="handlePrint(row)">打印</t-link>
            <t-popconfirm content="确认作废吗？" @confirm="handleCancel(row)">
              <t-link theme="danger">作废</t-link>
            </t-popconfirm>
          </t-space>
        </template>
      </t-table>
    </t-card>

    <!-- 发票编辑弹窗 -->
    <t-dialog
      v-model:visible="dialogVisible"
      :header="dialogTitle"
      width="1100px"
      :confirm-btn="null"
      @close="handleDialogClose"
    >
      <t-tabs v-model="activeTab">
        <t-tab-panel value="basic" label="基本信息">
          <t-form :data="formData" label-width="100px">
            <t-row :gutter="16">
              <t-col :span="8">
                <t-form-item label="发票号码" name="invoiceNo">
                  <t-input v-model="formData.invoiceNo" disabled />
                </t-form-item>
              </t-col>
              <t-col :span="8">
                <t-form-item label="发票类型" name="invoiceType">
                  <t-select
                    v-model="formData.invoiceType"
                    placeholder="请选择发票类型"
                    style="width: 100%"
                  >
                    <t-option value="vat_general" label="增值税普通发票" />
                    <t-option value="vat_special" label="增值税专用发票" />
                    <t-option value="electronic" label="电子发票" />
                  </t-select>
                </t-form-item>
              </t-col>
              <t-col :span="8">
                <t-form-item label="开票日期" name="invoiceDate">
                  <t-date-picker
                    v-model="formData.invoiceDate"
                    placeholder="请选择开票日期"
                    style="width: 100%"
                  />
                </t-form-item>
              </t-col>
            </t-row>
            <t-row :gutter="16">
              <t-col :span="8">
                <t-form-item label="客户" name="customerId">
                  <t-select
                    v-model="formData.customerId"
                    placeholder="请选择客户"
                    style="width: 100%"
                  >
                    <t-option value="1" label="客户A" />
                    <t-option value="2" label="客户B" />
                  </t-select>
                </t-form-item>
              </t-col>
              <t-col :span="8">
                <t-form-item label="销售订单" name="orderNo">
                  <t-input v-model="formData.orderNo" placeholder="请输入销售订单号" />
                </t-form-item>
              </t-col>
              <t-col :span="8">
                <t-form-item label="币种" name="currency">
                  <t-select
                    v-model="formData.currency"
                    placeholder="请选择币种"
                    style="width: 100%"
                  >
                    <t-option value="CNY" label="人民币" />
                    <t-option value="USD" label="美元" />
                  </t-select>
                </t-form-item>
              </t-col>
            </t-row>
            <t-divider content="购买方信息" />
            <t-row :gutter="16">
              <t-col :span="12">
                <t-form-item label="购买方名称" name="buyerName">
                  <t-input v-model="formData.buyerName" placeholder="请输入购买方名称" />
                </t-form-item>
              </t-col>
              <t-col :span="12">
                <t-form-item label="纳税人识别号" name="buyerTaxNo">
                  <t-input v-model="formData.buyerTaxNo" placeholder="请输入纳税人识别号" />
                </t-form-item>
              </t-col>
            </t-row>
            <t-row :gutter="16">
              <t-col :span="12">
                <t-form-item label="购买方地址" name="buyerAddress">
                  <t-input v-model="formData.buyerAddress" placeholder="请输入购买方地址" />
                </t-form-item>
              </t-col>
              <t-col :span="12">
                <t-form-item label="购买方电话" name="buyerPhone">
                  <t-input v-model="formData.buyerPhone" placeholder="请输入购买方电话" />
                </t-form-item>
              </t-col>
            </t-row>
            <t-row :gutter="16">
              <t-col :span="12">
                <t-form-item label="开户银行" name="buyerBank">
                  <t-input v-model="formData.buyerBank" placeholder="请输入开户银行" />
                </t-form-item>
              </t-col>
              <t-col :span="12">
                <t-form-item label="银行账号" name="bankAccount">
                  <t-input v-model="formData.bankAccount" placeholder="请输入银行账号" />
                </t-form-item>
              </t-col>
            </t-row>
          </t-form>
        </t-tab-panel>

        <t-tab-panel value="products" label="发票明细">
          <t-button theme="primary" class="mb-4" @click="handleSelectProduct">
            <template #icon><add-icon /></template>
            添加明细
          </t-button>
          <t-table
            :data="formData.products"
            :columns="productColumns"
            row-key="productId"
            max-height="400"
          >
            <template #taxRate="{ row }">
              <t-select v-model="row.taxRate" size="small" style="width: 100px">
                <t-option :value="0" label="0%" />
                <t-option :value="0.03" label="3%" />
                <t-option :value="0.06" label="6%" />
                <t-option :value="0.09" label="9%" />
                <t-option :value="0.13" label="13%" />
              </t-select>
            </template>
            <template #amount="{ row }">
              ¥{{ (row.price * row.quantity).toFixed(2) }}
            </template>
            <template #taxAmount="{ row }">
              ¥{{ (row.price * row.quantity * row.taxRate).toFixed(2) }}
            </template>
            <template #totalAmount="{ row }">
              ¥{{ (row.price * row.quantity * (1 + row.taxRate)).toFixed(2) }}
            </template>
            <template #operation="{ row }">
              <t-popconfirm
                content="确认移除该明细吗？"
                @confirm="handleRemoveProduct(row)"
              >
                <t-link theme="danger">移除</t-link>
              </t-popconfirm>
            </template>
          </t-table>
          <div class="mt-4 text-right">
            <t-space direction="vertical" align="end">
              <div>价税合计: ¥{{ totalAmount.toFixed(2) }}</div>
              <div>税额: ¥{{ totalTaxAmount.toFixed(2) }}</div>
              <div>不含税金额: ¥{{ (totalAmount - totalTaxAmount).toFixed(2) }}</div>
              <div>价税合计(大写): <span class="text-primary font-bold">{{ amountInWords }}</span></div>
            </t-space>
          </div>
        </t-tab-panel>

        <t-tab-panel value="summary" label="汇总信息">
          <t-descriptions :column="2" bordered>
            <t-descriptions-item label="发票号码">
              {{ formData.invoiceNo }}
            </t-descriptions-item>
            <t-descriptions-item label="发票类型">
              {{ getInvoiceTypeName(formData.invoiceType) }}
            </t-descriptions-item>
            <t-descriptions-item label="开票日期">
              {{ formData.invoiceDate }}
            </t-descriptions-item>
            <t-descriptions-item label="购买方名称">
              {{ formData.buyerName }}
            </t-descriptions-item>
            <t-descriptions-item label="纳税人识别号">
              {{ formData.buyerTaxNo }}
            </t-descriptions-item>
            <t-descriptions-item label="不含税金额">
              ¥{{ (totalAmount - totalTaxAmount).toFixed(2) }}
            </t-descriptions-item>
            <t-descriptions-item label="税额">
              ¥{{ totalTaxAmount.toFixed(2) }}
            </t-descriptions-item>
            <t-descriptions-item label="价税合计">
              <span class="text-primary font-bold">¥{{ totalAmount.toFixed(2) }}</span>
            </t-descriptions-item>
          </t-descriptions>
        </t-tab-panel>
      </t-tabs>

      <template #footer>
        <t-button theme="default" @click="handleDialogClose">取消</t-button>
        <t-button theme="default" @click="handleSaveDraft">保存草稿</t-button>
        <t-button theme="primary" @click="handleSubmit">提交开票</t-button>
      </template>
    </t-dialog>

    <!-- 发票详情弹窗 -->
    <t-dialog
      v-model:visible="detailDialogVisible"
      header="发票详情"
      width="1100px"
      :footer="false"
    >
      <t-tabs v-model="detailActiveTab">
        <t-tab-panel value="basic" label="基本信息">
          <t-descriptions :column="2" bordered>
            <t-descriptions-item label="发票号码">
              {{ detailData.invoiceNo }}
            </t-descriptions-item>
            <t-descriptions-item label="发票类型">
              {{ getInvoiceTypeName(detailData.invoiceType) }}
            </t-descriptions-item>
            <t-descriptions-item label="开票日期">
              {{ detailData.invoiceDate }}
            </t-descriptions-item>
            <t-descriptions-item label="销售订单">
              {{ detailData.orderNo }}
            </t-descriptions-item>
            <t-divider content="购买方信息" />
            <t-descriptions-item label="购买方名称">
              {{ detailData.buyerName }}
            </t-descriptions-item>
            <t-descriptions-item label="纳税人识别号">
              {{ detailData.buyerTaxNo }}
            </t-descriptions-item>
            <t-descriptions-item label="购买方地址">
              {{ detailData.buyerAddress }}
            </t-descriptions-item>
            <t-descriptions-item label="购买方电话">
              {{ detailData.buyerPhone }}
            </t-descriptions-item>
            <t-descriptions-item label="开户银行">
              {{ detailData.buyerBank }}
            </t-descriptions-item>
            <t-descriptions-item label="银行账号">
              {{ detailData.bankAccount }}
            </t-descriptions-item>
          </t-descriptions>
        </t-tab-panel>

        <t-tab-panel value="products" label="发票明细">
          <t-table
            :data="detailData.products"
            :columns="detailProductColumns"
            max-height="400"
          />
        </t-tab-panel>

        <t-tab-panel value="summary" label="汇总信息">
          <t-descriptions :column="2" bordered>
            <t-descriptions-item label="不含税金额">
              ¥{{ ((detailData.totalAmount || 0) - (detailData.totalTaxAmount || 0)).toFixed(2) }}
            </t-descriptions-item>
            <t-descriptions-item label="税额">
              ¥{{ (detailData.totalTaxAmount || 0).toFixed(2) }}
            </t-descriptions-item>
            <t-descriptions-item label="价税合计" :span="2">
              <span class="text-primary font-bold">¥{{ (detailData.totalAmount || 0).toFixed(2) }}</span>
            </t-descriptions-item>
          </t-descriptions>
        </t-tab-panel>
      </t-tabs>
    </t-dialog>

    <!-- 从订单开票弹窗 -->
    <t-dialog
      v-model:visible="orderSelectDialogVisible"
      header="选择销售订单"
      width="1000px"
      :confirm-btn="null"
      @close="handleOrderSelectClose"
    >
      <t-table
        :data="orderList"
        :columns="orderSelectColumns"
        row-key="id"
        @row-click="handleOrderRowClick"
      >
        <template #status="{ row }">
          <t-tag v-if="row.status === 'approved'" theme="success">已审核</t-tag>
          <t-tag v-else theme="default">{{ row.status }}</t-tag>
        </template>
      </t-table>
    </t-dialog>

    <!-- 商品明细选择弹窗 -->
    <t-dialog
      v-model:visible="productSelectDialogVisible"
      header="选择商品明细"
      width="900px"
      :confirm-btn="null"
      @close="handleProductSelectClose"
    >
      <t-form :data="productSearchForm" layout="inline" class="mb-4">
        <t-form-item label="商品名称" name="productName">
          <t-input
            v-model="productSearchForm.productName"
            placeholder="请输入商品名称"
          />
        </t-form-item>
        <t-form-item>
          <t-button theme="primary" @click="handleSearchProduct">查询</t-button>
        </t-form-item>
      </t-form>
      <t-table
        :data="productList"
        :columns="productSelectColumns"
        :selected-row-keys="selectedProductKeys"
        row-key="id"
        @select-change="handleProductSelectChange"
      />
      <template #footer>
        <t-button theme="default" @click="handleProductSelectClose">取消</t-button>
        <t-button theme="primary" @click="handleConfirmProductSelect">确认</t-button>
      </template>
    </t-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import {
  AddIcon,
  DeleteIcon,
  DownloadIcon,
  PrintIcon,
  FileCopyIcon,
} from 'tdesign-icons-vue-next'
import { MessagePlugin } from 'tdesign-vue-next'

// 统计数据
const statistics = ref({
  monthlyCount: 56,
  monthlyAmount: 32500.00,
  pendingAmount: 8500.00,
  issuedAmount: 24000.00,
})

// 搜索表单
const searchForm = reactive({
  invoiceNo: '',
  invoiceType: '',
  customerId: '',
  invoiceDate: [],
  status: '',
})

// 表格数据
const loading = ref(false)
const tableData = ref([
  {
    id: '1',
    invoiceNo: 'INV20250219001',
    invoiceType: 'vat_special',
    invoiceDate: '2025-02-19',
    customerId: '1',
    customerName: '客户A',
    orderNo: 'SO20250219001',
    currency: 'CNY',
    buyerName: '深圳某某有限公司',
    buyerTaxNo: '91440300XXXXXXXXX',
    buyerAddress: '广东省深圳市南山区',
    buyerPhone: '0755-XXXXXXXX',
    buyerBank: '招商银行',
    bankAccount: 'XXXXXXXXXX',
    totalAmount: 11300.00,
    totalTaxAmount: 1300.00,
    status: 'issued',
  },
  {
    id: '2',
    invoiceNo: 'INV20250218001',
    invoiceType: 'vat_general',
    invoiceDate: '2025-02-18',
    customerId: '2',
    customerName: '客户B',
    orderNo: 'SO20250218001',
    currency: 'CNY',
    buyerName: '广州某某有限公司',
    buyerTaxNo: '91440100XXXXXXXXX',
    buyerAddress: '广东省广州市天河区',
    buyerPhone: '020-XXXXXXXX',
    buyerBank: '工商银行',
    bankAccount: 'XXXXXXXXXX',
    totalAmount: 5300.00,
    totalTaxAmount: 300.00,
    status: 'draft',
  },
])

// 表格列
const columns = [
  { colKey: 'invoiceNo', title: '发票号码', width: 150 },
  { colKey: 'invoiceType', title: '发票类型', width: 120 },
  { colKey: 'invoiceDate', title: '开票日期', width: 120 },
  { colKey: 'customerName', title: '客户', width: 120 },
  { colKey: 'orderNo', title: '销售订单', width: 150 },
  { colKey: 'buyerName', title: '购买方名称', width: 180 },
  { colKey: 'buyerTaxNo', title: '纳税人识别号', width: 160 },
  { colKey: 'totalAmount', title: '价税合计', width: 120 },
  { colKey: 'status', title: '发票状态', width: 100 },
  { colKey: 'operation', title: '操作', width: 240, fixed: 'right' },
]

// 分页
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 100,
})

// 选中行
const selectedRowKeys = ref([])

// 弹窗
const dialogVisible = ref(false)
const dialogTitle = ref('新增发票')
const activeTab = ref('basic')
const formData = reactive({
  invoiceNo: '',
  invoiceType: 'vat_general',
  invoiceDate: '',
  customerId: '',
  orderNo: '',
  currency: 'CNY',
  buyerName: '',
  buyerTaxNo: '',
  buyerAddress: '',
  buyerPhone: '',
  buyerBank: '',
  bankAccount: '',
  products: [],
})

// 商品表格列
const productColumns = [
  { colKey: 'productName', title: '商品名称', width: 200 },
  { colKey: 'spec', title: '规格型号', width: 150 },
  { colKey: 'unit', title: '单位', width: 80 },
  { colKey: 'quantity', title: '数量', width: 100 },
  { colKey: 'price', title: '单价', width: 120 },
  { colKey: 'taxRate', title: '税率', width: 120 },
  { colKey: 'amount', title: '金额', width: 120 },
  { colKey: 'taxAmount', title: '税额', width: 120 },
  { colKey: 'totalAmount', title: '价税合计', width: 130 },
  { colKey: 'operation', title: '操作', width: 80 },
]

// 计算合计
const totalAmount = computed(() => {
  return formData.products.reduce(
    (sum, item) => sum + item.price * item.quantity * (1 + item.taxRate),
    0
  )
})

const totalTaxAmount = computed(() => {
  return formData.products.reduce(
    (sum, item) => sum + item.price * item.quantity * item.taxRate,
    0
  )
})

const amountInWords = computed(() => {
  return convertAmountToWords(totalAmount.value)
})

// 详情弹窗
const detailDialogVisible = ref(false)
const detailActiveTab = ref('basic')
const detailData = ref({})

const detailProductColumns = [
  { colKey: 'productName', title: '商品名称', width: 200 },
  { colKey: 'spec', title: '规格型号', width: 150 },
  { colKey: 'unit', title: '单位', width: 80 },
  { colKey: 'quantity', title: '数量', width: 100 },
  { colKey: 'price', title: '单价', width: 120 },
  { colKey: 'taxRate', title: '税率', width: 100 },
  { colKey: 'amount', title: '金额', width: 120 },
  { colKey: 'taxAmount', title: '税额', width: 120 },
  { colKey: 'totalAmount', title: '价税合计', width: 130 },
]

// 订单选择弹窗
const orderSelectDialogVisible = ref(false)
const orderList = ref([
  {
    id: '1',
    orderNo: 'SO20250219001',
    orderDate: '2025-02-19',
    customerName: '客户A',
    finalAmount: 10000.00,
    status: 'approved',
  },
  {
    id: '2',
    orderNo: 'SO20250218001',
    orderDate: '2025-02-18',
    customerName: '客户B',
    finalAmount: 5000.00,
    status: 'approved',
  },
])
const orderSelectColumns = [
  { colKey: 'orderNo', title: '订单编号', width: 150 },
  { colKey: 'orderDate', title: '订单日期', width: 120 },
  { colKey: 'customerName', title: '客户', width: 120 },
  { colKey: 'finalAmount', title: '订单金额', width: 120 },
  { colKey: 'status', title: '状态', width: 100 },
]

// 商品选择弹窗
const productSelectDialogVisible = ref(false)
const productSearchForm = reactive({
  productName: '',
})
const productList = ref([
  {
    id: '1',
    productName: '商品A',
    spec: '500g/包',
    unit: '包',
    price: 50.00,
    taxRate: 0.13,
  },
  {
    id: '2',
    productName: '商品B',
    spec: '330ml/瓶',
    unit: '瓶',
    price: 6.00,
    taxRate: 0.13,
  },
])
const selectedProductKeys = ref([])
const productSelectColumns = [
  { colKey: 'productName', title: '商品名称', width: 200 },
  { colKey: 'spec', title: '规格型号', width: 150 },
  { colKey: 'unit', title: '单位', width: 80 },
  { colKey: 'price', title: '单价', width: 120 },
  { colKey: 'taxRate', title: '税率', width: 100 },
]

// 搜索
const handleSearch = () => {
  MessagePlugin.success('查询成功')
}

const handleReset = () => {
  Object.assign(searchForm, {
    invoiceNo: '',
    invoiceType: '',
    customerId: '',
    invoiceDate: [],
    status: '',
  })
}

// 操作
const handleAdd = () => {
  dialogTitle.value = '新增发票'
  activeTab.value = 'basic'
  const today = new Date().toISOString().split('T')[0]
  Object.assign(formData, {
    invoiceNo: `INV${Date.now()}`,
    invoiceType: 'vat_general',
    invoiceDate: today,
    customerId: '',
    orderNo: '',
    currency: 'CNY',
    buyerName: '',
    buyerTaxNo: '',
    buyerAddress: '',
    buyerPhone: '',
    buyerBank: '',
    bankAccount: '',
    products: [],
  })
  dialogVisible.value = true
}

const handleCreateFromOrder = () => {
  orderSelectDialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑发票'
  activeTab.value = 'basic'
  Object.assign(formData, {
    ...row,
    products: [
      {
        productId: '1',
        productName: '商品A',
        spec: '500g/包',
        unit: '包',
        quantity: 10,
        price: 50.00,
        taxRate: 0.13,
      },
    ],
  })
  dialogVisible.value = true
}

const handleDelete = (row) => {
  MessagePlugin.success('删除成功')
}

const handleBatchDelete = () => {
  if (selectedRowKeys.value.length === 0) {
    MessagePlugin.warning('请选择要删除的记录')
    return
  }
  MessagePlugin.success(`批量删除 ${selectedRowKeys.value.length} 条记录成功`)
  selectedRowKeys.value = []
}

const handleView = (row) => {
  detailActiveTab.value = 'basic'
  detailData.value = {
    ...row,
    products: [
      {
        productName: '商品A',
        spec: '500g/包',
        unit: '包',
        quantity: 10,
        price: 50.00,
        taxRate: 0.13,
        amount: 500.00,
        taxAmount: 65.00,
        totalAmount: 565.00,
      },
    ],
    totalAmount: 565.00,
    totalTaxAmount: 65.00,
  }
  detailDialogVisible.value = true
}

const handleIssue = (row) => {
  MessagePlugin.success('开票成功')
}

const handleCancel = (row) => {
  MessagePlugin.success('作废成功')
}

const handleExport = () => {
  MessagePlugin.success('导出成功')
}

const handlePrint = (row) => {
  MessagePlugin.success('打印成功')
}

// 表格选择
const handleSelectChange = (value) => {
  selectedRowKeys.value = value
}

// 分页
const handlePageChange = (pageInfo) => {
  pagination.current = pageInfo.current
  pagination.pageSize = pageInfo.pageSize
}

// 弹窗操作
const handleDialogClose = () => {
  dialogVisible.value = false
}

const handleSaveDraft = () => {
  MessagePlugin.success('保存草稿成功')
  dialogVisible.value = false
}

const handleSubmit = () => {
  MessagePlugin.success('提交开票成功')
  dialogVisible.value = false
}

// 订单选择
const handleOrderRowClick = (row) => {
  dialogTitle.value = '新增发票'
  activeTab.value = 'basic'
  const today = new Date().toISOString().split('T')[0]
  Object.assign(formData, {
    invoiceNo: `INV${Date.now()}`,
    invoiceType: 'vat_general',
    invoiceDate: today,
    customerId: row.customerName === '客户A' ? '1' : '2',
    orderNo: row.orderNo,
    currency: 'CNY',
    buyerName: `${row.customerName}有限公司`,
    buyerTaxNo: '91440000XXXXXXXXX',
    buyerAddress: '广东省深圳市南山区',
    buyerPhone: '0755-XXXXXXXX',
    buyerBank: '招商银行',
    bankAccount: 'XXXXXXXXXX',
    products: [
      {
        productId: '1',
        productName: '商品A',
        spec: '500g/包',
        unit: '包',
        quantity: 10,
        price: 50.00,
        taxRate: 0.13,
      },
    ],
  })
  orderSelectDialogVisible.value = false
  dialogVisible.value = true
}

const handleOrderSelectClose = () => {
  orderSelectDialogVisible.value = false
}

// 商品明细选择
const handleSelectProduct = () => {
  productSelectDialogVisible.value = true
}

const handleSearchProduct = () => {
  MessagePlugin.success('查询商品成功')
}

const handleProductSelectChange = (value) => {
  selectedProductKeys.value = value
}

const handleProductSelectClose = () => {
  productSelectDialogVisible.value = false
  selectedProductKeys.value = []
}

const handleConfirmProductSelect = () => {
  const selectedProducts = productList.value.filter(item =>
    selectedProductKeys.value.includes(item.id)
  )
  selectedProducts.forEach(product => {
    const exists = formData.products.find(p => p.productId === product.id)
    if (!exists) {
      formData.products.push({
        ...product,
        quantity: 1,
      })
    }
  })
  productSelectDialogVisible.value = false
  selectedProductKeys.value = []
  MessagePlugin.success(`添加 ${selectedProducts.length} 个明细成功`)
}

const handleRemoveProduct = (row) => {
  const index = formData.products.findIndex(p => p.productId === row.productId)
  if (index > -1) {
    formData.products.splice(index, 1)
  }
}

// 工具函数
const getInvoiceTypeName = (type) => {
  const map = {
    vat_general: '增值税普通发票',
    vat_special: '增值税专用发票',
    electronic: '电子发票',
  }
  return map[type] || type
}

// 金额转大写
const convertAmountToWords = (amount: number): string => {
  const digits = ['零', '壹', '贰', '叁', '肆', '伍', '陆', '柒', '捌', '玖']
  const units = ['', '拾', '佰', '仟', '万', '拾', '佰', '仟', '亿']
  
  if (amount === 0) return '零元整'
  
  const yuan = Math.floor(amount)
  const jiao = Math.floor((amount - yuan) * 10)
  const fen = Math.round((amount - yuan) * 100 % 10)
  
  let result = ''
  let temp = yuan
  let unitIndex = 0
  let lastZero = false
  
  while (temp > 0) {
    const digit = temp % 10
    if (digit === 0) {
      if (!lastZero && unitIndex > 0) {
        result = digits[0] + result
      }
      lastZero = true
    } else {
      result = digits[digit] + units[unitIndex] + result
      lastZero = false
    }
    temp = Math.floor(temp / 10)
    unitIndex++
  }
  
  result += '元'
  
  if (jiao > 0 || fen > 0) {
    if (jiao > 0) {
      result += digits[jiao] + '角'
    } else {
      result += '零'
    }
    if (fen > 0) {
      result += digits[fen] + '分'
    } else {
      result += '整'
    }
  } else {
    result += '整'
  }
  
  return result
}

onMounted(() => {
  // 初始化数据
})
</script>

<style scoped lang="scss">
.invoice-container {
  padding: 16px;
}

.text-primary {
  color: var(--td-brand-color);
}

.font-bold {
  font-weight: bold;
}
</style>
