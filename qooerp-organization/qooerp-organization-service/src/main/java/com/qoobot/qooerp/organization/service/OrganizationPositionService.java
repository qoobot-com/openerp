package com.qoobot.qooerp.organization.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.organization.dto.OrganizationPositionDTO;
import com.qoobot.qooerp.organization.entity.OrganizationPosition;
import com.qoobot.qooerp.organization.vo.OrganizationPositionVO;

/**
 * 岗位服务接口
 */
public interface OrganizationPositionService extends IService<OrganizationPosition> {

    /**
     * 创建岗位
     *
     * @param position 岗位信息
     * @return 岗位ID
     */
    Long createPosition(OrganizationPosition position);

    /**
     * 更新岗位
     *
     * @param position 岗位信息
     */
    void updatePosition(OrganizationPosition position);

    /**
     * 删除岗位
     *
     * @param positionId 岗位ID
     */
    void deletePosition(Long positionId);

    /**
     * 获取岗位详情
     *
     * @param positionId 岗位ID
     * @return 岗位VO
     */
    OrganizationPositionVO getPositionDetail(Long positionId);

    /**
     * 分页查询岗位
     *
     * @param dto 查询条件
     * @return 分页结果
     */
    PageResult<OrganizationPositionVO> pagePosition(OrganizationPositionDTO dto);

    /**
     * 根据部门ID查询岗位列表
     *
     * @param deptId 部门ID
     * @return 岗位列表
     */
    List<OrganizationPositionVO> listByDeptId(Long deptId);

    /**
     * 切换岗位状态
     *
     * @param positionId 岗位ID
     * @param status     状态
     */
    void changeStatus(Long positionId, Integer status);
}
