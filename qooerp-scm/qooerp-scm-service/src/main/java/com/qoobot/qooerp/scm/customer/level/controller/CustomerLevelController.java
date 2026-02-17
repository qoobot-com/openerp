package com.qoobot.qooerp.scm.customer.level.controller;

import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.scm.customer.level.domain.CustomerLevel;
import com.qoobot.qooerp.scm.customer.level.dto.CustomerLevelDTO;
import com.qoobot.qooerp.scm.customer.level.service.ICustomerLevelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 客户等级控制器
 *
 * @author QooERP
 * @since 2026-02-17
 */
@Tag(name = "客户等级管理", description = "客户等级相关接口")
@RestController
@RequestMapping("/api/scm/customer-levels")
@RequiredArgsConstructor
public class CustomerLevelController {

    private final ICustomerLevelService customerLevelService;

    /**
     * 创建客户等级
     */
    @Operation(summary = "创建客户等级")
    @PostMapping
    public Result<Long> create(@RequestBody CustomerLevelDTO dto) {
        Long id = customerLevelService.create(dto);
        return Result.success(id);
    }

    /**
     * 更新客户等级
     */
    @Operation(summary = "更新客户等级")
    @PutMapping("/{id}")
    public Result<Boolean> update(@PathVariable Long id, @RequestBody CustomerLevelDTO dto) {
        dto.setId(id);
        boolean success = customerLevelService.update(dto);
        return Result.success(success);
    }

    /**
     * 删除客户等级
     */
    @Operation(summary = "删除客户等级")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        boolean success = customerLevelService.delete(id);
        return Result.success(success);
    }

    /**
     * 获取等级详情
     */
    @Operation(summary = "获取等级详情")
    @GetMapping("/{id}")
    public Result<CustomerLevelDTO> getDetail(@PathVariable Long id) {
        CustomerLevelDTO dto = customerLevelService.getDetail(id);
        return Result.success(dto);
    }

    /**
     * 分页查询等级列表
     */
    @Operation(summary = "分页查询等级列表")
    @GetMapping
    public Result<PageResult<CustomerLevel>> queryPage(@RequestParam(defaultValue = "1") Integer pageNo,
                                                        @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<CustomerLevel> pageResult = customerLevelService.queryPage(pageNo, pageSize);
        return Result.success(pageResult);
    }

    /**
     * 获取所有启用的等级
     */
    @Operation(summary = "获取所有启用的等级")
    @GetMapping("/enabled")
    public Result<List<CustomerLevel>> getAllEnabledLevels() {
        List<CustomerLevel> levels = customerLevelService.getAllEnabledLevels();
        return Result.success(levels);
    }

    /**
     * 根据交易金额和订单数自动判定客户等级
     */
    @Operation(summary = "自动判定客户等级")
    @GetMapping("/determine")
    public Result<CustomerLevel> determineCustomerLevel(@RequestParam java.math.BigDecimal totalAmount,
                                                         @RequestParam Integer orderCount) {
        CustomerLevel level = customerLevelService.determineCustomerLevel(totalAmount, orderCount);
        return Result.success(level);
    }

    /**
     * 更新等级状态
     */
    @Operation(summary = "更新等级状态")
    @PutMapping("/{id}/status")
    public Result<Boolean> updateStatus(@PathVariable Long id, @RequestParam String status) {
        boolean success = customerLevelService.updateStatus(id, status);
        return Result.success(success);
    }

    /**
     * 校验等级编码是否存在
     */
    @Operation(summary = "校验等级编码是否存在")
    @GetMapping("/check-code")
    public Result<Boolean> checkCodeExists(@RequestParam String levelCode,
                                           @RequestParam(required = false) Long excludeId) {
        boolean exists = customerLevelService.checkCodeExists(levelCode, excludeId);
        return Result.success(exists);
    }
}
