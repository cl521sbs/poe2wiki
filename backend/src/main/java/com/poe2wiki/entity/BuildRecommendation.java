package com.poe2wiki.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("build_recommendations")
public class BuildRecommendation {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String buildName;
    private String className;
    private String ascendancy;
    private String stage;
    private String skillIds;
    private String equipmentIds;
    private String passiveIds;
    private String notesCn;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
