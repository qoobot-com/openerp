/**
 * Electron 主进程入口
 * P6-ELEC-T1: Electron 主进程
 */

import { app, BrowserWindow, ipcMain } from 'electron'
import path from 'path'
import { fileURLToPath } from 'url'

const __filename = fileURLToPath(import.meta.url)
const __dirname = path.dirname(__filename)

let mainWindow: BrowserWindow | null = null

// 创建主窗口
const createWindow = () => {
  mainWindow = new BrowserWindow({
    width: 1400,
    height: 900,
    minWidth: 1200,
    minHeight: 700,
    show: false,
    backgroundColor: '#f3f4f6',
    webPreferences: {
      preload: path.join(__dirname, '../preload/index.js'),
      nodeIntegration: false,
      contextIsolation: true,
      webSecurity: true,
    },
    titleBarStyle: 'hiddenInset',
    trafficLightPosition: { x: 20, y: 20 },
  })

  // 开发环境加载本地服务器，生产环境加载构建文件
  if (process.env.VITE_DEV_SERVER_URL) {
    mainWindow.loadURL(process.env.VITE_DEV_SERVER_URL)
    mainWindow.webContents.openDevTools()
  } else {
    mainWindow.loadFile(path.join(__dirname, '../renderer/index.html'))
  }

  mainWindow.on('ready-to-show', () => {
    mainWindow?.show()
  })

  mainWindow.on('closed', () => {
    mainWindow = null
  })
}

// 应用就绪
app.whenReady().then(() => {
  createWindow()

  app.on('activate', () => {
    if (BrowserWindow.getAllWindows().length === 0) {
      createWindow()
    }
  })
})

// 所有窗口关闭
app.on('window-all-closed', () => {
  if (process.platform !== 'darwin') {
    app.quit()
  }
})

// IPC 通信处理
ipcMain.handle('app:getVersion', () => {
  return app.getVersion()
})

ipcMain.handle('app:getPath', (_, name: string) => {
  return app.getPath(name as any)
})

ipcMain.handle('app:quit', () => {
  app.quit()
})

ipcMain.handle('window:minimize', () => {
  mainWindow?.minimize()
})

ipcMain.handle('window:maximize', () => {
  mainWindow?.maximize()
})

ipcMain.handle('window:unmaximize', () => {
  mainWindow?.unmaximize()
})

ipcMain.handle('window:close', () => {
  mainWindow?.close()
})

ipcMain.handle('window:isMaximized', () => {
  return mainWindow?.isMaximized() || false
})

// 系统托盘
import { Tray, Menu, nativeImage } from 'electron'

let tray: Tray | null = null

const createTray = () => {
  const iconPath = path.join(__dirname, '../../assets/icon.png')
  const icon = nativeImage.createFromPath(iconPath)

  tray = new Tray(icon)

  const contextMenu = Menu.buildFromTemplate([
    { label: '显示主窗口', click: () => mainWindow?.show() },
    { label: '隐藏窗口', click: () => mainWindow?.hide() },
    { type: 'separator' },
    { label: '退出', click: () => app.quit() },
  ])

  tray.setToolTip('QooERP')
  tray.setContextMenu(contextMenu)

  tray.on('click', () => {
    if (mainWindow?.isVisible()) {
      mainWindow.hide()
    } else {
      mainWindow?.show()
    }
  })
}

app.whenReady().then(() => {
  createTray()
})

// 自动更新
import { autoUpdater } from 'electron-updater'

autoUpdater.checkForUpdatesAndNotify()

autoUpdater.on('checking-for-update', () => {
  mainWindow?.webContents.send('update:checking')
})

autoUpdater.on('update-available', (info) => {
  mainWindow?.webContents.send('update:available', info)
})

autoUpdater.on('update-not-available', () => {
  mainWindow?.webContents.send('update:not-available')
})

autoUpdater.on('download-progress', (progress) => {
  mainWindow?.webContents.send('update:downloading', progress)
})

autoUpdater.on('update-downloaded', (info) => {
  mainWindow?.webContents.send('update:downloaded', info)
})

ipcMain.on('update:install', () => {
  autoUpdater.quitAndInstall()
})

export {}
