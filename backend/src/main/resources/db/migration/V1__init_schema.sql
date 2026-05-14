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
