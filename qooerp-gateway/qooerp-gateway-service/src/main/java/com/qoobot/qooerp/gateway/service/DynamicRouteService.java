package com.qoobot.qooerp.gateway.service;

import com.alibaba.cloud.nacos.NacosConfigProperties;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * 动态路由服务 - 从Nacos加载和管理路由配置
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
@Slf4j
@Service
public class DynamicRouteService {

    private final RouteDefinitionWriter routeDefinitionWriter;
    private final RouteDefinitionLocator routeDefinitionLocator;
    private final NacosConfigProperties nacosConfigProperties;

    private static final String DATA_ID = "gateway-routes.yml";
    private static final String GROUP = "GATEWAY_GROUP";

    public DynamicRouteService(RouteDefinitionWriter routeDefinitionWriter,
                               RouteDefinitionLocator routeDefinitionLocator,
                               NacosConfigProperties nacosConfigProperties) {
        this.routeDefinitionWriter = routeDefinitionWriter;
        this.routeDefinitionLocator = routeDefinitionLocator;
        this.nacosConfigProperties = nacosConfigProperties;
    }

    /**
     * 从Nacos加载路由配置
     */
    @PostConstruct
    public void loadRoutesFromNacos() {
        try {
            ConfigService configService = NacosFactory.createConfigService(
                nacosConfigProperties.getServerAddr());

            String content = configService.getConfig(
                DATA_ID,
                GROUP,
                5000
            );

            if (content != null && !content.isEmpty()) {
                List<RouteDefinition> routes = parseRoutes(content);
                routes.forEach(route -> {
                    routeDefinitionWriter.save(Mono.just(route)).subscribe();
                    log.info("加载路由: {} -> {}", route.getId(), route.getUri());
                });
                log.info("从Nacos加载路由配置完成，共{}条路由", routes.size());
            }
        } catch (NacosException e) {
            log.error("从Nacos加载路由配置失败", e);
        }
    }

    /**
     * 监听Nacos配置变化
     */
    @PostConstruct
    public void listenRouteConfigChange() {
        try {
            ConfigService configService = NacosFactory.createConfigService(
                nacosConfigProperties.getServerAddr());

            configService.addListener(
                DATA_ID,
                GROUP,
                new Listener() {
                    @Override
                    public void receiveConfigInfo(String configInfo) {
                        log.info("收到路由配置变更通知");
                        
                        // 清除所有现有路由
                        routeDefinitionLocator.getRouteDefinitions()
                            .flatMap(route -> routeDefinitionWriter.delete(Mono.just(route.getId())))
                            .collectList()
                            .block();

                        // 加载新路由
                        if (configInfo != null && !configInfo.isEmpty()) {
                            List<RouteDefinition> routes = parseRoutes(configInfo);
                            routes.forEach(route -> {
                                routeDefinitionWriter.save(Mono.just(route)).subscribe();
                                log.info("更新路由: {} -> {}", route.getId(), route.getUri());
                            });
                            log.info("路由配置更新完成，共{}条路由", routes.size());
                        }
                    }

                    @Override
                    public Executor getExecutor() {
                        return null;
                    }
                }
            );
            log.info("监听路由配置变化成功");
        } catch (NacosException e) {
            log.error("监听路由配置变化失败", e);
        }
    }

    /**
     * 解析路由配置
     * TODO: 实际实现需要解析YAML格式的路由配置
     */
    private List<RouteDefinition> parseRoutes(String content) {
        // TODO: 解析YAML格式的路由配置
        return Collections.emptyList();
    }
}
