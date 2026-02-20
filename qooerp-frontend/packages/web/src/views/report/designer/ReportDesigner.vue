<template>
  <div class="report-designer">
    <t-card title="报表设计器" :bordered="false">
      <template #actions>
        <t-space>
          <t-button theme="default" @click="handleSave">保存</t-button>
          <t-button theme="default" @click="handlePreview">预览</t-button>
          <t-button theme="primary" @click="handlePublish">发布</t-button>
        </t-space>
      </template>

      <!-- 报表基本信息 -->
      <div class="report-info">
        <t-form label-width="100px">
          <t-row :gutter="16">
            <t-col :span="8">
              <t-form-item label="报表名称">
                <t-input v-model="reportInfo.name" placeholder="请输入报表名称" />
              </t-form-item>
            </t-col>
            <t-col :span="8">
              <t-form-item label="报表编码">
                <t-input v-model="reportInfo.code" placeholder="请输入报表编码" />
              </t-form-item>
            </t-col>
            <t-col :span="8">
              <t-form-item label="报表分类">
                <t-select v-model="reportInfo.category" placeholder="请选择分类">
                  <t-option value="business" label="业务报表" />
                  <t-option value="finance" label="财务报表" />
                  <t-option value="sales" label="销售报表" />
                  <t-option value="hr" label="人事报表" />
                </t-select>
              </t-form-item>
            </t-col>
          </t-row>
          <t-form-item label="报表描述">
            <t-textarea v-model="reportInfo.description" placeholder="请输入报表描述" :autosize="{ minRows: 2 }" />
          </t-form-item>
        </t-form>
      </div>

      <!-- 设计区域 -->
      <div class="design-area">
        <!-- 组件库 -->
        <div class="component-library">
          <div class="library-title">
            <folder-icon />
            组件库
          </div>
          <div class="library-tabs">
            <t-tabs v-model="activeTab">
              <t-tab-panel value="chart" label="图表组件">
                <div class="component-list">
                  <div
                    v-for="item in chartComponents"
                    :key="item.type"
                    class="component-item"
                    draggable="true"
                    @dragstart="handleDragStart(item)"
                  >
                    <component :is="item.icon" />
                    <span>{{ item.label }}</span>
                  </div>
                </div>
              </t-tab-panel>
              <t-tab-panel value="table" label="表格组件">
                <div class="component-list">
                  <div
                    v-for="item in tableComponents"
                    :key="item.type"
                    class="component-item"
                    draggable="true"
                    @dragstart="handleDragStart(item)"
                  >
                    <component :is="item.icon" />
                    <span>{{ item.label }}</span>
                  </div>
                </div>
              </t-tab-panel>
              <t-tab-panel value="text" label="文本组件">
                <div class="component-list">
                  <div
                    v-for="item in textComponents"
                    :key="item.type"
                    class="component-item"
                    draggable="true"
                    @dragstart="handleDragStart(item)"
                  >
                    <component :is="item.icon" />
                    <span>{{ item.label }}</span>
                  </div>
                </div>
              </t-tab-panel>
            </t-tabs>
          </div>
        </div>

        <!-- 画布 -->
        <div class="design-canvas" @drop="handleDrop" @dragover.prevent>
          <div class="canvas-toolbar">
            <t-space>
              <t-button size="small" variant="outline" @click="handleUndo">
                <template #icon><rollback-icon /></template>
                撤销
              </t-button>
              <t-button size="small" variant="outline" @click="handleRedo">
                <template #icon><rollback-icon /></template>
                重做
              </t-button>
              <t-button size="small" variant="outline" @click="handleClear">清空</t-button>
            </t-space>
          </div>
          <div class="canvas-content">
            <div v-if="canvasComponents.length === 0" class="empty-canvas">
              <folder-icon />
              <p>拖拽组件到此处</p>
            </div>
            <div
              v-for="(item, index) in canvasComponents"
              :key="item.id"
              class="canvas-component"
              :class="{ active: selectedComponent?.id === item.id }"
              @click="handleSelectComponent(item)"
            >
              <div class="component-header">
                <span>{{ item.label }}</span>
                <t-button size="small" variant="text" @click.stop="handleRemoveComponent(index)">
                  <close-icon />
                </t-button>
              </div>
              <div class="component-body">
                <div v-if="item.type === 'line'" class="chart-placeholder">折线图区域</div>
                <div v-else-if="item.type === 'bar'" class="chart-placeholder">柱状图区域</div>
                <div v-else-if="item.type === 'pie'" class="chart-placeholder">饼图区域</div>
                <div v-else-if="item.type === 'table'" class="table-placeholder">数据表格区域</div>
                <div v-else-if="item.type === 'text'" class="text-placeholder">文本内容区域</div>
              </div>
            </div>
          </div>
        </div>

        <!-- 属性面板 -->
        <div class="property-panel">
          <div class="panel-title">
            <setting-icon />
            属性配置
          </div>
          <div class="panel-content">
            <div v-if="selectedComponent" class="property-form">
              <t-form label-width="80px">
                <t-form-item label="组件名称">
                  <t-input v-model="selectedComponent.label" />
                </t-form-item>
                <t-form-item label="数据源">
                  <t-select v-model="selectedComponent.dataSource" placeholder="请选择数据源">
                    <t-option value="api" label="API接口" />
                    <t-option value="database" label="数据库" />
                    <t-option value="static" label="静态数据" />
                  </t-select>
                </t-form-item>
                <t-form-item label="宽度">
                  <t-input-number v-model="selectedComponent.width" :min="1" :max="12" />
                </t-form-item>
                <t-form-item label="高度">
                  <t-input-number v-model="selectedComponent.height" :min="200" :max="800" unit="px" />
                </t-form-item>
              </t-form>
            </div>
            <div v-else class="empty-tip">请选择要配置的组件</div>
          </div>
        </div>
      </div>
    </t-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { MessagePlugin } from 'tdesign-vue-next'
import {
  FolderIcon, SettingIcon, RollbackIcon, CloseIcon, ChartLineIcon, ChartBarIcon, ChartPieIcon,
  TableIcon, TextIcon, FilterIcon,
} from 'tdesign-icons-vue-next'

const activeTab = ref('chart')

const reportInfo = ref({
  name: '销售业绩报表',
  code: 'sales_report',
  category: 'sales',
  description: '月度销售业绩统计分析报表',
})

const chartComponents = [
  { type: 'line', label: '折线图', icon: ChartLineIcon },
  { type: 'bar', label: '柱状图', icon: ChartBarIcon },
  { type: 'pie', label: '饼图', icon: ChartPieIcon },
]

const tableComponents = [
  { type: 'table', label: '数据表格', icon: TableIcon },
  { type: 'pivot', label: '透视表', icon: TableIcon },
]

const textComponents = [
  { type: 'text', label: '文本', icon: TextIcon },
  { type: 'filter', label: '筛选器', icon: FilterIcon },
]

const canvasComponents = ref<any[]>([])
const selectedComponent = ref<any>(null)
let draggedItem: any = null

const handleDragStart = (item: any) => {
  draggedItem = item
}

const handleDrop = (e: DragEvent) => {
  e.preventDefault()
  if (draggedItem) {
    canvasComponents.value.push({
      ...draggedItem,
      id: Date.now(),
      width: 12,
      height: 300,
      dataSource: 'api',
    })
    draggedItem = null
  }
}

const handleSelectComponent = (item: any) => {
  selectedComponent.value = item
}

const handleRemoveComponent = (index: number) => {
  canvasComponents.value.splice(index, 1)
  if (selectedComponent.value === canvasComponents.value[index]) {
    selectedComponent.value = null
  }
}

const handleSave = () => {
  MessagePlugin.success('保存成功')
}

const handlePreview = () => {
  MessagePlugin.info('预览模式')
}

const handlePublish = () => {
  MessagePlugin.success('发布成功')
}

const handleUndo = () => {
  MessagePlugin.info('撤销')
}

const handleRedo = () => {
  MessagePlugin.info('重做')
}

const handleClear = () => {
  canvasComponents.value = []
  selectedComponent.value = null
  MessagePlugin.success('清空画布')
}
</script>

<style scoped lang="scss">
.report-designer {
  .report-info {
    margin-bottom: 16px;
  }

  .design-area {
    display: grid;
    grid-template-columns: 240px 1fr 280px;
    gap: 16px;
    min-height: 600px;

    .component-library,
    .property-panel {
      background: var(--td-bg-color-container-hover);
      border-radius: 4px;
      padding: 16px;

      .library-title,
      .panel-title {
        display: flex;
        align-items: center;
        gap: 8px;
        font-weight: 500;
        margin-bottom: 16px;
        padding-bottom: 12px;
        border-bottom: 1px solid var(--td-component-border);
      }
    }

    .component-library {
      .component-list {
        display: flex;
        flex-direction: column;
        gap: 8px;

        .component-item {
          display: flex;
          align-items: center;
          gap: 8px;
          padding: 10px 12px;
          background: white;
          border: 1px solid var(--td-component-border);
          border-radius: 4px;
          cursor: move;

          &:hover {
            border-color: var(--td-brand-color);
          }
        }
      }
    }

    .design-canvas {
      background: white;
      border: 1px dashed var(--td-component-border);
      border-radius: 4px;

      .canvas-toolbar {
        padding: 12px;
        border-bottom: 1px solid var(--td-component-border);
      }

      .canvas-content {
        min-height: 500px;
        padding: 20px;

        .empty-canvas {
          display: flex;
          flex-direction: column;
          align-items: center;
          justify-content: center;
          height: 460px;
          color: var(--td-text-color-placeholder);

          :deep(.t-icon) {
            font-size: 64px;
            margin-bottom: 16px;
          }
        }

        .canvas-component {
          margin-bottom: 16px;
          padding: 12px;
          border: 2px dashed transparent;
          border-radius: 4px;
          background: var(--td-bg-color-container);

          &:hover {
            border-color: var(--td-brand-color);
          }

          &.active {
            border-color: var(--td-brand-color);
            background: var(--td-brand-color-light);
          }

          .component-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 8px;
            font-weight: 500;
          }

          .component-body {
            .chart-placeholder,
            .table-placeholder,
            .text-placeholder {
              min-height: 200px;
              display: flex;
              align-items: center;
              justify-content: center;
              background: var(--td-bg-color-page);
              border-radius: 4px;
              color: var(--td-text-color-placeholder);
            }
          }
        }
      }
    }

    .property-panel {
      .panel-content {
        .empty-tip {
          text-align: center;
          color: var(--td-text-color-placeholder);
          margin-top: 40px;
        }
      }
    }
  }
}
</style>
