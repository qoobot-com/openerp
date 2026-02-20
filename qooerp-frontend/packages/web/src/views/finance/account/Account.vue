<template>
  <div class="account-container">
    <!-- 统计卡片 -->
    <t-row :gutter="16" class="statistics-cards">
      <t-col :span="6">
        <t-card>
          <t-statistic title="账户总数" :value="stats.totalCount" />
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card>
          <t-statistic title="账户总余额(元)" :value="stats.totalBalance" :precision="2" />
        </t-card>
      </t-col>
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
    </t-row>

    <!-- 搜索卡片 -->
    <t-card class="search-card" title="查询条件">
      <t-form :data="searchForm" layout="inline" @submit="handleSearch" @reset="handleReset">
        <t-form-item label="账户名称" name="name">
          <t-input v-model="searchForm.name" placeholder="请输入账户名称" clearable />
        </t-form-item>
        <t-form-item label="账户编号" name="accountNo">
          <t-input v-model="searchForm.accountNo" placeholder="请输入账户编号" clearable />
        </t-form-item>
        <t-form-item label="账户类型" name="type">
          <t-select v-model="searchForm.type" placeholder="请选择账户类型" clearable>
            <t-option value="bank" label="银行账户" />
            <t-option value="alipay" label="支付宝" />
            <t-option value="wechat" label="微信" />
            <t-option value="cash" label="现金" />
            <t-option value="other" label="其他" />
          </t-select>
        </t-form-item>
        <t-form-item label="账户状态" name="status">
          <t-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <t-option value="normal" label="正常" />
            <t-option value="frozen" label="冻结" />
            <t-option value="closed" label="已关闭" />
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
          新增账户
        </t-button>
        <t-button theme="default" @click="handleBatchDelete">
          <template #icon><delete-icon /></template>
          批量删除
        </t-button>
        <t-button theme="default" @click="handleExport">
          <template #icon><download-icon /></template>
          导出账户
        </t-button>
      </t-space>
    </t-card>

    <!-- 账户列表 -->
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
          <t-tag v-if="row.type === 'bank'" theme="primary">银行账户</t-tag>
          <t-tag v-else-if="row.type === 'alipay'" theme="success">支付宝</t-tag>
          <t-tag v-else-if="row.type === 'wechat'" theme="warning">微信</t-tag>
          <t-tag v-else-if="row.type === 'cash'" theme="default">现金</t-tag>
          <t-tag v-else>其他</t-tag>
        </template>
        <template #status="{ row }">
          <t-tag v-if="row.status === 'normal'" theme="success">正常</t-tag>
          <t-tag v-else-if="row.status === 'frozen'" theme="danger">冻结</t-tag>
          <t-tag v-else theme="default">已关闭</t-tag>
        </template>
        <template #operation="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleView(row)">查看</t-link>
            <t-link theme="primary" @click="handleEdit(row)">编辑</t-link>
            <t-link v-if="row.status === 'normal'" theme="primary" @click="handleFreeze(row)">冻结</t-link>
            <t-link v-else theme="primary" @click="handleUnfreeze(row)">解冻</t-link>
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
        <t-form-item label="账户名称" name="name">
          <t-input v-model="formData.name" placeholder="请输入账户名称" />
        </t-form-item>
        <t-form-item label="账户类型" name="type">
          <t-select v-model="formData.type" placeholder="请选择账户类型" clearable>
            <t-option value="bank" label="银行账户" />
            <t-option value="alipay" label="支付宝" />
            <t-option value="wechat" label="微信" />
            <t-option value="cash" label="现金" />
            <t-option value="other" label="其他" />
          </t-select>
        </t-form-item>
        <t-form-item label="开户银行" name="bankName">
          <t-input v-model="formData.bankName" placeholder="请输入开户银行" />
        </t-form-item>
        <t-form-item label="账户编号" name="accountNo">
          <t-input v-model="formData.accountNo" placeholder="请输入账户编号" />
        </t-form-item>
        <t-form-item label="持卡人姓名" name="accountHolder">
          <t-input v-model="formData.accountHolder" placeholder="请输入持卡人姓名" />
        </t-form-item>
        <t-form-item label="初始余额(元)" name="balance">
          <t-input-number v-model="formData.balance" placeholder="请输入初始余额" :precision="2" :min="0" />
        </t-form-item>
        <t-form-item label="账户状态" name="status">
          <t-radio-group v-model="formData.status">
            <t-radio value="normal">正常</t-radio>
            <t-radio value="frozen">冻结</t-radio>
          </t-radio-group>
        </t-form-item>
        <t-form-item label="备注" name="remark">
          <t-textarea v-model="formData.remark" placeholder="请输入备注" :maxlength="200" />
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
    <t-dialog v-model:visible="detailVisible" header="账户详情" width="700px" :footer="false">
      <t-card>
        <template #header>
          <t-space align="center">
            <span>账户余额</span>
            <t-statistic :value="detailData.balance" :precision="2" size="large" />
          </t-space>
        </template>
      </t-card>
      <t-descriptions bordered :column="2" style="margin-top: 16px;">
        <t-descriptions-item label="账户名称">{{ detailData.name }}</t-descriptions-item>
        <t-descriptions-item label="账户编号">{{ detailData.accountNo }}</t-descriptions-item>
        <t-descriptions-item label="账户类型">
          <t-tag v-if="detailData.type === 'bank'" theme="primary">银行账户</t-tag>
          <t-tag v-else-if="detailData.type === 'alipay'" theme="success">支付宝</t-tag>
          <t-tag v-else-if="detailData.type === 'wechat'" theme="warning">微信</t-tag>
          <t-tag v-else-if="detailData.type === 'cash'" theme="default">现金</t-tag>
          <t-tag v-else>其他</t-tag>
        </t-descriptions-item>
        <t-descriptions-item label="开户银行">{{ detailData.bankName }}</t-descriptions-item>
        <t-descriptions-item label="持卡人姓名">{{ detailData.accountHolder }}</t-descriptions-item>
        <t-descriptions-item label="账户状态">
          <t-tag v-if="detailData.status === 'normal'" theme="success">正常</t-tag>
          <t-tag v-else-if="detailData.status === 'frozen'" theme="danger">冻结</t-tag>
          <t-tag v-else theme="default">已关闭</t-tag>
        </t-descriptions-item>
        <t-descriptions-item label="创建时间">{{ detailData.createTime }}</t-descriptions-item>
        <t-descriptions-item label="更新时间">{{ detailData.updateTime }}</t-descriptions-item>
        <t-descriptions-item label="备注" :span="2">{{ detailData.remark }}</t-descriptions-item>
      </t-descriptions>
      <t-divider>交易记录</t-divider>
      <t-table :data="detailData.transactions" :columns="transactionColumns" row-key="id" />
    </t-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { MessagePlugin, DialogPlugin } from 'tdesign-vue-next';
import { AddIcon, DeleteIcon, DownloadIcon } from 'tdesign-icons-vue-next';

// 统计数据
const stats = ref({
  totalCount: 8,
  totalBalance: 586500.00,
  monthIncome: 325000.00,
  monthExpense: 186200.00
});

// 搜索表单
const searchForm = reactive({
  name: '',
  accountNo: '',
  type: undefined,
  status: undefined
});

// 表格数据
const loading = ref(false);
const tableData = ref([
  {
    id: 1,
    name: '工商银行基本户',
    type: 'bank',
    bankName: '中国工商银行',
    accountNo: '6222021234567890123',
    accountHolder: '张三',
    balance: 350000.00,
    status: 'normal',
    remark: '公司基本户',
    createTime: '2025-01-15 10:30:00',
    updateTime: '2026-02-18 16:45:00',
    transactions: [
      { id: 1, type: 'income', amount: 100000, date: '2026-02-18', description: '销售收款', balance: 350000 },
      { id: 2, type: 'expense', amount: 50000, date: '20xx-xx-xx', description: '采购付款', balance: 250000 }
    ]
  },
  {
    id: 2,
    name: '建设银行一般户',
    type: 'bank',
    bankName: '中国建设银行',
    accountNo: '6217009876543210987',
    accountHolder: '张三',
    balance: 120000.00,
    status: 'normal',
    remark: '资金周转账户',
    createTime: '2025-03-20 14:20:00',
    updateTime: '2026-02-19 09:15:00',
    transactions: []
  },
  {
    id: 3,
    name: '企业支付宝',
    type: 'alipay',
    bankName: '支付宝',
    accountNo: '2088123456789012',
    accountHolder: '张三',
    balance: 86500.00,
    status: 'normal',
    remark: '线上收款账户',
    createTime: '2025-06-10 11:00:00',
    updateTime: '2026-02-19 10:30:00',
    transactions: []
  },
  {
    id: 4,
    name: '备用金',
    type: 'cash',
    bankName: '',
    accountNo: 'CASH001',
    accountHolder: '李四',
    balance: 30000.00,
    status: 'normal',
    remark: '日常备用金',
    createTime: '2025-01-01 08:00:00',
    updateTime: '2026-02-19 11:00:00',
    transactions: []
  }
]);

const selectedRowKeys = ref<number[]>([]);

// 表格列
const columns = [
  { colKey: 'name', title: '账户名称', width: 180 },
  { colKey: 'accountNo', title: '账户编号', width: 200 },
  { colKey: 'type', title: '账户类型', width: 120 },
  { colKey: 'bankName', title: '开户银行', width: 150 },
  { colKey: 'accountHolder', title: '持卡人', width: 100 },
  { colKey: 'balance', title: '余额(元)', width: 120, cell: (h, { row }) => row.balance?.toFixed(2) },
  { colKey: 'status', title: '状态', width: 100 },
  { colKey: 'operation', title: '操作', width: 260, fixed: 'right' }
];

// 交易记录列
const transactionColumns = [
  { colKey: 'date', title: '日期', width: 120 },
  { colKey: 'type', title: '类型', width: 100, cell: (h, { row }) => {
    return h('t-tag', { theme: row.type === 'income' ? 'success' : 'danger' }, row.type === 'income' ? '收入' : '支出');
  }},
  { colKey: 'description', title: '说明', width: 200 },
  { colKey: 'amount', title: '金额(元)', width: 120, cell: (h, { row }) => {
    const color = row.type === 'income' ? '#00a870' : '#d54941';
    const prefix = row.type === 'income' ? '+' : '-';
    return h('span', { style: { color, fontWeight: 'bold' } }, `${prefix}${row.amount?.toFixed(2)}`);
  }},
  { colKey: 'balance', title: '余额(元)', width: 120, cell: (h, { row }) => row.balance?.toFixed(2) }
];

// 分页
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 8
});

// 弹窗
const dialogVisible = ref(false);
const dialogTitle = ref('');
const detailVisible = ref(false);

// 表单
const formRef = ref();
const formData = reactive({
  id: undefined,
  name: '',
  type: 'bank',
  bankName: '',
  accountNo: '',
  accountHolder: '',
  balance: 0,
  status: 'normal',
  remark: ''
});

// 详情数据
const detailData = ref({});

// 表单验证规则
const rules = {
  name: [{ required: true, message: '请输入账户名称', type: 'error' }],
  type: [{ required: true, message: '请选择账户类型', type: 'error' }],
  accountNo: [{ required: true, message: '请输入账户编号', type: 'error' }],
  accountHolder: [{ required: true, message: '请输入持卡人姓名', type: 'error' }],
  balance: [{ required: true, message: '请输入初始余额', type: 'error' }],
  status: [{ required: true, message: '请选择账户状态', type: 'error' }]
};

// 搜索
const handleSearch = () => {
  MessagePlugin.success('查询成功');
  loadData();
};

const handleReset = () => {
  Object.assign(searchForm, {
    name: '',
    accountNo: '',
    type: undefined,
    status: undefined
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
  dialogTitle.value = '新增账户';
  Object.assign(formData, {
    id: undefined,
    name: '',
    type: 'bank',
    bankName: '',
    accountNo: '',
    accountHolder: '',
    balance: 0,
    status: 'normal',
    remark: ''
  });
  dialogVisible.value = true;
};

// 编辑
const handleEdit = (row: any) => {
  dialogTitle.value = '编辑账户';
  Object.assign(formData, row);
  dialogVisible.value = true;
};

// 查看
const handleView = (row: any) => {
  detailData.value = { ...row };
  detailVisible.value = true;
};

// 冻结
const handleFreeze = async (row: any) => {
  const dialog = await DialogPlugin.confirm({
    header: '确认冻结',
    body: `确定要冻结账户 ${row.name} 吗？`,
    confirmBtn: '确定',
    cancelBtn: '取消'
  });
  if (dialog) {
    MessagePlugin.success('冻结成功');
    loadData();
  }
};

// 解冻
const handleUnfreeze = async (row: any) => {
  const dialog = await DialogPlugin.confirm({
    header: '确认解冻',
    body: `确定要解冻账户 ${row.name} 吗？`,
    confirmBtn: '确定',
    cancelBtn: '取消'
  });
  if (dialog) {
    MessagePlugin.success('解冻成功');
    loadData();
  }
};

// 删除
const handleDelete = async (row: any) => {
  const dialog = await DialogPlugin.confirm({
    header: '确认删除',
    body: `确定要删除账户 ${row.name} 吗？删除后将无法恢复。`,
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
.account-container {
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
