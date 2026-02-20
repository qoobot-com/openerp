<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { MessagePlugin } from 'tdesign-vue-next'

// 统计数据
const statistics = reactive({
  total: 156,
  optimized: 128,
  inProgress: 18,
  pending: 10,
})

// 搜索表单
const searchForm = reactive({
  scheduleNo: '',
  orderNo: '',
  productCode: '',
  scheduleStatus: '',
  dateRange: [],
})

// 状态选项
const statusOptions = [
  { label: '全部', value: '' },
  { label: '已排程', value: 'scheduled' },
  { label: '生产中', value: 'in_production' },
  { label: '已完成', value: 'completed' },
  { label: '已取消', value: 'cancelled' },
]

// 表格数据
const tableData = ref([])
const loading = ref(false)

// 分页
const pagination = reactive({
  current: 1,
  pageSize: 20,
  total: 156,
})

// 选中行
const selectedRowKeys = ref([])

// 弹窗
const dialogVisible = ref(false)
const dialogMode = ref('add')
const detailDialogVisible = ref(false)

// 表单数据
const formData = reactive({
  scheduleNo: '',
  orderNo: '',
  productCode: '',
  productName: '',
  planQuantity: 0,
  planStartDate: '',
  planEndDate: '',
  productionLine: '',
  workShift: '',
  priority: '',
  scheduleStatus: '',
  remark: '',
})

// 详情数据
const detailData = ref(null)

// Gantt 图数据
const ganttData = ref([
  {
    id: 1,
    orderNo: 'PO202601001',
    productName: '产品A',
    startDate: '2026-02-19',
    endDate: '2026-02-20',
    progress: 100,
    color: '#52c41a',
  },
  {
    id: 2,
    orderNo: 'PO202601002',
    productName: '产品B',
    startDate: '2026-02-19',
    endDate: '2026-02-22',
    progress: 60,
    color: '#1890ff',
  },
  {
    id: 3,
    orderNo: 'PO202601003',
    productName: '产品C',
    startDate: '2026-02-20',
    endDate: '2026-02-23',
    progress: 30,
    color: '#1890ff',
  },
  {
    id: 4,
    orderNo: 'PO202601004',
    productName: '产品D',
    startDate: '2026-02-21',
    endDate: '2026-02-25',
    progress: 0,
    color: '#faad14',
  },
  {
    id: 5,
    orderNo: 'PO202601005',
    productName: '产品E',
    startDate: '2026-02-22',
    endDate: '2026-02-24',
    progress: 0,
    color: '#faad14',
  },
])

// 获取数据
const fetchData = async () => {
  loading.value = true
  try {
    // 模拟 API 调用
    await new Promise(resolve => setTimeout(resolve, 500))
    tableData.value = Array.from({ length: 20 }, (_, i) => ({
      key: i + 1,
      scheduleNo: `PS202602${String(i + 1).padStart(3, '0')}`,
      orderNo: `PO202601${String(i + 1).padStart(3, '0')}`,
      productCode: `P00${i + 1}`,
      productName: ['产品A', '产品B', '产品C', '产品D', '产品E'][i % 5],
      planQuantity: Math.floor(Math.random() * 500) + 100,
      productionLine: ['生产线1', '生产线2', '生产线3', '生产线4'][i % 4],
      workShift: ['白班', '夜班'][i % 2],
      planStartDate: '2026-02-19',
      planEndDate: '2026-02-20',
      priority: ['高', '中', '低'][i % 3],
      scheduleStatus: ['scheduled', 'in_production', 'completed', 'cancelled'][i % 4],
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
    scheduleNo: '',
    orderNo: '',
    productCode: '',
    scheduleStatus: '',
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
  MessagePlugin.warning('请确认是否删除此排程？')
}

// 批量优化
const handleBatchOptimize = () => {
  if (selectedRowKeys.value.length === 0) {
    MessagePlugin.warning('请选择需要优化的排程')
    return
  }
  MessagePlugin.success('批量优化排程成功')
}

// 自动排程
const handleAutoSchedule = () => {
  MessagePlugin.success('自动排程成功')
}

// 手动调整
const handleManualAdjust = () => {
  MessagePlugin.info('进入手动调整模式')
}

// 导出报表
const handleExport = () => {
  MessagePlugin.success('导出成功')
}

// 提交表单
const handleSubmit = () => {
  MessagePlugin.success(dialogMode.value === 'add' ? '新增成功' : '编辑成功')
  dialogVisible.value = false
  fetchData()
}

// 状态标签
const getStatusTag = (status) => {
  const map = {
    scheduled: { label: '已排程', theme: 'default' },
    in_production: { label: '生产中', theme: 'success' },
    completed: { label: '已完成', theme: 'success' },
    cancelled: { label: '已取消', theme: 'warning' },
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
  <div class="production-schedule-optimization">
    <!-- 统计卡片 -->
    <t-row :gutter="16" class="statistics-cards">
      <t-col :span="6">
        <t-card>
          <t-statistic title="排程总数" :value="statistics.total" />
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="success">
          <t-statistic title="已优化" :value="statistics.optimized" />
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="primary">
          <t-statistic title="进行中" :value="statistics.inProgress" />
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="warning">
          <t-statistic title="待处理" :value="statistics.pending" />
        </t-card>
      </t-col>
    </t-row>

    <!-- 搜索卡片 -->
    <t-card title="排程查询" class="search-card">
      <t-form :data="searchForm" layout="inline">
        <t-form-item label="排程编号">
          <t-input v-model="searchForm.scheduleNo" placeholder="请输入排程编号" clearable />
        </t-form-item>
        <t-form-item label="生产订单">
          <t-input v-model="searchForm.orderNo" placeholder="请输入生产订单" clearable />
        </t-form-item>
        <t-form-item label="产品编码">
          <t-input v-model="searchForm.productCode" placeholder="请输入产品编码" clearable />
        </t-form-item>
        <t-form-item label="排程状态">
          <t-select v-model="searchForm.scheduleStatus" :options="statusOptions" placeholder="请选择排程状态" clearable />
        </t-form-item>
        <t-form-item label="计划日期">
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
          <t-button theme="primary" @click="handleAutoSchedule">
            <template #icon><t-icon name="play-circle" /></template>
            自动排程
          </t-button>
          <t-button theme="success" @click="handleManualAdjust">
            <template #icon><t-icon name="edit" /></template>
            手动调整
          </t-button>
          <t-button theme="warning" @click="handleBatchOptimize">
            <template #icon><t-icon name="check" /></template>
            批量优化
          </t-button>
          <t-button theme="default" @click="handleExport">
            <template #icon><t-icon name="download" /></template>
            导出报表
          </t-button>
        </t-space>
      </t-col>
    </t-row>

    <!-- 甘特图 -->
    <t-card title="生产排程甘特图" class="gantt-card">
      <div class="gantt-container">
        <div class="gantt-header">
          <div class="gantt-header-item">订单</div>
          <div class="gantt-header-item">产品</div>
          <div class="gantt-timeline">
            <div class="gantt-day">2/19</div>
            <div class="gantt-day">2/20</div>
            <div class="gantt-day">2/21</div>
            <div class="gantt-day">2/22</div>
            <div class="gantt-day">2/23</div>
            <div class="gantt-day">2/24</div>
            <div class="gantt-day">2/25</div>
          </div>
        </div>
        <div class="gantt-body">
          <div v-for="item in ganttData" :key="item.id" class="gantt-row">
            <div class="gantt-cell">{{ item.orderNo }}</div>
            <div class="gantt-cell">{{ item.productName }}</div>
            <div class="gantt-timeline">
              <div
                class="gantt-bar"
                :style="{
                  backgroundColor: item.color,
                  left: '0px',
                  width: `${(item.progress / 100) * 100}%`,
                }"
              >
                <div class="gantt-progress">{{ item.progress }}%</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </t-card>

    <!-- 排程列表表格 -->
    <t-card title="排程列表" class="table-card">
      <t-table
        :data="tableData"
        :columns="[
          { colKey: 'row-select', type: 'multiple', width: 50 },
          { colKey: 'scheduleNo', title: '排程编号', width: 150 },
          { colKey: 'orderNo', title: '生产订单', width: 150 },
          { colKey: 'productCode', title: '产品编码', width: 120 },
          { colKey: 'productName', title: '产品名称', width: 120 },
          { colKey: 'planQuantity', title: '计划数量', width: 100 },
          { colKey: 'productionLine', title: '生产线', width: 100 },
          { colKey: 'workShift', title: '班次', width: 80 },
          { colKey: 'planStartDate', title: '计划开始日期', width: 120 },
          { colKey: 'planEndDate', title: '计划结束日期', width: 120 },
          { colKey: 'priority', title: '优先级', width: 80 },
          { colKey: 'scheduleStatus', title: '排程状态', width: 100 },
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
        <template #scheduleStatus="{ row }">
          <t-tag :theme="getStatusTag(row.scheduleStatus).theme">
            {{ getStatusTag(row.scheduleStatus).label }}
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
      :header="dialogMode === 'add' ? '新增排程' : '编辑排程'"
      width="800px"
      :confirm-btn="{ content: '确定', theme: 'primary' }"
      @confirm="handleSubmit"
    >
      <t-form :data="formData" label-width="100px">
        <t-form-item label="排程编号" name="scheduleNo">
          <t-input v-model="formData.scheduleNo" placeholder="请输入排程编号" />
        </t-form-item>
        <t-form-item label="生产订单" name="orderNo">
          <t-input v-model="formData.orderNo" placeholder="请输入生产订单" />
        </t-form-item>
        <t-form-item label="产品编码" name="productCode">
          <t-input v-model="formData.productCode" placeholder="请输入产品编码" />
        </t-form-item>
        <t-form-item label="产品名称" name="productName">
          <t-input v-model="formData.productName" placeholder="请输入产品名称" />
        </t-form-item>
        <t-form-item label="计划数量" name="planQuantity">
          <t-input-number v-model="formData.planQuantity" :min="1" />
        </t-form-item>
        <t-form-item label="计划开始日期" name="planStartDate">
          <t-date-picker v-model="formData.planStartDate" />
        </t-form-item>
        <t-form-item label="计划结束日期" name="planEndDate">
          <t-date-picker v-model="formData.planEndDate" />
        </t-form-item>
        <t-form-item label="生产线" name="productionLine">
          <t-select v-model="formData.productionLine" placeholder="请选择生产线">
            <t-option value="生产线1" label="生产线1" />
            <t-option value="生产线2" label="生产线2" />
            <t-option value="生产线3" label="生产线3" />
            <t-option value="生产线4" label="生产线4" />
          </t-select>
        </t-form-item>
        <t-form-item label="班次" name="workShift">
          <t-select v-model="formData.workShift" placeholder="请选择班次">
            <t-option value="白班" label="白班" />
            <t-option value="夜班" label="夜班" />
          </t-select>
        </t-form-item>
        <t-form-item label="优先级" name="priority">
          <t-select v-model="formData.priority" placeholder="请选择优先级">
            <t-option value="高" label="高" />
            <t-option value="中" label="中" />
            <t-option value="低" label="低" />
          </t-select>
        </t-form-item>
        <t-form-item label="排程状态" name="scheduleStatus">
          <t-select v-model="formData.scheduleStatus" placeholder="请选择排程状态">
            <t-option value="scheduled" label="已排程" />
            <t-option value="in_production" label="生产中" />
            <t-option value="completed" label="已完成" />
            <t-option value="cancelled" label="已取消" />
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
      header="排程详情"
      width="800px"
      :footer="false"
    >
      <t-descriptions v-if="detailData" :column="2" bordered>
        <t-descriptions-item label="排程编号">{{ detailData.scheduleNo }}</t-descriptions-item>
        <t-descriptions-item label="生产订单">{{ detailData.orderNo }}</t-descriptions-item>
        <t-descriptions-item label="产品编码">{{ detailData.productCode }}</t-descriptions-item>
        <t-descriptions-item label="产品名称">{{ detailData.productName }}</t-descriptions-item>
        <t-descriptions-item label="计划数量">{{ detailData.planQuantity }}</t-descriptions-item>
        <t-descriptions-item label="生产线">{{ detailData.productionLine }}</t-descriptions-item>
        <t-descriptions-item label="班次">{{ detailData.workShift }}</t-descriptions-item>
        <t-descriptions-item label="优先级">{{ detailData.priority }}</t-descriptions-item>
        <t-descriptions-item label="计划开始日期">{{ detailData.planStartDate }}</t-descriptions-item>
        <t-descriptions-item label="计划结束日期">{{ detailData.planEndDate }}</t-descriptions-item>
        <t-descriptions-item label="排程状态">
          <t-tag :theme="getStatusTag(detailData.scheduleStatus).theme">
            {{ getStatusTag(detailData.scheduleStatus).label }}
          </t-tag>
        </t-descriptions-item>
      </t-descriptions>
    </t-dialog>
  </div>
</template>

<style scoped lang="less">
.production-schedule-optimization {
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

  .gantt-card {
    margin-bottom: 16px;

    .gantt-container {
      overflow-x: auto;
    }

    .gantt-header {
      display: flex;
      border-bottom: 1px solid #e8e8e8;
      padding-bottom: 8px;
      margin-bottom: 8px;
      font-weight: bold;

      .gantt-header-item {
        width: 150px;
        flex-shrink: 0;
      }

      .gantt-timeline {
        flex: 1;
        display: flex;
        gap: 8px;

        .gantt-day {
          width: 100px;
          text-align: center;
        }
      }
    }

    .gantt-body {
      .gantt-row {
        display: flex;
        padding: 8px 0;
        border-bottom: 1px solid #f0f0f0;

        .gantt-cell {
          width: 150px;
          flex-shrink: 0;
        }

        .gantt-timeline {
          flex: 1;
          position: relative;
          height: 32px;

          .gantt-bar {
            position: absolute;
            height: 100%;
            border-radius: 4px;
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-size: 12px;
            transition: all 0.3s;
          }

          .gantt-bar:hover {
            opacity: 0.8;
          }

          .gantt-progress {
            font-size: 12px;
            font-weight: bold;
          }
        }
      }
    }
  }

  .table-card {
    margin-bottom: 16px;
  }
}
</style>
