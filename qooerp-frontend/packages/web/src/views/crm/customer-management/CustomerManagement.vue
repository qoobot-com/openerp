<template>
  <div class="customer-management-container">
    <!-- 统计卡片 -->
    <t-row :gutter="16" class="mb-4">
      <t-col :span="6">
        <t-card theme="primary">
          <t-statistic title="客户总数" :value="statistics.total" :loading="loading">
            <template #prefix><icon-user /></template>
          </t-statistic>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="success">
          <t-statistic title="活跃客户" :value="statistics.active" :loading="loading">
            <template #prefix><icon-check-circle /></template>
          </t-statistic>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="warning">
          <t-statistic title="潜在客户" :value="statistics.potential" :loading="loading">
            <template #prefix><icon-time /></template>
          </t-statistic>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="danger">
          <t-statistic title="停用客户" :value="statistics.inactive" :loading="loading">
            <template #prefix><icon-close-circle /></template>
          </t-statistic>
        </t-card>
      </t-col>
    </t-row>

    <!-- 搜索卡片 -->
    <t-card class="mb-4">
      <t-form :data="searchForm" layout="inline" @submit="handleSearch">
        <t-form-item label="客户编号" name="customerCode">
          <t-input v-model="searchForm.customerCode" placeholder="请输入客户编号" clearable />
        </t-form-item>
        <t-form-item label="客户名称" name="customerName">
          <t-input v-model="searchForm.customerName" placeholder="请输入客户名称" clearable />
        </t-form-item>
        <t-form-item label="客户类型" name="customerType">
          <t-select v-model="searchForm.customerType" placeholder="请选择客户类型" clearable>
            <t-option value="企业客户" label="企业客户" />
            <t-option value="个人客户" label="个人客户" />
            <t-option value="政府客户" label="政府客户" />
            <t-option value="其他" label="其他" />
          </t-select>
        </t-form-item>
        <t-form-item label="客户状态" name="status">
          <t-select v-model="searchForm.status" placeholder="请选择客户状态" clearable>
            <t-option value="active" label="活跃" />
            <t-option value="potential" label="潜在" />
            <t-option value="inactive" label="停用" />
          </t-select>
        </t-form-item>
        <t-form-item label="销售负责人" name="salesPerson">
          <t-input v-model="searchForm.salesPerson" placeholder="请输入销售负责人" clearable />
        </t-form-item>
        <t-form-item>
          <t-button theme="primary" type="submit">查询</t-button>
          <t-button theme="default" @click="handleReset">重置</t-button>
        </t-form-item>
      </t-form>
    </t-card>

    <!-- 操作栏 -->
    <div class="mb-4 flex justify-between items-center">
      <div>
        <t-button theme="primary" @click="handleAdd">
          <template #icon><icon-add /></template>
          新增客户
        </t-button>
        <t-button theme="default" @click="handleBatchImport">
          <template #icon><icon-upload /></template>
          批量导入
        </t-button>
        <t-button theme="default" @click="handleExport">
          <template #icon><icon-download /></template>
          导出报表
        </t-button>
      </div>
      <t-button theme="default" variant="outline" @click="handleRefresh">
        <template #icon><icon-refresh /></template>
        刷新
      </t-button>
    </div>

    <!-- 客户列表 -->
    <t-card>
      <t-table
        :data="tableData"
        :columns="columns"
        :loading="loading"
        :pagination="pagination"
        row-key="id"
        :selected-row-keys="selectedRowKeys"
        @select-change="handleSelectChange"
        @page-change="handlePageChange"
      >
        <template #status="{ row }">
          <t-tag v-if="row.status === 'active'" theme="success" variant="light">活跃</t-tag>
          <t-tag v-else-if="row.status === 'potential'" theme="warning" variant="light">潜在</t-tag>
          <t-tag v-else-if="row.status === 'inactive'" theme="danger" variant="light">停用</t-tag>
        </template>
        <template #customerLevel="{ row }">
          <t-tag v-if="row.customerLevel === 'A'" theme="danger" variant="light">A级</t-tag>
          <t-tag v-else-if="row.customerLevel === 'B'" theme="warning" variant="light">B级</t-tag>
          <t-tag v-else-if="row.customerLevel === 'C'" theme="primary" variant="light">C级</t-tag>
          <t-tag v-else theme="default" variant="light">D级</t-tag>
        </template>
        <template #operation="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleView(row)">查看</t-link>
            <t-link theme="primary" @click="handleEdit(row)">编辑</t-link>
            <t-link theme="primary" @click="handleFollowUp(row)">跟进</t-link>
            <t-popconfirm content="确定要删除此客户吗？" @confirm="handleDelete(row)">
              <t-link theme="danger">删除</t-link>
            </t-popconfirm>
          </t-space>
        </template>
      </t-table>
    </t-card>

    <!-- 新增/编辑客户弹窗 -->
    <t-dialog
      v-model:visible="dialogVisible"
      :header="dialogTitle"
      width="900px"
      :footer="false"
      @close="handleDialogClose"
    >
      <t-tabs v-model="activeTab" theme="card">
        <!-- 基本信息 -->
        <t-tab-panel value="basic" label="基本信息">
          <t-form :data="formData" ref="formRef" :rules="rules" label-width="120px">
            <t-row :gutter="16">
              <t-col :span="12">
                <t-form-item label="客户编号" name="customerCode">
                  <t-input v-model="formData.customerCode" placeholder="自动生成" disabled />
                </t-form-item>
              </t-col>
              <t-col :span="12">
                <t-form-item label="客户名称" name="customerName">
                  <t-input v-model="formData.customerName" placeholder="请输入客户名称" />
                </t-form-item>
              </t-col>
            </t-row>
            <t-row :gutter="16">
              <t-col :span="12">
                <t-form-item label="客户类型" name="customerType">
                  <t-select v-model="formData.customerType" placeholder="请选择客户类型">
                    <t-option value="企业客户" label="企业客户" />
                    <t-option value="个人客户" label="个人客户" />
                    <t-option value="政府客户" label="政府客户" />
                    <t-option value="其他" label="其他" />
                  </t-select>
                </t-form-item>
              </t-col>
              <t-col :span="12">
                <t-form-item label="客户级别" name="customerLevel">
                  <t-select v-model="formData.customerLevel" placeholder="请选择客户级别">
                    <t-option value="A" label="A级" />
                    <t-option value="B" label="B级" />
                    <t-option value="C" label="C级" />
                    <t-option value="D" label="D级" />
                  </t-select>
                </t-form-item>
              </t-col>
            </t-row>
            <t-row :gutter="16">
              <t-col :span="12">
                <t-form-item label="所属行业" name="industry">
                  <t-select v-model="formData.industry" placeholder="请选择所属行业">
                    <t-option value="制造业" label="制造业" />
                    <t-option value="IT/互联网" label="IT/互联网" />
                    <t-option value="金融" label="金融" />
                    <t-option value="零售" label="零售" />
                    <t-option value="其他" label="其他" />
                  </t-select>
                </t-form-item>
              </t-col>
              <t-col :span="12">
                <t-form-item label="客户来源" name="source">
                  <t-select v-model="formData.source" placeholder="请选择客户来源">
                    <t-option value="市场推广" label="市场推广" />
                    <t-option value="客户介绍" label="客户介绍" />
                    <t-option value="线上获客" label="线上获客" />
                    <t-option value="电话营销" label="电话营销" />
                    <t-option value="其他" label="其他" />
                  </t-select>
                </t-form-item>
              </t-col>
            </t-row>
            <t-row :gutter="16">
              <t-col :span="12">
                <t-form-item label="销售负责人" name="salesPerson">
                  <t-input v-model="formData.salesPerson" placeholder="请输入销售负责人" />
                </t-form-item>
              </t-col>
              <t-col :span="12">
                <t-form-item label="客户状态" name="status">
                  <t-select v-model="formData.status" placeholder="请选择客户状态">
                    <t-option value="active" label="活跃" />
                    <t-option value="potential" label="潜在" />
                    <t-option value="inactive" label="停用" />
                  </t-select>
                </t-form-item>
              </t-col>
            </t-row>
            <t-form-item label="客户地址" name="address">
              <t-input v-model="formData.address" placeholder="请输入客户地址" />
            </t-form-item>
            <t-form-item label="备注" name="remark">
              <t-textarea v-model="formData.remark" placeholder="请输入备注" :maxlength="500" />
            </t-form-item>
          </t-form>
        </t-tab-panel>

        <!-- 联系信息 -->
        <t-tab-panel value="contact" label="联系信息">
          <t-form :data="formData" label-width="120px">
            <t-form-item label="联系人" name="contactPerson">
              <t-input v-model="formData.contactPerson" placeholder="请输入联系人" />
            </t-form-item>
            <t-form-item label="联系电话" name="contactPhone">
              <t-input v-model="formData.contactPhone" placeholder="请输入联系电话" />
            </t-form-item>
            <t-form-item label="邮箱" name="email">
              <t-input v-model="formData.email" placeholder="请输入邮箱" />
            </t-form-item>
            <t-form-item label="网站" name="website">
              <t-input v-model="formData.website" placeholder="请输入网站" />
            </t-form-item>
          </t-form>
        </t-tab-panel>

        <!-- 财务信息 -->
        <t-tab-panel value="finance" label="财务信息">
          <t-form :data="formData" label-width="120px">
            <t-form-item label="信用额度" name="creditLimit">
              <t-input-number v-model="formData.creditLimit" placeholder="请输入信用额度" :min="0" />
            </t-form-item>
            <t-form-item label="账期(天)" name="paymentTerm">
              <t-input-number v-model="formData.paymentTerm" placeholder="请输入账期" :min="0" />
            </t-form-item>
            <t-form-item label="开票信息" name="invoiceInfo">
              <t-textarea v-model="formData.invoiceInfo" placeholder="请输入开票信息" />
            </t-form-item>
            <t-form-item label="银行账号" name="bankAccount">
              <t-input v-model="formData.bankAccount" placeholder="请输入银行账号" />
            </t-form-item>
          </t-form>
        </t-tab-panel>
      </t-tabs>

      <div class="dialog-footer">
        <t-button theme="default" @click="dialogVisible = false">取消</t-button>
        <t-button theme="primary" @click="handleSave">保存</t-button>
      </div>
    </t-dialog>

    <!-- 客户详情弹窗 -->
    <t-dialog
      v-model:visible="detailVisible"
      header="客户详情"
      width="900px"
      :footer="false"
    >
      <t-tabs v-model="detailActiveTab" theme="card">
        <t-tab-panel value="basic" label="基本信息">
          <t-descriptions :column="2" bordered>
            <t-descriptions-item label="客户编号">{{ detailData.customerCode }}</t-descriptions-item>
            <t-descriptions-item label="客户名称">{{ detailData.customerName }}</t-descriptions-item>
            <t-descriptions-item label="客户类型">{{ detailData.customerType }}</t-descriptions-item>
            <t-descriptions-item label="客户级别">{{ detailData.customerLevel }}</t-descriptions-item>
            <t-descriptions-item label="所属行业">{{ detailData.industry }}</t-descriptions-item>
            <t-descriptions-item label="客户来源">{{ detailData.source }}</t-descriptions-item>
            <t-descriptions-item label="销售负责人">{{ detailData.salesPerson }}</t-descriptions-item>
            <t-descriptions-item label="客户状态">
              <t-tag v-if="detailData.status === 'active'" theme="success">活跃</t-tag>
              <t-tag v-else-if="detailData.status === 'potential'" theme="warning">潜在</t-tag>
              <t-tag v-else theme="danger">停用</t-tag>
            </t-descriptions-item>
            <t-descriptions-item label="创建时间" :span="2">{{ detailData.createTime }}</t-descriptions-item>
            <t-descriptions-item label="客户地址" :span="2">{{ detailData.address }}</t-descriptions-item>
            <t-descriptions-item label="备注" :span="2">{{ detailData.remark }}</t-descriptions-item>
          </t-descriptions>
        </t-tab-panel>

        <t-tab-panel value="contact" label="联系信息">
          <t-descriptions :column="2" bordered>
            <t-descriptions-item label="联系人">{{ detailData.contactPerson }}</t-descriptions-item>
            <t-descriptions-item label="联系电话">{{ detailData.contactPhone }}</t-descriptions-item>
            <t-descriptions-item label="邮箱">{{ detailData.email }}</t-descriptions-item>
            <t-descriptions-item label="网站">{{ detailData.website }}</t-descriptions-item>
          </t-descriptions>
        </t-tab-panel>

        <t-tab-panel value="finance" label="财务信息">
          <t-descriptions :column="2" bordered>
            <t-descriptions-item label="信用额度">{{ detailData.creditLimit }}</t-descriptions-item>
            <t-descriptions-item label="账期">{{ detailData.paymentTerm }}天</t-descriptions-item>
            <t-descriptions-item label="开票信息" :span="2">{{ detailData.invoiceInfo }}</t-descriptions-item>
            <t-descriptions-item label="银行账号" :span="2">{{ detailData.bankAccount }}</t-descriptions-item>
          </t-descriptions>
        </t-tab-panel>

        <t-tab-panel value="logs" label="操作日志">
          <t-table :data="detailData.logs || []" :columns="logColumns" :pagination="false" />
        </t-tab-panel>
      </t-tabs>
    </t-dialog>

    <!-- 跟进记录弹窗 -->
    <t-dialog
      v-model:visible="followUpVisible"
      header="添加跟进记录"
      width="600px"
      :footer="false"
    >
      <t-form :data="followUpForm" label-width="100px">
        <t-form-item label="跟进方式" name="type">
          <t-select v-model="followUpForm.type" placeholder="请选择跟进方式">
            <t-option value="电话" label="电话" />
            <t-option value="拜访" label="拜访" />
            <t-option value="邮件" label="邮件" />
            <t-option value="微信" label="微信" />
            <t-option value="其他" label="其他" />
          </t-select>
        </t-form-item>
        <t-form-item label="跟进内容" name="content">
          <t-textarea v-model="followUpForm.content" placeholder="请输入跟进内容" />
        </t-form-item>
        <t-form-item label="下次跟进时间" name="nextTime">
          <t-date-picker v-model="followUpForm.nextTime" placeholder="请选择下次跟进时间" />
        </t-form-item>
      </t-form>
      <div class="dialog-footer">
        <t-button theme="default" @click="followUpVisible = false">取消</t-button>
        <t-button theme="primary" @click="handleSaveFollowUp">保存</t-button>
      </div>
    </t-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { MessagePlugin } from 'tdesign-vue-next';
import {
  IconUser, IconCheckCircle, IconTime, IconCloseCircle,
  IconAdd, IconUpload, IconDownload, IconRefresh
} from 'tdesign-icons-vue-next';

const loading = ref(false);
const dialogVisible = ref(false);
const detailVisible = ref(false);
const followUpVisible = ref(false);
const dialogTitle = ref('新增客户');
const activeTab = ref('basic');
const detailActiveTab = ref('basic');
const formRef = ref();
const selectedRowKeys = ref([]);

const statistics = reactive({
  total: 156,
  active: 89,
  potential: 45,
  inactive: 22
});

const searchForm = reactive({
  customerCode: '',
  customerName: '',
  customerType: '',
  status: '',
  salesPerson: ''
});

const formData = reactive({
  id: '',
  customerCode: '',
  customerName: '',
  customerType: '',
  customerLevel: '',
  industry: '',
  source: '',
  salesPerson: '',
  status: 'active',
  address: '',
  remark: '',
  contactPerson: '',
  contactPhone: '',
  email: '',
  website: '',
  creditLimit: 0,
  paymentTerm: 0,
  invoiceInfo: '',
  bankAccount: ''
});

const followUpForm = reactive({
  customerId: '',
  type: '',
  content: '',
  nextTime: ''
});

const detailData = reactive<any>({});

const pagination = reactive({
  current: 1,
  pageSize: 20,
  total: 156
});

const columns = [
  { colKey: 'customerCode', title: '客户编号', width: 120 },
  { colKey: 'customerName', title: '客户名称', width: 150 },
  { colKey: 'customerType', title: '客户类型', width: 100 },
  { colKey: 'customerLevel', title: '客户级别', width: 80 },
  { colKey: 'industry', title: '所属行业', width: 100 },
  { colKey: 'salesPerson', title: '销售负责人', width: 100 },
  { colKey: 'status', title: '客户状态', width: 80 },
  { colKey: 'createTime', title: '创建时间', width: 120 },
  { colKey: 'operation', title: '操作', width: 180, fixed: 'right' }
];

const logColumns = [
  { colKey: 'operator', title: '操作人', width: 100 },
  { colKey: 'action', title: '操作内容', width: 200 },
  { colKey: 'createTime', title: '操作时间', width: 150 }
];

const rules = {
  customerName: [{ required: true, message: '请输入客户名称', type: 'error' }],
  customerType: [{ required: true, message: '请选择客户类型', type: 'error' }],
  salesPerson: [{ required: true, message: '请输入销售负责人', type: 'error' }]
};

const tableData = ref([
  {
    id: 1,
    customerCode: 'C202600001',
    customerName: '上海科技有限公司',
    customerType: '企业客户',
    customerLevel: 'A',
    industry: 'IT/互联网',
    source: '市场推广',
    salesPerson: '张三',
    status: 'active',
    address: '上海市浦东新区张江高科技园区',
    contactPerson: '李四',
    contactPhone: '13800138000',
    email: 'li4@example.com',
    website: 'www.example.com',
    creditLimit: 1000000,
    paymentTerm: 30,
    createTime: '2026-01-15 10:30:00'
  },
  {
    id: 2,
    customerCode: 'C202600002',
    customerName: '北京贸易公司',
    customerType: '企业客户',
    customerLevel: 'B',
    industry: '零售',
    source: '客户介绍',
    salesPerson: '王五',
    status: 'active',
    address: '北京市朝阳区建国路88号',
    contactPerson: '赵六',
    contactPhone: '13900139000',
    email: 'zhao6@example.com',
    website: 'www.trading.com',
    creditLimit: 500000,
    paymentTerm: 15,
    createTime: '2026-01-20 14:20:00'
  }
]);

const handleSearch = () => {
  MessagePlugin.success('查询成功');
};

const handleReset = () => {
  Object.assign(searchForm, {
    customerCode: '',
    customerName: '',
    customerType: '',
    status: '',
    salesPerson: ''
  });
};

const handleAdd = () => {
  dialogTitle.value = '新增客户';
  dialogVisible.value = true;
  activeTab.value = 'basic';
};

const handleEdit = (row: any) => {
  dialogTitle.value = '编辑客户';
  dialogVisible.value = true;
  activeTab.value = 'basic';
  Object.assign(formData, row);
};

const handleView = (row: any) => {
  detailVisible.value = true;
  detailActiveTab.value = 'basic';
  Object.assign(detailData, row);
  detailData.logs = [
    { operator: '张三', action: '创建客户', createTime: '2026-01-15 10:30:00' },
    { operator: '张三', action: '更新客户信息', createTime: '2026-01-18 09:15:00' }
  ];
};

const handleFollowUp = (row: any) => {
  followUpForm.customerId = row.id;
  followUpVisible.value = true;
};

const handleSaveFollowUp = () => {
  MessagePlugin.success('跟进记录已保存');
  followUpVisible.value = false;
};

const handleDelete = (row: any) => {
  MessagePlugin.success('客户已删除');
};

const handleSave = () => {
  MessagePlugin.success(dialogTitle.value === '新增客户' ? '客户已创建' : '客户已更新');
  dialogVisible.value = false;
};

const handleDialogClose = () => {
  Object.assign(formData, {
    id: '',
    customerCode: '',
    customerName: '',
    customerType: '',
    customerLevel: '',
    industry: '',
    source: '',
    salesPerson: '',
    status: 'active',
    address: '',
    remark: '',
    contactPerson: '',
    contactPhone: '',
    email: '',
    website: '',
    creditLimit: 0,
    paymentTerm: 0,
    invoiceInfo: '',
    bankAccount: ''
  });
};

const handleBatchImport = () => {
  MessagePlugin.info('批量导入功能开发中');
};

const handleExport = () => {
  MessagePlugin.success('报表导出成功');
};

const handleRefresh = () => {
  MessagePlugin.success('数据已刷新');
};

const handleSelectChange = (value: any) => {
  selectedRowKeys.value = value;
};

const handlePageChange = (pageInfo: any) => {
  pagination.current = pageInfo.current;
  pagination.pageSize = pageInfo.pageSize;
};

onMounted(() => {
  console.log('CustomerManagement mounted');
});
</script>

<style scoped lang="less">
.customer-management-container {
  padding: 20px;
}

.mb-4 {
  margin-bottom: 16px;
}

.flex {
  display: flex;
}

.justify-between {
  justify-content: space-between;
}

.items-center {
  align-items: center;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}
</style>
