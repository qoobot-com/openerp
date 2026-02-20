<template>
  <div class="workflow-optimization">
    <t-card title="流程优化" :bordered="false">
      <t-tabs v-model="activeTab">
        <t-tab-panel value="bottleneck" label="瓶颈分析">
          <div class="optimization-section">
            <t-alert theme="warning" title="发现3个流程瓶颈，建议优先处理" style="margin-bottom: 16px" />

            <t-row :gutter="[16, 16]">
              <t-col :span="8" v-for="item in bottlenecks" :key="item.id">
                <t-card :bordered="true" hover-shadow>
                  <div class="bottleneck-item">
                    <div class="bottleneck-header">
                      <error-circle-icon />
                      <span class="title">{{ item.title }}</span>
                      <t-tag theme="danger" size="small">高风险</t-tag>
                    </div>
                    <div class="bottleneck-content">
                      <div class="info-item">
                        <span class="label">流程名称：</span>
                        <span>{{ item.processName }}</span>
                      </div>
                      <div class="info-item">
                        <span class="label">瓶颈节点：</span>
                        <span>{{ item.node }}</span>
                      </div>
                      <div class="info-item">
                        <span class="label">平均耗时：</span>
                        <span class="value">{{ item.avgDuration }}</span>
                      </div>
                      <div class="info-item">
                        <span class="label">影响实例：</span>
                        <span class="value">{{ item.instances }}</span>
                      </div>
                    </div>
                    <div class="bottleneck-action">
                      <t-button theme="primary" size="small" @click="handleOptimize(item)">优化建议</t-button>
                      <t-button theme="default" size="small" @click="handleAnalyze(item)">详细分析</t-button>
                    </div>
                  </div>
                </t-card>
              </t-col>
            </t-row>
          </div>
        </t-tab-panel>

        <t-tab-panel value="timeout" label="超时分析">
          <div class="optimization-section">
            <t-card title="超时流程统计" :bordered="false">
              <t-row :gutter="[16, 16]" class="stats-row">
                <t-col :span="6">
                  <t-statistic title="超时流程数" :value="timeoutStats.total" />
                </t-col>
                <t-col :span="6">
                  <t-statistic title="超时率" :value="timeoutStats.rate" suffix="%" />
                </t-col>
                <t-col :span="6">
                  <t-statistic title="平均超时时长" :value="timeoutStats.avgDuration" suffix="小时" />
                </t-col>
                <t-col :span="6">
                  <t-statistic title="影响人数" :value="timeoutStats.affectedUsers" />
                </t-col>
              </t-row>

              <t-table
                :data="timeoutData"
                :columns="timeoutColumns"
                row-key="id"
                :pagination="{ pageSize: 10 }"
                style="margin-top: 16px"
              >
                <template #action="{ row }">
                  <t-space>
                    <t-link theme="primary" @click="handleViewDetail(row)">查看详情</t-link>
                    <t-link theme="warning" @click="handleOptimizeProcess(row)">优化流程</t-link>
                  </t-space>
                </template>
              </t-table>
            </t-card>
          </div>
        </t-tab-panel>

        <t-tab-panel value="recommendation" label="优化建议">
          <div class="optimization-section">
            <t-row :gutter="[16, 16]">
              <t-col :span="24" v-for="item in recommendations" :key="item.id">
                <t-card :bordered="true" hover-shadow>
                  <div class="recommendation-item">
                    <div class="recommendation-header">
                      <lightbulb-icon />
                      <span class="title">{{ item.title }}</span>
                      <t-tag :theme="item.priority === 'high' ? 'danger' : item.priority === 'medium' ? 'warning' : 'default'">
                        {{ { high: '高优先级', medium: '中优先级', low: '低优先级' }[item.priority] }}
                      </t-tag>
                    </div>
                    <div class="recommendation-content">
                      <div class="description">{{ item.description }}</div>
                      <div class="impact">
                        <span class="label">预期效果：</span>
                        <span class="value">{{ item.expectedImpact }}</span>
                      </div>
                    </div>
                    <div class="recommendation-action">
                      <t-button theme="primary" size="small" @click="handleApply(item)">应用建议</t-button>
                      <t-button theme="default" size="small" @click="handleView(item)">查看详情</t-button>
                    </div>
                  </div>
                </t-card>
              </t-col>
            </t-row>
          </div>
        </t-tab-panel>

        <t-tab-panel value="automation" label="自动化建议">
          <div class="optimization-section">
            <t-card title="自动化机会分析" :bordered="false">
              <t-table
                :data="automationData"
                :columns="automationColumns"
                row-key="id"
              >
                <template #automationLevel="{ row }">
                  <t-progress
                    :percentage="row.automationLevel"
                    :label="`${row.automationLevel}%`"
                    :theme="row.automationLevel > 80 ? 'success' : row.automationLevel > 50 ? 'warning' : 'error'"
                  />
                </template>
                <template #action="{ row }">
                  <t-space>
                    <t-link theme="primary" @click="handleSetAutomation(row)">配置自动化</t-link>
                    <t-link theme="default" @click="handlePreview(row)">预览效果</t-link>
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
import { ref, reactive } from 'vue'
import { MessagePlugin } from 'tdesign-vue-next'
import { ErrorCircleIcon, LightbulbIcon } from 'tdesign-icons-vue-next'

const activeTab = ref('bottleneck')

const bottlenecks = ref([
  {
    id: 1,
    title: '财务审批耗时过长',
    processName: '报销审批流程',
    node: '财务审批',
    avgDuration: '4.5小时',
    instances: 156,
  },
  {
    id: 2,
    title: '总经理审批积压严重',
    processName: '采购审批流程',
    node: '总经理审批',
    avgDuration: '6.2小时',
    instances: 89,
  },
  {
    id: 3,
    title: '合同审核响应慢',
    processName: '合同审批流程',
    node: '法务审核',
    avgDuration: '8.5小时',
    instances: 45,
  },
])

const timeoutStats = reactive({
  total: 28,
  rate: 2.3,
  avgDuration: 3.5,
  affectedUsers: 156,
})

const timeoutData = ref([
  {
    id: 1,
    processName: '报销审批流程',
    instanceNo: 'INS202602180001',
    timeoutNode: '财务审批',
    timeoutTime: '2026-02-18 17:30:00',
    overtimeDuration: '5.5小时',
  },
  {
    id: 2,
    processName: '采购审批流程',
    instanceNo: 'INS202602180002',
    timeoutNode: '总经理审批',
    timeoutTime: '2026-02-18 16:00:00',
    overtimeDuration: '8.2小时',
  },
  {
    id: 3,
    processName: '合同审批流程',
    instanceNo: 'INS202602180003',
    timeoutNode: '法务审核',
    timeoutTime: '2026-02-18 15:00:00',
    overtimeDuration: '10.5小时',
  },
])

const timeoutColumns = [
  { colKey: 'processName', title: '流程名称' },
  { colKey: 'instanceNo', title: '实例编号' },
  { colKey: 'timeoutNode', title: '超时节点' },
  { colKey: 'timeoutTime', title: '超时时间' },
  { colKey: 'overtimeDuration', title: '超时时长' },
  { colKey: 'action', title: '操作', width: 180 },
]

const recommendations = ref([
  {
    id: 1,
    title: '优化财务审批节点',
    description: '将财务审批拆分为初审和终审两个节点，提高处理效率。初审可由财务专员完成，复杂案例再提交给财务经理。',
    priority: 'high',
    expectedImpact: '预计减少40%的平均耗时',
  },
  {
    id: 2,
    title: '设置金额阈值自动审批',
    description: '对于金额低于5000元的报销单，设置自动审批规则，无需人工介入。',
    priority: 'medium',
    expectedImpact: '预计减少30%的低金额审批工作量',
  },
  {
    id: 3,
    title: '添加提醒通知功能',
    description: '在流程超时前2小时发送提醒通知给相关审批人，避免流程超时。',
    priority: 'high',
    expectedImpact: '预计减少60%的超时情况',
  },
])

const automationData = ref([
  {
    id: 1,
    processName: '请假审批流程',
    nodeName: '部门审批',
    currentProcess: '人工审批',
    automationLevel: 75,
    automationSuggestion: '根据请假天数自动审批，3天以下可自动通过',
  },
  {
    id: 2,
    processName: '报销审批流程',
    nodeName: '初审',
    currentProcess: '人工审批',
    automationLevel: 60,
    automationSuggestion: '低于标准的报销单可自动通过',
  },
  {
    id: 3,
    processName: '采购审批流程',
    nodeName: '供应商资质审核',
    currentProcess: '人工审核',
    automationLevel: 85,
    automationSuggestion: '对接供应商系统，自动获取资质信息',
  },
])

const automationColumns = [
  { colKey: 'processName', title: '流程名称' },
  { colKey: 'nodeName', title: '节点名称' },
  { colKey: 'currentProcess', title: '当前处理方式' },
  { colKey: 'automationLevel', title: '可自动化程度' },
  { colKey: 'automationSuggestion', title: '自动化建议' },
  { colKey: 'action', title: '操作', width: 180 },
]

const handleOptimize = (item: any) => {
  MessagePlugin.info(`优化瓶颈: ${item.title}`)
}

const handleAnalyze = (item: any) => {
  MessagePlugin.info(`详细分析: ${item.title}`)
}

const handleViewDetail = (row: any) => {
  MessagePlugin.info(`查看详情: ${row.instanceNo}`)
}

const handleOptimizeProcess = (row: any) => {
  MessagePlugin.info(`优化流程: ${row.processName}`)
}

const handleApply = (item: any) => {
  MessagePlugin.success(`应用建议: ${item.title}`)
}

const handleView = (item: any) => {
  MessagePlugin.info(`查看详情: ${item.title}`)
}

const handleSetAutomation = (row: any) => {
  MessagePlugin.info(`配置自动化: ${row.nodeName}`)
}

const handlePreview = (row: any) => {
  MessagePlugin.info(`预览效果: ${row.nodeName}`)
}
</script>

<style scoped lang="scss">
.workflow-optimization {
  .optimization-section {
    padding: 20px 0;

    .bottleneck-item,
    .recommendation-item {
      .bottleneck-header,
      .recommendation-header {
        display: flex;
        align-items: center;
        gap: 8px;
        margin-bottom: 12px;

        .title {
          flex: 1;
          font-weight: 500;
        }
      }

      .bottleneck-content,
      .recommendation-content {
        margin-bottom: 12px;

        .info-item {
          display: flex;
          margin-bottom: 4px;
          font-size: 14px;

          .label {
            color: var(--td-text-color-secondary);
            width: 80px;
          }

          .value {
            color: var(--td-error-color);
            font-weight: 500;
          }
        }

        .description {
          color: var(--td-text-color-secondary);
          margin-bottom: 8px;
          font-size: 14px;
        }

        .impact {
          font-size: 14px;

          .label {
            color: var(--td-text-color-secondary);
          }

          .value {
            color: var(--td-success-color);
            font-weight: 500;
          }
        }
      }

      .bottleneck-action,
      .recommendation-action {
        display: flex;
        gap: 8px;
      }
    }
  }
}
</style>
