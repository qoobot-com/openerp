package com.qoobot.qooerp.production.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI 配置类
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI productionOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("QooERP 生产管理服务 API")
                        .description("生产管理服务接口文档")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Qoobot Team")
                                .email("dev@qoobot.com")
                                .url("https://www.qoobot.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
}
