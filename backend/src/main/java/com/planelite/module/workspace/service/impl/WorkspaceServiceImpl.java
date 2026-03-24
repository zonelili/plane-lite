package com.planelite.module.workspace.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.planelite.common.exception.BusinessException;
import com.planelite.common.exception.ForbiddenException;
import com.planelite.module.workspace.dto.WorkspaceVO;
import com.planelite.module.workspace.entity.Workspace;
import com.planelite.module.workspace.mapper.WorkspaceMapper;
import com.planelite.module.workspace.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 工作区服务实现类
 *
 * @author AI Coding Team
 * @since 2026-03-24
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WorkspaceServiceImpl implements WorkspaceService {

    private final WorkspaceMapper workspaceMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Workspace createDefaultWorkspace(Long userId, String username) {
        log.info("创建默认工作区 - userId: {}, username: {}", userId, username);

        Workspace workspace = new Workspace();
        workspace.setName(username + "'s Workspace");
        workspace.setSlug(username.toLowerCase().replaceAll("[^a-z0-9]", "-") + "-workspace");
        workspace.setDescription("Default workspace");
        workspace.setOwnerId(userId);

        workspaceMapper.insert(workspace);

        log.info("默认工作区创建成功 - workspaceId: {}", workspace.getId());
        return workspace;
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkspaceVO> getUserWorkspaces(Long userId) {
        log.info("获取用户工作区列表 - userId: {}", userId);

        LambdaQueryWrapper<Workspace> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Workspace::getOwnerId, userId)
                .orderByDesc(Workspace::getCreatedAt);

        List<Workspace> workspaces = workspaceMapper.selectList(wrapper);

        return workspaces.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public WorkspaceVO getWorkspaceById(Long workspaceId, Long userId) {
        log.info("获取工作区详情 - workspaceId: {}, userId: {}", workspaceId, userId);

        Workspace workspace = workspaceMapper.selectById(workspaceId);
        if (workspace == null) {
            throw new BusinessException("工作区不存在");
        }

        // 验证权限：只有工作区所有者可以访问
        if (!workspace.getOwnerId().equals(userId)) {
            throw new ForbiddenException("无权访问该工作区");
        }

        return convertToVO(workspace);
    }

    /**
     * 将 Workspace 实体转换为 WorkspaceVO
     */
    private WorkspaceVO convertToVO(Workspace workspace) {
        WorkspaceVO vo = new WorkspaceVO();
        BeanUtils.copyProperties(workspace, vo);
        return vo;
    }
}
