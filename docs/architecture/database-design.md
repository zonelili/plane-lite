# 数据库设计文档

**文档版本**：v1.0
**创建日期**：2026-03-23
**状态**：设计中

---

## 1. 数据库概述

### 1.1 基本信息

- **数据库类型**：MySQL 8.0
- **字符集**：utf8mb4
- **排序规则**：utf8mb4_unicode_ci
- **引擎**：InnoDB
- **命名规范**：小写下划线命名

### 1.2 设计原则

1. **规范化**：符合第三范式（3NF）
2. **可扩展**：预留扩展字段
3. **性能优先**：合理使用索引
4. **审计追踪**：所有表包含 `created_at` 和 `updated_at`
5. **软删除**：核心业务表支持软删除

---

## 2. MVP 数据库表设计

### 2.1 用户表（user）

**表说明**：存储用户基本信息和认证数据

```sql
CREATE TABLE `user` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
  `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
  `email` VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱',
  `password` VARCHAR(255) NOT NULL COMMENT '密码（BCrypt 加密）',
  `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像 URL',
  `display_name` VARCHAR(100) DEFAULT NULL COMMENT '显示名称',
  `is_active` TINYINT(1) DEFAULT 1 COMMENT '是否激活（0:否, 1:是）',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX `idx_email` (`email`),
  INDEX `idx_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';
```

**字段说明**：

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | BIGINT | ✅ | 主键，自增 |
| username | VARCHAR(50) | ✅ | 用户名，唯一 |
| email | VARCHAR(100) | ✅ | 邮箱，唯一 |
| password | VARCHAR(255) | ✅ | BCrypt 加密后的密码 |
| avatar | VARCHAR(255) | ❌ | 头像 URL |
| display_name | VARCHAR(100) | ❌ | 显示名称 |
| is_active | TINYINT(1) | ✅ | 账号是否激活 |
| created_at | DATETIME | ✅ | 创建时间 |
| updated_at | DATETIME | ✅ | 更新时间 |

### 2.2 工作区表（workspace）

**表说明**：工作区是最顶层的组织单元，包含多个项目

```sql
CREATE TABLE `workspace` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '工作区ID',
  `name` VARCHAR(100) NOT NULL COMMENT '工作区名称',
  `slug` VARCHAR(50) NOT NULL UNIQUE COMMENT '工作区唯一标识（URL 友好）',
  `description` TEXT DEFAULT NULL COMMENT '工作区描述',
  `owner_id` BIGINT NOT NULL COMMENT '所有者用户ID',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  FOREIGN KEY (`owner_id`) REFERENCES `user`(`id`) ON DELETE CASCADE,
  INDEX `idx_owner_id` (`owner_id`),
  INDEX `idx_slug` (`slug`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工作区表';
```

**字段说明**：

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | BIGINT | ✅ | 主键，自增 |
| name | VARCHAR(100) | ✅ | 工作区名称 |
| slug | VARCHAR(50) | ✅ | URL 标识，唯一 |
| description | TEXT | ❌ | 工作区描述 |
| owner_id | BIGINT | ✅ | 所有者用户ID |
| created_at | DATETIME | ✅ | 创建时间 |
| updated_at | DATETIME | ✅ | 更新时间 |

**MVP 说明**：
- MVP 阶段：一个用户对应一个默认工作区
- V2.0：支持多工作区、工作区成员管理

### 2.3 项目表（project）

**表说明**：项目是问题的容器，归属于工作区

```sql
CREATE TABLE `project` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '项目ID',
  `workspace_id` BIGINT NOT NULL COMMENT '所属工作区ID',
  `name` VARCHAR(100) NOT NULL COMMENT '项目名称',
  `identifier` VARCHAR(10) NOT NULL COMMENT '项目标识（用于问题编号，如 PROJ）',
  `description` TEXT DEFAULT NULL COMMENT '项目描述',
  `icon` VARCHAR(50) DEFAULT NULL COMMENT '项目图标（emoji）',
  `cover_image` VARCHAR(255) DEFAULT NULL COMMENT '封面图片 URL',
  `created_by` BIGINT NOT NULL COMMENT '创建者用户ID',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  FOREIGN KEY (`workspace_id`) REFERENCES `workspace`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`created_by`) REFERENCES `user`(`id`) ON DELETE RESTRICT,
  UNIQUE KEY `uk_workspace_identifier` (`workspace_id`, `identifier`),
  INDEX `idx_workspace_id` (`workspace_id`),
  INDEX `idx_created_by` (`created_by`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='项目表';
```

**字段说明**：

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | BIGINT | ✅ | 主键，自增 |
| workspace_id | BIGINT | ✅ | 所属工作区ID |
| name | VARCHAR(100) | ✅ | 项目名称 |
| identifier | VARCHAR(10) | ✅ | 项目标识（如 PROJ） |
| description | TEXT | ❌ | 项目描述 |
| icon | VARCHAR(50) | ❌ | 项目图标 |
| cover_image | VARCHAR(255) | ❌ | 封面图片 |
| created_by | BIGINT | ✅ | 创建者ID |
| created_at | DATETIME | ✅ | 创建时间 |
| updated_at | DATETIME | ✅ | 更新时间 |

### 2.4 问题表（issue）

**表说明**：核心业务表，存储任务/问题信息

```sql
CREATE TABLE `issue` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '问题ID',
  `project_id` BIGINT NOT NULL COMMENT '所属项目ID',
  `sequence_id` INT NOT NULL COMMENT '项目内序号（用于生成问题编号）',
  `title` VARCHAR(255) NOT NULL COMMENT '问题标题',
  `description` TEXT DEFAULT NULL COMMENT '问题描述',
  `priority` ENUM('none', 'low', 'medium', 'high', 'urgent') NOT NULL DEFAULT 'medium' COMMENT '优先级',
  `status` ENUM('todo', 'in_progress', 'done', 'closed') NOT NULL DEFAULT 'todo' COMMENT '状态',
  `assignee_id` BIGINT DEFAULT NULL COMMENT '负责人用户ID',
  `reporter_id` BIGINT NOT NULL COMMENT '报告人用户ID',
  `start_date` DATE DEFAULT NULL COMMENT '开始日期',
  `due_date` DATE DEFAULT NULL COMMENT '截止日期',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  FOREIGN KEY (`project_id`) REFERENCES `project`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`assignee_id`) REFERENCES `user`(`id`) ON DELETE SET NULL,
  FOREIGN KEY (`reporter_id`) REFERENCES `user`(`id`) ON DELETE RESTRICT,
  UNIQUE KEY `uk_project_sequence` (`project_id`, `sequence_id`),
  INDEX `idx_project_id` (`project_id`),
  INDEX `idx_assignee_id` (`assignee_id`),
  INDEX `idx_reporter_id` (`reporter_id`),
  INDEX `idx_status` (`status`),
  INDEX `idx_priority` (`priority`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='问题表';
```

**字段说明**：

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | BIGINT | ✅ | 主键，自增 |
| project_id | BIGINT | ✅ | 所属项目ID |
| sequence_id | INT | ✅ | 项目内序号（如 123） |
| title | VARCHAR(255) | ✅ | 问题标题 |
| description | TEXT | ❌ | 问题描述（支持 Markdown） |
| priority | ENUM | ✅ | 优先级（无、低、中、高、紧急） |
| status | ENUM | ✅ | 状态（待办、进行中、完成、关闭） |
| assignee_id | BIGINT | ❌ | 负责人ID |
| reporter_id | BIGINT | ✅ | 报告人ID |
| start_date | DATE | ❌ | 开始日期 |
| due_date | DATE | ❌ | 截止日期 |
| created_at | DATETIME | ✅ | 创建时间 |
| updated_at | DATETIME | ✅ | 更新时间 |

**问题编号规则**：
- 格式：`{project.identifier}-{sequence_id}`
- 示例：`PROJ-123`

### 2.5 问题评论表（issue_comment）

**表说明**：存储问题的评论和讨论

```sql
CREATE TABLE `issue_comment` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '评论ID',
  `issue_id` BIGINT NOT NULL COMMENT '所属问题ID',
  `user_id` BIGINT NOT NULL COMMENT '评论用户ID',
  `content` TEXT NOT NULL COMMENT '评论内容',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  FOREIGN KEY (`issue_id`) REFERENCES `issue`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE,
  INDEX `idx_issue_id` (`issue_id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='问题评论表';
```

**字段说明**：

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | BIGINT | ✅ | 主键，自增 |
| issue_id | BIGINT | ✅ | 所属问题ID |
| user_id | BIGINT | ✅ | 评论用户ID |
| content | TEXT | ✅ | 评论内容 |
| created_at | DATETIME | ✅ | 创建时间 |
| updated_at | DATETIME | ✅ | 更新时间 |

---

## 3. ER 图

```
┌─────────────┐
│    user     │
│             │
│  id (PK)    │
│  username   │
│  email      │
│  password   │
└──────┬──────┘
       │
       │ owner_id
       │
       ▼
┌─────────────┐
│  workspace  │
│             │
│  id (PK)    │
│  name       │
│  slug       │
│  owner_id(FK)
└──────┬──────┘
       │
       │ workspace_id
       │
       ▼
┌─────────────┐        ┌──────────────┐
│   project   │◄───────┤    issue     │
│             │        │              │
│  id (PK)    │        │  id (PK)     │
│  name       │        │  project_id  │
│  identifier │        │  title       │
│  created_by │        │  status      │
└──────┬──────┘        │  priority    │
       │               │  assignee_id │
       │               │  reporter_id │
       │               └──────┬───────┘
       │                      │
       │                      │ issue_id
       │                      │
       │                      ▼
       │               ┌──────────────┐
       │               │issue_comment │
       │               │              │
       │               │  id (PK)     │
       └───created_by──┤  issue_id(FK)│
                       │  user_id (FK)│
                       │  content     │
                       └──────────────┘
```

---

## 4. 索引设计

### 4.1 主键索引
所有表的 `id` 字段均为主键索引。

### 4.2 唯一索引

| 表 | 字段 | 说明 |
|------|------|------|
| user | username | 用户名唯一 |
| user | email | 邮箱唯一 |
| workspace | slug | 工作区标识唯一 |
| project | workspace_id + identifier | 项目标识在工作区内唯一 |
| issue | project_id + sequence_id | 问题序号在项目内唯一 |

### 4.3 外键索引

| 表 | 字段 | 关联表 | 说明 |
|------|------|--------|------|
| workspace | owner_id | user | 工作区所有者 |
| project | workspace_id | workspace | 项目所属工作区 |
| project | created_by | user | 项目创建者 |
| issue | project_id | project | 问题所属项目 |
| issue | assignee_id | user | 问题负责人 |
| issue | reporter_id | user | 问题报告人 |
| issue_comment | issue_id | issue | 评论所属问题 |
| issue_comment | user_id | user | 评论用户 |

### 4.4 查询优化索引

| 表 | 索引字段 | 查询场景 |
|------|----------|----------|
| issue | status | 按状态筛选问题 |
| issue | priority | 按优先级筛选问题 |
| issue_comment | created_at | 评论按时间排序 |

---

## 5. V2.0 扩展表（预览）

### 5.1 标签表（label）

```sql
CREATE TABLE `label` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '标签ID',
  `project_id` BIGINT NOT NULL COMMENT '所属项目ID',
  `name` VARCHAR(50) NOT NULL COMMENT '标签名称',
  `color` VARCHAR(7) NOT NULL COMMENT '标签颜色（Hex 格式）',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`project_id`) REFERENCES `project`(`id`) ON DELETE CASCADE,
  UNIQUE KEY `uk_project_name` (`project_id`, `name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='标签表';
```

### 5.2 问题标签关联表（issue_label）

```sql
CREATE TABLE `issue_label` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '关联ID',
  `issue_id` BIGINT NOT NULL COMMENT '问题ID',
  `label_id` BIGINT NOT NULL COMMENT '标签ID',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`issue_id`) REFERENCES `issue`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`label_id`) REFERENCES `label`(`id`) ON DELETE CASCADE,
  UNIQUE KEY `uk_issue_label` (`issue_id`, `label_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='问题标签关联表';
```

### 5.3 项目成员表（project_member）

```sql
CREATE TABLE `project_member` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '成员ID',
  `project_id` BIGINT NOT NULL COMMENT '项目ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `role` ENUM('owner', 'member', 'viewer') NOT NULL DEFAULT 'member' COMMENT '角色',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`project_id`) REFERENCES `project`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE,
  UNIQUE KEY `uk_project_user` (`project_id`, `user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='项目成员表';
```

---

## 6. 数据库初始化脚本

**文件位置**：`/backend/src/main/resources/db/migration/V1__init.sql`

包含：
1. 创建所有 MVP 表
2. 创建索引
3. 插入测试数据（可选）

---

## 7. 更新记录

| 版本 | 日期 | 变更说明 |
|------|------|----------|
| v1.0 | 2026-03-23 | 初始版本，MVP 表设计 |
