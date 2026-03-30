# Day 6 架构设计图解

## 整体架构

```
┌─────────────────────────────────────────────────────────────────┐
│                        Browser / Vue 3                          │
└────────────┬────────────────────────────────────────┬───────────┘
             │                                        │
    ┌────────▼────────┐                    ┌─────────▼──────────┐
    │   Vue Router    │                    │   Pinia Store      │
    │   (routes)      │                    │   (state mgmt)     │
    └────────┬────────┘                    └─────────┬──────────┘
             │                                       │
    ┌────────▼────────────────────────────────────────▼─────────┐
    │                   Components (Vue)                        │
    │  ┌────────────────────────────────────────────────────┐  │
    │  │  AppLayout                                         │  │
    │  │  ├─ AppHeader (导航栏)                             │  │
    │  │  ├─ AppSidebar (侧边栏)                            │  │
    │  │  └─ Router View (页面内容)                         │  │
    │  └────────────────────────────────────────────────────┘  │
    │                                                            │
    │  ┌────────────────┐  ┌────────────────┐                  │
    │  │ ProjectList    │  │ ProjectDetail  │                  │
    │  └────────────────┘  └────────────────┘                  │
    │                                                            │
    │  ┌────────────────┐  ┌────────────────┐                  │
    │  │  IssueList     │  │ IssueDetail    │                  │
    │  └────────────────┘  └────────────────┘                  │
    │                                                            │
    │  ┌──────────────────────────────────────┐               │
    │  │ IssueForm / ProjectForm (Dialog)     │               │
    │  └──────────────────────────────────────┘               │
    └────────┬──────────────────────────────┬─────────────────┘
             │ API calls                    │
    ┌────────▼──────────┐         ┌────────▼────────────┐
    │   Axios Instance  │         │  TypeScript Types   │
    │   (request.ts)    │         │  & Enums            │
    │   - Interceptors  │         │  (src/types/)       │
    │   - Error Handler │         └─────────────────────┘
    └────────┬──────────┘
             │
    ┌────────▼──────────────────────────────────────────┐
    │            API Layer (src/api/)                   │
    │  ┌─────────────┐ ┌─────────────┐ ┌──────────────┐ │
    │  │workspace.api│ │project.api  │ │ issue.api    │ │
    │  └──────┬──────┘ └──────┬──────┘ └──────┬───────┘ │
    └─────────┼────────────────┼──────────────┼────────┘
              │                │              │
    ┌─────────▼────────────────▼──────────────▼─────────┐
    │            HTTP / REST / Bearer Token              │
    └─────────────────────────┬──────────────────────────┘
                              │
                    ┌─────────▼──────────┐
                    │  Spring Boot API   │
                    │  (Backend)         │
                    │                    │
                    │  /api/v1/          │
                    │  ├─ workspaces     │
                    │  ├─ projects       │
                    │  ├─ issues         │
                    │  └─ comments       │
                    └────────┬───────────┘
                             │
                    ┌────────▼───────┐
                    │   MySQL Database│
                    │   (tables:      │
                    │   - users       │
                    │   - workspaces  │
                    │   - projects    │
                    │   - issues      │
                    │   - comments)   │
                    └────────────────┘
```

---

## 数据流向

### 1. 项目列表加载流程

```
用户访问 /projects
       ↓
   Vue Router
       ↓
ProjectList.vue (onMounted)
       ↓
projectStore.fetchProjects(workspaceId)
       ↓
projectApi.list(workspaceId)
       ↓
axios.get('/projects?workspace_id=1')
       ↓
[请求拦截器] 添加 Authorization header
       ↓
Backend: GET /api/v1/projects?workspace_id=1
       ↓
Backend 查询数据库
       ↓
返回 {code: 200, message: 'success', data: [...]}
       ↓
[响应拦截器] 错误处理
       ↓
Store 更新 projects = data
       ↓
Vue 响应式更新模板
       ↓
页面渲染项目列表
```

### 2. 创建问题流程

```
用户点击 "创建问题"
       ↓
IssueFormDialog 打开
       ↓
用户填写表单
       ↓
用户点击提交
       ↓
IssueForm 验证表单
       ↓
issueApi.create(formData)
       ↓
axios.post('/issues', formData)
       ↓
Backend: POST /api/v1/issues
       ↓
Backend 创建 Issue 记录
       ↓
返回 {code: 200, message: 'success', data: newIssue}
       ↓
Store 更新 issues 列表
       ↓
对话框关闭
       ↓
显示成功提示
       ↓
刷新列表视图
```

### 3. 问题更新流程

```
用户在详情页编辑问题
       ↓
修改 title / status / priority
       ↓
用户点击保存
       ↓
issueApi.update(id, updateData)
       ↓
axios.put('/issues/:id', updateData)
       ↓
Backend: PUT /api/v1/issues/{id}
       ↓
Backend 更新 Issue 记录
       ↓
返回更新后的 Issue
       ↓
Store 更新 currentIssue
       ↓
页面响应式更新显示
       ↓
显示保存成功提示
```

---

## 状态管理架构

### Pinia Store 结构

```
┌──────────────────────────────────────────────────┐
│        Pinia Store (src/stores/)                 │
│                                                  │
│  ┌────────────────────────────────────────────┐ │
│  │ useWorkspaceStore                          │ │
│  │ - State:                                   │ │
│  │   workspaces: Workspace[]                  │ │
│  │   currentWorkspace: Workspace | null       │ │
│  │   loading: boolean                         │ │
│  │                                            │ │
│  │ - Actions:                                 │ │
│  │   fetchWorkspaces()                        │ │
│  │   selectWorkspace(id)                      │ │
│  └────────────────────────────────────────────┘ │
│                                                  │
│  ┌────────────────────────────────────────────┐ │
│  │ useProjectStore                            │ │
│  │ - State:                                   │ │
│  │   projects: Project[]                      │ │
│  │   currentProject: Project | null           │ │
│  │   loading: boolean                         │ │
│  │   error: string | null                     │ │
│  │                                            │ │
│  │ - Getters:                                 │ │
│  │   projectCount()                           │ │
│  │                                            │ │
│  │ - Actions:                                 │ │
│  │   fetchProjects(workspaceId)               │ │
│  │   createProject(form)                      │ │
│  │   updateProject(id, form)                  │ │
│  │   deleteProject(id)                        │ │
│  └────────────────────────────────────────────┘ │
│                                                  │
│  ┌────────────────────────────────────────────┐ │
│  │ useIssueStore                              │ │
│  │ - State:                                   │ │
│  │   issues: Issue[]                          │ │
│  │   currentIssue: Issue | null               │ │
│  │   loading: boolean                         │ │
│  │   pagination: {page, size, total, pages}   │ │
│  │   filters: {status?, priority?}            │ │
│  │                                            │ │
│  │ - Getters:                                 │ │
│  │   issueCount()                             │ │
│  │   filteredIssues()                         │ │
│  │   issuesByStatus()                         │ │
│  │                                            │ │
│  │ - Actions:                                 │ │
│  │   fetchIssues(query)                       │ │
│  │   fetchIssueDetail(id)                     │ │
│  │   createIssue(form)                        │ │
│  │   updateIssue(id, form)                    │ │
│  │   deleteIssue(id)                          │ │
│  │   setFilters(filters)                      │ │
│  └────────────────────────────────────────────┘ │
└──────────────────────────────────────────────────┘
```

---

## 组件嵌套关系

```
App.vue
└─ AppLayout.vue
   ├─ AppHeader.vue
   │  └─ 用户菜单、工作区选择、退出登录
   │
   ├─ AppSidebar.vue
   │  └─ 导航菜单
   │     ├─ Projects
   │     ├─ Issues
   │     └─ Settings
   │
   └─ Router View
      │
      ├─ ProjectList.vue
      │  ├─ 搜索框
      │  └─ ProjectCard (多个)
      │     ├─ 项目信息
      │     └─ 操作按钮
      │
      ├─ ProjectDetail.vue
      │  ├─ 项目头部信息
      │  ├─ 4x StatCard
      │  │  ├─ Todo
      │  │  ├─ In Progress
      │  │  ├─ Done
      │  │  └─ Closed
      │  └─ 操作按钮
      │
      ├─ IssueList.vue
      │  ├─ 搜索/筛选框
      │  ├─ el-table
      │  │  └─ Issue rows
      │  └─ el-pagination
      │
      ├─ IssueDetail.vue
      │  ├─ 面包屑
      │  ├─ 主内容区
      │  │  ├─ 标题
      │  │  ├─ 描述
      │  │  └─ 评论列表 (Day 7)
      │  │
      │  └─ 右侧属性面板
      │     ├─ 编号
      │     ├─ 状态选择
      │     ├─ 优先级选择
      │     ├─ 处理人
      │     ├─ 创建人
      │     └─ 时间戳
      │
      └─ 对话框 (v-if)
         ├─ ProjectFormDialog
         │  └─ ProjectForm (表单)
         │
         └─ IssueFormDialog
            └─ IssueForm (表单)
```

---

## 类型系统层级

```
┌──────────────────────────────────────────────┐
│         src/types/ (TypeScript 类型)         │
│                                              │
│  ┌────────────────────────────────────────┐ │
│  │ api.ts                                 │ │
│  │ - ApiResponse<T>                       │ │
│  │ - PageData<T>                          │ │
│  └────────────────────────────────────────┘ │
│                                              │
│  ┌────────────────────────────────────────┐ │
│  │ user.ts                                │ │
│  │ - User                                 │ │
│  │ - LoginForm                            │ │
│  │ - RegisterForm                         │ │
│  │ - LoginResponse                        │ │
│  └────────────────────────────────────────┘ │
│                                              │
│  ┌────────────────────────────────────────┐ │
│  │ project.ts                             │ │
│  │ - Project                              │ │
│  │ - ProjectCreateForm                    │ │
│  │ - ProjectUpdateForm                    │ │
│  │ - ProjectListQuery                     │ │
│  └────────────────────────────────────────┘ │
│                                              │
│  ┌────────────────────────────────────────┐ │
│  │ issue.ts                               │ │
│  │ - Issue                                │ │
│  │ - IssueStatus (enum)                   │ │
│  │ - IssuePriority (enum)                 │ │
│  │ - IssueCreateForm                      │ │
│  │ - IssueUpdateForm                      │ │
│  │ - IssueListQuery                       │ │
│  │ - IssueBoardData                       │ │
│  └────────────────────────────────────────┘ │
│                                              │
│  ┌────────────────────────────────────────┐ │
│  │ workspace.ts                           │ │
│  │ - Workspace                            │ │
│  │ - WorkspaceListQuery                   │ │
│  └────────────────────────────────────────┘ │
└──────────────────────────────────────────────┘
```

---

## API 调用层架构

```
┌──────────────────────────────────────────────┐
│         src/api/ (API 调用层)                 │
│                                              │
│  ┌────────────────────────────────────────┐ │
│  │ request.ts (Axios 配置)                │ │
│  │ - 创建 Axios 实例                       │ │
│  │ - 请求拦截器 (Token)                  │ │
│  │ - 响应拦截器 (错误处理)                │ │
│  │ - ApiResponse 类型                     │ │
│  └────────────────────────────────────────┘ │
│                                              │
│  ┌────────────────────────────────────────┐ │
│  │ workspace.api.ts                       │ │
│  │ - list(): Promise<Workspace[]>         │ │
│  │ - getById(id): Promise<Workspace>      │ │
│  └────────────────────────────────────────┘ │
│                                              │
│  ┌────────────────────────────────────────┐ │
│  │ project.api.ts                         │ │
│  │ - list(workspaceId)                    │ │
│  │ - getById(id)                          │ │
│  │ - create(form)                         │ │
│  │ - update(id, form)                     │ │
│  │ - delete(id)                           │ │
│  └────────────────────────────────────────┘ │
│                                              │
│  ┌────────────────────────────────────────┐ │
│  │ issue.api.ts                           │ │
│  │ - list(query)                          │ │
│  │ - getById(id)                          │ │
│  │ - create(form)                         │ │
│  │ - update(id, form)                     │ │
│  │ - delete(id)                           │ │
│  │ - getBoardData(projectId)              │ │
│  └────────────────────────────────────────┘ │
└──────────────────────────────────────────────┘
```

---

## 路由树结构

```
/
├─ /login              (public)    → Login.vue
├─ /register           (public)    → Register.vue
│
└─ / (AppLayout)       (protected)
   ├─ /projects        → ProjectList.vue
   ├─ /projects/:id    → ProjectDetail.vue
   ├─ /projects/:id/issues
   │  └─             → IssueList.vue
   └─ /issues/:id      → IssueDetail.vue
```

---

## 错误处理流程

```
Axios 请求
    ↓
[响应拦截器]
    ├─ code = 200 → 返回 data
    │
    └─ code ≠ 200
        ↓
        ├─ HTTP 401 (Unauthorized)
        │  ├─ ElMessage.error('登录已过期')
        │  ├─ removeToken()
        │  └─ router.push('/login')
        │
        ├─ HTTP 403 (Forbidden)
        │  └─ ElMessage.error('无权限访问')
        │
        ├─ HTTP 404 (Not Found)
        │  └─ ElMessage.error('资源不存在')
        │
        ├─ HTTP 500 (Server Error)
        │  └─ ElMessage.error('服务器错误')
        │
        └─ Network Error
           └─ ElMessage.error('网络错误')
            ↓
        Promise.reject(error)
```

---

## 加载状态管理

```
用户操作
    ↓
loading = true
    ↓
禁用按钮、显示加载动画
    ↓
API 请求
    ├─ 成功 → 更新数据 → 显示成功提示
    │                  ↓
    │            loading = false
    │                  ↓
    │            启用按钮、隐藏加载
    │
    └─ 失败 → 显示错误提示
                     ↓
                loading = false
                     ↓
                启用按钮、隐藏加载
```

---

## 表单验证流程

```
用户输入表单
    ↓
实时验证 (optional)
    ├─ 字段长度
    ├─ 格式检查 (email, 大写字母等)
    └─ 显示错误提示
    ↓
用户点击提交
    ↓
提前验证整个表单
    ├─ 检查必填字段
    ├─ 检查字段格式
    ├─ 如有错误 → 显示错误信息，不提交
    │
    └─ 验证通过
        ↓
        API 请求
        ├─ 成功 → 关闭对话框 → 刷新列表
        └─ 失败 → 显示后端错误信息
```

---

## 分页处理流程

```
页面初始化
    ↓
currentPage = 1, pageSize = 20
    ↓
fetchIssues() 发送请求
    ↓
后端返回：
{
  items: [issue1, issue2, ...],
  pagination: {
    page: 1,
    size: 20,
    total: 100,
    pages: 5
  }
}
    ↓
更新 issues 列表和 pagination 信息
    ↓
el-pagination 显示分页控件
    ↓
用户点击下一页
    ↓
currentPage 变化
    ↓
触发 watch → fetchIssues(newPage)
    ↓
加载新数据
```

---

**相关文档**:
- 完整方案: `day-6-plan.md`
- 快速参考: `day-6-quick-ref.md`
