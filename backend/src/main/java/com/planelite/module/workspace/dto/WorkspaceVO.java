package com.planelite.module.workspace.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 工作区视图对象
 *
 * @author AI Coding Team
 * @since 2026-03-24
 */
@Data
@Schema(description = "工作区信息")
public class WorkspaceVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "工作区 ID")
    private Long id;

    @Schema(description = "工作区名称")
    private String name;

    @Schema(description = "工作区标识符")
    private String slug;

    @Schema(description = "工作区描述")
    private String description;

    @Schema(description = "所有者 ID")
    private Long ownerId;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
