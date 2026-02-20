/**
 * Electron 预加载脚本
 * P6-ELEC-T2: 预加载脚本与安全隔离
 */

import { contextBridge, ipcRenderer } from 'electron'

// 暴露安全的 API 到渲染进程
contextBridge.exposeInMainWorld('electronAPI', {
  // 应用相关
  app: {
    getVersion: () => ipcRenderer.invoke('app:getVersion'),
    getPath: (name: string) => ipcRenderer.invoke('app:getPath', name),
    quit: () => ipcRenderer.invoke('app:quit'),
  },

  // 窗口相关
  window: {
    minimize: () => ipcRenderer.invoke('window:minimize'),
    maximize: () => ipcRenderer.invoke('window:maximize'),
    unmaximize: () => ipcRenderer.invoke('window:unmaximize'),
    close: () => ipcRenderer.invoke('window:close'),
    isMaximized: () => ipcRenderer.invoke('window:isMaximized'),
  },

  // 自动更新
  update: {
    onChecking: (callback: () => void) => {
      ipcRenderer.on('update:checking', callback)
    },
    onAvailable: (callback: (info: any) => void) => {
      ipcRenderer.on('update:available', (_, info) => callback(info))
    },
    onNotAvailable: (callback: () => void) => {
      ipcRenderer.on('update:not-available', callback)
    },
    onDownloading: (callback: (progress: any) => void) => {
      ipcRenderer.on('update:downloading', (_, progress) => callback(progress))
    },
    onDownloaded: (callback: (info: any) => void) => {
      ipcRenderer.on('update:downloaded', (_, info) => callback(info))
    },
    install: () => ipcRenderer.send('update:install'),
    removeAllListeners: () => {
      ipcRenderer.removeAllListeners('update:checking')
      ipcRenderer.removeAllListeners('update:available')
      ipcRenderer.removeAllListeners('update:not-available')
      ipcRenderer.removeAllListeners('update:downloading')
      ipcRenderer.removeAllListeners('update:downloaded')
    },
  },

  // 文件系统
  fs: {
    selectFile: (options: any) => ipcRenderer.invoke('fs:selectFile', options),
    selectDirectory: (options: any) => ipcRenderer.invoke('fs:selectDirectory', options),
    readFile: (path: string) => ipcRenderer.invoke('fs:readFile', path),
    writeFile: (path: string, content: string) => ipcRenderer.invoke('fs:writeFile', path, content),
  },

  // 本地存储
  store: {
    get: (key: string) => ipcRenderer.invoke('store:get', key),
    set: (key: string, value: any) => ipcRenderer.invoke('store:set', key, value),
    delete: (key: string) => ipcRenderer.invoke('store:delete', key),
    clear: () => ipcRenderer.invoke('store:clear'),
  },

  // 通知
  notification: {
    send: (options: any) => ipcRenderer.invoke('notification:send', options),
  },

  // 剪贴板
  clipboard: {
    readText: () => ipcRenderer.invoke('clipboard:readText'),
    writeText: (text: string) => ipcRenderer.invoke('clipboard:writeText', text),
  },
})

// 类型定义
export interface ElectronAPI {
  app: {
    getVersion: () => Promise<string>
    getPath: (name: string) => Promise<string>
    quit: () => Promise<void>
  }
  window: {
    minimize: () => Promise<void>
    maximize: () => Promise<void>
    unmaximize: () => Promise<void>
    close: () => Promise<void>
    isMaximized: () => Promise<boolean>
  }
  update: {
    onChecking: (callback: () => void) => void
    onAvailable: (callback: (info: any) => void) => void
    onNotAvailable: (callback: () => void) => void
    onDownloading: (callback: (progress: any) => void) => void
    onDownloaded: (callback: (info: any) => void) => void
    install: () => void
    removeAllListeners: () => void
  }
  fs: {
    selectFile: (options: any) => Promise<string | null>
    selectDirectory: (options: any) => Promise<string | null>
    readFile: (path: string) => Promise<string>
    writeFile: (path: string, content: string) => Promise<void>
  }
  store: {
    get: (key: string) => Promise<any>
    set: (key: string, value: any) => Promise<void>
    delete: (key: string) => Promise<void>
    clear: () => Promise<void>
  }
  notification: {
    send: (options: any) => Promise<void>
  }
  clipboard: {
    readText: () => Promise<string>
    writeText: (text: string) => Promise<void>
  }
}

declare global {
  interface Window {
    electronAPI: ElectronAPI
  }
}
