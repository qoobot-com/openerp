<template>
  <div class="cost-accounting-container">
    <!-- 统计卡片 -->
    <t-row :gutter="[16, 16]" class="mb-4">
      <t-col :span="3">
        <t-card theme="primary1" :header="false" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon">
              <t-icon name="shop" size="40px" />
            </div>
            <div class="stat-info">
              <div class="stat-value">¥{{ stats.totalCost.toLocaleString() }}</div>
              <div class="stat-label">总成本</div>
            </div>
          </div>
        </t-card>
      </t-col>
      <t-col :span="3">
        <t-card theme="warning" :header="false" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon">
              <t-icon name="cart" size="40px" />
            </div>
            <div class="stat-info">
              <div class="stat-value">¥{{ stats.materialCost.toLocaleString() }}</div>
              <div class="stat-label">材料成本</div>
            </div>
          </div>
        </t-card>
      </t-col>
      <t-col :span="3">
        <t-card theme="success" :header="false" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon">
              <t-icon name="usergroup" size="40px" />
            </div>
            <div class="stat-info">
              <div class="stat-value">¥{{ stats.laborCost.toLocaleString() }}</div>
              <div class="stat-label">人工成本</div>
            </div>
          </div>
        </t-card>
      </t-col>
      <t-col :span="3">
        <t-card theme="info" :header="false" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon">
              <t-icon name="setting" size="40px" />
            </div>
            <div class="stat-info">
              <div class="stat-value">¥{{ stats.otherCost.toLocaleString() }}</div>
              <div class="stat-label">其他成本</div>
            </div>
          </div>
        </t-card>
      </t-col>
    </t-row>

    <!-- 搜索卡片 -->
    <t-card class="mb-4">
      <t-form :data="searchForm" layout="inline" @submit="handleSearch" @reset="handleReset">
        <t-form-item label="核算期间" name="accountingPeriod">
          <t-date-picker
            v-model="searchForm.accountingPeriod"
            placeholder="请选择核算期间"
            picker="month"
            style="width: 180px"
            clearable
          />
        </t-form-item>
        <t-form-item label="成本类别" name="costType">
          <t-select
            v-model="searchForm.costType"
            placeholder="请选择成本类别"
            style="width: 150px"
            clearable
          >
            <t-option value="material" label="材料成本" />
            <t-option value="labor" label="人工成本" />
            <t-option value="manufacture" label="制造费用" />
            <t-option value="other" label="其他成本" />
          </t-select>
        </t-form-item>
        <t-form-item label="部门" name="departmentId">
          <t-select
            v-model="searchForm.departmentId"
            placeholder="请选择部门"
            style="width: 180px"
            clearable
            filterable
          >
            <t-option v-for="item in departmentList" :key="item.value" :value="item.value" :label="item.label" />
          </t-select>
        </t-form-item>
        <t-form-item label="产品" name="productId">
          <t-select
            v-model="searchForm.productId"
            placeholder="请选择产品"
            style="width: 180px"
            clearable
            filterable
          >
            <t-option v-for="item in productList" :key="item.value" :value="item.value" :label="item.label" />
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
    <t-card class="mb-4">
      <t-space>
        <t-button theme="primary" @click="handleCalculate">
          <template #icon>
            <t-icon name="calculator" />
          </template>
          开始核算
        </t-button>
        <t-button theme="success" @click="handleExport">
          <template #icon>
            <t-icon name="download" />
          </template>
          导出报表
        </t-button>
        <t-button theme="default" @click="handleRefresh">
          <template #icon>
            <t-icon name="refresh" />
          </template>
          刷新数据
        </t-button>
      </t-space>
    </t-card>

    <!-- 成本核算列表 -->
    <t-card>
      <t-tabs v-model="activeTab">
        <t-tab-panel value="list" label="成本明细">
          <t-table
            :data="costList"
            :columns="columns"
            :pagination="pagination"
            :loading="loading"
            stripe
            hover
            row-key="id"
          >
            <template #costType="{ row }">
              <t-tag v-if="row.costType === 'material'" theme="primary">材料成本</t-tag>
              <t-tag v-else-if="row.costType === 'labor'" theme="success">人工成本</t-tag>
              <t-tag v-else-if="row.costType === 'manufacture'" theme="warning">制造费用</t-tag>
              <t-tag v-else theme="default">其他成本</t-tag>
            </template>
            <template #action="{ row }">
              <t-space>
                <t-link theme="primary" @click="handleView(row)">查看</t-link>
                <t-link theme="default" @click="handleDetail(row)">明细</t-link>
              </t-space>
            </template>
          </t-table>
        </t-tab-panel>
        <t-tab-panel value="summary" label="成本汇总">
          <t-table
            :data="summaryList"
            :columns="summaryColumns"
            :pagination="false"
            :loading="loading"
            stripe
            hover
          />
        </t-tab-panel>
        <t-tab-panel value="product" label="产品成本">
          <t-table
            :data="productCostList"
            :columns="productCostColumns"
            :pagination="pagination"
            :loading="loading"
            stripe
            hover
            row-key="id"
          />
        </t-tab-panel>
        <t-tab-panel value="department" label="部门成本">
          <t-table
            :data="departmentCostList"
            :columns="departmentCostColumns"
            :pagination="pagination"
            :loading="loading"
            stripe
            hover
            row-key="id"
          />
        </t-tab-panel>
      </t-tabs>
    </t-card>

    <!-- 成本详情弹窗 -->
    <t-dialog
      v-model:visible="detailDialogVisible"
      header="成本详情"
      width="900px"
      :confirm-btn="null"
      :cancel-btn="null"
    >
      <t-descriptions :column="2" bordered>
        <t-descriptions-item label="核算单号">{{ currentCost.costNo }}</t-descriptions-item>
        <t-descriptions-item label="核算期间">{{ currentCost.accountingPeriod }}</t-descriptions-item>
        <t-descriptions-item label="成本类别">
          <t-tag v-if="currentCost.costType === 'material'" theme="primary">材料成本</t-tag>
          <t-tag v-else-if="currentCost.costType === 'labor'" theme="success">人工成本</t-tag>
          <t-tag v-else-if="currentCost.costType === 'manufacture'" theme="warning">制造费用</t-tag>
          <t-tag v-else theme="default">其他成本</t-tag>
        </t-descriptions-item>
        <t-descriptions-item label="部门">{{ currentCost.departmentName }}</t-descriptions-item>
        <t-descriptions-item label="产品">{{ currentCost.productName || '-' }}</t-descriptions-item>
        <t-descriptions-item label="成本金额">¥{{ currentCost.costAmount?.toLocaleString() }}</t-descriptions-item>
        <t-descriptions-item label="核算状态">{{ currentCost.status }}</t-descriptions-item>
        <t-descriptions-item label="核算时间">{{ currentCost.calculateTime || '-' }}</t-descriptions-item>
        <t-descriptions-item label="说明" :span="2">{{ currentCost.remark || '-' }}</t-descriptions-item>
      </t-descriptions>
      <div class="dialog-footer">
        <t-button theme="default" @click="detailDialogVisible = false">关闭</t-button>
      </div>
    </t-dialog>

    <!-- 核算确认弹窗 -->
    <t-dialog
      v-model:visible="calculateDialogVisible"
      header="开始核算"
      width="500px"
      :confirm-btn="null"
      :cancel-btn="null"
    >
      <t-form :data="calculateForm" label-width="120px" @submit="handleCalculateSubmit">
        <t-form-item label="核算期间" name="accountingPeriod">
          <t-date-picker v-model="calculateForm.accountingPeriod" placeholder="请选择核算期间" picker="month" />
        </t-form-item>
        <t-form-item label="核算类型" name="calculateType">
          <t-radio-group v-model="calculateForm.calculateType">
            <t-radio value="full">全量核算</t-radio>
            <t-radio value="increment">增量核算</t-radio>
          </t-radio-group>
        </t-form-item>
        <t-form-item label="核算说明" name="remark">
          <t-textarea
            v-model="calculateForm.remark"
            placeholder="请输入核算说明"
            :autosize="{ minRows: 3, maxRows: 5 }"
          />
        </t-form-item>
        <t-form-item>
          <t-space>
            <t-button theme="primary" type="submit">开始核算</t-button>
            <t-button theme="default" @click="calculateDialogVisible = false">取消</t-button>
          </t-space>
        </t-form-item>
      </t-form>
    </t-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { MessagePlugin, DialogPlugin } from 'tdesign-vue-next'

// 统计数据
const stats = ref({
  totalCost: 2580000,
  materialCost: 1200000,
  laborCost: 850000,
  otherCost: 530000,
})

// 搜索表单
const searchForm = reactive({
  accountingPeriod: undefined,
  costType: undefined,
  departmentId: undefined,
  productId: undefined,
})

// 部门列表
const departmentList = ref([
  { value: '1', label: '生产部' },
  { value: '2', label: '研发部' },
  { value: '3', label: '销售部' },
  { value: '4', label: '采购部' },
  { value: '5', label: '财务部' },
])

// 产品列表
const productList = ref([
  { value: '1', label: '笔记本电脑' },
  { value: '2', label: '无线鼠标' },
  { value: '3', label: '机械键盘' },
  { value: '4', label: '显示器' },
  { value: '5', label: '耳机' },
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

// 当前Tab
const activeTab = ref('list')

// 成本详情弹窗
const detailDialogVisible = ref(false)
const currentCost = ref<any>({})

// 核算确认弹窗
const calculateDialogVisible = ref(false)
const calculateForm = reactive({
  accountingPeriod: undefined,
  calculateType: 'full',
  remark: '',
})

// 成本列表
const costList = ref([
  {
    id: '1',
    costNo: 'CA202602190001',
    accountingPeriod: '2026-02',
    costType: 'material',
    departmentName: '生产部',
    productName: '笔记本电脑',
    costAmount: 50000,
    status: '已核算',
    calculateTime: '2026-02-19 15:00:00',
    remark: '直接材料成本',
  },
  {
    id: '2',
    costNo: 'CA202602190002',
    accountingPeriod: '2026-02',
    costType: 'labor',
    departmentName: '生产部',
    productName: '笔记本电脑',
    costAmount: 35000,
    status: '已核算',
    calculateTime: '2026-02-19 15:00:00',
    remark: '直接人工成本',
  },
  {
    id: '3',
    costNo: 'CA202602190003',
    accountingPeriod: '2026-02',
    costType: 'manufacture',
    departmentName: '生产部',
    productName: '笔记本电脑',
    costAmount: 20000,
    status: '已核算',
    calculateTime: '2026-02-19 15:00:00',
    remark: '制造费用分摊',
  },
  {
    id: '4',
    costNo: 'CA202602180001',
    accountingPeriod: '2026-02',
    costType: 'material',
    departmentName: '生产部',
    productName: '无线鼠标',
    costAmount: 8000,
    status: '已核算',
    calculateTime: '2026-02-18 14:00:00',
    remark: '直接材料成本',
  },
  {
    id: '5',
    costNo: 'CA202602180002',
    accountingPeriod: '2026-02',
    costType: 'labor',
    departmentName: '生产部',
    productName: '无线鼠标',
    costAmount: 5000,
    status: '已核算',
    calculateTime: '2026-02-18 14:00:00',
    remark: '直接人工成本',
  },
])

// 列定义
const columns = [
  { colKey: 'costNo', title: '核算单号', width: 150 },
  { colKey: 'accountingPeriod', title: '核算期间', width: 100 },
  { colKey: 'costType', title: '成本类别', width: 100 },
  { colKey: 'departmentName', title: '部门', width: 120 },
  { colKey: 'productName', title: '产品', width: 150 },
  { colKey: 'costAmount', title: '成本金额', width: 120 },
  { colKey: 'status', title: '核算状态', width: 100 },
  { colKey: 'calculateTime', title: '核算时间', width: 180 },
  { colKey: 'action', title: '操作', width: 150, fixed: 'right' },
]

// 成本汇总列
const summaryColumns = [
  { colKey: 'costType', title: '成本类别', width: 120 },
  { colKey: 'currentAmount', title: '本期金额', width: 120 },
  { colKey: 'lastAmount', title: '上期金额', width: 120 },
  { colKey: 'diffAmount', title: '差异金额', width: 120 },
  { colKey: 'diffRate', title: '差异率', width: 100 },
  { colKey: 'proportion', title: '占比', width: 100 },
]

// 成本汇总数据
const summaryList = ref([
  {
    id: '1',
    costType: '材料成本',
    currentAmount: 1200000,
    lastAmount: 1050000,
    diffAmount: 150000,
    diffRate: '+14.3%',
    proportion: '46.5%',
  },
  {
    id: '2',
    costType: '人工成本',
    currentAmount: 850000,
    lastAmount: 720000,
    diffAmount: 130000,
    diffRate: '+18.1%',
    proportion: '33.0%',
  },
  {
    id: '3',
    costType: '制造费用',
    currentAmount: 380000,
    lastAmount: 350000,
    diffAmount: 30000,
    diffRate: '+8.6%',
    proportion: '14.7%',
  },
  {
    id: '4',
    costType: '其他成本',
    currentAmount: 150000,
    lastAmount: 130000,
    diffAmount: 20000,
    diffRate: '+15.4%',
    proportion: '5.8%',
  },
])

// 产品成本列
const productCostColumns = [
  { colKey: 'productCode', title: '产品编号', width: 120 },
  { colKey: 'productName', title: '产品名称', width: 200 },
  { colKey: 'spec', title: '规格', width: 120 },
  { colKey: 'materialCost', title: '材料成本', width: 120 },
  { colKey: 'laborCost', title: '人工成本', width: 120 },
  { colKey: 'manufactureCost', title: '制造费用', width: 120 },
  { colKey: 'totalCost', title: '总成本', width: 120 },
  { colKey: 'unitCost', title: '单位成本', width: 120 },
]

// 产品成本数据
const productCostList = ref([
  {
    id: '1',
    productCode: 'P001',
    productName: '笔记本电脑',
    spec: 'i7/16G/512G',
    materialCost: 500000,
    laborCost: 350000,
    manufactureCost: 200000,
    totalCost: 1050000,
    unitCost: 3500,
  },
  {
    id: '2',
    productCode: 'P002',
    productName: '无线鼠标',
    spec: '黑色',
    materialCost: 80000,
    laborCost: 50000,
    manufactureCost: 30000,
    totalCost: 160000,
    unitCost: 16,
  },
  {
    id: '3',
    productCode: 'P003',
    productName: '机械键盘',
    spec: '青轴',
    materialCost: 120000,
    laborCost: 80000,
    manufactureCost: 50000,
    totalCost: 250000,
    unitCost: 50,
  },
  {
    id: '4',
    productCode: 'P004',
    productName: '显示器',
    spec: '27寸/IPS',
    materialCost: 350000,
    laborCost: 250000,
    manufactureCost: 150000,
    totalCost: 750000,
    unitCost: 375,
  },
  {
    id: '5',
    productCode: 'P005',
    productName: '耳机',
    spec: '降噪',
    materialCost: 150000,
    laborCost: 120000,
    manufactureCost: 50000,
    totalCost: 320000,
    unitCost: 32,
  },
])

// 部门成本列
const departmentCostColumns = [
  { colKey: 'departmentCode', title: '部门编号', width: 120 },
  { colKey: 'departmentName', title: '部门名称', width: 180 },
  { colKey: 'materialCost', title: '材料成本', width: 120 },
  { colKey: 'laborCost', title: '人工成本', width: 120 },
  { colKey: 'otherCost', title: '其他成本', width: 120 },
  { colKey: 'totalCost', title: '总成本', width: 120 },
  { colKey: 'budgetAmount', title: '预算金额', width: 120 },
  { colKey: 'budgetRate', title: '预算执行率', width: 120 },
]

// 部门成本数据
const departmentCostList = ref([
  {
    id: '1',
    departmentCode: 'D001',
    departmentName: '生产部',
    materialCost: 1200000,
    laborCost: 850000,
    otherCost: 380000,
    totalCost: 2430000,
    budgetAmount: 2500000,
    budgetRate: '97.2%',
  },
  {
    id: '2',
    departmentCode: 'D002',
    departmentName: '研发部',
    materialCost: 50000,
    laborCost: 450000,
    otherCost: 80000,
    totalCost: 580000,
    budgetAmount: 600000,
    budgetRate: '96.7%',
  },
  {
    id: '3',
    departmentCode: 'D003',
    departmentName: '销售部',
    materialCost: 0,
    laborCost: 380000,
    otherCost: 50000,
    totalCost: 430000,
    budgetAmount: 500000,
    budgetRate: '86.0%',
  },
  {
    id: '4',
    departmentCode: 'D004',
    departmentName: '采购部',
    materialCost: 0,
    laborCost: 280000,
    otherCost: 35000,
    totalCost: 315000,
    budgetAmount: 350000,
    budgetRate: '90.0%',
  },
  {
    id: '5',
    departmentCode: 'D005',
    departmentName: '财务部',
    materialCost: 0,
    laborCost: 220000,
    otherCost: 25000,
    totalCost: 245000,
    budgetAmount: 300000,
    budgetRate: '81.7%',
  },
])

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
  searchForm.accountingPeriod = undefined
  searchForm.costType = undefined
  searchForm.departmentId = undefined
  searchForm.productId = undefined
  pagination.current = 1
  loadData()
}

// 查看详情
const handleView = (row: any) => {
  currentCost.value = row
  detailDialogVisible.value = true
}

// 明细
const handleDetail = (row: any) => {
  MessagePlugin.info('功能开发中')
}

// 开始核算
const handleCalculate = () => {
  calculateForm.accountingPeriod = undefined
  calculateForm.calculateType = 'full'
  calculateForm.remark = ''
  calculateDialogVisible.value = true
}

// 核算提交
const handleCalculateSubmit = () => {
  const dialog = DialogPlugin({
    header: '确认核算',
    body: '是否确认开始成本核算？此操作可能需要较长时间。',
    confirmBtn: {
      content: '确认开始',
      theme: 'primary',
    },
    onConfirm: () => {
      dialog.hide()
      calculateDialogVisible.value = false
      MessagePlugin.success('成本核算已开始，请稍后查看结果')
      loadData()
    },
  })
}

// 导出报表
const handleExport = () => {
  MessagePlugin.success('报表导出成功')
}

// 刷新数据
const handleRefresh = () => {
  loadData()
  MessagePlugin.success('数据刷新成功')
}
</script>

<style scoped lang="less">
.cost-accounting-container {
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

  .dialog-footer {
    margin-top: 20px;
    text-align: center;
  }
}
</style>
