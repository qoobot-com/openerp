<template>
  <div class="contract-management-container">
    <!-- 统计卡片 -->
    <t-row :gutter="16" class="mb-4">
      <t-col :span="6">
        <t-card theme="primary">
          <t-statistic title="合同总数" :value="statistics.total" :loading="loading">
            <template #prefix><icon-file-copy /></template>
          </t-statistic>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="success">
          <t-statistic title="合同总金额" :value="statistics.amount" :loading="loading">
            <template #prefix><icon-money-circle /></template>
          </t-statistic>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="warning">
          <t-statistic title="执行中" :value="statistics.executing" :loading="loading">
            <template #prefix><icon-time /></template>
          </t-statistic>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="danger">
          <t-statistic title="即将到期" :value="statistics.expiring" :loading="loading">
            <template #prefix><icon-error-circle /></template>
          </t-statistic>
        </t-card>
      </t-col>
    </t-row>

    <!-- 搜索卡片 -->
    <t-card class="mb-4">
      <t-form :data="searchForm" layout="inline" @submit="handleSearch">
        <t-form-item label="合同编号" name="contractCode">
          <t-input v-model="searchForm.contractCode" placeholder="请输入合同编号" clearable />
        </t-form-item>
        <t-form-item label="合同名称" name="contractName">
          <t-input v-model="searchForm.contractName" placeholder="请输入合同名称" clearable />
        </t-form-item>
        <t-form-item label="客户名称" name="customerName">
          <t-input v-model="searchForm.customerName" placeholder="请输入客户名称" clearable />
        </t-form-item>
        <t-form-item label="合同状态" name="status">
          <t-select v-model="searchForm.status" placeholder="请选择合同状态" clearable>
            <t-option value="draft" label="草稿" />
            <t-option value="pending" label="待签署" />
            <t-option value="executing" label="执行中" />
            <t-option value="completed" label="已完成" />
            <t-option value="terminated" label="已终止" />
          </t-select>
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
          新增合同
        </t-button>
        <t-button theme="default" @click="handleBatchSign">
          <template #icon><icon-edit /></template>
          批量签署
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

    <!-- 合同列表 -->
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
          <t-tag v-if="row.status === 'draft'" theme="default" variant="light">草稿</t-tag>
          <t-tag v-else-if="row.status === 'pending'" theme="warning" variant="light">待签署</t-tag>
          <t-tag v-else-if="row.status === 'executing'" theme="success" variant="light">执行中</t-tag>
          <t-tag v-else-if="row.status === 'completed'" theme="primary" variant="light">已完成</t-tag>
          <t-tag v-else theme="danger" variant="light">已终止</t-tag>
        </template>
        <template #operation="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleView(row)">查看</t-link>
            <t-link theme="primary" @click="handleEdit(row)">编辑</t-link>
            <t-link theme="success" @click="handleSign(row)">签署</t-link>
            <t-popconfirm content="确定要删除此合同吗？" @confirm="handleDelete(row)">
              <t-link theme="danger">删除</t-link>
            </t-popconfirm>
          </t-space>
        </template>
      </t-table>
    </t-card>

    <!-- 新增/编辑合同弹窗 -->
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
                <t-form-item label="合同编号" name="contractCode">
                  <t-input v-model="formData.contractCode" placeholder="自动生成" disabled />
                </t-form-item>
              </t-col>
              <t-col :span="12">
                <t-form-item label="合同名称" name="contractName">
                  <t-input v-model="formData.contractName" placeholder="请输入合同名称" />
                </t-form-item>
              </t-col>
            </t-row>
            <t-row :gutter="16">
              <t-col :span="12">
                <t-form-item label="关联客户" name="customerId">
                  <t-select v-model="formData.customerId" placeholder="请选择客户" filterable>
                    <t-option v-for="item in customers" :key="item.id" :value="item.id" :label="item.name" />
                  </t-select>
                </t-form-item>
              </t-col>
              <t-col :span="12">
                <t-form-item label="合同类型" name="contractType">
                  <t-select v-model="formData.contractType" placeholder="请选择合同类型">
                    <t-option value="销售合同" label="销售合同" />
                    <t-option value="服务合同" label="服务合同" />
                    <t-option value="采购合同" label="采购合同" />
                    <t-option value="其他" label="其他" />
                  </t-select>
                </t-form-item>
              </t-col>
            </t-row>
            <t-row :gutter="16">
              <t-col :span="12">
                <t-form-item label="合同金额" name="amount">
                  <t-input-number v-model="formData.amount" placeholder="请输入合同金额" :min="0" />
                </t-form-item>
              </t-col>
              <t-col :span="12">
                <t-form-item label="币种" name="currency">
                  <t-select v-model="formData.currency" placeholder="请选择币种">
                    <t-option value="CNY" label="人民币" />
                    <t-option value="USD" label="美元" />
                    <t-option value="EUR" label="欧元" />
                  </t-select>
                </t-form-item>
              </t-col>
            </t-row>
            <t-row :gutter="16">
              <t-col :span="12">
                <t-form-item label="签署日期" name="signDate">
                  <t-date-picker v-model="formData.signDate" placeholder="请选择签署日期" />
                </t-form-item>
              </t-col>
              <t-col :span="12">
                <t-form-item label="生效日期" name="effectiveDate">
                  <t-date-picker v-model="formData.effectiveDate" placeholder="请选择生效日期" />
                </t-form-item>
              </t-col>
            </t-row>
            <t-row :gutter="16">
              <t-col :span="12">
                <t-form-item label="到期日期" name="expireDate">
                  <t-date-picker v-model="formData.expireDate" placeholder="请选择到期日期" />
                </t-form-item>
              </t-col>
              <t-col :span="12">
                <t-form-item label="合同状态" name="status">
                  <t-select v-model="formData.status" placeholder="请选择合同状态">
                    <t-option value="draft" label="草稿" />
                    <t-option value="pending" label="待签署" />
                    <t-option value="executing" label="执行中" />
                    <t-option value="completed" label="已完成" />
                    <t-option value="terminated" label="已终止" />
                  </t-select>
                </t-form-item>
              </t-col>
            </t-row>
            <t-form-item label="付款方式" name="paymentMethod">
              <t-textarea v-model="formData.paymentMethod" placeholder="请输入付款方式" :maxlength="500" />
            </t-form-item>
            <t-form-item label="备注" name="remark">
              <t-textarea v-model="formData.remark" placeholder="请输入备注" :maxlength="500" />
            </t-form-item>
          </t-form>
        </t-tab-panel>

        <!-- 条款明细 -->
        <t-tab-panel value="terms" label="条款明细">
          <t-button theme="primary" size="small" @click="handleAddTerm">
            <template #icon><icon-add /></template>
            添加条款
          </t-button>
          <t-table :data="formData.terms || []" :columns="termColumns" :pagination="false" class="mt-4">
            <template #operation="{ row, index }">
              <t-link theme="danger" @click="handleDeleteTerm(index)">删除</t-link>
            </template>
          </t-table>
        </t-tab-panel>

        <!-- 附件信息 -->
        <t-tab-panel value="attachments" label="附件信息">
          <t-upload
            v-model="formData.attachments"
            action="/api/upload"
            theme="file-input"
            placeholder="点击或拖拽文件到此区域上传"
            :auto-upload="true"
          />
        </t-tab-panel>
      </t-tabs>

      <div class="dialog-footer">
        <t-button theme="default" @click="dialogVisible = false">取消</t-button>
        <t-button theme="primary" @click="handleSave">保存</t-button>
      </div>
    </t-dialog>

    <!-- 合同详情弹窗 -->
    <t-dialog
      v-model:visible="detailVisible"
      header="合同详情"
      width="900px"
      :footer="false"
    >
      <t-tabs v-model="detailActiveTab" theme="card">
        <t-tab-panel value="basic" label="基本信息">
          <t-descriptions :column="2" bordered>
            <t-descriptions-item label="合同编号">{{ detailData.contractCode }}</t-descriptions-item>
            <t-descriptions-item label="合同名称">{{ detailData.contractName }}</t-descriptions-item>
            <t-descriptions-item label="关联客户">{{ detailData.customerName }}</t-descriptions-item>
            <t-descriptions-item label="合同类型">{{ detailData.contractType }}</t-descriptions-item>
            <t-descriptions-item label="合同金额">{{ detailData.amount }} {{ detailData.currency }}</t-descriptions-item>
            <t-descriptions-item label="签署日期">{{ detailData.signDate }}</t-descriptions-item>
            <t-descriptions-item label="生效日期">{{ detailData.effectiveDate }}</t-descriptions-item>
            <t-descriptions-item label="到期日期">{{ detailData.expireDate }}</t-descriptions-item>
            <t-descriptions-item label="合同状态">
              <t-tag v-if="detailData.status === 'draft'" theme="default">草稿</t-tag>
              <t-tag v-else-if="detailData.status === 'pending'" theme="warning">待签署</t-tag>
              <t-tag v-else-if="detailData.status === 'executing'" theme="success">执行中</t-tag>
              <t-tag v-else-if="detailData.status === 'completed'" theme="primary">已完成</t-tag>
              <t-tag v-else theme="danger">已终止</t-tag>
            </t-descriptions-item>
            <t-descriptions-item label="付款方式" :span="2">{{ detailData.paymentMethod }}</t-descriptions-item>
            <t-descriptions-item label="备注" :span="2">{{ detailData.remark }}</t-descriptions-item>
          </t-descriptions>
        </t-tab-panel>

        <t-tab-panel value="terms" label="条款明细">
          <t-table :data="detailData.terms || []" :columns="termColumns" :pagination="false" />
        </t-tab-panel>

        <t-tab-panel value="logs" label="操作日志">
          <t-table :data="detailData.logs || []" :columns="logColumns" :pagination="false" />
        </t-tab-panel>
      </t-tabs>
    </t-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { MessagePlugin } from 'tdesign-vue-next';
import {
  IconFileCopy, IconMoneyCircle, IconTime, IconErrorCircle,
  IconAdd, IconEdit, IconDownload, IconRefresh
} from 'tdesign-icons-vue-next';

const loading = ref(false);
const dialogVisible = ref(false);
const detailVisible = ref(false);
const dialogTitle = ref('新增合同');
const activeTab = ref('basic');
const detailActiveTab = ref('basic');
const formRef = ref();
const selectedRowKeys = ref([]);

const statistics = reactive({
  total: 89,
  amount: 12500000,
  executing: 45,
  expiring: 8
});

const searchForm = reactive({
  contractCode: '',
  contractName: '',
  customerName: '',
  status: ''
});

const formData = reactive({
  id: '',
  contractCode: '',
  contractName: '',
  customerId: '',
  contractType: '',
  amount: 0,
  currency: 'CNY',
  signDate: '',
  effectiveDate: '',
  expireDate: '',
  status: 'draft',
  paymentMethod: '',
  remark: '',
  terms: [],
  attachments: []
});

const detailData = reactive<any>({});

const pagination = reactive({
  current: 1,
  pageSize: 20,
  total: 89
});

const customers = ref([
  { id: 1, name: '上海科技有限公司' },
  { id: 2, name: '北京贸易公司' }
]);

const columns = [
  { colKey: 'contractCode', title: '合同编号', width: 130 },
  { colKey: 'contractName', title: '合同名称', width: 150 },
  { colKey: 'customerName', title: '关联客户', width: 150 },
  { colKey: 'contractType', title: '合同类型', width: 100 },
  { colKey: 'amount', title: '合同金额', width: 120 },
  { colKey: 'signDate', title: '签署日期', width: 120 },
  { colKey: 'expireDate', title: '到期日期', width: 120 },
  { colKey: 'status', title: '合同状态', width: 90 },
  { colKey: 'createTime', title: '创建时间', width: 120 },
  { colKey: 'operation', title: '操作', width: 180, fixed: 'right' }
];

const termColumns = [
  { colKey: 'termName', title: '条款名称', width: 200 },
  { colKey: 'termContent', title: '条款内容', width: 300 },
  { colKey: 'operation', title: '操作', width: 80 }
];

const logColumns = [
  { colKey: 'operator', title: '操作人', width: 100 },
  { colKey: 'action', title: '操作内容', width: 200 },
  { colKey: 'createTime', title: '操作时间', width: 150 }
];

const rules = {
  contractName: [{ required: true, message: '请输入合同名称', type: 'error' }],
  customerId: [{ required: true, message: '请选择客户', type: 'error' }],
  contractType: [{ required: true, message: '请选择合同类型', type: 'error' }],
  amount: [{ required: true, message: '请输入合同金额', type: 'error' }]
};

const tableData = ref([
  {
    id: 1,
    contractCode: 'CT202600001',
    contractName: 'ERP系统采购合同',
    customerId: 1,
    customerName: '上海科技有限公司',
    contractType: '销售合同',
    amount: 500000,
    currency: 'CNY',
    signDate: '2026-01-15',
    effectiveDate: '2026-01-20',
    expireDate: '2027-01-19',
    status: 'executing',
    paymentMethod: '分期付款：首付30%，验收后支付70%',
    remark: '',
    terms: [
      { termName: '交付周期', termContent: '合同签署后30个工作日内完成系统部署' },
      { termName: '售后服务', termContent: '提供1年免费技术支持服务' }
    ],
    createTime: '2026-01-15 10:30:00'
  },
  {
    id: 2,
    contractCode: 'CT202600002',
    contractName: 'CRM系统服务合同',
    customerId: 2,
    customerName: '北京贸易公司',
    contractType: '服务合同',
    amount: 120000,
    currency: 'CNY',
    signDate: '2026-01-20',
    effectiveDate: '2026-01-25',
    expireDate: '2026-07-24',
    status: 'executing',
    paymentMethod: '按月支付，每月10000元',
    remark: '',
    terms: [],
    createTime: '2026-01-20 14:20:00'
  }
]);

const handleSearch = () => {
  MessagePlugin.success('查询成功');
};

const handleReset = () => {
  Object.assign(searchForm, {
    contractCode: '',
    contractName: '',
    customerName: '',
    status: ''
  });
};

const handleAdd = () => {
  dialogTitle.value = '新增合同';
  dialogVisible.value = true;
  activeTab.value = 'basic';
};

const handleEdit = (row: any) => {
  dialogTitle.value = '编辑合同';
  dialogVisible.value = true;
  activeTab.value = 'basic';
  Object.assign(formData, row);
};

const handleView = (row: any) => {
  detailVisible.value = true;
  detailActiveTab.value = 'basic';
  Object.assign(detailData, row);
  detailData.logs = [
    { operator: '张三', action: '创建合同', createTime: '2026-01-15 10:30:00' }
  ];
};

const handleSign = (row: any) => {
  MessagePlugin.success(`合同 ${row.contractName} 已签署`);
};

const handleBatchSign = () => {
  MessagePlugin.success('批量签署成功');
};

const handleAddTerm = () => {
  if (!formData.terms) formData.terms = [];
  formData.terms.push({ termName: '', termContent: '' });
};

const handleDeleteTerm = (index: number) => {
  formData.terms.splice(index, 1);
};

const handleDelete = (row: any) => {
  MessagePlugin.success('合同已删除');
};

const handleSave = () => {
  MessagePlugin.success(dialogTitle.value === '新增合同' ? '合同已创建' : '合同已更新');
  dialogVisible.value = false;
};

const handleDialogClose = () => {
  Object.assign(formData, {
    id: '',
    contractCode: '',
    contractName: '',
    customerId: '',
    contractType: '',
    amount: 0,
    currency: 'CNY',
    signDate: '',
    effectiveDate: '',
    expireDate: '',
    status: 'draft',
    paymentMethod: '',
    remark: '',
    terms: [],
    attachments: []
  });
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
  console.log('ContractManagement mounted');
});
</script>

<style scoped lang="less">
.contract-management-container {
  padding: 20px;
}

.mb-4 {
  margin-bottom: 16px;
}

.mt-4 {
  margin-top: 16px;
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
