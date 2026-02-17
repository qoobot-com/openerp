package com.qoobot.qooerp.common.aspect;

import com.qoobot.qooerp.common.annotation.OperationLog;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

/**
 * 操作日志切面
 */
@Slf4j
@Aspect
@Component
public class OperationLogAspect {

    @Around("@annotation(operationLog)")
    public Object around(ProceedingJoinPoint point, OperationLog operationLog) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = null;
        Throwable exception = null;

        try {
            // 执行目标方法
            result = point.proceed();
            return result;
        } catch (Throwable e) {
            exception = e;
            throw e;
        } finally {
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;

            // 记录操作日志
            logOperation(point, operationLog, result, exception, executionTime);
        }
    }

    /**
     * 记录操作日志
     */
    private void logOperation(ProceedingJoinPoint point, OperationLog operationLog,
                              Object result, Throwable exception, long executionTime) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        String className = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getName();

        String module = operationLog.module();
        String operation = operationLog.operation();
        String description = operationLog.description();
        String type = operationLog.type().name();

        log.info("【操作日志】模块: {}, 操作: {}, 描述: {}, 类型: {}, 类: {}, 方法: {}, " +
                        "耗时: {}ms, 状态: {}",
                module, operation, description, type, className, methodName,
                executionTime, exception == null ? "成功" : "失败");

        if (exception != null) {
            log.error("【操作日志】操作失败: {}", exception.getMessage());
        }

        // TODO: 异步保存操作日志到数据库
    }
}
