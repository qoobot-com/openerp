package com.qoobot.qooerp.monitor.controller;

import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.monitor.service.OtlpReceiverService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.ByteBuffer;

/**
 * OTLP 接收控制器
 * 接收 OpenTelemetry 协议的数据（Traces/Metrics/Logs）
 */
@Slf4j
@RestController
@RequestMapping("/monitor/api/otlp")
@RequiredArgsConstructor
@Tag(name = "OTLP 接收", description = "OpenTelemetry 协议数据接收接口")
public class OtlpReceiverController {

    private final OtlpReceiverService otlpReceiverService;

    /**
     * 接收链路追踪数据（OTLP Traces）
     * 
     * 支持 HTTP/gRPC 协议
     * Content-Type: application/x-protobuf 或 application/json
     */
    @Operation(summary = "接收 Traces 数据")
    @PostMapping(value = "/v1/traces", consumes = {MediaType.APPLICATION_OCTET_STREAM_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Void> receiveTraces(
            @RequestHeader HttpHeaders headers,
            @RequestBody byte[] data) {
        
        String contentType = headers.getContentType() != null 
            ? headers.getContentType().toString() 
            : MediaType.APPLICATION_OCTET_STREAM_VALUE;
        
        otlpReceiverService.receiveTraces(data, contentType);
        
        return ResponseEntity.ok().build();
    }

    /**
     * 接收指标数据（OTLP Metrics）
     */
    @Operation(summary = "接收 Metrics 数据")
    @PostMapping(value = "/v1/metrics", consumes = {MediaType.APPLICATION_OCTET_STREAM_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Void> receiveMetrics(
            @RequestHeader HttpHeaders headers,
            @RequestBody byte[] data) {
        
        String contentType = headers.getContentType() != null 
            ? headers.getContentType().toString() 
            : MediaType.APPLICATION_OCTET_STREAM_VALUE;
        
        otlpReceiverService.receiveMetrics(data, contentType);
        
        return ResponseEntity.ok().build();
    }

    /**
     * 接收日志数据（OTLP Logs）
     */
    @Operation(summary = "接收 Logs 数据")
    @PostMapping(value = "/v1/logs", consumes = {MediaType.APPLICATION_OCTET_STREAM_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Void> receiveLogs(
            @RequestHeader HttpHeaders headers,
            @RequestBody byte[] data) {
        
        String contentType = headers.getContentType() != null 
            ? headers.getContentType().toString() 
            : MediaType.APPLICATION_OCTET_STREAM_VALUE;
        
        otlpReceiverService.receiveLogs(data, contentType);
        
        return ResponseEntity.ok().build();
    }

    /**
     * 批量接收 Traces 数据
     */
    @Operation(summary = "批量接收 Traces 数据")
    @PostMapping(value = "/v1/traces/batch", consumes = {MediaType.APPLICATION_OCTET_STREAM_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Result<Void> batchReceiveTraces(
            @RequestHeader HttpHeaders headers,
            @RequestBody byte[][] dataList) {
        
        String contentType = headers.getContentType() != null 
            ? headers.getContentType().toString() 
            : MediaType.APPLICATION_OCTET_STREAM_VALUE;
        
        otlpReceiverService.batchReceiveTraces(dataList, contentType);
        
        return Result.success();
    }

    /**
     * 批量接收 Metrics 数据
     */
    @Operation(summary = "批量接收 Metrics 数据")
    @PostMapping(value = "/v1/metrics/batch", consumes = {MediaType.APPLICATION_OCTET_STREAM_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Result<Void> batchReceiveMetrics(
            @RequestHeader HttpHeaders headers,
            @RequestBody byte[][] dataList) {
        
        String contentType = headers.getContentType() != null 
            ? headers.getContentType().toString() 
            : MediaType.APPLICATION_OCTET_STREAM_VALUE;
        
        otlpReceiverService.batchReceiveMetrics(dataList, contentType);
        
        return Result.success();
    }

    /**
     * 批量接收 Logs 数据
     */
    @Operation(summary = "批量接收 Logs 数据")
    @PostMapping(value = "/v1/logs/batch", consumes = {MediaType.APPLICATION_OCTET_STREAM_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Result<Void> batchReceiveLogs(
            @RequestHeader HttpHeaders headers,
            @RequestBody byte[][] dataList) {
        
        String contentType = headers.getContentType() != null 
            ? headers.getContentType().toString() 
            : MediaType.APPLICATION_OCTET_STREAM_VALUE;
        
        otlpReceiverService.batchReceiveLogs(dataList, contentType);
        
        return Result.success();
    }

    /**
     * 健康检查
     */
    @Operation(summary = "OTLP 接收器健康检查")
    @GetMapping("/health")
    public Result<String> health() {
        return Result.success("OTLP Receiver is running");
    }
}
