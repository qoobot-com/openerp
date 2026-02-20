<template>
  <t-dialog
    v-model:visible="dialogVisible"
    :header="`${dictName} - 数据项`"
    width="900px"
    :footer="false"
  >
    <div class="dict-data-dialog">
      <!-- 操作栏 -->
      <div class="action-bar">
        <t-space>
          <t-button theme="primary" @click="handleAdd">
            <template #icon><t-icon name="add" /></template>
            新增数据项
          </t-button>
          <t-button theme="danger" :disabled="!selectedRowKeys.length" @click="handleBatchDelete">
            <template #icon><t-icon name="delete" /></template>
            批量删除
          </t-button>
        </t-space>
      </div>

      <!-- 数据项表格 -->
      <t-table
        :data="tableData"
        :columns="columns"
        :selected-row-keys="selectedRowKeys"
        :loading="loading"
        :pagination="pagination"
        row-key="id"
        @select-change="handleSelectChange"
        @page-change="handlePageChange"
      >
        <template #status="{ row }">
          <t-tag v-if="row.status === '1'" theme="success" variant="light">启用</t-tag>
          <t-tag v-else theme="danger" variant="light">禁用</t-tag>
        </template>
        <template #isDefault="{ row }">
          <t-tag v-if="row.isDefault === '1'" theme="primary" variant="light">默认</t-tag>
          <t-tag v-else theme="default" variant="light">普通</t-tag>
        </template>
        <template #operation="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleEdit(row)">编辑</t-link>
            <t-popconfirm content="确认删除该数据项吗？" @confirm="handleDelete(row.id)">
              <t-link theme="danger">删除</t-link>
            </t-popconfirm>
          </t-space>
        </template>
      </t-table>

      <!-- 数据项编辑弹窗 -->
      <t-dialog
        v-model:visible="editDialogVisible"
        :header="isEdit ? '编辑数据项' : '新增数据项'"
        width="600px"
        :confirm-btn="{
          content: '确定',
          theme: 'primary',
          loading: submitting,
        }"
        @confirm="handleSubmit"
      >
        <t-form
          ref="formRef"
          :data="formData"
          :rules="rules"
          label-align="right"
        >
          <t-form-item label="数据标签" name="label">
            <t-input v-model="formData.label" placeholder="请输入数据标签" />
          </t-form-item>
          <t-form-item label="数据值" name="value">
            <t-input v-model="formData.value" placeholder="请输入数据值" />
          </t-form-item>
          <t-form-item label="排序" name="sort">
            <t-input-number v-model="formData.sort" :min="0" placeholder="请输入排序" />
          </t-form-item>
          <t-form-item label="状态" name="status">
            <t-radio-group v-model="formData.status">
              <t-radio value="1">启用</t-radio>
              <t-radio value="0">禁用</t-radio>
            </t-radio-group>
          </t-form-item>
          <t-form-item label="是否默认" name="isDefault">
            <t-radio-group v-model="formData.isDefault">
              <t-radio value="1">是</t-radio>
              <t-radio value="0">否</t-radio>
            </t-radio-group>
          </t-form-item>
          <t-form-item label="备注" name="remark">
            <t-textarea v-model="formData.remark" placeholder="请输入备注" :maxlength="200" />
          </t-form-item>
        </t-form>
      </t-dialog>
    </div>
  </t-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch } from 'vue';
import { MessagePlugin } from 'tdesign-vue-next';

const props = defineProps<{
  visible: boolean;
  dictId: number | null;
  dictName: string;
}>();

const emit = defineEmits<{
  'update:visible': [value: boolean];
}>();

const dialogVisible = computed({
  get: () => props.visible,
  set: (value) => emit('update:visible', value),
});

// 表格数据
const tableData = ref([]);
const loading = ref(false);
const selectedRowKeys = ref([]);

// 分页
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
});

// 编辑弹窗
const editDialogVisible = ref(false);
const isEdit = ref(false);
const formRef = ref();
const submitting = ref(false);

const formData = reactive({
  id: undefined,
  label: '',
  value: '',
  sort: 0,
  status: '1',
  isDefault: '0',
  remark: '',
});

const rules = {
  label: [{ required: true, message: '请输入数据标签', type: 'error' }],
  value: [{ required: true, message: '请输入数据值', type: 'error' }],
};

// 表格列
const columns = [
  { colKey: 'row-select', type: 'multiple', width: 50 },
  { colKey: 'label', title: '数据标签', width: 150 },
  { colKey: 'value', title: '数据值', width: 150 },
  { colKey: 'sort', title: '排序', width: 100 },
  { colKey: 'status', title: '状态', width: 100 },
  { colKey: 'isDefault', title: '是否默认', width: 100 },
  { colKey: 'remark', title: '备注', ellipsis: true },
  { colKey: 'operation', title: '操作', width: 150, fixed: 'right' },
];

// 加载数据
const loadData = async () => {
  if (!props.dictId) return;
  
  loading.value = true;
  try {
    await new Promise(resolve => setTimeout(resolve, 300));
    const mockData = [
      { id: 1, label: '男', value: '1', sort: 1, status: '1', isDefault: '0', remark: '' },
      { id: 2, label: '女', value: '2', sort: 2, status: '1', isDefault: '0', remark: '' },
      { id: 3, label: '未知', value: '0', sort: 0, status: '1', isDefault: '1', remark: '' },
    ];
    tableData.value = mockData;
    pagination.total = mockData.length;
  } catch (error) {
    MessagePlugin.error('加载数据失败');
  } finally {
    loading.value = false;
  }
};

// 新增
const handleAdd = () => {
  isEdit.value = false;
  Object.assign(formData, {
    id: undefined,
    label: '',
    value: '',
    sort: 0,
    status: '1',
    isDefault: '0',
    remark: '',
  });
  editDialogVisible.value = true;
};

// 编辑
const handleEdit = (row: any) => {
  isEdit.value = true;
  Object.assign(formData, row);
  editDialogVisible.value = true;
};

// 删除
const handleDelete = async (id: number) => {
  try {
    await new Promise(resolve => setTimeout(resolve, 300));
    MessagePlugin.success('删除成功');
    loadData();
  } catch (error) {
    MessagePlugin.error('删除失败');
  }
};

// 批量删除
const handleBatchDelete = async () => {
  try {
    await new Promise(resolve => setTimeout(resolve, 300));
    MessagePlugin.success(`成功删除 ${selectedRowKeys.value.length} 条数据`);
    selectedRowKeys.value = [];
    loadData();
  } catch (error) {
    MessagePlugin.error('删除失败');
  }
};

// 提交
const handleSubmit = async () => {
  const valid = await formRef.value?.validate();
  if (!valid) return;

  submitting.value = true;
  try {
    await new Promise(resolve => setTimeout(resolve, 500));
    MessagePlugin.success(isEdit.value ? '更新成功' : '创建成功');
    editDialogVisible.value = false;
    loadData();
  } catch (error) {
    MessagePlugin.error('操作失败');
  } finally {
    submitting.value = false;
  }
};

// 选择变化
const handleSelectChange = (value: number[]) => {
  selectedRowKeys.value = value;
};

// 分页变化
const handlePageChange = (pageInfo: any) => {
  pagination.current = pageInfo.current;
  pagination.pageSize = pageInfo.pageSize;
  loadData();
};

watch(() => props.visible, (val) => {
  if (val) {
    loadData();
  }
});
</script>

<style lang="scss" scoped>
.dict-data-dialog {
  .action-bar {
    margin-bottom: 16px;
  }
}
</style>
