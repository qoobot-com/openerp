package com.qoobot.qooerp.organization.controller;

import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.organization.dto.UserPositionDTO;
import com.qoobot.qooerp.organization.service.UserPositionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 用户岗位关联Controller
 */
@Tag(name = "用户岗位关联", description = "用户岗位关联相关接口")
@RestController
@RequestMapping("/api/organization/userPosition")
@RequiredArgsConstructor
public class UserPositionController {

    private final UserPositionService userPositionService;

    @Operation(summary = "绑定用户岗位")
    @PostMapping("/bind")
    public Result<Void> bind(@Valid @RequestBody UserPositionDTO dto) {
        userPositionService.bind(dto);
        return Result.success();
    }

    @Operation(summary = "解绑用户岗位")
    @DeleteMapping("/unbind")
    public Result<Void> unbind(@RequestParam Long userId, @RequestParam Long positionId) {
        userPositionService.unbind(userId, positionId);
        return Result.success();
    }

    @Operation(summary = "获取用户岗位列表")
    @GetMapping("/list/{userId}")
    public Result<List<Map<String, Object>>> getUserPositions(@PathVariable Long userId) {
        List<Map<String, Object>> positions = userPositionService.getUserPositions(userId);
        return Result.success(positions);
    }
}
