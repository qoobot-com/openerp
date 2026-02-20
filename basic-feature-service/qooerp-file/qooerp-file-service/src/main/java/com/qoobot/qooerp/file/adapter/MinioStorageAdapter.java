package com.qoobot.qooerp.file.adapter;

import io.minio.*;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.InputStream;

/**
 * MinIO存储适配器实现
 *
 * @author QooERP
 * @date 20xx-xx-xx
 */
@Slf4j
@Component
public class MinioStorageAdapter implements StorageAdapter {

    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${minio.access-key}")
    private String accessKey;

    @Value("${minio.secret-key}")
    private String secretKey;

    @Value("${minio.bucket-name:qooerp-files}")
    private String bucketName;

    private MinioClient minioClient;

    @PostConstruct
    public void init() {
        try {
            minioClient = MinioClient.builder()
                    .endpoint(endpoint)
                    .credentials(accessKey, secretKey)
                    .build();
            createBucketIfNotExists();
            log.info("MinIO存储适配器初始化成功");
        } catch (Exception e) {
            log.error("MinIO存储适配器初始化失败", e);
        }
    }

    private void createBucketIfNotExists() {
        try {
            boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(bucketName).build()
            );
            if (!exists) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder().bucket(bucketName).build()
                );
                log.info("创建MinIO桶: {}", bucketName);
            }
        } catch (Exception e) {
            log.error("创建MinIO桶失败", e);
        }
    }

    @Override
    public String upload(String bucketName, String objectName, InputStream inputStream,
                        String contentType, long fileSize) {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(inputStream, fileSize, -1)
                            .contentType(contentType)
                            .build()
            );
            return objectName;
        } catch (Exception e) {
            log.error("MinIO上传失败: {}", objectName, e);
            throw new RuntimeException("文件上传失败", e);
        }
    }

    @Override
    public InputStream download(String objectName) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
        } catch (Exception e) {
            log.error("MinIO下载失败: {}", objectName, e);
            throw new RuntimeException("文件下载失败", e);
        }
    }

    @Override
    public void delete(String objectName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
        } catch (Exception e) {
            log.error("MinIO删除失败: {}", objectName, e);
            throw new RuntimeException("文件删除失败", e);
        }
    }

    @Override
    public String getUrl(String objectName, int expireSeconds) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(objectName)
                            .expiry(expireSeconds)
                            .build()
            );
        } catch (Exception e) {
            log.error("MinIO获取URL失败: {}", objectName, e);
            throw new RuntimeException("获取文件URL失败", e);
        }
    }

    @Override
    public boolean exists(String objectName) {
        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public long getFileSize(String objectName) {
        try {
            StatObjectResponse stat = minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
            return stat.size();
        } catch (Exception e) {
            log.error("MinIO获取文件大小失败: {}", objectName, e);
            return 0;
        }
    }
}
