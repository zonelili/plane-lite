-- Plane Lite 数据库初始化脚本
-- 版本: v1.0.0
-- 日期: 2026-03-24
-- 说明: 创建核心表结构

-- ==============================================
-- 1. 用户表
-- ==============================================
CREATE TABLE IF NOT EXISTS `user` (
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

-- ==============================================
-- 2. 工作区表
-- ==============================================
CREATE TABLE IF NOT EXISTS `workspace` (
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

-- ==============================================
-- 3. 项目表
-- ==============================================
CREATE TABLE IF NOT EXISTS `project` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '项目ID',
  `workspace_id` BIGINT NOT NULL COMMENT '所属工作区ID',
  `name` VARCHAR(100) NOT NULL COMMENT '项目名称',
  `identifier` VARCHAR(10) NOT NULL COMMENT '项目标识（用于问题编号，如 PROJ）',
  `description` TEXT DEFAULT NULL COMMENT '项目描述',
  `icon` VARCHAR(50) DEFAULT NULL COMMENT '项目图标（emoji）',
  `cover_image` VARCHAR(255) DEFAULT NULL COMMENT '封面图片 URL',
  `lead_id` BIGINT DEFAULT NULL COMMENT '项目负责人用户ID',
  `created_by` BIGINT NOT NULL COMMENT '创建者用户ID',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  FOREIGN KEY (`workspace_id`) REFERENCES `workspace`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`lead_id`) REFERENCES `user`(`id`) ON DELETE SET NULL,
  FOREIGN KEY (`created_by`) REFERENCES `user`(`id`) ON DELETE RESTRICT,
  UNIQUE KEY `uk_workspace_identifier` (`workspace_id`, `identifier`),
  INDEX `idx_workspace_id` (`workspace_id`),
  INDEX `idx_lead_id` (`lead_id`),
  INDEX `idx_created_by` (`created_by`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='项目表';

-- ==============================================
-- 4. 问题表
-- ==============================================
CREATE TABLE IF NOT EXISTS `issue` (
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

-- ==============================================
-- 5. 问题评论表
-- ==============================================
CREATE TABLE IF NOT EXISTS `issue_comment` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '评论ID',
  `issue_id` BIGINT NOT NULL COMMENT '所属问题ID',
  `user_id` BIGINT NOT NULL COMMENT '评论用户ID',
  `content` VARCHAR(2000) NOT NULL COMMENT '评论内容',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  FOREIGN KEY (`issue_id`) REFERENCES `issue`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE,
  INDEX `idx_issue_id` (`issue_id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='问题评论表';

-- ==============================================
-- 插入测试数据（可选）
-- ==============================================

-- 测试用户（密码: password123, BCrypt 加密）
INSERT INTO `user` (`username`, `email`, `password`, `display_name`) VALUES
('admin', 'admin@planelite.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '管理员');

-- 测试工作区
INSERT INTO `workspace` (`name`, `slug`, `description`, `owner_id`) VALUES
('默认工作区', 'default-workspace', '这是默认工作区', 1);

-- 测试项目
INSERT INTO `project` (`workspace_id`, `name`, `identifier`, `description`, `icon`, `created_by`) VALUES
(1, 'Plane Lite', 'PL', '轻量级项目管理系统', '🚀', 1);

-- 测试问题
INSERT INTO `issue` (`project_id`, `sequence_id`, `title`, `description`, `priority`, `status`, `reporter_id`) VALUES
(1, 1, '实现用户登录功能', '需要支持邮箱登录和 JWT 认证', 'high', 'todo', 1),
(1, 2, '设计数据库表结构', '按照需求文档设计 5 张核心表', 'medium', 'done', 1),
(1, 3, '实现项目管理功能', '支持项目的创建、编辑、删除', 'medium', 'in_progress', 1);

-- ==============================================
-- 初始化完成
-- ==============================================
SELECT '✅ 数据库初始化完成！' AS status;
