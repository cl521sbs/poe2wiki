# PoE2Wiki 设计文档

## 项目概述

流放之路2 (Path of Exile 2) 中文攻略网站。提供图文攻略、游戏数据库查询、DPS计算器、社区功能。主数据使用中英文对照形式，方便中国玩家查阅国外攻略和 PoB 资源。

**技术栈：** Spring Boot 3 + Java 21 + Vue 3 + TypeScript + PostgreSQL + Redis + Docker

---

## 1. 整体架构

```
poe2wiki/
├── backend/               Spring Boot 3.2 + Java 21 + MyBatis-Plus + PostgreSQL 16 + Redis 7
│   ├── src/main/java/com/poe2wiki/
│   │   ├── entity/        数据库实体
│   │   ├── mapper/        MyBatis-Plus BaseMapper
│   │   ├── service/       业务逻辑
│   │   ├── controller/    REST API (统一返回 ApiResult<T>)
│   │   ├── dto/           请求/响应对象, PageResult<T>
│   │   ├── security/      JWT 认证 (HS256)
│   │   ├── config/        分页插件、MetaObjectHandler、Redis、WebMvc
│   │   ├── exception/     BusinessException + GlobalExceptionHandler
│   │   └── common/        ApiResult, 工具类
│   └── src/main/resources/
│       ├── application.yml
│       └── db/migration/  Flyway 迁移脚本
├── frontend/              Vue 3 + TypeScript + Vite + Element Plus
│   └── src/
│       ├── views/         页面
│       ├── components/    组件 (layout/, common/, game-data/)
│       ├── api/           Axios 封装, API 模块
│       ├── stores/        Pinia 状态管理
│       ├── router/        路由 + 权限守卫
│       └── composables/   组合式函数 (分页、数据获取)
├── docker/                docker-compose.yml, nginx.conf, Dockerfile
├── scripts/               数据同步/导入脚本
└── docs/                  设计文档
```

### 技术决策

| 决策 | 选择 | 理由 |
|------|------|------|
| ORM | MyBatis-Plus | Lambda 类型安全查询，无需手写 SQL |
| 认证 | Stateless JWT | Access Token 2h + Refresh Token 7d |
| 限流 | Redis Lua | INCR + EXPIRE 原子操作 |
| 迁移 | Flyway | 版本化管理数据库 schema |
| CSS | Element Plus | 成熟 Vue 3 组件库 |
| 主题 | 暗黑风格 | 底色 #1a1a2e，卡片 #16213e，强调色 #af6025 |

---

## 2. 数据库设计

### 2.1 用户与权限

**users**
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | 自增 |
| username | VARCHAR(20) UNIQUE | 4-20位，字母数字下划线 |
| email | VARCHAR(100) UNIQUE | 用于找回密码 |
| password_hash | VARCHAR(255) | BCrypt |
| nickname | VARCHAR(16) | 显示名称，可中文 |
| avatar_url | VARCHAR(500) | 头像 |
| bio | VARCHAR(500) | 个人简介 |
| role | VARCHAR(20) | user / editor / admin |
| status | VARCHAR(10) | active / banned |
| created_at | TIMESTAMP | |
| updated_at | TIMESTAMP | |

**refresh_tokens**
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | |
| user_id | BIGINT FK | |
| token | VARCHAR(500) UNIQUE | |
| expires_at | TIMESTAMP | |
| created_at | TIMESTAMP | |

### 2.2 游戏数据（中英对照）

所有文本字段同时存储中文和英文：`name_cn` / `name_en`、`description_cn` / `description_en`

**skills（技能宝石）**
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | |
| name_cn | VARCHAR(200) | 电弧 |
| name_en | VARCHAR(200) | Arc |
| type | VARCHAR(20) | active / support / spirit / meta |
| tags | JSONB | ["闪电","法术","连锁"] |
| level | INT | 技能等级 |
| attr_requirements | JSONB | {"str":0,"dex":0,"int":72} |
| mana_cost | INT | |
| cooldown | DECIMAL(10,3) | 冷却时间(秒) |
| cast_time | DECIMAL(10,3) | 施放时间(秒) |
| damage_multiplier | DECIMAL(10,2) | 伤害倍率% |
| damage_type | VARCHAR(50) | physical/fire/cold/lightning/chaos/mixed |
| effect_cn | TEXT | 技能效果中文描述 |
| effect_en | TEXT | 技能效果英文描述 |
| icon_url | VARCHAR(500) | |
| created_at | TIMESTAMP | |
| updated_at | TIMESTAMP | |

**equipment（装备）**
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | |
| name_cn | VARCHAR(200) | |
| name_en | VARCHAR(200) | |
| category | VARCHAR(30) | weapon/armor/accessory/offhand/flask |
| subcategory | VARCHAR(30) | sword/bow/ring/helmet... |
| rarity | VARCHAR(10) | normal/magic/rare/unique |
| level_required | INT | |
| attr_requirements | JSONB | |
| implicit_mods | JSONB | 基底词缀 |
| explicit_mods | JSONB | 随机词缀范围 |
| damage_physical_min | INT | |
| damage_physical_max | INT | |
| damage_fire_min | INT | |
| damage_fire_max | INT | |
| damage_cold_min | INT | |
| damage_cold_max | INT | |
| damage_lightning_min | INT | |
| damage_lightning_max | INT | |
| damage_chaos_min | INT | |
| damage_chaos_max | INT | |
| attack_speed | DECIMAL(6,2) | |
| crit_chance | DECIMAL(6,2) | |
| crit_multiplier | DECIMAL(6,2) | |
| block_chance | DECIMAL(5,2) | 盾 |
| armour | INT | |
| evasion | INT | |
| energy_shield | INT | |
| flavor_text_cn | TEXT | 暗金装备背景文字 |
| flavor_text_en | TEXT | |
| icon_url | VARCHAR(500) | |
| created_at | TIMESTAMP | |
| updated_at | TIMESTAMP | |

**modifiers（词缀）**
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | |
| name_cn | VARCHAR(200) | |
| name_en | VARCHAR(200) | |
| type | VARCHAR(10) | prefix / suffix / implicit / crafted |
| effect_cn | TEXT | 词缀效果中文 |
| effect_en | TEXT | 词缀效果英文 |
| min_value | DECIMAL(10,2) | |
| max_value | DECIMAL(10,2) | |
| applicable_slots | JSONB | ["ring","amulet"] 适用装备部位 |
| tags | JSONB | ["damage","fire"] |
| created_at | TIMESTAMP | |
| updated_at | TIMESTAMP | |

**passives（天赋）**
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | |
| name_cn | VARCHAR(200) | |
| name_en | VARCHAR(200) | |
| type | VARCHAR(20) | notable / keystone / ascendancy |
| class_restriction | VARCHAR(50) | 职业限制，空=通用 |
| ascendancy_name | VARCHAR(50) | 升华名称 |
| effect_cn | TEXT | |
| effect_en | TEXT | |
| tree_position | VARCHAR(50) | |
| stat_bonuses | JSONB | 数值加成结构化数据 |
| created_at | TIMESTAMP | |
| updated_at | TIMESTAMP | |

**monsters（怪物/BOSS）**
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | |
| name_cn | VARCHAR(200) | |
| name_en | VARCHAR(200) | |
| type | VARCHAR(20) | normal / magic / rare / unique / pinnacle |
| location | VARCHAR(200) | |
| life | BIGINT | |
| armour | INT | |
| evasion | INT | |
| energy_shield | BIGINT | |
| fire_res | INT | -200~200 |
| cold_res | INT | |
| lightning_res | INT | |
| chaos_res | INT | |
| skills | JSONB | 怪物技能列表 |
| drops | JSONB | 掉落表 |
| mechanics_cn | TEXT | 机制说明 |
| mechanics_en | TEXT | |
| icon_url | VARCHAR(500) | |
| created_at | TIMESTAMP | |
| updated_at | TIMESTAMP | |

**currency（通货/配方）**
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | |
| name_cn | VARCHAR(200) | |
| name_en | VARCHAR(200) | |
| type | VARCHAR(30) | currency/essence/catalyst/omen/splinter |
| effect_cn | TEXT | |
| effect_en | TEXT | |
| stack_size | INT | 最大堆叠 |
| icon_url | VARCHAR(500) | |
| created_at | TIMESTAMP | |
| updated_at | TIMESTAMP | |

### 2.3 内容与社区

**guides（攻略）**
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | |
| title_cn | VARCHAR(500) | |
| title_en | VARCHAR(500) | 可选 |
| category | VARCHAR(30) | build/boss/beginner/mechanics |
| class_restriction | VARCHAR(50) | 关联职业 |
| content_cn | TEXT | Markdown |
| content_en | TEXT | Markdown，可选 |
| tags | JSONB | ["开荒","endgame"] |
| status | VARCHAR(10) | draft/published/reviewed |
| author_id | BIGINT FK | |
| view_count | BIGINT | |
| like_count | BIGINT | |
| favorite_count | BIGINT | |
| created_at | TIMESTAMP | |
| updated_at | TIMESTAMP | |

**comments（评论）**
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | |
| guide_id | BIGINT FK | 关联攻略 |
| user_id | BIGINT FK | |
| parent_id | BIGINT | NULL=顶级评论, 非NULL=回复 |
| content | TEXT | Markdown |
| created_at | TIMESTAMP | |

**favorites（收藏）**
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | |
| user_id | BIGINT FK | |
| guide_id | BIGINT FK | |
| created_at | TIMESTAMP | |

### 2.4 推荐系统

**build_recommendations（推荐配置）**
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | |
| build_name | VARCHAR(200) | Build 名称 |
| class_name | VARCHAR(50) | 职业 |
| ascendancy | VARCHAR(50) | 升华 |
| stage | VARCHAR(10) | early/mid/late |
| skill_ids | JSONB | 推荐技能 ID 列表 |
| equipment_ids | JSONB | 推荐装备 ID 列表 |
| passive_ids | JSONB | 推荐天赋 ID 列表 |
| notes_cn | TEXT | 说明 |
| created_at | TIMESTAMP | |
| updated_at | TIMESTAMP | |

---

## 3. REST API 设计

统一返回格式：`{ code: 200, message: "success", data: {...} }`

### 3.1 认证 (/api/auth)

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| POST | /api/auth/register | 注册 | 公开 |
| POST | /api/auth/login | 登录 | 公开 |
| POST | /api/auth/refresh | 刷新Token | 公开 |
| POST | /api/auth/logout | 登出 | 需登录 |
| PUT | /api/auth/password | 修改密码 | 需登录 |
| GET | /api/auth/profile | 获取个人信息 | 需登录 |
| PUT | /api/auth/profile | 修改个人信息 | 需登录 |

### 3.2 游戏数据 (/api/game-data)

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | /api/game-data/skills | 技能列表(分页+筛选) | 公开 |
| GET | /api/game-data/skills/{id} | 技能详情 | 公开 |
| GET | /api/game-data/equipment | 装备列表 | 公开 |
| GET | /api/game-data/equipment/{id} | 装备详情 | 公开 |
| GET | /api/game-data/modifiers | 词缀列表 | 公开 |
| GET | /api/game-data/passives | 天赋列表 | 公开 |
| GET | /api/game-data/monsters | 怪物列表 | 公开 |
| GET | /api/game-data/currency | 通货列表 | 公开 |

### 3.3 主数据维护 (/api/admin/data)

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| POST | /api/admin/data/skills | 新增技能 | admin/editor |
| PUT | /api/admin/data/skills/{id} | 编辑技能 | admin/editor |
| DELETE | /api/admin/data/skills/{id} | 删除技能 | admin |
| POST | /api/admin/data/import | 批量导入(Excel) | admin/editor |
| ... | 装备/词缀/天赋/怪物/通货 同理 | | |

### 3.4 攻略 (/api/guides)

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | /api/guides | 攻略列表(分页+分类筛选) | 公开 |
| GET | /api/guides/{id} | 攻略详情 | 公开 |
| POST | /api/guides | 发布攻略 | admin/editor |
| PUT | /api/guides/{id} | 编辑攻略 | 作者/admin |
| DELETE | /api/guides/{id} | 删除攻略 | 作者/admin |

### 3.5 社区 (/api/community)

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | /api/community/comments?guideId={id} | 攻略评论列表 | 公开 |
| POST | /api/community/comments | 发表评论 | 需登录 |
| DELETE | /api/community/comments/{id} | 删除评论 | 作者/admin |
| POST | /api/community/favorites/{guideId} | 收藏攻略 | 需登录 |
| DELETE | /api/community/favorites/{guideId} | 取消收藏 | 需登录 |
| GET | /api/community/my-favorites | 我的收藏 | 需登录 |

### 3.6 DPS计算器 (/api/calculator)

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| POST | /api/calculator/dps | 计算DPS | 公开 |
| POST | /api/calculator/compare | 对比两套配置 | 公开 |

**DPS 计算输入结构：**
```json
{
  "skills": [{
    "skillId": 1,
    "level": 20,
    "supportGems": [2, 3, 4, 5, 6]
  }],
  "equipment": {
    "weapon": { "physMin": 50, "physMax": 100, "attackSpeed": 1.5, "critChance": 6.0 },
    "offhand": null,
    "rings": [{"addedFireMin": 10, "addedFireMax": 20}],
    "amulet": {},
    "gloves": {},
    "helmet": {},
    "bodyArmour": {},
    "boots": {},
    "belt": {}
  },
  "passives": {
    "damageInc": 150,
    "attackSpeedInc": 20,
    "critChanceInc": 100,
    "critMultiInc": 50,
    "penetration": 10
  },
  "ascendancyId": 3,
  "buffs": ["onslaught", "arcane_surge"],
  "target": {
    "fireRes": 30,
    "coldRes": 30,
    "lightningRes": 30,
    "chaosRes": 10
  }
}
```

**DPS 计算输出结构：**
```json
{
  "avgDamage": 15000,
  "dps": 45000,
  "critDps": 75000,
  "effectiveDps": 52000,
  "breakdown": {
    "baseDamage": { "physical": 300, "fire": 100, "total": 400 },
    "increasedMods": { "total": 250, "sources": [{"name": "天赋", "value": 150}] },
    "moreMods": { "total": 2.5, "sources": [{"name": "辅助宝石", "value": 1.4}] },
    "attackSpeed": { "base": 1.5, "effective": 3.0 },
    "crit": { "chance": 35, "multiplier": 4.5 },
    "hitChance": 95,
    "enemyResistances": { "lightning": -20 }
  }
}
```

### 3.7 推荐系统 (/api/recommendations)

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | /api/recommendations | 推荐列表(按职业+阶段筛选) | 公开 |
| GET | /api/recommendations/{id} | 推荐详情 | 公开 |
| POST | /api/admin/recommendations | 新增推荐 | admin/editor |
| PUT | /api/admin/recommendations/{id} | 编辑推荐 | admin/editor |

### 3.8 用户管理 (/api/admin/users)

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | /api/admin/users | 用户列表 | admin |
| PUT | /api/admin/users/{id}/role | 修改角色 | admin |
| PUT | /api/admin/users/{id}/status | 封禁/解封 | admin |

### 3.9 搜索 (/api/search)

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | /api/search?q=xxx&type=all | 全局搜索 | 公开 |

搜索范围：skills + equipment + passives + monsters + currency + guides

### 3.10 文件上传 (/api/files)

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| POST | /api/files/upload | 上传图片 | admin/editor |
| GET | /api/files/{filename} | 查看文件 | 公开 |

---

## 4. 前端设计

### 4.1 路由结构

```
/                       首页
/guides                 攻略列表
/guides/:id             攻略详情
/database               数据库首页
/database/skills        技能列表
/database/skills/:id    技能详情
/database/equipment     装备列表
/database/passives      天赋列表
/database/monsters      怪物列表
/database/currency      通货列表
/tools/dps               DPS计算器
/recommendations         推荐Build
/community              讨论区
/login                   登录
/register                注册
/profile                 个人中心
/admin                   管理后台
/admin/users             用户管理
/admin/data              主数据维护
/admin/guides-review     攻略审核
```

### 4.2 核心组件

- `AppLayout.vue` — 全局布局（顶栏+内容+底栏）
- `GameDataList.vue` — 游戏数据列表通用组件（分页+筛选+搜索）
- `GameDataDetail.vue` — 游戏数据详情通用组件（中英对照展示）
- `GuideCard.vue` — 攻略卡片
- `DpsCalculator.vue` — DPS计算器主组件
- `CommentSection.vue` — 评论区

### 4.3 暗黑主题

```css
/* 主题配色 */
--bg-primary: #1a1a2e;       /* 主背景 */
--bg-card: #16213e;          /* 卡片背景 */
--bg-hover: #1f2f4e;         /* 悬浮 */
--border: #2a3a5e;            /* 边框 */
--accent: #af6025;            /* 强调色/按钮 */
--accent-hover: #c97a3e;     /* 强调悬浮 */
--text-primary: #e0e0e0;     /* 主文字 */
--text-secondary: #a0a0a0;   /* 次要文字 */
```

### 4.4 DPS计算器页面布局

```
┌─────────────────────────────────────────┐
│  技能配置区                               │
│  [主技能 ▼] [等级:20]                    │
│  [辅助1 ▼] [辅助2 ▼] [辅助3 ▼] ...     │
├─────────────────────────────────────────┤
│  装备区                                   │
│  武器 | 副手 | 戒指 | 项链 | 护甲...    │
├─────────────────────────────────────────┤
│  天赋 & 升华                              │
│  伤害加成% [___] 攻速% [___]            │
│  升华 [锐眼 ▼]                           │
├─────────────────────────────────────────┤
│  Buff & 目标                              │
│  光环 [___] 目标抗性 [30,30,30,10]      │
├─────────────────────────────────────────┤
│  >>> 计算结果 <<<                         │
│  平均伤害: 15,000  DPS: 45,000          │
│  暴击DPS: 75,000  有效DPS: 52,000       │
│  [查看明细]                               │
└─────────────────────────────────────────┘
```

---

## 5. 安全设计

| 措施 | 实现 |
|------|------|
| XSS | 用户输入内容通过 Jsoup Safelist 清洗 |
| SQL注入 | MyBatis-Plus LambdaQueryWrapper 参数化 |
| CSRF | 无状态 JWT，不依赖 Cookie |
| 限流 | Redis Lua — 登录5次/15min锁定，注册3次/h |
| 密码 | BCrypt 加密存储 |
| CORS | 仅允许前端域名 |

---

## 6. 部署方案

```yaml
# docker-compose.yml
services:
  postgres:
    image: postgres:16
    volumes: [pgdata:/var/lib/postgresql/data]
  redis:
    image: redis:7
  backend:
    build: ./docker/Dockerfile.backend
    depends_on: [postgres, redis]
    ports: ["8080:8080"]
  frontend:
    build: ./docker/Dockerfile.frontend
    ports: ["3000:3000"]
  nginx:
    image: nginx:alpine
    ports: ["80:80"]
    volumes: [./docker/nginx.conf:/etc/nginx/nginx.conf]
```

---

## 7. 数据初始化方案

1. **手动录入**：编辑者在后台逐条添加核心数据
2. **Excel批量导入**：按模板整理数据 → `/api/admin/data/import` 导入
3. **poe2db.tw 数据同步**（可选后期）：写 Python 脚本爬取/解析 poe2db.tw 数据自动入库

---

## 8. 开发优先级

| 阶段 | 内容 | 预估 |
|------|------|------|
| Phase 1 | 项目脚手架 + 用户系统 + 菜单框架 | 基础 |
| Phase 2 | 5项游戏数据库（表+后台CRUD+前端展示） | 核心 |
| Phase 3 | DPS计算器（PoB集成+前端交互） | 核心 |
| Phase 4 | 攻略系统（发布/展示/审核） | 核心 |
| Phase 5 | 社区（评论/收藏/投稿） | 扩展 |
| Phase 6 | 推荐系统 + 搜索 + 性能优化 | 增强 |
| Phase 7 | SEO + 响应式 + 部署上线 | 收尾 |

---

## 9. 待定事项

- gstack 技能目前不可用，需后续安装配置
- PoB 引擎集成方案需验证 Lua 运行时在服务器上的可用性
- 数据初始化具体方式由 sen-wang 决定
