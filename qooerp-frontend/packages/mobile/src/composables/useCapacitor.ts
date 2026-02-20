/**
 * Capacitor 核心 Composable
 * P6-CAP-T2: Capacitor API 封装
 */
import { ref, onMounted } from 'vue'
import { Capacitor } from '@capacitor/core'
import { App } from '@capacitor/app'
import { Device } from '@capacitor/device'

export interface UseCapacitorOptions {
  enableAppState?: boolean
  enableDeviceInfo?: boolean
}

export function useCapacitor(options: UseCapacitorOptions = {}) {
  const {
    enableAppState = true,
    enableDeviceInfo = true,
  } = options

  // 平台信息
  const platform = ref<string>(Capacitor.getPlatform())
  const isNative = ref<boolean>(Capacitor.isNativePlatform())
  const isWeb = ref<boolean>(Capacitor.isPluginAvailable('core'))

  // 应用状态
  const isActive = ref<boolean>(true)

  // 设备信息
  const deviceInfo = ref<any>(null)

  // 初始化
  const init = async () => {
    // 获取设备信息
    if (enableDeviceInfo) {
      try {
        deviceInfo.value = await Device.getInfo()
      } catch (error) {
        console.error('Failed to get device info:', error)
      }
    }

    // 监听应用状态
    if (enableAppState && isNative.value) {
      setupAppStateListeners()
    }
  }

  // 设置应用状态监听
  const setupAppStateListeners = () => {
    App.addListener('appStateChange', (state) => {
      isActive.value = state.isActive
    })
  }

  // 获取应用信息
  const getAppInfo = async () => {
    try {
      return await App.getInfo()
    } catch (error) {
      console.error('Failed to get app info:', error)
      return null
    }
  }

  // 获取应用状态
  const getAppState = async () => {
    try {
      return await App.getState()
    } catch (error) {
      console.error('Failed to get app state:', error)
      return null
    }
  }

  // 最小化应用
  const minimizeApp = async () => {
    if (isNative.value) {
      try {
        await App.minimizeApp()
      } catch (error) {
        console.error('Failed to minimize app:', error)
      }
    }
  }

  // 退出应用
  const exitApp = async () => {
    if (isNative.value && platform.value === 'android') {
      try {
        await App.exitApp()
      } catch (error) {
        console.error('Failed to exit app:', error)
      }
    }
  }

  // 启动 URL
  const launchUrl = async (url: string) => {
    try {
      return await App.launchUrl({ url })
    } catch (error) {
      console.error('Failed to launch URL:', error)
      return false
    }
  }

  // 检查网络状态
  const checkNetworkStatus = async () => {
    if (!isNative.value) {
      return navigator.onLine
    }

    try {
      const { Network } = await import('@capacitor/network')
      const status = await Network.getStatus()
      return status.connected
    } catch (error) {
      console.error('Failed to check network status:', error)
      return true
    }
  }

  // 打开浏览器
  const openBrowser = async (url: string) => {
    try {
      const { Browser } = await import('@capacitor/browser')
      await Browser.open({ url })
      return true
    } catch (error) {
      console.error('Failed to open browser:', error)
      // 降级方案
      window.open(url, '_blank')
      return false
    }
  }

  // 关闭浏览器
  const closeBrowser = async () => {
    if (!isNative.value) return

    try {
      const { Browser } = await import('@capacitor/browser')
      await Browser.close()
    } catch (error) {
      console.error('Failed to close browser:', error)
    }
  }

  // 分享
  const share = async (options: { title: string; text: string; url?: string }) => {
    try {
      const { Share } = await import('@capacitor/share')
      await Share.share(options)
      return true
    } catch (error) {
      console.error('Failed to share:', error)
      return false
    }
  }

  // 检查是否支持功能
  const isAvailable = (plugin: string): boolean => {
    return Capacitor.isPluginAvailable(plugin)
  }

  onMounted(() => {
    init()
  })

  return {
    // 状态
    platform,
    isNative,
    isWeb,
    isActive,
    deviceInfo,

    // 应用方法
    getAppInfo,
    getAppState,
    minimizeApp,
    exitApp,
    launchUrl,

    // 工具方法
    checkNetworkStatus,
    openBrowser,
    closeBrowser,
    share,
    isAvailable,
  }
}
