package com.qoobot.qooerp.common.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

/**
 * Micrometer 配置
 * 自定义度量指标和标签
 */
@Configuration
public class MicrometerConfig {

    /**
     * 自定义通用标签
     * 所有指标都会自动带上这些标签
     */
    @Bean
    public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
        return registry -> registry.config()
                .commonTags(Collections.singletonList(Tag.of("application", "qooerp"))
                );
    }
}
