<template>
  <div class="login-container">
    <div class="login-box">
      <h1>QooERP</h1>
      <p>登录页面</p>
      <div class="form">
        <t-input placeholder="用户名" v-model="username" />
        <t-input type="password" placeholder="密码" v-model="password" />
        <t-button theme="primary" @click="handleLogin">登录</t-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/modules/user'

const router = useRouter()
const userStore = useUserStore()

const username = ref('')
const password = ref('')

const handleLogin = async () => {
  // 简单的模拟登录，实际项目中应该调用后端API
  if (username.value && password.value) {
    localStorage.setItem('token', 'mock-token-' + Date.now())
    localStorage.setItem('permissions', JSON.stringify(['*']))
    await userStore.setUserInfo({
      id: '1',
      username: username.value,
      realName: '管理员',
      avatar: '',
      roles: ['admin'],
      permissions: ['*'],
    })
    router.push('/')
  }
}
</script>

<style scoped lang="scss">
.login-container {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);

  .login-box {
    background: white;
    padding: 40px;
    border-radius: 12px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
    width: 400px;
    text-align: center;

    h1 {
      font-size: 28px;
      color: #333;
      margin-bottom: 10px;
    }

    p {
      color: #666;
      margin-bottom: 30px;
    }

    .form {
      display: flex;
      flex-direction: column;
      gap: 16px;

      .t-input {
        width: 100%;
      }

      .t-button {
        width: 100%;
        margin-top: 10px;
      }
    }
  }
}
</style>
