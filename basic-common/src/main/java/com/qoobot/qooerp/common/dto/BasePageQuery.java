package com.qoobot.qooerp.common.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 基础分页查询DTO
 */
@Data
public class BasePageQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 页码
     */
    private Integer page = 1;

    /**
     * 每页大小
     */
    private Integer size = 10;
}
