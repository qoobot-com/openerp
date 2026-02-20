package com.qoobot.qooerp.organization.service;

import com.qoobot.qooerp.organization.vo.OrganizationTreeVO;

import java.util.List;

/**
 * 组织树服务接口
 */
public interface OrganizationTreeService {

    /**
     * 获取完整组织架构树（公司-部门-岗位）
     */
    List<OrganizationTreeVO> getFullOrganizationTree(Long tenantId);

    /**
     * 获取部门组织树
     */
    List<OrganizationTreeVO> getDeptTree(Long tenantId);

    /**
     * 获取部门路径
     */
    String getDeptPath(Long deptId);
}
