<template>
  <div class="workflow-integration">
    <t-card title="工作流集成" :bordered="false">
      <t-tabs v-model="activeTab">
        <t-tab-panel value="system" label="系统集成">
          <div class="integration-section">
            <t-row :gutter="[16, 16]">
              <t-col :span="8" v-for="item in systemList" :key="item.id">
                <t-card :bordered="true" hover-shadow>
                  <div class="system-item">
                    <div class="system-icon">
                      <component :is="item.icon" />
                    </div>
                    <div class="system-info">
                      <div class="system-name">{{ item.name }}</div>
                      <div class="system-desc">{{ item.description }}</div>
                      <div class="system-status">
                        <t-tag :theme="item.connected ? 'success' : 'default'">
                          {{ item.connected ? '已连接' : '未连接' }}
                        </t-tag>
                      </div>
                    </div>
                    <div class="system-action">
                      <t-button v-if="!item.connected" theme="primary" size="small" @click="handleConnect(item)">连接</t-button>
                      <t-button v-else theme="default" size="small" @click="handleDisconnect(item)">断开</t-button>
                      <t-button theme="default" size="small" @click="handleConfig(item)">配置</t-button>
                    </div>
                  </div>
                </t-card>
              </t-col>
            </t-row>
          </div>
        </t-tab-panel>

        <t-tab-panel value="api" label="API配置">
          <div class="api-section">
            <t-card title="API密钥管理" :bordered="false">
              <t-table
                :data="apiKeyList"
                :columns="apiKeyColumns"
                row-key="id"
              >
                <template #status="{ row }">
                  <t-tag :theme="row.status === 'active' ? 'success' : 'default'">
                    {{ row.status === 'active' ? '启用' : '禁用' }}
                  </t-tag>
                </template>
                <template #action="{ row }">
                  <t-space>
                    <t-link theme="primary" @click="handleViewKey(row)">查看</t-link>
                    <t-link theme="primary" @click="handleRegenerate(row)">重新生成</t-link>
                    <t-link theme="danger" @click="handleDeleteKey(row)">删除</t-link>
                  </t-space>
                </template>
              </t-table>
              <div class="api-actions">
                <t-button theme="primary" @click="handleCreateKey">创建API密钥</t-button>
              </div>
            </t-card>
          </div>
        </t-tab-panel>

        <t-tab-panel value="webhook" label="Webhook配置">
          <div class="webhook-section">
            <t-button theme="primary" @click="handleCreateWebhook" style="margin-bottom: 16px">
              <template #icon><add-icon /></template>
              新建Webhook
            </t-button>
            <t-table
              :data="webhookList"
              :columns="webhookColumns"
              row-key="id"
            >
              <template #status="{ row }">
                <t-tag :theme="row.status === 'active' ? 'success' : 'default'">
                  {{ row.status === 'active' ? '启用' : '禁用' }}
                </t-tag>
              </template>
              <template #events="{ row }">
                <t-space size="small">
                  <t-tag v-for="event in row.events" :key="event" size="small">{{ event }}</t-tag>
                </t-space>
              </template>
              <template #action="{ row }">
                <t-space>
                  <t-link theme="primary" @click="handleEditWebhook(row)">编辑</t-link>
                  <t-link theme="primary" @click="handleTestWebhook(row)">测试</t-link>
                  <t-link v-if="row.status === 'active'" theme="warning" @click="handleDisableWebhook(row)">禁用</t-link>
                  <t-link v-else theme="success" @click="handleEnableWebhook(row)">启用</t-link>
                  <t-link theme="danger" @click="handleDeleteWebhook(row)">删除</t-link>
                </t-space>
              </template>
            </t-table>
          </div>
        </t-tab-panel>

        <t-tab-panel value="data" label="数据同步">
          <div class="data-section">
            <t-card title="数据同步配置" :bordered="false">
              <t-table
                :data="syncList"
                :columns="syncColumns"
                row-key="id"
              >
                <template #status="{ row }">
                  <t-tag :theme="row.status === 'syncing' ? 'primary' : 'success'">
                    {{ row.status === 'syncing' ? '同步中' : '已完成' }}
                  </t-tag>
                </template>
                <template #action="{ row }">
                  <t-space>
                    <t-link theme="primary" @click="handleSyncNow(row)">立即同步</t-link>
                    <t-link theme="default" @click="handleViewLog(row)">查看日志</t-link>
                  </t-space>
                </template>
              </t-table>
            </t-card>
          </div>
        </t-tab-panel>
      </t-tabs>
    </t-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { MessagePlugin } from 'tdesign-vue-next'
import { AddIcon, LogoWechatIcon, LogoDingtalkIcon, MailIcon, ServerIcon } from 'tdesign-icons-vue-next'

const activeTab = ref('system')

const systemList = ref([
  {
    id: 1,
    name: '企业微信',
    description: '企业微信工作集成',
    icon: LogoWechatIcon,
    connected: true,
  },
  {
    id: 2,
    name: '钉钉',
    description: '钉钉工作集成',
    icon: LogoDingtalkIcon,
    connected: true,
  },
  {
    id: 3,
    name: '邮件系统',
    description: '邮件通知集成',
    icon: MailIcon,
    connected: false,
  },
  {
    id: 4,
    name: '业务系统',
    description: '业务数据集成',
    icon: ServerIcon,
    connected: true,
  },
])

const apiKeyList = ref([
  {
    id: 1,
    name: '生产环境API',
    key: 'sk_****************************',
    status: 'active',
    createTime: '2026-02-01',
    lastUsed: '2026-02-19',
  },
  {
    id: 2,
    name: '测试环境API',
    key: 'sk_****************************',
    status: 'active',
    createTime: '2026-02-10',
    lastUsed: '2026-02-18',
  },
])

const apiKeyColumns = [
  { colKey: 'name', title: '密钥名称' },
  { colKey: 'key', title: 'API密钥' },
  { colKey: 'status', title: '状态', width: 100 },
  { colKey: 'createTime', title: '创建时间', width: 120 },
  { colKey: 'lastUsed', title: '最后使用', width: 120 },
  { colKey: 'action', title: '操作', width: 220 },
]

const webhookList = ref([
  {
    id: 1,
    name: '流程完成通知',
    url: 'https://example.com/webhook/workflow',
    events: ['process.completed', 'process.terminated'],
    status: 'active',
    createTime: '2026-02-01',
  },
  {
    id: 2,
    name: '任务分配通知',
    url: 'https://example.com/webhook/task',
    events: ['task.assigned', 'task.completed'],
    status: 'active',
    createTime: '2026-02-10',
  },
])

const webhookColumns = [
  { colKey: 'name', title: 'Webhook名称' },
  { colKey: 'url', title: '回调URL' },
  { colKey: 'events', title: '触发事件' },
  { colKey: 'status', title: '状态', width: 100 },
  { colKey: 'createTime', title: '创建时间', width: 120 },
  { colKey: 'action', title: '操作', width: 260 },
]

const syncList = ref([
  {
    id: 1,
    name: '组织架构同步',
    source: '企业微信',
    target: '本地数据库',
    status: 'syncing',
    lastSync: '2026-02-19 10:00:00',
    nextSync: '2026-02-19 11:00:00',
  },
  {
    id: 2,
    name: '流程数据同步',
    source: '业务系统',
    target: '工作流引擎',
    status: 'completed',
    lastSync: '2026-02-19 09:30:00',
    nextSync: '2026-02-19 10:30:00',
  },
])

const syncColumns = [
  { colKey: 'name', title: '同步任务' },
  { colKey: 'source', title: '数据源' },
  { colKey: 'target', title: '目标系统' },
  { colKey: 'status', title: '状态', width: 100 },
  { colKey: 'lastSync', title: '上次同步', width: 180 },
  { colKey: 'nextSync', title: '下次同步', width: 180 },
  { colKey: 'action', title: '操作', width: 150 },
]

const handleConnect = (item: any) => {
  MessagePlugin.success(`连接系统: ${item.name}`)
  item.connected = true
}

const handleDisconnect = (item: any) => {
  MessagePlugin.success(`断开系统: ${item.name}`)
  item.connected = false
}

const handleConfig = (item: any) => {
  MessagePlugin.info(`配置系统: ${item.name}`)
}

const handleCreateKey = () => {
  MessagePlugin.info('创建API密钥')
}

const handleViewKey = (row: any) => {
  MessagePlugin.info(`查看密钥: ${row.name}`)
}

const handleRegenerate = (row: any) => {
  MessagePlugin.success(`重新生成密钥: ${row.name}`)
}

const handleDeleteKey = (row: any) => {
  MessagePlugin.success(`删除密钥: ${row.name}`)
}

const handleCreateWebhook = () => {
  MessagePlugin.info('新建Webhook')
}

const handleEditWebhook = (row: any) => {
  MessagePlugin.info(`编辑Webhook: ${row.name}`)
}

const handleTestWebhook = (row: any) => {
  MessagePlugin.success(`测试Webhook: ${row.name}`)
}

const handleEnableWebhook = (row: any) => {
  row.status = 'active'
  MessagePlugin.success('启用成功')
}

const handleDisableWebhook = (row: any) => {
  row.status = 'disabled'
  MessagePlugin.success('禁用成功')
}

const handleDeleteWebhook = (row: any) => {
  MessagePlugin.success(`删除Webhook: ${row.name}`)
}

const handleSyncNow = (row: any) => {
  row.status = 'syncing'
  MessagePlugin.success('开始同步')
}

const handleViewLog = (row: any) => {
  MessagePlugin.info('查看同步日志')
}
</script>

<style scoped lang="scss">
.workflow-integration {
  .integration-section {
    padding: 20px 0;

    .system-item {
      display: flex;
      flex-direction: column;
      gap: 12px;

      .system-icon {
        display: flex;
        justify-content: center;
        font-size: 48px;
        color: var(--td-brand-color);
      }

      .system-info {
        text-align: center;

        .system-name {
          font-size: 16px;
          font-weight: 500;
          margin-bottom: 4px;
        }

        .system-desc {
          font-size: 12px;
          color: var(--td-text-color-secondary);
          margin-bottom: 8px;
        }
      }

      .system-action {
        display: flex;
        gap: 8px;
      }
    }
  }

  .api-section {
    .api-actions {
      margin-top: 16px;
    }
  }

  .webhook-section,
  .data-section {
    padding: 20px 0;
  }
}
</style>
