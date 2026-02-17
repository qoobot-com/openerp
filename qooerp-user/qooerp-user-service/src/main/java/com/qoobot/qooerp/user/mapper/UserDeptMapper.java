package com.qoobot.qooerp.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qoobot.qooerp.user.entity.UserDept;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户部门关联Mapper
 */
@Mapper
public interface UserDeptMapper extends BaseMapper<UserDept> {

    /**
     * 查询用户部门列表
     */
    List<Long> selectDeptIdsByUserId(@Param("userId") Long userId);

    /**
     * 查询用户主部门
     */
    Long selectPrimaryDeptByUserId(@Param("userId") Long userId);

    /**
     * 删除用户部门关联
     */
    int deleteByUserId(@Param("userId") Long userId);

    /**
     * 删除部门用户关联
     */
    int deleteByDeptId(@Param("deptId") Long deptId);

    /**
     * 设置用户主部门
     */
    int setPrimaryDept(@Param("userId") Long userId, @Param("deptId") Long deptId);
}
