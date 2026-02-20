package com.qoobot.qooerp.production.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.common.tenant.TenantContextHolder;
import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.production.dto.ProductionMaterialDTO;
import com.qoobot.qooerp.production.entity.ProductionMaterial;
import com.qoobot.qooerp.production.mapper.ProductionMaterialMapper;
import com.qoobot.qooerp.production.service.ProductionMaterialService;
import com.qoobot.qooerp.production.vo.ProductionMaterialVO;
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
public class ProductionMaterialServiceImpl extends ServiceImpl<ProductionMaterialMapper, ProductionMaterial> implements ProductionMaterialService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createProductionMaterial(ProductionMaterialDTO dto) {
        LambdaQueryWrapper<ProductionMaterial> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductionMaterial::getMaterialNo, dto.getMaterialNo())
                .eq(ProductionMaterial::getTenantId, TenantContextHolder.getTenantId());
        if (this.count(wrapper) > 0) {
            throw new BusinessException("领料单号已存在");
        }

        ProductionMaterial material = BeanUtil.copyProperties(dto, ProductionMaterial.class);
        material.setStatus("draft");
        material.setIssuedQuantity(BigDecimal.ZERO);
        material.setTenantId(TenantContextHolder.getTenantId());

        this.save(material);
        log.info("创建生产领料成功: materialNo={}", material.getMaterialNo());
        return material.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateProductionMaterial(Long id, ProductionMaterialDTO dto) {
        ProductionMaterial material = this.getById(id);
        if (material == null) {
            throw new BusinessException("生产领料不存在");
        }
        if (!"draft".equals(material.getStatus())) {
            throw new BusinessException("只有草稿状态的领料才能修改");
        }
        BeanUtil.copyProperties(dto, material);
        this.updateById(material);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteProductionMaterial(Long id) {
        ProductionMaterial material = this.getById(id);
        if (material == null) {
            throw new BusinessException("生产领料不存在");
        }
        if (!"draft".equals(material.getStatus()) && !"cancelled".equals(material.getStatus())) {
            throw new BusinessException("只有草稿或已取消状态的领料才能删除");
        }
        this.removeById(id);
        return true;
    }

    @Override
    public ProductionMaterialVO getProductionMaterialById(Long id) {
        ProductionMaterial material = this.getById(id);
        if (material == null) {
            throw new BusinessException("生产领料不存在");
        }

        ProductionMaterialVO vo = BeanUtil.copyProperties(material, ProductionMaterialVO.class);
        vo.setStatusDesc(getStatusDesc(material.getStatus()));

        if (material.getRequiredQuantity().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal rate = material.getIssuedQuantity()
                    .divide(material.getRequiredQuantity(), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);
            vo.setIssueRate(rate);
        } else {
            vo.setIssueRate(BigDecimal.ZERO);
        }

        return vo;
    }

    @Override
    public IPage<ProductionMaterialVO> queryProductionMaterialPage(Integer current, Integer size, String materialNo, String status) {
        Page<ProductionMaterial> page = new Page<>(current, size);
        IPage<ProductionMaterial> materialPage = baseMapper.selectPageByCondition(
                page, materialNo, status, TenantContextHolder.getTenantId()
        );

        return materialPage.convert(material -> {
            ProductionMaterialVO vo = BeanUtil.copyProperties(material, ProductionMaterialVO.class);
            vo.setStatusDesc(getStatusDesc(material.getStatus()));

            if (material.getRequiredQuantity().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal rate = material.getIssuedQuantity()
                        .divide(material.getRequiredQuantity(), 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .setScale(2, RoundingMode.HALF_UP);
                vo.setIssueRate(rate);
            } else {
                vo.setIssueRate(BigDecimal.ZERO);
            }

            return vo;
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean batchDeleteProductionMaterial(List<Long> ids) {
        for (Long id : ids) {
            deleteProductionMaterial(id);
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean approveProductionMaterial(Long id) {
        ProductionMaterial material = this.getById(id);
        if (material == null) {
            throw new BusinessException("生产领料不存在");
        }
        if (!"draft".equals(material.getStatus())) {
            throw new BusinessException("只有草稿状态的领料才能批准");
        }
        material.setStatus("approved");
        this.updateById(material);
        log.info("批准生产领料成功: materialNo={}", material.getMaterialNo());
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean issueMaterial(Long id) {
        ProductionMaterial material = this.getById(id);
        if (material == null) {
            throw new BusinessException("生产领料不存在");
        }
        if (!"approved".equals(material.getStatus())) {
            throw new BusinessException("只有已批准的领料才能出库");
        }

        material.setStatus("issued");
        this.updateById(material);
        log.info("领料出库成功: materialNo={}", material.getMaterialNo());

        // TODO: 调用库存服务扣减库存
        return true;
    }

    private String getStatusDesc(String status) {
        return switch (status) {
            case "draft" -> "草稿";
            case "approved" -> "已批准";
            case "issued" -> "已领料";
            case "completed" -> "已完成";
            default -> status;
        };
    }
}
