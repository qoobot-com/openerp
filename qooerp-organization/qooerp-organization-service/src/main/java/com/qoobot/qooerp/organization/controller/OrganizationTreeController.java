package com.qoobot.qooerp.organization.controller;

import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.organization.service.OrganizationTreeService;
import com.qoobot.qooerp.organization.vo.OrganizationTreeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 组织树Controller
 */
@Tag(name = "组织架构树", description = "组织架构树相关接口")
@RestController
@RequestMapping("/api/organization/tree")
@RequiredArgsConstructor
public class OrganizationTreeController {

    private final OrganizationTreeService treeService;

    @Operation(summary = "获取完整组织架构树")
    @GetMapping("/company/{companyId}")
    public Result<List<OrganizationTreeVO>> getFullTree(@PathVariable Long companyId) {
        List<OrganizationTreeVO> tree = treeService.getFullOrganizationTree(companyId);
        return Result.success(tree);
    }

    @Operation(summary = "获取部门组织树")
    @GetMapping("/dept/{companyId}")
    public Result<List<OrganizationTreeVO>> getDeptTree(@PathVariable Long companyId) {
        List<OrganizationTreeVO> tree = treeService.getDeptTree(companyId);
        return Result.success(tree);
    }

    @Operation(summary = "获取部门路径")
    @GetMapping("/dept/path/{deptId}")
    public Result<String> getDeptPath(@PathVariable Long deptId) {
        String path = treeService.getDeptPath(deptId);
        return Result.success(path);
    }
}
