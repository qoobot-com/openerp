package com.qoobot.qooerp.scm.logistics.cost.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qoobot.qooerp.scm.logistics.cost.domain.LogisticsCost;
import org.apache.ibatis.annotations.Mapper;

/**
 * 物流费用Mapper接口
 *
 * @author QooERP
 * @since 2026-02-17
 */
@Mapper
public interface LogisticsCostMapper extends BaseMapper<LogisticsCost> {
}
