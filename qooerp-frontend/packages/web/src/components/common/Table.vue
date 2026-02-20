<template>
  <div class="q-table">
    <!-- 搜索卡片 -->
    <search-card
      v-if="searchConfig"
      :config="searchConfig"
      @search="handleSearch"
      @reset="handleReset"
    />

    <!-- 工具栏 -->
    <div v-if="toolbar" class="table-toolbar">
      <slot name="toolbar-left">
        <t-button
          v-for="btn in toolbar"
          :key="btn.key"
          :theme="btn.theme || 'primary'"
          :variant="btn.variant || 'base'"
          :disabled="btn.disabled"
          @click="handleToolbarClick(btn)"
        >
          <template v-if="btn.icon" #icon>
            <t-icon :name="btn.icon" />
          </template>
          {{ btn.label }}
        </t-button>
      </slot>
      <slot name="toolbar-right" />
    </div>

    <!-- 表格 -->
    <t-table
      :data="tableData"
      :columns="columns"
      :loading="loading"
      :pagination="paginationConfig"
      :row-key="rowKey"
      :selected-row-keys="selectedKeys"
      :bordered="bordered"
      :stripe="stripe"
      :hover="hover"
      :size="size"
      :max-height="maxHeight"
      @select-change="handleSelectChange"
      @page-change="handlePageChange"
    >
      <!-- 自定义列插槽 -->
      <template
        v-for="col in customColumns"
        #[col.slotName]="{ row, rowIndex }"
        :key="col.colKey"
      >
        <slot :name="col.slotName" :row="row" :rowIndex="rowIndex" />
      </template>
    </t-table>

    <!-- 空状态 -->
    <t-empty
      v-if="!loading && tableData.length === 0"
      description="暂无数据"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import type { TableProps } from 'tdesign-vue-next'

// Props 定义
interface Column {
  colKey: string
  title: string
  width?: number
  align?: 'left' | 'center' | 'right'
  fixed?: 'left' | 'right'
  sortType?: 'all' | 'single' | 'none'
  sorter?: boolean | ((a: any, b: any) => number)
  filter?: {
    type: 'single' | 'multiple'
    list: Array<{ label: string; value: any }>
    confirmEvents?: Array<'confirm' | 'reset'>
  }
  ellipsis?: boolean
  slotName?: string
}

interface ToolbarButton {
  key: string
  label: string
  icon?: string
  theme?: 'primary' | 'default' | 'danger' | 'warning'
  variant?: 'base' | 'outline' | 'dashed' | 'text'
  disabled?: boolean
  permission?: string
}

interface PaginationConfig {
  current?: number
  pageSize?: number
  total?: number
  showSizeChanger?: boolean
  showJumper?: boolean
  pageSizeOptions?: number[]
}

interface SearchFormItem {
  name: string
  label: string
  type: 'input' | 'select' | 'date' | 'daterange' | 'number'
  placeholder?: string
  options?: Array<{ label: string; value: any }>
  defaultValue?: any
}

interface Props {
  // 表格配置
  columns: Column[]
  data: any[]
  loading?: boolean
  rowKey?: string
  bordered?: boolean
  stripe?: boolean
  hover?: boolean
  size?: 'small' | 'medium' | 'large'
  maxHeight?: number

  // 工具栏
  toolbar?: ToolbarButton[]

  // 搜索配置
  searchConfig?: SearchFormItem[]

  // 分页
  pagination?: PaginationConfig | false

  // 选中
  multiple?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  loading: false,
  rowKey: 'id',
  bordered: true,
  stripe: true,
  hover: true,
  size: 'medium',
  multiple: false,
  pagination: () => ({
    current: 1,
    pageSize: 10,
    showSizeChanger: true,
    showJumper: true,
    pageSizeOptions: [10, 20, 50, 100],
  }),
})

// Emits
const emit = defineEmits<{
  search: [params: Record<string, any>]
  reset: []
  refresh: []
  toolbarClick: [key: string]
  selectChange: [selectedKeys: (string | number)[]]
  pageChange: [pageInfo: { current: number; pageSize: number }]
}>()

// 状态
const tableData = ref<any[]>([])
const selectedKeys = ref<(string | number)[]>([])

// 计算属性
const customColumns = computed(() =>
  props.columns.filter((col) => col.slotName)
)

const paginationConfig = computed(() => {
  if (props.pagination === false) return undefined
  return {
    ...props.pagination,
    onChange: (pageInfo: { current: number; pageSize: number }) => {
      emit('pageChange', pageInfo)
    },
  }
})

// 监听数据变化
watch(
  () => props.data,
  (newData) => {
    tableData.value = newData
  },
  { immediate: true, deep: true }
)

// 方法
const handleSearch = (params: Record<string, any>) => {
  emit('search', params)
}

const handleReset = () => {
  emit('reset')
}

const handleToolbarClick = (btn: ToolbarButton) => {
  emit('toolbarClick', btn.key)
}

const handleSelectChange = (value: (string | number)[]) => {
  selectedKeys.value = value
  emit('selectChange', value)
}

const handlePageChange = (pageInfo: { current: number; pageSize: number }) => {
  emit('pageChange', pageInfo)
}

// 暴露给父组件
defineExpose({
  refresh: () => emit('refresh'),
  clearSelection: () => {
    selectedKeys.value = []
  },
})
</script>

<style scoped lang="scss">
.q-table {
  .table-toolbar {
    margin-bottom: 16px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 12px;
  }

  :deep(.t-table) {
    border-radius: 4px;
    overflow: hidden;
  }
}
</style>
