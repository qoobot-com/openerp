package com.qoobot.qooerp.gateway.config;

import com.alibaba.csp.sentinel.adapter.gateway.common.SentinelGatewayConstants;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiDefinition;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPathPredicateItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPredicateItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.GatewayApiDefinitionManager;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayParamFlowItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager;
import com.alibaba.csp.sentinel.adapter.gateway.sc.SentinelGatewayFilter;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import jakarta.annotation.PostConstruct;
import java.util.*;

/**
 * Sentinel网关配置
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
@Slf4j
@Configuration
public class SentinelConfig {

    /**
     * 配置限流规则
     */
    @PostConstruct
    public void initGatewayRules() {
        Set<GatewayFlowRule> rules = new HashSet<>();

        // 认证服务限流规则
        rules.add(buildFlowRule("qooerp-auth", 100, 200));
        // 用户服务限流规则
        rules.add(buildFlowRule("qooerp-user", 200, 400));
        // 权限服务限流规则
        rules.add(buildFlowRule("qooerp-permission", 150, 300));
        // 组织服务限流规则
        rules.add(buildFlowRule("qooerp-organization", 100, 200));
        // 系统服务限流规则
        rules.add(buildFlowRule("qooerp-system", 150, 300));

        GatewayRuleManager.loadRules(rules);
        log.info("Sentinel网关限流规则加载完成，共{}条规则", rules.size());
    }

    /**
     * 配置API分组
     */
    @PostConstruct
    public void initApiDefinitions() {
        Set<ApiDefinition> definitions = new HashSet<>();

        // 认证API分组
        definitions.add(buildApiDefinition("auth_api",
            Collections.singletonList("/api/auth/**")));

        // 用户API分组
        definitions.add(buildApiDefinition("user_api",
            Collections.singletonList("/api/user/**")));

        GatewayApiDefinitionManager.loadApiDefinitions(definitions);
        log.info("Sentinel API分组配置完成，共{}个分组", definitions.size());
    }

    /**
     * 配置降级回调
     */
    @PostConstruct
    public void initBlockHandler() {
        GatewayCallbackManager.setBlockHandler(new BlockRequestHandler() {
            @Override
            public Mono<ServerResponse> handleRequest(ServerWebExchange exchange, Throwable ex) {
                Map<String, Object> result = new HashMap<>();

                if (ex instanceof com.alibaba.csp.sentinel.slots.block.flow.FlowException) {
                    result.put("code", 429);
                    result.put("message", "访问过于频繁，请稍后再试");
                } else if (ex instanceof com.alibaba.csp.sentinel.slots.block.degrade.DegradeException) {
                    result.put("code", 503);
                    result.put("message", "服务暂时不可用，请稍后再试");
                } else if (ex instanceof com.alibaba.csp.sentinel.slots.block.authority.AuthorityException) {
                    result.put("code", 403);
                    result.put("message", "无权访问");
                } else {
                    result.put("code", 500);
                    result.put("message", "系统繁忙，请稍后再试");
                }

                result.put("timestamp", System.currentTimeMillis());
                result.put("path", exchange.getRequest().getPath().value());

                return ServerResponse.status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(result));
            }
        });
        log.info("Sentinel降级回调配置完成");
    }

    /**
     * 构建限流规则
     */
    private GatewayFlowRule buildFlowRule(String resource, long count, long burst) {
        return new GatewayFlowRule(resource)
            .setCount(count)
            .setIntervalSec(1)
            .setBurst((int) burst)
            .setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_RATE_LIMITER)
            .setGrade(RuleConstant.FLOW_GRADE_QPS);
    }

    /**
     * 构建API定义
     */
    private ApiDefinition buildApiDefinition(String apiName, List<String> predicates) {
        Set<ApiPredicateItem> predicateItems = new HashSet<>();
        for (String pattern : predicates) {
            ApiPathPredicateItem item = new ApiPathPredicateItem()
                .setPattern(pattern)
                .setMatchStrategy(SentinelGatewayConstants.URL_MATCH_STRATEGY_PREFIX);
            predicateItems.add(item);
        }
        return new ApiDefinition(apiName).setPredicateItems(predicateItems);
    }
}
