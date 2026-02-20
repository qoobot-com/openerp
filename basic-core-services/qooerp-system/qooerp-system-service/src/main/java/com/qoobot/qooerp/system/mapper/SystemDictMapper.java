package com.qoobot.qooerp.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.system.dto.SystemDictQueryDTO;
import com.qoobot.qooerp.system.entity.SystemDict;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 字典Mapper
 */
@Mapper
public interface SystemDictMapper extends BaseMapper<SystemDict> {

    /**
     * 分页查询字典
     */
    IPage<SystemDict> selectPageByQuery(Page<SystemDict> page, @Param("query") SystemDictQueryDTO query);
}
