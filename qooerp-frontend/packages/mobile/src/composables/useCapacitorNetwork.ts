/**
 * Capacitor 网络状态管理
 * P6-CAP-T7: 网络状态监控
 */
import { ref, onMounted, onUnmounted } from 'vue'
import { Network } from '@capacitor/network'

export interface NetworkStatus {
  connected: boolean
  connectionType: 'wifi' | 'cellular' | 'none' | 'unknown' | 'ethernet'
}

export interface NetworkMetrics {
  ping: number
  downloadSpeed: number
  uploadSpeed: number
}

export function useCapacitorNetwork() {
  const networkStatus = ref<NetworkStatus>({
    connected: true,
    connectionType: 'unknown',
  })

  const previousStatus = ref<NetworkStatus | null>(null)
  const listenersSetup = ref(false)

  // 初始化
  const init = async () => {
    try {
      const status = await Network.getStatus()
      networkStatus.value = {
        connected: status.connected,
        connectionType: status.connectionType,
      }

      setupListeners()
    } catch (error) {
      console.error('Failed to init network status:', error)
    }
  }

  // 设置网络状态监听器
  const setupListeners = () => {
    if (listenersSetup.value) return

    Network.addListener('networkStatusChange', (status) => {
      previousStatus.value = { ...networkStatus.value }
      networkStatus.value = {
        connected: status.connected,
        connectionType: status.connectionType,
      }

      // 触发网络状态变化事件
      window.dispatchEvent(new CustomEvent('networkStatusChange', { detail: networkStatus.value }))
    })

    listenersSetup.value = true
  }

  // 获取当前网络状态
  const getStatus = async (): Promise<NetworkStatus> => {
    try {
      const status = await Network.getStatus()
      networkStatus.value = {
        connected: status.connected,
        connectionType: status.connectionType,
      }
      return networkStatus.value
    } catch (error) {
      console.error('Failed to get network status:', error)
      return networkStatus.value
    }
  }

  // 检查是否在线
  const isOnline = async (): Promise<boolean> => {
    const status = await getStatus()
    return status.connected
  }

  // 检查是否离线
  const isOffline = async (): Promise<boolean> => {
    return !(await isOnline())
  }

  // 检查是否使用 WiFi
  const isWifi = async (): Promise<boolean> => {
    const status = await getStatus()
    return status.connected && status.connectionType === 'wifi'
  }

  // 检查是否使用蜂窝网络
  const isCellular = async (): Promise<boolean> => {
    const status = await getStatus()
    return status.connected && status.connectionType === 'cellular'
  }

  // 网络状态变化回调
  const onNetworkChange = (callback: (status: NetworkStatus) => void) => {
    const handler = (event: Event) => {
      const detail = (event as CustomEvent).detail
      callback(detail)
    }

    window.addEventListener('networkStatusChange', handler as EventListener)

    // 返回清理函数
    return () => {
      window.removeEventListener('networkStatusChange', handler as EventListener)
    }
  }

  // 在线回调
  const onOnline = (callback: () => void) => {
    return onNetworkChange((status) => {
      if (status.connected && !previousStatus.value?.connected) {
        callback()
      }
    })
  }

  // 离线回调
  const onOffline = (callback: () => void) => {
    return onNetworkChange((status) => {
      if (!status.connected && previousStatus.value?.connected) {
        callback()
      }
    })
  }

  // 网络重连回调
  const onReconnect = (callback: () => void) => {
    return onNetworkChange((status) => {
      if (status.connected && !previousStatus.value?.connected) {
        callback()
      }
    })
  }

  // 测量网络延迟
  const measurePing = async (url: string = '/api/health', timeout: number = 5000): Promise<number | null> => {
    try {
      const start = performance.now()
      const controller = new AbortController()
      const id = setTimeout(() => controller.abort(), timeout)

      await fetch(url, {
        method: 'HEAD',
        signal: controller.signal,
      })

      clearTimeout(id)
      const end = performance.now()
      return Math.round(end - start)
    } catch (error) {
      console.error('Failed to measure ping:', error)
      return null
    }
  }

  // 测量下载速度
  const measureDownloadSpeed = async (
    fileSize: number = 1000000 // 1MB
  ): Promise<number | null> => {
    try {
      const start = performance.now()

      const response = await fetch(`/api/benchmark/download?size=${fileSize}`)
      const blob = await response.blob()

      const end = performance.now()
      const duration = (end - start) / 1000 // 秒
      const speed = (blob.size / 1024) / duration // KB/s

      return Math.round(speed)
    } catch (error) {
      console.error('Failed to measure download speed:', error)
      return null
    }
  }

  // 测量上传速度
  const measureUploadSpeed = async (
    data: Blob
  ): Promise<number | null> => {
    try {
      const start = performance.now()

      const formData = new FormData()
      formData.append('file', data)

      await fetch('/api/benchmark/upload', {
        method: 'POST',
        body: formData,
      })

      const end = performance.now()
      const duration = (end - start) / 1000 // 秒
      const speed = (data.size / 1024) / duration // KB/s

      return Math.round(speed)
    } catch (error) {
      console.error('Failed to measure upload speed:', error)
      return null
    }
  }

  // 获取网络指标
  const getNetworkMetrics = async (): Promise<NetworkMetrics | null> => {
    const ping = await measurePing()
    if (ping === null) return null

    const downloadSpeed = await measureDownloadSpeed()
    if (downloadSpeed === null) return null

    const uploadData = new Blob(['a'.repeat(1024 * 1024)]) // 1MB data
    const uploadSpeed = await measureUploadSpeed(uploadData)

    return {
      ping,
      downloadSpeed,
      uploadSpeed: uploadSpeed || 0,
    }
  }

  // 获取网络质量评级
  const getNetworkQuality = (metrics: NetworkMetrics): 'excellent' | 'good' | 'fair' | 'poor' => {
    if (metrics.ping < 50 && metrics.downloadSpeed > 5000) return 'excellent'
    if (metrics.ping < 100 && metrics.downloadSpeed > 2000) return 'good'
    if (metrics.ping < 200 && metrics.downloadSpeed > 500) return 'fair'
    return 'poor'
  }

  onMounted(() => {
    init()
  })

  onUnmounted(() => {
    // 清理监听器
    listenersSetup.value = false
  })

  return {
    // 状态
    networkStatus,
    previousStatus,

    // 基础方法
    getStatus,
    isOnline,
    isOffline,
    isWifi,
    isCellular,

    // 事件监听
    onNetworkChange,
    onOnline,
    onOffline,
    onReconnect,

    // 网络测试
    measurePing,
    measureDownloadSpeed,
    measureUploadSpeed,
    getNetworkMetrics,
    getNetworkQuality,
  }
}

/**
 * 网络请求重试 Hook
 */
export function useNetworkRetry() {
  const { isOnline } = useCapacitorNetwork()

  const retryWithBackoff = async <T>(
    fn: () => Promise<T>,
    options: {
      maxRetries?: number
      initialDelay?: number
      maxDelay?: number
      backoffFactor?: number
    } = {}
  ): Promise<T> => {
    const {
      maxRetries = 3,
      initialDelay = 1000,
      maxDelay = 10000,
      backoffFactor = 2,
    } = options

    let lastError: Error | null = null
    let delay = initialDelay

    for (let attempt = 0; attempt < maxRetries; attempt++) {
      try {
        return await fn()
      } catch (error) {
        lastError = error as Error

        // 检查是否在线
        const online = await isOnline()
        if (!online) {
          throw new Error('网络不可用')
        }

        // 最后一次重试失败，抛出错误
        if (attempt === maxRetries - 1) {
          throw lastError
        }

        // 等待后重试
        await new Promise(resolve => setTimeout(resolve, delay))
        delay = Math.min(delay * backoffFactor, maxDelay)
      }
    }

    throw lastError
  }

  return {
    retryWithBackoff,
  }
}
