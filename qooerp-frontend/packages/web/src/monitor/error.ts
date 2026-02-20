/**
 * 错误监控模块
 * 用于捕获和上报 JavaScript 错误
 */

import { getTracer, addEvent, recordException } from '@/otel';

/**
 * 错误信息类型
 */
export interface ErrorInfo {
  // 错误类型
  type: 'error' | 'unhandledrejection' | 'resource' | 'custom';
  // 错误消息
  message: string;
  // 错误堆栈
  stack?: string;
  // 错误发生时间
  timestamp: number;
  // 用户信息
  userId?: string;
  // 页面 URL
  url: string;
  // 用户代理
  userAgent: string;
  // 额外数据
  extra?: Record<string, any>;
}

/**
 * 错误收集器
 */
class ErrorCollector {
  private errors: ErrorInfo[] = [];
  private maxErrors = 50;
  private reportInterval = 5000;
  private reportTimer: NodeJS.Timeout | null = null;
  private tracer = getTracer('error-monitor', '1.0.0');

  /**
   * 初始化
   */
  init() {
    this.setupErrorHandler();
    this.setupUnhandledRejection();
    this.setupResourceError();
    this.startReportTimer();
  }

  /**
   * 设置全局错误处理
   */
  private setupErrorHandler() {
    window.addEventListener('error', (event) => {
      const errorInfo: ErrorInfo = {
        type: 'error',
        message: event.message,
        stack: event.error?.stack,
        timestamp: Date.now(),
        url: window.location.href,
        userAgent: navigator.userAgent,
      };
      this.collect(errorInfo);
    });
  }

  /**
   * 设置未处理的 Promise 拒绝
   */
  private setupUnhandledRejection() {
    window.addEventListener('unhandledrejection', (event) => {
      const errorInfo: ErrorInfo = {
        type: 'unhandledrejection',
        message: event.reason?.message || String(event.reason),
        stack: event.reason?.stack,
        timestamp: Date.now(),
        url: window.location.href,
        userAgent: navigator.userAgent,
      };
      this.collect(errorInfo);
    });
  }

  /**
   * 设置资源加载错误
   */
  private setupResourceError() {
    window.addEventListener('error', (event) => {
      if (event.target !== window) {
        const target = event.target as HTMLElement;
        const errorInfo: ErrorInfo = {
          type: 'resource',
          message: `Resource loading failed: ${target.tagName}`,
          timestamp: Date.now(),
          url: window.location.href,
          userAgent: navigator.userAgent,
          extra: {
            tagName: target.tagName,
            src: (target as any).src,
            href: (target as any).href,
          },
        };
        this.collect(errorInfo);
      }
    }, true);
  }

  /**
   * 收集错误
   */
  collect(errorInfo: ErrorInfo) {
    // 创建 Span 记录错误
    const span = this.tracer.startSpan('error.collect');
    addEvent(span, 'error.collected', {
      type: errorInfo.type,
      message: errorInfo.message,
    });
    recordException(span, new Error(errorInfo.message));
    span.end();

    // 添加到错误列表
    this.errors.push(errorInfo);

    // 限制错误数量
    if (this.errors.length > this.maxErrors) {
      this.errors.shift();
    }

    // 打印错误到控制台
    console.error('[ErrorCollector]', errorInfo);
  }

  /**
   * 自定义错误上报
   */
  reportCustomError(message: string, extra?: Record<string, any>) {
    const errorInfo: ErrorInfo = {
      type: 'custom',
      message,
      timestamp: Date.now(),
      url: window.location.href,
      userAgent: navigator.userAgent,
      extra,
    };
    this.collect(errorInfo);
  }

  /**
   * 获取所有错误
   */
  getErrors(): ErrorInfo[] {
    return [...this.errors];
  }

  /**
   * 清空错误
   */
  clearErrors() {
    this.errors = [];
  }

  /**
   * 上报错误到服务器
   */
  private async reportErrors() {
    if (this.errors.length === 0) return;

    try {
      const span = this.tracer.startSpan('error.report');
      // 这里可以发送错误到服务器
      // await axios.post('/api/error/report', { errors: this.errors });
      addEvent(span, 'error.reported', { count: this.errors.length });
      span.end();

      console.log('[ErrorCollector] Reported', this.errors.length, 'errors');
      this.clearErrors();
    } catch (error) {
      console.error('[ErrorCollector] Failed to report errors:', error);
    }
  }

  /**
   * 启动定时上报
   */
  private startReportTimer() {
    this.reportTimer = setInterval(() => {
      this.reportErrors();
    }, this.reportInterval);
  }

  /**
   * 停止定时上报
   */
  stop() {
    if (this.reportTimer) {
      clearInterval(this.reportTimer);
      this.reportTimer = null;
    }
  }
}

// 创建全局错误收集器实例
const errorCollector = new ErrorCollector();

/**
 * 初始化错误监控
 */
export const initErrorMonitor = () => {
  errorCollector.init();
};

/**
 * 上报自定义错误
 */
export const reportError = (message: string, extra?: Record<string, any>) => {
  errorCollector.reportCustomError(message, extra);
};

/**
 * 获取错误列表
 */
export const getErrors = () => {
  return errorCollector.getErrors();
};

/**
 * 清空错误
 */
export const clearErrors = () => {
  errorCollector.clearErrors();
};

/**
 * 停止错误监控
 */
export const stopErrorMonitor = () => {
  errorCollector.stop();
};

export default errorCollector;
