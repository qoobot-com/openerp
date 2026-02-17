package com.qoobot.qooerp.production.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.common.context.TenantContext;
import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.production.entity.ProductionRework;
import com.qoobot.qooerp.production.mapper.ProductionReworkMapper;
import com.qoobot.qooerp.production.service.ProductionReworkService;
import com.qoobot.qooerp.production.vo.ProductionReworkVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductionReworkServiceImpl extends ServiceImpl<ProductionReworkMapper, ProductionRework> implements ProductionReworkService {

    private final ProductionReworkMapper productionReworkMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createProductionRework(ProductionRework rework) {
        LambdaQueryWrapper<ProductionRework> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductionRework::getReworkNo, rework.getReworkNo())
                .eq(ProductionRework::getTenantId, TenantContext.getTenantId());
        if (this.count(wrapper) > 0) {
            throw new BusinessException("返工单号已存在");
        }

        rework.setStatus("draft");
        rework.setTenantId(TenantContext.getTenantId());
        this.save(rework);
        log.info("创建生产返工成功: reworkNo={}", rework.getReworkNo());
        return rework.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateProductionRework(Long id, ProductionRework rework) {
        ProductionRework exist = this.getById(id);
        if (exist == null) {
            throw new BusinessException("生产返工不存在");
        }
        if (!"draft".equals(exist.getStatus())) {
            throw new BusinessException("只有草稿状态的返工才能修改");
        }
        rework.setId(id);
        this.updateById(rework);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteProductionRework(Long id) {
        ProductionRework rework = this.getById(id);
        if (rework == null) {
            throw new BusinessException("生产返工不存在");
        }
        if (!"draft".equals(rework.getStatus())) {
            throw new BusinessException("只有草稿状态的返工才能删除");
        }
        this.removeById(id);
        return true;
    }

    @Override
    public ProductionRework getProductionReworkById(Long id) {
        ProductionRework rework = this.getById(id);
        if (rework == null) {
            throw new BusinessException("生产返工不存在");
        }
        return rework;
    }

    @Override
    public IPage<ProductionReworkVO> queryProductionReworkPage(Integer current, Integer size, String reworkNo, String status) {
        Page<ProductionRework> page = new Page<>(current, size);
        IPage<ProductionRework> reworkPage = productionReworkMapper.selectPageByCondition(
                page, reworkNo, status, TenantContext.getTenantId()
        );

        return reworkPage.convert(rework -> {
            ProductionReworkVO vo = BeanUtil.copyProperties(rework, ProductionReworkVO.class);
            vo.setStatusDesc(getStatusDesc(rework.getStatus()));
            return vo;
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean startRework(Long id) {
        ProductionRework rework = this.getById(id);
        if (rework == null) {
            throw new BusinessException("生产返工不存在");
        }
        if (!"draft".equals(rework.getStatus())) {
            throw new BusinessException("只有草稿状态的返工才能开始");
        }
        rework.setStatus("in_progress");
        this.updateById(rework);
        log.info("开始生产返工: reworkNo={}", rework.getReworkNo());
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean completeRework(Long id) {
        ProductionRework rework = this.getById(id);
        if (rework == null) {
            throw new BusinessException("生产返工不存在");
        }
        if (!"in_progress".equals(rework.getStatus())) {
            throw new BusinessException("只有进行中的返工才能完成");
        }
        rework.setStatus("completed");
        this.updateById(rework);
        log.info("完成生产返工: reworkNo={}", rework.getReworkNo());
        return true;
    }

    private String getStatusDesc(String status) {
        return switch (status) {
            case "draft" -> "草稿";
            case "in_progress" -> "进行中";
            case "completed" -> "已完成";
            default -> status;
        };
    }
}
