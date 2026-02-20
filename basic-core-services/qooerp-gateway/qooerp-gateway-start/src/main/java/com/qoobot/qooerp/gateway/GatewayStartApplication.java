package com.qoobot.qooerp.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

/**
 * QooERP Gateway 启动类
 *
 * @author QooERP Team
 * @since 20xx-xx-xx
 */
@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
public class GatewayStartApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayStartApplication.class, args);
    }
}
