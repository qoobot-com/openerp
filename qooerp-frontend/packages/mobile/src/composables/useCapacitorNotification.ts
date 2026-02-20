/**
 * Capacitor 通知管理
 * P6-CAP-T4: 推送与本地通知
 */
import { ref } from 'vue'
import { LocalNotifications } from '@capacitor/local-notifications'
import { PushNotifications } from '@capacitor/push-notifications'

export interface NotificationSchedule {
  id: number
  title: string
  body: string
  schedule?: {
    at?: Date
    repeats?: boolean
    every?: 'year' | 'month' | 'two-weeks' | 'week' | 'day' | 'hour' | 'minute' | 'second'
    count?: number
  }
  sound?: string
  attachments?: Array<{ id: string; url: string }]
  actionTypeId?: string
  extra?: any
}

export interface PushNotificationPayload {
  title?: string
  body?: string
  data?: any
}

export function useCapacitorNotification() {
  const permissionStatus = ref<'prompt' | 'prompt-with-rationale' | 'granted' | 'denied'>('prompt')
  const pushRegistered = ref(false)

  // 初始化本地通知
  const initLocalNotifications = async () => {
    try {
      const { display } = await LocalNotifications.requestPermissions()
      permissionStatus.value = display

      // 设置通知动作
      await LocalNotifications.createChannel({
        id: 'default',
        name: '默认通知',
        description: '应用默认通知渠道',
        importance: 5,
        visibility: 1,
      })

      await LocalNotifications.createChannel({
        id: 'important',
        name: '重要通知',
        description: '重要通知渠道',
        importance: 5,
        visibility: 1,
        vibration: true,
        sound: 'beep.wav',
      })
    } catch (error) {
      console.error('Failed to init local notifications:', error)
    }
  }

  // 初始化推送通知
  const initPushNotifications = async () => {
    try {
      const result = await PushNotifications.requestPermissions()
      if (result.receive === 'granted') {
        await PushNotifications.register()
        pushRegistered.value = true
      }
    } catch (error) {
      console.error('Failed to init push notifications:', error)
    }
  }

  // 发送本地通知
  const sendLocalNotification = async (notification: NotificationSchedule): Promise<number> => {
    try {
      const result = await LocalNotifications.schedule({
        notifications: [notification],
      })
      return result.notifications[0]?.id || 0
    } catch (error) {
      console.error('Failed to send local notification:', error)
      return 0
    }
  }

  // 取消通知
  const cancelNotification = async (id: number): Promise<void> => {
    try {
      await LocalNotifications.cancel({ notifications: [id] })
    } catch (error) {
      console.error('Failed to cancel notification:', error)
    }
  }

  // 取消所有通知
  const cancelAllNotifications = async (): Promise<void> => {
    try {
      await LocalNotifications.cancel()
    } catch (error) {
      console.error('Failed to cancel all notifications:', error)
    }
  }

  // 获取待发送通知
  const getPendingNotifications = async () => {
    try {
      return await LocalNotifications.getPending()
    } catch (error) {
      console.error('Failed to get pending notifications:', error)
      return { notifications: [] }
    }
  }

  // 立即发送通知
  const notify = async (title: string, body: string, options: Partial<NotificationSchedule> = {}): Promise<number> => {
    const id = Date.now()
    return await sendLocalNotification({
      id,
      title,
      body,
      ...options,
    })
  }

  // 发送重要通知
  const notifyImportant = async (title: string, body: string): Promise<number> => {
    return await notify(title, body, {
      actionTypeId: 'important',
      sound: 'beep.wav',
    })
  }

  // 定时通知
  const schedule = async (title: string, body: string, at: Date): Promise<number> => {
    const id = Date.now()
    return await sendLocalNotification({
      id,
      title,
      body,
      schedule: { at },
    })
  }

  // 重复通知
  const scheduleRepeating = async (
    title: string,
    body: string,
    every: NotificationSchedule['schedule']['every']
  ): Promise<number> => {
    const id = Date.now()
    return await sendLocalNotification({
      id,
      title,
      body,
      schedule: { every, repeats: true },
    })
  }

  // 监听本地通知
  const onLocalNotificationReceived = (callback: (notification: any) => void) => {
    LocalNotifications.addListener('localNotificationReceived', callback)
  }

  // 监听本地通知点击
  const onLocalNotificationActionPerformed = (callback: (action: any) => void) => {
    LocalNotifications.addListener('localNotificationActionPerformed', callback)
  }

  // 监听推送通知
  const onPushNotificationReceived = (callback: (notification: any) => void) => {
    PushNotifications.addListener('pushNotificationReceived', callback)
  }

  // 监听推送通知点击
  const onPushNotificationActionPerformed = (callback: (action: any) => void) => {
    PushNotifications.addListener('pushNotificationActionPerformed', callback)
  }

  // 清理监听器
  const removeAllListeners = async () => {
    await LocalNotifications.removeAllListeners()
    await PushNotifications.removeAllListeners()
  }

  return {
    // 状态
    permissionStatus,
    pushRegistered,

    // 初始化
    initLocalNotifications,
    initPushNotifications,

    // 本地通知
    sendLocalNotification,
    cancelNotification,
    cancelAllNotifications,
    getPendingNotifications,

    // 快捷方法
    notify,
    notifyImportant,
    schedule,
    scheduleRepeating,

    // 事件监听
    onLocalNotificationReceived,
    onLocalNotificationActionPerformed,
    onPushNotificationReceived,
    onPushNotificationActionPerformed,

    // 清理
    removeAllListeners,
  }
}

/**
 * 业务通知 Hook
 */
export function useBusinessNotification() {
  const notification = useCapacitorNotification()

  // 新消息通知
  const newMessage = async (title: string, count: number): Promise<void> => {
    await notification.notify('新消息', `${title}，您有 ${count} 条新消息`, {
      sound: 'beep.wav',
    })
  }

  // 任务提醒
  const taskReminder = async (taskName: string, dueTime: string): Promise<void> => {
    await notification.notifyImportant('任务提醒', `任务「${taskName}」将于 ${dueTime} 到期`)
  }

  // 审批通知
  const approvalNotify = async (
    type: 'pending' | 'approved' | 'rejected',
    title: string
  ): Promise<void> => {
    const messages = {
      pending: `待审批：${title}`,
      approved: `审批通过：${title}`,
      rejected: `审批拒绝：${title}`,
    }
    await notification.notify('审批通知', messages[type])
  }

  // 系统公告
  const announcement = async (title: string, content: string): Promise<void> => {
    await notification.notifyImportant('系统公告', `${title}\n${content}`)
  }

  return {
    newMessage,
    taskReminder,
    approvalNotify,
    announcement,
  }
}
