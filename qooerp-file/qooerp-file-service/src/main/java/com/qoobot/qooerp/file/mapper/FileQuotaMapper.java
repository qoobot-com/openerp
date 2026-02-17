package com.qoobot.qooerp.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qoobot.qooerp.file.entity.FileQuota;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 存储配额Mapper
 *
 * @author QooERP
 * @date 2026-02-17
 */
@Mapper
public interface FileQuotaMapper extends BaseMapper<FileQuota> {

    /**
     * 根据租户ID查询配额
     *
     * @param tenantId 租户ID
     * @return 配额信息
     */
    FileQuota selectByTenantId(@Param("tenantId") Long tenantId);
}
