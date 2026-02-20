package com.qoobot.qooerp.report.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ReportWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/report/subscribe")
    @SendTo("/topic/report/data")
    public Map<String, Object> handleSubscription(Map<String, Object> message) {
        log.info("收到订阅消息: {}", message);
        
        Map<String, Object> response = new HashMap<>();
        response.put("type", "subscription_ack");
        response.put("reportId", message.get("reportId"));
        response.put("status", "subscribed");
        response.put("timestamp", System.currentTimeMillis());
        
        return response;
    }

    public void pushReportData(Long reportId, Object data) {
        messagingTemplate.convertAndSend("/topic/report/" + reportId, data);
    }

    public void pushToUser(String userId, Object data) {
        messagingTemplate.convertAndSendToUser(userId, "/queue/report", data);
    }

    public void broadcast(String destination, Object data) {
        messagingTemplate.convertAndSend("/topic/" + destination, data);
    }
}
