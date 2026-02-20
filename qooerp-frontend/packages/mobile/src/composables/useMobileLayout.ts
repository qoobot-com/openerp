/**
 * 移动端布局适配
 * P6-CAP-T10: 响应式布局与手势
 */
import { ref, onMounted, onUnmounted } from 'vue'
import { StatusBar, Style } from '@capacitor/status-bar'
import { SafeArea } from 'capacitor-plugin-safe-area'

export function useMobileLayout() {
  const safeAreaInsets = ref({
    top: 0,
    right: 0,
    bottom: 0,
    left: 0,
  })

  const statusBarHeight = ref(0)
  const isStatusBarVisible = ref(true)
  const isKeyboardVisible = ref(false)
  const keyboardHeight = ref(0)

  // 初始化安全区域
  const initSafeArea = async () => {
    try {
      const { insets } = await SafeArea.getSafeAreaInsets()
      safeAreaInsets.value = insets
    } catch (error) {
      console.error('Failed to get safe area insets:', error)
    }
  }

  // 初始化状态栏
  const initStatusBar = async () => {
    try {
      const { visible } = await StatusBar.getInfo()
      isStatusBarVisible.value = visible

      if (visible) {
        const { height } = await StatusBar.getInfo()
        statusBarHeight.value = height || 0
      }
    } catch (error) {
      console.error('Failed to init status bar:', error)
    }
  }

  // 设置状态栏样式
  const setStatusBarStyle = async (style: 'light' | 'dark' = 'dark') => {
    try {
      await StatusBar.setStyle({ style: style === 'light' ? Style.Light : Style.Dark })
    } catch (error) {
      console.error('Failed to set status bar style:', error)
    }
  }

  // 设置状态栏背景色
  const setStatusBarBackgroundColor = async (color: string) => {
    try {
      await StatusBar.setBackgroundColor({ color })
    } catch (error) {
      console.error('Failed to set status bar background color:', error)
    }
  }

  // 显示状态栏
  const showStatusBar = async () => {
    try {
      await StatusBar.show()
      isStatusBarVisible.value = true
      await initStatusBar()
    } catch (error) {
      console.error('Failed to show status bar:', error)
    }
  }

  // 隐藏状态栏
  const hideStatusBar = async () => {
    try {
      await StatusBar.hide()
      isStatusBarVisible.value = false
    } catch (error) {
      console.error('Failed to hide status bar:', error)
    }
  }

  // 覆盖状态栏
  const setStatusBarOverlaysWebView = async (overlays: boolean) => {
    try {
      await StatusBar.setOverlaysWebView({ overlays })
    } catch (error) {
      console.error('Failed to set status bar overlays:', error)
    }
  }

  // 监听键盘事件
  const setupKeyboardListeners = () => {
    const { Keyboard } = require('@capacitor/keyboard')

    Keyboard.addListener('keyboardWillShow', (info) => {
      isKeyboardVisible.value = true
      keyboardHeight.value = info.keyboardHeight
    })

    Keyboard.addListener('keyboardWillHide', () => {
      isKeyboardVisible.value = false
      keyboardHeight.value = 0
    })
  }

  // 清理键盘监听器
  const cleanupKeyboardListeners = async () => {
    try {
      const { Keyboard } = await import('@capacitor/keyboard')
      await Keyboard.removeAllListeners()
    } catch (error) {
      console.error('Failed to cleanup keyboard listeners:', error)
    }
  }

  // 获取可用高度
  const getAvailableHeight = (windowHeight: number): number => {
    return windowHeight - safeAreaInsets.value.top - safeAreaInsets.value.bottom
  }

  // 获取安全区域样式
  const getSafeAreaStyle = (): Record<string, string> => {
    return {
      paddingTop: `${safeAreaInsets.value.top}px`,
      paddingRight: `${safeAreaInsets.value.right}px`,
      paddingBottom: `${safeAreaInsets.value.bottom}px`,
      paddingLeft: `${safeAreaInsets.value.left}px`,
    }
  }

  // 获取内容区域样式
  const getContentStyle = (): Record<string, string> => {
    return {
      height: `calc(100vh - ${safeAreaInsets.value.top}px - ${safeAreaInsets.value.bottom}px)`,
      marginTop: `${safeAreaInsets.value.top}px`,
      marginBottom: `${safeAreaInsets.value.bottom}px`,
    }
  }

  // 检测是否为刘海屏
  const isNotchedScreen = (): boolean => {
    return safeAreaInsets.value.top > 20
  }

  // 检测是否为全面屏手势
  const hasHomeIndicator = (): boolean => {
    return safeAreaInsets.value.bottom > 0
  }

  // 防止内容被状态栏遮挡
  const preventOverlap = (element: HTMLElement): void => {
    element.style.paddingTop = `${safeAreaInsets.value.top}px`
    element.style.paddingLeft = `${safeAreaInsets.value.left}px`
    element.style.paddingRight = `${safeAreaInsets.value.right}px`
  }

  // 处理键盘遮挡
  const handleKeyboardOverlap = (element: HTMLElement): void => {
    if (isKeyboardVisible.value) {
      element.style.paddingBottom = `${keyboardHeight.value}px`
    } else {
      element.style.paddingBottom = `${safeAreaInsets.value.bottom}px`
    }
  }

  onMounted(async () => {
    await initSafeArea()
    await initStatusBar()
    setupKeyboardListeners()

    // 监听屏幕方向变化
    window.addEventListener('resize', initSafeArea)
  })

  onUnmounted(async () => {
    await cleanupKeyboardListeners()
    window.removeEventListener('resize', initSafeArea)
  })

  return {
    // 状态
    safeAreaInsets,
    statusBarHeight,
    isStatusBarVisible,
    isKeyboardVisible,
    keyboardHeight,

    // 初始化
    initSafeArea,
    initStatusBar,

    // 状态栏
    setStatusBarStyle,
    setStatusBarBackgroundColor,
    showStatusBar,
    hideStatusBar,
    setStatusBarOverlaysWebView,

    // 工具方法
    getAvailableHeight,
    getSafeAreaStyle,
    getContentStyle,
    isNotchedScreen,
    hasHomeIndicator,
    preventOverlap,
    handleKeyboardOverlap,
  }
}

/**
 * 手势操作 Hook
 */
export function useGestures() {
  const swipeDirection = ref<'left' | 'right' | 'up' | 'down' | null>(null)
  const isSwiping = ref(false)

  // 滑动手势
  const handleSwipe = (
    element: HTMLElement,
    options: {
      onSwipeLeft?: () => void
      onSwipeRight?: () => void
      onSwipeUp?: () => void
      onSwipeDown?: () => void
      threshold?: number
    }
  ) => {
    const threshold = options.threshold || 50
    let startX = 0
    let startY = 0

    element.addEventListener('touchstart', (e) => {
      startX = e.touches[0].clientX
      startY = e.touches[0].clientY
      isSwiping.value = true
    })

    element.addEventListener('touchmove', (e) => {
      if (!isSwiping.value) return
      e.preventDefault()
    })

    element.addEventListener('touchend', (e) => {
      if (!isSwiping.value) return

      const endX = e.changedTouches[0].clientX
      const endY = e.changedTouches[0].clientY
      const diffX = endX - startX
      const diffY = endY - startY

      if (Math.abs(diffX) > Math.abs(diffY)) {
        // 水平滑动
        if (Math.abs(diffX) > threshold) {
          if (diffX > 0) {
            swipeDirection.value = 'right'
            options.onSwipeRight?.()
          } else {
            swipeDirection.value = 'left'
            options.onSwipeLeft?.()
          }
        }
      } else {
        // 垂直滑动
        if (Math.abs(diffY) > threshold) {
          if (diffY > 0) {
            swipeDirection.value = 'down'
            options.onSwipeDown?.()
          } else {
            swipeDirection.value = 'up'
            options.onSwipeUp?.()
          }
        }
      }

      isSwiping.value = false
    })
  }

  // 长按手势
  const handleLongPress = (
    element: HTMLElement,
    callback: () => void,
    duration: number = 800
  ) => {
    let timer: number | null = null

    element.addEventListener('touchstart', () => {
      timer = window.setTimeout(() => {
        callback()
      }, duration)
    })

    element.addEventListener('touchend', () => {
      if (timer) {
        clearTimeout(timer)
        timer = null
      }
    })

    element.addEventListener('touchmove', () => {
      if (timer) {
        clearTimeout(timer)
        timer = null
      }
    })
  }

  // 双击手势
  const handleDoubleTap = (element: HTMLElement, callback: () => void) => {
    let lastTap = 0

    element.addEventListener('touchend', (e) => {
      const currentTime = new Date().getTime()
      const tapLength = currentTime - lastTap

      if (tapLength < 300 && tapLength > 0) {
        callback()
        e.preventDefault()
      }

      lastTap = currentTime
    })
  }

  // 捏合手势
  const handlePinch = (
    element: HTMLElement,
    callback: (scale: number) => void
  ) => {
    let initialDistance = 0

    element.addEventListener('touchstart', (e) => {
      if (e.touches.length === 2) {
        const dx = e.touches[0].clientX - e.touches[1].clientX
        const dy = e.touches[0].clientY - e.touches[1].clientY
        initialDistance = Math.sqrt(dx * dx + dy * dy)
      }
    })

    element.addEventListener('touchmove', (e) => {
      if (e.touches.length === 2) {
        const dx = e.touches[0].clientX - e.touches[1].clientX
        const dy = e.touches[0].clientY - e.touches[1].clientY
        const distance = Math.sqrt(dx * dx + dy * dy)
        const scale = distance / initialDistance

        callback(scale)
      }
    })
  }

  return {
    swipeDirection,
    isSwiping,
    handleSwipe,
    handleLongPress,
    handleDoubleTap,
    handlePinch,
  }
}
