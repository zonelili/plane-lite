package com.planelite.module.issue.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.planelite.module.issue.entity.Issue;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 问题 Mapper
 *
 * @author AI Coding Team
 * @since 2026-03-24
 */
@Mapper
public interface IssueMapper extends BaseMapper<Issue> {

    /**
     * 查询项目内最大的 sequence_id
     *
     * @param projectId 项目 ID
     * @return 最大的 sequence_id，如果没有则返回 null
     */
    @Select("SELECT MAX(sequence_id) FROM issue WHERE project_id = #{projectId}")
    Integer selectMaxSequenceIdByProject(Long projectId);
}
