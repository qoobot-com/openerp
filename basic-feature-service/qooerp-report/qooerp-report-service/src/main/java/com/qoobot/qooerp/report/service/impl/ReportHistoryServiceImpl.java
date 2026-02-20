package com.qoobot.qooerp.report.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.report.dto.ReportHistoryDTO;
import com.qoobot.qooerp.report.dto.ReportHistoryQueryDTO;
import com.qoobot.qooerp.report.entity.ReportHistory;
import com.qoobot.qooerp.report.mapper.ReportHistoryMapper;
import com.qoobot.qooerp.report.service.ReportHistoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ReportHistoryServiceImpl extends ServiceImpl<ReportHistoryMapper, ReportHistory> implements ReportHistoryService {

    @Override
    public ReportHistoryDTO createSnapshot(Long reportId, String snapshotData, String snapshotConfig) {
        ReportHistory history = new ReportHistory();
        history.setReportId(reportId);
        history.setSnapshotData(snapshotData);
        history.setSnapshotConfig(snapshotConfig);
        history.setSnapshotTime(LocalDateTime.now());
        save(history);
        return toDTO(history);
    }

    @Override
    public PageResult<ReportHistoryDTO> queryPage(ReportHistoryQueryDTO dto) {
        LambdaQueryWrapper<ReportHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(dto.getReportId() != null, ReportHistory::getReportId, dto.getReportId())
               .ge(dto.getStartTime() != null, ReportHistory::getSnapshotTime, dto.getStartTime())
               .le(dto.getEndTime() != null, ReportHistory::getSnapshotTime, dto.getEndTime())
               .orderByDesc(ReportHistory::getSnapshotTime);
        
        IPage<ReportHistory> page = page(new Page<>(dto.getPage(), dto.getSize()), wrapper);
        return new PageResult<>(
            page.getCurrent(),
            page.getSize(),
            page.getTotal(),
            page.getRecords().stream().map(this::toDTO).toList()
        );
    }

    @Override
    public ReportHistoryDTO getById(Long id) {
        ReportHistory history = super.getById(id);
        if (history == null) {
            throw new BusinessException("报表历史不存在");
        }
        return toDTO(history);
    }

    @Override
    public void delete(Long id) {
        ReportHistory history = super.getById(id);
        if (history == null) {
            throw new BusinessException("报表历史不存在");
        }
        removeById(id);
    }

    @Override
    public void deleteByReportId(Long reportId) {
        LambdaQueryWrapper<ReportHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ReportHistory::getReportId, reportId);
        remove(wrapper);
    }

    private ReportHistoryDTO toDTO(ReportHistory history) {
        ReportHistoryDTO dto = new ReportHistoryDTO();
        BeanUtils.copyProperties(history, dto);
        return dto;
    }
}
