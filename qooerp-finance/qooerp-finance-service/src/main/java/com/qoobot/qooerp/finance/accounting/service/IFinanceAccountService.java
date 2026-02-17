package com.qoobot.qooerp.finance.accounting.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.finance.accounting.domain.FinanceAccount;
import com.qoobot.qooerp.finance.accounting.dto.FinanceAccountQueryDTO;
import com.qoobot.qooerp.finance.accounting.dto.FinanceAccountDTO;

import java.util.List;

/**
 * 会计科目服务接口
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
public interface IFinanceAccountService extends IService<FinanceAccount> {

    /**
     * 分页查询科目列表
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    PageResult<FinanceAccount> queryPage(FinanceAccountQueryDTO queryDTO);

    /**
     * 获取科目树
     *
     * @return 科目树
     */
    List<FinanceAccount> getAccountTree();

    /**
     * 创建科目
     *
     * @param dto 科目信息
     * @return 科目ID
     */
    Long create(FinanceAccountDTO dto);

    /**
     * 更新科目
     *
     * @param dto 科目信息
     * @return 是否成功
     */
    Boolean update(FinanceAccountDTO dto);

    /**
     * 删除科目
     *
     * @param id 科目ID
     * @return 是否成功
     */
    Boolean delete(Long id);

    /**
     * 启用/禁用科目
     *
     * @param id 科目ID
     * @param enabled 是否启用
     * @return 是否成功
     */
    Boolean toggleEnabled(Long id, Boolean enabled);

    /**
     * 根据科目编码查询
     *
     * @param accountCode 科目编码
     * @return 科目信息
     */
    FinanceAccount getByCode(String accountCode);

    /**
     * 获取末级科目列表
     *
     * @return 末级科目列表
     */
    List<FinanceAccount> getLeafAccounts();

    /**
     * 获取现金科目列表
     *
     * @return 现金科目列表
     */
    List<FinanceAccount> getCashAccounts();

    /**
     * 获取银行科目列表
     *
     * @return 银行科目列表
     */
    List<FinanceAccount> getBankAccounts();

    /**
     * 根据科目类型查询
     *
     * @param accountType 科目类型
     * @return 科目列表
     */
    List<FinanceAccount> listByType(String accountType);
}
