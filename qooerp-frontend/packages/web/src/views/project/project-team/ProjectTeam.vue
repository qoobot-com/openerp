<template>
  <div class="project-team-container">
    <!-- 统计卡片 -->
    <t-row :gutter="16" class="mb-4">
      <t-col :span="6">
        <t-card theme="primary">
          <t-statistic title="团队成员" :value="statistics.total" :loading="loading">
            <template #prefix><icon-usergroup /></template>
          </t-statistic>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="success">
          <t-statistic title="项目经理" :value="statistics.managers" :loading="loading">
            <template #prefix><icon-user /></template>
          </t-statistic>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="warning">
          <t-statistic title="开发人员" :value="statistics.developers" :loading="loading">
            <template #prefix><icon-code /></template>
          </t-statistic>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="default">
          <t-statistic title="测试人员" :value="statistics.testers" :loading="loading">
            <template #prefix><icon-check-circle /></template>
          </t-statistic>
        </t-card>
      </t-col>
    </t-row>

    <!-- 搜索卡片 -->
    <t-card class="mb-4">
      <t-form :data="searchForm" layout="inline" @submit="handleSearch">
        <t-form-item label="成员姓名" name="memberName">
          <t-input v-model="searchForm.memberName" placeholder="请输入成员姓名" clearable />
        </t-form-item>
        <t-form-item label="项目名称" name="projectName">
          <t-input v-model="searchForm.projectName" placeholder="请输入项目名称" clearable />
        </t-form-item>
        <t-form-item label="角色" name="role">
          <t-select v-model="searchForm.role" placeholder="请选择角色" clearable>
            <t-option value="项目经理" label="项目经理" />
            <t-option value="技术负责人" label="技术负责人" />
            <t-option value="开发人员" label="开发人员" />
            <t-option value="测试人员" label="测试人员" />
            <t-option value="UI设计师" label="UI设计师" />
          </t-select>
        </t-form-item>
        <t-form-item label="状态" name="status">
          <t-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <t-option value="active" label="在职" />
            <t-option value="inactive" label="离职" />
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
          新增成员
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

    <!-- 成员列表 -->
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
          <t-tag v-if="row.status === 'active'" theme="success" variant="light">在职</t-tag>
          <t-tag v-else theme="default" variant="light">离职</t-tag>
        </template>
        <template #operation="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleView(row)">查看</t-link>
            <t-link theme="primary" @click="handleEdit(row)">编辑</t-link>
            <t-link theme="success" @click="handleAssignProject(row)">分配项目</t-link>
            <t-popconfirm content="确定要删除此成员吗？" @confirm="handleDelete(row)">
              <t-link theme="danger">删除</t-link>
            </t-popconfirm>
          </t-space>
        </template>
      </t-table>
    </t-card>

    <!-- 新增/编辑成员弹窗 -->
    <t-dialog
      v-model:visible="dialogVisible"
      :header="dialogTitle"
      width="700px"
      :footer="false"
      @close="handleDialogClose"
    >
      <t-form :data="formData" ref="formRef" :rules="rules" label-width="120px">
        <t-row :gutter="16">
          <t-col :span="12">
            <t-form-item label="成员编号" name="memberCode">
              <t-input v-model="formData.memberCode" placeholder="自动生成" disabled />
            </t-form-item>
          </t-col>
          <t-col :span="12">
            <t-form-item label="姓名" name="name">
              <t-input v-model="formData.name" placeholder="请输入姓名" />
            </t-form-item>
          </t-col>
        </t-row>
        <t-row :gutter="16">
          <t-col :span="12">
            <t-form-item label="角色" name="role">
              <t-select v-model="formData.role" placeholder="请选择角色">
                <t-option value="项目经理" label="项目经理" />
                <t-option value="技术负责人" label="技术负责人" />
                <t-option value="开发人员" label="开发人员" />
                <t-option value="测试人员" label="测试人员" />
                <t-option value="UI设计师" label="UI设计师" />
              </t-select>
            </t-form-item>
          </t-col>
          <t-col :span="12">
            <t-form-item label="职级" name="level">
              <t-select v-model="formData.level" placeholder="请选择职级">
                <t-option value="初级" label="初级" />
                <t-option value="中级" label="中级" />
                <t-option value="高级" label="高级" />
                <t-option value="专家" label="专家" />
              </t-select>
            </t-form-item>
          </t-col>
        </t-row>
        <t-row :gutter="16">
          <t-col :span="12">
            <t-form-item label="手机号码" name="phone">
              <t-input v-model="formData.phone" placeholder="请输入手机号码" />
            </t-form-item>
          </t-col>
          <t-col :span="12">
            <t-form-item label="邮箱" name="email">
              <t-input v-model="formData.email" placeholder="请输入邮箱" />
            </t-form-item>
          </t-col>
        </t-row>
        <t-row :gutter="16">
          <t-col :span="12">
            <t-form-item label="入职日期" name="joinDate">
              <t-date-picker v-model="formData.joinDate" placeholder="请选择入职日期" />
            </t-form-item>
          </t-col>
          <t-col :span="12">
            <t-form-item label="状态" name="status">
              <t-select v-model="formData.status" placeholder="请选择状态">
                <t-option value="active" label="在职" />
                <t-option value="inactive" label="离职" />
              </t-select>
            </t-form-item>
          </t-col>
        </t-row>
        <t-form-item label="技能标签" name="skills">
          <t-select v-model="formData.skills" multiple placeholder="请选择技能标签">
            <t-option value="Vue" label="Vue" />
            <t-option value="React" label="React" />
            <t-option value="TypeScript" label="TypeScript" />
            <t-option value="Node.js" label="Node.js" />
            <t-option value="Java" label="Java" />
            <t-option value="Python" label="Python" />
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

    <!-- 成员详情弹窗 -->
    <t-dialog
      v-model:visible="detailVisible"
      header="成员详情"
      width="800px"
      :footer="false"
    >
      <t-tabs v-model="detailActiveTab" theme="card">
        <t-tab-panel value="basic" label="基本信息">
          <t-descriptions :column="2" bordered>
            <t-descriptions-item label="成员编号">{{ detailData.memberCode }}</t-descriptions-item>
            <t-descriptions-item label="姓名">{{ detailData.name }}</t-descriptions-item>
            <t-descriptions-item label="角色">{{ detailData.role }}</t-descriptions-item>
            <t-descriptions-item label="职级">{{ detailData.level }}</t-descriptions-item>
            <t-descriptions-item label="手机号码">{{ detailData.phone }}</t-descriptions-item>
            <t-descriptions-item label="邮箱">{{ detailData.email }}</t-descriptions-item>
            <t-descriptions-item label="入职日期">{{ detailData.joinDate }}</t-descriptions-item>
            <t-descriptions-item label="状态">
              <t-tag v-if="detailData.status === 'active'" theme="success">在职</t-tag>
              <t-tag v-else>离职</t-tag>
            </t-descriptions-item>
            <t-descriptions-item label="技能标签" :span="2">{{ detailData.skills?.join(', ') }}</t-descriptions-item>
            <t-descriptions-item label="备注" :span="2">{{ detailData.remark }}</t-descriptions-item>
          </t-descriptions>
        </t-tab-panel>

        <t-tab-panel value="projects" label="参与项目">
          <t-table :data="detailData.projects || []" :columns="projectColumns" :pagination="false" />
        </t-tab-panel>

        <t-tab-panel value="performance" label="绩效记录">
          <t-table :data="detailData.performance || []" :columns="performanceColumns" :pagination="false" />
        </t-tab-panel>

        <t-tab-panel value="logs" label="操作日志">
          <t-table :data="detailData.logs || []" :columns="logColumns" :pagination="false" />
        </t-tab-panel>
      </t-tabs>
    </t-dialog>

    <!-- 分配项目弹窗 -->
    <t-dialog
      v-model:visible="assignDialogVisible"
      header="分配项目"
      width="600px"
      :footer="false"
    >
      <t-form :data="assignForm" label-width="100px">
        <t-form-item label="成员姓名">
          <t-input :value="currentMember?.name" disabled />
        </t-form-item>
        <t-form-item label="选择项目" name="projectIds">
          <t-select v-model="assignForm.projectIds" multiple placeholder="请选择项目">
            <t-option value="1" label="ERP系统开发" />
            <t-option value="2" label="CRM系统开发" />
            <t-option value="3" label="移动端开发" />
          </t-select>
        </t-form-item>
      </t-form>
      <div class="dialog-footer">
        <t-button theme="default" @click="assignDialogVisible = false">取消</t-button>
        <t-button theme="primary" @click="handleConfirmAssign">确定</t-button>
      </div>
    </t-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { MessagePlugin } from 'tdesign-vue-next';
import {
  IconUsergroup, IconUser, IconCode, IconCheckCircle,
  IconAdd, IconDownload, IconRefresh
} from 'tdesign-icons-vue-next';

const loading = ref(false);
const dialogVisible = ref(false);
const detailVisible = ref(false);
const assignDialogVisible = ref(false);
const dialogTitle = ref('新增成员');
const detailActiveTab = ref('basic');
const formRef = ref();
const selectedRowKeys = ref([]);
const currentMember = ref<any>(null);

const statistics = reactive({
  total: 32,
  managers: 5,
  developers: 20,
  testers: 7
});

const searchForm = reactive({
  memberName: '',
  projectName: '',
  role: '',
  status: ''
});

const formData = reactive({
  id: '',
  memberCode: '',
  name: '',
  role: '',
  level: '',
  phone: '',
  email: '',
  joinDate: '',
  status: 'active',
  skills: [],
  remark: ''
});

const assignForm = reactive({
  projectIds: []
});

const detailData = reactive<any>({});

const pagination = reactive({
  current: 1,
  pageSize: 20,
  total: 32
});

const columns = [
  { colKey: 'memberCode', title: '成员编号', width: 130 },
  { colKey: 'name', title: '姓名', width: 100 },
  { colKey: 'role', title: '角色', width: 120 },
  { colKey: 'level', title: '职级', width: 80 },
  { colKey: 'phone', title: '手机号码', width: 130 },
  { colKey: 'email', title: '邮箱', width: 180, ellipsis: true },
  { colKey: 'joinDate', title: '入职日期', width: 120 },
  { colKey: 'status', title: '状态', width: 80 },
  { colKey: 'operation', title: '操作', width: 200, fixed: 'right' }
];

const projectColumns = [
  { colKey: 'projectName', title: '项目名称', width: 150 },
  { colKey: 'roleInProject', title: '项目角色', width: 120 },
  { colKey: 'joinDate', title: '加入日期', width: 120 },
  { colKey: 'status', title: '状态', width: 100 }
];

const performanceColumns = [
  { colKey: 'period', title: '考核周期', width: 120 },
  { colKey: 'score', title: '绩效分数', width: 100 },
  { colKey: 'level', title: '绩效等级', width: 100 },
  { colKey: 'comment', title: '评价', width: 200 }
];

const logColumns = [
  { colKey: 'operator', title: '操作人', width: 100 },
  { colKey: 'action', title: '操作内容', width: 200 },
  { colKey: 'createTime', title: '操作时间', width: 150 }
];

const rules = {
  name: [{ required: true, message: '请输入姓名', type: 'error' }],
  role: [{ required: true, message: '请选择角色', type: 'error' }],
  phone: [{ required: true, message: '请输入手机号码', type: 'error' }],
  email: [{ required: true, message: '请输入邮箱', type: 'error' }],
  joinDate: [{ required: true, message: '请选择入职日期', type: 'error' }]
};

const tableData = ref([
  {
    id: 1,
    memberCode: 'PM202600001',
    name: '张三',
    role: '项目经理',
    level: '高级',
    phone: '13800138000',
    email: 'zhangsan@example.com',
    joinDate: '2025-01-15',
    status: 'active',
    skills: ['Vue', 'TypeScript', '项目管理'],
    remark: '负责ERP系统项目',
    createTime: '2025-01-15 10:00:00'
  },
  {
    id: 2,
    memberCode: 'DV202600001',
    name: '李四',
    role: '开发人员',
    level: '中级',
    phone: '13900139000',
    email: 'lisi@example.com',
    joinDate: '2025-03-01',
    status: 'active',
    skills: ['Vue', 'React', 'JavaScript'],
    remark: '前端开发工程师',
    createTime: '2025-03-01 14:00:00'
  }
]);

const handleSearch = () => {
  MessagePlugin.success('查询成功');
};

const handleReset = () => {
  Object.assign(searchForm, {
    memberName: '',
    projectName: '',
    role: '',
    status: ''
  });
};

const handleAdd = () => {
  dialogTitle.value = '新增成员';
  dialogVisible.value = true;
};

const handleEdit = (row: any) => {
  dialogTitle.value = '编辑成员';
  dialogVisible.value = true;
  Object.assign(formData, row);
};

const handleView = (row: any) => {
  detailVisible.value = true;
  detailActiveTab.value = 'basic';
  Object.assign(detailData, row);
  detailData.projects = [
    { projectName: 'ERP系统开发', roleInProject: '项目经理', joinDate: '2025-01-15', status: '进行中' }
  ];
  detailData.performance = [
    { period: '2025-Q4', score: 92, level: 'A', comment: '表现优秀' }
  ];
  detailData.logs = [
    { operator: '管理员', action: '创建成员', createTime: '2025-01-15 10:00:00' }
  ];
};

const handleAssignProject = (row: any) => {
  currentMember.value = row;
  assignForm.projectIds = [];
  assignDialogVisible.value = true;
};

const handleConfirmAssign = () => {
  MessagePlugin.success('项目分配成功');
  assignDialogVisible.value = false;
};

const handleBatchAssign = () => {
  if (selectedRowKeys.value.length === 0) {
    MessagePlugin.warning('请选择要分配的成员');
    return;
  }
  MessagePlugin.success('批量分配功能开发中');
};

const handleDelete = (row: any) => {
  MessagePlugin.success('成员已删除');
};

const handleSave = () => {
  MessagePlugin.success(dialogTitle.value === '新增成员' ? '成员已创建' : '成员已更新');
  dialogVisible.value = false;
};

const handleDialogClose = () => {
  Object.assign(formData, {
    id: '',
    memberCode: '',
    name: '',
    role: '',
    level: '',
    phone: '',
    email: '',
    joinDate: '',
    status: 'active',
    skills: [],
    remark: ''
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
  console.log('ProjectTeam mounted');
});
</script>

<style scoped lang="less">
.project-team-container {
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
