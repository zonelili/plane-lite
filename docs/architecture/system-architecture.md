# 系统架构设计

**文档版本**：v1.0
**创建日期**：2026-03-23
**状态**：已确认

---

## 1. 架构概述

### 1.1 整体架构

Plane Lite 采用**前后端分离**的架构，通过 RESTful API 进行通信。

```
┌─────────────────────────────────────────────────────────────┐
│                         用户浏览器                           │
│                    (Chrome/Firefox/Safari)                   │
└────────────────────────┬────────────────────────────────────┘
                         │ HTTPS
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                         Nginx                                │
│                    (反向代理 + 静态资源)                      │
└────────┬────────────────────────────────────┬───────────────┘
         │                                    │
         │ /api/*                             │ /*
         ▼                                    ▼
┌─────────────────────┐              ┌──────────────────────┐
│   后端服务           │              │   前端静态资源        │
│   Spring Boot       │              │   Vue 3 SPA          │
│   Port: 8080        │              │   Nginx 托管         │
└──────────┬──────────┘              └──────────────────────┘
           │
           │ JDBC
           ▼
┌─────────────────────┐              ┌──────────────────────┐
│   MySQL 8.0         │              │   Redis 7.x          │
│   Port: 3306        │              │   Port: 6379         │
│   (持久化存储)       │              │   (缓存 + Session)   │
└─────────────────────┘              └──────────────────────┘
```

### 1.2 核心设计原则

1. **分层架构**：前端、后端、数据层清晰分离
2. **RESTful API**：标准的 HTTP 方法和资源路径
3. **无状态**：JWT Token 认证，不依赖 Session
4. **松耦合**：模块间通过接口通信
5. **可扩展**：预留扩展点，便于后续迭代

---

## 2. 后端架构设计

### 2.1 分层架构

采用**经典三层架构**，职责清晰：

```
┌──────────────────────────────────────────────────────────┐
│                     Controller 层                         │
│  职责: HTTP 请求处理、参数校验、响应封装                   │
│  技术: @RestController, @Valid, Result 统一响应           │
└─────────────────────┬────────────────────────────────────┘
                      │
                      ▼
┌──────────────────────────────────────────────────────────┐
│                      Service 层                           │
│  职责: 业务逻辑、事务管理、跨模块调用                       │
│  技术: @Service, @Transactional                           │
└─────────────────────┬────────────────────────────────────┘
                      │
                      ▼
┌──────────────────────────────────────────────────────────┐
│                      Mapper 层                            │
│  职责: 数据访问、SQL 执行                                  │
│  技术: MyBatis-Plus BaseMapper                            │
└─────────────────────┬────────────────────────────────────┘
                      │
                      ▼
┌──────────────────────────────────────────────────────────┐
│                       数据库                              │
└──────────────────────────────────────────────────────────┘
```

### 2.2 模块划分

按业务领域划分模块，每个模块包含完整的三层结构：

```
com.planelite/
├── common/                    # 公共模块
│   ├── config/               # 配置类
│   ├── constant/             # 常量定义
│   ├── exception/            # 异常定义
│   ├── result/               # 统一响应
│   └── util/                 # 工具类
├── module/
│   ├── user/                 # 用户模块
│   │   ├── controller/
│   │   │   └── UserController.java
│   │   ├── service/
│   │   │   ├── UserService.java
│   │   │   └── impl/
│   │   │       └── UserServiceImpl.java
│   │   ├── mapper/
│   │   │   └── UserMapper.java
│   │   ├── entity/
│   │   │   └── User.java
│   │   └── dto/
│   │       ├── UserRegisterDTO.java
│   │       └── UserLoginDTO.java
│   ├── workspace/            # 工作区模块
│   ├── project/              # 项目模块
│   ├── issue/                # 问题模块
│   └── comment/              # 评论模块
└── PlaneApplication.java     # 启动类
```

### 2.3 依赖关系

**允许的依赖**：
```
✅ Controller → Service
✅ Service → Mapper
✅ Service → Service（同模块内）
✅ Service → 其他模块的 Service（通过接口）
```

**禁止的依赖**：
```
❌ Controller → Mapper（跨层调用）
❌ Mapper → Service（反向依赖）
❌ Controller → Controller（控制器间调用）
```

---

## 3. 前端架构设计

### 3.1 整体结构

采用 **Vue 3 Composition API + TypeScript**，模块化组织代码：

```
src/
├── assets/                   # 静态资源
│   ├── images/
│   ├── icons/
│   └── styles/
│       ├── variables.scss   # 全局变量
│       └── global.scss      # 全局样式
├── components/               # 组件
│   ├── layout/              # 布局组件
│   │   ├── AppHeader.vue
│   │   ├── AppSidebar.vue
│   │   └── AppMain.vue
│   └── common/              # 通用组件
│       ├── EmptyState.vue
│       ├── LoadingSpinner.vue
│       └── ConfirmDialog.vue
├── views/                    # 页面组件
│   ├── auth/
│   │   ├── Login.vue
│   │   └── Register.vue
│   ├── workspace/
│   │   └── WorkspaceHome.vue
│   ├── project/
│   │   ├── ProjectList.vue
│   │   └── ProjectDetail.vue
│   └── issue/
│       ├── IssueList.vue
│       ├── IssueDetail.vue
│       └── IssueBoard.vue
├── composables/              # 组合式函数
│   ├── useUser.ts
│   ├── useProject.ts
│   └── useIssue.ts
├── api/                      # API 调用
│   ├── request.ts           # Axios 封装
│   ├── user.api.ts
│   ├── project.api.ts
│   ├── issue.api.ts
│   └── comment.api.ts
├── stores/                   # Pinia 状态管理
│   ├── user.ts
│   ├── project.ts
│   ├── issue.ts
│   └── comment.ts
├── router/                   # 路由
│   ├── index.ts
│   └── routes.ts
├── types/                    # TypeScript 类型
│   ├── user.ts
│   ├── project.ts
│   └── issue.ts
├── utils/                    # 工具函数
│   ├── auth.ts
│   ├── date.ts
│   └── validation.ts
├── App.vue
└── main.ts
```

### 3.2 页面层级

```
/ (根路径)
├── /login                    # 登录页
├── /register                 # 注册页
└── /workspace/:id            # 工作区（需要认证）
    ├── /projects             # 项目列表
    ├── /projects/:id         # 项目详情
    │   ├── /issues          # 问题列表
    │   ├── /issues/:id      # 问题详情
    │   └── /board           # 看板视图
    └── /profile              # 用户设置
```

### 3.3 组件分类

#### 布局组件（Layout Components）
- **AppHeader**: 顶部导航栏
- **AppSidebar**: 侧边栏菜单
- **AppMain**: 主内容区

#### 业务组件（Business Components）
- **ProjectCard**: 项目卡片
- **IssueCard**: 问题卡片
- **IssueForm**: 问题表单
- **CommentList**: 评论列表

#### 通用组件（Common Components）
- **Button**: 按钮
- **Input**: 输入框
- **Select**: 下拉选择
- **Modal**: 模态框
- **EmptyState**: 空状态
- **LoadingSpinner**: 加载动画

---

## 4. 数据流设计

### 4.1 后端数据流

```
HTTP 请求
    ↓
Controller（参数校验）
    ↓
Service（业务逻辑 + 事务管理）
    ↓
Mapper（数据访问）
    ↓
数据库
    ↓
Mapper（返回 Entity）
    ↓
Service（Entity → DTO 转换）
    ↓
Controller（DTO → Result 封装）
    ↓
HTTP 响应
```

### 4.2 前端数据流

```
用户操作
    ↓
Vue Component
    ↓
Composable / Store Action
    ↓
API 调用（request.ts）
    ↓
后端 API
    ↓
响应处理
    ↓
Store State 更新
    ↓
Component 响应式更新
    ↓
UI 重新渲染
```

---

## 5. 认证和授权

### 5.1 JWT 认证流程

```
┌──────────┐                                      ┌──────────┐
│  前端    │                                      │  后端    │
└────┬─────┘                                      └────┬─────┘
     │                                                 │
     │  1. POST /api/v1/auth/login                    │
     │    {email, password}                            │
     ├────────────────────────────────────────────────>│
     │                                                 │
     │                        2. 验证用户名密码         │
     │                        3. 生成 JWT Token       │
     │                                                 │
     │  4. 返回 Token                                  │
     │<────────────────────────────────────────────────┤
     │                                                 │
     │  5. 存储 Token 到 LocalStorage                  │
     │                                                 │
     │  6. 后续请求携带 Token                          │
     │     Authorization: Bearer {token}              │
     ├────────────────────────────────────────────────>│
     │                                                 │
     │                        7. 验证 Token            │
     │                        8. 解析用户信息          │
     │                                                 │
     │  9. 返回数据                                    │
     │<────────────────────────────────────────────────┤
     │                                                 │
```

### 5.2 Token 设计

**JWT Payload 结构**：
```json
{
  "userId": 1,
  "username": "zhangsan",
  "email": "zhangsan@example.com",
  "iat": 1711180800,
  "exp": 1711785600
}
```

**Token 有效期**：7 天

**刷新策略**：
- MVP: 不实现 Refresh Token，Token 过期后重新登录
- V2.0: 实现 Refresh Token 机制

### 5.3 权限控制

**MVP 阶段**：
- 用户只能访问自己创建的资源
- 不做精细的 RBAC 权限控制

**实现方式**：
```java
// Service 层检查
public Project getProject(Long projectId, Long currentUserId) {
    Project project = projectMapper.selectById(projectId);
    if (!project.getCreatedBy().equals(currentUserId)) {
        throw new ForbiddenException("无权访问该项目");
    }
    return project;
}
```

---

## 6. 缓存策略

### 6.1 Redis 使用场景

**MVP 阶段**：
```
✅ Token 黑名单（退出登录时）
✅ 用户信息缓存（减少数据库查询）

暂不使用：
❌ 问题列表缓存（数据变化频繁）
❌ 项目列表缓存（暂时不需要）
```

### 6.2 缓存键设计

```
# Token 黑名单
token:blacklist:{token}          # TTL: Token 剩余有效期

# 用户信息缓存
user:info:{userId}               # TTL: 1 小时
```

---

## 7. 异常处理

### 7.1 异常分类

```
BusinessException           # 业务异常（400）
├── ValidationException    # 参数校验异常
├── UnauthorizedException  # 未认证异常（401）
├── ForbiddenException     # 无权限异常（403）
└── NotFoundException      # 资源不存在（404）

SystemException            # 系统异常（500）
├── DatabaseException      # 数据库异常
└── ExternalServiceException # 外部服务异常
```

### 7.2 全局异常处理

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result handleBusinessException(BusinessException e) {
        return Result.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result handleValidationException(MethodArgumentNotValidException e) {
        // 参数校验异常处理
        List<FieldError> errors = e.getBindingResult().getFieldErrors();
        return Result.error(400, "参数校验失败", errors);
    }

    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e) {
        log.error("系统异常", e);
        return Result.error(500, "系统内部错误");
    }
}
```

---

## 8. 日志设计

### 8.1 日志级别

```
ERROR: 系统错误、异常
WARN:  业务警告（如参数校验失败）
INFO:  关键业务操作（登录、创建项目）
DEBUG: 开发调试信息
```

### 8.2 日志内容

**Controller 层**：
```java
log.info("用户登录 - email: {}", loginDTO.getEmail());
log.info("创建项目 - userId: {}, projectName: {}", userId, dto.getName());
```

**Service 层**：
```java
log.debug("查询项目详情 - projectId: {}", projectId);
log.error("创建项目失败 - userId: {}, error: {}", userId, e.getMessage(), e);
```

### 8.3 日志格式

```
%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} - %msg%n
```

---

## 9. 事务管理

### 9.1 事务边界

**原则**：
- Service 层方法是事务边界
- 一个 HTTP 请求 = 一个事务

### 9.2 事务注解

```java
@Service
public class IssueServiceImpl implements IssueService {

    // 默认事务：读写
    @Transactional
    public Issue createIssue(IssueCreateDTO dto) {
        // 业务逻辑
    }

    // 只读事务：查询
    @Transactional(readOnly = true)
    public Issue getIssueById(Long id) {
        // 查询逻辑
    }
}
```

### 9.3 事务回滚

```java
// 遇到 RuntimeException 和 Error 时回滚
@Transactional(rollbackFor = Exception.class)
public void complexBusinessLogic() {
    // 业务逻辑
}
```

---

## 10. 性能优化

### 10.1 数据库优化

```
✅ 索引设计（已在数据库设计文档中）
✅ 避免 N+1 查询（使用 MyBatis-Plus 的关联查询）
✅ 分页查询（Page<T>）

暂不需要：
❌ 读写分离（MVP 数据量小）
❌ 分库分表（MVP 数据量小）
```

### 10.2 前端优化

```
✅ 路由懒加载
✅ 图片懒加载
✅ 防抖和节流（搜索、滚动）
✅ 虚拟列表（如果列表很长）

暂不需要：
❌ SSR（MVP 不需要 SEO）
❌ PWA（V2.0 考虑）
```

---

## 11. 部署架构

### 11.1 Docker Compose 部署

```yaml
version: '3.8'
services:
  mysql:
    image: mysql:8.0
    ports:
      - "3306:3306"

  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"

  backend:
    build: ./backend
    ports:
      - "8080:8080"
    depends_on:
      - mysql
      - redis

  frontend:
    build: ./frontend
    ports:
      - "80:80"
    depends_on:
      - backend
```

### 11.2 环境变量

```bash
# MySQL
MYSQL_HOST=mysql
MYSQL_PORT=3306
MYSQL_DATABASE=plane_lite
MYSQL_USER=root
MYSQL_PASSWORD=password

# Redis
REDIS_HOST=redis
REDIS_PORT=6379

# JWT
JWT_SECRET=your-secret-key
JWT_EXPIRATION=604800000

# 应用配置
SPRING_PROFILES_ACTIVE=prod
SERVER_PORT=8080
```

---

## 12. 技术栈总结

| 层次 | 技术 | 版本 | 用途 |
|------|------|------|------|
| **后端框架** | Spring Boot | 3.2 | Web 框架 |
| **ORM** | MyBatis-Plus | 3.5 | 数据访问 |
| **数据库** | MySQL | 8.0 | 持久化存储 |
| **缓存** | Redis | 7.x | 缓存 + Session |
| **认证** | JWT | - | Token 认证 |
| **前端框架** | Vue | 3.4 | UI 框架 |
| **构建工具** | Vite | 5.x | 前端构建 |
| **UI 组件** | Element Plus | - | UI 组件库 |
| **状态管理** | Pinia | - | 状态管理 |
| **HTTP 客户端** | Axios | - | API 调用 |
| **容器化** | Docker | - | 容器化部署 |
| **反向代理** | Nginx | - | 反向代理 |

---

## 13. 更新记录

| 版本 | 日期 | 变更说明 |
|------|------|----------|
| v1.0 | 2026-03-23 | 初始版本，完整的系统架构设计 |
