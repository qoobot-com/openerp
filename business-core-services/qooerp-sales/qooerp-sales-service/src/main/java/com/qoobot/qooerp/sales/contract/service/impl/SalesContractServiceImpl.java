package com.qoobot.qooerp.sales.contract.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.common.constant.ErrorCodeConstant;
import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.sales.contract.domain.SalesContract;
import com.qoobot.qooerp.sales.contract.domain.SalesContractDetail;
import com.qoobot.qooerp.sales.contract.dto.ContractDTO;
import com.qoobot.qooerp.sales.contract.dto.ContractDetailDTO;
import com.qoobot.qooerp.sales.contract.dto.ContractQueryDTO;
import com.qoobot.qooerp.sales.contract.mapper.SalesContractDetailMapper;
import com.qoobot.qooerp.sales.contract.mapper.SalesContractMapper;
import com.qoobot.qooerp.sales.contract.service.SalesContractService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * 销售合同 Service 实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SalesContractServiceImpl implements SalesContractService {

    private final SalesContractMapper contractMapper;
    private final SalesContractDetailMapper contractDetailMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ContractDTO createContract(ContractDTO dto) {
        log.info("创建销售合同, customerId: {}", dto.getCustomerId());

        SalesContract contract = new SalesContract();
        BeanUtils.copyProperties(dto, contract);

        // 生成合同编号
        contract.setContractNo(generateContractNo());
        contract.setStatus("DRAFT");
        contract.setPaidAmount(BigDecimal.ZERO);
        contract.setCreateTime(LocalDateTime.now());
        contract.setUpdateTime(LocalDateTime.now());

        contractMapper.insert(contract);

        // 保存明细
        if (dto.getDetails() != null && !dto.getDetails().isEmpty()) {
            BigDecimal totalAmount = BigDecimal.ZERO;
            for (ContractDetailDTO detailDTO : dto.getDetails()) {
                SalesContractDetail detail = new SalesContractDetail();
                BeanUtils.copyProperties(detailDTO, detail);
                detail.setContractId(contract.getId());

                // 计算金额
                BigDecimal quantity = detailDTO.getQuantity() != null ? detailDTO.getQuantity() : BigDecimal.ZERO;
                BigDecimal unitPrice = detailDTO.getUnitPrice() != null ? detailDTO.getUnitPrice() : BigDecimal.ZERO;
                BigDecimal discountRate = detailDTO.getDiscountRate() != null ? detailDTO.getDiscountRate() : BigDecimal.ZERO;

                BigDecimal discountAmount = quantity.multiply(unitPrice).multiply(discountRate).divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);
                BigDecimal amount = quantity.multiply(unitPrice).subtract(discountAmount);

                detail.setDiscountAmount(discountAmount);
                detail.setAmount(amount);

                contractDetailMapper.insert(detail);
                totalAmount = totalAmount.add(amount);
            }
            contract.setContractAmount(totalAmount);
            contractMapper.updateById(contract);
        }

        BeanUtils.copyProperties(contract, dto);
        return dto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ContractDTO updateContract(Long id, ContractDTO dto) {
        log.info("更新销售合同, id: {}", id);

        SalesContract contract = contractMapper.selectById(id);
        if (contract == null) {
            throw new BusinessException(ErrorCodeConstant.NOT_FOUND, "合同不存在");
        }

        if (!"DRAFT".equals(contract.getStatus())) {
            throw new BusinessException(ErrorCodeConstant.BUSINESS_ERROR, "只有草稿状态的合同才能修改");
        }

        BeanUtils.copyProperties(dto, contract);
        contract.setUpdateTime(LocalDateTime.now());
        contractMapper.updateById(contract);

        BeanUtils.copyProperties(contract, dto);
        return dto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteContract(Long id) {
        log.info("删除销售合同, id: {}", id);

        SalesContract contract = contractMapper.selectById(id);
        if (contract == null) {
            throw new BusinessException(ErrorCodeConstant.NOT_FOUND, "合同不存在");
        }

        if (!"DRAFT".equals(contract.getStatus())) {
            throw new BusinessException(ErrorCodeConstant.BUSINESS_ERROR, "只有草稿状态的合同才能删除");
        }

        contractMapper.deleteById(id);

        // 删除明细
        LambdaQueryWrapper<SalesContractDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SalesContractDetail::getContractId, id);
        contractDetailMapper.delete(wrapper);
    }

    @Override
    public ContractDTO getContractById(Long id) {
        SalesContract contract = contractMapper.selectById(id);
        if (contract == null) {
            throw new BusinessException(ErrorCodeConstant.NOT_FOUND, "合同不存在");
        }

        ContractDTO dto = new ContractDTO();
        BeanUtils.copyProperties(contract, dto);

        // 查询明细
        LambdaQueryWrapper<SalesContractDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SalesContractDetail::getContractId, id);
        List<SalesContractDetail> details = contractDetailMapper.selectList(wrapper);
        List<ContractDetailDTO> detailDTOs = details.stream()
                .map(detail -> {
                    ContractDetailDTO detailDTO = new ContractDetailDTO();
                    BeanUtils.copyProperties(detail, detailDTO);
                    return detailDTO;
                })
                .collect(Collectors.toList());
        dto.setDetails(detailDTOs);

        return dto;
    }

    @Override
    public Page<ContractDTO> queryContracts(ContractQueryDTO queryDTO) {
        Page<SalesContract> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        LambdaQueryWrapper<SalesContract> wrapper = new LambdaQueryWrapper<>();

        if (queryDTO.getContractNo() != null && !queryDTO.getContractNo().isEmpty()) {
            wrapper.like(SalesContract::getContractNo, queryDTO.getContractNo());
        }
        if (queryDTO.getCustomerId() != null) {
            wrapper.eq(SalesContract::getCustomerId, queryDTO.getCustomerId());
        }
        if (queryDTO.getStatus() != null && !queryDTO.getStatus().isEmpty()) {
            wrapper.eq(SalesContract::getStatus, queryDTO.getStatus());
        }
        if (queryDTO.getContractType() != null && !queryDTO.getContractType().isEmpty()) {
            wrapper.eq(SalesContract::getContractType, queryDTO.getContractType());
        }

        wrapper.orderByDesc(SalesContract::getCreateTime);
        Page<SalesContract> resultPage = contractMapper.selectPage(page, wrapper);

        Page<ContractDTO> dtoPage = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
        List<ContractDTO> dtoList = resultPage.getRecords().stream()
                .map(contract -> {
                    ContractDTO dto = new ContractDTO();
                    BeanUtils.copyProperties(contract, dto);
                    return dto;
                })
                .collect(Collectors.toList());
        dtoPage.setRecords(dtoList);

        return dtoPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approveContract(Long id, Long approverId, String approveRemark, boolean approved) {
        log.info("审核销售合同, id: {}, approved: {}", id, approved);

        SalesContract contract = contractMapper.selectById(id);
        if (contract == null) {
            throw new BusinessException(ErrorCodeConstant.NOT_FOUND, "合同不存在");
        }

        if (!"DRAFT".equals(contract.getStatus()) && !"PENDING".equals(contract.getStatus())) {
            throw new BusinessException(ErrorCodeConstant.BUSINESS_ERROR, "只有草稿或待审核状态的合同才能审核");
        }

        contract.setApproverId(approverId);
        contract.setApproveTime(LocalDateTime.now());
        contract.setApproveRemark(approveRemark);

        if (approved) {
            contract.setStatus("ACTIVE");
        } else {
            contract.setStatus("REJECTED");
        }

        contract.setUpdateTime(LocalDateTime.now());
        contractMapper.updateById(contract);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void activateContract(Long id) {
        log.info("激活销售合同, id: {}", id);

        SalesContract contract = contractMapper.selectById(id);
        if (contract == null) {
            throw new BusinessException(ErrorCodeConstant.NOT_FOUND, "合同不存在");
        }

        contract.setStatus("ACTIVE");
        contract.setUpdateTime(LocalDateTime.now());
        contractMapper.updateById(contract);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void terminateContract(Long id, String reason) {
        log.info("终止销售合同, id: {}, reason: {}", id, reason);

        SalesContract contract = contractMapper.selectById(id);
        if (contract == null) {
            throw new BusinessException(ErrorCodeConstant.NOT_FOUND, "合同不存在");
        }

        if (!"ACTIVE".equals(contract.getStatus())) {
            throw new BusinessException(ErrorCodeConstant.BUSINESS_ERROR, "只有激活状态的合同才能终止");
        }

        contract.setStatus("TERMINATED");
        contract.setRemark(reason);
        contract.setUpdateTime(LocalDateTime.now());
        contractMapper.updateById(contract);
    }

    @Override
    public String generateContractNo() {
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        Random random = new Random();
        int randomNum = random.nextInt(9000) + 1000;
        return "CT" + dateStr + randomNum;
    }
}
