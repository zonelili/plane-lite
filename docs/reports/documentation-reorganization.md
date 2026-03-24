# 文档重组记录 - 2026-03-24

## 问题描述

**发现时间**：2026-03-24
**发现人**：用户反馈
**问题严重程度**：MEDIUM（影响项目可维护性）

### 主要问题

1. **文档分类混乱**
   - `CODE_REVIEW_DAY1-2.md` 和 `JAVA_VERSION_FIX.md` 直接放在 `/docs` 根目录
   - 没有按照功能分类到对应的子目录
   - 文档命名不规范（使用全大写）

2. **CLAUDE.md 索引缺失**
   - 添加了新文档但没有更新 CLAUDE.md 的"架构地图"
   - AI 无法通过 CLAUDE.md 快速找到新文档
   - 违反了 Harness 的"渐进式披露"原则

3. **文档定位不清晰**
   - 没有区分"持久文档"和"临时文档"
   - 缺少文档分类规范
   - 容易导致文档堆积和混乱

## 根本原因分析

### 为什么会出现这个问题？

1. **没有遵循 Harness 原则**
   - 忘记了"CLAUDE.md 作为地图，不堆砌细节"的原则
   - 没有维护文档索引的习惯

2. **缺少文档分类规范**
   - 项目初期没有定义文档分类标准
   - 不清楚哪些文档应该放在哪里

3. **临时文档和持久文档混淆**
   - 代码审查报告是临时文档，但放在了根目录
   - 没有考虑文档的生命周期

## 修复过程

### 步骤 1：重组文档目录结构

**创建 reports/ 目录**：
```bash
mkdir -p docs/reports
```

**移动文档到正确位置**：
```bash
mv docs/CODE_REVIEW_DAY1-2.md docs/reports/code-review-day1-2.md
mv docs/JAVA_VERSION_FIX.md docs/reports/java-version-fix.md
```

**修复后的目录结构**：
```
docs/
├── requirements/          # 📋 需求文档（持久）
├── architecture/          # 🏗️ 架构设计（持久）
├── conventions/           # 📖 编码规范（持久）
├── workflows/             # 🔄 工作流程（持久）
├── decisions/             # 🎯 架构决策记录（持久）
├── reports/               # 📊 报告和分析（临时）✨ 新增
│   ├── code-review-day1-2.md
│   └── java-version-fix.md
├── development-plan.md    # 📅 开发计划（临时）
└── lessons-learned.md     # 💡 经验总结（持久累积）
```

### 步骤 2：创建文档分类规范

在 `docs/README.md` 中添加：

#### 持久文档（Persistent Documents）
- 定义：项目长期维护的文档，需要持续更新
- 目录：requirements/, architecture/, conventions/, workflows/, decisions/
- 命名：使用小写字母和连字符（`database-design.md`）

#### 临时文档（Temporary Documents）
- 定义：项目特定阶段的文档，项目完成后归档或删除
- 目录：reports/, development-plan.md
- 生命周期：完成后归档到 `archive/` 或合并到 `lessons-learned.md`

#### 文档创建规则
在创建新文档前，必须问自己：
1. 这是持久文档还是临时文档？
2. 这个内容是否可以合并到现有文档？
3. 文档命名是否符合规范？
4. CLAUDE.md 是否需要更新索引？

### 步骤 3：更新 CLAUDE.md 索引

**修复前**：
```markdown
### 📚 核心文档
- `/docs/requirements/` - 需求文档和版本规划
- `/docs/architecture/` - 系统架构和技术选型
- `/docs/conventions/` - 编码规范和最佳实践
- `/docs/workflows/` - 常见开发任务流程
- `/docs/decisions/` - 架构决策记录（ADR）
```

**修复后**：
```markdown
### 📚 核心文档
- `/docs/requirements/` - 需求文档和版本规划
- `/docs/architecture/` - 系统架构和技术选型
- `/docs/conventions/` - 编码规范和最佳实践
- `/docs/workflows/` - 开发工作流程（质量保障、Git 流程等）
- `/docs/decisions/` - 架构决策记录（ADR）
- `/docs/reports/` - 代码审查报告和问题修复记录（临时）✨ 新增
- `/docs/development-plan.md` - 开发计划（Day 1-7 任务分解）
- `/docs/lessons-learned.md` - 项目经验总结（持续累积）

**📖 详细索引**：见 `/docs/README.md`
```

### 步骤 4：更新项目状态

在 CLAUDE.md 中添加：
- 当前完成的工作（Day 1-2）
- 已知的问题（JWT 安全性、密码存储）
- 下一步计划（Day 3）

## 修复结果

### 文档结构对比

| 修复前 | 修复后 |
|--------|--------|
| ❌ 文档分散在根目录 | ✅ 按功能分类到子目录 |
| ❌ CLAUDE.md 索引过时 | ✅ CLAUDE.md 索引完整 |
| ❌ 没有分类规范 | ✅ 定义了持久/临时文档规范 |
| ❌ 命名不规范（全大写） | ✅ 统一使用小写字母和连字符 |

### 验证结果

✅ **文档结构清晰**：
```bash
$ tree docs -L 2
docs/
├── reports/              # ✅ 新增临时文档目录
│   ├── code-review-day1-2.md
│   └── java-version-fix.md
├── workflows/
│   └── quality-assurance.md
├── decisions/
│   └── ADR-001-quality-assurance-workflow.md
└── README.md            # ✅ 包含完整的分类规范
```

✅ **CLAUDE.md 索引完整**：
- 列出了所有核心文档目录
- 指向 `/docs/README.md` 获取详细索引
- 更新了项目状态

✅ **docs/README.md 索引完整**：
- 包含完整的目录结构
- 定义了文档分类规范
- 列出了所有文档的状态和更新时间

## 经验总结

### 关键教训

#### 1. Harness 原则必须严格遵守

**CLAUDE.md 作为"地图"，不堆砌细节**
- ❌ 错误：添加新文档后不更新 CLAUDE.md
- ✅ 正确：每次添加新目录或重要文档时立即更新 CLAUDE.md

**渐进式披露原则**
- ❌ 错误：把所有内容都写在 CLAUDE.md 中
- ✅ 正确：CLAUDE.md 只列出核心目录，详细索引在 `/docs/README.md`

#### 2. 文档分类必须清晰

**持久文档 vs 临时文档**
- 持久文档：长期维护，需要持续更新（requirements, architecture, conventions）
- 临时文档：特定阶段，完成后归档（reports, development-plan）

**明确的目录结构**
- 按功能分类：requirements, architecture, conventions, workflows, decisions
- 临时文档独立目录：reports
- 避免在根目录堆积文档

#### 3. 文档命名必须规范

**统一的命名规则**
- ✅ 小写字母 + 连字符：`code-review-day1-2.md`
- ❌ 全大写：`CODE_REVIEW_DAY1-2.md`
- ✅ 有序文档加编号：`01-project-overview.md`
- ✅ ADR 标准格式：`ADR-001-quality-assurance-workflow.md`

### 可复用模式

**文档创建检查清单**：
```markdown
在创建新文档前，检查：
- [ ] 这是持久文档还是临时文档？
- [ ] 应该放在哪个目录？
- [ ] 文档命名是否符合规范？
- [ ] 是否可以合并到现有文档？
- [ ] CLAUDE.md 是否需要更新？
- [ ] docs/README.md 是否需要更新？
```

**文档索引维护规则**：
```markdown
当执行以下操作时，必须更新索引：
1. 创建新的文档目录 → 更新 CLAUDE.md
2. 添加重要的持久文档 → 更新 docs/README.md
3. 移动或重命名文档 → 更新所有引用
4. 归档临时文档 → 从索引中移除
```

## 预防措施

### 1. 建立文档审查机制

在质量保障流程（Layer 3: Code Review）中添加文档审查：
- [ ] 新文档是否按规范分类？
- [ ] CLAUDE.md 索引是否更新？
- [ ] docs/README.md 是否更新？
- [ ] 文档命名是否规范？

### 2. 创建文档模板

为常见文档类型创建模板：
- `templates/code-review-template.md` - 代码审查报告模板
- `templates/adr-template.md` - 架构决策记录模板
- `templates/issue-fix-template.md` - 问题修复报告模板

### 3. 添加自动化检查

创建脚本验证文档结构：
```bash
#!/bin/bash
# scripts/check-docs.sh

# 检查是否有文件在 docs/ 根目录（除了 README.md 等）
INVALID_FILES=$(find docs -maxdepth 1 -type f -name "*.md" \
  ! -name "README.md" \
  ! -name "development-plan.md" \
  ! -name "lessons-learned.md" \
  ! -name "project-kickoff.md")

if [ -n "$INVALID_FILES" ]; then
  echo "错误：以下文件应该移动到子目录："
  echo "$INVALID_FILES"
  exit 1
fi

echo "文档结构检查通过"
```

## 改进建议

### 短期改进（立即执行）

1. ✅ 重组文档目录结构
2. ✅ 更新 CLAUDE.md 索引
3. ✅ 创建文档分类规范
4. [ ] 在 lessons-learned.md 中记录这次教训

### 中期改进（本周完成）

5. [ ] 创建文档模板
6. [ ] 添加文档结构自动化检查脚本
7. [ ] 在质量保障流程中添加文档审查步骤

### 长期改进（V2.0+）

8. [ ] 使用 MkDocs 或 Docusaurus 生成在线文档
9. [ ] 集成文档 Lint 工具检查格式
10. [ ] 添加文档版本管理

## 相关文档

- [质量保障流程](./workflows/quality-assurance.md)
- [ADR-001: 质量保障工作流](./decisions/ADR-001-quality-assurance-workflow.md)
- [经验总结](./lessons-learned.md)
- [文档索引](./README.md)

---

**修复状态**：✅ 已完成
**验证状态**：✅ 已验证
**文档状态**：✅ 已记录

**修复人员**：Claude Opus 4.6
**修复日期**：2026-03-24
