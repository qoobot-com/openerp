package com.qoobot.qooerp.production.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.common.context.TenantContext;
import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.production.dto.ProductionReceiptDTO;
import com.qoobot.qooerp.production.entity.ProductionReceipt;
import com.qoobot.qooerp.production.mapper.ProductionReceiptMapper;
import com.qoobot.qooerp.production.service.ProductionReceiptService;
import com.qoobot.qooerp.production.vo.ProductionReceiptVO;
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
public class ProductionReceiptServiceImpl extends ServiceImpl<ProductionReceiptMapper, ProductionReceipt> implements ProductionReceiptService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createProductionReceipt(ProductionReceiptDTO dto) {
        LambdaQueryWrapper<ProductionReceipt> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductionReceipt::getReceiptNo, dto.getReceiptNo())
                .eq(ProductionReceipt::getTenantId, TenantContext.getTenantId());
        if (this.count(wrapper) > 0) {
            throw new BusinessException("入库单号已存在");
        }

        ProductionReceipt receipt = BeanUtil.copyProperties(dto, ProductionReceipt.class);
        receipt.setStatus("draft");
        receipt.setQualifiedQuantity(BigDecimal.ZERO);
        receipt.setRejectQuantity(BigDecimal.ZERO);
        receipt.setTenantId(TenantContext.getTenantId());

        this.save(receipt);
        log.info("创建生产入库成功: receiptNo={}", receipt.getReceiptNo());
        return receipt.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateProductionReceipt(Long id, ProductionReceiptDTO dto) {
        ProductionReceipt receipt = this.getById(id);
        if (receipt == null) {
            throw new BusinessException("生产入库不存在");
        }
        if (!"draft".equals(receipt.getStatus())) {
            throw new BusinessException("只有草稿状态的入库才能修改");
        }
        BeanUtil.copyProperties(dto, receipt);
        this.updateById(receipt);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteProductionReceipt(Long id) {
        ProductionReceipt receipt = this.getById(id);
        if (receipt == null) {
            throw new BusinessException("生产入库不存在");
        }
        if (!"draft".equals(receipt.getStatus()) && !"cancelled".equals(receipt.getStatus())) {
            throw new BusinessException("只有草稿或已取消状态的入库才能删除");
        }
        this.removeById(id);
        return true;
    }

    @Override
    public ProductionReceiptVO getProductionReceiptById(Long id) {
        ProductionReceipt receipt = this.getById(id);
        if (receipt == null) {
            throw new BusinessException("生产入库不存在");
        }

        ProductionReceiptVO vo = BeanUtil.copyProperties(receipt, ProductionReceiptVO.class);
        vo.setStatusDesc(getStatusDesc(receipt.getStatus()));

        if (receipt.getReceiptQuantity().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal rate = receipt.getQualifiedQuantity()
                    .divide(receipt.getReceiptQuantity(), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);
            vo.setQualifiedRate(rate);
        } else {
            vo.setQualifiedRate(BigDecimal.ZERO);
        }

        return vo;
    }

    @Override
    public IPage<ProductionReceiptVO> queryProductionReceiptPage(Integer current, Integer size, String receiptNo, String status) {
        Page<ProductionReceipt> page = new Page<>(current, size);
        IPage<ProductionReceipt> receiptPage = productionReceiptMapper.selectPageByCondition(
                page, receiptNo, status, TenantContext.getTenantId()
        );

        return receiptPage.convert(receipt -> {
            ProductionReceiptVO vo = BeanUtil.copyProperties(receipt, ProductionReceiptVO.class);
            vo.setStatusDesc(getStatusDesc(receipt.getStatus()));

            if (receipt.getReceiptQuantity().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal rate = receipt.getQualifiedQuantity()
                        .divide(receipt.getReceiptQuantity(), 4, RoundingMode.HALF_UP)
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
    public Boolean batchDeleteProductionReceipt(List<Long> ids) {
        for (Long id : ids) {
            deleteProductionReceipt(id);
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean approveProductionReceipt(Long id) {
        ProductionReceipt receipt = this.getById(id);
        if (receipt == null) {
            throw new BusinessException("生产入库不存在");
        }
        if (!"draft".equals(receipt.getStatus())) {
            throw new BusinessException("只有草稿状态的入库才能批准");
        }
        receipt.setStatus("approved");
        this.updateById(receipt);
        log.info("批准生产入库成功: receiptNo={}", receipt.getReceiptNo());
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean warehouseReceipt(Long id) {
        ProductionReceipt receipt = this.getById(id);
        if (receipt == null) {
            throw new BusinessException("生产入库不存在");
        }
        if (!"approved".equals(receipt.getStatus())) {
            throw new BusinessException("只有已批准的入库才能入库");
        }

        receipt.setStatus("warehoused");
        this.updateById(receipt);
        log.info("生产入库成功: receiptNo={}", receipt.getReceiptNo());

        // TODO: 调用库存服务增加库存
        return true;
    }

    private String getStatusDesc(String status) {
        return switch (status) {
            case "draft" -> "草稿";
            case "approved" -> "已批准";
            case "warehoused" -> "已入库";
            case "completed" -> "已完成";
            default -> status;
        };
    }
}
