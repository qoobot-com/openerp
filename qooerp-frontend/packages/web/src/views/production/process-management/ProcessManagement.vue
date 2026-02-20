<template>
  <div class="process-management">
    <!-- 统计卡片 -->
    <t-row :gutter="[16, 16]" class="statistics-cards">
      <t-col :span="3">
        <t-card theme="primary1" :header-bordered="false">
          <div class="statistic-content">
            <div class="statistic-value">{{ statistics.total }}</div>
            <div class="statistic-label">工艺总数</div>
          </div>
        </t-card>
      </t-col>
      <t-col :span="3">
        <t-card theme="success" :header-bordered="false">
          <div class="statistic-content">
            <div class="statistic-value">{{ statistics.using }}</div>
            <div class="statistic-label">使用中</div>
          </div>
        </t-card>
      </t-col>
      <t-col :span="3">
        <t-card theme="warning" :header-bordered="false">
          <div class="statistic-content">
            <div class="statistic-value">{{ statistics.draft }}</div>
            <div class="statistic-label">草稿</div>
          </div>
        </t-card>
      </t-col>
      <t-col :span="3">
        <t-card theme="info" :header-bordered="false">
          <div class="statistic-content">
            <div class="statistic-value">{{ statistics.revision }}</div>
            <div class="statistic-label">修订中</div>
          </div>
        </t-card>
      </t-col>
    </t-row>

    <!-- 搜索卡片 -->
    <t-card title="工艺查询" class="search-card">
      <t-form :data="searchForm" layout="inline" @submit="handleSearch">
        <t-form-item label="工艺编号" name="processCode">
          <t-input v-model="searchForm.processCode" placeholder="请输入工艺编号" clearable />
        </t-form-item>
        <t-form-item label="工艺名称" name="processName">
          <t-input v-model="searchForm.processName" placeholder="请输入工艺名称" clearable />
        </t-form-item>
        <t-form-item label="产品类型" name="productType">
          <t-select v-model="searchForm.productType" placeholder="请选择产品类型" clearable>
            <t-option value="finished" label="成品" />
            <t-option value="semi-finished" label="半成品" />
            <t-option value="raw" label="原材料" />
          </t-select>
        </t-form-item>
        <t-form-item label="工艺状态" name="status">
          <t-select v-model="searchForm.status" placeholder="请选择工艺状态" clearable>
            <t-option value="draft" label="草稿" />
            <t-option value="using" label="使用中" />
            <t-option value="revision" label="修订中" />
            <t-option value="archived" label="已归档" />
          </t-select>
        </t-form-item>
        <t-form-item>
          <t-space>
            <t-button theme="primary" type="submit">查询</t-button>
            <t-button theme="default" @click="handleReset">重置</t-button>
          </t-space>
        </t-form-item>
      </t-form>
    </t-card>

    <!-- 工艺列表 -->
    <t-card title="工艺列表" class="table-card">
      <template #actions>
        <t-space>
          <t-button theme="primary" @click="handleAdd">
            <template #icon><add-icon /></template>
            新增工艺
          </t-button>
          <t-button theme="success" @click="handleBatchEnable">
            <template #icon><check-icon /></template>
            批量启用
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
          <t-tag v-if="row.status === 'draft'" theme="default" variant="light">草稿</t-tag>
          <t-tag v-else-if="row.status === 'using'" theme="success" variant="light">使用中</t-tag>
          <t-tag v-else-if="row.status === 'revision'" theme="warning" variant="light">修订中</t-tag>
          <t-tag v-else theme="default" variant="light">已归档</t-tag>
        </template>
        <template #productType="{ row }">
          <t-tag v-if="row.productType === 'finished'" theme="primary" variant="light">成品</t-tag>
          <t-tag v-else-if="row.productType === 'semi-finished'" theme="warning" variant="light">半成品</t-tag>
          <t-tag v-else theme="info" variant="light">原材料</t-tag>
        </template>
        <template #action="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleView(row)">查看</t-link>
            <t-link theme="primary" @click="handleEdit(row)">编辑</t-link>
            <t-link v-if="row.status === 'draft'" theme="success" @click="handleEnable(row)">启用</t-link>
            <t-link v-if="row.status === 'using'" theme="warning" @click="handleRevision(row)">修订</t-link>
            <t-link theme="danger" @click="handleDelete(row)">删除</t-link>
          </t-space>
        </template>
      </t-table>
    </t-card>

    <!-- 新增/编辑工艺弹窗 -->
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
            <t-form-item label="工艺编号" name="processCode">
              <t-input v-model="formData.processCode" placeholder="请输入工艺编号" />
            </t-form-item>
            <t-form-item label="工艺名称" name="processName">
              <t-input v-model="formData.processName" placeholder="请输入工艺名称" />
            </t-form-item>
            <t-form-item label="产品类型" name="productType">
              <t-select v-model="formData.productType" placeholder="请选择产品类型">
                <t-option value="finished" label="成品" />
                <t-option value="semi-finished" label="半成品" />
                <t-option value="raw" label="原材料" />
              </t-select>
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
            <t-form-item label="版本号" name="version">
              <t-input v-model="formData.version" placeholder="请输入版本号" />
            </t-form-item>
            <t-form-item label="工艺状态" name="status">
              <t-select v-model="formData.status" placeholder="请选择工艺状态">
                <t-option value="draft" label="草稿" />
                <t-option value="using" label="使用中" />
                <t-option value="revision" label="修订中" />
                <t-option value="archived" label="已归档" />
              </t-select>
            </t-form-item>
            <t-form-item label="工艺描述" name="description">
              <t-textarea v-model="formData.description" placeholder="请输入工艺描述" :maxlength="1000" />
            </t-form-item>
          </t-form>
        </t-tab-panel>
        <t-tab-panel value="procedures" label="工艺流程">
          <div class="procedures-section">
            <t-button theme="primary" size="small" @click="handleAddProcedure">
              <template #icon><add-icon /></template>
              添加工序
            </t-button>
            <t-table
              :data="formData.procedures"
              :columns="procedureColumns"
              :pagination="false"
              row-key="id"
            >
              <template #action="{ row, rowIndex }">
                <t-space>
                  <t-link theme="primary" @click="handleMoveProcedure(rowIndex, -1)">上移</t-link>
                  <t-link theme="primary" @click="handleMoveProcedure(rowIndex, 1)">下移</t-link>
                  <t-link theme="danger" @click="handleDeleteProcedure(rowIndex)">删除</t-link>
                </t-space>
              </template>
            </t-table>
          </div>
        </t-tab-panel>
        <t-tab-panel value="materials" label="物料清单">
          <div class="materials-section">
            <t-button theme="primary" size="small" @click="handleAddMaterial">
              <template #icon><add-icon /></template>
              添加物料
            </t-button>
            <t-table
              :data="formData.materials"
              :columns="materialColumns"
              :pagination="false"
              row-key="id"
            >
              <template #action="{ row, rowIndex }">
                <t-link theme="danger" @click="handleDeleteMaterial(rowIndex)">删除</t-link>
              </template>
            </t-table>
          </div>
        </t-tab-panel>
        <t-tab-panel value="summary" label="汇总信息">
          <t-descriptions :column="2" bordered>
            <t-descriptions-item label="工艺编号">{{ formData.processCode }}</t-descriptions-item>
            <t-descriptions-item label="工艺名称">{{ formData.processName }}</t-descriptions-item>
            <t-descriptions-item label="产品类型">
              <t-tag v-if="formData.productType === 'finished'" theme="primary" variant="light">成品</t-tag>
              <t-tag v-else-if="formData.productType === 'semi-finished'" theme="warning" variant="light">半成品</t-tag>
              <t-tag v-else theme="info" variant="light">原材料</t-tag>
            </t-descriptions-item>
            <t-descriptions-item label="产品名称">{{ formData.productName }}</t-descriptions-item>
            <t-descriptions-item label="规格型号">{{ formData.specification }}</t-descriptions-item>
            <t-descriptions-item label="版本号">{{ formData.version }}</t-descriptions-item>
            <t-descriptions-item label="工序数量">{{ formData.procedures.length }}个</t-descriptions-item>
            <t-descriptions-item label="物料种类">{{ formData.materials.length }}种</t-descriptions-item>
            <t-descriptions-item label="预计工时">{{ formData.totalWorkHours }}小时</t-descriptions-item>
            <t-descriptions-item label="工艺状态">
              <t-tag v-if="formData.status === 'draft'" theme="default" variant="light">草稿</t-tag>
              <t-tag v-else-if="formData.status === 'using'" theme="success" variant="light">使用中</t-tag>
              <t-tag v-else-if="formData.status === 'revision'" theme="warning" variant="light">修订中</t-tag>
              <t-tag v-else theme="default" variant="light">已归档</t-tag>
            </t-descriptions-item>
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

    <!-- 工艺详情弹窗 -->
    <t-dialog
      v-model:visible="detailVisible"
      header="工艺详情"
      :width="1000"
      :footer="false"
    >
      <t-tabs v-model="detailTab" theme="card">
        <t-tab-panel value="basic" label="基本信息">
          <t-descriptions :column="2" bordered>
            <t-descriptions-item label="工艺编号">{{ detailData.processCode }}</t-descriptions-item>
            <t-descriptions-item label="工艺名称">{{ detailData.processName }}</t-descriptions-item>
            <t-descriptions-item label="产品类型">
              <t-tag v-if="detailData.productType === 'finished'" theme="primary" variant="light">成品</t-tag>
              <t-tag v-else-if="detailData.productType === 'semi-finished'" theme="warning" variant="light">半成品</t-tag>
              <t-tag v-else theme="info" variant="light">原材料</t-tag>
            </t-descriptions-item>
            <t-descriptions-item label="产品名称">{{ detailData.productName }}</t-descriptions-item>
            <t-descriptions-item label="规格型号">{{ detailData.specification }}</t-descriptions-item>
            <t-descriptions-item label="版本号">{{ detailData.version }}</t-descriptions-item>
            <t-descriptions-item label="工序数量">{{ detailData.procedures?.length || 0 }}个</t-descriptions-item>
            <t-descriptions-item label="物料种类">{{ detailData.materials?.length || 0 }}种</t-descriptions-item>
            <t-descriptions-item label="预计工时">{{ detailData.totalWorkHours }}小时</t-descriptions-item>
            <t-descriptions-item label="工艺状态">
              <t-tag v-if="detailData.status === 'draft'" theme="default" variant="light">草稿</t-tag>
              <t-tag v-else-if="detailData.status === 'using'" theme="success" variant="light">使用中</t-tag>
              <t-tag v-else-if="detailData.status === 'revision'" theme="warning" variant="light">修订中</t-tag>
              <t-tag v-else theme="default" variant="light">已归档</t-tag>
            </t-descriptions-item>
            <t-descriptions-item label="工艺描述" :span="2">{{ detailData.description }}</t-descriptions-item>
          </t-descriptions>
        </t-tab-panel>
        <t-tab-panel value="procedures" label="工艺流程">
          <t-steps :current="activeStep" theme="simple">
            <t-step-item
              v-for="(proc, index) in detailData.procedures || []"
              :key="index"
              :title="proc.procedureName"
              :content="`工时: ${proc.workHours}小时`"
            />
          </t-steps>
          <t-divider />
          <t-table
            :data="detailData.procedures || []"
            :columns="procedureColumns"
            :pagination="false"
            row-key="id"
          />
        </t-tab-panel>
        <t-tab-panel value="materials" label="物料清单">
          <t-table
            :data="detailData.materials || []"
            :columns="materialColumns"
            :pagination="false"
            row-key="id"
          />
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
  total: 80,
  using: 50,
  draft: 20,
  revision: 10
})

// 搜索表单
const searchForm = reactive({
  processCode: '',
  processName: '',
  productType: '',
  status: ''
})

// 表格数据
const loading = ref(false)
const tableData = ref([
  {
    id: 1,
    processCode: 'PRC-001',
    processName: '成品A生产工艺',
    productType: 'finished',
    productCode: 'P-001',
    productName: '成品A',
    specification: '规格A',
    version: 'V1.0',
    status: 'using',
    totalWorkHours: 8,
    description: '成品A的标准生产工艺流程',
    procedures: [
      { id: 1, procedureName: '备料', sequence: 1, workHours: 1, equipment: '设备A', remarks: '' },
      { id: 2, procedureName: '加工', sequence: 2, workHours: 4, equipment: '设备B', remarks: '' },
      { id: 3, procedureName: '装配', sequence: 3, workHours: 2, equipment: '设备C', remarks: '' },
      { id: 4, procedureName: '检验', sequence: 4, workHours: 1, equipment: '检测设备', remarks: '' }
    ],
    materials: [
      { id: 1, materialCode: 'M-001', materialName: '原材料A', specification: '规格A', quantity: 10, unit: '件' },
      { id: 2, materialCode: 'M-002', materialName: '原材料B', specification: '规格B', quantity: 5, unit: '件' }
    ]
  },
  {
    id: 2,
    processCode: 'PRC-002',
    processName: '半成品B生产工艺',
    productType: 'semi-finished',
    productCode: 'P-002',
    productName: '半成品B',
    specification: '规格B',
    version: 'V1.2',
    status: 'revision',
    totalWorkHours: 6,
    description: '半成品B的生产工艺流程',
    procedures: [
      { id: 1, procedureName: '备料', sequence: 1, workHours: 1, equipment: '设备A', remarks: '' },
      { id: 2, procedureName: '加工', sequence: 2, workHours: 3, equipment: '设备B', remarks: '' },
      { id: 3, procedureName: '检验', sequence: 3, workHours: 2, equipment: '检测设备', remarks: '' }
    ],
    materials: [
      { id: 1, materialCode: 'M-003', materialName: '原材料C', specification: '规格C', quantity: 8, unit: '件' }
    ]
  },
  {
    id: 3,
    processCode: 'PRC-003',
    processName: '成品C生产工艺',
    productType: 'finished',
    productCode: 'P-003',
    productName: '成品C',
    specification: '规格C',
    version: 'V1.0',
    status: 'draft',
    totalWorkHours: 10,
    description: '成品C的标准生产工艺流程',
    procedures: [],
    materials: []
  }
])

// 表格列定义
const columns = [
  { colKey: 'processCode', title: '工艺编号', width: 120 },
  { colKey: 'processName', title: '工艺名称', width: 150 },
  { colKey: 'productType', title: '产品类型', width: 100, cell: 'productType' },
  { colKey: 'productName', title: '产品名称', width: 150 },
  { colKey: 'specification', title: '规格型号', width: 120 },
  { colKey: 'version', title: '版本号', width: 100 },
  { colKey: 'totalWorkHours', title: '预计工时(h)', width: 120 },
  { colKey: 'status', title: '工艺状态', width: 100, cell: 'status' },
  { colKey: 'action', title: '操作', width: 200, fixed: 'right', cell: 'action' }
]

const procedureColumns = [
  { colKey: 'sequence', title: '顺序', width: 80 },
  { colKey: 'procedureName', title: '工序名称', width: 150 },
  { colKey: 'workHours', title: '工时(小时)', width: 120 },
  { colKey: 'equipment', title: '使用设备', width: 150 },
  { colKey: 'remarks', title: '备注', width: 200 },
  { colKey: 'action', title: '操作', width: 180, cell: 'action' }
]

const materialColumns = [
  { colKey: 'materialCode', title: '物料编号', width: 120 },
  { colKey: 'materialName', title: '物料名称', width: 150 },
  { colKey: 'specification', title: '规格', width: 120 },
  { colKey: 'quantity', title: '用量', width: 100 },
  { colKey: 'unit', title: '单位', width: 80 },
  { colKey: 'action', title: '操作', width: 80, cell: 'action' }
]

// 分页
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 80
})

const rowKey = 'id'
const selectedRowKeys = ref<number[]>([])
const activeStep = ref(0)

// 新增/编辑弹窗
const dialogVisible = ref(false)
const dialogTitle = computed(() => (formData.id ? '编辑工艺' : '新增工艺'))
const activeTab = ref('basic')
const formRef = ref()
const formData = reactive({
  id: undefined,
  processCode: '',
  processName: '',
  productType: '',
  productCode: '',
  productName: '',
  specification: '',
  version: 'V1.0',
  status: 'draft',
  description: '',
  totalWorkHours: 0,
  procedures: [],
  materials: []
})

const rules = {
  processCode: [{ required: true, message: '请输入工艺编号' }],
  processName: [{ required: true, message: '请输入工艺名称' }],
  productType: [{ required: true, message: '请选择产品类型' }],
  productCode: [{ required: true, message: '请输入产品编号' }],
  productName: [{ required: true, message: '请输入产品名称' }],
  specification: [{ required: true, message: '请输入规格型号' }],
  version: [{ required: true, message: '请输入版本号' }]
}

// 工艺详情弹窗
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
    processCode: '',
    processName: '',
    productType: '',
    status: ''
  })
  MessagePlugin.success('已重置')
}

// 新增工艺
const handleAdd = () => {
  Object.assign(formData, {
    id: undefined,
    processCode: '',
    processName: '',
    productType: '',
    productCode: '',
    productName: '',
    specification: '',
    version: 'V1.0',
    status: 'draft',
    description: '',
    totalWorkHours: 0,
    procedures: [],
    materials: []
  })
  activeTab.value = 'basic'
  dialogVisible.value = true
}

// 编辑工艺
const handleEdit = (row: any) => {
  Object.assign(formData, { ...row })
  activeTab.value = 'basic'
  dialogVisible.value = true
}

// 启用工艺
const handleEnable = (row: any) => {
  const dialog = DialogPlugin({
    header: '确认启用',
    body: `确认要启用工艺"${row.processName}"吗？`,
    confirmBtn: '确定',
    cancelBtn: '取消',
    onConfirm: () => {
      dialog.hide()
      MessagePlugin.success('工艺已启用')
    }
  })
}

// 修订工艺
const handleRevision = (row: any) => {
  MessagePlugin.success('工艺已进入修订状态')
}

// 删除工艺
const handleDelete = (row: any) => {
  const dialog = DialogPlugin({
    header: '确认删除',
    body: `确认要删除工艺"${row.processName}"吗？`,
    confirmBtn: '确定',
    cancelBtn: '取消',
    onConfirm: () => {
      dialog.hide()
      MessagePlugin.success('删除成功')
    }
  })
}

// 批量启用
const handleBatchEnable = () => {
  if (selectedRowKeys.value.length === 0) {
    MessagePlugin.warning('请先选择工艺')
    return
  }
  MessagePlugin.success(`已批量启用${selectedRowKeys.value.length}个工艺`)
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
      { id: 1, time: '2026-02-19 10:00', content: '创建工艺', operator: '管理员' },
      { id: 2, time: '2026-02-18 14:30', content: '更新工艺流程', operator: '技术员' }
    ]
  }
  detailTab.value = 'basic'
  detailVisible.value = true
}

// 添加工序
const handleAddProcedure = () => {
  formData.procedures.push({
    id: Date.now(),
    procedureName: '',
    sequence: formData.procedures.length + 1,
    workHours: 0,
    equipment: '',
    remarks: ''
  })
}

// 删除工序
const handleDeleteProcedure = (index: number) => {
  formData.procedures.splice(index, 1)
}

// 移动工序
const handleMoveProcedure = (index: number, direction: number) => {
  const newIndex = index + direction
  if (newIndex < 0 || newIndex >= formData.procedures.length) return
  const temp = formData.procedures[index]
  formData.procedures[index] = formData.procedures[newIndex]
  formData.procedures[newIndex] = temp
  // 更新顺序
  formData.procedures.forEach((proc, i) => {
    proc.sequence = i + 1
  })
}

// 添加物料
const handleAddMaterial = () => {
  formData.materials.push({
    id: Date.now(),
    materialCode: '',
    materialName: '',
    specification: '',
    quantity: 0,
    unit: ''
  })
}

// 删除物料
const handleDeleteMaterial = (index: number) => {
  formData.materials.splice(index, 1)
}
</script>

<style scoped lang="less">
.process-management {
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

  .procedures-section,
  .materials-section {
    .t-button {
      margin-bottom: 16px;
    }
  }

  .log-operator {
    font-size: 12px;
    color: rgba(0, 0, 0, 0.6);
    margin-top: 4px;
  }
}
</style>
