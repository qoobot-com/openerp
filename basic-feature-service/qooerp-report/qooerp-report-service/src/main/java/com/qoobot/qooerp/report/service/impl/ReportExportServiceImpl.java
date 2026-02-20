package com.qoobot.qooerp.report.service.impl;

import com.qoobot.qooerp.report.service.ReportExportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportExportServiceImpl implements ReportExportService {

    @Override
    public ResponseEntity<byte[]> exportToExcel(Long reportId, Map<String, Object> params) {
        String filename = "report_" + reportId + ".xlsx";
        byte[] data = generateMockExcelData(reportId);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", filename);
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(data);
    }

    @Override
    public ResponseEntity<byte[]> exportToPdf(Long reportId, Map<String, Object> params) {
        String filename = "report_" + reportId + ".pdf";
        byte[] data = generateMockPdfData(reportId);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", filename);
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(data);
    }

    @Override
    public ResponseEntity<byte[]> exportToCsv(Long reportId, Map<String, Object> params) {
        String filename = "report_" + reportId + ".csv";
        byte[] data = generateMockCsvData(reportId);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("text", "csv", StandardCharsets.UTF_8));
        headers.setContentDispositionFormData("attachment", filename);
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(data);
    }

    @Override
    public ResponseEntity<byte[]> exportToImage(Long reportId, Map<String, Object> params) {
        String filename = "report_" + reportId + ".png";
        byte[] data = generateMockImageData(reportId);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        headers.setContentDispositionFormData("attachment", filename);
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(data);
    }

    private byte[] generateMockExcelData(Long reportId) {
        return ("Mock Excel Data for Report " + reportId).getBytes(StandardCharsets.UTF_8);
    }

    private byte[] generateMockPdfData(Long reportId) {
        return ("Mock PDF Data for Report " + reportId).getBytes(StandardCharsets.UTF_8);
    }

    private byte[] generateMockCsvData(Long reportId) {
        return ("id,name,value\n1,Item1,100\n2,Item2,200").getBytes(StandardCharsets.UTF_8);
    }

    private byte[] generateMockImageData(Long reportId) {
        return ("Mock Image Data for Report " + reportId).getBytes(StandardCharsets.UTF_8);
    }
}
