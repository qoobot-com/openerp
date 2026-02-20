package com.qoobot.qooerp.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.system.dto.SystemLogDTO;
import com.qoobot.qooerp.system.dto.SystemLogQueryDTO;
import com.qoobot.qooerp.system.entity.SystemLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * 操作日志Mapper
 */
@Mapper
public interface SystemLogMapper extends BaseMapper<SystemLog> {

    /**
     * 分页查询日志
     */
    IPage<SystemLogDTO> selectPageByQuery(Page<SystemLog> page, @Param("query") SystemLogQueryDTO query);

    /**
     * 日志统计
     */
    Map<String, Object> selectStatistics(@Param("query") SystemLogQueryDTO query);
}
