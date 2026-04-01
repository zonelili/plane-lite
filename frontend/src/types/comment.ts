export interface Comment {
  id: number
  issueId: number
  userId: number
  content: string
  createdAt: string
  updatedAt: string
}

export interface CreateCommentForm {
  issueId: number
  content: string
}
