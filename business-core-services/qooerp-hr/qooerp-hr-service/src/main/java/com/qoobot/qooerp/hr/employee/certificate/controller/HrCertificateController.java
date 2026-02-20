package com.qoobot.qooerp.hr.employee.certificate.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.hr.employee.certificate.domain.HrCertificate;
import com.qoobot.qooerp.hr.employee.certificate.mapper.HrCertificateMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 员工证件管理控制器
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
@Slf4j
@RestController
@RequestMapping("/certificate")
@RequiredArgsConstructor
@Tag(name = "员工证件管理", description = "员工证件管理")
@Validated
public class HrCertificateController {

    private final HrCertificateMapper certificateMapper;

    @Operation(summary = "创建员工证件")
    @PostMapping
    public Result<Long> createCertificate(@Valid @RequestBody HrCertificate certificate) {
        certificateMapper.insert(certificate);
        return Result.success(certificate.getId());
    }

    @Operation(summary = "更新员工证件")
    @PutMapping
    public Result<Boolean> updateCertificate(@Valid @RequestBody HrCertificate certificate) {
        return Result.success(certificateMapper.updateById(certificate) > 0);
    }

    @Operation(summary = "删除员工证件")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteCertificate(@PathVariable Long id) {
        return Result.success(certificateMapper.deleteById(id) > 0);
    }

    @Operation(summary = "获取证件详情")
    @GetMapping("/{id}")
    public Result<HrCertificate> getCertificate(@PathVariable Long id) {
        return Result.success(certificateMapper.selectById(id));
    }

    @Operation(summary = "获取员工证件列表")
    @GetMapping("/employee/{employeeId}")
    public Result<List<HrCertificate>> getCertificatesByEmployee(@PathVariable Long employeeId) {
        LambdaQueryWrapper<HrCertificate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HrCertificate::getEmployeeId, employeeId);
        return Result.success(certificateMapper.selectList(wrapper));
    }
}
