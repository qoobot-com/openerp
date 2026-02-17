package com.qoobot.qooerp.scm.logistics.controller;

import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.scm.logistics.domain.LogisticsTracking;
import com.qoobot.qooerp.scm.logistics.dto.LogisticsTrackingDTO;
import com.qoobot.qooerp.scm.logistics.dto.TrackingQueryDTO;
import com.qoobot.qooerp.scm.logistics.service.ILogisticsTrackingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 物流跟踪控制器
 *
 * @author QooERP
 * @since 2026-02-17
 */
@Tag(name = "物流跟踪管理", description = "物流跟踪相关接口")
@RestController
@RequestMapping("/api/scm/logistics/tracking")
@RequiredArgsConstructor
public class LogisticsTrackingController {

    private final ILogisticsTrackingService logisticsTrackingService;

    /**
     * 创建物流跟踪
     */
    @Operation(summary = "创建物流跟踪")
    @PostMapping
    public Result<Long> create(@RequestBody LogisticsTrackingDTO dto) {
        Long id = logisticsTrackingService.create(dto);
        return Result.success(id);
    }

    /**
     * 更新物流跟踪
     */
    @Operation(summary = "更新物流跟踪")
    @PutMapping("/{id}")
    public Result<Boolean> update(@PathVariable Long id, @RequestBody LogisticsTrackingDTO dto) {
        dto.setId(id);
        boolean success = logisticsTrackingService.update(dto);
        return Result.success(success);
    }

    /**
     * 删除物流跟踪
     */
    @Operation(summary = "删除物流跟踪")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        boolean success = logisticsTrackingService.delete(id);
        return Result.success(success);
    }

    /**
     * 获取物流跟踪详情
     */
    @Operation(summary = "获取物流跟踪详情")
    @GetMapping("/{id}")
    public Result<LogisticsTrackingDTO> getDetail(@PathVariable Long id) {
        LogisticsTrackingDTO dto = logisticsTrackingService.getDetail(id);
        return Result.success(dto);
    }

    /**
     * 分页查询物流跟踪列表
     */
    @Operation(summary = "分页查询物流跟踪列表")
    @GetMapping
    public Result<PageResult<LogisticsTracking>> queryPage(TrackingQueryDTO queryDTO) {
        PageResult<LogisticsTracking> pageResult = logisticsTrackingService.queryPage(queryDTO);
        return Result.success(pageResult);
    }

    /**
     * 根据运单号查询物流跟踪
     */
    @Operation(summary = "根据运单号查询物流跟踪")
    @GetMapping("/tracking-number/{trackingNumber}")
    public Result<LogisticsTrackingDTO> getByTrackingNumber(@PathVariable String trackingNumber) {
        LogisticsTrackingDTO dto = logisticsTrackingService.getByTrackingNumber(trackingNumber);
        return Result.success(dto);
    }

    /**
     * 根据订单ID查询物流跟踪
     */
    @Operation(summary = "根据订单ID查询物流跟踪")
    @GetMapping("/order/{orderId}")
    public Result<LogisticsTrackingDTO> getByOrderId(@PathVariable Long orderId) {
        LogisticsTrackingDTO dto = logisticsTrackingService.getByOrderId(orderId);
        return Result.success(dto);
    }

    /**
     * 更新物流状态
     */
    @Operation(summary = "更新物流状态")
    @PutMapping("/{id}/status")
    public Result<Boolean> updateStatus(@PathVariable Long id, @RequestParam String status) {
        boolean success = logisticsTrackingService.updateStatus(id, status);
        return Result.success(success);
    }

    /**
     * 生成运单号
     */
    @Operation(summary = "生成运单号")
    @GetMapping("/tracking-number/generate")
    public Result<String> generateTrackingNumber() {
        String trackingNumber = logisticsTrackingService.generateTrackingNumber();
        return Result.success(trackingNumber);
    }

    /**
     * 更新物流位置
     */
    @Operation(summary = "更新物流位置")
    @PutMapping("/{id}/location")
    public Result<Boolean> updateLocation(@PathVariable Long id, @RequestParam String currentLocation) {
        boolean success = logisticsTrackingService.updateLocation(id, currentLocation);
        return Result.success(success);
    }
}
