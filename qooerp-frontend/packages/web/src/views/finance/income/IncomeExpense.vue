<template>
  <div class="income-expense-container">
    <!-- 统计卡片 -->
    <t-row :gutter="16" class="statistics-cards">
      <t-col :span="6">
        <t-card>
          <t-statistic title="本月收入(元)" :value="stats.monthIncome" :precision="2" />
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card>
          <t-statistic title="本月支出(元)" :value="stats.monthExpense" :precision="2" />
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card>
          <t-statistic title="本月结余(元)" :value="stats.monthBalance" :precision="2" />
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card>
          <t-statistic title="待审批笔数" :value="stats.pendingCount" />
        </t-card>
      </t-col>
    </t-row>

    <!-- 搜索卡片 -->
    <t-card class="search-card" title="查询条件">
      <t-form :data="searchForm" layout="inline" @submit="handleSearch" @reset="handleReset">
        <t-form-item label="收支类型" name="type">
          <t-select v-model="searchForm.type" placeholder="请选择收支类型" clearable>
            <t-option value="income" label="收入" />
            <t-option value="expense" label="支出" />
          </t-select>
        </t-form-item>
        <t-form-item label="收支科目" name="category">
          <t-select v-model="searchForm.category" placeholder="请选择科目" clearable>
            <t-option v-for="cat in categoryList" :key="cat.value" :value="cat.value" :label="cat.label" />
          </t-select>
        </t-form-item>
        <t-form-item label="账户" name="accountId">
          <t-select v-model="searchForm.accountId" placeholder="请选择账户" clearable>
            <t-option v-for="acc in accountList" :key="acc.id" :value="acc.id" :label="acc.name" />
          </t-select>
        </t-form-item>
        <t-form-item label="金额范围" name="amountRange">
          <t-input-number v-model="searchForm.minAmount" placeholder="最小金额" :min="0" style="width: 120px" />
          <span style="margin: 0 8px">-</span>
          <t-input-number v-model="searchForm.maxAmount" placeholder="最大金额" :min="0" style="width: 120px" />
        </t-form-item>
        <t-form-item label="交易日期" name="dateRange">
          <t-date-range-picker v-model="searchForm.dateRange" placeholder="请选择日期范围" />
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
          新增收支
        </t-button>
        <t-button theme="primary" @click="handleAddIncome">
          <template #icon><money-circle-icon /></template>
          新增收入
        </t-button>
        <t-button theme="danger" @click="handleAddExpense">
          <template #icon><icon-shopping /></template>
          新增支出
        </t-button>
        <t-button theme="default" @click="handleBatchDelete">
          <template #icon><delete-icon /></template>
          批量删除
        </t-button>
        <t-button theme="default" @click="handleExport">
          <template #icon><download-icon /></template>
          导出报表
        </t-button>
      </t-space>
    </t-card>

    <!-- 收支列表 -->
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
        <template #type="{ row }">
          <t-tag v-if="row.type === 'income'" theme="success">收入</t-tag>
          <t-tag v-else theme="danger">支出</t-tag>
        </template>
        <template #amount="{ row }">
          <span :style="{ color: row.type === 'income' ? '#00a870' : '#d54941', fontWeight: 'bold' }">
            {{ row.type === 'income' ? '+' : '-' }}{{ row.amount?.toFixed(2) }}
          </span>
        </template>
        <template #status="{ row }">
          <t-tag v-if="row.status === 'pending'" theme="warning">待审批</t-tag>
          <t-tag v-else-if="row.status === 'approved'" theme="success">已通过</t-tag>
          <t-tag v-else-if="row.status === 'rejected'" theme="danger">已拒绝</t-tag>
        </template>
        <template #operation="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleView(row)">查看</t-link>
            <t-link v-if="row.status === 'pending'" theme="primary" @click="handleApprove(row)">审批</t-link>
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
      width="700px"
    >
      <t-form
        ref="formRef"
        :data="formData"
        :rules="rules"
        label-width="120px"
        @submit="handleSubmit"
      >
        <t-form-item label="收支类型" name="type">
          <t-radio-group v-model="formData.type">
            <t-radio value="income">收入</t-radio>
            <t-radio value="expense">支出</t-radio>
          </t-radio-group>
        </t-form-item>
        <t-form-item label="收支科目" name="category">
          <t-select v-model="formData.category" placeholder="请选择科目" clearable>
            <t-option v-for="cat in categoryList" :key="cat.value" :value="cat.value" :label="cat.label" />
          </t-select>
        </t-form-item>
        <t-form-item label="交易金额(元)" name="amount">
          <t-input-number v-model="formData.amount" placeholder="请输入金额" :precision="2" :min="0" style="width: 100%" />
        </t-form-item>
        <t-form-item label="交易日期" name="transactionDate">
          <t-date-picker v-model="formData.transactionDate" placeholder="请选择日期" style="width: 100%" />
        </t-form-item>
        <t-form-item label="收支账户" name="accountId">
          <t-select v-model="formData.accountId" placeholder="请选择账户" clearable>
            <t-option v-for="acc in accountList" :key="acc.id" :value="acc.id" :label="acc.name" />
          </t-select>
        </t-form-item>
        <t-form-item label="关联对象" name="relatedId">
          <t-select v-model="formData.relatedId" placeholder="请选择关联对象" filterable clearable>
            <t-option v-for="obj in relatedList" :key="obj.id" :value="obj.id" :label="obj.name" />
          </t-select>
        </t-form-item>
        <t-form-item label="经办人" name="handlerId">
          <t-select v-model="formData.handlerId" placeholder="请选择经办人" filterable clearable>
            <t-option v-for="emp in employeeList" :key="emp.id" :value="emp.id" :label="emp.name" />
          </t-select>
        </t-form-item>
        <t-form-item label="备注说明" name="remark">
          <t-textarea v-model="formData.remark" placeholder="请输入备注说明" :maxlength="500" />
        </t-form-item>
        <t-form-item label="附件上传" name="attachments">
          <t-upload
            v-model="formData.attachments"
            action=""
            theme="file-flow"
            accept="image/*,.pdf,.doc,.docx"
            :max="5"
          />
        </t-form-item>
        <t-form-item>
          <t-space>
            <t-button theme="primary" type="submit">确定</t-button>
            <t-button theme="default" @click="dialogVisible = false">取消</t-button>
          </t-space>
        </t-form-item>
      </t-form>
    </t-dialog>

    <!-- 详情弹窗 -->
    <t-dialog v-model:visible="detailVisible" header="收支详情" width="700px" :footer="false">
      <t-descriptions bordered :column="2">
        <t-descriptions-item label="收支单号">{{ detailData.voucherNo }}</t-descriptions-item>
        <t-descriptions-item label="收支类型">
          <t-tag v-if="detailData.type === 'income'" theme="success">收入</t-tag>
          <t-tag v-else theme="danger">支出</t-tag>
        </t-descriptions-item>
        <t-descriptions-item label="收支科目">{{ detailData.categoryName }}</t-descriptions-item>
        <t-descriptions-item label="交易金额">
          <span :style="{ color: detailData.type === 'income' ? '#00a870' : '#d54941', fontWeight: 'bold', fontSize: '18px' }">
            {{ detailData.type === 'income' ? '+' : '-' }}{{ detailData.amount?.toFixed(2) }}
          </span>
        </t-descriptions-item>
        <t-descriptions-item label="交易日期">{{ detailData.transactionDate }}</t-descriptions-item>
        <t-descriptions-item label="收支账户">{{ detailData.accountName }}</t-descriptions-item>
        <t-descriptions-item label="关联对象">{{ detailData.relatedName }}</t-descriptions-item>
        <t-descriptions-item label="经办人">{{ detailData.handlerName }}</t-descriptions-item>
        <t-descriptions-item label="审批状态">
          <t-tag v-if="detailData.status === 'pending'" theme="warning">待审批</t-tag>
          <t-tag v-else-if="detailData.status === 'approved'" theme="success">已通过</t-tag>
          <t-tag v-else theme="danger">已拒绝</t-tag>
        </t-descriptions-item>
        <t-descriptions-item label="审批人">{{ detailData.approverName || '-' }}</t-descriptions-item>
        <t-descriptions-item label="创建时间">{{ detailData.createTime }}</t-descriptions-item>
        <t-descriptions-item label="备注说明" :span="2">{{ detailData.remark }}</t-descriptions-item>
      </t-descriptions>
      <t-divider>附件</t-divider>
      <t-list v-if="detailData.attachments?.length" :split="true">
        <t-list-item v-for="file in detailData.attachments" :key="file.id">
          <t-list-item-meta :title="file.name" :description="`${file.size} - ${file.uploadTime}`" />
        </t-list-item>
      </t-list>
      <t-empty v-else description="暂无附件" />
    </t-dialog>

    <!-- 审批弹窗 -->
    <t-dialog v-model:visible="approveVisible" header="收支审批" width="600px">
      <t-form ref="approveFormRef" :data="approveData" :rules="approveRules" label-width="120px">
        <t-form-item label="审批结果" name="result">
          <t-radio-group v-model="approveData.result">
            <t-radio value="approved">通过</t-radio>
            <t-radio value="rejected">拒绝</t-radio>
          </t-radio-group>
        </t-form-item>
        <t-form-item label="审批意见" name="comment">
          <t-textarea v-model="approveData.comment" placeholder="请输入审批意见" :maxlength="500" />
        </t-form-item>
      </t-form>
      <template #footer>
        <t-space>
          <t-button theme="primary" @click="handleApproveSubmit">确定</t-button>
          <t-button theme="default" @click="approveVisible = false">取消</t-button>
        </t-space>
      </template>
    </t-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { MessagePlugin, DialogPlugin } from 'tdesign-vue-next';
import { AddIcon, DeleteIcon, DownloadIcon, MoneyCircleIcon } from 'tdesign-icons-vue-next';

// 统计数据
const stats = ref({
  monthIncome: 325000.00,
  monthExpense: 186200.00,
  monthBalance: 138800.00,
  pendingCount: 12
});

// 搜索表单
const searchForm = reactive({
  type: undefined,
  category: undefined,
  accountId: undefined,
  minAmount: undefined,
  maxAmount: undefined,
  dateRange: []
});

// 收支科目列表
const categoryList = ref([
  { value: 'sales_income', label: '销售收入' },
  { value: 'service_income', label: '服务收入' },
  { value: 'other_income', label: '其他收入' },
  { value: 'purchase_payment', label: '采购付款' },
  { value: 'salary_payment', label: '工资支付' },
  { value: 'office_expense', label: '办公费用' },
  { value: 'travel_expense', label: '差旅费用' },
  { value: 'other_expense', label: '其他支出' }
]);

// 账户列表
const accountList = ref([
  { id: 1, name: '工商银行基本户' },
  { id: 2, name: '建设银行一般户' },
  { id: 3, name: '企业支付宝' },
  { id: 4, name: '备用金' }
]);

// 员工列表
const employeeList = ref([
  { id: 1, name: '张三' },
  { id: 2, name: '李四' },
  { id: 3, name: '王五' }
]);

// 关联对象列表
const relatedList = ref([
  { id: 1, name: '客户A' },
  { id: 2, name: '供应商B' },
  { id: 3, name: '项目X' }
]);

// 表格数据
const loading = ref(false);
const tableData = ref([
  {
    id: 1,
    voucherNo: 'SZ20260219001',
    type: 'income',
    category: 'sales_income',
    categoryName: '销售收入',
    amount: 150000.00,
    transactionDate: '2026-02-19',
    accountId: 1,
    accountName: '工商银行基本户',
    relatedId: 1,
    relatedName: '客户A',
    handlerId: 1,
    handlerName: '张三',
    status: 'approved',
    approverName: '李四',
    remark: '2月份货款',
    attachments: [
      { id: 1, name: '发票.pdf', size: '2.5MB', uploadTime: '2026-02-19 10:30' }
    ],
    createTime: '2026-02-19 10:30:00'
  },
  {
    id: 2,
    voucherNo: 'SZ20260219002',
    type: 'expense',
    category: 'purchase_payment',
    categoryName: '采购付款',
    amount: 80000.00,
    transactionDate: '2026-02-19',
    accountId: 1,
    accountName: '工商银行基本户',
    relatedId: 2,
    relatedName: '供应商B',
    handlerId: 2,
    handlerName: '李四',
    status: 'pending',
    approverName: null,
    remark: '采购原材料付款',
    attachments: [],
    createTime: '2026-02-19 14:20:00'
  },
  {
    id: 3,
    voucherNo: 'SZ20260219003',
    type: 'expense',
    category: 'salary_payment',
    categoryName: '工资支付',
    amount: 95000.00,
    transactionDate: '2026-02-18',
    accountId: 1,
    accountName: '工商银行基本户',
    relatedId: null,
    relatedName: '-',
    handlerId: 3,
    handlerName: '王五',
    status: 'approved',
    approverName: '张三',
    remark: '2月份工资',
    attachments: [
      { id: 2, name: '工资表.xlsx', size: '1.2MB', uploadTime: '2026-02-18 16:00' }
    ],
    createTime: '2026-02-18 16:00:00'
  }
]);

const selectedRowKeys = ref<number[]>([]);

// 表格列
const columns = [
  { colKey: 'voucherNo', title: '收支单号', width: 160 },
  { colKey: 'type', title: '收支类型', width: 100 },
  { colKey: 'categoryName', title: '收支科目', width: 140 },
  { colKey: 'amount', title: '金额(元)', width: 150 },
  { colKey: 'transactionDate', title: '交易日期', width: 120 },
  { colKey: 'accountName', title: '收支账户', width: 150 },
  { colKey: 'relatedName', title: '关联对象', width: 120 },
  { colKey: 'handlerName', title: '经办人', width: 100 },
  { colKey: 'status', title: '审批状态', width: 100 },
  { colKey: 'operation', title: '操作', width: 200, fixed: 'right' }
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
const approveVisible = ref(false);

// 表单
const formRef = ref();
const formData = reactive({
  id: undefined,
  type: 'income',
  category: undefined,
  amount: undefined,
  transactionDate: undefined,
  accountId: undefined,
  relatedId: undefined,
  handlerId: undefined,
  remark: '',
  attachments: []
});

// 详情数据
const detailData = ref({});

// 审批表单
const approveFormRef = ref();
const approveData = reactive({
  id: undefined,
  result: 'approved',
  comment: ''
});

const approveRules = {
  result: [{ required: true, message: '请选择审批结果', type: 'error' }],
  comment: [{ required: true, message: '请输入审批意见', type: 'error' }]
};

// 表单验证规则
const rules = {
  type: [{ required: true, message: '请选择收支类型', type: 'error' }],
  category: [{ required: true, message: '请选择收支科目', type: 'error' }],
  amount: [{ required: true, message: '请输入交易金额', type: 'error' }],
  transactionDate: [{ required: true, message: '请选择交易日期', type: 'error' }],
  accountId: [{ required: true, message: '请选择收支账户', type: 'error' }]
};

// 搜索
const handleSearch = () => {
  MessagePlugin.success('查询成功');
  loadData();
};

const handleReset = () => {
  Object.assign(searchForm, {
    type: undefined,
    category: undefined,
    accountId: undefined,
    minAmount: undefined,
    maxAmount: undefined,
    dateRange: []
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

// 新增收支
const handleAdd = () => {
  dialogTitle.value = '新增收支';
  Object.assign(formData, {
    id: undefined,
    type: 'income',
    category: undefined,
    amount: undefined,
    transactionDate: undefined,
    accountId: undefined,
    relatedId: undefined,
    handlerId: undefined,
    remark: '',
    attachments: []
  });
  dialogVisible.value = true;
};

// 新增收入
const handleAddIncome = () => {
  dialogTitle.value = '新增收入';
  Object.assign(formData, {
    id: undefined,
    type: 'income',
    category: undefined,
    amount: undefined,
    transactionDate: undefined,
    accountId: undefined,
    relatedId: undefined,
    handlerId: undefined,
    remark: '',
    attachments: []
  });
  dialogVisible.value = true;
};

// 新增支出
const handleAddExpense = () => {
  dialogTitle.value = '新增支出';
  Object.assign(formData, {
    id: undefined,
    type: 'expense',
    category: undefined,
    amount: undefined,
    transactionDate: undefined,
    accountId: undefined,
    relatedId: undefined,
    handlerId: undefined,
    remark: '',
    attachments: []
  });
  dialogVisible.value = true;
};

// 编辑
const handleEdit = (row: any) => {
  dialogTitle.value = '编辑收支';
  Object.assign(formData, {
    ...row,
    attachments: row.attachments || []
  });
  dialogVisible.value = true;
};

// 查看
const handleView = (row: any) => {
  detailData.value = { ...row };
  detailVisible.value = true;
};

// 审批
const handleApprove = (row: any) => {
  Object.assign(approveData, {
    id: row.id,
    result: 'approved',
    comment: ''
  });
  approveVisible.value = true;
};

const handleApproveSubmit = async ({ validateResult }: any) => {
  if (validateResult === true) {
    MessagePlugin.success('审批成功');
    approveVisible.value = false;
    loadData();
  }
};

// 删除
const handleDelete = async (row: any) => {
  const dialog = await DialogPlugin.confirm({
    header: '确认删除',
    body: `确定要删除收支单号 ${row.voucherNo} 的记录吗？`,
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
.income-expense-container {
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
