package com.qoobot.qooerp.hr.employee.domain;

import com.qoobot.qooerp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 员工合同实体
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class HrContract extends BaseEntity {

    /** 合同编号 */
    private String contractCode;

    /** 员工ID */
    private Long employeeId;

    /** 合同类型：1-正式合同 2-实习合同 3-劳务合同 4-其他 */
    private Integer contractType;

    /** 合同开始日期 */
    private LocalDate startDate;

    /** 合同结束日期 */
    private LocalDate endDate;

    /** 试用期开始日期 */
    private LocalDate probationStartDate;

    /** 试用期结束日期 */
    private LocalDate probationEndDate;

    /** 合同状态：0-未生效 1-生效中 2-已到期 3-已终止 */
    private Integer status;

    /** 合同文件路径 */
    private String filePath;

    /** 备注 */
    private String remark;
}
