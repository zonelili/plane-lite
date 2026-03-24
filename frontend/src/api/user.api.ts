import request from './request'
import type { LoginForm, RegisterForm, LoginResponse, User } from '@/types/user'

export const userApi = {
  // 用户注册
  register(data: RegisterForm) {
    return request.post<any, void>('/auth/register', data)
  },

  // 用户登录
  login(data: LoginForm) {
    return request.post<any, LoginResponse>('/auth/login', data)
  },

  // 获取当前用户信息
  getCurrentUser() {
    return request.get<any, User>('/auth/me')
  },
}
