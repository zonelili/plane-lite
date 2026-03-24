package com.planelite.module.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 创建项目请求 DTO
 *
 * @author AI Coding Team
 * @since 2026-03-24
 */
@Data
@Schema(description = "创建项目请求")
public class ProjectCreateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "项目名称", example = "Plane Lite Development")
    @NotBlank(message = "项目名称不能为空")
    @Size(max = 100, message = "项目名称不能超过 100 个字符")
    private String name;

    @Schema(description = "项目标识符（2-10个大写字母）", example = "PL")
    @NotBlank(message = "项目标识符不能为空")
    @Pattern(regexp = "^[A-Z]{2,10}$", message = "项目标识符必须是 2-10 个大写字母")
    private String identifier;

    @Schema(description = "项目描述", example = "轻量级项目管理系统")
    @Size(max = 500, message = "项目描述不能超过 500 个字符")
    private String description;

    @Schema(description = "所属工作区 ID", example = "1")
    @NotNull(message = "工作区 ID 不能为空")
    private Long workspaceId;

    @Schema(description = "项目负责人 ID（默认为创建者）", example = "1")
    private Long leadId;
}
