<template>
  <div class="instance-detail">
    <t-card title="实例详情" :bordered="false">
      <template #actions>
        <t-button theme="default" @click="handleBack">返回</t-button>
      </template>

      <!-- 实例信息 -->
      <div class="instance-info">
        <t-descriptions :column="3" bordered>
          <t-descriptions-item label="实例编号">INS202602190001</t-descriptions-item>
          <t-descriptions-item label="流程名称">请假审批流程</t-descriptions-item>
          <t-descriptions-item label="流程版本">v2.1</t-descriptions-item>
          <t-descriptions-item label="发起人">张三</t-descriptions-item>
          <t-descriptions-item label="发起时间">2026-02-19 09:30:00</t-descriptions-item>
          <t-descriptions-item label="当前状态">
            <t-tag theme="primary">运行中</t-tag>
          </t-descriptions-item>
        </t-descriptions>
      </div>

      <!-- 流程图 -->
      <div class="process-flow">
        <t-card title="流程进度" :bordered="false">
          <t-steps :current="2" theme="dot">
            <t-step-item title="开始" content="已提交申请" />
            <t-step-item title="部门审批" content="审批通过" />
            <t-step-item title="财务审批" content="审批中" />
            <t-step-item title="结束" content="待处理" />
          </t-steps>
        </t-card>
      </div>

      <!-- 审批历史 -->
      <div class="approval-history">
        <t-card title="审批历史" :bordered="false">
          <t-timeline>
            <t-timeline-item label="2026-02-19 10:30:00">
              <p>张三 提交了申请</p>
              <t-tag size="small" theme="default">开始节点</t-tag>
            </t-timeline-item>
            <t-timeline-item label="2026-02-19 11:00:00">
              <p>李四 审批通过</p>
              <p class="remark">审批意见：同意申请</p>
              <t-tag size="small" theme="success">部门审批</t-tag>
            </t-timeline-item>
            <t-timeline-item label="2026-02-19 14:20:00">
              <p>王五 正在审批</p>
              <t-tag size="small" theme="primary">财务审批</t-tag>
            </t-timeline-item>
          </t-timeline>
        </t-card>
      </div>

      <!-- 表单数据 -->
      <div class="form-data">
        <t-card title="表单数据" :bordered="false">
          <t-descriptions :column="2" bordered>
            <t-descriptions-item label="申请人">张三</t-descriptions-item>
            <t-descriptions-item label="部门">技术部</t-descriptions-item>
            <t-descriptions-item label="请假类型">年假</t-descriptions-item>
            <t-descriptions-item label="请假天数">3天</t-descriptions-item>
            <t-descriptions-item label="开始时间">2026-02-22</t-descriptions-item>
            <t-descriptions-item label="结束时间">2026-02-24</t-descriptions-item>
            <t-descriptions-item label="请假原因" :span="2">需要处理家庭事务</t-descriptions-item>
          </t-descriptions>
        </t-card>
      </div>
    </t-card>
  </div>
</template>

<script setup lang="ts">
import { MessagePlugin } from 'tdesign-vue-next'

const handleBack = () => {
  MessagePlugin.info('返回列表')
}
</script>

<style scoped lang="scss">
.instance-detail {
  .instance-info {
    margin-bottom: 16px;
  }

  .process-flow {
    margin-bottom: 16px;
  }

  .approval-history {
    margin-bottom: 16px;

    .remark {
      margin-top: 4px;
      font-size: 12px;
      color: var(--td-text-color-secondary);
    }
  }
}
</style>
