package com.qoobot.qooerp.system;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 系统管理服务启动类
 */
@SpringBootApplication(scanBasePackages = {"com.qoobot.qooerp.system", "com.qoobot.qooerp.common"})
@EnableDiscoveryClient
@EnableCaching
@EnableAsync
@EnableScheduling
@MapperScan("com.qoobot.qooerp.system.mapper")
public class SystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(SystemApplication.class, args);
    }
}
