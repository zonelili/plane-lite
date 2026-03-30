export enum IssueStatus {
  TODO = 'todo',
  IN_PROGRESS = 'in_progress',
  DONE = 'done',
  CLOSED = 'closed',
}

export enum IssuePriority {
  NONE = 'none',
  LOW = 'low',
  MEDIUM = 'medium',
  HIGH = 'high',
  URGENT = 'urgent',
}

export interface Issue {
  id: number
  projectId: number
  sequenceId: number
  number: string
  title: string
  description: string | null
  status: IssueStatus
  priority: IssuePriority
  assigneeId: number | null
  createdAt: string
  updatedAt: string
}

export interface CreateIssueForm {
  title: string
  description?: string
  status?: IssueStatus
  priority?: IssuePriority
  assigneeId?: number
}

export interface UpdateIssueForm {
  title?: string
  description?: string
  status?: IssueStatus
  priority?: IssuePriority
  assigneeId?: number
}

export interface IssueListResponse {
  items: Issue[]
  pagination: {
    page: number
    size: number
    total: number
    pages: number
  }
}

export interface IssueBoardData {
  todo: Issue[]
  in_progress: Issue[]
  done: Issue[]
  closed: Issue[]
}
