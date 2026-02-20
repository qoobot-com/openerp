package com.qoobot.qooerp.sales.receipt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.common.constant.ErrorCodeConstant;
import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.sales.receipt.domain.SalesReceipt;
import com.qoobot.qooerp.sales.receipt.dto.ReceiptDTO;
import com.qoobot.qooerp.sales.receipt.mapper.SalesReceiptMapper;
import com.qoobot.qooerp.sales.receipt.service.SalesReceiptService;
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
 * 销售收款 Service 实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SalesReceiptServiceImpl implements SalesReceiptService {

    private final SalesReceiptMapper receiptMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ReceiptDTO createReceipt(ReceiptDTO dto) {
        log.info("创建收款单, orderId: {}", dto.getOrderId());

        SalesReceipt receipt = new SalesReceipt();
        BeanUtils.copyProperties(dto, receipt);

        receipt.setReceiptNo(generateReceiptNo());
        receipt.setStatus("PENDING");
        receipt.setWriteoffAmount(dto.getReceiptAmount());
        receipt.setCreateTime(LocalDateTime.now());
        receipt.setUpdateTime(LocalDateTime.now());

        receiptMapper.insert(receipt);

        BeanUtils.copyProperties(receipt, dto);
        return dto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ReceiptDTO updateReceipt(Long id, ReceiptDTO dto) {
        log.info("更新收款单, id: {}", id);

        SalesReceipt receipt = receiptMapper.selectById(id);
        if (receipt == null) {
            throw new BusinessException(ErrorCodeConstant.NOT_FOUND.getCode(), "收款单不存在");
        }

        if (!"PENDING".equals(receipt.getStatus())) {
            throw new BusinessException(ErrorCodeConstant.BUSINESS_ERROR.getCode(), "只有待确认状态的收款单才能修改");
        }

        BeanUtils.copyProperties(dto, receipt);
        receipt.setUpdateTime(LocalDateTime.now());
        receiptMapper.updateById(receipt);

        BeanUtils.copyProperties(receipt, dto);
        return dto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteReceipt(Long id) {
        log.info("删除收款单, id: {}", id);

        SalesReceipt receipt = receiptMapper.selectById(id);
        if (receipt == null) {
            throw new BusinessException(ErrorCodeConstant.NOT_FOUND.getCode(), "收款单不存在");
        }

        if (!"PENDING".equals(receipt.getStatus())) {
            throw new BusinessException(ErrorCodeConstant.BUSINESS_ERROR.getCode(), "只有待确认状态的收款单才能删除");
        }

        receiptMapper.deleteById(id);
    }

    @Override
    public ReceiptDTO getReceiptById(Long id) {
        SalesReceipt receipt = receiptMapper.selectById(id);
        if (receipt == null) {
            throw new BusinessException(ErrorCodeConstant.NOT_FOUND.getCode(), "收款单不存在");
        }

        ReceiptDTO dto = new ReceiptDTO();
        BeanUtils.copyProperties(receipt, dto);
        return dto;
    }

    @Override
    public Page<ReceiptDTO> queryReceipts(Long customerId, String status, Integer current, Integer size) {
        Page<SalesReceipt> page = new Page<>(current, size);
        LambdaQueryWrapper<SalesReceipt> wrapper = new LambdaQueryWrapper<>();

        if (customerId != null) {
            wrapper.eq(SalesReceipt::getCustomerId, customerId);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(SalesReceipt::getStatus, status);
        }

        wrapper.orderByDesc(SalesReceipt::getCreateTime);
        Page<SalesReceipt> resultPage = receiptMapper.selectPage(page, wrapper);

        Page<ReceiptDTO> dtoPage = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
        List<ReceiptDTO> dtoList = resultPage.getRecords().stream()
                .map(receipt -> {
                    ReceiptDTO dto = new ReceiptDTO();
                    BeanUtils.copyProperties(receipt, dto);
                    return dto;
                })
                .collect(Collectors.toList());
        dtoPage.setRecords(dtoList);

        return dtoPage;
    }

    @Override
    public List<ReceiptDTO> getReceiptsByOrderId(Long orderId) {
        LambdaQueryWrapper<SalesReceipt> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SalesReceipt::getOrderId, orderId);
        wrapper.orderByDesc(SalesReceipt::getCreateTime);

        List<SalesReceipt> receipts = receiptMapper.selectList(wrapper);
        return receipts.stream()
                .map(receipt -> {
                    ReceiptDTO dto = new ReceiptDTO();
                    BeanUtils.copyProperties(receipt, dto);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmReceipt(Long id, Long receiverId) {
        log.info("确认收款, id: {}, receiverId: {}", id, receiverId);

        SalesReceipt receipt = receiptMapper.selectById(id);
        if (receipt == null) {
            throw new BusinessException(ErrorCodeConstant.NOT_FOUND.getCode(), "收款单不存在");
        }

        if (!"PENDING".equals(receipt.getStatus())) {
            throw new BusinessException(ErrorCodeConstant.BUSINESS_ERROR.getCode(), "收款状态不允许确认");
        }

        receipt.setStatus("CONFIRMED");
        receipt.setReceiverId(receiverId);
        receipt.setUpdateTime(LocalDateTime.now());
        receiptMapper.updateById(receipt);

        // TODO: 更新订单收款状态
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void writeoffReceipt(Long id, Long orderId, BigDecimal amount) {
        log.info("核销应收账款, id: {}, orderId: {}, amount: {}", id, orderId, amount);

        SalesReceipt receipt = receiptMapper.selectById(id);
        if (receipt == null) {
            throw new BusinessException(ErrorCodeConstant.NOT_FOUND.getCode(), "收款单不存在");
        }

        if (!"CONFIRMED".equals(receipt.getStatus())) {
            throw new BusinessException(ErrorCodeConstant.BUSINESS_ERROR.getCode(), "只有已确认的收款单才能核销");
        }

        receipt.setWriteoffOrderId(orderId);
        receipt.setWriteoffAmount(amount);
        receipt.setStatus("WRITEOFF");
        receipt.setUpdateTime(LocalDateTime.now());
        receiptMapper.updateById(receipt);

        // TODO: 更新客户信用额度
        // TODO: 更新订单收款状态
    }

    @Override
    public String generateReceiptNo() {
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        Random random = new Random();
        int randomNum = random.nextInt(9000) + 1000;
        return "RC" + dateStr + randomNum;
    }
}
