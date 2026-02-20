package com.qoobot.qooerp.auth.exception;

/**
 * 登录失败异常
 */
public class LoginFailedException extends AuthException {

    public LoginFailedException(String message) {
        super("AUTH_002", message);
    }

    public LoginFailedException() {
        super("AUTH_002", "用户名或密码错误");
    }
}
