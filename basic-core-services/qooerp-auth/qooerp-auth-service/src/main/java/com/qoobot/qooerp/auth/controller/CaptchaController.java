package com.qoobot.qooerp.auth.controller;

import com.qoobot.qooerp.auth.dto.CaptchaResponse;
import com.qoobot.qooerp.auth.dto.CaptchaVerifyRequest;
import com.qoobot.qooerp.auth.dto.SmsCodeRequest;
import com.qoobot.qooerp.auth.dto.EmailCodeRequest;
import com.qoobot.qooerp.auth.service.CaptchaService;
import com.qoobot.qooerp.common.response.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * 验证码控制器
 * 提供图形验证码、滑块验证码、短信验证码、邮箱验证码的生成和验证功能
 */
@Slf4j
@RestController
@RequestMapping("/api/captcha")
@RequiredArgsConstructor
@Tag(name = "验证码管理", description = "图形验证码、滑块验证码、短信验证码、邮箱验证码")
public class CaptchaController {

    private final CaptchaService captchaService;

    @GetMapping("/image")
    @Operation(
        summary = "生成图形验证码",
        description = "生成字符图形验证码并直接返回图片流，适用于表单提交场景",
        responses = {
            @ApiResponse(responseCode = "200", description = "验证码图片", 
                content = @Content(mediaType = "image/jpeg")),
            @ApiResponse(responseCode = "500", description = "生成失败")
        }
    )
    public void generateCaptcha(
        @Parameter(description = "HTTP响应对象") HttpServletResponse response
    ) throws IOException {
        try {
            log.info("开始生成图形验证码");
            captchaService.generateCaptcha(response);
            log.info("图形验证码生成成功");
        } catch (Exception e) {
            log.error("生成图形验证码失败", e);
            throw e;
        }
    }

    @GetMapping("/generate")
    @Operation(
        summary = "生成验证码",
        description = "生成验证码并返回验证码信息，支持字符验证码和滑块验证码",
        parameters = {
            @Parameter(name = "type", description = "验证码类型：0-字符验证码，1-滑块验证码", 
                      example = "0", schema = @Schema(defaultValue = "0"))
        },
        responses = {
            @ApiResponse(responseCode = "200", description = "生成成功", 
                content = @Content(schema = @Schema(implementation = CaptchaResponse.class))),
            @ApiResponse(responseCode = "500", description = "生成失败")
        }
    )
    public Result<CaptchaResponse> generateCaptcha(
        @RequestParam(required = false, defaultValue = "0") 
        @Parameter(description = "验证码类型：0-字符验证码，1-滑块验证码") Integer type
    ) {
        try {
            log.info("开始生成验证码，类型：{}", type);
            CaptchaResponse response = captchaService.generateCaptcha(type);
            log.info("验证码生成成功，Key：{}", response.getKey());
            return Result.success(response);
        } catch (Exception e) {
            log.error("生成验证码失败，类型：{}", type, e);
            throw new RuntimeException("验证码生成失败：" + e.getMessage(), e);
        }
    }

    @PostMapping("/verify")
    @Operation(
        summary = "验证验证码",
        description = "验证图形验证码或滑块验证码的有效性",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "验证码验证请求",
            content = @Content(schema = @Schema(implementation = CaptchaVerifyRequest.class)),
            required = true
        ),
        responses = {
            @ApiResponse(responseCode = "200", description = "验证结果", 
                content = @Content(schema = @Schema(implementation = Boolean.class))),
            @ApiResponse(responseCode = "400", description = "参数错误"),
            @ApiResponse(responseCode = "500", description = "验证失败")
        }
    )
    public Result<Boolean> verifyCaptcha(
        @Valid @RequestBody 
        @Parameter(description = "验证码验证请求", required = true) CaptchaVerifyRequest request
    ) {
        try {
            log.info("开始验证验证码，Key：{}，类型：{}", request.getKey(), request.getType());
            
            boolean result;
            if (request.getType() != null && request.getType() == 1) {
                // 滑块验证码验证
                if (request.getX() == null || request.getY() == null) {
                    log.warn("滑块验证码坐标参数缺失");
                    throw new IllegalArgumentException("滑块验证码需要提供坐标参数");
                }
                result = captchaService.verifySliderCaptcha(request.getKey(), request.getX(), request.getY());
                log.info("滑块验证码验证{}，坐标：({}, {})", result ? "成功" : "失败", request.getX(), request.getY());
            } else {
                // 字符验证码验证
                if (request.getCode() == null || request.getCode().isEmpty()) {
                    log.warn("字符验证码参数缺失");
                    throw new IllegalArgumentException("字符验证码不能为空");
                }
                result = captchaService.verifyCaptcha(request.getKey(), request.getCode());
                log.info("字符验证码验证{}，验证码：{}", result ? "成功" : "失败", request.getCode());
            }
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("验证验证码失败，Key：{}", request.getKey(), e);
            throw new RuntimeException("验证码验证失败：" + e.getMessage(), e);
        }
    }

    @PostMapping("/sms/send")
    @Operation(
        summary = "发送短信验证码",
        description = "向指定手机号发送短信验证码，用于手机登录验证",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "短信验证码发送请求",
            content = @Content(schema = @Schema(implementation = SmsCodeRequest.class)),
            required = true
        ),
        responses = {
            @ApiResponse(responseCode = "200", description = "发送成功"),
            @ApiResponse(responseCode = "400", description = "参数错误"),
            @ApiResponse(responseCode = "500", description = "发送失败")
        }
    )
    public Result<Void> sendSmsCode(
        @Valid @RequestBody 
        @Parameter(description = "短信验证码发送请求", required = true) SmsCodeRequest request
    ) {
        try {
            log.info("开始发送短信验证码到手机号：{}", request.getPhone());
            boolean success = captchaService.sendSmsCode(request.getPhone());
            
            if (success) {
                log.info("短信验证码发送成功，手机号：{}", request.getPhone());
                return Result.success();
            } else {
                log.warn("短信验证码发送失败，手机号：{}", request.getPhone());
                throw new RuntimeException("短信验证码发送失败，请稍后重试");
            }
        } catch (Exception e) {
            log.error("发送短信验证码异常，手机号：{}", request.getPhone(), e);
            throw new RuntimeException("短信验证码发送异常：" + e.getMessage(), e);
        }
    }

    @PostMapping("/email/send")
    @Operation(
        summary = "发送邮箱验证码",
        description = "向指定邮箱发送验证码，用于邮箱登录验证",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "邮箱验证码发送请求",
            content = @Content(schema = @Schema(implementation = EmailCodeRequest.class)),
            required = true
        ),
        responses = {
            @ApiResponse(responseCode = "200", description = "发送成功"),
            @ApiResponse(responseCode = "400", description = "参数错误"),
            @ApiResponse(responseCode = "500", description = "发送失败")
        }
    )
    public Result<Void> sendEmailCode(
        @Valid @RequestBody 
        @Parameter(description = "邮箱验证码发送请求", required = true) EmailCodeRequest request
    ) {
        try {
            log.info("开始发送邮箱验证码到邮箱：{}", request.getEmail());
            boolean success = captchaService.sendEmailCode(request.getEmail());
            
            if (success) {
                log.info("邮箱验证码发送成功，邮箱：{}", request.getEmail());
                return Result.success();
            } else {
                log.warn("邮箱验证码发送失败，邮箱：{}", request.getEmail());
                throw new RuntimeException("邮箱验证码发送失败，请稍后重试");
            }
        } catch (Exception e) {
            log.error("发送邮箱验证码异常，邮箱：{}", request.getEmail(), e);
            throw new RuntimeException("邮箱验证码发送异常：" + e.getMessage(), e);
        }
    }
}
