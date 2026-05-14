package com.poe2wiki.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("modifiers")
public class Modifier {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String nameCn;
    private String nameEn;
    private String type;
    private String effectCn;
    private String effectEn;
    private BigDecimal minValue;
    private BigDecimal maxValue;
    private String applicableSlots;
    private String tags;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
