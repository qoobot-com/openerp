<template>
  <div class="not-found">
    <div class="content">
      <h1>404</h1>
      <p>页面不存在</p>
      <p class="description">抱歉,您访问的页面不存在或已被删除</p>
      <t-space>
        <t-button theme="primary" @click="goHome">返回首页</t-button>
        <t-button theme="default" @click="goBack">返回上一页</t-button>
      </t-space>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

const goHome = () => {
  router.push('/')
}

const goBack = () => {
  router.back()
}
</script>

<style scoped lang="scss">
.not-found {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100vh;
  background: #f5f5f5;

  .content {
    text-align: center;

    h1 {
      font-size: 120px;
      color: var(--td-brand-color);
      margin: 0;
      line-height: 1;
    }

    p {
      font-size: 18px;
      color: var(--td-text-color-secondary);
      margin: 16px 0;

      &.description {
        font-size: 14px;
        color: var(--td-text-color-placeholder);
      }
    }
  }
}
</style>
