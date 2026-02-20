package com.qoobot.qooerp.report.service;

import java.util.List;
import java.util.Map;

public interface ReportDataService {
    
    Map<String, Object> queryReportData(Long reportId, Map<String, Object> params);
    
    Map<String, Object> executeQuery(Long datasourceId, String sql, Map<String, Object> params);
    
    Map<String, Object> drillDown(Long reportId, Map<String, Object> params, String drillConfig);
}
