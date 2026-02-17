package com.qoobot.qooerp.permission.exception;

/**
 * 角色已存在异常
 */
public class RoleAlreadyExistsException extends PermissionException {

    public RoleAlreadyExistsException(String roleCode) {
        super("角色编码已存在: " + roleCode);
    }
}
