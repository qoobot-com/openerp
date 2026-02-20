<template>
  <t-dialog
    v-model:visible="dialogVisible"
    :header="isEdit ? '编辑员工' : '新增员工'"
    width="800px"
    :confirm-btn="null"
    @close="handleClose"
  >
    <t-form
      ref="formRef"
      :data="formData"
      :rules="rules"
      label-width="100px"
      @submit="handleSubmit"
    >
      <t-form-item label="工号" name="code">
        <t-input
          v-model="formData.code"
          placeholder="请输入工号"
          :disabled="isEdit"
        />
      </t-form-item>

      <t-form-item label="姓名" name="name">
        <t-input v-model="formData.name" placeholder="请输入姓名" />
      </t-form-item>

      <t-form-item label="性别" name="gender">
        <t-radio-group v-model="formData.gender">
          <t-radio value="male">男</t-radio>
          <t-radio value="female">女</t-radio>
        </t-radio-group>
      </t-form-item>

      <t-form-item label="手机号" name="phone">
        <t-input v-model="formData.phone" placeholder="请输入手机号" />
      </t-form-item>

      <t-form-item label="邮箱" name="email">
        <t-input v-model="formData.email" placeholder="请输入邮箱" />
      </t-form-item>

      <t-form-item label="部门" name="deptId">
        <t-select
          v-model="formData.deptId"
          placeholder="请选择部门"
          :options="deptOptions"
        />
      </t-form-item>

      <t-form-item label="岗位" name="positionId">
        <t-select
          v-model="formData.positionId"
          placeholder="请选择岗位"
          :options="positionOptions"
        />
      </t-form-item>

      <t-form-item label="直属领导" name="leaderId">
        <t-select
          v-model="formData.leaderId"
          placeholder="请选择直属领导"
          filterable
          :options="leaderOptions"
        />
      </t-form-item>

      <t-form-item label="入职日期" name="entryDate">
        <t-date-picker
          v-model="formData.entryDate"
          placeholder="请选择入职日期"
          style="width: 100%"
        />
      </t-form-item>

      <t-form-item label="状态" name="status">
        <t-radio-group v-model="formData.status">
          <t-radio :value="1">在职</t-radio>
          <t-radio :value="0">试用期</t-radio>
          <t-radio :value="2">离职</t-radio>
        </t-radio-group>
      </t-form-item>

      <t-form-item label="工龄" name="workYears">
        <t-input-number v-model="formData.workYears" :min="0" :decimal-places="1" />
        <span style="margin-left: 8px">年</span>
      </t-form-item>

      <t-form-item label="学历" name="education">
        <t-select
          v-model="formData.education"
          placeholder="请选择学历"
          :options="educationOptions"
        />
      </t-form-item>

      <t-form-item label="住址" name="address">
        <t-textarea
          v-model="formData.address"
          placeholder="请输入住址"
          :maxlength="200"
        />
      </t-form-item>

      <t-form-item label="备注" name="remark">
        <t-textarea
          v-model="formData.remark"
          placeholder="请输入备注"
          :maxlength="200"
        />
      </t-form-item>
    </t-form>

    <template #footer>
      <t-button theme="default" @click="handleClose">取消</t-button>
      <t-button theme="primary" :loading="loading" @click="handleSubmit">确定</t-button>
    </template>
  </t-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch } from 'vue'
import { MessagePlugin } from 'tdesign-vue-next'

interface Props {
  visible: boolean
  employeeId: string
}

interface Emits {
  (e: 'update:visible', value: boolean): void
  (e: 'success'): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

const formRef = ref()
const loading = ref(false)

const dialogVisible = computed({
  get: () => props.visible,
  set: (val) => emit('update:visible', val),
})

const isEdit = computed(() => !!props.employeeId)

const formData = reactive({
  code: '',
  name: '',
  gender: 'male',
  phone: '',
  email: '',
  deptId: '',
  positionId: '',
  leaderId: '',
  entryDate: '',
  status: 1,
  workYears: 0,
  education: '',
  address: '',
  remark: '',
})

const rules = {
  code: [{ required: true, message: '请输入工号' }],
  name: [{ required: true, message: '请输入姓名' }],
  gender: [{ required: true, message: '请选择性别' }],
  phone: [
    { required: true, message: '请输入手机号' },
    {
      pattern: /^1[3-9]\d{9}$/,
      message: '手机号格式不正确',
    },
  ],
  email: [
    { required: true, message: '请输入邮箱' },
    {
      pattern: /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/,
      message: '邮箱格式不正确',
    },
  ],
  deptId: [{ required: true, message: '请选择部门' }],
  positionId: [{ required: true, message: '请选择岗位' }],
  entryDate: [{ required: true, message: '请选择入职日期' }],
  education: [{ required: true, message: '请选择学历' }],
}

const deptOptions = [
  { label: '技术部', value: '1' },
  { label: '销售部', value: '2' },
  { label: '财务部', value: '3' },
  { label: '人事部', value: '4' },
]

const positionOptions = [
  { label: '前端工程师', value: '1' },
  { label: '后端工程师', value: '2' },
  { label: '销售经理', value: '3' },
  { label: '财务专员', value: '4' },
]

const leaderOptions = [
  { label: '张三', value: '1' },
  { label: '李四', value: '2' },
  { label: '王五', value: '3' },
]

const educationOptions = [
  { label: '本科', value: 'bachelor' },
  { label: '硕士', value: 'master' },
  { label: '博士', value: 'doctor' },
  { label: '大专', value: 'college' },
  { label: '高中', value: 'high' },
]

const resetForm = () => {
  Object.assign(formData, {
    code: '',
    name: '',
    gender: 'male',
    phone: '',
    email: '',
    deptId: '',
    positionId: '',
    leaderId: '',
    entryDate: '',
    status: 1,
    workYears: 0,
    education: '',
    address: '',
    remark: '',
  })
  formRef.value?.reset()
}

const fetchEmployeeDetail = async (id: string) => {
  Object.assign(formData, {
    code: 'EMP001',
    name: '张三',
    gender: 'male',
    phone: '13800138001',
    email: 'zhangsan@qooerp.com',
    deptId: '1',
    positionId: '1',
    leaderId: '2',
    entryDate: '2024-01-01',
    status: 1,
    workYears: 3,
    education: 'bachelor',
    address: '北京市朝阳区',
    remark: '优秀员工',
  })
}

const handleSubmit = async () => {
  const valid = await formRef.value?.validate()
  if (valid === true) {
    loading.value = true
    try {
      await new Promise((resolve) => setTimeout(resolve, 500))
      MessagePlugin.success(isEdit.value ? '编辑成功' : '新增成功')
      emit('success')
      resetForm()
    } catch (error) {
      MessagePlugin.error('操作失败')
    } finally {
      loading.value = false
    }
  }
}

const handleClose = () => {
  resetForm()
  dialogVisible.value = false
}

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
