package com.planelite.module.project.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 项目实体
 *
 * @author AI Coding Team
 * @since 2026-03-24
 */
@Data
@TableName("project")
public class Project implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 项目 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 项目名称
     */
    private String name;

    /**
     * 项目标识符（用于生成问题编号，如 PL、PROJ）
     */
    private String identifier;

    /**
     * 项目描述
     */
    private String description;

    /**
     * 项目图标（emoji）
     */
    private String icon;

    /**
     * 封面图片 URL
     */
    private String coverImage;

    /**
     * 所属工作区 ID
     */
    private Long workspaceId;

    /**
     * 项目负责人 ID
     */
    private Long leadId;

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

    /**
     * 创建者 ID
     */
    private Long createdBy;
}
