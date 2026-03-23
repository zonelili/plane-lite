# Plane Lite - 轻量级项目管理系统

> 基于开源项目 Plane 的简化实现，用于验证 AI Coding 的全流程开发能力

## 项目概述

Plane Lite 是一个现代化的项目管理平台，核心功能包括：
- 项目管理：创建和组织项目
- 问题追踪：任务的创建、分配、状态流转
- 看板视图：可视化展示工作流
- 团队协作：评论、通知

**目标**：用 1 周时间完成 MVP，验证 AI 全自动开发的可行性

## 架构地图

### 📚 核心文档
- `/docs/requirements/` - 需求文档和版本规划
- `/docs/architecture/` - 系统架构和技术选型
- `/docs/conventions/` - 编码规范和最佳实践
- `/docs/workflows/` - 常见开发任务流程
- `/docs/decisions/` - 架构决策记录（ADR）

### 🏗️ 代码结构
- `/backend/` - Spring Boot 后端服务
- `/frontend/` - Vue 3 前端应用
- `/deployments/` - Docker 部署配置
- `/tests/` - 集成测试和 E2E 测试

### 🔧 基础设施
- `/.harness/` - Harness 配置和自动化脚本

## 快速开始

### 环境要求
```bash
- JDK 17+
- Node.js 18+
- MySQL 8.0+
- Redis 7.0+
- Docker & Docker Compose
```

### 本地开发
```bash
# 启动数据库和缓存
docker-compose up -d mysql redis

# 启动后端
cd backend
./mvnw spring-boot:run

# 启动前端
cd frontend
npm install
npm run dev
```

### 运行测试
```bash
# 后端单元测试
cd backend
./mvnw test

# 前端单元测试
cd frontend
npm run test

# E2E 测试
npm run test:e2e
```

## 关键约束

### 非协商规则（Agents 必须遵守）

1. **分层架构**
   - 后端：Controller → Service → Mapper，严格分层
   - 前端：View → Composable → API，单向依赖
   - 禁止跨层调用

2. **数据库规范**
   - 表名使用小写下划线命名
   - 所有表必须有 `created_at` 和 `updated_at`
   - 外键必须定义索引
   - 详见：`/docs/conventions/database.md`

3. **API 设计**
   - 遵循 RESTful 规范
   - 统一响应格式：`{code, message, data}`
   - 统一异常处理
   - 详见：`/docs/conventions/api.md`

4. **前端规范**
   - 组件命名：PascalCase
   - 组合式函数：use 前缀
   - 路由命名：kebab-case
   - 详见：`/docs/conventions/frontend.md`

5. **代码质量**
   - 所有 Service 方法必须有事务注解
   - 所有 API 必须有参数校验
   - 关键业务逻辑必须有单元测试
   - 禁止使用魔法数字，使用常量或枚举

## 开发工作流

### 新功能开发
```bash
1. 阅读需求文档：docs/requirements/
2. 设计技术方案：创建 ADR（docs/decisions/）
3. 实现代码（TDD）
4. 本地测试验证
5. 提交代码审查
```

### 问题修复
```bash
1. 重现问题
2. 编写失败的测试用例
3. 修复代码
4. 验证测试通过
5. 提交修复
```

## 验证检查点

### 功能完成标准
- [ ] 功能符合需求文档描述
- [ ] 所有 API 通过 Postman/Swagger 测试
- [ ] 前端页面交互正常
- [ ] 单元测试覆盖率 > 80%
- [ ] 代码通过 Lint 检查
- [ ] 数据库表结构符合规范

### 代码审查检查点
- [ ] 代码分层清晰
- [ ] 异常处理完整
- [ ] 日志记录合理
- [ ] 变量命名语义化
- [ ] 没有重复代码
- [ ] 没有硬编码

## 技术栈

### 后端
- **框架**：Spring Boot 3.2
- **ORM**：MyBatis-Plus 3.5
- **数据库**：MySQL 8.0
- **缓存**：Redis 7.x
- **认证**：JWT
- **工具**：Lombok, Validation

### 前端
- **框架**：Vue 3.4 + TypeScript
- **构建**：Vite 5.x
- **UI**：Element Plus
- **状态**：Pinia
- **路由**：Vue Router 4
- **HTTP**：Axios

### 基础设施
- **容器化**：Docker, Docker Compose
- **反向代理**：Nginx
- **CI/CD**：（待定）

## 资源链接

- **原项目**：[Plane GitHub](https://github.com/makeplane/plane)
- **官网**：[plane.so](https://plane.so)
- **技术文档**：见 `/docs` 目录

## 项目状态

- **当前版本**：MVP 规划中
- **预计完成**：1 周
- **最后更新**：2026-03-23
