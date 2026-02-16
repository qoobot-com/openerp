package com.qoobot.common.dto;

import com.qoobot.common.constant.CommonConstant;
import lombok.Data;

import java.io.Serializable;

/**
 * 分页查询DTO
 */
@Data
public class PageQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 当前页
     */
    private Long current = (long) CommonConstant.DEFAULT_PAGE;

    /**
     * 每页大小
     */
    private Long size = (long) CommonConstant.DEFAULT_SIZE;

    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 排序方向
     */
    private String sortOrder;

    /**
     * 检查并设置默认值
     */
    public void checkAndSetDefault() {
        if (this.current == null || this.current < 1) {
            this.current = (long) CommonConstant.DEFAULT_PAGE;
        }
        if (this.size == null || this.size < 1) {
            this.size = (long) CommonConstant.DEFAULT_SIZE;
        }
        if (this.size > CommonConstant.MAX_SIZE) {
            this.size = (long) CommonConstant.MAX_SIZE;
        }
    }
}
