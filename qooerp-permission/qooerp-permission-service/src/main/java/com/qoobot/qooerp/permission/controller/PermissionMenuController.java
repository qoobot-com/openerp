package com.qoobot.qooerp.permission.controller;

import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.permission.dto.PermissionMenuDTO;
import com.qoobot.qooerp.permission.service.PermissionMenuService;
import com.qoobot.qooerp.permission.vo.PermissionMenuVO;
import com.qoobot.qooerp.permission.vo.PermissionTreeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单Controller
 */
@Tag(name = "菜单管理")
@RestController
@RequestMapping("/api/permission/menu")
@RequiredArgsConstructor
public class PermissionMenuController {

    private final PermissionMenuService menuService;

    /**
     * 创建菜单
     */
    @Operation(summary = "创建菜单")
    @PostMapping
    public Result<Long> createMenu(@Valid @RequestBody PermissionMenuDTO dto) {
        Long id = menuService.createMenu(dto);
        return Result.success(id);
    }

    /**
     * 更新菜单
     */
    @Operation(summary = "更新菜单")
    @PutMapping
    public Result<Boolean> updateMenu(@Valid @RequestBody PermissionMenuDTO dto) {
        Boolean result = menuService.updateMenu(dto);
        return Result.success(result);
    }

    /**
     * 删除菜单
     */
    @Operation(summary = "删除菜单")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteMenu(@PathVariable Long id) {
        Boolean result = menuService.deleteMenu(id);
        return Result.success(result);
    }

    /**
     * 根据ID查询菜单
     */
    @Operation(summary = "根据ID查询菜单")
    @GetMapping("/{id}")
    public Result<PermissionMenuVO> getMenuById(@PathVariable Long id) {
        PermissionMenuVO vo = menuService.getMenuById(id);
        return Result.success(vo);
    }

    /**
     * 获取所有菜单
     */
    @Operation(summary = "获取所有菜单")
    @GetMapping("/list")
    public Result<List<PermissionMenuVO>> getAllMenus() {
        List<PermissionMenuVO> list = menuService.getAllMenus();
        return Result.success(list);
    }

    /**
     * 获取菜单树
     */
    @Operation(summary = "获取菜单树")
    @GetMapping("/tree")
    public Result<List<PermissionMenuVO>> getMenuTree() {
        List<PermissionMenuVO> tree = menuService.getMenuTree();
        return Result.success(tree);
    }

    /**
     * 获取角色的菜单树（用于授权）
     */
    @Operation(summary = "获取角色的菜单树")
    @GetMapping("/tree/role/{roleId}")
    public Result<List<PermissionTreeVO>> getMenuTreeForRole(@PathVariable Long roleId) {
        List<PermissionTreeVO> tree = menuService.getMenuTreeForRole(roleId);
        return Result.success(tree);
    }
}
