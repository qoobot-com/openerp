package com.qoobot.qooerp.crm.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qoobot.qooerp.common.dto.PageQueryDTO;
import com.qoobot.qooerp.crm.dto.CrmOpportunityDTO;

import java.util.List;

/**
 * 商机服务接口
 */
public interface CrmOpportunityService {

    /**
     * 创建商机
     */
    CrmOpportunityDTO createOpportunity(CrmOpportunityDTO dto);

    /**
     * 更新商机
     */
    CrmOpportunityDTO updateOpportunity(Long id, CrmOpportunityDTO dto);

    /**
     * 获取商机详情
     */
    CrmOpportunityDTO getOpportunity(Long id);

    /**
     * 分页查询商机
     */
    IPage<CrmOpportunityDTO> listOpportunities(PageQueryDTO pageParam, Long customerId, Integer stage, Integer status, Long ownerId);

    /**
     * 删除商机
     */
    void deleteOpportunity(Long id);

    /**
     * 更新商机阶段
     */
    void updateStage(Long id, Integer stage);

    /**
     * 商机转合同
     */
    Long convertToContract(Long opportunityId);

    /**
     * 获取客户商机列表
     */
    List<CrmOpportunityDTO> getCustomerOpportunities(Long customerId);
}
