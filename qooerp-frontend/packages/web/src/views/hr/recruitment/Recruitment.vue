<template>
  <div class="recruitment-page">
    <!-- 统计卡片 -->
    <t-row :gutter="16" class="stats-row">
      <t-col :span="6">
        <t-card theme="primary1" header-bordered>
          <div class="stat-item">
            <div class="stat-icon">
              <t-icon name="user" size="24px" />
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ statistics.totalPositions }}</div>
              <div class="stat-label">招聘岗位</div>
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
              <div class="stat-value">{{ statistics.interviewedCount }}</div>
              <div class="stat-label">已面试</div>
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
              <div class="stat-value">{{ statistics.pendingCount }}</div>
              <div class="stat-label">待处理</div>
            </div>
          </div>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="danger1" header-bordered>
          <div class="stat-item">
            <div class="stat-icon">
              <t-icon name="error-circle" size="24px" />
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ statistics.hiredCount }}</div>
              <div class="stat-label">已录用</div>
            </div>
          </div>
        </t-card>
      </t-col>
    </t-row>

    <!-- 搜索卡片 -->
    <t-card title="搜索筛选" class="search-card">
      <t-form :data="searchForm" layout="inline" @submit="handleSearch" @reset="handleReset">
        <t-form-item label="岗位名称" name="positionName">
          <t-input v-model="searchForm.positionName" placeholder="请输入岗位名称" clearable />
        </t-form-item>
        <t-form-item label="部门" name="department">
          <t-select v-model="searchForm.department" placeholder="请选择部门" clearable>
            <t-option v-for="dept in departments" :key="dept.value" :value="dept.value" :label="dept.label" />
          </t-select>
        </t-form-item>
        <t-form-item label="招聘状态" name="status">
          <t-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <t-option value="recruiting" label="招聘中" />
            <t-option value="paused" label="暂停" />
            <t-option value="completed" label="已完成" />
            <t-option value="cancelled" label="已取消" />
          </t-select>
        </t-form-item>
        <t-form-item label="发布日期" name="publishDate">
          <t-date-range-picker v-model="searchForm.publishDate" placeholder="请选择日期" clearable />
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
          新增岗位
        </t-button>
        <t-button theme="success" @click="handleBatchProcess">
          <template #icon><t-icon name="check" /></template>
          批量处理
        </t-button>
        <t-button theme="default" @click="handleExport">
          <template #icon><t-icon name="download" /></template>
          导出报表
        </t-button>
      </t-space>
    </t-card>

    <!-- 招聘岗位列表 -->
    <t-card>
      <t-table
        :data="positionList"
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
        <template #status="{ row }">
          <t-tag v-if="row.status === 'recruiting'" theme="success">招聘中</t-tag>
          <t-tag v-else-if="row.status === 'paused'" theme="warning">暂停</t-tag>
          <t-tag v-else-if="row.status === 'completed'" theme="default">已完成</t-tag>
          <t-tag v-else-if="row.status === 'cancelled'" theme="danger">已取消</t-tag>
        </template>
        <template #urgency="{ row }">
          <t-tag v-if="row.urgency === 'high'" theme="danger">紧急</t-tag>
          <t-tag v-else-if="row.urgency === 'medium'" theme="warning">一般</t-tag>
          <t-tag v-else theme="default">不紧急</t-tag>
        </template>
        <template #publishProgress="{ row }">
          <t-progress :percentage="calculateProgress(row)" :show-info="false" theme="success" />
        </template>
        <template #action="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleView(row)">查看</t-link>
            <t-link theme="primary" @click="handleEdit(row)">编辑</t-link>
            <t-link theme="success" @click="handleInterview(row)">面试</t-link>
            <t-link theme="danger" @click="handleDelete(row)">删除</t-link>
          </t-space>
        </template>
      </t-table>
    </t-card>

    <!-- 岗位编辑弹窗 -->
    <t-dialog
      v-model:visible="editDialogVisible"
      :header="editDialogTitle"
      width="800px"
      :footer="false"
      @close="handleEditClose"
    >
      <t-form ref="editFormRef" :data="editForm" :rules="editRules" label-width="100px" @submit="handleEditSubmit">
        <t-tabs v-model="activeTab">
          <t-tab-panel value="basic" label="基本信息">
            <t-form-item label="岗位名称" name="positionName">
              <t-input v-model="editForm.positionName" placeholder="请输入岗位名称" />
            </t-form-item>
            <t-form-item label="所属部门" name="department">
              <t-select v-model="editForm.department" placeholder="请选择部门">
                <t-option v-for="dept in departments" :key="dept.value" :value="dept.value" :label="dept.label" />
              </t-select>
            </t-form-item>
            <t-form-item label="招聘人数" name="headcount">
              <t-input-number v-model="editForm.headcount" :min="1" :max="100" placeholder="请输入招聘人数" />
            </t-form-item>
            <t-form-item label="岗位性质" name="nature">
              <t-radio-group v-model="editForm.nature">
                <t-radio value="fulltime">全职</t-radio>
                <t-radio value="parttime">兼职</t-radio>
                <t-radio value="intern">实习</t-radio>
                <t-radio value="contract">合同工</t-radio>
              </t-radio-group>
            </t-form-item>
            <t-form-item label="工作地点" name="location">
              <t-input v-model="editForm.location" placeholder="请输入工作地点" />
            </t-form-item>
            <t-form-item label="薪资范围" name="salary">
              <t-space>
                <t-input-number v-model="editForm.salaryMin" :min="0" placeholder="最低薪资" />
                <span>-</span>
                <t-input-number v-model="editForm.salaryMax" :min="0" placeholder="最高薪资" />
                <t-select v-model="editForm.salaryUnit" placeholder="单位" style="width: 100px">
                  <t-option value="month" label="元/月" />
                  <t-option value="year" label="元/年" />
                  <t-option value="day" label="元/天" />
                </t-select>
              </t-space>
            </t-form-item>
            <t-form-item label="紧急程度" name="urgency">
              <t-radio-group v-model="editForm.urgency">
                <t-radio value="high">紧急</t-radio>
                <t-radio value="medium">一般</t-radio>
                <t-radio value="low">不紧急</t-radio>
              </t-radio-group>
            </t-form-item>
            <t-form-item label="招聘状态" name="status">
              <t-radio-group v-model="editForm.status">
                <t-radio value="recruiting">招聘中</t-radio>
                <t-radio value="paused">暂停</t-radio>
                <t-radio value="completed">已完成</t-radio>
                <t-radio value="cancelled">已取消</t-radio>
              </t-radio-group>
            </t-form-item>
            <t-form-item label="发布日期" name="publishDate">
              <t-date-picker v-model="editForm.publishDate" placeholder="请选择发布日期" />
            </t-form-item>
            <t-form-item label="截止日期" name="deadline">
              <t-date-picker v-model="editForm.deadline" placeholder="请选择截止日期" />
            </t-form-item>
          </t-tab-panel>

          <t-tab-panel value="requirements" label="岗位要求">
            <t-form-item label="学历要求" name="education">
              <t-select v-model="editForm.education" placeholder="请选择学历要求">
                <t-option value="junior" label="大专" />
                <t-option value="bachelor" label="本科" />
                <t-option value="master" label="硕士" />
                <t-option value="phd" label="博士" />
                <t-option value="none" label="不限" />
              </t-select>
            </t-form-item>
            <t-form-item label="工作经验" name="experience">
              <t-select v-model="editForm.experience" placeholder="请选择工作经验">
                <t-option value="no" label="无经验" />
                <t-option value="1-3" label="1-3年" />
                <t-option value="3-5" label="3-5年" />
                <t-option value="5-10" label="5-10年" />
                <t-option value="10+" label="10年以上" />
              </t-select>
            </t-form-item>
            <t-form-item label="年龄要求" name="age">
              <t-space>
                <t-input-number v-model="editForm.ageMin" :min="18" :max="60" placeholder="最小年龄" />
                <span>-</span>
                <t-input-number v-model="editForm.ageMax" :min="18" :max="60" placeholder="最大年龄" />
              </t-space>
            </t-form-item>
            <t-form-item label="专业要求" name="major">
              <t-input v-model="editForm.major" placeholder="请输入专业要求" />
            </t-form-item>
            <t-form-item label="技能要求" name="skills">
              <t-textarea v-model="editForm.skills" placeholder="请输入技能要求，多个技能用逗号分隔" :autosize="{ minRows: 3, maxRows: 6 }" />
            </t-form-item>
            <t-form-item label="岗位描述" name="description">
              <t-textarea v-model="editForm.description" placeholder="请输入岗位描述" :autosize="{ minRows: 4, maxRows: 8 }" />
            </t-form-item>
            <t-form-item label="岗位职责" name="responsibilities">
              <t-textarea v-model="editForm.responsibilities" placeholder="请输入岗位职责" :autosize="{ minRows: 4, maxRows: 8 }" />
            </t-form-item>
          </t-tab-panel>

          <t-tab-panel value="summary" label="汇总信息">
            <t-descriptions :column="2" bordered>
              <t-descriptions-item label="岗位名称">{{ editForm.positionName }}</t-descriptions-item>
              <t-descriptions-item label="所属部门">{{ getDepartmentName(editForm.department) }}</t-descriptions-item>
              <t-descriptions-item label="招聘人数">{{ editForm.headcount }}</t-descriptions-item>
              <t-descriptions-item label="岗位性质">{{ getNatureName(editForm.nature) }}</t-descriptions-item>
              <t-descriptions-item label="工作地点">{{ editForm.location }}</t-descriptions-item>
              <t-descriptions-item label="薪资范围">{{ editForm.salaryMin }} - {{ editForm.salaryMax }} {{ getSalaryUnitName(editForm.salaryUnit) }}</t-descriptions-item>
              <t-descriptions-item label="紧急程度">{{ getUrgencyName(editForm.urgency) }}</t-descriptions-item>
              <t-descriptions-item label="招聘状态">{{ getStatusName(editForm.status) }}</t-descriptions-item>
              <t-descriptions-item label="学历要求">{{ getEducationName(editForm.education) }}</t-descriptions-item>
              <t-descriptions-item label="工作经验">{{ getExperienceName(editForm.experience) }}</t-descriptions-item>
              <t-descriptions-item label="发布日期">{{ editForm.publishDate }}</t-descriptions-item>
              <t-descriptions-item label="截止日期">{{ editForm.deadline }}</t-descriptions-item>
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

    <!-- 岗位详情弹窗 -->
    <t-dialog
      v-model:visible="detailDialogVisible"
      header="岗位详情"
      width="800px"
      :footer="false"
    >
      <t-tabs>
        <t-tab-panel value="basic" label="基本信息">
          <t-descriptions :column="2" bordered>
            <t-descriptions-item label="岗位名称">{{ detailForm.positionName }}</t-descriptions-item>
            <t-descriptions-item label="所属部门">{{ getDepartmentName(detailForm.department) }}</t-descriptions-item>
            <t-descriptions-item label="招聘人数">{{ detailForm.headcount }}</t-descriptions-item>
            <t-descriptions-item label="岗位性质">{{ getNatureName(detailForm.nature) }}</t-descriptions-item>
            <t-descriptions-item label="工作地点">{{ detailForm.location }}</t-descriptions-item>
            <t-descriptions-item label="薪资范围">{{ detailForm.salaryMin }} - {{ detailForm.salaryMax }} {{ getSalaryUnitName(detailForm.salaryUnit) }}</t-descriptions-item>
            <t-descriptions-item label="紧急程度">{{ getUrgencyName(detailForm.urgency) }}</t-descriptions-item>
            <t-descriptions-item label="招聘状态">{{ getStatusName(detailForm.status) }}</t-descriptions-item>
            <t-descriptions-item label="发布日期">{{ detailForm.publishDate }}</t-descriptions-item>
            <t-descriptions-item label="截止日期">{{ detailForm.deadline }}</t-descriptions-item>
          </t-descriptions>
        </t-tab-panel>
        <t-tab-panel value="requirements" label="岗位要求">
          <t-descriptions :column="1" bordered>
            <t-descriptions-item label="学历要求">{{ getEducationName(detailForm.education) }}</t-descriptions-item>
            <t-descriptions-item label="工作经验">{{ getExperienceName(detailForm.experience) }}</t-descriptions-item>
            <t-descriptions-item label="年龄要求">{{ detailForm.ageMin }} - {{ detailForm.ageMax }}岁</t-descriptions-item>
            <t-descriptions-item label="专业要求">{{ detailForm.major }}</t-descriptions-item>
            <t-descriptions-item label="技能要求">{{ detailForm.skills }}</t-descriptions-item>
            <t-descriptions-item label="岗位描述">{{ detailForm.description }}</t-descriptions-item>
            <t-descriptions-item label="岗位职责">{{ detailForm.responsibilities }}</t-descriptions-item>
          </t-descriptions>
        </t-tab-panel>
        <t-tab-panel value="candidates" label="候选人">
          <t-table :data="candidateList" :columns="candidateColumns" stripe hover>
            <template #status="{ row }">
              <t-tag v-if="row.status === 'applied'" theme="default">已申请</t-tag>
              <t-tag v-else-if="row.status === 'interviewing'" theme="warning">面试中</t-tag>
              <t-tag v-else-if="row.status === 'hired'" theme="success">已录用</t-tag>
              <t-tag v-else-if="row.status === 'rejected'" theme="danger">已拒绝</t-tag>
            </template>
            <template #action="{ row }">
              <t-link theme="primary" @click="handleViewCandidate(row)">查看</t-link>
            </template>
          </t-table>
        </t-tab-panel>
      </t-tabs>
    </t-dialog>

    <!-- 面试弹窗 -->
    <t-dialog
      v-model:visible="interviewDialogVisible"
      header="安排面试"
      width="600px"
      :footer="false"
    >
      <t-form :data="interviewForm" label-width="100px">
        <t-form-item label="面试时间" name="interviewTime">
          <t-date-time-picker v-model="interviewForm.interviewTime" placeholder="请选择面试时间" />
        </t-form-item>
        <t-form-item label="面试地点" name="location">
          <t-input v-model="interviewForm.location" placeholder="请输入面试地点" />
        </t-form-item>
        <t-form-item label="面试官" name="interviewer">
          <t-select v-model="interviewForm.interviewer" placeholder="请选择面试官">
            <t-option v-for="user in interviewers" :key="user.value" :value="user.value" :label="user.label" />
          </t-select>
        </t-form-item>
        <t-form-item label="面试类型" name="type">
          <t-select v-model="interviewForm.type" placeholder="请选择面试类型">
            <t-option value="first" label="初试" />
            <t-option value="second" label="复试" />
            <t-option value="final" label="终试" />
          </t-select>
        </t-form-item>
        <t-form-item label="面试备注" name="remark">
          <t-textarea v-model="interviewForm.remark" placeholder="请输入面试备注" :autosize="{ minRows: 3, maxRows: 6 }" />
        </t-form-item>
        <t-form-item>
          <t-space>
            <t-button theme="primary" @click="handleInterviewSubmit">确认</t-button>
            <t-button theme="default" @click="interviewDialogVisible = false">取消</t-button>
          </t-space>
        </t-form-item>
      </t-form>
    </t-dialog>

    <!-- 批量处理弹窗 -->
    <t-dialog
      v-model:visible="batchDialogVisible"
      header="批量处理"
      width="500px"
      :footer="false"
    >
      <t-form :data="batchForm" label-width="100px">
        <t-form-item label="操作类型" name="action">
          <t-select v-model="batchForm.action" placeholder="请选择操作类型">
            <t-option value="publish" label="批量发布" />
            <t-option value="pause" label="批量暂停" />
            <t-option value="cancel" label="批量取消" />
          </t-select>
        </t-form-item>
        <t-form-item label="处理备注" name="remark">
          <t-textarea v-model="batchForm.remark" placeholder="请输入处理备注" :autosize="{ minRows: 3, maxRows: 6 }" />
        </t-form-item>
        <t-form-item>
          <t-space>
            <t-button theme="primary" @click="handleBatchSubmit">确认</t-button>
            <t-button theme="default" @click="batchDialogVisible = false">取消</t-button>
          </t-space>
        </t-form-item>
      </t-form>
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
const interviewDialogVisible = ref(false)
const batchDialogVisible = ref(false)
const editDialogTitle = computed(() => isEdit.value ? '编辑岗位' : '新增岗位')
const isEdit = ref(false)
const activeTab = ref('basic')

const statistics = reactive({
  totalPositions: 0,
  interviewedCount: 0,
  pendingCount: 0,
  hiredCount: 0
})

const searchForm = reactive({
  positionName: '',
  department: '',
  status: '',
  publishDate: []
})

const editForm = reactive({
  id: '',
  positionName: '',
  department: '',
  headcount: 1,
  nature: 'fulltime',
  location: '',
  salaryMin: 0,
  salaryMax: 0,
  salaryUnit: 'month',
  urgency: 'medium',
  status: 'recruiting',
  publishDate: '',
  deadline: '',
  education: 'bachelor',
  experience: '1-3',
  ageMin: 22,
  ageMax: 45,
  major: '',
  skills: '',
  description: '',
  responsibilities: ''
})

const editRules = {
  positionName: [{ required: true, message: '请输入岗位名称' }],
  department: [{ required: true, message: '请选择部门' }],
  headcount: [{ required: true, message: '请输入招聘人数' }],
  nature: [{ required: true, message: '请选择岗位性质' }],
  location: [{ required: true, message: '请输入工作地点' }],
  salaryMin: [{ required: true, message: '请输入最低薪资' }],
  salaryMax: [{ required: true, message: '请输入最高薪资' }],
  salaryUnit: [{ required: true, message: '请选择薪资单位' }],
  urgency: [{ required: true, message: '请选择紧急程度' }],
  status: [{ required: true, message: '请选择招聘状态' }],
  education: [{ required: true, message: '请选择学历要求' }],
  experience: [{ required: true, message: '请选择工作经验' }]
}

const detailForm = reactive({
  ...editForm
})

const interviewForm = reactive({
  positionId: '',
  interviewTime: '',
  location: '',
  interviewer: '',
  type: 'first',
  remark: ''
})

const batchForm = reactive({
  action: '',
  remark: ''
})

const departments = ref([
  { value: 'tech', label: '技术部' },
  { value: 'product', label: '产品部' },
  { value: 'marketing', label: '市场部' },
  { value: 'sales', label: '销售部' },
  { value: 'hr', label: '人力资源部' },
  { value: 'finance', label: '财务部' }
])

const interviewers = ref([
  { value: 'user1', label: '张经理' },
  { value: 'user2', label: '李主管' },
  { value: 'user3', label: '王总监' }
])

const positionList = ref<any[]>([])
const selectedRowKeys = ref<string[]>([])
const candidateList = ref<any[]>([])

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0
})

const columns = [
  { colKey: 'positionName', title: '岗位名称', width: 150 },
  { colKey: 'department', title: '所属部门', width: 100, cell: (h, { row }) => getDepartmentName(row.department) },
  { colKey: 'headcount', title: '招聘人数', width: 80 },
  { colKey: 'appliedCount', title: '已申请', width: 80 },
  { colKey: 'interviewedCount', title: '已面试', width: 80 },
  { colKey: 'hiredCount', title: '已录用', width: 80 },
  { colKey: 'publishProgress', title: '招聘进度', width: 120 },
  { colKey: 'urgency', title: '紧急程度', width: 80 },
  { colKey: 'status', title: '状态', width: 80 },
  { colKey: 'publishDate', title: '发布日期', width: 110 },
  { colKey: 'deadline', title: '截止日期', width: 110 },
  { colKey: 'action', title: '操作', width: 200, fixed: 'right' }
]

const candidateColumns = [
  { colKey: 'name', title: '姓名', width: 100 },
  { colKey: 'phone', title: '联系电话', width: 130 },
  { colKey: 'email', title: '邮箱', width: 180 },
  { colKey: 'applyDate', title: '申请日期', width: 110 },
  { colKey: 'interviewDate', title: '面试日期', width: 110 },
  { colKey: 'status', title: '状态', width: 90 },
  { colKey: 'action', title: '操作', width: 80 }
]

onMounted(() => {
  loadData()
  loadStatistics()
})

const loadData = async () => {
  loading.value = true
  // 模拟数据
  setTimeout(() => {
    positionList.value = [
      {
        id: '1',
        positionName: '前端开发工程师',
        department: 'tech',
        headcount: 2,
        appliedCount: 15,
        interviewedCount: 8,
        hiredCount: 1,
        nature: 'fulltime',
        location: '北京',
        salaryMin: 15000,
        salaryMax: 25000,
        salaryUnit: 'month',
        urgency: 'high',
        status: 'recruiting',
        publishDate: '2026-02-01',
        deadline: '2026-03-01',
        education: 'bachelor',
        experience: '3-5',
        ageMin: 22,
        ageMax: 35,
        major: '计算机相关专业',
        skills: 'Vue,React,TypeScript',
        description: '负责公司前端产品开发',
        responsibilities: '1. 参与需求分析\n2. 编写高质量代码\n3. 进行代码评审'
      },
      {
        id: '2',
        positionName: '产品经理',
        department: 'product',
        headcount: 1,
        appliedCount: 8,
        interviewedCount: 4,
        hiredCount: 0,
        nature: 'fulltime',
        location: '北京',
        salaryMin: 20000,
        salaryMax: 35000,
        salaryUnit: 'month',
        urgency: 'medium',
        status: 'recruiting',
        publishDate: '2026-02-05',
        deadline: '2026-03-05',
        education: 'bachelor',
        experience: '3-5',
        ageMin: 25,
        ageMax: 40,
        major: '不限',
        skills: '需求分析,产品设计,项目管理',
        description: '负责产品规划与设计',
        responsibilities: '1. 负责产品规划\n2. 需求分析与设计\n3. 跟进产品开发'
      }
    ]
    pagination.total = 20
    loading.value = false
  }, 500)
}

const loadStatistics = async () => {
  // 模拟数据
  statistics.totalPositions = 12
  statistics.interviewedCount = 45
  statistics.pendingCount = 8
  statistics.hiredCount = 15
}

const handleSearch = () => {
  pagination.current = 1
  loadData()
}

const handleReset = () => {
  Object.assign(searchForm, {
    positionName: '',
    department: '',
    status: '',
    publishDate: []
  })
  handleSearch()
}

const handleAdd = () => {
  isEdit.value = false
  Object.assign(editForm, {
    id: '',
    positionName: '',
    department: '',
    headcount: 1,
    nature: 'fulltime',
    location: '',
    salaryMin: 0,
    salaryMax: 0,
    salaryUnit: 'month',
    urgency: 'medium',
    status: 'recruiting',
    publishDate: '',
    deadline: '',
    education: 'bachelor',
    experience: '1-3',
    ageMin: 22,
    ageMax: 45,
    major: '',
    skills: '',
    description: '',
    responsibilities: ''
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
  MessagePlugin.success(isEdit.value ? '岗位更新成功' : '岗位创建成功')
  editDialogVisible.value = false
  loadData()
}

const handleView = (row: any) => {
  Object.assign(detailForm, { ...row })
  candidateList.value = [
    { id: '1', name: '张三', phone: '13800138001', email: 'zhangsan@example.com', applyDate: '2026-02-10', interviewDate: '2026-02-15', status: 'hired' },
    { id: '2', name: '李四', phone: '13800138002', email: 'lisi@example.com', applyDate: '2026-02-12', interviewDate: '2026-02-17', status: 'interviewing' }
  ]
  detailDialogVisible.value = true
}

const handleInterview = (row: any) => {
  interviewForm.positionId = row.id
  interviewForm.interviewTime = ''
  interviewForm.location = ''
  interviewForm.interviewer = ''
  interviewForm.type = 'first'
  interviewForm.remark = ''
  interviewDialogVisible.value = true
}

const handleInterviewSubmit = () => {
  MessagePlugin.success('面试安排成功')
  interviewDialogVisible.value = false
  loadData()
}

const handleDelete = (row: any) => {
  const dialog = DialogPlugin({
    header: '确认删除',
    body: `确定要删除岗位"${row.positionName}"吗？`,
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

const handleBatchProcess = () => {
  if (selectedRowKeys.value.length === 0) {
    MessagePlugin.warning('请先选择要处理的岗位')
    return
  }
  batchForm.action = ''
  batchForm.remark = ''
  batchDialogVisible.value = true
}

const handleBatchSubmit = () => {
  MessagePlugin.success('批量处理成功')
  batchDialogVisible.value = false
  loadData()
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

const handleViewCandidate = (row: any) => {
  MessagePlugin.info(`查看候选人：${row.name}`)
}

const calculateProgress = (row: any) => {
  if (row.headcount === 0) return 0
  return Math.round((row.hiredCount / row.headcount) * 100)
}

const getDepartmentName = (value: string) => {
  const dept = departments.value.find(d => d.value === value)
  return dept ? dept.label : value
}

const getNatureName = (value: string) => {
  const map = {
    fulltime: '全职',
    parttime: '兼职',
    intern: '实习',
    contract: '合同工'
  }
  return map[value] || value
}

const getSalaryUnitName = (value: string) => {
  const map = {
    month: '元/月',
    year: '元/年',
    day: '元/天'
  }
  return map[value] || value
}

const getUrgencyName = (value: string) => {
  const map = {
    high: '紧急',
    medium: '一般',
    low: '不紧急'
  }
  return map[value] || value
}

const getStatusName = (value: string) => {
  const map = {
    recruiting: '招聘中',
    paused: '暂停',
    completed: '已完成',
    cancelled: '已取消'
  }
  return map[value] || value
}

const getEducationName = (value: string) => {
  const map = {
    junior: '大专',
    bachelor: '本科',
    master: '硕士',
    phd: '博士',
    none: '不限'
  }
  return map[value] || value
}

const getExperienceName = (value: string) => {
  const map = {
    no: '无经验',
    '1-3': '1-3年',
    '3-5': '3-5年',
    '5-10': '5-10年',
    '10+': '10年以上'
  }
  return map[value] || value
}
</script>

<style scoped lang="scss">
.recruitment-page {
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
