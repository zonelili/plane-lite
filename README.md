# Plane Lite - 轻量级项目管理系统

<div align="center">

![Status](https://img.shields.io/badge/status-in%20development-yellow)
![Version](https://img.shields.io/badge/version-MVP-blue)
![AI Powered](https://img.shields.io/badge/AI-Powered-green)

基于开源项目 [Plane](https://github.com/makeplane/plane) 的简化实现

**用 1 周时间验证 AI Coding 的全流程开发能力**

</div>

---

## 🎯 项目简介

Plane Lite 是一个现代化的轻量级项目管理平台，核心功能包括：

- 📋 **项目管理**：创建和组织项目
- 🎫 **问题追踪**：任务的创建、分配、状态流转
- 📊 **看板视图**：可视化展示工作流
- 💬 **团队协作**：评论、讨论

### 为什么做这个项目？

1. **验证 AI Coding**：测试 AI 能否独立完成一个完整项目
2. **学习最佳实践**：研究 Plane 的设计和实现
3. **技术栈实战**：Spring Boot + Vue 3 全栈开发
4. **实际应用价值**：可用于团队内部项目管理

---

## ✨ 功能特性

### MVP 版本（当前）

✅ **用户系统**
- 用户注册（邮箱 + 密码）
- 用户登录（JWT 认证）
- 用户信息管理

✅ **项目管理**
- 创建/编辑/删除项目
- 项目列表和详情
- 项目图标和描述

✅ **问题管理**
- 创建/编辑/删除问题
- 问题状态流转（待办 → 进行中 → 完成 → 关闭）
- 问题优先级（无、低、中、高、紧急）
- 问题分配和跟踪

✅ **看板视图**
- 按状态分组展示
- 问题卡片展示
- 快速状态切换

✅ **评论系统**
- 问题评论
- 评论列表展示

### 计划中功能

🚧 **V2.0**（第 2 周）
- 标签系统
- 项目成员管理
- 看板拖拽
- 高级搜索

🚧 **V3.0**（第 3 周）
- Cycles（冲刺管理）
- 子问题和依赖关系
- 文件上传
- 通知系统

🚧 **V4.0+**（长期）
- 自定义视图
- 模块管理
- 文档系统
- 数据分析
- AI 功能集成

---

## 🏗️ 技术架构

### 技术栈

**后端**
- **框架**：Spring Boot 3.2
- **ORM**：MyBatis-Plus 3.5
- **数据库**：MySQL 8.0
- **缓存**：Redis 7.x
- **认证**：JWT
- **工具**：Lombok, Validation

**前端**
- **框架**：Vue 3.4 + TypeScript
- **构建**：Vite 5.x
- **UI**：Element Plus
- **状态**：Pinia
- **路由**：Vue Router 4
- **HTTP**：Axios

**基础设施**
- **容器化**：Docker, Docker Compose
- **反向代理**：Nginx
- **API 文档**：Swagger / OpenAPI

### 项目结构

```
plane-lite/
├── backend/              # Spring Boot 后端
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   └── resources/
│   │   └── test/
│   └── pom.xml
├── frontend/             # Vue 3 前端
│   ├── src/
│   │   ├── assets/
│   │   ├── components/
│   │   ├── views/
│   │   ├── api/
│   │   ├── stores/
│   │   └── router/
│   └── package.json
├── deployments/          # 部署配置
│   ├── docker-compose.yml
│   └── nginx/
├── docs/                 # 项目文档
│   ├── requirements/     # 需求文档
│   ├── architecture/     # 架构设计
│   ├── conventions/      # 编码规范
│   └── workflows/        # 工作流程
├── tests/                # 集成测试
├── .harness/             # Harness 配置
├── CLAUDE.md             # AI 开发指南
└── README.md             # 项目说明
```

---

## 🚀 快速开始

### 环境要求

- JDK 17+
- Node.js 18+
- MySQL 8.0+
- Redis 7.0+
- Docker & Docker Compose（可选）

### 本地开发

#### 1. 启动数据库和缓存

```bash
cd deployments
docker-compose up -d mysql redis
```

#### 2. 启动后端

```bash
cd backend
./mvnw spring-boot:run
```

后端服务将运行在 `http://localhost:8080`

#### 3. 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端服务将运行在 `http://localhost:5173`

### Docker 部署

```bash
cd deployments
docker-compose up -d
```

访问 `http://localhost` 即可使用。

---

## 📖 文档

### 核心文档

- **[CLAUDE.md](./CLAUDE.md)** - AI 开发指南（项目地图）
- **[项目概述](./docs/requirements/01-project-overview.md)** - 项目背景和版本规划
- **[数据库设计](./docs/architecture/database-design.md)** - 数据库表结构和 ER 图
- **[API 设计规范](./docs/conventions/api.md)** - RESTful API 接口文档

### 文档导航

详见 **[docs/README.md](./docs/README.md)**

---

## 🧪 测试

### 后端测试

```bash
cd backend
./mvnw test                    # 单元测试
./mvnw verify                  # 集成测试
./mvnw test jacoco:report      # 测试覆盖率
```

### 前端测试

```bash
cd frontend
npm run test                   # 单元测试
npm run test:e2e               # E2E 测试
```

---

## 📊 项目进度

### 开发计划

| 阶段 | 功能 | 状态 | 预计完成 |
|------|------|------|----------|
| Day 1 | 用户认证 + 工作区 | ⏳ 进行中 | 2026-03-24 |
| Day 2 | 项目管理 | ⏳ 待开始 | 2026-03-25 |
| Day 3-4 | 问题管理 | ⏳ 待开始 | 2026-03-27 |
| Day 5 | 看板视图 | ⏳ 待开始 | 2026-03-28 |
| Day 6-7 | 优化和部署 | ⏳ 待开始 | 2026-03-30 |

### MVP 验收标准

- [ ] 用户可以注册、登录
- [ ] 用户可以创建项目
- [ ] 用户可以在项目中创建问题
- [ ] 用户可以修改问题状态
- [ ] 用户可以在看板上查看问题
- [ ] 用户可以给问题添加评论
- [ ] 前端界面美观，交互流畅
- [ ] 可以通过 Docker 一键部署

---

## 🤝 开发规范

### 代码规范

详见 [编码规范](./docs/conventions/coding-standards.md)

**核心原则**：
1. 严格分层：Controller → Service → Mapper
2. 统一响应格式
3. 异常统一处理
4. 参数校验
5. 单元测试覆盖率 > 80%

### Git 工作流

详见 [Git 工作流](./docs/workflows/git-workflow.md)

```bash
# 功能开发
git checkout -b feature/user-login
# 开发、提交
git commit -m "feat: 实现用户登录功能"
# 合并到主分支
git checkout main
git merge feature/user-login
```

---

## 📝 更新日志

### [Unreleased]

**新增**
- 项目初始化
- 文档结构搭建
- 技术方案设计

---

## 🔗 相关资源

### 原项目
- **Plane 官网**：https://plane.so
- **Plane GitHub**：https://github.com/makeplane/plane
- **Plane 文档**：https://docs.plane.so

### 技术文档
- **Spring Boot**：https://spring.io/projects/spring-boot
- **Vue 3**：https://vuejs.org/
- **Element Plus**：https://element-plus.org/

---

## 📄 许可证

本项目仅用于学习和研究目的。

原项目 Plane 采用 GNU AGPL v3.0 许可证。

---

## 🙏 致谢

- 感谢 [Plane](https://github.com/makeplane/plane) 团队的开源贡献
- 感谢 Claude AI 提供的开发支持

---

**Last Updated**: 2026-03-23
