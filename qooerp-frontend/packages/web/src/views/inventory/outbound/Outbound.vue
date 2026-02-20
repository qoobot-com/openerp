<template>
  <div class="outbound-container">
    <!-- 统计卡片 -->
    <t-row :gutter="16" class="statistics-cards">
      <t-col :span="6">
        <t-card>
          <t-statistic title="本月出库单数" :value="stats.monthCount" />
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card>
          <t-statistic title="本月出库数量" :value="stats.monthQuantity" />
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card>
          <t-statistic title="本月出库金额(元)" :value="stats.monthAmount" :precision="2" />
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card>
          <t-statistic title="待审核出库单" :value="stats.pendingCount" />
        </t-card>
      </t-col>
    </t-row>

    <!-- 搜索卡片 -->
    <t-card class="search-card" title="查询条件">
      <t-form :data="searchForm" layout="inline" @submit="handleSearch" @reset="handleReset">
        <t-form-item label="出库单号" name="orderNo">
          <t-input v-model="searchForm.orderNo" placeholder="请输入出库单号" clearable />
        </t-form-item>
        <t-form-item label="出库类型" name="type">
          <t-select v-model="searchForm.type" placeholder="请选择类型" clearable>
            <t-option value="sale" label="销售出库" />
            <t-option value="return" label="退货出库" />
            <t-option value="transfer" label="调拨出库" />
            <t-option value="loss" label="损耗出库" />
            <t-option value="other" label="其他出库" />
          </t-select>
        </t-form-item>
        <t-form-item label="客户" name="customerId">
          <t-select v-model="searchForm.customerId" placeholder="请选择客户" clearable filterable>
            <t-option v-for="cus in customerList" :key="cus.id" :value="cus.id" :label="cus.name" />
          </t-select>
        </t-form-item>
        <t-form-item label="出库日期" name="dateRange">
          <t-date-range-picker v-model="searchForm.dateRange" placeholder="请选择日期范围" />
        </t-form-item>
        <t-form-item label="审核状态" name="status">
          <t-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <t-option value="pending" label="待审核" />
            <t-option value="approved" label="已审核" />
            <t-option value="rejected" label="已拒绝" />
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
    <t-card class="operation-card">
      <t-space>
        <t-button theme="primary" @click="handleAdd">
          <template #icon><add-icon /></template>
          新增出库
        </t-button>
        <t-button theme="default" @click="handleExport">
          <template #icon><download-icon /></template>
          导出报表
        </t-button>
      </t-space>
    </t-card>

    <!-- 出库单列表 -->
    <t-card class="table-card">
      <t-table
        :data="tableData"
        :columns="columns"
        :row-key="'id'"
        :loading="loading"
        :pagination="pagination"
        @page-change="handlePageChange"
      >
        <template #type="{ row }">
          <t-tag v-if="row.type === 'sale'" theme="primary">销售出库</t-tag>
          <t-tag v-else-if="row.type === 'return'" theme="success">退货出库</t-tag>
          <t-tag v-else-if="row.type === 'transfer'" theme="warning">调拨出库</t-tag>
          <t-tag v-else-if="row.type === 'loss'" theme="danger">损耗出库</t-tag>
          <t-tag v-else>其他出库</t-tag>
        </template>
        <template #status="{ row }">
          <t-tag v-if="row.status === 'pending'" theme="warning">待审核</t-tag>
          <t-tag v-else-if="row.status === 'approved'" theme="success">已审核</t-tag>
          <t-tag v-else theme="danger">已拒绝</t-tag>
        </template>
        <template #operation="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleView(row)">查看</t-link>
            <t-link v-if="row.status === 'pending'" theme="primary" @click="handleApprove(row)">审核</t-link>
            <t-link v-if="row.status === 'pending'" theme="primary" @click="handleEdit(row)">编辑</t-link>
            <t-link v-if="row.status === 'approved'" theme="primary" @click="handlePrint(row)">打印</t-link>
          </t-space>
        </template>
      </t-table>
    </t-card>

    <!-- 新增/编辑弹窗 -->
    <t-dialog
      v-model:visible="dialogVisible"
      :header="dialogTitle"
      :confirm-btn="null"
      :cancel-btn="null"
      width="900px"
    >
      <t-form
        ref="formRef"
        :data="formData"
        :rules="rules"
        label-width="120px"
        @submit="handleSubmit"
      >
        <t-tabs v-model="activeTab">
          <t-tab-panel value="basic" label="基本信息">
            <t-form-item label="出库类型" name="type">
              <t-select v-model="formData.type" placeholder="请选择出库类型" clearable>
                <t-option value="sale" label="销售出库" />
                <t-option value="return" label="退货出库" />
                <t-option value="transfer" label="调拨出库" />
                <t-option value="loss" label="损耗出库" />
                <t-option value="other" label="其他出库" />
              </t-select>
            </t-form-item>
            <t-form-item label="出库日期" name="outboundDate">
              <t-date-picker v-model="formData.outboundDate" placeholder="请选择出库日期" style="width: 100%" />
            </t-form-item>
            <t-form-item label="客户" name="customerId">
              <t-select v-model="formData.customerId" placeholder="请选择客户" clearable filterable>
                <t-option v-for="cus in customerList" :key="cus.id" :value="cus.id" :label="cus.name" />
              </t-select>
            </t-form-item>
            <t-form-item label="仓库" name="warehouseId">
              <t-select v-model="formData.warehouseId" placeholder="请选择仓库" clearable>
                <t-option v-for="wh in warehouseList" :key="wh.id" :value="wh.id" :label="wh.name" />
              </t-select>
            </t-form-item>
            <t-form-item label="经手人" name="operatorId">
              <t-select v-model="formData.operatorId" placeholder="请选择经手人" clearable filterable>
                <t-option v-for="emp in employeeList" :key="emp.id" :value="emp.id" :label="emp.name" />
              </t-select>
            </t-form-item>
            <t-form-item label="备注" name="remark">
              <t-textarea v-model="formData.remark" placeholder="请输入备注" :maxlength="500" />
            </t-form-item>
          </t-tab-panel>
          <t-tab-panel value="items" label="出库商品">
            <t-button theme="primary" @click="handleAddItem" style="margin-bottom: 16px;">
              <template #icon><add-icon /></template>
              添加商品
            </t-button>
            <t-table :data="formData.items" :columns="itemColumns" row-key="id">
              <template #price="{ row }">
                <t-input-number v-model="row.price" :min="0" :precision="2" size="small" />
              </template>
              <template #quantity="{ row }">
                <t-input-number v-model="row.quantity" :min="1" :max="row.maxQuantity" size="small" />
              </template>
              <template #operation="{ row }">
                <t-link theme="danger" @click="handleDeleteItem(row)">删除</t-link>
              </template>
            </t-table>
            <t-form-item label="合计" style="margin-top: 16px;">
              <t-space>
                <span>数量: {{ totalQuantity }}</span>
                <span>金额: {{ totalAmount?.toFixed(2) }}元</span>
              </t-space>
            </t-form-item>
          </t-tab-panel>
        </t-tabs>
        <t-form-item style="margin-top: 20px;">
          <t-space>
            <t-button theme="primary" type="submit">确定</t-button>
            <t-button theme="default" @click="dialogVisible = false">取消</t-button>
          </t-space>
        </t-form-item>
      </t-form>
    </t-dialog>

    <!-- 详情弹窗 -->
    <t-dialog v-model:visible="detailVisible" header="出库单详情" width="900px" :footer="false">
      <t-descriptions bordered :column="2">
        <t-descriptions-item label="出库单号">{{ detailData.orderNo }}</t-descriptions-item>
        <t-descriptions-item label="出库类型">{{ detailData.typeName }}</t-descriptions-item>
        <t-descriptions-item label="出库日期">{{ detailData.outboundDate }}</t-descriptions-item>
        <t-descriptions-item label="客户">{{ detailData.customerName }}</t-descriptions-item>
        <t-descriptions-item label="仓库">{{ detailData.warehouseName }}</t-descriptions-item>
        <t-descriptions-item label="经手人">{{ detailData.operatorName }}</t-descriptions-item>
        <t-descriptions-item label="审核状态">
          <t-tag v-if="detailData.status === 'pending'" theme="warning">待审核</t-tag>
          <t-tag v-else-if="detailData.status === 'approved'" theme="success">已审核</t-tag>
          <t-tag v-else theme="danger">已拒绝</t-tag>
        </t-descriptions-item>
        <t-descriptions-item label="创建时间">{{ detailData.createTime }}</t-descriptions-item>
        <t-descriptions-item label="备注" :span="2">{{ detailData.remark }}</t-descriptions-item>
      </t-descriptions>
      <t-divider>出库商品</t-divider>
      <t-table :data="detailData.items" :columns="itemDetailColumns" row-key="id" />
      <t-divider>汇总信息</t-divider>
      <t-row :gutter="16">
        <t-col :span="8">
          <t-statistic title="出库数量" :value="detailData.totalQuantity" />
        </t-col>
        <t-col :span="8">
          <t-statistic title="出库金额" :value="detailData.totalAmount" :precision="2" />
        </t-col>
        <t-col :span="8">
          <t-statistic title="商品种类" :value="detailData.itemCount" />
        </t-col>
      </t-row>
    </t-dialog>

    <!-- 审核弹窗 -->
    <t-dialog v-model:visible="approveVisible" header="出库单审核" width="600px">
      <t-form ref="approveFormRef" :data="approveData" :rules="approveRules" label-width="120px">
        <t-form-item label="审核结果" name="result">
          <t-radio-group v-model="approveData.result">
            <t-radio value="approved">通过</t-radio>
            <t-radio value="rejected">拒绝</t-radio>
          </t-radio-group>
        </t-form-item>
        <t-form-item label="审核意见" name="comment">
          <t-textarea v-model="approveData.comment" placeholder="请输入审核意见" :maxlength="500" />
        </t-form-item>
      </t-form>
      <template #footer>
        <t-space>
          <t-button theme="primary" @click="handleApproveSubmit">确定</t-button>
          <t-button theme="default" @click="approveVisible = false">取消</t-button>
        </t-space>
      </template>
    </t-dialog>

    <!-- 选择商品弹窗 -->
    <t-dialog v-model:visible="selectProductVisible" header="选择商品" width="900px">
      <t-input v-model="productSearch" placeholder="请输入商品名称或编码搜索" clearable />
      <t-table
        :data="productList"
        :columns="productColumns"
        row-key="id"
        @row-click="handleSelectProduct"
        style="margin-top: 16px; cursor: pointer;"
      >
        <template #stock="{ row }">
          <t-tag v-if="row.stock <= row.minStock" theme="warning">{{ row.stock }}</t-tag>
          <t-tag v-else-if="row.stock === 0" theme="danger">{{ row.stock }}</t-tag>
          <t-tag v-else theme="success">{{ row.stock }}</t-tag>
        </template>
        <template #operation="{ row }">
          <t-button theme="primary" size="small" @click="handleSelectProduct(row)">选择</t-button>
        </template>
      </t-table>
      <template #footer>
        <t-button theme="default" @click="selectProductVisible = false">取消</t-button>
      </template>
    </t-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue';
import { MessagePlugin } from 'tdesign-vue-next';
import { AddIcon, DownloadIcon } from 'tdesign-icons-vue-next';

// 统计数据
const stats = ref({
  monthCount: 128,
  monthQuantity: 4286,
  monthAmount: 958000.00,
  pendingCount: 12
});

// 搜索表单
const searchForm = reactive({
  orderNo: '',
  type: undefined,
  customerId: undefined,
  dateRange: [],
  status: undefined
});

// 客户列表
const customerList = ref([
  { id: 1, name: '深圳科技有限公司' },
  { id: 2, name: '北京贸易有限公司' },
  { id: 3, name: '广州电子商务公司' }
]);

// 仓库列表
const warehouseList = ref([
  { id: 1, name: 'A区仓库' },
  { id: 2, name: 'B区仓库' },
  { id: 3, name: 'C区仓库' }
]);

// 员工列表
const employeeList = ref([
  { id: 1, name: '张三' },
  { id: 2, name: '李四' },
  { id: 3, name: '王五' }
]);

// 表格数据
const loading = ref(false);
const tableData = ref([
  {
    id: 1,
    orderNo: 'OUT20260219001',
    type: 'sale',
    typeName: '销售出库',
    outboundDate: '2026-02-19',
    customerId: 1,
    customerName: '深圳科技有限公司',
    warehouseId: 1,
    warehouseName: 'A区仓库',
    operatorId: 1,
    operatorName: '张三',
    status: 'approved',
    remark: '2月份销售出库',
    items: [
      { id: 1, productName: '笔记本电脑', productCode: 'PD001', spec: 'i5-12代 16G 512G', unit: 'piece', price: 4999, quantity: 30, amount: 149970 },
      { id: 2, productName: '无线鼠标', productCode: 'PD002', spec: '2.4G 无线', unit: 'piece', price: 59, quantity: 50, amount: 2950 }
    ],
    totalQuantity: 80,
    totalAmount: 152920.00,
    itemCount: 2,
    createTime: '2026-02-19 10:30:00'
  },
  {
    id: 2,
    orderNo: 'OUT20260218001',
    type: 'return',
    typeName: '退货出库',
    outboundDate: '2026-02-18',
    customerId: 2,
    customerName: '北京贸易有限公司',
    warehouseId: 2,
    warehouseName: 'B区仓库',
    operatorId: 2,
    operatorName: '李四',
    status: 'pending',
    remark: '供应商退货',
    items: [
      { id: 1, productName: '机械键盘', productCode: 'PD004', spec: '机械键盘', unit: 'piece', price: 180, quantity: 15, amount: 2700 }
    ],
    totalQuantity: 15,
    totalAmount: 2700.00,
    itemCount: 1,
    createTime: '2026-02-18 14:20:00'
  }
]);

// 表格列
const columns = [
  { colKey: 'orderNo', title: '出库单号', width: 160 },
  { colKey: 'type', title: '出库类型', width: 120 },
  { colKey: 'outboundDate', title: '出库日期', width: 120 },
  { colKey: 'customerName', title: '客户', width: 180 },
  { colKey: 'warehouseName', title: '仓库', width: 120 },
  { colKey: 'totalQuantity', title: '出库数量', width: 100 },
  { colKey: 'totalAmount', title: '出库金额(元)', width: 120, cell: (h, { row }) => row.totalAmount?.toFixed(2) },
  { colKey: 'operatorName', title: '经手人', width: 100 },
  { colKey: 'status', title: '审核状态', width: 100 },
  { colKey: 'operation', title: '操作', width: 200, fixed: 'right' }
];

// 分页
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 128
});

// 弹窗
const dialogVisible = ref(false);
const dialogTitle = ref('');
const detailVisible = ref(false);
const approveVisible = ref(false);
const selectProductVisible = ref(false);
const activeTab = ref('basic');

// 表单
const formRef = ref();
const formData = reactive({
  id: undefined,
  type: 'sale',
  outboundDate: undefined,
  customerId: undefined,
  warehouseId: undefined,
  operatorId: undefined,
  remark: '',
  items: []
});

// 详情数据
const detailData = ref({});

// 审核表单
const approveFormRef = ref();
const approveData = reactive({
  id: undefined,
  result: 'approved',
  comment: ''
});

const approveRules = {
  result: [{ required: true, message: '请选择审核结果', type: 'error' }],
  comment: [{ required: true, message: '请输入审核意见', type: 'error' }]
};

// 表单验证规则
const rules = {
  type: [{ required: true, message: '请选择出库类型', type: 'error' }],
  outboundDate: [{ required: true, message: '请选择出库日期', type: 'error' }],
  warehouseId: [{ required: true, message: '请选择仓库', type: 'error' }],
  operatorId: [{ required: true, message: '请选择经手人', type: 'error' }]
};

// 商品表格列
const itemColumns = [
  { colKey: 'productCode', title: '商品编码', width: 120 },
  { colKey: 'productName', title: '商品名称', width: 150 },
  { colKey: 'spec', title: '规格', width: 140 },
  { colKey: 'unit', title: '单位', width: 80 },
  { colKey: 'stock', title: '库存', width: 80 },
  { colKey: 'price', title: '单价(元)', width: 120 },
  { colKey: 'quantity', title: '数量', width: 100 },
  { colKey: 'amount', title: '金额(元)', width: 100, cell: (h, { row }) => (row.price * row.quantity).toFixed(2) },
  { colKey: 'operation', title: '操作', width: 80 }
];

const itemDetailColumns = [
  { colKey: 'productCode', title: '商品编码', width: 120 },
  { colKey: 'productName', title: '商品名称', width: 150 },
  { colKey: 'spec', title: '规格', width: 140 },
  { colKey: 'unit', title: '单位', width: 80 },
  { colKey: 'price', title: '单价(元)', width: 100 },
  { colKey: 'quantity', title: '数量', width: 100 },
  { colKey: 'amount', title: '金额(元)', width: 100, cell: (h, { row }) => row.amount?.toFixed(2) }
];

// 合计
const totalQuantity = computed(() => formData.items.reduce((sum: number, item: any) => sum + (item.quantity || 0), 0));
const totalAmount = computed(() => formData.items.reduce((sum: number, item: any) => sum + (item.price || 0) * (item.quantity || 0), 0));

// 商品列表
const productSearch = ref('');
const productList = ref([
  { id: 1, code: 'PD001', name: '笔记本电脑', spec: 'i5-12代 16G 512G', unit: 'piece', price: 4999, stock: 156, minStock: 20 },
  { id: 2, code: 'PD002', name: '无线鼠标', spec: '2.4G 无线', unit: 'piece', price: 59, stock: 8, minStock: 30 },
  { id: 3, code: 'PD003', name: 'A4打印纸', spec: '70g 500张/包', unit: 'box', price: 25, stock: 256, minStock: 50 }
]);

const productColumns = [
  { colKey: 'code', title: '商品编码', width: 120 },
  { colKey: 'name', title: '商品名称', width: 150 },
  { colKey: 'spec', title: '规格', width: 140 },
  { colKey: 'unit', title: '单位', width: 80 },
  { colKey: 'price', title: '参考价(元)', width: 100, cell: (h, { row }) => row.price?.toFixed(2) },
  { colKey: 'stock', title: '库存', width: 80 },
  { colKey: 'operation', title: '操作', width: 80 }
];

// 搜索
const handleSearch = () => {
  MessagePlugin.success('查询成功');
  loadData();
};

const handleReset = () => {
  Object.assign(searchForm, {
    orderNo: '',
    type: undefined,
    customerId: undefined,
    dateRange: [],
    status: undefined
  });
  loadData();
};

// 分页
const handlePageChange = (pageInfo: any) => {
  pagination.current = pageInfo.current;
  pagination.pageSize = pageInfo.pageSize;
  loadData();
};

// 新增
const handleAdd = () => {
  dialogTitle.value = '新增出库单';
  activeTab.value = 'basic';
  Object.assign(formData, {
    id: undefined,
    type: 'sale',
    outboundDate: undefined,
    customerId: undefined,
    warehouseId: undefined,
    operatorId: undefined,
    remark: '',
    items: []
  });
  dialogVisible.value = true;
};

// 编辑
const handleEdit = (row: any) => {
  dialogTitle.value = '编辑出库单';
  activeTab.value = 'basic';
  Object.assign(formData, {
    ...row,
    items: row.items || []
  });
  dialogVisible.value = true;
};

// 查看
const handleView = (row: any) => {
  detailData.value = { ...row };
  detailVisible.value = true;
};

// 审核
const handleApprove = (row: any) => {
  Object.assign(approveData, {
    id: row.id,
    result: 'approved',
    comment: ''
  });
  approveVisible.value = true;
};

const handleApproveSubmit = async ({ validateResult }: any) => {
  if (validateResult === true) {
    MessagePlugin.success('审核成功');
    approveVisible.value = false;
    loadData();
  }
};

// 打印
const handlePrint = (row: any) => {
  MessagePlugin.info(`打印出库单 ${row.orderNo}`);
};

// 添加商品
const handleAddItem = () => {
  selectProductVisible.value = true;
};

const handleSelectProduct = (row: any) => {
  const newItem = {
    id: Date.now(),
    productId: row.id,
    productCode: row.code,
    productName: row.name,
    spec: row.spec,
    unit: row.unit,
    price: row.price,
    stock: row.stock,
    maxQuantity: row.stock,
    quantity: 1
  };
  formData.items.push(newItem);
  selectProductVisible.value = false;
  activeTab.value = 'items';
};

// 删除商品
const handleDeleteItem = (row: any) => {
  const index = formData.items.findIndex((item: any) => item.id === row.id);
  if (index > -1) {
    formData.items.splice(index, 1);
  }
};

// 导出
const handleExport = () => {
  MessagePlugin.success('导出成功');
};

// 提交表单
const handleSubmit = async ({ validateResult }: any) => {
  if (validateResult === true) {
    if (formData.items.length === 0) {
      MessagePlugin.warning('请至少添加一个商品');
      return;
    }
    MessagePlugin.success(formData.id ? '更新成功' : '创建成功');
    dialogVisible.value = false;
    loadData();
  }
};

// 加载数据
const loadData = () => {
  loading.value = true;
  setTimeout(() => {
    loading.value = false;
  }, 500);
};

onMounted(() => {
  loadData();
});
</script>

<style scoped lang="less">
.outbound-container {
  .statistics-cards {
    margin-bottom: 16px;
  }

  .search-card {
    margin-bottom: 16px;
  }

  .operation-card {
    margin-bottom: 16px;
  }

  .table-card {
    margin-bottom: 16px;
  }
}
</style>
