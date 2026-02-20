<template>
  <div class="production-plan-container">
    <!-- 统计卡片 -->
    <t-row :gutter="[16, 16]" class="mb-4">
      <t-col :span="3">
        <t-card theme="primary1" :header="false" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon">
              <t-icon name="file-list" size="40px" />
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.planCount }}</div>
              <div class="stat-label">计划数</div>
            </div>
          </div>
        </t-card>
      </t-col>
      <t-col :span="3">
        <t-card theme="warning" :header="false" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon">
              <t-icon name="time" size="40px" />
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.pendingCount }}</div>
              <div class="stat-label">待执行</div>
            </div>
          </div>
        </t-card>
      </t-col>
      <t-col :span="3">
        <t-card theme="success" :header="false" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon">
              <t-icon name="check-circle" size="40px" />
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.completedCount }}</div>
              <div class="stat-label">已完成</div>
            </div>
          </div>
        </t-card>
      </t-col>
      <t-col :span="3">
        <t-card theme="info" :header="false" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon">
              <t-icon name="cart" size="40px" />
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalQuantity }}</div>
              <div class="stat-label">计划产量</div>
            </div>
          </div>
        </t-card>
      </t-col>
    </t-row>

    <!-- 搜索卡片 -->
    <t-card class="mb-4">
      <t-form :data="searchForm" layout="inline" @submit="handleSearch" @reset="handleReset">
        <t-form-item label="计划编号" name="planNo">
          <t-input v-model="searchForm.planNo" placeholder="请输入计划编号" clearable style="width: 180px" />
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
        <t-form-item label="计划状态" name="status">
          <t-select
            v-model="searchForm.status"
            placeholder="请选择计划状态"
            style="width: 140px"
            clearable
          >
            <t-option value="draft" label="草稿" />
            <t-option value="pending" label="待执行" />
            <t-option value="inProgress" label="执行中" />
            <t-option value="completed" label="已完成" />
            <t-option value="cancelled" label="已取消" />
          </t-select>
        </t-form-item>
        <t-form-item label="计划日期" name="planDate">
          <t-date-range-picker
            v-model="searchForm.planDate"
            placeholder="请选择计划日期"
            style="width: 280px"
            clearable
          />
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
        <t-button theme="primary" @click="handleAdd">
          <template #icon>
            <t-icon name="add" />
          </template>
          新增计划
        </t-button>
        <t-button theme="success" @click="handleBatchStart">
          <template #icon>
            <t-icon name="play-circle" />
          </template>
          批量开始
        </t-button>
        <t-button theme="default" @click="handleExport">
          <template #icon>
            <t-icon name="download" />
          </template>
          导出报表
        </t-button>
      </t-space>
    </t-card>

    <!-- 计划列表 -->
    <t-card>
      <t-table
        :data="planList"
        :columns="columns"
        :pagination="pagination"
        :loading="loading"
        stripe
        hover
        row-key="id"
        :selected-row-keys="selectedRowKeys"
        @select-change="handleSelectChange"
      >
        <template #priority="{ row }">
          <t-tag v-if="row.priority === 'high'" theme="danger">高</t-tag>
          <t-tag v-else-if="row.priority === 'medium'" theme="warning">中</t-tag>
          <t-tag v-else theme="default">低</t-tag>
        </template>
        <template #status="{ row }">
          <t-tag v-if="row.status === 'draft'" theme="default">草稿</t-tag>
          <t-tag v-else-if="row.status === 'pending'" theme="warning">待执行</t-tag>
          <t-tag v-else-if="row.status === 'inProgress'" theme="primary">执行中</t-tag>
          <t-tag v-else-if="row.status === 'completed'" theme="success">已完成</t-tag>
          <t-tag v-else theme="default">已取消</t-tag>
        </template>
        <template #progress="{ row }">
          <t-progress :percentage="row.progress" :label="true" size="small" />
        </template>
        <template #action="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleView(row)">查看</t-link>
            <t-link v-if="row.status === 'draft'" theme="warning" @click="handleEdit(row)">编辑</t-link>
            <t-link v-if="row.status === 'pending'" theme="success" @click="handleStart(row)">开始</t-link>
            <t-link v-if="row.status === 'inProgress'" theme="warning" @click="handlePause(row)">暂停</t-link>
            <t-link v-if="row.status === 'inProgress'" theme="success" @click="handleComplete(row)">完成</t-link>
            <t-link v-if="['draft', 'pending'].includes(row.status)" theme="danger" @click="handleCancel(row)">取消</t-link>
          </t-space>
        </template>
      </t-table>
    </t-card>

    <!-- 新增/编辑计划弹窗 -->
    <t-dialog
      v-model:visible="editDialogVisible"
      :header="isEdit ? '编辑生产计划' : '新增生产计划'"
      width="900px"
      :confirm-btn="null"
      :cancel-btn="null"
    >
      <t-tabs v-model="activeEditTab">
        <t-tab-panel value="basic" label="基本信息">
          <t-form :data="editForm" label-width="120px">
            <t-row :gutter="16">
              <t-col :span="12">
                <t-form-item label="计划编号">
                  <span>{{ editForm.planNo || '自动生成' }}</span>
                </t-form-item>
              </t-col>
              <t-col :span="12">
                <t-form-item label="计划日期" name="planDate">
                  <t-date-picker v-model="editForm.planDate" placeholder="请选择计划日期" />
                </t-form-item>
              </t-col>
            </t-row>
            <t-row :gutter="16">
              <t-col :span="12">
                <t-form-item label="产品" name="productId">
                  <t-select v-model="editForm.productId" placeholder="请选择产品" filterable>
                    <t-option v-for="item in productList" :key="item.value" :value="item.value" :label="item.label" />
                  </t-select>
                </t-form-item>
              </t-col>
              <t-col :span="12">
                <t-form-item label="规格型号">
                  <span>{{ getProductSpec(editForm.productId) }}</span>
                </t-form-item>
              </t-col>
            </t-row>
            <t-row :gutter="16">
              <t-col :span="12">
                <t-form-item label="计划数量" name="planQuantity">
                  <t-input-number v-model="editForm.planQuantity" placeholder="请输入计划数量" :min="1" />
                </t-form-item>
              </t-col>
              <t-col :span="12">
                <t-form-item label="单位">
                  <span>{{ getProductUnit(editForm.productId) }}</span>
                </t-form-item>
              </t-col>
            </t-row>
            <t-row :gutter="16">
              <t-col :span="12">
                <t-form-item label="开始日期" name="startDate">
                  <t-date-picker v-model="editForm.startDate" placeholder="请选择开始日期" />
                </t-form-item>
              </t-col>
              <t-col :span="12">
                <t-form-item label="完成日期" name="endDate">
                  <t-date-picker v-model="editForm.endDate" placeholder="请选择完成日期" />
                </t-form-item>
              </t-col>
            </t-row>
            <t-row :gutter="16">
              <t-col :span="12">
                <t-form-item label="优先级" name="priority">
                  <t-select v-model="editForm.priority" placeholder="请选择优先级">
                    <t-option value="high" label="高" />
                    <t-option value="medium" label="中" />
                    <t-option value="low" label="低" />
                  </t-select>
                </t-form-item>
              </t-col>
              <t-col :span="12">
                <t-form-item label="生产车间" name="workshopId">
                  <t-select v-model="editForm.workshopId" placeholder="请选择生产车间">
                    <t-option value="1" label="车间A" />
                    <t-option value="2" label="车间B" />
                    <t-option value="3" label="车间C" />
                  </t-select>
                </t-form-item>
              </t-col>
            </t-row>
            <t-form-item label="备注" name="remark">
              <t-textarea
                v-model="editForm.remark"
                placeholder="请输入备注"
                :autosize="{ minRows: 3, maxRows: 5 }"
              />
            </t-form-item>
          </t-form>
        </t-tab-panel>
        <t-tab-panel value="material" label="物料清单">
          <div class="detail-header">
            <t-button theme="primary" size="small" @click="handleAddMaterial">
              <template #icon>
                <t-icon name="add" />
              </template>
              添加物料
            </t-button>
          </div>
          <t-table :data="editForm.materials" :columns="materialColumns" stripe size="small">
            <template #action="{ rowIndex }">
              <t-link theme="danger" @click="handleRemoveMaterial(rowIndex)">删除</t-link>
            </template>
          </t-table>
        </t-tab-panel>
        <t-tab-panel value="process" label="工序流程">
          <div class="detail-header">
            <t-button theme="primary" size="small" @click="handleAddProcess">
              <template #icon>
                <t-icon name="add" />
              </template>
              添加工序
            </t-button>
          </div>
          <t-table :data="editForm.processes" :columns="processColumns" stripe size="small">
            <template #action="{ rowIndex }">
              <t-link theme="danger" @click="handleRemoveProcess(rowIndex)">删除</t-link>
            </template>
          </t-table>
        </t-tab-panel>
        <t-tab-panel value="summary" label="汇总信息">
          <t-descriptions :column="2" bordered>
            <t-descriptions-item label="计划数量">{{ editForm.planQuantity }}</t-descriptions-item>
            <t-descriptions-item label="预计完工日期">{{ editForm.endDate }}</t-descriptions-item>
            <t-descriptions-item label="物料种类">{{ editForm.materials?.length || 0 }}</t-descriptions-item>
            <t-descriptions-item label="工序数量">{{ editForm.processes?.length || 0 }}</t-descriptions-item>
            <t-descriptions-item label="预计工时">{{ getTotalWorkHours() }}小时</t-descriptions-item>
            <t-descriptions-item label="生产车间">{{ getWorkshopName(editForm.workshopId) }}</t-descriptions-item>
          </t-descriptions>
        </t-tab-panel>
      </t-tabs>
      <div class="dialog-footer">
        <t-space>
          <t-button theme="primary" @click="handleEditSubmit">确认</t-button>
          <t-button theme="default" @click="editDialogVisible = false">取消</t-button>
        </t-space>
      </div>
    </t-dialog>

    <!-- 计划详情弹窗 -->
    <t-dialog
      v-model:visible="detailDialogVisible"
      header="生产计划详情"
      width="900px"
      :confirm-btn="null"
      :cancel-btn="null"
    >
      <t-tabs v-model="activeDetailTab">
        <t-tab-panel value="basic" label="基本信息">
          <t-descriptions :column="2" bordered>
            <t-descriptions-item label="计划编号">{{ currentPlan.planNo }}</t-descriptions-item>
            <t-descriptions-item label="计划日期">{{ currentPlan.planDate }}</t-descriptions-item>
            <t-descriptions-item label="产品">{{ currentPlan.productName }}</t-descriptions-item>
            <t-descriptions-item label="规格型号">{{ currentPlan.spec }}</t-descriptions-item>
            <t-descriptions-item label="计划数量">{{ currentPlan.planQuantity }}</t-descriptions-item>
            <t-descriptions-item label="单位">{{ currentPlan.unit }}</t-descriptions-item>
            <t-descriptions-item label="完成数量">{{ currentPlan.completedQuantity }}</t-descriptions-item>
            <t-descriptions-item label="完成进度">
              <t-progress :percentage="currentPlan.progress" :label="true" size="small" />
            </t-descriptions-item>
            <t-descriptions-item label="开始日期">{{ currentPlan.startDate }}</t-descriptions-item>
            <t-descriptions-item label="完成日期">{{ currentPlan.endDate }}</t-descriptions-item>
            <t-descriptions-item label="优先级">
              <t-tag v-if="currentPlan.priority === 'high'" theme="danger">高</t-tag>
              <t-tag v-else-if="currentPlan.priority === 'medium'" theme="warning">中</t-tag>
              <t-tag v-else theme="default">低</t-tag>
            </t-descriptions-item>
            <t-descriptions-item label="状态">
              <t-tag v-if="currentPlan.status === 'draft'" theme="default">草稿</t-tag>
              <t-tag v-else-if="currentPlan.status === 'pending'" theme="warning">待执行</t-tag>
              <t-tag v-else-if="currentPlan.status === 'inProgress'" theme="primary">执行中</t-tag>
              <t-tag v-else-if="currentPlan.status === 'completed'" theme="success">已完成</t-tag>
              <t-tag v-else theme="default">已取消</t-tag>
            </t-descriptions-item>
            <t-descriptions-item label="生产车间">{{ currentPlan.workshopName }}</t-descriptions-item>
            <t-descriptions-item label="备注" :span="2">{{ currentPlan.remark || '-' }}</t-descriptions-item>
          </t-descriptions>
        </t-tab-panel>
        <t-tab-panel value="material" label="物料清单">
          <t-table :data="currentPlan.materials" :columns="viewMaterialColumns" stripe />
        </t-tab-panel>
        <t-tab-panel value="process" label="工序流程">
          <t-table :data="currentPlan.processes" :columns="viewProcessColumns" stripe />
        </t-tab-panel>
        <t-tab-panel value="log" label="操作日志">
          <t-timeline>
            <t-timeline-item
              v-for="log in currentPlan.logs"
              :key="log.id"
              :label="log.time"
            >
              {{ log.operator }} {{ log.action }} - {{ log.remark }}
            </t-timeline-item>
          </t-timeline>
        </t-tab-panel>
      </t-tabs>
      <div class="dialog-footer">
        <t-button theme="default" @click="detailDialogVisible = false">关闭</t-button>
      </div>
    </t-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { MessagePlugin, DialogPlugin } from 'tdesign-vue-next'

// 统计数据
const stats = ref({
  planCount: 86,
  pendingCount: 12,
  completedCount: 58,
  totalQuantity: 12500,
})

// 搜索表单
const searchForm = reactive({
  planNo: '',
  productId: undefined,
  status: undefined,
  planDate: [],
})

// 产品列表
const productList = ref([
  { value: '1', label: '笔记本电脑', spec: 'i7/16G/512G', unit: '台' },
  { value: '2', label: '无线鼠标', spec: '黑色', unit: '个' },
  { value: '3', label: '机械键盘', spec: '青轴', unit: '个' },
  { value: '4', label: '显示器', spec: '27寸/IPS', unit: '台' },
  { value: '5', label: '耳机', spec: '降噪', unit: '个' },
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

// 选中的行
const selectedRowKeys = ref([])

// 新增/编辑计划
const editDialogVisible = ref(false)
const isEdit = ref(false)
const activeEditTab = ref('basic')
const editForm = reactive({
  id: '',
  planNo: '',
  planDate: '',
  productId: undefined,
  planQuantity: 0,
  startDate: '',
  endDate: '',
  priority: 'medium',
  workshopId: undefined,
  remark: '',
  materials: [] as any[],
  processes: [] as any[],
})

// 计划详情
const detailDialogVisible = ref(false)
const activeDetailTab = ref('basic')
const currentPlan = ref<any>({})

// 物料列表列
const materialColumns = [
  { colKey: 'materialCode', title: '物料编号', width: 120 },
  { colKey: 'materialName', title: '物料名称', width: 200 },
  { colKey: 'spec', title: '规格', width: 120 },
  { colKey: 'unit', title: '单位', width: 80 },
  { colKey: 'quantity', title: '需求数量', width: 100, edit: true },
  { colKey: 'action', title: '操作', width: 80 },
]

// 查看物料列
const viewMaterialColumns = [
  { colKey: 'materialCode', title: '物料编号', width: 120 },
  { colKey: 'materialName', title: '物料名称', width: 200 },
  { colKey: 'spec', title: '规格', width: 120 },
  { colKey: 'unit', title: '单位', width: 80 },
  { colKey: 'quantity', title: '需求数量', width: 100 },
  { colKey: 'issuedQuantity', title: '已领数量', width: 100 },
]

// 工序列
const processColumns = [
  { colKey: 'processName', title: '工序名称', width: 150 },
  { colKey: 'sequence', title: '顺序', width: 80, edit: true },
  { colKey: 'workHours', title: '工时(小时)', width: 120, edit: true },
  { colKey: 'action', title: '操作', width: 80 },
]

// 查看工序列
const viewProcessColumns = [
  { colKey: 'processName', title: '工序名称', width: 150 },
  { colKey: 'sequence', title: '顺序', width: 80 },
  { colKey: 'workHours', title: '工时(小时)', width: 120 },
  { colKey: 'status', title: '状态', width: 100 },
  { colKey: 'startTime', title: '开始时间', width: 180 },
]

// 计划列表
const planList = ref([
  {
    id: '1',
    planNo: 'PP202602190001',
    planDate: '2026-02-19',
    productName: '笔记本电脑',
    spec: 'i7/16G/512G',
    unit: '台',
    planQuantity: 200,
    completedQuantity: 150,
    progress: 75,
    startDate: '2026-02-19',
    endDate: '2026-02-25',
    priority: 'high',
    status: 'inProgress',
    workshopName: '车间A',
    createTime: '2026-02-19 10:00:00',
    remark: '',
  },
  {
    id: '2',
    planNo: 'PP202602190002',
    planDate: '2026-02-19',
    productName: '无线鼠标',
    spec: '黑色',
    unit: '个',
    planQuantity: 500,
    completedQuantity: 500,
    progress: 100,
    startDate: '2026-02-19',
    endDate: '2026-02-22',
    priority: 'medium',
    status: 'completed',
    workshopName: '车间B',
    createTime: '2026-02-19 11:00:00',
    remark: '',
  },
  {
    id: '3',
    planNo: 'PP202602180001',
    planDate: '2026-02-18',
    productName: '机械键盘',
    spec: '青轴',
    unit: '个',
    planQuantity: 300,
    completedQuantity: 0,
    progress: 0,
    startDate: '20xx-xx-xx',
    endDate: '2026-02-25',
    priority: 'medium',
    status: 'pending',
    workshopName: '车间C',
    createTime: '2026-02-18 09:00:00',
    remark: '',
  },
  {
    id: '4',
    planNo: 'PP202602170001',
    planDate: '20xx-xx-xx',
    productName: '显示器',
    spec: '27寸/IPS',
    unit: '台',
    planQuantity: 100,
    completedQuantity: 100,
    progress: 100,
    startDate: '2026-02-18',
    endDate: '20xx-xx-xx',
    priority: 'high',
    status: 'completed',
    workshopName: '车间A',
    createTime: '20xx-xx-xx 10:00:00',
    remark: '',
  },
  {
    id: '5',
    planNo: 'PP202602160001',
    planDate: '2026-02-16',
    productName: '耳机',
    spec: '降噪',
    unit: '个',
    planQuantity: 200,
    completedQuantity: 0,
    progress: 0,
    startDate: '2026-02-19',
    endDate: '2026-02-23',
    priority: 'low',
    status: 'pending',
    workshopName: '车间B',
    createTime: '2026-02-16 11:00:00',
    remark: '',
  },
])

// 列定义
const columns = [
  { colKey: 'planNo', title: '计划编号', width: 150 },
  { colKey: 'productName', title: '产品', width: 150 },
  { colKey: 'spec', title: '规格型号', width: 120 },
  { colKey: 'planQuantity', title: '计划数量', width: 100 },
  { colKey: 'completedQuantity', title: '完成数量', width: 100 },
  { colKey: 'progress', title: '完成进度', width: 150 },
  { colKey: 'startDate', title: '开始日期', width: 120 },
  { colKey: 'endDate', title: '完成日期', width: 120 },
  { colKey: 'priority', title: '优先级', width: 80 },
  { colKey: 'status', title: '状态', width: 100 },
  { colKey: 'createTime', title: '创建时间', width: 180 },
  { colKey: 'action', title: '操作', width: 280, fixed: 'right' },
]

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
  searchForm.planNo = ''
  searchForm.productId = undefined
  searchForm.status = undefined
  searchForm.planDate = []
  pagination.current = 1
  loadData()
}

// 选择变化
const handleSelectChange = (value: any) => {
  selectedRowKeys.value = value
}

// 新增计划
const handleAdd = () => {
  isEdit.value = false
  editForm.id = ''
  editForm.planNo = ''
  editForm.planDate = ''
  editForm.productId = undefined
  editForm.planQuantity = 0
  editForm.startDate = ''
  editForm.endDate = ''
  editForm.priority = 'medium'
  editForm.workshopId = undefined
  editForm.remark = ''
  editForm.materials = []
  editForm.processes = []
  activeEditTab.value = 'basic'
  editDialogVisible.value = true
}

// 编辑计划
const handleEdit = (row: any) => {
  isEdit.value = true
  Object.assign(editForm, row)
  editForm.materials = [
    { id: '1', materialCode: 'M001', materialName: '主板', spec: 'Z790', unit: '个', quantity: 200 },
  ]
  editForm.processes = [
    { id: '1', processName: '组装', sequence: 1, workHours: 2 },
  ]
  activeEditTab.value = 'basic'
  editDialogVisible.value = true
}

// 添加物料
const handleAddMaterial = () => {
  editForm.materials.push({
    id: Date.now().toString(),
    materialCode: '',
    materialName: '',
    spec: '',
    unit: '',
    quantity: 0,
  })
}

// 删除物料
const handleRemoveMaterial = (rowIndex: number) => {
  editForm.materials.splice(rowIndex, 1)
}

// 添加工序
const handleAddProcess = () => {
  editForm.processes.push({
    id: Date.now().toString(),
    processName: '',
    sequence: 0,
    workHours: 0,
  })
}

// 删除工序
const handleRemoveProcess = (rowIndex: number) => {
  editForm.processes.splice(rowIndex, 1)
}

// 获取产品规格
const getProductSpec = (productId: string) => {
  const product = productList.value.find((p) => p.value === productId)
  return product?.spec || '-'
}

// 获取产品单位
const getProductUnit = (productId: string) => {
  const product = productList.value.find((p) => p.value === productId)
  return product?.unit || '-'
}

// 获取车间名称
const getWorkshopName = (workshopId: string) => {
  const workshops: Record<string, string> = {
    '1': '车间A',
    '2': '车间B',
    '3': '车间C',
  }
  return workshops[workshopId] || '-'
}

// 获取总工时
const getTotalWorkHours = () => {
  return editForm.processes.reduce((sum, item) => sum + item.workHours, 0)
}

// 提交编辑
const handleEditSubmit = () => {
  editDialogVisible.value = false
  MessagePlugin.success(isEdit.value ? '修改成功' : '新增成功')
  loadData()
}

// 查看详情
const handleView = (row: any) => {
  currentPlan.value = {
    ...row,
    materials: [
      { id: '1', materialCode: 'M001', materialName: '主板', spec: 'Z790', unit: '个', quantity: 200, issuedQuantity: 150 },
    ],
    processes: [
      { id: '1', processName: '组装', sequence: 1, workHours: 2, status: '已完成', startTime: '2026-02-19 10:00:00' },
    ],
    logs: [
      { id: '1', time: '2026-02-19 10:00:00', operator: '张三', action: '创建生产计划', remark: '' },
      { id: '2', time: '2026-02-19 10:30:00', operator: '张三', action: '开始生产', remark: '' },
    ],
  }
  activeDetailTab.value = 'basic'
  detailDialogVisible.value = true
}

// 开始生产
const handleStart = (row: any) => {
  const dialog = DialogPlugin({
    header: '开始生产',
    body: `是否确认开始生产计划 ${row.planNo}？`,
    confirmBtn: {
      content: '确认开始',
      theme: 'primary',
    },
    onConfirm: () => {
      dialog.hide()
      MessagePlugin.success('生产已开始')
      loadData()
    },
  })
}

// 暂停生产
const handlePause = (row: any) => {
  const dialog = DialogPlugin({
    header: '暂停生产',
    body: `是否确认暂停生产计划 ${row.planNo}？`,
    confirmBtn: {
      content: '确认暂停',
      theme: 'warning',
    },
    onConfirm: () => {
      dialog.hide()
      MessagePlugin.success('生产已暂停')
      loadData()
    },
  })
}

// 完成生产
const handleComplete = (row: any) => {
  const dialog = DialogPlugin({
    header: '完成生产',
    body: `是否确认完成生产计划 ${row.planNo}？`,
    confirmBtn: {
      content: '确认完成',
      theme: 'success',
    },
    onConfirm: () => {
      dialog.hide()
      MessagePlugin.success('生产已完成')
      loadData()
    },
  })
}

// 取消计划
const handleCancel = (row: any) => {
  const dialog = DialogPlugin({
    header: '取消计划',
    body: `是否取消生产计划 ${row.planNo}？`,
    confirmBtn: {
      content: '确认取消',
      theme: 'danger',
    },
    onConfirm: () => {
      dialog.hide()
      MessagePlugin.success('计划已取消')
      loadData()
    },
  })
}

// 批量开始
const handleBatchStart = () => {
  if (selectedRowKeys.value.length === 0) {
    MessagePlugin.warning('请先选择要开始的生产计划')
    return
  }
  const dialog = DialogPlugin({
    header: '批量开始',
    body: `是否确认批量开始 ${selectedRowKeys.value.length} 个生产计划？`,
    confirmBtn: {
      content: '确认开始',
      theme: 'primary',
    },
    onConfirm: () => {
      dialog.hide()
      MessagePlugin.success('批量开始成功')
      selectedRowKeys.value = []
      loadData()
    },
  })
}

// 导出报表
const handleExport = () => {
  MessagePlugin.success('报表导出成功')
}
</script>

<style scoped lang="less">
.production-plan-container {
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

  .detail-header {
    margin-bottom: 12px;
  }

  .dialog-footer {
    margin-top: 20px;
    text-align: center;
  }
}
</style>
