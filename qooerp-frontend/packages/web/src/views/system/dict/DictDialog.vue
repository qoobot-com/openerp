<template>
  <t-dialog
    v-model:visible="dialogVisible"
    :header="isEdit ? '编辑字典' : '新增字典'"
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
      @submit="handleSubmit"
    >
      <t-form-item label="字典名称" name="dictName">
        <t-input v-model="formData.dictName" placeholder="请输入字典名称" />
      </t-form-item>
      <t-form-item label="字典编码" name="dictCode">
        <t-input v-model="formData.dictCode" placeholder="请输入字典编码" :disabled="isEdit" />
      </t-form-item>
      <t-form-item label="字典类型" name="dictType">
        <t-select v-model="formData.dictType" placeholder="请选择字典类型">
          <t-option value="string" label="字符串" />
          <t-option value="number" label="数字" />
          <t-option value="boolean" label="布尔" />
        </t-select>
      </t-form-item>
      <t-form-item label="状态" name="status">
        <t-radio-group v-model="formData.status">
          <t-radio value="1">启用</t-radio>
          <t-radio value="0">禁用</t-radio>
        </t-radio-group>
      </t-form-item>
      <t-form-item label="备注" name="remark">
        <t-textarea v-model="formData.remark" placeholder="请输入备注" :maxlength="200" />
      </t-form-item>
    </t-form>
  </t-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch } from 'vue';
import { MessagePlugin } from 'tdesign-vue-next';

const props = defineProps<{
  visible: boolean;
  dictData?: any;
}>();

const emit = defineEmits<{
  'update:visible': [value: boolean];
  'success': [];
}>();

const dialogVisible = computed({
  get: () => props.visible,
  set: (value) => emit('update:visible', value),
});

const isEdit = computed(() => !!props.dictData?.id);

const formRef = ref();
const submitting = ref(false);

const formData = reactive({
  id: undefined,
  dictName: '',
  dictCode: '',
  dictType: 'string',
  status: '1',
  remark: '',
});

const rules = {
  dictName: [{ required: true, message: '请输入字典名称', type: 'error' }],
  dictCode: [{ required: true, message: '请输入字典编码', type: 'error' }],
  dictType: [{ required: true, message: '请选择字典类型', type: 'error' }],
};

watch(
  () => props.dictData,
  (data) => {
    if (data) {
      Object.assign(formData, data);
    } else {
      Object.assign(formData, {
        id: undefined,
        dictName: '',
        dictCode: '',
        dictType: 'string',
        status: '1',
        remark: '',
      });
    }
  },
  { immediate: true }
);

const handleSubmit = async () => {
  const valid = await formRef.value?.validate();
  if (!valid) return;

  submitting.value = true;
  try {
    await new Promise(resolve => setTimeout(resolve, 500));
    MessagePlugin.success(isEdit.value ? '更新成功' : '创建成功');
    emit('success');
    dialogVisible.value = false;
  } catch (error) {
    MessagePlugin.error('操作失败');
  } finally {
    submitting.value = false;
  }
};
</script>
