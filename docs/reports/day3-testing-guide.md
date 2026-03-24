# Day 3 - 问题管理模块测试指南

**测试目的**：验证问题管理模块的所有功能是否正常工作
**测试环境**：
- 后端服务：`http://localhost:8080`
- Swagger UI：`http://localhost:8080/swagger-ui/index.html`
- 测试时间：2026-03-24

---

## 准备工作

### 1. 确认服务运行

```bash
# 检查服务是否运行
curl -s http://localhost:8080/swagger-ui/index.html | grep -o "Swagger UI" | head -1

# 预期输出：Swagger UI
```

### 2. 准备测试账号

访问 Swagger UI: `http://localhost:8080/swagger-ui/index.html`

#### 方案 A：使用现有管理员账号
- **邮箱**：`admin@planelite.com`
- **密码**：`admin123`（需要确认）

#### 方案 B：注册新测试账号
使用 `POST /api/v1/auth/register` 注册新用户：
```json
{
  "username": "test_issue",
  "email": "test_issue@example.com",
  "password": "Test123456"
}
```

---

## 测试用例

### Test Case 1: 准备测试数据

**目标**：创建工作区和项目，为问题测试做准备

#### 1.1 登录获取 Token

**API**: `POST /api/v1/auth/login`

**请求**:
```json
{
  "email": "admin@planelite.com",
  "password": "admin123"
}
```

**预期响应**:
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "user": {
      "id": 1,
      "username": "admin",
      "email": "admin@planelite.com"
    }
  }
}
```

**✅ 验证点**:
- [ ] 状态码 200
- [ ] 返回了 JWT token
- [ ] 复制 token，后续请求需要使用

---

#### 1.2 创建工作区（如果不存在）

**API**: `POST /api/v1/workspaces`

**请求**:
```json
{
  "name": "测试工作区",
  "slug": "test-workspace",
  "description": "用于测试问题管理功能"
}
```

**Headers**:
```
Authorization: Bearer {your_token}
```

**预期响应**:
```json
{
  "code": 200,
  "message": "工作区创建成功",
  "data": {
    "id": 1,
    "name": "测试工作区",
    "slug": "test-workspace",
    ...
  }
}
```

**✅ 验证点**:
- [ ] 状态码 200
- [ ] 记录 `workspace_id`（后续使用）

---

#### 1.3 创建项目

**API**: `POST /api/v1/projects`

**请求**:
```json
{
  "name": "测试项目",
  "identifier": "TEST",
  "description": "用于测试问题管理功能",
  "workspaceId": 1
}
```

**Headers**:
```
Authorization: Bearer {your_token}
```

**预期响应**:
```json
{
  "code": 200,
  "message": "项目创建成功",
  "data": {
    "id": 1,
    "name": "测试项目",
    "identifier": "TEST",
    "workspaceId": 1,
    ...
  }
}
```

**✅ 验证点**:
- [ ] 状态码 200
- [ ] `identifier` 为 "TEST"（用于生成问题编号）
- [ ] 记录 `project_id`（后续使用）

---

### Test Case 2: 创建问题（验证 sequence_id 自动递增）

**目标**：验证创建问题时 `sequence_id` 自动递增，问题编号格式正确

#### 2.1 创建第一个问题

**API**: `POST /api/v1/issues`

**请求**:
```json
{
  "projectId": 1,
  "title": "实现用户登录功能",
  "description": "需要实现用户名密码登录和第三方登录",
  "priority": "high",
  "startDate": "2026-03-24",
  "dueDate": "2026-03-25"
}
```

**预期响应**:
```json
{
  "code": 200,
  "message": "问题创建成功",
  "data": {
    "id": 1,
    "issueNumber": "TEST-1",
    "projectId": 1,
    "title": "实现用户登录功能",
    "description": "需要实现用户名密码登录和第三方登录",
    "priority": "high",
    "status": "todo",
    "reporterId": 1,
    "assigneeId": null,
    "startDate": "2026-03-24",
    "dueDate": "2026-03-25",
    "createdAt": "2026-03-24T...",
    "updatedAt": "2026-03-24T..."
  }
}
```

**✅ 验证点**:
- [ ] `issueNumber` 为 "TEST-1"（第一个问题）
- [ ] `status` 默认为 "todo"
- [ ] `reporterId` 为当前用户 ID
- [ ] `priority` 为 "high"

---

#### 2.2 创建第二个问题

**API**: `POST /api/v1/issues`

**请求**:
```json
{
  "projectId": 1,
  "title": "实现项目看板功能",
  "description": "实现拖拽式看板，支持状态流转",
  "priority": "medium"
}
```

**预期响应**:
```json
{
  "code": 200,
  "message": "问题创建成功",
  "data": {
    "id": 2,
    "issueNumber": "TEST-2",
    "title": "实现项目看板功能",
    "priority": "medium",
    "status": "todo",
    ...
  }
}
```

**✅ 验证点**:
- [ ] `issueNumber` 为 "TEST-2"（序号递增）
- [ ] 可选字段（description）可以省略

---

#### 2.3 创建第三个问题

**API**: `POST /api/v1/issues`

**请求**:
```json
{
  "projectId": 1,
  "title": "修复评论功能 Bug",
  "description": "评论无法保存到数据库",
  "priority": "urgent",
  "assigneeId": 1
}
```

**预期响应**:
```json
{
  "code": 200,
  "message": "问题创建成功",
  "data": {
    "id": 3,
    "issueNumber": "TEST-3",
    "title": "修复评论功能 Bug",
    "priority": "urgent",
    "assigneeId": 1,
    ...
  }
}
```

**✅ 验证点**:
- [ ] `issueNumber` 为 "TEST-3"（继续递增）
- [ ] `assigneeId` 正确设置

---

### Test Case 3: 获取问题列表（验证筛选功能）

**目标**：验证问题列表的筛选功能

#### 3.1 获取所有问题

**API**: `GET /api/v1/issues?projectId=1`

**预期响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 3,
      "issueNumber": "TEST-3",
      "title": "修复评论功能 Bug",
      ...
    },
    {
      "id": 2,
      "issueNumber": "TEST-2",
      "title": "实现项目看板功能",
      ...
    },
    {
      "id": 1,
      "issueNumber": "TEST-1",
      "title": "实现用户登录功能",
      ...
    }
  ]
}
```

**✅ 验证点**:
- [ ] 返回 3 个问题
- [ ] 按创建时间降序排列（最新的在前）

---

#### 3.2 按状态筛选（status=todo）

**API**: `GET /api/v1/issues?projectId=1&status=todo`

**预期响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    // 所有 status 为 "todo" 的问题
  ]
}
```

**✅ 验证点**:
- [ ] 只返回状态为 "todo" 的问题
- [ ] 新创建的问题默认都是 "todo"

---

#### 3.3 按优先级筛选（priority=high）

**API**: `GET /api/v1/issues?projectId=1&priority=high`

**预期响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "issueNumber": "TEST-1",
      "title": "实现用户登录功能",
      "priority": "high",
      ...
    }
  ]
}
```

**✅ 验证点**:
- [ ] 只返回优先级为 "high" 的问题
- [ ] TEST-1 的优先级是 "high"

---

#### 3.4 按负责人筛选（assigneeId=1）

**API**: `GET /api/v1/issues?projectId=1&assigneeId=1`

**预期响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 3,
      "issueNumber": "TEST-3",
      "title": "修复评论功能 Bug",
      "assigneeId": 1,
      ...
    }
  ]
}
```

**✅ 验证点**:
- [ ] 只返回负责人为用户 1 的问题
- [ ] TEST-3 的负责人是用户 1

---

### Test Case 4: 获取问题详情

**API**: `GET /api/v1/issues/1`

**预期响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "issueNumber": "TEST-1",
    "projectId": 1,
    "title": "实现用户登录功能",
    "description": "需要实现用户名密码登录和第三方登录",
    "priority": "high",
    "status": "todo",
    "reporterId": 1,
    "assigneeId": null,
    "startDate": "2026-03-24",
    "dueDate": "2026-03-25",
    "createdAt": "...",
    "updatedAt": "..."
  }
}
```

**✅ 验证点**:
- [ ] 返回完整的问题信息
- [ ] 所有字段都存在（即使是 null）

---

### Test Case 5: 更新问题（验证状态流转）

**目标**：验证问题的更新功能和状态流转

#### 5.1 更新问题状态（todo → in_progress）

**API**: `PUT /api/v1/issues/1`

**请求**:
```json
{
  "status": "in_progress"
}
```

**预期响应**:
```json
{
  "code": 200,
  "message": "问题更新成功",
  "data": {
    "id": 1,
    "issueNumber": "TEST-1",
    "status": "in_progress",
    "updatedAt": "2026-03-24T..." // 更新时间变化
  }
}
```

**✅ 验证点**:
- [ ] 状态从 "todo" 变为 "in_progress"
- [ ] `updatedAt` 自动更新

---

#### 5.2 更新问题标题和描述

**API**: `PUT /api/v1/issues/2`

**请求**:
```json
{
  "title": "实现项目看板功能（含拖拽）",
  "description": "实现拖拽式看板，支持状态流转和优先级调整"
}
```

**预期响应**:
```json
{
  "code": 200,
  "message": "问题更新成功",
  "data": {
    "id": 2,
    "title": "实现项目看板功能（含拖拽）",
    "description": "实现拖拽式看板，支持状态流转和优先级调整",
    ...
  }
}
```

**✅ 验证点**:
- [ ] 标题和描述成功更新
- [ ] 其他字段不变

---

#### 5.3 分配负责人和修改优先级

**API**: `PUT /api/v1/issues/2`

**请求**:
```json
{
  "assigneeId": 1,
  "priority": "high",
  "status": "in_progress"
}
```

**预期响应**:
```json
{
  "code": 200,
  "message": "问题更新成功",
  "data": {
    "id": 2,
    "assigneeId": 1,
    "priority": "high",
    "status": "in_progress",
    ...
  }
}
```

**✅ 验证点**:
- [ ] 负责人、优先级、状态同时更新成功

---

#### 5.4 完成问题（in_progress → done）

**API**: `PUT /api/v1/issues/1`

**请求**:
```json
{
  "status": "done"
}
```

**预期响应**:
```json
{
  "code": 200,
  "message": "问题更新成功",
  "data": {
    "id": 1,
    "status": "done",
    ...
  }
}
```

**✅ 验证点**:
- [ ] 状态从 "in_progress" 变为 "done"

---

### Test Case 6: 获取看板数据（验证分组功能）

**目标**：验证看板数据按状态正确分组

**API**: `GET /api/v1/issues/board?projectId=1`

**预期响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "todo": [
      {
        "id": 3,
        "issueNumber": "TEST-3",
        "title": "修复评论功能 Bug",
        "status": "todo",
        ...
      }
    ],
    "inProgress": [
      {
        "id": 2,
        "issueNumber": "TEST-2",
        "title": "实现项目看板功能（含拖拽）",
        "status": "in_progress",
        ...
      }
    ],
    "done": [
      {
        "id": 1,
        "issueNumber": "TEST-1",
        "title": "实现用户登录功能",
        "status": "done",
        ...
      }
    ],
    "closed": []
  }
}
```

**✅ 验证点**:
- [ ] 问题按状态正确分组
- [ ] todo 列表包含 TEST-3
- [ ] inProgress 列表包含 TEST-2
- [ ] done 列表包含 TEST-1
- [ ] closed 列表为空数组（不是 null）

---

### Test Case 7: 删除问题

**目标**：验证删除功能

#### 7.1 删除问题

**API**: `DELETE /api/v1/issues/3`

**预期响应**:
```json
{
  "code": 200,
  "message": "问题删除成功",
  "data": null
}
```

**✅ 验证点**:
- [ ] 状态码 200
- [ ] 返回成功消息

---

#### 7.2 验证删除结果

**API**: `GET /api/v1/issues/3`

**预期响应**:
```json
{
  "code": 404,
  "message": "问题不存在",
  "data": null
}
```

**✅ 验证点**:
- [ ] 返回 404
- [ ] 问题已被删除

---

### Test Case 8: 权限验证

**目标**：验证跨用户权限控制

#### 8.1 注册第二个用户

**API**: `POST /api/v1/auth/register`

**请求**:
```json
{
  "username": "user2",
  "email": "user2@example.com",
  "password": "User2123456"
}
```

---

#### 8.2 用户2登录

**API**: `POST /api/v1/auth/login`

**请求**:
```json
{
  "email": "user2@example.com",
  "password": "User2123456"
}
```

**✅ 验证点**:
- [ ] 获取 user2 的 token

---

#### 8.3 用户2尝试访问用户1的项目

**API**: `GET /api/v1/issues?projectId=1`

**Headers**:
```
Authorization: Bearer {user2_token}
```

**预期响应**:
```json
{
  "code": 403,
  "message": "无权访问该项目",
  "data": null
}
```

**✅ 验证点**:
- [ ] 返回 403 Forbidden
- [ ] 跨用户访问被正确拒绝

---

### Test Case 9: 参数校验

**目标**：验证参数校验是否生效

#### 9.1 标题为空

**API**: `POST /api/v1/issues`

**请求**:
```json
{
  "projectId": 1,
  "title": "",
  "description": "测试"
}
```

**预期响应**:
```json
{
  "code": 400,
  "message": "参数校验失败",
  "data": null
}
```

**✅ 验证点**:
- [ ] 返回 400
- [ ] 标题不能为空的校验生效

---

#### 9.2 项目ID为空

**API**: `POST /api/v1/issues`

**请求**:
```json
{
  "title": "测试问题"
}
```

**预期响应**:
```json
{
  "code": 400,
  "message": "参数校验失败",
  "data": null
}
```

**✅ 验证点**:
- [ ] 返回 400
- [ ] 项目ID不能为空的校验生效

---

## 测试总结

### 测试结果记录表

| 测试用例 | 状态 | 备注 |
|---------|------|------|
| Test Case 1: 准备测试数据 | ⬜ 待测试 | |
| Test Case 2: 创建问题（sequence_id递增） | ⬜ 待测试 | |
| Test Case 3: 获取问题列表（筛选功能） | ⬜ 待测试 | |
| Test Case 4: 获取问题详情 | ⬜ 待测试 | |
| Test Case 5: 更新问题（状态流转） | ⬜ 待测试 | |
| Test Case 6: 获取看板数据（分组功能） | ⬜ 待测试 | |
| Test Case 7: 删除问题 | ⬜ 待测试 | |
| Test Case 8: 权限验证 | ⬜ 待测试 | |
| Test Case 9: 参数校验 | ⬜ 待测试 | |

### 验收标准

**必须全部通过的项目**：
- [ ] 问题编号格式正确（TEST-1, TEST-2, TEST-3）
- [ ] sequence_id 自动递增
- [ ] 状态筛选正常
- [ ] 优先级筛选正常
- [ ] 看板数据按状态正确分组
- [ ] 问题更新和删除功能正常
- [ ] 权限验证生效（跨用户访问被拒绝）
- [ ] 参数校验生效

**可选项**：
- [ ] 并发创建问题时 sequence_id 无冲突
- [ ] 日期范围校验（dueDate >= startDate）

---

## 问题记录

**如果测试中发现问题，记录在此：**

| 问题编号 | 问题描述 | 严重程度 | 状态 |
|---------|---------|---------|------|
| BUG-1 | （示例）问题编号生成错误 | Critical | 待修复 |

---

**测试人员**：（待填写）
**测试日期**：2026-03-24
**测试版本**：Day 3 Implementation
