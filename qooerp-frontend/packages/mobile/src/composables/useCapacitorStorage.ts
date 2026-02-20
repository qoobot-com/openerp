/**
 * Capacitor 存储管理
 * P6-CAP-T3: 移动端数据持久化
 */
import { Preferences } from '@capacitor/preferences'

export class CapacitorStorage {
  /**
   * 存储数据
   */
  static async set(key: string, value: any): Promise<void> {
    const jsonValue = JSON.stringify(value)
    await Preferences.set({ key, value: jsonValue })
  }

  /**
   * 获取数据
   */
  static async get<T = any>(key: string): Promise<T | null> {
    const { value } = await Preferences.get({ key })
    if (value === null) return null
    try {
      return JSON.parse(value)
    } catch {
      return value as T
    }
  }

  /**
   * 删除数据
   */
  static async remove(key: string): Promise<void> {
    await Preferences.remove({ key })
  }

  /**
   * 清空所有数据
   */
  static async clear(): Promise<void> {
    await Preferences.clear()
  }

  /**
   * 获取所有键
   */
  static async keys(): Promise<string[]> {
    const { keys } = await Preferences.keys()
    return keys
  }

  /**
   * 批量获取
   */
  static async getMany<T = any>(keys: string[]): Promise<Record<string, T>> {
    const result: Record<string, T> = {} as Record<string, T>
    await Promise.all(
      keys.map(async (key) => {
        result[key] = await this.get<T>(key)
      })
    )
    return result
  }

  /**
   * 批量设置
   */
  static async setMany(entries: Record<string, any>): Promise<void> {
    await Promise.all(
      Object.entries(entries).map(([key, value]) => this.set(key, value))
    )
  }

  /**
   * 批量删除
   */
  static async removeMany(keys: string[]): Promise<void> {
    await Promise.all(keys.map((key) => this.remove(key)))
  }

  /**
   * 检查键是否存在
   */
  static async has(key: string): Promise<boolean> {
    const { keys } = await Preferences.keys()
    return keys.includes(key)
  }

  /**
   * 存在时获取，不存在则设置默认值
   */
  static async getOrSet<T = any>(key: string, defaultValue: T): Promise<T> {
    const value = await this.get<T>(key)
    if (value === null) {
      await this.set(key, defaultValue)
      return defaultValue
    }
    return value
  }
}

/**
 * 便捷的存储 Hook
 */
export function useCapacitorStorage() {
  const storage = CapacitorStorage

  return {
    // 基础方法
    get: storage.get,
    set: storage.set,
    remove: storage.remove,
    clear: storage.clear,
    keys: storage.keys,

    // 批量方法
    getMany: storage.getMany,
    setMany: storage.setMany,
    removeMany: storage.removeMany,

    // 工具方法
    has: storage.has,
    getOrSet: storage.getOrSet,
  }
}

/**
 * Token 存储 Hook
 */
export function useTokenStorage() {
  const { get, set, remove } = useCapacitorStorage()

  const getToken = async (): Promise<string | null> => {
    return await get<string>('access_token')
  }

  const setToken = async (token: string): Promise<void> => {
    await set('access_token', token)
  }

  const removeToken = async (): Promise<void> => {
    await remove('access_token')
  }

  const getRefreshToken = async (): Promise<string | null> => {
    return await get<string>('refresh_token')
  }

  const setRefreshToken = async (token: string): Promise<void> => {
    await set('refresh_token', token)
  }

  const clearTokens = async (): Promise<void> => {
    await remove('access_token')
    await remove('refresh_token')
  }

  return {
    getToken,
    setToken,
    removeToken,
    getRefreshToken,
    setRefreshToken,
    clearTokens,
  }
}

/**
 * 用户信息存储 Hook
 */
export function useUserStorage() {
  const { get, set, remove } = useCapacitorStorage()

  const getUserInfo = async () => {
    return await get('user_info')
  }

  const setUserInfo = async (userInfo: any) => {
    await set('user_info', userInfo)
  }

  const removeUserInfo = async () => {
    await remove('user_info')
  }

  const clearUserData = async () => {
    await remove('user_info')
    await remove('user_settings')
  }

  return {
    getUserInfo,
    setUserInfo,
    removeUserInfo,
    clearUserData,
  }
}

/**
 * 配置存储 Hook
 */
export function useConfigStorage<T extends Record<string, any>>(defaultConfig: T) {
  const { get, set } = useCapacitorStorage()

  const getConfig = async (): Promise<T> => {
    const config = await get<T>('app_config')
    return { ...defaultConfig, ...(config || {}) }
  }

  const updateConfig = async (updates: Partial<T>): Promise<T> => {
    const currentConfig = await getConfig()
    const newConfig = { ...currentConfig, ...updates }
    await set('app_config', newConfig)
    return newConfig
  }

  const resetConfig = async (): Promise<T> => {
    await set('app_config', defaultConfig)
    return defaultConfig
  }

  return {
    getConfig,
    updateConfig,
    resetConfig,
  }
}
