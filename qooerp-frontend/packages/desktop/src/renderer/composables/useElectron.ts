/**
 * Electron 专用 Composable
 * P6-ELEC-T4: Electron API 封装
 */
import { ref, onMounted, onUnmounted } from 'vue'
import { MessagePlugin } from 'tdesign-vue-next'

export interface UseElectronOptions {
  enableAutoUpdate?: boolean
  enableNotification?: boolean
  enableTray?: boolean
}

export function useElectron(options: UseElectronOptions = {}) {
  const {
    enableAutoUpdate = true,
    enableNotification = true,
    enableTray = true,
  } = options

  // 是否在 Electron 环境
  const isElectron = ref<boolean>(false)

  // 应用信息
  const appVersion = ref<string>('')
  const appPath = ref<Record<string, string>>({})

  // 更新状态
  const updateAvailable = ref(false)
  const updateInfo = ref<any>(null)
  const updateProgress = ref<number>(0)
  const isUpdating = ref(false)

  // 初始化
  const init = async () => {
    isElectron.value = !!(window as any).electronAPI

    if (!isElectron.value) {
      return
    }

    const api = (window as any).electronAPI

    // 获取应用信息
    try {
      appVersion.value = await api.app.getVersion()
      appPath.value = {
        home: await api.app.getPath('home'),
        documents: await api.app.getPath('documents'),
        downloads: await api.app.getPath('downloads'),
      }
    } catch (error) {
      console.error('Failed to get app info:', error)
    }

    // 设置更新监听
    if (enableAutoUpdate) {
      setupUpdateListeners()
    }
  }

  // 设置更新监听器
  const setupUpdateListeners = () => {
    if (!isElectron.value) return

    const api = (window as any).electronAPI

    api.update.onChecking(() => {
      console.log('Checking for updates...')
    })

    api.update.onAvailable((info: any) => {
      updateAvailable.value = true
      updateInfo.value = info
      MessagePlugin.success(`发现新版本: ${info.version}`)
    })

    api.update.onNotAvailable(() => {
      updateAvailable.value = false
      updateInfo.value = null
    })

    api.update.onDownloading((progress: any) => {
      updateProgress.value = progress.percent || 0
      isUpdating.value = true
    })

    api.update.onDownloaded((info: any) => {
      isUpdating.value = false
      updateProgress.value = 100
      updateInfo.value = info
      MessagePlugin.success('更新已下载完成，请重启应用')
    })
  }

  // 清理更新监听器
  const cleanupUpdateListeners = () => {
    if (!isElectron.value) return
    const api = (window as any).electronAPI
    api.update.removeAllListeners()
  }

  // 窗口操作
  const minimizeWindow = () => {
    if (isElectron.value) {
      window.electronAPI.window.minimize()
    }
  }

  const maximizeWindow = async () => {
    if (isElectron.value) {
      await window.electronAPI.window.maximize()
    }
  }

  const unmaximizeWindow = async () => {
    if (isElectron.value) {
      await window.electronAPI.window.unmaximize()
    }
  }

  const toggleMaximize = async () => {
    if (isElectron.value) {
      const maximized = await window.electronAPI.window.isMaximized()
      if (maximized) {
        await window.electronAPI.window.unmaximize()
      } else {
        await window.electronAPI.window.maximize()
      }
    }
  }

  const closeWindow = () => {
    if (isElectron.value) {
      window.electronAPI.window.close()
    }
  }

  const isMaximized = async () => {
    if (isElectron.value) {
      return await window.electronAPI.window.isMaximized()
    }
    return false
  }

  // 应用操作
  const quitApp = () => {
    if (isElectron.value) {
      window.electronAPI.app.quit()
    }
  }

  // 文件操作
  const selectFile = async (options: any = {}) => {
    if (isElectron.value) {
      return await window.electronAPI.fs.selectFile(options)
    }
    return null
  }

  const selectDirectory = async (options: any = {}) => {
    if (isElectron.value) {
      return await window.electronAPI.fs.selectDirectory(options)
    }
    return null
  }

  const readFile = async (path: string) => {
    if (isElectron.value) {
      return await window.electronAPI.fs.readFile(path)
    }
    return null
  }

  const writeFile = async (path: string, content: string) => {
    if (isElectron.value) {
      await window.electronAPI.fs.writeFile(path, content)
    }
  }

  // 本地存储
  const storeGet = async (key: string) => {
    if (isElectron.value) {
      return await window.electronAPI.store.get(key)
    }
    return null
  }

  const storeSet = async (key: string, value: any) => {
    if (isElectron.value) {
      await window.electronAPI.store.set(key, value)
    }
  }

  const storeDelete = async (key: string) => {
    if (isElectron.value) {
      await window.electronAPI.store.delete(key)
    }
  }

  const storeClear = async () => {
    if (isElectron.value) {
      await window.electronAPI.store.clear()
    }
  }

  // 通知
  const sendNotification = async (options: any) => {
    if (isElectron.value && enableNotification) {
      await window.electronAPI.notification.send(options)
    } else {
      new Notification(options.title || 'QooERP', {
        body: options.body || '',
        icon: options.icon,
      })
    }
  }

  // 剪贴板
  const readClipboard = async () => {
    if (isElectron.value) {
      return await window.electronAPI.clipboard.readText()
    }
    return ''
  }

  const writeClipboard = async (text: string) => {
    if (isElectron.value) {
      await window.electronAPI.clipboard.writeText(text)
    }
  }

  // 更新操作
  const installUpdate = () => {
    if (isElectron.value) {
      window.electronAPI.update.install()
    }
  }

  onMounted(() => {
    init()
  })

  onUnmounted(() => {
    cleanupUpdateListeners()
  })

  return {
    // 状态
    isElectron,
    appVersion,
    appPath,
    updateAvailable,
    updateInfo,
    updateProgress,
    isUpdating,

    // 窗口操作
    minimizeWindow,
    maximizeWindow,
    unmaximizeWindow,
    toggleMaximize,
    closeWindow,
    isMaximized,

    // 应用操作
    quitApp,

    // 文件操作
    selectFile,
    selectDirectory,
    readFile,
    writeFile,

    // 本地存储
    storeGet,
    storeSet,
    storeDelete,
    storeClear,

    // 通知
    sendNotification,

    // 剪贴板
    readClipboard,
    writeClipboard,

    // 更新操作
    installUpdate,
  }
}
