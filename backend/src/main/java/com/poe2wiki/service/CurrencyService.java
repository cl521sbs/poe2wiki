package com.poe2wiki.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.poe2wiki.common.PageResult;
import com.poe2wiki.entity.CurrencyEntity;
import com.poe2wiki.mapper.CurrencyMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class CurrencyService {
    private final CurrencyMapper mapper;

    public PageResult<CurrencyEntity> list(int page, int size, String type, String keyword) {
        LambdaQueryWrapper<CurrencyEntity> qw = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(type)) qw.eq(CurrencyEntity::getType, type);
        if (StringUtils.hasText(keyword)) qw.and(w -> w.like(CurrencyEntity::getNameCn, keyword).or().like(CurrencyEntity::getNameEn, keyword));
        qw.orderByAsc(CurrencyEntity::getId);
        return PageResult.from(mapper.selectPage(Page.of(page, size), qw));
    }

    public CurrencyEntity getById(Long id) { return mapper.selectById(id); }
}
