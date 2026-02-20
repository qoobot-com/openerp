package com.qoobot.qooerp.lowcode;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 低代码开发平台启动类
 */
@EnableDiscoveryClient
@EnableTransactionManagement
@SpringBootApplication(scanBasePackages = "com.qoobot.qooerp")
public class LowcodeApplication {

    public static void main(String[] args) {
        SpringApplication.run(LowcodeApplication.class, args);
        System.out.println("=================================");
        System.out.println("QooERP低代码开发平台启动成功！");
        System.out.println("访问地址: http://localhost:8089/lowcode");
        System.out.println("API文档: http://localhost:8089/lowcode/swagger-ui.html");
        System.out.println("=================================");
    }
}
