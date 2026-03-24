package com.planelite.module.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 登录响应 DTO
 *
 * @author AI Coding Team
 * @since 2026-03-24
 */
@Data
@AllArgsConstructor
public class LoginResponseDTO {

    private String token;
    private UserVO user;
}
