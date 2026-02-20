package com.qoobot.qooerp.organization.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qoobot.qooerp.organization.entity.OrganizationDept;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 部门Mapper接口
 */
@Mapper
public interface OrganizationDeptMapper extends BaseMapper<OrganizationDept> {

    /**
     * 查询所有子部门ID（递归）
     *
     * @param deptId 部门ID
     * @return 子部门ID列表
     */
    @Select("WITH RECURSIVE dept_tree AS (" +
            "  SELECT id FROM organization_dept WHERE parent_id = #{deptId} " +
            "  UNION ALL " +
            "  SELECT d.id FROM organization_dept d " +
            "  INNER JOIN dept_tree dt ON d.parent_id = dt.id" +
            ") SELECT id FROM dept_tree")
    List<Long> selectChildDeptIds(@Param("deptId") Long deptId);

    /**
     * 查询部门树（递归CTE）
     *
     * @param parentId 父部门ID
     * @return 部门列表
     */
    @Select("WITH RECURSIVE dept_tree AS (" +
            "  SELECT * FROM organization_dept " +
            "  WHERE (#{parentId} IS NULL OR parent_id = #{parentId}) " +
            "  UNION ALL " +
            "  SELECT d.* FROM organization_department d " +
            "  INNER JOIN dept_tree dt ON d.parent_id = dt.id" +
            ") SELECT * FROM dept_tree ORDER BY sort")
    List<OrganizationDept> selectDeptTree(@Param("parentId") Long parentId);
}
