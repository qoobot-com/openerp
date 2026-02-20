<template>
  <div class="form-design">
    <t-card title="表单设计器" :bordered="false">
      <template #actions>
        <t-space>
          <t-button theme="default" @click="handleSave">保存</t-button>
          <t-button theme="default" @click="handlePreview">预览</t-button>
          <t-button theme="primary" @click="handlePublish">发布</t-button>
        </t-space>
      </template>

      <!-- 表单基本信息 -->
      <div class="form-info">
        <t-form label-width="100px">
          <t-row :gutter="16">
            <t-col :span="8">
              <t-form-item label="表单名称">
                <t-input v-model="formInfo.name" placeholder="请输入表单名称" />
              </t-form-item>
            </t-col>
            <t-col :span="8">
              <t-form-item label="表单编码">
                <t-input v-model="formInfo.code" placeholder="请输入表单编码" />
              </t-form-item>
            </t-col>
            <t-col :span="8">
              <t-form-item label="表单分类">
                <t-select v-model="formInfo.category" placeholder="请选择分类">
                  <t-option value="business" label="业务表单" />
                  <t-option value="approval" label="审批表单" />
                  <t-option value="system" label="系统表单" />
                </t-select>
              </t-form-item>
            </t-col>
          </t-row>
          <t-form-item label="表单描述">
            <t-textarea v-model="formInfo.description" placeholder="请输入表单描述" :autosize="{ minRows: 2 }" />
          </t-form-item>
        </t-form>
      </div>

      <!-- 设计区域 -->
      <div class="design-area">
        <div class="component-list">
          <div class="list-title">
            <folder-icon />
            表单组件
          </div>
          <draggable
            class="drag-list"
            :list="components"
            :group="{ name: 'form-design', pull: 'clone', put: false }"
            :clone="cloneComponent"
            item-key="type"
          >
            <template #item="{ element }">
              <div class="component-item">
                <component :is="element.icon" />
                <span>{{ element.label }}</span>
              </div>
            </template>
          </draggable>
        </div>

        <div class="form-canvas">
          <div class="canvas-title">
            <edit-icon />
            设计画布
          </div>
          <div class="canvas-content">
            <draggable
              v-model="formFields"
              group="form-design"
              item-key="id"
              @change="handleDragChange"
            >
              <template #item="{ element }">
                <div class="form-field-item" :class="{ active: activeField?.id === element.id }" @click="handleSelectField(element)">
                  <t-form-item :label="element.label" required>
                    <component :is="getComponent(element.type)" v-model="element.value" v-bind="element.props" />
                  </t-form-item>
                </div>
              </template>
            </draggable>
          </div>
        </div>

        <div class="property-panel">
          <div class="panel-title">
            <setting-icon />
            属性配置
          </div>
          <div class="panel-content">
            <t-form v-if="activeField" label-width="80px">
              <t-form-item label="字段标签">
                <t-input v-model="activeField.label" />
              </t-form-item>
              <t-form-item label="字段名称">
                <t-input v-model="activeField.name" />
              </t-form-item>
              <t-form-item label="默认值">
                <t-input v-model="activeField.defaultValue" />
              </t-form-item>
              <t-form-item label="占位符">
                <t-input v-model="activeField.placeholder" />
              </t-form-item>
              <t-form-item label="必填">
                <t-switch v-model="activeField.required" />
              </t-form-item>
              <t-form-item label="提示信息">
                <t-input v-model="activeField.tip" />
              </t-form-item>
            </t-form>
            <div v-else class="empty-tip">请选择要配置的表单字段</div>
          </div>
        </div>
      </div>
    </t-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { MessagePlugin } from 'tdesign-vue-next'
import draggable from 'vuedraggable'
import {
  FolderIcon, EditIcon, SettingIcon, TextIcon, InputNumberIcon, CalendarIcon,
  TimeIcon, CheckCircleIcon, TextareaIcon, SelectIcon, UploadIcon, DateRangeIcon,
} from 'tdesign-icons-vue-next'

const formInfo = reactive({
  name: '请假申请表',
  code: 'leave_request',
  category: 'approval',
  description: '员工请假申请审批表单',
})

const components = [
  { type: 'input', label: '单行文本', icon: TextIcon },
  { type: 'number', label: '数字输入', icon: InputNumberIcon },
  { type: 'date', label: '日期选择', icon: CalendarIcon },
  { type: 'time', label: '时间选择', icon: TimeIcon },
  { type: 'radio', label: '单选框', icon: CheckCircleIcon },
  { type: 'textarea', label: '多行文本', icon: TextareaIcon },
  { type: 'select', label: '下拉选择', icon: SelectIcon },
  { type: 'upload', label: '文件上传', icon: UploadIcon },
  { type: 'daterange', label: '日期范围', icon: DateRangeIcon },
]

const formFields = ref([
  { id: 1, type: 'input', label: '申请人', name: 'applicant', value: '', required: true, placeholder: '', tip: '' },
  { id: 2, type: 'input', label: '部门', name: 'department', value: '', required: true, placeholder: '', tip: '' },
  { id: 3, type: 'select', label: '请假类型', name: 'leaveType', value: '', required: true, placeholder: '请选择', tip: '', props: { options: [{ label: '年假', value: 'annual' }, { label: '事假', value: 'personal' }] } },
  { id: 4, type: 'number', label: '请假天数', name: 'leaveDays', value: '', required: true, placeholder: '请输入天数', tip: '', props: { min: 0.5, max: 30, step: 0.5 } },
  { id: 5, type: 'daterange', label: '请假时间', name: 'leaveDate', value: '', required: true, placeholder: '请选择日期范围', tip: '' },
  { id: 6, type: 'textarea', label: '请假原因', name: 'leaveReason', value: '', required: true, placeholder: '请输入请假原因', tip: '', props: { autosize: { minRows: 3 } } },
])

const activeField = ref<any>(null)

const cloneComponent = (item: any) => ({
  ...item,
  id: Date.now(),
  name: `field_${Date.now()}`,
  value: '',
  required: false,
  placeholder: '',
  tip: '',
  props: {},
})

const getComponent = (type: string) => {
  const map: any = {
    input: 't-input',
    number: 't-input-number',
    date: 't-date-picker',
    time: 't-time-picker',
    radio: 't-radio-group',
    textarea: 't-textarea',
    select: 't-select',
    upload: 't-upload',
    daterange: 't-date-range-picker',
  }
  return map[type] || 't-input'
}

const handleSelectField = (field: any) => {
  activeField.value = field
}

const handleDragChange = () => {
  console.log('拖拽完成')
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
</script>

<style scoped lang="scss">
.form-design {
  .form-info {
    margin-bottom: 16px;
  }

  .design-area {
    display: grid;
    grid-template-columns: 240px 1fr 280px;
    gap: 16px;
    min-height: 600px;

    .component-list,
    .property-panel {
      background: var(--td-bg-color-container-hover);
      border-radius: 4px;
      padding: 16px;

      .list-title,
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

    .component-list {
      .drag-list {
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

    .form-canvas {
      background: white;
      border: 1px dashed var(--td-component-border);
      border-radius: 4px;
      padding: 20px;

      .canvas-title {
        display: flex;
        align-items: center;
        gap: 8px;
        font-weight: 500;
        margin-bottom: 16px;
        padding-bottom: 12px;
        border-bottom: 1px solid var(--td-component-border);
      }

      .canvas-content {
        .form-field-item {
          padding: 12px;
          border: 1px dashed transparent;
          border-radius: 4px;
          margin-bottom: 16px;

          &:hover {
            border-color: var(--td-brand-color);
          }

          &.active {
            border-color: var(--td-brand-color);
            background: var(--td-brand-color-light);
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
