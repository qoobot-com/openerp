package com.qoobot.qooerp.permission.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.common.util.BeanUtils;
import com.qoobot.qooerp.permission.constants.PermissionConstants;
import com.qoobot.qooerp.permission.dto.PermissionMenuDTO;
import com.qoobot.qooerp.permission.entity.PermissionMenu;
import com.qoobot.qooerp.permission.enums.MenuTypeEnum;
import com.qoobot.qooerp.permission.mapper.PermissionMenuMapper;
import com.qoobot.qooerp.permission.service.PermissionMenuService;
import com.qoobot.qooerp.permission.service.PermissionRoleService;
import com.qoobot.qooerp.permission.util.TreeUtil;
import com.qoobot.qooerp.permission.vo.PermissionMenuVO;
import com.qoobot.qooerp.permission.vo.PermissionTreeVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜单Service实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionMenuServiceImpl extends ServiceImpl<PermissionMenuMapper, PermissionMenu> implements PermissionMenuService {

    private final PermissionRoleService roleService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = PermissionConstants.CACHE_MENU, allEntries = true)
    public Long createMenu(PermissionMenuDTO dto) {
        // 检查父菜单是否存在
        if (dto.getParentId() != null && !dto.getParentId().equals(PermissionConstants.ROOT_ID)) {
            PermissionMenu parent = baseMapper.selectById(dto.getParentId());
            if (parent == null) {
                throw new BusinessException("父菜单不存在");
            }
        }

        // 转换为实体
        PermissionMenu menu = new PermissionMenu();
        BeanUtils.copyProperties(dto, menu);

        // 设置默认值
        if (menu.getSort() == null) {
            menu.setSort(0);
        }
        if (menu.getVisible() == null) {
            menu.setVisible(1);
        }
        if (menu.getIsCache() == null) {
            menu.setIsCache(0);
        }
        if (menu.getIsFrame() == null) {
            menu.setIsFrame(0);
        }

        // 保存菜单
        baseMapper.insert(menu);

        log.info("创建菜单成功: {}", menu);
        return menu.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = PermissionConstants.CACHE_MENU, allEntries = true)
    public Boolean updateMenu(PermissionMenuDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("菜单ID不能为空");
        }

        // 检查菜单是否存在
        PermissionMenu existMenu = baseMapper.selectById(dto.getId());
        if (existMenu == null) {
            throw new BusinessException("菜单不存在");
        }

        // 检查是否将自己设置为父菜单
        if (dto.getId().equals(dto.getParentId())) {
            throw new BusinessException("不能将自己设置为父菜单");
        }

        // 检查父菜单是否存在
        if (dto.getParentId() != null && !dto.getParentId().equals(PermissionConstants.ROOT_ID)) {
            PermissionMenu parent = baseMapper.selectById(dto.getParentId());
            if (parent == null) {
                throw new BusinessException("父菜单不存在");
            }
        }

        // 转换为实体
        PermissionMenu menu = new PermissionMenu();
        BeanUtils.copyProperties(dto, menu);

        // 更新菜单
        baseMapper.updateById(menu);

        log.info("更新菜单成功: {}", menu);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {PermissionConstants.CACHE_MENU, PermissionConstants.CACHE_ROLE_MENU, PermissionConstants.CACHE_USER_MENU}, allEntries = true)
    public Boolean deleteMenu(Long id) {
        if (id == null) {
            throw new BusinessException("菜单ID不能为空");
        }

        // 检查菜单是否存在
        PermissionMenu menu = baseMapper.selectById(id);
        if (menu == null) {
            throw new BusinessException("菜单不存在");
        }

        // 检查是否有子菜单
        LambdaQueryWrapper<PermissionMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PermissionMenu::getParentId, id);
        long count = baseMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException("存在子菜单，不能删除");
        }

        // 删除菜单
        baseMapper.deleteById(id);

        log.info("删除菜单成功: id={}", id);
        return true;
    }

    @Override
    @Cacheable(value = PermissionConstants.CACHE_MENU, key = "#id")
    public PermissionMenuVO getMenuById(Long id) {
        PermissionMenu menu = baseMapper.selectById(id);
        if (menu == null) {
            throw new BusinessException("菜单不存在");
        }

        PermissionMenuVO vo = new PermissionMenuVO();
        BeanUtils.copyProperties(menu, vo);
        vo.setMenuTypeName(MenuTypeEnum.getByCode(menu.getMenuType()).getDesc());

        return vo;
    }

    @Override
    @Cacheable(value = PermissionConstants.CACHE_MENU + ":all")
    public List<PermissionMenuVO> getAllMenus() {
        List<PermissionMenu> menus = baseMapper.selectAllSorted();
        return menus.stream().map(menu -> {
            PermissionMenuVO vo = new PermissionMenuVO();
            BeanUtils.copyProperties(menu, vo);
            vo.setMenuTypeName(MenuTypeEnum.getByCode(menu.getMenuType()).getDesc());
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = PermissionConstants.CACHE_MENU + ":tree")
    public List<PermissionMenuVO> getMenuTree() {
        List<PermissionMenuVO> menus = getAllMenus();
        return TreeUtil.buildMenuTree(menus);
    }

    @Override
    public List<PermissionTreeVO> getMenuTreeForRole(Long roleId) {
        // 获取所有菜单
        List<PermissionMenuVO> menus = getAllMenus();

        // 获取角色的菜单ID列表
        List<Long> menuIds = roleService.getMenuIdsByRoleId(roleId);

        // 构建菜单树
        return TreeUtil.buildTree(menus, menuIds);
    }

    @Override
    @Cacheable(value = PermissionConstants.CACHE_USER_MENU, key = "#userId")
    public List<PermissionMenuVO> getMenuTreeByUserId(Long userId) {
        // 查询用户的菜单
        List<PermissionMenu> menus = baseMapper.selectByUserId(userId);

        // 转换为VO
        List<PermissionMenuVO> menuVOs = menus.stream().map(menu -> {
            PermissionMenuVO vo = new PermissionMenuVO();
            BeanUtils.copyProperties(menu, vo);
            vo.setMenuTypeName(MenuTypeEnum.getByCode(menu.getMenuType()).getDesc());
            return vo;
        }).collect(Collectors.toList());

        // 构建菜单树
        return TreeUtil.buildMenuTree(menuVOs);
    }

    @Override
    public List<PermissionMenu> getMenusByRoleId(Long roleId) {
        return baseMapper.selectByRoleId(roleId);
    }

    @Override
    @CacheEvict(value = {PermissionConstants.CACHE_MENU, PermissionConstants.CACHE_ROLE_MENU, PermissionConstants.CACHE_USER_MENU}, allEntries = true)
    public void clearCache() {
        log.info("菜单缓存已清除");
    }
}
