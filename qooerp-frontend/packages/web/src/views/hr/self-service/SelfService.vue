<template>
  <div class="self-service-page">
    <!-- 个人信息卡片 -->
    <t-card title="个人信息" class="info-card">
      <t-row :gutter="16">
        <t-col :span="6">
          <div class="info-item">
            <div class="info-label">姓名</div>
            <div class="info-value">{{ employeeInfo.name }}</div>
          </div>
        </t-col>
        <t-col :span="6">
          <div class="info-item">
            <div class="info-label">工号</div>
            <div class="info-value">{{ employeeInfo.employeeId }}</div>
          </div>
        </t-col>
        <t-col :span="6">
          <div class="info-item">
            <div class="info-label">部门</div>
            <div class="info-value">{{ employeeInfo.department }}</div>
          </div>
        </t-col>
        <t-col :span="6">
          <div class="info-item">
            <div class="info-label">岗位</div>
            <div class="info-value">{{ employeeInfo.position }}</div>
          </div>
        </t-col>
        <t-col :span="6">
          <div class="info-item">
            <div class="info-label">入职日期</div>
            <div class="info-value">{{ employeeInfo.joinDate }}</div>
          </div>
        </t-col>
        <t-col :span="6">
          <div class="info-item">
            <div class="info-label">手机号</div>
            <div class="info-value">{{ employeeInfo.phone }}</div>
          </div>
        </t-col>
        <t-col :span="6">
          <div class="info-item">
            <div class="info-label">邮箱</div>
            <div class="info-value">{{ employeeInfo.email }}</div>
          </div>
        </t-col>
        <t-col :span="6">
          <div class="info-item">
            <div class="info-label">状态</div>
            <div class="info-value">
              <t-tag v-if="employeeInfo.status === 'active'" theme="success">在职</t-tag>
              <t-tag v-else theme="default">离职</t-tag>
            </div>
          </div>
        </t-col>
      </t-row>
    </t-card>

    <!-- 考勤统计 -->
    <t-card title="本月考勤统计" class="attendance-card">
      <t-row :gutter="16">
        <t-col :span="6">
          <div class="stat-item">
            <div class="stat-icon">
              <t-icon name="check-circle" size="24px" />
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ attendanceStats.presentDays }}</div>
              <div class="stat-label">出勤天数</div>
            </div>
          </div>
        </t-col>
        <t-col :span="6">
          <div class="stat-item">
            <div class="stat-icon">
              <t-icon name="time" size="24px" />
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ attendanceStats.lateDays }}</div>
              <div class="stat-label">迟到次数</div>
            </div>
          </div>
        </t-col>
        <t-col :span="6">
          <div class="stat-item">
            <div class="stat-icon">
              <t-icon name="error-circle" size="24px" />
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ attendanceStats.absentDays }}</div>
              <div class="stat-label">缺勤天数</div>
            </div>
          </div>
        </t-col>
        <t-col :span="6">
          <div class="stat-item">
            <div class="stat-icon">
              <t-icon name="calendar" size="24px" />
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ attendanceStats.leaveDays }}</div>
              <div class="stat-label">请假天数</div>
            </div>
          </div>
        </t-col>
      </t-row>
    </t-card>

    <!-- 快捷功能 -->
    <t-card title="快捷功能" class="quick-actions">
      <t-row :gutter="16">
        <t-col :span="4">
          <div class="action-item" @click="handleApplyLeave">
            <t-icon name="calendar-check" size="32px" />
            <div class="action-label">请假申请</div>
          </div>
        </t-col>
        <t-col :span="4">
          <div class="action-item" @click="handleApplyOvertime">
            <t-icon name="time" size="32px" />
            <div class="action-label">加班申请</div>
          </div>
        </t-col>
        <t-col :span="4">
          <div class="action-item" @click="handleReimbursement">
            <t-icon name="money-circle" size="32px" />
            <div class="action-label">报销申请</div>
          </div>
        </t-col>
        <t-col :span="4">
          <div class="action-item" @click="handleViewSalary">
            <t-icon name="wallet" size="32px" />
            <div class="action-label">工资查询</div>
          </div>
        </t-col>
        <t-col :span="4">
          <div class="action-item" @click="handleAttendanceRecord">
            <t-icon name="check-circle" size="32px" />
            <div class="action-label">考勤记录</div>
          </div>
        </t-col>
        <t-col :span="4">
          <div class="action-item" @click="handleTrainingCenter">
            <t-icon name="usergroup" size="32px" />
            <div class="action-label">培训中心</div>
          </div>
        </t-col>
      </t-row>
    </t-card>

    <!-- 我的申请 -->
    <t-card title="我的申请" class="applications">
      <t-tabs v-model="activeTab">
        <t-tab-panel value="leave" label="请假申请">
          <t-button theme="primary" @click="handleApplyLeave" style="margin-bottom: 16px">
            <template #icon><t-icon name="add" /></template>
            新增请假
          </t-button>
          <t-table :data="leaveList" :columns="leaveColumns" stripe hover>
            <template #status="{ row }">
              <t-tag v-if="row.status === 'pending'" theme="warning">待审核</t-tag>
              <t-tag v-else-if="row.status === 'approved'" theme="success">已通过</t-tag>
              <t-tag v-else-if="row.status === 'rejected'" theme="danger">已驳回</t-tag>
              <t-tag v-else theme="default">已取消</t-tag>
            </template>
            <template #action="{ row }">
              <t-space>
                <t-link theme="primary" @click="handleViewLeave(row)">查看</t-link>
                <t-link v-if="row.status === 'pending'" theme="danger" @click="handleCancelLeave(row)">撤销</t-link>
              </t-space>
            </template>
          </t-table>
        </t-tab-panel>

        <t-tab-panel value="overtime" label="加班申请">
          <t-button theme="primary" @click="handleApplyOvertime" style="margin-bottom: 16px">
            <template #icon><t-icon name="add" /></template>
            新增加班
          </t-button>
          <t-table :data="overtimeList" :columns="overtimeColumns" stripe hover>
            <template #status="{ row }">
              <t-tag v-if="row.status === 'pending'" theme="warning">待审核</t-tag>
              <t-tag v-else-if="row.status === 'approved'" theme="success">已通过</t-tag>
              <t-tag v-else-if="row.status === 'rejected'" theme="danger">已驳回</t-tag>
              <t-tag v-else theme="default">已取消</t-tag>
            </template>
            <template #action="{ row }">
              <t-space>
                <t-link theme="primary" @click="handleViewOvertime(row)">查看</t-link>
                <t-link v-if="row.status === 'pending'" theme="danger" @click="handleCancelOvertime(row)">撤销</t-link>
              </t-space>
            </template>
          </t-table>
        </t-tab-panel>

        <t-tab-panel value="reimbursement" label="报销申请">
          <t-button theme="primary" @click="handleReimbursement" style="margin-bottom: 16px">
            <template #icon><t-icon name="add" /></template>
            新增报销
          </t-button>
          <t-table :data="reimbursementList" :columns="reimbursementColumns" stripe hover>
            <template #status="{ row }">
              <t-tag v-if="row.status === 'pending'" theme="warning">待审核</t-tag>
              <t-tag v-else-if="row.status === 'approved'" theme="success">已通过</t-tag>
              <t-tag v-else-if="row.status === 'rejected'" theme="danger">已驳回</t-tag>
              <t-tag v-else theme="default">已取消</t-tag>
            </template>
            <template #action="{ row }">
              <t-space>
                <t-link theme="primary" @click="handleViewReimbursement(row)">查看</t-link>
                <t-link v-if="row.status === 'pending'" theme="danger" @click="handleCancelReimbursement(row)">撤销</t-link>
              </t-space>
            </template>
          </t-table>
        </t-tab-panel>
      </t-tabs>
    </t-card>

    <!-- 请假申请弹窗 -->
    <t-dialog
      v-model:visible="leaveDialogVisible"
      header="请假申请"
      width="600px"
      :footer="false"
    >
      <t-form :data="leaveForm" :rules="leaveRules" label-width="100px" @submit="handleLeaveSubmit">
        <t-form-item label="请假类型" name="leaveType">
          <t-select v-model="leaveForm.leaveType" placeholder="请选择请假类型">
            <t-option value="annual" label="年假" />
            <t-option value="sick" label="病假" />
            <t-option value="personal" label="事假" />
            <t-option value="marriage" label="婚假" />
            <t-option value="maternity" label="产假" />
            <t-option value="other" label="其他" />
          </t-select>
        </t-form-item>
        <t-form-item label="开始时间" name="startTime">
          <t-date-time-picker v-model="leaveForm.startTime" placeholder="请选择开始时间" />
        </t-form-item>
        <t-form-item label="结束时间" name="endTime">
          <t-date-time-picker v-model="leaveForm.endTime" placeholder="请选择结束时间" />
        </t-form-item>
        <t-form-item label="请假天数" name="days">
          <t-input-number v-model="leaveForm.days" :min="0.5" :step="0.5" placeholder="请假天数" disabled />
        </t-form-item>
        <t-form-item label="请假原因" name="reason">
          <t-textarea v-model="leaveForm.reason" placeholder="请输入请假原因" :autosize="{ minRows: 4, maxRows: 8 }" />
        </t-form-item>
        <t-form-item label="请假附件" name="attachments">
          <t-upload
            v-model="leaveForm.attachments"
            theme="file-input"
            placeholder="点击上传附件"
          />
        </t-form-item>
        <t-form-item>
          <t-space>
            <t-button theme="primary" type="submit">提交申请</t-button>
            <t-button theme="default" @click="leaveDialogVisible = false">取消</t-button>
          </t-space>
        </t-form-item>
      </t-form>
    </t-dialog>

    <!-- 加班申请弹窗 -->
    <t-dialog
      v-model:visible="overtimeDialogVisible"
      header="加班申请"
      width="600px"
      :footer="false"
    >
      <t-form :data="overtimeForm" :rules="overtimeRules" label-width="100px" @submit="handleOvertimeSubmit">
        <t-form-item label="加班日期" name="overtimeDate">
          <t-date-picker v-model="overtimeForm.overtimeDate" placeholder="请选择加班日期" />
        </t-form-item>
        <t-form-item label="开始时间" name="startTime">
          <t-time-picker v-model="overtimeForm.startTime" placeholder="请选择开始时间" />
        </t-form-item>
        <t-form-item label="结束时间" name="endTime">
          <t-time-picker v-model="overtimeForm.endTime" placeholder="请选择结束时间" />
        </t-form-item>
        <t-form-item label="加班时长" name="duration">
          <t-input-number v-model="overtimeForm.duration" :min="0" :step="0.5" placeholder="加班时长" disabled />
        </t-form-item>
        <t-form-item label="加班原因" name="reason">
          <t-textarea v-model="overtimeForm.reason" placeholder="请输入加班原因" :autosize="{ minRows: 4, maxRows: 8 }" />
        </t-form-item>
        <t-form-item>
          <t-space>
            <t-button theme="primary" type="submit">提交申请</t-button>
            <t-button theme="default" @click="overtimeDialogVisible = false">取消</t-button>
          </t-space>
        </t-form-item>
      </t-form>
    </t-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch } from 'vue'
import { MessagePlugin } from 'tdesign-vue-next'

const activeTab = ref('leave')

const employeeInfo = reactive({
  name: '张三',
  employeeId: 'EMP001',
  department: '技术部',
  position: '前端开发工程师',
  joinDate: '2024-01-15',
  phone: '13800138000',
  email: 'zhangsan@qoobot.com',
  status: 'active'
})

const attendanceStats = reactive({
  presentDays: 15,
  lateDays: 2,
  absentDays: 0,
  leaveDays: 1
})

const leaveDialogVisible = ref(false)
const overtimeDialogVisible = ref(false)
const reimbursementDialogVisible = ref(false)

const leaveForm = reactive({
  leaveType: '',
  startTime: '',
  endTime: '',
  days: 0,
  reason: '',
  attachments: []
})

const leaveRules = {
  leaveType: [{ required: true, message: '请选择请假类型' }],
  startTime: [{ required: true, message: '请选择开始时间' }],
  endTime: [{ required: true, message: '请选择结束时间' }],
  reason: [{ required: true, message: '请输入请假原因' }]
}

const overtimeForm = reactive({
  overtimeDate: '',
  startTime: '',
  endTime: '',
  duration: 0,
  reason: ''
})

const overtimeRules = {
  overtimeDate: [{ required: true, message: '请选择加班日期' }],
  startTime: [{ required: true, message: '请选择开始时间' }],
  endTime: [{ required: true, message: '请选择结束时间' }],
  reason: [{ required: true, message: '请输入加班原因' }]
}

const leaveList = ref<any[]>([])
const overtimeList = ref<any[]>([])
const reimbursementList = ref<any[]>([])

const leaveColumns = [
  { colKey: 'leaveNo', title: '请假单号', width: 140 },
  { colKey: 'leaveType', title: '请假类型', width: 100 },
  { colKey: 'startTime', title: '开始时间', width: 160 },
  { colKey: 'endTime', title: '结束时间', width: 160 },
  { colKey: 'days', title: '请假天数', width: 100 },
  { colKey: 'reason', title: '请假原因', width: 200 },
  { colKey: 'status', title: '状态', width: 90 },
  { colKey: 'applyDate', title: '申请日期', width: 110 },
  { colKey: 'action', title: '操作', width: 120 }
]

const overtimeColumns = [
  { colKey: 'overtimeNo', title: '加班单号', width: 140 },
  { colKey: 'overtimeDate', title: '加班日期', width: 110 },
  { colKey: 'startTime', title: '开始时间', width: 90 },
  { colKey: 'endTime', title: '结束时间', width: 90 },
  { colKey: 'duration', title: '加班时长', width: 100 },
  { colKey: 'reason', title: '加班原因', width: 200 },
  { colKey: 'status', title: '状态', width: 90 },
  { colKey: 'applyDate', title: '申请日期', width: 110 },
  { colKey: 'action', title: '操作', width: 120 }
]

const reimbursementColumns = [
  { colKey: 'reimbursementNo', title: '报销单号', width: 140 },
  { colKey: 'expenseType', title: '费用类型', width: 100 },
  { colKey: 'expenseDate', title: '费用日期', width: 110 },
  { colKey: 'amount', title: '报销金额', width: 100 },
  { colKey: 'description', title: '费用说明', width: 200 },
  { colKey: 'status', title: '状态', width: 90 },
  { colKey: 'applyDate', title: '申请日期', width: 110 },
  { colKey: 'action', title: '操作', width: 120 }
]

onMounted(() => {
  loadLeaveList()
  loadOvertimeList()
  loadReimbursementList()
})

const loadLeaveList = async () => {
  leaveList.value = [
    {
      id: '1',
      leaveNo: 'LJ20260219001',
      leaveType: 'annual',
      startTime: '20xx-xx-xx 09:00',
      endTime: '2026-02-21 18:00',
      days: 2,
      reason: '个人事务处理',
      status: 'pending',
      applyDate: '2026-02-19'
    },
    {
      id: '2',
      leaveNo: 'LJ20260218001',
      leaveType: 'sick',
      startTime: '2026-02-15 09:00',
      endTime: '2026-02-15 18:00',
      days: 1,
      reason: '身体不适',
      status: 'approved',
      applyDate: '2026-02-15'
    }
  ]
}

const loadOvertimeList = async () => {
  overtimeList.value = [
    {
      id: '1',
      overtimeNo: 'JB20260219001',
      overtimeDate: '2026-02-18',
      startTime: '18:00',
      endTime: '21:00',
      duration: 3,
      reason: '项目紧急任务',
      status: 'approved',
      applyDate: '20xx-xx-xx'
    }
  ]
}

const loadReimbursementList = async () => {
  reimbursementList.value = [
    {
      id: '1',
      reimbursementNo: 'BX20260219001',
      expenseType: 'travel',
      expenseDate: '2026-02-10',
      amount: 500,
      description: '差旅费',
      status: 'approved',
      applyDate: '2026-02-12'
    }
  ]
}

const handleApplyLeave = () => {
  Object.assign(leaveForm, {
    leaveType: '',
    startTime: '',
    endTime: '',
    days: 0,
    reason: '',
    attachments: []
  })
  leaveDialogVisible.value = true
}

const handleLeaveSubmit = () => {
  MessagePlugin.success('请假申请提交成功')
  leaveDialogVisible.value = false
  loadLeaveList()
}

const handleApplyOvertime = () => {
  Object.assign(overtimeForm, {
    overtimeDate: '',
    startTime: '',
    endTime: '',
    duration: 0,
    reason: ''
  })
  overtimeDialogVisible.value = true
}

const handleOvertimeSubmit = () => {
  MessagePlugin.success('加班申请提交成功')
  overtimeDialogVisible.value = false
  loadOvertimeList()
}

const handleReimbursement = () => {
  MessagePlugin.info('跳转到报销申请页面')
}

const handleViewSalary = () => {
  MessagePlugin.info('跳转到工资查询页面')
}

const handleAttendanceRecord = () => {
  MessagePlugin.info('跳转到考勤记录页面')
}

const handleTrainingCenter = () => {
  MessagePlugin.info('跳转到培训中心页面')
}

const handleViewLeave = (row: any) => {
  MessagePlugin.info(`查看请假申请：${row.leaveNo}`)
}

const handleCancelLeave = (row: any) => {
  MessagePlugin.success(`请假申请已撤销：${row.leaveNo}`)
  loadLeaveList()
}

const handleViewOvertime = (row: any) => {
  MessagePlugin.info(`查看加班申请：${row.overtimeNo}`)
}

const handleCancelOvertime = (row: any) => {
  MessagePlugin.success(`加班申请已撤销：${row.overtimeNo}`)
  loadOvertimeList()
}

const handleViewReimbursement = (row: any) => {
  MessagePlugin.info(`查看报销申请：${row.reimbursementNo}`)
}

const handleCancelReimbursement = (row: any) => {
  MessagePlugin.success(`报销申请已撤销：${row.reimbursementNo}`)
  loadReimbursementList()
}

// 监听请假时间变化，自动计算天数
watch(() => [leaveForm.startTime, leaveForm.endTime], ([start, end]) => {
  if (start && end) {
    const startDate = new Date(start)
    const endDate = new Date(end)
    const diffTime = endDate.getTime() - startDate.getTime()
    const diffDays = diffTime / (1000 * 3600 * 24)
    leaveForm.days = Math.max(0, Math.round(diffDays * 2) / 2)
  }
})

// 监听加班时间变化，自动计算时长
watch(() => [overtimeForm.startTime, overtimeForm.endTime], ([start, end]) => {
  if (start && end) {
    const [startHour, startMin] = start.split(':').map(Number)
    const [endHour, endMin] = end.split(':').map(Number)
    const startMinTotal = startHour * 60 + startMin
    const endMinTotal = endHour * 60 + endMin
    const diffMin = endMinTotal - startMinTotal
    overtimeForm.duration = Math.max(0, diffMin / 60)
  }
})
</script>

<style scoped lang="scss">
.self-service-page {
  padding: 20px;

  .info-card,
  .attendance-card,
  .quick-actions,
  .applications {
    margin-bottom: 16px;
  }

  .info-item {
    padding: 10px 0;

    .info-label {
      font-size: 14px;
      color: #666;
      margin-bottom: 8px;
    }

    .info-value {
      font-size: 16px;
      color: #333;
      font-weight: 500;
    }
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

  .action-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 24px;
    border-radius: 8px;
    background: #f7f8fa;
    cursor: pointer;
    transition: all 0.3s;

    &:hover {
      background: #e9e9eb;
      transform: translateY(-2px);
    }

    .action-label {
      margin-top: 12px;
      font-size: 14px;
      color: #333;
    }
  }
}
</style>
