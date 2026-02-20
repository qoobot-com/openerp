<template>
  <div class="stocktake-container">
    <!-- 统计卡片 -->
    <t-row :gutter="16" class="mb-4">
      <t-col :span="6">
        <t-card>
          <t-statistic
            title="本月盘点单数"
            :value="statistics.monthlyCount"
            suffix="单"
          />
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card>
          <t-statistic
            title="盘点商品数"
            :value="statistics.productCount"
            suffix="件"
          />
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card>
          <t-statistic
            title="盘盈金额"
            :value="statistics.profitAmount"
            :precision="2"
            prefix="¥"
            theme="success"
          />
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card>
          <t-statistic
            title="盘亏金额"
            :value="statistics.lossAmount"
            :precision="2"
            prefix="¥"
            theme="danger"
          />
        </t-card>
      </t-col>
    </t-row>

    <!-- 搜索卡片 -->
    <t-card class="mb-4">
      <t-form :data="searchForm" layout="inline" @submit="handleSearch">
        <t-form-item label="盘点单号" name="stocktakeNo">
          <t-input
            v-model="searchForm.stocktakeNo"
            placeholder="请输入盘点单号"
            clearable
          />
        </t-form-item>
        <t-form-item label="盘点类型" name="stocktakeType">
          <t-select
            v-model="searchForm.stocktakeType"
            placeholder="请选择盘点类型"
            clearable
            style="width: 200px"
          >
            <t-option value="full" label="全盘" />
            <t-option value="partial" label="部分盘点" />
            <t-option value="category" label="分类盘点" />
          </t-select>
        </t-form-item>
        <t-form-item label="盘点仓库" name="warehouseId">
          <t-select
            v-model="searchForm.warehouseId"
            placeholder="请选择仓库"
            clearable
            style="width: 200px"
          >
            <t-option value="1" label="主仓库" />
            <t-option value="2" label="2号仓库" />
            <t-option value="3" label="3号仓库" />
          </t-select>
        </t-form-item>
        <t-form-item label="盘点日期" name="stocktakeDate">
          <t-date-range-picker
            v-model="searchForm.stocktakeDate"
            clearable
            placeholder="请选择日期范围"
          />
        </t-form-item>
        <t-form-item label="盘点状态" name="status">
          <t-select
            v-model="searchForm.status"
            placeholder="请选择状态"
            clearable
            style="width: 200px"
          >
            <t-option value="draft" label="草稿" />
            <t-option value="pending" label="待审核" />
            <t-option value="completed" label="已完成" />
            <t-option value="rejected" label="已驳回" />
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
          新增盘点
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
          打印报表
        </t-button>
      </t-space>
    </t-card>

    <!-- 盘点单列表表格 -->
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
        <template #stocktakeType="{ row }">
          <t-tag v-if="row.stocktakeType === 'full'" theme="primary">全盘</t-tag>
          <t-tag v-else-if="row.stocktakeType === 'partial'" theme="success">
            部分盘点
          </t-tag>
          <t-tag v-else theme="warning">分类盘点</t-tag>
        </template>

        <template #status="{ row }">
          <t-tag v-if="row.status === 'draft'" theme="default">草稿</t-tag>
          <t-tag v-else-if="row.status === 'pending'" theme="warning">
            待审核
          </t-tag>
          <t-tag v-else-if="row.status === 'completed'" theme="success">
            已完成
          </t-tag>
          <t-tag v-else theme="danger">已驳回</t-tag>
        </template>

        <template #profitStatus="{ row }">
          <t-tag v-if="row.profitStatus === 'balanced'" theme="success">
            账实相符
          </t-tag>
          <t-tag v-else-if="row.profitStatus === 'profit'" theme="warning">
            盘盈
          </t-tag>
          <t-tag v-else theme="danger">盘亏</t-tag>
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
            <t-link theme="primary" @click="handlePrint(row)">打印</t-link>
            <t-popconfirm content="确认删除吗？" @confirm="handleDelete(row)">
              <t-link theme="danger">删除</t-link>
            </t-popconfirm>
          </t-space>
        </template>
      </t-table>
    </t-card>

    <!-- 盘点编辑弹窗 -->
    <t-dialog
      v-model:visible="dialogVisible"
      :header="dialogTitle"
      width="1000px"
      :confirm-btn="null"
      @close="handleDialogClose"
    >
      <t-tabs v-model="activeTab">
        <t-tab-panel value="basic" label="基本信息">
          <t-form :data="formData" label-width="100px">
            <t-form-item label="盘点单号" name="stocktakeNo">
              <t-input v-model="formData.stocktakeNo" disabled />
            </t-form-item>
            <t-form-item label="盘点类型" name="stocktakeType">
              <t-radio-group v-model="formData.stocktakeType">
                <t-radio value="full">全盘</t-radio>
                <t-radio value="partial">部分盘点</t-radio>
                <t-radio value="category">分类盘点</t-radio>
              </t-radio-group>
            </t-form-item>
            <t-form-item label="盘点日期" name="stocktakeDate">
              <t-date-picker
                v-model="formData.stocktakeDate"
                placeholder="请选择盘点日期"
              />
            </t-form-item>
            <t-form-item label="盘点仓库" name="warehouseId">
              <t-select
                v-model="formData.warehouseId"
                placeholder="请选择仓库"
                style="width: 100%"
              >
                <t-option value="1" label="主仓库" />
                <t-option value="2" label="2号仓库" />
                <t-option value="3" label="3号仓库" />
              </t-select>
            </t-form-item>
            <t-form-item label="盘点人" name="stocktakePerson">
              <t-input v-model="formData.stocktakePerson" placeholder="请输入盘点人" />
            </t-form-item>
            <t-form-item label="备注" name="remark">
              <t-textarea
                v-model="formData.remark"
                placeholder="请输入备注"
                :autosize="{ minRows: 3, maxRows: 6 }"
              />
            </t-form-item>
          </t-form>
        </t-tab-panel>

        <t-tab-panel value="products" label="盘点商品">
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
            <template #realStock="{ row }">
              <t-input-number
                v-model="row.realStock"
                :min="0"
                size="small"
                style="width: 100px"
              />
            </template>
            <template #profitLossQty="{ row }">
              <span :class="row.profitLossQty > 0 ? 'text-success' : row.profitLossQty < 0 ? 'text-danger' : ''">
                {{ row.profitLossQty }}
              </span>
            </template>
            <template #profitLossAmount="{ row }">
              <span :class="row.profitLossAmount > 0 ? 'text-success' : row.profitLossAmount < 0 ? 'text-danger' : ''">
                ¥{{ row.profitLossAmount.toFixed(2) }}
              </span>
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
              <div>盘点商品数: {{ formData.products.length }}</div>
              <div>
                盘盈数量:
                <span class="text-success">{{ profitQty }}</span>
              </div>
              <div>
                盘亏数量:
                <span class="text-danger">{{ lossQty }}</span>
              </div>
              <div>
                盘盈金额:
                <span class="text-success">¥{{ profitAmount.toFixed(2) }}</span>
              </div>
              <div>
                盘亏金额:
                <span class="text-danger">¥{{ lossAmount.toFixed(2) }}</span>
              </div>
            </t-space>
          </div>
        </t-tab-panel>

        <t-tab-panel value="summary" label="汇总信息">
          <t-descriptions :column="2" bordered>
            <t-descriptions-item label="盘点单号">
              {{ formData.stocktakeNo }}
            </t-descriptions-item>
            <t-descriptions-item label="盘点类型">
              {{ getStocktakeTypeName(formData.stocktakeType) }}
            </t-descriptions-item>
            <t-descriptions-item label="盘点日期">
              {{ formData.stocktakeDate }}
            </t-descriptions-item>
            <t-descriptions-item label="盘点仓库">
              {{ getWarehouseName(formData.warehouseId) }}
            </t-descriptions-item>
            <t-descriptions-item label="盘点商品数">
              {{ formData.products.length }}
            </t-descriptions-item>
            <t-descriptions-item label="盘点人">
              {{ formData.stocktakePerson }}
            </t-descriptions-item>
            <t-descriptions-item label="盘盈数量">
              <span class="text-success">{{ profitQty }}</span>
            </t-descriptions-item>
            <t-descriptions-item label="盘亏数量">
              <span class="text-danger">{{ lossQty }}</span>
            </t-descriptions-item>
            <t-descriptions-item label="盘盈金额">
              <span class="text-success">¥{{ profitAmount.toFixed(2) }}</span>
            </t-descriptions-item>
            <t-descriptions-item label="盘亏金额">
              <span class="text-danger">¥{{ lossAmount.toFixed(2) }}</span>
            </t-descriptions-item>
            <t-descriptions-item label="净盈亏金额" :span="2">
              <span :class="netAmount > 0 ? 'text-success' : netAmount < 0 ? 'text-danger' : ''">
                ¥{{ netAmount.toFixed(2) }}
              </span>
            </t-descriptions-item>
            <t-descriptions-item label="备注" :span="2">
              {{ formData.remark || '-' }}
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

    <!-- 盘点单详情弹窗 -->
    <t-dialog
      v-model:visible="detailDialogVisible"
      header="盘点单详情"
      width="1000px"
      :footer="false"
    >
      <t-tabs v-model="detailActiveTab">
        <t-tab-panel value="basic" label="基本信息">
          <t-descriptions :column="2" bordered>
            <t-descriptions-item label="盘点单号">
              {{ detailData.stocktakeNo }}
            </t-descriptions-item>
            <t-descriptions-item label="盘点类型">
              {{ getStocktakeTypeName(detailData.stocktakeType) }}
            </t-descriptions-item>
            <t-descriptions-item label="盘点日期">
              {{ detailData.stocktakeDate }}
            </t-descriptions-item>
            <t-descriptions-item label="盘点仓库">
              {{ getWarehouseName(detailData.warehouseId) }}
            </t-descriptions-item>
            <t-descriptions-item label="盘点商品数">
              {{ detailData.products?.length || 0 }}
            </t-descriptions-item>
            <t-descriptions-item label="盘点人">
              {{ detailData.stocktakePerson }}
            </t-descriptions-item>
            <t-descriptions-item label="盘点状态">
              <t-tag v-if="detailData.status === 'draft'" theme="default">草稿</t-tag>
              <t-tag v-else-if="detailData.status === 'pending'" theme="warning">
                待审核
              </t-tag>
              <t-tag v-else-if="detailData.status === 'completed'" theme="success">
                已完成
              </t-tag>
              <t-tag v-else theme="danger">已驳回</t-tag>
            </t-descriptions-item>
            <t-descriptions-item label="盘点结果">
              <t-tag v-if="detailData.profitStatus === 'balanced'" theme="success">
                账实相符
              </t-tag>
              <t-tag v-else-if="detailData.profitStatus === 'profit'" theme="warning">
                盘盈
              </t-tag>
              <t-tag v-else theme="danger">盘亏</t-tag>
            </t-descriptions-item>
            <t-descriptions-item label="盘盈数量">
              <span class="text-success">{{ detailData.profitQty || 0 }}</span>
            </t-descriptions-item>
            <t-descriptions-item label="盘亏数量">
              <span class="text-danger">{{ detailData.lossQty || 0 }}</span>
            </t-descriptions-item>
            <t-descriptions-item label="盘盈金额">
              <span class="text-success">¥{{ (detailData.profitAmount || 0).toFixed(2) }}</span>
            </t-descriptions-item>
            <t-descriptions-item label="盘亏金额">
              <span class="text-danger">¥{{ (detailData.lossAmount || 0).toFixed(2) }}</span>
            </t-descriptions-item>
            <t-descriptions-item label="备注" :span="2">
              {{ detailData.remark || '-' }}
            </t-descriptions-item>
          </t-descriptions>
        </t-tab-panel>

        <t-tab-panel value="products" label="盘点商品明细">
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
            <template #profitLossQty="{ row }">
              <span :class="row.profitLossQty > 0 ? 'text-success' : row.profitLossQty < 0 ? 'text-danger' : ''">
                {{ row.profitLossQty }}
              </span>
            </template>
            <template #profitLossAmount="{ row }">
              <span :class="row.profitLossAmount > 0 ? 'text-success' : row.profitLossAmount < 0 ? 'text-danger' : ''">
                ¥{{ row.profitLossAmount.toFixed(2) }}
              </span>
            </template>
          </t-table>
        </t-tab-panel>
      </t-tabs>
    </t-dialog>

    <!-- 盘点审核弹窗 -->
    <t-dialog
      v-model:visible="approveDialogVisible"
      header="盘点单审核"
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
  monthlyCount: 12,
  productCount: 156,
  profitAmount: 2350.00,
  lossAmount: 1580.00,
})

// 搜索表单
const searchForm = reactive({
  stocktakeNo: '',
  stocktakeType: '',
  warehouseId: '',
  stocktakeDate: [],
  status: '',
})

// 表格数据
const loading = ref(false)
const tableData = ref([
  {
    id: '1',
    stocktakeNo: 'ST20250219001',
    stocktakeType: 'full',
    stocktakeDate: '2025-02-19',
    warehouseId: '1',
    stocktakePerson: '张三',
    productCount: 45,
    profitQty: 5,
    lossQty: 3,
    profitAmount: 1200.00,
    lossAmount: 850.00,
    profitStatus: 'profit',
    status: 'completed',
    remark: '月度全盘',
  },
  {
    id: '2',
    stocktakeNo: 'ST20250218001',
    stocktakeType: 'partial',
    stocktakeDate: '2025-02-18',
    warehouseId: '2',
    stocktakePerson: '李四',
    productCount: 12,
    profitQty: 2,
    lossQty: 0,
    profitAmount: 580.00,
    lossAmount: 0,
    profitStatus: 'profit',
    status: 'pending',
    remark: '部分商品盘点',
  },
])

// 表格列
const columns = [
  { colKey: 'stocktakeNo', title: '盘点单号', width: 150 },
  { colKey: 'stocktakeType', title: '盘点类型', width: 100 },
  { colKey: 'stocktakeDate', title: '盘点日期', width: 120 },
  { colKey: 'warehouseName', title: '盘点仓库', width: 120 },
  { colKey: 'stocktakePerson', title: '盘点人', width: 100 },
  { colKey: 'productCount', title: '盘点商品数', width: 100 },
  { colKey: 'profitLossQty', title: '盈亏数量', width: 100 },
  { colKey: 'profitLossAmount', title: '盈亏金额', width: 120 },
  { colKey: 'profitStatus', title: '盘点结果', width: 100 },
  { colKey: 'status', title: '盘点状态', width: 100 },
  { colKey: 'remark', title: '备注', ellipsis: true },
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
const dialogTitle = ref('新增盘点单')
const activeTab = ref('basic')
const formData = reactive({
  stocktakeNo: '',
  stocktakeType: 'full',
  stocktakeDate: '',
  warehouseId: '1',
  stocktakePerson: '',
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
  { colKey: 'bookStock', title: '账面库存', width: 100 },
  { colKey: 'realStock', title: '实盘库存', width: 120 },
  { colKey: 'profitLossQty', title: '盈亏数量', width: 100 },
  { colKey: 'profitLossAmount', title: '盈亏金额', width: 120 },
  { colKey: 'operation', title: '操作', width: 80 },
]

// 计算盈亏
const profitQty = computed(() => {
  return formData.products.reduce((sum, item) => sum + (item.profitLossQty > 0 ? item.profitLossQty : 0), 0)
})

const lossQty = computed(() => {
  return formData.products.reduce((sum, item) => sum + (item.profitLossQty < 0 ? Math.abs(item.profitLossQty) : 0), 0)
})

const profitAmount = computed(() => {
  return formData.products.reduce((sum, item) => sum + (item.profitLossAmount > 0 ? item.profitLossAmount : 0), 0)
})

const lossAmount = computed(() => {
  return formData.products.reduce((sum, item) => sum + (item.profitLossAmount < 0 ? Math.abs(item.profitLossAmount) : 0), 0)
})

const netAmount = computed(() => {
  return profitAmount.value - lossAmount.value
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
  { colKey: 'bookStock', title: '账面库存', width: 100 },
  { colKey: 'realStock', title: '实盘库存', width: 100 },
  { colKey: 'profitLossQty', title: '盈亏数量', width: 100 },
  { colKey: 'profitLossAmount', title: '盈亏金额', width: 120 },
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
    bookStock: 100,
    costPrice: 10.00,
  },
  {
    id: '2',
    imageUrl: '',
    productCode: 'P002',
    productName: '商品B',
    categoryName: '饮料',
    spec: '330ml/瓶',
    unit: '瓶',
    bookStock: 200,
    costPrice: 5.00,
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
  { colKey: 'bookStock', title: '账面库存', width: 100 },
  { colKey: 'costPrice', title: '成本价', width: 100 },
]

// 搜索
const handleSearch = () => {
  MessagePlugin.success('查询成功')
}

const handleReset = () => {
  Object.assign(searchForm, {
    stocktakeNo: '',
    stocktakeType: '',
    warehouseId: '',
    stocktakeDate: [],
    status: '',
  })
}

// 操作
const handleAdd = () => {
  dialogTitle.value = '新增盘点单'
  activeTab.value = 'basic'
  Object.assign(formData, {
    stocktakeNo: `ST${Date.now()}`,
    stocktakeType: 'full',
    stocktakeDate: '',
    warehouseId: '1',
    stocktakePerson: '',
    remark: '',
    products: [],
  })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑盘点单'
  activeTab.value = 'basic'
  Object.assign(formData, {
    ...row,
    products: [
      {
        imageUrl: 'https://tdesign.gtimg.com/site/avatar.jpg',
        productCode: 'P001',
        productName: '商品A',
        categoryName: '食品',
        spec: '500g/包',
        unit: '包',
        bookStock: 100,
        realStock: 105,
        profitLossQty: 5,
        profitLossAmount: 50.00,
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
        bookStock: 100,
        realStock: 105,
        profitLossQty: 5,
        profitLossAmount: 50.00,
      },
    ],
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
        bookStock: product.bookStock,
        realStock: product.bookStock,
        profitLossQty: 0,
        profitLossAmount: 0,
        costPrice: product.costPrice,
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
const getStocktakeTypeName = (type) => {
  const map = {
    full: '全盘',
    partial: '部分盘点',
    category: '分类盘点',
  }
  return map[type] || type
}

const getWarehouseName = (id) => {
  const map = {
    '1': '主仓库',
    '2': '2号仓库',
    '3': '3号仓库',
  }
  return map[id] || id
}

onMounted(() => {
  // 初始化数据
})
</script>

<style scoped lang="scss">
.stocktake-container {
  padding: 16px;
}

.text-success {
  color: var(--td-success-color);
}

.text-danger {
  color: var(--td-error-color);
}

.image-placeholder {
  border-radius: 4px;
  display: inline-block;
}
</style>
