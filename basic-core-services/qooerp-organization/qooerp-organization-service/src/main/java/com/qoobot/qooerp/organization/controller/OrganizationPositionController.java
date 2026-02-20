package com.qoobot.qooerp.organization.controller;

import com.qoobot.qooerp.common.annotation.OperationLog;
import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.organization.dto.OrganizationPositionDTO;
import com.qoobot.qooerp.organization.entity.OrganizationPosition;
import com.qoobot.qooerp.organization.service.OrganizationPositionService;
import com.qoobot.qooerp.organization.vo.OrganizationPositionVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 岗位管理Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/organization/position")
@RequiredArgsConstructor
@Validated
@Tag(name = "岗位管理", description = "岗位管理接口")
public class OrganizationPositionController {

    private final OrganizationPositionService positionService;

    @PostMapping("/create")
    @Operation(summary = "创建岗位")
    @OperationLog(module = "岗位管理", operation = "创建岗位")
    public Result<Long> createPosition(@Valid @RequestBody OrganizationPosition position) {
        Long positionId = positionService.createPosition(position);
        return Result.success(positionId);
    }

    @PutMapping("/update")
    @Operation(summary = "更新岗位")
    @OperationLog(module = "岗位管理", operation = "更新岗位")
    public Result<Void> updatePosition(@Valid @RequestBody OrganizationPosition position) {
        positionService.updatePosition(position);
        return Result.success();
    }

    @DeleteMapping("/delete/{positionId}")
    @Operation(summary = "删除岗位")
    @OperationLog(module = "岗位管理", operation = "删除岗位")
    public Result<Void> deletePosition(@PathVariable @NotNull(message = "岗位ID不能为空") Long positionId) {
        positionService.deletePosition(positionId);
        return Result.success();
    }

    @GetMapping("/detail/{positionId}")
    @Operation(summary = "获取岗位详情")
    public Result<OrganizationPositionVO> getPositionDetail(@PathVariable @NotNull(message = "岗位ID不能为空") Long positionId) {
        OrganizationPositionVO positionVO = positionService.getPositionDetail(positionId);
        return Result.success(positionVO);
    }

    @PostMapping("/page")
    @Operation(summary = "分页查询岗位")
    public Result<PageResult<OrganizationPositionVO>> pagePosition(@RequestBody OrganizationPositionDTO dto) {
        PageResult<OrganizationPositionVO> pageResult = positionService.pagePosition(dto);
        return Result.success(pageResult);
    }

    @GetMapping("/listByDept/{deptId}")
    @Operation(summary = "根据部门ID查询岗位列表")
    public Result<List<OrganizationPositionVO>> listByDeptId(@PathVariable @NotNull(message = "部门ID不能为空") Long deptId) {
        List<OrganizationPositionVO> list = positionService.listByDeptId(deptId);
        return Result.success(list);
    }

    @PutMapping("/status/change")
    @Operation(summary = "切换岗位状态")
    @OperationLog(module = "岗位管理", operation = "切换岗位状态")
    public Result<Void> changeStatus(
            @RequestParam @NotNull(message = "岗位ID不能为空") Long positionId,
            @RequestParam @NotNull(message = "状态不能为空") Integer status) {
        positionService.changeStatus(positionId, status);
        return Result.success();
    }
}
