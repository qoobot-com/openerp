package com.qoobot.qooerp.organization;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * QooERP组织管理服务启动类
 */
@SpringBootApplication(scanBasePackages = {"com.qoobot.qooerp.organization", "com.qoobot.qooerp.common"})
@EnableDiscoveryClient
@EnableCaching
@MapperScan("com.qoobot.qooerp.organization.mapper")
public class OrganizationApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrganizationApplication.class, args);
        System.out.println("""

                =====================================================
                   QooERP Organization Service started successfully!
                   访问地址: http://localhost:8085
                   Swagger文档: http://localhost:8085/swagger-ui.html
                =====================================================
                """);
    }
}
