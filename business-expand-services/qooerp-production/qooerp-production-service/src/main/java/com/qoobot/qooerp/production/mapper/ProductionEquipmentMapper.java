package com.qoobot.qooerp.production.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.production.entity.ProductionEquipment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 生产设备Mapper接口
 */
@Mapper
public interface ProductionEquipmentMapper extends BaseMapper<ProductionEquipment> {

    /**
     * 分页查询生产设备
     */
    IPage<ProductionEquipment> selectPageByCondition(
            Page<ProductionEquipment> page,
            @Param("equipmentCode") String equipmentCode,
            @Param("equipmentName") String equipmentName,
            @Param("status") String status,
            @Param("workshopId") Long workshopId,
            @Param("tenantId") Long tenantId
    );
}
