# Plane Lite 项目文档索引

本目录包含 Plane Lite 项目的所有设计文档、规范和工作流说明。

---

## 📁 目录结构

```
docs/
├── requirements/          # 📋 需求文档（持久）
│   └── 01-project-overview.md
├── architecture/          # 🏗️ 架构设计（持久）
│   ├── database-design.md
│   ├── system-architecture.md
│   ├── backend-implementation.md
│   ├── frontend-implementation.md
│   └── deployment.md
├── conventions/           # 📖 编码规范（持久）
│   ├── api.md
│   ├── coding-standards.md (待创建)
│   ├── database.md (待创建)
│   └── frontend.md (待创建)
├── workflows/             # 🔄 工作流程（持久）
│   ├── quality-assurance.md
│   ├── development-workflow.md (待创建)
│   └── git-workflow.md (待创建)
├── decisions/             # 🎯 架构决策记录 ADR（持久）
│   ├── ADR-001-quality-assurance-workflow.md
│   ├── ADR-002-command-binding-principle.md
│   └── ADR-003-frontend-design-confirmation.md
├── experiences/           # 📚 可复用的经验库（持久）
│   ├── README.md
│   ├── 2026-03-25-frontend-visual-design-confirmation.md
│   ├── 2026-03-26-quality-assurance-four-layers.md
│   └── 2026-03-26-documentation-reorganization.md
├── reports/               # 📊 报告和分析（临时）
│   ├── code-review-day1-2.md
│   └── java-version-fix.md
├── development-plan.md    # 📅 开发计划（临时，项目完成后归档）
├── lessons-learned.md     # 💡 经验总结（持久累积）
└── project-kickoff.md     # 🚀 项目启动记录
```

## 📚 文档分类规范

### 持久文档（Persistent Documents）

**定义**：项目长期维护的文档，需要持续更新

| 目录 | 用途 | 示例 | 更新频率 |
|------|------|------|----------|
| `requirements/` | 需求文档和版本规划 | 项目概述、功能需求 | 版本迭代时 |
| `architecture/` | 系统架构和技术选型 | 数据库设计、系统架构图 | 架构变更时 |
| `conventions/` | 编码规范和最佳实践 | API 规范、代码风格 | 规范调整时 |
| `workflows/` | 常见开发任务流程 | 质量保障流程、Git 工作流 | 流程优化时 |
| `decisions/` | 架构决策记录（ADR） | ADR-001/002/003 等决策 | 重大决策时 |
| `experiences/` | 可复用的经验知识库 | 设计确认、质量保障经验 | 持续积累 |
| `lessons-learned.md` | 项目经验总结 | Harness 实践、技术决策 | 持续累积 |

**命名规范**：
- 使用小写字母和连字符：`database-design.md`
- 有序文档加编号前缀：`01-project-overview.md`
- ADR 使用标准格式：`ADR-NNN-title.md`

### 临时文档（Temporary Documents）

**定义**：项目特定阶段的文档，项目完成后归档或删除

| 目录/文件 | 用途 | 示例 | 生命周期 |
|----------|------|------|----------|
| `reports/` | 审查报告、分析报告 | 代码审查报告、问题修复报告 | 完成后归档 |
| `development-plan.md` | 开发计划和任务分解 | Day 1-7 开发任务 | 项目完成后归档 |
| `project-kickoff.md` | 项目启动记录 | 初始化过程记录 | 归档保留 |

**归档规则**：
- 项目完成后移动到 `archive/` 目录
- 保留有价值的内容到 `lessons-learned.md`
- 删除过时的临时报告

### 文档创建规则

**在创建新文档前，必须问自己：**

1. **这是持久文档还是临时文档？**
   - 持久 → 放在对应的分类目录下
   - 临时 → 放在 `reports/` 或作为临时文件

2. **这个内容是否可以合并到现有文档？**
   - 问题修复记录 → 合并到 `lessons-learned.md`
   - 技术决策 → 创建 ADR 或更新 architecture/
   - 工作流程 → 创建或更新 workflows/

3. **文档命名是否符合规范？**
   - ✅ `code-review-day1-2.md`
   - ❌ `CODE_REVIEW_DAY1-2.md`（不要全大写）

4. **CLAUDE.md 是否需要更新索引？**
   - 如果添加了新的**持久文档目录**，必须更新 CLAUDE.md
   - 临时文档不需要更新 CLAUDE.md

---

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

### 工作流程
- **[质量保障流程](./workflows/quality-assurance.md)**
  - 三层质量保障机制（Plan Mode、Implementation、Code Review）
  - 技术栈兼容性验证
  - 实体类与数据库一致性检查
  - 配置文件完整性验证
  - 编译和启动验证

### 架构决策记录（ADR）
- **[ADR-001: 质量保障工作流](./decisions/ADR-001-quality-assurance-workflow.md)**
  - 背景：Day 2 开发中暴露的质量问题
  - 决策：建立四层质量保障机制
  - 理由：减少返工、提高代码质量、知识积累
  - 影响：开发流程优化、文档持久化

- **[ADR-002: 真实命令绑定与证据链管理](./decisions/ADR-002-command-binding-principle.md)** ⭐ NEW
  - 背景：学习 Harness Lab 框架的最佳实践
  - 决策：使用 Makefile 进行真实命令绑定
  - 原则：命令显式绑定、证据链管理、环境清单绑定
  - 影响：AI Agent 可自动执行、复现性强、易于 CI/CD 集成

- **[ADR-003: 前端视觉设计语言一次性确认](./decisions/ADR-003-frontend-design-confirmation.md)** ⭐ NEW
  - 背景：Day 5 前端开发进行了 10+ 次设计调整
  - 决策：在 tech-design 阶段前置确认 4 大设计问题
  - ROI：预期 Day 6+ 时间节省 50%（4:1 收益）
  - 影响：提高开发效率、改善用户满意度

### 经验知识库 ⭐ NEW
- **[经验库索引](./experiences/README.md)**
  - 可复用的知识文件，按时间和主题组织
  - 每个文件包含：问题场景、根本原因、解决方案、验证方式

- **[前端视觉设计确认经验](./experiences/2026-03-25-frontend-visual-design-confirmation.md)**
  - Day 5 前端页面从暗色改为亮色，进行 10+ 次迭代的根本原因分析
  - 解决方案：在 tech-design 阶段一次性确认设计语言

- **[质量保障四层机制](./experiences/2026-03-26-quality-assurance-four-layers.md)**
  - 从三层升级到四层的演进过程
  - Code Review 必须先于 Testing 的原因分析
  - 每层的职责分工和前置检查

- **[文档分类规范和重组实践](./experiences/2026-03-26-documentation-reorganization.md)**
  - Day 2 文档混乱问题的根本原因分析
  - 七类文档分类体系的设计原则和实施方式

### 报告和分析
- **[代码审查报告 - Day 1-2](./reports/code-review-day1-2.md)**
  - 全面审查 Day 1-2 的代码质量
  - 发现 1 个 CRITICAL 问题（Java 版本不匹配）
  - 发现 2 个安全问题（JWT、密码存储）
  - 验证代码与 Plan 一致性

- **[Java 版本问题修复报告](./reports/java-version-fix.md)**
  - 修复 Maven Java 版本不匹配问题
  - 验证编译和启动成功
  - 提供预防措施和改进建议

### 开发指南
- **[开发计划](./development-plan.md)**
  - Day 1-7 开发任务分解
  - 每日目标和验收标准
  - 技术栈和依赖关系

- **[经验总结](./lessons-learned.md)**
  - Harness 实践经验
  - 质量保障机制的教训（Day 2）
  - 人机协作经验
  - 技术决策经验
  - 文档编写经验

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
→ [质量保障流程](./workflows/quality-assurance.md)
→ [开发工作流](./workflows/development-workflow.md) (待创建)

**开发计划**
→ [Day 1-7 开发计划](./development-plan.md)

**经验总结**
→ [AI Coding 实践经验](./lessons-learned.md)

**代码审查**
→ [代码审查报告 - Day 1-2](./reports/code-review-day1-2.md)

**问题修复**
→ [Java 版本问题修复](./reports/java-version-fix.md)

**架构决策**
→ [ADR-001: 质量保障工作流](./decisions/ADR-001-quality-assurance-workflow.md)
→ [ADR-002: 真实命令绑定](./decisions/ADR-002-command-binding-principle.md)
→ [ADR-003: 前端设计确认](./decisions/ADR-003-frontend-design-confirmation.md)

**可复用经验** ⭐ NEW
→ [经验库索引](./experiences/README.md)
→ [前端设计确认](./experiences/2026-03-25-frontend-visual-design-confirmation.md)
→ [质量保障四层机制](./experiences/2026-03-26-quality-assurance-four-layers.md)
→ [文档分类规范](./experiences/2026-03-26-documentation-reorganization.md)

---

## 📝 文档状态

| 文档 | 状态 | 最后更新 |
|------|------|----------|
| 项目概述与版本规划 | ✅ 已完成 | 2026-03-23 |
| 数据库设计 | ✅ 已完成 | 2026-03-23 |
| API 设计规范 | ✅ 已完成 | 2026-03-23 |
| 质量保障流程 | ✅ 已完成 | 2026-03-24 |
| ADR-001 质量保障工作流 | ✅ 已完成 | 2026-03-24 |
| ADR-002 真实命令绑定原则 | ✅ 已完成 | 2026-03-26 |
| ADR-003 前端设计确认流程 | ✅ 已完成 | 2026-03-26 |
| 前端视觉设计确认经验 | ✅ 已完成 | 2026-03-25 |
| 质量保障四层机制 | ✅ 已完成 | 2026-03-26 |
| 文档分类规范和重组实践 | ✅ 已完成 | 2026-03-26 |
| 开发计划 | ✅ 已完成 | 2026-03-24 |
| 经验总结 | ✅ 持续更新 | 2026-03-24 |
| 代码审查报告 Day 1-2 | ✅ 已完成 | 2026-03-24 |
| Java 版本修复报告 | ✅ 已完成 | 2026-03-24 |
| 系统架构设计 | ✅ 已完成 | 2026-03-23 |
| 后端实现文档 | ✅ 已完成 | 2026-03-23 |
| 前端实现文档 | ✅ 已完成 | 2026-03-23 |
| 部署文档 | ✅ 已完成 | 2026-03-23 |
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

**最后更新**：2026-03-26
