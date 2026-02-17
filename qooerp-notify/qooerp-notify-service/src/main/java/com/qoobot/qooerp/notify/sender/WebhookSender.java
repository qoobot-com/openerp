package com.qoobot.qooerp.notify.sender;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Webhook发送器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebhookSender {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private static final int TIMEOUT_SECONDS = 10;
    private static final int MAX_RETRY_TIMES = 3;

    /**
     * 发送Webhook通知
     *
     * @param url Webhook URL
     * @param data 发送数据
     * @return 发送结果
     */
    public boolean send(String url, Map<String, Object> data) {
        return sendWithRetry(url, data, MAX_RETRY_TIMES);
    }

    /**
     * 发送Webhook通知（带重试）
     *
     * @param url Webhook URL
     * @param data 发送数据
     * @param retryTimes 重试次数
     * @return 发送结果
     */
    private boolean sendWithRetry(String url, Map<String, Object> data, int retryTimes) {
        for (int i = 0; i < retryTimes; i++) {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<String> entity = new HttpEntity<>(
                        objectMapper.writeValueAsString(data),
                        headers
                );

                ResponseEntity<String> response = restTemplate.postForEntity(
                        url,
                        entity,
                        String.class
                );

                if (response.getStatusCode().is2xxSuccessful()) {
                    log.info("Webhook发送成功: url={}", url);
                    return true;
                } else {
                    log.warn("Webhook返回非2xx状态: url={}, status={}", url, response.getStatusCode());
                }
            } catch (Exception e) {
                log.error("Webhook发送失败: url={}, retry={}/{}", url, i + 1, retryTimes, e);
            }

            // 指数退避
            if (i < retryTimes - 1) {
                try {
                    Thread.sleep((long) Math.pow(2, i) * 1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return false;
                }
            }
        }

        return false;
    }
}
