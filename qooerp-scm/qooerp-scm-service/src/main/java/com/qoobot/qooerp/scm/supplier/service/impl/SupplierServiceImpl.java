package com.qoobot.qooerp.scm.supplier.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.scm.supplier.domain.Supplier;
import com.qoobot.qooerp.scm.supplier.dto.*;
import com.qoobot.qooerp.scm.supplier.mapper.SupplierMapper;
import com.qoobot.qooerp.scm.supplier.service.ISupplierService;
import com.qoobot.qooerp.scm.supplier.evaluation.service.ISupplierEvaluationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 供应商Service实现
 *
 * @author QooERP
 * @since 2026-02-17
 */
@Service
@RequiredArgsConstructor
public class SupplierServiceImpl extends ServiceImpl<SupplierMapper, Supplier> implements ISupplierService {

    private static final String SUPPLIER_CODE_PREFIX = "SUP";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final ISupplierEvaluationService supplierEvaluationService;

    // 临时存储当前排序字段和方向（用于内部方法调用）
    private String currentSortField;
    private String currentSortOrder;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(SupplierDTO dto) {
        // 生成供应商编码
        if (!StringUtils.hasText(dto.getSupplierCode())) {
            dto.setSupplierCode(generateSupplierCode());
        }

        // 校验编码唯一性
        Supplier existSupplier = getByCode(dto.getSupplierCode());
        if (existSupplier != null) {
            throw new BusinessException("供应商编码已存在：" + dto.getSupplierCode());
        }

        Supplier supplier = new Supplier();
        BeanUtils.copyProperties(dto, supplier);

        if (supplier.getStatus() == null) {
            supplier.setStatus("ENABLED");
        }

        save(supplier);
        return supplier.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(SupplierDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("供应商ID不能为空");
        }

        Supplier supplier = getById(dto.getId());
        if (supplier == null) {
            throw new BusinessException("供应商不存在");
        }

        // 如果修改了编码，需要校验唯一性
        if (StringUtils.hasText(dto.getSupplierCode()) &&
            !dto.getSupplierCode().equals(supplier.getSupplierCode())) {
            Supplier existSupplier = getByCode(dto.getSupplierCode());
            if (existSupplier != null && !existSupplier.getId().equals(dto.getId())) {
                throw new BusinessException("供应商编码已存在：" + dto.getSupplierCode());
            }
        }

        BeanUtils.copyProperties(dto, supplier);
        return updateById(supplier);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long id) {
        Supplier supplier = getById(id);
        if (supplier == null) {
            throw new BusinessException("供应商不存在");
        }
        return removeById(id);
    }

    @Override
    public SupplierDTO getDetail(Long id) {
        Supplier supplier = getById(id);
        if (supplier == null) {
            throw new BusinessException("供应商不存在");
        }

        SupplierDTO dto = new SupplierDTO();
        BeanUtils.copyProperties(supplier, dto);
        return dto;
    }

    @Override
    public PageResult<Supplier> queryPage(SupplierQueryDTO queryDTO) {
        LambdaQueryWrapper<Supplier> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(queryDTO.getSupplierCode())) {
            wrapper.like(Supplier::getSupplierCode, queryDTO.getSupplierCode());
        }
        if (StringUtils.hasText(queryDTO.getSupplierName())) {
            wrapper.like(Supplier::getSupplierName, queryDTO.getSupplierName());
        }
        if (StringUtils.hasText(queryDTO.getSupplierType())) {
            wrapper.eq(Supplier::getSupplierType, queryDTO.getSupplierType());
        }
        if (StringUtils.hasText(queryDTO.getCreditRating())) {
            wrapper.eq(Supplier::getCreditRating, queryDTO.getCreditRating());
        }
        if (StringUtils.hasText(queryDTO.getStatus())) {
            wrapper.eq(Supplier::getStatus, queryDTO.getStatus());
        }
        if (StringUtils.hasText(queryDTO.getContactPerson())) {
            wrapper.like(Supplier::getContactPerson, queryDTO.getContactPerson());
        }
        if (StringUtils.hasText(queryDTO.getContactPhone())) {
            wrapper.like(Supplier::getContactPhone, queryDTO.getContactPhone());
        }
        if (StringUtils.hasText(queryDTO.getProvince())) {
            wrapper.eq(Supplier::getProvince, queryDTO.getProvince());
        }
        if (StringUtils.hasText(queryDTO.getCity())) {
            wrapper.eq(Supplier::getCity, queryDTO.getCity());
        }

        wrapper.orderByDesc(Supplier::getCreateTime);

        Page<Supplier> page = new Page<>(queryDTO.getPageNo(), queryDTO.getPageSize());
        Page<Supplier> result = page(page, wrapper);

        return new PageResult<>(page.getCurrent(), page.getSize(), result.getTotal(), result.getRecords());
    }

    @Override
    public Supplier getByCode(String supplierCode) {
        LambdaQueryWrapper<Supplier> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Supplier::getSupplierCode, supplierCode);
        return getOne(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatus(Long id, String status) {
        Supplier supplier = getById(id);
        if (supplier == null) {
            throw new BusinessException("供应商不存在");
        }
        supplier.setStatus(status);
        return updateById(supplier);
    }

    @Override
    public String generateSupplierCode() {
        String dateStr = LocalDateTime.now().format(DATE_FORMATTER);
        String prefix = SUPPLIER_CODE_PREFIX + dateStr;

        LambdaQueryWrapper<Supplier> wrapper = new LambdaQueryWrapper<>();
        wrapper.likeRight(Supplier::getSupplierCode, prefix);
        wrapper.orderByDesc(Supplier::getSupplierCode);
        wrapper.last("LIMIT 1");

        Supplier lastSupplier = getOne(wrapper);

        int seq = 1;
        if (lastSupplier != null && StringUtils.hasText(lastSupplier.getSupplierCode())) {
            String lastCode = lastSupplier.getSupplierCode();
            try {
                String seqStr = lastCode.substring(prefix.length());
                seq = Integer.parseInt(seqStr) + 1;
            } catch (Exception e) {
                // 解析失败，从1开始
            }
        }

        return String.format("%s%04d", prefix, seq);
    }

    @Override
    public PageResult<SupplierWithEvaluationDTO> filterSuppliers(SupplierFilterDTO filterDTO) {
        // 保存排序上下文
        setSortContext(filterDTO.getSortField(), filterDTO.getSortOrder());

        // 构建查询条件
        LambdaQueryWrapper<Supplier> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(filterDTO.getSupplierName())) {
            wrapper.like(Supplier::getSupplierName, filterDTO.getSupplierName());
        }

        if (filterDTO.getSupplierTypes() != null && !filterDTO.getSupplierTypes().isEmpty()) {
            wrapper.in(Supplier::getSupplierType, filterDTO.getSupplierTypes());
        }

        if (filterDTO.getCreditRatings() != null && !filterDTO.getCreditRatings().isEmpty()) {
            wrapper.in(Supplier::getCreditRating, filterDTO.getCreditRatings());
        }

        if (filterDTO.getProvinces() != null && !filterDTO.getProvinces().isEmpty()) {
            wrapper.in(Supplier::getProvince, filterDTO.getProvinces());
        }

        if (filterDTO.getCities() != null && !filterDTO.getCities().isEmpty()) {
            wrapper.in(Supplier::getCity, filterDTO.getCities());
        }

        // 只查询启用的供应商
        wrapper.eq(Supplier::getStatus, "ENABLED");

        // 排序处理
        applySorting(wrapper, filterDTO.getSortField(), filterDTO.getSortOrder());

        // 分页查询
        Page<Supplier> page = new Page<>(filterDTO.getPageNo(), filterDTO.getPageSize());
        Page<Supplier> result = page(page, wrapper);

        // 转换为带评估信息的DTO
        List<SupplierWithEvaluationDTO> dtoList = convertToWithEvaluationDTO(result.getRecords());

        // 根据评估得分筛选（需要在获取评估信息后进行）
        if (filterDTO.getMinEvaluationScore() != null || filterDTO.getMaxEvaluationScore() != null) {
            dtoList = dtoList.stream()
                    .filter(dto -> {
                        if (dto.getLatestEvaluationScore() == null) {
                            return false;
                        }
                        if (filterDTO.getMinEvaluationScore() != null &&
                            dto.getLatestEvaluationScore().compareTo(filterDTO.getMinEvaluationScore()) < 0) {
                            return false;
                        }
                        if (filterDTO.getMaxEvaluationScore() != null &&
                            dto.getLatestEvaluationScore().compareTo(filterDTO.getMaxEvaluationScore()) > 0) {
                            return false;
                        }
                        return true;
                    })
                    .collect(Collectors.toList());
        }

        // 根据评估等级筛选
        if (filterDTO.getEvaluationLevels() != null && !filterDTO.getEvaluationLevels().isEmpty()) {
            dtoList = dtoList.stream()
                    .filter(dto -> dto.getLatestEvaluationLevel() != null &&
                                 filterDTO.getEvaluationLevels().contains(dto.getLatestEvaluationLevel()))
                    .collect(Collectors.toList());
        }

        // 如果有筛选条件，重新计算总数
        long total = filterDTO.getMinEvaluationScore() != null ||
                      filterDTO.getMaxEvaluationScore() != null ||
                      (filterDTO.getEvaluationLevels() != null && !filterDTO.getEvaluationLevels().isEmpty())
                      ? dtoList.size() : result.getTotal();

        return new PageResult<>(page.getCurrent(), page.getSize(), total, dtoList);
    }

    /**
     * 应用排序
     */
    private void applySorting(LambdaQueryWrapper<Supplier> wrapper, String sortField, String sortOrder) {
        boolean isDesc = "desc".equalsIgnoreCase(sortOrder);

        if ("createTime".equals(sortField)) {
            wrapper.orderBy(true, !isDesc, Supplier::getCreateTime);
        } else if ("updateTime".equals(sortField)) {
            wrapper.orderBy(true, !isDesc, Supplier::getUpdateTime);
        } else if ("supplierName".equals(sortField)) {
            wrapper.orderBy(true, !isDesc, Supplier::getSupplierName);
        } else if (!"evaluationScore".equals(sortField)) {
            // 默认按创建时间倒序（evaluationScore需要在内存中排序）
            wrapper.orderByDesc(Supplier::getCreateTime);
        }
    }

    /**
     * 转换为带评估信息的DTO
     */
    private List<SupplierWithEvaluationDTO> convertToWithEvaluationDTO(List<Supplier> suppliers) {
        List<SupplierWithEvaluationDTO> result = new ArrayList<>();

        for (Supplier supplier : suppliers) {
            SupplierWithEvaluationDTO dto = new SupplierWithEvaluationDTO();
            BeanUtils.copyProperties(supplier, dto);

            // 获取最新评估信息
            try {
                com.qoobot.qooerp.scm.supplier.evaluation.dto.SupplierEvaluationDTO latestEvaluation =
                    supplierEvaluationService.getLatestEvaluation(supplier.getId());
                if (latestEvaluation != null) {
                    dto.setLatestEvaluationId(latestEvaluation.getId());
                    dto.setLatestEvaluationScore(latestEvaluation.getTotalScore());
                    dto.setLatestEvaluationLevel(latestEvaluation.getEvaluationLevel());
                    dto.setLatestEvaluationDate(latestEvaluation.getEvaluationDate());
                    dto.setLatestEvaluator(latestEvaluation.getEvaluator());
                }
            } catch (BusinessException e) {
                // 供应商没有评估记录，忽略
            }

            result.add(dto);
        }

        // 如果排序字段是评估得分，在这里进行排序
        if ("evaluationScore".equals(getCurrentSortField())) {
            String sortOrder = getCurrentSortOrder();
            boolean isDesc = "desc".equalsIgnoreCase(sortOrder);
            result.sort((a, b) -> {
                BigDecimal scoreA = a.getLatestEvaluationScore() != null ? a.getLatestEvaluationScore() : BigDecimal.ZERO;
                BigDecimal scoreB = b.getLatestEvaluationScore() != null ? b.getLatestEvaluationScore() : BigDecimal.ZERO;
                return isDesc ? scoreB.compareTo(scoreA) : scoreA.compareTo(scoreB);
            });
        }

        return result;
    }

    private void setSortContext(String sortField, String sortOrder) {
        this.currentSortField = sortField;
        this.currentSortOrder = sortOrder;
    }

    private String getCurrentSortField() {
        return this.currentSortField;
    }

    private String getCurrentSortOrder() {
        return this.currentSortOrder;
    }
}
