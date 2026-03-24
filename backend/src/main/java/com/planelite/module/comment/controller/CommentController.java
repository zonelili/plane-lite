package com.planelite.module.comment.controller;

import com.planelite.common.result.Result;
import com.planelite.module.comment.dto.CommentCreateDTO;
import com.planelite.module.comment.dto.CommentVO;
import com.planelite.module.comment.service.CommentService;
import com.planelite.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 评论控制器
 *
 * @author AI Coding Team
 * @since 2026-03-24
 */
@Tag(name = "评论管理", description = "问题评论的查询、创建、删除")
@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "获取问题的评论列表")
    @GetMapping
    public Result<List<CommentVO>> listComments(
            @Parameter(description = "问题 ID", required = true) @RequestParam Long issueId,
            @RequestHeader("Authorization") String authHeader) {
        Long userId = getUserIdFromToken(authHeader);
        List<CommentVO> comments = commentService.listComments(issueId, userId);
        return Result.success(comments);
    }

    @Operation(summary = "添加评论")
    @PostMapping
    public Result<CommentVO> createComment(
            @Valid @RequestBody CommentCreateDTO dto,
            @RequestHeader("Authorization") String authHeader) {
        Long userId = getUserIdFromToken(authHeader);
        CommentVO comment = commentService.createComment(dto, userId);
        return Result.success("评论创建成功", comment);
    }

    @Operation(summary = "删除评论")
    @DeleteMapping("/{id}")
    public Result<String> deleteComment(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        Long userId = getUserIdFromToken(authHeader);
        commentService.deleteComment(id, userId);
        return Result.success("评论删除成功", null);
    }

    /**
     * 从 Authorization Header 中提取用户 ID
     */
    private Long getUserIdFromToken(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        return jwtTokenProvider.getUserIdFromToken(token);
    }
}
