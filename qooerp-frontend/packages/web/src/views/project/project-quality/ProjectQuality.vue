<template>
  <div class="project-quality-container">
    <!-- 统计卡片 -->
    <t-row :gutter="16" class="mb-4">
      <t-col :span="6">
        <t-card theme="primary">
          <t-statistic title="检查总数" :value="statistics.total" :loading="loading">
            <template #prefix><icon-check-circle /></template>
          </t-statistic>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="success">
          <t-statistic title="通过率" :value="statistics.passRate" suffix="%" :loading="loading">
            <template #prefix><icon-check /></template>
          </t-statistic>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="warning">
          <t-statistic title="待检查" :value="statistics.pending" :loading="loading">
            <template #prefix><icon-time /></template>
          </t-statistic>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="danger">
          <t-statistic title="问题数" :value="statistics.issues" :loading="loading">
            <template #prefix><icon-error-circle /></template>
          </t-statistic>
        </t-card>
      </t-col>
    </t-row>

    <!-- 搜索卡片 -->
    <t-card class="mb-4">
      <t-form :data="searchForm" layout="inline" @submit="handleSearch">
        <t-form-item label="检查编号" name="checkCode">
          <t-input v-model="searchForm.checkCode" placeholder="请输入检查编号" clearable />
        </t-form-item>
        <t-form-item label="项目名称" name="projectName">
          <t-input v-model="searchForm.projectName" placeholder="请输入项目名称" clearable />
        </t-form-item>
        <t-form-item label="检查类型" name="checkType">
          <t-select v-model="searchForm.checkType" placeholder="请选择检查类型" clearable>
            <t-option value="代码审查" label="代码审查" />
            <t-option value="功能测试" label="功能测试" />
            <t-option value="性能测试" label="性能测试" />
            <t-option value="安全测试" label="安全测试" />
          </t-select>
        </t-form-item>
        <t-form-item label="检查状态" name="status">
          <t-select v-model="searchForm.status" placeholder="请选择检查状态" clearable>
            <t-option value="pending" label="待检查" />
            <t-option value="passed" label="已通过" />
            <t-option value="failed" label="未通过" />
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
          新增检查
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

    <!-- 检查列表 -->
    <t-card>
      <t-table
        :data="tableData"
        :columns="columns"
        :loading="loading"
        :pagination="pagination"
        row-key="id"
        @page-change="handlePageChange"
      >
        <template #status="{ row }">
          <t-tag v-if="row.status === 'pending'" theme="warning" variant="light">待检查</t-tag>
          <t-tag v-else-if="row.status === 'passed'" theme="success" variant="light">已通过</t-tag>
          <t-tag v-else theme="danger" variant="light">未通过</t-tag>
        </template>
        <template #priority="{ row }">
          <t-tag v-if="row.priority === 'high'" theme="danger" variant="light">高</t-tag>
          <t-tag v-else-if="row.priority === 'medium'" theme="warning" variant="light">中</t-tag>
          <t-tag v-else theme="default" variant="light">低</t-tag>
        </template>
        <template #operation="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleView(row)">查看</t-link>
            <t-link theme="primary" @click="handleEdit(row)">编辑</t-link>
            <t-link v-if="row.status === 'pending'" theme="success" @click="handlePass(row)">通过</t-link>
            <t-link v-if="row.status === 'pending'" theme="danger" @click="handleFail(row)">不通过</t-link>
          </t-space>
        </template>
      </t-table>
    </t-card>

    <!-- 新增/编辑检查弹窗 -->
    <t-dialog
      v-model:visible="dialogVisible"
      :header="dialogTitle"
      width="800px"
      :footer="false"
      @close="handleDialogClose"
    >
      <t-form :data="formData" ref="formRef" :rules="rules" label-width="120px">
        <t-row :gutter="16">
          <t-col :span="12">
            <t-form-item label="检查编号" name="checkCode">
              <t-input v-model="formData.checkCode" placeholder="自动生成" disabled />
            </t-form-item>
          </t-col>
          <t-col :span="12">
            <t-form-item label="关联项目" name="projectId">
              <t-select v-model="formData.projectId" placeholder="请选择项目">
                <t-option value="1" label="ERP系统开发" />
                <t-option value="2" label="CRM系统开发" />
                <t-option value="3" label="移动端开发" />
              </t-select>
            </t-form-item>
          </t-col>
        </t-row>
        <t-row :gutter="16">
          <t-col :span="12">
            <t-form-item label="检查类型" name="checkType">
              <t-select v-model="formData.checkType" placeholder="请选择检查类型">
                <t-option value="代码审查" label="代码审查" />
                <t-option value="功能测试" label="功能测试" />
                <t-option value="性能测试" label="性能测试" />
                <t-option value="安全测试" label="安全测试" />
              </t-select>
            </t-form-item>
          </t-col>
          <t-col :span="12">
            <t-form-item label="优先级" name="priority">
              <t-select v-model="formData.priority" placeholder="请选择优先级">
                <t-option value="high" label="高" />
                <t-option value="medium" label="中" />
                <t-option value="low" label="低" />
              </t-select>
            </t-form-item>
          </t-col>
        </t-row>
        <t-form-item label="检查内容" name="checkContent">
          <t-textarea v-model="formData.checkContent" placeholder="请输入检查内容" :maxlength="1000" />
        </t-form-item>
        <t-row :gutter="16">
          <t-col :span="12">
            <t-form-item label="检查人" name="checker">
              <t-input v-model="formData.checker" placeholder="请输入检查人" />
            </t-form-item>
          </t-col>
          <t-col :span="12">
            <t-form-item label="检查日期" name="checkDate">
              <t-date-picker v-model="formData.checkDate" placeholder="请选择检查日期" />
            </t-form-item>
          </t-col>
        </t-row>
        <t-form-item label="检查标准" name="checkStandard">
          <t-textarea v-model="formData.checkStandard" placeholder="请输入检查标准" :maxlength="1000" />
        </t-form-item>
        <t-form-item label="备注" name="remark">
          <t-textarea v-model="formData.remark" placeholder="请输入备注" :maxlength="500" />
        </t-form-item>
      </t-form>
      <div class="dialog-footer">
        <t-button theme="default" @click="dialogVisible = false">取消</t-button>
        <t-button theme="primary" @click="handleSave">保存</t-button>
      </div>
    </t-dialog>

    <!-- 检查详情弹窗 -->
    <t-dialog
      v-model:visible="detailVisible"
      header="质量检查详情"
      width="900px"
      :footer="false"
    >
      <t-tabs v-model="detailActiveTab" theme="card">
        <t-tab-panel value="basic" label="基本信息">
          <t-descriptions :column="2" bordered>
            <t-descriptions-item label="检查编号">{{ detailData.checkCode }}</t-descriptions-item>
            <t-descriptions-item label="关联项目">{{ detailData.projectName }}</t-descriptions-item>
            <t-descriptions-item label="检查类型">{{ detailData.checkType }}</t-descriptions-item>
            <t-descriptions-item label="优先级">
              <t-tag v-if="detailData.priority === 'high'" theme="danger">高</t-tag>
              <t-tag v-else-if="detailData.priority === 'medium'" theme="warning">中</t-tag>
              <t-tag v-else>低</t-tag>
            </t-descriptions-item>
            <t-descriptions-item label="检查人">{{ detailData.checker }}</t-descriptions-item>
            <t-descriptions-item label="检查日期">{{ detailData.checkDate }}</t-descriptions-item>
            <t-descriptions-item label="检查状态" :span="2">
              <t-tag v-if="detailData.status === 'pending'" theme="warning">待检查</t-tag>
              <t-tag v-else-if="detailData.status === 'passed'" theme="success">已通过</t-tag>
              <t-tag v-else theme="danger">未通过</t-tag>
            </t-descriptions-item>
          </t-descriptions>
        </t-tab-panel>

        <t-tab-panel value="content" label="检查内容">
          <div class="quality-content">{{ detailData.checkContent }}</div>
        </t-tab-panel>

        <t-tab-panel value="standard" label="检查标准">
          <div class="quality-content">{{ detailData.checkStandard }}</div>
        </t-tab-panel>

        <t-tab-panel value="result" label="检查结果">
          <t-form label-width="120px">
            <t-form-item label="检查结果">
              <t-tag v-if="detailData.status === 'passed'" theme="success" size="large">通过</t-tag>
              <t-tag v-else-if="detailData.status === 'failed'" theme="danger" size="large">未通过</t-tag>
              <t-tag v-else theme="warning" size="large">待检查</t-tag>
            </t-form-item>
            <t-form-item label="检查意见">
              <div class="quality-content">{{ detailData.checkResult }}</div>
            </t-form-item>
            <t-form-item label="问题清单">
              <t-table :data="detailData.issues || []" :columns="issueColumns" :pagination="false" />
            </t-form-item>
          </t-form>
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
  IconCheckCircle, IconCheck, IconTime, IconErrorCircle,
  IconAdd, IconDownload, IconRefresh
} from 'tdesign-icons-vue-next';

const loading = ref(false);
const dialogVisible = ref(false);
const detailVisible = ref(false);
const dialogTitle = ref('新增检查');
const detailActiveTab = ref('basic');
const formRef = ref();

const statistics = reactive({
  total: 78,
  passRate: 85,
  pending: 12,
  issues: 15
});

const searchForm = reactive({
  checkCode: '',
  projectName: '',
  checkType: '',
  status: ''
});

const formData = reactive({
  id: '',
  checkCode: '',
  projectId: '',
  checkType: '',
  priority: 'medium',
  checkContent: '',
  checker: '',
  checkDate: '',
  checkStandard: '',
  remark: ''
});

const detailData = reactive<any>({});

const pagination = reactive({
  current: 1,
  pageSize: 20,
  total: 78
});

const columns = [
  { colKey: 'checkCode', title: '检查编号', width: 130 },
  { colKey: 'projectName', title: '关联项目', width: 150 },
  { colKey: 'checkType', title: '检查类型', width: 100 },
  { colKey: 'checkContent', title: '检查内容', width: 200, ellipsis: true },
  { colKey: 'checker', title: '检查人', width: 100 },
  { colKey: 'priority', title: '优先级', width: 80 },
  { colKey: 'status', title: '状态', width: 90 },
  { colKey: 'checkDate', title: '检查日期', width: 120 },
  { colKey: 'operation', title: '操作', width: 180, fixed: 'right' }
];

const issueColumns = [
  { colKey: 'issueNo', title: '问题编号', width: 120 },
  { colKey: 'issueType', title: '问题类型', width: 100 },
  { colKey: 'description', title: '问题描述', width: 200 },
  { colKey: 'severity', title: '严重程度', width: 100 },
  { colKey: 'status', title: '状态', width: 100 }
];

const logColumns = [
  { colKey: 'operator', title: '操作人', width: 100 },
  { colKey: 'action', title: '操作内容', width: 200 },
  { colKey: 'createTime', title: '操作时间', width: 150 }
];

const rules = {
  projectId: [{ required: true, message: '请选择项目', type: 'error' }],
  checkType: [{ required: true, message: '请选择检查类型', type: 'error' }],
  priority: [{ required: true, message: '请选择优先级', type: 'error' }],
  checkContent: [{ required: true, message: '请输入检查内容', type: 'error' }],
  checker: [{ required: true, message: '请输入检查人', type: 'error' }],
  checkStandard: [{ required: true, message: '请输入检查标准', type: 'error' }]
};

const tableData = ref([
  {
    id: 1,
    checkCode: 'PQ202600001',
    projectName: 'ERP系统开发',
    projectId: '1',
    checkType: '代码审查',
    priority: 'high',
    checkContent: '对供应链模块代码进行审查',
    checker: '张三',
    checkDate: '2026-02-19',
    checkStandard: '遵循代码规范和最佳实践',
    status: 'pending',
    remark: '重点检查代码复用性',
    createTime: '2026-02-19 10:00:00'
  }
]);

const handleSearch = () => {
  MessagePlugin.success('查询成功');
};

const handleReset = () => {
  Object.assign(searchForm, {
    checkCode: '',
    projectName: '',
    checkType: '',
    status: ''
  });
};

const handleAdd = () => {
  dialogTitle.value = '新增检查';
  dialogVisible.value = true;
};

const handleEdit = (row: any) => {
  dialogTitle.value = '编辑检查';
  dialogVisible.value = true;
  Object.assign(formData, row);
};

const handleView = (row: any) => {
  detailVisible.value = true;
  detailActiveTab.value = 'basic';
  Object.assign(detailData, row);
  detailData.issues = [
    { issueNo: 'I001', issueType: '代码规范', description: '变量命名不符合规范', severity: 'medium', status: 'open' }
  ];
  detailData.logs = [
    { operator: '张三', action: '创建质量检查', createTime: '2026-02-19 10:00:00' }
  ];
};

const handlePass = (row: any) => {
  MessagePlugin.success('检查已通过');
};

const handleFail = (row: any) => {
  MessagePlugin.success('检查标记为未通过');
};

const handleSave = () => {
  MessagePlugin.success(dialogTitle.value === '新增检查' ? '检查已创建' : '检查已更新');
  dialogVisible.value = false;
};

const handleDialogClose = () => {
  Object.assign(formData, {
    id: '',
    checkCode: '',
    projectId: '',
    checkType: '',
    priority: 'medium',
    checkContent: '',
    checker: '',
    checkDate: '',
    checkStandard: '',
    remark: ''
  });
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
  console.log('ProjectQuality mounted');
});
</script>

<style scoped lang="less">
.project-quality-container {
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

.quality-content {
  padding: 20px;
  background-color: #f5f5f5;
  border-radius: 4px;
  white-space: pre-wrap;
}
</style>
