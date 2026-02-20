<template>
  <div class="file-manager">
    <t-card title="文件管理" :bordered="false">
      <template #actions>
        <t-button theme="primary" @click="handleUpload">
          <template #icon><upload-icon /></template>
          上传文件
        </t-button>
        <t-button theme="default" @click="handleCreateFolder">
          <template #icon><folder-add-icon /></template>
          新建文件夹
        </t-button>
      </template>

      <!-- 面包屑 -->
      <div class="breadcrumb">
        <t-breadcrumb>
          <t-breadcrumb-item>全部文件</t-breadcrumb-item>
          <t-breadcrumb-item v-for="item in breadcrumb" :key="item" @click="handleNavigate(item)">
            {{ item }}
          </t-breadcrumb-item>
        </t-breadcrumb>
      </div>

      <!-- 文件列表 -->
      <div class="file-list">
        <div
          v-for="item in fileList"
          :key="item.id"
          class="file-item"
          :class="{ selected: selectedItems.includes(item.id) }"
          @click="handleSelect(item.id)"
          @dblclick="item.type === 'folder' && handleOpenFolder(item)"
        >
          <div class="file-icon">
            <component :is="item.icon" />
          </div>
          <div class="file-info">
            <div class="file-name">{{ item.name }}</div>
            <div class="file-meta">
              <span>{{ item.size }}</span>
              <span>{{ item.updateTime }}</span>
            </div>
          </div>
          <div class="file-actions">
            <t-dropdown :options="getActionOptions(item)" @click="({ value }) => handleAction(value, item)">
              <t-button variant="text">
                <more-icon />
              </t-button>
            </t-dropdown>
          </div>
        </div>
      </div>
    </t-card>

    <!-- 上传对话框 -->
    <t-dialog v-model:visible="uploadDialogVisible" header="上传文件" width="500px">
      <t-upload
        v-model="uploadFiles"
        action="/api/upload"
        theme="file-input"
        :max="10"
        :auto-upload="false"
      />
      <template #footer>
        <t-button theme="default" @click="uploadDialogVisible = false">取消</t-button>
        <t-button theme="primary" @click="handleConfirmUpload">上传</t-button>
      </template>
    </t-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { MessagePlugin } from 'tdesign-vue-next'
import {
  UploadIcon, FolderAddIcon, FolderIcon, FileIcon, ImageIcon, VideoIcon, FileExcelIcon, MoreIcon,
} from 'tdesign-icons-vue-next'

const breadcrumb = ref<string[]>([])
const selectedItems = ref<number[]>([])
const uploadDialogVisible = ref(false)
const uploadFiles = ref([])

const fileList = ref([
  { id: 1, name: '技术文档', type: 'folder', size: '-', updateTime: '2026-02-19', icon: FolderIcon },
  { id: 2, name: '项目资料', type: 'folder', size: '-', updateTime: '2026-02-18', icon: FolderIcon },
  { id: 3, name: '设计稿.png', type: 'image', size: '2.5MB', updateTime: '2026-02-19', icon: ImageIcon },
  { id: 4, name: '需求文档.docx', type: 'word', size: '1.2MB', updateTime: '2026-02-18', icon: FileIcon },
  { id: 5, name: '数据报表.xlsx', type: 'excel', size: '3.5MB', updateTime: '2026-02-17', icon: FileExcelIcon },
  { id: 6, name: '演示视频.mp4', type: 'video', size: '125MB', updateTime: '2026-02-16', icon: VideoIcon },
])

const getActionOptions = (item: any) => [
  { content: '下载', value: 'download' },
  { content: '移动', value: 'move' },
  { content: '重命名', value: 'rename' },
  { content: '删除', value: 'delete', theme: 'error' },
]

const handleUpload = () => {
  uploadDialogVisible.value = true
}

const handleCreateFolder = () => {
  MessagePlugin.info('新建文件夹')
}

const handleSelect = (id: number) => {
  const index = selectedItems.value.indexOf(id)
  if (index > -1) {
    selectedItems.value.splice(index, 1)
  } else {
    selectedItems.value.push(id)
  }
}

const handleOpenFolder = (item: any) => {
  breadcrumb.value.push(item.name)
  MessagePlugin.info(`打开文件夹: ${item.name}`)
}

const handleNavigate = (item: string) => {
  const index = breadcrumb.value.indexOf(item)
  breadcrumb.value = breadcrumb.value.slice(0, index + 1)
}

const handleAction = (action: string, item: any) => {
  MessagePlugin.success(`${action}: ${item.name}`)
}

const handleConfirmUpload = () => {
  MessagePlugin.success('上传成功')
  uploadDialogVisible.value = false
}
</script>

<style scoped lang="scss">
.file-manager {
  .breadcrumb {
    margin-bottom: 16px;
  }

  .file-list {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
    gap: 16px;

    .file-item {
      padding: 20px;
      border: 1px solid var(--td-component-border);
      border-radius: 8px;
      cursor: pointer;
      transition: all 0.3s;

      &:hover {
        border-color: var(--td-brand-color);
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
      }

      &.selected {
        border-color: var(--td-brand-color);
        background: var(--td-brand-color-light);
      }

      .file-icon {
        display: flex;
        justify-content: center;
        margin-bottom: 12px;
        font-size: 48px;
        color: var(--td-brand-color);
      }

      .file-info {
        .file-name {
          font-size: 14px;
          color: var(--td-text-color-primary);
          margin-bottom: 8px;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }

        .file-meta {
          display: flex;
          justify-content: space-between;
          font-size: 12px;
          color: var(--td-text-color-placeholder);
        }
      }

      .file-actions {
        position: absolute;
        top: 8px;
        right: 8px;
      }
    }
  }
}
</style>
