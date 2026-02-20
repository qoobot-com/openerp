package com.qoobot.qooerp.production.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.common.tenant.TenantContextHolder;
import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.production.dto.ProductionQualityDTO;
import com.qoobot.qooerp.production.entity.ProductionQuality;
import com.qoobot.qooerp.production.mapper.ProductionQualityMapper;
import com.qoobot.qooerp.production.service.ProductionQualityService;
import com.qoobot.qooerp.production.vo.ProductionQualityVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductionQualityServiceImpl extends ServiceImpl<ProductionQualityMapper, ProductionQuality> implements ProductionQualityService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createProductionQuality(ProductionQualityDTO dto) {
        LambdaQueryWrapper<ProductionQuality> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductionQuality::getQualityNo, dto.getQualityNo())
                .eq(ProductionQuality::getTenantId, TenantContextHolder.getTenantId());
        if (this.count(wrapper) > 0) {
            throw new BusinessException("质检单号已存在");
        }

        ProductionQuality quality = BeanUtil.copyProperties(dto, ProductionQuality.class);
        quality.setStatus("draft");
        quality.setQualifiedQuantity(BigDecimal.ZERO);
        quality.setRejectQuantity(BigDecimal.ZERO);
        quality.setTenantId(TenantContextHolder.getTenantId());

        this.save(quality);
        log.info("创建生产质检成功: qualityNo={}", quality.getQualityNo());
        return quality.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateProductionQuality(Long id, ProductionQualityDTO dto) {
        ProductionQuality quality = this.getById(id);
        if (quality == null) {
            throw new BusinessException("生产质检不存在");
        }
        if (!"draft".equals(quality.getStatus())) {
            throw new BusinessException("只有草稿状态的质检才能修改");
        }
        BeanUtil.copyProperties(dto, quality);
        this.updateById(quality);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteProductionQuality(Long id) {
        ProductionQuality quality = this.getById(id);
        if (quality == null) {
            throw new BusinessException("生产质检不存在");
        }
        if (!"draft".equals(quality.getStatus())) {
            throw new BusinessException("只有草稿状态的质检才能删除");
        }
        this.removeById(id);
        return true;
    }

    @Override
    public ProductionQualityVO getProductionQualityById(Long id) {
        ProductionQuality quality = this.getById(id);
        if (quality == null) {
            throw new BusinessException("生产质检不存在");
        }

        ProductionQualityVO vo = BeanUtil.copyProperties(quality, ProductionQualityVO.class);
        vo.setStatusDesc(getStatusDesc(quality.getStatus()));

        if (quality.getInspectionQuantity().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal rate = quality.getQualifiedQuantity()
                    .divide(quality.getInspectionQuantity(), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);
            vo.setQualifiedRate(rate);
        } else {
            vo.setQualifiedRate(BigDecimal.ZERO);
        }

        return vo;
    }

    @Override
    public IPage<ProductionQualityVO> queryProductionQualityPage(Integer current, Integer size, String qualityNo, String status) {
        Page<ProductionQuality> page = new Page<>(current, size);
        IPage<ProductionQuality> qualityPage = baseMapper.selectPageByCondition(
                page, qualityNo, status, TenantContextHolder.getTenantId()
        );

        return qualityPage.convert(quality -> {
            ProductionQualityVO vo = BeanUtil.copyProperties(quality, ProductionQualityVO.class);
            vo.setStatusDesc(getStatusDesc(quality.getStatus()));

            if (quality.getInspectionQuantity().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal rate = quality.getQualifiedQuantity()
                        .divide(quality.getInspectionQuantity(), 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .setScale(2, RoundingMode.HALF_UP);
                vo.setQualifiedRate(rate);
            } else {
                vo.setQualifiedRate(BigDecimal.ZERO);
            }

            return vo;
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean batchDeleteProductionQuality(List<Long> ids) {
        for (Long id : ids) {
            deleteProductionQuality(id);
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean startInspection(Long id) {
        ProductionQuality quality = this.getById(id);
        if (quality == null) {
            throw new BusinessException("生产质检不存在");
        }
        if (!"draft".equals(quality.getStatus())) {
            throw new BusinessException("只有草稿状态的质检才能开始检验");
        }
        quality.setStatus("inspecting");
        this.updateById(quality);
        log.info("开始质检: qualityNo={}", quality.getQualityNo());
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean passQualityCheck(Long id) {
        ProductionQuality quality = this.getById(id);
        if (quality == null) {
            throw new BusinessException("生产质检不存在");
        }
        if (!"inspecting".equals(quality.getStatus())) {
            throw new BusinessException("只有检验中的质检才能判定合格");
        }

        quality.setStatus("passed");

        // 计算合格率
        if (quality.getInspectionQuantity().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal rate = quality.getQualifiedQuantity()
                    .divide(quality.getInspectionQuantity(), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);
            quality.setQualifiedRate(rate);
        }

        this.updateById(quality);
        log.info("质检合格: qualityNo={}", quality.getQualityNo());

        // TODO: 生成生产入库单
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean failQualityCheck(Long id) {
        ProductionQuality quality = this.getById(id);
        if (quality == null) {
            throw new BusinessException("生产质检不存在");
        }
        if (!"inspecting".equals(quality.getStatus())) {
            throw new BusinessException("只有检验中的质检才能判定不合格");
        }

        quality.setStatus("rejected");

        // 计算合格率
        if (quality.getInspectionQuantity().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal rate = quality.getQualifiedQuantity()
                    .divide(quality.getInspectionQuantity(), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);
            quality.setQualifiedRate(rate);
        }

        this.updateById(quality);
        log.info("质检不合格: qualityNo={}", quality.getQualityNo());

        // TODO: 生成返工单
        return true;
    }

    private String getStatusDesc(String status) {
        return switch (status) {
            case "draft" -> "草稿";
            case "inspecting" -> "检验中";
            case "passed" -> "合格";
            case "rejected" -> "不合格";
            default -> status;
        };
    }
}
