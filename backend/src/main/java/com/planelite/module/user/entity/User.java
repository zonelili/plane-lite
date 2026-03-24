package com.planelite.module.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户实体
 *
 * @author AI Coding Team
 * @since 2026-03-24
 */
@Data
@TableName("user")
public class User implements Serializable {

    /**
     * 用户ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 密码（BCrypt 加密）
     */
    private String password;

    /**
     * 头像 URL
     */
    private String avatar;

    /**
     * 显示名称
     */
    private String displayName;

    /**
     * 是否激活（0:否, 1:是）
     */
    private Boolean isActive;

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
