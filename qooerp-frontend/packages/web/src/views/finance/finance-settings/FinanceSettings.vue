<template>
  <div class="finance-settings-container">
    <t-card>
      <t-tabs v-model="activeTab">
        <t-tab-panel value="account" label="账户设置">
          <t-form :data="accountSettings" label-width="120px" @submit="handleAccountSubmit">
            <t-form-item label="默认收款账户" name="defaultReceiveAccount">
              <t-select v-model="accountSettings.defaultReceiveAccount" placeholder="请选择默认收款账户">
                <t-option value="1" label="工商银行-6222021234567890" />
                <t-option value="2" label="建设银行-6227001234567890" />
                <t-option value="3" label="农业银行-6228481234567890" />
              </t-select>
            </t-form-item>
            <t-form-item label="默认付款账户" name="defaultPayAccount">
              <t-select v-model="accountSettings.defaultPayAccount" placeholder="请选择默认付款账户">
                <t-option value="1" label="工商银行-6222021234567890" />
                <t-option value="2" label="建设银行-6227001234567890" />
                <t-option value="3" label="农业银行-6228481234567890" />
              </t-select>
            </t-form-item>
            <t-form-item label="允许透支" name="allowOverdraft">
              <t-switch v-model="accountSettings.allowOverdraft" />
            </t-form-item>
            <t-form-item label="透支限额" name="overdraftLimit">
              <t-input-number v-model="accountSettings.overdraftLimit" placeholder="请输入透支限额" :min="0" :decimal-places="2" />
            </t-form-item>
            <t-form-item>
              <t-button theme="primary" type="submit">保存设置</t-button>
            </t-form-item>
          </t-form>
        </t-tab-panel>
        <t-tab-panel value="invoice" label="发票设置">
          <t-form :data="invoiceSettings" label-width="120px" @submit="handleInvoiceSubmit">
            <t-form-item label="默认税率" name="defaultTaxRate">
              <t-input-number v-model="invoiceSettings.defaultTaxRate" placeholder="请输入默认税率" :min="0" :max="100" :decimal-places="2" />
              <span style="margin-left: 8px">%</span>
            </t-form-item>
            <t-form-item label="自动开票" name="autoInvoice">
              <t-switch v-model="invoiceSettings.autoInvoice" />
            </t-form-item>
            <t-form-item label="发票前缀" name="invoicePrefix">
              <t-input v-model="invoiceSettings.invoicePrefix" placeholder="请输入发票前缀" />
            </t-form-item>
            <t-form-item label="发票起始号" name="invoiceStartNo">
              <t-input-number v-model="invoiceSettings.invoiceStartNo" placeholder="请输入发票起始号" :min="1" />
            </t-form-item>
            <t-form-item label="电子发票" name="electronicInvoice">
              <t-switch v-model="invoiceSettings.electronicInvoice" />
            </t-form-item>
            <t-form-item>
              <t-button theme="primary" type="submit">保存设置</t-button>
            </t-form-item>
          </t-form>
        </t-tab-panel>
        <t-tab-panel value="reimbursement" label="报销设置">
          <t-form :data="reimbursementSettings" label-width="120px" @submit="handleReimbursementSubmit">
            <t-form-item label="报销审批" name="needApproval">
              <t-switch v-model="reimbursementSettings.needApproval" />
            </t-form-item>
            <t-form-item label="报销限额" name="reimbursementLimit">
              <t-input-number v-model="reimbursementSettings.reimbursementLimit" placeholder="请输入报销限额" :min="0" :decimal-places="2" />
            </t-form-item>
            <t-form-item label="单日限额" name="dailyLimit">
              <t-input-number v-model="reimbursementSettings.dailyLimit" placeholder="请输入单日限额" :min="0" :decimal-places="2" />
            </t-form-item>
            <t-form-item label="自动通过金额" name="autoPassAmount">
              <t-input-number v-model="reimbursementSettings.autoPassAmount" placeholder="请输入自动通过金额" :min="0" :decimal-places="2" />
            </t-form-item>
            <t-form-item label="支持附件" name="allowAttachment">
              <t-switch v-model="reimbursementSettings.allowAttachment" />
            </t-form-item>
            <t-form-item>
              <t-button theme="primary" type="submit">保存设置</t-button>
            </t-form-item>
          </t-form>
        </t-tab-panel>
        <t-tab-panel value="budget" label="预算设置">
          <t-form :data="budgetSettings" label-width="120px" @submit="handleBudgetSubmit">
            <t-form-item label="启用预算控制" name="enableBudgetControl">
              <t-switch v-model="budgetSettings.enableBudgetControl" />
            </t-form-item>
            <t-form-item label="预算周期" name="budgetCycle">
              <t-select v-model="budgetSettings.budgetCycle" placeholder="请选择预算周期">
                <t-option value="monthly" label="月度" />
                <t-option value="quarterly" label="季度" />
                <t-option value="yearly" label="年度" />
              </t-select>
            </t-form-item>
            <t-form-item label="超预算处理" name="overBudgetAction">
              <t-radio-group v-model="budgetSettings.overBudgetAction">
                <t-radio value="reject">拒绝操作</t-radio>
                <t-radio value="warn">警告提示</t-radio>
                <t-radio value="allow">允许通过</t-radio>
              </t-radio-group>
            </t-form-item>
            <t-form-item label="预算预警阈值" name="budgetWarningThreshold">
              <t-input-number v-model="budgetSettings.budgetWarningThreshold" placeholder="请输入预算预警阈值" :min="0" :max="100" :decimal-places="2" />
              <span style="margin-left: 8px">%</span>
            </t-form-item>
            <t-form-item>
              <t-button theme="primary" type="submit">保存设置</t-button>
            </t-form-item>
          </t-form>
        </t-tab-panel>
        <t-tab-panel value="currency" label="币种设置">
          <div class="currency-list">
            <div class="list-header">
              <t-space>
                <t-button theme="primary" @click="handleAddCurrency">
                  <template #icon>
                    <t-icon name="add" />
                  </template>
                  新增币种
                </t-button>
                <t-button theme="default" @click="handleRefreshCurrency">
                  <template #icon>
                    <t-icon name="refresh" />
                  </template>
                  刷新汇率
                </t-button>
              </t-space>
            </div>
            <t-table
              :data="currencyList"
              :columns="currencyColumns"
              :pagination="false"
              stripe
              hover
              row-key="id"
            >
              <template #isDefault="{ row }">
                <t-tag v-if="row.isDefault" theme="primary">默认</t-tag>
                <span v-else>-</span>
              </template>
              <template #status="{ row }">
                <t-tag v-if="row.status === 'active'" theme="success">启用</t-tag>
                <t-tag v-else theme="default">禁用</t-tag>
              </template>
              <template #action="{ row }">
                <t-space>
                  <t-link theme="primary" @click="handleEditCurrency(row)">编辑</t-link>
                  <t-link theme="warning" @click="handleSetDefault(row)">设为默认</t-link>
                  <t-link v-if="row.status === 'active'" theme="danger" @click="handleDisableCurrency(row)">禁用</t-link>
                  <t-link v-else theme="success" @click="handleEnableCurrency(row)">启用</t-link>
                </t-space>
              </template>
            </t-table>
          </div>
        </t-tab-panel>
        <t-tab-panel value="payment" label="收款方式">
          <div class="payment-list">
            <div class="list-header">
              <t-space>
                <t-button theme="primary" @click="handleAddPayment">
                  <template #icon>
                    <t-icon name="add" />
                  </template>
                  新增收款方式
                </t-button>
              </t-space>
            </div>
            <t-table
              :data="paymentList"
              :columns="paymentColumns"
              :pagination="false"
              stripe
              hover
              row-key="id"
            >
              <template #status="{ row }">
                <t-tag v-if="row.status === 'active'" theme="success">启用</t-tag>
                <t-tag v-else theme="default">禁用</t-tag>
              </template>
              <template #action="{ row }">
                <t-space>
                  <t-link theme="primary" @click="handleEditPayment(row)">编辑</t-link>
                  <t-link v-if="row.status === 'active'" theme="danger" @click="handleDisablePayment(row)">禁用</t-link>
                  <t-link v-else theme="success" @click="handleEnablePayment(row)">启用</t-link>
                </t-space>
              </template>
            </t-table>
          </div>
        </t-tab-panel>
      </t-tabs>
    </t-card>

    <!-- 新增/编辑币种弹窗 -->
    <t-dialog
      v-model:visible="currencyDialogVisible"
      :header="isEditCurrency ? '编辑币种' : '新增币种'"
      width="500px"
      :confirm-btn="null"
      :cancel-btn="null"
    >
      <t-form :data="currencyForm" label-width="120px" @submit="handleCurrencySubmit">
        <t-form-item label="币种代码" name="currencyCode">
          <t-input v-model="currencyForm.currencyCode" placeholder="请输入币种代码，如：USD" />
        </t-form-item>
        <t-form-item label="币种名称" name="currencyName">
          <t-input v-model="currencyForm.currencyName" placeholder="请输入币种名称，如：美元" />
        </t-form-item>
        <t-form-item label="汇率" name="exchangeRate">
          <t-input-number v-model="currencyForm.exchangeRate" placeholder="请输入汇率" :min="0" :decimal-places="4" />
        </t-form-item>
        <t-form-item label="符号" name="symbol">
          <t-input v-model="currencyForm.symbol" placeholder="请输入符号，如：$" />
        </t-form-item>
        <t-form-item label="状态" name="status">
          <t-switch v-model="currencyForm.status" :custom-value="[true, false]" :label="['启用', '禁用']" />
        </t-form-item>
        <t-form-item>
          <t-space>
            <t-button theme="primary" type="submit">确认</t-button>
            <t-button theme="default" @click="currencyDialogVisible = false">取消</t-button>
          </t-space>
        </t-form-item>
      </t-form>
    </t-dialog>

    <!-- 新增/编辑收款方式弹窗 -->
    <t-dialog
      v-model:visible="paymentDialogVisible"
      :header="isEditPayment ? '编辑收款方式' : '新增收款方式'"
      width="500px"
      :confirm-btn="null"
      :cancel-btn="null"
    >
      <t-form :data="paymentForm" label-width="120px" @submit="handlePaymentSubmit">
        <t-form-item label="收款方式" name="paymentMethod">
          <t-input v-model="paymentForm.paymentMethod" placeholder="请输入收款方式，如：银行转账" />
        </t-form-item>
        <t-form-item label="方式代码" name="paymentCode">
          <t-input v-model="paymentForm.paymentCode" placeholder="请输入方式代码，如：BANK_TRANSFER" />
        </t-form-item>
        <t-form-item label="排序" name="sort">
          <t-input-number v-model="paymentForm.sort" placeholder="请输入排序" :min="0" />
        </t-form-item>
        <t-form-item label="状态" name="status">
          <t-switch v-model="paymentForm.status" :custom-value="[true, false]" :label="['启用', '禁用']" />
        </t-form-item>
        <t-form-item>
          <t-space>
            <t-button theme="primary" type="submit">确认</t-button>
            <t-button theme="default" @click="paymentDialogVisible = false">取消</t-button>
          </t-space>
        </t-form-item>
      </t-form>
    </t-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { MessagePlugin, DialogPlugin } from 'tdesign-vue-next'

// 当前Tab
const activeTab = ref('account')

// 账户设置
const accountSettings = reactive({
  defaultReceiveAccount: '1',
  defaultPayAccount: '1',
  allowOverdraft: true,
  overdraftLimit: 100000,
})

// 发票设置
const invoiceSettings = reactive({
  defaultTaxRate: 13,
  autoInvoice: false,
  invoicePrefix: 'FP',
  invoiceStartNo: 10001,
  electronicInvoice: true,
})

// 报销设置
const reimbursementSettings = reactive({
  needApproval: true,
  reimbursementLimit: 5000,
  dailyLimit: 1000,
  autoPassAmount: 500,
  allowAttachment: true,
})

// 预算设置
const budgetSettings = reactive({
  enableBudgetControl: true,
  budgetCycle: 'monthly',
  overBudgetAction: 'warn',
  budgetWarningThreshold: 80,
})

// 币种列表
const currencyList = ref([
  {
    id: '1',
    currencyCode: 'CNY',
    currencyName: '人民币',
    exchangeRate: 1.0000,
    symbol: '¥',
    isDefault: true,
    status: 'active',
  },
  {
    id: '2',
    currencyCode: 'USD',
    currencyName: '美元',
    exchangeRate: 7.2450,
    symbol: '$',
    isDefault: false,
    status: 'active',
  },
  {
    id: '3',
    currencyCode: 'EUR',
    currencyName: '欧元',
    exchangeRate: 7.8250,
    symbol: '€',
    isDefault: false,
    status: 'active',
  },
  {
    id: '4',
    currencyCode: 'HKD',
    currencyName: '港币',
    exchangeRate: 0.9280,
    symbol: 'HK$',
    isDefault: false,
    status: 'active',
  },
  {
    id: '5',
    currencyCode: 'JPY',
    currencyName: '日元',
    exchangeRate: 0.0485,
    symbol: '¥',
    isDefault: false,
    status: 'inactive',
  },
])

// 币种列
const currencyColumns = [
  { colKey: 'currencyCode', title: '币种代码', width: 120 },
  { colKey: 'currencyName', title: '币种名称', width: 120 },
  { colKey: 'exchangeRate', title: '汇率', width: 120 },
  { colKey: 'symbol', title: '符号', width: 80 },
  { colKey: 'isDefault', title: '默认', width: 80 },
  { colKey: 'status', title: '状态', width: 80 },
  { colKey: 'action', title: '操作', width: 200 },
]

// 收款方式列表
const paymentList = ref([
  {
    id: '1',
    paymentMethod: '银行转账',
    paymentCode: 'BANK_TRANSFER',
    sort: 1,
    status: 'active',
  },
  {
    id: '2',
    paymentMethod: '现金',
    paymentCode: 'CASH',
    sort: 2,
    status: 'active',
  },
  {
    id: '3',
    paymentMethod: '支付宝',
    paymentCode: 'ALIPAY',
    sort: 3,
    status: 'active',
  },
  {
    id: '4',
    paymentMethod: '微信支付',
    paymentCode: 'WECHAT_PAY',
    sort: 4,
    status: 'active',
  },
  {
    id: '5',
    paymentMethod: '支票',
    paymentCode: 'CHECK',
    sort: 5,
    status: 'inactive',
  },
])

// 收款方式列
const paymentColumns = [
  { colKey: 'paymentMethod', title: '收款方式', width: 150 },
  { colKey: 'paymentCode', title: '方式代码', width: 150 },
  { colKey: 'sort', title: '排序', width: 80 },
  { colKey: 'status', title: '状态', width: 80 },
  { colKey: 'action', title: '操作', width: 150 },
]

// 币种弹窗
const currencyDialogVisible = ref(false)
const isEditCurrency = ref(false)
const currencyForm = reactive({
  id: '',
  currencyCode: '',
  currencyName: '',
  exchangeRate: 0,
  symbol: '',
  status: true,
})

// 收款方式弹窗
const paymentDialogVisible = ref(false)
const isEditPayment = ref(false)
const paymentForm = reactive({
  id: '',
  paymentMethod: '',
  paymentCode: '',
  sort: 0,
  status: true,
})

// 账户设置提交
const handleAccountSubmit = () => {
  MessagePlugin.success('账户设置保存成功')
}

// 发票设置提交
const handleInvoiceSubmit = () => {
  MessagePlugin.success('发票设置保存成功')
}

// 报销设置提交
const handleReimbursementSubmit = () => {
  MessagePlugin.success('报销设置保存成功')
}

// 预算设置提交
const handleBudgetSubmit = () => {
  MessagePlugin.success('预算设置保存成功')
}

// 新增币种
const handleAddCurrency = () => {
  isEditCurrency.value = false
  currencyForm.id = ''
  currencyForm.currencyCode = ''
  currencyForm.currencyName = ''
  currencyForm.exchangeRate = 0
  currencyForm.symbol = ''
  currencyForm.status = true
  currencyDialogVisible.value = true
}

// 编辑币种
const handleEditCurrency = (row: any) => {
  isEditCurrency.value = true
  Object.assign(currencyForm, row)
  currencyForm.status = row.status === 'active'
  currencyDialogVisible.value = true
}

// 币种提交
const handleCurrencySubmit = () => {
  currencyDialogVisible.value = false
  MessagePlugin.success(isEditCurrency.value ? '币种修改成功' : '币种新增成功')
}

// 设为默认币种
const handleSetDefault = (row: any) => {
  const dialog = DialogPlugin({
    header: '设为默认',
    body: `是否将 ${row.currencyName} 设为默认币种？`,
    confirmBtn: {
      content: '确认',
      theme: 'primary',
    },
    onConfirm: () => {
      dialog.hide()
      currencyList.value.forEach((item) => {
        item.isDefault = item.id === row.id
      })
      MessagePlugin.success('设置成功')
    },
  })
}

// 禁用币种
const handleDisableCurrency = (row: any) => {
  const dialog = DialogPlugin({
    header: '禁用币种',
    body: `是否禁用 ${row.currencyName}？`,
    confirmBtn: {
      content: '确认禁用',
      theme: 'danger',
    },
    onConfirm: () => {
      dialog.hide()
      row.status = 'inactive'
      MessagePlugin.success('已禁用')
    },
  })
}

// 启用币种
const handleEnableCurrency = (row: any) => {
  row.status = 'active'
  MessagePlugin.success('已启用')
}

// 刷新汇率
const handleRefreshCurrency = () => {
  MessagePlugin.success('汇率刷新成功')
}

// 新增收款方式
const handleAddPayment = () => {
  isEditPayment.value = false
  paymentForm.id = ''
  paymentForm.paymentMethod = ''
  paymentForm.paymentCode = ''
  paymentForm.sort = 0
  paymentForm.status = true
  paymentDialogVisible.value = true
}

// 编辑收款方式
const handleEditPayment = (row: any) => {
  isEditPayment.value = true
  Object.assign(paymentForm, row)
  paymentForm.status = row.status === 'active'
  paymentDialogVisible.value = true
}

// 收款方式提交
const handlePaymentSubmit = () => {
  paymentDialogVisible.value = false
  MessagePlugin.success(isEditPayment.value ? '收款方式修改成功' : '收款方式新增成功')
}

// 禁用收款方式
const handleDisablePayment = (row: any) => {
  const dialog = DialogPlugin({
    header: '禁用收款方式',
    body: `是否禁用 ${row.paymentMethod}？`,
    confirmBtn: {
      content: '确认禁用',
      theme: 'danger',
    },
    onConfirm: () => {
      dialog.hide()
      row.status = 'inactive'
      MessagePlugin.success('已禁用')
    },
  })
}

// 启用收款方式
const handleEnablePayment = (row: any) => {
  row.status = 'active'
  MessagePlugin.success('已启用')
}
</script>

<style scoped lang="less">
.finance-settings-container {
  padding: 16px;
  background-color: #f3f3f3;
  min-height: 100vh;

  .currency-list,
  .payment-list {
    .list-header {
      margin-bottom: 16px;
    }
  }
}
</style>
