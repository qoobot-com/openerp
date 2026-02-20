package com.qoobot.qooerp.finance.voucher.controller;

import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.finance.voucher.domain.FinanceVoucher;
import com.qoobot.qooerp.finance.voucher.dto.FinanceVoucherDTO;
import com.qoobot.qooerp.finance.voucher.service.IFinanceVoucherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 财务凭证控制器
 */
@Api(tags = "财务凭证管理")
@RestController
@RequestMapping("/finance/voucher")
public class FinanceVoucherController {

    @Autowired
    private IFinanceVoucherService voucherService;

    @ApiOperation("创建凭证")
    @PostMapping("/create")
    public Result<FinanceVoucher> createVoucher(
            @ApiParam("凭证信息") @RequestBody FinanceVoucherDTO dto) {
        Long voucherId = voucherService.create(dto);
        FinanceVoucher voucher = voucherService.getById(voucherId);
        return Result.success(voucher);
    }

    @ApiOperation("提交审核")
    @PostMapping("/submit/{voucherId}")
    public Result<Void> submitVoucher(
            @ApiParam("凭证ID") @PathVariable Long voucherId) {
        voucherService.submit(voucherId);
        return Result.success();
    }

    @ApiOperation("审核凭证")
    @PostMapping("/review/{voucherId}")
    public Result<Void> reviewVoucher(
            @ApiParam("凭证ID") @PathVariable Long voucherId,
            @ApiParam("是否通过") @RequestParam Boolean approved,
            @ApiParam("审核意见") @RequestParam String reviewComment) {
        voucherService.review(voucherId, approved, reviewComment);
        return Result.success();
    }

    @ApiOperation("记账")
    @PostMapping("/post/{voucherId}")
    public Result<Void> postVoucher(@ApiParam("凭证ID") @PathVariable Long voucherId) {
        voucherService.posting(voucherId);
        return Result.success();
    }

    @ApiOperation("反记账")
    @PostMapping("/unpost/{voucherId}")
    public Result<Void> unpostVoucher(@ApiParam("凭证ID") @PathVariable Long voucherId) {
        voucherService.unPosting(voucherId);
        return Result.success();
    }

    @ApiOperation("获取凭证详情")
    @GetMapping("/{voucherId}")
    public Result<FinanceVoucher> getVoucher(@ApiParam("凭证ID") @PathVariable Long voucherId) {
        FinanceVoucher voucher = voucherService.getDetail(voucherId);
        return Result.success(voucher);
    }
}
