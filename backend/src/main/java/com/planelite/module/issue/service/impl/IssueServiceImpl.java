package com.planelite.module.issue.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.planelite.common.exception.BusinessException;
import com.planelite.common.exception.ForbiddenException;
import com.planelite.module.issue.dto.*;
import com.planelite.module.issue.entity.Issue;
import com.planelite.module.issue.enums.IssueStatus;
import com.planelite.module.issue.mapper.IssueMapper;
import com.planelite.module.issue.service.IssueService;
import com.planelite.module.project.entity.Project;
import com.planelite.module.project.mapper.ProjectMapper;
import com.planelite.module.workspace.entity.Workspace;
import com.planelite.module.workspace.mapper.WorkspaceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 问题服务实现类
 *
 * @author AI Coding Team
 * @since 2026-03-24
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IssueServiceImpl implements IssueService {

    private final IssueMapper issueMapper;
    private final ProjectMapper projectMapper;
    private final WorkspaceMapper workspaceMapper;

    private static final int MAX_RETRY_COUNT = 3;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public IssueVO createIssue(IssueCreateDTO dto, Long userId) {
        log.info("创建问题 - title: {}, projectId: {}, userId: {}", dto.getTitle(), dto.getProjectId(), userId);

        // 验证项目存在并获取项目信息
        Project project = projectMapper.selectById(dto.getProjectId());
        if (project == null) {
            throw new BusinessException("项目不存在");
        }

        // 权限检查
        verifyProjectAccess(project, userId);

        // 尝试插入问题，处理并发冲突
        Issue issue = null;
        int retryCount = 0;
        while (retryCount < MAX_RETRY_COUNT) {
            try {
                // 获取下一个 sequence_id
                Integer maxSequenceId = issueMapper.selectMaxSequenceIdByProject(dto.getProjectId());
                Integer nextSequenceId = (maxSequenceId == null) ? 1 : maxSequenceId + 1;

                // 创建 Issue 实体
                issue = new Issue();
                issue.setProjectId(dto.getProjectId());
                issue.setSequenceId(nextSequenceId);
                issue.setTitle(dto.getTitle());
                issue.setDescription(dto.getDescription());
                issue.setPriority(dto.getPriority());
                issue.setStatus(IssueStatus.TODO);
                issue.setReporterId(userId);
                issue.setAssigneeId(dto.getAssigneeId());
                issue.setStartDate(dto.getStartDate());
                issue.setDueDate(dto.getDueDate());

                // 插入数据库
                issueMapper.insert(issue);
                break;
            } catch (DuplicateKeyException e) {
                retryCount++;
                log.warn("问题创建 sequence_id 冲突，重试 {}/{}", retryCount, MAX_RETRY_COUNT);
                if (retryCount >= MAX_RETRY_COUNT) {
                    throw new BusinessException("问题创建失败，请稍后重试");
                }
            }
        }

        // 生成问题编号
        String issueNumber = project.getIdentifier() + "-" + issue.getSequenceId();

        // 转换为 VO
        IssueVO vo = convertToVO(issue);
        vo.setIssueNumber(issueNumber);

        log.info("问题创建成功 - issueId: {}, issueNumber: {}", issue.getId(), issueNumber);
        return vo;
    }

    @Override
    @Transactional(readOnly = true)
    public List<IssueVO> listIssues(IssueQueryDTO query, Long userId) {
        log.info("获取问题列表 - projectId: {}, userId: {}", query.getProjectId(), userId);

        // 验证项目存在并获取项目信息
        Project project = projectMapper.selectById(query.getProjectId());
        if (project == null) {
            throw new BusinessException("项目不存在");
        }

        // 权限检查
        verifyProjectAccess(project, userId);

        // 构建查询条件
        LambdaQueryWrapper<Issue> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Issue::getProjectId, query.getProjectId())
                .eq(query.getStatus() != null, Issue::getStatus, query.getStatus())
                .eq(query.getPriority() != null, Issue::getPriority, query.getPriority())
                .eq(query.getAssigneeId() != null, Issue::getAssigneeId, query.getAssigneeId())
                .eq(query.getReporterId() != null, Issue::getReporterId, query.getReporterId())
                .orderByDesc(Issue::getCreatedAt);

        List<Issue> issues = issueMapper.selectList(wrapper);

        // 转换为 VO（批量生成问题编号）
        return issues.stream()
                .map(issue -> {
                    IssueVO vo = convertToVO(issue);
                    vo.setIssueNumber(project.getIdentifier() + "-" + issue.getSequenceId());
                    return vo;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public IssueVO getIssueById(Long issueId, Long userId) {
        log.info("获取问题详情 - issueId: {}, userId: {}", issueId, userId);

        Issue issue = issueMapper.selectById(issueId);
        if (issue == null) {
            throw new BusinessException("问题不存在");
        }

        // 验证项目访问权限
        Project project = projectMapper.selectById(issue.getProjectId());
        if (project == null) {
            throw new BusinessException("项目不存在");
        }
        verifyProjectAccess(project, userId);

        // 转换为 VO
        IssueVO vo = convertToVO(issue);
        vo.setIssueNumber(project.getIdentifier() + "-" + issue.getSequenceId());

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public IssueVO updateIssue(Long issueId, IssueUpdateDTO dto, Long userId) {
        log.info("更新问题 - issueId: {}, userId: {}", issueId, userId);

        Issue issue = issueMapper.selectById(issueId);
        if (issue == null) {
            throw new BusinessException("问题不存在");
        }

        // 验证项目访问权限
        Project project = projectMapper.selectById(issue.getProjectId());
        if (project == null) {
            throw new BusinessException("项目不存在");
        }
        verifyProjectAccess(project, userId);

        // 更新问题字段
        if (dto.getTitle() != null) {
            issue.setTitle(dto.getTitle());
        }
        if (dto.getDescription() != null) {
            issue.setDescription(dto.getDescription());
        }
        if (dto.getPriority() != null) {
            issue.setPriority(dto.getPriority());
        }
        if (dto.getStatus() != null) {
            issue.setStatus(dto.getStatus());
        }
        if (dto.getAssigneeId() != null) {
            issue.setAssigneeId(dto.getAssigneeId());
        }
        if (dto.getStartDate() != null) {
            issue.setStartDate(dto.getStartDate());
        }
        if (dto.getDueDate() != null) {
            issue.setDueDate(dto.getDueDate());
        }

        issueMapper.updateById(issue);

        // 转换为 VO
        IssueVO vo = convertToVO(issue);
        vo.setIssueNumber(project.getIdentifier() + "-" + issue.getSequenceId());

        log.info("问题更新成功 - issueId: {}", issueId);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteIssue(Long issueId, Long userId) {
        log.info("删除问题 - issueId: {}, userId: {}", issueId, userId);

        Issue issue = issueMapper.selectById(issueId);
        if (issue == null) {
            throw new BusinessException("问题不存在");
        }

        // 验证项目访问权限
        Project project = projectMapper.selectById(issue.getProjectId());
        if (project == null) {
            throw new BusinessException("项目不存在");
        }
        verifyProjectAccess(project, userId);

        issueMapper.deleteById(issueId);

        log.info("问题删除成功 - issueId: {}", issueId);
    }

    @Override
    @Transactional(readOnly = true)
    public IssueBoardVO getBoardData(Long projectId, Long userId) {
        log.info("获取看板数据 - projectId: {}, userId: {}", projectId, userId);

        // 验证项目存在并获取项目信息
        Project project = projectMapper.selectById(projectId);
        if (project == null) {
            throw new BusinessException("项目不存在");
        }

        // 权限检查
        verifyProjectAccess(project, userId);

        // 查询所有问题
        LambdaQueryWrapper<Issue> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Issue::getProjectId, projectId)
                .orderByDesc(Issue::getCreatedAt);
        List<Issue> issues = issueMapper.selectList(wrapper);

        // 按状态分组
        Map<IssueStatus, List<IssueVO>> groupedIssues = issues.stream()
                .collect(Collectors.groupingBy(
                        Issue::getStatus,
                        Collectors.mapping(
                                issue -> {
                                    IssueVO vo = convertToVO(issue);
                                    vo.setIssueNumber(project.getIdentifier() + "-" + issue.getSequenceId());
                                    return vo;
                                },
                                Collectors.toList()
                        )
                ));

        // 构建响应 VO
        IssueBoardVO boardVO = new IssueBoardVO();
        boardVO.setTodo(groupedIssues.getOrDefault(IssueStatus.TODO, Collections.emptyList()));
        boardVO.setInProgress(groupedIssues.getOrDefault(IssueStatus.IN_PROGRESS, Collections.emptyList()));
        boardVO.setDone(groupedIssues.getOrDefault(IssueStatus.DONE, Collections.emptyList()));
        boardVO.setClosed(groupedIssues.getOrDefault(IssueStatus.CLOSED, Collections.emptyList()));

        return boardVO;
    }

    /**
     * 验证用户是否有权限访问项目
     */
    private void verifyProjectAccess(Project project, Long userId) {
        Workspace workspace = workspaceMapper.selectById(project.getWorkspaceId());
        if (workspace == null || !workspace.getOwnerId().equals(userId)) {
            throw new ForbiddenException("无权访问该项目");
        }
    }

    /**
     * 将 Issue 实体转换为 IssueVO
     */
    private IssueVO convertToVO(Issue issue) {
        IssueVO vo = new IssueVO();
        BeanUtils.copyProperties(issue, vo);
        return vo;
    }
}
