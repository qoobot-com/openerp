package com.qoobot.qooerp.common.response;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页响应结果
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 当前页码
     */
    private Long current;

    /**
     * 每页大小
     */
    private Long size;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 总页数
     */
    private Long pages;

    /**
     * 数据列表
     */
    private List<T> records;

    public PageResult() {
    }

    public PageResult(Long current, Long size, Long total, List<T> records) {
        this.current = current;
        this.size = size;
        this.total = total;
        this.pages = (total + size - 1) / size;
        this.records = records;
    }

    /**
     * 构建分页结果
     */
    public static <T> PageResult<T> of(Long current, Long size, Long total, List<T> records) {
        return new PageResult<>(current, size, total, records);
    }

    /**
     * 从MyBatis-Plus的Page对象构建分页结果
     */
    public static <T, R> PageResult<R> of(Page<T> page, List<R> records) {
        return new PageResult<>(page.getCurrent(), page.getSize(), page.getTotal(), records);
    }
}
