# Code Review Report - Day 1-2

**审查日期**：2026-03-24
**审查范围**：Day 1-2 后端开发工作
**审查方式**：自动化审查 + 人工验证
**审查标准**：质量保障流程三层机制

---

## 📊 审查总览

| 检查项 | 状态 | 严重程度 | 发现问题数 |
|--------|------|----------|-----------|
| 1. 代码 vs Plan 一致性 | ✅ 通过 | - | 0 |
| 2. 实体类 vs 数据库一致性 | ✅ 通过 | - | 0 |
| 3. 配置文件完整性 | ⚠️ 警告 | MEDIUM | 1 |
| 4. 版本兼容性 | ❌ 失败 | **CRITICAL** | 1 |
| 5. 代码质量 | ✅ 通过 | - | 0 |
| 6. 安全问题 | ⚠️ 警告 | HIGH | 1 |

**总体评分**：70/100（需要修复 CRITICAL 问题）

---

## 1️⃣ 代码 vs Plan 一致性检查

### 检查标准
- Plan 中定义的 API 是否都已实现？
- Plan 中定义的字段是否都在实体类和数据库表中？
- Plan 中的技术栈版本是否与实际代码一致？

### 检查结果：✅ 通过

#### Day 1 任务对比

**Plan 要求**：
- [x] 创建后端项目骨架
- [x] 创建数据库初始化脚本（5 张核心表）
- [x] 实现用户认证模块（3 个 API）

**实际完成**：
- ✅ 后端项目骨架已创建（Maven + Spring Boot）
- ✅ 数据库脚本 `V1__init.sql` 包含 5 张表（user, workspace, project, issue, issue_comment）
- ✅ 用户认证模块已实现（UserController + UserService）

**API 接口对比**：

| Plan API | 实际实现 | 状态 |
|----------|---------|------|
| POST /api/v1/auth/register | ✅ UserController.register() | ✅ 一致 |
| POST /api/v1/auth/login | ✅ UserController.login() | ✅ 一致 |
| GET /api/v1/auth/me | ✅ UserController.getCurrentUser() | ✅ 一致 |

#### Day 2 任务对比

**Plan 要求**：
- [ ] 工作区管理模块（2 个 API）
- [ ] 项目管理模块（5 个 API）

**实际完成**：
- ✅ Workspace 实体、Mapper、Service、Controller 已实现
- ✅ Project 实体、Mapper、Service、Controller 已实现

**API 接口对比**：

| Plan API | 实际实现 | 状态 |
|----------|---------|------|
| GET /api/v1/workspaces | ✅ WorkspaceController.getWorkspaces() | ✅ 一致 |
| GET /api/v1/workspaces/{id} | ✅ WorkspaceController.getWorkspaceById() | ✅ 一致 |
| GET /api/v1/projects?workspace_id=1 | ✅ ProjectController.getProjects() | ✅ 一致 |
| GET /api/v1/projects/{id} | ✅ ProjectController.getProjectById() | ✅ 一致 |
| POST /api/v1/projects | ✅ ProjectController.createProject() | ✅ 一致 |
| PUT /api/v1/projects/{id} | ✅ ProjectController.updateProject() | ✅ 一致 |
| DELETE /api/v1/projects/{id} | ✅ ProjectController.deleteProject() | ✅ 一致 |

**结论**：所有 Plan 中定义的 API 都已实现，代码与 Plan 一致。

---

## 2️⃣ 实体类 vs 数据库表一致性检查

### 检查标准
- 实体类的每个字段在表中都有对应的列
- 表中的每个列在实体类中都有对应的字段
- 字段类型匹配（Long ↔ BIGINT, String ↔ VARCHAR）
- 字段名转换正确（camelCase ↔ snake_case）

### 检查结果：✅ 通过（已修复 Day 2 遗留问题）

#### User 实体 vs user 表

| 实体字段 | 数据库列 | 类型匹配 | 状态 |
|---------|---------|---------|------|
| id | id | Long ↔ BIGINT | ✅ |
| username | username | String ↔ VARCHAR(50) | ✅ |
| email | email | String ↔ VARCHAR(100) | ✅ |
| password | password | String ↔ VARCHAR(255) | ✅ |
| avatar | avatar | String ↔ VARCHAR(255) | ✅ |
| displayName | display_name | String ↔ VARCHAR(100) | ✅ |
| isActive | is_active | Boolean ↔ TINYINT(1) | ✅ |
| createdAt | created_at | LocalDateTime ↔ DATETIME | ✅ |
| updatedAt | updated_at | LocalDateTime ↔ DATETIME | ✅ |

**结论**：✅ 完全一致

#### Workspace 实体 vs workspace 表

| 实体字段 | 数据库列 | 类型匹配 | 状态 |
|---------|---------|---------|------|
| id | id | Long ↔ BIGINT | ✅ |
| name | name | String ↔ VARCHAR(100) | ✅ |
| slug | slug | String ↔ VARCHAR(50) | ✅ |
| description | description | String ↔ TEXT | ✅ |
| ownerId | owner_id | Long ↔ BIGINT | ✅ |
| createdAt | created_at | LocalDateTime ↔ DATETIME | ✅ |
| updatedAt | updated_at | LocalDateTime ↔ DATETIME | ✅ |

**结论**：✅ 完全一致

#### Project 实体 vs project 表

| 实体字段 | 数据库列 | 类型匹配 | 状态 |
|---------|---------|---------|------|
| id | id | Long ↔ BIGINT | ✅ |
| name | name | String ↔ VARCHAR(100) | ✅ |
| identifier | identifier | String ↔ VARCHAR(10) | ✅ |
| description | description | String ↔ TEXT | ✅ |
| workspaceId | workspace_id | Long ↔ BIGINT | ✅ |
| **leadId** | **lead_id** | Long ↔ BIGINT | ✅ **已修复** |
| createdAt | created_at | LocalDateTime ↔ DATETIME | ✅ |
| updatedAt | updated_at | LocalDateTime ↔ DATETIME | ✅ |
| createdBy | created_by | Long ↔ BIGINT | ✅ |

**结论**：✅ 完全一致（Day 2 遗留的 `lead_id` 缺失问题已修复）

---

## 3️⃣ 配置文件完整性检查

### 检查标准
- MySQL 配置是否正确
- JWT secret 长度是否 >= 512 bits
- Redis 配置是否正确
- 日志配置是否合理

### 检查结果：⚠️ 警告（1 个 MEDIUM 级别问题）

#### application-dev.yml 检查

| 配置项 | 当前值 | 要求 | 状态 | 严重程度 |
|--------|--------|------|------|----------|
| MySQL host | localhost:3306 | 可配置 | ✅ 正常 | - |
| MySQL username | root | 可配置 | ✅ 正常 | - |
| MySQL password | zhangyuhe | 用户自定义 | ⚠️ **明文存储** | **MEDIUM** |
| MySQL database | plane_lite | 正确命名 | ✅ 正常 | - |
| Redis host | localhost:6379 | 可配置 | ✅ 正常 | - |
| JWT secret | 105 chars (840 bits) | >= 64 chars (512 bits) | ✅ 正常 | - |
| JWT expiration | 604800000ms (7 天) | 合理范围 | ✅ 正常 | - |
| Log level | debug | 开发环境适用 | ✅ 正常 | - |

#### ⚠️ 问题 1：数据库密码明文存储

**严重程度**：MEDIUM
**位置**：`application-dev.yml:5`
**问题描述**：
```yaml
password: zhangyuhe  # 明文存储在配置文件中
```

**风险**：
- 密码以明文形式存储在代码仓库中
- 如果代码泄露，数据库密码会被暴露
- 不符合安全最佳实践

**建议修复**：
```yaml
# 方案 1：使用环境变量
password: ${MYSQL_PASSWORD:default_password}

# 方案 2：使用 Spring Cloud Config
# 从外部配置中心读取

# 方案 3：使用 Jasypt 加密
# spring:
#   datasource:
#     password: ENC(encrypted_password)
```

**优先级**：中等（开发环境可接受，但生产环境必须修复）

---

## 4️⃣ 版本兼容性检查

### 检查标准
- Spring Boot 版本 ↔ MyBatis-Plus 版本兼容性
- Lombok 版本 ↔ Java 版本兼容性
- Maven Java 版本 ↔ 项目 Java 版本一致性

### 检查结果：❌ 失败（1 个 CRITICAL 级别问题）

#### pom.xml 版本配置

| 依赖 | 配置版本 | 状态 |
|------|---------|------|
| Spring Boot | 3.1.5 | ✅ 稳定版本 |
| MyBatis-Plus | 3.5.5 | ✅ 兼容 Spring Boot 3.1.x |
| Lombok | 1.18.32 | ✅ 支持 Java 17 |
| JWT (jjwt) | 0.11.5 | ✅ 最新稳定版 |
| Java (项目配置) | 17 | ✅ LTS 版本 |

**版本兼容性验证**：
- ✅ Spring Boot 3.1.5 + MyBatis-Plus 3.5.5 - [官方兼容](https://github.com/baomidou/mybatis-plus/issues/5196)
- ✅ Lombok 1.18.32 + Java 17 - 官方支持
- ✅ JWT 0.11.5 + Spring Boot 3.1.5 - 兼容

#### ❌ 问题 2：Maven 运行时 Java 版本不匹配

**严重程度**：CRITICAL
**问题描述**：
```bash
# 项目配置 Java 17
<java.version>17</java.version>

# 但 Maven 实际使用 Java 25
$ mvn -version
Java version: 25.0.2, vendor: Homebrew
```

**编译错误**：
```
[ERROR] Fatal error compiling: java.lang.ExceptionInInitializerError:
com.sun.tools.javac.code.TypeTag :: UNKNOWN
```

**根本原因**：
- Lombok 1.18.32 不支持 Java 25（Java 25 是预览版本）
- Maven 使用的 Java 版本与项目配置的 Java 版本不一致
- Lombok 的 AST 解析器无法识别 Java 25 的新语法特性

**影响范围**：
- ❌ 无法编译项目
- ❌ 无法启动应用
- ❌ 完全阻塞开发

**建议修复**：

**方案 1：切换 Maven 使用的 Java 版本（推荐）**
```bash
# 检查已安装的 Java 版本
/usr/libexec/java_home -V

# 临时设置 Java 17
export JAVA_HOME=$(/usr/libexec/java_home -v 17)

# 或永久设置（添加到 ~/.zshrc 或 ~/.bash_profile）
echo 'export JAVA_HOME=$(/usr/libexec/java_home -v 17)' >> ~/.zshrc
source ~/.zshrc

# 验证
java -version  # 应该显示 Java 17
mvn -version   # 应该显示 Java 17
```

**方案 2：升级 Lombok 版本（不推荐，可能不稳定）**
```xml
<!-- pom.xml -->
<properties>
    <lombok.version>1.18.34</lombok.version>  <!-- 最新版本 -->
</properties>
```

**方案 3：配置 Maven Toolchains**
```xml
<!-- ~/.m2/toolchains.xml -->
<toolchains>
  <toolchain>
    <type>jdk</type>
    <provides>
      <version>17</version>
    </provides>
    <configuration>
      <jdkHome>/path/to/jdk17</jdkHome>
    </configuration>
  </toolchain>
</toolchains>
```

**优先级**：**最高**（必须立即修复才能继续开发）

---

## 5️⃣ 代码质量检查

### 检查标准
- Service 方法是否有事务注解
- DTO 是否有校验注解
- Controller 是否有 Swagger 注解
- 代码分层是否清晰
- 异常处理是否完整

### 检查结果：✅ 通过

#### ✅ Service 事务注解检查

**UserServiceImpl**：
- ✅ `register()` - `@Transactional(rollbackFor = Exception.class)` ✅
- ✅ `login()` - `@Transactional(readOnly = true)` ✅
- ✅ `getUserById()` - `@Transactional(readOnly = true)` ✅
- ✅ `getUserByEmail()` - `@Transactional(readOnly = true)` ✅

**ProjectServiceImpl**：
- ✅ `createProject()` - `@Transactional(rollbackFor = Exception.class)` ✅
- ✅ `getProjectsByWorkspace()` - `@Transactional(readOnly = true)` ✅
- ✅ `getProjectById()` - `@Transactional(readOnly = true)` ✅
- ✅ `updateProject()` - `@Transactional(rollbackFor = Exception.class)` ✅
- ✅ `deleteProject()` - `@Transactional(rollbackFor = Exception.class)` ✅

**WorkspaceServiceImpl**：
- ✅ `createDefaultWorkspace()` - `@Transactional(rollbackFor = Exception.class)` ✅
- ✅ `getWorkspacesByUserId()` - `@Transactional(readOnly = true)` ✅
- ✅ `getWorkspaceById()` - `@Transactional(readOnly = true)` ✅

**结论**：所有 Service 方法都有正确的事务注解

#### ✅ DTO 校验注解检查

**UserRegisterDTO**：
```java
@NotBlank(message = "用户名不能为空")
@Size(min = 3, max = 50, message = "用户名长度必须在 3-50 个字符之间")
private String username;

@NotBlank(message = "邮箱不能为空")
@Email(message = "邮箱格式不正确")
private String email;

@NotBlank(message = "密码不能为空")
@Size(min = 6, max = 20, message = "密码长度必须在 6-20 个字符之间")
private String password;
```
✅ 所有字段都有必要的校验注解

**ProjectCreateDTO**：
```java
@NotBlank(message = "项目名称不能为空")
@Size(max = 100, message = "项目名称不能超过 100 个字符")
private String name;

@NotBlank(message = "项目标识符不能为空")
@Pattern(regexp = "^[A-Z]{2,10}$", message = "项目标识符必须是 2-10 个大写字母")
private String identifier;

@NotNull(message = "工作区 ID 不能为空")
private Long workspaceId;
```
✅ 所有必填字段都有校验注解

#### ✅ Controller Swagger 注解检查

**UserController**：
```java
@Tag(name = "用户认证", description = "用户注册、登录、获取当前用户信息")
@RestController
@RequestMapping("/api/v1/auth")

@Operation(summary = "用户注册")
@PostMapping("/register")

@Operation(summary = "用户登录")
@PostMapping("/login")

@Operation(summary = "获取当前用户信息")
@GetMapping("/me")
```
✅ 所有 API 都有 Swagger 注解

#### ✅ 代码分层检查

```
Controller → Service → Mapper
     ↓          ↓         ↓
   DTO        VO       Entity
```

- ✅ Controller 只负责接收请求和返回响应
- ✅ Service 负责业务逻辑处理
- ✅ Mapper 负责数据库操作
- ✅ 没有跨层调用

#### ✅ 异常处理检查

**GlobalExceptionHandler**：
- ✅ 统一异常处理器已实现
- ✅ 处理 `BusinessException`、`UnauthorizedException`、`ForbiddenException`、`NotFoundException`
- ✅ 处理参数校验异常 `MethodArgumentNotValidException`
- ✅ 处理通用异常 `Exception`

**业务逻辑异常处理**：
```java
// UserServiceImpl.register()
if (existingUserByEmail != null) {
    throw new BusinessException("邮箱已被注册");
}

// ProjectServiceImpl.createProject()
if (workspace == null) {
    throw new BusinessException("工作区不存在");
}
if (!workspace.getOwnerId().equals(userId)) {
    throw new ForbiddenException("无权在该工作区创建项目");
}
```
✅ 异常处理完整且规范

---

## 6️⃣ 安全问题检查

### 检查标准
- 是否有硬编码的凭证、API Key、Token
- 是否有 SQL 注入风险
- 是否有 XSS 风险
- 密码是否正确加密
- JWT 是否正确签名和验证

### 检查结果：⚠️ 警告（1 个 HIGH 级别问题）

#### ✅ 密码加密检查

**PasswordUtil**：
```java
public static String encode(String rawPassword) {
    return passwordEncoder.encode(rawPassword);
}

public static boolean matches(String rawPassword, String encodedPassword) {
    return passwordEncoder.matches(rawPassword, encodedPassword);
}
```
✅ 使用 BCrypt 加密，符合安全标准

#### ✅ JWT 签名检查

**JwtTokenProvider**：
```java
Key key = Keys.hmacShaKeyFor(secret.getBytes());

return Jwts.builder()
    .setSubject(String.valueOf(userId))
    .claim("username", username)
    .claim("email", email)
    .setIssuedAt(now)
    .setExpiration(expiryDate)
    .signWith(key, SignatureAlgorithm.HS512)
    .compact();
```
✅ 使用 HS512 算法，secret 长度 840 bits（>= 512 bits），符合安全标准

#### ✅ SQL 注入检查

**MyBatis-Plus LambdaQueryWrapper**：
```java
LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
wrapper.eq(User::getEmail, email);
User user = userMapper.selectOne(wrapper);
```
✅ 使用参数化查询，无 SQL 注入风险

#### ✅ XSS 检查

- ✅ 所有用户输入都经过校验注解验证
- ✅ 使用 Jackson 自动转义 JSON 字符串
- ✅ 无直接拼接 HTML 代码

#### ⚠️ 问题 3：JWT Token 未设置 HttpOnly Cookie

**严重程度**：HIGH
**位置**：`UserController.login()`
**问题描述**：
```java
// 当前实现：Token 通过响应体返回
return Result.success("登录成功", response);

// 客户端需要手动存储 Token（通常存在 localStorage）
```

**风险**：
- Token 存储在 localStorage 容易受到 XSS 攻击
- 恶意脚本可以读取 localStorage 中的 Token
- Token 泄露后，攻击者可以冒充用户

**建议修复**：
```java
@PostMapping("/login")
public Result<LoginResponseDTO> login(
    @Valid @RequestBody UserLoginDTO dto,
    HttpServletResponse response) {

    LoginResponseDTO loginResponse = userService.login(dto);

    // 将 Token 设置到 HttpOnly Cookie 中
    Cookie cookie = new Cookie("token", loginResponse.getToken());
    cookie.setHttpOnly(true);  // 防止 JavaScript 访问
    cookie.setSecure(true);    // 仅 HTTPS 传输
    cookie.setPath("/");
    cookie.setMaxAge(7 * 24 * 60 * 60);  // 7 天
    response.addCookie(cookie);

    return Result.success("登录成功", loginResponse);
}
```

**优先级**：高（但不阻塞 MVP 开发，可以在后续版本修复）

---

## 📋 问题汇总

### ❌ CRITICAL（必须立即修复）

| 问题 | 位置 | 影响 | 修复方案 | 优先级 |
|------|------|------|----------|--------|
| Maven Java 版本不匹配 | 系统环境 | 无法编译 | 切换 JAVA_HOME 到 Java 17 | P0 |

### ⚠️ HIGH（重要但不阻塞）

| 问题 | 位置 | 影响 | 修复方案 | 优先级 |
|------|------|------|----------|--------|
| JWT Token 未使用 HttpOnly Cookie | UserController.login() | XSS 风险 | 设置 HttpOnly Cookie | P1 |

### ⚠️ MEDIUM（建议修复）

| 问题 | 位置 | 影响 | 修复方案 | 优先级 |
|------|------|------|----------|--------|
| 数据库密码明文存储 | application-dev.yml | 凭证泄露风险 | 使用环境变量或加密 | P2 |

---

## 🎯 修复建议

### 立即修复（阻塞开发）

**1. 修复 Java 版本不匹配问题**

```bash
# 步骤 1：检查已安装的 Java 版本
/usr/libexec/java_home -V

# 步骤 2：设置 JAVA_HOME 到 Java 17
export JAVA_HOME=$(/usr/libexec/java_home -v 17)

# 步骤 3：永久设置（添加到 ~/.zshrc）
echo 'export JAVA_HOME=$(/usr/libexec/java_home -v 17)' >> ~/.zshrc
source ~/.zshrc

# 步骤 4：验证
java -version  # 应该显示 17.0.x
mvn -version   # 应该显示 Java version: 17.0.x

# 步骤 5：重新编译
cd /Users/zhangyuhe/Documents/myproject/plane-lite/backend
mvn clean compile
```

### 后续优化（不阻塞开发）

**2. 优化 JWT Token 安全性（P1）**
- 使用 HttpOnly Cookie 存储 Token
- 添加 CSRF Token 防护
- 实现 Token 刷新机制

**3. 优化配置文件安全性（P2）**
- 使用环境变量存储敏感信息
- 添加 `.env.example` 模板
- 在 `.gitignore` 中排除敏感配置

---

## ✅ 优点总结

**代码质量**：
- ✅ 代码分层清晰，遵循 Controller → Service → Mapper 架构
- ✅ 所有 Service 方法都有正确的事务注解
- ✅ 所有 DTO 都有完整的校验注解
- ✅ 所有 API 都有 Swagger 文档注解
- ✅ 异常处理完整且规范

**安全性**：
- ✅ 密码使用 BCrypt 加密
- ✅ JWT 使用 HS512 算法，secret 长度符合要求
- ✅ 使用参数化查询，无 SQL 注入风险
- ✅ 用户输入都经过校验

**一致性**：
- ✅ 实体类与数据库表完全一致（已修复 Day 2 遗留问题）
- ✅ 代码与 Plan 完全一致
- ✅ API 接口设计符合 RESTful 规范

---

## 📈 改进建议

### 短期改进（1-2 天）

1. **修复 Java 版本问题**（P0）
2. **添加单元测试**
   - UserService 单元测试（注册、登录、获取用户）
   - ProjectService 单元测试（CRUD 操作）
   - 测试覆盖率目标：80%

3. **添加 API 集成测试**
   - 使用 MockMvc 测试所有 API 端点
   - 验证请求参数校验
   - 验证响应格式

### 中期改进（3-7 天）

4. **优化 JWT 安全性**（P1）
   - 使用 HttpOnly Cookie
   - 添加 Token 刷新机制
   - 实现 Token 黑名单

5. **优化配置管理**（P2）
   - 使用环境变量管理敏感信息
   - 添加配置文件加密
   - 创建配置模板文件

6. **添加日志审计**
   - 记录用户登录日志
   - 记录敏感操作日志
   - 实现日志聚合和分析

### 长期改进（V2.0+）

7. **引入 Spring Security**
   - 替换手动 JWT 验证
   - 添加角色和权限管理
   - 实现 OAuth2 集成

8. **添加监控和告警**
   - 集成 Prometheus + Grafana
   - 添加应用性能监控（APM）
   - 配置关键指标告警

---

## 📝 审查结论

**总体评价**：代码质量良好，架构清晰，符合设计规范

**阻塞问题**：1 个（Java 版本不匹配，必须立即修复）

**安全问题**：2 个（1 个 HIGH，1 个 MEDIUM）

**建议**：
1. 立即修复 Java 版本问题，确保项目可以编译和运行
2. 优先修复 JWT 安全性问题（使用 HttpOnly Cookie）
3. 添加单元测试和集成测试，提高代码覆盖率
4. 在 V2.0 版本中引入 Spring Security，增强安全性

**下一步行动**：
1. 修复 JAVA_HOME 配置
2. 重新编译项目
3. 启动应用并测试 API
4. 继续 Day 3 开发（问题管理模块）

---

**审查人**：Claude Opus 4.6
**审查日期**：2026-03-24
**文档版本**：v1.0
