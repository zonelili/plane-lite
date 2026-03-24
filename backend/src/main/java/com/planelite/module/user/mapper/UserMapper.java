package com.planelite.module.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.planelite.module.user.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户 Mapper
 *
 * @author AI Coding Team
 * @since 2026-03-24
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
