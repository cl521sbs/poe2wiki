package com.poe2wiki.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("currency")
public class CurrencyEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String nameCn;
    private String nameEn;
    private String type;
    private String effectCn;
    private String effectEn;
    private Integer stackSize;
    private String iconUrl;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
