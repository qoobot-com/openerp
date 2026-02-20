package com.qoobot.qooerp.project.risk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.project.risk.domain.ProjectRisk;
import com.qoobot.qooerp.project.risk.dto.ProjectRiskDTO;
import com.qoobot.qooerp.project.risk.mapper.ProjectRiskMapper;
import com.qoobot.qooerp.project.risk.service.IProjectRiskService;
import com.qoobot.qooerp.project.project.dto.ProjectDTO;
import com.qoobot.qooerp.project.project.service.IProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 项目风险服务实现类
 *
 * @author QooERP Team
 * @since 2026-02-18
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectRiskServiceImpl extends ServiceImpl<ProjectRiskMapper, ProjectRisk>
        implements IProjectRiskService {

    private final IProjectService projectService;

    private static final int STATUS_NOT_HANDLED = 0;   // 未处理
    private static final int STATUS_HANDLING = 1;       // 处理中
    private static final int STATUS_RESOLVED = 2;       // 已解决
    private static final int STATUS_CLOSED = 3;          // 已关闭

    private static final int LEVEL_LOW = 1;
    private static final int LEVEL_MEDIUM = 1;
    private static final int LEVEL_HIGH = 2;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProjectRiskDTO create(ProjectRiskDTO dto) {
        ProjectDTO project = projectService.getById(dto.getProjectId());
        if (project == null) {
            throw new BusinessException("项目不存在");
        }

        ProjectRisk risk = new ProjectRisk();
        BeanUtils.copyProperties(dto, risk);
        risk.setStatus(STATUS_NOT_HANDLED);

        save(risk);
        return toDTO(risk);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProjectRiskDTO update(Long id, ProjectRiskDTO dto) {
        ProjectRisk risk = super.getById(id);
        if (risk == null) {
            throw new BusinessException("风险不存在");
        }

        BeanUtils.copyProperties(dto, risk);

        updateById(risk);
        return toDTO(risk);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        ProjectRisk risk = super.getById(id);
        if (risk == null) {
            throw new BusinessException("风险不存在");
        }

        // 已处理的风险不能删除
        if (STATUS_RESOLVED == risk.getStatus() || STATUS_CLOSED == risk.getStatus()) {
            throw new BusinessException("已处理的风险不能删除");
        }

        removeById(id);
    }

    @Override
    public ProjectRiskDTO getById(Long id) {
        ProjectRisk risk = super.getById(id);
        return toDTO(risk);
    }

    @Override
    public PageResult<ProjectRiskDTO> page(Long projectId, Long current, Long size, Long tenantId) {
        LambdaQueryWrapper<ProjectRisk> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectRisk::getProjectId, projectId);
        wrapper.eq(ProjectRisk::getTenantId, tenantId);
        wrapper.orderByDesc(ProjectRisk::getStatus);
        wrapper.orderByDesc(ProjectRisk::getProbability);
        wrapper.orderByDesc(ProjectRisk::getImpact);

        Page<ProjectRisk> page = new Page<>(current, size);
        IPage<ProjectRisk> riskPage = page(page, wrapper);

        return PageResult.of(
                riskPage.getCurrent(),
                riskPage.getSize(),
                riskPage.getTotal(),
                riskPage.getRecords().stream().map(this::toDTO).toList()
        );
    }

    @Override
    public List<ProjectRiskDTO> listByProjectId(Long projectId) {
        LambdaQueryWrapper<ProjectRisk> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectRisk::getProjectId, projectId);
        wrapper.orderByDesc(ProjectRisk::getStatus);
        wrapper.orderByDesc(ProjectRisk::getProbability);
        wrapper.orderByDesc(ProjectRisk::getImpact);

        return list(wrapper).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status) {
        ProjectRisk risk = super.getById(id);
        if (risk == null) {
            throw new BusinessException("风险不存在");
        }

        risk.setStatus(status);
        updateById(risk);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assessRisk(Long id) {
        ProjectRisk risk = super.getById(id);
        if (risk == null) {
            throw new BusinessException("风险不存在");
        }

        // 风险等级在DTO中动态计算，无需更新数据库
        // 保留此方法以便后续扩展其他风险评估逻辑
    }

    private String calculateRiskLevel(Integer probability, Integer impact) {
        if (probability == null || impact == null) {
            return "未知";
        }

        int score = probability * impact;

        if (score >= 6) {
            return "高风险";
        } else if (score >= 3) {
            return "中风险";
        } else {
            return "低风险";
        }
    }

    private ProjectRiskDTO toDTO(ProjectRisk risk) {
        if (risk == null) {
            return null;
        }

        ProjectRiskDTO dto = new ProjectRiskDTO();
        BeanUtils.copyProperties(risk, dto);
        dto.setProbabilityName(getProbabilityName(risk.getProbability()));
        dto.setImpactName(getImpactName(risk.getImpact()));
        dto.setStatusName(getStatusName(risk.getStatus()));

        // 动态计算风险等级
        dto.setRiskLevel(calculateRiskLevel(risk.getProbability(), risk.getImpact()));

        if (risk.getProjectId() != null) {
            ProjectDTO project = projectService.getById(risk.getProjectId());
            if (project != null) {
                dto.setProjectName(project.getProjectName());
            }
        }

        return dto;
    }

    private String getProbabilityName(Integer probability) {
        if (probability == null) {
            return "未知";
        }
        return switch (probability) {
            case 1 -> "低";
            case 2 -> "中";
            case 3 -> "高";
            default -> "未知";
        };
    }

    private String getImpactName(Integer impact) {
        if (impact == null) {
            return "未知";
        }
        return switch (impact) {
            case 1 -> "低";
            case 2 -> "中";
            case 3 -> "高";
            default -> "未知";
        };
    }

    private String getStatusName(Integer status) {
        if (status == null) {
            return "未知";
        }
        return switch (status) {
            case STATUS_NOT_HANDLED -> "未处理";
            case STATUS_HANDLING -> "处理中";
            case STATUS_RESOLVED -> "已解决";
            case STATUS_CLOSED -> "已关闭";
            default -> "未知";
        };
    }
}
