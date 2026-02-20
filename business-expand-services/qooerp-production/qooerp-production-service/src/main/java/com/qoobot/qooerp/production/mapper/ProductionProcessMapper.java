package com.qoobot.qooerp.production.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qoobot.qooerp.production.entity.ProductionProcess;
import org.apache.ibatis.annotations.Mapper;

/**
 * 生产工序Mapper接口
 */
@Mapper
public interface ProductionProcessMapper extends BaseMapper<ProductionProcess> {
}
