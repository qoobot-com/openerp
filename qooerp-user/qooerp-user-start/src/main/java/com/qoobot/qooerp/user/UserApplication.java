package com.qoobot.qooerp.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * QooERP用户管理服务启动类
 */
@SpringBootApplication(scanBasePackages = {"com.qoobot.qooerp"})
@EnableDiscoveryClient
@EnableCaching
@MapperScan({"com.qoobot.qooerp.user.mapper"})
public class UserApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
        System.out.println("""
                ========================================
                   QooERP用户管理服务启动成功！
                   访问地址: http://localhost:8084
                   Swagger文档: http://localhost:8084/swagger-ui.html
                ========================================
                """);
    }
}
