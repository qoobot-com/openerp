<template>
  <div class="customer-service-container">
    <!-- 统计卡片 -->
    <t-row :gutter="16" class="mb-4">
      <t-col :span="6">
        <t-card theme="primary">
          <t-statistic title="服务工单总数" :value="statistics.total" :loading="loading">
            <template #prefix><icon-service /></template>
          </t-statistic>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="warning">
          <t-statistic title="处理中" :value="statistics.processing" :loading="loading">
            <template #prefix><icon-time /></template>
          </t-statistic>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="success">
          <t-statistic title="已完成" :value="statistics.completed" :loading="loading">
            <template #prefix><icon-check-circle /></template>
          </t-statistic>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="danger">
          <t-statistic title="满意度" :value="statistics.satisfaction" suffix="%" :loading="loading">
            <template #prefix><icon-heart /></template>
          </t-statistic>
        </t-card>
      </t-col>
    </t-row>

    <!-- 搜索卡片 -->
    <t-card class="mb-4">
      <t-form :data="searchForm" layout="inline" @submit="handleSearch">
        <t-form-item label="工单编号" name="ticketCode">
          <t-input v-model="searchForm.ticketCode" placeholder="请输入工单编号" clearable />
        </t-form-item>
        <t-form-item label="客户名称" name="customerName">
          <t-input v-model="searchForm.customerName" placeholder="请输入客户名称" clearable />
        </t-form-item>
        <t-form-item label="服务类型" name="serviceType">
          <t-select v-model="searchForm.serviceType" placeholder="请选择服务类型" clearable>
            <t-option value="咨询" label="咨询" />
            <t-option value="投诉" label="投诉" />
            <t-option value="建议" label="建议" />
            <t-option value="故障" label="故障" />
            <t-option value="其他" label="其他" />
          </t-select>
        </t-form-item>
        <t-form-item label="工单状态" name="status">
          <t-select v-model="searchForm.status" placeholder="请选择工单状态" clearable>
            <t-option value="pending" label="待处理" />
            <t-option value="processing" label="处理中" />
            <t-option value="completed" label="已完成" />
            <t-option value="closed" label="已关闭" />
          </t-select>
        </t-form-item>
        <t-form-item label="处理人" name="handler">
          <t-input v-model="searchForm.handler" placeholder="请输入处理人" clearable />
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
          新增工单
        </t-button>
        <t-button theme="default" @click="handleBatchAssign">
          <template #icon><icon-user /></template>
          批量分配
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

    <!-- 服务工单列表 -->
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
        <template #priority="{ row }">
          <t-tag v-if="row.priority === 'urgent'" theme="danger" variant="light">紧急</t-tag>
          <t-tag v-else-if="row.priority === 'high'" theme="warning" variant="light">高</t-tag>
          <t-tag v-else-if="row.priority === 'medium'" theme="primary" variant="light">中</t-tag>
          <t-tag v-else theme="default" variant="light">低</t-tag>
        </template>
        <template #status="{ row }">
          <t-tag v-if="row.status === 'pending'" theme="warning" variant="light">待处理</t-tag>
          <t-tag v-else-if="row.status === 'processing'" theme="primary" variant="light">处理中</t-tag>
          <t-tag v-else-if="row.status === 'completed'" theme="success" variant="light">已完成</t-tag>
          <t-tag v-else theme="default" variant="light">已关闭</t-tag>
        </template>
        <template #operation="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleView(row)">查看</t-link>
            <t-link theme="primary" @click="handleEdit(row)">编辑</t-link>
            <t-link theme="success" @click="handleAssign(row)">分配</t-link>
            <t-link theme="success" @click="handleComplete(row)">完成</t-link>
            <t-popconfirm content="确定要删除此工单吗？" @confirm="handleDelete(row)">
              <t-link theme="danger">删除</t-link>
            </t-popconfirm>
          </t-space>
        </template>
      </t-table>
    </t-card>

    <!-- 新增/编辑工单弹窗 -->
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
                <t-form-item label="工单编号" name="ticketCode">
                  <t-input v-model="formData.ticketCode" placeholder="自动生成" disabled />
                </t-form-item>
              </t-col>
              <t-col :span="12">
                <t-form-item label="关联客户" name="customerId">
                  <t-select v-model="formData.customerId" placeholder="请选择客户" filterable>
                    <t-option v-for="item in customers" :key="item.id" :value="item.id" :label="item.name" />
                  </t-select>
                </t-form-item>
              </t-col>
            </t-row>
            <t-row :gutter="16">
              <t-col :span="12">
                <t-form-item label="服务类型" name="serviceType">
                  <t-select v-model="formData.serviceType" placeholder="请选择服务类型">
                    <t-option value="咨询" label="咨询" />
                    <t-option value="投诉" label="投诉" />
                    <t-option value="建议" label="建议" />
                    <t-option value="故障" label="故障" />
                    <t-option value="其他" label="其他" />
                  </t-select>
                </t-form-item>
              </t-col>
              <t-col :span="12">
                <t-form-item label="优先级" name="priority">
                  <t-select v-model="formData.priority" placeholder="请选择优先级">
                    <t-option value="urgent" label="紧急" />
                    <t-option value="high" label="高" />
                    <t-option value="medium" label="中" />
                    <t-option value="low" label="低" />
                  </t-select>
                </t-form-item>
              </t-col>
            </t-row>
            <t-row :gutter="16">
              <t-col :span="12">
                <t-form-item label="联系人" name="contactPerson">
                  <t-input v-model="formData.contactPerson" placeholder="请输入联系人" />
                </t-form-item>
              </t-col>
              <t-col :span="12">
                <t-form-item label="联系电话" name="contactPhone">
                  <t-input v-model="formData.contactPhone" placeholder="请输入联系电话" />
                </t-form-item>
              </t-col>
            </t-row>
            <t-form-item label="问题描述" name="description">
              <t-textarea v-model="formData.description" placeholder="请详细描述问题" :maxlength="1000" />
            </t-form-item>
            <t-form-item label="备注" name="remark">
              <t-textarea v-model="formData.remark" placeholder="请输入备注" :maxlength="500" />
            </t-form-item>
          </t-form>
        </t-tab-panel>

        <!-- 处理信息 -->
        <t-tab-panel value="handle" label="处理信息">
          <t-form :data="formData" label-width="120px">
            <t-form-item label="处理人" name="handler">
              <t-input v-model="formData.handler" placeholder="请输入处理人" />
            </t-form-item>
            <t-form-item label="处理结果" name="handleResult">
              <t-textarea v-model="formData.handleResult" placeholder="请输入处理结果" />
            </t-form-item>
            <t-form-item label="工单状态" name="status">
              <t-select v-model="formData.status" placeholder="请选择工单状态">
                <t-option value="pending" label="待处理" />
                <t-option value="processing" label="处理中" />
                <t-option value="completed" label="已完成" />
                <t-option value="closed" label="已关闭" />
              </t-select>
            </t-form-item>
          </t-form>
        </t-tab-panel>

        <!-- 评价信息 -->
        <t-tab-panel value="evaluation" label="评价信息">
          <t-form :data="formData" label-width="120px">
            <t-form-item label="满意度" name="satisfaction">
              <t-rate v-model="formData.satisfaction" allow-half :count="5" />
            </t-form-item>
            <t-form-item label="评价内容" name="evaluation">
              <t-textarea v-model="formData.evaluation" placeholder="请输入评价内容" />
            </t-form-item>
          </t-form>
        </t-tab-panel>
      </t-tabs>

      <div class="dialog-footer">
        <t-button theme="default" @click="dialogVisible = false">取消</t-button>
        <t-button theme="primary" @click="handleSave">保存</t-button>
      </div>
    </t-dialog>

    <!-- 工单详情弹窗 -->
    <t-dialog
      v-model:visible="detailVisible"
      header="工单详情"
      width="900px"
      :footer="false"
    >
      <t-tabs v-model="detailActiveTab" theme="card">
        <t-tab-panel value="basic" label="基本信息">
          <t-descriptions :column="2" bordered>
            <t-descriptions-item label="工单编号">{{ detailData.ticketCode }}</t-descriptions-item>
            <t-descriptions-item label="关联客户">{{ detailData.customerName }}</t-descriptions-item>
            <t-descriptions-item label="服务类型">{{ detailData.serviceType }}</t-descriptions-item>
            <t-descriptions-item label="优先级">
              <t-tag v-if="detailData.priority === 'urgent'" theme="danger">紧急</t-tag>
              <t-tag v-else-if="detailData.priority === 'high'" theme="warning">高</t-tag>
              <t-tag v-else-if="detailData.priority === 'medium'" theme="primary">中</t-tag>
              <t-tag v-else theme="default">低</t-tag>
            </t-descriptions-item>
            <t-descriptions-item label="联系人">{{ detailData.contactPerson }}</t-descriptions-item>
            <t-descriptions-item label="联系电话">{{ detailData.contactPhone }}</t-descriptions-item>
            <t-descriptions-item label="工单状态">
              <t-tag v-if="detailData.status === 'pending'" theme="warning">待处理</t-tag>
              <t-tag v-else-if="detailData.status === 'processing'" theme="primary">处理中</t-tag>
              <t-tag v-else-if="detailData.status === 'completed'" theme="success">已完成</t-tag>
              <t-tag v-else theme="default">已关闭</t-tag>
            </t-descriptions-item>
            <t-descriptions-item label="处理人">{{ detailData.handler }}</t-descriptions-item>
            <t-descriptions-item label="创建时间" :span="2">{{ detailData.createTime }}</t-descriptions-item>
            <t-descriptions-item label="问题描述" :span="2">{{ detailData.description }}</t-descriptions-item>
            <t-descriptions-item label="备注" :span="2">{{ detailData.remark }}</t-descriptions-item>
          </t-descriptions>
        </t-tab-panel>

        <t-tab-panel value="handle" label="处理信息">
          <t-descriptions :column="1" bordered>
            <t-descriptions-item label="处理人">{{ detailData.handler }}</t-descriptions-item>
            <t-descriptions-item label="处理结果">{{ detailData.handleResult }}</t-descriptions-item>
            <t-descriptions-item label="完成时间">{{ detailData.completeTime }}</t-descriptions-item>
          </t-descriptions>
        </t-tab-panel>

        <t-tab-panel value="evaluation" label="评价信息">
          <t-descriptions :column="1" bordered>
            <t-descriptions-item label="满意度">
              <t-rate :value="detailData.satisfaction" disabled :count="5" allow-half />
            </t-descriptions-item>
            <t-descriptions-item label="评价内容">{{ detailData.evaluation }}</t-descriptions-item>
            <t-descriptions-item label="评价时间">{{ detailData.evaluationTime }}</t-descriptions-item>
          </t-descriptions>
        </t-tab-panel>

        <t-tab-panel value="logs" label="操作日志">
          <t-table :data="detailData.logs || []" :columns="logColumns" :pagination="false" />
        </t-tab-panel>
      </t-tabs>
    </t-dialog>

    <!-- 分配处理人弹窗 -->
    <t-dialog
      v-model:visible="assignVisible"
      header="分配处理人"
      width="500px"
      :footer="false"
    >
      <t-form :data="assignForm" label-width="100px">
        <t-form-item label="处理人" name="handler">
          <t-input v-model="assignForm.handler" placeholder="请输入处理人" />
        </t-form-item>
        <t-form-item label="分配说明" name="assignNote">
          <t-textarea v-model="assignForm.assignNote" placeholder="请输入分配说明" />
        </t-form-item>
      </t-form>
      <div class="dialog-footer">
        <t-button theme="default" @click="assignVisible = false">取消</t-button>
        <t-button theme="primary" @click="handleSaveAssign">确认分配</t-button>
      </div>
    </t-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { MessagePlugin } from 'tdesign-vue-next';
import {
  IconService, IconTime, IconCheckCircle, IconHeart,
  IconAdd, IconUser, IconDownload, IconRefresh
} from 'tdesign-icons-vue-next';

const loading = ref(false);
const dialogVisible = ref(false);
const detailVisible = ref(false);
const assignVisible = ref(false);
const dialogTitle = ref('新增工单');
const activeTab = ref('basic');
const detailActiveTab = ref('basic');
const formRef = ref();
const selectedRowKeys = ref([]);

const statistics = reactive({
  total: 234,
  processing: 45,
  completed: 178,
  satisfaction: 92
});

const searchForm = reactive({
  ticketCode: '',
  customerName: '',
  serviceType: '',
  status: '',
  handler: ''
});

const formData = reactive({
  id: '',
  ticketCode: '',
  customerId: '',
  serviceType: '',
  priority: 'medium',
  contactPerson: '',
  contactPhone: '',
  description: '',
  remark: '',
  handler: '',
  handleResult: '',
  status: 'pending',
  satisfaction: 5,
  evaluation: ''
});

const assignForm = reactive({
  ticketId: '',
  handler: '',
  assignNote: ''
});

const detailData = reactive<any>({});

const pagination = reactive({
  current: 1,
  pageSize: 20,
  total: 234
});

const customers = ref([
  { id: 1, name: '上海科技有限公司' },
  { id: 2, name: '北京贸易公司' }
]);

const columns = [
  { colKey: 'ticketCode', title: '工单编号', width: 130 },
  { colKey: 'customerName', title: '客户名称', width: 150 },
  { colKey: 'serviceType', title: '服务类型', width: 80 },
  { colKey: 'priority', title: '优先级', width: 80 },
  { colKey: 'contactPerson', title: '联系人', width: 100 },
  { colKey: 'contactPhone', title: '联系电话', width: 120 },
  { colKey: 'handler', title: '处理人', width: 100 },
  { colKey: 'status', title: '工单状态', width: 90 },
  { colKey: 'createTime', title: '创建时间', width: 120 },
  { colKey: 'operation', title: '操作', width: 220, fixed: 'right' }
];

const logColumns = [
  { colKey: 'operator', title: '操作人', width: 100 },
  { colKey: 'action', title: '操作内容', width: 200 },
  { colKey: 'createTime', title: '操作时间', width: 150 }
];

const rules = {
  customerId: [{ required: true, message: '请选择客户', type: 'error' }],
  serviceType: [{ required: true, message: '请选择服务类型', type: 'error' }],
  description: [{ required: true, message: '请输入问题描述', type: 'error' }]
};

const tableData = ref([
  {
    id: 1,
    ticketCode: 'TS202600001',
    customerId: 1,
    customerName: '上海科技有限公司',
    serviceType: '故障',
    priority: 'urgent',
    contactPerson: '李四',
    contactPhone: '13800138000',
    description: 'ERP系统登录失败，无法正常使用系统',
    remark: '',
    handler: '张三',
    handleResult: '已修复登录问题，系统已恢复正常',
    status: 'completed',
    satisfaction: 5,
    evaluation: '处理及时，服务态度很好',
    createTime: '2026-02-18 10:30:00',
    completeTime: '2026-02-18 14:20:00',
    evaluationTime: '2026-02-18 15:00:00'
  },
  {
    id: 2,
    ticketCode: 'TS202600002',
    customerId: 2,
    customerName: '北京贸易公司',
    serviceType: '咨询',
    priority: 'medium',
    contactPerson: '赵六',
    contactPhone: '13900139000',
    description: '咨询CRM系统的自定义字段功能',
    remark: '',
    handler: '',
    handleResult: '',
    status: 'pending',
    satisfaction: 0,
    evaluation: '',
    createTime: '2026-02-19 09:15:00'
  }
]);

const handleSearch = () => {
  MessagePlugin.success('查询成功');
};

const handleReset = () => {
  Object.assign(searchForm, {
    ticketCode: '',
    customerName: '',
    serviceType: '',
    status: '',
    handler: ''
  });
};

const handleAdd = () => {
  dialogTitle.value = '新增工单';
  dialogVisible.value = true;
  activeTab.value = 'basic';
};

const handleEdit = (row: any) => {
  dialogTitle.value = '编辑工单';
  dialogVisible.value = true;
  activeTab.value = 'basic';
  Object.assign(formData, row);
};

const handleView = (row: any) => {
  detailVisible.value = true;
  detailActiveTab.value = 'basic';
  Object.assign(detailData, row);
  detailData.logs = [
    { operator: '系统', action: '创建工单', createTime: '2026-02-18 10:30:00' },
    { operator: '张三', action: '分配处理人', createTime: '2026-02-18 10:35:00' },
    { operator: '张三', action: '完成处理', createTime: '2026-02-18 14:20:00' }
  ];
};

const handleAssign = (row: any) => {
  assignForm.ticketId = row.id;
  assignVisible.value = true;
};

const handleSaveAssign = () => {
  MessagePlugin.success('分配成功');
  assignVisible.value = false;
};

const handleBatchAssign = () => {
  MessagePlugin.success('批量分配成功');
};

const handleComplete = (row: any) => {
  MessagePlugin.success('工单已完成');
};

const handleDelete = (row: any) => {
  MessagePlugin.success('工单已删除');
};

const handleSave = () => {
  MessagePlugin.success(dialogTitle.value === '新增工单' ? '工单已创建' : '工单已更新');
  dialogVisible.value = false;
};

const handleDialogClose = () => {
  Object.assign(formData, {
    id: '',
    ticketCode: '',
    customerId: '',
    serviceType: '',
    priority: 'medium',
    contactPerson: '',
    contactPhone: '',
    description: '',
    remark: '',
    handler: '',
    handleResult: '',
    status: 'pending',
    satisfaction: 5,
    evaluation: ''
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
  console.log('CustomerService mounted');
});
</script>

<style scoped lang="less">
.customer-service-container {
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
