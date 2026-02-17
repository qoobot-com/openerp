package com.qoobot.qooerp.production.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.common.context.TenantContext;
import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.production.dto.ProductionOrderDTO;
import com.qoobot.qooerp.production.entity.ProductionOrder;
import com.qoobot.qooerp.production.entity.ProductionPlan;
import com.qoobot.qooerp.production.mapper.ProductionOrderMapper;
import com.qoobot.qooerp.production.service.ProductionOrderService;
import com.qoobot.qooerp.production.service.ProductionPlanService;
import com.qoobot.qooerp.production.vo.ProductionOrderVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * 生产订单Service实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductionOrderServiceImpl extends ServiceImpl<ProductionOrderMapper, ProductionOrder> implements ProductionOrderService {

    private final ProductionOrderMapper productionOrderMapper;
    private final ProductionPlanService productionPlanService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createProductionOrder(ProductionOrderDTO dto) {
        // 检查订单编号是否已存在
        LambdaQueryWrapper<ProductionOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductionOrder::getOrderNo, dto.getOrderNo())
                .eq(ProductionOrder::getTenantId, TenantContext.getTenantId());
        if (this.count(wrapper) > 0) {
            throw new BusinessException("订单编号已存在");
        }

        // 如果有关联生产计划，验证计划是否存在
        if (dto.getPlanId() != null) {
            ProductionPlan plan = productionPlanService.getById(dto.getPlanId());
            if (plan == null) {
                throw new BusinessException("生产计划不存在");
            }
        }

        ProductionOrder order = BeanUtil.copyProperties(dto, ProductionOrder.class);
        order.setStatus("draft");
        order.setCompletedQuantity(BigDecimal.ZERO);
        order.setQualifiedQuantity(BigDecimal.ZERO);
        order.setRejectQuantity(BigDecimal.ZERO);
        order.setProgressRate(BigDecimal.ZERO);
        order.setTenantId(TenantContext.getTenantId());

        this.save(order);
        log.info("创建生产订单成功: orderNo={}", order.getOrderNo());
        return order.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateProductionOrder(Long id, ProductionOrderDTO dto) {
        ProductionOrder order = this.getById(id);
        if (order == null) {
            throw new BusinessException("生产订单不存在");
        }

        // 草稿状态才能修改
        if (!"draft".equals(order.getStatus())) {
            throw new BusinessException("只有草稿状态的订单才能修改");
        }

        // 检查订单编号是否已存在
        if (!order.getOrderNo().equals(dto.getOrderNo())) {
            LambdaQueryWrapper<ProductionOrder> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ProductionOrder::getOrderNo, dto.getOrderNo())
                    .eq(ProductionOrder::getTenantId, TenantContext.getTenantId());
            if (this.count(wrapper) > 0) {
                throw new BusinessException("订单编号已存在");
            }
        }

        BeanUtil.copyProperties(dto, order);
        this.updateById(order);
        log.info("更新生产订单成功: orderNo={}", order.getOrderNo());
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteProductionOrder(Long id) {
        ProductionOrder order = this.getById(id);
        if (order == null) {
            throw new BusinessException("生产订单不存在");
        }

        // 草稿或已取消状态才能删除
        if (!"draft".equals(order.getStatus()) && !"cancelled".equals(order.getStatus())) {
            throw new BusinessException("只有草稿或已取消状态的订单才能删除");
        }

        this.removeById(id);
        log.info("删除生产订单成功: orderNo={}", order.getOrderNo());
        return true;
    }

    @Override
    public ProductionOrderVO getProductionOrderById(Long id) {
        ProductionOrder order = this.getById(id);
        if (order == null) {
            throw new BusinessException("生产订单不存在");
        }

        ProductionOrderVO vo = BeanUtil.copyProperties(order, ProductionOrderVO.class);
        vo.setStatusDesc(getStatusDesc(order.getStatus()));

        // 计算进度
        if (order.getOrderQuantity().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal progress = order.getCompletedQuantity()
                    .divide(order.getOrderQuantity(), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);
            vo.setProgressRate(progress);
        } else {
            vo.setProgressRate(BigDecimal.ZERO);
        }

        return vo;
    }

    @Override
    public IPage<ProductionOrderVO> queryProductionOrderPage(Integer current, Integer size, String orderNo, String status, Long workshopId) {
        Page<ProductionOrder> page = new Page<>(current, size);
        IPage<ProductionOrder> orderPage = productionOrderMapper.selectPageByCondition(
                page, orderNo, status, workshopId, TenantContext.getTenantId()
        );

        // 转换为VO
        return orderPage.convert(order -> {
            ProductionOrderVO vo = BeanUtil.copyProperties(order, ProductionOrderVO.class);
            vo.setStatusDesc(getStatusDesc(order.getStatus()));

            // 计算进度
            if (order.getOrderQuantity().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal progress = order.getCompletedQuantity()
                        .divide(order.getOrderQuantity(), 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .setScale(2, RoundingMode.HALF_UP);
                vo.setProgressRate(progress);
            } else {
                vo.setProgressRate(BigDecimal.ZERO);
            }

            return vo;
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean batchDeleteProductionOrder(List<Long> ids) {
        for (Long id : ids) {
            deleteProductionOrder(id);
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean startProduction(Long id) {
        ProductionOrder order = this.getById(id);
        if (order == null) {
            throw new BusinessException("生产订单不存在");
        }

        if (!"pending".equals(order.getStatus()) && !"draft".equals(order.getStatus())) {
            throw new BusinessException("只有待生产或草稿状态的订单才能开始生产");
        }

        order.setStatus("in_production");
        this.updateById(order);
        log.info("开始生产: orderNo={}", order.getOrderNo());
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean completeProduction(Long id) {
        ProductionOrder order = this.getById(id);
        if (order == null) {
            throw new BusinessException("生产订单不存在");
        }

        if (!"in_production".equals(order.getStatus())) {
            throw new BusinessException("只有生产中的订单才能完成");
        }

        order.setStatus("completed");
        this.updateById(order);
        log.info("完成生产: orderNo={}", order.getOrderNo());

        // TODO: 更新关联的生产计划进度
        // TODO: 生成生产入库单

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean cancelProductionOrder(Long id) {
        ProductionOrder order = this.getById(id);
        if (order == null) {
            throw new BusinessException("生产订单不存在");
        }

        if ("completed".equals(order.getStatus()) || "cancelled".equals(order.getStatus())) {
            throw new BusinessException("已完成或已取消的订单不能取消");
        }

        order.setStatus("cancelled");
        this.updateById(order);
        log.info("取消生产订单: orderNo={}", order.getOrderNo());
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateProgress(Long id, BigDecimal completedQuantity, BigDecimal qualifiedQuantity, BigDecimal rejectQuantity) {
        ProductionOrder order = this.getById(id);
        if (order == null) {
            throw new BusinessException("生产订单不存在");
        }

        if (!"in_production".equals(order.getStatus())) {
            throw new BusinessException("只有生产中的订单才能更新进度");
        }

        order.setCompletedQuantity(completedQuantity);
        order.setQualifiedQuantity(qualifiedQuantity);
        order.setRejectQuantity(rejectQuantity);

        // 计算进度百分比
        if (order.getOrderQuantity().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal progress = completedQuantity
                    .divide(order.getOrderQuantity(), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);
            order.setProgressRate(progress);
        }

        this.updateById(order);

        // 检查是否完成
        if (order.getCompletedQuantity().compareTo(order.getOrderQuantity()) >= 0) {
            order.setStatus("completed");
            this.updateById(order);
        }

        log.info("更新订单进度: orderNo={}, progressRate={}%", order.getOrderNo(), order.getProgressRate());
        return true;
    }

    /**
     * 获取状态描述
     */
    private String getStatusDesc(String status) {
        return switch (status) {
            case "draft" -> "草稿";
            case "pending" -> "待生产";
            case "in_production" -> "生产中";
            case "completed" -> "已完成";
            case "cancelled" -> "已取消";
            default -> status;
        };
    }
}
