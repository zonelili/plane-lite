import axios, { AxiosInstance, AxiosResponse } from 'axios'
import { ElMessage } from 'element-plus'
import { getToken, removeToken } from '@/utils/auth'
import router from '@/router'

// 创建 Axios 实例
const request: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 15000,
})

// 请求拦截器：自动添加 Token
request.interceptors.request.use(
  (config) => {
    const token = getToken()
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器：统一处理错误
request.interceptors.response.use(
  (response: AxiosResponse) => {
    const { code, message, data } = response.data

    if (code === 200) {
      return data
    } else {
      ElMessage.error(message || '请求失败')
      return Promise.reject(new Error(message || 'Error'))
    }
  },
  (error) => {
    if (error.response) {
      const { status, data } = error.response

      if (status === 401) {
        ElMessage.error('登录已过期，请重新登录')
        removeToken()
        router.push('/login')
      } else if (status === 403) {
        ElMessage.error('无权限访问')
      } else if (status === 404) {
        ElMessage.error('请求的资源不存在')
      } else {
        ElMessage.error(data.message || '服务器错误')
      }
    } else {
      ElMessage.error('网络错误')
    }

    return Promise.reject(error)
  }
)

export default request
