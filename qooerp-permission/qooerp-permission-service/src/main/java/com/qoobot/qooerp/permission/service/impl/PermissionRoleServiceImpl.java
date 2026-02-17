package com.qoobot.qooerp.permission.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.common.util.BeanUtils;
import com.qoobot.qooerp.common.util.SecurityUtils;
import com.qoobot.qooerp.permission.constants.PermissionConstants;
import com.qoobot.qooerp.permission.dto.PermissionRoleDTO;
import com.qoobot.qooerp.permission.entity.PermissionRole;
import com.qoobot.qooerp.permission.entity.PermissionRoleMenu;
import com.qoobot.qooerp.permission.enums.DataScopeEnum;
import com.qoobot.qooerp.permission.enums.RoleTypeEnum;
import com.qoobot.qooerp.permission.enums.StatusEnum;
import com.qoobot.qooerp.permission.exception.RoleAlreadyExistsException;
import com.qoobot.qooerp.permission.mapper.PermissionRoleMapper;
import com.qoobot.qooerp.permission.mapper.PermissionRoleMenuMapper;
import com.qoobot.qooerp.permission.service.PermissionRoleService;
import com.qoobot.qooerp.permission.vo.PermissionRoleVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色Service实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionRoleServiceImpl extends ServiceImpl<PermissionRoleMapper, PermissionRole> implements PermissionRoleService {

    private final PermissionRoleMenuMapper roleMenuMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createRole(PermissionRoleDTO dto) {
        // 检查角色编码是否已存在
        PermissionRole existRole = baseMapper.selectByRoleCode(dto.getRoleCode());
        if (existRole != null) {
            throw new RoleAlreadyExistsException(dto.getRoleCode());
        }

        // 设置租户ID
        if (dto.getTenantId() == null) {
            dto.setTenantId(SecurityUtils.getTenantIdOrDefault(PermissionConstants.DEFAULT_TENANT_ID));
        }

        // 转换为实体
        PermissionRole role = new PermissionRole();
        BeanUtils.copyProperties(dto, role);

        // 保存角色
        baseMapper.insert(role);

        // 分配菜单
        if (!CollectionUtils.isEmpty(dto.getMenuIds())) {
            assignMenusToRole(role.getId(), dto.getMenuIds());
        }

        log.info("创建角色成功: {}", role);
        return role.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {PermissionConstants.CACHE_ROLE, PermissionConstants.CACHE_ROLE_MENU}, allEntries = true)
    public Boolean updateRole(PermissionRoleDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("角色ID不能为空");
        }

        // 检查角色是否存在
        PermissionRole existRole = baseMapper.selectById(dto.getId());
        if (existRole == null) {
            throw new BusinessException("角色不存在");
        }

        // 检查角色编码是否被其他角色使用
        PermissionRole codeRole = baseMapper.selectByRoleCode(dto.getRoleCode());
        if (codeRole != null && !codeRole.getId().equals(dto.getId())) {
            throw new RoleAlreadyExistsException(dto.getRoleCode());
        }

        // 系统角色不能修改类型
        if (RoleTypeEnum.SYSTEM.getCode().equals(existRole.getRoleType()) &&
            !existRole.getRoleType().equals(dto.getRoleType())) {
            throw new BusinessException("系统角色不能修改类型");
        }

        // 转换为实体
        PermissionRole role = new PermissionRole();
        BeanUtils.copyProperties(dto, role);

        // 更新角色
        baseMapper.updateById(role);

        // 重新分配菜单
        if (dto.getMenuIds() != null) {
            assignMenusToRole(role.getId(), dto.getMenuIds());
        }

        log.info("更新角色成功: {}", role);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {PermissionConstants.CACHE_ROLE, PermissionConstants.CACHE_ROLE_MENU}, allEntries = true)
    public Boolean deleteRole(Long id) {
        if (id == null) {
            throw new BusinessException("角色ID不能为空");
        }

        // 检查角色是否存在
        PermissionRole role = baseMapper.selectById(id);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }

        // 系统角色不能删除
        if (RoleTypeEnum.SYSTEM.getCode().equals(role.getRoleType())) {
            throw new BusinessException("系统角色不能删除");
        }

        // 删除角色（软删除）
        baseMapper.deleteById(id);

        // 删除角色菜单关联
        roleMenuMapper.deleteByRoleId(id);

        log.info("删除角色成功: id={}", id);
        return true;
    }

    @Override
    @Cacheable(value = PermissionConstants.CACHE_ROLE, key = "#id")
    public PermissionRoleVO getRoleById(Long id) {
        PermissionRole role = baseMapper.selectById(id);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }

        PermissionRoleVO vo = new PermissionRoleVO();
        BeanUtils.copyProperties(role, vo);

        // 设置枚举名称
        vo.setRoleTypeName(RoleTypeEnum.getByCode(role.getRoleType()).getDesc());
        vo.setDataScopeName(DataScopeEnum.getByCode(role.getDataScope()).getDesc());
        vo.setStatusName(StatusEnum.getByCode(role.getStatus()).getDesc());

        // 查询菜单ID列表
        List<Long> menuIds = getMenuIdsByRoleId(id);
        vo.setMenuIds(menuIds);

        return vo;
    }

    @Override
    public IPage<PermissionRoleVO> pageRoles(Long current, Long size, String roleName, Integer status) {
        // 构建查询条件
        LambdaQueryWrapper<PermissionRole> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(roleName)) {
            wrapper.like(PermissionRole::getRoleName, roleName);
        }
        if (status != null) {
            wrapper.eq(PermissionRole::getStatus, status);
        }
        wrapper.orderByAsc(PermissionRole::getSort);

        // 分页查询
        Page<PermissionRole> page = new Page<>(current, size);
        IPage<PermissionRole> rolePage = baseMapper.selectPage(page, wrapper);

        // 转换为VO
        Page<PermissionRoleVO> voPage = new Page<>(rolePage.getCurrent(), rolePage.getSize(), rolePage.getTotal());
        List<PermissionRoleVO> voList = rolePage.getRecords().stream().map(role -> {
            PermissionRoleVO vo = new PermissionRoleVO();
            BeanUtils.copyProperties(role, vo);
            vo.setRoleTypeName(RoleTypeEnum.getByCode(role.getRoleType()).getDesc());
            vo.setDataScopeName(DataScopeEnum.getByCode(role.getDataScope()).getDesc());
            vo.setStatusName(StatusEnum.getByCode(role.getStatus()).getDesc());
            return vo;
        }).collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    @Override
    @Cacheable(value = PermissionConstants.CACHE_ROLE + ":all")
    public List<PermissionRoleVO> getAllRoles() {
        List<PermissionRole> roles = baseMapper.selectList(null);
        return roles.stream().map(role -> {
            PermissionRoleVO vo = new PermissionRoleVO();
            BeanUtils.copyProperties(role, vo);
            vo.setRoleTypeName(RoleTypeEnum.getByCode(role.getRoleType()).getDesc());
            vo.setDataScopeName(DataScopeEnum.getByCode(role.getDataScope()).getDesc());
            vo.setStatusName(StatusEnum.getByCode(role.getStatus()).getDesc());
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {PermissionConstants.CACHE_ROLE, PermissionConstants.CACHE_ROLE_MENU, PermissionConstants.CACHE_USER_MENU}, allEntries = true)
    public Boolean assignMenusToRole(Long roleId, List<Long> menuIds) {
        if (roleId == null) {
            throw new BusinessException("角色ID不能为空");
        }

        // 删除原有的角色菜单关联
        roleMenuMapper.deleteByRoleId(roleId);

        // 批量插入新的角色菜单关联
        if (!CollectionUtils.isEmpty(menuIds)) {
            for (Long menuId : menuIds) {
                PermissionRoleMenu roleMenu = new PermissionRoleMenu();
                roleMenu.setRoleId(roleId);
                roleMenu.setMenuId(menuId);
                roleMenuMapper.insert(roleMenu);
            }
        }

        log.info("分配菜单给角色成功: roleId={}, menuIds={}", roleId, menuIds);
        return true;
    }

    @Override
    @Cacheable(value = PermissionConstants.CACHE_ROLE_MENU, key = "#roleId")
    public List<Long> getMenuIdsByRoleId(Long roleId) {
        // 从角色菜单关联表查询菜单ID
        return roleMenuMapper.selectList(new LambdaQueryWrapper<PermissionRoleMenu>()
                .eq(PermissionRoleMenu::getRoleId, roleId)
                .select(PermissionRoleMenu::getMenuId))
                .stream()
                .map(PermissionRoleMenu::getMenuId)
                .collect(Collectors.toList());
    }

    @Override
    public List<PermissionRole> getRolesByUserId(Long userId) {
        return baseMapper.selectByUserId(userId);
    }

    @Override
    @CacheEvict(value = {PermissionConstants.CACHE_ROLE, PermissionConstants.CACHE_ROLE_MENU, PermissionConstants.CACHE_USER_MENU}, allEntries = true)
    public void clearCache() {
        log.info("角色缓存已清除");
    }
}
