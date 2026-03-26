# 文档分类规范和重组实践（2026-03-26）

## 问题场景

在 Day 2 开发时，发现文档分类混乱：
- 技术方案、数据库设计、API 规范混放在 `/docs/architecture/`
- 经验总结、每日日志混放在 `/docs/` 根目录
- README 文件多达 5+ 个，入口不清晰
- 新人无法快速判断"这个问题的答案在哪个文件"

**表现**：
- 搜索文档困难
- 重复编写同类型文档
- 文档容易被忽视或过期

## 根本原因

原有的文档结构是**线性的、按功能域划分的**，而不是**按用途和优先级划分的**。

### 问题分类

1. **长期有效的规范**（应该沉淀）
   - API 设计规范
   - 数据库设计规范
   - 代码编写规范

2. **项目特定的决策**（应该可查）
   - 技术选型理由
   - 架构设计方案
   - 数据库表结构设计

3. **可复用的经验**（应该共享）
   - 解决过的问题
   - 最佳实践
   - 踩过的坑

4. **阶段性的记录**（应该归档）
   - 开发计划
   - 会议记录
   - 每日进度

原有的结构让这四类混在一起，导致：
- ❌ 规范文档难以查找
- ❌ 经验容易丢失
- ❌ 决策理由不明确
- ❌ 进度更新无处可记

## 解决方案

### 新的文档分类体系

```
docs/
├── 📋 requirements/              # 持久的需求和规划
│   ├── INDEX.md                  # 需求索引（当前活跃需求）
│   ├── 01-project-overview.md   # 项目概述
│   ├── development-plan.md      # 开发计划（Day 1-7）
│   └── reports/                 # 需求报告和结果
│
├── 🏗️ architecture/              # 持久的架构设计
│   ├── system-architecture.md   # 系统架构总览
│   ├── database-design.md       # 数据库设计（ER图、表结构）
│   ├── backend-implementation.md # 后端实现方案
│   └── frontend-implementation.md # 前端实现方案
│
├── 📖 conventions/               # 持久的规范和标准
│   ├── api.md                    # RESTful API 设计规范
│   ├── reporting.md              # 报告编写规范
│   └── code-style.md            # 代码风格指南（待创建）
│
├── 🔄 workflows/                 # 持久的工作流程
│   └── quality-assurance.md      # 质量保障四层机制
│
├── 🎯 decisions/                 # 架构决策记录（ADR）
│   └── ADR-001-quality-assurance-workflow.md
│
├── 📚 experiences/               # 可复用的经验库
│   ├── README.md                 # 经验索引
│   ├── 2026-03-26-frontend-visual-design-confirmation.md
│   ├── 2026-03-26-quality-assurance-four-layers.md
│   └── 2026-03-26-documentation-reorganization.md
│
├── 📊 reports/                   # 临时的执行报告
│   ├── REPORT_TEMPLATE.md       # 报告模板
│   ├── day-1-code-review.md
│   ├── day-1-qa.md
│   └── day-N-*.md
│
├── 📅 lessons-learned.md         # 里程碑总结（精简版）
├── project-kickoff.md           # 项目启动记录（归档）
└── README.md                     # 文档索引（导航入口）
```

### 分类规则

#### 1. `requirements/`（需求和规划）

**特点**：
- 长期保留
- 频繁更新状态
- 有对应的报告

**内容**：
- 项目概述和范围
- 开发计划和里程碑
- 需求索引和状态变更
- 执行报告（Day N 的 Code Review、QA、Ship）

#### 2. `architecture/`（架构设计）

**特点**：
- 长期有效
- 设计阶段确定，实施阶段参考
- 可能随功能演进而更新

**内容**：
- 系统架构图和模块划分
- 数据库表结构和 ER 图
- 技术栈选型和理由
- 后端和前端的实现方案

#### 3. `conventions/`（规范和标准）

**特点**：
- 长期有效
- 跨项目通用
- 约束开发过程中的决策

**内容**：
- API 设计规范（URL、请求/响应格式）
- 代码编写规范（命名、注解、异常处理）
- 数据库设计规范（表名、字段、索引）
- 报告编写规范（内容、格式、审批流程）

#### 4. `workflows/`（工作流程）

**特点**：
- 长期有效
- 定义了如何工作
- 约束开发方法

**内容**：
- 质量保障流程（四层机制）
- 代码审查流程
- 发布流程

#### 5. `decisions/`（架构决策记录，ADR）

**特点**：
- 永久记录
- 记录"为什么这样做"
- 防止决策被遗忘或反复讨论

**内容**：
- ADR-001: 质量保障工作流（为什么要四层？）
- ADR-002: 技术栈选型（为什么用 Spring Boot 3.1.5？）
- ADR-003: 前端设计语言（为什么是编辑风工业极简？）

#### 6. `experiences/`（经验库）

**特点**：
- 独立的知识文件
- 可复用和共享
- 带时间戳便于查找

**内容**：
- 解决过的问题和解决方案
- 最佳实践总结
- 踩过的坑和教训
- 工具和脚本使用经验

#### 7. `reports/`（执行报告）

**特点**：
- 临时文件
- 随着项目进度生成
- 报告完成后可归档

**内容**：
- Code Review 报告
- QA / 测试报告
- Ship / 发布报告
- 性能测试报告（如有）

### 分类的关键原则

| 分类 | 更新频率 | 保留期限 | 用途 |
|------|---------|---------|------|
| requirements | 高（状态变化） | 长期 | 了解当前任务 |
| architecture | 中（功能演进） | 长期 | 实施代码时参考 |
| conventions | 低（规范改进） | 长期 | 开发过程中遵循 |
| workflows | 低（流程优化） | 长期 | 整个团队遵循 |
| decisions | 无（历史决策） | 永久 | 理解"为什么" |
| experiences | 中（知识沉淀） | 长期 | 复用和学习 |
| reports | 高（每日生成） | 中期 | 审查和验收 |

## 实施方式

### 第 1 步：建立新的目录结构

```bash
mkdir -p docs/{architecture,conventions,workflows,decisions,experiences,reports}
```

### 第 2 步：文件迁移

**从 `/docs/architecture/` 迁移到对应目录**：
- `system-architecture.md` → `/docs/architecture/system-architecture.md`（保留）
- `database-design.md` → `/docs/architecture/database-design.md`（保留）
- `backend-implementation.md` → `/docs/architecture/backend-implementation.md`（保留）
- `deployment.md` → 拆分到 workflows 或 architecture（待决定）

**新增文件**：
- `/docs/conventions/reporting.md` ← 新增
- `/docs/conventions/code-style.md` ← 待创建
- `/docs/experiences/2026-03-26-*.md` ← 经验文件

### 第 3 步：创建导航和索引

**顶层导航**：
- `docs/README.md` - 文档总索引，分类导航
- `requirements/INDEX.md` - 需求状态索引
- `experiences/README.md` - 经验库索引

**各分类的 README**（可选）：
- `architecture/README.md` - 架构设计导航
- `conventions/README.md` - 规范导航
- `experiences/README.md` - 经验导航

### 第 4 步：更新 CLAUDE.md

在快速导航中引用新的分类结构：
```markdown
### 规范和标准
- **API 规范**: `/docs/conventions/api.md`
- **报告规范**: `/docs/conventions/reporting.md`
- **数据库规范**: `/docs/architecture/database-design.md`

### 经验和决策
- **经验库**: `/docs/experiences/`
- **架构决策**: `/docs/decisions/`
```

## 验证方式

### 短期检查

- [ ] 新的目录结构是否已创建？
- [ ] 主要文件是否已迁移到对应位置？
- [ ] 导航链接是否都有效？

### 长期检查

- [ ] 新人能在 5 分钟内找到需要的文档吗？
- [ ] 文档是否保持最新（未过期）？
- [ ] 是否还有文件被放在错误的位置？

## 适用场景

### ✅ 适用

- 任何多人开发的项目
- 需要知识沉淀的长期项目
- 有明确规范和工作流的项目

### ⚠️ 可调整

- 小型项目可能不需要 `decisions/` 和 `workflows/`
- 初创项目可能 `conventions/` 和 `experiences/` 会快速增长

### ❌ 不适用

- 一次性脚本项目
- 纯原型项目
- 没有多人协作的项目

## 相关文件

### 参考文档
- `docs/README.md` - 文档总索引（待创建）
- `CLAUDE.md` - 项目地图

### 新增或更新的文件
- `docs/conventions/reporting.md` - 报告规范
- `.claude/skills/README.md` - Skills 导航
- `docs/experiences/README.md` - 经验库导航

## 优势总结

实施新的文档分类后的收益：

1. **快速查找**
   - 规范查询快速（conventions/）
   - 经验查询快速（experiences/）
   - 决策查询快速（decisions/）

2. **知识沉淀**
   - 经验独立成文，易复用
   - 决策明确记录，防止重复讨论
   - 规范统一管理，易维护

3. **工作流清晰**
   - 每个人都知道"这个问题的答案在哪个文件"
   - 规范执行有章可循
   - 经验分享有固定渠道

4. **项目演进可追踪**
   - requirements 记录了需求变化
   - architecture 记录了设计演进
   - decisions 记录了关键决策
   - experiences 记录了踩过的坑

## 版本历史

| 日期 | 变更 |
|------|------|
| 2026-03-26 | 创建文档分类规范，总结分类原则和实施方式 |
| 2026-03-24 | 原文档重组，发现分类混乱问题 |

## 后续改进

### 可考虑的优化

1. **自动化文档检查**
   - 检查过期文档（超过 3 个月未更新）
   - 检查链接有效性
   - 检查文档分类是否正确

2. **文档版本管理**
   - 规范文件版本化（conventions/api-v1.md, api-v2.md）
   - 决策文件可追加评论

3. **搜索和索引**
   - 建立全文搜索索引
   - 创建标签系统便于查询

4. **文档模板库**
   - 创建 templates/ 目录
   - 提供各类型文档的编写模板

## 核心原则

> **好的文档分类能让信息流动更顺畅，让新人快速上手，让经验得以沉淀和复用。**

分类的目标不是"完美的架构"，而是"能快速找到答案"。
