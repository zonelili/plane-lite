package com.planelite.module.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 创建评论请求 DTO
 *
 * @author AI Coding Team
 * @since 2026-03-24
 */
@Data
@Schema(description = "创建评论请求")
public class CommentCreateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "所属问题 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "问题 ID 不能为空")
    private Long issueId;

    @Schema(description = "评论内容", example = "这个问题需要先完成用户认证模块", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "评论内容不能为空")
    @Size(max = 2000, message = "评论内容不能超过 2000 个字符")
    private String content;
}
