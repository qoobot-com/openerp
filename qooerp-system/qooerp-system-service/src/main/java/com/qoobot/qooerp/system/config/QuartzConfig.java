package com.qoobot.qooerp.system.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 * Quartz 定时任务配置
 *
 * @author QooERP Team
 * @version 1.0.0
 */
@Slf4j
@Configuration
@EnableScheduling
public class QuartzConfig {

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setAutoStartup(true);
        factory.setStartupDelay(10);
        return factory;
    }
}
