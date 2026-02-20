<template>
  <t-dialog
    v-model:visible="dialogVisible"
    :header="isEdit ? '编辑菜单' : isAddChild ? '新增子菜单' : '新增菜单'"
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
      <t-form-item label="上级菜单" name="parentId">
        <t-tree-select
          v-model="formData.parentId"
          :data="menuTree"
          placeholder="请选择上级菜单（不选则为顶级菜单）"
          clearable
          filterable
        />
      </t-form-item>

      <t-form-item label="菜单名称" name="name">
        <t-input v-model="formData.name" placeholder="请输入菜单名称" />
      </t-form-item>

      <t-form-item label="菜单类型" name="type">
        <t-radio-group v-model="formData.type">
          <t-radio value="catalog">目录</t-radio>
          <t-radio value="menu">菜单</t-radio>
          <t-radio value="button">按钮</t-radio>
        </t-radio-group>
      </t-form-item>

      <t-form-item label="菜单图标" name="icon">
        <t-input v-model="formData.icon" placeholder="请输入图标名称" />
      </t-form-item>

      <t-form-item v-if="formData.type !== 'button'" label="路由路径" name="path">
        <t-input v-model="formData.path" placeholder="请输入路由路径" />
      </t-form-item>

      <t-form-item v-if="formData.type === 'menu'" label="组件路径" name="component">
        <t-input v-model="formData.component" placeholder="请输入组件路径" />
      </t-form-item>

      <t-form-item v-if="formData.type === 'button'" label="权限标识" name="permission">
        <t-input v-model="formData.permission" placeholder="请输入权限标识，如: system:user:add" />
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

      <t-form-item label="是否显示" name="visible">
        <t-radio-group v-model="formData.visible">
          <t-radio :value="1">显示</t-radio>
          <t-radio :value="0">隐藏</t-radio>
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
  menuId: string
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

const isEdit = computed(() => !!props.menuId)
const isAddChild = computed(() => !!props.parentId)

const formData = reactive({
  parentId: '',
  name: '',
  type: 'menu',
  icon: '',
  path: '',
  component: '',
  permission: '',
  sort: 0,
  status: 1,
  visible: 1,
  remark: '',
})

const rules = {
  name: [{ required: true, message: '请输入菜单名称' }],
  type: [{ required: true, message: '请选择菜单类型' }],
  path: [
    {
      required: computed(() => formData.type !== 'button'),
      message: '请输入路由路径',
      type: 'error',
    },
  ],
  component: [
    {
      required: computed(() => formData.type === 'menu'),
      message: '请输入组件路径',
      type: 'error',
    },
  ],
  permission: [
    {
      required: computed(() => formData.type === 'button'),
      message: '请输入权限标识',
      type: 'error',
    },
  ],
}

// 菜单树数据（用于选择父菜单）
const menuTree = ref([
  {
    value: '1',
    label: '系统管理',
    children: [
      { value: '1-1', label: '用户管理' },
      { value: '1-2', label: '角色管理' },
      { value: '1-3', label: '菜单管理' },
    ],
  },
  {
    value: '2',
    label: '组织架构',
    children: [
      { value: '2-1', label: '部门管理' },
      { value: '2-2', label: '员工管理' },
    ],
  },
])

const resetForm = () => {
  Object.assign(formData, {
    parentId: '',
    name: '',
    type: 'menu',
    icon: '',
    path: '',
    component: '',
    permission: '',
    sort: 0,
    status: 1,
    visible: 1,
    remark: '',
  })
  formRef.value?.reset()
}

const fetchMenuDetail = async (id: string) => {
  Object.assign(formData, {
    parentId: '1',
    name: '用户管理',
    type: 'menu',
    icon: 'user',
    path: '/system/user',
    component: 'system/user/UserList',
    permission: '',
    sort: 1,
    status: 1,
    visible: 1,
    remark: '用户管理菜单',
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
  () => props.menuId,
  (val) => {
    if (val) {
      fetchMenuDetail(val)
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
