package com.qoobot.qooerp.finance.period.controller;

import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.finance.period.domain.FinancePeriod;
import com.qoobot.qooerp.finance.period.service.IFinancePeriodService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 会计期间控制器
 * 
 * @author QooERP Team
 * @since 20xx-xx-xx
 */
@Tag(name = "会计期间管理", description = "会计期间相关接口")
@RestController
@RequestMapping("/finance/period")
@RequiredArgsConstructor
public class FinancePeriodController {

    private final IFinancePeriodService periodService;

    @Operation(summary = "创建会计期间")
    @PostMapping("/create")
    public Result<FinancePeriod> createPeriod(@RequestBody FinancePeriod period) {
        return Result.success(periodService.createPeriod(period));
    }

    @Operation(summary = "查询当前会计期间")
    @GetMapping("/current")
    public Result<FinancePeriod> getCurrentPeriod() {
        return Result.success(periodService.getCurrentPeriod());
    }

    @Operation(summary = "查询会计年度期间列表")
    @GetMapping("/year/{year}")
    public Result<List<FinancePeriod>> getPeriodsByYear(
            @Parameter(description = "会计年度") @PathVariable Integer year) {
        return Result.success(periodService.getPeriodsByYear(year));
    }

    @Operation(summary = "开启期间")
    @PostMapping("/open/{id}")
    public Result<Void> openPeriod(@Parameter(description = "期间ID") @PathVariable Long id) {
        periodService.openPeriod(id);
        return Result.success();
    }

    @Operation(summary = "关闭期间")
    @PostMapping("/close/{id}")
    public Result<Void> closePeriod(
            @Parameter(description = "期间ID") @PathVariable Long id,
            @Parameter(description = "操作人ID") @RequestParam Long operatorId,
            @Parameter(description = "操作人姓名") @RequestParam String operatorName) {
        periodService.closePeriod(id, operatorId, operatorName);
        return Result.success();
    }
}
