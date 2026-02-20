import { defineStore } from 'pinia'
import { ref } from 'vue'
import request from '@/api'
import type { ApiResponse } from '@/types'

interface UserInfo {
  id: string
  username: string
  email?: string
  avatar?: string
  realName?: string
  roles?: string[]
  permissions?: string[]
}

interface LoginParams {
  username: string
  password: string
  captcha?: string
}

export const useUserStore = defineStore(
  'user',
  () => {
    // 状态
    const token = ref<string>(localStorage.getItem('token') || '')
    const userInfo = ref<UserInfo | null>(null)
    const permissions = ref<string[]>(JSON.parse(localStorage.getItem('permissions') || '[]'))

    // Getters
    const isLoggedIn = () => !!token.value
    const hasPermission = (permission: string) => {
      return permissions.value.includes(permission)
    }
    const hasAnyPermission = (permList: string[]) => {
      return permList.some((perm) => permissions.value.includes(perm))
    }
    const hasAllPermissions = (permList: string[]) => {
      return permList.every((perm) => permissions.value.includes(perm))
    }

    // Actions
    const setToken = (newToken: string) => {
      token.value = newToken
      localStorage.setItem('token', newToken)
    }

    const setUserInfo = (info: UserInfo) => {
      userInfo.value = info
    }

    const setPermissions = (perms: string[]) => {
      permissions.value = perms
      localStorage.setItem('permissions', JSON.stringify(perms))
    }

    // 登录
    const login = async (params: LoginParams) => {
      try {
        const res: ApiResponse<{ token: string; userInfo: UserInfo }> =
          await request.post('/auth/login', params)

        if (res.code === 200) {
          setToken(res.data.token)
          setUserInfo(res.data.userInfo)
          return true
        }
        return false
      } catch (error) {
        console.error('登录失败:', error)
        return false
      }
    }

    // 获取用户信息
    const getUserInfo = async () => {
      try {
        const res: ApiResponse<{ userInfo: UserInfo; permissions: string[] }> =
          await request.get('/user/info')

        if (res.code === 200) {
          setUserInfo(res.data.userInfo)
          setPermissions(res.data.permissions)
          return true
        }
        return false
      } catch (error) {
        console.error('获取用户信息失败:', error)
        return false
      }
    }

    // 退出登录
    const logout = async () => {
      try {
        await request.post('/auth/logout')
      } catch (error) {
        console.error('退出登录失败:', error)
      } finally {
        // 清除本地数据
        token.value = ''
        userInfo.value = null
        permissions.value = []
        localStorage.removeItem('token')
        localStorage.removeItem('permissions')
      }
    }

    // 刷新 Token
    const refreshToken = async () => {
      try {
        const res: ApiResponse<{ token: string }> = await request.post('/auth/refresh')
        if (res.code === 200) {
          setToken(res.data.token)
          return true
        }
        return false
      } catch (error) {
        console.error('刷新Token失败:', error)
        logout()
        return false
      }
    }

    // 初始化用户信息
    const initUser = async () => {
      if (!token.value) {
        console.log('未登录，跳过获取用户信息')
        return false
      }
      const success = await getUserInfo()
      if (!success) {
        console.warn('获取用户信息失败')
      }
      return success
    }

    return {
      // 状态
      token,
      userInfo,
      permissions,

      // Getters
      isLoggedIn,
      hasPermission,
      hasAnyPermission,
      hasAllPermissions,

      // Actions
      setToken,
      setUserInfo,
      setPermissions,
      login,
      getUserInfo,
      logout,
      refreshToken,
      initUser,
    }
  },
  {
    // 持久化配置
    persist: {
      key: 'qooerp-user',
      storage: localStorage,
      paths: ['token'],
    },
  }
)

export type { UserInfo, LoginParams }
