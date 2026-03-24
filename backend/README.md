# Plane Lite Backend

Plane Lite 后端服务 - 基于 Spring Boot 3.2 构建的轻量级项目管理系统后端。

## 技术栈

- **框架**: Spring Boot 3.2.0
- **ORM**: MyBatis-Plus 3.5.5
- **数据库**: MySQL 8.0
- **缓存**: Redis 7.x
- **认证**: JWT
- **API 文档**: SpringDoc OpenAPI 3.0

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 7.0+

### 启动步骤

1. **配置数据库**

```bash
# 创建数据库
mysql -u root -p
CREATE DATABASE plane_lite CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. **修改配置文件**

编辑 `src/main/resources/application-dev.yml`，修改数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/plane_lite...
    username: root
    password: your_password
```

3. **启动应用**

```bash
# 使用 Maven 启动
./mvnw spring-boot:run

# 或者先构建后运行
./mvnw clean package
java -jar target/plane-lite-backend.jar
```

4. **访问 API 文档**

启动成功后访问: http://localhost:8080/swagger-ui.html

## 项目结构

```
src/main/java/com/planelite/
├── PlaneApplication.java          # 启动类
├── config/                        # 配置类
│   ├── MybatisPlusConfig.java
│   └── WebMvcConfig.java
├── common/                        # 公共模块
│   ├── constant/                  # 常量
│   ├── exception/                 # 异常定义
│   ├── result/                    # 统一响应
│   └── handler/                   # 全局处理器
├── security/                      # 安全认证
├── util/                          # 工具类
└── module/                        # 业务模块
    ├── user/                      # 用户模块
    ├── workspace/                 # 工作区模块
    ├── project/                   # 项目模块
    ├── issue/                     # 问题模块
    └── comment/                   # 评论模块
```

## 开发规范

- **分层架构**: Controller → Service → Mapper
- **命名规范**: 使用驼峰命名，类名 PascalCase，方法名 camelCase
- **事务管理**: Service 层方法添加 @Transactional
- **异常处理**: 统一使用 BusinessException 及其子类
- **参数校验**: 使用 @Valid 和 javax.validation 注解

## API 接口规范

所有 API 返回统一格式：

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

## 构建和部署

```bash
# 构建
./mvnw clean package -DskipTests

# 运行
java -jar target/plane-lite-backend.jar --spring.profiles.active=prod

# Docker 构建
docker build -t plane-lite-backend .
docker run -p 8080:8080 plane-lite-backend
```

## License

MIT License
