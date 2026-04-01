import request from './request'
import type { Comment, CreateCommentForm } from '@/types/comment'
import type { ApiResponse } from '@/types/api'

export const commentApi = {
  /**
   * 获取问题的评论列表
   * @param issueId 问题 ID
   */
  list(issueId: number): Promise<Comment[]> {
    return request.get<ApiResponse<Comment[]>>('/comments', {
      params: { issue_id: issueId }
    }).then(res => res.data)
  },

  /**
   * 创建评论
   * @param data 评论内容
   */
  create(data: CreateCommentForm): Promise<Comment> {
    return request.post<ApiResponse<Comment>>('/comments', {
      issue_id: data.issueId,
      content: data.content
    }).then(res => res.data)
  },

  /**
   * 删除评论
   * @param id 评论 ID
   */
  delete(id: number): Promise<void> {
    return request.delete<ApiResponse<void>>(`/comments/${id}`).then(() => {})
  }
}
