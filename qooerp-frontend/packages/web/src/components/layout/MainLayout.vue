<template>
  <div class="main-layout" :class="{ collapsed: appStore.collapsed, 'dark-theme': appStore.theme === 'dark' }">
    <!-- 侧边栏 -->
    <Sidebar ref="sidebarRef" />

    <!-- 主内容区 -->
    <div class="main-container">
      <!-- 顶部导航栏 -->
      <header class="header">
        <div class="header-left">
          <t-button
            theme="default"
            variant="text"
            @click="appStore.toggleSidebar"
          >
            <template #icon>
              <t-icon :name="appStore.collapsed ? 'menu-fold' : 'menu-unfold'" />
            </template>
          </t-button>
          <t-breadcrumb>
            <t-breadcrumb-item
              v-for="(item, index) in appStore.breadcrumb"
              :key="index"
              :to="item.path"
            >
              {{ item.title }}
            </t-breadcrumb-item>
          </t-breadcrumb>
        </div>
        <div class="header-right">
          <!-- 主题切换 -->
          <t-tooltip content="切换主题">
            <t-button theme="default" variant="text" @click="appStore.toggleTheme">
              <template #icon>
                <t-icon :name="appStore.theme === 'light' ? 'mode-dark' : 'mode-light'" />
              </template>
            </t-button>
          </t-tooltip>

          <!-- 全屏 -->
          <t-tooltip content="全屏">
            <t-button theme="default" variant="text" @click="toggleFullscreen">
              <template #icon>
                <t-icon name="fullscreen" />
              </template>
            </t-button>
          </t-tooltip>

          <!-- 消息通知 -->
          <t-dropdown :options="messageOptions" @click="handleMessageClick">
            <t-badge :count="unreadCount" :max="99">
              <t-button theme="default" variant="text">
                <template #icon>
                  <t-icon name="notification" />
                </template>
              </t-button>
            </t-badge>
          </t-dropdown>

          <!-- 用户信息 -->
          <t-dropdown :options="userOptions" @click="handleUserClick">
            <div class="user-info">
              <t-avatar :image="userStore.userInfo?.avatar" size="32px">
                {{ userStore.userInfo?.realName?.[0] || userStore.userInfo?.username?.[0] || 'U' }}
              </t-avatar>
              <span class="user-name">{{ userStore.userInfo?.realName || userStore.userInfo?.username }}</span>
            </div>
          </t-dropdown>
        </div>
      </header>

      <!-- 标签页 -->
      <div v-if="showTabs" class="tabs-view">
        <t-tabs v-model:active="activeTab" @remove="handleTabRemove">
          <t-tab-panel
            v-for="tab in appStore.visitedViews"
            :key="tab.path"
            :value="tab.path"
            :closable="appStore.visitedViews.length > 1"
            :label="tab.title"
          />
        </t-tabs>
      </div>

      <!-- 页面内容 -->
      <main class="main-content">
        <router-view v-slot="{ Component }">
          <keep-alive :include="Array.from(appStore.cachedViews)">
            <component :is="Component" :key="$route.path" />
          </keep-alive>
        </router-view>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAppStore } from '@/stores/modules/app'
import { useUserStore } from '@/stores/modules/user'
import Sidebar from './Sidebar.vue'

// Stores
const appStore = useAppStore()
const userStore = useUserStore()

// Router
const route = useRoute()
const router = useRouter()

// 状态
const activeTab = ref(route.path)
const showTabs = ref(true)
const unreadCount = ref(3)

// 消息下拉选项
const messageOptions = [
  { content: '查看全部消息', value: 'all' },
  { content: '标记已读', value: 'read' },
  { content: '清空消息', value: 'clear' },
]

// 用户下拉选项
const userOptions = [
  { content: '个人中心', value: 'profile' },
  { content: '修改密码', value: 'password' },
  { divider: true },
  { content: '退出登录', value: 'logout' },
]

// 方法
const handleMenuChange = (value: string) => {
  router.push(value)
}

const handleMessageClick = (value: string) => {
  console.log('消息操作:', value)
}

const handleUserClick = async (value: string) => {
  if (value === 'logout') {
    await userStore.logout()
    router.push('/login')
  } else if (value === 'profile') {
    router.push('/user/profile')
  } else if (value === 'password') {
    router.push('/user/password')
  }
}

const handleTabRemove = (value: string) => {
  const tab = appStore.visitedViews.find((v) => v.path === value)
  if (tab) {
    appStore.delVisitedView(tab)
    if (activeTab.value === value) {
      const lastTab = appStore.visitedViews[appStore.visitedViews.length - 1]
      if (lastTab) {
        router.push(lastTab.path)
      }
    }
  }
}

const toggleFullscreen = () => {
  if (!document.fullscreenElement) {
    document.documentElement.requestFullscreen()
  } else {
    document.exitFullscreen()
  }
}

// 监听路由变化
watch(
  () => route.path,
  (newPath) => {
    activeTab.value = newPath

    // 更新面包屑
    const matched = route.matched.filter((item) => item.meta && item.meta.title)
    const breadcrumb = matched.map((item) => ({
      path: item.path,
      title: item.meta?.title,
    }))
    appStore.setBreadcrumb(breadcrumb)

    // 添加标签页
    if (route.meta?.title) {
      appStore.addVisitedView({
        path: route.path,
        title: route.meta.title as string,
      })
      if (route.meta?.keepAlive) {
        appStore.addCachedView(route.name as string)
      }
    }
  },
  { immediate: true }
)

// 初始化
onMounted(() => {
  appStore.initApp()
  userStore.initUser()
})
</script>

<style scoped lang="scss">
.main-layout {
  display: flex;
  height: 100vh;
  width: 100%;

  .main-container {
    flex: 1;
    display: flex;
    flex-direction: column;
    overflow: hidden;
  }

  .header {
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 20px;
    background: #fff;
    border-bottom: 1px solid var(--td-border-level-1-color);

    .header-left {
      display: flex;
      align-items: center;
      gap: 12px;
    }

    .header-right {
      display: flex;
      align-items: center;
      gap: 8px;
    }

    .user-info {
      display: flex;
      align-items: center;
      gap: 8px;
      cursor: pointer;
      padding: 4px 8px;
      border-radius: 4px;
      transition: background-color 0.2s;

      &:hover {
        background-color: var(--td-bg-color-container-hover);
      }

      .user-name {
        font-size: 14px;
      }
    }
  }

  .tabs-view {
    height: 44px;
    background: #f5f5f5;
    border-bottom: 1px solid var(--td-border-level-1-color);
    padding: 0 16px;
    display: flex;
    align-items: center;

    :deep(.t-tabs) {
      width: 100%;

      .t-tabs__nav {
        border: none;
      }
    }
  }

  .main-content {
    flex: 1;
    overflow: auto;
    padding: 20px;
    background: #f5f5f5;
  }
}

.dark-theme {
  .header {
    background: #1f1f1f;
    border-color: #333;
  }

  .tabs-view,
  .main-content {
    background: #141414;
  }
}
</style>
