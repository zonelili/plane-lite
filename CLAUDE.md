# Plane Lite - 项目地图

> 给 AI Agent 看的项目索引。想了解项目功能和快速开始,请看 `/docs/requirements/01-project-overview.md`

## 项目概述

Plane Lite 是一个现代化的项目管理平台,核心功能:项目管理、问题追踪、看板视图、团队协作。
**目标**:用 1 周时间完成 MVP,验证 AI 全自动开发的可行性。

---

## 📁 项目结构总览

### 根目录文件

```
plane-lite/
├── CLAUDE.md              # 项目地图(给 AI Agent 看)
├── .gitignore             # Git 忽略规则
└── (其他配置文件根据需要添加)
```

---

## 📂 /backend/ - Spring Boot 后端服务

### 核心文件

- `pom.xml` - Maven 依赖配置(Spring Boot 3.1.5 + MyBatis-Plus 3.5.5)
- `README.md` - 后端说明文档

### 目录结构

```
backend/
├── src/main/java/com/planelite/
│   ├── PlaneApplication.java                    # Spring Boot 启动类
│   │
│   ├── common/                                   # 公共组件
│   │   ├── constant/                             # 常量定义
│   │   │   ├── ErrorCode.java                    # 错误码常量
│   │   │   ├── IssueConstants.java               # 问题模块常量(状态、优先级)
│   │   │   └── (其他常量类)
│   │   ├── exception/                            # 自定义异常
│   │   │   ├── BusinessException.java            # 业务异常
│   │   │   ├── UnauthorizedException.java        # 未授权异常
│   │   │   ├── ResourceNotFoundException.java    # 资源不存在异常
│   │   │   └── (其他异常类)
│   │   ├── handler/                              # 全局异常处理器
│   │   │   └── GlobalExceptionHandler.java       # 统一异常处理
│   │   └── result/                               # 统一响应封装
│   │       └── Result.java                       # 统一响应格式{code, message, data}
│   │
│   ├── config/                                   # 配置类
│   │   ├── MybatisPlusConfig.java                # MyBatis-Plus 配置(分页插件)
│   │   └── WebMvcConfig.java                     # Web MVC 配置(CORS、拦截器)
│   │
│   ├── module/                                   # 业务模块(按功能模块划分)
│   │   │
│   │   ├── user/                                 # 用户模块(认证、注册、登录)
│   │   │   ├── controller/
│   │   │   │   └── UserController.java           # 用户 API(/api/v1/auth)
│   │   │   ├── dto/
│   │   │   │   ├── UserRegisterDTO.java          # 注册请求 DTO
│   │   │   │   ├── UserLoginDTO.java             # 登录请求 DTO
│   │   │   │   ├── UserVO.java                   # 用户信息响应 VO
│   │   │   │   └── (其他 DTO)
│   │   │   ├── entity/
│   │   │   │   └── User.java                     # 用户实体类(users 表)
│   │   │   ├── mapper/
│   │   │   │   └── UserMapper.java               # MyBatis Mapper(用户表操作)
│   │   │   └── service/
│   │   │       ├── UserService.java              # 用户服务接口
│   │   │       └── impl/
│   │   │           └── UserServiceImpl.java      # 用户服务实现(注册、登录、信息管理)
│   │   │
│   │   ├── workspace/                            # 工作区模块(工作区管理)
│   │   │   ├── controller/
│   │   │   │   └── WorkspaceController.java      # 工作区 API(/api/v1/workspaces)
│   │   │   ├── dto/
│   │   │   │   ├── WorkspaceCreateDTO.java       # 创建工作区请求 DTO
│   │   │   │   ├── WorkspaceUpdateDTO.java       # 更新工作区请求 DTO
│   │   │   │   ├── WorkspaceVO.java              # 工作区响应 VO
│   │   │   │   └── (其他 DTO)
│   │   │   ├── entity/
│   │   │   │   └── Workspace.java                # 工作区实体类(workspaces 表)
│   │   │   ├── mapper/
│   │   │   │   └── WorkspaceMapper.java          # MyBatis Mapper(工作区表操作)
│   │   │   └── service/
│   │   │       ├── WorkspaceService.java         # 工作区服务接口
│   │   │       └── impl/
│   │   │           └── WorkspaceServiceImpl.java # 工作区服务实现(CRUD)
│   │   │
│   │   ├── project/                              # 项目模块(项目管理)
│   │   │   ├── controller/
│   │   │   │   └── ProjectController.java        # 项目 API(/api/v1/projects)
│   │   │   ├── dto/
│   │   │   │   ├── ProjectCreateDTO.java         # 创建项目请求 DTO
│   │   │   │   ├── ProjectUpdateDTO.java         # 更新项目请求 DTO
│   │   │   │   ├── ProjectVO.java                # 项目响应 VO
│   │   │   │   └── (其他 DTO)
│   │   │   ├── entity/
│   │   │   │   └── Project.java                  # 项目实体类(projects 表)
│   │   │   ├── mapper/
│   │   │   │   └── ProjectMapper.java            # MyBatis Mapper(项目表操作)
│   │   │   └── service/
│   │   │       ├── ProjectService.java           # 项目服务接口
│   │   │       └── impl/
│   │   │           └── ProjectServiceImpl.java   # 项目服务实现(CRUD)
│   │   │
│   │   └── (待实现: issue/, comment/ 等模块)    # 问题管理、评论模块(Day 3+)
│   │
│   ├── security/                                 # 安全组件
│   │   └── JwtTokenProvider.java                 # JWT Token 生成和验证
│   │
│   └── util/                                     # 工具类
│       └── PasswordUtil.java                     # 密码加密工具(BCrypt)
│
├── src/main/resources/
│   ├── application.yml                           # 主配置文件
│   ├── application-dev.yml                       # 开发环境配置(MySQL, Redis, JWT)
│   ├── application-prod.yml                      # 生产环境配置
│   ├── db/migration/                             # Flyway 数据库迁移脚本
│   │   └── V1__init.sql                          # 初始化脚本(5 张表: users, workspaces, projects, issues, comments)
│   └── mapper/                                   # MyBatis XML 映射文件(如需要)
│
└── src/test/java/com/planelite/
    └── (单元测试,待添加)
```

**数据库表清单**(V1__init.sql):
- `users` - 用户表
- `workspaces` - 工作区表
- `projects` - 项目表
- `issues` - 问题表(待实现)
- `comments` - 评论表(待实现)

---

## 📂 /frontend/ - Vue 3 前端应用

**状态**: ⚠️ 待实现(目录已创建,内容未实现)

**计划结构**:
```
frontend/
├── src/
│   ├── assets/                # 静态资源(图片、样式)
│   ├── components/            # Vue 组件
│   ├── views/                 # 页面视图
│   ├── api/                   # API 调用封装
│   ├── stores/                # Pinia 状态管理
│   ├── router/                # Vue Router 路由配置
│   └── main.ts                # 应用入口
├── package.json               # npm 依赖(Vue 3, TypeScript, Vite, Element Plus)
├── vite.config.ts             # Vite 构建配置
├── tsconfig.json              # TypeScript 配置
└── index.html                 # HTML 模板
```

---

## 📂 /deployments/ - Docker 部署配置

**状态**: ⚠️ 待实现(目录已创建,内容未实现)

**计划结构**:
```
deployments/
├── docker-compose.yml         # Docker Compose 编排(MySQL, Redis, Backend, Frontend, Nginx)
├── nginx/                     # Nginx 配置
│   ├── nginx.conf             # Nginx 主配置
│   └── default.conf           # 站点配置(反向代理)
├── backend.Dockerfile         # 后端 Docker 镜像
└── frontend.Dockerfile        # 前端 Docker 镜像
```

---

## 📂 /tests/ - 集成测试和 E2E 测试

**状态**: ⚠️ 待实现(目录已创建,内容未实现)

**计划结构**:
```
tests/
├── integration/               # 集成测试(API 测试)
│   └── api/                   # API 接口测试
└── e2e/                       # E2E 测试(Playwright)
    └── scenarios/             # 测试场景
```

---

## 📂 /.harness/ - Harness 工程配置

```
.harness/
└── checklist.md               # Harness 工程检查清单(质量保障、开发规范)
```

---

## 📂 /docs/ - 项目文档

详细索引见 [docs/README.md](./docs/README.md)

### 目录结构

```
docs/
├── requirements/              # 📋 需求文档(持久)
│   └── 01-project-overview.md # 项目概述、功能范围、版本规划
│
├── architecture/              # 🏗️ 架构设计(持久)
│   ├── system-architecture.md # 系统架构总览(分层架构、技术选型)
│   ├── database-design.md     # 数据库设计(ER 图、表结构、索引设计)
│   ├── backend-implementation.md # 后端实现方案(目录结构、模块划分、技术细节)
│   ├── frontend-implementation.md # 前端实现方案(组件设计、路由、状态管理)
│   └── deployment.md          # 部署方案(Docker、Nginx、环境配置)
│
├── conventions/               # 📖 编码规范(持久)
│   └── api.md                 # API 设计规范(RESTful、请求/响应格式、错误处理)
│
├── workflows/                 # 🔄 工作流程(持久)
│   └── quality-assurance.md   # 质量保障三层机制(Plan Mode、Implementation、Code Review)
│
├── decisions/                 # 🎯 架构决策记录 ADR(持久)
│   └── ADR-001-quality-assurance-workflow.md # ADR-001: 质量保障工作流
│
├── reports/                   # 📊 报告和分析(临时)
│   ├── code-review-day1-2.md  # Day 1-2 代码审查报告
│   ├── documentation-reorganization.md # 文档重组报告
│   └── java-version-fix.md    # Java 版本兼容性问题修复报告
│
├── development-plan.md        # 📅 开发计划(Day 1-7 任务分解)
├── lessons-learned.md         # 💡 经验总结(持续累积)
├── project-kickoff.md         # 🚀 项目启动记录
└── README.md                  # 文档索引和分类规范
```

---

## 🔧 技术栈

### 后端
- **框架**: Spring Boot 3.1.5
- **ORM**: MyBatis-Plus 3.5.5
- **数据库**: MySQL 8.0
- **缓存**: Redis 7.x
- **认证**: JWT(JJWT 0.11.5)
- **工具**: Lombok 1.18.32, Validation
- **文档**: SpringDoc OpenAPI(Swagger)

### 前端(待实现)
- **框架**: Vue 3.4 + TypeScript
- **构建**: Vite 5.x
- **UI**: Element Plus
- **状态**: Pinia
- **路由**: Vue Router 4
- **HTTP**: Axios

### 基础设施(待实现)
- **容器化**: Docker, Docker Compose
- **反向代理**: Nginx

---

## 📋 关键约束

### 非协商规则(Agents 必须遵守)

1. **分层架构**
   - 后端: Controller → Service → Mapper,严格分层
   - 前端: View → Composable → API,单向依赖
   - 禁止跨层调用

2. **数据库规范**
   - 表名使用小写下划线命名
   - 所有表必须有 `created_at` 和 `updated_at`
   - 外键必须定义索引
   - 详见: `/docs/architecture/database-design.md`

3. **API 设计**
   - 遵循 RESTful 规范
   - 统一响应格式: `{code, message, data}`
   - 统一异常处理
   - 详见: `/docs/conventions/api.md`

4. **前端规范**(待实现)
   - 组件命名: PascalCase
   - 组合式函数: use 前缀
   - 路由命名: kebab-case
   - 详见: `/docs/conventions/frontend.md`(待创建)

5. **代码质量**
   - 所有 Service 方法必须有事务注解
   - 所有 API 必须有参数校验
   - 关键业务逻辑必须有单元测试
   - 禁止使用魔法数字,使用常量或枚举

---

## 🔄 开发工作流

### 质量保障四层机制

详见: [docs/workflows/quality-assurance.md](./docs/workflows/quality-assurance.md)

**Layer 1: Plan Mode(方案设计)**
- 触发: 新功能、技术选型、数据库设计
- 必须: 技术栈验证、环境清单、完整 DDL、验收标准

**Layer 2: Implementation(编码实现)**
- 自检: 实体 ↔ 数据库、DTO 校验、事务注解、Swagger 文档

**Layer 3: Testing(测试验证)**
- 触发: 完成 Day N 任务、Bug 修复后
- 测试: 单元测试、API 测试、部署验证
- 验收: 测试通过、覆盖率 >= 80%(建议)

**Layer 4: Code Review(代码审查)**
- 触发: 测试通过后、准备 commit
- 检查: 代码 vs Plan、实体 vs 数据库、配置完整性

---

## 📊 项目状态

- **当前版本**: Day 1-2 已完成
- **当前阶段**: 后端开发(Phase 1)
- **下一步**: Day 3 - 问题管理模块
- **最后更新**: 2026-03-24

### 已完成工作
- ✅ Day 1: 后端项目骨架 + 用户认证模块
- ✅ Day 2: 工作区管理 + 项目管理模块
- ✅ 建立质量保障三层机制
- ✅ 修复 Java 版本兼容性问题

### 当前问题
- ⚠️ JWT Token 安全性(使用 localStorage,建议改为 HttpOnly Cookie)
- ⚠️ 数据库密码明文存储(开发环境可接受,生产环境需加密)

---

## 🔗 快速导航

### 核心文档入口
- **项目概述**: `/docs/requirements/01-project-overview.md` - 了解项目背景、功能特性
- **开发计划**: `/docs/development-plan.md` - Day 1-7 开发任务
- **质量保障**: `/docs/workflows/quality-assurance.md` - 三层质量机制
- **数据库设计**: `/docs/architecture/database-design.md` - 表结构和 ER 图
- **API 规范**: `/docs/conventions/api.md` - RESTful API 接口文档

### 环境配置
- **后端配置**: `/backend/src/main/resources/application-dev.yml`
- **数据库脚本**: `/backend/src/main/resources/db/migration/V1__init.sql`
- **Harness 检查清单**: `/.harness/checklist.md`

---

**Last Updated**: 2026-03-24
