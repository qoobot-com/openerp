package com.qoobot.qooerp.finance.asset.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.common.util.BeanUtils;
import com.qoobot.qooerp.finance.asset.domain.FinanceAsset;
import com.qoobot.qooerp.finance.asset.domain.FinanceAssetDepreciation;
import com.qoobot.qooerp.finance.asset.dto.AssetQueryDTO;
import com.qoobot.qooerp.finance.asset.dto.FinanceAssetDTO;
import com.qoobot.qooerp.finance.asset.mapper.FinanceAssetDepreciationMapper;
import com.qoobot.qooerp.finance.asset.mapper.FinanceAssetMapper;
import com.qoobot.qooerp.finance.asset.service.IFinanceAssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * 固定资产服务实现
 */
@Service
public class FinanceAssetServiceImpl extends ServiceImpl<FinanceAssetMapper, FinanceAsset> implements IFinanceAssetService {

    @Autowired
    private FinanceAssetDepreciationMapper depreciationMapper;

    @Override
    public PageResult<FinanceAsset> queryPage(AssetQueryDTO queryDTO) {
        // 构建查询条件
        LambdaQueryWrapper<FinanceAsset> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(queryDTO.getAssetCode())) {
            wrapper.like(FinanceAsset::getAssetCode, queryDTO.getAssetCode());
        }
        if (StringUtils.hasText(queryDTO.getAssetName())) {
            wrapper.like(FinanceAsset::getAssetName, queryDTO.getAssetName());
        }
        if (StringUtils.hasText(queryDTO.getAssetCategory())) {
            wrapper.eq(FinanceAsset::getCategoryName, queryDTO.getAssetCategory());
        }
        if (queryDTO.getDepartmentId() != null) {
            wrapper.eq(FinanceAsset::getDepartmentId, queryDTO.getDepartmentId());
        }
        if (queryDTO.getPurchaseStartDate() != null) {
            wrapper.ge(FinanceAsset::getEntryDate, queryDTO.getPurchaseStartDate());
        }
        if (queryDTO.getPurchaseEndDate() != null) {
            wrapper.le(FinanceAsset::getEntryDate, queryDTO.getPurchaseEndDate());
        }
        if (StringUtils.hasText(queryDTO.getAssetStatus())) {
            wrapper.eq(FinanceAsset::getStatus, queryDTO.getAssetStatus());
        }
        
        wrapper.orderByDesc(FinanceAsset::getEntryDate);
        
        // 分页查询
        Page<FinanceAsset> page = new Page<>(queryDTO.getPageNo(), queryDTO.getPageSize());
        page = page(page, wrapper);
        
        return PageResult.of(page.getCurrent(), page.getSize(), page.getTotal(), page.getRecords());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(FinanceAssetDTO dto) {
        // 检查编码是否重复
        LambdaQueryWrapper<FinanceAsset> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FinanceAsset::getAssetCode, dto.getAssetCode());
        if (count(wrapper) > 0) {
            throw new BusinessException("资产编码已存在");
        }
        
        FinanceAsset asset = new FinanceAsset();
        BeanUtils.copyProperties(dto, asset);
        
        // 设置初始状态
        asset.setStatus("NORMAL");
        asset.setDepreciationStatus("NORMAL");
        asset.setAccumulatedDepreciation(BigDecimal.ZERO);
        if (asset.getNetValue() == null) {
            asset.setNetValue(asset.getOriginalValue());
        }
        
        save(asset);
        return asset.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean update(FinanceAssetDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("资产ID不能为空");
        }
        
        FinanceAsset existAsset = getById(dto.getId());
        if (existAsset == null) {
            throw new BusinessException("资产不存在");
        }
        
        // 检查编码是否被其他资产使用
        if (!existAsset.getAssetCode().equals(dto.getAssetCode())) {
            LambdaQueryWrapper<FinanceAsset> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(FinanceAsset::getAssetCode, dto.getAssetCode())
                   .ne(FinanceAsset::getId, dto.getId());
            if (count(wrapper) > 0) {
                throw new BusinessException("资产编码已被其他资产使用");
            }
        }
        
        FinanceAsset asset = new FinanceAsset();
        BeanUtils.copyProperties(dto, asset);
        
        return updateById(asset);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean delete(Long id) {
        if (id == null) {
            throw new BusinessException("资产ID不能为空");
        }
        
        FinanceAsset asset = getById(id);
        if (asset == null) {
            throw new BusinessException("资产不存在");
        }
        
        if (!"NORMAL".equals(asset.getStatus())) {
            throw new BusinessException("只能删除正常状态的资产");
        }
        
        return removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean transfer(Long id, Long departmentId, String departmentName, Long userId, String userName, String location) {
        FinanceAsset asset = getById(id);
        if (asset == null) {
            throw new BusinessException("资产不存在");
        }
        if (!"NORMAL".equals(asset.getStatus())) {
            throw new BusinessException("只有正常状态的资产才能调拨");
        }

        asset.setDepartmentId(departmentId);
        asset.setDepartmentName(departmentName);
        asset.setUserId(userId);
        asset.setUserName(userName);
        if (StringUtils.hasText(location)) {
            asset.setLocation(location);
        }
        asset.setStatus("TRANSFERRED");

        return updateById(asset);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean scrap(Long id) {
        FinanceAsset asset = getById(id);
        if (asset == null) {
            throw new BusinessException("资产不存在");
        }
        if (!"NORMAL".equals(asset.getStatus())) {
            throw new BusinessException("只有正常状态的资产才能报废");
        }

        asset.setStatus("SCRAPPED");
        asset.setDepreciationStatus("COMPLETED");
        asset.setRemark(asset.getRemark() != null ? asset.getRemark() + " [已报废]" : "已报废");

        return updateById(asset);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FinanceAssetDepreciation calculateDepreciation(Long assetId, String period) {
        FinanceAsset asset = getById(assetId);
        if (asset == null) {
            throw new BusinessException("资产不存在");
        }
        if (!"NORMAL".equals(asset.getStatus())) {
            throw new BusinessException("只能计算正常状态资产的折旧");
        }
        if (!"NORMAL".equals(asset.getDepreciationStatus())) {
            throw new BusinessException("资产折旧状态不正常");
        }

        YearMonth periodMonth = YearMonth.parse(period);
        BigDecimal depreciationAmount = calculateAssetDepreciation(asset, periodMonth);

        // 更新累计折旧和净值
        asset.setAccumulatedDepreciation(
            asset.getAccumulatedDepreciation().add(depreciationAmount)
        );
        asset.setNetValue(
            asset.getOriginalValue().subtract(asset.getAccumulatedDepreciation())
        );
        updateById(asset);

        // 保存折旧记录
        FinanceAssetDepreciation depreciation = new FinanceAssetDepreciation();
        depreciation.setAssetId(asset.getId());
        depreciation.setDepreciationPeriod(period);
        depreciation.setDepreciationAmount(depreciationAmount);
        depreciation.setAccumulatedDepreciation(asset.getAccumulatedDepreciation());
        depreciation.setNetValue(asset.getNetValue());
        depreciationMapper.insert(depreciation);

        return depreciation;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer batchCalculateDepreciation(String period) {
        YearMonth periodMonth = YearMonth.parse(period);
        
        // 查询所有正常状态的资产
        List<FinanceAsset> assets = list(new LambdaQueryWrapper<FinanceAsset>()
            .eq(FinanceAsset::getStatus, "NORMAL")
            .eq(FinanceAsset::getDepreciationStatus, "NORMAL"));

        int count = 0;
        for (FinanceAsset asset : assets) {
            try {
                calculateDepreciation(asset.getId(), period);
                count++;
            } catch (Exception e) {
                // 记录错误但继续处理其他资产
                continue;
            }
        }

        return count;
    }

    @Override
    public List<FinanceAssetDepreciation> getDepreciationHistory(Long assetId) {
        return depreciationMapper.selectList(
            new LambdaQueryWrapper<FinanceAssetDepreciation>()
                .eq(FinanceAssetDepreciation::getAssetId, assetId)
                .orderByDesc(FinanceAssetDepreciation::getDepreciationPeriod)
        );
    }

    /**
     * 计算单个资产折旧
     */
    private BigDecimal calculateAssetDepreciation(FinanceAsset asset, YearMonth period) {
        BigDecimal originalValue = asset.getOriginalValue();
        Integer usefulLife = asset.getUsefulLife(); // 年
        String depreciationMethod = asset.getDepreciationMethod();

        if (usefulLife == null || usefulLife <= 0) {
            throw new BusinessException("资产使用年限无效");
        }

        LocalDate startDate = asset.getStartDate();
        if (startDate == null) {
            startDate = asset.getEntryDate();
        }
        
        // 计算使用月数
        int monthsUsed = (int) ChronoUnit.MONTHS.between(YearMonth.from(startDate), period);

        if (monthsUsed >= usefulLife * 12) {
            // 已提足折旧
            return BigDecimal.ZERO;
        }

        int usefulLifeMonths = usefulLife * 12;
        BigDecimal depreciationAmount;
        
        // 根据折旧方法计算
        if ("STRAIGHT_LINE".equals(depreciationMethod)) {
            // 直线法：原值 / 使用月数
            depreciationAmount = originalValue.divide(
                BigDecimal.valueOf(usefulLifeMonths), 2, RoundingMode.HALF_UP
            );
        } else if ("DOUBLE_DECLINING".equals(depreciationMethod)) {
            // 双倍余额递减法：(原值 - 累计折旧) * 2 / 使用月数
            BigDecimal remainingValue = originalValue.subtract(asset.getAccumulatedDepreciation());
            depreciationAmount = remainingValue
                .multiply(BigDecimal.valueOf(2))
                .divide(BigDecimal.valueOf(usefulLifeMonths), 2, RoundingMode.HALF_UP);
        } else if ("SUM_OF_YEARS".equals(depreciationMethod)) {
            // 年数总和法：原值 * 剩余月数 / 年数总和
            int remainingMonths = usefulLifeMonths - monthsUsed;
            int sumOfYearNumbers = usefulLifeMonths * (usefulLifeMonths + 1) / 2;
            depreciationAmount = originalValue
                .multiply(BigDecimal.valueOf(remainingMonths))
                .divide(BigDecimal.valueOf(sumOfYearNumbers), 2, RoundingMode.HALF_UP);
        } else {
            // 默认使用直线法
            depreciationAmount = originalValue.divide(
                BigDecimal.valueOf(usefulLifeMonths), 2, RoundingMode.HALF_UP
            );
        }

        return depreciationAmount;
    }
}
