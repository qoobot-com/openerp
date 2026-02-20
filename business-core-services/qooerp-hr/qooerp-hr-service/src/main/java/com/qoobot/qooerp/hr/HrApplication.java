package com.qoobot.qooerp.hr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * QooERP HR 启动类
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(basePackages = {
    "com.qoobot.qooerp.hr",
    "com.qoobot.qooerp.common"
})
public class HrApplication {

    public static void main(String[] args) {
        SpringApplication.run(HrApplication.class, args);
    }
}
