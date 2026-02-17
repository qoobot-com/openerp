package com.qoobot.qooerp.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qoobot.qooerp.system.dto.SystemDictDTO;
import com.qoobot.qooerp.system.entity.SystemDict;
import com.qoobot.qooerp.system.vo.SystemDictVO;

import java.util.List;

/**
 * 字典服务接口
 *
 * @author QooERP Team
 * @version 1.0.0
 */
public interface SystemDictService extends IService<SystemDict> {

    /**
     * 创建字典
     *
     * @param dto 字典DTO
     * @return 字典ID
     */
    Long createDict(SystemDictDTO dto);

    /**
     * 更新字典
     *
     * @param id  字典ID
     * @param dto 字典DTO
     * @return 是否成功
     */
    boolean updateDict(Long id, SystemDictDTO dto);

    /**
     * 删除字典
     *
     * @param id 字典ID
     * @return 是否成功
     */
    boolean deleteDict(Long id);

    /**
     * 获取字典详情
     *
     * @param id 字典ID
     * @return 字典VO
     */
    SystemDictVO getDict(Long id);

    /**
     * 根据编码获取字典
     *
     * @param dictCode 字典编码
     * @return 字典VO
     */
    SystemDictVO getDictByCode(String dictCode);

    /**
     * 分页查询字典
     *
     * @param current  当前页
     * @param size     每页大小
     * @param dictCode 字典编码
     * @param dictName 字典名称
     * @param dictType 字典类型
     * @return 分页结果
     */
    Page<SystemDictVO> pageDict(Long current, Long size, String dictCode, String dictName, String dictType);

    /**
     * 获取所有字典（缓存）
     *
     * @return 字典列表
     */
    List<SystemDictVO> getAllDicts();

    /**
     * 清除字典缓存
     */
    void clearCache();
}
