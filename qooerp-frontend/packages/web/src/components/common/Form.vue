<template>
  <div class="form-container">
    <t-form
      ref="formRef"
      :data="formData"
      :rules="rules"
      :label-width="labelWidth"
      :label-align="labelAlign"
      :colon="colon"
      :reset-type="resetType"
      @submit="handleSubmit"
      @reset="handleReset"
      @validate="handleValidate"
    >
      <t-form-item
        v-for="item in formItems"
        :key="item.field"
        :name="item.field"
        :label="item.label"
        :required="item.required"
      >
        <!-- 输入框 -->
        <t-input
          v-if="item.type === 'input'"
          v-model="formData[item.field]"
          :placeholder="item.placeholder"
          :disabled="item.disabled"
          :clearable="item.clearable"
        />

        <!-- 文本域 -->
        <t-textarea
          v-else-if="item.type === 'textarea'"
          v-model="formData[item.field]"
          :placeholder="item.placeholder"
          :disabled="item.disabled"
          :autosize="item.autosize || { minRows: 3, maxRows: 6 }"
          :maxlength="item.maxlength"
        />

        <!-- 数字输入框 -->
        <t-input-number
          v-else-if="item.type === 'number'"
          v-model="formData[item.field]"
          :placeholder="item.placeholder"
          :disabled="item.disabled"
          :min="item.min"
          :max="item.max"
          :step="item.step"
        />

        <!-- 选择框 -->
        <t-select
          v-else-if="item.type === 'select'"
          v-model="formData[item.field]"
          :placeholder="item.placeholder"
          :disabled="item.disabled"
          :clearable="item.clearable"
          :multiple="item.multiple"
          :filterable="item.filterable"
        >
          <t-option
            v-for="opt in item.options"
            :key="opt.value"
            :value="opt.value"
            :label="opt.label"
          />
        </t-select>

        <!-- 日期选择器 -->
        <t-date-picker
          v-else-if="item.type === 'date'"
          v-model="formData[item.field]"
          :placeholder="item.placeholder"
          :disabled="item.disabled"
          :clearable="item.clearable"
          :format="item.format || 'YYYY-MM-DD'"
        />

        <!-- 日期范围选择器 -->
        <t-date-range-picker
          v-else-if="item.type === 'daterange'"
          v-model="formData[item.field]"
          :placeholder="item.placeholder"
          :disabled="item.disabled"
          :clearable="item.clearable"
          :format="item.format || 'YYYY-MM-DD'"
        />

        <!-- 时间选择器 -->
        <t-time-picker
          v-else-if="item.type === 'time'"
          v-model="formData[item.field]"
          :placeholder="item.placeholder"
          :disabled="item.disabled"
          :clearable="item.clearable"
          :format="item.format || 'HH:mm:ss'"
        />

        <!-- 开关 -->
        <t-switch
          v-else-if="item.type === 'switch'"
          v-model="formData[item.field]"
          :disabled="item.disabled"
          :size="item.size"
        />

        <!-- 单选框组 -->
        <t-radio-group
          v-else-if="item.type === 'radio'"
          v-model="formData[item.field]"
          :disabled="item.disabled"
        >
          <t-radio
            v-for="opt in item.options"
            :key="opt.value"
            :value="opt.value"
          >
            {{ opt.label }}
          </t-radio>
        </t-radio-group>

        <!-- 复选框组 -->
        <t-checkbox-group
          v-else-if="item.type === 'checkbox'"
          v-model="formData[item.field]"
          :disabled="item.disabled"
        >
          <t-checkbox
            v-for="opt in item.options"
            :key="opt.value"
            :value="opt.value"
          >
            {{ opt.label }}
          </t-checkbox>
        </t-checkbox-group>

        <!-- 级联选择器 -->
        <t-cascader
          v-else-if="item.type === 'cascader'"
          v-model="formData[item.field]"
          :placeholder="item.placeholder"
          :disabled="item.disabled"
          :clearable="item.clearable"
          :options="item.options"
        />

        <!-- 上传组件 -->
        <t-upload
          v-else-if="item.type === 'upload'"
          v-model="formData[item.field]"
          :action="item.action"
          :disabled="item.disabled"
          :multiple="item.multiple"
          :max="item.max"
          :accept="item.accept"
          :request-method="item.requestMethod"
        />

        <!-- 自定义插槽 -->
        <slot v-else :name="item.field" :field="item.field" :value="formData[item.field]" />
      </t-form-item>

      <!-- 表单操作按钮 -->
      <t-form-item v-if="showButtons">
        <t-space>
          <t-button
            v-if="showSubmit"
            theme="primary"
            type="submit"
            :loading="submitLoading"
            :disabled="submitDisabled"
          >
            {{ submitText }}
          </t-button>
          <t-button
            v-if="showReset"
            theme="default"
            type="reset"
            :disabled="resetDisabled"
          >
            {{ resetText }}
          </t-button>
          <slot name="actions" />
        </t-space>
      </t-form-item>
    </t-form>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';
import type { FormInstanceFunctions, FormRule, FormDataContext } from 'tdesign-vue-next';

export interface FormItem {
  field: string;
  label: string;
  type: 'input' | 'textarea' | 'number' | 'select' | 'date' | 'daterange' | 'time' | 'switch' | 'radio' | 'checkbox' | 'cascader' | 'upload' | 'custom';
  placeholder?: string;
  required?: boolean;
  disabled?: boolean;
  clearable?: boolean;
  multiple?: boolean;
  filterable?: boolean;
  options?: Array<{ label: string; value: any }>;
  format?: string;
  min?: number;
  max?: number;
  step?: number;
  size?: 'small' | 'medium' | 'large';
  autosize?: { minRows: number; maxRows: number };
  maxlength?: number;
  action?: string;
  requestMethod?: any;
  accept?: string;
}

interface Props {
  modelValue?: Record<string, any>;
  items: FormItem[];
  rules?: Record<string, FormRule[]>;
  labelWidth?: string | number;
  labelAlign?: 'left' | 'right' | 'top';
  colon?: boolean;
  resetType?: 'empty' | 'initial';
  showButtons?: boolean;
  showSubmit?: boolean;
  showReset?: boolean;
  submitText?: string;
  resetText?: string;
  submitLoading?: boolean;
  submitDisabled?: boolean;
  resetDisabled?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: () => ({}),
  rules: () => ({}),
  labelWidth: '120px',
  labelAlign: 'right',
  colon: true,
  resetType: 'empty',
  showButtons: true,
  showSubmit: true,
  showReset: true,
  submitText: '提交',
  resetText: '重置',
  submitLoading: false,
  submitDisabled: false,
  resetDisabled: false,
});

const emit = defineEmits<{
  'update:modelValue': [value: Record<string, any>];
  'submit': [data: any];
  'reset': [data: any];
  'validate': [result: any];
}>();

const formRef = ref<FormInstanceFunctions>();
const formData = ref<Record<string, any>>({ ...props.modelValue });
const formItems = ref<FormItem[]>(props.items);

// 监听外部数据变化
watch(
  () => props.modelValue,
  (newVal) => {
    formData.value = { ...newVal };
  },
  { deep: true }
);

// 监听 items 变化
watch(
  () => props.items,
  (newVal) => {
    formItems.value = newVal;
  },
  { deep: true }
);

// 监听内部数据变化，同步到外部
watch(
  formData,
  (newVal) => {
    emit('update:modelValue', { ...newVal });
  },
  { deep: true }
);

const handleSubmit = (data: any) => {
  emit('submit', data);
};

const handleReset = (data: any) => {
  emit('reset', data);
};

const handleValidate = (result: any) => {
  emit('validate', result);
};

// 暴露方法
const validate = () => {
  return formRef.value?.validate();
};

const reset = () => {
  return formRef.value?.reset();
};

const setFieldValue = (field: string, value: any) => {
  formData.value[field] = value;
};

const getFieldValue = (field: string) => {
  return formData.value[field];
};

defineExpose({
  validate,
  reset,
  setFieldValue,
  getFieldValue,
  formData,
});
</script>

<style scoped lang="scss">
.form-container {
  :deep(.t-form) {
    .t-form__item {
      margin-bottom: 24px;
    }
  }
}
</style>
