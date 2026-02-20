package com.qoobot.qooerp.common.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * JdbcTemplate工具类
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JdbcTemplateUtil {

    private final JdbcTemplate jdbcTemplate;

    /**
     * 查询单个对象
     */
    public <T> T queryForObject(String sql, RowMapper<T> rowMapper, Object... args) {
        try {
            return jdbcTemplate.queryForObject(sql, rowMapper, args);
        } catch (Exception e) {
            log.error("查询对象失败: sql={}, args={}", sql, args, e);
            return null;
        }
    }

    /**
     * 查询对象列表
     */
    public <T> List<T> queryForList(String sql, RowMapper<T> rowMapper, Object... args) {
        try {
            return jdbcTemplate.query(sql, rowMapper, args);
        } catch (Exception e) {
            log.error("查询列表失败: sql={}, args={}", sql, args, e);
            return List.of();
        }
    }

    /**
     * 查询Map列表
     */
    public List<Map<String, Object>> queryForMapList(String sql, Object... args) {
        try {
            return jdbcTemplate.queryForList(sql, args);
        } catch (Exception e) {
            log.error("查询Map列表失败: sql={}, args={}", sql, args, e);
            return List.of();
        }
    }

    /**
     * 查询单个Map
     */
    public Map<String, Object> queryForSingleMap(String sql, Object... args) {
        try {
            return jdbcTemplate.queryForMap(sql, args);
        } catch (Exception e) {
            log.error("查询单个Map失败: sql={}, args={}", sql, args, e);
            return Map.of();
        }
    }

    /**
     * 查询数量
     */
    public Long queryForCount(String sql, Object... args) {
        try {
            return jdbcTemplate.queryForObject(sql, Long.class, args);
        } catch (Exception e) {
            log.error("查询数量失败: sql={}, args={}", sql, args, e);
            return 0L;
        }
    }

    /**
     * 判断是否存在
     */
    public boolean exists(String sql, Object... args) {
        return queryForCount(sql, args) > 0;
    }

    /**
     * 执行更新
     */
    public int update(String sql, Object... args) {
        try {
            return jdbcTemplate.update(sql, args);
        } catch (Exception e) {
            log.error("执行更新失败: sql={}, args={}", sql, args, e);
            return 0;
        }
    }

    /**
     * 批量更新
     */
    public int[] batchUpdate(String sql, List<Object[]> batchArgs) {
        try {
            return jdbcTemplate.batchUpdate(sql, batchArgs);
        } catch (Exception e) {
            log.error("批量更新失败: sql={}", sql, e);
            return new int[0];
        }
    }

    /**
     * 执行插入并返回主键
     */
    public Long insertAndReturnKey(String sql, Object... args) {
        try {
            jdbcTemplate.update(sql, args);
            // 查询最后插入的ID
            return jdbcTemplate.queryForObject("SELECT lastval()", Long.class);
        } catch (Exception e) {
            log.error("插入并返回主键失败: sql={}, args={}", sql, args, e);
            return null;
        }
    }

    /**
     * 执行SQL（无返回值）
     */
    public void execute(String sql) {
        try {
            jdbcTemplate.execute(sql);
        } catch (Exception e) {
            log.error("执行SQL失败: sql={}", sql, e);
            throw new RuntimeException("执行SQL失败", e);
        }
    }
}
