<template>
  <div class="production-scheduling">
    <!-- 统计卡片 -->
    <t-row :gutter="[16, 16]" class="statistics-cards">
      <t-col :span="3">
        <t-card theme="primary1" :header-bordered="false">
          <div class="statistic-content">
            <div class="statistic-value">{{ statistics.total }}</div>
            <div class="statistic-label">排程总数</div>
          </div>
        </t-card>
      </t-col>
      <t-col :span="3">
        <t-card theme="success" :header-bordered="false">
          <div class="statistic-content">
            <div class="statistic-value">{{ statistics.scheduled }}</div>
            <div class="statistic-label">已排程</div>
          </div>
        </t-card>
      </t-col>
      <t-col :span="3">
        <t-card theme="warning" :header-bordered="false">
          <div class="statistic-content">
            <div class="statistic-value">{{ statistics.pending }}</div>
            <div class="statistic-label">待排程</div>
          </div>
        </t-card>
      </t-col>
      <t-col :span="3">
        <t-card theme="info" :header-bordered="false">
          <div class="statistic-content">
            <div class="statistic-value">{{ statistics.utilization }}%</div>
            <div class="statistic-label">设备利用率</div>
          </div>
        </t-card>
      </t-col>
    </t-row>

    <!-- 搜索卡片 -->
    <t-card title="排程查询" class="search-card">
      <t-form :data="searchForm" layout="inline" @submit="handleSearch">
        <t-form-item label="排程单号" name="scheduleNo">
          <t-input v-model="searchForm.scheduleNo" placeholder="请输入排程单号" clearable />
        </t-form-item>
        <t-form-item label="产品名称" name="productName">
          <t-input v-model="searchForm.productName" placeholder="请输入产品名称" clearable />
        </t-form-item>
        <t-form-item label="车间" name="workshop">
          <t-select v-model="searchForm.workshop" placeholder="请选择车间" clearable>
            <t-option value="A" label="A车间" />
            <t-option value="B" label="B车间" />
            <t-option value="C" label="C车间" />
          </t-select>
        </t-form-item>
        <t-form-item label="排程日期" name="scheduleDate">
          <t-date-range-picker v-model="searchForm.scheduleDate" placeholder="请选择排程日期" />
        </t-form-item>
        <t-form-item>
          <t-space>
            <t-button theme="primary" type="submit">查询</t-button>
            <t-button theme="default" @click="handleReset">重置</t-button>
          </t-space>
        </t-form-item>
      </t-form>
    </t-card>

    <!-- 排程列表 -->
    <t-card title="排程列表" class="table-card">
      <template #actions>
        <t-space>
          <t-button theme="primary" @click="handleAdd">
            <template #icon><add-icon /></template>
            新增排程
          </t-button>
          <t-button theme="success" @click="handleAutoSchedule">
            <template #icon><auto-complete-icon /></template>
            自动排程
          </t-button>
          <t-button theme="default" @click="handleExport">
            <template #icon><download-icon /></template>
            导出报表
          </t-button>
        </t-space>
      </template>
      <t-table
        :data="tableData"
        :columns="columns"
        :row-key="rowKey"
        :loading="loading"
        :pagination="pagination"
        @page-change="handlePageChange"
      >
        <template #status="{ row }">
          <t-tag v-if="row.status === 'pending'" theme="default" variant="light">待排程</t-tag>
          <t-tag v-else-if="row.status === 'scheduled'" theme="success" variant="light">已排程</t-tag>
          <t-tag v-else-if="row.status === 'in-progress'" theme="primary" variant="light">进行中</t-tag>
          <t-tag v-else theme="info" variant="light">已完成</t-tag>
        </template>
        <template #progress="{ row }">
          <t-progress :percentage="row.progress" size="small" style="width: 100px" />
        </template>
        <template #action="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleView(row)">查看</t-link>
            <t-link theme="primary" @click="handleEdit(row)">编辑</t-link>
            <t-link v-if="row.status === 'scheduled'" theme="success" @click="handleStart(row)">开始生产</t-link>
            <t-link theme="danger" @click="handleDelete(row)">删除</t-link>
          </t-space>
        </template>
      </t-table>
    </t-card>

    <!-- 新增/编辑排程弹窗 -->
    <t-dialog
      v-model:visible="dialogVisible"
      :header="dialogTitle"
      :width="900"
      :confirm-btn="null"
      :cancel-btn="null"
      @close="handleDialogClose"
    >
      <t-tabs v-model="activeTab" theme="card">
        <t-tab-panel value="basic" label="基本信息">
          <t-form :data="formData" ref="formRef" :rules="rules" label-width="120px">
            <t-form-item label="排程单号" name="scheduleNo">
              <t-input v-model="formData.scheduleNo" placeholder="请输入排程单号" />
            </t-form-item>
            <t-form-item label="产品编号" name="productCode">
              <t-input v-model="formData.productCode" placeholder="请输入产品编号" />
            </t-form-item>
            <t-form-item label="产品名称" name="productName">
              <t-input v-model="formData.productName" placeholder="请输入产品名称" />
            </t-form-item>
            <t-form-item label="规格型号" name="specification">
              <t-input v-model="formData.specification" placeholder="请输入规格型号" />
            </t-form-item>
            <t-form-item label="排程数量" name="scheduleQty">
              <t-input-number v-model="formData.scheduleQty" :min="1" placeholder="请输入排程数量" />
            </t-form-item>
            <t-form-item label="所属车间" name="workshop">
              <t-select v-model="formData.workshop" placeholder="请选择所属车间">
                <t-option value="A" label="A车间" />
                <t-option value="B" label="B车间" />
                <t-option value="C" label="C车间" />
              </t-select>
            </t-form-item>
            <t-form-item label="使用设备" name="equipment">
              <t-select v-model="formData.equipment" placeholder="请选择使用设备" filterable>
                <t-option value="EQ-001" label="数控机床A1" />
                <t-option value="EQ-002" label="检测仪器B1" />
                <t-option value="EQ-003" label="加工中心C1" />
              </t-select>
            </t-form-item>
            <t-form-item label="计划开始时间" name="startTime">
              <t-date-picker v-model="formData.startTime" placeholder="请选择计划开始时间" />
            </t-form-item>
            <t-form-item label="计划结束时间" name="endTime">
              <t-date-picker v-model="formData.endTime" placeholder="请选择计划结束时间" />
            </t-form-item>
            <t-form-item label="备注" name="remark">
              <t-textarea v-model="formData.remark" placeholder="请输入备注" :maxlength="500" />
            </t-form-item>
          </t-form>
        </t-tab-panel>
        <t-tab-panel value="gantt" label="甘特图">
          <div class="gantt-chart">
            <t-alert theme="info" message="甘特图预览" :default="true" />
            <div class="gantt-placeholder">
              <t-icon name="time" size="48px" style="color: rgba(0,0,0,0.25)" />
              <div>甘特图区域</div>
              <div>显示排程时间线</div>
            </div>
          </div>
        </t-tab-panel>
        <t-tab-panel value="summary" label="汇总信息">
          <t-descriptions :column="2" bordered>
            <t-descriptions-item label="排程单号">{{ formData.scheduleNo }}</t-descriptions-item>
            <t-descriptions-item label="产品名称">{{ formData.productName }}</t-descriptions-item>
            <t-descriptions-item label="规格型号">{{ formData.specification }}</t-descriptions-item>
            <t-descriptions-item label="排程数量">{{ formData.scheduleQty }}</t-descriptions-item>
            <t-descriptions-item label="所属车间">{{ formData.workshop }}车间</t-descriptions-item>
            <t-descriptions-item label="使用设备">{{ formData.equipment }}</t-descriptions-item>
            <t-descriptions-item label="计划开始时间">{{ formData.startTime }}</t-descriptions-item>
            <t-descriptions-item label="计划结束时间">{{ formData.endTime }}</t-descriptions-item>
            <t-descriptions-item label="预计工时(h)" :span="2">{{ estimatedHours }}</t-descriptions-item>
          </t-descriptions>
        </t-tab-panel>
      </t-tabs>
      <template #footer>
        <t-space>
          <t-button theme="default" @click="handleDialogClose">取消</t-button>
          <t-button theme="primary" @click="handleSubmit">确定</t-button>
        </t-space>
      </template>
    </t-dialog>

    <!-- 排程详情弹窗 -->
    <t-dialog
      v-model:visible="detailVisible"
      header="排程详情"
      :width="1000"
      :footer="false"
    >
      <t-tabs v-model="detailTab" theme="card">
        <t-tab-panel value="basic" label="基本信息">
          <t-descriptions :column="2" bordered>
            <t-descriptions-item label="排程单号">{{ detailData.scheduleNo }}</t-descriptions-item>
            <t-descriptions-item label="产品名称">{{ detailData.productName }}</t-descriptions-item>
            <t-descriptions-item label="规格型号">{{ detailData.specification }}</t-descriptions-item>
            <t-descriptions-item label="排程数量">{{ detailData.scheduleQty }}</t-descriptions-item>
            <t-descriptions-item label="所属车间">{{ detailData.workshop }}车间</t-descriptions-item>
            <t-descriptions-item label="使用设备">{{ detailData.equipment }}</t-descriptions-item>
            <t-descriptions-item label="计划开始时间">{{ detailData.startTime }}</t-descriptions-item>
            <t-descriptions-item label="计划结束时间">{{ detailData.endTime }}</t-descriptions-item>
            <t-descriptions-item label="排程状态">
              <t-tag v-if="detailData.status === 'pending'" theme="default" variant="light">待排程</t-tag>
              <t-tag v-else-if="detailData.status === 'scheduled'" theme="success" variant="light">已排程</t-tag>
              <t-tag v-else-if="detailData.status === 'in-progress'" theme="primary" variant="light">进行中</t-tag>
              <t-tag v-else theme="info" variant="light">已完成</t-tag>
            </t-descriptions-item>
            <t-descriptions-item label="完成进度">{{ detailData.progress }}%</t-descriptions-item>
            <t-descriptions-item label="备注" :span="2">{{ detailData.remark }}</t-descriptions-item>
          </t-descriptions>
        </t-tab-panel>
        <t-tab-panel value="process" label="工序流程">
          <t-steps :current="activeStep" theme="simple">
            <t-step-item
              v-for="(proc, index) in detailData.processes || []"
              :key="index"
              :title="proc.processName"
              :content="`工时: ${proc.workHours}小时`"
            />
          </t-steps>
          <t-divider />
          <t-table
            :data="detailData.processes || []"
            :columns="processColumns"
            :pagination="false"
            row-key="id"
          >
            <template #status="{ row }">
              <t-tag v-if="row.status === 'pending'" theme="default" variant="light">待开始</t-tag>
              <t-tag v-else-if="row.status === 'in-progress'" theme="primary" variant="light">进行中</t-tag>
              <t-tag v-else theme="success" variant="light">已完成</t-tag>
            </template>
          </t-table>
        </t-tab-panel>
        <t-tab-panel value="log" label="操作日志">
          <t-timeline>
            <t-timeline-item v-for="log in detailData.logs || []" :key="log.id" :label="log.time">
              <div>{{ log.content }}</div>
              <div class="log-operator">操作人: {{ log.operator }}</div>
            </t-timeline-item>
          </t-timeline>
        </t-tab-panel>
      </t-tabs>
    </t-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { MessagePlugin, DialogPlugin } from 'tdesign-vue-next'
import { AddIcon, DownloadIcon } from 'tdesign-icons-vue-next'

// 统计数据
const statistics = ref({
  total: 80,
  scheduled: 60,
  pending: 20,
  utilization: 85
})

// 搜索表单
const searchForm = reactive({
  scheduleNo: '',
  productName: '',
  workshop: '',
  scheduleDate: []
})

// 表格数据
const loading = ref(false)
const tableData = ref([
  {
    id: 1,
    scheduleNo: 'PS-20260219-001',
    productCode: 'P-001',
    productName: '成品A',
    specification: '规格A',
    scheduleQty: 100,
    workshop: 'A',
    equipment: 'EQ-001',
    startTime: '2026-02-19 08:00',
    endTime: '2026-02-20 18:00',
    status: 'in-progress',
    progress: 50,
    remark: '正常生产',
    processes: [
      { id: 1, processName: '备料', workHours: 2, status: 'completed' },
      { id: 2, processName: '加工', workHours: 8, status: 'in-progress' },
      { id: 3, processName: '装配', workHours: 4, status: 'pending' }
    ]
  },
  {
    id: 2,
    scheduleNo: 'PS-20260219-002',
    productCode: 'P-002',
    productName: '成品B',
    specification: '规格B',
    scheduleQty: 50,
    workshop: 'B',
    equipment: 'EQ-002',
    startTime: '2026-02-19 09:00',
    endTime: '2026-02-19 17:00',
    status: 'scheduled',
    progress: 0,
    remark: '待开始',
    processes: [
      { id: 1, processName: '备料', workHours: 1, status: 'pending' },
      { id: 2, processName: '装配', workHours: 6, status: 'pending' }
    ]
  },
  {
    id: 3,
    scheduleNo: 'PS-20260219-003',
    productCode: 'P-003',
    productName: '成品C',
    specification: '规格C',
    scheduleQty: 80,
    workshop: 'C',
    equipment: 'EQ-003',
    startTime: '2026-02-20 08:00',
    endTime: '2026-02-21 18:00',
    status: 'pending',
    progress: 0,
    remark: '待排程',
    processes: []
  }
])

// 表格列定义
const columns = [
  { colKey: 'scheduleNo', title: '排程单号', width: 150 },
  { colKey: 'productName', title: '产品名称', width: 120 },
  { colKey: 'specification', title: '规格型号', width: 120 },
  { colKey: 'scheduleQty', title: '排程数量', width: 100 },
  { colKey: 'workshop', title: '车间', width: 80 },
  { colKey: 'equipment', title: '使用设备', width: 120 },
  { colKey: 'startTime', title: '开始时间', width: 150 },
  { colKey: 'endTime', title: '结束时间', width: 150 },
  { colKey: 'progress', title: '完成进度', width: 120, cell: 'progress' },
  { colKey: 'status', title: '排程状态', width: 100, cell: 'status' },
  { colKey: 'action', title: '操作', width: 200, fixed: 'right', cell: 'action' }
]

const processColumns = [
  { colKey: 'processName', title: '工序名称', width: 150 },
  { colKey: 'workHours', title: '工时(小时)', width: 120 },
  { colKey: 'status', title: '状态', width: 100, cell: 'status' }
]

// 分页
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 80
})

const rowKey = 'id'
const activeStep = ref(0)

// 新增/编辑弹窗
const dialogVisible = ref(false)
const dialogTitle = computed(() => (formData.id ? '编辑排程' : '新增排程'))
const activeTab = ref('basic')
const formRef = ref()
const formData = reactive({
  id: undefined,
  scheduleNo: '',
  productCode: '',
  productName: '',
  specification: '',
  scheduleQty: 1,
  workshop: '',
  equipment: '',
  startTime: '',
  endTime: '',
  remark: ''
})

const rules = {
  scheduleNo: [{ required: true, message: '请输入排程单号' }],
  productCode: [{ required: true, message: '请输入产品编号' }],
  productName: [{ required: true, message: '请输入产品名称' }],
  specification: [{ required: true, message: '请输入规格型号' }],
  scheduleQty: [{ required: true, message: '请输入排程数量' }],
  workshop: [{ required: true, message: '请选择所属车间' }],
  equipment: [{ required: true, message: '请选择使用设备' }],
  startTime: [{ required: true, message: '请选择计划开始时间' }],
  endTime: [{ required: true, message: '请选择计划结束时间' }]
}

// 计算预计工时
const estimatedHours = computed(() => {
  if (formData.startTime && formData.endTime) {
    const start = new Date(formData.startTime).getTime()
    const end = new Date(formData.endTime).getTime()
    return ((end - start) / (1000 * 60 * 60)).toFixed(1)
  }
  return 0
})

// 排程详情弹窗
const detailVisible = ref(false)
const detailTab = ref('basic')
const detailData = ref<any>({})

// 搜索
const handleSearch = () => {
  loading.value = true
  setTimeout(() => {
    loading.value = false
    MessagePlugin.success('查询成功')
  }, 500)
}

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    scheduleNo: '',
    productName: '',
    workshop: '',
    scheduleDate: []
  })
  MessagePlugin.success('已重置')
}

// 新增排程
const handleAdd = () => {
  Object.assign(formData, {
    id: undefined,
    scheduleNo: '',
    productCode: '',
    productName: '',
    specification: '',
    scheduleQty: 1,
    workshop: '',
    equipment: '',
    startTime: '',
    endTime: '',
    remark: ''
  })
  activeTab.value = 'basic'
  dialogVisible.value = true
}

// 编辑排程
const handleEdit = (row: any) => {
  Object.assign(formData, { ...row })
  activeTab.value = 'basic'
  dialogVisible.value = true
}

// 开始生产
const handleStart = (row: any) => {
  const dialog = DialogPlugin({
    header: '开始生产',
    body: `确认开始生产"${row.productName}"吗？`,
    confirmBtn: '确定',
    cancelBtn: '取消',
    onConfirm: () => {
      dialog.hide()
      MessagePlugin.success('生产已开始')
    }
  })
}

// 删除排程
const handleDelete = (row: any) => {
  const dialog = DialogPlugin({
    header: '确认删除',
    body: `确认要删除排程"${row.scheduleNo}"吗？`,
    confirmBtn: '确定',
    cancelBtn: '取消',
    onConfirm: () => {
      dialog.hide()
      MessagePlugin.success('删除成功')
    }
  })
}

// 自动排程
const handleAutoSchedule = () => {
  MessagePlugin.loading('自动排程计算中...', 2000)
  setTimeout(() => {
    MessagePlugin.success('自动排程完成')
  }, 2000)
}

// 导出报表
const handleExport = () => {
  MessagePlugin.success('导出成功')
}

// 分页变化
const handlePageChange = (pageInfo: any) => {
  pagination.current = pageInfo.current
  pagination.pageSize = pageInfo.pageSize
}

// 提交表单
const handleSubmit = async () => {
  const valid = await formRef.value?.validate()
  if (valid === true) {
    dialogVisible.value = false
    MessagePlugin.success(formData.id ? '更新成功' : '新增成功')
  }
}

// 关闭弹窗
const handleDialogClose = () => {
  dialogVisible.value = false
}

// 查看详情
const handleView = (row: any) => {
  detailData.value = {
    ...row,
    logs: [
      { id: 1, time: '2026-02-19 08:00', content: '创建排程', operator: '计划员' },
      { id: 2, time: '2026-02-19 09:00', content: '开始生产', operator: '操作员' }
    ]
  }
  detailTab.value = 'basic'
  detailVisible.value = true
}
</script>

<style scoped lang="less">
.production-scheduling {
  padding: 24px;

  .statistics-cards {
    margin-bottom: 24px;

    .statistic-content {
      text-align: center;
      padding: 16px 0;

      .statistic-value {
        font-size: 32px;
        font-weight: 600;
        margin-bottom: 8px;
      }

      .statistic-label {
        font-size: 14px;
        color: rgba(0, 0, 0, 0.6);
      }
    }
  }

  .search-card,
  .table-card {
    margin-bottom: 24px;
  }

  .gantt-chart {
    .gantt-placeholder {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      height: 400px;
      background: rgba(0, 0, 0, 0.02);
      border-radius: 4px;
      margin-top: 16px;

      div {
        margin-top: 8px;
        color: rgba(0, 0, 0, 0.45);
      }
    }
  }

  .log-operator {
    font-size: 12px;
    color: rgba(0, 0, 0, 0.6);
    margin-top: 4px;
  }
}
</style>
