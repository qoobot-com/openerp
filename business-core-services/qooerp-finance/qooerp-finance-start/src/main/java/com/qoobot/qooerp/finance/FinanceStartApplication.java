package com.qoobot.qooerp.finance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 财务管理服务启动类
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
@SpringBootApplication(scanBasePackages = {"com.qoobot.qooerp"})
@EnableDiscoveryClient
public class FinanceStartApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinanceStartApplication.class, args);
    }
}
