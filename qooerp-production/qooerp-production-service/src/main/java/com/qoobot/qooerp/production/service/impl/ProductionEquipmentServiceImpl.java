package com.qoobot.qooerp.production.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.common.context.TenantContext;
import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.production.entity.ProductionEquipment;
import com.qoobot.qooerp.production.mapper.ProductionEquipmentMapper;
import com.qoobot.qooerp.production.service.ProductionEquipmentService;
import com.qoobot.qooerp.production.vo.ProductionEquipmentVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductionEquipmentServiceImpl extends ServiceImpl<ProductionEquipmentMapper, ProductionEquipment> implements ProductionEquipmentService {

    private final ProductionEquipmentMapper productionEquipmentMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createProductionEquipment(ProductionEquipment equipment) {
        LambdaQueryWrapper<ProductionEquipment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductionEquipment::getEquipmentCode, equipment.getEquipmentCode())
                .eq(ProductionEquipment::getTenantId, TenantContext.getTenantId());
        if (this.count(wrapper) > 0) {
            throw new BusinessException("设备编码已存在");
        }

        equipment.setTenantId(TenantContext.getTenantId());
        this.save(equipment);
        log.info("创建生产设备成功: equipmentCode={}", equipment.getEquipmentCode());
        return equipment.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateProductionEquipment(Long id, ProductionEquipment equipment) {
        ProductionEquipment exist = this.getById(id);
        if (exist == null) {
            throw new BusinessException("生产设备不存在");
        }

        if (!exist.getEquipmentCode().equals(equipment.getEquipmentCode())) {
            LambdaQueryWrapper<ProductionEquipment> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ProductionEquipment::getEquipmentCode, equipment.getEquipmentCode())
                    .eq(ProductionEquipment::getTenantId, TenantContext.getTenantId());
            if (this.count(wrapper) > 0) {
                throw new BusinessException("设备编码已存在");
            }
        }

        equipment.setId(id);
        equipment.setTenantId(TenantContext.getTenantId());
        this.updateById(equipment);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteProductionEquipment(Long id) {
        ProductionEquipment equipment = this.getById(id);
        if (equipment == null) {
            throw new BusinessException("生产设备不存在");
        }
        this.removeById(id);
        return true;
    }

    @Override
    public ProductionEquipment getProductionEquipmentById(Long id) {
        ProductionEquipment equipment = this.getById(id);
        if (equipment == null) {
            throw new BusinessException("生产设备不存在");
        }
        return equipment;
    }

    @Override
    public IPage<ProductionEquipmentVO> queryProductionEquipmentPage(Integer current, Integer size, String equipmentCode, String equipmentName, String status, Long workshopId) {
        Page<ProductionEquipment> page = new Page<>(current, size);
        IPage<ProductionEquipment> equipmentPage = productionEquipmentMapper.selectPageByCondition(
                page, equipmentCode, equipmentName, status, workshopId, TenantContext.getTenantId()
        );

        return equipmentPage.convert(equipment -> {
            ProductionEquipmentVO vo = BeanUtil.copyProperties(equipment, ProductionEquipmentVO.class);
            vo.setStatusDesc(getStatusDesc(equipment.getStatus()));
            return vo;
        });
    }

    private String getStatusDesc(String status) {
        return switch (status) {
            case "normal" -> "正常";
            case "maintenance" -> "维保中";
            case "fault" -> "故障";
            case "scrapped" -> "报废";
            default -> status;
        };
    }
}
