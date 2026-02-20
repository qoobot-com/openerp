package com.qoobot.qooerp.organization.vo;

import com.qoobot.qooerp.common.vo.TreeNodeVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 组织树VO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OrganizationTreeVO extends TreeNodeVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 节点类型: company/department/position
     */
    private String type;

    /**
     * 公司名称（type=company时使用）
     */
    private String companyName;

    /**
     * 英文名称
     */
    private String nameEn;

    /**
     * 所属公司ID
     */
    private Long companyId;

    /**
     * 所属部门ID
     */
    private Long deptId;

    /**
     * 岗位ID
     */
    private Long positionId;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 电子邮箱
     */
    private String email;

    /**
     * 地址
     */
    private String address;

    /**
     * 描述
     */
    private String description;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 员工数量
     */
    private Integer employeeCount;

    /**
     * 负责人姓名
     */
    private String leaderName;
}
