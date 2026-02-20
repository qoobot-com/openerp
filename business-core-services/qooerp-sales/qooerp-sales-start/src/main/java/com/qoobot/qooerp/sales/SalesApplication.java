package com.qoobot.qooerp.sales;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * QooERP 销售管理 - 启动类
 */
@SpringBootApplication(scanBasePackages = {"com.qoobot.qooerp.sales", "com.qoobot.qooerp.common"})
@EnableDiscoveryClient
public class SalesApplication {

    public static void main(String[] args) {
        SpringApplication.run(SalesApplication.class, args);
    }
}
