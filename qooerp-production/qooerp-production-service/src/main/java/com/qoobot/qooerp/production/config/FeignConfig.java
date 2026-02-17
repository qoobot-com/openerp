package com.qoobot.qooerp.production.config;

import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenFeign 配置类
 */
@Configuration
public class FeignConfig {

    @Bean
    public Logger.Level feignLoggerLevel() {
        // 设置Feign日志级别为FULL，记录完整的请求和响应信息
        return Logger.Level.FULL;
    }
}
