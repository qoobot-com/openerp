package com.qoobot.qooerp.permission.exception;

/**
 * 菜单已存在异常
 */
public class MenuAlreadyExistsException extends PermissionException {

    public MenuAlreadyExistsException(String menuName) {
        super("菜单已存在: " + menuName);
    }
}
