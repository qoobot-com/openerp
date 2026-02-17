package com.qoobot.qooerp.system.exception;

/**
 * 定时任务执行异常
 *
 * @author QooERP Team
 * @version 1.0.0
 */
public class JobExecutionException extends SystemException {

    public JobExecutionException(String errorMessage) {
        super("JOB_EXECUTION_ERROR", errorMessage);
    }

    public JobExecutionException(String errorMessage, Throwable cause) {
        super("JOB_EXECUTION_ERROR", errorMessage, cause);
    }

    /**
     * 任务不存在异常
     */
    public static class JobNotFoundException extends JobExecutionException {
        public JobNotFoundException(Long jobId) {
            super(String.format("定时任务不存在，ID: %d", jobId));
        }
    }

    /**
     * 任务类不存在异常
     */
    public static class JobClassNotFoundException extends JobExecutionException {
        public JobClassNotFoundException(String className) {
            super(String.format("任务类不存在: %s", className));
        }
    }

    /**
     * Cron表达式无效异常
     */
    public static class InvalidCronExpressionException extends JobExecutionException {
        public InvalidCronExpressionException(String cronExpression) {
            super(String.format("无效的Cron表达式: %s", cronExpression));
        }
    }
}
