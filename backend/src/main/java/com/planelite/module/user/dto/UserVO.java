package com.planelite.module.user.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户视图对象（返回给前端）
 *
 * @author AI Coding Team
 * @since 2026-03-24
 */
@Data
public class UserVO {

    private Long id;
    private String username;
    private String email;
    private String avatar;
    private String displayName;
    private LocalDateTime createdAt;
}
