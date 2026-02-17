package com.qoobot.qooerp.auth.strategy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 登录策略工厂
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LoginStrategyFactory {

    private final List<LoginStrategy> strategies;
    private Map<String, LoginStrategy> strategyMap;

    /**
     * 获取登录策略
     *
     * @param loginType 登录类型
     * @return 登录策略
     */
    public LoginStrategy getStrategy(String loginType) {
        if (strategyMap == null) {
            strategyMap = strategies.stream()
                    .collect(Collectors.toMap(
                            strategy -> strategy.getClass().getSimpleName(),
                            Function.identity()
                    ));
        }

        return strategies.stream()
                .filter(strategy -> strategy.supports(loginType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("不支持的登录类型: " + loginType));
    }
}
