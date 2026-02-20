<template>
  <div class="marketing-campaign-container">
    <!-- 统计卡片 -->
    <t-row :gutter="16" class="mb-4">
      <t-col :span="6">
        <t-card theme="primary">
          <t-statistic title="活动总数" :value="statistics.total" :loading="loading">
            <template #prefix><icon-activity /></template>
          </t-statistic>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="success">
          <t-statistic title="进行中" :value="statistics.ongoing" :loading="loading">
            <template #prefix><icon-play-circle /></template>
          </t-statistic>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="warning">
          <t-statistic title="计划中" :value="statistics.planned" :loading="loading">
            <template #prefix><icon-time /></template>
          </t-statistic>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="danger">
          <t-statistic title="总预算" :value="statistics.budget" :loading="loading">
            <template #prefix><icon-money-circle /></template>
          </t-statistic>
        </t-card>
      </t-col>
    </t-row>

    <!-- 搜索卡片 -->
    <t-card class="mb-4">
      <t-form :data="searchForm" layout="inline" @submit="handleSearch">
        <t-form-item label="活动编号" name="campaignCode">
          <t-input v-model="searchForm.campaignCode" placeholder="请输入活动编号" clearable />
        </t-form-item>
        <t-form-item label="活动名称" name="campaignName">
          <t-input v-model="searchForm.campaignName" placeholder="请输入活动名称" clearable />
        </t-form-item>
        <t-form-item label="活动类型" name="campaignType">
          <t-select v-model="searchForm.campaignType" placeholder="请选择活动类型" clearable>
            <t-option value="线上推广" label="线上推广" />
            <t-option value="线下活动" label="线下活动" />
            <t-option value="邮件营销" label="邮件营销" />
            <t-option value="电话营销" label="电话营销" />
            <t-option value="其他" label="其他" />
          </t-select>
        </t-form-item>
        <t-form-item label="活动状态" name="status">
          <t-select v-model="searchForm.status" placeholder="请选择活动状态" clearable>
            <t-option value="planned" label="计划中" />
            <t-option value="ongoing" label="进行中" />
            <t-option value="completed" label="已完成" />
            <t-option value="cancelled" label="已取消" />
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
          新增活动
        </t-button>
        <t-button theme="default" @click="handleBatchStart">
          <template #icon><icon-play-circle /></template>
          批量启动
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

    <!-- 活动列表 -->
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
          <t-tag v-if="row.status === 'planned'" theme="default" variant="light">计划中</t-tag>
          <t-tag v-else-if="row.status === 'ongoing'" theme="success" variant="light">进行中</t-tag>
          <t-tag v-else-if="row.status === 'completed'" theme="primary" variant="light">已完成</t-tag>
          <t-tag v-else theme="danger" variant="light">已取消</t-tag>
        </template>
        <template #budgetProgress="{ row }">
          <t-progress :percentage="row.budgetProgress" size="small" />
        </template>
        <template #operation="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleView(row)">查看</t-link>
            <t-link theme="primary" @click="handleEdit(row)">编辑</t-link>
            <t-link v-if="row.status === 'planned'" theme="success" @click="handleStart(row)">启动</t-link>
            <t-link v-if="row.status === 'ongoing'" theme="warning" @click="handleStop(row)">暂停</t-link>
            <t-popconfirm content="确定要删除此活动吗？" @confirm="handleDelete(row)">
              <t-link theme="danger">删除</t-link>
            </t-popconfirm>
          </t-space>
        </template>
      </t-table>
    </t-card>

    <!-- 新增/编辑活动弹窗 -->
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
                <t-form-item label="活动编号" name="campaignCode">
                  <t-input v-model="formData.campaignCode" placeholder="自动生成" disabled />
                </t-form-item>
              </t-col>
              <t-col :span="12">
                <t-form-item label="活动名称" name="campaignName">
                  <t-input v-model="formData.campaignName" placeholder="请输入活动名称" />
                </t-form-item>
              </t-col>
            </t-row>
            <t-row :gutter="16">
              <t-col :span="12">
                <t-form-item label="活动类型" name="campaignType">
                  <t-select v-model="formData.campaignType" placeholder="请选择活动类型">
                    <t-option value="线上推广" label="线上推广" />
                    <t-option value="线下活动" label="线下活动" />
                    <t-option value="邮件营销" label="邮件营销" />
                    <t-option value="电话营销" label="电话营销" />
                    <t-option value="其他" label="其他" />
                  </t-select>
                </t-form-item>
              </t-col>
              <t-col :span="12">
                <t-form-item label="负责人" name="owner">
                  <t-input v-model="formData.owner" placeholder="请输入负责人" />
                </t-form-item>
              </t-col>
            </t-row>
            <t-row :gutter="16">
              <t-col :span="12">
                <t-form-item label="开始日期" name="startDate">
                  <t-date-picker v-model="formData.startDate" placeholder="请选择开始日期" />
                </t-form-item>
              </t-col>
              <t-col :span="12">
                <t-form-item label="结束日期" name="endDate">
                  <t-date-picker v-model="formData.endDate" placeholder="请选择结束日期" />
                </t-form-item>
              </t-col>
            </t-row>
            <t-row :gutter="16">
              <t-col :span="12">
                <t-form-item label="活动预算" name="budget">
                  <t-input-number v-model="formData.budget" placeholder="请输入活动预算" :min="0" />
                </t-form-item>
              </t-col>
              <t-col :span="12">
                <t-form-item label="目标客户数" name="targetCustomers">
                  <t-input-number v-model="formData.targetCustomers" placeholder="请输入目标客户数" :min="0" />
                </t-form-item>
              </t-col>
            </t-row>
            <t-form-item label="活动目标" name="objective">
              <t-textarea v-model="formData.objective" placeholder="请输入活动目标" :maxlength="500" />
            </t-form-item>
            <t-form-item label="活动描述" name="description">
              <t-textarea v-model="formData.description" placeholder="请输入活动描述" :maxlength="1000" />
            </t-form-item>
            <t-form-item label="备注" name="remark">
              <t-textarea v-model="formData.remark" placeholder="请输入备注" :maxlength="500" />
            </t-form-item>
          </t-form>
        </t-tab-panel>

        <!-- 目标客户 -->
        <t-tab-panel value="targets" label="目标客户">
          <t-button theme="primary" size="small" @click="handleAddTarget">
            <template #icon><icon-add /></template>
            添加目标客户
          </t-button>
          <t-table :data="formData.targets || []" :columns="targetColumns" :pagination="false" class="mt-4">
            <template #operation="{ row, index }">
              <t-link theme="danger" @click="handleDeleteTarget(index)">删除</t-link>
            </template>
          </t-table>
        </t-tab-panel>

        <!-- 活动效果 -->
        <t-tab-panel value="results" label="活动效果">
          <t-form :data="formData" label-width="120px">
            <t-row :gutter="16">
              <t-col :span="12">
                <t-form-item label="实际触达客户" name="actualReach">
                  <t-input-number v-model="formData.actualReach" placeholder="实际触达客户数" :min="0" />
                </t-form-item>
              </t-col>
              <t-col :span="12">
                <t-form-item label="产生销售机会" name="generatedLeads">
                  <t-input-number v-model="formData.generatedLeads" placeholder="产生销售机会数" :min="0" />
                </t-form-item>
              </t-col>
            </t-row>
            <t-row :gutter="16">
              <t-col :span="12">
                <t-form-item label="转化订单数" name="convertedOrders">
                  <t-input-number v-model="formData.convertedOrders" placeholder="转化订单数" :min="0" />
                </t-form-item>
              </t-col>
              <t-col :span="12">
                <t-form-item label="实际销售额" name="actualSales">
                  <t-input-number v-model="formData.actualSales" placeholder="实际销售额" :min="0" />
                </t-form-item>
              </t-col>
            </t-row>
            <t-form-item label="ROI" name="roi">
              <t-input v-model="formData.roi" placeholder="ROI" disabled />
            </t-form-item>
            <t-form-item label="效果分析" name="resultAnalysis">
              <t-textarea v-model="formData.resultAnalysis" placeholder="请输入效果分析" />
            </t-form-item>
          </t-form>
        </t-tab-panel>
      </t-tabs>

      <div class="dialog-footer">
        <t-button theme="default" @click="dialogVisible = false">取消</t-button>
        <t-button theme="primary" @click="handleSave">保存</t-button>
      </div>
    </t-dialog>

    <!-- 活动详情弹窗 -->
    <t-dialog
      v-model:visible="detailVisible"
      header="活动详情"
      width="900px"
      :footer="false"
    >
      <t-tabs v-model="detailActiveTab" theme="card">
        <t-tab-panel value="basic" label="基本信息">
          <t-descriptions :column="2" bordered>
            <t-descriptions-item label="活动编号">{{ detailData.campaignCode }}</t-descriptions-item>
            <t-descriptions-item label="活动名称">{{ detailData.campaignName }}</t-descriptions-item>
            <t-descriptions-item label="活动类型">{{ detailData.campaignType }}</t-descriptions-item>
            <t-descriptions-item label="负责人">{{ detailData.owner }}</t-descriptions-item>
            <t-descriptions-item label="开始日期">{{ detailData.startDate }}</t-descriptions-item>
            <t-descriptions-item label="结束日期">{{ detailData.endDate }}</t-descriptions-item>
            <t-descriptions-item label="活动预算">¥{{ detailData.budget }}</t-descriptions-item>
            <t-descriptions-item label="目标客户数">{{ detailData.targetCustomers }}</t-descriptions-item>
            <t-descriptions-item label="活动状态">
              <t-tag v-if="detailData.status === 'planned'" theme="default">计划中</t-tag>
              <t-tag v-else-if="detailData.status === 'ongoing'" theme="success">进行中</t-tag>
              <t-tag v-else-if="detailData.status === 'completed'" theme="primary">已完成</t-tag>
              <t-tag v-else theme="danger">已取消</t-tag>
            </t-descriptions-item>
            <t-descriptions-item label="创建时间">{{ detailData.createTime }}</t-descriptions-item>
            <t-descriptions-item label="活动目标" :span="2">{{ detailData.objective }}</t-descriptions-item>
            <t-descriptions-item label="活动描述" :span="2">{{ detailData.description }}</t-descriptions-item>
            <t-descriptions-item label="备注" :span="2">{{ detailData.remark }}</t-descriptions-item>
          </t-descriptions>
        </t-tab-panel>

        <t-tab-panel value="targets" label="目标客户">
          <t-table :data="detailData.targets || []" :columns="targetColumns" :pagination="false" />
        </t-tab-panel>

        <t-tab-panel value="results" label="活动效果">
          <t-descriptions :column="2" bordered>
            <t-descriptions-item label="实际触达客户">{{ detailData.actualReach }}</t-descriptions-item>
            <t-descriptions-item label="产生销售机会">{{ detailData.generatedLeads }}</t-descriptions-item>
            <t-descriptions-item label="转化订单数">{{ detailData.convertedOrders }}</t-descriptions-item>
            <t-descriptions-item label="实际销售额">¥{{ detailData.actualSales }}</t-descriptions-item>
            <t-descriptions-item label="ROI" :span="2">{{ detailData.roi }}</t-descriptions-item>
            <t-descriptions-item label="效果分析" :span="2">{{ detailData.resultAnalysis }}</t-descriptions-item>
          </t-descriptions>
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
  IconActivity, IconPlayCircle, IconTime, IconMoneyCircle,
  IconAdd, IconDownload, IconRefresh
} from 'tdesign-icons-vue-next';

const loading = ref(false);
const dialogVisible = ref(false);
const detailVisible = ref(false);
const dialogTitle = ref('新增活动');
const activeTab = ref('basic');
const detailActiveTab = ref('basic');
const formRef = ref();
const selectedRowKeys = ref([]);

const statistics = reactive({
  total: 45,
  ongoing: 12,
  planned: 15,
  budget: 2800000
});

const searchForm = reactive({
  campaignCode: '',
  campaignName: '',
  campaignType: '',
  status: ''
});

const formData = reactive({
  id: '',
  campaignCode: '',
  campaignName: '',
  campaignType: '',
  owner: '',
  startDate: '',
  endDate: '',
  budget: 0,
  targetCustomers: 0,
  objective: '',
  description: '',
  remark: '',
  targets: [],
  actualReach: 0,
  generatedLeads: 0,
  convertedOrders: 0,
  actualSales: 0,
  roi: '',
  resultAnalysis: ''
});

const detailData = reactive<any>({});

const pagination = reactive({
  current: 1,
  pageSize: 20,
  total: 45
});

const columns = [
  { colKey: 'campaignCode', title: '活动编号', width: 130 },
  { colKey: 'campaignName', title: '活动名称', width: 150 },
  { colKey: 'campaignType', title: '活动类型', width: 100 },
  { colKey: 'owner', title: '负责人', width: 100 },
  { colKey: 'startDate', title: '开始日期', width: 120 },
  { colKey: 'endDate', title: '结束日期', width: 120 },
  { colKey: 'budget', title: '活动预算', width: 120 },
  { colKey: 'status', title: '活动状态', width: 90 },
  { colKey: 'budgetProgress', title: '预算使用', width: 120 },
  { colKey: 'createTime', title: '创建时间', width: 120 },
  { colKey: 'operation', title: '操作', width: 200, fixed: 'right' }
];

const targetColumns = [
  { colKey: 'customerName', title: '客户名称', width: 200 },
  { colKey: 'contactPerson', title: '联系人', width: 100 },
  { colKey: 'contactPhone', title: '联系电话', width: 120 },
  { colKey: 'operation', title: '操作', width: 80 }
];

const logColumns = [
  { colKey: 'operator', title: '操作人', width: 100 },
  { colKey: 'action', title: '操作内容', width: 200 },
  { colKey: 'createTime', title: '操作时间', width: 150 }
];

const rules = {
  campaignName: [{ required: true, message: '请输入活动名称', type: 'error' }],
  campaignType: [{ required: true, message: '请选择活动类型', type: 'error' }],
  owner: [{ required: true, message: '请输入负责人', type: 'error' }],
  startDate: [{ required: true, message: '请选择开始日期', type: 'error' }],
  endDate: [{ required: true, message: '请选择结束日期', type: 'error' }],
  budget: [{ required: true, message: '请输入活动预算', type: 'error' }]
};

const tableData = ref([
  {
    id: 1,
    campaignCode: 'MC202600001',
    campaignName: '2026春季新品推广活动',
    campaignType: '线上推广',
    owner: '张三',
    startDate: '2026-03-01',
    endDate: '2026-03-31',
    budget: 300000,
    targetCustomers: 500,
    objective: '推广2026春季新品，提高品牌知名度',
    description: '通过多渠道线上推广，提升新品曝光率',
    remark: '',
    targets: [
      { customerName: '上海科技有限公司', contactPerson: '李四', contactPhone: '13800138000' }
    ],
    actualReach: 0,
    generatedLeads: 0,
    convertedOrders: 0,
    actualSales: 0,
    roi: '',
    resultAnalysis: '',
    status: 'planned',
    budgetProgress: 0,
    createTime: '2026-02-18 10:30:00'
  },
  {
    id: 2,
    campaignCode: 'MC202600002',
    campaignName: '客户回访月活动',
    campaignType: '电话营销',
    owner: '李四',
    startDate: '2026-02-01',
    endDate: '2026-02-29',
    budget: 50000,
    targetCustomers: 200,
    objective: '提高客户满意度，挖掘潜在需求',
    description: '对老客户进行电话回访，了解产品使用情况',
    remark: '',
    targets: [],
    actualReach: 180,
    generatedLeads: 25,
    convertedOrders: 8,
    actualSales: 120000,
    roi: '240%',
    resultAnalysis: '活动效果良好，ROI达到240%',
    status: 'ongoing',
    budgetProgress: 75,
    createTime: '2026-01-28 14:20:00'
  }
]);

const handleSearch = () => {
  MessagePlugin.success('查询成功');
};

const handleReset = () => {
  Object.assign(searchForm, {
    campaignCode: '',
    campaignName: '',
    campaignType: '',
    status: ''
  });
};

const handleAdd = () => {
  dialogTitle.value = '新增活动';
  dialogVisible.value = true;
  activeTab.value = 'basic';
};

const handleEdit = (row: any) => {
  dialogTitle.value = '编辑活动';
  dialogVisible.value = true;
  activeTab.value = 'basic';
  Object.assign(formData, row);
};

const handleView = (row: any) => {
  detailVisible.value = true;
  detailActiveTab.value = 'basic';
  Object.assign(detailData, row);
  detailData.logs = [
    { operator: '张三', action: '创建活动', createTime: '2026-02-18 10:30:00' }
  ];
};

const handleStart = (row: any) => {
  MessagePlugin.success(`活动 ${row.campaignName} 已启动`);
};

const handleStop = (row: any) => {
  MessagePlugin.success(`活动 ${row.campaignName} 已暂停`);
};

const handleBatchStart = () => {
  MessagePlugin.success('批量启动成功');
};

const handleAddTarget = () => {
  if (!formData.targets) formData.targets = [];
  formData.targets.push({ customerName: '', contactPerson: '', contactPhone: '' });
};

const handleDeleteTarget = (index: number) => {
  formData.targets.splice(index, 1);
};

const handleDelete = (row: any) => {
  MessagePlugin.success('活动已删除');
};

const handleSave = () => {
  MessagePlugin.success(dialogTitle.value === '新增活动' ? '活动已创建' : '活动已更新');
  dialogVisible.value = false;
};

const handleDialogClose = () => {
  Object.assign(formData, {
    id: '',
    campaignCode: '',
    campaignName: '',
    campaignType: '',
    owner: '',
    startDate: '',
    endDate: '',
    budget: 0,
    targetCustomers: 0,
    objective: '',
    description: '',
    remark: '',
    targets: [],
    actualReach: 0,
    generatedLeads: 0,
    convertedOrders: 0,
    actualSales: 0,
    roi: '',
    resultAnalysis: ''
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
  console.log('MarketingCampaign mounted');
});
</script>

<style scoped lang="less">
.marketing-campaign-container {
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
