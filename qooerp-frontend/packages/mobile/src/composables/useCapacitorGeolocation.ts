/**
 * Capacitor 地理位置服务
 * P6-CAP-T6: 位置服务集成
 */
import { ref, onUnmounted } from 'vue'
import { Geolocation } from '@capacitor/geolocation'

export interface Position {
  latitude: number
  longitude: number
  accuracy: number
  altitude?: number
  altitudeAccuracy?: number
  heading?: number
  speed?: number
  timestamp: number
}

export interface GeolocationOptions {
  enableHighAccuracy?: boolean
  timeout?: number
  maximumAge?: number
}

export function useCapacitorGeolocation() {
  const currentPosition = ref<Position | null>(null)
  const isWatching = ref(false)
  const watchId = ref<string | null>(null)

  // 检查权限
  const checkPermissions = async () => {
    try {
      const status = await Geolocation.checkPermissions()
      return status
    } catch (error) {
      console.error('Failed to check geolocation permissions:', error)
      return null
    }
  }

  // 请求权限
  const requestPermissions = async () => {
    try {
      const status = await Geolocation.requestPermissions()
      return status
    } catch (error) {
      console.error('Failed to request geolocation permissions:', error)
      return null
    }
  }

  // 获取当前位置
  const getCurrentPosition = async (options?: GeolocationOptions): Promise<Position | null> => {
    try {
      const position = await Geolocation.getCurrentPosition({
        enableHighAccuracy: options?.enableHighAccuracy || true,
        timeout: options?.timeout || 10000,
        maximumAge: options?.maximumAge || 0,
      })

      currentPosition.value = position.coords as Position
      return currentPosition.value
    } catch (error) {
      console.error('Failed to get current position:', error)
      return null
    }
  }

  // 监听位置变化
  const watchPosition = async (
    callback: (position: Position | null, error?: any) => void,
    options?: GeolocationOptions
  ): Promise<void> => {
    try {
      const id = await Geolocation.watchPosition(
        {
          enableHighAccuracy: options?.enableHighAccuracy || true,
          timeout: options?.timeout || 10000,
          maximumAge: options?.maximumAge || 0,
        },
        (position, error) => {
          if (error) {
            currentPosition.value = null
            callback(null, error)
          } else {
            currentPosition.value = position.coords as Position
            callback(currentPosition.value)
          }
        }
      )

      watchId.value = id
      isWatching.value = true
    } catch (error) {
      console.error('Failed to watch position:', error)
      isWatching.value = false
    }
  }

  // 停止监听
  const clearWatch = async (): Promise<void> => {
    if (watchId.value) {
      try {
        await Geolocation.clearWatch({ id: watchId.value })
        watchId.value = null
        isWatching.value = false
      } catch (error) {
        console.error('Failed to clear watch:', error)
      }
    }
  }

  // 计算两点间距离 (米)
  const calculateDistance = (
    lat1: number,
    lon1: number,
    lat2: number,
    lon2: number
  ): number => {
    const R = 6371e3 // 地球半径 (米)
    const φ1 = (lat1 * Math.PI) / 180
    const φ2 = (lat2 * Math.PI) / 180
    const Δφ = ((lat2 - lat1) * Math.PI) / 180
    const Δλ = ((lon2 - lon1) * Math.PI) / 180

    const a =
      Math.sin(Δφ / 2) * Math.sin(Δφ / 2) +
      Math.cos(φ1) * Math.cos(φ2) * Math.sin(Δλ / 2) * Math.sin(Δλ / 2)
    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

    return R * c
  }

  // 格式化坐标
  const formatCoordinate = (lat: number, lon: number): string => {
    return `${Math.abs(lat).toFixed(6)}°${lat >= 0 ? 'N' : 'S'} ${Math.abs(lon).toFixed(6)}°${lon >= 0 ? 'E' : 'W'}`
  }

  // 获取地址 (需要逆地理编码 API)
  const reverseGeocode = async (
    lat: number,
    lon: number
  ): Promise<string | null> => {
    // 这里可以集成逆地理编码 API，如高德、百度等
    // 示例：调用高德逆地理编码 API
    try {
      const response = await fetch(
        `https://restapi.amap.com/v3/geocode/regeo?location=${lon},${lat}&key=YOUR_API_KEY`
      )
      const data = await response.json()
      return data.regeocode?.formatted_address || null
    } catch (error) {
      console.error('Failed to reverse geocode:', error)
      return null
    }
  }

  // 地理编码 (地址转坐标)
  const geocode = async (address: string): Promise<{ lat: number; lon: number } | null> => {
    // 这里可以集成地理编码 API，如高德、百度等
    try {
      const response = await fetch(
        `https://restapi.amap.com/v3/geocode/geo?address=${encodeURIComponent(address)}&key=YOUR_API_KEY`
      )
      const data = await response.json()
      const location = data.geocodes?.[0]?.location
      if (location) {
        const [lon, lat] = location.split(',').map(Number)
        return { lat, lon }
      }
      return null
    } catch (error) {
      console.error('Failed to geocode:', error)
      return null
    }
  }

  // 打开地图应用
  const openMap = (lat: number, lon: number, label: string = '位置'): void => {
    const url = `https://maps.google.com/?q=${lat},${lon}(${encodeURIComponent(label)})`
    window.open(url, '_blank')
  }

  // 生成导航链接
  const getNavigationUrl = (
    destLat: number,
    destLon: number,
    destLabel?: string,
    startLat?: number,
    startLon?: number
  ): string => {
    const base = 'https://maps.google.com/maps'
    const params = new URLSearchParams()

    params.append('daddr', `${destLat},${destLon}`)

    if (destLabel) {
      params.append('destination', encodeURIComponent(destLabel))
    }

    if (startLat !== undefined && startLon !== undefined) {
      params.append('saddr', `${startLat},${startLon}`)
    }

    params.append('dirflg', 'd')

    return `${base}?${params.toString()}`
  }

  // 清理监听器
  onUnmounted(() => {
    clearWatch()
  })

  return {
    // 状态
    currentPosition,
    isWatching,

    // 权限
    checkPermissions,
    requestPermissions,

    // 操作
    getCurrentPosition,
    watchPosition,
    clearWatch,

    // 工具
    calculateDistance,
    formatCoordinate,
    reverseGeocode,
    geocode,
    openMap,
    getNavigationUrl,
  }
}
