package com.planelite.module.issue.dto;

import com.planelite.module.issue.enums.IssuePriority;
import com.planelite.module.issue.enums.IssueStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 更新问题请求 DTO
 *
 * @author AI Coding Team
 * @since 2026-03-24
 */
@Data
@Schema(description = "更新问题请求")
public class IssueUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "问题标题", example = "实现用户登录功能")
    @Size(max = 255, message = "问题标题不能超过 255 个字符")
    private String title;

    @Schema(description = "问题描述", example = "需要实现用户名密码登录和第三方登录")
    private String description;

    @Schema(description = "优先级", example = "HIGH")
    private IssuePriority priority;

    @Schema(description = "状态", example = "IN_PROGRESS")
    private IssueStatus status;

    @Schema(description = "负责人 ID", example = "2")
    private Long assigneeId;

    @Schema(description = "开始日期", example = "2026-03-24")
    private LocalDate startDate;

    @Schema(description = "截止日期", example = "2026-03-25")
    private LocalDate dueDate;
}
