/**
 * Electron 本地存储
 * P6-ELEC-T6: 本地数据持久化
 */
import { ref } from 'vue'

export function useNativeStore() {
  const isElectron = ref(!!(window as any).electronAPI)

  // 获取存储值
  const get = async <T = any>(key: string, defaultValue?: T): Promise<T | undefined> => {
    if (!isElectron.value) {
      // Web 环境使用 localStorage
      try {
        const value = localStorage.getItem(key)
        return value ? JSON.parse(value) : defaultValue
      } catch {
        return defaultValue
      }
    }

    const api = (window as any).electronAPI
    try {
      const value = await api.store.get(key)
      return value ?? defaultValue
    } catch (error) {
      console.error('Failed to get store value:', error)
      return defaultValue
    }
  }

  // 设置存储值
  const set = async (key: string, value: any): Promise<boolean> => {
    if (!isElectron.value) {
      // Web 环境使用 localStorage
      try {
        localStorage.setItem(key, JSON.stringify(value))
        return true
      } catch (error) {
        console.error('Failed to set localStorage:', error)
        return false
      }
    }

    const api = (window as any).electronAPI
    try {
      await api.store.set(key, value)
      return true
    } catch (error) {
      console.error('Failed to set store value:', error)
      return false
    }
  }

  // 删除存储值
  const remove = async (key: string): Promise<boolean> => {
    if (!isElectron.value) {
      // Web 环境使用 localStorage
      try {
        localStorage.removeItem(key)
        return true
      } catch (error) {
        console.error('Failed to remove localStorage:', error)
        return false
      }
    }

    const api = (window as any).electronAPI
    try {
      await api.store.delete(key)
      return true
    } catch (error) {
      console.error('Failed to remove store value:', error)
      return false
    }
  }

  // 清空所有存储
  const clear = async (): Promise<boolean> => {
    if (!isElectron.value) {
      // Web 环境使用 localStorage
      try {
        localStorage.clear()
        return true
      } catch (error) {
        console.error('Failed to clear localStorage:', error)
        return false
      }
    }

    const api = (window as any).electronAPI
    try {
      await api.store.clear()
      return true
    } catch (error) {
      console.error('Failed to clear store:', error)
      return false
    }
  }

  // 批量获取
  const getMany = async <T = any>(keys: string[]): Promise<Record<string, T>> => {
    const result: Record<string, T> = {} as Record<string, T>
    for (const key of keys) {
      result[key] = await get<T>(key)
    }
    return result
  }

  // 批量设置
  const setMany = async (entries: Record<string, any>): Promise<boolean> => {
    for (const key in entries) {
      const success = await set(key, entries[key])
      if (!success) return false
    }
    return true
  }

  // 检查键是否存在
  const has = async (key: string): Promise<boolean> => {
    const value = await get(key)
    return value !== undefined
  }

  return {
    isElectron,
    get,
    set,
    remove,
    clear,
    getMany,
    setMany,
    has,
  }
}

// 便捷的配置存储 Hook
export function useConfigStore<T extends Record<string, any>>(defaultConfig: T) {
  const store = useNativeStore()

  const getConfig = async (): Promise<T> => {
    const config = await store.get<T>('config', {} as T)
    return { ...defaultConfig, ...config }
  }

  const updateConfig = async (updates: Partial<T>): Promise<T> => {
    const currentConfig = await getConfig()
    const newConfig = { ...currentConfig, ...updates }
    await store.set('config', newConfig)
    return newConfig
  }

  const resetConfig = async (): Promise<T> => {
    await store.remove('config')
    return defaultConfig
  }

  return {
    getConfig,
    updateConfig,
    resetConfig,
  }
}

// 便捷的用户设置存储 Hook
export function useUserSettings() {
  const store = useNativeStore()

  const getSetting = async <T = any>(key: string, defaultValue?: T): Promise<T | undefined> => {
    const settings = await store.get<Record<string, any>>('settings', {})
    return settings[key] ?? defaultValue
  }

  const setSetting = async <T = any>(key: string, value: T): Promise<void> => {
    const settings = await store.get<Record<string, any>>('settings', {})
    settings[key] = value
    await store.set('settings', settings)
  }

  const removeSetting = async (key: string): Promise<void> => {
    const settings = await store.get<Record<string, any>>('settings', {})
    delete settings[key]
    await store.set('settings', settings)
  }

  const getAllSettings = async (): Promise<Record<string, any>> => {
    return await store.get<Record<string, any>>('settings', {})
  }

  const clearSettings = async (): Promise<void> => {
    await store.remove('settings')
  }

  return {
    getSetting,
    setSetting,
    removeSetting,
    getAllSettings,
    clearSettings,
  }
}
