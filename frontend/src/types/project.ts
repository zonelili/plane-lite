export interface Project {
  id: number
  workspaceId: number
  name: string
  identifier: string
  description: string | null
  createdAt: string
  updatedAt: string
}

export interface CreateProjectForm {
  name: string
  identifier: string
  description?: string
}

export interface UpdateProjectForm {
  name?: string
  description?: string
}

export interface ProjectListResponse {
  items: Project[]
  pagination: {
    page: number
    size: number
    total: number
    pages: number
  }
}
