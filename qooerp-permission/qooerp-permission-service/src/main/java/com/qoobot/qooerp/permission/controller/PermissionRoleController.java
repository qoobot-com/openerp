package com.qoobot.qooerp.permission.controller;

import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.permission.dto.PermissionRoleDTO;
import com.qoobot.qooerp.permission.service.PermissionRoleService;
import com.qoobot.qooerp.permission.vo.PermissionRoleVO;
import com.qoobot.qooerp.permission.vo.PermissionTreeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色Controller
 */
@Tag(name = "角色管理")
@RestController
@RequestMapping("/api/permission/role")
@RequiredArgsConstructor
public class PermissionRoleController {

    private final PermissionRoleService roleService;

    /**
     * 创建角色
     */
    @Operation(summary = "创建角色")
    @PostMapping
    public Result<Long> createRole(@Valid @RequestBody PermissionRoleDTO dto) {
        Long id = roleService.createRole(dto);
        return Result.success(id);
    }

    /**
     * 更新角色
     */
    @Operation(summary = "更新角色")
    @PutMapping
    public Result<Boolean> updateRole(@Valid @RequestBody PermissionRoleDTO dto) {
        Boolean result = roleService.updateRole(dto);
        return Result.success(result);
    }

    /**
     * 删除角色
     */
    @Operation(summary = "删除角色")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteRole(@PathVariable Long id) {
        Boolean result = roleService.deleteRole(id);
        return Result.success(result);
    }

    /**
     * 根据ID查询角色
     */
    @Operation(summary = "根据ID查询角色")
    @GetMapping("/{id}")
    public Result<PermissionRoleVO> getRoleById(@PathVariable Long id) {
        PermissionRoleVO vo = roleService.getRoleById(id);
        return Result.success(vo);
    }

    /**
     * 分页查询角色
     */
    @Operation(summary = "分页查询角色")
    @GetMapping("/page")
    public Result<com.baomidou.mybatisplus.core.metadata.IPage<PermissionRoleVO>> pageRoles(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String roleName,
            @RequestParam(required = false) Integer status) {
        com.baomidou.mybatisplus.core.metadata.IPage<PermissionRoleVO> page = roleService.pageRoles(current, size, roleName, status);
        return Result.success(page);
    }

    /**
     * 获取所有角色
     */
    @Operation(summary = "获取所有角色")
    @GetMapping("/list")
    public Result<List<PermissionRoleVO>> getAllRoles() {
        List<PermissionRoleVO> list = roleService.getAllRoles();
        return Result.success(list);
    }

    /**
     * 分配菜单给角色
     */
    @Operation(summary = "分配菜单给角色")
    @PostMapping("/{roleId}/menus")
    public Result<Boolean> assignMenusToRole(@PathVariable Long roleId, @RequestBody List<Long> menuIds) {
        Boolean result = roleService.assignMenusToRole(roleId, menuIds);
        return Result.success(result);
    }

    /**
     * 获取角色的菜单ID列表
     */
    @Operation(summary = "获取角色的菜单ID列表")
    @GetMapping("/{roleId}/menus")
    public Result<List<Long>> getMenuIdsByRoleId(@PathVariable Long roleId) {
        List<Long> menuIds = roleService.getMenuIdsByRoleId(roleId);
        return Result.success(menuIds);
    }
}
