package com.qoobot.qooerp.permission.util;

import com.qoobot.qooerp.permission.vo.PermissionMenuVO;
import com.qoobot.qooerp.permission.vo.PermissionTreeVO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 树形工具类
 */
public class TreeUtil {

    /**
     * 构建菜单树
     *
     * @param menuList 菜单列表
     * @return 菜单树
     */
    public static List<PermissionMenuVO> buildMenuTree(List<PermissionMenuVO> menuList) {
        List<PermissionMenuVO> roots = new ArrayList<>();
        for (PermissionMenuVO menu : menuList) {
            if (menu.getParentId() == null || menu.getParentId() == 0) {
                roots.add(menu);
                buildChildren(menu, menuList);
            }
        }
        return roots;
    }

    /**
     * 递归构建子菜单
     *
     * @param parent 父菜单
     * @param menuList 菜单列表
     */
    private static void buildChildren(PermissionMenuVO parent, List<PermissionMenuVO> menuList) {
        List<PermissionMenuVO> children = menuList.stream()
                .filter(menu -> parent.getId().equals(menu.getParentId()))
                .collect(Collectors.toList());

        parent.setChildren(children.isEmpty() ? null : children);

        for (PermissionMenuVO child : children) {
            buildChildren(child, menuList);
        }
    }

    /**
     * 构建菜单树（用于角色授权）
     *
     * @param menuList 菜单列表
     * @param menuIds 已选中的菜单ID列表
     * @return 菜单树
     */
    public static List<PermissionTreeVO> buildTree(List<PermissionMenuVO> menuList, List<Long> menuIds) {
        List<PermissionTreeVO> result = new ArrayList<>();

        for (PermissionMenuVO menu : menuList) {
            if (menu.getParentId() == null || menu.getParentId() == 0) {
                PermissionTreeVO tree = new PermissionTreeVO();
                tree.setId(menu.getId());
                tree.setLabel(menu.getMenuName());
                tree.setParentId(menu.getParentId());
                tree.setMenuType(menu.getMenuType());
                tree.setChecked(menuIds != null && menuIds.contains(menu.getId()));
                tree.setChildren(buildChildrenTree(menu, menuList, menuIds));
                result.add(tree);
            }
        }

        return result;
    }

    /**
     * 递归构建子菜单树
     */
    private static List<PermissionTreeVO> buildChildrenTree(PermissionMenuVO parent, List<PermissionMenuVO> menuList, List<Long> menuIds) {
        List<PermissionTreeVO> children = new ArrayList<>();

        for (PermissionMenuVO menu : menuList) {
            if (parent.getId().equals(menu.getParentId())) {
                PermissionTreeVO tree = new PermissionTreeVO();
                tree.setId(menu.getId());
                tree.setLabel(menu.getMenuName());
                tree.setParentId(menu.getParentId());
                tree.setMenuType(menu.getMenuType());
                tree.setChecked(menuIds != null && menuIds.contains(menu.getId()));
                tree.setChildren(buildChildrenTree(menu, menuList, menuIds));
                children.add(tree);
            }
        }

        return children.isEmpty() ? null : children;
    }

    /**
     * 获取所有子菜单ID
     *
     * @param menuId 菜单ID
     * @param menuList 菜单列表
     * @return 子菜单ID列表
     */
    public static List<Long> getChildIds(Long menuId, List<PermissionMenuVO> menuList) {
        List<Long> ids = new ArrayList<>();
        for (PermissionMenuVO menu : menuList) {
            if (menuId.equals(menu.getParentId())) {
                ids.add(menu.getId());
                ids.addAll(getChildIds(menu.getId(), menuList));
            }
        }
        return ids;
    }
}
