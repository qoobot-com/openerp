package com.qoobot.qooerp.scm.customer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.scm.customer.domain.Customer;
import com.qoobot.qooerp.scm.customer.dto.CustomerDTO;
import com.qoobot.qooerp.scm.customer.dto.CustomerQueryDTO;

/**
 * 客户Service接口
 *
 * @author QooERP
 * @since 2026-02-17
 */
public interface ICustomerService extends IService<Customer> {

    /**
     * 创建客户
     *
     * @param dto 客户DTO
     * @return 客户ID
     */
    Long create(CustomerDTO dto);

    /**
     * 更新客户
     *
     * @param dto 客户DTO
     * @return 是否成功
     */
    boolean update(CustomerDTO dto);

    /**
     * 删除客户
     *
     * @param id 客户ID
     * @return 是否成功
     */
    boolean delete(Long id);

    /**
     * 查询客户详情
     *
     * @param id 客户ID
     * @return 客户DTO
     */
    CustomerDTO getDetail(Long id);

    /**
     * 分页查询客户列表
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    PageResult<Customer> queryPage(CustomerQueryDTO queryDTO);

    /**
     * 根据编码查询客户
     *
     * @param customerCode 客户编码
     * @return 客户
     */
    Customer getByCode(String customerCode);

    /**
     * 更新客户状态
     *
     * @param id 客户ID
     * @param status 状态
     * @return 是否成功
     */
    boolean updateStatus(Long id, String status);

    /**
     * 生成客户编码
     *
     * @return 客户编码
     */
    String generateCustomerCode();
}
