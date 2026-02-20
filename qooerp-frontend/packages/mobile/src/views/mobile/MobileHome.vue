/**
 * 移动端首页
 * P6-MOB-T1: 移动端首页
 */
<template>
  <div class="mobile-home">
    <!-- 首页头部 -->
    <div class="home-header">
      <div class="user-info">
        <div class="avatar">
          <img src="/avatar.png" alt="avatar" v-if="userInfo.avatar" />
          <UserIcon v-else size="40px" />
        </div>
        <div class="info">
          <div class="welcome">欢迎回来</div>
          <div class="name">{{ userInfo.name || '用户' }}</div>
        </div>
      </div>
      <div class="header-actions">
        <div class="action-item" @click="showNotifications">
          <Badge :count="notificationCount" :offset="[0, 0]">
            <NotificationIcon size="24px" />
          </Badge>
        </div>
        <div class="action-item" @click="scanQRCode">
          <ScanIcon size="24px" />
        </div>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-section">
      <div class="stats-scroll">
        <div class="stat-card" v-for="stat in stats" :key="stat.key">
          <div class="stat-icon" :style="{ background: stat.color }">
            <component :is="stat.icon" />
          </div>
          <div class="stat-value">{{ stat.value }}</div>
          <div class="stat-label">{{ stat.label }}</div>
        </div>
      </div>
    </div>

    <!-- 快捷入口 -->
    <div class="quick-actions">
      <div class="section-title">快捷入口</div>
      <div class="actions-grid">
        <div class="action-item" v-for="action in quickActions" :key="action.key" @click="handleAction(action)">
          <div class="action-icon" :style="{ background: action.color }">
            <component :is="action.icon" />
          </div>
          <div class="action-label">{{ action.label }}</div>
        </div>
      </div>
    </div>

    <!-- 待办事项 -->
    <div class="todo-section">
      <div class="section-header">
        <div class="section-title">待办事项</div>
        <div class="view-all" @click="viewAllTodos">查看全部</div>
      </div>
      <div class="todo-list">
        <div class="todo-item" v-for="todo in todoList" :key="todo.id" @click="handleTodo(todo)">
          <div class="todo-icon" :class="todo.priority">
            <component :is="todo.priority === 'high' ? 'TimeFilledIcon' : 'TimeIcon'" />
          </div>
          <div class="todo-content">
            <div class="todo-title">{{ todo.title }}</div>
            <div class="todo-time">{{ todo.time }}</div>
          </div>
          <ChevronRightIcon size="20px" />
        </div>
      </div>
    </div>

    <!-- 底部导航 -->
    <div class="bottom-nav">
      <div
        class="nav-item"
        v-for="nav in bottomNav"
        :key="nav.key"
        :class="{ active: activeNav === nav.key }"
        @click="switchNav(nav)"
      >
        <component :is="nav.icon" size="24px" />
        <span>{{ nav.label }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import {
  UserIcon,
  NotificationIcon,
  ScanIcon,
  UsergroupIcon,
  FileIcon,
  CheckIcon,
  ChartIcon,
  SettingIcon,
  TimeIcon,
  TimeFilledIcon,
  ChevronRightIcon,
  HomeIcon,
  MessageIcon,
  AppsIcon,
} from 'tdesign-icons-vue-next'
import { Badge } from 'tdesign-vue-next'

const router = useRouter()

// 用户信息
const userInfo = ref({
  name: '',
  avatar: '',
})

// 通知数量
const notificationCount = ref(3)

// 统计数据
const stats = ref([
  { key: 'users', label: '用户数', value: '1,234', icon: UsergroupIcon, color: '#e7f4ff' },
  { key: 'orders', label: '订单', value: '567', icon: FileIcon, color: '#fff0e7' },
  { key: 'tasks', label: '待办', value: '89', icon: CheckIcon, color: '#e8f7ed' },
  { key: 'revenue', label: '收入', value: '¥12.3K', icon: ChartIcon, color: '#fef0e7' },
])

// 快捷入口
const quickActions = ref([
  { key: 'approval', label: '审批', icon: CheckIcon, color: '#e7f4ff' },
  { key: 'report', label: '报表', icon: ChartIcon, color: '#fff0e7' },
  { key: 'contact', label: '通讯录', icon: UsergroupIcon, color: '#e8f7ed' },
  { key: 'settings', label: '设置', icon: SettingIcon, color: '#fef0e7' },
])

// 待办事项
const todoList = ref([
  { id: 1, title: '审核新用户注册申请', time: '10分钟前', priority: 'high' },
  { id: 2, title: '处理客户投诉反馈', time: '30分钟前', priority: 'high' },
  { id: 3, title: '更新产品价格信息', time: '1小时前', priority: 'medium' },
  { id: 4, title: '准备周报数据', time: '2小时前', priority: 'low' },
])

// 底部导航
const bottomNav = ref([
  { key: 'home', label: '首页', icon: HomeIcon },
  { key: 'message', label: '消息', icon: MessageIcon },
  { key: 'apps', label: '应用', icon: AppsIcon },
  { key: 'mine', label: '我的', icon: UserIcon },
])

// 当前激活的导航
const activeNav = ref('home')

// 显示通知
const showNotifications = () => {
  router.push('/mobile/notifications')
}

// 扫码
const scanQRCode = () => {
  router.push('/mobile/scan')
}

// 快捷操作
const handleAction = (action: any) => {
  router.push(`/mobile/${action.key}`)
}

// 处理待办事项
const handleTodo = (todo: any) => {
  router.push(`/mobile/todo/${todo.id}`)
}

// 查看全部待办
const viewAllTodos = () => {
  router.push('/mobile/todos')
}

// 切换导航
const switchNav = (nav: any) => {
  activeNav.value = nav.key
  router.push(`/mobile/${nav.key}`)
}
</script>

<style scoped lang="scss">
.mobile-home {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 60px;

  .home-header {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    padding: 20px 16px;
    color: white;
    display: flex;
    justify-content: space-between;
    align-items: center;

    .user-info {
      display: flex;
      align-items: center;
      gap: 12px;

      .avatar {
        width: 48px;
        height: 48px;
        border-radius: 50%;
        background: rgba(255, 255, 255, 0.2);
        display: flex;
        align-items: center;
        justify-content: center;
        overflow: hidden;

        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }
      }

      .info {
        .welcome {
          font-size: 12px;
          opacity: 0.9;
        }

        .name {
          font-size: 18px;
          font-weight: 600;
        }
      }
    }

    .header-actions {
      display: flex;
      gap: 16px;

      .action-item {
        display: flex;
        align-items: center;
        justify-content: center;
        width: 40px;
        height: 40px;
        border-radius: 50%;
        background: rgba(255, 255, 255, 0.2);
      }
    }
  }

  .stats-section {
    margin: 16px 0;

    .stats-scroll {
      display: flex;
      overflow-x: auto;
      gap: 12px;
      padding: 0 16px;

      &::-webkit-scrollbar {
        display: none;
      }

      .stat-card {
        flex-shrink: 0;
        width: 120px;
        background: white;
        border-radius: 12px;
        padding: 16px;
        text-align: center;

        .stat-icon {
          width: 48px;
          height: 48px;
          border-radius: 12px;
          display: flex;
          align-items: center;
          justify-content: center;
          margin: 0 auto 12px;
          font-size: 24px;
          color: #0052d9;
        }

        .stat-value {
          font-size: 20px;
          font-weight: 600;
          color: #333;
          margin-bottom: 4px;
        }

        .stat-label {
          font-size: 12px;
          color: #999;
        }
      }
    }
  }

  .quick-actions {
    background: white;
    margin: 0 16px 16px;
    padding: 16px;
    border-radius: 12px;

    .section-title {
      font-size: 16px;
      font-weight: 600;
      color: #333;
      margin-bottom: 16px;
    }

    .actions-grid {
      display: grid;
      grid-template-columns: repeat(4, 1fr);
      gap: 16px;

      .action-item {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 8px;

        .action-icon {
          width: 48px;
          height: 48px;
          border-radius: 12px;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 24px;
          color: #0052d9;
        }

        .action-label {
          font-size: 12px;
          color: #666;
        }
      }
    }
  }

  .todo-section {
    background: white;
    margin: 0 16px;
    padding: 16px;
    border-radius: 12px;

    .section-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;

      .section-title {
        font-size: 16px;
        font-weight: 600;
        color: #333;
      }

      .view-all {
        font-size: 14px;
        color: #0052d9;
      }
    }

    .todo-list {
      .todo-item {
        display: flex;
        align-items: center;
        gap: 12px;
        padding: 12px 0;
        border-bottom: 1px solid #f0f0f0;

        &:last-child {
          border-bottom: none;
        }

        .todo-icon {
          width: 36px;
          height: 36px;
          border-radius: 8px;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 20px;

          &.high {
            background: #ffece6;
            color: #ff4d4f;
          }

          &.medium {
            background: #fff7e6;
            color: #faad14;
          }

          &.low {
            background: #f6ffed;
            color: #52c41a;
          }
        }

        .todo-content {
          flex: 1;

          .todo-title {
            font-size: 14px;
            color: #333;
            margin-bottom: 4px;
          }

          .todo-time {
            font-size: 12px;
            color: #999;
          }
        }
      }
    }
  }

  .bottom-nav {
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    background: white;
    display: flex;
    justify-content: space-around;
    padding: 8px 0;
    box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.05);

    .nav-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 4px;
      color: #999;
      font-size: 12px;

      &.active {
        color: #0052d9;
      }
    }
  }
}
</style>
