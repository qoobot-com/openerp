package com.qoobot.qooerp.common.config;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Tracer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenTelemetry 配置
 * 链路追踪核心配置
 */
@Configuration
public class OpenTelemetryConfig {

    /**
     * 获取链路追踪 Tracer
     * 用于手动创建 span
     */
    @Bean
    public Tracer otelTracer(OpenTelemetry openTelemetry) {
        return openTelemetry.getTracerProvider()
                .tracerBuilder("qooerp-tracer")
                .setInstrumentationVersion("1.0.0")
                .build();
    }
}
