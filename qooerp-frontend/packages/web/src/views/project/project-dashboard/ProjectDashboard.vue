<template>
  <div class="project-dashboard-container">
    <!-- 统计卡片 -->
    <t-row :gutter="16" class="mb-4">
      <t-col :span="6">
        <t-card theme="primary" hover>
          <t-statistic title="项目总数" :value="statistics.totalProjects" :loading="loading">
            <template #prefix><icon-apps /></template>
          </t-statistic>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="success" hover>
          <t-statistic title="进行中" :value="statistics.inProgress" :loading="loading">
            <template #prefix><icon-play-circle /></template>
          </t-statistic>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="warning" hover>
          <t-statistic title="即将到期" :value="statistics.expiringSoon" :loading="loading">
            <template #prefix><icon-time /></template>
          </t-statistic>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="danger" hover>
          <t-statistic title="已延期" :value="statistics.delayed" :loading="loading">
            <template #prefix><icon-error-circle /></template>
          </t-statistic>
        </t-card>
      </t-col>
    </t-row>

    <!-- 图表区域 -->
    <t-row :gutter="16" class="mb-4">
      <t-col :span="12">
        <t-card title="项目状态分布">
          <div class="chart-container" ref="statusChartRef"></div>
        </t-card>
      </t-col>
      <t-col :span="12">
        <t-card title="项目优先级分布">
          <div class="chart-container" ref="priorityChartRef"></div>
        </t-card>
      </t-col>
    </t-row>

    <t-row :gutter="16" class="mb-4">
      <t-col :span="24">
        <t-card title="项目进度概览">
          <div class="progress-container">
            <div v-for="item in projectProgressData" :key="item.id" class="progress-item">
              <div class="progress-header">
                <span class="project-name">{{ item.projectName }}</span>
                <span class="progress-percent">{{ item.progress }}%</span>
              </div>
              <t-progress
                :percentage="item.progress"
                :show-info="false"
                :theme="getProgressTheme(item.progress)"
                :label="item.projectName"
              />
              <div class="progress-footer">
                <span>负责人: {{ item.owner }}</span>
                <span>{{ item.status }}</span>
              </div>
            </div>
          </div>
        </t-card>
      </t-col>
    </t-row>

    <!-- 列表区域 -->
    <t-row :gutter="16" class="mb-4">
      <t-col :span="12">
        <t-card title="待办事项" header-bordered>
          <template #actions>
            <t-link theme="primary" @click="handleViewAllTodos">查看全部</t-link>
          </template>
          <div class="todo-list">
            <div v-for="item in todoList" :key="item.id" class="todo-item" :class="`priority-${item.priority}`">
              <div class="todo-header">
                <t-checkbox v-model="item.completed" @change="handleTodoComplete(item)" />
                <span class="todo-title" :class="{ completed: item.completed }">{{ item.title }}</span>
                <t-tag v-if="item.priority === 'high'" theme="danger" size="small">高</t-tag>
              </div>
              <div class="todo-footer">
                <span class="todo-project">{{ item.projectName }}</span>
                <span class="todo-date">{{ item.dueDate }}</span>
              </div>
            </div>
          </div>
        </t-card>
      </t-col>
      <t-col :span="12">
        <t-card title="最近动态" header-bordered>
          <template #actions>
            <t-link theme="primary" @click="handleViewAllActivities">查看全部</t-link>
          </template>
          <div class="activity-list">
            <div v-for="item in activityList" :key="item.id" class="activity-item">
              <div class="activity-icon" :class="`type-${item.type}`">
                <component :is="getActivityIcon(item.type)" :size="20" />
              </div>
              <div class="activity-content">
                <div class="activity-text">{{ item.content }}</div>
                <div class="activity-time">{{ item.time }}</div>
              </div>
            </div>
          </div>
        </t-card>
      </t-col>
    </t-row>

    <!-- 项目概览表格 -->
    <t-card title="项目概览">
      <t-table
        :data="projectTableData"
        :columns="columns"
        :loading="loading"
        :pagination="pagination"
        row-key="id"
        @page-change="handlePageChange"
      >
        <template #status="{ row }">
          <t-tag v-if="row.status === 'not_started'" theme="default" variant="light">未开始</t-tag>
          <t-tag v-else-if="row.status === 'in_progress'" theme="success" variant="light">进行中</t-tag>
          <t-tag v-else-if="row.status === 'completed'" theme="primary" variant="light">已完成</t-tag>
          <t-tag v-else theme="danger" variant="light">已延期</t-tag>
        </template>
        <template #progress="{ row }">
          <t-progress :percentage="row.progress" :show-info="true" size="small" />
        </template>
        <template #operation="{ row }">
          <t-link theme="primary" @click="handleViewProject(row)">查看</t-link>
        </template>
      </t-table>
    </t-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, h } from 'vue';
import { MessagePlugin } from 'tdesign-vue-next';
import {
  IconApps, IconPlayCircle, IconTime, IconErrorCircle,
  IconCheckCircle, IconUser, IconEdit, IconFileCopy, IconChat
} from 'tdesign-icons-vue-next';

const loading = ref(false);
const statusChartRef = ref();
const priorityChartRef = ref();

const statistics = reactive({
  totalProjects: 15,
  inProgress: 8,
  expiringSoon: 3,
  delayed: 2
});

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 15
});

const columns = [
  { colKey: 'projectCode', title: '项目编号', width: 130 },
  { colKey: 'projectName', title: '项目名称', width: 150 },
  { colKey: 'owner', title: '负责人', width: 100 },
  { colKey: 'startDate', title: '开始日期', width: 120 },
  { colKey: 'endDate', title: '结束日期', width: 120 },
  { colKey: 'progress', title: '完成进度', width: 150 },
  { colKey: 'status', title: '状态', width: 100 },
  { colKey: 'operation', title: '操作', width: 100 }
];

const projectProgressData = ref([
  { id: 1, projectName: 'ERP系统开发', owner: '张三', progress: 75, status: '进行中' },
  { id: 2, projectName: 'CRM系统开发', owner: '李四', progress: 45, status: '进行中' },
  { id: 3, projectName: '移动端开发', owner: '王五', progress: 20, status: '进行中' },
  { id: 4, projectName: '数据分析平台', owner: '赵六', progress: 90, status: '即将完成' },
  { id: 5, projectName: 'API网关服务', owner: '钱七', progress: 60, status: '进行中' }
]);

const todoList = ref([
  { id: 1, title: '完成ERP系统需求评审', projectName: 'ERP系统开发', priority: 'high', dueDate: '2026-02-20', completed: false },
  { id: 2, title: '提交CRM系统设计方案', projectName: 'CRM系统开发', priority: 'medium', dueDate: '2026-02-22', completed: false },
  { id: 3, title: '修复移动端登录问题', projectName: '移动端开发', priority: 'high', dueDate: '2026-02-21', completed: false },
  { id: 4, title: '更新项目文档', projectName: 'ERP系统开发', priority: 'low', dueDate: '2026-02-25', completed: true }
]);

const activityList = ref([
  { id: 1, type: 'task', content: '张三完成了"完成ERP系统需求评审"任务', time: '10分钟前' },
  { id: 2, type: 'doc', content: '李四上传了"CRM系统设计文档"', time: '30分钟前' },
  { id: 3, type: 'comment', content: '王五在"移动端开发"项目中发表了评论', time: '1小时前' },
  { id: 4, type: 'edit', content: '赵六更新了"数据分析平台"项目进度', time: '2小时前' }
]);

const projectTableData = ref([
  {
    id: 1,
    projectCode: 'PJ202600001',
    projectName: 'ERP系统开发',
    owner: '张三',
    startDate: '2026-01-01',
    endDate: '2026-06-30',
    progress: 75,
    status: 'in_progress'
  },
  {
    id: 2,
    projectCode: 'PJ202600002',
    projectName: 'CRM系统开发',
    owner: '李四',
    startDate: '2026-02-01',
    endDate: '2026-07-31',
    progress: 45,
    status: 'in_progress'
  },
  {
    id: 3,
    projectCode: 'PJ202600003',
    projectName: '移动端开发',
    owner: '王五',
    startDate: '2026-03-01',
    endDate: '2026-08-31',
    progress: 20,
    status: 'in_progress'
  }
]);

const getProgressTheme = (progress: number) => {
  if (progress >= 80) return 'success';
  if (progress >= 50) return 'primary';
  if (progress >= 30) return 'warning';
  return 'default';
};

const getActivityIcon = (type: string) => {
  switch (type) {
    case 'task':
      return h(IconCheckCircle);
    case 'doc':
      return h(IconFileCopy);
    case 'comment':
      return h(IconChat);
    case 'edit':
      return h(IconEdit);
    default:
      return h(IconUser);
  }
};

const handleTodoComplete = (item: any) => {
  MessagePlugin.success(`任务"${item.title}"已标记为${item.completed ? '已完成' : '未完成'}`);
};

const handleViewAllTodos = () => {
  MessagePlugin.info('跳转到待办事项页面');
};

const handleViewAllActivities = () => {
  MessagePlugin.info('跳转到动态页面');
};

const handleViewProject = (row: any) => {
  MessagePlugin.info(`查看项目: ${row.projectName}`);
};

const handlePageChange = (pageInfo: any) => {
  pagination.current = pageInfo.current;
  pagination.pageSize = pageInfo.pageSize;
};

onMounted(() => {
  console.log('ProjectDashboard mounted');
  // 这里可以初始化图表，例如使用 ECharts
});
</script>

<style scoped lang="less">
.project-dashboard-container {
  padding: 20px;
}

.mb-4 {
  margin-bottom: 16px;
}

.chart-container {
  height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
}

.progress-container {
  .progress-item {
    margin-bottom: 24px;

    &:last-child {
      margin-bottom: 0;
    }

    .progress-header {
      display: flex;
      justify-content: space-between;
      margin-bottom: 8px;
    }

    .project-name {
      font-weight: 500;
      color: #333;
    }

    .progress-percent {
      color: #0052d9;
      font-weight: bold;
    }

    .progress-footer {
      display: flex;
      justify-content: space-between;
      margin-top: 8px;
      font-size: 13px;
      color: #666;
    }
  }
}

.todo-list {
  .todo-item {
    padding: 12px 0;
    border-bottom: 1px solid #f0f0f0;

    &:last-child {
      border-bottom: none;
    }

    &.priority-high {
      border-left: 3px solid #d54941;
    }

    .todo-header {
      display: flex;
      align-items: center;
      gap: 8px;
      margin-bottom: 8px;
    }

    .todo-title {
      flex: 1;
      font-size: 14px;

      &.completed {
        text-decoration: line-through;
        color: #999;
      }
    }

    .todo-footer {
      display: flex;
      justify-content: space-between;
      font-size: 12px;
      color: #999;
      margin-left: 24px;
    }
  }
}

.activity-list {
  .activity-item {
    display: flex;
    gap: 12px;
    padding: 12px 0;
    border-bottom: 1px solid #f0f0f0;

    &:last-child {
      border-bottom: none;
    }

    .activity-icon {
      width: 32px;
      height: 32px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      flex-shrink: 0;

      &.type-task {
        background-color: #e8f3ff;
        color: #0052d9;
      }

      &.type-doc {
        background-color: #e8f8f5;
        color: #00a870;
      }

      &.type-comment {
        background-color: #fff7e8;
        color: #ed7b2f;
      }

      &.type-edit {
        background-color: #f3e8ff;
        color: #722ed1;
      }
    }

    .activity-content {
      flex: 1;

      .activity-text {
        font-size: 14px;
        color: #333;
        margin-bottom: 4px;
      }

      .activity-time {
        font-size: 12px;
        color: #999;
      }
    }
  }
}
</style>
