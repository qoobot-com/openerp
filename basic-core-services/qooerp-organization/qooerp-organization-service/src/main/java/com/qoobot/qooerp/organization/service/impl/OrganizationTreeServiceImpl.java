package com.qoobot.qooerp.organization.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qoobot.qooerp.common.exception.DataNotFoundException;
import com.qoobot.qooerp.organization.entity.OrganizationDept;
import com.qoobot.qooerp.organization.entity.OrganizationPosition;
import com.qoobot.qooerp.organization.mapper.OrganizationDeptMapper;
import com.qoobot.qooerp.organization.mapper.OrganizationPositionMapper;
import com.qoobot.qooerp.organization.service.OrganizationTreeService;
import com.qoobot.qooerp.organization.vo.OrganizationTreeVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 组织树服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrganizationTreeServiceImpl implements OrganizationTreeService {

    private final OrganizationDeptMapper deptMapper;
    private final OrganizationPositionMapper positionMapper;

    @Override
    @Cacheable(value = "orgTree", key = "'tenant:' + #tenantId")
    public List<OrganizationTreeVO> getFullOrganizationTree(Long tenantId) {
        // 获取租户下的所有部门
        LambdaQueryWrapper<OrganizationDept> deptWrapper = new LambdaQueryWrapper<>();
        deptWrapper.eq(OrganizationDept::getTenantId, tenantId);
        deptWrapper.eq(OrganizationDept::getStatus, 1);
        deptWrapper.orderByAsc(OrganizationDept::getSort);
        List<OrganizationDept> allDepts = deptMapper.selectList(deptWrapper);

        // 获取所有部门ID
        List<Long> deptIds = allDepts.stream()
                .map(OrganizationDept::getId)
                .collect(Collectors.toList());

        // 获取这些部门下的所有岗位
        LambdaQueryWrapper<OrganizationPosition> posWrapper = new LambdaQueryWrapper<>();
        posWrapper.in(OrganizationPosition::getDeptId, deptIds);
        posWrapper.eq(OrganizationPosition::getStatus, 1);
        List<OrganizationPosition> allPositions = positionMapper.selectList(posWrapper);

        // 按部门ID分组岗位
        Map<Long, List<OrganizationTreeVO>> positionMap = allPositions.stream()
                .map(pos -> convertPositionToTreeVO(pos))
                .collect(Collectors.groupingBy(OrganizationTreeVO::getDeptId));

        // 构建完整树
        return buildFullTree(allDepts, positionMap, null);
    }

    @Override
    @Cacheable(value = "deptTree", key = "'tenant:' + #tenantId")
    public List<OrganizationTreeVO> getDeptTree(Long tenantId) {
        LambdaQueryWrapper<OrganizationDept> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrganizationDept::getTenantId, tenantId);
        wrapper.eq(OrganizationDept::getStatus, 1);
        wrapper.orderByAsc(OrganizationDept::getSort);
        List<OrganizationDept> allDepts = deptMapper.selectList(wrapper);

        // 转换为TreeVO
        List<OrganizationTreeVO> allTreeNodes = allDepts.stream()
                .map(this::convertDeptToTreeVO)
                .collect(Collectors.toList());

        // 构建树形结构
        return buildDeptTree(allTreeNodes, null);
    }

    @Override
    @Cacheable(value = "deptPath", key = "#deptId")
    public String getDeptPath(Long deptId) {
        List<String> path = new ArrayList<>();
        OrganizationDept dept = deptMapper.selectById(deptId);
        
        if (dept == null) {
            throw new DataNotFoundException("部门不存在");
        }

        while (dept != null && dept.getId() != null) {
            path.add(0, dept.getDeptName());
            if (dept.getParentId() != null && dept.getParentId() > 0) {
                dept = deptMapper.selectById(dept.getParentId());
            } else {
                dept = null;
            }
        }

        return String.join(" / ", path);
    }

    /**
     * 构建完整组织树（包含岗位）
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private List<OrganizationTreeVO> buildFullTree(List<OrganizationDept> allDepts,
                                                      Map<Long, List<OrganizationTreeVO>> positionMap,
                                                      Long parentId) {
        // 获取父部门为parentId的所有部门
        List<OrganizationDept> childrenDepts = allDepts.stream()
                .filter(dept -> {
                    if (parentId == null) {
                        return dept.getParentId() == null || dept.getParentId() == 0;
                    }
                    return parentId.equals(dept.getParentId());
                })
                .sorted(Comparator.comparing(OrganizationDept::getSort))
                .collect(Collectors.toList());

        List<OrganizationTreeVO> treeNodes = new ArrayList<>();
        for (OrganizationDept dept : childrenDepts) {
            OrganizationTreeVO node = convertDeptToTreeVO(dept);

            // 添加子部门
            List<OrganizationTreeVO> childDepts = buildFullTree(allDepts, positionMap, dept.getId());
            node.setChildren((List)new ArrayList<>(childDepts));
            node.setHasChildren(!childDepts.isEmpty());

            // 添加该部门的岗位
            List<OrganizationTreeVO> positions = positionMap.get(dept.getId());
            if (positions != null && !positions.isEmpty()) {
                List<OrganizationTreeVO> merged = new ArrayList<>(childDepts);
                merged.addAll(positions);
                node.setChildren((List)new ArrayList<>(merged));
            }

            treeNodes.add(node);
        }

        return treeNodes;
    }

    /**
     * 构建部门树
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private List<OrganizationTreeVO> buildDeptTree(List<OrganizationTreeVO> nodes, Long parentId) {
        return nodes.stream()
                .filter(node -> {
                    if (parentId == null) {
                        return node.getParentId() == null || node.getParentId() == 0;
                    }
                    return parentId.equals(node.getParentId());
                })
                .map(node -> {
                    List<OrganizationTreeVO> children = buildDeptTree(nodes, node.getId());
                    node.setChildren((List)new ArrayList<>(children));
                    node.setHasChildren(!children.isEmpty());
                    return node;
                })
                .sorted(Comparator.comparing(OrganizationTreeVO::getSort))
                .collect(Collectors.toList());
    }

    /**
     * 部门转TreeVO
     */
    private OrganizationTreeVO convertDeptToTreeVO(OrganizationDept dept) {
        OrganizationTreeVO vo = new OrganizationTreeVO();
        vo.setId(dept.getId());
        vo.setName(dept.getDeptName());
        vo.setParentId(dept.getParentId());
        vo.setType("department");
        vo.setDeptId(dept.getId());
        vo.setPhone(dept.getPhone());
        vo.setEmail(dept.getEmail());
        vo.setAddress(dept.getAddress());
        vo.setDescription(dept.getRemark());
        vo.setSort(dept.getSort());
        vo.setStatus(dept.getStatus());
        return vo;
    }

    /**
     * 岗位转TreeVO
     */
    private OrganizationTreeVO convertPositionToTreeVO(OrganizationPosition position) {
        OrganizationTreeVO vo = new OrganizationTreeVO();
        vo.setId(position.getId());
        vo.setName(position.getPositionName());
        vo.setParentId(position.getDeptId());
        vo.setLevel(position.getPositionLevel());
        vo.setType("position");
        vo.setDeptId(position.getDeptId());
        vo.setPositionId(position.getId());
        vo.setDescription(position.getRemark());
        vo.setStatus(position.getStatus());
        return vo;
    }
}
