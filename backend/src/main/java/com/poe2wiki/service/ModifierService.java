package com.poe2wiki.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.poe2wiki.common.PageResult;
import com.poe2wiki.entity.Modifier;
import com.poe2wiki.mapper.ModifierMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class ModifierService {
    private final ModifierMapper mapper;

    public PageResult<Modifier> list(int page, int size, String type, String keyword) {
        LambdaQueryWrapper<Modifier> qw = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(type)) qw.eq(Modifier::getType, type);
        if (StringUtils.hasText(keyword)) qw.and(w -> w.like(Modifier::getNameCn, keyword).or().like(Modifier::getNameEn, keyword));
        qw.orderByAsc(Modifier::getId);
        return PageResult.from(mapper.selectPage(Page.of(page, size), qw));
    }

    public Modifier getById(Long id) { return mapper.selectById(id); }
}
