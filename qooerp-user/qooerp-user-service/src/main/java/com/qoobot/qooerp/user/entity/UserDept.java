package com.qoobot.qooerp.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 用户部门关联实体
 */
@Data
@TableName("user_dept")
public class UserDept {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 是否主部门：0-否 1-是
     */
    private Integer isPrimary;
}
