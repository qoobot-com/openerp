package com.qoobot.qooerp.scm.logistics.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.common.util.SecurityUtils;
import com.qoobot.qooerp.scm.constant.ScmConstant;
import com.qoobot.qooerp.scm.logistics.domain.ScmLogistics;
import com.qoobot.qooerp.scm.logistics.domain.ScmLogisticsTracking;
import com.qoobot.qooerp.scm.logistics.dto.LogisticsDTO;
import com.qoobot.qooerp.scm.logistics.dto.LogisticsQueryDTO;
import com.qoobot.qooerp.scm.logistics.mapper.ScmLogisticsMapper;
import com.qoobot.qooerp.scm.logistics.mapper.ScmLogisticsTrackingMapper;
import com.qoobot.qooerp.scm.logistics.service.IScmLogisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 物流Service实现
 *
 * @author QooERP
 * @since 2026-02-18
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ScmLogisticsServiceImpl extends ServiceImpl<ScmLogisticsTrackingMapper, ScmLogisticsTracking> implements IScmLogisticsService {

    private final ScmLogisticsMapper logisticsMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createLogistics(LogisticsDTO dto) {
        String logisticsCode = generateLogisticsCode();

        ScmLogistics exist = getByCode(logisticsCode);
        if (exist != null) {
            throw new BusinessException("物流单号已存在");
        }

        ScmLogistics logistics = BeanUtil.copyProperties(dto, ScmLogistics.class);
        logistics.setLogisticsCode(logisticsCode);
        logistics.setLogisticsStatus(ScmConstant.LOGISTICS_STATUS_PENDING);
        logistics.setTenantId(SecurityUtils.getTenantId());
        logistics.setDeleted(0);

        logisticsMapper.insert(logistics);

        log.info("创建物流单成功，物流ID：{}，物流单号：{}", logistics.getId(), logisticsCode);
        return logistics.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateLogistics(Long id, LogisticsDTO dto) {
        ScmLogistics logistics = logisticsMapper.selectById(id);
        if (logistics == null) {
            throw new BusinessException("物流单不存在");
        }

        ScmLogistics update = BeanUtil.copyProperties(dto, ScmLogistics.class);
        update.setId(id);
        update.setLogisticsCode(logistics.getLogisticsCode());

        int result = logisticsMapper.updateById(update);
        log.info("更新物流单成功，物流ID：{}", id);
        return result > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteLogistics(Long id) {
        ScmLogistics logistics = logisticsMapper.selectById(id);
        if (logistics == null) {
            throw new BusinessException("物流单不存在");
        }

        int result = logisticsMapper.deleteById(id);
        log.info("删除物流单成功，物流ID：{}", id);
        return result > 0;
    }

    @Override
    public LogisticsDTO getLogistics(Long id) {
        ScmLogistics logistics = logisticsMapper.selectById(id);
        if (logistics == null) {
            throw new BusinessException("物流单不存在");
        }

        return BeanUtil.copyProperties(logistics, LogisticsDTO.class);
    }

    @Override
    public PageResult<ScmLogistics> queryLogistics(LogisticsQueryDTO queryDTO) {
        Page<ScmLogistics> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());

        LambdaQueryWrapper<ScmLogistics> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(queryDTO.getLogisticsStatus() != null, ScmLogistics::getLogisticsStatus, queryDTO.getLogisticsStatus())
                .like(queryDTO.getLogisticsCode() != null, ScmLogistics::getLogisticsCode, queryDTO.getLogisticsCode())
                .eq(queryDTO.getBusinessType() != null, ScmLogistics::getBusinessType, queryDTO.getBusinessType())
                .like(queryDTO.getRelatedOrderCode() != null, ScmLogistics::getRelatedOrderCode, queryDTO.getRelatedOrderCode())
                .like(queryDTO.getLogisticsCompany() != null, ScmLogistics::getLogisticsCompany, queryDTO.getLogisticsCompany())
                .orderByDesc(ScmLogistics::getCreateTime);

        IPage<ScmLogistics> result = logisticsMapper.selectPage(page, wrapper);

        return PageResult.of(result.getCurrent(), result.getSize(), result.getTotal(), result.getRecords());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean trackLogistics(Long logisticsId, String status, String location, String description) {
        ScmLogistics logistics = logisticsMapper.selectById(logisticsId);
        if (logistics == null) {
            throw new BusinessException("物流单不存在");
        }

        ScmLogisticsTracking tracking = new ScmLogisticsTracking();
        tracking.setLogisticsId(logisticsId);
        tracking.setLogisticsCode(logistics.getLogisticsCode());
        tracking.setTrackingTime(LocalDateTime.now());
        tracking.setLocation(location);
        tracking.setStatus(status);
        tracking.setDescription(description);
        tracking.setOperator(SecurityUtils.getUsername());
        tracking.setTenantId(SecurityUtils.getTenantId());
        tracking.setDeleted(0);

        baseMapper.insert(tracking);

        logistics.setLogisticsStatus(status);
        logisticsMapper.updateById(logistics);

        log.info("物流跟踪成功，物流ID：{}，状态：{}", logisticsId, status);
        return true;
    }

    @Override
    public List<ScmLogisticsTracking> queryTracking(Long logisticsId) {
        LambdaQueryWrapper<ScmLogisticsTracking> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ScmLogisticsTracking::getLogisticsId, logisticsId)
                .orderByDesc(ScmLogisticsTracking::getTrackingTime);
        return baseMapper.selectList(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatus(Long id, String status) {
        ScmLogistics logistics = logisticsMapper.selectById(id);
        if (logistics == null) {
            throw new BusinessException("物流单不存在");
        }

        logistics.setLogisticsStatus(status);
        int result = logisticsMapper.updateById(logistics);
        log.info("更新物流状态成功，物流ID：{}，状态：{}", id, status);
        return result > 0;
    }

    @Override
    public String generateLogisticsCode() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomNum = String.format("%03d", IdUtil.getSnowflakeNextId() % 1000);
        return "LG" + date + randomNum;
    }

    private ScmLogistics getByCode(String logisticsCode) {
        LambdaQueryWrapper<ScmLogistics> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ScmLogistics::getLogisticsCode, logisticsCode);
        return logisticsMapper.selectOne(wrapper);
    }
}
