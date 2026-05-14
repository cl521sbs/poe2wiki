-- PoE2Wiki 种子数据 (Seed Data)
-- 用于开发和测试游戏数据库 API

-- ==================== 技能 (Skills) ====================
INSERT INTO skills (name_cn, name_en, type, tags, level, attr_requirements, mana_cost, cooldown, cast_time, damage_multiplier, damage_type, effect_cn, effect_en, icon_url) VALUES
('熔岩之击', 'Molten Strike', 'active', '["attack", "fire", "aoe"]', 1, '{"str": 10, "dex": 5}', 6, 0, 0.8, 120, 'fire', '将武器注入熔岩之力，击中敌人时释放熔岩球', 'Infuses your weapon with molten energy, releasing magma balls on hit', '/icons/molten_strike.png'),
('冰霜射击', 'Ice Shot', 'active', '["attack", "cold", "projectile"]', 1, '{"dex": 12}', 8, 0, 1.0, 100, 'cold', '射出会冻结敌人的冰箭', 'Fires an arrow that freezes enemies', '/icons/ice_shot.png'),
('闪电箭', 'Lightning Arrow', 'active', '["attack", "lightning", "projectile"]', 1, '{"dex": 12}', 7, 0, 0.9, 130, 'lightning', '射出一支闪电箭，对附近敌人造成连锁伤害', 'Fires a lightning arrow that chains to nearby enemies', '/icons/lightning_arrow.png'),
('旋风斩', 'Cyclone', 'active', '["attack", "aoe", "movement", "melee"]', 28, '{"str": 20, "dex": 15}', 12, 0, 0, 150, 'physical', '开始旋转，对周围敌人造成伤害', 'Begin spinning, dealing damage to surrounding enemies', '/icons/cyclone.png'),
('烈焰爆破', 'Flameblast', 'active', '["spell", "fire", "aoe", "channeling"]', 28, '{"int": 25}', 10, 0, 0.3, 200, 'fire', '引导施放一个巨大的火焰爆炸', 'Channels to build up a large fiery explosion', '/icons/flameblast.png'),
('召唤骷髅', 'Summon Skeletons', 'active', '["spell", "minion", "duration"]', 1, '{"int": 8}', 5, 0, 1.0, 0, 'physical', '召唤骷髅战士为你而战', 'Summons skeleton warriors to fight for you', '/icons/summon_skeletons.png'),
('骨盾', 'Bone Shield', 'active', '["spell", "minion", "buff"]', 16, '{"int": 18}', 15, 5.0, 0.5, 0, 'physical', '为自己和周围召唤物提供防护骨盾', 'Grants a protective bone shield to you and nearby minions', '/icons/bone_shield.png'),
('闪打', 'Flicker Strike', 'active', '["attack", "movement", "melee"]', 20, '{"dex": 18, "str": 12}', 10, 0, 0, 80, 'physical', '瞬间传送到敌人身边并攻击', 'Teleports to a nearby enemy and strikes it', '/icons/flicker_strike.png'),
('正义之火', 'Righteous Fire', 'active', '["spell", "fire", "aoe"]', 16, '{"int": 18}', 0, 0, 0, 50, 'fire', '点燃自己，对周围敌人造成火焰伤害', 'Engulfs you in flames, dealing fire damage to nearby enemies', '/icons/righteous_fire.png'),
('快速攻击', 'Faster Attacks', 'support', '["support", "attack"]', 1, '{}', 0, 0, 0, 0, 'none', '辅助宝石：提升攻击速度', 'Supports attack skills, increasing attack speed', '/icons/faster_attacks.png');

-- ==================== 装备 (Equipment) ====================
INSERT INTO equipment (name_cn, name_en, category, subcategory, rarity, level_required, attr_requirements, armour, evasion, energy_shield, flavor_text_cn, flavor_text_en, icon_url) VALUES
('无悔之爱', 'The Coming Calamity', 'body_armour', 'body_armour', 'unique', 53, '{"str": 30, "int": 30}', 200, 80, 120, '爱如潮水，无可阻挡', 'Love like a flood, nothing can stop it', '/icons/coming_calamity.png'),
('空色之梦', 'Voidhome', 'weapon', 'one_hand_sword', 'unique', 45, '{"dex": 15, "int": 15}', 0, 0, 0, '在虚空中寻找答案', 'Seek answers in the void', '/icons/voidhome.png'),
('黄道之冠', 'Crown of the Zodiac', 'helmet', 'helmet', 'unique', 60, '{"str": 20, "dex": 20, "int": 20}', 150, 0, 80, '群星为你指引方向', 'The stars guide your path', '/icons/zodiac_crown.png'),
('风暴之盾', 'Storm Shield', 'shield', 'shield', 'unique', 40, '{"str": 18, "int": 12}', 180, 0, 70, '风暴之力汇聚于我', 'The storm''s power gathers within me', '/icons/storm_shield.png'),
('疾风之靴', 'Windfoot Boots', 'boots', 'boots', 'unique', 35, '{"dex": 18}', 0, 120, 40, '如风一般自由', 'Free as the wind', '/icons/windfoot.png');

-- ==================== 词缀 (Modifiers) ====================
INSERT INTO modifiers (name_cn, name_en, type, effect_cn, effect_en, min_value, max_value, applicable_slots, tags) VALUES
('火焰伤害', 'Fire Damage', 'prefix', '增加火焰伤害', 'Adds fire damage', 1, 10, '["weapon", "ring"]', '["fire", "damage"]'),
('冰冷抗性', 'Cold Resistance', 'suffix', '+冰冷抗性', '+cold resistance', 5, 45, '["helmet", "body_armour", "shield", "boots", "gloves"]', '["cold", "defense"]'),
('最大生命', 'Maximum Life', 'prefix', '+最大生命', '+maximum life', 10, 120, '["helmet", "body_armour", "shield", "boots", "gloves", "belt"]', '["life", "defense"]'),
('攻击速度', 'Attack Speed', 'suffix', '攻击速度增加', 'Increased attack speed', 5, 25, '["weapon", "gloves"]', '["attack", "speed"]'),
('暴击几率', 'Critical Strike Chance', 'suffix', '暴击几率增加', 'Increased critical strike chance', 10, 35, '["weapon"]', '["critical", "attack"]');

-- ==================== 天赋 (Passives) ====================
INSERT INTO passives (name_cn, name_en, type, class_restriction, effect_cn, effect_en) VALUES
('火焰之心', 'Heart of Flame', 'notable', NULL, '火焰伤害提升30%，火焰抗性穿透+5%', '30% increased fire damage, +5% fire penetration'),
('钢铁意志', 'Iron Will', 'keystone', NULL, '力量带来的近战伤害加成也作用于法术伤害', 'Strength''s damage bonus applies to spell damage as well'),
('混沌天赋', 'Chaos Inoculation', 'keystone', NULL, '最大生命变为1，免疫混沌伤害', 'Maximum life becomes 1, immune to chaos damage'),
('暗杀者之步', 'Assassin Step', 'notable', 'Shadow', '暴击几率提升40%，暴击伤害+20%', '40% increased crit chance, +20% crit multiplier'),
('防御大师', 'Defensive Mastery', 'mastery', NULL, '护甲、闪避、能量护盾各提升10%', '10% increased armour, evasion, and energy shield'),
('鲜血魔法', 'Blood Magic', 'keystone', NULL, '移除魔力，技能消耗生命', 'Removes mana, skills cost life instead');

-- ==================== 怪物 (Monsters) ====================
INSERT INTO monsters (name_cn, name_en, type, location, life, fire_res, cold_res, lightning_res, chaos_res, mechanics_cn, mechanics_en, icon_url) VALUES
('希拉克', 'Hillock', 'boss', '暮光海岸', 5000, 20, 0, 0, 0, '早期首领，使用重击和冲锋', 'Early boss, uses heavy slam and charge attacks', '/icons/hillock.png'),
('派蒂', 'Piety', 'boss', '月神殿', 12000, 30, 30, 50, 0, '操控雷电和尸体爆炸', 'Controls lightning and corpse explosions', '/icons/piety.png'),
('裂界守卫', 'Elder Guardian', 'boss', '异界地图', 50000, 30, 30, 30, 15, '使用虚空技能的异界boss', 'Elder boss using void skills', '/icons/elder_guardian.png'),
('复生骷髅', 'Risen Skeleton', 'normal', '墓地', 200, 0, 0, 0, 0, '基础近战怪物，攻击缓慢', 'Basic melee monster, slow attacks', '/icons/risen_skeleton.png'),
('混沌召唤者', 'Void Caller', 'magic', '虚空', 800, 0, 0, 0, 30, '四处游荡的施法者，释放混沌弹', 'Ranged caster that fires chaos projectiles', '/icons/void_caller.png'),
('火焰魔像', 'Flame Golem', 'rare', '火焰之殿', 3000, 75, 0, 0, 0, '耐火的强力近战怪物', 'Fire-resistant powerful melee monster', '/icons/flame_golem.png');

-- ==================== 通货 (Currency) ====================
INSERT INTO currency (name_cn, name_en, type, effect_cn, effect_en, stack_size, icon_url) VALUES
('混沌石', 'Chaos Orb', 'core', '重铸一件稀有物品为新的随机词缀', 'Reforges a rare item with new random modifiers', 20, '/icons/chaos_orb.png'),
('崇高石', 'Exalted Orb', 'core', '为一件稀有物品增加一条随机词缀', 'Adds a random modifier to a rare item', 10, '/icons/exalted_orb.png'),
('神圣石', 'Divine Orb', 'core', '重铸一件物品的词缀数值', 'Randomizes the numeric values of modifiers', 10, '/icons/divine_orb.png'),
('磨刀石', 'Blacksmith''s Whetstone', 'quality', '提升武器品质', 'Improves the quality of a weapon', 40, '/icons/whetstone.png'),
('护甲片', 'Armourer''s Scrap', 'quality', '提升护甲品质', 'Improves the quality of armour', 40, '/icons/armour_scrap.png'),
('改造石', 'Orb of Alteration', 'crafting', '重铸一件魔法物品为新的随机词缀', 'Reforges a magic item with new random modifiers', 20, '/icons/alteration_orb.png'),
('点金石', 'Orb of Alchemy', 'crafting', '将一件普通物品升级为稀有物品', 'Upgrades a normal item to a rare item', 10, '/icons/alchemy_orb.png'),
('制图钉', 'Cartographer''s Chisel', 'map', '提升地图品质', 'Improves the quality of a map', 20, '/icons/chisel.png');
