# 部署方案

**文档版本**：v1.0
**创建日期**：2026-03-23
**状态**：已确认

---

## 1. 部署架构

```
                    ┌──────────────┐
                    │   用户浏览器  │
                    └──────┬───────┘
                           │ HTTPS
                           ▼
                    ┌──────────────┐
                    │    Nginx     │
                    │   Port: 80   │
                    └──────┬───────┘
                           │
           ┌───────────────┴───────────────┐
           │                               │
           ▼                               ▼
    ┌─────────────┐              ┌────────────────┐
    │  Frontend   │              │    Backend     │
    │  (静态文件)  │              │  Spring Boot   │
    │             │              │   Port: 8080   │
    └─────────────┘              └────────┬───────┘
                                          │
                        ┌─────────────────┴─────────────────┐
                        │                                   │
                        ▼                                   ▼
                 ┌─────────────┐                    ┌─────────────┐
                 │    MySQL    │                    │    Redis    │
                 │  Port: 3306 │                    │  Port: 6379 │
                 └─────────────┘                    └─────────────┘
```

---

## 2. Docker Compose 配置

### 2.1 docker-compose.yml

```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    container_name: plane-lite-mysql
    restart: always
    ports:
      - "3306:3306"
    environment:
      MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD}
      MYSQL_DATABASE: ${MYSQL_DATABASE}
      MYSQL_USER: ${MYSQL_USER}
      MYSQL_PASSWORD: ${MYSQL_PASSWORD}
      TZ: Asia/Shanghai
    volumes:
      - mysql-data:/var/lib/mysql
      - ./backend/src/main/resources/db/migration:/docker-entrypoint-initdb.d
    networks:
      - plane-network
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      interval: 10s
      timeout: 5s
      retries: 5

  redis:
    image: redis:7-alpine
    container_name: plane-lite-redis
    restart: always
    ports:
      - "6379:6379"
    volumes:
      - redis-data:/data
    networks:
      - plane-network
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 10s
      timeout: 5s
      retries: 5

  backend:
    build:
      context: ./backend
      dockerfile: Dockerfile
    container_name: plane-lite-backend
    restart: always
    ports:
      - "8080:8080"
    environment:
      SPRING_PROFILES_ACTIVE: prod
      MYSQL_HOST: mysql
      MYSQL_PORT: 3306
      MYSQL_DATABASE: ${MYSQL_DATABASE}
      MYSQL_USER: ${MYSQL_USER}
      MYSQL_PASSWORD: ${MYSQL_PASSWORD}
      REDIS_HOST: redis
      REDIS_PORT: 6379
      JWT_SECRET: ${JWT_SECRET}
      JWT_EXPIRATION: ${JWT_EXPIRATION}
    depends_on:
      mysql:
        condition: service_healthy
      redis:
        condition: service_healthy
    networks:
      - plane-network
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3

  frontend:
    build:
      context: ./frontend
      dockerfile: Dockerfile
    container_name: plane-lite-frontend
    restart: always
    ports:
      - "80:80"
    depends_on:
      - backend
    networks:
      - plane-network

volumes:
  mysql-data:
  redis-data:

networks:
  plane-network:
    driver: bridge
```

### 2.2 .env 文件

```bash
# MySQL
MYSQL_ROOT_PASSWORD=root_password_change_me
MYSQL_DATABASE=plane_lite
MYSQL_USER=plane_user
MYSQL_PASSWORD=plane_password_change_me

# JWT
JWT_SECRET=your-secret-key-at-least-32-characters-long-change-me
JWT_EXPIRATION=604800000

# 应用端口
BACKEND_PORT=8080
FRONTEND_PORT=80
```

---

## 3. Dockerfile

### 3.1 后端 Dockerfile

```dockerfile
# backend/Dockerfile

# 阶段 1：构建
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app

# 复制 pom.xml 并下载依赖（利用缓存）
COPY pom.xml .
RUN mvn dependency:go-offline

# 复制源代码
COPY src ./src

# 构建应用
RUN mvn clean package -DskipTests

# 阶段 2：运行
FROM eclipse-temurin:17-jre

WORKDIR /app

# 复制构建产物
COPY --from=builder /app/target/plane-lite-backend-*.jar app.jar

# 暴露端口
EXPOSE 8080

# 启动应用
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 3.2 前端 Dockerfile

```dockerfile
# frontend/Dockerfile

# 阶段 1：构建
FROM node:18-alpine AS builder

WORKDIR /app

# 复制 package.json 并安装依赖（利用缓存）
COPY package*.json ./
RUN npm ci

# 复制源代码
COPY . .

# 构建应用
RUN npm run build

# 阶段 2：运行
FROM nginx:alpine

# 复制构建产物
COPY --from=builder /app/dist /usr/share/nginx/html

# 复制 Nginx 配置
COPY nginx.conf /etc/nginx/conf.d/default.conf

# 暴露端口
EXPOSE 80

# 启动 Nginx
CMD ["nginx", "-g", "daemon off;"]
```

### 3.3 Nginx 配置

```nginx
# frontend/nginx.conf

server {
    listen 80;
    server_name localhost;

    root /usr/share/nginx/html;
    index index.html;

    # Gzip 压缩
    gzip on;
    gzip_types text/plain text/css application/json application/javascript text/xml application/xml application/xml+rss text/javascript;

    # 前端路由（SPA）
    location / {
        try_files $uri $uri/ /index.html;
    }

    # API 代理
    location /api/ {
        proxy_pass http://backend:8080/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # 缓存静态资源
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }
}
```

---

## 4. 部署步骤

### 4.1 本地开发环境

```bash
# 1. 启动数据库和 Redis
docker-compose up -d mysql redis

# 2. 等待服务就绪
docker-compose ps

# 3. 启动后端
cd backend
./mvnw spring-boot:run

# 4. 启动前端
cd frontend
npm run dev
```

### 4.2 生产环境部署

```bash
# 1. 克隆代码
git clone https://github.com/zonelili/plane-lite.git
cd plane-lite

# 2. 配置环境变量
cp .env.example .env
# 编辑 .env 文件，修改数据库密码和 JWT 密钥

# 3. 构建并启动所有服务
docker-compose up -d --build

# 4. 查看日志
docker-compose logs -f

# 5. 检查服务状态
docker-compose ps

# 6. 访问应用
# 前端：http://localhost
# 后端 API：http://localhost/api
# Swagger 文档：http://localhost/api/swagger-ui.html
```

### 4.3 数据库初始化

```sql
-- backend/src/main/resources/db/migration/V1__init.sql

-- 用户表
CREATE TABLE `user` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `username` VARCHAR(50) NOT NULL UNIQUE,
  `email` VARCHAR(100) NOT NULL UNIQUE,
  `password` VARCHAR(255) NOT NULL,
  `avatar` VARCHAR(255) DEFAULT NULL,
  `display_name` VARCHAR(100) DEFAULT NULL,
  `is_active` TINYINT(1) DEFAULT 1,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX `idx_email` (`email`),
  INDEX `idx_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 工作区表
CREATE TABLE `workspace` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  `slug` VARCHAR(50) NOT NULL UNIQUE,
  `description` TEXT DEFAULT NULL,
  `owner_id` BIGINT NOT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (`owner_id`) REFERENCES `user`(`id`) ON DELETE CASCADE,
  INDEX `idx_owner_id` (`owner_id`),
  INDEX `idx_slug` (`slug`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 项目表
CREATE TABLE `project` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `workspace_id` BIGINT NOT NULL,
  `name` VARCHAR(100) NOT NULL,
  `identifier` VARCHAR(10) NOT NULL,
  `description` TEXT DEFAULT NULL,
  `icon` VARCHAR(50) DEFAULT NULL,
  `cover_image` VARCHAR(255) DEFAULT NULL,
  `created_by` BIGINT NOT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (`workspace_id`) REFERENCES `workspace`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`created_by`) REFERENCES `user`(`id`) ON DELETE RESTRICT,
  UNIQUE KEY `uk_workspace_identifier` (`workspace_id`, `identifier`),
  INDEX `idx_workspace_id` (`workspace_id`),
  INDEX `idx_created_by` (`created_by`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 问题表
CREATE TABLE `issue` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `project_id` BIGINT NOT NULL,
  `sequence_id` INT NOT NULL,
  `title` VARCHAR(255) NOT NULL,
  `description` TEXT DEFAULT NULL,
  `priority` ENUM('none', 'low', 'medium', 'high', 'urgent') NOT NULL DEFAULT 'medium',
  `status` ENUM('todo', 'in_progress', 'done', 'closed') NOT NULL DEFAULT 'todo',
  `assignee_id` BIGINT DEFAULT NULL,
  `reporter_id` BIGINT NOT NULL,
  `start_date` DATE DEFAULT NULL,
  `due_date` DATE DEFAULT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (`project_id`) REFERENCES `project`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`assignee_id`) REFERENCES `user`(`id`) ON DELETE SET NULL,
  FOREIGN KEY (`reporter_id`) REFERENCES `user`(`id`) ON DELETE RESTRICT,
  UNIQUE KEY `uk_project_sequence` (`project_id`, `sequence_id`),
  INDEX `idx_project_id` (`project_id`),
  INDEX `idx_assignee_id` (`assignee_id`),
  INDEX `idx_reporter_id` (`reporter_id`),
  INDEX `idx_status` (`status`),
  INDEX `idx_priority` (`priority`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 问题评论表
CREATE TABLE `issue_comment` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `issue_id` BIGINT NOT NULL,
  `user_id` BIGINT NOT NULL,
  `content` TEXT NOT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (`issue_id`) REFERENCES `issue`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE,
  INDEX `idx_issue_id` (`issue_id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

---

## 5. 运维管理

### 5.1 常用命令

```bash
# 启动所有服务
docker-compose up -d

# 停止所有服务
docker-compose down

# 重启服务
docker-compose restart

# 查看日志
docker-compose logs -f [服务名]

# 进入容器
docker-compose exec [服务名] /bin/sh

# 查看服务状态
docker-compose ps

# 重新构建镜像
docker-compose build --no-cache

# 清理未使用的容器和镜像
docker system prune -a
```

### 5.2 备份和恢复

**数据库备份**：
```bash
# 备份
docker-compose exec mysql mysqldump -u root -p${MYSQL_ROOT_PASSWORD} plane_lite > backup.sql

# 恢复
docker-compose exec -T mysql mysql -u root -p${MYSQL_ROOT_PASSWORD} plane_lite < backup.sql
```

**数据卷备份**：
```bash
# 备份 MySQL 数据卷
docker run --rm \
  -v plane-lite_mysql-data:/data \
  -v $(pwd):/backup \
  alpine tar czf /backup/mysql-backup.tar.gz /data

# 恢复
docker run --rm \
  -v plane-lite_mysql-data:/data \
  -v $(pwd):/backup \
  alpine sh -c "cd /data && tar xzf /backup/mysql-backup.tar.gz --strip 1"
```

### 5.3 日志管理

```bash
# 查看最近 100 行日志
docker-compose logs --tail=100 backend

# 实时查看日志
docker-compose logs -f backend

# 保存日志到文件
docker-compose logs backend > backend.log
```

---

## 6. 监控和健康检查

### 6.1 健康检查端点

**后端**：
- Health: `http://localhost:8080/actuator/health`
- Metrics: `http://localhost:8080/actuator/metrics`

**前端**：
- `http://localhost/`

**数据库**：
```bash
docker-compose exec mysql mysqladmin -u root -p ping
```

**Redis**：
```bash
docker-compose exec redis redis-cli ping
```

### 6.2 性能监控

```bash
# 查看容器资源使用情况
docker stats

# 查看特定容器
docker stats plane-lite-backend plane-lite-mysql
```

---

## 7. 安全配置

### 7.1 生产环境检查清单

- [ ] 修改默认密码（MySQL、Redis）
- [ ] 使用强 JWT 密钥（至少 32 字符）
- [ ] 配置 HTTPS（使用 Let's Encrypt）
- [ ] 限制数据库端口访问（不对外暴露）
- [ ] 配置防火墙规则
- [ ] 启用 Redis 密码认证
- [ ] 定期备份数据
- [ ] 配置日志轮转
- [ ] 限制文件上传大小
- [ ] 配置 CORS 白名单

### 7.2 HTTPS 配置（Let's Encrypt）

```nginx
# 使用 Certbot 生成证书
server {
    listen 443 ssl http2;
    server_name your-domain.com;

    ssl_certificate /etc/letsencrypt/live/your-domain.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/your-domain.com/privkey.pem;

    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers HIGH:!aNULL:!MD5;

    # 其他配置...
}

# HTTP 重定向到 HTTPS
server {
    listen 80;
    server_name your-domain.com;
    return 301 https://$server_name$request_uri;
}
```

---

## 8. 故障排查

### 8.1 常见问题

**问题 1：后端无法连接数据库**
```bash
# 检查 MySQL 是否启动
docker-compose ps mysql

# 查看 MySQL 日志
docker-compose logs mysql

# 检查网络连接
docker-compose exec backend ping mysql
```

**问题 2：前端无法访问后端 API**
```bash
# 检查后端是否启动
docker-compose ps backend

# 检查 Nginx 配置
docker-compose exec frontend cat /etc/nginx/conf.d/default.conf

# 测试 API
curl http://localhost/api/v1/auth/me
```

**问题 3：容器内存不足**
```bash
# 增加 Docker 内存限制
# 编辑 docker-compose.yml
services:
  backend:
    deploy:
      resources:
        limits:
          memory: 2G
```

---

## 9. 更新和升级

### 9.1 应用更新

```bash
# 1. 拉取最新代码
git pull origin main

# 2. 停止服务
docker-compose down

# 3. 重新构建镜像
docker-compose build --no-cache

# 4. 启动服务
docker-compose up -d

# 5. 查看日志确认启动成功
docker-compose logs -f
```

### 9.2 数据库迁移

```bash
# 执行新的 SQL 脚本
docker-compose exec -T mysql mysql -u root -p${MYSQL_ROOT_PASSWORD} plane_lite < migration.sql
```

---

## 10. 更新记录

| 版本 | 日期 | 变更说明 |
|------|------|----------|
| v1.0 | 2026-03-23 | 初始版本，完整的部署方案 |
