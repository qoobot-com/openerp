package com.qoobot.qooerp.production.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.common.tenant.TenantContextHolder;
import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.production.dto.ProductionPlanDTO;
import com.qoobot.qooerp.production.entity.ProductionPlan;
import com.qoobot.qooerp.production.mapper.ProductionPlanMapper;
import com.qoobot.qooerp.production.service.ProductionPlanService;
import com.qoobot.qooerp.production.vo.ProductionPlanVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * 生产计划Service实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductionPlanServiceImpl extends ServiceImpl<ProductionPlanMapper, ProductionPlan> implements ProductionPlanService {

    private final ProductionPlanMapper productionPlanMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createProductionPlan(ProductionPlanDTO dto) {
        // 检查计划编号是否已存在
        LambdaQueryWrapper<ProductionPlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductionPlan::getPlanNo, dto.getPlanNo())
                .eq(ProductionPlan::getTenantId, TenantContextHolder.getTenantId());
        if (this.count(wrapper) > 0) {
            throw new BusinessException("计划编号已存在");
        }

        ProductionPlan plan = BeanUtil.copyProperties(dto, ProductionPlan.class);
        plan.setStatus("draft");
        plan.setMrpStatus("pending");
        plan.setCompletedQuantity(BigDecimal.ZERO);
        plan.setTenantId(TenantContextHolder.getTenantId());

        this.save(plan);
        log.info("创建生产计划成功: planNo={}", plan.getPlanNo());
        return plan.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateProductionPlan(Long id, ProductionPlanDTO dto) {
        ProductionPlan plan = this.getById(id);
        if (plan == null) {
            throw new BusinessException("生产计划不存在");
        }

        // 草稿状态才能修改
        if (!"draft".equals(plan.getStatus())) {
            throw new BusinessException("只有草稿状态的计划才能修改");
        }

        // 检查计划编号是否已存在
        if (!plan.getPlanNo().equals(dto.getPlanNo())) {
            LambdaQueryWrapper<ProductionPlan> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ProductionPlan::getPlanNo, dto.getPlanNo())
                    .eq(ProductionPlan::getTenantId, TenantContextHolder.getTenantId());
            if (this.count(wrapper) > 0) {
                throw new BusinessException("计划编号已存在");
            }
        }

        BeanUtil.copyProperties(dto, plan);
        this.updateById(plan);
        log.info("更新生产计划成功: planNo={}", plan.getPlanNo());
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteProductionPlan(Long id) {
        ProductionPlan plan = this.getById(id);
        if (plan == null) {
            throw new BusinessException("生产计划不存在");
        }

        // 草稿或已取消状态才能删除
        if (!"draft".equals(plan.getStatus()) && !"cancelled".equals(plan.getStatus())) {
            throw new BusinessException("只有草稿或已取消状态的计划才能删除");
        }

        this.removeById(id);
        log.info("删除生产计划成功: planNo={}", plan.getPlanNo());
        return true;
    }

    @Override
    public ProductionPlanVO getProductionPlanById(Long id) {
        ProductionPlan plan = this.getById(id);
        if (plan == null) {
            throw new BusinessException("生产计划不存在");
        }

        ProductionPlanVO vo = BeanUtil.copyProperties(plan, ProductionPlanVO.class);
        vo.setStatusDesc(getStatusDesc(plan.getStatus()));

        // 计算进度
        if (plan.getPlanQuantity().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal progress = plan.getCompletedQuantity()
                    .divide(plan.getPlanQuantity(), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);
            vo.setProgressRate(progress);
        } else {
            vo.setProgressRate(BigDecimal.ZERO);
        }

        return vo;
    }

    @Override
    public IPage<ProductionPlanVO> queryProductionPlanPage(Integer current, Integer size, String planNo, String planName, String status) {
        Page<ProductionPlan> page = new Page<>(current, size);
        IPage<ProductionPlan> planPage = productionPlanMapper.selectPageByCondition(
                page, planNo, planName, status, TenantContextHolder.getTenantId()
        );

        // 转换为VO
        return planPage.convert(plan -> {
            ProductionPlanVO vo = BeanUtil.copyProperties(plan, ProductionPlanVO.class);
            vo.setStatusDesc(getStatusDesc(plan.getStatus()));

            // 计算进度
            if (plan.getPlanQuantity().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal progress = plan.getCompletedQuantity()
                        .divide(plan.getPlanQuantity(), 4, RoundingMode.HALF_UP)
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
    public Boolean batchDeleteProductionPlan(List<Long> ids) {
        for (Long id : ids) {
            deleteProductionPlan(id);
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean approveProductionPlan(Long id) {
        ProductionPlan plan = this.getById(id);
        if (plan == null) {
            throw new BusinessException("生产计划不存在");
        }

        if (!"draft".equals(plan.getStatus())) {
            throw new BusinessException("只有草稿状态的计划才能批准");
        }

        plan.setStatus("approved");
        plan.setMrpStatus("calculating");
        this.updateById(plan);
        log.info("批准生产计划成功: planNo={}", plan.getPlanNo());

        // TODO: 异步执行MRP计算
        // executeMRP(id);

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean cancelProductionPlan(Long id) {
        ProductionPlan plan = this.getById(id);
        if (plan == null) {
            throw new BusinessException("生产计划不存在");
        }

        if ("completed".equals(plan.getStatus()) || "cancelled".equals(plan.getStatus())) {
            throw new BusinessException("已完成或已取消的计划不能取消");
        }

        plan.setStatus("cancelled");
        this.updateById(plan);
        log.info("取消生产计划成功: planNo={}", plan.getPlanNo());
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean executeMRP(Long id) {
        ProductionPlan plan = this.getById(id);
        if (plan == null) {
            throw new BusinessException("生产计划不存在");
        }

        // TODO: 调用库存服务获取当前库存，计算物料需求
        // TODO: 生成采购申请单

        plan.setMrpStatus("completed");
        this.updateById(plan);
        log.info("MRP计算完成: planNo={}", plan.getPlanNo());
        return true;
    }

    /**
     * 获取状态描述
     */
    private String getStatusDesc(String status) {
        return switch (status) {
            case "draft" -> "草稿";
            case "approved" -> "已批准";
            case "in_progress" -> "进行中";
            case "completed" -> "已完成";
            case "cancelled" -> "已取消";
            default -> status;
        };
    }
}
