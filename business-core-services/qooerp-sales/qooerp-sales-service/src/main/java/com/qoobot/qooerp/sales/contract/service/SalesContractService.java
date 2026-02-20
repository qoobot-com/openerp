package com.qoobot.qooerp.sales.contract.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.sales.contract.dto.ContractDTO;
import com.qoobot.qooerp.sales.contract.dto.ContractQueryDTO;

/**
 * 销售合同 Service 接口
 */
public interface SalesContractService {

    /**
     * 创建合同
     */
    ContractDTO createContract(ContractDTO dto);

    /**
     * 更新合同
     */
    ContractDTO updateContract(Long id, ContractDTO dto);

    /**
     * 删除合同
     */
    void deleteContract(Long id);

    /**
     * 根据ID查询合同
     */
    ContractDTO getContractById(Long id);

    /**
     * 分页查询合同
     */
    Page<ContractDTO> queryContracts(ContractQueryDTO queryDTO);

    /**
     * 审核合同
     */
    void approveContract(Long id, Long approverId, String approveRemark, boolean approved);

    /**
     * 激活合同
     */
    void activateContract(Long id);

    /**
     * 终止合同
     */
    void terminateContract(Long id, String reason);

    /**
     * 生成合同编号
     */
    String generateContractNo();
}
