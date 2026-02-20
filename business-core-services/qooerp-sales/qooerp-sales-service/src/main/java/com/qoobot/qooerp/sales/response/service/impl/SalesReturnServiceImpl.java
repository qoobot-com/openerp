package com.qoobot.qooerp.sales.response.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.common.constant.ErrorCodeConstant;
import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.sales.response.domain.SalesReturn;
import com.qoobot.qooerp.sales.response.dto.ReturnDTO;
import com.qoobot.qooerp.sales.response.mapper.SalesReturnMapper;
import com.qoobot.qooerp.sales.response.service.SalesReturnService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * 销售退货 Service 实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SalesReturnServiceImpl implements SalesReturnService {

    private final SalesReturnMapper returnMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ReturnDTO createReturn(ReturnDTO dto) {
        log.info("创建退货单, orderId: {}", dto.getOrderId());

        SalesReturn salesReturn = new SalesReturn();
        BeanUtils.copyProperties(dto, salesReturn);

        salesReturn.setReturnNo(generateReturnNo());
        salesReturn.setStatus("PENDING");
        salesReturn.setTotalAmount(BigDecimal.ZERO);
        salesReturn.setRefundAmount(BigDecimal.ZERO);
        salesReturn.setCreateTime(LocalDateTime.now());
        salesReturn.setUpdateTime(LocalDateTime.now());

        returnMapper.insert(salesReturn);

        BeanUtils.copyProperties(salesReturn, dto);
        return dto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ReturnDTO updateReturn(Long id, ReturnDTO dto) {
        log.info("更新退货单, id: {}", id);

        SalesReturn salesReturn = returnMapper.selectById(id);
        if (salesReturn == null) {
            throw new BusinessException(ErrorCodeConstant.NOT_FOUND, "退货单不存在");
        }

        if (!"PENDING".equals(salesReturn.getStatus())) {
            throw new BusinessException(ErrorCodeConstant.BUSINESS_ERROR, "只有待审核状态的退货单才能修改");
        }

        BeanUtils.copyProperties(dto, salesReturn);
        salesReturn.setUpdateTime(LocalDateTime.now());
        returnMapper.updateById(salesReturn);

        BeanUtils.copyProperties(salesReturn, dto);
        return dto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteReturn(Long id) {
        log.info("删除退货单, id: {}", id);

        SalesReturn salesReturn = returnMapper.selectById(id);
        if (salesReturn == null) {
            throw new BusinessException(ErrorCodeConstant.NOT_FOUND, "退货单不存在");
        }

        if (!"PENDING".equals(salesReturn.getStatus())) {
            throw new BusinessException(ErrorCodeConstant.BUSINESS_ERROR, "只有待审核状态的退货单才能删除");
        }

        returnMapper.deleteById(id);
    }

    @Override
    public ReturnDTO getReturnById(Long id) {
        SalesReturn salesReturn = returnMapper.selectById(id);
        if (salesReturn == null) {
            throw new BusinessException(ErrorCodeConstant.NOT_FOUND, "退货单不存在");
        }

        ReturnDTO dto = new ReturnDTO();
        BeanUtils.copyProperties(salesReturn, dto);
        return dto;
    }

    @Override
    public Page<ReturnDTO> queryReturns(Long orderId, String status, Integer current, Integer size) {
        Page<SalesReturn> page = new Page<>(current, size);
        LambdaQueryWrapper<SalesReturn> wrapper = new LambdaQueryWrapper<>();

        if (orderId != null) {
            wrapper.eq(SalesReturn::getOrderId, orderId);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(SalesReturn::getStatus, status);
        }

        wrapper.orderByDesc(SalesReturn::getCreateTime);
        Page<SalesReturn> resultPage = returnMapper.selectPage(page, wrapper);

        Page<ReturnDTO> dtoPage = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
        resultPage.getRecords().forEach(salesReturn -> {
            ReturnDTO dto = new ReturnDTO();
            BeanUtils.copyProperties(salesReturn, dto);
            dtoPage.getRecords().add(dto);
        });

        return dtoPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approveReturn(Long id, Long approverId, String approveRemark, boolean approved) {
        log.info("审核退货单, id: {}, approved: {}", id, approved);

        SalesReturn salesReturn = returnMapper.selectById(id);
        if (salesReturn == null) {
            throw new BusinessException(ErrorCodeConstant.NOT_FOUND, "退货单不存在");
        }

        if (!"PENDING".equals(salesReturn.getStatus())) {
            throw new BusinessException(ErrorCodeConstant.BUSINESS_ERROR, "只有待审核状态的退货单才能审核");
        }

        salesReturn.setApproverId(approverId);
        salesReturn.setApproveTime(LocalDateTime.now());
        salesReturn.setApproveRemark(approveRemark);

        if (approved) {
            salesReturn.setStatus("APPROVED");
        } else {
            salesReturn.setStatus("REJECTED");
        }

        salesReturn.setUpdateTime(LocalDateTime.now());
        returnMapper.updateById(salesReturn);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void processReturn(Long id) {
        log.info("处理退货, id: {}", id);

        SalesReturn salesReturn = returnMapper.selectById(id);
        if (salesReturn == null) {
            throw new BusinessException(ErrorCodeConstant.NOT_FOUND, "退货单不存在");
        }

        if (!"APPROVED".equals(salesReturn.getStatus())) {
            throw new BusinessException(ErrorCodeConstant.BUSINESS_ERROR, "只有审核通过的退货单才能处理");
        }

        salesReturn.setStatus("COMPLETED");
        salesReturn.setUpdateTime(LocalDateTime.now());
        returnMapper.updateById(salesReturn);

        // TODO: 调用库存模块增加库存
        // TODO: 调用财务模块处理退款
    }

    @Override
    public String generateReturnNo() {
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        Random random = new Random();
        int randomNum = random.nextInt(9000) + 1000;
        return "RT" + dateStr + randomNum;
    }
}
