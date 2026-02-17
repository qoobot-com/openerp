package com.qoobot.qooerp.organization.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.common.exception.DataNotFoundException;
import com.qoobot.qooerp.common.tenant.TenantContextHolder;
import com.qoobot.qooerp.common.util.BeanUtils;
import com.qoobot.qooerp.common.util.StringUtils;
import com.qoobot.qooerp.organization.dto.OrganizationCompanyDTO;
import com.qoobot.qooerp.organization.dto.OrganizationCompanyVO;
import com.qoobot.qooerp.organization.entity.OrganizationCompany;
import com.qoobot.qooerp.organization.mapper.OrganizationCompanyMapper;
import com.qoobot.qooerp.organization.service.OrganizationCompanyService;
import com.qoobot.qooerp.organization.vo.OrganizationTreeVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 公司服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrganizationCompanyServiceImpl extends ServiceImpl<OrganizationCompanyMapper, OrganizationCompany>
        implements OrganizationCompanyService {

    private final OrganizationCompanyMapper companyMapper;

    private static final Integer MAX_COMPANY_LEVEL = 10;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "companyTree", allEntries = true)
    public Long create(OrganizationCompanyDTO dto) {
        // 校验公司编码唯一性
        LambdaQueryWrapper<OrganizationCompany> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrganizationCompany::getCompanyCode, dto.getCompanyCode());
        if (companyMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("公司编码已存在");
        }

        OrganizationCompany company = BeanUtils.copyBean(dto, OrganizationCompany.class);
        company.setTenantId(TenantContextHolder.getTenantId());

        // 计算公司级别
        if (company.getParentId() != null && company.getParentId() > 0) {
            OrganizationCompany parentCompany = companyMapper.selectById(company.getParentId());
            if (parentCompany == null) {
                throw new DataNotFoundException("父公司不存在");
            }

            company.setCompanyLevel(parentCompany.getCompanyLevel() != null ? parentCompany.getCompanyLevel() + 1 : 2);
        } else {
            company.setCompanyLevel(1);
            company.setParentId(0L);
        }

        // 检查层级深度
        if (company.getCompanyLevel() > MAX_COMPANY_LEVEL) {
            throw new BusinessException("公司层级不能超过" + MAX_COMPANY_LEVEL + "级");
        }

        if (company.getSort() == null) {
            company.setSort(0);
        }

        companyMapper.insert(company);
        log.info("创建公司成功: {}", company);
        return company.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "companyTree", allEntries = true)
    public void update(OrganizationCompanyDTO dto) {
        OrganizationCompany existCompany = companyMapper.selectById(dto.getId());
        if (existCompany == null) {
            throw new DataNotFoundException("公司不存在");
        }

        // 检查公司编码唯一性
        LambdaQueryWrapper<OrganizationCompany> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrganizationCompany::getCompanyCode, dto.getCompanyCode());
        wrapper.ne(OrganizationCompany::getId, dto.getId());
        if (companyMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("公司编码已存在");
        }

        OrganizationCompany company = BeanUtils.copyBean(dto, OrganizationCompany.class);
        companyMapper.updateById(company);
        log.info("更新公司成功: {}", company);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "companyTree", allEntries = true)
    public void delete(Long id) {
        OrganizationCompany company = companyMapper.selectById(id);
        if (company == null) {
            throw new DataNotFoundException("公司不存在");
        }

        // 检查是否有子公司
        LambdaQueryWrapper<OrganizationCompany> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrganizationCompany::getParentId, id);
        if (companyMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("存在子公司，无法删除");
        }

        companyMapper.deleteById(id);
        log.info("删除公司成功: id={}", id);
    }

    @Override
    @Cacheable(value = "companyDetail", key = "#id")
    public OrganizationCompanyVO get(Long id) {
        OrganizationCompany company = companyMapper.selectById(id);
        if (company == null) {
            throw new DataNotFoundException("公司不存在");
        }

        OrganizationCompanyVO vo = BeanUtils.copyBean(company, OrganizationCompanyVO.class);
        vo.setStatusName(company.getStatus() == 1 ? "启用" : "禁用");

        return vo;
    }

    @Override
    public Page<OrganizationCompanyVO> page(Page<OrganizationCompanyDTO> page) {
        LambdaQueryWrapper<OrganizationCompany> wrapper = new LambdaQueryWrapper<>();

        OrganizationCompanyDTO dto = page.getRecords().isEmpty() ? new OrganizationCompanyDTO() : page.getRecords().get(0);

        if (StringUtils.isNotEmpty(dto.getCompanyName())) {
            wrapper.like(OrganizationCompany::getCompanyName, dto.getCompanyName());
        }

        if (StringUtils.isNotEmpty(dto.getCompanyCode())) {
            wrapper.like(OrganizationCompany::getCompanyCode, dto.getCompanyCode());
        }

        if (dto.getParentId() != null) {
            wrapper.eq(OrganizationCompany::getParentId, dto.getParentId());
        }

        if (dto.getStatus() != null) {
            wrapper.eq(OrganizationCompany::getStatus, dto.getStatus());
        }

        wrapper.orderByAsc(OrganizationCompany::getSort)
                .orderByDesc(OrganizationCompany::getCreateTime);

        Page<OrganizationCompany> pageResult = companyMapper.selectPage(page, wrapper);

        // 转换为VO
        List<OrganizationCompanyVO> voList = pageResult.getRecords().stream()
                .map(company -> {
                    OrganizationCompanyVO vo = BeanUtils.copyBean(company, OrganizationCompanyVO.class);
                    vo.setStatusName(company.getStatus() == 1 ? "启用" : "禁用");
                    return vo;
                })
                .collect(Collectors.toList());

        page.setRecords(voList);
        return page;
    }

    @Override
    @Cacheable(value = "companyTree", key = "'root'")
    public List<OrganizationTreeVO> getCompanyTree() {
        // 查询所有公司
        LambdaQueryWrapper<OrganizationCompany> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrganizationCompany::getStatus, 1);
        wrapper.orderByAsc(OrganizationCompany::getSort);
        List<OrganizationCompany> allCompanies = companyMapper.selectList(wrapper);

        // 转换为TreeVO
        List<OrganizationTreeVO> allTreeNodes = allCompanies.stream()
                .map(company -> {
                    OrganizationTreeVO node = new OrganizationTreeVO();
                    node.setId(company.getId());
                    node.setName(company.getCompanyName());
                    node.setNameEn(company.getCompanyNameEn());
                    node.setParentId(company.getParentId());
                    node.setLevel(company.getCompanyLevel());
                    node.setType("company");
                    node.setCompanyId(company.getId());
                    node.setPhone(company.getPhone());
                    node.setEmail(company.getEmail());
                    node.setSort(company.getSort());
                    node.setStatus(company.getStatus());
                    return node;
                })
                .collect(Collectors.toList());

        // 构建树形结构
        return buildTree(allTreeNodes, null);
    }

    /**
     * 递归构建树形结构
     */
    private List<OrganizationTreeVO> buildTree(List<OrganizationTreeVO> nodes, Long parentId) {
        return nodes.stream()
                .filter(node -> {
                    if (parentId == null) {
                        return node.getParentId() == null || node.getParentId() == 0;
                    }
                    return parentId.equals(node.getParentId());
                })
                .map(node -> {
                    List<OrganizationTreeVO> children = buildTree(nodes, node.getId());
                    node.setChildren(children);
                    node.setHasChildren(!children.isEmpty());
                    return node;
                })
                .sorted(Comparator.comparing(OrganizationTreeVO::getSort))
                .collect(Collectors.toList());
    }
}
