package com.qoobot.qooerp.auth.controller;

import com.qoobot.qooerp.auth.dto.MfaSetupResponse;
import com.qoobot.qooerp.auth.entity.AuthUser;
import com.qoobot.qooerp.auth.service.AuthService;
import com.qoobot.qooerp.auth.service.MfaService;
import com.qoobot.qooerp.auth.service.TokenService;
import com.qoobot.qooerp.common.response.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * MFA控制器
 */
@Tag(name = "MFA管理", description = "多因素认证接口")
@RestController
@RequestMapping("/api/auth/mfa")
@RequiredArgsConstructor
public class MfaController {

    private final MfaService mfaService;
    private final AuthService authService;
    private final TokenService tokenService;

    @Operation(summary = "生成TOTP密钥")
    @GetMapping("/totp/generate")
    public Result<MfaSetupResponse> generateTotpSecret(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        Long userId = tokenService.getUserIdFromToken(token.replace("Bearer ", ""));

        AuthUser user = authService.getCurrentUser(token.replace("Bearer ", "")).toEntity();
        MfaSetupResponse response = mfaService.generateTotpSecret(userId, user.getUsername());

        return Result.success(response);
    }

    @Operation(summary = "启用MFA")
    @PostMapping("/totp/enable")
    public Result<Void> enableTotpMfa(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        Long userId = tokenService.getUserIdFromToken(token.replace("Bearer ", ""));

        boolean result = mfaService.enableTotpMfa(
                userId,
                request.get("secret"),
                request.get("code")
        );

        return result ? Result.success() : Result.fail("验证失败");
    }

    @Operation(summary = "禁用MFA")
    @PostMapping("/disable")
    public Result<Void> disableMfa(HttpServletRequest request, @RequestBody Map<String, String> body) {
        String token = request.getHeader("Authorization");
        Long userId = tokenService.getUserIdFromToken(token.replace("Bearer ", ""));

        boolean result = mfaService.disableMfa(userId, body.get("password"));
        return result ? Result.success() : Result.fail("禁用失败");
    }

    @Operation(summary = "验证TOTP验证码")
    @PostMapping("/totp/verify")
    public Result<Void> verifyTotpCode(HttpServletRequest request, @RequestBody Map<String, String> body) {
        String token = request.getHeader("Authorization");
        Long userId = tokenService.getUserIdFromToken(token.replace("Bearer ", ""));

        boolean result = mfaService.verifyTotpCode(userId, body.get("code"));
        return result ? Result.success() : Result.fail("验证失败");
    }

    @Operation(summary = "检查是否启用MFA")
    @GetMapping("/status")
    public Result<Map<String, Boolean>> checkMfaStatus(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        Long userId = tokenService.getUserIdFromToken(token.replace("Bearer ", ""));

        boolean enabled = mfaService.isMfaEnabled(userId);
        return Result.success(Map.of("enabled", enabled));
    }
}
