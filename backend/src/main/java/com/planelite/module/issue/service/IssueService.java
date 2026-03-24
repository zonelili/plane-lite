package com.planelite.module.issue.service;

import com.planelite.module.issue.dto.*;

import java.util.List;

/**
 * 问题服务接口
 *
 * @author AI Coding Team
 * @since 2026-03-24
 */
public interface IssueService {

    /**
     * 创建问题
     *
     * @param dto 问题信息
     * @param userId 当前用户 ID（作为报告人）
     * @return 问题详情
     */
    IssueVO createIssue(IssueCreateDTO dto, Long userId);

    /**
     * 获取问题列表（支持筛选）
     *
     * @param query 查询条件
     * @param userId 当前用户 ID
     * @return 问题列表
     */
    List<IssueVO> listIssues(IssueQueryDTO query, Long userId);

    /**
     * 获取问题详情
     *
     * @param issueId 问题 ID
     * @param userId 当前用户 ID
     * @return 问题详情
     */
    IssueVO getIssueById(Long issueId, Long userId);

    /**
     * 更新问题
     *
     * @param issueId 问题 ID
     * @param dto 更新信息
     * @param userId 当前用户 ID
     * @return 更新后的问题详情
     */
    IssueVO updateIssue(Long issueId, IssueUpdateDTO dto, Long userId);

    /**
     * 删除问题
     *
     * @param issueId 问题 ID
     * @param userId 当前用户 ID
     */
    void deleteIssue(Long issueId, Long userId);

    /**
     * 获取看板数据（按状态分组）
     *
     * @param projectId 项目 ID
     * @param userId 当前用户 ID
     * @return 看板数据
     */
    IssueBoardVO getBoardData(Long projectId, Long userId);
}
