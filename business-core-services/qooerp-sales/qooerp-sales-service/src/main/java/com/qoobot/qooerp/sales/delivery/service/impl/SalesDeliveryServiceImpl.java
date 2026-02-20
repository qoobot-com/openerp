package com.qoobot.qooerp.sales.delivery.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.common.constant.ErrorCodeConstant;
import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.sales.delivery.domain.SalesDelivery;
import com.qoobot.qooerp.sales.delivery.dto.DeliveryDTO;
import com.qoobot.qooerp.sales.delivery.mapper.SalesDeliveryMapper;
import com.qoobot.qooerp.sales.delivery.service.SalesDeliveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * 销售发货 Service 实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SalesDeliveryServiceImpl implements SalesDeliveryService {

    private final SalesDeliveryMapper deliveryMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DeliveryDTO createDelivery(DeliveryDTO dto) {
        log.info("创建发货单, orderId: {}", dto.getOrderId());

        SalesDelivery delivery = new SalesDelivery();
        BeanUtils.copyProperties(dto, delivery);

        delivery.setDeliveryNo(generateDeliveryNo());
        delivery.setStatus("PENDING");
        delivery.setCreateTime(LocalDateTime.now());
        delivery.setUpdateTime(LocalDateTime.now());

        deliveryMapper.insert(delivery);

        BeanUtils.copyProperties(delivery, dto);
        return dto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DeliveryDTO updateDelivery(Long id, DeliveryDTO dto) {
        log.info("更新发货单, id: {}", id);

        SalesDelivery delivery = deliveryMapper.selectById(id);
        if (delivery == null) {
            throw new BusinessException(ErrorCodeConstant.NOT_FOUND, "发货单不存在");
        }

        BeanUtils.copyProperties(dto, delivery);
        delivery.setUpdateTime(LocalDateTime.now());
        deliveryMapper.updateById(delivery);

        BeanUtils.copyProperties(delivery, dto);
        return dto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDelivery(Long id) {
        log.info("删除发货单, id: {}", id);

        SalesDelivery delivery = deliveryMapper.selectById(id);
        if (delivery == null) {
            throw new BusinessException(ErrorCodeConstant.NOT_FOUND, "发货单不存在");
        }

        deliveryMapper.deleteById(id);
    }

    @Override
    public DeliveryDTO getDeliveryById(Long id) {
        SalesDelivery delivery = deliveryMapper.selectById(id);
        if (delivery == null) {
            throw new BusinessException(ErrorCodeConstant.NOT_FOUND, "发货单不存在");
        }

        DeliveryDTO dto = new DeliveryDTO();
        BeanUtils.copyProperties(delivery, dto);
        return dto;
    }

    @Override
    public Page<DeliveryDTO> queryDeliveries(Long orderId, Integer current, Integer size) {
        Page<SalesDelivery> page = new Page<>(current, size);
        LambdaQueryWrapper<SalesDelivery> wrapper = new LambdaQueryWrapper<>();

        if (orderId != null) {
            wrapper.eq(SalesDelivery::getOrderId, orderId);
        }

        wrapper.orderByDesc(SalesDelivery::getCreateTime);
        Page<SalesDelivery> resultPage = deliveryMapper.selectPage(page, wrapper);

        Page<DeliveryDTO> dtoPage = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
        List<DeliveryDTO> dtoList = resultPage.getRecords().stream()
                .map(delivery -> {
                    DeliveryDTO dto = new DeliveryDTO();
                    BeanUtils.copyProperties(delivery, dto);
                    return dto;
                })
                .collect(Collectors.toList());
        dtoPage.setRecords(dtoList);

        return dtoPage;
    }

    @Override
    public List<DeliveryDTO> getDeliveriesByOrderId(Long orderId) {
        LambdaQueryWrapper<SalesDelivery> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SalesDelivery::getOrderId, orderId);
        wrapper.orderByDesc(SalesDelivery::getCreateTime);

        List<SalesDelivery> deliveries = deliveryMapper.selectList(wrapper);
        return deliveries.stream()
                .map(delivery -> {
                    DeliveryDTO dto = new DeliveryDTO();
                    BeanUtils.copyProperties(delivery, dto);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmDelivery(Long id) {
        log.info("确认发货, id: {}", id);

        SalesDelivery delivery = deliveryMapper.selectById(id);
        if (delivery == null) {
            throw new BusinessException(ErrorCodeConstant.NOT_FOUND, "发货单不存在");
        }

        if (!"PENDING".equals(delivery.getStatus())) {
            throw new BusinessException(ErrorCodeConstant.BUSINESS_ERROR, "发货状态不允许确认发货");
        }

        delivery.setStatus("SHIPPED");
        delivery.setUpdateTime(LocalDateTime.now());
        deliveryMapper.updateById(delivery);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmReceive(Long id, String receiver) {
        log.info("确认签收, id: {}, receiver: {}", id, receiver);

        SalesDelivery delivery = deliveryMapper.selectById(id);
        if (delivery == null) {
            throw new BusinessException(ErrorCodeConstant.NOT_FOUND, "发货单不存在");
        }

        if (!"SHIPPED".equals(delivery.getStatus())) {
            throw new BusinessException(ErrorCodeConstant.BUSINESS_ERROR, "发货状态不允许确认签收");
        }

        delivery.setStatus("RECEIVED");
        delivery.setReceiver(receiver);
        delivery.setReceiveTime(LocalDateTime.now());
        delivery.setUpdateTime(LocalDateTime.now());
        deliveryMapper.updateById(delivery);
    }

    @Override
    public String generateDeliveryNo() {
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        Random random = new Random();
        int randomNum = random.nextInt(9000) + 1000;
        return "DV" + dateStr + randomNum;
    }
}
