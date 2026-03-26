---
name: implement-day
description: >
  自动化执行开发任务的完整工作流,支持任何技术栈(Java/Node.js/Python/Go等)。
  触发时机:当用户说"实现 Day X"、"开始 Day X"、"继续 Day X"、"开始开发 Day X 的功能"时使用。
  完整流程: Plan Mode → Implementation → Code Review → Testing → Commit → Reporting。
  确保在每个开发任务开始时自动触发,执行完整的质量保障流程。
---

# Implement Day - 自动化开发工作流

## 触发时机

当用户说以下内容时,**必须**使用此 skill:
- "实现 Day X" / "开始 Day X" / "implement Day X"
- "继续 Day X" / "完成 Day X"
- "开始开发 Day X 的功能"

**不应触发**:
- "Day X 的计划是什么?" → 这是查询,不是执行
- "给我看看 Day X 要做什么" → 这是查询
- "我想修改 Day X 的某个 bug" → 单个 bug 修复

---

## 工作流概述

6 个阶段的自动化开发流程:

```
0. Preparation (准备和验证)
   ↓
1. Plan Mode (方案设计与验证)
   ↓
2. Implementation (编码实现)
   ↓
3. Code Review (代码审查) ← 先审查代码质量
   ↓
4. Testing (自动化测试) ← 再测试功能
   ↓
5. Commit (代码提交)
   ↓
6. Reporting (生成报告和文档更新)
```

**核心原则**:
- 任何阶段失败,立即停止流程
- 清晰报告错误,提供修复建议
- 严格执行质量保障机制

---

## 配置和路径

### 从项目中读取配置

在阶段 0 中,从以下文件读取项目配置:
1. **CLAUDE.md** - 项目地图,了解:
   - 项目结构
   - 技术栈
   - 开发规范
   - 文件路径约定

2. **docs/requirements/development-plan.md** 或 **docs/development-plan.md** - 开发计划,提取 Day N 任务

3. **docs/workflows/** - 质量保障流程(如果存在)

### 默认路径模式

如果项目没有明确配置,使用这些常见模式:
```
技术方案: .claude/plans/day-N-plan.md
状态文件: .claude/state/day-N-state.json
测试报告: docs/reports/day-N-test-results.md
审查报告: docs/reports/day-N-code-review.md
实施报告: docs/reports/day-N-implementation.md
经验总结: docs/lessons-learned.md
```

---

## 阶段 0: 准备和验证

### 目标
确认环境和计划文件存在,提取 Day N 任务描述。

### 🚨 前置检查（必须，5秒）

**在开始任何工作前，必须回答这 3 个问题：**

1. **我是通过 implement-day skill 进入的吗？**
   - 如果你是直接看到用户的计划就开始写代码 → ❌ 错误！
   - 应该通过 Skill 工具调用 implement-day
   - **如果发现自己跳过了 skill → 立即停止，报告错误**

2. **当前工作目录正确吗？**
   - 执行 `pwd` 确认
   - 如果在错误的目录 → 立即切换

3. **是否有未完成的前置阶段？**
   - 检查 `.claude/state/day-N-state.json` 是否存在
   - 如果存在且 `current_stage` 不是 "completed" → 从断点恢复

**时间成本**：< 5 秒

### 步骤

1. **解析用户输入**
   - 提取 Day 编号: "实现 Day 3" → `day_number = 3`
   - 识别模式: "继续" → `resume_mode = true`

2. **检查断点恢复**
   ```bash
   if [ -f .claude/state/day-${day_number}-state.json ]; then
     # 读取状态文件,从 current_stage 继续
   fi
   ```

3. **验证项目环境**
   - 确认工作目录存在
   - 读取 `CLAUDE.md` 了解项目结构
   - 找到开发计划文件(可能在 `docs/development-plan.md` 或 `docs/requirements/development-plan.md`)

4. **提取 Day N 任务**
   - 查找标题: `## Day N:` 或 `### Day N:`
   - 提取: 任务标题、功能需求、技术要点
   - 如果找不到,列出所有可用的 Day

5. **创建状态文件**
   ```json
   {
     "day_number": 3,
     "current_stage": "preparation",
     "completed_stages": [],
     "started_at": "2026-03-24T10:00:00Z"
   }
   ```
   保存到 `.claude/state/day-N-state.json`

### 成功标准
- ✅ 项目环境验证通过
- ✅ 提取到 Day N 任务描述
- ✅ 状态文件创建成功

### 错误处理
任何检查失败时,报告失败原因和修复建议,不继续后续阶段。

---

## 阶段 1: Plan Mode (方案设计)

### 目标
使用 Plan Agent 设计和验证技术方案。

### 步骤

1. **进入 Plan Mode**
   ```
   调用 EnterPlanMode 工具
   ```

2. **启动 Plan Agent**
   调用 Agent 工具:
   - `subagent_type`: "Plan"
   - `description`: "设计 Day N 实施方案"
   - `prompt`: 包含以下内容
     - 项目背景(从 CLAUDE.md 提取)
     - 技术栈信息
     - Day N 任务描述
     - 要求输出:
       * 技术栈兼容性验证
       * 数据库表结构设计(如需要)
       * API 接口清单
       * 文件创建清单(按顺序)
       * 验收标准

3. **验证方案文件**
   检查 `.claude/plans/day-N-plan.md` 包含必需章节:
   - 技术要点
   - 文件创建清单
   - 验收标准

4. **退出 Plan Mode**
   ```
   调用 ExitPlanMode 工具
   ```

5. **更新状态文件**
   ```json
   {
     "current_stage": "plan",
     "completed_stages": ["preparation"],
     "plan_file": ".claude/plans/day-3-plan.md"
   }
   ```

### 成功标准
- ✅ 方案文件生成完整
- ✅ 包含所有必需章节

**详细说明**: 参见 `references/stage1-plan-mode.md`

---

## 阶段 2: Implementation (编码实现)

### 目标
按照 Plan 实现代码,确保编译/语法检查通过。

### 步骤

1. **读取方案文件**
   从 `.claude/plans/day-N-plan.md` 提取文件创建清单

1.5. **🎨 前端页面强制规则（Vue/React/前端任务必须执行）**

   如果当前 Day 任务包含任何 **页面、组件、视图（View）** 的开发（即技术栈为 Vue/React/前端），**必须**按以下流程执行：

   **步骤 A：读取已定的设计规范（不得跳过）**

   从技术方案文档（`.claude/plans/` 或 `docs/architecture/` 下）找「前端设计规范」章节，提取：
   - 风格方向、主色、高亮色、背景色
   - 字体规范
   - 按钮/输入框/错误态交互规范

   如果找不到设计规范 → **停止实现，提示用户先用 `tech-design` skill 补充前端设计规范后再继续。**

   **步骤 B：调用 frontend-design skill**

   ```
   调用 Skill 工具:
   - skill: "document-skills:frontend-design"
   ```

   调用时将已确定的设计规范作为约束传入，不得自行重新决定风格。

   **步骤 C：实现页面代码**

   严格按设计规范实现，禁止使用 UI 框架默认样式直出页面。

   **步骤 D：实现完成后向用户展示效果确认（必须）**

   实现完每个页面后，向用户说明：
   - 当前页面遵循的设计规范要点
   - 需要用户打开浏览器查看效果
   - 明确询问：「视觉效果是否符合预期？有需要调整的地方吗？」
   - **等待用户确认后再继续下一个页面**

   **触发条件**（满足任意一条即触发）：
   - 任务描述中包含：页面、视图、组件、UI、界面、列表页、详情页、表单
   - 文件路径包含：`views/`、`components/`、`pages/`
   - 技术栈为 Vue / React / 纯前端

   **不触发条件**：
   - 纯后端任务（Java/Go/Python Service、Controller、Mapper 等）
   - 仅修改配置文件、路由、Store（无视觉组件）

2. **按顺序创建文件**
   严格按照 Plan 中的顺序:
   - 顺序 1: 枚举类和常量
   - 顺序 2: 实体类/模型类
   - 顺序 3: DTO/VO
   - 顺序 4: Mapper/Repository
   - 顺序 5: Service
   - 顺序 6: Controller/Handler
   - 顺序 7: 单元测试

3. **自检清单**
   每创建一批文件后,对照项目规范检查:
   - 命名规范正确?
   - 注解完整?
   - 依赖注入正确?

4. **编译/语法验证**
   根据技术栈运行:
   - Java: `mvn clean compile`
   - Node.js: `npm run build` 或 `tsc`
   - Python: `pylint` 或 `mypy`
   - Go: `go build`

   如果失败,分析错误,修复,重新验证(最多 3 次)

5. **更新状态文件**
   ```json
   {
     "current_stage": "implementation",
     "completed_stages": ["preparation", "plan"]
   }
   ```

### 成功标准
- ✅ 所有文件创建完成
- ✅ 编译/语法检查通过

**详细说明**: 参见 `references/stage2-implementation.md`

---

## 阶段 3: Code Review (代码审查)

### 目标
使用 Code Review Agent 审查代码质量,确保符合规范。

### 🚨 前置检查（必须，3秒）

**在开始 Code Review 前，必须确认：**

1. **Implementation 阶段是否完成？**
   - 检查状态文件：`current_stage` 必须是 "implementation"
   - 如果不是 → 报错："Code Review 前必须先完成 Implementation"

2. **编译是否通过？**
   - 如果编译失败 → 先修复编译错误再 Code Review

**如果检查失败 → 立即停止，不允许继续**

**时间成本**：< 3 秒

### 步骤

1. **获取代码变动范围**（优化 token 消耗）
   ```bash
   # 如果已有 commit，对比上次 commit
   git diff HEAD~1 HEAD --stat

   # 如果未 commit，查看工作区变动
   git status --short
   git diff HEAD --stat
   ```

   **原则**：只审查变动部分，不读取全部文件
   - 新增模块：所有文件都是新的，需要全部审查
   - 修改已有代码：只审查变动的文件和行，大幅节省 token

2. **调用 Code Review Agent**
   ```
   调用 Agent 工具:
   - subagent_type: "superpowers:code-reviewer"
   - 传递变动文件列表和 git diff 内容
   - 而不是传递所有文件路径
   ```

3. **Code Review Agent 检查**
   - 代码与 Plan 的一致性
   - 实体类与数据库表的一致性(如适用)
   - 代码规范和最佳实践
   - 配置文件完整性

4. **处理审查结果**
   审查报告问题分级:
   - **Critical/Major**: 必须修复,返回阶段 2
   - **Minor/Trivial**: 记录到报告,可以继续

5. **更新状态文件**
   ```json
   {
     "current_stage": "review",
     "completed_stages": ["preparation", "plan", "implementation"],
     "review_report": "docs/reports/day-3-code-review.md"
   }
   ```

### 成功标准
- ✅ Code Review 完成
- ✅ 无 Critical 或 Major 问题

### 错误处理
如果有 Critical/Major 问题:
1. 报告问题详情
2. 返回阶段 2 修复
3. 重新编译验证
4. 重新 Code Review

**详细说明**: 参见 `references/stage3-code-review.md`

---

## 阶段 4: Testing (自动化测试)

### 目标
执行自动化测试,验证功能正确性。

### 🚨 前置检查（必须，3秒）

**在开始测试前，必须确认：**

1. **Code Review 是否完成？**
   - 检查状态文件：`current_stage` 必须是 "review"
   - 如果不是 → 报错："❌ 测试前必须先完成 Code Review！"
   - **这是最容易被跳过的检查，务必执行！**

2. **Code Review 是否有 Critical 问题？**
   - 如果有未修复的 Critical 问题 → 先修复再测试

**如果检查失败 → 立即停止，不允许跳过 Code Review 直接测试**

**时间成本**：< 3 秒

### 步骤

**⚠️ 重要：在编写测试脚本前，必须先查阅项目文档！**

0. **查阅测试所需文档**（必须步骤）
   在编写任何测试脚本前，依次查找并阅读：

   a. **API 文档**（必读）
      - 路径：`docs/conventions/api.md` 或 `docs/architecture/backend-implementation.md`
      - 关键信息：
        - 认证方式（JWT token 格式、登录接口）
        - 请求示例（完整的 Request Body）
        - 响应格式（统一响应结构）
      - **禁止凭记忆或猜测 API 格式！**

   b. **历史测试报告**（优先复用）
      - 路径：`docs/reports/day-{N-1}-test-results.md`（上一个 Day 的测试）
      - 目的：复用已验证的测试逻辑（如登录、创建测试数据）
      - 如果 Day 1/2/3 已有测试，Day 4 应该复用它们的登录逻辑

   c. **数据库初始化脚本**（如需测试用户）
      - 路径：`*/db/migration/V1__init.sql` 或类似
      - 查看：测试用户的 email/username 和密码

   **检查清单**：
   - ✅ 已读取 API 文档，知道登录接口的准确格式
   - ✅ 已查看历史测试报告，可以复用测试逻辑
   - ✅ 已确认测试用户的凭据（或计划注册新用户）

1. **单元测试**
   根据技术栈运行:
   - Java: `mvn clean test`
   - Node.js: `npm test`
   - Python: `pytest`
   - Go: `go test ./...`

   **记录执行的命令和结果**：
   ```bash
   # 示例：执行 Java 单元测试
   Command executed: mvn clean test
   Return code: 0
   Summary: 10/10 tests passed, 0 failures
   Duration: 45s
   ```

   验收标准: 所有测试通过 (return code = 0)

2. **集成/API 测试**(如果项目有)
   - 查找测试脚本: `tests/day-N-api-test.sh` 或类似
   - 如果没有,按以下步骤创建测试脚本：

   **⚠️ 编写测试脚本的正确流程（必须遵守）**：

   **步骤 2.1：先获取真实 API 响应**
   ```bash
   # 不要凭想象写提取逻辑！先手动调用 API
   curl -s -X POST "http://localhost:8080/api/v1/auth/login" \
     -H "Content-Type: application/json" \
     -d '{"email":"test@example.com","password":"password"}' | jq .

   # 或者不用 jq，直接查看原始 JSON
   curl -s ... > /tmp/response.json
   cat /tmp/response.json
   ```

   **步骤 2.2：基于真实响应编写提取逻辑**
   ```bash
   # 看到真实响应：{"code":200,"data":{"token":"xxx"}}
   # 然后写提取逻辑
   RESPONSE=$(cat /tmp/response.json)
   TOKEN=$(echo "$RESPONSE" | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

   # 立即验证
   echo "Extracted token: ${TOKEN:0:30}..."
   ```

   **步骤 2.3：逐个 API 测试并验证**
   - 一次只写一个 API 调用
   - 立即执行并验证响应
   - 确认提取逻辑正确后，再写下一个

   **步骤 2.4：保存测试脚本**
   - 路径：`tests/day-N-test.sh` 或项目约定路径
   - 脚本必须包含完整测试流程
   - 必须能在干净环境独立运行
   - **Day N+1 必须能复用此脚本的逻辑**

   **反模式（禁止）**：
   - ❌ 看文档就开始写完整脚本
   - ❌ 凭想象写 `grep` 表达式
   - ❌ 写完脚本再一次性运行
   - ❌ 测试完就删除脚本

   - 执行测试

3. **部署验证**(如果项目需要)
   - 启动应用
   - 健康检查
   - 验证核心功能可访问

4. **生成测试报告**
   创建 `docs/reports/day-N-test-results.md`:
   - 测试概要(总数、通过率)
   - **实际执行的命令**（必须包含）
   - 命令返回码
   - 详细结果
   - 性能指标(如适用)
   - **测试脚本路径**（必须包含）

   示例：
   ```markdown
   ## 单元测试结果

   命令: mvn clean test
   返回码: 0
   总计: 10 tests
   通过: 10
   失败: 0
   持续时间: 45s

   ## 集成测试结果

   命令: bash tests/day-5-api-test.sh
   返回码: 0
   摘要: 8/8 API 测试通过

   ## 验证命令

   完整验证: make verify
   返回码: 0
   ```

5. **更新状态文件**
   ```json
   {
     "current_stage": "testing",
     "completed_stages": ["preparation", "plan", "implementation", "review"],
     "test_report": "docs/reports/day-N-test-results.md",
     "test_script": "tests/day-N-test.sh"
   }
   ```

### 成功标准
- ✅ 所有测试通过
- ✅ 测试报告生成

### 错误处理
**测试失败不应该发生**(因为 Code Review 已通过)。

如果发生:
1. 分析失败原因
2. 报告详细错误信息
3. 返回阶段 2 修复
4. 重新走完 Code Review 和 Testing

**详细说明**: 参见 `references/stage4-testing.md`

---

## 阶段 5: Commit (代码提交)

### 目标
创建规范的 Git commit。

### 步骤

1. **检查 Git 状态**
   ```bash
   git status
   ```

2. **生成 Commit Message**
   遵循 Conventional Commits 规范:
   ```
   <type>(<scope>): <subject>

   <body>
   - 主要变更 1
   - 主要变更 2

   Test Results:
   - [测试结果摘要]

   Code Review:
   - Score: X/10
   - Issues: [问题数量]

   Co-Authored-By: Claude Opus 4.6 <noreply@anthropic.com>
   ```

3. **添加文件并提交**
   ```bash
   git add [新增和修改的文件]
   git commit -m "$(cat <<'EOF'
   [生成的 commit message]
   EOF
   )"
   ```

4. **验证提交成功**
   ```bash
   git log -1 --stat
   ```

5. **更新状态文件**
   ```json
   {
     "current_stage": "commit",
     "completed_stages": ["preparation", "plan", "implementation", "review", "testing"],
     "commit_id": "abc1234..."
   }
   ```

### 成功标准
- ✅ Commit 成功
- ✅ Commit message 符合规范

**详细说明**: 参见 `references/stage5-6-commit-report.md`

---

## 阶段 6: Reporting (生成报告和文档更新)

### 目标
生成完整报告,更新项目文档。

### 步骤

1. **创建实施报告**
   文件: `docs/reports/day-N-implementation.md`

   包含:
   - 任务概述
   - 实施内容(文件清单、代码行数)
   - 技术亮点
   - 遇到的问题和解决方案
   - 方案 vs 实际差异(如有)
   - 质量指标(测试通过率、代码评分)
   - 实际工时
   - Git commit 信息

2. **更新经验总结**
   文件: `docs/lessons-learned.md`

   追加 Day N 章节:
   - 技术决策及理由
   - 最佳实践
   - 反模式(避免的做法)
   - 有用的工具和脚本
   - 待办事项

3. **更新项目地图**
   文件: `CLAUDE.md`

   更新:
   - 项目状态 (当前 Day)
   - 已完成的模块列表
   - 最后更新时间

4. **汇总输出给用户**
   ```
   ✅ Day N 实施完成！

   📊 统计数据:
   - 新增文件: X 个
   - 测试通过率: Y%
   - 代码评分: Z/10

   📄 生成的文档:
   - 技术方案: [路径]
   - 测试报告: [路径]
   - 审查报告: [路径]
   - 实施报告: [路径]

   💾 Git Commit: [commit_id]

   ✨ 下一步: Day {N+1}
   ```

### 成功标准
- ✅ 实施报告生成
- ✅ 经验总结更新
- ✅ CLAUDE.md 更新
- ✅ 用户收到友好的总结

**详细说明**: 参见 `references/stage5-6-commit-report.md`

---

## 断点恢复机制

### 状态文件格式

文件: `.claude/state/day-N-state.json`

```json
{
  "day_number": 3,
  "current_stage": "testing",
  "completed_stages": ["preparation", "plan", "implementation", "review"],
  "failed_stage": null,
  "failure_reason": null,
  "plan_file": ".claude/plans/day-3-plan.md",
  "test_report": "docs/reports/day-3-test-results.md",
  "review_report": "docs/reports/day-3-code-review.md",
  "commit_id": null,
  "started_at": "2026-03-24T10:00:00Z",
  "updated_at": "2026-03-24T11:30:00Z"
}
```

### 恢复流程

当用户说 "继续 Day N" 时:
1. 读取状态文件
2. 从 `current_stage` 继续执行
3. 跳过 `completed_stages` 中的阶段

### 失败记录

如果某阶段失败,更新状态文件:
```json
{
  "failed_stage": "testing",
  "failure_reason": "Unit tests failed: 2/8 test cases",
  "failure_details": "[详细错误信息]"
}
```

恢复时提示用户失败原因和修复建议。

---

## 错误处理原则

### 通用规则

1. **立即停止**: 任何阶段失败,立即停止,不继续后续阶段
2. **清晰报告**: 使用统一格式报告错误
3. **具体建议**: 提供可操作的修复建议
4. **记录状态**: 失败信息记录到状态文件

### 错误报告格式

```
❌ 阶段 X 失败: {阶段名称}
原因: {简短原因}

{详细错误信息}

建议:
1. {具体建议 1}
2. {具体建议 2}

修复后使用: "继续 Day N"
```

---

## 适配不同技术栈

### 技术栈识别

从 `CLAUDE.md` 或项目文件识别:
- Java: `pom.xml`, `build.gradle`
- Node.js: `package.json`
- Python: `requirements.txt`, `pyproject.toml`
- Go: `go.mod`
- Ruby: `Gemfile`
- PHP: `composer.json`

### 命令映射

| 操作 | Java | Node.js | Python | Go |
|------|------|---------|--------|-----|
| 编译 | `mvn compile` | `npm run build` | - | `go build` |
| 测试 | `mvn test` | `npm test` | `pytest` | `go test ./...` |
| 启动 | `mvn spring-boot:run` | `npm start` | `python app.py` | `go run .` |

根据项目实际情况调整。

---

## 总结

### Skill 核心价值

1. **自动化**: 一条命令触发完整流程
2. **质量保障**: 严格执行 Code Review → Testing
3. **通用性**: 支持任何技术栈
4. **可追溯**: 生成完整文档和报告
5. **容错性**: 清晰的错误处理和断点恢复

### 适用场景

✅ **适合使用**:
- 执行开发计划中定义的 Day 任务
- 需要完整质量保障流程
- 需要生成完整文档

❌ **不适合使用**:
- 简单 bug 修复
- 探索性开发
- 原型验证

---

**Skill 版本**: 2.0
**创建时间**: 2026-03-24
**最后更新**: 2026-03-24
