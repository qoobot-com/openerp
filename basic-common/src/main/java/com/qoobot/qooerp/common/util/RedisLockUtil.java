package com.qoobot.qooerp.common.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * Redis分布式锁工具类
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisLockUtil {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final Long SUCCESS = 1L;

    /**
     * 尝试获取锁
     *
     * @param lockKey   锁的key
     * @param lockValue 锁的值（用于释放锁时校验）
     * @param expireTime 过期时间（秒）
     * @return 是否获取成功
     */
    public boolean tryLock(String lockKey, String lockValue, long expireTime) {
        try {
            String luaScript = "if redis.call('setnx', KEYS[1], ARGV[1]) == 1 then " +
                    "redis.call('expire', KEYS[1], ARGV[2]) " +
                    "response 1 else " +
                    "response 0 end";

            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(luaScript, Long.class);
            Long result = redisTemplate.execute(redisScript, Collections.singletonList(lockKey), lockValue, expireTime);
            return SUCCESS.equals(result);
        } catch (Exception e) {
            log.error("获取锁失败: lockKey={}", lockKey, e);
            return false;
        }
    }

    /**
     * 尝试获取锁（带等待时间）
     *
     * @param lockKey    锁的key
     * @param lockValue  锁的值
     * @param expireTime 过期时间（秒）
     * @param waitTime   等待时间（毫秒）
     * @return 是否获取成功
     */
    public boolean tryLock(String lockKey, String lockValue, long expireTime, long waitTime) {
        long startTime = System.currentTimeMillis();
        while (true) {
            if (tryLock(lockKey, lockValue, expireTime)) {
                return true;
            }

            long elapsedTime = System.currentTimeMillis() - startTime;
            if (elapsedTime >= waitTime) {
                log.warn("获取锁超时: lockKey={}, waitTime={}ms", lockKey, waitTime);
                return false;
            }

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
    }

    /**
     * 释放锁
     *
     * @param lockKey   锁的key
     * @param lockValue 锁的值（用于校验）
     * @return 是否释放成功
     */
    public boolean unlock(String lockKey, String lockValue) {
        try {
            String luaScript = "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                    "response redis.call('del', KEYS[1]) " +
                    "else " +
                    "response 0 end";

            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(luaScript, Long.class);
            Long result = redisTemplate.execute(redisScript, Collections.singletonList(lockKey), lockValue);
            return SUCCESS.equals(result);
        } catch (Exception e) {
            log.error("释放锁失败: lockKey={}", lockKey, e);
            return false;
        }
    }

    /**
     * 强制释放锁（不校验值）
     *
     * @param lockKey 锁的key
     */
    public void forceUnlock(String lockKey) {
        try {
            redisTemplate.delete(lockKey);
        } catch (Exception e) {
            log.error("强制释放锁失败: lockKey={}", lockKey, e);
        }
    }

    /**
     * 判断锁是否存在
     *
     * @param lockKey 锁的key
     * @return 是否存在
     */
    public boolean isLocked(String lockKey) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(lockKey));
        } catch (Exception e) {
            log.error("判断锁是否存在失败: lockKey={}", lockKey, e);
            return false;
        }
    }

    /**
     * 续期锁
     *
     * @param lockKey   锁的key
     * @param lockValue 锁的值
     * @param expireTime 新的过期时间（秒）
     * @return 是否续期成功
     */
    public boolean renew(String lockKey, String lockValue, long expireTime) {
        try {
            String luaScript = "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                    "response redis.call('expire', KEYS[1], ARGV[2]) " +
                    "else " +
                    "response 0 end";

            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(luaScript, Long.class);
            Long result = redisTemplate.execute(redisScript, Collections.singletonList(lockKey), lockValue, expireTime);
            return SUCCESS.equals(result);
        } catch (Exception e) {
            log.error("续期锁失败: lockKey={}", lockKey, e);
            return false;
        }
    }
}
