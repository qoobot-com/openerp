package com.qoobot.qooerp.auth.service;

import com.qoobot.qooerp.auth.dto.CaptchaResponse;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 验证码服务接口
 */
public interface CaptchaService {

    /**
     * 生成图形验证码（字符验证码）
     *
     * @param response HTTP响应
     * @throws IOException IO异常
     */
    void generateCaptcha(HttpServletResponse response) throws IOException;

    /**
     * 生成图形验证码
     *
     * @param type 验证码类型：0-字符验证码 1-滑块验证码
     * @return 验证码响应
     */
    CaptchaResponse generateCaptcha(Integer type);

    /**
     * 验证图形验证码
     *
     * @param key    验证码Key
     * @param code   验证码
     * @return 是否验证成功
     */
    boolean verifyCaptcha(String key, String code);

    /**
     * 验证滑块验证码
     *
     * @param key   验证码Key
     * @param x     滑块X坐标
     * @param y     滑块Y坐标
     * @return 是否验证成功
     */
    boolean verifySliderCaptcha(String key, Integer x, Integer y);

    /**
     * 发送短信验证码
     *
     * @param phone  手机号
     * @return 是否发送成功
     */
    boolean sendSmsCode(String phone);

    /**
     * 验证短信验证码
     *
     * @param phone  手机号
     * @param code   验证码
     * @return 是否验证成功
     */
    boolean verifySmsCode(String phone, String code);

    /**
     * 发送邮箱验证码
     *
     * @param email  邮箱
     * @return 是否发送成功
     */
    boolean sendEmailCode(String email);

    /**
     * 验证邮箱验证码
     *
     * @param email  邮箱
     * @param code   验证码
     * @return 是否验证成功
     */
    boolean verifyEmailCode(String email, String code);
}
