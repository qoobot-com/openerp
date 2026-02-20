/**
 * OpenTelemetry 监控集成
 * 用于链路追踪、性能监控、错误收集等
 */

import {
  trace,
  context,
  Span,
  SpanKind,
  SpanStatusCode,
} from '@opentelemetry/api';
import { Resource } from '@opentelemetry/resources';
import {
  SemanticResourceAttributes,
} from '@opentelemetry/semantic-conventions';
import {
  BatchSpanProcessor,
  SimpleSpanProcessor,
  ConsoleSpanExporter,
  WebTracerProvider,
} from '@opentelemetry/sdk-trace-web';
import { OTLPTraceExporter } from '@opentelemetry/exporter-trace-otlp-http';
import { registerInstrumentations } from '@opentelemetry/instrumentation';
import { UserInteractionInstrumentation } from '@opentelemetry/instrumentation-user-interaction';
import { DocumentLoadInstrumentation } from '@opentelemetry/instrumentation-document-load';
import { FetchInstrumentation } from '@opentelemetry/instrumentation-fetch';
import { XMLHttpRequestInstrumentation } from '@opentelemetry/instrumentation-xml-http-request';

/**
 * 监控配置
 */
interface OtelConfig {
  // 服务名称
  serviceName: string;
  // 服务版本
  serviceVersion: string;
  // 环境标识
  environment: 'development' | 'staging' | 'production';
  // OTLP 端点
  endpoint?: string;
  // 是否启用控制台输出
  enableConsole?: boolean;
  // 采样率 (0-1)
  sampleRate?: number;
}

/**
 * 默认配置
 */
const defaultConfig: Required<OtelConfig> = {
  serviceName: 'qooerp-web',
  serviceVersion: '1.0.0',
  environment: process.env.NODE_ENV as any || 'development',
  endpoint: process.env.VITE_OTEL_ENDPOINT || 'http://localhost:4318/v1/traces',
  enableConsole: process.env.NODE_ENV === 'development',
  sampleRate: 1.0,
};

/**
 * 创建资源
 */
const createResource = (config: Required<OtelConfig>) => {
  return new Resource({
    [SemanticResourceAttributes.SERVICE_NAME]: config.serviceName,
    [SemanticResourceAttributes.SERVICE_VERSION]: config.serviceVersion,
    [SemanticResourceAttributes.DEPLOYMENT_ENVIRONMENT]: config.environment,
  });
};

/**
 * 创建导出器
 */
const createExporters = (config: Required<OtelConfig>) => {
  const exporters = [];

  // 控制台导出器 (开发环境)
  if (config.enableConsole) {
    exporters.push(new ConsoleSpanExporter());
  }

  // OTLP 导出器 (生产环境)
  if (config.environment === 'production' && config.endpoint) {
    exporters.push(
      new OTLPTraceExporter({
        url: config.endpoint,
      })
    );
  }

  return exporters;
};

/**
 * 初始化 OpenTelemetry
 */
export const initOtel = (config: Partial<OtelConfig> = {}) => {
  const mergedConfig = { ...defaultConfig, ...config };

  // 创建资源
  const resource = createResource(mergedConfig);

  // 创建 Tracer Provider
  const provider = new WebTracerProvider({
    resource,
    // 设置采样率
    spanProcessors: [
      new BatchSpanProcessor(
        new OTLPTraceExporter({
          url: mergedConfig.endpoint,
        })
      ),
    ],
  });

  // 添加 Span 处理器
  const exporters = createExporters(mergedConfig);
  exporters.forEach((exporter) => {
    if (mergedConfig.enableConsole) {
      provider.addSpanProcessor(new SimpleSpanProcessor(exporter));
    } else {
      provider.addSpanProcessor(new BatchSpanProcessor(exporter));
    }
  });

  // 注册 Tracer
  provider.register();

  // 注册自动埋点
  registerInstrumentations({
    instrumentations: [
      new UserInteractionInstrumentation(),
      new DocumentLoadInstrumentation(),
      new FetchInstrumentation(),
      new XMLHttpRequestInstrumentation(),
    ],
  });

  console.log('[OpenTelemetry] Initialized with config:', mergedConfig);

  return provider;
};

/**
 * 获取 Tracer
 */
export const getTracer = (name = 'default', version = '1.0.0') => {
  return trace.getTracer(name, version);
};

/**
 * 创建 Span
 */
export const createSpan = (
  name: string,
  options?: {
    kind?: SpanKind;
    attributes?: Record<string, any>;
  }
) => {
  const tracer = getTracer();
  const span = tracer.startSpan(name, {
    kind: options?.kind,
    attributes: options?.attributes,
  });
  return span;
};

/**
 * 执行带追踪的函数
 */
export const withSpan = async <T>(
  name: string,
  fn: (span: Span) => T | Promise<T>,
  options?: {
    kind?: SpanKind;
    attributes?: Record<string, any>;
  }
): Promise<T> => {
  const span = createSpan(name, options);
  try {
    const result = await fn(span);
    span.setStatus({ code: SpanStatusCode.OK });
    return result;
  } catch (error) {
    span.recordException(error as Error);
    span.setStatus({
      code: SpanStatusCode.ERROR,
      message: (error as Error).message,
    });
    throw error;
  } finally {
    span.end();
  }
};

/**
 * 添加事件到 Span
 */
export const addEvent = (span: Span, name: string, attributes?: Record<string, any>) => {
  span.addEvent(name, attributes);
};

/**
 * 添加属性到 Span
 */
export const setAttribute = (span: Span, key: string, value: any) => {
  span.setAttribute(key, value);
};

/**
 * 记录异常
 */
export const recordException = (span: Span, error: Error) => {
  span.recordException(error);
  span.setStatus({
    code: SpanStatusCode.ERROR,
    message: error.message,
  });
};

/**
 * 获取当前 Span
 */
export const getCurrentSpan = () => {
  return trace.getSpan(context.active());
};

/**
 * 默认导出
 */
export default {
  initOtel,
  getTracer,
  createSpan,
  withSpan,
  addEvent,
  setAttribute,
  recordException,
  getCurrentSpan,
};
