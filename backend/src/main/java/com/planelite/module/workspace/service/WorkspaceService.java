package com.planelite.module.workspace.service;

import com.planelite.module.workspace.dto.WorkspaceVO;
import com.planelite.module.workspace.entity.Workspace;

import java.util.List;

/**
 * 工作区服务接口
 *
 * @author AI Coding Team
 * @since 2026-03-24
 */
public interface WorkspaceService {

    /**
     * 创建默认工作区（用户注册时调用）
     *
     * @param userId 用户 ID
     * @param username 用户名
     * @return 工作区实体
     */
    Workspace createDefaultWorkspace(Long userId, String username);

    /**
     * 获取当前用户的工作区列表
     *
     * @param userId 用户 ID
     * @return 工作区列表
     */
    List<WorkspaceVO> getUserWorkspaces(Long userId);

    /**
     * 获取工作区详情
     *
     * @param workspaceId 工作区 ID
     * @param userId 当前用户 ID
     * @return 工作区详情
     */
    WorkspaceVO getWorkspaceById(Long workspaceId, Long userId);
}
