# 质量保障四层机制设计与实施（2026-03-26）

## 问题场景

在 Plane Lite 的 Day 1-4 开发过程中，我们经历了三个质量关键时期：

### Day 2：质量问题爆发
- Spring Boot 3.2 + MyBatis-Plus 3.5.5 版本不兼容问题
- 代码实现与方案设计不一致
- 配置完整性不足（缺 Swagger 文档、JWT 配置等）

**问题**：当时只有"方案设计（Plan Mode）+ 代码实现（Implementation）"两层，缺少**独立的验证环节**。

### Day 3-4：流程逐步完善
- Day 3 实现后补上了"代码审查"环节
- Day 4 发现代码审查后仍需"独立测试"环节

**发现**：需要完整的"Plan → Implementation → Code Review → Testing → Commit"流程。

## 根本原因

原有的三层机制：
1. Plan Mode（方案设计）
2. Implementation（代码实现）
3. Testing（测试验证）

**缺陷**：
- Code Review 和 Testing 的顺序不清
- Code Review 和 Testing 的职责重叠
- 没有明确的"什么时候该停止"的标准

## 解决方案

### 新的四层机制

```
Day N 任务
  ↓
【Layer 1: Plan Mode（方案设计）】
  - 进入 Plan Mode
  - 调用 Plan Agent 设计方案
  - 生成技术方案文档（架构、数据库、接口、DFX、前端规范）
  - 验收：方案完整、包含文件清单、验收标准明确
  ↓
【Layer 2: Implementation（代码实现）】
  - 读取方案文件
  - 按文件顺序创建代码
  - 编译验证（mvn clean compile）
  - 验收：编译通过、所有文件已创建
  ↓
【Layer 3: Code Review（代码审查）】
  - 获取代码变动范围（git diff）
  - 启动独立的 Code Reviewer Agent
  - 验证：
    * 代码与方案一致
    * 实体类与数据库表一致
    * 代码规范、架构分层、注解完整
    * 配置文件完整性
  - 验收：无 Critical 问题、问题可修复
  ↓
【Layer 4: Testing（自动化测试）】
  - 查阅测试文档（API 规范、历史测试、数据库脚本）
  - 编写测试脚本（Day N+1 可复用）
  - 执行测试（mvn clean test）
  - 执行 API 测试（测试脚本）
  - 验收：所有测试通过（return code = 0）
  ↓
【Code Review → Testing 的关键原则】
  - Code Review **必须**先于 Testing
  - Testing 不应该发现 Code Review 能发现的问题
  - 如果 Testing 失败，反馈到 Code Review，修复后重新 Testing
```

### 关键设计决策

#### 1. 为什么 Code Review 要先于 Testing？

**因为**：
- Code Review 检查的是"代码质量"和"规范符合度"（结构问题）
- Testing 检查的是"功能正确性"（逻辑问题）
- 如果代码有结构问题（如缺 @Transactional 注解），Testing 可能无法检测到

**例子**：
- ✅ Code Review 能发现：缺少异常处理、Service 没加 @Transactional、DTO 没加 @Valid
- ❌ Testing 无法发现：因为这些问题不会导致测试失败

#### 2. 为什么需要独立的 Code Reviewer Agent？

**因为**：
- 主会话已知实现过程中的所有细节，容易有偏见
- 独立的 Agent 从零开始审查，能发现主会话容易忽略的问题
- 保证了代码审查的**客观性**

#### 3. 谁负责 Layer 3（Code Review）和 Layer 4（Testing）？

**设计理念**：
- Layer 3（Code Review）由 `superpowers:code-reviewer` agent 负责
- Layer 4（Testing）由主会话的 Testing 阶段负责
- 两层的职责清晰分工，互不重叠

## 实施方式

### 在 implement-day skill 中的体现

```
.claude/skills/implement-day/SKILL.md

阶段 3: Code Review（代码审查）
  ├─ 前置检查：Implementation 是否完成、编译是否通过
  ├─ 获取代码变动范围（git diff）
  └─ 调用 superpowers:code-reviewer agent
      └─ 审查维度：架构、规范、配置、数据库一致性

阶段 4: Testing（自动化测试）
  ├─ 前置检查：Code Review 是否完成、无 Critical 问题
  ├─ 查阅测试文档（API、历史报告、DB 脚本）
  ├─ 执行单元测试（mvn clean test）
  ├─ 执行集成测试（tests/day-N-test.sh）
  └─ 生成测试报告（命令、返回码、摘要）
```

### 报告体系

对应四层机制的报告：
- Layer 1（Plan）→ 技术方案文档（.claude/plans/day-N-design.md）
- Layer 2（Implementation）→ 实施报告（docs/reports/day-N-implementation.md）
- Layer 3（Code Review）→ 审查报告（docs/reports/day-N-code-review.md）
- Layer 4（Testing）→ 测试报告（docs/reports/day-N-test-results.md）

## 验证方式

### 短期验证（每个 Day）

- [ ] 是否经过了完整的四层流程？
- [ ] 每层的报告是否都生成了？
- [ ] 是否记录了实际执行的命令和返回码？

### 长期验证

- [ ] 代码质量是否在下降或稳定？
- [ ] 测试通过率是否保持在 100%？
- [ ] 是否还在出现 Code Review 应该发现的问题？

## 适用场景

### ✅ 适用

- 所有常规 Day N 开发任务
- 需要代码质量保证的功能实现
- 需要自动化测试验证的模块

### ⚠️ 可调整

- 紧急 Hotfix：可能跳过 Plan Mode，直接实施
- 小型 Bug 修复：可能简化 Code Review 范围
- 文档更新：可能不需要完整的 Testing

### ❌ 不适用

- 纯文档变更
- 配置临时调试
- 原型验证（一次性代码）

## 相关文件

### 核心 Skill 文档
- `.claude/skills/implement-day/SKILL.md` - 四层流程的具体实施
- `.claude/skills/tech-design/SKILL.md` - Plan Mode 的设计规范

### 报告规范
- `docs/conventions/reporting.md` - Code Review / QA / Ship 报告格式

### 实施记录
- `docs/workflows/quality-assurance.md` - 详细的工作流说明

## 成功案例

### Day 3-4 应用四层机制后

| 指标 | Day 2 | Day 3-4 |
|------|--------|---------|
| 测试通过率 | 80% | 100% |
| Code Review 发现问题数 | N/A | 8 个 |
| Testing 失败后修复周期 | 长 | 短（Code Review 已消除大部分问题） |
| 整体开发周期 | 长 | 更可控 |

**关键改进**：
- Day 3 执行 Code Review 后，发现了 Service 缺少 @Transactional 注解等问题
- Day 4 这些问题在 Code Review 阶段就被捕获，Testing 阶段没有失败

## 核心原则总结

> **质量保障四层机制确保了代码的结构质量和功能正确性，通过明确的职责分工和前置检查机制，减少了问题发现和修复的周期。**

1. **Plan → Implementation 必须一致**（Layer 1 → Layer 2）
2. **Code Review 必须先于 Testing**（Layer 3 → Layer 4）
3. **每层都有明确的前置检查和验收标准**
4. **每层都有对应的报告**
5. **失败了立即反馈上一层修复**（不能往下推）

## 后续改进方向

### 可考虑的优化

1. **自动化 Code Review 检查**
   - 创建 Pre-Commit Hook 执行基础检查
   - 关键规范（如缺 @Transactional）可自动检测

2. **测试覆盖率提升**
   - 建立最低覆盖率标准（>= 80%）
   - 自动化生成覆盖率报告

3. **文档与代码同步**
   - Swagger 文档与 API 实现同步检查
   - 技术方案与最终实现的差异分析

4. **性能审查层**
   - 可考虑增加 Layer 5：Performance Review
   - 但暂时不需要，因为项目规模还小

## 版本历史

| 日期 | 变更 |
|------|------|
| 2026-03-26 | 创建四层机制经验文档，总结实施方式和核心原则 |
| 2026-03-24 | 升级三层到四层，独立出 Testing 环节 |
| 2026-03-23 | 初始的三层质量保障机制（Plan → Implementation → Testing） |

## 参考链接

- `docs/workflows/quality-assurance.md` - 完整的工作流说明
- `.claude/skills/implement-day/SKILL.md` - 实施细节
- `docs/conventions/reporting.md` - 报告规范
