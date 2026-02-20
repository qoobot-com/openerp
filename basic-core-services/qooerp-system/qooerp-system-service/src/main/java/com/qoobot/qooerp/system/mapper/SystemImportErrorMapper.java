package com.qoobot.qooerp.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qoobot.qooerp.system.entity.SystemImportError;
import org.apache.ibatis.annotations.Mapper;

/**
 * 导入错误记录Mapper
 */
@Mapper
public interface SystemImportErrorMapper extends BaseMapper<SystemImportError> {
}
