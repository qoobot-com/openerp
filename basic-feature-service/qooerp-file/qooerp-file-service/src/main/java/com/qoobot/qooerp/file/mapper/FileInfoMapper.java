package com.qoobot.qooerp.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qoobot.qooerp.file.entity.FileInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 文件信息Mapper
 *
 * @author QooERP
 * @date 20xx-xx-xx
 */
@Mapper
public interface FileInfoMapper extends BaseMapper<FileInfo> {

    /**
     * 根据MD5查询文件
     *
     * @param fileMd5 文件MD5
     * @param tenantId 租户ID
     * @return 文件信息
     */
    FileInfo selectByMd5(@Param("fileMd5") String fileMd5, @Param("tenantId") Long tenantId);

    /**
     * 根据文件夹ID查询文件列表
     *
     * @param folderId 文件夹ID
     * @param tenantId 租户ID
     * @return 文件列表
     */
    List<FileInfo> selectByFolderId(@Param("folderId") Long folderId, @Param("tenantId") Long tenantId);

    /**
     * 统计文件类型分布
     *
     * @param tenantId 租户ID
     * @return 类型统计
     */
    List<Map<String, Object>> countByFileType(@Param("tenantId") Long tenantId);

    /**
     * 统计今日上传数量
     *
     * @param tenantId 租户ID
     * @return 上传数量
     */
    Long countTodayUploads(@Param("tenantId") Long tenantId);

    /**
     * 统计今日下载数量
     *
     * @param tenantId 租户ID
     * @return 下载数量
     */
    Long countTodayDownloads(@Param("tenantId") Long tenantId);
}
