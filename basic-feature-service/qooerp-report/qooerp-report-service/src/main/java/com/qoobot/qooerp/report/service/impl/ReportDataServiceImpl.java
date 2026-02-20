package com.qoobot.qooerp.report.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.report.entity.ReportDatasource;
import com.qoobot.qooerp.report.service.ReportDataService;
import com.qoobot.qooerp.report.service.ReportDatasourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportDataServiceImpl implements ReportDataService {

    private final ReportDatasourceService datasourceService;
    private final Map<String, DataSource> dataSourceMap = new HashMap<>();

    @Override
    public Map<String, Object> queryReportData(Long reportId, Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", List.of());
        result.put("total", 0);
        
        return result;
    }

    @Override
    public Map<String, Object> executeQuery(Long datasourceId, String sql, Map<String, Object> params) {
        // TODO: 需要实现获取数据源的方法
        // ReportDatasource datasource = datasourceService.getDatasourceById(datasourceId);
        if (datasourceId == null) {
            throw new BusinessException("数据源ID不能为空");
        }

        String actualSql = replaceParams(sql, params);

        Map<String, Object> result = new HashMap<>();
        try {
            List<Map<String, Object>> data = executeSql(null, actualSql);
            result.put("success", true);
            result.put("data", data);
            result.put("total", data.size());
        } catch (Exception e) {
            log.error("执行SQL失败", e);
            result.put("success", false);
            result.put("message", e.getMessage());
        }

        return result;
    }

    @Override
    public Map<String, Object> drillDown(Long reportId, Map<String, Object> params, String drillConfig) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", List.of());
        
        return result;
    }

    private String replaceParams(String sql, Map<String, Object> params) {
        if (params == null || params.isEmpty()) {
            return sql;
        }
        
        String result = sql;
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String placeholder = "${" + entry.getKey() + "}";
            String value = entry.getValue() != null ? entry.getValue().toString() : "";
            result = result.replace(placeholder, value);
        }
        return result;
    }

    private List<Map<String, Object>> executeSql(ReportDatasource datasource, String sql) {
        DataSource dataSource = getDataSource(datasource);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        
        if (sql.trim().toUpperCase().startsWith("SELECT")) {
            return jdbcTemplate.queryForList(sql);
        } else {
            jdbcTemplate.execute(sql);
            return List.of();
        }
    }

    private DataSource getDataSource(ReportDatasource datasource) {
        String key = "default";
        return dataSourceMap.getOrDefault(key, createMockDataSource());
    }

    private DataSource createMockDataSource() {
        return new javax.sql.DataSource() {
            @Override
            public java.sql.Connection getConnection() {
                return null;
            }
            @Override
            public java.sql.Connection getConnection(String username, String password) {
                return null;
            }
            @Override
            public java.io.PrintWriter getLogWriter() { return null; }
            @Override
            public void setLogWriter(java.io.PrintWriter out) {}
            @Override
            public void setLoginTimeout(int seconds) {}
            @Override
            public int getLoginTimeout() { return 0; }
            @Override
            public java.util.logging.Logger getParentLogger() { return null; }
            @Override
            public <T> T unwrap(Class<T> iface) { return null; }
            @Override
            public boolean isWrapperFor(Class<?> iface) { return false; }
        };
    }
}
