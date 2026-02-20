<template>
  <div class="workflow-setting">
    <t-card title="工作流设置" :bordered="false">
      <t-tabs v-model="activeTab" @change="handleTabChange">
        <t-tab-panel value="basic" label="基础设置">
          <div class="setting-section">
            <t-form label-width="150px">
              <t-form-item label="默认审批人">
                <t-input v-model="settings.defaultApprover" placeholder="请输入默认审批人" />
                <template #tips>当流程节点未配置审批人时使用的默认审批人</template>
              </t-form-item>
              <t-form-item label="默认处理时限">
                <t-input-number v-model="settings.defaultTimeout" :min="1" :max="720" unit="小时" />
                <template #tips>流程节点的默认处理时限</template>
              </t-form-item>
              <t-form-item label="超时提醒时间">
                <t-input-number v-model="settings.remindTime" :min="1" :max="24" unit="小时" />
                <template #tips>在截止时间前多少小时发送超时提醒</template>
              </t-form-item>
              <t-form-item label="自动流转">
                <t-switch v-model="settings.autoTransfer" />
                <template #tips>启用后，审批通过自动流转到下一节点</template>
              </t-form-item>
              <t-form-item label="允许撤回">
                <t-switch v-model="settings.allowRecall" />
                <template #tips>允许发起人撤回已提交的流程</template>
              </t-form-item>
              <t-form-item label="允许加签">
                <t-switch v-model="settings.allowAddSign" />
                <template #tips>允许审批人添加其他审批人</template>
              </t-form-item>
              <t-form-item>
                <t-button theme="primary" @click="handleSave">保存设置</t-button>
              </t-form-item>
            </t-form>
          </div>
        </t-tab-panel>

        <t-tab-panel value="notification" label="通知设置">
          <div class="setting-section">
            <t-form label-width="150px">
              <t-form-item label="启用消息通知">
                <t-switch v-model="notification.enabled" />
              </t-form-item>
              <t-form-item label="待办任务提醒">
                <t-checkbox-group v-model="notification.todoTypes">
                  <t-checkbox value="email">邮件</t-checkbox>
                  <t-checkbox value="sms">短信</t-checkbox>
                  <t-checkbox value="push">推送</t-checkbox>
                  <t-checkbox value="dingtalk">钉钉</t-checkbox>
                  <t-checkbox value="wechat">微信</t-checkbox>
                </t-checkbox-group>
              </t-form-item>
              <t-form-item label="超时提醒">
                <t-checkbox-group v-model="notification.timeoutTypes">
                  <t-checkbox value="email">邮件</t-checkbox>
                  <t-checkbox value="sms">短信</t-checkbox>
                  <t-checkbox value="push">推送</t-checkbox>
                </t-checkbox-group>
              </t-form-item>
              <t-form-item label="流程完成通知">
                <t-checkbox-group v-model="notification.completeTypes">
                  <t-checkbox value="email">邮件</t-checkbox>
                  <t-checkbox value="push">推送</t-checkbox>
                </t-checkbox-group>
              </t-form-item>
              <t-form-item>
                <t-button theme="primary" @click="handleSave">保存设置</t-button>
              </t-form-item>
            </t-form>
          </div>
        </t-tab-panel>

        <t-tab-panel value="permission" label="权限设置">
          <div class="setting-section">
            <t-card title="流程管理权限" :bordered="false">
              <t-table
                :data="permissionData"
                :columns="permissionColumns"
                row-key="id"
              >
                <template #action="{ row }">
                  <t-link theme="primary" @click="handleEditPermission(row)">编辑</t-link>
                </template>
              </t-table>
            </t-card>
          </div>
        </t-tab-panel>

        <t-tab-panel value="log" label="操作日志">
          <div class="setting-section">
            <t-table
              :data="logData"
              :columns="logColumns"
              row-key="id"
              :pagination="{ pageSize: 10 }"
            >
              <template #action="{ row }">
                <t-link theme="primary" @click="handleViewLog(row)">查看</t-link>
              </template>
            </t-table>
          </div>
        </t-tab-panel>
      </t-tabs>
    </t-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { MessagePlugin } from 'tdesign-vue-next'

const activeTab = ref('basic')

const settings = reactive({
  defaultApprover: 'admin',
  defaultTimeout: 24,
  remindTime: 2,
  autoTransfer: true,
  allowRecall: true,
  allowAddSign: false,
})

const notification = reactive({
  enabled: true,
  todoTypes: ['email', 'push', 'dingtalk'],
  timeoutTypes: ['email', 'push'],
  completeTypes: ['email', 'push'],
})

const permissionData = ref([
  {
    id: 1,
    roleName: '系统管理员',
    permissions: ['流程设计', '流程发布', '流程监控', '流程管理'],
    createTime: '2026-02-01',
  },
  {
    id: 2,
    roleName: '流程管理员',
    permissions: ['流程设计', '流程发布', '流程监控'],
    createTime: '2026-02-01',
  },
  {
    id: 3,
    roleName: '普通用户',
    permissions: ['发起流程', '办理任务', '查看流程'],
    createTime: '2026-02-01',
  },
])

const permissionColumns = [
  { colKey: 'roleName', title: '角色名称' },
  { colKey: 'permissions', title: '权限列表' },
  { colKey: 'createTime', title: '创建时间' },
  { colKey: 'action', title: '操作', width: 100 },
]

const logData = ref([
  {
    id: 1,
    operator: '张三',
    operation: '创建流程',
    target: '请假审批流程',
    ip: '192.168.1.100',
    time: '2026-02-19 10:30:00',
  },
  {
    id: 2,
    operator: '李四',
    operation: '修改流程',
    target: '报销审批流程',
    ip: '192.168.1.101',
    time: '2026-02-19 09:20:00',
  },
  {
    id: 3,
    operator: '王五',
    operation: '删除流程',
    target: '采购审批流程',
    ip: '192.168.1.102',
    time: '2026-02-18 16:00:00',
  },
])

const logColumns = [
  { colKey: 'operator', title: '操作人', width: 100 },
  { colKey: 'operation', title: '操作类型', width: 120 },
  { colKey: 'target', title: '操作对象' },
  { colKey: 'ip', title: 'IP地址', width: 150 },
  { colKey: 'time', title: '操作时间', width: 180 },
  { colKey: 'action', title: '操作', width: 100 },
]

const handleTabChange = (value: string) => {
  console.log('Tab changed:', value)
}

const handleSave = () => {
  MessagePlugin.success('保存成功')
}

const handleEditPermission = (row: any) => {
  MessagePlugin.info(`编辑权限: ${row.roleName}`)
}

const handleViewLog = (row: any) => {
  MessagePlugin.info(`查看日志详情`)
}
</script>

<style scoped lang="scss">
.workflow-setting {
  .setting-section {
    padding: 20px 0;
  }
}
</style>
