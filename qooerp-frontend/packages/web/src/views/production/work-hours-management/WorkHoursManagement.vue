<template>
  <div class="work-hours-management">
    <!-- 统计卡片 -->
    <t-row :gutter="[16, 16]" class="statistics-cards">
      <t-col :span="3">
        <t-card theme="primary1" :header-bordered="false">
          <div class="statistic-content">
            <div class="statistic-value">{{ statistics.total }}</div>
            <div class="statistic-label">工时总数</div>
          </div>
        </t-card>
      </t-col>
      <t-col :span="3">
        <t-card theme="success" :header-bordered="false">
          <div class="statistic-content">
            <div class="statistic-value">{{ statistics.completed }}</div>
            <div class="statistic-label">已完成</div>
          </div>
        </t-card>
      </t-col>
      <t-col :span="3">
        <t-card theme="warning" :header-bordered="false">
          <div class="statistic-content">
            <div class="statistic-value">{{ statistics.pending }}</div>
            <div class="statistic-label">待审核</div>
          </div>
        </t-card>
      </t-col>
      <t-col :span="3">
        <t-card theme="info" :header-bordered="false">
          <div class="statistic-content">
            <div class="statistic-value">{{ statistics.avgHours }}</div>
            <div class="statistic-label">平均工时(h)</div>
          </div>
        </t-card>
      </t-col>
    </t-row>

    <!-- 搜索卡片 -->
    <t-card title="工时查询" class="search-card">
      <t-form :data="searchForm" layout="inline" @submit="handleSearch">
        <t-form-item label="工时单号" name="workHoursNo">
          <t-input v-model="searchForm.workHoursNo" placeholder="请输入工时单号" clearable />
        </t-form-item>
        <t-form-item label="员工姓名" name="workerName">
          <t-input v-model="searchForm.workerName" placeholder="请输入员工姓名" clearable />
        </t-form-item>
        <t-form-item label="生产订单" name="orderNo">
          <t-input v-model="searchForm.orderNo" placeholder="请输入生产订单号" clearable />
        </t-form-item>
        <t-form-item label="工时状态" name="status">
          <t-select v-model="searchForm.status" placeholder="请选择工时状态" clearable>
            <t-option value="pending" label="待审核" />
            <t-option value="approved" label="已审核" />
            <t-option value="rejected" label="已驳回" />
          </t-select>
        </t-form-item>
        <t-form-item label="工时日期" name="workDate">
          <t-date-range-picker v-model="searchForm.workDate" placeholder="请选择工时日期" />
        </t-form-item>
        <t-form-item>
          <t-space>
            <t-button theme="primary" type="submit">查询</t-button>
            <t-button theme="default" @click="handleReset">重置</t-button>
          </t-space>
        </t-form-item>
      </t-form>
    </t-card>

    <!-- 工时列表 -->
    <t-card title="工时列表" class="table-card">
      <template #actions>
        <t-space>
          <t-button theme="primary" @click="handleAdd">
            <template #icon><add-icon /></template>
            新增工时
          </t-button>
          <t-button theme="success" @click="handleBatchApprove">
            <template #icon><check-icon /></template>
            批量审核
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
        :selected-row-keys="selectedRowKeys"
        :loading="loading"
        :pagination="pagination"
        @select-change="handleSelectChange"
        @page-change="handlePageChange"
      >
        <template #status="{ row }">
          <t-tag v-if="row.status === 'pending'" theme="warning" variant="light">待审核</t-tag>
          <t-tag v-else-if="row.status === 'approved'" theme="success" variant="light">已审核</t-tag>
          <t-tag v-else theme="error" variant="light">已驳回</t-tag>
        </template>
        <template #action="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleView(row)">查看</t-link>
            <t-link theme="primary" @click="handleEdit(row)">编辑</t-link>
            <t-link v-if="row.status === 'pending'" theme="success" @click="handleApprove(row)">审核</t-link>
            <t-link theme="danger" @click="handleDelete(row)">删除</t-link>
          </t-space>
        </template>
      </t-table>
    </t-card>

    <!-- 新增/编辑工时弹窗 -->
    <t-dialog
      v-model:visible="dialogVisible"
      :header="dialogTitle"
      :width="800"
      :confirm-btn="null"
      :cancel-btn="null"
      @close="handleDialogClose"
    >
      <t-tabs v-model="activeTab" theme="card">
        <t-tab-panel value="basic" label="基本信息">
          <t-form :data="formData" ref="formRef" :rules="rules" label-width="120px">
            <t-form-item label="工时单号" name="workHoursNo">
              <t-input v-model="formData.workHoursNo" placeholder="请输入工时单号" />
            </t-form-item>
            <t-form-item label="员工姓名" name="workerName">
              <t-select v-model="formData.workerName" placeholder="请选择员工" filterable>
                <t-option value="1" label="张三" />
                <t-option value="2" label="李四" />
                <t-option value="3" label="王五" />
              </t-select>
            </t-form-item>
            <t-form-item label="生产订单" name="orderNo">
              <t-input v-model="formData.orderNo" placeholder="请输入生产订单号" />
            </t-form-item>
            <t-form-item label="工序名称" name="processName">
              <t-input v-model="formData.processName" placeholder="请输入工序名称" />
            </t-form-item>
            <t-form-item label="工作日期" name="workDate">
              <t-date-picker v-model="formData.workDate" placeholder="请选择工作日期" />
            </t-form-item>
            <t-form-item label="工作时长(h)" name="workHours">
              <t-input-number v-model="formData.workHours" :min="0" :step="0.5" placeholder="请输入工作时长" />
            </t-form-item>
            <t-form-item label="产量" name="output">
              <t-input-number v-model="formData.output" :min="0" placeholder="请输入产量" />
            </t-form-item>
            <t-form-item label="备注" name="remark">
              <t-textarea v-model="formData.remark" placeholder="请输入备注" :maxlength="500" />
            </t-form-item>
          </t-form>
        </t-tab-panel>
        <t-tab-panel value="summary" label="汇总信息">
          <t-descriptions :column="2" bordered>
            <t-descriptions-item label="工时单号">{{ formData.workHoursNo }}</t-descriptions-item>
            <t-descriptions-item label="员工姓名">{{ formData.workerName }}</t-descriptions-item>
            <t-descriptions-item label="生产订单">{{ formData.orderNo }}</t-descriptions-item>
            <t-descriptions-item label="工序名称">{{ formData.processName }}</t-descriptions-item>
            <t-descriptions-item label="工作日期">{{ formData.workDate }}</t-descriptions-item>
            <t-descriptions-item label="工作时长">{{ formData.workHours }}小时</t-descriptions-item>
            <t-descriptions-item label="产量">{{ formData.output }}</t-descriptions-item>
            <t-descriptions-item label="单位工时产量">{{ formData.output ? (formData.output / formData.workHours).toFixed(2) : 0 }}</t-descriptions-item>
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

    <!-- 工时详情弹窗 -->
    <t-dialog
      v-model:visible="detailVisible"
      header="工时详情"
      :width="800"
      :footer="false"
    >
      <t-tabs v-model="detailTab" theme="card">
        <t-tab-panel value="basic" label="基本信息">
          <t-descriptions :column="2" bordered>
            <t-descriptions-item label="工时单号">{{ detailData.workHoursNo }}</t-descriptions-item>
            <t-descriptions-item label="员工姓名">{{ detailData.workerName }}</t-descriptions-item>
            <t-descriptions-item label="生产订单">{{ detailData.orderNo }}</t-descriptions-item>
            <t-descriptions-item label="工序名称">{{ detailData.processName }}</t-descriptions-item>
            <t-descriptions-item label="工作日期">{{ detailData.workDate }}</t-descriptions-item>
            <t-descriptions-item label="工作时长">{{ detailData.workHours }}小时</t-descriptions-item>
            <t-descriptions-item label="产量">{{ detailData.output }}</t-descriptions-item>
            <t-descriptions-item label="工时状态">
              <t-tag v-if="detailData.status === 'pending'" theme="warning" variant="light">待审核</t-tag>
              <t-tag v-else-if="detailData.status === 'approved'" theme="success" variant="light">已审核</t-tag>
              <t-tag v-else theme="error" variant="light">已驳回</t-tag>
            </t-descriptions-item>
            <t-descriptions-item label="审核人">{{ detailData.approver }}</t-descriptions-item>
            <t-descriptions-item label="审核时间">{{ detailData.approveTime }}</t-descriptions-item>
            <t-descriptions-item label="备注" :span="2">{{ detailData.remark }}</t-descriptions-item>
          </t-descriptions>
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
import { AddIcon, CheckIcon, DownloadIcon } from 'tdesign-icons-vue-next'

// 统计数据
const statistics = ref({
  total: 2500,
  completed: 2300,
  pending: 200,
  avgHours: 8
})

// 搜索表单
const searchForm = reactive({
  workHoursNo: '',
  workerName: '',
  orderNo: '',
  status: '',
  workDate: []
})

// 表格数据
const loading = ref(false)
const tableData = ref([
  {
    id: 1,
    workHoursNo: 'WH-20260219-001',
    workerName: '1',
    orderNo: 'PO-20260219-001',
    processName: '加工工序',
    workDate: '2026-02-19',
    workHours: 8,
    output: 100,
    status: 'approved',
    approver: '管理员',
    approveTime: '2026-02-19 17:00',
    remark: '正常工作'
  },
  {
    id: 2,
    workHoursNo: 'WH-20260219-002',
    workerName: '2',
    orderNo: 'PO-20260219-002',
    processName: '装配工序',
    workDate: '2026-02-19',
    workHours: 7,
    output: 80,
    status: 'pending',
    approver: '',
    approveTime: '',
    remark: '提前完成'
  },
  {
    id: 3,
    workHoursNo: 'WH-20260219-003',
    workerName: '3',
    orderNo: 'PO-20260219-003',
    processName: '检验工序',
    workDate: '2026-02-19',
    workHours: 8,
    output: 90,
    status: 'rejected',
    approver: '管理员',
    approveTime: '2026-02-19 16:00',
    remark: '数据有误'
  }
])

// 表格列定义
const columns = [
  { colKey: 'workHoursNo', title: '工时单号', width: 150 },
  { colKey: 'workerName', title: '员工姓名', width: 100 },
  { colKey: 'orderNo', title: '生产订单', width: 150 },
  { colKey: 'processName', title: '工序名称', width: 120 },
  { colKey: 'workDate', title: '工作日期', width: 120 },
  { colKey: 'workHours', title: '工作时长(h)', width: 120 },
  { colKey: 'output', title: '产量', width: 100 },
  { colKey: 'status', title: '工时状态', width: 100, cell: 'status' },
  { colKey: 'action', title: '操作', width: 200, fixed: 'right', cell: 'action' }
]

// 分页
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 2500
})

const rowKey = 'id'
const selectedRowKeys = ref<number[]>([])

// 新增/编辑弹窗
const dialogVisible = ref(false)
const dialogTitle = computed(() => (formData.id ? '编辑工时' : '新增工时'))
const activeTab = ref('basic')
const formRef = ref()
const formData = reactive({
  id: undefined,
  workHoursNo: '',
  workerName: '',
  orderNo: '',
  processName: '',
  workDate: '',
  workHours: 0,
  output: 0,
  remark: ''
})

const rules = {
  workHoursNo: [{ required: true, message: '请输入工时单号' }],
  workerName: [{ required: true, message: '请选择员工' }],
  orderNo: [{ required: true, message: '请输入生产订单号' }],
  processName: [{ required: true, message: '请输入工序名称' }],
  workDate: [{ required: true, message: '请选择工作日期' }],
  workHours: [{ required: true, message: '请输入工作时长' }],
  output: [{ required: true, message: '请输入产量' }]
}

// 工时详情弹窗
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
    workHoursNo: '',
    workerName: '',
    orderNo: '',
    status: '',
    workDate: []
  })
  MessagePlugin.success('已重置')
}

// 新增工时
const handleAdd = () => {
  Object.assign(formData, {
    id: undefined,
    workHoursNo: '',
    workerName: '',
    orderNo: '',
    processName: '',
    workDate: '',
    workHours: 0,
    output: 0,
    remark: ''
  })
  activeTab.value = 'basic'
  dialogVisible.value = true
}

// 编辑工时
const handleEdit = (row: any) => {
  Object.assign(formData, { ...row })
  activeTab.value = 'basic'
  dialogVisible.value = true
}

// 审核工时
const handleApprove = (row: any) => {
  const dialog = DialogPlugin({
    header: '审核工时',
    body: `确认审核工时单"${row.workHoursNo}"吗？`,
    confirmBtn: '确定',
    cancelBtn: '取消',
    onConfirm: () => {
      dialog.hide()
      MessagePlugin.success('审核成功')
    }
  })
}

// 删除工时
const handleDelete = (row: any) => {
  const dialog = DialogPlugin({
    header: '确认删除',
    body: `确认要删除工时单"${row.workHoursNo}"吗？`,
    confirmBtn: '确定',
    cancelBtn: '取消',
    onConfirm: () => {
      dialog.hide()
      MessagePlugin.success('删除成功')
    }
  })
}

// 批量审核
const handleBatchApprove = () => {
  if (selectedRowKeys.value.length === 0) {
    MessagePlugin.warning('请先选择工时单')
    return
  }
  MessagePlugin.success(`已批量审核${selectedRowKeys.value.length}个工时单`)
}

// 导出报表
const handleExport = () => {
  MessagePlugin.success('导出成功')
}

// 选择行
const handleSelectChange = (value: number[]) => {
  selectedRowKeys.value = value
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
      { id: 1, time: '2026-02-19 18:00', content: '提交工时单', operator: '张三' },
      { id: 2, time: '2026-02-19 17:00', content: '审核通过', operator: '管理员' }
    ]
  }
  detailTab.value = 'basic'
  detailVisible.value = true
}
</script>

<style scoped lang="less">
.work-hours-management {
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

  .log-operator {
    font-size: 12px;
    color: rgba(0, 0, 0, 0.6);
    margin-top: 4px;
  }
}
</style>
