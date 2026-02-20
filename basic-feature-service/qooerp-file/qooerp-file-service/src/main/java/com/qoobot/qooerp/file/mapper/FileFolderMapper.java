package com.qoobot.qooerp.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qoobot.qooerp.file.entity.FileFolder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文件夹Mapper
 *
 * @author QooERP
 * @date 2026-02-17
 */
@Mapper
public interface FileFolderMapper extends BaseMapper<FileFolder> {

    /**
     * 查询子文件夹
     *
     * @param parentId 父文件夹ID
     * @param tenantId 租户ID
     * @return 子文件夹列表
     */
    List<FileFolder> selectByParentId(@Param("parentId") Long parentId, @Param("tenantId") Long tenantId);

    /**
     * 查询文件夹树
     *
     * @param tenantId 租户ID
     * @return 文件夹树
     */
    List<FileFolder> selectFolderTree(@Param("tenantId") Long tenantId);

    /**
     * 检查文件夹名称是否存在
     *
     * @param parentId 父文件夹ID
     * @param folderName 文件夹名称
     * @param tenantId 租户ID
     * @param excludeId 排除的ID（用于更新时检查）
     * @return 数量
     */
    Long checkNameExists(@Param("parentId") Long parentId, @Param("folderName") String folderName,
                         @Param("tenantId") Long tenantId, @Param("excludeId") Long excludeId);
}
