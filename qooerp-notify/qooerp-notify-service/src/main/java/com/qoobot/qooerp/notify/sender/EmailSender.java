package com.qoobot.qooerp.notify.sender;

import com.qoobot.qooerp.notify.enums.NotifyStatusEnum;
import com.qoobot.qooerp.notify.enums.NotifyTypeEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import jakarta.mail.internet.MimeMessage;

/**
 * 邮件发送器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EmailSender {

    @Autowired
    private JavaMailSender mailSender;

    /**
     * 发送简单文本邮件
     */
    public boolean sendText(String to, String subject, String content) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);
            mailSender.send(message);
            log.info("邮件发送成功: to={}, subject={}", to, subject);
            return true;
        } catch (Exception e) {
            log.error("邮件发送失败: to={}, subject={}", to, subject, e);
            return false;
        }
    }

    /**
     * 发送HTML邮件
     */
    public boolean sendHtml(String to, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            mailSender.send(message);
            log.info("HTML邮件发送成功: to={}, subject={}", to, subject);
            return true;
        } catch (Exception e) {
            log.error("HTML邮件发送失败: to={}, subject={}", to, subject, e);
            return false;
        }
    }

    /**
     * 发送邮件（根据内容类型自动判断）
     */
    public boolean send(String to, String subject, String content) {
        if (content != null && content.contains("<")) {
            return sendHtml(to, subject, content);
        }
        return sendText(to, subject, content);
    }
}
