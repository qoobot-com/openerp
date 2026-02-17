package com.qoobot.qooerp.file.adapter;

import java.io.InputStream;

/**
 * 存储适配器接口
 *
 * @author QooERP
 * @date 2026-02-17
 */
public interface StorageAdapter {

    /**
     * 上传文件
     *
     * @param bucketName 桶名称
     * @param objectName 对象名称
     * @param inputStream 文件流
     * @param contentType 内容类型
     * @param fileSize 文件大小
     * @return 文件路径
     */
    String upload(String bucketName, String objectName, InputStream inputStream, String contentType, long fileSize);

    /**
     * 下载文件
     *
     * @param objectName 对象名称
     * @return 文件流
     */
    InputStream download(String objectName);

    /**
     * 删除文件
     *
     * @param objectName 对象名称
     */
    void delete(String objectName);

    /**
     * 获取文件URL
     *
     * @param objectName 对象名称
     * @param expireSeconds 过期时间（秒）
     * @return 文件URL
     */
    String getUrl(String objectName, int expireSeconds);

    /**
     * 检查文件是否存在
     *
     * @param objectName 对象名称
     * @return 是否存在
     */
    boolean exists(String objectName);

    /**
     * 获取文件大小
     *
     * @param objectName 对象名称
     * @return 文件大小
     */
    long getFileSize(String objectName);
}
