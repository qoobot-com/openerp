<template>
  <t-dialog
    v-model:visible="dialogVisible"
    header="员工详情"
    width="800px"
    :footer="false"
  >
    <div v-if="loading" class="loading">
      <t-loading />
    </div>
    <div v-else class="employee-detail">
      <!-- 基本信息 -->
      <t-card title="基本信息" :bordered="false" class="detail-card">
        <t-descriptions :column="2" bordered>
          <t-descriptions-item label="工号">{{ employeeInfo.code }}</t-descriptions-item>
          <t-descriptions-item label="姓名">{{ employeeInfo.name }}</t-descriptions-item>
          <t-descriptions-item label="性别">
            <t-tag v-if="employeeInfo.gender === 'male'" theme="primary">男</t-tag>
            <t-tag v-else theme="danger">女</t-tag>
          </t-descriptions-item>
          <t-descriptions-item label="手机号">{{ employeeInfo.phone }}</t-descriptions-item>
          <t-descriptions-item label="邮箱">{{ employeeInfo.email }}</t-descriptions-item>
          <t-descriptions-item label="部门">{{ employeeInfo.deptName }}</t-descriptions-item>
          <t-descriptions-item label="岗位">{{ employeeInfo.positionName }}</t-descriptions-item>
          <t-descriptions-item label="直属领导">{{ employeeInfo.leaderName }}</t-descriptions-item>
          <t-descriptions-item label="入职日期">{{ employeeInfo.entryDate }}</t-descriptions-item>
          <t-descriptions-item label="工龄">{{ employeeInfo.workYears }} 年</t-descriptions-item>
          <t-descriptions-item label="学历">
            <t-tag theme="default">{{ getEducationLabel(employeeInfo.education) }}</t-tag>
          </t-descriptions-item>
          <t-descriptions-item label="状态">
            <t-tag v-if="employeeInfo.status === 1" theme="success">在职</t-tag>
            <t-tag v-else-if="employeeInfo.status === 2" theme="warning">离职</t-tag>
            <t-tag v-else theme="default">试用期</t-tag>
          </t-descriptions-item>
        </t-descriptions>
      </t-card>

      <!-- 其他信息 -->
      <t-card title="其他信息" :bordered="false" class="detail-card">
        <t-descriptions :column="1" bordered>
          <t-descriptions-item label="住址">{{ employeeInfo.address || '-' }}</t-descriptions-item>
          <t-descriptions-item label="备注">{{ employeeInfo.remark || '-' }}</t-descriptions-item>
        </t-descriptions>
      </t-card>
    </div>
  </t-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'

interface Props {
  visible: boolean
  employeeId: string
}

interface Emits {
  (e: 'update:visible', value: boolean): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

const loading = ref(false)
const employeeInfo = reactive<any>({})

const dialogVisible = computed({
  get: () => props.visible,
  set: (val) => emit('update:visible', val),
})

const getEducationLabel = (value: string) => {
  const map: Record<string, string> = {
    bachelor: '本科',
    master: '硕士',
    doctor: '博士',
    college: '大专',
    high: '高中',
  }
  return map[value] || value
}

const fetchEmployeeDetail = async (id: string) => {
  loading.value = true
  try {
    await new Promise((resolve) => setTimeout(resolve, 300))
    Object.assign(employeeInfo, {
      code: 'EMP001',
      name: '张三',
      gender: 'male',
      phone: '13800138001',
      email: 'zhangsan@qooerp.com',
      deptName: '技术部',
      positionName: '前端工程师',
      leaderName: '李四',
      entryDate: '2024-01-01',
      workYears: 3,
      education: 'bachelor',
      status: 1,
      address: '北京市朝阳区',
      remark: '优秀员工',
    })
  } finally {
    loading.value = false
  }
}

import { watch } from 'vue'
watch(
  () => props.employeeId,
  (val) => {
    if (val) {
      fetchEmployeeDetail(val)
    }
  },
  { immediate: true },
)
</script>

<style scoped lang="scss">
.employee-detail {
  .detail-card {
    margin-bottom: 16px;
  }
}

.loading {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 200px;
}
</style>
