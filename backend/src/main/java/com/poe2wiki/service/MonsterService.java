package com.poe2wiki.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.poe2wiki.common.PageResult;
import com.poe2wiki.entity.Monster;
import com.poe2wiki.mapper.MonsterMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class MonsterService {
    private final MonsterMapper mapper;

    public PageResult<Monster> list(int page, int size, String type, String keyword) {
        LambdaQueryWrapper<Monster> qw = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(type)) qw.eq(Monster::getType, type);
        if (StringUtils.hasText(keyword)) qw.and(w -> w.like(Monster::getNameCn, keyword).or().like(Monster::getNameEn, keyword));
        qw.orderByAsc(Monster::getId);
        return PageResult.from(mapper.selectPage(Page.of(page, size), qw));
    }

    public Monster getById(Long id) { return mapper.selectById(id); }
}
