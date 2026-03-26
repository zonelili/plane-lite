# 报告编写规范

> Plane Lite 项目的报告交付物标准和规范
> 适用于 Code Review、QA、Ship 等所有阶段的报告

## 报告体系

### 三类报告

| 报告类型 | 触发时机 | 责任人 | 输出路径 |
|---------|---------|--------|---------|
| **Code Review** | Implementation 完成后 | Code Reviewer Agent | `docs/reports/day-N-code-review.md` |
| **QA** | Testing 完成后 | QA / Test Runner | `docs/reports/day-N-qa.md` |
| **Ship** | 准备发布时 | Release Manager | `docs/reports/day-N-ship.md` |（可选）

## 报告通用规范

### 必须包含的内容

```markdown
# Day N 代码审查报告（或 QA / Ship）

## 执行信息

- **执行时间**：YYYY-MM-DD HH:mm:ss
- **执行命令**：实际执行的命令（例如：`make backend-verify`）
- **返回码**：0（成功）/ 非 0（失败）
- **执行时长**：XX 分钟 YY 秒

## 执行结果

### ✅ 通过项

- [ ] 项目 1
- [ ] 项目 2
- [ ] 项目 N

### ⚠️ 警告项

- [ ] 项目 1：说明
- [ ] 项目 2：说明

### ❌ 失败项

- [ ] 项目 1：说明
- [ ] 项目 2：说明

## 详细信息

### 标准 / 规范符合度

| 标准 | 状态 | 说明 |
|------|------|------|
| API 规范 | ✅ / ⚠️ / ❌ | ... |
| 数据库规范 | ✅ / ⚠️ / ❌ | ... |
| 代码规范 | ✅ / ⚠️ / ❌ | ... |

### 关键指标

- 代码行数：XXX 行
- 测试覆盖率：XX%（如适用）
- 性能指标：XXX（如适用）

## 建议

### 继续推进

如果：通过项 ≥ 90% 且无 Critical/Major 问题

建议：继续推进到下一阶段

### 需要修复

如果：有 Critical 或 Major 问题

建议：
1. 修复以下问题：[列表]
2. 重新运行 `make verify`
3. 重新提交审查

### 搁置或阻塞

如果：有不可解决的依赖或环境问题

建议：
1. 原因：[说明]
2. 恢复条件：[说明]
3. 预期时间：XXX

## 附录：命令执行日志

```bash
$ make verify
# 完整的命令输出内容
# ...
```

---

## Code Review 报告专项规范

### 标题

```
# Day N 代码审查报告
```

### 审查维度

```markdown
## 代码架构与分层

- [ ] Controller 层：接收请求、参数校验、调用 Service
- [ ] Service 层：业务逻辑、事务管理、异常处理
- [ ] Repository/Mapper 层：数据库操作、查询优化
- [ ] Entity 类：字段设计、注解完整性、数据库映射

## 规范符合度

- [ ] 命名规范（类名、方法名、变量名）
- [ ] 注解规范（@Service、@Transactional、@Valid 等）
- [ ] 异常处理（BusinessException、ResourceNotFoundException 等）
- [ ] 日志规范（SLF4J、日志级别）

## 数据库规范

- [ ] 表结构：字段类型、长度、NOT NULL
- [ ] 索引：主键、唯一索引、普通索引
- [ ] 命名：表名下划线、字段名下划线、索引名前缀
- [ ] 时间字段：created_at、updated_at 是否存在
- [ ] 软删除：is_delete 字段是否正确

## API 规范

- [ ] URL 设计：RESTful，名词复数，版本号 /api/v1/
- [ ] 请求参数：DTO、@Valid 验证、请求示例
- [ ] 响应格式：统一 {code, message, data}、错误码明确
- [ ] 文档：Swagger 注解、@ApiOperation、@ApiParam

## 审查结论

### Summary

单句总结（例如："代码质量良好，遵守规范，可以合并"）

### Issues Found

按 Severity 分类：
- **Critical**：必须修复，阻塞合并
  - [ ] 问题 1
  - [ ] 问题 2
- **Major**：强烈建议修复
  - [ ] 问题 1
- **Minor**：建议改进
  - [ ] 问题 1
- **Trivial**：可选改进
  - [ ] 问题 1

### Recommended Actions

基于上述分类的建议行动。
```

---

## QA 报告专项规范

### 标题

```
# Day N QA 测试报告
```

### 测试范围

```markdown
## 测试覆盖

- [ ] 单元测试：XXX 个测试，XXX 通过，X 失败
- [ ] 集成测试：XXX 个测试，XXX 通过，X 失败
- [ ] API 测试：XXX 个端点，XXX 通过，X 失败
- [ ] 部署验证：启动成功、健康检查通过

## 命令执行

### 后端测试

Command: mvn clean test
Return code: 0
Output: [摘要]

### 前端构建

Command: npm run build
Return code: 0
Output: [摘要]

### 完整验证

Command: make verify
Return code: 0
Duration: XX 分钟 YY 秒
```

### 缺陷报告

```markdown
## 发现的缺陷

### Critical

- [ ] 缺陷 1：[描述] [复现步骤] [预期 vs 实际]

### Major

- [ ] 缺陷 1：[描述]

### Minor

- [ ] 缺陷 1：[描述]

## 测试结论

- 总体通过率：XX%
- 建议：[继续推进 / 修复后重测 / 搁置原因]
```

---

## Ship 报告专项规范

### 标题

```
# Day N 发布报告
```

### 发布信息

```markdown
## 发布清单

- [ ] 代码已合并到主分支
- [ ] 所有测试通过
- [ ] 版本号已更新（backend/pom.xml、frontend/package.json）
- [ ] 发布日志已生成
- [ ] 文档已更新

## 部署验证

- [ ] 后端启动成功：http://localhost:8080/health
- [ ] 前端可访问：http://localhost:5173
- [ ] API 响应正常
- [ ] 核心业务流程可用

## 发布结论

- 发布时间：YYYY-MM-DD HH:mm:ss
- 发布人员：XX
- 发布环境：dev / test / prod
- 回滚方案：[如需回滚的操作]
```

---

## 报告文件命名约定

```
docs/reports/
├── day-N-code-review.md      # Code Review 报告
├── day-N-qa.md               # QA 测试报告
├── day-N-ship.md             # Ship 发布报告（可选）
├── day-N-test-results.md     # 测试结果详细版（可选）
└── REPORT_TEMPLATE.md        # 本规范文档
```

## 报告审批流程

```
实现完成 → Code Review ✅ → Testing ✅ → Ship ✅ → 完成
              ↓                  ↓              ↓
           报告 CR            报告 QA        报告 Ship
           (24h内)            (24h内)       (发布前)
```

### 关键检查点

| 阶段 | 必须条件 | 报告要求 |
|------|---------|---------|
| Code Review | 无 Critical 问题 | 记录审查维度和结论 |
| Testing | 所有测试通过（返回码=0） | 记录命令、返回码、结果 |
| Ship | 环境验证通过 | 记录发布时间、版本、验证结果 |

## 常见问题

### Q: 报告何时提交？

**A:**
- Code Review：Implementation 完成、Commit 前
- QA：Testing 完成、Push 前
- Ship：准备发布时

### Q: 命令执行失败怎么写报告？

**A:** 如实写失败：
```markdown
- **返回码**：非 0（具体数字）
- **失败原因**：[错误信息摘要]
- **建议**：修复并重新运行命令

## 附录：完整错误日志

```bash
$ mvn clean test
[错误输出完整内容]
```
```

### Q: 可以省略某些部分吗？

**A:** 不可以。每个报告必须包含：
1. 执行时间
2. 执行命令
3. 返回码
4. 执行结果

其他内容根据类型选择包含。

---

## 版本历史

| 日期 | 变更 |
|------|------|
| 2026-03-26 | 创建报告编写规范，统一 CR / QA / Ship 报告格式 |

## 相关文档

- **质量保障**：`docs/workflows/quality-assurance.md`
- **API 规范**：`docs/conventions/api.md`
- **数据库规范**：`docs/architecture/database-design.md`
- **Skill 导航**：`.claude/skills/README.md`
