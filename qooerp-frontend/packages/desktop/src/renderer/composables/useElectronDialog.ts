/**
 * Electron 对话框
 * P6-ELEC-T9: 系统对话框集成
 */
import { ref } from 'vue'
import { DialogPlugin, MessagePlugin } from 'tdesign-vue-next'

export interface DialogOptions {
  title?: string
  message?: string
  detail?: string
  buttons?: Array<{ label: string; value?: any; primary?: boolean }>
  type?: 'info' | 'warning' | 'error' | 'question'
}

export interface FileDialogOptions {
  title?: string
  filters?: Array<{ name: string; extensions: string[] }>
  defaultPath?: string
  buttonLabel?: string
}

export interface SaveDialogOptions extends FileDialogOptions {
  defaultName?: string
  showsTagField?: boolean
}

export interface MessageDialogOptions {
  type?: 'info' | 'success' | 'warning' | 'error'
  title?: string
  content?: string
  confirmBtn?: string | { content: string; theme?: any }
  cancelBtn?: string | { content: string; theme?: any }
  onConfirm?: () => void | Promise<void>
  onCancel?: () => void
}

export function useElectronDialog() {
  const isElectron = ref(!!(window as any).electronAPI)

  // 显示信息对话框
  const showMessageBox = async (options: DialogOptions): Promise<any> => {
    if (!isElectron.value) {
      // Web 环境使用 TDesign Dialog
      return new Promise((resolve) => {
        const dialog = DialogPlugin.confirm({
          header: options.title || '提示',
          body: options.message || '',
          theme: options.type === 'error' ? 'danger' : options.type === 'warning' ? 'warning' : 'info',
          confirmBtn: options.buttons?.[0]?.label || '确定',
          onConfirm: () => {
            resolve(0)
            dialog.destroy()
          },
          onClose: () => {
            resolve(1)
            dialog.destroy()
          },
        })
      })
    }

    // Electron 环境
    return await (window as any).electronAPI.dialog.showMessageBox(options)
  }

  // 显示错误对话框
  const showErrorBox = (title: string, content: string): void => {
    if (isElectron.value) {
      (window as any).electronAPI.dialog.showErrorBox(title, content)
    } else {
      MessagePlugin.error(`${title}: ${content}`)
    }
  }

  // 打开文件对话框
  const showOpenDialog = async (options: FileDialogOptions = {}): Promise<string | string[] | null> => {
    if (!isElectron.value) {
      return new Promise((resolve) => {
        const input = document.createElement('input')
        input.type = 'file'
        if (options.filters) {
          input.accept = options.filters.map(f => f.extensions.map(e => `.${e}`).join(',')).join(',')
        }
        input.onchange = (e) => {
          const files = (e.target as HTMLInputElement).files
          resolve(files ? files[0]?.name || null : null)
        }
        input.oncancel = () => resolve(null)
        input.click()
      })
    }

    return await (window as any).electronAPI.dialog.showOpenDialog(options)
  }

  // 保存文件对话框
  const showSaveDialog = async (options: SaveDialogOptions = {}): Promise<string | null> => {
    if (!isElectron.value) {
      return new Promise((resolve) => {
        const input = document.createElement('input')
        input.type = 'file'
        input.nwsaveas = options.defaultName || 'download'
        if (options.filters) {
          input.accept = options.filters.map(f => f.extensions.map(e => `.${e}`).join(',')).join(',')
        }
        input.onchange = (e) => {
          const file = (e.target as HTMLInputElement).files?.[0]
          resolve(file?.name || null)
        }
        input.oncancel = () => resolve(null)
        input.click()
      })
    }

    return await (window as any).electronAPI.dialog.showSaveDialog(options)
  }

  // 确认对话框
  const confirm = async (message: string, title: string = '确认'): Promise<boolean> => {
    if (!isElectron.value) {
      return new Promise((resolve) => {
        const dialog = DialogPlugin.confirm({
          header: title,
          body: message,
          onConfirm: () => {
            resolve(true)
            dialog.destroy()
          },
          onClose: () => {
            resolve(false)
            dialog.destroy()
          },
        })
      })
    }

    const result = await showMessageBox({
      title,
      message,
      type: 'question',
      buttons: [
        { label: '确定', value: true, primary: true },
        { label: '取消', value: false },
      ],
    })
    return result === 0 || result === true
  }

  // 警告对话框
  const alert = async (message: string, title: string = '警告'): Promise<void> => {
    if (!isElectron.value) {
      MessagePlugin.warning(message)
      return
    }

    await showMessageBox({
      title,
      message,
      type: 'warning',
      buttons: [{ label: '确定', primary: true }],
    })
  }

  // 错误对话框
  const error = async (message: string, title: string = '错误'): Promise<void> => {
    if (!isElectron.value) {
      MessagePlugin.error(message)
      return
    }

    await showMessageBox({
      title,
      message,
      type: 'error',
      buttons: [{ label: '确定', primary: true }],
    })
  }

  // 信息对话框
  const info = async (message: string, title: string = '信息'): Promise<void> => {
    if (!isElectron.value) {
      MessagePlugin.info(message)
      return
    }

    await showMessageBox({
      title,
      message,
      type: 'info',
      buttons: [{ label: '确定', primary: true }],
    })
  }

  // 成功对话框
  const success = async (message: string, title: string = '成功'): Promise<void> => {
    if (!isElectron.value) {
      MessagePlugin.success(message)
      return
    }

    await showMessageBox({
      title,
      message,
      type: 'info',
      buttons: [{ label: '确定', primary: true }],
    })
  }

  // 提示对话框
  const prompt = async (message: string, defaultValue: string = '', title: string = '输入'): Promise<string | null> => {
    return new Promise((resolve) => {
      const dialog = DialogPlugin.confirm({
        header: title,
        body: `${message}<input type="text" id="prompt-input" value="${defaultValue}" style="width: 100%; margin-top: 12px; padding: 8px; border: 1px solid #dcdfe6; border-radius: 4px;">`,
        onConfirm: () => {
          const input = document.getElementById('prompt-input') as HTMLInputElement
          resolve(input?.value || null)
          dialog.destroy()
        },
        onClose: () => {
          resolve(null)
          dialog.destroy()
        },
      })
    })
  }

  // 进度对话框
  const showProgress = (title: string = '处理中...', total: number = 100): {
    update: (current: number) => void
    close: () => void
  } => {
    let current = 0
    const dialog = DialogPlugin.confirm({
      header: title,
      body: `
        <div style="padding: 20px;">
          <div style="margin-bottom: 8px;">${current}/${total}</div>
          <div style="height: 8px; background: #e7e7e7; border-radius: 4px; overflow: hidden;">
            <div id="progress-bar" style="height: 100%; background: var(--td-brand-color); width: ${ (current / total) * 100 }%; transition: width 0.3s;"></div>
          </div>
        </div>
      `,
      confirmBtn: null,
      cancelBtn: null,
    })

    return {
      update: (value: number) => {
        current = value
        const bar = document.getElementById('progress-bar')
        if (bar) {
          bar.style.width = `${(current / total) * 100}%`
        }
      },
      close: () => dialog.destroy(),
    }
  }

  return {
    // 状态
    isElectron,

    // 基础方法
    showMessageBox,
    showErrorBox,
    showOpenDialog,
    showSaveDialog,

    // 快捷方法
    confirm,
    alert,
    error,
    info,
    success,
    prompt,
    showProgress,
  }
}

// 业务对话框 Hook
export function useBusinessDialog() {
  const dialog = useElectronDialog()

  // 删除确认
  const confirmDelete = async (itemName: string = '此项目'): Promise<boolean> => {
    return await dialog.confirm(`确定要删除${itemName}吗？删除后不可恢复。`, '删除确认')
  }

  // 保存确认
  const confirmSave = async (): Promise<boolean> => {
    return await dialog.confirm('当前有未保存的更改，确定要保存吗？', '保存提示')
  }

  // 退出确认
  const confirmExit = async (): Promise<boolean> => {
    return await dialog.confirm('确定要退出吗？', '退出确认')
  }

  // 提交确认
  const confirmSubmit = async (title: string = ''): Promise<boolean> => {
    return await dialog.confirm(`确定要提交${title}吗？`, '提交确认')
  }

  // 导出提示
  const showExporting = () => {
    return dialog.showProgress('正在导出数据...', 100)
  }

  // 导入提示
  const showImporting = () => {
    return dialog.showProgress('正在导入数据...', 100)
  }

  // 处理提示
  const showProcessing = (title: string = '处理中...') => {
    return dialog.showProgress(title, 100)
  }

  return {
    confirmDelete,
    confirmSave,
    confirmExit,
    confirmSubmit,
    showExporting,
    showImporting,
    showProcessing,
  }
}
