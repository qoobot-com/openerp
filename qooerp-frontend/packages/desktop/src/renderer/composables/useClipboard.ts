/**
 * Electron 剪贴板操作
 * P6-ELEC-T8: 剪贴板集成
 */
import { ref } from 'vue'
import { MessagePlugin } from 'tdesign-vue-next'

export interface ClipboardData {
  text?: string
  html?: string
  image?: Blob
  files?: File[]
}

export function useClipboard() {
  const isElectron = ref(!!(window as any).electronAPI)
  const clipboardSupported = ref<boolean>(!!navigator.clipboard)

  // 读取文本
  const readText = async (): Promise<string | null> => {
    if (!clipboardSupported.value) {
      MessagePlugin.warning('当前环境不支持剪贴板操作')
      return null
    }

    try {
      if (isElectron.value) {
        return await (window as any).electronAPI.clipboard.readText()
      }
      return await navigator.clipboard.readText()
    } catch (error) {
      MessagePlugin.error('读取剪贴板失败')
      return null
    }
  }

  // 写入文本
  const writeText = async (text: string): Promise<boolean> => {
    if (!clipboardSupported.value) {
      MessagePlugin.warning('当前环境不支持剪贴板操作')
      return false
    }

    try {
      if (isElectron.value) {
        await (window as any).electronAPI.clipboard.writeText(text)
      } else {
        await navigator.clipboard.writeText(text)
      }
      MessagePlugin.success('已复制到剪贴板')
      return true
    } catch (error) {
      MessagePlugin.error('复制到剪贴板失败')
      return false
    }
  }

  // 复制文本
  const copy = async (text: string): Promise<boolean> => {
    return await writeText(text)
  }

  // 粘贴文本
  const paste = async (): Promise<string | null> => {
    return await readText()
  }

  // 复制 HTML
  const copyHtml = async (html: string): Promise<boolean> => {
    if (!clipboardSupported.value) {
      MessagePlugin.warning('当前环境不支持剪贴板操作')
      return false
    }

    try {
      const blob = new Blob([html], { type: 'text/html' })
      const item = new ClipboardItem({ 'text/html': blob })
      await navigator.clipboard.write([item])
      MessagePlugin.success('已复制 HTML')
      return true
    } catch (error) {
      MessagePlugin.error('复制 HTML 失败')
      return false
    }
  }

  // 复制图片
  const copyImage = async (blob: Blob): Promise<boolean> => {
    if (!clipboardSupported.value) {
      MessagePlugin.warning('当前环境不支持剪贴板操作')
      return false
    }

    try {
      const item = new ClipboardItem({ 'image/png': blob })
      await navigator.clipboard.write([item])
      MessagePlugin.success('已复制图片')
      return true
    } catch (error) {
      MessagePlugin.error('复制图片失败')
      return false
    }
  }

  // 从剪贴板读取图片
  const readImage = async (): Promise<Blob | null> => {
    if (!clipboardSupported.value) {
      MessagePlugin.warning('当前环境不支持剪贴板操作')
      return null
    }

    try {
      const items = await navigator.clipboard.read()
      for (const item of items) {
        const image = await item.getType('image/png')
        return image
      }
      return null
    } catch (error) {
      MessagePlugin.error('读取图片失败')
      return null
    }
  }

  // 复制文件
  const copyFiles = async (files: File[]): Promise<boolean> => {
    if (!clipboardSupported.value) {
      MessagePlugin.warning('当前环境不支持剪贴板操作')
      return false
    }

    try {
      const items = files.map((file) => {
        return new ClipboardItem({
          [file.type]: file,
        })
      })
      await navigator.clipboard.write(items)
      MessagePlugin.success('已复制文件')
      return true
    } catch (error) {
      MessagePlugin.error('复制文件失败')
      return false
    }
  }

  // 清空剪贴板
  const clear = async (): Promise<boolean> => {
    try {
      if (isElectron.value) {
        await (window as any).electronAPI.clipboard.writeText('')
      }
      return true
    } catch (error) {
      MessagePlugin.error('清空剪贴板失败')
      return false
    }
  }

  // 检查剪贴板是否有数据
  const hasText = async (): Promise<boolean> => {
    const text = await readText()
    return text !== null && text.length > 0
  }

  return {
    // 状态
    isElectron,
    clipboardSupported,

    // 文本操作
    readText,
    writeText,
    copy,
    paste,

    // HTML 操作
    copyHtml,

    // 图片操作
    copyImage,
    readImage,

    // 文件操作
    copyFiles,

    // 其他操作
    clear,
    hasText,
  }
}

// 便捷的数据复制 Hook
export function useDataCopy() {
  const clipboard = useClipboard()

  // 复制对象
  const copyObject = async <T extends Record<string, any>>(data: T): Promise<boolean> => {
    const text = JSON.stringify(data, null, 2)
    return await clipboard.writeText(text)
  }

  // 复制表格数据
  const copyTableData = async (data: any[][], headers?: string[]): Promise<boolean> => {
    const rows = headers ? [headers, ...data] : data
    const text = rows.map((row) => row.join('\t')).join('\n')
    return await clipboard.writeText(text)
  }

  // 复制 ID 列表
  const copyIds = async (ids: (string | number)[]): Promise<boolean> => {
    const text = ids.join(',')
    return await clipboard.writeText(text)
  }

  // 复制链接
  const copyLink = async (url: string, title?: string): Promise<boolean> => {
    const text = title ? `${title}\n${url}` : url
    return await clipboard.writeText(text)
  }

  // 复制代码
  const copyCode = async (code: string, language: string = ''): Promise<boolean> => {
    const text = language ? `${language}\n${code}` : code
    return await clipboard.writeText(text)
  }

  return {
    copyObject,
    copyTableData,
    copyIds,
    copyLink,
    copyCode,
  }
}
