package com.qoobot.qooerp.notify.sender;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 短信发送器
 * 需要集成阿里云短信等服务
 */
@Slf4j
@Component
public class SmsSender {

    /**
     * 发送短信
     *
     * @param phone 手机号
     * @param content 短信内容
     * @return 发送结果
     */
    public boolean send(String phone, String content) {
        try {
            // TODO: 集成阿里云短信服务
            // 参考阿里云短信SDK文档进行集成
            log.info("短信发送: phone={}, content={}", phone, content);

            // 模拟发送成功
            return true;
        } catch (Exception e) {
            log.error("短信发送失败: phone={}", phone, e);
            return false;
        }
    }

    /**
     * 发送验证码短信
     *
     * @param phone 手机号
     * @param code 验证码
     * @param minutes 有效期（分钟）
     * @return 发送结果
     */
    public boolean sendVerifyCode(String phone, String code, int minutes) {
        String content = String.format("您的验证码是: %s，%d分钟内有效。", code, minutes);
        return send(phone, content);
    }
}
