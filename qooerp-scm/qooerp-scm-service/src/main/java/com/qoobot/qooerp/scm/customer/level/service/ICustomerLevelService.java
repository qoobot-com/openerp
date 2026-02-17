package com.qoobot.qooerp.scm.customer.level.service;

import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.scm.customer.level.domain.CustomerLevel;
import com.qoobot.qooerp.scm.customer.level.dto.CustomerLevelDTO;

import java.math.BigDecimal;
import java.util.List;

/**
 * 客户等级Service接口
 *
 * @author QooERP
 * @since 2026-02-17
 */
public interface ICustomerLevelService {

    /**
     * 创建客户等级
     *
     * @param dto 客户等级DTO
     * @return 等级ID
     */
    Long create(CustomerLevelDTO dto);

    /**
     * 更新客户等级
     *
     * @param dto 客户等级DTO
     * @return 是否成功
     */
    boolean update(CustomerLevelDTO dto);

    /**
     * 删除客户等级
     *
     * @param id 等级ID
     * @return 是否成功
     */
    boolean delete(Long id);

    /**
     * 获取等级详情
     *
     * @param id 等级ID
     * @return 等级DTO
     */
    CustomerLevelDTO getDetail(Long id);

    /**
     * 分页查询等级列表
     *
     * @param pageNo 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    PageResult<CustomerLevel> queryPage(Integer pageNo, Integer pageSize);

    /**
     * 获取所有启用的等级（按优先级排序）
     *
     * @return 等级列表
     */
    List<CustomerLevel> getAllEnabledLevels();

    /**
     * 根据交易金额和订单数自动判定客户等级
     *
     * @param totalAmount 交易总金额
     * @param orderCount 订单数量
     * @return 客户等级
     */
    CustomerLevel determineCustomerLevel(BigDecimal totalAmount, Integer orderCount);

    /**
     * 根据编码获取等级
     *
     * @param levelCode 等级编码
     * @return 等级实体
     */
    CustomerLevel getByCode(String levelCode);

    /**
     * 更新等级状态
     *
     * @param id 等级ID
     * @param status 状态
     * @return 是否成功
     */
    boolean updateStatus(Long id, String status);

    /**
     * 校验等级编码是否存在
     *
     * @param levelCode 等级编码
     * @param excludeId 排除的ID（更新时使用）
     * @return 是否存在
     */
    boolean checkCodeExists(String levelCode, Long excludeId);
}
