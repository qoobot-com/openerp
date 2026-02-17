package com.qoobot.qooerp.organization.service;

import com.qoobot.qooerp.organization.dto.UserDeptDTO;

import java.util.List;
import java.util.Map;

/**
 * 用户部门关联服务接口
 */
public interface UserDeptService {

    /**
     * 绑定用户部门
     */
    void bind(UserDeptDTO dto);

    /**
     * 解绑用户部门
     */
    void unbind(Long userId, Long deptId);

    /**
     * 获取用户部门列表
     */
    List<Map<String, Object>> getUserDepts(Long userId);

    /**
     * 获取用户主部门
     */
    Long getUserPrimaryDept(Long userId);
}
