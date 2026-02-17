package com.qoobot.qooerp.finance.accounting.controller;

import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.finance.accounting.domain.FinanceAccount;
import com.qoobot.qooerp.finance.accounting.dto.FinanceAccountDTO;
import com.qoobot.qooerp.finance.accounting.dto.FinanceAccountQueryDTO;
import com.qoobot.qooerp.finance.accounting.service.IFinanceAccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 会计科目控制器
 */
@Api(tags = "会计科目管理")
@RestController
@RequestMapping("/finance/account")
public class FinanceAccountController {

    @Autowired
    private IFinanceAccountService accountService;

    @ApiOperation("创建会计科目")
    @PostMapping("/create")
    public Result<Long> createAccount(@RequestBody FinanceAccountDTO dto) {
        Long accountId = accountService.create(dto);
        return Result.success(accountId);
    }

    @ApiOperation("更新会计科目")
    @PostMapping("/update")
    public Result<Boolean> updateAccount(@RequestBody FinanceAccountDTO dto) {
        Boolean result = accountService.update(dto);
        return Result.success(result);
    }

    @ApiOperation("删除会计科目")
    @DeleteMapping("/delete/{accountId}")
    public Result<Boolean> deleteAccount(@PathVariable Long accountId) {
        Boolean result = accountService.delete(accountId);
        return Result.success(result);
    }

    @ApiOperation("启用科目")
    @PostMapping("/enable/{accountId}")
    public Result<Boolean> enableAccount(@PathVariable Long accountId) {
        Boolean result = accountService.toggleEnabled(accountId, true);
        return Result.success(result);
    }

    @ApiOperation("禁用科目")
    @PostMapping("/disable/{accountId}")
    public Result<Boolean> disableAccount(@PathVariable Long accountId) {
        Boolean result = accountService.toggleEnabled(accountId, false);
        return Result.success(result);
    }

    @ApiOperation("分页查询科目列表")
    @PostMapping("/page")
    public Result<PageResult<FinanceAccount>> queryPage(@RequestBody FinanceAccountQueryDTO queryDTO) {
        PageResult<FinanceAccount> pageResult = accountService.queryPage(queryDTO);
        return Result.success(pageResult);
    }

    @ApiOperation("获取科目树")
    @GetMapping("/tree")
    public Result<List<FinanceAccount>> getAccountTree() {
        List<FinanceAccount> tree = accountService.getAccountTree();
        return Result.success(tree);
    }

    @ApiOperation("根据编码查询科目")
    @GetMapping("/code/{accountCode}")
    public Result<FinanceAccount> getAccountByCode(@PathVariable String accountCode) {
        FinanceAccount account = accountService.getByCode(accountCode);
        return Result.success(account);
    }

    @ApiOperation("获取末级科目列表")
    @GetMapping("/leaf")
    public Result<List<FinanceAccount>> getLeafAccounts() {
        List<FinanceAccount> accounts = accountService.getLeafAccounts();
        return Result.success(accounts);
    }

    @ApiOperation("获取现金科目列表")
    @GetMapping("/cash")
    public Result<List<FinanceAccount>> getCashAccounts() {
        List<FinanceAccount> accounts = accountService.getCashAccounts();
        return Result.success(accounts);
    }

    @ApiOperation("获取银行科目列表")
    @GetMapping("/bank")
    public Result<List<FinanceAccount>> getBankAccounts() {
        List<FinanceAccount> accounts = accountService.getBankAccounts();
        return Result.success(accounts);
    }
}
