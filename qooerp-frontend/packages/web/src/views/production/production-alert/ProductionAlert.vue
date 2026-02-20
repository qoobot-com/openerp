<template>
  <div class="production-alert">
    <!-- 统计卡片 -->
    <t-row :gutter="[16, 16]" class="statistics-cards">
      <t-col :span="3">
        <t-card theme="error" :header-bordered="false">
          <div class="statistic-content">
            <div class="statistic-value">{{ statistics.critical }}</div>
            <div class="statistic-label">紧急预警</div>
          </div>
        </t-card>
      </t-col>
      <t-col :span="3">
        <t-card theme="warning" :header-bordered="false">
          <div class="statistic-content">
            <div class="statistic-value">{{ statistics.warning }}</div>
            <div class="statistic-label">一般预警</div>
          </div>
        </t-card>
      </t-col>
      <t-col :span="3">
        <t-card theme="info" :header-bordered="false">
          <div class="statistic-content">
            <div class="statistic-value">{{ statistics.today }}</div>
            <div class="statistic-label">今日预警</div>
          </div>
        </t-card>
      </t-col>
      <t-col :span="3">
        <t-card theme="success" :header-bordered="false">
          <div class="statistic-content">
            <div class="statistic-value">{{ statistics.resolved }}</div>
            <div class="statistic-label">已处理</div>
          </div>
        </t-card>
      </t-col>
    </t-row>

    <!-- 搜索卡片 -->
    <t-card title="预警查询" class="search-card">
      <t-form :data="searchForm" layout="inline" @submit="handleSearch">
        <t-form-item label="预警编号" name="alertNo">
          <t-input v-model="searchForm.alertNo" placeholder="请输入预警编号" clearable />
        </t-form-item>
        <t-form-item label="预警类型" name="alertType">
          <t-select v-model="searchForm.alertType" placeholder="请选择预警类型" clearable>
            <t-option value="stock" label="库存预警" />
            <t-option value="equipment" label="设备预警" />
            <t-option value="quality" label="质量预警" />
            <t-option value="schedule" label="进度预警" />
          </t-select>
        </t-form-item>
        <t-form-item label="预警级别" name="alertLevel">
          <t-select v-model="searchForm.alertLevel" placeholder="请选择预警级别" clearable>
            <t-option value="critical" label="紧急" />
            <t-option value="warning" label="一般" />
            <t-option value="info" label="提示" />
          </t-select>
        </t-form-item>
        <t-form-item label="处理状态" name="status">
          <t-select v-model="searchForm.status" placeholder="请选择处理状态" clearable>
            <t-option value="pending" label="待处理" />
            <t-option value="processing" label="处理中" />
            <t-option value="resolved" label="已处理" />
            <t-option value="ignored" label="已忽略" />
          </t-select>
        </t-form-item>
        <t-form-item label="预警时间" name="alertTime">
          <t-date-range-picker v-model="searchForm.alertTime" placeholder="请选择预警时间" />
        </t-form-item>
        <t-form-item>
          <t-space>
            <t-button theme="primary" type="submit">查询</t-button>
            <t-button theme="default" @click="handleReset">重置</t-button>
          </t-space>
        </t-form-item>
      </t-form>
    </t-card>

    <!-- 预警列表 -->
    <t-card title="预警列表" class="table-card">
      <template #actions>
        <t-space>
          <t-button theme="primary" @click="handleAdd">
            <template #icon><add-icon /></template>
            新增预警
          </t-button>
          <t-button theme="success" @click="handleBatchResolve">
            <template #icon><check-icon /></template>
            批量处理
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
        <template #alertLevel="{ row }">
          <t-tag v-if="row.alertLevel === 'critical'" theme="error" variant="light">紧急</t-tag>
          <t-tag v-else-if="row.alertLevel === 'warning'" theme="warning" variant="light">一般</t-tag>
          <t-tag v-else theme="info" variant="light">提示</t-tag>
        </template>
        <template #status="{ row }">
          <t-tag v-if="row.status === 'pending'" theme="error" variant="light">待处理</t-tag>
          <t-tag v-else-if="row.status === 'processing'" theme="warning" variant="light">处理中</t-tag>
          <t-tag v-else-if="row.status === 'resolved'" theme="success" variant="light">已处理</t-tag>
          <t-tag v-else theme="default" variant="light">已忽略</t-tag>
        </template>
        <template #action="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleView(row)">查看</t-link>
            <t-link v-if="row.status !== 'resolved'" theme="success" @click="handleResolve(row)">处理</t-link>
            <t-link v-if="row.status === 'pending'" theme="warning" @click="handleIgnore(row)">忽略</t-link>
          </t-space>
        </template>
      </t-table>
    </t-card>

    <!-- 新增预警弹窗 -->
    <t-dialog
      v-model:visible="dialogVisible"
      header="新增预警"
      :width="600"
      :confirm-btn="null"
      :cancel-btn="null"
      @close="handleDialogClose"
    >
      <t-form :data="formData" ref="formRef" :rules="rules" label-width="120px">
        <t-form-item label="预警编号" name="alertNo">
          <t-input v-model="formData.alertNo" placeholder="请输入预警编号" />
        </t-form-item>
        <t-form-item label="预警类型" name="alertType">
          <t-select v-model="formData.alertType" placeholder="请选择预警类型">
            <t-option value="stock" label="库存预警" />
            <t-option value="equipment" label="设备预警" />
            <t-option value="quality" label="质量预警" />
            <t-option value="schedule" label="进度预警" />
          </t-select>
        </t-form-item>
        <t-form-item label="预警级别" name="alertLevel">
          <t-select v-model="formData.alertLevel" placeholder="请选择预警级别">
            <t-option value="critical" label="紧急" />
            <t-option value="warning" label="一般" />
            <t-option value="info" label="提示" />
          </t-select>
        </t-form-item>
        <t-form-item label="预警内容" name="alertContent">
          <t-textarea v-model="formData.alertContent" placeholder="请输入预警内容" :maxlength="500" />
        </t-form-item>
        <t-form-item label="关联对象" name="relatedObject">
          <t-input v-model="formData.relatedObject" placeholder="请输入关联对象" />
        </t-form-item>
        <t-form-item label="备注" name="remark">
          <t-textarea v-model="formData.remark" placeholder="请输入备注" :maxlength="500" />
        </t-form-item>
      </t-form>
      <template #footer>
        <t-space>
          <t-button theme="default" @click="handleDialogClose">取消</t-button>
          <t-button theme="primary" @click="handleSubmit">确定</t-button>
        </t-space>
      </template>
    </t-dialog>

    <!-- 预警详情弹窗 -->
    <t-dialog
      v-model:visible="detailVisible"
      header="预警详情"
      :width="800"
      :footer="false"
    >
      <t-tabs v-model="detailTab" theme="card">
        <t-tab-panel value="basic" label="基本信息">
          <t-descriptions :column="2" bordered>
            <t-descriptions-item label="预警编号">{{ detailData.alertNo }}</t-descriptions-item>
            <t-descriptions-item label="预警类型">
              <t-tag v-if="detailData.alertType === 'stock'" theme="primary" variant="light">库存预警</t-tag>
              <t-tag v-else-if="detailData.alertType === 'equipment'" theme="warning" variant="light">设备预警</t-tag>
              <t-tag v-else-if="detailData.alertType === 'quality'" theme="error" variant="light">质量预警</t-tag>
              <t-tag v-else theme="info" variant="light">进度预警</t-tag>
            </t-descriptions-item>
            <t-descriptions-item label="预警级别">
              <t-tag v-if="detailData.alertLevel === 'critical'" theme="error" variant="light">紧急</t-tag>
              <t-tag v-else-if="detailData.alertLevel === 'warning'" theme="warning" variant="light">一般</t-tag>
              <t-tag v-else theme="info" variant="light">提示</t-tag>
            </t-descriptions-item>
            <t-descriptions-item label="处理状态">
              <t-tag v-if="detailData.status === 'pending'" theme="error" variant="light">待处理</t-tag>
              <t-tag v-else-if="detailData.status === 'processing'" theme="warning" variant="light">处理中</t-tag>
              <t-tag v-else-if="detailData.status === 'resolved'" theme="success" variant="light">已处理</t-tag>
              <t-tag v-else theme="default" variant="light">已忽略</t-tag>
            </t-descriptions-item>
            <t-descriptions-item label="预警时间">{{ detailData.alertTime }}</t-descriptions-item>
            <t-descriptions-item label="处理人">{{ detailData.handler }}</t-descriptions-item>
            <t-descriptions-item label="关联对象" :span="2">{{ detailData.relatedObject }}</t-descriptions-item>
            <t-descriptions-item label="预警内容" :span="2">{{ detailData.alertContent }}</t-descriptions-item>
            <t-descriptions-item label="处理说明" :span="2">{{ detailData.handleRemark }}</t-descriptions-item>
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

    <!-- 处理预警弹窗 -->
    <t-dialog
      v-model:visible="resolveVisible"
      header="处理预警"
      :width="600"
      :confirm-btn="null"
      :cancel-btn="null"
    >
      <t-form :data="resolveForm" ref="resolveFormRef" :rules="resolveRules" label-width="120px">
        <t-form-item label="预警编号">
          <t-input v-model="resolveForm.alertNo" disabled />
        </t-form-item>
        <t-form-item label="预警内容">
          <t-input v-model="resolveForm.alertContent" disabled />
        </t-form-item>
        <t-form-item label="处理方式" name="handleMethod">
          <t-select v-model="resolveForm.handleMethod" placeholder="请选择处理方式">
            <t-option value="immediate" label="立即处理" />
            <t-option value="scheduled" label="计划处理" />
            <t-option value="ignored" label="忽略" />
          </t-select>
        </t-form-item>
        <t-form-item label="处理说明" name="handleRemark">
          <t-textarea v-model="resolveForm.handleRemark" placeholder="请输入处理说明" :maxlength="500" />
        </t-form-item>
      </t-form>
      <template #footer>
        <t-space>
          <t-button theme="default" @click="resolveVisible = false">取消</t-button>
          <t-button theme="primary" @click="handleSubmitResolve">确定</t-button>
        </t-space>
      </template>
    </t-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { MessagePlugin, DialogPlugin } from 'tdesign-vue-next'
import { AddIcon, CheckIcon, DownloadIcon } from 'tdesign-icons-vue-next'

// 统计数据
const statistics = ref({
  critical: 15,
  warning: 45,
  today: 20,
  resolved: 150
})

// 搜索表单
const searchForm = reactive({
  alertNo: '',
  alertType: '',
  alertLevel: '',
  status: '',
  alertTime: []
})

// 表格数据
const loading = ref(false)
const tableData = ref([
  {
    id: 1,
    alertNo: 'ALERT-20260219-001',
    alertType: 'stock',
    alertLevel: 'critical',
    alertContent: '原材料A库存低于安全库存',
    relatedObject: '原材料A',
    status: 'pending',
    alertTime: '2026-02-19 10:00',
    handler: '',
    handleTime: '',
    handleRemark: '',
    remark: '需立即补货'
  },
  {
    id: 2,
    alertNo: 'ALERT-20260219-002',
    alertType: 'equipment',
    alertLevel: 'warning',
    alertContent: '设备B需要定期维护',
    relatedObject: '设备B',
    status: 'processing',
    alertTime: '2026-02-19 09:00',
    handler: '张三',
    handleTime: '2026-02-19 10:30',
    handleRemark: '已安排维护',
    remark: ''
  },
  {
    id: 3,
    alertNo: 'ALERT-20260219-003',
    alertType: 'quality',
    alertLevel: 'critical',
    alertContent: '成品C不合格率超过阈值',
    relatedObject: '成品C',
    status: 'resolved',
    alertTime: '2026-02-19 08:00',
    handler: '李四',
    handleTime: '2026-02-19 11:00',
    handleRemark: '已调整工艺参数',
    remark: '质量问题已解决'
  }
])

// 表格列定义
const columns = [
  { colKey: 'alertNo', title: '预警编号', width: 150 },
  { colKey: 'alertType', title: '预警类型', width: 100 },
  { colKey: 'alertLevel', title: '预警级别', width: 100, cell: 'alertLevel' },
  { colKey: 'alertContent', title: '预警内容', width: 250 },
  { colKey: 'relatedObject', title: '关联对象', width: 150 },
  { colKey: 'status', title: '处理状态', width: 100, cell: 'status' },
  { colKey: 'alertTime', title: '预警时间', width: 150 },
  { colKey: 'handler', title: '处理人', width: 100 },
  { colKey: 'action', title: '操作', width: 200, fixed: 'right', cell: 'action' }
]

// 分页
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 200
})

const rowKey = 'id'
const selectedRowKeys = ref<number[]>([])

// 新增预警弹窗
const dialogVisible = ref(false)
const formRef = ref()
const formData = reactive({
  alertNo: '',
  alertType: '',
  alertLevel: 'warning',
  alertContent: '',
  relatedObject: '',
  remark: ''
})

const rules = {
  alertNo: [{ required: true, message: '请输入预警编号' }],
  alertType: [{ required: true, message: '请选择预警类型' }],
  alertLevel: [{ required: true, message: '请选择预警级别' }],
  alertContent: [{ required: true, message: '请输入预警内容' }],
  relatedObject: [{ required: true, message: '请输入关联对象' }]
}

// 预警详情弹窗
const detailVisible = ref(false)
const detailTab = ref('basic')
const detailData = ref<any>({})

// 处理预警弹窗
const resolveVisible = ref(false)
const resolveFormRef = ref()
const resolveForm = reactive({
  alertNo: '',
  alertContent: '',
  handleMethod: 'immediate',
  handleRemark: ''
})

const resolveRules = {
  handleMethod: [{ required: true, message: '请选择处理方式' }],
  handleRemark: [{ required: true, message: '请输入处理说明' }]
}

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
    alertNo: '',
    alertType: '',
    alertLevel: '',
    status: '',
    alertTime: []
  })
  MessagePlugin.success('已重置')
}

// 新增预警
const handleAdd = () => {
  Object.assign(formData, {
    alertNo: '',
    alertType: '',
    alertLevel: 'warning',
    alertContent: '',
    relatedObject: '',
    remark: ''
  })
  dialogVisible.value = true
}

// 处理预警
const handleResolve = (row: any) => {
  Object.assign(resolveForm, {
    alertNo: row.alertNo,
    alertContent: row.alertContent,
    handleMethod: 'immediate',
    handleRemark: ''
  })
  resolveVisible.value = true
}

// 忽略预警
const handleIgnore = (row: any) => {
  const dialog = DialogPlugin({
    header: '确认忽略',
    body: `确认要忽略预警"${row.alertNo}"吗？`,
    confirmBtn: '确定',
    cancelBtn: '取消',
    onConfirm: () => {
      dialog.hide()
      MessagePlugin.success('预警已忽略')
    }
  })
}

// 批量处理
const handleBatchResolve = () => {
  if (selectedRowKeys.value.length === 0) {
    MessagePlugin.warning('请先选择预警')
    return
  }
  MessagePlugin.success(`已批量处理${selectedRowKeys.value.length}个预警`)
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
    MessagePlugin.success('新增成功')
  }
}

// 提交处理
const handleSubmitResolve = async () => {
  const valid = await resolveFormRef.value?.validate()
  if (valid === true) {
    resolveVisible.value = false
    MessagePlugin.success('处理成功')
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
      { id: 1, time: '2026-02-19 10:00', content: '预警触发', operator: '系统' },
      { id: 2, time: '2026-02-19 10:30', content: '开始处理', operator: '张三' }
    ]
  }
  detailTab.value = 'basic'
  detailVisible.value = true
}
</script>

<style scoped lang="less">
.production-alert {
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
