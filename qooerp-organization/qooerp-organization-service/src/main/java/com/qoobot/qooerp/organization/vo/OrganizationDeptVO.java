package com.qoobot.qooerp.organization.vo;

import com.qoobot.qooerp.common.vo.TreeNodeVO;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 部门VO
 */
@Data
public class OrganizationDeptVO implements Serializable, TreeNodeVO {

    private static final long serialVersionUID = 1L;

    /**
     * 部门ID
     */
    private Long id;

    /**
     * 父部门ID
     */
    private Long parentId;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 部门编码
     */
    private String deptCode;

    /**
     * 负责人ID
     */
    private Long leaderId;

    /**
     * 负责人姓名
     */
    private String leaderName;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 地址
     */
    private String address;

    /**
     * 排序号
     */
    private Integer sort;

    /**
     * 状态：0-禁用 1-启用
     */
    private Integer status;

    /**
     * 状态名称
     */
    private String statusName;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 创建人
     */
    private String createByName;

    /**
     * 更新人
     */
    private String updateByName;

    /**
     * 子部门列表（树形结构）
     */
    private List<OrganizationDeptVO> children = new ArrayList<>();

    /**
     * 是否有子节点
     */
    private Boolean hasChildren = false;

    /**
     * 员工数量
     */
    private Integer employeeCount = 0;

    @Override
    public Long getTreeId() {
        return this.id;
    }

    @Override
    public Long getTreeParentId() {
        return this.parentId;
    }

    @Override
    public Integer getTreeSort() {
        return this.sort;
    }
}
