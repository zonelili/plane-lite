# 前端实现方案

**文档版本**：v1.0
**创建日期**：2026-03-23
**状态**：已确认

---

## 1. 项目初始化

### 1.1 创建项目

```bash
# 使用 Vite 创建项目
npm create vite@latest plane-lite-frontend -- --template vue-ts

cd plane-lite-frontend
npm install
```

### 1.2 安装依赖

```bash
# 核心依赖
npm install vue-router@4 pinia axios

# UI 组件库
npm install element-plus @element-plus/icons-vue

# 工具库
npm install dayjs lodash-es

# TypeScript 类型
npm install -D @types/lodash-es

# 开发工具
npm install -D sass
```

---

## 2. 项目结构

```
plane-lite-frontend/
├── public/
│   └── favicon.ico
├── src/
│   ├── assets/
│   │   ├── images/
│   │   ├── icons/
│   │   └── styles/
│   │       ├── variables.scss
│   │       ├── mixins.scss
│   │       └── global.scss
│   ├── components/
│   │   ├── layout/
│   │   │   ├── AppHeader.vue
│   │   │   ├── AppSidebar.vue
│   │   │   └── AppMain.vue
│   │   └── common/
│   │       ├── EmptyState.vue
│   │       ├── LoadingSpinner.vue
│   │       └── ConfirmDialog.vue
│   ├── views/
│   │   ├── auth/
│   │   │   ├── Login.vue
│   │   │   └── Register.vue
│   │   ├── workspace/
│   │   │   └── WorkspaceHome.vue
│   │   ├── project/
│   │   │   ├── ProjectList.vue
│   │   │   └── ProjectDetail.vue
│   │   └── issue/
│   │       ├── IssueList.vue
│   │       ├── IssueDetail.vue
│   │       └── IssueBoard.vue
│   ├── composables/
│   │   ├── useUser.ts
│   │   ├── useProject.ts
│   │   └── useIssue.ts
│   ├── api/
│   │   ├── request.ts
│   │   ├── user.api.ts
│   │   ├── project.api.ts
│   │   ├── issue.api.ts
│   │   └── comment.api.ts
│   ├── stores/
│   │   ├── user.ts
│   │   ├── project.ts
│   │   ├── issue.ts
│   │   └── comment.ts
│   ├── router/
│   │   ├── index.ts
│   │   └── routes.ts
│   ├── types/
│   │   ├── user.ts
│   │   ├── project.ts
│   │   ├── issue.ts
│   │   └── common.ts
│   ├── utils/
│   │   ├── auth.ts
│   │   ├── date.ts
│   │   └── storage.ts
│   ├── App.vue
│   └── main.ts
├── .env.development
├── .env.production
├── index.html
├── package.json
├── tsconfig.json
└── vite.config.ts
```

---

## 3. 配置文件

### 3.1 vite.config.ts

```typescript
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src')
    }
  },
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
```

### 3.2 tsconfig.json

```json
{
  "compilerOptions": {
    "target": "ES2020",
    "useDefineForClassFields": true,
    "module": "ESNext",
    "lib": ["ES2020", "DOM", "DOM.Iterable"],
    "skipLibCheck": true,

    "moduleResolution": "bundler",
    "allowImportingTsExtensions": true,
    "resolveJsonModule": true,
    "isolatedModules": true,
    "noEmit": true,
    "jsx": "preserve",

    "strict": true,
    "noUnusedLocals": true,
    "noUnusedParameters": true,
    "noFallthroughCasesInSwitch": true,

    "baseUrl": ".",
    "paths": {
      "@/*": ["src/*"]
    }
  },
  "include": ["src/**/*.ts", "src/**/*.d.ts", "src/**/*.tsx", "src/**/*.vue"],
  "references": [{ "path": "./tsconfig.node.json" }]
}
```

### 3.3 .env.development

```
VITE_API_BASE_URL=http://localhost:8080/api/v1
VITE_APP_TITLE=Plane Lite
```

### 3.4 .env.production

```
VITE_API_BASE_URL=/api/v1
VITE_APP_TITLE=Plane Lite
```

---

## 4. 核心代码实现

### 4.1 Axios 封装

```typescript
// src/api/request.ts
import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios'
import { ElMessage } from 'element-plus'
import { getToken, removeToken } from '@/utils/auth'
import router from '@/router'

export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}

const service: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 10000
})

// 请求拦截器
service.interceptors.request.use(
  (config) => {
    const token = getToken()
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  (response: AxiosResponse<ApiResponse>) => {
    const { code, message, data } = response.data

    if (code === 200) {
      return data
    } else {
      ElMessage.error(message || '请求失败')
      return Promise.reject(new Error(message || '请求失败'))
    }
  },
  (error) => {
    if (error.response) {
      const { status, data } = error.response

      switch (status) {
        case 401:
          ElMessage.error('未登录或登录已过期')
          removeToken()
          router.push('/login')
          break
        case 403:
          ElMessage.error('无权限访问')
          break
        case 404:
          ElMessage.error('请求的资源不存在')
          break
        case 500:
          ElMessage.error(data?.message || '服务器错误')
          break
        default:
          ElMessage.error(data?.message || '请求失败')
      }
    } else {
      ElMessage.error('网络错误，请检查网络连接')
    }

    return Promise.reject(error)
  }
)

export default service
```

### 4.2 类型定义

```typescript
// src/types/user.ts
export interface User {
  id: number
  username: string
  email: string
  avatar?: string
  createdAt: string
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
```

```typescript
// src/types/project.ts
export interface Project {
  id: number
  workspaceId: number
  name: string
  identifier: string
  description?: string
  icon?: string
  coverImage?: string
  createdBy: number
  createdAt: string
  updatedAt: string
}

export interface ProjectCreateForm {
  workspaceId: number
  name: string
  identifier: string
  description?: string
  icon?: string
}
```

```typescript
// src/types/issue.ts
export enum IssueStatus {
  TODO = 'todo',
  IN_PROGRESS = 'in_progress',
  DONE = 'done',
  CLOSED = 'closed'
}

export enum IssuePriority {
  NONE = 'none',
  LOW = 'low',
  MEDIUM = 'medium',
  HIGH = 'high',
  URGENT = 'urgent'
}

export interface Issue {
  id: number
  projectId: number
  issueNumber: string
  title: string
  description?: string
  priority: IssuePriority
  status: IssueStatus
  assignee?: User
  reporter: User
  createdAt: string
  updatedAt: string
}

export interface IssueCreateForm {
  projectId: number
  title: string
  description?: string
  priority: IssuePriority
  status: IssueStatus
  assigneeId?: number
}
```

### 4.3 API 调用

```typescript
// src/api/user.api.ts
import request from './request'
import type { User, LoginForm, RegisterForm, LoginResponse } from '@/types/user'

export const userApi = {
  // 注册
  register(data: RegisterForm) {
    return request.post<any, User>('/auth/register', data)
  },

  // 登录
  login(data: LoginForm) {
    return request.post<any, LoginResponse>('/auth/login', data)
  },

  // 获取当前用户信息
  getCurrentUser() {
    return request.get<any, User>('/auth/me')
  }
}
```

```typescript
// src/api/project.api.ts
import request from './request'
import type { Project, ProjectCreateForm } from '@/types/project'

export const projectApi = {
  // 获取项目列表
  list(workspaceId: number) {
    return request.get<any, Project[]>('/projects', {
      params: { workspace_id: workspaceId }
    })
  },

  // 获取项目详情
  getById(id: number) {
    return request.get<any, Project>(`/projects/${id}`)
  },

  // 创建项目
  create(data: ProjectCreateForm) {
    return request.post<any, Project>('/projects', data)
  },

  // 更新项目
  update(id: number, data: Partial<ProjectCreateForm>) {
    return request.put<any, Project>(`/projects/${id}`, data)
  },

  // 删除项目
  delete(id: number) {
    return request.delete<any, void>(`/projects/${id}`)
  }
}
```

### 4.4 Pinia Store

```typescript
// src/stores/user.ts
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { userApi } from '@/api/user.api'
import { setToken, removeToken } from '@/utils/auth'
import type { User, LoginForm, RegisterForm } from '@/types/user'

export const useUserStore = defineStore('user', () => {
  const user = ref<User | null>(null)
  const token = ref<string>('')

  const isLoggedIn = computed(() => !!token.value)

  // 注册
  async function register(data: RegisterForm) {
    const result = await userApi.register(data)
    return result
  }

  // 登录
  async function login(data: LoginForm) {
    const { token: newToken, user: userData } = await userApi.login(data)
    token.value = newToken
    user.value = userData
    setToken(newToken)
  }

  // 获取当前用户信息
  async function fetchCurrentUser() {
    const userData = await userApi.getCurrentUser()
    user.value = userData
  }

  // 退出登录
  function logout() {
    user.value = null
    token.value = ''
    removeToken()
  }

  return {
    user,
    token,
    isLoggedIn,
    register,
    login,
    fetchCurrentUser,
    logout
  }
})
```

### 4.5 路由配置

```typescript
// src/router/index.ts
import { createRouter, createWebHistory } from 'vue-router'
import { getToken } from '@/utils/auth'
import { ElMessage } from 'element-plus'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/auth/Login.vue'),
      meta: { requiresAuth: false }
    },
    {
      path: '/register',
      name: 'Register',
      component: () => import('@/views/auth/Register.vue'),
      meta: { requiresAuth: false }
    },
    {
      path: '/',
      component: () => import('@/components/layout/AppLayout.vue'),
      meta: { requiresAuth: true },
      children: [
        {
          path: '',
          redirect: '/projects'
        },
        {
          path: 'projects',
          name: 'ProjectList',
          component: () => import('@/views/project/ProjectList.vue')
        },
        {
          path: 'projects/:id',
          name: 'ProjectDetail',
          component: () => import('@/views/project/ProjectDetail.vue')
        },
        {
          path: 'projects/:id/issues',
          name: 'IssueList',
          component: () => import('@/views/issue/IssueList.vue')
        },
        {
          path: 'projects/:id/board',
          name: 'IssueBoard',
          component: () => import('@/views/issue/IssueBoard.vue')
        },
        {
          path: 'issues/:id',
          name: 'IssueDetail',
          component: () => import('@/views/issue/IssueDetail.vue')
        }
      ]
    }
  ]
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = getToken()
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth !== false)

  if (requiresAuth && !token) {
    ElMessage.warning('请先登录')
    next('/login')
  } else if ((to.path === '/login' || to.path === '/register') && token) {
    next('/projects')
  } else {
    next()
  }
})

export default router
```

### 4.6 工具函数

```typescript
// src/utils/auth.ts
const TOKEN_KEY = 'plane-lite-token'

export function getToken(): string | null {
  return localStorage.getItem(TOKEN_KEY)
}

export function setToken(token: string): void {
  localStorage.setItem(TOKEN_KEY, token)
}

export function removeToken(): void {
  localStorage.removeItem(TOKEN_KEY)
}
```

```typescript
// src/utils/date.ts
import dayjs from 'dayjs'
import relativeTime from 'dayjs/plugin/relativeTime'
import 'dayjs/locale/zh-cn'

dayjs.extend(relativeTime)
dayjs.locale('zh-cn')

export function formatDate(date: string | Date, format = 'YYYY-MM-DD HH:mm:ss'): string {
  return dayjs(date).format(format)
}

export function fromNow(date: string | Date): string {
  return dayjs(date).fromNow()
}
```

---

## 5. 开发规范

### 5.1 命名规范

**文件命名**：
- 组件文件：PascalCase - `UserProfile.vue`
- 工具文件：camelCase - `dateUtils.ts`
- 类型文件：camelCase - `user.ts`

**组件命名**：
- PascalCase
- 多单词命名（避免与 HTML 元素冲突）
- 例：`UserProfile`, `ProjectCard`, `IssueBoard`

**变量和函数命名**：
- camelCase
- 函数用动词开头：`fetchUser`, `createProject`, `updateIssue`
- 布尔值用 is/has 开头：`isLoggedIn`, `hasPermission`

### 5.2 组件结构

```vue
<template>
  <div class="user-profile">
    <!-- 模板内容 -->
  </div>
</template>

<script setup lang="ts">
// 1. 导入
import { ref, computed, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'

// 2. Props
interface Props {
  userId: number
}
const props = defineProps<Props>()

// 3. Emits
const emit = defineEmits<{
  update: [userId: number]
}>()

// 4. Composables / Stores
const userStore = useUserStore()

// 5. Reactive State
const loading = ref(false)
const user = ref<User | null>(null)

// 6. Computed
const displayName = computed(() => user.value?.username || 'Unknown')

// 7. Methods
async function fetchUser() {
  loading.value = true
  try {
    user.value = await userApi.getById(props.userId)
  } finally {
    loading.value = false
  }
}

// 8. Lifecycle
onMounted(() => {
  fetchUser()
})
</script>

<style scoped lang="scss">
.user-profile {
  // 样式
}
</style>
```

### 5.3 TypeScript 使用

**类型定义**：
```typescript
// 优先使用 interface
interface User {
  id: number
  name: string
}

// 联合类型使用 type
type Status = 'pending' | 'success' | 'error'
```

**组件 Props**：
```typescript
// 使用 TypeScript 定义 Props
interface Props {
  id: number
  name: string
  optional?: boolean
}

const props = defineProps<Props>()
```

---

## 6. 样式规范

### 6.1 全局变量

```scss
// src/assets/styles/variables.scss
$primary-color: #409eff;
$success-color: #67c23a;
$warning-color: #e6a23c;
$danger-color: #f56c6c;
$info-color: #909399;

$text-primary: #303133;
$text-regular: #606266;
$text-secondary: #909399;
$text-placeholder: #c0c4cc;

$border-base: #dcdfe6;
$border-light: #e4e7ed;
$border-lighter: #ebeef5;
$border-extra-light: #f2f6fc;

$bg-color: #ffffff;
$bg-color-page: #f5f7fa;
```

### 6.2 BEM 命名

```scss
.issue-card {
  // Block

  &__header {
    // Element
  }

  &__title {
    // Element
  }

  &--urgent {
    // Modifier
  }
}
```

---

## 7. 更新记录

| 版本 | 日期 | 变更说明 |
|------|------|----------|
| v1.0 | 2026-03-23 | 初始版本，完整的前端实现方案 |
