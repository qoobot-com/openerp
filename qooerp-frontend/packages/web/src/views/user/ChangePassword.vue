<template>
  <div class="change-password-page">
    <t-card title="修改密码" :bordered="false" style="max-width: 600px">
      <t-form
        ref="formRef"
        :data="formData"
        :rules="rules"
        label-width="120px"
        @submit="handleSubmit"
      >
        <t-form-item label="当前密码" name="oldPassword">
          <t-input
            v-model="formData.oldPassword"
            type="password"
            placeholder="请输入当前密码"
            clearable
          />
        </t-form-item>

        <t-form-item label="新密码" name="newPassword">
          <t-input
            v-model="formData.newPassword"
            type="password"
            placeholder="请输入新密码（6-20位）"
            clearable
          />
        </t-form-item>

        <t-form-item label="确认密码" name="confirmPassword">
          <t-input
            v-model="formData.confirmPassword"
            type="password"
            placeholder="请再次输入新密码"
            clearable
          />
        </t-form-item>

        <t-form-item>
          <t-space>
            <t-button theme="primary" :loading="loading" @click="handleSubmit">提交</t-button>
            <t-button @click="handleReset">重置</t-button>
          </t-space>
        </t-form-item>
      </t-form>

      <t-divider />

      <div class="password-tips">
        <div class="tip-title">密码安全建议：</div>
        <ul class="tip-list">
          <li>密码长度不少于 6 位</li>
          <li>建议使用字母、数字、符号组合</li>
          <li>定期更换密码，提高安全性</li>
          <li>不要使用与账号相同的密码</li>
        </ul>
      </div>
    </t-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { MessagePlugin } from 'tdesign-vue-next'

const router = useRouter()
const formRef = ref()
const loading = ref(false)

const formData = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: '',
})

const rules = {
  oldPassword: [
    { required: true, message: '请输入当前密码', type: 'error' },
    { min: 6, message: '密码长度不能少于6位', type: 'warning' },
  ],
  newPassword: [
    { required: true, message: '请输入新密码', type: 'error' },
    { min: 6, message: '密码长度不能少于6位', type: 'warning' },
    { max: 20, message: '密码长度不能超过20位', type: 'warning' },
    {
      validator: (val: string) => val !== formData.oldPassword,
      message: '新密码不能与当前密码相同',
      type: 'error',
    },
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', type: 'error' },
    {
      validator: (val: string) => val === formData.newPassword,
      message: '两次输入的密码不一致',
      type: 'error',
    },
  ],
}

const handleSubmit = async () => {
  const valid = await formRef.value?.validate()
  if (valid === true) {
    loading.value = true
    try {
      await new Promise((resolve) => setTimeout(resolve, 500))
      MessagePlugin.success('密码修改成功，请重新登录')
      // 清除登录信息
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      // 跳转到登录页
      setTimeout(() => {
        router.push('/login')
      }, 1000)
    } catch (error) {
      MessagePlugin.error('密码修改失败，请重试')
    } finally {
      loading.value = false
    }
  }
}

const handleReset = () => {
  Object.assign(formData, {
    oldPassword: '',
    newPassword: '',
    confirmPassword: '',
  })
  formRef.value?.reset()
}
</script>

<style scoped lang="scss">
.change-password-page {
  display: flex;
  justify-content: center;
  padding: 40px 0;

  .password-tips {
    margin-top: 24px;
    padding: 16px;
    background: var(--td-bg-color-container-hover);
    border-radius: 8px;

    .tip-title {
      font-size: 14px;
      font-weight: 600;
      color: var(--td-text-color-primary);
      margin-bottom: 8px;
    }

    .tip-list {
      margin: 0;
      padding-left: 20px;
      font-size: 13px;
      color: var(--td-text-color-secondary);

      li {
        margin-bottom: 4px;
      }
    }
  }
}
</style>
