package com.qoobot.qooerp.gateway.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 网关管理控制器
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
@Slf4j
@RestController
@RequestMapping("/gateway")
public class GatewayController {

    private final RouteDefinitionLocator routeDefinitionLocator;

    public GatewayController(RouteDefinitionLocator routeDefinitionLocator) {
        this.routeDefinitionLocator = routeDefinitionLocator;
    }

    /**
     * 查询所有路由
     */
    @GetMapping("/routes")
    public Mono<ResponseEntity<Map<String, Object>>> getRoutes() {
        return routeDefinitionLocator.getRouteDefinitions()
            .collectList()
            .map(routes -> {
                Map<String, Object> result = new HashMap<>();
                result.put("code", 200);
                result.put("message", "success");
                result.put("data", routes);
                result.put("total", routes.size());
                return ResponseEntity.ok(result);
            });
    }

    /**
     * 网关健康检查
     */
    @GetMapping("/health")
    public Mono<ResponseEntity<Map<String, Object>>> health() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "UP");
        result.put("timestamp", System.currentTimeMillis());
        return Mono.just(ResponseEntity.ok(result));
    }
}
