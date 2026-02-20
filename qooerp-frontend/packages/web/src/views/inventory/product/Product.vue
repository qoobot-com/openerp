<template>
  <div class="product-container">
    <!-- 统计卡片 -->
    <t-row :gutter="16" class="statistics-cards">
      <t-col :span="6">
        <t-card>
          <t-statistic title="商品总数" :value="stats.totalCount" />
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card>
          <t-statistic title="库存总值(元)" :value="stats.totalValue" :precision="2" />
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card>
          <t-statistic title="在售商品" :value="stats.onSaleCount" />
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card>
          <t-statistic title="预警商品" :value="stats.warningCount" />
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
        <t-form-item label="商品状态" name="status">
          <t-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <t-option value="on_sale" label="在售" />
            <t-option value="off_shelf" label="下架" />
            <t-option value="out_of_stock" label="缺货" />
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
          新增商品
        </t-button>
        <t-button theme="default" @click="handleBatchDelete">
          <template #icon><delete-icon /></template>
          批量删除
        </t-button>
        <t-button theme="default" @click="handleBatchOnShelf">
          <template #icon><icon-check /></template>
          批量上架
        </t-button>
        <t-button theme="default" @click="handleBatchOffShelf">
          <template #icon><icon-close /></template>
          批量下架
        </t-button>
        <t-button theme="default" @click="handleImport">
          <template #icon><upload-icon /></template>
          批量导入
        </t-button>
        <t-button theme="default" @click="handleExport">
          <template #icon><download-icon /></template>
          导出商品
        </t-button>
      </t-space>
    </t-card>

    <!-- 商品列表 -->
    <t-card class="table-card">
      <t-table
        :data="tableData"
        :columns="columns"
        :row-key="'id'"
        :selected-row-keys="selectedRowKeys"
        :loading="loading"
        :pagination="pagination"
        @select-change="handleSelectChange"
        @page-change="handlePageChange"
      >
        <template #image="{ row }">
          <t-image :src="row.image" fit="cover" :style="{ width: '60px', height: '60px', borderRadius: '4px' }" />
        </template>
        <template #status="{ row }">
          <t-tag v-if="row.status === 'on_sale'" theme="success">在售</t-tag>
          <t-tag v-else-if="row.status === 'off_shelf'" theme="default">下架</t-tag>
          <t-tag v-else theme="danger">缺货</t-tag>
        </template>
        <template #stock="{ row }">
          <t-tag v-if="row.stock <= row.minStock" theme="warning">
            {{ row.stock }} (预警)
          </t-tag>
          <t-tag v-else-if="row.stock === 0" theme="danger">
            {{ row.stock }} (缺货)
          </t-tag>
          <span v-else>{{ row.stock }}</span>
        </template>
        <template #operation="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleView(row)">查看</t-link>
            <t-link theme="primary" @click="handleEdit(row)">编辑</t-link>
            <t-link v-if="row.status === 'off_shelf'" theme="primary" @click="handleOnShelf(row)">上架</t-link>
            <t-link v-else theme="primary" @click="handleOffShelf(row)">下架</t-link>
            <t-link theme="danger" @click="handleDelete(row)">删除</t-link>
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
            <t-form-item label="商品名称" name="name">
              <t-input v-model="formData.name" placeholder="请输入商品名称" />
            </t-form-item>
            <t-row :gutter="16">
              <t-col :span="12">
                <t-form-item label="商品编码" name="code">
                  <t-input v-model="formData.code" placeholder="请输入商品编码" />
                </t-form-item>
              </t-col>
              <t-col :span="12">
                <t-form-item label="商品分类" name="categoryId">
                  <t-select v-model="formData.categoryId" placeholder="请选择分类" clearable>
                    <t-option v-for="cat in categoryList" :key="cat.id" :value="cat.id" :label="cat.name" />
                  </t-select>
                </t-form-item>
              </t-col>
            </t-row>
            <t-row :gutter="16">
              <t-col :span="12">
                <t-form-item label="品牌" name="brand">
                  <t-input v-model="formData.brand" placeholder="请输入品牌" />
                </t-form-item>
              </t-col>
              <t-col :span="12">
                <t-form-item label="规格" name="spec">
                  <t-input v-model="formData.spec" placeholder="请输入规格" />
                </t-form-item>
              </t-col>
            </t-row>
            <t-form-item label="单位" name="unit">
              <t-select v-model="formData.unit" placeholder="请选择单位" clearable>
                <t-option value="piece" label="件" />
                <t-option value="kg" label="公斤" />
                <t-option value="g" label="克" />
                <t-option value="l" label="升" />
                <t-option value="ml" label="毫升" />
                <t-option value="box" label="箱" />
                <t-option value="set" label="套" />
                <t-option value="other" label="其他" />
              </t-select>
            </t-form-item>
          </t-tab-panel>
          <t-tab-panel value="price" label="价格信息">
            <t-row :gutter="16">
              <t-col :span="8">
                <t-form-item label="采购价(元)" name="purchasePrice">
                  <t-input-number v-model="formData.purchasePrice" placeholder="请输入采购价" :min="0" :precision="2" />
                </t-form-item>
              </t-col>
              <t-col :span="8">
                <t-form-item label="销售价(元)" name="salePrice">
                  <t-input-number v-model="formData.salePrice" placeholder="请输入销售价" :min="0" :precision="2" />
                </t-form-item>
              </t-col>
              <t-col :span="8">
                <t-form-item label="毛利率(%)" name="profitRate">
                  <t-input-number v-model="formData.profitRate" placeholder="请输入毛利率" :min="0" :max="100" :precision="2" />
                </t-form-item>
              </t-col>
            </t-row>
            <t-row :gutter="16">
              <t-col :span="12">
                <t-form-item label="最低售价(元)" name="minPrice">
                  <t-input-number v-model="formData.minPrice" placeholder="请输入最低售价" :min="0" :precision="2" />
                </t-form-item>
              </t-col>
              <t-col :span="12">
                <t-form-item label="建议零售价(元)" name="retailPrice">
                  <t-input-number v-model="formData.retailPrice" placeholder="请输入建议零售价" :min="0" :precision="2" />
                </t-form-item>
              </t-col>
            </t-row>
          </t-tab-panel>
          <t-tab-panel value="stock" label="库存信息">
            <t-row :gutter="16">
              <t-col :span="8">
                <t-form-item label="当前库存" name="stock">
                  <t-input-number v-model="formData.stock" placeholder="请输入当前库存" :min="0" />
                </t-form-item>
              </t-col>
              <t-col :span="8">
                <t-form-item label="最低库存" name="minStock">
                  <t-input-number v-model="formData.minStock" placeholder="请输入最低库存" :min="0" />
                </t-form-item>
              </t-col>
              <t-col :span="8">
                <t-form-item label="最高库存" name="maxStock">
                  <t-input-number v-model="formData.maxStock" placeholder="请输入最高库存" :min="0" />
                </t-form-item>
              </t-col>
            </t-row>
            <t-form-item label="存放位置" name="location">
              <t-input v-model="formData.location" placeholder="请输入存放位置，如：A区-01-01" />
            </t-form-item>
            <t-form-item label="商品状态" name="status">
              <t-radio-group v-model="formData.status">
                <t-radio value="on_sale">在售</t-radio>
                <t-radio value="off_shelf">下架</t-radio>
              </t-radio-group>
            </t-form-item>
          </t-tab-panel>
          <t-tab-panel value="detail" label="详细信息">
            <t-form-item label="商品图片" name="images">
              <t-upload
                v-model="formData.images"
                action=""
                theme="image"
                accept="image/*"
                :max="5"
                :grid-row="2"
                :grid-column="3"
              />
            </t-form-item>
            <t-form-item label="商品描述" name="description">
              <t-textarea v-model="formData.description" placeholder="请输入商品描述" :maxlength="1000" />
            </t-form-item>
            <t-form-item label="商品备注" name="remark">
              <t-textarea v-model="formData.remark" placeholder="请输入商品备注" :maxlength="500" />
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
    <t-dialog v-model:visible="detailVisible" header="商品详情" width="900px" :footer="false">
      <t-card>
        <template #header>
          <t-space>
            <t-image :src="detailData.image" fit="cover" :style="{ width: '80px', height: '80px', borderRadius: '4px' }" />
            <div>
              <h3>{{ detailData.name }}</h3>
              <p style="color: #999; margin-top: 4px;">{{ detailData.code }}</p>
            </div>
          </t-space>
        </template>
      </t-card>
      <t-descriptions bordered :column="2" style="margin-top: 16px;">
        <t-descriptions-item label="商品分类">{{ detailData.categoryName }}</t-descriptions-item>
        <t-descriptions-item label="品牌">{{ detailData.brand }}</t-descriptions-item>
        <t-descriptions-item label="规格">{{ detailData.spec }}</t-descriptions-item>
        <t-descriptions-item label="单位">{{ detailData.unitName }}</t-descriptions-item>
        <t-descriptions-item label="采购价">{{ detailData.purchasePrice?.toFixed(2) }}元</t-descriptions-item>
        <t-descriptions-item label="销售价">{{ detailData.salePrice?.toFixed(2) }}元</t-descriptions-item>
        <t-descriptions-item label="毛利率">{{ detailData.profitRate }}%</t-descriptions-item>
        <t-descriptions-item label="建议零售价">{{ detailData.retailPrice?.toFixed(2) }}元</t-descriptions-item>
        <t-descriptions-item label="当前库存">
          <t-tag v-if="detailData.stock <= detailData.minStock" theme="warning">
            {{ detailData.stock }} (预警)
          </t-tag>
          <t-tag v-else-if="detailData.stock === 0" theme="danger">
            {{ detailData.stock }} (缺货)
          </t-tag>
          <t-tag v-else theme="success">{{ detailData.stock }}</t-tag>
        </t-descriptions-item>
        <t-descriptions-item label="存放位置">{{ detailData.location }}</t-descriptions-item>
        <t-descriptions-item label="商品状态">
          <t-tag v-if="detailData.status === 'on_sale'" theme="success">在售</t-tag>
          <t-tag v-else theme="default">下架</t-tag>
        </t-descriptions-item>
        <t-descriptions-item label="创建时间">{{ detailData.createTime }}</t-descriptions-item>
        <t-descriptions-item label="商品描述" :span="2">{{ detailData.description }}</t-descriptions-item>
        <t-descriptions-item label="备注" :span="2">{{ detailData.remark }}</t-descriptions-item>
      </t-descriptions>
    </t-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { MessagePlugin, DialogPlugin } from 'tdesign-vue-next';
import { AddIcon, DeleteIcon, DownloadIcon, UploadIcon } from 'tdesign-icons-vue-next';
import { IconCheck, IconClose } from 'tdesign-icons-vue-next';

// 统计数据
const stats = ref({
  totalCount: 386,
  totalValue: 2856000.00,
  onSaleCount: 328,
  warningCount: 24
});

// 搜索表单
const searchForm = reactive({
  name: '',
  code: '',
  categoryId: undefined,
  status: undefined
});

// 商品分类列表
const categoryList = ref([
  { id: 1, name: '电子产品' },
  { id: 2, name: '办公用品' },
  { id: 3, name: '日用品' },
  { id: 4, name: '食品饮料' },
  { id: 5, name: '服装鞋帽' },
  { id: 6, name: '家居用品' }
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
    profitRate: 42.83,
    minPrice: 4500.00,
    retailPrice: 5499.00,
    stock: 156,
    minStock: 20,
    maxStock: 200,
    location: 'A区-01-01',
    status: 'on_sale',
    image: 'https://via.placeholder.com/60x60?text=Laptop',
    images: [],
    description: '联想笔记本电脑，高性能办公首选',
    remark: '热销商品',
    createTime: '2025-01-15 10:30:00'
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
    profitRate: 136.00,
    minPrice: 49.00,
    retailPrice: 79.00,
    stock: 8,
    minStock: 30,
    maxStock: 100,
    location: 'A区-01-02',
    status: 'on_sale',
    image: 'https://via.placeholder.com/60x60?text=Mouse',
    images: [],
    description: '罗技无线鼠标，手感舒适',
    remark: '库存预警',
    createTime: '2025-03-20 14:20:00'
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
    profitRate: 108.33,
    minPrice: 20.00,
    retailPrice: 30.00,
    stock: 256,
    minStock: 50,
    maxStock: 500,
    location: 'B区-02-01',
    status: 'on_sale',
    image: 'https://via.placeholder.com/60x60?text=Paper',
    images: [],
    description: '得力A4打印纸，优质纸张',
    remark: '',
    createTime: '2025-06-10 11:00:00'
  }
]);

const selectedRowKeys = ref<number[]>([]);

// 表格列
const columns = [
  { colKey: 'image', title: '图片', width: 80 },
  { colKey: 'code', title: '商品编码', width: 120 },
  { colKey: 'name', title: '商品名称', width: 180 },
  { colKey: 'categoryName', title: '分类', width: 120 },
  { colKey: 'brand', title: '品牌', width: 100 },
  { colKey: 'spec', title: '规格', width: 180 },
  { colKey: 'unitName', title: '单位', width: 80 },
  { colKey: 'purchasePrice', title: '采购价(元)', width: 100, cell: (h, { row }) => row.purchasePrice?.toFixed(2) },
  { colKey: 'salePrice', title: '销售价(元)', width: 100, cell: (h, { row }) => row.salePrice?.toFixed(2) },
  { colKey: 'stock', title: '库存', width: 120 },
  { colKey: 'status', title: '状态', width: 100 },
  { colKey: 'operation', title: '操作', width: 240, fixed: 'right' }
];

// 分页
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 386
});

// 弹窗
const dialogVisible = ref(false);
const dialogTitle = ref('');
const detailVisible = ref(false);
const activeTab = ref('basic');

// 表单
const formRef = ref();
const formData = reactive({
  id: undefined,
  name: '',
  code: '',
  categoryId: undefined,
  brand: '',
  spec: '',
  unit: 'piece',
  purchasePrice: undefined,
  salePrice: undefined,
  profitRate: undefined,
  minPrice: undefined,
  retailPrice: undefined,
  stock: 0,
  minStock: 0,
  maxStock: 0,
  location: '',
  status: 'on_sale',
  images: [],
  description: '',
  remark: ''
});

// 详情数据
const detailData = ref({});

// 表单验证规则
const rules = {
  name: [{ required: true, message: '请输入商品名称', type: 'error' }],
  code: [{ required: true, message: '请输入商品编码', type: 'error' }],
  categoryId: [{ required: true, message: '请选择商品分类', type: 'error' }],
  unit: [{ required: true, message: '请选择单位', type: 'error' }],
  purchasePrice: [{ required: true, message: '请输入采购价', type: 'error' }],
  salePrice: [{ required: true, message: '请输入销售价', type: 'error' }],
  stock: [{ required: true, message: '请输入当前库存', type: 'error' }]
};

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
    status: undefined
  });
  loadData();
};

// 表格选择
const handleSelectChange = (value: number[]) => {
  selectedRowKeys.value = value;
};

// 分页
const handlePageChange = (pageInfo: any) => {
  pagination.current = pageInfo.current;
  pagination.pageSize = pageInfo.pageSize;
  loadData();
};

// 新增
const handleAdd = () => {
  dialogTitle.value = '新增商品';
  activeTab.value = 'basic';
  Object.assign(formData, {
    id: undefined,
    name: '',
    code: '',
    categoryId: undefined,
    brand: '',
    spec: '',
    unit: 'piece',
    purchasePrice: undefined,
    salePrice: undefined,
    profitRate: undefined,
    minPrice: undefined,
    retailPrice: undefined,
    stock: 0,
    minStock: 0,
    maxStock: 0,
    location: '',
    status: 'on_sale',
    images: [],
    description: '',
    remark: ''
  });
  dialogVisible.value = true;
};

// 编辑
const handleEdit = (row: any) => {
  dialogTitle.value = '编辑商品';
  activeTab.value = 'basic';
  Object.assign(formData, row);
  dialogVisible.value = true;
};

// 查看
const handleView = (row: any) => {
  detailData.value = { ...row };
  detailVisible.value = true;
};

// 上架
const handleOnShelf = async (row: any) => {
  const dialog = await DialogPlugin.confirm({
    header: '确认上架',
    body: `确定要上架商品 ${row.name} 吗？`,
    confirmBtn: '确定',
    cancelBtn: '取消'
  });
  if (dialog) {
    MessagePlugin.success('上架成功');
    loadData();
  }
};

// 下架
const handleOffShelf = async (row: any) => {
  const dialog = await DialogPlugin.confirm({
    header: '确认下架',
    body: `确定要下架商品 ${row.name} 吗？`,
    confirmBtn: '确定',
    cancelBtn: '取消'
  });
  if (dialog) {
    MessagePlugin.success('下架成功');
    loadData();
  }
};

// 批量上架
const handleBatchOnShelf = async () => {
  if (selectedRowKeys.value.length === 0) {
    MessagePlugin.warning('请先选择要上架的商品');
    return;
  }
  const dialog = await DialogPlugin.confirm({
    header: '确认上架',
    body: `确定要上架选中的 ${selectedRowKeys.value.length} 个商品吗？`,
    confirmBtn: '确定',
    cancelBtn: '取消'
  });
  if (dialog) {
    MessagePlugin.success('批量上架成功');
    selectedRowKeys.value = [];
    loadData();
  }
};

// 批量下架
const handleBatchOffShelf = async () => {
  if (selectedRowKeys.value.length === 0) {
    MessagePlugin.warning('请先选择要下架的商品');
    return;
  }
  const dialog = await DialogPlugin.confirm({
    header: '确认下架',
    body: `确定要下架选中的 ${selectedRowKeys.value.length} 个商品吗？`,
    confirmBtn: '确定',
    cancelBtn: '取消'
  });
  if (dialog) {
    MessagePlugin.success('批量下架成功');
    selectedRowKeys.value = [];
    loadData();
  }
};

// 删除
const handleDelete = async (row: any) => {
  const dialog = await DialogPlugin.confirm({
    header: '确认删除',
    body: `确定要删除商品 ${row.name} 吗？`,
    confirmBtn: '确定',
    cancelBtn: '取消'
  });
  if (dialog) {
    MessagePlugin.success('删除成功');
    loadData();
  }
};

// 批量删除
const handleBatchDelete = async () => {
  if (selectedRowKeys.value.length === 0) {
    MessagePlugin.warning('请先选择要删除的记录');
    return;
  }
  const dialog = await DialogPlugin.confirm({
    header: '确认删除',
    body: `确定要删除选中的 ${selectedRowKeys.value.length} 条记录吗？`,
    confirmBtn: '确定',
    cancelBtn: '取消'
  });
  if (dialog) {
    MessagePlugin.success('批量删除成功');
    selectedRowKeys.value = [];
    loadData();
  }
};

// 批量导入
const handleImport = () => {
  MessagePlugin.info('请上传Excel文件进行批量导入');
};

// 导出
const handleExport = () => {
  MessagePlugin.success('导出成功');
};

// 提交表单
const handleSubmit = async ({ validateResult }: any) => {
  if (validateResult === true) {
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
.product-container {
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
