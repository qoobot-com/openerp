package com.qoobot.qooerp.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.system.dto.SystemConfigQueryDTO;
import com.qoobot.qooerp.system.entity.SystemConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 系统参数Mapper
 */
@Mapper
public interface SystemConfigMapper extends BaseMapper<SystemConfig> {

    /**
     * 分页查询参数
     */
    IPage<SystemConfig> selectPageByQuery(Page<SystemConfig> page, @Param("query") SystemConfigQueryDTO query);
}
