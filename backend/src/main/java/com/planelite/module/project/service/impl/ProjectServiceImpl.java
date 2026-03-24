package com.planelite.module.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.planelite.common.exception.BusinessException;
import com.planelite.common.exception.ForbiddenException;
import com.planelite.module.project.dto.ProjectCreateDTO;
import com.planelite.module.project.dto.ProjectUpdateDTO;
import com.planelite.module.project.dto.ProjectVO;
import com.planelite.module.project.entity.Project;
import com.planelite.module.project.mapper.ProjectMapper;
import com.planelite.module.project.service.ProjectService;
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
 * 项目服务实现类
 *
 * @author AI Coding Team
 * @since 2026-03-24
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectMapper projectMapper;
    private final WorkspaceMapper workspaceMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProjectVO createProject(ProjectCreateDTO dto, Long userId) {
        log.info("创建项目 - name: {}, identifier: {}, workspaceId: {}, userId: {}",
                dto.getName(), dto.getIdentifier(), dto.getWorkspaceId(), userId);

        // 验证工作区是否存在且用户有权限
        Workspace workspace = workspaceMapper.selectById(dto.getWorkspaceId());
        if (workspace == null) {
            throw new BusinessException("工作区不存在");
        }
        if (!workspace.getOwnerId().equals(userId)) {
            throw new ForbiddenException("无权在该工作区创建项目");
        }

        // 检查项目标识符是否在该工作区下已存在
        LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Project::getWorkspaceId, dto.getWorkspaceId())
                .eq(Project::getIdentifier, dto.getIdentifier());
        Project existingProject = projectMapper.selectOne(wrapper);
        if (existingProject != null) {
            throw new BusinessException("项目标识符已存在");
        }

        // 创建项目
        Project project = new Project();
        BeanUtils.copyProperties(dto, project);
        project.setCreatedBy(userId);

        // 如果未指定负责人，默认为创建者
        if (dto.getLeadId() == null) {
            project.setLeadId(userId);
        }

        projectMapper.insert(project);

        log.info("项目创建成功 - projectId: {}", project.getId());
        return convertToVO(project);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectVO> getProjectsByWorkspace(Long workspaceId, Long userId) {
        log.info("获取工作区项目列表 - workspaceId: {}, userId: {}", workspaceId, userId);

        // 验证工作区是否存在且用户有权限
        Workspace workspace = workspaceMapper.selectById(workspaceId);
        if (workspace == null) {
            throw new BusinessException("工作区不存在");
        }
        if (!workspace.getOwnerId().equals(userId)) {
            throw new ForbiddenException("无权访问该工作区");
        }

        LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Project::getWorkspaceId, workspaceId)
                .orderByDesc(Project::getUpdatedAt);

        List<Project> projects = projectMapper.selectList(wrapper);

        return projects.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectVO getProjectById(Long projectId, Long userId) {
        log.info("获取项目详情 - projectId: {}, userId: {}", projectId, userId);

        Project project = projectMapper.selectById(projectId);
        if (project == null) {
            throw new BusinessException("项目不存在");
        }

        // 验证用户权限
        verifyProjectAccess(project, userId);

        return convertToVO(project);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProjectVO updateProject(Long projectId, ProjectUpdateDTO dto, Long userId) {
        log.info("更新项目 - projectId: {}, userId: {}", projectId, userId);

        Project project = projectMapper.selectById(projectId);
        if (project == null) {
            throw new BusinessException("项目不存在");
        }

        // 验证用户权限
        verifyProjectAccess(project, userId);

        // 如果更新标识符，检查是否重复
        if (dto.getIdentifier() != null && !dto.getIdentifier().equals(project.getIdentifier())) {
            LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Project::getWorkspaceId, project.getWorkspaceId())
                    .eq(Project::getIdentifier, dto.getIdentifier())
                    .ne(Project::getId, projectId);
            Project existingProject = projectMapper.selectOne(wrapper);
            if (existingProject != null) {
                throw new BusinessException("项目标识符已存在");
            }
        }

        // 更新项目
        if (dto.getName() != null) {
            project.setName(dto.getName());
        }
        if (dto.getIdentifier() != null) {
            project.setIdentifier(dto.getIdentifier());
        }
        if (dto.getDescription() != null) {
            project.setDescription(dto.getDescription());
        }
        if (dto.getLeadId() != null) {
            project.setLeadId(dto.getLeadId());
        }

        projectMapper.updateById(project);

        log.info("项目更新成功 - projectId: {}", projectId);
        return convertToVO(project);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProject(Long projectId, Long userId) {
        log.info("删除项目 - projectId: {}, userId: {}", projectId, userId);

        Project project = projectMapper.selectById(projectId);
        if (project == null) {
            throw new BusinessException("项目不存在");
        }

        // 验证用户权限
        verifyProjectAccess(project, userId);

        projectMapper.deleteById(projectId);

        log.info("项目删除成功 - projectId: {}", projectId);
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
     * 将 Project 实体转换为 ProjectVO
     */
    private ProjectVO convertToVO(Project project) {
        ProjectVO vo = new ProjectVO();
        BeanUtils.copyProperties(project, vo);
        return vo;
    }
}
