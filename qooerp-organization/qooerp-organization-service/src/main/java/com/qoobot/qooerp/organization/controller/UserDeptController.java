package com.qoobot.qooerp.organization.controller;

import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.organization.dto.UserDeptDTO;
import com.qoobot.qooerp.organization.service.UserDeptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 用户部门关联Controller
 */
@Tag(name = "用户部门关联", description = "用户部门关联相关接口")
@RestController
@RequestMapping("/api/organization/userDept")
@RequiredArgsConstructor
public class UserDeptController {

    private final UserDeptService userDeptService;

    @Operation(summary = "绑定用户部门")
    @PostMapping("/bind")
    public Result<Void> bind(@Valid @RequestBody UserDeptDTO dto) {
        userDeptService.bind(dto);
        return Result.success();
    }

    @Operation(summary = "解绑用户部门")
    @DeleteMapping("/unbind")
    public Result<Void> unbind(@RequestParam Long userId, @RequestParam Long deptId) {
        userDeptService.unbind(userId, deptId);
        return Result.success();
    }

    @Operation(summary = "获取用户部门列表")
    @GetMapping("/list/{userId}")
    public Result<List<Map<String, Object>>> getUserDepts(@PathVariable Long userId) {
        List<Map<String, Object>> depts = userDeptService.getUserDepts(userId);
        return Result.success(depts);
    }
}
