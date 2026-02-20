<template>
  <div class="salary-page">
    <!-- 统计卡片 -->
    <t-row :gutter="16" style="margin-bottom: 16px;">
      <t-col :span="6">
        <t-card>
          <t-statistic title="本月应发工资总额" :value="stats.totalSalary" :precision="2" prefix="¥" />
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card>
          <t-statistic title="本月实发工资总额" :value="stats.actualSalary" :precision="2" prefix="¥" />
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card>
          <t-statistic title="本月社保公积金总额" :value="stats.socialTotal" :precision="2" prefix="¥" />
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card>
          <t-statistic title="本月个税总额" :value="stats.taxTotal" :precision="2" prefix="¥" />
        </t-card>
      </t-col>
    </t-row>

    <!-- 操作栏 -->
    <t-card class="action-card">
      <t-space>
        <t-button theme="primary" @click="handleCalculate">
          <template #icon><t-icon name="calculator" /></template>
          计算薪资
        </t-button>
        <t-button theme="success" @click="handleBatchPay">
          <template #icon><t-icon name="money-circle" /></template>
          批量发放
        </t-button>
        <t-button theme="default" @click="handleExport">
          <template #icon><t-icon name="download" /></template>
          导出工资表
        </t-button>
      </t-space>
      <t-select v-model="searchForm.month" placeholder="请选择月份" style="width: 200px; float: right;" @change="loadData">
        <t-option value="2026-01" label="2026年01月" />
        <t-option value="2026-02" label="2026年02月" />
        <t-option value="2025-12" label="2025年12月" />
        <t-option value="2025-11" label="2025年11月" />
      </t-select>
    </t-card>

    <!-- 搜索卡片 -->
    <t-card class="search-card">
      <t-form ref="searchForm" :data="searchForm" layout="inline" @submit="handleSearch" @reset="handleReset">
        <t-form-item label="姓名" name="name">
          <t-input v-model="searchForm.name" placeholder="请输入姓名" clearable />
        </t-form-item>
        <t-form-item label="工号" name="employeeNo">
          <t-input v-model="searchForm.employeeNo" placeholder="请输入工号" clearable />
        </t-form-item>
        <t-form-item label="部门" name="departmentId">
          <t-select v-model="searchForm.departmentId" placeholder="请选择部门" clearable>
            <t-option value="1" label="技术研发部" />
            <t-option value="2" label="市场部" />
            <t-option value="3" label="运营部" />
            <t-option value="4" label="财务部" />
          </t-select>
        </t-form-item>
        <t-form-item label="发放状态" name="payStatus">
          <t-select v-model="searchForm.payStatus" placeholder="请选择发放状态" clearable>
            <t-option value="unpaid" label="未发放" />
            <t-option value="paid" label="已发放" />
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

    <!-- 薪资表格 -->
    <t-card class="table-card">
      <t-table
        :data="tableData"
        :columns="columns"
        :loading="loading"
        :pagination="pagination"
        row-key="id"
        @page-change="handlePageChange"
      >
        <template #payStatus="{ row }">
          <t-tag v-if="row.payStatus === 'paid'" theme="success" variant="light">已发放</t-tag>
          <t-tag v-else theme="warning" variant="light">未发放</t-tag>
        </template>
        <template #operation="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleDetail(row)">详情</t-link>
            <t-link v-if="row.payStatus === 'unpaid'" theme="success" @click="handlePay(row)">发放</t-link>
            <t-link theme="primary" @click="handleSlip(row)">工资条</t-link>
          </t-space>
        </template>
      </t-table>
    </t-card>

    <!-- 薪资详情弹窗 -->
    <t-dialog v-model:visible="detailVisible" header="薪资详情" width="900px" :footer="false">
      <t-descriptions v-if="currentSalary" :column="2" bordered title="基本信息">
        <t-descriptions-item label="工号">{{ currentSalary.employeeNo }}</t-descriptions-item>
        <t-descriptions-item label="姓名">{{ currentSalary.name }}</t-descriptions-item>
        <t-descriptions-item label="部门">{{ currentSalary.department }}</t-descriptions-item>
        <t-descriptions-item label="职位">{{ currentSalary.position }}</t-descriptions-item>
        <t-descriptions-item label="发放月份">{{ currentSalary.month }}</t-descriptions-item>
        <t-descriptions-item label="发放状态">
          <t-tag v-if="currentSalary.payStatus === 'paid'" theme="success">已发放</t-tag>
          <t-tag v-else theme="warning">未发放</t-tag>
        </t-descriptions-item>
      </t-descriptions>
      <t-divider>薪资明细</t-divider>
      <t-descriptions v-if="currentSalary" :column="2" bordered>
        <t-descriptions-item label="基本工资">¥{{ currentSalary.baseSalary?.toFixed(2) }}</t-descriptions-item>
        <t-descriptions-item label="绩效工资">¥{{ currentSalary.performanceSalary?.toFixed(2) }}</t-descriptions-item>
        <t-descriptions-item label="岗位津贴">¥{{ currentSalary.positionAllowance?.toFixed(2) }}</t-descriptions-item>
        <t-descriptions-item label="住房补贴">¥{{ currentSalary.housingAllowance?.toFixed(2) }}</t-descriptions-item>
        <t-descriptions-item label="交通补贴">¥{{ currentSalary.transportAllowance?.toFixed(2) }}</t-descriptions-item>
        <t-descriptions-item label="餐补">¥{{ currentSalary.mealAllowance?.toFixed(2) }}</t-descriptions-item>
        <t-descriptions-item label="加班补贴">¥{{ currentSalary.overtimeAllowance?.toFixed(2) }}</t-descriptions-item>
        <t-descriptions-item label="全勤奖">¥{{ currentSalary.attendanceBonus?.toFixed(2) }}</t-descriptions-item>
        <t-descriptions-item label="应发合计" :span="2">
          <span style="font-weight: bold; color: var(--td-brand-color);">¥{{ currentSalary.totalIncome?.toFixed(2) }}</span>
        </t-descriptions-item>
      </t-descriptions>
      <t-divider>扣款明细</t-divider>
      <t-descriptions v-if="currentSalary" :column="2" bordered>
        <t-descriptions-item label="社保个人">¥{{ currentSalary.socialPersonal?.toFixed(2) }}</t-descriptions-item>
        <t-descriptions-item label="公积金个人">¥{{ currentSalary.providentPersonal?.toFixed(2) }}</t-descriptions-item>
        <t-descriptions-item label="个税">¥{{ currentSalary.tax?.toFixed(2) }}</t-descriptions-item>
        <t-descriptions-item label="其他扣款">¥{{ currentSalary.otherDeduction?.toFixed(2) }}</t-descriptions-item>
        <t-descriptions-item label="扣款合计" :span="2">
          <span style="font-weight: bold; color: var(--td-error-color);">¥{{ currentSalary.totalDeduction?.toFixed(2) }}</span>
        </t-descriptions-item>
      </t-descriptions>
      <t-divider>实发工资</t-divider>
      <t-result theme="success" title="实发工资" :sub-title="`¥${currentSalary?.actualSalary?.toFixed(2) || 0}`" />
    </t-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue';
import { MessagePlugin } from 'tdesign-vue-next';

// 统计数据
const stats = reactive({
  totalSalary: 285000.00,
  actualSalary: 238500.00,
  socialTotal: 32000.00,
  taxTotal: 14500.00,
});

// 搜索表单
const searchForm = reactive({
  month: '2026-02',
  name: '',
  employeeNo: '',
  departmentId: '',
  payStatus: '',
});

// 表格数据
const tableData = ref([]);
const loading = ref(false);

// 分页
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
});

// 弹窗
const detailVisible = ref(false);
const currentSalary = ref<any>(null);

// 表格列
const columns = [
  { colKey: 'employeeNo', title: '工号', width: 120 },
  { colKey: 'name', title: '姓名', width: 100 },
  { colKey: 'department', title: '部门', width: 150 },
  { colKey: 'position', title: '职位', width: 120 },
  { colKey: 'baseSalary', title: '基本工资', width: 120 },
  { colKey: 'performanceSalary', title: '绩效工资', width: 120 },
  { colKey: 'totalIncome', title: '应发合计', width: 120 },
  { colKey: 'socialPersonal', title: '社保', width: 100 },
  { colKey: 'tax', title: '个税', width: 100 },
  { colKey: 'actualSalary', title: '实发工资', width: 120 },
  { colKey: 'payStatus', title: '发放状态', width: 100 },
  { colKey: 'operation', title: '操作', width: 180, fixed: 'right' },
];

// 加载数据
const loadData = async () => {
  loading.value = true;
  try {
    await new Promise(resolve => setTimeout(resolve, 500));
    const mockData = [
      {
        id: 1,
        employeeNo: 'E001',
        name: '张三',
        department: '技术研发部',
        position: '软件工程师',
        month: '2026-02',
        baseSalary: 8000,
        performanceSalary: 2000,
        positionAllowance: 500,
        housingAllowance: 800,
        transportAllowance: 200,
        mealAllowance: 300,
        overtimeAllowance: 600,
        attendanceBonus: 200,
        totalIncome: 12600,
        socialPersonal: 1008,
        providentPersonal: 504,
        tax: 480,
        otherDeduction: 0,
        totalDeduction: 1992,
        actualSalary: 10608,
        payStatus: 'unpaid',
      },
      {
        id: 2,
        employeeNo: 'E002',
        name: '李四',
        department: '市场部',
        position: '销售代表',
        month: '2026-02',
        baseSalary: 7000,
        performanceSalary: 5000,
        positionAllowance: 300,
        housingAllowance: 800,
        transportAllowance: 300,
        mealAllowance: 300,
        overtimeAllowance: 0,
        attendanceBonus: 200,
        totalIncome: 13900,
        socialPersonal: 882,
        providentPersonal: 441,
        tax: 720,
        otherDeduction: 0,
        totalDeduction: 2043,
        actualSalary: 11857,
        payStatus: 'paid',
      },
      {
        id: 3,
        employeeNo: 'E003',
        name: '王五',
        department: '运营部',
        position: '运营专员',
        month: '2026-02',
        baseSalary: 6000,
        performanceSalary: 1000,
        positionAllowance: 200,
        housingAllowance: 600,
        transportAllowance: 200,
        mealAllowance: 300,
        overtimeAllowance: 400,
        attendanceBonus: 200,
        totalIncome: 8900,
        socialPersonal: 712,
        providentPersonal: 356,
        tax: 220,
        otherDeduction: 0,
        totalDeduction: 1288,
        actualSalary: 7612,
        payStatus: 'unpaid',
      },
    ];
    tableData.value = mockData;
    pagination.total = 128;
  } catch (error) {
    MessagePlugin.error('加载数据失败');
  } finally {
    loading.value = false;
  }
};

// 搜索
const handleSearch = () => {
  pagination.current = 1;
  loadData();
};

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    month: '2026-02',
    name: '',
    employeeNo: '',
    departmentId: '',
    payStatus: '',
  });
  handleSearch();
};

// 计算薪资
const handleCalculate = async () => {
  try {
    await new Promise(resolve => setTimeout(resolve, 1000));
    MessagePlugin.success('薪资计算完成');
    loadData();
  } catch (error) {
    MessagePlugin.error('计算失败');
  }
};

// 批量发放
const handleBatchPay = async () => {
  try {
    await new Promise(resolve => setTimeout(resolve, 500));
    MessagePlugin.success('批量发放成功');
    loadData();
  } catch (error) {
    MessagePlugin.error('发放失败');
  }
};

// 导出
const handleExport = async () => {
  try {
    await new Promise(resolve => setTimeout(resolve, 500));
    MessagePlugin.success('导出成功');
  } catch (error) {
    MessagePlugin.error('导出失败');
  }
};

// 详情
const handleDetail = (row: any) => {
  currentSalary.value = row;
  detailVisible.value = true;
};

// 发放
const handlePay = async (row: any) => {
  try {
    await new Promise(resolve => setTimeout(resolve, 300));
    MessagePlugin.success(`已发放 ${row.name} 的工资`);
    loadData();
  } catch (error) {
    MessagePlugin.error('发放失败');
  }
};

// 工资条
const handleSlip = (row: any) => {
  MessagePlugin.info(`生成 ${row.name} 的工资条`);
};

// 分页变化
const handlePageChange = (pageInfo: any) => {
  pagination.current = pageInfo.current;
  pagination.pageSize = pageInfo.pageSize;
  loadData();
};

loadData();
</script>

<style lang="scss" scoped>
.salary-page {
  .action-card,
  .search-card,
  .table-card {
    margin-bottom: 16px;
  }
}
</style>
