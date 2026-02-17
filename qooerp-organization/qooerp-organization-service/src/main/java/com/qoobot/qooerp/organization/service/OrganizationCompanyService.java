package com.qoobot.qooerp.organization.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.organization.dto.OrganizationCompanyDTO;
import com.qoobot.qooerp.organization.dto.OrganizationCompanyVO;
import com.qoobot.qooerp.organization.vo.OrganizationTreeVO;

import java.util.List;

/**
 * 公司服务接口
 */
public interface OrganizationCompanyService {

    /**
     * 创建公司
     */
    Long create(OrganizationCompanyDTO dto);

    /**
     * 更新公司
     */
    void update(OrganizationCompanyDTO dto);

    /**
     * 删除公司
     */
    void delete(Long id);

    /**
     * 获取公司详情
     */
    OrganizationCompanyVO get(Long id);

    /**
     * 分页查询公司
     */
    Page<OrganizationCompanyVO> page(Page<OrganizationCompanyDTO> page);

    /**
     * 获取公司树
     */
    List<OrganizationTreeVO> getCompanyTree();
}
