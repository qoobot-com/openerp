package com.qoobot.qooerp.production.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qoobot.qooerp.production.dto.ProductionPlanDTO;
import com.qoobot.qooerp.production.entity.ProductionPlan;
import com.qoobot.qooerp.production.vo.ProductionPlanVO;

import java.util.List;

/**
 * 生产计划Service接口
 */
public interface ProductionPlanService extends IService<ProductionPlan> {

    /**
     * 创建生产计划
     *
     * @param dto 生产计划DTO
     * @return 生产计划ID
     */
    Long createProductionPlan(ProductionPlanDTO dto);

    /**
     * 更新生产计划
     *
     * @param id 生产计划ID
     * @param dto 生产计划DTO
     * @return 是否成功
     */
    Boolean updateProductionPlan(Long id, ProductionPlanDTO dto);

    /**
     * 删除生产计划
     *
     * @param id 生产计划ID
     * @return 是否成功
     */
    Boolean deleteProductionPlan(Long id);

    /**
     * 根据ID查询生产计划
     *
     * @param id 生产计划ID
     * @return 生产计划VO
     */
    ProductionPlanVO getProductionPlanById(Long id);

    /**
     * 分页查询生产计划
     *
     * @param current 当前页
     * @param size 每页大小
     * @param planNo 计划编号
     * @param planName 计划名称
     * @param status 状态
     * @return 分页结果
     */
    IPage<ProductionPlanVO> queryProductionPlanPage(Integer current, Integer size, String planNo, String planName, String status);

    /**
     * 批量删除生产计划
     *
     * @param ids 生产计划ID列表
     * @return 是否成功
     */
    Boolean batchDeleteProductionPlan(List<Long> ids);

    /**
     * 批准生产计划
     *
     * @param id 生产计划ID
     * @return 是否成功
     */
    Boolean approveProductionPlan(Long id);

    /**
     * 取消生产计划
     *
     * @param id 生产计划ID
     * @return 是否成功
     */
    Boolean cancelProductionPlan(Long id);

    /**
     * 执行MRP计算
     *
     * @param id 生产计划ID
     * @return 是否成功
     */
    Boolean executeMRP(Long id);
}
