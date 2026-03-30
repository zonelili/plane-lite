# Plane Lite 开发计划

## Day 6 实现方案文档

### 文档清单

本目录包含 Day 6 前端开发的完整技术方案：

#### 1. **day-6-plan.md** (1468 行) - 完整实现方案
   - **目的**: 全面的技术设计文档
   - **包含内容**:
     - 技术栈验证（Vue 3、TypeScript、Pinia、组件组织）
     - API 集成清单（所有后端端点、请求/响应格式）
     - 前端设计规范（色彩、布局、交互、表单验证）
     - 组件文件清单（23 个文件，按优先级排序）
     - 验收标准（功能、技术、UI/UX）
     - 风险识别和应对方案
     - 工作任务分解（5 个阶段）
     - 关键技术细节
   - **目标用户**: 技术架构师、项目经理、开发人员

#### 2. **day-6-quick-ref.md** (217 行) - 快速参考指南
   - **目的**: 快速查询和速查表
   - **包含内容**:
     - 23 个文件清单（按优先级分层）
     - API 端点速查表
     - 枚举值参考（优先级、状态）
     - 色彩方案速查
     - 表单验证规则
     - 关键实现细节
     - 工作分解时间表
     - 验收检查清单
     - 常见问题解决
   - **目标用户**: 开发人员（快速查询）

#### 3. **day-6-architecture.md** (529 行) - 架构设计图解
   - **目的**: 可视化架构和数据流
   - **包含内容**:
     - 整体架构图（Browser → Vue → API → Backend → DB）
     - 数据流向（项目列表、创建问题、更新问题）
     - 状态管理架构（3 个 Pinia Store）
     - 组件嵌套关系图
     - 类型系统层级
     - API 调用层架构
     - 路由树结构
     - 错误处理流程
     - 加载状态管理流程
     - 表单验证流程
     - 分页处理流程
   - **目标用户**: 架构师、新加入成员、系统设计审查

---

## 快速导航

### 按用途选择文档

| 用途 | 推荐文档 |
|------|---------|
| 了解整体方案 | `day-6-plan.md` |
| 快速查询信息 | `day-6-quick-ref.md` |
| 理解架构 | `day-6-architecture.md` |
| 查看 API 端点 | `day-6-quick-ref.md` (第 2 部分) |
| 查看文件清单 | `day-6-quick-ref.md` (第 1 部分) 或 `day-6-plan.md` (第 4 部分) |
| 查看色彩方案 | `day-6-quick-ref.md` (第 5 部分) |
| 学习数据流 | `day-6-architecture.md` (第 2 部分) |
| 理解组件结构 | `day-6-architecture.md` (第 5 部分) |

---

## 核心内容速查

### 必读部分

1. **技术栈**（来自 day-6-plan.md）
   - Vue 3 Composition API with `<script setup>`
   - TypeScript (strict mode)
   - Pinia for state management
   - Axios for HTTP client
   - Element Plus for UI components

2. **文件清单**（23 个文件）
   - Layer 1: Types & APIs (5 files)
   - Layer 2: State Management (3 stores)
   - Layer 3: Layout & Common (6 components)
   - Layer 4: Project Management (4 components)
   - Layer 5: Issue Management (4 components)
   - Layer 6: Router (1 file)

3. **核心 API**（来自 day-6-quick-ref.md）
   ```
   Workspaces: GET /workspaces
   Projects: GET/POST/PUT/DELETE /projects
   Issues: GET/POST/PUT/DELETE /issues + board
   ```

4. **验收标准**（来自 day-6-plan.md 第 5 部分）
   - 所有 23 个功能页面
   - TypeScript 类型检查通过
   - 完整 API 集成
   - UI/UX 符合规范

---

## 工作计划

### 时间分解（5-6 小时）

| 阶段 | 时间 | 任务 |
|------|------|------|
| Phase 1 | 1-1.5h | 基础设施（类型、API、Store、路由） |
| Phase 2 | 1-1.5h | 布局和基础组件 |
| Phase 3 | 1-1.5h | 项目管理页面 |
| Phase 4 | 1.5-2h | 问题管理页面 |
| Phase 5 | 0.5-1h | 测试和调试 |

### 优先级（按依赖关系）

**必先完成 (Phase 1)**:
1. Types (project.ts, issue.ts)
2. APIs (workspace.api, project.api, issue.api)
3. Stores (workspace, project, issue)
4. Router (updated index.ts)

**后续阶段**:
5. Layout (AppLayout, AppHeader, AppSidebar)
6. Pages (ProjectList, ProjectDetail, IssueList, IssueDetail)
7. Forms (IssueForm, ProjectForm with dialogs)

---

## 关键设计决策

### 1. 字段名转换
- **后端**: snake_case (project_id, issue_number)
- **前端**: camelCase (projectId, issueNumber)
- **处理**: API 层统一转换

### 2. 错误处理
- 401: 清除 Token，重定向登录
- 403/404: 显示友好提示
- 500: 显示服务器错误
- Network: 显示网络连接错误

### 3. 分页
- 前端维护: page, size, total
- 后端返回: `{items: [...], pagination: {...}}`

### 4. 加载状态
- 显示加载动画/骨架屏
- 禁用操作按钮
- 防止重复提交

### 5. 表单验证
- 必填字段标记 *
- 实时错误提示
- 提交前完整验证
- 后端错误映射到字段

---

## 色彩方案速查

```
Primary (蓝): #409eff - 主操作、链接
Success (绿): #67c23a - 成功、完成
Warning (橙): #e6a23c - 警告、进行中
Danger (红): #f56c6c - 删除、错误、紧急
Info (灰): #909399 - 信息、禁用、无优先级

Text Primary: #303133 - 标题、主文本
Text Regular: #606266 - 常规文本
Text Secondary: #909399 - 次要文本、描述
```

---

## 枚举值映射

### 问题优先级 (IssuePriority)
- none → 无 (gray)
- low → 低 (green)
- medium → 中 (orange)
- high → 高 (red)
- urgent → 紧急 (red, bold)

### 问题状态 (IssueStatus)
- todo → 待办 (blue)
- in_progress → 进行中 (orange)
- done → 已完成 (green)
- closed → 已关闭 (red)

---

## 验收检查清单

### 功能测试
- [ ] 项目列表：加载、创建、编辑、删除、搜索
- [ ] 项目详情：统计卡片、导航
- [ ] 问题列表：加载、创建、编辑、删除、搜索、筛选、分页
- [ ] 问题详情：属性编辑、快速状态切换

### 技术测试
- [ ] `npm run type-check` 无错
- [ ] 所有 API 集成正确
- [ ] Token 认证正常
- [ ] 错误处理完整
- [ ] 加载/空状态处理

### 美观度检查
- [ ] 配色符合 variables.scss
- [ ] 布局合理美观
- [ ] 交互友好
- [ ] 响应式正常

---

## 相关资源

### 项目文档
- `/docs/conventions/api.md` - API 设计规范
- `/docs/architecture/frontend-implementation.md` - 前端实现方案
- `/docs/architecture/database-design.md` - 数据库设计
- `/docs/development-plan.md` - 完整开发计划

### 已完成工作
- ✅ Day 1-4: 后端完整 API
- ✅ Day 5: 前端项目搭建 + 用户认证

### 后续工作
- Day 6: 项目和问题管理页面（本文档）
- Day 7: 看板视图 + 评论功能
- Day 8-10: 优化、部署、测试

---

## 常见问题

### Q: 为什么需要字段转换？
A: 后端使用 snake_case 遵循 Java 命名规范，前端使用 camelCase 遵循 JavaScript 规范。通过转换保持两端一致。

### Q: 怎么避免 Token 过期问题？
A: Axios 拦截器捕获 401 错误，自动清除 Token 并重定向登录。

### Q: 怎么处理大量问题列表？
A: 使用分页加载（每页 20 条），不使用无限滚动（性能考虑）。

### Q: 怎么测试 API 集成？
A: 使用 Postman 或浏览器 DevTools 查看网络请求，对比后端文档验证。

---

## 文档版本历史

| 版本 | 日期 | 状者 |
|------|------|------|
| v1.0 | 2026-03-30 | 初始版本，完整 Day 6 设计方案 |

---

## 如何使用本文档

### 对于项目经理
1. 阅读 `day-6-plan.md` 第 7 部分（工作任务分解）
2. 查看 `day-6-quick-ref.md` 第 6 部分（时间表）
3. 使用验收标准检查进度

### 对于开发人员
1. 先阅读 `day-6-quick-ref.md` 了解全貌
2. 参考 `day-6-plan.md` 实现细节
3. 查看 `day-6-architecture.md` 理解架构
4. 按优先级实现文件

### 对于架构师
1. 阅读 `day-6-plan.md` 第 1-3 部分（技术栈、API、设计规范）
2. 研究 `day-6-architecture.md` 的所有图解
3. 关注风险识别部分（`day-6-plan.md` 第 6 部分）

### 对于新加入成员
1. 从 `day-6-architecture.md` 开始理解整体架构
2. 用 `day-6-quick-ref.md` 快速查询细节
3. 按照 `day-6-plan.md` 第 4 部分的文件清单理解代码结构

---

**生成日期**: 2026-03-30  
**状态**: 设计方案确认  
**下一步**: 开始 Implementation Phase
