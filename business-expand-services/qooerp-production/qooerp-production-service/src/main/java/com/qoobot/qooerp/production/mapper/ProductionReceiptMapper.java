package com.qoobot.qooerp.production.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.production.entity.ProductionReceipt;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 生产入库Mapper接口
 */
@Mapper
public interface ProductionReceiptMapper extends BaseMapper<ProductionReceipt> {

    /**
     * 分页查询生产入库
     */
    IPage<ProductionReceipt> selectPageByCondition(
            Page<ProductionReceipt> page,
            @Param("receiptNo") String receiptNo,
            @Param("status") String status,
            @Param("tenantId") Long tenantId
    );
}
