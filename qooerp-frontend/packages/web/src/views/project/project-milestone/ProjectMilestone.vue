<template>
  <div class="project-milestone-container">
    <!-- 统计卡片 -->
    <t-row :gutter="16" class="mb-4">
      <t-col :span="6">
        <t-card theme="primary">
          <t-statistic title="里程碑总数" :value="statistics.total" :loading="loading">
            <template #prefix><icon-flag /></template>
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
        <t-card theme="warning">
          <t-statistic title="进行中" :value="statistics.inProgress" :loading="loading">
            <template #prefix><icon-time /></template>
          </t-statistic>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="default">
          <t-statistic title="未开始" :value="statistics.notStarted" :loading="loading">
            <template #prefix><icon-calendar /></template>
          </t-statistic>
        </t-card>
      </t-col>
    </t-row>

    <!-- 搜索卡片 -->
    <t-card class="mb-4">
      <t-form :data="searchForm" layout="inline" @submit="handleSearch">
        <t-form-item label="项目名称" name="projectName">
          <t-input v-model="searchForm.projectName" placeholder="请输入项目名称" clearable />
        </t-form-item>
        <t-form-item label="里程碑名称" name="milestoneName">
          <t-input v-model="searchForm.milestoneName" placeholder="请输入里程碑名称" clearable />
        </t-form-item>
        <t-form-item label="状态" name="status">
          <t-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <t-option value="not_started" label="未开始" />
            <t-option value="in_progress" label="进行中" />
            <t-option value="completed" label="已完成" />
            <t-option value="delayed" label="已延期" />
          </t-select>
        </t-form-item>
        <t-form-item label="日期范围" name="dateRange">
          <t-date-range-picker v-model="searchForm.dateRange" placeholder="选择日期范围" />
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
          新增里程碑
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

    <!-- 甘特图视图 -->
    <t-card class="mb-4">
      <div class="gantt-header">
        <span class="gantt-title">项目里程碑甘特图</span>
      </div>
      <div class="gantt-container">
        <div v-for="item in ganttData" :key="item.id" class="gantt-row">
          <div class="gantt-label">{{ item.projectName }}</div>
          <div class="gantt-timeline">
            <div
              v-for="milestone in item.milestones"
              :key="milestone.id"
              class="gantt-bar"
              :class="`status-${milestone.status}`"
              :style="{
                left: milestone.left + '%',
                width: milestone.width + '%'
              }"
              :title="milestone.name"
            >
              {{ milestone.name }}
            </div>
          </div>
        </div>
      </div>
    </t-card>

    <!-- 里程碑列表 -->
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
          <t-tag v-if="row.status === 'not_started'" theme="default" variant="light">未开始</t-tag>
          <t-tag v-else-if="row.status === 'in_progress'" theme="warning" variant="light">进行中</t-tag>
          <t-tag v-else-if="row.status === 'completed'" theme="success" variant="light">已完成</t-tag>
          <t-tag v-else theme="danger" variant="light">已延期</t-tag>
        </template>
        <template #progress="{ row }">
          <t-progress :percentage="row.progress" :show-info="true" :theme="row.status === 'completed' ? 'success' : 'primary'" />
        </template>
        <template #operation="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleView(row)">查看</t-link>
            <t-link theme="primary" @click="handleEdit(row)">编辑</t-link>
            <t-link v-if="row.status !== 'completed'" theme="success" @click="handleComplete(row)">完成</t-link>
            <t-popconfirm content="确定要删除此里程碑吗？" @confirm="handleDelete(row)">
              <t-link theme="danger">删除</t-link>
            </t-popconfirm>
          </t-space>
        </template>
      </t-table>
    </t-card>

    <!-- 新增/编辑里程碑弹窗 -->
    <t-dialog
      v-model:visible="dialogVisible"
      :header="dialogTitle"
      width="700px"
      :footer="false"
      @close="handleDialogClose"
    >
      <t-form :data="formData" ref="formRef" :rules="rules" label-width="120px">
        <t-form-item label="关联项目" name="projectId">
          <t-select v-model="formData.projectId" placeholder="请选择项目">
            <t-option value="1" label="ERP系统开发" />
            <t-option value="2" label="CRM系统开发" />
            <t-option value="3" label="移动端开发" />
          </t-select>
        </t-form-item>
        <t-form-item label="里程碑名称" name="name">
          <t-input v-model="formData.name" placeholder="请输入里程碑名称" />
        </t-form-item>
        <t-form-item label="里程碑描述" name="description">
          <t-textarea v-model="formData.description" placeholder="请输入里程碑描述" :maxlength="1000" />
        </t-form-item>
        <t-row :gutter="16">
          <t-col :span="12">
            <t-form-item label="计划开始日期" name="planStartDate">
              <t-date-picker v-model="formData.planStartDate" placeholder="请选择计划开始日期" />
            </t-form-item>
          </t-col>
          <t-col :span="12">
            <t-form-item label="计划结束日期" name="planEndDate">
              <t-date-picker v-model="formData.planEndDate" placeholder="请选择计划结束日期" />
            </t-form-item>
          </t-col>
        </t-row>
        <t-row :gutter="16">
          <t-col :span="12">
            <t-form-item label="实际开始日期" name="actualStartDate">
              <t-date-picker v-model="formData.actualStartDate" placeholder="请选择实际开始日期" />
            </t-form-item>
          </t-col>
          <t-col :span="12">
            <t-form-item label="实际结束日期" name="actualEndDate">
              <t-date-picker v-model="formData.actualEndDate" placeholder="请选择实际结束日期" />
            </t-form-item>
          </t-col>
        </t-row>
        <t-form-item label="负责人" name="owner">
          <t-input v-model="formData.owner" placeholder="请输入负责人" />
        </t-form-item>
        <t-form-item label="优先级" name="priority">
          <t-select v-model="formData.priority" placeholder="请选择优先级">
            <t-option value="high" label="高" />
            <t-option value="medium" label="中" />
            <t-option value="low" label="低" />
          </t-select>
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

    <!-- 里程碑详情弹窗 -->
    <t-dialog
      v-model:visible="detailVisible"
      header="里程碑详情"
      width="900px"
      :footer="false"
    >
      <t-tabs v-model="detailActiveTab" theme="card">
        <t-tab-panel value="basic" label="基本信息">
          <t-descriptions :column="2" bordered>
            <t-descriptions-item label="关联项目">{{ detailData.projectName }}</t-descriptions-item>
            <t-descriptions-item label="里程碑名称">{{ detailData.name }}</t-descriptions-item>
            <t-descriptions-item label="负责人">{{ detailData.owner }}</t-descriptions-item>
            <t-descriptions-item label="优先级">
              <t-tag v-if="detailData.priority === 'high'" theme="danger">高</t-tag>
              <t-tag v-else-if="detailData.priority === 'medium'" theme="warning">中</t-tag>
              <t-tag v-else>低</t-tag>
            </t-descriptions-item>
            <t-descriptions-item label="计划开始日期">{{ detailData.planStartDate }}</t-descriptions-item>
            <t-descriptions-item label="计划结束日期">{{ detailData.planEndDate }}</t-descriptions-item>
            <t-descriptions-item label="实际开始日期">{{ detailData.actualStartDate || '-' }}</t-descriptions-item>
            <t-descriptions-item label="实际结束日期">{{ detailData.actualEndDate || '-' }}</t-descriptions-item>
            <t-descriptions-item label="状态" :span="2">
              <t-tag v-if="detailData.status === 'not_started'" theme="default">未开始</t-tag>
              <t-tag v-else-if="detailData.status === 'in_progress'" theme="warning">进行中</t-tag>
              <t-tag v-else-if="detailData.status === 'completed'" theme="success">已完成</t-tag>
              <t-tag v-else theme="danger">已延期</t-tag>
            </t-descriptions-item>
            <t-descriptions-item label="完成进度" :span="2">
              <t-progress :percentage="detailData.progress" :show-info="true" />
            </t-descriptions-item>
          </t-descriptions>
        </t-tab-panel>

        <t-tab-panel value="description" label="里程碑描述">
          <div class="milestone-content">{{ detailData.description }}</div>
        </t-tab-panel>

        <t-tab-panel value="tasks" label="关联任务">
          <t-table :data="detailData.tasks || []" :columns="taskColumns" :pagination="false" />
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
  IconFlag, IconCheckCircle, IconTime, IconCalendar,
  IconAdd, IconDownload, IconRefresh
} from 'tdesign-icons-vue-next';

const loading = ref(false);
const dialogVisible = ref(false);
const detailVisible = ref(false);
const dialogTitle = ref('新增里程碑');
const detailActiveTab = ref('basic');
const formRef = ref();

const statistics = reactive({
  total: 28,
  completed: 12,
  inProgress: 8,
  notStarted: 8
});

const searchForm = reactive({
  projectName: '',
  milestoneName: '',
  status: '',
  dateRange: []
});

const formData = reactive({
  id: '',
  projectId: '',
  name: '',
  description: '',
  planStartDate: '',
  planEndDate: '',
  actualStartDate: '',
  actualEndDate: '',
  owner: '',
  priority: 'medium',
  remark: ''
});

const detailData = reactive<any>({});

const pagination = reactive({
  current: 1,
  pageSize: 20,
  total: 28
});

const columns = [
  { colKey: 'projectName', title: '关联项目', width: 150 },
  { colKey: 'name', title: '里程碑名称', width: 200 },
  { colKey: 'owner', title: '负责人', width: 100 },
  { colKey: 'planStartDate', title: '计划开始', width: 120 },
  { colKey: 'planEndDate', title: '计划结束', width: 120 },
  { colKey: 'progress', title: '完成进度', width: 150 },
  { colKey: 'status', title: '状态', width: 100 },
  { colKey: 'operation', title: '操作', width: 200, fixed: 'right' }
];

const taskColumns = [
  { colKey: 'taskCode', title: '任务编号', width: 120 },
  { colKey: 'taskName', title: '任务名称', width: 150 },
  { colKey: 'assignee', title: '负责人', width: 100 },
  { colKey: 'status', title: '状态', width: 100 },
  { colKey: 'completeDate', title: '完成日期', width: 120 }
];

const logColumns = [
  { colKey: 'operator', title: '操作人', width: 100 },
  { colKey: 'action', title: '操作内容', width: 200 },
  { colKey: 'createTime', title: '操作时间', width: 150 }
];

const rules = {
  projectId: [{ required: true, message: '请选择项目', type: 'error' }],
  name: [{ required: true, message: '请输入里程碑名称', type: 'error' }],
  planStartDate: [{ required: true, message: '请选择计划开始日期', type: 'error' }],
  planEndDate: [{ required: true, message: '请选择计划结束日期', type: 'error' }],
  owner: [{ required: true, message: '请输入负责人', type: 'error' }]
};

const ganttData = ref([
  {
    id: 1,
    projectName: 'ERP系统开发',
    milestones: [
      { id: 1, name: '需求分析', status: 'completed', left: 0, width: 20 },
      { id: 2, name: '系统设计', status: 'completed', left: 20, width: 15 },
      { id: 3, name: '开发实现', status: 'in_progress', left: 35, width: 35 },
      { id: 4, name: '测试验收', status: 'not_started', left: 70, width: 15 }
    ]
  },
  {
    id: 2,
    projectName: 'CRM系统开发',
    milestones: [
      { id: 5, name: '需求分析', status: 'completed', left: 0, width: 18 },
      { id: 6, name: '系统设计', status: 'in_progress', left: 18, width: 15 },
      { id: 7, name: '开发实现', status: 'not_started', left: 33, width: 32 },
      { id: 8, name: '测试验收', status: 'not_started', left: 65, width: 15 }
    ]
  }
]);

const tableData = ref([
  {
    id: 1,
    projectId: '1',
    projectName: 'ERP系统开发',
    name: '需求分析完成',
    description: '完成ERP系统需求分析和调研',
    owner: '张三',
    priority: 'high',
    planStartDate: '2026-01-01',
    planEndDate: '2026-01-20',
    actualStartDate: '2026-01-01',
    actualEndDate: '2026-01-18',
    status: 'completed',
    progress: 100,
    remark: '提前完成'
  },
  {
    id: 2,
    projectId: '1',
    projectName: 'ERP系统开发',
    name: '系统设计完成',
    description: '完成ERP系统架构设计和详细设计',
    owner: '李四',
    priority: 'high',
    planStartDate: '2026-01-21',
    planEndDate: '2026-02-10',
    actualStartDate: '2026-01-21',
    actualEndDate: '2026-02-08',
    status: 'completed',
    progress: 100,
    remark: '按期完成'
  },
  {
    id: 3,
    projectId: '1',
    projectName: 'ERP系统开发',
    name: '核心模块开发',
    description: '完成供应链、财务等核心模块开发',
    owner: '王五',
    priority: 'high',
    planStartDate: '2026-02-11',
    planEndDate: '2026-04-30',
    actualStartDate: '2026-02-11',
    actualEndDate: '',
    status: 'in_progress',
    progress: 45,
    remark: ''
  }
]);

const handleSearch = () => {
  MessagePlugin.success('查询成功');
};

const handleReset = () => {
  Object.assign(searchForm, {
    projectName: '',
    milestoneName: '',
    status: '',
    dateRange: []
  });
};

const handleAdd = () => {
  dialogTitle.value = '新增里程碑';
  dialogVisible.value = true;
};

const handleEdit = (row: any) => {
  dialogTitle.value = '编辑里程碑';
  dialogVisible.value = true;
  Object.assign(formData, row);
};

const handleView = (row: any) => {
  detailVisible.value = true;
  detailActiveTab.value = 'basic';
  Object.assign(detailData, row);
  detailData.tasks = [
    { taskCode: 'T001', taskName: '需求调研', assignee: '张三', status: '已完成', completeDate: '2026-01-15' }
  ];
  detailData.logs = [
    { operator: '张三', action: '创建里程碑', createTime: '2026-01-01 09:00:00' },
    { operator: '张三', action: '完成里程碑', createTime: '2026-01-18 17:00:00' }
  ];
};

const handleComplete = (row: any) => {
  MessagePlugin.success('里程碑已标记为完成');
};

const handleDelete = (row: any) => {
  MessagePlugin.success('里程碑已删除');
};

const handleSave = () => {
  MessagePlugin.success(dialogTitle.value === '新增里程碑' ? '里程碑已创建' : '里程碑已更新');
  dialogVisible.value = false;
};

const handleDialogClose = () => {
  Object.assign(formData, {
    id: '',
    projectId: '',
    name: '',
    description: '',
    planStartDate: '',
    planEndDate: '',
    actualStartDate: '',
    actualEndDate: '',
    owner: '',
    priority: 'medium',
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
  console.log('ProjectMilestone mounted');
});
</script>

<style scoped lang="less">
.project-milestone-container {
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

.gantt-header {
  margin-bottom: 16px;
  font-size: 16px;
  font-weight: 500;
}

.gantt-title {
  color: #333;
}

.gantt-container {
  padding: 20px;
  background-color: #f5f5f5;
  border-radius: 4px;
}

.gantt-row {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
}

.gantt-label {
  width: 150px;
  font-size: 14px;
  color: #666;
  flex-shrink: 0;
}

.gantt-timeline {
  flex: 1;
  height: 40px;
  background-color: #e8e8e8;
  border-radius: 4px;
  position: relative;
}

.gantt-bar {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  height: 24px;
  background-color: #0052d9;
  color: white;
  font-size: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 3px;
  padding: 0 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  cursor: pointer;
  transition: all 0.3s;

  &:hover {
    opacity: 0.8;
    z-index: 10;
  }
}

.status-completed {
  background-color: #00a870;
}

.status-in_progress {
  background-color: #ed7b2f;
}

.status-not_started {
  background-color: #c9cdd4;
}

.status-delayed {
  background-color: #d54941;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}

.milestone-content {
  padding: 20px;
  background-color: #f5f5f5;
  border-radius: 4px;
  white-space: pre-wrap;
}
</style>
