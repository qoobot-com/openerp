package com.qoobot.qooerp.hr.employee.certificate.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qoobot.qooerp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 员工证件实体
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("hr_certificate")
public class HrCertificate extends BaseEntity {

    /**
     * 证件ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 员工ID
     */
    @TableField("employee_id")
    private Long employeeId;

    /**
     * 员工姓名
     */
    @TableField("employee_name")
    private String employeeName;

    /**
     * 证件类型：1-身份证 2-护照 3-港澳通行证 4-台胞证 5-驾驶证 6-其他
     */
    @TableField("certificate_type")
    private Integer certificateType;

    /**
     * 证件号码
     */
    @TableField("certificate_no")
    private String certificateNo;

    /**
     * 证件姓名
     */
    @TableField("certificate_name")
    private String certificateName;

    /**
     * 发证机关
     */
    @TableField("issue_authority")
    private String issueAuthority;

    /**
     * 发证日期
     */
    @TableField("issue_date")
    private LocalDate issueDate;

    /**
     * 有效期至
     */
    @TableField("expiry_date")
    private LocalDate expiryDate;

    /**
     * 证件照片URL
     */
    @TableField("photo_url")
    private String photoUrl;

    /**
     * 附件ID（多个逗号分隔）
     */
    @TableField("attachment_ids")
    private String attachmentIds;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;
}
