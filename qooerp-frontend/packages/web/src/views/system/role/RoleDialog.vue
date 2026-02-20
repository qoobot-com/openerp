<template>
  <t-dialog
    v-model:visible="dialogVisible"
    :header="isEdit ? '编辑角色' : '新增角色'"
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
      <t-form-item label="角色名称" name="name">
        <t-input v-model="formData.name" placeholder="请输入角色名称" />
      </t-form-item>

      <t-form-item label="角色编码" name="code">
        <t-input
          v-model="formData.code"
          placeholder="请输入角色编码"
          :disabled="isEdit"
        />
      </t-form-item>

      <t-form-item label="排序" name="sort">
        <t-input-number v-model="formData.sort" :min="0" />
      </t-form-item>

      <t-form-item label="状态" name="status">
        <t-radio-group v-model="formData.status">
          <t-radio :value="1">正常</t-radio>
          <t-radio :value="0">禁用</t-radio>
        </t-radio-group>
      </t-form-item>

      <t-form-item label="描述" name="description">
        <t-textarea
          v-model="formData.description"
          placeholder="请输入描述"
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
  roleId: string
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

const isEdit = computed(() => !!props.roleId)

const formData = reactive({
  name: '',
  code: '',
  sort: 0,
  status: 1,
  description: '',
})

const rules = {
  name: [{ required: true, message: '请输入角色名称' }],
  code: [
    { required: true, message: '请输入角色编码' },
    {
      pattern: /^[a-zA-Z_][a-zA-Z0-9_]*$/,
      message: '角色编码只能包含字母、数字和下划线，且必须以字母或下划线开头',
    },
  ],
}

const resetForm = () => {
  Object.assign(formData, {
    name: '',
    code: '',
    sort: 0,
    status: 1,
    description: '',
  })
  formRef.value?.reset()
}

const fetchRoleDetail = async (id: string) => {
  Object.assign(formData, {
    name: '超级管理员',
    code: 'super_admin',
    sort: 0,
    status: 1,
    description: '拥有系统所有权限',
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
  () => props.roleId,
  (val) => {
    if (val) {
      fetchRoleDetail(val)
    }
  },
  { immediate: true },
)
</script>
