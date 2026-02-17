package com.qoobot.qooerp.organization.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.common.exception.DataNotFoundException;
import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.common.util.BeanUtils;
import com.qoobot.qooerp.common.util.StringUtils;
import com.qoobot.qooerp.organization.dto.OrganizationPositionDTO;
import com.qoobot.qooerp.organization.entity.OrganizationDept;
import com.qoobot.qooerp.organization.entity.OrganizationPosition;
import com.qoobot.qooerp.organization.mapper.OrganizationDeptMapper;
import com.qoobot.qooerp.organization.mapper.OrganizationPositionMapper;
import com.qoobot.qooerp.organization.service.OrganizationPositionService;
import com.qoobot.qooerp.organization.vo.OrganizationPositionVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 岗位服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrganizationPositionServiceImpl extends ServiceImpl<OrganizationPositionMapper, OrganizationPosition>
        implements OrganizationPositionService {

    private final OrganizationPositionMapper positionMapper;
    private final OrganizationDeptMapper deptMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createPosition(OrganizationPosition position) {
        // 校验岗位编码唯一性
        LambdaQueryWrapper<OrganizationPosition> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrganizationPosition::getPositionCode, position.getPositionCode());
        if (positionMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("岗位编码已存在");
        }

        // 如果指定了部门，检查部门是否存在
        if (position.getDeptId() != null && position.getDeptId() > 0) {
            OrganizationDept dept = deptMapper.selectById(position.getDeptId());
            if (dept == null) {
                throw new DataNotFoundException("部门不存在");
            }
        }

        positionMapper.insert(position);
        log.info("创建岗位成功: {}", position);
        return position.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePosition(OrganizationPosition position) {
        OrganizationPosition existPosition = positionMapper.selectById(position.getId());
        if (existPosition == null) {
            throw new DataNotFoundException("岗位不存在");
        }

        // 检查岗位编码唯一性
        LambdaQueryWrapper<OrganizationPosition> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrganizationPosition::getPositionCode, position.getPositionCode());
        wrapper.ne(OrganizationPosition::getId, position.getId());
        if (positionMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("岗位编码已存在");
        }

        // 如果指定了部门，检查部门是否存在
        if (position.getDeptId() != null && position.getDeptId() > 0) {
            OrganizationDept dept = deptMapper.selectById(position.getDeptId());
            if (dept == null) {
                throw new DataNotFoundException("部门不存在");
            }
        }

        positionMapper.updateById(position);
        log.info("更新岗位成功: {}", position);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePosition(Long positionId) {
        OrganizationPosition position = positionMapper.selectById(positionId);
        if (position == null) {
            throw new DataNotFoundException("岗位不存在");
        }

        // TODO: 检查是否有用户绑定到该岗位

        positionMapper.deleteById(positionId);
        log.info("删除岗位成功: positionId={}", positionId);
    }

    @Override
    public OrganizationPositionVO getPositionDetail(Long positionId) {
        OrganizationPosition position = positionMapper.selectById(positionId);
        if (position == null) {
            throw new DataNotFoundException("岗位不存在");
        }

        OrganizationPositionVO vo = BeanUtils.copyBean(position, OrganizationPositionVO.class);
        vo.setStatusName(position.getStatus() == 1 ? "启用" : "禁用");

        // 查询部门名称
        if (position.getDeptId() != null && position.getDeptId() > 0) {
            OrganizationDept dept = deptMapper.selectById(position.getDeptId());
            if (dept != null) {
                vo.setDeptName(dept.getDeptName());
            }
        }

        return vo;
    }

    @Override
    public PageResult<OrganizationPositionVO> pagePosition(OrganizationPositionDTO dto) {
        LambdaQueryWrapper<OrganizationPosition> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.isNotEmpty(dto.getPositionName())) {
            wrapper.like(OrganizationPosition::getPositionName, dto.getPositionName());
        }

        if (StringUtils.isNotEmpty(dto.getPositionCode())) {
            wrapper.like(OrganizationPosition::getPositionCode, dto.getPositionCode());
        }

        if (dto.getDeptId() != null) {
            wrapper.eq(OrganizationPosition::getDeptId, dto.getDeptId());
        }

        if (dto.getPositionLevel() != null) {
            wrapper.eq(OrganizationPosition::getPositionLevel, dto.getPositionLevel());
        }

        if (dto.getStatus() != null) {
            wrapper.eq(OrganizationPosition::getStatus, dto.getStatus());
        }

        wrapper.orderByAsc(OrganizationPosition::getPositionLevel)
                .orderByDesc(OrganizationPosition::getCreateTime);

        PageResult<OrganizationPosition> pageResult = positionMapper.selectPage(dto.toPage(), wrapper);

        // 转换为VO
        List<OrganizationPositionVO> voList = pageResult.getRecords().stream()
                .map(position -> {
                    OrganizationPositionVO vo = BeanUtils.copyBean(position, OrganizationPositionVO.class);
                    vo.setStatusName(position.getStatus() == 1 ? "启用" : "禁用");

                    // 查询部门名称
                    if (position.getDeptId() != null && position.getDeptId() > 0) {
                        OrganizationDept dept = deptMapper.selectById(position.getDeptId());
                        if (dept != null) {
                            vo.setDeptName(dept.getDeptName());
                        }
                    }

                    return vo;
                })
                .collect(Collectors.toList());

        return PageResult.of(pageResult, voList);
    }

    @Override
    public List<OrganizationPositionVO> listByDeptId(Long deptId) {
        LambdaQueryWrapper<OrganizationPosition> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrganizationPosition::getDeptId, deptId);
        wrapper.eq(OrganizationPosition::getStatus, 1);
        wrapper.orderByAsc(OrganizationPosition::getPositionLevel);

        List<OrganizationPosition> positions = positionMapper.selectList(wrapper);

        return positions.stream()
                .map(position -> {
                    OrganizationPositionVO vo = BeanUtils.copyBean(position, OrganizationPositionVO.class);
                    vo.setStatusName(position.getStatus() == 1 ? "启用" : "禁用");
                    return vo;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeStatus(Long positionId, Integer status) {
        OrganizationPosition position = positionMapper.selectById(positionId);
        if (position == null) {
            throw new DataNotFoundException("岗位不存在");
        }

        position.setStatus(status);
        positionMapper.updateById(position);

        log.info("切换岗位状态成功: positionId={}, status={}", positionId, status);
    }
}
