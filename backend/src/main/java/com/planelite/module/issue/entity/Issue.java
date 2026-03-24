package com.planelite.module.issue.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.planelite.module.issue.enums.IssuePriority;
import com.planelite.module.issue.enums.IssueStatus;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 问题实体
 *
 * @author AI Coding Team
 * @since 2026-03-24
 */
@Data
@TableName("issue")
public class Issue implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 问题 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 所属项目 ID
     */
    private Long projectId;

    /**
     * 项目内序号(用于生成问题编号)
     */
    private Integer sequenceId;

    /**
     * 问题标题
     */
    private String title;

    /**
     * 问题描述
     */
    private String description;

    /**
     * 优先级
     */
    private IssuePriority priority;

    /**
     * 状态
     */
    private IssueStatus status;

    /**
     * 负责人 ID
     */
    private Long assigneeId;

    /**
     * 报告人 ID
     */
    private Long reporterId;

    /**
     * 开始日期
     */
    private LocalDate startDate;

    /**
     * 截止日期
     */
    private LocalDate dueDate;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
