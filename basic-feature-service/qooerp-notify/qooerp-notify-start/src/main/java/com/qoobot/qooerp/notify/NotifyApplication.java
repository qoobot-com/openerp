package com.qoobot.qooerp.notify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * QooERP 通知服务启动类
 *
 * @author QooBot
 * @since 2026-02-18
 */
@SpringBootApplication(scanBasePackages = {"com.qoobot.qooerp.common", "com.qoobot.qooerp.notify"})
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.qoobot.qooerp"})
public class NotifyApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotifyApplication.class, args);
        System.out.println("""

                ========================================
                  QooERP 通知服务启动成功！
                  http://localhost:18085/doc.html
                ========================================
                """);
    }
}
