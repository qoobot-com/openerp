<template>
  <t-dialog
    v-model:visible="dialogVisible"
    :header="isEdit ? '编辑用户' : '新增用户'"
    width="600px"
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
      <t-form-item label="用户名" name="username">
        <t-input
          v-model="formData.username"
          placeholder="请输入用户名"
          :disabled="isEdit"
        />
      </t-form-item>

      <t-form-item label="姓名" name="name">
        <t-input v-model="formData.name" placeholder="请输入姓名" />
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

      <t-form-item label="角色" name="roleIds">
        <t-select
          v-model="formData.roleIds"
          placeholder="请选择角色"
          multiple
          :options="roleOptions"
        />
      </t-form-item>

      <t-form-item v-if="!isEdit" label="密码" name="password">
        <t-input
          v-model="formData.password"
          type="password"
          placeholder="请输入密码"
        />
      </t-form-item>

      <t-form-item label="状态" name="status">
        <t-radio-group v-model="formData.status">
          <t-radio :value="1">正常</t-radio>
          <t-radio :value="0">禁用</t-radio>
        </t-radio-group>
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
  userId: string
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

const isEdit = computed(() => !!props.userId)

const formData = reactive({
  username: '',
  name: '',
  phone: '',
  email: '',
  deptId: '',
  roleIds: [] as string[],
  password: '',
  status: 1,
  remark: '',
})

const rules = {
  username: [{ required: true, message: '请输入用户名' }],
  name: [{ required: true, message: '请输入姓名' }],
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
  roleIds: [{ required: true, message: '请选择角色' }],
  password: [
    {
      required: computed(() => !isEdit.value),
      message: '请输入密码',
      type: 'error',
    },
    {
      min: 6,
      message: '密码长度至少6位',
      type: 'warning',
    },
  ],
}

const deptOptions = [
  { label: '技术部', value: '1' },
  { label: '销售部', value: '2' },
  { label: '财务部', value: '3' },
  { label: '人事部', value: '4' },
]

const roleOptions = [
  { label: '超级管理员', value: '1' },
  { label: '普通管理员', value: '2' },
  { label: '普通用户', value: '3' },
]

// 重置表单
const resetForm = () => {
  Object.assign(formData, {
    username: '',
    name: '',
    phone: '',
    email: '',
    deptId: '',
    roleIds: [],
    password: '',
    status: 1,
    remark: '',
  })
  formRef.value?.reset()
}

// 获取用户详情
const fetchUserDetail = async (id: string) => {
  // 模拟获取详情
  Object.assign(formData, {
    username: 'admin',
    name: '管理员',
    phone: '13800138000',
    email: 'admin@qooerp.com',
    deptId: '1',
    roleIds: ['1'],
    status: 1,
    remark: '系统管理员',
  })
}

// 提交
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

// 关闭
const handleClose = () => {
  resetForm()
  dialogVisible.value = false
}

// 监听 userId 变化
watch(
  () => props.userId,
  (val) => {
    if (val) {
      fetchUserDetail(val)
    }
  },
  { immediate: true },
)
</script>
