import request from './request'
import type { Issue, CreateIssueForm, UpdateIssueForm, IssueListResponse, IssueBoardData } from '@/types/issue'

export const issueApi = {
  list(projectId: number, params?: { page?: number; size?: number; status?: string; priority?: string }) {
    return request.get<any, IssueListResponse>('/issues', {
      params: { project_id: projectId, ...params },
    })
  },

  get(id: number) {
    return request.get<any, Issue>(`/issues/${id}`)
  },

  create(data: CreateIssueForm & { projectId: number }) {
    const payload = {
      project_id: data.projectId,
      title: data.title,
      description: data.description || null,
      status: data.status || 'todo',
      priority: data.priority || 'none',
      assignee_id: data.assigneeId || null,
    }
    return request.post<any, Issue>('/issues', payload)
  },

  update(id: number, data: UpdateIssueForm) {
    return request.put<any, Issue>(`/issues/${id}`, data)
  },

  delete(id: number) {
    return request.delete<any, void>(`/issues/${id}`)
  },

  board(projectId: number) {
    return request.get<any, IssueBoardData>('/issues/board', {
      params: { project_id: projectId },
    })
  },
}
