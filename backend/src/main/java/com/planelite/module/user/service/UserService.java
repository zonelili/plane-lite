package com.planelite.module.user.service;

import com.planelite.module.user.dto.LoginResponseDTO;
import com.planelite.module.user.dto.UserLoginDTO;
import com.planelite.module.user.dto.UserRegisterDTO;
import com.planelite.module.user.dto.UserVO;

/**
 * 用户服务接口
 *
 * @author AI Coding Team
 * @since 2026-03-24
 */
public interface UserService {

    /**
     * 用户注册
     *
     * @param dto 注册信息
     * @return 用户信息
     */
    UserVO register(UserRegisterDTO dto);

    /**
     * 用户登录
     *
     * @param dto 登录信息
     * @return Token 和用户信息
     */
    LoginResponseDTO login(UserLoginDTO dto);

    /**
     * 根据 ID 获取用户信息
     *
     * @param userId 用户 ID
     * @return 用户信息
     */
    UserVO getUserById(Long userId);

    /**
     * 根据邮箱获取用户
     *
     * @param email 邮箱
     * @return 用户实体
     */
    com.planelite.module.user.entity.User getUserByEmail(String email);
}
