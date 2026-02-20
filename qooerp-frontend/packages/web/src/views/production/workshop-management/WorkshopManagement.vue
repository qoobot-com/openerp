<template>
  <div class="workshop-management">
    <!-- 统计卡片 -->
    <t-row :gutter="[16, 16]" class="statistics-cards">
      <t-col :span="3">
        <t-card theme="primary1" :header-bordered="false">
          <div class="statistic-content">
            <div class="statistic-value">{{ statistics.total }}</div>
            <div class="statistic-label">车间总数</div>
          </div>
        </t-card>
      </t-col>
      <t-col :span="3">
        <t-card theme="success" :header-bordered="false">
          <div class="statistic-content">
            <div class="statistic-value">{{ statistics.active }}</div>
            <div class="statistic-label">正常生产</div>
          </div>
        </t-card>
      </t-col>
      <t-col :span="3">
        <t-card theme="warning" :header-bordered="false">
          <div class="statistic-content">
            <div class="statistic-value">{{ statistics.equipmentCount }}</div>
            <div class="statistic-label">设备总数</div>
          </div>
        </t-card>
      </t-col>
      <t-col :span="3">
        <t-card theme="info" :header-bordered="false">
          <div class="statistic-content">
            <div class="statistic-value">{{ statistics.workerCount }}</div>
            <div class="statistic-label">员工总数</div>
          </div>
        </t-card>
      </t-col>
    </t-row>

    <!-- 搜索卡片 -->
    <t-card title="车间查询" class="search-card">
      <t-form :data="searchForm" layout="inline" @submit="handleSearch">
        <t-form-item label="车间编号" name="workshopCode">
          <t-input v-model="searchForm.workshopCode" placeholder="请输入车间编号" clearable />
        </t-form-item>
        <t-form-item label="车间名称" name="workshopName">
          <t-input v-model="searchForm.workshopName" placeholder="请输入车间名称" clearable />
        </t-form-item>
        <t-form-item label="车间类型" name="workshopType">
          <t-select v-model="searchForm.workshopType" placeholder="请选择车间类型" clearable>
            <t-option value="production" label="生产车间" />
            <t-option value="assembly" label="装配车间" />
            <t-option value="packaging" label="包装车间" />
            <t-option value="warehouse" label="仓储车间" />
          </t-select>
        </t-form-item>
        <t-form-item label="车间状态" name="status">
          <t-select v-model="searchForm.status" placeholder="请选择车间状态" clearable>
            <t-option value="active" label="正常生产" />
            <t-option value="maintenance" label="维护中" />
            <t-option value="suspended" label="停工" />
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

    <!-- 车间列表 -->
    <t-card title="车间列表" class="table-card">
      <template #actions>
        <t-space>
          <t-button theme="primary" @click="handleAdd">
            <template #icon><add-icon /></template>
            新增车间
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
          <t-tag v-if="row.status === 'active'" theme="success" variant="light">正常生产</t-tag>
          <t-tag v-else-if="row.status === 'maintenance'" theme="warning" variant="light">维护中</t-tag>
          <t-tag v-else theme="error" variant="light">停工</t-tag>
        </template>
        <template #workshopType="{ row }">
          <t-tag v-if="row.workshopType === 'production'" theme="primary" variant="light">生产车间</t-tag>
          <t-tag v-else-if="row.workshopType === 'assembly'" theme="success" variant="light">装配车间</t-tag>
          <t-tag v-else-if="row.workshopType === 'packaging'" theme="warning" variant="light">包装车间</t-tag>
          <t-tag v-else theme="info" variant="light">仓储车间</t-tag>
        </template>
        <template #action="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleView(row)">查看</t-link>
            <t-link theme="primary" @click="handleEdit(row)">编辑</t-link>
            <t-link theme="danger" @click="handleDelete(row)">删除</t-link>
          </t-space>
        </template>
      </t-table>
    </t-card>

    <!-- 新增/编辑车间弹窗 -->
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
            <t-form-item label="车间编号" name="workshopCode">
              <t-input v-model="formData.workshopCode" placeholder="请输入车间编号" />
            </t-form-item>
            <t-form-item label="车间名称" name="workshopName">
              <t-input v-model="formData.workshopName" placeholder="请输入车间名称" />
            </t-form-item>
            <t-form-item label="车间类型" name="workshopType">
              <t-select v-model="formData.workshopType" placeholder="请选择车间类型">
                <t-option value="production" label="生产车间" />
                <t-option value="assembly" label="装配车间" />
                <t-option value="packaging" label="包装车间" />
                <t-option value="warehouse" label="仓储车间" />
              </t-select>
            </t-form-item>
            <t-form-item label="车间状态" name="status">
              <t-select v-model="formData.status" placeholder="请选择车间状态">
                <t-option value="active" label="正常生产" />
                <t-option value="maintenance" label="维护中" />
                <t-option value="suspended" label="停工" />
              </t-select>
            </t-form-item>
            <t-form-item label="车间面积(m²)" name="area">
              <t-input-number v-model="formData.area" :min="0" placeholder="请输入车间面积" />
            </t-form-item>
            <t-form-item label="车间负责人" name="manager">
              <t-select v-model="formData.manager" placeholder="请选择车间负责人" filterable>
                <t-option value="1" label="张三" />
                <t-option value="2" label="李四" />
                <t-option value="3" label="王五" />
              </t-select>
            </t-form-item>
            <t-form-item label="联系电话" name="phone">
              <t-input v-model="formData.phone" placeholder="请输入联系电话" />
            </t-form-item>
            <t-form-item label="备注" name="remark">
              <t-textarea v-model="formData.remark" placeholder="请输入备注" :maxlength="500" />
            </t-form-item>
          </t-form>
        </t-tab-panel>
        <t-tab-panel value="summary" label="汇总信息">
          <t-descriptions :column="2" bordered>
            <t-descriptions-item label="车间编号">{{ formData.workshopCode }}</t-descriptions-item>
            <t-descriptions-item label="车间名称">{{ formData.workshopName }}</t-descriptions-item>
            <t-descriptions-item label="车间类型">
              <t-tag v-if="formData.workshopType === 'production'" theme="primary" variant="light">生产车间</t-tag>
              <t-tag v-else-if="formData.workshopType === 'assembly'" theme="success" variant="light">装配车间</t-tag>
              <t-tag v-else-if="formData.workshopType === 'packaging'" theme="warning" variant="light">包装车间</t-tag>
              <t-tag v-else theme="info" variant="light">仓储车间</t-tag>
            </t-descriptions-item>
            <t-descriptions-item label="车间状态">
              <t-tag v-if="formData.status === 'active'" theme="success" variant="light">正常生产</t-tag>
              <t-tag v-else-if="formData.status === 'maintenance'" theme="warning" variant="light">维护中</t-tag>
              <t-tag v-else theme="error" variant="light">停工</t-tag>
            </t-descriptions-item>
            <t-descriptions-item label="车间面积">{{ formData.area }}m²</t-descriptions-item>
            <t-descriptions-item label="设备数量">{{ formData.equipmentCount }}台</t-descriptions-item>
            <t-descriptions-item label="员工数量">{{ formData.workerCount }}人</t-descriptions-item>
            <t-descriptions-item label="车间负责人">{{ formData.manager }}</t-descriptions-item>
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

    <!-- 车间详情弹窗 -->
    <t-dialog
      v-model:visible="detailVisible"
      header="车间详情"
      :width="1000"
      :footer="false"
    >
      <t-tabs v-model="detailTab" theme="card">
        <t-tab-panel value="basic" label="基本信息">
          <t-descriptions :column="2" bordered>
            <t-descriptions-item label="车间编号">{{ detailData.workshopCode }}</t-descriptions-item>
            <t-descriptions-item label="车间名称">{{ detailData.workshopName }}</t-descriptions-item>
            <t-descriptions-item label="车间类型">
              <t-tag v-if="detailData.workshopType === 'production'" theme="primary" variant="light">生产车间</t-tag>
              <t-tag v-else-if="detailData.workshopType === 'assembly'" theme="success" variant="light">装配车间</t-tag>
              <t-tag v-else-if="detailData.workshopType === 'packaging'" theme="warning" variant="light">包装车间</t-tag>
              <t-tag v-else theme="info" variant="light">仓储车间</t-tag>
            </t-descriptions-item>
            <t-descriptions-item label="车间状态">
              <t-tag v-if="detailData.status === 'active'" theme="success" variant="light">正常生产</t-tag>
              <t-tag v-else-if="detailData.status === 'maintenance'" theme="warning" variant="light">维护中</t-tag>
              <t-tag v-else theme="error" variant="light">停工</t-tag>
            </t-descriptions-item>
            <t-descriptions-item label="车间面积">{{ detailData.area }}m²</t-descriptions-item>
            <t-descriptions-item label="设备数量">{{ detailData.equipmentCount }}台</t-descriptions-item>
            <t-descriptions-item label="员工数量">{{ detailData.workerCount }}人</t-descriptions-item>
            <t-descriptions-item label="车间负责人">{{ detailData.manager }}</t-descriptions-item>
            <t-descriptions-item label="联系电话">{{ detailData.phone }}</t-descriptions-item>
            <t-descriptions-item label="备注" :span="2">{{ detailData.remark }}</t-descriptions-item>
          </t-descriptions>
        </t-tab-panel>
        <t-tab-panel value="equipment" label="设备列表">
          <t-table
            :data="detailData.equipmentList || []"
            :columns="equipmentColumns"
            :pagination="false"
            row-key="id"
          >
            <template #status="{ row }">
              <t-tag v-if="row.status === 'normal'" theme="success" variant="light">正常运行</t-tag>
              <t-tag v-else-if="row.status === 'maintenance'" theme="warning" variant="light">维护中</t-tag>
              <t-tag v-else theme="error" variant="light">故障</t-tag>
            </template>
          </t-table>
        </t-tab-panel>
        <t-tab-panel value="workers" label="员工列表">
          <t-table
            :data="detailData.workerList || []"
            :columns="workerColumns"
            :pagination="false"
            row-key="id"
          />
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
  total: 15,
  active: 12,
  equipmentCount: 150,
  workerCount: 300
})

// 搜索表单
const searchForm = reactive({
  workshopCode: '',
  workshopName: '',
  workshopType: '',
  status: ''
})

// 表格数据
const loading = ref(false)
const tableData = ref([
  {
    id: 1,
    workshopCode: 'WS-A',
    workshopName: 'A车间',
    workshopType: 'production',
    status: 'active',
    area: 2000,
    manager: '1',
    phone: '13800138001',
    equipmentCount: 50,
    workerCount: 100,
    remark: '主要生产车间'
  },
  {
    id: 2,
    workshopCode: 'WS-B',
    workshopName: 'B车间',
    workshopType: 'assembly',
    status: 'active',
    area: 1500,
    manager: '2',
    phone: '13800138002',
    equipmentCount: 40,
    workerCount: 80,
    remark: '装配车间'
  },
  {
    id: 3,
    workshopCode: 'WS-C',
    workshopName: 'C车间',
    workshopType: 'packaging',
    status: 'maintenance',
    area: 1000,
    manager: '3',
    phone: '13800138003',
    equipmentCount: 30,
    workerCount: 60,
    remark: '包装车间'
  }
])

// 表格列定义
const columns = [
  { colKey: 'workshopCode', title: '车间编号', width: 100 },
  { colKey: 'workshopName', title: '车间名称', width: 120 },
  { colKey: 'workshopType', title: '车间类型', width: 120, cell: 'workshopType' },
  { colKey: 'area', title: '面积(m²)', width: 100 },
  { colKey: 'equipmentCount', title: '设备数量', width: 100 },
  { colKey: 'workerCount', title: '员工数量', width: 100 },
  { colKey: 'manager', title: '负责人', width: 100 },
  { colKey: 'status', title: '车间状态', width: 100, cell: 'status' },
  { colKey: 'action', title: '操作', width: 180, fixed: 'right', cell: 'action' }
]

const equipmentColumns = [
  { colKey: 'equipmentCode', title: '设备编号', width: 120 },
  { colKey: 'equipmentName', title: '设备名称', width: 150 },
  { colKey: 'specification', title: '规格型号', width: 120 },
  { colKey: 'status', title: '设备状态', width: 100, cell: 'status' }
]

const workerColumns = [
  { colKey: 'workerCode', title: '员工编号', width: 120 },
  { colKey: 'workerName', title: '员工姓名', width: 100 },
  { colKey: 'position', title: '岗位', width: 100 },
  { colKey: 'phone', title: '联系电话', width: 120 }
]

// 分页
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 15
})

const rowKey = 'id'

// 新增/编辑弹窗
const dialogVisible = ref(false)
const dialogTitle = computed(() => (formData.id ? '编辑车间' : '新增车间'))
const activeTab = ref('basic')
const formRef = ref()
const formData = reactive({
  id: undefined,
  workshopCode: '',
  workshopName: '',
  workshopType: '',
  status: 'active',
  area: 0,
  manager: '',
  phone: '',
  equipmentCount: 0,
  workerCount: 0,
  remark: ''
})

const rules = {
  workshopCode: [{ required: true, message: '请输入车间编号' }],
  workshopName: [{ required: true, message: '请输入车间名称' }],
  workshopType: [{ required: true, message: '请选择车间类型' }],
  status: [{ required: true, message: '请选择车间状态' }],
  area: [{ required: true, message: '请输入车间面积' }],
  manager: [{ required: true, message: '请选择车间负责人' }]
}

// 车间详情弹窗
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
    workshopCode: '',
    workshopName: '',
    workshopType: '',
    status: ''
  })
  MessagePlugin.success('已重置')
}

// 新增车间
const handleAdd = () => {
  Object.assign(formData, {
    id: undefined,
    workshopCode: '',
    workshopName: '',
    workshopType: '',
    status: 'active',
    area: 0,
    manager: '',
    phone: '',
    equipmentCount: 0,
    workerCount: 0,
    remark: ''
  })
  activeTab.value = 'basic'
  dialogVisible.value = true
}

// 编辑车间
const handleEdit = (row: any) => {
  Object.assign(formData, { ...row })
  activeTab.value = 'basic'
  dialogVisible.value = true
}

// 查看详情
const handleView = (row: any) => {
  detailData.value = {
    ...row,
    equipmentList: [
      { id: 1, equipmentCode: 'EQ-001', equipmentName: '数控机床A1', specification: 'CNC-2000', status: 'normal' },
      { id: 2, equipmentCode: 'EQ-002', equipmentName: '检测仪器B1', specification: 'TEST-500', status: 'maintenance' }
    ],
    workerList: [
      { id: 1, workerCode: 'W-001', workerName: '张三', position: '操作工', phone: '13800138001' },
      { id: 2, workerCode: 'W-002', workerName: '李四', position: '技术员', phone: '13800138002' }
    ]
  }
  detailTab.value = 'basic'
  detailVisible.value = true
}

// 删除车间
const handleDelete = (row: any) => {
  const dialog = DialogPlugin({
    header: '确认删除',
    body: `确认要删除车间"${row.workshopName}"吗？`,
    confirmBtn: '确定',
    cancelBtn: '取消',
    onConfirm: () => {
      dialog.hide()
      MessagePlugin.success('删除成功')
    }
  })
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
</script>

<style scoped lang="less">
.workshop-management {
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
}
</style>
