package com.poe2wiki.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.poe2wiki.common.PageResult;
import com.poe2wiki.entity.Guide;
import com.poe2wiki.mapper.GuideMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class GuideService {

    private final GuideMapper mapper;

    public PageResult<Guide> list(int page, int size, String category, String status, String keyword) {
        LambdaQueryWrapper<Guide> qw = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(category)) qw.eq(Guide::getCategory, category);
        if (StringUtils.hasText(status)) qw.eq(Guide::getStatus, status);
        else qw.eq(Guide::getStatus, "published");
        if (StringUtils.hasText(keyword)) qw.and(w -> w.like(Guide::getTitleCn, keyword).or().like(Guide::getTitleEn, keyword));
        qw.orderByDesc(Guide::getCreatedAt);
        return PageResult.from(mapper.selectPage(Page.of(page, size), qw));
    }

    public Guide getById(Long id) {
        Guide guide = mapper.selectById(id);
        if (guide != null) {
            guide.setViewCount(guide.getViewCount() == null ? 1 : guide.getViewCount() + 1);
            mapper.updateById(guide);
        }
        return guide;
    }

    public Guide create(Guide guide) {
        guide.setStatus("draft");
        guide.setViewCount(0L);
        guide.setLikeCount(0L);
        guide.setFavoriteCount(0L);
        mapper.insert(guide);
        return guide;
    }

    public Guide update(Guide guide) {
        mapper.updateById(guide);
        return mapper.selectById(guide.getId());
    }

    public void delete(Long id) {
        mapper.deleteById(id);
    }
}
