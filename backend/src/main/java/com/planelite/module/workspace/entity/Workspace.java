package com.planelite.module.workspace.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 工作区实体
 *
 * @author AI Coding Team
 * @since 2026-03-24
 */
@Data
@TableName("workspace")
public class Workspace implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 工作区 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 工作区名称
     */
    private String name;

    /**
     * 工作区标识符（唯一，用于 URL）
     */
    private String slug;

    /**
     * 工作区描述
     */
    private String description;

    /**
     * 所有者 ID
     */
    private Long ownerId;

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
