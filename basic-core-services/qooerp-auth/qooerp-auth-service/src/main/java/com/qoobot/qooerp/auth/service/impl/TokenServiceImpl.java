package com.qoobot.qooerp.auth.service.impl;

import com.qoobot.qooerp.auth.constants.AuthConstant;
import com.qoobot.qooerp.auth.constants.TokenConstant;
import com.qoobot.qooerp.auth.dto.TokenResponse;
import com.qoobot.qooerp.auth.entity.AuthUser;
import com.qoobot.qooerp.auth.service.TokenService;
import com.qoobot.qooerp.common.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 令牌服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${auth.token.access-expire:7200}")
    private Long accessExpire;

    @Value("${auth.token.refresh-expire:604800}")
    private Long refreshExpire;

    @Override
    public String generateAccessToken(AuthUser user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(TokenConstant.CLAIM_USER_ID, user.getId());
        claims.put(TokenConstant.CLAIM_USERNAME, user.getUsername());
        claims.put(TokenConstant.CLAIM_TYPE, TokenConstant.TOKEN_TYPE_ACCESS);
        return JwtUtil.generateToken(user.getUsername(), claims);
    }

    @Override
    public String generateRefreshToken(Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(TokenConstant.CLAIM_USER_ID, userId);
        claims.put(TokenConstant.CLAIM_TYPE, TokenConstant.TOKEN_TYPE_REFRESH);
        return JwtUtil.generateToken(userId.toString(), claims);
    }

    @Override
    public TokenResponse generateTokenPair(AuthUser user) {
        String accessToken = generateAccessToken(user);
        String refreshToken = generateRefreshToken(user.getId());

        // 保存刷新令牌
        redisTemplate.opsForValue().set(
                AuthConstant.REFRESH_TOKEN_KEY + user.getId(),
                refreshToken,
                refreshExpire,
                TimeUnit.SECONDS
        );

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType(TokenConstant.BEARER_PREFIX.trim())
                .expiresIn(accessExpire)
                .build();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            // 检查是否在黑名单中
            if (isTokenBlacklisted(token)) {
                return false;
            }
            return JwtUtil.validateToken(token);
        } catch (Exception e) {
            log.error("验证令牌失败", e);
            return false;
        }
    }

    @Override
    public boolean isTokenBlacklisted(String token) {
        String key = AuthConstant.TOKEN_BLACKLIST_KEY + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    @Override
    public void addToBlacklist(String token) {
        try {
            String key = AuthConstant.TOKEN_BLACKLIST_KEY + token;
            Long ttl = getExpiresIn(token);
            if (ttl != null && ttl > 0) {
                redisTemplate.opsForValue().set(key, "1", ttl, TimeUnit.SECONDS);
            } else {
                redisTemplate.opsForValue().set(key, "1", 2, TimeUnit.HOURS);
            }
            log.info("令牌已加入黑名单");
        } catch (Exception e) {
            log.error("令牌加入黑名单失败", e);
        }
    }

    @Override
    public Long getExpiresIn(String token) {
        try {
            return JwtUtil.getExpiration(token).getTime() - System.currentTimeMillis();
        } catch (Exception e) {
            log.error("获取令牌过期时间失败", e);
            return 0L;
        }
    }

    @Override
    public Long getUserIdFromToken(String token) {
        try {
            return JwtUtil.getUserId(token);
        } catch (Exception e) {
            log.error("从令牌获取用户ID失败", e);
            return null;
        }
    }

    @Override
    public String getUsernameFromToken(String token) {
        try {
            return JwtUtil.getUsername(token);
        } catch (Exception e) {
            log.error("从令牌获取用户名失败", e);
            return null;
        }
    }

    @Override
    public boolean isTokenExpired(String token) {
        try {
            return JwtUtil.getExpiration(token).before(new java.util.Date());
        } catch (Exception e) {
            log.error("检查令牌是否过期失败", e);
            return true;
        }
    }
}
