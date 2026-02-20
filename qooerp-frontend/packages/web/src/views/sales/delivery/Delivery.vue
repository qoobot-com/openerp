<template>
  <div class="delivery-container">
    <!-- 统计卡片 -->
    <t-row :gutter="16" class="mb-4">
      <t-col :span="6">
        <t-card>
          <t-statistic
            title="本月发货单数"
            :value="statistics.monthlyCount"
            suffix="单"
          />
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card>
          <t-statistic
            title="本月发货金额"
            :value="statistics.monthlyAmount"
            :precision="2"
            prefix="¥"
          />
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card>
          <t-statistic
            title="待发货订单"
            :value="statistics.pendingOrder"
            suffix="单"
          />
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card>
          <t-statistic
            title="待发货商品"
            :value="statistics.pendingProduct"
            suffix="件"
          />
        </t-card>
      </t-col>
    </t-row>

    <!-- 搜索卡片 -->
    <t-card class="mb-4">
      <t-form :data="searchForm" layout="inline" @submit="handleSearch">
        <t-form-item label="发货单号" name="deliveryNo">
          <t-input
            v-model="searchForm.deliveryNo"
            placeholder="请输入发货单号"
            clearable
          />
        </t-form-item>
        <t-form-item label="销售订单" name="orderNo">
          <t-input
            v-model="searchForm.orderNo"
            placeholder="请输入销售订单号"
            clearable
          />
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
        <t-form-item label="发货日期" name="deliveryDate">
          <t-date-range-picker
            v-model="searchForm.deliveryDate"
            clearable
            placeholder="请选择日期范围"
          />
        </t-form-item>
        <t-form-item label="发货状态" name="status">
          <t-select
            v-model="searchForm.status"
            placeholder="请选择状态"
            clearable
            style="width: 200px"
          >
            <t-option value="draft" label="草稿" />
            <t-option value="partial" label="部分发货" />
            <t-option value="shipped" label="已发货" />
            <t-option value="cancelled" label="已取消" />
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
          新增发货单
        </t-button>
        <t-button theme="default" @click="handleCreateFromOrder">
          <template #icon><file-copy-icon /></template>
          从订单创建
        </t-button>
        <t-button theme="danger" @click="handleBatchDelete">
          <template #icon><delete-icon /></template>
          批量删除
        </t-button>
        <t-button theme="default" @click="handleExport">
          <template #icon><download-icon /></template>
          导出报表
        </t-button>
        <t-button theme="default" @click="handlePrint">
          <template #icon><print-icon /></template>
          打印单据
        </t-button>
      </t-space>
    </t-card>

    <!-- 发货单列表表格 -->
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
          <t-tag v-else-if="row.status === 'partial'" theme="warning">
            部分发货
          </t-tag>
          <t-tag v-else-if="row.status === 'shipped'" theme="success">
            已发货
          </t-tag>
          <t-tag v-else theme="danger">已取消</t-tag>
        </template>

        <template #shippingMethod="{ row }">
          <t-tag v-if="row.shippingMethod === 'express'" theme="primary">快递</t-tag>
          <t-tag v-else-if="row.shippingMethod === 'logistics'" theme="success">
            物流
          </t-tag>
          <t-tag v-else-if="row.shippingMethod === 'self'" theme="default">自提</t-tag>
          <t-tag v-else theme="warning">其他</t-tag>
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
              v-if="row.status === 'draft' || row.status === 'partial'"
              theme="success"
              @click="handleShip(row)"
            >
              确认发货
            </t-link>
            <t-link theme="primary" @click="handlePrint(row)">打印</t-link>
            <t-link theme="danger" @click="handleCancel(row)">取消</t-link>
          </t-space>
        </template>
      </t-table>
    </t-card>

    <!-- 发货单编辑弹窗 -->
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
                <t-form-item label="发货单号" name="deliveryNo">
                  <t-input v-model="formData.deliveryNo" disabled />
                </t-form-item>
              </t-col>
              <t-col :span="8">
                <t-form-item label="销售订单" name="orderNo">
                  <t-input v-model="formData.orderNo" placeholder="请输入销售订单号" />
                </t-form-item>
              </t-col>
              <t-col :span="8">
                <t-form-item label="发货日期" name="deliveryDate">
                  <t-date-picker
                    v-model="formData.deliveryDate"
                    placeholder="请选择发货日期"
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
                <t-form-item label="收货人" name="receiverName">
                  <t-input v-model="formData.receiverName" placeholder="请输入收货人" />
                </t-form-item>
              </t-col>
              <t-col :span="8">
                <t-form-item label="联系电话" name="receiverPhone">
                  <t-input v-model="formData.receiverPhone" placeholder="请输入联系电话" />
                </t-form-item>
              </t-col>
            </t-row>
            <t-row :gutter="16">
              <t-col :span="12">
                <t-form-item label="收货地址" name="receiverAddress">
                  <t-input v-model="formData.receiverAddress" placeholder="请输入收货地址" />
                </t-form-item>
              </t-col>
              <t-col :span="6">
                <t-form-item label="发货仓库" name="warehouseId">
                  <t-select
                    v-model="formData.warehouseId"
                    placeholder="请选择仓库"
                    style="width: 100%"
                  >
                    <t-option value="1" label="主仓库" />
                    <t-option value="2" label="2号仓库" />
                  </t-select>
                </t-form-item>
              </t-col>
              <t-col :span="6">
                <t-form-item label="发货方式" name="shippingMethod">
                  <t-select
                    v-model="formData.shippingMethod"
                    placeholder="请选择发货方式"
                    style="width: 100%"
                  >
                    <t-option value="express" label="快递" />
                    <t-option value="logistics" label="物流" />
                    <t-option value="self" label="自提" />
                    <t-option value="other" label="其他" />
                  </t-select>
                </t-form-item>
              </t-col>
            </t-row>
            <t-row :gutter="16">
              <t-col :span="12">
                <t-form-item label="物流公司" name="logisticsCompany">
                  <t-input v-model="formData.logisticsCompany" placeholder="请输入物流公司" />
                </t-form-item>
              </t-col>
              <t-col :span="12">
                <t-form-item label="物流单号" name="trackingNumber">
                  <t-input v-model="formData.trackingNumber" placeholder="请输入物流单号" />
                </t-form-item>
              </t-col>
            </t-row>
            <t-form-item label="发货备注" name="remark">
              <t-textarea
                v-model="formData.remark"
                placeholder="请输入发货备注"
                :autosize="{ minRows: 2, maxRows: 4 }"
              />
            </t-form-item>
          </t-form>
        </t-tab-panel>

        <t-tab-panel value="products" label="发货商品">
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
            <template #shipQuantity="{ row }">
              <t-input-number
                v-model="row.shipQuantity"
                :min="0"
                :max="row.orderQuantity - row.shippedQuantity"
                size="small"
                style="width: 100px"
              />
            </template>
            <template #amount="{ row }">
              ¥{{ (row.price * row.shipQuantity).toFixed(2) }}
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
              <div>发货商品数: {{ totalQuantity }}</div>
              <div>发货总金额: ¥{{ totalAmount.toFixed(2) }}</div>
            </t-space>
          </div>
        </t-tab-panel>

        <t-tab-panel value="summary" label="汇总信息">
          <t-descriptions :column="2" bordered>
            <t-descriptions-item label="发货单号">
              {{ formData.deliveryNo }}
            </t-descriptions-item>
            <t-descriptions-item label="销售订单">
              {{ formData.orderNo }}
            </t-descriptions-item>
            <t-descriptions-item label="发货日期">
              {{ formData.deliveryDate }}
            </t-descriptions-item>
            <t-descriptions-item label="客户">
              {{ getCustomerName(formData.customerId) }}
            </t-descriptions-item>
            <t-descriptions-item label="发货仓库">
              {{ getWarehouseName(formData.warehouseId) }}
            </t-descriptions-item>
            <t-descriptions-item label="发货方式">
              {{ getShippingMethodName(formData.shippingMethod) }}
            </t-descriptions-item>
            <t-descriptions-item label="发货商品数">
              {{ totalQuantity }}
            </t-descriptions-item>
            <t-descriptions-item label="发货总金额">
              ¥{{ totalAmount.toFixed(2) }}
            </t-descriptions-item>
            <t-descriptions-item label="物流公司" :span="2">
              {{ formData.logisticsCompany || '-' }}
            </t-descriptions-item>
            <t-descriptions-item label="物流单号" :span="2">
              {{ formData.trackingNumber || '-' }}
            </t-descriptions-item>
            <t-descriptions-item label="发货备注" :span="2">
              {{ formData.remark || '-' }}
            </t-descriptions-item>
          </t-descriptions>
        </t-tab-panel>
      </t-tabs>

      <template #footer>
        <t-button theme="default" @click="handleDialogClose">取消</t-button>
        <t-button theme="default" @click="handleSaveDraft">保存草稿</t-button>
        <t-button theme="primary" @click="handleSubmit">确认发货</t-button>
      </template>
    </t-dialog>

    <!-- 发货单详情弹窗 -->
    <t-dialog
      v-model:visible="detailDialogVisible"
      header="发货单详情"
      width="1100px"
      :footer="false"
    >
      <t-tabs v-model="detailActiveTab">
        <t-tab-panel value="basic" label="基本信息">
          <t-descriptions :column="3" bordered>
            <t-descriptions-item label="发货单号">
              {{ detailData.deliveryNo }}
            </t-descriptions-item>
            <t-descriptions-item label="销售订单">
              {{ detailData.orderNo }}
            </t-descriptions-item>
            <t-descriptions-item label="发货日期">
              {{ detailData.deliveryDate }}
            </t-descriptions-item>
            <t-descriptions-item label="客户">
              {{ getCustomerName(detailData.customerId) }}
            </t-descriptions-item>
            <t-descriptions-item label="收货人">
              {{ detailData.receiverName }}
            </t-descriptions-item>
            <t-descriptions-item label="联系电话">
              {{ detailData.receiverPhone }}
            </t-descriptions-item>
            <t-descriptions-item label="收货地址" :span="3">
              {{ detailData.receiverAddress }}
            </t-descriptions-item>
            <t-descriptions-item label="发货仓库">
              {{ getWarehouseName(detailData.warehouseId) }}
            </t-descriptions-item>
            <t-descriptions-item label="发货方式">
              {{ getShippingMethodName(detailData.shippingMethod) }}
            </t-descriptions-item>
            <t-descriptions-item label="发货状态">
              <t-tag v-if="detailData.status === 'draft'" theme="default">草稿</t-tag>
              <t-tag v-else-if="detailData.status === 'partial'" theme="warning">
                部分发货
              </t-tag>
              <t-tag v-else-if="detailData.status === 'shipped'" theme="success">
                已发货
              </t-tag>
              <t-tag v-else theme="danger">已取消</t-tag>
            </t-descriptions-item>
            <t-descriptions-item label="物流公司" :span="3">
              {{ detailData.logisticsCompany || '-' }}
            </t-descriptions-item>
            <t-descriptions-item label="物流单号" :span="3">
              {{ detailData.trackingNumber || '-' }}
            </t-descriptions-item>
            <t-descriptions-item label="发货备注" :span="3">
              {{ detailData.remark || '-' }}
            </t-descriptions-item>
          </t-descriptions>
        </t-tab-panel>

        <t-tab-panel value="products" label="发货商品明细">
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

        <t-tab-panel value="summary" label="汇总信息">
          <t-descriptions :column="2" bordered>
            <t-descriptions-item label="发货商品数">
              {{ detailData.totalQuantity }}
            </t-descriptions-item>
            <t-descriptions-item label="发货总金额">
              ¥{{ detailData.totalAmount?.toFixed(2) || 0 }}
            </t-descriptions-item>
            <t-descriptions-item label="包装数量">
              {{ detailData.packageCount }} 箱
            </t-descriptions-item>
            <t-descriptions-item label="总重量">
              {{ detailData.totalWeight }} kg
            </t-descriptions-item>
          </t-descriptions>
        </t-tab-panel>
      </t-tabs>
    </t-dialog>

    <!-- 确认发货弹窗 -->
    <t-dialog
      v-model:visible="shipDialogVisible"
      header="确认发货"
      width="600px"
      @confirm="handleConfirmShip"
    >
      <t-form :data="shipForm" label-width="100px">
        <t-form-item label="发货方式" name="shippingMethod">
          <t-select
            v-model="shipForm.shippingMethod"
            placeholder="请选择发货方式"
            style="width: 100%"
          >
            <t-option value="express" label="快递" />
            <t-option value="logistics" label="物流" />
            <t-option value="self" label="自提" />
          </t-select>
        </t-form-item>
        <t-form-item label="物流公司" name="logisticsCompany">
          <t-input v-model="shipForm.logisticsCompany" placeholder="请输入物流公司" />
        </t-form-item>
        <t-form-item label="物流单号" name="trackingNumber">
          <t-input v-model="shipForm.trackingNumber" placeholder="请输入物流单号" />
        </t-form-item>
        <t-form-item label="包装数量" name="packageCount">
          <t-input-number v-model="shipForm.packageCount" :min="0" style="width: 100%" />
        </t-form-item>
        <t-form-item label="总重量" name="totalWeight">
          <t-input-number v-model="shipForm.totalWeight" :min="0" :decimal-places="2" style="width: 100%" />
        </t-form-item>
      </t-form>
    </t-dialog>

    <!-- 从订单创建弹窗 -->
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
          <t-tag v-else-if="row.status === 'partial'" theme="primary">部分发货</t-tag>
          <t-tag v-else theme="default">{{ row.status }}</t-tag>
        </template>
      </t-table>
    </t-dialog>

    <!-- 商品选择弹窗 -->
    <t-dialog
      v-model:visible="productSelectDialogVisible"
      header="选择商品"
      width="900px"
      :confirm-btn="null"
      @close="handleProductSelectClose"
    >
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
  FileCopyIcon,
} from 'tdesign-icons-vue-next'
import { MessagePlugin } from 'tdesign-vue-next'

// 统计数据
const statistics = ref({
  monthlyCount: 89,
  monthlyAmount: 65800.00,
  pendingOrder: 23,
  pendingProduct: 156,
})

// 搜索表单
const searchForm = reactive({
  deliveryNo: '',
  orderNo: '',
  customerId: '',
  deliveryDate: [],
  status: '',
})

// 表格数据
const loading = ref(false)
const tableData = ref([
  {
    id: '1',
    deliveryNo: 'DL20250219001',
    orderNo: 'SO20250219001',
    deliveryDate: '2025-02-19',
    customerId: '1',
    customerName: '客户A',
    receiverName: '张三',
    receiverPhone: '13800138000',
    receiverAddress: '广东省深圳市南山区',
    warehouseId: '1',
    shippingMethod: 'express',
    logisticsCompany: '顺丰速运',
    trackingNumber: 'SF123456789',
    totalQuantity: 50,
    totalAmount: 2500.00,
    status: 'shipped',
    remark: '正常发货',
  },
  {
    id: '2',
    deliveryNo: 'DL20250218001',
    orderNo: 'SO20250218001',
    deliveryDate: '2025-02-18',
    customerId: '2',
    customerName: '客户B',
    receiverName: '王五',
    receiverPhone: '13900139000',
    receiverAddress: '广东省广州市天河区',
    warehouseId: '2',
    shippingMethod: 'logistics',
    logisticsCompany: '德邦物流',
    trackingNumber: 'DP987654321',
    totalQuantity: 30,
    totalAmount: 1500.00,
    status: 'draft',
    remark: '部分发货',
  },
])

// 表格列
const columns = [
  { colKey: 'deliveryNo', title: '发货单号', width: 150 },
  { colKey: 'orderNo', title: '销售订单', width: 150 },
  { colKey: 'deliveryDate', title: '发货日期', width: 120 },
  { colKey: 'customerName', title: '客户', width: 120 },
  { colKey: 'receiverName', title: '收货人', width: 100 },
  { colKey: 'shippingMethod', title: '发货方式', width: 100 },
  { colKey: 'logisticsCompany', title: '物流公司', width: 120 },
  { colKey: 'trackingNumber', title: '物流单号', width: 140 },
  { colKey: 'totalQuantity', title: '发货数量', width: 100 },
  { colKey: 'totalAmount', title: '发货金额', width: 120 },
  { colKey: 'status', title: '状态', width: 100 },
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
const dialogTitle = ref('新增发货单')
const activeTab = ref('basic')
const formData = reactive({
  deliveryNo: '',
  orderNo: '',
  deliveryDate: '',
  customerId: '',
  receiverName: '',
  receiverPhone: '',
  receiverAddress: '',
  warehouseId: '1',
  shippingMethod: 'express',
  logisticsCompany: '',
  trackingNumber: '',
  remark: '',
  products: [],
})

// 商品表格列
const productColumns = [
  { colKey: 'imageUrl', title: '图片', width: 80 },
  { colKey: 'productCode', title: '商品编码', width: 120 },
  { colKey: 'productName', title: '商品名称', width: 150 },
  { colKey: 'categoryName', title: '商品分类', width: 100 },
  { colKey: 'spec', title: '规格', width: 100 },
  { colKey: 'unit', title: '单位', width: 60 },
  { colKey: 'price', title: '单价', width: 100 },
  { colKey: 'orderQuantity', title: '订单数量', width: 100 },
  { colKey: 'shippedQuantity', title: '已发数量', width: 100 },
  { colKey: 'shipQuantity', title: '本次发货', width: 130 },
  { colKey: 'amount', title: '金额', width: 120 },
  { colKey: 'operation', title: '操作', width: 80 },
]

// 计算合计
const totalQuantity = computed(() => {
  return formData.products.reduce((sum, item) => sum + item.shipQuantity, 0)
})

const totalAmount = computed(() => {
  return formData.products.reduce((sum, item) => sum + item.price * item.shipQuantity, 0)
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
  { colKey: 'shipQuantity', title: '发货数量', width: 100 },
  { colKey: 'amount', title: '金额', width: 120 },
]

// 确认发货弹窗
const shipDialogVisible = ref(false)
const shipForm = reactive({
  shippingMethod: 'express',
  logisticsCompany: '',
  trackingNumber: '',
  packageCount: 0,
  totalWeight: 0,
})
const currentShipRow = ref(null)

// 订单选择弹窗
const orderSelectDialogVisible = ref(false)
const orderList = ref([
  {
    id: '1',
    orderNo: 'SO20250219001',
    orderDate: '2025-02-19',
    customerName: '客户A',
    totalQuantity: 100,
    finalAmount: 5000.00,
    status: 'approved',
  },
  {
    id: '2',
    orderNo: 'SO20250218001',
    orderDate: '2025-02-18',
    customerName: '客户B',
    totalQuantity: 50,
    finalAmount: 3000.00,
    status: 'partial',
  },
])
const orderSelectColumns = [
  { colKey: 'orderNo', title: '订单编号', width: 150 },
  { colKey: 'orderDate', title: '订单日期', width: 120 },
  { colKey: 'customerName', title: '客户', width: 120 },
  { colKey: 'totalQuantity', title: '商品总数', width: 100 },
  { colKey: 'finalAmount', title: '订单金额', width: 120 },
  { colKey: 'status', title: '状态', width: 100 },
]

// 商品选择弹窗
const productSelectDialogVisible = ref(false)
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
    stock: 200,
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
    stock: 500,
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
  { colKey: 'price', title: '销售价', width: 100 },
  { colKey: 'stock', title: '库存', width: 80 },
]

// 搜索
const handleSearch = () => {
  MessagePlugin.success('查询成功')
}

const handleReset = () => {
  Object.assign(searchForm, {
    deliveryNo: '',
    orderNo: '',
    customerId: '',
    deliveryDate: [],
    status: '',
  })
}

// 操作
const handleAdd = () => {
  dialogTitle.value = '新增发货单'
  activeTab.value = 'basic'
  const today = new Date().toISOString().split('T')[0]
  Object.assign(formData, {
    deliveryNo: `DL${Date.now()}`,
    orderNo: '',
    deliveryDate: today,
    customerId: '',
    receiverName: '',
    receiverPhone: '',
    receiverAddress: '',
    warehouseId: '1',
    shippingMethod: 'express',
    logisticsCompany: '',
    trackingNumber: '',
    remark: '',
    products: [],
  })
  dialogVisible.value = true
}

const handleCreateFromOrder = () => {
  orderSelectDialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑发货单'
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
        orderQuantity: 10,
        shippedQuantity: 5,
        shipQuantity: 5,
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
        shipQuantity: 5,
        amount: 250.00,
      },
    ],
    totalQuantity: 5,
    totalAmount: 250.00,
    packageCount: 1,
    totalWeight: 2.5,
  }
  detailDialogVisible.value = true
}

const handleShip = (row) => {
  currentShipRow.value = row
  shipForm.shippingMethod = row.shippingMethod || 'express'
  shipForm.logisticsCompany = row.logisticsCompany || ''
  shipForm.trackingNumber = row.trackingNumber || ''
  shipForm.packageCount = 0
  shipForm.totalWeight = 0
  shipDialogVisible.value = true
}

const handleConfirmShip = () => {
  MessagePlugin.success('确认发货成功')
  shipDialogVisible.value = false
}

const handleCancel = (row) => {
  MessagePlugin.success('取消成功')
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
  MessagePlugin.success('确认发货成功')
  dialogVisible.value = false
}

// 订单选择
const handleOrderRowClick = (row) => {
  dialogTitle.value = '新增发货单'
  activeTab.value = 'basic'
  const today = new Date().toISOString().split('T')[0]
  Object.assign(formData, {
    deliveryNo: `DL${Date.now()}`,
    orderNo: row.orderNo,
    deliveryDate: today,
    customerId: row.customerId === '客户A' ? '1' : '2',
    receiverName: '张三',
    receiverPhone: '13800138000',
    receiverAddress: '广东省深圳市南山区',
    warehouseId: '1',
    shippingMethod: 'express',
    logisticsCompany: '',
    trackingNumber: '',
    remark: '',
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
        orderQuantity: 10,
        shippedQuantity: 0,
        shipQuantity: 5,
      },
    ],
  })
  orderSelectDialogVisible.value = false
  dialogVisible.value = true
}

const handleOrderSelectClose = () => {
  orderSelectDialogVisible.value = false
}

// 商品选择
const handleSelectProduct = () => {
  productSelectDialogVisible.value = true
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
        orderQuantity: product.stock,
        shippedQuantity: 0,
        shipQuantity: 0,
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
const getCustomerName = (id) => {
  const map = {
    '1': '客户A',
    '2': '客户B',
  }
  return map[id] || id
}

const getWarehouseName = (id) => {
  const map = {
    '1': '主仓库',
    '2': '2号仓库',
  }
  return map[id] || id
}

const getShippingMethodName = (method) => {
  const map = {
    express: '快递',
    logistics: '物流',
    self: '自提',
    other: '其他',
  }
  return map[method] || method
}

onMounted(() => {
  // 初始化数据
})
</script>

<style scoped lang="scss">
.delivery-container {
  padding: 16px;
}

.image-placeholder {
  border-radius: 4px;
  display: inline-block;
}
</style>
