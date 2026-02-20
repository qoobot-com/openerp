<template>
  <div class="employee-file-page">
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
        <t-form-item label="入职日期" name="entryDate">
          <t-date-range-picker v-model="searchForm.entryDate" placeholder="请选择入职日期" clearable />
        </t-form-item>
        <t-form-item label="状态" name="status">
          <t-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <t-option value="1" label="在职" />
            <t-option value="2" label="试用期" />
            <t-option value="3" label="离职" />
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
    <t-card class="action-card">
      <t-space>
        <t-button theme="primary" @click="handleAdd">
          <template #icon><t-icon name="add" /></template>
          新建档案
        </t-button>
        <t-button theme="default" @click="handleImport">
          <template #icon><t-icon name="upload" /></template>
          批量导入
        </t-button>
        <t-button theme="default" @click="handleExport">
          <template #icon><t-icon name="download" /></template>
          导出档案
        </t-button>
      </t-space>
    </t-card>

    <!-- 员工档案表格 -->
    <t-card class="table-card">
      <t-table
        :data="tableData"
        :columns="columns"
        :loading="loading"
        :pagination="pagination"
        row-key="id"
        @page-change="handlePageChange"
      >
        <template #avatar="{ row }">
          <t-avatar :image="row.avatar" size="medium" />
        </template>
        <template #gender="{ row }">
          <t-tag v-if="row.gender === '1'" theme="primary" variant="light">男</t-tag>
          <t-tag v-else theme="warning" variant="light">女</t-tag>
        </template>
        <template #status="{ row }">
          <t-tag v-if="row.status === '1'" theme="success" variant="light">在职</t-tag>
          <t-tag v-else-if="row.status === '2'" theme="primary" variant="light">试用期</t-tag>
          <t-tag v-else theme="default" variant="light">离职</t-tag>
        </template>
        <template #operation="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleView(row)">查看</t-link>
            <t-link theme="primary" @click="handleEdit(row)">编辑</t-link>
            <t-link theme="primary" @click="handleHistory(row)">异动记录</t-link>
            <t-popconfirm content="确认删除该档案吗？" @confirm="handleDelete(row.id)">
              <t-link theme="danger">删除</t-link>
            </t-popconfirm>
          </t-space>
        </template>
      </t-table>
    </t-card>

    <!-- 档案编辑弹窗 -->
    <t-dialog
      v-model:visible="dialogVisible"
      :header="isEdit ? '编辑员工档案' : '新建员工档案'"
      width="900px"
      :confirm-btn="{
        content: '保存',
        theme: 'primary',
        loading: saving,
      }"
      @confirm="handleSubmit"
    >
      <t-form ref="formRef" :data="formData" :rules="rules" label-align="right" label-width="120px">
        <t-tabs v-model="activeTab">
          <t-tab-panel value="basic" label="基本信息">
            <t-space direction="vertical" style="width: 100%;" :size="16">
              <t-row :gutter="16">
                <t-col :span="12">
                  <t-form-item label="姓名" name="name">
                    <t-input v-model="formData.name" placeholder="请输入姓名" />
                  </t-form-item>
                </t-col>
                <t-col :span="12">
                  <t-form-item label="工号" name="employeeNo">
                    <t-input v-model="formData.employeeNo" placeholder="请输入工号" />
                  </t-form-item>
                </t-col>
              </t-row>
              <t-row :gutter="16">
                <t-col :span="12">
                  <t-form-item label="性别" name="gender">
                    <t-radio-group v-model="formData.gender">
                      <t-radio value="1">男</t-radio>
                      <t-radio value="2">女</t-radio>
                    </t-radio-group>
                  </t-form-item>
                </t-col>
                <t-col :span="12">
                  <t-form-item label="出生日期" name="birthDate">
                    <t-date-picker v-model="formData.birthDate" placeholder="请选择出生日期" />
                  </t-form-item>
                </t-col>
              </t-row>
              <t-row :gutter="16">
                <t-col :span="12">
                  <t-form-item label="手机号" name="phone">
                    <t-input v-model="formData.phone" placeholder="请输入手机号" />
                  </t-form-item>
                </t-col>
                <t-col :span="12">
                  <t-form-item label="邮箱" name="email">
                    <t-input v-model="formData.email" placeholder="请输入邮箱" />
                  </t-form-item>
                </t-col>
              </t-row>
              <t-row :gutter="16">
                <t-col :span="12">
                  <t-form-item label="身份证号" name="idCard">
                    <t-input v-model="formData.idCard" placeholder="请输入身份证号" />
                  </t-form-item>
                </t-col>
                <t-col :span="12">
                  <t-form-item label="籍贯" name="nativePlace">
                    <t-input v-model="formData.nativePlace" placeholder="请输入籍贯" />
                  </t-form-item>
                </t-col>
              </t-row>
            </t-space>
          </t-tab-panel>

          <t-tab-panel value="work" label="工作信息">
            <t-space direction="vertical" style="width: 100%;" :size="16">
              <t-row :gutter="16">
                <t-col :span="12">
                  <t-form-item label="所属部门" name="departmentId">
                    <t-select v-model="formData.departmentId" placeholder="请选择部门">
                      <t-option value="1" label="技术研发部" />
                      <t-option value="2" label="市场部" />
                      <t-option value="3" label="运营部" />
                      <t-option value="4" label="财务部" />
                    </t-select>
                  </t-form-item>
                </t-col>
                <t-col :span="12">
                  <t-form-item label="职位" name="positionId">
                    <t-select v-model="formData.positionId" placeholder="请选择职位">
                      <t-option value="1" label="软件工程师" />
                      <t-option value="2" label="产品经理" />
                      <t-option value="3" label="销售代表" />
                      <t-option value="4" label="会计" />
                    </t-select>
                  </t-form-item>
                </t-col>
              </t-row>
              <t-row :gutter="16">
                <t-col :span="12">
                  <t-form-item label="入职日期" name="entryDate">
                    <t-date-picker v-model="formData.entryDate" placeholder="请选择入职日期" />
                  </t-form-item>
                </t-col>
                <t-col :span="12">
                  <t-form-item label="转正日期" name="regularDate">
                    <t-date-picker v-model="formData.regularDate" placeholder="请选择转正日期" />
                  </t-form-item>
                </t-col>
              </t-row>
              <t-row :gutter="16">
                <t-col :span="12">
                  <t-form-item label="员工状态" name="status">
                    <t-select v-model="formData.status" placeholder="请选择状态">
                      <t-option value="1" label="在职" />
                      <t-option value="2" label="试用期" />
                      <t-option value="3" label="离职" />
                    </t-select>
                  </t-form-item>
                </t-col>
                <t-col :span="12">
                  <t-form-item label="工作地点" name="workLocation">
                    <t-input v-model="formData.workLocation" placeholder="请输入工作地点" />
                  </t-form-item>
                </t-col>
              </t-row>
              <t-form-item label="办公地址" name="officeAddress">
                <t-input v-model="formData.officeAddress" placeholder="请输入办公地址" />
              </t-form-item>
            </t-space>
          </t-tab-panel>

          <t-tab-panel value="salary" label="薪资信息">
            <t-space direction="vertical" style="width: 100%;" :size="16">
              <t-row :gutter="16">
                <t-col :span="8">
                  <t-form-item label="基本工资" name="baseSalary">
                    <t-input-number v-model="formData.baseSalary" :decimal-places="2" placeholder="请输入基本工资" />
                  </t-form-item>
                </t-col>
                <t-col :span="8">
                  <t-form-item label="绩效工资" name="performanceSalary">
                    <t-input-number v-model="formData.performanceSalary" :decimal-places="2" placeholder="请输入绩效工资" />
                  </t-form-item>
                </t-col>
                <t-col :span="8">
                  <t-form-item label="岗位津贴" name="positionAllowance">
                    <t-input-number v-model="formData.positionAllowance" :decimal-places="2" placeholder="请输入岗位津贴" />
                  </t-form-item>
                </t-col>
              </t-row>
              <t-row :gutter="16">
                <t-col :span="8">
                  <t-form-item label="住房补贴" name="housingAllowance">
                    <t-input-number v-model="formData.housingAllowance" :decimal-places="2" placeholder="请输入住房补贴" />
                  </t-form-item>
                </t-col>
                <t-col :span="8">
                  <t-form-item label="交通补贴" name="transportAllowance">
                    <t-input-number v-model="formData.transportAllowance" :decimal-places="2" placeholder="请输入交通补贴" />
                  </t-form-item>
                </t-col>
                <t-col :span="8">
                  <t-form-item label="餐补" name="mealAllowance">
                    <t-input-number v-model="formData.mealAllowance" :decimal-places="2" placeholder="请输入餐补" />
                  </t-form-item>
                </t-col>
              </t-row>
              <t-alert theme="info" message="月薪合计 = 基本工资 + 绩效工资 + 岗位津贴 + 各类补贴" />
            </t-space>
          </t-tab-panel>

          <t-tab-panel value="social" label="社保公积金">
            <t-space direction="vertical" style="width: 100%;" :size="16">
              <t-form-item label="是否缴纳社保" name="hasSocial">
                <t-switch v-model="formData.hasSocial" />
              </t-form-item>
              <template v-if="formData.hasSocial">
                <t-row :gutter="16">
                  <t-col :span="12">
                    <t-form-item label="社保账号" name="socialAccount">
                      <t-input v-model="formData.socialAccount" placeholder="请输入社保账号" />
                    </t-form-item>
                  </t-col>
                  <t-col :span="12">
                    <t-form-item label="公积金账号" name="providentAccount">
                      <t-input v-model="formData.providentAccount" placeholder="请输入公积金账号" />
                    </t-form-item>
                  </t-col>
                </t-row>
                <t-row :gutter="16">
                  <t-col :span="8">
                    <t-form-item label="社保基数" name="socialBase">
                      <t-input-number v-model="formData.socialBase" :decimal-places="2" placeholder="请输入社保基数" />
                    </t-form-item>
                  </t-col>
                  <t-col :span="8">
                    <t-form-item label="公积金基数" name="providentBase">
                      <t-input-number v-model="formData.providentBase" :decimal-places="2" placeholder="请输入公积金基数" />
                    </t-form-item>
                  </t-col>
                  <t-col :span="8">
                    <t-form-item label="公积金比例" name="providentRatio">
                      <t-input-number v-model="formData.providentRatio" :min="0" :max="100" placeholder="请输入比例" />
                      <span style="margin-left: 8px;">%</span>
                    </t-form-item>
                  </t-col>
                </t-row>
              </template>
            </t-space>
          </t-tab-panel>
        </t-tabs>
      </t-form>
    </t-dialog>

    <!-- 档案详情弹窗 -->
    <t-dialog v-model:visible="detailVisible" header="员工档案详情" width="900px" :footer="false">
      <t-space v-if="currentEmployee" direction="vertical" style="width: 100%;" :size="16">
        <t-card>
          <template #header>
            <t-space>
              <t-avatar :image="currentEmployee.avatar" size="large" />
              <div>
                <div style="font-size: 18px; font-weight: bold;">{{ currentEmployee.name }}</div>
                <div style="color: var(--td-text-color-secondary);">{{ currentEmployee.employeeNo }} | {{ currentEmployee.department }}</div>
              </div>
            </t-space>
          </template>
          <t-descriptions :column="2" bordered>
            <t-descriptions-item label="性别">{{ currentEmployee.gender === '1' ? '男' : '女' }}</t-descriptions-item>
            <t-descriptions-item label="出生日期">{{ currentEmployee.birthDate }}</t-descriptions-item>
            <t-descriptions-item label="手机号">{{ currentEmployee.phone }}</t-descriptions-item>
            <t-descriptions-item label="邮箱">{{ currentEmployee.email }}</t-descriptions-item>
            <t-descriptions-item label="身份证号">{{ currentEmployee.idCard }}</t-descriptions-item>
            <t-descriptions-item label="籍贯">{{ currentEmployee.nativePlace }}</t-descriptions-item>
            <t-descriptions-item label="部门">{{ currentEmployee.department }}</t-descriptions-item>
            <t-descriptions-item label="职位">{{ currentEmployee.position }}</t-descriptions-item>
            <t-descriptions-item label="入职日期">{{ currentEmployee.entryDate }}</t-descriptions-item>
            <t-descriptions-item label="转正日期">{{ currentEmployee.regularDate }}</t-descriptions-item>
            <t-descriptions-item label="工作地点">{{ currentEmployee.workLocation }}</t-descriptions-item>
            <t-descriptions-item label="基本工资">¥{{ currentEmployee.baseSalary?.toFixed(2) }}</t-descriptions-item>
          </t-descriptions>
        </t-card>
        <t-space>
          <t-button theme="primary" @click="handleEdit(currentEmployee)">编辑</t-button>
          <t-button @click="detailVisible = false">关闭</t-button>
        </t-space>
      </t-space>
    </t-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue';
import { MessagePlugin } from 'tdesign-vue-next';

// 搜索表单
const searchForm = reactive({
  name: '',
  employeeNo: '',
  departmentId: '',
  entryDate: [],
  status: '',
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
const dialogVisible = ref(false);
const detailVisible = ref(false);
const activeTab = ref('basic');
const isEdit = computed(() => !!formData.id);
const formRef = ref();
const saving = ref(false);
const currentEmployee = ref<any>(null);

const formData = reactive({
  id: undefined,
  name: '',
  employeeNo: '',
  gender: '1',
  birthDate: '',
  phone: '',
  email: '',
  idCard: '',
  nativePlace: '',
  departmentId: '',
  positionId: '',
  entryDate: '',
  regularDate: '',
  status: '1',
  workLocation: '',
  officeAddress: '',
  baseSalary: 0,
  performanceSalary: 0,
  positionAllowance: 0,
  housingAllowance: 0,
  transportAllowance: 0,
  mealAllowance: 0,
  hasSocial: false,
  socialAccount: '',
  providentAccount: '',
  socialBase: 0,
  providentBase: 0,
  providentRatio: 12,
});

const rules = {
  name: [{ required: true, message: '请输入姓名', type: 'error' }],
  employeeNo: [{ required: true, message: '请输入工号', type: 'error' }],
  phone: [{ required: true, message: '请输入手机号', type: 'error' }],
  departmentId: [{ required: true, message: '请选择部门', type: 'error' }],
  positionId: [{ required: true, message: '请选择职位', type: 'error' }],
  entryDate: [{ required: true, message: '请选择入职日期', type: 'error' }],
};

// 表格列
const columns = [
  { colKey: 'avatar', title: '头像', width: 80 },
  { colKey: 'employeeNo', title: '工号', width: 120 },
  { colKey: 'name', title: '姓名', width: 100 },
  { colKey: 'gender', title: '性别', width: 80 },
  { colKey: 'department', title: '部门', width: 150 },
  { colKey: 'position', title: '职位', width: 120 },
  { colKey: 'phone', title: '手机号', width: 130 },
  { colKey: 'entryDate', title: '入职日期', width: 120 },
  { colKey: 'status', title: '状态', width: 100 },
  { colKey: 'operation', title: '操作', width: 200, fixed: 'right' },
];

// 加载数据
const loadData = async () => {
  loading.value = true;
  try {
    await new Promise(resolve => setTimeout(resolve, 500));
    const mockData = [
      {
        id: 1,
        avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=1',
        employeeNo: 'E001',
        name: '张三',
        gender: '1',
        phone: '13800138001',
        email: 'zhangsan@example.com',
        department: '技术研发部',
        position: '软件工程师',
        entryDate: '2023-01-15',
        regularDate: '2023-04-15',
        status: '1',
      },
      {
        id: 2,
        avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=2',
        employeeNo: 'E002',
        name: '李四',
        gender: '2',
        phone: '13800138002',
        email: 'lisi@example.com',
        department: '市场部',
        position: '销售代表',
        entryDate: '2023-03-20',
        regularDate: '2023-06-20',
        status: '1',
      },
      {
        id: 3,
        avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=3',
        employeeNo: 'E003',
        name: '王五',
        gender: '1',
        phone: '13800138003',
        email: 'wangwu@example.com',
        department: '财务部',
        position: '会计',
        entryDate: '2023-06-10',
        regularDate: '2023-09-10',
        status: '2',
      },
    ];
    tableData.value = mockData;
    pagination.total = 50;
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
    name: '',
    employeeNo: '',
    departmentId: '',
    entryDate: [],
    status: '',
  });
  handleSearch();
};

// 新增
const handleAdd = () => {
  activeTab.value = 'basic';
  Object.assign(formData, {
    id: undefined,
    name: '',
    employeeNo: '',
    gender: '1',
    birthDate: '',
    phone: '',
    email: '',
    idCard: '',
    nativePlace: '',
    departmentId: '',
    positionId: '',
    entryDate: '',
    regularDate: '',
    status: '1',
    workLocation: '',
    officeAddress: '',
    baseSalary: 0,
    performanceSalary: 0,
    positionAllowance: 0,
    housingAllowance: 0,
    transportAllowance: 0,
    mealAllowance: 0,
    hasSocial: false,
    socialAccount: '',
    providentAccount: '',
    socialBase: 0,
    providentBase: 0,
    providentRatio: 12,
  });
  dialogVisible.value = true;
};

// 编辑
const handleEdit = (row: any) => {
  activeTab.value = 'basic';
  Object.assign(formData, row);
  dialogVisible.value = true;
};

// 查看
const handleView = (row: any) => {
  currentEmployee.value = row;
  detailVisible.value = true;
};

// 删除
const handleDelete = async (id: number) => {
  try {
    await new Promise(resolve => setTimeout(resolve, 300));
    MessagePlugin.success('删除成功');
    loadData();
  } catch (error) {
    MessagePlugin.error('删除失败');
  }
};

// 导入
const handleImport = () => {
  MessagePlugin.info('导入功能开发中');
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

// 异动记录
const handleHistory = (row: any) => {
  MessagePlugin.info('异动记录功能开发中');
};

// 提交
const handleSubmit = async () => {
  const valid = await formRef.value?.validate();
  if (!valid) return;

  saving.value = true;
  try {
    await new Promise(resolve => setTimeout(resolve, 500));
    MessagePlugin.success(isEdit.value ? '更新成功' : '创建成功');
    dialogVisible.value = false;
    loadData();
  } catch (error) {
    MessagePlugin.error('操作失败');
  } finally {
    saving.value = false;
  }
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
.employee-file-page {
  .search-card,
  .action-card,
  .table-card {
    margin-bottom: 16px;
  }
}
</style>
