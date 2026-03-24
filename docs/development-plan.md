# Plane Lite MVP 开发计划

**创建日期**：2026-03-24
**计划周期**：7 天
**开发策略**：先完成后端，再开发前端（方案 A）

---

## 📊 总体进度

| 阶段 | 状态 | 开始时间 | 完成时间 | 进度 |
|------|------|----------|----------|------|
| Phase 1: 后端开发 | 🏃 进行中 | 2026-03-24 | 预计 03-27 | 25% |
| Phase 2: 前端开发 | ⏳ 待开始 | 预计 03-28 | 预计 03-30 | 0% |
| Phase 3: 集成测试 | ⏳ 待开始 | 预计 03-30 | 预计 03-30 | 0% |
| Phase 4: 部署上线 | ⏳ 待开始 | 预计 03-30 | 预计 03-30 | 0% |

---

## 🎯 Phase 1: 后端开发（Day 1-4）

### Day 1 - 2026-03-24 ✅ 已完成

**目标**：搭建后端基础架构和用户认证

#### ✅ 已完成任务
- [x] 创建后端项目骨架
  - Maven 配置
  - Spring Boot 配置文件
  - 统一响应封装
  - 全局异常处理
  - MyBatis-Plus 配置

- [x] 创建数据库初始化脚本
  - 5 张核心表结构
  - 索引设计
  - 测试数据

- [x] 实现用户认证模块
  - User 实体
  - UserMapper
  - UserService/UserServiceImpl
  - UserController
  - JWT Token 生成和验证
  - BCrypt 密码加密
  - 3 个 API 接口

**成果**：
- 代码行数：~1330 行
- Git 提交：3 次
- API 接口：3 个

**API 接口清单**：
- POST /api/v1/auth/register - 用户注册
- POST /api/v1/auth/login - 用户登录
- GET /api/v1/auth/me - 获取当前用户信息

---

### Day 2 - 2026-03-25 ⏳ 待开始

**目标**：实现工作区和项目管理模块

#### 计划任务

**1. 工作区管理模块（预计 1.5 小时）**
- [ ] Workspace 实体
- [ ] WorkspaceMapper
- [ ] WorkspaceService/WorkspaceServiceImpl
- [ ] WorkspaceController
- [ ] 功能：
  - 用户注册时自动创建默认工作区
  - 获取当前用户的工作区

**API 接口**：
- GET /api/v1/workspaces - 获取当前用户的工作区列表
- GET /api/v1/workspaces/{id} - 获取工作区详情

**2. 项目管理模块（预计 2-2.5 小时）**
- [ ] Project 实体
- [ ] ProjectMapper
- [ ] ProjectService/ProjectServiceImpl
- [ ] ProjectController
- [ ] 功能：
  - 创建项目
  - 获取项目列表
  - 获取项目详情
  - 更新项目
  - 删除项目

**API 接口**：
- GET /api/v1/projects?workspace_id=1 - 获取项目列表
- GET /api/v1/projects/{id} - 获取项目详情
- POST /api/v1/projects - 创建项目
- PUT /api/v1/projects/{id} - 更新项目
- DELETE /api/v1/projects/{id} - 删除项目

**验收标准**：
- [ ] 所有 API 在 Swagger 中可测试
- [ ] 参数校验正常
- [ ] 错误处理完整
- [ ] 日志记录合理

---

### Day 3 - 2026-03-26 ⏳ 待开始

**目标**：实现问题管理模块（核心功能）

#### 计划任务

**1. 问题管理模块（预计 4-5 小时）**
- [ ] Issue 实体
- [ ] IssueMapper + XML（自动生成 sequence_id）
- [ ] IssueService/IssueServiceImpl
- [ ] IssueController
- [ ] 功能：
  - 创建问题（自动生成问题编号 PROJ-1）
  - 获取问题列表（支持筛选：状态、优先级、负责人）
  - 获取问题详情
  - 更新问题
  - 删除问题
  - 问题状态流转
  - 获取看板数据（按状态分组）

**API 接口**：
- GET /api/v1/issues?project_id=1&status=todo&priority=high - 获取问题列表
- GET /api/v1/issues/{id} - 获取问题详情
- POST /api/v1/issues - 创建问题
- PUT /api/v1/issues/{id} - 更新问题
- DELETE /api/v1/issues/{id} - 删除问题
- GET /api/v1/issues/board?project_id=1 - 获取看板数据

**技术要点**：
- sequence_id 自动递增（使用 MyBatis-Plus 自定义逻辑）
- 问题编号生成：{project.identifier}-{sequence_id}
- 状态枚举：todo, in_progress, done, closed
- 优先级枚举：none, low, medium, high, urgent

**验收标准**：
- [ ] 问题编号正确生成（如 PL-1, PL-2）
- [ ] 状态流转正常
- [ ] 筛选和排序功能正常
- [ ] 看板数据正确分组

---

### Day 4 - 2026-03-27 ⏳ 待开始

**目标**：实现评论模块，完善和测试后端

#### 计划任务

**1. 评论管理模块（预计 1.5 小时）**
- [ ] IssueComment 实体
- [ ] IssueCommentMapper
- [ ] IssueCommentService/IssueCommentServiceImpl
- [ ] IssueCommentController
- [ ] 功能：
  - 获取问题的评论列表
  - 添加评论
  - 删除评论（仅评论作者可删除）

**API 接口**：
- GET /api/v1/comments?issueId={id} - 获取问题评论列表（按时间倒序）
- POST /api/v1/comments - 添加评论
- DELETE /api/v1/comments/{id} - 删除评论

**2. 后端完善和测试（预计 2-3 小时）**
- [ ] 用 Swagger 测试所有 API
- [ ] 修复发现的 Bug
- [ ] 完善错误处理
- [ ] 优化日志输出
- [ ] 添加关键业务的单元测试（可选）
- [ ] 更新 API 文档

**验收标准**：
- [ ] 所有 API 接口测试通过
- [ ] 无明显 Bug
- [ ] 错误处理完整
- [ ] Swagger 文档完整

---

## 📊 Phase 1 总结

### 预计产出

| 模块 | 实体 | 接口数 | 预计代码行数 |
|------|------|--------|--------------|
| 用户认证 | 1 | 3 | ✅ ~537 行 |
| 工作区 | 1 | 2 | ~300 行 |
| 项目 | 1 | 5 | ~500 行 |
| 问题 | 1 | 6 | ~800 行 |
| 评论 | 1 | 3 | ~350 行 |
| **总计** | **5** | **19** | **~2487 行** |

### API 接口总览

```
认证模块 (3)
├── POST   /api/v1/auth/register
├── POST   /api/v1/auth/login
└── GET    /api/v1/auth/me

工作区模块 (2)
├── GET    /api/v1/workspaces
└── GET    /api/v1/workspaces/{id}

项目模块 (5)
├── GET    /api/v1/projects
├── GET    /api/v1/projects/{id}
├── POST   /api/v1/projects
├── PUT    /api/v1/projects/{id}
└── DELETE /api/v1/projects/{id}

问题模块 (6)
├── GET    /api/v1/issues
├── GET    /api/v1/issues/{id}
├── POST   /api/v1/issues
├── PUT    /api/v1/issues/{id}
├── DELETE /api/v1/issues/{id}
└── GET    /api/v1/issues/board

评论模块 (3)
├── GET    /api/v1/issues/{id}/comments
├── POST   /api/v1/issues/{id}/comments
└── DELETE /api/v1/issues/{id}/comments/{commentId}
```

---

## 🎨 Phase 2: 前端开发（Day 5-7）

### Day 5 - 2026-03-28 ⏳ 待开始

**目标**：前端项目搭建和用户认证页面

#### 计划任务
- [ ] 创建 Vue 3 + TypeScript 项目
- [ ] 配置 Vite、路由、状态管理
- [ ] 封装 Axios（请求拦截器、响应拦截器）
- [ ] 实现登录页面
- [ ] 实现注册页面
- [ ] 实现路由守卫（JWT Token 验证）

**预计工时**：4-5 小时

---

### Day 6 - 2026-03-29 ⏳ 待开始

**目标**：项目和问题管理页面

#### 计划任务
- [ ] 项目列表页面
- [ ] 项目详情页面
- [ ] 问题列表页面
- [ ] 问题详情页面
- [ ] 问题创建/编辑表单
- [ ] 问题筛选功能

**预计工时**：5-6 小时

---

### Day 7 - 2026-03-30 ⏳ 待开始

**目标**：看板视图、评论功能、部署

#### 计划任务
- [ ] 看板视图（按状态分组）
- [ ] 问题状态切换
- [ ] 评论列表和添加评论
- [ ] UI 优化和响应式适配
- [ ] Docker 部署配置
- [ ] 部署测试

**预计工时**：5-6 小时

---

## ✅ Phase 3: 集成测试（Day 7）

- [ ] 完整业务流程测试
  - 注册 → 登录 → 创建项目 → 创建问题 → 看板查看 → 添加评论
- [ ] 异常场景测试
- [ ] 浏览器兼容性测试
- [ ] 响应式布局测试

---

## 🚀 Phase 4: 部署上线（Day 7）

- [ ] Docker Compose 配置
- [ ] 环境变量配置
- [ ] 数据库迁移
- [ ] 启动测试
- [ ] 访问验证

---

## 📈 风险和应对

| 风险 | 级别 | 应对措施 |
|------|------|----------|
| API 接口设计变更 | 🟡 中 | 先完成后端再开发前端，减少返工 |
| 问题编号生成逻辑复杂 | 🟡 中 | 使用数据库锁或唯一索引保证唯一性 |
| 前端状态管理复杂 | 🟢 低 | 按模块划分 Store，职责清晰 |
| 时间不足 | 🟢 低 | MVP 功能已简化，可进一步裁剪 |

---

## 🎯 成功标准

### MVP 验收标准
- [ ] 用户可以注册、登录
- [ ] 用户可以创建项目
- [ ] 用户可以创建、编辑、删除问题
- [ ] 用户可以在看板上查看问题
- [ ] 用户可以给问题添加评论
- [ ] 前端界面美观，交互流畅
- [ ] 可以通过 Docker 一键部署

### 质量标准
- [ ] 后端单元测试覆盖核心逻辑
- [ ] 所有 API 接口文档完整
- [ ] 代码符合规范，无明显技术债
- [ ] 无严重 Bug

---

## 📝 更新记录

| 日期 | 更新内容 | 更新者 |
|------|----------|--------|
| 2026-03-24 | 创建开发计划，完成 Day 1 | Claude Opus 4.6 |

---

**下一步行动**：继续 Day 2 任务 - 实现工作区和项目管理模块
