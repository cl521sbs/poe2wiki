package com.poe2wiki.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.poe2wiki.common.PageResult;
import com.poe2wiki.entity.Passive;
import com.poe2wiki.mapper.PassiveMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class PassiveService {
    private final PassiveMapper mapper;

    public PageResult<Passive> list(int page, int size, String type, String classRestriction, String keyword) {
        LambdaQueryWrapper<Passive> qw = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(type)) qw.eq(Passive::getType, type);
        if (StringUtils.hasText(classRestriction)) qw.eq(Passive::getClassRestriction, classRestriction);
        if (StringUtils.hasText(keyword)) qw.and(w -> w.like(Passive::getNameCn, keyword).or().like(Passive::getNameEn, keyword));
        qw.orderByAsc(Passive::getId);
        return PageResult.from(mapper.selectPage(Page.of(page, size), qw));
    }

    public Passive getById(Long id) { return mapper.selectById(id); }
}
