import request from './request'
import type { User } from '@/types/user'

export interface Workspace {
  id: number
  name: string
  slug: string
  ownerId: number
  createdAt: string
  updatedAt: string
}

export const workspaceApi = {
  list() {
    return request.get<any, Workspace[]>('/workspaces')
  },

  get(id: number) {
    return request.get<any, Workspace>(`/workspaces/${id}`)
  },
}
