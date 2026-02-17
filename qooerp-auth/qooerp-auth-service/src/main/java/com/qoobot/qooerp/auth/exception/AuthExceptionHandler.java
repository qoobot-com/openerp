package com.qoobot.qooerp.auth.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * 认证服务全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class AuthExceptionHandler {

    /**
     * 处理登录失败异常
     */
    @ExceptionHandler(LoginFailedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public void handleLoginFailedException(LoginFailedException e) {
        log.warn("登录失败: {}", e.getMessage());
        throw e;
    }

    /**
     * 处理用户锁定异常
     */
    @ExceptionHandler(UserLockedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public void handleUserLockedException(UserLockedException e) {
        log.warn("用户锁定: {}", e.getMessage());
        throw e;
    }

    /**
     * 处理认证异常
     */
    @ExceptionHandler(AuthException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public void handleAuthException(AuthException e) {
        log.warn("认证异常: {}", e.getMessage());
        throw e;
    }

    /**
     * 处理参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.warn("参数校验失败: {}", message);
        throw new IllegalArgumentException(message);
    }

    /**
     * 处理绑定异常
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleBindException(BindException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.warn("参数绑定失败: {}", message);
        throw new IllegalArgumentException(message);
    }

    /**
     * 处理非法参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("非法参数: {}", e.getMessage());
        throw e;
    }

    /**
     * 处理其他异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public void handleException(Exception e) {
        log.error("系统异常", e);
        throw new RuntimeException("系统异常，请稍后重试", e);
    }
}
