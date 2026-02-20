<template>
  <t-dialog
    v-model:visible="dialogVisible"
    :header="isEdit ? '编辑岗位' : '新增岗位'"
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
      <t-form-item label="岗位编码" name="code">
        <t-input
          v-model="formData.code"
          placeholder="请输入岗位编码"
          :disabled="isEdit"
        />
      </t-form-item>

      <t-form-item label="岗位名称" name="name">
        <t-input v-model="formData.name" placeholder="请输入岗位名称" />
      </t-form-item>

      <t-form-item label="所属部门" name="deptId">
        <t-select
          v-model="formData.deptId"
          placeholder="请选择所属部门"
          :options="deptOptions"
        />
      </t-form-item>

      <t-form-item label="岗位级别" name="level">
        <t-select
          v-model="formData.level"
          placeholder="请选择岗位级别"
          :options="levelOptions"
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

      <t-form-item label="岗位描述" name="description">
        <t-textarea
          v-model="formData.description"
          placeholder="请输入岗位描述"
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
  positionId: string
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

const isEdit = computed(() => !!props.positionId)

const formData = reactive({
  code: '',
  name: '',
  deptId: '',
  level: '',
  sort: 0,
  status: 1,
  description: '',
})

const rules = {
  code: [{ required: true, message: '请输入岗位编码' }],
  name: [{ required: true, message: '请输入岗位名称' }],
  deptId: [{ required: true, message: '请选择所属部门' }],
  level: [{ required: true, message: '请选择岗位级别' }],
}

const deptOptions = [
  { label: '技术部', value: '1' },
  { label: '销售部', value: '2' },
  { label: '财务部', value: '3' },
  { label: '人事部', value: '4' },
]

const levelOptions = [
  { label: 'P1 - 实习生', value: 'P1' },
  { label: 'P2 - 初级', value: 'P2' },
  { label: 'P3 - 中级', value: 'P3' },
  { label: 'P4 - 高级', value: 'P4' },
  { label: 'P5 - 资深', value: 'P5' },
  { label: 'P6 - 专家', value: 'P6' },
]

const resetForm = () => {
  Object.assign(formData, {
    code: '',
    name: '',
    deptId: '',
    level: '',
    sort: 0,
    status: 1,
    description: '',
  })
  formRef.value?.reset()
}

const fetchPositionDetail = async (id: string) => {
  Object.assign(formData, {
    code: 'POS001',
    name: '前端工程师',
    deptId: '1',
    level: 'P3',
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
  () => props.positionId,
  (val) => {
    if (val) {
      fetchPositionDetail(val)
    }
  },
  { immediate: true },
)
</script>
