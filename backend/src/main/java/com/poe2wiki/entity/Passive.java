package com.poe2wiki.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("passives")
public class Passive {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String nameCn;
    private String nameEn;
    private String type;
    private String classRestriction;
    private String ascendancyName;
    private String effectCn;
    private String effectEn;
    private String treePosition;
    private String statBonuses;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
