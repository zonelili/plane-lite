package com.planelite.module.user.controller;

import com.planelite.common.result.Result;
import com.planelite.module.user.dto.LoginResponseDTO;
import com.planelite.module.user.dto.UserLoginDTO;
import com.planelite.module.user.dto.UserRegisterDTO;
import com.planelite.module.user.dto.UserVO;
import com.planelite.module.user.service.UserService;
import com.planelite.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 *
 * @author AI Coding Team
 * @since 2026-03-24
 */
@Tag(name = "用户认证", description = "用户注册、登录、获取当前用户信息")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<UserVO> register(@Valid @RequestBody UserRegisterDTO dto) {
        UserVO userVO = userService.register(dto);
        return Result.success("注册成功", userVO);
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<LoginResponseDTO> login(@Valid @RequestBody UserLoginDTO dto) {
        LoginResponseDTO response = userService.login(dto);
        return Result.success("登录成功", response);
    }

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/me")
    public Result<UserVO> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        // 从 Authorization header 中提取 Token
        String token = extractToken(authHeader);
        if (token == null || !jwtTokenProvider.validateToken(token)) {
            throw new com.planelite.common.exception.UnauthorizedException("Token 无效或已过期");
        }

        // 从 Token 中获取用户 ID
        Long userId = jwtTokenProvider.getUserIdFromToken(token);
        UserVO userVO = userService.getUserById(userId);

        return Result.success(userVO);
    }

    /**
     * 从 Authorization header 中提取 Token
     */
    private String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}
