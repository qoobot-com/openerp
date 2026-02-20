<template>
  <div class="stock-query-container">
    <!-- 统计卡片 -->
    <t-row :gutter="16" class="statistics-cards">
      <t-col :span="6">
        <t-card>
          <t-statistic title="库存总数量" :value="stats.totalQuantity" />
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card>
          <t-statistic title="库存总金额(元)" :value="stats.totalAmount" :precision="2" />
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card>
          <t-statistic title="预警商品数" :value="stats.warningCount" />
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card>
          <t-statistic title="缺货商品数" :value="stats.outOfStockCount" />
        </t-card>
      </t-col>
    </t-row>

    <!-- 搜索卡片 -->
    <t-card class="search-card" title="查询条件">
      <t-form :data="searchForm" layout="inline" @submit="handleSearch" @reset="handleReset">
        <t-form-item label="商品名称" name="name">
          <t-input v-model="searchForm.name" placeholder="请输入商品名称" clearable />
        </t-form-item>
        <t-form-item label="商品编码" name="code">
          <t-input v-model="searchForm.code" placeholder="请输入商品编码" clearable />
        </t-form-item>
        <t-form-item label="商品分类" name="categoryId">
          <t-select v-model="searchForm.categoryId" placeholder="请选择分类" clearable>
            <t-option v-for="cat in categoryList" :key="cat.id" :value="cat.id" :label="cat.name" />
          </t-select>
        </t-form-item>
        <t-form-item label="库存状态" name="stockStatus">
          <t-select v-model="searchForm.stockStatus" placeholder="请选择状态" clearable>
            <t-option value="normal" label="正常" />
            <t-option value="warning" label="预警" />
            <t-option value="out_of_stock" label="缺货" />
          </t-select>
        </t-form-item>
        <t-form-item label="存放位置" name="location">
          <t-input v-model="searchForm.location" placeholder="请输入位置" clearable />
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

    <!-- 库存列表 -->
    <t-card class="table-card">
      <t-table
        :data="tableData"
        :columns="columns"
        :row-key="'id'"
        :loading="loading"
        :pagination="pagination"
        @page-change="handlePageChange"
      >
        <template #image="{ row }">
          <t-image :src="row.image" fit="cover" :style="{ width: '50px', height: '50px', borderRadius: '4px' }" />
        </template>
        <template #stockStatus="{ row }">
          <t-tag v-if="row.stock === 0" theme="danger">缺货</t-tag>
          <t-tag v-else-if="row.stock <= row.minStock" theme="warning">预警</t-tag>
          <t-tag v-else theme="success">正常</t-tag>
        </template>
        <template #stock="{ row }">
          <t-progress
            :percentage="((row.stock / row.maxStock) * 100).toFixed(0)"
            :label="`${row.stock} / ${row.maxStock}`"
            :color="row.stock <= row.minStock ? '#ff7a00' : '#00a870'"
            :size="'small'"
          />
        </template>
        <template #operation="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleViewDetail(row)">查看详情</t-link>
            <t-link theme="primary" @click="handleViewRecord(row)">出入库记录</t-link>
          </t-space>
        </template>
      </t-table>
    </t-card>

    <!-- 商品库存详情弹窗 -->
    <t-dialog v-model:visible="detailVisible" header="商品库存详情" width="700px" :footer="false">
      <t-card>
        <template #header>
          <t-space align="center">
            <t-image :src="detailData.image" fit="cover" :style="{ width: '60px', height: '60px', borderRadius: '4px' }" />
            <div>
              <h3>{{ detailData.name }}</h3>
              <p style="color: #999; margin-top: 4px;">{{ detailData.code }}</p>
            </div>
          </t-space>
        </template>
      </t-card>
      <t-row :gutter="16" style="margin-top: 16px;">
        <t-col :span="12">
          <t-statistic title="当前库存" :value="detailData.stock" />
        </t-col>
        <t-col :span="12">
          <t-statistic title="库存金额" :value="detailData.stockAmount" :precision="2" />
        </t-col>
      </t-row>
      <t-row :gutter="16" style="margin-top: 16px;">
        <t-col :span="12">
          <t-statistic title="最低库存" :value="detailData.minStock" />
        </t-col>
        <t-col :span="12">
          <t-statistic title="最高库存" :value="detailData.maxStock" />
        </t-col>
      </t-row>
      <t-descriptions bordered :column="2" style="margin-top: 16px;">
        <t-descriptions-item label="商品分类">{{ detailData.categoryName }}</t-descriptions-item>
        <t-descriptions-item label="品牌">{{ detailData.brand }}</t-descriptions-item>
        <t-descriptions-item label="规格">{{ detailData.spec }}</t-descriptions-item>
        <t-descriptions-item label="单位">{{ detailData.unitName }}</t-descriptions-item>
        <t-descriptions-item label="采购价">{{ detailData.purchasePrice?.toFixed(2) }}元</t-descriptions-item>
        <t-descriptions-item label="销售价">{{ detailData.salePrice?.toFixed(2) }}元</t-descriptions-item>
        <t-descriptions-item label="存放位置">{{ detailData.location }}</t-descriptions-item>
        <t-descriptions-item label="库存状态">
          <t-tag v-if="detailData.stock === 0" theme="danger">缺货</t-tag>
          <t-tag v-else-if="detailData.stock <= detailData.minStock" theme="warning">预警</t-tag>
          <t-tag v-else theme="success">正常</t-tag>
        </t-descriptions-item>
      </t-descriptions>
    </t-dialog>

    <!-- 出入库记录弹窗 -->
    <t-dialog v-model:visible="recordVisible" header="出入库记录" width="900px" :footer="false">
      <t-card>
        <template #header>
          <t-space>
            <span>{{ recordData.name }}</span>
            <t-tag theme="primary">{{ recordData.code }}</t-tag>
          </t-space>
        </template>
      </t-card>
      <t-table :data="recordData.records" :columns="recordColumns" row-key="id" />
    </t-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { MessagePlugin } from 'tdesign-vue-next';
import { DownloadIcon, PrintIcon } from 'tdesign-icons-vue-next';

// 统计数据
const stats = ref({
  totalQuantity: 12856,
  totalAmount: 2856000.00,
  warningCount: 24,
  outOfStockCount: 8
});

// 搜索表单
const searchForm = reactive({
  name: '',
  code: '',
  categoryId: undefined,
  stockStatus: undefined,
  location: ''
});

// 商品分类列表
const categoryList = ref([
  { id: 1, name: '电子产品' },
  { id: 2, name: '办公用品' },
  { id: 3, name: '日用品' },
  { id: 4, name: '食品饮料' },
  { id: 5, name: '服装鞋帽' }
]);

// 表格数据
const loading = ref(false);
const tableData = ref([
  {
    id: 1,
    name: '笔记本电脑',
    code: 'PD001',
    categoryId: 1,
    categoryName: '电子产品',
    brand: '联想',
    spec: 'i5-12代 16G 512G SSD',
    unit: 'piece',
    unitName: '件',
    purchasePrice: 3500.00,
    salePrice: 4999.00,
    stock: 156,
    minStock: 20,
    maxStock: 200,
    location: 'A区-01-01',
    stockAmount: 546000.00,
    image: 'https://via.placeholder.com/60x60?text=Laptop'
  },
  {
    id: 2,
    name: '无线鼠标',
    code: 'PD002',
    categoryId: 1,
    categoryName: '电子产品',
    brand: '罗技',
    spec: '2.4G 无线',
    unit: 'piece',
    unitName: '件',
    purchasePrice: 25.00,
    salePrice: 59.00,
    stock: 8,
    minStock: 30,
    maxStock: 100,
    location: 'A区-01-02',
    stockAmount: 200.00,
    image: 'https://via.placeholder.com/60x60?text=Mouse'
  },
  {
    id: 3,
    name: 'A4打印纸',
    code: 'PD003',
    categoryId: 2,
    categoryName: '办公用品',
    brand: '得力',
    spec: '70g 500张/包',
    unit: 'box',
    unitName: '箱',
    purchasePrice: 12.00,
    salePrice: 25.00,
    stock: 256,
    minStock: 50,
    maxStock: 500,
    location: 'B区-02-01',
    stockAmount: 3072.00,
    image: 'https://via.placeholder.com/60x60?text=Paper'
  },
  {
    id: 4,
    name: '键盘',
    code: 'PD004',
    categoryId: 1,
    categoryName: '电子产品',
    brand: '罗技',
    spec: '机械键盘',
    unit: 'piece',
    unitName: '件',
    purchasePrice: 180.00,
    salePrice: 299.00,
    stock: 0,
    minStock: 20,
    maxStock: 100,
    location: 'A区-01-03',
    stockAmount: 0,
    image: 'https://via.placeholder.com/60x60?text=Keyboard'
  }
]);

// 表格列
const columns = [
  { colKey: 'image', title: '图片', width: 70 },
  { colKey: 'code', title: '商品编码', width: 120 },
  { colKey: 'name', title: '商品名称', width: 150 },
  { colKey: 'categoryName', title: '分类', width: 100 },
  { colKey: 'brand', title: '品牌', width: 80 },
  { colKey: 'spec', title: '规格', width: 140 },
  { colKey: 'location', title: '存放位置', width: 120 },
  { colKey: 'stock', title: '库存进度', width: 180 },
  { colKey: 'purchasePrice', title: '采购价(元)', width: 100, cell: (h, { row }) => row.purchasePrice?.toFixed(2) },
  { colKey: 'stockAmount', title: '库存金额(元)', width: 120, cell: (h, { row }) => row.stockAmount?.toFixed(2) },
  { colKey: 'stockStatus', title: '状态', width: 100 },
  { colKey: 'operation', title: '操作', width: 180, fixed: 'right' }
];

// 分页
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 386
});

// 弹窗
const detailVisible = ref(false);
const recordVisible = ref(false);

// 详情数据
const detailData = ref({});

// 记录数据
const recordData = ref({
  name: '',
  code: '',
  records: [
    { id: 1, type: 'in', typeName: '入库', quantity: 100, date: '2026-02-18 10:30', operator: '张三', remark: '采购入库' },
    { id: 2, type: 'out', typeName: '出库', quantity: 50, date: '2026-02-19 14:20', operator: '李四', remark: '销售出库' }
  ]
});

// 出入库记录列
const recordColumns = [
  { colKey: 'date', title: '时间', width: 170 },
  { colKey: 'type', title: '类型', width: 80, cell: (h, { row }) => {
    return h('t-tag', { theme: row.type === 'in' ? 'success' : 'danger' }, row.type === 'in' ? '入库' : '出库');
  }},
  { colKey: 'quantity', title: '数量', width: 100 },
  { colKey: 'operator', title: '操作人', width: 100 },
  { colKey: 'remark', title: '备注' }
];

// 搜索
const handleSearch = () => {
  MessagePlugin.success('查询成功');
  loadData();
};

const handleReset = () => {
  Object.assign(searchForm, {
    name: '',
    code: '',
    categoryId: undefined,
    stockStatus: undefined,
    location: ''
  });
  loadData();
};

// 分页
const handlePageChange = (pageInfo: any) => {
  pagination.current = pageInfo.current;
  pagination.pageSize = pageInfo.pageSize;
  loadData();
};

// 查看详情
const handleViewDetail = (row: any) => {
  detailData.value = { ...row };
  detailVisible.value = true;
};

// 查看记录
const handleViewRecord = (row: any) => {
  recordData.value = {
    name: row.name,
    code: row.code,
    records: [
      { id: 1, type: 'in', typeName: '入库', quantity: 100, date: '2026-02-18 10:30', operator: '张三', remark: '采购入库' },
      { id: 2, type: 'out', typeName: '出库', quantity: 50, date: '2026-02-19 14:20', operator: '李四', remark: '销售出库' },
      { id: 3, type: 'in', typeName: '入库', quantity: 200, date: '2026-02-17 09:00', operator: '王五', remark: '入库补充' }
    ]
  };
  recordVisible.value = true;
};

// 导出
const handleExport = () => {
  MessagePlugin.success('导出成功');
};

// 打印
const handlePrint = () => {
  MessagePlugin.success('准备打印...');
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
.stock-query-container {
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
