<template>
  <div class="social-insurance-container">
    <!-- 统计卡片 -->
    <t-row :gutter="16" class="statistics-cards">
      <t-col :span="6">
        <t-card>
          <t-statistic title="本月社保缴纳人数" :value="stats.insuredCount" />
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card>
          <t-statistic title="本月社保总额(元)" :value="stats.insuranceTotal" :precision="2" />
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card>
          <t-statistic title="本月公积金缴纳人数" :value="stats.fundCount" />
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card>
          <t-statistic title="本月公积金总额(元)" :value="stats.fundTotal" :precision="2" />
        </t-card>
      </t-col>
    </t-row>

    <!-- 搜索卡片 -->
    <t-card class="search-card" title="查询条件">
      <t-form :data="searchForm" layout="inline" @submit="handleSearch" @reset="handleReset">
        <t-form-item label="姓名" name="name">
          <t-input v-model="searchForm.name" placeholder="请输入员工姓名" clearable />
        </t-form-item>
        <t-form-item label="工号" name="employeeNo">
          <t-input v-model="searchForm.employeeNo" placeholder="请输入工号" clearable />
        </t-form-item>
        <t-form-item label="部门" name="deptId">
          <t-select v-model="searchForm.deptId" placeholder="请选择部门" clearable>
            <t-option v-for="dept in deptList" :key="dept.id" :value="dept.id" :label="dept.name" />
          </t-select>
        </t-form-item>
        <t-form-item label="缴纳类型" name="type">
          <t-select v-model="searchForm.type" placeholder="请选择类型" clearable>
            <t-option value="insurance" label="社保" />
            <t-option value="fund" label="公积金" />
          </t-select>
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
    <t-card class="operation-card">
      <t-space>
        <t-button theme="primary" @click="handleAdd">
          <template #icon><add-icon /></template>
          新增缴纳
        </t-button>
        <t-button theme="default" @click="handleBatchDelete">
          <template #icon><delete-icon /></template>
          批量删除
        </t-button>
        <t-button theme="default" @click="handleCalculate">
          <template #icon><calculate-icon /></template>
          计算本月
        </t-button>
        <t-button theme="default" @click="handleExport">
          <template #icon><download-icon /></template>
          导出报表
        </t-button>
      </t-space>
    </t-card>

    <!-- 社保公积金列表 -->
    <t-card class="table-card">
      <t-table
        :data="tableData"
        :columns="columns"
        :row-key="'id'"
        :selected-row-keys="selectedRowKeys"
        :loading="loading"
        :pagination="pagination"
        @select-change="handleSelectChange"
        @page-change="handlePageChange"
      >
        <template #status="{ row }">
          <t-tag v-if="row.status === 1" theme="success">正常缴纳</t-tag>
          <t-tag v-else-if="row.status === 2" theme="warning">暂停缴纳</t-tag>
          <t-tag v-else theme="danger">已离职</t-tag>
        </template>
        <template #operation="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleView(row)">查看</t-link>
            <t-link theme="primary" @click="handleEdit(row)">编辑</t-link>
            <t-link theme="danger" @click="handleDelete(row)">删除</t-link>
          </t-space>
        </template>
      </t-table>
    </t-card>

    <!-- 新增/编辑弹窗 -->
    <t-dialog
      v-model:visible="dialogVisible"
      :header="dialogTitle"
      :confirm-btn="null"
      :cancel-btn="null"
      width="900px"
    >
      <t-form
        ref="formRef"
        :data="formData"
        :rules="rules"
        label-width="120px"
        @submit="handleSubmit"
      >
        <t-tabs v-model="activeTab">
          <t-tab-panel value="basic" label="基本信息">
            <t-form-item label="员工" name="employeeId">
              <t-select v-model="formData.employeeId" placeholder="请选择员工" filterable clearable>
                <t-option v-for="emp in employeeList" :key="emp.id" :value="emp.id" :label="`${emp.name} (${emp.employeeNo})`" />
              </t-select>
            </t-form-item>
            <t-form-item label="缴纳类型" name="type">
              <t-radio-group v-model="formData.type">
                <t-radio value="insurance">社保</t-radio>
                <t-radio value="fund">公积金</t-radio>
              </t-radio-group>
            </t-form-item>
            <t-form-item label="缴纳状态" name="status">
              <t-radio-group v-model="formData.status">
                <t-radio :value="1">正常缴纳</t-radio>
                <t-radio :value="2">暂停缴纳</t-radio>
              </t-radio-group>
            </t-form-item>
            <t-form-item label="缴纳城市" name="city">
              <t-cascader v-model="formData.city" :options="cityOptions" placeholder="请选择城市" clearable />
            </t-form-item>
          </t-tab-panel>
          <t-tab-panel value="insurance" label="社保配置">
            <t-form-item label="养老缴费基数" name="pensionBase">
              <t-input-number v-model="formData.pensionBase" placeholder="请输入养老缴费基数" :min="0" />
            </t-form-item>
            <t-form-item label="养老单位比例(%)" name="pensionCompanyRate">
              <t-input-number v-model="formData.pensionCompanyRate" placeholder="请输入单位比例" :min="0" :max="100" />
            </t-form-item>
            <t-form-item label="养老个人比例(%)" name="pensionPersonalRate">
              <t-input-number v-model="formData.pensionPersonalRate" placeholder="请输入个人比例" :min="0" :max="100" />
            </t-form-item>
            <t-divider />
            <t-form-item label="医疗缴费基数" name="medicalBase">
              <t-input-number v-model="formData.medicalBase" placeholder="请输入医疗缴费基数" :min="0" />
            </t-form-item>
            <t-form-item label="医疗单位比例(%)" name="medicalCompanyRate">
              <t-input-number v-model="formData.medicalCompanyRate" placeholder="请输入单位比例" :min="0" :max="100" />
            </t-form-item>
            <t-form-item label="医疗个人比例(%)" name="medicalPersonalRate">
              <t-input-number v-model="formData.medicalPersonalRate" placeholder="请输入个人比例" :min="0" :max="100" />
            </t-form-item>
            <t-divider />
            <t-form-item label="失业缴费基数" name="unemploymentBase">
              <t-input-number v-model="formData.unemploymentBase" placeholder="请输入失业缴费基数" :min="0" />
            </t-form-item>
            <t-form-item label="失业单位比例(%)" name="unemploymentCompanyRate">
              <t-input-number v-model="formData.unemploymentCompanyRate" placeholder="请输入单位比例" :min="0" :max="100" />
            </t-form-item>
            <t-form-item label="失业个人比例(%)" name="unemploymentPersonalRate">
              <t-input-number v-model="formData.unemploymentPersonalRate" placeholder="请输入个人比例" :min="0" :max="100" />
            </t-form-item>
            <t-divider />
            <t-form-item label="生育缴费基数" name="maternityBase">
              <t-input-number v-model="formData.maternityBase" placeholder="请输入生育缴费基数" :min="0" />
            </t-form-item>
            <t-form-item label="生育单位比例(%)" name="maternityCompanyRate">
              <t-input-number v-model="formData.maternityCompanyRate" placeholder="请输入单位比例" :min="0" :max="100" />
            </t-form-item>
          </t-tab-panel>
          <t-tab-panel value="fund" label="公积金配置">
            <t-form-item label="公积金基数" name="fundBase">
              <t-input-number v-model="formData.fundBase" placeholder="请输入公积金基数" :min="0" />
            </t-form-item>
            <t-form-item label="单位比例(%)" name="fundCompanyRate">
              <t-input-number v-model="formData.fundCompanyRate" placeholder="请输入单位比例" :min="0" :max="100" />
            </t-form-item>
            <t-form-item label="个人比例(%)" name="fundPersonalRate">
              <t-input-number v-model="formData.fundPersonalRate" placeholder="请输入个人比例" :min="0" :max="100" />
            </t-form-item>
          </t-tab-panel>
        </t-tabs>
        <t-form-item style="margin-top: 20px;">
          <t-space>
            <t-button theme="primary" type="submit">确定</t-button>
            <t-button theme="default" @click="dialogVisible = false">取消</t-button>
          </t-space>
        </t-form-item>
      </t-form>
    </t-dialog>

    <!-- 详情弹窗 -->
    <t-dialog v-model:visible="detailVisible" header="缴纳详情" width="800px" :footer="false">
      <t-descriptions bordered :column="2">
        <t-descriptions-item label="员工姓名">{{ detailData.employeeName }}</t-descriptions-item>
        <t-descriptions-item label="工号">{{ detailData.employeeNo }}</t-descriptions-item>
        <t-descriptions-item label="部门">{{ detailData.deptName }}</t-descriptions-item>
        <t-descriptions-item label="缴纳类型">
          <t-tag v-if="detailData.type === 'insurance'" theme="primary">社保</t-tag>
          <t-tag v-else theme="success">公积金</t-tag>
        </t-descriptions-item>
        <t-descriptions-item label="缴纳城市">{{ detailData.cityName }}</t-descriptions-item>
        <t-descriptions-item label="缴纳状态">
          <t-tag v-if="detailData.status === 1" theme="success">正常缴纳</t-tag>
          <t-tag v-else theme="warning">暂停缴纳</t-tag>
        </t-descriptions-item>
      </t-descriptions>
      <t-divider>社保明细</t-divider>
      <t-descriptions bordered :column="3">
        <t-descriptions-item label="养老基数">{{ detailData.pensionBase }}</t-descriptions-item>
        <t-descriptions-item label="养老单位">{{ detailData.pensionCompanyAmount }}元</t-descriptions-item>
        <t-descriptions-item label="养老个人">{{ detailData.pensionPersonalAmount }}元</t-descriptions-item>
        <t-descriptions-item label="医疗基数">{{ detailData.medicalBase }}</t-descriptions-item>
        <t-descriptions-item label="医疗单位">{{ detailData.medicalCompanyAmount }}元</t-descriptions-item>
        <t-descriptions-item label="医疗个人">{{ detailData.medicalPersonalAmount }}元</t-descriptions-item>
        <t-descriptions-item label="失业基数">{{ detailData.unemploymentBase }}</t-descriptions-item>
        <t-descriptions-item label="失业单位">{{ detailData.unemploymentCompanyAmount }}元</t-descriptions-item>
        <t-descriptions-item label="失业个人">{{ detailData.unemploymentPersonalAmount }}元</t-descriptions-item>
        <t-descriptions-item label="生育基数">{{ detailData.maternityBase }}</t-descriptions-item>
        <t-descriptions-item label="生育单位">{{ detailData.maternityCompanyAmount }}元</t-descriptions-item>
        <t-descriptions-item label="生育个人">{{ detailData.maternityPersonalAmount }}元</t-descriptions-item>
      </t-descriptions>
      <t-divider>公积金明细</t-divider>
      <t-descriptions bordered :column="3">
        <t-descriptions-item label="公积金基数">{{ detailData.fundBase }}</t-descriptions-item>
        <t-descriptions-item label="单位部分">{{ detailData.fundCompanyAmount }}元</t-descriptions-item>
        <t-descriptions-item label="个人部分">{{ detailData.fundPersonalAmount }}元</t-descriptions-item>
      </t-descriptions>
      <t-divider>汇总</t-descriptions>
      <t-descriptions bordered :column="2">
        <t-descriptions-item label="社保单位合计">{{ detailData.insuranceCompanyTotal }}元</t-descriptions-item>
        <t-descriptions-item label="社保个人合计">{{ detailData.insurancePersonalTotal }}元</t-descriptions-item>
        <t-descriptions-item label="公积金单位合计">{{ detailData.fundCompanyAmount }}元</t-descriptions-item>
        <t-descriptions-item label="公积金个人合计">{{ detailData.fundPersonalAmount }}元</t-descriptions-item>
        <t-descriptions-item label="单位承担总额">{{ detailData.companyTotal }}元</t-descriptions-item>
        <t-descriptions-item label="个人承担总额">{{ detailData.personalTotal }}元</t-descriptions-item>
      </t-descriptions>
    </t-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { MessagePlugin, DialogPlugin } from 'tdesign-vue-next';
import { AddIcon, DeleteIcon, DownloadIcon, CalculateIcon } from 'tdesign-icons-vue-next';

// 统计数据
const stats = ref({
  insuredCount: 156,
  insuranceTotal: 358000.00,
  fundCount: 142,
  fundTotal: 142000.00
});

// 搜索表单
const searchForm = reactive({
  name: '',
  employeeNo: '',
  deptId: undefined,
  type: undefined
});

// 部门列表
const deptList = ref([
  { id: 1, name: '研发部' },
  { id: 2, name: '市场部' },
  { id: 3, name: '销售部' },
  { id: 4, name: '财务部' },
  { id: 5, name: '人事部' }
]);

// 表格数据
const loading = ref(false);
const tableData = ref([
  {
    id: 1,
    employeeId: 1,
    employeeNo: 'EMP001',
    employeeName: '张三',
    deptName: '研发部',
    type: 'insurance',
    status: 1,
    city: ['北京市', '北京市'],
    cityName: '北京市',
    pensionBase: 25000,
    pensionCompanyRate: 16,
    pensionPersonalRate: 8,
    pensionCompanyAmount: 4000,
    pensionPersonalAmount: 2000,
    medicalBase: 25000,
    medicalCompanyRate: 10,
    medicalPersonalRate: 2,
    medicalCompanyAmount: 2500,
    medicalPersonalAmount: 500,
    unemploymentBase: 25000,
    unemploymentCompanyRate: 0.7,
    unemploymentPersonalRate: 0.3,
    unemploymentCompanyAmount: 175,
    unemploymentPersonalAmount: 75,
    maternityBase: 25000,
    maternityCompanyRate: 0.8,
    maternityCompanyAmount: 200,
    maternityPersonalAmount: 0,
    fundBase: 25000,
    fundCompanyRate: 12,
    fundPersonalRate: 12,
    fundCompanyAmount: 3000,
    fundPersonalAmount: 3000,
    insuranceCompanyTotal: 6875,
    insurancePersonalTotal: 2575,
    companyTotal: 9875,
    personalTotal: 5575
  },
  {
    id: 2,
    employeeId: 2,
    employeeNo: 'EMP002',
    employeeName: '李四',
    deptName: '市场部',
    type: 'insurance',
    status: 1,
    city: ['上海市', '上海市'],
    cityName: '上海市',
    pensionBase: 20000,
    pensionCompanyRate: 16,
    pensionPersonalRate: 8,
    pensionCompanyAmount: 3200,
    pensionPersonalAmount: 1600,
    medicalBase: 20000,
    medicalCompanyRate: 10,
    medicalPersonalRate: 2,
    medicalCompanyAmount: 2000,
    medicalPersonalAmount: 400,
    unemploymentBase: 20000,
    unemploymentCompanyRate: 0.7,
    unemploymentPersonalRate: 0.3,
    unemploymentCompanyAmount: 140,
    unemploymentPersonalAmount: 60,
    maternityBase: 20000,
    maternityCompanyRate: 0.8,
    maternityCompanyAmount: 160,
    maternityPersonalAmount: 0,
    fundBase: 20000,
    fundCompanyRate: 7,
    fundPersonalRate: 7,
    fundCompanyAmount: 1400,
    fundPersonalAmount: 1400,
    insuranceCompanyTotal: 5500,
    insurancePersonalTotal: 2060,
    companyTotal: 6900,
    personalTotal: 3460
  }
]);

const selectedRowKeys = ref<number[]>([]);

// 表格列
const columns = [
  { colKey: 'employeeNo', title: '工号', width: 120 },
  { colKey: 'employeeName', title: '姓名', width: 100 },
  { colKey: 'deptName', title: '部门', width: 120 },
  { colKey: 'type', title: '缴纳类型', width: 100, cell: (h, { row }) => {
    return h('t-tag', { theme: row.type === 'insurance' ? 'primary' : 'success' }, row.type === 'insurance' ? '社保' : '公积金');
  }},
  { colKey: 'companyTotal', title: '单位承担(元)', width: 120, cell: (h, { row }) => row.companyTotal?.toFixed(2) },
  { colKey: 'personalTotal', title: '个人承担(元)', width: 120, cell: (h, { row }) => row.personalTotal?.toFixed(2) },
  { colKey: 'cityName', title: '缴纳城市', width: 120 },
  { colKey: 'status', title: '状态', width: 100 },
  { colKey: 'operation', title: '操作', width: 180, fixed: 'right' }
];

// 分页
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 156
});

// 弹窗
const dialogVisible = ref(false);
const dialogTitle = ref('');
const detailVisible = ref(false);
const activeTab = ref('basic');

// 表单
const formRef = ref();
const formData = reactive({
  id: undefined,
  employeeId: undefined,
  type: 'insurance',
  status: 1,
  city: [],
  pensionBase: undefined,
  pensionCompanyRate: 16,
  pensionPersonalRate: 8,
  medicalBase: undefined,
  medicalCompanyRate: 10,
  medicalPersonalRate: 2,
  unemploymentBase: undefined,
  unemploymentCompanyRate: 0.7,
  unemploymentPersonalRate: 0.3,
  maternityBase: undefined,
  maternityCompanyRate: 0.8,
  fundBase: undefined,
  fundCompanyRate: 12,
  fundPersonalRate: 12
});

const employeeList = ref([
  { id: 1, name: '张三', employeeNo: 'EMP001' },
  { id: 2, name: '李四', employeeNo: 'EMP002' },
  { id: 3, name: '王五', employeeNo: 'EMP003' }
]);

const cityOptions = ref([
  {
    value: '北京市',
    label: '北京市',
    children: [{ value: '北京市', label: '北京市' }]
  },
  {
    value: '上海市',
    label: '上海市',
    children: [{ value: '上海市', label: '上海市' }]
  },
  {
    value: '广东省',
    label: '广东省',
    children: [
      { value: '广州市', label: '广州市' },
      { value: '深圳市', label: '深圳市' }
    ]
  }
]);

// 详情数据
const detailData = ref({});

// 表单验证规则
const rules = {
  employeeId: [{ required: true, message: '请选择员工', type: 'error' }],
  type: [{ required: true, message: '请选择缴纳类型', type: 'error' }],
  status: [{ required: true, message: '请选择缴纳状态', type: 'error' }],
  city: [{ required: true, message: '请选择缴纳城市', type: 'error' }]
};

// 搜索
const handleSearch = () => {
  MessagePlugin.success('查询成功');
  loadData();
};

const handleReset = () => {
  Object.assign(searchForm, {
    name: '',
    employeeNo: '',
    deptId: undefined,
    type: undefined
  });
  loadData();
};

// 表格选择
const handleSelectChange = (value: number[]) => {
  selectedRowKeys.value = value;
};

// 分页
const handlePageChange = (pageInfo: any) => {
  pagination.current = pageInfo.current;
  pagination.pageSize = pageInfo.pageSize;
  loadData();
};

// 新增
const handleAdd = () => {
  dialogTitle.value = '新增缴纳';
  activeTab.value = 'basic';
  Object.assign(formData, {
    id: undefined,
    employeeId: undefined,
    type: 'insurance',
    status: 1,
    city: [],
    pensionBase: undefined,
    pensionCompanyRate: 16,
    pensionPersonalRate: 8,
    medicalBase: undefined,
    medicalCompanyRate: 10,
    medicalPersonalRate: 2,
    unemploymentBase: undefined,
    unemploymentCompanyRate: 0.7,
    unemploymentPersonalRate: 0.3,
    maternityBase: undefined,
    maternityCompanyRate: 0.8,
    fundBase: undefined,
    fundCompanyRate: 12,
    fundPersonalRate: 12
  });
  dialogVisible.value = true;
};

// 编辑
const handleEdit = (row: any) => {
  dialogTitle.value = '编辑缴纳';
  activeTab.value = 'basic';
  Object.assign(formData, row);
  dialogVisible.value = true;
};

// 查看
const handleView = (row: any) => {
  detailData.value = { ...row };
  detailVisible.value = true;
};

// 删除
const handleDelete = async (row: any) => {
  const dialog = await DialogPlugin.confirm({
    header: '确认删除',
    body: `确定要删除员工 ${row.employeeName} 的缴纳记录吗？`,
    confirmBtn: '确定',
    cancelBtn: '取消'
  });
  if (dialog) {
    MessagePlugin.success('删除成功');
    loadData();
  }
};

// 批量删除
const handleBatchDelete = async () => {
  if (selectedRowKeys.value.length === 0) {
    MessagePlugin.warning('请先选择要删除的记录');
    return;
  }
  const dialog = await DialogPlugin.confirm({
    header: '确认删除',
    body: `确定要删除选中的 ${selectedRowKeys.value.length} 条记录吗？`,
    confirmBtn: '确定',
    cancelBtn: '取消'
  });
  if (dialog) {
    MessagePlugin.success('批量删除成功');
    selectedRowKeys.value = [];
    loadData();
  }
};

// 计算本月
const handleCalculate = async () => {
  const dialog = await DialogPlugin.confirm({
    header: '确认计算',
    body: '确定要计算本月的社保公积金吗？',
    confirmBtn: '确定',
    cancelBtn: '取消'
  });
  if (dialog) {
    MessagePlugin.success('计算成功');
    loadData();
  }
};

// 导出
const handleExport = () => {
  MessagePlugin.success('导出成功');
};

// 提交表单
const handleSubmit = async ({ validateResult }: any) => {
  if (validateResult === true) {
    MessagePlugin.success(formData.id ? '更新成功' : '创建成功');
    dialogVisible.value = false;
    loadData();
  }
};

// 加载数据
const loadData = () => {
  loading.value = true;
  setTimeout(() => {
    loading.value = false;
  }, 500);
};

onMounted(() => {
  loadData();
});
</script>

<style scoped lang="less">
.social-insurance-container {
  .statistics-cards {
    margin-bottom: 16px;
  }

  .search-card {
    margin-bottom: 16px;
  }

  .operation-card {
    margin-bottom: 16px;
  }

  .table-card {
    margin-bottom: 16px;
  }
}
</style>
