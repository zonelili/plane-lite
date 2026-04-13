# Plane Lite 项目 AI 编程演进总结

> 记录从 Day 1 到 Day 7 的完整 AI 全自动开发实践历程
>
> **时间跨度**: 2026-03-23 至 2026-04-01 (约 9 天)
> **文档目的**: 复盘 AI 编程的演进过程，总结踩过的坑、借鉴的思想、改进的方向

---

## 目录

1. [项目背景与目标](#1-项目背景与目标)
2. [演进的四个关键阶段](#2-演进的四个关键阶段)
3. [文档目录的修改历程](#3-文档目录的修改历程)
4. [Skill 的修改历程](#4-skill-的修改历程)
5. [流程的修改完善](#5-流程的修改完善)
6. [踩过的坑](#6-踩过的坑)
7. [借鉴的思想](#7-借鉴的思想)
8. [值得改进的方向](#8-值得改进的方向)
9. [最终形成的工作流](#9-最终形成的工作流)
10. [核心经验总结](#10-核心经验总结)

---

## 1. 项目背景与目标

### 1.1 项目概述

**Plane Lite** 是一个项目管理平台 MVP，模仿开源项目 [Plane](https://plane.so)，用 7 天时间验证 AI 全自动开发的可行性。

**技术栈**:
- 后端: Spring Boot 3.1.5 + MyBatis-Plus 3.5.5 + MySQL 8.0
- 前端: Vue 3.3.11 + TypeScript 5.2.2 + Vite 5.0.8 + Element Plus
- 部署: Docker + Nginx

**核心目标**:
1. 验证 AI 能否独立完成完整的软件开发周期
2. 探索 AI 编程的最佳实践和工程化方法
3. 积累可复用的经验和流程

### 1.2 开发时间线

| 日期 | 里程碑 | 关键事件 |
|------|--------|----------|
| 2026-03-23 | Day 1 | 项目初始化、后端骨架、用户认证 |
| 2026-03-24 | Day 2-4 | 工作区/项目/问题/评论模块 + **质量危机 → 四层机制建立** |
| 2026-03-25 | Day 5 | 前端项目搭建 + **设计迭代危机 → 设计确认机制** |
| 2026-03-26 | Harness 整合 | **真实命令绑定 + 证据链管理 + 经验独立化** |
| 2026-03-30 | Day 6 | 项目和问题管理页面 |
| 2026-04-01 | Day 7 | 看板视图、评论功能、Docker 部署 |

---

## 2. 演进的四个关键阶段

### 阶段 1: 质量危机与四层机制建立 (Day 2)

**发生了什么**:

Day 2 开发中暴露了三类严重问题:

```
问题 1: 版本兼容性
- Spring Boot 3.2 + MyBatis-Plus 3.5.5 不兼容
- 反复犯同样的错误，浪费 30+ 分钟

问题 2: 代码与方案不一致
- Project 实体有 leadId 字段
- 数据库脚本缺少 lead_id 列
- 启动报错: Unknown column 'lead_id'

问题 3: 环境配置问题
- MySQL 密码使用默认值
- JWT secret 长度不足 (440bits < 512bits)
```

**根本原因分析**:
- ❌ 技术选型时没有验证兼容性
- ❌ 代码完成后没有与 Plan 同步验证
- ❌ 启动失败才被动修复，不是主动检查
- ❌ AI 记忆不持久，流程没有文档化

**解决方案**: 建立 **四层质量保障机制**

```
Layer 1: Plan Mode (方案设计)
├── 技术栈兼容性验证
├── 环境依赖清单
├── 完整 DDL 设计
└── 验收标准定义
    ↓
Layer 2: Implementation (编码实现)
├── 实体类 ↔ 数据库自检
├── DTO 校验注解检查
├── Service 事务注解检查
└── 配置文件完整性检查
    ↓
Layer 3: Testing (测试验证) ← 独立层
├── 单元测试 (mvn test)
├── API 功能测试 (curl)
└── 部署验证 (启动 + 健康检查)
    ↓
Layer 4: Code Review (代码审查)
├── 代码 vs Plan 一致性
├── 实体 vs 数据库一致性
└── 代码规范检查
```

**关键文档**:
- `docs/workflows/quality-assurance.md`
- `docs/decisions/ADR-001-quality-assurance-workflow.md`

---

### 阶段 2: Skill 工作流优化 (Day 3-4)

**发生了什么**:

Day 3-4 虽然有了四层机制，但执行时又出问题:

```
问题 1: 完全跳过 Skill
- 看到用户计划后直接编码
- 没有调用 implement-day skill
- 违反了质量规范

问题 2: 跳过 Code Review 直接测试
- Implementation 完成后直接写测试脚本
- 跳过了 Code Review 阶段
- 测试时犯了 API 格式错误

问题 3: 测试前没有查文档
- 凭记忆/猜测 API 格式
- 用 username 而不是 email 登录
- Token 提取逻辑写错
```

**根本原因**: 缺少强制检查机制，靠 AI "自觉" 不可靠

**解决方案**: implement-day Skill 三次迭代

**第 1 次迭代**: 增加"查阅文档"步骤
```markdown
测试阶段步骤 0: 查阅文档（必须）
1. API 文档 (docs/conventions/api.md)
2. 历史测试报告 (docs/reports/day-{N-1}-test-results.md)
3. 数据库脚本 (db/migration/V1__init.sql)
```

**第 2 次迭代**: 强制"先获取真实响应，再写提取逻辑"
```bash
# 正确流程
步骤 2.1: 手动调用 API，获取真实 JSON 响应
步骤 2.2: 基于真实响应编写提取逻辑（不凭想象）
步骤 2.3: 逐个 API 测试并验证（不写完再测）
步骤 2.4: 保存测试脚本供后续复用
```

**第 3 次迭代**: 增加强制前置检查
```markdown
阶段 0: 工作流自检 (5秒)
- 是否通过 skill 进入？
- 当前工作目录正确吗？
- 是否有未完成的前置阶段？

阶段 3: Code Review 前置检查 (3秒)
- Implementation 完成且编译通过？

阶段 4: Testing 前置检查 (3秒)
- Code Review 必须完成？← 最容易跳过
```

**关键洞察**:
> "规范是用来遵守的，不是用来理解的。如果 Skill 说要做 X，就必须做 X，不要问为什么。"

---

### 阶段 3: 前端视觉设计确认机制 (Day 5)

**发生了什么**:

Day 5 前端开发时，登录/注册页面反复变更设计:

```
时间分配:
- 计划编码时间: 2 小时
- 实际编码时间: 1 小时
- 设计调整时间: 2+ 小时 (10+ 次迭代)
- 效率损失: 50%+

迭代内容:
暗色 → 亮色 → 对比度调整 → 输入框样式 → 按钮样式 → 字体颜色 ...
```

**根本原因**: tech-design 阶段没有前端视觉设计确认

**解决方案**: ADR-003 前端视觉设计一次性确认机制

**tech-design 阶段** (4 个设计问题):
```markdown
Q1: 色调定位 → 亮色系/暗色系/混合
Q2: 品牌色 → 主色、辅助色、强调色 (RGB/Hex)
Q3: 字体搭配 → 标题字体、正文字体、等宽字体
Q4: 参考设计 → 参照哪个设计系统或产品

输出物: frontend-design-spec.md
```

**implement-day 阶段** (一页一确认):
```
编码完成每个页面 → 展示效果 → 用户确认 → 调整 → 继续下一个
```

**Day 5 最终确定的设计规范**:
```
主色调: 亮色系（冷白背景）
品牌色:
  - 主色: 深墨棕 #1C1410（导航、标题）
  - 强调色: 琥珀金 #D4870A（按钮、链接）
  - 背景色: 暖奶油白 #FAF7F2（页面背景）
字体搭配:
  - 标题: DM Serif Display（衬线体）
  - 正文: JetBrains Mono（等宽体）
```

**预期效果**:
```
原模式（迭代式）: 2+ 小时，用户满意度 70%
新模式（一次性）: 30+ 分钟，用户满意度 90%+
ROI 提升: 75% 时间节省
```

---

### 阶段 4: Harness Lab 框架整合 (Day 6-7 前)

**发生了什么**:

基于 Harness Engineering 原则，进行两轮工程化改造:

**第一轮改造**:
1. ✅ 创建 Makefile 统一命令管理
2. ✅ 建立证据链管理（记录命令、返回码、时长）
3. ✅ 创建 `.claude/skills/README.md` 技能导航
4. ✅ 创建 `docs/experiences/` 经验独立化

**第二轮改造**:
1. ✅ 创建 ADR-002 真实命令绑定原则
2. ✅ 创建 ADR-003 前端设计确认原则
3. ✅ 验证 Makefile 命令实际生效
4. ✅ 建立快速恢复路径 (progress.txt < 1分钟恢复)

**核心成果**:

**1. 真实命令绑定 (ADR-002)**
```makefile
# Makefile
backend-verify: clean-backend compile-backend test-backend lint-backend
frontend-verify: clean-frontend lint-frontend test-frontend build-frontend
verify: backend-verify frontend-verify
```

**2. 证据链管理**
```markdown
## 执行信息
- 执行时间: 2026-03-26 14:30:00
- 执行命令: make backend-verify
- 返回码: 0（成功）
- 执行时长: 2 分钟 34 秒

## 命令输出摘要
$ mvn clean compile → BUILD SUCCESS
$ mvn test → 10/10 tests passed
```

**3. 经验独立化**
```
docs/experiences/
├── 2026-03-25-frontend-visual-design-confirmation.md
├── 2026-03-26-quality-assurance-four-layers.md
├── 2026-03-26-documentation-reorganization.md
└── README.md
```

**4. 快速恢复路径**
```
.claude/progress.txt
├── Current active Day
├── Key Decisions
├── Environment Status
├── Known Blockers
├── Pending Tasks
└── Recent Improvements
```

---

## 3. 文档目录的修改历程

### 初始状态 (Day 1)

```
docs/
├── api-design.md           # API 设计
├── backend-implementation.md
├── database-design.md
├── development-plan.md
├── frontend-implementation.md
├── project-kickoff.md
├── system-architecture.md
└── README.md
```

**问题**: 所有文档平铺，没有分类

### 第一次重组 (Day 2)

```
docs/
├── requirements/           # 需求文档
│   └── 01-project-overview.md
├── architecture/           # 架构设计
│   ├── system-architecture.md
│   ├── database-design.md
│   ├── backend-implementation.md
│   └── frontend-implementation.md
├── conventions/            # 编码规范
│   └── api.md
├── reports/                # 临时报告
│   └── code-review-day1-2.md
├── development-plan.md
└── README.md
```

**改进**: 按功能分类

### 第二次重组 (Day 3-4)

```
docs/
├── requirements/           # 需求文档（持久）
├── architecture/           # 架构设计（持久）
├── conventions/            # 编码规范（持久）
├── workflows/              # 工作流程（持久）← 新增
│   └── quality-assurance.md
├── decisions/              # ADR（永久）← 新增
│   └── ADR-001-quality-assurance-workflow.md
├── reports/                # 临时报告
└── README.md
```

**改进**: 增加 workflows 和 decisions 目录

### 第三次重组 (Harness 整合)

```
docs/
├── requirements/           # 需求和规划（更新频繁）
├── architecture/           # 架构设计（设计阶段确定）
├── conventions/            # 规范和标准（长期有效）
│   ├── api.md
│   └── reporting.md        ← 新增报告规范
├── workflows/              # 工作流程（长期有效）
├── decisions/              # ADR（永久记录）
│   ├── ADR-001-quality-assurance-workflow.md
│   ├── ADR-002-command-binding-principle.md  ← 新增
│   └── ADR-003-frontend-design-confirmation.md  ← 新增
├── experiences/            # 经验库（独立知识）← 新增
│   ├── 2026-03-25-frontend-visual-design-confirmation.md
│   ├── 2026-03-26-quality-assurance-four-layers.md
│   └── 2026-03-26-documentation-reorganization.md
├── reports/                # 临时执行报告
├── lessons-learned.md      # 经验总结（线性累积）
└── README.md
```

### 文档分类原则

| 分类 | 更新频率 | 保留期限 | 用途 |
|------|---------|---------|------|
| requirements | 高 | 长期 | 了解当前任务 |
| architecture | 中 | 长期 | 参考实施 |
| conventions | 低 | 长期 | 遵循规范 |
| workflows | 低 | 长期 | 团队流程 |
| decisions | 无 | 永久 | 理解"为什么" |
| experiences | 中 | 长期 | 复用知识 |
| reports | 高 | 中期 | 审查验收 |

---

## 4. Skill 的修改历程

### implement-day Skill 演进

**版本 1.0 (初始)**
```
6 个阶段:
0. Preparation
1. Plan Mode
2. Implementation
3. Code Review
4. Testing
5. Commit
6. Reporting
```

**版本 1.1 (增加文档查阅)**
```markdown
阶段 4 Testing 增加:
步骤 0: 查阅文档（必须）
├── API 文档
├── 历史测试报告
└── 数据库脚本
```

**版本 1.2 (增加真实响应验证)**
```markdown
阶段 4 Testing 强化:
步骤 2.1: 先获取真实 API 响应
步骤 2.2: 基于真实响应编写提取逻辑
步骤 2.3: 逐个 API 测试并验证
步骤 2.4: 保存测试脚本供复用
```

**版本 2.0 (增加强制前置检查)**
```markdown
阶段 0: 前置检查（必须，5秒）
├── 是否通过 skill 进入？
├── 当前工作目录正确吗？
└── 是否有未完成的前置阶段？

阶段 3: Code Review 前置检查（3秒）
└── Implementation 完成且编译通过？

阶段 4: Testing 前置检查（3秒）
└── Code Review 必须完成？

阶段 2: 前端页面强制规则
├── 步骤 A: 读取设计规范（不得跳过）
├── 步骤 B: 调用 frontend-design skill
├── 步骤 C: 严格按规范实现
└── 步骤 D: 实现完向用户展示确认
```

### Skills 体系建立

```
.claude/skills/
├── README.md                    # 技能导航
├── implement-day/
│   └── SKILL.md                 # 核心开发工作流
└── (未来可扩展)
    ├── tech-design/             # 技术方案设计
    ├── code-review/             # 代码审查
    └── api-test/                # API 测试
```

**最常用的 3 个 Skills**:
1. `implement-day` - 自动化开发工作流（Day N 实现）
2. `tech-design` - 技术方案设计（含设计确认）
3. `superpowers:code-reviewer` - 代码审查

---

## 5. 流程的修改完善

### 从无到有的质量保障流程

**Day 1 (无流程)**
```
用户需求 → 直接编码 → 手动测试 → 提交
```

**Day 2 (三层机制)**
```
Plan Mode → Implementation → Code Review → 提交
```

**Day 3+ (四层机制)**
```
Plan Mode → Implementation → Testing → Code Review → 提交
     ↓              ↓            ↓           ↓
   ADR-001      自检清单      自动化测试    Agent审查
```

**Harness 整合后 (完整机制)**
```
Plan Mode
    ↓
Implementation
    ├── 前端: 读设计规范 → 调用 frontend-design → 实现 → 确认
    └── 后端: 按顺序创建文件 → 自检 → 编译验证
    ↓
Code Review (git diff 只审查变动)
    ↓
Testing (查文档 → 获取真实响应 → 逐个测试)
    ↓
Commit (Conventional Commits 规范)
    ↓
Reporting (实施报告 + 经验更新 + CLAUDE.md 更新)
```

### 关键流程文档

| 文档 | 位置 | 用途 |
|------|------|------|
| 质量保障 | `/docs/workflows/quality-assurance.md` | 四层流程详细 |
| ADR-001 | `/docs/decisions/ADR-001-*.md` | 质量机制决策 |
| ADR-002 | `/docs/decisions/ADR-002-*.md` | 命令绑定原则 |
| ADR-003 | `/docs/decisions/ADR-003-*.md` | 设计确认流程 |
| Skill | `/.claude/skills/implement-day/SKILL.md` | 工作流详细 |
| 报告规范 | `/docs/conventions/reporting.md` | 报告格式 |

---

## 6. 踩过的坑

### 6.1 技术选型的坑

| 问题 | 损失 | 根因 | 解决方案 |
|------|------|------|---------|
| Spring Boot 3.2 + MyBatis-Plus 不兼容 | 30+ 分钟 | 没有查阅兼容性文档 | Plan Mode 强制验证 |
| JWT secret 长度不足 | 启动失败 | 使用默认值不验证 | 配置检查清单 |
| Java 版本问题反复出现 | 每次手动设置 | 临时绕过不从根源解决 | 创建 ~/.mavenrc |

### 6.2 流程执行的坑

| 问题 | 损失 | 根因 | 解决方案 |
|------|------|------|---------|
| 跳过 Plan Mode | 返工 | 看到计划就直接编码 | Skill 入口强制检查 |
| 跳过 Code Review | 遗漏问题 | 编译通过就测试 | 前置检查（3秒） |
| 凭想象写测试脚本 | 反复调试 | 不查文档不看真实响应 | 强制文档查阅步骤 |
| 目录踩踏 | 操作错误项目 | 不验证工作目录 | pwd 确认 + 自动切换 |

### 6.3 设计迭代的坑

| 问题 | 损失 | 根因 | 解决方案 |
|------|------|------|---------|
| 前端设计反复变更 | 2+ 小时 | tech-design 没有视觉确认 | ADR-003 一次性确认 |
| 使用默认 UI 框架样式 | 不符合预期 | 没有读取设计规范 | Skill 强制读取规范 |

### 6.4 工程化的坑

| 问题 | 损失 | 根因 | 解决方案 |
|------|------|------|---------|
| 命令分散难复现 | 新人不知道怎么验证 | 没有统一命令入口 | Makefile 绑定 |
| 测试报告无证据 | 无法追溯问题 | 只记录"通过/失败" | 证据链管理 |
| 经验难以复用 | 重复犯错 | 写在线性日记里 | 独立经验文件 |

---

## 7. 借鉴的思想

### 7.1 Harness Engineering 原则

**来源**: OpenAI Harness Engineering Framework

**借鉴的核心思想**:

1. **真实命令绑定**
   - 所有验证命令必须写入 Makefile
   - 不依赖口头传达或记忆
   - CI/CD 可以直接调用

2. **证据链管理**
   - 记录实际执行的命令
   - 记录返回码和执行时长
   - 完整的日志便于审计

3. **经验独立化**
   - 每个经验一个文件
   - 带时间戳和主题
   - 便于检索和复用

4. **快速恢复路径**
   - progress.txt < 1 分钟恢复上下文
   - 包含当前状态、关键决策、待办事项

### 7.2 ADR (Architecture Decision Records)

**来源**: Michael Nygard 的 ADR 模式

**借鉴的结构**:
```markdown
# ADR-XXX: 标题

**状态**: 已采纳/已废弃
**日期**: YYYY-MM-DD

## 背景 (Context)
## 决策 (Decision)
## 理由 (Rationale)
## 影响 (Consequences)
## 替代方案 (Alternatives)
```

**应用**: 创建了 3 个 ADR
- ADR-001: 四层质量保障机制
- ADR-002: 真实命令绑定原则
- ADR-003: 前端设计确认流程

### 7.3 四层质量保障

**借鉴的思想**:
- 测试驱动开发 (TDD) 的测试前置
- 代码审查的独立验证
- CI/CD 的自动化流程

**创新点**:
- 将 Testing 和 Code Review 分离
- 定义明确的触发条件
- 文档化以持久化 AI 记忆

### 7.4 Conventional Commits

**来源**: Conventional Commits 规范

**应用的格式**:
```
<type>(<scope>): <subject>

<body>
- 主要变更 1
- 主要变更 2

Test Results:
- [测试结果摘要]

Code Review:
- Score: X/10

Co-Authored-By: Claude <noreply@anthropic.com>
```

---

## 8. 值得改进的方向

### 8.1 短期改进 (Day 8-14)

| 改进项 | 当前状态 | 目标状态 | 优先级 |
|--------|---------|---------|--------|
| pre-commit hook | 无 | `make verify` 自动运行 | P1 |
| API 测试框架 | curl 脚本 | RestAssured 集成 | P1 |
| E2E 测试 | 无 | Playwright 基础用例 | P2 |
| 测试覆盖率 | 未统计 | >= 80% 强制检查 | P2 |

### 8.2 中期改进 (V2.0)

| 改进项 | 当前状态 | 目标状态 |
|--------|---------|---------|
| CI/CD 集成 | 手动 Makefile | GitHub Actions 自动化 |
| 代码质量门禁 | 可选 | SonarQube 集成 |
| 性能基准测试 | 无 | JMeter/Gatling 基准 |
| 安全扫描 | 无 | OWASP 依赖检查 |

### 8.3 长期改进 (V3.0+)

| 改进项 | 描述 |
|--------|------|
| AI Agent 协作 | 多 Agent 并行工作（设计、编码、测试） |
| 自动化重构 | 检测代码异味并自动重构 |
| 智能测试生成 | 基于代码变更自动生成测试用例 |
| 知识图谱 | 项目知识的图结构化存储 |

### 8.4 Skill 体系改进

**当前**: 只有 `implement-day` 一个完整 Skill

**计划扩展**:
```
.claude/skills/
├── implement-day/           # 核心开发工作流
├── tech-design/             # 技术方案设计
│   ├── SKILL.md
│   └── templates/
├── code-review/             # 代码审查
├── api-test/                # API 自动化测试
├── bug-fix/                 # Bug 修复工作流
└── refactor/                # 重构工作流
```

---

## 9. 最终形成的工作流

### 9.1 完整的 Day N 工作流

```
┌─────────────────────────────────────────────────────────────┐
│                    用户: "实现 Day N"                        │
└────────────────────────────┬────────────────────────────────┘
                             ↓
┌─────────────────────────────────────────────────────────────┐
│  阶段 0: Preparation (准备和验证)                            │
│  ├── 前置检查 (5秒): skill入口? 目录正确? 前置完成?          │
│  ├── 读取 CLAUDE.md 了解项目                                 │
│  ├── 提取 Day N 任务描述                                     │
│  └── 创建状态文件 .claude/state/day-N-state.json            │
└────────────────────────────┬────────────────────────────────┘
                             ↓
┌─────────────────────────────────────────────────────────────┐
│  阶段 1: Plan Mode (方案设计)                                │
│  ├── EnterPlanMode                                          │
│  ├── 调用 Plan Agent 设计方案                                │
│  │   ├── 技术栈兼容性验证                                    │
│  │   ├── 数据库表结构设计                                    │
│  │   ├── API 接口清单                                        │
│  │   └── 验收标准                                            │
│  ├── 如果是前端任务: 视觉设计确认 (ADR-003)                  │
│  └── ExitPlanMode                                           │
└────────────────────────────┬────────────────────────────────┘
                             ↓
┌─────────────────────────────────────────────────────────────┐
│  阶段 2: Implementation (编码实现)                           │
│  ├── 后端: 枚举 → 实体 → DTO → Mapper → Service → Controller │
│  ├── 前端: 读设计规范 → frontend-design → 实现 → 用户确认   │
│  ├── 自检清单: 命名规范? 注解完整? 依赖注入?                 │
│  └── 编译验证: mvn compile / npm run build                  │
└────────────────────────────┬────────────────────────────────┘
                             ↓
┌─────────────────────────────────────────────────────────────┐
│  阶段 3: Code Review (代码审查)                              │
│  ├── 前置检查 (3秒): Implementation 完成? 编译通过?          │
│  ├── git diff 获取变动范围（优化 token）                     │
│  ├── 调用 code-reviewer Agent                               │
│  │   ├── 代码 vs Plan 一致性                                 │
│  │   ├── 实体 vs 数据库一致性                                │
│  │   └── 代码规范检查                                        │
│  └── Critical/Major → 返回阶段 2 修复                        │
└────────────────────────────┬────────────────────────────────┘
                             ↓
┌─────────────────────────────────────────────────────────────┐
│  阶段 4: Testing (自动化测试)                                │
│  ├── 前置检查 (3秒): Code Review 完成? ← 最容易跳过          │
│  ├── 查阅文档: API文档 + 历史测试 + 数据库脚本               │
│  ├── 单元测试: mvn test / npm test                          │
│  ├── API 测试: 先获取真实响应 → 再写提取逻辑 → 逐个验证      │
│  ├── 记录证据链: 命令 + 返回码 + 时长 + 输出摘要             │
│  └── 生成测试报告 docs/reports/day-N-test-results.md        │
└────────────────────────────┬────────────────────────────────┘
                             ↓
┌─────────────────────────────────────────────────────────────┐
│  阶段 5: Commit (代码提交)                                   │
│  ├── git status 检查变更                                    │
│  ├── 生成 Conventional Commit message                       │
│  ├── git add + git commit                                   │
│  └── git log -1 验证                                        │
└────────────────────────────┬────────────────────────────────┘
                             ↓
┌─────────────────────────────────────────────────────────────┐
│  阶段 6: Reporting (生成报告)                                │
│  ├── 创建实施报告 docs/reports/day-N-implementation.md      │
│  ├── 更新经验总结 docs/lessons-learned.md                   │
│  ├── 更新项目地图 CLAUDE.md                                  │
│  └── 汇总输出给用户                                          │
└────────────────────────────┬────────────────────────────────┘
                             ↓
┌─────────────────────────────────────────────────────────────┐
│  ✅ Day N 实施完成！                                         │
│  📊 统计: 新增文件 X 个, 测试通过率 Y%, 代码评分 Z/10        │
│  📄 文档: 方案/测试/审查/实施 报告                           │
│  💾 Commit: abc1234                                         │
│  ✨ 下一步: Day N+1                                          │
└─────────────────────────────────────────────────────────────┘
```

### 9.2 快速验证命令

```bash
# 完整验证
make verify

# 后端验证
make backend-verify    # compile + test + checkstyle

# 前端验证
make frontend-verify   # lint + test + build

# 环境检查
make check-env

# 查看帮助
make help
```

---

## 10. 核心经验总结

### 10.1 AI 编程的 8 条核心原则

1. **流程优于天赋**
   > 好的流程能让普通 AI 产出优质代码，坏的流程会让优秀 AI 也要返工。

2. **文档优于记忆**
   > AI 的"记忆"只能靠文档持久化。口头规则在下一个会话就会被遗忘。

3. **前置优于事后**
   > Plan Mode 阶段 20 分钟的验证，比实施阶段 2 小时的返工更划算。

4. **自动优于手动**
   > 能自动化的检查项绝不手动，能被 Skill 执行的流程绝不口头。

5. **证据优于宣称**
   > 不是"我测试通过了"，而是"返回码 0，执行时长 45 秒，8/8 测试通过"。

6. **强制优于自觉**
   > 靠 AI "自觉"遵守流程永远不可靠，必须有强制检查点。

7. **独立优于线性**
   > 经验要独立成文件，不要写在线性日记里，便于检索和复用。

8. **设计优于迭代**
   > 前端视觉设计在 tech-design 阶段一次确定，不在 implement-day 反复调整。

### 10.2 关键数据统计

| 指标 | 数值 |
|------|------|
| 完成天数 | Day 1-7 (7 天) |
| 后端代码行数 | ~3,000 行 |
| 前端代码行数 | ~4,700 行 |
| API 端点 | 25+ 个 |
| 数据库表 | 5 张 |
| 文档页数 | 86 页 |
| Git 提交 | 20+ 个 |
| ADR 文档 | 3 个 |
| Skill 版本 | 2.0 |

### 10.3 效率改进数据

| 阶段 | 改进内容 | ROI |
|------|---------|-----|
| Day 2 → Day 3 | 四层质量保障 | 减少返工 30+ 分钟 |
| Day 4 | Skill 流程优化 | 前置检查 20 秒避免 30 分钟返工 |
| Day 5 | 设计确认机制 | 减少设计迭代 50%+ |
| Day 6-7 | 真实命令绑定 | 减少环境问题 100% |

### 10.4 推荐的 AI 项目启动清单

- [ ] 建立分层文档结构 (requirements, architecture, conventions, workflows, decisions, experiences)
- [ ] 定义质量保障工作流 (Plan → Implementation → Code Review → Testing)
- [ ] 创建 Makefile 统一命令
- [ ] 编写 Skill 工作流指导 (implement-day, tech-design 等)
- [ ] 建立 ADR 文档记录关键决策
- [ ] 设置 progress.txt 支持快速恢复
- [ ] 创建经验库 (docs/experiences/) 持续沉淀

---

## 附录: 关键文档清单

| 文档 | 位置 | 用途 |
|------|------|------|
| 项目地图 | `/CLAUDE.md` | AI 快速定位任何文件 |
| 质量保障 | `/docs/workflows/quality-assurance.md` | 四层流程详细 |
| ADR 系列 | `/docs/decisions/ADR-001~003.md` | 关键决策记录 |
| 经验库 | `/docs/experiences/` | 可复用知识沉淀 |
| 进度追踪 | `/.claude/progress.txt` | 快速恢复上下文 |
| Skills 导航 | `/.claude/skills/README.md` | 自动化工作流索引 |
| 报告规范 | `/docs/conventions/reporting.md` | 统一报告格式 |
| 开发计划 | `/docs/development-plan.md` | Day 1-7 任务分解 |
| 经验总结 | `/docs/lessons-learned.md` | 跨阶段教训积累 |

---

**文档版本**: 1.0
**创建时间**: 2026-04-13
**作者**: Claude Opus 4.5 + 用户协作

---

> "从混乱到有序的演进路径: 无序 → 质量危机 → 四层机制 → Skill 优化 → 设计确认 → 工程化完善"
>
> 核心不在于 AI 技能本身，而在于**系统、流程、文档和验证机制**的建立与完善。
