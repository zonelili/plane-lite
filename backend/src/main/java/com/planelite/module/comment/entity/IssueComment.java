package com.planelite.module.comment.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 问题评论实体
 *
 * @author AI Coding Team
 * @since 2026-03-24
 */
@Data
@TableName("issue_comment")
public class IssueComment implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 评论 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 所属问题 ID
     */
    private Long issueId;

    /**
     * 评论用户 ID
     */
    private Long userId;

    /**
     * 评论内容
     */
    private String content;

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
