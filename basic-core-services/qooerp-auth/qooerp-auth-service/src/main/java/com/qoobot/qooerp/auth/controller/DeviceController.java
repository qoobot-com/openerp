package com.qoobot.qooerp.auth.controller;

import com.qoobot.qooerp.auth.entity.AuthDevice;
import com.qoobot.qooerp.auth.service.DeviceService;
import com.qoobot.qooerp.auth.service.TokenService;
import com.qoobot.qooerp.common.response.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 设备管理控制器
 */
@Tag(name = "设备管理", description = "设备管理接口")
@RestController
@RequestMapping("/api/auth/device")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;
    private final TokenService tokenService;

    @Operation(summary = "获取设备列表")
    @GetMapping("/list")
    public Result<List<AuthDevice>> getDeviceList(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        Long userId = tokenService.getUserIdFromToken(token.replace("Bearer ", ""));

        List<AuthDevice> devices = deviceService.getUserDevices(userId);
        return Result.success(devices);
    }

    @Operation(summary = "删除设备")
    @DeleteMapping("/{deviceId}")
    public Result<Void> removeDevice(HttpServletRequest request, @PathVariable Long deviceId) {
        String token = request.getHeader("Authorization");
        Long userId = tokenService.getUserIdFromToken(token.replace("Bearer ", ""));

        boolean result = deviceService.removeDevice(userId, deviceId);
        return result ? Result.success() : Result.fail("删除失败");
    }

    @Operation(summary = "清除所有设备")
    @DeleteMapping("/clear-all")
    public Result<Void> clearAllDevices(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        Long userId = tokenService.getUserIdFromToken(token.replace("Bearer ", ""));

        boolean result = deviceService.clearAllDevices(userId);
        return result ? Result.success() : Result.fail("清除失败");
    }
}
