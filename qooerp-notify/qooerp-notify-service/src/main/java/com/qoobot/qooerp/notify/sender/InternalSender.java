package com.qoobot.qooerp.notify.sender;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 站内通知发送器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InternalSender {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String INTERNAL_NOTIFY_KEY = "internal:notify:";
    private static final long EXPIRE_DAYS = 30;

    /**
     * 发送站内通知
     *
     * @param userId 用户ID
     * @param title 标题
     * @param content 内容
     * @return 发送结果
     */
    public boolean send(Long userId, String title, String content) {
        try {
            String key = INTERNAL_NOTIFY_KEY + userId;
            InternalNotify notify = new InternalNotify();
            notify.setId(System.currentTimeMillis());
            notify.setTitle(title);
            notify.setContent(content);
            notify.setRead(false);
            notify.setCreateTime(System.currentTimeMillis());

            redisTemplate.opsForList().leftPush(key, notify);
            redisTemplate.expire(key, EXPIRE_DAYS, TimeUnit.DAYS);

            log.info("站内通知发送成功: userId={}, title={}", userId, title);
            return true;
        } catch (Exception e) {
            log.error("站内通知发送失败: userId={}", userId, e);
            return false;
        }
    }

    /**
     * 站内通知实体
     */
    @lombok.Data
    public static class InternalNotify {
        private Long id;
        private String title;
        private String content;
        private Boolean read;
        private Long createTime;
    }
}
