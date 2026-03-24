package com.planelite.module.issue.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 问题状态枚举
 *
 * @author AI Coding Team
 * @since 2026-03-24
 */
@Getter
public enum IssueStatus {

    /**
     * 待办
     */
    TODO("todo", "待办"),

    /**
     * 进行中
     */
    IN_PROGRESS("in_progress", "进行中"),

    /**
     * 已完成
     */
    DONE("done", "已完成"),

    /**
     * 已关闭
     */
    CLOSED("closed", "已关闭");

    /**
     * 数据库存储值(与 MySQL ENUM 对应)
     */
    @EnumValue
    @JsonValue
    private final String value;

    /**
     * 显示名称
     */
    private final String label;

    IssueStatus(String value, String label) {
        this.value = value;
        this.label = label;
    }
}
