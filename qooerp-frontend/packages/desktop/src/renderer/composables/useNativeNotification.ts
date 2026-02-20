/**
 * Electron 原生通知
 * P6-ELEC-T7: 系统通知集成
 */
import { ref } from 'vue'
import { MessagePlugin } from 'tdesign-vue-next'

export interface NotificationOptions {
  title: string
  body: string
  icon?: string
  sound?: string
  onClick?: () => void
  onClose?: () => void
}

export function useNativeNotification() {
  const isElectron = ref(!!(window as any).electronAPI)
  const permission = ref<NotificationPermission>('default')

  // 初始化
  const init = async () => {
    if ('Notification' in window) {
      permission.value = Notification.permission
    }
  }

  // 请求权限
  const requestPermission = async (): Promise<boolean> {
    if (!('Notification' in window)) {
      MessagePlugin.warning('当前浏览器不支持通知')
      return false
    }

    const result = await Notification.requestPermission()
    permission.value = result
    return result === 'granted'
  }

  // 发送通知
  const send = (options: NotificationOptions): void => {
    if (permission.value !== 'granted') {
      MessagePlugin.warning('请先允许通知权限')
      return
    }

    if (isElectron.value) {
      // Electron 环境
      const api = (window as any).electronAPI
      api.notification.send(options).catch((error: any) => {
        console.error('Failed to send notification:', error)
        MessagePlugin.error('发送通知失败')
      })
    } else {
      // Web 环境
      const notification = new Notification(options.title, {
        body: options.body,
        icon: options.icon,
      })

      if (options.onClick) {
        notification.onclick = options.onClick
      }

      if (options.onClose) {
        notification.onclose = options.onClose
      }
    }
  }

  // 快捷通知方法
  const info = (title: string, body: string): void => {
    send({ title, body })
  }

  const success = (title: string, body: string): void => {
    send({ title: `✓ ${title}`, body })
  }

  const warning = (title: string, body: string): void => {
    send({ title: `⚠ ${title}`, body })
  }

  const error = (title: string, body: string): void => {
    send({ title: `✕ ${title}`, body })
  }

  // 带图标的通知
  const withIcon = (title: string, body: string, icon: string): void => {
    send({ title, body, icon })
  }

  // 可点击的通知
  const clickable = (title: string, body: string, onClick: () => void): void => {
    send({ title, body, onClick })
  }

  // 消息通知
  const message = (type: 'info' | 'success' | 'warning' | 'error', title: string, body: string): void => {
    switch (type) {
      case 'info':
        info(title, body)
        break
      case 'success':
        success(title, body)
        break
      case 'warning':
        warning(title, body)
        break
      case 'error':
        error(title, body)
        break
    }
  }

  // 系统通知
  const system = (options: NotificationOptions): void => {
    send(options)
  }

  // 定时通知
  const schedule = (options: NotificationOptions, delay: number): void => {
    setTimeout(() => {
      send(options)
    }, delay)
  }

  // 批量通知
  const batch = (notifications: NotificationOptions[]): void => {
    notifications.forEach((options, index) => {
      schedule(options, index * 1000)
    })
  }

  init()

  return {
    // 状态
    isElectron,
    permission,

    // 基础方法
    requestPermission,
    send,

    // 快捷方法
    info,
    success,
    warning,
    error,
    withIcon,
    clickable,
    message,
    system,
    schedule,
    batch,
  }
}

// 业务通知 Hook
export function useBusinessNotification() {
  const notification = useNativeNotification()

  // 新消息通知
  const newMessage = (title: string, count: number): void => {
    notification.info('新消息', `${title}，您有 ${count} 条新消息`)
  }

  // 任务提醒
  const taskReminder = (taskName: string, dueTime: string): void => {
    notification.warning('任务提醒', `任务「${taskName}」将于 ${dueTime} 到期`)
  }

  // 审批通知
  const approvalNotify = (type: 'pending' | 'approved' | 'rejected', title: string): void => {
    const messages = {
      pending: `待审批：${title}`,
      approved: `审批通过：${title}`,
      rejected: `审批拒绝：${title}`,
    }
    notification.info('审批通知', messages[type])
  }

  // 系统公告
  const announcement = (title: string, content: string): void => {
    notification.success('系统公告', `${title}\n${content}`)
  }

  // 更新提醒
  const updateNotify = (version: string): void => {
    notification.info('版本更新', `发现新版本 v${version}，请更新`)
  }

  // 异常警告
  const alert = (type: string, message: string): void => {
    notification.error('系统警告', `${type}：${message}`)
  }

  return {
    newMessage,
    taskReminder,
    approvalNotify,
    announcement,
    updateNotify,
    alert,
  }
}
