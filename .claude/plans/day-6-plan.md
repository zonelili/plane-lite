# Day 6 实现方案：项目和问题管理页面开发

**文档版本**: v1.0  
**创建日期**: 2026-03-30  
**状态**: 设计方案确认  
**预计工时**: 5-6 小时  

---

## 1. 技术栈验证

### 1.1 Vue 3 Composition API 使用模式

采用 `<script setup>` 语法，确保：
- 自动暴露顶级绑定到模板
- 更简洁的组件代码结构
- 完整的 TypeScript 类型推断支持

**推荐结构**:
```
1. 导入 (imports)
2. Props 定义 (defineProps<Props>())
3. Emits 定义 (defineEmits<Emits>())
4. 组合式函数/Stores (useXxx, useXxxStore)
5. 响应式状态 (ref, reactive)
6. 计算属性 (computed)
7. 方法 (function)
8. 生命周期钩子 (onMounted, watch)
```

### 1.2 TypeScript 类型定义

**类型文件位置**: `src/types/`

严格要求：
- 所有 API 响应必须定义类型
- Props/Emits 必须使用 interface 定义
- 禁止使用 `any`（极特殊情况除外）
- 使用 `type` 定义联合类型（Status 枚举）

**枚举定义**: 
```typescript
export enum IssuePriority {
  NONE = 'none',
  LOW = 'low',
  MEDIUM = 'medium',
  HIGH = 'high',
  URGENT = 'urgent'
}

export enum IssueStatus {
  TODO = 'todo',
  IN_PROGRESS = 'in_progress',
  DONE = 'done',
  CLOSED = 'closed'
}
```

### 1.3 Pinia Store 结构

**原则**：
- 按业务模块划分 Store（project.ts, issue.ts）
- 使用 Composition API 风格定义 Store
- Actions 处理异步操作（API 调用）
- State 保持最小化（避免重复数据）
- 通过 computed 派生数据

**Store 模板**:
```typescript
export const useProjectStore = defineStore('project', () => {
  // State
  const projects = ref<Project[]>([])
  const currentProject = ref<Project | null>(null)
  const loading = ref(false)
  const error = ref<string | null>(null)

  // Getters
  const projectCount = computed(() => projects.value.length)

  // Actions
  async function fetchProjects(workspaceId: number) {
    loading.value = true
    error.value = null
    try {
      projects.value = await projectApi.list(workspaceId)
    } catch (err) {
      error.value = (err as Error).message
      throw err
    } finally {
      loading.value = false
    }
  }

  return {
    projects,
    currentProject,
    loading,
    error,
    projectCount,
    fetchProjects,
  }
})
```

### 1.4 组件组织方式

**文件结构**:
```
src/
├── components/
│   ├── layout/
│   │   ├── AppHeader.vue         # 顶部导航
│   │   ├── AppSidebar.vue        # 侧边栏
│   │   └── AppLayout.vue         # 布局容器
│   └── common/
│       ├── EmptyState.vue        # 空状态提示
│       ├── LoadingSpinner.vue    # 加载动画
│       └── ConfirmDialog.vue     # 确认对话框
├── views/
│   ├── project/
│   │   ├── ProjectList.vue       # 项目列表页
│   │   └── ProjectDetail.vue     # 项目详情页
│   └── issue/
│       ├── IssueList.vue         # 问题列表页
│       ├── IssueDetail.vue       # 问题详情页
│       └── IssueBoard.vue        # 看板视图（Day 7）
└── components/
    └── form/
        └── IssueForm.vue         # 问题创建/编辑表单
```

---

## 2. API 集成清单

### 2.1 需要调用的后端 API 端点

#### 工作区相关
```
GET /api/v1/workspaces
  - 获取当前用户的工作区列表
  - Headers: Authorization: Bearer {token}
  - Response: List<WorkspaceVO>
```

#### 项目相关
```
GET /api/v1/projects?workspace_id={workspaceId}
  - 获取项目列表（按工作区）
  - Response: List<ProjectVO>

GET /api/v1/projects/{id}
  - 获取项目详情
  - Response: ProjectVO

POST /api/v1/projects
  - 创建项目
  - Body: ProjectCreateDTO
  - Response: ProjectVO

PUT /api/v1/projects/{id}
  - 更新项目
  - Body: ProjectUpdateDTO
  - Response: ProjectVO

DELETE /api/v1/projects/{id}
  - 删除项目
  - Response: null
```

#### 问题相关
```
GET /api/v1/issues?project_id={projectId}&status={status}&page={page}&size={size}
  - 获取问题列表（支持筛选）
  - Query: project_id, status(可选), priority(可选), page, size
  - Response: List<IssueVO>

GET /api/v1/issues/{id}
  - 获取问题详情
  - Response: IssueVO

POST /api/v1/issues
  - 创建问题
  - Body: IssueCreateDTO
  - Response: IssueVO

PUT /api/v1/issues/{id}
  - 更新问题
  - Body: IssueUpdateDTO
  - Response: IssueVO

DELETE /api/v1/issues/{id}
  - 删除问题
  - Response: null

GET /api/v1/issues/board?project_id={projectId}
  - 获取看板数据（按状态分组）
  - Response: IssueBoardVO
```

### 2.2 请求/响应格式

#### 统一 API 响应格式
```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

#### ProjectVO 格式
```json
{
  "id": 1,
  "name": "Plane Lite",
  "identifier": "PL",
  "description": "项目管理系统",
  "icon": "🚀",
  "created_by": 1,
  "created_at": "2026-03-23T10:00:00",
  "updated_at": "2026-03-23T10:00:00"
}
```

#### IssueVO 格式
```json
{
  "id": 1,
  "project_id": 1,
  "issue_number": "PL-1",
  "title": "实现用户登录功能",
  "description": "需要支持邮箱登录和 JWT 认证",
  "priority": "high",
  "status": "in_progress",
  "assignee": {
    "id": 1,
    "username": "zhangsan",
    "avatar": null
  },
  "reporter": {
    "id": 1,
    "username": "zhangsan",
    "avatar": null
  },
  "created_at": "2026-03-23T10:00:00",
  "updated_at": "2026-03-23T11:00:00"
}
```

### 2.3 错误处理策略

**Axios 拦截器** (`src/api/request.ts`)：
- 401: 清除 Token，重定向到登录页
- 403: 显示无权限提示
- 404: 显示资源不存在
- 500: 显示服务器错误
- 网络错误: 显示网络连接错误

**前端错误处理**:
```typescript
// API 调用时
try {
  projects.value = await projectApi.list(workspaceId)
} catch (err) {
  error.value = (err as Error).message
  ElMessage.error('加载失败，请重试')
}
```

---

## 3. 前端设计规范

### 3.1 色彩方案（基于 variables.scss）

```scss
// 主色系
$primary-color: #409eff;       // 蓝色（主操作）
$success-color: #67c23a;       // 绿色（成功、完成）
$warning-color: #e6a23c;       // 橙色（警告）
$danger-color: #f56c6c;        // 红色（删除、错误）
$info-color: #909399;          // 灰色（信息）

// 文字色系
$text-primary: #303133;        // 主文本（标题、正文）
$text-regular: #606266;        // 常规文本
$text-secondary: #909399;      // 次要文本（描述、备注）
$text-placeholder: #c0c4cc;    // 占位符（输入框提示）

// 边框色系
$border-base: #dcdfe6;         // 标准边框
$border-light: #e4e7ed;        // 浅色边框
$border-lighter: #ebeef5;      // 更浅边框
$border-extra-light: #f2f6fc;  // 最浅边框

// 背景色
$background-base: #f5f7fa;     // 页面背景
```

### 3.2 页面布局模式

#### 标准页面布局
```
┌─────────────────────────────────┐
│       AppHeader (导航栏)         │
├──────────┬──────────────────────┤
│          │                      │
│AppSidebar│   MainContent        │
│(侧边栏)   │   (主内容区)         │
│          │                      │
└──────────┴──────────────────────┘
```

**AppHeader**: 
- 显示用户名/头像
- 工作区选择
- 退出登录

**AppSidebar**:
- 导航菜单（项目、问题、设置）
- 工作区切换
- 快速创建按钮

**MainContent**:
- 页面标题和操作栏
- 内容主体
- 分页/加载提示

### 3.3 交互设计原则

1. **列表页交互**:
   - 搜索/筛选功能置顶
   - 分页或无限滚动
   - 行级操作（编辑、删除、查看）
   - 批量操作（可选）

2. **详情页交互**:
   - 面包屑导航（回溯）
   - 编辑/删除按钮
   - 相关信息面板
   - 时间戳显示（创建时间、修改时间）

3. **表单交互**:
   - 必填字段用 * 标记
   - 实时验证反馈
   - 提交时禁用按钮（防止重复提交）
   - 成功后显示提示并重定向

4. **加载/空状态**:
   - 加载中：显示骨架屏或加载动画
   - 空状态：显示示意图和创建提示
   - 错误状态：显示错误信息和重试按钮

### 3.4 表单验证规则

#### 项目表单
| 字段 | 规则 | 错误信息 |
|------|------|---------|
| name | 必填，1-100字符 | 项目名称不能为空且不超过100个字符 |
| identifier | 必填，2-10字符，大写 | 项目标识符不能为空且2-10个字符 |
| description | 可选，最多500字符 | 项目描述不超过500个字符 |

#### 问题表单
| 字段 | 规则 | 错误信息 |
|------|------|---------|
| title | 必填，1-200字符 | 问题标题不能为空且不超过200个字符 |
| description | 可选，最多5000字符 | 描述不超过5000个字符 |
| priority | 必选 | 请选择优先级 |
| status | 必选 | 请选择状态 |
| assignee_id | 可选 | - |

---

## 4. 组件文件清单（优先级排序）

### 第一层：基础配置和 API 层

#### 4.1.1 `src/types/project.ts` - 项目相关类型定义

**包含内容**:
```typescript
// 项目实体
interface Project {
  id: number
  workspaceId: number
  name: string
  identifier: string
  description?: string
  icon?: string
  createdBy: number
  createdAt: string
  updatedAt: string
}

// 创建项目表单
interface ProjectCreateForm {
  workspaceId: number
  name: string
  identifier: string
  description?: string
  icon?: string
}

// 更新项目表单
interface ProjectUpdateForm {
  name?: string
  description?: string
  icon?: string
}

// 项目列表查询
interface ProjectListQuery {
  workspaceId: number
}
```

#### 4.1.2 `src/types/issue.ts` - 问题相关类型定义

**包含内容**:
```typescript
// 枚举定义
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

// 问题实体
interface Issue {
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

// 问题创建表单
interface IssueCreateForm {
  projectId: number
  title: string
  description?: string
  priority: IssuePriority
  status: IssueStatus
  assigneeId?: number
}

// 问题更新表单
interface IssueUpdateForm {
  title?: string
  description?: string
  priority?: IssuePriority
  status?: IssueStatus
  assigneeId?: number
}

// 问题列表查询
interface IssueListQuery {
  projectId: number
  status?: IssueStatus
  priority?: IssuePriority
  page: number
  size: number
}

// 看板数据
interface IssueBoardData {
  todo: Issue[]
  in_progress: Issue[]
  done: Issue[]
  closed: Issue[]
}
```

#### 4.1.3 `src/api/project.api.ts` - 项目 API 调用

**包含内容**:
```typescript
export const projectApi = {
  // 获取项目列表
  list(workspaceId: number): Promise<Project[]>

  // 获取项目详情
  getById(id: number): Promise<Project>

  // 创建项目
  create(data: ProjectCreateForm): Promise<Project>

  // 更新项目
  update(id: number, data: ProjectUpdateForm): Promise<Project>

  // 删除项目
  delete(id: number): Promise<void>
}
```

#### 4.1.4 `src/api/issue.api.ts` - 问题 API 调用

**包含内容**:
```typescript
export const issueApi = {
  // 获取问题列表
  list(query: IssueListQuery): Promise<Issue[]>

  // 获取问题详情
  getById(id: number): Promise<Issue>

  // 创建问题
  create(data: IssueCreateForm): Promise<Issue>

  // 更新问题
  update(id: number, data: IssueUpdateForm): Promise<Issue>

  // 删除问题
  delete(id: number): Promise<void>

  // 获取看板数据
  getBoardData(projectId: number): Promise<IssueBoardData>
}
```

#### 4.1.5 `src/api/workspace.api.ts` - 工作区 API 调用

**包含内容**:
```typescript
export const workspaceApi = {
  // 获取工作区列表
  list(): Promise<Workspace[]>

  // 获取工作区详情
  getById(id: number): Promise<Workspace>
}
```

### 第二层：状态管理

#### 4.2.1 `src/stores/project.ts` - 项目 Store

**State**:
- projects: Project[] - 项目列表
- currentProject: Project | null - 当前项目
- loading: boolean - 加载状态
- error: string | null - 错误信息
- currentWorkspaceId: number - 当前工作区 ID

**Getters**:
- projectCount - 项目总数
- hasProjects - 是否有项目

**Actions**:
- fetchProjects(workspaceId) - 加载项目列表
- selectProject(id) - 选择项目
- createProject(form) - 创建项目
- updateProject(id, form) - 更新项目
- deleteProject(id) - 删除项目
- clearProjects() - 清空项目列表

#### 4.2.2 `src/stores/issue.ts` - 问题 Store

**State**:
- issues: Issue[] - 问题列表
- currentIssue: Issue | null - 当前问题
- loading: boolean - 加载状态
- error: string | null - 错误信息
- pagination: {page, size, total, pages} - 分页信息
- filters: {status?, priority?} - 当前筛选条件

**Getters**:
- issueCount - 问题总数
- filteredIssues - 按筛选条件的问题列表
- issuesByStatus - 按状态分组的问题

**Actions**:
- fetchIssues(query) - 加载问题列表
- fetchIssueDetail(id) - 加载问题详情
- createIssue(form) - 创建问题
- updateIssue(id, form) - 更新问题
- deleteIssue(id) - 删除问题
- setFilters(filters) - 设置筛选条件
- clearFilters() - 清空筛选条件

#### 4.2.3 `src/stores/workspace.ts` - 工作区 Store

**State**:
- workspaces: Workspace[] - 工作区列表
- currentWorkspace: Workspace | null - 当前工作区
- loading: boolean

**Actions**:
- fetchWorkspaces() - 加载工作区
- selectWorkspace(id) - 选择工作区

### 第三层：页面/视图组件

#### 4.3.1 `src/views/project/ProjectList.vue` - 项目列表页面

**功能**:
- 展示工作区下所有项目
- 创建项目按钮
- 项目搜索/筛选
- 项目卡片展示（项目名称、描述、成员数、问题数）
- 编辑/删除操作
- 点击卡片进入项目详情

**结构**:
```vue
<template>
  <div class="project-list">
    <!-- 页面头部 -->
    <div class="header">
      <h1>项目列表</h1>
      <el-button type="primary" @click="showCreateForm">
        创建项目
      </el-button>
    </div>

    <!-- 搜索/筛选 -->
    <div class="filters">
      <el-input 
        v-model="searchKeyword" 
        placeholder="搜索项目..."
      />
    </div>

    <!-- 项目网格 -->
    <div v-if="projects.length" class="project-grid">
      <ProjectCard 
        v-for="project in filteredProjects"
        :key="project.id"
        :project="project"
        @click="navigateToDetail"
        @edit="handleEdit"
        @delete="handleDelete"
      />
    </div>

    <!-- 空状态 -->
    <el-empty 
      v-else
      description="暂无项目，创建一个新项目开始吧"
    />

    <!-- 创建/编辑对话框 -->
    <ProjectFormDialog
      v-model:visible="formVisible"
      :project="editingProject"
      @success="handleFormSuccess"
    />
  </div>
</template>
```

**关键逻辑**:
- onMounted: 加载当前工作区的项目列表
- 实时搜索过滤
- 创建/编辑/删除操作

#### 4.3.2 `src/views/project/ProjectDetail.vue` - 项目详情页面

**功能**:
- 展示项目信息（名称、描述、标识符等）
- 编辑项目按钮
- 问题统计（待办、进行中、已完成、已关闭）
- 快速创建问题按钮
- 导航到问题列表、看板视图

**结构**:
```vue
<template>
  <div class="project-detail" v-if="currentProject">
    <!-- 面包屑 -->
    <el-breadcrumb :separator-icon="ArrowRight">
      <el-breadcrumb-item :to="'/projects'">项目列表</el-breadcrumb-item>
      <el-breadcrumb-item>{{ currentProject.name }}</el-breadcrumb-item>
    </el-breadcrumb>

    <!-- 项目信息头 -->
    <div class="project-header">
      <h1>{{ currentProject.name }}</h1>
      <div class="actions">
        <el-button @click="navigateToIssues">问题列表</el-button>
        <el-button @click="navigateToBoard">看板</el-button>
        <el-button type="primary" @click="showCreateIssue">
          创建问题
        </el-button>
        <el-button @click="showEditProject">编辑</el-button>
      </div>
    </div>

    <!-- 项目统计 -->
    <div class="statistics">
      <StatCard 
        icon="📋"
        title="待办"
        :count="issueStats.todo"
      />
      <StatCard 
        icon="⚙️"
        title="进行中"
        :count="issueStats.in_progress"
      />
      <StatCard 
        icon="✅"
        title="已完成"
        :count="issueStats.done"
      />
      <StatCard 
        icon="🚫"
        title="已关闭"
        :count="issueStats.closed"
      />
    </div>

    <!-- 项目描述 -->
    <div class="project-info">
      <p>{{ currentProject.description }}</p>
    </div>
  </div>
</template>
```

**关键逻辑**:
- 从路由参数获取项目 ID
- 加载项目详情和问题统计
- 导航到子页面（问题列表、看板）

#### 4.3.3 `src/views/issue/IssueList.vue` - 问题列表页面

**功能**:
- 展示项目下所有问题
- 创建问题按钮
- 问题搜索、筛选（状态、优先级）
- 问题表格展示（编号、标题、优先级、状态、处理人、创建时间）
- 编辑/删除操作
- 分页
- 点击问题进入详情

**结构**:
```vue
<template>
  <div class="issue-list">
    <!-- 页面头部 -->
    <div class="header">
      <h1>问题列表</h1>
      <el-button type="primary" @click="showCreateForm">
        创建问题
      </el-button>
    </div>

    <!-- 搜索/筛选 -->
    <div class="filters">
      <el-input 
        v-model="searchKeyword"
        placeholder="搜索问题..."
      />
      <el-select
        v-model="selectedStatus"
        placeholder="筛选状态"
        clearable
        @change="handleFilterChange"
      >
        <el-option
          v-for="status in Object.values(IssueStatus)"
          :key="status"
          :label="getStatusLabel(status)"
          :value="status"
        />
      </el-select>
      <el-select
        v-model="selectedPriority"
        placeholder="筛选优先级"
        clearable
        @change="handleFilterChange"
      >
        <el-option
          v-for="priority in Object.values(IssuePriority)"
          :key="priority"
          :label="getPriorityLabel(priority)"
          :value="priority"
        />
      </el-select>
    </div>

    <!-- 问题表格 -->
    <el-table
      :data="filteredIssues"
      @row-click="handleRowClick"
      stripe
    >
      <el-table-column prop="issueNumber" label="编号" width="100" />
      <el-table-column prop="title" label="标题" min-width="200" />
      <el-table-column 
        label="优先级"
        width="100"
        :filters="priorityFilters"
      >
        <template #default="{ row }">
          <el-tag :type="getPriorityType(row.priority)">
            {{ getPriorityLabel(row.priority) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column 
        label="状态"
        width="100"
        :filters="statusFilters"
      >
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">
            {{ getStatusLabel(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column 
        prop="assignee.username"
        label="处理人"
        width="120"
      />
      <el-table-column
        prop="createdAt"
        label="创建时间"
        width="180"
        :formatter="formatDate"
      />
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button 
            link
            type="primary"
            @click.stop="handleEdit(row)"
          >
            编辑
          </el-button>
          <el-button 
            link
            type="danger"
            @click.stop="handleDelete(row)"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
      v-model:current-page="currentPage"
      v-model:page-size="pageSize"
      :page-sizes="[10, 20, 50, 100]"
      :total="pagination.total"
      @change="handlePageChange"
    />

    <!-- 创建/编辑对话框 -->
    <IssueFormDialog
      v-model:visible="formVisible"
      :issue="editingIssue"
      :project-id="projectId"
      @success="handleFormSuccess"
    />
  </div>
</template>
```

**关键逻辑**:
- 从路由参数获取项目 ID
- 加载问题列表，支持分页
- 实时搜索和筛选
- 表格操作（编辑、删除）
- 分页处理

#### 4.3.4 `src/views/issue/IssueDetail.vue` - 问题详情页面

**功能**:
- 展示问题完整信息（标题、描述、优先级、状态等）
- 编辑问题按钮
- 删除问题按钮
- 问题属性面板（处理人、创建人、时间等）
- 问题评论功能（Day 7）
- 返回列表导航

**结构**:
```vue
<template>
  <div class="issue-detail" v-if="currentIssue">
    <!-- 面包屑 -->
    <el-breadcrumb>
      <el-breadcrumb-item :to="'/projects'">项目</el-breadcrumb-item>
      <el-breadcrumb-item :to="'/projects/' + projectId">
        {{ projectName }}
      </el-breadcrumb-item>
      <el-breadcrumb-item :to="'/projects/' + projectId + '/issues'">
        问题列表
      </el-breadcrumb-item>
      <el-breadcrumb-item>{{ currentIssue.issueNumber }}</el-breadcrumb-item>
    </el-breadcrumb>

    <div class="detail-container">
      <!-- 主内容区 -->
      <div class="main-content">
        <div class="issue-header">
          <h1>{{ currentIssue.title }}</h1>
          <div class="actions">
            <el-button @click="showEditForm">编辑</el-button>
            <el-button type="danger" @click="handleDelete">删除</el-button>
          </div>
        </div>

        <!-- 描述 -->
        <div class="description">
          <h3>描述</h3>
          <p>{{ currentIssue.description }}</p>
        </div>

        <!-- 评论（Day 7） -->
        <div class="comments">
          <CommentList :issue-id="issueId" />
        </div>
      </div>

      <!-- 右侧属性面板 -->
      <aside class="properties-panel">
        <div class="property">
          <label>编号</label>
          <p>{{ currentIssue.issueNumber }}</p>
        </div>

        <div class="property">
          <label>状态</label>
          <el-select
            v-model="currentIssue.status"
            @change="handleStatusChange"
          >
            <el-option
              v-for="status in Object.values(IssueStatus)"
              :key="status"
              :label="getStatusLabel(status)"
              :value="status"
            />
          </el-select>
        </div>

        <div class="property">
          <label>优先级</label>
          <el-select
            v-model="currentIssue.priority"
            @change="handlePriorityChange"
          >
            <el-option
              v-for="priority in Object.values(IssuePriority)"
              :key="priority"
              :label="getPriorityLabel(priority)"
              :value="priority"
            />
          </el-select>
        </div>

        <div class="property">
          <label>处理人</label>
          <p v-if="currentIssue.assignee">
            {{ currentIssue.assignee.username }}
          </p>
          <p v-else>未分配</p>
        </div>

        <div class="property">
          <label>创建人</label>
          <p>{{ currentIssue.reporter.username }}</p>
        </div>

        <div class="property">
          <label>创建时间</label>
          <p>{{ formatDate(currentIssue.createdAt) }}</p>
        </div>

        <div class="property">
          <label>更新时间</label>
          <p>{{ formatDate(currentIssue.updatedAt) }}</p>
        </div>
      </aside>
    </div>

    <!-- 编辑对话框 -->
    <IssueFormDialog
      v-model:visible="formVisible"
      :issue="currentIssue"
      :project-id="projectId"
      @success="handleFormSuccess"
    />
  </div>
</template>
```

**关键逻辑**:
- 从路由参数获取问题 ID
- 加载问题详情
- 支持快速状态/优先级切换
- 编辑和删除操作
- 返回导航

### 第四层：组件和表单

#### 4.4.1 `src/components/IssueForm.vue` - 问题创建/编辑表单

**功能**:
- 表单字段：标题、描述、优先级、状态、处理人
- 表单验证
- 创建/编辑模式切换
- 提交和取消按钮

**Props**:
```typescript
interface Props {
  issue?: Issue    // 编辑时传入
  projectId: number
}
```

**Emits**:
```typescript
emit<{
  success: [issue: Issue]
  cancel: []
}>()
```

#### 4.4.2 `src/components/IssueFormDialog.vue` - 问题表单对话框

**功能**:
- 对话框容器
- 调用 IssueForm 组件
- 处理表单提交结果

#### 4.4.3 `src/components/ProjectCard.vue` - 项目卡片

**功能**:
- 展示项目基本信息（名称、描述、图标）
- 展示项目统计（问题数等）
- 操作按钮（编辑、删除）

#### 4.4.4 `src/components/ProjectFormDialog.vue` - 项目表单对话框

#### 4.4.5 `src/components/StatCard.vue` - 统计卡片

**功能**:
- 展示统计数据（图标、标题、数字）

#### 4.4.6 `src/components/layout/AppLayout.vue` - 主布局容器

**功能**:
- 导航栏（AppHeader）
- 侧边栏（AppSidebar）
- 主内容区（插槽）

#### 4.4.7 `src/components/layout/AppHeader.vue` - 顶部导航栏

**功能**:
- 显示当前工作区
- 显示用户名/头像
- 退出登录按钮
- 工作区切换

#### 4.4.8 `src/components/layout/AppSidebar.vue` - 侧边栏

**功能**:
- 导航菜单（项目、问题、设置）
- 当前项目展示
- 快速创建菜单

#### 4.4.9 `src/components/common/EmptyState.vue` - 空状态组件

#### 4.4.10 `src/components/common/LoadingSpinner.vue` - 加载动画

### 第五层：路由配置

#### 4.5.1 `src/router/index.ts` - 路由更新

**新增路由**:
```typescript
{
  path: '/',
  component: () => import('@/components/layout/AppLayout.vue'),
  meta: { requiresAuth: true },
  children: [
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
      path: 'issues/:id',
      name: 'IssueDetail',
      component: () => import('@/views/issue/IssueDetail.vue')
    }
  ]
}
```

---

## 5. 验收标准

### 功能验收

- [ ] **项目列表页面**
  - [ ] 展示当前工作区的所有项目
  - [ ] 创建项目功能正常
  - [ ] 编辑项目功能正常
  - [ ] 删除项目功能正常
  - [ ] 搜索项目功能正常
  - [ ] 点击项目进入详情页面

- [ ] **项目详情页面**
  - [ ] 展示项目信息（名称、描述等）
  - [ ] 展示问题统计卡片
  - [ ] 导航到问题列表页面
  - [ ] 编辑项目按钮可用
  - [ ] 创建问题快捷按钮可用

- [ ] **问题列表页面**
  - [ ] 展示所有问题列表
  - [ ] 创建问题功能正常
  - [ ] 编辑问题功能正常
  - [ ] 删除问题功能正常
  - [ ] 搜索问题功能正常
  - [ ] 筛选问题（按状态、优先级）正常
  - [ ] 分页功能正常
  - [ ] 点击问题进入详情页面

- [ ] **问题详情页面**
  - [ ] 展示问题完整信息
  - [ ] 展示问题属性面板（编号、状态、优先级、处理人等）
  - [ ] 快速切换问题状态/优先级
  - [ ] 编辑问题按钮可用
  - [ ] 删除问题按钮可用
  - [ ] 面包屑导航可用

### 技术验收

- [ ] **TypeScript 类型检查**
  - [ ] `npm run type-check` 无错误
  - [ ] 所有 API 响应都有类型定义
  - [ ] Props/Emits 都用 interface 定义
  - [ ] 无 `any` 类型（除极特殊情况）

- [ ] **API 集成**
  - [ ] 所有后端 API 端点都有对应的 API 调用函数
  - [ ] 请求头正确添加 Authorization token
  - [ ] 错误处理正确（401、403、404、500）
  - [ ] 加载状态管理正确

- [ ] **Pinia Store**
  - [ ] Project Store 工作正常
  - [ ] Issue Store 工作正常
  - [ ] State/Actions/Getters 逻辑清晰
  - [ ] 数据不重复存储

- [ ] **路由**
  - [ ] 路由定义完整
  - [ ] 路由守卫正常工作
  - [ ] 面包屑导航正确
  - [ ] 历史记录导航正常

### UI/UX 验收

- [ ] **视觉设计**
  - [ ] 配色方案符合 variables.scss 定义
  - [ ] 文字大小/颜色清晰可读
  - [ ] 空白间距合理
  - [ ] 响应式布局正常

- [ ] **交互设计**
  - [ ] 加载中显示加载提示
  - [ ] 空状态显示友好提示
  - [ ] 表单验证实时显示错误
  - [ ] 操作成功显示提示消息
  - [ ] 删除操作前有确认对话框

- [ ] **无障碍**
  - [ ] 按钮有清晰的 hover 效果
  - [ ] 表单标签清晰
  - [ ] 必填字段用 * 标记

---

## 6. 风险和应对

### 6.1 前后端 API 格式不一致

**风险**: 后端返回的字段名称与前端类型定义不一致（如 camelCase vs snake_case）

**应对**:
- 在 API 调用层进行字段映射转换
- 使用 axios 拦截器统一处理字段名转换
- 核对 API 文档确保字段名一致

```typescript
// 示例：响应拦截器中进行字段转换
service.interceptors.response.use((response) => {
  const { data } = response
  // 将 snake_case 转换为 camelCase
  return convertSnakeToCamel(data)
})
```

### 6.2 网络错误处理

**风险**: 网络不稳定导致请求失败、超时

**应对**:
- Axios 设置合理的超时时间（15秒）
- 实现重试机制（可选，对 GET 请求）
- 显示友好的错误提示
- 保存用户输入，避免丢失

```typescript
// 实现重试机制
const axiosRetry = require('axios-retry')
axiosRetry(axios, { retries: 3, retryDelay: axiosRetry.exponentialDelay })
```

### 6.3 加载状态管理

**风险**: 长时间加载导致用户困惑，重复点击导致重复请求

**应对**:
- 显示加载动画或骨架屏
- 禁用操作按钮直到请求完成
- 在 API 调用前检查 loading 状态

```typescript
// 防止重复提交
async function handleSubmit() {
  if (loading.value) return
  loading.value = true
  try {
    await submitForm()
  } finally {
    loading.value = false
  }
}
```

### 6.4 Token 过期处理

**风险**: Token 过期时请求失败，用户需要重新登录

**应对**:
- Axios 拦截器捕获 401 错误
- 清除本地 Token
- 重定向到登录页面
- 显示友好提示

```typescript
// 已在 request.ts 中实现
if (status === 401) {
  ElMessage.error('登录已过期，请重新登录')
  removeToken()
  router.push('/login')
}
```

### 6.5 数据同步问题

**风险**: 本地 Store 和服务器数据不同步

**应对**:
- 编辑/删除操作后重新加载列表
- 使用 optimistic update（可选，高级）
- 定期刷新数据
- 冲突时显示冲突提示

```typescript
// 编辑后重新加载
async function handleUpdateIssue(form) {
  await issueApi.update(issueId, form)
  await fetchIssueDetail(issueId) // 重新加载
}
```

---

## 7. 工作任务分解

### Phase 1：基础设施（1-1.5 小时）

- [ ] 创建类型定义文件（project.ts, issue.ts）
- [ ] 创建 API 调用文件（project.api.ts, issue.api.ts, workspace.api.ts）
- [ ] 创建 Pinia Store（project.ts, issue.ts, workspace.ts）
- [ ] 更新路由配置（router/index.ts）

### Phase 2：布局组件（1-1.5 小时）

- [ ] 实现 AppLayout.vue（主布局容器）
- [ ] 实现 AppHeader.vue（顶部导航）
- [ ] 实现 AppSidebar.vue（侧边栏）
- [ ] 实现公共组件（EmptyState, LoadingSpinner, StatCard）

### Phase 3：项目管理（1-1.5 小时）

- [ ] 实现 ProjectList.vue（项目列表页面）
- [ ] 实现 ProjectDetail.vue（项目详情页面）
- [ ] 实现 ProjectCard.vue（项目卡片组件）
- [ ] 实现 ProjectFormDialog.vue（项目表单对话框）

### Phase 4：问题管理（1.5-2 小时）

- [ ] 实现 IssueList.vue（问题列表页面）
- [ ] 实现 IssueDetail.vue（问题详情页面）
- [ ] 实现 IssueForm.vue（问题表单）
- [ ] 实现 IssueFormDialog.vue（问题表单对话框）

### Phase 5：测试和调试（0.5-1 小时）

- [ ] TypeScript 类型检查
- [ ] 功能测试（创建、编辑、删除、搜索、筛选）
- [ ] API 集成测试
- [ ] UI/UX 视觉检查
- [ ] 性能优化（如需要）

---

## 8. 关键技术细节

### 8.1 字段名转换（snake_case → camelCase）

后端使用 snake_case（如 `project_id`），前端使用 camelCase（如 `projectId`）

**解决方案**：在 API 调用层进行转换

```typescript
// src/utils/transform.ts
export function snakeToCamel(obj: any): any {
  if (Array.isArray(obj)) {
    return obj.map(snakeToCamel)
  }
  if (obj !== null && obj.constructor === Object) {
    return Object.keys(obj).reduce((result, key) => {
      const camelKey = key.replace(/_([a-z])/g, (_, letter) => letter.toUpperCase())
      result[camelKey] = snakeToCamel(obj[key])
      return result
    }, {} as any)
  }
  return obj
}

export function camelToSnake(obj: any): any {
  if (Array.isArray(obj)) {
    return obj.map(camelToSnake)
  }
  if (obj !== null && obj.constructor === Object) {
    return Object.keys(obj).reduce((result, key) => {
      const snakeKey = key.replace(/([A-Z])/g, '_$1').toLowerCase()
      result[snakeKey] = camelToSnake(obj[key])
      return result
    }, {} as any)
  }
  return obj
}
```

### 8.2 分页处理

**前端分页参数**: `page`, `size`  
**后端分页响应**: `{items: [], pagination: {page, size, total, pages}}`

```typescript
// 获取问题列表（带分页）
async function fetchIssues() {
  const query: IssueListQuery = {
    projectId: props.projectId,
    status: selectedStatus.value,
    priority: selectedPriority.value,
    page: currentPage.value,
    size: pageSize.value
  }
  const response = await issueApi.list(query)
  issues.value = response.items
  pagination.value = response.pagination
}
```

### 8.3 表单验证规则

使用 Element Plus 的表单验证

```typescript
// 项目表单规则
const projectFormRules = {
  name: [
    { required: true, message: '项目名称不能为空', trigger: 'blur' },
    { min: 1, max: 100, message: '项目名称长度在1-100个字符', trigger: 'blur' }
  ],
  identifier: [
    { required: true, message: '项目标识符不能为空', trigger: 'blur' },
    { min: 2, max: 10, message: '项目标识符长度在2-10个字符', trigger: 'blur' },
    { 
      pattern: /^[A-Z]+$/, 
      message: '项目标识符只能包含大写字母', 
      trigger: 'blur' 
    }
  ]
}
```

### 8.4 优先级和状态映射

```typescript
// 优先级标签和类型
export const PRIORITY_MAP = {
  [IssuePriority.NONE]: { label: '无', type: 'info' },
  [IssuePriority.LOW]: { label: '低', type: 'success' },
  [IssuePriority.MEDIUM]: { label: '中', type: 'warning' },
  [IssuePriority.HIGH]: { label: '高', type: 'danger' },
  [IssuePriority.URGENT]: { label: '紧急', type: 'danger' }
}

// 状态标签和类型
export const STATUS_MAP = {
  [IssueStatus.TODO]: { label: '待办', type: 'info' },
  [IssueStatus.IN_PROGRESS]: { label: '进行中', type: 'warning' },
  [IssueStatus.DONE]: { label: '已完成', type: 'success' },
  [IssueStatus.CLOSED]: { label: '已关闭', type: 'danger' }
}
```

---

## 9. 相关文件参考

- **API 规范**: `/docs/conventions/api.md`
- **前端实现方案**: `/docs/architecture/frontend-implementation.md`
- **数据库设计**: `/docs/architecture/database-design.md`
- **开发计划**: `/docs/development-plan.md`

---

## 10. 更新记录

| 版本 | 日期 | 变更说明 |
|------|------|----------|
| v1.0 | 2026-03-30 | Day 6 完整实现方案设计 |

