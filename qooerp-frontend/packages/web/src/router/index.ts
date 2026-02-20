import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import MainLayout from '@/components/layout/MainLayout.vue'

// 配置 NProgress
NProgress.configure({
  showSpinner: false,
  trickleSpeed: 200,
})

// 导入所有路由模块
const modules = import.meta.glob('./modules/*.ts', { eager: true })
const routes: RouteRecordRaw[] = Object.values(modules).flatMap(
  (module: any) => module.default || []
)

// 主布局路由
const layoutRoutes: RouteRecordRaw = {
  path: '/',
  component: MainLayout,
  children: [
    {
      path: '',
      name: 'Dashboard',
      component: () => import('@/views/dashboard/Dashboard.vue'),
      meta: {
        title: '仪表盘',
        requiresAuth: false,
        keepAlive: true,
      },
    },
    ...routes,
  ],
}

// 添加基础路由
const constantRoutes: RouteRecordRaw[] = [
  layoutRoutes,
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/Login.vue'),
    meta: {
      title: '登录',
      requiresAuth: false,
    },
  },
  {
    path: '/403',
    name: 'Forbidden',
    component: () => import('@/views/error/403.vue'),
    meta: {
      title: '无权限',
      requiresAuth: false,
    },
  },
  {
    path: '/404',
    name: 'NotFound',
    component: () => import('@/views/error/404.vue'),
    meta: {
      title: '页面不存在',
      requiresAuth: false,
    },
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes: [...constantRoutes, { path: '/:pathMatch(.*)*', redirect: '/404' }],
  scrollBehavior(_to, _from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    } else {
      return { top: 0 }
    }
  },
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 开始进度条
  NProgress.start()

  // 设置页面标题
  if (to.meta?.title) {
    document.title = `${to.meta.title} - QooERP`
  }

  // 检查是否需要认证
  const requiresAuth = to.meta?.requiresAuth !== false
  const token = localStorage.getItem('token')

  if (requiresAuth && !token) {
    // 需要认证但未登录,跳转到登录页
    next({
      path: '/login',
      query: { redirect: to.fullPath },
    })
    return
  }

  if (to.path === '/login' && token) {
    // 已登录访问登录页,跳转到首页
    next({ path: '/' })
    return
  }

  // 检查权限
  const permissions = to.meta?.permissions as string[]
  if (permissions && permissions.length > 0) {
    const userPermissions = JSON.parse(localStorage.getItem('permissions') || '[]')
    const hasPermission = permissions.some((perm) => userPermissions.includes(perm))
    if (!hasPermission) {
      console.warn('没有访问权限:', permissions)
      next('/403')
      return
    }
  }

  // 打印路由信息用于调试
  console.log('路由跳转:', to.path, to.meta)

  next()
})

router.afterEach((_to, _from) => {
  // 结束进度条
  NProgress.done()
})

export default router
