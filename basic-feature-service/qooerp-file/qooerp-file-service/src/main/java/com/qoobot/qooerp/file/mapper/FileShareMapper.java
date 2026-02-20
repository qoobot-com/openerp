package com.qoobot.qooerp.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qoobot.qooerp.file.entity.FileShare;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文件分享Mapper
 *
 * @author QooERP
 * @date 20xx-xx-xx
 */
@Mapper
public interface FileShareMapper extends BaseMapper<FileShare> {

    /**
     * 根据分享码查询
     *
     * @param shareCode 分享码
     * @return 分享信息
     */
    FileShare selectByShareCode(@Param("shareCode") String shareCode);

    /**
     * 根据文件ID查询分享列表
     *
     * @param fileId 文件ID
     * @param tenantId 租户ID
     * @return 分享列表
     */
    List<FileShare> selectByFileId(@Param("fileId") Long fileId, @Param("tenantId") Long tenantId);
}
