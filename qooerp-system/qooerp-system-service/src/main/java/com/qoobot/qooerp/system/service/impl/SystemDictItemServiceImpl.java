package com.qoobot.qooerp.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.system.dto.SystemDictItemDTO;
import com.qoobot.qooerp.system.entity.SystemDict;
import com.qoobot.qooerp.system.entity.SystemDictItem;
import com.qoobot.qooerp.system.mapper.SystemDictItemMapper;
import com.qoobot.qooerp.system.mapper.SystemDictMapper;
import com.qoobot.qooerp.system.service.SystemDictItemService;
import com.qoobot.qooerp.system.service.SystemDictService;
import com.qoobot.qooerp.system.vo.SystemDictItemVO;
import com.qoobot.qooerp.system.vo.SystemDictVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 字典项服务实现
 *
 * @author QooERP Team
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SystemDictItemServiceImpl extends ServiceImpl<SystemDictItemMapper, SystemDictItem> implements SystemDictItemService {

    private final SystemDictService dictService;
    private final SystemDictMapper dictMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "system:dict:item", allEntries = true)
    public Long createDictItem(SystemDictItemDTO dto) {
        // 检查字典是否存在
        SystemDict dict = dictMapper.selectById(dto.getDictId());
        if (dict == null) {
            throw new BusinessException("字典不存在");
        }

        // 检查字典项编码是否重复
        LambdaQueryWrapper<SystemDictItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemDictItem::getDictId, dto.getDictId())
                .eq(SystemDictItem::getItemCode, dto.getItemCode());
        if (baseMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("字典项编码已存在");
        }

        SystemDictItem item = new SystemDictItem();
        BeanUtils.copyProperties(dto, item);
        item.setStatus(dto.getStatus() == null ? 1 : dto.getStatus());
        item.setSort(dto.getSort() == null ? 0 : dto.getSort());
        baseMapper.insert(item);
        return item.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "system:dict:item", key = "#id")
    public boolean updateDictItem(Long id, SystemDictItemDTO dto) {
        SystemDictItem item = baseMapper.selectById(id);
        if (item == null) {
            throw new BusinessException("字典项不存在");
        }

        // 如果修改编码，检查是否冲突
        if (dto.getItemCode() != null && !dto.getItemCode().equals(item.getItemCode())) {
            LambdaQueryWrapper<SystemDictItem> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SystemDictItem::getDictId, item.getDictId())
                    .eq(SystemDictItem::getItemCode, dto.getItemCode())
                    .ne(SystemDictItem::getId, id);
            if (baseMapper.selectCount(wrapper) > 0) {
                throw new BusinessException("字典项编码已存在");
            }
        }

        BeanUtils.copyProperties(dto, item);
        return baseMapper.updateById(item) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "system:dict:item", key = "#id")
    public boolean deleteDictItem(Long id) {
        return baseMapper.deleteById(id) > 0;
    }

    @Override
    public SystemDictItemVO getDictItem(Long id) {
        SystemDictItem item = baseMapper.selectById(id);
        if (item == null) {
            return null;
        }
        SystemDictItemVO vo = new SystemDictItemVO();
        BeanUtils.copyProperties(item, vo);
        return vo;
    }

    @Override
    public List<SystemDictItemVO> getItemsByDictId(Long dictId) {
        LambdaQueryWrapper<SystemDictItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemDictItem::getDictId, dictId)
                .eq(SystemDictItem::getStatus, 1)
                .orderByAsc(SystemDictItem::getSort);
        List<SystemDictItem> items = baseMapper.selectList(wrapper);
        
        return items.stream().map(item -> {
            SystemDictItemVO vo = new SystemDictItemVO();
            BeanUtils.copyProperties(item, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "system:dict:item", key = "#dictCode")
    public List<SystemDictItemVO> getItemsByDictCode(String dictCode) {
        SystemDictVO dict = dictService.getDictByCode(dictCode);
        if (dict == null) {
            return List.of();
        }
        return getItemsByDictId(dict.getId());
    }

    @Override
    public Page<SystemDictItemVO> pageDictItem(Long current, Long size, Long dictId, String itemLabel) {
        Page<SystemDictItem> page = new Page<>(current, size);
        LambdaQueryWrapper<SystemDictItem> wrapper = new LambdaQueryWrapper<>();
        
        if (dictId != null) {
            wrapper.eq(SystemDictItem::getDictId, dictId);
        }
        if (itemLabel != null && !itemLabel.isEmpty()) {
            wrapper.like(SystemDictItem::getItemLabel, itemLabel);
        }
        
        wrapper.orderByAsc(SystemDictItem::getSort);
        
        Page<SystemDictItem> resultPage = baseMapper.selectPage(page, wrapper);
        
        Page<SystemDictItemVO> voPage = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
        List<SystemDictItemVO> voList = resultPage.getRecords().stream().map(item -> {
            SystemDictItemVO vo = new SystemDictItemVO();
            BeanUtils.copyProperties(item, vo);
            return vo;
        }).collect(Collectors.toList());
        voPage.setRecords(voList);
        
        return voPage;
    }
}
