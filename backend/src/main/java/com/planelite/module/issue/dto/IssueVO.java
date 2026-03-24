package com.planelite.module.issue.dto;

import com.planelite.module.issue.enums.IssuePriority;
import com.planelite.module.issue.enums.IssueStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 问题响应 VO
 *
 * @author AI Coding Team
 * @since 2026-03-24
 */
@Data
@Schema(description = "问题信息")
public class IssueVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "问题 ID", example = "1")
    private Long id;

    @Schema(description = "问题编号", example = "PL-1")
    private String issueNumber;

    @Schema(description = "项目 ID", example = "1")
    private Long projectId;

    @Schema(description = "问题标题", example = "实现用户登录功能")
    private String title;

    @Schema(description = "问题描述", example = "需要实现用户名密码登录和第三方登录")
    private String description;

    @Schema(description = "优先级", example = "HIGH")
    private IssuePriority priority;

    @Schema(description = "状态", example = "TODO")
    private IssueStatus status;

    @Schema(description = "负责人 ID", example = "2")
    private Long assigneeId;

    @Schema(description = "报告人 ID", example = "1")
    private Long reporterId;

    @Schema(description = "开始日期", example = "2026-03-24")
    private LocalDate startDate;

    @Schema(description = "截止日期", example = "2026-03-25")
    private LocalDate dueDate;

    @Schema(description = "创建时间", example = "2026-03-24T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间", example = "2026-03-24T10:00:00")
    private LocalDateTime updatedAt;
}
