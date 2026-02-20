package com.qoobot.qooerp.auth.controller;

import com.qoobot.qooerp.auth.dto.LoginResponse;
import com.qoobot.qooerp.auth.dto.ThirdPartyLoginRequest;
import com.qoobot.qooerp.auth.service.ThirdPartyLoginService;
import com.qoobot.qooerp.common.response.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 第三方登录控制器
 */
@Tag(name = "第三方登录", description = "第三方登录接口")
@RestController
@RequestMapping("/api/auth/third-party")
@RequiredArgsConstructor
public class ThirdPartyLoginController {

    private final ThirdPartyLoginService thirdPartyLoginService;

    @Operation(summary = "微信登录")
    @PostMapping("/wechat/login")
    public Result<LoginResponse> wechatLogin(@RequestBody Map<String, String> request) {
        String code = request.get("code");
        LoginResponse response = thirdPartyLoginService.wechatLogin(code);
        return Result.success(response);
    }

    @Operation(summary = "钉钉登录")
    @PostMapping("/dingtalk/login")
    public Result<LoginResponse> dingtalkLogin(@RequestBody Map<String, String> request) {
        String code = request.get("code");
        LoginResponse response = thirdPartyLoginService.dingtalkLogin(code);
        return Result.success(response);
    }

    @Operation(summary = "企业微信登录")
    @PostMapping("/wecom/login")
    public Result<LoginResponse> weComLogin(@RequestBody Map<String, String> request) {
        String code = request.get("code");
        LoginResponse response = thirdPartyLoginService.weComLogin(code);
        return Result.success(response);
    }

    @Operation(summary = "绑定第三方账号")
    @PostMapping("/bind")
    public Result<Void> bindThirdParty(@RequestBody Map<String, String> request) {
        Long userId = Long.parseLong(request.get("userId"));
        String platform = request.get("platform");
        String openid = request.get("openid");
        String unionid = request.get("unionid");

        boolean result = thirdPartyLoginService.bindThirdParty(userId, platform, openid, unionid);
        return result ? Result.success() : Result.fail("绑定失败");
    }

    @Operation(summary = "解绑第三方账号")
    @PostMapping("/unbind")
    public Result<Void> unbindThirdParty(@RequestBody Map<String, String> request) {
        Long userId = Long.parseLong(request.get("userId"));
        String platform = request.get("platform");

        boolean result = thirdPartyLoginService.unbindThirdParty(userId, platform);
        return result ? Result.success() : Result.fail("解绑失败");
    }
}
