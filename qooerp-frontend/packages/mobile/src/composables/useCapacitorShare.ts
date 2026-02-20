/**
 * Capacitor 分享功能
 * P6-CAP-T8: 社交分享集成
 */
import { Share } from '@capacitor/share'

export interface ShareOptions {
  title?: string
  text?: string
  url?: string
  dialogTitle?: string
}

export interface ShareResult {
  activityType?: string
  completed: boolean
}

export function useCapacitorShare() {
  // 分享文本
  const shareText = async (text: string, title?: string): Promise<ShareResult | null> => {
    try {
      const result = await Share.share({
        title,
        text,
      })
      return result
    } catch (error) {
      console.error('Failed to share text:', error)
      return null
    }
  }

  // 分享链接
  const shareUrl = async (url: string, title?: string, text?: string): Promise<ShareResult | null> => {
    try {
      const result = await Share.share({
        title,
        text,
        url,
      })
      return result
    } catch (error) {
      console.error('Failed to share URL:', error)
      return null
    }
  }

  // 分享到微信
  const shareToWeChat = async (
    options: { title: string; description: string; url: string; imageUrl?: string }
  ): Promise<ShareResult | null> => {
    try {
      const result = await Share.share({
        title: options.title,
        text: options.description,
        url: options.url,
        dialogTitle: '分享到微信',
      })
      return result
    } catch (error) {
      console.error('Failed to share to WeChat:', error)
      return null
    }
  }

  // 分享到朋友圈
  const shareToWeChatMoments = async (
    options: { title: string; description: string; url: string; imageUrl?: string }
  ): Promise<ShareResult | null> => {
    try {
      const result = await Share.share({
        title: options.title,
        text: options.description,
        url: options.url,
        dialogTitle: '分享到朋友圈',
      })
      return result
    } catch (error) {
      console.error('Failed to share to WeChat moments:', error)
      return null
    }
  }

  // 分享到微博
  const shareToWeibo = async (
    options: { title: string; description: string; url: string; imageUrl?: string }
  ): Promise<ShareResult | null> => {
    try {
      const result = await Share.share({
        title: options.title,
        text: `${options.description} ${options.url}`,
        dialogTitle: '分享到微博',
      })
      return result
    } catch (error) {
      console.error('Failed to share to Weibo:', error)
      return null
    }
  }

  // 分享到 QQ
  const shareToQQ = async (
    options: { title: string; description: string; url: string; imageUrl?: string }
  ): Promise<ShareResult | null> => {
    try {
      const result = await Share.share({
        title: options.title,
        text: options.description,
        url: options.url,
        dialogTitle: '分享到QQ',
      })
      return result
    } catch (error) {
      console.error('Failed to share to QQ:', error)
      return null
    }
  }

  // 检查分享可用性
  const canShare = async (): Promise<boolean> => {
    try {
      return await Share.canShare()
    } catch (error) {
      return false
    }
  }

  // 生成分享链接
  const generateShareLink = (baseUrl: string, params: Record<string, any>): string => {
    const url = new URL(baseUrl)
    Object.entries(params).forEach(([key, value]) => {
      url.searchParams.append(key, String(value))
    })
    return url.toString()
  }

  // 复制链接
  const copyLink = async (url: string): Promise<boolean> => {
    try {
      await navigator.clipboard.writeText(url)
      return true
    } catch (error) {
      console.error('Failed to copy link:', error)
      return false
    }
  }

  // 生成分享文本
  const generateShareText = (
    template: string,
    data: Record<string, any>
  ): string => {
    return template.replace(/\{(\w+)\}/g, (_, key) => String(data[key] || ''))
  }

  // 预览分享内容
  const previewShare = (options: ShareOptions): void => {
    console.log('Share Preview:', options)
  }

  return {
    // 基础方法
    shareText,
    shareUrl,
    canShare,

    // 平台分享
    shareToWeChat,
    shareToWeChatMoments,
    shareToWeibo,
    shareToQQ,

    // 工具方法
    generateShareLink,
    copyLink,
    generateShareText,
    previewShare,
  }
}

/**
 * 业务分享 Hook
 */
export function useBusinessShare() {
  const share = useCapacitorShare()

  // 分享报表
  const shareReport = async (
    reportName: string,
    reportId: string,
    baseUrl: string
  ): Promise<ShareResult | null> => {
    const url = share.generateShareLink(`${baseUrl}/reports/${reportId}`, {
      ref: 'share',
    })

    const text = share.generateShareText('{reportName} 报表分享', { reportName })

    return await share.shareUrl(url, '报表分享', text)
  }

  // 分享任务
  const shareTask = async (
    taskName: string,
    taskId: string,
    baseUrl: string
  ): Promise<ShareResult | null> => {
    const url = share.generateShareLink(`${baseUrl}/tasks/${taskId}`, {
      ref: 'share',
    })

    const text = share.generateShareText('任务: {taskName}', { taskName })

    return await share.shareUrl(url, '任务分享', text)
  }

  // 分享文档
  const shareDocument = async (
    docName: string,
    docId: string,
    baseUrl: string
  ): Promise<ShareResult | null> => {
    const url = share.generateShareLink(`${baseUrl}/documents/${docId}`, {
      ref: 'share',
    })

    const text = share.generateShareText('文档: {docName}', { docName })

    return await share.shareUrl(url, '文档分享', text)
  }

  // 邀请用户
  const inviteUser = async (
    inviteCode: string,
    inviteUrl: string
  ): Promise<ShareResult | null> => {
    const text = share.generateShareText(
      '邀请您加入 QooERP，邀请码：{inviteCode}',
      { inviteCode }
    )

    return await share.shareUrl(inviteUrl, '邀请加入', text)
  }

  // 分享活动
  const shareActivity = async (
    activityName: string,
    activityId: string,
    baseUrl: string
  ): Promise<ShareResult | null> => {
    const url = share.generateShareLink(`${baseUrl}/activities/${activityId}`, {
      ref: 'share',
    })

    const text = share.generateShareText('活动: {activityName}', { activityName })

    return await share.shareUrl(url, '活动分享', text)
  }

  // 分享新闻
  const shareNews = async (
    newsTitle: string,
    newsId: string,
    baseUrl: string
  ): Promise<ShareResult | null> => {
    const url = share.generateShareLink(`${baseUrl}/news/${newsId}`, {
      ref: 'share',
    })

    const text = share.generateShareText('新闻: {newsTitle}', { newsTitle })

    return await share.shareUrl(url, '新闻分享', text)
  }

  return {
    shareReport,
    shareTask,
    shareDocument,
    inviteUser,
    shareActivity,
    shareNews,
  }
}
