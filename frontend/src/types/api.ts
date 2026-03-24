// 统一 API 响应格式
export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}

// 分页响应
export interface PageData<T> {
  items: T[]
  pagination: {
    page: number
    size: number
    total: number
    pages: number
  }
}
