<template>
  <div class="error-page">
    <div class="error-content">
      <div class="error-icon">
        <svg viewBox="0 0 24 24" width="80" height="80">
          <path fill="#ff4d4f" d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z"/>
        </svg>
      </div>
      <h1>403</h1>
      <p>抱歉，您没有权限访问该页面</p>
      <t-button theme="primary" @click="goHome">返回首页</t-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

const goHome = () => {
  router.push('/')
}
</script>

<style scoped lang="scss">
.error-page {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100vh;
  background: #f5f5f5;

  .error-content {
    text-align: center;

    .error-icon {
      margin-bottom: 20px;
    }

    h1 {
      font-size: 72px;
      color: #333;
      margin-bottom: 10px;
    }

    p {
      font-size: 16px;
      color: #666;
      margin-bottom: 30px;
    }
  }
}
</style>
