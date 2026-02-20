package com.qoobot.qooerp.crm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * CRM服务启动类
 */
@SpringBootApplication(scanBasePackages = {"com.qoobot.qooerp.crm", "com.qoobot.qooerp.common"})
@EnableDiscoveryClient
public class CrmApplication {
    public static void main(String[] args) {
        SpringApplication.run(CrmApplication.class, args);
    }
}
