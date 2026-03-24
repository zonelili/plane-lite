package com.planelite.module.issue.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 问题优先级枚举
 *
 * @author AI Coding Team
 * @since 2026-03-24
 */
@Getter
public enum IssuePriority {

    /**
     * 无优先级
     */
    NONE("none", "无"),

    /**
     * 低优先级
     */
    LOW("low", "低"),

    /**
     * 中等优先级
     */
    MEDIUM("medium", "中"),

    /**
     * 高优先级
     */
    HIGH("high", "高"),

    /**
     * 紧急
     */
    URGENT("urgent", "紧急");

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

    IssuePriority(String value, String label) {
        this.value = value;
        this.label = label;
    }
}
