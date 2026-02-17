package com.qoobot.qooerp.system.handler;

import com.qoobot.qooerp.common.enums.ResponseCode;
import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.system.exception.FileUploadException;
import com.qoobot.qooerp.system.exception.JobExecutionException;
import com.qoobot.qooerp.system.exception.SystemException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 *
 * @author QooERP Team
 * @version 1.0.0
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理系统模块业务异常
     */
    @ExceptionHandler(SystemException.class)
    public Result<Void> handleSystemException(SystemException e) {
        log.error("系统模块业务异常: {}", e.getErrorMessage(), e);
        return Result.error(Integer.valueOf(e.getErrorCode()), e.getErrorMessage());
    }

    /**
     * 处理文件上传异常
     */
    @ExceptionHandler(FileUploadException.class)
    public Result<Void> handleFileUploadException(FileUploadException e) {
        log.error("文件上传异常: {}", e.getErrorMessage(), e);
        return Result.error(Integer.valueOf(e.getErrorCode()), e.getErrorMessage());
    }

    /**
     * 处理定时任务异常
     */
    @ExceptionHandler(JobExecutionException.class)
    public Result<Void> handleJobExecutionException(JobExecutionException e) {
        log.error("定时任务执行异常: {}", e.getErrorMessage(), e);
        return Result.error(Integer.valueOf(e.getErrorCode()), e.getErrorMessage());
    }

    /**
     * 处理参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidationException(MethodArgumentNotValidException e) {
        String errorMsg = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.error("参数校验异常: {}", errorMsg);
        return Result.error(ResponseCode.PARAM_ERROR.getCode(), errorMsg);
    }

    /**
     * 处理绑定异常
     */
    @ExceptionHandler(BindException.class)
    public Result<Void> handleBindException(BindException e) {
        String errorMsg = e.getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.error("参数绑定异常: {}", errorMsg);
        return Result.error(ResponseCode.PARAM_ERROR.getCode(), errorMsg);
    }

    /**
     * 处理其他未知异常
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常: ", e);
        return Result.error(ResponseCode.SYSTEM_ERROR);
    }
}
