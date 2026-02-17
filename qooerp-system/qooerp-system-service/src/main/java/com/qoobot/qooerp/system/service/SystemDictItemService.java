package com.qoobot.qooerp.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qoobot.qooerp.system.dto.SystemDictItemDTO;
import com.qoobot.qooerp.system.entity.SystemDictItem;
import com.qoobot.qooerp.system.vo.SystemDictItemVO;

import java.util.List;

/**
 * 字典项服务接口
 *
 * @author QooERP Team
 * @version 1.0.0
 */
public interface SystemDictItemService extends IService<SystemDictItem> {

    /**
     * 创建字典项
     *
     * @param dto 字典项DTO
     * @return 字典项ID
     */
    Long createDictItem(SystemDictItemDTO dto);

    /**
     * 更新字典项
     *
     * @param id  字典项ID
     * @param dto 字典项DTO
     * @return 是否成功
     */
    boolean updateDictItem(Long id, SystemDictItemDTO dto);

    /**
     * 删除字典项
     *
     * @param id 字典项ID
     * @return 是否成功
     */
    boolean deleteDictItem(Long id);

    /**
     * 获取字典项详情
     *
     * @param id 字典项ID
     * @return 字典项VO
     */
    SystemDictItemVO getDictItem(Long id);

    /**
     * 根据字典ID获取字典项列表
     *
     * @param dictId 字典ID
     * @return 字典项列表
     */
    List<SystemDictItemVO> getItemsByDictId(Long dictId);

    /**
     * 根据字典编码获取字典项列表（缓存）
     *
     * @param dictCode 字典编码
     * @return 字典项列表
     */
    List<SystemDictItemVO> getItemsByDictCode(String dictCode);

    /**
     * 分页查询字典项
     *
     * @param current   当前页
     * @param size      每页大小
     * @param dictId    字典ID
     * @param itemLabel 字典标签
     * @return 分页结果
     */
    Page<SystemDictItemVO> pageDictItem(Long current, Long size, Long dictId, String itemLabel);
}
