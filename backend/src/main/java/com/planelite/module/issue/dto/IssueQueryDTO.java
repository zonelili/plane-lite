package com.planelite.module.issue.dto;

import com.planelite.module.issue.enums.IssuePriority;
import com.planelite.module.issue.enums.IssueStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 问题查询条件 DTO
 *
 * @author AI Coding Team
 * @since 2026-03-24
 */
@Data
@Schema(description = "问题查询条件")
public class IssueQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "项目 ID（必填）", example = "1")
    @NotNull(message = "项目 ID 不能为空")
    private Long projectId;

    @Schema(description = "状态", example = "TODO")
    private IssueStatus status;

    @Schema(description = "优先级", example = "HIGH")
    private IssuePriority priority;

    @Schema(description = "负责人 ID", example = "2")
    private Long assigneeId;

    @Schema(description = "报告人 ID", example = "1")
    private Long reporterId;
}
