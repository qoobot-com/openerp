<template>
  <div class="performance-container">
    <!-- 统计卡片 -->
    <t-row :gutter="16" class="statistics-cards">
      <t-col :span="6">
        <t-card>
          <t-statistic title="本月考核人数" :value="stats.totalCount" />
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card>
          <t-statistic title="平均绩效得分" :value="stats.avgScore" :precision="2" />
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card>
          <t-statistic title="优秀人数" :value="stats.excellentCount" />
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card>
          <t-statistic title="需改进人数" :value="stats.improveCount" />
        </t-card>
      </t-col>
    </t-row>

    <!-- 搜索卡片 -->
    <t-card class="search-card" title="查询条件">
      <t-form :data="searchForm" layout="inline" @submit="handleSearch" @reset="handleReset">
        <t-form-item label="员工姓名" name="name">
          <t-input v-model="searchForm.name" placeholder="请输入员工姓名" clearable />
        </t-form-item>
        <t-form-item label="工号" name="employeeNo">
          <t-input v-model="searchForm.employeeNo" placeholder="请输入工号" clearable />
        </t-form-item>
        <t-form-item label="部门" name="deptId">
          <t-select v-model="searchForm.deptId" placeholder="请选择部门" clearable>
            <t-option v-for="dept in deptList" :key="dept.id" :value="dept.id" :label="dept.name" />
          </t-select>
        </t-form-item>
        <t-form-item label="考核周期" name="period">
          <t-date-range-picker v-model="searchForm.period" format="YYYY-MM" placeholder="请选择考核周期" />
        </t-form-item>
        <t-form-item label="考核状态" name="status">
          <t-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <t-option value="pending" label="待评分" />
            <t-option value="completed" label="已完成" />
            <t-option value="reviewed" label="已复核" />
          </t-select>
        </t-form-item>
        <t-form-item>
          <t-space>
            <t-button theme="primary" type="submit">查询</t-button>
            <t-button theme="default" type="reset">重置</t-button>
          </t-space>
        </t-form-item>
      </t-form>
    </t-card>

    <!-- 操作栏 -->
    <t-card class="operation-card">
      <t-space>
        <t-button theme="primary" @click="handleAdd">
          <template #icon><add-icon /></template>
          新增考核
        </t-button>
        <t-button theme="default" @click="handleBatchDelete">
          <template #icon><delete-icon /></template>
          批量删除
        </t-button>
        <t-button theme="default" @click="handleExport">
          <template #icon><download-icon /></template>
          导出报表
        </t-button>
      </t-space>
    </t-card>

    <!-- 绩效考核列表 -->
    <t-card class="table-card">
      <t-table
        :data="tableData"
        :columns="columns"
        :row-key="'id'"
        :selected-row-keys="selectedRowKeys"
        :loading="loading"
        :pagination="pagination"
        @select-change="handleSelectChange"
        @page-change="handlePageChange"
      >
        <template #score="{ row }">
          <t-tag v-if="row.score >= 90" theme="success">{{ row.score }}</t-tag>
          <t-tag v-else-if="row.score >= 80" theme="primary">{{ row.score }}</t-tag>
          <t-tag v-else-if="row.score >= 60" theme="warning">{{ row.score }}</t-tag>
          <t-tag v-else theme="danger">{{ row.score }}</t-tag>
        </template>
        <template #level="{ row }">
          <t-tag v-if="row.level === 'S'" theme="success">S级 - 卓越</t-tag>
          <t-tag v-else-if="row.level === 'A'" theme="primary">A级 - 优秀</t-tag>
          <t-tag v-else-if="row.level === 'B'" theme="warning">B级 - 良好</t-tag>
          <t-tag v-else-if="row.level === 'C'" theme="default">C级 - 合格</t-tag>
          <t-tag v-else theme="danger">D级 - 需改进</t-tag>
        </template>
        <template #status="{ row }">
          <t-tag v-if="row.status === 'pending'" theme="warning">待评分</t-tag>
          <t-tag v-else-if="row.status === 'completed'" theme="success">已完成</t-tag>
          <t-tag v-else theme="primary">已复核</t-tag>
        </template>
        <template #operation="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleView(row)">查看</t-link>
            <t-link v-if="row.status === 'pending'" theme="primary" @click="handleEdit(row)">评分</t-link>
            <t-link theme="primary" @click="handleReview(row)">复核</t-link>
            <t-link theme="danger" @click="handleDelete(row)">删除</t-link>
          </t-space>
        </template>
      </t-table>
    </t-card>

    <!-- 新增/编辑弹窗 -->
    <t-dialog
      v-model:visible="dialogVisible"
      :header="dialogTitle"
      :confirm-btn="null"
      :cancel-btn="null"
      width="900px"
    >
      <t-form
        ref="formRef"
        :data="formData"
        :rules="rules"
        label-width="120px"
        @submit="handleSubmit"
      >
        <t-tabs v-model="activeTab">
          <t-tab-panel value="basic" label="基本信息">
            <t-form-item label="被考核人" name="employeeId">
              <t-select v-model="formData.employeeId" placeholder="请选择员工" filterable clearable>
                <t-option v-for="emp in employeeList" :key="emp.id" :value="emp.id" :label="`${emp.name} (${emp.employeeNo})`" />
              </t-select>
            </t-form-item>
            <t-form-item label="考核类型" name="type">
              <t-select v-model="formData.type" placeholder="请选择考核类型" clearable>
                <t-option value="monthly" label="月度考核" />
                <t-option value="quarterly" label="季度考核" />
                <t-option value="yearly" label="年度考核" />
                <t-option value="project" label="项目考核" />
              </t-select>
            </t-form-item>
            <t-form-item label="考核周期" name="period">
              <t-date-range-picker v-model="formData.period" format="YYYY-MM" placeholder="请选择考核周期" />
            </t-form-item>
            <t-form-item label="评估人" name="evaluatorId">
              <t-select v-model="formData.evaluatorId" placeholder="请选择评估人" filterable clearable>
                <t-option v-for="emp in employeeList" :key="emp.id" :value="emp.id" :label="`${emp.name} (${emp.employeeNo})`" />
              </t-select>
            </t-form-item>
          </t-tab-panel>
          <t-tab-panel value="indicators" label="考核指标">
            <t-table :data="formData.indicators" :columns="indicatorColumns" row-key="id">
              <template #weight="{ row }">
                <t-input-number v-model="row.weight" :min="0" :max="100" size="small" />
              </template>
              <template #score="{ row }">
                <t-input-number v-model="row.score" :min="0" :max="row.maxScore" size="small" />
              </template>
              <template #remark="{ row }">
                <t-input v-model="row.remark" placeholder="请输入备注" size="small" />
              </template>
            </t-table>
            <t-form-item label="总权重" style="margin-top: 16px;">
              <t-input-number v-model="totalWeight" readonly disabled />
            </t-form-item>
          </t-tab-panel>
          <t-tab-panel value="result" label="考核结果">
            <t-form-item label="总分" name="totalScore">
              <t-input-number v-model="formData.totalScore" readonly disabled :precision="2" />
            </t-form-item>
            <t-form-item label="绩效等级" name="level">
              <t-radio-group v-model="formData.level">
                <t-radio value="S">S级 - 卓越 (90-100)</t-radio>
                <t-radio value="A">A级 - 优秀 (80-89)</t-radio>
                <t-radio value="B">B级 - 良好 (70-79)</t-radio>
                <t-radio value="C">C级 - 合格 (60-69)</t-radio>
                <t-radio value="D">D级 - 需改进 (0-59)</t-radio>
              </t-radio-group>
            </t-form-item>
            <t-form-item label="综合评价" name="comment">
              <t-textarea v-model="formData.comment" placeholder="请输入综合评价" :maxlength="500" />
            </t-form-item>
            <t-form-item label="改进建议" name="suggestion">
              <t-textarea v-model="formData.suggestion" placeholder="请输入改进建议" :maxlength="500" />
            </t-form-item>
          </t-tab-panel>
        </t-tabs>
        <t-form-item style="margin-top: 20px;">
          <t-space>
            <t-button theme="primary" type="submit">确定</t-button>
            <t-button theme="default" @click="dialogVisible = false">取消</t-button>
          </t-space>
        </t-form-item>
      </t-form>
    </t-dialog>

    <!-- 详情弹窗 -->
    <t-dialog v-model:visible="detailVisible" header="考核详情" width="900px" :footer="false">
      <t-descriptions bordered :column="2">
        <t-descriptions-item label="被考核人">{{ detailData.employeeName }}</t-descriptions-item>
        <t-descriptions-item label="工号">{{ detailData.employeeNo }}</t-descriptions-item>
        <t-descriptions-item label="部门">{{ detailData.deptName }}</t-descriptions-item>
        <t-descriptions-item label="职位">{{ detailData.positionName }}</t-descriptions-item>
        <t-descriptions-item label="考核类型">{{ detailData.typeName }}</t-descriptions-item>
        <t-descriptions-item label="考核周期">{{ detailData.period }}</t-descriptions-item>
        <t-descriptions-item label="评估人">{{ detailData.evaluatorName }}</t-descriptions-item>
        <t-descriptions-item label="评估日期">{{ detailData.evalDate }}</t-descriptions-item>
        <t-descriptions-item label="总分">
          <t-tag :theme="detailData.score >= 90 ? 'success' : detailData.score >= 60 ? 'primary' : 'danger'">{{ detailData.totalScore }}</t-tag>
        </t-descriptions-item>
        <t-descriptions-item label="绩效等级">
          <t-tag v-if="detailData.level === 'S'" theme="success">S级 - 卓越</t-tag>
          <t-tag v-else-if="detailData.level === 'A'" theme="primary">A级 - 优秀</t-tag>
          <t-tag v-else-if="detailData.level === 'B'" theme="warning">B级 - 良好</t-tag>
          <t-tag v-else-if="detailData.level === 'C'" theme="default">C级 - 合格</t-tag>
          <t-tag v-else theme="danger">D级 - 需改进</t-tag>
        </t-descriptions-item>
      </t-descriptions>
      <t-divider>考核指标明细</t-divider>
      <t-table :data="detailData.indicators" :columns="indicatorDetailColumns" row-key="id" />
      <t-divider>评价信息</t-divider>
      <t-descriptions bordered :column="1">
        <t-descriptions-item label="综合评价">{{ detailData.comment }}</t-descriptions-item>
        <t-descriptions-item label="改进建议">{{ detailData.suggestion }}</t-descriptions-item>
      </t-descriptions>
    </t-dialog>

    <!-- 复核弹窗 -->
    <t-dialog v-model:visible="reviewVisible" header="绩效复核" width="600px">
      <t-form ref="reviewFormRef" :data="reviewData" :rules="reviewRules" label-width="120px">
        <t-form-item label="复核结果" name="reviewResult">
          <t-radio-group v-model="reviewData.reviewResult">
            <t-radio value="pass">通过</t-radio>
            <t-radio value="reject">不通过</t-radio>
            <t-radio value="adjust">调整</t-radio>
          </t-radio-group>
        </t-form-item>
        <t-form-item label="调整分数" name="adjustScore">
          <t-input-number v-model="reviewData.adjustScore" :min="0" :max="100" :precision="2" />
        </t-form-item>
        <t-form-item label="复核意见" name="reviewComment">
          <t-textarea v-model="reviewData.reviewComment" placeholder="请输入复核意见" :maxlength="500" />
        </t-form-item>
      </t-form>
      <template #footer>
        <t-space>
          <t-button theme="primary" @click="handleReviewSubmit">确定</t-button>
          <t-button theme="default" @click="reviewVisible = false">取消</t-button>
        </t-space>
      </template>
    </t-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue';
import { MessagePlugin, DialogPlugin } from 'tdesign-vue-next';
import { AddIcon, DeleteIcon, DownloadIcon } from 'tdesign-icons-vue-next';

// 统计数据
const stats = ref({
  totalCount: 86,
  avgScore: 82.5,
  excellentCount: 23,
  improveCount: 5
});

// 搜索表单
const searchForm = reactive({
  name: '',
  employeeNo: '',
  deptId: undefined,
  period: [],
  status: undefined
});

// 部门列表
const deptList = ref([
  { id: 1, name: '研发部' },
  { id: 2, name: '市场部' },
  { id: 3, name: '销售部' },
  { id: 4, name: '财务部' },
  { id: 5, name: '人事部' }
]);

// 表格数据
const loading = ref(false);
const tableData = ref([
  {
    id: 1,
    employeeId: 1,
    employeeNo: 'EMP001',
    employeeName: '张三',
    deptName: '研发部',
    positionName: '高级开发工程师',
    type: 'monthly',
    typeName: '月度考核',
    period: '2026-02',
    evaluatorId: 2,
    evaluatorName: '李四',
    evalDate: '2026-02-28',
    totalScore: 92,
    score: 92,
    level: 'S',
    status: 'completed',
    comment: '工作积极主动，技术能力强，能够独立完成复杂任务。',
    suggestion: '继续保持，可考虑提升为技术负责人。',
    indicators: [
      { id: 1, name: '工作任务完成', weight: 40, maxScore: 40, score: 38, remark: '按时完成所有任务' },
      { id: 2, name: '工作质量', weight: 30, maxScore: 30, score: 28, remark: '代码质量高' },
      { id: 3, name: '团队协作', weight: 20, maxScore: 20, score: 18, remark: '团队配合良好' },
      { id: 4, name: '学习成长', weight: 10, maxScore: 10, score: 8, remark: '主动学习新技术' }
    ]
  },
  {
    id: 2,
    employeeId: 3,
    employeeNo: 'EMP003',
    employeeName: '王五',
    deptName: '市场部',
    positionName: '市场专员',
    type: 'monthly',
    typeName: '月度考核',
    period: '2026-02',
    evaluatorId: 4,
    evaluatorName: '赵六',
    evalDate: '2026-02-28',
    totalScore: 75,
    score: 75,
    level: 'B',
    status: 'completed',
    comment: '能够完成本职工作，但在市场分析方面还需加强。',
    suggestion: '建议参加市场分析相关培训。',
    indicators: [
      { id: 1, name: '工作任务完成', weight: 40, maxScore: 40, score: 32, remark: '大部分任务完成' },
      { id: 2, name: '工作质量', weight: 30, maxScore: 30, score: 25, remark: '质量有待提升' },
      { id: 3, name: '团队协作', weight: 20, maxScore: 20, score: 16, remark: '协作一般' },
      { id: 4, name: '学习成长', weight: 10, maxScore: 10, score: 2, remark: '学习积极性不足' }
    ]
  }
]);

const selectedRowKeys = ref<number[]>([]);

// 表格列
const columns = [
  { colKey: 'employeeNo', title: '工号', width: 120 },
  { colKey: 'employeeName', title: '姓名', width: 100 },
  { colKey: 'deptName', title: '部门', width: 120 },
  { colKey: 'type', title: '考核类型', width: 120 },
  { colKey: 'period', title: '考核周期', width: 120 },
  { colKey: 'score', title: '得分', width: 100 },
  { colKey: 'level', title: '等级', width: 140 },
  { colKey: 'evaluatorName', title: '评估人', width: 100 },
  { colKey: 'evalDate', title: '评估日期', width: 120 },
  { colKey: 'status', title: '状态', width: 100 },
  { colKey: 'operation', title: '操作', width: 240, fixed: 'right' }
];

// 分页
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 86
});

// 弹窗
const dialogVisible = ref(false);
const dialogTitle = ref('');
const detailVisible = ref(false);
const reviewVisible = ref(false);
const activeTab = ref('basic');

// 表单
const formRef = ref();
const formData = reactive({
  id: undefined,
  employeeId: undefined,
  type: 'monthly',
  period: [],
  evaluatorId: undefined,
  totalScore: 0,
  level: 'B',
  comment: '',
  suggestion: '',
  indicators: [
    { id: 1, name: '工作任务完成', weight: 40, maxScore: 40, score: 0, remark: '' },
    { id: 2, name: '工作质量', weight: 30, maxScore: 30, score: 0, remark: '' },
    { id: 3, name: '团队协作', weight: 20, maxScore: 20, score: 0, remark: '' },
    { id: 4, name: '学习成长', weight: 10, maxScore: 10, score: 0, remark: '' }
  ]
});

const employeeList = ref([
  { id: 1, name: '张三', employeeNo: 'EMP001' },
  { id: 2, name: '李四', employeeNo: 'EMP002' },
  { id: 3, name: '王五', employeeNo: 'EMP003' },
  { id: 4, name: '赵六', employeeNo: 'EMP004' }
]);

// 指标表格列
const indicatorColumns = [
  { colKey: 'name', title: '指标名称', width: 200 },
  { colKey: 'weight', title: '权重(%)', width: 120 },
  { colKey: 'maxScore', title: '满分', width: 100 },
  { colKey: 'score', title: '得分', width: 120 },
  { colKey: 'remark', title: '备注' }
];

const indicatorDetailColumns = [
  { colKey: 'name', title: '指标名称', width: 200 },
  { colKey: 'weight', title: '权重(%)', width: 100 },
  { colKey: 'maxScore', title: '满分', width: 100 },
  { colKey: 'score', title: '得分', width: 100 },
  { colKey: 'remark', title: '备注' }
];

// 总权重
const totalWeight = computed(() => {
  return formData.indicators.reduce((sum: number, item: any) => sum + (item.weight || 0), 0);
});

// 详情数据
const detailData = ref({});

// 复核表单
const reviewFormRef = ref();
const reviewData = reactive({
  id: undefined,
  reviewResult: 'pass',
  adjustScore: undefined,
  reviewComment: ''
});

const reviewRules = {
  reviewResult: [{ required: true, message: '请选择复核结果', type: 'error' }],
  reviewComment: [{ required: true, message: '请输入复核意见', type: 'error' }]
};

// 表单验证规则
const rules = {
  employeeId: [{ required: true, message: '请选择被考核人', type: 'error' }],
  type: [{ required: true, message: '请选择考核类型', type: 'error' }],
  period: [{ required: true, message: '请选择考核周期', type: 'error' }],
  evaluatorId: [{ required: true, message: '请选择评估人', type: 'error' }]
};

// 搜索
const handleSearch = () => {
  MessagePlugin.success('查询成功');
  loadData();
};

const handleReset = () => {
  Object.assign(searchForm, {
    name: '',
    employeeNo: '',
    deptId: undefined,
    period: [],
    status: undefined
  });
  loadData();
};

// 表格选择
const handleSelectChange = (value: number[]) => {
  selectedRowKeys.value = value;
};

// 分页
const handlePageChange = (pageInfo: any) => {
  pagination.current = pageInfo.current;
  pagination.pageSize = pageInfo.pageSize;
  loadData();
};

// 新增
const handleAdd = () => {
  dialogTitle.value = '新增考核';
  activeTab.value = 'basic';
  Object.assign(formData, {
    id: undefined,
    employeeId: undefined,
    type: 'monthly',
    period: [],
    evaluatorId: undefined,
    totalScore: 0,
    level: 'B',
    comment: '',
    suggestion: '',
    indicators: [
      { id: 1, name: '工作任务完成', weight: 40, maxScore: 40, score: 0, remark: '' },
      { id: 2, name: '工作质量', weight: 30, maxScore: 30, score: 0, remark: '' },
      { id: 3, name: '团队协作', weight: 20, maxScore: 20, score: 0, remark: '' },
      { id: 4, name: '学习成长', weight: 10, maxScore: 10, score: 0, remark: '' }
    ]
  });
  dialogVisible.value = true;
};

// 编辑
const handleEdit = (row: any) => {
  dialogTitle.value = '绩效评分';
  activeTab.value = 'indicators';
  Object.assign(formData, {
    ...row,
    period: [row.period, row.period]
  });
  dialogVisible.value = true;
};

// 查看
const handleView = (row: any) => {
  detailData.value = { ...row };
  detailVisible.value = true;
};

// 复核
const handleReview = (row: any) => {
  Object.assign(reviewData, {
    id: row.id,
    reviewResult: 'pass',
    adjustScore: undefined,
    reviewComment: ''
  });
  reviewVisible.value = true;
};

const handleReviewSubmit = async ({ validateResult }: any) => {
  if (validateResult === true) {
    MessagePlugin.success('复核成功');
    reviewVisible.value = false;
    loadData();
  }
};

// 删除
const handleDelete = async (row: any) => {
  const dialog = await DialogPlugin.confirm({
    header: '确认删除',
    body: `确定要删除员工 ${row.employeeName} 的考核记录吗？`,
    confirmBtn: '确定',
    cancelBtn: '取消'
  });
  if (dialog) {
    MessagePlugin.success('删除成功');
    loadData();
  }
};

// 批量删除
const handleBatchDelete = async () => {
  if (selectedRowKeys.value.length === 0) {
    MessagePlugin.warning('请先选择要删除的记录');
    return;
  }
  const dialog = await DialogPlugin.confirm({
    header: '确认删除',
    body: `确定要删除选中的 ${selectedRowKeys.value.length} 条记录吗？`,
    confirmBtn: '确定',
    cancelBtn: '取消'
  });
  if (dialog) {
    MessagePlugin.success('批量删除成功');
    selectedRowKeys.value = [];
    loadData();
  }
};

// 导出
const handleExport = () => {
  MessagePlugin.success('导出成功');
};

// 提交表单
const handleSubmit = async ({ validateResult }: any) => {
  if (validateResult === true) {
    MessagePlugin.success(formData.id ? '更新成功' : '创建成功');
    dialogVisible.value = false;
    loadData();
  }
};

// 加载数据
const loadData = () => {
  loading.value = true;
  setTimeout(() => {
    loading.value = false;
  }, 500);
};

onMounted(() => {
  loadData();
});
</script>

<style scoped lang="less">
.performance-container {
  .statistics-cards {
    margin-bottom: 16px;
  }

  .search-card {
    margin-bottom: 16px;
  }

  .operation-card {
    margin-bottom: 16px;
  }

  .table-card {
    margin-bottom: 16px;
  }
}
</style>
