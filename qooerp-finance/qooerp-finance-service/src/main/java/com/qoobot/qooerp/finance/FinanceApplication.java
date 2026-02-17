package com.qoobot.qooerp.finance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 财务管理服务启动类
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
@SpringBootApplication(scanBasePackages = {"com.qoobot.qooerp"})
@EnableFeignClients(basePackages = {"com.qoobot.qooerp"})
public class FinanceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinanceApplication.class, args);
    }
}
