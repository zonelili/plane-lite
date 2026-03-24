package com.planelite.module.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 评论响应 VO
 *
 * @author AI Coding Team
 * @since 2026-03-24
 */
@Data
@Schema(description = "评论信息")
public class CommentVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "评论 ID", example = "1")
    private Long id;

    @Schema(description = "所属问题 ID", example = "1")
    private Long issueId;

    @Schema(description = "评论用户 ID", example = "1")
    private Long userId;

    @Schema(description = "评论内容", example = "这个问题需要先完成用户认证模块")
    private String content;

    @Schema(description = "创建时间", example = "2026-03-24T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间", example = "2026-03-24T10:00:00")
    private LocalDateTime updatedAt;
}
