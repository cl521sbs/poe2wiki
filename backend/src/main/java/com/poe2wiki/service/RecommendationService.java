package com.poe2wiki.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.poe2wiki.common.PageResult;
import com.poe2wiki.entity.BuildRecommendation;
import com.poe2wiki.mapper.BuildRecommendationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final BuildRecommendationMapper mapper;

    public PageResult<BuildRecommendation> list(String className, String stage, int page, int size) {
        LambdaQueryWrapper<BuildRecommendation> qw = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(className)) {
            qw.eq(BuildRecommendation::getClassName, className);
        }
        if (StringUtils.hasText(stage)) {
            qw.eq(BuildRecommendation::getStage, stage);
        }
        qw.orderByAsc(BuildRecommendation::getStage, BuildRecommendation::getId);
        return PageResult.from(mapper.selectPage(Page.of(page, size), qw));
    }

    public BuildRecommendation getById(Long id) {
        return mapper.selectById(id);
    }
}
