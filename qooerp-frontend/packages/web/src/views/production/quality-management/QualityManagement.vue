<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { MessagePlugin } from 'tdesign-vue-next'

// 统计数据
const statistics = reactive({
  total: 856,
  qualified: 818,
  unqualified: 38,
  passRate: 95.6,
})

// 搜索表单
const searchForm = reactive({
  inspectionNo: '',
  productCode: '',
  productName: '',
  inspectionType: '',
  qualityStatus: '',
  dateRange: [],
})

// 检验类型选项
const inspectionTypeOptions = [
  { label: '全部', value: '' },
  { label: '进料检验', value: 'incoming' },
  { label: '过程检验', value: 'process' },
  { label: '成品检验', value: 'final' },
  { label: '出货检验', value: 'outgoing' },
]

// 质量状态选项
const qualityStatusOptions = [
  { label: '全部', value: '' },
  { label: '合格', value: 'qualified' },
  { label: '不合格', value: 'unqualified' },
  { label: '待检', value: 'pending' },
]

// 表格数据
const tableData = ref([])
const loading = ref(false)

// 分页
const pagination = reactive({
  current: 1,
  pageSize: 20,
  total: 856,
})

// 选中行
const selectedRowKeys = ref([])

// 弹窗
const dialogVisible = ref(false)
const dialogMode = ref('add')
const detailDialogVisible = ref(false)

// 表单数据
const formData = reactive({
  inspectionNo: '',
  productCode: '',
  productName: '',
  inspectionType: '',
  batchNo: '',
  inspectionDate: '',
  inspector: '',
  totalQuantity: 0,
  qualifiedQuantity: 0,
  unqualifiedQuantity: 0,
  passRate: 0,
  qualityStatus: '',
  remark: '',
})

// 详情数据
const detailData = ref(null)

// 获取数据
const fetchData = async () => {
  loading.value = true
  try {
    // 模拟 API 调用
    await new Promise(resolve => setTimeout(resolve, 500))
    tableData.value = Array.from({ length: 20 }, (_, i) => ({
      key: i + 1,
      inspectionNo: `QC202602${String(i + 1).padStart(4, '0')}`,
      productCode: `P00${i + 1}`,
      productName: ['产品A', '产品B', '产品C', '产品D', '产品E'][i % 5],
      inspectionType: ['incoming', 'process', 'final', 'outgoing'][i % 4],
      batchNo: `BATCH202602${String(i + 1).padStart(3, '0')}`,
      inspectionDate: '2026-02-19',
      inspector: ['张三', '李四', '王五', '赵六'][i % 4],
      totalQuantity: Math.floor(Math.random() * 500) + 100,
      qualifiedQuantity: 0,
      unqualifiedQuantity: 0,
      passRate: 0,
      qualityStatus: ['qualified', 'unqualified', 'pending'][i % 3],
    })).map(item => {
      item.unqualifiedQuantity = Math.floor(item.totalQuantity * (Math.random() * 0.05))
      item.qualifiedQuantity = item.totalQuantity - item.unqualifiedQuantity
      item.passRate = ((item.qualifiedQuantity / item.totalQuantity) * 100).toFixed(2)
      return item
    })
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  fetchData()
}

// 重置搜索
const handleReset = () => {
  Object.assign(searchForm, {
    inspectionNo: '',
    productCode: '',
    productName: '',
    inspectionType: '',
    qualityStatus: '',
    dateRange: [],
  })
  handleSearch()
}

// 新增
const handleAdd = () => {
  dialogMode.value = 'add'
  dialogVisible.value = true
}

// 编辑
const handleEdit = (record) => {
  dialogMode.value = 'edit'
  Object.assign(formData, record)
  dialogVisible.value = true
}

// 详情
const handleDetail = (record) => {
  detailData.value = record
  detailDialogVisible.value = true
}

// 删除
const handleDelete = (record) => {
  MessagePlugin.warning('请确认是否删除此检验记录？')
}

// 提交表单
const handleSubmit = () => {
  MessagePlugin.success(dialogMode.value === 'add' ? '新增成功' : '编辑成功')
  dialogVisible.value = false
  fetchData()
}

// 检验类型标签
const getInspectionTypeTag = (type) => {
  const map = {
    incoming: { label: '进料检验', theme: 'primary' },
    process: { label: '过程检验', theme: 'success' },
    final: { label: '成品检验', theme: 'warning' },
    outgoing: { label: '出货检验', theme: 'danger' },
  }
  return map[type] || { label: '未知', theme: 'default' }
}

// 质量状态标签
const getQualityStatusTag = (status) => {
  const map = {
    qualified: { label: '合格', theme: 'success' },
    unqualified: { label: '不合格', theme: 'danger' },
    pending: { label: '待检', theme: 'warning' },
  }
  return map[status] || { label: '未知', theme: 'default' }
}

// 分页变化
const onPageChange = (pageInfo) => {
  pagination.current = pageInfo.current
  pagination.pageSize = pageInfo.pageSize
  fetchData()
}

onMounted(() => {
  fetchData()
})
</script>

<template>
  <div class="production-quality-management">
    <!-- 统计卡片 -->
    <t-row :gutter="16" class="statistics-cards">
      <t-col :span="6">
        <t-card>
          <t-statistic title="检验总数" :value="statistics.total" />
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="success">
          <t-statistic title="合格数量" :value="statistics.qualified" />
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="danger">
          <t-statistic title="不合格数量" :value="statistics.unqualified" />
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="primary">
          <t-statistic title="合格率(%)" :value="statistics.passRate" :precision="1" />
        </t-card>
      </t-col>
    </t-row>

    <!-- 搜索卡片 -->
    <t-card title="质量查询" class="search-card">
      <t-form :data="searchForm" layout="inline">
        <t-form-item label="检验编号">
          <t-input v-model="searchForm.inspectionNo" placeholder="请输入检验编号" clearable />
        </t-form-item>
        <t-form-item label="产品编码">
          <t-input v-model="searchForm.productCode" placeholder="请输入产品编码" clearable />
        </t-form-item>
        <t-form-item label="产品名称">
          <t-input v-model="searchForm.productName" placeholder="请输入产品名称" clearable />
        </t-form-item>
        <t-form-item label="检验类型">
          <t-select v-model="searchForm.inspectionType" :options="inspectionTypeOptions" placeholder="请选择检验类型" clearable />
        </t-form-item>
        <t-form-item label="质量状态">
          <t-select v-model="searchForm.qualityStatus" :options="qualityStatusOptions" placeholder="请选择质量状态" clearable />
        </t-form-item>
        <t-form-item label="检验日期">
          <t-date-range-picker v-model="searchForm.dateRange" clearable />
        </t-form-item>
        <t-form-item>
          <t-button theme="primary" @click="handleSearch">查询</t-button>
          <t-button theme="default" @click="handleReset">重置</t-button>
        </t-form-item>
      </t-form>
    </t-card>

    <!-- 操作栏 -->
    <t-row :gutter="16" class="action-bar">
      <t-col :span="24">
        <t-space>
          <t-button theme="primary" @click="handleAdd">
            <template #icon><t-icon name="add" /></template>
            新增检验
          </t-button>
          <t-button theme="default">
            <template #icon><t-icon name="upload" /></template>
            批量导入
          </t-button>
          <t-button theme="default">
            <template #icon><t-icon name="download" /></template>
            导出报表
          </t-button>
        </t-space>
      </t-col>
    </t-row>

    <!-- 质量检验列表表格 -->
    <t-card title="检验记录列表" class="table-card">
      <t-table
        :data="tableData"
        :columns="[
          { colKey: 'row-select', type: 'multiple', width: 50 },
          { colKey: 'inspectionNo', title: '检验编号', width: 150 },
          { colKey: 'productCode', title: '产品编码', width: 120 },
          { colKey: 'productName', title: '产品名称', width: 120 },
          { colKey: 'inspectionType', title: '检验类型', width: 120 },
          { colKey: 'batchNo', title: '批次号', width: 140 },
          { colKey: 'inspectionDate', title: '检验日期', width: 120 },
          { colKey: 'inspector', title: '检验人', width: 100 },
          { colKey: 'totalQuantity', title: '检验数量', width: 100 },
          { colKey: 'qualifiedQuantity', title: '合格数量', width: 100 },
          { colKey: 'unqualifiedQuantity', title: '不合格数量', width: 120 },
          { colKey: 'passRate', title: '合格率(%)', width: 100 },
          { colKey: 'qualityStatus', title: '质量状态', width: 100 },
          { colKey: 'action', title: '操作', width: 150, fixed: 'right' },
        ]"
        :loading="loading"
        :pagination="pagination"
        :selected-row-keys="selectedRowKeys"
        @select-change="(value) => selectedRowKeys = value"
        @page-change="onPageChange"
        row-key="key"
        bordered
        stripe
      >
        <template #inspectionType="{ row }">
          <t-tag :theme="getInspectionTypeTag(row.inspectionType).theme">
            {{ getInspectionTypeTag(row.inspectionType).label }}
          </t-tag>
        </template>
        <template #qualityStatus="{ row }">
          <t-tag :theme="getQualityStatusTag(row.qualityStatus).theme">
            {{ getQualityStatusTag(row.qualityStatus).label }}
          </t-tag>
        </template>
        <template #passRate="{ row }">
          <t-progress :percentage="parseFloat(row.passRate)" :theme="parseFloat(row.passRate) >= 95 ? 'success' : parseFloat(row.passRate) >= 80 ? 'warning' : 'danger'" />
        </template>
        <template #action="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleDetail(row)">详情</t-link>
            <t-link theme="primary" @click="handleEdit(row)">编辑</t-link>
            <t-link theme="danger" @click="handleDelete(row)">删除</t-link>
          </t-space>
        </template>
      </t-table>
    </t-card>

    <!-- 新增/编辑弹窗 -->
    <t-dialog
      v-model:visible="dialogVisible"
      :header="dialogMode === 'add' ? '新增检验' : '编辑检验'"
      width="800px"
      :confirm-btn="{ content: '确定', theme: 'primary' }"
      @confirm="handleSubmit"
    >
      <t-form :data="formData" label-width="100px">
        <t-form-item label="检验编号" name="inspectionNo">
          <t-input v-model="formData.inspectionNo" placeholder="请输入检验编号" />
        </t-form-item>
        <t-form-item label="产品编码" name="productCode">
          <t-input v-model="formData.productCode" placeholder="请输入产品编码" />
        </t-form-item>
        <t-form-item label="产品名称" name="productName">
          <t-input v-model="formData.productName" placeholder="请输入产品名称" />
        </t-form-item>
        <t-form-item label="检验类型" name="inspectionType">
          <t-select v-model="formData.inspectionType" placeholder="请选择检验类型">
            <t-option value="incoming" label="进料检验" />
            <t-option value="process" label="过程检验" />
            <t-option value="final" label="成品检验" />
            <t-option value="outgoing" label="出货检验" />
          </t-select>
        </t-form-item>
        <t-form-item label="批次号" name="batchNo">
          <t-input v-model="formData.batchNo" placeholder="请输入批次号" />
        </t-form-item>
        <t-form-item label="检验日期" name="inspectionDate">
          <t-date-picker v-model="formData.inspectionDate" />
        </t-form-item>
        <t-form-item label="检验人" name="inspector">
          <t-input v-model="formData.inspector" placeholder="请输入检验人" />
        </t-form-item>
        <t-form-item label="检验数量" name="totalQuantity">
          <t-input-number v-model="formData.totalQuantity" :min="1" />
        </t-form-item>
        <t-form-item label="合格数量" name="qualifiedQuantity">
          <t-input-number v-model="formData.qualifiedQuantity" :min="0" />
        </t-form-item>
        <t-form-item label="不合格数量" name="unqualifiedQuantity">
          <t-input-number v-model="formData.unqualifiedQuantity" :min="0" />
        </t-form-item>
        <t-form-item label="质量状态" name="qualityStatus">
          <t-select v-model="formData.qualityStatus" placeholder="请选择质量状态">
            <t-option value="qualified" label="合格" />
            <t-option value="unqualified" label="不合格" />
            <t-option value="pending" label="待检" />
          </t-select>
        </t-form-item>
        <t-form-item label="备注" name="remark">
          <t-textarea v-model="formData.remark" placeholder="请输入备注" />
        </t-form-item>
      </t-form>
    </t-dialog>

    <!-- 详情弹窗 -->
    <t-dialog
      v-model:visible="detailDialogVisible"
      header="检验详情"
      width="800px"
      :footer="false"
    >
      <t-descriptions v-if="detailData" :column="2" bordered>
        <t-descriptions-item label="检验编号">{{ detailData.inspectionNo }}</t-descriptions-item>
        <t-descriptions-item label="产品编码">{{ detailData.productCode }}</t-descriptions-item>
        <t-descriptions-item label="产品名称">{{ detailData.productName }}</t-descriptions-item>
        <t-descriptions-item label="检验类型">
          <t-tag :theme="getInspectionTypeTag(detailData.inspectionType).theme">
            {{ getInspectionTypeTag(detailData.inspectionType).label }}
          </t-tag>
        </t-descriptions-item>
        <t-descriptions-item label="批次号">{{ detailData.batchNo }}</t-descriptions-item>
        <t-descriptions-item label="检验日期">{{ detailData.inspectionDate }}</t-descriptions-item>
        <t-descriptions-item label="检验人">{{ detailData.inspector }}</t-descriptions-item>
        <t-descriptions-item label="检验数量">{{ detailData.totalQuantity }}</t-descriptions-item>
        <t-descriptions-item label="合格数量">{{ detailData.qualifiedQuantity }}</t-descriptions-item>
        <t-descriptions-item label="不合格数量">{{ detailData.unqualifiedQuantity }}</t-descriptions-item>
        <t-descriptions-item label="合格率">
          <t-progress :percentage="parseFloat(detailData.passRate)" />
        </t-descriptions-item>
        <t-descriptions-item label="质量状态">
          <t-tag :theme="getQualityStatusTag(detailData.qualityStatus).theme">
            {{ getQualityStatusTag(detailData.qualityStatus).label }}
          </t-tag>
        </t-descriptions-item>
      </t-descriptions>
    </t-dialog>
  </div>
</template>

<style scoped lang="less">
.production-quality-management {
  padding: 16px;

  .statistics-cards {
    margin-bottom: 16px;
  }

  .search-card {
    margin-bottom: 16px;
  }

  .action-bar {
    margin-bottom: 16px;
  }

  .table-card {
    margin-bottom: 16px;
  }
}
</style>
