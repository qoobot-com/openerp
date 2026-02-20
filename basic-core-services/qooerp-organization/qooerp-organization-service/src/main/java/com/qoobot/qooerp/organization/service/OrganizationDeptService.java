package com.qoobot.qooerp.organization.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.organization.dto.OrganizationDeptDTO;
import com.qoobot.qooerp.organization.entity.OrganizationDept;
import com.qoobot.qooerp.organization.vo.OrganizationDeptVO;

import java.util.List;

/**
 * 部门服务接口
 */
public interface OrganizationDeptService extends IService<OrganizationDept> {

    /**
     * 创建部门
     *
     * @param dept 部门信息
     * @return 部门ID
     */
    Long createDept(OrganizationDept dept);

    /**
     * 更新部门
     *
     * @param dept 部门信息
     */
    void updateDept(OrganizationDept dept);

    /**
     * 删除部门
     *
     * @param deptId 部门ID
     */
    void deleteDept(Long deptId);

    /**
     * 获取部门详情
     *
     * @param deptId 部门ID
     * @return 部门VO
     */
    OrganizationDeptVO getDeptDetail(Long deptId);

    /**
     * 分页查询部门
     *
     * @param dto 查询条件
     * @return 分页结果
     */
    PageResult<OrganizationDeptVO> pageDept(OrganizationDeptDTO dto);

    /**
     * 获取部门树
     *
     * @param parentId 父部门ID
     * @return 部门树
     */
    List<OrganizationDeptVO> getDeptTree(Long parentId);

    /**
     * 移动部门
     *
     * @param deptId      部门ID
     * @param newParentId 新父部门ID
     */
    void moveDept(Long deptId, Long newParentId);

    /**
     * 更新部门排序
     *
     * @param deptIdList 部门ID列表
     */
    void updateDeptSort(List<Long> deptIdList);

    /**
     * 获取所有子部门ID（递归）
     *
     * @param deptId 部门ID
     * @return 子部门ID列表
     */
    List<Long> getAllChildDeptIds(Long deptId);

    /**
     * 切换部门状态
     *
     * @param deptId 部门ID
     * @param status 状态
     */
    void changeStatus(Long deptId, Integer status);
}
