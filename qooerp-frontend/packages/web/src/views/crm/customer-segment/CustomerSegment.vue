<template>
  <div class="customer-segment-container">
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
          <t-statistic title="分段数量" :value="statistics.segments" :loading="loading">
            <template #prefix><icon-layer /></template>
          </t-statistic>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="warning">
          <t-statistic title="覆盖客户" :value="statistics.covered" :loading="loading">
            <template #prefix><icon-check-circle /></template>
          </t-statistic>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="default">
          <t-statistic title="未分段" :value="statistics.uncovered" :loading="loading">
            <template #prefix><icon-minus-circle /></template>
          </t-statistic>
        </t-card>
      </t-col>
    </t-row>

    <!-- 操作栏 -->
    <div class="mb-4 flex justify-between items-center">
      <div>
        <t-button theme="primary" @click="handleAdd">
          <template #icon><icon-add /></template>
          新建分段
        </t-button>
        <t-button theme="default" @click="handleAutoSegment">
          <template #icon><icon-tools /></template>
          自动分段
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

    <!-- 分段概览 -->
    <t-card title="客户分段概览" class="mb-4">
      <t-row :gutter="16">
        <t-col v-for="segment in segmentOverview" :key="segment.id" :span="6">
          <div class="segment-card" :style="{ borderColor: segment.color }">
            <div class="segment-header">
              <div class="segment-icon" :style="{ backgroundColor: segment.color }">
                <icon-user :style="{ color: 'white' }" />
              </div>
              <div class="segment-title">{{ segment.name }}</div>
            </div>
            <div class="segment-body">
              <div class="segment-count">{{ segment.count }}</div>
              <div class="segment-percent">{{ segment.percent }}%</div>
            </div>
            <div class="segment-footer">
              <span>占比: {{ segment.percent }}%</span>
            </div>
          </div>
        </t-col>
      </t-row>
    </t-card>

    <!-- 分段列表 -->
    <t-card title="客户分段列表">
      <t-table
        :data="tableData"
        :columns="columns"
        :loading="loading"
        :pagination="pagination"
        row-key="id"
        @page-change="handlePageChange"
      >
        <template #status="{ row }">
          <t-tag v-if="row.status === 'active'" theme="success" variant="light">启用</t-tag>
          <t-tag v-else theme="default" variant="light">禁用</t-tag>
        </template>
        <template #operation="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleView(row)">查看</t-link>
            <t-link theme="primary" @click="handleEdit(row)">编辑</t-link>
            <t-link theme="primary" @click="handleViewCustomers(row)">客户列表</t-link>
            <t-link theme="danger" @click="handleDelete(row)">删除</t-link>
          </t-space>
        </template>
      </t-table>
    </t-card>

    <!-- 新建/编辑分段弹窗 -->
    <t-dialog
      v-model:visible="dialogVisible"
      :header="dialogTitle"
      width="800px"
      :footer="false"
      @close="handleDialogClose"
    >
      <t-form :data="formData" ref="formRef" :rules="rules" label-width="120px">
        <t-form-item label="分段名称" name="name">
          <t-input v-model="formData.name" placeholder="请输入分段名称" />
        </t-form-item>
        <t-form-item label="分段描述" name="description">
          <t-textarea v-model="formData.description" placeholder="请输入分段描述" :maxlength="500" />
        </t-form-item>
        <t-form-item label="分段规则" name="rules">
          <t-radio-group v-model="formData.ruleType">
            <t-radio value="manual">手动选择</t-radio>
            <t-radio value="auto">自动规则</t-radio>
          </t-radio-group>
        </t-form-item>
        <t-form-item v-if="formData.ruleType === 'auto'" label="规则条件" name="conditions">
          <div class="rules-container">
            <div v-for="(condition, index) in formData.conditions" :key="index" class="rule-item">
              <t-select v-model="condition.field" placeholder="选择字段" style="width: 150px">
                <t-option value="customerType" label="客户类型" />
                <t-option value="customerLevel" label="客户级别" />
                <t-option value="industry" label="所属行业" />
                <t-option value="totalAmount" label="累计金额" />
                <t-option value="orderCount" label="订单数量" />
              </t-select>
              <t-select v-model="condition.operator" placeholder="操作符" style="width: 100px">
                <t-option value="eq" label="等于" />
                <t-option value="ne" label="不等于" />
                <t-option value="gt" label="大于" />
                <t-option value="gte" label="大于等于" />
                <t-option value="lt" label="小于" />
                <t-option value="lte" label="小于等于" />
                <t-option value="contains" label="包含" />
              </t-select>
              <t-input v-model="condition.value" placeholder="值" style="width: 200px" />
              <t-button theme="danger" variant="text" @click="removeCondition(index)">
                <icon-delete />
              </t-button>
            </div>
            <t-button theme="default" variant="dashed" @click="addCondition">
              <icon-add /> 添加条件
            </t-button>
          </div>
        </t-form-item>
        <t-row :gutter="16">
          <t-col :span="12">
            <t-form-item label="分段颜色" name="color">
              <t-color-picker v-model="formData.color" format="HEX" />
            </t-form-item>
          </t-col>
          <t-col :span="12">
            <t-form-item label="状态" name="status">
              <t-select v-model="formData.status" placeholder="请选择状态">
                <t-option value="active" label="启用" />
                <t-option value="inactive" label="禁用" />
              </t-select>
            </t-form-item>
          </t-col>
        </t-row>
        <t-form-item label="备注" name="remark">
          <t-textarea v-model="formData.remark" placeholder="请输入备注" :maxlength="500" />
        </t-form-item>
      </t-form>
      <div class="dialog-footer">
        <t-button theme="default" @click="dialogVisible = false">取消</t-button>
        <t-button theme="primary" @click="handleSave">保存</t-button>
      </div>
    </t-dialog>

    <!-- 分段详情弹窗 -->
    <t-dialog
      v-model:visible="detailVisible"
      header="分段详情"
      width="900px"
      :footer="false"
    >
      <t-tabs v-model="detailActiveTab" theme="card">
        <t-tab-panel value="basic" label="基本信息">
          <t-descriptions :column="2" bordered>
            <t-descriptions-item label="分段名称">{{ detailData.name }}</t-descriptions-item>
            <t-descriptions-item label="客户数量">{{ detailData.customerCount }}</t-descriptions-item>
            <t-descriptions-item label="占比">{{ detailData.percent }}%</t-descriptions-item>
            <t-descriptions-item label="分段类型">
              <t-tag v-if="detailData.ruleType === 'manual'" theme="default">手动选择</t-tag>
              <t-tag v-else theme="primary">自动规则</t-tag>
            </t-descriptions-item>
            <t-descriptions-item label="状态" :span="2">
              <t-tag v-if="detailData.status === 'active'" theme="success">启用</t-tag>
              <t-tag v-else>禁用</t-tag>
            </t-descriptions-item>
            <t-descriptions-item label="分段描述" :span="2">{{ detailData.description }}</t-descriptions-item>
            <t-descriptions-item label="创建时间">{{ detailData.createTime }}</t-descriptions-item>
            <t-descriptions-item label="更新时间">{{ detailData.updateTime }}</t-descriptions-item>
          </t-descriptions>
        </t-tab-panel>

        <t-tab-panel value="customers" label="客户列表">
          <t-table :data="detailData.customers || []" :columns="customerColumns" :pagination="false" />
        </t-tab-panel>

        <t-tab-panel value="logs" label="操作日志">
          <t-table :data="detailData.logs || []" :columns="logColumns" :pagination="false" />
        </t-tab-panel>
      </t-tabs>
    </t-dialog>

    <!-- 客户列表弹窗 -->
    <t-dialog
      v-model:visible="customersVisible"
      header="分段客户列表"
      width="1000px"
      :footer="false"
    >
      <t-card header-bordered>
        <template #header>
          <span>{{ currentSegment?.name }}</span>
          <span class="customer-count">（{{ customerList.length }} 个客户）</span>
        </template>
        <t-table :data="customerList" :columns="customerColumns" :pagination="false" />
      </t-card>
    </t-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, h } from 'vue';
import { MessagePlugin } from 'tdesign-vue-next';
import {
  IconUser, IconLayer, IconCheckCircle, IconMinusCircle,
  IconAdd, IconTools, IconDownload, IconRefresh, IconDelete
} from 'tdesign-icons-vue-next';

const loading = ref(false);
const dialogVisible = ref(false);
const detailVisible = ref(false);
const customersVisible = ref(false);
const dialogTitle = ref('新建分段');
const detailActiveTab = ref('basic');
const formRef = ref();
const currentSegment = ref<any>(null);

const statistics = reactive({
  total: 156,
  segments: 5,
  covered: 145,
  uncovered: 11
});

const formData = reactive({
  id: '',
  name: '',
  description: '',
  ruleType: 'manual',
  conditions: [],
  color: '#0052d9',
  status: 'active',
  remark: ''
});

const detailData = reactive<any>({});

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 5
});

const columns = [
  { colKey: 'name', title: '分段名称', width: 150 },
  { colKey: 'customerCount', title: '客户数量', width: 100 },
  { colKey: 'percent', title: '占比', width: 80 },
  { colKey: 'ruleType', title: '分段类型', width: 120 },
  { colKey: 'description', title: '分段描述', width: 200, ellipsis: true },
  { colKey: 'status', title: '状态', width: 80 },
  { colKey: 'updateTime', title: '更新时间', width: 150 },
  { colKey: 'operation', title: '操作', width: 200, fixed: 'right' }
];

const customerColumns = [
  { colKey: 'customerCode', title: '客户编号', width: 130 },
  { colKey: 'customerName', title: '客户名称', width: 150 },
  { colKey: 'customerType', title: '客户类型', width: 100 },
  { colKey: 'customerLevel', title: '客户级别', width: 80 },
  { colKey: 'salesPerson', title: '销售负责人', width: 100 },
  { colKey: 'totalAmount', title: '累计金额', width: 120 },
  { colKey: 'lastOrderDate', title:最后下单日期, width: 120 }
];

const logColumns = [
  { colKey: 'operator', title: '操作人', width: 100 },
  { colKey: 'action', title: '操作内容', width: 200 },
  { colKey: 'createTime', title: '操作时间', width: 150 }
];

const rules = {
  name: [{ required: true, message: '请输入分段名称', type: 'error' }],
  color: [{ required: true, message: '请选择分段颜色', type: 'error' }]
};

const segmentOverview = ref([
  { id: 1, name: '核心客户', count: 32, percent: 20.5, color: '#d54941' },
  { id: 2, name: '重要客户', count: 45, percent: 28.8, color: '#ed7b2f' },
  { id: 3, name: '普通客户', count: 56, percent: 35.9, color: '#0052d9' },
  { id: 4, name: '潜在客户', count: 12, percent: 7.7, color: '#00a870' },
  { id: 5, name: '流失客户', count: 11, percent: 7.1, color: '#c9cdd4' }
]);

const customerList = ref([
  { customerCode: 'C202600001', customerName: '上海科技有限公司', customerType: '企业客户', customerLevel: 'A', salesPerson: '张三', totalAmount: 256800, lastOrderDate: '2026-02-15' },
  { customerCode: 'C202600002', customerName: '北京贸易公司', customerType: '企业客户', customerLevel: 'B', salesPerson: '李四', totalAmount: 125600, lastOrderDate: '2026-02-10' }
]);

const tableData = ref([
  {
    id: 1,
    name: '核心客户',
    description: 'A级客户，月消费超过5万元',
    customerCount: 32,
    percent: 20.5,
    ruleType: 'auto',
    status: 'active',
    color: '#d54941',
    remark: '',
    createTime: '2026-01-01 10:00:00',
    updateTime: '2026-02-10 14:00:00'
  },
  {
    id: 2,
    name: '重要客户',
    description: 'B级客户，月消费超过2万元',
    customerCount: 45,
    percent: 28.8,
    ruleType: 'auto',
    status: 'active',
    color: '#ed7b2f',
    remark: '',
    createTime: '2026-01-01 10:00:00',
    updateTime: '2026-02-10 14:00:00'
  },
  {
    id: 3,
    name: '普通客户',
    description: 'C级客户，月消费超过5千元',
    customerCount: 56,
    percent: 35.9,
    ruleType: 'auto',
    status: 'active',
    color: '#0052d9',
    remark: '',
    createTime: '2026-01-01 10:00:00',
    updateTime: '2026-02-10 14:00:00'
  }
]);

const handleAdd = () => {
  dialogTitle.value = '新建分段';
  dialogVisible.value = true;
};

const handleEdit = (row: any) => {
  dialogTitle.value = '编辑分段';
  dialogVisible.value = true;
  Object.assign(formData, row);
};

const handleView = (row: any) => {
  detailVisible.value = true;
  detailActiveTab.value = 'basic';
  Object.assign(detailData, row);
  detailData.customers = customerList.value;
  detailData.logs = [
    { operator: '管理员', action: '创建分段', createTime: '2026-01-01 10:00:00' }
  ];
};

const handleViewCustomers = (row: any) => {
  currentSegment.value = row;
  customersVisible.value = true;
};

const handleDelete = (row: any) => {
  MessagePlugin.success('分段已删除');
};

const handleAutoSegment = () => {
  MessagePlugin.info('自动分段功能开发中');
};

const handleSave = () => {
  MessagePlugin.success(dialogTitle.value === '新建分段' ? '分段已创建' : '分段已更新');
  dialogVisible.value = false;
};

const handleDialogClose = () => {
  Object.assign(formData, {
    id: '',
    name: '',
    description: '',
    ruleType: 'manual',
    conditions: [],
    color: '#0052d9',
    status: 'active',
    remark: ''
  });
};

const addCondition = () => {
  formData.conditions.push({ field: '', operator: 'eq', value: '' });
};

const removeCondition = (index: number) => {
  formData.conditions.splice(index, 1);
};

const handleExport = () => {
  MessagePlugin.success('报表导出成功');
};

const handleRefresh = () => {
  MessagePlugin.success('数据已刷新');
};

const handlePageChange = (pageInfo: any) => {
  pagination.current = pageInfo.current;
  pagination.pageSize = pageInfo.pageSize;
};

onMounted(() => {
  console.log('CustomerSegment mounted');
});
</script>

<style scoped lang="less">
.customer-segment-container {
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

.segment-card {
  border: 2px solid #e7e7e7;
  border-radius: 8px;
  padding: 16px;
  background-color: white;
  transition: all 0.3s;

  &:hover {
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    transform: translateY(-2px);
  }

  .segment-header {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 16px;
  }

  .segment-icon {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .segment-title {
    font-size: 16px;
    font-weight: 500;
    color: #333;
  }

  .segment-body {
    display: flex;
    justify-content: space-between;
    align-items: flex-end;
    margin-bottom: 12px;
  }

  .segment-count {
    font-size: 28px;
    font-weight: bold;
    color: #0052d9;
  }

  .segment-percent {
    font-size: 24px;
    font-weight: 500;
    color: #666;
  }

  .segment-footer {
    font-size: 13px;
    color: #999;
  }
}

.rules-container {
  border: 1px solid #e7e7e7;
  border-radius: 4px;
  padding: 16px;
}

.rule-item {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}

.customer-count {
  margin-left: 8px;
  color: #999;
  font-size: 14px;
}
</style>
