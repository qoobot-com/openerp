package com.qoobot.qooerp.system.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger/OpenAPI 配置
 *
 * @author QooERP Team
 * @version 1.0.0
 */
@Configuration
public class SwaggerConfig {

    /**
     * OpenAPI 配置
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("QooERP System API")
                        .version("1.0.0")
                        .description("QooERP 系统管理模块 API 文档")
                        .contact(new Contact()
                                .name("QooERP Team")
                                .email("support@qooerp.com")
                                .url("https://www.qooerp.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }

    /**
     * System 模块 API 分组
     */
    @Bean
    public GroupedOpenApi systemApi() {
        return GroupedOpenApi.builder()
                .group("system")
                .pathsToMatch("/api/system/**")
                .build();
    }
}
