<template>
  <div class="sidebar" :class="{ 'is-collapsed': isCollapsed, 'is-mobile': isMobile }">
    <!-- Logo 区域 -->
    <div class="sidebar-logo">
      <div class="logo-wrapper">
        <svg class="logo-icon" viewBox="0 0 24 24" fill="none">
          <path d="M12 2L2 7L12 12L22 7L12 2Z" fill="currentColor" />
          <path d="M2 17L12 22L22 17" stroke="currentColor" stroke-width="2" />
          <path d="M2 12L12 17L22 12" stroke="currentColor" stroke-width="2" />
        </svg>
        <span v-if="!isCollapsed" class="logo-text">QooERP</span>
      </div>
    </div>

    <!-- 菜单区域 -->
    <div class="sidebar-menu">
      <t-menu
        v-model:value="activeMenu"
        :collapsed="isCollapsed"
        :theme="theme"
        @change="handleMenuChange"
      >
        <template v-for="menu in menuList" :key="menu.path">
          <!-- 有子菜单 -->
          <t-submenu v-if="menu.children && menu.children.length > 0" :value="menu.path">
            <template #icon>
              <component :is="menu.icon" v-if="menu.icon" />
            </template>
            <template #title>
              <span>{{ $t(menu.i18nKey || menu.title) }}</span>
            </template>
            <t-menu-item
              v-for="submenu in menu.children"
              :key="submenu.path"
              :value="submenu.path"
              :to="submenu.path"
            >
              <template #icon>
                <component :is="submenu.icon" v-if="submenu.icon" />
              </template>
              <span>{{ $t(submenu.i18nKey || submenu.title) }}</span>
            </t-menu-item>
          </t-submenu>

          <!-- 无子菜单 -->
          <t-menu-item v-else :value="menu.path" :to="menu.path">
            <template #icon>
              <component :is="menu.icon" v-if="menu.icon" />
            </template>
            <span>{{ $t(menu.i18nKey || menu.title) }}</span>
          </t-menu-item>
        </template>
      </t-menu>
    </div>

    <!-- 底部折叠按钮 -->
    <div class="sidebar-footer">
      <t-tooltip :content="isCollapsed ? $t('layout.expand') : $t('layout.collapse')" placement="right">
        <t-button
          shape="square"
          variant="text"
          @click="toggleCollapse"
        >
          <template #icon>
            <ChevronLeftIcon v-if="!isCollapsed" />
            <ChevronRightIcon v-else />
          </template>
        </t-button>
      </t-tooltip>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useAppStore } from '@/stores/modules/app';
import {
  DashboardIcon,
  SettingIcon,
  UsergroupIcon,
  UserIcon,
  HomeIcon,
  DesktopIcon,
  ShopIcon,
  FileCopyIcon,
  FolderIcon,
  Edit1Icon,
  ChartAnalysisIcon,
  FileIcon,
  NotificationIcon,
  CheckCircleIcon,
  ChevronLeftIcon,
  ChevronRightIcon,
} from 'tdesign-icons-vue-next';

interface MenuItem {
  path: string;
  title: string;
  i18nKey?: string;
  icon?: any;
  children?: MenuItem[];
}

const route = useRoute();
const router = useRouter();
const appStore = useAppStore();

const isCollapsed = computed(() => appStore.sidebarCollapsed);
const isMobile = computed(() => appStore.isMobile);
const theme = computed(() => appStore.theme === 'dark' ? 'dark' : 'light');

// 当前激活的菜单
const activeMenu = ref(route.path);

// 监听路由变化更新激活菜单
watch(
  () => route.path,
  (path) => {
    activeMenu.value = path;
  }
);

// 菜单列表
const menuList = ref<MenuItem[]>([
  {
    path: '/',
    title: 'menu.dashboard',
    i18nKey: 'menu.dashboard',
    icon: DashboardIcon,
  },
  {
    path: '/system',
    title: 'menu.system',
    i18nKey: 'menu.system',
    icon: SettingIcon,
    children: [
      { path: '/system/user', title: '用户管理', icon: UserIcon },
      { path: '/system/role', title: '角色管理', icon: UsergroupIcon },
      { path: '/system/permission', title: '权限管理', icon: CheckCircleIcon },
      { path: '/system/dict', title: '字典管理', icon: FileCopyIcon },
      { path: '/system/config', title: '系统配置', icon: SettingIcon },
    ],
  },
  {
    path: '/organization',
    title: 'menu.organization',
    i18nKey: 'menu.organization',
    icon: HomeIcon,
    children: [
      { path: '/organization/department', title: '部门管理', icon: UsergroupIcon },
      { path: '/organization/employee', title: '员工管理', icon: UserIcon },
      { path: '/organization/position', title: '岗位管理', icon: SettingIcon },
    ],
  },
  {
    path: '/workflow',
    title: 'menu.workflow',
    i18nKey: 'menu.workflow',
    icon: Edit1Icon,
  },
  {
    path: '/report',
    title: 'menu.report',
    i18nKey: 'menu.report',
    icon: ChartAnalysisIcon,
  },
  {
    path: '/file',
    title: 'menu.file',
    i18nKey: 'menu.file',
    icon: FolderIcon,
  },
  {
    path: '/message',
    title: 'menu.message',
    i18nKey: 'menu.message',
    icon: NotificationIcon,
  },
]);

// 菜单切换处理
const handleMenuChange = (value: string) => {
  router.push(value);
};

// 切换折叠状态
const toggleCollapse = () => {
  appStore.toggleSidebar();
};

// 暴露方法
defineExpose({
  menuList,
  activeMenu,
});
</script>

<style scoped lang="scss">
.sidebar {
  display: flex;
  flex-direction: column;
  width: 240px;
  height: 100%;
  background-color: var(--td-bg-color-container);
  border-right: 1px solid var(--td-border-level-1-color);
  transition: width 0.3s ease;

  &.is-collapsed {
    width: 64px;
  }

  &.is-mobile {
    position: fixed;
    left: 0;
    top: 0;
    z-index: 1000;
    box-shadow: 2px 0 8px rgba(0, 0, 0, 0.15);
  }

  .sidebar-logo {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 60px;
    border-bottom: 1px solid var(--td-border-level-1-color);

    .logo-wrapper {
      display: flex;
      align-items: center;
      gap: 12px;

      .logo-icon {
        width: 32px;
        height: 32px;
        color: var(--td-brand-color);
      }

      .logo-text {
        font-size: 20px;
        font-weight: 600;
        color: var(--td-brand-color);
        white-space: nowrap;
      }
    }
  }

  .sidebar-menu {
    flex: 1;
    overflow-y: auto;
    overflow-x: hidden;

    :deep(.t-menu) {
      .t-menu-item {
        margin: 4px 8px;
        border-radius: 4px;
        transition: all 0.2s;

        &:hover {
          background-color: var(--td-bg-color-container-hover);
        }

        &.t-is-active {
          background-color: var(--td-brand-color-light);
          color: var(--td-brand-color);
          font-weight: 500;
        }
      }

      .t-submenu {
        .t-submenu__title {
          margin: 4px 8px;
          border-radius: 4px;
          transition: all 0.2s;

          &:hover {
            background-color: var(--td-bg-color-container-hover);
          }
        }
      }
    }
  }

  .sidebar-footer {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 48px;
    border-top: 1px solid var(--td-border-level-1-color);
  }
}

// 暗色主题
:root[data-theme='dark'] {
  .sidebar {
    background-color: var(--td-dark-bg-color-container);
    border-right-color: var(--td-dark-border-level-1-color);

    .sidebar-logo {
      border-bottom-color: var(--td-dark-border-level-1-color);
    }

    .sidebar-footer {
      border-top-color: var(--td-dark-border-level-1-color);
    }
  }
}
</style>
