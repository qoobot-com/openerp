<template>
  <div class="process-design">
    <t-card title="流程设计器" :bordered="false">
      <template #actions>
        <t-space>
          <t-button theme="default" @click="handleSave">保存</t-button>
          <t-button theme="default" @click="handlePreview">预览</t-button>
          <t-button theme="default" @click="handlePublish">发布</t-button>
        </t-space>
      </template>

      <!-- 工具栏 -->
      <div class="toolbar">
        <t-space>
          <t-button variant="text" @click="handleAddStart"><play-icon /> 开始节点</t-button>
          <t-button variant="text" @click="handleAddTask"><check-circle-icon /> 审批节点</t-button>
          <t-button variant="text" @click="handleAddCondition"><share-icon /> 条件节点</t-button>
          <t-button variant="text" @click="handleAddParallel"><check-icon /> 并行节点</t-button>
          <t-button variant="text" @click="handleAddEnd"><stop-icon /> 结束节点</t-button>
        </t-space>
      </div>

      <!-- 设计画布 -->
      <div class="design-canvas">
        <div class="canvas-area">
          <!-- 节点示例 -->
          <div class="node start-node">
            <div class="node-header">
              <play-icon />
              <span>开始</span>
            </div>
          </div>

          <div class="connector">
            <chevron-down-icon />
          </div>

          <div class="node task-node">
            <div class="node-header">
              <check-circle-icon />
              <span>部门审批</span>
            </div>
            <div class="node-content">
              <div>审批人: 部门经理</div>
              <div>审批方式: 会签</div>
            </div>
          </div>

          <div class="connector">
            <chevron-down-icon />
          </div>

          <div class="node condition-node">
            <div class="node-header">
              <share-icon />
              <span>条件判断</span>
            </div>
            <div class="node-content">
              <div>条件: 金额 > 5000</div>
            </div>
          </div>

          <div class="connector">
            <chevron-down-icon />
          </div>

          <div class="node task-node">
            <div class="node-header">
              <check-circle-icon />
              <span>财务审批</span>
            </div>
          </div>

          <div class="connector">
            <chevron-down-icon />
          </div>

          <div class="node end-node">
            <div class="node-header">
              <stop-icon />
              <span>结束</span>
            </div>
          </div>
        </div>
      </div>
    </t-card>
  </div>
</template>

<script setup lang="ts">
import { MessagePlugin } from 'tdesign-vue-next'
import {
  PlayIcon, CheckCircleIcon, ShareIcon, CheckIcon, StopIcon, ChevronDownIcon,
} from 'tdesign-icons-vue-next'

const handleSave = () => {
  MessagePlugin.success('保存成功')
}

const handlePreview = () => {
  MessagePlugin.info('预览模式')
}

const handlePublish = () => {
  MessagePlugin.success('发布成功')
}

const handleAddStart = () => {
  MessagePlugin.info('添加开始节点')
}

const handleAddTask = () => {
  MessagePlugin.info('添加审批节点')
}

const handleAddCondition = () => {
  MessagePlugin.info('添加条件节点')
}

const handleAddParallel = () => {
  MessagePlugin.info('添加并行节点')
}

const handleAddEnd = () => {
  MessagePlugin.info('添加结束节点')
}
</script>

<style scoped lang="scss">
.process-design {
  .toolbar {
    padding: 12px;
    background: var(--td-bg-color-container-hover);
    border-radius: 4px;
    margin-bottom: 16px;
  }

  .design-canvas {
    min-height: 600px;
    background: var(--td-bg-color-container);
    border: 1px solid var(--td-component-border);
    border-radius: 4px;
    padding: 40px;

    .canvas-area {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 40px;
    }

    .node {
      min-width: 200px;
      padding: 12px 20px;
      border-radius: 8px;
      border: 2px solid;
      background: white;

      &.start-node {
        border-color: var(--td-success-color);
      }

      &.end-node {
        border-color: var(--td-error-color);
      }

      &.task-node {
        border-color: var(--td-brand-color);
      }

      &.condition-node {
        border-color: var(--td-warning-color);
      }

      .node-header {
        display: flex;
        align-items: center;
        gap: 8px;
        font-weight: 500;
      }

      .node-content {
        margin-top: 8px;
        font-size: 12px;
        color: var(--td-text-color-secondary);
      }
    }

    .connector {
      color: var(--td-text-color-placeholder);
      font-size: 20px;
    }
  }
}
</style>
