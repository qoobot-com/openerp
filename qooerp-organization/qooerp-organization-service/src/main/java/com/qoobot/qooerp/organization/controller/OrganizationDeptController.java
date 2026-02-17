package com.qoobot.qooerp.organization.controller;

import com.qoobot.qooerp.common.annotation.OperationLog;
import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.organization.dto.OrganizationDeptDTO;
import com.qoobot.qooerp.organization.entity.OrganizationDept;
import com.qoobot.qooerp.organization.service.OrganizationDeptService;
import com.qoobot.qooerp.organization.vo.OrganizationDeptVO;
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
 * 部门管理Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/organization/dept")
@RequiredArgsConstructor
@Validated
@Tag(name = "部门管理", description = "部门管理接口")
public class OrganizationDeptController {

    private final OrganizationDeptService deptService;

    @PostMapping("/create")
    @Operation(summary = "创建部门")
    @OperationLog(module = "部门管理", operation = "创建部门")
    public Result<Long> createDept(@Valid @RequestBody OrganizationDept dept) {
        Long deptId = deptService.createDept(dept);
        return Result.success(deptId);
    }

    @PutMapping("/update")
    @Operation(summary = "更新部门")
    @OperationLog(module = "部门管理", operation = "更新部门")
    public Result<Void> updateDept(@Valid @RequestBody OrganizationDept dept) {
        deptService.updateDept(dept);
        return Result.success();
    }

    @DeleteMapping("/delete/{deptId}")
    @Operation(summary = "删除部门")
    @OperationLog(module = "部门管理", operation = "删除部门")
    public Result<Void> deleteDept(@PathVariable @NotNull(message = "部门ID不能为空") Long deptId) {
        deptService.deleteDept(deptId);
        return Result.success();
    }

    @GetMapping("/detail/{deptId}")
    @Operation(summary = "获取部门详情")
    public Result<OrganizationDeptVO> getDeptDetail(@PathVariable @NotNull(message = "部门ID不能为空") Long deptId) {
        OrganizationDeptVO deptVO = deptService.getDeptDetail(deptId);
        return Result.success(deptVO);
    }

    @PostMapping("/page")
    @Operation(summary = "分页查询部门")
    public Result<PageResult<OrganizationDeptVO>> pageDept(@RequestBody OrganizationDeptDTO dto) {
        PageResult<OrganizationDeptVO> pageResult = deptService.pageDept(dto);
        return Result.success(pageResult);
    }

    @GetMapping("/tree")
    @Operation(summary = "获取部门树")
    public Result<List<OrganizationDeptVO>> getDeptTree(@RequestParam(required = false) Long parentId) {
        List<OrganizationDeptVO> tree = deptService.getDeptTree(parentId);
        return Result.success(tree);
    }

    @PutMapping("/move")
    @Operation(summary = "移动部门")
    @OperationLog(module = "部门管理", operation = "移动部门")
    public Result<Void> moveDept(
            @RequestParam @NotNull(message = "部门ID不能为空") Long deptId,
            @RequestParam @NotNull(message = "新父部门ID不能为空") Long newParentId) {
        deptService.moveDept(deptId, newParentId);
        return Result.success();
    }

    @PutMapping("/sort")
    @Operation(summary = "更新部门排序")
    @OperationLog(module = "部门管理", operation = "更新部门排序")
    public Result<Void> updateDeptSort(@RequestBody List<Long> deptIdList) {
        deptService.updateDeptSort(deptIdList);
        return Result.success();
    }

    @PutMapping("/status/change")
    @Operation(summary = "切换部门状态")
    @OperationLog(module = "部门管理", operation = "切换部门状态")
    public Result<Void> changeStatus(
            @RequestParam @NotNull(message = "部门ID不能为空") Long deptId,
            @RequestParam @NotNull(message = "状态不能为空") Integer status) {
        deptService.changeStatus(deptId, status);
        return Result.success();
    }
}
