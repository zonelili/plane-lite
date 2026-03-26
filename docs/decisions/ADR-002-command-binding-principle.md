# ADR-002: 真实命令绑定与证据链管理

**状态**: 已采纳（2026-03-26）
**日期**: 2026-03-26
**决策者**: AI Agent + 用户
**相关文档**: `/Makefile`, `/docs/conventions/reporting.md`, `/docs/experiences/2026-03-26-documentation-reorganization.md`

---

## 背景（Context）

在 Plane Lite 项目的早期开发中，我们存在以下问题：

1. **命令分散且难以复现**
   - 后端验证需要：`mvn clean compile && mvn test && mvn checkstyle:check`
   - 前端验证需要：`npm run lint && npm run test && npm run build`
   - 每个命令都不一样，新人容易搞错
   - 执行结果只记录"通过/失败"，不记录实际返回码

2. **质量检查无法追踪**
   - 代码审查报告只写"✅ 通过"，没有执行的实际命令
   - 测试报告没有记录执行时长、返回码、输出摘要
   - 无法复现测试场景，出现问题时无法调试

3. **工程化缺失**
   - 没有统一的 `make verify` 命令
   - 没有 pre-commit 验证机制
   - CI/CD 流水线无法自动化

### 根本原因

Harness Lab 框架中发现的问题：**命令执行必须被显式绑定和记录**

- ❌ 口头说"你运行 mvn test"（没有写入项目配置）
- ❌ 报告中写"测试通过"（没有记录实际执行的命令和返回码）
- ❌ 依赖开发者记忆（下次来的人不知道该怎么做）

---

## 决策（Decision）

建立**真实命令绑定原则**：

### 1. 命令显式绑定

**在 Makefile 中定义所有验证命令**：
```makefile
# 后端验证
backend-verify: clean-backend compile-backend test-backend lint-backend

# 前端验证
frontend-verify: clean-frontend lint-frontend test-frontend build-frontend

# 统一验证
verify: backend-verify frontend-verify
```

**好处**：
- 新人运行 `make verify` 就能完成所有验证
- 命令集中管理，易于修改和维护
- 可被 CI/CD 系统直接调用

### 2. 证据链管理

**在报告中记录实际执行的信息**：

```markdown
## 执行信息

- **执行时间**：2026-03-26 14:30:00
- **执行命令**：make backend-verify
- **返回码**：0（成功）
- **执行时长**：2 分钟 34 秒

## 命令输出摘要

```bash
$ mvn clean compile
[INFO] BUILD SUCCESS

$ mvn test
[INFO] 10/10 tests passed
[INFO] BUILD SUCCESS

$ mvn checkstyle:check
[INFO] BUILD SUCCESS
```
```

**好处**：
- 可追踪性：知道执行了哪个命令
- 可复现性：其他人可以运行同一个命令
- 证据链：完整的执行日志，便于问题排查

### 3. 环境清单绑定

**在项目初始化时明确环境依赖**：

```
# Makefile 头部
# 环境要求
#   - Java: OpenJDK 17+
#   - Maven: 3.8.1+
#   - Node.js: 18.x+
#   - npm: 9.x+
#   - MySQL: 8.0+

.PHONY: check-env
check-env:
	@echo "Checking environment..."
	java -version 2>&1 | grep "17"
	mvn --version 2>&1 | grep "3.8"
	node --version 2>&1 | grep "v18"
	npm --version 2>&1 | grep "9"
```

**好处**：
- 环境问题前置暴露
- 新人可快速检查本地环境是否满足
- 减少"在我电脑上能跑"的问题

---

## 理由（Rationale）

### 为什么命令要显式绑定？

1. **AI Agent 需要明确的执行指令**
   - AI 可以读懂 Makefile 中的命令
   - AI 知道该执行什么命令，而不是靠"猜测"
   - 便于自动化执行和重复运行

2. **复现性是调试的基础**
   - 如果出现问题，必须能重新运行同一个命令
   - 否则"我在本地运行没问题"无法验证
   - 证据链被破坏，问题无法追踪

3. **集成到 CI/CD 的先决条件**
   - CI/CD 系统必须知道"验证命令是什么"
   - 单独的 shell 脚本无法完全替代 Makefile
   - 标准化的命令是团队合作的基础

### 为什么要记录返回码和执行时长？

1. **返回码是唯一的真相**
   - 返回码 0 = 成功，非 0 = 失败
   - 避免"看起来成功但实际失败"的情况
   - 完全自动化的验证需要依赖返回码

2. **执行时长帮助识别性能问题**
   - 如果测试从 30 秒增加到 5 分钟，需要调查
   - 可以设置性能预警（如果超过 X 秒则告警）
   - 帮助持续优化开发体验

3. **完整的日志便于事后审计**
   - 出现 Bug 时，可以查看当时的执行日志
   - 便于问题复现和追踪
   - 支持团队知识共享和经验总结

### 为什么要定义环境清单？

1. **环境问题是项目启动的主要困难**
   - Java 版本不对、Maven 版本不对等
   - 新人浪费很多时间在环境搭建上
   - 提前检查可以节省时间

2. **显式的环境要求便于自动化**
   - CI/CD 系统可以基于环境清单做选择
   - 容器化部署时可以按需安装依赖
   - 减少"环境不一致"的问题

---

## 实施方式

### Phase 1: 创建 Makefile（已完成）

- ✅ 在项目根目录创建 `Makefile`
- ✅ 定义后端验证命令（compile, test, lint）
- ✅ 定义前端验证命令（lint, test, build）
- ✅ 定义统一的 `make verify` 命令
- ✅ 定义环境检查命令（check-env）

### Phase 2: 更新 pom.xml（已完成）

- ✅ 添加 Maven Checkstyle 插件
- ✅ 添加 Maven Surefire 插件（单元测试）
- ✅ 配置插件参数（includes, excludes, fail-on-error）

### Phase 3: 更新报告规范（已完成）

- ✅ 创建 `/docs/conventions/reporting.md`
- ✅ 定义报告必须包含的字段（时间、命令、返回码、输出）
- ✅ 更新 implement-day skill 的 Testing 阶段

### Phase 4: 验证执行（当前）

- [ ] 运行 `make verify` 验证所有命令生效
- [ ] 运行 `make check-env` 验证环境检查
- [ ] 记录实际执行时长和返回码
- [ ] 在 Day 6 开发中应用新的报告规范

### Phase 5: 集成到工作流（后续）

- [ ] 在 implement-day skill 中自动调用 `make verify`
- [ ] 在 code-reviewer 中自动检查命令执行结果
- [ ] 考虑集成到 Git pre-commit hook

---

## 影响（Consequences）

### 积极影响

✅ **AI Agent 友好**：明确的命令绑定，AI 知道该执行什么

✅ **复现性强**：任何人都能运行同一个命令并得到相同结果

✅ **自动化友好**：CI/CD 系统可以直接调用 Makefile

✅ **团队协作高效**：新人快速上手，知道如何验证代码

✅ **可追踪性好**：完整的执行日志，便于问题排查

✅ **性能可观测**：执行时长记录，便于性能监控

### 潜在风险

⚠️ **维护成本**：Makefile 需要持续维护，保持与实际命令同步

**缓解措施**：
- 定期回顾 Makefile，确保命令仍然有效
- 在更新验证命令时同步更新 Makefile
- 记录 Makefile 的版本和修改日期

⚠️ **学习曲线**：需要学习 Makefile 语法

**缓解措施**：
- Makefile 保持简单，只做命令组合，不做复杂逻辑
- 提供 `make help` 显示所有可用命令
- 在 README 中说明如何使用 Makefile

---

## 替代方案

### 方案 1: 使用 Shell 脚本（scripts/verify.sh）

**优点**：
- 灵活性高
- 可以做复杂的逻辑

**缺点**：
- 需要执行 `bash scripts/verify.sh`，不如 `make verify` 简洁
- 跨平台性不如 Makefile
- 难以与 CI/CD 系统集成

**决策**：不采用，因为 Makefile 更标准化

### 方案 2: 使用 Maven 多模块项目

**优点**：
- 可以在 pom.xml 中定义所有命令
- 对后端项目友好

**缺点**：
- 前端项目无法纳入 Maven 管理
- 需要复杂的 pom.xml 配置
- 跨平台支持不如 Makefile

**决策**：不采用，因为无法统一管理前后端

### 方案 3: 使用 npm scripts（对标前端）

**优点**：
- 前端友好
- 配置简单

**缺点**：
- 后端无法使用
- 需要两套命令系统
- 不利于统一管理

**决策**：不采用，Makefile 更通用

---

## 参考资料

- GNU Make 官方文档：https://www.gnu.org/software/make/manual/
- Maven 插件文档：https://maven.apache.org/plugins/
- 真实命令绑定原则：Harness Engineering Framework
- 项目 Makefile：`/Makefile`
- 报告规范：`/docs/conventions/reporting.md`

---

## 修订历史

| 日期 | 版本 | 修订内容 | 修订人 |
|------|------|---------|--------|
| 2026-03-26 | 1.0 | 初始版本，记录命令绑定原则 | AI Agent |
| | | | |
