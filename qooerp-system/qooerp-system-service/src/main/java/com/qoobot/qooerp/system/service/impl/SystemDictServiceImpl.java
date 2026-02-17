package com.qoobot.qooerp.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.system.dto.SystemDictDTO;
import com.qoobot.qooerp.system.entity.SystemDict;
import com.qoobot.qooerp.system.entity.SystemDictItem;
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
 * 字典服务实现
 *
 * @author QooERP Team
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SystemDictServiceImpl extends ServiceImpl<SystemDictMapper, SystemDict> implements SystemDictService {

    private final SystemDictItemService dictItemService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "system:dict", allEntries = true)
    public Long createDict(SystemDictDTO dto) {
        // 检查字典编码是否存在
        LambdaQueryWrapper<SystemDict> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemDict::getDictCode, dto.getDictCode());
        if (baseMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("字典编码已存在");
        }

        SystemDict dict = new SystemDict();
        BeanUtils.copyProperties(dto, dict);
        dict.setStatus(dto.getStatus() == null ? 1 : dto.getStatus());
        baseMapper.insert(dict);
        return dict.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "system:dict", key = "#id")
    public boolean updateDict(Long id, SystemDictDTO dto) {
        SystemDict dict = baseMapper.selectById(id);
        if (dict == null) {
            throw new BusinessException("字典不存在");
        }

        // 如果修改编码，检查是否冲突
        if (dto.getDictCode() != null && !dto.getDictCode().equals(dict.getDictCode())) {
            LambdaQueryWrapper<SystemDict> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SystemDict::getDictCode, dto.getDictCode()).ne(SystemDict::getId, id);
            if (baseMapper.selectCount(wrapper) > 0) {
                throw new BusinessException("字典编码已存在");
            }
        }

        BeanUtils.copyProperties(dto, dict);
        return baseMapper.updateById(dict) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "system:dict", key = "#id")
    public boolean deleteDict(Long id) {
        // 检查是否有字典项
        LambdaQueryWrapper<SystemDictItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemDictItem::getDictId, id);
        if (dictItemService.count(wrapper) > 0) {
            throw new BusinessException("字典下有字典项，无法删除");
        }

        return baseMapper.deleteById(id) > 0;
    }

    @Override
    public SystemDictVO getDict(Long id) {
        SystemDict dict = baseMapper.selectById(id);
        if (dict == null) {
            return null;
        }

        SystemDictVO vo = new SystemDictVO();
        BeanUtils.copyProperties(dict, vo);

        // 获取字典项
        List<SystemDictItemVO> items = dictItemService.getItemsByDictId(id);
        vo.setItems(items);

        return vo;
    }

    @Override
    @Cacheable(value = "system:dict", key = "#dictCode")
    public SystemDictVO getDictByCode(String dictCode) {
        LambdaQueryWrapper<SystemDict> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemDict::getDictCode, dictCode);
        SystemDict dict = baseMapper.selectOne(wrapper);
        if (dict == null) {
            return null;
        }

        SystemDictVO vo = new SystemDictVO();
        BeanUtils.copyProperties(dict, vo);
        return vo;
    }

    @Override
    public Page<SystemDictVO> pageDict(Long current, Long size, String dictCode, String dictName, String dictType) {
        Page<SystemDict> page = new Page<>(current, size);
        LambdaQueryWrapper<SystemDict> wrapper = new LambdaQueryWrapper<>();
        
        if (dictCode != null && !dictCode.isEmpty()) {
            wrapper.like(SystemDict::getDictCode, dictCode);
        }
        if (dictName != null && !dictName.isEmpty()) {
            wrapper.like(SystemDict::getDictName, dictName);
        }
        if (dictType != null && !dictType.isEmpty()) {
            wrapper.eq(SystemDict::getDictType, dictType);
        }
        
        wrapper.orderByDesc(SystemDict::getCreateTime);
        
        Page<SystemDict> resultPage = baseMapper.selectPage(page, wrapper);
        
        Page<SystemDictVO> voPage = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
        List<SystemDictVO> voList = resultPage.getRecords().stream().map(dict -> {
            SystemDictVO vo = new SystemDictVO();
            BeanUtils.copyProperties(dict, vo);
            return vo;
        }).collect(Collectors.toList());
        voPage.setRecords(voList);
        
        return voPage;
    }

    @Override
    @Cacheable(value = "system:dict:all")
    public List<SystemDictVO> getAllDicts() {
        List<SystemDict> dicts = baseMapper.selectList(null);
        return dicts.stream().map(dict -> {
            SystemDictVO vo = new SystemDictVO();
            BeanUtils.copyProperties(dict, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    @CacheEvict(value = {"system:dict", "system:dict:all"}, allEntries = true)
    public void clearCache() {
        log.info("字典缓存已清除");
    }
}
