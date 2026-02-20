package com.qoobot.qooerp.report.service;

import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface ReportExportService {
    
    ResponseEntity<byte[]> exportToExcel(Long reportId, Map<String, Object> params);
    
    ResponseEntity<byte[]> exportToPdf(Long reportId, Map<String, Object> params);
    
    ResponseEntity<byte[]> exportToCsv(Long reportId, Map<String, Object> params);
    
    ResponseEntity<byte[]> exportToImage(Long reportId, Map<String, Object> params);
}
