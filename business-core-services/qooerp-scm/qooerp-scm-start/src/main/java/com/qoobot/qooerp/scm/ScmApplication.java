package com.qoobot.qooerp.scm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 供应链管理应用启动类
 */
@SpringBootApplication(scanBasePackages = {"com.qoobot.qooerp.common", "com.qoobot.qooerp.scm"})
@EnableDiscoveryClient
public class ScmApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScmApplication.class, args);
    }
}
