<template>
  <div class="customer-container">
    <!-- 统计卡片 -->
    <t-row :gutter="16" class="statistics-cards">
      <t-col :span="6">
        <t-card>
          <t-statistic title="客户总数" :value="stats.totalCount" />
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card>
          <t-statistic title="合作中客户" :value="stats.activeCount" />
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card>
          <t-statistic title="本月销售额(元)" :value="stats.monthSales" :precision="2" />
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card>
          <t-statistic title="待审核客户" :value="stats.pendingCount" />
        </t-card>
      </t-col>
    </t-row>

    <!-- 搜索卡片 -->
    <t-card class="search-card" title="查询条件">
      <t-form :data="searchForm" layout="inline" @submit="handleSearch" @reset="handleReset">
        <t-form-item label="客户名称" name="name">
          <t-input v-model="searchForm.name" placeholder="请输入客户名称" clearable />
        </t-form-item>
        <t-form-item label="客户编码" name="code">
          <t-input v-model="searchForm.code" placeholder="请输入客户编码" clearable />
        </t-form-item>
        <t-form-item label="客户类型" name="type">
          <t-select v-model="searchForm.type" placeholder="请选择类型" clearable>
            <t-option value="enterprise" label="企业客户" />
            <t-option value="individual" label="个人客户" />
            <t-option value="government" label="政府客户" />
            <t-option value="other" label="其他" />
          </t-select>
        </t-form-item>
        <t-form-item label="合作状态" name="status">
          <t-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <t-option value="pending" label="待审核" />
            <t-option value="active" label="合作中" />
            <t-option value="inactive" label="已暂停" />
            <t-option value="terminated" label="已终止" />
          </t-select>
        </t-form-item>
        <t-form-item label="客户等级" name="level">
          <t-select v-model="searchForm.level" placeholder="请选择等级" clearable>
            <t-option value="A" label="A级" />
            <t-option value="B" label="B级" />
            <t-option value="C" label="C级" />
            <t-option value="D" label="D级" />
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
          新增客户
        </t-button>
        <t-button theme="default" @click="handleBatchDelete">
          <template #icon><delete-icon /></template>
          批量删除
        </t-button>
        <t-button theme="default" @click="handleImport">
          <template #icon><upload-icon /></template>
          批量导入
        </t-button>
        <t-button theme="default" @click="handleExport">
          <template #icon><download-icon /></template>
          导出客户
        </t-button>
      </t-space>
    </t-card>

    <!-- 客户列表 -->
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
          <t-tag v-if="row.status === 'pending'" theme="warning">待审核</t-tag>
          <t-tag v-else-if="row.status === 'active'" theme="success">合作中</t-tag>
          <t-tag v-else-if="row.status === 'inactive'" theme="default">已暂停</t-tag>
          <t-tag v-else theme="danger">已终止</t-tag>
        </template>
        <template #level="{ row }">
          <t-tag v-if="row.level === 'A'" theme="success">A级</t-tag>
          <t-tag v-else-if="row.level === 'B'" theme="primary">B级</t-tag>
          <t-tag v-else-if="row.level === 'C'" theme="warning">C级</t-tag>
          <t-tag v-else theme="danger">D级</t-tag>
        </template>
        <template #operation="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleView(row)">查看</t-link>
            <t-link v-if="row.status === 'pending'" theme="primary" @click="handleApprove(row)">审核</t-link>
            <t-link theme="primary" @click="handleEdit(row)">编辑</t-link>
            <t-link theme="primary" @click="handleOrder(row)">下单</t-link>
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
            <t-row :gutter="16">
              <t-col :span="12">
                <t-form-item label="客户名称" name="name">
                  <t-input v-model="formData.name" placeholder="请输入客户名称" />
                </t-form-item>
              </t-col>
              <t-col :span="12">
                <t-form-item label="客户编码" name="code">
                  <t-input v-model="formData.code" placeholder="请输入客户编码" />
                </t-form-item>
              </t-col>
            </t-row>
            <t-row :gutter="16">
              <t-col :span="12">
                <t-form-item label="客户类型" name="type">
                  <t-select v-model="formData.type" placeholder="请选择类型" clearable>
                    <t-option value="enterprise" label="企业客户" />
                    <t-option value="individual" label="个人客户" />
                    <t-option value="government" label="政府客户" />
                    <t-option value="other" label="其他" />
                  </t-select>
                </t-form-item>
              </t-col>
              <t-col :span="12">
                <t-form-item label="客户等级" name="level">
                  <t-radio-group v-model="formData.level">
                    <t-radio value="A">A级</t-radio>
                    <t-radio value="B">B级</t-radio>
                    <t-radio value="C">C级</t-radio>
                    <t-radio value="D">D级</t-radio>
                  </t-radio-group>
                </t-form-item>
              </t-col>
            </t-row>
            <t-form-item label="客户地址" name="address">
              <t-input v-model="formData.address" placeholder="请输入客户地址" />
            </t-form-item>
          </t-tab-panel>
          <t-tab-panel value="contact" label="联系信息">
            <t-row :gutter="16">
              <t-col :span="12">
                <t-form-item label="联系人" name="contactName">
                  <t-input v-model="formData.contactName" placeholder="请输入联系人" />
                </t-form-item>
              </t-col>
              <t-col :span="12">
                <t-form-item label="联系电话" name="contactPhone">
                  <t-input v-model="formData.contactPhone" placeholder="请输入联系电话" />
                </t-form-item>
              </t-col>
            </t-row>
            <t-row :gutter="16">
              <t-col :span="12">
                <t-form-item label="邮箱" name="email">
                  <t-input v-model="formData.email" placeholder="请输入邮箱" />
                </t-form-item>
              </t-col>
              <t-col :span="12">
                <t-form-item label="传真" name="fax">
                  <t-input v-model="formData.fax" placeholder="请输入传真" />
                </t-form-item>
              </t-col>
            </t-row>
            <t-row :gutter="16">
              <t-col :span="12">
                <t-form-item label="开户银行" name="bankName">
                  <t-input v-model="formData.bankName" placeholder="请输入开户银行" />
                </t-form-item>
              </t-col>
              <t-col :span="12">
                <t-form-item label="银行账号" name="bankAccount">
                  <t-input v-model="formData.bankAccount" placeholder="请输入银行账号" />
                </t-form-item>
              </t-col>
            </t-row>
          </t-tab-panel>
          <t-tab-panel value="business" label="业务信息">
            <t-form-item label="主营产品" name="mainProduct">
              <t-input v-model="formData.mainProduct" placeholder="请输入主营产品" />
            </t-form-item>
            <t-row :gutter="16">
              <t-col :span="12">
                <t-form-item label="信用额度(元)" name="creditLimit">
                  <t-input-number v-model="formData.creditLimit" placeholder="请输入信用额度" :min="0" :precision="2" />
                </t-form-item>
              </t-col>
              <t-col :span="12">
                <t-form-item label="已用额度(元)" name="usedCredit">
                  <t-input-number v-model="formData.usedCredit" placeholder="请输入已用额度" :min="0" :precision="2" />
                </t-form-item>
              </t-col>
            </t-row>
            <t-form-item label="付款方式" name="paymentMethod">
              <t-select v-model="formData.paymentMethod" placeholder="请选择付款方式" clearable>
                <t-option value="cash" label="现金" />
                <t-option value="bank" label="银行转账" />
                <t-option value="credit" label="账期支付" />
                <t-option value="other" label="其他" />
              </t-select>
            </t-form-item>
            <t-form-item label="付款周期(天)" name="paymentCycle">
              <t-input-number v-model="formData.paymentCycle" placeholder="请输入付款周期" :min="0" />
            </t-form-item>
          </t-tab-panel>
          <t-tab-panel value="remark" label="备注信息">
            <t-form-item label="备注" name="remark">
              <t-textarea v-model="formData.remark" placeholder="请输入备注" :maxlength="500" />
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
    <t-dialog v-model:visible="detailVisible" header="客户详情" width="900px" :footer="false">
      <t-card>
        <template #header>
          <t-space>
            <span>{{ detailData.name }}</span>
            <t-tag v-if="detailData.status === 'pending'" theme="warning">待审核</t-tag>
            <t-tag v-else-if="detailData.status === 'active'" theme="success">合作中</t-tag>
            <t-tag v-else-if="detailData.status === 'inactive'" theme="default">已暂停</t-tag>
            <t-tag v-else theme="danger">已终止</t-tag>
          </t-space>
        </template>
      </t-card>
      <t-descriptions bordered :column="2" style="margin-top: 16px;">
        <t-descriptions-item label="客户编码">{{ detailData.code }}</t-descriptions-item>
        <t-descriptions-item label="客户类型">{{ detailData.typeName }}</t-descriptions-item>
        <t-descriptions-item label="客户等级">
          <t-tag v-if="detailData.level === 'A'" theme="success">A级</t-tag>
          <t-tag v-else-if="detailData.level === 'B'" theme="primary">B级</t-tag>
          <t-tag v-else-if="detailData.level === 'C'" theme="warning">C级</t-tag>
          <t-tag v-else theme="danger">D级</t-tag>
        </t-descriptions-item>
        <t-descriptions-item label="地址">{{ detailData.address }}</t-descriptions-item>
        <t-descriptions-item label="联系人">{{ detailData.contactName }}</t-descriptions-item>
        <t-descriptions-item label="联系电话">{{ detailData.contactPhone }}</t-descriptions-item>
        <t-descriptions-item label="邮箱">{{ detailData.email }}</t-descriptions-item>
        <t-descriptions-item label="传真">{{ detailData.fax }}</t-descriptions-item>
        <t-descriptions-item label="开户银行">{{ detailData.bankName }}</t-descriptions-item>
        <t-descriptions-item label="银行账号">{{ detailData.bankAccount }}</t-descriptions-item>
        <t-descriptions-item label="主营产品">{{ detailData.mainProduct }}</t-descriptions-item>
        <t-descriptions-item label="信用额度">{{ detailData.creditLimit?.toFixed(2) }}元</t-descriptions-item>
        <t-descriptions-item label="已用额度">{{ detailData.usedCredit?.toFixed(2) }}元</t-descriptions-item>
        <t-descriptions-item label="可用额度">{{ (detailData.creditLimit - detailData.usedCredit)?.toFixed(2) }}元</t-descriptions-item>
        <t-descriptions-item label="付款方式">{{ detailData.paymentMethodName }}</t-descriptions-item>
        <t-descriptions-item label="付款周期">{{ detailData.paymentCycle }}天</t-descriptions-item>
        <t-descriptions-item label="创建时间">{{ detailData.createTime }}</t-descriptions-item>
        <t-descriptions-item label="更新时间">{{ detailData.updateTime }}</t-descriptions-item>
        <t-descriptions-item label="备注" :span="2">{{ detailData.remark }}</t-descriptions-item>
      </t-descriptions>
      <t-divider>销售记录</t-divider>
      <t-table :data="detailData.salesRecords" :columns="salesColumns" row-key="id" />
    </t-dialog>

    <!-- 审核弹窗 -->
    <t-dialog v-model:visible="approveVisible" header="客户审核" width="600px">
      <t-form ref="approveFormRef" :data="approveData" :rules="approveRules" label-width="120px">
        <t-form-item label="审核结果" name="result">
          <t-radio-group v-model="approveData.result">
            <t-radio value="approved">通过</t-radio>
            <t-radio value="rejected">拒绝</t-radio>
          </t-radio-group>
        </t-form-item>
        <t-form-item label="客户等级" name="level">
          <t-radio-group v-model="approveData.level">
            <t-radio value="A">A级</t-radio>
            <t-radio value="B">B级</t-radio>
            <t-radio value="C">C级</t-radio>
            <t-radio value="D">D级</t-radio>
          </t-radio-group>
        </t-form-item>
        <t-form-item label="审核意见" name="comment">
          <t-textarea v-model="approveData.comment" placeholder="请输入审核意见" :maxlength="500" />
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
import { AddIcon, DeleteIcon, DownloadIcon, UploadIcon } from 'tdesign-icons-vue-next';

// 统计数据
const stats = ref({
  totalCount: 128,
  activeCount: 105,
  monthSales: 485000.00,
  pendingCount: 5
});

// 搜索表单
const searchForm = reactive({
  name: '',
  code: '',
  type: undefined,
  status: undefined,
  level: undefined
});

// 表格数据
const loading = ref(false);
const tableData = ref([
  {
    id: 1,
    name: '深圳科技有限公司',
    code: 'CUS001',
    type: 'enterprise',
    typeName: '企业客户',
    address: '深圳市南山区科技园',
    contactName: '张经理',
    contactPhone: '13800138001',
    email: 'zhang@sz-tech.com',
    fax: '0755-12345678',
    bankName: '中国工商银行',
    bankAccount: '6222021234567890',
    mainProduct: '电子产品、数码配件',
    creditLimit: 500000.00,
    usedCredit: 280000.00,
    paymentMethod: 'credit',
    paymentMethodName: '账期支付',
    paymentCycle: 30,
    level: 'A',
    status: 'active',
    remark: '长期合作客户',
    attachments: [],
    createTime: '2025-01-15 10:30:00',
    updateTime: '2026-02-19 16:45:00',
    salesRecords: [
      { id: 1, orderNo: 'SO20260218001', date: '2026-02-18', amount: 120000, status: 'completed' },
      { id: 2, orderNo: 'SO20260210001', date: '2026-02-10', amount: 160000, status: 'completed' }
    ]
  },
  {
    id: 2,
    name: '北京贸易有限公司',
    code: 'CUS002',
    type: 'enterprise',
    typeName: '企业客户',
    address: '北京市朝阳区国贸大厦',
    contactName: '李经理',
    contactPhone: '13900139002',
    email: 'li@bj-trade.com',
    fax: '010-12345678',
    bankName: '中国建设银行',
    bankAccount: '6217009876543210',
    mainProduct: '办公用品、日用品',
    creditLimit: 300000.00,
    usedCredit: 150000.00,
    paymentMethod: 'bank',
    paymentMethodName: '银行转账',
    paymentCycle: 15,
    level: 'B',
    status: 'active',
    remark: '优质客户',
    attachments: [],
    createTime: '2025-03-20 14:20:00',
    updateTime: '2026-02-19 09:15:00',
    salesRecords: []
  },
  {
    id: 3,
    name: '广州电子商务公司',
    code: 'CUS003',
    type: 'enterprise',
    typeName: '企业客户',
    address: '广州市天河区珠江新城',
    contactName: '王经理',
    contactPhone: '13700137003',
    email: 'wang@gz-ecommerce.com',
    fax: '020-12345678',
    bankName: '招商银行',
    bankAccount: '6225881234567890',
    mainProduct: '电子商务、零售',
    creditLimit: 200000.00,
    usedCredit: 0,
    paymentMethod: 'credit',
    paymentMethodName: '账期支付',
    paymentCycle: 45,
    level: 'C',
    status: 'pending',
    remark: '新客户待审核',
    attachments: [],
    createTime: '2026-02-19 10:00:00',
    updateTime: '2026-02-19 10:00:00',
    salesRecords: []
  }
]);

const selectedRowKeys = ref<number[]>([]);

// 表格列
const columns = [
  { colKey: 'code', title: '客户编码', width: 140 },
  { colKey: 'name', title: '客户名称', width: 200 },
  { colKey: 'typeName', title: '客户类型', width: 150 },
  { colKey: 'contactName', title: '联系人', width: 100 },
  { colKey: 'contactPhone', title: '联系电话', width: 130 },
  { colKey: 'level', title: '评级', width: 100 },
  { colKey: 'status', title: '状态', width: 100 },
  { colKey: 'createTime', title: '创建时间', width: 170 },
  { colKey: 'operation', title: '操作', width: 260, fixed: 'right' }
];

// 销售记录列
const salesColumns = [
  { colKey: 'orderNo', title: '销售单号', width: 160 },
  { colKey: 'date', title: '销售日期', width: 120 },
  { colKey: 'amount', title: '销售金额(元)', width: 140, cell: (h, { row }) => row.amount?.toFixed(2) },
  { colKey: 'status', title: '状态', width: 100, cell: (h, { row }) => {
    return h('t-tag', { theme: row.status === 'completed' ? 'success' : 'default' }, row.status === 'completed' ? '已完成' : '进行中');
  }}
];

// 分页
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 128
});

// 弹窗
const dialogVisible = ref(false);
const dialogTitle = ref('');
const detailVisible = ref(false);
const approveVisible = ref(false);
const activeTab = ref('basic');

// 表单
const formRef = ref();
const formData = reactive({
  id: undefined,
  name: '',
  code: '',
  type: 'enterprise',
  level: 'B',
  address: '',
  contactName: '',
  contactPhone: '',
  email: '',
  fax: '',
  bankName: '',
  bankAccount: '',
  mainProduct: '',
  creditLimit: 0,
  usedCredit: 0,
  paymentMethod: 'bank',
  paymentCycle: 30,
  remark: '',
  attachments: []
});

// 详情数据
const detailData = ref({});

// 审核表单
const approveFormRef = ref();
const approveData = reactive({
  id: undefined,
  result: 'approved',
  level: 'B',
  comment: ''
});

const approveRules = {
  result: [{ required: true, message: '请选择审核结果', type: 'error' }],
  level: [{ required: true, message: '请选择客户等级', type: 'error' }],
  comment: [{ required: true, message: '请输入审核意见', type: 'error' }]
};

// 表单验证规则
const rules = {
  name: [{ required: true, message: '请输入客户名称', type: 'error' }],
  code: [{ required: true, message: '请输入客户编码', type: 'error' }],
  type: [{ required: true, message: '请选择客户类型', type: 'error' }],
  level: [{ required: true, message: '请选择客户等级', type: 'error' }]
};

// 搜索
const handleSearch = () => {
  MessagePlugin.success('查询成功');
  loadData();
};

const handleReset = () => {
  Object.assign(searchForm, {
    name: '',
    code: '',
    type: undefined,
    status: undefined,
    level: undefined
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
  dialogTitle.value = '新增客户';
  activeTab.value = 'basic';
  Object.assign(formData, {
    id: undefined,
    name: '',
    code: '',
    type: 'enterprise',
    level: 'B',
    address: '',
    contactName: '',
    contactPhone: '',
    email: '',
    fax: '',
    bankName: '',
    bankAccount: '',
    mainProduct: '',
    creditLimit: 0,
    usedCredit: 0,
    paymentMethod: 'bank',
    paymentCycle: 30,
    remark: '',
    attachments: []
  });
  dialogVisible.value = true;
};

// 编辑
const handleEdit = (row: any) => {
  dialogTitle.value = '编辑客户';
  activeTab.value = 'basic';
  Object.assign(formData, row);
  dialogVisible.value = true;
};

// 查看
const handleView = (row: any) => {
  detailData.value = { ...row };
  detailVisible.value = true;
};

// 审核
const handleApprove = (row: any) => {
  Object.assign(approveData, {
    id: row.id,
    result: 'approved',
    level: 'B',
    comment: ''
  });
  approveVisible.value = true;
};

const handleApproveSubmit = async ({ validateResult }: any) => {
  if (validateResult === true) {
    MessagePlugin.success('审核成功');
    approveVisible.value = false;
    loadData();
  }
};

// 下单
const handleOrder = (row: any) => {
  MessagePlugin.info(`跳转到客户 ${row.name} 的下单页面`);
};

// 删除
const handleDelete = async (row: any) => {
  const dialog = await DialogPlugin.confirm({
    header: '确认删除',
    body: `确定要删除客户 ${row.name} 吗？`,
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

// 批量导入
const handleImport = () => {
  MessagePlugin.info('请上传Excel文件进行批量导入');
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
.customer-container {
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
