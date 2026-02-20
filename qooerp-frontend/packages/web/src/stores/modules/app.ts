import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAppStore = defineStore(
  'app',
  () => {
    // 状态
    const title = ref('QooERP')
    const collapsed = ref(false)
    const sidebarCollapsed = ref(false)
    const isMobile = ref(false)
    const theme = ref<'light' | 'dark'>('light')
    const language = ref('zh-CN')
    const breadcrumb = ref<any[]>([])
    const visitedViews = ref<any[]>([])
    const cachedViews = ref<Set<string>>(new Set())

    // Getters
    const getTheme = () => theme.value
    const getLanguage = () => language.value
    const getCollapsed = () => collapsed.value

    // 设置标题
    const setTitle = (newTitle: string) => {
      title.value = newTitle
      document.title = newTitle
    }

    // 切换侧边栏
    const toggleSidebar = () => {
      collapsed.value = !collapsed.value
    }

    const setCollapsed = (value: boolean) => {
      collapsed.value = value
    }

    // 切换主题
    const toggleTheme = () => {
      theme.value = theme.value === 'light' ? 'dark' : 'light'
      localStorage.setItem('theme', theme.value)
      document.documentElement.setAttribute('data-theme', theme.value)
    }

    const setTheme = (newTheme: 'light' | 'dark') => {
      theme.value = newTheme
      localStorage.setItem('theme', newTheme)
      document.documentElement.setAttribute('data-theme', newTheme)
    }

    // 切换语言
    const setLanguage = (lang: string) => {
      language.value = lang
      localStorage.setItem('language', lang)
    }

    // 面包屑
    const setBreadcrumb = (items: any[]) => {
      breadcrumb.value = items
    }

    // 标签页
    const addVisitedView = (view: any) => {
      if (visitedViews.value.some((v) => v.path === view.path)) return
      visitedViews.value.push(view)
    }

    const delVisitedView = (view: any) => {
      const index = visitedViews.value.findIndex((v) => v.path === view.path)
      if (index > -1) visitedViews.value.splice(index, 1)
    }

    const delAllVisitedViews = () => {
      visitedViews.value = []
    }

    const delOthersVisitedViews = (view: any) => {
      visitedViews.value = visitedViews.value.filter((v) => v.path === view.path)
    }

    // 缓存视图
    const addCachedView = (viewName: string) => {
      cachedViews.value.add(viewName)
    }

    const delCachedView = (viewName: string) => {
      cachedViews.value.delete(viewName)
    }

    const delAllCachedViews = () => {
      cachedViews.value.clear()
    }

    // 初始化
    const initApp = () => {
      console.log('初始化应用...')

      // 从本地存储恢复主题
      const savedTheme = localStorage.getItem('theme') as 'light' | 'dark'
      if (savedTheme) {
        theme.value = savedTheme
        document.documentElement.setAttribute('data-theme', savedTheme)
        console.log('恢复主题:', savedTheme)
      }

      // 从本地存储恢复语言
      const savedLang = localStorage.getItem('language')
      if (savedLang) {
        language.value = savedLang
        console.log('恢复语言:', savedLang)
      }

      console.log('应用初始化完成')
    }

    return {
      // 状态
      title,
      collapsed,
      sidebarCollapsed,
      isMobile,
      theme,
      language,
      breadcrumb,
      visitedViews,
      cachedViews,

      // Getters
      getTheme,
      getLanguage,
      getCollapsed,

      // Actions
      setTitle,
      toggleSidebar,
      setCollapsed,
      toggleTheme,
      setTheme,
      setLanguage,
      setBreadcrumb,
      addVisitedView,
      delVisitedView,
      delAllVisitedViews,
      delOthersVisitedViews,
      addCachedView,
      delCachedView,
      delAllCachedViews,
      initApp,
    }
  },
  {
    // 持久化配置
    persist: {
      key: 'qooerp-app',
      storage: localStorage,
      paths: ['theme', 'language', 'collapsed'],
    },
  }
)
