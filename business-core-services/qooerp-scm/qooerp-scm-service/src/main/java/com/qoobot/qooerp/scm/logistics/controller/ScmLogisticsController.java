package com.qoobot.qooerp.scm.logistics.controller;

import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.scm.logistics.domain.ScmLogistics;
import com.qoobot.qooerp.scm.logistics.domain.ScmLogisticsTracking;
import com.qoobot.qooerp.scm.logistics.dto.LogisticsDTO;
import com.qoobot.qooerp.scm.logistics.dto.LogisticsQueryDTO;
import com.qoobot.qooerp.scm.logistics.service.IScmLogisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 物流管理Controller
 *
 * @author QooERP
 * @since 2026-02-18
 */
@Tag(name = "物流管理")
@RestController
@RequestMapping("/api/scm/logistics")
@RequiredArgsConstructor
public class ScmLogisticsController {

    private final IScmLogisticsService logisticsService;

    @Operation(summary = "创建物流")
    @PostMapping
    public Result<Long> createLogistics(@RequestBody @Valid LogisticsDTO dto) {
        Long id = logisticsService.createLogistics(dto);
        return Result.success(id);
    }

    @Operation(summary = "更新物流")
    @PutMapping("/{id}")
    public Result<Boolean> updateLogistics(@PathVariable Long id, @RequestBody @Valid LogisticsDTO dto) {
        boolean result = logisticsService.updateLogistics(id, dto);
        return Result.success(result);
    }

    @Operation(summary = "删除物流")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteLogistics(@PathVariable Long id) {
        boolean result = logisticsService.deleteLogistics(id);
        return Result.success(result);
    }

    @Operation(summary = "查询物流详情")
    @GetMapping("/{id}")
    public Result<LogisticsDTO> getLogistics(@PathVariable Long id) {
        LogisticsDTO dto = logisticsService.getLogistics(id);
        return Result.success(dto);
    }

    @Operation(summary = "分页查询物流列表")
    @GetMapping
    public Result<PageResult<ScmLogistics>> queryLogistics(LogisticsQueryDTO queryDTO) {
        PageResult<ScmLogistics> result = logisticsService.queryLogistics(queryDTO);
        return Result.success(result);
    }

    @Operation(summary = "物流跟踪")
    @PostMapping("/{id}/track")
    public Result<Boolean> trackLogistics(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String description) {
        boolean result = logisticsService.trackLogistics(id, status, location, description);
        return Result.success(result);
    }

    @Operation(summary = "查询物流跟踪")
    @GetMapping("/{id}/tracking")
    public Result<List<ScmLogisticsTracking>> queryTracking(@PathVariable Long id) {
        List<ScmLogisticsTracking> list = logisticsService.queryTracking(id);
        return Result.success(list);
    }

    @Operation(summary = "更新物流状态")
    @PutMapping("/{id}/status")
    public Result<Boolean> updateStatus(@PathVariable Long id, @RequestParam String status) {
        boolean result = logisticsService.updateStatus(id, status);
        return Result.success(result);
    }
}
