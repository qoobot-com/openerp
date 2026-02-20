package com.qoobot.qooerp.report.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.report.dto.ReportDatasourceCreateDTO;
import com.qoobot.qooerp.report.dto.ReportDatasourceDTO;
import com.qoobot.qooerp.report.dto.ReportDatasourceQueryDTO;
import com.qoobot.qooerp.report.dto.ReportDatasourceUpdateDTO;
import com.qoobot.qooerp.report.entity.ReportDatasource;
import com.qoobot.qooerp.report.mapper.ReportDatasourceMapper;
import com.qoobot.qooerp.report.service.ReportDatasourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 报表数据源服务实现
 *
 * @author Auto
 * @since 2026-02-18
 */
@Slf4j
@Service
public class ReportDatasourceServiceImpl extends ServiceImpl<ReportDatasourceMapper, ReportDatasource>
        implements ReportDatasourceService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ReportDatasourceDTO createDatasource(ReportDatasourceCreateDTO dto) {
        log.info("创建数据源: {}", dto);

        ReportDatasource datasource = new ReportDatasource();
        BeanUtils.copyProperties(dto, datasource);

        // 设置租户ID
        datasource.setTenantId(getCurrentTenantId());

        save(datasource);

        log.info("数据源创建成功: id={}", datasource.getId());

        return convertToDTO(datasource);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateDatasource(ReportDatasourceUpdateDTO dto) {
        log.info("更新数据源: {}", dto);

        ReportDatasource datasource = getById(dto.getId());
        if (datasource == null) {
            throw new BusinessException("数据源不存在");
        }

        BeanUtils.copyProperties(dto, datasource, "id", "reportId", "tenantId");

        return updateById(datasource);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteDatasource(Long id) {
        log.info("删除数据源: id={}", id);

        ReportDatasource datasource = getById(id);
        if (datasource == null) {
            throw new BusinessException("数据源不存在");
        }

        return removeById(id);
    }

    @Override
    public ReportDatasourceDTO getDatasourceById(Long id) {
        ReportDatasource datasource = getById(id);
        if (datasource == null) {
            throw new BusinessException("数据源不存在");
        }
        return convertToDTO(datasource);
    }

    @Override
    public PageResult<ReportDatasourceDTO> queryDatasources(ReportDatasourceQueryDTO queryDTO) {
        log.info("分页查询数据源: {}", queryDTO);

        LambdaQueryWrapper<ReportDatasource> wrapper = new LambdaQueryWrapper<>();

        if (queryDTO.getReportId() != null) {
            wrapper.eq(ReportDatasource::getReportId, queryDTO.getReportId());
        }

        if (StringUtils.hasText(queryDTO.getDatasourceName())) {
            wrapper.like(ReportDatasource::getDatasourceName, queryDTO.getDatasourceName());
        }

        if (StringUtils.hasText(queryDTO.getDatasourceType())) {
            wrapper.eq(ReportDatasource::getDatasourceType, queryDTO.getDatasourceType());
        }

        wrapper.orderByDesc(ReportDatasource::getCreateTime);

        Page<ReportDatasource> page = new Page<>(queryDTO.getPage(), queryDTO.getSize());
        IPage<ReportDatasource> result = page(page, wrapper);

        List<ReportDatasourceDTO> dtoList = result.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return PageResult.of(result.getCurrent(), result.getSize(), result.getTotal(), dtoList);
    }

    @Override
    public boolean testConnection(Long id) {
        log.info("测试数据源连接: id={}", id);

        ReportDatasource datasource = getById(id);
        if (datasource == null) {
            throw new BusinessException("数据源不存在");
        }

        // TODO: 实现实际的连接测试逻辑
        // 根据数据源类型进行连接测试
        switch (datasource.getDatasourceType().toLowerCase()) {
            case "mysql":
            case "postgresql":
                return testJdbcConnection(datasource);
            case "api":
                return testApiConnection(datasource);
            case "elastic":
                return testElasticConnection(datasource);
            default:
                throw new BusinessException("不支持的数据源类型");
        }
    }

    /**
     * 测试JDBC连接
     */
    private boolean testJdbcConnection(ReportDatasource datasource) {
        // TODO: 实现JDBC连接测试
        log.info("测试JDBC连接: type={}", datasource.getDatasourceType());
        return true;
    }

    /**
     * 测试API连接
     */
    private boolean testApiConnection(ReportDatasource datasource) {
        // TODO: 实现API连接测试
        log.info("测试API连接");
        return true;
    }

    /**
     * 测试Elasticsearch连接
     */
    private boolean testElasticConnection(ReportDatasource datasource) {
        // TODO: 实现Elasticsearch连接测试
        log.info("测试Elasticsearch连接");
        return true;
    }

    /**
     * 获取当前租户ID
     */
    private Long getCurrentTenantId() {
        // TODO: 从上下文获取当前租户ID
        return 1L;
    }

    /**
     * 转换为DTO
     */
    private ReportDatasourceDTO convertToDTO(ReportDatasource datasource) {
        ReportDatasourceDTO dto = new ReportDatasourceDTO();
        BeanUtils.copyProperties(datasource, dto);
        return dto;
    }
}
