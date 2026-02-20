package com.qoobot.qooerp.report;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * QooERP 报表与分析模块启动类
 *
 * @author Auto
 * @since 2026-02-18
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableAsync
@ComponentScan(basePackages = {"com.qoobot.qooerp.report", "com.qoobot.qooerp.common"})
@MapperScan("com.qoobot.qooerp.report.mapper")
public class ReportApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReportApplication.class, args);
        System.out.println("========================================");
        System.out.println("QooERP 报表与分析模块启动成功！");
        System.out.println("访问地址: http://localhost:8096");
        System.out.println("========================================");
    }
}
