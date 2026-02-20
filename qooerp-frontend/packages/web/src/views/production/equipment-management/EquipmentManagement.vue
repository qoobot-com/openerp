<template>
  <div class="equipment-management">
    <!-- 统计卡片 -->
    <t-row :gutter="[16, 16]" class="statistics-cards">
      <t-col :span="3">
        <t-card theme="primary1" :header-bordered="false">
          <div class="statistic-content">
            <div class="statistic-value">{{ statistics.total }}</div>
            <div class="statistic-label">设备总数</div>
          </div>
        </t-card>
      </t-col>
      <t-col :span="3">
        <t-card theme="success" :header-bordered="false">
          <div class="statistic-content">
            <div class="statistic-value">{{ statistics.normal }}</div>
            <div class="statistic-label">正常运行</div>
          </div>
        </t-card>
      </t-col>
      <t-col :span="3">
        <t-card theme="warning" :header-bordered="false">
          <div class="statistic-content">
            <div class="statistic-value">{{ statistics.maintenance }}</div>
            <div class="statistic-label">维护中</div>
          </div>
        </t-card>
      </t-col>
      <t-col :span="3">
        <t-card theme="error" :header-bordered="false">
          <div class="statistic-content">
            <div class="statistic-value">{{ statistics.fault }}</div>
            <div class="statistic-label">故障</div>
          </div>
        </t-card>
      </t-col>
    </t-row>

    <!-- 搜索卡片 -->
    <t-card title="设备查询" class="search-card">
      <t-form :data="searchForm" layout="inline" @submit="handleSearch">
        <t-form-item label="设备编号" name="equipmentCode">
          <t-input v-model="searchForm.equipmentCode" placeholder="请输入设备编号" clearable />
        </t-form-item>
        <t-form-item label="设备名称" name="equipmentName">
          <t-input v-model="searchForm.equipmentName" placeholder="请输入设备名称" clearable />
        </t-form-item>
        <t-form-item label="设备类型" name="equipmentType">
          <t-select v-model="searchForm.equipmentType" placeholder="请选择设备类型" clearable>
            <t-option value="production" label="生产设备" />
            <t-option value="testing" label="检测设备" />
            <t-option value="transport" label="运输设备" />
            <t-option value="other" label="其他设备" />
          </t-select>
        </t-form-item>
        <t-form-item label="设备状态" name="status">
          <t-select v-model="searchForm.status" placeholder="请选择设备状态" clearable>
            <t-option value="normal" label="正常运行" />
            <t-option value="maintenance" label="维护中" />
            <t-option value="fault" label="故障" />
            <t-option value="scrapped" label="已报废" />
          </t-select>
        </t-form-item>
        <t-form-item label="所属车间" name="workshop">
          <t-select v-model="searchForm.workshop" placeholder="请选择所属车间" clearable>
            <t-option value="A" label="A车间" />
            <t-option value="B" label="B车间" />
            <t-option value="C" label="C车间" />
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

    <!-- 设备列表 -->
    <t-card title="设备列表" class="table-card">
      <template #actions>
        <t-space>
          <t-button theme="primary" @click="handleAdd">
            <template #icon><add-icon /></template>
            新增设备
          </t-button>
          <t-button theme="success" @click="handleMaintenance">
            <template #icon><tool-icon /></template>
            批量维护
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
          <t-tag v-if="row.status === 'normal'" theme="success" variant="light">正常运行</t-tag>
          <t-tag v-else-if="row.status === 'maintenance'" theme="warning" variant="light">维护中</t-tag>
          <t-tag v-else-if="row.status === 'fault'" theme="error" variant="light">故障</t-tag>
          <t-tag v-else theme="default" variant="light">已报废</t-tag>
        </template>
        <template #equipmentType="{ row }">
          <t-tag v-if="row.equipmentType === 'production'" theme="primary" variant="light">生产设备</t-tag>
          <t-tag v-else-if="row.equipmentType === 'testing'" theme="success" variant="light">检测设备</t-tag>
          <t-tag v-else-if="row.equipmentType === 'transport'" theme="warning" variant="light">运输设备</t-tag>
          <t-tag v-else theme="default" variant="light">其他设备</t-tag>
        </template>
        <template #action="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleView(row)">查看</t-link>
            <t-link theme="primary" @click="handleEdit(row)">编辑</t-link>
            <t-link theme="warning" @click="handleMaintenanceRecord(row)">维护记录</t-link>
            <t-link theme="danger" @click="handleScrap(row)">报废</t-link>
          </t-space>
        </template>
      </t-table>
    </t-card>

    <!-- 新增/编辑设备弹窗 -->
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
            <t-form-item label="设备编号" name="equipmentCode">
              <t-input v-model="formData.equipmentCode" placeholder="请输入设备编号" />
            </t-form-item>
            <t-form-item label="设备名称" name="equipmentName">
              <t-input v-model="formData.equipmentName" placeholder="请输入设备名称" />
            </t-form-item>
            <t-form-item label="设备类型" name="equipmentType">
              <t-select v-model="formData.equipmentType" placeholder="请选择设备类型">
                <t-option value="production" label="生产设备" />
                <t-option value="testing" label="检测设备" />
                <t-option value="transport" label="运输设备" />
                <t-option value="other" label="其他设备" />
              </t-select>
            </t-form-item>
            <t-form-item label="规格型号" name="specification">
              <t-input v-model="formData.specification" placeholder="请输入规格型号" />
            </t-form-item>
            <t-form-item label="所属车间" name="workshop">
              <t-select v-model="formData.workshop" placeholder="请选择所属车间">
                <t-option value="A" label="A车间" />
                <t-option value="B" label="B车间" />
                <t-option value="C" label="C车间" />
              </t-select>
            </t-form-item>
            <t-form-item label="设备状态" name="status">
              <t-select v-model="formData.status" placeholder="请选择设备状态">
                <t-option value="normal" label="正常运行" />
                <t-option value="maintenance" label="维护中" />
                <t-option value="fault" label="故障" />
                <t-option value="scrapped" label="已报废" />
              </t-select>
            </t-form-item>
            <t-form-item label="购买日期" name="purchaseDate">
              <t-date-picker v-model="formData.purchaseDate" placeholder="请选择购买日期" />
            </t-form-item>
            <t-form-item label="购买价格" name="purchasePrice">
              <t-input-number v-model="formData.purchasePrice" :min="0" placeholder="请输入购买价格" />
            </t-form-item>
            <t-form-item label="折旧年限" name="depreciationYears">
              <t-input-number v-model="formData.depreciationYears" :min="1" placeholder="请输入折旧年限" />
            </t-form-item>
            <t-form-item label="负责人" name="personInCharge">
              <t-select v-model="formData.personInCharge" placeholder="请选择负责人" filterable>
                <t-option value="1" label="张三" />
                <t-option value="2" label="李四" />
                <t-option value="3" label="王五" />
              </t-select>
            </t-form-item>
            <t-form-item label="备注" name="remark">
              <t-textarea v-model="formData.remark" placeholder="请输入备注" :maxlength="500" />
            </t-form-item>
          </t-form>
        </t-tab-panel>
        <t-tab-panel value="maintenance" label="维护信息">
          <t-form :data="formData" label-width="120px">
            <t-form-item label="上次维护日期">
              <t-date-picker v-model="formData.lastMaintenanceDate" placeholder="请选择上次维护日期" />
            </t-form-item>
            <t-form-item label="下次维护日期">
              <t-date-picker v-model="formData.nextMaintenanceDate" placeholder="请选择下次维护日期" />
            </t-form-item>
            <t-form-item label="维护周期(月)">
              <t-input-number v-model="formData.maintenanceCycle" :min="1" placeholder="请输入维护周期" />
            </t-form-item>
            <t-form-item label="维护次数">
              <t-input-number v-model="formData.maintenanceCount" :min="0" :disabled="true" />
            </t-form-item>
            <t-form-item label="累计维护费用">
              <t-input-number v-model="formData.totalMaintenanceCost" :min="0" :disabled="true" />
            </t-form-item>
          </t-form>
        </t-tab-panel>
        <t-tab-panel value="summary" label="汇总信息">
          <t-descriptions :column="2" bordered>
            <t-descriptions-item label="设备编号">{{ formData.equipmentCode }}</t-descriptions-item>
            <t-descriptions-item label="设备名称">{{ formData.equipmentName }}</t-descriptions-item>
            <t-descriptions-item label="设备类型">
              <t-tag v-if="formData.equipmentType === 'production'" theme="primary" variant="light">生产设备</t-tag>
              <t-tag v-else-if="formData.equipmentType === 'testing'" theme="success" variant="light">检测设备</t-tag>
              <t-tag v-else-if="formData.equipmentType === 'transport'" theme="warning" variant="light">运输设备</t-tag>
              <t-tag v-else theme="default" variant="light">其他设备</t-tag>
            </t-descriptions-item>
            <t-descriptions-item label="规格型号">{{ formData.specification }}</t-descriptions-item>
            <t-descriptions-item label="所属车间">{{ formData.workshop }}车间</t-descriptions-item>
            <t-descriptions-item label="设备状态">
              <t-tag v-if="formData.status === 'normal'" theme="success" variant="light">正常运行</t-tag>
              <t-tag v-else-if="formData.status === 'maintenance'" theme="warning" variant="light">维护中</t-tag>
              <t-tag v-else-if="formData.status === 'fault'" theme="error" variant="light">故障</t-tag>
              <t-tag v-else theme="default" variant="light">已报废</t-tag>
            </t-descriptions-item>
            <t-descriptions-item label="购买日期">{{ formData.purchaseDate }}</t-descriptions-item>
            <t-descriptions-item label="购买价格">¥{{ formData.purchasePrice }}</t-descriptions-item>
            <t-descriptions-item label="折旧年限">{{ formData.depreciationYears }}年</t-descriptions-item>
            <t-descriptions-item label="负责人">{{ formData.personInCharge }}</t-descriptions-item>
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

    <!-- 设备详情弹窗 -->
    <t-dialog
      v-model:visible="detailVisible"
      header="设备详情"
      :width="1000"
      :footer="false"
    >
      <t-tabs v-model="detailTab" theme="card">
        <t-tab-panel value="basic" label="基本信息">
          <t-descriptions :column="2" bordered>
            <t-descriptions-item label="设备编号">{{ detailData.equipmentCode }}</t-descriptions-item>
            <t-descriptions-item label="设备名称">{{ detailData.equipmentName }}</t-descriptions-item>
            <t-descriptions-item label="设备类型">
              <t-tag v-if="detailData.equipmentType === 'production'" theme="primary" variant="light">生产设备</t-tag>
              <t-tag v-else-if="detailData.equipmentType === 'testing'" theme="success" variant="light">检测设备</t-tag>
              <t-tag v-else-if="detailData.equipmentType === 'transport'" theme="warning" variant="light">运输设备</t-tag>
              <t-tag v-else theme="default" variant="light">其他设备</t-tag>
            </t-descriptions-item>
            <t-descriptions-item label="规格型号">{{ detailData.specification }}</t-descriptions-item>
            <t-descriptions-item label="所属车间">{{ detailData.workshop }}车间</t-descriptions-item>
            <t-descriptions-item label="设备状态">
              <t-tag v-if="detailData.status === 'normal'" theme="success" variant="light">正常运行</t-tag>
              <t-tag v-else-if="detailData.status === 'maintenance'" theme="warning" variant="light">维护中</t-tag>
              <t-tag v-else-if="detailData.status === 'fault'" theme="error" variant="light">故障</t-tag>
              <t-tag v-else theme="default" variant="light">已报废</t-tag>
            </t-descriptions-item>
            <t-descriptions-item label="购买日期">{{ detailData.purchaseDate }}</t-descriptions-item>
            <t-descriptions-item label="购买价格">¥{{ detailData.purchasePrice }}</t-descriptions-item>
            <t-descriptions-item label="折旧年限">{{ detailData.depreciationYears }}年</t-descriptions-item>
            <t-descriptions-item label="负责人">{{ detailData.personInCharge }}</t-descriptions-item>
            <t-descriptions-item label="备注" :span="2">{{ detailData.remark }}</t-descriptions-item>
          </t-descriptions>
        </t-tab-panel>
        <t-tab-panel value="maintenance" label="维护记录">
          <t-table
            :data="detailData.maintenanceRecords || []"
            :columns="maintenanceColumns"
            :pagination="false"
            row-key="id"
          >
            <template #status="{ row }">
              <t-tag v-if="row.status === 'completed'" theme="success" variant="light">已完成</t-tag>
              <t-tag v-else-if="row.status === 'pending'" theme="warning" variant="light">进行中</t-tag>
              <t-tag v-else theme="default" variant="light">待处理</t-tag>
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

    <!-- 维护记录弹窗 -->
    <t-dialog
      v-model:visible="maintenanceRecordVisible"
      header="设备维护记录"
      :width="1200"
      :footer="false"
    >
      <t-space direction="vertical" style="width: 100%">
        <t-button theme="primary" @click="handleAddMaintenance">
          <template #icon><add-icon /></template>
          新增维护
        </t-button>
        <t-table
          :data="maintenanceRecordData"
          :columns="maintenanceRecordColumns"
          :pagination="maintenancePagination"
          row-key="id"
          @page-change="handleMaintenancePageChange"
        >
          <template #status="{ row }">
            <t-tag v-if="row.status === 'completed'" theme="success" variant="light">已完成</t-tag>
            <t-tag v-else-if="row.status === 'pending'" theme="warning" variant="light">进行中</t-tag>
            <t-tag v-else theme="default" variant="light">待处理</t-tag>
          </template>
          <template #action="{ row }">
            <t-space>
              <t-link theme="primary" @click="handleViewMaintenance(row)">查看</t-link>
              <t-link v-if="row.status === 'pending'" theme="success" @click="handleCompleteMaintenance(row)">完成</t-link>
            </t-space>
          </template>
        </t-table>
      </t-space>
    </t-dialog>

    <!-- 新增维护弹窗 -->
    <t-dialog
      v-model:visible="addMaintenanceVisible"
      header="新增维护"
      :width="600"
      :confirm-btn="null"
      :cancel-btn="null"
    >
      <t-form :data="maintenanceForm" ref="maintenanceFormRef" :rules="maintenanceRules" label-width="120px">
        <t-form-item label="维护日期" name="maintenanceDate">
          <t-date-picker v-model="maintenanceForm.maintenanceDate" placeholder="请选择维护日期" />
        </t-form-item>
        <t-form-item label="维护类型" name="maintenanceType">
          <t-select v-model="maintenanceForm.maintenanceType" placeholder="请选择维护类型">
            <t-option value="regular" label="定期维护" />
            <t-option value="preventive" label="预防性维护" />
            <t-option value="corrective" label="故障修复" />
            <t-option value="upgrade" label="升级改造" />
          </t-select>
        </t-form-item>
        <t-form-item label="维护内容" name="maintenanceContent">
          <t-textarea v-model="maintenanceForm.maintenanceContent" placeholder="请输入维护内容" :maxlength="500" />
        </t-form-item>
        <t-form-item label="维护费用" name="maintenanceCost">
          <t-input-number v-model="maintenanceForm.maintenanceCost" :min="0" placeholder="请输入维护费用" />
        </t-form-item>
        <t-form-item label="维护人员" name="maintenancePerson">
          <t-select v-model="maintenanceForm.maintenancePerson" placeholder="请选择维护人员" filterable>
            <t-option value="1" label="张三" />
            <t-option value="2" label="李四" />
            <t-option value="3" label="王五" />
          </t-select>
        </t-form-item>
        <t-form-item label="备注" name="remark">
          <t-textarea v-model="maintenanceForm.remark" placeholder="请输入备注" :maxlength="500" />
        </t-form-item>
      </t-form>
      <template #footer>
        <t-space>
          <t-button theme="default" @click="addMaintenanceVisible = false">取消</t-button>
          <t-button theme="primary" @click="handleSubmitMaintenance">确定</t-button>
        </t-space>
      </template>
    </t-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { MessagePlugin, DialogPlugin } from 'tdesign-vue-next'
import { AddIcon, ToolIcon, DownloadIcon } from 'tdesign-icons-vue-next'

// 统计数据
const statistics = ref({
  total: 150,
  normal: 120,
  maintenance: 20,
  fault: 10
})

// 搜索表单
const searchForm = reactive({
  equipmentCode: '',
  equipmentName: '',
  equipmentType: '',
  status: '',
  workshop: ''
})

// 表格数据
const loading = ref(false)
const tableData = ref([
  {
    id: 1,
    equipmentCode: 'EQ-001',
    equipmentName: '数控机床A1',
    equipmentType: 'production',
    specification: 'CNC-2000',
    workshop: 'A',
    status: 'normal',
    purchaseDate: '2020-01-15',
    purchasePrice: 500000,
    depreciationYears: 10,
    personInCharge: '1',
    remark: '主要生产设备'
  },
  {
    id: 2,
    equipmentCode: 'EQ-002',
    equipmentName: '检测仪器B1',
    equipmentType: 'testing',
    specification: 'TEST-500',
    workshop: 'B',
    status: 'maintenance',
    purchaseDate: '2020-03-20',
    purchasePrice: 100000,
    depreciationYears: 8,
    personInCharge: '2',
    remark: '质量检测设备'
  },
  {
    id: 3,
    equipmentCode: 'EQ-003',
    equipmentName: '叉车C1',
    equipmentType: 'transport',
    specification: 'FORK-200',
    workshop: 'C',
    status: 'fault',
    purchaseDate: '2019-05-10',
    purchasePrice: 80000,
    depreciationYears: 5,
    personInCharge: '3',
    remark: '物流运输设备'
  }
])

// 表格列定义
const columns = [
  { colKey: 'equipmentCode', title: '设备编号', width: 120 },
  { colKey: 'equipmentName', title: '设备名称', width: 150 },
  { colKey: 'equipmentType', title: '设备类型', width: 120, cell: 'equipmentType' },
  { colKey: 'specification', title: '规格型号', width: 120 },
  { colKey: 'workshop', title: '所属车间', width: 100 },
  { colKey: 'status', title: '设备状态', width: 100, cell: 'status' },
  { colKey: 'purchaseDate', title: '购买日期', width: 120 },
  { colKey: 'purchasePrice', title: '购买价格', width: 120 },
  { colKey: 'personInCharge', title: '负责人', width: 100 },
  { colKey: 'action', title: '操作', width: 200, fixed: 'right', cell: 'action' }
]

// 分页
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 150
})

const rowKey = 'id'
const selectedRowKeys = ref<number[]>([])

// 新增/编辑弹窗
const dialogVisible = ref(false)
const dialogTitle = computed(() => (formData.id ? '编辑设备' : '新增设备'))
const activeTab = ref('basic')
const formRef = ref()
const formData = reactive({
  id: undefined,
  equipmentCode: '',
  equipmentName: '',
  equipmentType: '',
  specification: '',
  workshop: '',
  status: 'normal',
  purchaseDate: '',
  purchasePrice: 0,
  depreciationYears: 10,
  personInCharge: '',
  remark: '',
  lastMaintenanceDate: '',
  nextMaintenanceDate: '',
  maintenanceCycle: 6,
  maintenanceCount: 0,
  totalMaintenanceCost: 0
})

const rules = {
  equipmentCode: [{ required: true, message: '请输入设备编号' }],
  equipmentName: [{ required: true, message: '请输入设备名称' }],
  equipmentType: [{ required: true, message: '请选择设备类型' }],
  specification: [{ required: true, message: '请输入规格型号' }],
  workshop: [{ required: true, message: '请选择所属车间' }],
  purchaseDate: [{ required: true, message: '请选择购买日期' }],
  purchasePrice: [{ required: true, message: '请输入购买价格' }],
  depreciationYears: [{ required: true, message: '请输入折旧年限' }]
}

// 设备详情弹窗
const detailVisible = ref(false)
const detailTab = ref('basic')
const detailData = ref<any>({})

const maintenanceColumns = [
  { colKey: 'maintenanceDate', title: '维护日期', width: 120 },
  { colKey: 'maintenanceType', title: '维护类型', width: 120 },
  { colKey: 'maintenanceContent', title: '维护内容', width: 200 },
  { colKey: 'maintenanceCost', title: '维护费用', width: 100 },
  { colKey: 'maintenancePerson', title: '维护人员', width: 100 },
  { colKey: 'status', title: '状态', width: 100, cell: 'status' }
]

// 维护记录弹窗
const maintenanceRecordVisible = ref(false)
const maintenanceRecordData = ref([
  {
    id: 1,
    maintenanceDate: '2026-01-15',
    maintenanceType: 'regular',
    maintenanceContent: '定期检查润滑系统',
    maintenanceCost: 500,
    maintenancePerson: '张三',
    status: 'completed'
  },
  {
    id: 2,
    maintenanceDate: '2026-02-10',
    maintenanceType: 'preventive',
    maintenanceContent: '预防性维护更换零件',
    maintenanceCost: 2000,
    maintenancePerson: '李四',
    status: 'pending'
  }
])

const maintenanceRecordColumns = [
  { colKey: 'maintenanceDate', title: '维护日期', width: 120 },
  { colKey: 'maintenanceType', title: '维护类型', width: 120 },
  { colKey: 'maintenanceContent', title: '维护内容', width: 200 },
  { colKey: 'maintenanceCost', title: '维护费用', width: 100 },
  { colKey: 'maintenancePerson', title: '维护人员', width: 100 },
  { colKey: 'status', title: '状态', width: 100, cell: 'status' },
  { colKey: 'action', title: '操作', width: 150, cell: 'action' }
]

const maintenancePagination = reactive({
  current: 1,
  pageSize: 10,
  total: 2
})

// 新增维护弹窗
const addMaintenanceVisible = ref(false)
const maintenanceFormRef = ref()
const maintenanceForm = reactive({
  maintenanceDate: '',
  maintenanceType: '',
  maintenanceContent: '',
  maintenanceCost: 0,
  maintenancePerson: '',
  remark: ''
})

const maintenanceRules = {
  maintenanceDate: [{ required: true, message: '请选择维护日期' }],
  maintenanceType: [{ required: true, message: '请选择维护类型' }],
  maintenanceContent: [{ required: true, message: '请输入维护内容' }],
  maintenanceCost: [{ required: true, message: '请输入维护费用' }],
  maintenancePerson: [{ required: true, message: '请选择维护人员' }]
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
    equipmentCode: '',
    equipmentName: '',
    equipmentType: '',
    status: '',
    workshop: ''
  })
  MessagePlugin.success('已重置')
}

// 新增设备
const handleAdd = () => {
  Object.assign(formData, {
    id: undefined,
    equipmentCode: '',
    equipmentName: '',
    equipmentType: '',
    specification: '',
    workshop: '',
    status: 'normal',
    purchaseDate: '',
    purchasePrice: 0,
    depreciationYears: 10,
    personInCharge: '',
    remark: '',
    lastMaintenanceDate: '',
    nextMaintenanceDate: '',
    maintenanceCycle: 6,
    maintenanceCount: 0,
    totalMaintenanceCost: 0
  })
  activeTab.value = 'basic'
  dialogVisible.value = true
}

// 编辑设备
const handleEdit = (row: any) => {
  Object.assign(formData, { ...row })
  activeTab.value = 'basic'
  dialogVisible.value = true
}

// 查看设备详情
const handleView = (row: any) => {
  detailData.value = {
    ...row,
    maintenanceRecords: [
      {
        id: 1,
        maintenanceDate: '2026-01-15',
        maintenanceType: '定期维护',
        maintenanceContent: '定期检查润滑系统',
        maintenanceCost: 500,
        maintenancePerson: '张三',
        status: 'completed'
      }
    ],
    logs: [
      { id: 1, time: '2026-02-19 10:00', content: '更新设备信息', operator: '管理员' },
      { id: 2, time: '2026-02-10 14:30', content: '完成定期维护', operator: '张三' },
      { id: 3, time: '2026-01-15 09:00', content: '新增设备', operator: '管理员' }
    ]
  }
  detailTab.value = 'basic'
  detailVisible.value = true
}

// 维护记录
const handleMaintenanceRecord = (row: any) => {
  currentEquipment.value = row
  maintenanceRecordVisible.value = true
}

const currentEquipment = ref<any>({})

// 报废设备
const handleScrap = (row: any) => {
  const dialog = DialogPlugin({
    header: '确认报废',
    body: `确认要报废设备"${row.equipmentName}"吗？`,
    confirmBtn: '确定',
    cancelBtn: '取消',
    onConfirm: () => {
      dialog.hide()
      MessagePlugin.success('设备已报废')
    }
  })
}

// 批量维护
const handleMaintenance = () => {
  if (selectedRowKeys.value.length === 0) {
    MessagePlugin.warning('请先选择设备')
    return
  }
  MessagePlugin.success(`已发起${selectedRowKeys.value.length}个设备的维护流程`)
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

// 新增维护
const handleAddMaintenance = () => {
  Object.assign(maintenanceForm, {
    maintenanceDate: '',
    maintenanceType: '',
    maintenanceContent: '',
    maintenanceCost: 0,
    maintenancePerson: '',
    remark: ''
  })
  addMaintenanceVisible.value = true
}

// 提交维护
const handleSubmitMaintenance = async () => {
  const valid = await maintenanceFormRef.value?.validate()
  if (valid === true) {
    addMaintenanceVisible.value = false
    MessagePlugin.success('维护记录已添加')
  }
}

// 完成维护
const handleCompleteMaintenance = (row: any) => {
  MessagePlugin.success('维护已完成')
}

// 维护记录分页
const handleMaintenancePageChange = (pageInfo: any) => {
  maintenancePagination.current = pageInfo.current
  maintenancePagination.pageSize = pageInfo.pageSize
}
</script>

<style scoped lang="less">
.equipment-management {
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

    .search-form {
      display: flex;
      justify-content: flex-end;
    }
  }

  .log-operator {
    font-size: 12px;
    color: rgba(0, 0, 0, 0.6);
    margin-top: 4px;
  }
}
</style>
