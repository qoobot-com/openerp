package com.qoobot.qooerp.scm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 供应链管理服务启动类
 *
 * @author QooERP
 * @since 2026-02-17
 */
@SpringBootApplication(scanBasePackages = {"com.qoobot.qooerp"})
@EnableFeignClients(basePackages = {"com.qoobot.qooerp"})
public class ScmApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScmApplication.class, args);
    }
}
