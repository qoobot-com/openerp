package com.qoobot.common.exception;

import com.qoobot.common.enums.ResponseCode;
import com.qoobot.common.response.Result;

/**
 * 异常工具类
 */
public class ExceptionHelper {

    /**
     * 抛出业务异常
     */
    public static void throwBusinessException(String message) {
        throw new BizException(message);
    }

    /**
     * 抛出业务异常
     */
    public static void throwBusinessException(ResponseCode responseCode) {
        throw new BizException(responseCode);
    }

    /**
     * 抛出业务异常（自定义消息）
     */
    public static void throwBusinessException(ResponseCode responseCode, String customMessage) {
        throw new BizException(responseCode, customMessage);
    }

    /**
     * 条件抛出业务异常
     */
    public static void throwBusinessExceptionIf(boolean condition, String message) {
        if (condition) {
            throw new BizException(message);
        }
    }

    /**
     * 条件抛出业务异常
     */
    public static void throwBusinessExceptionIf(boolean condition, ResponseCode responseCode) {
        if (condition) {
            throw new BizException(responseCode);
        }
    }

    /**
     * 抛出参数校验异常
     */
    public static void throwValidationException(String message) {
        throw new ValidationException(message);
    }

    /**
     * 抛出参数校验异常
     */
    public static void throwValidationException(String fieldName, String message) {
        throw new ValidationException(fieldName, message);
    }

    /**
     * 条件抛出参数校验异常
     */
    public static void throwValidationExceptionIf(boolean condition, String message) {
        if (condition) {
            throw new ValidationException(message);
        }
    }

    /**
     * 抛出权限异常
     */
    public static void throwPermissionException(String message) {
        throw new PermissionException(message);
    }

    /**
     * 抛出权限异常
     */
    public static void throwPermissionException(Integer code, String message) {
        throw new PermissionException(code, message);
    }

    /**
     * 条件抛出权限异常
     */
    public static void throwPermissionExceptionIf(boolean condition, String message) {
        if (condition) {
            throw new PermissionException(message);
        }
    }

    /**
     * 抛出数据不存在异常
     */
    public static void throwDataNotFoundException(String message) {
        throw new DataNotFoundException(message);
    }

    /**
     * 抛出数据不存在异常
     */
    public static void throwDataNotFoundException(Class<?> entityClass, Object id) {
        throw new DataNotFoundException(entityClass, id);
    }

    /**
     * 条件抛出数据不存在异常
     */
    public static void throwDataNotFoundExceptionIfNull(Object data, Class<?> entityClass, Object id) {
        if (data == null) {
            throw new DataNotFoundException(entityClass, id);
        }
    }

    /**
     * 断言非空
     */
    public static <T> T assertNotNull(T object, String message) {
        if (object == null) {
            throw new ValidationException(message);
        }
        return object;
    }

    /**
     * 断言条件为真
     */
    public static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new BizException(message);
        }
    }

    /**
     * 断言条件为假
     */
    public static void assertFalse(boolean condition, String message) {
        if (condition) {
            throw new BizException(message);
        }
    }

    /**
     * 异常转Result
     */
    public static Result<Void> toResult(BizException e) {
        return e.toResult();
    }

    /**
     * 异常转Result
     */
    public static Result<Void> toResult(RuntimeException e) {
        if (e instanceof BizException) {
            return ((BizException) e).toResult();
        }
        return Result.error(e.getMessage());
    }
}
