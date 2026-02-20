<template>
  <div class="quality-inspection">
    <!-- 统计卡片 -->
    <t-row :gutter="[16, 16]" class="statistics-cards">
      <t-col :span="3">
        <t-card theme="primary1" :header-bordered="false">
          <div class="statistic-content">
            <div class="statistic-value">{{ statistics.total }}</div>
            <div class="statistic-label">检验总数</div>
          </div>
        </t-card>
      </t-col>
      <t-col :span="3">
        <t-card theme="success" :header-bordered="false">
          <div class="statistic-content">
            <div class="statistic-value">{{ statistics.qualified }}</div>
            <div class="statistic-label">合格数</div>
          </div>
        </t-card>
      </t-col>
      <t-col :span="3">
        <t-card theme="warning" :header-bordered="false">
          <div class="statistic-content">
            <div class="statistic-value">{{ statistics.unqualified }}</div>
            <div class="statistic-label">不合格数</div>
          </div>
        </t-card>
      </t-col>
      <t-col :span="3">
        <t-card theme="info" :header-bordered="false">
          <div class="statistic-content">
            <div class="statistic-value">{{ statistics.qualifiedRate }}%</div>
            <div class="statistic-label">合格率</div>
          </div>
        </t-card>
      </t-col>
    </t-row>

    <!-- 搜索卡片 -->
    <t-card title="检验查询" class="search-card">
      <t-form :data="searchForm" layout="inline" @submit="handleSearch">
        <t-form-item label="检验单号" name="inspectionNo">
          <t-input v-model="searchForm.inspectionNo" placeholder="请输入检验单号" clearable />
        </t-form-item>
        <t-form-item label="产品名称" name="productName">
          <t-input v-model="searchForm.productName" placeholder="请输入产品名称" clearable />
        </t-form-item>
        <t-form-item label="检验类型" name="inspectionType">
          <t-select v-model="searchForm.inspectionType" placeholder="请选择检验类型" clearable>
            <t-option value="incoming" label="进料检验" />
            <t-option value="process" label="过程检验" />
            <t-option value="final" label="成品检验" />
            <t-option value="outgoing" label="出货检验" />
          </t-select>
        </t-form-item>
        <t-form-item label="检验状态" name="status">
          <t-select v-model="searchForm.status" placeholder="请选择检验状态" clearable>
            <t-option value="pending" label="待检验" />
            <t-option value="inspecting" label="检验中" />
            <t-option value="qualified" label="合格" />
            <t-option value="unqualified" label="不合格" />
          </t-select>
        </t-form-item>
        <t-form-item label="检验日期" name="inspectionDate">
          <t-date-range-picker v-model="searchForm.inspectionDate" placeholder="请选择检验日期" />
        </t-form-item>
        <t-form-item>
          <t-space>
            <t-button theme="primary" type="submit">查询</t-button>
            <t-button theme="default" @click="handleReset">重置</t-button>
          </t-space>
        </t-form-item>
      </t-form>
    </t-card>

    <!-- 检验列表 -->
    <t-card title="检验列表" class="table-card">
      <template #actions>
        <t-space>
          <t-button theme="primary" @click="handleAdd">
            <template #icon><add-icon /></template>
            新增检验
          </t-button>
          <t-button theme="success" @click="handleBatchQualified">
            <template #icon><check-icon /></template>
            批量合格
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
          <t-tag v-if="row.status === 'pending'" theme="default" variant="light">待检验</t-tag>
          <t-tag v-else-if="row.status === 'inspecting'" theme="warning" variant="light">检验中</t-tag>
          <t-tag v-else-if="row.status === 'qualified'" theme="success" variant="light">合格</t-tag>
          <t-tag v-else theme="error" variant="light">不合格</t-tag>
        </template>
        <template #inspectionType="{ row }">
          <t-tag v-if="row.inspectionType === 'incoming'" theme="primary" variant="light">进料检验</t-tag>
          <t-tag v-else-if="row.inspectionType === 'process'" theme="success" variant="light">过程检验</t-tag>
          <t-tag v-else-if="row.inspectionType === 'final'" theme="warning" variant="light">成品检验</t-tag>
          <t-tag v-else theme="info" variant="light">出货检验</t-tag>
        </template>
        <template #qualifiedRate="{ row }">
          <t-progress
            :percentage="row.qualifiedRate"
            :theme="row.qualifiedRate >= 90 ? 'success' : row.qualifiedRate >= 80 ? 'warning' : 'danger'"
            size="small"
            style="width: 100px"
          />
        </template>
        <template #action="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleView(row)">查看</t-link>
            <t-link v-if="row.status === 'pending'" theme="primary" @click="handleStart(row)">开始检验</t-link>
            <t-link v-if="row.status === 'inspecting'" theme="success" @click="handleComplete(row)">完成检验</t-link>
            <t-link theme="danger" @click="handleDelete(row)">删除</t-link>
          </t-space>
        </template>
      </t-table>
    </t-card>

    <!-- 新增/编辑检验弹窗 -->
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
            <t-form-item label="检验单号" name="inspectionNo">
              <t-input v-model="formData.inspectionNo" placeholder="请输入检验单号" />
            </t-form-item>
            <t-form-item label="检验类型" name="inspectionType">
              <t-select v-model="formData.inspectionType" placeholder="请选择检验类型">
                <t-option value="incoming" label="进料检验" />
                <t-option value="process" label="过程检验" />
                <t-option value="final" label="成品检验" />
                <t-option value="outgoing" label="出货检验" />
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
            <t-form-item label="检验数量" name="inspectionQty">
              <t-input-number v-model="formData.inspectionQty" :min="1" placeholder="请输入检验数量" />
            </t-form-item>
            <t-form-item label="检验标准" name="inspectionStandard">
              <t-input v-model="formData.inspectionStandard" placeholder="请输入检验标准" />
            </t-form-item>
            <t-form-item label="检验员" name="inspector">
              <t-select v-model="formData.inspector" placeholder="请选择检验员" filterable>
                <t-option value="1" label="张三" />
                <t-option value="2" label="李四" />
                <t-option value="3" label="王五" />
              </t-select>
            </t-form-item>
            <t-form-item label="检验日期" name="inspectionDate">
              <t-date-picker v-model="formData.inspectionDate" placeholder="请选择检验日期" />
            </t-form-item>
            <t-form-item label="备注" name="remark">
              <t-textarea v-model="formData.remark" placeholder="请输入备注" :maxlength="500" />
            </t-form-item>
          </t-form>
        </t-tab-panel>
        <t-tab-panel value="items" label="检验项目">
          <div class="inspection-items">
            <t-button theme="primary" size="small" @click="handleAddItem">
              <template #icon><add-icon /></template>
              添加检验项目
            </t-button>
            <t-table
              :data="formData.inspectionItems"
              :columns="itemColumns"
              :pagination="false"
              row-key="id"
            >
              <template #result="{ row }">
                <t-select v-model="row.result" placeholder="请选择结果" size="small" style="width: 100px">
                  <t-option value="qualified" label="合格" />
                  <t-option value="unqualified" label="不合格" />
                </t-select>
              </template>
              <template #action="{ row, rowIndex }">
                <t-link theme="danger" @click="handleDeleteItem(rowIndex)">删除</t-link>
              </template>
            </t-table>
          </div>
        </t-tab-panel>
        <t-tab-panel value="summary" label="汇总信息">
          <t-descriptions :column="2" bordered>
            <t-descriptions-item label="检验单号">{{ formData.inspectionNo }}</t-descriptions-item>
            <t-descriptions-item label="检验类型">
              <t-tag v-if="formData.inspectionType === 'incoming'" theme="primary" variant="light">进料检验</t-tag>
              <t-tag v-else-if="formData.inspectionType === 'process'" theme="success" variant="light">过程检验</t-tag>
              <t-tag v-else-if="formData.inspectionType === 'final'" theme="warning" variant="light">成品检验</t-tag>
              <t-tag v-else theme="info" variant="light">出货检验</t-tag>
            </t-descriptions-item>
            <t-descriptions-item label="产品名称">{{ formData.productName }}</t-descriptions-item>
            <t-descriptions-item label="规格型号">{{ formData.specification }}</t-descriptions-item>
            <t-descriptions-item label="检验数量">{{ formData.inspectionQty }}</t-descriptions-item>
            <t-descriptions-item label="合格数量">{{ formData.qualifiedQty }}</t-descriptions-item>
            <t-descriptions-item label="不合格数量">{{ formData.unqualifiedQty }}</t-descriptions-item>
            <t-descriptions-item label="合格率">{{ formData.qualifiedRate }}%</t-descriptions-item>
            <t-descriptions-item label="检验员">{{ formData.inspector }}</t-descriptions-item>
            <t-descriptions-item label="检验日期">{{ formData.inspectionDate }}</t-descriptions-item>
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

    <!-- 检验详情弹窗 -->
    <t-dialog
      v-model:visible="detailVisible"
      header="检验详情"
      :width="1000"
      :footer="false"
    >
      <t-tabs v-model="detailTab" theme="card">
        <t-tab-panel value="basic" label="基本信息">
          <t-descriptions :column="2" bordered>
            <t-descriptions-item label="检验单号">{{ detailData.inspectionNo }}</t-descriptions-item>
            <t-descriptions-item label="检验类型">
              <t-tag v-if="detailData.inspectionType === 'incoming'" theme="primary" variant="light">进料检验</t-tag>
              <t-tag v-else-if="detailData.inspectionType === 'process'" theme="success" variant="light">过程检验</t-tag>
              <t-tag v-else-if="detailData.inspectionType === 'final'" theme="warning" variant="light">成品检验</t-tag>
              <t-tag v-else theme="info" variant="light">出货检验</t-tag>
            </t-descriptions-item>
            <t-descriptions-item label="产品名称">{{ detailData.productName }}</t-descriptions-item>
            <t-descriptions-item label="规格型号">{{ detailData.specification }}</t-descriptions-item>
            <t-descriptions-item label="检验数量">{{ detailData.inspectionQty }}</t-descriptions-item>
            <t-descriptions-item label="合格数量">{{ detailData.qualifiedQty }}</t-descriptions-item>
            <t-descriptions-item label="不合格数量">{{ detailData.unqualifiedQty }}</t-descriptions-item>
            <t-descriptions-item label="合格率">{{ detailData.qualifiedRate }}%</t-descriptions-item>
            <t-descriptions-item label="检验状态">
              <t-tag v-if="detailData.status === 'pending'" theme="default" variant="light">待检验</t-tag>
              <t-tag v-else-if="detailData.status === 'inspecting'" theme="warning" variant="light">检验中</t-tag>
              <t-tag v-else-if="detailData.status === 'qualified'" theme="success" variant="light">合格</t-tag>
              <t-tag v-else theme="error" variant="light">不合格</t-tag>
            </t-descriptions-item>
            <t-descriptions-item label="检验员">{{ detailData.inspector }}</t-descriptions-item>
            <t-descriptions-item label="检验日期">{{ detailData.inspectionDate }}</t-descriptions-item>
            <t-descriptions-item label="备注" :span="2">{{ detailData.remark }}</t-descriptions-item>
          </t-descriptions>
        </t-tab-panel>
        <t-tab-panel value="items" label="检验项目">
          <t-table
            :data="detailData.inspectionItems || []"
            :columns="itemColumns"
            :pagination="false"
            row-key="id"
          >
            <template #result="{ row }">
              <t-tag v-if="row.result === 'qualified'" theme="success" variant="light">合格</t-tag>
              <t-tag v-else theme="error" variant="light">不合格</t-tag>
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
import { AddIcon, CheckIcon, DownloadIcon } from 'tdesign-icons-vue-next'

// 统计数据
const statistics = ref({
  total: 1250,
  qualified: 1180,
  unqualified: 70,
  qualifiedRate: 94.4
})

// 搜索表单
const searchForm = reactive({
  inspectionNo: '',
  productName: '',
  inspectionType: '',
  status: '',
  inspectionDate: []
})

// 表格数据
const loading = ref(false)
const tableData = ref([
  {
    id: 1,
    inspectionNo: 'QC-20260219-001',
    inspectionType: 'incoming',
    productCode: 'P-001',
    productName: '原材料A',
    specification: '规格A',
    inspectionQty: 100,
    qualifiedQty: 95,
    unqualifiedQty: 5,
    qualifiedRate: 95,
    status: 'qualified',
    inspector: '1',
    inspectionDate: '2026-02-19',
    inspectionStandard: 'ISO-9001',
    inspectionItems: [
      { id: 1, itemName: '外观检查', standard: '无明显缺陷', result: 'qualified', remark: '' },
      { id: 2, itemName: '尺寸测量', standard: '±0.1mm', result: 'qualified', remark: '' }
    ]
  },
  {
    id: 2,
    inspectionNo: 'QC-20260219-002',
    inspectionType: 'process',
    productCode: 'P-002',
    productName: '半成品B',
    specification: '规格B',
    inspectionQty: 50,
    qualifiedQty: 45,
    unqualifiedQty: 5,
    qualifiedRate: 90,
    status: 'inspecting',
    inspector: '2',
    inspectionDate: '2026-02-19',
    inspectionStandard: 'GB/T-1234',
    inspectionItems: [
      { id: 1, itemName: '工艺检查', standard: '符合工艺要求', result: 'qualified', remark: '' },
      { id: 2, itemName: '性能测试', standard: '≥95%', result: 'unqualified', remark: '性能略低于标准' }
    ]
  },
  {
    id: 3,
    inspectionNo: 'QC-20260219-003',
    inspectionType: 'final',
    productCode: 'P-003',
    productName: '成品C',
    specification: '规格C',
    inspectionQty: 80,
    qualifiedQty: 0,
    unqualifiedQty: 0,
    qualifiedRate: 0,
    status: 'pending',
    inspector: '3',
    inspectionDate: '2026-02-19',
    inspectionStandard: 'ASTM-D123',
    inspectionItems: []
  }
])

// 表格列定义
const columns = [
  { colKey: 'inspectionNo', title: '检验单号', width: 150 },
  { colKey: 'inspectionType', title: '检验类型', width: 100, cell: 'inspectionType' },
  { colKey: 'productName', title: '产品名称', width: 150 },
  { colKey: 'specification', title: '规格型号', width: 120 },
  { colKey: 'inspectionQty', title: '检验数量', width: 100 },
  { colKey: 'qualifiedQty', title: '合格数量', width: 100 },
  { colKey: 'unqualifiedQty', title: '不合格数量', width: 120 },
  { colKey: 'qualifiedRate', title: '合格率', width: 120, cell: 'qualifiedRate' },
  { colKey: 'status', title: '检验状态', width: 100, cell: 'status' },
  { colKey: 'inspector', title: '检验员', width: 100 },
  { colKey: 'inspectionDate', title: '检验日期', width: 120 },
  { colKey: 'action', title: '操作', width: 200, fixed: 'right', cell: 'action' }
]

const itemColumns = [
  { colKey: 'itemName', title: '检验项目', width: 150 },
  { colKey: 'standard', title: '检验标准', width: 200 },
  { colKey: 'result', title: '检验结果', width: 120, cell: 'result' },
  { colKey: 'remark', title: '备注', width: 200 },
  { colKey: 'action', title: '操作', width: 80, cell: 'action' }
]

// 分页
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 1250
})

const rowKey = 'id'
const selectedRowKeys = ref<number[]>([])

// 新增/编辑弹窗
const dialogVisible = ref(false)
const dialogTitle = computed(() => (formData.id ? '编辑检验' : '新增检验'))
const activeTab = ref('basic')
const formRef = ref()
const formData = reactive({
  id: undefined,
  inspectionNo: '',
  inspectionType: '',
  productCode: '',
  productName: '',
  specification: '',
  inspectionQty: 1,
  inspectionStandard: '',
  inspector: '',
  inspectionDate: '',
  remark: '',
  qualifiedQty: 0,
  unqualifiedQty: 0,
  qualifiedRate: 0,
  inspectionItems: []
})

const rules = {
  inspectionNo: [{ required: true, message: '请输入检验单号' }],
  inspectionType: [{ required: true, message: '请选择检验类型' }],
  productCode: [{ required: true, message: '请输入产品编号' }],
  productName: [{ required: true, message: '请输入产品名称' }],
  inspectionQty: [{ required: true, message: '请输入检验数量' }],
  inspectionStandard: [{ required: true, message: '请输入检验标准' }],
  inspector: [{ required: true, message: '请选择检验员' }],
  inspectionDate: [{ required: true, message: '请选择检验日期' }]
}

// 检验详情弹窗
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
    inspectionNo: '',
    productName: '',
    inspectionType: '',
    status: '',
    inspectionDate: []
  })
  MessagePlugin.success('已重置')
}

// 新增检验
const handleAdd = () => {
  Object.assign(formData, {
    id: undefined,
    inspectionNo: '',
    inspectionType: '',
    productCode: '',
    productName: '',
    specification: '',
    inspectionQty: 1,
    inspectionStandard: '',
    inspector: '',
    inspectionDate: '',
    remark: '',
    qualifiedQty: 0,
    unqualifiedQty: 0,
    qualifiedRate: 0,
    inspectionItems: []
  })
  activeTab.value = 'basic'
  dialogVisible.value = true
}

// 开始检验
const handleStart = (row: any) => {
  MessagePlugin.success('检验已开始')
}

// 完成检验
const handleComplete = (row: any) => {
  const dialog = DialogPlugin({
    header: '完成检验',
    body: '确认完成此检验？',
    confirmBtn: '确定',
    cancelBtn: '取消',
    onConfirm: () => {
      dialog.hide()
      MessagePlugin.success('检验已完成')
    }
  })
}

// 删除检验
const handleDelete = (row: any) => {
  const dialog = DialogPlugin({
    header: '确认删除',
    body: `确认要删除检验单"${row.inspectionNo}"吗？`,
    confirmBtn: '确定',
    cancelBtn: '取消',
    onConfirm: () => {
      dialog.hide()
      MessagePlugin.success('删除成功')
    }
  })
}

// 批量合格
const handleBatchQualified = () => {
  if (selectedRowKeys.value.length === 0) {
    MessagePlugin.warning('请先选择检验单')
    return
  }
  MessagePlugin.success(`已批量确认${selectedRowKeys.value.length}个检验单合格`)
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
      { id: 1, time: '2026-02-19 10:00', content: '创建检验单', operator: '管理员' },
      { id: 2, time: '2026-02-19 14:30', content: '完成检验', operator: '张三' }
    ]
  }
  detailTab.value = 'basic'
  detailVisible.value = true
}

// 添加检验项目
const handleAddItem = () => {
  formData.inspectionItems.push({
    id: Date.now(),
    itemName: '',
    standard: '',
    result: '',
    remark: ''
  })
}

// 删除检验项目
const handleDeleteItem = (index: number) => {
  formData.inspectionItems.splice(index, 1)
}
</script>

<style scoped lang="less">
.quality-inspection {
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

  .inspection-items {
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
