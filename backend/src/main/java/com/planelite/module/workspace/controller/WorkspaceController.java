package com.planelite.module.workspace.controller;

import com.planelite.common.result.Result;
import com.planelite.module.workspace.dto.WorkspaceVO;
import com.planelite.module.workspace.service.WorkspaceService;
import com.planelite.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 工作区控制器
 *
 * @author AI Coding Team
 * @since 2026-03-24
 */
@Tag(name = "工作区管理", description = "工作区查询")
@RestController
@RequestMapping("/api/v1/workspaces")
@RequiredArgsConstructor
public class WorkspaceController {

    private final WorkspaceService workspaceService;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "获取当前用户的工作区列表")
    @GetMapping
    public Result<List<WorkspaceVO>> getUserWorkspaces(@RequestHeader("Authorization") String authHeader) {
        Long userId = getUserIdFromToken(authHeader);
        List<WorkspaceVO> workspaces = workspaceService.getUserWorkspaces(userId);
        return Result.success(workspaces);
    }

    @Operation(summary = "获取工作区详情")
    @GetMapping("/{id}")
    public Result<WorkspaceVO> getWorkspaceById(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        Long userId = getUserIdFromToken(authHeader);
        WorkspaceVO workspace = workspaceService.getWorkspaceById(id, userId);
        return Result.success(workspace);
    }

    /**
     * 从 Authorization header 中提取用户 ID
     */
    private Long getUserIdFromToken(String authHeader) {
        String token = extractToken(authHeader);
        if (token == null || !jwtTokenProvider.validateToken(token)) {
            throw new com.planelite.common.exception.UnauthorizedException("Token 无效或已过期");
        }
        return jwtTokenProvider.getUserIdFromToken(token);
    }

    /**
     * 从 Authorization header 中提取 Token
     */
    private String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}
