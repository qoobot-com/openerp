package com.qoobot.qooerp.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 认证服务启动类
 */
@EnableDiscoveryClient
@EnableTransactionManagement
@SpringBootApplication(scanBasePackages = "com.qoobot.qooerp")
public class AuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
        System.out.println("=================================");
        System.out.println("QooERP认证服务启动成功！");
        System.out.println("访问地址: http://localhost:8081/auth");
        System.out.println("=================================");
    }
}
