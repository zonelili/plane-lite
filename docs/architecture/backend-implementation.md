# 后端实现方案

**文档版本**：v1.0
**创建日期**：2026-03-23
**状态**：已确认

---

## 1. 项目结构

### 1.1 完整目录结构

```
plane-lite-backend/
├── src/
│   ├── main/
│   │   ├── java/com/planelite/
│   │   │   ├── PlaneApplication.java
│   │   │   ├── config/
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   ├── RedisConfig.java
│   │   │   │   ├── MybatisPlusConfig.java
│   │   │   │   ├── WebMvcConfig.java
│   │   │   │   └── SwaggerConfig.java
│   │   │   ├── common/
│   │   │   │   ├── constant/
│   │   │   │   │   ├── ResultCode.java
│   │   │   │   │   ├── IssueStatus.java
│   │   │   │   │   └── IssuePriority.java
│   │   │   │   ├── exception/
│   │   │   │   │   ├── BusinessException.java
│   │   │   │   │   ├── UnauthorizedException.java
│   │   │   │   │   ├── ForbiddenException.java
│   │   │   │   │   └── NotFoundException.java
│   │   │   │   ├── result/
│   │   │   │   │   ├── Result.java
│   │   │   │   │   └── PageResult.java
│   │   │   │   └── handler/
│   │   │   │       └── GlobalExceptionHandler.java
│   │   │   ├── security/
│   │   │   │   ├── JwtTokenProvider.java
│   │   │   │   ├── JwtAuthenticationFilter.java
│   │   │   │   └── SecurityUser.java
│   │   │   ├── util/
│   │   │   │   ├── BeanCopyUtil.java
│   │   │   │   └── PasswordUtil.java
│   │   │   └── module/
│   │   │       ├── user/
│   │   │       │   ├── controller/
│   │   │       │   │   └── UserController.java
│   │   │       │   ├── service/
│   │   │       │   │   ├── UserService.java
│   │   │       │   │   └── impl/
│   │   │       │   │       └── UserServiceImpl.java
│   │   │       │   ├── mapper/
│   │   │       │   │   └── UserMapper.java
│   │   │       │   ├── entity/
│   │   │       │   │   └── User.java
│   │   │       │   └── dto/
│   │   │       │       ├── UserRegisterDTO.java
│   │   │       │       ├── UserLoginDTO.java
│   │   │       │       └── UserVO.java
│   │   │       ├── workspace/
│   │   │       ├── project/
│   │   │       ├── issue/
│   │   │       └── comment/
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       ├── application-prod.yml
│   │       ├── mapper/
│   │       │   ├── UserMapper.xml
│   │       │   ├── ProjectMapper.xml
│   │       │   └── IssueMapper.xml
│   │       └── db/
│   │           └── migration/
│   │               └── V1__init.sql
│   └── test/
│       └── java/com/planelite/
│           ├── PlaneApplicationTests.java
│           └── module/
│               └── user/
│                   └── UserServiceTest.java
├── pom.xml
├── Dockerfile
└── README.md
```

---

## 2. Maven 依赖配置

### 2.1 pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>

    <groupId>com.planelite</groupId>
    <artifactId>plane-lite-backend</artifactId>
    <version>1.0.0</version>
    <name>Plane Lite Backend</name>

    <properties>
        <java.version>17</java.version>
        <mybatis-plus.version>3.5.5</mybatis-plus.version>
        <jwt.version>0.11.5</jwt.version>
        <springdoc.version>2.2.0</springdoc.version>
    </properties>

    <dependencies>
        <!-- Spring Boot Web -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- Spring Boot Validation -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>

        <!-- MyBatis-Plus -->
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-boot-starter</artifactId>
            <version>${mybatis-plus.version}</version>
        </dependency>

        <!-- MySQL -->
        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
        </dependency>

        <!-- Redis -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>

        <!-- JWT -->
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-api</artifactId>
            <version>${jwt.version}</version>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-impl</artifactId>
            <version>${jwt.version}</version>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-jackson</artifactId>
            <version>${jwt.version}</version>
        </dependency>

        <!-- Lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>

        <!-- SpringDoc OpenAPI (Swagger) -->
        <dependency>
            <groupId>org.springdoc</groupId>
            <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
            <version>${springdoc.version}</version>
        </dependency>

        <!-- Test -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

---

## 3. 配置文件

### 3.1 application.yml

```yaml
spring:
  application:
    name: plane-lite
  profiles:
    active: ${SPRING_PROFILES_ACTIVE:dev}

server:
  port: ${SERVER_PORT:8080}

# API 文档
springdoc:
  api-docs:
    path: /v3/api-docs
  swagger-ui:
    path: /swagger-ui.html
```

### 3.2 application-dev.yml

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/plane_lite?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: password
    driver-class-name: com.mysql.cj.jdbc.Driver

  redis:
    host: localhost
    port: 6379
    database: 0
    timeout: 3000ms

# MyBatis-Plus
mybatis-plus:
  mapper-locations: classpath:mapper/**/*.xml
  type-aliases-package: com.planelite.module.*.entity
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  global-config:
    db-config:
      id-type: auto
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0

# JWT
jwt:
  secret: ${JWT_SECRET:your-secret-key-change-in-production}
  expiration: ${JWT_EXPIRATION:604800000}

# 日志
logging:
  level:
    com.planelite: debug
    org.springframework.web: info
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} - %msg%n"
```

### 3.3 application-prod.yml

```yaml
spring:
  datasource:
    url: jdbc:mysql://${MYSQL_HOST}:${MYSQL_PORT}/${MYSQL_DATABASE}?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=Asia/Shanghai
    username: ${MYSQL_USER}
    password: ${MYSQL_PASSWORD}
    driver-class-name: com.mysql.cj.jdbc.Driver

  redis:
    host: ${REDIS_HOST}
    port: ${REDIS_PORT}

# MyBatis-Plus
mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.slf4j.Slf4jImpl

# 日志
logging:
  level:
    com.planelite: info
    org.springframework.web: warn
```

---

## 4. 核心代码实现

### 4.1 统一响应封装

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result<T> {
    private Integer code;
    private String message;
    private T data;

    public static <T> Result<T> success(T data) {
        return new Result<>(200, "success", data);
    }

    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data);
    }

    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null);
    }

    public static <T> Result<T> error(String message) {
        return new Result<>(500, message, null);
    }
}
```

### 4.2 JWT Token 生成和验证

```java
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    public String generateToken(Long userId, String username, String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("username", username)
                .claim("email", email)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return Long.parseLong(claims.getSubject());
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
```

### 4.3 JWT 认证过滤器

```java
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = getTokenFromRequest(request);

            if (token != null && tokenProvider.validateToken(token)) {
                Long userId = tokenProvider.getUserIdFromToken(token);
                // 将用户 ID 存入请求属性
                request.setAttribute("userId", userId);
            }
        } catch (Exception e) {
            logger.error("JWT 认证失败", e);
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
```

### 4.4 全局异常处理

```java
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e) {
        log.warn("业务异常: {}", e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(UnauthorizedException.class)
    public Result<?> handleUnauthorizedException(UnauthorizedException e) {
        log.warn("未认证: {}", e.getMessage());
        return Result.error(401, e.getMessage());
    }

    @ExceptionHandler(ForbiddenException.class)
    public Result<?> handleForbiddenException(ForbiddenException e) {
        log.warn("无权限: {}", e.getMessage());
        return Result.error(403, e.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    public Result<?> handleNotFoundException(NotFoundException e) {
        log.warn("资源不存在: {}", e.getMessage());
        return Result.error(404, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleValidationException(MethodArgumentNotValidException e) {
        List<String> errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());
        log.warn("参数校验失败: {}", errors);
        return Result.error(400, "参数校验失败");
    }

    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        log.error("系统异常", e);
        return Result.error(500, "系统内部错误");
    }
}
```

### 4.5 Entity 基类

```java
@Data
@MappedSuperclass
public abstract class BaseEntity implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
```

### 4.6 MyBatis-Plus 配置

```java
@Configuration
@EnableTransactionManagement
@MapperScan("com.planelite.module.*.mapper")
public class MybatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 分页插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new MetaObjectHandler() {
            @Override
            public void insertFill(MetaObject metaObject) {
                this.strictInsertFill(metaObject, "createdAt", LocalDateTime.class, LocalDateTime.now());
                this.strictInsertFill(metaObject, "updatedAt", LocalDateTime.class, LocalDateTime.now());
            }

            @Override
            public void updateFill(MetaObject metaObject) {
                this.strictUpdateFill(metaObject, "updatedAt", LocalDateTime.class, LocalDateTime.now());
            }
        };
    }
}
```

---

## 5. 开发规范

### 5.1 命名规范

**包命名**：
- 全小写
- 单词间用点分隔
- 例：`com.planelite.module.user.controller`

**类命名**：
- PascalCase（大驼峰）
- Controller: `UserController`
- Service: `UserService` / `UserServiceImpl`
- Mapper: `UserMapper`
- Entity: `User`
- DTO: `UserRegisterDTO` / `UserVO`

**方法命名**：
- camelCase（小驼峰）
- CRUD: `createUser`, `getUserById`, `updateUser`, `deleteUser`
- 查询列表: `listProjects`, `listIssues`
- 检查: `existsByEmail`, `checkPermission`

**常量命名**：
- 全大写，下划线分隔
- 例：`MAX_PAGE_SIZE`, `DEFAULT_STATUS`

### 5.2 注释规范

**类注释**：
```java
/**
 * 用户服务实现类
 *
 * @author AI Coding Team
 * @since 2026-03-23
 */
@Service
public class UserServiceImpl implements UserService {
    // ...
}
```

**方法注释**：
```java
/**
 * 用户注册
 *
 * @param dto 注册信息
 * @return 用户信息
 * @throws BusinessException 如果邮箱已存在
 */
@Override
public UserVO register(UserRegisterDTO dto) {
    // ...
}
```

### 5.3 事务管理

**规则**：
- Service 层方法添加 `@Transactional`
- 查询方法使用 `@Transactional(readOnly = true)`
- 回滚所有异常：`@Transactional(rollbackFor = Exception.class)`

```java
@Service
@RequiredArgsConstructor
public class IssueServiceImpl implements IssueService {

    // 写操作：默认事务
    @Transactional(rollbackFor = Exception.class)
    public Issue createIssue(IssueCreateDTO dto) {
        // ...
    }

    // 读操作：只读事务
    @Transactional(readOnly = true)
    public Issue getIssueById(Long id) {
        // ...
    }
}
```

### 5.4 参数校验

**DTO 校验**：
```java
@Data
public class UserRegisterDTO {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度 3-50 个字符")
    private String username;

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度 6-20 个字符")
    private String password;
}
```

**Controller 校验**：
```java
@PostMapping("/register")
public Result<UserVO> register(@Valid @RequestBody UserRegisterDTO dto) {
    UserVO userVO = userService.register(dto);
    return Result.success(userVO);
}
```

---

## 6. 测试规范

### 6.1 单元测试

```java
@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    void testRegister_Success() {
        // Given
        UserRegisterDTO dto = new UserRegisterDTO();
        dto.setUsername("testuser");
        dto.setEmail("test@example.com");
        dto.setPassword("password123");

        // When
        UserVO result = userService.register(dto);

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void testRegister_DuplicateEmail() {
        // Given
        UserRegisterDTO dto = new UserRegisterDTO();
        dto.setEmail("existing@example.com");

        // When & Then
        assertThrows(BusinessException.class, () -> {
            userService.register(dto);
        });
    }
}
```

---

## 7. 部署配置

### 7.1 Dockerfile

```dockerfile
FROM openjdk:17-slim

WORKDIR /app

COPY target/plane-lite-backend-1.0.0.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 7.2 启动脚本

```bash
#!/bin/bash

# 构建
./mvnw clean package -DskipTests

# 运行
java -jar target/plane-lite-backend-1.0.0.jar \
  --spring.profiles.active=dev \
  --server.port=8080
```

---

## 8. 更新记录

| 版本 | 日期 | 变更说明 |
|------|------|----------|
| v1.0 | 2026-03-23 | 初始版本，完整的后端实现方案 |
