package com.planelite.module.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.planelite.common.exception.BusinessException;
import com.planelite.common.exception.UnauthorizedException;
import com.planelite.module.user.dto.LoginResponseDTO;
import com.planelite.module.user.dto.UserLoginDTO;
import com.planelite.module.user.dto.UserRegisterDTO;
import com.planelite.module.user.dto.UserVO;
import com.planelite.module.user.entity.User;
import com.planelite.module.user.mapper.UserMapper;
import com.planelite.module.user.service.UserService;
import com.planelite.security.JwtTokenProvider;
import com.planelite.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户服务实现类
 *
 * @author AI Coding Team
 * @since 2026-03-24
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserVO register(UserRegisterDTO dto) {
        log.info("用户注册 - email: {}, username: {}", dto.getEmail(), dto.getUsername());

        // 检查邮箱是否已存在
        User existingUserByEmail = getUserByEmail(dto.getEmail());
        if (existingUserByEmail != null) {
            throw new BusinessException("邮箱已被注册");
        }

        // 检查用户名是否已存在
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, dto.getUsername());
        User existingUserByUsername = userMapper.selectOne(wrapper);
        if (existingUserByUsername != null) {
            throw new BusinessException("用户名已被使用");
        }

        // 创建用户
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(PasswordUtil.encode(dto.getPassword()));
        user.setIsActive(true);

        userMapper.insert(user);

        log.info("用户注册成功 - userId: {}", user.getId());
        return convertToVO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResponseDTO login(UserLoginDTO dto) {
        log.info("用户登录 - email: {}", dto.getEmail());

        // 查询用户
        User user = getUserByEmail(dto.getEmail());
        if (user == null) {
            throw new UnauthorizedException("邮箱或密码错误");
        }

        // 验证密码
        if (!PasswordUtil.matches(dto.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("邮箱或密码错误");
        }

        // 检查账号是否激活
        if (!user.getIsActive()) {
            throw new BusinessException("账号已被禁用");
        }

        // 生成 Token
        String token = jwtTokenProvider.generateToken(
                user.getId(),
                user.getUsername(),
                user.getEmail()
        );

        log.info("用户登录成功 - userId: {}", user.getId());
        return new LoginResponseDTO(token, convertToVO(user));
    }

    @Override
    @Transactional(readOnly = true)
    public UserVO getUserById(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return convertToVO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getEmail, email);
        return userMapper.selectOne(wrapper);
    }

    /**
     * 将 User 实体转换为 UserVO
     */
    private UserVO convertToVO(User user) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }
}
