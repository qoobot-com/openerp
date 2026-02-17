package com.qoobot.qooerp.system.util;

import com.qoobot.qooerp.common.util.SpringContextHolder;
import com.qoobot.qooerp.system.entity.SystemDict;
import com.qoobot.qooerp.system.entity.SystemDictItem;
import com.qoobot.qooerp.system.service.SystemDictItemService;
import com.qoobot.qooerp.system.service.SystemDictService;
import com.qoobot.qooerp.system.vo.SystemDictItemVO;
import com.qoobot.qooerp.system.vo.SystemDictVO;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 字典工具类
 *
 * @author QooERP Team
 * @version 1.0.0
 */
public class DictUtils {

    /**
     * 获取字典服务
     */
    private static SystemDictService getDictService() {
        return SpringContextHolder.getBean(SystemDictService.class);
    }

    /**
     * 获取字典项服务
     */
    private static SystemDictItemService getDictItemService() {
        return SpringContextHolder.getBean(SystemDictItemService.class);
    }

    /**
     * 根据字典编码获取字典
     *
     * @param dictCode 字典编码
     * @return 字典VO
     */
    public static SystemDictVO getDict(String dictCode) {
        return getDictService().getDictByCode(dictCode);
    }

    /**
     * 根据字典编码获取字典项列表
     *
     * @param dictCode 字典编码
     * @return 字典项列表
     */
    public static List<SystemDictItemVO> getDictItems(String dictCode) {
        SystemDictVO dict = getDict(dictCode);
        if (dict == null) {
            return List.of();
        }
        return getDictItemService().getItemsByDictId(dict.getId());
    }

    /**
     * 根据字典编码获取字典项Map（value -> label）
     *
     * @param dictCode 字典编码
     * @return 字典项Map
     */
    public static Map<String, String> getDictItemsMap(String dictCode) {
        List<SystemDictItemVO> items = getDictItems(dictCode);
        return items.stream()
                .collect(Collectors.toMap(SystemDictItemVO::getItemValue, SystemDictItemVO::getItemLabel));
    }

    /**
     * 根据字典编码和值获取标签
     *
     * @param dictCode 字典编码
     * @param itemValue 字典项值
     * @return 字典项标签
     */
    public static String getDictLabel(String dictCode, String itemValue) {
        Map<String, String> map = getDictItemsMap(dictCode);
        return map.get(itemValue);
    }

    /**
     * 根据字典编码和编码获取值
     *
     * @param dictCode 字典编码
     * @param itemCode 字典项编码
     * @return 字典项值
     */
    public static String getDictValue(String dictCode, String itemCode) {
        List<SystemDictItemVO> items = getDictItems(dictCode);
        return items.stream()
                .filter(item -> itemCode.equals(item.getItemCode()))
                .findFirst()
                .map(SystemDictItemVO::getItemValue)
                .orElse(null);
    }

    /**
     * 根据字典编码和值获取编码
     *
     * @param dictCode 字典编码
     * @param itemValue 字典项值
     * @return 字典项编码
     */
    public static String getDictCodeByValue(String dictCode, String itemValue) {
        List<SystemDictItemVO> items = getDictItems(dictCode);
        return items.stream()
                .filter(item -> itemValue.equals(item.getItemValue()))
                .findFirst()
                .map(SystemDictItemVO::getItemCode)
                .orElse(null);
    }

    /**
     * 清除字典缓存
     */
    public static void clearDictCache() {
        getDictService().clearCache();
    }

    private DictUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
