# Skills 导航和索引

> Plane Lite 项目专用的自动化工作流 skills
> 这些 skills 是为 Plane Lite 的特定需求定制的

## 快速开始

### 最常用的 3 个 Skills

1. **implement-day** - 自动化开发工作流（Day N 实现）
   ```bash
   # 用法：当用户说"实现 Day 6"时自动触发
   # 流程：Plan Mode → Implementation → Code Review → Testing → Commit → Reporting
   # 文件：.claude/skills/implement-day/SKILL.md
   ```

2. **tech-design** - 技术方案设计
   ```bash
   # 用法：设计新功能的技术方案（Day N 前置）
   # 输出：技术方案文档（架构、数据库、接口、前端设计规范）
   # 文件：skills/tech-design/SKILL.md
   ```

3. **harness-workflow** - Harness 工程检查
   ```bash
   # 用法：质量卡点检查、流程验证
   # 文件：skills/harness-workflow/README.md
   ```

## 完整 Skills 列表

### 核心开发流程

| Skill | 用途 | 触发条件 | 输出 |
|-------|------|---------|------|
| `implement-day` | 自动化 Day N 全流程 | "实现 Day X" / "开始 Day X" | 代码 + 报告 + 经验 |
| `tech-design` | 技术方案设计 | 需要设计技术方案 | 方案文档 + 前端规范 |
| `java-dev` | Java 开发规范检查 | 写 Java 代码时 | 规范指导 + 代码审查 |
| `sql-standard` | 数据库设计规范 | 设计数据库时 | 规范检查 + DDL 脚本 |
| `data-arch-designer` | 数据架构设计 | 设计数据模型时 | ER 图 + SQL 语句 |

### 前端开发

| Skill | 用途 | 触发条件 | 输出 |
|-------|------|---------|------|
| `document-skills:frontend-design` | 前端页面设计和实现 | 实现前端页面 | 设计代码 + 样式 |

### 文档和交付

| Skill | 用途 | 触发条件 | 输出 |
|-------|------|---------|------|
| `document-skills:doc-coauthoring` | 文档编写协作 | 编写技术文档 | Markdown 文档 |
| `document-skills:xlsx` | Excel 相关操作 | 处理电子表格 | Excel 文件 |
| `document-skills:pdf` | PDF 相关操作 | 处理 PDF 文件 | PDF 文件 |

### 质量和发布

| Skill | 用途 | 触发条件 | 输出 |
|-------|------|---------|------|
| `harness-workflow` | 工程质量检查 | 需要流程验证 | 检查报告 |
| `skill-creator` | Skill 开发和维护 | 修改或创建 skill | 新 skill 或 skill 更新 |

### 研究和探索

| Skill | 用途 | 触发条件 | 输出 |
|-------|------|---------|------|
| `autoresearch` | 自动迭代研究 | 需要探索和验证 | 研究结果 + 实现方案 |

## Skill 工作流集成

### 标准 Day N 开发流程

```
Day N 任务开始
  ↓
tech-design（设计技术方案）
  ├─ 架构设计
  ├─ 数据库设计（data-arch-designer）
  ├─ 接口设计
  └─ 前端设计规范（如有前端任务）
  ↓
implement-day（自动化实现）
  ├─ Plan Mode（进入设计文档审视）
  ├─ Implementation（编码实现）
  │  ├─ 后端：加载 java-dev 规范
  │  ├─ 数据库：加载 sql-standard 规范
  │  └─ 前端：加载 frontend-design
  ├─ Code Review（代码审查）
  ├─ Testing（自动化测试）
  ├─ Commit（代码提交）
  └─ Reporting（生成报告）
  ↓
Day N 完成 ✅
  ↓
经验沉淀（docs/experiences/）
```

## 关键改进（2026-03-26）

### 1. 真实命令绑定
所有 skills 现在都使用真实的命令：
```bash
# 后端验证
make backend-verify    # mvn clean compile + mvn test + checkstyle

# 前端验证
make frontend-verify   # eslint + test + build

# 完整验证
make verify           # 后端 + 前端全部检查
```

### 2. 证据链管理
每个 skill 执行后都必须记录：
- 实际执行的命令
- 返回码
- 执行时间
- 输出摘要

### 3. 经验沉淀
可复用的经验现在存放在 `docs/experiences/` 独立文件中：
```
docs/experiences/
├── README.md                    # 索引
└── 2026-03-25-*.md             # 独立经验文档
```

## 配置和自定义

### 添加新 Skill

1. 复制已有 skill 的目录结构到 `skills/new-skill/`
2. 创建 `skills/new-skill/SKILL.md`
3. 定义 `description`、工作流程、触发条件
4. 在本 README 中添加引用

### 修改 Skill 行为

参考相关 skill 的 `SKILL.md` 文件，按需修改：
- 工作流程步骤
- 触发条件
- 输出格式
- 参数定义

## 相关文档

- **项目地图**：`CLAUDE.md`
- **开发计划**：`docs/development-plan.md`
- **质量保障**：`docs/workflows/quality-assurance.md`
- **API 规范**：`docs/conventions/api.md`
- **报告规范**：`docs/conventions/reporting.md`（待创建）
- **经验库**：`docs/experiences/`

## 版本历史

| 日期 | 变更 |
|------|------|
| 2026-03-26 | 创建 skills 导航，整合改进后的 implement-day 和 tech-design |
| 2026-03-25 | 优化 tech-design：新增前端视觉设计确认 |
| 2026-03-24 | 优化 implement-day：新增代码审查和测试阶段细化 |
| 2026-03-23 | 初始化 implement-day skill |

## 常见问题

### Q: 我应该用哪个 skill？

**A:** 看任务类型：
- Day N 全流程：`implement-day`
- 仅设计方案：`tech-design`
- 仅写 Java：`java-dev`
- 仅设计数据库：`data-arch-designer`
- 仅写前端：`document-skills:frontend-design`

### Q: Skill 之间的顺序是什么？

**A:** 通常是这样：
1. `tech-design` 先生成方案
2. `implement-day` 再按方案实现
3. 其他 skills 在需要时被 implement-day 调用

### Q: 如何验证 skill 运行是否成功？

**A:** 查看：
1. 是否返回了文件或代码
2. 是否记录了执行命令和返回码
3. 是否生成了对应的报告
4. 是否更新了 `.claude/progress.txt`

## 联系和反馈

- **问题报告**：检查 `docs/decisions/ADR-*.md`
- **改进建议**：创建 `docs/experiences/` 中的新经验文档
- **Skill 修改**：参考 `skill-creator` 和相关 skill 的 `SKILL.md`
