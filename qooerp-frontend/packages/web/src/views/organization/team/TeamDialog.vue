<template>
  <t-dialog
    v-model:visible="dialogVisible"
    :header="isEdit ? '编辑团队' : '新增团队'"
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
      <t-form-item label="团队编码" name="code">
        <t-input
          v-model="formData.code"
          placeholder="请输入团队编码"
          :disabled="isEdit"
        />
      </t-form-item>

      <t-form-item label="团队名称" name="name">
        <t-input v-model="formData.name" placeholder="请输入团队名称" />
      </t-form-item>

      <t-form-item label="所属部门" name="deptId">
        <t-select
          v-model="formData.deptId"
          placeholder="请选择所属部门"
          :options="deptOptions"
        />
      </t-form-item>

      <t-form-item label="团队负责人" name="leaderId">
        <t-select
          v-model="formData.leaderId"
          placeholder="请选择团队负责人"
          filterable
          :options="leaderOptions"
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

      <t-form-item label="团队描述" name="description">
        <t-textarea
          v-model="formData.description"
          placeholder="请输入团队描述"
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
  teamId: string
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

const isEdit = computed(() => !!props.teamId)

const formData = reactive({
  code: '',
  name: '',
  deptId: '',
  leaderId: '',
  sort: 0,
  status: 1,
  description: '',
})

const rules = {
  code: [{ required: true, message: '请输入团队编码' }],
  name: [{ required: true, message: '请输入团队名称' }],
  deptId: [{ required: true, message: '请选择所属部门' }],
  leaderId: [{ required: true, message: '请选择团队负责人' }],
}

const deptOptions = [
  { label: '技术部', value: '1' },
  { label: '销售部', value: '2' },
  { label: '财务部', value: '3' },
  { label: '人事部', value: '4' },
]

const leaderOptions = [
  { label: '张三', value: '1' },
  { label: '李四', value: '2' },
  { label: '王五', value: '3' },
]

const resetForm = () => {
  Object.assign(formData, {
    code: '',
    name: '',
    deptId: '',
    leaderId: '',
    sort: 0,
    status: 1,
    description: '',
  })
  formRef.value?.reset()
}

const fetchTeamDetail = async (id: string) => {
  Object.assign(formData, {
    code: 'TEAM001',
    name: '前端开发组',
    deptId: '1',
    leaderId: '1',
    sort: 1,
    status: 1,
    description: '负责前端开发工作',
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
  () => props.teamId,
  (val) => {
    if (val) {
      fetchTeamDetail(val)
    }
  },
  { immediate: true },
)
</script>
