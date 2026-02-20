<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { MessagePlugin } from 'tdesign-vue-next'

// 统计数据
const statistics = reactive({
  total: 45,
  pending: 8,
  processing: 12,
  resolved: 25,
})

// 搜索表单
const searchForm = reactive({
  exceptionNo: '',
  exceptionType: '',
  exceptionLevel: '',
  exceptionStatus: '',
  dateRange: [],
})

// 异常类型选项
const exceptionTypeOptions = [
  { label: '全部', value: '' },
  { label: '设备故障', value: 'equipment' },
  { label: '质量问题', value: 'quality' },
  { label: '物料短缺', value: 'material' },
  { label: '人员异常', value: 'personnel' },
  { label: '其他异常', value: 'other' },
]

// 异常级别选项
const exceptionLevelOptions = [
  { label: '全部', value: '' },
  { label: '紧急', value: 'critical' },
  { label: '重要', value: 'major' },
  { label: '一般', value: 'minor' },
]

// 异常状态选项
const exceptionStatusOptions = [
  { label: '全部', value: '' },
  { label: '待处理', value: 'pending' },
  { label: '处理中', value: 'processing' },
  { label: '已解决', value: 'resolved' },
]

// 表格数据
const tableData = ref([])
const loading = ref(false)

// 分页
const pagination = reactive({
  current: 1,
  pageSize: 20,
  total: 45,
})

// 弹窗
const dialogVisible = ref(false)
const dialogMode = ref('add')
const detailDialogVisible = ref(false)

// 表单数据
const formData = reactive({
  exceptionNo: '',
  exceptionType: '',
  exceptionLevel: '',
  productionLine: '',
  workstation: '',
  productCode: '',
  exceptionDescription: '',
  occurrenceTime: '',
  reportedBy: '',
  handler: '',
  handlingStatus: '',
  handlingResult: '',
  resolvedTime: '',
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
      exceptionNo: `EX202602${String(i + 1).padStart(4, '0')}`,
      exceptionType: ['equipment', 'quality', 'material', 'personnel', 'other'][i % 5],
      exceptionLevel: ['critical', 'major', 'minor'][i % 3],
      productionLine: ['生产线1', '生产线2', '生产线3', '生产线4'][i % 4],
      workstation: `工位${(i % 3) + 1}`,
      productCode: `P00${i + 1}`,
      exceptionDescription: '设备故障导致生产线停机',
      occurrenceTime: '2026-02-19 10:30:00',
      reportedBy: ['张三', '李四', '王五', '赵六'][i % 4],
      handler: ['技术员A', '技术员B', '技术员C'][i % 3],
      handlingStatus: ['pending', 'processing', 'resolved'][i % 3],
      handlingResult: '设备维修完成，生产线恢复',
      resolvedTime: '2026-02-19 14:00:00',
    }))
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
    exceptionNo: '',
    exceptionType: '',
    exceptionLevel: '',
    exceptionStatus: '',
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
  MessagePlugin.warning('请确认是否删除此异常记录？')
}

// 提交表单
const handleSubmit = () => {
  MessagePlugin.success(dialogMode.value === 'add' ? '新增成功' : '编辑成功')
  dialogVisible.value = false
  fetchData()
}

// 异常类型标签
const getExceptionTypeTag = (type) => {
  const map = {
    equipment: { label: '设备故障', theme: 'danger' },
    quality: { label: '质量问题', theme: 'warning' },
    material: { label: '物料短缺', theme: 'primary' },
    personnel: { label: '人员异常', theme: 'warning' },
    other: { label: '其他异常', theme: 'default' },
  }
  return map[type] || { label: '未知', theme: 'default' }
}

// 异常级别标签
const getExceptionLevelTag = (level) => {
  const map = {
    critical: { label: '紧急', theme: 'danger' },
    major: { label: '重要', theme: 'warning' },
    minor: { label: '一般', theme: 'default' },
  }
  return map[level] || { label: '未知', theme: 'default' }
}

// 异常状态标签
const getExceptionStatusTag = (status) => {
  const map = {
    pending: { label: '待处理', theme: 'warning' },
    processing: { label: '处理中', theme: 'primary' },
    resolved: { label: '已解决', theme: 'success' },
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
  <div class="production-exception-handling">
    <!-- 统计卡片 -->
    <t-row :gutter="16" class="statistics-cards">
      <t-col :span="6">
        <t-card>
          <t-statistic title="异常总数" :value="statistics.total" />
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="warning">
          <t-statistic title="待处理" :value="statistics.pending" />
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="primary">
          <t-statistic title="处理中" :value="statistics.processing" />
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="success">
          <t-statistic title="已解决" :value="statistics.resolved" />
        </t-card>
      </t-col>
    </t-row>

    <!-- 搜索卡片 -->
    <t-card title="异常查询" class="search-card">
      <t-form :data="searchForm" layout="inline">
        <t-form-item label="异常编号">
          <t-input v-model="searchForm.exceptionNo" placeholder="请输入异常编号" clearable />
        </t-form-item>
        <t-form-item label="异常类型">
          <t-select v-model="searchForm.exceptionType" :options="exceptionTypeOptions" placeholder="请选择异常类型" clearable />
        </t-form-item>
        <t-form-item label="异常级别">
          <t-select v-model="searchForm.exceptionLevel" :options="exceptionLevelOptions" placeholder="请选择异常级别" clearable />
        </t-form-item>
        <t-form-item label="异常状态">
          <t-select v-model="searchForm.exceptionStatus" :options="exceptionStatusOptions" placeholder="请选择异常状态" clearable />
        </t-form-item>
        <t-form-item label="发生日期">
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
            新增异常
          </t-button>
          <t-button theme="default">
            <template #icon><t-icon name="download" /></template>
            导出报表
          </t-button>
        </t-space>
      </t-col>
    </t-row>

    <!-- 异常列表表格 -->
    <t-card title="异常记录列表" class="table-card">
      <t-table
        :data="tableData"
        :columns="[
          { colKey: 'exceptionNo', title: '异常编号', width: 150 },
          { colKey: 'exceptionType', title: '异常类型', width: 120 },
          { colKey: 'exceptionLevel', title: '异常级别', width: 100 },
          { colKey: 'productionLine', title: '生产线', width: 120 },
          { colKey: 'workstation', title: '工位', width: 100 },
          { colKey: 'productCode', title: '产品编码', width: 120 },
          { colKey: 'exceptionDescription', title: '异常描述', width: 200 },
          { colKey: 'occurrenceTime', title: '发生时间', width: 160 },
          { colKey: 'reportedBy', title: '报告人', width: 100 },
          { colKey: 'handler', title: '处理人', width: 100 },
          { colKey: 'handlingStatus', title: '处理状态', width: 100 },
          { colKey: 'handlingResult', title: '处理结果', width: 200 },
          { colKey: 'resolvedTime', title: '解决时间', width: 160 },
          { colKey: 'action', title: '操作', width: 150, fixed: 'right' },
        ]"
        :loading="loading"
        :pagination="pagination"
        @page-change="onPageChange"
        row-key="key"
        bordered
        stripe
      >
        <template #exceptionType="{ row }">
          <t-tag :theme="getExceptionTypeTag(row.exceptionType).theme">
            {{ getExceptionTypeTag(row.exceptionType).label }}
          </t-tag>
        </template>
        <template #exceptionLevel="{ row }">
          <t-tag :theme="getExceptionLevelTag(row.exceptionLevel).theme">
            {{ getExceptionLevelTag(row.exceptionLevel).label }}
          </t-tag>
        </template>
        <template #handlingStatus="{ row }">
          <t-tag :theme="getExceptionStatusTag(row.handlingStatus).theme">
            {{ getExceptionStatusTag(row.handlingStatus).label }}
          </t-tag>
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
      :header="dialogMode === 'add' ? '新增异常' : '编辑异常'"
      width="800px"
      :confirm-btn="{ content: '确定', theme: 'primary' }"
      @confirm="handleSubmit"
    >
      <t-form :data="formData" label-width="100px">
        <t-form-item label="异常编号" name="exceptionNo">
          <t-input v-model="formData.exceptionNo" placeholder="请输入异常编号" />
        </t-form-item>
        <t-form-item label="异常类型" name="exceptionType">
          <t-select v-model="formData.exceptionType" placeholder="请选择异常类型">
            <t-option value="equipment" label="设备故障" />
            <t-option value="quality" label="质量问题" />
            <t-option value="material" label="物料短缺" />
            <t-option value="personnel" label="人员异常" />
            <t-option value="other" label="其他异常" />
          </t-select>
        </t-form-item>
        <t-form-item label="异常级别" name="exceptionLevel">
          <t-select v-model="formData.exceptionLevel" placeholder="请选择异常级别">
            <t-option value="critical" label="紧急" />
            <t-option value="major" label="重要" />
            <t-option value="minor" label="一般" />
          </t-select>
        </t-form-item>
        <t-form-item label="生产线" name="productionLine">
          <t-input v-model="formData.productionLine" placeholder="请输入生产线" />
        </t-form-item>
        <t-form-item label="工位" name="workstation">
          <t-input v-model="formData.workstation" placeholder="请输入工位" />
        </t-form-item>
        <t-form-item label="产品编码" name="productCode">
          <t-input v-model="formData.productCode" placeholder="请输入产品编码" />
        </t-form-item>
        <t-form-item label="异常描述" name="exceptionDescription">
          <t-textarea v-model="formData.exceptionDescription" placeholder="请输入异常描述" />
        </t-form-item>
        <t-form-item label="发生时间" name="occurrenceTime">
          <t-date-time-picker v-model="formData.occurrenceTime" />
        </t-form-item>
        <t-form-item label="报告人" name="reportedBy">
          <t-input v-model="formData.reportedBy" placeholder="请输入报告人" />
        </t-form-item>
        <t-form-item label="处理人" name="handler">
          <t-input v-model="formData.handler" placeholder="请输入处理人" />
        </t-form-item>
        <t-form-item label="处理状态" name="handlingStatus">
          <t-select v-model="formData.handlingStatus" placeholder="请选择处理状态">
            <t-option value="pending" label="待处理" />
            <t-option value="processing" label="处理中" />
            <t-option value="resolved" label="已解决" />
          </t-select>
        </t-form-item>
        <t-form-item label="处理结果" name="handlingResult">
          <t-textarea v-model="formData.handlingResult" placeholder="请输入处理结果" />
        </t-form-item>
        <t-form-item label="解决时间" name="resolvedTime">
          <t-date-time-picker v-model="formData.resolvedTime" />
        </t-form-item>
        <t-form-item label="备注" name="remark">
          <t-textarea v-model="formData.remark" placeholder="请输入备注" />
        </t-form-item>
      </t-form>
    </t-dialog>

    <!-- 详情弹窗 -->
    <t-dialog
      v-model:visible="detailDialogVisible"
      header="异常详情"
      width="800px"
      :footer="false"
    >
      <t-descriptions v-if="detailData" :column="2" bordered>
        <t-descriptions-item label="异常编号">{{ detailData.exceptionNo }}</t-descriptions-item>
        <t-descriptions-item label="异常类型">
          <t-tag :theme="getExceptionTypeTag(detailData.exceptionType).theme">
            {{ getExceptionTypeTag(detailData.exceptionType).label }}
          </t-tag>
        </t-descriptions-item>
        <t-descriptions-item label="异常级别">
          <t-tag :theme="getExceptionLevelTag(detailData.exceptionLevel).theme">
            {{ getExceptionLevelTag(detailData.exceptionLevel).label }}
          </t-tag>
        </t-descriptions-item>
        <t-descriptions-item label="生产线">{{ detailData.productionLine }}</t-descriptions-item>
        <t-descriptions-item label="工位">{{ detailData.workstation }}</t-descriptions-item>
        <t-descriptions-item label="产品编码">{{ detailData.productCode }}</t-descriptions-item>
        <t-descriptions-item label="异常描述" :span="2">{{ detailData.exceptionDescription }}</t-descriptions-item>
        <t-descriptions-item label="发生时间">{{ detailData.occurrenceTime }}</t-descriptions-item>
        <t-descriptions-item label="报告人">{{ detailData.reportedBy }}</t-descriptions-item>
        <t-descriptions-item label="处理人">{{ detailData.handler }}</t-descriptions-item>
        <t-descriptions-item label="处理状态">
          <t-tag :theme="getExceptionStatusTag(detailData.handlingStatus).theme">
            {{ getExceptionStatusTag(detailData.handlingStatus).label }}
          </t-tag>
        </t-descriptions-item>
        <t-descriptions-item label="处理结果" :span="2">{{ detailData.handlingResult }}</t-descriptions-item>
        <t-descriptions-item label="解决时间">{{ detailData.resolvedTime }}</t-descriptions-item>
      </t-descriptions>
    </t-dialog>
  </div>
</template>

<style scoped lang="less">
.production-exception-handling {
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
