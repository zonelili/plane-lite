# Plane Lite 项目文档索引

本目录包含 Plane Lite 项目的所有设计文档、规范和工作流说明。

---

## 📁 目录结构

```
docs/
├── requirements/          # 需求文档
│   └── 01-project-overview.md
├── architecture/          # 架构设计
│   ├── database-design.md
│   ├── system-architecture.md (待创建)
│   └── frontend-architecture.md (待创建)
├── conventions/           # 编码规范
│   ├── api.md
│   ├── coding-standards.md (待创建)
│   ├── database.md (待创建)
│   └── frontend.md (待创建)
├── workflows/             # 工作流程
│   ├── development-workflow.md (待创建)
│   └── git-workflow.md (待创建)
└── decisions/             # 架构决策记录 (ADR)
    └── (待添加)
```

---

## 📚 核心文档

### 需求文档
- **[项目概述与版本规划](./requirements/01-project-overview.md)**
  - 项目背景和目标
  - Plane 原项目功能分析
  - MVP 版本规划（1 周目标）
  - 后续版本规划（V2.0、V3.0、V4.0+）
  - 风险评估和成功标准

### 架构设计
- **[数据库设计](./architecture/database-design.md)**
  - MVP 数据库表设计
  - ER 图
  - 索引设计
  - V2.0 扩展表预览

### 编码规范
- **[API 设计规范](./conventions/api.md)**
  - RESTful API 设计原则
  - URL 设计规范
  - 统一响应格式
  - MVP API 接口清单
  - 认证和授权
  - 错误处理

---

## 🚀 快速导航

### 我想了解...

**项目整体情况**
→ [项目概述与版本规划](./requirements/01-project-overview.md)

**数据库表结构**
→ [数据库设计](./architecture/database-design.md)

**API 接口定义**
→ [API 设计规范](./conventions/api.md)

**编码规范**
→ [编码规范](./conventions/coding-standards.md) (待创建)

**开发流程**
→ [开发工作流](./workflows/development-workflow.md) (待创建)

---

## 📝 文档状态

| 文档 | 状态 | 最后更新 |
|------|------|----------|
| 项目概述与版本规划 | ✅ 已完成 | 2026-03-23 |
| 数据库设计 | ✅ 已完成 | 2026-03-23 |
| API 设计规范 | ✅ 已完成 | 2026-03-23 |
| 系统架构设计 | ⏳ 待创建 | - |
| 前端架构设计 | ⏳ 待创建 | - |
| 编码规范 | ⏳ 待创建 | - |
| 数据库规范 | ⏳ 待创建 | - |
| 前端规范 | ⏳ 待创建 | - |
| 开发工作流 | ⏳ 待创建 | - |
| Git 工作流 | ⏳ 待创建 | - |

---

## 🔄 文档更新规则

### 何时更新文档？

1. **需求变更**：更新需求文档
2. **架构调整**：更新架构文档，创建 ADR
3. **API 变更**：更新 API 规范
4. **规范修改**：更新编码规范

### 文档版本管理

所有文档都应包含版本信息：
```markdown
**文档版本**：v1.0
**创建日期**：2026-03-23
**最后更新**：2026-03-23
**状态**：已确认/草稿/废弃
```

### 文档审查

重要文档变更需要经过审查：
- 需求文档：产品负责人审查
- 架构文档：技术负责人审查
- 规范文档：团队共识

---

## 📖 文档撰写规范

### Markdown 格式

- 使用标准 Markdown 语法
- 标题层级清晰（最多 4 级）
- 代码块指定语言
- 表格对齐

### 文件命名

- 使用小写字母和连字符：`database-design.md`
- 有序文档加编号前缀：`01-project-overview.md`
- 英文命名，便于跨平台

### 内容组织

- **概述**：简要说明文档目的
- **详细内容**：分章节详细说明
- **示例**：提供具体示例
- **参考资料**：相关链接和文档
- **更新记录**：记录变更历史

---

## 🔗 相关资源

### 外部资源
- **Plane 官网**：https://plane.so
- **Plane GitHub**：https://github.com/makeplane/plane
- **Plane 文档**：https://docs.plane.so

### 内部资源
- **CLAUDE.md**：项目根目录的 AI 指导文档
- **README.md**：项目根目录的项目说明

---

## 📞 联系方式

如有疑问或建议，请：
1. 查阅相关文档
2. 在团队群里讨论
3. 创建 Issue 或 Pull Request

---

**最后更新**：2026-03-23
