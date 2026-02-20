<template>
  <div class="alert-container">
    <!-- 统计卡片 -->
    <t-row :gutter="[16, 16]" class="statistics-cards">
      <t-col :span="6">
        <t-card>
          <statistic-item
            title="预警商品数"
            :value="statistics.totalCount"
            :loading="loading"
            icon="error-circle"
            color="#E34D59"
          />
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card>
          <statistic-item
            title="缺货商品数"
            :value="statistics.outOfStockCount"
            :loading="loading"
            icon="close-circle"
            color="#FF0000"
          />
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card>
          <statistic-item
            title="库存总值"
            :value="statistics.totalStockValue"
            :precision="2"
            prefix="¥"
            :loading="loading"
            icon="chart-column"
            color="#00A870"
          />
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card>
          <statistic-item
            title="待处理预警"
            :value="statistics.pendingCount"
            :loading="loading"
            icon="time"
            color="#ED7B2F"
          />
        </t-card>
      </t-col>
    </t-row>

    <!-- 搜索卡片 -->
    <t-card class="search-card">
      <t-form
        ref="searchFormRef"
        :data="searchForm"
        label-width="100px"
        @submit="handleSearch"
        @reset="handleReset"
      >
        <t-row :gutter="16">
          <t-col :span="4">
            <t-form-item label="商品名称" name="productName">
              <t-input v-model="searchForm.productName" placeholder="请输入商品名称" clearable />
            </t-form-item>
          </t-col>
          <t-col :span="4">
            <t-form-item label="商品编码" name="productCode">
              <t-input v-model="searchForm.productCode" placeholder="请输入商品编码" clearable />
            </t-form-item>
          </t-col>
          <t-col :span="4">
            <t-form-item label="商品分类" name="category">
              <t-select v-model="searchForm.category" placeholder="请选择商品分类" clearable>
                <t-option value="食品" label="食品" />
                <t-option value="饮料" label="饮料" />
                <t-option value="日用品" label="日用品" />
              </t-select>
            </t-form-item>
          </t-col>
          <t-col :span="4">
            <t-form-item label="预警类型" name="alertType">
              <t-select v-model="searchForm.alertType" placeholder="请选择预警类型" clearable>
                <t-option value="low" label="低库存预警" />
                <t-option value="out" label="缺货预警" />
                <t-option value="overstock" label="积压预警" />
              </t-select>
            </t-form-item>
          </t-col>
          <t-col :span="4">
            <t-form-item label="预警级别" name="alertLevel">
              <t-select v-model="searchForm.alertLevel" placeholder="请选择预警级别" clearable>
                <t-option value="high" label="高" />
                <t-option value="medium" label="中" />
                <t-option value="low" label="低" />
              </t-select>
            </t-form-item>
          </t-col>
          <t-col :span="4">
            <t-form-item label="仓库" name="warehouse">
              <t-select v-model="searchForm.warehouse" placeholder="请选择仓库" clearable>
                <t-option value="主仓库" label="主仓库" />
                <t-option value="分仓A" label="分仓A" />
                <t-option value="分仓B" label="分仓B" />
              </t-select>
            </t-form-item>
          </t-col>
        </t-row>
        <t-row>
          <t-col :span="24" class="search-actions">
            <t-space>
              <t-button theme="primary" type="submit">查询</t-button>
              <t-button theme="default" type="reset">重置</t-button>
            </t-space>
          </t-col>
        </t-row>
      </t-form>
    </t-card>

    <!-- 操作栏 -->
    <t-card class="action-card">
      <t-space>
        <t-button theme="primary" @click="handleAdd">
          <template #icon><t-icon name="add" /></template>
          新增预警规则
        </t-button>
        <t-button theme="default" @click="handleBatchProcess">
          <template #icon><t-icon name="check" /></template>
          批量处理
        </t-button>
        <t-button theme="default" @click="handleExport">
          <template #icon><t-icon name="download" /></template>
          导出报表
        </t-button>
        <t-button theme="warning" @click="handleRefresh">
          <template #icon><t-icon name="refresh" /></template>
          刷新预警
        </t-button>
      </t-space>
    </t-card>

    <!-- 列表 -->
    <t-card>
      <t-table
        v-model:row-selection="selectedRowKeys"
        :data="tableData"
        :columns="columns"
        :loading="loading"
        :pagination="pagination"
        @page-change="handlePageChange"
        row-key="id"
      >
        <template #alertType="{ row }">
          <t-tag :theme="getAlertTypeTheme(row.alertType)">
            {{ getAlertTypeText(row.alertType) }}
          </t-tag>
        </template>
        <template #alertLevel="{ row }">
          <t-tag :theme="getAlertLevelTheme(row.alertLevel)">
            {{ getAlertLevelText(row.alertLevel) }}
          </t-tag>
        </template>
        <template #stockStatus="{ row }">
          <t-progress
            :percentage="row.stockPercentage"
            :theme="getStockStatusTheme(row.stockStatus)"
            :label="`${row.currentStock}/${row.maxStock}`"
            :size="'small'"
          />
        </template>
        <template #status="{ row }">
          <t-tag :theme="getStatusTheme(row.status)">
            {{ getStatusText(row.status) }}
          </t-tag>
        </template>
        <template #action="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleView(row)">详情</t-link>
            <t-link theme="primary" @click="handleEdit(row)">编辑</t-link>
            <t-link theme="success" @click="handleProcess(row)">处理</t-link>
            <t-link theme="danger" @click="handleDelete(row)">删除</t-link>
          </t-space>
        </template>
      </t-table>
    </t-card>

    <!-- 编辑弹窗 -->
    <t-dialog
      v-model:visible="editDialogVisible"
      header="预警规则"
      width="800px"
      :footer="false"
      @close="handleEditDialogClose"
    >
      <t-form
        ref="editFormRef"
        :data="editForm"
        :rules="editRules"
        label-width="120px"
        @submit="handleSubmit"
      >
        <t-row :gutter="16">
          <t-col :span="12">
            <t-form-item label="商品编码" name="productCode">
              <t-input v-model="editForm.productCode" placeholder="请选择商品" @click="handleSelectProduct">
                <template #suffix-icon>
                  <t-icon name="search" />
                </template>
              </t-input>
            </t-form-item>
          </t-col>
          <t-col :span="12">
            <t-form-item label="商品名称" name="productName">
              <t-input v-model="editForm.productName" placeholder="商品名称" disabled />
            </t-form-item>
          </t-col>
        </t-row>
        <t-row :gutter="16">
          <t-col :span="12">
            <t-form-item label="最低库存" name="minStock">
              <t-input-number v-model="editForm.minStock" placeholder="最低库存" :min="0" />
            </t-form-item>
          </t-col>
          <t-col :span="12">
            <t-form-item label="最高库存" name="maxStock">
              <t-input-number v-model="editForm.maxStock" placeholder="最高库存" :min="0" />
            </t-form-item>
          </t-col>
        </t-row>
        <t-row :gutter="16">
          <t-col :span="12">
            <t-form-item label="预警类型" name="alertType">
              <t-select v-model="editForm.alertType" placeholder="请选择预警类型">
                <t-option value="low" label="低库存预警" />
                <t-option value="out" label="缺货预警" />
                <t-option value="overstock" label="积压预警" />
              </t-select>
            </t-form-item>
          </t-col>
          <t-col :span="12">
            <t-form-item label="预警级别" name="alertLevel">
              <t-select v-model="editForm.alertLevel" placeholder="请选择预警级别">
                <t-option value="high" label="高" />
                <t-option value="medium" label="中" />
                <t-option value="low" label="低" />
              </t-select>
            </t-form-item>
          </t-col>
        </t-row>
        <t-row :gutter="16">
          <t-col :span="12">
            <t-form-item label="仓库" name="warehouse">
              <t-select v-model="editForm.warehouse" placeholder="请选择仓库">
                <t-option value="主仓库" label="主仓库" />
                <t-option value="分仓A" label="分仓A" />
                <t-option value="分仓B" label="分仓B" />
              </t-select>
            </t-form-item>
          </t-col>
          <t-col :span="12">
            <t-form-item label="通知方式" name="notifyMethod">
              <t-checkbox-group v-model="editForm.notifyMethod">
                <t-checkbox value="email">邮件</t-checkbox>
                <t-checkbox value="sms">短信</t-checkbox>
                <t-checkbox value="system">系统消息</t-checkbox>
              </t-checkbox-group>
            </t-form-item>
          </t-col>
        </t-row>
        <t-form-item label="预警说明" name="description">
          <t-textarea
            v-model="editForm.description"
            placeholder="请输入预警说明"
            :maxlength="500"
            :autosize="{ minRows: 3 }"
          />
        </t-form-item>
      </t-form>
      <div class="dialog-footer">
        <t-space>
          <t-button theme="default" @click="editDialogVisible = false">取消</t-button>
          <t-button theme="primary" @click="handleSubmit">保存</t-button>
        </t-space>
      </div>
    </t-dialog>

    <!-- 详情弹窗 -->
    <t-dialog v-model:visible="detailDialogVisible" header="预警详情" width="800px" :footer="false">
      <t-descriptions :column="2" bordered>
        <t-descriptions-item label="商品编码">{{ currentRow.productCode }}</t-descriptions-item>
        <t-descriptions-item label="商品名称">{{ currentRow.productName }}</t-descriptions-item>
        <t-descriptions-item label="商品分类">{{ currentRow.category }}</t-descriptions-item>
        <t-descriptions-item label="仓库">{{ currentRow.warehouse }}</t-descriptions-item>
        <t-descriptions-item label="当前库存">{{ currentRow.currentStock }}</t-descriptions-item>
        <t-descriptions-item label="最低库存">{{ currentRow.minStock }}</t-descriptions-item>
        <t-descriptions-item label="最高库存">{{ currentRow.maxStock }}</t-descriptions-item>
        <t-descriptions-item label="预警类型">
          <t-tag :theme="getAlertTypeTheme(currentRow.alertType)">
            {{ getAlertTypeText(currentRow.alertType) }}
          </t-tag>
        </t-descriptions-item>
        <t-descriptions-item label="预警级别">
          <t-tag :theme="getAlertLevelTheme(currentRow.alertLevel)">
            {{ getAlertLevelText(currentRow.alertLevel) }}
          </t-tag>
        </t-descriptions-item>
        <t-descriptions-item label="库存状态">
          <t-tag :theme="getStockStatusTheme(currentRow.stockStatus)">
            {{ getStockStatusText(currentRow.stockStatus) }}
          </t-tag>
        </t-descriptions-item>
        <t-descriptions-item label="处理状态">
          <t-tag :theme="getStatusTheme(currentRow.status)">
            {{ getStatusText(currentRow.status) }}
          </t-tag>
        </t-descriptions-item>
        <t-descriptions-item label="通知方式" :span="2">
          <t-tag v-for="method in currentRow.notifyMethod" :key="method" variant="light" theme="primary">
            {{ getNotifyMethodText(method) }}
          </t-tag>
        </t-descriptions-item>
        <t-descriptions-item label="预警说明" :span="2">{{ currentRow.description }}</t-descriptions-item>
        <t-descriptions-item label="创建时间">{{ currentRow.createTime }}</t-descriptions-item>
        <t-descriptions-item label="更新时间">{{ currentRow.updateTime }}</t-descriptions-item>
      </t-descriptions>
    </t-dialog>

    <!-- 处理弹窗 -->
    <t-dialog v-model:visible="processDialogVisible" header="处理预警" width="600px" @confirm="handleProcessConfirm">
      <t-form :data="processForm" label-width="100px">
        <t-form-item label="处理方式">
          <t-radio-group v-model="processForm.method">
            <t-radio value="purchase">采购补货</t-radio>
            <t-radio value="transfer">调拨补货</t-radio>
            <t-radio value="ignore">忽略预警</t-radio>
            <t-radio value="close">关闭预警</t-radio>
          </t-radio-group>
        </t-form-item>
        <t-form-item label="处理数量">
          <t-input-number v-model="processForm.quantity" placeholder="处理数量" :min="0" />
        </t-form-item>
        <t-form-item label="处理说明">
          <t-textarea v-model="processForm.remark" placeholder="请输入处理说明" :autosize="{ minRows: 3 }" />
        </t-form-item>
      </t-form>
    </t-dialog>

    <!-- 选择商品弹窗 -->
    <t-dialog v-model:visible="productDialogVisible" header="选择商品" width="800px" :footer="false">
      <t-table
        :data="productList"
        :columns="productColumns"
        :pagination="productPagination"
        row-key="id"
        @row-click="handleSelectProductRow"
      />
    </t-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { MessagePlugin } from 'tdesign-vue-next'
import type { FormInstanceFunctions, FormRule } from 'tdesign-vue-next'

defineOptions({ name: 'Alert' })

// 统计数据
const statistics = reactive({
  totalCount: 86,
  outOfStockCount: 12,
  totalStockValue: 456800.00,
  pendingCount: 35
})

// 搜索表单
const searchForm = reactive({
  productName: '',
  productCode: '',
  category: '',
  alertType: '',
  alertLevel: '',
  warehouse: ''
})

// 表格数据
const loading = ref(false)
const tableData = ref<any[]>([
  {
    id: '1',
    productCode: 'SP001',
    productName: '商品A',
    category: '食品',
    warehouse: '主仓库',
    currentStock: 15,
    minStock: 20,
    maxStock: 100,
    stockPercentage: 15,
    alertType: 'low',
    alertLevel: 'high',
    stockStatus: 'low',
    status: 'pending',
    notifyMethod: ['email', 'system'],
    description: '库存低于最低库存',
    createTime: '2026-02-19 10:00:00',
    updateTime: '2026-02-19 10:00:00'
  },
  {
    id: '2',
    productCode: 'SP002',
    productName: '商品B',
    category: '饮料',
    warehouse: '分仓A',
    currentStock: 0,
    minStock: 30,
    maxStock: 150,
    stockPercentage: 0,
    alertType: 'out',
    alertLevel: 'high',
    stockStatus: 'out',
    status: 'pending',
    notifyMethod: ['sms', 'system'],
    description: '库存为零，缺货',
    createTime: '2026-02-19 09:00:00',
    updateTime: '2026-02-19 09:00:00'
  },
  {
    id: '3',
    productCode: 'SP003',
    productName: '商品C',
    category: '日用品',
    warehouse: '分仓B',
    currentStock: 180,
    minStock: 20,
    maxStock: 100,
    stockPercentage: 100,
    alertType: 'overstock',
    alertLevel: 'medium',
    stockStatus: 'overstock',
    status: 'processed',
    notifyMethod: ['system'],
    description: '库存超过最高库存',
    createTime: '2026-02-18 16:00:00',
    updateTime: '2026-02-19 08:00:00'
  }
])

const selectedRowKeys = ref([])
const pagination = reactive({
  current: 1,
  pageSize: 20,
  total: tableData.value.length
})

// 表格列
const columns = [
  { colKey: 'productCode', title: '商品编码', width: 120 },
  { colKey: 'productName', title: '商品名称', width: 150 },
  { colKey: 'category', title: '商品分类', width: 100 },
  { colKey: 'warehouse', title: '仓库', width: 100 },
  { colKey: 'stockStatus', title: '库存状态', width: 200 },
  { colKey: 'currentStock', title: '当前库存', width: 100 },
  { colKey: 'minStock', title: '最低库存', width: 100 },
  { colKey: 'alertType', title: '预警类型', width: 120 },
  { colKey: 'alertLevel', title: '预警级别', width: 100 },
  { colKey: 'status', title: '处理状态', width: 100 },
  { colKey: 'action', title: '操作', width: 220, fixed: 'right' }
]

// 商品列表
const productDialogVisible = ref(false)
const productList = ref<any[]>([
  {
    id: '1',
    productCode: 'SP004',
    productName: '商品D',
    category: '食品',
    currentStock: 50
  }
])
const productPagination = reactive({
  current: 1,
  pageSize: 10,
  total: 1
})
const productColumns = [
  { colKey: 'productCode', title: '商品编码', width: 150 },
  { colKey: 'productName', title: '商品名称', width: 200 },
  { colKey: 'category', title: '商品分类', width: 100 },
  { colKey: 'currentStock', title: '当前库存', width: 100 },
  { colKey: 'action', title: '操作', width: 80, cell: () => '选择' }
]

// 编辑弹窗
const editDialogVisible = ref(false)
const editFormRef = ref<FormInstanceFunctions>()
const editForm = reactive({
  id: '',
  productCode: '',
  productName: '',
  category: '',
  warehouse: '',
  minStock: 0,
  maxStock: 0,
  alertType: '',
  alertLevel: '',
  notifyMethod: [],
  description: ''
})

const editRules: Record<string, FormRule[]> = {
  productCode: [{ required: true, message: '请选择商品', type: 'error' }],
  minStock: [{ required: true, message: '请输入最低库存', type: 'error' }],
  maxStock: [{ required: true, message: '请输入最高库存', type: 'error' }],
  alertType: [{ required: true, message: '请选择预警类型', type: 'error' }],
  alertLevel: [{ required: true, message: '请选择预警级别', type: 'error' }],
  warehouse: [{ required: true, message: '请选择仓库', type: 'error' }]
}

// 详情弹窗
const detailDialogVisible = ref(false)
const currentRow = ref<any>({})

// 处理弹窗
const processDialogVisible = ref(false)
const processForm = reactive({
  method: 'purchase',
  quantity: 0,
  remark: ''
})
const currentProcessRow = ref<any>(null)

// 方法
const handleSearch = () => {
  loading.value = true
  setTimeout(() => {
    loading.value = false
    MessagePlugin.success('查询成功')
  }, 500)
}

const handleReset = () => {
  searchForm.productName = ''
  searchForm.productCode = ''
  searchForm.category = ''
  searchForm.alertType = ''
  searchForm.alertLevel = ''
  searchForm.warehouse = ''
}

const handlePageChange = (pageInfo: any) => {
  pagination.current = pageInfo.current
  pagination.pageSize = pageInfo.pageSize
}

const handleAdd = () => {
  Object.assign(editForm, {
    id: '',
    productCode: '',
    productName: '',
    category: '',
    warehouse: '',
    minStock: 0,
    maxStock: 0,
    alertType: '',
    alertLevel: '',
    notifyMethod: [],
    description: ''
  })
  editDialogVisible.value = true
}

const handleEdit = (row: any) => {
  Object.assign(editForm, row)
  editDialogVisible.value = true
}

const handleView = (row: any) => {
  currentRow.value = row
  detailDialogVisible.value = true
}

const handleDelete = (row: any) => {
  MessagePlugin.warning('删除功能待后端接口支持')
}

const handleBatchProcess = () => {
  if (selectedRowKeys.value.length === 0) {
    MessagePlugin.warning('请选择要处理的预警')
    return
  }
  MessagePlugin.warning('批量处理功能待后端接口支持')
}

const handleExport = () => {
  MessagePlugin.info('导出功能开发中')
}

const handleRefresh = () => {
  loading.value = true
  setTimeout(() => {
    loading.value = false
    MessagePlugin.success('刷新成功')
  }, 500)
}

const handleSelectProduct = () => {
  productDialogVisible.value = true
}

const handleSelectProductRow = (row: any) => {
  editForm.productCode = row.productCode
  editForm.productName = row.productName
  editForm.category = row.category
  productDialogVisible.value = false
}

const handleEditDialogClose = () => {
  editFormRef.value?.reset()
}

const handleSubmit = async () => {
  const valid = await editFormRef.value?.validate()
  if (valid) {
    MessagePlugin.success('保存成功')
    editDialogVisible.value = false
  }
}

const handleProcess = (row: any) => {
  currentProcessRow.value = row
  processForm.method = 'purchase'
  processForm.quantity = row.minStock - row.currentStock
  processForm.remark = ''
  processDialogVisible.value = true
}

const handleProcessConfirm = () => {
  if (currentProcessRow.value) {
    currentProcessRow.value.status = 'processed'
    MessagePlugin.success('处理成功')
    processDialogVisible.value = false
  }
}

// 辅助函数
const getAlertTypeText = (type: string) => {
  const map: Record<string, string> = {
    low: '低库存预警',
    out: '缺货预警',
    overstock: '积压预警'
  }
  return map[type] || type
}

const getAlertTypeTheme = (type: string) => {
  const map: Record<string, string> = {
    low: 'warning',
    out: 'danger',
    overstock: 'default'
  }
  return map[type] || 'default'
}

const getAlertLevelText = (level: string) => {
  const map: Record<string, string> = {
    high: '高',
    medium: '中',
    low: '低'
  }
  return map[level] || level
}

const getAlertLevelTheme = (level: string) => {
  const map: Record<string, string> = {
    high: 'danger',
    medium: 'warning',
    low: 'default'
  }
  return map[level] || 'default'
}

const getStockStatusText = (status: string) => {
  const map: Record<string, string> = {
    normal: '正常',
    low: '库存不足',
    out: '缺货',
    overstock: '积压'
  }
  return map[status] || status
}

const getStockStatusTheme = (status: string) => {
  const map: Record<string, string> = {
    normal: 'success',
    low: 'warning',
    out: 'error',
    overstock: 'default'
  }
  return map[status] || 'default'
}

const getStatusText = (status: string) => {
  const map: Record<string, string> = {
    pending: '待处理',
    processed: '已处理',
    ignored: '已忽略',
    closed: '已关闭'
  }
  return map[status] || status
}

const getStatusTheme = (status: string) => {
  const map: Record<string, string> = {
    pending: 'warning',
    processed: 'success',
    ignored: 'default',
    closed: 'default'
  }
  return map[status] || 'default'
}

const getNotifyMethodText = (method: string) => {
  const map: Record<string, string> = {
    email: '邮件',
    sms: '短信',
    system: '系统消息'
  }
  return map[method] || method
}
</script>

<script lang="ts">
import { defineComponent, h, computed } from 'vue'

const StatisticItem = defineComponent({
  name: 'StatisticItem',
  props: {
    title: String,
    value: [Number, String],
    precision: { type: Number, default: 0 },
    prefix: String,
    loading: Boolean,
    icon: String,
    color: { type: String, default: '#0052D9' }
  },
  setup(props) {
    const displayValue = computed(() => {
      if (props.loading) return '-'
      if (typeof props.value === 'number') {
        return props.value.toFixed(props.precision)
      }
      return props.value
    })
    
    return () => h('div', { class: 'statistic-item' }, [
      h('div', { class: 'statistic-icon', style: { color: props.color } }, [
        h('t-icon', { name: props.icon, size: '32px' })
      ]),
      h('div', { class: 'statistic-content' }, [
        h('div', { class: 'statistic-title' }, props.title),
        h('div', { class: 'statistic-value', style: { color: props.color } }, [
          props.prefix,
          displayValue.value
        ])
      ])
    ])
  }
})

const components = { StatisticItem }
</script>

<style scoped lang="less">
.alert-container {
  padding: 16px;
  
  .statistics-cards {
    margin-bottom: 16px;
    
    .statistic-item {
      display: flex;
      align-items: center;
      
      .statistic-icon {
        margin-right: 16px;
      }
      
      .statistic-content {
        flex: 1;
        
        .statistic-title {
          font-size: 14px;
          color: var(--td-text-color-secondary);
          margin-bottom: 8px;
        }
        
        .statistic-value {
          font-size: 28px;
          font-weight: bold;
        }
      }
    }
  }
  
  .search-card {
    margin-bottom: 16px;
    
    .search-actions {
      display: flex;
      justify-content: flex-end;
    }
  }
  
  .action-card {
    margin-bottom: 16px;
  }
  
  .dialog-footer {
    margin-top: 24px;
    display: flex;
    justify-content: flex-end;
  }
}
</style>
