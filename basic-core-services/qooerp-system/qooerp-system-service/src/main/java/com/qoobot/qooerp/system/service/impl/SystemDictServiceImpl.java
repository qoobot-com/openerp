package com.qoobot.qooerp.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.system.config.SystemProperties;
import com.qoobot.qooerp.system.dto.SystemDictDTO;
import com.qoobot.qooerp.system.dto.SystemDictItemQueryDTO;
import com.qoobot.qooerp.system.dto.SystemDictItemDTO;
import com.qoobot.qooerp.system.dto.SystemDictQueryDTO;
import com.qoobot.qooerp.system.entity.SystemDict;
import com.qoobot.qooerp.system.entity.SystemDictItem;
import com.qoobot.qooerp.system.mapper.SystemDictItemMapper;
import com.qoobot.qooerp.system.mapper.SystemDictMapper;
import com.qoobot.qooerp.system.service.SystemDictService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 字典服务实现（带缓存）
 */
@Service
@RequiredArgsConstructor
public class SystemDictServiceImpl extends ServiceImpl<SystemDictMapper, SystemDict> implements SystemDictService {

    private final SystemDictMapper dictMapper;
    private final SystemDictItemMapper dictItemMapper;
    private final SystemProperties systemProperties;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String DICT_CACHE_PREFIX = "system:dict:";
    private static final String DICT_ITEM_CACHE_PREFIX = "system:dict_item:";

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "dict", allEntries = true)
    public Long createDict(SystemDictDTO dictDTO) {
        // 检查字典编码是否已存在
        LambdaQueryWrapper<SystemDict> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemDict::getDictCode, dictDTO.getDictCode());
        if (dictMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("字典编码已存在");
        }

        SystemDict dict = new SystemDict();
        BeanUtils.copyProperties(dictDTO, dict);
        dictMapper.insert(dict);
        return dict.getId();
    }

    @Override
    @Cacheable(value = "dict", key = "#id")
    public SystemDictDTO getDict(Long id) {
        SystemDict dict = dictMapper.selectById(id);
        if (dict == null) {
            throw new BusinessException("字典不存在");
        }

        SystemDictDTO dto = new SystemDictDTO();
        BeanUtils.copyProperties(dict, dto);
        return dto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "dict", key = "#id")
    public void updateDict(Long id, SystemDictDTO dictDTO) {
        SystemDict existDict = dictMapper.selectById(id);
        if (existDict == null) {
            throw new BusinessException("字典不存在");
        }

        // 如果修改了编码，检查新编码是否已存在
        if (StringUtils.hasText(dictDTO.getDictCode())
            && !dictDTO.getDictCode().equals(existDict.getDictCode())) {
            LambdaQueryWrapper<SystemDict> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SystemDict::getDictCode, dictDTO.getDictCode());
            wrapper.ne(SystemDict::getId, id);
            if (dictMapper.selectCount(wrapper) > 0) {
                throw new BusinessException("字典编码已存在");
            }
        }

        SystemDict dict = new SystemDict();
        BeanUtils.copyProperties(dictDTO, dict);
        dict.setId(id);
        dictMapper.updateById(dict);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "dict", key = "#id")
    public void deleteDict(Long id) {
        SystemDict dict = dictMapper.selectById(id);
        if (dict == null) {
            throw new BusinessException("字典不存在");
        }

        // 检查是否有字典项
        LambdaQueryWrapper<SystemDictItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemDictItem::getDictId, id);
        long count = dictItemMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException("该字典下存在字典项，无法删除");
        }

        dictMapper.deleteById(id);
    }

    @Override
    public IPage<SystemDictDTO> pageDict(Integer current, Integer size, SystemDictQueryDTO queryDTO) {
        Page<SystemDict> page = new Page<>(current, size);
        IPage<SystemDict> dictPage = dictMapper.selectPageByQuery(page, queryDTO);

        Page<SystemDictDTO> result = new Page<>(current, size, dictPage.getTotal());
        List<SystemDictDTO> dtoList = dictPage.getRecords().stream().map(dict -> {
            SystemDictDTO dto = new SystemDictDTO();
            BeanUtils.copyProperties(dict, dto);
            return dto;
        }).toList();
        result.setRecords(dtoList);

        return result;
    }

    @Override
    @Cacheable(value = "dict", key = "'code:' + #dictCode")
    public SystemDictDTO getDictByCode(String dictCode) {
        LambdaQueryWrapper<SystemDict> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemDict::getDictCode, dictCode);
        SystemDict dict = dictMapper.selectOne(wrapper);
        if (dict == null) {
            return null;
        }

        SystemDictDTO dto = new SystemDictDTO();
        BeanUtils.copyProperties(dict, dto);
        return dto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "dictItem", allEntries = true)
    public Long createDictItem(SystemDictItem dictItem) {
        // 检查字典是否存在
        SystemDict dict = dictMapper.selectById(dictItem.getDictId());
        if (dict == null) {
            throw new BusinessException("字典不存在");
        }

        dictItemMapper.insert(dictItem);

        // 清除该字典的缓存
        String cacheKey = DICT_ITEM_CACHE_PREFIX + "dict:" + dictItem.getDictId();
        redisTemplate.delete(cacheKey);
        if (dict.getDictCode() != null) {
            String codeCacheKey = DICT_ITEM_CACHE_PREFIX + "code:" + dict.getDictCode();
            redisTemplate.delete(codeCacheKey);
        }

        return dictItem.getId();
    }

    @Override
    public SystemDictItem getDictItem(Long id) {
        return dictItemMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "dictItem", allEntries = true)
    public void updateDictItem(Long id, SystemDictItem dictItem) {
        SystemDictItem existItem = dictItemMapper.selectById(id);
        if (existItem == null) {
            throw new BusinessException("字典项不存在");
        }

        // 检查字典是否存在
        Long dictId = dictItem.getDictId() != null ? dictItem.getDictId() : existItem.getDictId();
        if (dictId != null) {
            SystemDict dict = dictMapper.selectById(dictId);
            if (dict == null) {
                throw new BusinessException("字典不存在");
            }

            // 清除缓存
            String cacheKey = DICT_ITEM_CACHE_PREFIX + "dict:" + dictId;
            redisTemplate.delete(cacheKey);
            if (dict.getDictCode() != null) {
                String codeCacheKey = DICT_ITEM_CACHE_PREFIX + "code:" + dict.getDictCode();
                redisTemplate.delete(codeCacheKey);
            }
        }

        dictItem.setId(id);
        dictItemMapper.updateById(dictItem);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "dictItem", allEntries = true)
    public void deleteDictItem(Long id) {
        SystemDictItem item = dictItemMapper.selectById(id);
        if (item != null && item.getDictId() != null) {
            // 清除缓存
            String cacheKey = DICT_ITEM_CACHE_PREFIX + "dict:" + item.getDictId();
            redisTemplate.delete(cacheKey);
        }
        dictItemMapper.deleteById(id);
    }

    @Override
    public IPage<SystemDictItem> pageDictItem(Integer current, Integer size, SystemDictItemQueryDTO queryDTO) {
        Page<SystemDictItem> page = new Page<>(current, size);
        return dictItemMapper.selectPageByQuery(page, queryDTO);
    }

    @Override
    public List<SystemDictItem> getDictItemsByDictId(Long dictId) {
        if (!systemProperties.getDict().isCacheEnabled()) {
            return dictItemMapper.selectByDictId(dictId);
        }

        String cacheKey = DICT_ITEM_CACHE_PREFIX + "dict:" + dictId;
        List<SystemDictItem> cachedItems = (List<SystemDictItem>) redisTemplate.opsForValue().get(cacheKey);

        if (cachedItems != null) {
            return cachedItems;
        }

        List<SystemDictItem> items = dictItemMapper.selectByDictId(dictId);

        // 缓存30分钟
        if (items != null && !items.isEmpty()) {
            redisTemplate.opsForValue().set(cacheKey, items,
                    systemProperties.getDict().getCacheTtl(), TimeUnit.SECONDS);
        }

        return items;
    }

    @Override
    public List<SystemDictItem> getDictItemsByDictCode(String dictCode) {
        if (!systemProperties.getDict().isCacheEnabled()) {
            return dictItemMapper.selectByDictCode(dictCode);
        }

        String cacheKey = DICT_ITEM_CACHE_PREFIX + "code:" + dictCode;
        List<SystemDictItem> cachedItems = (List<SystemDictItem>) redisTemplate.opsForValue().get(cacheKey);

        if (cachedItems != null) {
            return cachedItems;
        }

        List<SystemDictItem> items = dictItemMapper.selectByDictCode(dictCode);

        // 缓存30分钟
        if (items != null && !items.isEmpty()) {
            redisTemplate.opsForValue().set(cacheKey, items,
                    systemProperties.getDict().getCacheTtl(), TimeUnit.SECONDS);
        }

        return items;
    }
}
