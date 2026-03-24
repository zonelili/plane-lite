package com.planelite.module.project.service;

import com.planelite.module.project.dto.ProjectCreateDTO;
import com.planelite.module.project.dto.ProjectUpdateDTO;
import com.planelite.module.project.dto.ProjectVO;

import java.util.List;

/**
 * 项目服务接口
 *
 * @author AI Coding Team
 * @since 2026-03-24
 */
public interface ProjectService {

    /**
     * 创建项目
     *
     * @param dto 项目信息
     * @param userId 当前用户 ID
     * @return 项目详情
     */
    ProjectVO createProject(ProjectCreateDTO dto, Long userId);

    /**
     * 获取工作区下的项目列表
     *
     * @param workspaceId 工作区 ID
     * @param userId 当前用户 ID
     * @return 项目列表
     */
    List<ProjectVO> getProjectsByWorkspace(Long workspaceId, Long userId);

    /**
     * 获取项目详情
     *
     * @param projectId 项目 ID
     * @param userId 当前用户 ID
     * @return 项目详情
     */
    ProjectVO getProjectById(Long projectId, Long userId);

    /**
     * 更新项目
     *
     * @param projectId 项目 ID
     * @param dto 更新信息
     * @param userId 当前用户 ID
     * @return 更新后的项目详情
     */
    ProjectVO updateProject(Long projectId, ProjectUpdateDTO dto, Long userId);

    /**
     * 删除项目
     *
     * @param projectId 项目 ID
     * @param userId 当前用户 ID
     */
    void deleteProject(Long projectId, Long userId);
}
