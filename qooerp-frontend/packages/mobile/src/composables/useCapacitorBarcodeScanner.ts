/**
 * Capacitor 条码扫描
 * P6-CAP-T9: 扫码功能集成
 */
import { ref } from 'vue'
import { BarcodeScanner } from '@capacitor/barcode-scanner'

export interface BarcodeScanOptions {
  text?: string
  color?: string
  textColor?: string
  cameraDirection?: 'front' | 'back'
}

export interface BarcodeResult {
  format: string
  content: string
}

export function useCapacitorBarcodeScanner() {
  const isScanning = ref(false)
  const scanResult = ref<BarcodeResult | null>(null)
  const permissionGranted = ref(false)

  // 检查权限
  const checkPermissions = async () => {
    try {
      const status = await BarcodeScanner.checkPermissions()
      permissionGranted.value = status.camera === 'granted'
      return status
    } catch (error) {
      console.error('Failed to check barcode scanner permissions:', error)
      return null
    }
  }

  // 请求权限
  const requestPermissions = async (): Promise<boolean> => {
    try {
      const status = await BarcodeScanner.checkPermissions()
      if (status.camera !== 'granted') {
        await BarcodeScanner.requestPermissions()
      }
      const newStatus = await BarcodeScanner.checkPermissions()
      permissionGranted.value = newStatus.camera === 'granted'
      return permissionGranted.value
    } catch (error) {
      console.error('Failed to request barcode scanner permissions:', error)
      return false
    }
  }

  // 开始扫描
  const startScan = async (options: BarcodeScanOptions = {}): Promise<BarcodeResult | null> => {
    try {
      // 先请求权限
      await requestPermissions()

      // 准备扫描
      await BarcodeScanner.prepare()

      // 隐藏背景（显示摄像头）
      document.querySelector('body')?.classList.add('barcode-scanner-active')

      // 开始扫描
      isScanning.value = true
      const result = await BarcodeScanner.startScan()

      // 恢复背景
      document.querySelector('body')?.classList.remove('barcode-scanner-active')

      isScanning.value = false
      scanResult.value = {
        format: result.format,
        content: result.content,
      }

      return scanResult.value
    } catch (error) {
      console.error('Failed to start scan:', error)
      document.querySelector('body')?.classList.remove('barcode-scanner-active')
      isScanning.value = false
      return null
    }
  }

  // 停止扫描
  const stopScan = async (): Promise<void> => {
    try {
      await BarcodeScanner.stopScan()
      document.querySelector('body')?.classList.remove('barcode-scanner-active')
      isScanning.value = false
    } catch (error) {
      console.error('Failed to stop scan:', error)
    }
  }

  // 生成二维码
  const generateQRCode = async (
    text: string,
    options: { width?: number; height?: number } = {}
  ): Promise<string | null> => {
    try {
      // 使用 Canvas 生成二维码
      const QRCode = await import('qrcode')
      const url = await QRCode.toDataURL(text, {
        width: options.width || 200,
        height: options.height || 200,
      })
      return url
    } catch (error) {
      console.error('Failed to generate QR code:', error)
      return null
    }
  }

  // 生成条形码
  const generateBarcode = async (
    text: string,
    options: { format?: string; width?: number; height?: number } = {}
  ): Promise<string | null> => {
    try {
      const JsBarcode = await import('jsbarcode')
      const canvas = document.createElement('canvas')
      JsBarcode(canvas, text, {
        format: (options.format as any) || 'CODE128',
        width: options.width || 2,
        height: options.height || 100,
        displayValue: true,
      })
      return canvas.toDataURL()
    } catch (error) {
      console.error('Failed to generate barcode:', error)
      return null
    }
  }

  // 下载二维码
  const downloadQRCode = async (dataUrl: string, filename: string = 'qrcode.png'): Promise<void> => {
    try {
      const link = document.createElement('a')
      link.href = dataUrl
      link.download = filename
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
    } catch (error) {
      console.error('Failed to download QR code:', error)
    }
  }

  // 识别图片中的条码
  const scanImage = async (imageUrl: string): Promise<BarcodeResult | null> => {
    try {
      // 使用 jsQR 识别图片中的二维码
      const jsQR = await import('jsqr')
      const response = await fetch(imageUrl)
      const blob = await response.blob()
      const imageData = await createImageBitmap(blob)

      // 绘制到 canvas
      const canvas = document.createElement('canvas')
      const ctx = canvas.getContext('2d')
      canvas.width = imageData.width
      canvas.height = imageData.height
      ctx?.drawImage(imageData, 0, 0)

      // 获取像素数据
      const imageData2 = ctx?.getImageData(0, 0, canvas.width, canvas.height)

      if (imageData2) {
        const code = jsQR(imageData2.data, imageData2.width, imageData2.height)
        if (code) {
          return {
            format: 'QR_CODE',
            content: code.data,
          }
        }
      }

      return null
    } catch (error) {
      console.error('Failed to scan image:', error)
      return null
    }
  }

  // 验证条码格式
  const validateBarcode = (content: string, format: string): boolean => {
    switch (format) {
      case 'EAN_13':
        return /^\d{13}$/.test(content)
      case 'EAN_8':
        return /^\d{8}$/.test(content)
      case 'UPC_A':
        return /^\d{12}$/.test(content)
      case 'CODE_128':
        return content.length > 0
      case 'QR_CODE':
        return content.length > 0
      default:
        return content.length > 0
    }
  }

  // 格式化条码显示
  const formatBarcode = (content: string, format: string): string => {
    switch (format) {
      case 'EAN_13':
        return content.replace(/(\d{1})(\d{6})(\d{6})/, '$1 $2 $3')
      case 'EAN_8':
        return content.replace(/(\d{4})(\d{4})/, '$1 $2')
      default:
        return content
    }
  }

  // 检测条码类型
  const detectBarcodeType = (content: string): string => {
    if (/^\d{13}$/.test(content)) return 'EAN_13'
    if (/^\d{8}$/.test(content)) return 'EAN_8'
    if (/^\d{12}$/.test(content)) return 'UPC_A'
    if (content.length < 20) return 'CODE_128'
    return 'QR_CODE'
  }

  return {
    // 状态
    isScanning,
    scanResult,
    permissionGranted,

    // 扫描
    startScan,
    stopScan,
    checkPermissions,
    requestPermissions,

    // 生成
    generateQRCode,
    generateBarcode,
    downloadQRCode,
    scanImage,

    // 工具
    validateBarcode,
    formatBarcode,
    detectBarcodeType,
  }
}

/**
 * 业务条码扫描 Hook
 */
export function useBusinessBarcode() {
  const scanner = useCapacitorBarcodeScanner()

  // 扫描产品条码
  const scanProduct = async (): Promise<string | null> => {
    const result = await scanner.startScan()
    return result?.content || null
  }

  // 生成产品二维码
  const generateProductQR = async (
    productId: string,
    baseUrl: string
  ): Promise<string | null> => {
    const url = `${baseUrl}/products/${productId}`
    return await scanner.generateQRCode(url, { width: 200, height: 200 })
  }

  // 扫描工单条码
  const scanWorkOrder = async (): Promise<string | null> => {
    const result = await scanner.startScan()
    return result?.content || null
  }

  // 扫描仓库条码
  const scanWarehouse = async (): Promise<string | null> => {
    const result = await scanner.startScan()
    return result?.content || null
  }

  // 生成库存二维码
  const generateStockQR = async (
    stockId: string,
    baseUrl: string
  ): Promise<string | null> => {
    const url = `${baseUrl}/stock/${stockId}`
    return await scanner.generateQRCode(url, { width: 200, height: 200 })
  }

  // 扫描客户二维码
  const scanCustomer = async (): Promise<string | null> => {
    const result = await scanner.startScan()
    return result?.content || null
  }

  // 生成客户二维码
  const generateCustomerQR = async (
    customerId: string,
    baseUrl: string
  ): Promise<string | null> => {
    const url = `${baseUrl}/customers/${customerId}`
    return await scanner.generateQRCode(url, { width: 200, height: 200 })
  }

  return {
    // 扫描
    scanProduct,
    scanWorkOrder,
    scanWarehouse,
    scanCustomer,

    // 生成
    generateProductQR,
    generateStockQR,
    generateCustomerQR,

    // 扫描器
    scanner,
  }
}
