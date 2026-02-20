package com.qoobot.qooerp.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.system.dto.SystemDictItemQueryDTO;
import com.qoobot.qooerp.system.entity.SystemDictItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 字典项Mapper
 */
@Mapper
public interface SystemDictItemMapper extends BaseMapper<SystemDictItem> {

    /**
     * 分页查询字典项
     */
    IPage<SystemDictItem> selectPageByQuery(Page<SystemDictItem> page, @Param("query") SystemDictItemQueryDTO query);

    /**
     * 根据字典ID查询字典项列表
     */
    List<SystemDictItem> selectByDictId(@Param("dictId") Long dictId);

    /**
     * 根据字典编码查询字典项列表
     */
    List<SystemDictItem> selectByDictCode(@Param("dictCode") String dictCode);
}
