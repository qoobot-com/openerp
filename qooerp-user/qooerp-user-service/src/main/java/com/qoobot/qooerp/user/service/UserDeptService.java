package com.qoobot.qooerp.user.service;

import java.util.List;

/**
 * 用户部门服务接口
 */
public interface UserDeptService {

    /**
     * 分配部门
     */
    boolean assignDepts(Long userId, List<Long> deptIds);

    /**
     * 设置主部门
     */
    boolean setPrimaryDept(Long userId, Long deptId);

    /**
     * 移除部门
     */
    boolean removeDepts(Long userId, List<Long> deptIds);

    /**
     * 更新用户部门
     */
    boolean updateDepts(Long userId, List<Long> deptIds);

    /**
     * 清空用户部门
     */
    boolean clearDepts(Long userId);

    /**
     * 获取用户部门ID列表
     */
    List<Long> getDeptIds(Long userId);

    /**
     * 获取用户主部门
     */
    Long getPrimaryDept(Long userId);

    /**
     * 批量分配部门
     */
    boolean batchAssignDepts(List<Long> userIds, List<Long> deptIds);

    /**
     * 批量移除部门
     */
    boolean batchRemoveDepts(List<Long> userIds, List<Long> deptIds);
}
