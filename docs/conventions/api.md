# API 设计规范

**文档版本**：v1.0
**创建日期**：2026-03-23
**状态**：已确认

---

## 1. RESTful API 设计原则

### 1.1 基本原则

1. **资源导向**：URL 表示资源，使用名词而非动词
2. **HTTP 方法语义化**：正确使用 GET、POST、PUT、DELETE
3. **无状态**：每个请求包含完整的认证信息
4. **统一响应格式**：所有 API 返回格式一致
5. **版本控制**：通过 URL 路径进行版本管理

### 1.2 HTTP 方法使用

| 方法 | 语义 | 幂等性 | 示例 |
|------|------|--------|------|
| GET | 获取资源 | ✅ | GET /api/v1/projects |
| POST | 创建资源 | ❌ | POST /api/v1/projects |
| PUT | 完整更新资源 | ✅ | PUT /api/v1/projects/1 |
| PATCH | 部分更新资源 | ❌ | PATCH /api/v1/projects/1 |
| DELETE | 删除资源 | ✅ | DELETE /api/v1/projects/1 |

---

## 2. URL 设计规范

### 2.1 URL 结构

```
/{version}/{resource}/{id}/{sub-resource}
```

**示例**：
```
GET    /api/v1/projects              # 获取项目列表
GET    /api/v1/projects/1            # 获取单个项目
POST   /api/v1/projects              # 创建项目
PUT    /api/v1/projects/1            # 更新项目
DELETE /api/v1/projects/1            # 删除项目
GET    /api/v1/projects/1/issues     # 获取项目下的问题列表
```

### 2.2 命名规范

- **使用小写字母和连字符**：`/api/v1/issue-comments`
- **使用复数名词**：`/projects` 而非 `/project`
- **避免动词**：`POST /projects` 而非 `POST /create-project`
- **层级不超过 3 层**：保持 URL 简洁

### 2.3 查询参数

**分页参数**：
```
GET /api/v1/issues?page=1&size=20
```

**筛选参数**：
```
GET /api/v1/issues?status=in_progress&priority=high
```

**排序参数**：
```
GET /api/v1/issues?sort=created_at:desc
```

**搜索参数**：
```
GET /api/v1/issues?keyword=登录
```

---

## 3. 统一响应格式

### 3.1 成功响应

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "title": "实现用户登录功能"
  }
}
```

**字段说明**：
- `code`：业务状态码（200 表示成功）
- `message`：响应消息
- `data`：响应数据（单个对象或数组）

### 3.2 分页响应

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "items": [
      {"id": 1, "title": "问题1"},
      {"id": 2, "title": "问题2"}
    ],
    "pagination": {
      "page": 1,
      "size": 20,
      "total": 100,
      "pages": 5
    }
  }
}
```

### 3.3 错误响应

```json
{
  "code": 400,
  "message": "参数校验失败",
  "errors": [
    {
      "field": "title",
      "message": "标题不能为空"
    }
  ]
}
```

**常见错误码**：

| 错误码 | HTTP 状态码 | 说明 |
|--------|-------------|------|
| 200 | 200 | 成功 |
| 400 | 400 | 请求参数错误 |
| 401 | 401 | 未认证 |
| 403 | 403 | 无权限 |
| 404 | 404 | 资源不存在 |
| 409 | 409 | 资源冲突 |
| 500 | 500 | 服务器内部错误 |

---

## 4. MVP API 接口清单

### 4.1 认证接口

#### 用户注册
```
POST /api/v1/auth/register

Request:
{
  "username": "zhangsan",
  "email": "zhangsan@example.com",
  "password": "password123"
}

Response:
{
  "code": 200,
  "message": "注册成功",
  "data": {
    "id": 1,
    "username": "zhangsan",
    "email": "zhangsan@example.com",
    "avatar": null
  }
}
```

#### 用户登录
```
POST /api/v1/auth/login

Request:
{
  "email": "zhangsan@example.com",
  "password": "password123"
}

Response:
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "id": 1,
      "username": "zhangsan",
      "email": "zhangsan@example.com",
      "avatar": null
    }
  }
}
```

#### 获取当前用户信息
```
GET /api/v1/auth/me

Headers:
Authorization: Bearer {token}

Response:
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "username": "zhangsan",
    "email": "zhangsan@example.com",
    "avatar": null
  }
}
```

### 4.2 工作区接口

#### 获取当前用户的工作区
```
GET /api/v1/workspaces

Headers:
Authorization: Bearer {token}

Response:
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "name": "张三的工作区",
    "slug": "zhangsan-workspace",
    "owner_id": 1
  }
}
```

### 4.3 项目接口

#### 获取项目列表
```
GET /api/v1/projects?workspace_id=1

Headers:
Authorization: Bearer {token}

Response:
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "name": "Plane Lite",
      "identifier": "PL",
      "description": "项目管理系统",
      "icon": "🚀",
      "created_by": 1,
      "created_at": "2026-03-23T10:00:00"
    }
  ]
}
```

#### 创建项目
```
POST /api/v1/projects

Headers:
Authorization: Bearer {token}

Request:
{
  "workspace_id": 1,
  "name": "Plane Lite",
  "identifier": "PL",
  "description": "项目管理系统",
  "icon": "🚀"
}

Response:
{
  "code": 200,
  "message": "创建成功",
  "data": {
    "id": 1,
    "name": "Plane Lite",
    "identifier": "PL",
    "description": "项目管理系统",
    "icon": "🚀",
    "created_by": 1,
    "created_at": "2026-03-23T10:00:00"
  }
}
```

#### 获取项目详情
```
GET /api/v1/projects/1

Headers:
Authorization: Bearer {token}

Response:
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "name": "Plane Lite",
    "identifier": "PL",
    "description": "项目管理系统",
    "icon": "🚀",
    "created_by": 1,
    "created_at": "2026-03-23T10:00:00",
    "issue_count": {
      "todo": 10,
      "in_progress": 5,
      "done": 20,
      "closed": 3
    }
  }
}
```

#### 更新项目
```
PUT /api/v1/projects/1

Headers:
Authorization: Bearer {token}

Request:
{
  "name": "Plane Lite Pro",
  "description": "升级版项目管理系统"
}

Response:
{
  "code": 200,
  "message": "更新成功",
  "data": {
    "id": 1,
    "name": "Plane Lite Pro",
    "identifier": "PL",
    "description": "升级版项目管理系统",
    "icon": "🚀"
  }
}
```

#### 删除项目
```
DELETE /api/v1/projects/1

Headers:
Authorization: Bearer {token}

Response:
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

### 4.4 问题接口

#### 获取问题列表
```
GET /api/v1/issues?project_id=1&status=in_progress&page=1&size=20

Headers:
Authorization: Bearer {token}

Response:
{
  "code": 200,
  "message": "success",
  "data": {
    "items": [
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
    ],
    "pagination": {
      "page": 1,
      "size": 20,
      "total": 50,
      "pages": 3
    }
  }
}
```

#### 创建问题
```
POST /api/v1/issues

Headers:
Authorization: Bearer {token}

Request:
{
  "project_id": 1,
  "title": "实现用户登录功能",
  "description": "需要支持邮箱登录和 JWT 认证",
  "priority": "high",
  "status": "todo",
  "assignee_id": 1
}

Response:
{
  "code": 200,
  "message": "创建成功",
  "data": {
    "id": 1,
    "project_id": 1,
    "issue_number": "PL-1",
    "title": "实现用户登录功能",
    "description": "需要支持邮箱登录和 JWT 认证",
    "priority": "high",
    "status": "todo",
    "assignee_id": 1,
    "reporter_id": 1,
    "created_at": "2026-03-23T10:00:00"
  }
}
```

#### 获取问题详情
```
GET /api/v1/issues/1

Headers:
Authorization: Bearer {token}

Response:
{
  "code": 200,
  "message": "success",
  "data": {
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
}
```

#### 更新问题
```
PUT /api/v1/issues/1

Headers:
Authorization: Bearer {token}

Request:
{
  "title": "实现用户登录和注册功能",
  "status": "in_progress",
  "priority": "urgent"
}

Response:
{
  "code": 200,
  "message": "更新成功",
  "data": {
    "id": 1,
    "title": "实现用户登录和注册功能",
    "status": "in_progress",
    "priority": "urgent"
  }
}
```

#### 删除问题
```
DELETE /api/v1/issues/1

Headers:
Authorization: Bearer {token}

Response:
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

#### 获取看板数据
```
GET /api/v1/issues/board?project_id=1

Headers:
Authorization: Bearer {token}

Response:
{
  "code": 200,
  "message": "success",
  "data": {
    "todo": [
      {
        "id": 2,
        "issue_number": "PL-2",
        "title": "设计数据库表结构",
        "priority": "medium",
        "assignee": {...}
      }
    ],
    "in_progress": [
      {
        "id": 1,
        "issue_number": "PL-1",
        "title": "实现用户登录功能",
        "priority": "high",
        "assignee": {...}
      }
    ],
    "done": [],
    "closed": []
  }
}
```

### 4.5 评论接口

#### 获取问题评论列表
```
GET /api/v1/issues/1/comments

Headers:
Authorization: Bearer {token}

Response:
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "issue_id": 1,
      "user": {
        "id": 1,
        "username": "zhangsan",
        "avatar": null
      },
      "content": "已完成 API 设计，准备开始编码",
      "created_at": "2026-03-23T11:30:00"
    }
  ]
}
```

#### 添加评论
```
POST /api/v1/issues/1/comments

Headers:
Authorization: Bearer {token}

Request:
{
  "content": "已完成 API 设计，准备开始编码"
}

Response:
{
  "code": 200,
  "message": "评论成功",
  "data": {
    "id": 1,
    "issue_id": 1,
    "user_id": 1,
    "content": "已完成 API 设计，准备开始编码",
    "created_at": "2026-03-23T11:30:00"
  }
}
```

---

## 5. 认证和授权

### 5.1 JWT 认证

**流程**：
1. 用户登录成功后，服务器返回 JWT Token
2. 客户端在后续请求中携带 Token：`Authorization: Bearer {token}`
3. 服务器验证 Token 有效性

**Token 格式**：
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c
```

**Token 有效期**：7 天

### 5.2 权限控制

MVP 阶段：
- 用户只能访问自己创建的资源
- 不做精细的权限控制

V2.0 扩展：
- 基于角色的权限控制（RBAC）
- 项目成员权限：Owner、Member、Viewer

---

## 6. 错误处理

### 6.1 参数校验错误

```json
{
  "code": 400,
  "message": "参数校验失败",
  "errors": [
    {
      "field": "title",
      "message": "标题不能为空"
    },
    {
      "field": "email",
      "message": "邮箱格式不正确"
    }
  ]
}
```

### 6.2 业务逻辑错误

```json
{
  "code": 409,
  "message": "用户名已存在",
  "data": null
}
```

### 6.3 服务器错误

```json
{
  "code": 500,
  "message": "服务器内部错误",
  "data": null
}
```

---

## 7. API 文档

**工具**：Swagger / OpenAPI

**访问地址**：`http://localhost:8080/swagger-ui.html`

**生成方式**：通过注解自动生成

---

## 8. 更新记录

| 版本 | 日期 | 变更说明 |
|------|------|----------|
| v1.0 | 2026-03-23 | 初始版本，MVP API 设计 |
