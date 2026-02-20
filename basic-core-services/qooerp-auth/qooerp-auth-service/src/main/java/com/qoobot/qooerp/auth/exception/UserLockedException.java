package com.qoobot.qooerp.auth.exception;

/**
 * 用户锁定异常
 */
public class UserLockedException extends AuthException {

    public UserLockedException(String message) {
        super("AUTH_003", message);
    }
}
