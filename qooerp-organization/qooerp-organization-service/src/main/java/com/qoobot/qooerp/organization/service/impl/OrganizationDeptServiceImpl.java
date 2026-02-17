package com.qoobot.qooerp.organization.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.common.exception.DataNotFoundException;
import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.common.util.BeanUtils;
import com.qoobot.qooerp.common.util.StringUtils;
import com.qoobot.qooerp.organization.dto.OrganizationDeptDTO;
import com.qoobot.qooerp.organization.entity.OrganizationDept;
import com.qoobot.qooerp.organization.mapper.OrganizationDeptMapper;
import com.qoobot.qooerp.organization.service.OrganizationDeptService;
import com.qoobot.qooerp.organization.vo.OrganizationDeptVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 部门服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrganizationDeptServiceImpl extends ServiceImpl<OrganizationDeptMapper, OrganizationDept>
        implements OrganizationDeptService {

    private final OrganizationDeptMapper deptMapper;

    private static final Integer MAX_DEPT_LEVEL = 20;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "deptTree", allEntries = true)
    public Long createDept(OrganizationDept dept) {
        // 校验部门编码唯一性
        LambdaQueryWrapper<OrganizationDept> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrganizationDept::getDeptCode, dept.getDeptCode());
        if (deptMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("部门编码已存在");
        }

        // 计算部门级别
        if (dept.getParentId() != null && dept.getParentId() > 0) {
            OrganizationDept parentDept = deptMapper.selectById(dept.getParentId());
            if (parentDept == null) {
                throw new DataNotFoundException("父部门不存在");
            }

            // 计算层级（这里简化处理，实际可以增加deptLevel字段）
            dept.setDeptLevel(parentDept.getDeptLevel() != null ? parentDept.getDeptLevel() + 1 : 2);
        } else {
            dept.setDeptLevel(1);
            dept.setParentId(0L);
        }

        // 检查层级深度
        if (dept.getDeptLevel() > MAX_DEPT_LEVEL) {
            throw new BusinessException("部门层级不能超过" + MAX_DEPT_LEVEL + "级");
        }

        deptMapper.insert(dept);
        log.info("创建部门成功: {}", dept);
        return dept.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "deptTree", allEntries = true)
    public void updateDept(OrganizationDept dept) {
        OrganizationDept existDept = deptMapper.selectById(dept.getId());
        if (existDept == null) {
            throw new DataNotFoundException("部门不存在");
        }

        // 检查部门编码唯一性
        LambdaQueryWrapper<OrganizationDept> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrganizationDept::getDeptCode, dept.getDeptCode());
        wrapper.ne(OrganizationDept::getId, dept.getId());
        if (deptMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("部门编码已存在");
        }

        deptMapper.updateById(dept);
        log.info("更新部门成功: {}", dept);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "deptTree", allEntries = true)
    public void deleteDept(Long deptId) {
        OrganizationDept dept = deptMapper.selectById(deptId);
        if (dept == null) {
            throw new DataNotFoundException("部门不存在");
        }

        // 检查是否有子部门
        LambdaQueryWrapper<OrganizationDept> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrganizationDept::getParentId, deptId);
        if (deptMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("存在子部门，无法删除");
        }

        // TODO: 检查是否有用户绑定到该部门

        deptMapper.deleteById(deptId);
        log.info("删除部门成功: deptId={}", deptId);
    }

    @Override
    @Cacheable(value = "deptDetail", key = "#deptId")
    public OrganizationDeptVO getDeptDetail(Long deptId) {
        OrganizationDept dept = deptMapper.selectById(deptId);
        if (dept == null) {
            throw new DataNotFoundException("部门不存在");
        }

        OrganizationDeptVO vo = BeanUtils.copyBean(dept, OrganizationDeptVO.class);
        vo.setStatusName(vo.getStatus() == 1 ? "启用" : "禁用");

        // 查询父部门名称
        if (dept.getParentId() != null && dept.getParentId() > 0) {
            OrganizationDept parentDept = deptMapper.selectById(dept.getParentId());
            if (parentDept != null) {
                // TODO: 可以在VO中添加父部门名称字段
            }
        }

        // 查询负责人名称
        // TODO: 调用用户服务查询负责人名称

        return vo;
    }

    @Override
    public PageResult<OrganizationDeptVO> pageDept(OrganizationDeptDTO dto) {
        LambdaQueryWrapper<OrganizationDept> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.isNotEmpty(dto.getDeptName())) {
            wrapper.like(OrganizationDept::getDeptName, dto.getDeptName());
        }

        if (StringUtils.isNotEmpty(dto.getDeptCode())) {
            wrapper.like(OrganizationDept::getDeptCode, dto.getDeptCode());
        }

        if (dto.getParentId() != null) {
            wrapper.eq(OrganizationDept::getParentId, dto.getParentId());
        }

        if (dto.getStatus() != null) {
            wrapper.eq(OrganizationDept::getStatus, dto.getStatus());
        }

        wrapper.orderByAsc(OrganizationDept::getSort)
                .orderByDesc(OrganizationDept::getCreateTime);

        PageResult<OrganizationDept> pageResult = deptMapper.selectPage(dto.toPage(), wrapper);

        // 转换为VO
        List<OrganizationDeptVO> voList = pageResult.getRecords().stream()
                .map(dept -> {
                    OrganizationDeptVO vo = BeanUtils.copyBean(dept, OrganizationDeptVO.class);
                    vo.setStatusName(dept.getStatus() == 1 ? "启用" : "禁用");
                    return vo;
                })
                .collect(Collectors.toList());

        return PageResult.of(pageResult, voList);
    }

    @Override
    @Cacheable(value = "deptTree", key = "#parentId")
    public List<OrganizationDeptVO> getDeptTree(Long parentId) {
        // 查询所有部门
        LambdaQueryWrapper<OrganizationDept> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrganizationDept::getStatus, 1);
        wrapper.orderByAsc(OrganizationDept::getSort);
        List<OrganizationDept> allDepts = deptMapper.selectList(wrapper);

        // 转换为VO
        List<OrganizationDeptVO> allDeptVOs = allDepts.stream()
                .map(dept -> {
                    OrganizationDeptVO vo = BeanUtils.copyBean(dept, OrganizationDeptVO.class);
                    vo.setStatusName(dept.getStatus() == 1 ? "启用" : "禁用");
                    return vo;
                })
                .collect(Collectors.toList());

        // 构建树形结构
        return buildTree(allDeptVOs, parentId);
    }

    /**
     * 递归构建树形结构
     */
    private List<OrganizationDeptVO> buildTree(List<OrganizationDeptVO> nodes, Long parentId) {
        Long targetParentId = (parentId == null || parentId == 0) ? null : parentId;

        return nodes.stream()
                .filter(node -> {
                    if (targetParentId == null) {
                        return node.getParentId() == null || node.getParentId() == 0;
                    }
                    return targetParentId.equals(node.getParentId());
                })
                .map(node -> {
                    List<OrganizationDeptVO> children = buildTree(nodes, node.getId());
                    node.setChildren(children);
                    node.setHasChildren(!children.isEmpty());
                    return node;
                })
                .sorted(Comparator.comparing(OrganizationDeptVO::getSort))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "deptTree", allEntries = true)
    public void moveDept(Long deptId, Long newParentId) {
        OrganizationDept dept = deptMapper.selectById(deptId);
        if (dept == null) {
            throw new DataNotFoundException("部门不存在");
        }

        Long oldParentId = dept.getParentId();

        // 检查是否移动到自己的子部门下
        if (newParentId != null && newParentId > 0) {
            List<Long> childIds = getAllChildDeptIds(deptId);
            if (childIds.contains(newParentId)) {
                throw new BusinessException("不能将部门移动到其子部门下");
            }

            // 检查新父部门是否存在
            OrganizationDept newParentDept = deptMapper.selectById(newParentId);
            if (newParentDept == null) {
                throw new DataNotFoundException("新父部门不存在");
            }
        }

        // 更新父部门
        dept.setParentId(newParentId != null ? newParentId : 0L);
        deptMapper.updateById(dept);

        log.info("移动部门成功: deptId={}, oldParentId={}, newParentId={}", deptId, oldParentId, newParentId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "deptTree", allEntries = true)
    public void updateDeptSort(List<Long> deptIdList) {
        if (deptIdList == null || deptIdList.isEmpty()) {
            return;
        }

        for (int i = 0; i < deptIdList.size(); i++) {
            OrganizationDept dept = new OrganizationDept();
            dept.setId(deptIdList.get(i));
            dept.setSort(i + 1);
            deptMapper.updateById(dept);
        }

        log.info("更新部门排序成功: deptIdList={}", deptIdList);
    }

    @Override
    public List<Long> getAllChildDeptIds(Long deptId) {
        return deptMapper.selectChildDeptIds(deptId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "deptTree", allEntries = true)
    public void changeStatus(Long deptId, Integer status) {
        OrganizationDept dept = deptMapper.selectById(deptId);
        if (dept == null) {
            throw new DataNotFoundException("部门不存在");
        }

        dept.setStatus(status);
        deptMapper.updateById(dept);

        log.info("切换部门状态成功: deptId={}, status={}", deptId, status);
    }
}
