<template>
  <t-dialog
    v-model:visible="dialogVisible"
    :header="isEdit ? '编辑部门' : isAddChild ? '新增子部门' : '新增部门'"
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
      <t-form-item label="上级部门" name="parentId">
        <t-tree-select
          v-model="formData.parentId"
          :data="deptTree"
          placeholder="请选择上级部门（不选则为顶级部门）"
          clearable
          filterable
        />
      </t-form-item>

      <t-form-item label="部门名称" name="name">
        <t-input v-model="formData.name" placeholder="请输入部门名称" />
      </t-form-item>

      <t-form-item label="部门编码" name="code">
        <t-input v-model="formData.code" placeholder="请输入部门编码" />
      </t-form-item>

      <t-form-item label="负责人" name="leader">
        <t-input v-model="formData.leader" placeholder="请输入负责人" />
      </t-form-item>

      <t-form-item label="联系电话" name="phone">
        <t-input v-model="formData.phone" placeholder="请输入联系电话" />
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
  deptId: string
  parentId: string
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

const isEdit = computed(() => !!props.deptId)
const isAddChild = computed(() => !!props.parentId)

const formData = reactive({
  parentId: '',
  name: '',
  code: '',
  leader: '',
  phone: '',
  sort: 0,
  status: 1,
  remark: '',
})

const rules = {
  name: [{ required: true, message: '请输入部门名称' }],
  code: [{ required: true, message: '请输入部门编码' }],
  leader: [{ required: true, message: '请输入负责人' }],
  phone: [
    { required: true, message: '请输入联系电话' },
    {
      pattern: /^1[3-9]\d{9}$|^0\d{2,3}-?\d{7,8}$/,
      message: '电话格式不正确',
    },
  ],
}

// 部门树数据（用于选择父部门）
const deptTree = ref([
  {
    value: '1',
    label: 'QooERP 科技有限公司',
    children: [
      {
        value: '1-1',
        label: '技术部',
        children: [
          { value: '1-1-1', label: '前端组' },
          { value: '1-1-2', label: '后端组' },
        ],
      },
      { value: '1-2', label: '销售部' },
      { value: '1-3', label: '财务部' },
      { value: '1-4', label: '人事部' },
    ],
  },
])

const resetForm = () => {
  Object.assign(formData, {
    parentId: '',
    name: '',
    code: '',
    leader: '',
    phone: '',
    sort: 0,
    status: 1,
    remark: '',
  })
  formRef.value?.reset()
}

const fetchDeptDetail = async (id: string) => {
  Object.assign(formData, {
    parentId: '1',
    name: '技术部',
    code: 'TECH',
    leader: '李技术',
    phone: '010-12345679',
    sort: 1,
    status: 1,
    remark: '技术部',
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
  () => props.deptId,
  (val) => {
    if (val) {
      fetchDeptDetail(val)
    }
  },
  { immediate: true },
)

watch(
  () => props.parentId,
  (val) => {
    if (val) {
      formData.parentId = val
    }
  },
  { immediate: true },
)
</script>
