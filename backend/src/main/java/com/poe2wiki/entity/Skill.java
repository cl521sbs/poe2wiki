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
    private String tags;
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
