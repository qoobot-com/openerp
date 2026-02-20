<template>
  <t-dialog
    v-model:visible="dialogVisible"
    header="权限配置"
    width="800px"
    :confirm-btn="null"
    @close="handleClose"
  >
    <div class="permission-tree">
      <t-tree
        ref="treeRef"
        v-model:checked="checkedKeys"
        :data="treeData"
        checkable
        :expand-all="true"
        :hover="true"
      />
    </div>

    <template #footer>
      <t-button theme="default" @click="handleClose">取消</t-button>
      <t-button theme="primary" :loading="loading" @click="handleSubmit">确定</t-button>
    </template>
  </t-dialog>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
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

const treeRef = ref()
const loading = ref(false)
const checkedKeys = ref<string[]>([])

const dialogVisible = computed({
  get: () => props.visible,
  set: (val) => emit('update:visible', val),
})

// 模拟权限树数据
const treeData = ref([
  {
    value: 'system',
    label: '系统管理',
    children: [
      {
        value: 'system-user',
        label: '用户管理',
        children: [
          { value: 'system-user-view', label: '查看' },
          { value: 'system-user-add', label: '新增' },
          { value: 'system-user-edit', label: '编辑' },
          { value: 'system-user-delete', label: '删除' },
        ],
      },
      {
        value: 'system-role',
        label: '角色管理',
        children: [
          { value: 'system-role-view', label: '查看' },
          { value: 'system-role-add', label: '新增' },
          { value: 'system-role-edit', label: '编辑' },
          { value: 'system-role-delete', label: '删除' },
          { value: 'system-role-permission', label: '权限配置' },
        ],
      },
      {
        value: 'system-menu',
        label: '菜单管理',
        children: [
          { value: 'system-menu-view', label: '查看' },
          { value: 'system-menu-add', label: '新增' },
          { value: 'system-menu-edit', label: '编辑' },
          { value: 'system-menu-delete', label: '删除' },
        ],
      },
    ],
  },
  {
    value: 'organization',
    label: '组织架构',
    children: [
      {
        value: 'organization-dept',
        label: '部门管理',
        children: [
          { value: 'organization-dept-view', label: '查看' },
          { value: 'organization-dept-add', label: '新增' },
          { value: 'organization-dept-edit', label: '编辑' },
          { value: 'organization-dept-delete', label: '删除' },
        ],
      },
      {
        value: 'organization-employee',
        label: '员工管理',
        children: [
          { value: 'organization-employee-view', label: '查看' },
          { value: 'organization-employee-add', label: '新增' },
          { value: 'organization-employee-edit', label: '编辑' },
          { value: 'organization-employee-delete', label: '删除' },
        ],
      },
    ],
  },
])

const fetchRolePermissions = async (roleId: string) => {
  checkedKeys.value = ['system-user-view', 'system-role-view', 'organization-dept-view']
}

const handleSubmit = async () => {
  loading.value = true
  try {
    await new Promise((resolve) => setTimeout(resolve, 500))
    MessagePlugin.success('权限配置成功')
    emit('success')
  } catch (error) {
    MessagePlugin.error('操作失败')
  } finally {
    loading.value = false
  }
}

const handleClose = () => {
  checkedKeys.value = []
  dialogVisible.value = false
}

// 监听 roleId 变化
import { watch } from 'vue'
watch(
  () => props.roleId,
  (val) => {
    if (val) {
      fetchRolePermissions(val)
    }
  },
  { immediate: true },
)
</script>

<style scoped lang="scss">
.permission-tree {
  max-height: 500px;
  overflow-y: auto;
  padding: 16px 0;
}
</style>
