package com.qoobot.qooerp.inventory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.inventory.dto.MaterialCreateDTO;
import com.qoobot.qooerp.inventory.dto.MaterialDTO;
import com.qoobot.qooerp.inventory.dto.MaterialQueryDTO;
import com.qoobot.qooerp.inventory.dto.MaterialUpdateDTO;
import com.qoobot.qooerp.inventory.entity.InventoryMaterial;
import com.qoobot.qooerp.inventory.mapper.InventoryMaterialMapper;
import com.qoobot.qooerp.inventory.service.InventoryMaterialService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryMaterialServiceImpl implements InventoryMaterialService {

    private final InventoryMaterialMapper materialMapper;

    @Override
    public Long createMaterial(MaterialCreateDTO dto) {
        LambdaQueryWrapper<InventoryMaterial> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InventoryMaterial::getTenantId, getCurrentTenantId())
                .eq(InventoryMaterial::getMaterialCode, dto.getMaterialCode());
        if (materialMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("物料编码已存在");
        }

        InventoryMaterial material = new InventoryMaterial();
        BeanUtils.copyProperties(dto, material);
        material.setTenantId(getCurrentTenantId());
        material.setMaterialStatus("ENABLED");
        material.setCreateTime(LocalDateTime.now());
        materialMapper.insert(material);
        return material.getId();
    }

    @Override
    public void updateMaterial(MaterialUpdateDTO dto) {
        InventoryMaterial material = materialMapper.selectById(dto.getId());
        if (material == null) {
            throw new RuntimeException("物料不存在");
        }
        BeanUtils.copyProperties(dto, material);
        material.setUpdateTime(LocalDateTime.now());
        materialMapper.updateById(material);
    }

    @Override
    public void deleteMaterial(Long id) {
        materialMapper.deleteById(id);
    }

    @Override
    public MaterialDTO getMaterialById(Long id) {
        InventoryMaterial material = materialMapper.selectById(id);
        if (material == null) {
            throw new RuntimeException("物料不存在");
        }
        return convertToDTO(material);
    }

    @Override
    public Page<MaterialDTO> queryMaterials(MaterialQueryDTO dto) {
        IPage<InventoryMaterial> page = new Page<>(dto.getCurrent(), dto.getSize());
        LambdaQueryWrapper<InventoryMaterial> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InventoryMaterial::getTenantId, getCurrentTenantId())
                .like(dto.getMaterialCode() != null, InventoryMaterial::getMaterialCode, dto.getMaterialCode())
                .like(dto.getMaterialName() != null, InventoryMaterial::getMaterialName, dto.getMaterialName())
                .eq(dto.getCategoryId() != null, InventoryMaterial::getCategoryId, dto.getCategoryId())
                .eq(dto.getMaterialStatus() != null, InventoryMaterial::getMaterialStatus, dto.getMaterialStatus());
        IPage<InventoryMaterial> result = materialMapper.selectPage(page, wrapper);

        Page<MaterialDTO> dtoPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        dtoPage.setRecords(result.getRecords().stream().map(this::convertToDTO).collect(Collectors.toList()));
        return dtoPage;
    }

    private MaterialDTO convertToDTO(InventoryMaterial entity) {
        MaterialDTO dto = new MaterialDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    private Long getCurrentTenantId() {
        return 1L;
    }
}
