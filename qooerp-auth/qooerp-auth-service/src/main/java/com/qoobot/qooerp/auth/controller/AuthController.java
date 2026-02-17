package com.qoobot.qooerp.auth.controller;

import com.qoobot.qooerp.auth.dto.LoginRequest;
import com.qoobot.qooerp.auth.dto.LoginResponse;
import com.qoobot.qooerp.auth.dto.RefreshTokenRequest;
import com.qoobot.qooerp.auth.dto.TokenResponse;
import com.qoobot.qooerp.auth.service.AuthService;
import com.qoobot.qooerp.auth.util.LoginContextUtil;
import com.qoobot.qooerp.common.response.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "认证管理", description = "用户登录、登出、令牌管理")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "支持用户名密码、手机验证码、邮箱验证码登录")
    public Result<LoginResponse> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        // 获取客户端信息
        String ip = LoginContextUtil.getClientIp();
        String userAgent = LoginContextUtil.getUserAgent();
        String browser = LoginContextUtil.parseBrowser(userAgent);
        String os = LoginContextUtil.parseOs(userAgent);
        String device = LoginContextUtil.parseDevice(userAgent);

        // 设置请求信息
        request.setIp(ip);
        request.setBrowser(browser);
        request.setOs(os);

        log.info("用户登录请求: {}, IP: {}, Browser: {}, OS: {}",
                request.getUsername(), ip, browser, os);

        LoginResponse response = authService.login(request);
        return Result.success(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "退出登录，令牌失效")
    public Result<Void> logout(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            authService.logout(token);
        }
        return Result.success();
    }

    @PostMapping("/refresh")
    @Operation(summary = "刷新令牌", description = "使用刷新令牌获取新的访问令牌")
    public Result<TokenResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        TokenResponse response = authService.refreshToken(request);
        return Result.success(response);
    }

    @GetMapping("/validate")
    @Operation(summary = "验证令牌", description = "验证访问令牌是否有效")
    public Result<Boolean> validateToken(@RequestHeader("Authorization") String authorization) {
        String token = authorization.substring(7);
        boolean valid = authService.validateToken(token);
        return Result.success(valid);
    }

    @GetMapping("/current")
    @Operation(summary = "获取当前用户", description = "获取当前登录用户信息")
    public Result<LoginResponse> getCurrentUser(@RequestHeader("Authorization") String authorization) {
        String token = authorization.substring(7);
        LoginResponse response = authService.getCurrentUser(token);
        return Result.success(response);
    }
}
