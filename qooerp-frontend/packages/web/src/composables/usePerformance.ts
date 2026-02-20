import { onMounted, onUnmounted } from 'vue';

/**
 * 性能指标类型
 */
export interface PerformanceMetrics {
  // 首次内容绘制
  fcp: number;
  // 最大内容绘制
  lcp: number;
  // 首次输入延迟
  fid: number;
  // 累积布局偏移
  cls: number;
  // 首次字节时间
  ttfb: number;
  // DOM 内容加载完成
  domContentLoaded: number;
  // 完全加载
  load: number;
}

/**
 * 使用性能监控 Hook
 */
export function usePerformance() {
  let performanceObserver: PerformanceObserver | null = null;
  const metrics = {
    fcp: 0,
    lcp: 0,
    fid: 0,
    cls: 0,
    ttfb: 0,
    domContentLoaded: 0,
    load: 0,
  };

  /**
   * 获取 TTFB (Time to First Byte)
   */
  const getTTFB = () => {
    const navigation = performance.getEntriesByType('navigation')[0] as PerformanceNavigationTiming;
    if (navigation) {
      metrics.ttfb = navigation.responseStart - navigation.requestStart;
    }
  };

  /**
   * 获取 FCP (First Contentful Paint)
   */
  const getFCP = () => {
    const paintEntries = performance.getEntriesByType('paint');
    const fcpEntry = paintEntries.find((entry) => entry.name === 'first-contentful-paint');
    if (fcpEntry) {
      metrics.fcp = fcpEntry.startTime;
    }
  };

  /**
   * 获取 LCP (Largest Contentful Paint)
   */
  const getLCP = () => {
    if ('PerformanceObserver' in window) {
      performanceObserver = new PerformanceObserver((list) => {
        const entries = list.getEntries();
        const lastEntry = entries[entries.length - 1] as any;
        metrics.lcp = lastEntry.startTime;
      });
      performanceObserver.observe({ type: 'largest-contentful-paint', buffered: true });
    }
  };

  /**
   * 获取 FID (First Input Delay)
   */
  const getFID = () => {
    if ('PerformanceObserver' in window) {
      performanceObserver = new PerformanceObserver((list) => {
        const entries = list.getEntries();
        const firstEntry = entries[0] as any;
        metrics.fid = firstEntry.processingStart - firstEntry.startTime;
      });
      performanceObserver.observe({ type: 'first-input', buffered: true });
    }
  };

  /**
   * 获取 CLS (Cumulative Layout Shift)
   */
  const getCLS = () => {
    if ('PerformanceObserver' in window) {
      let clsValue = 0;
      performanceObserver = new PerformanceObserver((list) => {
        for (const entry of list.getEntries()) {
          if (!(entry as any).hadRecentInput) {
            clsValue += (entry as any).value;
          }
        }
        metrics.cls = clsValue;
      });
      performanceObserver.observe({ type: 'layout-shift', buffered: true });
    }
  };

  /**
   * 获取 DOM 加载时间
   */
  const getDomLoadTime = () => {
    const navigation = performance.getEntriesByType('navigation')[0] as PerformanceNavigationTiming;
    if (navigation) {
      metrics.domContentLoaded = navigation.domContentLoadedEventEnd - navigation.navigationStart;
      metrics.load = navigation.loadEventEnd - navigation.navigationStart;
    }
  };

  /**
   * 获取所有性能指标
   */
  const getAllMetrics = (): PerformanceMetrics => {
    getTTFB();
    getFCP();
    getDomLoadTime();
    return { ...metrics };
  };

  /**
   * 上报性能数据
   */
  const reportMetrics = (data: PerformanceMetrics) => {
    // 这里可以上报到监控系统
    console.log('Performance Metrics:', data);
    // 示例: 上报到服务器
    // axios.post('/api/performance/report', data);
  };

  /**
   * 初始化性能监控
   */
  const initPerformanceMonitor = () => {
    getLCP();
    getFID();
    getCLS();

    // 页面加载完成后上报
    window.addEventListener('load', () => {
      setTimeout(() => {
        const data = getAllMetrics();
        reportMetrics(data);
      }, 0);
    });
  };

  /**
   * 清理性能监控
   */
  const cleanupPerformanceMonitor = () => {
    if (performanceObserver) {
      performanceObserver.disconnect();
      performanceObserver = null;
    }
  };

  return {
    metrics,
    getAllMetrics,
    reportMetrics,
    initPerformanceMonitor,
    cleanupPerformanceMonitor,
  };
}

/**
 * 使用内存监控 Hook
 */
export function useMemoryMonitor() {
  let timer: NodeJS.Timeout | null = null;

  /**
   * 获取内存信息
   */
  const getMemoryInfo = () => {
    if ('memory' in performance) {
      const memory = (performance as any).memory;
      return {
        usedJSHeapSize: memory.usedJSHeapSize,
        totalJSHeapSize: memory.totalJSHeapSize,
        jsHeapSizeLimit: memory.jsHeapSizeLimit,
        usage: memory.usedJSHeapSize / memory.jsHeapSizeLimit,
      };
    }
    return null;
  };

  /**
   * 开始监控内存
   */
  const startMonitoring = (interval = 5000) => {
    timer = setInterval(() => {
      const memoryInfo = getMemoryInfo();
      if (memoryInfo) {
        // 内存使用超过 80% 时警告
        if (memoryInfo.usage > 0.8) {
          console.warn('High memory usage:', memoryInfo);
        }
      }
    }, interval);
  };

  /**
   * 停止监控
   */
  const stopMonitoring = () => {
    if (timer) {
      clearInterval(timer);
      timer = null;
    }
  };

  return {
    getMemoryInfo,
    startMonitoring,
    stopMonitoring,
  };
}

/**
 * 使用资源监控 Hook
 */
export function useResourceMonitor() {
  /**
   * 获取资源加载时间
   */
  const getResourceTiming = () => {
    const resources = performance.getEntriesByType('resource') as PerformanceResourceTiming[];
    return resources.map((resource) => ({
      name: resource.name,
      duration: resource.duration,
      size: resource.transferSize,
      type: resource.initiatorType,
    }));
  };

  /**
   * 获取慢资源
   */
  const getSlowResources = (threshold = 1000) => {
    const resources = getResourceTiming();
    return resources.filter((r) => r.duration > threshold);
  };

  /**
   * 获取大资源
   */
  const getLargeResources = (threshold = 1024 * 1024) => {
    // 默认 1MB
    const resources = getResourceTiming();
    return resources.filter((r) => r.size > threshold);
  };

  return {
    getResourceTiming,
    getSlowResources,
    getLargeResources,
  };
}

/**
 * 使用 FPS 监控 Hook
 */
export function useFPSMonitor() {
  let frameCount = 0;
  let lastTime = performance.now();
  let timer: NodeJS.Timeout | null = null;
  const fpsHistory: number[] = [];

  /**
   * 计算 FPS
   */
  const calculateFPS = () => {
    frameCount++;
    const currentTime = performance.now();
    const delta = currentTime - lastTime;

    if (delta >= 1000) {
      const fps = Math.round((frameCount * 1000) / delta);
      fpsHistory.push(fps);
      if (fpsHistory.length > 60) {
        fpsHistory.shift();
      }

      // FPS 低于 24 时警告
      if (fps < 24) {
        console.warn('Low FPS:', fps);
      }

      frameCount = 0;
      lastTime = currentTime;
    }

    requestAnimationFrame(calculateFPS);
  };

  /**
   * 开始 FPS 监控
   */
  const startMonitoring = () => {
    requestAnimationFrame(calculateFPS);
  };

  /**
   * 获取平均 FPS
   */
  const getAverageFPS = () => {
    if (fpsHistory.length === 0) return 0;
    const sum = fpsHistory.reduce((a, b) => a + b, 0);
    return Math.round(sum / fpsHistory.length);
  };

  /**
   * 获取 FPS 历史
   */
  const getFPSHistory = () => {
    return [...fpsHistory];
  };

  return {
    startMonitoring,
    getAverageFPS,
    getFPSHistory,
  };
}
