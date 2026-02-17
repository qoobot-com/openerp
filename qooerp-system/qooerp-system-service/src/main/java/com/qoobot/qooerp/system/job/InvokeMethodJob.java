package com.qoobot.qooerp.system.job;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 动态任务执行器
 *
 * @author QooERP Team
 * @version 1.0.0
 */
@Slf4j
@Component
public class InvokeMethodJob implements Job {

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        String jobName = dataMap.getString("jobName");
        String jobClass = dataMap.getString("jobClass");

        log.info("开始执行定时任务: {}", jobName);

        long startTime = System.currentTimeMillis();

        try {
            // 获取Bean
            Object bean = applicationContext.getBean(jobClass);

            // 执行execute方法
            Method method = bean.getClass().getDeclaredMethod("execute");
            method.setAccessible(true);
            method.invoke(bean);

            long costTime = System.currentTimeMillis() - startTime;
            log.info("定时任务执行成功: {}, 耗时: {}ms", jobName, costTime);

        } catch (Exception e) {
            long costTime = System.currentTimeMillis() - startTime;
            log.error("定时任务执行失败: {}, 耗时: {}ms", jobName, costTime, e);
            throw new JobExecutionException(e);
        }
    }
}
