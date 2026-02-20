<template>
  <div class="profile-page">
    <t-row :gutter="[16, 16]">
      <!-- 左侧信息 -->
      <t-col :span="6">
        <t-card :bordered="false" class="profile-card">
          <div class="user-info">
            <div class="avatar">
              <t-avatar :image="userInfo.avatar || ''" size="large">{{ userInfo.name?.charAt(0) }}</t-avatar>
              <t-button size="small" variant="text" @click="handleAvatarClick">
                <template #icon><t-icon name="edit" /></template>
              </t-button>
            </div>
            <div class="name">{{ userInfo.name }}</div>
            <div class="username">@{{ userInfo.username }}</div>
            <t-divider />
            <div class="info-item">
              <t-icon name="user" />
              <span>{{ userInfo.position }}</span>
            </div>
            <div class="info-item">
              <t-icon name="branch" />
              <span>{{ userInfo.deptName }}</span>
            </div>
            <div class="info-item">
              <t-icon name="phone" />
              <span>{{ userInfo.phone }}</span>
            </div>
            <div class="info-item">
              <t-icon name="email" />
              <span>{{ userInfo.email }}</span>
            </div>
          </div>
        </t-card>

        <!-- 操作菜单 -->
        <t-card :bordered="false" class="menu-card">
          <t-menu v-model:value="activeTab" theme="light" @change="handleTabChange">
            <t-menu-item value="basic">
              <template #icon><t-icon name="user" /></template>
              基本信息
            </t-menu-item>
            <t-menu-item value="password">
              <template #icon><t-icon name="lock-on" /></template>
              修改密码
            </t-menu-item>
            <t-menu-item value="notification">
              <template #icon><t-icon name="notification" /></template>
              消息通知
            </t-menu-item>
          </t-menu>
        </t-card>
      </t-col>

      <!-- 右侧内容 -->
      <t-col :span="18">
        <!-- 基本信息 -->
        <t-card v-if="activeTab === 'basic'" :bordered="false" title="基本信息">
          <t-form
            ref="formRef"
            :data="formData"
            label-width="120px"
            @submit="handleBasicSubmit"
          >
            <t-row :gutter="[16, 16]">
              <t-col :span="12">
                <t-form-item label="用户名">
                  <t-input v-model="formData.username" disabled />
                </t-form-item>
              </t-col>
              <t-col :span="12">
                <t-form-item label="姓名">
                  <t-input v-model="formData.name" />
                </t-form-item>
              </t-col>
              <t-col :span="12">
                <t-form-item label="手机号">
                  <t-input v-model="formData.phone" />
                </t-form-item>
              </t-col>
              <t-col :span="12">
                <t-form-item label="邮箱">
                  <t-input v-model="formData.email" />
                </t-form-item>
              </t-col>
              <t-col :span="24">
                <t-form-item label="个人简介">
                  <t-textarea
                    v-model="formData.bio"
                    placeholder="请输入个人简介"
                    :maxlength="200"
                  />
                </t-form-item>
              </t-col>
              <t-col :span="24">
                <t-form-item>
                  <t-space>
                    <t-button theme="primary" :loading="loading" @click="handleBasicSubmit">保存</t-button>
                    <t-button @click="handleReset">重置</t-button>
                  </t-space>
                </t-form-item>
              </t-col>
            </t-row>
          </t-form>
        </t-card>

        <!-- 修改密码 -->
        <t-card v-if="activeTab === 'password'" :bordered="false" title="修改密码">
          <t-form
            ref="passwordFormRef"
            :data="passwordData"
            :rules="passwordRules"
            label-width="120px"
            @submit="handlePasswordSubmit"
          >
            <t-form-item label="当前密码" name="oldPassword">
              <t-input
                v-model="passwordData.oldPassword"
                type="password"
                placeholder="请输入当前密码"
              />
            </t-form-item>
            <t-form-item label="新密码" name="newPassword">
              <t-input
                v-model="passwordData.newPassword"
                type="password"
                placeholder="请输入新密码"
              />
            </t-form-item>
            <t-form-item label="确认密码" name="confirmPassword">
              <t-input
                v-model="passwordData.confirmPassword"
                type="password"
                placeholder="请再次输入新密码"
              />
            </t-form-item>
            <t-form-item>
              <t-space>
                <t-button theme="primary" :loading="loading" @click="handlePasswordSubmit">提交</t-button>
                <t-button @click="handlePasswordReset">重置</t-button>
              </t-space>
            </t-form-item>
          </t-form>
        </t-card>

        <!-- 消息通知 -->
        <t-card v-if="activeTab === 'notification'" :bordered="false" title="消息通知">
          <t-list :split="true">
            <t-list-item v-for="item in notificationList" :key="item.id">
              <div class="notification-item">
                <div class="notification-content">
                  <t-tag v-if="item.type === 'system'" theme="primary">系统</t-tag>
                  <t-tag v-else-if="item.type === 'todo'" theme="warning">待办</t-tag>
                  <t-tag v-else theme="success">消息</t-tag>
                  <span class="title">{{ item.title }}</span>
                </div>
                <div class="notification-time">{{ item.time }}</div>
              </div>
            </t-list-item>
          </t-list>
        </t-card>
      </t-col>
    </t-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { MessagePlugin } from 'tdesign-vue-next'

const activeTab = ref('basic')
const loading = ref(false)
const formRef = ref()
const passwordFormRef = ref()

const userInfo = ref({
  avatar: '',
  username: 'admin',
  name: '管理员',
  position: '超级管理员',
  deptName: '技术部',
  phone: '13800138000',
  email: 'admin@qooerp.com',
})

const formData = reactive({
  username: 'admin',
  name: '管理员',
  phone: '13800138000',
  email: 'admin@qooerp.com',
  bio: '',
})

const passwordData = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: '',
})

const passwordRules = {
  oldPassword: [{ required: true, message: '请输入当前密码' }],
  newPassword: [
    { required: true, message: '请输入新密码' },
    { min: 6, message: '密码长度至少6位' },
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码' },
    {
      validator: (val: string) => val === passwordData.newPassword,
      message: '两次输入的密码不一致',
    },
  ],
}

const notificationList = ref([
  { id: 1, type: 'system', title: '系统将于今晚进行维护，请提前保存数据', time: '10分钟前' },
  { id: 2, type: 'todo', title: '您有待审批的申请需要处理', time: '30分钟前' },
  { id: 3, type: 'message', title: '张三给您发了一条消息', time: '1小时前' },
  { id: 4, type: 'system', title: '欢迎使用 QooERP 系统', time: '1天前' },
])

const handleTabChange = (value: string) => {
  activeTab.value = value
}

const handleBasicSubmit = async () => {
  loading.value = true
  try {
    await new Promise((resolve) => setTimeout(resolve, 500))
    MessagePlugin.success('保存成功')
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  Object.assign(formData, {
    username: 'admin',
    name: '管理员',
    phone: '13800138000',
    email: 'admin@qooerp.com',
    bio: '',
  })
}

const handlePasswordSubmit = async () => {
  const valid = await passwordFormRef.value?.validate()
  if (valid === true) {
    loading.value = true
    try {
      await new Promise((resolve) => setTimeout(resolve, 500))
      MessagePlugin.success('密码修改成功，请重新登录')
      handlePasswordReset()
    } finally {
      loading.value = false
    }
  }
}

const handlePasswordReset = () => {
  Object.assign(passwordData, {
    oldPassword: '',
    newPassword: '',
    confirmPassword: '',
  })
  passwordFormRef.value?.reset()
}

const handleAvatarClick = () => {
  MessagePlugin.info('头像上传功能开发中...')
}

onMounted(() => {
  Object.assign(formData, userInfo.value)
})
</script>

<style scoped lang="scss">
.profile-page {
  .profile-card {
    margin-bottom: 16px;

    .user-info {
      text-align: center;

      .avatar {
        position: relative;
        display: inline-block;
        margin-bottom: 16px;

        .t-button {
          position: absolute;
          right: -8px;
          bottom: 0;
          background: var(--td-brand-color);
          color: #fff;
        }
      }

      .name {
        font-size: 20px;
        font-weight: 600;
        margin-bottom: 4px;
      }

      .username {
        font-size: 14px;
        color: var(--td-text-color-secondary);
        margin-bottom: 16px;
      }

      .info-item {
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 8px;
        margin-bottom: 12px;
        font-size: 14px;
        color: var(--td-text-color-secondary);
      }
    }
  }

  .menu-card {
    :deep(.t-menu__item) {
      padding: 12px 16px;
      border-radius: 8px;
      margin-bottom: 4px;
    }
  }

  .notification-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: 100%;

    .notification-content {
      display: flex;
      align-items: center;
      gap: 8px;

      .title {
        font-size: 14px;
      }
    }

    .notification-time {
      font-size: 12px;
      color: var(--td-text-color-placeholder);
    }
  }
}
</style>
