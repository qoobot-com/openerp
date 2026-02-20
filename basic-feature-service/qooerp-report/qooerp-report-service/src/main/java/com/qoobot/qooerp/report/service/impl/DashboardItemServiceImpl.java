package com.qoobot.qooerp.report.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.report.dto.DashboardItemCreateDTO;
import com.qoobot.qooerp.report.dto.DashboardItemDTO;
import com.qoobot.qooerp.report.dto.DashboardItemQueryDTO;
import com.qoobot.qooerp.report.dto.DashboardItemUpdateDTO;
import com.qoobot.qooerp.report.entity.DashboardItem;
import com.qoobot.qooerp.report.mapper.DashboardItemMapper;
import com.qoobot.qooerp.report.service.DashboardItemService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DashboardItemServiceImpl extends ServiceImpl<DashboardItemMapper, DashboardItem> implements DashboardItemService {

    @Override
    public DashboardItemDTO create(DashboardItemCreateDTO dto) {
        DashboardItem item = new DashboardItem();
        BeanUtils.copyProperties(dto, item);
        item.setCreateTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());
        save(item);
        return toDTO(item);
    }

    @Override
    public DashboardItemDTO update(Long id, DashboardItemUpdateDTO dto) {
        DashboardItem item = super.getById(id);
        if (item == null) {
            throw new BusinessException("仪表盘项不存在");
        }
        BeanUtils.copyProperties(dto, item);
        item.setUpdateTime(LocalDateTime.now());
        updateById(item);
        return toDTO(item);
    }

    @Override
    public void delete(Long id) {
        DashboardItem item = super.getById(id);
        if (item == null) {
            throw new BusinessException("仪表盘项不存在");
        }
        removeById(id);
    }

    @Override
    public DashboardItemDTO getById(Long id) {
        DashboardItem item = super.getById(id);
        if (item == null) {
            throw new BusinessException("仪表盘项不存在");
        }
        return toDTO(item);
    }

    @Override
    public PageResult<DashboardItemDTO> queryPage(DashboardItemQueryDTO dto) {
        LambdaQueryWrapper<DashboardItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(dto.getDashboardId() != null, DashboardItem::getDashboardId, dto.getDashboardId())
               .eq(dto.getReportId() != null, DashboardItem::getReportId, dto.getReportId())
               .like(dto.getItemName() != null, DashboardItem::getItemName, dto.getItemName())
               .orderByAsc(DashboardItem::getSortOrder);
        
        IPage<DashboardItem> page = page(new Page<>(dto.getPage(), dto.getSize()), wrapper);
        List<DashboardItemDTO> dtoList = page.getRecords().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        return new PageResult<>(page.getCurrent(), page.getSize(), page.getTotal(), dtoList);
    }

    @Override
    public List<DashboardItemDTO> getByDashboardId(Long dashboardId) {
        LambdaQueryWrapper<DashboardItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DashboardItem::getDashboardId, dashboardId)
               .orderByAsc(DashboardItem::getSortOrder);
        
        return list(wrapper).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void updateSort(Long id, Integer sortOrder) {
        DashboardItem item = super.getById(id);
        if (item == null) {
            throw new BusinessException("仪表盘项不存在");
        }
        item.setSortOrder(sortOrder);
        item.setUpdateTime(LocalDateTime.now());
        updateById(item);
    }

    private DashboardItemDTO toDTO(DashboardItem item) {
        DashboardItemDTO dto = new DashboardItemDTO();
        BeanUtils.copyProperties(item, dto);
        return dto;
    }
}
