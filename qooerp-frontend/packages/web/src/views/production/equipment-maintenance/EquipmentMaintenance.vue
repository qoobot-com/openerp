<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { MessagePlugin } from 'tdesign-vue-next'

// 统计数据
const statistics = reactive({
  total: 56,
  normal: 48,
  maintenance: 5,
  repair: 3,
})

// 搜索表单
const searchForm = reactive({
  equipmentNo: '',
  equipmentName: '',
  equipmentType: '',
  equipmentStatus: '',
  department: '',
})

// 设备类型选项
const equipmentTypeOptions = [
  { label: '全部', value: '' },
  { label: '生产设备', value: 'production' },
  { label: '辅助设备', value: 'auxiliary' },
  { label: '检测设备', value: 'test' },
  { label: '运输设备', value: 'transport' },
]

// 设备状态选项
const equipmentStatusOptions = [
  { label: '全部', value: '' },
  { label: '正常', value: 'normal' },
  { label: '维护中', value: 'maintenance' },
  { label: '维修中', value: 'repair' },
  { label: '停用', value: 'stopped' },
]

// 表格数据
const tableData = ref([])
const loading = ref(false)

// 分页
const pagination = reactive({
  current: 1,
  pageSize: 20,
  total: 56,
})

// 弹窗
const dialogVisible = ref(false)
const dialogMode = ref('add')
const detailDialogVisible = ref(false)

// 表单数据
const formData = reactive({
  equipmentNo: '',
  equipmentName: '',
  equipmentType: '',
  model: '',
  manufacturer: '',
  purchaseDate: '',
  department: '',
  location: '',
  equipmentStatus: '',
  lastMaintenanceDate: '',
  nextMaintenanceDate: '',
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
      equipmentNo: `EQ${String(i + 1).padStart(4, '0')}`,
      equipmentName: ['数控车床', '铣床', '磨床', '钻床', '冲床'][i % 5],
      equipmentType: ['production', 'auxiliary', 'test', 'transport'][i % 4],
      model: `MODEL-${String(i + 1).padStart(3, '0')}`,
      manufacturer: '设备制造厂',
      purchaseDate: '2024-01-01',
      department: ['生产部', '装配部', '质检部', '物流部'][i % 4],
      location: `车间${(i % 5) + 1}`,
      equipmentStatus: ['normal', 'maintenance', 'repair', 'stopped'][i % 4],
      lastMaintenanceDate: '2026-02-10',
      nextMaintenanceDate: '2026-03-10',
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
    equipmentNo: '',
    equipmentName: '',
    equipmentType: '',
    equipmentStatus: '',
    department: '',
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
  MessagePlugin.warning('请确认是否删除此设备？')
}

// 维护保养
const handleMaintenance = (record) => {
  MessagePlugin.success('发起维护保养')
}

// 维修申请
const handleRepair = (record) => {
  MessagePlugin.success('发起维修申请')
}

// 提交表单
const handleSubmit = () => {
  MessagePlugin.success(dialogMode.value === 'add' ? '新增成功' : '编辑成功')
  dialogVisible.value = false
  fetchData()
}

// 设备类型标签
const getEquipmentTypeTag = (type) => {
  const map = {
    production: { label: '生产设备', theme: 'primary' },
    auxiliary: { label: '辅助设备', theme: 'success' },
    test: { label: '检测设备', theme: 'warning' },
    transport: { label: '运输设备', theme: 'danger' },
  }
  return map[type] || { label: '未知', theme: 'default' }
}

// 设备状态标签
const getEquipmentStatusTag = (status) => {
  const map = {
    normal: { label: '正常', theme: 'success' },
    maintenance: { label: '维护中', theme: 'warning' },
    repair: { label: '维修中', theme: 'danger' },
    stopped: { label: '停用', theme: 'default' },
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
  <div class="production-equipment-maintenance">
    <!-- 统计卡片 -->
    <t-row :gutter="16" class="statistics-cards">
      <t-col :span="6">
        <t-card>
          <t-statistic title="设备总数" :value="statistics.total" />
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="success">
          <t-statistic title="正常运行" :value="statistics.normal" />
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="warning">
          <t-statistic title="维护中" :value="statistics.maintenance" />
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="danger">
          <t-statistic title="维修中" :value="statistics.repair" />
        </t-card>
      </t-col>
    </t-row>

    <!-- 搜索卡片 -->
    <t-card title="设备查询" class="search-card">
      <t-form :data="searchForm" layout="inline">
        <t-form-item label="设备编号">
          <t-input v-model="searchForm.equipmentNo" placeholder="请输入设备编号" clearable />
        </t-form-item>
        <t-form-item label="设备名称">
          <t-input v-model="searchForm.equipmentName" placeholder="请输入设备名称" clearable />
        </t-form-item>
        <t-form-item label="设备类型">
          <t-select v-model="searchForm.equipmentType" :options="equipmentTypeOptions" placeholder="请选择设备类型" clearable />
        </t-form-item>
        <t-form-item label="设备状态">
          <t-select v-model="searchForm.equipmentStatus" :options="equipmentStatusOptions" placeholder="请选择设备状态" clearable />
        </t-form-item>
        <t-form-item label="所属部门">
          <t-input v-model="searchForm.department" placeholder="请输入所属部门" clearable />
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
            新增设备
          </t-button>
          <t-button theme="default">
            <template #icon><t-icon name="download" /></template>
            导出报表
          </t-button>
        </t-space>
      </t-col>
    </t-row>

    <!-- 设备列表表格 -->
    <t-card title="设备列表" class="table-card">
      <t-table
        :data="tableData"
        :columns="[
          { colKey: 'equipmentNo', title: '设备编号', width: 120 },
          { colKey: 'equipmentName', title: '设备名称', width: 120 },
          { colKey: 'equipmentType', title: '设备类型', width: 120 },
          { colKey: 'model', title: '型号', width: 120 },
          { colKey: 'manufacturer', title: '制造商', width: 140 },
          { colKey: 'purchaseDate', title: '采购日期', width: 120 },
          { colKey: 'department', title: '所属部门', width: 120 },
          { colKey: 'location', title: '存放位置', width: 100 },
          { colKey: 'equipmentStatus', title: '设备状态', width: 100 },
          { colKey: 'lastMaintenanceDate', title: '上次维护日期', width: 140 },
          { colKey: 'nextMaintenanceDate', title: '下次维护日期', width: 140 },
          { colKey: 'action', title: '操作', width: 250, fixed: 'right' },
        ]"
        :loading="loading"
        :pagination="pagination"
        @page-change="onPageChange"
        row-key="key"
        bordered
        stripe
      >
        <template #equipmentType="{ row }">
          <t-tag :theme="getEquipmentTypeTag(row.equipmentType).theme">
            {{ getEquipmentTypeTag(row.equipmentType).label }}
          </t-tag>
        </template>
        <template #equipmentStatus="{ row }">
          <t-tag :theme="getEquipmentStatusTag(row.equipmentStatus).theme">
            {{ getEquipmentStatusTag(row.equipmentStatus).label }}
          </t-tag>
        </template>
        <template #action="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleDetail(row)">详情</t-link>
            <t-link theme="primary" @click="handleEdit(row)">编辑</t-link>
            <t-link theme="warning" @click="handleMaintenance(row)">维护</t-link>
            <t-link theme="danger" @click="handleRepair(row)">维修</t-link>
            <t-link theme="danger" @click="handleDelete(row)">删除</t-link>
          </t-space>
        </template>
      </t-table>
    </t-card>

    <!-- 新增/编辑弹窗 -->
    <t-dialog
      v-model:visible="dialogVisible"
      :header="dialogMode === 'add' ? '新增设备' : '编辑设备'"
      width="800px"
      :confirm-btn="{ content: '确定', theme: 'primary' }"
      @confirm="handleSubmit"
    >
      <t-form :data="formData" label-width="100px">
        <t-form-item label="设备编号" name="equipmentNo">
          <t-input v-model="formData.equipmentNo" placeholder="请输入设备编号" />
        </t-form-item>
        <t-form-item label="设备名称" name="equipmentName">
          <t-input v-model="formData.equipmentName" placeholder="请输入设备名称" />
        </t-form-item>
        <t-form-item label="设备类型" name="equipmentType">
          <t-select v-model="formData.equipmentType" placeholder="请选择设备类型">
            <t-option value="production" label="生产设备" />
            <t-option value="auxiliary" label="辅助设备" />
            <t-option value="test" label="检测设备" />
            <t-option value="transport" label="运输设备" />
          </t-select>
        </t-form-item>
        <t-form-item label="型号" name="model">
          <t-input v-model="formData.model" placeholder="请输入型号" />
        </t-form-item>
        <t-form-item label="制造商" name="manufacturer">
          <t-input v-model="formData.manufacturer" placeholder="请输入制造商" />
        </t-form-item>
        <t-form-item label="采购日期" name="purchaseDate">
          <t-date-picker v-model="formData.purchaseDate" />
        </t-form-item>
        <t-form-item label="所属部门" name="department">
          <t-input v-model="formData.department" placeholder="请输入所属部门" />
        </t-form-item>
        <t-form-item label="存放位置" name="location">
          <t-input v-model="formData.location" placeholder="请输入存放位置" />
        </t-form-item>
        <t-form-item label="设备状态" name="equipmentStatus">
          <t-select v-model="formData.equipmentStatus" placeholder="请选择设备状态">
            <t-option value="normal" label="正常" />
            <t-option value="maintenance" label="维护中" />
            <t-option value="repair" label="维修中" />
            <t-option value="stopped" label="停用" />
          </t-select>
        </t-form-item>
        <t-form-item label="上次维护日期" name="lastMaintenanceDate">
          <t-date-picker v-model="formData.lastMaintenanceDate" />
        </t-form-item>
        <t-form-item label="下次维护日期" name="nextMaintenanceDate">
          <t-date-picker v-model="formData.nextMaintenanceDate" />
        </t-form-item>
        <t-form-item label="备注" name="remark">
          <t-textarea v-model="formData.remark" placeholder="请输入备注" />
        </t-form-item>
      </t-form>
    </t-dialog>

    <!-- 详情弹窗 -->
    <t-dialog
      v-model:visible="detailDialogVisible"
      header="设备详情"
      width="800px"
      :footer="false"
    >
      <t-descriptions v-if="detailData" :column="2" bordered>
        <t-descriptions-item label="设备编号">{{ detailData.equipmentNo }}</t-descriptions-item>
        <t-descriptions-item label="设备名称">{{ detailData.equipmentName }}</t-descriptions-item>
        <t-descriptions-item label="设备类型">
          <t-tag :theme="getEquipmentTypeTag(detailData.equipmentType).theme">
            {{ getEquipmentTypeTag(detailData.equipmentType).label }}
          </t-tag>
        </t-descriptions-item>
        <t-descriptions-item label="型号">{{ detailData.model }}</t-descriptions-item>
        <t-descriptions-item label="制造商">{{ detailData.manufacturer }}</t-descriptions-item>
        <t-descriptions-item label="采购日期">{{ detailData.purchaseDate }}</t-descriptions-item>
        <t-descriptions-item label="所属部门">{{ detailData.department }}</t-descriptions-item>
        <t-descriptions-item label="存放位置">{{ detailData.location }}</t-descriptions-item>
        <t-descriptions-item label="设备状态">
          <t-tag :theme="getEquipmentStatusTag(detailData.equipmentStatus).theme">
            {{ getEquipmentStatusTag(detailData.equipmentStatus).label }}
          </t-tag>
        </t-descriptions-item>
        <t-descriptions-item label="上次维护日期">{{ detailData.lastMaintenanceDate }}</t-descriptions-item>
        <t-descriptions-item label="下次维护日期">{{ detailData.nextMaintenanceDate }}</t-descriptions-item>
      </t-descriptions>
    </t-dialog>
  </div>
</template>

<style scoped lang="less">
.production-equipment-maintenance {
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
