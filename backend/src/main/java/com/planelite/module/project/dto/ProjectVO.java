package com.planelite.module.project.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 项目视图对象
 *
 * @author AI Coding Team
 * @since 2026-03-24
 */
@Data
@Schema(description = "项目信息")
public class ProjectVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "项目 ID")
    private Long id;

    @Schema(description = "项目名称")
    private String name;

    @Schema(description = "项目标识符")
    private String identifier;

    @Schema(description = "项目描述")
    private String description;

    @Schema(description = "所属工作区 ID")
    private Long workspaceId;

    @Schema(description = "项目负责人 ID")
    private Long leadId;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    @Schema(description = "创建者 ID")
    private Long createdBy;
}
