/**
 * Capacitor 相机和相册
 * P6-CAP-T5: 拍照与图片选择
 */
import { ref } from 'vue'
import { Camera, CameraResultType, CameraSource, CameraDirection } from '@capacitor/camera'
import { Filesystem, Directory } from '@capacitor/filesystem'

export interface CameraOptions {
  quality?: number
  allowEditing?: boolean
  resultType?: CameraResultType
  source?: CameraSource
  direction?: CameraDirection
  width?: number
  height?: number
}

export interface Photo {
  dataUrl: string
  path: string
  webviewPath?: string
  format: string
  saved: boolean
}

export function useCapacitorCamera() {
  const photos = ref<Photo[]>([])
  const isCameraAvailable = ref(true)

  // 检查相机权限
  const checkPermissions = async () => {
    try {
      const status = await Camera.checkPermissions()
      return status
    } catch (error) {
      console.error('Failed to check camera permissions:', error)
      return null
    }
  }

  // 请求相机权限
  const requestPermissions = async () => {
    try {
      const status = await Camera.requestPermissions()
      return status
    } catch (error) {
      console.error('Failed to request camera permissions:', error)
      return null
    }
  }

  // 拍照
  const takePhoto = async (options: CameraOptions = {}): Promise<Photo | null> => {
    try {
      const image = await Camera.getPhoto({
        quality: options.quality || 90,
        allowEditing: options.allowEditing || false,
        resultType: options.resultType || CameraResultType.DataUrl,
        source: options.source || CameraSource.Camera,
        direction: options.direction || CameraDirection.Rear,
        width: options.width,
        height: options.height,
      })

      const photo: Photo = {
        dataUrl: image.dataUrl || '',
        path: image.path || '',
        webviewPath: image.webPath,
        format: image.format || 'jpeg',
        saved: false,
      }

      photos.value.push(photo)
      return photo
    } catch (error) {
      console.error('Failed to take photo:', error)
      return null
    }
  }

  // 从相册选择
  const pickImage = async (options: CameraOptions = {}): Promise<Photo | null> => {
    try {
      const image = await Camera.getPhoto({
        quality: options.quality || 90,
        allowEditing: options.allowEditing || false,
        resultType: options.resultType || CameraResultType.DataUrl,
        source: CameraSource.Photos,
      })

      const photo: Photo = {
        dataUrl: image.dataUrl || '',
        path: image.path || '',
        webviewPath: image.webPath,
        format: image.format || 'jpeg',
        saved: false,
      }

      photos.value.push(photo)
      return photo
    } catch (error) {
      console.error('Failed to pick image:', error)
      return null
    }
  }

  // 保存照片到文件系统
  const savePhoto = async (photo: Photo): Promise<string | null> => {
    try {
      if (!photo.dataUrl) return null

      const base64Data = photo.dataUrl.split(',')[1]
      const fileName = `photo_${Date.now()}.jpg`

      const result = await Filesystem.writeFile({
        path: fileName,
        data: base64Data,
        directory: Directory.Data,
      })

      photo.saved = true
      photo.path = result.uri

      return result.uri
    } catch (error) {
      console.error('Failed to save photo:', error)
      return null
    }
  }

  // 读取照片文件
  const readPhoto = async (path: string): Promise<string | null> => {
    try {
      const result = await Filesystem.readFile({
        path,
      })

      return result.data as string
    } catch (error) {
      console.error('Failed to read photo:', error)
      return null
    }
  }

  // 删除照片
  const deletePhoto = async (photo: Photo): Promise<void> => {
    try {
      if (photo.path && photo.saved) {
        await Filesystem.deleteFile({
          path: photo.path,
        })
      }
      photos.value = photos.value.filter((p) => p !== photo)
    } catch (error) {
      console.error('Failed to delete photo:', error)
    }
  }

  // 清空所有照片
  const clearPhotos = async (): Promise<void> => {
    photos.value = []
  }

  // 获取照片 Blob
  const getPhotoBlob = async (photo: Photo): Promise<Blob | null> => {
    try {
      const response = await fetch(photo.webviewPath || photo.dataUrl)
      return await response.blob()
    } catch (error) {
      console.error('Failed to get photo blob:', error)
      return null
    }
  }

  // 获取照片 File 对象
  const getPhotoFile = async (photo: Photo, fileName?: string): Promise<File | null> => {
    try {
      const blob = await getPhotoBlob(photo)
      if (!blob) return null

      return new File([blob], fileName || `photo_${Date.now()}.${photo.format}`, {
        type: `image/${photo.format}`,
      })
    } catch (error) {
      console.error('Failed to get photo file:', error)
      return null
    }
  }

  // 压缩图片
  const compressImage = async (photo: Photo, quality: number = 0.8): Promise<Photo | null> => {
    return new Promise((resolve) => {
      const img = new Image()
      img.onload = () => {
        const canvas = document.createElement('canvas')
        const ctx = canvas.getContext('2d')

        if (!ctx) {
          resolve(null)
          return
        }

        canvas.width = img.width
        canvas.height = img.height
        ctx.drawImage(img, 0, 0)

        const compressed = canvas.toDataURL(`image/${photo.format}`, quality)

        resolve({
          ...photo,
          dataUrl: compressed,
        })
      }
      img.onerror = () => resolve(null)
      img.src = photo.dataUrl || photo.webviewPath || ''
    })
  }

  return {
    // 状态
    photos,
    isCameraAvailable,

    // 权限
    checkPermissions,
    requestPermissions,

    // 操作
    takePhoto,
    pickImage,
    savePhoto,
    readPhoto,
    deletePhoto,
    clearPhotos,

    // 转换
    getPhotoBlob,
    getPhotoFile,
    compressImage,
  }
}
