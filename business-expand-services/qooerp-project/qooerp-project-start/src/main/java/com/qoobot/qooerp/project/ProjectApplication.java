package com.qoobot.qooerp.project;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 项目管理服务启动类
 *
 * @author QooERP Team
 * @since 2026-02-18
 */
@SpringBootApplication(scanBasePackages = {"com.qoobot.qooerp"})
@EnableFeignClients(basePackages = {"com.qoobot.qooerp"})
@MapperScan("com.qoobot.qooerp.project.**.mapper")
public class ProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProjectApplication.class, args);
    }
}
