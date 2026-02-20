<template>
  <div class="training-page">
    <!-- 统计卡片 -->
    <t-row :gutter="16" class="stats-row">
      <t-col :span="6">
        <t-card theme="primary1" header-bordered>
          <div class="stat-item">
            <div class="stat-icon">
              <t-icon name="usergroup" size="24px" />
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ statistics.totalCourses }}</div>
              <div class="stat-label">培训课程</div>
            </div>
          </div>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="success1" header-bordered>
          <div class="stat-item">
            <div class="stat-icon">
              <t-icon name="check-circle" size="24px" />
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ statistics.completedTrainees }}</div>
              <div class="stat-label">已完成培训</div>
            </div>
          </div>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="warning1" header-bordered>
          <div class="stat-item">
            <div class="stat-icon">
              <t-icon name="time" size="24px" />
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ statistics.inProgressCourses }}</div>
              <div class="stat-label">进行中</div>
            </div>
          </div>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="danger1" header-bordered>
          <div class="stat-item">
            <div class="stat-icon">
              <t-icon name="calendar" size="24px" />
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ statistics.upcomingCourses }}</div>
              <div class="stat-label">即将开始</div>
            </div>
          </div>
        </t-card>
      </t-col>
    </t-row>

    <!-- 搜索卡片 -->
    <t-card title="搜索筛选" class="search-card">
      <t-form :data="searchForm" layout="inline" @submit="handleSearch" @reset="handleReset">
        <t-form-item label="课程名称" name="courseName">
          <t-input v-model="searchForm.courseName" placeholder="请输入课程名称" clearable />
        </t-form-item>
        <t-form-item label="培训类型" name="type">
          <t-select v-model="searchForm.type" placeholder="请选择培训类型" clearable>
            <t-option value="online" label="线上培训" />
            <t-option value="offline" label="线下培训" />
            <t-option value="mixed" label="混合培训" />
          </t-select>
        </t-form-item>
        <t-form-item label="培训状态" name="status">
          <t-select v-model="searchForm.status" placeholder="请选择培训状态" clearable>
            <t-option value="draft" label="草稿" />
            <t-option value="published" label="已发布" />
            <t-option value="inProgress" label="进行中" />
            <t-option value="completed" label="已完成" />
            <t-option value="cancelled" label="已取消" />
          </t-select>
        </t-form-item>
        <t-form-item label="培训日期" name="trainingDate">
          <t-date-range-picker v-model="searchForm.trainingDate" placeholder="请选择日期" clearable />
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
    <t-card class="action-card">
      <t-space>
        <t-button theme="primary" @click="handleAdd">
          <template #icon><t-icon name="add" /></template>
          新增课程
        </t-button>
        <t-button theme="success" @click="handlePublishBatch">
          <template #icon><t-icon name="check" /></template>
          批量发布
        </t-button>
        <t-button theme="default" @click="handleExport">
          <template #icon><t-icon name="download" /></template>
          导出报表
        </t-button>
      </t-space>
    </t-card>

    <!-- 培训课程列表 -->
    <t-card>
      <t-table
        :data="courseList"
        :columns="columns"
        :row-key="rowKey"
        :loading="loading"
        :pagination="pagination"
        @page-change="handlePageChange"
        :selected-row-keys="selectedRowKeys"
        @select-change="handleSelectChange"
        stripe
        hover
      >
        <template #type="{ row }">
          <t-tag v-if="row.type === 'online'" theme="primary">线上培训</t-tag>
          <t-tag v-else-if="row.type === 'offline'" theme="success">线下培训</t-tag>
          <t-tag v-else theme="warning">混合培训</t-tag>
        </template>
        <template #status="{ row }">
          <t-tag v-if="row.status === 'draft'" theme="default">草稿</t-tag>
          <t-tag v-else-if="row.status === 'published'" theme="primary">已发布</t-tag>
          <t-tag v-else-if="row.status === 'inProgress'" theme="warning">进行中</t-tag>
          <t-tag v-else-if="row.status === 'completed'" theme="success">已完成</t-tag>
          <t-tag v-else-if="row.status === 'cancelled'" theme="danger">已取消</t-tag>
        </template>
        <template #progress="{ row }">
          <t-progress :percentage="row.progress" :show-info="false" theme="success" />
        </template>
        <template #action="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleView(row)">查看</t-link>
            <t-link theme="primary" @click="handleEdit(row)">编辑</t-link>
            <t-link v-if="row.status === 'draft'" theme="success" @click="handlePublish(row)">发布</t-link>
            <t-link v-if="row.status === 'published'" theme="success" @click="handleStart(row)">开始</t-link>
            <t-link theme="danger" @click="handleDelete(row)">删除</t-link>
          </t-space>
        </template>
      </t-table>
    </t-card>

    <!-- 课程编辑弹窗 -->
    <t-dialog
      v-model:visible="editDialogVisible"
      :header="editDialogTitle"
      width="900px"
      :footer="false"
      @close="handleEditClose"
    >
      <t-form ref="editFormRef" :data="editForm" :rules="editRules" label-width="100px" @submit="handleEditSubmit">
        <t-tabs v-model="activeTab">
          <t-tab-panel value="basic" label="基本信息">
            <t-form-item label="课程名称" name="courseName">
              <t-input v-model="editForm.courseName" placeholder="请输入课程名称" />
            </t-form-item>
            <t-form-item label="课程编号" name="courseCode">
              <t-input v-model="editForm.courseCode" placeholder="请输入课程编号" disabled />
            </t-form-item>
            <t-form-item label="培训类型" name="type">
              <t-radio-group v-model="editForm.type">
                <t-radio value="online">线上培训</t-radio>
                <t-radio value="offline">线下培训</t-radio>
                <t-radio value="mixed">混合培训</t-radio>
              </t-radio-group>
            </t-form-item>
            <t-form-item label="培训类别" name="category">
              <t-select v-model="editForm.category" placeholder="请选择培训类别">
                <t-option value="new" label="新员工培训" />
                <t-option value="skill" label="技能提升" />
                <t-option value="leadership" label="管理培训" />
                <t-option value="compliance" label="合规培训" />
                <t-option value="other" label="其他培训" />
              </t-select>
            </t-form-item>
            <t-form-item label="培训讲师" name="trainer">
              <t-select v-model="editForm.trainer" placeholder="请选择讲师">
                <t-option v-for="trainer in trainers" :key="trainer.value" :value="trainer.value" :label="trainer.label" />
              </t-select>
            </t-form-item>
            <t-form-item label="培训时长" name="duration">
              <t-space>
                <t-input-number v-model="editForm.duration" :min="1" :max="100" placeholder="时长" />
                <t-select v-model="editForm.durationUnit" placeholder="单位" style="width: 100px">
                  <t-option value="hour" label="小时" />
                  <t-option value="day" label="天" />
                </t-select>
              </t-space>
            </t-form-item>
            <t-form-item label="培训人数" name="maxTrainees">
              <t-input-number v-model="editForm.maxTrainees" :min="1" :max="500" placeholder="请输入培训人数" />
            </t-form-item>
            <t-form-item label="培训开始时间" name="startTime">
              <t-date-time-picker v-model="editForm.startTime" placeholder="请选择培训开始时间" />
            </t-form-item>
            <t-form-item label="培训结束时间" name="endTime">
              <t-date-time-picker v-model="editForm.endTime" placeholder="请选择培训结束时间" />
            </t-form-item>
            <t-form-item label="培训地点" name="location">
              <t-input v-model="editForm.location" placeholder="请输入培训地点" />
            </t-form-item>
            <t-form-item label="培训状态" name="status">
              <t-radio-group v-model="editForm.status">
                <t-radio value="draft">草稿</t-radio>
                <t-radio value="published">已发布</t-radio>
                <t-radio value="inProgress">进行中</t-radio>
                <t-radio value="completed">已完成</t-radio>
                <t-radio value="cancelled">已取消</t-radio>
              </t-radio-group>
            </t-form-item>
          </t-tab-panel>

          <t-tab-panel value="content" label="课程内容">
            <t-form-item label="课程简介" name="introduction">
              <t-textarea v-model="editForm.introduction" placeholder="请输入课程简介" :autosize="{ minRows: 4, maxRows: 8 }" />
            </t-form-item>
            <t-form-item label="培训目标" name="objectives">
              <t-textarea v-model="editForm.objectives" placeholder="请输入培训目标" :autosize="{ minRows: 4, maxRows: 8 }" />
            </t-form-item>
            <t-form-item label="课程大纲" name="outline">
              <t-textarea v-model="editForm.outline" placeholder="请输入课程大纲" :autosize="{ minRows: 6, maxRows: 12 }" />
            </t-form-item>
            <t-form-item label="培训要求" name="requirements">
              <t-textarea v-model="editForm.requirements" placeholder="请输入培训要求" :autosize="{ minRows: 3, maxRows: 6 }" />
            </t-form-item>
            <t-form-item label="课程资料" name="materials">
              <t-upload
                v-model="editForm.materials"
                theme="file-input"
                placeholder="点击上传课程资料"
                multiple
              />
            </t-form-item>
          </t-tab-panel>

          <t-tab-panel value="trainees" label="培训对象">
            <t-form-item label="参训部门" name="departments">
              <t-select v-model="editForm.departments" placeholder="请选择参训部门" multiple>
                <t-option v-for="dept in departments" :key="dept.value" :value="dept.value" :label="dept.label" />
              </t-select>
            </t-form-item>
            <t-form-item label="参训人员" name="trainees">
              <t-transfer
                v-model="editForm.trainees"
                :data="allEmployees"
                :titles="['可选员工', '已选员工']"
                :target-order="push"
              />
            </t-form-item>
          </t-tab-panel>

          <t-tab-panel value="summary" label="汇总信息">
            <t-descriptions :column="2" bordered>
              <t-descriptions-item label="课程名称">{{ editForm.courseName }}</t-descriptions-item>
              <t-descriptions-item label="课程编号">{{ editForm.courseCode }}</t-descriptions-item>
              <t-descriptions-item label="培训类型">{{ getTypeName(editForm.type) }}</t-descriptions-item>
              <t-descriptions-item label="培训类别">{{ getCategoryName(editForm.category) }}</t-descriptions-item>
              <t-descriptions-item label="培训讲师">{{ getTrainerName(editForm.trainer) }}</t-descriptions-item>
              <t-descriptions-item label="培训时长">{{ editForm.duration }} {{ getDurationUnitName(editForm.durationUnit) }}</t-descriptions-item>
              <t-descriptions-item label="培训人数">{{ editForm.maxTrainees }}</t-descriptions-item>
              <t-descriptions-item label="已报名人数">{{ editForm.enrolledCount }}</t-descriptions-item>
              <t-descriptions-item label="培训地点">{{ editForm.location }}</t-descriptions-item>
              <t-descriptions-item label="培训状态">{{ getStatusName(editForm.status) }}</t-descriptions-item>
              <t-descriptions-item label="开始时间">{{ editForm.startTime }}</t-descriptions-item>
              <t-descriptions-item label="结束时间">{{ editForm.endTime }}</t-descriptions-item>
            </t-descriptions>
          </t-tab-panel>
        </t-tabs>
        <t-form-item style="margin-top: 20px">
          <t-space>
            <t-button theme="primary" type="submit">保存</t-button>
            <t-button theme="default" @click="handleEditClose">取消</t-button>
          </t-space>
        </t-form-item>
      </t-form>
    </t-dialog>

    <!-- 课程详情弹窗 -->
    <t-dialog
      v-model:visible="detailDialogVisible"
      header="课程详情"
      width="900px"
      :footer="false"
    >
      <t-tabs>
        <t-tab-panel value="basic" label="基本信息">
          <t-descriptions :column="2" bordered>
            <t-descriptions-item label="课程名称">{{ detailForm.courseName }}</t-descriptions-item>
            <t-descriptions-item label="课程编号">{{ detailForm.courseCode }}</t-descriptions-item>
            <t-descriptions-item label="培训类型">{{ getTypeName(detailForm.type) }}</t-descriptions-item>
            <t-descriptions-item label="培训类别">{{ getCategoryName(detailForm.category) }}</t-descriptions-item>
            <t-descriptions-item label="培训讲师">{{ getTrainerName(detailForm.trainer) }}</t-descriptions-item>
            <t-descriptions-item label="培训时长">{{ detailForm.duration }} {{ getDurationUnitName(detailForm.durationUnit) }}</t-descriptions-item>
            <t-descriptions-item label="培训人数">{{ detailForm.maxTrainees }}</t-descriptions-item>
            <t-descriptions-item label="已报名人数">{{ detailForm.enrolledCount }}</t-descriptions-item>
            <t-descriptions-item label="培训地点">{{ detailForm.location }}</t-descriptions-item>
            <t-descriptions-item label="培训状态">{{ getStatusName(detailForm.status) }}</t-descriptions-item>
            <t-descriptions-item label="开始时间">{{ detailForm.startTime }}</t-descriptions-item>
            <t-descriptions-item label="结束时间">{{ detailForm.endTime }}</t-descriptions-item>
          </t-descriptions>
        </t-tab-panel>
        <t-tab-panel value="content" label="课程内容">
          <t-descriptions :column="1" bordered>
            <t-descriptions-item label="课程简介">{{ detailForm.introduction }}</t-descriptions-item>
            <t-descriptions-item label="培训目标">{{ detailForm.objectives }}</t-descriptions-item>
            <t-descriptions-item label="课程大纲">{{ detailForm.outline }}</t-descriptions-item>
            <t-descriptions-item label="培训要求">{{ detailForm.requirements }}</t-descriptions-item>
          </t-descriptions>
        </t-tab-panel>
        <t-tab-panel value="trainees" label="参训人员">
          <t-table :data="traineeList" :columns="traineeColumns" stripe hover>
            <template #status="{ row }">
              <t-tag v-if="row.status === 'enrolled'" theme="success">已报名</t-tag>
              <t-tag v-else-if="row.status === 'inProgress'" theme="warning">学习中</t-tag>
              <t-tag v-else-if="row.status === 'completed'" theme="primary">已完成</t-tag>
              <t-tag v-else-if="row.status === 'absent'" theme="danger">缺勤</t-tag>
            </template>
            <template #progress="{ row }">
              <t-progress :percentage="row.progress" :show-info="true" size="small" />
            </template>
            <template #action="{ row }">
              <t-link theme="primary" @click="handleViewTrainee(row)">查看</t-link>
            </template>
          </t-table>
        </t-tab-panel>
      </t-tabs>
    </t-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { MessagePlugin, DialogPlugin } from 'tdesign-vue-next'

const rowKey = 'id'

const loading = ref(false)
const editDialogVisible = ref(false)
const detailDialogVisible = ref(false)
const editDialogTitle = computed(() => isEdit.value ? '编辑课程' : '新增课程')
const isEdit = ref(false)
const activeTab = ref('basic')

const statistics = reactive({
  totalCourses: 0,
  completedTrainees: 0,
  inProgressCourses: 0,
  upcomingCourses: 0
})

const searchForm = reactive({
  courseName: '',
  type: '',
  status: '',
  trainingDate: []
})

const editForm = reactive({
  id: '',
  courseName: '',
  courseCode: '',
  type: 'online',
  category: '',
  trainer: '',
  duration: 1,
  durationUnit: 'hour',
  maxTrainees: 30,
  startTime: '',
  endTime: '',
  location: '',
  status: 'draft',
  introduction: '',
  objectives: '',
  outline: '',
  requirements: '',
  materials: [],
  departments: [],
  trainees: [],
  enrolledCount: 0
})

const editRules = {
  courseName: [{ required: true, message: '请输入课程名称' }],
  type: [{ required: true, message: '请选择培训类型' }],
  category: [{ required: true, message: '请选择培训类别' }],
  trainer: [{ required: true, message: '请选择培训讲师' }],
  duration: [{ required: true, message: '请输入培训时长' }],
  durationUnit: [{ required: true, message: '请选择时长单位' }],
  maxTrainees: [{ required: true, message: '请输入培训人数' }],
  startTime: [{ required: true, message: '请选择培训开始时间' }],
  endTime: [{ required: true, message: '请选择培训结束时间' }],
  location: [{ required: true, message: '请输入培训地点' }],
  status: [{ required: true, message: '请选择培训状态' }]
}

const detailForm = reactive({
  ...editForm
})

const departments = ref([
  { value: 'tech', label: '技术部' },
  { value: 'product', label: '产品部' },
  { value: 'marketing', label: '市场部' },
  { value: 'sales', label: '销售部' },
  { value: 'hr', label: '人力资源部' },
  { value: 'finance', label: '财务部' }
])

const trainers = ref([
  { value: 'trainer1', label: '张老师' },
  { value: 'trainer2', label: '李老师' },
  { value: 'trainer3', label: '王老师' }
])

const allEmployees = ref<any[]>([])
const courseList = ref<any[]>([])
const traineeList = ref<any[]>([])
const selectedRowKeys = ref<string[]>([])

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0
})

const columns = [
  { colKey: 'courseName', title: '课程名称', width: 180 },
  { colKey: 'courseCode', title: '课程编号', width: 120 },
  { colKey: 'type', title: '培训类型', width: 100 },
  { colKey: 'trainer', title: '培训讲师', width: 100, cell: (h, { row }) => getTrainerName(row.trainer) },
  { colKey: 'startTime', title: '开始时间', width: 160 },
  { colKey: 'enrolledCount', title: '已报名', width: 80 },
  { colKey: 'maxTrainees', title: '培训人数', width: 80 },
  { colKey: 'progress', title: '培训进度', width: 120 },
  { colKey: 'status', title: '状态', width: 90 },
  { colKey: 'action', title: '操作', width: 220, fixed: 'right' }
]

const traineeColumns = [
  { colKey: 'employeeId', title: '工号', width: 100 },
  { colKey: 'name', title: '姓名', width: 100 },
  { colKey: 'department', title: '部门', width: 100 },
  { colKey: 'enrollDate', title: '报名时间', width: 160 },
  { colKey: 'status', title: '状态', width: 90 },
  { colKey: 'progress', title: '学习进度', width: 150 },
  { colKey: 'score', title: '培训成绩', width: 90 },
  { colKey: 'action', title: '操作', width: 80 }
]

onMounted(() => {
  loadData()
  loadStatistics()
  loadEmployees()
})

const loadData = async () => {
  loading.value = true
  setTimeout(() => {
    courseList.value = [
      {
        id: '1',
        courseName: '新员工入职培训',
        courseCode: 'TX20260219001',
        type: 'offline',
        category: 'new',
        trainer: 'trainer1',
        duration: 8,
        durationUnit: 'hour',
        maxTrainees: 30,
        startTime: '2026-02-20 09:00',
        endTime: '2026-02-20 17:00',
        location: '公司培训室A',
        status: 'published',
        enrolledCount: 25,
        progress: 0,
        introduction: '帮助新员工快速了解公司文化、规章制度和工作流程',
        objectives: '了解公司文化\n掌握规章制度\n熟悉工作流程',
        outline: '1. 公司文化介绍\n2. 规章制度讲解\n3. 工作流程说明\n4. 互动答疑',
        requirements: '请携带入职资料准时参加',
        materials: [],
        departments: ['tech', 'product'],
        trainees: ['emp1', 'emp2']
      },
      {
        id: '2',
        courseName: '管理技能提升培训',
        courseCode: 'TX20260219002',
        type: 'mixed',
        category: 'leadership',
        trainer: 'trainer2',
        duration: 16,
        durationUnit: 'hour',
        maxTrainees: 20,
        startTime: '2026-02-25 09:00',
        endTime: '2026-02-26 17:00',
        location: '公司培训室B',
        status: 'published',
        enrolledCount: 18,
        progress: 0,
        introduction: '提升中层管理者的管理能力和领导力',
        objectives: '提升管理能力\n增强领导力\n掌握沟通技巧',
        outline: '1. 管理理论\n2. 实战案例\n3. 角色扮演\n4. 总结反馈',
        requirements: '请提前预习相关资料',
        materials: [],
        departments: ['sales', 'marketing'],
        trainees: ['emp3', 'emp4']
      }
    ]
    pagination.total = 20
    loading.value = false
  }, 500)
}

const loadStatistics = async () => {
  statistics.totalCourses = 15
  statistics.completedTrainees = 128
  statistics.inProgressCourses = 3
  statistics.upcomingCourses = 5
}

const loadEmployees = async () => {
  allEmployees.value = [
    { key: 'emp1', label: '张三', disabled: false },
    { key: 'emp2', label: '李四', disabled: false },
    { key: 'emp3', label: '王五', disabled: false },
    { key: 'emp4', label: '赵六', disabled: false }
  ]
}

const handleSearch = () => {
  pagination.current = 1
  loadData()
}

const handleReset = () => {
  Object.assign(searchForm, {
    courseName: '',
    type: '',
    status: '',
    trainingDate: []
  })
  handleSearch()
}

const handleAdd = () => {
  isEdit.value = false
  Object.assign(editForm, {
    id: '',
    courseName: '',
    courseCode: `TX${Date.now()}`,
    type: 'online',
    category: '',
    trainer: '',
    duration: 1,
    durationUnit: 'hour',
    maxTrainees: 30,
    startTime: '',
    endTime: '',
    location: '',
    status: 'draft',
    introduction: '',
    objectives: '',
    outline: '',
    requirements: '',
    materials: [],
    departments: [],
    trainees: [],
    enrolledCount: 0
  })
  activeTab.value = 'basic'
  editDialogVisible.value = true
}

const handleEdit = (row: any) => {
  isEdit.value = true
  Object.assign(editForm, { ...row })
  activeTab.value = 'basic'
  editDialogVisible.value = true
}

const handleEditClose = () => {
  editDialogVisible.value = false
}

const handleEditSubmit = () => {
  MessagePlugin.success(isEdit.value ? '课程更新成功' : '课程创建成功')
  editDialogVisible.value = false
  loadData()
}

const handleView = (row: any) => {
  Object.assign(detailForm, { ...row })
  traineeList.value = [
    { employeeId: 'EMP001', name: '张三', department: '技术部', enrollDate: '2026-02-15 10:30', status: 'completed', progress: 100, score: 95 },
    { employeeId: 'EMP002', name: '李四', department: '产品部', enrollDate: '2026-02-15 14:20', status: 'inProgress', progress: 65, score: 0 }
  ]
  detailDialogVisible.value = true
}

const handlePublish = (row: any) => {
  const dialog = DialogPlugin({
    header: '确认发布',
    body: `确定要发布课程"${row.courseName}"吗？`,
    confirmBtn: {
      theme: 'primary',
      content: '确认发布'
    },
    onConfirm: () => {
      MessagePlugin.success('发布成功')
      dialog.destroy()
      loadData()
    }
  })
}

const handleStart = (row: any) => {
  const dialog = DialogPlugin({
    header: '确认开始',
    body: `确定要开始课程"${row.courseName}"吗？`,
    confirmBtn: {
      theme: 'primary',
      content: '确认开始'
    },
    onConfirm: () => {
      MessagePlugin.success('课程已开始')
      dialog.destroy()
      loadData()
    }
  })
}

const handleDelete = (row: any) => {
  const dialog = DialogPlugin({
    header: '确认删除',
    body: `确定要删除课程"${row.courseName}"吗？`,
    confirmBtn: {
      theme: 'danger',
      content: '确认删除'
    },
    onConfirm: () => {
      MessagePlugin.success('删除成功')
      dialog.destroy()
      loadData()
    }
  })
}

const handlePublishBatch = () => {
  if (selectedRowKeys.value.length === 0) {
    MessagePlugin.warning('请先选择要发布的课程')
    return
  }
  const dialog = DialogPlugin({
    header: '批量发布',
    body: `确定要发布选中的 ${selectedRowKeys.value.length} 个课程吗？`,
    confirmBtn: {
      theme: 'primary',
      content: '确认发布'
    },
    onConfirm: () => {
      MessagePlugin.success('批量发布成功')
      dialog.destroy()
      loadData()
    }
  })
}

const handleExport = () => {
  MessagePlugin.success('报表导出成功')
}

const handlePageChange = (pageInfo: any) => {
  pagination.current = pageInfo.current
  pagination.pageSize = pageInfo.pageSize
  loadData()
}

const handleSelectChange = (value: string[]) => {
  selectedRowKeys.value = value
}

const handleViewTrainee = (row: any) => {
  MessagePlugin.info(`查看培训记录：${row.name}`)
}

const getTypeName = (value: string) => {
  const map = {
    online: '线上培训',
    offline: '线下培训',
    mixed: '混合培训'
  }
  return map[value] || value
}

const getCategoryName = (value: string) => {
  const map = {
    new: '新员工培训',
    skill: '技能提升',
    leadership: '管理培训',
    compliance: '合规培训',
    other: '其他培训'
  }
  return map[value] || value
}

const getTrainerName = (value: string) => {
  const trainer = trainers.value.find(t => t.value === value)
  return trainer ? trainer.label : value
}

const getDurationUnitName = (value: string) => {
  const map = {
    hour: '小时',
    day: '天'
  }
  return map[value] || value
}

const getStatusName = (value: string) => {
  const map = {
    draft: '草稿',
    published: '已发布',
    inProgress: '进行中',
    completed: '已完成',
    cancelled: '已取消'
  }
  return map[value] || value
}
</script>

<style scoped lang="scss">
.training-page {
  padding: 20px;

  .stats-row {
    margin-bottom: 16px;
  }

  .stat-item {
    display: flex;
    align-items: center;
    gap: 16px;

    .stat-icon {
      width: 48px;
      height: 48px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      background: rgba(255, 255, 255, 0.2);
      color: white;
    }

    .stat-content {
      flex: 1;

      .stat-value {
        font-size: 24px;
        font-weight: bold;
        color: #333;
        line-height: 1.2;
      }

      .stat-label {
        font-size: 14px;
        color: #666;
        margin-top: 4px;
      }
    }
  }

  .search-card,
  .action-card {
    margin-bottom: 16px;
  }
}
</style>
