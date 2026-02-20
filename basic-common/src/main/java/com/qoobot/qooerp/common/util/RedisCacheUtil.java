package com.qoobot.qooerp.common.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis缓存工具类
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisCacheUtil {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 设置缓存
     *
     * @param key   键
     * @param value 值
     */
    public void set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
        } catch (Exception e) {
            log.error("设置缓存失败: key={}", key, e);
        }
    }

    /**
     * 设置缓存（带过期时间）
     *
     * @param key     键
     * @param value   值
     * @param timeout 过期时间
     * @param unit    时间单位
     */
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        try {
            redisTemplate.opsForValue().set(key, value, timeout, unit);
        } catch (Exception e) {
            log.error("设置缓存失败: key={}", key, e);
        }
    }

    /**
     * 设置缓存（默认过期时间：秒）
     *
     * @param key     键
     * @param value   值
     * @param timeout 过期时间（秒）
     */
    public void set(String key, Object value, long timeout) {
        set(key, value, timeout, TimeUnit.SECONDS);
    }

    /**
     * 获取缓存
     *
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error("获取缓存失败: key={}", key, e);
            return null;
        }
    }

    /**
     * 获取缓存（指定类型）
     *
     * @param key   键
     * @param clazz 类型
     * @param <T>   泛型
     * @return 值
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> clazz) {
        try {
            Object value = get(key);
            return value != null ? (T) value : null;
        } catch (Exception e) {
            log.error("获取缓存失败: key={}, class={}", key, clazz.getName(), e);
            return null;
        }
    }

    /**
     * 删除缓存
     *
     * @param key 键
     */
    public void delete(String key) {
        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
            log.error("删除缓存失败: key={}", key, e);
        }
    }

    /**
     * 批量删除缓存
     *
     * @param keys 键集合
     */
    public void delete(Collection<String> keys) {
        try {
            redisTemplate.delete(keys);
        } catch (Exception e) {
            log.error("批量删除缓存失败: keys={}", keys, e);
        }
    }

    /**
     * 判断缓存是否存在
     *
     * @param key 键
     * @return 是否存在
     */
    public boolean exists(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error("判断缓存是否存在失败: key={}", key, e);
            return false;
        }
    }

    /**
     * 设置过期时间
     *
     * @param key     键
     * @param timeout 过期时间
     * @param unit    时间单位
     */
    public void expire(String key, long timeout, TimeUnit unit) {
        try {
            redisTemplate.expire(key, timeout, unit);
        } catch (Exception e) {
            log.error("设置过期时间失败: key={}", key, e);
        }
    }

    /**
     * 获取过期时间
     *
     * @param key 键
     * @return 过期时间（秒）
     */
    public long getExpire(String key) {
        try {
            return redisTemplate.getExpire(key, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("获取过期时间失败: key={}", key, e);
            return -1L;
        }
    }

    /**
     * Hash设置
     *
     * @param key   键
     * @param field 字段
     * @param value 值
     */
    public void hSet(String key, String field, Object value) {
        try {
            redisTemplate.opsForHash().put(key, field, value);
        } catch (Exception e) {
            log.error("Hash设置失败: key={}, field={}", key, field, e);
        }
    }

    /**
     * Hash获取
     *
     * @param key   键
     * @param field 字段
     * @return 值
     */
    public Object hGet(String key, String field) {
        try {
            return redisTemplate.opsForHash().get(key, field);
        } catch (Exception e) {
            log.error("Hash获取失败: key={}, field={}", key, field, e);
            return null;
        }
    }

    /**
     * Hash获取所有
     *
     * @param key 键
     * @return 所有值
     */
    public Map<Object, Object> hGetAll(String key) {
        try {
            return redisTemplate.opsForHash().entries(key);
        } catch (Exception e) {
            log.error("Hash获取所有失败: key={}", key, e);
            return Map.of();
        }
    }

    /**
     * Hash删除
     *
     * @param key    键
     * @param fields 字段集合
     */
    public void hDelete(String key, Object... fields) {
        try {
            redisTemplate.opsForHash().delete(key, fields);
        } catch (Exception e) {
            log.error("Hash删除失败: key={}, fields={}", key, fields, e);
        }
    }

    /**
     * List设置（左侧）
     *
     * @param key   键
     * @param value 值
     */
    public void lLeftPush(String key, Object value) {
        try {
            redisTemplate.opsForList().leftPush(key, value);
        } catch (Exception e) {
            log.error("List左侧添加失败: key={}", key, e);
        }
    }

    /**
     * List设置（右侧）
     *
     * @param key   键
     * @param value 值
     */
    public void lRightPush(String key, Object value) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
        } catch (Exception e) {
            log.error("List右侧添加失败: key={}", key, e);
        }
    }

    /**
     * List获取范围
     *
     * @param key   键
     * @param start 开始
     * @param end   结束
     * @return 列表
     */
    public List<Object> lRange(String key, long start, long end) {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            log.error("List获取范围失败: key={}", key, e);
            return List.of();
        }
    }

    /**
     * List获取长度
     *
     * @param key 键
     * @return 长度
     */
    public long lSize(String key) {
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            log.error("List获取长度失败: key={}", key, e);
            return 0L;
        }
    }

    /**
     * Set添加
     *
     * @param key   键
     * @param value 值
     */
    public void sAdd(String key, Object value) {
        try {
            redisTemplate.opsForSet().add(key, value);
        } catch (Exception e) {
            log.error("Set添加失败: key={}", key, e);
        }
    }

    /**
     * Set获取所有
     *
     * @param key 键
     * @return 集合
     */
    public Set<Object> sMembers(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            log.error("Set获取所有失败: key={}", key, e);
            return Set.of();
        }
    }

    /**
     * Set删除
     *
     * @param key   键
     * @param value 值
     */
    public void sRemove(String key, Object value) {
        try {
            redisTemplate.opsForSet().remove(key, value);
        } catch (Exception e) {
            log.error("Set删除失败: key={}", key, e);
        }
    }

    /**
     * ZSet添加
     *
     * @param key   键
     * @param value 值
     * @param score 分数
     */
    public void zAdd(String key, Object value, double score) {
        try {
            redisTemplate.opsForZSet().add(key, value, score);
        } catch (Exception e) {
            log.error("ZSet添加失败: key={}", key, e);
        }
    }

    /**
     * ZSet获取范围
     *
     * @param key   键
     * @param start 开始
     * @param end   结束
     * @return 集合
     */
    public Set<Object> zRange(String key, long start, long end) {
        try {
            return redisTemplate.opsForZSet().range(key, start, end);
        } catch (Exception e) {
            log.error("ZSet获取范围失败: key={}", key, e);
            return Set.of();
        }
    }

    /**
     * ZSet获取排名
     *
     * @param key   键
     * @param value 值
     * @return 排名
     */
    public long zRank(String key, Object value) {
        try {
            Long rank = redisTemplate.opsForZSet().rank(key, value);
            return rank != null ? rank : -1L;
        } catch (Exception e) {
            log.error("ZSet获取排名失败: key={}", key, e);
            return -1L;
        }
    }

    /**
     * ZSet删除
     *
     * @param key   键
     * @param value 值
     */
    public void zRemove(String key, Object value) {
        try {
            redisTemplate.opsForZSet().remove(key, value);
        } catch (Exception e) {
            log.error("ZSet删除失败: key={}", key, e);
        }
    }
}
