package com.qoobot.qooerp.scm.customer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.scm.customer.domain.ScmCustomer;
import com.qoobot.qooerp.scm.customer.dto.CustomerDTO;
import com.qoobot.qooerp.scm.customer.dto.CustomerQueryDTO;

/**
 * 客户Service接口
 *
 * @author QooERP
 * @since 2026-02-18
 */
public interface IScmCustomerService extends IService<ScmCustomer> {

    Long createCustomer(CustomerDTO dto);

    boolean updateCustomer(Long id, CustomerDTO dto);

    boolean deleteCustomer(Long id);

    CustomerDTO getCustomer(Long id);

    PageResult<ScmCustomer> queryCustomers(CustomerQueryDTO queryDTO);

    boolean updateStatus(Long id, String status);

    String generateCustomerCode();
}
