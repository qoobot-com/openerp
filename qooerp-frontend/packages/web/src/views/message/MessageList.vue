<template>
  <div class="message-list">
    <t-list v-if="messages.length > 0" :split="true">
      <t-list-item v-for="item in messages" :key="item.id">
        <template #content>
          <div class="message-item" :class="{ unread: !item.read }">
            <div class="message-header">
              <div class="message-type">
                <t-tag
                  :theme="{
                    system: 'default',
                    task: 'primary',
                    approval: 'success',
                  }[item.type]"
                >
                  {{ { system: '系统', task: '任务', approval: '审批' }[item.type] }}
                </t-tag>
              </div>
              <div class="message-time">{{ item.time }}</div>
            </div>
            <div class="message-title">{{ item.title }}</div>
            <div class="message-content">{{ item.content }}</div>
            <div class="message-actions">
              <t-space>
                <t-link v-if="!item.read" theme="primary" @click="$emit('read', item.id)">标记已读</t-link>
                <t-link theme="danger" @click="$emit('delete', item.id)">删除</t-link>
              </t-space>
            </div>
          </div>
        </template>
      </t-list-item>
    </t-list>
    <t-empty v-else description="暂无消息" />
  </div>
</template>

<script setup lang="ts">
import { defineProps, defineEmits } from 'vue'

interface Message {
  id: number
  type: string
  title: string
  content: string
  read: boolean
  time: string
}

defineProps<{
  messages: Message[]
}>()

defineEmits<{
  read: [id: number]
  delete: [id: number]
}>()
</script>

<style scoped lang="scss">
.message-list {
  .message-item {
    padding: 16px;

    &.unread {
      background: var(--td-brand-color-light);
      border-left: 3px solid var(--td-brand-color);
    }

    .message-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 8px;

      .message-time {
        font-size: 12px;
        color: var(--td-text-color-placeholder);
      }
    }

    .message-title {
      font-size: 16px;
      font-weight: 500;
      color: var(--td-text-color-primary);
      margin-bottom: 8px;
    }

    .message-content {
      font-size: 14px;
      color: var(--td-text-color-secondary);
      margin-bottom: 12px;
      line-height: 1.6;
    }

    .message-actions {
      display: flex;
      justify-content: flex-end;
    }
  }
}
</style>
