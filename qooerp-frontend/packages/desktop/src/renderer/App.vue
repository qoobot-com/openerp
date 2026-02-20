/**
 * Electron 渲染进程应用入口
 * P6-ELEC-T3: 渲染进程与窗口管理
 */
<template>
  <div id="app">
    <!-- 自定义标题栏 -->
    <div class="titlebar" v-if="isElectron">
      <div class="titlebar-left">
        <img src="/icon.png" alt="logo" class="logo" />
        <span class="title">QooERP</span>
      </div>
      <div class="titlebar-center">
        <span class="window-title">{{ documentTitle }}</span>
      </div>
      <div class="titlebar-right">
        <div class="titlebar-button" @click="minimizeWindow">
          <MinusIcon size="16px" />
        </div>
        <div class="titlebar-button" @click="toggleMaximize">
          <component :is="isMaximized ? 'RestoreIcon' : 'MaximizeIcon'" size="16px" />
        </div>
        <div class="titlebar-button close" @click="closeWindow">
          <CloseIcon size="16px" />
        </div>
      </div>
    </div>

    <!-- 主内容区域 -->
    <div class="app-content" :class="{ 'with-titlebar': isElectron }">
      <router-view />
    </div>

    <!-- 更新提示 -->
    <t-dialog
      v-model:visible="updateDialogVisible"
      header="发现新版本"
      :confirm-btn="updateDownloaded ? '立即安装' : '后台下载'"
      @confirm="handleUpdateConfirm"
    >
      <div class="update-content">
        <div v-if="updateStatus === 'checking'">
          <t-loading text="正在检查更新..." />
        </div>
        <div v-else-if="updateStatus === 'downloading'">
          <t-loading text="正在下载更新..." />
          <t-progress
            :percentage="updateProgress.percent"
            :label="false"
            theme="primary"
            style="margin-top: 16px"
          />
        </div>
        <div v-else-if="updateStatus === 'available'">
          <div class="update-info">
            <div class="update-title">版本 {{ updateInfo?.version }}</div>
            <div class="update-desc">{{ updateInfo?.releaseNotes || '' }}</div>
          </div>
        </div>
        <div v-else-if="updateStatus === 'downloaded'">
          <div class="update-success">
            <CheckCircleFilledIcon size="48px" style="color: var(--td-success-color)" />
            <p>更新已下载完成</p>
          </div>
        </div>
      </div>
    </t-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useRoute } from 'vue-router'
import {
  MinusIcon,
  MaximizeIcon,
  RestoreIcon,
  CloseIcon,
  CheckCircleFilledIcon,
} from 'tdesign-icons-vue-next'

const route = useRoute()

// 判断是否在 Electron 环境
const isElectron = !!(window as any).electronAPI

// 窗口标题
const documentTitle = ref('QooERP - 企业管理平台')

// 更新相关
const updateDialogVisible = ref(false)
const updateStatus = ref<'checking' | 'available' | 'downloading' | 'downloaded' | 'none'>('none')
const updateInfo = ref<any>(null)
const updateProgress = ref<any>({})
const updateDownloaded = ref(false)

// 窗口状态
const isMaximized = ref(false)

// 窗口操作
const minimizeWindow = () => {
  if (isElectron) {
    window.electronAPI.window.minimize()
  }
}

const toggleMaximize = async () => {
  if (isElectron) {
    const maximized = await window.electronAPI.window.isMaximized()
    if (maximized) {
      window.electronAPI.window.unmaximize()
    } else {
      window.electronAPI.window.maximize()
    }
    isMaximized.value = !maximized
  }
}

const closeWindow = () => {
  if (isElectron) {
    window.electronAPI.window.close()
  }
}

// 更新处理
const handleUpdateConfirm = () => {
  if (updateDownloaded.value) {
    window.electronAPI.update.install()
  } else if (isElectron) {
    updateStatus.value = 'downloading'
  }
}

// 监听更新事件
const setupUpdateListeners = () => {
  if (!isElectron) return

  const api = window.electronAPI

  api.update.onChecking(() => {
    updateStatus.value = 'checking'
    updateDialogVisible.value = true
  })

  api.update.onAvailable((info) => {
    updateStatus.value = 'available'
    updateInfo.value = info
  })

  api.update.onNotAvailable(() => {
    updateStatus.value = 'none'
    updateDialogVisible.value = false
  })

  api.update.onDownloading((progress) => {
    updateStatus.value = 'downloading'
    updateProgress.value = progress
  })

  api.update.onDownloaded((info) => {
    updateStatus.value = 'downloaded'
    updateDownloaded.value = true
    updateInfo.value = info
  })
}

// 清理更新监听
const cleanupUpdateListeners = () => {
  if (!isElectron) return
  window.electronAPI.update.removeAllListeners()
}

// 监听路由变化更新标题
onMounted(() => {
  setupUpdateListeners()

  documentTitle.value = `${route.meta.title || 'QooERP'} - 企业管理平台`

  if (isElectron) {
    window.electronAPI.window.isMaximized().then((maximized) => {
      isMaximized.value = maximized
    })
  }
})

onUnmounted(() => {
  cleanupUpdateListeners()
})
</script>

<style scoped lang="scss">
#app {
  height: 100vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.titlebar {
  height: 40px;
  background: var(--td-bg-color-container);
  border-bottom: 1px solid var(--td-component-border);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 12px;
  -webkit-app-region: drag;
  user-select: none;

  .titlebar-left {
    display: flex;
    align-items: center;
    gap: 8px;

    .logo {
      width: 24px;
      height: 24px;
    }

    .title {
      font-size: 14px;
      font-weight: 500;
      color: var(--td-text-color-primary);
    }
  }

  .titlebar-center {
    position: absolute;
    left: 50%;
    transform: translateX(-50%);

    .window-title {
      font-size: 13px;
      color: var(--td-text-color-secondary);
    }
  }

  .titlebar-right {
    display: flex;
    align-items: center;

    .titlebar-button {
      width: 32px;
      height: 32px;
      display: flex;
      align-items: center;
      justify-content: center;
      border-radius: 4px;
      cursor: pointer;
      color: var(--td-text-color-primary);
      -webkit-app-region: no-drag;
      transition: background-color 0.2s;

      &:hover {
        background: var(--td-bg-color-container-hover);
      }

      &.close:hover {
        background: #ff4d4f;
        color: white;
      }
    }
  }
}

.app-content {
  flex: 1;
  overflow: auto;
  display: flex;
  flex-direction: column;

  &.with-titlebar {
    height: calc(100vh - 40px);
  }
}

.update-content {
  padding: 24px 0;
  text-align: center;

  .update-info {
    .update-title {
      font-size: 18px;
      font-weight: 600;
      color: var(--td-text-color-primary);
      margin-bottom: 12px;
    }

    .update-desc {
      font-size: 14px;
      color: var(--td-text-color-secondary);
      line-height: 1.6;
    }
  }

  .update-success {
    padding: 24px;
  }
}
</style>
