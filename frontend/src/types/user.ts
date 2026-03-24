export interface User {
  id: number
  username: string
  email: string
  avatar: string | null
  createdAt: string
  updatedAt: string
}

export interface LoginForm {
  email: string
  password: string
}

export interface RegisterForm {
  username: string
  email: string
  password: string
}

export interface LoginResponse {
  token: string
  user: User
}
