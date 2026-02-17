package com.qoobot.crm.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qoobot.qooerp.common.core.page.PageParam;
import com.qoobot.qooerp.crm.dto.CrmContractDTO;

/**
 * 合同服务接口
 */
public interface CrmContractService {

    /**
     * 创建合同
     */
    CrmContractDTO createContract(CrmContractDTO dto);

    /**
     * 更新合同
     */
    CrmContractDTO updateContract(Long id, CrmContractDTO dto);

    /**
     * 获取合同详情
     */
    CrmContractDTO getContract(Long id);

    /**
     * 分页查询合同
     */
    IPage<CrmContractDTO> listContracts(PageParam pageParam, Long customerId, Integer status);

    /**
     * 删除合同
     */
    void deleteContract(Long id);

    /**
     * 审核合同
     */
    void approveContract(Long id);

    /**
     * 拒绝合同
     */
    void rejectContract(Long id);
}
