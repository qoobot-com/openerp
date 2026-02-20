package com.qoobot.qooerp.finance.accounting.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.common.util.BeanUtils;
import com.qoobot.qooerp.finance.accounting.domain.FinanceAccount;
import com.qoobot.qooerp.finance.accounting.dto.FinanceAccountQueryDTO;
import com.qoobot.qooerp.finance.accounting.dto.FinanceAccountDTO;
import com.qoobot.qooerp.finance.accounting.mapper.FinanceAccountMapper;
import com.qoobot.qooerp.finance.accounting.service.IFinanceAccountService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 会计科目服务实现
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
@Service
public class FinanceAccountServiceImpl extends ServiceImpl<FinanceAccountMapper, FinanceAccount> implements IFinanceAccountService {

    @Override
    public PageResult<FinanceAccount> queryPage(FinanceAccountQueryDTO queryDTO) {
        LambdaQueryWrapper<FinanceAccount> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FinanceAccount::getTenantId, queryDTO.getTenantId())
                .like(StringUtils.isNotBlank(queryDTO.getAccountCode()), FinanceAccount::getAccountCode, queryDTO.getAccountCode())
                .like(StringUtils.isNotBlank(queryDTO.getAccountName()), FinanceAccount::getAccountName, queryDTO.getAccountName())
                .eq(StringUtils.isNotBlank(queryDTO.getAccountType()), FinanceAccount::getAccountType, queryDTO.getAccountType())
                .orderByAsc(FinanceAccount::getAccountCode);

        Page<FinanceAccount> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        page(page, wrapper);

        return PageResult.of(page.getCurrent(), page.getSize(), page.getTotal(), page.getRecords());
    }

    @Override
    public List<FinanceAccount> getAccountTree() {
        LambdaQueryWrapper<FinanceAccount> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(FinanceAccount::getAccountCode);
        List<FinanceAccount> allAccounts = list(wrapper);

        return buildTree(allAccounts, 0L);
    }

    private List<FinanceAccount> buildTree(List<FinanceAccount> accounts, Long parentId) {
        return accounts.stream()
                .filter(account -> parentId.equals(account.getParentId()))
                .peek(account -> {
                    List<FinanceAccount> children = buildTree(accounts, account.getId());
                    account.setChildren(children);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(FinanceAccountDTO dto) {
        FinanceAccount account = BeanUtils.copyBean(dto, FinanceAccount.class);
        save(account);

        // 更新父科目是否末级
        if (account.getParentId() != null && account.getParentId() > 0) {
            updateById(new FinanceAccount() {{
                setId(account.getParentId());
                setIsLeaf(0);
            }});
        }

        return account.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean update(FinanceAccountDTO dto) {
        FinanceAccount account = BeanUtils.copyBean(dto, FinanceAccount.class);
        return updateById(account);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean delete(Long id) {
        // 检查是否有子科目
        LambdaQueryWrapper<FinanceAccount> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FinanceAccount::getParentId, id);
        long count = count(wrapper);
        if (count > 0) {
            throw new RuntimeException("存在子科目，不能删除");
        }

        return removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean toggleEnabled(Long id, Boolean enabled) {
        LambdaUpdateWrapper<FinanceAccount> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(FinanceAccount::getId, id)
                .set(FinanceAccount::getIsEnabled, enabled ? 1 : 0);
        return update(wrapper);
    }

    @Override
    public FinanceAccount getByCode(String accountCode) {
        LambdaQueryWrapper<FinanceAccount> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FinanceAccount::getAccountCode, accountCode);
        return getOne(wrapper);
    }

    @Override
    public List<FinanceAccount> getLeafAccounts() {
        LambdaQueryWrapper<FinanceAccount> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FinanceAccount::getIsLeaf, 1)
                .eq(FinanceAccount::getIsEnabled, 1)
                .orderByAsc(FinanceAccount::getAccountCode);
        return list(wrapper);
    }

    @Override
    public List<FinanceAccount> getCashAccounts() {
        LambdaQueryWrapper<FinanceAccount> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FinanceAccount::getIsCashAccount, 1)
                .eq(FinanceAccount::getIsEnabled, 1)
                .orderByAsc(FinanceAccount::getAccountCode);
        return list(wrapper);
    }

    @Override
    public List<FinanceAccount> getBankAccounts() {
        LambdaQueryWrapper<FinanceAccount> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FinanceAccount::getIsBankAccount, 1)
                .eq(FinanceAccount::getIsEnabled, 1)
                .orderByAsc(FinanceAccount::getAccountCode);
        return list(wrapper);
    }

    @Override
    public List<FinanceAccount> listByType(String accountType) {
        LambdaQueryWrapper<FinanceAccount> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FinanceAccount::getAccountType, accountType)
                .eq(FinanceAccount::getIsEnabled, 1)
                .orderByAsc(FinanceAccount::getAccountCode);
        return list(wrapper);
    }
}
