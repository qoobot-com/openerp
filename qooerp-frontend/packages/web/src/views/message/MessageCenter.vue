<template>
  <div class="message-center">
    <t-card title="消息中心" :bordered="false">
      <t-tabs v-model="activeTab" @change="handleTabChange">
        <t-tab-panel value="all" :label="`全部消息 (${totalCount})`">
          <message-list :messages="allMessages" @read="handleRead" @delete="handleDelete" />
        </t-tab-panel>
        <t-tab-panel value="unread" :label="`未读消息 (${unreadCount})`">
          <message-list :messages="unreadMessages" @read="handleRead" @delete="handleDelete" />
        </t-tab-panel>
        <t-tab-panel value="system" label="系统通知">
          <message-list :messages="systemMessages" @read="handleRead" @delete="handleDelete" />
        </t-tab-panel>
        <t-tab-panel value="task" label="任务消息">
          <message-list :messages="taskMessages" @read="handleRead" @delete="handleDelete" />
        </t-tab-panel>
        <t-tab-panel value="approval" label="审批消息">
          <message-list :messages="approvalMessages" @read="handleRead" @delete="handleDelete" />
        </t-tab-panel>
      </t-tabs>
    </t-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { MessagePlugin } from 'tdesign-vue-next'
import MessageList from './MessageList.vue'

const activeTab = ref('all')

const allMessages = ref([
  {
    id: 1,
    type: 'system',
    title: '系统维护通知',
    content: '系统将于今晚22:00-24:00进行维护，请提前保存工作内容。',
    read: false,
    time: '2026-02-19 10:30:00',
  },
  {
    id: 2,
    type: 'task',
    title: '新任务分配',
    content: '您有1个新的待办任务需要处理：请假审批 - 张三',
    read: false,
    time: '2026-02-19 09:20:00',
  },
  {
    id: 3,
    type: 'approval',
    title: '审批通过',
    content: '您的报销申请已通过审批，报销金额：¥1,500',
    read: true,
    time: '2026-02-18 16:30:00',
  },
])

const unreadMessages = computed(() => allMessages.value.filter(m => !m.read))
const systemMessages = computed(() => allMessages.value.filter(m => m.type === 'system'))
const taskMessages = computed(() => allMessages.value.filter(m => m.type === 'task'))
const approvalMessages = computed(() => allMessages.value.filter(m => m.type === 'approval'))

const totalCount = ref(3)
const unreadCount = ref(2)

const handleTabChange = (value: string) => {
  console.log('Tab changed:', value)
}

const handleRead = (id: number) => {
  const message = allMessages.value.find(m => m.id === id)
  if (message) {
    message.read = true
    unreadCount.value--
  }
  MessagePlugin.success('标记为已读')
}

const handleDelete = (id: number) => {
  const index = allMessages.value.findIndex(m => m.id === id)
  if (index > -1) {
    allMessages.value.splice(index, 1)
    totalCount.value--
  }
  MessagePlugin.success('删除成功')
}
</script>

<style scoped lang="scss">
.message-center {
  :deep(.t-tabs__content) {
    min-height: 500px;
  }
}
</style>
