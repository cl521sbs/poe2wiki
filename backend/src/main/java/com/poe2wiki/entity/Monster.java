package com.poe2wiki.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("monsters")
public class Monster {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String nameCn;
    private String nameEn;
    private String type;
    private String location;
    private Long life;
    private Integer armour;
    private Integer evasion;
    private Long energyShield;
    private Integer fireRes;
    private Integer coldRes;
    private Integer lightningRes;
    private Integer chaosRes;
    private String skills;
    private String drops;
    private String mechanicsCn;
    private String mechanicsEn;
    private String iconUrl;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
