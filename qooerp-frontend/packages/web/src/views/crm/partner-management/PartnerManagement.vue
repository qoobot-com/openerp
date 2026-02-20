<template>
  <div class="partner-management-container">
    <!-- 统计卡片 -->
    <t-row :gutter="16" class="mb-4">
      <t-col :span="6">
        <t-card theme="primary">
          <t-statistic title="合作伙伴总数" :value="statistics.totalPartners" :loading="loading">
            <template #prefix><icon-shop /></template>
          </t-statistic>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="success">
          <t-statistic title="活跃伙伴" :value="statistics.activePartners" :loading="loading">
            <template #prefix><icon-check-circle /></template>
          </t-statistic>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="warning">
          <t-statistic title="本月新增" :value="statistics.newPartners" :loading="loading">
            <template #prefix><icon-add-circle /></template>
          </t-statistic>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="danger">
          <t-statistic title="总交易额" :value="statistics.totalAmount" suffix="元" :loading="loading">
            <template #prefix><icon-money-circle /></template>
          </t-statistic>
        </t-card>
      </t-row>

    <!-- 搜索卡片 -->
    <t-card class="mb-4">
      <t-form :data="searchForm" layout="inline" @submit="handleSearch">
        <t-form-item label="伙伴名称" name="partnerName">
          <t-input v-model="searchForm.partnerName" placeholder="请输入伙伴名称" clearable />
        </t-form-item>
        <t-form-item label="伙伴类型" name="partnerType">
          <t-select v-model="searchForm.partnerType" placeholder="请选择伙伴类型" clearable>
            <t-option value="distributor" label="经销商" />
            <t-option value="agent" label="代理商" />
            <t-option value="reseller" label="分销商" />
            <t-option value="service" label="服务商" />
          </t-select>
        </t-form-item>
        <t-form-item label="伙伴等级" name="level">
          <t-select v-model="searchForm.level" placeholder="请选择伙伴等级" clearable>
            <t-option value="platinum" label="白金伙伴" />
            <t-option value="gold" label="黄金伙伴" />
            <t-option value="silver" label="白银伙伴" />
            <t-option value="bronze" label="青铜伙伴" />
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
          新增伙伴
        </t-button>
        <t-button theme="default" @click="handleBatchImport">
          <template #icon><icon-upload /></template>
          批量导入
        </t-button>
      </div>
      <t-button theme="default" variant="outline" @click="handleRefresh">
        <template #icon><icon-refresh /></template>
        刷新
      </t-button>
    </div>

    <!-- 伙伴列表 -->
    <t-card>
      <t-table
        :data="tableData"
        :columns="columns"
        :loading="loading"
        :pagination="pagination"
        row-key="id"
        @page-change="handlePageChange"
      >
        <template #partnerType="{ row }">
          <t-tag v-if="row.partnerType === 'distributor'" theme="primary" variant="light">经销商</t-tag>
          <t-tag v-else-if="row.partnerType === 'agent'" theme="success" variant="light">代理商</t-tag>
          <t-tag v-else-if="row.partnerType === 'reseller'" theme="warning" variant="light">分销商</t-tag>
          <t-tag v-else theme="default" variant="light">服务商</t-tag>
        </template>
        <template #level="{ row }">
          <t-tag v-if="row.level === 'platinum'" theme="primary">白金伙伴</t-tag>
          <t-tag v-else-if="row.level === 'gold'" theme="success">黄金伙伴</t-tag>
          <t-tag v-else-if="row.level === 'silver'" theme="warning">白银伙伴</t-tag>
          <t-tag v-else>青铜伙伴</t-tag>
        </template>
        <template #status="{ row }">
          <t-tag v-if="row.status === 'active'" theme="success" variant="light">活跃</t-tag>
          <t-tag v-else-if="row.status === 'inactive'" theme="default" variant="light">非活跃</t-tag>
          <t-tag v-else theme="danger" variant="light">已停用</t-tag>
        </template>
        <template #operation="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleView(row)">查看</t-link>
            <t-link theme="primary" @click="handleEdit(row)">编辑</t-link>
            <t-link theme="warning" @click="handleSuspend(row)" v-if="row.status === 'active'">停用</t-link>
            <t-link theme="success" @click="handleActivate(row)" v-else>激活</t-link>
          </t-space>
        </template>
      </t-table>
    </t-card>

    <!-- 新增/编辑伙伴弹窗 -->
    <t-dialog
      v-model:visible="dialogVisible"
      :header="dialogTitle"
      width="900px"
      :footer="false"
      @close="handleDialogClose"
    >
      <t-tabs v-model="dialogActiveTab" theme="card">
        <t-tab-panel value="basic" label="基本信息">
          <t-form :data="formData" ref="formRef" :rules="rules" label-width="120px">
            <t-row :gutter="16">
              <t-col :span="12">
                <t-form-item label="伙伴名称" name="partnerName">
                  <t-input v-model="formData.partnerName" placeholder="请输入伙伴名称" />
                </t-form-item>
              </t-col>
              <t-col :span="12">
                <t-form-item label="伙伴类型" name="partnerType">
                  <t-select v-model="formData.partnerType" placeholder="请选择伙伴类型">
                    <t-option value="distributor" label="经销商" />
                    <t-option value="agent" label="代理商" />
                    <t-option value="reseller" label="分销商" />
                    <t-option value="service" label="服务商" />
                  </t-select>
                </t-form-item>
              </t-col>
            </t-row>
            <t-row :gutter="16">
              <t-col :span="12">
                <t-form-item label="伙伴等级" name="level">
                  <t-select v-model="formData.level" placeholder="请选择伙伴等级">
                    <t-option value="platinum" label="白金伙伴" />
                    <t-option value="gold" label="黄金伙伴" />
                    <t-option value="silver" label="白银伙伴" />
                    <t-option value="bronze" label="青铜伙伴" />
                  </t-select>
                </t-form-item>
              </t-col>
              <t-col :span="12">
                <t-form-item label="合作区域" name="region">
                  <t-input v-model="formData.region" placeholder="请输入合作区域" />
                </t-form-item>
              </t-col>
            </t-row>
            <t-row :gutter="16">
              <t-col :span="12">
                <t-form-item label="联系人" name="contact">
                  <t-input v-model="formData.contact" placeholder="请输入联系人" />
                </t-form-item>
              </t-col>
              <t-col :span="12">
                <t-form-item label="联系电话" name="phone">
                  <t-input v-model="formData.phone" placeholder="请输入联系电话" />
                </t-form-item>
              </t-col>
            </t-row>
            <t-row :gutter="16">
              <t-col :span="12">
                <t-form-item label="电子邮箱" name="email">
                  <t-input v-model="formData.email" placeholder="请输入电子邮箱" />
                </t-form-item>
              </t-col>
              <t-col :span="12">
                <t-form-item label="所在地址" name="address">
                  <t-input v-model="formData.address" placeholder="请输入所在地址" />
                </t-form-item>
              </t-col>
            </t-row>
          </t-form>
        </t-tab-panel>

        <t-tab-panel value="cooperation" label="合作信息">
          <t-form :data="formData" label-width="120px">
            <t-row :gutter="16">
              <t-col :span="12">
                <t-form-item label="合作开始日期" name="startDate">
                  <t-date-picker v-model="formData.startDate" placeholder="请选择合作开始日期" />
                </t-form-item>
              </t-col>
              <t-col :span="12">
                <t-form-item label="合作结束日期" name="endDate">
                  <t-date-picker v-model="formData.endDate" placeholder="请选择合作结束日期" />
                </t-form-item>
              </t-col>
            </t-row>
            <t-form-item label="合作模式" name="cooperationMode">
              <t-select v-model="formData.cooperationMode" placeholder="请选择合作模式">
                <t-option value="exclusive" label="独家代理" />
                <t-option value="non_exclusive" label="非独家代理" />
                <t-option value="regional" label="区域代理" />
                <t-option value="project" label="项目合作" />
              </t-select>
            </t-form-item>
            <t-row :gutter="16">
              <t-col :span="12">
                <t-form-item label="分成比例" name="commissionRate">
                  <t-input-number v-model="formData.commissionRate" :min="0" :max="100" :suffix="'%'" />
                </t-form-item>
              </t-col>
              <t-col :span="12">
                <t-form-item label="最低销售额" name="minSalesAmount">
                  <t-input-number v-model="formData.minSalesAmount" :min="0" :suffix="'元'" />
                </t-form-item>
              </t-col>
            </t-row>
            <t-form-item label="合作内容" name="cooperationContent">
              <t-textarea v-model="formData.cooperationContent" placeholder="请输入合作内容" :maxlength="1000" :autosize="{ minRows: 4 }" />
            </t-form-item>
          </t-form>
        </t-tab-panel>

        <t-tab-panel value="product" label="产品授权">
          <t-form :data="formData" label-width="120px">
            <t-form-item label="授权产品" name="authorizedProducts">
              <t-select v-model="formData.authorizedProducts" placeholder="请选择授权产品" multiple>
                <t-option value="1" label="财务管理系统" />
                <t-option value="2" label="人力资源系统" />
                <t-option value="3" label="供应链系统" />
                <t-option value="4" label="销售管理系统" />
              </t-select>
            </t-form-item>
            <t-form-item label="授权价格" name="authorizedPrices">
              <t-textarea v-model="formData.authorizedPrices" placeholder="请输入授权价格（JSON格式）" :autosize="{ minRows: 4 }" />
            </t-form-item>
            <t-form-item label="备注" name="remark">
              <t-textarea v-model="formData.remark" placeholder="请输入备注信息" :maxlength="500" />
            </t-form-item>
          </t-form>
        </t-tab-panel>
      </t-tabs>
      <div class="dialog-footer">
        <t-button theme="default" @click="dialogVisible = false">取消</t-button>
        <t-button theme="primary" @click="handleSave">保存</t-button>
      </div>
    </t-dialog>

    <!-- 伙伴详情弹窗 -->
    <t-dialog
      v-model:visible="detailVisible"
      header="伙伴详情"
      width="900px"
      :footer="false"
    >
      <t-tabs v-model="detailActiveTab" theme="card">
        <t-tab-panel value="basic" label="基本信息">
          <t-descriptions :column="2" bordered>
            <t-descriptions-item label="伙伴名称" :span="2">{{ detailData.partnerName }}</t-descriptions-item>
            <t-descriptions-item label="伙伴编号">{{ detailData.partnerCode }}</t-descriptions-item>
            <t-descriptions-item label="伙伴类型">{{ getPartnerTypeName(detailData.partnerType) }}</t-descriptions-item>
            <t-descriptions-item label="伙伴等级">{{ getLevelName(detailData.level) }}</t-descriptions-item>
            <t-descriptions-item label="合作区域">{{ detailData.region }}</t-descriptions-item>
            <t-descriptions-item label="联系人">{{ detailData.contact }}</t-descriptions-item>
            <t-descriptions-item label="联系电话">{{ detailData.phone }}</t-descriptions-item>
            <t-descriptions-item label="电子邮箱">{{ detailData.email }}</t-descriptions-item>
            <t-descriptions-item label="所在地址" :span="2">{{ detailData.address }}</t-descriptions-item>
            <t-descriptions-item label="合作开始日期">{{ detailData.startDate }}</t-descriptions-item>
            <t-descriptions-item label="合作结束日期">{{ detailData.endDate }}</t-descriptions-item>
            <t-descriptions-item label="合作模式">{{ detailData.cooperationMode }}</t-descriptions-item>
            <t-descriptions-item label="分成比例">{{ detailData.commissionRate }}%</t-descriptions-item>
          </t-descriptions>
        </t-tab-panel>

        <t-tab-panel value="performance" label="业绩数据">
          <t-row :gutter="16" class="mb-4">
            <t-col :span="6">
              <t-statistic title="累计销售额" :value="detailData.totalSales || 0" suffix="元" />
            </t-col>
            <t-col :span="6">
              <t-statistic title="累计订单数" :value="detailData.totalOrders || 0" />
            </t-col>
            <t-col :span="6">
              <t-statistic title="本月销售额" :value="detailData.monthlySales || 0" suffix="元" />
            </t-col>
            <t-col :span="6">
              <t-statistic title="完成率" :value="detailData.completionRate || 0" suffix="%" />
            </t-col>
          </t-row>
          <div class="chart-placeholder">
            <t-icon name="chart-line" size="60px" />
            <p>业绩趋势图表</p>
          </div>
        </t-tab-panel>

        <t-tab-panel value="orders" label="订单记录">
          <t-table :data="detailData.orders || []" :columns="orderColumns" :pagination="false" />
        </t-tab-panel>
      </t-tabs>
    </t-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { MessagePlugin } from 'tdesign-vue-next';
import {
  IconShop, IconCheckCircle, IconAddCircle, IconMoneyCircle,
  IconAdd, IconUpload, IconRefresh
} from 'tdesign-icons-vue-next';

const loading = ref(false);
const dialogVisible = ref(false);
const detailVisible = ref(false);
const dialogTitle = ref('新增伙伴');
const dialogActiveTab = ref('basic');
const detailActiveTab = ref('basic');
const formRef = ref();

const statistics = reactive({
  totalPartners: 156,
  activePartners: 123,
  newPartners: 8,
  totalAmount: 5680000
});

const searchForm = reactive({
  partnerName: '',
  partnerType: '',
  level: ''
});

const formData = reactive({
  id: '',
  partnerCode: '',
  partnerName: '',
  partnerType: '',
  level: '',
  region: '',
  contact: '',
  phone: '',
  email: '',
  address: '',
  startDate: '',
  endDate: '',
  cooperationMode: '',
  commissionRate: 10,
  minSalesAmount: 0,
  cooperationContent: '',
  authorizedProducts: [],
  authorizedPrices: '',
  remark: ''
});

const detailData = reactive<any>({});

const pagination = reactive({
  current: 1,
  pageSize: 20,
  total: 156
});

const columns = [
  { colKey: 'partnerCode', title: '伙伴编号', width: 140 },
  { colKey: 'partnerName', title: '伙伴名称', width: 150 },
  { colKey: 'partnerType', title: '伙伴类型', width: 100 },
  { colKey: 'level', title: '伙伴等级', width: 100 },
  { colKey: 'region', title: '合作区域', width: 120 },
  { colKey: 'contact', title: '联系人', width: 100 },
  { colKey: 'phone', title: '联系电话', width: 120 },
  { colKey: 'status', title: '状态', width: 80 },
  { colKey: 'totalSales', title: '累计销售', width: 100 },
  { colKey: 'operation', title: '操作', width: 180 }
];

const orderColumns = [
  { colKey: 'orderCode', title: '订单编号', width: 140 },
  { colKey: 'productName', title: '产品名称', width: 150 },
  { colKey: 'amount', title: '订单金额', width: 100 },
  { colKey: 'commission', title: '分成金额', width: 100 },
  { colKey: 'orderDate', title: '下单日期', width: 120 }
];

const rules = {
  partnerName: [{ required: true, message: '请输入伙伴名称', type: 'error' }],
  partnerType: [{ required: true, message: '请选择伙伴类型', type: 'error' }],
  level: [{ required: true, message: '请选择伙伴等级', type: 'error' }],
  contact: [{ required: true, message: '请输入联系人', type: 'error' }],
  phone: [{ required: true, message: '请输入联系电话', type: 'error' }]
};

const tableData = ref([
  {
    id: 1,
    partnerCode: 'PART20260001',
    partnerName: '华东贸易有限公司',
    partnerType: 'distributor',
    level: 'platinum',
    region: '华东地区',
    contact: '张经理',
    phone: '021-12345678',
    email: 'zhang@hdtrade.com',
    address: '上海市浦东新区',
    status: 'active',
    totalSales: 2340000,
    startDate: '2025-01-01',
    endDate: '2026-12-31',
    cooperationMode: '独家代理',
    commissionRate: 15,
    minSalesAmount: 500000
  }
]);

const handleSearch = () => { MessagePlugin.success('查询成功'); };
const handleReset = () => { Object.assign(searchForm, { partnerName: '', partnerType: '', level: '' }); };
const handleRefresh = () => { MessagePlugin.success('数据已刷新'); };

const handleAdd = () => {
  dialogTitle.value = '新增伙伴';
  dialogVisible.value = true;
};

const handleEdit = (row: any) => {
  dialogTitle.value = '编辑伙伴';
  dialogVisible.value = true;
  Object.assign(formData, row);
};

const handleView = (row: any) => {
  detailVisible.value = true;
  detailActiveTab.value = 'basic';
  Object.assign(detailData, row);
  detailData.orders = [
    { orderCode: 'ORD202600001', productName: '财务管理系统', amount: 58000, commission: 8700, orderDate: '2026-02-19' }
  ];
};

const handleBatchImport = () => { MessagePlugin.success('批量导入'); };
const handleSuspend = (row: any) => { MessagePlugin.success('已停用'); };
const handleActivate = (row: any) => { MessagePlugin.success('已激活'); };

const handleSave = () => {
  MessagePlugin.success(dialogTitle.value === '新增伙伴' ? '伙伴已添加' : '伙伴已更新');
  dialogVisible.value = false;
};

const handleDialogClose = () => {
  Object.assign(formData, {
    id: '', partnerCode: '', partnerName: '', partnerType: '', level: '',
    region: '', contact: '', phone: '', email: '', address: '',
    startDate: '', endDate: '', cooperationMode: '', commissionRate: 10,
    minSalesAmount: 0, cooperationContent: '', authorizedProducts: [],
    authorizedPrices: '', remark: ''
  });
};

const handlePageChange = (pageInfo: any) => {
  pagination.current = pageInfo.current;
  pagination.pageSize = pageInfo.pageSize;
};

const getPartnerTypeName = (type: string) => {
  const names: Record<string, string> = {
    distributor: '经销商', agent: '代理商', reseller: '分销商', service: '服务商'
  };
  return names[type] || type;
};

const getLevelName = (level: string) => {
  const names: Record<string, string> = {
    platinum: '白金伙伴', gold: '黄金伙伴', silver: '白银伙伴', bronze: '青铜伙伴'
  };
  return names[level] || level;
};

onMounted(() => { console.log('PartnerManagement mounted'); });
</script>

<style scoped lang="less">
.partner-management-container { padding: 20px; }
.mb-4 { margin-bottom: 16px; }
.flex { display: flex; }
.justify-between { justify-content: space-between; }
.items-center { align-items: center; }
.dialog-footer { display: flex; justify-content: flex-end; gap: 10px; margin-top: 20px; }
.chart-placeholder { text-align: center; padding: 40px; color: #999; }
</style>
