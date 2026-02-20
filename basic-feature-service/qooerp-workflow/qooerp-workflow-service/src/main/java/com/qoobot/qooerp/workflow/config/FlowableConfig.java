package com.qoobot.qooerp.workflow.config;

import org.flowable.common.engine.impl.history.HistoryLevel;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RepositoryService;
import org.flowable.image.ProcessDiagramGenerator;
import org.flowable.image.impl.DefaultProcessDiagramGenerator;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.boot.EngineConfigurationConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Flowable 配置
 */
@Configuration
public class FlowableConfig {

    /**
     * Flowable 引擎配置
     */
    @Bean
    public EngineConfigurationConfigurer<SpringProcessEngineConfiguration> flowableEngineConfigurer() {
        return engineConfiguration -> {
            // 设置异步执行器激活
            engineConfiguration.setAsyncExecutorActivate(true);

            // 设置历史记录级别为 FULL
            HistoryLevel historyLevel = HistoryLevel.FULL;
            engineConfiguration.setHistoryLevel(historyLevel);

            // 启用异步历史
            engineConfiguration.setEnableHistoryCleaning(true);

            // 设置数据库 schema 更新策略
            engineConfiguration.setDatabaseSchemaUpdate(SpringProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);

            // 设置邮件服务器配置
            engineConfiguration.setMailServerHost("smtp.example.com");
            engineConfiguration.setMailServerPort(587);
            engineConfiguration.setMailServerUseSSL(false);
            engineConfiguration.setMailServerUseTLS(true);

            // 设置流程图生成配置
            engineConfiguration.setActivityFontName("宋体");
            engineConfiguration.setLabelFontName("宋体");
            engineConfiguration.setAnnotationFontName("宋体");
        };
    }

    /**
     * 流程图生成器 Bean
     */
    @Bean
    public ProcessDiagramGenerator processDiagramGenerator(ProcessEngine processEngine) {
        return new DefaultProcessDiagramGenerator();
    }
}
