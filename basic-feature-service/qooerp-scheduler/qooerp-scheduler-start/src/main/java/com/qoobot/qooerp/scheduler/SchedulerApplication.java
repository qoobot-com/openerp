package com.qoobot.qooerp.scheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 任务调度模块启动类
 */
@SpringBootApplication(scanBasePackages = {"com.qoobot.qooerp.scheduler", "com.qoobot.qooerp.common"})
public class SchedulerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SchedulerApplication.class, args);
        System.out.println("========================================");
        System.out.println("QooERP 任务调度模块启动成功!");
        System.out.println("========================================");
    }
}
