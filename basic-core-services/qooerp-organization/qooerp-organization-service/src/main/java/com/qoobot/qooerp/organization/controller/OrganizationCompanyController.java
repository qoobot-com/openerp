package com.qoobot.qooerp.organization.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.common.util.BeanUtils;
import com.qoobot.qooerp.organization.dto.OrganizationCompanyDTO;
import com.qoobot.qooerp.organization.dto.OrganizationCompanyVO;
import com.qoobot.qooerp.organization.service.OrganizationCompanyService;
import com.qoobot.qooerp.organization.vo.OrganizationTreeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 公司管理Controller
 */
@Tag(name = "公司管理", description = "公司管理相关接口")
@RestController
@RequestMapping("/api/organization/company")
@RequiredArgsConstructor
public class OrganizationCompanyController {

    private final OrganizationCompanyService companyService;

    @Operation(summary = "创建公司")
    @PostMapping("/create")
    public Result<Long> create(@Valid @RequestBody OrganizationCompanyDTO dto) {
        Long id = companyService.create(dto);
        return Result.success(id);
    }

    @Operation(summary = "更新公司")
    @PostMapping("/update")
    public Result<Void> update(@Valid @RequestBody OrganizationCompanyDTO dto) {
        companyService.update(dto);
        return Result.success();
    }

    @Operation(summary = "删除公司")
    @DeleteMapping("/delete/{companyId}")
    public Result<Void> delete(@PathVariable Long companyId) {
        companyService.delete(companyId);
        return Result.success();
    }

    @Operation(summary = "获取公司详情")
    @GetMapping("/detail/{companyId}")
    public Result<OrganizationCompanyVO> getDetail(@PathVariable Long companyId) {
        OrganizationCompanyVO company = companyService.get(companyId);
        return Result.success(company);
    }

    @Operation(summary = "分页查询公司")
    @PostMapping("/page")
    public Result<Page<OrganizationCompanyVO>> page(@RequestBody Page<OrganizationCompanyDTO> page) {
        Page<OrganizationCompanyVO> result = companyService.page(page);
        return Result.success(result);
    }

    @Operation(summary = "获取公司树")
    @GetMapping("/tree")
    public Result<List<OrganizationTreeVO>> getTree() {
        List<OrganizationTreeVO> tree = companyService.getCompanyTree();
        return Result.success(tree);
    }
}
