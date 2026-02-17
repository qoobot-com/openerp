package com.qoobot.qooerp.system.aspect;

import com.alibaba.fastjson2.JSON;
import com.qoobot.qooerp.common.util.IpUtils;
import com.qoobot.qooerp.system.entity.SystemLog;
import com.qoobot.qooerp.system.service.SystemLogService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

/**
 * 操作日志切面
 *
 * @author QooERP Team
 * @version 1.0.0
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LogAspect {

    private final SystemLogService logService;

    /**
     * 定义切点：拦截所有Controller方法
     */
    @Pointcut("execution(* com.qoobot.qooerp.system.controller..*.*(..))")
    public void logPointcut() {
    }

    /**
     * 环绕通知：记录操作日志
     */
    @Around("logPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        SystemLog systemLog = new SystemLog();
        Object result = null;
        Exception exception = null;

        try {
            // 获取请求信息
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();

                // 获取方法签名
                MethodSignature signature = (MethodSignature) joinPoint.getSignature();
                Operation operation = signature.getMethod().getAnnotation(Operation.class);

                // 设置日志基本信息
                if (operation != null) {
                    systemLog.setModule("系统管理");
                    systemLog.setOperation(operation.description() != null ? operation.description() : operation.summary());
                }
                systemLog.setMethod(signature.getDeclaringTypeName() + "." + signature.getName());
                systemLog.setParams(JSON.toJSONString(joinPoint.getArgs()));
                systemLog.setIp(IpUtils.getIpAddress(request));
                systemLog.setOperateTime(LocalDateTime.now());
            }

            // 执行方法
            result = joinPoint.proceed();

            // 设置执行状态为成功
            systemLog.setStatus(1);

            return result;
        } catch (Exception e) {
            exception = e;
            systemLog.setStatus(0);
            systemLog.setErrorMsg(e.getMessage());
            throw e;
        } finally {
            // 计算执行时间
            long costTime = System.currentTimeMillis() - startTime;
            systemLog.setCostTime((int) costTime);

            // 异步保存日志
            saveLogAsync(systemLog);
        }
    }

    /**
     * 异步保存日志
     */
    @Async
    public void saveLogAsync(SystemLog systemLog) {
        CompletableFuture.runAsync(() -> {
            try {
                logService.createLog(systemLog);
            } catch (Exception e) {
                log.error("保存操作日志失败", e);
            }
        });
    }
}
