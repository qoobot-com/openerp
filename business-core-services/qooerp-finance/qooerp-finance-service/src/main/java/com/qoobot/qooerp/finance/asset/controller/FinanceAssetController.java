package com.qoobot.qooerp.finance.asset.controller;

import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.finance.asset.domain.FinanceAsset;
import com.qoobot.qooerp.finance.asset.domain.FinanceAssetDepreciation;
import com.qoobot.qooerp.finance.asset.dto.AssetQueryDTO;
import com.qoobot.qooerp.finance.asset.dto.FinanceAssetDTO;
import com.qoobot.qooerp.finance.asset.service.IFinanceAssetService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 固定资产控制器
 */
@Api(tags = "固定资产管理")
@RestController
@RequestMapping("/finance/asset")
public class FinanceAssetController {

    @Autowired
    private IFinanceAssetService assetService;

    @ApiOperation("分页查询资产列表")
    @GetMapping("/page")
    public Result<List<FinanceAsset>> queryPage(AssetQueryDTO queryDTO) {
        return Result.success(assetService.queryPage(queryDTO).getRecords());
    }

    @ApiOperation("创建资产卡片")
    @PostMapping("/create")
    public Result<Long> create(@RequestBody FinanceAssetDTO dto) {
        Long assetId = assetService.create(dto);
        return Result.success(assetId);
    }

    @ApiOperation("更新资产卡片")
    @PostMapping("/update")
    public Result<Void> update(@RequestBody FinanceAssetDTO dto) {
        assetService.update(dto);
        return Result.success();
    }

    @ApiOperation("删除资产卡片")
    @DeleteMapping("/delete/{assetId}")
    public Result<Void> delete(@PathVariable Long assetId) {
        assetService.delete(assetId);
        return Result.success();
    }

    @ApiOperation("资产调拨")
    @PostMapping("/transfer/{assetId}")
    public Result<Void> transfer(
            @PathVariable Long assetId,
            @RequestParam Long departmentId,
            @RequestParam(required = false) String departmentName,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String userName,
            @RequestParam(required = false) String location) {
        assetService.transfer(assetId, departmentId, departmentName, userId, userName, location);
        return Result.success();
    }

    @ApiOperation("资产报废")
    @PostMapping("/scrap/{assetId}")
    public Result<Void> scrap(@PathVariable Long assetId) {
        assetService.scrap(assetId);
        return Result.success();
    }

    @ApiOperation("计算单个资产折旧")
    @PostMapping("/depreciation/calculate")
    public Result<FinanceAssetDepreciation> calculateDepreciation(
            @RequestParam Long assetId,
            @RequestParam String period) {
        FinanceAssetDepreciation depreciation = assetService.calculateDepreciation(assetId, period);
        return Result.success(depreciation);
    }

    @ApiOperation("批量计提折旧")
    @PostMapping("/depreciation/batch")
    public Result<Integer> batchCalculateDepreciation(@RequestParam String period) {
        Integer count = assetService.batchCalculateDepreciation(period);
        return Result.success(count);
    }

    @ApiOperation("查询资产详情")
    @GetMapping("/{assetId}")
    public Result<FinanceAsset> getAsset(@PathVariable Long assetId) {
        FinanceAsset asset = assetService.getById(assetId);
        return Result.success(asset);
    }

    @ApiOperation("查询折旧历史")
    @GetMapping("/depreciation/history/{assetId}")
    public Result<List<FinanceAssetDepreciation>> getDepreciationHistory(@PathVariable Long assetId) {
        List<FinanceAssetDepreciation> history = assetService.getDepreciationHistory(assetId);
        return Result.success(history);
    }
}
