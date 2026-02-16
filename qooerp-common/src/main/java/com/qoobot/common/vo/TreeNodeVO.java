package com.qoobot.common.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 树形结构VO
 */
@Data
public class TreeNodeVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 节点ID
     */
    private Long id;

    /**
     * 节点名称
     */
    private String name;

    /**
     * 父节点ID
     */
    private Long parentId;

    /**
     * 子节点列表
     */
    private List<TreeNodeVO> children = new ArrayList<>();

    /**
     * 是否有子节点
     */
    private Boolean hasChildren;

    /**
     * 节点层级
     */
    private Integer level;

    /**
     * 节点路径
     */
    private String path;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 添加子节点
     */
    public void addChild(TreeNodeVO child) {
        if (this.children == null) {
            this.children = new ArrayList<>();
        }
        this.children.add(child);
        this.hasChildren = true;
    }
}
