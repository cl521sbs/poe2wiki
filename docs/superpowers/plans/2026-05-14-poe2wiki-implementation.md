# PoE2Wiki 实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 构建流放之路2中文攻略网站 — 游戏数据库 + DPS计算器 + 推荐系统 + 攻略 + 社区

**Architecture:** Spring Boot 3.2 后端提供 REST API（统一 ApiResult 响应），Vue 3 + Element Plus 前端（暗黑主题），PostgreSQL 16 存储游戏数据，Redis 7 缓存和限流，Docker Compose 部署

**Tech Stack:** Spring Boot 3.2 + Java 21 + MyBatis-Plus 3.5 + Flyway + Vue 3 + TypeScript + Vite + Element Plus + PostgreSQL 16 + Redis 7 + Docker

**Execution Strategy:** 方案C — 数据抓取 → 数据MVP入库 → DPS计算器+推荐 → 补全数据库 → 攻略+社区

---

## Phase 0: 项目脚手架

### Task 0.1: 初始化 Spring Boot 后端项目

**Files:**
- Create: `backend/pom.xml`
- Create: `backend/src/main/java/com/poe2wiki/Poe2WikiApplication.java`
- Create: `backend/src/main/resources/application.yml`

- [ ] **Step 1: 创建 Maven pom.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.5</version>
    </parent>
    <groupId>com.poe2wiki</groupId>
    <artifactId>poe2wiki</artifactId>
    <version>0.1.0</version>
    <name>PoE2Wiki</name>

    <properties>
        <java.version>21</java.version>
        <mybatis-plus.version>3.5.7</mybatis-plus.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
            <version>${mybatis-plus.version}</version>
        </dependency>
        <dependency>
            <groupId>org.flywaydb</groupId>
            <artifactId>flyway-core</artifactId>
        </dependency>
        <dependency>
            <groupId>org.flywaydb</groupId>
            <artifactId>flyway-database-postgresql</artifactId>
        </dependency>
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-api</artifactId>
            <version>0.12.5</version>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-impl</artifactId>
            <version>0.12.5</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-jackson</artifactId>
            <version>0.12.5</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.jsoup</groupId>
            <artifactId>jsoup</artifactId>
            <version>1.17.2</version>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.security</groupId>
            <artifactId>spring-security-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

- [ ] **Step 2: 创建 Spring Boot 启动类**

```java
package com.poe2wiki;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Poe2WikiApplication {
    public static void main(String[] args) {
        SpringApplication.run(Poe2WikiApplication.class, args);
    }
}
```

- [ ] **Step 3: 创建 application.yml**

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/poe2wiki
    username: postgres
    password: postgres
    driver-class-name: org.postgresql.Driver
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
  data:
    redis:
      host: localhost
      port: 6379
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true

mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  global-config:
    db-config:
      id-type: auto
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0

jwt:
  secret: poe2wiki-jwt-secret-key-change-in-production-use-256bit-minimum
  access-token-expiration: 7200
  refresh-token-expiration: 604800

logging:
  level:
    com.poe2wiki: debug
```

- [ ] **Step 4: 验证项目启动**

```bash
cd /home/wangsen/projects/poe2wiki/backend && mvn compile
```
Expected: BUILD SUCCESS

- [ ] **Step 5: Commit**

```bash
cd /home/wangsen/projects/poe2wiki && git add backend/pom.xml backend/src/
git commit -m "feat: initialize Spring Boot 3.2 backend project with dependencies"
```

### Task 0.2: 初始化 Vue 3 前端项目

**Files:**
- Create: `frontend/` (via `npm create vite@latest`)

- [ ] **Step 1: 使用 Vite 创建 Vue 3 + TypeScript 项目**

```bash
cd /home/wangsen/projects/poe2wiki
npm create vite@latest frontend -- --template vue-ts
```

- [ ] **Step 2: 安装依赖**

```bash
cd /home/wangsen/projects/poe2wiki/frontend
npm install
npm install element-plus @element-plus/icons-vue vue-router@4 pinia axios
npm install -D @types/node sass
```

- [ ] **Step 3: 配置 Vite 代理和 Element Plus 自动导入**

修改 `frontend/vite.config.ts`:

```typescript
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src'),
    },
  },
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
})
```

修改 `frontend/src/main.ts`:

```typescript
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import App from './App.vue'
import router from './router'

const app = createApp(App)
app.use(createPinia())
app.use(router)
app.use(ElementPlus)

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.mount('#app')
```

- [ ] **Step 4: 验证前端启动**

```bash
cd /home/wangsen/projects/poe2wiki/frontend && npm run dev
```
Expected: Vite dev server running at http://localhost:3000

- [ ] **Step 5: Commit**

```bash
cd /home/wangsen/projects/poe2wiki
echo "node_modules/" > frontend/.gitignore
git add frontend/
git commit -m "feat: initialize Vue 3 + TypeScript frontend with Element Plus"
```

### Task 0.3: Docker Compose 环境

**Files:**
- Create: `docker/docker-compose.yml`
- Create: `docker/nginx.conf`
- Create: `docker/Dockerfile.backend`
- Create: `docker/Dockerfile.frontend`

- [ ] **Step 1: 创建 docker-compose.yml**

```yaml
version: '3.8'
services:
  postgres:
    image: postgres:16
    container_name: poe2wiki-postgres
    environment:
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
      POSTGRES_DB: poe2wiki
    ports:
      - "5432:5432"
    volumes:
      - pgdata:/var/lib/postgresql/data

  redis:
    image: redis:7-alpine
    container_name: poe2wiki-redis
    ports:
      - "6379:6379"

  backend:
    build:
      context: ..
      dockerfile: docker/Dockerfile.backend
    container_name: poe2wiki-backend
    ports:
      - "8080:8080"
    depends_on:
      - postgres
      - redis
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/poe2wiki
      SPRING_DATA_REDIS_HOST: redis

  frontend:
    build:
      context: ../frontend
      dockerfile: ../docker/Dockerfile.frontend
    container_name: poe2wiki-frontend
    ports:
      - "3000:3000"

  nginx:
    image: nginx:alpine
    container_name: poe2wiki-nginx
    ports:
      - "80:80"
    volumes:
      - ./nginx.conf:/etc/nginx/nginx.conf:ro
    depends_on:
      - backend
      - frontend

volumes:
  pgdata:
```

- [ ] **Step 2: 创建 nginx.conf**

```nginx
events { worker_connections 1024; }

http {
    upstream backend {
        server backend:8080;
    }

    upstream frontend {
        server frontend:3000;
    }

    server {
        listen 80;
        server_name localhost;

        location /api/ {
            proxy_pass http://backend;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
        }

        location / {
            proxy_pass http://frontend;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
        }
    }
}
```

- [ ] **Step 3: 创建 Dockerfiles**

`docker/Dockerfile.backend`:
```dockerfile
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY backend/pom.xml .
RUN mvn dependency:go-offline
COPY backend/src ./src
RUN mvn package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

`docker/Dockerfile.frontend`:
```dockerfile
FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 3000
CMD ["nginx", "-g", "daemon off;"]
```

- [ ] **Step 4: 验证 Docker Compose**

```bash
cd /home/wangsen/projects/poe2wiki/docker && docker compose config
```
Expected: Valid config output with no errors

- [ ] **Step 5: Commit**

```bash
cd /home/wangsen/projects/poe2wiki && git add docker/
git commit -m "feat: add Docker Compose environment with PostgreSQL, Redis, Nginx"
```

### Task 0.4: 数据库 Flyway 迁移脚本

**Files:**
- Create: `backend/src/main/resources/db/migration/V1__init_schema.sql`

- [ ] **Step 1: 编写完整建表 SQL**

```sql
-- V1__init_schema.sql
-- PoE2Wiki 数据库初始化

-- 用户表
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(20) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    nickname VARCHAR(16),
    avatar_url VARCHAR(500),
    bio VARCHAR(500),
    role VARCHAR(20) NOT NULL DEFAULT 'user',
    status VARCHAR(10) NOT NULL DEFAULT 'active',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 刷新令牌表
CREATE TABLE refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    token VARCHAR(500) NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 技能表
CREATE TABLE skills (
    id BIGSERIAL PRIMARY KEY,
    name_cn VARCHAR(200) NOT NULL,
    name_en VARCHAR(200) NOT NULL,
    type VARCHAR(20) NOT NULL,
    tags JSONB DEFAULT '[]',
    level INT NOT NULL DEFAULT 1,
    attr_requirements JSONB DEFAULT '{}',
    mana_cost INT DEFAULT 0,
    cooldown DECIMAL(10,3) DEFAULT 0,
    cast_time DECIMAL(10,3) DEFAULT 0,
    damage_multiplier DECIMAL(10,2) DEFAULT 100,
    damage_type VARCHAR(50) DEFAULT 'physical',
    effect_cn TEXT,
    effect_en TEXT,
    icon_url VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 装备表
CREATE TABLE equipment (
    id BIGSERIAL PRIMARY KEY,
    name_cn VARCHAR(200) NOT NULL,
    name_en VARCHAR(200) NOT NULL,
    category VARCHAR(30) NOT NULL,
    subcategory VARCHAR(30) NOT NULL,
    rarity VARCHAR(10) NOT NULL DEFAULT 'normal',
    level_required INT DEFAULT 1,
    attr_requirements JSONB DEFAULT '{}',
    implicit_mods JSONB DEFAULT '[]',
    explicit_mods JSONB DEFAULT '[]',
    damage_physical_min INT DEFAULT 0,
    damage_physical_max INT DEFAULT 0,
    damage_fire_min INT DEFAULT 0,
    damage_fire_max INT DEFAULT 0,
    damage_cold_min INT DEFAULT 0,
    damage_cold_max INT DEFAULT 0,
    damage_lightning_min INT DEFAULT 0,
    damage_lightning_max INT DEFAULT 0,
    damage_chaos_min INT DEFAULT 0,
    damage_chaos_max INT DEFAULT 0,
    attack_speed DECIMAL(6,2) DEFAULT 1.0,
    crit_chance DECIMAL(6,2) DEFAULT 5.0,
    crit_multiplier DECIMAL(6,2) DEFAULT 150.0,
    block_chance DECIMAL(5,2) DEFAULT 0,
    armour INT DEFAULT 0,
    evasion INT DEFAULT 0,
    energy_shield INT DEFAULT 0,
    flavor_text_cn TEXT,
    flavor_text_en TEXT,
    icon_url VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 词缀表
CREATE TABLE modifiers (
    id BIGSERIAL PRIMARY KEY,
    name_cn VARCHAR(200) NOT NULL,
    name_en VARCHAR(200) NOT NULL,
    type VARCHAR(10) NOT NULL,
    effect_cn TEXT,
    effect_en TEXT,
    min_value DECIMAL(10,2) DEFAULT 0,
    max_value DECIMAL(10,2) DEFAULT 0,
    applicable_slots JSONB DEFAULT '[]',
    tags JSONB DEFAULT '[]',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 天赋表
CREATE TABLE passives (
    id BIGSERIAL PRIMARY KEY,
    name_cn VARCHAR(200) NOT NULL,
    name_en VARCHAR(200) NOT NULL,
    type VARCHAR(20) NOT NULL,
    class_restriction VARCHAR(50),
    ascendancy_name VARCHAR(50),
    effect_cn TEXT,
    effect_en TEXT,
    tree_position VARCHAR(50),
    stat_bonuses JSONB DEFAULT '{}',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 怪物表
CREATE TABLE monsters (
    id BIGSERIAL PRIMARY KEY,
    name_cn VARCHAR(200) NOT NULL,
    name_en VARCHAR(200) NOT NULL,
    type VARCHAR(20) NOT NULL DEFAULT 'normal',
    location VARCHAR(200),
    life BIGINT DEFAULT 0,
    armour INT DEFAULT 0,
    evasion INT DEFAULT 0,
    energy_shield BIGINT DEFAULT 0,
    fire_res INT DEFAULT 0,
    cold_res INT DEFAULT 0,
    lightning_res INT DEFAULT 0,
    chaos_res INT DEFAULT 0,
    skills JSONB DEFAULT '[]',
    drops JSONB DEFAULT '[]',
    mechanics_cn TEXT,
    mechanics_en TEXT,
    icon_url VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 通货表
CREATE TABLE currency (
    id BIGSERIAL PRIMARY KEY,
    name_cn VARCHAR(200) NOT NULL,
    name_en VARCHAR(200) NOT NULL,
    type VARCHAR(30) NOT NULL,
    effect_cn TEXT,
    effect_en TEXT,
    stack_size INT DEFAULT 1,
    icon_url VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 攻略表
CREATE TABLE guides (
    id BIGSERIAL PRIMARY KEY,
    title_cn VARCHAR(500) NOT NULL,
    title_en VARCHAR(500),
    category VARCHAR(30) NOT NULL,
    class_restriction VARCHAR(50),
    content_cn TEXT,
    content_en TEXT,
    tags JSONB DEFAULT '[]',
    status VARCHAR(10) NOT NULL DEFAULT 'draft',
    author_id BIGINT REFERENCES users(id),
    view_count BIGINT DEFAULT 0,
    like_count BIGINT DEFAULT 0,
    favorite_count BIGINT DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 评论表
CREATE TABLE comments (
    id BIGSERIAL PRIMARY KEY,
    guide_id BIGINT NOT NULL REFERENCES guides(id),
    user_id BIGINT NOT NULL REFERENCES users(id),
    parent_id BIGINT,
    content TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 收藏表
CREATE TABLE favorites (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    guide_id BIGINT NOT NULL REFERENCES guides(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, guide_id)
);

-- Build推荐表
CREATE TABLE build_recommendations (
    id BIGSERIAL PRIMARY KEY,
    build_name VARCHAR(200) NOT NULL,
    class_name VARCHAR(50) NOT NULL,
    ascendancy VARCHAR(50),
    stage VARCHAR(10) NOT NULL,
    skill_ids JSONB DEFAULT '[]',
    equipment_ids JSONB DEFAULT '[]',
    passive_ids JSONB DEFAULT '[]',
    notes_cn TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 索引
CREATE INDEX idx_skills_type ON skills(type);
CREATE INDEX idx_skills_damage_type ON skills(damage_type);
CREATE INDEX idx_equipment_category ON equipment(category);
CREATE INDEX idx_equipment_rarity ON equipment(rarity);
CREATE INDEX idx_equipment_subcategory ON equipment(subcategory);
CREATE INDEX idx_passives_type ON passives(type);
CREATE INDEX idx_passives_class ON passives(class_restriction);
CREATE INDEX idx_monsters_type ON monsters(type);
CREATE INDEX idx_guides_category ON guides(category);
CREATE INDEX idx_guides_status ON guides(status);
CREATE INDEX idx_guides_author ON guides(author_id);
CREATE INDEX idx_comments_guide ON comments(guide_id);
CREATE INDEX idx_favorites_user ON favorites(user_id);
CREATE INDEX idx_build_recommendations_class ON build_recommendations(class_name);
CREATE INDEX idx_build_recommendations_stage ON build_recommendations(stage);
CREATE INDEX idx_refresh_tokens_user ON refresh_tokens(user_id);
```

- [ ] **Step 2: 验证迁移脚本语法**

```bash
cd /home/wangsen/projects/poe2wiki/backend
mvn flyway:migrate
```
Expected: 如果 PostgreSQL 已运行则迁移成功；否则先启动 docker compose 中的 postgres

- [ ] **Step 3: 验证表创建**

```bash
PGPASSWORD=postgres psql -h localhost -U postgres -d poe2wiki -c "\dt"
```
Expected: 列出 11 张表（含 flyway_schema_history）

- [ ] **Step 4: Commit**

```bash
cd /home/wangsen/projects/poe2wiki && git add backend/src/main/resources/db/
git commit -m "feat: add Flyway migration V1 with all 11 tables and indexes"
```

---

## Phase 1: 后端公共基础设施

### Task 1.1: 统一响应与分页类

**Files:**
- Create: `backend/src/main/java/com/poe2wiki/common/ApiResult.java`
- Create: `backend/src/main/java/com/poe2wiki/common/PageResult.java`
- Create: `backend/src/main/java/com/poe2wiki/exception/BusinessException.java`
- Create: `backend/src/main/java/com/poe2wiki/exception/GlobalExceptionHandler.java`
- Create: `backend/src/test/java/com/poe2wiki/common/ApiResultTest.java`

- [ ] **Step 1: 编写 ApiResult 测试**

```java
package com.poe2wiki.common;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ApiResultTest {

    @Test
    void success_shouldHaveCode200() {
        ApiResult<String> result = ApiResult.success("hello");
        assertThat(result.getCode()).isEqualTo(200);
        assertThat(result.getMessage()).isEqualTo("success");
        assertThat(result.getData()).isEqualTo("hello");
    }

    @Test
    void error_shouldHaveGivenCodeAndMessage() {
        ApiResult<Void> result = ApiResult.error(400, "参数错误");
        assertThat(result.getCode()).isEqualTo(400);
        assertThat(result.getMessage()).isEqualTo("参数错误");
        assertThat(result.getData()).isNull();
    }

    @Test
    void fail_shouldHaveCode500() {
        ApiResult<Void> result = ApiResult.fail("服务器错误");
        assertThat(result.getCode()).isEqualTo(500);
    }
}
```

- [ ] **Step 2: 运行测试验证失败**

```bash
cd /home/wangsen/projects/poe2wiki/backend && mvn test -Dtest=ApiResultTest
```
Expected: FAIL — ApiResult 类未定义

- [ ] **Step 3: 实现 ApiResult**

```java
package com.poe2wiki.common;

import lombok.Data;

@Data
public class ApiResult<T> {
    private int code;
    private String message;
    private T data;

    private ApiResult(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResult<T> success(T data) {
        return new ApiResult<>(200, "success", data);
    }

    public static <T> ApiResult<T> success() {
        return new ApiResult<>(200, "success", null);
    }

    public static <T> ApiResult<T> error(int code, String message) {
        return new ApiResult<>(code, message, null);
    }

    public static <T> ApiResult<T> fail(String message) {
        return new ApiResult<>(500, message, null);
    }
}
```

- [ ] **Step 4: 实现 PageResult**

```java
package com.poe2wiki.common;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;
import java.util.List;

@Data
public class PageResult<T> {
    private List<T> records;
    private long total;
    private long page;
    private long size;

    public static <T> PageResult<T> from(IPage<T> page) {
        PageResult<T> result = new PageResult<>();
        result.records = page.getRecords();
        result.total = page.getTotal();
        result.page = page.getCurrent();
        result.size = page.getSize();
        return result;
    }
}
```

- [ ] **Step 5: 实现 BusinessException 和 GlobalExceptionHandler**

```java
package com.poe2wiki.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final int code;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(String message) {
        this(400, message);
    }
}
```

```java
package com.poe2wiki.exception;

import com.poe2wiki.common.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResult<Void> handleBusiness(BusinessException e) {
        log.warn("Business exception: {}", e.getMessage());
        return ApiResult.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResult<Void> handleValidation(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return ApiResult.error(400, msg);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResult<Void> handleUnknown(Exception e) {
        log.error("Unexpected error", e);
        return ApiResult.fail("服务器内部错误");
    }
}
```

- [ ] **Step 6: 运行测试验证通过**

```bash
cd /home/wangsen/projects/poe2wiki/backend && mvn test -Dtest=ApiResultTest
```
Expected: PASS

- [ ] **Step 7: Commit**

```bash
cd /home/wangsen/projects/poe2wiki && git add backend/src/main/java/com/poe2wiki/common/ backend/src/main/java/com/poe2wiki/exception/ backend/src/test/
git commit -m "feat: add ApiResult, PageResult, BusinessException, GlobalExceptionHandler"
```

### Task 1.2: MyBatis-Plus 配置与分页插件

**Files:**
- Create: `backend/src/main/java/com/poe2wiki/config/MybatisPlusConfig.java`
- Create: `backend/src/main/java/com/poe2wiki/config/MetaHandler.java`

- [ ] **Step 1: 编写 MyBatis-Plus 配置**

```java
package com.poe2wiki.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MybatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.POSTGRE_SQL));
        return interceptor;
    }
}
```

- [ ] **Step 2: 编写 MetaHandler (自动填充 created_at/updated_at)**

```java
package com.poe2wiki.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MetaHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createdAt", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "updatedAt", LocalDateTime.class, LocalDateTime.now());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updatedAt", LocalDateTime.class, LocalDateTime.now());
    }
}
```

- [ ] **Step 3: 在 application.yml 中添加 Mapper 扫描配置**

在现有 `mybatis-plus:` 段后添加 `mapper-locations`（本项目中 Mapper 使用注解方式，无需 XML）

- [ ] **Step 4: 验证编译通过**

```bash
cd /home/wangsen/projects/poe2wiki/backend && mvn compile
```
Expected: BUILD SUCCESS

- [ ] **Step 5: Commit**

```bash
cd /home/wangsen/projects/poe2wiki && git add backend/src/main/java/com/poe2wiki/config/
git commit -m "feat: add MyBatis-Plus pagination plugin and MetaHandler"
```

### Task 1.3: Redis 配置与限流

**Files:**
- Create: `backend/src/main/java/com/poe2wiki/config/RedisConfig.java`
- Create: `backend/src/main/java/com/poe2wiki/common/RateLimiter.java`

- [ ] **Step 1: 编写 Redis 配置**

```java
package com.poe2wiki.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }
}
```

- [ ] **Step 2: 编写限流器（Redis Lua脚本）**

```java
package com.poe2wiki.common;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RateLimiter {

    private final StringRedisTemplate redisTemplate;

    private static final DefaultRedisScript<Long> RATE_LIMIT_SCRIPT = new DefaultRedisScript<>(
        "local count = redis.call('INCR', KEYS[1])\n" +
        "if count == 1 then\n" +
        "    redis.call('EXPIRE', KEYS[1], tonumber(ARGV[2]))\n" +
        "end\n" +
        "if count > tonumber(ARGV[1]) then\n" +
        "    return 0\n" +
        "end\n" +
        "return 1", Long.class
    );

    /**
     * @param key 限流 key（如 rate:login:username）
     * @param maxAttempts 最大尝试次数
     * @param windowSeconds 时间窗口（秒）
     * @return true=允许, false=触发限流
     */
    public boolean tryAcquire(String key, int maxAttempts, int windowSeconds) {
        Long result = redisTemplate.execute(
            RATE_LIMIT_SCRIPT,
            List.of(key),
            String.valueOf(maxAttempts),
            String.valueOf(windowSeconds)
        );
        return result != null && result == 1L;
    }
}
```

- [ ] **Step 3: Commit**

```bash
cd /home/wangsen/projects/poe2wiki && git add backend/src/main/java/com/poe2wiki/common/RateLimiter.java backend/src/main/java/com/poe2wiki/config/RedisConfig.java
git commit -m "feat: add Redis config and Lua-based rate limiter"
```

### Task 1.4: JWT 认证基础设施

**Files:**
- Create: `backend/src/main/java/com/poe2wiki/security/JwtUtils.java`
- Create: `backend/src/main/java/com/poe2wiki/security/JwtAuthFilter.java`
- Create: `backend/src/main/java/com/poe2wiki/security/SecurityConfig.java`
- Create: `backend/src/main/java/com/poe2wiki/security/UserDetailsServiceImpl.java`

- [ ] **Step 1: 编写 JwtUtils**

```java
package com.poe2wiki.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtils {

    private final SecretKey key;
    private final long accessExpiration;
    private final long refreshExpiration;

    public JwtUtils(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration}") long accessExpiration,
            @Value("${jwt.refresh-token-expiration}") long refreshExpiration) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessExpiration = accessExpiration;
        this.refreshExpiration = refreshExpiration;
    }

    public String generateAccessToken(Long userId, String username, String role) {
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("username", username)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessExpiration * 1000))
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(Long userId) {
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshExpiration * 1000))
                .signWith(key)
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Long getUserId(Claims claims) {
        return Long.parseLong(claims.getSubject());
    }

    public boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }
}
```

- [ ] **Step 2: 编写 JwtAuthFilter**

```java
package com.poe2wiki.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (!StringUtils.hasText(header) || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = header.substring(7);
            Claims claims = jwtUtils.parseToken(token);
            if (!jwtUtils.isTokenExpired(claims)) {
                String role = claims.get("role", String.class);
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        jwtUtils.getUserId(claims),
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + role))
                );
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (JwtException e) {
            // Token 无效，不设置认证
        }

        filterChain.doFilter(request, response);
    }
}
```

- [ ] **Step 3: 编写 SecurityConfig**

```java
package com.poe2wiki.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/register", "/api/auth/login", "/api/auth/refresh").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/game-data/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/guides", "/api/guides/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/recommendations", "/api/recommendations/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/calculator/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/search/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/files/**").permitAll()
                .requestMatchers("/api/admin/**").hasAnyRole("admin", "editor")
                .requestMatchers("/api/community/**").authenticated()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
```

- [ ] **Step 4: 验证编译**

```bash
cd /home/wangsen/projects/poe2wiki/backend && mvn compile
```
Expected: BUILD SUCCESS

- [ ] **Step 5: Commit**

```bash
cd /home/wangsen/projects/poe2wiki && git add backend/src/main/java/com/poe2wiki/security/
git commit -m "feat: add JWT authentication with access/refresh token support"
```

---

## Phase 2: 用户系统

### Task 2.1: 用户 Entity 与 Mapper

**Files:**
- Create: `backend/src/main/java/com/poe2wiki/entity/User.java`
- Create: `backend/src/main/java/com/poe2wiki/entity/RefreshToken.java`
- Create: `backend/src/main/java/com/poe2wiki/mapper/UserMapper.java`
- Create: `backend/src/main/java/com/poe2wiki/mapper/RefreshTokenMapper.java`

- [ ] **Step 1: 编写 User Entity**

```java
package com.poe2wiki.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("users")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String email;
    private String passwordHash;
    private String nickname;
    private String avatarUrl;
    private String bio;
    private String role;
    private String status;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
```

- [ ] **Step 2: 编写 RefreshToken Entity**

```java
package com.poe2wiki.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("refresh_tokens")
public class RefreshToken {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String token;
    private LocalDateTime expiresAt;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
```

- [ ] **Step 3: 编写 Mapper 接口**

```java
package com.poe2wiki.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.poe2wiki.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
```

```java
package com.poe2wiki.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.poe2wiki.entity.RefreshToken;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RefreshTokenMapper extends BaseMapper<RefreshToken> {
}
```

- [ ] **Step 4: Commit**

```bash
cd /home/wangsen/projects/poe2wiki && git add backend/src/main/java/com/poe2wiki/entity/ backend/src/main/java/com/poe2wiki/mapper/
git commit -m "feat: add User and RefreshToken entities with MyBatis-Plus mappers"
```

### Task 2.2: 注册 API

**Files:**
- Create: `backend/src/main/java/com/poe2wiki/dto/RegisterRequest.java`
- Create: `backend/src/main/java/com/poe2wiki/dto/LoginRequest.java`
- Create: `backend/src/main/java/com/poe2wiki/dto/LoginResponse.java`
- Create: `backend/src/main/java/com/poe2wiki/service/UserService.java`
- Create: `backend/src/main/java/com/poe2wiki/controller/AuthController.java`
- Create: `backend/src/test/java/com/poe2wiki/controller/AuthControllerTest.java`

- [ ] **Step 1: 编写 DTO**

```java
package com.poe2wiki.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "用户名不能为空")
    @Size(min = 4, max = 20, message = "用户名长度4-20位")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字和下划线")
    private String username;

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 50, message = "密码长度6-50位")
    private String password;
}
```

```java
package com.poe2wiki.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;
}
```

```java
package com.poe2wiki.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private long expiresIn;
    private UserProfile user;
}
```

- [ ] **Step 2: 编写 UserService（注册 + 登录 + 限流）**

```java
package com.poe2wiki.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.poe2wiki.common.RateLimiter;
import com.poe2wiki.dto.*;
import com.poe2wiki.entity.RefreshToken;
import com.poe2wiki.entity.User;
import com.poe2wiki.exception.BusinessException;
import com.poe2wiki.mapper.RefreshTokenMapper;
import com.poe2wiki.mapper.UserMapper;
import com.poe2wiki.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final RefreshTokenMapper refreshTokenMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final RateLimiter rateLimiter;

    @Transactional
    public LoginResponse register(RegisterRequest req) {
        // 限流检查
        if (!rateLimiter.tryAcquire("rate:register:" + req.getUsername(), 3, 3600)) {
            throw new BusinessException(429, "注册请求过于频繁，请1小时后再试");
        }

        // 检查用户名唯一性
        if (userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, req.getUsername())) > 0) {
            throw new BusinessException("用户名已被注册");
        }

        // 检查邮箱唯一性
        if (userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getEmail, req.getEmail())) > 0) {
            throw new BusinessException("邮箱已被注册");
        }

        // 创建用户
        User user = new User();
        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        user.setNickname(req.getUsername());
        user.setRole("user");
        user.setStatus("active");
        userMapper.insert(user);

        // 生成 Token
        return buildLoginResponse(user);
    }

    public LoginResponse login(LoginRequest req) {
        if (!rateLimiter.tryAcquire("rate:login:" + req.getUsername(), 5, 900)) {
            throw new BusinessException(429, "登录尝试过于频繁，请15分钟后再试");
        }

        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, req.getUsername()));

        if (user == null || !passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new BusinessException(401, "用户名或密码错误");
        }

        if ("banned".equals(user.getStatus())) {
            throw new BusinessException(403, "账号已被封禁");
        }

        return buildLoginResponse(user);
    }

    private LoginResponse buildLoginResponse(User user) {
        String accessToken = jwtUtils.generateAccessToken(user.getId(), user.getUsername(), user.getRole());
        String refreshToken = jwtUtils.generateRefreshToken(user.getId());

        // 保存 RefreshToken
        RefreshToken rt = new RefreshToken();
        rt.setUserId(user.getId());
        rt.setToken(refreshToken);
        rt.setExpiresAt(LocalDateTime.now().plusDays(7));
        refreshTokenMapper.insert(rt);

        UserProfile profile = new UserProfile();
        profile.setId(user.getId());
        profile.setUsername(user.getUsername());
        profile.setNickname(user.getNickname());
        profile.setRole(user.getRole());

        return new LoginResponse(accessToken, refreshToken, 7200, profile);
    }
}
```

- [ ] **Step 3: 编写 AuthController**

```java
package com.poe2wiki.controller;

import com.poe2wiki.common.ApiResult;
import com.poe2wiki.dto.LoginRequest;
import com.poe2wiki.dto.LoginResponse;
import com.poe2wiki.dto.RegisterRequest;
import com.poe2wiki.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ApiResult<LoginResponse> register(@Valid @RequestBody RegisterRequest req) {
        return ApiResult.success(userService.register(req));
    }

    @PostMapping("/login")
    public ApiResult<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        return ApiResult.success(userService.login(req));
    }
}
```

- [ ] **Step 4: 编写集成测试**

```java
package com.poe2wiki.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poe2wiki.dto.LoginRequest;
import com.poe2wiki.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void register_shouldReturnTokens() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("testuser");
        req.setEmail("test@example.com");
        req.setPassword("password123");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.data.refreshToken").isNotEmpty());
    }

    @Test
    void login_shouldReturnTokens() throws Exception {
        // 先注册
        RegisterRequest regReq = new RegisterRequest();
        regReq.setUsername("loginuser");
        regReq.setEmail("login@example.com");
        regReq.setPassword("password123");
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(regReq)));

        // 再登录
        LoginRequest loginReq = new LoginRequest();
        loginReq.setUsername("loginuser");
        loginReq.setPassword("password123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty());
    }

    @Test
    void register_duplicateUsername_shouldReturn400() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("dupuser");
        req.setEmail("dup1@example.com");
        req.setPassword("password123");
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)));

        RegisterRequest req2 = new RegisterRequest();
        req2.setUsername("dupuser");
        req2.setEmail("dup2@example.com");
        req2.setPassword("password123");
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req2)))
                .andExpect(jsonPath("$.code").value(400));
    }
}
```

- [ ] **Step 5: 运行测试**

```bash
cd /home/wangsen/projects/poe2wiki/backend && mvn test -Dtest=AuthControllerTest
```
Expected: PASS (需要 PostgreSQL 和 Redis 运行)

- [ ] **Step 6: Commit**

```bash
cd /home/wangsen/projects/poe2wiki && git add backend/src/main/java/com/poe2wiki/dto/ backend/src/main/java/com/poe2wiki/service/ backend/src/main/java/com/poe2wiki/controller/ backend/src/test/
git commit -m "feat: add register and login APIs with rate limiting"
```

### Task 2.3: Token 刷新、登出、个人信息

**Files:**
- Modify: `backend/src/main/java/com/poe2wiki/service/UserService.java`
- Modify: `backend/src/main/java/com/poe2wiki/controller/AuthController.java`

- [ ] **Step 1: 添加 refreshToken 方法到 UserService**

在 `UserService.java` 中添加：

```java
public LoginResponse refreshToken(String refreshTokenStr) {
    RefreshToken rt = refreshTokenMapper.selectOne(
        new LambdaQueryWrapper<RefreshToken>()
            .eq(RefreshToken::getToken, refreshTokenStr));

    if (rt == null || rt.getExpiresAt().isBefore(LocalDateTime.now())) {
        throw new BusinessException(401, "Refresh token 无效或已过期");
    }

    User user = userMapper.selectById(rt.getUserId());
    if (user == null || "banned".equals(user.getStatus())) {
        throw new BusinessException(401, "用户不存在或已被封禁");
    }

    // 删除旧 refresh token
    refreshTokenMapper.deleteById(rt.getId());

    return buildLoginResponse(user);
}

public void logout(Long userId, String refreshTokenStr) {
    refreshTokenMapper.delete(
        new LambdaQueryWrapper<RefreshToken>()
            .eq(RefreshToken::getUserId, userId)
            .eq(RefreshToken::getToken, refreshTokenStr));
}
```

- [ ] **Step 2: 添加控制器方法**

在 `AuthController.java` 中添加：

```java
@PostMapping("/refresh")
public ApiResult<LoginResponse> refresh(@RequestBody Map<String, String> body) {
    return ApiResult.success(userService.refreshToken(body.get("refreshToken")));
}

@PostMapping("/logout")
public ApiResult<Void> logout(@RequestAttribute("userId") Long userId,
                                @RequestBody Map<String, String> body) {
    userService.logout(userId, body.get("refreshToken"));
    return ApiResult.success();
}

@GetMapping("/profile")
public ApiResult<UserProfile> profile(@RequestAttribute("userId") Long userId) {
    return ApiResult.success(userService.getProfile(userId));
}
```

在 `UserService.java` 添加 `getProfile`:

```java
public UserProfile getProfile(Long userId) {
    User user = userMapper.selectById(userId);
    if (user == null) {
        throw new BusinessException(404, "用户不存在");
    }
    UserProfile profile = new UserProfile();
    profile.setId(user.getId());
    profile.setUsername(user.getUsername());
    profile.setNickname(user.getNickname());
    profile.setRole(user.getRole());
    return profile;
}
```

- [ ] **Step 3: Commit**

```bash
cd /home/wangsen/projects/poe2wiki && git add backend/src/main/java/com/poe2wiki/service/UserService.java backend/src/main/java/com/poe2wiki/controller/AuthController.java
git commit -m "feat: add token refresh, logout, and profile APIs"
```

---

## Phase 3: 游戏数据 API

### Task 3.1: 技能 (Skills) Entity + API

**Files:**
- Create: `backend/src/main/java/com/poe2wiki/entity/Skill.java`
- Create: `backend/src/main/java/com/poe2wiki/mapper/SkillMapper.java`
- Create: `backend/src/main/java/com/poe2wiki/service/SkillService.java`
- Create: `backend/src/main/java/com/poe2wiki/controller/GameDataController.java`

**由于后续所有游戏数据 API 结构类似，此处展示 Skill 完整实现，其余 Entity/Service 类推。**

- [ ] **Step 1: 创建 Skill Entity**

```java
package com.poe2wiki.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("skills")
public class Skill {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String nameCn;
    private String nameEn;
    private String type;
    private String tags;        // JSONB → JSON string
    private Integer level;
    private String attrRequirements;
    private Integer manaCost;
    private BigDecimal cooldown;
    private BigDecimal castTime;
    private BigDecimal damageMultiplier;
    private String damageType;
    private String effectCn;
    private String effectEn;
    private String iconUrl;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
```

- [ ] **Step 2: 创建 SkillMapper**

```java
package com.poe2wiki.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.poe2wiki.entity.Skill;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SkillMapper extends BaseMapper<Skill> {
}
```

- [ ] **Step 3: 创建 SkillService**

```java
package com.poe2wiki.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.poe2wiki.common.PageResult;
import com.poe2wiki.entity.Skill;
import com.poe2wiki.mapper.SkillMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillMapper skillMapper;

    public PageResult<Skill> list(int page, int size, String type, String damageType, String keyword) {
        LambdaQueryWrapper<Skill> qw = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(type)) {
            qw.eq(Skill::getType, type);
        }
        if (StringUtils.hasText(damageType)) {
            qw.eq(Skill::getDamageType, damageType);
        }
        if (StringUtils.hasText(keyword)) {
            qw.and(w -> w.like(Skill::getNameCn, keyword)
                         .or().like(Skill::getNameEn, keyword));
        }
        qw.orderByAsc(Skill::getId);
        return PageResult.from(skillMapper.selectPage(Page.of(page, size), qw));
    }

    public Skill getById(Long id) {
        return skillMapper.selectById(id);
    }
}
```

- [ ] **Step 4: 创建 GameDataController（技能部分）**

```java
package com.poe2wiki.controller;

import com.poe2wiki.common.ApiResult;
import com.poe2wiki.common.PageResult;
import com.poe2wiki.entity.Skill;
import com.poe2wiki.service.SkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/game-data")
@RequiredArgsConstructor
public class GameDataController {

    private final SkillService skillService;

    @GetMapping("/skills")
    public ApiResult<PageResult<Skill>> listSkills(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String damageType,
            @RequestParam(required = false) String keyword) {
        return ApiResult.success(skillService.list(page, size, type, damageType, keyword));
    }

    @GetMapping("/skills/{id}")
    public ApiResult<Skill> getSkill(@PathVariable Long id) {
        return ApiResult.success(skillService.getById(id));
    }
}
```

- [ ] **Step 5: Commit**

```bash
cd /home/wangsen/projects/poe2wiki && git add backend/src/main/java/com/poe2wiki/entity/Skill.java backend/src/main/java/com/poe2wiki/mapper/SkillMapper.java backend/src/main/java/com/poe2wiki/service/SkillService.java backend/src/main/java/com/poe2wiki/controller/GameDataController.java
git commit -m "feat: add Skill entity, service, and public API endpoints"
```

### Task 3.2: 装备、词缀、天赋、怪物、通货 Entity + API

**按照 Skill 的模式，批量创建剩余 5 个游戏数据实体的 Entity、Mapper、Service 和 Controller 方法。**

- [ ] **Step 1: 创建 Equipment Entity 和 Mapper**

`backend/src/main/java/com/poe2wiki/entity/Equipment.java` — 使用 `@TableName("equipment")`，字段对应 V1 建表 SQL
`backend/src/main/java/com/poe2wiki/mapper/EquipmentMapper.java` — 继承 `BaseMapper<Equipment>`

- [ ] **Step 2: 创建 Modifier Entity 和 Mapper**

`backend/src/main/java/com/poe2wiki/entity/Modifier.java`
`backend/src/main/java/com/poe2wiki/mapper/ModifierMapper.java`

- [ ] **Step 3: 创建 Passive Entity 和 Mapper**

`backend/src/main/java/com/poe2wiki/entity/Passive.java`
`backend/src/main/java/com/poe2wiki/mapper/PassiveMapper.java`

- [ ] **Step 4: 创建 Monster Entity 和 Mapper**

`backend/src/main/java/com/poe2wiki/entity/Monster.java`
`backend/src/main/java/com/poe2wiki/mapper/MonsterMapper.java`

- [ ] **Step 5: 创建 Currency Entity 和 Mapper**

`backend/src/main/java/com/poe2wiki/entity/Currency.java`
`backend/src/main/java/com/poe2wiki/mapper/CurrencyMapper.java`

- [ ] **Step 6: 创建对应的 Service 类**

```java
// EquipmentService, ModifierService, PassiveService, MonsterService, CurrencyService
// 结构同 SkillService，筛选参数使用各自实体的业务字段
```

- [ ] **Step 7: 在 GameDataController 添加所有端点**

```java
// GET /api/game-data/equipment?page=&size=&category=&rarity=&keyword=
// GET /api/game-data/equipment/{id}
// GET /api/game-data/modifiers?page=&size=&type=&keyword=
// GET /api/game-data/passives?page=&size=&type=&classRestriction=&keyword=
// GET /api/game-data/monsters?page=&size=&type=&keyword=
// GET /api/game-data/currency?page=&size=&type=&keyword=
```

- [ ] **Step 8: Commit**

```bash
cd /home/wangsen/projects/poe2wiki && git add backend/src/main/java/com/poe2wiki/entity/ backend/src/main/java/com/poe2wiki/mapper/ backend/src/main/java/com/poe2wiki/service/ backend/src/main/java/com/poe2wiki/controller/
git commit -m "feat: add Equipment, Modifier, Passive, Monster, Currency entities and APIs"
```

---

## Phase 4: 数据抓取脚本

### Task 4.1: Python 爬虫 — 技能与装备

**Files:**
- Create: `scripts/scrape_skills.py`
- Create: `scripts/scrape_equipment.py`
- Create: `scripts/requirements.txt`

- [ ] **Step 1: 创建 requirements.txt**

```
requests>=2.31.0
beautifulsoup4>=4.12.0
psycopg2-binary>=2.9.9
```

- [ ] **Step 2: 编写技能抓取脚本**

```python
#!/usr/bin/env python3
"""从 poe2db.tw 抓取技能数据并导入 PostgreSQL"""

import json
import re
import time
import psycopg2
import requests
from bs4 import BeautifulSoup

BASE_URL = "https://poe2db.tw"
DB_CONFIG = {
    "host": "localhost",
    "port": 5432,
    "user": "postgres",
    "password": "postgres",
    "dbname": "poe2wiki",
}


def fetch_page(url):
    """获取页面并解析为 BeautifulSoup"""
    resp = requests.get(url, timeout=30)
    resp.raise_for_status()
    time.sleep(0.5)  # 礼貌等待
    return BeautifulSoup(resp.text, "html.parser")


def parse_skill_from_detail(url, name_cn, name_en, skill_type):
    """从技能详情页解析属性"""
    soup = fetch_page(url)
    data = {
        "name_cn": name_cn,
        "name_en": name_en,
        "type": skill_type,
        "tags": [],
        "level": 1,
        "attr_requirements": {},
        "mana_cost": 0,
        "cooldown": 0,
        "cast_time": 0,
        "damage_multiplier": 100,
        "damage_type": "physical",
        "effect_cn": "",
        "effect_en": "",
        "icon_url": "",
    }

    # 解析详情表格（具体选择器需根据实际 HTML 调整）
    tables = soup.select("table")
    for table in tables:
        rows = table.select("tr")
        for row in rows:
            cells = row.select("td, th")
            if len(cells) >= 2:
                key = cells[0].get_text(strip=True)
                value = cells[1].get_text(strip=True)
                # 根据 key 映射到字段
                _map_skill_field(data, key, value)

    # 抓取技能效果描述
    desc_div = soup.select_one("div.description, div.skill-desc")
    if desc_div:
        data["effect_cn"] = desc_div.get_text(strip=True)

    return data


def _map_skill_field(data, key, value):
    """将网页表格的 key 映射到数据库字段"""
    mapping = {
        "魔力消耗": ("mana_cost", int),
        "冷却时间": ("cooldown", float),
        "施放时间": ("cast_time", float),
        "伤害倍率": ("damage_multiplier", float),
        "伤害类型": ("damage_type", str),
    }
    for keyword, (field, converter) in mapping.items():
        if keyword in key:
            try:
                nums = re.findall(r"[\d.]+", value)
                if nums:
                    data[field] = converter(nums[0])
            except (ValueError, IndexError):
                pass


def save_to_db(conn, skills):
    """批量插入技能数据"""
    with conn.cursor() as cur:
        for skill in skills:
            cur.execute(
                """INSERT INTO skills (name_cn, name_en, type, tags, level,
                   attr_requirements, mana_cost, cooldown, cast_time,
                   damage_multiplier, damage_type, effect_cn, effect_en, icon_url)
                   VALUES (%(name_cn)s, %(name_en)s, %(type)s, %(tags)s, %(level)s,
                   %(attr_requirements)s, %(mana_cost)s, %(cooldown)s, %(cast_time)s,
                   %(damage_multiplier)s, %(damage_type)s, %(effect_cn)s, %(effect_en)s, %(icon_url)s)""",
                {
                    **skill,
                    "tags": json.dumps(skill["tags"]),
                    "attr_requirements": json.dumps(skill["attr_requirements"]),
                },
            )
    conn.commit()


def main():
    print("开始抓取技能数据...")
    # 从技能列表页获取所有技能链接
    soup = fetch_page(f"{BASE_URL}/tw/Skill_gems")
    skill_links = []
    for link in soup.select("a[href]"):
        href = link.get("href", "")
        if "/tw/" in href and link.get_text(strip=True):
            skill_links.append((link.get_text(strip=True), BASE_URL + href))

    print(f"找到 {len(skill_links)} 个技能链接")

    conn = psycopg2.connect(**DB_CONFIG)
    skills = []

    for name_cn, url in skill_links[:10]:  # 先抓取前10个作为 MVP
        print(f"  抓取: {name_cn}")
        try:
            skill_data = parse_skill_from_detail(url, name_cn, name_cn, "active")
            skills.append(skill_data)
        except Exception as e:
            print(f"  错误: {e}")

    save_to_db(conn, skills)
    print(f"成功导入 {len(skills)} 条技能数据")
    conn.close()


if __name__ == "__main__":
    main()
```

- [ ] **Step 3: 编写装备抓取脚本（结构类似）**

`scripts/scrape_equipment.py` — 从 `https://poe2db.tw/tw/Unique_item` 抓取暗金装备数据，解析属性后入库。结构与技能脚本相同，字段映射到 equipment 表。

- [ ] **Step 4: 运行抓取脚本验证**

```bash
cd /home/wangsen/projects/poe2wiki/scripts
pip install -r requirements.txt
python scrape_skills.py
```
Expected: 成功插入技能数据到 PostgreSQL

- [ ] **Step 5: Commit**

```bash
cd /home/wangsen/projects/poe2wiki && git add scripts/
git commit -m "feat: add Python scrapers for skills and equipment from poe2db.tw"
```

---

## Phase 5: DPS 计算器

### Task 5.1: DPS 计算服务

**Files:**
- Create: `backend/src/main/java/com/poe2wiki/service/CalculatorService.java`
- Create: `backend/src/main/java/com/poe2wiki/dto/DpsRequest.java`
- Create: `backend/src/main/java/com/poe2wiki/dto/DpsResponse.java`
- Create: `backend/src/main/java/com/poe2wiki/controller/CalculatorController.java`
- Create: `backend/src/test/java/com/poe2wiki/service/CalculatorServiceTest.java`

- [ ] **Step 1: 编写 DpsRequest DTO**

```java
package com.poe2wiki.dto;

import lombok.Data;
import java.util.List;

@Data
public class DpsRequest {
    private List<SkillConfig> skills;
    private EquipmentConfig equipment;
    private PassiveConfig passives;
    private Long ascendancyId;
    private List<String> buffs;
    private TargetResistances target;

    @Data
    public static class SkillConfig {
        private Long skillId;
        private Integer level;
        private List<Long> supportGems;
    }

    @Data
    public static class EquipmentConfig {
        private WeaponConfig weapon;
        private WeaponConfig offhand;
        private List<RingConfig> rings;
        private ItemConfig amulet;
        private ItemConfig gloves;
        private ItemConfig helmet;
        private ItemConfig bodyArmour;
        private ItemConfig boots;
        private ItemConfig belt;
    }

    @Data
    public static class WeaponConfig {
        private Integer physMin, physMax;
        private Integer fireMin, fireMax;
        private Integer coldMin, coldMax;
        private Integer lightningMin, lightningMax;
        private Integer chaosMin, chaosMax;
        private Double attackSpeed, critChance, critMultiplier;
    }

    @Data
    public static class RingConfig {
        private Integer addedFireMin, addedFireMax;
        private Integer addedColdMin, addedColdMax;
        private Integer addedLightningMin, addedLightningMax;
        private Integer addedPhysicalMin, addedPhysicalMax;
    }

    @Data
    public static class ItemConfig {
        // 通用装备属性
    }

    @Data
    public static class PassiveConfig {
        private Double damageInc;
        private Double attackSpeedInc;
        private Double critChanceInc;
        private Double critMultiInc;
        private Double penetration;
    }

    @Data
    public static class TargetResistances {
        private Double fireRes, coldRes, lightningRes, chaosRes;
    }
}
```

- [ ] **Step 2: 编写 DpsResponse DTO**

```java
package com.poe2wiki.dto;

import lombok.Data;
import java.util.List;

@Data
public class DpsResponse {
    private Double avgDamage;
    private Double dps;
    private Double critDps;
    private Double effectiveDps;
    private DamageBreakdown breakdown;

    @Data
    public static class DamageBreakdown {
        private DamageValue baseDamage;
        private ModSources increasedMods;
        private ModSources moreMods;
        private SpeedInfo attackSpeed;
        private CritInfo crit;
        private Double hitChance;
        private ResistInfo enemyResistances;

        @Data
        public static class DamageValue {
            private Double physical, fire, cold, lightning, chaos, total;
        }

        @Data
        public static class ModSources {
            private Double total;
            private List<ModSource> sources;
        }

        @Data
        public static class ModSource {
            private String name;
            private Double value;
        }

        @Data
        public static class SpeedInfo {
            private Double base, effective;
        }

        @Data
        public static class CritInfo {
            private Double chance, multiplier;
        }

        @Data
        public static class ResistInfo {
            private Double fire, cold, lightning, chaos;
        }
    }
}
```

- [ ] **Step 3: 编写 CalculatorService 测试**

```java
package com.poe2wiki.service;

import com.poe2wiki.dto.DpsRequest;
import com.poe2wiki.dto.DpsResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

@SpringBootTest
class CalculatorServiceTest {

    @Autowired
    private CalculatorService calculatorService;

    @Test
    void calculate_shouldReturnPositiveDps() {
        DpsRequest req = buildSimpleRequest();
        DpsResponse result = calculatorService.calculate(req);
        assertThat(result.getDps()).isPositive();
        assertThat(result.getAvgDamage()).isPositive();
        assertThat(result.getBreakdown()).isNotNull();
    }

    @Test
    void calculate_shouldAccountForResistances() {
        DpsRequest lowRes = buildSimpleRequest();
        lowRes.getTarget().setLightningRes(0.0);

        DpsRequest highRes = buildSimpleRequest();
        highRes.getTarget().setLightningRes(75.0);

        DpsResponse r1 = calculatorService.calculate(lowRes);
        DpsResponse r2 = calculatorService.calculate(highRes);
        assertThat(r1.getEffectiveDps()).isGreaterThan(r2.getEffectiveDps());
    }

    private DpsRequest buildSimpleRequest() {
        DpsRequest req = new DpsRequest();

        DpsRequest.SkillConfig skill = new DpsRequest.SkillConfig();
        skill.setSkillId(1L);
        skill.setLevel(20);
        skill.setSupportGems(List.of());
        req.setSkills(List.of(skill));

        DpsRequest.EquipmentConfig equip = new DpsRequest.EquipmentConfig();
        DpsRequest.WeaponConfig weapon = new DpsRequest.WeaponConfig();
        weapon.setPhysMin(50);
        weapon.setPhysMax(100);
        weapon.setAttackSpeed(1.5);
        weapon.setCritChance(6.0);
        weapon.setCritMultiplier(150.0);
        equip.setWeapon(weapon);
        req.setEquipment(equip);

        DpsRequest.PassiveConfig passives = new DpsRequest.PassiveConfig();
        passives.setDamageInc(100.0);
        passives.setAttackSpeedInc(10.0);
        passives.setCritChanceInc(50.0);
        passives.setCritMultiInc(30.0);
        passives.setPenetration(10.0);
        req.setPassives(passives);

        DpsRequest.TargetResistances target = new DpsRequest.TargetResistances();
        target.setFireRes(30.0);
        target.setColdRes(30.0);
        target.setLightningRes(30.0);
        target.setChaosRes(10.0);
        req.setTarget(target);

        req.setBuffs(List.of());
        return req;
    }
}
```

- [ ] **Step 4: 运行测试验证失败**

```bash
cd /home/wangsen/projects/poe2wiki/backend && mvn test -Dtest=CalculatorServiceTest
```
Expected: FAIL — CalculatorService 未实现

- [ ] **Step 5: 实现 CalculatorService**

```java
package com.poe2wiki.service;

import com.poe2wiki.dto.DpsRequest;
import com.poe2wiki.dto.DpsResponse;
import com.poe2wiki.dto.DpsResponse.DamageBreakdown.*;
import com.poe2wiki.entity.Skill;
import com.poe2wiki.mapper.SkillMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CalculatorService {

    private final SkillMapper skillMapper;

    public DpsResponse calculate(DpsRequest req) {
        DpsRequest.SkillConfig skillConfig = req.getSkills().get(0);  // MVP: 单技能
        Skill skill = skillMapper.selectById(skillConfig.getSkillId());

        // 1. 基础伤害
        DpsRequest.WeaponConfig wp = req.getEquipment().getWeapon();
        double physBase = (wp.getPhysMin() + wp.getPhysMax()) / 2.0;
        double fireBase = nvl(wp.getFireMin()) + nvl(wp.getFireMax()) / 2.0;
        double coldBase = nvl(wp.getColdMin()) + nvl(wp.getColdMax()) / 2.0;
        double lightBase = nvl(wp.getLightningMin()) + nvl(wp.getLightningMax()) / 2.0;
        double chaosBase = nvl(wp.getChaosMin()) + nvl(wp.getChaosMax()) / 2.0;

        DamageValue baseDamage = new DamageValue();
        baseDamage.setPhysical(physBase);
        baseDamage.setFire(fireBase);
        baseDamage.setCold(coldBase);
        baseDamage.setLightning(lightBase);
        baseDamage.setChaos(chaosBase);
        baseDamage.setTotal(physBase + fireBase + coldBase + lightBase + chaosBase);

        // 2. 伤害倍率
        double skillMultiplier = skill != null ? skill.getDamageMultiplier().doubleValue() / 100.0 : 1.0;
        double baseAfterSkill = baseDamage.getTotal() * skillMultiplier;

        // 3. 增伤 (increased)
        double incMultiplier = 1.0 + nvl(req.getPassives().getDamageInc()) / 100.0;
        List<ModSource> incSources = new ArrayList<>();
        incSources.add(buildSource("天赋", nvl(req.getPassives().getDamageInc())));

        ModSources increasedMods = new ModSources();
        increasedMods.setTotal(incMultiplier);
        increasedMods.setSources(incSources);

        // 4. 更多伤害 (more) — MVP: 仅 Skill multiplier
        double moreMultiplier = skillMultiplier;
        List<ModSource> moreSources = new ArrayList<>();
        moreSources.add(buildSource("技能倍率", skillMultiplier * 100));

        ModSources moreMods = new ModSources();
        moreMods.setTotal(moreMultiplier);
        moreMods.setSources(moreSources);

        // 5. 总伤害
        double totalDamage = baseDamage.getTotal() * incMultiplier * moreMultiplier;

        // 6. 攻速
        double baseSpeed = nvl(wp.getAttackSpeed());
        double speedMultiplier = 1.0 + nvl(req.getPassives().getAttackSpeedInc()) / 100.0;
        double effectiveSpeed = baseSpeed * speedMultiplier;

        SpeedInfo speedInfo = new SpeedInfo();
        speedInfo.setBase(baseSpeed);
        speedInfo.setEffective(effectiveSpeed);

        // 7. 暴击
        double baseCrit = nvl(wp.getCritChance());
        double critChance = Math.min(baseCrit * (1.0 + nvl(req.getPassives().getCritChanceInc()) / 100.0), 100.0);
        double critMulti = (nvl(wp.getCritMultiplier()) + nvl(req.getPassives().getCritMultiInc())) / 100.0;

        CritInfo critInfo = new CritInfo();
        critInfo.setChance(critChance);
        critInfo.setMultiplier(critMulti);

        // 8. 命中率
        double hitChance = 95.0;  // MVP: 固定值

        // 9. 抗性计算
        double penetration = nvl(req.getPassives().getPenetration());
        double enemyLightRes = nvl(req.getTarget().getLightningRes()) - penetration;
        double resistMultiplier = 1.0 - Math.max(enemyLightRes, 0) / 100.0;

        ResistInfo resistInfo = new ResistInfo();
        resistInfo.setLightning(enemyLightRes);
        resistInfo.setFire(nvl(req.getTarget().getFireRes()));
        resistInfo.setCold(nvl(req.getTarget().getColdRes()));
        resistInfo.setChaos(nvl(req.getTarget().getChaosRes()));

        // 10. 汇总
        double avgDamage = totalDamage;
        double dps = avgDamage * effectiveSpeed;
        double critDps = avgDamage * effectiveSpeed
                * (critChance / 100.0 * critMulti + (1.0 - critChance / 100.0));
        double effectiveDps = critDps * (hitChance / 100.0) * resistMultiplier;

        DpsResponse result = new DpsResponse();
        result.setAvgDamage(Math.round(avgDamage * 100.0) / 100.0);
        result.setDps(Math.round(dps * 100.0) / 100.0);
        result.setCritDps(Math.round(critDps * 100.0) / 100.0);
        result.setEffectiveDps(Math.round(effectiveDps * 100.0) / 100.0);

        DamageBreakdown breakdown = new DamageBreakdown();
        breakdown.setBaseDamage(baseDamage);
        breakdown.setIncreasedMods(increasedMods);
        breakdown.setMoreMods(moreMods);
        breakdown.setAttackSpeed(speedInfo);
        breakdown.setCrit(critInfo);
        breakdown.setHitChance(hitChance);
        breakdown.setEnemyResistances(resistInfo);
        result.setBreakdown(breakdown);

        return result;
    }

    private double nvl(Double v) {
        return v == null ? 0.0 : v;
    }

    private ModSource buildSource(String name, Double value) {
        ModSource s = new ModSource();
        s.setName(name);
        s.setValue(value);
        return s;
    }
}
```

- [ ] **Step 6: 创建 CalculatorController**

```java
package com.poe2wiki.controller;

import com.poe2wiki.common.ApiResult;
import com.poe2wiki.dto.DpsRequest;
import com.poe2wiki.dto.DpsResponse;
import com.poe2wiki.service.CalculatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/calculator")
@RequiredArgsConstructor
public class CalculatorController {

    private final CalculatorService calculatorService;

    @PostMapping("/dps")
    public ApiResult<DpsResponse> calculateDps(@RequestBody DpsRequest req) {
        return ApiResult.success(calculatorService.calculate(req));
    }
}
```

- [ ] **Step 7: 运行测试验证通过**

```bash
cd /home/wangsen/projects/poe2wiki/backend && mvn test -Dtest=CalculatorServiceTest
```
Expected: PASS

- [ ] **Step 8: Commit**

```bash
cd /home/wangsen/projects/poe2wiki && git add backend/src/main/java/com/poe2wiki/dto/ backend/src/main/java/com/poe2wiki/service/CalculatorService.java backend/src/main/java/com/poe2wiki/controller/CalculatorController.java backend/src/test/
git commit -m "feat: add DPS calculator service with breakdown output"
```

### Task 5.2: Build 对比 API

**Files:**
- Modify: `backend/src/main/java/com/poe2wiki/controller/CalculatorController.java`
- Modify: `backend/src/main/java/com/poe2wiki/service/CalculatorService.java`

- [ ] **Step 1: 添加 compare 方法**

在 `CalculatorService.java` 添加：

```java
public Map<String, DpsResponse> compare(DpsRequest buildA, DpsRequest buildB) {
    Map<String, DpsResponse> result = new java.util.LinkedHashMap<>();
    result.put("buildA", calculate(buildA));
    result.put("buildB", calculate(buildB));
    return result;
}
```

在 `CalculatorController.java` 添加：

```java
@PostMapping("/compare")
public ApiResult<Map<String, DpsResponse>> compareDps(@RequestBody Map<String, DpsRequest> body) {
    return ApiResult.success(calculatorService.compare(
            body.get("buildA"), body.get("buildB")));
}
```

- [ ] **Step 2: Commit**

```bash
cd /home/wangsen/projects/poe2wiki && git add backend/src/main/java/com/poe2wiki/controller/CalculatorController.java backend/src/main/java/com/poe2wiki/service/CalculatorService.java
git commit -m "feat: add build comparison API endpoint"
```

---

## Phase 6: 推荐系统

### Task 6.1: 推荐系统 Entity + API

**Files:**
- Create: `backend/src/main/java/com/poe2wiki/entity/BuildRecommendation.java`
- Create: `backend/src/main/java/com/poe2wiki/mapper/BuildRecommendationMapper.java`
- Create: `backend/src/main/java/com/poe2wiki/service/RecommendationService.java`
- Create: `backend/src/main/java/com/poe2wiki/controller/RecommendationController.java`

- [ ] **Step 1: 创建 Entity 和 Mapper**

```java
// BuildRecommendation.java — @TableName("build_recommendations")
// 字段：buildName, className, ascendancy, stage, skillIds(JSON), equipmentIds(JSON),
//        passiveIds(JSON), notesCn, createdAt, updatedAt

// BuildRecommendationMapper.java — extends BaseMapper<BuildRecommendation>
```

- [ ] **Step 2: 创建 RecommendationService**

```java
package com.poe2wiki.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.poe2wiki.common.PageResult;
import com.poe2wiki.entity.BuildRecommendation;
import com.poe2wiki.mapper.BuildRecommendationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final BuildRecommendationMapper mapper;

    public PageResult<BuildRecommendation> list(String className, String stage, int page, int size) {
        LambdaQueryWrapper<BuildRecommendation> qw = new LambdaQueryWrapper<>();
        if (className != null) qw.eq(BuildRecommendation::getClassName, className);
        if (stage != null) qw.eq(BuildRecommendation::getStage, stage);
        qw.orderByAsc(BuildRecommendation::getStage, BuildRecommendation::getId);
        return PageResult.from(mapper.selectPage(Page.of(page, size), qw));
    }

    public BuildRecommendation getById(Long id) {
        return mapper.selectById(id);
    }
}
```

- [ ] **Step 3: 创建 RecommendationController**

```java
@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService service;

    @GetMapping
    public ApiResult<PageResult<BuildRecommendation>> list(
            @RequestParam(required = false) String className,
            @RequestParam(required = false) String stage,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ApiResult.success(service.list(className, stage, page, size));
    }

    @GetMapping("/{id}")
    public ApiResult<BuildRecommendation> getById(@PathVariable Long id) {
        return ApiResult.success(service.getById(id));
    }
}
```

- [ ] **Step 4: Commit**

```bash
cd /home/wangsen/projects/poe2wiki && git add backend/src/main/java/com/poe2wiki/entity/BuildRecommendation.java backend/src/main/java/com/poe2wiki/mapper/BuildRecommendationMapper.java backend/src/main/java/com/poe2wiki/service/RecommendationService.java backend/src/main/java/com/poe2wiki/controller/RecommendationController.java
git commit -m "feat: add build recommendation system with class/stage filtering"
```

---

## Phase 7: 管理后台 API

### Task 7.1: 主数据管理 CRUD

**Files:**
- Create: `backend/src/main/java/com/poe2wiki/controller/admin/AdminDataController.java`
- Create: `backend/src/main/java/com/poe2wiki/service/AdminDataService.java`

**SecurityConfig 已在 Phase 1 配置：`/api/admin/**` 需要 admin/editor 角色。**

- [ ] **Step 1: 创建 AdminDataController（技能 CRUD 示例）**

```java
package com.poe2wiki.controller.admin;

import com.poe2wiki.common.ApiResult;
import com.poe2wiki.entity.Skill;
import com.poe2wiki.service.AdminDataService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/data")
@RequiredArgsConstructor
public class AdminDataController {

    private final AdminDataService adminDataService;

    @PostMapping("/skills")
    public ApiResult<Skill> createSkill(@Valid @RequestBody Skill skill) {
        return ApiResult.success(adminDataService.createSkill(skill));
    }

    @PutMapping("/skills/{id}")
    public ApiResult<Skill> updateSkill(@PathVariable Long id, @RequestBody Skill skill) {
        skill.setId(id);
        return ApiResult.success(adminDataService.updateSkill(skill));
    }

    @DeleteMapping("/skills/{id}")
    public ApiResult<Void> deleteSkill(@PathVariable Long id) {
        adminDataService.deleteSkill(id);
        return ApiResult.success();
    }

    // Equipment / Modifier / Passive / Monster / Currency CRUD 同理
}
```

- [ ] **Step 2: 创建 AdminDataService**

```java
@Service
@RequiredArgsConstructor
public class AdminDataService {
    private final SkillMapper skillMapper;
    // ... 其他 Mapper

    public Skill createSkill(Skill skill) {
        skillMapper.insert(skill);
        return skill;
    }

    public Skill updateSkill(Skill skill) {
        skillMapper.updateById(skill);
        return skill;
    }

    public void deleteSkill(Long id) {
        skillMapper.deleteById(id);
    }
    // ... 其他实体同理
}
```

- [ ] **Step 3: Commit**

```bash
cd /home/wangsen/projects/poe2wiki && git add backend/src/main/java/com/poe2wiki/controller/admin/ backend/src/main/java/com/poe2wiki/service/AdminDataService.java
git commit -m "feat: add admin CRUD endpoints for game data management"
```

---

## Phase 8: 攻略与社区 API

### Task 8.1: 攻略 CRUD + 评论 + 收藏

**按 Phase 3/6 的模式创建 Guide、Comment、Favorite 的 Entity、Mapper、Service、Controller。**

- [ ] **Step 1: 创建 Guide Entity + Mapper + Service + Controller**

```
entity/Guide.java — 包含：titleCn, titleEn, category, classRestriction, contentCn, 
                      contentEn, tags(JSON), status, authorId, viewCount, likeCount, favoriteCount
mapper/GuideMapper.java
service/GuideService.java — list(分页+分类+关键词+状态筛选), getById, create, update, delete
controller/GuideController.java — GET /api/guides, GET /api/guides/{id},
     POST /api/guides, PUT /api/guides/{id}, DELETE /api/guides/{id}
```

- [ ] **Step 2: 创建 Comment Entity + Mapper + Service + Controller**

```
entity/Comment.java — guideId, userId, parentId, content
mapper/CommentMapper.java
service/CommentService.java — listByGuideId, create, delete
controller/CommentController.java — GET /api/community/comments?guideId={id},
     POST /api/community/comments, DELETE /api/community/comments/{id}
```

- [ ] **Step 3: 创建 Favorite Entity + Mapper + Service + Controller**

```
entity/Favorite.java — userId, guideId
mapper/FavoriteMapper.java
service/FavoriteService.java — add, remove, listByUser
controller/FavoriteController.java — POST /api/community/favorites/{guideId},
     DELETE /api/community/favorites/{guideId}, GET /api/community/my-favorites
```

- [ ] **Step 4: Commit**

```bash
cd /home/wangsen/projects/poe2wiki && git add backend/src/main/java/com/poe2wiki/entity/ backend/src/main/java/com/poe2wiki/mapper/ backend/src/main/java/com/poe2wiki/service/ backend/src/main/java/com/poe2wiki/controller/
git commit -m "feat: add guides, comments, and favorites CRUD APIs"
```

---

## Phase 9: 前端核心框架

### Task 9.1: 布局、路由、主题、Axios 封装

**Files:**
- Create: `frontend/src/router/index.ts`
- Create: `frontend/src/App.vue`
- Create: `frontend/src/views/layout/AppLayout.vue`
- Create: `frontend/src/styles/theme.scss`
- Create: `frontend/src/api/request.ts`
- Create: `frontend/src/api/modules/auth.ts`
- Create: `frontend/src/stores/auth.ts`

- [ ] **Step 1: 编写暗黑主题 CSS**

```scss
// frontend/src/styles/theme.scss
:root {
  --bg-primary: #1a1a2e;
  --bg-card: #16213e;
  --bg-hover: #1f2f4e;
  --border: #2a3a5e;
  --accent: #af6025;
  --accent-hover: #c97a3e;
  --text-primary: #e0e0e0;
  --text-secondary: #a0a0a0;
}

body {
  margin: 0;
  background-color: var(--bg-primary);
  color: var(--text-primary);
  font-family: 'Segoe UI', 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

// Element Plus 暗黑主题覆盖
.el-card {
  background-color: var(--bg-card) !important;
  border-color: var(--border) !important;
}

.el-button--primary {
  --el-button-bg-color: var(--accent);
  --el-button-border-color: var(--accent);
  --el-button-hover-bg-color: var(--accent-hover);
}
```

- [ ] **Step 2: 编写路由配置**

```typescript
// frontend/src/router/index.ts
import { createRouter, createWebHistory } from 'vue-router'
import AppLayout from '@/views/layout/AppLayout.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      component: AppLayout,
      children: [
        { path: '', name: 'Home', component: () => import('@/views/HomePage.vue') },
        { path: 'guides', name: 'Guides', component: () => import('@/views/guides/GuideList.vue') },
        { path: 'guides/:id', name: 'GuideDetail', component: () => import('@/views/guides/GuideDetail.vue') },
        { path: 'database', name: 'Database', component: () => import('@/views/database/DatabaseHome.vue') },
        { path: 'database/skills', name: 'Skills', component: () => import('@/views/database/SkillList.vue') },
        { path: 'database/equipment', name: 'Equipment', component: () => import('@/views/database/EquipmentList.vue') },
        { path: 'database/passives', name: 'Passives', component: () => import('@/views/database/PassiveList.vue') },
        { path: 'database/monsters', name: 'Monsters', component: () => import('@/views/database/MonsterList.vue') },
        { path: 'database/currency', name: 'Currency', component: () => import('@/views/database/CurrencyList.vue') },
        { path: 'tools/dps', name: 'DpsCalc', component: () => import('@/views/tools/DpsCalculator.vue') },
        { path: 'recommendations', name: 'Recommendations', component: () => import('@/views/Recommendations.vue') },
        { path: 'community', name: 'Community', component: () => import('@/views/Community.vue') },
        { path: 'profile', name: 'Profile', component: () => import('@/views/Profile.vue'), meta: { auth: true } },
      ],
    },
    { path: '/login', name: 'Login', component: () => import('@/views/LoginPage.vue') },
    { path: '/register', name: 'Register', component: () => import('@/views/RegisterPage.vue') },
  ],
})

router.beforeEach((to, _from, next) => {
  if (to.meta.auth) {
    const token = localStorage.getItem('accessToken')
    if (!token) return next('/login')
  }
  next()
})

export default router
```

- [ ] **Step 3: 编写 AppLayout 布局组件**

```vue
<!-- frontend/src/views/layout/AppLayout.vue -->
<template>
  <el-container class="app-layout">
    <el-header class="app-header">
      <div class="logo" @click="$router.push('/')">
        <img src="@/assets/logo.png" alt="PoE2Wiki" />
        <span>PoE2Wiki</span>
      </div>
      <el-menu mode="horizontal" :default-active="activeMenu" router>
        <el-menu-item index="/database">数据库</el-menu-item>
        <el-menu-item index="/tools/dps">DPS计算器</el-menu-item>
        <el-menu-item index="/recommendations">推荐Build</el-menu-item>
        <el-menu-item index="/guides">攻略</el-menu-item>
        <el-menu-item index="/community">社区</el-menu-item>
      </el-menu>
      <div class="user-area">
        <template v-if="authStore.isLoggedIn">
          <el-dropdown>
            <span>{{ authStore.user?.nickname }}</span>
            <template #dropdown>
              <el-dropdown-item @click="$router.push('/profile')">个人中心</el-dropdown-item>
              <el-dropdown-item @click="authStore.logout()">退出登录</el-dropdown-item>
            </template>
          </el-dropdown>
        </template>
        <template v-else>
          <el-button @click="$router.push('/login')">登录</el-button>
          <el-button type="primary" @click="$router.push('/register')">注册</el-button>
        </template>
      </div>
    </el-header>
    <el-main>
      <router-view />
    </el-main>
    <el-footer class="app-footer">
      PoE2Wiki © 2026 — 流放之路2中文攻略站
    </el-footer>
  </el-container>
</template>

<script setup lang="ts">
import { computed, watch } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
const route = useRoute()
const activeMenu = computed(() => '/' + route.path.split('/')[1])
</script>
```

- [ ] **Step 4: 编写 Axios 封装和 Auth API**

```typescript
// frontend/src/api/request.ts
import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

const request = axios.create({
  baseURL: '/api',
  timeout: 15000,
})

request.interceptors.request.use((config) => {
  const token = localStorage.getItem('accessToken')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

request.interceptors.response.use(
  (resp) => {
    const data = resp.data
    if (data.code !== 200) {
      ElMessage.error(data.message || '请求失败')
      return Promise.reject(new Error(data.message))
    }
    return data
  },
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('accessToken')
      router.push('/login')
    }
    ElMessage.error(error.message || '网络错误')
    return Promise.reject(error)
  }
)

export default request
```

```typescript
// frontend/src/api/modules/auth.ts
import request from '../request'

export interface LoginParams {
  username: string
  password: string
}

export interface RegisterParams {
  username: string
  email: string
  password: string
}

export const authApi = {
  login: (params: LoginParams) =>
    request.post('/auth/login', params),
  register: (params: RegisterParams) =>
    request.post('/auth/register', params),
  refreshToken: (refreshToken: string) =>
    request.post('/auth/refresh', { refreshToken }),
  getProfile: () =>
    request.get('/auth/profile'),
}
```

- [ ] **Step 5: 编写 Pinia Auth Store**

```typescript
// frontend/src/stores/auth.ts
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi, type LoginParams, type RegisterParams } from '@/api/modules/auth'
import router from '@/router'

interface UserProfile {
  id: number
  username: string
  nickname: string
  role: string
}

export const useAuthStore = defineStore('auth', () => {
  const user = ref<UserProfile | null>(null)
  const accessToken = ref(localStorage.getItem('accessToken') || '')
  const refreshToken = ref(localStorage.getItem('refreshToken') || '')

  const isLoggedIn = computed(() => !!accessToken.value)

  async function login(params: LoginParams) {
    const resp = await authApi.login(params)
    const data = resp.data as any
    accessToken.value = data.accessToken
    refreshToken.value = data.refreshToken
    user.value = data.user
    localStorage.setItem('accessToken', data.accessToken)
    localStorage.setItem('refreshToken', data.refreshToken)
    router.push('/')
  }

  async function register(params: RegisterParams) {
    const resp = await authApi.register(params)
    const data = resp.data as any
    accessToken.value = data.accessToken
    refreshToken.value = data.refreshToken
    user.value = data.user
    localStorage.setItem('accessToken', data.accessToken)
    localStorage.setItem('refreshToken', data.refreshToken)
    router.push('/')
  }

  function logout() {
    accessToken.value = ''
    refreshToken.value = ''
    user.value = null
    localStorage.removeItem('accessToken')
    localStorage.removeItem('refreshToken')
    router.push('/')
  }

  return { user, accessToken, refreshToken, isLoggedIn, login, register, logout }
})
```

- [ ] **Step 6: 验证前端运行**

```bash
cd /home/wangsen/projects/poe2wiki/frontend && npm run dev
```
Expected: 开发服务器启动，访问 localhost:3000 可看到暗黑主题布局

- [ ] **Step 7: Commit**

```bash
cd /home/wangsen/projects/poe2wiki && git add frontend/src/
git commit -m "feat: add frontend layout, dark theme, router, axios, auth store"
```

### Task 9.2: 登录/注册页面

**Files:**
- Create: `frontend/src/views/LoginPage.vue`
- Create: `frontend/src/views/RegisterPage.vue`

- [ ] **Step 1: 编写 LoginPage.vue**

```vue
<template>
  <div class="auth-page">
    <el-card class="auth-card">
      <h2>登录 PoE2Wiki</h2>
      <el-form :model="form" :rules="rules" ref="formRef" @submit.prevent="handleLogin">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名" prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" native-type="submit" :loading="loading" class="full-width">
            登录
          </el-button>
        </el-form-item>
      </el-form>
      <p class="switch-link">
        还没有账号？<router-link to="/register">立即注册</router-link>
      </p>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
const formRef = ref()
const loading = ref(false)

const form = reactive({
  username: '',
  password: '',
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

async function handleLogin() {
  const valid = await formRef.value?.validate()
  if (!valid) return
  loading.value = true
  try {
    await authStore.login(form)
  } catch {
    // 错误已在拦截器处理
  } finally {
    loading.value = false
  }
}
</script>
```

- [ ] **Step 2: 编写 RegisterPage.vue（增加邮箱字段）**

类似于 LoginPage，额外增加 `email` 字段和对应的验证规则（`{ type: 'email', message: '邮箱格式不正确' }`）

- [ ] **Step 3: Commit**

```bash
cd /home/wangsen/projects/poe2wiki && git add frontend/src/views/LoginPage.vue frontend/src/views/RegisterPage.vue
git commit -m "feat: add login and register pages with form validation"
```

---

## Phase 10: 前端游戏数据页面

### Task 10.1: 通用列表组件 + 技能/装备页面

**Files:**
- Create: `frontend/src/components/common/GameDataTable.vue`
- Create: `frontend/src/api/modules/gameData.ts`
- Create: `frontend/src/views/database/DatabaseHome.vue`
- Create: `frontend/src/views/database/SkillList.vue`

- [ ] **Step 1: 编写游戏数据 API 模块**

```typescript
// frontend/src/api/modules/gameData.ts
import request from '../request'

export const gameDataApi = {
  getSkills: (params: { page: number; size: number; type?: string; damageType?: string; keyword?: string }) =>
    request.get('/game-data/skills', { params }),
  getSkillDetail: (id: number) =>
    request.get(`/game-data/skills/${id}`),
  getEquipment: (params: { page: number; size: number; category?: string; rarity?: string; keyword?: string }) =>
    request.get('/game-data/equipment', { params }),
  getPassives: (params: { page: number; size: number; type?: string; classRestriction?: string; keyword?: string }) =>
    request.get('/game-data/passives', { params }),
  getMonsters: (params: { page: number; size: number; type?: string; keyword?: string }) =>
    request.get('/game-data/monsters', { params }),
  getCurrency: (params: { page: number; size: number; type?: string; keyword?: string }) =>
    request.get('/game-data/currency', { params }),
}
```

- [ ] **Step 2: 编写通用 GameDataTable 组件**

```vue
<!-- frontend/src/components/common/GameDataTable.vue -->
<template>
  <div class="game-data-table">
    <div class="toolbar">
      <el-input v-model="searchKeyword" placeholder="搜索..." clearable class="search-input"
        @keyup.enter="handleSearch" />
      <el-select v-if="filters" v-model="activeFilter" placeholder="筛选" clearable @change="handleSearch">
        <el-option v-for="f in filters" :key="f.value" :label="f.label" :value="f.value" />
      </el-select>
    </div>
    <el-table :data="data" v-loading="loading" stripe @row-click="handleRowClick">
      <slot />
    </el-table>
    <el-pagination v-if="total > 0"
      v-model:current-page="page"
      :page-size="size"
      :total="total"
      layout="prev, pager, next"
      @current-change="fetchData" />
  </div>
</template>

<script setup lang="ts" generic="T">
import { ref, onMounted } from 'vue'

const props = defineProps<{
  fetchFn: (params: any) => Promise<any>
  filters?: { label: string; value: string }[]
  filterKey?: string
}>()

const emit = defineEmits<{
  rowClick: [row: T]
}>()

const data = ref<T[]>([])
const loading = ref(false)
const page = ref(1)
const size = ref(20)
const total = ref(0)
const searchKeyword = ref('')
const activeFilter = ref('')

async function fetchData() {
  loading.value = true
  try {
    const params: any = { page: page.value, size: size.value }
    if (searchKeyword.value) params.keyword = searchKeyword.value
    if (activeFilter.value && props.filterKey) params[props.filterKey] = activeFilter.value
    const resp = await props.fetchFn(params)
    data.value = resp.data.records
    total.value = resp.data.total
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  page.value = 1
  fetchData()
}

function handleRowClick(row: T) {
  emit('rowClick', row)
}

onMounted(fetchData)
</script>
```

- [ ] **Step 3: 编写技能列表页面**

```vue
<!-- frontend/src/views/database/SkillList.vue -->
<template>
  <GameDataTable :fetchFn="fetchSkills" :filters="skillTypes" filterKey="type"
    @rowClick="(row) => $router.push(`/database/skills/${row.id}`)">
    <el-table-column prop="nameCn" label="名称" />
    <el-table-column prop="nameEn" label="英文名" />
    <el-table-column prop="type" label="类型">
      <template #default="{ row }">
        <el-tag :type="tagType(row.type)">{{ row.type }}</el-tag>
      </template>
    </el-table-column>
    <el-table-column prop="damageType" label="伤害类型" />
    <el-table-column prop="damageMultiplier" label="倍率%" />
  </GameDataTable>
</template>

<script setup lang="ts">
import { gameDataApi } from '@/api/modules/gameData'
import GameDataTable from '@/components/common/GameDataTable.vue'

const skillTypes = [
  { label: '主动技能', value: 'active' },
  { label: '辅助宝石', value: 'support' },
  { label: '光环', value: 'spirit' },
  { label: 'Meta', value: 'meta' },
]

function fetchSkills(params: any) { return gameDataApi.getSkills(params) }
function tagType(type: string) {
  const map: Record<string, string> = { active: 'danger', support: 'info', spirit: 'warning', meta: '' }
  return map[type] || ''
}
</script>
```

- [ ] **Step 4: 编写 DatabaseHome（数据库首页，分类卡片导航）**

使用 `el-card` 网格布局展示 5 个数据分类入口，点击跳转到对应列表页。

- [ ] **Step 5: Commit**

```bash
cd /home/wangsen/projects/poe2wiki && git add frontend/src/api/modules/gameData.ts frontend/src/components/common/GameDataTable.vue frontend/src/views/database/
git commit -m "feat: add game data table component and skill list page"
```

---

## Phase 11: DPS 计算器前端

### Task 11.1: DPS 计算器页面

**Files:**
- Create: `frontend/src/views/tools/DpsCalculator.vue`
- Create: `frontend/src/api/modules/calculator.ts`

- [ ] **Step 1: 编写 Calculator API 模块**

```typescript
// frontend/src/api/modules/calculator.ts
import request from '../request'

export const calculatorApi = {
  calculateDps: (config: any) =>
    request.post('/calculator/dps', config),
  compareDps: (buildA: any, buildB: any) =>
    request.post('/calculator/compare', { buildA, buildB }),
}
```

- [ ] **Step 2: 编写 DPS 计算器页面**

```vue
<template>
  <div class="dps-calculator">
    <h2>DPS 计算器</h2>
    <el-row :gutter="20">
      <!-- 技能配置 -->
      <el-col :span="12">
        <el-card header="技能配置">
          <el-form>
            <el-form-item label="主技能">
              <el-select v-model="config.skills[0].skillId" placeholder="选择技能" filterable>
                <el-option v-for="s in skills" :key="s.id" :label="s.nameCn" :value="s.id" />
              </el-select>
            </el-form-item>
            <el-form-item label="技能等级">
              <el-input-number v-model="config.skills[0].level" :min="1" :max="40" />
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>

      <!-- 装备区 -->
      <el-col :span="12">
        <el-card header="武器">
          <el-row :gutter="10">
            <el-col :span="12">
              <el-form-item label="物理伤害 Min">
                <el-input-number v-model="config.equipment.weapon.physMin" :min="0" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="物理伤害 Max">
                <el-input-number v-model="config.equipment.weapon.physMax" :min="0" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="攻速">
                <el-input-number v-model="config.equipment.weapon.attackSpeed" :min="0.1" :step="0.1" :precision="2" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="暴击率%">
                <el-input-number v-model="config.equipment.weapon.critChance" :min="0" :step="0.1" :precision="1" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="暴伤%">
                <el-input-number v-model="config.equipment.weapon.critMultiplier" :min="100" :precision="1" />
              </el-form-item>
            </el-col>
          </el-row>
        </el-card>
      </el-col>

      <!-- 天赋 & 升华 -->
      <el-col :span="12">
        <el-card header="天赋与升华">
          <el-form-item label="伤害加成%">
            <el-input-number v-model="config.passives.damageInc" :min="0" />
          </el-form-item>
          <el-form-item label="攻速加成%">
            <el-input-number v-model="config.passives.attackSpeedInc" :min="0" />
          </el-form-item>
          <el-form-item label="暴击率加成%">
            <el-input-number v-model="config.passives.critChanceInc" :min="0" />
          </el-form-item>
          <el-form-item label="暴伤加成%">
            <el-input-number v-model="config.passives.critMultiInc" :min="0" />
          </el-form-item>
          <el-form-item label="穿透%">
            <el-input-number v-model="config.passives.penetration" :min="0" />
          </el-form-item>
        </el-card>
      </el-col>

      <!-- 目标抗性 -->
      <el-col :span="12">
        <el-card header="目标抗性">
          <el-row :gutter="10">
            <el-col :span="6">
              <el-form-item label="火抗">
                <el-input-number v-model="config.target.fireRes" :min="-200" :max="200" />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="冰抗">
                <el-input-number v-model="config.target.coldRes" :min="-200" :max="200" />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="电抗">
                <el-input-number v-model="config.target.lightningRes" :min="-200" :max="200" />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="混沌抗">
                <el-input-number v-model="config.target.chaosRes" :min="-200" :max="200" />
              </el-form-item>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>

    <!-- 计算按钮 -->
    <div class="calc-actions">
      <el-button type="primary" size="large" @click="calculateDps" :loading="calculating">
        计算 DPS
      </el-button>
    </div>

    <!-- 结果展示 -->
    <el-card v-if="result" class="result-card">
      <el-row :gutter="20">
        <el-col :span="6">
          <div class="stat">
            <div class="stat-label">平均伤害</div>
            <div class="stat-value">{{ formatNumber(result.avgDamage) }}</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat">
            <div class="stat-label">DPS</div>
            <div class="stat-value accent">{{ formatNumber(result.dps) }}</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat">
            <div class="stat-label">暴击 DPS</div>
            <div class="stat-value">{{ formatNumber(result.critDps) }}</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat">
            <div class="stat-label">有效 DPS</div>
            <div class="stat-value accent">{{ formatNumber(result.effectiveDps) }}</div>
          </div>
        </el-col>
      </el-row>

      <!-- 伤害明细 -->
      <el-collapse class="breakdown">
        <el-collapse-item title="伤害明细" name="1">
          <p>基础伤害: {{ result.breakdown.baseDamage.total }}</p>
          <p>增伤倍率: {{ result.breakdown.increasedMods.total }}x</p>
          <p>更多倍率: {{ result.breakdown.moreMods.total }}x</p>
          <p>攻击速度: {{ result.breakdown.attackSpeed.effective }} APS</p>
          <p>暴击率: {{ result.breakdown.crit.chance }}% | 暴伤: {{ result.breakdown.crit.multiplier }}x</p>
          <p>命中率: {{ result.breakdown.hitChance }}%</p>
        </el-collapse-item>
      </el-collapse>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted } from 'vue'
import { calculatorApi } from '@/api/modules/calculator'
import { gameDataApi } from '@/api/modules/gameData'

const skills = ref<any[]>([])
const calculating = ref(false)
const result = ref<any>(null)

const config = reactive({
  skills: [{ skillId: null, level: 20, supportGems: [] as number[] }],
  equipment: {
    weapon: { physMin: 50, physMax: 100, fireMin: 0, fireMax: 0, coldMin: 0, coldMax: 0,
              lightningMin: 0, lightningMax: 0, chaosMin: 0, chaosMax: 0,
              attackSpeed: 1.5, critChance: 6.0, critMultiplier: 150.0 },
    offhand: null,
    rings: [] as any[],
    amulet: {}, gloves: {}, helmet: {}, bodyArmour: {}, boots: {}, belt: {},
  },
  passives: { damageInc: 100, attackSpeedInc: 10, critChanceInc: 50, critMultiInc: 30, penetration: 10 },
  ascendancyId: null as number | null,
  buffs: [] as string[],
  target: { fireRes: 30, coldRes: 30, lightningRes: 30, chaosRes: 10 },
})

async function calculateDps() {
  calculating.value = true
  try {
    const resp = await calculatorApi.calculateDps(config)
    result.value = resp.data
  } finally {
    calculating.value = false
  }
}

function formatNumber(n: number) {
  return n?.toLocaleString('en-US') ?? '0'
}

onMounted(async () => {
  const resp = await gameDataApi.getSkills({ page: 1, size: 500 })
  skills.value = resp.data.records
})
</script>

<style scoped>
.calc-actions { text-align: center; margin: 24px 0; }
.result-card { margin-top: 24px; }
.stat { text-align: center; padding: 16px; }
.stat-label { font-size: 12px; color: var(--text-secondary); }
.stat-value { font-size: 24px; font-weight: bold; }
.stat-value.accent { color: var(--accent); }
.breakdown { margin-top: 16px; }
.breakdown p { margin: 4px 0; color: var(--text-secondary); }
</style>
```

- [ ] **Step 2: 添加到路由**

已在 Task 9.1 路由中包含 `tools/dps` 路径。

- [ ] **Step 3: Commit**

```bash
cd /home/wangsen/projects/poe2wiki && git add frontend/src/views/tools/ frontend/src/api/modules/calculator.ts
git commit -m "feat: add DPS calculator page with full input form and result display"
```

---

## Phase 12: 首页 + 推荐 + 攻略页面

### Task 12.1: 首页（个性化 + 快捷入口）

**Files:**
- Create: `frontend/src/views/HomePage.vue`

- [ ] **Step 1: 编写 HomePage.vue**

使用 hero banner + 4 个快捷入口卡片（数据库、计算器、推荐、攻略）。如果用户已登录，在 localStorage 中存储最近查看的 class 偏好并在首页展示推荐 Build 预览。

### Task 12.2: 推荐与攻略页面

- [ ] **Step 2: 创建 Recommendations.vue — 按职业 tabs + 早/中/晚期筛选的推荐列表**

- [ ] **Step 3: 创建 GuideList.vue / GuideDetail.vue — 攻略列表和详情页**

- [ ] **Step 4: 创建 Community.vue / Profile.vue — 社区讨论和个人中心（框架）**

- [ ] **Step 5: Commit**

```bash
cd /home/wangsen/projects/poe2wiki && git add frontend/src/views/
git commit -m "feat: add homepage, recommendations, guides, community pages"
```

---

## Phase 13: Build 分享链接

### Task 13.1: 分享链接生成与解析

**Files:**
- Create: `backend/src/main/java/com/poe2wiki/entity/BuildShare.java`
- Create `backend/src/main/java/com/poe2wiki/service/ShareService.java`
- Create: `backend/src/main/java/com/poe2wiki/controller/ShareController.java`

- [ ] **Step 1: 创建分享表迁移 V2**

```sql
-- V2__build_share.sql
CREATE TABLE build_shares (
    id BIGSERIAL PRIMARY KEY,
    share_code VARCHAR(12) NOT NULL UNIQUE,
    dps_config JSONB NOT NULL,
    created_by BIGINT REFERENCES users(id),
    view_count BIGINT DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_build_shares_code ON build_shares(share_code);
```

- [ ] **Step 2: 实现 ShareService（生成短码 + Base64 存储配置）**

```java
// shareCode = 8位随机字母数字
// POST /api/share → 返回 shareCode
// GET /api/share/{shareCode} → 返回配置 JSON
```

- [ ] **Step 3: Commit**

```bash
cd /home/wangsen/projects/poe2wiki && git add backend/src/main/java/com/poe2wiki/entity/BuildShare.java backend/src/main/java/com/poe2wiki/service/ShareService.java backend/src/main/java/com/poe2wiki/controller/ShareController.java backend/src/main/resources/db/migration/V2__build_share.sql
git commit -m "feat: add build share link generation and retrieval"
```

---

## Phase 14: 交叉关联 + 个性化

### Task 14.1: 数据交叉关联 API

- [ ] **Step 1: 在 Skill 详情 API 中添加关联 Build 和装备查询**

修改 `GameDataController.getSkill` 返回增强后的 Skill 详情，包含：
- `relatedBuilds`: 引用该技能的 build_recommendations 记录
- `relatedEquipment`: 统计常用装备

- [ ] **Step 2: 在 Equipment 详情中添加关联推荐**

### Task 14.2: 个性化首页

- [ ] **Step 3: 用户偏好存储（localStorage class + stage 偏好）**

在 HomePage.vue 中根据 localStorage 中存储的用户 class 偏好，自动加载对应的推荐 Build。

---

## Phase 15: 搜索 + SEO + 部署

### Task 15.1: 全局搜索

**Files:**
- Create: `backend/src/main/java/com/poe2wiki/controller/SearchController.java`
- Create: `backend/src/main/java/com/poe2wiki/service/SearchService.java`

- [ ] **Step 1: 实现跨表搜索（UNION ALL 或 PostgreSQL Full Text Search）**

```java
// POST /api/search?q=keyword&type=all
// 在 skills/equipment/passives/monsters/currency/guides 的 name_cn/name_en 中 LIKE 搜索
```

### Task 15.2: SEO + meta 标签 + sitemap

- [ ] **Step 2: 在 index.html 中添加 SEO meta 标签**
- [ ] **Step 3: 生成 sitemap.xml**

### Task 15.3: Docker 部署验证

- [ ] **Step 4: 完整启动 docker compose，验证前后端 + 数据库 + Nginx 全部正常**

```bash
cd /home/wangsen/projects/poe2wiki/docker && docker compose up -d
# 验证: curl http://localhost/api/auth/login
# 验证: curl http://localhost/
```

---

## 任务汇总

| 阶段 | 任务数 | 内容 |
|------|--------|------|
| Phase 0 | 4 | 项目脚手架 (Spring Boot + Vue + Docker + DB) |
| Phase 1 | 4 | 公共基础设施 (ApiResult, MyBatis-Plus, Redis, JWT) |
| Phase 2 | 3 | 用户系统 (注册/登录/Token/个人信息) |
| Phase 3 | 2 | 游戏数据 API (6张表的公开查询) |
| Phase 4 | 1 | 数据抓取脚本 (Python爬虫) |
| Phase 5 | 2 | DPS计算器 + Build对比 |
| Phase 6 | 1 | 推荐系统 |
| Phase 7 | 1 | 管理后台 CRUD |
| Phase 8 | 1 | 攻略 + 评论 + 收藏 |
| Phase 9 | 2 | 前端核心 (布局/路由/主题/登录注册) |
| Phase 10 | 1 | 前端数据页面 |
| Phase 11 | 1 | DPS计算器前端 |
| Phase 12 | 1 | 首页 + 推荐 + 攻略页面 |
| Phase 13 | 1 | Build分享链接 |
| Phase 14 | 1 | 交叉关联 + 个性化 |
| Phase 15 | 1 | 搜索 + SEO + 部署 |
| **合计** | **27** | |

---

## 实施顺序建议

```
Phase 0 → Phase 1 → Phase 4 → Phase 2 → Phase 3 → Phase 5 → Phase 6 (后端MVP完成)
    ↓
Phase 9 → Phase 10 → Phase 11 (前端MVP完成)
    ↓
Phase 7 → Phase 8 → Phase 12 → Phase 13 → Phase 14 → Phase 15 (全功能完成)
```
