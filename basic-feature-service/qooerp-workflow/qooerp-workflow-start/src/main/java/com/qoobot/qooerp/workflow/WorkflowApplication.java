package com.qoobot.qooerp.workflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 工作流服务启动类
 */
@EnableDiscoveryClient
@EnableTransactionManagement
@SpringBootApplication(scanBasePackages = "com.qoobot.qooerp")
public class WorkflowApplication {

    public static void main(String[] args) {
        SpringApplication.run(WorkflowApplication.class, args);
        System.out.println("=================================");
        System.out.println("QooERP工作流服务启动成功！");
        System.out.println("访问地址: http://localhost:8086/workflow");
        System.out.println("=================================");
    }
}
