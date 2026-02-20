package com.qoobot.qooerp.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qoobot.qooerp.file.entity.FileLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文件操作日志Mapper
 *
 * @author QooERP
 * @date 20xx-xx-xx
 */
@Mapper
public interface FileLogMapper extends BaseMapper<FileLog> {

    /**
     * 根据文件ID查询日志
     *
     * @param fileId 文件ID
     * @return 日志列表
     */
    List<FileLog> selectByFileId(@Param("fileId") Long fileId);

    /**
     * 批量插入日志
     *
     * @param logs 日志列表
     */
    void batchInsert(@Param("logs") List<FileLog> logs);
}
