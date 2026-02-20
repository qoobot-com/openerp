package com.qoobot.qooerp.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qoobot.qooerp.system.dto.SystemDictDTO;
import com.qoobot.qooerp.system.dto.SystemDictItemQueryDTO;
import com.qoobot.qooerp.system.dto.SystemDictQueryDTO;
import com.qoobot.qooerp.system.entity.SystemDict;
import com.qoobot.qooerp.system.entity.SystemDictItem;

import java.util.List;

/**
 * 字典服务接口
 */
public interface SystemDictService extends IService<SystemDict> {

    /**
     * 创建字典
     */
    Long createDict(SystemDictDTO dictDTO);

    /**
     * 获取字典
     */
    SystemDictDTO getDict(Long id);

    /**
     * 更新字典
     */
    void updateDict(Long id, SystemDictDTO dictDTO);

    /**
     * 删除字典
     */
    void deleteDict(Long id);

    /**
     * 分页查询字典
     */
    IPage<SystemDictDTO> pageDict(Integer current, Integer size, SystemDictQueryDTO queryDTO);

    /**
     * 根据编码获取字典
     */
    SystemDictDTO getDictByCode(String dictCode);

    /**
     * 创建字典项
     */
    Long createDictItem(SystemDictItem dictItem);

    /**
     * 获取字典项
     */
    SystemDictItem getDictItem(Long id);

    /**
     * 更新字典项
     */
    void updateDictItem(Long id, SystemDictItem dictItem);

    /**
     * 删除字典项
     */
    void deleteDictItem(Long id);

    /**
     * 分页查询字典项
     */
    IPage<SystemDictItem> pageDictItem(Integer current, Integer size, SystemDictItemQueryDTO queryDTO);

    /**
     * 根据字典ID获取字典项列表
     */
    List<SystemDictItem> getDictItemsByDictId(Long dictId);

    /**
     * 根据字典编码获取字典项列表
     */
    List<SystemDictItem> getDictItemsByDictCode(String dictCode);
}
