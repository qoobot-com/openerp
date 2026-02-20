package com.qoobot.qooerp.permission.exception;

import com.qoobot.qooerp.common.exception.BusinessException;

/**
 * 权限异常
 */
public class PermissionException extends BusinessException {

    public PermissionException(String message) {
        super(message);
    }

    public PermissionException(String message, Throwable cause) {
        super(message, cause);
    }
}
