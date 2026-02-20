/**
 * Electron 原生文件系统操作
 * P6-ELEC-T5: 文件系统集成
 */
import { ref } from 'vue'
import { MessagePlugin } from 'tdesign-vue-next'

export interface FileSelectOptions {
  title?: string
  filters?: Array<{ name: string; extensions: string[] }>
  properties?: Array<'openFile' | 'openDirectory' | 'multiSelections'>
  defaultPath?: string
}

export interface DirectorySelectOptions {
  title?: string
  properties?: Array<'openDirectory' | 'multiSelections'>
  defaultPath?: string
}

export function useNativeFs() {
  const isElectron = ref(!!(window as any).electronAPI)

  // 选择文件
  const selectFile = async (options: FileSelectOptions = {}): Promise<string | string[] | null> => {
    if (!isElectron.value) {
      // Web 环境使用文件选择器
      return new Promise((resolve) => {
        const input = document.createElement('input')
        input.type = 'file'
        input.multiple = options.properties?.includes('multiSelections')
        if (options.filters) {
          input.accept = options.filters.map(f => f.extensions.map(e => `.${e}`).join(',')).join(',')
        }
        input.onchange = (e) => {
          const files = (e.target as HTMLInputElement).files
          if (files) {
            if (options.properties?.includes('multiSelections')) {
              resolve(Array.from(files).map(f => f.name))
            } else {
              resolve(files[0]?.name || null)
            }
          } else {
            resolve(null)
          }
        }
        input.oncancel = () => resolve(null)
        input.click()
      })
    }

    const api = (window as any).electronAPI
    try {
      return await api.fs.selectFile(options)
    } catch (error) {
      MessagePlugin.error('选择文件失败')
      return null
    }
  }

  // 选择目录
  const selectDirectory = async (options: DirectorySelectOptions = {}): Promise<string | string[] | null> => {
    if (!isElectron.value) {
      MessagePlugin.warning('Web 环境不支持目录选择')
      return null
    }

    const api = (window as any).electronAPI
    try {
      return await api.fs.selectDirectory(options)
    } catch (error) {
      MessagePlugin.error('选择目录失败')
      return null
    }
  }

  // 读取文件
  const readFile = async (filePath: string): Promise<string | null> => {
    if (!isElectron.value) {
      MessagePlugin.warning('Web 环境不支持直接读取文件')
      return null
    }

    const api = (window as any).electronAPI
    try {
      return await api.fs.readFile(filePath)
    } catch (error) {
      MessagePlugin.error('读取文件失败')
      return null
    }
  }

  // 写入文件
  const writeFile = async (filePath: string, content: string): Promise<boolean> => {
    if (!isElectron.value) {
      MessagePlugin.warning('Web 环境不支持直接写入文件')
      return false
    }

    const api = (window as any).electronAPI
    try {
      await api.fs.writeFile(filePath, content)
      MessagePlugin.success('文件保存成功')
      return true
    } catch (error) {
      MessagePlugin.error('保存文件失败')
      return false
    }
  }

  // 导出数据为文件
  const exportFile = (data: any, filename: string, type: string = 'application/json'): void => {
    const blob = new Blob([JSON.stringify(data, null, 2)], { type })
    const url = URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = filename
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    URL.revokeObjectURL(url)
    MessagePlugin.success('文件导出成功')
  }

  // 导入文件
  const importFile = (accept: string = '.json,.csv,.xlsx'): Promise<File | null> => {
    return new Promise((resolve) => {
      const input = document.createElement('input')
      input.type = 'file'
      input.accept = accept
      input.onchange = (e) => {
        const files = (e.target as HTMLInputElement).files
        resolve(files?.[0] || null)
      }
      input.oncancel = () => resolve(null)
      input.click()
    })
  }

  // 读取文件内容
  const readFileContent = (file: File): Promise<string> => {
    return new Promise((resolve, reject) => {
      const reader = new FileReader()
      reader.onload = (e) => resolve(e.target?.result as string)
      reader.onerror = (e) => reject(e)
      reader.readAsText(file)
    })
  }

  // 下载文件
  const downloadFile = (url: string, filename: string): void => {
    const link = document.createElement('a')
    link.href = url
    link.download = filename
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
  }

  // 上传文件
  const uploadFile = async (file: File, uploadUrl: string, options: any = {}): Promise<any> => {
    const formData = new FormData()
    formData.append('file', file)

    for (const key in options) {
      formData.append(key, options[key])
    }

    try {
      const response = await fetch(uploadUrl, {
        method: 'POST',
        body: formData,
      })
      const result = await response.json()
      MessagePlugin.success('文件上传成功')
      return result
    } catch (error) {
      MessagePlugin.error('文件上传失败')
      throw error
    }
  }

  return {
    isElectron,
    selectFile,
    selectDirectory,
    readFile,
    writeFile,
    exportFile,
    importFile,
    readFileContent,
    downloadFile,
    uploadFile,
  }
}
