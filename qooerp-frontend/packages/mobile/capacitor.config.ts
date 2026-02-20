/**
 * Capacitor 配置文件
 * P6-CAP-T1: Capacitor 核心配置
 */
import type { CapacitorConfig } from '@capacitor/cli';

const config: CapacitorConfig = {
  appId: 'com.qoobot.qooerp',
  appName: 'QooERP',
  webDir: 'dist',
  bundledWebRuntime: false,

  // 服务器配置
  server: {
    // 开发环境下使用本地服务器
    url: process.env.NODE_ENV === 'development'
      ? 'http://localhost:5173'
      : undefined,
    cleartext: true,
    androidScheme: 'https',
  },

  // 插件配置
  plugins: {
    // App 插件
    App: {
      launchShowDuration: 0,
    },

    // 本地通知
    LocalNotifications: {
      smallIcon: 'ic_stat_icon_config_sample',
      iconColor: '#0066ff',
      sound: 'beep.wav',
    },

    // Push 通知
    PushNotifications: {
      presentationOptions: ['badge', 'sound', 'alert'],
    },

    // 状态栏
    StatusBar: {
      style: 'DARK',
      overlaysWebView: false,
    },

    // 启动屏
    SplashScreen: {
      launchShowDuration: 2000,
      launchAutoHide: true,
      backgroundColor: '#0066ff',
      androidSplashResourceName: 'splash',
      androidScaleType: 'CENTER_CROP',
      showSpinner: true,
      spinnerStyle: 'large',
    },

    // 键盘
    Keyboard: {
      resize: 'body',
      style: 'dark',
      resizeOnFullScreen: true,
    },

    // 剪贴板
    Clipboard: {
      permissions: ['clipboard-read', 'clipboard-write'],
    },

    // 文件系统
    Filesystem: {
      permissions: ['read', 'write', 'append', 'delete'],
    },

    // 相机
    Camera: {
      permissions: ['camera', 'photos'],
    },

    // 地理位置
      permissions: ['location'],
    },

    // 网络
    Network: {
      logRequests: process.env.NODE_ENV === 'development',
      logResponses: process.env.NODE_ENV === 'development',
    },

    // 设备信息
    Device: {
      info: true,
    },

    // 应用状态
    AppLauncher: {
      urls: ['qooerp://*'],
    },

    // 分享
    Share: {
      urls: ['https://www.qooerp.com/*'],
    },

    // 浏览器
    Browser: {
      height: '100%',
      width: '100%',
    },

    // 硬件返回按钮
    HardwareBackButton: {
      priority: 10,
    },
  },

  // iOS 配置
  ios: {
    contentInset: 'automatic',
    scrollEnabled: false,
    allowsLinkPreview: false,
    handleLinks: 'all',
  },

  // Android 配置
  android: {
    allowMixedContent: true,
    captureInput: true,
    webContentsDebuggingEnabled: process.env.NODE_ENV === 'development',
  },
};

export default config;
