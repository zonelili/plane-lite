package com.planelite.module.project.controller;

import com.planelite.common.result.Result;
import com.planelite.module.project.dto.ProjectCreateDTO;
import com.planelite.module.project.dto.ProjectUpdateDTO;
import com.planelite.module.project.dto.ProjectVO;
import com.planelite.module.project.service.ProjectService;
import com.planelite.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 项目控制器
 *
 * @author AI Coding Team
 * @since 2026-03-24
 */
@Tag(name = "项目管理", description = "项目的增删改查")
@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "创建项目")
    @PostMapping
    public Result<ProjectVO> createProject(
            @Valid @RequestBody ProjectCreateDTO dto,
            @RequestHeader("Authorization") String authHeader) {
        Long userId = getUserIdFromToken(authHeader);
        ProjectVO project = projectService.createProject(dto, userId);
        return Result.success("项目创建成功", project);
    }

    @Operation(summary = "获取项目列表")
    @GetMapping
    public Result<List<ProjectVO>> getProjects(
            @Parameter(description = "工作区 ID", required = true)
            @RequestParam Long workspaceId,
            @RequestHeader("Authorization") String authHeader) {
        Long userId = getUserIdFromToken(authHeader);
        List<ProjectVO> projects = projectService.getProjectsByWorkspace(workspaceId, userId);
        return Result.success(projects);
    }

    @Operation(summary = "获取项目详情")
    @GetMapping("/{id}")
    public Result<ProjectVO> getProjectById(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        Long userId = getUserIdFromToken(authHeader);
        ProjectVO project = projectService.getProjectById(id, userId);
        return Result.success(project);
    }

    @Operation(summary = "更新项目")
    @PutMapping("/{id}")
    public Result<ProjectVO> updateProject(
            @PathVariable Long id,
            @Valid @RequestBody ProjectUpdateDTO dto,
            @RequestHeader("Authorization") String authHeader) {
        Long userId = getUserIdFromToken(authHeader);
        ProjectVO project = projectService.updateProject(id, dto, userId);
        return Result.success("项目更新成功", project);
    }

    @Operation(summary = "删除项目")
    @DeleteMapping("/{id}")
    public Result<String> deleteProject(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        Long userId = getUserIdFromToken(authHeader);
        projectService.deleteProject(id, userId);
        return Result.success("项目删除成功", null);
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
