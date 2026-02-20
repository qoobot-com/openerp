/**
 * 移动端待办列表
 * P6-MOB-T3: 移动端待办管理
 */
<template>
  <div class="mobile-todo-list">
    <!-- 顶部导航 -->
    <div class="page-header">
      <div class="header-left">
        <ChevronLeftIcon size="24px" @click="goBack" />
      </div>
      <div class="header-title">待办事项</div>
      <div class="header-right">
        <FilterIcon size="24px" @click="showFilter" />
        <AddIcon size="24px" @click="addTodo" />
      </div>
    </div>

    <!-- 筛选标签 -->
    <div class="filter-tabs">
      <div
        class="tab-item"
        v-for="tab in tabs"
        :key="tab.key"
        :class="{ active: activeTab === tab.key }"
        @click="switchTab(tab)"
      >
        {{ tab.label }}
        <div class="badge" v-if="tab.count > 0">{{ tab.count }}</div>
      </div>
    </div>

    <!-- 待办列表 -->
    <div class="todo-list">
      <div class="todo-item" v-for="todo in filteredTodos" :key="todo.id" @click="handleTodo(todo)">
        <div class="todo-left">
          <div class="todo-priority" :class="todo.priority"></div>
          <div class="todo-content">
            <div class="todo-title">{{ todo.title }}</div>
            <div class="todo-info">
              <span class="todo-type">{{ todo.type }}</span>
              <span class="todo-time">{{ todo.time }}</span>
            </div>
          </div>
        </div>
        <div class="todo-actions">
          <div class="action-btn" @click.stop="completeTodo(todo)">
            <CheckIcon size="20px" />
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div class="empty-state" v-if="filteredTodos.length === 0">
        <div class="empty-icon">
          <TimeIcon size="64px" />
        </div>
        <div class="empty-text">暂无待办事项</div>
      </div>
    </div>

    <!-- 加载更多 -->
    <div class="load-more" v-if="hasMore">
      <t-button variant="text" :loading="loading" @click="loadMore">
        {{ loading ? '加载中...' : '加载更多' }}
      </t-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { MessagePlugin } from 'tdesign-vue-next'
import {
  ChevronLeftIcon,
  FilterIcon,
  AddIcon,
  CheckIcon,
  TimeIcon,
} from 'tdesign-icons-vue-next'

const router = useRouter()

// 筛选标签
const tabs = ref([
  { key: 'all', label: '全部', count: 20 },
  { key: 'high', label: '重要', count: 5 },
  { key: 'pending', label: '待处理', count: 12 },
  { key: 'completed', label: '已完成', count: 3 },
])

const activeTab = ref('all')

// 待办列表
const todoList = ref([
  {
    id: 1,
    title: '审核新用户注册申请',
    type: '审批',
    time: '10分钟前',
    priority: 'high',
    status: 'pending',
  },
  {
    id: 2,
    title: '处理客户投诉反馈',
    type: '客户服务',
    time: '30分钟前',
    priority: 'high',
    status: 'pending',
  },
  {
    id: 3,
    title: '更新产品价格信息',
    type: '产品管理',
    time: '1小时前',
    priority: 'medium',
    status: 'pending',
  },
  {
    id: 4,
    title: '准备周报数据',
    type: '报表',
    time: '2小时前',
    priority: 'low',
    status: 'pending',
  },
  {
    id: 5,
    title: '安排团队会议',
    type: '会议',
    time: '3小时前',
    priority: 'medium',
    status: 'completed',
  },
])

// 加载状态
const loading = ref(false)
const hasMore = ref(true)

// 过滤后的待办列表
const filteredTodos = computed(() => {
  if (activeTab.value === 'all') {
    return todoList.value
  }
  if (activeTab.value === 'high') {
    return todoList.value.filter(t => t.priority === 'high')
  }
  if (activeTab.value === 'pending') {
    return todoList.value.filter(t => t.status === 'pending')
  }
  if (activeTab.value === 'completed') {
    return todoList.value.filter(t => t.status === 'completed')
  }
  return todoList.value
})

// 返回
const goBack = () => {
  router.back()
}

// 显示筛选
const showFilter = () => {
  MessagePlugin.info('筛选功能开发中')
}

// 添加待办
const addTodo = () => {
  router.push('/mobile/todo/create')
}

// 切换标签
const switchTab = (tab: any) => {
  activeTab.value = tab.key
}

// 处理待办
const handleTodo = (todo: any) => {
  router.push(`/mobile/todo/${todo.id}`)
}

// 完成待办
const completeTodo = (todo: any) => {
  const index = todoList.value.findIndex(t => t.id === todo.id)
  if (index !== -1) {
    todoList.value[index].status = 'completed'
    MessagePlugin.success('已完成')
  }
}

// 加载更多
const loadMore = async () => {
  loading.value = true
  try {
    await new Promise(resolve => setTimeout(resolve, 1000))
    // 模拟加载更多数据
    todoList.value.push(
      {
        id: Date.now(),
        title: '新待办事项',
        type: '任务',
        time: '刚刚',
        priority: 'medium',
        status: 'pending',
      }
    )
  } catch (error) {
    MessagePlugin.error('加载失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
.mobile-todo-list {
  min-height: 100vh;
  background: #f5f5f5;

  .page-header {
    background: white;
    padding: 12px 16px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);

    .header-left,
    .header-right {
      display: flex;
      align-items: center;
      gap: 16px;
    }

    .header-title {
      font-size: 18px;
      font-weight: 600;
    }
  }

  .filter-tabs {
    background: white;
    display: flex;
    padding: 0 16px;
    border-bottom: 1px solid #f0f0f0;

    .tab-item {
      flex: 1;
      text-align: center;
      padding: 16px 0;
      font-size: 14px;
      color: #666;
      position: relative;
      cursor: pointer;

      &.active {
        color: #0052d9;
        font-weight: 500;

        &::after {
          content: '';
          position: absolute;
          bottom: 0;
          left: 50%;
          transform: translateX(-50%);
          width: 20px;
          height: 3px;
          background: #0052d9;
          border-radius: 2px;
        }
      }

      .badge {
        position: absolute;
        top: 8px;
        right: 20%;
        min-width: 18px;
        height: 18px;
        background: #ff4d4f;
        color: white;
        font-size: 12px;
        line-height: 18px;
        text-align: center;
        border-radius: 9px;
        padding: 0 4px;
      }
    }
  }

  .todo-list {
    padding: 12px 16px;

    .todo-item {
      background: white;
      border-radius: 12px;
      padding: 16px;
      margin-bottom: 12px;
      display: flex;
      align-items: center;
      justify-content: space-between;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);

      .todo-left {
        display: flex;
        align-items: center;
        gap: 12px;
        flex: 1;

        .todo-priority {
          width: 4px;
          height: 48px;
          border-radius: 2px;

          &.high {
            background: #ff4d4f;
          }

          &.medium {
            background: #faad14;
          }

          &.low {
            background: #52c41a;
          }
        }

        .todo-content {
          flex: 1;

          .todo-title {
            font-size: 16px;
            color: #333;
            margin-bottom: 8px;
            line-height: 1.4;
          }

          .todo-info {
            display: flex;
            gap: 12px;
            font-size: 12px;
            color: #999;

            .todo-type {
              background: #f0f0f0;
              padding: 2px 8px;
              border-radius: 4px;
            }
          }
        }
      }

      .todo-actions {
        .action-btn {
          width: 40px;
          height: 40px;
          border-radius: 50%;
          background: #f0f0f0;
          display: flex;
          align-items: center;
          justify-content: center;
          color: #999;
          transition: all 0.2s;

          &:active {
            background: #e0e0e0;
            transform: scale(0.95);
          }
        }
      }
    }

    .empty-state {
      padding: 80px 0;
      text-align: center;

      .empty-icon {
        margin-bottom: 16px;
        color: #d9d9d9;
      }

      .empty-text {
        font-size: 14px;
        color: #999;
      }
    }
  }

  .load-more {
    text-align: center;
    padding: 16px;
  }
}
</style>
