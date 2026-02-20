<template>
  <t-card class="search-card" :class="{ collapsed: isCollapsed }">
    <t-form
      ref="formRef"
      :data="formData"
      :label-width="labelWidth"
      colon
      @submit="handleSubmit"
      @reset="handleReset"
    >
      <t-row :gutter="[16, 16]">
        <template v-for="(item, index) in visibleItems" :key="item.name">
          <t-col
            :span="item.span || defaultSpan"
            v-show="index < maxShow || !isCollapsed"
          >
            <t-form-item :name="item.name" :label="item.label">
              <!-- 输入框 -->
              <t-input
                v-if="item.type === 'input'"
                v-model="formData[item.name]"
                :placeholder="item.placeholder || '请输入'"
                clearable
              />

              <!-- 数字输入 -->
              <t-input-number
                v-else-if="item.type === 'number'"
                v-model="formData[item.name]"
                :placeholder="item.placeholder || '请输入'"
                style="width:100%"
              />

              <!-- 下拉选择 -->
              <t-select
                v-else-if="item.type === 'select'"
                v-model="formData[item.name]"
                :placeholder="item.placeholder || '请选择'"
                clearable
                filterable
                style="width:100%"
              >
                <t-option
                  v-for="opt in item.options"
                  :key="opt.value"
                  :value="opt.value"
                  :label="opt.label"
                />
              </t-select>

              <!-- 日期选择 -->
              <t-date-picker
                v-else-if="item.type === 'date'"
                v-model="formData[item.name]"
                :placeholder="item.placeholder || '请选择日期'"
                clearable
                style="width:100%"
              />

              <!-- 日期范围 -->
              <t-date-range-picker
                v-else-if="item.type === 'daterange'"
                v-model="formData[item.name]"
                :placeholder="['开始日期', '结束日期']"
                clearable
                style="width:100%"
              />
            </t-form-item>
          </t-col>
        </template>

        <!-- 操作按钮 -->
        <t-col v-if="showActions" :span="24" class="search-actions">
          <t-button theme="primary" type="submit">查询</t-button>
          <t-button theme="default" type="reset" style="margin-left:8px">重置</t-button>
          <t-button
            v-if="config.length > maxShow"
            theme="default"
            variant="text"
            @click="toggleCollapse"
            style="margin-left:8px"
          >
            {{ isCollapsed ? '展开' : '收起' }}
            <template #icon>
              <t-icon :name="isCollapsed ? 'chevron-down' : 'chevron-up'" />
            </template>
          </t-button>
        </t-col>
      </t-row>
    </t-form>
  </t-card>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import type { FormInstanceFunctions } from 'tdesign-vue-next'

// Props
interface SearchFormItem {
  name: string
  label: string
  type: 'input' | 'select' | 'date' | 'daterange' | 'number'
  placeholder?: string
  span?: number
  options?: Array<{ label: string; value: any }>
  defaultValue?: any
}

interface Props {
  config: SearchFormItem[]
  labelWidth?: string
  maxShow?: number
  defaultSpan?: number
  showActions?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  labelWidth: '100px',
  maxShow: 3,
  defaultSpan: 6,
  showActions: true,
})

// Emits
const emit = defineEmits<{
  search: [params: Record<string, any>]
  reset: []
}>()

// 状态
const formRef = ref<FormInstanceFunctions>()
const formData = ref<Record<string, any>>({})
const isCollapsed = ref(true)

// 计算属性
const visibleItems = computed(() => props.config)

// 初始化表单数据
const initFormData = () => {
  props.config.forEach((item) => {
    if (item.defaultValue !== undefined) {
      formData.value[item.name] = item.defaultValue
    } else {
      formData.value[item.name] = undefined
    }
  })
}

// 展开/收起
const toggleCollapse = () => {
  isCollapsed.value = !isCollapsed.value
}

// 提交搜索
const handleSubmit = () => {
  emit('search', { ...formData.value })
}

// 重置
const handleReset = () => {
  initFormData()
  emit('reset')
}

// 暴露方法
defineExpose({
  getFormData: () => formData.value,
  setFormData: (data: Record<string, any>) => {
    formData.value = { ...data }
  },
  reset: () => {
    handleReset()
  },
})

// 初始化
onMounted(() => {
  initFormData()
})
</script>

<style scoped lang="scss">
.search-card {
  margin-bottom: 16px;

  :deep(.t-card__body) {
    padding-bottom: 0;
  }

  .search-actions {
    text-align: center;
    padding: 8px 0;
  }
}
</style>
