<template>
  <div class="order-container">
    <!-- 统计卡片 -->
    <t-row :gutter="16" class="mb-4">
      <t-col :span="6">
        <t-card>
          <t-statistic
            title="本月采购单数"
            :value="statistics.monthlyCount"
            suffix="单"
          />
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card>
          <t-statistic
            title="本月采购金额"
            :value="statistics.monthlyAmount"
            :precision="2"
            prefix="¥"
          />
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card>
          <t-statistic
            title="待收货订单"
            :value="statistics.pendingReceive"
            suffix="单"
          />
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card>
          <t-statistic
            title="待付款订单"
            :value="statistics.pendingPay"
            suffix="单"
          />
        </t-card>
      </t-col>
    </t-row>

    <!-- 搜索卡片 -->
    <t-card class="mb-4">
      <t-form :data="searchForm" layout="inline" @submit="handleSearch">
        <t-form-item label="采购单号" name="orderNo">
          <t-input
            v-model="searchForm.orderNo"
            placeholder="请输入采购单号"
            clearable
          />
        </t-form-item>
        <t-form-item label="供应商" name="supplierId">
          <t-select
            v-model="searchForm.supplierId"
            placeholder="请选择供应商"
            clearable
            style="width: 200px"
          >
            <t-option value="1" label="供应商A" />
            <t-option value="2" label="供应商B" />
          </t-select>
        </t-form-item>
        <t-form-item label="采购日期" name="orderDate">
          <t-date-range-picker
            v-model="searchForm.orderDate"
            clearable
            placeholder="请选择日期范围"
          />
        </t-form-item>
        <t-form-item label="订单状态" name="status">
          <t-select
            v-model="searchForm.status"
            placeholder="请选择状态"
            clearable
            style="width: 200px"
          >
            <t-option value="draft" label="草稿" />
            <t-option value="pending" label="待审核" />
            <t-option value="approved" label="已审核" />
            <t-option value="partial" label="部分收货" />
            <t-option value="received" label="已收货" />
            <t-option value="cancelled" label="已取消" />
          </t-select>
        </t-form-item>
        <t-form-item label="付款状态" name="payStatus">
          <t-select
            v-model="searchForm.payStatus"
            placeholder="请选择付款状态"
            clearable
            style="width: 200px"
          >
            <t-option value="unpaid" label="未付款" />
            <t-option value="partial" label="部分付款" />
            <t-option value="paid" label="已付款" />
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
          新增采购单
        </t-button>
        <t-button theme="danger" @click="handleBatchDelete">
          <template #icon><delete-icon /></template>
          批量删除
        </t-button>
        <t-button theme="default" @click="handleExport">
          <template #icon><download-icon /></template>
          导出采购单
        </t-button>
        <t-button theme="default" @click="handlePrint">
          <template #icon><print-icon /></template>
          打印采购单
        </t-button>
      </t-space>
    </t-card>

    <!-- 采购单列表表格 -->
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
        <template #status="{ row }">
          <t-tag v-if="row.status === 'draft'" theme="default">草稿</t-tag>
          <t-tag v-else-if="row.status === 'pending'" theme="warning">
            待审核
          </t-tag>
          <t-tag v-else-if="row.status === 'approved'" theme="success">
            已审核
          </t-tag>
          <t-tag v-else-if="row.status === 'partial'" theme="primary">
            部分收货
          </t-tag>
          <t-tag v-else-if="row.status === 'received'" theme="success">
            已收货
          </t-tag>
          <t-tag v-else theme="danger">已取消</t-tag>
        </template>

        <template #payStatus="{ row }">
          <t-tag v-if="row.payStatus === 'unpaid'" theme="danger">未付款</t-tag>
          <t-tag v-else-if="row.payStatus === 'partial'" theme="warning">
            部分付款
          </t-tag>
          <t-tag v-else theme="success">已付款</t-tag>
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
              v-if="row.status === 'pending'"
              theme="success"
              @click="handleApprove(row)"
            >
              审核
            </t-link>
            <t-link
              v-if="row.status === 'approved' || row.status === 'partial'"
              theme="primary"
              @click="handleReceive(row)"
            >
              收货
            </t-link>
            <t-link theme="primary" @click="handlePrint(row)">打印</t-link>
            <t-popconfirm content="确认删除吗？" @confirm="handleDelete(row)">
              <t-link theme="danger">删除</t-link>
            </t-popconfirm>
          </t-space>
        </template>
      </t-table>
    </t-card>

    <!-- 采购单编辑弹窗 -->
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
                <t-form-item label="采购单号" name="orderNo">
                  <t-input v-model="formData.orderNo" disabled />
                </t-form-item>
              </t-col>
              <t-col :span="8">
                <t-form-item label="采购日期" name="orderDate">
                  <t-date-picker
                    v-model="formData.orderDate"
                    placeholder="请选择采购日期"
                    style="width: 100%"
                  />
                </t-form-item>
              </t-col>
              <t-col :span="8">
                <t-form-item label="采购类型" name="orderType">
                  <t-select
                    v-model="formData.orderType"
                    placeholder="请选择采购类型"
                    style="width: 100%"
                  >
                    <t-option value="normal" label="普通采购" />
                    <t-option value="urgent" label="加急采购" />
                    <t-option value="other" label="其他" />
                  </t-select>
                </t-form-item>
              </t-col>
            </t-row>
            <t-row :gutter="16">
              <t-col :span="8">
                <t-form-item label="供应商" name="supplierId">
                  <t-select
                    v-model="formData.supplierId"
                    placeholder="请选择供应商"
                    filterable
                    style="width: 100%"
                  >
                    <t-option value="1" label="供应商A" />
                    <t-option value="2" label="供应商B" />
                  </t-select>
                </t-form-item>
              </t-col>
              <t-col :span="8">
                <t-form-item label="联系人" name="contactPerson">
                  <t-input v-model="formData.contactPerson" placeholder="请输入联系人" />
                </t-form-item>
              </t-col>
              <t-col :span="8">
                <t-form-item label="联系电话" name="contactPhone">
                  <t-input v-model="formData.contactPhone" placeholder="请输入联系电话" />
                </t-form-item>
              </t-col>
            </t-row>
            <t-row :gutter="16">
              <t-col :span="12">
                <t-form-item label="收货地址" name="shippingAddress">
                  <t-input v-model="formData.shippingAddress" placeholder="请输入收货地址" />
                </t-form-item>
              </t-col>
              <t-col :span="6">
                <t-form-item label="采购部门" name="departmentId">
                  <t-select
                    v-model="formData.departmentId"
                    placeholder="请选择采购部门"
                    style="width: 100%"
                  >
                    <t-option value="1" label="采购一部" />
                    <t-option value="2" label="采购二部" />
                  </t-select>
                </t-form-item>
              </t-col>
              <t-col :span="6">
                <t-form-item label="采购员" name="buyer">
                  <t-input v-model="formData.buyer" placeholder="请输入采购员" />
                </t-form-item>
              </t-col>
            </t-row>
            <t-row :gutter="16">
              <t-col :span="8">
                <t-form-item label="付款方式" name="paymentMethod">
                  <t-select
                    v-model="formData.paymentMethod"
                    placeholder="请选择付款方式"
                    style="width: 100%"
                  >
                    <t-option value="cash" label="现金" />
                    <t-option value="transfer" label="银行转账" />
                    <t-option value="check" label="支票" />
                    <t-option value="other" label="其他" />
                  </t-select>
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
                    <t-option value="EUR" label="欧元" />
                  </t-select>
                </t-form-item>
              </t-col>
              <t-col :span="8">
                <t-form-item label="汇率" name="exchangeRate">
                  <t-input-number
                    v-model="formData.exchangeRate"
                    :min="0"
                    :decimal-places="4"
                    style="width: 100%"
                  />
                </t-form-item>
              </t-col>
            </t-row>
            <t-row :gutter="16">
              <t-col :span="12">
                <t-form-item label="采购备注" name="remark">
                  <t-textarea
                    v-model="formData.remark"
                    placeholder="请输入采购备注"
                    :autosize="{ minRows: 2, maxRows: 4 }"
                  />
                </t-form-item>
              </t-col>
              <t-col :span="12">
                <t-form-item label="内部备注" name="internalRemark">
                  <t-textarea
                    v-model="formData.internalRemark"
                    placeholder="请输入内部备注"
                    :autosize="{ minRows: 2, maxRows: 4 }"
                  />
                </t-form-item>
              </t-col>
            </t-row>
          </t-form>
        </t-tab-panel>

        <t-tab-panel value="products" label="采购商品">
          <t-button theme="primary" class="mb-4" @click="handleSelectProduct">
            <template #icon><add-icon /></template>
            添加商品
          </t-button>
          <t-table
            :data="formData.products"
            :columns="productColumns"
            row-key="productId"
            max-height="400"
          >
            <template #imageUrl="{ row }">
              <t-image
                v-if="row.imageUrl"
                :src="row.imageUrl"
                fit="cover"
                style="width: 40px; height: 40px; border-radius: 4px"
              />
              <div
                v-else
                class="image-placeholder"
                style="width: 40px; height: 40px; background: #f3f3f3"
              ></div>
            </template>
            <template #price="{ row }">
              <t-input-number
                v-model="row.price"
                :min="0"
                :decimal-places="2"
                size="small"
                style="width: 100px"
              />
            </template>
            <template #quantity="{ row }">
              <t-input-number
                v-model="row.quantity"
                :min="1"
                size="small"
                style="width: 100px"
              />
            </template>
            <template #discount="{ row }">
              <t-input-number
                v-model="row.discount"
                :min="0"
                :max="100"
                :decimal-places="2"
                size="small"
                style="width: 100px"
              />
            </template>
            <template #amount="{ row }">
              ¥{{ (row.price * row.quantity * (1 - row.discount / 100)).toFixed(2) }}
            </template>
            <template #operation="{ row }">
              <t-popconfirm
                content="确认移除该商品吗？"
                @confirm="handleRemoveProduct(row)"
              >
                <t-link theme="danger">移除</t-link>
              </t-popconfirm>
            </template>
          </t-table>
          <div class="mt-4 text-right">
            <t-space direction="vertical" align="end">
              <div>商品总数: {{ totalQuantity }}</div>
              <div>商品总金额: ¥{{ totalAmount.toFixed(2) }}</div>
              <div>优惠总额: -¥{{ totalDiscount.toFixed(2) }}</div>
              <div>订单总金额: <span class="text-primary font-bold">¥{{ finalAmount.toFixed(2) }}</span></div>
            </t-space>
          </div>
        </t-tab-panel>

        <t-tab-panel value="settlement" label="结算信息">
          <t-descriptions :column="2" bordered>
            <t-descriptions-item label="采购单号">
              {{ formData.orderNo }}
            </t-descriptions-item>
            <t-descriptions-item label="供应商">
              {{ getSupplierName(formData.supplierId) }}
            </t-descriptions-item>
            <t-descriptions-item label="商品总数">
              {{ totalQuantity }}
            </t-descriptions-item>
            <t-descriptions-item label="商品总金额">
              ¥{{ totalAmount.toFixed(2) }}
            </t-descriptions-item>
            <t-descriptions-item label="优惠金额">
              ¥{{ totalDiscount.toFixed(2) }}
            </t-descriptions-item>
            <t-descriptions-item label="订单总金额">
              ¥{{ finalAmount.toFixed(2) }}
            </t-descriptions-item>
            <t-descriptions-item label="已付金额">
              ¥{{ formData.paidAmount?.toFixed(2) || 0 }}
            </t-descriptions-item>
            <t-descriptions-item label="未付金额">
              <span class="text-danger">¥{{ (finalAmount - (formData.paidAmount || 0)).toFixed(2) }}</span>
            </t-descriptions-item>
          </t-descriptions>
        </t-tab-panel>
      </t-tabs>

      <template #footer>
        <t-button theme="default" @click="handleDialogClose">取消</t-button>
        <t-button theme="default" @click="handleSaveDraft">保存草稿</t-button>
        <t-button theme="primary" @click="handleSubmit">提交审核</t-button>
      </template>
    </t-dialog>

    <!-- 采购单详情弹窗 -->
    <t-dialog
      v-model:visible="detailDialogVisible"
      header="采购单详情"
      width="1100px"
      :footer="false"
    >
      <t-tabs v-model="detailActiveTab">
        <t-tab-panel value="basic" label="基本信息">
          <t-descriptions :column="3" bordered>
            <t-descriptions-item label="采购单号">
              {{ detailData.orderNo }}
            </t-descriptions-item>
            <t-descriptions-item label="采购日期">
              {{ detailData.orderDate }}
            </t-descriptions-item>
            <t-descriptions-item label="采购类型">
              {{ detailData.orderType === 'normal' ? '普通采购' : detailData.orderType === 'urgent' ? '加急采购' : '其他' }}
            </t-descriptions-item>
            <t-descriptions-item label="供应商">
              {{ getSupplierName(detailData.supplierId) }}
            </t-descriptions-item>
            <t-descriptions-item label="联系人">
              {{ detailData.contactPerson }}
            </t-descriptions-item>
            <t-descriptions-item label="联系电话">
              {{ detailData.contactPhone }}
            </t-descriptions-item>
            <t-descriptions-item label="收货地址" :span="3">
              {{ detailData.shippingAddress }}
            </t-descriptions-item>
            <t-descriptions-item label="采购部门">
              {{ getDepartmentName(detailData.departmentId) }}
            </t-descriptions-item>
            <t-descriptions-item label="采购员">
              {{ detailData.buyer }}
            </t-descriptions-item>
            <t-descriptions-item label="付款方式">
              {{ getPaymentMethodName(detailData.paymentMethod) }}
            </t-descriptions-item>
            <t-descriptions-item label="订单状态">
              <t-tag v-if="detailData.status === 'draft'" theme="default">草稿</t-tag>
              <t-tag v-else-if="detailData.status === 'pending'" theme="warning">
                待审核
              </t-tag>
              <t-tag v-else-if="detailData.status === 'approved'" theme="success">
                已审核
              </t-tag>
              <t-tag v-else-if="detailData.status === 'partial'" theme="primary">
                部分收货
              </t-tag>
              <t-tag v-else-if="detailData.status === 'received'" theme="success">
                已收货
              </t-tag>
              <t-tag v-else theme="danger">已取消</t-tag>
            </t-descriptions-item>
            <t-descriptions-item label="付款状态">
              <t-tag v-if="detailData.payStatus === 'unpaid'" theme="danger">未付款</t-tag>
              <t-tag v-else-if="detailData.payStatus === 'partial'" theme="warning">
                部分付款
              </t-tag>
              <t-tag v-else theme="success">已付款</t-tag>
            </t-descriptions-item>
            <t-descriptions-item label="备注" :span="3">
              {{ detailData.remark || '-' }}
            </t-descriptions-item>
          </t-descriptions>
        </t-tab-panel>

        <t-tab-panel value="products" label="采购商品明细">
          <t-table
            :data="detailData.products"
            :columns="detailProductColumns"
            max-height="400"
          >
            <template #imageUrl="{ row }">
              <t-image
                v-if="row.imageUrl"
                :src="row.imageUrl"
                fit="cover"
                style="width: 40px; height: 40px; border-radius: 4px"
              />
            </template>
          </t-table>
        </t-tab-panel>

        <t-tab-panel value="settlement" label="结算信息">
          <t-descriptions :column="2" bordered>
            <t-descriptions-item label="商品总数">
              {{ detailData.totalQuantity }}
            </t-descriptions-item>
            <t-descriptions-item label="商品总金额">
              ¥{{ detailData.totalAmount?.toFixed(2) || 0 }}
            </t-descriptions-item>
            <t-descriptions-item label="优惠金额">
              ¥{{ detailData.totalDiscount?.toFixed(2) || 0 }}
            </t-descriptions-item>
            <t-descriptions-item label="订单总金额">
              ¥{{ detailData.finalAmount?.toFixed(2) || 0 }}
            </t-descriptions-item>
            <t-descriptions-item label="已付金额">
              ¥{{ detailData.paidAmount?.toFixed(2) || 0 }}
            </t-descriptions-item>
            <t-descriptions-item label="未付金额">
              <span class="text-danger">
                ¥{{ ((detailData.finalAmount || 0) - (detailData.paidAmount || 0)).toFixed(2) }}
              </span>
            </t-descriptions-item>
          </t-descriptions>
        </t-tab-panel>
      </t-tabs>
    </t-dialog>

    <!-- 审核弹窗 -->
    <t-dialog
      v-model:visible="approveDialogVisible"
      header="采购单审核"
      width="600px"
      @confirm="handleConfirmApprove"
    >
      <t-form :data="approveForm" label-width="100px">
        <t-form-item label="审核结果" name="approveResult">
          <t-radio-group v-model="approveForm.approveResult">
            <t-radio value="approved">通过</t-radio>
            <t-radio value="rejected">驳回</t-radio>
          </t-radio-group>
        </t-form-item>
        <t-form-item label="审核意见" name="approveComment">
          <t-textarea
            v-model="approveForm.approveComment"
            placeholder="请输入审核意见"
            :autosize="{ minRows: 3, maxRows: 6 }"
          />
        </t-form-item>
      </t-form>
    </t-dialog>

    <!-- 商品选择弹窗 -->
    <t-dialog
      v-model:visible="productSelectDialogVisible"
      header="选择商品"
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
        <t-form-item label="商品编码" name="productCode">
          <t-input
            v-model="productSearchForm.productCode"
            placeholder="请输入商品编码"
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
      >
        <template #imageUrl="{ row }">
          <t-image
            v-if="row.imageUrl"
            :src="row.imageUrl"
            fit="cover"
            style="width: 40px; height: 40px; border-radius: 4px"
          />
        </template>
      </t-table>
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
} from 'tdesign-icons-vue-next'
import { MessagePlugin } from 'tdesign-vue-next'

// 统计数据
const statistics = ref({
  monthlyCount: 78,
  monthlyAmount: 45200.00,
  pendingReceive: 15,
  pendingPay: 12,
})

// 搜索表单
const searchForm = reactive({
  orderNo: '',
  supplierId: '',
  orderDate: [],
  status: '',
  payStatus: '',
})

// 表格数据
const loading = ref(false)
const tableData = ref([
  {
    id: '1',
    orderNo: 'PO20250219001',
    orderDate: '2025-02-19',
    supplierId: '1',
    supplierName: '供应商A',
    orderType: 'normal',
    contactPerson: '张三',
    contactPhone: '13800138000',
    shippingAddress: '广东省深圳市南山区',
    departmentId: '1',
    buyer: '李四',
    paymentMethod: 'transfer',
    currency: 'CNY',
    exchangeRate: 1.0000,
    totalQuantity: 100,
    totalAmount: 5000.00,
    totalDiscount: 250.00,
    finalAmount: 4750.00,
    paidAmount: 0,
    status: 'pending',
    payStatus: 'unpaid',
    remark: '普通采购',
  },
  {
    id: '2',
    orderNo: 'PO20250218001',
    orderDate: '2025-02-18',
    supplierId: '2',
    supplierName: '供应商B',
    orderType: 'urgent',
    contactPerson: '王五',
    contactPhone: '13900139000',
    shippingAddress: '广东省广州市天河区',
    departmentId: '2',
    buyer: '赵六',
    paymentMethod: 'check',
    currency: 'CNY',
    exchangeRate: 1.0000,
    totalQuantity: 50,
    totalAmount: 3000.00,
    totalDiscount: 150.00,
    finalAmount: 2850.00,
    paidAmount: 2850.00,
    status: 'approved',
    payStatus: 'paid',
    remark: '加急采购',
  },
])

// 表格列
const columns = [
  { colKey: 'orderNo', title: '采购单号', width: 150 },
  { colKey: 'orderDate', title: '采购日期', width: 120 },
  { colKey: 'supplierName', title: '供应商', width: 120 },
  { colKey: 'contactPerson', title: '联系人', width: 100 },
  { colKey: 'contactPhone', title: '联系电话', width: 120 },
  { colKey: 'totalQuantity', title: '商品总数', width: 100 },
  { colKey: 'finalAmount', title: '采购金额', width: 120 },
  { colKey: 'buyer', title: '采购员', width: 100 },
  { colKey: 'status', title: '订单状态', width: 100 },
  { colKey: 'payStatus', title: '付款状态', width: 100 },
  { colKey: 'remark', title: '备注', ellipsis: true },
  { colKey: 'operation', title: '操作', width: 280, fixed: 'right' },
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
const dialogTitle = ref('新增采购单')
const activeTab = ref('basic')
const formData = reactive({
  orderNo: '',
  orderDate: '',
  orderType: 'normal',
  supplierId: '',
  contactPerson: '',
  contactPhone: '',
  shippingAddress: '',
  departmentId: '',
  buyer: '',
  paymentMethod: 'transfer',
  currency: 'CNY',
  exchangeRate: 1.0000,
  remark: '',
  internalRemark: '',
  products: [],
  paidAmount: 0,
})

// 商品表格列
const productColumns = [
  { colKey: 'imageUrl', title: '图片', width: 80 },
  { colKey: 'productCode', title: '商品编码', width: 120 },
  { colKey: 'productName', title: '商品名称', width: 150 },
  { colKey: 'categoryName', title: '商品分类', width: 100 },
  { colKey: 'spec', title: '规格', width: 100 },
  { colKey: 'unit', title: '单位', width: 60 },
  { colKey: 'price', title: '单价', width: 120 },
  { colKey: 'quantity', title: '数量', width: 120 },
  { colKey: 'discount', title: '折扣(%)', width: 120 },
  { colKey: 'amount', title: '金额', width: 120 },
  { colKey: 'operation', title: '操作', width: 80 },
]

// 计算合计
const totalQuantity = computed(() => {
  return formData.products.reduce((sum, item) => sum + item.quantity, 0)
})

const totalAmount = computed(() => {
  return formData.products.reduce((sum, item) => sum + item.price * item.quantity, 0)
})

const totalDiscount = computed(() => {
  return formData.products.reduce(
    (sum, item) => sum + item.price * item.quantity * (item.discount / 100),
    0
  )
})

const finalAmount = computed(() => {
  return totalAmount.value - totalDiscount.value
})

// 详情弹窗
const detailDialogVisible = ref(false)
const detailActiveTab = ref('basic')
const detailData = ref({})

const detailProductColumns = [
  { colKey: 'imageUrl', title: '图片', width: 80 },
  { colKey: 'productCode', title: '商品编码', width: 120 },
  { colKey: 'productName', title: '商品名称', width: 150 },
  { colKey: 'categoryName', title: '商品分类', width: 100 },
  { colKey: 'spec', title: '规格', width: 100 },
  { colKey: 'unit', title: '单位', width: 60 },
  { colKey: 'price', title: '单价', width: 100 },
  { colKey: 'quantity', title: '数量', width: 80 },
  { colKey: 'discount', title: '折扣(%)', width: 100 },
  { colKey: 'amount', title: '金额', width: 120 },
]

// 审核弹窗
const approveDialogVisible = ref(false)
const approveForm = reactive({
  approveResult: 'approved',
  approveComment: '',
})
const currentApproveRow = ref(null)

// 商品选择弹窗
const productSelectDialogVisible = ref(false)
const productSearchForm = reactive({
  productName: '',
  productCode: '',
})
const productList = ref([
  {
    id: '1',
    imageUrl: 'https://tdesign.gtimg.com/site/avatar.jpg',
    productCode: 'P001',
    productName: '商品A',
    categoryName: '食品',
    spec: '500g/包',
    unit: '包',
    price: 50.00,
  },
  {
    id: '2',
    imageUrl: '',
    productCode: 'P002',
    productName: '商品B',
    categoryName: '饮料',
    spec: '330ml/瓶',
    unit: '瓶',
    price: 6.00,
  },
])
const selectedProductKeys = ref([])
const productSelectColumns = [
  { colKey: 'imageUrl', title: '图片', width: 80 },
  { colKey: 'productCode', title: '商品编码', width: 120 },
  { colKey: 'productName', title: '商品名称', width: 150 },
  { colKey: 'categoryName', title: '商品分类', width: 100 },
  { colKey: 'spec', title: '规格', width: 100 },
  { colKey: 'unit', title: '单位', width: 60 },
  { colKey: 'price', title: '采购价', width: 100 },
]

// 搜索
const handleSearch = () => {
  MessagePlugin.success('查询成功')
}

const handleReset = () => {
  Object.assign(searchForm, {
    orderNo: '',
    supplierId: '',
    orderDate: [],
    status: '',
    payStatus: '',
  })
}

// 操作
const handleAdd = () => {
  dialogTitle.value = '新增采购单'
  activeTab.value = 'basic'
  const today = new Date().toISOString().split('T')[0]
  Object.assign(formData, {
    orderNo: `PO${Date.now()}`,
    orderDate: today,
    orderType: 'normal',
    supplierId: '',
    contactPerson: '',
    contactPhone: '',
    shippingAddress: '',
    departmentId: '',
    buyer: '',
    paymentMethod: 'transfer',
    currency: 'CNY',
    exchangeRate: 1.0000,
    remark: '',
    internalRemark: '',
    products: [],
    paidAmount: 0,
  })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑采购单'
  activeTab.value = 'basic'
  Object.assign(formData, {
    ...row,
    products: [
      {
        productId: '1',
        imageUrl: 'https://tdesign.gtimg.com/site/avatar.jpg',
        productCode: 'P001',
        productName: '商品A',
        categoryName: '食品',
        spec: '500g/包',
        unit: '包',
        price: 50.00,
        quantity: 10,
        discount: 5,
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
        imageUrl: 'https://tdesign.gtimg.com/site/avatar.jpg',
        productCode: 'P001',
        productName: '商品A',
        categoryName: '食品',
        spec: '500g/包',
        unit: '包',
        price: 50.00,
        quantity: 10,
        discount: 5,
        amount: 475.00,
      },
    ],
    totalQuantity: 10,
    totalAmount: 500.00,
    totalDiscount: 25.00,
    finalAmount: 475.00,
  }
  detailDialogVisible.value = true
}

const handleApprove = (row) => {
  currentApproveRow.value = row
  approveForm.approveResult = 'approved'
  approveForm.approveComment = ''
  approveDialogVisible.value = true
}

const handleConfirmApprove = () => {
  MessagePlugin.success(approveForm.approveResult === 'approved' ? '审核通过' : '审核驳回')
  approveDialogVisible.value = false
}

const handleReceive = (row) => {
  MessagePlugin.success('进入收货流程')
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
  MessagePlugin.success('提交审核成功')
  dialogVisible.value = false
}

// 商品选择
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
        productId: product.id,
        imageUrl: product.imageUrl,
        productCode: product.productCode,
        productName: product.productName,
        categoryName: product.categoryName,
        spec: product.spec,
        unit: product.unit,
        price: product.price,
        quantity: 1,
        discount: 0,
      })
    }
  })
  productSelectDialogVisible.value = false
  selectedProductKeys.value = []
  MessagePlugin.success(`添加 ${selectedProducts.length} 个商品成功`)
}

const handleRemoveProduct = (row) => {
  const index = formData.products.findIndex(p => p.productId === row.productId)
  if (index > -1) {
    formData.products.splice(index, 1)
  }
}

// 工具函数
const getSupplierName = (id) => {
  const map = {
    '1': '供应商A',
    '2': '供应商B',
  }
  return map[id] || id
}

const getDepartmentName = (id) => {
  const map = {
    '1': '采购一部',
    '2': '采购二部',
  }
  return map[id] || id
}

const getPaymentMethodName = (method) => {
  const map = {
    cash: '现金',
    transfer: '银行转账',
    check: '支票',
    other: '其他',
  }
  return map[method] || method
}

onMounted(() => {
  // 初始化数据
})
</script>

<style scoped lang="scss">
.order-container {
  padding: 16px;
}

.text-primary {
  color: var(--td-brand-color);
}

.text-danger {
  color: var(--td-error-color);
}

.font-bold {
  font-weight: bold;
}

.image-placeholder {
  border-radius: 4px;
  display: inline-block;
}
</style>
