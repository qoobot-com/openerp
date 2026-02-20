package com.qoobot.qooerp.production.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.common.tenant.TenantContextHolder;
import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.production.entity.ProductionProcess;
import com.qoobot.qooerp.production.mapper.ProductionProcessMapper;
import com.qoobot.qooerp.production.service.ProductionProcessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductionProcessServiceImpl extends ServiceImpl<ProductionProcessMapper, ProductionProcess> implements ProductionProcessService {

    private final ProductionProcessMapper productionProcessMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createProductionProcess(ProductionProcess process) {
        LambdaQueryWrapper<ProductionProcess> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductionProcess::getProcessCode, process.getProcessCode())
                .eq(ProductionProcess::getTenantId, TenantContextHolder.getTenantId());
        if (this.count(wrapper) > 0) {
            throw new BusinessException("工序编码已存在");
        }

        process.setTenantId(TenantContextHolder.getTenantId());
        this.save(process);
        log.info("创建生产工序成功: processCode={}", process.getProcessCode());
        return process.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateProductionProcess(Long id, ProductionProcess process) {
        ProductionProcess exist = this.getById(id);
        if (exist == null) {
            throw new BusinessException("生产工序不存在");
        }

        if (!exist.getProcessCode().equals(process.getProcessCode())) {
            LambdaQueryWrapper<ProductionProcess> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ProductionProcess::getProcessCode, process.getProcessCode())
                    .eq(ProductionProcess::getTenantId, TenantContextHolder.getTenantId());
            if (this.count(wrapper) > 0) {
                throw new BusinessException("工序编码已存在");
            }
        }

        process.setId(id);
        process.setTenantId(TenantContextHolder.getTenantId());
        this.updateById(process);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteProductionProcess(Long id) {
        ProductionProcess process = this.getById(id);
        if (process == null) {
            throw new BusinessException("生产工序不存在");
        }
        this.removeById(id);
        return true;
    }

    @Override
    public ProductionProcess getProductionProcessById(Long id) {
        ProductionProcess process = this.getById(id);
        if (process == null) {
            throw new BusinessException("生产工序不存在");
        }
        return process;
    }
}
