/**
 * 移动端登录页
 * P6-MOB-T2: 移动端登录
 */
<template>
  <div class="mobile-login">
    <div class="login-container">
      <!-- Logo -->
      <div class="logo-section">
        <div class="logo">
          <img src="/logo.png" alt="QooERP" />
        </div>
        <div class="title">QooERP</div>
        <div class="subtitle">企业管理平台</div>
      </div>

      <!-- 登录表单 -->
      <div class="login-form">
        <t-tabs v-model:active="loginType" class="login-tabs">
          <t-tab-panel value="password" label="密码登录">
            <t-form ref="formRef" :data="formData" :rules="rules" class="form">
              <t-form-item name="username">
                <t-input
                  v-model="formData.username"
                  placeholder="请输入用户名"
                  size="large"
                  clearable
                >
                  <template #prefix-icon>
                    <UserIcon />
                  </template>
                </t-input>
              </t-form-item>
              <t-form-item name="password">
                <t-input
                  v-model="formData.password"
                  type="password"
                  placeholder="请输入密码"
                  size="large"
                  clearable
                >
                  <template #prefix-icon>
                    <LockOnIcon />
                  </template>
                </t-input>
              </t-form-item>
              <div class="form-actions">
                <t-checkbox v-model="rememberMe">记住我</t-checkbox>
                <t-link theme="primary" @click="forgotPassword">忘记密码？</t-link>
              </div>
              <t-button
                theme="primary"
                size="large"
                block
                :loading="loading"
                @click="handleLogin"
              >
                登录
              </t-button>
            </t-form>
          </t-tab-panel>
          <t-tab-panel value="code" label="验证码登录">
            <t-form ref="codeFormRef" :data="codeFormData" :rules="codeRules" class="form">
              <t-form-item name="phone">
                <t-input
                  v-model="codeFormData.phone"
                  placeholder="请输入手机号"
                  size="large"
                  clearable
                >
                  <template #prefix-icon>
                    <PhoneIcon />
                  </template>
                </t-input>
              </t-form-item>
              <t-form-item name="code">
                <t-input
                  v-model="codeFormData.code"
                  placeholder="请输入验证码"
                  size="large"
                  clearable
                >
                  <template #prefix-icon>
                    <CheckCircleIcon />
                  </template>
                  <template #suffix>
                    <t-button
                      variant="text"
                      :disabled="codeCountdown > 0"
                      @click="sendCode"
                    >
                      {{ codeCountdown > 0 ? `${codeCountdown}s` : '发送验证码' }}
                    </t-button>
                  </template>
                </t-input>
              </t-form-item>
              <t-button
                theme="primary"
                size="large"
                block
                :loading="loading"
                @click="handleCodeLogin"
              >
                登录
              </t-button>
            </t-form>
          </t-tab-panel>
        </t-tabs>
      </div>

      <!-- 其他登录方式 -->
      <div class="other-login">
        <div class="divider">
          <span>其他登录方式</span>
        </div>
        <div class="login-methods">
          <div class="method-item" @click="wechatLogin">
            <img src="/icons/wechat.png" alt="微信" />
          </div>
          <div class="method-item" @click="alipayLogin">
            <img src="/icons/alipay.png" alt="支付宝" />
          </div>
          <div class="method-item" @click="scanLogin">
            <ScanIcon size="32px" />
          </div>
        </div>
      </div>

      <!-- 底部链接 -->
      <div class="footer-links">
        <t-link theme="default" @click="register">注册账号</t-link>
        <t-divider layout="vertical" />
        <t-link theme="default" @click="help">帮助中心</t-link>
        <t-divider layout="vertical" />
        <t-link theme="default" @click="privacy">隐私政策</t-link>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { MessagePlugin } from 'tdesign-vue-next'
import {
  UserIcon,
  LockOnIcon,
  PhoneIcon,
  CheckCircleIcon,
  ScanIcon,
} from 'tdesign-icons-vue-next'

const router = useRouter()

// 登录类型
const loginType = ref('password')

// 表单数据
const formData = ref({
  username: '',
  password: '',
})

const codeFormData = ref({
  phone: '',
  code: '',
})

// 表单规则
const rules = {
  username: [{ required: true, message: '请输入用户名' }],
  password: [{ required: true, message: '请输入密码' }],
}

const codeRules = {
  phone: [
    { required: true, message: '请输入手机号' },
    { pattern: /^1\d{10}$/, message: '请输入正确的手机号' },
  ],
  code: [{ required: true, message: '请输入验证码' }],
}

// 加载状态
const loading = ref(false)

// 记住我
const rememberMe = ref(false)

// 验证码倒计时
const codeCountdown = ref(0)

// 表单引用
const formRef = ref()
const codeFormRef = ref()

// 密码登录
const handleLogin = async () => {
  const valid = await formRef.value?.validate()
  if (!valid) return

  loading.value = true
  try {
    // 模拟登录请求
    await new Promise(resolve => setTimeout(resolve, 1000))
    MessagePlugin.success('登录成功')
    router.replace('/mobile/home')
  } catch (error) {
    MessagePlugin.error('登录失败，请检查用户名和密码')
  } finally {
    loading.value = false
  }
}

// 验证码登录
const handleCodeLogin = async () => {
  const valid = await codeFormRef.value?.validate()
  if (!valid) return

  loading.value = true
  try {
    // 模拟登录请求
    await new Promise(resolve => setTimeout(resolve, 1000))
    MessagePlugin.success('登录成功')
    router.replace('/mobile/home')
  } catch (error) {
    MessagePlugin.error('登录失败，请检查手机号和验证码')
  } finally {
    loading.value = false
  }
}

// 发送验证码
const sendCode = async () => {
  if (!codeFormData.value.phone) {
    MessagePlugin.warning('请先输入手机号')
    return
  }

  try {
    // 模拟发送验证码
    await new Promise(resolve => setTimeout(resolve, 500))
    MessagePlugin.success('验证码已发送')
    codeCountdown.value = 60
    const timer = setInterval(() => {
      codeCountdown.value--
      if (codeCountdown.value <= 0) {
        clearInterval(timer)
      }
    }, 1000)
  } catch (error) {
    MessagePlugin.error('发送验证码失败')
  }
}

// 忘记密码
const forgotPassword = () => {
  router.push('/mobile/forgot-password')
}

// 微信登录
const wechatLogin = () => {
  MessagePlugin.info('微信登录功能开发中')
}

// 支付宝登录
const alipayLogin = () => {
  MessagePlugin.info('支付宝登录功能开发中')
}

// 扫码登录
const scanLogin = () => {
  router.push('/mobile/scan')
}

// 注册
const register = () => {
  router.push('/mobile/register')
}

// 帮助中心
const help = () => {
  router.push('/mobile/help')
}

// 隐私政策
const privacy = () => {
  router.push('/mobile/privacy')
}
</script>

<style scoped lang="scss">
.mobile-login {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;

  .login-container {
    width: 100%;
    max-width: 400px;

    .logo-section {
      text-align: center;
      margin-bottom: 40px;

      .logo {
        width: 80px;
        height: 80px;
        margin: 0 auto 16px;
        background: white;
        border-radius: 20px;
        display: flex;
        align-items: center;
        justify-content: center;
        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);

        img {
          width: 50px;
          height: 50px;
        }
      }

      .title {
        font-size: 28px;
        font-weight: 700;
        color: white;
        margin-bottom: 8px;
      }

      .subtitle {
        font-size: 14px;
        color: rgba(255, 255, 255, 0.8);
      }
    }

    .login-form {
      background: white;
      border-radius: 16px;
      padding: 24px;
      box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);

      .login-tabs {
        :deep(.t-tabs__nav-item) {
          font-size: 16px;
        }
      }

      .form {
        margin-top: 24px;

        .form-actions {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 16px;
          font-size: 14px;
        }

        .t-button {
          margin-top: 8px;
        }
      }
    }

    .other-login {
      margin-top: 32px;

      .divider {
        text-align: center;
        position: relative;
        margin-bottom: 24px;

        &::before,
        &::after {
          content: '';
          position: absolute;
          top: 50%;
          width: 30%;
          height: 1px;
          background: rgba(255, 255, 255, 0.3);
        }

        &::before {
          left: 0;
        }

        &::after {
          right: 0;
        }

        span {
          background: transparent;
          padding: 0 12px;
          color: rgba(255, 255, 255, 0.8);
          font-size: 14px;
        }
      }

      .login-methods {
        display: flex;
        justify-content: center;
        gap: 32px;

        .method-item {
          width: 48px;
          height: 48px;
          background: white;
          border-radius: 50%;
          display: flex;
          align-items: center;
          justify-content: center;
          cursor: pointer;
          transition: transform 0.2s;

          &:hover {
            transform: scale(1.1);
          }

          img {
            width: 28px;
            height: 28px;
          }
        }
      }
    }

    .footer-links {
      margin-top: 32px;
      text-align: center;

      .t-link {
        color: rgba(255, 255, 255, 0.8);
        font-size: 14px;
      }
    }
  }
}
</style>
