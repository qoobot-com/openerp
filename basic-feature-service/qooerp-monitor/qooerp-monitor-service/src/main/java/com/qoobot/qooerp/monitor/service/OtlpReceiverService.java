package com.qoobot.qooerp.monitor.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * OTLP 接收服务接口
 * 负责 OpenTelemetry 协议数据的接收与解析
 */
public interface OtlpReceiverService {

    /**
     * 接收链路追踪数据（OTLP Traces）
     *
     * @param data OTLP Protobuf 格式的二进制数据
     * @param contentType 内容类型（application/x-protobuf 或 application/json）
     */
    void receiveTraces(byte[] data, String contentType);

    /**
     * 接收指标数据（OTLP Metrics）
     *
     * @param data OTLP Protobuf 格式的二进制数据
     * @param contentType 内容类型（application/x-protobuf 或 application/json）
     */
    void receiveMetrics(byte[] data, String contentType);

    /**
     * 接收日志数据（OTLP Logs）
     *
     * @param data OTLP Protobuf 格式的二进制数据
     * @param contentType 内容类型（application/x-protobuf 或 application/json）
     */
    void receiveLogs(byte[] data, String contentType);

    /**
     * 批量接收 Traces 数据
     *
     * @param dataList Traces 数据列表
     */
    void batchReceiveTraces(byte[][] dataList, String contentType);

    /**
     * 批量接收 Metrics 数据
     *
     * @param dataList Metrics 数据列表
     */
    void batchReceiveMetrics(byte[][] dataList, String contentType);

    /**
     * 批量接收 Logs 数据
     *
     * @param dataList Logs 数据列表
     */
    void batchReceiveLogs(byte[][] dataList, String contentType);
}
