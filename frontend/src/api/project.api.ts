import request from './request'
import type { Project, CreateProjectForm, UpdateProjectForm, ProjectListResponse } from '@/types/project'

export const projectApi = {
  list(workspaceId: number, params?: { page?: number; size?: number }) {
    return request.get<any, ProjectListResponse>('/projects', {
      params: { workspace_id: workspaceId, ...params },
    })
  },

  get(id: number) {
    return request.get<any, Project>(`/projects/${id}`)
  },

  create(data: CreateProjectForm & { workspaceId: number }) {
    const payload = {
      workspace_id: data.workspaceId,
      name: data.name,
      identifier: data.identifier,
      description: data.description || null,
    }
    return request.post<any, Project>('/projects', payload)
  },

  update(id: number, data: UpdateProjectForm) {
    return request.put<any, Project>(`/projects/${id}`, data)
  },

  delete(id: number) {
    return request.delete<any, void>(`/projects/${id}`)
  },
}
