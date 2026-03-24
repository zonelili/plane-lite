package com.planelite.module.comment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.planelite.module.comment.entity.IssueComment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 评论 Mapper
 *
 * @author AI Coding Team
 * @since 2026-03-24
 */
@Mapper
public interface CommentMapper extends BaseMapper<IssueComment> {
}
