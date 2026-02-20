package com.qoobot.qooerp.file;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 文件服务启动类
 *
 * @author QooERP
 * @date 20xx-xx-xx
 */
@SpringBootApplication(scanBasePackages = {"com.qoobot.qooerp.file", "com.qoobot.qooerp.common"})
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.qoobot.qooerp"})
public class FileApplication {

    public static void main(String[] args) {
        SpringApplication.run(FileApplication.class, args);
    }
}
