package com.qoobot.common.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * 日志切面
 * 记录方法执行的入参、出参、执行时间
 */
@Slf4j
@Aspect
@Component
public class LogAspect {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 定义切点：拦截所有Controller层方法
     */
    @Pointcut("execution(* com.qoobot..*.*(..)) && @within(org.springframework.web.bind.annotation.RestController)")
    public void logPointcut() {
    }

    /**
     * 环绕通知
     */
    @Around("logPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        try {
            // 记录入参
            log.info("[{}.{}] 方法开始执行", className, methodName);
            if (args != null && args.length > 0) {
                String argsJson = objectMapper.writeValueAsString(args);
                log.info("[{}.{}] 入参: {}", className, methodName, argsJson);
            }

            // 执行方法
            Object result = joinPoint.proceed();

            // 记录出参和执行时间
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;

            if (result != null) {
                String resultJson = objectMapper.writeValueAsString(result);
                log.info("[{}.{}] 出参: {}", className, methodName, resultJson);
            }
            log.info("[{}.{}] 方法执行完成，耗时: {}ms", className, methodName, executionTime);

            return result;
        } catch (Throwable e) {
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;
            log.error("[{}.{}] 方法执行异常，耗时: {}ms, 异常信息: {}", className, methodName, executionTime, e.getMessage(), e);
            throw e;
        }
    }
}
