package com.qoobot.qooerp.scm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 供应链管理服务启动类
 *
 * @author QooERP
 * @since 2026-02-17
 */
@SpringBootApplication(scanBasePackages = {"com.qoobot.qooerp"})
public class ScmStartApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScmStartApplication.class, args);
    }
}
