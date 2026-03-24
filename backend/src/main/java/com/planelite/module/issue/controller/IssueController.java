package com.planelite.module.issue.controller;

import com.planelite.common.result.Result;
import com.planelite.module.issue.dto.*;
import com.planelite.module.issue.service.IssueService;
import com.planelite.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 问题控制器
 *
 * @author AI Coding Team
 * @since 2026-03-24
 */
@Tag(name = "问题管理", description = "问题的增删改查和看板数据")
@RestController
@RequestMapping("/api/v1/issues")
@RequiredArgsConstructor
public class IssueController {

    private final IssueService issueService;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "创建问题")
    @PostMapping
    public Result<IssueVO> createIssue(
            @Valid @RequestBody IssueCreateDTO dto,
            @RequestHeader("Authorization") String authHeader) {
        Long userId = getUserIdFromToken(authHeader);
        IssueVO issue = issueService.createIssue(dto, userId);
        return Result.success("问题创建成功", issue);
    }

    @Operation(summary = "获取问题列表（支持筛选）")
    @GetMapping
    public Result<List<IssueVO>> listIssues(
            @Valid IssueQueryDTO query,
            @RequestHeader("Authorization") String authHeader) {
        Long userId = getUserIdFromToken(authHeader);
        List<IssueVO> issues = issueService.listIssues(query, userId);
        return Result.success(issues);
    }

    @Operation(summary = "获取问题详情")
    @GetMapping("/{id}")
    public Result<IssueVO> getIssueById(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        Long userId = getUserIdFromToken(authHeader);
        IssueVO issue = issueService.getIssueById(id, userId);
        return Result.success(issue);
    }

    @Operation(summary = "更新问题")
    @PutMapping("/{id}")
    public Result<IssueVO> updateIssue(
            @PathVariable Long id,
            @Valid @RequestBody IssueUpdateDTO dto,
            @RequestHeader("Authorization") String authHeader) {
        Long userId = getUserIdFromToken(authHeader);
        IssueVO issue = issueService.updateIssue(id, dto, userId);
        return Result.success("问题更新成功", issue);
    }

    @Operation(summary = "删除问题")
    @DeleteMapping("/{id}")
    public Result<String> deleteIssue(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        Long userId = getUserIdFromToken(authHeader);
        issueService.deleteIssue(id, userId);
        return Result.success("问题删除成功", null);
    }

    @Operation(summary = "获取看板数据（按状态分组）")
    @GetMapping("/board")
    public Result<IssueBoardVO> getBoardData(
            @Parameter(description = "项目 ID", required = true)
            @RequestParam Long projectId,
            @RequestHeader("Authorization") String authHeader) {
        Long userId = getUserIdFromToken(authHeader);
        IssueBoardVO boardData = issueService.getBoardData(projectId, userId);
        return Result.success(boardData);
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
