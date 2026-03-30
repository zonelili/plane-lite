# Day 6 快速参考指南

## 文件清单（按优先级）

### Layer 1: 类型和 API（必先完成）
1. `src/types/project.ts` - 项目类型定义
2. `src/types/issue.ts` - 问题类型定义  
3. `src/api/workspace.api.ts` - 工作区 API
4. `src/api/project.api.ts` - 项目 API
5. `src/api/issue.api.ts` - 问题 API

### Layer 2: 状态管理
6. `src/stores/workspace.ts` - 工作区 Store
7. `src/stores/project.ts` - 项目 Store
8. `src/stores/issue.ts` - 问题 Store

### Layer 3: 布局和基础组件
9. `src/components/layout/AppLayout.vue` - 主布局
10. `src/components/layout/AppHeader.vue` - 顶部导航
11. `src/components/layout/AppSidebar.vue` - 侧边栏
12. `src/components/common/EmptyState.vue` - 空状态
13. `src/components/common/LoadingSpinner.vue` - 加载动画
14. `src/components/common/StatCard.vue` - 统计卡片

### Layer 4: 项目管理
15. `src/views/project/ProjectList.vue` - 项目列表页
16. `src/views/project/ProjectDetail.vue` - 项目详情页
17. `src/components/ProjectCard.vue` - 项目卡片
18. `src/components/ProjectFormDialog.vue` - 项目表单对话框

### Layer 5: 问题管理  
19. `src/views/issue/IssueList.vue` - 问题列表页
20. `src/views/issue/IssueDetail.vue` - 问题详情页
21. `src/components/IssueForm.vue` - 问题表单
22. `src/components/IssueFormDialog.vue` - 问题表单对话框

### Layer 6: 路由配置
23. `src/router/index.ts` - 更新路由配置

---

## 核心 API 端点速查

### 工作区
```
GET /api/v1/workspaces                    - 获取工作区列表
GET /api/v1/workspaces/{id}               - 获取工作区详情
```

### 项目
```
GET /api/v1/projects?workspace_id=1       - 获取项目列表
GET /api/v1/projects/{id}                 - 获取项目详情
POST /api/v1/projects                     - 创建项目
PUT /api/v1/projects/{id}                 - 更新项目
DELETE /api/v1/projects/{id}              - 删除项目
```

### 问题
```
GET /api/v1/issues?project_id=1&page=1   - 获取问题列表（分页）
GET /api/v1/issues/{id}                   - 获取问题详情
POST /api/v1/issues                       - 创建问题
PUT /api/v1/issues/{id}                   - 更新问题
DELETE /api/v1/issues/{id}                - 删除问题
GET /api/v1/issues/board?project_id=1    - 获取看板数据
```

---

## 枚举值参考

### 问题优先级 (IssuePriority)
- `none` - 无
- `low` - 低
- `medium` - 中
- `high` - 高
- `urgent` - 紧急

### 问题状态 (IssueStatus)
- `todo` - 待办
- `in_progress` - 进行中
- `done` - 已完成
- `closed` - 已关闭

---

## 色彩方案（SCSS 变量）

| 用途 | 变量名 | 值 |
|------|--------|-----|
| 主颜色 | `$primary-color` | #409eff (蓝) |
| 成功 | `$success-color` | #67c23a (绿) |
| 警告 | `$warning-color` | #e6a23c (橙) |
| 危险 | `$danger-color` | #f56c6c (红) |
| 信息 | `$info-color` | #909399 (灰) |

---

## 表单验证规则

### 项目表单
```typescript
name: 必填，1-100字符
identifier: 必填，2-10字符，大写字母
description: 可选，最多500字符
icon: 可选，emoji
```

### 问题表单
```typescript
title: 必填，1-200字符
description: 可选，最多5000字符
priority: 必选
status: 必选
projectId: 必填
assigneeId: 可选
```

---

## 关键实现细节

### 1. 字段名转换
- 后端: snake_case (project_id, issue_number)
- 前端: camelCase (projectId, issueNumber)
- 在 API 调用层进行转换

### 2. 分页处理
```
请求: GET /api/v1/issues?project_id=1&page=1&size=20
响应: { items: [...], pagination: { page, size, total, pages } }
```

### 3. 错误处理
- 401: 清除 Token，重定向登录
- 403: 显示无权限提示
- 404: 显示资源不存在
- 500: 显示服务器错误

### 4. 加载状态
- 显示 loading 动画
- 禁用操作按钮
- 防止重复提交

---

## 工作分解时间表

| 阶段 | 任务 | 预计时间 |
|------|------|---------|
| Phase 1 | 基础设施（类型、API、Store、路由） | 1-1.5h |
| Phase 2 | 布局和基础组件 | 1-1.5h |
| Phase 3 | 项目管理页面 | 1-1.5h |
| Phase 4 | 问题管理页面 | 1.5-2h |
| Phase 5 | 测试和调试 | 0.5-1h |
| **总计** | | **5-6h** |

---

## 验收检查清单

### 功能检查
- [ ] 项目列表：展示、创建、编辑、删除、搜索
- [ ] 项目详情：信息展示、统计卡片、导航
- [ ] 问题列表：展示、创建、编辑、删除、搜索、筛选、分页
- [ ] 问题详情：信息展示、属性面板、快速编辑

### 技术检查
- [ ] TypeScript 类型检查无错
- [ ] API 集成完整
- [ ] Pinia Store 逻辑清晰
- [ ] 路由配置正确

### UI/UX 检查
- [ ] 配色符合 variables.scss
- [ ] 加载/空状态提示清晰
- [ ] 表单验证实时显示
- [ ] 操作成功显示反馈

---

## 常见问题解决

### Q: API 返回 snake_case，怎么转换？
A: 在 API 响应拦截器中进行字段转换，使用工具函数 `snakeToCamel()`

### Q: 怎么避免重复提交？
A: 提交前检查 loading 状态，成功后禁用按钮

### Q: 怎么处理分页？
A: 使用 currentPage 和 pageSize ref，监听变化时重新加载

### Q: Token 过期怎么办？
A: Axios 拦截器捕获 401，清除 Token，重定向登录

---

## 快速命令

```bash
# 类型检查
npm run type-check

# 开发服务
npm run dev

# 构建生产
npm run build

# 预览生产构建
npm run preview
```

---

**完整文档**: `.claude/plans/day-6-plan.md`
