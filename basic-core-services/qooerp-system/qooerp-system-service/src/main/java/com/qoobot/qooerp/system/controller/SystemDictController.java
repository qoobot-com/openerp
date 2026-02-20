package com.qoobot.qooerp.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.system.dto.SystemDictDTO;
import com.qoobot.qooerp.system.dto.SystemDictItemDTO;
import com.qoobot.qooerp.system.dto.SystemDictItemQueryDTO;
import com.qoobot.qooerp.system.dto.SystemDictQueryDTO;
import com.qoobot.qooerp.system.entity.SystemDictItem;
import com.qoobot.qooerp.system.service.SystemDictService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字典控制器
 */
@RestController
@RequestMapping("/api/system/dict")
@RequiredArgsConstructor
public class SystemDictController {

    private final SystemDictService dictService;

    /**
     * 创建字典
     */
    @PostMapping
    public Result<Long> createDict(@RequestBody SystemDictDTO dictDTO) {
        Long id = dictService.createDict(dictDTO);
        return Result.success(id);
    }

    /**
     * 获取字典
     */
    @GetMapping("/{id}")
    public Result<SystemDictDTO> getDict(@PathVariable Long id) {
        SystemDictDTO dictDTO = dictService.getDict(id);
        return Result.success(dictDTO);
    }

    /**
     * 更新字典
     */
    @PutMapping("/{id}")
    public Result<Void> updateDict(@PathVariable Long id, @RequestBody SystemDictDTO dictDTO) {
        dictService.updateDict(id, dictDTO);
        return Result.success();
    }

    /**
     * 删除字典
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteDict(@PathVariable Long id) {
        dictService.deleteDict(id);
        return Result.success();
    }

    /**
     * 分页查询字典
     */
    @GetMapping("/list")
    public Result<IPage<SystemDictDTO>> pageDict(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            SystemDictQueryDTO queryDTO) {
        IPage<SystemDictDTO> page = dictService.pageDict(current, size, queryDTO);
        return Result.success(page);
    }

    /**
     * 根据编码获取字典
     */
    @GetMapping("/code/{code}")
    public Result<SystemDictDTO> getDictByCode(@PathVariable String code) {
        SystemDictDTO dictDTO = dictService.getDictByCode(code);
        return Result.success(dictDTO);
    }

    /**
     * 创建字典项
     */
    @PostMapping("/item")
    public Result<Long> createDictItem(@RequestBody SystemDictItem dictItem) {
        Long id = dictService.createDictItem(dictItem);
        return Result.success(id);
    }

    /**
     * 获取字典项
     */
    @GetMapping("/item/{id}")
    public Result<SystemDictItem> getDictItem(@PathVariable Long id) {
        SystemDictItem dictItem = dictService.getDictItem(id);
        return Result.success(dictItem);
    }

    /**
     * 更新字典项
     */
    @PutMapping("/item/{id}")
    public Result<Void> updateDictItem(@PathVariable Long id, @RequestBody SystemDictItem dictItem) {
        dictService.updateDictItem(id, dictItem);
        return Result.success();
    }

    /**
     * 删除字典项
     */
    @DeleteMapping("/item/{id}")
    public Result<Void> deleteDictItem(@PathVariable Long id) {
        dictService.deleteDictItem(id);
        return Result.success();
    }

    /**
     * 分页查询字典项
     */
    @GetMapping("/item/list")
    public Result<IPage<SystemDictItem>> pageDictItem(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            SystemDictItemQueryDTO queryDTO) {
        IPage<SystemDictItem> page = dictService.pageDictItem(current, size, queryDTO);
        return Result.success(page);
    }

    /**
     * 根据字典ID获取字典项列表
     */
    @GetMapping("/item/dict/{dictId}")
    public Result<List<SystemDictItem>> getDictItemsByDictId(@PathVariable Long dictId) {
        List<SystemDictItem> items = dictService.getDictItemsByDictId(dictId);
        return Result.success(items);
    }

    /**
     * 根据字典编码获取字典项列表
     */
    @GetMapping("/item/code/{dictCode}")
    public Result<List<SystemDictItem>> getDictItemsByDictCode(@PathVariable String dictCode) {
        List<SystemDictItem> items = dictService.getDictItemsByDictCode(dictCode);
        return Result.success(items);
    }
}
