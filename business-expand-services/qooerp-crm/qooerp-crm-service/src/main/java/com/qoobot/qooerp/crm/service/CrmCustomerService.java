package com.qoobot.qooerp.crm.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qoobot.qooerp.common.dto.PageQueryDTO;
import com.qoobot.qooerp.crm.dto.CrmCustomerDTO;
import com.qoobot.qooerp.crm.dto.CrmFollowupDTO;

/**
 * 客户服务接口
 */
public interface CrmCustomerService {

    /**
     * 创建客户
     */
    CrmCustomerDTO createCustomer(CrmCustomerDTO dto);

    /**
     * 更新客户
     */
    CrmCustomerDTO updateCustomer(Long id, CrmCustomerDTO dto);

    /**
     * 获取客户详情
     */
    CrmCustomerDTO getCustomer(Long id);

    /**
     * 分页查询客户
     */
    IPage<CrmCustomerDTO> listCustomers(PageQueryDTO pageParam, String customerName, Integer customerType, Integer level, Integer status);

    /**
     * 删除客户
     */
    void deleteCustomer(Long id);

    /**
     * 添加跟进记录
     */
    void addFollowup(Long customerId, CrmFollowupDTO dto);
}
