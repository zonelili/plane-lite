package com.planelite.module.issue.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 看板数据响应 VO
 *
 * @author AI Coding Team
 * @since 2026-03-24
 */
@Data
@Schema(description = "看板数据（按状态分组的问题列表）")
public class IssueBoardVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "待办问题列表")
    private List<IssueVO> todo;

    @Schema(description = "进行中问题列表")
    private List<IssueVO> inProgress;

    @Schema(description = "已完成问题列表")
    private List<IssueVO> done;

    @Schema(description = "已关闭问题列表")
    private List<IssueVO> closed;
}
