package com.qoobot.qooerp.production.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qoobot.qooerp.production.entity.ProductionSchedule;
import com.qoobot.qooerp.production.vo.ProductionScheduleVO;

/**
 * 生产排程Service接口
 */
public interface ProductionScheduleService extends IService<ProductionSchedule> {
    Long createProductionSchedule(ProductionSchedule schedule);
    Boolean updateProductionSchedule(Long id, ProductionSchedule schedule);
    Boolean deleteProductionSchedule(Long id);
    ProductionSchedule getProductionScheduleById(Long id);
    IPage<ProductionScheduleVO> queryProductionSchedulePage(Integer current, Integer size, String scheduleCode, String status, Long equipmentId);
    Boolean startSchedule(Long id);
    Boolean completeSchedule(Long id);
}
