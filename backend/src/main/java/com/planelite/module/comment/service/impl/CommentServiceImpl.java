package com.planelite.module.comment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.planelite.common.exception.BusinessException;
import com.planelite.common.exception.ForbiddenException;
import com.planelite.module.comment.dto.CommentCreateDTO;
import com.planelite.module.comment.dto.CommentVO;
import com.planelite.module.comment.entity.IssueComment;
import com.planelite.module.comment.mapper.CommentMapper;
import com.planelite.module.comment.service.CommentService;
import com.planelite.module.issue.entity.Issue;
import com.planelite.module.issue.mapper.IssueMapper;
import com.planelite.module.project.entity.Project;
import com.planelite.module.project.mapper.ProjectMapper;
import com.planelite.module.workspace.entity.Workspace;
import com.planelite.module.workspace.mapper.WorkspaceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 评论服务实现类
 *
 * @author AI Coding Team
 * @since 2026-03-24
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;
    private final IssueMapper issueMapper;
    private final ProjectMapper projectMapper;
    private final WorkspaceMapper workspaceMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CommentVO> listComments(Long issueId, Long userId) {
        log.info("获取评论列表 - issueId: {}, userId: {}", issueId, userId);

        // 1. 验证 issue 存在
        Issue issue = issueMapper.selectById(issueId);
        if (issue == null) {
            throw new BusinessException("问题不存在");
        }

        // 2. 权限检查：用户是否有权访问该 issue
        verifyIssueAccess(issue, userId);

        // 3. 查询评论，按创建时间倒序
        LambdaQueryWrapper<IssueComment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(IssueComment::getIssueId, issueId)
               .orderByDesc(IssueComment::getCreatedAt);

        List<IssueComment> comments = commentMapper.selectList(wrapper);

        // 4. 转换为 VO
        return comments.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommentVO createComment(CommentCreateDTO dto, Long userId) {
        log.info("创建评论 - issueId: {}, userId: {}", dto.getIssueId(), userId);

        // 1. 验证 issue 存在
        Issue issue = issueMapper.selectById(dto.getIssueId());
        if (issue == null) {
            throw new BusinessException("问题不存在");
        }

        // 2. 权限检查：用户是否有权访问该 issue
        verifyIssueAccess(issue, userId);

        // 3. 创建评论
        IssueComment comment = new IssueComment();
        comment.setIssueId(dto.getIssueId());
        comment.setUserId(userId);  // 当前用户
        comment.setContent(dto.getContent());

        commentMapper.insert(comment);

        // 4. 返回 VO
        log.info("评论创建成功 - commentId: {}", comment.getId());
        return convertToVO(comment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteComment(Long commentId, Long userId) {
        log.info("删除评论 - commentId: {}, userId: {}", commentId, userId);

        // 1. 验证评论存在
        IssueComment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new BusinessException("评论不存在");
        }

        // 2. 权限检查：仅评论作者可删除
        if (!comment.getUserId().equals(userId)) {
            throw new ForbiddenException("只能删除自己的评论");
        }

        // 3. 执行删除
        commentMapper.deleteById(commentId);
        log.info("评论删除成功 - commentId: {}", commentId);
    }

    /**
     * 权限验证辅助方法
     */
    private void verifyIssueAccess(Issue issue, Long userId) {
        Project project = projectMapper.selectById(issue.getProjectId());
        if (project == null) {
            throw new BusinessException("项目不存在");
        }

        Workspace workspace = workspaceMapper.selectById(project.getWorkspaceId());
        if (workspace == null || !workspace.getOwnerId().equals(userId)) {
            throw new ForbiddenException("无权访问该问题");
        }
    }

    /**
     * 实体转 VO
     */
    private CommentVO convertToVO(IssueComment comment) {
        CommentVO vo = new CommentVO();
        BeanUtils.copyProperties(comment, vo);
        return vo;
    }
}
