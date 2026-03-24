package com.planelite.module.comment.service;

import com.planelite.module.comment.dto.CommentCreateDTO;
import com.planelite.module.comment.dto.CommentVO;

import java.util.List;

/**
 * 评论服务接口
 *
 * @author AI Coding Team
 * @since 2026-03-24
 */
public interface CommentService {

    /**
     * 获取问题的评论列表
     *
     * @param issueId 问题 ID
     * @param userId 当前用户 ID
     * @return 评论列表
     */
    List<CommentVO> listComments(Long issueId, Long userId);

    /**
     * 添加评论
     *
     * @param dto 评论信息
     * @param userId 当前用户 ID（评论作者）
     * @return 评论详情
     */
    CommentVO createComment(CommentCreateDTO dto, Long userId);

    /**
     * 删除评论
     *
     * @param commentId 评论 ID
     * @param userId 当前用户 ID
     */
    void deleteComment(Long commentId, Long userId);
}
