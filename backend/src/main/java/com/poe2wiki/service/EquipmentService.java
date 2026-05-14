package com.poe2wiki.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.poe2wiki.common.PageResult;
import com.poe2wiki.entity.Equipment;
import com.poe2wiki.mapper.EquipmentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class EquipmentService {
    private final EquipmentMapper mapper;

    public PageResult<Equipment> list(int page, int size, String category, String rarity, String keyword) {
        LambdaQueryWrapper<Equipment> qw = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(category)) qw.eq(Equipment::getCategory, category);
        if (StringUtils.hasText(rarity)) qw.eq(Equipment::getRarity, rarity);
        if (StringUtils.hasText(keyword)) qw.and(w -> w.like(Equipment::getNameCn, keyword).or().like(Equipment::getNameEn, keyword));
        qw.orderByAsc(Equipment::getId);
        return PageResult.from(mapper.selectPage(Page.of(page, size), qw));
    }

    public Equipment getById(Long id) { return mapper.selectById(id); }
}
