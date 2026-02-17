package com.qoobot.qooerp.common.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;

import java.util.List;
import java.util.Map;

/**
 * JSON工具类
 */
public class JsonUtils {

    /**
     * 对象转JSON字符串
     */
    public static String toJson(Object obj) {
        if (obj == null) {
            return null;
        }
        return JSON.toJSONString(obj);
    }

    /**
     * 对象转格式化的JSON字符串
     */
    public static String toJsonPretty(Object obj) {
        if (obj == null) {
            return null;
        }
        return JSON.toJSONString(obj, JSONWriter.Feature.PrettyFormat);
    }

    /**
     * JSON字符串转对象
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        if (StringUtils.isEmpty(json)) {
            return null;
        }
        return JSON.parseObject(json, clazz);
    }

    /**
     * JSON字符串转List
     */
    public static <T> List<T> fromJsonArray(String json, Class<T> clazz) {
        if (StringUtils.isEmpty(json)) {
            return null;
        }
        return JSON.parseArray(json, clazz);
    }

    /**
     * JSON字符串转Map
     */
    public static Map<String, Object> toMap(String json) {
        if (StringUtils.isEmpty(json)) {
            return null;
        }
        return JSON.parseObject(json);
    }

    /**
     * JSON字符串转Map（指定键值类型）
     */
    public static <K, V> Map<K, V> toMap(String json, Class<K> keyClass, Class<V> valueClass) {
        if (StringUtils.isEmpty(json)) {
            return null;
        }
        return JSON.parseObject(json, new com.alibaba.fastjson2.TypeReference<Map<K, V>>() {});
    }

    /**
     * 解析JSON为节点树
     */
    public static JSONObject parseNode(String json) {
        if (StringUtils.isEmpty(json)) {
            return null;
        }
        return JSON.parseObject(json);
    }

    /**
     * 获取JSON路径对应的值
     */
    public static String getValue(String json, String path) {
        if (StringUtils.isEmpty(json) || StringUtils.isEmpty(path)) {
            return null;
        }
        JSONObject node = JSON.parseObject(json);
        String[] keys = path.split("\\.");
        Object current = node;
        for (String key : keys) {
            if (current == null) {
                return null;
            }
            if (current instanceof JSONArray) {
                try {
                    int index = Integer.parseInt(key);
                    current = ((JSONArray) current).get(index);
                } catch (NumberFormatException e) {
                    return null;
                }
            } else if (current instanceof JSONObject) {
                current = ((JSONObject) current).get(key);
            } else {
                return null;
            }
        }
        return current != null ? current.toString() : null;
    }

    /**
     * 对象转JSON字节数组
     */
    public static byte[] toJsonBytes(Object obj) {
        if (obj == null) {
            return null;
        }
        return JSON.toJSONBytes(obj);
    }

    /**
     * JSON字节数组转对象
     */
    public static <T> T fromJsonBytes(byte[] bytes, Class<T> clazz) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        return JSON.parseObject(bytes, clazz);
    }
}
