package com.poe2wiki.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("guides")
public class Guide {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String titleCn;
    private String titleEn;
    private String category;
    private String classRestriction;
    private String contentCn;
    private String contentEn;
    private String tags;
    private String status;
    private Long authorId;
    private Long viewCount;
    private Long likeCount;
    private Long favoriteCount;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
