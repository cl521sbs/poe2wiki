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
        private Integer addedPhysicalMin, addedPhysicalMax;
        private Integer addedFireMin, addedFireMax;
        private Integer addedColdMin, addedColdMax;
        private Integer addedLightningMin, addedLightningMax;
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
