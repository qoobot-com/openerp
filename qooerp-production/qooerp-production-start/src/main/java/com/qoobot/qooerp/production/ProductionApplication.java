package com.qoobot.qooerp.production;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 生产管理服务启动类
 */
@EnableDiscoveryClient
@EnableTransactionManagement
@SpringBootApplication(scanBasePackages = "com.qoobot.qooerp")
public class ProductionApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductionApplication.class, args);
        System.out.println("=================================");
        System.out.println("QooERP生产管理服务启动成功！");
        System.out.println("访问地址: http://localhost:8013/production");
        System.out.println("=================================");
    }
}
