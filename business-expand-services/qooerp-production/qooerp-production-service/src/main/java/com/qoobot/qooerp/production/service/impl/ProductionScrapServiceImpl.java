package com.qoobot.qooerp.production.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.common.tenant.TenantContextHolder;
import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.production.entity.ProductionScrap;
import com.qoobot.qooerp.production.mapper.ProductionScrapMapper;
import com.qoobot.qooerp.production.service.ProductionScrapService;
import com.qoobot.qooerp.production.vo.ProductionScrapVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductionScrapServiceImpl extends ServiceImpl<ProductionScrapMapper, ProductionScrap> implements ProductionScrapService {

    private final ProductionScrapMapper productionScrapMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createProductionScrap(ProductionScrap scrap) {
        LambdaQueryWrapper<ProductionScrap> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductionScrap::getScrapNo, scrap.getScrapNo())
                .eq(ProductionScrap::getTenantId, TenantContextHolder.getTenantId());
        if (this.count(wrapper) > 0) {
            throw new BusinessException("报废单号已存在");
        }

        scrap.setStatus("draft");
        scrap.setTenantId(TenantContextHolder.getTenantId());
        this.save(scrap);
        log.info("创建生产报废成功: scrapNo={}", scrap.getScrapNo());
        return scrap.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateProductionScrap(Long id, ProductionScrap scrap) {
        ProductionScrap exist = this.getById(id);
        if (exist == null) {
            throw new BusinessException("生产报废不存在");
        }
        if (!"draft".equals(exist.getStatus())) {
            throw new BusinessException("只有草稿状态的报废才能修改");
        }
        scrap.setId(id);
        this.updateById(scrap);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteProductionScrap(Long id) {
        ProductionScrap scrap = this.getById(id);
        if (scrap == null) {
            throw new BusinessException("生产报废不存在");
        }
        if (!"draft".equals(scrap.getStatus())) {
            throw new BusinessException("只有草稿状态的报废才能删除");
        }
        this.removeById(id);
        return true;
    }

    @Override
    public ProductionScrap getProductionScrapById(Long id) {
        ProductionScrap scrap = this.getById(id);
        if (scrap == null) {
            throw new BusinessException("生产报废不存在");
        }
        return scrap;
    }

    @Override
    public IPage<ProductionScrapVO> queryProductionScrapPage(Integer current, Integer size, String scrapNo, String status) {
        Page<ProductionScrap> page = new Page<>(current, size);
        IPage<ProductionScrap> scrapPage = productionScrapMapper.selectPageByCondition(
                page, scrapNo, status, TenantContextHolder.getTenantId()
        );

        return scrapPage.convert(scrap -> {
            ProductionScrapVO vo = BeanUtil.copyProperties(scrap, ProductionScrapVO.class);
            vo.setStatusDesc(getStatusDesc(scrap.getStatus()));
            return vo;
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean approveScrap(Long id) {
        ProductionScrap scrap = this.getById(id);
        if (scrap == null) {
            throw new BusinessException("生产报废不存在");
        }
        if (!"draft".equals(scrap.getStatus())) {
            throw new BusinessException("只有草稿状态的报废才能批准");
        }
        scrap.setStatus("completed");
        this.updateById(scrap);
        log.info("批准生产报废成功: scrapNo={}", scrap.getScrapNo());
        return true;
    }

    private String getStatusDesc(String status) {
        return switch (status) {
            case "draft" -> "草稿";
            case "approved" -> "已批准";
            case "completed" -> "已完成";
            default -> status;
        };
    }
}
