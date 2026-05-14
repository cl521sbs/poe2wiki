package com.poe2wiki.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("equipment")
public class Equipment {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String nameCn;
    private String nameEn;
    private String category;
    private String subcategory;
    private String rarity;
    private Integer levelRequired;
    private String attrRequirements;
    private String implicitMods;
    private String explicitMods;
    private Integer damagePhysicalMin;
    private Integer damagePhysicalMax;
    private Integer damageFireMin;
    private Integer damageFireMax;
    private Integer damageColdMin;
    private Integer damageColdMax;
    private Integer damageLightningMin;
    private Integer damageLightningMax;
    private Integer damageChaosMin;
    private Integer damageChaosMax;
    private BigDecimal attackSpeed;
    private BigDecimal critChance;
    private BigDecimal critMultiplier;
    private BigDecimal blockChance;
    private Integer armour;
    private Integer evasion;
    private Integer energyShield;
    private String flavorTextCn;
    private String flavorTextEn;
    private String iconUrl;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
